// DO:  more ~username/.my.cnf to see your password
// CHANGE:  MYUSERNAME and MYMYSQLPASSWORD to your username and mysql password
// COMPILE:  g++ -Wall -I/usr/include/cppconn -o odbc_example odbc_example.cpp -L/usr/lib -lmysqlcppconn
// RUN:      ./odbc_example
#include "mysql_connection.h"
#include <cppconn/driver.h>
#include <cppconn/exception.h>
#include <cppconn/resultset.h>
#include <cppconn/metadata.h>
#include <cppconn/resultset_metadata.h>
#include <cppconn/statement.h>
#include <cppconn/prepared_statement.h>
 
using namespace std;
 
sql::Driver *driver;
sql::Connection *con;
sql::Statement *statement;
sql::ResultSet *resultSet;
sql::ResultSetMetaData *metaData;
sql::Connection* Connect(string, string);
 
void insert(string table, string values );
void initDatabase(const string Username, const string Password, const string SchemaName);
void query (string q);
void print (sql::ResultSet *resultSet);
void disconnect();
void printRecords(sql::ResultSet *resultSet, int numColumns);
void printHeader(sql::ResultSetMetaData *metaData, int numColumns);
 

int main() 
{
 
    string Username = "MYUSERNAME";             // Change to your own username
    string mysqlPassword = "MYMYSQLPASSWORD";   // Change to your own mysql password

    con = Connect (Username, mysqlPassword);
    initDatabase(Username, mysqlPassword, Username);
 
    string query1 = "SELECT * from Dish";
    string query2 = "SELECT restaurantName, city, dishName, price  FROM Restaurant, Dish, MenuItem WHERE MenuItem.restaurantNo=Restaurant.restaurantID AND MenuItem.dishNo=Dish.dishNo";

    query(query1);
    query(query2);
    disconnect();
}   

// Connect to the database
sql::Connection* Connect(const string Username, const string Password)
 {
	 try{
	
		driver = get_driver_instance();
		con = driver->connect("tcp://127.0.0.1:3306", Username, Password);
		}
		 
	catch (sql::SQLException &e) {
        cout << "ERROR: SQLException in " << __FILE__;
        cout << " (" << __func__<< ") on line " << __LINE__ << endl;
        cout << "ERROR: " << e.what();
        cout << " (MySQL error code: " << e.getErrorCode();
        cout << ", SQLState: " << e.getSQLState() << ")" << endl;
        }
   return con;
}

// Disconnect from the database
void disconnect()
{
		delete resultSet;
		delete statement;
		con -> close();
		delete con;
} 

// Execute an SQL query passed in as a string parameter
// and print the resulting relation
void query (string q)
{
        try {
            resultSet = statement->executeQuery(q);
            cout<<("\n---------------------------------\n");
            cout<<("Query: \n" + q + "\n\nResult: \n");
            print(resultSet);
        }
        catch (sql::SQLException e) {
          
	    cout << "ERROR: SQLException in " << __FILE__;
        cout << " (" << __func__<< ") on line " << __LINE__ << endl;
        cout << "ERROR: " << e.what();
        cout << " (MySQL error code: " << e.getErrorCode();
        cout << ", SQLState: " << e.getSQLState() << ")" << endl;
        }
}
 
// Print the results of a query with attribute names on the first line
// Followed by the tuples, one per line
void print (sql::ResultSet *resultSet) 
{
    try{
		if (resultSet -> rowsCount() != 0)
		{
   		   sql::ResultSetMetaData *metaData = resultSet->getMetaData();
           int numColumns = metaData->getColumnCount();
		   printHeader( metaData, numColumns);
           printRecords( resultSet, numColumns);
		}
        else
			throw runtime_error("ResultSetMetaData FAILURE - no records in the result set");
    }
	catch (std::runtime_error &e) {
    }
    
}	

// Print the attribute names
void printHeader(sql::ResultSetMetaData *metaData, int numColumns)
{    
	/*Printing Column names*/  
    for (int i = 1; i <= numColumns; i++) {
            if (i > 1)
                cout<<",  ";
            cout<< metaData->getColumnLabel(i); //ColumnName
        }
        cout<<endl;
}		

// Print the attribute values for all tuples in the result
void printRecords(sql::ResultSet *resultSet, int numColumns)   
{ 
        while (resultSet->next()) {
            for (int i = 1; i <= numColumns; i++) {
                if (i > 1)
                    cout<<",  ";
                cout<< resultSet->getString(i);
               ;
            }
        cout<<endl;
        }
}
 
// Insert into any table, any values from data passed in as String parameters
void insert(const string table, const string values) 
{
    string query = "INSERT into " + table + " values (" + values + ")";
    statement->executeUpdate(query);
}
 

// Remove all records and fill them with values for testing
// Assumes that the tables are already created
void initDatabase(const string Username, const string Password, const string SchemaName) 
{
        // Create a connection 
        driver = get_driver_instance();
        con = driver->connect("tcp://127.0.0.1:3306", Username, Password);

        // Connect to the MySQL test database 
        con->setSchema(SchemaName);
 
        statement = con->createStatement();
        statement->executeUpdate("DELETE from FoodOrder");
        statement->executeUpdate("DELETE from MenuItem");
        statement->executeUpdate("DELETE from Dish");
        statement->executeUpdate("DELETE from Restaurant");

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
}
