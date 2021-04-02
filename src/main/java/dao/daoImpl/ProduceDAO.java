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

public class ProduceDAO extends DAO {
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
    public int createEntry(Object dataObject) {
        if(dataObject == null){
            return 0;
        }
        Produce produce = (Produce) dataObject;

        FunctionLog.addLog(TAG, "Attempting to get recognised products");
        Database db = Database.getInstance();
        Connection con = db.getConnection();

        try{
            Statement stmt = con.createStatement();
            return stmt.executeUpdate("INSERT INTO produce (producer, product_code, batch, name, weight, expiration)" +
                                            "VALUES ('"+produce.getProducer()+"','"+produce.getProduct_code()+"','"+produce.getBatch()+"'," +
                                                    "'"+produce.getName()+"','"+produce.getWeight()+"','"+produce.getExpiry()+"')");
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
