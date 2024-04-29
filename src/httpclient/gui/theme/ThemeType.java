package httpclient.gui.theme;

/**
 * Theme types to show in options dropdown
 */
public enum ThemeType {
    /**
     * light theme
     */
    light("Light Theme", 1),
    /**
     * dark theme
     */
    dark("Dark Theme", 2);

    /**
     * theme title
     */
    private String title;
    /**
     * theme code
     */
    private int code;

    /**
     * Theme type constructor
     *
     * @param title theme title
     * @param code  theme code
     */
    ThemeType(String title, int code) {
        this.title = title;
        this.code = code;
    }

    /**
     * Gets theme code.
     *
     * @return theme code
     */
    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return title;
    }
}
