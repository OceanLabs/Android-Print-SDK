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
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;

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
  static private   final String  JSON_NAME_OPTIONS       = "options";
  static protected final String  JSON_NAME_POLAROID_TEXT = "polaroid_text";


  private           long                     mId;  // The id from the basket database
  transient private Product                  mProduct;  // Stop the product being serialised
  private           int                      mOrderQuantity;
  private final     HashMap<String, String>  mOptionsMap;


  public abstract BigDecimal getCost( String currencyCode );

  public abstract Set<String> getCurrenciesSupported();

  abstract public int getQuantity();

  abstract List<UploadableImage> getImagesForUploading();

  abstract JSONObject getJSONRepresentation();


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a print job.
   *
   *****************************************************/

  static public Job createPrintJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, List<?> imageList, boolean nullImagesAreBlank )
    {
    return ( new ImagesJob( product, orderQuantity, optionsMap, imageList, nullImagesAreBlank ) );
    }

  static public Job createPrintJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, List<?> imageList )
    {
    return ( new ImagesJob( product, orderQuantity, optionsMap, imageList, false ) );
    }

  static public Job createPrintJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, List<?> imageList, int offset, int length, boolean nullImagesAreBlank )
    {
    return ( new ImagesJob( product, orderQuantity, optionsMap, imageList, offset, length, nullImagesAreBlank ) );
    }

  static public Job createPrintJob( Product product, HashMap<String, String> optionsMap, List<?> imageList )
    {
    return ( new ImagesJob( product, 1, optionsMap, imageList ) );
    }

  static public Job createPrintJob( Product product, List<?> imageList )
    {
    return ( new ImagesJob( product, 1, null, imageList ) );
    }

  static public Job createPrintJob( Product product, HashMap<String,String> optionsMap, Object image )
    {
    List<UploadableImage> singleImageSpecList = new ArrayList<>( 1 );

    singleImageSpecList.add( singleUploadableImageFrom( image ) );

    return ( new ImagesJob( product, 1, optionsMap, singleImageSpecList ) );
    }

  static public Job createPrintJob( Product product, Object image )
    {
    return ( createPrintJob( product, null, image ) );
    }


  /*****************************************************
   *
   * Creates a photobook job.
   *
   *****************************************************/

  static public PhotobookJob createPhotobookJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, Object frontCoverImage, List<?> contentImageList )
    {
    return ( new PhotobookJob( product, orderQuantity, optionsMap, frontCoverImage, contentImageList ) );
    }

  static public PhotobookJob createPhotobookJob( Product product, HashMap<String,String> optionsMap, Object frontCoverImage, List<?> contentImageList )
    {
    return ( createPhotobookJob( product, 1, optionsMap, frontCoverImage, contentImageList ) );
    }

  static public PhotobookJob createPhotobookJob( Product product, Object frontCoverImage, List<?> contentImageList )
    {
    return ( createPhotobookJob( product, 1, null, frontCoverImage, contentImageList ) );
    }


  /*****************************************************
   *
   * Creates a greeting card job.
   *
   *****************************************************/

  static public GreetingCardJob createGreetingCardJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, Object frontImage, Object backImage, Object insideLeftImage, Object insideRightImage )
    {
    return ( new GreetingCardJob( product, orderQuantity, optionsMap, frontImage, backImage, insideLeftImage, insideRightImage ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, Object frontImage )
    {
    return ( new GreetingCardJob( product, orderQuantity, optionsMap, frontImage, null, null, null ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, Object frontImage, Object backImage, Object insideLeftImage, Object insideRightImage )
    {
    return ( createGreetingCardJob( product, 1, null, frontImage, backImage, insideLeftImage, insideRightImage ) );
    }

  static public GreetingCardJob createGreetingCardJob( Product product, Object frontImage )
    {
    return ( createGreetingCardJob( product, 1, null, frontImage, null, null, null ) );
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


  /*****************************************************
   *
   * Adds uploadable images and any border text to two lists.
   *
   *****************************************************/
  static protected void addUploadableImages( Object object, List<UploadableImage> uploadableImageList, List<String> borderTextList, boolean nullObjectsAreBlankPages )
    {
    if ( object == null && nullObjectsAreBlankPages )
      {
      uploadableImageList.add( null );

      if ( borderTextList != null ) borderTextList.add( null );
      }


    // For ImageSpecs, we need to add as many images as the quantity, and keep
    // track of any border text.

    else if ( object instanceof ImageSpec )
      {
      ImageSpec imageSpec = (ImageSpec)object;

      AssetFragment assetFragment = imageSpec.getAssetFragment();
      String        borderText    = imageSpec.getBorderText();
      int           quantity      = imageSpec.getQuantity();

      for ( int index = 0; index < quantity; index ++ )
        {
        uploadableImageList.add( new UploadableImage( assetFragment ) );

        if ( borderTextList != null ) borderTextList.add( borderText );
        }
      }


    // Anything else is just one image

    else
      {
      UploadableImage uploadableImage = singleUploadableImageFrom( object );

      if ( uploadableImage != null )
        {
        uploadableImageList.add( uploadableImage );

        if ( borderTextList != null ) borderTextList.add( null );
        }
      }
    }


  /*****************************************************
   *
   * Returns an UploadableImage from an unknown image object.
   *
   *****************************************************/
  static protected UploadableImage singleUploadableImageFrom( Object object )
    {
    if ( object == null ) return ( null );

    if ( object instanceof UploadableImage ) return ( (UploadableImage)object );
    if ( object instanceof ImageSpec       ) return ( new UploadableImage( ( (ImageSpec)object ).getAssetFragment() ) );
    if ( object instanceof AssetFragment   ) return ( new UploadableImage( (AssetFragment)object ) );
    if ( object instanceof Asset           ) return ( new UploadableImage( (Asset)object ) );

    throw ( new IllegalArgumentException( "Unable to convert " + object + " into UploadableImage" ) );
    }


  ////////// Constructor(s) //////////

  protected Job( long id, Product product, int orderQuantity, HashMap<String, String> optionsMap )
    {
    mId            = id;
    mProduct       = product;
    mOrderQuantity = orderQuantity;

    mOptionsMap = ( optionsMap != null ? optionsMap : new HashMap<String, String>( 0 ) );
    }

  protected Job( Parcel sourceParcel )
    {
    mId            = sourceParcel.readLong();
    mProduct       = Product.CREATOR.createFromParcel( sourceParcel );
    mOrderQuantity = sourceParcel.readInt();
    mOptionsMap    = sourceParcel.readHashMap( HashMap.class.getClassLoader() );
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


  /*****************************************************
   *
   * Adds the product option choices to the JSON.
   *
   * @return The options JSON object, so that the caller
   *         may add further options if desired.
   *
   *****************************************************/
  protected JSONObject addProductOptions( JSONObject jobJSONObject ) throws JSONException
    {
    JSONObject optionsJSONObject = new JSONObject();

    if ( mOptionsMap != null )
      {
      for ( String optionCode : mOptionsMap.keySet() )
        {
        optionsJSONObject.put( optionCode, mOptionsMap.get( optionCode ) );
        }
      }

    jobJSONObject.put( JSON_NAME_OPTIONS, optionsJSONObject );

    return ( optionsJSONObject );
    }


  /*****************************************************
   *
   * Returns the chosen options for the product.
   *
   *****************************************************/
  public HashMap<String,String> getProductOptions()
    {
    return ( mOptionsMap );
    }


  /*****************************************************
   *
   * Returns a product option.
   *
   *****************************************************/
  public String getProductOption( String parameter )
    {
    if ( mOptionsMap != null )
      {
      return ( mOptionsMap.get( parameter ) );
      }

    return ( null );
    }


  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    parcel.writeLong( mId );

    mProduct.writeToParcel( parcel, flags );

    parcel.writeInt( mOrderQuantity );

    parcel.writeMap( mOptionsMap );
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

    if ( ( mOptionsMap == null && otherOptionMap != null ) ||
         ( mOptionsMap != null && ( otherOptionMap == null ||
                                   mOptionsMap.size() != otherOptionMap.size() ) ) )
      {
      return ( false );
      }

    for ( String name : mOptionsMap.keySet() )
      {
      String value      = mOptionsMap.get( name );
      String otherValue = otherOptionMap.get( name );

      if ( ( value == null && otherValue != null ) ||
           ( value != null && ! value.equals( otherValue ) ) ) return ( false );
      }

    return ( true );
    }

  }
