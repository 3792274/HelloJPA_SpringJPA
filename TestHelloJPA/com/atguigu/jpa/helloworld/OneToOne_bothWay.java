package com.atguigu.jpa.helloworld;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

/**
 
  JPA映射的关联关系测试(双向一对一) 
  
  注意：
  	 
  -----------------------------------基于外键的-----------------------------------------------------
  Manager: 
  		private Department dept;
	  		
		//对于不维护关联关系, 没有外键的一方, 使用 @OneToOne 来进行映射, 建议设置 mappedBy=true
		@OneToOne(mappedBy="mgr")
		public Department getDept() {
			return dept;
		}

			
  Department：
  		private Manager mgr;
  		
		//使用 @OneToOne 来映射 1-1 关联关系。
		//若需要在当前数据表中添加主键则需要使用 @JoinColumn 来进行映射. 注意, 1-1 关联关系, 所以需要添加 unique=true
		@JoinColumn(name="MGR_ID", unique=true) //本表会生成一个外键
		@OneToOne(fetch=FetchType.LAZY)  //默认情况非懒加载，会使用左外连接获取其关联的对象
		public Manager getMgr() {
			return mgr;
		}
		
 -------------------------------------------------------------------------------------------------- 
  
 
*/




public class OneToOne_bothWay extends TestCase { 

	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction entityTransaction;

	@Override
	protected void setUp() throws Exception {
		entityManagerFactory = Persistence.createEntityManagerFactory("HelloJPA");
		entityManager = entityManagerFactory.createEntityManager();
		entityTransaction = entityManager.getTransaction(); // 不同于Hibernate,Hibernate中可以直接begin(),session.begin();
		entityTransaction.begin();

	}

	@Override
	protected void tearDown() throws Exception {
		entityTransaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}

 
	
	
	
	
	
	//双向 1-1 的关联关系, 建议先保存不维护关联关系的一方, 即没有外键的一方, 这样不会多出 UPDATE 语句.
	@Test
	public void testOneToOnePersistence(){
		Manager mgr = new Manager();
		mgr.setMgrName("M-BB");
		
		Department dept = new Department();
		dept.setDeptName("D-BB");
		
		//设置关联关系
		mgr.setDept(dept);
		dept.setMgr(mgr);
		
		//执行保存操作
		entityManager.persist(mgr);
		entityManager.persist(dept);
	}
	
	
	
	
	
	
	//1.默认情况下, 若获取维护关联关系的一方, 则会通过左外连接获取其关联的对象. 
	//但可以通过 @OntToOne 的 fetch 属性来修改加载策略.
	@Test
	public void testOneToOneFind(){
		Department dept = entityManager.find(Department.class, 1);
		System.out.println(dept.getDeptName());
		System.out.println(dept.getMgr().getClass().getName());  //懒加载，为代理对象
	}
	
	
	
	//1. 默认情况下, 若获取不维护关联关系的一方, 则也会通过左外连接获取其关联的对象. 
	//可以通过 @OneToOne 的 fetch 属性来修改加载策略. 但依然会再发送 SQL 语句来初始化其关联的对象
	//这说明在不维护关联关系的一方, 不建议修改 fetch 属性. 
	@Test
	public void testOneToOneFind2(){
		Manager mgr = entityManager.find(Manager.class, 1);
		System.out.println(mgr.getMgrName());
		
		System.out.println(mgr.getDept().getClass().getName()); //非代理对象
	}
	
	
	
	
}
