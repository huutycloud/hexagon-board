package hexagon.board;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nguyen on 6/30/17.
 */

public class AlertPOJO {

    private String uid;
    private String subject;
    private String content;
    private LatLng latLng;

    public AlertPOJO(String uid, String subject, String content, LatLng latLng) {
        this.uid = uid;
        this.subject = subject;
        this.content = content;
        this.latLng = latLng;
    }

    // Getter block
    public String getUid(){
        return uid;
    }

    //Setter block
    public void setUid(String uid){
        this.uid = uid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject){
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

}
