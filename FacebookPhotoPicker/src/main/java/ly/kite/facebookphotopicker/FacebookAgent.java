/*****************************************************
 *
 * FacebookAgent.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2015 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.facebookphotopicker;


///// Import(s) /////

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookRequestError;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import ly.kite.imagepicker.IImagePickerItem;
import ly.kite.imagepicker.ISelectableItem;


///// Class Declaration /////

/*****************************************************
 *
 * This class is an agent for the Facebook APIs.
 *
 *****************************************************/
public class FacebookAgent
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings("unused")
  static private final String  LOG_TAG                               = "FacebookAgent";

  static private final boolean DEBUGGING_ENABLED                     = true;

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
  private CallbackManager  mCallbackManager;

  private GraphRequest     mNextAlbumsPageGraphRequest;
  private GraphRequest     mNextPhotosPageGraphRequest;

  private ARequest         mPendingRequest;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this agent.
   *
   *****************************************************/
  static public FacebookAgent getInstance( Activity activity )
    {
    // We don't cache the instance, because we don't want to hold
    // onto the activity. The activity we use always needs to be the
    // current one, otherwise subsequent re-log-ins can fail.

    return ( new FacebookAgent( activity ) );
    }


  /*****************************************************
   *
   * Returns true if we are logged in to Facebook (i.e.
   * there is a valid access token).
   *
   *****************************************************/
  static boolean isLoggedIn()
    {
    AccessToken accessToken = AccessToken.getCurrentAccessToken();

    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "accessToken = " + stringFrom( accessToken ) );

    if ( accessToken == null || accessToken.getUserId() == null || accessToken.isExpired() )
      {
      return ( false );
      }

    return ( true );
    }


  /*****************************************************
   *
   * Logs out.
   *
   *****************************************************/
  static void logOut()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> logOut()" );

    LoginManager.getInstance().logOut();

    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "  Access token = " + AccessToken.getCurrentAccessToken() );
      Log.d( LOG_TAG, "<-- logOut()" );
      }
    }


  /*****************************************************
   *
   * Returns a string representation of an access token.
   *
   *****************************************************/
  static private String stringFrom( AccessToken accessToken )
    {
    if ( accessToken == null ) return ( "<null>" );

    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append( "Token          : " ).append( accessToken.getToken()         ).append( ( '\n' ) );
    stringBuilder.append( "Application Id : " ).append( accessToken.getApplicationId() ).append( ( '\n' ) );
    stringBuilder.append( "Expires        : " ).append( accessToken.getExpires()       ).append( ( '\n' ) );
    stringBuilder.append( "Last Refresh   : " ).append( accessToken.getLastRefresh()   ).append( ( '\n' ) );
    stringBuilder.append( "Source         : " ).append( accessToken.getSource()        ).append( ( '\n' ) );
    stringBuilder.append( "Permissions    : " ).append( accessToken.getPermissions()   ).append( ( '\n' ) );
    stringBuilder.append( "User Id        : " ).append( accessToken.getUserId()        ).append( ( '\n' ) );

    return ( stringBuilder.toString() );
    }


  ////////// Constructor(s) //////////

  private FacebookAgent( Context context )
    {
    }

  private FacebookAgent( Activity activity )
    {
    mActivity = activity;

    FacebookSdk.sdkInitialize( activity.getApplicationContext() );

    mCallbackManager = CallbackManager.Factory.create();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Called when an activity returns a result.
   *
   *****************************************************/
  void onActivityResult( int requestCode, int resultCode, Intent data )
    {
    if ( mCallbackManager != null ) mCallbackManager.onActivityResult( requestCode, resultCode, data );
    }


  /*****************************************************
   *
   * Processes a new access token.
   *
   *****************************************************/
  private void newAccessToken( AccessToken accessToken )
    {
    if ( DEBUGGING_ENABLED )
      {
      Log.d( LOG_TAG, "--> newAccessToken( accessToken = " + stringFrom( accessToken ) + " )" );
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

      AccessToken accessToken = AccessToken.getCurrentAccessToken();

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "accessToken = " + stringFrom( accessToken ) );

      if ( accessToken == null || accessToken.getUserId() == null )
        {
        LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback( mCallbackManager, new LoginResultCallback() );

        mPendingRequest = request;

        loginManager.logInWithReadPermissions( mActivity, Arrays.asList( PERMISSION_USER_PHOTOS ) );

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- executeRequest( request )" );

        return;
        }


      // If the access token has expired - refresh it

      if ( accessToken.isExpired() )
        {
        Log.i( LOG_TAG, "Access token has expired - refreshing" );

        mPendingRequest = request;

        AccessToken.refreshCurrentAccessTokenAsync();

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
   * Clears any next page request, so albums are retrieved
   * from the start.
   *
   *****************************************************/
  void resetAlbums()
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "resetAlbums()" );

    mNextAlbumsPageGraphRequest = null;
    }


  /*****************************************************
   *
   * Returns the albums.
   *
   *****************************************************/
  void getAlbums( ICallback callback )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getAlbums( callback )" );

    AlbumsRequest albumsRequest = new AlbumsRequest( callback );

    executeRequest( albumsRequest, true );
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

    mNextPhotosPageGraphRequest = null;
    }


  /*****************************************************
   *
   * Loads the next available page of photos.
   *
   *****************************************************/
  void getPhotos( Album album, ICallback callback )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhotos( photosCallback )" );

    PhotosRequest photosRequest = new PhotosRequest( album, callback );

    executeRequest( photosRequest, true );
    }


  /*****************************************************
   *
   * Loads a single photo.
   *
   *****************************************************/
  void getPhoto( String photoId, ImageView targetImageView )
    {
    if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "getPhoto( photoId = " + photoId + ", targetImageView )" );

    PhotoRequest photoRequest = new PhotoRequest( photoId, targetImageView );

    executeRequest( photoRequest, false );
    }


  /*****************************************************
   *
   * Returns a photo from the supplied JSON.
   *
   *****************************************************/
  private Photo photoFromJSON( JSONObject photoJSONObject ) throws JSONException
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
      Photo photo = new Photo( id, picture, 100 );


      // Add the remaining images

      int imageCount = imageJSONArray.length();

      for ( int imageIndex = 0; imageIndex < imageCount; imageIndex++ )
        {
        JSONObject imageJSONObject = imageJSONArray.getJSONObject( imageIndex );

        String imageSourceURLString = imageJSONObject.getString( JSON_NAME_SOURCE );
        int    width                = imageJSONObject.optInt( JSON_NAME_WIDTH, Photo.Image.UNKNOWN_DIMENSION );
        int    height               = imageJSONObject.optInt( JSON_NAME_HEIGHT, Photo.Image.UNKNOWN_DIMENSION );

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
    public void facOnError( Exception exception );
    public void facOnCancel();
    public void facOnAlbumsSuccess( List<Album> albumList, boolean moreAlbums );
    public void facOnPhotosSuccess( List<Photo> photoList, boolean morePhotos );
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
      if ( mCallback != null ) mCallback.facOnError( exception );
      }


    void onCancel()
      {
      if ( mCallback != null ) mCallback.facOnCancel();
      }
    }


  /*****************************************************
   *
   * An albums request.
   *
   *****************************************************/
  private class AlbumsRequest extends ARequest<ICallback>
    {
    AlbumsRequest( ICallback callback )
      {
      super( callback );
      }


    @Override
    public void onExecute()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> AlbumsRequest.onExecute()" );


      AlbumsGraphRequestCallback albumsGraphRequestCallback = new AlbumsGraphRequestCallback( mCallback );


      // If we already have a next page request ready - execute it now. Otherwise
      // start a brand new request.

      if ( mNextAlbumsPageGraphRequest != null )
        {
        mNextAlbumsPageGraphRequest.setCallback( albumsGraphRequestCallback );

        mNextAlbumsPageGraphRequest.executeAsync();

        mNextAlbumsPageGraphRequest = null;

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- AlbumsRequest.onExecute()" );

        return;
        }

      Bundle parameters = new Bundle();
      parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_ALBUM_FIELDS );

      GraphRequest request = new GraphRequest(
              AccessToken.getCurrentAccessToken(),
              GRAPH_PATH_MY_ALBUMS,
              parameters,
              HttpMethod.GET,
              albumsGraphRequestCallback );

      request.executeAsync();

      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- AlbumsRequest.onExecute()" );
      }
    }


  /*****************************************************
   *
   * An album photos request.
   *
   *****************************************************/
  private class PhotosRequest extends ARequest<ICallback>
    {
    private Album  mAlbum;


    PhotosRequest( Album album, ICallback callback )
      {
      super( callback );

      mAlbum = album;
      }


    @Override
    public void onExecute()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> PhotosRequest.onExecute()" );


      PhotosGraphRequestCallback photosGraphRequestCallback = new PhotosGraphRequestCallback( mAlbum, mCallback );


      // If we already have a next page request ready - execute it now. Otherwise
      // start a brand new request.

      if ( mNextPhotosPageGraphRequest != null )
        {
        mNextPhotosPageGraphRequest.setCallback( photosGraphRequestCallback );

        mNextPhotosPageGraphRequest.executeAsync();

        mNextPhotosPageGraphRequest = null;

        if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotosRequest.onExecute()" );

        return;
        }


      // If we have a non-null album, request its photos

      if ( mAlbum != null )
        {
        String graphPathAlbumPhotos = String.format( GRAPH_PATH_FORMAT_STRING_ALBUM_PHOTOS, mAlbum.getId() );

        Bundle parameters = new Bundle();
        parameters.putString( PARAMETER_NAME_TYPE, PARAMETER_VALUE_TYPE );
        parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_PHOTO_FIELDS );

         if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "Requesting photos from: " + graphPathAlbumPhotos );

        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                graphPathAlbumPhotos,
                parameters,
                HttpMethod.GET,
                photosGraphRequestCallback );

        request.executeAsync();
        }
      else
        {
        // Notify the callback that there are no more photos
        if ( mCallback != null ) mCallback.facOnPhotosSuccess( null, false );
        }



      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotosRequest.onExecute()" );
      }
    }


  /*****************************************************
   *
   * An album photo request.
   *
   *****************************************************/
  private class PhotoRequest extends ARequest<ICallback>
    {
    private String     mPhotoId;
    private ImageView  mTargetImageView;


    PhotoRequest( String photoId, ImageView targetImageView )
      {
      super();

      mPhotoId         = photoId;
      mTargetImageView = targetImageView;
      }


    @Override
    public void onExecute()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "--> PhotoRequest.onExecute()" );


      PhotoGraphRequestCallback photoGraphRequestCallback = new PhotoGraphRequestCallback( mPhotoId, mTargetImageView );

      String graphPathPhoto = String.format( GRAPH_PATH_FORMAT_STRING_PHOTO, mPhotoId );

      Bundle parameters = new Bundle();
      parameters.putString( PARAMETER_NAME_FIELDS, PARAMETER_VALUE_PHOTO_FIELDS );

      GraphRequest request = new GraphRequest(
              AccessToken.getCurrentAccessToken(),
              graphPathPhoto,
              parameters,
              HttpMethod.GET,
              photoGraphRequestCallback );

      request.executeAsync();


      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "<-- PhotoRequest.onExecute()" );
      }
    }


  /*****************************************************
   *
   * A login result callback.
   *
   *****************************************************/
  private class LoginResultCallback implements FacebookCallback<LoginResult>
    {
    /*****************************************************
     *
     * Called when login succeeds.
     *
     *****************************************************/
    @Override
    public void onSuccess( LoginResult loginResult )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onSuccess( loginResult = " + loginResult.toString() + " )" );

      newAccessToken( loginResult.getAccessToken() );
      }


    /*****************************************************
     *
     * Called when login is cancelled.
     *
     *****************************************************/
    @Override
    public void onCancel()
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onCancel()" );

      if ( mPendingRequest != null ) mPendingRequest.onCancel();
      }


    /*****************************************************
     *
     * Called when login fails with an error.
     *
     *****************************************************/
    @Override
    public void onError( FacebookException facebookException )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "onError( facebookException = " + facebookException + ")", facebookException );

      if ( mPendingRequest != null ) mPendingRequest.onError( facebookException );
      }
    }


  /*****************************************************
   *
   * A graph request callback for albums.
   *
   *****************************************************/
  private class AlbumsGraphRequestCallback implements GraphRequest.Callback
    {
    private ICallback mCallback;


    AlbumsGraphRequestCallback( ICallback callback )
      {
      mCallback = callback;
      }


    @Override
    public void onCompleted( GraphResponse graphResponse )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "AlbumsGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );


      // Check for error

      FacebookRequestError error = graphResponse.getError();

      if ( error != null )
        {
        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );

        switch ( error.getCategory() )
          {
          case LOGIN_RECOVERABLE:

            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );

            mPendingRequest = new AlbumsRequest( mCallback );

            LoginManager.getInstance().resolveError( mActivity, graphResponse );

            return;

          case TRANSIENT:

            getAlbums( mCallback );

            return;

          case OTHER:

            // Fall through
          }

        if ( mCallback != null ) mCallback.facOnError( error.getException() );

        return;
        }


      // Check for data

      JSONObject responseJSONObject = graphResponse.getJSONObject();

      if ( responseJSONObject != null )
        {
        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );

        JSONArray dataJSONArray = responseJSONObject.optJSONArray( JSON_NAME_DATA );

        if ( dataJSONArray != null )
          {
          ArrayList<Album> albumArrayList = new ArrayList<>( dataJSONArray.length() );

          for ( int albumIndex = 0; albumIndex < dataJSONArray.length(); albumIndex ++ )
            {
            try
              {
              JSONObject albumJSONObject = dataJSONArray.getJSONObject( albumIndex );

              Album album = albumFromJSON( albumJSONObject );

              if ( album != null )
                {
                albumArrayList.add( album );
                }
              }
            catch ( JSONException je )
              {
              Log.e( LOG_TAG, "Unable to extract album data from JSON: " + responseJSONObject.toString(), je );
              }
            }

          mNextAlbumsPageGraphRequest = graphResponse.getRequestForPagedResults( GraphResponse.PagingDirection.NEXT );

          if ( mCallback != null ) mCallback.facOnAlbumsSuccess( albumArrayList, mNextPhotosPageGraphRequest != null );
          }
        else
          {
          Log.e( LOG_TAG, "No data found in JSON response: " + responseJSONObject );
          }
        }
      else
        {
        Log.e( LOG_TAG, "No JSON found in graph response" );
        }

      }


    /*****************************************************
     *
     * Returns a photo from the supplied JSON.
     *
     *****************************************************/
    private Album albumFromJSON( JSONObject albumJSONObject ) throws JSONException
      {
      String id           = albumJSONObject.getString( JSON_NAME_ID );
      String name         = albumJSONObject.getString( JSON_NAME_NAME );


      // Get the cover photo

      Object coverPhoto = albumJSONObject.get( JSON_NAME_COVER_PHOTO );

      String coverPhotoId = null;

      if ( coverPhoto instanceof String )
        {
        coverPhotoId = (String)coverPhoto;
        }
      else if ( coverPhoto instanceof JSONObject )
        {
        coverPhotoId = ( (JSONObject)coverPhoto ).getString( JSON_NAME_ID );
        }


      if ( DEBUGGING_ENABLED )
        {
        Log.d( LOG_TAG, "-- Album --" );
        Log.d( LOG_TAG, "Id             : " + id );
        Log.d( LOG_TAG, "Name           : " + name );
        Log.d( LOG_TAG, "Cover photo id : " + coverPhotoId );
        }

      Album album = new Album( id, name, coverPhotoId );

      return ( album );
      }
    }


  /*****************************************************
   *
   * A graph request callback for photos.
   *
   *****************************************************/
  private class PhotosGraphRequestCallback implements GraphRequest.Callback
    {
    private Album      mAlbum;
    private ICallback  mPhotosCallback;


    PhotosGraphRequestCallback( Album album, ICallback callback )
      {
      mAlbum          = album;
      mPhotosCallback = callback;
      }


    @Override
    public void onCompleted( GraphResponse graphResponse )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "PhotosGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );


      // Check for error

      FacebookRequestError error = graphResponse.getError();

      if ( error != null )
        {
        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );

        switch ( error.getCategory() )
          {
          case LOGIN_RECOVERABLE:

            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );

            mPendingRequest = new PhotosRequest( mAlbum, mPhotosCallback );

            LoginManager.getInstance().resolveError( mActivity, graphResponse );

            return;

          case TRANSIENT:

            getPhotos( mAlbum, mPhotosCallback );

            return;

          case OTHER:

            // Fall through
          }

        if ( mPhotosCallback != null ) mPhotosCallback.facOnError( error.getException() );

        return;
        }


      // Check for data

      JSONObject responseJSONObject = graphResponse.getJSONObject();

      if ( responseJSONObject != null )
        {
        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );

        // Returned image data is as follows:
        //
        //      {
        //        "data":
        //          [
        //            {
        //            "id":"127137810981327",
        //            "link":"https:\/\/www.facebook.com\/photo.php?fbid=127137810981327&set=a.127137917647983.1073741826.100010553266947&type=3",
        //            "picture":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/s130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=28cc43a422b5a6af600cf69383ead821&oe=57D436FB",
        //            "images":
        //              [
        //                {
        //                "height":2048,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":1536
        //                },
        //                {
        //                "height":1280,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-8\/q86\/p960x960\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":960
        //                },
        //                {
        //                "height":960,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-9\/12189788_127137810981327_132541351271856743_n.jpg?oh=70a79bd7db8038ba1bddb6571f44f204&oe=57D20748",
        //                "width":720
        //                },
        //                {
        //                "height":800,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/t31.0-0\/q81\/p600x600\/12240189_127137810981327_132541351271856743_o.jpg",
        //                "width":600
        //                },
        //                {
        //                "height":640,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/q81\/p480x480\/12189788_127137810981327_132541351271856743_n.jpg?oh=df73c06e98f6fdf144ed52032b7c284c&oe=57CE9AB1",
        //                "width":480
        //                },
        //                {
        //                "height":426,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p320x320\/12189788_127137810981327_132541351271856743_n.jpg?oh=a93025d1656980ef03778b6e65f8e3ee&oe=57DAEDDE",
        //                "width":320
        //                },
        //                {
        //                "height":540,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p180x540\/12189788_127137810981327_132541351271856743_n.jpg?oh=f727d706ac924e214bdd1e113546acb2&oe=57D86FA8",
        //                "width":405
        //                },
        //                {
        //                "height":173,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p130x130\/12189788_127137810981327_132541351271856743_n.jpg?oh=7e3705aa4673ef25aba315198bd81d7c&oe=57DF914E",
        //                "width":130
        //                },
        //                {
        //                "height":225,
        //                "source":"https:\/\/scontent.xx.fbcdn.net\/v\/t1.0-0\/p75x225\/12189788_127137810981327_132541351271856743_n.jpg?oh=fe6e37ebef0d7813a5e0b5f9c16490ce&oe=57DAD07C",
        //                "width":168
        //                }
        //              ]
        //            },
        //            ... <next photo> ...

        JSONArray dataJSONArray = responseJSONObject.optJSONArray( JSON_NAME_DATA );

        if ( dataJSONArray != null )
          {
          ArrayList<Photo> photoArrayList = new ArrayList<>( dataJSONArray.length() );

          for ( int photoIndex = 0; photoIndex < dataJSONArray.length(); photoIndex ++ )
            {
            try
              {
              JSONObject photoJSONObject = dataJSONArray.getJSONObject( photoIndex );

              Photo photo = photoFromJSON( photoJSONObject );

              if ( photo != null )
                {
                photoArrayList.add( photo );
                }
              }
            catch ( JSONException je )
              {
              Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
              }
            }

          mNextPhotosPageGraphRequest = graphResponse.getRequestForPagedResults( GraphResponse.PagingDirection.NEXT );

          if (mPhotosCallback != null ) mPhotosCallback.facOnPhotosSuccess( photoArrayList, mNextPhotosPageGraphRequest != null );
          }
        else
          {
          Log.e( LOG_TAG, "No data found in JSON response: " + responseJSONObject );
          }
        }
      else
        {
        Log.e( LOG_TAG, "No JSON found in graph response" );
        }

      }

    }


  /*****************************************************
   *
   * A graph request callback for a photo.
   *
   *****************************************************/
  private class PhotoGraphRequestCallback implements GraphRequest.Callback
    {
    private String     mPhotoId;
    private ImageView  mTargetImageView;


    PhotoGraphRequestCallback( String photoId, ImageView targetImageView )
      {
      mPhotoId         = photoId;
      mTargetImageView = targetImageView;
      }


    @Override
    public void onCompleted( GraphResponse graphResponse )
      {
      if ( DEBUGGING_ENABLED ) Log.d( LOG_TAG, "PhotoGraphRequestCallback.onCompleted( graphResponse = " + graphResponse + " )" );


      // Check for error

      FacebookRequestError error = graphResponse.getError();

      if ( error != null )
        {
        Log.e( LOG_TAG, "Received Facebook server error: " + error.toString() );

        switch ( error.getCategory() )
          {
          case LOGIN_RECOVERABLE:

            Log.e( LOG_TAG, "Attempting to resolve LOGIN_RECOVERABLE error" );

            LoginManager.getInstance().resolveError( mActivity, graphResponse );

            return;

          case TRANSIENT:

            getPhoto( mPhotoId, mTargetImageView );

            return;

          case OTHER:

            // Fall through
          }

        return;
        }


      // Check for data

      JSONObject responseJSONObject = graphResponse.getJSONObject();

      if ( responseJSONObject != null )
        {
        Log.d( LOG_TAG, "Response object: " + responseJSONObject.toString() );

        try
          {
          Photo photo = photoFromJSON( responseJSONObject );

          if ( photo != null )
            {
            photo.loadThumbnailImageInto( mTargetImageView );
            }
          }
        catch ( JSONException je )
          {
          Log.e( LOG_TAG, "Unable to extract photo data from JSON: " + responseJSONObject.toString(), je );
          }
        }
      else
        {
        Log.e( LOG_TAG, "No JSON found in graph response" );
        }

      }

    }


  /*****************************************************
   *
   * This class represents a Facebook album.
   *
   *****************************************************/
  public class Album implements IImagePickerItem
    {
    ////////// Static Constant(s) //////////

    @SuppressWarnings( "unused" )
    static private final String  LOG_TAG = "FacebookAlbum";


    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////

    private String  mId;
    private String  mName;
    private String  mCoverPhotoId;


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////


    ////////// Constructor(s) //////////

    Album( String id, String name, String coverPhotoId )
      {
      mId           = id;
      mName         = name;
      mCoverPhotoId = coverPhotoId;
      }


    ////////// IImagePickerItem Method(s) //////////

    /*****************************************************
     *
     * ...
     *
     *****************************************************/
    @Override
    public String getImageURLString()
      {
      return ( null );
      }

    @Override
    public void loadThumbnailImageInto( ImageView imageView )
      {
      getPhoto( mCoverPhotoId, imageView );
      }

    @Override
    public String getLabel()
      {
      return ( mName );
      }

    @Override
    public String getKeyIfParent()
      {
      return ( mId );
      }

    @Override
    public ISelectableItem getSelectableItem()
      {
      return ( null );
      }

    @Override
    public int getSelectedCount( LinkedHashMap<String,ISelectableItem> selectableItemTable )
      {
      // We return 0 regardless of how many photos in this album are selected, because
      // it's too much work to determine the count. All that will happen is that we won't
      // display an indicator on the album if any photos in it are selected.
      return ( 0 );
      }


    ////////// Method(s) //////////

    /*****************************************************
     *
     * Returns the album id.
     *
     *****************************************************/
    String getId()
      {
      return ( mId );
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

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
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
   * This class represents a Facebook photo.
   *
   *****************************************************/
  public class Photo implements IImagePickerItem
    {
    ////////// Static Constant(s) //////////

    @SuppressWarnings("unused")
    static private final String  LOG_TAG           = "FacebookPhoto";

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

    public Photo( String id )
      {
      mId        = id;
      mImageList = new ArrayList<>();
      }

    public Photo( String id, String thumbnailURLString, int width ) throws MalformedURLException
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
    public void loadThumbnailImageInto( ImageView imageView )
      {
      if ( imageView == null ) return;

      URL bestImageURL = getBestImageURL( imageView.getWidth(), imageView.getHeight() );

      Picasso.get()
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

      if ( ! ( otherObject instanceof Photo ) )
        {
        return ( false );
        }

      Photo otherPhoto = (Photo)otherObject;

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