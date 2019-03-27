package study.android.sampletodo_m_kuro;

import android.os.AsyncTask;
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
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            mShowDialog.createDialogLayout().show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        asyncInitialize();
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
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.move_complete:
                mShowDialog.show();
                break;
            case R.id.add_remind:
                mEditDialog.show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void asyncInitialize() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mEditDialog.init();
                mEditDialog.registerCallbackListener(mCallbackListener);
                return null;
            }
        }.execute();

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                mShowDialog.init();
                mShowDialog.registerCallbackListener(mCallbackListener);
                return null;
            }
        }.execute();
    }

    private void initialize() {
        mEditDialog = new EditRemindDialog(this);
        mShowDialog = new ShowRemindDialog(this);

        ListView listview = findViewById(R.id.list_view);
        updateList();
        listview.setAdapter(mArrayAdapter);
        listview.setOnItemClickListener(mItemClickListener);

        registerForContextMenu(listview);
    }

    private void updateList() {
        final ArrayList<String> itemsList = new ArrayList<>();
        for (RemindItem item : mRemindItems) {
            itemsList.add(item.getName());
        }
        if (mArrayAdapter == null) {
            mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemsList);
        } else {
            mArrayAdapter.clear();
            mArrayAdapter.addAll(itemsList);
            mArrayAdapter.notifyDataSetChanged();
        }
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
