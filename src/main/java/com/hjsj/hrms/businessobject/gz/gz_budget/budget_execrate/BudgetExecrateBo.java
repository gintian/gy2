package com.hjsj.hrms.businessobject.gz.gz_budget.budget_execrate;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;


public class BudgetExecrateBo {
	/* 没有预算时的索引号 */
	public static int V_InvalidIdx = -1;
	/* 实发的预算类型 */
	public static int V_ActualType = 4;
	
	private UserView userView;
	private Connection con;
	
	/* 预算年度列表：对象元素 BudgetYearItem 类 */
	private ArrayList list=null;
	
	private String lastErrorInfo;
	
	public String getLastErrroString() {
		return lastErrorInfo;
	}
	
	public BudgetExecrateBo(Connection con, UserView userView) {
		this.userView = userView;
		this.con = con;
	}
	
	/* load 预算索引数据 */
	private void loadBudgetData(){
		if (list==null)
			list = new ArrayList();
		else
			list.clear();
		
		ContentDAO dao = new ContentDAO(con);
		try {
			// 取出年份，预算索引，实发索引
			StringBuffer buffer = new StringBuffer("");
			buffer.append("select bg.budget_id, bg.yearNum, bg.budgetType, ");
			buffer.append("case when sf.budget_id IS null then -1 else sf.budget_id end as sfId ");
			buffer.append("	from gz_budget_index bg ");
			buffer.append("	inner join (select yearNum, Max(budget_id) as budget_id from gz_budget_index ");
			buffer.append("where budgetType in (1,2,3) and SPFlag in ('04', '09') group by yearNum ) fb ");
			buffer.append(" on bg.yearNum=fb.yearNum and bg.budget_id=fb.budget_id ");
			buffer.append("	left join (select yearNum, Max(budget_id) as budget_id from gz_budget_index ");
			buffer.append("where budgetType="+Integer.toString(V_ActualType));
			buffer.append("   group by yearNum ) sf on bg.yearNum=fb.yearNum ");
			buffer.append("	order by bg.yearNum desc ");    

			BudgetYearItem item;
			RowSet rs = dao.search(buffer.toString());			
			try {
				while (rs.next()) {
					item = new BudgetYearItem();
					item.setYear(rs.getInt("yearNum"));
					item.setBudgetIdx(rs.getInt("budget_id"));
					item.setActualIdx(rs.getInt("sfId"));
					
					list.add(item);
				}
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 2013, 2013年 的格式
	 * @return 返回 预算年度列表
	 */
	public ArrayList getPublishBudgetYearList() {
		if (list==null){
			loadBudgetData();
		}
		
		CommonData vo;  BudgetYearItem item;
		ArrayList yearList= new ArrayList();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()){			
			item = (BudgetYearItem) iterator.next();
			vo = new CommonData( item.getYearText(), item.getName() );
			yearList.add(vo);
		}
		return yearList;
	}
	
	/**
	 * 返回指定年度的预算对象
	 * @param Year
	 * @return
	 */
	public BudgetYearItem getItem(int year){
		if (list==null){
			loadBudgetData();
		}
		
		Iterator iterator = list.iterator();
		BudgetYearItem item;
		while(iterator.hasNext()){			
			item = (BudgetYearItem) iterator.next();
			if (item.getYear()==year) {
				return item;
			}
		}
		return null;	
	}
	
	/* 返回指定年份的预算索引号  */
	public int getBudgetIdx(int year) {
		BudgetYearItem item = getItem(year);
		if(item==null){
			return V_InvalidIdx;
		}else{
			return item.getBudgetIdx();
		}
	}

	/* 返回指定年份的实发索引号  */
	public int getActualIdx(int year) {
		BudgetYearItem item = getItem(year);
		if(item==null){
			return V_InvalidIdx;
		}else{
			return item.getActualIdx();
		}
	}

	public int getActualIdx(int year, boolean autoreate) throws Exception {
		if (autoreate) {
			generateActualVo(year);
		}
		return getActualIdx(year);
	}
	
	/* 返回指定年份的是否有实发索引  */
	public boolean haveActualIdx(int year) {
		BudgetYearItem item = getItem(year);
		if(item==null){
			return false;
		}else{
			return item.haveActualVo();
		}
	}
	
	// 检查指定年份的预算表有没有执行率分析表
	private boolean haveExecrateTable(ContentDAO dao, int year) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("select tab_id from gz_budget_tab ");
		buffer.append("where analyseflag=1 and tab_id in (select tab_id from gz_budget_exec where budget_id=");
		buffer.append(getBudgetIdx(year));
		buffer.append(")");
		
		try {
			RowSet rSet = dao.search(buffer.toString());
			
			if (rSet.next()) {
				return true;
			}
			rSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 *  生成指定年度的实发记录， 空数据 
	 *  
	 */
	public boolean generateActualVo(int year) throws Exception{
		lastErrorInfo = "";
		// 已有，不创建
		BudgetYearItem item = getItem(year);
		if (item==null || item.haveActualVo()) {
			return false;
		}
				
		ContentDAO dao = new ContentDAO(con);
		StringBuffer buffer = new StringBuffer();
		try {
			if ( !haveExecrateTable(dao, year) ) {
				lastErrorInfo = year + ResourceFactory.getProperty("gz.budget.execrate.notdeftable");  //"年度没有定义用于执行率分析的预算表";
				throw new Exception(lastErrorInfo);
			}
			
			String acIdx;
			try {
				acIdx = new IDFactoryBean().getId("gz_budget_index.budget_id", "", con);
			} catch (Exception e) {
				lastErrorInfo = ResourceFactory.getProperty("gz.budget.execrate.createindexfail");  //"生成实发索引失败！";
				throw new Exception(lastErrorInfo);
			}
			con.setAutoCommit(false);
			try {
				/* 生成实发索引记录 */
				buffer.append("insert into gz_budget_index(budget_id,yearNum,firstMonth,budgetType,I9999,adjustDate,SPFlag,curUser,extAttr) ");
				buffer.append("select ");
				buffer.append(acIdx);
				buffer.append(",yearNum,1,");
				buffer.append(V_ActualType);
				buffer.append(",I9999+1,"+Sql_switcher.sqlNow()+", '04','");				
				buffer.append(userView.getUserId());
				buffer.append("',extAttr from gz_budget_index where budget_id=");
				buffer.append(getBudgetIdx(year));
				dao.update(buffer.toString());
				
				// 初始所有表
				refreshActualRecs(dao, year, acIdx, " and tab_id in (select tab_id from gz_budget_tab where analyseflag=1)");
				
				con.commit();
				// 更新类
				item.setActualIdx(Integer.parseInt((acIdx)));
				return true;
			} catch (Exception e) {
				con.rollback();
				throw e;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	/** 计算实发数据
	 * @param b0110： 单位编号
	 * @param tabid：要计算的预算表，=0时计算所有表
	 * @return
	 */
	public boolean calcActualData(String b0110, int year, int month, int tabid) {
		int sfId = getActualIdx(year);
		
		/* 生成存储过程： P_GZBUDGET_CALC_ACTUAL
		 * 参数	@b0110	varchar(30)	预算单位
		 *      @budgetid	int	本次预算索引
		 *      @tabid	int	预算表号
		 *      @year	int	统计年度
		 *      @month	int	统计月份
		 *     执行存储过程(过程名: 参数1, 参数2, 参数3, ...)
		 */ 
		StringBuffer fmlBuffer = new StringBuffer("执行存储过程(P_GZBUDGET_CALC_ACTUAL:");
		// 单位
		fmlBuffer.append("\"");
		fmlBuffer.append(b0110);
		fmlBuffer.append("\",");
		// 预算索引
		fmlBuffer.append(sfId);
		fmlBuffer.append(",");
		// 表号
		fmlBuffer.append(tabid);
		fmlBuffer.append(",");
		// 年度
		fmlBuffer.append(year);
		fmlBuffer.append(",");
		// 月份
		fmlBuffer.append(month);
		fmlBuffer.append(")");
		
		ArrayList alNull = new ArrayList();
        YksjParser yp = new YksjParser(this.userView , alNull,
                YksjParser.forNormal, YksjParser.STRVALUE, YksjParser.forPerson, "", "");
        try {
        	yp.setCon(this.con);
	        yp.run(fmlBuffer.toString());

	        // 刷新临时表
			// 删除临时表该月及后面的数据，用于重新统计后面月份累计
			String tablename = getRateTablename();
			ContentDAO dao = new ContentDAO(con);	
			String updSql = "delete from " + tablename + " where b0110='" + b0110 + "' and tab_id=" + tabid + " and monthnum>="+month;
			dao.update(updSql);			
			refreshRateData(dao, tablename, b0110, year, month, tabid);
	        
	        return true;
        } catch (GeneralException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (SQLException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		return false;
	}
	
	/**
	 * 删除并重新创建实发记录
	 * @param dao
	 * @param year
	 * @param acIdx： 实发预算索引
	 * @param tabWhere： （指定条件的预算表， 含 and）
	 * @throws Exception
	 */
	private void refreshActualRecs(ContentDAO dao, int year, String acIdx, String tabWhere) throws Exception {
		StringBuffer buffer = new StringBuffer();
		try {
			int ysIdx = getBudgetIdx(year);
			
			// 不删除
//			buffer.append("delete from gz_budget_exec where budget_id=");
//			buffer.append(acIdx);
//			buffer.append(tabWhere);
//			dao.update(buffer.toString());
//			
//			buffer.append("delete from SC02 where budget_id=");
//			buffer.append(acIdx);
//			buffer.append(tabWhere);
//			dao.update(buffer.toString());
//			
//			buffer.append("delete from SC03 where budget_id=");
//			buffer.append(acIdx);
//			buffer.append(tabWhere);
//			dao.update(buffer.toString());
			
			// 存在则退出
			buffer.append("select 1 from gz_budget_exec where budget_id=");
			buffer.append(acIdx);
			buffer.append(tabWhere);
			RowSet rs = dao.search(buffer.toString());
			if (rs.next()) {
				rs.close();
				return;
			}
			rs.close();			
			
			// 处理其他表
			buffer.setLength(0);
			buffer.append("insert into gz_budget_exec(budget_id,tab_id,B0110,status,Seq) ");
			buffer.append("select ");
			buffer.append(acIdx);
			buffer.append(",tab_id,B0110,0,Seq from gz_budget_exec where budget_id=");
			buffer.append(ysIdx);
			// 增加一个条件，只有执行率分析的表才参与执行分析
			buffer.append(tabWhere);
			
			dao.update(buffer.toString());
			
			// SC02 budget_id
			buffer.setLength(0);
			buffer.append("insert into SC02(budget_id,tab_id,B0110,Itemid,itemdesc,Seq) ");
			buffer.append("select ");
			buffer.append(acIdx);
			buffer.append(",tab_id,B0110,Itemid,itemdesc,Seq from SC02 where budget_id=");
			buffer.append(ysIdx);
			// 增加一个条件，只有执行率分析的表才参与执行分析
			buffer.append(tabWhere);
			
			dao.update(buffer.toString());
			
			// SC03
			buffer.setLength(0);
			buffer.append("insert into SC03(budget_id,tab_id,B0110,Itemid,itemdesc,Seq) ");
			buffer.append("select ");
			buffer.append(acIdx);
			buffer.append(",tab_id,B0110,Itemid,itemdesc,Seq from SC03 where budget_id=");
			buffer.append(ysIdx);
			// 增加一个条件，只有执行率分析的表才参与执行分析
			buffer.append(tabWhere);
			
			dao.update(buffer.toString());
			
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	private void refreshActualRecs(int year, int tabId) throws Exception {
		String w = " and tab_id=" + tabId;
		ContentDAO dao = new ContentDAO(con);
		refreshActualRecs(dao, year, Integer.toString(getActualIdx(year)), w);
	}

	/**
	 * 返回执行率分析的字段列表
	 * @param b0110
	 * @param year
	 * @param tab_id
	 * @return
	 */
	public ArrayList getExecrateFieldList(String b0110, int year, int tab_id){
		ArrayList aList = new ArrayList();
		
		aList.add(getFieldObj("b0110", "b0110", "A", 30, 0, true, false));
		lastNewField.setIndexable(true);
		lastNewField.setKeyable(true);
		lastNewField.setNullable(false);
		aList.add(getFieldObj("yearnum", "年份", "N", 8, 0, true, false));
		lastNewField.setKeyable(true);
		lastNewField.setNullable(false);
		lastNewField.setIndexable(true);
		aList.add(getFieldObj("monthnum", "月份", "N", 8, 0, true, false));
		lastNewField.setKeyable(true);
		lastNewField.setNullable(false);
		lastNewField.setIndexable(true);
		aList.add(getFieldObj("tab_id", "tab_id", "N", 8, 0, true, false));
		lastNewField.setKeyable(true);
		lastNewField.setNullable(false);
		lastNewField.setIndexable(true);
		aList.add(getFieldObj("itemid", "itemid", "A", 30, 0, true, false));
		lastNewField.setKeyable(true);
		lastNewField.setNullable(false);
		aList.add(getFieldObj("seq", "seq", "N", 8, 0, true, false));
		aList.add(getFieldObj("itemdesc", ResourceFactory.getProperty("gz.budget.budgeting.ze.name"), "A", 100, 0, true, true));  // 项目
		aList.add(getFieldObj("nFB", ResourceFactory.getProperty("gz.budget.execrate.yearpublish"), "N", 8, 2, true, true));  	//"年度发布数"
		aList.add(getFieldObj("yFB", ResourceFactory.getProperty("gz.budget.execrate.monthpublish"), "N", 8, 2, true, true));  	//"本期发布数"
		aList.add(getFieldObj("ySF", ResourceFactory.getProperty("gz.budget.execrate.monthactual"), "N", 8, 2, false, true)); 	//"本期实际数"
		aList.add(getFieldObj("ljFB", ResourceFactory.getProperty("gz.budget.execrate.yearpublish_lj"), "N", 8, 2, true, true)); 	//"本期累计发布数"
		aList.add(getFieldObj("ljSF", ResourceFactory.getProperty("gz.budget.execrate.monthpubli_lj"), "N", 8, 2, true, true));		//"本期累计实际数"
		
		aList.add(getFieldObj("yCE", ResourceFactory.getProperty("gz.budget.execrate.month_ce"), "N", 8, 2, true, true));		//"本期预算差额"
		aList.add(getFieldObj("ljCE", ResourceFactory.getProperty("gz.budget.execrate.month_ljce"), "N", 8, 2, true, true));		//"本期累计差额"
		
		aList.add(getFieldObj("yRate", ResourceFactory.getProperty("gz.budget.execrate.monthrate"), "N", 8, 2, true, true, true));		//"月度执行率"	
		aList.add(getFieldObj("ljRate", ResourceFactory.getProperty("gz.budget.execrate.ljrate"), "N", 8, 2, true, true, true));		//"累计执行率"
		aList.add(getFieldObj("nRate", ResourceFactory.getProperty("gz.budget.execrate.yearrate"), "N", 8, 2, true, true, true));		//"年度执行率"	
		
		return aList;		
	}
	
	// 创建field
	private Field lastNewField;
	
	private Field getFieldObj(String itemid, String itemdesc, String dataType, int len, int decLen, boolean readonly, boolean visible){
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		
		Field field = new Field(itemid, "&nbsp;&nbsp;&nbsp;"+itemdesc+"&nbsp;&nbsp;&nbsp;");
		if ("N".equalsIgnoreCase(dataType)){
			field.setLength(len);
			field.setDecimalDigits(decLen);
			
			if (decLen == 0){
				field.setDatatype(DataType.INT);
				field.setFormat("####");
			}else{
				field.setDatatype(DataType.FLOAT);
				field.setFormat("####."+format.toString().substring(0, decLen));
			}
			field.setAlign("right");		
		} else if ("D".equalsIgnoreCase(dataType)){
			field.setLength(20);
			field.setDatatype(DataType.DATE);
			field.setFormat("yyyy.MM.dd");
			field.setAlign("right");	
		} else if ("M".equalsIgnoreCase(dataType)){
			field.setDatatype(DataType.CLOB);
			field.setAlign("left");		
		} else {
			field.setDatatype(DataType.STRING);
			field.setLength(len);
			field.setAlign("left");			
		}
		field.setReadonly(readonly);
		field.setVisible(visible);
		
		lastNewField = field;
		return field; 
	}
	
	private Field getFieldObj(String itemid, String itemdesc, String dataType, int len, int decLen, boolean readonly, boolean visible, boolean rate){
		Field field = getFieldObj(itemid, itemdesc, dataType, len, decLen, readonly, visible);
		if (rate) 
			field.setFormat("####.##%");				
		return field; 
	}
	
	// 删除临时数据表  
	// 避免脏数据，每次进入预算执行率都调用
	public void dropTempTable() {
		String tableName = getRateTablename();
		
		DbWizard dbWizard = new DbWizard(this.con);
		try {
			if (dbWizard.isExistTable(tableName, false)) {
				dbWizard.dropTable(tableName);
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 创建数据表
	private boolean createTempTable(String tableName) {
		DbWizard dbWizard = new DbWizard(this.con);
		try {
			Table tmpTb = new Table(tableName);
			
			if (dbWizard.isExistTable(tableName,false)) {
				return true;
			}
			
			ArrayList aList = getExecrateFieldList("",0, 0);
			Iterator iterator = aList.iterator();
			while (iterator.hasNext()){
				tmpTb.addField( (Field) iterator.next() );
			}
			dbWizard.createTable(tmpTb);	

			// 索引
			dbWizard.addIndex(tmpTb);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private String getWhereCaluse(String b0110, int year, int month, int tab_id) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("b0110='");
		buffer.append(b0110);
		buffer.append("' and yearnum=");
		buffer.append(year);
		buffer.append(" and monthnum=");
		buffer.append(month);
		buffer.append(" and tab_id=");
		buffer.append(tab_id);
		
		return buffer.toString();		
	}
	
	// 取月字段
	private String getSQL_val(int month, boolean ljFlag) {
		StringBuffer buffer = new StringBuffer();
		if (ljFlag) {
			for (int i = 1; i <= month; i++) {
				if (i!=1)
					buffer.append("+");
				buffer.append(Sql_switcher.sqlNull("val_"+i, 0));
			}
		} else {
			buffer.append("val_");
			buffer.append(month);
		}
		return buffer.toString();
	}
	
	// 生成执行率临时表数据
	private boolean genExecrateData(String tablename, String b0110, int year, int month, int tab_id) {
		if (createTempTable(tablename)){
			ContentDAO dao = new ContentDAO(con);
			
			try{
				// 判断有没有数据
				StringBuffer buffer = new StringBuffer("select count(*) as cou from ");
				buffer.append(tablename);
				buffer.append(" where ");
				buffer.append(getWhereCaluse(b0110, year, month, tab_id));
				
				int counter = 0;
				RowSet rSet = dao.search(buffer.toString());
				if (rSet.next()) {
					counter = rSet.getInt("cou");
				}
				
				// 创建记录
				if (counter == 0) {
					buffer.setLength(0);
					buffer.append("insert into ");
					buffer.append(tablename);
					buffer.append("(b0110, yearnum, monthnum, tab_id, itemid, itemdesc, nFB, yFB, ljFB, seq) ");
					buffer.append("select '");
					buffer.append(b0110+"',");
					buffer.append(year+",");
					buffer.append(month+",");
					buffer.append(tab_id+",");
					buffer.append("itemid, itemdesc, thisYearSum,");
					buffer.append(getSQL_val(month, false));
					buffer.append(",");
					buffer.append(getSQL_val(month, true));
					buffer.append(" as ljFB, seq ");
					buffer.append(" from SC03 ");
					buffer.append(" where b0110='");
					buffer.append(b0110);
					buffer.append("' and budget_id=");
					buffer.append(getBudgetIdx(year));
					buffer.append(" and tab_id=");
					buffer.append(tab_id);
					
					dao.update(buffer.toString());
					
					// 实发 和 累计实发  更新率
					refreshRateData(dao, tablename, b0110, year, month, tab_id);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;		
	}
	
	private void refreshRateData(ContentDAO dao, String tablename, String b0110, int year, int month, int tab_id) {
		// 重取实发 和 累计实发
		String updSql;
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL:
			updSql = "update @tb SET ySF=A.ySF, ljSF=A.ljSF " 
				   + " from (select itemid, " + getSQL_val(month, false) + " as ySF, " + getSQL_val(month, true) + " as ljSF "
				   + " from SC03 where b0110='" + b0110 + "' and budget_id=" + getActualIdx(year) + " and tab_id=" + tab_id + ") A "
				   + "where @tb.itemid=A.itemid and " + getWhereCaluse(b0110, year, month, tab_id);
			break;
		case Constant.ORACEL:
			updSql = "update @tb SET (ySF, ljSF)=(select ySF, ljSF " 
				   + " from (select itemid, " + getSQL_val(month, false) + " as ySF, " + getSQL_val(month, true) + " as ljSF "
				   + " from SC03 where b0110='" + b0110 + "' and budget_id=" + getActualIdx(year) + " and tab_id=" + tab_id + ") A "
				   + " where @tb.itemid=A.itemid)"
				   + "where " + getWhereCaluse(b0110, year, month, tab_id);
			break;
		default:
			updSql="";
			break;
		}
		updSql = PubFunc.replace(updSql, "@tb", tablename);					
		try {
			dao.update(updSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 差额及率
		StringBuffer buffer = new StringBuffer();
		buffer.append("update ");
		buffer.append(tablename);
		buffer.append(" set yCE=yFB-ySF, ljCE=ljFB-ljSF, yRate=ySF/NULLIF(yFB,0)*100.0, ljRate=ljSF/NULLIF(ljFB,0)*100.0, nRate=ljSF/NULLIF(nFB,0)*100.0 ");
		buffer.append(" where ");
		buffer.append(getWhereCaluse(b0110, year, month, tab_id));		
		try {
			dao.update(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getRateTablename(){
		return "tmp_"+userView.getUserId()+"_ys_rate";
	}
	
	// 检查指定年度、单位、预算 能不能分析
	private boolean canExecreate(String b0110, int year, int tab_id) {
		ContentDAO dao = new ContentDAO(con);
		try {
			int sfIdx = getActualIdx(year);
			StringBuffer buffer = new StringBuffer();
			RowSet rs;    boolean flag;
			
			// 先检查有没有实发
			buffer.append("select 1 from gz_budget_exec where budget_id=");
			buffer.append(sfIdx);
			buffer.append(" and b0110='");
			buffer.append(b0110);
			buffer.append("' and tab_id=");
			buffer.append(tab_id);
			rs = dao.search(buffer.toString());
			flag = rs.next();
			rs.close();
			if (flag) {
				return true;
			}			
			
			// 没有实发，检查有没有预算
			int ysIdx = getBudgetIdx(year);
			buffer.setLength(0);
			buffer.append("select 1 from gz_budget_exec where budget_id=");
			buffer.append(ysIdx);
			buffer.append(" and b0110='");
			buffer.append(b0110);
			buffer.append("' and tab_id=");
			buffer.append(tab_id);
			rs = dao.search(buffer.toString());
			flag = rs.next();
			rs.close();
			if (!flag) {
				lastErrorInfo = ResourceFactory.getProperty("gz.budget.execrate.nodata");  // "没有的预算数据"
				return false;
			}
			
			// 检查是不是执行分析表
			buffer.setLength(0);
			buffer.append("select 1 from gz_budget_tab where analyseFlag=1 and tab_id=");
			buffer.append(tab_id);
			rs = dao.search(buffer.toString());
			flag = rs.next();
			rs.close();
			if (!flag) {
				lastErrorInfo = ResourceFactory.getProperty("gz.budget.execrate.notansylsetable");  // "选定预算表不是执行分析表";
				return false;
			}
			
		} catch (Exception e) {
			lastErrorInfo = e.getMessage();
			e.printStackTrace();
		}
		return true;
		
	}
	
	
	/** 是否有实发数据，判断依据， SC03当月数值合计值是否为0或空
	 * @param b0110
	 * @param year
	 * @param month
	 * @param tab_id: =0 表示所有表
	 * @return
	 */
	public boolean haveSFData(String b0110, int year, int month, int tab_id) {
		boolean have = true;
		ContentDAO dao = new ContentDAO(con);
		try{
			StringBuffer sqlBuffer = new StringBuffer("select sum(val_");
			sqlBuffer.append(month);
			sqlBuffer.append(") as val from SC03 where b0110='");
			sqlBuffer.append(b0110);
			sqlBuffer.append("' and budget_id=");
			sqlBuffer.append(getActualIdx(year));
			if (tab_id != 0) {
				sqlBuffer.append(" and tab_id=");
				sqlBuffer.append(tab_id);
			}
			
			RowSet rSet = dao.search(sqlBuffer.toString());
			if (rSet.next()) {
				have = rSet.getInt("val") > 0;
			}
			rSet.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return have;
	}
	
	/**
	 * 返回执行率分析的ＳＱＬ
	 * @param b0110
	 * @param year
	 * @param tab_id
	 * @return
	 */
	public String getExecrateSQL (String b0110, int year, int month, int tab_id)
		throws Exception {
		String tablename = getRateTablename();
		// 判断是否实际预算单位，非实际预算单位只显示不能修改和保存
		if (canExecreate(b0110, year, tab_id)) {
			// 没有实发则生成
			if (!generateActualVo(year)) {
				// 检查指定表有没有数据， generateActualVo 为true 时已经调用
				refreshActualRecs(year, tab_id);
			}			

			// 检查有没有统计过实发，没有则自动统计, 先检查当前表，再检查所有表
			// 暂时禁止掉， 实发实际还跟月份有关
			/*
			if (!haveSFData(b0110, year, month, tab_id)) {
				if (!haveSFData(b0110, year, month, 0)) {
					calcActualData(b0110, year, month, 0);
				} else {
					calcActualData(b0110, year, month, tab_id);
				}
			}
			*/
			
			if (genExecrateData(tablename, b0110, year, month, tab_id)){
				StringBuffer buffer = new StringBuffer("select * from ");
				buffer.append(tablename);
				buffer.append(" where ");
				buffer.append(getWhereCaluse(b0110, year, month, tab_id));
				buffer.append(" order by seq, itemid");
				
				return buffer.toString();			
			}
		} else {
			throw new Exception(lastErrorInfo);
		}
		return null;
	}
	
	/**
	 * 保存界面的实发数据到实际表中
	 * @param b0110
	 * @param year
	 * @param month
	 * @param tab_id
	 * @return
	 */
	public boolean saveActualData(String b0110, int year, int month, int tab_id) {
		String tablename = getRateTablename();
		
		ContentDAO dao = new ContentDAO(con);		
		try {
			// 实发 和 累计实发 回写
			String updSql;
			switch (Sql_switcher.searchDbServer()) {
			case Constant.MSSQL:
				updSql = "update SC03 SET " + getSQL_val(month, false) + "=A.ySF " 
					   + " from (select itemid, ySF "
					   + " from @tb where " + getWhereCaluse(b0110, year, month, tab_id) + ") A "
					   + "where SC03.itemid=A.itemid and b0110='" + b0110 + "' and budget_id=" + getActualIdx(year) + " and tab_id=" + tab_id ;
				break;
			case Constant.ORACEL:
				updSql = "update SC03 SET (" + getSQL_val(month, false) + ")=(select ySF " 
				   + " from (select itemid, ySF "
				   + " from @tb where " + getWhereCaluse(b0110, year, month, tab_id) + ") A "
				   + " where SC03.itemid=A.itemid )"
				   + "where b0110='" + b0110 + "' and budget_id=" + getActualIdx(year) + " and tab_id=" + tab_id ;
				break;
			default:
				updSql="";
				break;
			}
			
			updSql = PubFunc.replace(updSql, "@tb", tablename);					
			dao.update(updSql);
			
			// 删除临时表该月后面的数据，用于重新统计后面月份累计
			updSql = "delete from " + tablename + " where b0110='" + b0110 + "' and tab_id=" + tab_id + " and monthnum>"+month;
			dao.update(updSql);
			
			refreshRateData(dao, tablename, b0110, year, month, tab_id);		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
}