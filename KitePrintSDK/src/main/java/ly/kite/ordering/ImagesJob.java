package ly.kite.ordering;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;

/**
 * Created by deonbotha on 09/02/2014.
 */

/*****************************************************
 *
 * This class represents any type of job that uses a
 * set of images. This can include multiple units of
 * a single-image product (such as phone cases) or
 * products that require multiple images (prints,
 * photobooks etc.)
 *
 *****************************************************/
public class ImagesJob extends Job
  {
  static private final String LOG_TAG = "ImagesJob";


  private List<UploadableImage> mUploadableImageList;


  public ImagesJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionMap, List<?> objectList, int offset, int length, boolean nullObjectsAreBlankPages )
    {
    super( jobId, product, orderQuantity, optionMap );


    // The image list can consist of the following objects:
    //   - null
    //   - UploadableImages
    //   - ImageSpecs
    //   - AssetFragments
    //   - Assets

    mUploadableImageList = new ArrayList<>();

    if ( objectList != null )
      {
      if ( offset < 0 )
        {
        length += offset;
        offset  = 0;
        }

      for ( int objectIndex = offset; objectIndex < objectList.size() && objectIndex < ( offset + length ); objectIndex ++ )
        {
        Object object = objectList.get( objectIndex );

        addUploadableImages( object, mUploadableImageList, nullObjectsAreBlankPages );
        }
      }
    }


  public ImagesJob( Product product, int orderQuantity, HashMap<String,String> optionMap, List<?> objectList, int offset, int length, boolean nullObjectsAreBlankPages )
    {
    this( 0, product, orderQuantity, optionMap, objectList, offset, length, nullObjectsAreBlankPages );
    }


  public ImagesJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionMap, List<?> objectList, int offset, boolean nullObjectsAreBlankPages )
    {
    this( jobId, product, orderQuantity, optionMap, objectList, offset, ( objectList != null ? objectList.size() : 0 ), nullObjectsAreBlankPages );
    }


  public ImagesJob( Product product, int orderQuantity, HashMap<String,String> optionMap, List<?> objectList, int offset, boolean nullObjectsAreBlankPages )
    {
    this( 0, product, orderQuantity, optionMap, objectList, offset, nullObjectsAreBlankPages );
    }


  public ImagesJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, List<?> objectList, boolean nullImagesAreBlank )
    {
    this( product, orderQuantity, optionsMap, objectList, 0, nullImagesAreBlank );
    }


  public ImagesJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, List<?> objectList )
    {
    this( product, orderQuantity, optionsMap, objectList, 0, false );
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
        return ( mUploadableImageList.size() );
    }


    @Override
    protected List<UploadableImage> getImagesForUploading()
      {
      // Copy only non-null uploadable images

      ArrayList<UploadableImage> uploadableImageArrayList = new ArrayList<>();

      addImagesForUploading( uploadableImageArrayList );

      return ( uploadableImageArrayList );
      }


  protected void addImagesForUploading( List<UploadableImage> uploadableImageList )
    {
    if ( mUploadableImageList != null )
      {
      for ( UploadableImage uploadableImage : mUploadableImageList )
        {
        if ( uploadableImage != null ) uploadableImageList.add( uploadableImage );
        }
      }
    }


  /*****************************************************
   *
   * Returns the images for this job as a list of image
   * specs.
   *
   *****************************************************/
  public List<ImageSpec> getImagesAsSpecList()
    {
    List<ImageSpec> imageSpecList = new ArrayList<>();

    if ( mUploadableImageList != null )
      {
      for ( UploadableImage uploadableImage : mUploadableImageList )
        {
        AssetFragment assetFragment;

        if ( uploadableImage != null &&
             ( assetFragment = uploadableImage.getAssetFragment() ) != null )
          {
          imageSpecList.add( new ImageSpec( assetFragment ) );
          }
        else
          {
          imageSpecList.add( null );
          }
        }
      }

    return ( imageSpecList );
    }


  @Override
  JSONObject getJSONRepresentation()
    {
    JSONObject jsonObject = new JSONObject();

    try
      {
      jsonObject.put( "template_id", getProductId() );

      addProductOptions( jsonObject );

      putAssetsJSON( mUploadableImageList, jsonObject );

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
  protected void putAssetsJSON( List<UploadableImage> uploadableImageList, JSONObject jsonObject ) throws JSONException
    {
    JSONArray assetsJSONArray = new JSONArray();

    for ( UploadableImage uploadableImage : uploadableImageList )
      {
      assetsJSONArray.put( "" + uploadableImage.getUploadedAssetId() );
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
        parcel.writeTypedList( mUploadableImageList );

    }

  protected ImagesJob( Parcel parcel )
    {
    super( parcel );

    mUploadableImageList = new ArrayList<>();
    parcel.readTypedList( mUploadableImageList, UploadableImage.CREATOR );
    }

    public static final Parcelable.Creator<ImagesJob> CREATOR
            = new Parcelable.Creator<ImagesJob>() {
        public ImagesJob createFromParcel( Parcel in) {
            return new ImagesJob(in);
        }

        public ImagesJob[] newArray( int size) {
            return new ImagesJob[size];
        }
    };


  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( ! ( otherObject instanceof ImagesJob ) ) ) return ( false );

    ImagesJob otherAssetListJob = (ImagesJob)otherObject;
    List<UploadableImage>  otherUploadableImageList    = otherAssetListJob.mUploadableImageList;

    if ( ! UploadableImage.areBothNullOrEqual( mUploadableImageList, otherUploadableImageList ) ) return ( false );

    return ( super.equals( otherObject ) );
    }

  }