import java.sql.Date;


public class tData {

	String TRANSACTION_ID;
	String PRODUCT_ID;
	String CUSTOMER_ID;
	String CUSTOMER_NAME;
	String STORE_ID;
	String STORE_NAME;
	Date T_DATE;
	Integer QUANTITY;
	// join attributes
	String PRODUCT_NAME;
	String SUPPLIER_ID;
	String SUPPLIER_NAME;
	Float TOTAL_SALE;

	@Override
	public String toString() {
		return "TRANSACTION_ID=" + TRANSACTION_ID + ", PRODUCT_ID=" + PRODUCT_ID + ", CUSTOMER_ID=" + CUSTOMER_ID
				+ ", CUSTOMER_NAME=" + CUSTOMER_NAME + ", STORE_ID=" + STORE_ID + ", STORE_NAME=" + STORE_NAME
				+ ", T_DATE=" + T_DATE + ", QUANTITY=" + QUANTITY + ", PRODUCT_NAME=" + PRODUCT_NAME + ", SUPPLIER_ID="
				+ SUPPLIER_ID + ", SUPPLIER_NAME=" + SUPPLIER_NAME + ", TOTAL_SALE=" + TOTAL_SALE;
	}

	public tData(String tRANSACTION_ID, String pRODUCT_ID, String cUSTOMER_ID, String cUSTOMER_NAME,
			String sTORE_ID, String sTORE_NAME, Date t_DATE, Integer qUANTITY) {
		TRANSACTION_ID = tRANSACTION_ID;
		PRODUCT_ID = pRODUCT_ID;
		CUSTOMER_ID = cUSTOMER_ID;
		CUSTOMER_NAME = cUSTOMER_NAME;
		STORE_ID = sTORE_ID;
		STORE_NAME = sTORE_NAME;
		T_DATE = t_DATE;
		QUANTITY = qUANTITY;
	}

	public void addAttributes(mData masterData) {
		this.PRODUCT_NAME = masterData.PRODUCT_NAME;
		this.SUPPLIER_ID = masterData.SUPPLIER_ID;
		this.SUPPLIER_NAME = masterData.SUPPLIER_NAME;
		this.TOTAL_SALE = this.QUANTITY * masterData.PRICE;
	}
}
