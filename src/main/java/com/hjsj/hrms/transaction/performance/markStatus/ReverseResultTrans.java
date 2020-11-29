package com.hjsj.hrms.transaction.performance.markStatus;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *<p>Title:ReverseResultTrans.java</p> 
 *<p>Description:考评进度统计表结果反查</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 11, 2011</p> 
 *@author JinChunhai
 *@version 5.0
 */

public class ReverseResultTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		
		ArrayList list = new ArrayList();
		try
		{
			String plan_id=PubFunc.decrypt((String)this.getFormHM().get("checkPlanId"));    // 打分页面选中的考核计划id
			String selectFashion=(String)this.getFormHM().get("selectFashion");  // 查询方式 1:按考核主体  2:考核对象
			String object_type=(String)this.getFormHM().get("object_type");    // 1:部门  2:人员
			
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String b0110=PubFunc.decrypt((String)hm.get("b0110"));     // 单位
			String e0122=PubFunc.decrypt((String)hm.get("e0122"));     // 部门
			String type=(String)hm.get("type"); // 状态 
			
			// 获得反查结果集
			list = getReverseResultList(this.frameconn, this.userView, selectFashion, b0110, e0122, type, object_type, plan_id);			
			
			this.getFormHM().put("b0110",b0110);
			this.getFormHM().put("e0122",e0122);
			this.getFormHM().put("type",type);
			this.getFormHM().put("reverseResultList",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**	
	 * 反查结果记录
	 */
	public ArrayList getReverseResultList(Connection con,UserView userView,String selectFashion,String b0110,String e0122,String type,String object_type,String plan_id)
	{
		ArrayList list = new ArrayList();		
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(con);
			
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("SELECT B0110,E0122,personUint_id,status,username FROM per_statistic " );			
			sqlstr.append(" where username='" + userView.getUserName() + "' " );
			
			if("unit".equalsIgnoreCase(b0110))
				sqlstr.append(" and (b0110 is null or b0110='') ");	
			else if(!"b0110".equalsIgnoreCase(b0110))
				sqlstr.append(" and b0110='"+ b0110 +"' ");	
				
			if((e0122!=null && e0122.trim().length()>0) && ("e0122".equalsIgnoreCase(e0122)))
				sqlstr.append(" and (e0122 is null or e0122='') ");	
			else if(e0122!=null && e0122.trim().length()>0)
				sqlstr.append(" and e0122='"+ e0122 +"' ");	
			
			if(!"allScore".equalsIgnoreCase(type))
				sqlstr.append(" and status='"+ type +"' ");				
			
			rowSet = dao.search(sqlstr.toString());	
			StringBuffer buf = new StringBuffer();
			while(rowSet.next())
			{		
//				if(selectFashion.equalsIgnoreCase("1"))	
//					buf.append(",'" + isNull(rowSet.getString("personUint_id")) + "'");
//				else
					buf.append(",'" + isNull(rowSet.getString("personUint_id")) + "'");							
			}
			
			
			/****************************************    反查结果   **************************************/  
			StringBuffer sql = new StringBuffer();
			if((!"2".equalsIgnoreCase(object_type)) && ("2".equalsIgnoreCase(selectFashion)))
			{
				sql.append("SELECT codeitemid,codeitemdesc FROM organization where codesetid <> '@K' " );
				if(buf.length()>0)
					sql.append(" AND codeitemid IN (" + buf.substring(1) + ") order by A0000 " );	 
				else
					sql.append(" AND 1=2 " );									
			}
			else
			{
				if("2".equalsIgnoreCase(selectFashion))
				{
					sql.append("SELECT B0110,E0122,E01A1,A0101,object_id FROM per_object where plan_id='"+ plan_id +"' " );			
					if(buf.length()>0)
						sql.append(" and object_id IN (" + buf.substring(1) + ") order by A0000 " );	 
					else
						sql.append(" and 1=2 " );	 
				}else
				{
					sql.append("SELECT distinct mainbody_id,B0110,E0122,E01A1,A0101 FROM per_mainbody where plan_id='"+ plan_id +"' " );			
					if(buf.length()>0)
						sql.append(" and mainbody_id IN (" + buf.substring(1) + ") " );	 
					else
						sql.append(" and 1=2 " );
				}
			}			
			rowSet=dao.search(sql.toString());
			int num = 1;
			while(rowSet.next())
			{										
				LazyDynaBean abean = new LazyDynaBean();				
				if((!"2".equalsIgnoreCase(object_type)) && ("2".equalsIgnoreCase(selectFashion)))
				{
					abean.set("numbers", String.valueOf(num));
					String unitId = getParentid(rowSet.getString("codeitemid"),con); // 查找单位或部门所在的单位
					abean.set("b0110", AdminCode.getCodeName("UN", isNull(unitId)));
					abean.set("a0101", isNull(rowSet.getString("codeitemdesc")));
					num++;
				}
				else
				{
					abean.set("numbers", String.valueOf(num));
					abean.set("b0110", AdminCode.getCodeName("UN", isNull(rowSet.getString("B0110"))));
					abean.set("e0122", AdminCode.getCodeName("UM", isNull(rowSet.getString("E0122"))));
					abean.set("e01a1", AdminCode.getCodeName("@K", isNull(rowSet.getString("E01A1"))));
					abean.set("a0101", isNull(rowSet.getString("A0101")));
					num++;
				}					
				list.add(abean);												
			}
						
			if(rowSet!=null)
				rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	/**	
	 * 获得organization表部门的信息
	 */
	public LazyDynaBean getE0122List(String codeitemid,Connection con)
	{
		LazyDynaBean abean=new LazyDynaBean();
		RowSet rowSet=null;
		try
		{
			ContentDAO dao = new ContentDAO(con);
			rowSet=dao.search("select codesetid,codeitemid,parentid from organization where codeitemid='" + codeitemid + "'");
			while(rowSet.next())
		    {
		    	abean.set("codesetid",rowSet.getString("codesetid"));
		    	abean.set("codeitemid",rowSet.getString("codeitemid"));
		    	abean.set("parentid",rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"");		    			    			    	
		    }
			
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return abean;
	}
	/**
	 * 部门对应的继承关系
	 * @return
	 */
	public String getParentid(String codeitemid,Connection con)
	{
		String str = "";
		String parentid = "";
		String codesetid = "";
		LazyDynaBean abean = null;
		try
		{
			abean = (LazyDynaBean)getE0122List(codeitemid,con);			
			parentid = (String)abean.get("parentid");
			codesetid = (String)abean.get("codesetid");
			
			if(((parentid!=null) && parentid.trim().length()>0))
			{							
				if("UN".equalsIgnoreCase(codesetid))
				{
					str = codeitemid;
					return str;
				}
				else
				{	
					ArrayList linkList=new ArrayList();					
					getParent_id(linkList,parentid,con);									
					for(int i=0;i<linkList.size();i++)
					{
						str=(String)linkList.get(0);
					}				
				}
			}else
			{
				str = codeitemid;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}	
	public void getParent_id(ArrayList list,String codeitemid,Connection con)
	{
		String parentid = "";
		String codesetid = "";
		LazyDynaBean abean = null;
		try
		{
			abean = (LazyDynaBean)getE0122List(codeitemid,con);			
			parentid = (String)abean.get("parentid");
			codesetid = (String)abean.get("codesetid");
				
			if("UN".equalsIgnoreCase(codesetid))
			{
				list.add(codeitemid);
				return;
			}
			else
			{				
				getParent_id(list,parentid,con);	
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	public String isNull(String str)
    {
		if (str == null)
		    str = "";
		return str;
    }

}
