package com.hrms.frame.utility;

import com.alibaba.druid.pool.DruidDataSource;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.log4j.Category;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * <p>Title:AdminDb </p>
 *  * <p>Description:管理数据库连接 </p>
 *  * <p>Copyright: Copyright (c) 2004</p>
 *  * <p>Company: easytong</p>
 *  * @author chenmengqing
 *  * @version 1.0
 */

public class AdminDb {
  public AdminDb() {
  }
  private static Category cat = Category.getInstance("com.hrms.frame.utility.AdminDb");

  static DataSource ds = null;

  /**
   * 上下文环境
   */
  private static Context context = null;
  /**ds工厂标识
   *=1 weblogic 
   *=2 websphere 
   */
  private static int jndiflag=1;

  /**
   * 初始化上下文,暂时仅对WebLogic而言
   * @return
   */
  public static synchronized Context getInitialContext()
  {
    String url = "";//T3协议URL
    String user = "";//用户名
    String password = "";//口令
    Properties properties = null;
    try
    {
      url= SystemConfig.getProperty("url");
      user= SystemConfig.getProperty("user");
      password= SystemConfig.getProperty("password");
      if (context == null)
      {
        properties = new Properties();
        if(jndiflag==2) //websphere6
        	properties.put(Context.INITIAL_CONTEXT_FACTORY,"com.ibm.websphere.naming.WsnInitialContextFactory");
        else
        	properties.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
        
        properties.put(Context.PROVIDER_URL, url);
        properties.put(Context.SECURITY_PRINCIPAL, user);
        properties.put(Context.SECURITY_CREDENTIALS,(password == null) ? "" : password);
        context = new InitialContext(properties);
      }
    }
    catch (NamingException ex)
    {
        ex.printStackTrace();
    }
    catch (GeneralException ge)
    {
        ge.printStackTrace();
    }
    //System.out.println("--------->Context!");
    return context;
  }

  public static Connection getConnection()throws GeneralException
  {


      Connection conn = null;
      try {
          if (ds != null) {
              return ds.getConnection();
          }

          String driver = SystemConfig.getPropertyValue("druid_driver");
          String url = SystemConfig.getPropertyValue("druid_url");
          String username = SystemConfig.getPropertyValue("druid_username");
          String password = SystemConfig.getPropertyValue("druid_password");
          String maxactive = SystemConfig.getPropertyValue("druid_maxactive");
          maxactive = "".equals(maxactive)?"100":maxactive;
          String maxwait = SystemConfig.getPropertyValue("druid_maxwait");
          maxwait = "".equals(maxwait)?"10000":maxwait;

          if(username.startsWith("@")||password.startsWith("@")){
              Des des = new Des();
              username = des.DecryPwdStr(username.substring(1));
              password = des.DecryPwdStr(password.substring(1));
          }

          DruidDataSource dds = new DruidDataSource();
          dds.setDriverClassName(driver);
          dds.setUrl(url);
          dds.setUsername(username);
          dds.setPassword(password);
          dds.setMaxActive(Integer.parseInt(maxactive));
          dds.setMaxWait(Integer.parseInt(maxwait));

          conn = dds.getConnection();
          ds = dds;
      }catch (SQLException e) {
        e.printStackTrace();
      }
      return conn;
  }

  /**
   * @param datasource java:comp/env/jdbc/xxxxx
   * @return
   * @throws GeneralException
   */
  public static Connection getConnection(String datasource)throws GeneralException
  {
      return getConnection();
  }

}
