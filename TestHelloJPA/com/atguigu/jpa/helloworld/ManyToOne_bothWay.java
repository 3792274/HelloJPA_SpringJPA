package com.atguigu.jpa.helloworld;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

/**
 
  JPA映射的关联关系测试(双向多对一、双向一对多) 
  
  注意：
  		1.两边的外键列，列名要一致：CUSTOMER_ID
  		2.若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了. 	
  			@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="customer")
  
  Order: 
  		Customer customer；
  		
		// 映射 n-1 的关联关系
		// 使用 @ManyToOne 来映射多对一的关联关系
		// 使用 @JoinColumn 来映射外键.
		// 可使用 @ManyToOne 的 fetch 属性来修改默认的关联属性的加载策略,默认EAGER
		@JoinColumn(name = "CUSTOMER_ID")
		@ManyToOne(fetch = FetchType.LAZY)
		public Customer getCustomer() {
			return customer;
		}

			
  Customer：
  		private Set<Order> orders = new HashSet<>();
  		
  		//映射双向 1-n 的关联关系
		//使用 @OneToMany 来映射 1-n 的关联关系
		//使用 @JoinColumn 来映射外键列的名称-会在多的一端表中生成外键
		//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略 - 默认懒加载  FetchType.EAGER 非懒加载，
		//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略.  - CascadeType.REMOVE 级联删除多的一端，而不是置空多的一端
		
		//如下两句都会维护关联关系，不合适
//		@JoinColumn(name="CUSTOMER_ID")   //会在Order表中生成外键
///		@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.REMOVE})   
		
		
		
		
		//多的一端维护关联关系
		//注意: 若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了. 		
		//@JoinColumn(name="CUSTOMER_ID")   
		@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="customer") //一的一端放弃维护，由多的一端的customer去维护
		public Set<Order> getOrders() {
			return orders;
		}

  
 
*/
public class ManyToOne_bothWay extends TestCase { 

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

	 
	

	
	
	//若是双向1-n关联关系，保存时
	//若先保存n的一端，再保存1的一端，会多出4条update语句。因为两端都维护关联关系
	//若先保存1的一端会多出2条
	//在进行双向 1-n 关联关系时, 建议使用 n 的一方来维护关联关系, 而 1 的一方不维护关联系, 这样会有效的减少 SQL 语句. 
	//注意: 若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了. 
	//只有这么一行。。OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="customer")
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
		
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行保存操作
		entityManager.persist(customer);

		entityManager.persist(order1);
		entityManager.persist(order2);   //若是双方都维护关联关系，后插入多的一端，order需要的customerID已经有了，只会执行2条update,外键关系已经完成，但需要customer在维护一遍，所以会有2条更新order的语句
	}
	
	
}
