/*
 * Created on 2005-9-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_exam;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SaveSortExamCondTrans</p>
 * <p>Description:保存排序条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 07, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveSortExamCondTrans extends IBusiness {
	public String idToName(String a0100){
		String name = "";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
	    {
			RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
			String dbpre = "";
	        if(rv!=null)
	        {
	            dbpre=rv.getString("str_value");
	            String sql = "select a0101 from "+dbpre+"a01 where a0100 = '"+a0100+"'";
		        this.frowset = dao.search(sql);
		        while(this.frowset.next()){
		         	if(this.getFrowset().getString("a0101") == null || "".equals(this.getFrowset().getString("a0101"))){
		          	    name = ""; 
		         	}else{
		         	 	name = this.getFrowset().getString("a0101"); 
		         	}
		         } 
	        }

	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	    }
		return name;
	}
	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		ArrayList nameList=new ArrayList();
		ArrayList fieldList = new ArrayList();
		String[] fieldsetvalue=(String[])this.getFormHM().get("fieldsetvalue");
		String ssql = "select * from zp_exam_report";
		try{
			ResultSet rset = dao.search(ssql,list);
			int num = rset.getMetaData().getColumnCount();
			for(int j=1;j<=num;j++){
				if(!"SUM_SCORE".equals((rset.getMetaData().getColumnName(j)).toUpperCase())){
				   fieldList.add(rset.getMetaData().getColumnName(j));
				}
			}
			fieldList.add("sum_score");
		}catch(SQLException e){
			 e.printStackTrace();
		     throw GeneralExceptionHandler.Handle(e);
		}
		StringBuffer fieldsetstr = new StringBuffer();
	     if(fieldsetvalue!=null && fieldsetvalue.length>0)
		 { 
	     	fieldsetstr.append("order by ");
		 	for(int i=0;i<fieldsetvalue.length;i++){
		 		fieldsetstr.append(fieldsetvalue[i]);       //把需要排序的字段放到StringBuffer中
		 		if(i<fieldsetvalue.length-1)
		 			fieldsetstr.append(",");
		 	}
		 }
	    
	     RecordVo rv= ConstantParamter.getRealConstantVo("ZP_DBNAME");
			String dbpre = "";
			String sql="";
			 if(rv!=null)
		     {
		            dbpre=rv.getString("str_value");
		            sql = "select zp_exam_report.*," + dbpre+"a01.a0101 from zp_exam_report," +dbpre+"a01 where " +dbpre+"a01.a0100=zp_exam_report.a0100 "+fieldsetstr.toString(); 
			 }			 
			 else
	           sql = "select * from zp_exam_report order by "+fieldsetstr.toString();
	     try{
	         ResultSet rs = dao.search(sql,list);
	         while(rs.next()){
	         	DynaBean vo = new LazyDynaBean();
		      	vo.set("a0100",rs.getString("a0100"));
		      	vo.set("a0101",idToName(rs.getString("a0100")));
	            int count = rs.getMetaData().getColumnCount();
	            for(int i=1;i<=count;i++){
	              	 if(!"A0100".equals((rs.getMetaData().getColumnName(i)).toUpperCase()) && !"SUM_SCORE".equals((rs.getMetaData().getColumnName(i)).toUpperCase())){
	              	 	if((rs.getString(rs.getMetaData().getColumnName(i))) == null || "".equals(rs.getString(rs.getMetaData().getColumnName(i)))){
	               	       vo.set(rs.getMetaData().getColumnName(i),"");
	                     }else{
	                      if(!"A0101".equals((rs.getMetaData().getColumnName(i)).toUpperCase()))
	                       vo.set(rs.getMetaData().getColumnName(i),String.valueOf(rs.getDouble(rs.getMetaData().getColumnName(i))));
	                      else
	                    	  vo.set(rs.getMetaData().getColumnName(i),rs.getString(rs.getMetaData().getColumnName(i)));  
	                     }
	                 }
	             }
	            if(rs.getString("sum_score") == null || "".equals(rs.getString("sum_score"))){
	           	    vo.set("sum_score","");
	            }else{
	                vo.set("sum_score",String.valueOf(rs.getDouble("sum_score")));
	            }
	            nameList.add(vo);
	         }
	     }catch(SQLException sqle){
	    	sqle.printStackTrace();
		    throw GeneralExceptionHandler.Handle(sqle);
	    }finally{
	    	this.getFormHM().put("sortCondList",nameList);
	    	this.getFormHM().put("fieldList",fieldList);
	    }
	}
}

