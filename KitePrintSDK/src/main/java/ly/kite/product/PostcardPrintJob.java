package ly.kite.product;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by alibros on 16/01/15.
 */
class PostcardPrintJob extends PrintJob {

    private Asset frontImageAsset;
    private String message;
    private Address address;

    public PostcardPrintJob( Product product, Asset frontImageAsset, String message, Address address) {
        super( product );

        this.frontImageAsset = frontImageAsset;
        this.message = message;
        this.address = address;
    }

    @Override
    public BigDecimal getCost(String currencyCode) {
        return getProduct().getCost(currencyCode);
    }

    @Override
    public Set<String> getCurrenciesSupported() {
        return getProduct().getCurrenciesSupported();
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    List<Asset> getAssetsForUploading() {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(frontImageAsset);
        return assets;
    }

    private static String getStringOrEmptyString(String val) {
        return val == null ? "" : val;
    }

    private JSONObject getJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("template_id", getProductId() );

        JSONObject assets = new JSONObject();
        json.put("assets", assets);
        assets.put("photo", frontImageAsset.getId());

        JSONObject frameContents = new JSONObject();
        json.put("frame_contents", frameContents);

        // set message
        frameContents.put("frame1", new JSONObject("{\"paragraphs\":[{\"content\":\"15\", \"style\":\"spacer\"}, {\"content\":\"" + message + "\", \"style\":\"body\"}]}"));

        ArrayList<Pair<String, String> > addrComponents = new ArrayList<Pair<String, String>>();
        addrComponents.add(new Pair<String, String>(address.getRecipientName(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getLine1(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getLine2(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getCity(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getStateOrCounty(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getZipOrPostalCode(), "postcode-or-country"));
        addrComponents.add(new Pair<String, String>(address.getCountry().displayName(), "postcode-or-country"));

        int addressComponentId = 0;
        for (Pair<String, String> addrComponent : addrComponents) {
            if (addrComponent.first != null) {
                frameContents.put("addr" + (++addressComponentId), new JSONObject("{\"paragraphs\":[{\"content\":\"" + addrComponent.first.trim() + "\", \"style\":\"" + addrComponent.second + "\"}]}"));
            }
        }

        if (address != null) {
            JSONObject shippingAddr = new JSONObject();
            json.put("shipping_address", shippingAddr);

            shippingAddr.put("recipient_name", getStringOrEmptyString(address.getRecipientName()));
            shippingAddr.put("address_line_1", getStringOrEmptyString(address.getLine1()));
            shippingAddr.put("address_line_2", getStringOrEmptyString(address.getLine2()));
            shippingAddr.put("city", getStringOrEmptyString(address.getCity()));
            shippingAddr.put("county_state", getStringOrEmptyString(address.getStateOrCounty()));
            shippingAddr.put("postcode", getStringOrEmptyString(address.getZipOrPostalCode()));
            shippingAddr.put("country_code", getStringOrEmptyString(address.getCountry().iso3Code()));
        }

        return json;
    }

    @Override
    JSONObject getJSONRepresentation() {
        try {
            return getJSON();
        } catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel( parcel, flags );
        parcel.writeParcelable(frontImageAsset, flags);
        parcel.writeString(message);
        parcel.writeParcelable(address, flags);
    }

    private PostcardPrintJob(Parcel parcel) {
        //super( ProductCache.getDirtyInstance().getProductById( parcel.readString() ) );
        super( parcel );
        this.frontImageAsset = parcel.readParcelable(Asset.class.getClassLoader());
        this.message = parcel.readString();
        this.address = (Address)parcel.readParcelable(Address.class.getClassLoader());
    }

    public static final Parcelable.Creator<PostcardPrintJob> CREATOR
            = new Parcelable.Creator<PostcardPrintJob>() {
        public PostcardPrintJob createFromParcel(Parcel in) {
            return new PostcardPrintJob(in);
        }

        public PostcardPrintJob[] newArray(int size) {
            return new PostcardPrintJob[size];
        }
    };

}
