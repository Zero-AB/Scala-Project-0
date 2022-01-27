// Created by: Andrew Bell
// Project Date: 1/18/22

// This is the initial(super) class for Project Zero, where I create a Scala CLI (command line interpreter)
// application that supports database integration, mainly using mySQl. This app will create a text-based adventure
// game in which the maps, choices, and results are imported from a mySQL database.

//import needed packages below
package Project_Zero
import Console.{BOLD, RESET, UNDERLINED}
import scala.io.StdIn._
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, Statement, Timestamp}
import scala.annotation.tailrec

// created an object with main
// and some function calls to handle game startup, running, and ending
object RunGame {

  def main(args: Array[String]): Unit = {
    // body of main goes here for run time

    startupFunctions.runStartUp()

    //Connect_to_DB.create_NewJournalEntry("pastelcub",120,80,67,"Good day today!")

    //below is for testing connection

     // Connect_to_DB.connect()

    //below is for checking database being used is correct

    //  Connect_to_DB.show_Databases()

    // Connect_to_DB.show_Usernames()

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
    print(s"$BOLD $UNDERLINED$gameName$RESET")
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
  @tailrec
  def titleScreenInputs(): Unit = {
    val titleInput = userCommands.getInput

    if (titleInput == "login") {
      println("Login selected" + "\n")
      loginFunctions.loginScreen()
      loginFunctions.checkUsername()
    } else if (titleInput == "options") {
      println("Options selected" + "\n")
      optionsFunctions.optionsScreen()
      optionsFunctions.optionScreenInputs()
    } else if (titleInput == "exit") {
      println("Exit selected" + "\n" + "\n")
      sys.exit(0)
    } else {
      println("I'm sorry, that is not one of the options above.")
      startupFunctions.titleScreenInputs()
    }
  }

}

//create function definitions for logging in
object loginFunctions {
  //display login screen and ask for username
  def loginScreen(): Unit = {
    val stringName = "LOGIN"
    println("********************************")
    print("*************")
    print(s"$BOLD $UNDERLINED$stringName$RESET")
    println(" ************")
    println("********************************")
    println()
  }

  @tailrec
  def checkUsername(): Unit = {
    val nameInput = userCommands.getUserName

    if (nameInput == "back") {
      println("\n" +"\n")
      startupFunctions.runStartUp()
    } else {
      val checkedName = Connect_to_DB.check_Username(nameInput)
      if (checkedName) {
        println("\n" + "Continue Logging in" + "\n")
        checkPassword(nameInput)
      } else {
        println("I'm sorry, there is no record of that username.")
        loginFunctions.checkUsername()
      }

    }
  }


  @tailrec
  def checkPassword(authUser: String): Unit = {
    val passwordInput = userCommands.getUserPassword(authUser)

    if (passwordInput == "back") {
      println("\n" +"\n")
      startupFunctions.runStartUp()
    } else {
      val checkedPassword = Connect_to_DB.check_Password(authUser,passwordInput)
      if (checkedPassword) {
        println("\n" + "Logged in" + "\n")
        Connect_to_DB.create_UserJournal(authUser)
        journalFunctions.journalTitleScreen(authUser)
      } else {
        println("I'm sorry, that password is incorrect.")
        loginFunctions.checkPassword(authUser)
      }

    }
  }



}

//create function definitions for options
object optionsFunctions {
  def optionsScreen(): Unit = {
    val stringName = "OPTIONS"
    println("********************************")
    print("************")
    print(s"$BOLD $UNDERLINED$stringName$RESET")
    println(" ***********")
    println("********************************")
    println()
    println("\t" + "     Create New User")
    println("\t" + "    Change User Password")
    println("\t" + "    Delete Existing User")
    println("\t" + "\t" + "      Back")
    println()
  }

