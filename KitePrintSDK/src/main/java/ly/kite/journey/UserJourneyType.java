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

import java.util.ArrayList;
import java.util.List;

import ly.kite.R;
import ly.kite.catalogue.Product;
import ly.kite.ordering.Job;
import ly.kite.ordering.Order;
import ly.kite.util.Asset;
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
  FRAME,
  GREETINGCARD  ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE )
            {
            // Greeting cards have their own job
            @Override
            public void addJobsToOrder( Product product, List<Asset> assetList, Order order )
              {
              for ( Asset asset : assetList )
                {
                order.addJob( Job.createGreetingCardJob( product, asset ) );
                }
              }
            },
  PHONE_CASE    ( true ),
  PHOTOBOOK     ( R.drawable.filled_white_rectangle, EditableMaskedImageView.BorderHighlight.RECTANGLE ),
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
  public void addJobsToOrder( Product product, List<Asset> assetList, Order order )
    {
    order.addJob( Job.createPrintJob( product, assetList ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

