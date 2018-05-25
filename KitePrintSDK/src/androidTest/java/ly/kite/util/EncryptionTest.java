package ly.kite.util;

/**
 * Created by andrei on 28/06/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import java.util.Random;

import ly.kite.KiteSDK;
import ly.kite.SecurePreferences;
import ly.kite.address.Address;
import ly.kite.address.Country;
import ly.kite.ordering.Order;
import ly.kite.ordering.OrderingDatabaseAgent;

public class EncryptionTest extends AndroidTestCase{


    private String ENCRYPTION_KEY = "5743h4j5bjhb34h5bhg3463";//random encryption key


    //created for testing the encryption/decryption with multiple string combinations
    public static String randomTextGenerator() {
        StringBuilder text = new StringBuilder();
        Random random = new Random();
        char temp;
        int randomLength = random.nextInt(30);

        //generate string no smaller that 3 characters but no bigger that 30 characters
        while( randomLength < 3 )
            randomLength = random.nextInt(30);

        for (int i = 0; i < randomLength; i++){
            temp = (char) (random.nextInt(96) + 32);
            text.append(temp);
        }
        return text.toString();
    }

     /**************************************************************************
     *                                                                         *
     *           Test if encryption input is different from output             *
     *                                                                         *
     **************************************************************************/
    public void testEncryption1()
        {
        SecurePreferences pref = new SecurePreferences(ENCRYPTION_KEY);
        String test_text       = "TestText";
        String encryptedText   = pref.encrypt(test_text);
        Assert.assertNotSame(test_text,encryptedText);
        }

     /**************************************************************************
     *                                                                         *
     *          Test if decryption outputs the correct information             *
     *                                                                         *
     **************************************************************************/
    public void testEncryption2()
        {
        SecurePreferences pref = new SecurePreferences(ENCRYPTION_KEY);
        String test_text       = "TestText";
        String encryptedText   = pref.encrypt(test_text);
        String decryptedText   = pref.decrypt(encryptedText);
        Assert.assertEquals(test_text, decryptedText);
        }


     /**************************************************************************
     *                                                                         *
     *         Test newOrder(OrderingDatabaseAgent) database encryption        *
     *                                                                         *
     **************************************************************************/

    public void testNewOrder()
    {
        int noOfRuns = 10;

        while(noOfRuns > 0) {
            getContext().deleteDatabase("ordering.db");//delete test database in case of old/corrupted data

            OrderingDatabaseAgent databaseAgent = new OrderingDatabaseAgent(getContext(), null);

            SecurePreferences pref = new SecurePreferences(ENCRYPTION_KEY);

            //generate address information
            Address adr = new Address();

            String recipient = randomTextGenerator();
            String line1 = randomTextGenerator();
            String line2 = randomTextGenerator();
            String state = randomTextGenerator();
            String zip = randomTextGenerator();
            String city = randomTextGenerator();
            String proof_pay = "PAY-test";
            String email = "info@kite.ly";
            String phone = "0123 456789";

            adr.setRecipientName(recipient);
            adr.setLine1(line1);
            adr.setLine2(line2);
            adr.setStateOrCounty(state);
            adr.setCountry(Country.USA);
            adr.setCity(city);
            adr.setZipOrPostalCode(zip);

            //populate order with information
            Order order = new Order();
            order.setShippingAddress(adr);
            order.setNotificationEmail(email);
            order.setNotificationPhoneNumber(phone);
            order.setOrderPricing(null);
            order.setProofOfPayment(proof_pay);

            databaseAgent.newOrder(1, order);

            Cursor cursor = null;

            //open the database
            SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/ly.kite.test/databases/ordering.db", null, 0);


            cursor = database.rawQuery("SELECT * FROM Address", null);

            //test if the information gets encrypted in Address table
            while (cursor.moveToNext()) {
                Assert.assertNotSame("address:recipient_name not encrypted",
                        cursor.getString(cursor.getColumnIndex("recipient_name")), recipient);
                Assert.assertNotSame("address:line1  not not encrypted",
                        cursor.getString(cursor.getColumnIndex("line1")), line1);
                Assert.assertNotSame("address:line2 not encrypted",
                        cursor.getString(cursor.getColumnIndex("line2")), line2);
                Assert.assertNotSame("address:city not encrypted",
                        cursor.getString(cursor.getColumnIndex("city")), city);
                Assert.assertNotSame("address:state_or_county not encrypted",
                        cursor.getString(cursor.getColumnIndex("state_or_county")), state);
                Assert.assertNotSame("address:zip code not encrypted",
                        cursor.getString(cursor.getColumnIndex("zip_or_postal_code")), zip);
                Assert.assertNotSame("address:county not encrypted",
                        cursor.getString(cursor.getColumnIndex("country_iso2_code")), adr.getCountry());
            }

            //reset cursor
            cursor = database.rawQuery("SELECT * FROM Address", null);

            //check if information gets decrypted correctly in Address table
            while (cursor.moveToNext()) {
                Assert.assertEquals("address:recipient_name not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("recipient_name"))), recipient);
                Assert.assertEquals("address:line1 not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("line1"))), line1);
                Assert.assertEquals("address:line2 not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("line2"))), line2);
                Assert.assertEquals("address:city not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("city"))), city);
                Assert.assertEquals("address:state_or_county not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("state_or_county"))), state);
                Assert.assertEquals("address:zip_or_postal_code not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("zip_or_postal_code"))), zip);
                Assert.assertEquals("address:country not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("country_iso2_code"))), "US");
            }

            //redefine cursor
            cursor = database.rawQuery("SELECT * FROM _Order", null);

            while (cursor.moveToNext()) {
                Assert.assertNotSame("order :proof_of_payment not encrypted",
                        cursor.getString(cursor.getColumnIndex("proof_of_payment")), proof_pay);
                Assert.assertNotSame("order :email not encrypted",
                        cursor.getString(cursor.getColumnIndex("notification_email")), email);
                Assert.assertNotSame("order :phone not encrypted",
                        cursor.getString(cursor.getColumnIndex("notification_phone")), phone);
            }

            //reset cursor
            cursor = database.rawQuery("SELECT * FROM _Order", null);

            while (cursor.moveToNext()) {
                Assert.assertEquals("order :proof_of_payment not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("proof_of_payment"))), proof_pay);
                Assert.assertEquals("order :email not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("notification_email"))), email);
                Assert.assertEquals("order :phone not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("notification_phone"))), phone);
            }

            database.close();
            noOfRuns--;
        }
    }

     /**************************************************************************
     *                                                                         *
     *  Test insertSuccessfulOrder (OrderingDatabaseAgent) database encryption *
     *                                                                         *
     **************************************************************************/

    public void testInsertSuccessfulOrder()
    {
        int noOfRuns = 10;

        while(noOfRuns > 0) {
            getContext().deleteDatabase("ordering.db");//delete test database in case of old/corrupted data

            OrderingDatabaseAgent databaseAgent = new OrderingDatabaseAgent(getContext(), null);
//            OrderingDatabaseAgent.setEncryptionKey(ENCRYPTION_KEY);


            String description = randomTextGenerator();
            String receipt = randomTextGenerator();
            String pricingJSON = randomTextGenerator();

            databaseAgent.insertSuccessfulOrder(description, receipt, pricingJSON);

            // Open the database
            SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/ly.kite.test/databases/ordering.db", null, 0);

            Cursor cursor = database.rawQuery("SELECT * FROM _Order", null);

            while (cursor.moveToNext()) {
                Assert.assertNotSame("receipt not encrypted",
                        cursor.getString(cursor.getColumnIndex("receipt")), receipt);
                Assert.assertNotSame("pricingJSON not encrypted",
                        cursor.getString(cursor.getColumnIndex("pricing_json")), pricingJSON);
            }

            //reset cursor
            cursor = database.rawQuery("SELECT * FROM _Order", null);

            SecurePreferences pref = new SecurePreferences(ENCRYPTION_KEY);
            while (cursor.moveToNext()) {
                Assert.assertEquals("receipt not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("receipt"))), receipt);
                Assert.assertEquals("receipt not decrypted correctly",
                        pref.decrypt(cursor.getString(cursor.getColumnIndex("pricing_json"))), pricingJSON);
            }

            database.close();
            noOfRuns--;
        }

    }

     /**************************************************************************
     *                                                                         *
     *      Test encryption and decryption of SharedPreferences files          *
     *                                                                         *
     **************************************************************************/

    public void testSharedPreferencesEncryption()
    {

        int noOfRuns = 10;

        while(noOfRuns>0) {
            Context context = getContext();


            SharedPreferences prefs_app = context.getSharedPreferences("kite_app_session_shared_prefs", Context.MODE_PRIVATE);
            SharedPreferences prefs_permanent = context.getSharedPreferences("kite_permanent_shared_prefs.xml", Context.MODE_PRIVATE);
            SharedPreferences prefs_customer = context.getSharedPreferences("kite_customer_session_shared_prefs.xml", Context.MODE_PRIVATE);
            //clear the preference files
            prefs_app.edit().clear().commit();
            prefs_customer.edit().clear().commit();
            prefs_permanent.edit().clear().commit();

            KiteSDK.IEnvironment environment;
            environment = KiteSDK.DefaultEnvironment.TEST;

            KiteSDK sdk = KiteSDK.getInstance(getContext());

            //generate keys
            String key1 = randomTextGenerator();
            String key2 = randomTextGenerator();
            String key3 = randomTextGenerator();

            String stringExample = randomTextGenerator();

            //generate address information
            Address adr = new Address();

            adr.setRecipientName(randomTextGenerator());
            adr.setLine1(randomTextGenerator());
            adr.setLine2(randomTextGenerator());
            adr.setStateOrCounty(randomTextGenerator());
            adr.setCountry(Country.USA);
            adr.setCity(randomTextGenerator());
            adr.setZipOrPostalCode(randomTextGenerator());

             /****************************************************
             * test kite_app_session_shared_prefs.xml encryption *
             ****************************************************/

            //test strings encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.APP_SESSION, key1, stringExample);
            //test boolean values encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.APP_SESSION, key2, false);
            //test address encryption/decryption
            sdk.setAppParameter(KiteSDK.Scope.APP_SESSION, key3, adr);

            //test if information was encrypted and gets decrypted correctly
            //NOTE: if the information is not encrypted in the first place , error occurs during decryption
            Assert.assertEquals("App_session: string value not decrypted correctly",
                    sdk.getStringAppParameter(context, KiteSDK.Scope.APP_SESSION, key1, "not_working"), stringExample);
            Assert.assertEquals("App_session: Boolean value not decrypted correctly",
                    sdk.getBooleanAppParameter(context, KiteSDK.Scope.APP_SESSION, key2, true), false);
            Assert.assertEquals("App_session: Address values not decrypted correctly",
                    sdk.getAddressAppParameter(KiteSDK.Scope.APP_SESSION, key3), adr);

             /*********************************************************
             * test kite_customer_session_shared_prefs.xml encryption *
             *********************************************************/

            //test strings encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.CUSTOMER_SESSION, key1, stringExample);
            //test boolean values encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.CUSTOMER_SESSION, key2, false);
            //test address encryption/decryption
            sdk.setAppParameter(KiteSDK.Scope.CUSTOMER_SESSION, key3, adr);

            //test if information was encrypted and gets decrypted correctly
            //NOTE: if the information is not encrypted in the first place , error occurs during decryption
            Assert.assertEquals("Customer_session: string value not decrypted correctly",
                    sdk.getStringAppParameter(context, KiteSDK.Scope.CUSTOMER_SESSION, key1, "not_working"), stringExample);
            Assert.assertEquals("Customer_session: Boolean value not decrypted correctly",
                    sdk.getBooleanAppParameter(context, KiteSDK.Scope.CUSTOMER_SESSION, key2, true), false);
            Assert.assertEquals("Customer_session: Address values not decrypted correctly",
                    sdk.getAddressAppParameter(KiteSDK.Scope.CUSTOMER_SESSION, key3), adr);

             /**************************************************
             * test kite_permanent_shared_prefs.xml encryption *
             **************************************************/

            //test strings encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.PERMANENT, key1, stringExample);
            //test boolean values encryption/decryption
            sdk.setAppParameter(context, KiteSDK.Scope.PERMANENT, key2, false);
            //test address encryption/decryption
            sdk.setAppParameter(KiteSDK.Scope.PERMANENT, key3, adr);

            //test if information was encrypted and gets decrypted correctly
            //NOTE: if the information is not encrypted in the first place , error occurs during decryption
            Assert.assertEquals("Permanent_session: string value not decrypted correctly",
                    sdk.getStringAppParameter(context, KiteSDK.Scope.PERMANENT, key1, "not_working"), stringExample);
            Assert.assertEquals("Permanent_session: Boolean value not decrypted correctly",
                    sdk.getBooleanAppParameter(context, KiteSDK.Scope.PERMANENT, key2, true), false);
            Assert.assertEquals("Permanent_session: Address values not decrypted correctly",
                    sdk.getAddressAppParameter(KiteSDK.Scope.PERMANENT, key3), adr);

            noOfRuns--;
        }
    }

    /**************************************************************************
     *                                                                         *
     *              Test if encryption can be deactivated                      *
     *                                                                         *
     **************************************************************************/

    public void testInactiveEncryption()
    {
        int noOfRuns = 10;

        while(noOfRuns>0) {
            Context context = getContext();
            KiteSDK.IEnvironment environment;
            environment = KiteSDK.DefaultEnvironment.TEST;

            KiteSDK sdk = KiteSDK.getInstance(getContext(),"pretend_key" ,environment);
            KiteSDK.ENCRYPTION_KEY = "off";

            SharedPreferences prefs_app = context.getSharedPreferences("kite_app_session_shared_prefs", Context.MODE_PRIVATE);
            prefs_app.edit().clear().commit();//clear the information from this preference file

            String stringExample = randomTextGenerator();

            //place information into kite_app_session_shared_prefs.xml by means of KiteSDK
            sdk.setAppParameter(context, KiteSDK.Scope.APP_SESSION, "text", stringExample);
            //directly read the info earlier placed into the .xml file and compare it to the original string
            Assert.assertEquals(prefs_app.getString("app_text", "defaultValue"), stringExample);

            noOfRuns--;
        }
    }
}
