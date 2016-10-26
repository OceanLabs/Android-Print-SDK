/*****************************************************
 *
 * BleedTests.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.os.Parcel;

import junit.framework.Assert;
import junit.framework.TestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the single currency amount class.
 *
 *****************************************************/
public class BleedTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "BleedTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Constructor tests.
   *
   *****************************************************/

  public void testConstructor1()
    {
    Bleed bleed = new Bleed( 1, 2, 3, 4 );

    Assert.assertEquals( 1, bleed.topPixels );
    Assert.assertEquals( 2, bleed.rightPixels );
    Assert.assertEquals( 3, bleed.bottomPixels );
    Assert.assertEquals( 4, bleed.leftPixels );
    }


  /*****************************************************
   *
   * Parcel tests.
   *
   *****************************************************/

  public void testParcel1()
    {
    Bleed originalBleed = new Bleed( 10, 11, 12, 13 );

    Parcel parcel = Parcel.obtain();

    originalBleed.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );

    Bleed readBleed = Bleed.CREATOR.createFromParcel( parcel );

    Assert.assertEquals( 10, readBleed.topPixels );
    Assert.assertEquals( 11, readBleed.rightPixels );
    Assert.assertEquals( 12, readBleed.bottomPixels );
    Assert.assertEquals( 13, readBleed.leftPixels );

    parcel.recycle();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

