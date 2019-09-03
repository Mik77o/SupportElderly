package com.example.supportelderly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainMedicinesActivity extends AppCompatActivity {

    EditText edtMedicine, edtDose, edtTime;
    Button btnChoose, btnAdd, btnListOfMedicines;
    ImageView imageView;

    final int REQUEST_CODE_GALLERY = 7;

    public static SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medicines);

        init();

        sqLiteHelper = new SQLiteHelper(this, "MEDICINESDB.sqlite", null, 1);

        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS MEDICINES(Id INTEGER PRIMARY KEY AUTOINCREMENT, nameOfMedicine VARCHAR, dose VARCHAR, frequency VARCHAR, image BLOB)");

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(
                        MainMedicinesActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GALLERY
                );
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    sqLiteHelper.insertData(
                            "Lek: " + edtMedicine.getText().toString().trim(),
                            "Dawka: " + edtDose.getText().toString().trim(),
                            "Częstotliwość: " + edtTime.getText().toString().trim(),
                            imageViewToByte(imageView)
                    );
                    Toast.makeText(getApplicationContext(), "Pomyślnie dodano!", Toast.LENGTH_SHORT).show();
                    edtMedicine.setText("");
                    edtDose.setText("");
                    edtTime.setText("");
                    imageView.setImageResource(R.drawable.medicine_icon);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnListOfMedicines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainMedicinesActivity.this, MedicinesList.class);
                startActivity(intent);
            }
        });
    }

    public static byte[] imageViewToByte(ImageView imageOfMedicine) {
        Bitmap bitmap = ((BitmapDrawable) imageOfMedicine.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            } else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void init() {
        edtMedicine = (EditText) findViewById(R.id.edtMedicine);
        edtDose = (EditText) findViewById(R.id.edtDose);
        edtTime = (EditText) findViewById(R.id.edtTime);
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnListOfMedicines = (Button) findViewById(R.id.btnListOfMedicines);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

}
