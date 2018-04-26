package edu.njit.cs656.nfcapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RealPathUtil {

    public static String getRealPathFromURI(Context context, Uri uri){
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(uri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getRealPathFromContactURI(Context context, Uri uri)  {
        Cursor cursor = null;

            String[] proj = {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.LOOKUP_KEY  };
            cursor = context.getContentResolver().query(uri,  proj, null, null, null);
            //int column_index = cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME);
            //String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)); // DISPLAY_NAME_PRIMARY will use other info if their name is not available
            cursor.moveToFirst();
            String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

            if (cursor != null) {
                cursor.close();
            }
            //return cursor.getString(column_index);


        StringBuilder sb = new StringBuilder();
        AssetFileDescriptor fd = null;
        Uri uriC = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
        try {
            fd = context.getContentResolver().openAssetFileDescriptor(uriC, "r");
            FileInputStream fis = fd.createInputStream();
            byte[] b = new byte[(int)fd.getDeclaredLength()];
            fis.read(b);

            String vCard = new String(b);
            sb.append(vCard);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();

    }
}
