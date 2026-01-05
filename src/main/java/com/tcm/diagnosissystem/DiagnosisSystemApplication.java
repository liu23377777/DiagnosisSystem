package com.tcm.diagnosissystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.tcm.diagnosissystem.mapper")
public class DiagnosisSystemApplication {

	public static void main(String[] args) {

//		/*
//		测试调试，每次获得对应数据的密码
//		 */
//		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//		String password = "123456";
//		String encoded = encoder.encode(password);
//		System.out.println("原始密码: " + password);
//		System.out.println("加密后的密码: " + encoded);
//		System.out.println("\n复制加密后的密码到SQL里");

		SpringApplication.run(DiagnosisSystemApplication.class, args);
	}

	//测试数据库连接
	@Bean
	public CommandLineRunner testDB(DataSource dataSource){
		return args -> {
			System.out.println("数据库连接成功"+dataSource.getConnection());
		};

	}
}
