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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class ShoppingActivity extends AppCompatActivity {
    ShoppingList listObject;
    ListView shoppingList;
    List<ShoppingItem> shoppingListArray = new ArrayList<>();
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
        registerForContextMenu(shoppingList);
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
                        ShoppingListAdapter adapter = new ShoppingListAdapter(ShoppingActivity.this, shoppingListArray);
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
        findShoppingListQuery.addValueEventListener(listListener);

        ShoppingListAdapter adapter = new ShoppingListAdapter(ShoppingActivity.this, shoppingListArray);
        shoppingList.setAdapter(adapter);

    }

    /* OPTIONS MENU */

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.shopping_list_menu, menu);
        return true;
    }

    /* CONTEXT MENU */

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.shoppingListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.shopping_list_long_click_menu, menu);
        }

    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.i("LONGCLICK:", "Long click detected");

        // Remove item
        if (item.getItemId() == R.id.deleteMenuOption) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingActivity.this);
            builder.setTitle("Remove Item From Shopping List");
            builder.setMessage(String.format("Are you sure you want to remove \"%s\" from the list?", shoppingListArray.get(info.position).item));

            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i("POSITION:", "" + info.position);
                    Log.i("SIZE", "" + shoppingListArray.size());
                    shoppingListArray.remove(info.position);
                    ShoppingListAdapter adapter1 = new ShoppingListAdapter(ShoppingActivity.this, shoppingListArray);
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

            // Assign item
        } else if (item.getItemId() == R.id.assignMenuOption) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Assignment");
            final EditText input = new EditText(this);
            input.setHint("Type here...");
            builder.setView(input);

            builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ShoppingItem selectedItem = shoppingListArray.get(info.position);
                    String user = input.getText().toString();
                    selectedItem.setAssignedHousemate(user);
                    ShoppingListAdapter adapter = new ShoppingListAdapter(ShoppingActivity.this, shoppingListArray);
                    shoppingList.setAdapter(adapter);
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

        } else {
            return super.onContextItemSelected(item);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.newItem) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Title");
            final EditText input = new EditText(this);
            input.setHint("Type here...");
            builder.setView(input);
            builder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogText = input.getText().toString();
                    shoppingListArray.add(new ShoppingItem(dialogText, "Not Assigned..."));
                    ShoppingListAdapter adapter = new ShoppingListAdapter(ShoppingActivity.this, shoppingListArray);
                    shoppingList.setAdapter(adapter);
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