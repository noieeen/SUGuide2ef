package com.example.noie.suguide;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.noie.suguide.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import static com.example.noie.suguide.db.DatabaseHelper.COL_ID;
import static com.example.noie.suguide.db.DatabaseHelper.COL_IMAGE;
import static com.example.noie.suguide.db.DatabaseHelper.COL_TYPE;
import static com.example.noie.suguide.db.DatabaseHelper.COL_TITLE;
import static com.example.noie.suguide.db.DatabaseHelper.COL_DATE;
import static com.example.noie.suguide.db.DatabaseHelper.COL_CALORIE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDb;
    private List<FoodItem> mFoodItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Add = findViewById(R.id.Add_Button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,AddFoodActivity.class);
                startActivity(i);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPhoneData();
        setupListView();
    }
    private void loadPhoneData() {
        Cursor c = mDb.query(TABLE_NAME, null, null, null, null, null, null);

        mFoodItemList = new ArrayList<>();
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(COL_ID));
            String type = c.getString(c.getColumnIndex(COL_TYPE));
            String title = c.getString(c.getColumnIndex(COL_TITLE));
            String date = c.getString(c.getColumnIndex(COL_DATE));
            String calorie = c.getString(c.getColumnIndex(COL_CALORIE));
            String image = c.getString(c.getColumnIndex(COL_IMAGE));

            FoodItem item = new FoodItem(id, type,title, date,calorie, image);
            mFoodItemList.add(item);
        }
        c.close();
    }

    private void setupListView() {
        FoodListAdapter adapter = new FoodListAdapter(
                MainActivity.this,
                R.layout.item_food,
                mFoodItemList
        );
        ListView lv = findViewById(R.id.result_list_view);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                FoodItem item = mFoodItemList.get(position);

                Toast t = Toast.makeText(MainActivity.this, item.number, Toast.LENGTH_SHORT);
                t.show();

                Intent intent = new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + item.number)
                );
                startActivity(intent);

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String[] items = new String[]{
                        "Edit",
                        "Delete"
                };

                new AlertDialog.Builder(MainActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final FoodItem phoneItem = mFoodItemList.get(position);

                                switch (i) {
                                    case 0: // Edit
                                        Intent intent = new Intent(MainActivity.this, EditPhoneItemActivity.class);
                                        intent.putExtra("title", phoneItem.title);
                                        intent.putExtra("number", phoneItem.number);
                                        intent.putExtra("id", phoneItem._id);
                                        startActivity(intent);
                                        break;
                                    case 1: // Delete
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("ต้องการลบข้อมูลเบอร์โทรนี้ ใช่หรือไม่")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        mDb.delete(
                                                                TABLE_NAME,
                                                                COL_ID + " = ?",
                                                                new String[]{String.valueOf(phoneItem._id)}
                                                        );
                                                        loadPhoneData();
                                                        setupListView();
                                                    }
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                        break;
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }
}


/*
package com.example.emergencyphonenumber;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.emergencyphonenumber.adapter.PhoneListAdapter;
import com.example.emergencyphonenumber.db.DatabaseHelper;
import com.example.emergencyphonenumber.model.PhoneItem;
import java.util.ArrayList;
import java.util.List;
import static com.example.emergencyphonenumber.db.DatabaseHelper.COL_ID;
import static com.example.emergencyphonenumber.db.DatabaseHelper.COL_IMAGE;
import static com.example.emergencyphonenumber.db.DatabaseHelper.COL_NUMBER;
import static com.example.emergencyphonenumber.db.DatabaseHelper.COL_TITLE;
import static com.example.emergencyphonenumber.db.DatabaseHelper.TABLE_NAME;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private DatabaseHelper mHelper;
    private SQLiteDatabase mDb;
    private List<PhoneItem> mPhoneItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new DatabaseHelper(MainActivity.this);
        mDb = mHelper.getWritableDatabase();

        Button addPhoneItemButton = findViewById(R.id.add_phone_item_button);
        addPhoneItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddPhoneItemActivity.class);
                startActivity(intent);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPhoneData();
        setupListView();
    }

    private void loadPhoneData() {
        Cursor c = mDb.query(TABLE_NAME, null, null, null, null, null, null);

        mPhoneItemList = new ArrayList<>();
        while (c.moveToNext()) {
            long id = c.getLong(c.getColumnIndex(COL_ID));
            String title = c.getString(c.getColumnIndex(COL_TITLE));
            String number = c.getString(c.getColumnIndex(COL_NUMBER));
            String image = c.getString(c.getColumnIndex(COL_IMAGE));

            PhoneItem item = new PhoneItem(id, title, number, image);
            mPhoneItemList.add(item);
        }
        c.close();
    }

    private void setupListView() {
        PhoneListAdapter adapter = new PhoneListAdapter(
                MainActivity.this,
                R.layout.item_phone,
                mPhoneItemList
        );
        ListView lv = findViewById(R.id.result_list_view);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PhoneItem item = mPhoneItemList.get(position);

                Toast t = Toast.makeText(MainActivity.this, item.number, Toast.LENGTH_SHORT);
                t.show();

                Intent intent = new Intent(
                        Intent.ACTION_DIAL,
                        Uri.parse("tel:" + item.number)
                );
                startActivity(intent);

            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                String[] items = new String[]{
                        "Edit",
                        "Delete"
                };

                new AlertDialog.Builder(MainActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final PhoneItem phoneItem = mPhoneItemList.get(position);

                                switch (i) {
                                    case 0: // Edit
                                        Intent intent = new Intent(MainActivity.this, EditPhoneItemActivity.class);
                                        intent.putExtra("title", phoneItem.title);
                                        intent.putExtra("number", phoneItem.number);
                                        intent.putExtra("id", phoneItem._id);
                                        startActivity(intent);
                                        break;
                                    case 1: // Delete
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setMessage("ต้องการลบข้อมูลเบอร์โทรนี้ ใช่หรือไม่")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        mDb.delete(
                                                                TABLE_NAME,
                                                                COL_ID + " = ?",
                                                                new String[]{String.valueOf(phoneItem._id)}
                                                        );
                                                        loadPhoneData();
                                                        setupListView();
                                                    }
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                        break;
                                }
                            }
                        })
                        .show();

                return true;
            }
        });
    }
}
*/