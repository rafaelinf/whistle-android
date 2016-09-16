package app.whistle.android.com.br.whistle.auxiliary;

/**
 * Created by rafael on 18/04/2016.
 */
public class ContactMobileVO {

    private String id;
    private String name;
    private String number;

    public ContactMobileVO(){

    }

    public ContactMobileVO(String id, String name, String number){
        this.id = id;
        this.name = name;
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
}
