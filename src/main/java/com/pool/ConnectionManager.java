package com.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.PropertiesManager;

/**
 * 连接池的设计思路
 * 1、
 * 初始化固定数目的连接（空闲连接与活跃连接在同一池中），建立连接的代理类，添加busy与startTime属性以便分发与回收连接
 * 另建立守护线程回收失效连接
 * 2、
 * 维护一空闲连接池，初始为空，需要连接时建立，用完的连接回收进入空闲连接池；
 * 后续所需连接从空闲连接池获取；activeNum记录活跃连接数目；
 * 当空闲连接池为空且活跃连接数达到上限时，请求等待，超时即获取连接失败，超时前有连接被释放方可获得连接
 * 第二个设计巧妙优势明显，采用第二种方式
 * 
 * 数据库连接管理类，单例模式
 * 可以管理加载多个数据库驱动，维护多个数据库连接池
 * @author shijin
 *
 */
public class ConnectionManager {
	
	private static ConnectionManager dbm = null;
	
	//单例模式里的成员变量都相当于是static了？
	/**
	 * 客户数目
	 */
	private static int clients = 0;
	/**
	 * 加载的驱动器集合
	 */
	private Set<Driver> drivers = new HashSet<Driver>();
	/**
	 * 数据库连接池字典
	 */
	private Hashtable<String,DBConnectionPool> pools = new Hashtable<String,DBConnectionPool>();
	
	private final org.slf4j.Logger log = LoggerFactory.getLogger(DBConnectionPool.class);
	
	private ConnectionManager() {
		loadDrivers();
		createPools();
	}

	/**
	 * 装载和注册所有的JDBC驱动程序
	 */
	private void loadDrivers() {
		String str_drivers = PropertiesManager.getProperty("driver");
		for(String str_driver:str_drivers.split("\\s")) {
			Driver driver = null;
			try {
				driver = (Driver)Class.forName(str_driver).newInstance();
				DriverManager.registerDriver(driver);
				log.info("成功加载JDBC驱动：" + str_driver);
			} catch (InstantiationException e) {
				log.error("加载JDBC驱动" + str_driver + "时初始化异常，请检查配置文件");
			} catch (IllegalAccessException e) {
				log.info("加载JDBC驱动" + str_driver + "时非法访问，请检查配置文件");
			} catch (ClassNotFoundException e) {
				log.info("未找到JDBC驱动" + str_driver + "请引入相关包");
			} catch (SQLException e) {
				log.info("加载JDBC驱动" + str_driver + "失败，请检查引入包的正确性");
			}
			drivers.add(driver);
		}
	}

	/**
	 * 根据配置文件创建数据库连接池
	 */
	private void createPools() {
		Enumeration<?> elements = PropertiesManager.propertiesNames();
		while(elements.hasMoreElements()) {
			String element = (String)elements.nextElement();
			if(element.endsWith(".url")) {
				String poolName = element.substring(0, element.lastIndexOf("."));
				String url = PropertiesManager.getProperty(poolName + ".url");
				if(url == null) {
					log.error("无法连接到数据库" + poolName + "请检查配置文件连接字符串");
					continue;
				}
				String user = PropertiesManager.getProperty(poolName + ".user");
				String pwd = PropertiesManager.getProperty(poolName + ".password");
				String str_max = PropertiesManager.getProperty(poolName + ".maxconn", "0");
				int maxConn = 0;
				try{
					maxConn = Integer.parseInt(str_max);
				}catch(NumberFormatException e) {
					log.error("数据库" + poolName + "最大连接数设置错误，默认设为20");
					maxConn = 20;
				}				
				DBConnectionPool pool = new DBConnectionPool(maxConn,url,poolName,user,pwd);
				pools.put(poolName, pool);
				log.info("成功创建数据库连接池" + poolName);
			}
		}
	}

	/**
	 * 获得单例
	 * @return ConnectionManager单例
	 */
	public synchronized static ConnectionManager getInstance() {
		if(dbm == null) {
			dbm = new ConnectionManager();
		}
		clients++;
		return dbm;
	}

	/**
	 * 从指定连接池中获取可用连接，无等待
	 * @param poolName	要获取连接的连接池名称
	 * @return	连接池中的一个可用连接或null
	 */
	public Connection getConnection(String poolName) {
		DBConnectionPool pool = (DBConnectionPool)pools.get(poolName);
		return pool.getConnection();
	}
	
	/**
	 * 从指定的连接池获取可用连接，有等待超时
	 * @param poolName	要获取连接的连接池名称
	 * @param timeout	获取可用连接的超时时间,单位为秒
	 * @return			连接池中的一个可用连接或null
	 */
	public Connection getConnection(String poolName,long timeout) {
		DBConnectionPool  pool = (DBConnectionPool)pools.get(poolName);
		return pool.getConnection(timeout);
	}
	
	/**
	 * 回收指定连接池的连接
	 * @param poolName	连接池名称
	 * @param conn		要回收的连接
	 */
	public void freeConnection(String poolName,Connection conn) {
		DBConnectionPool pool = (DBConnectionPool)pools.get(poolName);
		if(pool != null) {
			pool.freeConnection(conn);
		}
		log.error("找不到连接池，无法回收，请检查参数");
	}
	
