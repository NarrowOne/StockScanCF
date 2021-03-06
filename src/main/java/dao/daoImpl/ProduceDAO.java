package dao.daoImpl;

import dao.DAO;
import database.Database;
import functions.FunctionMain;
import models.Produce;
import utils.FunctionLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class ProduceDAO extends DAO<Produce> {
    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final String TAG = "ProduceDAO";
    private final String tableName = "produce";

    public ProduceDAO() {
        super();
    }

    @Override
    public List<HashMap<String, Object>> getFullTable() {
        return getFullTable("produce");
    }



    @Override
    public int createEntry(Produce dataObject) {
        if(dataObject == null){
            logger.severe("no data object provided");
            return 0;
        }
        logger.info(dataObject.getJsonString());

        FunctionLog.addLog(TAG, "Attempting to get recognised products");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("INSERT INTO produce (producer, product_code, batch, name, weight, expiration)" +
                                            "VALUES ('"+ dataObject.getProducer()+"','"+ dataObject.getProduct_code()+"','"+ dataObject.getBatch()+"'," +
                                                    "'"+ dataObject.getName()+"','"+ dataObject.getWeight()+"','"+ dataObject.getExpiry()+"')");
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
    public int updateEntry(Produce dataObject) {
        if(dataObject == null){
            return 0;
        }

        FunctionLog.addLog(TAG, "Attempting to get update product");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            FunctionLog.addLog(TAG, dataObject.toString());
            int result = stmt.executeUpdate("UPDATE produce" +
                                                "\nSET producer='"+dataObject.getProducer()+"', product_code='"+dataObject.getProduct_code()+
                                                    "', batch='"+dataObject.getBatch()+"', name='"+dataObject.getName()+
                                                    "', weight='"+dataObject.getWeight()+"', expiration='"+dataObject.getExpiry()+
                                                "'\nWHERE id="+dataObject.getId()+";");
            FunctionLog.addLog(TAG, String.valueOf(result));
            return result;
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
    public int removeEntry(Produce dataObject) {
        if(dataObject == null){
            return 0;
        }

        FunctionLog.addLog(TAG, "Attempting to get delete product");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("DELETE FROM produce" +
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
