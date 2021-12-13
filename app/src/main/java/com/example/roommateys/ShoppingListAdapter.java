package com.example.roommateys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class ShoppingListAdapter extends ArrayAdapter<ShoppingItem> {

    public ShoppingListAdapter(Context context, List<ShoppingItem> items) {
        super(context, R.layout.shopping_list_adapter_layout, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.shopping_list_adapter_layout, parent, false);

        }

        ShoppingItem item = getItem(position);

        TextView itemView = convertView.findViewById(R.id.shoppingItem);
        TextView assignedView = convertView.findViewById(R.id.assignedHousemate);

        itemView.setText((position + 1) + ". " + item.item);
        assignedView.setText("Assigned to: " + item.assignedHousemate);

        return convertView;
    }
}
