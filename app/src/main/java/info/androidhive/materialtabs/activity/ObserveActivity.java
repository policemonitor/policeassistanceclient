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
        String claim_id = b.getString("claim_id");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Заява №" + claim_id);
        toolbar.setNavigationIcon(R.drawable.ic_save);
        setSupportActionBar(toolbar);

        TextView claim_header   = (TextView) findViewById(R.id.claim_number);
        TextView theme_text     = (TextView) findViewById(R.id.theme_text);
        TextView message_text   = (TextView) findViewById(R.id.message_text);

        TextView theme_label    = (TextView) findViewById(R.id.theme_label);
        TextView message_label  = (TextView) findViewById(R.id.message_label);

        try {
            List<Claim> claim_list = Claim.findWithQuery(Claim.class, "select * from Claim where claim_id = ?", claim_id);
            Claim claim = claim_list.get(0);

            ImageView map = (ImageView) findViewById(R.id.map_img);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);

            claim_header.setText("Заява №" + claim_id);
            theme_text.setText(claim.theme);
            message_text.setText(claim.text);

            Picasso.with(getBaseContext())
                    .load("https://maps.googleapis.com/maps/api/staticmap?center=" + claim.latitude +
                            "," + claim.longitude + "&zoom=19&size=" + metrics.heightPixels + "x" +
                            metrics.widthPixels + "&markers=color:red%7C" + claim.latitude +
                            "," + claim.longitude + "&scale=2&language='ua'")
                    .error(R.mipmap.ic_map)
                    .into(map);
        } catch (Exception e) {
            claim_header.setText("Помилка");
            theme_label.setText("Заяву не знайдено!");
            message_label.setText("");
        }
    }
}
