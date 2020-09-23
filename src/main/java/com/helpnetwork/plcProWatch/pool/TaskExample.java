package com.helpnetwork.plcProWatch.pool;

/*import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helpnetwork.MESicPLC.MQTT.MqttServerSend;
import com.helpnetwork.MESicPLC.PLC4.PLCFeigeA53Class;
import com.helpnetwork.MESicPLC.REST.BaseRestStatus;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;*/

import com.helpnetwork.plcProWatch.rest.BaseRestStatus;
import com.helpnetwork.plcProWatch.s7.plcSoland;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;


public class TaskExample extends Thread {

    static String connectionUrl = "jdbc:sqlserver://10.80.0.113:1433;databaseName=PWNT_Russian;user=sa;password=Lukoil2019";
    //Connection connection = null;

    BaseRestStatus restStatus =  new BaseRestStatus();
    boolean runThreadTask = restStatus.isRunThreadTask();

  //  static JsonObject jsonObject = new JsonObject();
    static String contentMqtt = "Message Mqttt";

    @Override
    public void run() {
        System.out.println("Start Request DB Prowatch");
        System.out.println(restStatus.isRunThreadTask());

        while (restStatus.isRunThreadTask()) {
            // вкючил опрос контроллера
            //requestStatusPagePrinting(); // Send request read response printer
            System.out.println("Work Runnig");
            //poolRequestPlc();
           poolRequestProwatch();

            try {
                Thread.sleep(1000);
/*                try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                    br.readLine();
                }*/
            }

            catch (InterruptedException e) {
                e.printStackTrace();
                BaseRestStatus.StartTASKExample();

            }
            catch (Exception e){
                e.printStackTrace();
                BaseRestStatus.StartTASKExample();

            }
        }
        System.out.println("Stop Request DB Prowatch");

    }

    public void poolRequestProwatch(){
        System.out.println("Start Pool DB ");
        ResultSet resultSet = null;

        Driver driver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
        try {
            DriverManager.registerDriver(driver);


        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        //System.out.println(driver.toString());
        //System.out.println("Драйвер успешно зарегистрирован");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement();) {

            // Create and execute a SELECT SQL statement.
            String selectSql = "select * from dbo.EV_LOG  where CARDNO is not null and LNAME is not null and EVNT_DAT between dateadd(MINUTE, -1,GETDATE()) and GETDATE() and LOGDEVID = 0x006FEABDD25C1189464AA6AB62ACB4465EE9";
            resultSet = statement.executeQuery(selectSql);

            // Print results from select statement
           /* if (resultSet.wasNull()){
                System.out.println("есть данные ");

            } else {
                System.out.println("Пусто ");*/
           // }

            boolean empty = true;
           while (resultSet.next()) {
                System.out.println(resultSet.getString(8) +
                        " " + resultSet.getString(10)+
                        " " + resultSet.getString(11)+
                        " " + resultSet.getString(19)+
                        " " + resultSet.getString(20)+
                        " " + resultSet.getString(21)
                );
                empty =false;
               plcSoland.AccesSOLAND();
            }

            if (empty){
                System.out.println("EMPTY ");
                //plcSoland.NO_AccessSOLAND();
            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
