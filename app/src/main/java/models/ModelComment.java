package models;

import java.lang.reflect.Constructor;

public class ModelComment {
    String cId, comment, timestamp, uid, uEmail, uDp, uName;

    public ModelComment(){

    }

    public ModelComment(String cId, String comment, String timestamp, String uid, String uEmail, String uDp, String uName){
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getcId(){
        return cId;
    }

    public void setcId(String cId){
        this.cId = cId;
    }

    public String getcomment(){
        return comment;
    }

    public void setcomment(String comment){
        this.comment = comment;
    }

    public String gettimestamp(){
        return timestamp;
    }

    public void settimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getuid(){
        return uid;
    }

    public void setuid(String uid){
        this.uid = uid;
    }

    public String getuEmail(){
        return uEmail;
    }

    public void setuEmail(String uEmail){
        this.uEmail = uEmail;
    }

    public String getuDp(){
        return uDp;
    }

    public void setuDp(String uDp){
        this.uDp = uDp;
    }

    public String getuName(){
        return uName;
    }

    public void setuName(String uName){
        this.uName = uName;
    }

}
