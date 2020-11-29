package com.hjsj.hrms.businessobject.gz.gz_budget.formula;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 类描述：预算公式列表
 * 
 * @author: wangjh
 * @date： 日期：Feb 19, 2013 时间：4:07:22 PM
 * @version 1.0
 */
public class BudgetFormulaListBo {
	private UserView userView;
	private Connection con;

	/** 资源类 */
	private BudgetFormulaResBo res;
	/** 公式列表 */
	private ArrayList list;
	/** 预算单位 */
	private String budgetUnit = "";
	/** 预算索引 */
	private int budgetIdx = 0;
	/** 最近的错误 */
	private String lastErrorMsg;
	/** 公式顺序乱，重新加载单个公式时可能出现这种情况 */
	private boolean orderError = false;
	/** 本次预算开始月份 0表示未读取 */
	private int startMonth = 0;
	/** 名册的TabID */
	private int mcTabId = -1;
	/** 用工计划表TabID */
	private int ygjhTabId = -1;
	/** 预算总额 */
	private int zeTabId = -1;
	/** 一般条件：预算单位和预算索引 */
	private String baseWhere;
	/** 预算人员库 */
	private String[] dbList;
	/** 人员类别字段 */
	private String SC000 = "SC000";

	/** 计算项公式实现类 */

	private BudgetFormulaBo tempfo;

	/**
	 * 审核公式
	 * 
	 * @param formulaID:
	 *            公式ID
	 * @param reload：
	 *            是否重新加载
	 */
	public boolean verifyFormula(int formulaID, String formulaStr) {
		lastErrorMsg = "";

		// 重新加载，
		loadFormulas(3, formulaID);
		BudgetFormulaBo fo0 = getFormula(formulaID);
		if (fo0 != null) {
			// 新公式
			BudgetFormulaBo fo = (BudgetFormulaBo) fo0.clone();
			fo.setExpr(formulaStr);
			tempfo = fo;

			boolean fSucc = false;
			// 根据公式类型调用不同的解析类检查
			if (fo.tabID == getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_IMPORT) {
				// 名册：导入项
				fSucc = verifyMCImport(fo);
			} else if (fo.tabID == getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_CALC
					&& fo.rowColFlag == fo.BUDGET_FORMLAFLAG_COL) {
				// 名册：列
				fSucc = verifyMCCol(fo);
			} else if (isMcSumFormula(fo)) {
				// 名册：行
				fSucc = verifyMCSum(fo);
			} else if (fo.tabID != getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_IMPORT) {
				// 一般预算表：导入项
				fSucc = verifyCommonImport(fo);
			} else if (fo.tabID != getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_CALC) {
				// 一般预算表：计算项
				fSucc = verifyCommonRowCol(fo);
			}

			if (fSucc) {
				fo.setExpr(formulaStr);
			}
			return fSucc;
		} else {
			lastErrorMsg = ResourceFactory
					.getProperty("gz.budget.formula.nofound");
			return false;
		}
	}

	/**
	 * 校验行，列范围
	 * 
	 * @return
	 */
	public boolean verifyRowCol(int formulaID) {
		if (tempfo == null || tempfo.formulaID != formulaID) {
			loadFormulas(3, formulaID);
			tempfo = getFormula(formulaID);
			if (tempfo == null) {
				lastErrorMsg = tempfo.formulaName
						+ ResourceFactory
								.getProperty("gz.budget.formula.nofound");
				return false;
			}
		}
		// 列范围必须都有
		if (tempfo.colRange == null || "".equals(tempfo.colRange.trim())) {
			lastErrorMsg = tempfo.formulaName
					+ ResourceFactory
							.getProperty("gz.budget.formula.verify.nocolrange"); // 没有设置列范围
			return false;
		}
		// 名册的非合计行列范围只能一个（不能有逗号）
		if (tempfo.tabID == getMCTabId() && !isMcSumFormula(tempfo)) {
			if (tempfo.colRange.indexOf(",") != -1) {
				lastErrorMsg = tempfo.formulaName
						+ ResourceFactory
								.getProperty("gz.budget.formula.verify.colrangeinvalid"); // 列范围只能设置一个指标
				return false;
			}
		}
		// 非总额的行公式必须设置计算条件
		if (tempfo.tabID != getMCTabId()
				&& tempfo.formulaType == tempfo.BUDGET_FORMLATYPE_CALC
				&& tempfo.rowColFlag == tempfo.BUDGET_FORMLAFLAG_ROW) {
			if (tempfo.rowRange == null || "".equals(tempfo.rowRange.trim())) {
				lastErrorMsg = tempfo.formulaName
						+ ResourceFactory
								.getProperty("gz.budget.formula.verify.norowrange"); // 行计算公式必须设置计算条件（目标行）
				return false;
			}
		}
		return true;
	}

	public boolean execFormula() {
		return execFormula(0);
	}
	
	// 计算的实发月
	int sfMonthNum = 0;	
	int sfTabId = 0;
	
	/** 执行实发统计公式
	 * 目前使用预算公式，仅执行导入项公式
	 * @param tabid: 预算表号，=0表示所有表
	 * @param month: 计算月份
	 * @return
	 */
	public boolean execActualFormula(int tabid, int month) {
		lastErrorMsg = "";
		if (month <= 0 || month >12) {
			return false;
		}
		
		sfMonthNum = month;
		sfTabId = tabid;
		try {
			return execFormula(0); 
		} finally {
			sfMonthNum = 0;
			sfTabId = 0;
		}
		
	}

	private boolean isMcSumFormula(BudgetFormulaBo fo) {
		return (fo.tabID == getMCTabId()
				&& fo.formulaType == fo.BUDGET_FORMLATYPE_CALC && fo.rowColFlag == fo.BUDGET_FORMLAFLAG_ROW);
	}

