package com.example.roommateys;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ChoreListAdapter extends ArrayAdapter<ChoreItem> {

    public ChoreListAdapter(Context context, List<ChoreItem> items) {
        super(context, R.layout.shopping_list_adapter_layout, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.shopping_list_adapter_layout, parent, false);
        }

        ChoreItem item = getItem(position);

        TextView itemView = convertView.findViewById(R.id.shoppingItem);
        TextView assignedView = convertView.findViewById(R.id.assignedHousemate);

        itemView.setText((position + 1) + ". " + item.getItem());
        assignedView.setText("Assigned to: " + item.getAssignedHousemate());

        return convertView;
    }


}
