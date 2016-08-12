package com.atguigu.jpa.helloworld;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;



/**
 
  JPA映射的关联关系测试(单向一对多)
  
  Order: 
		  无Customer对象  
		  
		  
  Customer
  		private Set<Order> orders = new HashSet<>();
  		
  		//映射单向 1-n 的关联关系
		//使用 @OneToMany 来映射 1-n 的关联关系
		//使用 @JoinColumn 来映射外键列的名称-会在多的一端表中生成外键
		//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略 - 默认懒加载  FetchType.EAGER 非懒加载，
		//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略.  - CascadeType.REMOVE 级联删除多的一端，而不是置空多的一端
		
		@JoinColumn(name="CUSTOMER_ID")   //会在Order表中生成外键
		@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.REMOVE})   
		public Set<Order> getOrders() {
			return orders;
		}

	 
  		 
 
*/


public class OneToMany_oneWay extends TestCase {

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

	 
	
	
	
	//若是双向 1-n 的关联关系, 执行保存时
	//若先保存 n 的一端, 再保存 1 的一端, 默认情况下, 会多出 n 条 UPDATE 语句.
	//若先保存 1 的一端, 则会多出 n 条 UPDATE 语句
	//单向 1-n 关联关系执行保存时, 一定会多出 UPDATE 语句.
	//因为 n 的一端在插入时不会同时插入外键列. 
	@Test
	public void testOneToManyPersist(){
		Customer customer = new Customer();
		customer.setAge(18);
		customer.setBirth(new Date());
		customer.setCreatedTime(new Date());
		customer.setEmail("mm@163.com");
		customer.setLastName("MM");
		
		Order order1 = new Order();
		order1.setOrderName("O-MM-1");
		
		Order order2 = new Order();
		order2.setOrderName("O-MM-2");
		
		//建立关联关系
		customer.getOrders().add(order1);
		customer.getOrders().add(order2);
		
		
		//执行保存操作
		entityManager.persist(customer);

		entityManager.persist(order1);
		entityManager.persist(order2);  //不论先保存那个，都会最后执行2个update,因为order不维护关联关系
	}
	
	 
	
	
	//默认对关联的多的一方使用懒加载的加载策略. 
	//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略  
	//Customer类中-> set<order> - >@OneToMany(fetch=FetchType.EAGER),会使用左外链接查询
	@Test
	public void testOneToManyFind(){
		Customer customer = entityManager.find(Customer.class, 1);
		System.out.println(customer.getLastName());
		
		System.out.println(customer.getOrders().size());
	}
	

	
	
	//默认情况下, 若删除 1 的一端, 则会先把关联的 n 的一端的外键置空（null）, 然后进行删除. 
	//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略.    
	//Customer类中-> set<order> - >  @OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.REMOVE}) ,会级联删除，用的不多。
	@Test
	public void testOneToManyRemove(){
		Customer customer = entityManager.find(Customer.class, 8);
		entityManager.remove(customer);
	}
	
	
	
	//1.当 customer 类中  @OneToMany(fetch=FetchType.LAZY) ，（默认）以下语句只会更新第一条Order记录
	//2.当 customer 类中  @OneToMany(fetch=FetchType.EAGER) ，以下语句只会更新最后一条Order记录
	@Test
	public void testUpdate(){
		Customer customer = entityManager.find(Customer.class, 1);
		
		customer.getOrders().iterator().next().setOrderName("O-XXX-16");
	}
	

}
