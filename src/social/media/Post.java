package social.media;
import org.apache.commons.lang3.text.WordUtils;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

// Post Class to maintain the Post Records type
public class Post implements Serializable {

    /*
        Members and it's types

        postMessage - String
        Comments - Map <User, String> (HashMap) (Map reduces the query time significantly)
        Likes - Map <String, User> (HashMap)
        username - String
        timePosted - Date
    */
    private final String postMessage;
    private final Map<User, String> Comments;
    private final Map<String, User> Likes;
    private final String username;
    private final Date timePosted;

    // Every post message is sent via constructor
    public Post(String message, User user) {
        this.postMessage = message;
        this.Comments = new HashMap<>();
        this.Likes = new HashMap<>();
        this.timePosted = new Date();
        this.username = user.getUsername();
    }

    // Method that returns the post info as a List Object is the format
    // <Post message, Comments, Number of likes>
    public List<Object> getPostInfo() {
        return Arrays.asList(this.postMessage, this.Comments, Likes.size());
    }

    // Returns the username of the user who posted the message
    public String getUsername() {
        return this.username;
    }

    // returns the Post in a formatted string
    /*
        The WordUtils class of Apache Commons Lang3 Text API
        provides with a wrap method, that can format any String
        with a specified wrap length (Characters).
    */
    public String getPostString() {
        return  WordUtils.wrap(this.postMessage, 50, "\n", false) + "\n\n"
                + "Likes : "
                + Likes.size();
    }

    // Returns post in a detailed String format
    public String getPostDetails() {
        return "" + String.format("""
                username : %s              Time Posted : %s
                                
                Message :
                    %s

                Comments :
                %s
                """, this.username, getDate(), getPostString(), getComments());
    }

    // Function to get all comments in String format
    public String getComments() {
        StringBuilder comments = new StringBuilder();
        int id = 1;
        for (Map.Entry<User, String> comment : this.Comments.entrySet()) {
            comments.append(id).append(") ").append(comment.getKey().getUsername()).append(": \n").append(WordUtils.wrap(comment.getValue(), 50, "\n", false)).append("\n-------------------------------------------------------------\n");
        }
        if (comments.length() == 0)
            return "No comments so far!";
        return comments.toString();
    }

    // Get the Date and time of the post when it's posted
    public String getDate() {
        SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy | HH:mm");
        return dateformat.format(this.timePosted);
    }

    // method to add a comment to the post given User Object and message String
    public void comment(User user, String message) {
        Comments.put(user, message);
        userHandler.updateDatabase();
    }

    // method to add a like to the post given the user object
    public void like(User user) {
        if (!isLiked(user.getUsername())) {
            Likes.put(user.getUsername(), user);
            userHandler.updateDatabase();
        }
    }

    // method to remove a like from the post given a username
    public void unLike(String username) {
        if (isLiked(username)) {
            Likes.remove(username);
            userHandler.updateDatabase();
        }
    }

    // Alternate method to get unlike with User object
    public void unLike(User user) {
        Likes.remove(user.getUsername());
        userHandler.updateDatabase();
    }

    // Returns a boolean value true if the user liked the post else returns false
    public boolean isLiked(String username) {
        return Likes.get(username) != null;
    }

    // Fetches the Likes <Map> Structure
    public Map<String, User> getLikes() {
        return Likes;
    }
}
