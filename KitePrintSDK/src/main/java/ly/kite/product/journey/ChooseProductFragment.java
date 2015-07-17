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

package ly.kite.product.journey;


///// Import(s) /////

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.HashMap;

import ly.kite.R;
import ly.kite.analytics.Analytics;
import ly.kite.print.Product;
import ly.kite.print.ProductGroup;
import ly.kite.product.GroupOrProductAdaptor;


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
  public  static final String TAG                      = "ChooseProductFragment";

  private static final String BUNDLE_KEY_PRODUCT_GROUP = "productGroup";

  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private ProductGroup        mProductGroup;

  private ArrayList<Product>  mProductList;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates and returns a new instance of this fragment.
   *
   *****************************************************/
  public static ChooseProductFragment newInstance( ProductGroup productGroup )
    {
    ChooseProductFragment fragment = new ChooseProductFragment();

    Bundle argumentBundle = new Bundle();

    argumentBundle.putParcelable( BUNDLE_KEY_PRODUCT_GROUP, productGroup );

    fragment.setArguments( argumentBundle );

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
      mProductGroup = (ProductGroup)arguments.getParcelable( BUNDLE_KEY_PRODUCT_GROUP );

      if ( mProductGroup != null )
        {
        mKiteActivity.setTitle( mProductGroup.getDisplayLabel() );
        }
      else
        {
        Log.e( TAG, "No product group found in arguments" );
        }

      }
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
    View view = super.onCreateView( layoutInflator, container, savedInstanceState );

    if ( savedInstanceState == null )
      {
      Analytics.getInstance( mKiteActivity ).trackProductSelectionScreenViewed();
      }

    return ( view );
    }


  ////////// ProductManager.SyncListener Method(s) //////////

  /*****************************************************
   *
   * Called when the sync completes successfully.
   *
   *****************************************************/
  @Override
  public void onGotProducts( ArrayList<ProductGroup> productGroupList, HashMap<String,Product> productTable )
    {
    onProductFetchFinished();


    // Try and find a product list

    mProductList = ProductGroup.findProductsByGroupLabel( productGroupList, mProductGroup.getDisplayLabel() );

    if ( mProductList != null )
      {
      // Display the products
      mGridAdaptor = new GroupOrProductAdaptor( mKiteActivity, mProductList, mGridView );
      mGridView.setAdapter( mGridAdaptor );

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
    // Get the product group. Remember to ignore any clicks on placeholder images.

    int adaptorIndex = mGridView.adaptorIndexFromPosition( position );

    if ( adaptorIndex >= mProductList.size() ) return;

    Product chosenProduct = mProductList.get( adaptorIndex );


    // Call back to the activity
    if ( mKiteActivity instanceof ICallback )
      {
      ( (ICallback)mKiteActivity ).pOnProductChosen( chosenProduct );
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
    public void pOnProductChosen( Product product );
    }

  }