  @tailrec
  def optionScreenInputs(): Unit = {
    val optionsInput = userCommands.getInput

    if (optionsInput == "create new user") {
      println("Create new user selected" + "\n")
      createUser()
    } else if (optionsInput == "change user password") {
      println("change user password selected" + "\n")
      changePass()
    } else if (optionsInput == "delete existing user") {
      println("Delete existing user selected" + "\n")
      deleteUser()
    } else if (optionsInput == "back") {
      println("Back selected" + "\n" + "\n")
      startupFunctions.runStartUp()
    } else {
      println("I'm sorry, that is not one of the options above.")
      optionsFunctions.optionScreenInputs()
    }
  }

  @tailrec
  def createUser(): Unit = {
    val newNameInput = userCommands.getNewUserName

    if (newNameInput == "back") {
      println("\n" +"\n")
      optionsScreen()
      optionScreenInputs()
    } else {
      val checkedNewName = Connect_to_DB.check_Username(newNameInput)
      if (checkedNewName) {
        println("I'm sorry, that username already exists.")
        createUser()
      } else {
        val nameString = readLine("Please enter your name: ")
        println()
        val emailString = readLine("Please enter your email address: ")
        println()
        val passwordString = readLine("Please enter your password: ")
        Connect_to_DB.create_NewUser(newNameInput,nameString,emailString,passwordString)
        println("\n" + "User Created" + "\n")
        optionsScreen()
        optionScreenInputs()
      }

    }

  }

  @tailrec
  def changePass(): Unit = {
    val changePassNameInput = userCommands.getUserName

    if (changePassNameInput == "back") {
      println("\n" +"\n")
      optionsScreen()
      optionScreenInputs()
    } else {
      val checkedChangePassName = Connect_to_DB.check_Username(changePassNameInput)
      if (checkedChangePassName) {
        val changePasswordInput = userCommands.getUserPassword(changePassNameInput)
        println("\n" + "Password Input" + "\n")
        if (changePasswordInput == "back") {
          println("\n" + "\n")
          optionsScreen()
          optionScreenInputs()
        } else {
          val checkedChangePassword = Connect_to_DB.check_Password(changePassNameInput, changePasswordInput)
          if (checkedChangePassword) {
            val newPassw = userCommands.getNewPass
            Connect_to_DB.change_ExistingPass(changePassNameInput,newPassw)
            println("\n" + "Password Changed" + "\n")
            optionsScreen()
            optionScreenInputs()
          } else {
            println("I'm sorry, that password is incorrect.")
            changePass()
          }
        }
      } else {
        println("I'm sorry, there is no record of that username.")
        changePass()
      }
    }


  }

  @tailrec
  def deleteUser(): Unit = {
    val deleteNameInput = userCommands.getUserName

    if (deleteNameInput == "back") {
      println("\n" +"\n")
      optionsScreen()
      optionScreenInputs()
    } else {
      val checkedDeleteName = Connect_to_DB.check_Username(deleteNameInput)
      if (checkedDeleteName) {
        val deletePasswordInput = userCommands.getUserPassword(deleteNameInput)
        println("\n" + "Password Input" + "\n")
        if (deletePasswordInput == "back") {
          println("\n" +"\n")
          optionsScreen()
          optionScreenInputs()
        } else {
          val checkedDeletePassword = Connect_to_DB.check_Password(deleteNameInput,deletePasswordInput)
          if (checkedDeletePassword) {
            Connect_to_DB.delete_ExistingUser(deleteNameInput)
            println("\n" + "User Deleted" + "\n")
            optionsScreen()
            optionScreenInputs()
          } else {
            println("I'm sorry, that password is incorrect.")
            deleteUser()
          }

        }
      } else {
        println("I'm sorry, there is no record of that username.")
        deleteUser()
      }

    }


  }

}


//create functions for running the program after logging in
object journalFunctions {
  //display journal title screen and list of options
  @tailrec
  def journalTitleScreen(userID: String): Unit = {
    val userLoggedIN = Connect_to_DB.get_RealName(userID)
    val journalTitle = s"$userLoggedIN's Blood Pressure Journal"
    println("**************************************************************")
    print("  *********")
    print(s"$BOLD $UNDERLINED$journalTitle$RESET")
    println(" **********")
    println("**************************************************************")
    println()
    println("\t" + "\t" + "\t" + "\t" + "        Review Entries")
    println("\t" + "\t" + "\t" + "\t" + "         Create Entry")
    println("\t" + "\t" + "\t" + "\t" + "         Delete Entry")
    println("\t" + "\t" + "\t" + "\t" + "             Exit")
    println()

    val journalInput = userCommands.getInput

    if (journalInput == "review entries") {
      println("Review Entries selected" + "\n")
      reviewEntries(userID)
    } else if (journalInput == "create entry") {
      println("Create Entry selected" + "\n")
      createEntry(userID)
    } else if (journalInput == "delete entry") {
      println("Delete Entry selected" + "\n")
      deleteEntry(userID)
    } else if (journalInput == "exit") {
      println("Exit selected" + "\n" + "\n" + "Thank you for using your journal!")
      sys.exit(0)
    } else {
      println("I'm sorry, that is not one of the options above.")
      journalTitleScreen(userID)
    }
  }

