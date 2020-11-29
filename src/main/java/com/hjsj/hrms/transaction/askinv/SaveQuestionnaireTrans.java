/*
 * 创建日期 2005-7-12
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
package com.hjsj.hrms.transaction.askinv;

import com.hjsj.hrms.actionform.welcome.WelcomeForm;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author luangaojiong 保存问卷内容
 */
public class SaveQuestionnaireTrans extends IBusiness {
	String flag = "0"; //验证标记
	private String visibleUserName="";

	/*
	 * （非 Javadoc）
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		try {

			/**
			 * 得到要保存的HashMap
			 */
			WelcomeForm wf=new WelcomeForm();
		    visibleUserName = SystemConfig.getPropertyValue("investigate_visible_username");
			if(visibleUserName==null|| "".equals(visibleUserName))
				visibleUserName="false";
			ArrayList itemlist = (ArrayList) this.getFormHM().get("SelectItemSave");
			ArrayList askedlist=new ArrayList();
			if(this.getFormHM().get("ItemQuestionSave")!=null)
			{
				askedlist=(ArrayList)this.getFormHM().get("ItemQuestionSave");
			}

		
			/**
			 * 验证空
			 */
			if(itemlist.size()<=0 && askedlist.size()<=0)
			{
				this.getFormHM().put("message", "请选择题目!");
				return;
			}
			String id="0";
			if(this.getFormHM().get("topicid2")!=null && !"".equals(this.getFormHM().get("topicid2").toString()))
			{
				id=this.getFormHM().get("topicid2").toString().trim();
			}
			//System.out.println("---->SaveQuestionnaireTrans--->id->"+this.getFormHM().get("id"));
			/**
			 * 验证用户是否已提交该选择题
			 * 验证用户是否提交过项目问答
			 */
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String questionFlag = (String) hm.get("questionFlag");
			/*if("1".equals(questionFlag)){
				if(userAskExamine(askedlist,id))
				{
					return;
				}
			}*/
			
			/**
			 * 保存选择题操作
			 */
			/**
			 * 得到描述文本框
			 */
			ArrayList describelist=new ArrayList();
			if(this.getFormHM().get("DescribeTextSave")!=null)
			{
				describelist=(ArrayList)this.getFormHM().get("DescribeTextSave");
			}
			
		/*	if("1".equals(questionFlag)){
				saveQuestionnaire(itemlist,describelist);
				*//**
				 * 保存问答题操作
				 *//*
				saveAskQuestion(askedlist);
			}*/
			//else if("2".equals(questionFlag)){
				String sql = "";
				if(isExsit("investigate_content","staff_id","itemid"))
				{
				    sql = " delete from investigate_content  where staff_id=? and itemid=?  and create_user=?";
                    deleteQuestionnaire(askedlist,sql);
				} 
				sql = " insert into investigate_content (context,state,staff_id,itemid,create_user) values(?,?,?,?,?)";
				updateQuestion(askedlist,sql,questionFlag);
				if(isExsit("investigate_result","staff_id","itemid"))
				{
					sql = " delete from investigate_result  where staff_id=? and itemid=?  and create_user=?";
					deleteQuestionnaire(itemlist,sql);
				} 
				sql = " insert into investigate_result (pointid,context,staff_id,itemid,state,create_user) values(?,?,?,?,?,?) ";
				updateQuestionnaire(itemlist,describelist,sql,questionFlag);
			//}
			
