package com.helpnetwork.plcProWatch.s7;

import com.github.s7connector.api.DaveArea;
import com.github.s7connector.api.S7Connector;
import com.github.s7connector.api.factory.S7ConnectorFactory;
import com.github.s7connector.exception.S7Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import com.sun.org.slf4j.internal.Logger;
//import com.sun.org.slf4j.internal.LoggerFactory;

import java.io.IOException;

public class plcSoland {

    private static final Logger logger = LoggerFactory.getLogger(plcSoland.class);



    public static void main(String[] args) {
        logger.debug("LOG Connection");

        try {
            //Create connection
            S7Connector connector =
                    S7ConnectorFactory
                            .buildTCPConnector()
                            .withHost("192.168.0.1")
                            .withRack(0) //optional
                            .withSlot(2) //optional
                            .build();

            //byte[] ACCESS_MODE = connector.read(DaveArea.DB, 21, 1, 0);
            byte[] ACCESS_MODE = connector.read(DaveArea.DB, 21, 1, 6);
            logger.debug("LOG Connection", connector);

            switch (ACCESS_MODE[0]){
                case (0x00):
                    System.out.println("Доступ разрешен меняю на ЗАПРЩЕН ");
                    ACCESS_MODE[0]=0x01;
                    break;
                case (0x01):
                    System.out.println("Доступ запрещен меняю на РАЗРЕШЕН ");
                    ACCESS_MODE[0]=0x00;
                    break;
            }
            connector.write(DaveArea.DB, 21, 6, ACCESS_MODE);
            connector.close();

        } catch (S7Exception | IOException se){
            se.printStackTrace();
        }

    }
    public static void AccesSOLAND(){
        try {
            //Create connection
            S7Connector connector =
                    S7ConnectorFactory
                            .buildTCPConnector()
                            .withHost("192.168.0.1")
                            .withRack(0) //optional
                            .withSlot(2) //optional
                            .build();

            byte[] ACCESS_MODE = connector.read(DaveArea.DB, 21, 1, 6);
            logger.debug("LOG Connection", connector);

/*            switch (ACCESS_MODE[0]){
                case (0x00):
                    System.out.println("Доступ разрешен меняю на ЗАПРЩЕН ");
                    ACCESS_MODE[0]=0x01;
                    break;
                case (0x01):
                    System.out.println("Доступ запрещен меняю на РАЗРЕШЕН ");
                    ACCESS_MODE[0]=0x00;
                    break;
            }*/
            ACCESS_MODE[0]=0x01;
            connector.write(DaveArea.DB, 21, 6, ACCESS_MODE);
            connector.close();

        } catch (S7Exception | IOException se){
            se.printStackTrace();

        }
    }
    public static void NO_AccessSOLAND(){
        try {
            //Create connection
            S7Connector connector =
                    S7ConnectorFactory
                            .buildTCPConnector()
                            .withHost("192.168.0.1")
                            .withRack(0) //optional
                            .withSlot(2) //optional
                            .build();

            byte[] ACCESS_MODE = connector.read(DaveArea.DB, 21, 1, 0);
            logger.debug("LOG Connection", connector);

/*            switch (ACCESS_MODE[0]){
                case (0x00):
                    System.out.println("Доступ разрешен меняю на ЗАПРЩЕН ");
                    ACCESS_MODE[0]=0x01;
                    break;
                case (0x01):
                    System.out.println("Доступ запрещен меняю на РАЗРЕШЕН ");
                    ACCESS_MODE[0]=0x00;
                    break;
            }*/
            ACCESS_MODE[0]=0x00;
            connector.write(DaveArea.DB, 21, 0, ACCESS_MODE);
            connector.close();

        } catch (S7Exception | IOException se){
            se.printStackTrace();
        }
    }

    private static byte[] intToBytes(final int data) {
        return new byte[] {
                (byte)((data >> 24) & 0xff),
                (byte)((data >> 16) & 0xff),
                (byte)((data >> 8) & 0xff),
                (byte)((data >> 0) & 0xff),
        };
    }



}
