package com.hjsj.hrms.businessobject.gz.gz_budget;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.sun.rowset.CachedRowSetImpl;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * @author wangjh
 *
 */
public class BudgetServiceBo {
	
	private Connection conn = null;
	public BudgetSysBo defBo = null;
	private ContentDAO dao = null;
	private int budgetIdx;	//预算索引号
	private boolean emptyBudget = false;	// 预算数据为空标志
	
	private String ysze_Set;
	private String ysze_fld_Idx;
	private String ysze_fld_Status;
//	状态：04 处理为预算分配的分发，才可以进行预算编制
//	private String spValue = "03"; 			// 03: 已批  05: 执行中 （当做预算发布）    
		
	/**
	 * @param _con			: 数据连接
	 * @param _budgetIdx	: 当前预算索引号
	 */
	public BudgetServiceBo(Connection _con, int _budgetIdx){
		this.conn = _con;
		this.budgetIdx = _budgetIdx;
		
		defBo = new BudgetSysBo(_con, null);
		dao = new ContentDAO(this.conn);
		
		HashMap hm = defBo.getSysValueMap();
		ysze_Set = (String) hm.get("ysze_set");
		ysze_fld_Idx = (String) hm.get("ysze_idx_menu");
		ysze_fld_Status = (String) hm.get("ysze_status_menu");
		
		loadBudgetInfo();
		
	}
	
	// 定义当前预算参数
	int bgYear;   	// 预算年度
	int bgType;		// 预算类别：1 年初预算	2 年中预算	3 特别调整	4 实支	5 外部实发
	String spFlag;	// 审批标志：代码类23：1 起草 5 发布 6 暂停
	java.util.Date bgDate;	// 预算日期
	String bgUnits;	// 预算单位列表，以逗号分隔，单位编码
	
	public int getBgYear() {
		return bgYear;
	}

	public int getBgType() {
		return bgType;
	}

	public java.util.Date getBgDate() {
		return bgDate;
	}

	public String getBgUnits() {
		return bgUnits;
	}
	
	public String getUnitName(String unCode){
		return defBo.getUnitsName(unCode);
	}

