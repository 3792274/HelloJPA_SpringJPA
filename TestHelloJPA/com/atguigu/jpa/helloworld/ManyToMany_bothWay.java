package com.atguigu.jpa.helloworld;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import junit.framework.TestCase;

/**
 
  JPA映射的关联关系测试(双向多对多) 
  
  注意：
  		1.需要有一方放弃维护关联关系，否则主键重复。
  		
  		
  
  Item: 
  		private Set<Category> categories = new HashSet<>();
  		
		//使用 @ManyToMany 注解来映射多对多关联关系
		//使用 @JoinTable 来映射中间表
		//1. name 指向中间表的名字
		//2. joinColumns 映射当前类所在的表在中间表中的外键
		//2.1 name 指定外键列的列名
		//2.2 referencedColumnName 指定外键列关联当前表的哪一列
		//3. inverseJoinColumns 映射关联的类所在中间表的外键
		@JoinTable(name="ITEM_CATEGORY", //中间表的名字
				joinColumns={@JoinColumn(name="ITEM_ID", referencedColumnName="ID")},   //中间表的列名ITEM_ID，引用自本表的ID
				inverseJoinColumns={@JoinColumn(name="CATEGORY_ID", referencedColumnName="ID")}) //中间表的列名CATEGORY_ID，引用自对方表的ID
		@ManyToMany
		public Set<Category> getCategories() {
			return categories;
		}

			
  Category：
  		private Set<Item> items = new HashSet<>();
  		
		@ManyToMany(mappedBy="categories")
		public Set<Item> getItems() {
			return items;
		}   
		

  
 
*/
public class ManyToMany_bothWay extends TestCase { 

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

	 
	
	
	//多对所的保存 ，8条语句(Item-2条，Category-2条，中间表4条)
	@Test
	public void testManyToManyPersist(){
		Item i1 = new Item();
		i1.setItemName("i-1");
	
		Item i2 = new Item();
		i2.setItemName("i-2");
		
		Category c1 = new Category();
		c1.setCategoryName("C-1");
		
		Category c2 = new Category();
		c2.setCategoryName("C-2");
		
		//设置关联关系
		i1.getCategories().add(c1);
		i1.getCategories().add(c2);
		
		i2.getCategories().add(c1);
		i2.getCategories().add(c2);
		
		c1.getItems().add(i1);
		c1.getItems().add(i2);
		
		c2.getItems().add(i1);
		c2.getItems().add(i2);
		
		//执行保存
		entityManager.persist(i1);
		entityManager.persist(i2);
		entityManager.persist(c1);
		entityManager.persist(c2);
	}
	

 
	
	
	
	//对于关联的集合对象, 默认使用懒加载的策略.
	//使用维护关联关系的一方获取, 还是使用不维护关联关系的一方获取, SQL 语句相同. 
	@Test
	public void testManyToManyFind(){
		
		//获取维护关联关系的一方，默认使用懒加载的策略.
		Item item = entityManager.find(Item.class, 5);
		System.out.println(item.getItemName());
		System.out.println(item.getCategories().size());
		
		//获取不维护关联关系的一方，默认使用懒加载的策略.
		Category category = entityManager.find(Category.class, 3);
		System.out.println(category.getCategoryName());
		System.out.println(category.getItems().size());
	}
	
	
	
	
}
