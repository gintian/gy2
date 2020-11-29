package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.actionform.performance.AppraiseselfForm;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.interfaces.performance.PerMainBody;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * 
 * @author luangaojiong
 * @version 1.0
 *  
 */
public class SearchAppraiseselfTrans extends IBusiness {

	/**
	 * 初始化变量
	 */
	String knowId="";   //了解程度id
	String wholeEvenId="";	//总体评价id
	ArrayList itemlist = new ArrayList(); //项目

	ArrayList pointlist = new ArrayList(); //要素

	ArrayList rootlist = new ArrayList(); //根目录

	ArrayList perpointlist = new ArrayList(); //考核指标要素表

	ArrayList pointpopedomlist = new ArrayList(); //权限控制ArrayList

	Connection con = null;

	PreparedStatement statement;

	ResultSet resultset;

	String sql2 = "";

	int nowNum = 1;
	int nowNum2=1;	//计算层次
	int maxNum = 1;

	int nowMaxNum = 1;
	Hashtable htxml=new Hashtable();

	/***************************************************************************
	 * 入口执行函数
	 */

	public void execute() throws GeneralException {
		DbSecurityImpl dbS = new DbSecurityImpl();
	 try
	 {
		if (this.userView.getStatus() != 4) 
		{
			throw new GeneralException("", "非自助平台用户不能使用该功能!", "", "");
		}
		this.getFormHM().put("strSQL","select plan_id,name from per_plan where status=? and plan_id in  (select plan_id from per_mainbody where object_id=mainbody_id and mainbody_id='"+this.userView.getA0100()+"') and gather_type=0");
		itemlist = new ArrayList();
		/**取得考核指标要素表集合*/
		PerPointValueBean ppvb = new PerPointValueBean(this.getFrameconn());
		perpointlist = ppvb.getPerPointValue();
		ArrayList gradelist=ppvb.getPerPointGradelist();		
		/**得到提交的planId号*/
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String id = "0"; //计划号
		String template_id = "0";

		if (hm.get("planId") != null) 
		{
			id = hm.get("planId").toString();
		}
		/**第一次进入判断标识*/
		String planFlag = hm.get("planFlag").toString();

		if ("0".equals(planFlag))
		{
			this.getFormHM().put("planNum", "");
			this.getFormHM().put("nowPlanNum", "");
			this.getFormHM().put("outHtml","");
			return;
		} 
		else 
		{
			/**没有计划id号传过来*/
			if ("0".equals(id))
			{
				String sql = "select plan_id,template_id from per_plan where status=5";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try
				{
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						id = this.frowset.getString("plan_id");
						template_id = this.frowset.getString("template_id");
					}
				} catch (Exception ex) {
					cat.debug("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->read planid first error");
					ex.printStackTrace();
				}
				this.getFormHM().put("outHtml", "");
				return;
			} 
			else 
			{
				/**有计划id号传过来*/
				/**取得所有要素表对象列表*/
				String sql = "select plan_id,template_id from per_plan where  plan_id='"
						+ id + "'";
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				try {
					this.frowset = dao.search(sql);
					if (this.frowset.next()) {
						id = this.frowset.getString("plan_id");
						template_id = this.frowset.getString("template_id");
					}
					pointlist = getAllPointArrayList(template_id);

				} catch (Exception ex) {
					cat	.debug("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->read planid second error");
					ex.printStackTrace();
				}
				this.getFormHM().put("planNum", id);
				this.getFormHM().put("nowPlanNum", id);
			}
			
			/**得到用户权限 ArrayList对象*/
			PerforBean pbean = new PerforBean(this.getFrameconn());

			pointpopedomlist = pbean.getPurviewContral(this.userView.getA0100(), pointlist, id,template_id);
			/**取模板项目表根目录id号码*/
			String rootId = "0";
			try 
			{
				rootlist = getRootNum(template_id);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}
			/**遍历目录取得项目编号及名称等信息列表*/
			sql2 = "SELECT item_id,itemdesc FROM per_template_item WHERE parent_id= ? and template_id='"
					+ template_id + "' ORDER BY item_id ASC";
			try 
			{
				con = this.getFrameconn();
				// 打开Wallet
				dbS.open(con, sql2);
				statement = con.prepareStatement(sql2);
				StringBuffer menu = new StringBuffer();

				/**得最大层数*/
				ArrayList templist = new ArrayList();
				for (int i = 0; i < rootlist.size(); i++) 
				{
					nowMaxNum = 1;
					nowNum2=1;
					AppraiseselfForm af = (AppraiseselfForm) rootlist.get(i);
					MenuListOne(af.getItemid(), "", 0, false);
					af.setNowMaxLay(Integer.toString(nowMaxNum));	//动态设当前根目录最大层
					templist.add(af);
				}
				rootlist = templist;
				/**开始遍历*/
				for (int i = 0; i < rootlist.size(); i++) 
				{
					nowNum = 1;
					AppraiseselfForm af = (AppraiseselfForm) rootlist.get(i);
					String outHtml = MenuList(af.getItemid(), "", 0, menu,false);
				}
				/**保存清除之前的ArrayList*/
				ArrayList beforelist = itemlist; 
				/**得到输出前台的动态表单*/
				StringBuffer sb2 = new StringBuffer();
				sb2	.append("<table border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"ListTable\" width=\"700\">");
				/***************************************************************
				 * 多级栏目的处理
				 **************************************************************/
				PerforBeanMuilt pbm = new PerforBeanMuilt();

				itemlist = pbm.getPointNum(itemlist, pointlist); //添加项目叶要素数量

				itemlist = pbm.getItemPonintNum(itemlist, maxNum, template_id,
						rootId, pointlist); //添加项目枝要素数量

				itemlist = pbm.clearItemlstNameIsNull(itemlist); //清除名称为空的值
				LoadXml loadxml=new LoadXml(this.getFrameconn(),id);
				htxml=loadxml.getDegreeWhole();
				/**输出多级栏目字符*/
				String scoreflag=(String)htxml.get("scoreflag");
				PerforBeanHelp pbh = new PerforBeanHelp(template_id,id,this.userView.getA0100(),this.userView.getA0100(),this.getFrameconn(),scoreflag);
				pbh.setAllgradelist(gradelist);
				pbh.setScoreflag((String)htxml.get("scoreflag"));				
				this.getFormHM().put("insertUpdateFlag",pbh.getGrade()); //得到是否提交标识

				sb2.append(pbh.getMuiltString(itemlist, maxNum, template_id,
						rootId, rootlist, perpointlist, pointpopedomlist));
				this.getFormHM().put("pointreturnlist",	pbh.getTextNameArrayList()); //文本框名称ArrayList压入HashMap
				int columCount=maxNum+2;	//
				sb2.append("<tr><td align=\"center\" colspan=\"");
				sb2.append(columCount);
				sb2.append("\" >");
				/**************************************
				 * 解析xml得到了解程度，总体评价标志
				 * 处理了解程度，总体评价
				 **************************************/
				getKnowWholeId(this.userView.getA0100(),this.userView.getA0100(),id);	//得到了解程度，总体评价id号

				sb2.append(getDegreeWholeEven(htxml));
				sb2.append("</td><tr>");
				sb2.append("</table>");
				this.getFormHM().put("outHtml", sb2.toString());
				this.getFormHM().put("SummaryFlag",htxml.get("SummaryFlag"));
				/**查询打分状态*/
				PerMainBody permainbody=new PerMainBody(this.getFrameconn());
				String status=permainbody.getEditStatus(this.userView.getA0100(),this.userView.getA0100(),id);
				this.getFormHM().put("status",status);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			} finally {
				PubFunc.closeResource(resultset);
				PubFunc.closeResource(statement);
				dbS.close(con);
			}
		}
	 }
	 catch(Exception ee)
	 {
		 ee.printStackTrace();
		 throw GeneralExceptionHandler.Handle(ee);		 
	 }
	}
	/**
	 * 得到总体评价了解程度id
	 *
	 */
	public void getKnowWholeId(String objectId,String userId,String planId)
	{
		StringBuffer sb=new StringBuffer();
		sb.append("select whole_grade_id,know_id from per_mainbody where object_id='");
		sb.append(objectId);
		sb.append("' and mainbody_id='");
		sb.append(userId);
		sb.append("' and plan_id=");
		sb.append(planId);
		 ContentDAO dao = new ContentDAO(this.getFrameconn());
		try(
			ResultSet rs=dao.search(sb.toString());
		)
		{
			if(rs.next())
			{
				wholeEvenId=PubFunc.nullToStr(rs.getString("whole_grade_id"));
				knowId=PubFunc.nullToStr(rs.getString("know_id"));
			}
		}
		catch(Exception ex)
		{
			System.out.println("----->SearchAppraiseselfTrans--->getKnowWholeId-->is error");
			ex.printStackTrace();
		}
		
	}
	/**
	 * 处理了解程度，总体评价
	 * @return
	 */
	String getDegreeWholeEven(Hashtable ht)
	{
		StringBuffer sb=new StringBuffer();
		if(ht.size()<=0)
		{
			return "";
		}
		else
		{
			if("false".equals(ht.get("NodeKnowDegree").toString())
				 	&& "false".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseselfTrans-->getDegreeWholeEven-->1");
				return "";
			}
			else if("false".equals(ht.get("NodeKnowDegree").toString())
				 	&& "true".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseselfTrans-->getDegreeWholeEven-->2");
				sb.append("<table><tr><td>总体评价</td><td>");
				sb.append(getWhole(ht));
				sb.append("</td></tr></table>");
			}
			else if("true".equals(ht.get("NodeKnowDegree").toString())
				 	&& "false".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseselfTrans-->getDegreeWholeEven-->3");
				sb.append("<table><tr><td>了解程度</td><td>");
				sb.append(getKnowDegree());
				sb.append("</td></tr></table>");
			}
			else if("true".equals(ht.get("NodeKnowDegree").toString())
				 	&& "true".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseselfTrans-->getDegreeWholeEven-->4");
				sb.append("<table><tr><td>了解程度</td><td>");
				sb.append(getKnowDegree());
				sb.append("</td><td width=\"20\"></td><td>总体评价");
				sb.append(getWhole(ht));
				sb.append("</td></tr></table>");
			}
				
		}
		
		return sb.toString();
	}
	/**
	 * 得到总体评价等级字符
	 * @return
	 */
	public String getWhole(Hashtable ht)
	{
		StringBuffer sb=new StringBuffer();
		String wholeid=ht.get("GradeClass").toString();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try(
			ResultSet rs=dao.search("select id,itemname from per_degreedesc where degree_id="+wholeid);

		)
		{
			sb.append("<select name=\"wholeEven\" >");
			while(rs.next())
			{
				String wholeId=PubFunc.NullToZero(rs.getString("id"));
				String wholeName=PubFunc.nullToStr(rs.getString("itemname"));
				if(wholeId.trim().equals(this.wholeEvenId.trim()))
				{
				sb.append("<option value=\"");
				sb.append(wholeId);
				sb.append("\" selected >");
				sb.append(wholeName);
				sb.append("</option>");
				}
				else
				{
					sb.append("<option value=\"");
					sb.append(wholeId);
					sb.append("\" >");
					sb.append(wholeName);
					sb.append("</option>");
				}
			}
			sb.append("</select>");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 得到了解程度字符
	 * @return
	 */
	public String getKnowDegree()
	{
		StringBuffer sb=new StringBuffer();


		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try(
			ResultSet rs=dao.search("select know_id ,name from per_know where status=1");
		)
		{
			sb.append("<select name=\"knowDegree\" >");
			while(rs.next())
			{
				String knowId=PubFunc.NullToZero(rs.getString("know_id"));
				String knowName=PubFunc.nullToStr(rs.getString("name"));
				if(knowId.trim().equals(this.knowId.trim()))
				{
					sb.append("<option value=\"");
					sb.append(knowId);
					sb.append("\" selected >");
					sb.append(knowName);
					sb.append("</option>");
				}
				else
				{
					sb.append("<option value=\"");
					sb.append(knowId);
					sb.append("\">");
					sb.append(knowName);
					sb.append("</option>");
				}
			}
			sb.append("</select>");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return sb.toString();
	}
	
	/***************************************************************************
	 * 取得所有要素列表
	 * 
	 * @return ArrayList
	 */
	public ArrayList getAllPointArrayList(String template_id) {
		ArrayList list = new ArrayList();
		StringBuffer sb=new StringBuffer();
		
		String sql = "select per_point.pointname,per_point.pointkind,per_template_point.point_id,per_template_point.item_id,per_template_point.score from per_template_point,per_point where (per_template_point.point_id=per_point.point_id) and ";
		sb.append(sql);
		sb.append(" per_template_point.item_id in ( select item_id from per_template_item where template_id='");
		sb.append(template_id);
		sb.append("')");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sb.toString());
			while (this.frowset.next()) {
				AppraiseselfForm af = new AppraiseselfForm();
				af.setPointName(PubFunc.nullToStr(this.frowset.getString("pointname")));
				af.setPointKind(this.frowset.getString("pointkind"));
				af.setPointId(PubFunc.NullToZero(this.frowset.getString("point_id")));
				af.setItemid(Integer.toString(this.frowset.getInt("item_id")));
				af.setScore(PubFunc.NullToZero(Double.toString(this.frowset	.getDouble("score"))));
				list.add(af);
			}
		} catch (Exception ex) {
			System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans-getAllPointArrayList--> error");
			ex.printStackTrace();
		}
		return list;
	}

	/***************************************************************************
	 * 从ArrayList清除项目要素表中不存在的项目
	 */

	public ArrayList clearItemArrayList(ArrayList lst) throws Exception {
		/**
		 * 得到要素模板中所有itemid
		 */
		ArrayList list = new ArrayList();
		String sql = "select item_id from per_template_point group by item_id";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				String id = this.frowset.getString("item_id");
				if (!list.contains(id)) {
					list.add(id);
				}
			}
		} catch (Exception ex) {
			System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->clearItemArrayList error");

			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		/**
		 * 执行清除操作
		 */
		ArrayList list2 = new ArrayList();

		for (int i = 0; i < lst.size(); i++) 
		{
			AppraiseselfForm af = (AppraiseselfForm) lst.get(i);
			if (list.contains(af.getItemid())) {
				list2.add(af);
			}
		}

		return list2;

	}

	/**
	 * 取模板项目表根目录id号码函数
	 */
	public ArrayList getRootNum(String template_id) throws Exception {
		String sql2 = "";
		ArrayList list = new ArrayList();

		String id = "0";
		sql2 = "SELECT item_id,template_id,itemdesc FROM per_template_item WHERE  template_id='"
				+ template_id + "'and parent_id is null ORDER BY item_id ASC";

		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql2);
			while (this.frowset.next()) {
				AppraiseselfForm af = new AppraiseselfForm();
				id = this.frowset.getString("item_id");
				String name = this.frowset.getString("itemdesc");
				template_id = this.frowset.getString("template_id");
				af.setPointlst(getItemLstFromPoint(id));
				af.setItemid(id);
				af.setItemName(name);
				af.setLayer(Integer.toString(nowNum));
				itemlist.add(af);//加入根目录信息
				list.add(af);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return list;
	}

	/**
	 * 在递中得到项目的要素ArrayList
	 */
	public ArrayList getItemLstFromPoint(String itemid) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < pointlist.size(); i++) {
			AppraiseselfForm af = (AppraiseselfForm) pointlist.get(i);
			if (af.getItemid().equals(itemid)) {
				list.add(af);
			}
		}
		return list;
	}

	/**
	 * 计算最深的层次
	 */
	public void MenuListOne(String parentId, String name, int position,
			boolean init) {
		Vector childList = new Vector();
		Vector nameList = new Vector();
		StringBuffer outHTML = new StringBuffer();
		try {

			statement.setString(1, parentId);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				childList.addElement(resultset.getString("item_id"));
				nameList.addElement(resultset.getString("itemdesc"));

			}
		} catch (Exception e) {
			System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->MenuListOne error");
			e.printStackTrace();
		}

		int msize = childList.size();
		if (msize > 0) {
			++nowNum2;
			if (nowNum2 > maxNum) {
				maxNum = nowNum2;
			}
			//取项目最大层
			if (nowNum2 > nowMaxNum) {
				nowMaxNum = nowNum2;
			}

			for (int i = 0; i < msize; i++) {
				MenuListOne((String) childList.elementAt(i), (String) nameList.elementAt(i), i, false);

			}

		}

	}

	/**
	 * 
	 * 递归遍历项目列表
	 */
	public String MenuList(String parentId, String name, int position,
			StringBuffer outHTML, boolean init) {
		Vector childList = new Vector();
		Vector nameList = new Vector();
		try {

			statement.setString(1, parentId);
			resultset = statement.executeQuery();
			while (resultset.next()) {
				childList.addElement(resultset.getString("item_id"));
				nameList.addElement(resultset.getString("itemdesc"));

			}
		} catch (Exception e) {
			System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->MenuList error");
		}

		int msize = childList.size();

		if (msize > 0) {
			++nowNum;

			//if (!init)
			//{
			//不为根节点

			AppraiseselfForm af = new AppraiseselfForm();
			af.setItemid(parentId);
			af.setItemName(name);
			af.setPointlst(getItemLstFromPoint(parentId));
			af.setLayer(Integer.toString(nowNum));
			itemlist.add(af);
		//	System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->MenuList--->itemid-->"+parentId);
			//}

			for (int i = 0; i < msize; i++) {
				MenuList((String) childList.elementAt(i), (String) nameList	.elementAt(i), i, outHTML, false);

			}

		} else {
			AppraiseselfForm af = new AppraiseselfForm();
			af.setItemid(parentId);
			af.setItemName(name);
			af.setPointlst(getItemLstFromPoint(parentId));
			af.setLayer(Integer.toString(nowNum));
			itemlist.add(af);
			//System.out.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseselfTrans--->MenuList--->itemid-->"+parentId);
		}

		return outHTML.toString();
	}

}