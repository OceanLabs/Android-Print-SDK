package ly.kite.catalogue;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by alibros on 16/01/15.
 */
class PostcardPrintJob extends PrintJob {

    private Asset    mFrontImageAsset;
    private String   mMessage;
    private Address  mAddress;

    public PostcardPrintJob( Product product, HashMap<String,String> optionMap, Asset frontImageAsset, String message, Address address) {
        super( product, optionMap );

        mFrontImageAsset = frontImageAsset;
        mMessage         = message;
        mAddress         = address;
    }

    public PostcardPrintJob( Product product, Asset frontImageAsset, String message, Address address )
        {
        this( product, null, frontImageAsset, message, address );
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
        assets.add( mFrontImageAsset );
        return assets;
    }

    private static String getStringOrEmptyString(String val) {
        return val == null ? "" : val;
    }

    private JSONObject getJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("template_id", getProductId() );

    addProductOptions( json );

    JSONObject assets = new JSONObject();
        json.put("assets", assets);
        assets.put( "photo", mFrontImageAsset.getId() );

        JSONObject frameContents = new JSONObject();
        json.put("frame_contents", frameContents);

        // set message
        frameContents.put("frame1", new JSONObject("{\"paragraphs\":[{\"content\":\"15\", \"style\":\"spacer\"}, {\"content\":\"" + mMessage + "\", \"style\":\"body\"}]}"));

        ArrayList<Pair<String, String> > addrComponents = new ArrayList<Pair<String, String>>();
        addrComponents.add(new Pair<String, String>(mAddress.getRecipientName(), "body-centered"));
        addrComponents.add(new Pair<String, String>(mAddress.getLine1(), "body-centered"));
        addrComponents.add(new Pair<String, String>(mAddress.getLine2(), "body-centered"));
        addrComponents.add(new Pair<String, String>(mAddress.getCity(), "body-centered"));
        addrComponents.add(new Pair<String, String>(mAddress.getStateOrCounty(), "body-centered"));
        addrComponents.add(new Pair<String, String>(mAddress.getZipOrPostalCode(), "postcode-or-country"));
        addrComponents.add(new Pair<String, String>(mAddress.getCountry().displayName(), "postcode-or-country"));

        int addressComponentId = 0;
        for (Pair<String, String> addrComponent : addrComponents) {
            if (addrComponent.first != null) {
                frameContents.put("addr" + (++addressComponentId), new JSONObject("{\"paragraphs\":[{\"content\":\"" + addrComponent.first.trim() + "\", \"style\":\"" + addrComponent.second + "\"}]}"));
            }
        }

        if (mAddress != null) {
            JSONObject shippingAddr = new JSONObject();
            json.put("shipping_address", shippingAddr);

            shippingAddr.put("recipient_name", getStringOrEmptyString(mAddress.getRecipientName()));
            shippingAddr.put("address_line_1", getStringOrEmptyString(mAddress.getLine1()));
            shippingAddr.put("address_line_2", getStringOrEmptyString(mAddress.getLine2()));
            shippingAddr.put("city", getStringOrEmptyString(mAddress.getCity()));
            shippingAddr.put("county_state", getStringOrEmptyString(mAddress.getStateOrCounty()));
            shippingAddr.put("postcode", getStringOrEmptyString(mAddress.getZipOrPostalCode()));
            shippingAddr.put("country_code", getStringOrEmptyString(mAddress.getCountry().iso3Code()));
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
        parcel.writeParcelable( mFrontImageAsset, flags);
        parcel.writeString(mMessage);
        parcel.writeParcelable(mAddress, flags);
    }

    private PostcardPrintJob(Parcel parcel) {
        //super( ProductCache.getDirtyInstance().getProductById( parcel.readString() ) );
        super( parcel );
        mFrontImageAsset = parcel.readParcelable(Asset.class.getClassLoader());
        mMessage = parcel.readString();
        mAddress = (Address)parcel.readParcelable(Address.class.getClassLoader());
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
