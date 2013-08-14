package com.guillaumecharmetant.vrc;

import java.util.ArrayList;

import org.apache.http.conn.HttpHostConnectException;

import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommand;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommandResponseHandler;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpController;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpResponse;
import com.guillaumecharmetant.vrc.commandinterpreter.CommandInterpreter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainAct";

	private static final String DEFAULT_VLC_HOST_IP_KEY = "pref_vlc_host_ip";
	private static final String DEFAULT_VLC_HTTP_PORT_KEY = "pref_vlc_http_port";
	
	private SpeechRecognizer recognizer;
	private VlcHttpController vlcController;
	private final VlcHttpCommandResponseHandler responseHandler = new VlcHttpCommandResponseHandler() {
		@Override
		public void handleResponse(VlcHttpController controller, VlcHttpResponse response) {
			Log.d(TAG, "[" + String.valueOf(response.getStatusCode()) + "] " + response.getStatusText());
			Exception error = response.getError();
			if (error != null) {
				if (error.getClass() == HttpHostConnectException.class) {
					MainActivity.this.showToast(MainActivity.this.getResources().getString(R.string.error_vlc_unreachable));
				} else {
					Log.e(TAG, error.getClass().getName() + ": " + error.getMessage());
					MainActivity.this.showToast("Unexpected error: " + error.getMessage());
				}
			} else {
				String message = MainActivity.this.getResources().getString(R.string.command_executed) + " ";
				String cmdName = response.getResponseTo().getName();
				if (cmdName.contains("&")) {
					cmdName = cmdName.substring(0, cmdName.indexOf("&"));
				}
				message += MainActivity.this.getResourceString("hr_command_" + cmdName);
				MainActivity.this.showToast(message);
			}
			MainActivity.this.statusIndicator.setVisibility(ProgressBar.INVISIBLE);
		}
	};

	private Button listenButton;
	private ProgressBar statusIndicator;
	
	class STTHandler implements RecognitionListener {
		private CommandInterpreter commandInterpreter;
		
		public STTHandler(CommandInterpreter commandInterpreter) {
			this.commandInterpreter = commandInterpreter;
		}

		@Override
		public void onResults(Bundle results) {
			MainActivity.this.statusIndicator.setVisibility(ProgressBar.VISIBLE);
			boolean commandFound = false;

			ArrayList<String> possibleSentences = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (String sentence : possibleSentences) {
				log("heard: " + sentence);
				VlcHttpCommand command = this.commandInterpreter.getCommandFor(sentence, MainActivity.this.responseHandler);
				if (command != null) {
					log("executing: " + command);
					commandFound = true;
					command.execute();
					break;
				}
			}

			if (!commandFound) {
				MainActivity.this.statusIndicator.setVisibility(ProgressBar.INVISIBLE);
				MainActivity.this.showToast(MainActivity.this.getResources().getString(R.string.could_not_find_command));
			}
		}
		
		@Override
		public void onReadyForSpeech(Bundle params) {
			log("ready for speech");
			MainActivity.this.listenButton.setEnabled(false);
		}
		
		@Override
		public void onBeginningOfSpeech() {
			log("speech beginning");
		}
		
		@Override
		public void onEndOfSpeech() {
			log("speech end");
			MainActivity.this.listenButton.setEnabled(true);
		}
		
		@Override
		public void onError(int error) {
			Log.e("listener", "Error: " + String.valueOf(error));
			MainActivity.this.showToast(MainActivity.this.getResources().getString(R.string.could_not_find_command));
			MainActivity.this.listenButton.setEnabled(true);
		}
		
		@Override
		public void onBufferReceived(byte[] buffer) {
//			log("buffer reveived");
		}
		
		@Override
		public void onEvent(int eventType, Bundle params) {
//			log("event: " + String.valueOf(eventType));
		}
		
		@Override
		public void onPartialResults(Bundle partialResults) {
//			log("partial results");
		}
		
		@Override
		public void onRmsChanged(float rmsdB) {
//			log("rms changed");
		}
		
		private void log(String message) {
			Log.d("listener", message);
		}
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
//        boolean firstRun = this.getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
//        if (firstRun){
//	        this.getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//	            .edit()
//	            .putBoolean("firstrun", false)
//	            .commit();
//	        // TODO: show dialog to explain that we need to set up the host IP address
//        }
        
        setContentView(R.layout.activity_main);
        this.listenButton = (Button) this.findViewById(R.id.button_listen);
        this.statusIndicator = (ProgressBar) this.findViewById(R.id.status_indicator);
        this.statusIndicator.setVisibility(ProgressBar.INVISIBLE);
        Log.d(TAG, "on create");

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String hostIp = sharedPref.getString(DEFAULT_VLC_HOST_IP_KEY, "SETTINGS ERROR (ip)");
        String vlcHttpPort = sharedPref.getString(DEFAULT_VLC_HTTP_PORT_KEY, "SETTINGS ERROR (port)");
        
        this.vlcController = new VlcHttpController(hostIp, vlcHttpPort);
        CommandInterpreter commandInterpreter = CommandInterpreter.create(vlcController, "fr");
        
        this.recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        this.recognizer.setRecognitionListener(new STTHandler(commandInterpreter));

//      try {
//			vlcController.TEST();
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String hostIp = sharedPref.getString(DEFAULT_VLC_HOST_IP_KEY, "SETTINGS ERROR (ip)");
        String vlcHttpPort = sharedPref.getString(DEFAULT_VLC_HTTP_PORT_KEY, "SETTINGS ERROR (port)");
    	this.vlcController.setHost(hostIp, vlcHttpPort);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			showSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
    }
    private void showSettings() {
    	this.startActivity(new Intent(this, SettingsActivity.class));
    }
    
    public void listen(View view) {
		Log.d(TAG, "LISTEN CLICKED");
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.guillaumecharmetant.vvlc");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5); 
		this.recognizer.startListening(intent);
    }

	private void showToast(String textContent) {
		Toast toast = Toast.makeText(getApplicationContext(), textContent, Toast.LENGTH_LONG);
		toast.show();
	}

    /*
     * Adapted from: http://stackoverflow.com/questions/3648942/dynamic-resource-loading-android
     */
	public String getResourceString(String name) {
		Context context = getApplicationContext();
		int nameResourceID = context.getResources().getIdentifier(name, "string", context.getApplicationInfo().packageName);
		if (nameResourceID == 0) {
			throw new IllegalArgumentException("No resource string found with name " + name);
		}
		return context.getString(nameResourceID);
	}
}
