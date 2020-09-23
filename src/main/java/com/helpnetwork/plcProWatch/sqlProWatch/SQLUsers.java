package com.helpnetwork.plcProWatch.sqlProWatch;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;



public class SQLUsers {

  public static void main(String[] args) {

    Connection connection = null;

    try {
     Driver driver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
      DriverManager.registerDriver(driver);

      Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

      System.out.println(driver.toString());
      System.out.println("Драйвер успешно зарегистрирован");
    } catch (SQLException e) {
      System.err.println("Соединение не установлено");
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    try {
      String connectionUrl = "jdbc:sqlserver://10.80.0.113:1433;databaseName=PWNT_Russian;user=sa;password=Lukoil2019";

      connection = DriverManager.getConnection(connectionUrl );
      System.out.println(connection);

      if(! connection.isClosed()){
        System.out.println("Соединение установлено");
      }
    }catch (SQLException e){
      System.err.println("Соединение не установлено");
      e.printStackTrace();
    }
    finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
        System.out.println("Соединение закрыто");
      }

    }

  }
}
