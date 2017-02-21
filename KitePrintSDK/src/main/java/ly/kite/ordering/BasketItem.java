/*****************************************************
 *
 * BasketItem.java
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

package ly.kite.ordering;


///// Import(s) /////


///// Class Declaration /////

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.catalogue.Product;
import ly.kite.ordering.ImageSpec;
import ly.kite.util.Asset;
import ly.kite.util.AssetFragment;

/*****************************************************
 *
 * This class holds a basket item.
 *
 *****************************************************/
public class BasketItem
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "BasketItem";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private long                    mId;
  private Product                 mProduct;
  private int                     mOrderQuantity;
  private HashMap<String,String>  mOptionsMap;
  private ArrayList<ImageSpec>    mImageSpecList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  public BasketItem( long id, Product product, int orderQuantity, HashMap<String,String> optionsMap, ArrayList<ImageSpec> imageSpecList )
    {
    mId            = id;
    mProduct       = product;
    mOrderQuantity = orderQuantity;
    mOptionsMap    = optionsMap;
    mImageSpecList = imageSpecList;
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the item id.
   *
   *****************************************************/
  public long getId()
    {
    return ( mId );
    }


  /*****************************************************
   *
   * Returns the product.
   *
   *****************************************************/
  public Product getProduct()
    {
    return ( mProduct );
    }


  /*****************************************************
   *
   * Sets the order quantity.
   *
   *****************************************************/
  public void setOrderQuantity( int orderQuantity )
    {
    mOrderQuantity = orderQuantity;
    }


  /*****************************************************
   *
   * Returns the order quantity.
   *
   *****************************************************/
  public int getOrderQuantity()
    {
    return ( mOrderQuantity );
    }


  /*****************************************************
   *
   * Returns the options map.
   *
   *****************************************************/
  public HashMap<String,String> getOptionsMap()
    {
    return ( mOptionsMap );
    }


  /*****************************************************
   *
   * Returns the image spec list.
   *
   *****************************************************/
  public ArrayList<ImageSpec> getImageSpecList()
    {
    return ( mImageSpecList );
    }


  /*****************************************************
   *
   * Returns the image to be displayed as the basket item.
   * Tries to find an image spec; if there is none, returns
   * the product image.
   *
   *****************************************************/
  public AssetFragment getDisplayAssetFragment()
    {
    if ( mImageSpecList != null )
      {
      for ( ImageSpec imageSpec : mImageSpecList )
        {
        if ( imageSpec != null )
          {
          AssetFragment assetFragment = imageSpec.getAssetFragment();

          if ( assetFragment != null ) return ( assetFragment );
          }
        }
      }

    return ( null );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

