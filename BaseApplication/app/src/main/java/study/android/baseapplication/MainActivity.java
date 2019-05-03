package study.android.baseapplication;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TodoOpenHelper helper;
    private SQLiteDatabase db;
    private MainActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        String[] array = readData();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        registerForContextMenu(listView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_create:
                final View view = this.getLayoutInflater().inflate(R.layout.create_layout, null);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(helper == null){
                                    helper = new TodoOpenHelper(getApplicationContext());
                                }

                                if(db == null){
                                    db = helper.getWritableDatabase();
                                }

                                String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
                                String date = ((TextView)view.findViewById(R.id.date)).getText().toString();
                                String place = ((TextView)view.findViewById(R.id.place)).getText().toString();
                                String memo = ((TextView)view.findViewById(R.id.memo)).getText().toString();

                                if(checkDuplicateName(name)){
                                    Toast toast = Toast.makeText(activity, "名前が重複しているため登録できません。", Toast.LENGTH_LONG);
                                    // 位置調整
                                    toast.setGravity(Gravity.CENTER, 0, -200);
                                    toast.show();
                                }else{
                                    insertData(db, name,date,place,memo);
                                    String[] array = readData();
                                    ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, array);
                                    ListView listView = (ListView)findViewById(R.id.listView);
                                    listView.setAdapter(adapter);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch(v.getId()){
            case R.id.listView:

                AdapterView.AdapterContextMenuInfo info =
                        (AdapterView.AdapterContextMenuInfo) menuInfo;
                String[] a = {"test1","test2"};
                String s = a[info.position];
                menu.setHeaderTitle(s);

                getMenuInflater().inflate(
                        R.menu.list_menu, menu
                );

                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.listview_delete:
                return true;

            case R.id.listview_edit:
                return true;
        }
        return false;
    }

    private String[] readData(){
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                "tododb",
                new String[] { "name"},
                null,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        String[] array = new String[cursor.getCount()];
        for(int i = 0; i < cursor.getCount(); i++){
            array[i] = cursor.getString(0);
            cursor.moveToNext();
        }

        cursor.close();

        return array;
    }

    private void insertData(SQLiteDatabase db, String name, String date, String place, String memo){

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("place", place);
        values.put("memo", memo);

        db.insert("tododb", null, values);
    }

    /**
     * 名前の重複をチェックする
     * @param name タスク名
     * @return true：重複あり false:重複なし
     */
    private boolean checkDuplicateName(String name){
        String[] names = readData();

        for(String n:names){
            //重複がある場合はメソッドを終了する・
            if(n.equals(name)){
                return true;
            }
        }

        return false;
    }
}
