/*****************************************************
 *
 * SingleCurrencyCost.java
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

package ly.kite.product;


///// Import(s) /////


///// Class Declaration /////

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/*****************************************************
 *
 * This class represents a cost in a single currency.
 *
 *****************************************************/
public class SingleCurrencyCost implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                     = "SingleCurrencyCost";

  private static final String  FORMAL_AMOUNT_FORMAT_STRING = "$1$s $2$.2f";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<SingleCurrencyCost> CREATOR =
    new Parcelable.Creator<SingleCurrencyCost>()
      {
      public SingleCurrencyCost createFromParcel( Parcel sourceParcel )
        {
        return ( new SingleCurrencyCost( sourceParcel ) );
        }

      public SingleCurrencyCost[] newArray( int size )
        {
        return ( new SingleCurrencyCost[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private Currency    mCurrency;
  private BigDecimal  mAmount;
  private String      mFormattedAmount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public SingleCurrencyCost( Currency currency, BigDecimal amount, String formattedAmount )
    {
    mCurrency        = currency;
    mAmount          = amount;
    mFormattedAmount = formattedAmount;
    }


  public SingleCurrencyCost( Currency currency, BigDecimal amount )
    {
    this ( currency, amount, null );
    }


  // Constructor used by parcelable interface
  private SingleCurrencyCost( Parcel parcel )
    {
    mCurrency        = (Currency)parcel.readSerializable();
    mAmount          = (BigDecimal)parcel.readSerializable();
    mFormattedAmount = parcel.readString();
    }


  ////////// Parcelable Method(s) //////////

  /*****************************************************
   *
   * Describes the contents.
   *
   *****************************************************/
  @Override
  public int describeContents()
    {
    return ( 0 );
    }


  /*****************************************************
   *
   * Write the contents to a parcel.
   *
   *****************************************************/
  @Override
  public void writeToParcel( Parcel targetParcel, int flags )
    {
    targetParcel.writeSerializable( mCurrency );
    targetParcel.writeSerializable( mAmount );
    targetParcel.writeString( mFormattedAmount );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the currency.
   *
   *****************************************************/
  public Currency getCurrency()
    {
    return ( mCurrency );
    }


  /*****************************************************
   *
   * Returns the currency code.
   *
   *****************************************************/
  public String getCurrencyCode()
    {
    return ( mCurrency.getCurrencyCode() );
    }


  /*****************************************************
   *
   * Returns the amount.
   *
   *****************************************************/
  public BigDecimal getAmount()
    {
    return ( mAmount );
    }


  /*****************************************************
   *
   * Returns the amount as a double.
   *
   *****************************************************/
  public double getAmountAsDouble()
    {
    return ( mAmount.doubleValue() );
    }


  /*****************************************************
   *
   * Returns the formatted.
   *
   *****************************************************/
  public String getFormattedAmount()
    {
    return ( mFormattedAmount );
    }


  /*****************************************************
   *
   * Returns the amount as a displayable string for the
   * supplied locale.
   *
   * If our currency is the same as the main currency in use
   * for the supplied locale, then we use the number formatter
   * to format the amount - which will give a localised string.
   *
   * If the currency is different, then we format the amount with
   * the full currency code. We do this to avoid any ambiguity.
   * For example, if we were to live in Sweden but found a cost
   * in Danish Krone, then having an amount such as "4.00 kr"
   * would be misleading (because we would believe we were being
   * quoted in Swedish Kronor).
   *
   *****************************************************/
  public String getDisplayAmountForLocale( Locale locale )
    {
    // Get the currency used by the locale
    Currency localeCurrency = Currency.getInstance( locale );


    // If the currency matches the current locale's currency - use the number formatter

    if ( mCurrency.equals( localeCurrency ) )
      {
      NumberFormat numberFormatter = NumberFormat.getCurrencyInstance( locale );

      return ( numberFormatter.format( getAmountAsDouble() ) );
      }


    // Format the amount formally
    return ( String.format( FORMAL_AMOUNT_FORMAT_STRING, mCurrency, getAmountAsDouble() ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

