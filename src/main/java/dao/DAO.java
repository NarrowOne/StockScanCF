package dao;

import functions.FunctionMain;
import utils.FunctionLog;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public interface DAO {
    String TAG = "DAO";
    Logger logger = Logger.getLogger(FunctionMain.class.getName());

    List<HashMap<String, Object>> getFullTable();
    static List<HashMap<String, Object>> getResults(ResultSet results) throws SQLException{
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

}
