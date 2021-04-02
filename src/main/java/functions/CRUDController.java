package functions;

import builder.builderImpl.ProduceBuild;
import builder.director.ProduceDirector;
import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import dao.DAO;
import dao.daoImpl.ProduceDAO;
import models.Produce;

import java.util.HashMap;
import java.util.List;

public class CRUDController {
    private final DAO dao;

    public CRUDController(DAO dao){
        this.dao = dao;
    }

    public String saveProduce(Produce produce){
        if (dao.createEntry(produce) == 0) {
            return "\"error\" : \"Failed to insert data entry\",\n";
        }

        return "\"crud_result\" : \"Data entry inserted\",\n";
    }

    public String getTable(){
        List<HashMap<String, Object>> products = dao.getFullTable();
        ProduceDirector director = new ProduceDirector();
        StringBuilder res = new StringBuilder();

        for (HashMap<String, Object> produce : products){

            director.setBuilder(new ProduceBuild(produce));
            director.buildProduce();
            Produce tempProd = director.getProduce();

            res.append("\n\""+tempProd.getId()+"\" : "+tempProd.getJsonString());

            if(products.lastIndexOf(produce) != products.size()-1){
                res.append(",\n");
            }
        }

        return "\"products\" : {"+res.toString()+"\n},\n";
    }
}
