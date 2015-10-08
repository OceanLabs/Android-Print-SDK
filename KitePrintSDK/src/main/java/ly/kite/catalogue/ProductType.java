package ly.kite.catalogue;

/**
 * Created by deonbotha on 09/02/2014.
 */
public enum ProductType {
    POSTCARD("ps_postcard", "Postcard"),
    POLAROIDS("polaroids", "Polaroid Style Prints"),
    MINI_POLAROIDS("polaroids_mini", "Mini Polaroid Style Prints"),
    SQUARES("squares", "Square Prints"),
    MINI_SQUARES("squares_mini", "Mini Square Prints"),
    MAGNETS("magnets", "Magnets"),
    SQUARE_STICKERS("stickers_square","Square Stickers"),
    CIRCLE_STICKERS("stickers_circle","Circle Stickers"),
    GREETINGS_CARDS("greeting_cards","Circle Greeting Cards"),

    /* Frames */
    FRAMES_1X1_20CM("frames_20cm","Frame 20cm"),
    FRAMES_1X1_30CM("frames_30cm","Frame 30cm"),
    FRAMES_1X1_50CM("frames_50cm","Frame 50cm"),

    FRAMES_2x2_20CM("frames_20cm_2x2","Frame 20cm (2x2)"),
    FRAMES_2x2_30CM("frames_30cm_2x2","Frame 30cm (2x2)"),
    FRAMES_2x2_50CM("frames_50cm_2x2","Frame 50cm (2x2)"),

    FRAMES_3x3_30CM("frames_30cm_3x3","Frame 30cm (3x3)"),
    FRAMES_3x3_50CM("frames_50cm_3x3","Frame 30cm (3x3)"),

    FRAMES_4x4_50CM("frames_50cm_4x4","Frame 30cm (4x4)"),

    /* Legacy Frames */
    FRAMES_2x2("frames_2x2","2x2 Frame"),

    PHOTOS_4x6("photos_4x6","Circle Greeting Cards"),
    POSTER_A1("a1_poster","A1 Collage (54)"),
    POSTER_A1_35CM("a1_poster_35","A1 Collage (54)"),
    POSTER_A1_54CM("a1_poster_54","A1 Collage (54)"),
    POSTER_A1_70CM("a1_poster_70","A1 Collage (70)"),
    POSTER_A2("a2_poster","A1 Collage (54)"),
    POSTER_A2_35("a2_poster_35","A2 Collage (35)"),
    POSTER_A2_54("a2_poster_54","A2 Collage (54)"),
    POSTER_A2_70("a2_poster_70","A2 Collage (70)");

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    private final String defaultTemplate;
    private final String productName; // TODO: don't do this in final public API.

    private ProductType(String defaultTemplate, String productName) {
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
        } else if (template.equals(SQUARE_STICKERS.defaultTemplate)) {
            return SQUARE_STICKERS;
        } else if (template.equals(CIRCLE_STICKERS.defaultTemplate)) {
            return CIRCLE_STICKERS;
        } else if (template.equals(GREETINGS_CARDS.defaultTemplate)) {
            return GREETINGS_CARDS;
        } else if (template.equals(FRAMES_1X1_20CM.defaultTemplate)) {
            return FRAMES_1X1_20CM;
        } else if (template.equals(FRAMES_1X1_30CM.defaultTemplate)) {
            return FRAMES_1X1_30CM;
        } else if (template.equals(FRAMES_1X1_50CM.defaultTemplate)) {
            return FRAMES_1X1_50CM;
        } else if (template.equals(FRAMES_2x2_20CM.defaultTemplate)) {
            return FRAMES_2x2_20CM;
        } else if (template.equals(FRAMES_2x2_30CM.defaultTemplate)) {
            return FRAMES_2x2_30CM;
        } else if (template.equals(FRAMES_2x2_50CM.defaultTemplate)) {
            return FRAMES_2x2_50CM;
        } else if (template.equals(FRAMES_3x3_30CM.defaultTemplate)) {
            return FRAMES_3x3_30CM;
        } else if (template.equals(FRAMES_3x3_50CM.defaultTemplate)) {
            return FRAMES_3x3_50CM;
        } else if (template.equals(FRAMES_4x4_50CM.defaultTemplate)) {
            return FRAMES_4x4_50CM;
        } else if (template.equals(FRAMES_2x2.defaultTemplate)) {
            return FRAMES_2x2;
        } else if (template.equals(PHOTOS_4x6.defaultTemplate)) {
            return PHOTOS_4x6;
        } else if (template.equals(POSTER_A1.defaultTemplate)) {
            return POSTER_A1;
        } else if (template.equals(POSTER_A1_35CM.defaultTemplate)) {
            return POSTER_A1_35CM;
        } else if (template.equals(POSTER_A1_54CM.defaultTemplate)) {
            return POSTER_A1_54CM;
        } else if (template.equals(POSTER_A1_70CM.defaultTemplate)) {
            return POSTER_A1_70CM;
        } else if (template.equals(POSTER_A2.defaultTemplate)) {
            return POSTER_A2;
        } else if (template.equals(POSTER_A2_35.defaultTemplate)) {
            return POSTER_A2_35;
        } else if (template.equals(POSTER_A2_54.defaultTemplate)) {
            return POSTER_A2_54;
        } else if (template.equals(POSTER_A2_70.defaultTemplate)) {
            return POSTER_A2_70;
        }

        throw new IllegalArgumentException("Unrecognized template: " + template);
    }

    public String getProductName() {
        return productName;
    }

    @Override public String toString() {
        return getProductName();
    }
}
