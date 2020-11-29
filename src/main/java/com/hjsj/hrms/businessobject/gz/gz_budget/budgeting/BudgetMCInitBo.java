package com.hjsj.hrms.businessobject.gz.gz_budget.budgeting;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaListBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.formula.BudgetFormulaResBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author wangjh 2012-10-22
 * 本类专用于名册初始化的操作
 * 初始化的流程：
 * 清空当期名册——通过人员库、人员范围、预算归属单位引入人员——
 * 计算人员类别——计算退休日期（月份）——生成当前人员退休人员记录（可选）
 * 年初预算时，要删除次年1月前退休人员的非退休记录。
 */
public class BudgetMCInitBo {
	private UserView userView;
	private Connection con;
	private ContentDAO dao;
	
	// 数据库类型
	private int DBType = Sql_switcher.searchDbServer();// 
	// 名册的tabld固定=2, 表名固定为 SC01
	private int tabId = 2;
	// 预算系统项，参数
	private BudgetSysBo sysBo;
	// 初始化单位
	private String b0110;
	// 预算索引
	private int budgetIdx;
	// 人员范围，中文
	private String rangeZh;
	// 人员库列表
	private String dbList;
	// 归属单位指标
	private String unitMenuName;
	private FieldItem unitItem;
	// 是否生成退休记录
	private boolean isBuildTX;
	// 错误信息
	private String lastError;
	// 实发计算标识
	private boolean sfFlag=false;
	
	public boolean isSfFlag() {
		return sfFlag;
	}

	public void setSfFlag(boolean sfFlag) {
		this.sfFlag = sfFlag;
	}

	/**
	 * @param con: 数据库连接
	 * @param budgetIdx: 预算索引
	 * @param b0110: 预算单位
	 */
	public BudgetMCInitBo(Connection con, UserView userView, int budgetIdx, String b0110){ 
		this.con = con;
		this.budgetIdx = budgetIdx;
		this.b0110 = b0110;
		this.userView = userView;
		
		dao = new ContentDAO(this.con);
		sysBo = new BudgetSysBo(con, null);
		HashMap hm = sysBo.getSysValueMap();
		this.unitMenuName = (String) hm.get("unitmenu");
		this.rangeZh = (String) hm.get("range");
		this.dbList = (String) hm.get("dblist");
		this.isBuildTX = (String) hm.get("createTXrecord")=="1";
		
	}
	
	/**
	 * @return 参数是否合法，返回空表示正确
	 */
	private String validParam(){
		// 检查项：有没有归属单位指标并且已构库，人员库列表是否不为空，人员范围是否可解析
		StringBuffer rInfo = new StringBuffer();
		
		// 归属单位指标检查
		if("".equals(unitMenuName)){
			rInfo.append(ResourceFactory.getProperty("gz.budget.person.unit.belong_to") 
					   + ResourceFactory.getProperty("gz.budget.budgeting.mc.nodef"));
		}else{
			unitItem = DataDictionary.getFieldItem(unitMenuName, Constant.ALL_FIELD_SET);
			if(unitItem==null){
				rInfo.append(ResourceFactory.getProperty("gz.budget.person.unit.belong_to") 
						   + ResourceFactory.getProperty("gz.budget.budgeting.mc.nofoundmenu") );
			}
		}
		
		// 人员库列表检查
		if("".equals(dbList)){
			if(!"".equals(rInfo)){
				rInfo.append("\n");
			}
			rInfo.append(ResourceFactory.getProperty("gz.budget.person.base") 
					   + ResourceFactory.getProperty("gz.budget.budgeting.mc.nodef"));
		}
		
		// 人员范围检查
		if (!"".equals(rangeZh)){
			// 需要人员库, 实际运行时检查，有错抛异常
		}		
		return rInfo.toString();
	}
	
