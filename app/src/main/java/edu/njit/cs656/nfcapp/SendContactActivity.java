package edu.njit.cs656.nfcapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.widget.AdapterView;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.net.Uri;
import android.os.*;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SendContactActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {

    private Uri[] mFileUris = new Uri[10];
    private NfcAdapter mNfcAdapter;
    private String realPath = "";
    private SendContactActivity.FileUriCallback mFileUriCallback;

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
        setContentView(R.layout.activity_send_contact);
        // Gets the ListView from the View list of the parent activity
        mContactsList =
                (ListView) this.findViewById(R.id.contactslist);
        // Gets a CursorAdapter
        mCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.contacts_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        mContactsList.setAdapter(mCursorAdapter);

        mContactsList.setOnItemClickListener(this);
        // Initializes the loader
        //getLoaderManager().initLoader(0, null, this);

    }



    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1
    };
    // Define global mutable variables
    // Define a ListView object
    ListView mContactsList;
    // Define variables for the contact the user selects
    // The contact's _ID value
    long mContactId;
    // The contact's LOOKUP_KEY
    String mContactKey;
    // A content URI for the selected contact
    Uri mContactUri;
    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    // Empty public constructor, required by the system
    public SendContactActivity() {
    }

//    /*
//    // A UI Fragment must inflate its View
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the fragment layout
//        return inflater.inflate(R.layout.contacts_list_view,
//                container, false);
//    }
//    */
//
//    /*
//    public void onActivityCreated(Bundle savedInstanceState) {
//        // Always call the super method first
//        super.onActivityCreated(savedInstanceState);
//        // Gets the ListView from the View list of the parent activity
//        mContactsList =
//                (ListView) getActivity().findViewById(R.layout.contacts_list_view);
//        // Gets a CursorAdapter
//        mCursorAdapter = new SimpleCursorAdapter(
//                getActivity(),
//                R.layout.contacts_list_item,
//                null,
//                FROM_COLUMNS, TO_IDS,
//                0);
//        // Sets the adapter for the ListView
//        mContactsList.setAdapter(mCursorAdapter);
//
//        mContactsList.setOnItemClickListener(this);
//        // Initializes the loader
//        getLoaderManager().initLoader(0, null, this);
//
//    }
//    */

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    Build.VERSION.SDK_INT
                            >= Build.VERSION_CODES.HONEYCOMB ?
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                            ContactsContract.Contacts.DISPLAY_NAME

            };

    // The column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;
    // The column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    // Defines the text expression
    @SuppressLint("InlinedApi")
    private static final String SELECTION =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = {mSearchString};

    @Override
    public void onItemClick(
            AdapterView<?> parent, View item, int position, long rowID) {
        // Get the Cursor
        Cursor cursor = ((SimpleCursorAdapter) parent.getAdapter()).getCursor();
        // Move to the selected contact
        cursor.moveToPosition(position);
        // Get the _ID value
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        // Get the selected LOOKUP KEY
        mContactKey = getString(LOOKUP_KEY_INDEX);
        // Create the contact's content Uri
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
        /*
         * You can use mContactUri as the content URI for retrieving
         * the details for a contact.
         */

    }


    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
        public FileUriCallback() {

            if(realPath != "") {
                File requestFile = new File(realPath);
                requestFile.setReadable(true, false);
                Uri fileUri = mContactUri;
                System.out.println("FileUri: "+fileUri);
                if (fileUri != null) {
                    mFileUris[0] = fileUri;
                    Log.i("Main Activity", "File URI available for transfer.");
                } else {
                    Log.e("Main Activity", "No File URI available for file.");
                }
            }
            else{
                Log.e("Send Contact via NFC", "No File selected for transfer.");
            }
            // Get a URI for the File and add it to the list of URIs
        }
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            return mFileUris;
        }
    }

    public void send(View view) {
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
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        }
    }

//    @Override
//    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
//        /*
//         * Makes search string into pattern and
//         * stores it in the selection array
//         */
//        mSelectionArgs[0] = "%" + mSearchString + "%";
//        // Starts the query
//        return new CursorLoader(
//                getActivity(),
//                ContactsContract.Contacts.CONTENT_URI,
//                PROJECTION,
//                SELECTION,
//                mSelectionArgs,
//                null
//        );
//    }


    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);
    }

    //@Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Delete the reference to the existing Cursor
        mCursorAdapter.swapCursor(null);

    }
}
