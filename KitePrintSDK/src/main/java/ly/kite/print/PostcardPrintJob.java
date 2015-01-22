package ly.kite.print;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
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
public class PostcardPrintJob extends PrintJob {

    private String templateId;
    private Asset frontImageAsset;
    private Asset overLayImageAsset;
    private String message;
    private Address address;
    private String location1,location2;
    private List<Asset> assets;
    private String contentStyle;



    // OverlayImageAsset and Location1&2 parameters are optional. Pass null if you don't need them.
    public PostcardPrintJob(String templateId, Asset frontImageAsset, Asset overLayImageAsset, String message, Address address, String location1, String location2) {
        this.templateId = templateId;
        this.frontImageAsset = frontImageAsset;
        this.overLayImageAsset = overLayImageAsset;
        this.message = message;
        this.address = address;
        this.location1 = location1;
        this.location2 = location2;
        this.contentStyle = "body";

        assets.add(frontImageAsset);
        if (overLayImageAsset!=null)
            assets.add(overLayImageAsset);

    }


    public PostcardPrintJob(String templateId, List<Asset> assets, String message, Address address, String location1, String location2) {
        this.templateId = templateId;
        this.message = message;
        this.address = address;
        this.location1 = location1;
        this.location2 = location2;
        this.assets = assets;
        this.contentStyle = "body";

    }


    public PostcardPrintJob(String templateId, List<Asset> assets, String message, Address address, String location1, String location2, String style) {
        this.templateId = templateId;
        this.message = message;
        this.address = address;
        this.location1 = location1;
        this.location2 = location2;
        this.assets = assets;
        this.contentStyle = style;

    }

    private JSONObject getJson() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject frameContent = new JSONObject();
        JSONObject jsonAssets = new JSONObject();

        jsonAssets.put("photo",assets.get(0).getId());

        if (assets.size() > 1){
            jsonAssets.put("overlay_image",assets.get(1).getId());
        }

        frameContent.put("frame1",generateBodyParagraphsObject(message,contentStyle));
        frameContent.put("addr1",generateBodyParagraphsObject(address.getRecipientName(),contentStyle));
        frameContent.put("addr2",generateBodyParagraphsObject(address.getLine1(),contentStyle));
        frameContent.put("addr3",generateBodyParagraphsObject(address.getLine2(),contentStyle));
        frameContent.put("addr4",generateBodyParagraphsObject(address.getCity(),contentStyle));
        frameContent.put("addr5",generateBodyParagraphsObject(address.getStateOrCounty(),contentStyle));
        frameContent.put("addr6",generateBodyParagraphsObject(address.getZipOrPostalCode(),contentStyle));
        frameContent.put("addr7",generateBodyParagraphsObject(address.getCountry().getName(),contentStyle));

        if (location1.length()>0){
            frameContent.put("location1",generateBodyParagraphsObject(location1,"location1"));
        }
        if (location2.length()>0){
            frameContent.put("location2",generateBodyParagraphsObject(location2,"location2"));
        }

        json.put("template_id", templateId);
        json.put("assets", jsonAssets);
        json.put("frame_contents", frameContent);

        return  json;
    }


    private JSONObject generateBodyParagraphsObject(String content, String style) throws JSONException {
        JSONArray paragraphs = new JSONArray();
        JSONObject para1 = new JSONObject();
        para1.put("content", content);
        para1.put("style", style);
        paragraphs.put(para1);

        JSONObject json = new JSONObject();
        json.put("paragraphs",paragraphs);

        return json;

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
        return assets;
    }

    @Override
    public String getTemplateName() {
        return templateId;
    }

    @Override
    JSONObject getJSONRepresentation() {
            PostcardPrintJob job = new PostcardPrintJob(templateId, assets,message,address,location1,location2);

            try {
                return job.getJson();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(templateId);
        parcel.writeTypedList(assets);
        parcel.writeString(message);
        parcel.writeParcelable(address,flags);
        parcel.writeString(location1);
        parcel.writeString(location2);

    }

    private PostcardPrintJob(Parcel parcel) {


        this.templateId = parcel.readString();
        this.assets = new ArrayList<Asset>();
        parcel.readTypedList(assets, Asset.CREATOR);
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
        out.writeInt(assets.size());
        for (Asset a : assets) {
            out.writeObject(a);
        }
        out.writeObject(message);
        out.writeObject(address);
        out.writeObject(location1);
        out.writeObject(location2);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        templateId = (String)in.readObject();
        int numAssets = in.readInt();
        assets = new ArrayList<Asset>(numAssets);
        for (int i = 0; i < numAssets; ++i) {
            assets.add((Asset) in.readObject());
        }

        message = (String)in.readObject();
        address = (Address) in.readObject();
        location1 = (String)in.readObject();
        location2 = (String)in.readObject();
    }

}
