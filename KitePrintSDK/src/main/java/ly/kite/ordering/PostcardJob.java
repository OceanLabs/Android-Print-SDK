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
import ly.kite.util.AssetFragment;
import ly.kite.util.StringUtils;
import ly.kite.util.UploadableImage;

/**
 * Created by alibros on 16/01/15.
 */

///// Class Declaration /////

/*****************************************************
 *
 * This class represents a postcard job.
 *
 *****************************************************/
public class PostcardJob extends Job
  {
  private UploadableImage  mFrontUploadableImage;
  private UploadableImage  mBackUploadableImage;
  private String           mMessage;
  private Address          mAddress;


  public PostcardJob( long jobId, Product product, int orderQuantity, HashMap<String, String> optionsMap, Object frontImage, Object backImage, String message, Address address )
    {
    super( jobId, product, orderQuantity, optionsMap );

    mFrontUploadableImage = singleUploadableImageFrom( frontImage );
    mBackUploadableImage  = singleUploadableImageFrom( backImage );
    mMessage              = message;
    mAddress              = address;
    }

  public PostcardJob( Product product, int orderQuantity, HashMap<String, String> optionsMap, Object frontImage, Object backImage, String message, Address address )
    {
    this( 0, product, orderQuantity, optionsMap, frontImage, backImage, message, address );
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


  @Override
  List<UploadableImage> getImagesForUploading()
    {
    List<UploadableImage> uploadableImageList = new ArrayList<>();

    uploadableImageList.add( mFrontUploadableImage );

    if ( mBackUploadableImage != null )
      {
      uploadableImageList.add( mBackUploadableImage );
      }

    return ( uploadableImageList );
    }


  private static String getStringOrEmptyString( String val )
    {
    return val == null ? "" : val;
    }

  private JSONObject getJSON() throws JSONException
    {
    JSONObject json = new JSONObject();
    json.put( "template_id", getProductId() );

    addProductOptions( json );

    JSONObject assets = new JSONObject();
    json.put( "assets", assets );
    assets.put( "front_image", mFrontUploadableImage.getUploadedAssetId() );

    if ( mBackUploadableImage != null )
      {
      assets.put( "back_image", mBackUploadableImage.getUploadedAssetId() );
      }

    if ( mMessage != null )
      {
      json.put( "message", mMessage );
      }


    if ( mAddress != null )
      {
      JSONObject shippingAddr = new JSONObject();
      json.put( "shipping_address", shippingAddr );

      shippingAddr.put( "recipient_name", getStringOrEmptyString( mAddress.getRecipientName() ) );
      shippingAddr.put( "address_line_1", getStringOrEmptyString( mAddress.getLine1() ) );
      shippingAddr.put( "address_line_2", getStringOrEmptyString( mAddress.getLine2() ) );
      shippingAddr.put( "city", getStringOrEmptyString( mAddress.getCity() ) );
      shippingAddr.put( "county_state", getStringOrEmptyString( mAddress.getStateOrCounty() ) );
      shippingAddr.put( "postcode", getStringOrEmptyString( mAddress.getZipOrPostalCode() ) );
      shippingAddr.put( "country_code", getStringOrEmptyString( mAddress.getCountry().iso3Code() ) );
      }

    return json;
    }

  @Override
  JSONObject getJSONRepresentation()
    {
    try
      {
      return getJSON();
      }
    catch ( JSONException ex )
      {
      throw new RuntimeException( ex );
      }
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
    parcel.writeString( mMessage );
    parcel.writeParcelable( mAddress, flags );
    }

  private PostcardJob( Parcel parcel )
    {
    //super( ProductCache.getDirtyInstance().findProductById( parcel.readString() ) );
    super( parcel );

    mFrontUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    mBackUploadableImage = parcel.readParcelable( AssetFragment.class.getClassLoader() );
    mMessage                 = parcel.readString();
    mAddress                 = (Address)parcel.readParcelable( Address.class.getClassLoader() );
    }

  public static final Parcelable.Creator<PostcardJob> CREATOR
          = new Parcelable.Creator<PostcardJob>()
  {
  public PostcardJob createFromParcel( Parcel in )
    {
    return new PostcardJob( in );
    }

  public PostcardJob[] newArray( int size )
    {
    return new PostcardJob[ size ];
    }
  };


  public String getMessage()
    {
    return ( mMessage );
    }

  public Address getAddress()
    {
    return ( mAddress );
    }


  @Override
  public boolean equals( Object otherObject )
    {
    if ( otherObject == null || ( !( otherObject instanceof PostcardJob ) ) ) return ( false );

    PostcardJob otherPostcardJob = (PostcardJob)otherObject;

    String      otherMessage     = otherPostcardJob.mMessage;
    Address     otherAddress     = otherPostcardJob.mAddress;

    if ( ! UploadableImage.areBothNullOrEqual( mFrontUploadableImage, otherPostcardJob.mFrontUploadableImage ) ) return ( false );
    if ( ! UploadableImage.areBothNullOrEqual( mBackUploadableImage,  otherPostcardJob.mBackUploadableImage ) ) return ( false );

    if ( ! StringUtils.areBothNullOrEqual( mMessage, otherMessage ) ) return ( false );

    if ( ! Address.areBothNullOrEqual( mAddress, otherAddress ) ) return ( false );

    return ( super.equals( otherObject ) );
    }

  }
