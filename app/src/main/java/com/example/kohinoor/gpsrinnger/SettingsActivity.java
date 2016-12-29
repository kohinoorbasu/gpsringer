package com.example.kohinoor.gpsrinnger;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kohinoor.gpsrinnger.model.LocationGPSModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback, LocationListener{

    private AlertDialog _dialog = null;
    private final Context _context = this;
    private LocationManager _locationManager = null;
    private static final String FILE_NAME = "gpsringer.dat";

    private void getCurrentGeoCoord()
    {
        _locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        int result = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void writeToFile(ArrayList<LocationGPSModel> data)
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try
        {
            fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (oos != null)
                    oos.close();

                if (fos != null)
                    fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<LocationGPSModel> readFromFile()
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try
        {
            fis = openFileInput(FILE_NAME);
            ois = new ObjectInputStream(fis);
            return (ArrayList<LocationGPSModel>) ois.readObject();
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
        catch (IOException e)
        {
            // do nothing for now
            return null;
        }
        catch (ClassNotFoundException e)
        {
            // do nothing for now
            return null;
        }
    }

    private void saveData(String loc, String lat, String lon, int vol)
    {
        ArrayList<LocationGPSModel> data = null;

        //  Read from file
        data = readFromFile();

        //  if no data then create the object
        if (data == null)
            data = new ArrayList<>();

        LocationGPSModel row = new LocationGPSModel();
        row.set_Location(loc);
        row.set_Lat(lat);
        row.set_Lon(lon);
        row.set_Vol(vol);
        data.add(row);

        //  write the data to file
        writeToFile(data);
    }

    private void btnAddSetting_onClick(View view) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle("Add Location");

        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.content_setting_modal, null);

        // 3. Get the AlertDialog from create()
        builder.setView(promptsView);

        // 4.  Add the buttons
        builder.setPositiveButton(R.string.ok, null);
        builder.setNegativeButton(R.string.cancel, null);

        // 5.  Create Dialog
        _dialog = builder.create();

        _dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dlg) {

                //  setup vol slider
                setupAudioSlider();

                Button button = ((AlertDialog) dlg).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        TextView loc = (TextView)((AlertDialog) dlg).findViewById(R.id.edtTxtLoc);
                        if (loc.getText().toString().trim().length() == 0)
                        {
                            Toast.makeText(getApplicationContext(), "Location is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        TextView lon = (TextView)((AlertDialog) dlg).findViewById(R.id.edtTxtLon);
                        if (lon.getText().toString().trim().length() == 0)
                        {
                            Toast.makeText(getApplicationContext(), "Longitude is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        TextView lat = (TextView)((AlertDialog) dlg).findViewById(R.id.edtTxtLat);
                        if (lat.getText().toString().trim().length() == 0)
                        {
                            Toast.makeText(getApplicationContext(), "Latitude is required", Toast.LENGTH_LONG).show();
                            return;
                        }

                        SeekBar volControl = (SeekBar)_dialog.findViewById(R.id.slideVolBar);

                        //  Save the record in LocationGPSModel
                        saveData(loc.getText().toString(), lat.getText().toString(), lon.getText().toString(), volControl.getProgress());

                        dlg.dismiss();
                    }
                });
            }
        });

        _dialog.setCanceledOnTouchOutside(false);
        _dialog.setCancelable(false);
        _dialog.show();
    }

    private void setupAudioSlider()
    {
        final AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final SeekBar volControl = (SeekBar)_dialog.findViewById(R.id.slideVolBar);
        final TextView volLabel = (TextView)_dialog.findViewById(R.id.txtVol);
        volControl.setMax(maxVolume);
        volControl.setProgress(curVolume);
        volLabel.setText("Vol (" + curVolume + ")");
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                volLabel.setText("Vol (" + arg1 + ")");
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAddSetting);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddSetting_onClick(view);
            }
        });


    }

    public void btnCurrentLocation_onClick(View view){
        getCurrentGeoCoord();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        TextView t = (TextView)((AlertDialog) _dialog).findViewById(R.id.edtTxtLat);
        t.setText(String.format("%.4f", location.getLatitude()));

        t = (TextView)((AlertDialog) _dialog).findViewById(R.id.edtTxtLon);
        t.setText(String.format("%.4f", location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
