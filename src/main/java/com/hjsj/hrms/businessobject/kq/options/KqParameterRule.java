package com.hjsj.hrms.businessobject.kq.options;

import java.sql.Connection;

/**
 * 考勤参数规则
 * <p>Title:KqParameterRule.java</p>
 * <p>Description: 此类功能完全重复，不再使用。取考勤参数请用KqParam。</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 2, 2007 5:03:39 PM</p>
 * @author sunxin
 * @version 1.0
 * 
 * @modify zxj
 * @deprecated
 */
public class KqParameterRule {

   private Connection conn;   
   private String rule_name;
   public KqParameterRule(){}
   public KqParameterRule(Connection conn)
   {
	   this.conn=conn;	   
   }

}
