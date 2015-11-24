/*****************************************************
 *
 * ALimitedRangeEditTextEnforcer.java
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

package ly.kite.widget;


///// Import(s) /////

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


///// Class Declaration /////

/*****************************************************
 *
 * This class limits what can be typed into an edit text
 * to a small range of strings.
 *
 *****************************************************/
abstract public class ALimitedRangeEditTextEnforcer extends AEditTextEnforcer implements InputFilter, TextWatcher
  {
  ////////// Static Constant(s) //////////

  @SuppressWarnings( "unused" )
  static private final String  LOG_TAG = "ALimitedRangeEditTextEnforcer";


  ////////// Static Variable(s) //////////


  ////////// Member Variable(s) //////////


  ////////// Static Initialiser(s) //////////


  ////////// Static Method(s) //////////


  /*****************************************************
   *
   * Returns a list of all the valid strings that contain the
   * supplied (sub)string.
   *
   *****************************************************/
  static public List<String> getValidStrings( String[] validStrings, String searchString )
    {
    ArrayList<String> potentialStringList = new ArrayList<>();


    for ( String candidateString : validStrings )
      {
      if ( candidateString.contains( searchString ) )
        {
        potentialStringList.add( candidateString );
        }
      }


    return ( potentialStringList );
    }


  ////////// Constructor(s) //////////

  public ALimitedRangeEditTextEnforcer( EditText editText, ICallback callback )
    {
    super( editText, callback );

    // Set up the filter
    final InputFilter[] inputFilters = { this };
    editText.setFilters( inputFilters );

    // Set up the text change listener
    editText.addTextChangedListener( this );
    }


  ////////// InputFilter Method(s) //////////

  /*****************************************************
   *
   * Filters changes to the edit text field.
   *
   *****************************************************/
  @Override
  public CharSequence filter( CharSequence source, int start, int end, Spanned dest, int dstart, int dend )
    {
    //Log.d( LOG_TAG, "filter( source = " + source + ", start = " + start + ", end = " + end + ", dest = " + dest + ", dstart = " + dstart + ", dend = " + dend + " )" );


    // See if there is a shortcut string

    if ( ( dest == null || dest.length() == 0 ) && ( end - start ) == 1 )
      {
      String shortcutString = getShortcutString( source.charAt( start ) );

      if ( shortcutString != null ) return ( shortcutString );
      }


    // Work out what the result string would be, and see if any of the valid strings
    // contain it.

    StringBuilder stringBuilder = new StringBuilder( dest );

    stringBuilder.replace( dstart, dend, String.valueOf( source.subSequence( start, end ) ) );

    if ( getValidStringsContaining( stringBuilder.toString() ).size() < 1 )
      {
      return ( "" );
      }


    return ( null );
    }


  ////////// TextWatcher Method(s) //////////

  /*****************************************************
   *
   * Called before the text is changed.
   *
   *****************************************************/
  @Override
  public void beforeTextChanged( CharSequence s, int start, int count, int after )
    {
    // Do nothing
    }


  /*****************************************************
   *
   * Called after the text is changed.
   *
   *****************************************************/
  @Override
  public void onTextChanged( CharSequence charSequence, int start, int before, int count )
    {
    // Ignore empty strings
    if ( charSequence == null || charSequence.length() < 1 ) return;


    // If we find any single valid valid value containing the character sequence, change
    // the text to it

    List<String> potentialStringList = getValidStringsContaining( charSequence.toString() );

    int potentialStringCount = potentialStringList.size();

    if ( potentialStringCount < 1 )
      {
      // We found nothing so change the field to red
      mEditText.setTextColor( TEXT_COLOUR_ERROR );
      }
    else
      {
      // We found something so change the colour to normal
      mEditText.setTextColor( mOKTextColour );

      if ( potentialStringCount == 1 )
        {
        // We found one match. If the field isn't already the one match (we don't want
        // an infinite recursion), then change it now.

        String matchingString = potentialStringList.get( 0 );

        if ( ! matchingString.equals( charSequence.toString() ) )
          {
          mEditText.setText( matchingString );

          // Put the cursor at the end
          mEditText.setSelection( matchingString.length() );
          }

        if ( mCallback != null ) mCallback.eteOnTextComplete( mEditText );
        }
      }

    }


  /*****************************************************
   *
   * Called after the text is changed.
   *
   *****************************************************/
  @Override
  public void afterTextChanged( Editable s )
    {
    // Do nothing
    }


  /*****************************************************
   *
   * Returns a valid string for a single entered character,
   * or null if no shortcut should be available.
   *
   *****************************************************/
  protected String getShortcutString( char character )
    {
    return ( null );
    }


  /*****************************************************
   *
   * Returns a list of all the valid strings that contain the
   * supplied (sub)string.
   *
   *****************************************************/
  abstract protected List<String> getValidStringsContaining( String searchString );


  ////////// Inner Class(es) //////////

  }