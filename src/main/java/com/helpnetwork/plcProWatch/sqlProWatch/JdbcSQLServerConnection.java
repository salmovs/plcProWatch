package com.helpnetwork.plcProWatch.sqlProWatch;

import java.sql.*;


public class JdbcSQLServerConnection {

    static String connectionUrl = "jdbc:sqlserver://10.80.0.113:1433;databaseName=PWNT_Russian;user=sa;password=Lukoil2019";

/*   public static void main(String[] args) {

        Connection conn = null;

        try {

         *//*   String dbURL = "jdbc:sqlserver://10.80.0.113\\PWNT_Russian";
            String user = "sa";
            String pass = "Lukoil2019";*//*
            //Connection
            conn = DriverManager.getConnection("jdbc:sqlserver://10.80.0.113:1433;databaseName=PWNT_Russian","sa", "Lukoil2019");
           // conn = DriverManager.getConnection(dbURL, user, pass);
            if (conn != null) {
                DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
                System.out.println("Driver name: " + dm.getDriverName());
                System.out.println("Driver version: " + dm.getDriverVersion());
                System.out.println("Product name: " + dm.getDatabaseProductName());
                System.out.println("Product version: " + dm.getDatabaseProductVersion());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }*/

/*
    public static void main(String[] args) {
        String connectionUrl =
                "jdbc:sqlserver://10.80.0.113.database.windows.net:1433;"
                        + "database=PWNT_Russian;"
                        + "user=sa@10.80.0.113;"
                        + "password=Lukoil2019;"
                        + "encrypt=true;"
                        + "trustServerCertificate=false;"
                        + "loginTimeout=30;";

        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            // Code here.
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
public static void main(String[] args) {

    Connection conn = null;
    String connectionUrl = "jdbc:sqlserver://10.80.0.113:1433;databaseName=PWNT_Russian;user=sa;password=Lukoil2019";

    try {
        System.out.print("Connecting to SQL Server ... ");
/*        try (Connection connection = DriverManager.getConnection(connectionUrl)) {
            System.out.println("Done.");
        }*/
        conn = DriverManager.getConnection(connectionUrl);
        if (conn != null) {
            DatabaseMetaData dm = (DatabaseMetaData) conn.getMetaData();
            System.out.println("Driver name: " + dm.getDriverName());
            System.out.println("Driver version: " + dm.getDriverVersion());
            System.out.println("Product name: " + dm.getDatabaseProductName());
            System.out.println("Product version: " + dm.getDatabaseProductVersion());
        }
        conn.close();

        RequestSQLProwatch();

    } catch (Exception e){
        e.printStackTrace();
    }
}
public static void RequestSQLProwatch() {
    ResultSet resultSet = null;

    try (Connection connection = DriverManager.getConnection(connectionUrl);
         Statement statement = connection.createStatement();) {

        // Create and execute a SELECT SQL statement.
        String selectSql = "select * from dbo.EV_LOG  where CARDNO is not null and LNAME is not null and EVNT_DAT between dateadd(MINUTE, -1360,GETDATE()) and GETDATE() and LOGDEVID = 0x006FEABDD25C1189464AA6AB62ACB4465EE9";
        resultSet = statement.executeQuery(selectSql);

        // Print results from select statement
        while (resultSet.next()) {
            System.out.println(resultSet.getString(8) +
                    " " + resultSet.getString(10)+
                    " " + resultSet.getString(11)+
                    " " + resultSet.getString(19)+
                    " " + resultSet.getString(20)+
                    " " + resultSet.getString(21)
            );
        }
        resultSet.close();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


}
