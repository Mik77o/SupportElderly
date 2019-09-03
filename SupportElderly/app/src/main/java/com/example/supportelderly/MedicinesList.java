package com.example.supportelderly;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class MedicinesList extends AppCompatActivity {

    GridView gridViewForListOfMedicines;
    ArrayList<Medicine> listOfMedicines;
    MedicinesListAdapter adapter = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_list_activity);

        gridViewForListOfMedicines = (GridView) findViewById(R.id.gridView);
        listOfMedicines = new ArrayList<>();
        adapter = new MedicinesListAdapter(this, R.layout.medicines_items, listOfMedicines);
        gridViewForListOfMedicines.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = MainMedicinesActivity.sqLiteHelper.getData("SELECT * FROM MEDICINES");
        listOfMedicines.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nameOfMedicine = cursor.getString(1);
            String dose = cursor.getString(2);
            String frequency = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            listOfMedicines.add(new Medicine(nameOfMedicine, dose, frequency, image, id));
        }
        adapter.notifyDataSetChanged();

        gridViewForListOfMedicines.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"AKTUALIZUJ WPIS", "USUŃ WPIS"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(MedicinesList.this);

                dialog.setTitle("Wybierz akcję");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            // update
                            Cursor c = MainMedicinesActivity.sqLiteHelper.getData("SELECT id FROM MEDICINES");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            // show dialog update at here
                            showDialogUpdate(MedicinesList.this, arrID.get(position));

                        } else {
                            // delete
                            Cursor c = MainMedicinesActivity.sqLiteHelper.getData("SELECT id FROM MEDICINES");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()) {
                                arrID.add(c.getInt(0));
                            }
                            showDialogForDeleteChoice(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    ImageView imageViewMedicines;

    private void showDialogUpdate(Activity activity, final int position) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_medicines_activity);
        dialog.setTitle("Update");

        imageViewMedicines = (ImageView) dialog.findViewById(R.id.imageViewFood);
        final EditText edtMedicine = (EditText) dialog.findViewById(R.id.edtMedicine);
        final EditText edtDose = (EditText) dialog.findViewById(R.id.edtDose);
        final EditText edtTime = (EditText) dialog.findViewById(R.id.edtTime);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // set width for dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        // set height for dialog
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        MedicinesList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainMedicinesActivity.sqLiteHelper.updateData(
                            "Lek: " + edtMedicine.getText().toString().trim(),
                            "Dawka: " + edtDose.getText().toString().trim(),
                            "Częstotliwość: " + edtTime.getText().toString().trim(),
                            MainMedicinesActivity.imageViewToByte(imageViewMedicines),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Pomyślnie zaktualizowano!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception error) {
                    Log.e("Update error", error.getMessage());
                }
                updateMedicinesList();
            }
        });
    }

    private void showDialogForDeleteChoice(final int idMedicine) {
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(MedicinesList.this);

        dialogDelete.setTitle("Ostrzeżenie!!!");
        dialogDelete.setMessage("Na pewno chcesz usunąć wpis?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainMedicinesActivity.sqLiteHelper.deleteData(idMedicine);
                    Toast.makeText(getApplicationContext(), "Pomyślnie usunięto!!!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e("error", e.getMessage());
                }
                updateMedicinesList();
            }
        });

        dialogDelete.setNegativeButton("Rezygnuj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateMedicinesList() {
        // get all data from sqlite
        Cursor cursor = MainMedicinesActivity.sqLiteHelper.getData("SELECT * FROM MEDICINES");
        listOfMedicines.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String nameOfMedicine = cursor.getString(1);
            String dose = cursor.getString(2);
            String frequency = cursor.getString(3);
            byte[] image = cursor.getBlob(4);

            listOfMedicines.add(new Medicine(nameOfMedicine, dose, frequency, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 888) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 888 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewMedicines.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}