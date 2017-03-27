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

  static private final int     REQUEST_CODE_LOGIN                    = 37;

  static private final String  MEDIA_URL_ENDPOINT                    = "https://api.instagram.com/v1/users/self/media/recent";


  static private final String  PERMISSION_USER_PHOTOS                = "user_photos";

  static private final String  GRAPH_PATH_MY_ALBUMS                  = "/me/albums";
  static private final String  GRAPH_PATH_FORMAT_STRING_ALBUM_PHOTOS = "/%s/photos";
  static private final String  GRAPH_PATH_FORMAT_STRING_PHOTO        = "/%s";

  static private final String  PARAMETER_NAME_TYPE                   = "type";
  static private final String  PARAMETER_VALUE_TYPE                  = "uploaded";

  static private final String  PARAMETER_NAME_FIELDS                 = "fields";
  static private final String  PARAMETER_VALUE_ALBUM_FIELDS          = "id,name,cover_photo";
  static private final String  PARAMETER_VALUE_PHOTO_FIELDS          = "id,picture,images";

  static private final String  JSON_NAME_DATA                        = "data";
  static private final String  JSON_NAME_ID                          = "id";
  static private final String  JSON_NAME_COVER_PHOTO                 = "cover_photo";
  static private final String  JSON_NAME_NAME                        = "name";
  static private final String  JSON_NAME_PICTURE                     = "picture";
  static private final String  JSON_NAME_IMAGES                      = "images";

  static private final String  JSON_NAME_WIDTH                       = "width";
  static private final String  JSON_NAME_HEIGHT                      = "height";
  static private final String  JSON_NAME_SOURCE                      = "source";

//  static private final String  HTTP_HEADER_NAME_AUTHORISATION        = "Authorization";
//  static private final String  HTTP_AUTHORISATION_FORMAT_STRING      = "Bearer %s";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Activity         mActivity;
  private String           mClientId;
  private String           mRedirectUri;

  private String           mNextPhotosPageRequestURL;

  private ARequest         mPendingRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public InstagramAgent getInstance( Activity activity, String clientId, String redirectUri )
    {
    // We don't cache the instance, because we don't want to hold
    // onto the activity. The activity we use always needs to be the
    // current one, otherwise subsequent re-log-ins can fail.

    return ( new InstagramAgent( activity, clientId, redirectUri ) );
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

  private InstagramAgent( Activity activity, String clientId, String redirectUri )
    {
    mActivity    = activity;
    mClientId    = clientId;
    mRedirectUri = redirectUri;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when an activity returns a result.
   *
   *****************************************************/
  void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( requestCode == REQUEST_CODE_LOGIN )
      {

      if ( resultCode == Activity.RESULT_OK )
        {
        String accessToken = InstagramLoginActivity.getAccessToken( data );

        newAccessToken( accessToken );
        }
      }

    // TODO
    }


  /*****************************************************
   *
   * Processes a new access token.
   *
   *****************************************************/
  private void newAccessToken( String accessToken )
    {
    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "--> newAccessToken( accessToken = " + accessToken + " )" );
      Log.d( LOG_TAG, "mPendingRequest = " + mPendingRequest );
      }

    if ( mPendingRequest != null  )
      {
      ARequest pendingRequest = mPendingRequest;

      mPendingRequest = null;

      pendingRequest.onExecute();
      }


    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- newAccessToken( accessToken )" );

    }


  /*****************************************************
   *
   * Loads an initial set of photos.
   *
   *****************************************************/
  private void executeRequest( ARequest request, boolean checkForAccessToken )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> executeRequest( request = " + request + ", checkForAccessToken = " + checkForAccessToken + " )" );


    if ( checkForAccessToken )
      {
      // If we don't have an access token - make a log-in request.

      String accessToken = getAccessToken( mActivity );

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "accessToken = " + accessToken );

      if ( accessToken == null )
        {
        InstagramLoginActivity.startLoginForResult( mActivity, mClientId, mRedirectUri, REQUEST_CODE_LOGIN );

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );

        return;
        }
      }


    // Either we don't need or already have a valid access token, so execute the request
    request.onExecute();

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );
    }


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
  void getPhotos( ICallback callback )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhotos( photosCallback )" );

    PhotosRequest photosRequest = new PhotosRequest( callback );

    executeRequest( photosRequest, true );
    }


