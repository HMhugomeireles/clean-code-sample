package business;

public class WebBrowser {
    public final BrowserName name;

    public final int majorVersion;

    public WebBrowser(String name, int majorVersion) {
        this.name = translateStringToBrowserName(name);
        this.majorVersion = majorVersion;
    }

    private BrowserName translateStringToBrowserName(String name) {
        if (name.contains("IE")) {
            return BrowserName.INTERNET_EXPLORER;
        }
        //TODO: Add more logic for properly sniffing for other browsers.
        return BrowserName.UNKNOWN;
    }

    public enum BrowserName {
        CHROME,
        DOLPHIN,
        FIREFOX,
        INTERNET_EXPLORER,
        KONQUEROR,
        LINX,
        OPERA,
        SAFARI,
        UNKNOWN

    }
}