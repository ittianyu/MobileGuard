package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * restore service activity
 */
public class RestoreContactsAndSmsActivity extends BaseActivityUpEnable {
    // view
    private ListView lvFile;

    // data
    private File dir;
    private int which;
    private List<String> filesName;
    private List<String> datas;
    private List<Integer> checkedItemsId = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    /**
     * delete the selected item
     */
    private void deleteSelectedItems() {
        // no checked item
        if(0 == checkedItemsId.size()) {
            return;
        }
        // delete file
        for (int index: checkedItemsId) {
            String fileName = filesName.get(index);
            File file = new File(dir, fileName);
            file.delete();
        }
        // then reload all datas
        reloadData();
        Toast.makeText(this, R.string.success_to_delete, Toast.LENGTH_SHORT).show();
    }

    /**
     * reload all data
     */
    private void reloadData() {
        // clear list
        checkedItemsId.clear();
        datas.clear();
        filesName.clear();

        //  get file and time list
        String[] files = dir.list();
        filesName = new ArrayList<>(files.length);
        datas = new ArrayList<>(files.length);
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            String time = fileName.substring(0, fileName.lastIndexOf('.'));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = format.format(new Date(Long.parseLong(time)));
            filesName.add(fileName);
            datas.add(timeStr);
            System.out.println(fileName);
        }

        // refresh ListView
        adapter = new ArrayAdapter<>(this, R.layout.item_restore_lv, datas);
        // show list
        lvFile.setAdapter(adapter);
    }

    /**
     * construct method
     */
    public RestoreContactsAndSmsActivity() {
        super(R.string.title_restore_activity);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_restore_contacts_and_sms);
        lvFile = (ListView) findViewById(R.id.lv_file);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        Intent intent = getIntent();

        which = intent.getIntExtra(Constant.EXTRA_RESTORE_TYPE, 0);
        onRestore(which);
    }

    /**
     * search contacts backup files
     * if no files, give user a tips
     * if have files, show a ListView
     *
     * @param which 0:contacts    1:sms
     */
    private void onRestore(int which) {
        String type = null;
        switch (which) {
            case 0:
                type = "/contacts";
                break;
            case 1:
                type = "/sms";
                break;
            default:
                Toast.makeText(this, R.string.tips_no_data_to_restore, Toast.LENGTH_SHORT).show();
                return;
        }

        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + getString(R.string.app_name) + type);
        // check files exist
        if (!dir.isDirectory() || 0 == dir.list().length) {
            Toast.makeText(this, R.string.tips_no_data_to_restore, Toast.LENGTH_SHORT).show();
            return;
        }
        //  get file and time list
        String[] files = dir.list();
        filesName = new ArrayList<>(files.length);
        datas = new ArrayList<>(files.length);
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i];
            String time = fileName.substring(0, fileName.lastIndexOf('.'));
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStr = format.format(new Date(Long.parseLong(time)));
            filesName.add(fileName);
            datas.add(timeStr);
        }
        adapter = new ArrayAdapter<>(this, R.layout.item_restore_lv, datas);
        // show list
        lvFile.setAdapter(adapter);
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set on item click event
        lvFile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // normal mode
                String fileName = filesName.get(position);
                Intent intent = new Intent();

                intent.putExtra(Constant.EXTRA_RESTORE_FILE, new File(dir, fileName));
                intent.putExtra(Constant.EXTRA_RESTORE_TYPE, which);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        // enable batch delete
        lvFile.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        lvFile.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.conversation_multi_select_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.m_delete:
                        deleteSelectedItems();
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {

            }

            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                System.out.println("position:" + position + " checked:" + checked);
                if(checked) {
                    checkedItemsId.add(new Integer(position));
                } else {
                    checkedItemsId.remove(new Integer(position));
                }

            }
        });

    }

}