  def reviewEntries(loggedID: String): Unit = {
    Connect_to_DB.show_AllEntries(loggedID)
    val entrynum = userCommands.getEntryNumber
    val checkedNumber = Connect_to_DB.check_EntryNum(entrynum,loggedID)

    if (checkedNumber) {
      println("Entry Found")
      //show entry
      Connect_to_DB.show_SingleEntry(loggedID,entrynum)
      journalTitleScreen(loggedID)
    }
    else {
      if (entrynum > 0) {println("I'm sorry, that is not a valid entry number")}
      journalTitleScreen(loggedID)
    }

  }

  def createEntry(loggedID: String): Unit = {
    val upperBP = userCommands.getUpperPressure
    val lowerBP = userCommands.getLowerPressure
    val heartrate = userCommands.getHeartRate
    val notesString = readLine("Please enter any additional notes: ")
    println()
    //create entry
    Connect_to_DB.create_NewJournalEntry(loggedID,upperBP,lowerBP,heartrate,notesString)
    println("Entry Created")
    journalTitleScreen(loggedID)

  }

  def deleteEntry(loggedID: String): Unit = {
    Connect_to_DB.show_AllEntries(loggedID)
    val entrynumber = userCommands.getEntryNumber
    val checkedNum = Connect_to_DB.check_EntryNum(entrynumber,loggedID)

    if (checkedNum) {
      //delete entry
      println("Entry Deleted")
      journalTitleScreen(loggedID)
    }
    else {
      if (entrynumber > 0) {println("I'm sorry, that is not a valid entry number")}
      journalTitleScreen(loggedID)
    }

  }

}


// create a list of functions users can implement
object userCommands {

  //this function is for any user string inputs, returns lowercase string for boolean comparisons
  def getInput: String = {
    val inputString = readLine("Please enter one of the above options: ")
    println()
    val inputToLower = inputString.toLowerCase
    inputToLower
  }

  def getUserName: String = {
    val inputString = readLine("Please enter existing username or 'back' to go back: ")
    val inputToLower = inputString.toLowerCase
    inputToLower
  }

  def getNewUserName: String = {
    val inputString = readLine("Please enter new username or 'back' to go back: ")
    val inputToLower = inputString.toLowerCase
    inputToLower
  }

  def getUserPassword(aUser: String): String = {
    val inputPass = readLine(s"Please enter the password for $aUser or 'back' to go back: ")
    inputPass
  }

  def getNewPass: String = {
    val inputString = readLine("Please enter new password: ")
    val inputToLower = inputString.toLowerCase
    inputToLower
  }

  @tailrec
  def getEntryNumber: Int = {
    print("Please enter the number of the entry you would like to select (enter 0 to go back to journal menu: ")
    try {
      var inputInt = readInt()
      while(inputInt < 0) {
        print("Please enter a valid entry: ")
        inputInt = readInt()
      }
      inputInt
    }catch {
      case _: NumberFormatException =>
        println("That is not a valid entry!")
        getEntryNumber
    }
  }

