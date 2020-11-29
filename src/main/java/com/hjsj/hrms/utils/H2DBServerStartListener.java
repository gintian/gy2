package com.hjsj.hrms.utils;

import org.apache.log4j.Category;
import org.h2.tools.Server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.SQLException;


public class H2DBServerStartListener implements ServletContextListener { 
	//H2数据库服务器启动实例18    
		private Server server;     
		private  Category cat = Category.getInstance(this.getClass());
		/*    Web应用初始化时启动H2数据库 21      */
		public void contextInitialized(ServletContextEvent sce) {
			try {  
				cat.debug("正在启动h2数据库..."); 
	          //使用org.h2.tools.Server这个类创建一个H2数据库的服务并启动服务，由于没有指定任何参数，那么H2数据库启动时默认占用的端口就是808226   
				server = Server.createTcpServer().start(); 
				cat.debug("h2数据库启动成功...");       
	          
			} catch (SQLException e) { 
				cat.debug("启动h2数据库出错：" + e.toString()); 
				e.printStackTrace();   
				throw new RuntimeException(e);   
			}   
		} 
	
	
		/*  36      * Web应用销毁时停止H2数据库 37      */
		public void contextDestroyed(ServletContextEvent sce) {
				if (this.server != null) {
                  // 停止H2数据库 
					cat.debug("停止H2数据库");
					this.server.stop();
					this.server = null;
				} 
		}   
	 
}