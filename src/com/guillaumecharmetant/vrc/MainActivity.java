package com.guillaumecharmetant.vrc;

import java.util.ArrayList;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpCommand;
import com.guillaumecharmetant.vlchttpcontroller.VlcHttpController;
import com.guillaumecharmetant.vrc.commandinterpreter.CommandInterpreter;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	private static final String TAG = "test";

	private static final String DEFAULT_VLC_HOST_IP_KEY = "pref_vlc_host_ip";
	private static final String DEFAULT_VLC_HTTP_PORT_KEY = "pref_vlc_http_port";
	
	private SpeechRecognizer recognizer;
	private VlcHttpController vlcController;
	
	class STTHandler implements RecognitionListener {
		private CommandInterpreter commandInterpreter;
		
		public STTHandler(CommandInterpreter commandInterpreter) {
			this.commandInterpreter = commandInterpreter;
		}

		@Override
		public void onResults(Bundle results) {
			ArrayList<String> possibleSentences = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			for (String sentence : possibleSentences) {
				log("heard: " + sentence);
				VlcHttpCommand command = this.commandInterpreter.getCommandFor(sentence);
				if (command != null) {
					log("executing: " + command);
					command.execute();
					break;
				}
			}
		}
		
		@Override
		public void onReadyForSpeech(Bundle params) {
			log("ready for speech");
		}
		
		@Override
		public void onError(int error) {
			Log.e("listener", "Error: " + String.valueOf(error));
		}
		
		@Override
		public void onEndOfSpeech() {
			log("speech end");
		}
		
		@Override
		public void onBeginningOfSpeech() {
			log("speech beginning");
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
}
