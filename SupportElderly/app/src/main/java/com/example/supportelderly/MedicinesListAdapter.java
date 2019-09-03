package com.example.supportelderly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MedicinesListAdapter extends BaseAdapter {

    private Context contextOfApplication;
    private int layout;
    private ArrayList<Medicine> medicinesList;

    public MedicinesListAdapter(Context context, int layout, ArrayList<Medicine> medicinesList) {
        this.contextOfApplication = context;
        this.layout = layout;
        this.medicinesList = medicinesList;
    }

    @Override
    public int getCount() {
        return medicinesList.size();
    }

    @Override
    public Object getItem(int position) {
        return medicinesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView txtMedicines, txtDose, txtFrequency;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) contextOfApplication.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtMedicines = (TextView) row.findViewById(R.id.txtMedicines);
            holder.txtDose = (TextView) row.findViewById(R.id.txtDose);
            holder.txtFrequency = (TextView) row.findViewById(R.id.txtFrequency);
            holder.imageView = (ImageView) row.findViewById(R.id.imgMedicines);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Medicine medicine = medicinesList.get(position);

        holder.txtMedicines.setText(medicine.getNameOfMedicine());
        holder.txtDose.setText(medicine.getDose());
        holder.txtFrequency.setText(medicine.getFrequency());
        byte[] medicineImage = medicine.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(medicineImage, 0, medicineImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
