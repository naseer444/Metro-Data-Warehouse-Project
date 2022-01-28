import java.sql.SQLException;
import java.util.ArrayList;



public class meshJoin {
	public static void run(DBHandling dbHandler) throws SQLException {
		hashTable hashTableQueue = new hashTable(1000);
		ArrayList<tData> streamBuffer;
		ArrayList<mData> diskBuffer;
		int count=0;
		while (true) {
			// when there is end of transactional data
			if (dbHandler.endOfTransactions() && hashTableQueue.isEmpty()) {
				break;
			}
			// 1st Phase - Extraction of data from database using DBHandler class
			streamBuffer = dbHandler.getTransactionsData(hashTableQueue.getCapacity());
			hashTableQueue.addTransactions(streamBuffer);
			// 2nd Phase - Transformation of data, Load next MD partition into the disk buffer
			// MD partition in the disk-buffer is replaced by the new MD
			// partition from the disk.
			diskBuffer = dbHandler.getMasterData(hashTableQueue.getOldestEntry());
			// Look-up each tuple from the disk buffer to the hash table
			for (mData masterDataTuple : diskBuffer) {
				ArrayList<hashTable.Value> joined = hashTableQueue.join(masterDataTuple.PRODUCT_ID);
				for (hashTable.Value tuple : joined) {
					tuple.transaction.addAttributes(masterDataTuple);
			// 3rd Phase - Loading of data from transorformation phase to metroDWH 
					dbHandler.loadToDWH(tuple.transaction);	
					
					count++;
					System.out.println("Loading...  " + (count+1)+". "+tuple.transaction);				
					
						//System.out.println((count+1)+". "+tuple.transaction);
						
					
				}
				hashTableQueue.discard(masterDataTuple.PRODUCT_ID);
			}
		}
	}
}
