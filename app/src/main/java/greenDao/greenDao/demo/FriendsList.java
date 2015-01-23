package greenDao.greenDao.demo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table FRIENDS_LIST.
 */
public class FriendsList {

    private Long id;
    /** Not-null value. */
    private String username;
    /** Not-null value. */
    private String friendUsername;
    /** Not-null value. */
    private String friendRegid;

    public FriendsList() {
    }

    public FriendsList(Long id) {
        this.id = id;
    }

    public FriendsList(Long id, String username, String friendUsername, String friendRegid) {
        this.id = id;
        this.username = username;
        this.friendUsername = friendUsername;
        this.friendRegid = friendRegid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUsername() {
        return username;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Not-null value. */
    public String getFriendUsername() {
        return friendUsername;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    /** Not-null value. */
    public String getFriendRegid() {
        return friendRegid;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFriendRegid(String friendRegid) {
        this.friendRegid = friendRegid;
    }

}