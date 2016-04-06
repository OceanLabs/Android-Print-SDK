/*****************************************************
 *
 * ImageViewConsumer.java
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

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class enables a standard ImageView to become
 * an image consumer. The key is stored as a tag.
 *
 *****************************************************/
public class ImageViewConsumer implements IImageConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ImageViewConsumer";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ImageView  mImageView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Clears the key for an image view.
   *
   *****************************************************/
  static public void clearKey( ImageView imageView )
    {
    imageView.setTag( R.id.image_view_consumer_key, null );
    }


  ////////// Constructor(s) //////////

  public ImageViewConsumer( Object key, ImageView imageView )
    {
    if ( key       == null ) throw ( new IllegalArgumentException( "No key supplied" ) );
    if ( imageView == null ) throw ( new IllegalArgumentException( "No image view supplied" ) );

    mImageView = imageView;

    imageView.setTag( R.id.image_view_consumer_key, key );
    }


  ////////// IImageConsumer Method(s) //////////

  /*****************************************************
   *
   * Called if the image is being downloaded.
   *
   *****************************************************/
  @Override
  public void onImageDownloading( Object key )
    {
    Object imageViewKey = mImageView.getTag( R.id.image_view_consumer_key );

    if ( imageViewKey != null && imageViewKey.equals( key ) )
      {
      // Currently do nothing, although we could display
      // a loading image.
      }
    }


  @Override
  public void onImageAvailable( Object key, Bitmap bitmap )
    {
    Object imageViewKey = mImageView.getTag( R.id.image_view_consumer_key );

    if ( imageViewKey != null && imageViewKey.equals( key ) )
      {
      clearKey( mImageView );

      mImageView.setImageBitmap( bitmap );
      }
    }


  @Override
  public void onImageUnavailable( Object key, Exception exception )
    {
    Object imageViewKey = mImageView.getTag( R.id.image_view_consumer_key );

    if ( imageViewKey != null && imageViewKey.equals( key ) )
      {
      Log.e( LOG_TAG, "Unable to load image for key: " + key, exception );

      // Don't clear the key, in case for some reason it loads subsequently

      // Currently do nothing, although we could display
      // an error indicator image.
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

