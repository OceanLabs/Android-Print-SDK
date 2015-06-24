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

import java.net.URL;
import java.util.ArrayList;

import ly.kite.shopping.GroupOrProduct;
import ly.kite.shopping.MultipleCurrencyCost;
import ly.kite.shopping.ShippingCosts;


///// Class Declaration /////

/*****************************************************
 *
 * This class represents a print product.
 *
 *****************************************************/
public class Product implements GroupOrProduct
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "Product";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String                  mId;
  private String                  mCode;
  private String                  mName;
  private String                  mLabel;
  private String                  mUserJourneyType;

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
  private ArrayList<ProductSize>  mSizeList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  Product( String productId, String productCode, String productName, String productLabel, int labelColour, String userJourneyType )
    {
    mId              = productId;
    mCode            = productCode;
    mName            = productName;
    mLabel           = productLabel;
    mLabelColour     = labelColour;
    mUserJourneyType = userJourneyType;
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


  ////////// Method(s) //////////

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
  Product setSize( ArrayList<ProductSize> sizeList )
    {
    mSizeList = sizeList;

    return ( this );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }
