package com.atguigu.jpa.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service//是一个SpringBean
public class PersonService {

	@Autowired//自动注入
	private PersonDao personDao;
	
	
	@Transactional //增加事务。
	public void savePersons(Person p1,Person p2){
		personDao.save(p1);
		
		
		int i=10/0;  //模式抛出异常，由于使用事务，将导致没有数据被成功保存
		
		personDao.save(p2);
	}
}
