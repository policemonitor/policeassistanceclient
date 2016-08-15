package info.androidhive.materialtabs.database;

import com.orm.SugarRecord;

public class Claim  extends SugarRecord {
    public int    claim_id;
    public String lastname;
    public String phone_number;
    public String theme;
    public double latitude;
    public double longitude;
    public String text;

    public Claim(){ }

    public Claim(
            int    claim_id,
            String lastname,
            String phone_number,
            String theme,
            double latitude,
            double longitude,
            String text) {
        this.claim_id     = claim_id;
        this.lastname     = lastname;
        this.phone_number = phone_number;
        this.theme        = theme;
        this.latitude     = latitude;
        this.longitude    = longitude;
        this.text         = text;
    }
}
