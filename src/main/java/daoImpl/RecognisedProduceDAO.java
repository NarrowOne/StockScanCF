package daoImpl;

import dao.DAO;
import database.Database;
import utils.FunctionLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecognisedProduceDAO implements DAO {
    private static final String TAG = "RecognisedProduceDAO";

    @Override
    public List<HashMap<String, Object>> getFullTable() {
        FunctionLog.addLog(TAG, "Attempting to get recognised products");
        Connection con = Database.getConnection();

        try{
            Statement stmt = con.createStatement();
            List<HashMap<String, Object>> results = DAO.getResults(stmt.executeQuery("SELECT * FROM recognized_produce"));
            return results;
        } catch (SQLException throwable) {
            FunctionLog.addLog(TAG, throwable.getMessage());
            logger.severe(throwable.toString());
        }
        return null;
    }
}