	/**
	 * 读取预算信息
	 */
	private void loadBudgetInfo(){
		try{
			emptyBudget = true;
			RowSet rs = dao.search("select budget_id, YearNum, FirstMonth, budgetType, AdjustDate, SPFlag, extAttr from gz_budget_index where budget_id=" + budgetIdx);
			if(rs.next()){
				bgYear = rs.getInt("YearNum");
				bgType = rs.getInt("budgetType");
				spFlag = rs.getString("SPFlag");
				bgDate = rs.getDate("AdjustDate");
				if(bgDate == null){
					bgDate = new Date();
				}
				bgUnits = rs.getString("extAttr");	
				emptyBudget = false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @return 是否可以向外发送预算数据
	 * 需要检查预算单位的状态是不是审批
	 */
	private boolean canSendBudget(){
		String[] unLst = bgUnits.split(",");
		if (unLst.length==0){
			emptyBudget = true;
			return false;
		}
//		单位的SQL条件子句，用于in，不含括号
		String condUN = "";
		for(int i=0; i<unLst.length; i++){
			if(i!=0){
				condUN = "," + condUN;
			}
			condUN = "'" + unLst[i] +"'" + condUN;
		}
		
		RowSet rs = null;
		try{
//			读取总额记录语句： 总额表， 预算索引条件， 单位代码条件
			String sqlZE = "select * from " + ysze_Set + " where " + ysze_fld_Idx + "=? and B0110 in ("+ condUN +")";
			ArrayList list = new ArrayList();
			list.add(new Integer(budgetIdx));
//			list.add(new String(condUN));
			emptyBudget = true;
			rs = dao.search(sqlZE, list);
			while(rs.next()){
				emptyBudget = false;
				String _status = rs.getString(ysze_fld_Status);
				if ( !("03".equals(_status)|| "05".equals(_status)) ){
					return false;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * @return 返回预算结构的数据集
	 * units 本次所数据的单位
	 * grpFld 计提，支出等分组的代码的SQL（后面逐条转换比较慢）
	 */
	public RowSet openBudgetData(String units, String grpFld){
		DbSecurityImpl dbS = new DbSecurityImpl();
		RowSet rs = null;
//		单位的SQL条件子句，用于in，不含括号
		String[] unLst = units.split(",");
		if (unLst.length==0){
			emptyBudget = true;
			return rs;
		}
		String condUN = "";
		for(int i=0; i<unLst.length; i++){
			if(i!=0){
				condUN = "," + condUN;
			}
			condUN = "'" + unLst[i] +"'";
		}
		String sqlData="";
		// WJH 2013-6-9, 特别预算，ＳＡＰ要调整额。　不是源数据。
		if(bgType==3){
			// 取上一次预算数据，取其差额
			int frontIdx=-1;
			String sqlTemp = "select budget_id from gz_budget_index "
				           + "where budget_id<" + budgetIdx + " and yearNum=" + bgYear
				           + " order by budget_id desc";
			try {
				RowSet rst = dao.search(sqlTemp);
				while(rst.next()){
					frontIdx = rst.getInt("budget_id");
				}
				rst.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (frontIdx==-1) {
				return rs;
			}
			// 处理差额的SQL语句
			StringBuffer colsBuffer = new StringBuffer();
			for (int i=1; i<=12; i++) {
				//,(BC.Val_1-SC.Val_1) AS Val_1
				colsBuffer.append(",(");
				colsBuffer.append( Sql_switcher.sqlNull("BC.Val_"+i, 0) );
				colsBuffer.append("-");
				colsBuffer.append( Sql_switcher.sqlNull("SC.Val_"+i, 0) );
				colsBuffer.append(") AS Val_");
				colsBuffer.append(i);				
			}
						
			sqlData = "select BC.B0110, BC.tab_id, BC.Itemid "+colsBuffer.toString()
		   		+ ",BC.tabCode, BC.budgetGroup, BC.CodesetId, BC.ItemCode, BC.OrgCode,BC.bgGrpCode "  
				// 本次的
				+ "from ("
				+ "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
			    + ", T.tabCode, T.budgetGroup, T.CodesetId "
			    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
			    + " from SC03 A, gz_budget_tab T, codeitem C, organization O "
			    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
			    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
			    + "  and A.B0110=O.codeitemid "
			    + "  and A.budget_id=" + budgetIdx + " and A.B0110 in (" + condUN + ") "
			    // 2013-5-15, WJH 扩展S02
			    + " union "
			    + "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
			    + ", T.tabCode, T.budgetGroup, T.CodesetId "
			    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
			    + " from SC02 A, gz_budget_tab T, codeitem C, organization O "
			    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
			    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
			    + "  and A.B0110=O.codeitemid "
			    + "  and A.budget_id=" + budgetIdx + " and A.B0110 in (" + condUN + ") "
			    + ") BC LEFT JOIN ("
			    //------ 上次的
				+ "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
			    + ", T.tabCode, T.budgetGroup, T.CodesetId "
			    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
			    + " from SC03 A, gz_budget_tab T, codeitem C, organization O "
			    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
			    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
			    + "  and A.B0110=O.codeitemid "
			    + "  and A.budget_id=" + frontIdx + " and A.B0110 in (" + condUN + ") "
			    // 2013-5-15, WJH 扩展S02
			    + " union "
			    + "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
			    + ", T.tabCode, T.budgetGroup, T.CodesetId "
			    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
			    + " from SC02 A, gz_budget_tab T, codeitem C, organization O "
			    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
			    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
			    + "  and A.B0110=O.codeitemid "
			    + "  and A.budget_id=" + frontIdx + " and A.B0110 in (" + condUN + ") "
			    + ") SC ON (BC.B0110=SC.B0110 and BC.tab_id=SC.tab_id and BC.itemid=SC.itemid)"
			    //------
			    + "order by BC.tab_id, BC.Itemid";			
			
		} else {
	//		取数据，项目没有转换代码的
			sqlData = "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
				    + ", T.tabCode, T.budgetGroup, T.CodesetId "
				    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
				    + " from SC03 A, gz_budget_tab T, codeitem C, organization O "
				    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
				    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
				    + "  and A.B0110=O.codeitemid "
				    + "  and A.budget_id=" + budgetIdx + " and A.B0110 in (" + condUN + ") "
				    // 2013-5-15, WJH 扩展S02
				    + " union "
				    + "select A.B0110, A.tab_id, A.Itemid, A.Val_1, A.Val_2, A.Val_3, A.Val_4, A.Val_5, A.Val_6, A.Val_7, A.Val_8, A.Val_9, A.Val_10, A.Val_11, A.Val_12 "
				    + ", T.tabCode, T.budgetGroup, T.CodesetId "
				    + ", C.corCode As ItemCode, O.corCode As OrgCode " + grpFld
				    + " from SC02 A, gz_budget_tab T, codeitem C, organization O "
				    + "where A.tab_id=T.tab_id and T.bpFlag=1 "
				    + "  and T.codesetid=C.codesetid and A.itemid=C.codeitemid and not nullif(C.corCode, '') is null"
				    + "  and A.B0110=O.codeitemid "
				    + "  and A.budget_id=" + budgetIdx + " and A.B0110 in (" + condUN + ") "
				    //------
				    + "order by A.tab_id, A.Itemid";
		}
		Statement stmn = null;
		ResultSet rSet = null;
		try{
			stmn = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			// 打开Wallet
			dbS.open(conn, sqlData);
			rSet = stmn.executeQuery(sqlData);
			
			rs = new CachedRowSetImpl(); 
	        ((CachedRowSetImpl) rs).populate(rSet); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeResource(rSet);
			PubFunc.closeResource(stmn);
			try {
				// 关闭Wallet
				dbS.close(conn);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return rs;
	}
	
	/** 返回指定预算ID的预算表名
	 * @param tab_id
	 * @return
	 */
	public String getBudgetTableName(int tab_id){
		try{
		ResultSet rSet = dao.search("select tab_name from gz_budget_tab where tab_id=" + tab_id);
		return rSet.getString("tab_name");
		}catch(Exception e){
			return "";
		}
	}
	
	/**
	 * 同步预算数据接口，xml为返回值，广东中烟不用xml
	 * @param xml
	 */
	public String SendMessage(){
		String xml="";
		try{
			if(emptyBudget){
				xml = "没有薪资预算数据（index="+budgetIdx+"）！";
				return xml;
//				throw GeneralExceptionHandler.Handle(new Exception("没有薪资预算数据（index="+budgetIdx+"）！"));
			}
			
			if(emptyBudget || !canSendBudget()){
				xml = "所有预算单位必须全部批准后，才可以同步！";
				return xml;
//				throw GeneralExceptionHandler.Handle(new Exception("所有预算单位必须全部批准后，才可以同步！"));
			}
			
			String clientName = SystemConfig.getPropertyValue("clientName");
			if(clientName==null||clientName.trim().length()==0) {
				xml = "系统没有设置clientName，请在system.properties文件中设置！";
				return xml;
//				throw GeneralExceptionHandler.Handle(new Exception("系统没有设置clientName，请在system.properties文件中设置！"));
			}
			
			ResourceBundle myRes = ResourceBundle.getBundle(clientName);
			
			String sendClassName = myRes.getString("budget_srvclassname");
			if(sendClassName==null||sendClassName.trim().length()==0){
				xml = "系统没有配置预算同步类budget_srvclassname！";
				return xml;
//				throw GeneralExceptionHandler.Handle(new Exception("系统没有配置预算同步类budget_srvclassname！"));
			}
			
			// 创建配置类，并调用数据发送方法
			try{
				Class c = Class.forName(sendClassName);
				I_BudgetService sendInf = (I_BudgetService)c.newInstance();
				xml = sendInf.SendData(this, myRes);
			}catch(ClassNotFoundException e){
//				GeneralExceptionHandler.Handle(new Exception("类名未找到:"+sendClassName));
				xml = "类名未找到:"+sendClassName;
			}
		}catch(Exception e){
			xml="系统参数配置错误\r\n请检查clientName，budget_srvclassname等参数是否配置正确！";
			//e.printStackTrace();
		}
		return xml;
	}

	/** 预算类别（预算版本）的文本描述
	 * @param typeId
	 * @return
	 */
	public static String budgetTypeDesc(int typeId){
		// 预算的类别文本： 1 			2 年中预算			3 特别调整			4 实支			5 外部实发
		switch (typeId) {
		case 1: return "年初预算"; 
		case 2: return "年中预算"; 
		case 3: return "特别调整"; 
		case 4: return "实支"; 
		case 5: return "外部实发"; 
		default:
			return "";
		} 
	}
	
}
