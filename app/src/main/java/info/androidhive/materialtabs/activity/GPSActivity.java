package info.androidhive.materialtabs.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.fragments.OneFragment;

public class GPSActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    private Location location;

    public static final String fetchedLatitude  = "latitude";
    public static final String fetchedLongitude = "longitude";

    TextView latitude;
    TextView longitude;

    ImageView mapField;
    Button backButton;

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

        mapField =     (ImageView) findViewById(R.id.map_image);
        backButton =   (Button)    findViewById(R.id.back_button);
        latitude =      (TextView)  findViewById(R.id.latitude_field);
        longitude =     (TextView)  findViewById(R.id.longitude_field);

        final Intent intent = new Intent();

        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (location != null) {
                    setResult(AppCompatActivity.RESULT_OK, intent);
                    intent.putExtra(fetchedLatitude, location.getLatitude());
                    intent.putExtra(fetchedLongitude, location.getLongitude());
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


        SharedPreferences settings = getSharedPreferences(OneFragment.APP_PREFERENCES, MODE_PRIVATE);
        if (settings.contains(OneFragment.APP_PREFERENCES_COORDINATES) &&                           // If settings exists for coordinates
            settings.getString(OneFragment.APP_PREFERENCES_COORDINATES, "") == "gps") {                            // And if it configured for gps APPPREFERENCESCOORDINATES = FALSE)
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(this, "Джерело інформації: GPS", Toast.LENGTH_SHORT).show();
        } else {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);          // Set default coordinates provider - Network
            Toast.makeText(this, "Джерело інформації: Інтернет", Toast.LENGTH_SHORT).show();
        }
        //                  Setting up screen

        startupMap();
        updateLocationFields();
    }

    private void startupMap() {
        Picasso.with(getBaseContext())
                .load("http://icons.iconarchive.com/icons/flat-icons.com/flat/512/Satellite-icon.png")
                .error(R.drawable.ic_gps_fixed)
                .into(mapField);
        scaleDown = ObjectAnimator.ofPropertyValuesHolder(mapField,
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
                    .load("https://maps.googleapis.com/maps/api/staticmap?center=" +
                            location.getLatitude() + "," + location.getLongitude() +
                            "&zoom=17&size=" + metrics.heightPixels + "x" + metrics.widthPixels +
                            "&markers=color:red%7C" + location.getLatitude() + "," +
                            location.getLongitude() + "&scale=2&language='ua'")
                    .error(R.drawable.ic_error)
                    .into(mapField);
        }
    }
}
