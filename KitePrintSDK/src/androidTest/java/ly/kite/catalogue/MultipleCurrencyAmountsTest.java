/*****************************************************
 *
 * MultipleCurrencyAmountsTest.java
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

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the single currency amount class.
 *
 *****************************************************/
public class MultipleCurrencyAmountsTest extends TestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "MultipleCurrencyAmountsTest";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Fallback tests.
   *
   *****************************************************/

  public void testFallback1()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( "GBP" );

    Assert.assertEquals( "ILS", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 4.99, singleAmounts.getAmountAsDouble() );
    }

  public void testFallback2()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 3.00 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( Locale.UK );

    Assert.assertEquals( "ILS", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 3.00, singleAmounts.getAmountAsDouble() );
    }

  public void testFallback3()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( Locale.UK );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback4()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( (Currency)null );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback5()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( (Locale)null );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback6()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( new Locale( "en" ) );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback7()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( new Locale( "fa" ) );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback8()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( new Locale( "en", "en" ) );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  public void testFallback9()
    {
    MultipleCurrencyAmounts multipleAmounts = new MultipleCurrencyAmounts();

    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "ILS" ), BigDecimal.valueOf( 4.99 ) ) );
    multipleAmounts.add( new SingleCurrencyAmounts( Currency.getInstance( "GBP" ), BigDecimal.valueOf( 1.49 ), BigDecimal.valueOf( 2.99 ) ) );

    SingleCurrencyAmounts singleAmounts = multipleAmounts.getAmountsWithFallback( new Locale( "fa", "FA" ) );

    Assert.assertEquals( "GBP", singleAmounts.getCurrencyCode() );
    Assert.assertEquals( 1.49, singleAmounts.getAmountAsDouble() );
    Assert.assertEquals( 2.99, singleAmounts.getOriginalAmountAsDouble() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
