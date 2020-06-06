import java.sql.*;
import java.util.Scanner;

public class jdbc_example {

    // The instance variables for the class
    private static Connection connection;
    private static Statement statement;
    private static jdbc_example test = new jdbc_example();
    private static Scanner scan = new Scanner(System.in);
    
    private static String city;
    private static int decision;
    private static int a_id;
    private static String a_name;
    private static String a_city;
    private static int a_zip;
    private static int purchased_id;

    // The constructor for the class
    public jdbc_example() {
        connection = null;
        statement = null;
        decision = 0;
        city = "";
        a_id = 0;
        a_name = "";
        a_city = "";
        a_zip = 0;
        purchased_id = 0;
    }
   
    // Collect user input from main menu
    public void getUserInput() {
                
        // Print out menu options
        System.out.println("\n1.) Find all existing agents in a given city");
        System.out.println("2.) Purchase an available policy from a particular agent");
        System.out.println("3.) List all policies sold by a particular agent");
        System.out.println("4.) Cancel a policy");
        System.out.println("5.) Add a new agent for a city");
        System.out.println("6.) Quit");
        System.out.print("What would you like to do? ");
        
        // Read and collect the user's decision; try catch used if user enters a non-int
        try {
            decision = Integer.valueOf(scan.nextLine());
        } catch(Exception e) {
            System.out.println("ERROR: Incorrect menu option was entered. Program terminating. . .");
            System.exit(1);
        }
        
        // Error check user input
        if(decision < 1 || decision > 6) {
            System.out.println("ERROR: Incorrect menu option was entered. Program terminating. . .");
            System.exit(1);
        }
    }
    
    // Use a switch statement to call proper functions
    public void menuAction(int num, jdbc_example jdbc_example1) throws SQLException {
        switch(num) {
            case 1:
                System.out.print("Enter City Name: ");
                city = scan.next();
                break;
            case 2:
                
                break;
            case 3:
                System.out.println(decision);
                jdbc_example1.listPoliciesByAgent(jdbc_example1);
                break;
            case 4:
                System.out.println(decision);
                jdbc_example1.cancelPolicy(jdbc_example1);
                break;
            case 5:
                System.out.print("Enter A_ID: ");
                a_id = Integer.valueOf(scan.nextLine());
                System.out.print("Enter A_NAME: ");
                a_name = scan.next();
                System.out.print("Enter A_CITY: ");
                a_city = scan.next();
                System.out.print("Enter A_ZIP: ");
                a_zip = Integer.valueOf(scan.nextLine());
                break;
            case 6:
                System.out.println("Program Successfully Terminated");
                test.disConnect();
                System.exit(0);
                break;
            default:
                System.exit(1);
                break;
        }
    }

