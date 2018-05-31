package ly.kite.address;

import junit.framework.Assert;


///// Import(s) /////

import junit.framework.TestCase;

import java.util.Locale;


///// Class Declaration /////

/*****************************************************
 *
 * This class tests the country class.
 *
 *****************************************************/
public class CountryTests extends TestCase {
    ////////// Static Constant(s) //////////

    @SuppressWarnings("unused")
    private static final String LOG_TAG = "CountryTests";

    ////////// Static Variable(s) //////////


    ////////// Member Variable(s) //////////


    ////////// Static Initialiser(s) //////////


    ////////// Static Method(s) //////////

    public void testGetInstance_withISO2CodeGB_returnsUK() {
        Country country = Country.getInstance("GB");
        Assert.assertEquals(country.displayName(), "United Kingdom");
    }

    public void testGetInstance_withISO2CodeLG_returnsSpain() {
        Country country = Country.getInstance("LG");
        Assert.assertEquals(country.displayName(), "Spain");
    }

    public void testGetInstance_withISO2CodeUnknown_returnsUS() {
        Country country = Country.getInstance("XX");
        Assert.assertEquals(country.displayName(), "United States");
    }

    public void testGetInstance_withLocaleLG() {
        Locale locale = new Locale("es", "LG");
        Country country = Country.getInstance(locale);
        Assert.assertEquals(country.displayName(), "Spain");
    }
}