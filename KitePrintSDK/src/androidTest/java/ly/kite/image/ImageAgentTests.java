/*****************************************************
 *
 * ImageAgentTests.java
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
import android.graphics.Rect;
import android.graphics.RectF;

import junit.framework.Assert;
import junit.framework.TestCase;

import ly.kite.image.ImageAgent;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the image agent class.
 *
 *****************************************************/
public class ImageAgentTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageAgentTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Safe string tests.
   *
   *****************************************************/

  public void testSafeString1()
    {
    Assert.assertEquals( "", ImageAgent.toSafeString( null ) );
    Assert.assertEquals( "", ImageAgent.toSafeString( "" ) );

    Assert.assertEquals( "a", ImageAgent.toSafeString( "a" ) );
    Assert.assertEquals( "z", ImageAgent.toSafeString( "z" ) );

    Assert.assertEquals( "A", ImageAgent.toSafeString( "A" ) );
    Assert.assertEquals( "Z", ImageAgent.toSafeString( "Z" ) );

    Assert.assertEquals( "0", ImageAgent.toSafeString( "0" ) );
    Assert.assertEquals( "9", ImageAgent.toSafeString( "9" ) );

    Assert.assertEquals( "jskfh____k08723_______hjisdfh_h__", ImageAgent.toSafeString( "jskfh/?&^k08723/+[]{}£hjisdfh.h#@" ) );
    Assert.assertEquals( "abcedfghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_________________________________", ImageAgent.toSafeString( "abcedfghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"£$€%^&*()-_=+[]{};:'@#~,<.>/?\\|" ) );
    }


  /*****************************************************
   *
   * Get crop rectangle tests.
   *
   *****************************************************/

  public void testGetCropRectangle1()
    {
    Rect cropRectangle = ImageAgent.getCropRectangle( 12, 12, 1f );

    Assert.assertEquals(  0, cropRectangle.left   );
    Assert.assertEquals(  0, cropRectangle.top    );
    Assert.assertEquals( 12, cropRectangle.right  );
    Assert.assertEquals( 12, cropRectangle.bottom );
    }

  public void testGetCropRectangle2()
    {
    Rect cropRectangle = ImageAgent.getCropRectangle( 12, 12, 2f );

    Assert.assertEquals(  0, cropRectangle.left   );
    Assert.assertEquals(  3, cropRectangle.top    );
    Assert.assertEquals( 12, cropRectangle.right  );
    Assert.assertEquals(  9, cropRectangle.bottom );
    }

  public void testGetCropRectangle3()
    {
    Rect cropRectangle = ImageAgent.getCropRectangle( 12, 12, 0.5f );

    Assert.assertEquals(  3, cropRectangle.left   );
    Assert.assertEquals(  0, cropRectangle.top    );
    Assert.assertEquals(  9, cropRectangle.right  );
    Assert.assertEquals( 12, cropRectangle.bottom );
    }


  /*****************************************************
   *
   * Crop to aspect ratio tests.
   *
   *****************************************************/

  public void testCropAspectRatio1()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    Bitmap croppedBitmap  = ImageAgent.crop( originalBitmap, 1.0f );

    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );
    }

  public void testCropAspectRatio2()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    Bitmap croppedBitmap  = ImageAgent.crop( originalBitmap, 2.0f );

    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 2, croppedBitmap.getHeight() );
    }

  public void testCropAspectRatio3()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    Bitmap croppedBitmap  = ImageAgent.crop( originalBitmap, 0.5f );

    Assert.assertEquals( 2, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );
    }


  /*****************************************************
   *
   * Cropping to bounds tests.
   *
   *****************************************************/

  private void setAllPixels( Bitmap bitmap, int colour )
    {
    for ( int y = 0; y < bitmap.getHeight(); y ++ )
      for ( int x = 0; x < bitmap.getWidth(); x ++ )
        bitmap.setPixel( x, y, colour );
    }


  //
  // Test ARGB 8888 bitmaps
  //

  public void testCropBoundsARGB1()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.0f, 1.0f, 1.0f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsARGB2()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, 0.25f, 0.75f, 0.75f ) );


    Assert.assertEquals( 2, croppedBitmap.getWidth() );
    Assert.assertEquals( 2, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    }

  public void testCropBoundsARGB3()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.0f, 0.75f, 0.75f ) );


    Assert.assertEquals( 3, croppedBitmap.getWidth() );
    Assert.assertEquals( 3, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    }

  public void testCropBoundsARGB4()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( -0.25f, -0.25f, 0.75f, 0.75f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsARGB5()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, -0.25f, 1.25f, 0.75f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsARGB6()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, 0.25f, 1.25f, 1.25f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsARGB7()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.ARGB_8888 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.75f, 0.5f, 1.25f ) );


    Assert.assertEquals( 2, croppedBitmap.getWidth() );
    Assert.assertEquals( 2, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 1 ) );
    }


  //
  // Test RGB 565 bitmaps
  //

  public void testCropBoundsRGB1()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.0f, 1.0f, 1.0f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsRGB2()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, 0.25f, 0.75f, 0.75f ) );


    Assert.assertEquals( 2, croppedBitmap.getWidth() );
    Assert.assertEquals( 2, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    }

  public void testCropBoundsRGB3()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.0f, 0.75f, 0.75f ) );


    Assert.assertEquals( 3, croppedBitmap.getWidth() );
    Assert.assertEquals( 3, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    }

  public void testCropBoundsRGB4()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( -0.25f, -0.25f, 0.75f, 0.75f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsRGB5()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, -0.25f, 1.25f, 0.75f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsRGB6()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.25f, 0.25f, 1.25f, 1.25f ) );


    Assert.assertEquals( 4, croppedBitmap.getWidth() );
    Assert.assertEquals( 4, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 0 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 1 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 1 ) );

    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 2 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 2, 2 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 2 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 2, 3 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 3, 3 ) );
    }

  public void testCropBoundsRGB7()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    setAllPixels( originalBitmap, 0xff000000 );


    Bitmap croppedBitmap = ImageAgent.crop( originalBitmap, new RectF( 0.0f, 0.75f, 0.5f, 1.25f ) );


    Assert.assertEquals( 2, croppedBitmap.getWidth() );
    Assert.assertEquals( 2, croppedBitmap.getHeight() );


    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000000, croppedBitmap.getPixel( 1, 0 ) );

    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xffffffff, croppedBitmap.getPixel( 1, 1 ) );
    }


  /*****************************************************
   *
   * Downscale tests.
   *
   *****************************************************/

  public void testDownscaleBitmap1()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    Bitmap scaledBitmap  = ImageAgent.downscaleBitmap( originalBitmap, 5 );

    Assert.assertEquals( 4, scaledBitmap.getWidth() );
    Assert.assertEquals( 4, scaledBitmap.getHeight() );
    }

  public void testDownscaleBitmap2()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 4, Bitmap.Config.RGB_565 );

    Bitmap scaledBitmap  = ImageAgent.downscaleBitmap( originalBitmap, 2 );

    Assert.assertEquals( 2, scaledBitmap.getWidth() );
    Assert.assertEquals( 2, scaledBitmap.getHeight() );
    }


  /*****************************************************
   *
   * Flip vertically tests.
   *
   *****************************************************/

  public void testVerticallyFlipBitmap1()
    {
    Bitmap bitmap = Bitmap.createBitmap( 2, 2, Bitmap.Config.ARGB_8888 );
    bitmap.setPixel( 0, 0, 0xff000000 );
    bitmap.setPixel( 1, 0, 0xff000001 );
    bitmap.setPixel( 0, 1, 0xff000002 );
    bitmap.setPixel( 1, 1, 0xff000003 );

    ImageAgent.verticallyFlipBitmap( bitmap );

    Assert.assertEquals( 2, bitmap.getWidth() );
    Assert.assertEquals( 2, bitmap.getHeight() );

    Assert.assertEquals( 0xff000002, bitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000003, bitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, bitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000001, bitmap.getPixel( 1, 1 ) );
    }


  /*****************************************************
   *
   * Rotate anticlockwise tests.
   *
   *****************************************************/

  public void testRotateAnticlockwiseBitmap1()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 2, 2, Bitmap.Config.ARGB_8888 );
    originalBitmap.setPixel( 0, 0, 0xff000000 );
    originalBitmap.setPixel( 1, 0, 0xff000001 );
    originalBitmap.setPixel( 0, 1, 0xff000002 );
    originalBitmap.setPixel( 1, 1, 0xff000003 );

    Bitmap rotatedBitmap = ImageAgent.rotateAnticlockwiseBitmap( originalBitmap );

    Assert.assertEquals( 2, rotatedBitmap.getWidth() );
    Assert.assertEquals( 2, rotatedBitmap.getHeight() );

    Assert.assertEquals( 0xff000001, rotatedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000003, rotatedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000000, rotatedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000002, rotatedBitmap.getPixel( 1, 1 ) );
    }

  public void testRotateAnticlockwiseBitmap2()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 4, 1, Bitmap.Config.ARGB_8888 );
    originalBitmap.setPixel( 0, 0, 0xff000000 );
    originalBitmap.setPixel( 1, 0, 0xff000001 );
    originalBitmap.setPixel( 2, 0, 0xff000002 );
    originalBitmap.setPixel( 3, 0, 0xff000003 );

    Bitmap rotatedBitmap = ImageAgent.rotateAnticlockwiseBitmap( originalBitmap );

    Assert.assertEquals( 1, rotatedBitmap.getWidth() );
    Assert.assertEquals( 4, rotatedBitmap.getHeight() );

    Assert.assertEquals( 0xff000003, rotatedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000002, rotatedBitmap.getPixel( 0, 1 ) );
    Assert.assertEquals( 0xff000001, rotatedBitmap.getPixel( 0, 2 ) );
    Assert.assertEquals( 0xff000000, rotatedBitmap.getPixel( 0, 3 ) );
    }

  public void testRotateAnticlockwiseBitmap3()
    {
    Bitmap originalBitmap = Bitmap.createBitmap( 1, 4, Bitmap.Config.ARGB_8888 );
    originalBitmap.setPixel( 0, 0, 0xff000000 );
    originalBitmap.setPixel( 0, 1, 0xff000001 );
    originalBitmap.setPixel( 0, 2, 0xff000002 );
    originalBitmap.setPixel( 0, 3, 0xff000003 );

    Bitmap rotatedBitmap = ImageAgent.rotateAnticlockwiseBitmap( originalBitmap );

    Assert.assertEquals( 4, rotatedBitmap.getWidth() );
    Assert.assertEquals( 1, rotatedBitmap.getHeight() );

    Assert.assertEquals( 0xff000000, rotatedBitmap.getPixel( 0, 0 ) );
    Assert.assertEquals( 0xff000001, rotatedBitmap.getPixel( 1, 0 ) );
    Assert.assertEquals( 0xff000002, rotatedBitmap.getPixel( 2, 0 ) );
    Assert.assertEquals( 0xff000003, rotatedBitmap.getPixel( 3, 0 ) );
    }


  ////////// Inner Class(es) //////////

  }
