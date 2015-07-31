package ly.kite.product;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
public abstract class PrintJob implements Parcelable {

    private static final long serialVersionUID = 1L;

    transient private Product mProduct;  // Stop the product being serialised

    public abstract BigDecimal getCost(String currencyCode);
    public abstract Set<String> getCurrenciesSupported();

    public abstract int getQuantity();
    abstract List<Asset> getAssetsForUploading();
    abstract JSONObject getJSONRepresentation();

    protected PrintJob( Product product )
      {
      mProduct = product;
      }


  protected PrintJob( Parcel sourceParcel )
    {
    mProduct = Product.CREATOR.createFromParcel( sourceParcel );
    }


  public static PrintJob createPrintJob( Product product, Asset asset )
    {
    List<Asset> singleAssetList = new ArrayList<Asset>( 1 );
    singleAssetList.add( asset );

    return ( createPrintJob( product, singleAssetList ) );
    }

    public static PrintJob createPrintJob(Product product, List<Asset> assets) {
        return new PrintsPrintJob(product, assets);
    }

    public static PrintJob createPostcardPrintJob(Product product, Asset frontImageAsset, String message, Address address) {
        return new PostcardPrintJob(product, frontImageAsset, message, address);
    }


  public Product getProduct()
    {
    return ( mProduct );
    }


  public String getProductId()
    {
    return ( mProduct.getId() );
    }

  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    mProduct.writeToParcel( parcel, flags );
    }

}
