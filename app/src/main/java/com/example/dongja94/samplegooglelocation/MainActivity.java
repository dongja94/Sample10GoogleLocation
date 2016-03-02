package com.example.dongja94.samplegooglelocation;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mClient;
    TextView locationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        locationView = (TextView) findViewById(R.id.text_location);
        mClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (savedInstanceState != null) {
            isErrorProcessing = savedInstanceState.getBoolean(FIELD_ERROR_PROCESSING);
        }
    }

    private void displayLocation(Location location) {
        locationView.setText("lat : " + location.getLatitude() + ", lng :" + location.getLongitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLocation();
    }

    private static final int RC_PERMISSION = 1;
    private static final int RC_API_CLIENT = 2;

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, RC_PERMISSION);
            }
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (location != null) {
            displayLocation(location);
        }
        LocationRequest request = new LocationRequest();
        request.setFastestInterval(5000);
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient,request, mListener);
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            displayLocation(location);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_PERMISSION) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length > 0) {
            for (int code : grantResults) {
                if (code == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                    return;
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
//        LocationServices.FusedLocationApi.removeLocationUpdates(mClient, mListener);
    }

    private static final String FIELD_ERROR_PROCESSING = "errorProcessing";
    boolean isErrorProcessing = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (isErrorProcessing) return;
        isErrorProcessing = true;
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RC_API_CLIENT);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
                mClient.connect();
            }
        } else {
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CLIENT);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != RC_API_CLIENT) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        isErrorProcessing = false;
        if (resultCode == Activity.RESULT_OK) {
            mClient.connect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean(FIELD_ERROR_PROCESSING, isErrorProcessing);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
