package com.hjsj.hrms.transaction.welcome;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
  * Title:调查提交操作
 * create time:2005-6-4:15:43:02
 * 
 * @author luangaojiong
 *  
 */
public class SubmitOper extends IBusiness {

	private String visibleUserName="";
	private String questionFlag="";
	/**
	 * 提交操作函数
	 */
	public void execute() throws GeneralException {
		 visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
		 if(visibleUserName==null|| "".equals(visibleUserName))
			visibleUserName="false";
		String pointid = "0";
		
		
		String homePageHotId = this.getFormHM().get("homePageHotId").toString();
		this.getFormHM().put("homePageHotId", homePageHotId);
		
		/**
		 * 得到项目id
		 */
		String itemid = "0";
		if (this.getFormHM().get("hotitemid") != null) {
			itemid = this.getFormHM().get("hotitemid").toString();
		} else {
			return;
		}

		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		questionFlag = (String) map.get("questionFlag");
		String sql = "";
		if(isExsit("investigate_result","staff_id","itemid"))
		{
			sql = " delete investigate_result  where staff_id=? and itemid=?  and create_user=?";
			deleteQuestionnaire(itemid,sql);
		} 
		/*
		 * //验证判断
		 *  
		 
		String searchsql = "select * from investigate_result where staff_id='";

		StringBuffer insertsql = new StringBuffer();
		insertsql.append(searchsql);
		insertsql.append((visibleUserName.equalsIgnoreCase("false")?this.userView.getUserName():this.userView.getUserFullName()));
		insertsql.append("' and itemid='");
		insertsql.append(itemid);
		insertsql.append("'");
		try {

			this.frowset = dao.search(insertsql.toString());
			if (frowset.next()) {
				this.getFormHM().put("successmsg", "抱歉，您已提交过此问卷!");
				return;
			} else {
				this.getFormHM().put("successmsg", "");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}*/
		/**
		 * 判断是否有提交的文本
		 */
		if(this.getFormHM().get("multTextlist")==null)
		{
			doNoneTxtPoint(itemid); 
		}
		else
		{
			HashMap hm=(HashMap)this.getFormHM().get("multTextlist");
			/**
			 * 如果有文本内容
			 */
			if(hm.size()>0)
			{
				doTxtPoint(itemid,hm);
			}
			else
			{
				doNoneTxtPoint(itemid); 
			}
		}
			
	}
	public void deleteQuestionnaire(String itemid,String sql)
	{
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList paramList = new ArrayList();
			paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			paramList.add(itemid);
			paramList.add(this.userView.getUserName());
	 		int num = dao.update(sql,paramList);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public boolean isExsit(String tableName,String colomunName1,String colomunName2)
	{
		boolean b = false;
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from "+tableName+" where staff_id="+colomunName1+" and itemid="+colomunName2+" ");
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs =dao.search(sb.toString());
			if (rs.next()) {
				b = true;
			}
			//System.out.println("----->flag is "+flag);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	
	/**
	 * 有文本内容的要素处理
	 * @param itemid
	 */
	public void doTxtPoint(String itemid,HashMap hm)
	{
		/**
		 * 暂存提交的要素文本
		 */
		Hashtable ht=new Hashtable();
		ArrayList list=(ArrayList)this.getFormHM().get("hotchecklst");
		if(list.size()<=0)
		{
			return;
		}
				
		Set key=hm.keySet();
		Iterator iter=key.iterator();
		while(iter.hasNext())
		{
			String typeKey=iter.next().toString();
			String typeValue=hm.get(typeKey).toString();
			if(typeKey.length()>6)
			{
				ht.put(typeKey.substring(6,typeKey.length()),typeValue);
			}
			//System.out.println("SubmitOper-->key is -->"+typeKey+" value is--->"+typeValue);
			//System.out.println("SubmitOper-->key substring is -->"+typeKey.substring(6,typeKey.length()));
			
		}
		/**
		 * 添加要素操作
		 */
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		
		String sql="insert into investigate_result(staff_id,pointid,itemid,context,state,create_user) values(?,?,?,?,?,?)";
		int flag=1;
		try
		{
			for(int i=0;i<list.size();i++)
			{
				paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				paramList.add(list.get(i).toString());
				paramList.add(itemid);
				paramList.add(getPointContext(ht,list.get(i).toString()));
				paramList.add(Integer.parseInt(questionFlag));
				paramList.add(this.userView.getUserName());//提交的时候保存当前用户名  zhaoxg add 2014-7-30
				int num=dao.update(sql,paramList);
				if(num==0)
				{
					flag=0;
				}
			}
			
			if(flag==0)
			{
				if("2".equals(questionFlag))
					this.getFormHM().put("successmsg","提交不成功!");
				else
					this.getFormHM().put("successmsg","保存不成功!");
			}
			else
			{
				if("2".equals(questionFlag))
					this.getFormHM().put("successmsg","提交成功!");
				else
					this.getFormHM().put("successmsg","保存成功!");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
				
	}
	
	/**
	 * 返回提交的要素内容
	 * @param itemid
	 */
	public String getPointContext(Hashtable ht,String pointid)
	{
		String temp="";
		if(ht.containsKey(pointid))
		{
			temp=ht.get(pointid).toString();
		}
		else
		{
			temp="";
		}
		return temp;
	}
	
	/**
	 *没有文本内容的要素处理 
	 */
	public void doNoneTxtPoint(String itemid) 
	{
		ArrayList list=(ArrayList)this.getFormHM().get("hotchecklst");
		if(list.size()<=0)
		{
			return;
		}
		
		/**
		 * 添加要素
		 */
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList paramList = new ArrayList();
		String sql="insert into investigate_result(staff_id,pointid,itemid,state,create_user) values(?,?,?,?,?)";
		int flag=1;
		try
		{
			for(int i=0;i<list.size();i++)
			{
				paramList.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				paramList.add(list.get(i).toString());
				paramList.add(itemid);
				paramList.add(Integer.parseInt(questionFlag));
				paramList.add(this.userView.getUserName());//提交的时候保存当前用户名  zhaoxg add 2014-7-30
				int num=dao.update(sql,paramList);
				if(num==0)
				{
					flag=0;
				}
			}
			
			if(flag==0)
			{
				if("2".equals(questionFlag))
					this.getFormHM().put("successmsg","提交不成功!");
				else
					this.getFormHM().put("successmsg","保存不成功!");
			}
			else
			{
				if("2".equals(questionFlag))
					this.getFormHM().put("successmsg","提交成功!");
				else
					this.getFormHM().put("successmsg","保存成功!");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	
	


}