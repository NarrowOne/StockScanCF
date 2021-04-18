package dao.daoImpl;

import dao.DAO;
import database.Database;
import functions.FunctionMain;
import models.LogEntry;
import utils.FunctionLog;

import javax.swing.text.html.parser.Entity;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class LogDAO  extends DAO<LogEntry> {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "LogDAO";
    private final String tableName = "logs";

    public LogDAO() {
        super();
    }

    @Override
    public List<HashMap<String, Object>> getFullTable() {
        return getFullTable(tableName);
    }

    public List<HashMap<String, Object>> getLogsByType(int log_type) {
        return getFullTable(tableName, "WHERE log_type = "+log_type);
    }

    @Override
    public int createEntry(LogEntry dataObject) {
        if(dataObject == null){
            return 0;
        }
        LogEntry record = dataObject;

        FunctionLog.addLog(TAG, "Attempting to create new log");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("INSERT INTO logs (log_type, subject, description)" +
                    "VALUES ('"+record.getType()+"','"+record.getSubject()+"','"+record.getDescription()+"')");
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
        return 0;
    }

    @Override
    public int updateEntry(LogEntry dataObject) {
        if(dataObject == null){
            return 0;
        }

        FunctionLog.addLog(TAG, "Attempting to get update log");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("UPDATE logs" +
                                        "\nSET log_type='"+dataObject.getType()+
                                                "', subject='"+dataObject.getSubject()+
                                                "', description='"+dataObject.getDescription()+
                                        "'\nWHERE id="+dataObject.getId()+";");

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

        return 0;
    }

    @Override
    public int removeEntry(LogEntry dataObject) {

        if(dataObject == null){
            return 0;
        }

        FunctionLog.addLog(TAG, "Attempting to get delete log");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("DELETE FROM logs" +
                                        "\nWHERE id="+dataObject.getId()+";");
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
        return 0;
    }
}
