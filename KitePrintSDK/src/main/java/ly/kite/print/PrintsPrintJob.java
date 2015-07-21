package ly.kite.print;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by deonbotha on 09/02/2014.
 */
class PrintsPrintJob extends PrintJob {

    private static final long serialVersionUID = 1L;
    private List<Asset> mAssetList;


  public PrintsPrintJob( Product product, List<Asset> assetList )
    {
    super( product );

    this.mAssetList = assetList;
    }

    @Override
    public BigDecimal getCost(String currencyCode) {
        Product product = getProduct();
        BigDecimal sheetCost = product.getCost(currencyCode);
        int expectedQuantity = product.getQuantityPerSheet();

        int numOrders = (int) Math.floor((getQuantity() + expectedQuantity - 1) / expectedQuantity);
        return sheetCost.multiply(new BigDecimal(numOrders));
    }

    @Override
    public Set<String> getCurrenciesSupported() {
        try
            {
            Product product = getProduct();

            return product.getCurrenciesSupported();
            }
        catch ( IllegalArgumentException iae )
            {
            // Fall through
            }

    return Collections.EMPTY_SET;
    }

//    @Override
//    public ProductType getProductType() {
//        return productType;
//    }

    @Override
    public int getQuantity() {
        return mAssetList.size();
    }

    @Override
    List<Asset> getAssetsForUploading() {
        return mAssetList;
    }

//    @Override
//    public String getProductId() {
//        return productType.getDefaultTemplate();
//    }

    @Override
    JSONObject getJSONRepresentation() {
        JSONArray assets = new JSONArray();
        for (Asset a : this.mAssetList ) {
            assets.put("" + a.getId());
        }

        JSONObject json = new JSONObject();
        try {
            json.put("template_id", getProductId());
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
        super.writeToParcel( parcel, flags );
        parcel.writeTypedList( mAssetList );

    }

    private PrintsPrintJob(Parcel parcel) {
        //super( ProductCache.getDirtyInstance().getProductById( parcel.readString() ) );
        super( parcel );
        this.mAssetList = new ArrayList<Asset>();
        parcel.readTypedList( mAssetList, Asset.CREATOR);
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

//    protected void writeObject(java.io.ObjectOutputStream out) throws IOException {
//        super.writeObject( out );
//        out.writeInt( mAssetList.size() );
//        for (Asset a : mAssetList ) {
//            out.writeObject(a);
//        }
//    }
//
//    protected void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
//        super.readObject( in );
//        int numAssets = in.readInt();
//        mAssetList = new ArrayList<Asset>(numAssets);
//        for (int i = 0; i < numAssets; ++i) {
//            mAssetList.add( (Asset) in.readObject() );
//        }
//    }

}
