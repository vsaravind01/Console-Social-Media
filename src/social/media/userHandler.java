package social.media;
import java.io.IOException;
import java.util.TreeMap;

// userHandler class helps to
public class userHandler {
    // databaseHandler object to handle the database manipulations
    private static final databaseHandler handler = new databaseHandler("database.io");

    // TreeMap structure to store the users
    protected static TreeMap<String, User> users = new TreeMap<>();

    // Constructor to store the database into the users TreeMap
    // NOTE : This constructor is invoked only inside the Authenticator class constructor,
    //        Authenticator class extends the userHandler class
    public userHandler() throws IOException {
        //databaseHandler.serializerDB(handler.loadObjects());
        if (handler.initiateDatabase())
            
        users = handler.deserializerDB();
    }

    // Method to update the changes in the database file
    public static void updateDatabase() {
        databaseHandler.serializerDB(users);
    }

    // Method to fetch all the user records
    public static TreeMap<String, User> getUsers() {
        return users;
    }

    // Method to check if a user exists
    public static boolean userExists(String username) {
        return users.get(username) != null;
    }

    // A protected static method to delete a user from the database
    // NOTE : Only Authenticator has access to this function outside the native class
    protected static boolean deleteUserRecord(String username) {
        if (userExists(username)) {
            users.remove(username);
            updateDatabase();
            return true;
        }
        return false;
    }

    // Method to get a user from the database with a given username
    protected User getUser(String username) {
        if (users != null)
            return users.get(username);
        return null;
    }

    // Method to insert a user record into the database
    protected void insertRecord(User user) {
        users.put(user.getUsername(), user);
        if (!databaseHandler.serializerDB(users)) {
            System.out.println("Unable to insertRecord!");
        }
    }

    // Method to get the number of user records in the database
    protected int getSize() {
        if (users == null)
            return 0;
        return users.size();
    }
}
