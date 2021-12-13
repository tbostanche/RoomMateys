package com.example.roommateys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChoreActivity extends AppCompatActivity {
    ChoreList listObject;
    ListView choreList;
    List<ChoreItem> choreListArray = new ArrayList<>();
    String dialogText;
    DatabaseReference db;
    String houseName;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore);
        choreList = (ListView) findViewById(R.id.choreListView);
        registerForContextMenu(choreList);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        db = FirebaseDatabase.getInstance().getReference();
        houseName = sharedPreferences.getString("houseName", "NOHOUSEFOUND");
        Query findChoreList = db.child("ChoreLists").child(houseName);

        ValueEventListener choreListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getChildrenCount() == 1) {
                        listObject = snapshot.getValue(ChoreList.class);
                        choreListArray = listObject.choreList;
                        ChoreListAdapter adapter = new ChoreListAdapter(ChoreActivity.this, choreListArray);
                        choreList.setAdapter(adapter);

                    }
                } else {
                    Log.i("DB_ERROR", "No snapshot found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("DB_ERROR", "loadList:onCancelled", error.toException());
            }
        };
        findChoreList.addValueEventListener(choreListener);

        ChoreListAdapter adapter = new ChoreListAdapter(ChoreActivity.this, choreListArray);
        choreList.setAdapter(adapter);

    }

    /* OPTIONS MENU */

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chore_list_menu, menu);
        return true;
    }

    /* CONTEXT MENU */

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.choreListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.shopping_list_long_click_menu, menu);
        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // Remove item
        if (item.getItemId() == R.id.deleteMenuOption) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChoreActivity.this);
            builder.setTitle("Remove Item From Chore List");
            builder.setMessage(String.format("Are you sure you want to remove \"%s\" from the list?", choreListArray.get(info.position).item));

            builder.setPositiveButton("Remove", (dialogInterface, i) -> {
                choreListArray.remove(info.position);
                ChoreListAdapter adapter1 = new ChoreListAdapter(ChoreActivity.this, choreListArray);
                choreList.setAdapter(adapter1);
                listObject.setChoreList(choreListArray);

                db.child("ChoreLists").child(houseName).setValue(listObject);
                db.child("ChoreLists").child(houseName).child("list").removeValue();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            if (!isFinishing()) builder.show();

            // Assign item
        } else if (item.getItemId() == R.id.assignMenuOption) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Assignment");
            final EditText input = new EditText(this);
            input.setHint("Type here...");
            builder.setView(input);

            builder.setPositiveButton("Assign", (dialogInterface, i) -> {
                ChoreItem selectedItem = choreListArray.get(info.position);
                String user = input.getText().toString();
                selectedItem.setAssignedHousemate(user);
                ChoreListAdapter adapter = new ChoreListAdapter(ChoreActivity.this, choreListArray);
                choreList.setAdapter(adapter);
                listObject.setChoreList(choreListArray);

                db.child("ChoreLists").child(houseName).setValue(listObject);
                db.child("ChoreLists").child(houseName).child("list").removeValue();
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            if (!isFinishing()) builder.show();

        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newChore) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            final EditText input = new EditText(this);
            input.setHint("Type chore here");
            builder.setView(input);
            builder.setPositiveButton("Add Chore", (dialogInterface, i) -> {
                dialogText = input.getText().toString();
                choreListArray.add(new ChoreItem(dialogText, "Not Assigned..."));
                ChoreListAdapter adapter = new ChoreListAdapter(ChoreActivity.this, choreListArray);
                choreList.setAdapter(adapter);
                listObject.setChoreList(choreListArray);

                db.child("ChoreLists").child(houseName).setValue(listObject);
                db.child("ChoreLists").child(houseName).child("list").removeValue();

            });
            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel());
            builder.show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void shoppingOnClick(View view) {
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }
    public void messageOnClick(View view) {
        Intent intent = new Intent(this, MessageActivity.class);
        startActivity(intent);
    }
    public void settingsOnClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    public void mapOnClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}