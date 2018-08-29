/*****************************************************
 *
 * IImagePickerItem.java
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

package ly.kite.imagepicker;


///// Import(s) /////

import android.widget.ImageView;

import java.util.LinkedHashMap;


///// Interface Declaration /////

/*****************************************************
 *
 * This interface defines an item in the image picker grid.
 *
 *****************************************************/
public interface IImagePickerItem
  {

  /*****************************************************
   *
   * Returns the URL of the image for the item.
   *
   *****************************************************/
  public String getImageURLString();


  /*****************************************************
   *
   * Returns the URL of a thumbnail image for the item.
   *
   *****************************************************/
  public void loadThumbnailImageInto( ImageView imageView );


  /*****************************************************
   *
   * Returns the text label for the item.
   *
   *****************************************************/
  public String getLabel();


  /*****************************************************
   *
   * Returns a key if this item is a parent and can be
   * descended into.
   *
   *****************************************************/
  public String getKeyIfParent();


  /*****************************************************
   *
   * Returns a selectable item if this item may be selected,
   * null otherwise.
   *
   *****************************************************/
  public ISelectableItem getSelectableItem();


  /*****************************************************
   *
   * Returns the number of this item's children that are
   * selected.
   *
   *****************************************************/
  public int getSelectedCount( LinkedHashMap<String,ISelectableItem> selectableItemTable );

  }
