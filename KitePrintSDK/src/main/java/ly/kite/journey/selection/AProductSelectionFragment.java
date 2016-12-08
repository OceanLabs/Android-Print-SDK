/*****************************************************
 *
 * AProductSelectionFragment.java
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

package ly.kite.journey.selection;


///// Import(s) /////

import android.app.Activity;

import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ICatalogueConsumer;
import ly.kite.journey.AKiteFragment;


///// Class Declaration /////

/*****************************************************
 *
 * This class is the parent of all product selection
 * fragments.
 *
 *****************************************************/
abstract public class AProductSelectionFragment extends AKiteFragment implements ICatalogueConsumer
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "AProductSelectionFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  protected Catalogue  mCatalogue;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////


  ////////// ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    mCatalogue = catalogue;
    }


  /*****************************************************
   *
   * Called when the catalogue load is cancelled.
   *
   *****************************************************/
  @Override
  public void onCatalogueCancelled()
    {
    // Don't do anything. Catalogue cancellations are dealt with
    // by the activity.
    }


  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueError( Exception exception )
    {
    // Don't do anything. Catalogue load errors are dealt with
    // by the activity.
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Requests the catalogue.
   *
   *****************************************************/
  protected void requestCatalogue()
    {
    Activity activity = getActivity();

    if ( activity != null && activity instanceof ICatalogueHolder )
      {
      ( (ICatalogueHolder)activity ).requestCatalogue( this );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

