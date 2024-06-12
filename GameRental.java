/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class GameRental {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of GameRental store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public GameRental(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end GameRental

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            GameRental.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      GameRental esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the GameRental object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new GameRental (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Catalog");
                System.out.println("4. Place Rental Order");
                System.out.println("5. View Full Rental Order History");
                System.out.println("6. View Past 5 Rental Orders");
                System.out.println("7. View Rental Order Information");
                System.out.println("8. View Tracking Information");

                //the following functionalities basically used by employees & managers
                System.out.println("9. Update Tracking Information");

                //the following functionalities basically used by managers
                System.out.println("10. Update Catalog");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewProfile(esql); break;
                   case 2: updateProfile(esql); break;
                   case 3: viewCatalog(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewTrackingInfo(esql); break;
                   case 9: updateTrackingInfo(esql); break;
                   case 10: updateCatalog(esql); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(GameRental esql){
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(GameRental esql){
      return null;
   }//end

// Rest of the functions definition go in here

   public static void viewProfile(GameRental esql) {}
   public static void updateProfile(GameRental esql) {}
   public static void viewCatalog(GameRental esql) {}
   public static void placeOrder(GameRental esql) {}
   public static void viewAllOrders(GameRental esql) {
      try {
            // Get the logged-in user
            System.out.print("Enter your login to view your rental history: ");
            String login = in.readLine();

            // Construct the query to fetch rental orders in an order based on timestamp
            String query = String.format("SELECT rentalOrderID, orderTimestamp, dueDate, totalPrice FROM RentalOrder WHERE login='%s' ORDER BY orderTimestamp DESC;", login);
            
            // Execute the query and print the results
            int rowCount = esql.executeQueryAndPrintResult(query);
            
            // Check if any rental orders were found
            if (rowCount == 0) {
                System.out.println("No rental history found for the user: " + login);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
   public static void viewRecentOrders(GameRental esql) {
      try {
            // Get the logged-in user from the session
            System.out.print("Enter your login to view your 5 most recent rental history: ");
            String login = in.readLine();
            
            //String login = esql.currentUser;

            // Construct the query to fetch the five most recent rental orders in an order based on timestamp
            String query = String.format("SELECT rentalOrderID, orderTimestamp, dueDate, totalPrice FROM RentalOrder WHERE login='%s' ORDER BY orderTimestamp DESC LIMIT 5;", login);
            
            // Execute the query and print the results
            int rowCount = esql.executeQueryAndPrintResult(query);
            
            // Check if any rental orders were found
            if (rowCount == 0) {
                System.out.println("No recent orders found for the user: " + login);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
   public static void viewOrderInfo(GameRental esql) {
      try{
         System.out.println("Enter your login");
         String userLogin = in.readline();
            //using rental orderID to check for a specific order
         System.out.println("Enter rental order ID");
         String rentalorderID = in.readLine();
         //verify it is the correct user 
         String verification_query = String.format("SELECT * FROM RentalOrder WHERE rentalOrderID = '%s' AND login = '%s';",rentalOrderID, userLogin);
         int rows_affected = esql.executeQuery(verification_query);

         // retrieves rental order details if one or more exists
         if (rows_affected > 0){
            String rentalOrderDetailsQuery = String.format("SELECT rentalorderID, orderTimestamp, dueDate, totalPrice FROM RentalOrder WHERE rentalOrderID = '%s';",rentalorderID);
            esql.executeQueryAndReturnResult(rentalOrderDetailsQuery);

            String trackingInfoQuery = String.format("SELECT trackingID FROM TrackingInfo WHERE rentalOrderID = '%s';", rentalOrderID); 
            esql.executeQueryAndPrintResult(trackingInfoQuery);

            String gamesListQuery = String.format(
                "SELECT gameID, gameName, genre " +
                "FROM Catalog " +
                "WHERE gameID IN (SELECT gameID FROM GamesInOrder WHERE rentalOrderID = '%s');",rentalOrderID);       
                 esql.executeQueryAndPrintResult(gamesListQuery);
            } 
            else {
               System.out.println("No such rental order found for the given login.");
            }
         }
         catch (Exception e) {
            System.err.println(e.getMessage());
      }
   }
   public static void viewTrackingInfo(GameRental esql) {
       try {
            // Get the logged-in user so they cannot access other data
            System.out.println("Enter your login");
            String userLogin = in.readline();

            // Ask the user to input a trackingID
            System.out.print("Enter the trackingID to view tracking information: ");
            String trackingID = in.readLine();

            // Construct the query to fetch tracking information for the given trackingID and ensure it belongs to the logged-in user
            String query = String.format(
                "SELECT T.trackingID, T.rentalOrderID, T.status, T.currentLocation, T.courierName, T.lastUpdateDate, T.additionalComments " +
                "FROM TrackingInfo T, RentalOrder R " +
                "WHERE T.trackingID='%s' AND T.rentalOrderID=R.rentalOrderID AND R.login='%s';", 
                trackingID, login);

            // Execute the query and print the results
            int rowCount = esql.executeQueryAndPrintResult(query);

            // Check if any tracking information was found
            if (rowCount == 0) {
                System.out.println("No tracking information found for the trackingID: " + trackingID);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
   public static void updateTrackingInfo(GameRental esql) {
      try {
         if (!esql.currentUserRole.equals("employee") && !esql.currentUserRole.equals("manager")) {
                System.out.println("You do not have permission to update tracking information.");
                return;
            }
         System.out.println("Enter the trackinID you wish to update")
         System.out.println("1.Update status");
         System.out.println("2.Update currentLocation");
         System.out.println("3.Update courierName");
         System.out.println("4.Update additionalComments");
         switch(readChoice()){
            case 1:
            System.out.print("Enter the new status");
            String status = in.readline();
            String query1 = String.format("UPDATE TrackingInfo SET status='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",status, trackingID);
            esql.executeUpdate(query);
            System.out.println("New status sucessfully updated")
         break;
            case 2:
            System.out.print("Enter the new currentLocation");
            String currentLocation = in.readline();
            String query2 = String.format(
                    "UPDATE TrackingInfo SET currentLocation='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                    currentLocation, trackingID);
            esql.executeUpdate(query);
            System.out.println("New currentLocation sucessfully updated");
         break;
         case 3:
            System.out.print("Enter the new courierName");
            String courierName = in.readline();
            String query3 = String.format(
                    "UPDATE TrackingInfo SET courierName='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                    courierName, trackingID);
            esql.executeUpdate(query);
            System.out.println("New courierName sucessfully updated");
         break;
         case 4:
            System.out.print("Enter the new additionalComments");
            String additionalComments = in.readline();
            String query4 = String.format(
                    "UPDATE TrackingInfo SET additionalComments='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                    additionalComments, trackingID);
            esql.executeUpdate(query);
            System.out.println("New additionalComments sucessfully updated");
         break;
         }
         catch (Exception e) {
            System.err.println(e.getMessage());
         }
      }
   }
   public static void updateCatalog(GameRental esql) {}
   public static void updateUser(GameRental esql) {}


}//end GameRental

