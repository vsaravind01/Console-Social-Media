package social.media;
import java.util.*;

// Main page of the application
// NOTE : Has to be Authenticated first
public class MainPage extends Main {

    // A mediator method to emulate a dashboard view of a User
    protected void userPageMediator(String username) {
        User S_user = auth.getUser(username);
        clearScreen();

        // User existence check
        if (S_user != null) {
            System.out.println(S_user.UserPage());
            String message;

            // Message for follow / unfollow based on the current following status
            if (user.isFollowing(S_user.getUsername())) {
                message = "unfollow";
            }
            else {
                message = "follow";
            }
            System.out.println("[F - " + message + " | Any other key - Exit] "+ S_user.getUsername());
            System.out.print("Enter your choice > ");
            char f_ch = scan.next().charAt(0);

            // Follow / Unfollow
            if (f_ch == 'f' || f_ch == 'F') {
                user.follow(S_user);
                System.out.println(S_user.getUsername() + " " + message + " successfully!");
                userPageMediator(username);
            }
        }
        else {
            System.out.println("User not found!");
            scan.nextLine();
        }
    }

    // A mediator method to provide a dashboard view of a Post
    protected void postPageMediator(Post post) {
        // Loop until Exit
        while(true) {
            clearScreen();
            System.out.println(post.getPostDetails());
            if (post.isLiked(user.getUsername()))
                System.out.println("[L - Unlike | C - Comment | Any other key - Return]");
            else
                System.out.println("[L - Like | C - Comment | Any other key - Return]");
            System.out.print("Enter your choice > ");
            String p_choice = scan.nextLine();

            // Like the post
            if (p_choice.equals("L") || p_choice.equals("l")) {
                if (!post.isLiked(user.getUsername()))
                    post.like(user);
                else
                    post.unLike(user.getUsername());
            }

            // Commenting on the post
            else if (p_choice.equals("C") || p_choice.equals("c")) {
                System.out.print("\nEnter your comment [/exit - Exit]: ");
                String comment = scan.nextLine();
                if (comment.equals("/exit"))
                    continue;
                post.comment(user, comment);
            }
            else {
                System.out.println("\nPlease Enter to continue...");
                scan.nextLine();
                break;
            }
        }
    }

    // Display all the user records
    protected void displayUserRecords(TreeMap<String, User> users) {
        System.out.format("%-15s%-25s\n", "Index", "Username");
        int index = 0;
        for (User user : users.values()) {
            System.out.format("%-15d%-25s\n", index++, user.getUsername());
        }
    }

    // Display all the following posts of the logged-in user with a limit of posts
    protected void displayFollowingPosts(int limit) {
        TreeMap<String, User> Following = user.getFollowing();
        TreeMap<Date, Post> FollowingPosts = new TreeMap<>();

        // Following existence check
        if (Following != null) {
            while(true) {
                clearScreen();
                System.out.println("\n\t\tFollowing Posts\n");
                for (User follower : Following.values()) {
                    FollowingPosts.putAll(follower.getPosts());
                }
                // Following post existence
                if (FollowingPosts.size() == 0) {
                    System.out.println("\n\n\t\tNo posts to show!\n\n-------------------------------------------------------------");
                    scan.nextLine();
                    break;
                }

                // Storing the keys of the FollowingPosts TreeMap in a Date array
                Date[] keys = FollowingPosts.keySet().toArray(new Date[0]);
                // Containing the limit value to the maximum number of posts
                if (limit >= keys.length)
                    limit = keys.length;
                for (int i = 0; i < limit; i++) {
                    System.out.println((i+1)+") "+ FollowingPosts.get(keys[i]).getUsername() + ":");
                    System.out.println(FollowingPosts.get(keys[i]).getPostString()+"\n\n");
                }
                System.out.println("(Showing "+limit+" of "+keys.length+")");
                System.out.println("[L - Load more | 1 to "+limit+" - View Post | Any other key - Exit]");
                System.out.print("Enter your choice > ");
                String l_choice = scan.nextLine();

                // increasing the posts view limit by 10
                if(l_choice.equals("l") || l_choice.equals("L")) {
                    limit += 10;
                }
                // numerical choice to read a post in a detailed view
                else if (l_choice.chars().allMatch( Character::isDigit )) {
                    clearScreen();
                    int i_l_choice = Integer.parseInt(l_choice) - 1;
                    if (i_l_choice >= 0 && i_l_choice <= limit) {
                        Post post = FollowingPosts.get(keys[i_l_choice]);
                        postPageMediator(post);
                    }
                    else {
                        break;
                    }
                }
                else {
                    return;
                }
            }
        }
        else {
            System.out.println("\n\n\t\tNo posts to show!\n\n-------------------------------------------------------------");
            scan.nextLine();
        }
    }

