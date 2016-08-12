package com.atguigu.jpa.helloworld;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.hibernate.jpa.QueryHints;
import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;

/**
 	测试JPQL使用。
 		
 		一、
 
 
 
 
 
 */
public class Jpql_Test  extends TestCase {
	

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
	public void testHelloJPQL(){
		String jpql = "FROM Customer c WHERE c.age > ?";
		Query query = entityManager.createQuery(jpql);
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	
	
	
	
	//默认情况下, 若只查询部分属性, 则将返回 Object[] 类型的结果. 或者 Object[] 类型的 List.
	//也可以在实体类中创建对应的构造器, 然后再 JPQL 语句中利用对应的构造器返回实体类的对象.
	@Test
	public void testPartlyProperties(){
		
		//String jpql = "SELECT  c.lastName, c.age  FROM Customer c WHERE c.id > ?";  //返回 Object[] 类型的结果
		String jpql = "SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?";
		List result = entityManager.createQuery(jpql).setParameter(1, 1).getResultList();
		
		System.out.println(result);
	}
	
	
	
	
	
	
	//createNamedQuery 适用于在实体类前使用 @NamedQuery 标记的查询语句
	//Customer.java ->@NamedQuery(name="testNamedQuery",query="FROM Customer c WHERE c.id = ? ")
	@Test
	public void testNamedQuery(){
		Query query = entityManager.createNamedQuery("testNamedQuery").setParameter("customerId", 1);
		Customer customer = (Customer) query.getSingleResult();
		
		System.out.println(customer);
	}
	
	
	
	
	//createNativeQuery 适用于本地 SQL
	@Test
	public void testNativeQuery(){
		String sql = "SELECT age FROM jpa_customers WHERE id = ?";
		Query query = entityManager.createNativeQuery(sql).setParameter(1, 1);
		
		Object result = query.getSingleResult();
		System.out.println(result);
	}
	

	
	
	
	//使用 hibernate 的查询缓存. 前提是配置文件中配置了使用查询缓存(persistence.xml)
	//<property name="hibernate.cache.use_query_cache" value="true"/>
	@Test
	public void testQueryCache(){
		String jpql = "FROM Customer c WHERE c.age > ?";
		Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);  //使用查询缓存
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
		
		query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);//使用查询缓存
		
		//占位符的索引是从 1 开始
		query.setParameter(1, 1);
		customers = query.getResultList();
		System.out.println(customers.size());
	}
	

	
	
	
	//查询 order 数量大于 2 的那些 Customer
	@Test
	public void testGroupBy(){
		String jpql = "SELECT o.customer FROM Order o  GROUP BY o.customer  HAVING count(o.id) >= 2";
		List<Customer> customers = entityManager.createQuery(jpql).getResultList();
		
		System.out.println(customers);
	}
	
	
	
	
	
	
	@Test
	public void testOrderBy(){
		String jpql = "FROM Customer c WHERE c.age > ? ORDER BY c.age DESC";
		Query query = entityManager.createQuery(jpql).setHint(QueryHints.HINT_CACHEABLE, true);
		
		query.setParameter(1, 1);
		List<Customer> customers = query.getResultList();
		System.out.println(customers.size());
	}
	
	
	
	/**
	 * JPQL 的关联查询同 HQL 的关联查询. 
	 * 使用到了懒加载，FROM Customer c WHERE c.id = ? 查询到customer后调用customer.getOrders()会再发送一条SQL
	 * 当在jpql中使用  LEFT OUTER JOIN FETCH c.orders 将只会发送1条SQL语句。
	 * 
	 * select c.*,o.* from jpa_customers c  left outer join jpa_orders o on c.id=o.customer_id  where c.id=1
	 *  select * from jpa_customers c,jpa_orders o where c.id=o.CUSTOMER_ID  and c.id=1
	 */
	@Test
	public void testLeftOuterJoinFetch(){
		
		String jpql = "FROM Customer c LEFT OUTER JOIN FETCH c.orders WHERE c.id = ?"; // fetch关键字强制Hibernate进行集合填充操作,查询到的orders立即填充到customer中
		Customer customer =  (Customer) entityManager.createQuery(jpql).setParameter(1, 1).getSingleResult();
		System.out.println(customer.getLastName());
		System.out.println(customer.getOrders().size());
		Assert.assertTrue(customer.getOrders().size() > 0);   
		
//		String jpql2 = "FROM Order o where o.customer = ?";
//		Customer customer = new Customer();
//		customer.setId(1);
// 		List<Order> result = entityManager.createQuery(jpql2).setParameter(1, customer).getResultList();
// 		System.out.println(result);
		
		
//		String jpql = "FROM Customer c LEFT OUTER JOIN  c.orders WHERE c.id = ?";  //去掉 FETCH 返回的Object [] 中order未初始化
//		List<Object[]> result = entityManager.createQuery(jpql).setParameter(1, 1).getResultList();
//		System.out.println(result);
	}
	

	
	
	
	
	/**
	 * JPQL使用子查询
	 */
	@Test
	public void testSubQuery(){
		//查询所有 Customer 的 lastName 为 YY 的 Order
		String jpql = "SELECT o FROM Order o WHERE o.customer = (SELECT c FROM Customer c WHERE c.lastName = ?)";
		
		Query query = entityManager.createQuery(jpql).setParameter(1, "Customer01-3");
		List<Order> orders = query.getResultList();
		System.out.println(orders.size());
	}
	
 
	
	
	
	
	/*
	 * 使用 jpql 内建的函数
		concat(String s1, String s2)：字符串合并/连接函数。
		substring(String s, int start, int length)：取字串函数。
		trim([leading|trailing|both,] [char c,] String s)：从字符串中去掉首/尾指定的字符或空格。
		lower(String s)：将字符串转换成小写形式。
		upper(String s)：将字符串转换成大写形式。
		length(String s)：求字符串的长度。
		locate(String s1, String s2[, int start])：从第一个字符串中查找第二个字符串(子串)出现的位置。若未找到则返回0。
		算术函数主要有 abs、mod、sqrt、size 等。Size 用于求集合的元素个数。
		日期函数主要为三个，即 current_date、current_time、current_timestamp，它们不需要参数，返回服务器上的当前日期、时间和时戳
	 */
	@Test
	public void testJpqlFunction(){
		String jpql = "SELECT upper(c.email) FROM Customer c";
		
		List<String> emails = entityManager.createQuery(jpql).getResultList();
		System.out.println(emails);
	}

	
	
	
	
	
	//可以使用 JPQL 完成 UPDATE 和 DELETE 操作. 
	@Test
	public void testExecuteUpdate(){
		String jpql = "UPDATE Customer c SET c.lastName = ? WHERE c.id = ?";
		Query query = entityManager.createQuery(jpql).setParameter(1, "XXX-0").setParameter(2, 5);
		
		query.executeUpdate();
		entityTransaction.commit();
	}



}
