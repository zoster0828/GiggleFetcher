package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.PropertyHandler;
import vo.Content;

public class ContentRepository {
	static Logger logger = LogManager.getLogger("ContentRepository");
	final String JDBC_DRIVER = PropertyHandler.getProperty("mysql.db.driver");
	final String DB_URL = PropertyHandler.getProperty("mysql.db.url");
	final String USER = PropertyHandler.getProperty("mysql.db.user");
	final String PASS = PropertyHandler.getProperty("mysql.db.password");

	Connection conn = null;
	Statement stmt = null;

	public ContentRepository(){
		try {
//			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			stmt = conn.createStatement();
			logger.info("Database initializing success");
		} catch (Exception e) {
			logger.error("Mysql connection error");
			logger.error(e.toString());
		}
	}

	public void close() throws SQLException {
		stmt.close();
		conn.close();
	}

	public void save(Content content) throws SQLException {
		String sql = content.toSql();
		try {
			sql = sql.replace("\\", "");
			if(content.getSiteName().equals("MLBPark")){
				sql = sql.replace("&m=view","");
			}
			stmt.execute(sql);
		} catch (SQLException e) {
//			logger.warn("error sql is : "+sql);
//			logger.warn(e.getMessage());
			throw e;
		} catch (Exception e){
			logger.warn(e.getMessage());
			throw e;
		}
	}
}