    // Method to display all the posts of a User (Current status : Only this user)
    protected void displayPosts(User user) {
        int limit = 10;
        TreeMap<Date, Post> posts = user.getPosts();
        int post_size = posts.size();
        if (post_size > 0) {
            while (true) {
                clearScreen();
                System.out.println("\n\t\t"+user.getUsername()+"'s Posts\n");
                Date[] keys = posts.keySet().toArray(new Date[0]);
                if (limit >= keys.length)
                    limit = keys.length;
                for (int i = 0; i < limit; i++) {
                    System.out.println((i+1)+") "+ posts.get(keys[i]).getUsername() + ":");
                    System.out.println(posts.get(keys[i]).getPostString()+"\n-------------------------------------------------------------\n");
                }
                System.out.println("(Showing "+limit+" of "+keys.length+")");
                System.out.println("[1-"+limit+" - Delete Post | Any other key - Exit]");
                System.out.print("Enter your choice > ");
                String d_choice = scan.nextLine();

                // Option to delete the post
                if (d_choice.chars().allMatch( Character::isDigit )) {
                    int i_l_choice = Integer.parseInt(d_choice) - 1;
                    System.out.println(i_l_choice);
                    if (i_l_choice >= 0 && i_l_choice <= limit) {
                        user.deletePost(keys[i_l_choice]);
                        post_size--;
                    }
                    else {
                        System.out.println("\nPlease enter a valid choice!");
                        scan.nextLine();
                    }
                }
                else {
                    break;
                }
            }
        }
        else {
            clearScreen();
            System.out.print("\n\n\t\t\tNo posts to show!\n\n-------------------------------------------------------------");
            scan.nextLine();
        }
    }

