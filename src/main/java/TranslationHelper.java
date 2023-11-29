import jxl.Cell;
import jxl.CellView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import nu.studer.java.util.OrderedProperties;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TranslationHelper {
    private static final String PROPERTY_FILES_PATH = "/Users/agvozdikov/Align/projects/apps-lab-portal/pluto-labportal/pluto-labportal-web/src/main/resources";
    private static final String FILE_NAME = "LabPortalTranslationTemplate_v2.xls";
    private static final String EXCEL_FILE_PATH = "/Users/agvozdikov/Align/projects/PropertiesExtractor/TPS Translations template_vietnamese_TL-1080.xls";
    private static final String SPLIT_CHAR = "/";
    private static final String FILE_EXTENSION = "properties";
    private static final String LANGUAGE = "vi_VN";
    private static final String OUTPUT_FOLDER = "target";

    private TranslationHelper() {
    }

    /**
     * Create property files from xml file with translations recieved from translation team
     */
    public static void extractTranslations() {
        List<Property> properties = extractDataFromExcel();
        Map<String, List<Property>> propertyMap = properties.stream().collect(Collectors.groupingBy(Property::getFileName));
        for (Map.Entry<String, List<Property>> entry : propertyMap.entrySet()) {
            writePropertiesToFile(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Create xml for attaching to the task to the translation team from property files
     */
    public static void extractProperties() {
        WritableWorkbook workbook = null;
        try {
            Collection<File> files = findPropertyFiles();

            workbook = createWorkbook();
            WritableSheet sheet = workbook.createSheet("Translations", 0);
            CellView cellView = new CellView();
            cellView.setAutosize(true);
            sheet.setColumnView(0, cellView);
            sheet.setColumnView(1, cellView);
            sheet.setColumnView(2, cellView);

            WritableCellFormat cellFormat = new WritableCellFormat();
            cellFormat.setBackground(Colour.GREY_25_PERCENT);

            sheet.addCell(new Label(0, 0, "String identifier", cellFormat));
            sheet.addCell(new Label(1, 0, "Text that needs translation", cellFormat));
            sheet.addCell(new Label(2, 0, "Special instructions ", cellFormat));

            int currentRow = 1;

            for (File file : files) {
                List<Property> properties = extractProperties(file);
                for (Property property : properties) {
                    sheet.addCell(new Label(0, currentRow, property.getFileName() + SPLIT_CHAR + property.getPropertyName()));
                    sheet.addCell(new Label(1, currentRow, property.getText()));
                    sheet.addCell(new Label(2, currentRow, "No special instructions"));
                    currentRow++;
                }
            }

            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static WritableWorkbook createWorkbook() {
        try {
            String path = new File(".").getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + FILE_NAME;
            File file = new File(fileLocation);
            FileUtils.deleteQuietly(file);
            return Workbook.createWorkbook(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Collection<File> findPropertyFiles() {
        return FileUtils.listFiles(new File(PROPERTY_FILES_PATH), new RegexFileFilter("^((?!application)[^_])*properties$"), TrueFileFilter.INSTANCE);
    }

    private static List<Property> extractProperties(File file) {
        try(FileInputStream fileInputStream = FileUtils.openInputStream(file)) {
            List<Property> result = new ArrayList<>();

            OrderedProperties properties = new OrderedProperties();
            properties.load(fileInputStream);

            properties.entrySet().forEach(entry -> result.add(new Property(file.getName(), (String) entry.getKey(), (String) entry.getValue())));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static List<Property> extractDataFromExcel() {
        Workbook workbook = null;
        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            workbook = Workbook.getWorkbook(new File(EXCEL_FILE_PATH), ws);
            Sheet sheet = workbook.getSheets()[0];
            int rowCount = sheet.getRows();
            List<Property> properties = new ArrayList<>();
            for (int i = 1; i < rowCount; i++) {
                Cell[] row = sheet.getRow(i);
                String propertyId = row[1].getContents();
                if (propertyId.split(SPLIT_CHAR).length < 2) {
                    System.out.println("Missed row " + i + " property length " + row[2]);
                }
                String fileName = propertyId.split(SPLIT_CHAR)[0];
                String propertyName = propertyId.split(SPLIT_CHAR)[1];
                String text = row[2].getContents();
                String translation;
                if (row.length < 5) {
                    System.out.println("Missed row " + i + " property " + row[2]);
                    translation = "";
                } else {
                    translation = row[4].getContents();
                }
                properties.add(new Property(fileName, propertyName, text, translation, LANGUAGE));
            }
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    private static List<Property> extractDataFromExcelInOrderOfOtherFile() {
        Workbook workbook = null;
        Workbook workbookWithoutTranslations = null;
        try {
            workbook = Workbook.getWorkbook(new File(EXCEL_FILE_PATH));
            workbookWithoutTranslations = Workbook.getWorkbook(new File("/Users/agvozdikov/Align/projects/PropertiesExtractor/LabPortalTranslationTemplate_v2.xls"));
            Sheet sheet = workbook.getSheets()[0];
            Sheet sheetWithoutTranslations = workbookWithoutTranslations.getSheets()[0];
            int rowCount = sheet.getRows();
            Map<String, String> translations = new HashMap<>();
            for (int i = 1; i < rowCount; i++) {
                Cell[] row = sheet.getRow(i);
                String propertyId = row[0].getContents();
                String translation = row[2].getContents();
                translations.put(propertyId, translation);
            }
            List<Property> properties = new ArrayList<>();
            for (int i = 1; i < rowCount; i++) {
                Cell[] row = sheetWithoutTranslations.getRow(i);
                String propertyId = row[0].getContents();
                String fileName = propertyId.split(SPLIT_CHAR)[0];
                String propertyName = propertyId.split(SPLIT_CHAR)[1];
                String translation = StringUtils.defaultString(translations.get(propertyId));
                properties.add(new Property(fileName, propertyName, null, translation));
            }
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (workbook != null) {
                workbook.close();
                workbookWithoutTranslations.close();
            }
        }
    }

    private static void writePropertiesToFile(String fileName, List<Property> propertyList) {
        File file = new File(getOutputPath(fileName));
        try(FileOutputStream fileOutputStream = FileUtils.openOutputStream(file)) {
            OrderedProperties properties = new OrderedProperties();
            propertyList.stream().forEach(p -> properties.setProperty(p.getPropertyName(), p.getTranslation()));
            properties.store(fileOutputStream, null);
            removeFirstLine(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String getOutputPath(String fileName) {
        String fileNameWithoutExtension = fileName.replace("." + FILE_EXTENSION, "");
        String fullFileNameWithLanguage = fileNameWithoutExtension + "_" + LANGUAGE + "." + FILE_EXTENSION;
        return OUTPUT_FOLDER + File.separator + fullFileNameWithLanguage;
    }

    private static void removeFirstLine(String fileName) {
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            //Initial write position
            long writePosition = raf.getFilePointer();
            raf.readLine();
            // Shift the next lines upwards.
            long readPosition = raf.getFilePointer();

            byte[] buff = new byte[1024];
            int n;
            while (-1 != (n = raf.read(buff))) {
                raf.seek(writePosition);
                raf.write(buff, 0, n);
                readPosition += n;
                writePosition += n;
                raf.seek(readPosition);
            }
            raf.setLength(writePosition);
            raf.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
