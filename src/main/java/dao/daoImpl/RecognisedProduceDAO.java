package dao.daoImpl;

import dao.DAO;
import database.Database;
import functions.FunctionMain;
import utils.FunctionLog;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class RecognisedProduceDAO extends DAO {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "RecognisedProduceDAO";

    public RecognisedProduceDAO() {
        super();
    }

    @Override
    public List<HashMap<String, Object>> getFullTable() {
        return getFullTable("recognized_produce");
    }

    @Override
    public int createEntry(Object dataObject) {
        return 0;
    }
}
