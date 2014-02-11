package co.oceanlabs.pssdk;

/**
 * Created by deonbotha on 09/02/2014.
 */
public enum ProductType {
    POSTCARD(2, "ps_postcard"),
    POLAROIDS(3, "polaroids"),
    MINI_POLAROIDS(4, "polaroids_mini"),
    SQUARES(5, "squares"),
    MINI_SQUARES(6, "squares_mini"),
    MAGNETS(7, "magnets");

    final int value;
    final String defaultTemplate;

    private ProductType(int value, String defaultTemplate) {
        this.value = value;
        this.defaultTemplate = defaultTemplate;
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
}
