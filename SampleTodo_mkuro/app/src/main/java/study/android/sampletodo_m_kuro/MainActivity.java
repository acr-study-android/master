package study.android.sampletodo_m_kuro;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private BaseDialog mEditDialog;
    private BaseDialog mShowDialog;
    private ArrayAdapter<String> mArrayAdapter;
    private ArrayList<RemindItem> mRemindItems = new ArrayList<>();
    private CallbackListener mCallbackListener = new CallbackListener() {
        @Override
        public void onNotifyPositiveAction() {
            onNotifyPositiveAction(null);
        }

        @Override
        public void onNotifyPositiveAction(RemindItem item) {
            Log.v(LOG_TAG, "onNotifyPositiveAction item:" + item);
            if (item != null) {
                item.setId(mRemindItems.size());
                mRemindItems.add(item);
                updateList();
            }
        }

        @Override
        public void onNotifyNegativeAction() {
            Log.v(LOG_TAG, "onNotifyNegativeAction");
            // Nothing to do.
        }
    };
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Log.v(LOG_TAG, "onItemClick");
            ListView list = (ListView)adapterView;
            String clickName = (String)list.getItemAtPosition(position);
            RemindItem clickItem = null;
            for (RemindItem item : mRemindItems) {
                if (clickName.equals(item.getName())) {
                    clickItem = item;
                    break;
                }
            }
            createShowRemindDialog(clickItem).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch(v.getId()) {
            case R.id.list_view:
                getMenuInflater().inflate(R.menu.context, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item == null) {
            return false;
        }
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        RemindItem remindItem = mRemindItems.get(info.position);
        switch(item.getItemId()) {
            case R.id.move_complete:
                createShowRemindDialog(remindItem)
                        .setNegativeButton(null)
                        .setTitle(R.string.confirm_complete).show();
                break;
            case R.id.edit:
                createEditRemindDialog(remindItem).show(remindItem.getId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.move_complete:
                break;
            case R.id.add_remind:
                createEditRemindDialog(null).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initialize() {
        ListView listview = findViewById(R.id.list_view);
        updateList();
        listview.setAdapter(mArrayAdapter);
        listview.setOnItemClickListener(mItemClickListener);

        registerForContextMenu(listview);
    }

    private void updateList() {
        final ArrayList<String> itemsList = new ArrayList<>();
        for (RemindItem item : mRemindItems) {
            if (!item.isComplete()) {
                itemsList.add(item.getName());
            }
        }
        if (mArrayAdapter == null) {
            mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        } else {
            mArrayAdapter.clear();
            mArrayAdapter.addAll(itemsList);
            mArrayAdapter.notifyDataSetChanged();
        }
    }

    private ShowRemindDialog createShowRemindDialog(RemindItem item) {
        if (mShowDialog != null) {
            mShowDialog.unregisterCallbackListener(mCallbackListener);
        }
        mShowDialog = new ShowRemindDialog(this);
        mShowDialog.init();
        mShowDialog.registerCallbackListener(mCallbackListener);
        return (ShowRemindDialog)mShowDialog.createDialogLayout(item);
    }

    private EditRemindDialog createEditRemindDialog(RemindItem item) {
        if (mEditDialog != null) {
            mEditDialog.unregisterCallbackListener(mCallbackListener);
        }
        mEditDialog = new EditRemindDialog(this);
        mEditDialog.init();
        mEditDialog.registerCallbackListener(mCallbackListener);
        return (EditRemindDialog)mEditDialog.createDialogLayout(item);
    }

    /**
     * CallbackListener is interface that notify action from other class.
     */
    interface CallbackListener {
        void onNotifyPositiveAction();

        void onNotifyPositiveAction(RemindItem item);

        void onNotifyNegativeAction();
    }
}
