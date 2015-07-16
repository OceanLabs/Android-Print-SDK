/*****************************************************
 *
 * MultipleCurrencyCost.java
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

import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import ly.kite.address.Country;

/*****************************************************
 *
 * This class represents a cost in multiple currencies.
 *
 *****************************************************/
public class MultipleCurrencyCost implements Parcelable
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                     = "MultipleCurrencyCost";

  private static final String  FALLBACK_CURRENCY_CODE_1    = "USD";
  private static final String  FALLBACK_CURRENCY_CODE_2    = "GBP";
  private static final String  FALLBACK_CURRENCY_CODE_3    = "EUR";


  ////////// Static Variable(s) //////////

  public static final Parcelable.Creator<MultipleCurrencyCost> CREATOR =
    new Parcelable.Creator<MultipleCurrencyCost>()
      {
      public MultipleCurrencyCost createFromParcel( Parcel sourceParcel )
        {
        return ( new MultipleCurrencyCost( sourceParcel ) );
        }

      public MultipleCurrencyCost[] newArray( int size )
        {
        return ( new MultipleCurrencyCost[ size ] );
        }
      };


  ////////// Member Variable(s) //////////

  private HashMap<Currency,SingleCurrencyCost> mCurrencyCostTable;
  private HashMap<String,SingleCurrencyCost>   mCurrencyCodeCostTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public MultipleCurrencyCost()
    {
    mCurrencyCostTable     = new HashMap<Currency,SingleCurrencyCost>();
    mCurrencyCodeCostTable = new HashMap<String,SingleCurrencyCost>();
    }


  // Constructor used by parcelable interface
  private MultipleCurrencyCost( Parcel parcel )
    {
    this();


    // The costs are preceded by a count

    int count = parcel.readInt();

    for ( int index = 0; index < count; index ++ )
      {
      add( (SingleCurrencyCost)parcel.readParcelable( SingleCurrencyCost.class.getClassLoader() ) );
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

    targetParcel.writeInt( mCurrencyCostTable.size() );

    for ( SingleCurrencyCost singleCurrencyCost : mCurrencyCodeCostTable.values() )
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
  public void add( SingleCurrencyCost singleCurrencyCost )
    {
    mCurrencyCostTable.put( singleCurrencyCost.getCurrency(), singleCurrencyCost );
    mCurrencyCodeCostTable.put( singleCurrencyCost.getCurrency().getCurrencyCode(), singleCurrencyCost );
    }


  /*****************************************************
   *
   * Returns a the cost for a currency code.
   *
   *****************************************************/
  public SingleCurrencyCost get( String currencyCode )
    {
    return ( mCurrencyCodeCostTable.get( currencyCode ) );
    }


  /*****************************************************
   *
   * Returns the cost at a position.
   *
   *****************************************************/
  public SingleCurrencyCost get( int position )
    {
    return ( mCurrencyCodeCostTable.get( position ) );
    }


  /*****************************************************
   *
   * Returns all the currency codes.
   *
   *****************************************************/
  public Set<String> getAllCurrencyCodes()
    {
    return ( mCurrencyCodeCostTable.keySet() );
    }


  /*****************************************************
   *
   * Returns the cost in the default currency (for the default
   * locale), falling back if the cost is not known in the
   * requested currency.
   *
   *****************************************************/
  public SingleCurrencyCost getDefaultCostWithFallback()
    {
    Country defaultCountry = Country.getInstance();

    return ( getCostWithFallback( defaultCountry.iso3CurrencyCode() ) );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency, falling back
   * if the cost is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyCost getCostWithFallback( String preferredCurrencyCode )
    {
    SingleCurrencyCost cost;

    // First try the requested currency code
    if ( ( cost = get( preferredCurrencyCode ) ) != null ) return ( cost );

    // Next try falling back through major currencies
    if ( ( cost = get( FALLBACK_CURRENCY_CODE_1 ) ) != null ) return ( cost );
    if ( ( cost = get( FALLBACK_CURRENCY_CODE_2 ) ) != null ) return ( cost );
    if ( ( cost = get( FALLBACK_CURRENCY_CODE_3 ) ) != null ) return ( cost );

    // Lastly try and getCost the first supported currency
    if ( ( cost = get( 0 ) ) != null ) return ( cost );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency, falling back
   * if the cost is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyCost getCostWithFallback( Currency preferredCurrency )
    {
    return ( getCostWithFallback( preferredCurrency.getCurrencyCode() ) );
    }


  /*****************************************************
   *
   * Returns the cost as a formatted string. Tries to use
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
  public String getDefaultDisplayCostWithFallback()
    {
    Locale   defaultLocale   = Locale.getDefault();
    Currency defaultCurrency = Currency.getInstance( defaultLocale );


    // Get the single currency cost

    SingleCurrencyCost cost = getCostWithFallback( defaultCurrency.getCurrencyCode() );

    if ( cost == null ) return ( null );


    // Format the cost we found for the default locale. It may not be the same currency
    // we asked for.
    return ( cost.getDisplayAmountForLocale( defaultLocale ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

