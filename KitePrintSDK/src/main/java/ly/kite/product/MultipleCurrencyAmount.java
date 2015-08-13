/*****************************************************
 *
 * MultipleCurrencyAmount.java
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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import ly.kite.address.Country;

/*****************************************************
 *
 * This class represents an amount in multiple currencies.
 *
 *****************************************************/
public class MultipleCurrencyAmount implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                     = "MultipleCurrencyAmount";

  private static final String  FALLBACK_CURRENCY_CODE_1    = "USD";
  private static final String  FALLBACK_CURRENCY_CODE_2    = "GBP";
  private static final String  FALLBACK_CURRENCY_CODE_3    = "EUR";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<MultipleCurrencyAmount> CREATOR =
    new Parcelable.Creator<MultipleCurrencyAmount>()
      {
      public MultipleCurrencyAmount createFromParcel( Parcel sourceParcel )
        {
        return ( new MultipleCurrencyAmount( sourceParcel ) );
        }

      public MultipleCurrencyAmount[] newArray( int size )
        {
        return ( new MultipleCurrencyAmount[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private HashMap<Currency,SingleCurrencyAmount> mCurrencyAmountTable;
  private HashMap<String,SingleCurrencyAmount>   mCurrencyCodeAmountTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MultipleCurrencyAmount()
    {
    mCurrencyAmountTable     = new HashMap<Currency,SingleCurrencyAmount>();
    mCurrencyCodeAmountTable = new HashMap<String,SingleCurrencyAmount>();
    }


  /*****************************************************
   *
   * Creates a multiple currency amount from a JSON object.
   *
   *****************************************************/
  public MultipleCurrencyAmount( JSONObject jsonObject )
    {
    this();


    // The amounts aren't in an array, so we need to iterate through the keys, i.e. the currency codes.

    Iterator<String> currencyIterator = jsonObject.keys();

    while ( currencyIterator.hasNext() )
      {
      try
        {
        String currencyCode = currencyIterator.next();
        BigDecimal amount = new BigDecimal( jsonObject.getString( currencyCode ) );

        add( new SingleCurrencyAmount( Currency.getInstance( currencyCode ), amount ) );
        }
      catch ( JSONException je )
        {
        Log.e( LOG_TAG, "Could not get amounts from: " + jsonObject.toString() );
        }
      }
    }


  /*****************************************************
   *
   * Creates a multiple currency amount from a parcel.
   *
   *****************************************************/
  private MultipleCurrencyAmount( Parcel parcel )
    {
    this();


    // The amounts are preceded by a count

    int count = parcel.readInt();

    for ( int index = 0; index < count; index ++ )
      {
      add( (SingleCurrencyAmount)parcel.readParcelable( SingleCurrencyAmount.class.getClassLoader() ) );
      }
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
    // Write the count and all the entries to the parcel

    targetParcel.writeInt( mCurrencyAmountTable.size() );

    for ( SingleCurrencyAmount singleCurrencyCost : mCurrencyCodeAmountTable.values() )
      {
      targetParcel.writeParcelable( singleCurrencyCost, flags );
      }
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a cost in a single currency.
   *
   *****************************************************/
  public void add( SingleCurrencyAmount singleCurrencyAmount )
    {
    mCurrencyAmountTable.put( singleCurrencyAmount.getCurrency(), singleCurrencyAmount );
    mCurrencyCodeAmountTable.put( singleCurrencyAmount.getCurrency().getCurrencyCode(), singleCurrencyAmount );
    }


  /*****************************************************
   *
   * Returns a the cost for a currency code.
   *
   *****************************************************/
  public SingleCurrencyAmount get( String currencyCode )
    {
    return ( mCurrencyCodeAmountTable.get( currencyCode ) );
    }


  /*****************************************************
   *
   * Returns the cost at a position.
   *
   *****************************************************/
  public SingleCurrencyAmount get( int position )
    {
    return ( mCurrencyCodeAmountTable.get( position ) );
    }


  /*****************************************************
   *
   * Returns all the currency codes.
   *
   *****************************************************/
  public Set<String> getAllCurrencyCodes()
    {
    return ( mCurrencyCodeAmountTable.keySet() );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmount getAmountWithFallback( String preferredCurrencyCode )
    {
    SingleCurrencyAmount amount;

    // First try the requested currency code
    if ( ( amount = get( preferredCurrencyCode ) ) != null ) return ( amount );

    // Next try falling back through major currencies
    if ( ( amount = get( FALLBACK_CURRENCY_CODE_1 ) ) != null ) return ( amount );
    if ( ( amount = get( FALLBACK_CURRENCY_CODE_2 ) ) != null ) return ( amount );
    if ( ( amount = get( FALLBACK_CURRENCY_CODE_3 ) ) != null ) return ( amount );

    // Lastly try and getCost the first supported currency
    if ( ( amount = get( 0 ) ) != null ) return ( amount );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmount getAmountWithFallback( Currency preferredCurrency )
    {
    return ( getAmountWithFallback( preferredCurrency.getCurrencyCode() ) );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmount getAmountWithFallback( Locale locale )
    {
    return ( getAmountWithFallback( Currency.getInstance( locale ) ) );
    }


  /*****************************************************
   *
   * Returns the cost in the default currency (for the default
   * locale), falling back if the cost is not known in the
   * requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmount getDefaultAmountWithFallback()
    {
    Locale   defaultLocale   = Locale.getDefault();
    Currency defaultCurrency = Currency.getInstance( defaultLocale );

    return ( getAmountWithFallback( defaultCurrency.getCurrencyCode() ) );
    }



  /*****************************************************
   *
   * Returns the amount as a formatted string. Tries to use
   * the default currency, but will fall back to other
   * currencies if the preferred is not available.
   *
   * If the currency that we found matches the main currency
   * for the default locale, then we use the number formatter
   * to format the amount.
   *
   * If the currency that we found is different, then we format
   * the amount with the full currency code. We do this to
   * avoid any ambiguity. For example, if we were to live in
   * Sweden but found a cost in Danish Krone, then having an
   * amount such as 4.00 kr would be ambiguous (because we
   * would believe we were being quoted in Swedish Kroner).
   *
   *****************************************************/
  public String getDefaultDisplayAmountWithFallback()
    {
    Locale   defaultLocale   = Locale.getDefault();
    Currency defaultCurrency = Currency.getInstance( defaultLocale );


    // Get the single currency amount

    SingleCurrencyAmount amount = getAmountWithFallback( defaultCurrency.getCurrencyCode() );

    if ( amount == null ) return ( null );


    // Format the amount we found for the default locale. It may not be the same currency
    // we asked for.
    return ( amount.getDisplayAmountForLocale( defaultLocale ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

