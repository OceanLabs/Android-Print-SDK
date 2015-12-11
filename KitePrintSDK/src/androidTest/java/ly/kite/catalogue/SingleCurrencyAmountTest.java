/*****************************************************
 *
 * SingleCurrencyAmountTest.java
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

package ly.kite.catalogue;


///// Import(s) /////

import android.os.Parcel;
import android.util.Log;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the single currency amount class.
 *
 *****************************************************/
public class SingleCurrencyAmountTest extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "SingleCurrencyAmountTest";


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
    try
      {
      SingleCurrencyAmount amount = new SingleCurrencyAmount( null, BigDecimal.valueOf( 23.20 ) );

      Assert.fail();
      }
    catch ( IllegalArgumentException iae )
      {
      }
    }

  public void testConstructor2()
    {
    try
      {
      SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), null );

      Assert.fail();
      }
    catch ( IllegalArgumentException iae )
      {
      }
    }

  public void testConstructor3()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 23.20 ), null );

    Assert.assertEquals( "GBP", amount.getCurrencyCode() );
    Assert.assertEquals( 23.20, amount.getAmountAsDouble() );
    Assert.assertEquals( null, amount.getFormattedAmount() );
    }

  public void testConstructor4()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 23.20 ), "$23.40" );

    Assert.assertEquals( "GBP", amount.getCurrencyCode() );
    Assert.assertEquals( 23.20, amount.getAmountAsDouble() );
    Assert.assertEquals( "$23.40", amount.getFormattedAmount() );
    }



  /*****************************************************
   *
   * Parcel tests.
   *
   *****************************************************/

  public void testParcel1()
    {
    SingleCurrencyAmount writeAmount = new SingleCurrencyAmount( Currency.getInstance( "USD" ), BigDecimal.valueOf( 4.99 ) );

    Parcel parcel = Parcel.obtain();

    writeAmount.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );
    SingleCurrencyAmount readAmount = new SingleCurrencyAmount( parcel );

    Assert.assertEquals( "USD", readAmount.getCurrencyCode() );
    Assert.assertEquals( 4.99,  readAmount.getAmountAsDouble() );
    Assert.assertEquals( null,  readAmount.getFormattedAmount() );

    parcel.recycle();
    }

  public void testParcel2()
    {
    SingleCurrencyAmount writeAmount = new SingleCurrencyAmount( Currency.getInstance( "USD" ), BigDecimal.valueOf( 4.99 ), "£12.99" );

    Parcel parcel = Parcel.obtain();

    writeAmount.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );
    SingleCurrencyAmount readAmount = new SingleCurrencyAmount( parcel );

    Assert.assertEquals( "USD",    readAmount.getCurrencyCode() );
    Assert.assertEquals( 4.99,     readAmount.getAmountAsDouble() );
    Assert.assertEquals( "£12.99", readAmount.getFormattedAmount() );

    parcel.recycle();
    }


  /*****************************************************
   *
   * Zero tests.
   *
   *****************************************************/

  public void testZero1()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Assert.assertFalse( amount.isZero() );
    Assert.assertTrue( amount.isNonZero() );
    }

  public void testZero2()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 0.00 ) );

    Assert.assertTrue( amount.isZero() );
    Assert.assertFalse( amount.isNonZero() );
    }


  /*****************************************************
   *
   * Display amount tests.
   *
   *****************************************************/

  public void testDisplayAmount1()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Assert.assertEquals( "AUD 12.49", amount.getDisplayAmountForLocale( null ) );
    }

  public void testDisplayAmount2()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Locale locale = new Locale( "en", "AU" );

    Assert.assertEquals( "$12.49", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount3()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "SEK" ), BigDecimal.valueOf( 4.79 ) );

    Locale locale = new Locale( "sv", "SE" );

    // The Unicode non-breaking space is used instead of the ASCII space
    Assert.assertEquals( "4,79\u00a0kr", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount4()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "SEK" ), BigDecimal.valueOf( 4.79 ) );

    Locale locale = new Locale( "no", "NO" );

    Assert.assertEquals( "SEK 4.79", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount5()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "GB" );

    Assert.assertEquals( "£2.99", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount6()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "US" );

    Assert.assertEquals( "GBP 2.99", amount.getDisplayAmountForLocale( locale ) );
    }

  // Unsupported locale
  public void testDisplayAmount7()
    {
    SingleCurrencyAmount amount = new SingleCurrencyAmount( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "ZZ" );

    Assert.assertEquals( "GBP 2.99", amount.getDisplayAmountForLocale( locale ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

