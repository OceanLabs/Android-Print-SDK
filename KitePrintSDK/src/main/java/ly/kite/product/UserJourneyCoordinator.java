/*****************************************************
 *
 * UserJourneyCoordinator.java
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

package ly.kite.product;


///// Import(s) /////

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

import ly.kite.print.Asset;
import ly.kite.print.Product;
import ly.kite.product.journey.AJourneyFragment;
import ly.kite.product.journey.PhoneCaseFragment;


///// Class Declaration /////

/*****************************************************
 *
 * This singleton class coordinates the user journey following
 * the product selection.
 *
 *****************************************************/
public class UserJourneyCoordinator
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  private static final String  LOG_TAG = "UserJourneyCoordinator";


  ////////// Static Variable(s) //////////

  private static UserJourneyCoordinator  sUserJourneyCoordinator;


  ////////// Member Variable(s) //////////

  private HashMap<UserJourneyType,Journey>  mJourneyTable;


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////

  /*****************************************************
   *
   * Returns an instance of this coordinator.
   *
   *****************************************************/
  public static UserJourneyCoordinator getInstance()
    {
    if ( sUserJourneyCoordinator == null )
      {
      sUserJourneyCoordinator = new UserJourneyCoordinator();
      }

    return ( sUserJourneyCoordinator );
    }


  ////////// Constructor(s) //////////

  private UserJourneyCoordinator()
    {
    // Create the journey table

    mJourneyTable = new HashMap<>();

    addJourney( UserJourneyType.PHONE_CASE );  // TODO: Add UI class(es)
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Adds a user journey.
   *
   *****************************************************/
  private void addJourney( UserJourneyType type, Class<?>... uiClasses )
    {
    mJourneyTable.put( type, new Journey( type, uiClasses ) );
    }


  /*****************************************************
   *
   * Returns true if the user journey type is supported.
   *
   *****************************************************/
  public boolean isSupported( UserJourneyType type )
    {
    return ( mJourneyTable.containsKey( type ) );
    }


  /*****************************************************
   *
   * Starts the next stage in the appropriate user journey
   * for the supplied product.
   *
   *****************************************************/
  public AJourneyFragment getFragment( Context context, ArrayList<Asset> assetList, Product product )
    {
    return ( PhoneCaseFragment.newInstance( assetList, product ) );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * A set of fragments for a user journey type.
   *
   *****************************************************/
  private static class Journey
    {
    private UserJourneyType  mType;
    private Class<?>[]       mUIClasses;


    Journey( UserJourneyType type, Class<?>... uiClasses )
      {
      mType      = type;
      mUIClasses = uiClasses;
      }


    UserJourneyType getType()
      {
      return ( mType );
      }
    }

  }

