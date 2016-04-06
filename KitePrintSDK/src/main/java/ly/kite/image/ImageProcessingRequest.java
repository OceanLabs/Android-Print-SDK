/*****************************************************
 *
 * ImageProcessingRequest.java
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

package ly.kite.image;


///// Import(s) /////


///// Class Declaration /////

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import ly.kite.util.Asset;
import ly.kite.util.AssetHelper;

/*****************************************************
 *
 * This class represents an image processing request.
 *
 *****************************************************/
public class ImageProcessingRequest implements ServiceConnection
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ImageProcessingRequest";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private Context                   mApplicationContext;

  private Asset                     mSourceAsset;

  private Message                   mRequestMessage;

  private Asset                     mTargetAsset;
  private ICallback                 mCallback;

  private boolean                   mServiceConnected;
  private Messenger                 mRequestMessenger;

  private Handler                   mResponseHandler;
  private Messenger                 mResponseMessenger;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  ImageProcessingRequest( Context context )
    {
    mApplicationContext = context;
    }


  ////////// ServiceConnection Method(s) //////////

  /*****************************************************
   *
   * Called when we are connected to the service.
   *
   *****************************************************/
  @Override
  public void onServiceConnected( ComponentName componentName, IBinder serviceIBinder )
    {
    mServiceConnected = true;


    // Create the request and response messengers
    mRequestMessenger  = new Messenger( serviceIBinder );
    mResponseMessenger = new Messenger( mResponseHandler = new ResponseHandler() );


    // Set common values in the request message

    mRequestMessage.replyTo = mResponseMessenger;

    Bundle messageData = mRequestMessage.getData();
    messageData.putParcelable( ImageProcessingService.BUNDLE_KEY_SOURCE_ASSET, mSourceAsset );
    messageData.putParcelable( ImageProcessingService.BUNDLE_KEY_TARGET_ASSET, mTargetAsset );


    // Send the request message to the service

    try
      {
      mRequestMessenger.send( mRequestMessage );
      }
    catch ( RemoteException re )
      {
      Log.e( LOG_TAG, "Unable to send message to image processing service", re );
      }
    }


  /*****************************************************
   *
   * Called when we are disconnected from the service.
   *
   *****************************************************/
  @Override
  public void onServiceDisconnected( ComponentName name )
    {
    mServiceConnected = false;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Executes the request.
   *
   *****************************************************/
  private void execute()
    {
    // Make sure we have all the information we need

    if ( mSourceAsset    == null ) throw ( new IllegalStateException( "No source asset specified" ) );

    if ( mRequestMessage == null ) throw ( new IllegalStateException( "No transform request (message) specified" ) );

    if ( mTargetAsset    == null ) throw ( new IllegalStateException( "No target asset specified" ) );


    // Connect to the image processing service
    ImageProcessingService.bind( mApplicationContext, this );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A processing callback.
   *
   *****************************************************/
  public interface ICallback
    {
    public void ipcOnImageAvailable( Asset targetAsset );
    public void ipcOnImageUnavailable();
    }


  /*****************************************************
   *
   * A request builder.
   *
   *****************************************************/
  public class Builder implements ImageLoadRequest.IExecutor
    {
    private boolean                   mCreateNewAsset;
    private ImageLoadRequest.Builder  mImageLoadRequestBuilder;


    /*****************************************************
     *
     * Called when the load request builder is finished.
     *
     *****************************************************/
    public void execute()
      {
      create();
      }


    public Builder transform( Asset sourceAsset )
      {
      mSourceAsset = sourceAsset;

      if ( ! mCreateNewAsset ) mTargetAsset = sourceAsset;

      return ( this );
      }


    public Builder byCroppingToAspectRatio( float aspectRatio )
      {
      // Create the data for the message

      Bundle messageData = new Bundle();

      messageData.putFloat( ImageProcessingService.BUNDLE_KEY_ASPECT_RATIO, aspectRatio );


      // Create the message

      mRequestMessage = Message.obtain();

      mRequestMessage.what = ImageProcessingService.WHAT_CROP_TO_ASPECT_RATIO;
      mRequestMessage.setData( messageData );


      return ( this );
      }


    public Builder intoNewAsset()
      {
      mCreateNewAsset = true;

      // Create a placeholder for the target asset
      mTargetAsset = AssetHelper.createAsCachedFile( mApplicationContext, Asset.MIMEType.JPEG );

      return ( this );
      }


    /*****************************************************
     *
     * Specifies that once the processing has been performed,
     * who to notify.
     *
     *****************************************************/
    public void thenNotify( ICallback callback )
      {
      mCallback = callback;

      create();
      }


    /*****************************************************
     *
     * Specifies that once the processing has been performed,
     * we'll want to load the transformed image.
     *
     *****************************************************/
    public ImageLoadRequest.Builder thenLoad()
      {
      // Create a new load request builder, but make sure it doesn't get executed yet.
      mImageLoadRequestBuilder = new ImageLoadRequest( mApplicationContext ).new Builder( this );

      // Our target asset (i.e. the output of processing) is the load request's source asset
      mImageLoadRequestBuilder.load( mTargetAsset );

      return ( mImageLoadRequestBuilder );
      }


    /*****************************************************
     *
     * Creates and executes the request.
     *
     *****************************************************/
    private void create()
      {
      ImageProcessingRequest.this.execute();
      }

    }


  /*****************************************************
   *
   * A response handler.
   *
   *****************************************************/
  private class ResponseHandler extends Handler
    {
    @Override
    public void handleMessage( Message msg )
      {
      switch ( msg.what )
        {
        case ImageProcessingService.WHAT_IMAGE_AVAILABLE:

          if ( mCallback != null ) mCallback.ipcOnImageAvailable( mTargetAsset );

          break;

        case ImageProcessingService.WHAT_IMAGE_UNAVAILABLE:

          if ( mCallback != null ) mCallback.ipcOnImageUnavailable();

          break;

        default:
          super.handleMessage( msg );
        }
      }

    }

  }
