package com.example.pica.zmap;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
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
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.GregorianCalendar;

public class Servicio extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private GoogleApiClient cliente;
    protected static LocationRequest peticionLocalizaciones;
    private final int CTEPLAY = 1;
    private BaseDatos db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        db = intent.getParcelableExtra("db");
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
//                GooglePlayServicesUtil.getErrorDialog(status, this, CTEPLAY).show();
            } else {
                Toast.makeText(this, "No", Toast.LENGTH_LONG).show();
            }
        }


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        db.store(new Posicion(la, lo, new GregorianCalendar()));

        ObjectSet<Posicion> posiciones = db.query();

        while(posiciones.hasNext()){
            Posicion loc = posiciones.next();
            Log.v("MIAPP servicio", loc.toString());
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

}
