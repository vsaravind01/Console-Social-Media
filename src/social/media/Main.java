package social.media;

// Classes for user interaction
import java.io.Console;
import java.util.Scanner;

// Exception Classes
import java.io.IOException;
import java.util.InputMismatchException;

public class Main {
    protected static Scanner scan;
    protected static authenticator auth;
    protected static User user;
    protected static Console console = System.console();

    // function to run the clear console screen command
    // cls   - Windows
    // clear - Unix/Linux
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {
            System.out.println("Unable to clear screen!");
        }
        System.out.println("""
              ---------------------------------------------------------------------------------------------------------------------
                 ▄▄▄▄▄ ▄▄▄▄ ▄▄▄▄▄▄▄ ▄▄▄▄▄ ▄▄▄▄ ▄▄▄   ▄▄▄▄▄    ▄▄▄▄▄ ▄▄▄▄ ▄▄▄▄▄ ▄▄▄   ▄▄  ▄▄▄      ▄▄▄▄▄▄▄ ▄▄▄▄▄ ▄▄▄▄  ▄▄▄   ▄▄
                █─▄▄▄─█─▄▄─█▄─▀█▄─▄█─▄▄▄▄█─▄▄─█▄─▄███▄─▄▄─█  █─▄▄▄▄█─▄▄─█─▄▄▄─█▄─▄██▀▄─██▄─▄█    █▄─▀█▀─▄█▄─▄▄─█▄─▄▄▀█▄─▄██▀▄─█
                █─███▀█─██─██─█▄▀─██▄▄▄▄─█─██─██─██▀██─▄█▀█  █▄▄▄▄─█─██─█─███▀██─███─▀─███─██▀█  ██─█▄█─███─▄█▀██─██─██─███─▀─█
                ▀▄▄▄▄▄▀▄▄▄▄▀▄▄▄▀▀▄▄▀▄▄▄▄▄▀▄▄▄▄▀▄▄▄▄▄▀▄▄▄▄▄▀  ▀▄▄▄▄▄▀▄▄▄▄▀▄▄▄▄▄▀▄▄▄▀▄▄▀▄▄▀▄▄▄▄▄▀  ▀▄▄▄▀▄▄▄▀▄▄▄▄▄▀▄▄▄▄▀▀▄▄▄▀▄▄▀▄▄▀
              ---------------------------------------------------------------------------------------------------------------------
                """);
    }

    // Redirect to main page
    // based on authentication status
    private static void mainPage() {
        if (auth.isAuthenticated()) {
            new MainPage();
        } else {
            System.out.println("Invalid credentials...!");
            scan.nextLine();
            menu();
        }
    }

    // User registration portal
    public static void signUp() {
        clearScreen();
        System.out.println("\tSIGN UP\n");
        System.out.print("Username : ");
        String username = scan.nextLine();
        System.out.print("Password : ");
        char[] protectedPassword = console.readPassword();
        String password = new String(protectedPassword);
        System.out.print("Email : ");
        String email = scan.nextLine();
        System.out.print("Gender [M-Male | F-Female] : ");
        char g = scan.next().charAt(0);
        String gender = "not provided";

        // Email validation
        if (authenticator.isValidEmail(email)) {
            // Username existence check
            if (User.exists(auth.getUser(username))) {
                if (g == 'M' || g == 'm') {
                    gender = "Male";
                }
                else if (g == 'F' || g == 'f') {
                    gender = "Female";
                }
                else {
                    System.out.println("Invalid gender entered!\nPlease enter a valid gender choice(M/F)!");
                    scan.nextLine();
                    signUp();
                }
                // Final record insertion
                auth.insertRecord(new User(auth.getSize() + 1, username, email, gender, password));
                // Re-invoking authentication
                auth.authenticate(username, password);
                // Keeping track of the current user
                if (auth.isAuthenticated()) {
                    user = auth.getUser(username);
                }
                mainPage();
            }
            else {
                System.out.println("Username already exists!\nPlease enter a new username!");
                scan.nextLine();
                signUp();
            }
        }
        else {
            clearScreen();
            scan.nextLine();
            System.out.println("Invalid email address provided!\nPlease enter a valid email address!");
            scan.nextLine();
            signUp();
        }
    }

    // User login portal
    public static void login() throws IOException {
        clearScreen();
        System.out.println("\tLOGIN\n");
        System.out.print("Username : ");
        String username = scan.nextLine();
        System.out.print("Password : ");
        char[] protectedPassword = console.readPassword();
        String password = new String(protectedPassword);
        auth = new authenticator(username, password);
        if (auth.isAuthenticated()) {
            user = auth.getUser(username);
        }
        mainPage();
    }

    // main menu
    /*
        The program terminates with exit of 0
        within this method and never goes back
        to the method where it is called.
    */
    public static void menu() {
        clearScreen();
        System.out.println("\tMENU\n");
        System.out.println("1. Sign up");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.print("Enter your choice : ");
        try {
            int choice = scan.nextInt();
            scan.nextLine();
            if (choice == 1) {
                signUp();
            }
            else if (choice == 2) {
                login();
            }
            // Exit condition
            else if (choice == 3) {
                clearScreen();
                System.out.print("\nAre you sure?\nDo you want to Exit? (Y - Yes | Any other key - No) > ");
                char exit = scan.next().charAt(0);
                if (exit == 'Y' || exit == 'y') {
                    System.out.println("\n\n\n\t\t\tBye!\n\n\n");
                    System.out.print("Press enter to Exit...");
                    scan.nextLine();
                    scan.nextLine();
                    System.exit(0);
                }
            }
            else {
                System.out.println("\nPlease enter a valid choice!");
                scan.nextLine();
            }
        } catch (InputMismatchException | IOException e) {
            scan.next();
            System.out.println("\nPlease enter a valid choice!");
            scan.nextLine();
        }
        menu();
    }

    // Main method
    public static void main(String[] args) throws IOException {
        auth = new authenticator();
        scan = new Scanner(System.in);
        menu();
        scan.close();
    }
}
