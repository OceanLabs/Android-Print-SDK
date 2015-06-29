/*****************************************************
 *
 * Product.java
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

package ly.kite.print;


///// Import(s) /////

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import ly.kite.address.Country;
import ly.kite.shopping.IGroupOrProduct;
import ly.kite.shopping.MultipleCurrencyCost;
import ly.kite.shopping.ShippingCosts;
import ly.kite.shopping.SingleCurrencyCost;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a print product.
 *
 *****************************************************/
public class Product implements IGroupOrProduct
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String       LOG_TAG                  = "Product";

  private static final String       FALLBACK_CURRENCY_CODE_1 = "USD";
  private static final String       FALLBACK_CURRENCY_CODE_2 = "GBP";
  private static final String       FALLBACK_CURRENCY_CODE_3 = "EUR";

  private static final UnitOfLength FALLBACK_UNIT_1          = UnitOfLength.CENTIMETERS;
  private static final UnitOfLength FALLBACK_UNIT_2          = UnitOfLength.INCHES;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                  mId;
  private String                  mCode;
  private String                  mName;
  private String                  mLabel;
  private String                  mUserJourneyType;
  private int                     mQuantityPerSheet;

  private MultipleCurrencyCost    mCost;
  private ShippingCosts           mShippingCosts;
  //private URL                     mGroupImageURL;
  private URL                     mHeroImageURL;
  private int                     mLabelColour;
  //private String                  mGroupLabel;
  private ArrayList<URL>          mImageURLList;
  //private String                  mProductSubclass;
  private URL                     mMaskURL;
  private Bleed                   mMaskBleed;
  private MultipleUnitSize        mSize;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  Product( String productId, String productCode, String productName, String productLabel, int labelColour, String userJourneyType, int quantityPerSheet )
    {
    mId               = productId;
    mCode             = productCode;
    mName             = productName;
    mLabel            = productLabel;
    mLabelColour      = labelColour;
    mUserJourneyType  = userJourneyType;
    mQuantityPerSheet = quantityPerSheet;
    }


  ////////// DisplayItem Method(s) //////////

  /*****************************************************
   *
   * Returns the display image URL.
   *
   *****************************************************/
  @Override
  public URL getDisplayImageURL()
    {
    return ( mHeroImageURL );
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
   * Returns the quantity per sheet.
   *
   *****************************************************/
  public int getQuantityPerSheet()
    {
    return ( mQuantityPerSheet );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the id.
   *
   *****************************************************/
  public String getId()
    {
    return ( mId );
    }


  /*****************************************************
   *
   * Returns the name.
   *
   *****************************************************/
  public String getName()
    {
    return ( mName );
    }


  /*****************************************************
   *
   * Sets the cost.
   *
   *****************************************************/
  Product setCost( MultipleCurrencyCost cost )
    {
    mCost = cost;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the shipping costs.
   *
   *****************************************************/
  Product setShippingCosts( ShippingCosts shippingCosts )
    {
    mShippingCosts = shippingCosts;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the shipping costs.
   *
   *****************************************************/
  public ShippingCosts getShippingCosts()
    {
    return ( mShippingCosts );
    }


  /*****************************************************
   *
   * Sets the image URLs.
   *
   *****************************************************/
  Product setImageURLs( URL heroImageURL, ArrayList<URL> imageURLList )
    {
    mHeroImageURL = heroImageURL;
    mImageURLList = imageURLList;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the label colour.
   *
   *****************************************************/
  Product setLabelColour( int labelColour )
    {
    mLabelColour = labelColour;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the mask.
   *
   *****************************************************/
  Product setMask( URL url, Bleed bleed )
    {
    mMaskURL   = url;
    mMaskBleed = bleed;

    return ( this );
    }


  /*****************************************************
   *
   * Sets the size.
   *
   *****************************************************/
  Product setSize( MultipleUnitSize size )
    {
    mSize = size;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the size, falling back if the size is not
   * known in the requested units.
   *
   *****************************************************/
  public SingleUnitSize getSizeWithFallback( UnitOfLength unit )
    {
    SingleUnitSize size;

    // First try the requested unit
    if ( ( size = mSize.get( unit            ) ) != null ) return ( size );

    // Next try falling back through major currencies
    if ( ( size = mSize.get( FALLBACK_UNIT_1 ) ) != null ) return ( size );
    if ( ( size = mSize.get( FALLBACK_UNIT_2 ) ) != null ) return ( size );

    // Lastly try and get the first supported currency
    if ( ( size = mSize.get( 0               ) ) != null ) return ( size );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency, falling back
   * if the cost is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyCost getCostWithFallback( String currencyCode )
    {
    SingleCurrencyCost cost;

    // First try the requested currency code
    if ( ( cost = mCost.get( currencyCode             ) ) != null ) return ( cost );

    // Next try falling back through major currencies
    if ( ( cost = mCost.get( FALLBACK_CURRENCY_CODE_1 ) ) != null ) return ( cost );
    if ( ( cost = mCost.get( FALLBACK_CURRENCY_CODE_2 ) ) != null ) return ( cost );
    if ( ( cost = mCost.get( FALLBACK_CURRENCY_CODE_3 ) ) != null ) return ( cost );

    // Lastly try and get the first supported currency
    if ( ( cost = mCost.get( 0                        ) ) != null ) return ( cost );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency.
   *
   *****************************************************/
  public BigDecimal getCost( String currencyCode )
    {
    SingleCurrencyCost cost = mCost.get( currencyCode );

    if ( cost == null ) throw ( new IllegalArgumentException( "No cost found for currency " + currencyCode ) );

    return ( cost.getAmount() );
    }


  /*****************************************************
   *
   * Returns a set of supported currency codes.
   *
   *****************************************************/
  public Set<String> getCurrenciesSupported()
    {
    return ( mCost.getAllCurrencyCodes() );
    }


  /*****************************************************
   *
   * Returns the shipping cost to a destination country.
   *
   *****************************************************/
  public MultipleCurrencyCost getShippingCostTo( String countryCode )
    {
    // First see if we can find the country as a destination

    MultipleCurrencyCost shippingCost = mShippingCosts.get( countryCode );

    if ( shippingCost != null ) return ( shippingCost );


    // If the country is part of Europe, try and get a European cost.

    Country country = Country.getInstance( countryCode );

    if ( country.isInEurope() )
      {
      shippingCost = mShippingCosts.get( ShippingCosts.DESTINATION_CODE_EUROPE );

      if ( shippingCost != null ) return ( shippingCost );
      }


    // If all else fails, try and get a rest of world cost.
    return ( mShippingCosts.get( ShippingCosts.DESTINATION_CODE_REST_OF_WORLD ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
