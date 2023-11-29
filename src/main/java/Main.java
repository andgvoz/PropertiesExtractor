public class Main {
    private final static String XML_INPUT_PATH = "/Users/agvozdikov/Align/projects/PropertiesExtractor/TPS Translations template_vietnamese_TL-1080.xls";
    private final static String SQL_OUTPUT_PATH = "20231129173000__vn_translations.sql";

    public static void main(String[] args) {
        DBHelper.writePropertiesToSql(TranslationHelper.extractDataFromExcel(), SQL_OUTPUT_PATH);
        //TranslationHelper.extractTranslations();
    }
}

