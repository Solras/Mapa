package com.example.pica.zmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.config.AndroidSupport;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.query.Query;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.GregorianCalendar;

public class MapsActivity extends AppCompatActivity implements  OnMapReadyCallback,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                com.google.android.gms.location.LocationListener {


    private GoogleApiClient cliente;
    private LocationRequest peticionLocalizaciones;
    private final int CTEPLAY = 1;
    private GoogleMap mMap;
    private BaseDatos db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ObjectSet<Posicion> posiciones = db.query();

                while(posiciones.hasNext()){
                    Posicion p = posiciones.next();
                    LatLng location = new LatLng(p.getLatitud(),p.getLongitud());
                    mMap.addCircle(new CircleOptions().center(location).radius(1));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));

                    Log.v("MIAPP", p.toString());
                }
            }
        });
        init();
        Intent i = new Intent(this, Servicio.class);
        i.putExtra("db", db);
        startService(i);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CTEPLAY) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void init() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status == ConnectionResult.SUCCESS) {
            cliente = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            cliente.connect();

        } else {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }
        db = new BaseDatos(this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        peticionLocalizaciones = new LocationRequest();
        peticionLocalizaciones.setInterval(10000);
        peticionLocalizaciones.setFastestInterval(5000);
        peticionLocalizaciones.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(cliente, peticionLocalizaciones, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double la=location.getLatitude();
        double lo=location.getLongitude();
        Posicion p = new Posicion(la, lo, new GregorianCalendar());
        db.store(p);
//        LatLng myLocation = new LatLng(la,lo);
//        mMap.addCircle(new CircleOptions().center(myLocation).radius(1));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));

        ObjectSet<Posicion> posiciones = db.query();

        while(posiciones.hasNext()){
            Posicion loc = posiciones.next();
            Log.v("MIAPP", loc.toString());
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