	/**
	 * 执行计算公式
	 * 
	 * @param fromFormulaID:
	 *            公式ID, 从哪个公式开始执行. 0表示全部执行
	 */
	public boolean execFormula(int fromFormulaID) {
		lastErrorMsg = "";
		// 未指定预算单位和预算索引时不执行
		if (budgetIdx == 0 || budgetUnit == null || budgetUnit.length() == 0) {
			lastErrorMsg = ResourceFactory
					.getProperty("gz.budget.budgeting.mc.nodef")
					+ ResourceFactory
							.getProperty("gz.budget.budget_allocation.params.value");
			return false;
		}

		// 公式顺序乱，重新加载公式
		if (orderError) {
			loadFormulas(0, 0);
		}
		int fromIdx;
		if (fromFormulaID <= 0) {
			fromIdx = 0;
		} else {
			fromIdx = getFormulaIndex(fromFormulaID);
		}
		if (fromIdx == -1) {
			lastErrorMsg = ResourceFactory
					.getProperty("gz.budget.formula.nofound");
			return false;
		}
		boolean fSucc = true;
		// 开始执行公式
		for (int i = fromIdx; i < list.size(); i++) {
			BudgetFormulaBo fo = (BudgetFormulaBo) list.get(i);
			tempfo = fo;
			
			// 实发计算某个表
			if (sfTabId!=0 && fo.tabID!=sfTabId)
				continue;
			
			if (!verifyRowCol(fo.formulaID)) {
				return false;
			}
			
			if (sfMonthNum == 0) {
				// 预算的
				fo.startMonth = getStartMonth();
				fo.endMonth   = 12;
			} else {
				fo.startMonth = sfMonthNum;
				fo.endMonth   = sfMonthNum;
			}
			/**
			 * 公式分类： 名册：导入项 名册：计算项，列 名册：计算项，行（合计行） 一般预算表导入项 一般预算表计算项公式：行列
			 */
			if (fo.formulaType == fo.BUDGET_FORMLATYPE_INPUT
					|| fo.getUpdateColRange() == null
					|| ((fo.expr == null || "".equals(fo.expr)) && !isMcSumFormula(fo))) {
				// 录入项，没有需要修改的列，忽略
				continue;
			}
			// 解析行范围到SQL
			fo.setRowCond(readCalcCond(fo, null));
			if (fo.tabID == getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_IMPORT) {
				// 名册：导入项
				fSucc = calcMCImport(fo);
				// 退休月份后自动计算结束月份
				if (fo.colRange
						.equalsIgnoreCase(BudgetFormulaResBo.RetMonthFld)) {
					calcMCEndMonth(fo);
				}
			} else if (fo.tabID == getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_CALC
					&& fo.rowColFlag == fo.BUDGET_FORMLAFLAG_COL) {
				// 名册：列
				fSucc = calcMCCol(fo);
				// 退休月份后自动计算结束月份
				if (fo.colRange
						.equalsIgnoreCase(BudgetFormulaResBo.RetMonthFld)) {
					calcMCEndMonth(fo);
				}
			} else if (isMcSumFormula(fo)) {
				// 名册：行
				fSucc = calcMCSum(fo);
			} else if (fo.tabID != getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_IMPORT) {
				// 一般预算表：导入项
				fSucc = calcCommonImport(fo);
			} else if (fo.tabID != getMCTabId()
					&& fo.formulaType == fo.BUDGET_FORMLATYPE_CALC) {
				// 一般预算表：计算项
				fSucc = calcCommonRowCol(fo);
			}
			if (!fSucc) {
				lastErrorMsg = res.findBudgetTab(fo.tabID).tabName +":"+ fo.formulaName + " "
						+ ResourceFactory
								.getProperty("gz.budget.formula.calcfail");
				return false;
			}
		}
		return true;
	}

	private int getDataType(String type) {
		int datatype = 0;
		switch (type.charAt(0)) {
		case 'A':
			datatype = YksjParser.STRVALUE;
			break;
		case 'D':
			datatype = YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype = YksjParser.FLOAT;
			break;
		}
		return datatype;
	}

	/** 名册导入项公式解析器 */
	private YksjParser mcParser;

	/**
	 * 创建计算用的临时表
	 * 
	 * @param fieldlist
	 * @param tablename
	 * @param keyfield
	 * @return
	 */
	private boolean createMidTable(ArrayList fieldlist, String tablename,
			String keyfield) {
		boolean bflag = true;
		try {
			DbWizard dbw = new DbWizard(this.con);
			if (dbw.isExistTable(tablename, false))
				dbw.dropTable(tablename);
			Table table = new Table(tablename);
			for (int i = 0; i < fieldlist.size(); i++) {
				FieldItem fielditem = (FieldItem) fieldlist.get(i);
				Field field = fielditem.cloneField();
				if (field.getName().equalsIgnoreCase(keyfield)) {
					field.setNullable(false);
					field.setKeyable(true);
				}
				table.addField(field);
			}// for i loop end.
			dbw.createTable(table);
		} catch (Exception ex) {
			ex.printStackTrace();
			bflag = false;
		}
		return bflag;
	}

	/**
	 * 初始设置使用字段列表
	 * 
	 * @return
	 */
	private ArrayList initUsedFields() {
		ArrayList fieldlist = new ArrayList();
		/** 人员库 */
		FieldItem fielditem = new FieldItem("A01", "NBase");
		fielditem.setItemdesc("NBase");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(3);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 人员排序号 */
		fielditem = new FieldItem("A01", "A0100");
		fielditem.setItemdesc("a0100");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(8);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 单位名称 */
		fielditem = new FieldItem("A01", "B0110");
		fielditem.setItemdesc("单位名称");
		fielditem.setCodesetid("UN");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 姓名 */
		fielditem = new FieldItem("A01", "A0101");
		fielditem.setItemdesc("姓名");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 人员排序号 */
		fielditem = new FieldItem("A01", "I9999");
		fielditem.setItemdesc("I9999");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(9);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 部门名称 */
		fielditem = new FieldItem("A01", "E0122");
		fielditem.setItemdesc("部门");
		fielditem.setCodesetid("UM");
		fielditem.setItemtype("A");
		fielditem.setItemlength(30);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		/** 结果指标，避免提示错误 */
		fielditem = new FieldItem("A01", "AAAAA");
		fielditem.setItemdesc("AAAAA");
		fielditem.setCodesetid("0");
		fielditem.setItemtype("N");
		fielditem.setItemlength(2);
		fielditem.setDecimalwidth(0);
		fieldlist.add(fielditem);
		return fieldlist;
	}

	/**
	 * 追加不同的指标
	 * 
	 * @param slist
	 * @param dlist
	 */
	private void appendUsedFields(ArrayList slist, ArrayList dlist) {
		boolean bflag = false;
		for (int i = 0; i < slist.size(); i++) {
			FieldItem fielditem = (FieldItem) slist.get(i);
			String itemid = fielditem.getItemid();
			for (int j = 0; j < dlist.size(); j++) {
				bflag = false;
				FieldItem fielditem0 = (FieldItem) dlist.get(j);
				String ditemid = fielditem0.getItemid();
				if (itemid.equalsIgnoreCase(ditemid)) {
					bflag = true;
					break;
				}

			}// for j loop end.
			if (!bflag)
				dlist.add(fielditem);
		}// for i loop end.
	}

	private String mcTmpTable;

