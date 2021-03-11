package database;

import functions.FunctionMain;
import utils.FunctionLog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public final class Database {
//    private static final Logger logger = Logger.getLogger(FunctionMain.class.getName());
    private static final Database instance = new Database();
    private static volatile Connection connection;
    private static final String URL = "jdbc:mysql://db4free.net:3306/stockscandb";
    private static final String USERNAME = "stockscandb";
    private static final String PASSSWORD = "stockscan2020";
    private static final String TAG = "Database";

    private Database(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    URL,
                    USERNAME,
                    PASSSWORD
            );

            FunctionLog.addLog(TAG, "DB connection success");
//            logger.info("DB connection success");
        } catch (ClassNotFoundException | SQLException e) {
            FunctionLog.addLog(TAG,"DB connection failed");
//            logger.severe(e.toString());
        }
    }

    public static Database getInstance() {
        try {
            if(instance.getConnection().isClosed()){
                connection = DriverManager.getConnection(
                        URL,
                        USERNAME,
                        PASSSWORD
                );
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return instance;
    }

    public Connection getConnection(){
        return connection;
    }
}
