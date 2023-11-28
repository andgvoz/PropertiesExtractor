public class Property {
    private String fileName;
    private String propertyName;
    private String text;
    private String translation;
    private String limitations;

    public Property(String fileName, String propertyName, String text) {
        this.fileName = fileName;
        this.propertyName = propertyName;
        this.text = text;
    }

    public Property(String fileName, String propertyName, String text, String translation) {
        this.fileName = fileName;
        this.propertyName = propertyName;
        this.text = text;
        this.translation = translation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getLimitations() {
        return limitations;
    }

    public void setLimitations(String limitations) {
        this.limitations = limitations;
    }
}
