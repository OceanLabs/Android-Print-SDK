package ly.kite.print;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ly.kite.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
class PrintsPrintJob extends PrintJob {

    private static final long serialVersionUID = 0L;


    private ProductType productType;
    private List<Asset> assets;

    //Postcard Specific
    private boolean isPostCard;
    private Address postCardAddress;
    private String location1, location2;
    private String message;



    private String templateId;


    public PrintsPrintJob(String templateId, List<Asset> assets){
        this.templateId = templateId;
        this.productType = ProductType.productTypeFromTemplate(templateId);
        this.assets = assets;
        this.isPostCard = false;
    }

    public PrintsPrintJob(String templateId, List<Asset> assets, String message , Address address, String location1, String location2){
        this.templateId = templateId;
        this.productType = ProductType.productTypeFromTemplate(templateId);
        this.assets = assets;
        this.isPostCard = true;
        this.postCardAddress = address;
        this.location1 = location1;
        this.location2 = location2;
        this.message = message;

    }

    @Override
    public BigDecimal getCost() {
        int imagesPerSheet = Template.getSyncedTemplateNumberOfImages(templateId);
        if (imagesPerSheet == 0) imagesPerSheet = 1;
        BigDecimal cost = new BigDecimal(Template.getCostForTemplate(templateId));
        int numOrders = ((assets.size() + (imagesPerSheet - 1))/ imagesPerSheet);
        return cost.multiply(new BigDecimal(numOrders));
    }

    @Override
    public ProductType getProductType() {
        return productType;
    }

    @Override
    public int getQuantity() {
        return assets.size();
    }

    @Override
    List<Asset> getAssetsForUploading() {
        return assets;
    }

    @Override
    public String getTemplateName() {
        return productType.defaultTemplate;
    }

    @Override
    JSONObject getJSONRepresentation() {
        if (isPostCard == true){
            PostcardPrintJob job = new PostcardPrintJob(templateId,assets.get(0),null,message,postCardAddress,location1,location2);
            try {
                return job.getJson();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {


            JSONArray assets = new JSONArray();
            for (Asset a : this.assets) {
                assets.put("" + a.getId());
            }

            JSONObject json = new JSONObject();
            try {
                json.put("template_id", productType.defaultTemplate);
                json.put("assets", assets);
                json.put("frame_contents", new JSONObject());
            } catch (JSONException ex) {
                throw new RuntimeException(ex); // this should NEVER happen :)
            }
            return json;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(productType.defaultTemplate);
        parcel.writeTypedList(assets);

    }

    private PrintsPrintJob(Parcel parcel) {
        this.productType = ProductType.productTypeFromTemplate(parcel.readString());
        this.templateId = productType.getDefaultTemplate();
        this.assets = new ArrayList<Asset>();
        parcel.readTypedList(assets, Asset.CREATOR);
    }

    public static final Parcelable.Creator<PrintsPrintJob> CREATOR
            = new Parcelable.Creator<PrintsPrintJob>() {
        public PrintsPrintJob createFromParcel(Parcel in) {
            return new PrintsPrintJob(in);
        }

        public PrintsPrintJob[] newArray(int size) {
            return new PrintsPrintJob[size];
        }
    };

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(productType.defaultTemplate);
        out.writeInt(assets.size());
        for (Asset a : assets) {
            out.writeObject(a);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        productType = ProductType.productTypeFromTemplate((String) in.readObject());
        templateId = productType.getDefaultTemplate();
        int numAssets = in.readInt();
        assets = new ArrayList<Asset>(numAssets);
        for (int i = 0; i < numAssets; ++i) {
            assets.add((Asset) in.readObject());
        }
    }

}
