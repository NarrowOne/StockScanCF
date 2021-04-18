package dao.controllers;

import dao.daoImpl.LogDAO;

public class LogDAOController extends DAOController<String>{
    private final LogDAO dao;

    public LogDAOController(LogDAO dao) {
        super(dao);
        this.dao = dao;
    }


    @Override
    String getFullTable() {
        return toJson("logs", dao.getFullTable());
    }

    @Override
    public String read(String... details) {
        if(details.length == 0)
            return getFullTable();

        int type = Integer.parseInt(details[0]);

        return toJson("logs", dao.getLogsByType(type));
    }
}
