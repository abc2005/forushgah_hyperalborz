package com.persiandesigners.hyperalborz;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by navid on 10/3/2017.
 */
public class Maps extends AppCompatActivity implements GpsStatus.Listener {
    MapFragment googleMap;
    GoogleMap Map;
    Double lat = 0.0, lon;
    Boolean clicked;
    Typeface typeface;
    Button submit;
    private LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);

        declare();
        clicked = false;
        try {
            // Loading map
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            doGPS();
        }

    }


    private void submit() {
        if (clicked || lat != 0.0) {
            SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
            SharedPreferences.Editor pref = settings.edit();
            pref.putString("lat", lat + "");
            pref.putString("lon", lon + "");
            pref.commit();
            onBackPressed();
            finish();
            Log.v("this", lat + " gps " + lon);
        } else {
            MyToast.makeText(Maps.this, "موقعیت جغرافیایی مشخص نشده است");
        }
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            googleMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    Map=googleMap;


                    if(getString(R.string.lat).length()>2) {
                        LatLng sydney = new LatLng(Double.parseDouble(getString(R.string.lat)), Double.parseDouble(getString(R.string.lon)));
                        //Map.addMarker(new MarkerOptions().position(sydney).title(""));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                        Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }

                    if (googleMap == null) {
                        Toast.makeText(getApplicationContext(),
                                "نقشه روی گوشی شما قابل نمایش نیست", Toast.LENGTH_SHORT)
                                .show();
                    }

                    Map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            Map.clear();
                            Map.addMarker(new MarkerOptions().position(latLng));
                            lat = latLng.latitude;
                            lon = latLng.longitude;
                            clicked = true;
                        }
                    });
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
        if (locationManager != null)
            locationManager.addGpsStatusListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            doGPS();
        }
    }


    private void doGPS() {
        LocationManager mlocManager = null;
        LocationListener mlocListener;
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(Maps.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Maps.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {//gps is connected
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.addGpsStatusListener(this);
            startService();
        } else {
            android.support.v7.app.AlertDialog.Builder a = new android.support.v7.app.AlertDialog.Builder(Maps.this);
            a.setMessage(("جی پی اس خاموش است. آیا میخواهید روشن کنید؟"));
            a.setPositiveButton(("بله"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                    boolean enabled = service
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (!enabled) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
            a.setNegativeButton(("خیر"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            android.support.v7.app.AlertDialog dialog = a.show();
            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
            messageText.setGravity(Gravity.RIGHT);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doGPS();
                } else {
                    MyToast.makeText(Maps.this, "دسترسی به جی پی اس غیرفعال است");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                locationManager = (LocationManager) getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        500000, 500000, listener);
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                Log.v("this", "started");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.v("this", "stopped");
                break;
        }
    }

    private LocationListener listener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location == null)
                return;
//
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            Map.addMarker(new MarkerOptions().position(sydney).title(""));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(15).build();
            Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    };

    private void declare() {
        typeface = Typeface.createFromAsset(getAssets(), "IRAN Sans.ttf");

        submit = (Button) findViewById(R.id.submit);
        submit.setTypeface(typeface);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public boolean startService() {
        try {
            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();

        alert.setTitle(title);

        alert.setMessage(message);

        return alert;

    }

    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        AlertDialog.Builder a;
        AlertDialog dialog;

        public double lati = 0.0;
        public double longi = 0.0;

        public LocationManager mLocationManager;
        public VeggsterLocationListener mVeggsterLocationListener;

        @Override
        protected void onPreExecute() {
            mVeggsterLocationListener = new VeggsterLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    mVeggsterLocationListener);

//            a = new AlertDialog.Builder(Maps.this);
//            a.setMessage("در حال  به دست آوردن موقعیت جغرافیایی...");
//            a.setPositiveButton(("لغو"), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                    FetchCordinates.this.cancel(true);
//                }
//            });
//
//            dialog = a.show();
//            TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
//            messageText.setGravity(Gravity.RIGHT);
//            messageText.setTypeface(typeface);
        }

        @Override
        protected void onCancelled() {
            System.out.println("Cancelled by user!");
            dialog.dismiss();
            mLocationManager.removeUpdates(mVeggsterLocationListener);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if(dialog!=null)
                    dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            lat = lati;
            lon = longi;
            MyToast.makeText(Maps.this, "موقعیت شما با موفقیت ثبت شد");

            LatLng sydney = new LatLng(lat, lon);
            Map.addMarker(new MarkerOptions().position(sydney).title("موقعیت شما"));
            Map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 22));
            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
            Map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            while (this.lati == 0.0) {

            }
            return null;
        }

        public class VeggsterLocationListener implements LocationListener {

            @Override
            public void onLocationChanged(Location location) {

                int lat = (int) location.getLatitude(); // * 1E6);
                int log = (int) location.getLongitude(); // * 1E6);
                int acc = (int) (location.getAccuracy());

                String info = location.getProvider();
                try {

                    // LocatorService.myLatitude=location.getLatitude();

                    // LocatorService.myLongitude=location.getLongitude();

                    lati = location.getLatitude();
                    longi = location.getLongitude();

                } catch (Exception e) {
                    // progDailog.dismiss();
                    // Toast.makeText(getApplicationContext(),"Unable to get Location"
                    // , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.i("OnProviderDisabled", "OnProviderDisabled");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.i("onProviderEnabled", "onProviderEnabled");
            }

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                Log.i("onStatusChanged", "onStatusChanged");

            }

        }

    }


}
