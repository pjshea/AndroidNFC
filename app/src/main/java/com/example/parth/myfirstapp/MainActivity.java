package com.example.parth.myfirstapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private EditText edittext;
    private Uri[] mFileUris = new Uri[10];
    private NfcAdapter mNfcAdapter;
    private FileUriCallback mFileUriCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext = findViewById(R.id.editText);
    }

    public void sendFile(View view) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!mNfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!mNfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            mFileUriCallback = new FileUriCallback();
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        }
    }
//transferimage
    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {
            String transferFile = "20180219_182819_HDR.jpg";
            File extDir = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES);
            File requestFile = new File(extDir, transferFile);
            requestFile.setReadable(true, false);
            // Get a URI for the File and add it to the list of URIs
            Uri fileUri = Uri.fromFile(requestFile);
            if (fileUri != null) {
                mFileUris[0] = fileUri;
                Log.i("My Activity", "File URI available for transfer.");
            } else {
                Log.e("My Activity", "No File URI available for file.");
            }
        }

        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }
    
    /** Called when the user taps the Send button */
    public void sendMessage(View view) {

        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);

        editText.setOnEditorActionListener(new DoneOnEditorActionListener());

        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}
