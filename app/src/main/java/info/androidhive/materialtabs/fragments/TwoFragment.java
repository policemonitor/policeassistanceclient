package info.androidhive.materialtabs.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.telephony.PhoneNumberUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.androidhive.materialtabs.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class TwoFragment extends Fragment {

    private double latitude = 0.0;
    private double longitude = 0.0;

    private EditText lastname_field;
    private EditText phone_number_field;
    private EditText theme_field;
    private EditText location_field;
    private EditText message_field;

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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button send_button = (Button) root_view.findViewById(R.id.send_button);
        ImageView location_button = (ImageView) root_view.findViewById(R.id.auto_location);

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
                            "Неможливо відправити! Нема Інтернет зв'язку!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (check_information()) {
                        getLatLongFromAddress(location_field.getText().toString());
                        Toast toast = Toast.makeText(getContext(),
                                "Відправляємо", Toast.LENGTH_SHORT);
                        toast.show();

                        String response = postClaim(
                                lastname_field.getText().toString(),
                                phone_number_field.getText().toString(),
                                theme_field.getText().toString(),
                                longitude,
                                latitude,
                                message_field.getText().toString()
                        );
                    }
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

                location_field.setEnabled(false);
            }
        };
        location_button.setOnClickListener(location_listener);

        return root_view;
    }

    private boolean check_information() {
        boolean check_list = true;

        final String lastname = lastname_field.getText().toString();
        if (!isValidLastname(lastname)) {
            lastname_field.setError("Неправильний формат!");
            check_list = false;
        }

        final String phone_number = phone_number_field.getText().toString();
        if (!isValidPhoneNumber(phone_number)) {
            phone_number_field.setError("Невірний формат номеру!");
            check_list = false;
        }

        final String theme = theme_field.getText().toString();
        if (!isValidMessage(theme)) {
            theme_field.setError("Порожнє поле!");
            check_list = false;
        }

        final String location = location_field.getText().toString();
        if (!isValidLocation(location)) {
            location_field.setError("Порожнє поле!");
            check_list = false;
        }

        final String message = message_field.getText().toString();
        if (!isValidMessage(message)) {
            message_field.setError("Порожнє поле!");
            check_list = false;
        }

        return check_list;
    }

    private boolean isValidLastname(String lastname) {
        String EMAIL_PATTERN = "^([A-ZА-Яa-zа-яІіЬыЫьъЪїЇҐґ]+[,.]?[ ]?|[a-z]+['-]?)+$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(lastname);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phone_number) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phone_number) && phone_number.length() > 6;
    }

    private boolean isValidLocation(String location) {
        return location.length() != 0;
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

    private String postClaim(String lastname,
                             String phone,
                             String theme,
                             double lon,
                             double lat,
                             String message)  {

        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        JSONObject mainObject = new JSONObject();
        JSONObject requestObject = new JSONObject();

        try {
            requestObject.put("lastname", lastname);
            requestObject.put("phone", phone);
            requestObject.put("latitude", lat);
            requestObject.put("longitude", lon);
            requestObject.put("theme", theme);
            requestObject.put("text", message);
            mainObject.put("claim", requestObject);
        } catch (JSONException e) {
            return "JSON Error";
        }

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, mainObject.toString());
        String url = "http://192.168.15.86:3000/API";
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("Accept", "application/json")
                .url(url)
                .post(body)
                .build();

        String response_string;
        try {
            okhttp3.Response response = client.newCall(request).execute();
            response_string = response.body().string();
        } catch (IOException e) {
            response_string = "IO Error" + e.getMessage() + mainObject.toString();
        }
        return response_string;
    }

    private void getLatLongFromAddress(String address)
    {
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try
        {
            List<Address> addresses = geoCoder.getFromLocationName(address , 1);
            if (addresses.size() > 0)
            {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            }
        }
        catch(Exception e)
        {
            Toast  toast = Toast.makeText(getContext(),
                    "Адреса не визначена", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}

