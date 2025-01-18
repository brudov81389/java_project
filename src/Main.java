import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

// Add
// 1. Po kazdej rozgrywce zmiana, trudnosci
// 2. Przywilej dla lidera w rozgrywce wieloosobowej
// 3. Pryzwilej dla mistrza, inne dla rozgrywki turniejowej i inne dla nie turniejowej

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to the Number Guessing Game!");
        boolean isRun = true;
        Menu menu = new Menu();
        while(isRun)
        {
            isRun = menu.show();
        }
        StatOne.getInstance().save();
        System.out.println("Thank you for playing! See you again");
    }
}