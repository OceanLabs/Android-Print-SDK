/*****************************************************
 *
 * ChooseProductGroupFragment.java
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

package ly.kite.journey.selection;


///// Import(s) /////

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.ProductGroup;
import ly.kite.widget.HeaderFooterGridView;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment allows the user to choose a product
 * group.
 *
 *****************************************************/
public class ChooseProductGroupFragment extends AGroupOrProductFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public final String  TAG = "ChooseProductGroupFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ArrayList<ProductGroup>  mProductGroupList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates and returns a new instance of this fragment.
   *
   *****************************************************/
  public static ChooseProductGroupFragment newInstance( String... productIds )
    {
    ChooseProductGroupFragment fragment = new ChooseProductGroupFragment();

    addCommonArguments( fragment, productIds );

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AGroupOrProductFragment Method(s) //////////

  /*****************************************************
   *
   * Called when the fragment is created.
   *
   *****************************************************/
  @Override
  public void onCreate( Bundle savedInstanceState )
    {
    super.onCreate( savedInstanceState );

    setHasOptionsMenu( true );
    }


  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    // Get the parent grid view
    View view = super.onCreateView( layoutInflator, R.layout.screen_choose_product_group, container, savedInstanceState );

    if ( savedInstanceState == null )
      {
      Analytics analytics = Analytics.getInstance( mKiteActivity );

      analytics.trackSDKLoaded( Analytics.ENTRY_POINT_JSON_PROPERTY_VALUE_HOME_SCREEN );
      analytics.trackCategoryListScreenViewed();
      }

    return ( view );
    }


  /*****************************************************
   *
   * Called when the fragment is top-most.
   *
   *****************************************************/
  @Override
  public void onTop()
    {
    super.onTop();

    mKiteActivity.setTitle( R.string.kitesdk_title_choose_product_group);
    }


  /*****************************************************
   *
   * Called to create the menu.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    menuInflator.inflate( R.menu.choose_product_group, menu );
    }


  ////////// ICatalogueConsumer Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onCatalogueSuccess( Catalogue catalogue )
    {
    super.onCatalogueSuccess( catalogue );


    mProductGroupList = catalogue.getProductGroupList();


    // Call back to the activity in case it wants to (e.g.) add any headers / footers
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pgOnPrePopulateProductGroupGrid( catalogue, mGridView );
      }


    // Display the product groups

    mGridAdaptor = new GroupOrProductAdaptor( mKiteActivity, mProductGroupList, mGridView, R.layout.grid_item_product_group );
    mGridView.setAdapter( mGridAdaptor );

    onRestoreManagedAdaptorViewPosition();


    // Register for item selection
    mGridView.setOnItemClickListener( this );
    }


  ////////// AdapterView.OnItemClickListener Method(s) //////////

  /*****************************************************
   *
   * Called when a product group is clicked.
   *
   *****************************************************/
  @Override
  public void onItemClick( AdapterView<?> parent, View view, int position, long id )
    {
    onSaveManagedAdaptorViewPosition( position );


    if ( mKiteActivity instanceof ChooseProductFragment.ICallback )
      {
      ICallback callback = (ICallback) mKiteActivity;


      // Convert the position into an adaptor index
      int adaptorIndex = mGridView.adaptorIndexFromPosition( position );


      // If a header / footer image is clicked - call back to the activity

      if ( adaptorIndex < 0 || adaptorIndex >= mProductGroupList.size() )
        {
        callback.pgOnHeaderOrFooterClicked( position, adaptorIndex );

        return;
        }


      // Get the product group and call back to the activity with it

      ProductGroup chosenProductGroup = mProductGroupList.get( adaptorIndex );

      callback.pgOnProductGroupChosen( chosenProductGroup );
      }
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * The callback interface for this fragment.
   *
   *****************************************************/
  public interface ICallback
    {
    public void pgOnPrePopulateProductGroupGrid( Catalogue catalogue, HeaderFooterGridView headerFooterGridView );
    public void pgOnHeaderOrFooterClicked( int position, int adaptorIndex );
    public void pgOnProductGroupChosen( ProductGroup productGroup );
    }

  }

