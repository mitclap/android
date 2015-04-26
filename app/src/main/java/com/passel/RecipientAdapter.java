package com.passel;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by matthewgotteiner on 8/6/14.
 */
public class RecipientAdapter extends CursorAdapter {

    private Context mContext;

    private final String[] PROJECTION = new String[]{
            Data._ID,
            Contacts.DISPLAY_NAME_PRIMARY,
            Phone.NUMBER
    };

    public static final int DISPLAY_NAME_INDEX = 1;
    public static final int PHONE_NUMBER_INDEX = 2;

    private final String SELECTION = '(' + Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? OR " + Phone.NUMBER + " LIKE ?) AND " +
            Phone.TYPE + "='" + Phone.TYPE_MOBILE + "' AND " +
            Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'";


    public RecipientAdapter(Context context) {
        super(context, null, false);
        mContext = context;
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        return mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, PROJECTION, SELECTION, new String[]{constraint + "%", constraint + "%"}, Contacts.DISPLAY_NAME_PRIMARY + " DESC");
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(PHONE_NUMBER_INDEX);
    }

    private static class ViewHolder {
        public TextView mDisplayName;
        public TextView mPhoneNumber;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        holder.mDisplayName = (TextView) view.findViewById(android.R.id.text1);
        holder.mPhoneNumber = (TextView) view.findViewById(android.R.id.text2);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        String displayName = cursor.getString(DISPLAY_NAME_INDEX);
        String phoneNumber = cursor.getString(PHONE_NUMBER_INDEX);
        holder.mDisplayName.setText(displayName);
        holder.mPhoneNumber.setText(phoneNumber);
    }
}
