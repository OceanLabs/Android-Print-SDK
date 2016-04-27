package ly.kite.ordering;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ly.kite.catalogue.Product;
import ly.kite.util.Asset;

/**
 * Created by deonbotha on 09/02/2014.
 */

/*****************************************************
 *
 * This class represents any type of job that uses a
 * list of assets. This can include multiple units of
 * a single-image product (such as phone cases) or
 * products that require multiple images (prints,
 * photobooks etc.)
 *
 *****************************************************/
public class AssetListJob extends Job
  {

    private List<Asset> mAssetList;


  public AssetListJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionMap, List<Asset> assetList )
    {
    super( jobId, product, orderQuantity, optionMap );

    mAssetList = assetList;
    }

  public AssetListJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, List<Asset> assetList )
    {
    this( 0, product, orderQuantity, optionsMap, assetList );
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

    @Override
    public int getQuantity() {
        return mAssetList.size();
    }

    @Override
    List<Asset> getAssetsForUploading() {
        return mAssetList;
    }


  public List<Asset> getAssets()
    {
    return ( mAssetList );
    }


  @Override
  JSONObject getJSONRepresentation()
    {
    JSONObject jsonObject = new JSONObject();

    try
      {
      jsonObject.put( "template_id", getProductId() );

      addProductOptions( jsonObject );

      putAssetsJSON( mAssetList, jsonObject );

      jsonObject.put( "frame_contents", new JSONObject() );
      }
    catch ( JSONException ex )
      {
      throw ( new RuntimeException( ex ) ); // this should NEVER happen :)
      }

    return ( jsonObject );
    }


  /*****************************************************
   *
   * Adds the assets to the supplied JSON object. The default
   * implementation just adds the assets as an array.
   *
   *****************************************************/
  protected void putAssetsJSON( List<Asset> assetList, JSONObject jsonObject ) throws JSONException
    {
    JSONArray assetsJSONArray = new JSONArray();

    for ( Asset asset : assetList )
      {
      assetsJSONArray.put( "" + asset.getId() );
      }

    jsonObject.put( "assets", assetsJSONArray );
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

    protected AssetListJob( Parcel parcel) {
        super( parcel );
        this.mAssetList = new ArrayList<Asset>();
        parcel.readTypedList( mAssetList, Asset.CREATOR);
    }

    public static final Parcelable.Creator<AssetListJob> CREATOR
            = new Parcelable.Creator<AssetListJob>() {
        public AssetListJob createFromParcel( Parcel in) {
            return new AssetListJob(in);
        }

        public AssetListJob[] newArray( int size) {
            return new AssetListJob[size];
        }
    };


  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( ! ( otherObject instanceof AssetListJob ) ) ) return ( false );

    AssetListJob otherAssetListJob = (AssetListJob)otherObject;
    List<Asset>  otherAssetList    = otherAssetListJob.getAssets();

    if ( ! Asset.areBothNullOrEqual( mAssetList, otherAssetList ) ) return ( false );

    return ( super.equals( otherObject ) );
    }

  }