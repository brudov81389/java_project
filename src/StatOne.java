import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Hashtable;
import java.util.Set;

// Singleton pattern
public final class StatOne {
    private static StatOne instance;
    private static final String RECORDS_FOLDER = "records";
    static {
        File folder = new File(RECORDS_FOLDER);
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Folder 'records' created.");
            }
        }
    }

    private StatOne() {

    }

    public static StatOne getInstance() {
        if (instance == null) {
            instance = new StatOne();
        }
        return instance;
    }

    // Inner struct for data in single player
    // Contains best scores in differ levels of difficult
    public static class SingleStat{

        private final Hashtable<String, Integer> scores = new Hashtable<>();

        public int getBestScore(String diff){
            return scores.getOrDefault(diff, -1);
        }

        public void setBestScore(String diff, int b_score){
            scores.put(diff, b_score);
        }

        public Set<String> getDiffs() {
            return scores.keySet();
        }
    }

    // Inner class for data in multiplayer wins/lose
    public static class MultiStat{
        private int amountWin;
        private int amountLose;

        // Default constructor
        public MultiStat()
        {
            this.amountWin = 0;
            this.amountLose = 0;
        }

        public MultiStat(int amountWin, int amountLose){
            this.amountWin = amountWin;
            this.amountLose = amountLose;
        }

        public int getAmountWin(){
            return this.amountWin;
        }
        public int getAmountLose(){
            return this.amountLose;
        }

        public void incrementWins(){
            this.amountWin++;
        }

        public void incrementLose(){
            this.amountLose++;
        }
    }

    private final Hashtable<String, SingleStat> singleStats = new Hashtable<>();
    private final Hashtable<String, MultiStat> multiStats = new Hashtable<>();

    public void readStat(String username) {
        addPlayer(username);
        try {
            File file = new File("records/" + username + ".xml");
            if (!file.exists()) {
                System.out.println("No statistics found for user: " + username);
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            // Parse single player stats
            SingleStat singleStat = new SingleStat();
            NodeList singleList = doc.getElementsByTagName("difficulty");
            for (int i = 0; i < singleList.getLength() - 1; i++) {
                Element element = (Element) singleList.item(i);
                String level = element.getAttribute("level");
                int score = Integer.parseInt(element.getTextContent());
                singleStat.setBestScore(level, score);
            }
            singleStats.replace(username, singleStat);


            // Parse multiplayer stats
            MultiStat multiStat = new MultiStat();
            Element multiplayer = (Element) doc.getElementsByTagName("multiplayer").item(0);
            multiStat.amountWin = Integer.parseInt(multiplayer.getElementsByTagName("win").item(0).getTextContent());
            multiStat.amountLose = Integer.parseInt(multiplayer.getElementsByTagName("lose").item(0).getTextContent());
            multiStats.replace(username, multiStat);

            System.out.println("Statistics loaded for user: " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeStat(String username, String diff, int b_score){
        singleStats.get(username).setBestScore(diff, b_score);
    }

    public int getBScore(String username, String diff){
        return singleStats.get(username).getBestScore(diff);
    }

    public int getWins(String username){
        return multiStats.get(username).getAmountWin();
    }

    public int getLoses(String username){
        return multiStats.get(username).getAmountLose();
    }

    public void incrementWins(String username) {
        multiStats.get(username).incrementWins();
    }

    public void incrementLose(String username){
        multiStats.get(username).incrementLose();
    }

    public void save() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Iterates for each user
            for (String username : singleStats.keySet()) {
                // Create new document
                Document doc = builder.newDocument();

                // Create root element
                Element root = doc.createElement("player");
                doc.appendChild(root);

                // Add single player section to doc
                Element single = doc.createElement("single");
                root.appendChild(single);
                SingleStat singleStat = singleStats.get(username);
                if (singleStat != null) {
                    for (String difficulty : singleStat.getDiffs()) {

                        Element difficultyElement = doc.createElement("difficulty");
                        difficultyElement.setAttribute("level", difficulty);
                        difficultyElement.setTextContent(String.valueOf(singleStat.getBestScore(difficulty)));
                        single.appendChild(difficultyElement);
                    }
                }

                // Append multiplayer section to doc
                if(!multiStats.isEmpty()) {
                    Element multiplayer = doc.createElement("multiplayer");
                    root.appendChild(multiplayer);
                    MultiStat multiStat = multiStats.get(username);
                    if (multiStat != null) {
                        Element wins = doc.createElement("win");
                        wins.setTextContent(String.valueOf(multiStat.getAmountWin()));
                        multiplayer.appendChild(wins);

                        Element loses = doc.createElement("lose");
                        loses.setTextContent(String.valueOf(multiStat.getAmountLose()));
                        multiplayer.appendChild(loses);
                    }
                }

                // Write to XML file
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("records/" + username + ".xml"));
                transformer.transform(source, result);
            }

            System.out.println("Statistics saved for all users.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(String username)
    {
        SingleStat single = new SingleStat();

        single.setBestScore("Easy", -1);
        single.setBestScore("Medium", -1);
        single.setBestScore("Hard", -1);

        this.singleStats.put(username, single);

        MultiStat multi = new MultiStat();
        this.multiStats.put(username, multi);
    }
}
