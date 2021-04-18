package dao.controllers;

import dao.daoImpl.RecognisedProduceDAO;

import java.util.HashMap;
import java.util.List;

public class RecognisedProduceDaoController extends DAOController<List<HashMap<String, Object>>> {
    private final RecognisedProduceDAO dao;

    public RecognisedProduceDaoController(RecognisedProduceDAO dao) {
        super(dao);
        this.dao = dao;
    }

    @Override
    List<HashMap<String, Object>> getFullTable() {
        return dao.getFullTable();
    }

    @Override
    public List<HashMap<String, Object>> read(String... details) {
        return getFullTable();
    }

    @Override
    public String update(Object object) {
        return null;
    }

    @Override
    public String delete(Object object) {
        return null;
    }
}
