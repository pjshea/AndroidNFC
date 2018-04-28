package edu.njit.cs656.nfcapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.logging.Logger;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity {

    private EditText edittext;
    private Uri[] mFileUris = new Uri[10];
    private NfcAdapter mNfcAdapter;

    private FileUriCallback mFileUriCallback;;

    private int PICK_IMAGE_REQUEST = 1;
    private String realPath = "";

    private EditText contactPath;
    private int PICK_CONTACT = 2;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    TextView smsAddress,smsBody;
    private int PICK_SMS = 3;
    //public enum PickType { PICK_IMAGE_REQUEST, PICK_CONTACT, PICK_SMS };

    /**
     * Called when the main activity is first created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device
        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_LONG).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_LONG).show();
        }
        else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_LONG).show();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edittext = findViewById(R.id.editText);
        edittext.setEnabled(false);

        contactPath = findViewById(R.id.contactPath);
        contactPath.setEnabled(false);

        smsAddress = findViewById(R.id.address);
        smsBody = findViewById(R.id.body);


    }

    /**
     * Getting a Result from the browse activities for pictures, messages, contacts
     * @param requestCode The request code you passed to startActivityForResult()
     * @param resultCode A result code specified by the second activity.
     *                   This is either RESULT_OK if the operation was successful or
     *                   RESULT_CANCELED if the user backed out or the operation failed for some reason.
     * @param data An Intent that carries the result data - picture, contact, or SMS data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            realPath = RealPathUtil.getRealPathFromURI(this, uri);
            Log.i("Main Activity", "Real Path: "+realPath);

            // Capture the layout's TextView and set the string as its text
            edittext.setText(realPath);
        }

        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            realPath = RealPathUtil.getRealPathFromContactURI(this, uri);
            Log.i("Main Activity", "Real Path: "+realPath);

            contactPath.setText(realPath);
        }

        if (requestCode == PICK_SMS && resultCode == RESULT_OK && data != null) {
            HashMap<String, String> msg = (HashMap<String, String>)data.getSerializableExtra("lines");

            // Capture the layout's TextView and set the string as its text
            smsAddress.setText(msg.get("line1"));
            smsBody.setText(msg.get("line2"));
        }
    }

    private class FileUriCallback implements
            NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

            if(realPath != "") {
                File requestFile = new File(realPath);
                requestFile.setReadable(true, false);
                Uri fileUri = Uri.fromFile(requestFile);
                Log.i("FileUri: ", ""+fileUri);
                if (fileUri != null) {
                    mFileUris[0] = fileUri;
                    Log.i("Main Activity", "File URI available for transfer.");
                } else {
                    Log.e("Main Activity", "No File URI available for file.");
                }
            }
            else{
                Log.e("Main Activity", "No File selected for transfer.");
            }
            // Get a URI for the File and add it to the list of URIs
        }
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    public void browsePhotos(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void sendPicture(View view) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!mNfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!mNfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            mFileUriCallback = new FileUriCallback();
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
            Toast.makeText(this, "Place both devices’ backs against each other.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Gets the URI to the to the contact VCF file
     * @return An NDEF Record that contains the URI to the contact VCF file
     */
    private NdefRecord getContactRecord()
    {
        byte[] uriField = realPath.getBytes(Charset.forName("US-ASCII"));
        byte[] payload = new byte[uriField.length + 1];  // Add 1 for the URI Prefix.
        System.arraycopy(uriField, 0, payload, 1, uriField.length);  // Append URI to payload.
        NdefRecord nfcRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, "text/vcard".getBytes(), new byte[0], payload);
        Log.i("Main Activity", "Returning nfcRecord: "+nfcRecord.toString());
        return nfcRecord;
    }

    /**
     * Starts the Intent to browse through the stored contacts on the device
     * @param view
     */
    public void browseContacts(View view){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Contact"), PICK_CONTACT);
    }

    /**
     * Push the NDef record with contact to the NFC device
     * @param view
     */
    public void sendContact(View view) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(!mNfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!mNfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
                @Override
                public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                    return new NdefMessage(new NdefRecord[] { getContactRecord() });
                }
            }, this);

            Toast.makeText(this, "Place both devices’ backs against each other.", Toast.LENGTH_LONG).show();

            // Register callback to listen for message-sent success
            //mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
    }

    /**
     * Create a TNF_WELL_KNOWN NDEF record for the text message selected
     * @param payload String data to be added to the NdefRecord
     * @param locale Locale to use
     * @param encodeInUtf8 should be encoded in UTF8(true) or UTF16 (false)
     * @return NdefRecord
     */
    public NdefRecord createTextRecord(String payload, Locale locale, boolean encodeInUtf8) {
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
        byte[] textBytes = payload.getBytes(utfEncoding);
        int utfBit = encodeInUtf8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        return record;
    }

    /**
     * Start the SendMessageActivity to browse the text messages on the device.
     * @param view
     */
    public void browseMessages(View view) {
        Intent inent = new Intent(this, SendMessageActivity.class);

        //startActivity(inent);
        startActivityForResult(inent, PICK_SMS);
    }

    /**
     * Push the NDef record with test message data to the NFC device
     * @param view
     */
    public void sendMessage(View view) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        final String smsTextString = smsAddress.getText() + "\r\n" + smsBody.getText();

        if(!mNfcAdapter.isEnabled()){
            // NFC is disabled, show the settings UI to enable NFC
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if(!mNfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }
        else
        {
            // Register callback to set NDEF message
            mNfcAdapter.setNdefPushMessageCallback(new NfcAdapter.CreateNdefMessageCallback() {
                @Override
                public NdefMessage createNdefMessage(NfcEvent nfcEvent) {
                    return new NdefMessage(new NdefRecord[] { createTextRecord(smsTextString, Locale.getDefault(), false) });
                }
            }, this);
            Toast.makeText(this, "Place both devices’ backs against each other." + smsTextString, Toast.LENGTH_LONG).show();
        }
   }
}
