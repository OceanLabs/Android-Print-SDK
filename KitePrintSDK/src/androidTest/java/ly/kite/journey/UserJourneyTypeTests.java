/*****************************************************
 *
 * UserJourneyTypeTests.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2017 Kite Tech Ltd. https://www.kite.ly
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

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the image agent class.
 *
 *****************************************************/
public class UserJourneyTypeTests extends AndroidTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "UserJourneyTypeTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////


  /*****************************************************
   *
   * Flatten tests.
   *
   *****************************************************/

  public void testFlatten1()
    {
    ArrayList<ImageSpec> sourceImageSpecList = new ArrayList<>();

    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image1.jpg" ) ), null, 1 ) );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image2.jpg" ) ), "Border text", 1 ) );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image3.jpg" ) ), null, 2 ) );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image4.jpg" ) ), "More border text", 3 ) );

    List<ImageSpec> flattenedImageSpecList = UserJourneyType.flattenImageSpecList( sourceImageSpecList, false );

    Assert.assertEquals( 7, flattenedImageSpecList.size() );
    assertProperties( flattenedImageSpecList.get( 0 ), "/tmp/image1.jpg", null, 1 );
    assertProperties( flattenedImageSpecList.get( 1 ), "/tmp/image2.jpg", "Border text", 1 );
    assertProperties( flattenedImageSpecList.get( 2 ), "/tmp/image3.jpg", null, 1 );
    assertProperties( flattenedImageSpecList.get( 3 ), "/tmp/image3.jpg", null, 1 );
    assertProperties( flattenedImageSpecList.get( 4 ), "/tmp/image4.jpg", "More border text", 1 );
    assertProperties( flattenedImageSpecList.get( 5 ), "/tmp/image4.jpg", "More border text", 1  );
    assertProperties( flattenedImageSpecList.get( 6 ), "/tmp/image4.jpg", "More border text", 1  );
    }

  public void testFlatten2()
    {
    ArrayList<ImageSpec> sourceImageSpecList = new ArrayList<>();

    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image1.jpg" ) ), null, 1 ) );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image2.jpg" ) ), "Border text", 1 ) );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image3.jpg" ) ), null, 2 ) );
    sourceImageSpecList.add( null );
    sourceImageSpecList.add( new ImageSpec( new AssetFragment( new Asset( "/tmp/image4.jpg" ) ), "More border text", 3 ) );

    List<ImageSpec> flattenedImageSpecList = UserJourneyType.flattenImageSpecList( sourceImageSpecList, true );

    Assert.assertEquals( 11, flattenedImageSpecList.size() );
    assertNull( flattenedImageSpecList.get( 0 ) );
    assertProperties( flattenedImageSpecList.get( 1 ), "/tmp/image1.jpg", null, 1 );
    assertProperties( flattenedImageSpecList.get( 2 ), "/tmp/image2.jpg", "Border text", 1 );
    assertNull( flattenedImageSpecList.get( 3 ) );
    assertNull( flattenedImageSpecList.get( 4 ) );
    assertProperties( flattenedImageSpecList.get( 5 ), "/tmp/image3.jpg", null, 1 );
    assertProperties( flattenedImageSpecList.get( 6 ), "/tmp/image3.jpg", null, 1 );
    assertNull( flattenedImageSpecList.get( 7 ) );
    assertProperties( flattenedImageSpecList.get( 8 ), "/tmp/image4.jpg", "More border text", 1 );
    assertProperties( flattenedImageSpecList.get( 9 ), "/tmp/image4.jpg", "More border text", 1 );
    assertProperties( flattenedImageSpecList.get( 10 ), "/tmp/image4.jpg", "More border text", 1 );
    }


  /*****************************************************
   *
   * Checks the asset file name and quantity of an image spec.
   *
   *****************************************************/
  private void assertProperties( ImageSpec imageSpec, String filePath, String borderText, int quantity )
    {
    Assert.assertEquals( filePath, imageSpec.getAsset().getImageFilePath() );
    Assert.assertEquals( borderText, imageSpec.getBorderText() );
    Assert.assertEquals( quantity, imageSpec.getQuantity() );
    }



  ////////// Inner Class(es) //////////

  }