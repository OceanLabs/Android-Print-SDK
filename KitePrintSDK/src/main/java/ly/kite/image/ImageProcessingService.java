/*****************************************************
 *
 * ImageProcessingService.java
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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import ly.kite.KiteSDK;
import ly.kite.util.Asset;
import ly.kite.util.AssetHelper;


///// Class Declaration /////

/*****************************************************
 *
 * This is the service that performs image processing on
 * behalf of SDK components. It should be run it a separate
 * process so that it is less likely to run out of memory
 * when performing processing tasks, and thus ensures
 * that processed images are as high a quality as possible.
 *
 * It is implemented using a messenger and handler, so that
 * it may be run in a separate process.
 *
 *****************************************************/
public class ImageProcessingService extends Service
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                   = "ImageProcessingService";

  static public  final int     WHAT_CROP_TO_ASPECT_RATIO = 23;
  static public  final int     WHAT_FLIP_HORIZONTALLY    = 27;
  static public  final int     WHAT_ROTATE_ANTICLOCKWISE = 29;
  static public  final int     WHAT_CROP_TO_BOUNDS       = 32;

  static public  final int     WHAT_IMAGE_AVAILABLE      = 46;
  static public  final int     WHAT_IMAGE_UNAVAILABLE    = 48;

  static public  final String  BUNDLE_KEY_SOURCE_ASSET   = "sourceAsset";
  static public  final String  BUNDLE_KEY_TARGET_ASSET   = "targetAsset";
  static public  final String  BUNDLE_KEY_ASPECT_RATIO   = "aspectRatio";
  static public  final String  BUNDLE_KEY_CROP_BOUNDS    = "cropBounds";

  static public  final float   DEFAULT_ASPECT_RATIO      = 1.0f;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private RequestHandler  mRequestHandler;
  private Messenger       mRequestMessenger;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Binds to this service.
   *
   *****************************************************/
  static public void bind( Context context, ServiceConnection serviceConnection )
    {
    Intent intent = new Intent( context, ImageProcessingService.class );

    context.bindService( intent, serviceConnection, Context.BIND_AUTO_CREATE );
    }


  ////////// Constructor(s) //////////


  ////////// Service Method(s) //////////

  /*****************************************************
   *
   * Called when the service is created.
   *
   *****************************************************/
  @Override
  public void onCreate()
    {
    super.onCreate();

    mRequestHandler   = new RequestHandler();
    mRequestMessenger = new Messenger( mRequestHandler );
    }


  /*****************************************************
   *
   * Called when a client binds to the service.
   *
   *****************************************************/
  @Nullable
  @Override
  public IBinder onBind( Intent intent )
    {
    return ( mRequestMessenger.getBinder() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The handler for request messages.
   *
   *****************************************************/
  private class RequestHandler extends Handler
    {
    @Override
    public void handleMessage( Message message )
      {
      // Get common values from the message

      Messenger responseMessenger = message.replyTo;

      Bundle messageData = message.getData();

      messageData.setClassLoader( Asset.class.getClassLoader() );
      Asset sourceAsset = messageData.getParcelable( BUNDLE_KEY_SOURCE_ASSET );
      Asset targetAsset = messageData.getParcelable( BUNDLE_KEY_TARGET_ASSET );


      IImageTransformer transformer;

      switch ( message.what )
        {
        case WHAT_CROP_TO_ASPECT_RATIO:

          ///// Crop to aspect ratio /////

          float aspectRatio = messageData.getFloat( BUNDLE_KEY_ASPECT_RATIO, DEFAULT_ASPECT_RATIO );

          if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "Received CROP_TO_ASPECT_RATIO message: responseMessenger = " + responseMessenger + ", sourceAsset = " + sourceAsset + ", targetAsset = " + targetAsset );

          transformer = new CropToAspectRatioTransformer( aspectRatio );

          break;

        case WHAT_FLIP_HORIZONTALLY:

          ///// Flip horizontally /////

          if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "Received FLIP_HORIZONTALLY message: responseMessenger = " + responseMessenger + ", sourceAsset = " + sourceAsset + ", targetAsset = " + targetAsset );

          transformer = new FlipHorizontallyTransformer();

          break;

        case WHAT_ROTATE_ANTICLOCKWISE:

          ///// Rotate anticlockwise /////

          if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "Received ROTATE_ANTICLOCKWISE message: responseMessenger = " + responseMessenger + ", sourceAsset = " + sourceAsset + ", targetAsset = " + targetAsset );

          transformer = new RotateAnticlockwiseTransformer();

          break;

        case WHAT_CROP_TO_BOUNDS:

          ///// Crop to aspect ratio /////

          messageData.setClassLoader( RectF.class.getClassLoader() );
          RectF cropBounds = messageData.getParcelable( BUNDLE_KEY_CROP_BOUNDS );

          if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "Received CROP_TO_BOUNDS message: responseMessenger = " + responseMessenger + ", sourceAsset = " + sourceAsset + ", targetAsset = " + targetAsset );

          transformer = new CropToBoundsTransformer( cropBounds );

          break;

        default:

          super.handleMessage( message );

          return;
        }


      TransformedImageConsumer consumer = new TransformedImageConsumer( targetAsset, responseMessenger );

      ImageAgent.with( ImageProcessingService.this )
              .load( sourceAsset )
              .transformBeforeResize( transformer )
              .into( consumer, null );
      }
    }


  /*****************************************************
   *
   * A crop to aspect ratio transformer.
   *
   *****************************************************/
  private class CropToAspectRatioTransformer implements IImageTransformer
    {
    private float  mAspectRatio;


    CropToAspectRatioTransformer( float aspectRatio )
      {
      mAspectRatio = aspectRatio;
      }


    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      return ( ImageAgent.crop( bitmap, mAspectRatio ) );
      }
    }


  /*****************************************************
   *
   * A flip horizontally transformer.
   *
   *****************************************************/
  private class FlipHorizontallyTransformer implements IImageTransformer
    {
    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      ImageAgent.horizontallyFlipBitmap( bitmap );

      return ( bitmap );
      }
    }


  /*****************************************************
   *
   * A rotate anticlockwise transformer.
   *
   *****************************************************/
  private class RotateAnticlockwiseTransformer implements IImageTransformer
    {
    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      return ( ImageAgent.rotateAnticlockwiseBitmap( bitmap ) );
      }
    }


  /*****************************************************
   *
   * A crop to bounds transformer.
   *
   *****************************************************/
  private class CropToBoundsTransformer implements IImageTransformer
    {
    private RectF  mCropBounds;


    CropToBoundsTransformer( RectF cropBounds )
      {
      mCropBounds = cropBounds;
      }


    /*****************************************************
     *
     * Called on a background thread to transform a bitmap.
     * We use this to crop the bitmap, and create a file-backed
     * asset from it.
     *
     *****************************************************/
    @Override
    public Bitmap getTransformedBitmap( Bitmap bitmap )
      {
      return ( ImageAgent.crop( bitmap, mCropBounds ) );
      }
    }


  /*****************************************************
   *
   * The image consumer that receives the transformed image,
   * saves it to the target asset, and sends a message back
   * to the client.
   *
   *****************************************************/
  private class TransformedImageConsumer implements IImageConsumer
    {
    private Asset      mTargetAsset;
    private Messenger  mResponseMessenger;


    TransformedImageConsumer( Asset targetAsset, Messenger responseMessenger )
      {
      mTargetAsset       = targetAsset;
      mResponseMessenger = responseMessenger;
      }


    @Override
    public void onImageDownloading( Object key )
      {
      // Ignore
      }

    @Override
    public void onImageAvailable( Object key, Bitmap bitmap )
      {
      if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "onImageAvailable( key = " + key + ", bitmap = " + bitmap + " )" );

      // Save the the transformed image to the target
      AssetHelper.replaceAsset( bitmap, mTargetAsset );

      sendResponseMessage( WHAT_IMAGE_AVAILABLE );
      }

    @Override
    public void onImageUnavailable( Object key, Exception exception )
      {
      sendResponseMessage( WHAT_IMAGE_UNAVAILABLE );
      }


    private void sendResponseMessage( int what )
      {
      Message responseMessage = Message.obtain();

      responseMessage.what = what;

      try
        {
        if ( KiteSDK.DEBUG_IMAGE_PROCESSING ) Log.i( LOG_TAG, "Sending response message: what = " + what );

        mResponseMessenger.send( responseMessage );
        }
      catch ( RemoteException re )
        {
        Log.e( LOG_TAG, "Unable to send response message", re );
        }
      }
    }

  }

