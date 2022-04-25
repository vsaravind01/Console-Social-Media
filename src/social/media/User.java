package social.media;
import org.apache.commons.lang3.text.WordUtils;
import java.util.*;

// User Class to manage the user record type
public class User implements passwordMask, java.io.Serializable {
    /*
        Members and it's types
        user - Dictionary <String, String> (Hashtable) (Hashtable reduces the query time significantly)
        following - TreeMap <>
    */

    private final Dictionary<String, String> user = new Hashtable<>();
    public TreeMap<String, User> following;
    private final TreeMap<Date, Post> posts;

    // User constructor to add the user record into the Dictionary data structure
    public User(long id, String username, String email, String gender, String password) {
        this.user.put("id", String.valueOf(id));
        this.user.put("username", username);
        this.user.put("password", get_SHA_256_SecurePassword(password, username));
        this.user.put("gender", gender);
        this.user.put("email", email);
        this.user.put("bio", "NULL");
        posts = new TreeMap<>();
        following = new TreeMap<>();
    }


    // Deprecated method : Used to convert a line with "," delimiter into the user records
    // previously used to convert the lines in a csv file to user records
    @Deprecated
    public User(String line) {
        String[] record = line.split(",");
        this.user.put("id", String.valueOf(record[0]));
        this.user.put("username", record[1]);
        this.user.put("password", record[2]);
        this.user.put("gender", record[3]);
        this.user.put("email", record[4]);
        this.user.put("bio", record[5]);
        posts = new TreeMap<>();
        following = new TreeMap<>();
    }

    // Returns the Following users TreeMap structure
    public TreeMap<String, User> getFollowing() {
        return this.following;
    }

    // An overloaded Method that returns the user object
    public User getFollowingUser(String username) {
        return this.following.get(username);
    }

    // Returns true if the given user is following the current user (this)
    public boolean isFollowing(String username) {
        return this.following.get(username) != null;
    }


    // Fetches the user page in a formatted String
    public String UserPage() {
        String page = "";
        page += String.format("""
                UserID   : %-25d                 Email : %s
                Username : %-25s                Gender : %-5s
                Bio      :
                %s
                ---------------------------------------------------------------------------------------------------------------------
                
                Posts (%d) :
                %s
                """,this.getId(),
                    this.getEmail(),
                    this.getUsername(),
                    this.getGender(),
                    this.getBio(),
                    this.posts.size(),
                    this.getPostsString());
        return page;
    }

    // Returns the all the posts of the user in a formatted string
    public String getPostsString() {
        StringBuilder posts = new StringBuilder();
        int id = 1;
        for (Post post : this.posts.values()) {
            posts.append(id++).append(") \n").append(post.getPostString()).append("\n-------------------------------------------------------------\n");
        }
        if (this.posts.size() > 0) {
            return posts.toString();
        }
        return "No posts to show!";
    }

    // Fetch all the posts of the user
    public TreeMap<Date, Post> getPosts() {
        return posts;
    }

    // Fetch the post with given Date Object
    public Post getPost(Date date) {
        return posts.get(date);
    }

    // Fetch the user ID
    public long getId() {
        return Long.parseLong(this.user.get("id"));
    }

    // Fetch the username
    public String getUsername() {
        return this.user.get("username");
    }

    // Fetch Email-ID of the user
    public String getEmail() {
        return this.user.get("email");
    }

    // Fetch the Gender category of the user
    public String getGender() {
        return this.user.get("gender");
    }

    // Fetch the user Password (Hashed)
    public String getPassword() {
        return this.user.get("password");
    }

    // Fetch the user's Bio
    public String getBio() {
        return WordUtils.wrap(this.user.get("bio"), 50, "\n", false);
    }

    // Fetch any Attribute from the user record
    public String getAttribute(String attribute) {
        return this.user.get(attribute);
    }

    // Method to change the bio field of the user
    public void setBio(String bio) {
        this.user.put("bio", bio);
        userHandler.updateDatabase();
    }

    // Method to change the email of the user
    public void setEmail(String email) {
        this.user.put("email", email);
        userHandler.updateDatabase();
    }

    // Method to change the password of the user
    public void setPassword(String password) {
        this.user.put("password", get_SHA_256_SecurePassword(password, this.getUsername()));
        userHandler.updateDatabase();
    }

    // method to send a post (Adds the post to the post TreeMap)
    public void sendPost(String message) {
        posts.put(new Date(), new Post(message, this));
        TreeMap<String, User> users = userHandler.getUsers();
        if (users != null)
            userHandler.updateDatabase();
        else
            System.out.println("No");
    }

    // Method to delete a post given it's posted datetime
    public void deletePost(Date date) {
        posts.remove(date);
        userHandler.updateDatabase();
    }

    // Method to follow a user, given a User object
    /*
        follow if the user (this) hasn't followed the given user yet,
        else unfollow the given user.
    */
    public void follow(User user) {
        if (this.following.get(user.getUsername()) != null) {
            this.following.remove(user.getUsername());
        }
        else  {
            this.following.put(user.getUsername(), user);
        }
        userHandler.updateDatabase();
    }

    // Deprecated Method : Format the entire user record (Except Posts and Following Info) to a CSV record
    @Deprecated
    public String formatRecord() {
        return "\n" + this.user.get("id") + "," + this.user.get("username") + "," + this.user.get("password") + "," + this.user.get("gender") + "," + this.user.get("email") + "," + this.user.get("bio");
    }

    // A static method to check if a user exists
    // NOTE : Used only in authenticator
    public static boolean exists(User user) {
        return user == null;
    }

    // Deprecated Method : Compare the current user (this) with the given User Object
    @Deprecated
    public int compareTo(User value) {
        return this.getUsername().compareTo(value.getUsername());
    }
}
