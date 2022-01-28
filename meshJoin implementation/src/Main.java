

import java.sql.SQLException;


public class Main {

public static void main(String[] args) throws SQLException {
		
		//long startTime = System.nanoTime();
		final long startTime = System.nanoTime();
		DBHandling dbHandler = new DBHandling();
		meshJoin.run(dbHandler);
		dbHandler.close();
		
		final long duration = System.nanoTime() - startTime;
		System.out.println("The run time in seconds = ");
		System.out.println(duration/1000000000);
	}


}



