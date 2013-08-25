package com.guillaumecharmetant.vrc.commandinterpreter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommand;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommandResponseHandler;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpController;

public class CommandInterpreter {
	enum Command {
		REDO,
		PLAY_PAUSE,
		RESTART,
		STOP,
		PREVIOUS,
		NEXT,
		MUTE,
		RESET_VOLUME,
		INCREASE_VOLUME,
		DECREASE_VOLUME,
		TOGGLE_FULLSCREEN
	};
	
	public static final HashMap<String, LinkedHashMap<Pattern, Command>> LOCALES = new HashMap<String, LinkedHashMap<Pattern, Command>>();
	
	private static Pattern pattern(String pattern) {
		return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	}
	static {
		// french patterns initialization:
		LinkedHashMap<Pattern, Command> frPatterns = new LinkedHashMap<Pattern, Command>();
		
		//play
		frPatterns.put(pattern("^((commencer|reprend(s|re)?)( la)? )?lecture$"), Command.PLAY_PAUSE);
		//redo
		frPatterns.put(pattern("^encore( une fois)?$"), Command.REDO);
		//pause
		frPatterns.put(pattern("^((suspend(s|re)?)|(met(s|tre)? en )?pause)$"), Command.PLAY_PAUSE);
		//stop
		frPatterns.put(pattern("^(stop|arrêter?)$"), Command.STOP);
		//previous
		frPatterns.put(pattern("^précédente?$"), Command.PREVIOUS);
		//next
		frPatterns.put(pattern("^suivante?$"), Command.NEXT);
		//restart
		frPatterns.put(pattern("^recommencer?( la lecture)?$"), Command.RESTART);
		//mute
		frPatterns.put(pattern("couper?( le)? son"), Command.MUTE);
		//reset volume
		frPatterns.put(pattern("(re)?met(s|tre)? le son"), Command.RESET_VOLUME);
		//increase volume
		frPatterns.put(pattern("^(augmenter|monter) le (son|volume)$"), Command.INCREASE_VOLUME);
		//decrease volume
		frPatterns.put(pattern("^(diminuer|baisser) le (son|volume)$"), Command.DECREASE_VOLUME);
		//toogle fullscreen
		frPatterns.put(pattern("(basculer|entr(er|ée?)|sortir)? ?(en|du|le)?( ?mode)? plein écran"), Command.TOGGLE_FULLSCREEN);
		
		LOCALES.put("fr", frPatterns);
	}
	
	// Factory based on locale
	public static CommandInterpreter create(VlcHttpController controller, String locale) {
		if (!LOCALES.containsKey(locale)) {
			throw new IllegalArgumentException("This locale has not been implemented yet");
		}
		
		return new CommandInterpreter(controller, LOCALES.get(locale));
	}
	
	private LinkedHashMap<Pattern, Command> patterns;
	private VlcHttpController vlcController;
	private Command lastCommandType = null;
	private Matcher lastMatcher = null;
	
	private CommandInterpreter(VlcHttpController vlcController, LinkedHashMap<Pattern, Command> patterns) {
		this.vlcController = vlcController;
		this.patterns = patterns;
	}
	
	/**
	 * @param message: the message to interpret
	 * @return The corresponding HTTP command for VLC, or null if any
	 */
	// Caution: early return is used
	public VlcHttpCommand getCommandFor(String message, VlcHttpCommandResponseHandler responseHandler) {
		for (Pattern pattern : this.patterns.keySet()) {
			Matcher matcher = pattern.matcher(message);
			if (matcher.matches()) {
				return this.createCommand(this.patterns.get(pattern), matcher, responseHandler);
			}
		}
		return null;
	}
	
	private VlcHttpCommand createCommand(Command commandType, Matcher matcher, VlcHttpCommandResponseHandler responseHandler) {
		VlcHttpCommand command = null;
		switch (commandType) {
		case REDO:
			if (this.getLastCommandType() == null || this.getLastMatcher() == null) {
				command = null;
			} else {
				command = this.createCommand(this.getLastCommandType(), this.getLastMatcher(), responseHandler);
			}
			break;
		case PLAY_PAUSE:
			command = this.vlcController.createPlayPauseCommand(responseHandler);
			break;
		case RESTART:
			command = this.vlcController.createRestartCommand(responseHandler);
			break;
		case STOP:
			command = this.vlcController.createStopCommand(responseHandler);
			break;
		case PREVIOUS:
			command = this.vlcController.createGoPreviousCommand(responseHandler);
			break;
		case NEXT:
			command = this.vlcController.createGoNextCommand(responseHandler);
			break;
		case MUTE:
			command = this.vlcController.createMuteCommand(responseHandler);
			break;
		case RESET_VOLUME:
			command = this.vlcController.createResetVolumeCommand(responseHandler);
			break;
		case INCREASE_VOLUME:
			command = this.vlcController.createIncreaseVolumeCommand(responseHandler);
			break;
		case DECREASE_VOLUME:
			command = this.vlcController.createDecreaseVolumeCommand(responseHandler);
			break;
		case TOGGLE_FULLSCREEN:
			command = this.vlcController.createToggleFullscreenCommand(responseHandler);
			break;
		default:
			throw new UnsupportedOperationException("Command not supported yet: " + commandType);
		}
		
		if (commandType != Command.REDO) {
			this.lastCommandType = commandType;
			this.lastMatcher = matcher;
		}
		
		return command;
	}

	public Command getLastCommandType() {
		return lastCommandType;
	}

	public Matcher getLastMatcher() {
		return lastMatcher;
	}
}
