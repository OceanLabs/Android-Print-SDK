/*****************************************************
 *
 * KiteTestCase.java
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

package ly.kite;


///// Import(s) /////

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import ly.kite.catalogue.Product;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;
import ly.kite.util.AssetHelper;


///// Class Declaration /////

/*****************************************************
 *
 * This is the parent of Kite unit test cases.
 *
 *****************************************************/
public class KiteTestCase extends AndroidTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "KiteTestCase";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// AndroidTestCase Method(s) //////////


  ////////// Method(s) //////////


  /*****************************************************
   *
   * Checks the asset file name and quantity of an image spec.
   *
   *****************************************************/
  protected void assertProperties( ImageSpec imageSpec, String filePath, String borderText, int quantity )
    {
    if ( filePath != null ) Assert.assertEquals( filePath, imageSpec.getAsset().getImageFilePath() );
    Assert.assertEquals( borderText, imageSpec.getBorderText() );
    Assert.assertEquals( quantity, imageSpec.getQuantity() );
    }


  /*****************************************************
   *
   * Populates a dummy asset.
   *
   *****************************************************/
  protected Asset createSessionAssetFile()
    {
    byte[] dummyBytes = new byte[] { 0x3f, 0x12, 0x45 };

    return ( AssetHelper.createAsSessionAsset( getContext(), dummyBytes, Asset.MIMEType.JPEG ) );
    }


  ////////// Inner Class(es) //////////

  }