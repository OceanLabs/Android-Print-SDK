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
  CALENDAR ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // A calendar job is a standard order, but blank images are allowed

            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( splitImagesIntoJobs( imageSpecList, product, true ) );
              }

            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              addJobsToOrder( context, product, orderQuantity, optionsMap, imageSpecList, true, order );
              }
            },

    CIRCLE ( R.drawable.filled_white_circle,    EditableMaskedImageView.BorderHighlight.OVAL )
              {
              @Override
              public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
                {
                return ( splitImagesIntoJobs( imageSpecList, product, false ) );
                }
              },

    FRAME
            {
            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( null );
              }
            },

  GREETINGCARD ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // Greetings cards are saved to the database with 1 x (front) image and 3 x (null) other
            // images. So we want to ignore the null images for the moment.

            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              // Flatten the image spec list
              ArrayList<ImageSpec> flatImageSpecList = flattenImageSpecList( imageSpecList, false );


              // Create multiple sets of 4 images

              List<List<ImageSpec>> splitImageSpecLists = new ArrayList<>();

              for ( ImageSpec imageSpec : flatImageSpecList )
                {
                List<ImageSpec> itemImageSpecList = new ArrayList<>( 4 );

                itemImageSpecList.add( imageSpec );
                itemImageSpecList.add( null );
                itemImageSpecList.add( null );
                itemImageSpecList.add( null );

                splitImageSpecLists.add( itemImageSpecList );
                }


              return ( splitImageSpecLists );
              }

            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              for ( ImageSpec imageSpec : imageSpecList )
                {
                if ( imageSpec != null )
                  {
                  AssetFragment assetFragment = imageSpec.getAssetFragment();
                  int           quantity      = imageSpec.getQuantity();

                  // Create a new job for each image
                  for ( int index = 0; index < quantity; index++ )
                    {
                    order.addJob( Job.createGreetingCardJob( product, orderQuantity, optionsMap, assetFragment ) );
                    }
                  }
                }
              }

            @Override
            public ArrayList<ImageSpec> creationImagesFromDBImages( ArrayList<ImageSpec> basketItemImages )
              {
              // For greetings cards, images are stored in the basket as one front cover image and 3 null images. For
              // editing we want to simply return the front cover image.

              ArrayList<ImageSpec> productCreationImages = new ArrayList<>( 1 );

              productCreationImages.add( basketItemImages.get( 0 ) );

              return ( productCreationImages );
              }
            },

  PHONE_CASE ( true )
            {
            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( splitImagesIntoJobs( imageSpecList, product, false ) );
              }
            },

  PHOTOBOOK ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // Photobook image spec lists are already clamped to the correct size
            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              boolean frontCoverIsSummary = KiteSDK.getInstance( context ).getCustomiser().photobookFrontCoverIsSummary();

              int imagesPerJob = product.getQuantityPerSheet() + ( frontCoverIsSummary ? 0 : 1 );

              return ( splitImagesIntoJobs( imageSpecList, imagesPerJob, true ) );
              }

            // Photobooks have their own job
            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              // Flatten the images into a single list. We shouldn't technically need to do this, as
              // the Photobook UI only supplies image specs with a quantity of 1. Perform this step, though,
              // in case an app supplies the image spec, and also for testing.
              ArrayList<ImageSpec> flatImageSpecList = flattenImageSpecList( imageSpecList, true );


              // Determine if the front cover is a summary page
              boolean frontCoverIsSummary = KiteSDK.getInstance( context ).getCustomiser().photobookFrontCoverIsSummary();


              // We take the first image as the front cover, and use the remainder for the content pages

              AssetFragment       frontCoverAssetFragment = null;
              List<AssetFragment> contentAssetFragmentList = new ArrayList<>();

              int pageIndex = 0;

              for ( ImageSpec singleQuantityImageSpec : flatImageSpecList )
                {
                AssetFragment assetFragment;

                if ( singleQuantityImageSpec != null )
                  {
                  assetFragment = singleQuantityImageSpec.getAssetFragment();
                  }
                else
                  {
                  assetFragment = null;
                  }

                if ( pageIndex == 0 && ( ! frontCoverIsSummary ) )
                  {
                  frontCoverAssetFragment = assetFragment;
                  }
                else
                  {
                  contentAssetFragmentList.add( assetFragment );
                  }

                pageIndex ++;
                }

              order.addJob( Job.createPhotobookJob( product, orderQuantity, optionsMap, frontCoverAssetFragment, contentAssetFragmentList ) );
              }
            },

  POSTCARD
            {
            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( null );
              }
            },

  POSTER ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // A poster job is a standard order, but blank images are allowed

            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( splitImagesIntoJobs( imageSpecList, product, true ) );
              }

            @Override
            public void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String, String> optionsMap, List<ImageSpec> imageSpecList, Order order )
              {
              addJobsToOrder( context, product, orderQuantity, optionsMap, imageSpecList, true, order );
              }
            },

  RECTANGLE ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            @Override
            public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product )
              {
              return ( splitImagesIntoJobs( imageSpecList, product, false ) );
              }
            };


  ////////// Member Variable(s) //////////

  private boolean                                  mUsesSingleImage;
  private int                                      mEditMaskResourceId;
  private EditableMaskedImageView.BorderHighlight  mEditBorderHighlight;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Flattens an image spec list with multiple quantities
   * into a list with potentially duplicated image specs,
   * but all with a quantity of 1.
   *
   *****************************************************/
  static protected ArrayList<ImageSpec> flattenImageSpecList( List<ImageSpec> sourceImageSpecList, boolean nullImagesAreBlank )
    {
    // Expand out the images into a flat list, with images duplicated if the ImageSpec quantity
    // is greater than 1. Note that when we flatten out a null image spec, we add a null image
    // spec to the list, not an image spec with a null asset fragment.

    ArrayList<ImageSpec> flattenedImageSpecList = new ArrayList<>();

    for ( ImageSpec sourceImageSpec : sourceImageSpecList )
      {
      ImageSpec  flatImageSpec;
      int        quantity;

      if ( sourceImageSpec != null )
        {
        flatImageSpec = new ImageSpec( sourceImageSpec.getAssetFragment(), sourceImageSpec.getBorderText(), 1 );
        quantity      = sourceImageSpec.getQuantity();
        }
      else
        {
        flatImageSpec = null;
        quantity      = ( nullImagesAreBlank ? 1 : 0 );
        }

      // Duplicate images according to the quantity.
      for ( int index = 0; index < quantity; index ++ )
        {
        flattenedImageSpecList.add( flatImageSpec );
        }
      }

    return ( flattenedImageSpecList );
    }


  /*****************************************************
   *
   * Splits a single list of image image specs into multiple
   * lists, according to the maximum number of images per
   * item.
   *
   *****************************************************/
  static List<List<ImageSpec>> splitImagesIntoJobs( List<ImageSpec> imageSpecList, int imagesPerJob, boolean nullImagesAreBlank )
    {
    // Flatten the images into a single list
    ArrayList<ImageSpec> flatImageSpecList = flattenImageSpecList( imageSpecList, nullImagesAreBlank );


    // Go through the image specs in batches, and create a list for each batch of images.

    ArrayList<List<ImageSpec>> imageSpecLists = new ArrayList<>();

    for ( int offset = 0; offset < flatImageSpecList.size(); offset += imagesPerJob )
      {
      List<ImageSpec> jobImageSpecList = flatImageSpecList.subList( offset, Math.min( offset + imagesPerJob, flatImageSpecList.size() ) );

      imageSpecLists.add( jobImageSpecList );
      }


    return ( imageSpecLists );
    }


  /*****************************************************
   *
   * Splits a single list of image image specs into multiple
   * lists, according to the maximum number of images per
   * item.
   *
   *****************************************************/
  static List<List<ImageSpec>> splitImagesIntoJobs( List<ImageSpec> imageSpecList, Product product, boolean nullImagesAreBlank )
    {
    return ( splitImagesIntoJobs( imageSpecList, product.getQuantityPerSheet(), nullImagesAreBlank ) );
    }


  /*****************************************************
   *
   * Splits a single list of image image specs into multiple
   * lists, according to the maximum number of images per
   * item.
   *
   *****************************************************/
  static public List<List<ImageSpec>> splitImagesIntoJobs( List<ImageSpec> imageSpecList, Product product )
    {
    return ( splitImagesIntoJobs( imageSpecList, product, false ) );
    }



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
   * Splits a single list of image image specs into multiple
   * lists, according to how they are stored on the database.
   *
   *****************************************************/
  abstract public List<List<ImageSpec>> dbItemsFromCreationItems( Context context, List<ImageSpec> imageSpecList, Product product );


  /*****************************************************
   *
   * Creates a job from the supplied assets.
   *
   * The default implementation creates a generic print
   * job.
   *
   *****************************************************/
  static void addJobsToOrder( Context context, Product product, int orderQuantity, HashMap<String,String> optionsMap, List<ImageSpec> imageSpecList, boolean nullImagesAreBlank, Order order )
    {
    // Split the images into a list for each separate job
    List<List<ImageSpec>> imageSpecLists = splitImagesIntoJobs( imageSpecList, product, nullImagesAreBlank );

    // Go through each list of images and create a job for it.
    for ( List<ImageSpec> jobImageSpecList : imageSpecLists )
      {
      order.addJob( Job.createPrintJob( product, orderQuantity, optionsMap, jobImageSpecList, nullImagesAreBlank ) );
      }
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


  /*****************************************************
   *
   * Returns a list image image specs suitable for passing
   * to the product creation activity, from a basket item
   * list.
   *
   *****************************************************/
  public ArrayList<ImageSpec> creationImagesFromDBImages( ArrayList<ImageSpec> basketItemImages )
    {
    // Default behaviour is to return list unchanged
    return ( basketItemImages );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

