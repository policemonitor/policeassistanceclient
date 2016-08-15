package info.androidhive.materialtabs.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import info.androidhive.materialtabs.R;

public class ThreeFragment extends Fragment{


    public static final String  POLICE      = "102";
    public static final String  TEST_NUMBER = "+380502424424";

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view =  inflater.inflate(R.layout.fragment_three, container, false);
        ImageButton emergency_button = (ImageButton)root_view.findViewById(R.id.imageButtonMakeCall);

        ImageButton.OnClickListener listener = new ImageButton.OnClickListener() {
            @Override
            public void onClick(View arg0)
            {
                onMakeCallButtonClick();
            }
        };
        emergency_button.setOnClickListener(listener);
        return root_view;
    }
    public void onMakeCallButtonClick() {
        Intent make_call = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", TEST_NUMBER, null));
        try {
            Toast.makeText(getContext(),
                    "Викликаємо поліцію", Toast.LENGTH_SHORT).show();
            startActivity(make_call);
        }

        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getContext(),
                    "Неможливо виконати виклик", Toast.LENGTH_SHORT).show();
        }
    }
}