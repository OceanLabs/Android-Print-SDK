/*****************************************************
 *
 * ChooseProductFragment.java
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;

import ly.kite.KiteSDK;
import ly.kite.analytics.Analytics;
import ly.kite.catalogue.Catalogue;
import ly.kite.catalogue.Product;
import ly.kite.catalogue.ProductGroup;
import ly.kite.R;
import ly.kite.widget.HeaderFooterGridView;


///// Class Declaration /////

/*****************************************************
 *
 * This fragment allows the user to choose a product.
 *
 *****************************************************/
public class ChooseProductFragment extends AGroupOrProductFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static public  final String TAG                            = "ChooseProductFragment";

  static private final String BUNDLE_KEY_PRODUCT_GROUP_LABEL = "productGroupLabel";

  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private String              mProductGroupLabel;
  private ProductGroup        mProductGroup;

  private ArrayList<Product>  mProductList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates and returns a new instance of this fragment.
   *
   *****************************************************/
  public static ChooseProductFragment newInstance( ProductGroup productGroup, String... productIds )
    {
    ChooseProductFragment fragment = new ChooseProductFragment();

    Bundle argumentBundle = addCommonArguments( fragment, productIds );

    argumentBundle.putString( BUNDLE_KEY_PRODUCT_GROUP_LABEL, productGroup.getDisplayLabel() );

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


    // Try to get the product group from the arguments

    Bundle arguments = getArguments();

    if ( arguments != null )
      {
      mProductGroupLabel = arguments.getString( BUNDLE_KEY_PRODUCT_GROUP_LABEL, null );

      if ( mProductGroupLabel == null )
        {
        Log.e( TAG, "No product group label found in arguments" );
        }
      }


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
    KiteSDK kiteSDK = KiteSDK.getInstance( mKiteActivity );

    // Get the parent grid view
    View view = super.onCreateView( layoutInflator, kiteSDK.getCustomiser().getChooseProductLayoutResourceId(), container, savedInstanceState );


    // Add any footer view to the grid before setting the adaptor

    int footerViewLayoutResourceId = KiteSDK.getInstance( mKiteActivity ).getCustomiser().getChooseProductGridFooterLayoutResourceId();

    if ( footerViewLayoutResourceId != 0 )
      {
      View footerView = LayoutInflater.from( mKiteActivity ).inflate( footerViewLayoutResourceId, mGridView, false );

      if ( footerView != null ) mGridView.addFooterView( footerView );
      }


    if ( savedInstanceState == null )
      {
      Analytics.getInstance( mKiteActivity ).trackProductListScreenViewed();
      }

    if ( mProductGroupLabel != null ) mKiteActivity.setTitle( mProductGroupLabel );

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

    if ( mProductGroupLabel != null ) mKiteActivity.setTitle( mProductGroupLabel );
    }


  /*****************************************************
   *
   * Called to create the menu.
   *
   *****************************************************/
  @Override
  public void onCreateOptionsMenu( Menu menu, MenuInflater menuInflator )
    {
    menuInflator.inflate( R.menu.choose_product, menu );
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


    // Call back to the activity in case it wants to (e.g.) add any headers / footers
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pOnPrePopulateProductGrid( catalogue, mGridView );
      }


    // Try and find a product list

    mProductList = catalogue.getProductsForGroup( mProductGroupLabel );

    if ( mProductList != null )
      {
      // Display the products

      mGridAdaptor = new GroupOrProductAdaptor( mKiteActivity, mProductList, mGridView, R.layout.grid_item_product );
      mGridView.setAdapter( mGridAdaptor );

      onRestoreManagedAdaptorViewPosition();


      // Register for item selection
      mGridView.setOnItemClickListener( this );
      }
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


    if ( mKiteActivity instanceof ICallback )
      {
      ICallback callback = (ICallback)mKiteActivity;


      // Convert the position into an adaptor index
      int adaptorIndex = mGridView.adaptorIndexFromPosition( position );


      // If a header / footer image is clicked - call back to the activity

      if ( adaptorIndex < 0 || adaptorIndex >= mProductList.size() )
        {
        callback.pOnHeaderOrFooterClicked( position, adaptorIndex );

        return;
        }



      // Get the product and call back to the activity with it

      Product chosenProduct = mProductList.get( adaptorIndex );

      callback.pOnProductChosen( chosenProduct );
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
    public void pOnPrePopulateProductGrid( Catalogue catalogue, HeaderFooterGridView headerFooterGridView );
    public void pOnHeaderOrFooterClicked( int position, int adaptorIndex );
    public void pOnProductChosen( Product product );
    }

  }

