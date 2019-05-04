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

        //リストに表示するデータを取得し設定する
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

                //ダイアログのタイトルを設定
                TextView title = (TextView)view.findViewById(R.id.title);
                title.setText("Todo作成");

                //ダイアログの作成
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { //OKボタンが押された場合
                                //入力情報の取得
                                String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
                                String date = ((TextView)view.findViewById(R.id.date)).getText().toString();
                                String place = ((TextView)view.findViewById(R.id.place)).getText().toString();
                                String memo = ((TextView)view.findViewById(R.id.memo)).getText().toString();

                                if(checkDuplicateName(name)){
                                    //タスク名が重複している場合は登録できない旨トーストで表示
                                    Toast toast = Toast.makeText(activity, "名前が重複しているため登録できません。", Toast.LENGTH_LONG);

                                    // 位置調整
                                    toast.setGravity(Gravity.CENTER, 0, -200);
                                    toast.show();
                                }else{
                                    //データの追加
                                    insertData(name,date,place,memo);
                                    updateListView();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() { //キャンセルボタンが押された場合
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //何もしない
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
        //選択されたTodoの名前を取得する
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String[] a = readData();
        final String s = a[info.position];
        String[] strs = {s};

        switch(item.getItemId()){
            case R.id.listview_done:
            case R.id.listview_delete:
                //削除と完了の場合はデータを削除する
                deleteData(strs);
                updateListView();
                return true;
            case R.id.listview_edit:
                final View view = this.getLayoutInflater().inflate(R.layout.create_layout, null);

                //ダイアログのタイトルを設定する
                TextView title = (TextView)view.findViewById(R.id.title);
                title.setText("Todo編集");

                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setView(view)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            private String updateName = s;

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //入力情報を取得する
                                String name = ((TextView)view.findViewById(R.id.name)).getText().toString();
                                String date = ((TextView)view.findViewById(R.id.date)).getText().toString();
                                String place = ((TextView)view.findViewById(R.id.place)).getText().toString();
                                String memo = ((TextView)view.findViewById(R.id.memo)).getText().toString();

                                if(checkDuplicateName(name)){
                                    //Todo名が重複してる場合はトースターで登録できない旨表示する
                                    Toast toast = Toast.makeText(activity, "名前が重複しているため登録できません。", Toast.LENGTH_LONG);
                                    // 位置調整
                                    toast.setGravity(Gravity.CENTER, 0, -200);
                                    toast.show();
                                }else {
                                    //タスクの更新
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

                //SQLiteから値を取得し入力欄へ設定
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

    /**
     * SQLiteよりTodo名の一覧を取得する
     * @return Todo名
     */
    private String[] readData(){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        //SQLiteへのアクセス
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }

        //データの取得
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

        //取得したデータを文字列配列に格納する
        String[] array = new String[cursor.getCount()];
        for(int i = 0; i < cursor.getCount(); i++){
            array[i] = cursor.getString(0);
            cursor.moveToNext();
        }

        //使ったものを閉じる
        cursor.close();
        db.close();
        helper.close();

        return array;
    }

    /**
     * Todo名からTodoの情報を取得する
     * @param name 取得したいTodoの名前
     * @return Todo情報のMap
     */
    private Map readOneData(String name){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        //SQLiteへのアクセス
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getReadableDatabase();
        }

        //データの取得
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

        //取得したデータをMapに格納する
        Map<String, String> map = new HashMap<String, String>();
        for(int i = 0; i < cursor.getCount(); i++){
            map.put("name", cursor.getString(0));
            map.put("date", cursor.getString(1));
            map.put("place", cursor.getString(2));
            map.put("memo", cursor.getString(3));

            cursor.moveToNext();
        }

        //使ったものを閉じる
        cursor.close();
        db.close();
        helper.close();

        return map;
    }

    private void insertData(String name, String date, String place, String memo){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        //SQLiteへアクセス
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        //追加するデータを格納する
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("place", place);
        values.put("memo", memo);

        //データを追加
        db.insert("tododb", null, values);

        //使ったものを閉じる
        db.close();
        helper.close();
    }

    /**
     * Todoを更新する
     * @param before_name 更新するTodoの元の名前
     * @param name 更新後のTodo名
     * @param date 日付
     * @param place 場所
     * @param memo メモ
     */
    private void updateData(String before_name, String name, String date, String place, String memo){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        //SQLiteへアクセス
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        //更新する情報設定する
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("date", date);
        values.put("place", place);
        values.put("memo", memo);

        //更新
        db.update("tododb", values,"name = ?", new String[] {before_name});

        //使ったものを閉じる
        db.close();
        helper.close();
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

    /**
     * Todo一覧のリストを更新する
     */
    private void updateListView(){
        String[] array = readData();
        ArrayAdapter adapter = new ArrayAdapter(activity, android.R.layout.simple_list_item_1, array);
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

    }

    /**
     * Todoを削除する
     * @param strs 削除するTodoの文字列配列
     */
    private void deleteData(String[] strs){
        TodoOpenHelper helper = null;
        SQLiteDatabase db = null;

        //SQLiteへの接続
        if(helper == null){
            helper = new TodoOpenHelper(getApplicationContext());
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        //Todoの削除
        db.delete("tododb", "name=?", strs);

        //使ったものを閉じる
        db.close();
        helper.close();
    }
}
