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
  private HashMap<String,ProductGroup>  mProductIdGroupTable;

  private HashMap<String,Product>       mIdDiscardedProductTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  Catalogue()
    {
    mNameGroupTable          = new HashMap<>();
    mGroupList               = new ArrayList<>();
    mIdProductTable          = new HashMap<>();
    mProductIdGroupTable     = new HashMap<>();

    mIdDiscardedProductTable = new HashMap<>();
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


    productGroup.add( product );

    mIdProductTable.put( product.getId(), product );
    mProductIdGroupTable.put( product.getId(), productGroup );
    }


  /*****************************************************
   *
   * Adds a discarded product to the catalogue. This is
   * used for references purposes to allow products to be
   * ordered even if we don't have journeys for them, or
   * they have been filtered out.
   *
   *****************************************************/
  void addDiscardedProduct( Product product )
    {
    mIdDiscardedProductTable.put( product.getId(), product );
    }


  /*****************************************************
   *
   * Returns the number of products in this catalogue.
   *
   *****************************************************/
  public int getProductCount()
    {
    return ( mIdProductTable.size() );
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
   * Returns the product that has the supplied id.
   *
   *****************************************************/
  public Product getProductById( String productId )
    {
    // Check in both tables for the product

    Product product;

    if ( ( product = mIdProductTable.get( productId ) ) != null )
      {
      return ( product );
      }

    return ( mIdDiscardedProductTable.get( productId ) );
    }


  /*****************************************************
   *
   * Returns the product group for the product that has
   * the supplied id.
   *
   *****************************************************/
  public ProductGroup getGroupByProductId( String productId )
    {
    return ( mProductIdGroupTable.get( productId ) );
    }


  /*****************************************************
   *
   * Throws an exception if there is no product matching the
   * supplied id.
   *
   *****************************************************/
  public void confirmProductIdExistsOrThrow( String productId )
    {
    if ( getProductById( productId ) == null )
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


  /*****************************************************
   *
   * Creates a copy of this catalogue that is filtered
   * by a set of product ids.
   *
   * If no product ids are supplied, the returned (filtered)
   * catalogue will contain no products.
   *
   *****************************************************/
  public Catalogue createFiltered( String[] productIds )
    {
    // Create a catalogue with the same custom data
    Catalogue filteredCatalogue = new Catalogue( mUserConfigJSONObject );


    // For each product corresponding to an id in the filter list - add it to
    // the filtered catalogue.

    // Note that currently we do not retain the remaining products as discarded
    // products. It seems unlikely that we will want to filter products out and
    // then order them anyway. We also don't look for filtered products in the
    // original discarded list, because we can't display a UI for them anyway.

    if ( productIds != null )
      {
      for ( String productId : productIds )
        {
        Product      product      = getProductById( productId );
        ProductGroup productGroup = getGroupByProductId( productId );

        if ( product != null && productGroup != null )
          {
          filteredCatalogue.addProduct( productGroup.getDisplayLabel(), productGroup.getDisplayImageURL(), product );
          }
        }
      }


    return ( filteredCatalogue );
    }


  /*****************************************************
   *
   * Displays information in the log for image pre-caching
   * purposes.
   *
   *****************************************************/
  public void displayPreCachingInfo()
    {
    ArrayList<String> curlList    = new ArrayList<>();
    ArrayList<String> mappingList = new ArrayList<>();


    // Go through every group

    int groupIndex = 0;

    for ( ProductGroup group : mGroupList )
      {
      String urlString    = group.getDisplayImageURL().toString();
      String resourceName = "precached_group_" + String.valueOf( groupIndex );
      String resourceFile = resourceName + ".jpg";

      curlList.add( "curl " + urlString + " > " + resourceFile );
      mappingList.add( ".addResourceMapping( \"" + urlString + "\", R.drawable." + resourceName + " )" );

      groupIndex ++;
      }


    // Go through every product

    int productIndex = 0;

    for ( Product product : mIdProductTable.values() )
      {
      String urlString    = product.getDisplayImageURL().toString();
      String resourceName = "precached_product_" + String.valueOf( productIndex );
      String resourceFile = resourceName + ".jpg";

      curlList.add( "curl " + urlString + " > " + resourceFile );
      mappingList.add( ".addResourceMapping( \"" + urlString + "\", R.drawable." + resourceName + " )" );

      productIndex ++;
      }


    // Print out all the curl commands
    for ( String curlCommand : curlList )
      {
      Log.i( LOG_TAG, curlCommand );
      }

    // Print out all the resource mappings
    for ( String resourceMapping : mappingList )
      {
      Log.i( LOG_TAG, resourceMapping );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

