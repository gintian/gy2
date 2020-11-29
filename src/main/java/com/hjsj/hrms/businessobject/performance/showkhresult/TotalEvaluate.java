package com.hjsj.hrms.businessobject.performance.showkhresult;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class TotalEvaluate {
	Connection conn=null;
	
	public TotalEvaluate(Connection conn)
	{
		this.conn=conn;
	}
	
	
	
	public String getTitle(String objectid)
	{
		String titlename="";
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select a0101 from usra01 where a0100='"+objectid+"'");
			if(rowSet.next())
			{
				titlename=rowSet.getString(1)+"&nbsp; 的总体评价图示";
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return titlename;
	}
	
	
	/**
	 * 取得 总体评价 各指标的投票数
	 * @param planid
	 * @param objectid
	 * @return
	 */
	public ArrayList getTotalEvaluateLineList(String planid,String objectid)
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			HashMap map=new HashMap();
			String sql="select whole_grade_id,count(id) from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"' group by whole_grade_id";
			RowSet rowSet=dao.search(sql);	
			while(rowSet.next())
			{
				map.put(rowSet.getString(1),rowSet.getString(2));
			}
			
			LoadXml loadxml=new LoadXml(this.conn,planid);
			Hashtable htxml=new Hashtable();		
			htxml=loadxml.getDegreeWhole();
			String gradeClass=(String)htxml.get("GradeClass");					//等级分类ID
			sql="select pds.id,pds.itemname from per_degree pd,per_degreedesc pds where pd.degree_id=pds.degree_id and pd.degree_id="+gradeClass;
			rowSet=dao.search(sql);
			while(rowSet.next())
			{
				String id=rowSet.getString(1);
				String itemname=rowSet.getString(2);
				CommonData data=null;
				if(map.get(id)!=null)
				{
					data=new CommonData((String)map.get(id),itemname);
				}
				else
					data=new CommonData("0",itemname);
				list.add(data);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 得到 评语和意见列表
	 * @param planid  计划id
	 * @param objectid 考核对象
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getRemarkList(String planid,String objectid) throws GeneralException 
	{
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rowset=dao.search("select *  from per_mainbody where plan_id="+planid+" and object_id='"+objectid+"'");
			while(rowset.next())
			{
				String context=Sql_switcher.readMemo(rowset,"description").replaceAll("@#@","<br>");
				context=context.replaceAll("\r\n","<br>");
				context=context.replaceAll(" ","&nbsp;");
				if(context.trim().length()>0)
					list.add(context);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
		return list;
	}
	
	
	
	
}
