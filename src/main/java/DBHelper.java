import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {
    private DBHelper() {
    }

    public static void convertXmlToSql(String xmlInputPath, String sqlOutputPath) {
        writeMessagesToSql(extractMessagesFromXml(xmlInputPath), sqlOutputPath);
    }

    private static List<Message> extractMessagesFromXml(String xmlInputPath) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File(xmlInputPath));
            NodeList messageNodes = document.getElementsByTagName("message");
            List<Message> messages = new ArrayList<>();
            for (int i = 0; i < messageNodes.getLength(); i++) {
                Element messageNode = (Element) messageNodes.item(i);
                messages.add(new Message(
                        messageNode.getAttribute("id"),
                        messageNode.getAttribute("locale"),
                        messageNode.getElementsByTagName("original").item(0).getTextContent(),
                        messageNode.getElementsByTagName("translated").item(0).getTextContent()
                ));
            }
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void writeMessagesToSql(List<Message> messages, String sqlOutputPath) {
        try (FileWriter fileWriter = new FileWriter(sqlOutputPath);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            for (Message message : messages) {
                printWriter.printf("select upsert_i18n_messages('%s', '%s', '%s', '%s');\n",
                        message.getId(), message.getLocale(), message.getOriginal(), message.getTranslated());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
