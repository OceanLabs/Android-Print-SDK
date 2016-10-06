/*****************************************************
 *
 * ADeepLinkableActivity.java
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

package ly.kite.app;


///// Import(s) /////

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import ly.kite.journey.AKiteActivity;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent activity for apps that
 * implement deep linking into the SDK journey.
 *
 *****************************************************/
abstract public class ADeepLinkableActivity extends APermissionsRequestingActivity
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG                             = "ADeepLinkableActivity";

  static private final String  URI_PATH_PREFIX_PRODUCT_GROUP_LABEL = "/product-group-label/";
  static private final String  URI_PATH_PREFIX_PRODUCT_ID          = "/product-id/";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Checks the intent used to start this activity for a
   * deep link.
   *
   * @param schema The URI schema defined in your manifest for
   *               the intent filter.
   *
   * @return true, if a deep link was found. In addition, one
   *         of the callbacks will have been called.
   *
   *****************************************************/
  protected boolean findDeepLink( String schema )
    {
    // If the intent used to start this activity contains a URI, see if it matches
    // a deep link format.

    Intent intent = getIntent();

    if ( intent != null )
      {
      Uri uri = intent.getData();

      if ( uri != null )
        {
        String path =  uri.getPath();

        if ( path != null )
          {
          if ( path.startsWith( URI_PATH_PREFIX_PRODUCT_GROUP_LABEL ) )
            {
            onDeepLinkProductGroup( path.substring( URI_PATH_PREFIX_PRODUCT_GROUP_LABEL.length() ) );

            return ( true );
            }
          else if ( path.startsWith( URI_PATH_PREFIX_PRODUCT_ID ) )
            {
            onDeepLinkProductId( path.substring( URI_PATH_PREFIX_PRODUCT_ID.length() ) );

            return ( true );
            }
          }
        }
      }


    return ( false );
    }


  /*****************************************************
   *
   * Called when the intent URI contains a product group
   * label.
   *
   *****************************************************/
  abstract protected void onDeepLinkProductGroup( String productGroupLabel );


  /*****************************************************
   *
   * Called when the intent URI contains a product id.
   *
   *****************************************************/
  abstract protected void onDeepLinkProductId( String productId );


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

