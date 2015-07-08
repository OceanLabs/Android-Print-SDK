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
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ly.kite.address.Country;
import ly.kite.shopping.IGroupOrProduct;
import ly.kite.shopping.MultipleCurrencyCost;
import ly.kite.shopping.MultipleDestinationShippingCosts;
import ly.kite.shopping.SingleCurrencyCost;
import ly.kite.shopping.SingleDestinationShippingCost;
import ly.kite.shopping.UserJourneyType;


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
  private static final String       LOG_TAG                        = "Product";

  private static final UnitOfLength FALLBACK_UNIT_1                = UnitOfLength.CENTIMETERS;
  private static final UnitOfLength FALLBACK_UNIT_2                = UnitOfLength.INCHES;

  private static final String       DESTINATION_CODE_EUROPE        = "europe";
  private static final String       DESTINATION_CODE_REST_OF_WORLD = "rest_of_world";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                  mId;
  private String                  mCode;
  private String                  mName;
  private String                  mLabel;
  private UserJourneyType         mUserJourneyType;
  private int                     mQuantityPerSheet;

  private MultipleCurrencyCost    mCost;
  private MultipleDestinationShippingCosts mShippingCosts;
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

  Product( String productId, String productCode, String productName, String productLabel, int labelColour, UserJourneyType userJourneyType, int quantityPerSheet )
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
  Product setShippingCosts( MultipleDestinationShippingCosts shippingCosts )
    {
    mShippingCosts = shippingCosts;

    return ( this );
    }


  /*****************************************************
   *
   * Returns the shipping costs.
   *
   *****************************************************/
  public MultipleDestinationShippingCosts getShippingCosts()
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
   * Returns the image URLs.
   *
   *****************************************************/
  public ArrayList<URL> getImageURLList()
    {
    return ( mImageURLList );
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
   * Returns the mask URL.
   *
   *****************************************************/
  public URL getMaskURL()
    {
    return ( mMaskURL );
    }


  /*****************************************************
   *
   * Returns the mask bleed.
   *
   *****************************************************/
  public Bleed getMaskBleed()
    {
    return ( mMaskBleed );
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

    // Lastly try and getCost the first supported currency
    if ( ( size = mSize.get( 0               ) ) != null ) return ( size );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the cost in a specific currency, falling back
   * if the cost is not known in the requested currency.
   *
   *****************************************************/
  public SingleCurrencyCost getCostWithFallback( String preferredCurrencyCode )
    {
    return ( mCost.getCostWithFallback( preferredCurrencyCode ) );
    }


  /*****************************************************
   *
   * Returns the cost in the currency for the supplied locale,
   *
   *****************************************************/
  public SingleCurrencyCost getCostWithFallback( Locale locale )
    {
    return ( mCost.getCostWithFallback( Currency.getInstance( locale ) ) );
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

    MultipleCurrencyCost shippingCost = mShippingCosts.getCost( countryCode );

    if ( shippingCost != null ) return ( shippingCost );


    // If the country is part of Europe, try and getCost a European cost.

    Country country = Country.getInstance( countryCode );

    if ( country.isInEurope() )
      {
      shippingCost = mShippingCosts.getCost( MultipleDestinationShippingCosts.DESTINATION_CODE_EUROPE );

      if ( shippingCost != null ) return ( shippingCost );
      }


    // If all else fails, try and getCost a rest of world cost.
    return ( mShippingCosts.getCost( MultipleDestinationShippingCosts.DESTINATION_CODE_REST_OF_WORLD ) );
    }


  /*****************************************************
   *
   * Returns the shipping costs, but sorted by relevance
   * to the supplied country code.
   *
   *****************************************************/
  public List<SingleDestinationShippingCost> getSortedShippingCosts( Country country )
    {
    // Get the shipping costs as a list
    List<SingleDestinationShippingCost> shippingCostList = mShippingCosts.asList();

    // Sort the list in order of relevance
    Collections.sort( shippingCostList, new ShippingCostRelevanceComparator( country ) );

    return ( shippingCostList );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A shipping cost comparator. This is used to sort a
   * list of shipping costs into relevance order.
   *
   *****************************************************/
  private class ShippingCostRelevanceComparator implements Comparator<SingleDestinationShippingCost>
    {
    private Country  mCountry;


    ShippingCostRelevanceComparator( Country country )
      {
      mCountry = country;
      }


    /*****************************************************
     *
     * Compares two destinations, and returns a value indicating
     * how they should be sorted in relevance.
     *
     * Note that we can be a bit lazy with this, since we shouldn't
     * have any duplicates destinations (i.e. we should never have to return 0).
     *
     *****************************************************/
    @Override
    public int compare( SingleDestinationShippingCost leftShippingCost, SingleDestinationShippingCost rightShippingCost )
      {
      String leftDestinationCode = leftShippingCost.getDestinationCode();

      if ( mCountry != null )
        {

        }


      // Default order if we don't know what country we're in:
      // UK
      // USA
      // Any country code
      // Europe
      // Rest of world

      if ( Country.UK.usesISOCode( leftDestinationCode )                   ) return ( -1 );
      if ( Country.USA.usesISOCode( leftDestinationCode )                  ) return ( -1 );
      if ( Country.existsForISOCode( leftDestinationCode )                 ) return ( -1 );
      if ( DESTINATION_CODE_EUROPE.equals( leftDestinationCode )        ) return ( -1 );
      if ( DESTINATION_CODE_REST_OF_WORLD.equals( leftDestinationCode ) ) return ( -1 );

      return ( 1 );
      }
    }

  }
