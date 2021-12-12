package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
    List<String> choreListArray = new ArrayList<>();
    String dialogText;
    DatabaseReference db;
    String houseName;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore);
        choreList = (ListView) findViewById(R.id.choreListView);
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
                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, choreListArray);
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

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, choreListArray);
        choreList.setAdapter(adapter);
        choreList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("LONGCLICK", "Long click detected");
                AlertDialog.Builder builder = new AlertDialog.Builder(ChoreActivity.this);
                builder.setTitle("Remove Activity From Chore List");
                builder.setMessage(String.format("Are you sure you want to remove \"%s\" from the list?", choreListArray.get(position)));
                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("POSITION:", "" + position);
                        Log.i("SIZE:", "" + choreListArray.size());
                        choreListArray.remove(position);
                        ArrayAdapter adapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, choreListArray);
                        choreList.setAdapter(adapter1);
                        listObject.setChoreList(choreListArray);

                        db.child("ChoreLists").child(houseName).setValue(listObject);
                        db.child("ChoreLists").child(houseName).child("list").removeValue();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                if (!isFinishing()) builder.show();
                return false;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chore_list_menu, menu);
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
            builder.setPositiveButton("Add Chore", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogText = input.getText().toString();
                    choreListArray.add(dialogText);
                    choreList.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, choreListArray));
                    listObject.setChoreList(choreListArray);

                    db.child("ChoreLists").child(houseName).setValue(listObject);
                    db.child("ChoreLists").child(houseName).child("list").removeValue();

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
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