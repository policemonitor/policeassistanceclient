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

import at.markushi.ui.CircleButton;
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
        return inflater.inflate(R.layout.fragment_three, container, false);

    }

    public void onClick(View view) {
        Intent make_call = new Intent(Intent.ACTION_CALL, Uri.parse(TEST_NUMBER));
        try{

            Toast.makeText(getActivity(), "Викликаємо поліцію", Toast.LENGTH_SHORT).show();
            startActivity(make_call);
        }

        catch (android.content.ActivityNotFoundException ex){
            Toast.makeText(getActivity(),"Виклик з данного пристрою неможливий!",Toast.LENGTH_SHORT).show();
        }
    }
}