	/**
	 * 清除名册数据
	 */
	private void clearData(){
		StringBuffer sqlClear = new StringBuffer("delete from SC01 where budget_id=");
		sqlClear.append(budgetIdx);
		sqlClear.append(" and b0110='");
		sqlClear.append(b0110);
		sqlClear.append("' and not A0100 IS NULL");
		try{
			dao.update(sqlClear.toString());
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 取出人员范围条件 
	 */		
	private String getPersonRange(String db){
		
		return "";		
	}
	
	/**
	 * 增加人员到名册表
	 * 步骤：提取数据到用户的中间表，编号。
	 *  将数据复制到名册表，更新IDFactory表序号
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	public boolean appendPerson(){
		lastError = validParam();
		if(lastError!=null && !"".equals(lastError)){
			return false;
		}
		clearData();
		int count = readyEmpolyee();
		if (count==0) {
			lastError = "没有满足条件的人";
			return false;
		}
		
		// 提取数据(姓名，E0122)，逐个人员库处理 
		String[] db = dbList.split(",");
		String updSql;
		switch (DBType) {
		case Constant.MSSQL:
			updSql = "update SC01 SET A0101=A.A0101, A0000=:no*"+BudgetFormulaResBo.BaseA0000+"+A.A0000, E0122=case when A.E0122 is null then A.B0110 else A.E0122 end "
				   + "from SC01 S, :dbA01 A "
				   + "where S.Nbase=':db' and S.A0100=A.A0100 ";
		     updSql = updSql + " and S.b0110='" + b0110 + "' and S.budget_id=" + budgetIdx;
			break;
		case Constant.ORACEL:
			updSql = "update SC01 SET (A0101,A0000,E0122)=(select A.A0101, :no*"+BudgetFormulaResBo.BaseA0000+"+A.A0000, case when A.E0122 is null then B0110 else E0122 end AS deptcode "
				   + " from :dbA01 A "
				   + " where A.A0100 = Sc01.A0100 )";
				//   + " where exists(select 1 from SC01 S where S.Nbase=':db' and S.A0100=A.A0100 and S.b0110='" 
				//   + b0110 + "' and S.budget_id=" + budgetIdx + ")) "
				//   + "where 1=1 ";
		     updSql = updSql + " where b0110='" + b0110 + "' and budget_id=" + budgetIdx +" and  Nbase=':db'"+"";
			break;
		default:
			updSql="";
			break;
		}
		//updSql = updSql + " and S.b0110='" + b0110 + "' and S.budget_id=" + budgetIdx;
		try {
			for(int i=0; i<db.length; i++){
				String sql = PubFunc.Replace(updSql, ":db", db[i]);
				sql = PubFunc.Replace(sql, ":no", Integer.toString(i));				
				dao.update(sql);				
			}
			
			// 不是实发时才专门处理人员类别和退休
			if (!isSfFlag()) {
				// 计算人员类别 (SC000)
				// 计算退休月份的 retmonth
				BudgetFormulaListBo foListBo = new BudgetFormulaListBo(this.con, this.userView, true);
				foListBo.setBudgetIdx(this.budgetIdx);
				foListBo.setBudgetUnit(this.b0110);
				foListBo.execFormula();
				// 生成退休记录
				if (isBuildTX) {
					
				}else{
					;
				}
			}
			// 生成 beginMonth, endMonth
		} catch (Exception e) {
			
		}
		return true;
		
	}

	/**
	 * @param tmpTb 临时表
	 * @throws SQLException 
	 * @throws GeneralException 
	 */
	private int readyEmpolyee() {
		String tmpTbName = "t#"+userView.getUserName()+"_ys_init";
		if (DBType == Constant.MSSQL) {
			tmpTbName = "##" + tmpTbName;
		}
		
		// 建临时表
		DbWizard dbWizard = new DbWizard(this.con);
		try {
			Table tmpTb = new Table(tmpTbName);
			FieldItem fieldItem;

			fieldItem = new FieldItem("NBASE", "NBASE");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(8);
			tmpTb.addField(fieldItem.cloneField());

			fieldItem = new FieldItem("A0100", "A0100");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(8);
			tmpTb.addField(fieldItem.cloneField());

			//MSSQL库，id加为自增1
			if(Sql_switcher.searchDbServer()!=1){
				fieldItem = new FieldItem("id", "id");
				fieldItem.setItemtype("N");
				fieldItem.setDecimalwidth(0);
				tmpTb.addField(fieldItem.cloneField());
			}
			
			if (dbWizard.isExistTable(tmpTbName, false)) {
				dbWizard.dropTable(tmpTb);
			}
			dbWizard.createTable(tmpTb);

			// SQL格式串
			/*
			String sqlFmt = " from :a01 A ";
			if(!unitItem.isMainSet()){
				sqlFmt = sqlFmt + " inner join :subset S1 on (A.A0100=S1.A0100) inner join (select A0100, Max(I9999) AS MxI9 from :subset group by A0100) S2 on (S1.A0100=S2.A0100 and S1.I9999=S2.MxI9) "
				        + "where S1." + unitMenuName + " like '" + b0110 + "%'"; 
			}else {
				sqlFmt = sqlFmt + "where " + unitMenuName + " like '" + b0110 + "%'"; 
			}
			*/
			// 插入数据（人员库，A0100）
			String[] db = dbList.split(",");
			String sqlFrom; 
				
			// 逐个人员库处理
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, 
			         Constant.ALL_FIELD_SET);
			String cond = unitMenuName + " like '" + b0110 + "%'"; 
			if (!"".equals(rangeZh)){
			    cond =cond+" 且 ("+ rangeZh+")";			    
			}
			
			for(int i=0; i<db.length; i++){
			    /*
				sqlFrom = PubFunc.Replace(sqlFmt, ":a01", db[i]+"A01");
				if(!unitItem.isMainSet()){
					sqlFrom = PubFunc.Replace(sqlFrom, ":subset", db[i]+unitItem.getFieldsetid());
				}	
				*/
				StringBuffer sIn = new StringBuffer("insert into " + tmpTb.getName() + "(NBase, A0100)");
				sIn.append("select '"+db[i]+"' AS nbase, A.A0100 ");
				
				String whereIN="select "+" a0100 from "+db[i]+"A01";
                YksjParser yp = new YksjParser(this.userView ,alUsedFields,
                        YksjParser.forSearch, YksjParser.LOGIC, YksjParser.forPerson, "Ht",db[i]);
                YearMonthCount ymc=null;                            
                yp.run_Where(cond, ymc,"","hrpwarn_result", dao, whereIN,this.con,"A", null);
                String tempTableName = yp.getTempTableName();
                String s = yp.getSQL();
                String sWhere =" from "+db[i]+"A01 A  where exists (select a0100 from "+tempTableName+
                          " where "+tempTableName+".a0100=A.a0100 and ("+s+"))";
				sIn.append(sWhere);
				dao.update(sIn.toString());			
			}
			
			// 编序号
			// 根据数据库换方法
			StringBuffer buffer = new StringBuffer();
			switch (Sql_switcher.searchDbServer()) {
			// MSSQL
			case 1:
				buffer.append("alter table ");
				buffer.append(tmpTb.getName());
				buffer.append(" add id int identity(1,1)");
				dao.update(buffer.toString());
				break;
			default:
			  //oracle更新序号 wangrd 2014-02-14  
			    dao.update("update "+tmpTb.getName()+" set id = rownum");			    
				break;
			}
			
			// 取序号
			int row=0;
			String s1 = "select max(id) AS id from " + tmpTb.getName();
			RowSet rs = dao.search(s1);
			while(rs.next()){
				row = rs.getInt("id");
			}
			rs.close();
			if(row==0){
				return 0;
			}

			// 插入记录
			ArrayList al = new IDFactoryBean().getId("SC01.SC010", "", row, con);		
			StringBuffer sIn = new StringBuffer("insert into SC01(SC010, budget_id, B0110, tab_id, NBase, A0100, beginMonth, endMonth) select ");
			// sIn.append("select ?+id as id, ? as idx, ? as b0110, ? as tabid, NBase, A0100, 1, 12 from " + tmpTb.getName() );
			sIn.append(Integer.valueOf((String)al.get(al.size()-1)));
			// 增加的id从0开始，否则造成下次主键冲突
			sIn.append("-1+id as id, ");
			sIn.append(budgetIdx);
			sIn.append(" as idx, ");
			sIn.append("'" + b0110 + "'");
			sIn.append(" as b0110, ");
			sIn.append(tabId);
			sIn.append(" as tabid, NBase, A0100, 1, 12 from ");
			sIn.append(tmpTb.getName());
			
			dao.update(sIn.toString());

			dbWizard.dropTable(tmpTb);
			
			return row;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public String getLastError() {
		return lastError;
	}
			
}
	