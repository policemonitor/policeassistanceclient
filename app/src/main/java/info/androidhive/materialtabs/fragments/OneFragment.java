package info.androidhive.materialtabs.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.ObserveActivity;
import info.androidhive.materialtabs.database.Claim;


public class OneFragment extends Fragment{

    public static final String APP_PREFERENCES = "police.assistance.settings";
    public static final String APP_PREFERENCES_LASTNAME = "lastname";
    public static final String APP_PREFERENCES_PHONE = "phone";
    public static final String APP_PREFERENCES_COORDINATES = "coordinate";

    private SharedPreferences Settings;


    private EditText lastnameSettings;
    private EditText phoneNumberSettings;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        lastnameSettings           = (EditText) rootView.findViewById(R.id.lastname_settings);
        phoneNumberSettings        = (EditText) rootView.findViewById(R.id.phone_number_settings);

        RadioButton GPSbutton      = (RadioButton) rootView.findViewById(R.id.radio_button_gps);
        RadioButton INTERNETbutton = (RadioButton) rootView.findViewById(R.id.radio_button_gps);

        final ListView claimsList = (ListView) rootView.findViewById(R.id.old_claims_list);

        RadioButton.OnClickListener providerListener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onRadioButtonClick(arg0);
            }
        };
        GPSbutton.setOnClickListener(providerListener);
        INTERNETbutton.setOnClickListener(providerListener);

        Button saveButton = (Button) rootView.findViewById(R.id.save_button);

        Button.OnClickListener saveListener = new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor editor = Settings.edit();
                editor.putString(APP_PREFERENCES_LASTNAME, lastnameSettings.getText().toString()).apply();
                editor.putString(APP_PREFERENCES_PHONE, phoneNumberSettings.getText().toString()).apply();



                Toast.makeText(getContext(),
                        "Налаштування збережено", Toast.LENGTH_SHORT).show();
            }
        };
        saveButton.setOnClickListener(saveListener);

        Settings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (Settings.contains(APP_PREFERENCES_LASTNAME)) {
            lastnameSettings.setText(Settings.getString(APP_PREFERENCES_LASTNAME, ""));
        }
        if (Settings.contains(APP_PREFERENCES_PHONE)) {
            phoneNumberSettings.setText(Settings.getString(APP_PREFERENCES_PHONE, ""));
        }
        if (Settings.contains(APP_PREFERENCES_COORDINATES)) {
            switch (Settings.getString(APP_PREFERENCES_COORDINATES, "")) {
                case "internet" : INTERNETbutton.setChecked(true);  break;
                case "gps"      : GPSbutton.setChecked(true);       break;
                default         : INTERNETbutton.setChecked(true);  break;

            }
        } else {
            INTERNETbutton.setChecked(true);
            SharedPreferences.Editor editor = Settings.edit();
            editor.putString(APP_PREFERENCES_COORDINATES, "internet").apply();
        }

        final List <Claim> claims = Claim.listAll(Claim.class);
        final ArrayList titleList = new ArrayList();
        for (int i = 0; i < claims.size(); i++)
            titleList.add("Заява №" + claims.get(i).claim_id);
        if (titleList.isEmpty()) {
            TextView historyLabel = (TextView) rootView.findViewById(R.id.old_claims);
            historyLabel.setText("");
        }


        final ArrayAdapter adapter;
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, titleList);
        claimsList.setAdapter(adapter);

        claimsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String claimId = textView.getText().toString(); // получаем текст нажатого элемента
                claimId = claimId.replace("Заява №", "");

                Intent observe = new Intent(getActivity(), ObserveActivity.class);
                observe.putExtra("claimId", claimId);
                startActivity(observe);
            }
        });

        return rootView;
    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences.Editor editor = Settings.edit();

        switch(view.getId()) {
            case R.id.radio_button_gps:
                if (checked) {
                    editor.putString(APP_PREFERENCES_COORDINATES, "gps").apply();
                    break;
                }
            case R.id.radio_button_internet:
                if (checked)
                    editor.putString(APP_PREFERENCES_COORDINATES, "internet").apply();
                    break;
        }
    }
}
