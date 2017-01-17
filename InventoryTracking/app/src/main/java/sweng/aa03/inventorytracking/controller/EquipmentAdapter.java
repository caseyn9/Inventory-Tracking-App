package sweng.aa03.inventorytracking.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import sweng.aa03.inventorytracking.R;
import sweng.aa03.inventorytracking.model.Equipment;


/**
 * Created by nicky on 02-Dec-16.
 */
public class EquipmentAdapter extends ArrayAdapter<Equipment> {


    public EquipmentAdapter(Context context, List<Equipment> objects) { super(context, 0, objects); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Equipment equipment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_project,parent, false);
        }
        TextView text = (TextView) convertView.findViewById(R.id.textView);
        text.setText(equipment.name);
        return convertView;
    }
}

