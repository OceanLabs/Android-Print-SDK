/*****************************************************
 *
 * MultipleCurrencyAmounts.java
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


///// Class Declaration /////

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/*****************************************************
 *
 * This class represents an amount in multiple currencies.
 *
 *****************************************************/
public class MultipleCurrencyAmounts implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                     = "MultipleCurrencyAmounts";

  static private final String  JSON_NAME_AMOUNT            = "amount";
  static private final String  JSON_NAME_ORIGINAL_AMOUNT   = "original_amount";

  static private final String  FALLBACK_CURRENCY_CODE_1    = "USD";
  static private final String  FALLBACK_CURRENCY_CODE_2    = "GBP";
  static private final String  FALLBACK_CURRENCY_CODE_3    = "EUR";

  static public  final String[] FALLBACK_CURRENCY_CODES =
    {
    FALLBACK_CURRENCY_CODE_1,
    FALLBACK_CURRENCY_CODE_2,
    FALLBACK_CURRENCY_CODE_3
    };


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<MultipleCurrencyAmounts> CREATOR =
    new Parcelable.Creator<MultipleCurrencyAmounts>()
      {
      public MultipleCurrencyAmounts createFromParcel( Parcel sourceParcel )
        {
        return ( new MultipleCurrencyAmounts( sourceParcel ) );
        }

      public MultipleCurrencyAmounts[] newArray( int size )
        {
        return ( new MultipleCurrencyAmounts[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private HashMap<Currency,SingleCurrencyAmounts> mCurrencyAmountTable;
  private HashMap<String,SingleCurrencyAmounts>   mCurrencyCodeAmountTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new multiple currency amount containing just
   * the supplied currency, or the supplied source amount
   * if the currency is null.
   *
   *****************************************************/
  static public MultipleCurrencyAmounts newFilteredInstance( MultipleCurrencyAmounts originalMultipleCurrencyAmount, String currencyCode )
    {
    if ( originalMultipleCurrencyAmount == null ) return ( null );

    if ( currencyCode == null ) return ( originalMultipleCurrencyAmount );

    MultipleCurrencyAmounts newMultipleCurrencyAmount = new MultipleCurrencyAmounts();

    SingleCurrencyAmounts singleCurrencyAmount = originalMultipleCurrencyAmount.get( currencyCode );

    if ( singleCurrencyAmount != null ) newMultipleCurrencyAmount.add( singleCurrencyAmount );

    return ( newMultipleCurrencyAmount );
    }


  /*****************************************************
   *
   * Returns a currency code from a currency.
   *
   * @return null, if no currency was supplied
   * @return The currency code, if a currency was supplied
   *
   *****************************************************/
  static private String getSafeCurrencyCode( Currency currency )
    {
    if ( currency != null ) return ( currency.getCurrencyCode() );

    return ( "" );
    }


  ////////// Constructor(s) //////////

  public MultipleCurrencyAmounts()
    {
    mCurrencyAmountTable     = new HashMap<Currency,SingleCurrencyAmounts>();
    mCurrencyCodeAmountTable = new HashMap<String,SingleCurrencyAmounts>();
    }


  /*****************************************************
   *
   * Creates a multiple currency amount from a JSON object.
   *
   *****************************************************/
  public MultipleCurrencyAmounts( JSONObject jsonObject )
    {
    this();


    // The amounts aren't in an array, so we need to iterate through the keys, i.e. the currency codes.

    Iterator<String> currencyIterator = jsonObject.keys();

    while ( currencyIterator.hasNext() )
      {
      try
        {
        String currencyCode = currencyIterator.next();


        // The amount might be a simple decimal value, or another JSON object in the form:
        //   {"amount":"0.00000"}

        BigDecimal amountBigDecimal;
        BigDecimal originalAmountBigDecimal = null;

        try
          {
          amountBigDecimal = new BigDecimal( jsonObject.getString( currencyCode ) );
          }
        catch ( NumberFormatException nfe )
          {
          JSONObject amountJSONObject = jsonObject.getJSONObject( currencyCode );

          amountBigDecimal = new BigDecimal( amountJSONObject.getString( JSON_NAME_AMOUNT ) );


          // Check for an original amount

          String originalAmountString = amountJSONObject.optString( JSON_NAME_ORIGINAL_AMOUNT, null );

          originalAmountBigDecimal = ( originalAmountString != null ? new BigDecimal( originalAmountString ) : null );
          }


        add( new SingleCurrencyAmounts( Currency.getInstance( currencyCode ), amountBigDecimal, originalAmountBigDecimal ) );
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
  private MultipleCurrencyAmounts( Parcel parcel )
    {
    this();


    // The amounts are preceded by a count

    int count = parcel.readInt();

    for ( int index = 0; index < count; index ++ )
      {
      add( (SingleCurrencyAmounts)parcel.readParcelable( SingleCurrencyAmounts.class.getClassLoader() ) );
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

    for ( SingleCurrencyAmounts singleCurrencyCost : mCurrencyCodeAmountTable.values() )
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
  public void add( SingleCurrencyAmounts singleCurrencyAmount )
    {
    mCurrencyAmountTable.put( singleCurrencyAmount.getCurrency(), singleCurrencyAmount );
    mCurrencyCodeAmountTable.put( singleCurrencyAmount.getCurrency().getCurrencyCode(), singleCurrencyAmount );
    }


  /*****************************************************
   *
   * Returns a the cost for a currency code.
   *
   *****************************************************/
  public SingleCurrencyAmounts get( String currencyCode )
    {
    return ( mCurrencyCodeAmountTable.get( currencyCode ) );
    }


  /*****************************************************
   *
   * Returns true if the currency code is available.
   *
   *****************************************************/
  public boolean contains( String currencyCode )
    {
    return ( mCurrencyCodeAmountTable.containsKey( currencyCode ) );
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
  public SingleCurrencyAmounts getAmountsWithFallback( String preferredCurrencyCode )
    {
    SingleCurrencyAmounts amount;

    // First try the requested currency code
    if ( ( amount = get( preferredCurrencyCode ) ) != null ) return ( amount );

    // Next try falling back through major currencies
    for ( String fallbackCurrencyCode : FALLBACK_CURRENCY_CODES )
      {
      if ( ( amount = get( fallbackCurrencyCode ) ) != null ) return ( amount );
      }


    // Lastly, try and get any supported currency

    Collection<SingleCurrencyAmounts> collection = mCurrencyAmountTable.values();

    if ( collection != null )
      {
      Iterator<SingleCurrencyAmounts> iterator = collection.iterator();

      if ( iterator.hasNext() ) return ( iterator.next() );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getAmountsWithFallback( Currency preferredCurrency )
    {
    return ( getAmountsWithFallback( getSafeCurrencyCode( preferredCurrency ) ) );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getAmountsWithFallbackMultipliedBy( int quantity, Currency preferredCurrency )
    {
    return ( getAmountsWithFallback( getSafeCurrencyCode( preferredCurrency ) ).multipliedBy( quantity ) );
    }


  /*****************************************************
   *
   * Returns the amount in a specific currency, falling back
   * if the amount is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getAmountsWithFallback( Locale locale )
    {
    // We keep getting dodgy locales that have the country set incorrectly (e.g. fa, en),
    // so if we can't get a currency from the locale, allow the fall-back procedure
    // to pick the currency.

    Currency currency = null;

    try
      {
      currency = Currency.getInstance( locale );
      }
    catch ( Exception ignore )
      {
      }

    return ( getAmountsWithFallback( currency ) );
    }


  /*****************************************************
   *
   * Returns the cost in the default currency (for the default
   * locale), falling back if the cost is not known in the
   * requested currency.
   *
   *****************************************************/
  public SingleCurrencyAmounts getDefaultAmountWithFallback()
    {
    Locale   defaultLocale   = Locale.getDefault();
    Currency defaultCurrency = Currency.getInstance( defaultLocale );

    return ( getAmountsWithFallback( defaultCurrency.getCurrencyCode() ) );
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
    return ( getDisplayAmountWithFallbackMultipliedBy( null, 1, Locale.getDefault() ) );
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
  public String getDefaultDisplayAmountWithFallback( String preferredCurrency )
    {
    return ( getDisplayAmountWithFallbackMultipliedBy( preferredCurrency, 1, Locale.getDefault() ) );
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
  public String getDefaultDisplayAmountWithFallbackMultipliedBy( int quantity )
    {
    return ( getDisplayAmountWithFallbackMultipliedBy( null, quantity, Locale.getDefault() ) );
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
  public String getDisplayAmountWithFallback( Locale locale )
    {
    return ( getDisplayAmountWithFallbackMultipliedBy( null, 1, locale ) );
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
  public String getDisplayAmountWithFallbackMultipliedBy( String preferredCurrencyCode, int quantity, Locale locale )
    {
    Currency currency;

    if ( preferredCurrencyCode != null )
      {
      currency = Currency.getInstance( preferredCurrencyCode );
      }
    else
      {
        try {
          currency = Currency.getInstance(locale);
        } catch (IllegalArgumentException e) {
          //fallback to US$
          currency = Currency.getInstance(Locale.US);
        }
      }

    // Get the single currency amounts

    SingleCurrencyAmounts singleAmounts = getAmountsWithFallback( currency.getCurrencyCode() );

    if ( singleAmounts == null ) return ( null );


    SingleCurrencyAmounts multipliedAmount = singleAmounts.multipliedBy( quantity );

    // Format the amount we found for the default locale. It may not be the same currency
    // we asked for.
    return ( multipliedAmount.getDisplayAmountForLocale( locale ) );
    }


  /*****************************************************
   *
   * Returns the original amount as a formatted string. Tries to use
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
  public String getDisplayOriginalAmountWithFallbackMultipliedBy( String preferredCurrencyCode, int quantity, Locale locale )
    {
    Currency currency;

    if ( preferredCurrencyCode != null )
      {
      currency = Currency.getInstance( preferredCurrencyCode );
      }
    else
      {
        try {
          currency = Currency.getInstance(locale);
        } catch (IllegalArgumentException e) {
          //fallback to US$
          currency = Currency.getInstance(Locale.US);
        }
      }


    // Get the single currency amounts

    SingleCurrencyAmounts multipliedAmount = getAmountsWithFallback( currency.getCurrencyCode() ).multipliedBy( quantity );

    if ( multipliedAmount == null ) return ( null );


    // Format the amount we found for the default locale. It may not be the same currency
    // we asked for.
    return ( multipliedAmount.getDisplayOriginalAmountForLocale( locale ) );
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
  public String getDisplayAmountWithFallbackMultipliedBy( String preferredCurrencyCode, int quantity )
    {
    return ( getDisplayAmountWithFallbackMultipliedBy( preferredCurrencyCode, quantity, Locale.getDefault() ) );
    }


  /*****************************************************
   *
   * Returns the original amount as a formatted string. Tries to use
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
  public String getDisplayOriginalAmountWithFallbackMultipliedBy( String preferredCurrencyCode, int quantity )
    {
    return ( getDisplayOriginalAmountWithFallbackMultipliedBy( preferredCurrencyCode, quantity, Locale.getDefault() ) );
    }


  /*****************************************************
   *
   * Returns the amounts as a list.
   *
   *****************************************************/
  public Collection<SingleCurrencyAmounts> asCollection()
    {
    return ( mCurrencyAmountTable.values() );
    }


  /*****************************************************
   *
   * Returns the amounts as a string.
   *
   *****************************************************/
  @Override
  public String toString()
    {
    StringBuilder stringBuilder = new StringBuilder();

    for ( SingleCurrencyAmounts singleCurrencyAmount : mCurrencyAmountTable.values() )
      {
      stringBuilder
              .append( "  " )
              .append( singleCurrencyAmount.getCurrencyCode() )
              .append( " " )
              .append( singleCurrencyAmount.getAmountAsDouble() );
      }

    return ( stringBuilder.toString() );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

