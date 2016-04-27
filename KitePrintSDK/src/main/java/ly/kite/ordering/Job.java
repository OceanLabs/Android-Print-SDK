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

  private           long                     mId;  // The id from the basket database
  transient private Product                  mProduct;  // Stop the product being serialised
  private           int                      mOrderQuantity;
  private final     HashMap<String, String>  mOptionMap;


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

  static public Job createPrintJob( Product product, int orderQuantity, HashMap<String, String> optionMap, List<Asset> assets )
    {
    return ( new AssetListJob( product, orderQuantity, optionMap, assets ) );
    }

  static public Job createPrintJob( Product product, HashMap<String, String> optionMap, List<Asset> assets )
    {
    return ( createPrintJob( product, 1, optionMap, assets ) );
    }

  static public Job createPrintJob( Product product, List<Asset> assets )
    {
    return ( createPrintJob( product, 1, null, assets ) );
    }

  static public Job createPrintJob( Product product, HashMap<String, String> optionMap, Asset asset )
    {
    List<Asset> singleAssetList = new ArrayList<Asset>( 1 );
    singleAssetList.add( asset );

    return ( createPrintJob( product, 1, optionMap, singleAssetList ) );
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

  static public PhotobookJob createPhotobookJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    return ( new PhotobookJob( product, orderQuantity, optionsMap, frontCoverAsset, contentAssetList ) );
    }

  static public PhotobookJob createPhotobookJob( Product product, HashMap<String,String> optionsMap, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    return ( createPhotobookJob( product, 1, optionsMap, frontCoverAsset, contentAssetList ) );
    }

  static public PhotobookJob createPhotobookJob( Product product, Asset frontCoverAsset, List<Asset> contentAssetList )
    {
    return ( createPhotobookJob( product, 1, null, frontCoverAsset, contentAssetList ) );
    }


  /*****************************************************
   *
   * Creates a greeting card job.
   *
   *****************************************************/

  static public GreetingCardJob createGreetingCardJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, Asset frontImageAsset, Asset backImageAsset, Asset insideLeftImageAsset, Asset insideRightImageAsset )
    {
    return ( new GreetingCardJob( product, orderQuantity, optionsMap, frontImageAsset, backImageAsset, insideLeftImageAsset, insideRightImageAsset ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, Asset frontImageAsset, Asset backImageAsset, Asset insideLeftImageAsset, Asset insideRightImageAsset )
    {
    return ( createGreetingCardJob( product, 1, null, frontImageAsset, backImageAsset, insideLeftImageAsset, insideRightImageAsset ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, Asset frontImageAsset )
    {
    return ( createGreetingCardJob( product, 1, null, frontImageAsset, null, null, null ) );
    }


  /*****************************************************
   *
   * Creates a postcard job.
   *
   *****************************************************/

  public static Job createPostcardJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, Asset frontImageAsset, Asset backImageAsset, String message, Address address )
    {
    return ( new PostcardJob( product, orderQuantity, optionsMap, frontImageAsset, backImageAsset, message, address ) );
    }

  public static Job createPostcardJob( Product product, HashMap<String, String> optionMap, Asset frontImageAsset, String message, Address address )
    {
    return ( createPostcardJob( product, 1, optionMap, frontImageAsset, null, message, address ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, String message, Address address )
    {
    return ( createPostcardJob( product, 1, null, frontImageAsset, null, message, address ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, Asset backImageAsset )
    {
    return ( createPostcardJob( product, 1, null, frontImageAsset, backImageAsset, null, null ) );
    }

  public static Job createPostcardJob( Product product, Asset frontImageAsset, Asset backImageAsset, String message, Address address )
    {
    return ( createPostcardJob( product, 1, null, frontImageAsset, backImageAsset, message, address ) );
    }


  ////////// Constructor(s) //////////

  protected Job( long id, Product product, int orderQuantity, HashMap<String, String> optionMap )
    {
    mId            = id;
    mProduct       = product;
    mOrderQuantity = orderQuantity;

    mOptionMap     = ( optionMap != null ? optionMap : new HashMap<String, String>( 0 ) );
    }

  protected Job( Parcel sourceParcel )
    {
    mId            = sourceParcel.readLong();
    mProduct       = Product.CREATOR.createFromParcel( sourceParcel );
    mOrderQuantity = sourceParcel.readInt();
    mOptionMap     = sourceParcel.readHashMap( HashMap.class.getClassLoader() );
    }



  public long getId()
    {
    return ( mId );
    }


  public Product getProduct()
    {
    return ( mProduct );
    }


  public String getProductId()
    {
    return ( mProduct.getId() );
    }


  public void setOrderQuantity( int orderQuantity )
    {
    mOrderQuantity = orderQuantity;
    }


  public int getOrderQuantity()
    {
    return ( mOrderQuantity );
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
    parcel.writeLong( mId );

    mProduct.writeToParcel( parcel, flags );

    parcel.writeInt( mOrderQuantity );

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
