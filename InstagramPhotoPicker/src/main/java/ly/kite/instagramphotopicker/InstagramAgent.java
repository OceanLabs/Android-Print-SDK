/*****************************************************
 *
 * InstagramAgent.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2017 Kite Tech Ltd. https://www.kite.ly
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The software MAY ONLY be used with the Kite Tech Ltd platform and MAY NOT be modified
 * to be used with any competitor platforms. This means the software MAY NOT be modified 
 * to place orders with any competitors to Kite Tech Ltd, all orders MUST go through the
 * Kite Tech Ltd platform servers. 
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *****************************************************/

///// Package Declaration /////

package ly.kite.instagramphotopicker;


///// Import(s) /////

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.squareup.picasso.Picasso;

import ly.kite.imagepicker.IImagePickerItem;
import ly.kite.imagepicker.ISelectableItem;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an agent for the Instagram APIs.
 *
 *****************************************************/
public class InstagramAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String  LOG_TAG                               = "InstagramAgent";

  static private final boolean DEBUGGING_ENABLED                     = false;

  static private final String  SHARED_PREFERENCES_NAME               = "instagram_prefs";
  static private final String  PREFERENCE_KEY_ACCESS_TOKEN           = "access_token";

  static private final String  MEDIA_URL_ENDPOINT                    = "https://api.instagram.com/v1/users/self/media/recent";


  static private final String  JSON_NAME_DATA                        = "data";
  static private final String  JSON_NAME_ID                          = "id";
  static private final String  JSON_NAME_IMAGES                      = "images";
  static private final String  JSON_NAME_THUMBNAIL                   = "thumbnail";
  static private final String  JSON_NAME_LOW_RESOLUTION              = "low_resolution";
  static private final String  JSON_NAME_STANDARD_RESOLUTION         = "standard_resolution";
  static private final String  JSON_NAME_PAGINATION                  = "pagination";
  static private final String  JSON_NAME_NEXT_URL                    = "next_url";

  static private final String  JSON_NAME_URL                         = "url";
  static private final String  JSON_NAME_WIDTH                       = "width";
  static private final String  JSON_NAME_HEIGHT                      = "height";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity         mActivity;
  private String           mClientId;
  private String           mRedirectUri;
  private ICallback        mCallback;

  private String           mNextPhotosPageRequestURL;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public InstagramAgent getInstance( Activity activity, String clientId, String redirectUri, ICallback callback )
    {
    // We don't cache the instance, because we don't want to hold
    // onto the activity. The activity we use always needs to be the
    // current one, otherwise subsequent re-log-ins can fail.

    return ( new InstagramAgent( activity, clientId, redirectUri, callback ) );
    }


  /*****************************************************
   *
   * Saves an access token.
   *
   *****************************************************/
  static void saveAccessToken( Context context, String accessToken )
    {
    SharedPreferences sharedPreferences = context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

    sharedPreferences
            .edit()
              .putString( PREFERENCE_KEY_ACCESS_TOKEN, accessToken )
            .commit();
    }


  /*****************************************************
   *
   * Returns the access token, if we have one, null otherwise.
   *
   *****************************************************/
  static String getAccessToken( Context context )
    {
    SharedPreferences sharedPreferences = context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

    String accessToken = sharedPreferences.getString( PREFERENCE_KEY_ACCESS_TOKEN, null );

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "accessToken = " + accessToken );

    if ( accessToken == null || accessToken.trim().equals( "" ) )
      {
      return ( null );
      }

    return ( accessToken );
    }


  /*****************************************************
   *
   * Returns true if we have an access token.
   *
   *****************************************************/
  static boolean haveAccessToken( Context context )
    {
    return ( getAccessToken( context ) != null );
    }


  /*****************************************************
   *
   * Clears the access token.
   *
   *****************************************************/
  static void clearAccessToken( Context context )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> logOut()" );

    SharedPreferences sharedPreferences = context.getSharedPreferences( SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE );

    sharedPreferences.edit().clear().commit();
    }


  ////////// Constructor(s) //////////

  private InstagramAgent( Activity activity, String clientId, String redirectUri, ICallback callback )
    {
    mActivity    = activity;
    mClientId    = clientId;
    mRedirectUri = redirectUri;
    mCallback    = callback;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Clears any next page request, so photos are retrieved
   * from the start.
   *
   *****************************************************/
  void resetPhotos()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "resetPhotos()" );

    mNextPhotosPageRequestURL = null;
    }


  /*****************************************************
   *
   * Loads the next available page of photos.
   *
   *****************************************************/
  void getPhotos()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhotos( photosCallback )" );

    new MediaRequestTask( mCallback ).execute();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void iaRestart();
    public void iaOnError( Exception exception );
    public void iaOnCancel();
    public void iaOnPhotosSuccess( List<InstagramPhoto> photoList, boolean morePhotos );
    }


  /*****************************************************
   *
   * A background task that requests recent media.
   *
   *****************************************************/
  private class MediaRequestTask extends AsyncTask<Void, Void, Void>
    {
    private ICallback             mCallback;

    private int                   mHTTPStatusCode;
    private Exception             mException;
    private List<InstagramPhoto>  mPhotoList;


    MediaRequestTask( ICallback callback )
      {
      mCallback = callback;
      }


    @Override
    protected Void doInBackground( Void... voids )
      {
      String urlString = ( mNextPhotosPageRequestURL != null ? mNextPhotosPageRequestURL : MEDIA_URL_ENDPOINT );

      if ( ! urlString.contains( "access_token" ) )
        {
        urlString += "?access_token=" + getAccessToken( mActivity );
        }

      if ( ! urlString.contains( "&count=" ) )
        {
        urlString += "&count=33";
        }

      HttpClient httpclient = new DefaultHttpClient();
      HttpGet    request    = new HttpGet( urlString );

      try
        {
        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "Executing query: " + urlString );

        HttpResponse response = httpclient.execute( request );
        BufferedReader reader = new BufferedReader( new InputStreamReader( response.getEntity().getContent(), "UTF-8" ) );
        StringBuilder builder = new StringBuilder();
        for ( String line = null; ( line = reader.readLine() ) != null; )
          {
          builder.append( line ).append( "\n" );
          }

        JSONTokener t = new JSONTokener( builder.toString() );
        JSONObject json = new JSONObject( t );
        mHTTPStatusCode = response.getStatusLine().getStatusCode();

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "Status code = " + mHTTPStatusCode );

        if ( mHTTPStatusCode == 400 || mHTTPStatusCode == 401 )
          {
          }
        else if ( mHTTPStatusCode != 200 )
          {
          // TODO
          }
        else
          {
          mPhotoList                = parsePhotosFromResponseJSON( json );
          mNextPhotosPageRequestURL = parseNextPageRequestFromResponseJSON( json );

          if ( DEBUGGING_ENABLED )
            {
            Log.d( LOG_TAG, "Number of photos returned    : " + mPhotoList.size() );
            Log.d( LOG_TAG, "Next photos page request URL : " + mNextPhotosPageRequestURL );
            }
          }

        }
      catch ( Exception exception )
        {
        mException = exception;
        }

      return ( null );
      }

    @Override
    protected void onPostExecute( Void voidResult )
      {
      if ( mException != null )
        {
        mCallback.iaOnError( mException );
        }
      else if ( mHTTPStatusCode == 400 || mHTTPStatusCode == 401 )
        {
        // The access token is invalid - reset everything and start again

        clearAccessToken( mActivity );

        resetPhotos();

        mCallback.iaRestart();
        }
      else if ( mHTTPStatusCode != 200 )
        {
        }
      else
        {
        mCallback.iaOnPhotosSuccess( mPhotoList, mNextPhotosPageRequestURL != null );
        }
      }


    /*****************************************************
     *
     * Parses an Instagram media response, and returns a list
     * of photos.
     *
     *****************************************************/
    private List<InstagramPhoto> parsePhotosFromResponseJSON( JSONObject json ) throws JSONException
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "parsePhotosFromResponseJSON( json = " + json.toString() + " )" );

      final ArrayList<InstagramPhoto> photos = new ArrayList<>();

      JSONArray data = json.getJSONArray( JSON_NAME_DATA );

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "Found " + data.length() + " image(s)" );

      for ( int i = 0; i < data.length(); ++i )
        {
        try
          {
          JSONObject photoJSON = data.getJSONObject( i );

          String id = photoJSON.getString( JSON_NAME_ID );

          JSONObject images = photoJSON.getJSONObject( JSON_NAME_IMAGES );

          JSONObject thumbnail     = images.getJSONObject( JSON_NAME_THUMBNAIL );
          JSONObject lowResolution = images.getJSONObject( JSON_NAME_LOW_RESOLUTION );
          JSONObject standard      = images.getJSONObject( JSON_NAME_STANDARD_RESOLUTION );

          String thumbnailURL     = adjustedURL( thumbnail.getString( JSON_NAME_URL ) );
          int    thumbnailWidth   = thumbnail.getInt( JSON_NAME_WIDTH );
          int    thumbnailHeight  = thumbnail.getInt( JSON_NAME_WIDTH );

          String lowResolutionURL = adjustedURL( lowResolution.getString( JSON_NAME_URL ) );
          int    lowResWidth      = lowResolution.getInt( JSON_NAME_WIDTH );
          int    lowResHeight     = lowResolution.getInt( JSON_NAME_HEIGHT );

          String standardURL      = adjustedURL( standard.getString( JSON_NAME_URL ) );
          int    standardWidth    = standard.getInt( JSON_NAME_WIDTH );
          int    standardHeight   = standard.getInt( JSON_NAME_HEIGHT );

          if ( DEBUGGING_ENABLED )
            {
            Log.d( LOG_TAG, "Thumbnail      : " + thumbnailURL );
            Log.d( LOG_TAG, "Low resolution : " + lowResolutionURL );
            Log.d( LOG_TAG, "Standard       : " + standardURL );
            }

          // We use the low resolution image for the picking; the thumbnail image is too
          // low resolution for larger devices.
          InstagramPhoto photo = new InstagramPhoto( id );
          photo.addImage( thumbnailURL, thumbnailWidth, thumbnailHeight );
          photo.addImage( lowResolutionURL, lowResWidth, lowResHeight );
          photo.addImage( standardURL, standardWidth, standardHeight );

          photos.add( photo );
          }
        catch ( Exception exception )
          {
          Log.e( LOG_TAG, "Unable to get images", exception );
          }
        }

      return photos;
      }


    private String adjustedURL( String originalURL )
      {
      if ( originalURL.startsWith( "http://" ) ) return ( originalURL.replace( "http://", "https://" ) );

      return ( originalURL );
      }


    private String parseNextPageRequestFromResponseJSON( JSONObject json ) throws JSONException
      {
      JSONObject pagination = json.getJSONObject( JSON_NAME_PAGINATION );
      String nextPageURL = pagination.optString( JSON_NAME_NEXT_URL, null );
      return nextPageURL;
      }

    }


  /*****************************************************
   *
   * A selectable image.
   *
   *****************************************************/
  static public class SelectableImage implements ISelectableItem
    {

    ////////// Static Variable(s) //////////

    public static final Creator CREATOR = new Creator()
      {
      public SelectableImage createFromParcel( Parcel in )
        {
        return new SelectableImage( in );
        }

      public SelectableImage[] newArray( int size )
        {
        return new SelectableImage[ size ];
        }
      };


    ////////// Member Variable(s) //////////

    private String  mId;
    private String  mURLString;


    ////////// Constructor(s) //////////

    SelectableImage( String id, String urlString )
      {
      mId        = id;
      mURLString = urlString;
      }


    SelectableImage( Parcel sourceParcel )
      {
      mId        = sourceParcel.readString();
      mURLString = sourceParcel.readString();
      }


    ////////// Parcelable Method(s) //////////

    @Override
    public int describeContents()
      {
      return ( 0 );
      }

    @Override
    public void writeToParcel( Parcel targetParcel, int flags )
      {
      targetParcel.writeString( mId );
      targetParcel.writeString( mURLString );
      }


    ////////// ISelectableItem Method(s) //////////

    @Override
    public String getKey()
      {
      return ( mId );
      }

    @Override
    public String getImageURLString()
      {
      return ( mURLString );
      }
    }


  /*****************************************************
   *
   * This class represents an Instagram photo.
   *
   *****************************************************/
  public class InstagramPhoto implements IImagePickerItem
    {
    ////////// Static Constant(s) //////////

    @SuppressWarnings("unused")
    static private final String  LOG_TAG           = "InstagramPhoto";

    static private final boolean DEBUGGING_ENABLED = true;


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////

    private String       mId;
    private List<Image>  mImageList;
    private Image        mThumbnailImage;
    private Image        mLargestImage;


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////


    ////////// Constructor(s) //////////

    public InstagramPhoto( String id )
      {
      mId        = id;
      mImageList = new ArrayList<>();
      }

    public InstagramPhoto( String id, String thumbnailURLString, int width ) throws MalformedURLException
      {
      this( id );

      mThumbnailImage = addImage( thumbnailURLString, width );
      }


    ////////// IImagePickerItem Method(s) //////////

    @Override
    public String getImageURLString()
      {
      return ( mLargestImage.getSourceURL().toString() );
      }

    @Override
    public void loadThumbnailImageInto( Context context, ImageView imageView )
      {
      if ( imageView == null ) return;

      URL bestImageURL = getBestImageURL( imageView.getWidth(), imageView.getHeight() );

      Picasso.with( context )
              .load( bestImageURL.toString() )
              .resizeDimen( R.dimen.ip_image_default_resize_width, R.dimen.ip_image_default_resize_height )
              .centerCrop()
              .onlyScaleDown()
              .into( imageView );
      }

    @Override
    public String getLabel()
      {
      return ( null );
      }

    @Override
    public String getKeyIfParent()
      {
      return ( null );
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return ( new SelectableImage( mId, mLargestImage.getSourceURL().toString() ) );
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String,ISelectableItem> selectableItemTable )
      {
      return ( selectableItemTable.containsKey( mId ) ? 1 : 0 );
      }


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Adds an image.
     *
     *****************************************************/
    public Image addImage( String imageURLString, int width, int height ) throws MalformedURLException
      {
      URL imageURL = new URL( imageURLString );
      Image image    = new Image( imageURL, width, height );

      mImageList.add( image );


      // If this is the first, or largest image so far, save it

      int largestImageWidth;
      int largestImageHeight;

      if ( mLargestImage == null ||
              ( width  > 0 && ( largestImageWidth  = mLargestImage.getWidth()  ) > 0 && width  > largestImageWidth  ) ||
              ( height > 0 && ( largestImageHeight = mLargestImage.getHeight() ) > 0 && height > largestImageHeight ) )
        {
        mLargestImage = image;
        }


      return ( image );
      }


    /*****************************************************
     *
     * Adds an image.
     *
     *****************************************************/
    public Image addImage( String imageSourceURLString, int width ) throws MalformedURLException
      {
      return ( addImage( imageSourceURLString, width, Image.UNKNOWN_DIMENSION ) );
      }


    /*****************************************************
     *
     * Returns the URL of the thumbnail image.
     *
     *****************************************************/
    public URL getThumbnailURL()
      {
      return ( mThumbnailImage.getSourceURL() );
      }


    /*****************************************************
     *
     * Returns the URL of the full image.
     *
     *****************************************************/
    public URL getFullURL()
      {
      return ( mLargestImage.getSourceURL() );
      }


    /*****************************************************
     *
     * Returns the URL of the image is best suited to the
     * supplied required dimensions. This will be the smallest
     * image that is larger than the dimensions.
     *
     *****************************************************/
    public URL getBestImageURL( int minWidth, int minHeight )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getBestImage( minWidth = " + minWidth + ", minHeight = " + minHeight + " )" );

      Image bestSoFarImage = null;

      for ( Image candidateImage : mImageList )
        {
        if ( bestSoFarImage == null )
          {
          bestSoFarImage = candidateImage;
          }
        else
          {
          int bestSoFarImageWidth  = bestSoFarImage.getWidth();
          int bestSoFarImageHeight = bestSoFarImage.getHeight();

          int candidateImageWidth  = candidateImage.getWidth();
          int candidateImageHeight = candidateImage.getHeight();

          if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "  Candidate image: " + candidateImage );

          boolean widthDimensionIsBetter  = dimensionIsBetter( minWidth,  bestSoFarImageWidth,  candidateImageWidth );
          boolean heightDimensionIsBetter = dimensionIsBetter( minHeight, bestSoFarImageHeight, candidateImageHeight );

          if ( minWidth < 1 && minHeight < 1 )
            {
            if ( widthDimensionIsBetter && heightDimensionIsBetter ) bestSoFarImage = candidateImage;
            }
          else
            {
            if ( minWidth < 1 )
              {
              if ( heightDimensionIsBetter ) bestSoFarImage = candidateImage;
              }
            else if ( minHeight < 1 )
              {
              if ( widthDimensionIsBetter ) bestSoFarImage = candidateImage;
              }
            else
              {
              if ( widthDimensionIsBetter && heightDimensionIsBetter ) bestSoFarImage = candidateImage;
              }
            }
          }
        }


      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "  Picked image: " + bestSoFarImage );

      return ( bestSoFarImage.getSourceURL() );
      }


    /*****************************************************
     *
     * Returns true, if the dimensions are better for a
     * candidate image.
     *
     *****************************************************/
    private boolean dimensionIsBetter( int minValue, int bestSoFarValue, int candidateValue )
      {
      if ( minValue < 1 ) return ( candidateValue < bestSoFarValue );

      if ( bestSoFarValue < minValue ) return ( candidateValue > minValue );

      return ( candidateValue >= minValue && candidateValue < bestSoFarValue );
      }


    /*****************************************************
     *
     * Returns a hash code for this photo.
     *
     *****************************************************/
    @Override
    public int hashCode()
      {
      int v = 17;
      if ( mThumbnailImage != null ) v = v * 31 + mThumbnailImage.hashCode();
      if ( mLargestImage   != null ) v = v * 31 + mLargestImage.hashCode();
      return v;
      }


    /*****************************************************
     *
     * Returns true if this photo equals the other photo.
     *
     * As a shortcut, we just match the thumbnail image
     * and largest image.
     *
     *****************************************************/
    @Override
    public boolean equals( Object otherObject )
      {
      if ( otherObject == null ) return ( false );

      if ( otherObject == this ) return ( true );

      if ( ! ( otherObject instanceof InstagramPhoto ) )
        {
        return ( false );
        }

      InstagramPhoto otherPhoto = (InstagramPhoto)otherObject;

      return ( otherPhoto.mThumbnailImage.equals( mThumbnailImage ) && otherPhoto.mLargestImage.equals( mLargestImage ) );
      }


    ////////// Inner Class(es) //////////

    /*****************************************************
     *
     * A representation of the photo at a particular size. A
     * photo can be represented by many different sized images.
     *
     *****************************************************/
    public class Image
      {
      static public final int UNKNOWN_DIMENSION = -1;


      private URL  mSourceURL;
      private int  mWidth;
      private int  mHeight;


      public Image( URL sourceURL, int width, int height )
        {
        mSourceURL = sourceURL;
        mWidth     = width;
        mHeight    = height;
        }

      public Image( URL sourceURL, int width )
        {
        this( sourceURL, width, UNKNOWN_DIMENSION );
        }


      URL getSourceURL()
        {
        return ( mSourceURL );
        }


      int getWidth()
        {
        return ( mWidth );
        }


      int getHeight()
        {
        return ( mHeight );
        }


      @Override
      public boolean equals( Object otherObject )
        {
        if ( otherObject == null ) return ( false );

        if ( otherObject == this ) return ( true );

        if ( ! ( otherObject instanceof Image ) )
          {
          return ( false );
          }

        Image otherImage = (Image)otherObject;

        return ( otherImage.mSourceURL.equals( mSourceURL ) && otherImage.mWidth == mWidth && otherImage.mHeight == mHeight );
        }

      @Override
      public String toString()
        {
        return ( mSourceURL.toString() + " : " + mWidth + " x " + mHeight );
        }
      }

    }

  }