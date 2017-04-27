/*****************************************************
 *
 * UploadableImageTests.java
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

package ly.kite.util;


///// Import(s) /////

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import ly.kite.KiteTestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the string utils class.
 *
 *****************************************************/
public class UploadableImageTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "UploadableImageTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Are both null or equal tests
   *
   *****************************************************/

  public void testAreBothNullOrEqual1()
    {
    Asset asset1 = Asset.create( "/temp/image1.jpg" );
    Asset asset2 = Asset.create( "/temp/image2.jpg" );
    Asset asset3 = Asset.create( "/temp/image1.jpg" );

    UploadableImage uploadableImage1 = new UploadableImage( asset1 );
    UploadableImage uploadableImage2 = new UploadableImage( asset2 );
    UploadableImage uploadableImage3 = new UploadableImage( asset3 );

    Assert.assertTrue( UploadableImage.areBothNullOrEqual( (UploadableImage)null, (UploadableImage)null ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( (UploadableImage)null, uploadableImage1 ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImage2, (UploadableImage)null ) );

    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImage1, uploadableImage2 ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImage2, uploadableImage3 ) );
    Assert.assertTrue( UploadableImage.areBothNullOrEqual( uploadableImage1, uploadableImage3 ) );
    }

  public void testAreBothNullOrEqual2()
    {
    Asset asset1 = Asset.create( "/temp/image1.jpg" );
    Asset asset2 = Asset.create( "/temp/image2.jpg" );
    Asset asset3 = Asset.create( "/temp/image1.jpg" );

    UploadableImage uploadableImage1 = new UploadableImage( asset1 );
    UploadableImage uploadableImage2 = new UploadableImage( asset2 );
    UploadableImage uploadableImage3 = new UploadableImage( asset3 );

    List<UploadableImage> uploadableImageList1 = new ArrayList<>();

    List<UploadableImage> uploadableImageList2 = new ArrayList<>();
    uploadableImageList2.add( uploadableImage1 );

    List<UploadableImage> uploadableImageList3 = new ArrayList<>();
    uploadableImageList3.add( uploadableImage2 );
    uploadableImageList3.add( uploadableImage3 );

    List<UploadableImage> uploadableImageList4 = new ArrayList<>();
    uploadableImageList4.add( uploadableImage1 );
    uploadableImageList4.add( uploadableImage3 );

    List<UploadableImage> uploadableImageList5 = new ArrayList<>();
    uploadableImageList5.add( uploadableImage3 );
    uploadableImageList5.add( uploadableImage1 );

    List<UploadableImage> uploadableImageList6 = new ArrayList<>();
    uploadableImageList6.add( uploadableImage1 );
    uploadableImageList6.add( null );
    uploadableImageList6.add( uploadableImage3 );

    List<UploadableImage> uploadableImageList7 = new ArrayList<>();
    uploadableImageList7.add( uploadableImage3 );
    uploadableImageList7.add( null );
    uploadableImageList7.add( uploadableImage1 );


    Assert.assertTrue( UploadableImage.areBothNullOrEqual( (List<UploadableImage>)null, (List<UploadableImage>)null ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImageList1, (List<UploadableImage>)null ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImageList2, (List<UploadableImage>)null ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( (List<UploadableImage>)null, uploadableImageList3 ) );

    Assert.assertTrue( UploadableImage.areBothNullOrEqual( uploadableImageList4, uploadableImageList5 ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImageList3, uploadableImageList4 ) );
    Assert.assertFalse( UploadableImage.areBothNullOrEqual( uploadableImageList5, uploadableImageList6 ) );
    Assert.assertTrue( UploadableImage.areBothNullOrEqual( uploadableImageList6, uploadableImageList7 ) );
    }


  /*****************************************************
   *
   * Misc tests
   *
   *****************************************************/

  public void testMarkAsUploaded()
    {
    Asset asset = Asset.create( "/temp/image1.jpg" );

    UploadableImage uploadableImage = new UploadableImage( asset );

    Assert.assertFalse( uploadableImage.hasBeenUploaded() );

    uploadableImage.markAsUploaded( 267834L, null );

    Assert.assertTrue( uploadableImage.hasBeenUploaded() );
    Assert.assertEquals( 267834L, uploadableImage.getUploadedAssetId() );
    Assert.assertEquals( null, uploadableImage.getPreviewURL() );
    }


  public void testEquals()
    {
    Asset asset1 = Asset.create( "/temp/image1.jpg" );
    Asset asset2 = Asset.create( "http://www.kite.ly/no-image.jpg" );
    Asset asset3 = Asset.create( "/temp/image1.jpg" );

    UploadableImage uploadableImage1 = new UploadableImage( asset1 );
    UploadableImage uploadableImage2 = new UploadableImage( asset2 );
    UploadableImage uploadableImage3 = new UploadableImage( asset3 );

    Assert.assertFalse( uploadableImage1.equals( uploadableImage2 ) );
    Assert.assertTrue( uploadableImage1.equals( uploadableImage3 ) );

    uploadableImage1.markAsUploaded( 267834L, null );

    Assert.assertFalse( uploadableImage1.equals( uploadableImage3 ) );

    uploadableImage3.markAsUploaded( 267834L, null );

    Assert.assertTrue( uploadableImage1.equals( uploadableImage3 ) );
    }


  ////////// Inner Class(es) //////////

  }

