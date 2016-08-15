package info.androidhive.materialtabs.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.materialtabs.R;

public class ClaimResaultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_resault);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Результат");
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        String response = b.getString("response");

        TextView result_string = (TextView) findViewById(R.id.result_label);
        TextView phone = (TextView) findViewById(R.id.phone_label);
        TextView claim = (TextView) findViewById(R.id.claim_label);

        Button back_button = (Button) findViewById(R.id.return_result_button);
        back_button.setText("ПОВЕРНУТИСЯ");

        if (response.equalsIgnoreCase("IO Error")) {
            result_string.setText("НЕ ЗАРЕЄСТРОВАНО");
            claim.setText("Дані не були відправлені на сервер!");
            phone.setText("Спробуйте піздніше");
            back_button.setBackgroundColor(0xffC91717);
        } else {
            JSONObject jObject;
            String claim_id_descr = "НЕ ЗАРЕЄСТРОВАНО";
            String phone_descr = "Помилка у введених даних!";

            try {
                jObject        = new JSONObject(response);
                claim_id_descr = jObject.getString("claim_id");
                phone_descr    = jObject.getString("phone");
            } catch (JSONException e) { }

            if (!claim_id_descr.isEmpty() && !phone_descr.isEmpty()) {
                result_string.setText("ЗАРЕЄСТРОВАНО");
                claim.setText("Номер звернення: " + claim_id_descr);
                phone.setText("Телефон: " + phone_descr);

                back_button.setBackgroundColor(0xff009688);
            } else {
                back_button.setBackgroundColor(0xffC91717);
            }
        }

        back_button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
