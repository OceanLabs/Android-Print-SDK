package ly.kite.address;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class Address implements Parcelable, Serializable {

    private static final String PERSISTED_ADDRESS_BOOK_FILENAME = "address_book";
    private static final int NOT_PERSITED = -1;

    private static final long serialVersionUID = 0L;

    private String recipientName;
    private String line1;
    private String line2;
    private String city;
    private String stateOrCounty;
    private String zipOrPostalCode;
    private Country country;

    private int storageIdentifier = NOT_PERSITED;

    // Partial address fields
    private String addressId;
    private String displayName;
    private boolean searchRequiredForFullDetails;

    static Address createPartialAddress(String addressId, String displayName) {
        Address addr = new Address();
        addr.addressId = addressId;
        addr.displayName = displayName;
        addr.searchRequiredForFullDetails = true;
        return addr;
    }

    public Address() {

    }

    public Address(String recipientName, String line1, String line2, String city, String stateOrCounty, String zipOrPostalCode, Country country) {
        this.recipientName = recipientName;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.stateOrCounty = stateOrCounty;
        this.zipOrPostalCode = zipOrPostalCode;
        this.country = country;
    }

    public static AddressSearchRequest search(String query, Country country, AddressSearchRequestListener listener) {
        AddressSearchRequest req = new AddressSearchRequest();
        req.search(query, country, listener);
        return req;
    }

    public static AddressSearchRequest search(Address address, AddressSearchRequestListener listener) {
        AddressSearchRequest req = new AddressSearchRequest();
        req.search(address, listener);
        return req;
    }

    public static Address getKiteTeamAddress() {
        Address addr = new Address();
        addr.recipientName = "Kite Team";
        addr.line1 = "Eastcastle House";
        addr.line2 = "27-28 Eastcastle St";
        addr.city  = "London";
        addr.zipOrPostalCode = "W1W 8DH";
        addr.country = Country.getInstance("GBR");
        return addr;
    }

    public boolean isSearchRequiredForFullDetails() {
        return searchRequiredForFullDetails;
    }

    String getId() {
        return addressId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getStateOrCounty() {
        return stateOrCounty;
    }

    public String getZipOrPostalCode() {
        return zipOrPostalCode;
    }

    public Country getCountry() {
        return country;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStateOrCounty(String stateOrCounty) {
        this.stateOrCounty = stateOrCounty;
    }

    public void setZipOrPostalCode(String zipOrPostalCode) {
        this.zipOrPostalCode = zipOrPostalCode;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    @Override
    public String toString() {
        if (displayName != null) {
            return displayName;
        }

        StringBuilder strBuilder = new StringBuilder();

        if (recipientName != null && recipientName.trim().length() > 0) strBuilder.append(recipientName);
        String addressWithoutRecipient = getDisplayAddressWithoutRecipient();
        if (addressWithoutRecipient != null && addressWithoutRecipient.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(addressWithoutRecipient);

        return strBuilder.toString();
    }

    String getDisplayAddressWithoutRecipient() {
        StringBuilder strBuilder = new StringBuilder();

        if (line1 != null && line1.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(line1);
        if (line2 != null && line2.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(line2);
        if (city != null && city.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(city);
        if (stateOrCounty != null && stateOrCounty.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(stateOrCounty);
        if (zipOrPostalCode != null && zipOrPostalCode.trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(zipOrPostalCode);
        if (country != null && country.getName().trim().length() > 0) strBuilder.append(strBuilder.length() > 0 ? ", " : "").append(country.getName());

        return strBuilder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(recipientName);
        parcel.writeString(line1);
        parcel.writeString(line2);
        parcel.writeString(city);
        parcel.writeString(stateOrCounty);
        parcel.writeString(zipOrPostalCode);
        parcel.writeString(country.getCodeAlpha2());
        parcel.writeString(addressId);
        parcel.writeString(displayName);
        parcel.writeInt(storageIdentifier);
        parcel.writeInt(searchRequiredForFullDetails ? 1 : 0);
    }

    private Address(Parcel p) {
        this.recipientName = p.readString();
        this.line1 = p.readString();
        this.line2 = p.readString();
        this.city = p.readString();
        this.stateOrCounty = p.readString();
        this.zipOrPostalCode = p.readString();
        this.country = Country.getInstance(p.readString());
        this.addressId = p.readString();
        this.displayName = p.readString();
        this.storageIdentifier = p.readInt();
        this.searchRequiredForFullDetails = p.readInt() == 1;
    }

    public static final Parcelable.Creator<Address> CREATOR
            = new Parcelable.Creator<Address>() {
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.writeObject(recipientName);
        out.writeObject(line1);
        out.writeObject(line2);
        out.writeObject(city);
        out.writeObject(stateOrCounty);
        out.writeObject(zipOrPostalCode);
        out.writeObject(country.getCodeAlpha2());
        out.writeObject(addressId);
        out.writeObject(displayName);
        out.writeInt(storageIdentifier);
        out.writeInt(searchRequiredForFullDetails ? 1 : 0);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.recipientName = (String) in.readObject();
        this.line1 = (String) in.readObject();
        this.line2 = (String) in.readObject();
        this.city = (String) in.readObject();
        this.stateOrCounty = (String) in.readObject();
        this.zipOrPostalCode = (String) in.readObject();
        this.country = Country.getInstance((String) in.readObject());
        this.addressId = (String) in.readObject();
        this.displayName = (String) in.readObject();
        this.storageIdentifier = in.readInt();
        this.searchRequiredForFullDetails = in.readInt() == 1;
    }

    /*
     * AddressBook persisting methods
     */

    public void saveToAddressBook(Context c) {
        List<Address> currentAddresses = getAddressBook(c);
        if (!isSavedInAddressBook()) {
            storageIdentifier = getNextStorageIdentifier(currentAddresses);
        }

        ArrayList<Address> updatedAddresses = new ArrayList<Address>();
        updatedAddresses.add(this);
        for (Address a : currentAddresses) {
            if (a.storageIdentifier != storageIdentifier) {
                updatedAddresses.add(a);
            }
        }

        persistAddressesToDisk(c, updatedAddresses);
    }

    public void deleteFromAddressBook(Context c) {
        if (!isSavedInAddressBook()) {
            return;
        }

        List<Address> addresses = getAddressBook(c);
        Iterator<Address> iter = addresses.iterator();
        while (iter.hasNext()) {
            Address o = iter.next();
            if (o.storageIdentifier == storageIdentifier) {
                iter.remove();
                break;
            }
        }

        persistAddressesToDisk(c, addresses);
    }

    private static void persistAddressesToDisk(Context c, List<Address> addresses) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new BufferedOutputStream(c.openFileOutput(PERSISTED_ADDRESS_BOOK_FILENAME, Context.MODE_PRIVATE)));
            os.writeObject(addresses);
        } catch (Exception ex) {
            // ignore, we'll just lose this order from the history now
        } finally {
            try {
                os.close();
            } catch (Exception ex) {/* ignore */}
        }
    }

    public boolean isSavedInAddressBook() {
        return storageIdentifier != NOT_PERSITED;
    }

    public static int getNextStorageIdentifier(List<Address> addresses) {
        int nextIdentifier = 0;
        for (int i = 0; i < addresses.size(); ++i) {
            Address o = addresses.get(i);
            if (nextIdentifier <= o.storageIdentifier) {
                nextIdentifier = o.storageIdentifier + 1;
            }
        }
        return nextIdentifier;
    }

    public static List<Address> getAddressBook(Context c) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new BufferedInputStream(c.openFileInput(PERSISTED_ADDRESS_BOOK_FILENAME)));
            ArrayList<Address> addresses = (ArrayList<Address>) is.readObject();
            return addresses;
        } catch (FileNotFoundException ex) {
            // create a brand new address book
            ArrayList<Address> addrs = new ArrayList<Address>();
            persistAddressesToDisk(c, addrs);

            return addrs;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                is.close();
            } catch (Exception ex) { /* ignore */ }
        }
    }
}
