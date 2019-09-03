package com.example.supportelderly;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class SMSActivity extends AppCompatActivity {

    private static final int REQUEST_SMS = 0;
    private static final int REQ_PICK_CONTACT = 1;
    private EditText phoneNumberEditText;
    private EditText messageEditText;

    private TextView sendStatus;
    private TextView deliveryStatus;
    private CardView sendMessage;
    private ImageView pickContact;

    private BroadcastReceiver sentStatusBroadcastReceiver, deliveredStatusBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        phoneNumberEditText = (EditText) findViewById(R.id.phone_number_edit_text);
        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        sendMessage = (CardView) findViewById(R.id.send_button);
        sendStatus = (TextView) findViewById(R.id.message_status_text_view);
        deliveryStatus = (TextView) findViewById(R.id.delivery_status_text_view);
        pickContact = (ImageView) findViewById(R.id.add_contact_image_view);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    int hasSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);
                    if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                            showMessageOKCancel("Musisz pozwolić aplikacji na wysyłanie smsów!",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                                REQUEST_SMS);
                        return;
                    }
                    sendMySMSToReceiver();
                }
            }
        });

        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void sendMySMSToReceiver() {

        String phoneNumber = phoneNumberEditText.getText().toString();
        String message = messageEditText.getText().toString();

        if (phoneNumber.isEmpty()) {
            String text = "Proszę wprowadzić poprawny numer telefonu!";
            SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
            biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, text.length(), 0);
            Toast.makeText(getApplicationContext(), biggerText, Toast.LENGTH_LONG).show();
        } else {

            SmsManager smsManager = SmsManager.getDefault();
            // Jeśli wiadomość jest zbyt długa, to jest ona podzielona.
            List<String> messagesDivided = smsManager.divideMessage(message);
            for (String msg : messagesDivided) {

                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                smsManager.sendTextMessage(phoneNumber, null, msg, sentIntent, deliveredIntent);

            }
        }
    }

    public void onResume() {
        super.onResume();
        sentStatusBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String infoSMS = "Nieznany błąd";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        infoSMS = "Wiadomość wysłana!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        infoSMS = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        infoSMS = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        infoSMS = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        infoSMS = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                sendStatus.setText(infoSMS);

            }
        };
        deliveredStatusBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String infoDelivering = "Wiadomość nie została dostarczona!";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        infoDelivering = "Wiadomość została dostarczona!";
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                deliveryStatus.setText(infoDelivering);
                phoneNumberEditText.setText("");
                messageEditText.setText("");
            }
        };
        registerReceiver(sentStatusBroadcastReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusBroadcastReceiver, new IntentFilter("SMS_DELIVERED"));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusBroadcastReceiver);
        unregisterReceiver(deliveredStatusBroadcastReceiver);
    }


    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, REQUEST_SMS);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String text = "Pozwolenie zaakceptowane, możesz teraz wysyłać smsy z tej aplikacji.";
                    SpannableStringBuilder biggerText = new SpannableStringBuilder(text);
                    biggerText.setSpan(new RelativeSizeSpan(1.35f), 0, text.length(), 0);
                    Toast.makeText(getApplicationContext(), biggerText, Toast.LENGTH_SHORT).show();
                    sendMySMSToReceiver();

                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(SMSActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumberEditText.setText(number);
            }
        }
    }

}

