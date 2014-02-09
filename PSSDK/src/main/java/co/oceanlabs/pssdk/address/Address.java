package co.oceanlabs.pssdk.address;

/**
 * Created by deonbotha on 29/01/2014.
 */
public class Address {
    private String recipientName;
    private String line1;
    private String line2;
    private String city;
    private String stateOrCounty;
    private String zipOrPostalCode;
    private Country country;

    // Partial address fields
    private String addressId;
    private String displayName;

    static Address createPartialAddress(String addressId, String displayName) {
        Address addr = new Address();
        addr.addressId = addressId;
        addr.displayName = displayName;
        return addr;
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
}