				/*this.getFormHM().put("answerList", this.getFormHM().get("answerList"));
				this.getFormHM().put("answerDesc", this.getFormHM().get("answerDesc"));
				this.getFormHM().put("essayDesc", this.getFormHM().get("essayDesc"));*/
			
		} catch (Exception ex) {
			throw GeneralExceptionHandler.Handle(ex);
		}

	}
	public void deleteQuestionnaire(ArrayList saveList,String sql)
	{
		try(PreparedStatement st = this.getFrameconn().prepareStatement(sql)) {

			for(int i=0;i<saveList.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)saveList.get(i);
				st.setString(1, "false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
		 		String itemid=wf.getItemid();
		 		st.setString(2,itemid);
		 		st.setString(3, this.userView.getUserName());
		 		int num = st.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public boolean isExsit(String tableName,String colomunName1,String colomunName2)
	{
		boolean b = false;
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from "+tableName+" where staff_id="+colomunName1+" and itemid="+colomunName2+"");
		try {
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs = dao.search(sb.toString());
			if (rs.next()) {
				b = true;
			}
			//System.out.println("----->flag is "+flag);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return b;
	}
	public void updateQuestionnaire(ArrayList  saveList,ArrayList describelist,String sql,String questionFlag) {
		
		//String sql = "insert into investigate_result (pointid,context,staff_id,itemid) values(?,?,?,?)";
		int flag1 = 0;
		try (PreparedStatement st = this.getFrameconn().prepareStatement(sql)){

		 
			for(int i=0;i<saveList.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)saveList.get(i);
				
				 st.setString(3, "false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				 ArrayList list=wf.getPointList();
				
				 	for(int j=0;j<list.size();j++)
				 	{
				 		String itemid=wf.getItemid();
				 		String pointid=list.get(j).toString();
				 		st.setString(1,pointid);
				 		st.setString(4,itemid);
				 		st.setString(2,getText(describelist,itemid,pointid));
				 		st.setInt(5, Integer.parseInt(questionFlag));
				 		st.setString(6, this.userView.getUserName());
				 		int num = st.executeUpdate();
				 		if (num == 0) {
						flag1 = 1;
				 		}
				 	}
				 }
			
			
			if (flag1 == 1) {
				if("2".equals(questionFlag))
					this.getFormHM().put("message", "提交不成功");
				else
					this.getFormHM().put("message", "保存不成功");
			} else {
				if("2".equals(questionFlag))
					this.getFormHM().put("message", "提交成功");
				else
					this.getFormHM().put("message", "保存成功");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void updateQuestion(ArrayList askedlist,String sql,String questionFlag)
	{
		int flag1 = 0;
		PreparedStatement st=null;
		try 
		{
			st= this.getFrameconn().prepareStatement(sql);
			
			for(int i=0;i<askedlist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)askedlist.get(i);
				st.setString(3, "false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				st.setString(4,wf.getItemid());
				String contont = wf.getItemContext().replaceAll("\r\n", "<br>");
				st.setString(1,contont);
				st.setInt(2, Integer.parseInt(questionFlag));
				st.setString(5, this.userView.getUserName());
				int num = st.executeUpdate();
		 		if (num == 0) 
		 			flag1 = 1;
			}
			
			if (flag1 == 1) {
				if("2".equals(questionFlag))
					this.getFormHM().put("message", "提交不成功");
				else
					this.getFormHM().put("message", "保存不成功");
			} else {
				if("2".equals(questionFlag))
					this.getFormHM().put("message", "提交成功");
				else
					this.getFormHM().put("message", "保存成功");
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if(st!=null)
				{
					st.close();
					st=null;
				}
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
		
	}
	/**
	 * 保存问答题
	 * @param savelist
	 * @return
	 */
	void saveAskQuestion(ArrayList askedlist)
	{
		String sql = "insert into investigate_content (staff_id,itemid,context) values(?,?,?)";
		int flag1 = 0;
		try 
		{
		    ContentDAO dao = new ContentDAO(this.frameconn);
			for(int i=0;i<askedlist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)askedlist.get(i);
				
				ArrayList params = new ArrayList();
				params.add("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
				params.add(wf.getItemid());
				params.add(wf.getItemContext());

				int num = dao.update(sql, params);
		 		if (num == 0) 
		 			flag1 = 1;
			}
			
			if (flag1 == 1) {
				this.getFormHM().put("message", "提交不成功");
			} else {
				this.getFormHM().put("message", "提交成功");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * 问答题验证
	 * @param savelist
	 * @return
	 */
	
	public boolean userAskExamine(ArrayList savelist,String id)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("select itemid from investigate_content where staff_id='");
		sb.append(("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName()));
		sb.append("' and itemid in (select itemid from investigate_item where id='");
		sb.append(id);
		sb.append("') union select itemid from investigate_result where staff_id='");
		sb.append(("false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName()));
		sb.append("' and itemid in (select itemid from investigate_item where id='");
		sb.append(id);
		sb.append("')");
		/*
		
		System.out.println("----->SaveQuestionnaireTrans--userAskExamine->"+sql1);
		
		String flag="0";
		String sql = "select itemid from investigate_content where (";
		StringBuffer sb = new StringBuffer();
		sb.append(sql);
		for (int i = 0; i < savelist.size(); i++) {
			sb.append(" itemid='");
			sb.append(((WelcomeForm)savelist.get(i)).getItemid());
			sb.append("' or ");

		}

		sb.append("1>2 )");
		sb.append(" and ( staff_id='");
		sb.append(this.userView.getUserFullName());
		sb.append("')");
		*/
		try {
		    ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs = dao.search(sb.toString());
			flag = "0";
			if (rs.next()) {
				flag = "1";
			}
			//System.out.println("----->flag is "+flag);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if ("1".equals(flag)) {
			this.getFormHM().put("message", "抱歉，您已提交过此问卷");
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * 用户验证操作
	 * 
	 * @author luangaojiong
	 *  
	 */
	/*
	public boolean userExamine(ArrayList savelist) {
		String flag="0";
		String sql = "select itemid from investigate_result where (";
		StringBuffer sb = new StringBuffer();
		sb.append(sql);
		for (int i = 0; i < savelist.size(); i++) {
			sb.append(" itemid='");
			sb.append(((WelcomeForm)savelist.get(i)).getItemid());
			sb.append("' or ");

		}

		sb.append("1>2 )");
		sb.append(" and ( staff_id='");
		sb.append(this.userView.getUserFullName());
		sb.append("')");
		
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs = dao.search(sb.toString());
			flag = "0";
			if (rs.next()) {
				flag = "1";
			}
			//System.out.println("----->flag is "+flag);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		if (flag.equals("1")) {
			this.getFormHM().put("message", "你已提交过题目");
			return true;
		}
		else
		{
			return false;
		}
	}
	*/
	
	/**
	 * 保存问卷调查操作
	 * 
	 * @param hmSave
	 */
	public void saveQuestionnaire(ArrayList  saveList,ArrayList describelist) {
		
		String sql = "insert into investigate_result (staff_id,pointid,itemid,context) values(?,?,?,?)";
		int flag1 = 0;
		/**
		 * 如果不存在项目
		 */
		if ("0".equals(flag)) {
			try (PreparedStatement st = this.getFrameconn().prepareStatement(sql)){

			 
				for(int i=0;i<saveList.size();i++)
				{
					WelcomeForm wf=(WelcomeForm)saveList.get(i);
					
					 st.setString(1, "false".equalsIgnoreCase(visibleUserName)?this.userView.getUserName():this.userView.getUserFullName());
					 ArrayList list=wf.getPointList();
					
					 	for(int j=0;j<list.size();j++)
					 	{
					 		String itemid=wf.getItemid();
					 		String pointid=list.get(j).toString();
					 		st.setString(2,pointid);
					 		st.setString(3,itemid);
					 		st.setString(4,getText(describelist,itemid,pointid));
					 		int num = st.executeUpdate();
					 		if (num == 0) {
							flag1 = 1;
					 		}
					 	}
					 }
				
				
				if (flag1 == 1) {
					this.getFormHM().put("message", "提交不成功");
				} else {
					this.getFormHM().put("message", "提交成功");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		else
		{
			this.getFormHM().put("message", "你已提交过题目");
		}

	}
	/**
	 * 得到描述文本
	 * @param describelist
	 * @return
	 */
	public String getText(ArrayList describelist,String itemid,String pointid)
	{
		String temp="";
		if(describelist.size()<=0)
		{
			return "";
		}
		else
		{
			for(int i=0;i<describelist.size();i++)
			{
				WelcomeForm wf=(WelcomeForm)describelist.get(i);
			
				if(wf.getItemid().equals(itemid) && wf.getPointid().equals(pointid))
				{
					temp=wf.getPointContext();
					break;
				}
			}
			
		}
		return temp;
	}

}