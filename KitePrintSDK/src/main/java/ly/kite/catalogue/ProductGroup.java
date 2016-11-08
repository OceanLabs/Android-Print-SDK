/*****************************************************
 *
 * ProductGroup.java
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

import android.content.Context;
import android.util.Log;
import android.view.Gravity;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a product group.
 *
 * Do not make this class parcelable, as some product groups
 * are too big and cause transaction errors.
 *
 *****************************************************/
public class ProductGroup implements IGroupOrProduct
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "ProductGroup";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String              mLabel;
  private int                 mLabelColour;
  private URL                 mImageURL;

  private ArrayList<Product>  mProductList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  ProductGroup( String label, int labelColour, URL imageURL )
    {
    mLabel       = label;
    mLabelColour = labelColour;
    mImageURL    = imageURL;

    mProductList = new ArrayList<>();
    }


  ////////// IGroupOrProduct Method(s) //////////

  /*****************************************************
   *
   * Returns the display image URL.
   *
   *****************************************************/
  @Override
  public URL getDisplayImageURL()
    {
    return ( mImageURL );
    }


  /*****************************************************
   *
   * Returns the gravity for the display image.
   *
   *****************************************************/
  @Override
  public int getDisplayImageAnchorGravity( Context context )
    {
    return ( Gravity.NO_GRAVITY );
    }


  /*****************************************************
   *
   * Returns the display label.
   *
   *****************************************************/
  @Override
  public String getDisplayLabel()
    {
    return ( mLabel );
    }


  /*****************************************************
   *
   * Returns the display label colour.
   *
   *****************************************************/
  @Override
  public int getDisplayLabelColour()
    {
    return ( mLabelColour );
    }


  /*****************************************************
   *
   * Returns true if this group contains more than one
   * product, false otherwise.
   *
   *****************************************************/
  public boolean containsMultiplePrices()
    {
    return ( mProductList != null && mProductList.size() > 1 );
    }


  /*****************************************************
   *
   * Returns a display price. For product groups, this is
   * the lowest price of any of the products in the group.
   *
   * Thus it may be used as a "from" price.
   *
   *****************************************************/
  @Override
  public String getDisplayPrice( String preferredCurrency )
    {
    // We don't want to mess around with trying to compare prices in different
    // currencies, so we first need to find a currency that all prices are listed
    // in.

    Locale locale       = Locale.getDefault();
    String currencyCode = chooseBestCurrency( preferredCurrency );

    if ( currencyCode == null )
      {
      Log.e( LOG_TAG, "No currency is supported across all products" );

      return ( null );
      }


    SingleCurrencyAmounts lowestSingleCurrencyCost = null;

    for ( Product product : mProductList )
      {
      MultipleCurrencyAmounts candidateCost               = product.getCost();
      SingleCurrencyAmounts candidateSingleCurrencyCost = candidateCost.get( currencyCode );

      // See if this is the lowest cost
      if ( candidateSingleCurrencyCost != null &&
              ( lowestSingleCurrencyCost == null ||
                      candidateSingleCurrencyCost.getAmount().compareTo( lowestSingleCurrencyCost.getAmount() ) < 0 ) )
        {
        lowestSingleCurrencyCost = candidateSingleCurrencyCost;
        }
      }


    // If we found a low price - return it as a string formatted for the current locale
    if ( lowestSingleCurrencyCost != null ) return ( lowestSingleCurrencyCost.getDisplayAmountForLocale( locale ) );

    return ( null );
    }


  /*****************************************************
   *
   * Returns a description, or null if there is no
   * description.
   *
   *****************************************************/
  public String getDescription()
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns true or false according to whether a flag is
   * set.
   *
   *****************************************************/
  @Override
  public boolean flagIsSet( String tag )
    {
    return ( false );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Appends all the product images used by this product,
   * to the supplied list.
   *
   *****************************************************/
  public void appendAllImages( List<URL> targetURLList )
    {
    if ( mImageURL != null ) targetURLList.add( mImageURL );
    }


  /*****************************************************
   *
   * Returns the best currency code that is supported across
   * all products (i.e. all products have a cost in that
   * currency).
   *
   *****************************************************/
  public String chooseBestCurrency( String preferredCurrencyCode )
    {
    // Check if the preferred currency is supported

    if ( preferredCurrencyCode != null &&
         currencyIsSupportedByAllProducts( preferredCurrencyCode ) )
      {
      return ( preferredCurrencyCode );
      }


    // Try the fallback currencies

    for ( String fallbackCurrencyCode : MultipleCurrencyAmounts.FALLBACK_CURRENCY_CODES )
      {
      if ( currencyIsSupportedByAllProducts( fallbackCurrencyCode ) ) return ( fallbackCurrencyCode );
      }


    return ( null );
    }


  /*****************************************************
   *
   * Returns true if every product in this group as a price
   * available in the supplied currency code.
   *
   *****************************************************/
  public boolean currencyIsSupportedByAllProducts( String currencyCode )
    {
    if ( currencyCode == null || currencyCode.trim().equals( "" ) ) return ( false );

    for ( Product product : mProductList )
      {
      MultipleCurrencyAmounts multipleCurrencyCost = product.getCost();

      if ( multipleCurrencyCost.get( currencyCode ) == null ) return ( false );
      }

    return ( true );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a product to this group.
   *
   *****************************************************/
  void add( Product product )
    {
    mProductList.add( product );
    }


  /*****************************************************
   *
   * Returns the product list.
   *
   *****************************************************/
  public ArrayList<Product> getProductList()
    {
    return ( mProductList );
    }


  /*****************************************************
   *
   * Returns a log-displayable string representing this
   * product group.
   *
   *****************************************************/
  public String toLogString()
    {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append( "Label        : " ).append( mLabel ).append( "\n" );
    stringBuilder.append( "Label Colour : 0x" ).append( Integer.toHexString( mLabelColour ) ).append( "\n" );
    stringBuilder.append( "Image URL    : " ).append( mImageURL.toString() ).append( "\n" );

    return ( stringBuilder.toString() );
    }



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

