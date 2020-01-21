package com.example.lexuswatchman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    final private int REQUEST_IMAGE_CAPTURE=1;
    final private int SPEAK_REQUEST=2;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int PERMISSION_REQUEST_CODE = 100;
    private ImageView imageView,iv_splash;
    private Button imagebt,sendbt,speakbt;
    private File fileis;
    private EditText nameed;
    private Bitmap bitmap;
    private Spinner spinnerbt;

    String[] namesis={"Prakash Saini","Dr. Utpal Mistry","Anurag","Aditya Jaiswal"};
    String[] numberis={"916354161728","915654654646","444894945464","444894945464"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_splash=(ImageView)findViewById(R.id.iv_splash);
        iv_splash.setBackgroundResource(R.drawable.splash);
        final AnimationDrawable progressAnimation =(AnimationDrawable)iv_splash.getBackground();
        progressAnimation.start();

        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                Toast.makeText(MainActivity.this, "Please give your permission.", Toast.LENGTH_LONG).show();
            }
        }

        if(checkPermissionwrite()){
            Toast.makeText(this,"Write on",Toast.LENGTH_LONG).show();
        }
        else requestPermission();

        sendbt=findViewById(R.id.sendit);
        imagebt=findViewById(R.id.imagebt);
        imageView =findViewById(R.id.imageView);
        speakbt=findViewById(R.id.speakbt);
        nameed=findViewById(R.id.nameed);
        spinnerbt=findViewById(R.id.spinner);


        speakbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getvoiceinput();
            }
        });


        sendbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp();
            }
        });
        imagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        spinnerbt.setOnItemSelectedListener(this);
        ArrayAdapter aa=new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item,namesis);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerbt.setAdapter(aa);

    }

    private void getvoiceinput(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please Speak something!");
        startActivityForResult(intent,SPEAK_REQUEST);
    }

    private void openWhatsApp() {

        String smsNumber = "916354161728";
        String text="Name of the Person:- ";

        String nameofthe=nameed.getText().toString();
        if(nameofthe.isEmpty()){
            nameed.setError("Please Enter the name");
            return;
        }
        text+=nameofthe;
        Log.i("Name is :- ",text);
        try{
           int ch=bitmap.getWidth();
       }
       catch (Exception w){
           Toast.makeText(this,"Please Take Image",LENGTH_LONG).show();
       }
        String pathofBmp=
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        bitmap,"title", null);

       try {
           Uri uri1 = Uri.parse(pathofBmp);
       }catch (Exception w){
           Toast.makeText(this,"Please Take Image",LENGTH_LONG).show();
           return;
       }
       Uri uri=Uri.parse(pathofBmp);



        if(whatsappInstalledOrNot("com.whatsapp")) {
           Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_SEND);
            intent.setPackage("com.whatsapp");
           // intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            intent.putExtra("GSM", smsNumber + "@s.whatsapp.net");
            intent.putExtra("chat",true);
            intent.setType("image/jpeg");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_TEXT,text);
            startActivity(intent);

        }
        else {
            Toast.makeText(MainActivity.this,"Whatsapp is not installed",LENGTH_LONG).show();
        }
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == RESULT_OK) {
            if(requestCode==REQUEST_IMAGE_CAPTURE){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                try {
                    storeImage(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(imageBitmap);
            }
            if(requestCode==SPEAK_REQUEST){
                if(data!=null){
                    ArrayList<String>result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    nameed.setText(result.get(0));

                }
            }

        }

    }

    private void storeImage(Bitmap image) throws IOException {
        File pictureFile = createImageFile();
        fileis=pictureFile;
        if (pictureFile == null) {
            Log.i("Error", "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            OutputStream stream = null;

            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            imageView.setImageBitmap(image);
            bitmap=image;
            fos.flush();
            fos.close();

        } catch (FileNotFoundException e) {
            Log.i("Error", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.i("Error", "Error accessing file: " + e.getMessage());
        }


    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpeg",
                storageDir
        );
        String currentPhotoPath = image.getAbsolutePath();
        Toast.makeText(this,currentPhotoPath,Toast.LENGTH_LONG).show();
        return image;
    }


    private boolean checkPermissionwrite() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                   // Toast.makeText(MainActivity.this, "Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Write Permission granted.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Write Please give your permission.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
