package social.media;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class authenticator extends userHandler implements passwordMask {
    private boolean isAuthenticated;
    private final Scanner scan = new Scanner(System.in);

    // Using regex pattern to validate the email pattern
    public static boolean isValidEmail(String email) {
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // non - parameterized constructor
    public authenticator() throws IOException {
        super();
        this.isAuthenticated = false;
    }

    // parameterized constructor with Username and Password
    /*
        Sets the isAuthenticated value to true if the user is properly authenticated
    */
    public authenticator(String username, String password) throws IOException {
        super();
        this.isAuthenticated = false;
        User record = getUser(username);
        String securePassword;
        securePassword = get_SHA_256_SecurePassword(password, username);
        if (record != null) {
            if (record.getPassword().equals(securePassword)) {
                isAuthenticated = true;
            }
        } else {
            System.out.println("Your record not found!");
        }
    }

    // Destructor function to close the Scanner
    protected void finalize() {
        scan.close();
    }

    // Function to do the same as the parameterized constructor
    public void authenticate (String username, String password) {
        this.isAuthenticated = false;
        User record = getUser(username);
        if (record != null) {
            if (record.getPassword().equals(get_SHA_256_SecurePassword(password, username))) {
                isAuthenticated = true;
            }
        } else {
            System.out.println("Your record not found!");
        }
    }

    // User deletion portal
    public void deleteUser(User user) {
        System.out.println("\n\tUser deletion portal\n");
        isAuthenticated = false;
        System.out.println("Do you want to delete your account? Are you sure?");
        System.out.print("Enter your choice [ Y - Yes | Any other key - No] : ");
        String choice = scan.nextLine();
        if (choice.equals("Y") || choice.equals("y")) {
            System.out.print("Enter your password : ");
            String password = scan.nextLine();
            authenticate(user.getUsername(), password);
            if (isAuthenticated) {
                if (!deleteUserRecord(user.getUsername())) {
                    System.out.println("Unable to delete your record!");
                    scan.nextLine();
                }
                System.out.println("deleted");
            }
        }
        Main.menu();
    }

    // returns the status of the authentication to the user
    public boolean isAuthenticated() {
        return this.isAuthenticated;
    }

    // Method to log out the user
    public void logout() {
        this.isAuthenticated = false;
    }
}