	/**
	 * 关闭所有连接，撤销驱动器的注册
	 */
	public synchronized void release() {
		//所有客户连接都关闭时才真正关闭连接撤销注册
		if(clients-- != 0) {
			return;
		}
		for(Map.Entry<String,DBConnectionPool> poolEntry:pools.entrySet()) {
			DBConnectionPool pool = poolEntry.getValue();
			pool.releaseAll();
		}
		log.info("已经关闭所有连接");
		for(Driver driver:drivers) {
			try {
				DriverManager.deregisterDriver(driver);
				log.info("撤销JDBC驱动器" + driver.getClass().getName() + "的注册");
			} catch (SQLException e) {
				log.error("撤销JDBC驱动器" + driver.getClass().getName() + "的注册异常");
			}
		}
		log.info("驱动器撤销完成");
	}
	
	/**
	 * 此内部类定义了一个连接池.
	 * 它能够获取数据库连接,直到预定的最 大连接数为止
	 * 在返回连接给客户程序之前,它能够验证连接的有效性
	 * @author shijin
	 */
	private class DBConnectionPool {
		private int activeNum = 0;
		private int maxConn = 0;
		private String url = null;
		private String poolName = null;
		private String user = null;
		private String pwd = null;
		private List<Connection> freeConnections = new ArrayList<Connection>();
		
		/**
		 * 
		 * @param maxConn	设定的连接池允许的最大连接数
		 * @param url		数据库连接url
		 * @param poolName	连接池名称
		 * @param user		数据库用户名，或null
		 * @param pwd		数据库用户密码，或null
		 */
		public DBConnectionPool(int maxConn, String url, String poolName,
				String user, String pwd) {
			super();
			this.maxConn = maxConn;
			this.url = url;
			this.poolName = poolName;
			this.user = user;
			this.pwd = pwd;
		}
		
		/**
		 * 获得一个可用连接，不保证任何情况都能返回一个连接（比如超过最大连接数的时候返回null）
		 * 1、若空闲连接池不为空，从空闲连接池取出一个连接后检查有效性，正常则返回，失效则重新获取连接
		 * 2、若空闲连接池为空且未超过最大连接数限制，新建一个连接并返回
		 * 3、空闲连接数为空且超过最大连接数限制，返回null
		 * @return	获得的可用连接
		 */
		public synchronized Connection getConnection() {
			Connection conn = null;
			//空闲连接池中有空闲连接，直接取
			if(freeConnections.size() > 0) {
				//从空闲连接池中取出一个连接
				conn = freeConnections.get(0);
				freeConnections.remove(0);
				//检测连接有效性
				try{
					if(conn.isClosed()) {
						//由于已经从空闲连接池取出，所以不使用无效连接其就无法重新进入
						//空闲连接池，意味着其已经被删除了，记入日志即可
						log.info("从连接池" + poolName + "中取出的连接已关闭，重新获取连接");
						//继续从连接池尝试获取连接
						conn = getConnection();
					}
				}catch(SQLException e) {
					log.info("从连接池" + poolName + "中取出的发生服务器访问错误，重新获取连接");
					conn = getConnection();
				}
			} else if(activeNum < maxConn) {
				conn = newConnection();
			} else {
				//未获得连接
			}
			if(conn != null) {
				activeNum++;
			}
			return conn;
		}
		
		/**
		 * 当无空闲连接而又未达到最大连接数限制时创建新的连接
		 * @return	新创建的连接
		 */
		private Connection newConnection() {
			Connection conn = null;
			try{
				if(user == null) {
					conn = DriverManager.getConnection(url);
				} else {
					conn = DriverManager.getConnection(url, user, pwd);
				}
				log.info("与数据库" + poolName + "创建一个新连接");
			}catch(SQLException e) {
				log.error("无法根据\"" + url + "\"与数据库" + poolName + "建立新连接");
			}
			return conn;
		}
		
		/**
		 * 获得一个可用连接，超过最大连接数时线程等待，直到有有连接释放时返回一个可用连接或者超时返回null
		 * @param timeout 等待连接的超时时间，单位为秒
		 * @return
		 */
		public synchronized Connection getConnection(long timeout) {
			Connection conn = null;
			long startTime = System.currentTimeMillis();
			while((conn = getConnection()) == null) {
				try{
					//被notify(),notifyALL()唤醒或者超时自动苏醒
					wait(timeout);
				}catch(InterruptedException e) {
					log.error("等待连接的线程被意外打断");
				}
				//若线程在超时前被唤醒，则不会返回null，继续循环尝试获取连接
				if(System.currentTimeMillis() - startTime > timeout*1000000)
					return null;
			}
			return conn;
		}
		
		/**
		 * 将释放的空闲连接加入空闲连接池，活跃连接数减一并激活等待连接的线程
		 * @param conn	释放的连接
		 */
		public synchronized void freeConnection(Connection conn) {
			freeConnections.add(conn);
			activeNum--;
			notifyAll();//通知正在由于达到最大连接数限制而wait的线程获取连接
		}
		
		/**
		 * 关闭空闲连接池中的所有连接
		 */
		public synchronized void releaseAll() {
			for(Connection conn:freeConnections) {
				try{
					conn.close();
					log.info("关闭空闲连接池" + poolName + "中的一个连接");
				}catch(SQLException e) {
					log.error("关闭空闲连接池" + poolName + "中的连接失败");
				}
			}
			freeConnections.clear();
		}
	}
}

