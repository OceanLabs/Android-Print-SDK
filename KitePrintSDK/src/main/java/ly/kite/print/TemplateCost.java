package ly.kite.print;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by alibros on 07/01/15.
 */
public class TemplateCost implements Parcelable, Serializable {

    private String currency;
    private String amount;

    public TemplateCost(String currency, String amount) {
        this.currency = currency;
        this.amount = amount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}

