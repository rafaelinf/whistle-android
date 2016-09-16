package app.whistle.android.com.br.whistle.entity;

import java.util.Date;

/**
 * Created by rafael on 02/12/2015.
 */
public class User {

    public static final String[] COLUMNS = new String[] { "_id", "identification", "codecountry", "prefix", "number", "name", "email", "statusmsg", "status", "dtcreate", "dtlastlogin", "registrationcode", "urlImageProfile", "dtactive"};
    public static final String TABLE_NAME = "user";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String CREATE_TABLE = "create table user (" +
            "_id integer primary key autoincrement, " +
            "identification text not null," +
            "codecountry text, " +
            "prefix text not null, " +
            "number text not null, " +
            "name text not null, " +
            "email text, " +
            "statusmsg text, " +
            "status integer, " +
            "dtcreate NUMERIC, " +
            "dtlastlogin NUMERIC, " +
            "registrationcode text not null, " +
            "urlImageProfile text, " +
            "dtactive NUMERIC);";

    private int id;
    private String identification;
    private String codecountry;
    private String prefix;
    private String number;
    private String name;
    private String email;
    private String statusmsg;
    private int status;
    private Date dtcreate;
    private Date lastlogin;
    private String registrationcode;
    private String urlImageProfile;
    private Date dtactive;

    public User(){
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatusmsg() {
        return statusmsg;
    }

    public void setStatusmsg(String statusmsg) {
        this.statusmsg = statusmsg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDtcreate() {
        return dtcreate;
    }

    public void setDtcreate(Date dtcreate) {
        this.dtcreate = dtcreate;
    }

    public Date getLastlogin() {
        return lastlogin;
    }

    public void setLastlogin(Date lastlogin) {
        this.lastlogin = lastlogin;
    }

    public String getRegistrationcode() {
        return registrationcode;
    }

    public void setRegistrationcode(String registrationcode) {
        this.registrationcode = registrationcode;
    }

    public Date getDtactive() {
        return dtactive;
    }

    public void setDtactive(Date dtactive) {
        this.dtactive = dtactive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrlImageProfile() {
        return urlImageProfile;
    }

    public void setUrlImageProfile(String urlImageProfile) {
        this.urlImageProfile = urlImageProfile;
    }

    public String getCodecountry() {
        return codecountry;
    }

    public void setCodecountry(String codecountry) {
        this.codecountry = codecountry;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
