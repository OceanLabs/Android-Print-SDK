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

import ly.kite.R;


///// Class Declaration /////

/*****************************************************
 *
 * This enum defines a type of user journey through the
 * shopping process.
 *
 *****************************************************/
public enum UserJourneyType
  {
  CIRCLE ( R.drawable.filled_white_circle )
          {
          public boolean editedImageCompatibleWith( UserJourneyType otherType )
            {
            if ( otherType == CIRCLE || otherType == RECTANGLE )
              {
              return ( true );
              }

            return ( false );
            }
          },
  FRAME,
  GREETING_CARD,
  PHONE_CASE,
  PHOTOBOOK,
  POSTCARD,
  POSTER,
  RECTANGLE ( R.drawable.filled_white_rectangle )
          {
          public boolean editedImageCompatibleWith( UserJourneyType otherType )
            {
            if ( otherType == RECTANGLE || otherType == CIRCLE )
              {
              return ( true );
              }

            return ( false );
            }
          };


  ////////// Member Variable(s) //////////

  private int  mMaskResourceId;

  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  ////////// Constructor(s) //////////

  private UserJourneyType( int maskResourceId )
    {
    mMaskResourceId = maskResourceId;
    }


  private UserJourneyType()
    {
    this( 0 );
    }


  ////////// Method(s) //////////

  /*****************************************************
   *
   * Returns true if the other type is not null and
   * its edited image is compatible with this one.
   *
   * The default is false. Override the method for
   * individual types above.
   *
   *****************************************************/
  public boolean editedImageCompatibleWith( UserJourneyType otherType )
    {
    return ( false );
    }


  /*****************************************************
   *
   * Returns the resource id of the mask.
   *
   *****************************************************/
  public int maskResourceId()
    {
    return ( mMaskResourceId );
    }


  ////////// Inner Class(es) //////////

  /*****************************************************
   *
   * ...
   *
   *****************************************************/

  }

