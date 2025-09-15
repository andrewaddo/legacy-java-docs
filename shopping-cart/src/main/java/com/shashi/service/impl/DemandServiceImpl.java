package com.shashi.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.shashi.beans.DemandBean;
import com.shashi.service.DemandService;
import com.shashi.utility.DBUtil;

/**
 * Implementation of the DemandService interface.
 * This class handles business logic for user demands for out-of-stock products.
 * When a product is unavailable, a user's interest is recorded as a "demand".
 * This allows the system to notify users when the product is back in stock.
 */
public class DemandServiceImpl implements DemandService {

	/**
	 * Adds a product to the user demand list. If the user has already demanded this product, it does nothing.
	 *
	 * @param userId The user's ID.
	 * @param prodId The product's ID.
	 * @param demandQty The quantity of the product demanded.
	 * @return true if the demand was successfully added or already existed, false otherwise.
	 */
	@Override
	public boolean addProduct(String userId, String prodId, int demandQty) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			// First, check if the user has already demanded this product.
			ps = con.prepareStatement("select * from user_demand where username=? and prodid=?");

			ps.setString(1, userId);
			ps.setString(2, prodId);

			rs = ps.executeQuery();

			if (rs.next()) {
				// If a demand record already exists, do nothing and consider it a success.
				flag = true;
			} else {
				// If no demand exists, insert a new record.
				ps2 = con.prepareStatement("insert into  user_demand values(?,?,?)");

				ps2.setString(1, userId);

				ps2.setString(2, prodId);

				ps2.setInt(3, demandQty);

				int k = ps2.executeUpdate();

				if (k > 0)
					flag = true;
			}

		} catch (SQLException e) {
			flag = false;
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(ps2);
		DBUtil.closeConnection(rs);

		return flag;
	}

	/**
	 * Removes a product from a user's demand list.
	 *
	 * @param userId The user's ID.
	 * @param prodId The product's ID.
	 * @return true if the product was successfully removed or if it was not in the list to begin with.
	 */
	@Override
	public boolean removeProduct(String userId, String prodId) {
		boolean flag = false;

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from user_demand where username=? and prodid=?");

			ps.setString(1, userId);
			ps.setString(2, prodId);

			rs = ps.executeQuery();

			if (rs.next()) {
				// If the demand exists, delete it.
				ps2 = con.prepareStatement("delete from  user_demand where username=? and prodid=?");

				ps2.setString(1, userId);

				ps2.setString(2, prodId);

				int k = ps2.executeUpdate();

				if (k > 0)
					flag = true;

			} else {
				// If the demand does not exist, consider the removal successful.
				flag = true;
			}

		} catch (SQLException e) {
			flag = false;
			e.printStackTrace();
		}

		DBUtil.closeConnection(con);
		DBUtil.closeConnection(ps);
		DBUtil.closeConnection(ps2);
		DBUtil.closeConnection(rs);

		return flag;
	}

	/**
	 * Convenience method to add a product demand using a DemandBean.
	 *
	 * @param userDemandBean The bean containing user and product demand details.
	 * @return true if the demand was successfully added, false otherwise.
	 */
	@Override
	public boolean addProduct(DemandBean userDemandBean) {

		return addProduct(userDemandBean.getUserName(), userDemandBean.getProdId(), userDemandBean.getDemandQty());
	}

	/**
	 * Finds all users who have demanded a particular product.
	 *
	 * @param prodId The product ID to check for.
	 * @return A list of DemandBean objects, each representing a user's demand for the product.
	 */
	@Override
	public List<DemandBean> haveDemanded(String prodId) {
		List<DemandBean> demandList = new ArrayList<DemandBean>();

		Connection con = DBUtil.provideConnection();

		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("select * from user_demand where prodid=?");
			ps.setString(1, prodId);
			rs = ps.executeQuery();

			while (rs.next()) {

				DemandBean demand = new DemandBean(rs.getString("username"), rs.getString("prodid"),
						rs.getInt("quantity"));

				demandList.add(demand);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return demandList;
	}

}
