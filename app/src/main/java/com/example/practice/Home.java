package com.example.practice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.actions.NoteIntents;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class Home extends AppCompatActivity {

    private ImageButton camera;
    private ImageButton map;
    private ImageButton note;
    MediaPlayer mediaPlayer;
    public void playsound() {
        if(mediaPlayer != null){
            mediaPlayer.start();
        }
    }

    MediaPlayer mediaPlayer2;
    public void playsound2() {
        if(mediaPlayer2 != null){
            mediaPlayer2.start();
        }
    }

    MediaPlayer mediaPlayer3;
    public void playsound3() {
        if(mediaPlayer3 != null){
            mediaPlayer3.start();
        }
    }


    static final int Req_Image_capture = 1;
    private static final int PERMISSION_REQUEST_CODE = 123;


    private void openCurrentLocationInMap() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        // Google Maps app is not installed, handle this case
                        Toast.makeText(Home.this, "Google Maps app is not installed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now proceed to get the location
                openCurrentLocationInMap();
            } else {
                // Permission denied, handle this case
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        camera = findViewById(R.id.camera);
        map = findViewById(R.id.map);
        note = findViewById(R.id.note);
        mediaPlayer = MediaPlayer.create(this, R.raw.camera);
        mediaPlayer2 = MediaPlayer.create(this, R.raw.map);
        mediaPlayer3 = MediaPlayer.create(this, R.raw.note);


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                playsound();
                try{
                    startActivityForResult(intent, Req_Image_capture);
                }
                catch (ActivityNotFoundException e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                    builder.setTitle("Error").setMessage("An error occured: " + e.getMessage()).setPositiveButton("Ok", null).show();
                }
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playsound2();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                        openCurrentLocationInMap();
                    }
                    else{
                        openCurrentLocationInMap();
                    }
                }

            }
        });

        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playsound3();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(Home.this, "No note-taking app available", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}