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

package ly.kite.journey;


///// Import(s) /////

import android.app.DialogFragment;
import android.app.Fragment;
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
import ly.kite.util.Asset;
import ly.kite.util.AssetHelper;
import ly.kite.util.FileDownloader;
import ly.kite.util.HTTPJSONRequest;
import ly.kite.util.HTTPRequest;


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

  static private final String   REQUEST_URL_FORMAT_STRING  = "http://qr.de/api/short?expirein_option=1&longurl=http://api.kite.ly/public_upload/%s";
  static private final String   DOWNLOAD_URL_FORMAT_STRING = "https://s3-eu-west-1.amazonaws.com/co.oceanlabs.ps/kiosk/%s.jpeg";
  static private final String   IMAGES_HOST                = "images.qr.de";

  static private final long     POLLING_INTERVAL           = 5000L;  // 5s


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private boolean      mRunAsNormal;

  private String       mShortURLString;
  private URL          mQRCodeImageURL;
  private Asset        mAsset;

  private ImageView    mQRCodeImageView;
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

    setRetainInstance( true );

    startQRQuery();
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

    mQRCodeImageView = (ImageView)view.findViewById( R.id.qr_code_image_view );
    mURLTextView     = (TextView)view.findViewById( R.id.url_text_view );
    mProgressSpinner = (ProgressBar)view.findViewById( R.id.progress_spinner );

    if ( mShortURLString == null || mQRCodeImageURL == null )
      {
      // Display the progress spinner
      mProgressSpinner.setVisibility( View.VISIBLE );
      }

    tryToDisplayShortURL();
    tryToLoadQRImage();

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
   * Starts the QR code query.
   *
   *****************************************************/
  private void startQRQuery()
    {
    // We use the qr.net API to shorted the URL and generate a QR code. It
    // is in the form: http://qr.de/api/short?expirein_option=1&longurl=<URL>

    // Generate a UUID
    String uuid = UUID.randomUUID().toString();

    // Create the URLs
    String qrAPIRequestURLString = String.format( REQUEST_URL_FORMAT_STRING, uuid );
    String downloadURLString     = String.format( DOWNLOAD_URL_FORMAT_STRING, uuid );

    // Start the request
    new HTTPJSONRequest( getActivity(), HTTPJSONRequest.HttpMethod.GET, qrAPIRequestURLString, null, null ).start( new ShortResponseListener( downloadURLString ) );
    }


  /*****************************************************
   *
   * Sets the QR code string if we have both the text view and
   * the code itself.
   *
   *****************************************************/
  private void tryToDisplayShortURL()
    {
    if ( mShortURLString != null && mURLTextView != null )
      {
      mURLTextView.setText( mShortURLString );
      }
    }


  /*****************************************************
   *
   * Requests a load of the QR code image if we have both the image view and
   * the URL.
   *
   *****************************************************/
  private void tryToLoadQRImage()
    {
    if ( mQRCodeImageURL != null && mQRCodeImageView != null )
      {
      // Request the QR code image
      ImageAgent
              .with( getActivity() )
              .load( mQRCodeImageURL, KiteSDK.IMAGE_CATEGORY_SESSION_ASSET )
              .into( mQRCodeImageView );
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
    private String mDownloadURLString;


    ShortResponseListener( String downloadURLString )
      {
      mDownloadURLString = downloadURLString;
      }


    /*****************************************************
     *
     * Called when the QR request succeeds.
     *
     *****************************************************/
    @Override
    public void onSuccess( int httpStatusCode, JSONObject json )
      {
      if ( DEBUGGING_ENABLED ) Log.d( TAG, "onSuccess( httpStatusCode = " + httpStatusCode + ", json = " + json.toString() + " )" );

      // Hide the progress spinner
      mProgressSpinner.setVisibility( View.GONE );

      if ( ! mRunAsNormal ) return;

      // The result should be something like this:
      //   {
      //   "twitter_url":"http:\/\/twitter.com\/home?status=http:\/\/qr.de\/bcXa",
      //   "stat_url":"http:\/\/qr.de\/bcXa+",
      //   "target_host":"kite.ly",
      //   "host":"http:\/\/qr.de\/",
      //   "error":{
      //           "msg":"OK",
      //           "code":0
      //           },
      //   "url":"http:\/\/qr.de\/bcXa",
      //   "expire_date":"2016-06-08 12:44:30 CEST",
      //   "facebook_url":"http:\/\/www.facebook.com\/sharer.php?u=http:\/\/qr.de\/bcXa",
      //   "share":"http:\/\/qr.de\/share\/bcXa"
      //   }

      if ( json != null )
        {
        // If we found a short URL - display it

        mShortURLString = json.optString( "url" );

        if ( mShortURLString != null )
          {
          tryToDisplayShortURL();


          try
            {
            URL shortURL    = new URL( mShortURLString );
            URL downloadURL = new URL( mDownloadURLString );


            // The QR API doesn't seem to generate a QR code until we access http://qr.de/share/<code>. So
            // request this URL, ignore the result and then get the QR code image from
            // http://images.qr.de/<code>

            String shareURLString = json.optString( "share" );

            if ( shareURLString != null )
              {
              new HTTPRequest( getActivity(), HTTPJSONRequest.HttpMethod.GET, shareURLString, null, null ).start( new ShareResponseListener( shortURL, downloadURL) );
              }

            }
          catch ( MalformedURLException mue )
            {
            Log.e( TAG, "Invalid URL: " + mShortURLString, mue );

            dismiss();
            }
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
      String message = "Unable to get QR code: " + exception.getMessage();

      Log.e( TAG, message, exception );

      Toast.makeText( getActivity(), message, Toast.LENGTH_LONG ).show();

      dismiss();
      }
    }


  /*****************************************************
   *
   * QR share API response listener.
   *
   *****************************************************/
  private class ShareResponseListener implements HTTPRequest.IResponseListener
    {
    private URL  mShortURL;
    private URL  mDownloadURL;


    ShareResponseListener( URL shortURL, URL downloadURL )
      {
      mShortURL    = shortURL;
      mDownloadURL = downloadURL;
      }


    @Override
    public void onSuccess( int httpStatusCode )
      {
      onRequestComplete();
      }


    @Override
    public void onError( Exception exception )
      {
      onRequestComplete();
      }


    /*****************************************************
     *
     * Called when the share request has completed, regardless
     * of whether it succeeded or not.
     *
     *****************************************************/
    private void onRequestComplete()
      {
      if ( ! mRunAsNormal ) return;

      try
        {
        // Try and construct the URL for the image
        mQRCodeImageURL = new URL( mShortURL.getProtocol(), IMAGES_HOST, mShortURL.getFile() );

        tryToLoadQRImage();


        // Start polling for the photo

        mHandler = new Handler();

        new CheckForPhotoRunnable( mDownloadURL ).post();
        }
      catch ( MalformedURLException mue )
        {
        Log.e( TAG, "Unable to generate QR code image URL", mue );
        }
      }

    }


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

      FileDownloader.getInstance( getActivity() ).requestFileDownload( mDownloadURL, file.getParentFile(), file, this );
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

