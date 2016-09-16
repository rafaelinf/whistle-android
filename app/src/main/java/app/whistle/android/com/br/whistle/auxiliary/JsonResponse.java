package app.whistle.android.com.br.whistle.auxiliary;

import java.util.List;

/**
 * Created by rafael on 30/08/2016.
 */
public class JsonResponse{

    private int status;
    private String jsonString;

    public JsonResponse(){
    }

    public JsonResponse(int status, String jsonString){
        this.status = status;
        this.jsonString = jsonString;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