    // Main page constructor
    // Initiates the user interaction once authenticated
    public MainPage() {
        while (true) {
            clearScreen();
            System.out.println("\n\n1) Discover People");
            System.out.println("2) Read Posts");
            System.out.println("3) Create a Post");
            System.out.println("4) Settings");
            System.out.println("5) Logout");
            System.out.print("Enter your choice > ");
            int choice;
            try {
                choice = scan.nextInt();
                scan.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("\nPlease enter a valid choice!");
                scan.nextLine();
                continue;
            }

            // Discovering other users
            if (choice == 1) {
                while (true) {
                    clearScreen();
                    System.out.println("\n\tDiscover");
                    System.out.println("1) Search people");
                    System.out.println("2) View all users");
                    System.out.println("3) Return");
                    System.out.print("Enter your choice > ");
                    int d_choice;
                    try {
                        d_choice = scan.nextInt();
                        scan.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("\nPlease enter a valid choice!");
                        scan.nextLine();
                        continue;
                    }

                    // Find a user with their username
                    if (d_choice == 1) {
                        System.out.print("Enter the username : ");
                        String s_user = scan.nextLine();
                        userPageMediator(s_user);
                    }
                    // Choose a user from the list of all users
                    else if (d_choice == 2) {
                        while(true) {
                            clearScreen();

                            // Display the user records
                            displayUserRecords(userHandler.getUsers());

                            // Getting the user-index
                            System.out.print("\nEnter a user index to view profile > ");
                            int uid;
                            try {
                                uid = scan.nextInt();
                                scan.nextLine();
                            } catch (InputMismatchException e) {
                                System.out.println("\nPlease enter a valid choice!");
                                scan.nextLine();
                                continue;
                            }

                            // Creating an array structure to fetch the user record with an Integer index
                            Set<Map.Entry<String, User>> entrySet = userHandler.getUsers().entrySet();
                            List<Map.Entry<String, User>> entryList = new ArrayList<>(entrySet);

                            if (uid < entryList.size()) {
                                // fetching the user record
                                String s_user = entryList.get(uid).getKey();

                                // Redirects to user page mediator method
                                userPageMediator(s_user);
                                break;
                            } else {
                                System.out.println("\nPlease enter a valid user id!");
                                scan.nextLine();
                            }
                        }
                        scan.nextLine();
                    }
                    else if (d_choice == 3) {
                        break;
                    }
                    else {
                        System.out.println("\nPlease enter a valid choice!");
                    }
                }
            }

            // Read the following posts
            else if (choice == 2) {
                while (true) {
                    clearScreen();
                    System.out.println("\n\t\tRead Posts");
                    System.out.println("1) Following Posts");
                    System.out.println("2) Your Posts");
                    System.out.println("3) Return");
                    System.out.print("Enter your choice > ");
                    int r_choice;
                    try {
                        r_choice = scan.nextInt();
                        scan.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("\nPlease enter a valid choice!");
                        scan.nextLine();
                        continue;
                    }

                    if (r_choice == 1) {
                        displayFollowingPosts(10);
                    }
                    else if (r_choice == 2) {
                        displayPosts(user);
                    }
                    else if (r_choice == 3) {
                        break;
                    }
                    else {
                        System.out.println("\nPlease enter a valid choice!");
                        scan.nextLine();
                    }
                }
            }

            // Write a post
            else if (choice == 3) {
                clearScreen();
                System.out.println("\n\t\tPost");
                System.out.println("\n\tPast Posts\n");
                System.out.println("\n"+user.getPostsString());
                System.out.println("\n\nEnter a new message to post : ");
                String message = scan.nextLine();
                user.sendPost(message);
            }

            // Settings page to modify Email id and Password and an option to delete the user's account
            else if (choice == 4) {
                while(true) {
                    clearScreen();
                    System.out.println("\n\t\tSettings\n");
                    System.out.println("1) Change Email id");
                    System.out.println("2) Change Password");
                    System.out.println("3) Delete Account");
                    System.out.println("4) Update Bio");
                    System.out.println("5) Return");
                    System.out.print("Enter your choice > ");
                    int set_choice;
                    try {
                        set_choice = scan.nextInt();
                        scan.nextLine();
                    } catch (InputMismatchException e) {
                        System.out.println("\nPlease enter a valid choice!");
                        scan.nextLine();
                        continue;
                    }

                    // Updating the Email id
                    if (set_choice == 1) {
                        while(true) {
                            clearScreen();
                            System.out.print("\nEnter your new Email Id [/exit - Exit]: ");
                            String newEmail = scan.nextLine();
                            if (newEmail.equals("/exit")) {
                                System.out.println("\nPress enter to continue!");
                                continue;
                            }
                            if (authenticator.isValidEmail(newEmail)) {
                                user.setEmail(newEmail);
                                System.out.println("Email successfully updated!");
                                break;
                            }
                            else {
                                System.out.println("\nPlease enter a valid email!");
                            }
                        }
                    }
                    // Updating the Password
                    else if (set_choice == 2) {
                        while (true) {
                            clearScreen();
                            System.out.print("Enter your new password [/exit - Exit]: ");
                            String newPassword = scan.nextLine();
                            if (newPassword.equals("/exit")) {
                                System.out.println("\nPress enter to continue!");
                                break;
                            }
                            System.out.print("Confirm you new password : ");
                            String confirmPassword = scan.nextLine();

                            if (!Objects.equals(newPassword, confirmPassword)) {
                                System.out.println("Confirm password didn't match");
                                scan.nextLine();
                                continue;
                            }
                            user.setPassword(newPassword);
                            System.out.println("Password successfully updated!");
                            break;
                        }
                    }
                    // Redirecting to the user deletion portal in the authenticator class
                    // NOTE : The user will be logged-out when opted to redirect to the user deletion portal
                    else if (set_choice == 3) {
                        clearScreen();
                        auth.deleteUser(user);
                    }

                    // Updating user's bio field
                    else if(set_choice == 4) {
                        clearScreen();
                        System.out.println("\n\t\tBIO\n");
                        System.out.println("Current BIO : ");
                        System.out.println(user.getBio());
                        System.out.print("\nEnter a new BIO [/exit - Exit]: ");
                        String bio = scan.nextLine();
                        if (bio.equals("/exit"))
                            continue;
                        user.setBio(bio);
                        System.out.println("\nBio successfully updated!");
                    }
                    // Exit the settings
                    else if (set_choice == 5) {
                        break;
                    }
                    else {
                        System.out.println("\nPlease enter a valid choice!");
                    }
                    scan.nextLine();
                }
            }

            // Logout condition
            else if (choice == 5) {
                auth.logout();
                break;
            }
            else {
                System.out.println("\nPlease enter a valid choice!");
                scan.nextLine();
            }
        }
    }
}
