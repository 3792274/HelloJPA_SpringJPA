package com.atguigu.jpa.helloworld;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

/**
 	
 	测试JPA二级缓存（cache 非 cash ）
 	
 	一、
 		1.1级缓存在entityManager中，如果关闭，清空则缓存消失
 		2.2级缓存可以跨entityManager.
 		
 	二、增加2级缓存支持：
 			1.修改 persistence.xml、注意位置。
 			2.配置pom.xml增加 hibernate-ehcache相关Jar、
 			3.增加ehcache配置文件来自 project\etc\ehcache.xml
 			4.为需要缓存的实体类加入 @Cacheable(true)
 			
		<!-- persistence.xml   二级缓存相关 -->
		<property name="hibernate.cache.use_second_level_cache" value="true"/>
		<property name="hibernate.cache.region.factory_class" value="org.hibernate.cache.ehcache.EhCacheRegionFactory"/>
		<property name="hibernate.cache.use_query_cache" value="true"/>
 
 		<!--  persistence.xml  配置二级缓存的策略 
			ALL：所有的实体类都被缓存
			NONE：所有的实体类都不被缓存. 
			ENABLE_SELECTIVE：标识 @Cacheable(true) 注解的实体类将被缓存
			DISABLE_SELECTIVE：缓存除标识 @Cacheable(false) 以外的所有实体类
			UNSPECIFIED：默认值，JPA 产品默认值将被使用
		-->
		<shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
 
 */
public class SecondLevelCache  extends TestCase {
	

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

	 
	
	
	
	
	
	
	
	
	@Test
	public void testSecondLevelCache(){
		
		//如下两句会发送1条SQL,使用JPA的一级缓存
//		Customer customer1 = entityManager.find(Customer.class, 1);
//		Customer customer2 = entityManager.find(Customer.class, 1);
		
		
		

		//默认没有配置时候，提交事务并关闭事务后，重新获取事务及开始事务，会发送2条SQL
		//配置了缓存以后，将只发送一条
		Customer customer1 = entityManager.find(Customer.class, 1);
		
		entityTransaction.commit();
		entityManager.close();
		
		entityManager = entityManagerFactory.createEntityManager();
		entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		
		Customer customer2 = entityManager.find(Customer.class, 1);
		
		
	}
	
	

}
