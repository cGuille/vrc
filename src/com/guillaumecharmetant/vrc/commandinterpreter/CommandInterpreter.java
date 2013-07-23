package com.guillaumecharmetant.vrc.commandinterpreter;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommand;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommandResponseHandler;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpController;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpResponse;

public class CommandInterpreter {
	private static final String TAG = "CI";
	
	enum Command {
		REDO,
		PLAY_PAUSE,
		RESTART,
		STOP,
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
	
	private static final VlcHttpCommandResponseHandler RESPONSE_HANDLER = new VlcHttpCommandResponseHandler() {
		@Override
		public void handleResponse(VlcHttpController controller, VlcHttpResponse response) {
			Log.d(TAG, "[" + String.valueOf(response.getStatusCode()) + "] " + response.getStatusText());
			Exception error = response.getError();
			if (error != null) {
				if (error.getClass() == IOException.class) {
					Log.d(TAG, "ADVICE: have you started VLC and added the web interface in VLC (View > Add Interface > Web)?");
				}
			}
		}
	};
	
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
	public VlcHttpCommand getCommandFor(String message) {
		for (Pattern pattern : this.patterns.keySet()) {
			Matcher matcher = pattern.matcher(message);
			if (matcher.matches()) {
				return this.createCommand(this.patterns.get(pattern), matcher);
			}
		}
		return null;
	}
	
	private VlcHttpCommand createCommand(Command commandType, Matcher matcher) {
		VlcHttpCommand command = null;
		switch (commandType) {
		case REDO:
			if (this.getLastCommandType() == null || this.getLastMatcher() == null) {
				command = null;
			} else {
				command = this.createCommand(this.getLastCommandType(), this.getLastMatcher());
			}
			break;
		case PLAY_PAUSE:
			command = this.vlcController.createPlayPauseCommand(RESPONSE_HANDLER);
			break;
		case RESTART:
			command = this.vlcController.createRestartCommand(RESPONSE_HANDLER);
			break;
		case STOP:
			command = this.vlcController.createStopCommand(RESPONSE_HANDLER);
			break;
		case MUTE:
			command = this.vlcController.createMuteCommand(RESPONSE_HANDLER);
			break;
		case RESET_VOLUME:
			command = this.vlcController.createResetVolumeCommand(RESPONSE_HANDLER);
			break;
		case INCREASE_VOLUME:
			command = this.vlcController.createIncreaseVolumeCommand(RESPONSE_HANDLER);
			break;
		case DECREASE_VOLUME:
			command = this.vlcController.createDecreaseVolumeCommand(RESPONSE_HANDLER);
			break;
		case TOGGLE_FULLSCREEN:
			command = this.vlcController.createToggleFullscreenCommand(RESPONSE_HANDLER);
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
