package tam.data;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class represents a Teaching Assistant for the table of TAs.
 * 
 * @author Richard McKenna
 * @param <E>
 */
public class TeachingAssistant<E extends Comparable<E>> implements Comparable<E>  {
    // THE TABLE WILL STORE TA NAMES AND EMAILS
    private final StringProperty name;
    private final StringProperty email;
    // Each TA will have a set of hashkeys
    private final ArrayList<String> hashKeys;

    /**
     * Constructor initializes the TA name
     * @param initName
     * @param mail
     */
    public TeachingAssistant(String initName, String mail) {
        name = new SimpleStringProperty(initName);
        email = new SimpleStringProperty(mail);
        hashKeys = new ArrayList<>();
    }

    // ACCESSORS AND MUTATORS FOR THE PROPERTIES
    public String getEmail(){
        return email.get();
    }
    
    public String getName() {
        return name.get();
    }
    
    public void setEmail(String mail){
        email.set(mail);
    }
    
    public void setName(String initName) {
        name.set(initName);
    }
    
    public ArrayList<String> getKeys(){
        return hashKeys;
    }
    
    public void addKey(String s){
        hashKeys.add(s);
    }
    
    public String removeKey(String s){
        return hashKeys.remove(hashKeys.indexOf(s));
    }

    public boolean equals(Object otherTA){
        if(otherTA == null)
            return false;
        if(getName().equals(((TeachingAssistant)otherTA).getName()) && getEmail().equals(((TeachingAssistant)otherTA).getEmail())){
            return true;
        }
        return false;
    }

    @Override 
    public int compareTo(E otherTA) {
        return getName().compareTo(((TeachingAssistant)otherTA).getName());
    }
    
    @Override
    public String toString() {
        return name.getValue();
    }
}