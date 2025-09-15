package com.shashi.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * A utility class for managing database connections.
 * It provides a singleton connection to the database and helper methods to close resources.
 */
public class DBUtil {
	private static Connection conn;

	public DBUtil() {
	}

	/**
	 * Provides a singleton database connection.
	 * If the connection is null or closed, it creates a new one using details from the application.properties file.
	 * 
	 * @return A Connection object to the database.
	 */
	public static Connection provideConnection() {

		try {
			if (conn == null || conn.isClosed()) {
				// Load database configuration from the properties file
				ResourceBundle rb = ResourceBundle.getBundle("application");
				String connectionString = rb.getString("db.connectionString");
				String driverName = rb.getString("db.driverName");
				String username = rb.getString("db.username");
				String password = rb.getString("db.password");
				try {
					Class.forName(driverName);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				conn = DriverManager.getConnection(connectionString, username, password);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * Closes the given database connection.
	 * 
	 * @param con The Connection to close.
	 */
	public static void closeConnection(Connection con) {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the given ResultSet.
	 * 
	 * @param rs The ResultSet to close.
	 */
	public static void closeConnection(ResultSet rs) {
		try {
			if (rs != null && !rs.isClosed()) {
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Closes the given PreparedStatement.
	 * 
	 * @param ps The PreparedStatement to close.
	 */
	public static void closeConnection(PreparedStatement ps) {
		try {
			if (ps != null && !ps.isClosed()) {
				try {
					ps.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
