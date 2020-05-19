package hexagon.board;

/**
 * Created by nguyen on 6/29/17.
 * This is user POJO class which holds the user's full name, username and password
 * then save this POJO into Firebase
 */



public class UserPOJO {

    private String uid;
    private String fullname;
    private String username;
    private String password;

    public UserPOJO(String uid, String fullname, String username, String password){
        this.uid = uid;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
    }

    // Getter block
    public String getUid(){
        return uid;
    }
    public String getFullname(){
        return fullname;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }

    //Setter block
    public void setUid(String uid){
        this.uid = uid;
    }
    public void setFullname(String fullname){
        this.fullname = fullname;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
