package com.koddev.authenticatorapp.users;

public class ModelUser {

    String name, email, search, gender, image, cover, uid, age,birthday,constellation,position,mailbox;

    public ModelUser(){
    }

    public ModelUser(String name, String email, String search, String gender, String image, String cover, String uid,String age,String birthday,
                     String constellation ,  String position,String mailbox){
        this.name = name;
        this.email = email;
        this.search = search;
        this.gender = gender;
        this.image = image;
        this.cover = cover;
        this.uid = uid;
        this.age= age;
        this.birthday=birthday;
        this.constellation=constellation;
        this.position=position;
        this.mailbox=mailbox;
    }
     public String getName() {
        return name;
     }

     public void setName(String name){
        this.name = name;
     }

     public String getEmail(){
        return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

     public String getSearch(){
        return search;
     }

     public void  setSearch(String search){
        this.search = search;
     }

     public String getGender(){
        return gender;
     }

     public void  setGender(String gender){
        this.gender = gender;
     }

     public String getImage(){
        return image;
     }

     public void setImage(String image){
        this.image = image;
     }

     public String getCover() {
        return cover;
    }

    public void  setCover(String cover){
        this.cover = cover;
    }

    public String getUid() {
        return uid;
    }

    public void  setUid(String uid){
        this.uid = uid;
    }



    public String getAge() {
        return age;
    }

    public void  setAge(String age){
        this.age = age;
    }


    public String getBirthday() {
        return birthday;
    }

    public void  setBirthday(String birthday){
        this.birthday = birthday;
    }


    public String getConstellation() {
        return constellation;
    }

    public void  setConstellation(String constellation){
        this.constellation = constellation;
    }

    public String getPosition() {
        return position ;
    }

    public void  setPosition(String constellation){
        this.position = position;
    }


    public String getMailbox() {
        return mailbox ;
    }

    public void  setMailbox(String mailbox){
        this.mailbox = mailbox;
    }


}
