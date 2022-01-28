# Metro-Data-Warehouse-Project
This project is a complete pipeline of understanding the flow of data warehousing. From ETL to analyzing the warehouse, a complete project is being developed.




=======> INSTRUCTIONS TO OPERATE THE PROJECT 

1. 1st of all, Run the "Transaction_and_MasterData_generator.sql" file in your database.
2. Next, create schema of the DWH using the file "createDW.sql" that is attached.
3. Now, to implement ETL:

   Import the following
	+ commons-collections4-4.4.jar 
	+ mysql-connector-java-5.1.49.jar
	+ ojdbc6.jar 
libraries in the Project and then run. I have attached the above libraries in a folder named "libraries".

4. To run on your MySQL workbench, change the root name and password to your's name and password accordingly    in DBHandler.java file.
5. After successfully running of meshJoin, perform queries using the file "queries.sql" that is attached.
