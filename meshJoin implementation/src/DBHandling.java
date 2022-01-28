
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

public class DBHandling {
	
	
	String url;
	String username;
	String password;
	private Connection conn;
	private Integer transactionsRead;
	private Statement transactionsStatement;
	private Statement masterDataStatement;
	
	private ResultSet transactionalData;
	private ResultSet masterData;
	
	private Statement timeIDStatement;
	
	public static Connection con;

	public DBHandling() throws SQLException {
		this.url = "jdbc:mysql://localhost:3306/mdb";
		this.username = "root";
		this.password = "naseer786#N#";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.conn = DriverManager.getConnection(url, username, password);
		
		this.transactionsRead = 1;
		this.transactionsStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		this.masterDataStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		this.transactionalData = transactionsStatement.executeQuery("SELECT * FROM mdb.transactions;");
		this.masterData = masterDataStatement.executeQuery("SELECT * FROM mdb.masterdata;");
		this.timeIDStatement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		this.loadTime();
	}


	private int totalSize(ResultSet rset) throws SQLException {
		int previous = rset.getRow();
		int totalSize = 0;
		if (rset != null) {
			rset.last();
			totalSize = rset.getRow();
		}
		rset.absolute(previous);
		return totalSize;
	}

	private boolean isEnd(ResultSet rs) throws SQLException {
		return rs.getRow() == this.totalSize(rs);
	}

	public ArrayList<tData> getTransactionsData(int rows) throws SQLException {
		ArrayList<tData> transactionsToRet = new ArrayList<>(rows);
		for (int i = this.transactionsRead; (i < this.transactionsRead + rows)
				&& !this.isEnd(this.transactionalData); i++) {
			this.transactionalData.absolute(i);
			transactionsToRet.add(new tData(this.transactionalData.getString("TRANSACTION_ID"),
					this.transactionalData.getString("PRODUCT_ID"),
					this.transactionalData.getString("CUSTOMER_ID"),
					this.transactionalData.getString("CUSTOMER_NAME"),
					this.transactionalData.getString("STORE_ID"),
					this.transactionalData.getString("STORE_NAME"), this.transactionalData.getDate("T_DATE"),
					this.transactionalData.getInt("QUANTITY")));
		}
		this.transactionsRead += rows;
		return transactionsToRet;
	}

	public ArrayList<mData> getMasterData(String PRODUCT_ID) throws SQLException {
		ArrayList<mData> masterDataToRet = new ArrayList<>(10);
		for (int i = 1; i <= this.totalSize(this.masterData); i++) {
			this.masterData.absolute(i);
			if (this.masterData.getString("PRODUCT_ID").equals(PRODUCT_ID)) {
				for (int j = i; j < i + 10 && j <= this.totalSize(this.masterData); j++) {
					this.masterData.absolute(j);
					masterDataToRet.add(new mData(this.masterData.getString("PRODUCT_ID"),
							this.masterData.getString("PRODUCT_NAME"),
							this.masterData.getString("SUPPLIER_ID"),
							this.masterData.getString("SUPPLIER_NAME"),
							this.masterData.getFloat("PRICE")));
				}
				break;
			}
		}
		return masterDataToRet;
	}

	public boolean endOfTransactions() throws SQLException {
		return this.transactionalData.getRow() == this.totalSize(this.transactionalData);
	}

	public void loadToDWH(tData transaction) throws SQLException {
		// PRODUCT table
		PreparedStatement stmt = this.conn.prepareStatement("INSERT IGNORE INTO metro_dwh.product VALUES (?, ?)");
		stmt.setString(1, transaction.PRODUCT_ID);
		stmt.setString(2, transaction.PRODUCT_NAME);
		stmt.executeUpdate();
		// CUSTOMER table
		stmt = this.conn.prepareStatement("INSERT IGNORE INTO metro_dwh.customer VALUES (?, ?)");
		stmt.setString(1, transaction.CUSTOMER_ID);
		stmt.setString(2, transaction.CUSTOMER_NAME);
		stmt.executeUpdate();
		// STORE table
		stmt = this.conn.prepareStatement("INSERT IGNORE INTO metro_dwh.store VALUES (?, ?)");
		stmt.setString(1, transaction.STORE_ID);
		stmt.setString(2, transaction.STORE_NAME);
		stmt.executeUpdate();
		// SUPPLIER table
		stmt = this.conn.prepareStatement("INSERT IGNORE INTO metro_dwh.supplier VALUES (?, ?)");
		stmt.setString(1, transaction.SUPPLIER_ID);
		stmt.setString(2, transaction.SUPPLIER_NAME);
		stmt.executeUpdate();
		// GETTING TIME_ID
		
		LocalDate localDate = transaction.T_DATE.toLocalDate();
		ResultSet time_rs = this.timeIDStatement.executeQuery("SELECT time.TIME_ID FROM metro_dwh.time where time.DAY_OF_MONTH = "
						+ localDate.getDayOfMonth() + " and time.MONTH = " + localDate.getMonthValue()
						+ " and time.YEAR = " + localDate.getYear() + ";");
		time_rs.next();
		int time_id = time_rs.getInt(1);
		
		
		// SALES table
		stmt = this.conn.prepareStatement("INSERT IGNORE INTO metro_dwh.sales VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
		stmt.setString(1, transaction.TRANSACTION_ID);
		stmt.setString(2, transaction.CUSTOMER_ID);
		stmt.setString(3, transaction.STORE_ID);
		stmt.setString(4, transaction.PRODUCT_ID);
		stmt.setString(5, transaction.SUPPLIER_ID);
		stmt.setInt(6, time_id);
		stmt.setInt(7, transaction.QUANTITY);
		stmt.setFloat(8, transaction.TOTAL_SALE);
		stmt.executeUpdate();
	}
	
	private void loadTime() throws SQLException {
		PreparedStatement stmt = this.conn
				.prepareStatement("INSERT IGNORE INTO metro_dwh.time VALUES (?, ?, ?, ?, ?, ?)");
		int time_id = 1;
		int quarter = -1;
		for (LocalDate date = LocalDate.parse("2016-01-01"); date
				.isBefore(LocalDate.parse("2017-01-01")); date = date.plusDays(1)) {
			// TIME_ID
			stmt.setInt(1, time_id++);
			// DAY_OF_MONTH
			stmt.setInt(2, date.getDayOfMonth());
			// DAY_OF_WEEK
			stmt.setString(3, date.getDayOfWeek().toString());
			// MONTH
			stmt.setInt(4, date.getMonthValue());
			// QUARTER
			if (date.getMonthValue() >= 1 && date.getMonthValue() <= 3)
				quarter = 1;
			else if (date.getMonthValue() >= 4 && date.getMonthValue() <= 6)
				quarter = 2;
			else if (date.getMonthValue() >= 7 && date.getMonthValue() <= 9)
				quarter = 3;
			else if (date.getMonthValue() >= 10 && date.getMonthValue() <= 12)
				quarter = 4;
			stmt.setInt(5, quarter);
			// YEAR
			stmt.setInt(6, date.getYear());

			stmt.executeUpdate();
		}
	}

	public void close() throws SQLException {
		this.transactionalData.close();
		this.masterData.close();
		this.masterDataStatement.close();
		this.transactionsStatement.close();
		this.conn.close();
	}

}
