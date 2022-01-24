// Created by: Andrew Bell
// Project Date: 1/18/22

// This is the initial(super) class for Project Zero, where I create a Scala CLI (command line interpreter)
// application that supports database integration, mainly using mySQl. This app will create a text-based adventure
// game in which the maps, choices, and results are imported from a mySQL database.

//import needed packages below
package Project_Zero
import Console.{BOLD, RESET, UNDERLINED}
import scala.io.StdIn._
import java.sql.{Connection,DriverManager,ResultSet,Statement}

// created an object with main
// and some function calls to handle game startup, running, and ending
object RunGame {

  def main(args: Array[String]): Unit = {
    // body of main goes here for run time

   // startupFunctions.runStartUp()

    //below is for testing connection

     // Connect_to_DB.connect()

    //below is for checking database being used is correct

    //  Connect_to_DB.show_Databases()

    Connect_to_DB.show_Usernames()

    //sys.exit(0)
  }

}

//create function definitions for startup
object startupFunctions {

  def runStartUp(): Unit = {
    startupFunctions.titlescreen()
    startupFunctions.titleScreenInputs()
  }

  //display title screen and list of options
  def titlescreen(): Unit = {
    val gameName = "TrailBlazer"
    println("********************************")
    print("*********")
    print(s"${BOLD} ${UNDERLINED}$gameName${RESET}")
    println(" **********")
    println("********************************")
    println()
    println("\t" + "\t" + "     Login")
    println("\t" + "\t" + "    Options")
    println("\t" + "\t" + "     Exit")
    println()
  }


  // get user input and compare to list of options and open based on comparison
  // options include login, options, and exit
  def titleScreenInputs(): Unit = {
    val titleInput = userCommands.getInput()

    if (titleInput == "login") {
      println("Login selected" + "\n")
      loginFunctions.loginScreen()
      loginFunctions.checkUsername()
    } else if (titleInput == "options") {
      println("Options selected" + "\n")
    } else if (titleInput == "exit") {
      println("Exit selected" + "\n" + "\n")
      sys.exit(0)
    } else {
      println("I'm sorry, that is not one of the options above.")
      startupFunctions.titleScreenInputs()
    }
  }


  // accounts table holds username, emailaddress, and password in sql with the possibility of adding saved state later

}

//create function definitions for logging in
object loginFunctions {
  //display login screen and ask for username
  def loginScreen(): Unit = {
    val stringName = "LOGIN"
    println("********************************")
    print("*************")
    print(s"${BOLD} ${UNDERLINED}$stringName${RESET}")
    println(" ************")
    println("********************************")
    println()
  }

  def checkUsername(): Unit = {
    val nameInput = userCommands.getUserName()

    if (nameInput == "back") {
      println("\n" +"\n")
      startupFunctions.runStartUp()
    } else {
      println("hello")
    }

  }


}

//create function definitions for options
object optionsFunctions {
  def optionsScreen(): Unit = {
    val stringName = "OPTIONS"
    println("********************************")
    print("*********")
    print(s"${BOLD} ${UNDERLINED}$stringName${RESET}")
    println(" **********")
    println("********************************")
    println()
    println("\t" + "\t" + "     Login")
    println("\t" + "\t" + "    Options")
    println("\t" + "\t" + "     Exit")
    println()
  }


}


//create functions for running the game after logging in


// create a list of functions users can implement
object userCommands {

  //this function is for any user string inputs, returns lowercase string for boolean comparisons
  def getInput(): String = {
    val inputString = readLine("Please enter one of the above options: ")
    val inputToLower = inputString.toLowerCase
    return inputToLower
  }

  def getUserName(): String = {
    val inputString = readLine("Please enter existing username or 'back' to go back: ")
    val inputToLower = inputString.toLowerCase
    return inputToLower
  }

}



//create function to connect to database

object Connect_to_DB {
  var connection:Connection = _
  def connect(): Unit = {
    val jdbcDatabase = "mysql"
    val url = s"jdbc:mysql://localhost/${jdbcDatabase}"
    // val driver = "com.mysql.jdbc.Driver" <- not necessary when added to classpath
    val username = "root"
    val password = "Z3r02021!!"

    try {
      // make the connection
      //Class.forName(driver) <- not necessary when added to classpath
      connection = DriverManager.getConnection(url, username, password)

      // create the statement, and run the select query
      val statement = connection.createStatement()
      val resultSet = statement.executeQuery("SELECT host, user FROM user")
      while ( resultSet.next() ) {
        val host = resultSet.getString("host")
        val user = resultSet.getString("user")
        println("host, user = " + host + ", " + user)
      }
    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close()
  }

  //this function was created for debugging purposes
  def show_Databases(): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/${jdbcDatabase}"
    val username = "root"
    val password = "Z3r02021!!"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      println("Connection established......")
      // create the statement, and run the select query
      val statement = connection.createStatement()
      //retrieve the data
      val resultSet = statement.executeQuery("Show Databases")
      println("List of databases: ")
      while ( resultSet.next() ) {
        print(resultSet.getString(1))
        println()
      }
      //get tables from database
     // val dbSet = statement.executeQuery("Use myfirst")
      val tableSet = statement.executeQuery("Show Tables")
      println("List of tables: ")
      while ( tableSet.next() ) {
        print(tableSet.getString(1))
        println()
      }


    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close()
  }


  def show_Usernames(): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/${jdbcDatabase}"
    val username = "root"
    val password = "Z3r02021!!"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      println("Connection established......")
      // create the statement, and run the select query
      val statement = connection.createStatement()
      //retrieve the data
      //get tables from database
      // val dbSet = statement.executeQuery("Use myfirst")
      val tableSet = statement.executeQuery("Show Tables")
      println("List of tables: ")
      while ( tableSet.next() ) {
        print(tableSet.getString(1))
        println()
      }

      val userSet = statement.executeQuery("SELECT username FROM projectzero.`project0-accounts`")
      println("List of Usernames: ")
      while ( userSet.next() ) {
        print(userSet.getString(1))
        println()
      }


    } catch {
      case e: Exception => e.printStackTrace
    }
    connection.close()
  }




}

