package com.example.roommateys;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class MessageHolder extends RecyclerView.ViewHolder {
    TextView displayName, messageText;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;
    public MessageHolder(@NonNull View itemView) {
        super(itemView);
        displayName = itemView.findViewById(R.id.messageDisplayName);
        messageText = itemView.findViewById(R.id.messageText);
        constraintLayout = itemView.findViewById(R.id.messageConstraintLayout);
        linearLayout = itemView.findViewById(R.id.messageLinearLayout);
    }
}
