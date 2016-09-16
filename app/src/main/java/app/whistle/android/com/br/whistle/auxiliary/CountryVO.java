package app.whistle.android.com.br.whistle.auxiliary;

/**
 * Created by rafael on 27/07/2016.
 */
public class CountryVO {

    private String code;
    private String name;

    public CountryVO(){
    }

    public CountryVO(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
