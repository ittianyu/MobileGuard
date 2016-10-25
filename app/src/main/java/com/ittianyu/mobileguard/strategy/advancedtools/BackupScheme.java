package com.ittianyu.mobileguard.strategy.advancedtools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.engine.BackupRestoreEngine;
import com.ittianyu.mobileguard.strategy.OnClickListener;

import java.io.File;

/**
 * Created by yu.
 * show dialog to query user, and call engine to backup
 */
public class BackupScheme implements OnClickListener {
    private Activity context;

    @Override
    public void onSelected(Context context) {
        this.context = (Activity) context;
        onBackup();
    }

    /**
     * show a dialog to query user whether backup contacts and sms
     */
    private void onBackup() {
        // check sdcard
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, R.string.tips_sdcard_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        // create backup view
        View view = View.inflate(context, R.layout.dialog_contacts_sms_backup, null);
        final CheckBox cbBackupContacts = (CheckBox) view.findViewById(R.id.cb_backup_contacts);
        final CheckBox cbBackupSms = (CheckBox) view.findViewById(R.id.cb_backup_sms);

        new AlertDialog.Builder(context)
                .setTitle(R.string.tips)
                .setMessage(R.string.message_backup)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startBackup(cbBackupContacts.isChecked(), cbBackupSms.isChecked());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * call Engine to backup
     * show a Toast when backup finish
     * run on child thread
     * @param contacts
     * @param sms
     */
    private void startBackup(final boolean contacts, final boolean sms) {
        new Thread(){
            @Override
            public void run() {
                // create directory to save backup files
                File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + context.getString(R.string.app_name));
                // if directory not exist, create it
                if(!appDir.isDirectory()) {
                    appDir.mkdir();
                }
                // if selected contacts, backup contacts
                if(contacts) {
                    final boolean result = BackupRestoreEngine.backupContacts(context, appDir);
                    // show tips
                    showToastForResult(result, R.string.success_to_backup, R.string.failed_to_backup);
                }
                // if selected sms, backup sms
                if(sms) {
                    final boolean result = BackupRestoreEngine.backupSms(context, appDir);
                    // show tips
                    showToastForResult(result, R.string.success_to_backup, R.string.failed_to_backup);
                }
            }
        }.start();
    }

    /**
     * show restore result
     * run on ui thread
     */
    private void showToastForResult(final boolean result, final int successStringId, final int failedStringId) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(result) {
                    Toast.makeText(context, successStringId, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, failedStringId, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
