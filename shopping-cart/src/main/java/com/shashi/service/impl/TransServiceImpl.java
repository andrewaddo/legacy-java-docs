package com.shashi.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.shashi.service.TransService;
import com.shashi.utility.DBUtil;

/**
 * Implementation of the TransService interface.
 * This class handles business logic related to transactions.
 */
public class TransServiceImpl implements TransService {

	/**
	 * Retrieves the user ID associated with a given transaction ID.
	 *
	 * @param transId The transaction ID.
	 * @return The user ID (email) of the user who made the transaction, or an empty string if not found.
	 */
	@Override
	public String getUserId(String transId) {
		String userId = "";

		Connection con = DBUtil.provideConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			ps = con.prepareStatement("select username from transactions where transid=?");

			ps.setString(1, transId);

			rs = ps.executeQuery();

			if (rs.next())
				userId = rs.getString(1);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return userId;
	}

}
