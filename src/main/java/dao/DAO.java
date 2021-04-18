package dao;

import database.Database;
import functions.FunctionMain;
import utils.FunctionLog;

import javax.annotation.Nullable;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public abstract class DAO<T> {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "DAO";

    public DAO() {
    }

    protected static List<HashMap<String, Object>> getResults(ResultSet results) throws SQLException{
        if(results == null) {
            FunctionLog.addLog(TAG, "Empty Query Results");
            logger.severe("Empty Query Results");
            return null;
        }

        List<HashMap<String, Object>> resultList = new ArrayList<>();

        while(results.next()){
            HashMap<String, Object> resultLine = new HashMap<>();
            ResultSetMetaData rsmd = results.getMetaData();
            int columnCount = 1;
            while(columnCount <= rsmd.getColumnCount()){
                String columnName = rsmd.getColumnName(columnCount);
                resultLine.put(columnName, results.getObject(columnName));
                columnCount++;
            }

            resultList.add(resultLine);
        }

        return resultList;
    }

    public List<HashMap<String, Object>> getFullTable(String... details){
        String where = details.length > 1 ? " "+details[1] : ";";

        FunctionLog.addLog(TAG, "Attempting to get recognised products");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            String statementText = "SELECT * FROM "+details[0]+where;
            List<HashMap<String, Object>> results = DAO.getResults(stmt.executeQuery(statementText));
            return results;
        } catch (SQLException throwable) {
            FunctionLog.addLog(TAG, throwable.getMessage());
            logger.severe(throwable.toString());
        }finally {
            try {
                con.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return null;
    }

    public abstract List<HashMap<String, Object>> getFullTable();

    public abstract int createEntry(T dataObject);
    public abstract int updateEntry(T dataObject);
    public abstract int removeEntry(T dataObject);

}
