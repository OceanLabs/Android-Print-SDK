/*****************************************************
 *
 * BorderFTests.java
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
public class BorderFTests extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "BorderFTests";


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
    BorderF borderF = new BorderF( 1f, 2f, 3f, 4f );

    Assert.assertEquals( 1f, borderF.top );
    Assert.assertEquals( 2f, borderF.right );
    Assert.assertEquals( 3f, borderF.bottom );
    Assert.assertEquals( 4f, borderF.left );
    }


  /*****************************************************
   *
   * Parcel tests.
   *
   *****************************************************/

  public void testParcel1()
    {
    BorderF originalBorderF = new BorderF( 10f, 11f, 12f, 13f );

    Parcel parcel = Parcel.obtain();

    originalBorderF.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );

    BorderF readBorderF = BorderF.CREATOR.createFromParcel( parcel );

    Assert.assertEquals( 10f, readBorderF.top );
    Assert.assertEquals( 11f, readBorderF.right );
    Assert.assertEquals( 12f, readBorderF.bottom );
    Assert.assertEquals( 13f, readBorderF.left );

    parcel.recycle();
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

