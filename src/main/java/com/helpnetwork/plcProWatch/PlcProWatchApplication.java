package com.helpnetwork.plcProWatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.sql.DriverManager;
import java.sql.SQLException;

@EnableAutoConfiguration
@SpringBootApplication
public class PlcProWatchApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(PlcProWatchApplication.class, args);
		/*try {
			DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
	}
}
