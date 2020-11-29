/*
 * 创建日期 2005-8-11
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.welcome;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
/**
 * @author luangaojiong
 * 多选列表提交操作
 */
public class SaveInvMultTrans extends IBusiness {

	private String visibleUserName="";
	private String questionFlag="";
	/* （非 Javadoc）
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO 自动生成方法存根
		visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
		if(visibleUserName==null|| "".equals(visibleUserName))
			visibleUserName="false";
		String itemid=this.getFormHM().get("hotitemid").toString();
		String homePageHotId = this.getFormHM().get("homePageHotId").toString();
		this.getFormHM().put("homePageHotId", homePageHotId);
		if("".equals(itemid))
		{
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
		ArrayList list=(ArrayList)this.getFormHM().get("hotmultchecklst");
		if(list.size()<=0)
		{
			return;
		}
		/**
		 * 有效验证
		 */
		/*if(userCheck(itemid))
		{
			return;
		}*/
		
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
			//System.out.println("SaveInvMultTrans-->key is -->"+typeKey+" value is--->"+typeValue);
			//System.out.println("SaveInvMultTrans-->key substring is -->"+typeKey.substring(6,typeKey.length()));
			
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
				paramList.add(this.userView.getUserName());
				int num = dao.update(sql,paramList);
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
		ArrayList list=(ArrayList)this.getFormHM().get("hotmultchecklst");
		if(list.size()<=0)
		{
			return;
		}
		/**
		 * 有效验证
		 */
		/*if(userCheck(itemid))
		{
			return;
		}*/
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
				paramList.add(("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName()));
				paramList.add(list.get(i).toString());
				paramList.add(itemid);
				paramList.add(Integer.parseInt(questionFlag));
				paramList.add( this.userView.getUserName());//提交的时候保存当前用户名  zhaoxg add 2014-7-30
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
	 * 用户提交验证
	 * @author luangaojiong
	 *
	 */
	
	public boolean userCheck(String itemid) 
	{
		StringBuffer sb=new StringBuffer();
		sb.append("select pointid from investigate_result where ");
		sb.append("( staff_id='");
		sb.append(("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName()));
		sb.append("' and itemid='");
		sb.append(itemid);	
		sb.append("')");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		int flag=0;
		try
		{
		
			ResultSet rs=null;
			rs=dao.search(sb.toString());
			if(rs.next())
			{
				flag=1;
			}
			rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		if(flag==1)
		{
			this.getFormHM().put("successmsg","你已提交了该题目!");
			return true;
		}
		else
		{
			this.getFormHM().put("successmsg","");
			return false;
		}
		
	}

}
