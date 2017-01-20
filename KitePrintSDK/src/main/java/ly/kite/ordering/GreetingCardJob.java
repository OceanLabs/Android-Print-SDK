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

import ly.kite.catalogue.Product;
import ly.kite.util.AssetFragment;
import ly.kite.util.UploadableImage;

/**
 * Created by dbotha on 23/12/2015.
 */

///// Class Declaration /////

/*****************************************************
 *
 * This class represents a greeting card job.
 *
 *****************************************************/
public class GreetingCardJob extends Job
  {
  private UploadableImage  mFrontUploadableImage;
  private UploadableImage  mBackUploadableImage;
  private UploadableImage  mInsideLeftUploadableImage;
  private UploadableImage  mInsideRightUploadableImage;


  /*****************************************************
   *
   * Returns an image spec containing the asset fragment frpm
   * the supplied uploadable image, or null if the uploadable
   * image is null.
   *
   *****************************************************/
  static private ImageSpec imageSpecFrom( UploadableImage uploadableImage )
    {
    AssetFragment assetFragment;

    if ( uploadableImage == null || ( assetFragment = uploadableImage.getAssetFragment() ) == null )
      {
      return ( null );
      }

    return ( new ImageSpec( assetFragment ) );
    }


  public GreetingCardJob( long jobId, Product product, int orderQuantity, HashMap<String,String> optionsMap, Object frontImage, Object backImage, Object insideLeftImage, Object insideRightImage )
    {
    super( jobId, product, orderQuantity, optionsMap );

    mFrontUploadableImage       = singleUploadableImageFrom( frontImage );
    mBackUploadableImage        = singleUploadableImageFrom( backImage );
    mInsideLeftUploadableImage  = singleUploadableImageFrom( insideLeftImage );
    mInsideRightUploadableImage = singleUploadableImageFrom( insideRightImage );
    }

  public GreetingCardJob( Product product, int orderQuantity, HashMap<String,String> optionsMap, Object frontImage, Object backImage, Object insideLeftImage, Object insideRightImage )
    {
    this( 0, product, orderQuantity, optionsMap, frontImage, backImage, insideLeftImage, insideRightImage );
    }


  @Override
  public BigDecimal getCost( String currencyCode )
    {
    return getProduct().getCost( currencyCode );
    }

  @Override
  public Set<String> getCurrenciesSupported()
    {
    return getProduct().getCurrenciesSupported();
    }

  @Override
  public int getQuantity()
    {
    return 1;
    }


  /*****************************************************
   *
   * Returns the front uploadable image.
   *
   *****************************************************/
  public UploadableImage getFrontUploadableImage()
    {
    return ( mFrontUploadableImage );
    }


  /*****************************************************
   *
   * Returns the back uploadable image.
   *
   *****************************************************/
  public UploadableImage getBackUploadableImage()
    {
    return ( mBackUploadableImage );
    }


  /*****************************************************
   *
   * Returns the inside left uploadable image.
   *
   *****************************************************/
  public UploadableImage getInsideLeftUploadableImage()
    {
    return ( mInsideLeftUploadableImage );
    }


  /*****************************************************
   *
   * Returns the inside right uploadable image.
   *
   *****************************************************/
  public UploadableImage getInsideRightUploadableImage()
    {
    return ( mInsideRightUploadableImage );
    }


  @Override
  List<UploadableImage> getImagesForUploading()
    {
    ArrayList<UploadableImage> uploadableImageArrayList = new ArrayList<>();

    if ( mFrontUploadableImage       != null ) uploadableImageArrayList.add( mFrontUploadableImage );
    if ( mBackUploadableImage        != null ) uploadableImageArrayList.add( mBackUploadableImage );
    if ( mInsideLeftUploadableImage  != null ) uploadableImageArrayList.add( mInsideLeftUploadableImage );
    if ( mInsideRightUploadableImage != null ) uploadableImageArrayList.add( mInsideRightUploadableImage );

    return ( uploadableImageArrayList );
    }


  @Override
  JSONObject getJSONRepresentation()
    {
    JSONObject json = new JSONObject();
    try
      {
      json.put( "template_id", getProductId() );
      addProductOptions( json );

      JSONObject assets = new JSONObject();

      json.put( "assets", assets );

      if ( mFrontUploadableImage != null ) assets.put( "front_image",        mFrontUploadableImage.getUploadedAssetId() );
      if ( mBackUploadableImage != null ) assets.put( "back_image",         mBackUploadableImage.getUploadedAssetId() );
      if ( mInsideRightUploadableImage != null ) assets.put( "inside_right_image", mInsideRightUploadableImage.getUploadedAssetId() );
      if ( mInsideLeftUploadableImage != null ) assets.put( "inside_left_image",  mInsideLeftUploadableImage.getUploadedAssetId() );

      }
    catch ( JSONException e )
      {
      // ignore - won't happen ;)
      }

    return json;
    }

  @Override
  public int describeContents()
    {
    return 0;
    }

  @Override
  public void writeToParcel( Parcel parcel, int flags )
    {
    super.writeToParcel( parcel, flags );
    parcel.writeParcelable( mFrontUploadableImage, flags );
    parcel.writeParcelable( mBackUploadableImage, flags );
    parcel.writeParcelable( mInsideLeftUploadableImage, flags );
    parcel.writeParcelable( mInsideRightUploadableImage, flags );
    }

  private GreetingCardJob( Parcel parcel )
    {
    super( parcel );

    mFrontUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    mBackUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    mInsideLeftUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    mInsideRightUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    }

  public static final Parcelable.Creator<GreetingCardJob> CREATOR
          = new Parcelable.Creator<GreetingCardJob>()
    {
    public GreetingCardJob createFromParcel( Parcel in )
      {
      return new GreetingCardJob( in );
      }

    public GreetingCardJob[] newArray( int size )
      {
      return new GreetingCardJob[ size ];
      }
    };


  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( !( otherObject instanceof GreetingCardJob ) ) ) return ( false );

    GreetingCardJob otherGreetingCardJob = (GreetingCardJob) otherObject;

    if ( ! UploadableImage.areBothNullOrEqual( mFrontUploadableImage,       otherGreetingCardJob.mFrontUploadableImage       ) ) return ( false );
    if ( ! UploadableImage.areBothNullOrEqual( mBackUploadableImage,        otherGreetingCardJob.mBackUploadableImage        ) ) return ( false );
    if ( ! UploadableImage.areBothNullOrEqual( mInsideLeftUploadableImage,  otherGreetingCardJob.mInsideLeftUploadableImage  ) ) return ( false );
    if ( ! UploadableImage.areBothNullOrEqual( mInsideRightUploadableImage, otherGreetingCardJob.mInsideRightUploadableImage ) ) return ( false );

    return ( super.equals( otherObject ) );
    }

  }
