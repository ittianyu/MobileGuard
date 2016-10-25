package com.ittianyu.mobileguard.engine;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.domain.ContactBackupBean;
import com.ittianyu.mobileguard.domain.SmsBackupBean;
import com.ittianyu.mobileguard.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu.
 * Provide contacts and sms backup.
 */
public class BackupRestoreEngine {
    private static final String CONTENT_SMS = "content://sms";

    /**
     * backup contacts
     * @param context
     * @param dir the directory of backup files
     * @return true if success, false otherwise
     */
    public static boolean backupContacts(final Context context, File dir) {
        // contacts directory
        File contactsDir = new File(dir.getAbsolutePath() + "/contacts");
        // if directory not exist, create it
        if(!contactsDir.isDirectory()) {
            boolean mkdir = contactsDir.mkdir();
            System.out.println("contactsDir create:" + mkdir);
        }

        // query contacts
        List<ContactBackupBean> contacts = queryContacts(context);
        if(contacts.size() == 0) {
//            Message.obtain(handler, MSG_TOAST, R.string.tips_no_data_to_backup).sendToTarget();
            System.out.println(context.getString(R.string.tips_no_data_to_backup));
            return false;
        }

        // generate json
        Gson gson = new Gson();
        String json = gson.toJson(contacts);
        System.out.println(json);
        // save file
        // get file name
        String fileName = generateFileName() + ".json";
        try {
            FileUtils.saveFileWithString(new File(contactsDir, fileName), json);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(context.getString(R.string.failed_to_save_file));
//            Message.obtain(handler, MSG_TOAST, R.string.failed_to_save_file).sendToTarget();
            return false;
        }
        return true;
    }

    /**
     * restore sms
     * @param context
     * @param backupFile the backup file
     * @return true if success, false otherwise
     */
    public static boolean restoreContacts(final Context context, File backupFile) {
        FileReader reader = null;
        try {
            reader = new FileReader(backupFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
//            Message.obtain(handler, MSG_TOAST, R.string.file_not_found).sendToTarget();
            System.out.println(context.getString(R.string.file_not_found));
//            Toast.makeText(context, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            return false;
        }

        // parse json
        Gson gson = new Gson();
        List<ContactBackupBean> contacts = gson.fromJson(reader, new TypeToken<List<ContactBackupBean>>(){}.getType());
        System.out.println(contacts);
        if(contacts.size() == 0) {
//            Toast.makeText(context, R.string.tips_no_data_to_restore, Toast.LENGTH_SHORT).show();
//            Message.obtain(handler, MSG_TOAST, R.string.tips_no_data_to_restore).sendToTarget();
            System.out.println(context.getString(R.string.tips_no_data_to_restore));
            return false;
        }

        // restore contacts database
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        // delete all data and raw_contacts
        ops.add(ContentProviderOperation.newDelete(Data.CONTENT_URI).build());
        ops.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI).build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            ops.clear();
        }

        for (ContactBackupBean contact: contacts) {
            // add to raw_contacts
            ops.add(ContentProviderOperation
                    .newInsert(RawContacts.CONTENT_URI)
                    .withValue(RawContacts.ACCOUNT_NAME, contact.getAccountName())
                    .withValue(RawContacts.ACCOUNT_TYPE, contact.getAccountType())
                    .build());

            // add to data
            for (ContactBackupBean.ContactBackupDataBean data: contact.getDatas()) {
                ops.add(ContentProviderOperation
                        .newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, data.getMimetype())
                        .withValue(Data.DATA1, data.getData())
                        .build());
            }
            // apply batch
            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
//            Toast.makeText(context, R.string.failed_to_restore, Toast.LENGTH_SHORT).show();
//            Message.obtain(handler, MSG_TOAST, R.string.failed_to_restore).sendToTarget();
                System.out.println(context.getString(R.string.failed_to_restore));

