package edu.njit.cs656.nfcapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SendMessageActivity extends AppCompatActivity {

    private Uri[] mFileUris = new Uri[10];
    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
    }

    /** Called when the user taps the Send button */
//    public void send(View view) {
//
//    }


    /** Called when the user taps the Send button */
/**    public void sendMessage(View view) {

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);

        editText.setOnEditorActionListener(new DoneOnEditorActionListener());

        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
*/
}
