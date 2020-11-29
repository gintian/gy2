/*
 * 创建日期 2005-6-25
 *
 * TODO 要更改此生成的文件的模板，请转至
 * 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
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
 * @author luangaojiong
 * 
 * TODO 要更改此生成的类型注释的模板，请转至 窗口 － 首选项 － Java － 代码样式 － 代码模板
 */
public class SearchAppraiseMutualTrans extends IBusiness {

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

	String id = "0"; //计划号

	String template_id = "0";

	String objectId = "0";

	/***************************************************************************
	 * 入口执行函数
	 */

	public void execute() throws GeneralException {
			DbSecurityImpl dbS = new DbSecurityImpl();
			itemlist = new ArrayList();
			/**
			 * 取得所有要素表对象列表
			 */
			pointlist = getAllPointArrayList();
			/**
			 * 取得考核指标要素表集合
			 */
			PerPointValueBean ppvb = new PerPointValueBean(this.getFrameconn());
			perpointlist = ppvb.getPerPointValue();
			ArrayList gradelist=ppvb.getPerPointGradelist();
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String planFlag = hm.get("planFlag").toString();
			/**
			 * 得到计划号及对象objectid
			 */
			doPlanId();
			/**
			 * 得到权限控制 ArrayList对象
			 */
			PerforMutualBean pbean = new PerforMutualBean();

			pointpopedomlist = pbean.getPurviewContral(this.userView
					.getA0100(), objectId, pointlist, id, this.frameconn,template_id);

			/**
			 * 取模板项目表根目录id号码
			 */
			String rootId = "0";

			try 
			{
				rootlist = getRootNum(template_id);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			}

			/**
			 * 遍历目录取得项目编号及名称等信息列表
			 */

			sql2 = "SELECT item_id,itemdesc FROM per_template_item WHERE parent_id= ? and template_id='"
					+ template_id + "' ORDER BY item_id ASC";
			try {
				con = this.getFrameconn();
				// 打开Wallet
				dbS.open(con, sql2);
				statement = con.prepareStatement(sql2);
				StringBuffer menu = new StringBuffer();

				/**
				 * 得最大层数
				 */
				ArrayList templist = new ArrayList();

				for (int i = 0; i < rootlist.size(); i++) {
					nowMaxNum = 1;
					nowNum2=1;
					AppraiseselfForm af = (AppraiseselfForm) rootlist.get(i);
					MenuListOne(af.getItemid(), "", 0, false);
					af.setNowMaxLay(Integer.toString(nowMaxNum));
					templist.add(af);
				}
				rootlist = templist;

				/**
				 * 开始遍历
				 */
				for (int i = 0; i < rootlist.size(); i++) {
					nowNum = 1;
					AppraiseselfForm af = (AppraiseselfForm) rootlist.get(i);

					String outHtml = MenuList(af.getItemid(), "", 0, menu,false);
				}

				/**
				 * 从itemlist中清除项目要素表中不存在的项目
				 */

				/**
				 * 得到输出前台的动态表单
				 */

				StringBuffer sb2 = new StringBuffer();
				sb2.append("<table border=\"0\" cellspacing=\"0\"  align=\"center\" cellpadding=\"0\" class=\"ListTable\" width=\"700\">");

				/***************************************************************
				 * 多级栏目的处理
				 **************************************************************/
				PerforBeanMuilt pbm = new PerforBeanMuilt();
				itemlist = pbm.getPointNum(itemlist, pointlist); //添加项目叶要素数量
				//添加项目枝要素数量
				itemlist = pbm.getItemPonintNum(itemlist, maxNum, template_id,rootId, pointlist); 
				itemlist = pbm.clearItemlstNameIsNull(itemlist); //清除名称为空的值
				/**查找参数表*/
				Hashtable htxml=new Hashtable();
				LoadXml loadxml=new LoadXml(this.getFrameconn(),id);
				htxml=loadxml.getDegreeWhole();
				/**输出多级栏目字符*/
				String scoreflag=(String)htxml.get("scoreflag");
				PerforBeanHelp pbh = new PerforBeanHelp(template_id,id,objectId,this.userView.getA0100(),this.getFrameconn(),scoreflag);
				pbh.setAllgradelist(gradelist);
				pbh.setScoreflag((String)htxml.get("scoreflag"));
				/**是否提交标识*/
				this.getFormHM().put("insertUpdateFlag",pbh.getGrade());
				sb2.append(pbh.getMuiltString(itemlist, maxNum, template_id,rootId, rootlist, perpointlist, pointpopedomlist));
				this.getFormHM().put("pointreturnlist",	pbh.getTextNameArrayList()); //文本框名称ArrayList压入HashMap
				int columCount=maxNum+2;	//
				sb2.append("<tr><td align=\"center\" colspan=\"");
				sb2.append(columCount);
				sb2.append("\" >");
				/**
				 * 解析xml得到了解程度，总体评价标志
				 * 处理了解程度，总体评价
				 */
				getKnowWholeId(objectId,this.userView.getA0100(),id);
				sb2.append(getDegreeWholeEven(htxml));
				sb2.append("</td><tr>");
				sb2.append("</table>");
				this.getFormHM().put("outHtml", sb2.toString());
				this.getFormHM().put("SummaryFlag",htxml.get("SummaryFlag"));
				/**查询打分状态*/
				PerMainBody permainbody=new PerMainBody(this.getFrameconn());
				String status=permainbody.getEditStatus(objectId,this.userView.getA0100(),id);
				this.getFormHM().put("status",status);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw GeneralExceptionHandler.Handle(ex);
			} finally {
				PubFunc.closeResource(resultset);
				PubFunc.closeResource(statement);
				dbS.close(con);
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
				//System.out.println("--->SearchAppraiseMutalTrans-->getDegreeWholeEven-->1");
				return "";
			}
			else if("false".equals(ht.get("NodeKnowDegree").toString())
				 	&& "true".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseMutalTrans-->getDegreeWholeEven-->2");
				sb.append("<table><tr><td>总体评价</td><td>");
				sb.append(getWhole(ht));
				sb.append("</td></tr></table>");
			}
			else if("true".equals(ht.get("NodeKnowDegree").toString())
				 	&& "false".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseMutalTrans-->getDegreeWholeEven-->3");
				sb.append("<table><tr><td>了解程度</td><td>");
				sb.append(getKnowDegree());
				sb.append("</td></tr></table>");
			}
			else if("true".equals(ht.get("NodeKnowDegree").toString())
				 	&& "true".equals(ht.get("WholeEval").toString()))
			{
				//System.out.println("--->SearchAppraiseMutalTrans-->getDegreeWholeEven-->4");
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
	/**
	 * 处理计划号
	 * @return
	 */
	void doPlanId() 
	{
		/**
		 * 得到提交的对象id号
		 */
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");

		if (this.getFormHM().get("planNum") == null
				|| "0".equals(this.getFormHM().get("planNum").toString())
				|| "".equals(this.getFormHM().get("planNum").toString())) {
			this.getFormHM().put("outHtml", "");
			return;
		} else {
			id = this.getFormHM().get("planNum").toString();
		}

		if (hm.get("objectId") != null && !"".equals(hm.get("objectId"))) {
			objectId = hm.get("objectId").toString();
			this.getFormHM().put("objectId", objectId);
		}
		/**
		 * 没有对象id号传过来
		 */

		if ("0".equals(objectId)) {
			this.getFormHM().put("outHtml", "");
			return;
		} 
		else 
		{
			/**
			 * 有对象objectid号传过来
			 */
			String sql = "select plan_id,template_id from per_plan where  plan_id="+ id;
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try 
			{
				this.frowset = dao.search(sql);
				if (this.frowset.next()) 
				{
					id = this.frowset.getString("plan_id");
					template_id = this.frowset.getString("template_id");
				}

			} catch (Exception ex) {
				cat	.debug("----->com.hjsj.hrms.transaction.performance.SearchAppraiseMutalTrans--->read planid second error");
				ex.printStackTrace();
			}
			this.getFormHM().put("planNum", id);
		}
	}

	/***************************************************************************
	 * 取得所有要素列表
	 * 
	 * @return ArrayList
	 */
	public ArrayList getAllPointArrayList() {
		ArrayList list = new ArrayList();
		String sql = "select per_point.pointname,per_point.pointkind,per_template_point.point_id,per_template_point.item_id,per_template_point.score from per_template_point,per_point where per_template_point.point_id=per_point.point_id";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try {
			this.frowset = dao.search(sql);
			while (this.frowset.next()) {
				AppraiseselfForm af = new AppraiseselfForm();
				af.setPointName(this.frowset.getString("pointname"));
				af.setPointKind(this.frowset.getString("pointkind"));
				af.setPointId(this.frowset.getString("point_id"));
				af.setItemid(Integer.toString(this.frowset.getInt("item_id")));
				af.setScore(PubFunc.NullToZero(Double.toString(this.frowset	.getDouble("score"))));

				list.add(af);

			}
		} catch (Exception ex) {

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
			System.out
					.println("----->com.hjsj.hrms.transaction.performance.SearchAppraiseMutalTrans--->clearItemArrayList error");

			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		/**
		 * 执行清除操作
		 */
		ArrayList list2 = new ArrayList();

		for (int i = 0; i < lst.size(); i++) {
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
				MenuListOne((String) childList.elementAt(i), (String) nameList
						.elementAt(i), i, false);

			}

		}

	}

	/**
	 * 输出绩效考评表中数据采集部分页面内容
	 * @param parentId
	 * @param name
	 * @param position
	 * @param outHTML
	 * @param init
	 * @return
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
			e.printStackTrace();
		}

		int msize = childList.size();

		if (msize > 0) {
			++nowNum;

			AppraiseselfForm af = new AppraiseselfForm();
			af.setItemid(parentId);
			af.setItemName(name);
			af.setPointlst(getItemLstFromPoint(parentId));
			af.setLayer(Integer.toString(nowNum));
			itemlist.add(af);

			for (int i = 0; i < msize; i++) {
				MenuList((String) childList.elementAt(i), (String) nameList
						.elementAt(i), i, outHTML, false);

			}

		} else {
			AppraiseselfForm af = new AppraiseselfForm();
			af.setItemid(parentId);
			af.setItemName(name);
			af.setPointlst(getItemLstFromPoint(parentId));
			af.setLayer(Integer.toString(nowNum));
			itemlist.add(af);
		}

		return outHTML.toString();
	}

}