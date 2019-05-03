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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
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
                                    insertData(name,date,place,memo);
                                    updateListView();
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
                menu.setHeaderTitle("メニュー");
                getMenuInflater().inflate(R.menu.list_menu, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String[] a = readData();
        final String s = a[info.position];
        String[] strs = {s};

        switch(item.getItemId()){
            case R.id.listview_done:
                deleteData(strs);
                updateListView();
                return true;
            case R.id.listview_delete:
                deleteData(strs);
                updateListView();
                return true;
            case R.id.listview_edit:
                final View view = this.getLayoutInflater().inflate(R.layout.create_layout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            private String updateName = s;

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
                                String date = ((TextView)view.findViewById(R.id.date)).getText().toString();
                                String place = ((TextView)view.findViewById(R.id.place)).getText().toString();
                                String memo = ((TextView)view.findViewById(R.id.memo)).getText().toString();

                                if(checkDuplicateName(name)){
                                    Toast toast = Toast.makeText(activity, "名前が重複しているため登録できません。", Toast.LENGTH_LONG);
                                    // 位置調整
                                    toast.setGravity(Gravity.CENTER, 0, -200);
                                    toast.show();
                                }else {
                                    updateData(updateName,name, date, place, memo);
                                    updateListView();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                //SQLiteから値を取得する
                Map map = readOneData(s);
                ((TextView)view.findViewById(R.id.name)).setText((String)map.get("name"));
                ((TextView)view.findViewById(R.id.date)).setText((String)map.get("date"));
                ((TextView)view.findViewById(R.id.place)).setText((String)map.get("place"));
                ((TextView)view.findViewById(R.id.memo)).setText((String)map.get("memo"));

                builder.show();
                return true;
        }

        return false;
    }

    private String[] readData(){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

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

        db.close();

        return array;
    }

    private Map readOneData(String name){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                "tododb",
                new String[] { "name", "date", "place", "memo"},
                "name = '" + name + "'",
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        Map<String, String> map = new HashMap<String, String>();

        for(int i = 0; i < cursor.getCount(); i++){
            map.put("name", cursor.getString(0));
            map.put("date", cursor.getString(1));
            map.put("place", cursor.getString(2));
            map.put("memo", cursor.getString(3));

            cursor.moveToNext();
        }

        cursor.close();

        db.close();

        return map;
    }

    private void insertData(String name, String date, String place, String memo){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("place", place);
        values.put("memo", memo);

        db.insert("tododb", null, values);

        db.close();
    }

    private void updateData(String before_name, String name, String date, String place, String memo){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("place", place);
        values.put("memo", memo);

        db.update("tododb", values,"name = ?", new String[] {before_name});

        db.close();
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

    private void updateListView(){
        String[] array = readData();
        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, array);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

    private void deleteData(String[] strs){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        db.delete("tododb", "name=?", strs);

        db.close();
    }
}
