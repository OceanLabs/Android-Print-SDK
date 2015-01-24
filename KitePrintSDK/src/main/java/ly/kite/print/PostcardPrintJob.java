package ly.kite.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by alibros on 16/01/15.
 */
class PostcardPrintJob extends PrintJob {

    private String templateId;
    private Asset frontImageAsset;
    private Asset overLayImageAsset;
    private String message;
    private Address address;
    private String location1, location2;

    public PostcardPrintJob(String templateId, Asset frontImageAsset, String message, Address address) {
        this(templateId, frontImageAsset, null, message, address, null, null);
    }

    public PostcardPrintJob(String templateId, Asset frontImageAsset, Asset overLayImageAsset, String message, Address address, String location1, String location2) {
        this.templateId = templateId;
        this.frontImageAsset = frontImageAsset;
        this.overLayImageAsset = overLayImageAsset;
        this.message = message;
        this.address = address;
        this.location1 = location1;
        this.location2 = location2;
    }

    @Override
    public BigDecimal getCost(String currencyCode) {
        Template template = Template.getTemplate(templateId);
        return template.getCost(currencyCode);
    }

    @Override
    public Set<String> getCurrenciesSupported() {
        Template template = Template.getTemplate(templateId);
        return template.getCurrenciesSupported();
    }

    @Override
    public ProductType getProductType() {
        return ProductType.POSTCARD;
    }

    @Override
    public int getQuantity() {
        return 1;
    }

    @Override
    List<Asset> getAssetsForUploading() {
        ArrayList<Asset> assets = new ArrayList<Asset>();
        assets.add(frontImageAsset);
        if (overLayImageAsset != null) {
            assets.add(overLayImageAsset);
        }

        return assets;
    }

    @Override
    public String getTemplateId() {
        return templateId;
    }

    private static String getStringOrEmptyString(String val) {
        return val == null ? "" : val;
    }

    private JSONObject getJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("template_id", templateId);

        JSONObject assets = new JSONObject();
        json.put("assets", assets);
        assets.put("photo", frontImageAsset.getId());
        if (overLayImageAsset != null) {
            assets.put("overlay_image", overLayImageAsset.getId());
        }

        JSONObject frameContents = new JSONObject();
        json.put("frame_contents", frameContents);

        // set message
        frameContents.put("frame1", new JSONObject("{\"paragraphs\":[{\"content\":\"15\", \"style\":\"spacer\"}, {\"content\":\"" + message + "\", \"style\":\"body\"}]}"));

        // set location
        final int ASSET_ID_LOCATION_ICON = 10;
        ArrayList<String> location = new ArrayList<String>();
        if (location1 != null) location.add(location1);
        if (location2 != null) location.add(location2);

        if (location.size() == 1) {
            frameContents.put("location" , new JSONObject("{\"paragraphs\":[{\"content\":\"" + location.get(0) + "\", \"style\":\"location1\"}]}"));
            assets.put("location_icon", ASSET_ID_LOCATION_ICON);
        } else if (location.size() > 1) {
            frameContents.put("location", new JSONObject("{\"paragraphs\":[{\"content\":\"" + location.get(0) + "\", \"style\":\"location1\"}, {\"content\":\"" + location.get(1) + "\", \"style\":\"location2\"}]}"));
            assets.put("location_icon", ASSET_ID_LOCATION_ICON);
        }

        ArrayList<Pair<String, String> > addrComponents = new ArrayList<Pair<String, String>>();
        addrComponents.add(new Pair<String, String>(address.getRecipientName(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getLine1(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getLine2(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getCity(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getStateOrCounty(), "body-centered"));
        addrComponents.add(new Pair<String, String>(address.getZipOrPostalCode(), "postcode-or-country"));
        addrComponents.add(new Pair<String, String>(address.getCountry().getName(), "postcode-or-country"));

        int addressComponentId = 0;
        for (Pair<String, String> addrComponent : addrComponents) {
            if (addrComponent.first != null) {
                frameContents.put("addr" + (++addressComponentId), new JSONObject("{\"paragraphs\":[{\"content\":\"" + addrComponent.first.trim() + "\", \"style\":\"" + addrComponent.second + "\"}]}"));
            }
        }

        if (addressComponentId == 7) {
            assets.put("extra_dots", 11);
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
            shippingAddr.put("country_code", getStringOrEmptyString(address.getCountry().getCodeAlpha3()));
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
        parcel.writeString(templateId);
        parcel.writeParcelable(frontImageAsset, flags);
        parcel.writeParcelable(overLayImageAsset, flags);
        parcel.writeString(message);
        parcel.writeParcelable(address, flags);
        parcel.writeString(location1);
        parcel.writeString(location2);
    }

    private PostcardPrintJob(Parcel parcel) {
        this.templateId = parcel.readString();
        this.frontImageAsset = parcel.readParcelable(Asset.class.getClassLoader());
        this.overLayImageAsset = parcel.readParcelable(Asset.class.getClassLoader());
        this.message = parcel.readString();
        this.address = (Address)parcel.readParcelable(Address.class.getClassLoader());
        this.location1 = parcel.readString();
        this.location2 = parcel.readString();
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

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(templateId);
        out.writeObject(frontImageAsset);
        out.writeObject(overLayImageAsset);
        out.writeObject(message);
        out.writeObject(address);
        out.writeObject(location1);
        out.writeObject(location2);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        templateId = (String)in.readObject();
        frontImageAsset = (Asset) in.readObject();
        overLayImageAsset = (Asset) in.readObject();
        message = (String)in.readObject();
        address = (Address) in.readObject();
        location1 = (String)in.readObject();
        location2 = (String)in.readObject();
    }

}
