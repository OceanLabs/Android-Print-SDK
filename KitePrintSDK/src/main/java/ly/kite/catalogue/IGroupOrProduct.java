/*****************************************************
 *
 * IGroupOrProduct.java
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
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

import android.content.Context;
import android.widget.ImageView;

import java.net.URL;

/*****************************************************
 *
 * This interfaces represents a displayable item on the
 * product groups / products screens.
 *
 *****************************************************/
public interface IGroupOrProduct
  {
  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns the display image URL.
   *
   *****************************************************/
  public URL getDisplayImageURL();


  /*****************************************************
   *
   * Returns the gravity for the display image.
   *
   *****************************************************/
  public int getDisplayImageAnchorGravity( Context context );


  /*****************************************************
   *
   * Returns the display label.
   *
   *****************************************************/
  public String getDisplayLabel();


  /*****************************************************
   *
   * Returns the display label colour.
   *
   *****************************************************/
  public int getDisplayLabelColour();


  /*****************************************************
   *
   * Returns true if this group or product contains
   * multiple prices. Generally if this is a group containing
   * multiple products, it will return true.
   *
   *****************************************************/
  public boolean containsMultiplePrices();


  /*****************************************************
   *
   * Returns a display price.
   *
   *****************************************************/
  public String getDisplayPrice( String preferredCurrency );


  /*****************************************************
   *
   * Returns a description, or null if there is no
   * description.
   *
   *****************************************************/
  public String getDescription();


  /*****************************************************
   *
   * Returns true or false according to whether a flag is
   * set.
   *
   *****************************************************/
  public boolean flagIsSet( String tag );


  }

