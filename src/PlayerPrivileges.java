import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;

public class PlayerPrivileges {
    private static final String CHAMPIONS_FILE = "champions.xml";
    private static final String LEADERS_FILE = "leaders.xml";
    private final static ArrayList<String> champions = new ArrayList<>();
    private final static ArrayList<String> leaders = new ArrayList<>();


    // Add player to the champions list
    public static void grantChampion(String username) {
        champions.add(username);
        saveChampions();
    }

    // Remove status of champion
    public static void revokeChampion(String username) {
        champions.remove(username);
        saveChampions();
    }

    // Check player is champion or not
    public static boolean isChampion(String username) {
        return champions.contains(username);
    }

    // Add player to leader list
    public static void grantLeader(String username) {
        leaders.add(username);
        saveLeaders();
    }

    // Remove player from leader list
    public static void revokeLeader(String username) {
        leaders.remove(username);
        saveLeaders();
    }

    // Check if player is leader
    public static boolean isLeader(String username) {
        return leaders.contains(username);
    }

    // Save leaders to file leaders.xml
    private static void saveLeaders() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("leaders");
            document.appendChild(root);

            for (String entry : leaders) {
                    Element leader = document.createElement("leader");
                    leader.setTextContent(entry);
                    root.appendChild(leader);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(LEADERS_FILE));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

        } catch (Exception e) {
            System.err.println("Error saving leaders: " + e.getMessage());
        }
    }

    // Read leaders list from leaders.xml
    public static void loadLeaders() {
        try {
            File file = new File(LEADERS_FILE);
            if (!file.exists()) {
                return; // File does not exist
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            NodeList leaderNodes = document.getElementsByTagName("leader");
            for (int i = 0; i < leaderNodes.getLength(); i++) {
                Node node = leaderNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String username = node.getTextContent();
                    leaders.add(username);
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading leaders: " + e.getMessage());
        }
    }

    // Save champions to file champions.xml
    private static void saveChampions() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();

            Element root = document.createElement("champions");
            document.appendChild(root);

            for (String entry : champions) {
                Element champion = document.createElement("champion");
                champion.setTextContent(entry);
                root.appendChild(champion);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(CHAMPIONS_FILE));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);

        } catch (Exception e) {
            System.err.println("Error saving champions: " + e.getMessage());
        }
    }

    // Read champions from champions.xml
    public static void loadChampions() {
        try {
            File file = new File(CHAMPIONS_FILE);
            if (!file.exists()) {
                return; // File does not exist
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            NodeList championNodes = document.getElementsByTagName("champion");
            for (int i = 0; i < championNodes.getLength(); i++) {
                Node node = championNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String username = node.getTextContent();
                    champions.add(username);
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading champions: " + e.getMessage());
        }
    }
}