    // The main program that tests the methods
    public static void main(String[] args) throws SQLException {        
        String Username = "mshartst";
        String mysqlPassword = "Razorbacks20!";

        test.connect(Username, mysqlPassword);
        test.initDatabase(Username, mysqlPassword, Username);
        
        // Collect user information
        test.getUserInput();
        test.menuAction(decision,test);

        //String query1 = "SELECT *FROM CLIENTS";
        //String query3 = "SELECT *FROM AGENTS";
        //String query3 = "SELECT *FROM POLICY";
        //String query4 = "SELECT *FROM POLICIES_SOLD";
        
        //test.query(query1);
        //test.query(query2);
        //test.query(query3);
        //test.query(query4);
        
        if(decision == 1) // #1 - Find all existing agents in a given city
        {
            String query1 = "SELECT *FROM CLIENTS WHERE C_CITY = '" + city + "'";
            String query2 = "SELECT *FROM AGENTS WHERE A_CITY = '" + city + "'";
            test.query(query1);
            test.query(query2);
        }
        
        
        
        
//      if(decision == 4)   // #4 - Cancel a policy
//      {
//          //String query3 = "SELECT *FROM POLICIES_SOLD";
//          //test.query(query3);
//          String query3 = "DELETE FROM POLICIES_SOLD WHERE PURCHASED_ID = '" + purchased_id + "'";
//          test.query(query3);
//      }
        
        if(decision == 5)   // #5 - Add a new agent for a city
        {
            String t = "AGENTS";
            String v = a_id + ", '" + a_name + "', " + "'" + a_city + "'," + a_zip; 
            test.insert(t, v);
        }
        
        if(decision == 6) //#6 - Quit
        {
            System.out.println("Program Terminating.");
            System.exit(0);
        }
        
        String query10 = "SELECT *FROM AGENTS";
        test.query(query10);
        
        // Close scanner object and disconnect
//      scan.close();
        test.disConnect();
    }
    public void cancelPolicy(jdbc_example jdbc_example1) throws SQLException{
        Scanner input = new Scanner(System.in);
        String displayQuery =  "SELECT *FROM POLICIES_SOLD";
        int purchaseId = 0;
        jdbc_example1.query(displayQuery);
        System.out.println("Enter PURCHASE_ID");
        purchaseId = Integer.valueOf(input.nextLine());
        String deleteQuery = "DELETE FROM POLICIES_SOLD WHERE PURCHASE_ID = " + purchaseId;
        jdbc_example1.queryUpdate(deleteQuery);
        jdbc_example1.query(displayQuery);
        getUserInput();
    }
        public void listPoliciesByAgent(jdbc_example jdbc_example1) throws SQLException {
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet table = dbm.getTables(null, null, "AGENTS", null);
        if(table.next()){
            System.out.println("Table exists");
        }
        else{
            System.out.println("Table does not exist");
        }
        Scanner input = new Scanner(System.in);
        System.out.println("Enter name of agent");
        String name = input.nextLine();
        System.out.println("Enter name of city");
        String city = input.nextLine();
        String findingNameQuery;
        // findingNameQuery = "SELECT *FROM AGENTS WHERE A_NAME = '" + name + "'" +"AND CITY = '" + city + "'";
        findingNameQuery = "SELECT NAME, TYPE, COMMISSION_PERCENTAGE FROM POLICY WHERE POLICY_ID = (SELECT POLICY_ID FROM POLICIES_SOLD WHERE AGENT_ID= (SELECT A_ID FROM AGENTS WHERE A_NAME = '" + name + "' AND A_CITY = '" + city + "'))";
        jdbc_example1.query(findingNameQuery);
        // jdbc_example1.query(findingNameQuery);
        getUserInput();
    }
    // Connect to the database
    public void connect(String Username, String mysqlPassword) throws SQLException {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + Username + "?" +
                    "user=" + Username + "&password=" + mysqlPassword);
        }
        catch (Exception e) {
            throw e;
        }
    }

    // Disconnect from the database
    public void disConnect() throws SQLException {
        connection.close();
        statement.close();
    }

    // Execute an SQL query passed in as a String parameter
    // and print the resulting relation
    public void query(String q) {
        try {
            ResultSet resultSet = statement.executeQuery(q);
            System.out.println("\n---------------------------------");
            System.out.println("Query: \n" + q + "\n\nResult: ");
            print(resultSet);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void queryUpdate(String q) {
        try {
            statement.executeUpdate(q);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Print the results of a query with attribute names on the first line
    // Followed by the tuples, one per line
    public void print(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int numColumns = metaData.getColumnCount();

        printHeader(metaData, numColumns);
        printRecords(resultSet, numColumns);
    }

    // Print the attribute names
    public void printHeader(ResultSetMetaData metaData, int numColumns) throws SQLException {
        for (int i = 1; i <= numColumns; i++) {
            if (i > 1)
                System.out.print(",  ");
            System.out.print(metaData.getColumnName(i));
        }
        System.out.println();
    }

    // Print the attribute values for all tuples in the result
    public void printRecords(ResultSet resultSet, int numColumns) throws SQLException {
        String columnValue;
        while (resultSet.next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (i > 1)
                    System.out.print(",  ");
                columnValue = resultSet.getString(i);
                System.out.print(columnValue);
            }
            System.out.println("");
        }
    }

    // Insert into any table, any values from data passed in as String parameters
    public void insert(String table, String values) {
        String query = "INSERT into " + table + " values (" + values + ")" ;
        try {
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Remove all records and fill them with values for testing
    // Assumes that the tables are already created
    public void initDatabase(String Username, String Password, String SchemaName) throws SQLException {
        statement = connection.createStatement();

        // This was used on last semesters assignment but will be used to answer the
        // questions on the problem statement
        
    /*
        statement.executeUpdate("DELETE from FoodOrder");
        statement.executeUpdate("DELETE from MenuItem");
        statement.executeUpdate("DELETE from Dish");
        statement.executeUpdate("DELETE from Restaurant");

        insert("Restaurant", "0, 'Tasty Thai', 'Asian', 'Dallas'");
        insert("Restaurant", "3,'Eureka Pizza','Pizza', 'Fayetteville'");
        insert("Restaurant", "5,'Tasty Thai','Asian', 'Las Vegas'");

        insert("Dish", "13,'Spring Roll','ap'");
        insert("Dish", "15,'Pad Thai','en'");
        insert("Dish", "16,'Pad Stickers','ap'");
        insert("Dish", "22,'Masaman Curry','en'");
        insert("Dish", "10,'Custard','ds'");
        insert("Dish", "12,'Garlic Bread','ap'");
        insert("Dish", "44,'Salad','ap'");
        insert("Dish", "07,'Cheese Pizza','en'");
        insert("Dish", "19,'Pepperoni Pizza','en'");
        insert("Dish", "77,'Vegi Supreme Pizza','en'");

        insert("MenuItem", "0,0,13,8.00");
        insert("MenuItem", "1,0,16,9.00");
        insert("MenuItem", "2,0,44,10.00");
        insert("MenuItem", "3,0,15,19.00");
        insert("MenuItem", "4, 0,22,19.00");
        insert("MenuItem", "5, 3,44,6.25");
        insert("MenuItem", "6, 3,12,5.50");
        insert("MenuItem", "7, 3,7,12.50");
        insert("MenuItem", "8, 3,19,13.50");
        insert("MenuItem", "9,5,13,6.00");
        insert("MenuItem", "10,5,15,15.00");
        insert("MenuItem", "11,5,22,14.00");

        insert("FoodOrder", "0,2,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '10:30'");
        insert("FoodOrder", "1,0,STR_To_DATE('02,03,2017', '%d,%m,%Y'), '15:33'");
        insert("FoodOrder", "2,3,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '15:35'");
        insert("FoodOrder", "3,5,STR_To_DATE('03,03,2017', '%d,%m,%Y'), '21:00'");
        insert("FoodOrder", "4,7,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '18:11'");
        insert("FoodOrder", "5,7,STR_To_DATE('04,03,2017', '%d,%m,%Y'), '18:51'");
        insert("FoodOrder", "6,9,STR_To_DATE('01,03,2017', '%d,%m,%Y'), '19:00'");
        insert("FoodOrder", "7,11,STR_To_DATE('05,03,2017', '%d,%m,%Y'), '17:15'");
        */
    }
}