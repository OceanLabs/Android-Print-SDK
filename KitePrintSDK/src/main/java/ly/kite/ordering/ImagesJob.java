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
  private List<UploadableImage> mUploadableImageList;


  public ImagesJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionMap, List<?> objectList )
    {
    super( jobId, product, orderQuantity, optionMap );


    // The image list can be UploadableImages, ImageSpecs, AssetFragments, or Assets

    mUploadableImageList = new ArrayList<>();

    if ( objectList != null )
      {
      for ( Object object : objectList )
        {
        addUploadableImages( object, mUploadableImageList );
        }
      }
    }


  public ImagesJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, List<?> objectList )
    {
    this( 0, product, orderQuantity, optionsMap, objectList );
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
    List<UploadableImage> getImagesForUploading()
      {
      return ( mUploadableImageList );
      }


//  public List<Asset> getAssets()
//    {
//    return ( mAssetList );
//    }


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