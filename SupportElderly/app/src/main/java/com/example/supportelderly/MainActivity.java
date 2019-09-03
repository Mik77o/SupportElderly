package com.example.supportelderly;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CALL = 4;
    private static final int REQUEST_CAMERA = 8;

    TextView dateView;
    FloatingActionButton fab, fab_event;
    CardView calls, contacts, camera, medicines, messages, send_message, flashlight, calendar, battery, emergency, gallery, gps;
    String[] days = new String[]{"Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        emergency = findViewById(R.id.emergency);
        emergency.setOnClickListener(this);
        calls = findViewById(R.id.call);
        calls.setOnClickListener(this);
        contacts = findViewById(R.id.contacts);
        contacts.setOnClickListener(this);
        medicines = findViewById(R.id.medicines);
        medicines.setOnClickListener(this);
        messages = findViewById(R.id.messages);
        messages.setOnClickListener(this);
        send_message = findViewById(R.id.send_message);
        send_message.setOnClickListener(this);
        camera = findViewById(R.id.cameraDevice);
        camera.setOnClickListener(this);
        flashlight = findViewById(R.id.flash_light);
        flashlight.setOnClickListener(this);
        calendar = findViewById(R.id.calendar);
        calendar.setOnClickListener(this);
        fab_event = findViewById(R.id.floating_button_settings);
        fab_event.setOnClickListener(this);
        fab = findViewById(R.id.floating_button);
        fab.setOnClickListener(this);
        battery = findViewById(R.id.battery_module);
        battery.setOnClickListener(this);
        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(this);
        gps = findViewById(R.id.my_position);
        gps.setOnClickListener(this);


        String dateFormatString = DateFormat.getDateInstance().format(new Date());
        dateView = findViewById(R.id.dateView);
        Calendar calendar = Calendar.getInstance();
        String day = days[calendar.get(Calendar.DAY_OF_WEEK) - 2];
        dateView.setText(dateFormatString + "  " + day);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.call:
                intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
                break;
            case R.id.emergency:
                new AlertDialog.Builder(this)
                        .setTitle("Wzywanie pomocy!")
                        .setMessage("Czy na pewno chcesz zadzwonić na numer ratunkowy 112?")
                        .setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                makePhoneCallForHelp();
                            }
                        }).setNegativeButton("NIE", null).show();
                break;
            case R.id.contacts:
                intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
                break;
            case R.id.send_message:
                intent = new Intent(this, SMSActivity.class);
                startActivity(intent);
                break;
            case R.id.messages:
                intent = getPackageManager()
                        .getLaunchIntentForPackage(Telephony.Sms.getDefaultSmsPackage(this));
                startActivity(intent);
                break;
            case R.id.medicines:
                intent = new Intent(this, MainMedicinesActivity.class);
                startActivity(intent);
                break;
            case R.id.flash_light:
                intent = new Intent(this, FlashLightActivity.class);
                startActivity(intent);
                break;
            case R.id.cameraDevice:
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    intent = new Intent(this, CameraActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.calendar:
                Calendar calendar = Calendar.getInstance();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, calendar.getTimeInMillis());
                intent = new Intent(Intent.ACTION_VIEW)
                        .setData(builder.build());
                startActivity(intent);
                break;
            case R.id.floating_button_settings:
                Calendar cal = Calendar.getInstance();
                Intent intentForCalendarEvents = new Intent(Intent.ACTION_EDIT);
                intentForCalendarEvents.setType("vnd.android.cursor.item/event");
                intentForCalendarEvents.putExtra("beginTime", cal.getTimeInMillis());
                intentForCalendarEvents.putExtra("allDay", true);
                intentForCalendarEvents.putExtra("rule", "FREQ=YEARLY");
                intentForCalendarEvents.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
                intentForCalendarEvents.putExtra("title", "DODAJ WYDARZENIE");
                startActivity(intentForCalendarEvents);
                break;

            case R.id.floating_button:
                new AlertDialog.Builder(this)
                        .setTitle("Zamykanie aplikacji")
                        .setMessage("Na pewno chcesz zamknąć aplikację?")
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setNegativeButton("Nie", null).show();
                break;

            case R.id.battery_module:
                intent = new Intent(this, BatteryActivity.class);
                startActivity(intent);
                break;

            case R.id.gallery:
                intent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(intent);
                break;

            case R.id.my_position:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactsData = data.getData();
                CursorLoader loader = new CursorLoader(this, contactsData, null, null, null, null);
                Cursor c = loader.loadInBackground();
                if (c.moveToFirst()) {
                    String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            }
        }
    }

    private void makePhoneCallForHelp() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + "112";
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCallForHelp();
            } else {
                Toast.makeText(this, "Brak pozwolenia!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

