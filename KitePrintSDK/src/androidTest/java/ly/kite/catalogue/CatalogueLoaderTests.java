/*****************************************************
 *
 * CatalogueLoaderTests.java
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

package ly.kite.catalogue;


///// Import(s) /////

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ly.kite.KiteTestCase;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the single currency amount class.
 *
 *****************************************************/
public class CatalogueLoaderTests extends KiteTestCase
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "CatalogueLoaderTests";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Parse products test.
   *
   *****************************************************/

  public void testParseProducts1()
    {
    Catalogue catalogue = new Catalogue();

    JSONTokener tokener = new JSONTokener(
            "[" +
                    "{" +
                    "active:true," +
                    "product_active:true," +
                    "template_id:product1," +
                    "product_category:Prints," +
                    "name:\"Product 1\"," +
                    "description:\"This is product 1\"," +
                    "images_per_page:1," +  // Optional
                    "grid_count_x:1," +  // Optional
                    "grid_count_y:1," +  // Optional
                    "cost:[" +
                         "{currency:\"GBP\",amount:2.00,formatted:\"£2.00\"}," +
                         "{currency:\"EUR\",amount:2.50,formatted:\"€2.50\"}" +
                         "]," +
                    "print_in_store:false," +  // Optional
                    "shipping_costs:{" +
                                   "GBR:{GBP:1.50,EUR:1.75}," +
                                   "europe:{GBP:2.00,EUR:2.25}" +
                                   "}," +
                    "product:{" +
                            "ios_sdk_class_photo:\"https://d2ilj0z99kr04x.cloudfront.net/static/homepage/images/how_it_works2.bc40a551a141.png\"," +
                            "ios_sdk_label_color:[255,100,100]," +
                            "ios_sdk_product_class:\"Product group 1\"," +
                            "ios_sdk_product_shots:[\"https://d2ilj0z99kr04x.cloudfront.net/static/homepage/images/how_it_works2.bc40a551a141.png\"]," +
                            "ios_sdk_product_type:\"print\"," +
                            "ios_sdk_ui_class:RECTANGLE," +
                            "product_code:\"product1\"," +
                            "size:{" +
                                 "cm:{width:25.4,height:25.4}," +
                                 "inch:{width:10,height:10}" +
                                 "}," +
                            "image_aspect_ratio:1.0," +
                            "supports_text_on_border:false," +  // Optional
                            "cover_photo_variants:[ { variant_id:\"default\", url:\"https://d2ilj0z99kr04x.cloudfront.net/static/homepage/images/how_it_works2.bc40a551a141.png\" } ]," +
                            "mask_url: \"https://d2ilj0z99kr04x.cloudfront.net/static/homepage/images/how_it_works2.bc40a551a141.png\"," +
                            "mask_bleed: [ 1, 1, 1, 1 ]," +
                            "image_border: [ 0.1, 0.1, 0.1, 0.1 ]" +
                            "}" +
                    "}" +
            "]" );

    try
      {
      JSONArray productsJSONArray = new JSONArray( tokener );

      CatalogueLoader.parseProducts( productsJSONArray, catalogue );
      }
    catch ( JSONException je )
      {
      Assert.fail( je.getMessage() );
      }

    Assert.assertEquals( 1, catalogue.getProductCount() );
    }



  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