  @tailrec
  def getUpperPressure: Int = {
    print("Please enter your upper/systolic blood pressure: ")
    try {
      var inputInt = readInt()
      while(inputInt < 60) {
        print("Please enter a valid entry: ")
        inputInt = readInt()
      }
      inputInt
    }catch {
      case _: NumberFormatException =>
        println("That is not a valid entry!")
        getUpperPressure
    }
  }


  @tailrec
  def getLowerPressure: Int = {
    print("Please enter your lower/diastolic blood pressure: ")
    try {
      var inputInt = readInt()
      while(inputInt < 20) {
        print("Please enter a valid entry: ")
        inputInt = readInt()
      }
      inputInt
    }catch {
      case _: NumberFormatException =>
        println("That is not a valid entry!")
        getLowerPressure
    }
  }


  @tailrec
  def getHeartRate: Int = {
    print("Please enter your heart rate: ")
    try {
      var inputInt = readInt()
      while(inputInt < 30) {
        print("Please enter a valid entry: ")
        inputInt = readInt()
      }
      inputInt
    }catch {
      case _: NumberFormatException =>
        println("That is not a valid entry!")
        getHeartRate
    }
  }

}



//create function to connect to database

object Connect_to_DB {
  var connection:Connection = _
  def connect(): Unit = {
    val jdbcDatabase = "mysql"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    // val driver = "com.mysql.jdbc.Driver" <- not necessary when added to classpath
    val username = "root"
    val password = "abcd1234"

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
      case e: Exception => e.printStackTrace()
    }
    connection.close()
  }

