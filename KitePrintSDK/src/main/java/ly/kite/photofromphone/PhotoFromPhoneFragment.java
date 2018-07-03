/*****************************************************
 *
 * PhotoFromPhoneFragment.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2016 Kite Tech Ltd. https://www.kite.ly
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

package ly.kite.photofromphone;


///// Import(s) /////

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.image.ImageAgent;
import ly.kite.journey.AImageSource;
import ly.kite.util.Asset;
import ly.kite.util.AssetHelper;
import ly.kite.util.FileDownloader;
import ly.kite.util.HTTPJSONRequest;
import ly.kite.util.HTTPRequest;
import ly.kite.widget.QRCodeView;


///// Class Declaration /////


/*****************************************************
 *
 * This fragment is responsible for obtaining an uploaded
 * photo from a phone, by means of creating an upload
 * URL, shortening it, displaying a QR code, and polling
 * for the photo.
 *
 *****************************************************/
public class PhotoFromPhoneFragment extends DialogFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String    TAG                        = "PhotoFromPhoneFragment";

  static private final boolean  DEBUGGING_ENABLED          = true;

  static private final String   UPLOAD_URL_FORMAT_STRING      = "http://api.kite.ly/public_upload/%s";
  static private final String   DOWNLOAD_URL_FORMAT_STRING    = "https://s3-eu-west-1.amazonaws.com/co.oceanlabs.ps/kiosk/%s.jpeg";
  static private final String   SHORTEN_REQUEST_FORMAT_STRING = "https://is.gd/create.php?format=json&url=%s";

  static private final long     POLLING_INTERVAL           = 5000L;  // 5s


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean      mRunAsNormal;

  private URL          mImageUploadURL;
  private URL          mImageDownloadURL;
  private URL          mShortenRequestURL;

  private String       mShortenedURLString;

  private Asset        mAsset;

  private QRCodeView   mQRCodeView;
  private TextView     mURLTextView;
  private ProgressBar  mProgressSpinner;

  private Handler      mHandler;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// DialogFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );


    mRunAsNormal = true;


    // Create the upload / download URLs

    String uuid = UUID.randomUUID().toString();

    try
      {
      mImageUploadURL    = new URL( String.format( UPLOAD_URL_FORMAT_STRING, uuid ) );
      mImageDownloadURL  = new URL( String.format( DOWNLOAD_URL_FORMAT_STRING, uuid ) );
      mShortenRequestURL = new URL( String.format( SHORTEN_REQUEST_FORMAT_STRING, mImageUploadURL.toExternalForm() ) );

      // Start the request for the shortened URL
      new HTTPJSONRequest( getActivity(), HTTPRequest.HttpMethod.GET, mShortenRequestURL.toExternalForm(), null, null ).start( new ShortResponseListener() );


      // Start polling for the download image

      mHandler = new Handler();

      new CheckForPhotoRunnable( mImageDownloadURL ).post();
      }
    catch ( MalformedURLException mue )
      {
      Log.d( TAG, "Unable to create URLs", mue );
      }


    setRetainInstance( true );
    }


  /*****************************************************
   *
   * Returns a view for the dialog.
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.dialog_photo_from_phone, container, false );

    mQRCodeView      = (QRCodeView)view.findViewById( R.id.qr_code_view );
    mURLTextView     = (TextView)view.findViewById( R.id.url_text_view );
    mProgressSpinner = (ProgressBar)view.findViewById( R.id.progress_spinner );

    if ( mImageUploadURL != null )
      {
      mQRCodeView.setURL( mImageUploadURL );
      }


    if ( mShortenedURLString == null )
      {
      // Display the progress spinner
      mProgressSpinner.setVisibility( View.VISIBLE );
      }


    tryToDisplayShortURL();

    return ( view );
    }


  /*****************************************************
   *
   * Called when the view is destroyed.
   *
   *****************************************************/
  @Override
  public void onDestroyView()
    {
    // This is a work-around to stop the dialog being dismissed
    // on orientation change.

    if ( getDialog() != null && getRetainInstance() )
      {
      getDialog().setDismissMessage( null );
      }

    super.onDestroyView();
    }


  /*****************************************************
   *
   * Called when the fragment is dismissed.
   *
   *****************************************************/
  @Override
  public void onDismiss( DialogInterface dialogInterface )
    {
    super.onDismiss( dialogInterface );

    // Cancel any processing
    mRunAsNormal = false;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the QR code string if we have both the text view and
   * the code itself.
   *
   *****************************************************/
  private void tryToDisplayShortURL()
    {
    if ( mShortenedURLString != null && mURLTextView != null )
      {
      mURLTextView.setText( mShortenedURLString );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * QR short API response listener.
   *
   *****************************************************/
  private class ShortResponseListener implements HTTPJSONRequest.IJSONResponseListener
    {
    //private String mDownloadURLString;


    /*****************************************************
     *
     * Called when the shorten request succeeds.
     *
     *****************************************************/
    @Override
    public void onSuccess( int httpStatusCode, JSONObject json )
      {
      if ( DEBUGGING_ENABLED ) Log.d( TAG, "onSuccess( httpStatusCode = " + httpStatusCode + ", json = " + json.toString() + " )" );

      // Hide the progress spinner
      mProgressSpinner.setVisibility( View.GONE );

      if ( ! mRunAsNormal ) return;

      if ( json != null )
        {
        // If we found a short URL - display it

        mShortenedURLString = json.optString( "shorturl" );

        if ( mShortenedURLString != null )
          {
          tryToDisplayShortURL();
          }
        }
      }


    /*****************************************************
     *
     * Called when the QR request fails.
     *
     *****************************************************/
    @Override
    public void onError( Exception exception )
      {
      String message = "Unable to get shortened URL: " + exception.getMessage();

      Log.e( TAG, message, exception );

      Toast.makeText( getActivity(), message, Toast.LENGTH_LONG ).show();

      dismiss();
      }
    }


//  /*****************************************************
//   *
//   * QR share API response listener.
//   *
//   *****************************************************/
//  private class ShareResponseListener implements HTTPRequest.IResponseListener
//    {
//    private URL  mShortURL;
//    private URL  mDownloadURL;
//
//
//    ShareResponseListener( URL shortURL, URL downloadURL )
//      {
//      mShortURL    = shortURL;
//      mDownloadURL = downloadURL;
//      }
//
//
//    @Override
//    public void onSuccess( int httpStatusCode )
//      {
//      onRequestComplete();
//      }
//
//
//    @Override
//    public void onError( Exception exception )
//      {
//      onRequestComplete();
//      }
//
//
//    /*****************************************************
//     *
//     * Called when the share request has completed, regardless
//     * of whether it succeeded or not.
//     *
//     *****************************************************/
//    private void onRequestComplete()
//      {
//      if ( ! mRunAsNormal ) return;
//
//      try
//        {
//        // Try and construct the URL for the image
//        mQRCodeImageURL = new URL( mShortURL.getProtocol(), IMAGES_HOST, mShortURL.getFile() );
//
//        tryToLoadQRImage();
//
//
//        // Start polling for the photo
//
//        mHandler = new Handler();
//
//        new CheckForPhotoRunnable( mDownloadURL ).post();
//        }
//      catch ( MalformedURLException mue )
//        {
//        Log.e( TAG, "Unable to generate QR code image URL", mue );
//        }
//      }
//
//    }


  /*****************************************************
   *
   * A runnable that checks for the uploaded photo.
   *
   *****************************************************/
  private class CheckForPhotoRunnable implements Runnable, FileDownloader.ICallback
    {
    private URL    mDownloadURL;


    CheckForPhotoRunnable( URL downloadURL )
      {
      mDownloadURL = downloadURL;
      }


    @Override
    public void run()
      {
      if ( ! mRunAsNormal ) return;


      // Create a placeholder asset for the photo
      mAsset = AssetHelper.createAsSessionAsset( getActivity(), Asset.MIMEType.JPEG );


      // Try to download the photo

      File file = mAsset.getImageFile();

      FileDownloader.getInstance( getActivity() ).requestFileDownload( mDownloadURL, mAsset.getURLHeaderMap(), file.getParentFile(), file, this );
      }


    @Override
    public void onDownloadSuccess( URL sourceURL, File targetDirectory, File targetFile )
      {
      // If we successfully downloaded the photo, call back to the target fragment with the
      // image wrapped in an asset.

      Fragment targetFragment = getTargetFragment();

      if ( targetFragment != null && targetFragment instanceof AImageSource.IAssetConsumer )
        {
        List<Asset> assetList = new ArrayList<>( 1 );

        assetList.add( mAsset);

        ( (AImageSource.IAssetConsumer)targetFragment ).isacOnAssets( assetList );
        }
      else
        {
        Log.e( TAG, "Invalid target fragment for callback: " + targetFragment );
        }


      dismiss();
      }


    @Override
    public void onDownloadFailure( URL sourceURL, Exception exception )
      {
      if ( ! mRunAsNormal ) return;

      post();
      }


    void post()
      {
      mHandler.postDelayed( this, POLLING_INTERVAL );
      }
    }

  }

