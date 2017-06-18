package info.androidhive.materialtabs.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;
import android.telephony.PhoneNumberUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.ClaimResultActivity;
import info.androidhive.materialtabs.activity.GPSActivity;
import info.androidhive.materialtabs.database.Claim;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class TwoFragment extends Fragment {
    private final String urlAddress = "http://192.168.1.14:3000";

    private SharedPreferences Settings;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private boolean isCoordinatesReceived = false;

    private EditText lastnameField;
    private EditText phoneNumberField;
    private EditText themeField;
    private EditText locationField;
    private EditText messageField;

    private ImageView locationButton;

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
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button sendButton = (Button) rootView.findViewById(R.id.send_button);
        locationButton     = (ImageView) rootView.findViewById(R.id.auto_location);
        ImageView brushButton = (ImageView) rootView.findViewById(R.id.brush_button);

        lastnameField      = (EditText) rootView.findViewById(R.id.lastname);
        phoneNumberField   = (EditText) rootView.findViewById(R.id.phone_number);
        themeField         = (EditText) rootView.findViewById(R.id.theme);
        locationField      = (EditText) rootView.findViewById(R.id.location);
        messageField       = (EditText) rootView.findViewById(R.id.message);

        TextView lastnameLabel = (TextView) rootView.findViewById(R.id.label_lastname);
        TextView phoneNumberLabel = (TextView) rootView.findViewById(R.id.label_phone_number);

        Settings = this.getActivity().getSharedPreferences(OneFragment.APP_PREFERENCES,
                                                            Context.MODE_PRIVATE);

        Button.OnClickListener listener = new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!isOnline()) {
                    Toast toast = Toast.makeText(getContext(),
                            "Неможливо відправити! Нема Інтернет зв'язку!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    if (checkInformation()) {
                        getLatLongFromAddress(locationField.getText().toString());
                        Toast toast = Toast.makeText(getContext(),
                                "Відправляємо", Toast.LENGTH_SHORT);
                        toast.show();

                        String response = postClaim(
                                lastnameField.getText().toString(),
                                phoneNumberField.getText().toString(),
                                themeField.getText().toString(),
                                latitude,
                                longitude,
                                messageField.getText().toString()
                        );

                        Intent resultScreen = new Intent(getActivity(), ClaimResultActivity.class);

                        if (response.equalsIgnoreCase("IO Error")) {
                            resultScreen.putExtra("result","ПОМИЛКА");
                            resultScreen.putExtra("claim", "Сервер тимчасово недоступний!");
                            resultScreen.putExtra("phone", "Спробуйте пізніше");
                            resultScreen.putExtra("buttonColor", 0xffC91717);
                        } else {
                            JSONObject jObject;
                            int claimId = 0;
                            String phoneDescr = "";

                            try {
                                jObject        = new JSONObject(response);
                                claimId       = jObject.getInt("claim_id");
                                phoneDescr    = jObject.getString("phone");
                            } catch (JSONException ignored) { }

                            resultScreen.putExtra("result","ЗАРЕЄСТРОВАНО");
                            resultScreen.putExtra("claim", "Заява №" + claimId);
                            resultScreen.putExtra("phone", "Телефон: " + phoneDescr);
                            resultScreen.putExtra("buttonColor", 0xFF009688);

                            Claim claim = new Claim(
                                    claimId,
                                    lastnameField.getText().toString(),
                                    phoneNumberField.getText().toString(),
                                    themeField.getText().toString(),
                                    latitude,
                                    longitude,
                                    messageField.getText().toString());
                            claim.save();
                        }


                        startActivity(resultScreen);
                        resetForm();
                    }
                }
            }
        };
        sendButton.setOnClickListener(listener);

        ImageView.OnClickListener locationListener = new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!locationField.getText().toString().isEmpty()) {
                    getLatLongFromAddress(locationField.toString());
                } else {
                    Intent intent = new Intent(getActivity(), GPSActivity.class);
                    startActivityForResult(intent, 1);
                }
            }
        };
        locationButton.setOnClickListener(locationListener);

        ImageView.OnClickListener brushListener =  new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetForm();
                Toast.makeText(getContext(), "Форму очищено", Toast.LENGTH_SHORT).show();
            }
        };
        brushButton.setOnClickListener(brushListener);

        TextView.OnClickListener lastnameListener = new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Settings.contains(OneFragment.APP_PREFERENCES_LASTNAME)) {
                    lastnameField.setText(Settings.getString(OneFragment.APP_PREFERENCES_LASTNAME, ""));
                }
            }
        };
        lastnameLabel.setOnClickListener(lastnameListener);

        TextView.OnClickListener phoneNumberListener = new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (Settings.contains(OneFragment.APP_PREFERENCES_PHONE)) {
                    phoneNumberField.setText(Settings.getString(OneFragment.APP_PREFERENCES_PHONE, ""));
                }
            }
        };
        phoneNumberLabel.setOnClickListener(phoneNumberListener);

        return rootView;
    }

    /*                                 VALIDATION BLOCK                                           */

    private boolean checkInformation() {                       // Complex validation for whole form
        boolean checkList = true;

        final String lastname = lastnameField.getText().toString();
        if (!isValidLastname(lastname)) {
            lastnameField.setError("Неправильний формат!");
            checkList = false;
        }

        final String phoneNumber = phoneNumberField.getText().toString();
        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberField.setError("Невірний формат номеру!");
            checkList = false;
        }

        final String theme = themeField.getText().toString();
        if (!isValidMessage(theme)) {
            themeField.setError("Порожнє поле!");
            checkList = false;
        }

        if (!isValidLocation()) {
            locationField.setError("Місцеположення не визначено!");
            checkList = false;
        }

        final String message = messageField.getText().toString();
        if (!isValidMessage(message)) {
            messageField.setError("Порожнє поле!");
            checkList = false;
        }

        return checkList;
    }

    private boolean isValidLastname(String lastname) {  // Checking lastname with pattern and ukrainian, russian and english characters
        String NAME_PATTERN = "^([A-ZА-Яa-zа-яІіЬыЫьъЪїЇҐґ]+[,.]?[ ]?|[a-z]+['-]?)+$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(lastname);
        return matcher.matches() && lastname.length() >= 5;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {   // Validation phone field with international format
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber) && phoneNumber.length() > 12;
    }

    private boolean isValidLocation() {      // Checking location field
        return isCoordinatesReceived;
    }

    private boolean isValidMessage(String message) {    // Checking theme and message (mustn't be empty)
        return message.length() != 0;
    }

    public boolean isOnline() {                         // Checking connectivity to Internet
        ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void resetForm() {
        lastnameField.getText().clear();
        lastnameField.setError(null);

        phoneNumberField.getText().clear();
        phoneNumberField.setText("+380");
        phoneNumberField.setError(null);

        themeField.getText().clear();
        themeField.setError(null);

        locationField.getText().clear();
        locationField.setError(null);
        locationField.setEnabled(true);

        messageField.getText().clear();
        messageField.setError(null);

        latitude = 0.0;
        longitude = 0.0;

        isCoordinatesReceived = false;

        locationButton.setBackgroundResource(R.color.listen_location_button_color);
    }

    /*                                   DATA MANAGEMENT BLOCK                                    */

    private String postClaim(String lastname,
                             String phone,
                             String theme,
                             double lat,
                             double lon,
                             String message)  {         // Sending claim to server

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
        String url = urlAddress + "/api/new_claim";
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("Accept", "application/json")
                .url(url)
                .post(body)
                .build();

        String responseString;
        try {
            okhttp3.Response response = client.newCall(request).execute();
            responseString = response.body().string();
        } catch (IOException e) {
            responseString = "IO Error";
        }
        return responseString;
    }

    /*                                   GEO LOCATION SERVICES                                    */

    private void getLatLongFromAddress(String address)  {   // Translating street name to coordinates
        Locale locale = new Locale("ukUA");
        Locale.setDefault(locale);
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(address , 1);
            if (addresses.size() > 0) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            }
        }
        catch(Exception e) {
            Toast.makeText(getContext(), "Помилка з'єднання з сервісом Google Geocoder",
                    Toast.LENGTH_SHORT).show();
        }

        if (longitude != 0.0 && latitude != 0.0) {
            isCoordinatesReceived = true;
            locationButton.setBackgroundResource(R.color.success_location_button_color);
            locationField.setError(null);
        } else {
            Toast.makeText(getContext(), "Не вдалося визначити адресу!", Toast.LENGTH_SHORT).show();
            locationButton.setBackgroundResource(R.color.failed_location_button_color);
        }

    }

    public static Address getAddress(final Context context,
                                    final double latitude,
                                    final double longitude) {
        if (latitude == 0d || longitude == 0d) {
            return null;
        }

        final Geocoder geocoder = new Geocoder(context);
        final List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
        } catch (IOException e) {
            return null;
        }
        if (addresses != null && !addresses.isEmpty()) {
            return addresses.get(0);
        } else {
            return null;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getContext(), "Місцезнаходження визначено!", Toast.LENGTH_SHORT).show();

                longitude = data.getDoubleExtra(GPSActivity.fetchedLongitude, 0);
                latitude = data.getDoubleExtra(GPSActivity.fetchedLatitude, 0);

                locationButton.setBackgroundResource(R.color.success_location_button_color);
                Address locatedAddress = getAddress(getContext(),latitude, longitude);

                try {
                    locationField.setText(MessageFormat.format("{0}, {1}",
                            locatedAddress.getAddressLine(1),
                            locatedAddress.getAddressLine(0)));
                } catch (RuntimeException e) {
                    locationField.setText("Місцеположення встановлене");
                }



                locationField.setError(null);
                isCoordinatesReceived = true;
            } else {
                Toast.makeText(getContext(), "Місцезнаходження не визначено!", Toast.LENGTH_SHORT).show();
                locationButton.setBackgroundResource(R.color.failed_location_button_color);
            }
    }
}