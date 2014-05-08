package ly.kite.print;

/**
 * Created by deonbotha on 09/02/2014.
 */
public enum ProductType {
    POSTCARD(2, "ps_postcard", "Postcard"),
    POLAROIDS(3, "polaroids", "Polaroids"),
    MINI_POLAROIDS(4, "polaroids_mini", "Petite Polaroids"),
    SQUARES(5, "squares", "Squares"),
    MINI_SQUARES(6, "squares_mini", "Petite Squares"),
    MAGNETS(7, "magnets", "Magnets");

    final int value;
    final String defaultTemplate;
    final String productName; // TODO: don't do this in final public API.

    private ProductType(int value, String defaultTemplate, String productName) {
        this.value = value;
        this.defaultTemplate = defaultTemplate;
        this.productName = productName;
    }

    public static ProductType productTypeFromTemplate(String template) {
       if (template.equals(POSTCARD.defaultTemplate)) {
           return POSTCARD;
       } else if (template.equals(POLAROIDS.defaultTemplate)) {
           return POLAROIDS;
       } else if (template.equals(MINI_POLAROIDS.defaultTemplate)) {
           return MINI_POLAROIDS;
       } else if (template.equals(SQUARES.defaultTemplate)) {
           return SQUARES;
       } else if (template.equals(MINI_SQUARES.defaultTemplate)) {
           return MINI_SQUARES;
       } else if (template.equals(MAGNETS.defaultTemplate)) {
           return MAGNETS;
       }

        throw new IllegalArgumentException("Unrecognized template: " + template);
    }

    public String getProductName() {
        return productName;
    }
}
