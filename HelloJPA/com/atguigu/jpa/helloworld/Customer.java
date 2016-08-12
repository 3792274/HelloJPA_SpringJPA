package com.atguigu.jpa.helloworld;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;


//jqpl的命名查询，query必须加上select关键字可有可无
@NamedQuery(name="testNamedQuery",query="SELECT c FROM Customer c WHERE c.id =:customerId ")

//持久化类，一对无需新建对应的数据库，自动生成表,使用注解
@Cacheable(true)
@Entity     //实体类
@Table( name="JPA_CUSTOMERS",schema="scott")  //数据表
public class Customer {
	
	
	private Integer id;
	private String lastName;
	private String email;

	private int age;
	
	
	private Date createdTime;
	private Date birth;


	
	
	
	
	public Customer() {
		super();
	}



	//测试JPQL中，只查询部分属性，却返回对象的，需要的构造器  
	//SELECT new Customer(c.lastName, c.age) FROM Customer c WHERE c.id > ?
	public Customer(String lastName, int age) {
		super();
		this.lastName = lastName;
		this.age = age;
	}












	//单向1对多映射需要
	private Set<Order> orders = new HashSet<>();
	
	
	
	
/*	     //TABLE 生成主键方式， 需要自己新建立一张表jpa_id_generators
	@Id
	@TableGenerator(name="ID_GENERATOR_AA",
			table="jpa_id_generators", //自定义主键生成的表名
			pkColumnName="PK_NAME", //确定使用此表的哪一列内容
			pkColumnValue="CUSTOMER_ID", //确定使用此表哪一行内容
			valueColumnName="PK_VALUE",//确定该行的那列作为值
			allocationSize=100 //每次增加多少
			)
	
	@GeneratedValue(strategy=GenerationType.TABLE,generator="ID_GENERATOR_AA")
*/	
	
// 	@GeneratedValue(strategy=GenerationType.AUTO) //MySQL中会自动生成hibernate_sequence。2选1，这个，或者下面两个，但是不同。
	
	@Id	//对应数据表中的列，在get方法指定
	@Column(name="ID") 	
	@GeneratedValue(generator="a")
	@GenericGenerator(name="a", strategy = "increment")	  //自增长，会每次插入前执行select max(ID)操作
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@Column(name="LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	
	
	
	

	
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	
	@Temporal(TemporalType.DATE)  //使用@Temporal 精确数据表的列类型。
	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	@Transient//不需要映射为表的一列
	public String getInfo(){
		return "LastName: "+lastName+" ,Email:"+email;
	}
	
	
	
	
	
	
	
	

		//映射单向 1-n 的关联关系
		//使用 @OneToMany 来映射 1-n 的关联关系
		//使用 @JoinColumn 来映射外键列的名称
		
		//可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
		//可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略. 
//		@JoinColumn(name="CUSTOMER_ID")   //会在Order表中生成外键
//		@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.REMOVE})
		
		//双向1-n的关联关系
		//注意: 若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 端就不能再使用 @JoinColumn 属性了. 		
//		@JoinColumn(name="CUSTOMER_ID")   
		@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="customer")
		public Set<Order> getOrders() {
			return orders;
		}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	
	
	
	
	
	
	
	
	
	
	
	@Override
	public String toString() {
		return "Customer [id=" + id + ", lastName=" + lastName + ", email=" + email + ", age=" + age + ", createdTime=" + createdTime + ", birth=" + birth + "]";
	}
	
	
	
	
}
