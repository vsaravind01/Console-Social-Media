package social.media;

// Java I/O and Utilities Classes
import java.io.*;
import java.util.*;

// File handling Classes
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class databaseHandler {
    private static String databaseName;
    private final File database;
    private final Path databasePath;

    // parameterized constructor with User file name and Post file name
    // instantiates the User database File object and Posts database File Object
    public databaseHandler(String dbName) {
        databaseName = dbName;
        this.database = new File(dbName);
        this.databasePath = Paths.get(databaseName);
    }

    // returns User database file Path Object
    public Path getUserFilePath() {
        return databasePath;
    }

    // returns the number of lines in the User database
    public long numberOfLines(Path path) {
        long lines = 0;
        try {
            lines = Files.lines(path).count();
        } catch (IOException e) {
            System.out.println("An IO exception occurred!");
            e.printStackTrace();
        }
        return lines;
    }

    // Creates the User database file if it does not exist
    public boolean initiateDatabase() {
        boolean result = database.exists();
        if (!result) {
            try {
                result = database.createNewFile();
            } catch (IOException e) {
                System.out.println("An unknown error occurred!");
                e.printStackTrace();
            }
        }
        return result;
    }

    // Checks if the database is empty
    // NOTE : The buffer reader may throw an IOException
    public static boolean isDatabaseEmpty() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(databaseName));
        boolean status = br.readLine() == null;
        br.close();
        return status;
    }

    // Method to serialize the database objects (as a TreeMap Structure) and storing it in the database
    public static boolean serializerDB(TreeMap<String, User> users) {
        if (users != null) {
            try {
                FileOutputStream file = new FileOutputStream(databaseName);
                ObjectOutputStream out = new ObjectOutputStream(file);
                out.writeObject(users);
                out.close();
                file.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    // Method to deserialize database file to TreeMap objects
    public TreeMap<String, User> deserializerDB() throws IOException {
        if (!isDatabaseEmpty()) {
            try {
                FileInputStream file = new FileInputStream(databaseName);
                ObjectInputStream in = new ObjectInputStream(file);

                return (TreeMap<String, User>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return new TreeMap<>();
    }

    // writes all the records to the User database in CSV format
    @Deprecated
    public boolean writeToDb(TreeMap<String, User> users) {
        if (database.canWrite()) {
            try {
                FileWriter writer = new FileWriter(databaseName);
                writer.write("id,username,password,gender,email,bio");
                for (User user : users.values()) {
                    writer.write(user.formatRecord());
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("An IO error has been occurred!");
                e.printStackTrace();
            }
        }
        return database.canWrite();
    }

    // Inserts a record into the User database as CSV record
    @Deprecated
    public boolean insertRecord(User user) {
        if (database.canWrite()) {
            try {
                Files.write(this.databasePath, recordManipulator.stringify(user).getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("An IO error has been occurred!");
                e.printStackTrace();
            }
        }
        return database.canWrite();
    }


    // Reads the database in CSV format and converts it to TreeMap structure
    @Deprecated
    public TreeMap<String, User> loadObjects() {
        TreeMap<String, User> users = new TreeMap<>();
        User user;
        String line;
        try {
            BufferedReader br = new BufferedReader(new FileReader("users.csv"));
            br.readLine();
            while ((line = br.readLine()) != null) {
                user = new User(line);
                users.put(user.getUsername(), new User(line));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
}
