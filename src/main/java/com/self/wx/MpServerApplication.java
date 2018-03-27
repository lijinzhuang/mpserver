package com.self.wx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@MapperScan("com.self.wx.auto.dao")
@ImportResource(locations={"classpath:mybatis/dao-env-bean.xml"})
public class MpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MpServerApplication.class, args);
	}
}
