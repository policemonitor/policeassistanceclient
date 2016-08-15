package info.androidhive.materialtabs.database;

import com.orm.SugarRecord;

public class Claim  extends SugarRecord {
    String lastname;
    String phone_number;
    String theme;
    double latitude;
    double longitude;
    String text;

    public Claim(){ }

    public Claim(
            String lastname,
            String phone_number,
            String theme,
            double latitude,
            double longitude,
            String text) {
        this.lastname     = lastname;
        this.phone_number = phone_number;
        this.theme        = theme;
        this.latitude     = latitude;
        this.longitude    = longitude;
        this.text         = text;
    }
}
