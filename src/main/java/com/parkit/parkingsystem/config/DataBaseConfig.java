package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DataBaseConfig {
    private static final Logger logger = LogManager.getLogger("DataBaseConfig");

    public Connection getConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties = new Properties();

        try (FileInputStream resource = new FileInputStream("resources/credentials.properties")) {
            properties.load(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String user = properties.getProperty("username");
        String password = properties.getProperty("password");
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("url");

        logger.info("Create DB connection");
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }

    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }

    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
