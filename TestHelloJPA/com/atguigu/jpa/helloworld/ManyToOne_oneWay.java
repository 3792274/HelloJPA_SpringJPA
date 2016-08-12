package com.atguigu.jpa.helloworld;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

/**
 
  JPA映射的关联关系测试(单向多对一) 
  
  Order: 
  		Customer customer；
  		
		    // 映射单向 n-1 的关联关系
			// 使用 @ManyToOne 来映射多对一的关联关系
			// 使用 @JoinColumn 来映射外键.
			// 可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略，默认EAGER
			@JoinColumn(name = "CUSTOMER_ID")
			@ManyToOne(fetch = FetchType.LAZY)
			public Customer getCustomer() {
				return customer;
			}
		
			public void setCustomer(Customer customer) {
				this.customer = customer;
			}

			
  Customer
  		无更改，无List<order>
  
 
*/
public class ManyToOne_oneWay extends TestCase {

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

	/**
	 * 保存多对一时, 建议先保存 1 的一端, 后保存 n 的一端, 这样不会多出额外的 UPDATE 语句.
	 * 	1个客户Customer（没有List<Order>对象） ->  多个Order(有Customer对象，@JoinColumn(name = "CUSTOMER_ID") 、@ManyToOne(fetch = FetchType.LAZY)) 
	 */

	@Test
	public void testManyToOnePersist(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("gg@163.com");
		customer.setLastName("GG");
		
		Order order1 = new Order();
		order1.setOrderName("G-GG-1");
		
		Order order2 = new Order();
		order2.setOrderName("G-GG-2");
		
		//设置关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(order1);  //此时customer无ID,导致后面需要更新order1,order2的外键列
		entityManager.persist(order2);
		
		entityManager.persist(customer);  //先保存了多的一端order，再保存1的一端时，此行代码结束后，会再Update之前保存过的order对象。
	}
	
	
	
	
	
	//默认情况下(无fetch属性时), 使用左外连接的方式来获取 n 的一端的对象和其关联的 1 的一端的对象. 1条语句
	//可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略，2条SQL
	@Test
	public void testManyToOneFind(){
		Order order = entityManager.find(Order.class, 1);
		System.out.println(order.getOrderName());
		
		System.out.println(order.getCustomer().getLastName());
	}
	 
	
	
	
	
	//不能直接删除 1 的一端, 因为有外键约束. 
	@Test
	public void testManyToOneRemove(){
//		Order order = entityManager.find(Order.class, 1);
//		entityManager.remove(order);
		
		Customer customer = entityManager.find(Customer.class, 1);
		entityManager.remove(customer);
	}
	
	
	
	@Test
	public void testManyToOneUpdate(){
		Order order = entityManager.find(Order.class, 2);
		order.getCustomer().setLastName("FFF");
	}
	
	
	
	
	
	

}
