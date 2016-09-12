package info.androidhive.materialtabs.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.database.Claim;

public class ObserveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observe);

        Bundle b = getIntent().getExtras();
        String claimId = b.getString("claimId");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Заява №" + claimId);
        toolbar.setNavigationIcon(R.drawable.ic_save);
        setSupportActionBar(toolbar);

        TextView claimHeader   = (TextView) findViewById(R.id.claim_number);
        TextView themeText     = (TextView) findViewById(R.id.theme_text);
        TextView messageText   = (TextView) findViewById(R.id.message_text);

        TextView themeLabel    = (TextView) findViewById(R.id.theme_label);
        TextView messageLabel  = (TextView) findViewById(R.id.message_label);

        try {
            List<Claim> claimList = Claim.findWithQuery(Claim.class, "select * from Claim where claimid = ?", claimId);
            Claim claim = claimList.get(0);

            ImageView map = (ImageView) findViewById(R.id.map_img);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            claimHeader.setText(String.format("Заява №%s", claimId));
            themeText.setText(claim.theme);
            messageText.setText(claim.text);

            Picasso.with(getBaseContext())
                    .load("https://maps.googleapis.com/maps/api/staticmap?center=" + claim.latitude +
                            "," + claim.longitude + "&zoom=17&size=" + metrics.heightPixels + "x" +
                            metrics.widthPixels + "&markers=color:red%7C" + claim.latitude +
                            "," + claim.longitude + "&scale=2&language='ua'")
                    .error(R.mipmap.ic_map)
                    .into(map);
        } catch (Exception e) {
            claimHeader.setText("Помилка");
            themeLabel.setText("Заяву не знайдено!");
            messageLabel.setText("");
        }
    }
}
