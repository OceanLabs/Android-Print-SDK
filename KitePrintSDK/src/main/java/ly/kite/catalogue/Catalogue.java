/*****************************************************
 *
 * Catalogue.java
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

import android.util.Log;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/*****************************************************
 *
 * This class holds catalogue details such as product groups,
 * products, and additional information.
 *
 *****************************************************/
public class Catalogue
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "Catalogue";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private JSONObject                    mUserConfigJSONObject;

  private HashMap<String,ProductGroup>  mNameGroupTable;
  private ArrayList<ProductGroup>       mGroupList;
  private HashMap<String,Product>       mIdProductTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  Catalogue()
    {
    mNameGroupTable = new HashMap<>();
    mGroupList      = new ArrayList<>();
    mIdProductTable = new HashMap<>();
    }

  Catalogue( JSONObject userConfigJSONObject )
    {
    this();

    setCustomData( userConfigJSONObject );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the custom user data.
   *
   *****************************************************/
  void setCustomData( JSONObject userConfigJSONObject )
    {
    // Make sure we always have a JSON object, even if we weren't supplied one.
    mUserConfigJSONObject = ( userConfigJSONObject != null ? userConfigJSONObject
                                                           : new JSONObject() );
    }


  /*****************************************************
   *
   * Adds a product to the catalogue.
   *
   *****************************************************/
  void addProduct( String groupLabel, URL groupImageURL, Product product )
    {
    // See if we already have the product group. If not - create it now.

    ProductGroup productGroup = mNameGroupTable.get( groupLabel );

    if ( productGroup == null )
      {
      productGroup = new ProductGroup( groupLabel, product.getDisplayLabelColour(), groupImageURL );

      Log.i( LOG_TAG, "-- New Product Group --" );
      Log.i( LOG_TAG, productGroup.toLogString() );

      mGroupList.add( productGroup );
      mNameGroupTable.put( groupLabel, productGroup );
      }


    // Add the product to its group
    productGroup.add( product );

    // Add the product to the product id / product table
    mIdProductTable.put( product.getId(), product );
    }


  /*****************************************************
   *
   * Returns the product group list.
   *
   *****************************************************/
  public ArrayList<ProductGroup> getProductGroupList()
    {
    return ( mGroupList );
    }


  /*****************************************************
   *
   * Returns a list of products within the group specified
   * by the product group label.
   *
   *****************************************************/
  public ArrayList<Product> getProductsForGroup( String groupLabel )
    {
    ProductGroup group = mNameGroupTable.get( groupLabel );

    if ( group != null ) return ( group.getProductList() );

    return ( null );
    }


  /*****************************************************
   *
   * Throws an exception if there is no product matching the
   * supplied id.
   *
   *****************************************************/
  public void confirmProductIdExistsOrThrow( String productId )
    {
    if ( mIdProductTable.get( productId ) == null )
      {
      throw ( new IllegalStateException( "Product id " + productId + " not found in catalogue" ) );
      }
    }


  /*****************************************************
   *
   * Returns a custom data string.
   *
   *****************************************************/
  public String getCustomDataString( String name )
    {
    return ( mUserConfigJSONObject.optString( name ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

