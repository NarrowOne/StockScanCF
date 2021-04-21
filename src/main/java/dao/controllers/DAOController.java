package dao.controllers;

import dao.DAO;
import models.Produce;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class DAOController<T> {
    private final DAO dao;

    public DAOController(DAO dao){
        this.dao = dao;
    }

    public String toJson(String jsonName, Object data){
        StringBuilder res = new StringBuilder();
        int outerLoops = 1;

        if(data.getClass() == HashMap.class){
            outerLoops = ((HashMap<?, ?>) data).size();
        }else if (data.getClass() == ArrayList.class){
            outerLoops = ((ArrayList<?>) data).size();
        }else{
            Produce produce = (Produce) data;
            return "\""+produce.getId()+"\" : "+ produce.getJsonString() +"\n";
        }

        for(int i=0; i<outerLoops; i++){

            if(data.getClass() == HashMap.class){

                HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
                String[] keys = dataMap.keySet().toArray(new String[0]);
                res.append("\""+keys[i]+"\" : "+"\""+dataMap.get(keys[i])+"\"");

            }else if (data.getClass() == ArrayList.class){
                HashMap<String, Object> dataMap = (HashMap<String, Object>) ((ArrayList<?>) data).get(i);
                String[] keys = dataMap.keySet().toArray(new String[0]);
                res.append("\""+dataMap.get("id")+"\" : {\n");
                for(int j=0; j< keys.length; j++){
                    res.append("\""+keys[j]+"\" : \""+dataMap.get(keys[j])+"\"");

                    if(keys.length-j != 1)
                        res.append(",\n");
                    else
                        res.append("\n}");
                }
            }

            if(outerLoops-i != 1)
                res.append(",\n");

        }

        return "\""+jsonName+"\" : {"+res.toString()+"\n},\n";
    }

    public String create(Object object){
        String result = "\"crud_result\" : {\n";

        if (dao.createEntry(object) == 0) {
            result += "\"error\" : true,\n";
            result += "\"message\" : \"Failed to create entry\"\n";

        }else{
            result += "\"error\" : false,\n";
            result += "\"message\" : \"Data entry inserted\"\n";
        }

        result +="},\n";

        return result;
    }

    public String update(Object object){
        String result = "\"crud_result\" : {\n";

        if (dao.updateEntry(object) == 0) {
            result += "\"error\" : true,\n";
            result += "\"message\" : \"Failed to update entry\"\n";

        }else{
            result += "\"error\" : false,\n";
            result += "\"message\" : \"Data entry updated\"\n";
        }

        result +="},\n";

        return result;
    }

    public String delete(Object object){
        String result = "\"crud_result\" : {\n";

        if (dao.removeEntry(object) == 0) {
            result += "\"error\" : true,\n";
            result += "\"message\" : \"Failed to remove entry\"\n";

        }else{
            result += "\"error\" : false,\n";
            result += "\"message\" : \"Data entry removed\"\n";
        }

        result +="},\n";

        return result;
    }

    abstract T getFullTable();
    public abstract T read(@Nullable String ... details);

}
