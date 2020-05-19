package hexagon.board;

/**
 * Created by nguyen on 7/1/17.
 */

public class CommentPOJO {

    private String uid;
    private String comment;

    public CommentPOJO(String uid, String comment){
        this.uid = uid;
        this.comment = comment;
    }

    public String getUid() { return uid; }
    public String getComment() { return comment; }

    public void setUid(String uid) { this.uid = uid; }
    public void setComment(String comment) { this.comment = comment; }
}
