package QA;

import java.io.Serializable;

/**
 * Класс ответа команды
 */
public class Response implements Serializable {

    private final Object[] response;

    public Response(Object... response) {
        this.response = response;
    }

    public Object[] getResponse(){
        return this.response;
    }
    public Object getResponseById(Integer id){
        return this.response[id];
    }
    public int getResponseLength(){
        return this.response.length;
    }

    @Override
    public String toString(){
        String s = "";
        for(Object i: response){
            s += i.toString() + "\n";
        }
        return s;
    }


    public boolean equals(Response obj) {
        return getResponseById(0) == obj.getResponseById(0);
    }

}
