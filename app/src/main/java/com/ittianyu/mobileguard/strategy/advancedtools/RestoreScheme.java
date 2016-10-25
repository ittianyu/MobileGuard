package com.ittianyu.mobileguard.strategy.advancedtools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.RestoreContactsAndSmsActivity;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.engine.BackupRestoreEngine;
import com.ittianyu.mobileguard.strategy.OnClickListener;

import java.io.File;

/**
 * Created by yu.
 * show dialog to query user, and call engine to backup
 */
public class RestoreScheme implements OnClickListener {
    private Activity context;
    private static final int REQUEST_CODE_RESTORE = 1;

    @Override
    public void onSelected(Context context) {
        this.context = (Activity) context;
        onRestore();
    }

    /**
     * show a single select dialog to query user restore contacts or sms
     */
    private void onRestore() {
        // check sdcard
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(context, R.string.tips_sdcard_not_found, Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_on_restore)
                .setSingleChoiceItems(new String[]{context.getString(R.string.contacts), context.getString(R.string.sms)},
                        -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // if the version >= 19(Android 4.4), blacklist can't be used
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    new AlertDialog.Builder(context)
                                            .setTitle(R.string.tips)
                                            .setMessage(context.getString(R.string.tips_system_version_check)
                                                    + Build.VERSION.RELEASE
                                                    + context.getString(R.string.tips_restore_recommend_to_use_system_service))
                                            .setPositiveButton(R.string.ok, null)
                                            .show();
                                    dialog.dismiss();
                                    return;
                                }

                                Intent intent = new Intent(context,
                                        RestoreContactsAndSmsActivity.class);
                                intent.putExtra(Constant.EXTRA_RESTORE_TYPE, which);
                                context.startActivityForResult(intent, REQUEST_CODE_RESTORE);
                                dialog.dismiss();
                            }
                        })
                .show();
    }


    /**
     * call Engine to restore
     * show a Toast when backup finish
     * run on child thread
     * @param file
     * @param which
     */
    public void startRestore(final File file, final int which) {
        new Thread(){
            @Override
            public void run() {
                switch (which) {
                    case 0: {
                        // restore contacts
                        final boolean result = BackupRestoreEngine.restoreContacts(context, file);
                        // show tips
                        showToastForResult(result, R.string.success_to_restore, R.string.failed_to_restore);
                        break;
                    }
                    case 1: {
                        // restore sms
                        final boolean result = BackupRestoreEngine.restoreSms(context, file);
                        // show tips
                        showToastForResult(result, R.string.success_to_restore, R.string.failed_to_restore);
                        break;
                    }
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