                return false;
            } finally {
                // never forget to clear when success to insert a contact
                ops.clear();
            }

        }
        return true;
    }

    /**
     * backup sms
     * @param context
     * @param dir the directory of backup files
     * @return true if success, false otherwise
     */
    public static boolean backupSms(Context context, File dir) {
        // contacts directory
        File smsDir = new File(dir.getAbsolutePath() + "/sms");
        // if directory not exist, create it
        if(!smsDir.isDirectory()) {
            boolean mkdir = smsDir.mkdir();
            System.out.println("smsDir create:" + mkdir);
        }

        // query contacts
        List<SmsBackupBean> smses = querySms(context);
        if(smses.size() == 0) {
            System.out.println(context.getString(R.string.tips_no_data_to_backup));
            return false;
        }
        // generate json
        Gson gson = new Gson();
        String json = gson.toJson(smses);
        System.out.println(json);
        // save to file
        // get file name
        String fileName = generateFileName() + ".json";
        try {
            FileUtils.saveFileWithString(new File(smsDir, fileName), json);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(context.getString(R.string.failed_to_save_file));
            return false;
        }
        return true;
    }


    /**
     * restore sms
     * @param context
     * @param backupFile the backup file
     * @return true if success, false otherwise
     */
    public static boolean restoreSms(final Context context, File backupFile) {
        FileReader reader = null;
        try {
            reader = new FileReader(backupFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(context.getString(R.string.file_not_found));
            return false;
        }

        // parse json
        Gson gson = new Gson();
        List<SmsBackupBean> smses = gson.fromJson(reader, new TypeToken<List<SmsBackupBean>>(){}.getType());
        System.out.println(smses);
        if(smses.size() == 0) {
            System.out.println(context.getString(R.string.tips_no_data_to_restore));
            return false;
        }
        // start restore
        for (SmsBackupBean sms: smses) {
            ContentValues values = new ContentValues(sms.getDatas().size());
            // add data to ContentValues
            for (SmsBackupBean.Data data: sms.getDatas()) {
                values.put(data.getKey(), data.getValue());
            }
            // query the sms whether exist
            Cursor cursor = context.getContentResolver().query(
                    Uri.parse(CONTENT_SMS),
                    new String[]{"_id"},
                    "address =? and body=? and date=?",
                    new String[]{values.getAsString("address"),
                            values.getAsString("body"),
                            values.getAsString("date")},
                    null);
            // if the sms not exist, insert it
            if (cursor.getCount() == 0) {
                context.getContentResolver().insert(
                        Uri.parse(CONTENT_SMS), values);
                System.out.println("success restore a sms");
            }
            cursor.close();// never forget close
        }

        return true;
    }

    /**
     * generate file by current time
     */
    private static String generateFileName() {
        return System.currentTimeMillis() + "";
    }

    /**
     * query all contacts that not deleted
     * @param context
     * @return return a list which would not be null.
     */
    public static List<ContactBackupBean> queryContacts(Context context) {
        // create a list to contain contact bean
        List<ContactBackupBean> contacts = new ArrayList<>();

        // start to query
        ContentResolver resolver = context.getContentResolver();

        Cursor contactCursor = resolver
                .query(RawContacts.CONTENT_URI,
                        new String[]{RawContacts._ID, RawContacts.ACCOUNT_NAME, RawContacts.ACCOUNT_TYPE},
                        RawContacts.DELETED + "!=1", // the deleted contacts should not backup
                        null, null);
        while (contactCursor.moveToNext()) {
            // create bean and add to list
            ContactBackupBean contact = new ContactBackupBean();
            contacts.add(contact);
            // read value
            int id = contactCursor.getInt(0);
            String accountName = contactCursor.getString(1);
            String accountType = contactCursor.getString(2);
            // set to bean
            contact.setAccountName(accountName);
            contact.setAccountType(accountType);

            // create datas bean and set datas to bean
            List<ContactBackupBean.ContactBackupDataBean> datas = new ArrayList<>();
            contact.setDatas(datas);
            // query data
            Cursor dataCursor = resolver
                    .query(ContactsContract.Data.CONTENT_URI,
                            new String[]{Data._ID, Data.MIMETYPE, Data.DATA1},
                            Data.RAW_CONTACT_ID + "=? and " + Data.DATA1 + " not null",
                            new String[]{id + ""},
                            null);
            while (dataCursor.moveToNext()) {
                // get value
                String mimetype = dataCursor.getString(1);
                String data = dataCursor.getString(2);
                // create data bean and add to datas
                ContactBackupBean.ContactBackupDataBean dataBean = new ContactBackupBean.ContactBackupDataBean(mimetype, data);
                datas.add(dataBean);
            }
            dataCursor.close();
        }
        contactCursor.close();
        return contacts;
    }

    /**
     * query all sms
     * @param context
     * @return return a list which would not be null.
     */
    public static List<SmsBackupBean> querySms(Context context) {
        // create a list to contain contact bean
        List<SmsBackupBean> smses = new ArrayList<>();

        // select all sms
        Cursor cursor = context.getContentResolver()
                .query(android.net.Uri.parse(CONTENT_SMS)
                        , null, null, null, null);
        // record all column and value
        int colCount = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            // create sms and add to list
            SmsBackupBean sms = new SmsBackupBean();
            smses.add(sms);

            // create sms datas and set it
            List<SmsBackupBean.Data> datas = new ArrayList<>();
            sms.setDatas(datas);

            // start from 2, for detail to see the database
            for (int i = 2; i < colCount; i++) {
                String name = cursor.getColumnName(i);
                String value = cursor.getString(i);
//                System.out.println(name + ":" + value);
                // create data and add to datas
                datas.add(new SmsBackupBean.Data(name, value));
            }
        }
        cursor.close();

        return smses;
    }

}
