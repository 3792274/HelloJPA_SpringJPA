package com.atguigu.jpa.helloworld;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

// 测试持久化类 com.atguigu.jpa.helloworld.Customer
public class JpaTest  extends TestCase{ 
	
	
	private EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	private EntityTransaction entityTransaction;
	
	
	
	@Override
	protected void setUp() throws Exception {

	/*	
		Map<String, Object> properties = new HashMap<>(); //重载的Persistence.createEntityManagerFactory()
		properties.put("hibernate.show_sql", false);
		entityManagerFactory = Persistence.createEntityManagerFactory("HelloJPA", properties);
	*/	
		
		entityManagerFactory = Persistence.createEntityManagerFactory( "HelloJPA" );
		entityManager = entityManagerFactory.createEntityManager();
		entityTransaction = entityManager.getTransaction();  //不同于Hibernate,Hibernate中可以直接begin(),session.begin();
		entityTransaction.begin();
		
	}
	
	
	
	@Override
	protected void tearDown() throws Exception {
		entityTransaction.commit();
		entityManager.close();
		entityManagerFactory.close();
	}
	
	
	@Test
	public void testSave(){
		
		//1.创建 EntityManagerFactory
		 EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("HelloJPA");
		 
		 //2.创建entityManager
		 EntityManager entityManager = entityManagerFactory.createEntityManager();
		 
		 
		 //3.开启事务
		 EntityTransaction transaction = entityManager.getTransaction();
		 transaction.begin();
		 
		 
		 //4.进行持久化操作
		 Customer customer = new Customer();
		 customer.setLastName("TOM");
		 customer.setAge(18);
		 customer.setEmail("HelloJPA@TOM.COM");
		 customer.setBirth(new Date());
		 customer.setCreatedTime(new Date());
		 
		 entityManager.persist(customer);
		 
		 
		 //5.提交事务
		 transaction.commit();
		 
		 //6.关闭EntityManager
		 entityManager.close();
		 
		 //7.关闭EntityManagerFactory
		 entityManagerFactory.close();
	}
	
	
	
	
	@Test
	public void testFind(){
		
//		Customer customer = entityManager.find(Customer.class, 1);//类似于hibernate的get方法,没找到数据时，返回null 
		Customer customer  = entityManager.getReference(Customer.class,1); //类似于hibernate的load方法,延迟加载.没相应数据时会出现异常 
		System.out.println("-----------------------------------------------------------------");
		System.out.println(customer);
		System.out.println(customer.getClass().getName()); //getReference()返回的是代理对象
	}
	
	
	//类似于 hibernate 的 save 方法. 使对象由临时状态变为持久化状态. 
	//和 hibernate 的 save 方法的不同之处: 若对象有 id, 则不能执行 insert 操作, 而会抛出异常. 
	@Test
	public void testPersistence(){
		Customer customer = new Customer();
		customer.setAge(15);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("bb@163.com");
		customer.setLastName("BB");
	//	customer.setId(100);
		
		entityManager.persist(customer);
		System.out.println(customer.getId());
	}
	
	
	
	
	//类似于 hibernate 中 Session 的 delete 方法. 把对象对应的记录从数据库中移除
	//但注意: 该方法只能移除 持久化 对象. 而 hibernate 的 delete 方法实际上还可以移除 游离对象.
	@Test
	public void testRemove(){
//		Customer customer = new Customer();
//		customer.setId(2);
		
		Customer customer = entityManager.find(Customer.class, 2);
		entityManager.remove(customer);
	}
 
	
	
	
	/**
	 * 总的来说: 类似于 hibernate Session 的 saveOrUpdate 方法.
	 */
	//1. 若传入的是一个临时对象
	//会创建一个新的对象, 把临时对象的属性复制到新的对象中, 然后对新的对象执行持久化操作. 所以
	//新的对象中有 id, 但以前的临时对象中没有 id. 
	@Test
	public void testMerge1(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("cc@163.com");
		customer.setLastName("CC");
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	
	
	
	
	
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中也没有对应的记录
	//3. JPA 会创建一个新的对象, 然后把当前游离对象的属性复制到新创建的对象中
	//4. 对新创建的对象执行 insert 操作. 
	@Test
	public void testMerge2(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(100);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println("customer#id:" + customer.getId());
		System.out.println("customer2#id:" + customer2.getId());
	}
	
	
	
	
	
	
	


	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中没有该对象
	//2. 若在数据库中却有对应的记录
	//3. JPA 会查询对应的记录, 然后返回该记录对一个的对象, 再然后会把游离对象的属性复制到查询到的对象中.
	//4. 对查询到的对象执行 update 操作. 
	@Test
	public void testMerge3(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("ee@163.com");
		customer.setLastName("EE");
		
		customer.setId(4);
		
		Customer customer2 = entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	
	
	
	
	//若传入的是一个游离对象, 即传入的对象有 OID. 
	//1. 若在 EntityManager 缓存中有对应的对象
	//2. JPA 会把游离对象的属性复制到查询到EntityManager 缓存中的对象中.
	//3. EntityManager 缓存中的对象执行 UPDATE. 
	//JPA不同于Hibernate,JPA在一个Session中可以“关联2个ID相同的对象”，因为JPA比Hibernate多了一个复制的操作
	@Test
	public void testMerge4(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("dd@163.com");
		customer.setLastName("DD");
		
		customer.setId(4);
		Customer customer2 = entityManager.find(Customer.class, 4);
		
		entityManager.merge(customer);
		
		System.out.println(customer == customer2); //false
	}
	
	
	
	
	
	
	/**
	 * 同 hibernate 中 Session 的 flush 方法. 
	 */
	@Test
	public void testFlush(){
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println(customer);
		
		customer.setLastName("AA"); 
		
		entityManager.flush();	//正常情况下是 提交事务的时候发送sql语句，flush会强制发送sql,但事务并未提交，所以数据库看不到变化。
	}
	

	
	
	
	
	/**
	 * 同 hibernate 中 Session 的 refresh 方法. 
	 */
	@Test
	public void testRefresh(){
		Customer customer = entityManager.find(Customer.class, 1);
		customer = entityManager.find(Customer.class, 1);   //使用1级缓存，不会在发送SQL
		
	    entityManager.refresh(customer); //会在发送一次SQL,同步缓存对象与数据库对象。
	}
	

	
	
	
	
}