//  /*****************************************************
//   *
//   * Loads a single photo.
//   *
//   *****************************************************/
//  void getPhoto( String photoId, ImageView targetImageView )
//    {
//    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhoto( photoId = " + photoId + ", targetImageView )" );
//
//    PhotoRequest photoRequest = new PhotoRequest( photoId, targetImageView );
//
//    executeRequest( photoRequest, false );
//    }


  /*****************************************************
   *
   * Returns a photo from the supplied JSON.
   *
   *****************************************************/
  private InstagramPhoto photoFromJSON( JSONObject photoJSONObject ) throws JSONException
    {
    String    id             = photoJSONObject.getString( JSON_NAME_ID );
    String    picture        = photoJSONObject.getString( JSON_NAME_PICTURE );
    JSONArray imageJSONArray = photoJSONObject.getJSONArray( JSON_NAME_IMAGES );  // "The different stored representations of the photo. Can vary in number based upon the size of the original photo."

    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "-- Photo --" );
      Log.d( LOG_TAG, "Id      : " + id );
      Log.d( LOG_TAG, "Picture : " + picture );  // "Link to the 100px wide representation of this photo"
      }


    try
      {
      // Create a new photo, and add the (thumbnail) picture
      InstagramPhoto photo = new InstagramPhoto( id, picture, 100 );


      // Add the remaining images

      int imageCount = imageJSONArray.length();

      for ( int imageIndex = 0; imageIndex < imageCount; imageIndex++ )
        {
        JSONObject imageJSONObject = imageJSONArray.getJSONObject( imageIndex );

        String imageSourceURLString = imageJSONObject.getString( JSON_NAME_SOURCE );
        int    width                = imageJSONObject.optInt( JSON_NAME_WIDTH, InstagramPhoto.Image.UNKNOWN_DIMENSION );
        int    height               = imageJSONObject.optInt( JSON_NAME_HEIGHT, InstagramPhoto.Image.UNKNOWN_DIMENSION );

        if ( imageSourceURLString != null )
          {
          photo.addImage( imageSourceURLString, width, height );
          }
        }

      return ( photo );
      }
    catch ( MalformedURLException mue )
      {
      Log.e( LOG_TAG, "Invalid URL in JSON: " + photoJSONObject.toString(), mue );
      }

    return ( null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A callback interface.
   *
   *****************************************************/
  public interface ICallback
    {
    public void iaOnError( Exception exception );
    public void iaOnCancel();
    public void iaOnPhotosSuccess( List<InstagramPhoto> photoList, boolean morePhotos );
    }


  /*****************************************************
   *
   * A request.
   *
   *****************************************************/
  private abstract class ARequest<T extends ICallback>
    {
    T  mCallback;


    ARequest( T callback )
      {
      mCallback = callback;
      }

    ARequest()
      {
      this( null );
      }


    abstract void onExecute();


    void onError( Exception exception )
      {
      if ( mCallback != null ) mCallback.iaOnError( exception );
      }


    void onCancel()
      {
      if ( mCallback != null ) mCallback.iaOnCancel();
      }
    }


  /*****************************************************
   *
   * A photos request.
   *
   *****************************************************/
  private class PhotosRequest extends ARequest<ICallback>
    {
    PhotosRequest( ICallback callback )
      {
      super( callback );
      }


    @Override
    public void onExecute()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> PhotosRequest.onExecute()" );

      new MediaRequestTask( mCallback ).execute();

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotosRequest.onExecute()" );
      }
    }


