package ly.kite.ordering;

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
import ly.kite.catalogue.Product;
import ly.kite.util.Asset;

/**
 * Created by deonbotha on 09/02/2014.
 */

/*****************************************************
 *
 * This class represents a job: a request for a single
 * product. Orders may contain any number of jobs.
 *
 * Note that the naming isn't consistent with other abstract
 * classes used in the SDK (i.e. it is not called "AJob");
 * this is intentional. Since it is developer-facing, they are
 * probably more comfortable with this naming.
 *
 *****************************************************/
public abstract class Job implements Parcelable
  {

  private static final String JSON_NAME_OPTIONS = "options";

  transient private Product mProduct;  // Stop the product being serialised
  private final HashMap<String, String> mOptionMap;


  public abstract BigDecimal getCost( String currencyCode );

  public abstract Set<String> getCurrenciesSupported();

  abstract public int getQuantity();

  abstract List<Asset> getAssetsForUploading();

  abstract JSONObject getJSONRepresentation();


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a print job.
   *
   *****************************************************/

  static public Job createPrintJob( Product product, HashMap<String, String> optionMap, List<Asset> assets )
    {
    return ( new AssetListJob( product, optionMap, assets ) );
    }

  static public Job createPrintJob( Product product, List<Asset> assets )
    {
    return ( createPrintJob( product, null, assets ) );
    }

  static public Job createPrintJob( Product product, HashMap<String, String> optionMap, Asset asset )
    {
    List<Asset> singleAssetList = new ArrayList<Asset>( 1 );
    singleAssetList.add( asset );

    return ( createPrintJob( product, optionMap, singleAssetList ) );
    }

  static public Job createPrintJob( Product product, Asset asset )
    {
    return ( createPrintJob( product, null, asset ) );
    }


  /*****************************************************
   *
   * Creates a photobook job.
   *
   *****************************************************/

  static public PhotobookJob createPhotobookJob( Product product, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    return ( new PhotobookJob( product, null, frontCoverAsset, contentAssetList ) );
    }


  /*****************************************************
   *
   * Creates a greeting card job.
   *
   *****************************************************/

  static public GreetingCardJob createGreetingCardJob( Product product, Asset frontImageAsset, Asset backImageAsset, Asset insideLeftImageAsset, Asset insideRightImageAsset )
    {
    return ( new GreetingCardJob( product, frontImageAsset, backImageAsset, insideLeftImageAsset, insideRightImageAsset ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, Asset frontImageAsset )
    {
    return ( createGreetingCardJob( product, frontImageAsset, null, null, null ) );
    }


  /*****************************************************
   *
   * Creates a postcard job.
   *
   *****************************************************/

  public static Job createPostcardJob( Product product, HashMap<String, String> optionMap, Asset frontImageAsset, String message, Address address )
    {
    return ( new PostcardJob( product, optionMap, frontImageAsset, null, message, address ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, String message, Address address )
    {
    return ( new PostcardJob( product, frontImageAsset, message, address ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, Asset backImageAsset )
    {
    return ( new PostcardJob( product, frontImageAsset, backImageAsset ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, Asset backImageAsset, String message, Address address )
    {
    return ( new PostcardJob( product, frontImageAsset, backImageAsset, message, address ) );
    }


  ////////// Constructor(s) //////////

  protected Job( Product product, HashMap<String, String> optionMap )
    {
    mProduct = product;

    mOptionMap = ( optionMap != null ? optionMap : new HashMap<String, String>( 0 ) );
    }

  protected Job( Product product )
    {
    this( product, null );
    }


  protected Job( Parcel sourceParcel )
    {
    mProduct = Product.CREATOR.createFromParcel( sourceParcel );
    mOptionMap = sourceParcel.readHashMap( HashMap.class.getClassLoader() );
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


  /*****************************************************
   *
   * Returns the chosen options for the product.
   *
   *****************************************************/
  public HashMap<String,String> getProductOptions()
    {
    return ( mOptionMap );
    }


  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    mProduct.writeToParcel( parcel, flags );

    parcel.writeMap( mOptionMap );
    }


  /*****************************************************
   *
   * Returns true if this job equals the supplied job.
   *
   *****************************************************/
  @Override
  public boolean equals( Object otherJobObject )
    {
    if ( otherJobObject == null || ( ! ( otherJobObject instanceof Job ) ) ) return ( false );

    Job                    otherJob       = (Job)otherJobObject;
    Product                otherProduct   = otherJob.getProduct();
    HashMap<String,String> otherOptionMap = otherJob.getProductOptions();

    if ( ! mProduct.getId().equals( otherProduct.getId() ) ) return ( false );

    if ( ( mOptionMap == null && otherOptionMap != null ) ||
         ( mOptionMap != null && ( otherOptionMap == null ||
                                   mOptionMap.size() != otherOptionMap.size() ) ) )
      {
      return ( false );
      }

    for ( String name : mOptionMap.keySet() )
      {
      String value      = mOptionMap.get( name );
      String otherValue = otherOptionMap.get( name );

      if ( ( value == null && otherValue != null ) ||
           ( value != null && ! value.equals( otherValue ) ) ) return ( false );
      }

    return ( true );
    }

  }
