public class Main {
    private final static String XML_INPUT_PATH = "/Users/agvozdikov/Align/projects/ids/pluto-translations/src/main/resources/i18n/ja_JP/073-Pu9.0.0-ja_JP.xml";
    private final static String SQL_OUTPUT_PATH = "2022.7.1.20220620130000__password_reset_tab_lab_portal_translation_script.sql";

    public static void main(String[] args) {
        //DBHelper.convertXmlToSql(XML_INPUT_PATH, SQL_OUTPUT_PATH);
        TranslationHelper.extractTranslations();
    }


}

