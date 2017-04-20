/*****************************************************
 *
 * AssetTests.java
 *
 *
 * Modified MIT License
 *
 * Copyright (c) 2010-2017 Kite Tech Ltd. https://www.kite.ly
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

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.Assert;

import ly.kite.KiteTestCase;
import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the Asset class.
 *
 *****************************************************/
public class AssetTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "AssetTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Create from string tests.
   *
   *****************************************************/

  public void testCreateFromString()
    {
    Asset asset;


    asset = Asset.create( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "http://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( "https://psps.s3.amazonaws.com/sdk_static/1.jpg" );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "https://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( "file:///storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg" );

    Assert.assertEquals( Asset.Type.IMAGE_FILE, asset.getType() );
    Assert.assertEquals( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg", asset.getImageFilePath() );
    Assert.assertEquals( "IMG_20161219_094113.jpg", asset.getImageFileName() );


    asset = Asset.create( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg" );

    Assert.assertEquals( Asset.Type.IMAGE_FILE, asset.getType() );
    Assert.assertEquals( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg", asset.getImageFilePath() );
    Assert.assertEquals( "IMG_20161219_094113.jpg", asset.getImageFileName() );


    asset = Asset.create( "content://media/external/images/media/22572" );

    Assert.assertEquals( Asset.Type.IMAGE_URI, asset.getType() );
    Assert.assertEquals( "content://media/external/images/media/22572", asset.getImageURI().toString() );
    }


  /*****************************************************
   *
   * Create from URL tests.
   *
   *****************************************************/

  public void testCreateFromURL() throws MalformedURLException
    {
    Asset asset;


    asset = Asset.create( new URL( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "http://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( new URL( "https://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "https://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( new URL( "file:///storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg" ) );

    Assert.assertEquals( Asset.Type.IMAGE_FILE, asset.getType() );
    Assert.assertEquals( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg", asset.getImageFilePath() );
    Assert.assertEquals( "IMG_20161219_094113.jpg", asset.getImageFileName() );
    }


  /*****************************************************
   *
   * Create from URI tests.
   *
   *****************************************************/

  public void testCreateFromURI()
    {
    Asset asset;


    asset = Asset.create( Uri.parse( "http://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "http://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( Uri.parse( "https://psps.s3.amazonaws.com/sdk_static/1.jpg" ) );

    Assert.assertEquals( Asset.Type.REMOTE_URL, asset.getType() );
    Assert.assertEquals( "https://psps.s3.amazonaws.com/sdk_static/1.jpg", asset.getRemoteURL().toString() );
    Assert.assertEquals( Asset.MIMEType.JPEG, asset.getMIMEType() );


    asset = Asset.create( Uri.parse( "file:///storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg" ) );

    Assert.assertEquals( Asset.Type.IMAGE_FILE, asset.getType() );
    Assert.assertEquals( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg", asset.getImageFilePath() );
    Assert.assertEquals( "IMG_20161219_094113.jpg", asset.getImageFileName() );


    asset = Asset.create( Uri.parse( "content://media/external/images/media/22572" ) );

    Assert.assertEquals( Asset.Type.IMAGE_URI, asset.getType() );
    Assert.assertEquals( "content://media/external/images/media/22572", asset.getImageURI().toString() );
    }


  /*****************************************************
   *
   * Create from File tests.
   *
   *****************************************************/

  public void testCreateFromFile()
    {
    Asset asset;


    File file = new File( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg" );

    asset = Asset.create( file );

    Assert.assertEquals( Asset.Type.IMAGE_FILE, asset.getType() );
    Assert.assertEquals( "/storage/emulated/0/DCIM/Camera/IMG_20161219_094113.jpg", asset.getImageFilePath() );
    Assert.assertEquals( "IMG_20161219_094113.jpg", asset.getImageFileName() );
    }


  /*****************************************************
   *
   * Create from Bitmap tests.
   *
   *****************************************************/

  public void testCreateFromBitmap()
    {
    Asset asset;


    Bitmap bitmap = Bitmap.createBitmap( 24, 24, Bitmap.Config.ARGB_8888 );

    asset = Asset.create( bitmap );

    Assert.assertEquals( Asset.Type.BITMAP, asset.getType() );
    Assert.assertTrue( asset.getBitmap() == bitmap );
    }


  /*****************************************************
   *
   * Create from Bitmap resource id tests.
   *
   *****************************************************/

  public void testCreateFromBitmapResourceId()
    {
    Asset asset;


    // Explicit Integer
    asset = Asset.create( Integer.valueOf( R.drawable.placeholder ) );

    Assert.assertEquals( Asset.Type.BITMAP_RESOURCE_ID, asset.getType() );
    Assert.assertEquals( R.drawable.placeholder, asset.getBitmapResourceId() );


    // Boxed Integer
    asset = Asset.create( R.drawable.placeholder );

    Assert.assertEquals( Asset.Type.BITMAP_RESOURCE_ID, asset.getType() );
    Assert.assertEquals( R.drawable.placeholder, asset.getBitmapResourceId() );
    }


  ////////// Inner Class(es) //////////

  }

