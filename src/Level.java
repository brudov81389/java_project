import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;


public class Level {

    private static Hashtable<String, Level> levels = new Hashtable<String, Level>();
    private static final String LEVELS_FILE = "levels.xml";

    private final int begin;
    private final int end;
    private final String nameLevel;

    Level(int begin, int end, String name) {
        this.begin = begin;
        this.end = end;
        this.nameLevel = name;
    }

    public int getEnd() {
        return end;
    }

    public int getBegin() {
        return begin;
    }

    public String getNameLevel() {
        return nameLevel;
    }

    public static void addLevel(int begin, int end, String name)
    {
        Level newLevel = new Level(begin, end, name);
        levels.put(name, newLevel);

        saveLevelToXML(newLevel);
    }

    public static void initLevels(){
        levels = new Hashtable<>();
        levels.put("EASY", new Level(0, 100, "EASY"));
        levels.put("MEDIUM", new Level(0, 10000, "MEDIUM"));
        levels.put("HARD", new Level(0, 1000000, "HARD"));

        loadLevelsFromXML();
    }

    public static Collection<Level> getLevels() {
        return levels.values();
    }

    public static Level getLevel(String name){
        return levels.getOrDefault(name, new Level(0, 100, "Easy"));
    }

    private static void saveLevelToXML(Level level) {
        try {
            File file = new File(LEVELS_FILE);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc;

            // If file is exist read data from file, another way create a new file
            if (file.exists()) {
                doc = builder.parse(file);
            } else {
                doc = builder.newDocument();
                Element root = doc.createElement("levels");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();

            // Add new level to XML
            Element levelElement = doc.createElement("level");
            levelElement.setAttribute("name", level.getNameLevel());

            Element beginElement = doc.createElement("begin");
            beginElement.setTextContent(String.valueOf(level.getBegin()));
            levelElement.appendChild(beginElement);

            Element endElement = doc.createElement("end");
            endElement.setTextContent(String.valueOf(level.getEnd()));
            levelElement.appendChild(endElement);

            root.appendChild(levelElement);

            // Save to levels.xml
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);
            transformer.transform(source, result);

        } catch (Exception e) {
            System.err.println("Error saving level to XML: " + e.getMessage());
        }
    }

    private static void loadLevelsFromXML() {
        try {
            File file = new File(LEVELS_FILE);
            if (!file.exists()) {
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList levelNodes = doc.getElementsByTagName("level");
            for (int i = 0; i < levelNodes.getLength(); i++) {
                Node levelNode = levelNodes.item(i);
                if (levelNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element levelElement = (Element) levelNode;
                    String name = levelElement.getAttribute("name");
                    int begin = Integer.parseInt(levelElement.getElementsByTagName("begin").item(0).getTextContent());
                    int end = Integer.parseInt(levelElement.getElementsByTagName("end").item(0).getTextContent());
                    levels.put(name, new Level(begin, end, name));
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading levels from XML: " + e.getMessage());
        }
    }

    public static Level chooseLevel() {
        System.out.println("Choose a Level:");
        List<Level> levels = Level.levels.values().stream().toList();


        for(int i = 0; i < levels.size(); i++)
        {
            Level level = levels.get(i);
            System.out.printf("%d. %s (%d - %d).\n", i + 1, level.getNameLevel(), level.getBegin(), level.getEnd());
        }
        Scanner scan = new Scanner(System.in);
        int choice = scan.nextInt() - 1;
        try{
            Level lvl = levels.get(choice);
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("Invalid level! Set by default Easy (0 - 100).");
            return Level.getLevel("Easy");
        }

        return levels.get(choice);
    }
}