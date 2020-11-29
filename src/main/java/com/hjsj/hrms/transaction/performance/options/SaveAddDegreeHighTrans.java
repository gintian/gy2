package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.businessobject.performance.options.PerDegreeBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:SaveAddDegreeHighTrans.java</p>
 * <p>Description:保存添加等级分类高级设置</p>
 * <p>Company:hjsj</p>
 * <p>create time:2008-07-17 11:11:11</p>
 * @author JinChunhai
 * @version 1.0 
 */

public class SaveAddDegreeHighTrans extends IBusiness
{

    public void execute() throws GeneralException
    {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String degreeID = (String) hm.get("degreeID");
		String num= (String) hm.get("num");
		String mode= (String) hm.get("mode");
		String oper= (String) hm.get("oper");
		String value= (String) hm.get("value");
		String grouped= (String) hm.get("grouped");
		if(grouped==null || grouped.trim().length()<=0)
			grouped = (String)this.getFormHM().get("grouped");		
		String UMGrade= (String) hm.get("UMGrade");
		UMGrade=  com.hrms.frame.codec.SafeCode.decode(UMGrade);
		String plan_id = (String) this.getFormHM().get("plan_id");
		
		PerDegreeBo bo = new PerDegreeBo(this.frameconn, degreeID, plan_id);
		ArrayList degrees = bo.getDegrees();
		// 如果为编辑,num能取到值
		String degreeValues = (String) hm.get("degreeValues");
		String[] degrVals = degreeValues.split("@");
			
		String tableName = "degree_highset";
		StringBuffer strSql = new StringBuffer();
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		
		if ("".equals(num))// 添加保存
		{
		    StringBuffer buf = new StringBuffer();
		    num = bo.getNum();
		    strSql.append("insert into ");
		    strSql.append(tableName);
		    strSql.append("(num1,seq,mode1,oper,value,UMGrade,");
	
		    buf.append("values (?,?,?,?,?,?,");
	
		    value=value==null?"0":("".equals(value)?"0":value);
		    
		    list.add(num);
		    list.add(num);
		    list.add(mode);
		    list.add(oper);
		    list.add(value);
		    list.add(UMGrade);
		    
		    for (int i = 0; i < degrees.size(); i++)
		    {
				LazyDynaBean bean = (LazyDynaBean) degrees.get(i);
				String id = (String) bean.get("id");
				String field = "degree" + id;
				strSql.append(field + ",");
				buf.append("?,");
		    }
	
		    for (int i = 0; i < degrVals.length; i++)
		    {
				if ("0".equals(degrVals[i]) || "1".equals(degrVals[i]))
				    list.add(degrVals[i]);
		    }
	
		    strSql.append("grouped)");
		    buf.append("?)");
		    list.add(grouped.toUpperCase());
	
		    String insertSql = strSql.toString() + buf.toString();
		    try
		    {
		    	dao.insert(insertSql, list);
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }finally
		    {	
				this.getFormHM().put("itemCount", new Integer(degrees.size()));
				this.getFormHM().put("degrees",degrees);	
		        ArrayList data = bo.getData();
		        this.getFormHM().put("extpro",data);		      
		        bo.setUsed();
		        this.getFormHM().put("qy",bo.getUsed());			
		    }
		} else
		{
		    strSql.append("update ");
		    strSql.append(tableName);
		    strSql.append(" set mode1=?,oper=?,value=?,grouped=?,UMGrade=?,");
		    
		    list.add(mode);
		    list.add(oper);
		    list.add(value);
		    list.add(grouped.toUpperCase());
		    list.add(UMGrade);
		    
		    for (int i = 0; i < degrees.size(); i++)
		    {
				LazyDynaBean bean = (LazyDynaBean) degrees.get(i);
				String id = (String) bean.get("id");
				String field = "degree" + id;
				strSql.append(field + "=?,");  
		    }	    
	
		    for (int i = 0; i < degrVals.length; i++)
		    {
				if ("0".equals(degrVals[i]) || "1".equals(degrVals[i]))
				    list.add(degrVals[i]);
		    }	    
		    
		    strSql.setLength(strSql.length()-1);
		    strSql.append(" where num1=?");
		    list.add(num);
		    try
		    {
		    	dao.update(strSql.toString(), list);
		    } catch (SQLException e)
		    {
		    	e.printStackTrace();
		    }finally
		    {
	
				this.getFormHM().put("itemCount", new Integer(degrees.size()));
				this.getFormHM().put("degrees",degrees);
	
		        ArrayList data = bo.getData();
		        this.getFormHM().put("extpro",data);
		      
		        bo.setUsed();
		        this.getFormHM().put("qy",bo.getUsed());
			
		    }
		}

    }

}
