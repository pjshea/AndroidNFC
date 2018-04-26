package edu.njit.cs656.nfcapp;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Contacts;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SendMessageActivity extends AppCompatActivity {

    private Uri[] mFileUris = new Uri[10];
    private NfcAdapter mNfcAdapter;

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;

    private String[][] StatesAndCapitals =
            {{"Alabama","Montgomery"},
                    {"Alaska","Juneau"},
                    {"Arizona","Phoenix"},
                    {"Arkansas","Little Rock"},
                    {"California","Sacramento"},
                    {"Colorado","Denver"},
                    {"Connecticut","Hartford"},
                    {"Delaware","Dover"},
                    {"Florida","Tallahassee"},
                    {"Georgia","Atlanta"},
                    {"Hawaii","Honolulu"},
                    {"Idaho","Boise"},
                    {"Illinois","Springfield"},
                    {"Indiana","Indianapolis"},
                    {"Iowa","Des Moines"},
                    {"Kansas","Topeka"},
                    {"Kentucky","Frankfort"},
                    {"Louisiana","Baton Rouge"},
                    {"Maine","Augusta"}};

    ArrayList<HashMap<String,String>> messages = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);


        // Check or Get permission to read SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
                Toast.makeText(this, "The app needs permission to read SMS messages",
                        Toast.LENGTH_SHORT).show();
                // sees the explanation, try again to request the permission.

            } else {
                // Request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
            }
        }
        else
        {
            showMessages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    showMessages();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    public void showMessages() {

        ListView lstMessages = (ListView) findViewById(R.id.lstMessages);
        Uri smsUri = Uri.parse("content://sms");

        Cursor cur = getContentResolver().query(smsUri, new String[]{"_id", "address", "date", "body"},null, null, null);
        if (cur.moveToFirst()) { // must check the result to prevent exception
            HashMap<String,String> msg;
            do {
                Date date = new Date(cur.getLong(2));
                String formattedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(date);

                String line1 = formattedDate + "  " + cur.getString(1);
                String body = cur.getString(3);

                msg = new HashMap<String,String>();
                msg.put("line1", line1);
                msg.put("line2", body);
                messages.add(msg);
            } while (cur.moveToNext());

        } else {
            Toast.makeText(this, "SMS Inbox is empty", Toast.LENGTH_SHORT).show();
        }

        adapter = new SimpleAdapter(this, messages,
                R.layout.two_line_list,
                new String[] { "line1","line2" },
                new int[] {R.id.address, R.id.body});

        ((ListView)findViewById(R.id.lstMessages)).setAdapter(adapter);
    }

    /** Called when the user taps the Send button */
    public void send(View view) {
        Toast.makeText(this, "SEND MESSAGE", Toast.LENGTH_LONG).show();

    }
}
