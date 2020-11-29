package com.hjsj.hrms.businessobject.hire.jp_contest.param;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * 
 *<p>Title:EngageParam.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 19, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class EngageParam {
	private Connection conn;
	public EngageParam(Connection conn)
	{
		this.conn=conn;
	}
	
	/**
	 * 得到竞聘岗位显示列表
	 * @param app_view
	 * @return
	 */
	public ArrayList getAppList(String app_view){
	   ArrayList list = new ArrayList();
	   ContentDAO dao=new ContentDAO(this.conn);
	   String apps[] = app_view.split(",");
	   StringBuffer sql = new StringBuffer();
	   sql.append("select itemid,itemdesc from  t_hr_busifield where Upper(itemid) in( ");
	   for(int i=0;i<apps.length;i++){
		   sql.append("'"+apps[i].toUpperCase()+"',");
	   }
	   sql.setLength(sql.length()-1);
	   sql.append(")");
	   try {
		   RowSet rs=dao.search(sql.toString());
		   while(rs.next()){
			   CommonData appdate =new CommonData();
			   if("z0701".equalsIgnoreCase(rs.getString("itemid"))) {
                   appdate.setDataName("岗位名称");
               } else {
                   appdate.setDataName(rs.getString("itemdesc"));
               }
				appdate.setDataValue(rs.getString("itemid"));
				list.add(appdate);
		   }
		} catch (SQLException e) {e.printStackTrace();}
		return list;
   }
	/**
	 * 解析竞聘指标list成字符串
	 * @param code_fields
	 * @return 
	 */
	public String getAppMesslist(ArrayList code_fields)
	{
	   	StringBuffer mess=new StringBuffer();
	   	if(code_fields==null||code_fields.size()<=0) {
            return "";
        }
	   	String sql="";
	   	try
	   	{
	  		ContentDAO dao=new ContentDAO (conn);
	   		mess.append("<br>");
	   		int r=1;
	   		StringBuffer inStr=new StringBuffer();
	   		for(int i=0;i<code_fields.size();i++)
	   	{
	   			inStr.append("'"+code_fields.get(i).toString()+"',");
	    	}
	   		if(inStr==null||inStr.length()<=0) {
                return "";
            }
	  		inStr.setLength(inStr.length()-1);
	   		sql="select itemdesc  from t_hr_busifield where Upper(itemid) in("+inStr.toString().toUpperCase()+") order by itemid";
	   		RowSet rs=dao.search(sql);
	   		while(rs.next())
	   		{
	   			 mess.append(rs.getString("itemdesc"));
	   			 if(r%5==0) {
                     mess.append("<br>");
                 } else {
                     mess.append(",");
                 }
	    	    r++;
	   		}
	   		mess.append("<br>");
	   	}catch(Exception e){e.printStackTrace();}
	   	return mess.toString();
	 }
	
	/**
	* 得到指标
	* @param field
	* @return 
	*/
	public ArrayList getFields(String field)
	{
		String[] fields=field.split(",");
		ArrayList list=new ArrayList();
		if(fields==null||fields.length<=0) {
            return list;
        }
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			String sql="";
			for(int i=0;i<fields.length;i++)
			{
				
				String itemid=fields[i];
				if(itemid!=null&& "b0110".equals(itemid))
				{
					CommonData data=new CommonData();
					data.setDataName("单位名称");
					data.setDataValue(itemid);
					list.add(data);
				}
	    		if(itemid!=null&& "e01a1".equals(itemid))
	    		{
					CommonData data=new CommonData();
					data.setDataName("职位名称");
					data.setDataValue(itemid);
					list.add(data);
				}		    				 
				sql="select itemdesc from fielditem where Upper(itemid)='"+itemid.toUpperCase()+"'";
				RowSet rs=dao.search(sql);
				if(rs.next())
				{
					CommonData data=new CommonData();
					data.setDataName(rs.getString("itemdesc"));
					data.setDataValue(itemid);
					list.add(data);
				}
			}
		}catch(Exception e)
		{
			
		}			
		return list;
	}

	 /**
	 * 得到人员表
	 * @param gcond
	 * @return 
	 */
	 public ArrayList getSelectRname(String gcond)
	 {
		    if(gcond==null||gcond.length()<=0) {
                gcond="";
            }
	    	String gconds[]=gcond.split(",");
	    	ArrayList list =new ArrayList();
	    	if(gconds==null||gconds.length<=0) {
                return list;
            }
	    	StringBuffer sql=new StringBuffer();
	    	sql.append("select * from rname where ");
	    	sql.append(" tabid in(");
	    	for(int i=0;i<gconds.length;i++)
	    	{
	    		sql.append("'"+gconds[i]+"',");
	    	}
	    	sql.setLength(sql.length()-1);
	    	sql.append(")");
	    	sql.append(" order by tabid");
	    	ContentDAO dao=new ContentDAO(this.conn);	    	
	    	try
	    	{
	    		CommonData dataobj =null;
	    		RowSet rs=dao.search(sql.toString());
	    		while(rs.next())
	    		{
	    			dataobj=new CommonData();
					dataobj.setDataName(rs.getString("name"));
					dataobj.setDataValue(rs.getString("tabid"));
					list.add(dataobj);
	    		}
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	    	return list;
	  }
	 /**
	  * 解析竞聘岗位指标字符串
	  * @param app_view
	  * @return 
	  */
	 public String getAppViewMess(String app_view){
		 String mess = "";
		 String[] apps = app_view.split(",");
		 ArrayList list = new ArrayList();
		 for(int i=0;i<apps.length;i++){
			 list.add(apps[i]);
		 }
		 mess = this.getAppMesslist(list);
		 return mess;
	 }
}
