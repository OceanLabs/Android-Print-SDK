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

import android.util.Log;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


///// Class Declaration /////

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
  static private final String    LOG_TAG                          = "Catalogue";

  // Dummy catalogue - used for testing
  static public  final Catalogue DUMMY_CATALOGUE                  = new Catalogue().addProduct( "Dummy Group", null, Product.DUMMY_PRODUCT );

  static private final boolean   DISPLAY_PRODUCT_GROUPS           = false;

  static final String            JSON_NAME_THEME_COLOUR_PRIMARY   = "theme_colour_primary";
  static final String            JSON_NAME_THEME_COLOUR_SECONDARY = "theme_colour_secondary";

  static public  final int       NO_COLOUR                        = 0x000000;


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private JSONObject                    mUserConfigJSONObject;
  private HashMap<String,JSONObject>    mCustomDataTable;
  private ArrayList<String>             mPayPalSupportedCurrencyCodes;

  private HashMap<String,ProductGroup>  mLabelGroupTable;
  private ArrayList<ProductGroup>       mGroupList;
  private HashMap<String,Product>       mIdProductTable;
  private HashMap<String,ProductGroup>  mProductIdGroupTable;

  private HashMap<String,Product>       mIdDiscardedProductTable;

  private List<URL>                     mAllImagesURLList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Converts a colour string to a colour.
   *
   *****************************************************/
  static int colourFromString( String colourString )
    {
    if ( colourString != null )
      {
      int length = colourString.length();

      try
        {
        if ( length == 7 && colourString.charAt( 0 ) == '#' )
          {
          return ( 0xff000000 | Integer.parseInt( colourString.substring( 1 ), 16 ) );
          }
        else if ( length == 9 && colourString.charAt( 0 ) == '#' )
          {
          // We need to parse the colour as a long and then convert to integer, because
          // the integer parse doesn't like values greater than 0x7fffffff.
          return ( (int)Long.parseLong( colourString.substring( 1 ), 16 ) );
          }
        }
      catch ( NumberFormatException nfe )
        {
        Log.e( LOG_TAG, "Invalid colour format: " + colourString );
        }
      }

    return ( NO_COLOUR );
    }


  ////////// Constructor(s) //////////

  public Catalogue()
    {
    mCustomDataTable              = new HashMap<>();
    mPayPalSupportedCurrencyCodes = new ArrayList<>();

    setUserConfigData( null );


    mLabelGroupTable         = new HashMap<>();
    mGroupList               = new ArrayList<>();
    mIdProductTable          = new HashMap<>();
    mProductIdGroupTable     = new HashMap<>();

    mIdDiscardedProductTable = new HashMap<>();

    mAllImagesURLList        = new ArrayList<>();
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Sets the user config data.
   *
   *****************************************************/
  void setUserConfigData( JSONObject userConfigJSONObject )
    {
    // Make sure we always have a JSON object, even if we weren't supplied one.
    mUserConfigJSONObject = ( userConfigJSONObject != null ? userConfigJSONObject
                                                           : new JSONObject() );
    }


  /*****************************************************
   *
   * Returns a user config string.
   *
   *****************************************************/
  public String getUserConfigString( String name )
    {
    return ( mUserConfigJSONObject.optString( name ) );
    }


  /*****************************************************
   *
   * Returns a user config object.
   *
   *****************************************************/
  public JSONObject getUserConfigObject( String name )
    {
    return ( mUserConfigJSONObject.optJSONObject( name ) );
    }


  /*****************************************************
   *
   * Sets the custom data table.
   *
   *****************************************************/
  private void setCustomData( HashMap<String,JSONObject> customDataTable )
    {
    mCustomDataTable = customDataTable;
    }


  /*****************************************************
   *
   * Sets a custom data object.
   *
   *****************************************************/
  void setCustomObject( String name, JSONObject customJSONObject )
    {
    mCustomDataTable.put( name, customJSONObject );
    }


  /*****************************************************
   *
   * Returns a custom data object.
   *
   *****************************************************/
  public JSONObject getCustomObject( String name )
    {
    return ( mCustomDataTable.get( name ) );
    }


  /*****************************************************
   *
   * Returns the primary theme colour.
   *
   *****************************************************/
  public int getPrimaryThemeColour()
    {
    String colourString = getUserConfigString( JSON_NAME_THEME_COLOUR_PRIMARY );

    if ( colourString != null )
      {
      return ( colourFromString( colourString ) );
      }

    return ( NO_COLOUR );
    }


  /*****************************************************
   *
   * Returns the secondary theme colour.
   *
   *****************************************************/
  public int getSecondaryThemeColour()
    {
    String colourString = getUserConfigString( JSON_NAME_THEME_COLOUR_SECONDARY );

    if ( colourString != null )
      {
      return ( colourFromString( colourString ) );
      }

    return ( NO_COLOUR );
    }


  /*****************************************************
   *
   * Adds a PayPal supported currency.
   *
   *****************************************************/
  public void addPayPalSupportedCurrency( String currencyCode )
    {
    if ( currencyCode != null ) mPayPalSupportedCurrencyCodes.add( currencyCode );
    }


  /*****************************************************
   *
   * Returns the list of PayPal supported currencies.
   *
   *****************************************************/
  public ArrayList<String> getPayPalSupportedCurrencyCodes()
    {
    return ( mPayPalSupportedCurrencyCodes );
    }


  /*****************************************************
   *
   * Adds a product to the catalogue.
   *
   *****************************************************/
  public Catalogue addProduct( String groupLabel, URL groupImageURL, Product product )
    {
    // See if we already have the product group. If not - create it now.

    ProductGroup productGroup = findGroupByLabel( groupLabel );

    if ( productGroup == null )
      {
      productGroup = new ProductGroup( groupLabel, product.getDisplayLabelColour(), groupImageURL );

      if ( DISPLAY_PRODUCT_GROUPS )
        {
        Log.i( LOG_TAG, "-- New Product Group --" );
        Log.i( LOG_TAG, productGroup.toLogString() );
        }

      mGroupList.add( productGroup );
      mLabelGroupTable.put( groupLabel, productGroup );

      productGroup.appendAllImages( mAllImagesURLList );
      }


    productGroup.add( product );

    mIdProductTable.put( product.getId(), product );
    mProductIdGroupTable.put( product.getId(), productGroup );

    product.appendAllImages( mAllImagesURLList );

    return ( this );
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

    product.appendAllImages( mAllImagesURLList );
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
   * Returns a list of products.
   *
   *****************************************************/
  public Collection<Product> getProducts()
    {
    return ( mIdProductTable.values() );
    }


  /*****************************************************
   *
   * Returns a product based on its index.
   *
   *****************************************************/
  public Product getProduct( int soughtProductIndex )
    {
    Collection<Product> productCollection = getProducts();

    if ( productCollection == null || soughtProductIndex >= productCollection.size() ) return ( null );

    int productIndex = 0;

    for ( Product product : productCollection )
      {
      if ( productIndex == soughtProductIndex ) return ( product );

      productIndex ++;
      }

    return ( null );
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
   * Returns the product group that has the supplied label.
   *
   *****************************************************/
  public ProductGroup findGroupByLabel( String label )
    {
    return ( mLabelGroupTable.get( label ) );
    }


  /*****************************************************
   *
   * Returns a list of products within the group specified
   * by the product group label.
   *
   *****************************************************/
  public ArrayList<Product> getProductsForGroup( String groupLabel )
    {
    ProductGroup group = findGroupByLabel( groupLabel );

    if ( group != null ) return ( group.getProductList() );

    return ( null );
    }


  /*****************************************************
   *
   * Returns the product that has the supplied id.
   *
   *****************************************************/
  public Product findProductById( String productId )
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
  public ProductGroup getGroupForProductId( String productId )
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
    if ( findProductById( productId ) == null )
      {
      throw ( new IllegalStateException( "Product id " + productId + " not found in catalogue" ) );
      }
    }


  /*****************************************************
   *
   * Returns a list of all product images.
   *
   *****************************************************/
  public List<URL> getAllProductImageURLs()
    {
    return ( mAllImagesURLList );
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
    // Create a catalogue with the same custom and user config data

    Catalogue filteredCatalogue = new Catalogue();

    filteredCatalogue.setCustomData( mCustomDataTable );
    filteredCatalogue.setUserConfigData( mUserConfigJSONObject );


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
        Product      product      = findProductById( productId );
        ProductGroup productGroup = getGroupForProductId( productId );

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

