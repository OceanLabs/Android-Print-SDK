/*****************************************************
 *
 * UserJourneyType.java
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

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ly.kite.KiteSDK;
import ly.kite.R;
import ly.kite.catalogue.Product;
import ly.kite.ordering.ImageSpec;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.util.AssetFragment;
import ly.kite.widget.EditableMaskedImageView;


///// Class Declaration /////

/*****************************************************
 *
 * This enum defines a type of user journey through the
 * shopping process.
 *
 *****************************************************/
public enum UserJourneyType
  {
  CIRCLE        ( R.drawable.filled_white_circle,    EditableMaskedImageView.BorderHighlight.OVAL ),

  CALENDAR      ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // A calendar job is a standard order, but blank images are allowed
            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              addJobsToOrder( context, product, orderQuantity, optionsMap, imageSpecList, true, order );
              }
            },

  FRAME,

  GREETINGCARD  ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // Greeting cards have their own job
            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              for ( ImageSpec imageSpec : imageSpecList )
                {
                AssetFragment assetFragment = imageSpec.getAssetFragment();
                int           quantity      = imageSpec.getQuantity();

                // We create a new job for each asset fragment
                for ( int index = 0; index < quantity; index ++ )
                  {
                  order.addJob( Job.createGreetingCardJob( product, orderQuantity, optionsMap, assetFragment ) );
                  }
                }
              }
            },

  PHONE_CASE    ( true ),

  PHOTOBOOK     ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // Photobooks have their own job
            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              // Determine if the front cover is a summary page
              boolean frontCoverIsSummary = KiteSDK.getInstance( context ).getCustomiser().photobookFrontCoverIsSummary();


              // We take the first image as the front cover, and use the remainder for the content pages

              AssetFragment       frontCoverAssetFragment = null;
              List<AssetFragment> contentAssetFragmentList = new ArrayList<>();

              int pageIndex = 0;

              for ( ImageSpec imageSpec : imageSpecList )
                {
                AssetFragment assetFragment;
                int           quantity;

                if ( imageSpec != null )
                  {
                  assetFragment = imageSpec.getAssetFragment();
                  quantity      = imageSpec.getQuantity();
                  }
                else
                  {
                  assetFragment = null;
                  quantity      = 1;
                  }

                if ( pageIndex == 0 && ( ! frontCoverIsSummary ) )
                  {
                  frontCoverAssetFragment = assetFragment;
                  }
                else
                  {
                  for ( int index = 0; index < quantity; index ++ )
                    {
                    contentAssetFragmentList.add( assetFragment );
                    }
                  }

                pageIndex ++;
                }

              order.addJob( Job.createPhotobookJob( product, orderQuantity, optionsMap, frontCoverAssetFragment, contentAssetFragmentList ) );
              }
            },

  POSTCARD,

  POSTER,

  RECTANGLE     ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE );


  ////////// Member Variable(s) //////////

  private boolean                                  mUsesSingleImage;
  private int                                      mEditMaskResourceId;
  private EditableMaskedImageView.BorderHighlight  mEditBorderHighlight;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  private UserJourneyType( boolean usesSingleImage, int editMaskResourceId, EditableMaskedImageView.BorderHighlight editBorderHighlight )
    {
    mUsesSingleImage     = usesSingleImage;
    mEditMaskResourceId  = editMaskResourceId;
    mEditBorderHighlight = editBorderHighlight;
    }

  private UserJourneyType( boolean usesSingleImage )
    {
    this( usesSingleImage, 0, null );
    }

  private UserJourneyType( int editMaskResourceId, EditableMaskedImageView.BorderHighlight editBorderHighlight )
    {
    this( false, editMaskResourceId, editBorderHighlight );
    }

  private UserJourneyType( EditableMaskedImageView.BorderHighlight editBorderHighlight )
    {
    this( false, 0, editBorderHighlight );
    }

  private UserJourneyType()
    {
    this( false, 0, null );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the user journey type uses a single
   * image for creating items, false otherwise.
   *
   *****************************************************/
  public boolean usesSingleImage()
    {
    return ( mUsesSingleImage );
    }


  /*****************************************************
   *
   * Returns the resource id of the mask used for editing.
   *
   *****************************************************/
  public int editMaskResourceId()
    {
    return ( mEditMaskResourceId );
    }


  /*****************************************************
   *
   * Returns the border highlight for editing.
   *
   *****************************************************/
  public EditableMaskedImageView.BorderHighlight editBorderHighlight()
    {
    return ( mEditBorderHighlight );
    }


  /*****************************************************
   *
   * Creates a job from the supplied assets.
   *
   * The default implementation creates a generic print
   * job.
   *
   *****************************************************/
  void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, boolean nullImagesAreBlankPages, Order order )
    {
    // Create a list of images.

    List<AssetFragment> assetFragmentList = new ArrayList<>();

    for ( ImageSpec imageSpec : imageSpecList )
      {
      AssetFragment assetFragment;
      int           quantity;

      if ( imageSpec != null )
        {
        assetFragment = imageSpec.getAssetFragment();
        quantity      = imageSpec.getQuantity();
        }
      else
        {
        assetFragment = null;
        quantity      = ( nullImagesAreBlankPages ? 1 : 0 );
        }


      // We need to duplicate images according to the ImageSpec quantity.
      for ( int index = 0; index < quantity; index ++ )
        {
        assetFragmentList.add( assetFragment );
        }
      }


    // TODO: Create multiple jobs, if more than quantityPerSheet
    order.addJob( Job.createPrintJob( product, orderQuantity, optionsMap, assetFragmentList ) );
    }


  /*****************************************************
   *
   * Creates a job from the supplied assets.
   *
   * The default implementation creates a generic print
   * job.
   *
   *****************************************************/
  public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
    {
    addJobsToOrder( context, product, orderQuantity, optionsMap, imageSpecList, false, order );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