  //this function was created for debugging purposes
  def show_Databases(): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

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
      case e: Exception => e.printStackTrace()
    }
    connection.close()
  }


  def show_Usernames(): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

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
      case e: Exception => e.printStackTrace()
    }
    connection.close()
  }



  def check_Username(enteredUser: String): Boolean = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    var checkedName: Boolean = false

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      //use to go through usernames and set boolean variable if found
      val userSet = statement.executeQuery("SELECT username FROM projectzero.`project0-accounts`")
      while ( userSet.next() ) {
        if (userSet.getString(1).toLowerCase == enteredUser) {
         checkedName = true
        }
      }

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

    checkedName
  }

  def check_Password(enteredUID: String, enteredPass: String): Boolean = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    var checkedPass: Boolean = false

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      //use to go through usernames and set boolean variable if found
      val userSet = statement.executeQuery(s"SELECT password FROM projectzero.`project0-accounts` where username = '$enteredUID';")
      while ( userSet.next() ) {
       // print(userSet.getString(1))
        //println()
        if (userSet.getString(1) == enteredPass) {
          checkedPass = true
        }
      }

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

    checkedPass
  }

  def get_RealName(enteredUserID: String): String = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    var realName = ""

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      //use to go through usernames and set boolean variable if found
      val userSet = statement.executeQuery(s"SELECT name FROM projectzero.`project0-accounts` where username = '$enteredUserID';")

      while ( userSet.next() ) {
        realName = userSet.getString(1)
         }

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

    realName


  }





  def create_NewUser(enteredUID: String, enteredName: String, enteredEmail:String, enteredPass: String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    val insertSql = """
                      |INSERT INTO projectzero.`project0-accounts` (username,name,email,password)
                      |VALUES (?,?,?,?)
                    """.stripMargin

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the prepared statement, and run the select query

      val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)

      preparedStmt.setString(1,s"$enteredUID")
      preparedStmt.setString(2,s"$enteredName")
      preparedStmt.setString(3,s"$enteredEmail")
      preparedStmt.setString(4,s"$enteredPass")
      preparedStmt.execute()

      preparedStmt.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }


  def change_ExistingPass(enteredUID: String, newPass: String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the prepared statement, and run the select query

      val preparedStmt: PreparedStatement = connection.prepareStatement(s"Update projectzero.`project0-accounts` SET password = '$newPass' WHERE username = '$enteredUID';")
      preparedStmt.execute()

      preparedStmt.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }


  def delete_ExistingUser(enteredUID: String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the prepared statement, and run the select query

      val preparedStmt: PreparedStatement = connection.prepareStatement(s"Delete FROM projectzero.`project0-accounts` where username = '$enteredUID';")
      preparedStmt.execute()

      preparedStmt.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }

  def create_UserJournal(enteredUID: String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the prepared statement, and run the select query

      val preparedStmt: PreparedStatement = connection.prepareStatement(s"create table if not exists projectzero.`$enteredUID` (Entry mediumint not null auto_increment,`Date` Date, `Time` Time, `Upper/Systolic` int, `Lower/Diastolic` int, `Heart Rate` int, Notes longtext, primary key(Entry));")
      preparedStmt.execute()

      preparedStmt.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }

  def show_AllEntries(enteredUID: String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    var count = 0

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      val entrySet = statement.executeQuery(s"Select Entry, `Date` FROM projectzero.`$enteredUID`;")
      println("List of Entries: ")
      while ( entrySet.next() ) {
        print(entrySet.getString(1) + ": ")
        print(entrySet.getString(2) + " | ")
        count = count + 1
        if (count > 9) {
          println()
          count = 0
        }
      }


    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }


  def check_EntryNum(enteredNum: Int, enteredID: String): Boolean = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    var checkedNum: Boolean = false

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      //use to go through usernames and set boolean variable if found
      val userSet = statement.executeQuery(s"SELECT entry FROM projectzero.`$enteredID`")
      while ( userSet.next() ) {
        if (userSet.getInt(1) == enteredNum) {
          checkedNum = true
        }
      }

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

    checkedNum
  }

  def show_SingleEntry(enteredUID: String, enteredEntry: Int): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the statement, and run the select query
      val statement = connection.createStatement()

      val entrySet = statement.executeQuery(s"Select * FROM projectzero.`$enteredUID` where entry = '$enteredEntry';")
      println(s"Entry($enteredEntry): ")
      println("Entry" + "\t" + "Date" + "\t" + "\t" + "Time" + "\t" + "Upper/Systolic" + "\t" + "Lower/Diastolic" + "\t" + "HeartRate" + "\t" + "Notes")
      while ( entrySet.next() ) {
        print(" " + entrySet.getString(1) + " | ")
        print(entrySet.getString(2) + " | ")
        print(entrySet.getString(3) + " | " + "\t")
        print(entrySet.getString(4) + "\t" + "\t" + " | " + "\t"+ "\t"+ "\t")
        print(entrySet.getString(5) + "\t" + "   | ")
        print("  " + entrySet.getString(6) + "\t" + " | ")
        println(entrySet.getString(7))
      }


    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }

  def create_NewJournalEntry(enteredUID: String, enteredUBP: Int, enteredLBP:Int, enteredHR: Int, enteredNote:String): Unit = {
    val jdbcDatabase = "projectzero"
    val url = s"jdbc:mysql://localhost/$jdbcDatabase"
    val username = "root"
    val password = "abcd1234"
    val insertSql =
      s"""
         |INSERT INTO projectzero.`$enteredUID` (`Upper / Systolic`,`Lower / Diastolic`,`Heart Rate`,Notes, `Date`, `Time`)
         |Values (?,?,?,?, curdate(),curtime())
         |""".stripMargin

    try {
      // make the connection
      connection = DriverManager.getConnection(url, username, password)
      // create the prepared statement, and run the select query

      val preparedStmt: PreparedStatement = connection.prepareStatement(insertSql)

      preparedStmt.setInt(1, enteredUBP)
      preparedStmt.setInt(2,enteredLBP)
      preparedStmt.setInt(3,enteredHR)
      preparedStmt.setString(4,s"$enteredNote")
      preparedStmt.execute()

      preparedStmt.close()

    } catch {
      case e: Exception => e.printStackTrace()
    }
    connection.close()

  }



}

