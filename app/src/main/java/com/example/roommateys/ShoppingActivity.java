package com.example.roommateys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingActivity extends AppCompatActivity {
    ShoppingList listObject;
    ListView shoppingList;
    List<String> shoppingListArray = new ArrayList<>();
    String dialogText;
    DatabaseReference db;
    SharedPreferences sharedPreferences;
    String houseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);
        sharedPreferences = getSharedPreferences("com.example.roommateys", Context.MODE_PRIVATE);
        houseName = sharedPreferences.getString("houseName", "NOHOUSEFOUND");
        shoppingList = (ListView) findViewById(R.id.shoppingListView);
        db = FirebaseDatabase.getInstance().getReference();

        Query findShoppingListQuery = db.child("ShoppingLists").child(houseName);
        ValueEventListener listListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() == 1) {
                        Log.i("DB", dataSnapshot.toString());

                        listObject = dataSnapshot.getValue(ShoppingList.class);
                        shoppingListArray = listObject.shoppingList;

                        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingListArray);

                        shoppingList.setAdapter(adapter);
                    }
                } else {
                    Log.i("DBERROR", dataSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("DB_ERROR", "loadList:onCancelled", databaseError.toException());
            }
        };
        findShoppingListQuery.addListenerForSingleValueEvent(listListener);

        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingListArray);

        shoppingList.setAdapter(adapter);
        shoppingList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i("LONGCLICK:", "Long click detected");
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingActivity.this);
                builder.setTitle("Remove Item From Shopping List");
                builder.setMessage(String.format("Are you sure you want to remove \"%s\" from the list?", shoppingListArray.get(position)));

                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.i("POSITION:", "" + position);
                        Log.i("SIZE", "" + shoppingListArray.size());
                        shoppingListArray.remove(position);

                        ArrayAdapter adapter1 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingListArray);

                        shoppingList.setAdapter(adapter1);
                        listObject.setShoppingList(shoppingListArray);

                        db.child("ShoppingLists").child(houseName).setValue(listObject);
                        db.child("ShoppingLists").child(houseName).child("list").removeValue();
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

        inflater.inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            // Set up the input
            final EditText input = new EditText(this);
            input.setHint("Type here...");
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(input);
            // Set up the buttons
            builder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogText = input.getText().toString();
                    shoppingListArray.add(dialogText);
                    shoppingList.setAdapter(new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, shoppingListArray));
                    listObject.setShoppingList(shoppingListArray);

                    db.child("ShoppingLists").child(houseName).setValue(listObject);
                    db.child("ShoppingLists").child(houseName).child("list").removeValue();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void choreOnClick(View view) {
        Intent intent = new Intent(this, ChoreActivity.class);
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