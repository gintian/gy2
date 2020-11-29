/*
 * 创建日期 2005-8-10
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.welcome;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong
 *  
 * 添加热点调查中问答题
 *
 */
public class InvQuestion extends IBusiness {
	
	private String visibleUserName="";
	private String questionFlag="";

	
	public void execute() throws GeneralException {
		
		visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
		if(visibleUserName==null|| "".equals(visibleUserName))
			visibleUserName="false";
		
		String itemid="0";
		String content="";
		String homePageHotId = this.getFormHM().get("homePageHotId").toString();
		this.getFormHM().put("homePageHotId", homePageHotId);
		if(this.getFormHM().get("hotquestion")!=null)
		{
			//System.out.println("--->com.hjsj.hrms.transaction.welcome.InvQuestion#execute "+this.getFormHM().get("hotquestion"));
			content=this.getFormHM().get("hotquestion").toString();
		}
		
		if(this.getFormHM().get("hotitemid")!=null)
		{
			itemid=this.getFormHM().get("hotitemid").toString();
		}
		
		if("".equals(content.trim()))
		{
			this.getFormHM().put("successmsg","请输入内容!");
			return;
		}
		Connection con=this.getFrameconn();
		/**
		 * 用户提交验证
		 */
		/*StringBuffer sb=new StringBuffer();
		sb.append("select * from investigate_content where staff_id='");
		sb.append(this.userView.getUserFullName());
		sb.append("' and itemid='");
		sb.append(itemid);
		sb.append("'");
		PreparedStatement ps=null;
		Connection con=this.getFrameconn();
		int flag=0;
		try
		{
			ps=con.prepareStatement(sb.toString());
			ps.executeQuery();
			ResultSet rs=ps.getResultSet();
			if(rs.next())
			{
				
				flag=1;
			}
			else
			{
				flag=0;
			}
			rs.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		*//**
		 * 已提交
		 *//*
		if(flag==1)
		{
			this.getFormHM().put("successmsg","你已提交过该题目!");
			return;
		}*/
		
		HashMap map = (HashMap) this.getFormHM().get("requestPamaHM");
		questionFlag = (String) map.get("questionFlag");
		String sql = "";
		if(isExsit("investigate_content","staff_id","itemid"))
		{
			sql = " delete investigate_content  where staff_id=? and itemid=?  and create_user=?";
			deleteQuestionnaire(itemid,sql);
		} 
		
		{
			StringBuffer sbsql=new StringBuffer();
			sbsql.append("insert into investigate_content(staff_id,itemid,context,state,create_user) values ('");
			sbsql.append("false".equalsIgnoreCase(this.visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
			sbsql.append("','");
			sbsql.append(itemid);
			sbsql.append("','");
			content = content.replaceAll("\r\n", "<br>");
			sbsql.append(content);
			sbsql.append("', '");
			sbsql.append(questionFlag);
			sbsql.append("','"+this.userView.getUserName()+"')");
			try
			{
				ContentDAO dao = new ContentDAO(con);
				int num=dao.update(sbsql.toString());
				if(num==0)
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
				throw GeneralExceptionHandler.Handle(ex);
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

}
