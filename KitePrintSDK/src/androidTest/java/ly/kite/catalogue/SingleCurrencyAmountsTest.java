/*****************************************************
 *
 * SingleCurrencyAmountsTest.java
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
public class SingleCurrencyAmountsTest extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "SingleCurrencyAmountsTest";


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
      SingleCurrencyAmounts amount = new SingleCurrencyAmounts( null, BigDecimal.valueOf( 23.20 ) );

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
      SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), null );

      Assert.fail();
      }
    catch ( IllegalArgumentException iae )
      {
      }
    }

  public void testConstructor3()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 23.20 ), null, null );

    Assert.assertEquals( "GBP", amount.getCurrencyCode() );
    Assert.assertEquals( 23.20, amount.getAmountAsDouble() );
    Assert.assertEquals( null, amount.getFormattedAmount() );
    Assert.assertEquals( 0d, amount.getOriginalAmountAsDouble() );
    }

  public void testConstructor4()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 23.20 ), "£23.40" );

    Assert.assertEquals( "GBP", amount.getCurrencyCode() );
    Assert.assertEquals( 23.20, amount.getAmountAsDouble() );
    Assert.assertEquals( "£23.40", amount.getFormattedAmount() );
    Assert.assertEquals( 0d, amount.getOriginalAmountAsDouble() );
    }

  public void testConstructor5()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 23.20 ), "£23.40", BigDecimal.valueOf( 25.00 ) );

    Assert.assertEquals( "GBP", amount.getCurrencyCode() );
    Assert.assertEquals( 23.20, amount.getAmountAsDouble() );
    Assert.assertEquals( "£23.40", amount.getFormattedAmount() );
    Assert.assertEquals( 25.00, amount.getOriginalAmountAsDouble() );
    }



  /*****************************************************
   *
   * Parcel tests.
   *
   *****************************************************/

  public void testParcel1()
    {
    SingleCurrencyAmounts writeAmount = new SingleCurrencyAmounts( Currency.getInstance( "USD" ), BigDecimal.valueOf( 4.99 ) );

    Parcel parcel = Parcel.obtain();

    writeAmount.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );
    SingleCurrencyAmounts readAmount = new SingleCurrencyAmounts( parcel );

    Assert.assertEquals( "USD", readAmount.getCurrencyCode() );
    Assert.assertEquals( 4.99,  readAmount.getAmountAsDouble() );
    Assert.assertEquals( null,  readAmount.getFormattedAmount() );
    Assert.assertEquals( 0d,  readAmount.getOriginalAmountAsDouble() );

    parcel.recycle();
    }

  public void testParcel2()
    {
    SingleCurrencyAmounts writeAmount = new SingleCurrencyAmounts( Currency.getInstance( "USD" ), BigDecimal.valueOf( 4.99 ), "£12.99" );

    Parcel parcel = Parcel.obtain();

    writeAmount.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );
    SingleCurrencyAmounts readAmount = new SingleCurrencyAmounts( parcel );

    Assert.assertEquals( "USD",    readAmount.getCurrencyCode() );
    Assert.assertEquals( 4.99,     readAmount.getAmountAsDouble() );
    Assert.assertEquals( "£12.99", readAmount.getFormattedAmount() );
    Assert.assertEquals( 0d,  readAmount.getOriginalAmountAsDouble() );

    parcel.recycle();
    }

  public void testParcel3()
    {
    SingleCurrencyAmounts writeAmount = new SingleCurrencyAmounts( Currency.getInstance( "USD" ), BigDecimal.valueOf( 4.99 ), "£12.99", BigDecimal.valueOf ( 7.99 ) );

    Parcel parcel = Parcel.obtain();

    writeAmount.writeToParcel( parcel, 0 );

    parcel.setDataPosition( 0 );
    SingleCurrencyAmounts readAmount = new SingleCurrencyAmounts( parcel );

    Assert.assertEquals( "USD",    readAmount.getCurrencyCode() );
    Assert.assertEquals( 4.99,     readAmount.getAmountAsDouble() );
    Assert.assertEquals( "£12.99", readAmount.getFormattedAmount() );
    Assert.assertEquals(7.99,  readAmount.getOriginalAmountAsDouble() );

    parcel.recycle();
    }


  /*****************************************************
   *
   * Zero tests.
   *
   *****************************************************/

  public void testZero1()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Assert.assertFalse( amount.isZero() );
    Assert.assertTrue( amount.isNonZero() );
    }

  public void testZero2()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 0.00 ) );

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
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Assert.assertEquals( "AUD 12.49", amount.getDisplayAmountForLocale( null ) );
    }

  public void testDisplayAmount2()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "AUD" ), BigDecimal.valueOf( 12.49 ) );

    Locale locale = new Locale( "en", "AU" );

    Assert.assertEquals( "$12.49", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount3()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "SEK" ), BigDecimal.valueOf( 4.79 ) );

    Locale locale = new Locale( "sv", "SE" );

    // The Unicode non-breaking space is used instead of the ASCII space
    Assert.assertEquals( "4,79\u00a0kr", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount4()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "SEK" ), BigDecimal.valueOf( 4.79 ) );

    Locale locale = new Locale( "no", "NO" );

    Assert.assertEquals( "SEK 4.79", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount5()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "GB" );

    Assert.assertEquals( "£2.99", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount6()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "US" );

    Assert.assertEquals( "GBP 2.99", amount.getDisplayAmountForLocale( locale ) );
    }

  // Unsupported locale
  public void testDisplayAmount7()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ) );

    Locale locale = new Locale( "en", "ZZ" );

    Assert.assertEquals( "GBP 2.99", amount.getDisplayAmountForLocale( locale ) );
    }

  public void testDisplayAmount8()
    {
    SingleCurrencyAmounts amount = new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 2.99 ), BigDecimal.valueOf( 4.99 ) );

    Locale locale = new Locale( "en", "ZZ" );

    Assert.assertEquals( "GBP 2.99", amount.getDisplayAmountForLocale( locale ) );
    Assert.assertEquals( "GBP 4.99", amount.getDisplayOriginalAmountForLocale( locale ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

