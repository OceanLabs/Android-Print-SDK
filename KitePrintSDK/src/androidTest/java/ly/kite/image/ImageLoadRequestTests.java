/*****************************************************
 *
 * ImageLoadRequestTests.java
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

import android.media.ExifInterface;

import junit.framework.Assert;
import junit.framework.TestCase;

import ly.kite.image.ImageLoadRequest;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the image loader class.
 *
 *****************************************************/
public class ImageLoadRequestTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ImageLoadRequestTests";


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

  public void testDegreesFromEXIFOrientation()
    {
    Assert.assertEquals(   0, ImageLoadRequest.degreesFromEXIFOrientation( ExifInterface.ORIENTATION_NORMAL ) );
    Assert.assertEquals(  90, ImageLoadRequest.degreesFromEXIFOrientation( ExifInterface.ORIENTATION_ROTATE_90 ) );
    Assert.assertEquals( 180, ImageLoadRequest.degreesFromEXIFOrientation( ExifInterface.ORIENTATION_ROTATE_180 ) );
    Assert.assertEquals( 270, ImageLoadRequest.degreesFromEXIFOrientation( ExifInterface.ORIENTATION_ROTATE_270 ) );
    }


  public void testSampleSizeForResize()
    {
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768, 1024, 768 ) );
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768, 1023, 768 ) );
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768, 1024, 767 ) );

    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768, 513, 385 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 512, 385 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 511, 385 ) );

    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768, 513, 385 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 513, 384 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 513, 383 ) );

    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 512, 384 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 511, 384 ) );
    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 512, 383 ) );

    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 257, 193 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 256, 193 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 255, 193 ) );

    Assert.assertEquals(   2, ImageLoadRequest.sampleSizeForResize( 1024, 768, 257, 193 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 257, 192 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 257, 191 ) );

    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 256, 192 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 255, 192 ) );
    Assert.assertEquals(   4, ImageLoadRequest.sampleSizeForResize( 1024, 768, 256, 191 ) );

    Assert.assertEquals(   8, ImageLoadRequest.sampleSizeForResize( 1024, 768, 128,  96 ) );

    Assert.assertEquals(  16, ImageLoadRequest.sampleSizeForResize( 1024, 768,  64,  48 ) );

    Assert.assertEquals(  32, ImageLoadRequest.sampleSizeForResize( 1024, 768,  32,  24 ) );

    Assert.assertEquals(  64, ImageLoadRequest.sampleSizeForResize( 1024, 768,  16,  12 ) );

    Assert.assertEquals( 128, ImageLoadRequest.sampleSizeForResize( 1024, 768,   8,  6 ) );

    Assert.assertEquals( 256, ImageLoadRequest.sampleSizeForResize( 1024, 768,   4,  3 ) );

    Assert.assertEquals( 512, ImageLoadRequest.sampleSizeForResize( 1024, 768,   2,  1 ) );

    Assert.assertEquals( 512, ImageLoadRequest.sampleSizeForResize( 1024, 768,   1,  1 ) );

    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768,   0,   0 ) );
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768,  -1,   0 ) );
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768,   0,  -1 ) );
    Assert.assertEquals(   1, ImageLoadRequest.sampleSizeForResize( 1024, 768,  -1,  -1 ) );
    }

  }
