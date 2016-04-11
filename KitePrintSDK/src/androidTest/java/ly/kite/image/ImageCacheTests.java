/*****************************************************
 *
 * ImageCacheTests.java
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

package ly.kite.image;


///// Import(s) /////

import android.graphics.Bitmap;

import junit.framework.Assert;
import junit.framework.TestCase;

import ly.kite.image.IImageConsumer;
import ly.kite.image.ImageCache;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the image cache class.
 *
 *****************************************************/
public class ImageCacheTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageCacheTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  public void testCache1()
    {
    ImageCache cache = new ImageCache( 0 );

    Object key    = new Object();
    Bitmap bitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    cache.addImage( key, bitmap );

    Assert.assertEquals( null, cache.getImage( key ) );
    }

  public void testCache2()
    {
    ImageCache cache = new ImageCache( 70 );

    Object key1    = new Object();
    Bitmap bitmap1 = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    Object key2    = new Object();
    Bitmap bitmap2 = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    cache.addImage( key1, bitmap1 );
    Assert.assertEquals( bitmap1, cache.getImage( key1 ) );
    Assert.assertEquals( null, cache.getImage( key2 ) );

    cache.addImage( key2, bitmap2 );
    Assert.assertEquals( null, cache.getImage( key1 ) );
    Assert.assertEquals( bitmap2, cache.getImage( key2 ) );
    }

  public void testCache3()
    {
    ImageCache cache = new ImageCache( 150 );

    Object key1    = new Object();
    Bitmap bitmap1 = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    Object key2    = new Object();
    Bitmap bitmap2 = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    Object key3    = new Object();
    Bitmap bitmap3 = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    cache.addImage( key1, bitmap1 );
    Assert.assertEquals( bitmap1, cache.getImage( key1 ) );
    Assert.assertEquals( null,    cache.getImage( key2 ) );
    Assert.assertEquals( null,    cache.getImage( key3 ) );

    cache.addImage( key2, bitmap2 );
    Assert.assertEquals( bitmap1, cache.getImage( key1 ) );
    Assert.assertEquals( bitmap2, cache.getImage( key2 ) );
    Assert.assertEquals( null,    cache.getImage( key3 ) );

    cache.addImage( key3, bitmap3 );
    Assert.assertEquals( null, cache.getImage( key1 ) );
    Assert.assertEquals( bitmap2, cache.getImage( key2 ) );
    Assert.assertEquals( bitmap3, cache.getImage( key3 ) );
    }


  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  public void testPending1()
    {
    ImageCache    cache    = new ImageCache( 100 );
    ImageConsumer consumer = new ImageConsumer();

    Object key    = new Object();
    Bitmap bitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    cache.addPendingImage( key, consumer );

    Assert.assertEquals( false, consumer.downloadingCalled );
    Assert.assertEquals( false, consumer.availableCalled );
    Assert.assertEquals( false, consumer.unavailableCalled );

    cache.onImageAvailable( key, bitmap );

    Assert.assertEquals( false, consumer.downloadingCalled );
    Assert.assertEquals( true,  consumer.availableCalled );
    Assert.assertEquals( false, consumer.unavailableCalled );
    }

  public void testPending2()
    {
    ImageCache    cache    = new ImageCache( 100 );
    ImageConsumer consumer = new ImageConsumer();

    Object key    = new Object();

    cache.addPendingImage( key, consumer );

    cache.onImageDownloading( key );

    Assert.assertEquals( true,  consumer.downloadingCalled );
    Assert.assertEquals( false, consumer.availableCalled );
    Assert.assertEquals( false, consumer.unavailableCalled );
    }

  public void testPending3()
    {
    ImageCache    cache    = new ImageCache( 100 );
    ImageConsumer consumer = new ImageConsumer();

    Object key    = new Object();

    cache.addPendingImage( key, consumer );

    cache.onImageUnavailable( key, new Exception( "Test" ) );

    Assert.assertEquals( false, consumer.downloadingCalled );
    Assert.assertEquals( false, consumer.availableCalled );
    Assert.assertEquals( true,  consumer.unavailableCalled );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * An image consumer.
   *
   *****************************************************/

  private class ImageConsumer implements IImageConsumer
    {
    boolean downloadingCalled;
    boolean availableCalled;
    boolean unavailableCalled;

    @Override
    public void onImageDownloading( Object key )
      {
      this.downloadingCalled = true;
      }

    @Override
    public void onImageAvailable( Object key, Bitmap bitmap )
      {
      this.availableCalled = true;
      }

    @Override
    public void onImageUnavailable( Object key, Exception exception )
      {
      this.unavailableCalled = true;
      }
    }

  }
