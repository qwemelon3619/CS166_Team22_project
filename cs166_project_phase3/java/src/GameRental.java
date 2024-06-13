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
 import java.util.concurrent.ThreadLocalRandom;
 import java.util.Date;
 import javax.management.relation.Role;
 import java.sql.Timestamp;
 import java.util.ArrayList;
 import java.lang.Math;
import java.math.BigDecimal;
import java.util.Random;
 import java.util.Calendar;
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
             System.out.print (rs.getString(i) + "\t");
          System.out.println();
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
                String role=CheckRole(esql,authorisedUser);
                System.out.println(String.format("Welcome %s %s",role ,authorisedUser));
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
                 if(role.equals("employees") || role.equals("managers")){
                   //the following functionalities basically used by employees & managers
                   System.out.println("9. Update Tracking Information");
                   if(role.equals("managers")){
                      System.out.println("10. Update Catalog");
                      System.out.println("11. Update User");
                   }
                   //the following functionalities basically used by managers
                 }
 
                 System.out.println(".........................");
                 System.out.println("20. Log out");
                 switch (readChoice()){
                    case 1: viewProfile(esql,authorisedUser); break;
                    case 2: updateProfile(esql,authorisedUser); break;
                    case 3: viewCatalog(esql); break;
                    case 4: placeOrder(esql,authorisedUser); break;
                    case 5: viewAllOrders(esql); break;
                    case 6: viewRecentOrders(esql); break;
                    case 7: viewOrderInfo(esql); break;
                    case 8: viewTrackingInfo(esql); break;
                    case 9: 
                      if(role.equals("employees") || role.equals("managers")){
                         updateTrackingInfo(esql,role); 
                      }
                      else{
                         System.out.println("For employees");
                      }
                      break;
                    case 10: 
                     if(role.equals("managers")){
                        updateCatalog(esql);
                     }
                     else{
                        System.out.println("For managers");
                     }
                     break;
                    case 11: 
                    if(!(role.equals("managers"))){
                      authorisedUser = updateUser  (esql,authorisedUser);
                      role=CheckRole(esql,authorisedUser);
                    }
                    else{
                      System.out.println("For managers");
                    }
                    break;
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
       try{
          System.out.print("\tEnter ID: ");
          String ID = in.readLine();
          System.out.print("\tEnter password: ");
          String password = in.readLine();
          System.out.print("\tEnter phoneNumber: ");
          String phoneNumber = in.readLine();
          System.out.print("\tfavorite game(0 for blank): ");
          String favGames = in.readLine();
          String query = String.format("INSERT INTO Users (login,password,phoneNum,role,favGames,numOverdueGames) VALUES('%s','%s','%s','customer','%s',0)",ID,password,phoneNumber,favGames);
          esql.executeUpdate(query);
          query = String.format("INSERT INTO Customer (login) VALUES('%s')",ID);
          esql.executeUpdate(query);
          System.err.println("Finished");
       }  
       catch(Exception e){
          System.err.println (e.getMessage());
       }
    }//end CreateUser
 
 
    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static String LogIn(GameRental esql){
       try{
          System.out.print("\tEnter ID:");
          String ID = in.readLine();
          System.out.print("\tEnter password:");
          String password = in.readLine();
          String mySentence = String.format("SELECT * FROM Users WHERE login = '%s' AND password = '%s';", ID,password); 
          int rowCount = esql.executeQuery(mySentence);
          if(rowCount == 1){
             return ID;
          }
          else{
             return null;
          }
       }catch(Exception e){
          System.err.println (e.getMessage());
       }
       return null;
    }//end
 
 // Rest of the functions definition go in here
    public static String CheckRole(GameRental esql,String authorisedUser) {
       try{
          String query = String.format("SELECT * FROM Customer WHERE  login = '%s';",authorisedUser);
          int result = esql.executeQuery(query);
          if(result == 1){
             return "customer";//customer
          }
          query = String.format("SELECT * FROM Worker WHERE  login = '%s';",authorisedUser);
          result = esql.executeQuery(query);
          query = String.format("SELECT * FROM Users WHERE  login = '%s';",authorisedUser);
          List<List<String>> output = esql.executeQueryAndReturnResult(query);
          String role = output.get(0).get(2);
          return role;
       }catch(Exception e){
          System.err.println (e.getMessage());
       }
       return null;
    }
 
    public static String viewProfile(GameRental esql,String authorisedUser) {
       try{
          String query = String.format("SELECT * FROM Users WHERE  login = '%s';",authorisedUser);
          int result = esql.executeQueryAndPrintResult(query);
       }catch(Exception e){
          System.err.println (e.getMessage());
       }
       return null;
    }
    public static void updateProfile(GameRental esql,String authorisedUser) {
       try{
          System.out.println("1.Change MyPassword");
          System.out.println("2.Change Phone Number");
          System.out.println("3.Change Favorite Game");
          switch (readChoice()) {
             case 1:
             System.out.print("Your New password:");
             String password1 = in.readLine();
             System.out.print("Re New password:");
             String password2 = in.readLine();
             if(password1.equals(password2) && password1.length()<=30){
                String query = String.format("UPDATE Users SET password = '%s' WHERE login='%s';",password1,authorisedUser);
                esql.executeUpdate(query);
                System.out.println("Changed Password");
             }  
             else{
                System.out.println("You put wrong password or too long password");
             }
             break;
             case 2:
             System.out.print("Your New PhoneNumber:");
             String phonenumber = in.readLine();
             if(phonenumber.length()<=20){
                String query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login='%s';",phonenumber,authorisedUser);
                esql.executeUpdate(query);
                System.out.println("Changed PhoneNumber");
             }
             else{
                System.out.println("Too long PhoneNumber");
             }
             break;
             case 3:
                System.out.println("Whats your new Favorite Games?(put space between each game):");
                String favGames = in.readLine();
                String query = String.format("UPDATE Users SET favGames = '%s' WHERE login='%s';",favGames,authorisedUser);
                esql.executeUpdate(query);
                System.out.println("Changed Favorite Games");
             break;
             default:
                break;
          }
       }catch(Exception e){
          System.err.println (e.getMessage());
       }
    }
    
    public static void viewCatalog(GameRental esql) {
       try{
          System.out.println("1.Print all Catalog");
          System.out.println("2.Search Catalog Base on genre");
          System.out.println("3.Search Catalog Base on price");
          switch (readChoice()) {
             case 1:
                String query = String.format("SELECT * FROM Catalog");
                int rowCount = esql.executeQueryAndPrintResult(query);
             break;
             case 2:
                System.out.print("Which Genre Are You looking for:");
                String genre = in.readLine();
                String query_2 = String.format("SELECT * FROM Catalog WHERE genre='%s';",genre);
                int rowCount_2 = esql.executeQueryAndPrintResult(query_2);
             break;
             case 3:
                System.out.print("Search game under this price:");
                String price = in.readLine();
                System.out.print("1.ascending order 2.descending order:");
                String order = in.readLine();
                if(order.equals("1")){
                   String query_3 = String.format("SELECT * FROM Catalog WHERE price<%s ORDER BY price ASC;",price);
                   int rowCount_3 = esql.executeQueryAndPrintResult(query_3);
                }
                else if (order.equals("2")){
                   String query_3 = String.format("SELECT * FROM Catalog WHERE price<%s ORDER BY price DESC;",price);
                   int rowCount_3 = esql.executeQueryAndPrintResult(query_3);
                }
                else{
                   System.out.println("wrong value");
                }
             break;
             default:
             break;
          }
 
       }
       catch(Exception e){
          System.err.println (e.getMessage());
       }
 
    }
    public static void placeOrder(GameRental esql,String authorisedUser) {
      try {
         BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
         System.out.print("Game ID?: ");
         String gameID = in.readLine();
         System.out.print("How Many?: ");
         String a_ = in.readLine();
         int amount = Integer.parseInt(a_);

         String query = String.format("SELECT price FROM Catalog WHERE gameID='%s'", gameID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (result.isEmpty() || result.get(0).isEmpty()) {
             System.out.println("Game ID not found.");
             return;
         }

         float price = Float.parseFloat(result.get(0).get(0));
         System.out.println("price: " + price);

         if (price > 0) {
             System.out.println(String.format("Total Price is %s", price * amount));
             int rentalOrderID = Math.abs(ThreadLocalRandom.current().nextInt() % 99999);
             Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
             Calendar calendar = Calendar.getInstance();
             calendar.setTime(currentTimestamp);
             calendar.add(Calendar.DAY_OF_MONTH, 14);
            Timestamp timestampDue = new Timestamp(calendar.getTimeInMillis());
             query = String.format("INSERT INTO RentalOrder (rentalOrderID, login, noOfGames, totalPrice, orderTimestamp, dueDate) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
                     rentalOrderID, authorisedUser, amount, price * amount, currentTimestamp, timestampDue);
             esql.executeUpdate(query);

             int trackingID = Math.abs(ThreadLocalRandom.current().nextInt() % 99999);
             query = String.format("INSERT INTO TrackingInfo (trackingID, rentalOrderID, status, currentLocation, courierName, lastUpdateDate, additionalComments) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                     trackingID, rentalOrderID, "ordered", "shop", "Name", currentTimestamp, " ");
             esql.executeUpdate(query);

             query = String.format("INSERT INTO GamesInOrder (rentalOrderID, gameID, unitsOrdered) VALUES ('%s', '%s', '%s');",
                     rentalOrderID, gameID, amount);
             esql.executeUpdate(query);
             
               query = String.format("SELECT * FROM RentalOrder WHERE login='%s';", authorisedUser);
             // Execute the query and print the results
             esql.executeQueryAndPrintResult(query);
             query = String.format("SELECT * FROM TrackingInfo WHERE rentalOrderID='%s';", rentalOrderID);
             esql.executeQueryAndPrintResult(query);
             query = String.format("SELECT * FROM GamesInOrder WHERE rentalOrderID='%s';", rentalOrderID);
             esql.executeQueryAndPrintResult(query);
             System.out.println("Finished");
         } else {
             System.out.println("Invalid price retrieved.");
         }
     } catch (Exception e) {
         e.printStackTrace();
         System.out.println("An error occurred: " + e.getMessage());
     }
    }
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
          String userLogin = in.readLine();
          //using rental orderID to check for a specific order
          System.out.println("Enter rental order ID");
          String rentalorderID = in.readLine();
          //verify it is the correct user 
          String verification_query = String.format("SELECT * FROM RentalOrder WHERE rentalOrderID = '%s' AND login = '%s';", rentalorderID, userLogin);
          int rows_affected = esql.executeQuery(verification_query);
 
          // retrieves rental order details if one or more exists
          if (rows_affected > 0){
             String rentalOrderDetailsQuery = String.format("SELECT rentalorderID, orderTimestamp, dueDate, totalPrice FROM RentalOrder WHERE rentalOrderID = '%s';",rentalorderID);
             esql.executeQueryAndReturnResult(rentalOrderDetailsQuery);
 
             String trackingInfoQuery = String.format("SELECT trackingID FROM TrackingInfo WHERE rentalOrderID = '%s';", rentalorderID); 
             esql.executeQueryAndPrintResult(trackingInfoQuery);
 
             String gamesListQuery = String.format(
                 "SELECT gameID, gameName, genre " +
                 "FROM Catalog " +
                 "WHERE gameID IN (SELECT gameID FROM GamesInOrder WHERE rentalOrderID = '%s');",rentalorderID);       
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
             String userLogin = in.readLine();
 
             // Ask the user to input a trackingID
             System.out.print("Enter the trackingID to view tracking information: ");
             String trackingID = in.readLine();
 
             // Construct the query to fetch tracking information for the given trackingID and ensure it belongs to the logged-in user
             String query = String.format(
                 "SELECT T.trackingID, T.rentalOrderID, T.status, T.currentLocation, T.courierName, T.lastUpdateDate, T.additionalComments " +
                 "FROM TrackingInfo T, RentalOrder R " +
                 "WHERE T.trackingID='%s' AND T.rentalOrderID=R.rentalOrderID AND R.login='%s';", 
                 trackingID, userLogin);
 
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
    public static void updateTrackingInfo(GameRental esql,String role) {
       try {
          if (!role.equals("employee") && !role.equals("manager")) {
                 System.out.println("You do not have permission to update tracking information.");
                 return;
             }
          System.out.println("Enter the trackinID you wish to update");
          System.out.println("1.Update status");
          System.out.println("2.Update currentLocation");
          System.out.println("3.Update courierName");
          System.out.println("4.Update additionalComments");
          switch(readChoice()){
             case 1:
             System.out.print("Enter the new status");
             String status = in.readLine();
             System.out.print("Enter the trackingID to view tracking information: ");
             String trackingID = in.readLine();
             String query1 = String.format("UPDATE TrackingInfo SET status='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",status, trackingID);
             esql.executeUpdate(query1);
             System.out.println("New status sucessfully updated");
          break;
             case 2:
             System.out.print("Enter the new currentLocation");
             String currentLocation = in.readLine();
            System.out.print("Enter the trackingID to view tracking information: ");
             String trackingID_2 = in.readLine();
             String query2 = String.format(
                     "UPDATE TrackingInfo SET currentLocation='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                     currentLocation, trackingID_2);
             esql.executeUpdate(query2);
             System.out.println("New currentLocation sucessfully updated");
          break;
          case 3:
             System.out.print("Enter the new courierName");
             String courierName = in.readLine();
             System.out.print("Enter the trackingID to view tracking information: ");
             String trackingID_3 = in.readLine();
             String query3 = String.format(
                     "UPDATE TrackingInfo SET courierName='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                     courierName, trackingID_3);
             esql.executeUpdate(query3);
             System.out.println("New courierName sucessfully updated");
          break;
          case 4:
             System.out.print("Enter the new additionalComments");
             String additionalComments = in.readLine();
             System.out.print("Enter the trackingID to view tracking information: ");
             String trackingID_4 = in.readLine();
             String query4 = String.format(
                     "UPDATE TrackingInfo SET additionalComments='%s', lastUpdateDate=CURRENT_TIMESTAMP WHERE trackingID='%s';",
                     additionalComments, trackingID_4);
             esql.executeUpdate(query4);
             System.out.println("New additionalComments sucessfully updated");
          break;
          }
          }
          catch (Exception e) {
             System.err.println(e.getMessage());
          }
       }
    public static void updateCatalog(GameRental esql) {
       try {
          System.out.println("1.Add new game");
          System.out.println("2.Change info of game");
          System.out.println("3.Remove game from catalog");
          switch (readChoice()) {
             case 1:
                     System.out.print("Enter game ID: ");
                     String gameID = in.readLine();
                     System.out.print("Enter game name: ");
                     String gameName = in.readLine();
                     System.out.print("Enter genre: ");
                     String genre = in.readLine();
                     System.out.print("Enter price: ");
                     Float price = new Float(in.readLine());
                     System.out.print("Enter description: ");
                     String description = in.readLine();
                     System.out.print("Enter image URL: ");
                     String imageURL = in.readLine();
                     String query = String.format("INSERT INTO Catalog (gameID, gameName, genre, price, description, imageURL) VALUES ('%s', '%s','%s', '%s', '%s', '%s')", gameID,gameName,genre,price,description,imageURL);
                     esql.executeUpdate(query);
                     System.out.println("Finished");
                     break;
             case 2:
             System.out.print("Enter game ID to update: ");
               String gameID_2 = in.readLine();

               System.out.print("Enter new game name: ");
               String gameName_2 = in.readLine();
               System.out.print("Enter new genre: ");
               String genre_2 = in.readLine();
               System.out.print("Enter new price: ");
               BigDecimal price_2 = new BigDecimal(in.readLine());
               System.out.print("Enter new description: ");
               String description_2 = in.readLine();
               System.out.print("Enter new image URL: ");
               String imageURL_2 = in.readLine();
               String query_2 = String.format("UPDATE Catalog SET gameName = '%s', genre = '%s', price = '%s', description = '%s', imageURL = '%s' WHERE gameID = '%s'", gameID_2,gameName_2,genre_2,price_2,description_2,imageURL_2);
               esql.executeUpdate(query_2);
               System.out.println("Finished");
             break;
             case 3:
               System.out.print("Enter game ID to remove: ");
               String gameID_3 = in.readLine();
               String query_3= String.format("DELETE FROM Catalog WHERE gameID = '%s", gameID_3);
               esql.executeUpdate(query_3);
               System.out.println("Finished");
               break;
             default:
                break;
          }
       } catch (Exception e) {
          System.err.println (e.getMessage());
       }

    }
    public static String updateUser(GameRental esql, String ID) {
       try{
          System.out.println("1.Change One's Login");
          System.out.println("2.Change One's Role");
          System.out.println("3.Change One's numOverDueGames");
          System.out.println("4.Change One's Password");
          System.out.println("5.Change One's FavGames");
          System.out.println("6.Change One's Phone number");
          switch (readChoice()) {
             case 1:
                System.out.print("Who do you want to Change:");
                String target_login = in.readLine();
                System.out.print("To what Id?:");
                String changed_login = in.readLine();
                if(changed_login.length()<=50){
                   String query = String.format("SELECT * FROM Worker WHERE login='%s';",target_login);
                   int n = esql.executeQuery(query);
                   if(n == 1){
                      query = String.format("Delete FROM Worker WHERE login='%s';",target_login);
                      esql.executeUpdate(query);
                      query = String.format("UPDATE Users SET login='%s' WHERE login='%s';",changed_login,target_login);
                      esql.executeUpdate(query);
                      query = String.format("INSERT INTO Worker (login) VALUES ('%s');",changed_login);
                      esql.executeUpdate(query);
                   }
                   else{
                      query = String.format("Delete FROM Customer WHERE login='%s';",target_login);
                      esql.executeUpdate(query);
                      query = String.format("UPDATE Users SET login='%s' WHERE login='%s';",changed_login,target_login);
                      esql.executeUpdate(query);
                      query = String.format("INSERT INTO Customer (login) VALUES ('%s');",changed_login);
                      esql.executeUpdate(query);
                   }
                   System.out.println("Changed Login");
                   ID=changed_login;
                }  
                else{
                   System.out.println("You put wrong login or too loog login");
                }
                break;
             case 2:
                System.out.print("Who do you want to Change:");
                String target_login_2 = in.readLine();
                System.out.print("To what Role?(customer,managers,employees):");
                String changed_Role = in.readLine();
                if(changed_Role.equals("customer")){
                   String query = String.format("UPDATE Users SET role = '%s' WHERE login='%s';",changed_Role,target_login_2);
                   esql.executeUpdate(query);
                   query = String.format("DELETE FROM Worker WHERE login='%s';",target_login_2);
                   esql.executeUpdate(query);
                   query = String.format("INSERT INTO Customer(login) VALUES('%s');",target_login_2);
                   esql.executeUpdate(query);
                   System.out.println("Changed Role");
                }
                else if(changed_Role.equals("managers") || changed_Role.equals("employees")){
                   String query = String.format("UPDATE Users SET role = '%s' WHERE login='%s';",changed_Role,target_login_2);
                   esql.executeUpdate(query);
                   query = String.format("DELETE FROM Customer WHERE login='%s';",target_login_2);
                   esql.executeUpdate(query);
                   query = String.format("INSERT INTO Worker(login) VALUES('%s');",target_login_2);
                   esql.executeUpdate(query);
                   System.out.println("Changed Role");
                }
                else{
                   System.out.println("wrong Role name");
                }
                break;
             case 3:
                System.out.print("Who do you want to Change:");
                String target_login_3 = in.readLine();
                System.out.print("To what number of over due games?:");
                int overDueGames = Integer.parseInt(in.readLine());
                if(overDueGames == (int)overDueGames){
                   String query = String.format("UPDATE Users SET numOverDueGames = '%s' WHERE login='%s';",overDueGames,target_login_3);
                   esql.executeUpdate(query);
                   System.out.println("Changed Number of over due games");
                }
                else{
                   System.out.println("Error: Put integer");
                }
                break;
             case 4:
                System.out.print("Who do you want to Change:");
                String target_login_4 = in.readLine();
                System.out.print("one's New password:");
                String password1 = in.readLine();
                System.out.print("Re New password:");
                String password2 = in.readLine();
                if(password1.equals(password2) && password1.length()<=30){
                   String query = String.format("UPDATE Users SET password = '%s' WHERE login='%s';",password1,target_login_4);
                   esql.executeUpdate(query);
                   System.out.println("Changed Password");
                }  
             break;
             case 5:
                System.out.print("Who do you want to Change:");
                String target_login_5 = in.readLine();
                System.out.println("Whats one's new Favorite Games?(put space between each game):");
                String favGames = in.readLine();
                String query = String.format("UPDATE Users SET favGames = '%s' WHERE login='%s';",favGames,target_login_5);
                esql.executeUpdate(query);
                System.out.println("Changed Favorite Games");
             break;
             case 6:
                System.out.print("Who do you want to Change:");
                String target_login_6 = in.readLine();
                System.out.print("one's New PhoneNumber:");
                String phonenumber = in.readLine();
                if(phonenumber.length()<=20){
                   query = String.format("UPDATE Users SET phoneNum = '%s' WHERE login='%s';",phonenumber,target_login_6);
                   esql.executeUpdate(query);
                   System.out.println("Changed PhoneNumber");
                }
                else{
                   System.out.println("Too long PhoneNumber");
                }
                break;
             default:
             break;
          }
       }catch(Exception e){
          System.err.println (e.getMessage());
       }
       return ID;
    }

 } //end GameRe