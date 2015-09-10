/*****************************************************
 *
 * ImageSourceFragment.java
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

package ly.kite.journey;


///// Import(s) /////

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.List;

import ly.kite.R;
import ly.kite.product.Asset;


///// Class Declaration /////

/*****************************************************
 *
 * This activity allows the user to create a phone
 * case design using an image.
 *
 *****************************************************/
public class ImageSourceFragment extends AKiteFragment
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String      LOG_TAG                                   = "ImageSourceFragment";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////

  private GridView  mGridView;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Creates a new instance of this fragment.
   *
   *****************************************************/
  public static ImageSourceFragment newInstance()
    {
    ImageSourceFragment fragment = new ImageSourceFragment();

    return ( fragment );
    }


  ////////// Constructor(s) //////////


  ////////// AEditImageFragment Method(s) //////////

  /*****************************************************
   *
   * Returns the content view for this fragment
   *
   *****************************************************/
  @Override
  public View onCreateView( LayoutInflater layoutInflator, ViewGroup container, Bundle savedInstanceState )
    {
    View view = layoutInflator.inflate( R.layout.screen_image_source, container, false );

    mGridView = (GridView)view.findViewById( R.id.image_source_grid_view );


    // Set up the image source grid

    ImageSourceAdaptor imageSourceAdaptor = new ImageSourceAdaptor( mKiteActivity, R.layout.grid_item_image_source_vertical, ImageSource.values() );

    mGridView.setAdapter( imageSourceAdaptor );


    return ( view );
    }


  /*****************************************************
   *
   * Called with the result of an activity.
   *
   *****************************************************/
  @Override
  public void onActivityResult( int requestCode, int resultCode, Intent returnedIntent )
    {
    super.onActivityResult( requestCode, resultCode, returnedIntent );


    // Get assets for any images returned

    List<Asset> assetList = ImageSource.getAssetsFromResult( requestCode, resultCode, returnedIntent );

    if ( assetList != null && assetList.size() > 0 )
      {
      // TODO
      }

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

    mKiteActivity.setTitle( R.string.title_image_source );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/


  ////////// Inner Class(es) //////////


  }