//  /*****************************************************
//   *
//   * An album photo request.
//   *
//   *****************************************************/
//  private class PhotoRequest extends ARequest<ICallback>
//    {
//    private String     mPhotoId;
//    private ImageView  mTargetImageView;
//
//
//    PhotoRequest( String photoId, ImageView targetImageView )
//      {
//      super();
//
//      mPhotoId         = photoId;
//      mTargetImageView = targetImageView;
//      }
//
//
//    @Override
//    public void onExecute()
//      {
//      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> PhotoRequest.onExecute()" );
//
//
//      PhotoGraphRequestCallback photoGraphRequestCallback = new PhotoGraphRequestCallback( mPhotoId, mTargetImageView );
//
//      String graphPathPhoto = String.format( GRAPH_PATH_FORMAT_STRING_PHOTO, mPhotoId );
//
//      Bundle parameters = new Bundle();
//      parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_PHOTO_FIELDS );
//
//      GraphRequest request = new GraphRequest(
//              AccessToken.getCurrentAccessToken(),
//              graphPathPhoto,
//              parameters,
//              HttpMethod.GET,
//              photoGraphRequestCallback );
//
//      request.executeAsync();
//
//
//      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotoRequest.onExecute()" );
//      }
//    }
//
//
//  /*****************************************************
//   *
//   * A graph request callback for photos.
//   *
//   *****************************************************/
//  private class PhotosGraphRequestCallback implements GraphRequest.Callback
//    {
//    private Album      mAlbum;
//    private ICallback  mPhotosCallback;
//
//
//    PhotosGraphRequestCallback( Album album, ICallback callback )
//      {
//      mAlbum          = album;
//      mPhotosCallback = callback;
//      }
//
//
//    @Override
//    public void onCompleted( GraphResponse graphResponse )
//      {
//      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "PhotosGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );
//
//
//      // Check for error
//
//      FacebookRequestError error = graphResponse.getError();
//
//      if ( error != null )
//        {
//        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );
//
//        switch ( error.getCategory() )
//          {
//          case LOGIN_RECOVERABLE:
//
//            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );
//
//            mPendingRequest = new PhotosRequest( mAlbum, mPhotosCallback );
//
//            LoginManager.getInstance().resolveError( mActivity, graphResponse );
//
//            return;
//
//          case TRANSIENT:
//
//            getPhotos( mAlbum, mPhotosCallback );
//
//            return;
//
//          case OTHER:
//
//            // Fall through
//          }
//
//        if ( mPhotosCallback != null ) mPhotosCallback.facOnError( error.getException() );
//
//        return;
//        }
//
//
//      // Check for data
//
//      JSONObject responseJSONObject = graphResponse.getJSONObject();
//
//      if ( responseJSONObject != null )
//        {
//        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );
//
//        // Returned image data is as follows:
//        //
//        //      {
//        //        "data":
//        //          [
//        //            {
//        //            "id":"127137810981327",
//        //            "link":"https:\/\/www.facebook.com\/photo.php?fbid=127137810981327&set=a.127137917647983.1073741826.100010553266947&type=3",
//        //            "picture":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/s130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=28cc43a422b5a6af600cf69383ead821&oe=57D436FB",
//        //            "images":
//        //              [
//        //                {
//        //                "height":2048,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/12240189_127137810981327_132541351271856743_o.jpg",
//        //                "width":1536
//        //                },
//        //                {
//        //                "height":1280,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/q86\/p960x960\/12240189_127137810981327_132541351271856743_o.jpg",
//        //                "width":960
//        //                },
//        //                {
//        //                "height":960,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-9\/12189788_127137810981327_132541351271856743_n.jpg?oh=70a79bd7db8038ba1bddb6571f44f204&oe=57D20748",
//        //                "width":720
//        //                },
//        //                {
//        //                "height":800,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-0\/q81\/p600x600\/12240189_127137810981327_132541351271856743_o.jpg",
//        //                "width":600
//        //                },
//        //                {
//        //                "height":640,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/q81\/p480x480\/12189788_127137810981327_132541351271856743_n.jpg?oh=df73c06e98f6fdf144ed52032b7c284c&oe=57CE9AB1",
//        //                "width":480
//        //                },
//        //                {
//        //                "height":426,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p320x320\/12189788_127137810981327_132541351271856743_n.jpg?oh=a93025d1656980ef03778b6e65f8e3ee&oe=57DAEDDE",
//        //                "width":320
//        //                },
//        //                {
//        //                "height":540,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p180x540\/12189788_127137810981327_132541351271856743_n.jpg?oh=f727d706ac924e214bdd1e113546acb2&oe=57D86FA8",
//        //                "width":405
//        //                },
//        //                {
//        //                "height":173,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=7e3705aa4673ef25aba315198bd81d7c&oe=57DF914E",
//        //                "width":130
//        //                },
//        //                {
//        //                "height":225,
//        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p75x225\/12189788_127137810981327_132541351271856743_n.jpg?oh=fe6e37ebef0d7813a5e0b5f9c16490ce&oe=57DAD07C",
//        //                "width":168
//        //                }
//        //              ]
//        //            },
//        //            ... <next photo> ...
//
//        JSONArray dataJSONArray = responseJSONObject.optJSONArray( JSON_NAME_DATA );
//
//        if ( dataJSONArray != null )
//          {
//          ArrayList<InstagramPhoto> photoArrayList = new ArrayList<>( dataJSONArray.length() );
//
//          for ( int photoIndex = 0; photoIndex < dataJSONArray.length(); photoIndex ++ )
//            {
//            try
//              {
//              JSONObject photoJSONObject = dataJSONArray.getJSONObject( photoIndex );
//
//              InstagramPhoto photo = photoFromJSON( photoJSONObject );
//
//              if ( photo != null )
//                {
//                photoArrayList.add( photo );
//                }
//              }
//            catch ( JSONException je )
//              {
//              Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
//              }
//            }
//
//          mNextPhotosPageRequestURL = graphResponse.getRequestForPagedResults( GraphResponse.PagingDirection.NEXT );
//
//          if (mPhotosCallback != null ) mPhotosCallback.facOnPhotosSuccess( photoArrayList, mNextPhotosPageRequestURL != null );
//          }
//        else
//          {
//          Log.e( LOG_TAG, "No data found in JSON response: " + responseJSONObject );
//          }
//        }
//      else
//        {
//        Log.e( LOG_TAG, "No JSON found in graph response" );
//        }
//
//      }
//
//    }
//
//
//  /*****************************************************
//   *
//   * A graph request callback for a photo.
//   *
//   *****************************************************/
//  private class PhotoGraphRequestCallback implements GraphRequest.Callback
//    {
//    private String     mPhotoId;
//    private ImageView  mTargetImageView;
//
//
//    PhotoGraphRequestCallback( String photoId, ImageView targetImageView )
//      {
//      mPhotoId         = photoId;
//      mTargetImageView = targetImageView;
//      }
//
//
//    @Override
//    public void onCompleted( GraphResponse graphResponse )
//      {
//      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "PhotoGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );
//
//
//      // Check for error
//
//      FacebookRequestError error = graphResponse.getError();
//
//      if ( error != null )
//        {
//        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );
//
//        switch ( error.getCategory() )
//          {
//          case LOGIN_RECOVERABLE:
//
//            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );
//
//            LoginManager.getInstance().resolveError( mActivity, graphResponse );
//
//            return;
//
//          case TRANSIENT:
//
//            getPhoto( mPhotoId, mTargetImageView );
//
//            return;
//
//          case OTHER:
//
//            // Fall through
//          }
//
//        return;
//        }
//
//
//      // Check for data
//
//      JSONObject responseJSONObject = graphResponse.getJSONObject();
//
//      if ( responseJSONObject != null )
//        {
//        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );
//
//        try
//          {
//          InstagramPhoto photo = photoFromJSON( responseJSONObject );
//
//          if ( photo != null )
//            {
//            photo.loadThumbnailImageInto( mActivity, mTargetImageView );
//            }
//          }
//        catch ( JSONException je )
//          {
//          Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
//          }
//        }
//      else
//        {
//        Log.e( LOG_TAG, "No JSON found in graph response" );
//        }
//
//      }
//
//    }


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

        getPhotos( mCallback );
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
      final ArrayList<InstagramPhoto> photos = new ArrayList<>();

      JSONArray data = json.getJSONArray( "data" );
      for ( int i = 0; i < data.length(); ++i )
        {
        try
          {
          JSONObject photoJSON = data.getJSONObject( i );

          String id = photoJSON.getString( "id" );

          JSONObject images = photoJSON.getJSONObject( "images" );

          JSONObject thumbnail     = images.getJSONObject( "thumbnail" );
          JSONObject lowResolution = images.getJSONObject( "low_resolution" );
          JSONObject standard      = images.getJSONObject( "standard_resolution" );

          String thumbnailURL     = adjustedURL( thumbnail.getString( "url" ) );
          int    thumbnailWidth   = thumbnail.getInt( "width" );
          int    thumbnailHeight  = thumbnail.getInt( "height " );

          String lowResolutionURL = adjustedURL( lowResolution.getString( "url" ) );
          int    lowResWidth      = lowResolution.getInt( "width" );
          int    lowResHeight     = lowResolution.getInt( "height " );

          String standardURL      = adjustedURL( standard.getString( "url" ) );
          int    standardWidth    = standard.getInt( "width" );
          int    standardHeight   = standard.getInt( "height " );

          // We use the low resolution image for the picking; the thumbnail image is too
          // low resolution for larger devices.
          InstagramPhoto photo = new InstagramPhoto( id );
          photo.addImage( thumbnailURL, thumbnailWidth, thumbnailHeight );
          photo.addImage( lowResolutionURL, lowResWidth, lowResHeight );
          photo.addImage( standardURL, standardWidth, standardHeight );

          photos.add( photo );
          }
        catch ( Exception ex )
          { /* ignore */ }
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
      JSONObject pagination = json.getJSONObject( "pagination" );
      String nextPageURL = pagination.optString( "next_url", null );
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
      v = v * 31 + mThumbnailImage.hashCode();
      v = v * 31 + mLargestImage.hashCode();
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