	/**
	 * 初始化名册临时表 表中数据已经建立，并建立数据
	 * 
	 * @param dbPre:
	 *            人员库
	 * @return 返回表名
	 */
	private String iniMcTmpTable(String dbPre, FieldItem item) {
		if (mcTmpTable == null) {
			mcTmpTable = "t#" + this.userView.getUserName() + "_ys_mc";

			ArrayList usedlist = initUsedFields();
			// 创建表
			createMidTable(usedlist, mcTmpTable, "Nbase, A0100");
			// 初始化数据
			ContentDAO dao = new ContentDAO(this.con);
			try {
				StringBuffer buf = new StringBuffer("insert into ");
				buf.append(mcTmpTable);
				buf
						.append("(Nbase, A0100, B0110, A0101, E0122) select Nbase, A0100, B0110, A0101, E0122 from SC01 ");
				buf.append("where ");
				buf.append(getBaseWhereMCNoSum(null));
				// 初始表包括多个人员库
				// buf.append(" and nbase='");
				// buf.append(dbPre);
				// buf.append("'");
				dao.update(buf.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 重新构建AAAAA
		Table table = new Table(mcTmpTable);
		Field obj = item.cloneField();
		obj.setName("AAAAA");
		table.addField(obj);

		DbWizard dbw = new DbWizard(this.con);
		try {
			if (dbw.isExistField(mcTmpTable, "AAAAA"))
				dbw.dropColumns(table);
			dbw.addColumns(table);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return mcTmpTable;
	}

	/**
	 * 结束月份自动计算，（退休月份计算完成后调用）
	 * 
	 * @param fo
	 */
	private void calcMCEndMonth(BudgetFormulaBo fo) {
		ContentDAO dao = new ContentDAO(con);
		try {
			// 结束月份=退休月份-1， 当年1月退休的， endmonth=0， 名册汇总时可能需要排除 结束月份=0的情况
			StringBuffer buffer = new StringBuffer("update SC01 set endmonth=");
			buffer.append("case when retmonth is null or retmonth<1 or retmonth > 12 then 12 else retmonth-1 end ");
			buffer.append(" where ");
			buffer.append(getBaseWhereMCNoSum(null));
			dao.update(buffer.toString());

			// 退休记录处理
			// 退休月份小于0 的人员预算年度就已退休， 直接改为退休人员 或删除
			// 预算年度中退休的人员 如处理退休人员名册，则增加退休记录，但具体项目怎么处理不清楚，也可能导致合计行出错
			if (getRes().isBuildTX && !"".equals(getRes().getTxItemCode())) {
				/** 退休人员有几种？
				 *  1. 当前已退休人员
				 *  2. 当前在职，从本次预算开始月份起就退休的人员
				 *  3. 当前在职，本次预算期间退休的人
				 */
				/**
				 * 退休人员处理：
				 * 1. 清除自动产生的退休记录
				 * 2. 自动生成当前在职的退休记录
				 * 3. 删除本次预算开始月份起就退休人员的原始记录
				 * 
				 * 名册初始化时，自动退休记录已经处理。 正常计算时，自动退休记录已经产生。
				 * 增加一个标志指标，处理会比较方便：
				 * 1. 增加指标 voFlag = (0或null: 初始化后的记录， 1： 自动产生的退休记录)
				 *    统计：
				 *      名册合计行，不统计 endmonth = 0 的记录
				 *      预算汇总时，月份统计自动不统计 endmonth=0记录，但其他统计如减少人数应该统计 endmonth<> 12 的人数， endmonth=0的应统计。  
				 *    计算退休人员时，删除voFlag=1 的
				 *    
				 *  2013-11-20,  WJH, 调整 
				 *  预算年度1月份退休的人员，包含当前在职但预算年度前退休人员， 名册中也有2条记录， 退休月份=1（endmonth=0）
				 */
				buffer.setLength(0);
				buffer.append("delete from SC01 where ");
				buffer.append(getBaseWhere(null, -1));
				buffer.append(" and voFlag=1 ");
				dao.update(buffer.toString());
				
				// 恢复记录本次预算就退休人员记录， 保留，历史情况voFlag=-1的处理
				buffer.setLength(0);
				buffer.append("update SC01 set voFlag=0 where ");
				buffer.append(getBaseWhere(null, -1));
				buffer.append(" and voFlag=-1 ");
				dao.update(buffer.toString());
				
				// 取出产生的退休人员记录数
				buffer.setLength(0);
				buffer.append("select count(*) AS cou from SC01 where ");
				buffer.append(getBaseWhere(null, -1));
				buffer.append(" and SC000<>'" + getRes().txItemCode + "' and SC000<>'99' and endmonth<12");
				int txNum = 0;
				RowSet rSet = dao.search(buffer.toString());
				if(rSet.next())
					txNum = rSet.getInt("cou");
				rSet.close();
				
				// 插入退休记录
				ArrayList al = new IDFactoryBean().getId("SC01.SC010", "", txNum, con);
				
				String defFieldString = "SC010, tab_id, budget_id, B0110, NBase, A0100, A0000, A0101, SC000, voFlag, beginmonth, endmonth";
				//对应的值   :nid, tabid, budget_id, B0110, NBase, A0100, A0000, A0101, SC000, 1
				String fldsCopy = txFields(fo, defFieldString);
				
				// 退休月份置为退休记录的开始月份
				String insertCom = "insert into SC01(" + defFieldString + fldsCopy + ") "
		             + "select :nid, tab_id, budget_id, B0110, NBase, A0100, A0000, A0101, '"+getRes().txItemCode+"', 1, retmonth, 12" + fldsCopy + " from SC01 "
		             + "where SC010=:oid";	
				
				buffer.setLength(0);
				buffer.append("select SC010, retmonth from SC01 where ");
				buffer.append(getBaseWhere(null, -1));
				buffer.append(" and SC000<>'" + getRes().txItemCode + "' and SC000<>'99' and endmonth<12");
				rSet = dao.search(buffer.toString());
				int i = 0;
				while (rSet.next()) {
					dao.update(PubFunc.replace(PubFunc.replace(insertCom, ":nid", (String)al.get(i)), ":oid", rSet.getString("SC010") ) );
					i++;	
					
					// 本条退休一月份开始的话，原纪录 voFlag 置为 -1. (退休月份小于预算开始月份)
					// 2013-11-20,  WJH, 去掉，参见上面调整的注释
//					if (rSet.getInt("retmonth") <= getStartMonth())
//						dao.update("update SC01 set voFlag = -1 where SC010=" + rSet.getInt("SC010"));
				}
				rSet.close();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// 取计算退休人员时需要复制数据的指标, 参数为退休月份公式, 返回值以逗号开始
	// defFields 为默认列，返回字段中需要排除这些列
	private String txFields(BudgetFormulaBo fo, String defFields){
		StringBuffer flds = new StringBuffer();
	
		for (int i = 0; i < list.size(); i++){
			// 只处理退休月份前的计算公式指标
			BudgetFormulaBo cBo = (BudgetFormulaBo) list.get(i);
			if (cBo.colRange
					.equalsIgnoreCase(BudgetFormulaResBo.RetMonthFld)) {
				break;
			}
			
			// 
			if (cBo.tabID==getMCTabId()) {
				// 默认指标略过
				if (defFields.indexOf(cBo.colRange) >= 0) 
					continue;
				
				flds.append(",");
				flds.append(cBo.colRange);
			}
		}
		
		return flds.toString();
	}

	/** 计算名册的导入项 */
	private boolean calcMCImport(BudgetFormulaBo fo) {
		String dbPre = "usr";
		if (mcParser == null) {
			mcParser = new YksjParser(this.userView, getRes().getDBItemList(),
					YksjParser.forSearch, YksjParser.STRVALUE,
					YksjParser.forPerson, "SC01", dbPre);
		}

		ContentDAO dao = new ContentDAO(this.con);
		try {
			// 此处colRange只有一个列
			FieldItem item = DataDictionary.getFieldItem(fo.colRange);
			if (item == null) {
				throw GeneralExceptionHandler.Handle(new Exception("没有找到指标："
						+ fo.colRange));
			}
			// 多人员库一起处理
			mcDataTable = null;
			iniMcTmpTable(dbPre, item);

			mcParser.setStdTmpTable(mcTmpTable);
			mcParser.setTargetFieldDecimal(item.getDecimalwidth());
			mcParser.setVarType(getDataType(item.getItemtype()));
			
			String[] dbs = getDbList();
			// 遍历多个人员库
			for (int i = 0; i < dbs.length; i++) {
				dbPre = dbs[i];
				mcParser.setDbPre(dbPre);
				mcParser.run(fo.getExpr(), null, "AAAAA", mcTmpTable, dao,
						"select A0100 from " + mcTmpTable + " where nbase='"
								+ dbPre + "'", this.con, item.getItemtype(),
						item.getItemlength(), 2, item.getCodesetid());
			}
			DbWizard dbw = new DbWizard(this.con);
			StringBuffer bfJoin = new StringBuffer("SC01.A0100=" + mcTmpTable
					+ ".A0100 ");
			bfJoin.append(" and SC01.NBase=" + mcTmpTable + ".NBase ");
			StringBuffer bufCond = new StringBuffer();
			bufCond.append(getBaseWhereMCNoSum("SC01"));
			if (fo.getRowCond() != null) {
				bufCond.append(" and ");
				bufCond.append(fo.getRowCond());
			}
			// 计算人员类别公式时，“新员工”的人员类别不能被计算,  当年退休人员不能被计算
			if (fo.colRange.equalsIgnoreCase(SC000)) {
				bufCond.append(" and (SC01.A0101<>'" + getRes().NewStaffA0101 + "') and (voFlag is null or voFlag<>1)");
			}
			return dbw.updateRecord("SC01", mcTmpTable, bfJoin.toString(),
					"SC01." + item.getItemid() + "=" + mcTmpTable + ".AAAAA",
					bufCond.toString(), "");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 返回是否有set中的字段
	 * 
	 * @param itemList:
	 *            公式解析器返回的使用字段
	 * @param set：检索的信息集
	 * @return 返回使用的字段列表，以逗号分隔
	 */
	private String usedFields(HashMap itemMap, String set) {
		if (set == null || itemMap.size() == 0)
			return null;

		StringBuffer buf = new StringBuffer();
		Iterator iterator = itemMap.keySet().iterator();
		while (iterator.hasNext()) {
			FieldItem item = (FieldItem) itemMap.get(iterator.next());
			if (set.equalsIgnoreCase(item.getFieldsetid())) {
				if (buf.length() > 0) {
					buf.append(",");
				}
				buf.append(item.getItemid());
			}
		}

		if (buf.length() == 0)
			return null;
		else
			return buf.toString();
	}

	/** 计算名册的列公式 */
	private YksjParser mcColParser;

	private boolean calcMCCol(BudgetFormulaBo fo) {
		String dbPre = "";
		if (mcColParser == null) {
			mcColParser = new YksjParser(this.userView, getRes().getMCItemList(
					true), YksjParser.forNormal, YksjParser.STRVALUE,
					YksjParser.forPerson, "SC01", "");
		}

		ContentDAO dao = new ContentDAO(this.con);
		try {
			// 此处colRange只有一个列
			FieldItem item = DataDictionary.getFieldItem(fo.colRange, "SC01");
			if (item == null) {
				throw GeneralExceptionHandler.Handle(new Exception("没有找到指标："
						+ fo.colRange));
			}

			mcDataTable = null;
			mcColParser.setTargetFieldDecimal(item.getDecimalwidth());
			mcColParser.setVarType(getDataType(item.getItemtype()));
			mcColParser.run(fo.expr, this.con, getBaseWhere("SC01", fo.tabID),
					getTableName(fo.tabID));
			String expr = mcColParser.getSQL();

			// 用到的预算总额,参数指标
			String sZeFlds = usedFields(mcColParser.getMapUsedFieldItems(), res
					.getSetYSZE());
			String sCsFlds = usedFields(mcColParser.getMapUsedFieldItems(), res
					.getSetParam());
			String sSubset = "(select #flds from #tab where b0110='"
					+ this.budgetUnit + "' and #Idx=" + this.budgetIdx + ") ";
			String sZe = null;
			String sCs = null;

			int iFlag = 0;
			if (sZeFlds != null) {
				iFlag++;
				sZe = sSubset.replace("#flds", sZeFlds);
				sZe = sZe.replace("#tab", res.getSetYSZE());
				sZe = sZe.replace("#Idx", (String) res.getSysMap().get(
						"ysze_idx_menu"));
			}
			if (sCsFlds != null) {
				iFlag++;
				sCs = sSubset.replace("#flds", sCsFlds);
				sCs = sCs.replace("#tab", res.getSetParam());
				sCs = sCs.replace("#Idx", (String) res.getSysMap().get(
						"ysparam_idx_menu"));
			}

			StringBuffer buf = new StringBuffer("update ");
			buf.append(getTableName(fo.tabID));
			buf.append(" set ");
			if (Sql_switcher.searchDbServer() == 1 || iFlag == 0) {
				// MSSQL， Oracle 无子集
				buf.append(item.getItemid());
				buf.append("=");
				buf.append(expr);
			} else {
				// oracle, 有子集
				buf.append("(");
				buf.append(item.getItemid());
				buf.append(")=(select ");
				buf.append(expr);
			}

			// from 一样
			if (iFlag >= 1) {
				StringBuffer bufFromBuffer = new StringBuffer(" from ");
				if (sZe != null) {
					bufFromBuffer.append(sZe);
					bufFromBuffer.append("A ");
				}
				if (iFlag >= 2)
					bufFromBuffer.append(",");
				if (sCs != null) {
					bufFromBuffer.append(sCs);
					bufFromBuffer.append("B ");
				}
				if (Sql_switcher.searchDbServer() == 2) {
					bufFromBuffer.append(") ");
				}
				buf.append(bufFromBuffer.toString());
			}

			buf.append(" where ");
			buf.append(getBaseWhereMCNoSum(getTableName(fo.tabID)));
			if (fo.getRowCond() != null) {
				buf.append(" and ");
				buf.append(fo.getRowCond());
			}
			// 计算人员类别公式时，“新员工”的人员类别不能被计算
			if (fo.colRange.equalsIgnoreCase(SC000)) {
				buf.append(" and SC01.A0101<>'" + getRes().NewStaffA0101 + "'");
			}

			dao.update(buf.toString());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/** 计算名册的合计行 */
	private boolean calcMCSum(BudgetFormulaBo fo) {
		// 名册合计行，更加范围，统计列范围的SUM
		// 如果根据范围设置了多个合计行公式，不能用删除再插入记录的方式，只能使用更新
		DbWizard dbw = new DbWizard(this.con);
		ContentDAO dao = new ContentDAO(this.con);
		try {
			// 合计条件
			StringBuffer bufsum = new StringBuffer(" where ");
			bufsum.append(getBaseWhere(null, -1));
			bufsum.append(" and ");
			bufsum.append(getRes().mcSumField);
			bufsum.append("='");
			bufsum.append(getRes().mcSumValue);
			bufsum.append("'");

			StringBuffer bufExist = new StringBuffer("select A0101 from SC01 ");
			bufExist.append(bufsum.toString());
			RowSet rs = dao.search(bufExist.toString());
			// 空, 增加记录
			if (!rs.next()) {
				StringBuffer buffer = new StringBuffer(
						"insert into SC01(SC010, budget_id, B0110, tab_id, A0000, ");
				buffer.append(getRes().mcSumField);
				buffer.append(") values(");
				buffer.append(new IDFactoryBean().getId("SC01.SC010", "", con));
				buffer.append(",");
				buffer.append(this.budgetIdx);
				buffer.append(",'");
				buffer.append(this.budgetUnit);
				buffer.append("',");
				buffer.append(getMCTabId());
				buffer.append(",");
				buffer.append(getRes().SumA0000);
				buffer.append(",'");
				buffer.append(getRes().mcSumValue);
				buffer.append("')");
				dao.update(buffer.toString());
			}
			// 统计合计
			int iDb = Sql_switcher.searchDbServer();
			StringBuffer bufSum = new StringBuffer("(select ");
			StringBuffer bufSetV1 = new StringBuffer();
			StringBuffer bufSetV2 = new StringBuffer();

			String[] cols = fo.colRange.split(",");
			for (int i = 0; i < cols.length; i++) {
				FieldItem item = DataDictionary.getFieldItem(cols[i]);
				if (item == null || !"N".equalsIgnoreCase(item.getItemtype())) {
					continue;
				}
				if (bufSetV1.length() > 0) {
					bufSetV1.append(", ");
					bufSetV2.append(", ");
					bufSum.append(", ");
				}
				bufSum.append("sum(");
				bufSum.append(cols[i]);
				bufSum.append(") AS ");
				bufSum.append(cols[i]);
				switch (iDb) {
				case 1:
					bufSetV1.append(cols[i]);
					bufSetV1.append("=A.");
					bufSetV1.append(cols[i]);
					break;
				case 2:
					bufSetV1.append(cols[i]);
					bufSetV2.append("A.");
					bufSetV2.append(cols[i]);
					break;
				}
			}
			if (bufSetV1.length() != 0) {
				bufSum.append(" from ");
				bufSum.append(" SC01 where ");
				bufSum.append(getBaseWhereMCNoSum(null));
				//　2013-11-20 WJH
				bufsum.append(" and endmonth<>0 ");
				if (fo.getStatRange() != null
						&& !"".equals(fo.getStatRange().trim())) {
					String stat = readCalcCond(fo, fo.getStatRange());
					bufSum.append(" and ");
					bufSum.append(stat);
				}
				bufSum.append(") A");

				StringBuffer buf = new StringBuffer("update ");
				buf.append("SC01");
				buf.append(" set ");
				// oracle 和 MSSQL 的bufSetV1 是不同的
				if (iDb == 2) {
					buf.append("(");
					buf.append(bufSetV1.toString());
					buf.append(")=(select ");
					buf.append(bufSetV2.toString());
				} else {
					buf.append(bufSetV1.toString());
				}
				buf.append(" from ");
				buf.append(bufSum.toString());
				if (iDb == 2)
					buf.append(")");

				buf.append(bufsum.toString());
				dao.update(buf.toString());
			}
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/** 计算一般预算表的计算项 */
	private BudgetFormulaParser rowcolParser;

	private boolean calcCommonRowCol(BudgetFormulaBo fo) {
		if (rowcolParser == null)
			rowcolParser = new BudgetFormulaParser(this.con, this.userView,
					getRes());
		boolean flag = rowcolParser.run(fo, this.budgetIdx, this.budgetUnit);
		lastErrorMsg = rowcolParser.getError();

		return flag;
	}

	private String mcDataTable = null;

	// 名册临时数据表，计算导入项时复制的， 停用
	private String getMCDataTable() {
		if (mcDataTable == null) {
			mcDataTable = "t#" + userView.getUserName() + "_ys_mcdata";

			DbWizard dbw = new DbWizard(con);
			dbw.dropTable(mcDataTable);
			StringBuffer bufWhere = new StringBuffer();
			bufWhere.append(getBaseWhereMCNoSum(null));
			dbw.createTempTable("SC01", mcDataTable, "*", bufWhere.toString(),
					"");

		}
		return mcDataTable;
	}

	/** 计算一般预算表的导入项 */
	private YksjParser imParser;
	private boolean calcCommonImport(BudgetFormulaBo fo) {
		if (fo == null || fo.getUpdateColRange() == null) {
			return true;
		}

		if (imParser == null) {
			imParser = new YksjParser(this.userView, getRes().getMCItemList(
					true), YksjParser.forNormal, YksjParser.FLOAT,
					YksjParser.forPerson, "Ht", "");
		}
		String[] cols = fo.getUpdateColRange().split(",");
		for (int i = 0; i < cols.length; i++) {
			FieldItem item = DataDictionary.getFieldItem(cols[i],
					getTableName(fo.tabID));
			if (item == null)
				continue;

			imParser.setTargetFieldDataType(item.getItemtype());
			imParser.setTargetFieldDecimal(item.getDecimalwidth());

			// 替换公式中的计算月份
			int month = getRes().getYsMonthValue(cols[i]);
			String formula;
			// 通用统计条件，月份限制
			StringBuffer condSC01 = new StringBuffer();
			condSC01.append(getBaseWhereMCNoSum("SC01"));
			if (month > 0 && month <= 12) {
				formula = fo.getExpr().replaceAll(getRes().calcMonth,
						Integer.toString(month));
				condSC01.append(" and beginmonth<=");
				condSC01.append(month);
				condSC01.append(" and endmonth>=");
				condSC01.append(month);
			} else {
				formula = fo.getExpr();
			}
			/**
			 * 预算汇总公式 预算汇总(表达式[,条件[,分组指标]])
			 */
			try {
				// update SC02
				// set val_1 = G1
				// from (select sc000, sum(case when 1=2 then E5809+E5807 else
				// E5809+E5807+SC01z9 end) as G1 from SC01
				// where budget_id = 8 and B0110 = '0101' and A0101<>'合计'
				// and beginMonth<=1 and endMonth>=1
				// group by sc000
				// ) A
				// where itemid=A.SC000 and budget_id = 8 and B0110 = '0101'
				imParser.run(formula);
				// 简单处理：总额，参数直接加上
				String sTemplet = "(select * from #tab where b0110='"
						+ this.budgetUnit + "' and #Idx=" + this.budgetIdx
						+ ") ";
				String sParamSet = PubFunc.replace(PubFunc.replace(sTemplet,
						"#tab", getRes().getSetParam()), "#Idx", (String) res
						.getSysMap().get("ysparam_idx_menu"));
				String sZeSet = PubFunc.replace(PubFunc.replace(sTemplet,
						"#tab", getRes().getSetYSZE()), "#Idx", (String) res
						.getSysMap().get("ysze_idx_menu"));
				
				String cond = null, grpFld = null;
				String[] subSQL = new String[imParser.getSQLS().size()];
				if (Sql_switcher.searchDbServer() == 1) {
					for(int j=0; j<imParser.getSQLS().size(); j++){
						String[] sbg = imParser.getSQLS().get(j).toString().split(";");
						if(sbg.length<2 && !":#budgetsum".equals(sbg[0]))
							continue;

						String expr = sbg[1];
						if (sbg.length >= 3)
							cond = sbg[2];
						if (sbg.length >= 4)
							grpFld = sbg[3];
		
						// 统计子查询
						StringBuffer bufFrom = new StringBuffer("(select ");
						if (grpFld != null) {
							bufFrom.append(grpFld);
							bufFrom.append(",");
						}
						bufFrom.append(expr);
						bufFrom.append(" from SC01, ");
						bufFrom.append(sZeSet);
						bufFrom.append(" ZE,");
						bufFrom.append(sParamSet);
						bufFrom.append(" PM ");
						bufFrom.append(" where ");
						bufFrom.append(condSC01.toString());
						if (cond != null) {
							bufFrom.append(" and ");
							bufFrom.append(cond);
						}
						if (grpFld != null) {
							bufFrom.append(" group by ");
							bufFrom.append(grpFld);
						}
						bufFrom.append(") A"+j);
						subSQL[j] = bufFrom.toString();
					}
				}
				else {
					for(int j=0; j<imParser.getSQLS().size(); j++){
						String[] sbg = imParser.getSQLS().get(j).toString().split(";");
						if(sbg.length<2 && !":#budgetsum".equals(sbg[0]))
							continue;

						String expr = sbg[1];
						if (sbg.length >= 3)
							cond = sbg[2];
						if (sbg.length >= 4)
							grpFld = sbg[3];
		
						// 统计子查询
						StringBuffer bufFrom = new StringBuffer(" select * from (select ");
						if (grpFld != null) {
							bufFrom.append(grpFld);
							bufFrom.append(",");
						}
						bufFrom.append(expr);
						bufFrom.append(" from (select * from SC01, ");//oracle 
						bufFrom.append(sZeSet);
						bufFrom.append(" ZE,");
						bufFrom.append(sParamSet);
						bufFrom.append(" PM ");
						bufFrom.append(" where ");
						bufFrom.append(condSC01.toString());
						if (cond != null) {
							bufFrom.append(" and ");
							bufFrom.append(cond);
						}
						bufFrom.append(") AA ");
						if (grpFld != null) {
							bufFrom.append(" group by ");
							bufFrom.append(grpFld);
						}
						bufFrom.append(") A"+j);
						subSQL[j] = bufFrom.toString();
					}
				}
				

				// 组合ｕｐｄａｔｅ的ＳＱＬ
				StringBuffer buffer = new StringBuffer("update ");
				StringBuffer clearBuffer = new StringBuffer();
				buffer.append(getTableName(fo.getTabID()));
				buffer.append(" set ");
				
				// 清空
				clearBuffer.append(buffer.toString());
				clearBuffer.append(item.getItemid());
				clearBuffer.append(" = null where ");
				clearBuffer.append(getBaseWhere(null, fo.tabID));
				if (fo.rowCond != null) {
					clearBuffer.append(" and ");
					clearBuffer.append(fo.rowCond);
				}
				
				if (Sql_switcher.searchDbServer() == 1) {
					// MSSQL
					buffer.append(item.getItemid());
					buffer.append("="+imParser.getSQL()+" from ");
					for(int j=0; j<subSQL.length; j++){
						if(subSQL[j]==null || subSQL[j].length()==0)
							continue;
						buffer.append(subSQL[j]);
						buffer.append(",");
					}
					buffer.append(sZeSet);
					buffer.append(" ZE,");
					buffer.append(sParamSet);
					buffer.append(" PM ");
					buffer.append(" where ");
					buffer.append(getBaseWhere(getTableName(fo.getTabID()), fo.tabID));
					if (fo.rowCond != null) {
						buffer.append(" and ");
						buffer.append(fo.rowCond);
					}
					if (grpFld != null) {
						for(int j=0; j<subSQL.length; j++){
							buffer.append(" and itemid=A"+j+".");
							buffer.append(grpFld);
						}
					}
				} else {
					// oracle
					buffer.append("(");
					buffer.append(item.getItemid());
					buffer.append(")=(select "+imParser.getSQL()+" from (");
					for(int j=0; j<subSQL.length; j++){
						if(subSQL[j]==null || subSQL[j].length()==0)
							continue;
						buffer.append(subSQL[j]);
						buffer.append(",");
					}
					buffer.append(sZeSet);
					buffer.append(" ZE,");
					buffer.append(sParamSet);
					buffer.append(" PM ");
					buffer.append(") Ac ");
					if (grpFld != null) {
						buffer.append(" where itemid=Ac.");
						buffer.append(grpFld);
					}
					buffer.append(") where ");
					buffer.append(getBaseWhere(null, fo.tabID));
					if (fo.rowCond != null) {
						buffer.append(" and ");
						buffer.append(fo.rowCond);
					}
				}

				ContentDAO dao = new ContentDAO(con);
				// 先清空
				dao.update(clearBuffer.toString() );			
				dao.update(buffer.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/** 校验名册的导入项 */
	private boolean verifyMCImport(BudgetFormulaBo fo) {
		String dbPre = "usr";
		if (mcParser == null) {
			mcParser = new YksjParser(this.userView, getRes().getDBItemList(),
					YksjParser.forSearch, YksjParser.STRVALUE,
					YksjParser.forPerson, "SC01", dbPre);
			mcParser.setCon(con);
		}

		boolean fSucc = mcParser.Verify(fo.getExpr());
		if (!fSucc) {
			lastErrorMsg = mcParser.getStrError();
		}

		return fSucc;
	}

	/** 校验名册的列公式 */
	private boolean verifyMCCol(BudgetFormulaBo fo) {
		if (fo == null/* || fo.getUpdateColRange() == null */) {
			return true;
		}

		if (mcColParser == null) {
			mcColParser = new YksjParser(this.userView, getRes().getMCItemList(
					true), YksjParser.forNormal, YksjParser.FLOAT,
					YksjParser.forPerson, "Ht", "");
		}
		boolean fSucc = mcColParser.Verify(fo.getExpr());
		if (!fSucc) {
			lastErrorMsg = mcColParser.getStrError();
		}

		return fSucc;
	}

	/** 校验名册的合计行 */
	private boolean verifyMCSum(BudgetFormulaBo fo) {
		return true;
	}

	/** 校验一般预算表的计算项 */
	private boolean verifyCommonRowCol(BudgetFormulaBo fo) {
		if (rowcolParser == null)
			rowcolParser = new BudgetFormulaParser(this.con, this.userView,
					getRes());
		boolean flag = rowcolParser.verify(fo);
		lastErrorMsg = rowcolParser.getError();

		return flag;
	}

	/** 校验一般预算表的导入项 */
	private boolean verifyCommonImport(BudgetFormulaBo fo) {
		if (fo == null/* || fo.getUpdateColRange() == null */) {
			return false;
		}

		if (imParser == null) {
			imParser = new YksjParser(this.userView, getRes().getMCItemList(
					true), YksjParser.forNormal, YksjParser.FLOAT,
					YksjParser.forPerson, "Ht", "");
		}
		if (fo.getExpr().indexOf("预算汇总") == -1) {
			lastErrorMsg = ResourceFactory
					.getProperty("gz.budget.formula.verify.commonimport");
			return false;
		}

		boolean fSucc = imParser.Verify(fo.getExpr());
		if (!fSucc) {
			lastErrorMsg = imParser.getStrError();
		}

		return fSucc;
	}

	private HashMap getSysMap() {
		HashMap sysOptionMap = new HashMap(); // 系统项参数
		BudgetSysBo bo = new BudgetSysBo(this.con, this.userView);
		sysOptionMap = bo.getSysValueMap();
		return sysOptionMap;
	}

	// 返回计算条件
	private String readCalcCond(BudgetFormulaBo fo, String condFormula) {
		// 默认取计算条件标志
		boolean defcond = condFormula == null || "".equals(condFormula);

		if (fo == null || defcond
				&& (fo.rowRange == null || "".equals(fo.rowRange))) {
			return null;
		}

		YksjParser yp = null;
		try {
			ArrayList alItem;
			if (isMCTab(fo.tabID)) {
				alItem = getRes().getMCItemList();
			} else if (isZETab(fo.tabID)) {
				alItem = getRes().getZEItemList();
			} else if (isYgjhTab(fo.tabID)) {
				alItem = getRes().getYgjhItemList();
			} else {
				alItem = getRes().getYsbItemList();
			}

			yp = new YksjParser(this.userView, alItem, YksjParser.forNormal,
					YksjParser.LOGIC, YksjParser.forPerson,
					getTableName(fo.tabID), "");
			yp.setAddTableName(true); // 列名前加表名
			if (!defcond)
				yp.run_where(condFormula);
			else {
				yp.run_where(fo.rowRange);
			}
			return "(" + yp.getSQL() + ")";
		} catch (Exception ex) {

		} finally {
			yp = null;
		}
		return null;
	}

	// 取出公式的位置索引
	private int getFormulaIndex(int formulaID) {
		int i = -1;
		for (int j = 0; j < list.size(); j++) {
			i++;
			BudgetFormulaBo fo = (BudgetFormulaBo) list.get(j);
			if (fo.formulaID == formulaID) {
				return i;
			}
		}
		// 未找到，返回-1
		return -1;
	}

	// 根据公式ID返回公式对象
	public BudgetFormulaBo getFormula(int formulaID) {
		// for(BudgetFormulaBo fo : list ){
		for (int j = 0; j < list.size(); j++) {
			BudgetFormulaBo fo = (BudgetFormulaBo) list.get(j);
			if (fo.formulaID == formulaID) {
				return fo;
			}
		}
		return null;
	}

	/**
	 * 一般构造函数，取所有预算公式
	 * 
	 * @param con
	 * @param userView
	 */
	public BudgetFormulaListBo(Connection con, UserView userView) {
		this.userView = userView;
		this.con = con;
		list = new ArrayList();

		this.loadFormulas(0, 0);
	}

	/**
	 * 名册初始化时使用的构造函数，取所有名册初始化需要执行的预算公式，名册的导入项公式（给bMCInit传true）
	 * 
	 * @param con
	 * @param userView
	 */
	public BudgetFormulaListBo(Connection con, UserView userView,
			boolean bMCInit) {
		this.userView = userView;
		this.con = con;
		list = new ArrayList();

		if (bMCInit) {
			this.loadFormulas(1, 0);
		} else {
			this.loadFormulas(0, 0);
		}

	}

	/**
	 * 只加载指定的公式，用于公式的检查
	 * 
	 * @param con
	 * @param userView
	 */
	public BudgetFormulaListBo(Connection con, UserView userView, int formulaID) {
		this.userView = userView;
		this.con = con;
		list = new ArrayList();

		this.loadFormulas(2, formulaID);
	}

	/**
	 * @param flag:
	 *            0=所有公式, 1=名册初始化执行公式, 2=取formulaID指定的公式，用于公式检查
	 *            3=不进行初始化重新加载公式formulaID
	 * @param formulaID
	 */
	private void loadFormulas(int flag, int formulaID) {
		// flag=3是重载一个公式
		if (flag != 3) {
			list.clear();
			orderError = false;
		}
		BudgetFormulaBo fo = null;
		RowSet rs = null;
		ContentDAO dao = new ContentDAO(con);

		StringBuffer sb = new StringBuffer(
				"select formula_Id,tab_id,formulaName,formuladcrp,formulaType,seq,extAttr ");
		sb.append(" from gz_budget_formula ");
		sb.append(" where destFlag=1 ");
		// 排除掉录入项公式（不计算）
		sb.append(" and formulaType<>");
		sb.append(BudgetFormulaBo.BUDGET_FORMLATYPE_INPUT);
		switch (flag) {
		case 1:
			sb
					.append(" and tab_id in (select tab_id from gz_budget_tab where tab_type=2) and formulaType=");
			sb.append(BudgetFormulaBo.BUDGET_FORMLATYPE_IMPORT);
			break;
		case 3:
		case 2:
			sb.append(" and formula_Id=");
			sb.append(formulaID);
			break;
		}
		sb.append(" order by seq");
		try {
			rs = dao.search(sb.toString());
			String xml = "";
			while (rs.next()) {
				// flag=3是重载一个公式
				if (flag == 3) {
					fo = getFormula(formulaID);
					if (fo == null) {
						orderError = true;
					}
				}
				if (flag != 3 || fo == null) {
					fo = new BudgetFormulaBo();
					list.add(fo);
				}
				fo.setFormulaID(rs.getInt("formula_Id"));
				fo.setTabID(rs.getInt("tab_id"));
				fo.setFormulaName(rs.getString("formulaName"));
				fo.setFormulaType(rs.getInt("formulaType"));
				xml = rs.getString("extAttr");
				readFormulaExAttr(fo, xml);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// 解析XML属性，给公式赋值
	private void readFormulaExAttr(BudgetFormulaBo fo, String xml) {
		try {
			if (fo == null || xml == null || xml.trim().length() <= 5) {
				return;
			} else {
				Document doc = PubFunc.generateDom(xml);
				String xpath = "//formula";
				XPath xpath_ = XPath.newInstance(xpath);
				Element ele = (Element) xpath_.selectSingleNode(doc);
				if (ele != null) {
					if (ele.getChild("rowcolflag") != null) {
						String tmp = ele.getChild("rowcolflag").getTextTrim();
						if (tmp.length() > 0) {
							fo.rowColFlag = Integer.parseInt(tmp);
						}
					}
					if (ele.getChild("colrange") != null) {
						fo.colRange = ele.getChild("colrange").getTextTrim();
					}
					if (ele.getChild("rowrange") != null) {
						fo.rowRange = ele.getChild("rowrange").getTextTrim();
					}
					if (ele.getChild("content") != null) {
						fo.expr = ele.getChild("content").getTextTrim();
					}
					if (ele.getChild("tj_where") != null) {
						fo.statRange = ele.getChild("tj_where").getTextTrim();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return;

	}

	public String getLastErrorMsg() {
		return lastErrorMsg;
	}

	// 取名册表号
	public int getMCTabId() {
		// tabType = 2
		if (mcTabId == -1) {
			mcTabId = getTabId(2);
		}
		return mcTabId;
	}

	// 是否名册表
	public boolean isMCTab(int tabId) {
		return tabId == getMCTabId();
	}

	// 取表名
	public String getTableName(int tabID) {
		if (isMCTab(tabID)) {
			return new String("SC01");
		} else if (isYgjhTab(tabID)) {
			return new String("SC02");
		} else if (isZETab(tabID)) {
			return this.getRes().getSetYSZE();
		} else
			return new String("SC03");
	}

	// 是否用工计划表
	public boolean isYgjhTab(int tabId) {
		return tabId == getYgjhTabId();
	}

	// 是否总额表
	public boolean isZETab(int tabId) {
		// tabType = 1
		if (zeTabId == -1) {
			zeTabId = getTabId(1);
		}
		return zeTabId == tabId;
	}

	// 取用工计划表号
	public int getYgjhTabId() {
		// tabType = 3
		if (ygjhTabId == -1) {
			ygjhTabId = getTabId(3);
		}
		return ygjhTabId;
	}

	/**
	 * 取表号
	 * 
	 * @param tabType:
	 *            1,2,3,4=总额，名册，用工计划，一般预算表 总额，名册，用工计划 只有一个表，一般预算表有多个，没意义
	 */
	private int getTabId(int tabType) {
		int id = -1;
		ContentDAO dao = new ContentDAO(con);
		// tab_type:
		StringBuffer sb = new StringBuffer(
				"select tab_id, validFlag from gz_budget_tab where tab_type=");
		sb.append(tabType);
		sb.append(" order by validFlag");
		RowSet rs = null;
		try {
			rs = dao.search(sb.toString());
			while (rs.next()) {
				id = rs.getInt("tab_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	// 取开始月份，通过预算索引号
	public int getStartMonth() {
		if (startMonth == 0) {
			ContentDAO dao = new ContentDAO(con);
			StringBuffer sb = new StringBuffer(
					"select firstMonth, SPFlag from gz_budget_index where budget_id=");
			sb.append(budgetIdx);
			RowSet rs = null;
			try {
				rs = dao.search(sb.toString());
				while (rs.next()) {
					startMonth = rs.getInt("firstMonth");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return startMonth;
	}

	public String getBudgetUnit() {
		return budgetUnit;
	}

	public void setBudgetUnit(String budgetUnit) {
		this.budgetUnit = budgetUnit;
		this.startMonth = 0;
		this.baseWhere = null;
	}

	public int getBudgetIdx() {
		return budgetIdx;
	}

	public void setBudgetIdx(int budgetIdx) {
		this.budgetIdx = budgetIdx;
		this.startMonth = 0;
		this.baseWhere = null;
	}

	public BudgetFormulaResBo getRes() {
		if (res == null) {
			res = new BudgetFormulaResBo(this.con, this.userView);
		}
		return res;
	}

	public ArrayList getList() {
		return list;
	}

	private String getBaseWhere(String tab, int tabid) {
		if (baseWhere == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("(#B0110='");
			sb.append(getBudgetUnit());
			sb.append("' AND #budget_id=");
			sb.append(getBudgetIdx());
			sb.append(")");
			baseWhere = sb.toString();
		}
		// 总额表特殊
		String str = baseWhere;
		if (isZETab(tabid)) {
			str = PubFunc.replace(str, "budget_id", getRes().getSysMap().get(
					"ysze_idx_menu").toString());
		}
		if (tab == null || "".equals(tab)) {
			str = str.replaceAll("#", "");
			if (tabid > 0 && !isZETab(tabid))
				str = "(" + str + " and tab_id=" + tabid + ")";
		} else {
			str = str.replace("#", tab + ".");
			if (tabid > 0)
				str = "(" + str + " and " + tab + ".tab_id=" + tabid + ")";
		}
		return str;
	}

	/**
	 * 名册的非合计项计算条件
	 * 
	 * @param tab
	 * @return
	 */
	private String getBaseWhereMCNoSum(String tab) {
		StringBuffer buffer = new StringBuffer(getBaseWhere(tab, -1));
		buffer.append(" and ");
		if (tab != null && !"".equals(tab)) {
			buffer.append(tab + ".");
		}
		buffer.append(getRes().mcSumField);
		buffer.append("<>'");
		buffer.append(getRes().mcSumValue);
		buffer.append("'");

		// 2013-11-20 WJH, 没有voFlag<>-1 的记录了。 endmonth=0，特别放到名册合计，其他不放
		// buffer.append(" and (voFlag is null or voFlag<>-1)");
		
		return buffer.toString();
	}

	public String[] getDbList() {
		if (dbList == null) {
			String dbs = (String) getRes().getSysMap().get("dblist");
			dbList = dbs.split(",");
		}
		return dbList;
	}
}
