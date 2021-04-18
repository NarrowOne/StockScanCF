package dao.controllers;

import builder.builderImpl.ProduceBuild;
import builder.director.ProduceDirector;
import dao.daoImpl.ProduceDAO;
import models.Produce;
import utils.FunctionLog;

import java.util.HashMap;
import java.util.List;

public class ProduceDAOController extends DAOController<String>{
    private static final String TAG = "ProduceDAOController";
    private final ProduceDAO dao;

    public ProduceDAOController(ProduceDAO dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    public String getFullTable() {
        FunctionLog.addLog(TAG, "getFullTable");
        List<HashMap<String, Object>> products = dao.getFullTable();
        ProduceDirector director = new ProduceDirector();
        StringBuilder res = new StringBuilder();

        for (HashMap<String, Object> produce : products){

            director.setBuilder(new ProduceBuild(produce));
            director.buildProduce();
            Produce tempProd = director.getProduce();

            res.append(toJson("", tempProd));

            if(products.lastIndexOf(produce) != products.size()-1){
                res.append(",\n");
            }
        }

        return "\"products\" : {"+ res +"\n},\n";
    }

    @Override
    public String read(String... details) {
        assert details != null;
        if(details.length == 0)
            return getFullTable();

        return null;
    }
}
