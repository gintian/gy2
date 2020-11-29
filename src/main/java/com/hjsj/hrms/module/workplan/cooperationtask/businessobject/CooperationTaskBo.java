package com.hjsj.hrms.module.workplan.cooperationtask.businessobject;

import com.hjsj.hrms.businessobject.workplan.WorkPlanBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanOperationLogBo;
import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Title:        CooperationTaskBo.java
 * @Description:  工作计划协办任务公共bo类
 * @Company:      hjsj     
 * @Create time:  2016-6-8 10:16:55
 */
public class CooperationTaskBo {

	private Connection conn = null;
	private UserView userview;
	private WorkPlanUtil wpu;
	private WorkPlanBo wpb;


	public CooperationTaskBo(Connection conn, UserView userview) {
		super();
		this.conn = conn;
		this.userview = userview;
		this.wpu = new WorkPlanUtil(conn,userview);
		this.wpb = new WorkPlanBo(conn,userview);
	}

	/**
	 * 获取协作任务审批表头list
	 * @param fieldList：数据字典列表
	 * @return
	 */
	public ArrayList<ColumnsInfo> getCoopColumnList(ArrayList<FieldItem> fieldList){
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		try{
			if(fieldList!=null&&fieldList.size()!=0){
				int i = 0;
				for(FieldItem item : fieldList){
					String itemid = item.getItemid();//字段id
					String itemtype = item.getItemtype();//字段类型
					String codesetid = item.getCodesetid();//关联的代码			
					String columndesc = item.getItemdesc();//字段描述
					int itemlength = item.getItemlength();//字段长度
					String state = item.getState();//0隐藏  1显示
					ColumnsInfo columnInfo = getColumnInfo(itemid, columndesc, 150, itemtype);
					if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
						if("0".equals(codesetid) || codesetid == null){//非代码字符型
							//获得字段描述
							if("P1001".equalsIgnoreCase(itemid)){
								columnInfo.setColumnLength(itemlength);
								columnInfo.setCodesetId("0");
								columnInfo.setEncrypted(true);
								columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							}else if("p1015".equalsIgnoreCase(itemid)){
								columnInfo.setColumnLength(itemlength);
								columnInfo.setCodesetId("0");
								columnInfo.setRendererFunc("coopTaskApprove_me.changeCooper");
							}else{
								columnInfo.setColumnLength(itemlength);
								columnInfo.setCodesetId("0");
								if("0".equals(state)||"p1019".equalsIgnoreCase(itemid)){
									columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
								}
							}
						}else{//代码型字符
							columnInfo.setColumnLength(itemlength);
							columnInfo.setCodesetId(codesetid);
							if("0".equals(state)){
								columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
							}
						}
					} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
						columnInfo.setColumnLength(itemlength);
						columnInfo.setCodesetId("0");
						if("0".equals(state)){
							columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
						}
					}
					columnInfo.setEditableValidFunc("false");
					columnTmp.add(columnInfo);
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return columnTmp;
	}

	/**
	 * 获取我的协作任务表头list
	 * @param fieldList：数据字典列表
	 * @return
	 */
	public ArrayList<ColumnsInfo> getMyCoopColumnList(ArrayList<FieldItem> fieldList){
		ArrayList<ColumnsInfo> columnTmp = new ArrayList<ColumnsInfo>();
		try{
			if(fieldList!=null&&fieldList.size()!=0){
				for(FieldItem item : fieldList){
					String itemid = item.getItemid();//字段id
					String itemtype = item.getItemtype();//字段类型
					String codesetid = item.getCodesetid();//关联的代码			
					String columndesc = item.getItemdesc();//字段描述
					int itemlength = item.getItemlength();//字段长度
					String state = item.getState();//0隐藏  1显示

					ColumnsInfo columnInfo = getColumnInfo(itemid, columndesc, 150, itemtype);
					columnInfo.setReadOnly(true);
					if("A".equals(itemtype)){//A:字符型  D:日期型 N:数值型  M:备注型
						if("0".equals(codesetid) || codesetid == null){//非代码字符型
							//获得字段描述
							if("P1001".equalsIgnoreCase(itemid)){
								columnInfo.setColumnLength(itemlength);
								columnInfo.setCodesetId("0");
								columnInfo.setEncrypted(true);
								columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							}else{
								columnInfo.setColumnLength(itemlength);
								columnInfo.setCodesetId("0");
								if("0".equals(state)){
									columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
								}
							}
						}else{//代码型字符
							columnInfo.setColumnLength(itemlength);
							columnInfo.setCodesetId(codesetid);
							if("0".equals(state)){
								columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
							}
						}
					} else if("D".equals(itemtype)||"N".equals(itemtype)||"M".equals(itemtype)){//日期型。数值。备注
						columnInfo.setColumnLength(itemlength);
						columnInfo.setCodesetId("0");
						if("0".equals(state)){
							columnInfo.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
						}
					}
					columnInfo.setEditableValidFunc("false");
					if("p1019".equalsIgnoreCase(itemid))
						columnTmp.add(0,columnInfo);
					else
						columnTmp.add(columnInfo);
				}
				ColumnsInfo columnInfo = getColumnInfo("A0101", "当前审批人", 200, "A");
				columnInfo.setColumnLength(200);
				columnInfo.setReadOnly(true);
				columnTmp.add(0,columnInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			GeneralExceptionHandler.Handle(e);
		}
		return columnTmp;
	}

	/**
	 * 初始化控件列对象
	 * @param columnId
	 * @param columnDesc：名称
	 * @param columnWidth：显示列宽
	 * @param type：类型
	 * @return
	 */
	private ColumnsInfo getColumnInfo(String columnId, String columnDesc, int columnWidth, String type) {
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnType(type);// 类型N|M|A|D
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		if ("A".equals(type)) {
			columnsInfo.setCodesetId("0");
		}
		columnsInfo.setDecimalWidth(0);// 小数位
		// 数值和日期默认居右
		if ("D".equals(type) || "N".equals(type))
			columnsInfo.setTextAlign("right");
		return columnsInfo;
	}

	/**
	 * 获取协作任务审批页面功能按钮
	 * @return
	 */
	public ArrayList<Object> getCoopButtonList(){

		ArrayList<Object> buttonList = new ArrayList<Object>();
		try{
			ButtonInfo buttonInfo = new ButtonInfo();
			//toolbar里的竖线
			buttonList.add("-");
			//批准按钮
			buttonInfo = new ButtonInfo("批准", "coopTaskApprove_me.approveCoop");
			buttonInfo.setId("coopTaskApprove_approveCoop");
			buttonList.add(buttonInfo);
			//退回按钮
			buttonInfo = new ButtonInfo("退回", "coopTaskApprove_me.backCoop");
			buttonInfo.setId("coopTaskApprove_deletePerson");
			buttonList.add(buttonInfo);
			//toolbar里的填充
			//buttonList.add("->");
			//搜索框
			ButtonInfo searchBox = new ButtonInfo();
			searchBox.setFunctionId("WP00001002");//查询所走的交易号
			searchBox.setText("请输入姓名、email、单位名、部门名或任务名");//blank text
			searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
			searchBox.setShowPlanBox(false);//不显示查询方案
			buttonList.add(searchBox);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}

	/**
	 * 获取我的协作任务页面功能按钮
	 * @return
	 */
	public ArrayList<Object> getMyCoopButtonList(){

		ArrayList<Object> buttonList = new ArrayList<Object>();
		try{
			ButtonInfo buttonInfo = new ButtonInfo();
			//toolbar里的竖线
			buttonList.add("-");
			//批准按钮
			buttonInfo = new ButtonInfo("提醒批准协作任务申请", "myCoopTask_me.coopRemind");
			buttonInfo.setId("myCoopTask_coopRemind");
			buttonList.add(buttonInfo);
			//toolbar里的填充
			//buttonList.add("->");
			//搜索框
			ButtonInfo searchBox = new ButtonInfo();
			searchBox.setFunctionId("WP00001007");//查询所走的交易号
			searchBox.setText("请输入姓名、email、单位名、部门名或任务名");//blank text
			searchBox.setType(ButtonInfo.TYPE_QUERYBOX);//类型 查询框
			searchBox.setShowPlanBox(false);//不显示查询方案
			buttonList.add(searchBox);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return buttonList;
	}

	/**
	 * 通过人员编号和库前缀获取GUIDKEY
	 * @param userview 
	 * */
	public String getGuidKey(String a0100,String nbase){
		String guidKey = "";
//		String a0100 = this.userview.getA0100();
//		String nbase = this.userview.getDbname();
		StringBuffer sb = new StringBuffer();
		sb.append("select GUIDKEY from  ");
		sb.append(nbase);
		sb.append("A01  where A0100 = ?");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList al = new ArrayList();
		al.add(a0100);
		try {
			rs = dao.search(sb.toString(), al);
			if(rs.next()){
				guidKey = rs.getString("GUIDKEY");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return guidKey;
//		return "22C61D72-4CFF-4F5A-8D38-7460C78E93EF";
	}

	/**
	 * 通过GUIDKEY和库前缀获取人员编号
	 * @param userview 
	 * */
	public String getA0100(String guidKey,String nbase){
		String A0100 = "";
//		String a0100 = this.userview.getA0100();
//		String nbase = this.userview.getDbname();
		StringBuffer sb = new StringBuffer();
		sb.append("select A0100 from  ");
		sb.append(nbase);
		sb.append("A01  where guidKey = ?");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList al = new ArrayList();
		al.add(guidKey);
		try {
			rs = dao.search(sb.toString(), al);
			if(rs.next()){
				A0100 = rs.getString("A0100");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return A0100;
//		return "22C61D72-4CFF-4F5A-8D38-7460C78E93EF";
	}
	
	/**
	 * 获取姓名全称
	 * @param userview 
	 * */
	public String getUserFullName(String guidKey,String nbase){
		String A0101 = "";
//		String a0100 = this.userview.getA0100();
//		String nbase = this.userview.getDbname();
		StringBuffer sb = new StringBuffer();
		sb.append("select A0101 from  ");
		sb.append(nbase);
		sb.append("A01  where guidKey = ?");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		ArrayList al = new ArrayList();
		al.add(guidKey);
		try {
			rs = dao.search(sb.toString(), al);
			if(rs.next()){
				A0101 = rs.getString("A0101");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return A0101;
//		return "22C61D72-4CFF-4F5A-8D38-7460C78E93EF";
	}

	/**
	 * 审批协作任务
	 * @param 审批类型（int） type  1：批准 2：驳回
	 * @param 选中的元素list  al （ArrayList<String>）  
	 * */
	public void approveCoopTask(int type,ArrayList<String> al){
		
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			/** 更新协作任务的审批状态 */
			String flag = "";
			if(type==1)
				flag = "02";
			else if(type==2)
				flag = "03";

			String p1001Str = "";
			for(String p1001 : al){
				p1001Str += "'"+PubFunc.decrypt(p1001)+"',";
			}
			p1001Str = p1001Str.substring(0, p1001Str.length()-1);
			if(p1001Str.length() == 0){
				p1001Str = "''";
			}

			String insertSql = "UPDATE P10 SET P1019 = '"+flag+"' WHERE p1001 in ("+p1001Str+")";
			dao.update(insertSql);

			/** 驳回时、继续更新任务的审批状态为起草状态 */
			if(type == 2){
				String updateSql = "update p08 set p0811='01' where p0800 in (select distinct p0800 from p10 where p1001 in("+p1001Str+"))";
				dao.update(updateSql);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * */
	public void sendApprovedCoopTask(int type,ArrayList<String> al){
		//生成更改的sql语句
		String inSql = "";
		RowSet rs = null;
		RowSet rs1 = null;
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT P0800,GUIDKE_CREATER,GUIDKE_CREATER_SP,GUIDKE_OWNER,GUIDKE_OWNER_SP,P1009 FROM P10 WHERE p1001 in (");
		for(String p1001 : al){
			inSql+="'"+PubFunc.decrypt(p1001)+"',";
		}
		inSql = inSql.substring(0,inSql.length()-1);
		if(inSql.length()>0)
			sql.append(inSql);
		else
			sql.append("''");	
		sql.append(")");
		ContentDAO dao = new ContentDAO(this.conn);
		int emailType = 2;
		if(type == 1){//已批
			emailType = 2;//2:协作申请已批通知模板
		} else if(type == 2) {// 驳回
			emailType = 3;//3:任务申请退回通知模板
		}
		HashMap map = wpu.getEmailTemplateInfo(emailType);
		String bodyTextTemplate = (String)map.get("bodyText");//邮件模板
		String title = (String)map.get("title");
		String hrefDesc = (String)map.get("hrefDesc");
		try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				// 操作日志
				new WorkPlanOperationLogBo(conn, this.userview).addLog(Integer.parseInt(rs.getString("p0800")), type==1?"批准了协作任务申请":"退回了协作任务申请");
				// 更新待办
				update_cooperationTask("1",rs.getString("p0800"));
				
				// 发送给任务发起人。ps：先不发待办只发邮件，发待办的话不确定什么时候清除待办
				String text = "";
				text = bodyTextTemplate.replace("{mark1}", this.getUserFullName(rs.getString("GUIDKE_CREATER"), this.userview.getDbname()));
				text = text.replace("{mark2}", this.userview.getUserFullName());
				text = text.replace("{mark3}", rs.getString("P1009"));
				wpu.sendEmaiAndWeiXin(title, text, hrefDesc, wpb.getRemindEmail_MyCooperationTaskHref(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER"), this.userview.getDbname())), (this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER"), this.userview.getDbname())));
				String receiver = wpu.getUserNameByA0100(this.userview.getDbname(), this.getA0100(rs.getString("GUIDKE_CREATER"), this.userview.getDbname()));
				this.sendPending_ToMyCooperationTask(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()), receiver, title, rs.getString("p0800"));
				
				// 发送给发起人直接领导。ps：先不发待办只发邮件，发待办的话不确定什么时候清除待办
				String text1 = "";
				text1 = bodyTextTemplate.replace("{mark1}", this.getUserFullName(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()));
				text1 = text1.replace("{mark2}", this.userview.getUserFullName());
				text1 = text1.replace("{mark3}", rs.getString("P1009"));
				ArrayList list = new ArrayList();
				String href = wpb.getRemindEmail_PlanHref(this.userview.getDbname(),this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()),true);
				LazyDynaBean emailBean = wpb.getEmailBean(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()),"", text1, href,"去查看计划");                   
				//emailBean.set("bodySubject", "工作计划审批提醒");
				list.add(emailBean);
				wpu.sendWeixinMessageFromEmail(list);
				//this.sendPending_ToMyCooperationTask(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()), this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()), title, rs.getString("p0800"));
				//wpu.sendEmaiAndWeiXin(title, text1, hrefDesc, wpb.getRemindEmail_MyCooperationTaskHref(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname())), (this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname())));
				//驳回时发送待办给协办人直接上级，链接到工作计划页面 chent  
//				String pending_title = "协作任务退回提醒";          
//                String pending_url=wpb.getPendingPlanUrl();
//                LazyDynaBean pendingBean = new  LazyDynaBean();
//                pendingBean.set("pending_url", pending_url);
//                pendingBean.set("pending_title", pending_title);
//                String receiver1 =wpu.getUserNameByA0100(this.userview.getDbname(),this.getA0100(rs.getString("GUIDKE_CREATER_SP"), this.userview.getDbname()));
//                wpu.sendPending_BackPlan(this.userview.getUserName(), receiver1, pendingBean, rs.getString("p0800"));
				
				// 发送给协办人。ps：先不发待办只发邮件，发待办的话不确定什么时候清除待办
				if(type==1){
					HashMap templateInfoTopriPerson = wpu.getEmailTemplateInfo(4);//发给协办人的邮件模板
					String bodyText = (String)templateInfoTopriPerson.get("bodyText");
					String title2 = (String)templateInfoTopriPerson.get("title");
					String hrefDesc2 = (String)templateInfoTopriPerson.get("hrefDesc");
					String text2 = "";
					text2 = bodyText.replace("{mark1}", this.getUserFullName(rs.getString("GUIDKE_OWNER"), this.userview.getDbname()));
					text2 = text2.replace("{mark2}", this.userview.getUserFullName());
					text2 = text2.replace("{mark3}", rs.getString("P1009"));
					//this.sendPending_ToMyCooperationTask(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_OWNER"), this.userview.getDbname()), this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_OWNER"), this.userview.getDbname()), title, rs.getString("p0800"));
					wpu.sendEmaiAndWeiXin(title2, text2, hrefDesc2, wpb.getRemindEmail_MyCooperationTaskHref(this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_OWNER"), this.userview.getDbname())), (this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_OWNER"), this.userview.getDbname())));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rs);
		}
	}

	/**
	 * 	发送消息让接收者进入协办任务审批页面
	 *  @param  type 类型
	 *  @param  
	 * */
	public void sendPending_cooperationTask(ArrayList<String> al) {
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		String inSql = "SELECT p1001 FROM P10 WHERE P1001 IN(";
		sql.append("SELECT P0800,GUIDKE_CREATER,GUIDKE_CREATER_SP,GUIDKE_OWNER,GUIDKE_OWNER_SP FROM P10 WHERE p1001 in (");
		for(String p1001 : al){
			inSql+="'"+PubFunc.decrypt(p1001)+"',";
		}
		inSql = inSql.substring(0,inSql.length()-1);
		if(inSql.length()>0)
			sql.append(inSql);
		else
			sql.append("''");	
		sql.append("))");
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			rs =dao.search(sql.toString());
			while(rs.next()){
				wpu.sendPending_ToCooperationTask(this.userview.getDbname()+this.userview.getA0100(), this.userview.getDbname()+this.getA0100(rs.getString("GUIDKE_OWNER_SP"), this.userview.getDbname()), rs.getString("p0800"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 	发送消息让接收者进入我的协办任务
	 *  @param  type 类型
	 *  @param  
	 * */
	public void sendPending_ToMyCooperationTask(String sender,String receiver, String title,String p0800){
		wpu.sendPending_ToMyCooperationTask(sender,receiver,title,p0800);
	}
	/**
	 * 	更新待办任务状态
	 *  @param  type 类型
	 *  @param  
	 * */
	public void update_cooperationTask(String flag, String p0800){
		wpu.update_cooperationTask(flag,p0800);
	}
	/**
	 * 获取人员信息
	 */
	public ArrayList getDetailByDbA0100ForP09(String nbase,String A0100){
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(this.conn);
		String sql = "SELECT *FROM "+nbase+"a01 WHERE  A0100 =? ";
		ArrayList al = new ArrayList();
		try {
			rs = dao.search(sql,Arrays.asList(new Object[] {A0100}));
			if(rs.next()){
				al.add(rs.getString("A0100"));
				al.add(nbase);
				al.add(rs.getString("B0110"));
				al.add(rs.getString("E01A1"));
				al.add(rs.getString("E0122"));
				al.add(rs.getString("A0101"));
//				map.put("A0100", rs.getString("A0100"));
//				map.put("nbase", rs.getString("nbase"));
//				map.put("P0907", rs.getString("B0110"));
//				map.put("P0909", rs.getString("E01A1"));
//				map.put("P0911", rs.getString("E0122"));
//				map.put("P0913", rs.getString("A0101"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	} 
}
