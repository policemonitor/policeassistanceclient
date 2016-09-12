package info.androidhive.materialtabs.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import info.androidhive.materialtabs.R;

public class ClaimResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_claim_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Результат");
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();

        TextView resultString = (TextView) findViewById(R.id.result_label);
        TextView phone = (TextView) findViewById(R.id.phone_label);
        TextView claim = (TextView) findViewById(R.id.claim_label);

        resultString.setText(b.getString("result"));
        claim.setText(b.getString("claim"));
        phone.setText(b.getString("phone"));

        Button backButton = (Button) findViewById(R.id.return_result_button);
        backButton.setBackgroundColor(b.getInt("buttonColor"));
        backButton.setText("ПОВЕРНУТИСЯ");

        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}
