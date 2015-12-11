/*****************************************************
 *
 * ViewHelper.java
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

package ly.kite.widget;


///// Import(s) /////


///// Class Declaration /////

import android.view.View;
import android.view.ViewGroup;

import ly.kite.catalogue.Product;

/*****************************************************
 *
 * This class contains helper methods for use with views.
 *
 *****************************************************/
public class ViewHelper
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG                      = "ViewHelper";


  public  static final String  TAG_EFFECT_VISIBLE_IF        = "visibleif";

  private static final String  TAG_EFFECT_SEPARATOR         = ":";
  private static final String TAG_EQUALITY_OPERATOR         = "=";

  public  static final String  TAG_PROPERTY_PRODUCT_ID      = "product.id";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Traverses a view hierarchy returning each view to the
   * callback.
   *
   *****************************************************/
  public static void traverseViewHierarchy( View view, IViewCallback callback )
    {
    // The root view is either a View or a ViewGroup

    if ( view instanceof ViewGroup )
      {
      ViewGroup viewGroup = (ViewGroup)view;


      // Iterate through the view group's children, and recursively traverse each one.

      int childCount = viewGroup.getChildCount();

      for ( int childIndex = 0; childIndex < childCount; childIndex ++ )
        {
        traverseViewHierarchy( viewGroup.getChildAt( childIndex ), callback );
        }
      }
    else
      {
      callback.nextView( view );
      }
    }


  /*****************************************************
   *
   * Traverses a view hierarchy looking for tags. Alters
   * the properties of the view depending on the tag.
   *
   * Currently only used to conditionally set the visibilty
   * of a view according to the product id.
   *
   *****************************************************/
  public static void setAllViewProperties( View rootView, Product product )
    {
    // Traverse the view hierarchy using a callback that checks the tag and
    // sets the property accordingly.

    traverseViewHierarchy( rootView, new ViewPropertySetter( product ) );
    }


  ////////// Constructor(s) //////////


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A view hierarchy traversal callback.
   *
   *****************************************************/
  private interface IViewCallback
    {
    public void nextView( View view );
    }


  /*****************************************************
   *
   * A view consumer that sets the properties according to
   * the tag.
   *
   *****************************************************/
  static private class ViewPropertySetter implements IViewCallback
    {
    private Product  mProduct;


    ViewPropertySetter( Product product )
      {
      mProduct = product;
      }


    /*****************************************************
     *
     * Consumes a view, and sets any properties for it.
     *
     *****************************************************/
    public void nextView( View view )
      {
      // Look for special tags

      Object tagObject = view.getTag();

      if ( tagObject != null && tagObject instanceof String )
        {
        String tagString = (String)tagObject;

        int effectSeparatorIndex = tagString.indexOf( TAG_EFFECT_SEPARATOR );

        if ( effectSeparatorIndex > 0 )
          {
          int equalitySeparatorIndex = tagString.indexOf( TAG_EQUALITY_OPERATOR, effectSeparatorIndex );

          if ( equalitySeparatorIndex > effectSeparatorIndex )
            {
            String effect   = tagString.substring( 0, effectSeparatorIndex );
            String property = tagString.substring( effectSeparatorIndex + 1, equalitySeparatorIndex );
            String value    = tagString.substring( equalitySeparatorIndex + 1 );

            setViewProperties( view, effect, property, value );
            }
          }
        }
      }


    /*****************************************************
     *
     * Sets the view properties according to the effect, and
     * whether the property matches the value.
     *
     *****************************************************/
    private void setViewProperties( View view, String effect, String property, String value )
      {
      // Determine whether the condition is valid, i.e. the <property> = <value>
      boolean propertySet = propertyIsEqual( property, value );

      // Determine what effect needs to be set

      if ( effect.equals( TAG_EFFECT_VISIBLE_IF ) )
        {
        if ( propertySet ) view.setVisibility( View.VISIBLE );
        else               view.setVisibility( View.INVISIBLE );
        }
      }


    /*****************************************************
     *
     * Determines whether the property equals the value.
     *
     *****************************************************/
    private boolean propertyIsEqual( String property, String value )
      {
      // Determine what property we are testing

      if ( property.equals( TAG_PROPERTY_PRODUCT_ID ) )
        {
        return ( mProduct.getId().equals( value ) );
        }

      return ( false );
      }


    }


  }

