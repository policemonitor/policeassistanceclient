package info.androidhive.materialtabs.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import info.androidhive.materialtabs.R;

public class GPSActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private Location location;

    public static final String fetched_latitude  = "latitude";
    public static final String fetched_longitude = "longitude";

    TextView latitude;
    TextView longitude;

    ImageView map_field;
    Button back_button;

    ObjectAnimator scaleDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //                  Initializing

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Місцеположення");
        toolbar.setNavigationIcon(R.drawable.ic_gps_fixed_inverted);
        setSupportActionBar(toolbar);

        map_field =     (ImageView) findViewById(R.id.map_image);
        back_button =   (Button)    findViewById(R.id.back_button);
        latitude =      (TextView)  findViewById(R.id.latitude_field);
        longitude =     (TextView)  findViewById(R.id.longitude_field);

        final Intent intent = new Intent();

        back_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (location != null) {
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    intent.putExtra(fetched_latitude, location.getLatitude());
                    intent.putExtra(fetched_longitude, location.getLongitude());
                } else {
                    setResult(AppCompatActivity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });

        //                  Setting up Location Manager

        locationManager     = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria   = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //                  Setting up screen

        startupMap();
        updateLocationFields();
    }

    private void startupMap() {
        Picasso.with(getBaseContext())
                .load("http://icons.iconarchive.com/icons/flat-icons.com/flat/512/Satellite-icon.png")
                .error(R.drawable.ic_gps_fixed)
                .into(map_field);
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(map_field,
                PropertyValuesHolder.ofFloat("scaleX", 0.95f),
                PropertyValuesHolder.ofFloat("scaleY", 0.95f));
        scaleDown.setDuration(1000);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        updateLocationFields();
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

    private void updateLocationFields() {
        if (location != null) {
            latitude.setText(Double.toString(location.getLatitude()));
            longitude.setText(Double.toString(location.getLongitude()));

            scaleDown.end();

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            Picasso.with(getBaseContext())
                    .load("https://maps.googleapis.com/maps/api/staticmap?center=" + location.getLatitude() + "," + location.getLongitude() + "&zoom=14&size=" + metrics.heightPixels + "x" + metrics.widthPixels)
                    .error(R.drawable.ic_error)
                    .into(map_field);
        }
    }
}
