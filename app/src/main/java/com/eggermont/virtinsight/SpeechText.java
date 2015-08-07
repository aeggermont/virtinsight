package com.eggermont.virtinsight;

/**
 *  This class is used to enter descriptions of an album event
 *  in the form of speech recording, text or both
 */

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class SpeechText extends Activity {

    private static final String DEBUG_TAG = SpeechText.class.getCanonicalName();
    private static final int ACTION_RECORD_SPEECH = 100;

    private EditText mTxtSpeechInput;
    private TextView mTextAlbumName;
    private Button mRecordSpeech;
    private Button mButtonFinishTextInput;
    private String textInput = "";

    /**
     * This method adds text from speech to text recognition with
     * new lines for each text entry.
     * @param text to be added to the album event
     */
    private void addText(String text){
        textInput += System.getProperty("line.separator") + text;
    }

    /**
     * Returns the current string saved from speec input text.
     * @return textInput saved from TextEdit widget
     */
    private String getTextInput(){
        return textInput;
    }

    /**
     * This method creates a new intent to send back captured
     * text results to the AlbumEvent activity
     */

    private void returnToAlbum(){
        Log.i(DEBUG_TAG, "Activity being destroyed!");
        Intent updateAlbumEvent = new Intent(SpeechText.this, AlbumEvent.class);
        updateAlbumEvent.putExtra("eventDescription", mTxtSpeechInput.getText().toString());
        setResult(RESULT_OK, updateAlbumEvent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_text);

        Intent albumEventIntent = getIntent();
        String albumName = albumEventIntent.getExtras().getString("albumName");
        String albumDesc = albumEventIntent.getExtras().getString("albumDesc");

        Log.i(DEBUG_TAG, "Album name: " + albumName);
        Log.i(DEBUG_TAG, "Album description: " + albumDesc);

        mTxtSpeechInput = (EditText) findViewById(R.id.txtSpeechInput);
        mTextAlbumName = (TextView) findViewById(R.id.TextAlbumName);
        mTextAlbumName.setText(albumName);
        mTxtSpeechInput.setVisibility(EditText.INVISIBLE);

        // Handling Recording of speech
        mRecordSpeech = (Button) findViewById(R.id.ButtonRecordSpeech);
        mRecordSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });

        // Handling returning to the calling activity
        mButtonFinishTextInput = (Button) findViewById(R.id.ButtonFinishTextInput);
        mButtonFinishTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToAlbum();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speech_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case ACTION_RECORD_SPEECH:{
                if (resultCode == RESULT_OK && null != data ){

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    addText(result.get(0) + ".");
                    mTxtSpeechInput.setText(getTextInput());
                    mTxtSpeechInput.setVisibility(EditText.VISIBLE);
                }
                break;
            }// ACTION_RECORD_SPEECH
        }
    }

    /**
     * Showing google speech input dialog
     * TODO: Need to check if the intent is available first
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, ACTION_RECORD_SPEECH);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
