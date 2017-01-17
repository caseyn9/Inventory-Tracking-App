package sweng.aa03.inventorytracking.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Equipment;
import sweng.aa03.inventorytracking.view.AssignActivity;

/**
 * Created by eganl on 06/12/2016.
 */
public class EquipmentOptionsAdapter extends ArrayAdapter<Equipment>{

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference equipmentRef = rootRef.child("Equipment");

    public EquipmentOptionsAdapter(Context context, List<Equipment> objects) { super(context, 0, objects); }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Equipment equipment = getItem(position);
        //TODO: Show the project name and individual name for items that have been taken out
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_equipment,parent, false);
        }
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        TextView individualName = (TextView) convertView.findViewById(R.id.individualName);
        if (equipment.individualID != null) {
            individualName.setText("Assigned to individual: " + equipment.individualID);
        }

        Button left = (Button) convertView.findViewById(R.id.buttonLeft);
        if (equipment.projectID!= null || equipment.individualID != null) {
            if (equipment.damaged) {
                left.setBackgroundColor(Color.RED);
                left.setText("DAMAGED");
            } else {
                left.setText("Mark As Damaged");
                left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(), "Marked as damaged.", Toast.LENGTH_LONG).show();
                        equipment.damaged = true;
                        equipmentRef.child(equipment.getKey()).setValue(equipment);
                    }
                });
            }
        } else {
            if (equipment.damaged) {
                left.setBackgroundColor(Color.RED);
                left.setText("Damaged");
            } else {
                left.setVisibility(View.INVISIBLE);
            }
        }

        Button right = (Button) convertView.findViewById(R.id.buttonRight);
        if (equipment.projectID!= null || equipment.individualID != null) {
            right.setText("Return");
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Item returned.", Toast.LENGTH_LONG).show();
                    equipment.projectID = null;
                    equipment.individualID = null;
                    equipmentRef.child(equipment.getKey()).setValue(equipment);
                }
            });
        } else {
            if (!equipment.damaged) {
                right.setText("Assign");
                right.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), AssignActivity.class);
                        intent.putExtra("KEY", equipment.getKey());
                        getContext().startActivity(intent);
                    }
                });
            } else {
                right.setVisibility(View.INVISIBLE);
            }
        }

        itemName.setText(equipment.name);
        return convertView;
    }
}
