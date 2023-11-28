public class Message {
    private String id;
    private String locale;
    private String original;
    private String translated;

    public Message(String id, String locale, String original, String translated) {
        this.id = id;
        this.locale = locale;
        this.original = original;
        this.translated = translated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getTranslated() {
        return translated;
    }

    public void setTranslated(String translated) {
        this.translated = translated;
    }
}
