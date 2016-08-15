package info.androidhive.materialtabs.fragments;

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


    private EditText lastname_settings;
    private EditText phone_number_settings;

    private RadioButton GPSbutton;
    private RadioButton INTERNETbutton;

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
        View root_view = inflater.inflate(R.layout.fragment_one, container, false);

        lastname_settings     = (EditText) root_view.findViewById(R.id.lastname_settings);
        phone_number_settings = (EditText) root_view.findViewById(R.id.phone_number_settings);

        GPSbutton             = (RadioButton) root_view.findViewById(R.id.radio_button_gps);
        INTERNETbutton        = (RadioButton) root_view.findViewById(R.id.radio_button_internet);

        final ListView claims_list = (ListView) root_view.findViewById(R.id.old_claims_list);

        RadioButton.OnClickListener provider_listener = new RadioButton.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onRadioButtonClick(arg0);
            }
        };
        GPSbutton.setOnClickListener(provider_listener);
        INTERNETbutton.setOnClickListener(provider_listener);

        Button save_button = (Button) root_view.findViewById(R.id.save_button);

        Button.OnClickListener save_listener = new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor editor = Settings.edit();
                editor.putString(APP_PREFERENCES_LASTNAME, lastname_settings.getText().toString()).apply();
                editor.putString(APP_PREFERENCES_PHONE, phone_number_settings.getText().toString()).apply();



                Toast.makeText(getContext(),
                        "Налаштування збережено", Toast.LENGTH_SHORT).show();
            }
        };
        save_button.setOnClickListener(save_listener);

        Settings = this.getActivity().getSharedPreferences(APP_PREFERENCES, getActivity().MODE_PRIVATE);
        if (Settings.contains(APP_PREFERENCES_LASTNAME)) {
            lastname_settings.setText(Settings.getString(APP_PREFERENCES_LASTNAME, ""));
        }
        if (Settings.contains(APP_PREFERENCES_PHONE)) {
            phone_number_settings.setText(Settings.getString(APP_PREFERENCES_PHONE, ""));
        }
        if (Settings.contains(APP_PREFERENCES_COORDINATES)) {
            switch (Settings.getString(APP_PREFERENCES_COORDINATES, "")) {
                case "internet" : INTERNETbutton.setChecked(true); break;
                case "gps"      : GPSbutton.setChecked(true); break;
                default         : INTERNETbutton.setChecked(true); break;

            }
        } else {
            INTERNETbutton.setChecked(true);
            SharedPreferences.Editor editor = Settings.edit();
            editor.putString(APP_PREFERENCES_COORDINATES, "internet").apply();
        }

        final List <Claim> claims = Claim.listAll(Claim.class);
        final ArrayList<String> title_list = new ArrayList <String> ();
        for (int i = 0; i < claims.size(); i++)
            title_list.add("Заява №" + claims.get(i).claim_id);
        if (title_list.isEmpty()) {
            TextView history_label = (TextView) root_view.findViewById(R.id.old_claims);
            history_label.setText("");
        }


        final ArrayAdapter <String> adapter;
        adapter = new ArrayAdapter <String> (getContext(), android.R.layout.simple_list_item_1, title_list);
        claims_list.setAdapter(adapter);

        claims_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> parent, View itemClicked, int position,
                                    long id) {
                TextView textView = (TextView) itemClicked;
                String claim_id = textView.getText().toString(); // получаем текст нажатого элемента
                claim_id = claim_id.replace("Заява №", "");

                Intent observe = new Intent(getActivity(), ObserveActivity.class);
                observe.putExtra("claim_id", claim_id);
                startActivity(observe);
            }
        });

        return root_view;
    }

    public void onRadioButtonClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        SharedPreferences.Editor editor = Settings.edit();

        switch(view.getId()) {
            case R.id.radio_button_gps:
                if (checked)
                    editor.putString(APP_PREFERENCES_COORDINATES, "gps").apply();
                    break;
            case R.id.radio_button_internet:
                if (checked)
                    editor.putString(APP_PREFERENCES_COORDINATES, "internet").apply();
                    break;
        }
    }
}
