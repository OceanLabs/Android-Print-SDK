package ly.kite.catalogue;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ly.kite.address.Address;

/**
 * Created by deonbotha on 09/02/2014.
 */
public abstract class PrintJob implements Parcelable {

  private static final String JSON_NAME_OPTIONS = "options";

    transient private Product       mProduct;  // Stop the product being serialised
    private final HashMap<String,String> mOptionMap;

    public abstract BigDecimal getCost(String currencyCode);
    public abstract Set<String> getCurrenciesSupported();

    public abstract int getQuantity();
    abstract List<Asset> getAssetsForUploading();
    abstract JSONObject getJSONRepresentation();

    protected PrintJob( Product product, HashMap<String,String> optionMap )
      {
      mProduct   = product;

      mOptionMap = ( optionMap != null ? optionMap : new HashMap<String, String>( 0 ) );
      }

    protected PrintJob( Product product )
      {
      this( product, null );
      }


  protected PrintJob( Parcel sourceParcel )
    {
    mProduct   = Product.CREATOR.createFromParcel( sourceParcel );
    mOptionMap = sourceParcel.readHashMap( HashMap.class.getClassLoader() );
    }


  public static PrintJob createPrintJob( Product product, HashMap<String,String> optionMap, Asset asset )
    {
    List<Asset> singleAssetList = new ArrayList<Asset>( 1 );
    singleAssetList.add( asset );

    return ( createPrintJob( product, optionMap, singleAssetList ) );
    }

  public static PrintJob createPrintJob( Product product, Asset asset )
    {
    return ( createPrintJob( product, null, asset ) );
    }

    public static PrintJob createPrintJob(Product product, HashMap<String,String> optionMap, List<Asset> assets) {
        return new PrintsPrintJob( product, optionMap, assets );
    }

  public static PrintJob createPrintJob( Product product, List<Asset> assets )
    {
    return new PrintsPrintJob( product, null, assets );
    }

    public static PrintJob createPostcardPrintJob(Product product, HashMap<String,String> optionMap, Asset frontImageAsset, String message, Address address) {
        return new PostcardPrintJob(product, optionMap, frontImageAsset, message, address);
    }

  public static PrintJob createPostcardPrintJob( Product product, Asset frontImageAsset, String message, Address address )
    {
    return new PostcardPrintJob( product, null, frontImageAsset, message, address );
    }

  public Product getProduct()
    {
    return ( mProduct );
    }


  public String getProductId()
    {
    return ( mProduct.getId() );
    }


  // Adds the product option choices to the JSON
  protected void addProductOptions( JSONObject jobJSONObject ) throws JSONException
    {
    if ( mOptionMap == null ) return;

    JSONObject optionsJSONObject = new JSONObject();

    for ( String optionCode : mOptionMap.keySet() )
      {
      optionsJSONObject.put( optionCode, mOptionMap.get( optionCode ) );
      }

    jobJSONObject.put( JSON_NAME_OPTIONS, optionsJSONObject );
    }


  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    mProduct.writeToParcel( parcel, flags );
    parcel.writeMap( mOptionMap );
    }

}
