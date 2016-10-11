/*****************************************************
 *
 * SingleCurrencyAmounts.java
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
import android.os.Parcelable;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents an amount in a single currency.
 *
 *****************************************************/
public class SingleCurrencyAmounts implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                     = "SingleCurrencyAmounts";

  private static final String  FORMAL_AMOUNT_FORMAT_STRING = "%1$s %2$.2f";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<SingleCurrencyAmounts> CREATOR =
    new Parcelable.Creator<SingleCurrencyAmounts>()
      {
      public SingleCurrencyAmounts createFromParcel( Parcel sourceParcel )
        {
        return ( new SingleCurrencyAmounts( sourceParcel ) );
        }

      public SingleCurrencyAmounts[] newArray( int size )
        {
        return ( new SingleCurrencyAmounts[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private Currency    mCurrency;
  private BigDecimal  mAmount;
  private String      mFormattedAmount;
  private BigDecimal  mOriginalAmount;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  /*****************************************************
   *
   * Constructs a new single currency amount.
   *
   * @param currency        The currency that the amount is in.
   * @param amount          A BigDecimal representing the amount.
   * @param formattedAmount A string representation of the amount.
   * @param originalAmount  A BigDecimal representing the original amount
   *                        for strike-through pricing.
   *
   * @throws IllegalArgumentException if either the currency
   *         or the amount is null.
   *
   *****************************************************/
  public SingleCurrencyAmounts( Currency currency, BigDecimal amount, String formattedAmount, BigDecimal originalAmount )
    {
    if ( currency == null ) throw ( new IllegalArgumentException( "Currency must be supplied" ) );
    if ( amount   == null ) throw ( new IllegalArgumentException( "Amount must be supplied" ) );

    mCurrency        = currency;
    mAmount          = amount;
    mFormattedAmount = formattedAmount;
    mOriginalAmount  = originalAmount;
    }


  /*****************************************************
   *
   * Constructs a new single currency amount.
   *
   * @param currency        The currency that the amount is in.
   * @param amount          A BigDecimal representing the amount.
   * @param formattedAmount A string representation of the amount.
   *
   * @throws IllegalArgumentException if either the currency
   *         or the amount is null.
   *
   *****************************************************/
  public SingleCurrencyAmounts( Currency currency, BigDecimal amount, String formattedAmount )
    {
    this( currency, amount, formattedAmount, null );
    }


  /*****************************************************
   *
   * Constructs a new single currency amount, with no formatted
   * amount.
   *
   * @param currency        The currency that the amount is in.
   * @param amount          A BigDecimal representing the amount.
   * @param originalAmount  A BigDecimal representing the original amount
   *                        for strike-through pricing.
   *
   * @throws IllegalArgumentException if either the currency
   *         or the amount is null.
   *
   *****************************************************/
  public SingleCurrencyAmounts( Currency currency, BigDecimal amount, BigDecimal originalAmount )
    {
    this ( currency, amount, null, originalAmount );
    }


  /*****************************************************
   *
   * Constructs a new single currency amount, with no formatted
   * amount.
   *
   * @param currency        The currency that the amount is in.
   * @param amount          A BigDecimal representing the amount.
   *
   * @throws IllegalArgumentException if either the currency
   *         or the amount is null.
   *
   *****************************************************/
  public SingleCurrencyAmounts( Currency currency, BigDecimal amount )
    {
    this ( currency, amount, null, null );
    }


  // Constructor used by parcelable interface
  SingleCurrencyAmounts( Parcel parcel )
    {
    mCurrency        = (Currency)parcel.readSerializable();
    mAmount          = (BigDecimal)parcel.readSerializable();
    mFormattedAmount = parcel.readString();
    mOriginalAmount  = (BigDecimal)parcel.readSerializable();
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
    targetParcel.writeSerializable( mOriginalAmount );
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
   * Returns true if the amount is zero, false otherwise.
   *
   *****************************************************/
  public boolean isZero()
    {
    return ( mAmount.compareTo( BigDecimal.ZERO ) == 0 );
    }


  /*****************************************************
   *
   * Returns true if the amount is non-zero, false otherwise.
   *
   *****************************************************/
  public boolean isNonZero()
    {
    return ( mAmount.compareTo( BigDecimal.ZERO ) != 0 );
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
   * Returns true if the there is an original amount.
   *
   *****************************************************/
  public boolean hasOriginalAmount()
    {
    return ( mOriginalAmount != null );
    }


  /*****************************************************
   *
   * Returns the original amount as a double.
   *
   *****************************************************/
  public double getOriginalAmountAsDouble()
    {
    return ( mOriginalAmount != null ? mOriginalAmount.doubleValue() : 0d );
    }


  /*****************************************************
   *
   * Returns the amount as a displayable string for the
   * supplied locale.
   *
   *****************************************************/
  private String getDisplayAmountForLocale( double amountAsDouble, Locale locale )
    {
    if ( locale != null )
      {
      // The locale may not be supported by this device.
      try
        {
        // Get the currency used by the locale
        Currency localeCurrency = Currency.getInstance( locale );

        // If the currency matches the current locale's currency, use the number formatter to
        // format the amount.
        if ( mCurrency.equals( localeCurrency ) )
          {
          NumberFormat numberFormatter = NumberFormat.getCurrencyInstance( locale );

          return ( numberFormatter.format( amountAsDouble ) );
          }
        }
      catch ( IllegalArgumentException iae )
        {
        // Fall through
        }
      }


    // Format the amount formally
    return ( String.format( Locale.getDefault(), FORMAL_AMOUNT_FORMAT_STRING, mCurrency.getCurrencyCode(), amountAsDouble ) );
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
   * @param locale The locale for which a display amount is required.
   *               If the locale is not supplied or supported by the device,
   *               the display amount is always shown with a currency prefix.
   *
   *****************************************************/
  public String getDisplayAmountForLocale( Locale locale )
    {
    return ( getDisplayAmountForLocale( getAmountAsDouble(), locale ) );
    }


  /*****************************************************
   *
   * Returns the original amount as a displayable string for the
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
   * @param locale The locale for which a display amount is required.
   *               If the locale is not supplied or supported by the device,
   *               the display amount is always shown with a currency prefix.
   *
   *****************************************************/
  public String getDisplayOriginalAmountForLocale( Locale locale )
    {
    return ( hasOriginalAmount() ? getDisplayAmountForLocale( getOriginalAmountAsDouble(), locale ) : null );
    }


  /*****************************************************
   *
   * Returns this amount multiplied by the supplied quantity.
   *
   *****************************************************/
  public SingleCurrencyAmounts multipliedBy( int quantity )
    {
    BigDecimal quantityBigDecimal = BigDecimal.valueOf( quantity );

    BigDecimal multipliedOriginalAmount = ( hasOriginalAmount() ? mOriginalAmount.multiply( quantityBigDecimal ) : null );

    return ( new SingleCurrencyAmounts( mCurrency, mAmount.multiply( quantityBigDecimal ), multipliedOriginalAmount ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

