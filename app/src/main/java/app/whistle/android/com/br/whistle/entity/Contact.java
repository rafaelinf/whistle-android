package app.whistle.android.com.br.whistle.entity;

import java.util.Date;

/**
 * Created by rafael on 08/03/2016.
 */
public class Contact {

    public static final String[] COLUMNS = new String[] { "_id", "name", "number", "lat", "lng", "sharelocation", "allowtrace", "status", "urlimage", "dtupdate", "dtcreate", "version", "contactidphone"};
    public static final String TABLE_NAME = "contact";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String CREATE_TABLE = "create table contact (" +
            "_id integer primary key autoincrement, " +
            "name text not null," +
            "number text not null, " +
            "lat text, " +
            "lng text, " +
            "sharelocation NUMERIC, " +
            "allowtrace NUMERIC, " +
            "status NUMERIC, " +
            "urlimage text, " +
            "dtupdate NUMERIC, " +
            "dtcreate NUMERIC," +
            "version NUMERIC," +
            "contactidphone text);";

    private int id;
    private String name;
    private String number;
    private String lat;
    private String lng;
    private boolean sharelocation;
    private boolean allowtrace;
    private int status;
    private String urlimage;
    private Date dtupdate;
    private Date dtcreate;
    private int version;
    private String contactidphone;

    public Contact(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public boolean isSharelocation() {
        return sharelocation;
    }

    public void setSharelocation(boolean sharelocation) {
        this.sharelocation = sharelocation;
    }

    public boolean isAllowtrace() {
        return allowtrace;
    }

    public void setAllowtrace(boolean allowtrace) {
        this.allowtrace = allowtrace;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDtupdate() {
        return dtupdate;
    }

    public void setDtupdate(Date dtupdate) {
        this.dtupdate = dtupdate;
    }

    public Date getDtcreate() {
        return dtcreate;
    }

    public void setDtcreate(Date dtcreate) {
        this.dtcreate = dtcreate;
    }

    public String getUrlimage() {
        return urlimage;
    }

    public void setUrlimage(String urlimage) {
        this.urlimage = urlimage;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getContactidphone() {
        return contactidphone;
    }

    public void setContactidphone(String contactidphone) {
        this.contactidphone = contactidphone;
    }
}
