package info.androidhive.materialtabs.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.telephony.PhoneNumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.androidhive.materialtabs.R;


public class TwoFragment extends Fragment {

    private double latitude = 0.0;
    private double longitude = 0.0;

    private EditText lastname_field;
    private EditText phone_number_field;
    private EditText theme_field;
    private EditText location_field;
    private EditText message_field;

    Button send_button;
    private ImageView location_button;


    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_two, container, false);

        send_button = (Button) root_view.findViewById(R.id.send_button);
        location_button = (ImageView) root_view.findViewById(R.id.auto_location);

        lastname_field = (EditText) root_view.findViewById(R.id.lastname);
        phone_number_field = (EditText) root_view.findViewById(R.id.phone_number);
        theme_field = (EditText) root_view.findViewById(R.id.theme);
        location_field = (EditText) root_view.findViewById(R.id.location);
        message_field = (EditText) root_view.findViewById(R.id.message);

        Button.OnClickListener listener = new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!isOnline()) {
                    Toast toast = Toast.makeText(getContext(),
                            "Неможливо відіслати!\nНема Інтернет зв'язку!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    collect_information();
                    Toast toast = Toast.makeText(getContext(),
                            "Відправляємо", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };
        send_button.setOnClickListener(listener);

        ImageView.OnClickListener location_listener = new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Toast toast = Toast.makeText(getContext(),
                        "Визначаємо місцезнаходження", Toast.LENGTH_SHORT);
                toast.show();
                /*
                * TODO:
                *   Request coordinates
                *   Transform coordinates from string
                * */
                toast = Toast.makeText(getContext(),
                        "Lat.: " + latitude + " Lon.: " + longitude, Toast.LENGTH_SHORT);
                toast.show();

                location_field.setEnabled(false);
            }
        };
        location_button.setOnClickListener(location_listener);

        return root_view;
    }

    private void collect_information() {
        final String lastname = lastname_field.getText().toString();
        if (!isValidLastname(lastname)) {
            lastname_field.setError("Неправильний формат!");
        }

        final String phone_number = phone_number_field.getText().toString();
        if (!isValidPhoneNumber(phone_number)) {
            phone_number_field.setError("Невірний формат номеру!");
        }

        final String theme = theme_field.getText().toString();
        if (!isValidTheme(theme)) {
            theme_field.setError("Неприпустимі символи!");
        }

        final String location = location_field.getText().toString();
        if (!isValidLocation(location)) {
            location_field.setError("Порожнє поле!");
        }

        final String message = message_field.getText().toString();
        if (!isValidMessage(message)) {
            location_field.setError("Порожнє поле!");
        }
    }

    private boolean isValidLastname(String lastname) {
        String EMAIL_PATTERN = "^([A-ZА-Яa-zа-яІіЬыЫьЬъЪїЇҐґ]+[,.]?[ ]?|[a-z]+['-]?)+$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(lastname);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phone_number) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phone_number);
    }

    private boolean isValidTheme(String theme) {
        return theme.length() != 0;
    }

    private boolean isValidLocation(String location) {
        return true;
    }

    private boolean isValidMessage(String message) {
        return message.length() != 0;
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
