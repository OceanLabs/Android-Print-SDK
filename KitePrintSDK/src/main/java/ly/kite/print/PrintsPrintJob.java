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

/**
 * Created by deonbotha on 09/02/2014.
 */
class PrintsPrintJob extends PrintJob {

    private static final long serialVersionUID = 0L;

    private ProductType productType;
    private List<Asset> assets;

    public PrintsPrintJob(ProductType productType, List<Asset> assets) {
        this.productType = productType;
        this.assets = assets;
    }

    @Override
    public BigDecimal getCost() {
        // TODO: don't do this in the final public API, look up dynamically!
        switch (productType) {
            case MAGNETS: {
                BigDecimal cost = new BigDecimal("12.50");
                int numOrders = (assets.size() + 21) / 22;
                return cost.multiply(new BigDecimal(numOrders));
            }
            case POLAROIDS:
            case SQUARES: {
                BigDecimal cost = new BigDecimal("12.50");
                int numOrders = (assets.size() + 19) / 20;
                return cost.multiply(new BigDecimal(numOrders));
            }
            case MINI_POLAROIDS:
            case MINI_SQUARES: {
                BigDecimal cost = new BigDecimal("11.50");
                int numOrders = (assets.size() + 43) / 44;
                return cost.multiply(new BigDecimal(numOrders));
            }
            default:
                throw new AssertionError("Oops");
        }
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
        int numAssets = in.readInt();
        assets = new ArrayList<Asset>(numAssets);
        for (int i = 0; i < numAssets; ++i) {
            assets.add((Asset) in.readObject());
        }
    }

}
