package com.atguigu.jpa.spring;

import static org.hamcrest.CoreMatchers.nullValue;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JpaSpringTest {
	
	private ApplicationContext ctx=null;
	private PersonService personService = null;
	
	
	{
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		personService = ctx.getBean(PersonService.class);
		
	}

	
	
	@Test
	public void testDataSources() throws SQLException {
		DataSource dataSource = ctx.getBean(DataSource.class);
		Connection connection = dataSource.getConnection();
		System.out.println(connection);
	}

	
	
	@Test
	public void testPersonService(){
			Person p1 = new Person();
			p1.setAge(11);
			p1.setEmail("aa@163.com");
			p1.setLastName("AA");
			
			Person p2 = new Person();
			p2.setAge(12);
			p2.setEmail("bb@163.com");
			p2.setLastName("BB");
			
			System.out.println(personService.getClass().getName()); //如果打印的是代理对象，则说明事务加上了
			personService.savePersons(p1, p2);
	}
	
}
