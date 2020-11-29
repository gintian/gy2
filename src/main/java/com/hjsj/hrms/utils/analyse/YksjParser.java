package com.hjsj.hrms.utils.analyse;

import com.hjsj.hrms.businessobject.general.template.TemplateTableParamBo;
import com.hjsj.hrms.businessobject.gz.templateset.SalaryStandardBo;
import com.hjsj.hrms.businessobject.kq.interfaces.KqAppInterface;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.H2JdbcUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 查询条件解析器
 * 
 * @author zdb、zhm 使用方法：
 * 
 */
public class YksjParser implements IParserConstant {
	private static final Logger log = LoggerFactory.getLogger(YksjParser.class);
	private String codeset = "";
	private int flg;  // 1.是高级花名册 2.是报表变量  3.条件过滤  4.人事异动计算  5.薪资计算
	private String existWhereText="";  //创建临时表，塞入数据时如果 此变量中有值则调用 exist函数限定数据范围，否则还是照旧调用in函数
	private String whereText = "";
	private String targetFieldDataType;
	private ContentDAO dao;
	/** 目标指标长度 */
	private String targetField;
	private String targetTable;
	private int targetFieldLen = 50;
	private int targetFieldDecimal = 0;
	/** 主要用于临时变量中套用临时变量 */
	private String StdTmpTable = "";
	
	private String StdTmpTable_where=""; //分组汇总 （当前列表的 数据范围）
	
	/* 年月次 */
	private YearMonthCount ymc;
	private boolean isYearMonthCount = false;

	// 解析结果
	private String result;
	private Connection con;

	// private ArrayList FCASESQLS = new ArrayList();// 解析后的结果
	// 解析结果(查询条件)//Delphi=FSQL

	private StringBuffer SQL = new StringBuffer();
	private String ResultString = "";
	private String resultDataType="";  //返回 运行公式的结果类型

	public String getResultDataType() {
		return resultDataType;
	}

	public void setResultDataType(String resultDataType) {
		this.resultDataType = resultDataType;
	}
	
	/** 解析结果中，指标前是否加子集名 */
	private boolean addTableName=false;

	// 用于处理select，get特殊函数
	private int CurFuncNum = 0;

	private String VarName = "";

	private FieldItem Field = null;
	private FieldSet FieldSet = null;

	/** 语法分析器属性列表(前提条件) */
	private UserView userView; // 用户信息及用户权限信息// 解析器调用者信息

	private ArrayList fieldItems = null;
	/** 指标集合(数据范围) */

	private int ModeFlag = forNormal;// 查询模式是Normal还是Search,用来标识是单表简单查询还是复杂查询

	private int VarType = 0;// 变量类型

	private int InfoGroupFlag = forPerson;// 目标信息组类型

	private String DbPre = "USR"; // 应用库前缀

	private String TempTableName = "Ht"; // 临时表名称

	private int DBType = com.hrms.hjsj.sys.Constant.MSSQL;// 数据库类型标记 MSSQL =
															// 1, ORACEL = 2,
															// DB2 = 3;

	/** 语法分析器属性列表(语法分析部分) */
	private boolean isVerify = false; // 是否进行语法校验,不参与真正计算
	private boolean bVerify = false; // 语法效验标识

	private String FSource;// 全局:表达式原字符串

	private String strError;// 错误信息

	private boolean FError = false;// 错误标志

	private ArrayList UsedSets = new ArrayList();// 所用到的子集编号setid

	private HashMap mapUsedFieldItems = new HashMap();     // 所用到的指标代号
	private HashMap bracketsFieldMap=new HashMap();    // 中括号的指标
	private HashMap bracketsFieldValueMap=new HashMap(); //中括号指标赋予的值
	// private ArrayList UsedFields = new ArrayList();
	private ArrayList SQLS = new ArrayList();//

	private ArrayList FCTONSQLS = new ArrayList();// 代码转名称函数用到的Sql串
	/** 执行标准或代码调整用到的SQL串 */
	private ArrayList FSTDSQLS = new ArrayList();

	private int nFSourceLen = 0; // 要分析的字符串长度

	private int nCurPos = 0; // 因子累加器

	private int tok;

	private String token; // 一个因子

	// 因子数据类型 ( DELIMITER=1 分隔符类型 FIELDITEM=2 参数类型
	// FUNC=3 函数类型 QUOTE=4 引用类型 )
	private int token_type;
//	private boolean isFIELDITEM2=false;   //是否是中括号中的指标
	/** 代码调整计数器 */
	private int nCodeAddTime = 0;
	/** 除数标识 */
	private boolean bDivFlag = false;
	/** 标准表所用的指标列表 */
	private ArrayList StdFieldList = new ArrayList();

	/** 是否建临时表 */
	private boolean isTempTable = true;

	/** 取得公式涉及到的指标信息列表 支持单表操作 * */
	private boolean isSingleTalbe = true;
	/**
	 * 执行 对条件过滤的run方法：run_Where( 是否支持临时变量
	 */
	private boolean isSupportVar = false;
	private String  func_module_sql="";   //功能模块的临时变量sql 
    private ArrayList varList=new ArrayList();
	
	/** 是否调用统计函数计算多个临时变量 */
	private boolean  isStatMultipleVar=false; 
	/** 统计函数计算涉及的临时变量列表 */
	private ArrayList    statVarList=new ArrayList();
	
	private HashMap      gz_stdFieldMap=new HashMap();  //薪资标准函数涉及到的指标  数据联动中用到
	
	/**是否是取部门值参数 */
	private boolean isGetB0110Param=false;
	private ArrayList condItemList=new ArrayList();
	private ArrayList condItemList2=new ArrayList();
	/**是否是取岗位值参数 */
	private boolean isGetK01a1Param=false;
	private ArrayList condItemK01a1List=new ArrayList();
	private ArrayList condItemK01a1List2=new ArrayList();
	
	/**是否处于 IN 符号内 */
	private boolean isInFunc=false;  
	
	private boolean isFuncPROCEDURE=false; //是否是执行存储过程
	private String  PROCEDURE_NAME="";     //存储过程名
	
	private boolean isFuncSelectCondition=false;  //是否是统计函数定义的条件,解决条件中定义了函数造成SQL指标未明确定义列问题   2016-3-21,
	private boolean hasVarSelectCondition=false;  //统计条件是否包含临时变量  20171017
	
	/**取专项附加值需要传入存储过程的参数*/
	//zxfj_gz_tab:表名  | zxfj_sql_filter:薪资表待计算的人员范围  | zxfj_target_item:薪资表待写入专项额的目标指标
	//zxfj_tax_date_item:计税时间指标  | zxfj_id薪资表id
	private HashMap<String, String> zxfj_propertyMap = new HashMap<String, String>();
	
	private String dataBaseType = "";//传入的数据库类型，h2数据库个别函数需要特殊处理，如：今天
	
	//统计表单子集函数对应的条件中的指标
	private boolean isSelectSubSet=false;
	//统计表单子集函数条件的指标集合，需传出去，让H2数据库根据这里的指标集合建对应的字段 
	private ArrayList subSetConditionFieldItemList=new ArrayList();
	
	/**
	 * 解析器构造函数
	 * 
	 * @param userView
	 *            当前登录的用户信息
	 * @param fieldItemList
	 *            指标集合
	 * @param ModeFlag
	 *            查询对象类型forNormal，forSearch
	 * @param VarType
	 *            参数类型
	 * @param InfoGroup
	 *            目标信息组类型 forPerson， forDepartment， forUnit， forParty，
	 *            forWorkParty
	 * @param strTempTableName
	 *            临时表表名
	 * @param dbPre
	 *            库前缀
	 */
	public YksjParser(UserView userView, ArrayList fieldItemList, int modeFlag,
			int varType, int infoGroup, String strTempTableName, String dbPre) {

		setUserView(userView);// 得到用户信息及权限

		setFieldItems(fieldItemList);// 得到指标列表(数据范围)

		setModeFlag(modeFlag);// 设置查询模式是Normal还是Search

		setInfoGroupFlag(infoGroup); // 人员 单位 部门 职位

		setVarType(varType);// 变量类型(整数 浮点 字符 日期 逻辑)

		setDbPre(dbPre); // 库前缀

		// setVariableType(varType);

		if (strTempTableName == null || strTempTableName.trim().length() < 1) {
			strTempTableName = "Ht";
		}
		setTempTableName(strTempTableName); // 设置临时表名称

		setDBType(Sql_switcher.searchDbServer());// 设置数据库类型
		//创建临时表时，获取用户名去用户名中的“.”和“@” chenxg 2016-08-15
		String usrName = userView.getUserName();
		if(usrName.indexOf(".") > -1)
		    usrName = usrName.replace(".", "");
		
		if(usrName.indexOf("@") > -1)
            usrName = usrName.replace("@", "");
        
		if (this.DBType == Constant.ORACEL)
			isTempTable = false;
		if (DBType == Constant.MSSQL && isTempTable) {
			TempTableName = "##temp_" + usrName;		    
		} else
			TempTableName = "temp_" + usrName;
		/** 年月次 */
		Date sysdate = new Date();
		this.ymc = new YearMonthCount(DateUtils.getYear(sysdate), DateUtils
				.getMonth(sysdate), DateUtils.getDay(sysdate), 1);

	}

	private boolean Func_TodayPart(int FuncNum, RetValue retValue)
			throws GeneralException {
		if (!Get_Token())
			return false;
		if (tok == S_LPARENTHESIS) {
			if (!Get_Token())
				return false;
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			if (!Get_Token())
				return false;
		}
		String SystemTime = com.hrms.frame.utility.DateStyle.getSystemTime()
				.substring(0, 10);
		retValue.setValue("'" + SystemTime + "'");
		retValue.setValueType(DATEVALUE);

		switch (FuncNum) {
		case FUNCTODAY: {
			if(Sql_switcher.searchDbServer() == 1 && "h2".equalsIgnoreCase(dataBaseType)) {
				SQL = SQL.append("GETDATE()");
			}else
				SQL = SQL.append(Sql_switcher.today());
			break;
		}
		case FUNCTOWEEK: {
			SQL = SQL.append(Sql_switcher.toWeek());
			break;
		}
		case FUNCTOMONTH: {
			SQL = SQL.append(Sql_switcher.toMonth());
			break;
		}
		case FUNCTOQUARTER: {
			SQL = SQL.append(Sql_switcher.toQuarter());
			break;
		}
		case FUNCTOYEAR: {
			SQL = SQL.append(Sql_switcher.toYear());
			break;
		}
		}
		switch (FuncNum) {
		case FUNCTOWEEK: {
			retValue.setValue(new Integer(1));
			retValue.setValueType(INT);
			break;
		}
		case FUNCTOMONTH: {
			retValue.setValue(new Integer(SystemTime.substring(5, 7)));
			retValue.setValueType(INT);
			break;
		}
		case FUNCTOQUARTER: {
			int nq = Integer.parseInt(SystemTime.substring(5, 7)) / 3 + 1;
			retValue.setValue(new Integer(nq));// Integer.valueOf(SystemTime.substring(4,6)).intValue()
			// / 3);
			retValue.setValueType(INT);
			break;
		}
		case FUNCTOYEAR: {
			retValue.setValue(new Integer(SystemTime.substring(0, 4)));
			retValue.setValueType(INT);
			break;
		}
		}
		return true;
	}

	private boolean Func_AppDate(RetValue retValue) throws GeneralException {
		if (!Get_Token())
			return false;
		if (tok == S_LPARENTHESIS) {
			if (!Get_Token())
				return false;
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			if (!Get_Token())
				return false;
		}
		String strDate = "";
		if (userView == null) {
			strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
					.substring(0, 10);
		} else {
			if ("HrpWarn".equalsIgnoreCase(userView.getUserName())) // 预警建的虚拟userview
				strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
						.substring(0, 10);
			else
				strDate = com.hrms.hjsj.sys.ConstantParamter
						.getAppdate(userView.getUserName());
		}
		retValue.setValue("'" + strDate + "'");
		retValue.setValueType(DATEVALUE);
		SQL = SQL.append(com.hrms.hjsj.utils.Sql_switcher.dateValue(strDate));

		return true;
	}

	private boolean Func_CalcAge(int FuncNum, RetValue retValue)
			throws GeneralException, SQLException {

		int nYear1, nMonth1, nDay1;
		int nYear2, nMonth2, nDay2;
		String str, str1;
		str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = this.SQL.toString();
		if (!retValue.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		// DecodeDate(retValue,nYear1,nMonth1,nDay1);
		nYear1 = retValue.getYearOfDate();
		nMonth1 = retValue.getMonthOfDate();
		nDay1 = retValue.getDayOfDate();

		// nYear2 = Integer.parseInt(Sql_switcher.sysYear());
		// nMonth2 = Integer.parseInt(Sql_switcher.sysMonth());
		// nDay2 = Integer.parseInt(Sql_switcher.sysDay());

		String strDate = "";
		if (FuncNum == FUNCAPPAGE) {
			if (userView == null) {
				strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
						.substring(0, 10);
			} else {
				if ("HrpWarn".equalsIgnoreCase(userView.getUserName())) // 预警建的虚拟userview
					strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
							.substring(0, 10);
				else
					strDate = com.hrms.hjsj.sys.ConstantParamter
							.getAppdate(userView.getUserName());
			}

		} else {
			strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
					.substring(0, 10);
		}
		nYear2 = Integer.parseInt(strDate.substring(0, 4));
		nMonth2 = Integer.parseInt(strDate.substring(5, 7));
		nDay2 = Integer.parseInt(strDate.substring(8, 10));

		// str2 = Sql_switcher.dateValue(strDate);

		// System.out.println(retValue.getValue());
		// System.out.println(strDate);

		retValue.setValueType(INT);
		switch (FuncNum) {
		case FUNCAGE:
		case FUNCAPPAGE: {
			int nTemp = ((nYear2 - nYear1) * 10000 + (nMonth2 - nMonth1) * 100 + (nDay2 - nDay1)) / 10000;
			retValue.setValue(new Integer(nTemp));
			SQL.setLength(0);
			SQL.append(str);
			SQL.append(Sql_switcher.toInt(Sql_switcher.age(str1)));
			break;
		}
		case FUNCWORKAGE:
		case FUNCAPPWORKAGE: {
			SQL.setLength(0);
			SQL.append(str);
			SQL.append(Sql_switcher.toInt(Sql_switcher.workAge((str1))));
			retValue.setValue(new Integer(nYear2 - nYear1 + 1));
			break;
		}
		case FUNCMONTHAGE:
		case FUNCAPPMONTHAGE: {
			SQL.setLength(0);
			SQL.append(str);
			SQL.append(Sql_switcher.toInt(Sql_switcher.appMonthAge((str1))));
			retValue.setValue(new Integer(
					((nYear2 - nYear1) * 100 + (nMonth2 - nMonth1)) / 100));
			break;
		}
		}
		return Get_Token();
	}

	private boolean Func_DatePart(int DatePart, RetValue retValue)
			throws GeneralException, SQLException {
		String str1 = "", str2 = "";// str = "",
		// str = WhereCond.toString();
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		str2 = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		// System.out.println(retValue.values() + ":" +
		// retValue.getValueType());
		if (!retValue.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		// System.out.println(this.FSource.charAt(nCurPos));
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		int nYear1 = retValue.getYearOfDate();
		int nMonth1 = retValue.getMonthOfDate();
		int nDay1 = retValue.getDayOfDate();
		switch (DatePart) {
			case FUNCYEAR: {
				SQL.setLength(0);
				SQL.append(str2);
				SQL.append(Sql_switcher.year(str1));
				break;
			}
			case FUNCMONTH: {
				SQL.setLength(0);
				SQL.append(str2);
				SQL.append(Sql_switcher.month(str1));
				break;
			}
			case FUNCDAY: {
				SQL.setLength(0);
				SQL.append(str2);
				SQL.append(Sql_switcher.day(str1));
				break;
			}
			case FUNCQUARTER: {
				SQL.setLength(0);
				SQL.append(str2);
				String month_ = Sql_switcher.month(str1);
				if("h2".equalsIgnoreCase(dataBaseType)) {
					SQL.append(" QUARTER(" + str1 + ")");
				}else {
					SQL.append(Sql_switcher.quarter(str1));
				}
				break;
			}
			case FUNCWEEK: {
				SQL.setLength(0);
				SQL.append(str2);
				if("h2".equalsIgnoreCase(dataBaseType)) {
					SQL.append(" WEEK(" + str1 + ")");
				}else {
					SQL.append(Sql_switcher.week(str1));
				}
				break;
			}
			case FUNCWEEKDAY: {
				SQL.setLength(0);
				SQL.append(str2);
				if("h2".equalsIgnoreCase(dataBaseType)) {
					SQL.append(" DAY_OF_WEEK(" + str1 + ")-1");
				}else {
					SQL.append(Sql_switcher.weekDay(str1));
				}
				// System.out.println(SQL.toString());
				break;
			}
		}

		switch (DatePart) {
			case FUNCYEAR: {
				retValue.setValue(new Integer(nYear1));
				retValue.setValueType(INT);
				break;
			}
			case FUNCMONTH: {
				retValue.setValue(new Integer(nMonth1));
				retValue.setValueType(INT);
				break;
			}
			case FUNCDAY: {
				retValue.setValue(new Integer(nDay1));
				retValue.setValueType(INT);
				break;
			}
			case FUNCQUARTER: {
	
				retValue.setValue(new Integer(nMonth1 / 3 + 1));
				retValue.setValueType(INT);
				break;
			}
			case FUNCWEEK: {// 本年第xx周
				// DateFormat df = DateFormat.getDateInstance();
				// Date d =
				// df.parse(((String)retValue.getValue()).substring(1,11));
				Calendar c = Calendar.getInstance();
				c.set(nYear1, nMonth1, nDay1);
	
				retValue.setValue(new Integer(c.get(Calendar.WEEK_OF_YEAR)));
				retValue.setValueType(INT);
				break;
			}
			case FUNCWEEKDAY: {// 礼拜xx
				Calendar c = Calendar.getInstance();
				c.set(nYear1, nMonth1, nDay1);
				retValue.setValue(new Integer(c.get(Calendar.DAY_OF_WEEK)));
				retValue.setValueType(INT);
				break;
			}
		}
		return Get_Token();
	}

	/**
	 * 分组汇总 分组汇总(岗位工资,总和,单位名称,2,当前列表,部门="010101" 且 薪级="01")
	 */

	// 取得表达式中的各参数值
	private boolean getParamValue(LazyDynaBean abean, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		SQL.setLength(0); // 条件
		if (!Get_Token())
			return false;

		boolean _bDivFlag = bDivFlag; // 除数标示
		bDivFlag = false;
		HashMap usedFieldMap =(HashMap)this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems.clear();
		if (!level0(retValue)) {
			return false;
		}
		bDivFlag = _bDivFlag;
		abean.set("str1", this.SQL.toString());
		abean.set("str1FieldsMap", this.mapUsedFieldItems); // 如果为非当前列表 有效
	
		String _str=",";
		Iterator it = usedFieldMap.values().iterator();
		while (it.hasNext()) {
			FieldItem usedTemp = (FieldItem) it.next();
			_str+=usedTemp.getItemid().toLowerCase()+",";
		}
		
		it = mapUsedFieldItems.values().iterator();
		while (it.hasNext()) {
			FieldItem usedTemp = (FieldItem) it.next();
			if(_str.indexOf(","+usedTemp.getItemid().toLowerCase()+",")==-1)
				usedFieldMap.put(usedTemp.getItemid(),usedTemp);
		} 
		this.mapUsedFieldItems = usedFieldMap;

		/*
		 * if (token_type != FIELDITEM) { Putback(); SError(E_MUSTBEFIELDITEM);
		 * return false; } abean.set("str1",Field.getItemid()); if
		 * (!Get_Token()) return false;
		 */
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (tok != S_COUNT && tok != S_MAX && tok != S_MIN && tok != S_SUM
				&& tok != S_AVG) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		abean.set("str2", this.token);
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM && token_type != ODDVAR ) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		/*
		if (Field.getCodesetid().equals("0")) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}*/
		abean.set("str3", Field.getItemid());
		if (!Get_Token())
			return false;
		if (tok == S_RPARENTHESIS)
			return true;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		abean.set("str4", this.token);
		if (!Get_Token())
			return false;
		if (tok == S_RPARENTHESIS)
			return true;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (tok != S_CURRENTTABLE && tok != S_CURRENTA01) {
			Putback();
			SError(E_SYNTAX);
			return false;
		}
		abean.set("str5", this.token);

		if (!Get_Token())
			return false;
		if (tok == S_RPARENTHESIS)
			return true;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		SQL.setLength(0); // 条件
		if (!Get_Token()) {
			return false;
		}
		
		
		usedFieldMap=(HashMap)this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems.clear();
		
		if (!level0(retValue)) {
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		abean.set("str6", SQL.toString()); 
		
		if(this.mapUsedFieldItems.size()>0)
		{
			//abean.set("str1FieldsMap", this.mapUsedFieldItems);
			HashMap map=(HashMap)abean.get("str1FieldsMap");
			it = this.mapUsedFieldItems.values().iterator();
			while (it.hasNext()) {
				FieldItem usedTemp = (FieldItem) it.next();
				map.put(usedTemp.getItemid(),usedTemp);
			}
			abean.set("str1FieldsMap",map);
		}
		
		
		_str=",";
		it = usedFieldMap.values().iterator();
		while (it.hasNext()) {
			FieldItem usedTemp = (FieldItem) it.next();
			_str+=usedTemp.getItemid().toLowerCase()+",";
		}
		it = mapUsedFieldItems.values().iterator();
		while (it.hasNext()) {
			FieldItem usedTemp = (FieldItem) it.next();
			if(_str.indexOf(","+usedTemp.getItemid().toLowerCase()+",")==-1)
				usedFieldMap.put(usedTemp.getItemid(),usedTemp);
		} 
		this.mapUsedFieldItems = usedFieldMap;
		
		
		
		
		
		return true;
	}

	
	/**
	 * 建立分组汇总计算用的临时表 str1:汇总指标 str2:汇总方式 str3:分组指标 str4:分组级数 str5:范围 str6:条件
	 */
	private boolean createGroupStatTempTable(String str1, String str2,
			String str3, String str4, String str5, String str6,
			HashMap str1FieldsMap) {
		try {
			String tempTableName ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum; // userView.getUserName() + "GroupTable";
			String tempTableName2 ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum+"2"; // userView.getUserName() + "GroupTable2";
			DbWizard dbWizard = new DbWizard(this.con);
			ContentDAO dao = new ContentDAO(this.con);
			FieldItem fieldItem = null;

			Table table = new Table(tempTableName, tempTableName);

			Table table2 = new Table(tempTableName2, tempTableName2);
			if (this.isTempTable) {
				table.setBTemporary(true);
				table.setBTemporary(true);
				table2.setBTemporary(true);
			}
			if (DBType == Constant.MSSQL && isTempTable) {
				tempTableName = "##" + tempTableName;
				tempTableName2 = "##" + tempTableName2;
			}

			HashMap map = new HashMap();

			fieldItem = new FieldItem("I9999", "I9999");
			fieldItem.setItemlength(10);
			fieldItem.setItemtype("N");
			table.addField(fieldItem.cloneField());
			table2.addField(fieldItem.cloneField());

			
			if(this.InfoGroupFlag == YksjParser.forPerson)
			{
			
				fieldItem = new FieldItem("NBASE", "NBASE");
				fieldItem.setItemtype("A");
				fieldItem.setItemlength(8);
	
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());
	 
				
				fieldItem = new FieldItem("A0100", "A0100");
				fieldItem.setItemtype("A");
				fieldItem.setItemlength(8);
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());
			}
			else if(this.InfoGroupFlag == YksjParser.forUnit)
			{
				
				fieldItem = new FieldItem("B0110", "B0110");
				fieldItem.setItemtype("A");
				fieldItem.setItemlength(100);
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());
				
			}
			if(this.InfoGroupFlag == YksjParser.forPosition)
			{
				fieldItem = new FieldItem("E01A1", "E01A1");
				fieldItem.setItemtype("A");
				fieldItem.setItemlength(100);
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());
			}
			
			
			
			// 分组指标
			fieldItem = new FieldItem(str3, str3);
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(30);
			table.addField(fieldItem.cloneField());
			table2.addField(fieldItem.cloneField());
			map.put(str3, "1");

			ArrayList tableOtherFieldList = new ArrayList();
			fieldItem = new FieldItem("layer", "layer");
			fieldItem.setItemtype("N");
			fieldItem.setDecimalwidth(0);
			table.addField(fieldItem.cloneField());
			tableOtherFieldList.add(fieldItem.cloneItem());
			// 分组指标
			fieldItem = new FieldItem("a_" + str3, "a_" + str3);
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(30);
			table.addField(fieldItem.cloneField());
			tableOtherFieldList.add(fieldItem.cloneItem());
			// 汇总指标
			java.util.Set set = str1FieldsMap.keySet();
			for (Iterator t = set.iterator(); t.hasNext();) {
				String id = (String) t.next();
				FieldItem _item = (FieldItem) str1FieldsMap.get(id);
				if(_item.getItemid().equalsIgnoreCase(str3))
					continue;
				table.addField(_item);
			}
			/*
			 * if(map.get(str1)==null) { String a_itemid=str1;
			 * if(str1.length()>2&&(str1.substring(str1.length()-2).equals("_1")||str1.substring(str1.length()-2).equals("_2")))
			 * a_itemid=str1.substring(0,str1.length()-2);
			 * fieldItem=(FieldItem)DataDictionary.getFieldItem(a_itemid.toLowerCase()).cloneItem();
			 * fieldItem.setItemid(str1);
			 * table.addField(fieldItem.cloneField()); }
			 */

			if ("个数".equalsIgnoreCase(str2) || "人数".equalsIgnoreCase(str2)) {
				fieldItem = new FieldItem("goupValue", "goupValue");
				fieldItem.setItemtype("N");
				fieldItem.setDecimalwidth(0);
				table2.addField(fieldItem.cloneField());
			} else {
				fieldItem = new FieldItem("goupValue", "goupValue");
				fieldItem.setItemtype("N");
				fieldItem.setItemlength(15);
				fieldItem.setDecimalwidth(5);
				table2.addField(fieldItem.cloneField());
			}

			if ("当前列表".equalsIgnoreCase(str5)
					&& TempTableName.length() > 5
					&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")  || TempTableName
							.toLowerCase().endsWith("gzsp") )) {
				fieldItem = new FieldItem("a00z0", "a00z0");
				fieldItem.setItemtype("D");
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());

				fieldItem = new FieldItem("a00z1", "a00z1");
				fieldItem.setItemtype("N");
				fieldItem.setDecimalwidth(0);
				table.addField(fieldItem.cloneField());
				table2.addField(fieldItem.cloneField());
			}

		//	if (dbWizard.isExistTable(tempTableName, false)) 
			{
				dbWizard.dropTable(table);
			}
			if ("当前列表".equalsIgnoreCase(str5)) {
				if (StdTmpTable == null || StdTmpTable.trim().length() == 0)
					return true;
				StringBuffer strSQL = new StringBuffer("");
				if (DBType == Constant.ORACEL) {
					strSQL.setLength(0);
					strSQL.append("CREATE ");
					if (this.isTempTable)
						strSQL.append(" GLOBAL TEMPORARY ");
					strSQL.append(" TABLE  ").append(tempTableName);
					if (this.isTempTable)
						strSQL.append(" On Commit Preserve Rows ");
					strSQL.append(" AS SELECT *  FROM ").append(StdTmpTable);
				} else {
					strSQL.setLength(0);
					strSQL.append("SELECT * INTO ").append(tempTableName);
					strSQL.append(" FROM ").append(StdTmpTable);
				}
				boolean isWhere = false;
				/*
				 * if(!(whereText.equalsIgnoreCase("()"))&&whereText.trim().length()>0) { //
				 * sqlBuffer.append(" where a0100 in "+whereText);
				 * strSQL.append(" where "+whereText); isWhere=true; }
				 */
				 // 加上条件
		 		if (("q03".equalsIgnoreCase(StdTmpTable)|| "q05".equalsIgnoreCase(StdTmpTable))&&str6 != null && str6.trim().length() > 0)
				{ 
						strSQL.append(" where " + str6);

				}  
				if(StdTmpTable_where!=null&&StdTmpTable_where.trim().length()>0)
				{
					if(StdTmpTable_where.trim().toLowerCase().indexOf("where")==0)
						dbWizard.execute(strSQL.toString()+" "+StdTmpTable_where);
					else
						dbWizard.execute(strSQL.toString()+" where "+StdTmpTable_where);
				}
				else
					dbWizard.execute(strSQL.toString()+StdTmpTable_where);

				for (int i = 0; i < tableOtherFieldList.size(); i++) {
					strSQL.setLength(0);
					FieldItem item = (FieldItem) tableOtherFieldList.get(i);
					String sAdd = item.getItemid()
							+ " "
							+ Sql_switcher.getFieldType(item.getItemtype()
									.charAt(0), item.getItemlength(), item
									.getDecimalwidth());
					switch (DBType) {
					case Constant.MSSQL:
					case Constant.ORACEL:
						strSQL.append("alter table ");
						strSQL.append(tempTableName);
						strSQL.append(" add ");
						strSQL.append(sAdd);
						break;
					case Constant.DB2:
						strSQL.append("alter table ");
						strSQL.append(tempTableName);
						strSQL.append(" add column ");
						strSQL.append(sAdd);
						break;
					}
					dbWizard.execute(strSQL.toString());
				}
			} else {

				dbWizard.createTable(table);
			}

		//	if (dbWizard.isExistTable(tempTableName2, false)) 
			{
				dbWizard.dropTable(table2);
			}
			dbWizard.createTable(table2);

			impGroupStatTempTableData(str1, str2, str3, str4, str5, str6,
					str1FieldsMap);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 往临时表中写入数据 str1:汇总指标 str2:汇总方式 str3:分组指标 str4:分组级数 str5:范围 str6:条件
	 */
	public void impGroupStatTempTableData(String str1, String str2,
			String str3, String str4, String str5, String str6,
			HashMap str1FieldsMap) {
		try {
			String tempTableName ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum; // userView.getUserName() + "GroupTable";
			if (DBType == Constant.MSSQL && isTempTable)
				tempTableName = "##" + tempTableName;
			ContentDAO dao = new ContentDAO(this.con);
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("insert into " + tempTableName);
			// 人员表
			String table = "";
			boolean isWhere = false;
			if ("当前列表".equalsIgnoreCase(str5)) {
				/*
				 * sqlBuffer.append("(NBASE,A0100");
				 * 
				 * if(TempTableName.length()>8&&(TempTableName.toLowerCase().indexOf("salary")!=-1||TempTableName.toLowerCase().indexOf("sp_data")!=-1)) {
				 * sqlBuffer.append(",a00z0,a00z1"); sqlBuffer.append(") select
				 * "); sqlBuffer.append("nbase,A0100,a00z0,a00z1"); } else {
				 * sqlBuffer.append(") select ");
				 * 
				 * if(StdTmpTable.length()>8&&(StdTmpTable.toLowerCase().indexOf("salary")!=-1||StdTmpTable.toLowerCase().indexOf("sp_data")!=-1))
				 * sqlBuffer.append("nbase,A0100"); else
				 * sqlBuffer.append("basepre,A0100"); } sqlBuffer.append(" FROM
				 * "+StdTmpTable);
				 * if(!(whereText.equalsIgnoreCase("()"))&&whereText.trim().length()>0) { //
				 * sqlBuffer.append(" where a0100 in "+whereText);
				 * sqlBuffer.append(" where "+whereText); isWhere=true; }
				 * 
				 * if(this.ModeFlag==1&&isWhere) {
				 * if(StdTmpTable.length()>8&&(StdTmpTable.toLowerCase().indexOf("salary")!=-1||StdTmpTable.toLowerCase().indexOf("sp_data")!=-1))
				 * sqlBuffer.append(" and
				 * lower(nbase)='"+this.DbPre.toLowerCase()+"'"); else
				 * sqlBuffer.append(" and
				 * lower(basepre)='"+this.DbPre.toLowerCase()+"'"); }
				 */
				if (StdTmpTable == null || StdTmpTable.trim().length() == 0)
					return;
			} else {
				if (InfoGroupFlag == forPerson) {
					if (DbPre == null || DbPre.trim().length() == 0)
						return;
					sqlBuffer.append("(NBASE,A0100)");
					sqlBuffer.append(" select '" + DbPre + "'," + DbPre
							+ "A01.A0100  from ");
					table = this.DbPre + "A01";
					sqlBuffer.append(table);
					// 暂时跟cs保持一致，当前人员库指所有
					// if(!(whereText.equalsIgnoreCase("()"))&&whereText.trim().length()>0)
					// sqlBuffer.append(" where " + DbPre + "A01.A0100 in " +
					// whereText);
				}
				else if(this.InfoGroupFlag == YksjParser.forUnit)
				{
				
					sqlBuffer.append("(B0110)");
					sqlBuffer.append(" select B0110  from ");
					table ="B01";
					sqlBuffer.append(table);
				}
				else if(this.InfoGroupFlag == YksjParser.forPosition)
				{
				
					sqlBuffer.append("(E01A1)");
					sqlBuffer.append(" select E01A1  from ");
					table ="K01";
					sqlBuffer.append(table);
				}
				else
					return;
			}
			
			// 加上条件
		/*	if (str6 != null && str6.trim().length() > 0) 
			{
				if (isWhere)
					sqlBuffer.append(" AND " + str6);
				else
					sqlBuffer.append(" where " + str6);

			}*/
			if (!"当前列表".equalsIgnoreCase(str5))
				dao.update(sqlBuffer.toString());

			if ("当前列表".equalsIgnoreCase(str5)) {
				// 插入分组指标值
				/*
				 * sqlBuffer.setLength(0); sqlBuffer.append("update
				 * "+tempTableName+" set "+str3+"=(select "+str3+" from
				 * "+StdTmpTable);
				 * if(TempTableName.length()>8&&(TempTableName.toLowerCase().indexOf("salary")!=-1||TempTableName.toLowerCase().indexOf("sp_data")!=-1)) {
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".nbase="+tempTableName+".NBASE ");
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".a00z0="+tempTableName+".a00z0 ");
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".a00z1="+tempTableName+".a00z1 "); } else {
				 * if(StdTmpTable.length()>8&&(StdTmpTable.toLowerCase().indexOf("salary")!=-1||StdTmpTable.toLowerCase().indexOf("sp_data")!=-1))
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".NBASE="+tempTableName+".NBASE"); else
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".basepre="+tempTableName+".NBASE"); }
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".A0100="+tempTableName+".A0100 )");
				 * dao.update(sqlBuffer.toString());
				 * 
				 * 
				 * 
				 * //插入汇总指标值 java.util.Set set=str1FieldsMap.keySet();
				 * for(Iterator t=set.iterator();t.hasNext();) { String
				 * id=(String)t.next(); FieldItem
				 * _item=(FieldItem)str1FieldsMap.get(id);
				 * 
				 * String _itemid=_item.getItemid(); sqlBuffer.setLength(0);
				 * sqlBuffer.append("update "+tempTableName+" set
				 * "+_itemid+"=(select "+_itemid+" from "+StdTmpTable);
				 * if(TempTableName.length()>8&&(TempTableName.toLowerCase().indexOf("salary")!=-1||TempTableName.toLowerCase().indexOf("sp_data")!=-1)) {
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".nbase="+tempTableName+".NBASE");
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".a00z0="+tempTableName+".a00z0");
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".a00z1="+tempTableName+".a00z1"); } else {
				 * if(StdTmpTable.length()>8&&(StdTmpTable.toLowerCase().indexOf("salary")!=-1||StdTmpTable.toLowerCase().indexOf("sp_data")!=-1))
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".nbase="+tempTableName+".NBASE"); else
				 * sqlBuffer.append(" where
				 * "+StdTmpTable+".basepre="+tempTableName+".NBASE"); }
				 * sqlBuffer.append(" and
				 * "+StdTmpTable+".A0100="+tempTableName+".A0100 )");
				 * dao.update(sqlBuffer.toString()); }
				 * 
				 */

			} else {

				// 插入分组指标值
				FieldItem item = DataDictionary
						.getFieldItem(str3.toLowerCase());
                String tablename=DbPre+"A01";
                String tt_table="";
                String key="A0100";
                if(this.InfoGroupFlag == YksjParser.forUnit)
                {
                    tablename="B01";           
                    key="B0110";
                }
                else  if(this.InfoGroupFlag == YksjParser.forPosition)
                {
                    tablename="K01";             
                    key="E01A1";
                }                
                
				if (item!=null){	
				    
				    tt_table=DbPre + item.getFieldsetid();			
				    if(this.InfoGroupFlag == YksjParser.forUnit)
				    {		
				        tt_table=item.getFieldsetid();			
				    }
				    else  if(this.InfoGroupFlag == YksjParser.forPosition)
				    {			
				        tt_table=item.getFieldsetid();			
				    }	    
				    
				    if ("A01".equalsIgnoreCase(item.getFieldsetid())|| "B01".equalsIgnoreCase(item.getFieldsetid())|| "K01".equalsIgnoreCase(item.getFieldsetid())) {
				        sqlBuffer.setLength(0);
				        sqlBuffer.append("update " + tempTableName + " set " + str3
				                + "=(select " + str3 + " from " +tablename);
				        sqlBuffer.append(" where " + tempTableName + "."+key+"="
				                + tablename + "."+key+" )");
				        dao.update(sqlBuffer.toString());
				    } else {
				        //	String tt_table = DbPre + item.getFieldsetid();
				        sqlBuffer.setLength(0);
				        sqlBuffer.append("update " + tempTableName + " set " + str3
				                + "=(select c." + str3 + " from (");
				        sqlBuffer.append("select  a." + str3 + ",a."+key+" from "
				                + tt_table
				                + " a where a.i9999=(select max(b.i9999) from "
				                + tt_table + " b where a."+key+"=b."+key+")");
				        sqlBuffer.append(") c where " + tempTableName
				                + "."+key+"=c."+key+"  )");
				        dao.update(sqlBuffer.toString());
				    }
				}
				else {//可能是临时变量 不需要重新加载 
    				 if (!"当前列表".equalsIgnoreCase(str5)){//加载临时变量
    		                DbWizard dbw = new DbWizard(this.con);
    		                dbw.updateRecord(tempTableName,targetTable,
    		                        tempTableName+"."+key+"="+targetTable+"."+key,  
    		                        tempTableName+"."+str3+"="+targetTable+"."+str3,
    		                        "", "");
    				 }
		                
				    
				}
				// 插入汇总指标值
				java.util.Set set = str1FieldsMap.keySet();
				for (Iterator t = set.iterator(); t.hasNext();) {
					String id = (String) t.next();
					FieldItem _item = (FieldItem) str1FieldsMap.get(id);

					String _itemid = _item.getItemid();
					if(_item.getItemid().equalsIgnoreCase(str3))
						continue;
					// item=DataDictionary.getFieldItem(str1.toLowerCase());
					
					if ("A01".equalsIgnoreCase(_item.getFieldsetid())|| "B01".equalsIgnoreCase(_item.getFieldsetid())|| "K01".equalsIgnoreCase(_item.getFieldsetid())) {
						if(this.InfoGroupFlag == YksjParser.forUnit) 
								tablename="B01";
						else  if(this.InfoGroupFlag == YksjParser.forPosition)
								tablename="K01"; 
						sqlBuffer.setLength(0);
						sqlBuffer.append("update " + tempTableName + " set "
								+ _itemid + "=(select " + _itemid + " from "
								+tablename);
						sqlBuffer.append(" where " + tempTableName + "."+key+"="
								+ tablename + "."+key+" )");
						dao.update(sqlBuffer.toString());
					} else if(_item.getVarible()==0&&_item.getFieldsetid()!=null&&_item.getFieldsetid().trim().length()>0) {
						
						tt_table=DbPre + _item.getFieldsetid(); 
						if(this.InfoGroupFlag == YksjParser.forUnit)
							tt_table=_item.getFieldsetid();
						else  if(this.InfoGroupFlag == YksjParser.forPosition) 
							tt_table=_item.getFieldsetid(); 
						
					//	String tt_table = DbPre + item.getFieldsetid();
						sqlBuffer.setLength(0);
						sqlBuffer
								.append("update " + tempTableName + " set "
										+ _itemid + "=(select c." + _itemid
										+ " from (");
						sqlBuffer.append("select  a." + _itemid
								+ ",a."+key+" from " + tt_table
								+ " a where a.i9999=(select max(b.i9999) from "
								+ tt_table + " b where a."+key+"=b."+key+")");
						sqlBuffer.append(") c where " + tempTableName
								+ "."+key+"=c."+key+"  )");
						dao.update(sqlBuffer.toString());
					}
				}

			}

			String a_itemid = str3;
			if (str3.length() > 2
					&& ("_1".equals(str3.substring(str3.length() - 2)) || "_2".equals(str3
							.substring(str3.length() - 2))))
				a_itemid = str3.substring(0, str3.length() - 2);
			FieldItem item = DataDictionary
					.getFieldItem(a_itemid.toLowerCase());
			if(item!=null)
			{
				String codeTable = "";
				if ("UN".equalsIgnoreCase(item.getCodesetid())
						|| "UM".equalsIgnoreCase(item.getCodesetid())
						|| "@K".equalsIgnoreCase(item.getCodesetid())) {
					dao
							.update("update "
									+ tempTableName
									+ " set layer=(select layer from organization where organization.codeitemid="
									+ tempTableName + "." + str3 + ")");
					codeTable = "organization";
				} else {
					dao
							.update("update "
									+ tempTableName
									+ " set layer=(select layer from codeitem where codeitem.codeitemid="
									+ tempTableName + "." + str3
									+ " and codeitem.codesetid='"
									+ item.getCodesetid() + "'  )");
					codeTable = "codeitem";
				}
			 
			// "a_"+str3
				if ("0".equals(str4))
					dao.update("update " + tempTableName + " set a_" + str3 + "="
							+ str3);
				else {
					dao.update("update " + tempTableName + " set a_" + str3 + "="
							+ str3 + " where layer<=" + str4);
	
					String sql = "select codeitemid from ";
					if ("UN".equalsIgnoreCase(item.getCodesetid())
							|| "UM".equalsIgnoreCase(item.getCodesetid())
							|| "@K".equalsIgnoreCase(item.getCodesetid()))
						sql += " organization ";
					else
						sql += " codeitem ";
					sql += " where codesetid='" + item.getCodesetid()
							+ "' and layer=" + str4;
					RowSet rowSet = dao.search(sql);
					if (rowSet.next()) {
						String codeitemid = rowSet.getString(1);
						if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
							dao.update("update " + tempTableName + " set a_" + str3
									+ "=substring(" + str3 + ",0,"
									+ (codeitemid.length() + 1) + ") where layer>"
									+ str4);
						} else {
							dao.update("update " + tempTableName + " set a_"
											+ str3 + "=substr(" + str3 + ",0,"
											+ codeitemid.length()
											+ ") where layer>" + str4);
						}
					}
					rowSet.close();
				}
			
			}
			else //if (str5.equalsIgnoreCase("当前列表"))
			{
				dao.update("update " + tempTableName + " set a_" + str3+"="+str3);
			}

			sqlBuffer.setLength(0);
			String key="A0100";
			if(this.InfoGroupFlag == YksjParser.forPerson)
			{
				sqlBuffer.append("insert into " + tempTableName + "2 (a0100,nbase,"+ str3);
			}
			else if(this.InfoGroupFlag == YksjParser.forUnit)
			{
				sqlBuffer.append("insert into " + tempTableName + "2 (b0110,"+ str3);
				key="b0110";
			}
			else if(this.InfoGroupFlag == YksjParser.forPosition)
			{
				sqlBuffer.append("insert into " + tempTableName + "2 (e01a1,"+ str3);
				key="e01a1";
			}
			
			
			
			
			if ("当前列表".equalsIgnoreCase(str5)
					&& TempTableName.length() > 5
					&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")  || TempTableName
							.toLowerCase().endsWith("gzsp"))) {
				sqlBuffer.append(",a00z0,a00z1");
			}
			sqlBuffer.append(") select distinct  "+key);
			
			if(this.InfoGroupFlag == YksjParser.forPerson)
			{
				sqlBuffer.append(",");
				if (!"当前列表".equalsIgnoreCase(str5))
						sqlBuffer.append("nbase");
				else
				{
					if ("q05".equalsIgnoreCase(StdTmpTable)|| "q03".equalsIgnoreCase(StdTmpTable) ||StdTmpTable
							.toLowerCase().indexOf("s05") != -1||(StdTmpTable.length() > 5
							&& (StdTmpTable.toLowerCase().indexOf("salary") != -1||StdTmpTable.toLowerCase().endsWith("_gz")   || StdTmpTable
									.toLowerCase().endsWith("gzsp") )))
						sqlBuffer.append("nbase");
					else
					{
						if(item!=null&&StdTmpTable.toLowerCase().indexOf("per_result_")==-1)
							sqlBuffer.append("basepre");
						else
							sqlBuffer.append("''");
					}
				}
			}
			
			sqlBuffer.append(",a_" + str3);
			if ("当前列表".equalsIgnoreCase(str5)
					&& TempTableName.length() > 5
					&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName
							.toLowerCase().endsWith("gzsp") )) {
				sqlBuffer.append(",a00z0,a00z1");
			}
			sqlBuffer.append(" from " + tempTableName);
			dao.update(sqlBuffer.toString());
			// 计算分组汇总值
			calculatGroupStatValue(str1, str2, str3,str6);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 计算分组汇总值 str1:汇总指标 str2:汇总方式 str3:分组指标
	 */
	private void calculatGroupStatValue(String str1, String str2, String str3,String str6) {
		try {
			String tempTableName ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum; // userView.getUserName() + "GroupTable";
			String tempTableName2 ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum+"2"; // userView.getUserName() + "GroupTable2";
			if (DBType == Constant.MSSQL && isTempTable) {
				tempTableName = "##" + tempTableName;
				tempTableName2 = "##" + tempTableName2;
			}
			String tempSql = "";
			if ("个数".equalsIgnoreCase(str2) || "人数".equalsIgnoreCase(str2))
				tempSql = "select count(" + str3 + ") a,a_" + str3 + " b from "
						+ tempTableName; // + " group by a_" + str3;
			else if ("最小值".equalsIgnoreCase(str2))
				tempSql = "select min(" + str1 + ") a,a_" + str3 + " b from "
						+ tempTableName ;
			else if ("最大值".equalsIgnoreCase(str2))
				tempSql = "select max(" + str1 + ") a,a_" + str3 + " b from "
						+ tempTableName;
			else if ("平均值".equalsIgnoreCase(str2))
				tempSql = "select avg(" + str1 + ") a,a_" + str3 + " b from "
						+ tempTableName;
			else if ("总和".equalsIgnoreCase(str2))
				tempSql = "select sum(" + str1 + ") a,a_" + str3 + " b from "
						+ tempTableName ;
			
			if (str6 != null && str6.trim().length() > 0) 
			{
				tempSql+=" where " + str6;
			}
			tempSql+=" group by a_" + str3;
			
			ContentDAO dao = new ContentDAO(this.con); 
			 
			if ("个数".equalsIgnoreCase(str2) || "人数".equalsIgnoreCase(str2))
			{
				dao.update("update " + tempTableName2
						+ " set goupValue=(select  T.a   from (" + tempSql
						+ ") T where T.b=" + tempTableName2 + "." + str3 + " )");
			}
			else
			{
				dao.update("update " + tempTableName2
						+ " set goupValue=(select "+Sql_switcher.round("T.a",5)+"  from (" + tempSql
						+ ") T where T.b=" + tempTableName2 + "." + str3 + " )");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	HashMap usedFieldMap = new HashMap(); // ModeFlag==forNormal 时
											// mapUsedFieldItems
											// 里不会保存数据，所以用usedFieldMap 替代
	int GroupStatNum=0;
	private boolean Func_GroupStat(RetValue retValue) throws GeneralException,
			SQLException {
		
		GroupStatNum++;
		/**
		 * str1:汇总指标或公式 str2:汇总方式 str3:分组指标 str4:分组级数 str5:范围 str6:条件
		 */
		String str = "", str1 = "", str2 = "", str3 = "", str4 = "0", str5 = "当前列表", str6 = "";
		HashMap str1FieldsMap = null;
		RetValue hold = new RetValue();
		str = SQL.toString();
		LazyDynaBean abean = new LazyDynaBean();
		if (!getParamValue(abean, retValue)) {
			return false;
		}

		str1 = (String) abean.get("str1");
		str1FieldsMap = (HashMap) abean.get("str1FieldsMap");
		str2 = (String) abean.get("str2");
		str3 = (String) abean.get("str3");
		if (abean.get("str4") != null)
			str4 = (String) abean.get("str4");
		if (abean.get("str5") != null)
			str5 = (String) abean.get("str5");
		if (abean.get("str6") != null)
			str6 = (String) abean.get("str6");
		if (isVerify == false) // 是否进行语法校验
			createGroupStatTempTable(str1, str2, str3, str4, str5, str6,
					str1FieldsMap);

		SQL.setLength(0);
		SQL.append(str);
		String tempTableName2 ="t#"+userView.getUserName()+"_sf_gro_"+GroupStatNum+"2"; // userView.getUserName() + "GroupTable2";
		if (DBType == Constant.MSSQL && isTempTable)
			tempTableName2 = "##" + tempTableName2;
		String result = "";
		FieldItem field1 = new FieldItem();
		if (ModeFlag == forNormal)
			field1.setItemid("SELECT_" + (usedFieldMap.size() + 1));
		else
			field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
		field1.setItemdesc("分组汇总");
		if ("个数".equalsIgnoreCase(str2) || "人数".equalsIgnoreCase(str2)) {
			field1.setDecimalwidth(0);
		} else {
			field1.setDecimalwidth(5);
		}
	 	field1.setItemlength(10);
		field1.setItemtype("N");
		field1.setCodesetid("0");
		field1.setVarible(2);// nIsVar := 2;
		mapUsedFieldItems.put(field1.getItemid(), field1);
		usedFieldMap.put(field1.getItemid(), field1);

		// String tableName=this.TempTableName;
		// if(str5.equalsIgnoreCase("当前列表"))
		// tableName=StdTmpTable;

		if (ModeFlag == this.forSearch) {
			String goalItem = field1.getItemid();
			StringBuffer strSQL = new StringBuffer("");
			strSQL.append("UPDATE ").append(TempTableName);
			strSQL.append(" SET ").append(TempTableName).append(".").append(
					goalItem).append("=");
			strSQL.append(getSubSql_groupStat(tempTableName2, TempTableName,
					str5, Sql_switcher.isnull("goupValue", "0")));
			strSQL.append("  where exists "
					+ getSubSql_groupStat(tempTableName2, TempTableName, str5,
							"null"));
			if (!"".equals(strSQL.toString()))
				SQLS.add(strSQL.toString());
		//	result = "NULLIF("+field1.getItemid()+",0)";
		 	result = Sql_switcher.isnull(field1.getItemid(), "0");
		} else if (ModeFlag == forNormal && !isVerify) {

			if (StdTmpTable != null && StdTmpTable.trim().length() > 0) {
				Table table = new Table(StdTmpTable, StdTmpTable);
				table.addField(field1.cloneField());
				DbWizard dbWizard = new DbWizard(this.con);
				if (dbWizard.isExistField(StdTmpTable, field1.getItemid(),false)) {
					dbWizard.dropColumns(table);
				}
				dbWizard.addColumns(table);

				ContentDAO dao = new ContentDAO(this.con);
				String sql = "update "
						+ StdTmpTable
						+ " set "
						+ field1.getItemid()
						+ "="
						+ getSubSql_groupStat(tempTableName2, StdTmpTable,
								str5, Sql_switcher.isnull("goupValue", "0"));
				if (DBType == Constant.ORACEL)
				{
					sql += " where exists "
							+ getSubSql_groupStat(tempTableName2, StdTmpTable,
									str5, "null");
				}
				
				
				if("q05".equalsIgnoreCase(StdTmpTable)|| "q03".equalsIgnoreCase(StdTmpTable))
				{
					if (DBType == Constant.ORACEL)
					{
						sql+=" and "+str6;
					}
					else
						sql+=" where "+str6;
					
				}
				
				
				dao.update(sql);
				//201411102 dengcan
		/*		if(bDivFlag) // 除数标示
					result = "NULLIF("+field1.getItemid()+",0)"; //当用到除法的时候 防止除数为零	
				else */
					result = Sql_switcher.isnull(field1.getItemid(), "0");
				
			} else
				result = getSubSql_groupStat(tempTableName2, TempTableName,
						str5, Sql_switcher.isnull("goupValue", "0"));
		}

		SQL.append(result);

		if ("个数".equalsIgnoreCase(str2) || "人数".equalsIgnoreCase(str2)) {
			retValue.setValue(new Integer(123));
			retValue.setValueType(INT);
		} else {
			retValue.setValue(new Float(10.0));
			retValue.setValueType(FLOAT);
		}
		return Get_Token();
	}

	public String getSubSql_groupStat(String tempTableName2, String tableName,
			String str5, String itemid) {

		String key="A0100";
		if(this.InfoGroupFlag == YksjParser.forUnit)
			key="B0110";
		else if(this.InfoGroupFlag == YksjParser.forPosition)
			key="E01A1";
		
		StringBuffer strSQL = new StringBuffer("");
		strSQL.append("(select  ");
		//201411102 dengcan
	/*	if(bDivFlag) // 除数标示
			strSQL.append("NullIF("+itemid+",0)"); 
		else */
			strSQL.append(itemid); 
		strSQL.append(" from " + tempTableName2);
		strSQL.append(" where " + tempTableName2 + "."+key+"=");
		strSQL.append(tableName);
		strSQL.append("."+key+"");
		if ("当前列表".equalsIgnoreCase(str5)
				&&( !tableName.equalsIgnoreCase(this.TempTableName)||(tableName.length() > 5
						&& (tableName.toLowerCase().indexOf("salary") != -1||tableName.toLowerCase().endsWith("_gz") || tableName
								.toLowerCase().endsWith("gzsp")))  )) {
			if (tableName.length() > 5
					&& (tableName.toLowerCase().indexOf("salary") != -1||tableName.toLowerCase().endsWith("_gz") || tableName
							.toLowerCase().endsWith("gzsp"))) {
				strSQL.append(" and " + tempTableName2 + ".a00z0=" + tableName
						+ ".a00z0 ");
				strSQL.append(" and " + tempTableName2 + ".a00z1=" + tableName
						+ ".a00z1 ");
			}
			 
		}
	    if ("当前列表".equalsIgnoreCase(str5)&&this.ModeFlag == 0&&this.InfoGroupFlag == YksjParser.forPerson&&tableName.toLowerCase().indexOf("per_result_")==-1) //不包含绩效结果表per_result_xxx
		{
			strSQL.append(" and " + tempTableName2 + ".NBASE=" + tableName);
			if ( "q05".equalsIgnoreCase(tableName)|| "q03".equalsIgnoreCase(tableName)||(tableName.length() >5
					&& (tableName.toLowerCase().indexOf("salary") != -1||tableName.toLowerCase().endsWith("_gz") || tableName
							.toLowerCase().endsWith("gzsp"))))
				strSQL.append(".nbase");
			else
				strSQL.append(".basepre");
		}
		strSQL.append(")");
		return strSQL.toString();
	}
	
	/** 
	 * 预算汇总(表达式，统计条件[，分组指标])，没有分组指标时表示汇总， 固定求sum
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private String budgetGroupCheckFld;
	private boolean Func_BUDGETSUM(RetValue retValue) throws GeneralException,SQLException {
		/** update tab set fld = getexpr() from (select grpfld, 表达式 from 临时表 where 条件) A where 外部条件
		 * SQL 返回 带sum的表达式
		 * SQLS 依次返回： 0=条件，1=分组指标。
		 * WJH 2013-10-31 SQLS 返回 sum表达式;条件;分组表达式。 其中 sum表达式带别名。 并且别名返回给SQL
		 */
		String strWhere = "", strSQL = "", strExpr="", strGrpFld="", strFldAli="";
		RetValue retValue1 = new RetValue();
		try {
			// SQLS.clear();
			strSQL = SQL.toString();
			SQL.setLength(0);
			
			if (!Get_Token())
				return false;
			// 处理左括号
			if (tok != S_LPARENTHESIS) {
				Putback();
				SError(E_LOSSLPARENTHESE);
				return false;
			}
	
			/** 指标 */
			if (!Get_Token())
				return false;
	
			if (!level0(retValue))
				return false;
			
			strFldAli = "BGSUM_"+SQLS.size();
			strExpr = "sum(" + SQL.toString() +") AS "+strFldAli;
			
			// 后两个参数为空时
			if (tok == S_RPARENTHESIS)
				return Get_Token();
			// 条件
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			SQL.setLength(0); // 条件
			if (!Get_Token()) {
				return false;
			}
			if (!level0(retValue1)) {
				return false;
			}
			if (!retValue1.isBooleanType()) {
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}
			strWhere = "(" + SQL.toString() + ")";
			// SQLS.add(strWhere);
			
			// 后一个参数为空时
			if (tok == S_RPARENTHESIS){
				if(budgetGroupCheckFld==null){
					budgetGroupCheckFld = "";
				}else{
					if(budgetGroupCheckFld.length()!=0){
						Putback();
						SError(E_BUDGERGRPNOSAME);
						return false;
					}
				}
				return Get_Token();
			}
			// 分组指标
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			SQL.setLength(0); // 分组指标
			if (!Get_Token())
				return false;
			if (token_type!=FIELDITEM){
				// 此处必须是分组指标
				Putback();
				SError(E_MUSTBEFIELDITEM);
				return false;
			}
			// 检查分组指标类型: 字符串和整形才可以分组
			if (Field==null || !("A".equalsIgnoreCase(Field.getItemtype()) || ("N".equalsIgnoreCase(Field.getItemtype()) && Field.getDecimalwidth()==0) )){
				Putback();
				SError(E_PRO_TYPEERROR);
				return false;
			}		
			strGrpFld = Field.getItemid();
			// 一个公式中的预算汇总的分组指标必须一样
			if(budgetGroupCheckFld==null){
				budgetGroupCheckFld = strGrpFld;
			}else{
				if(!budgetGroupCheckFld.equals(strGrpFld)){
					Putback();
					SError(E_BUDGERGRPNOSAME);
					return false;
				}
			}
			// SQLS.add(strGrpFld);
			if (!Get_Token())
				return false;
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			return Get_Token();
		}finally{
			SQLS.add(":#budgetsum;"+strExpr+";"+strWhere+";"+strGrpFld);
			SQL.setLength(0);
			SQL.append(strSQL);
			SQL.append(strFldAli);
		}
	}
	
	
	/**
	 * 可修天数
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_KXDAYS(RetValue retValue) throws GeneralException,SQLException {
		String strSQL = SQL.toString(); 
		
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		} 
		FieldItem lx_item =null; //请假类型指标
		FieldItem qs_item =null; //起始时间指标 
		FieldItem zz_item =null; //结束时间指标
		String    ztFlag  ="0";  //在途单据
		String    kx_flag="0";// 取可修天数的扩展参数 当kx_flag为0时，表示单独取本年可休天数；当kx_flag为1时，表示单独取结余部分可休天数；当kx_flag没有值或其它值时，仍按原逻辑取可休天数
		
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		lx_item = this.Field;
		if ("".equalsIgnoreCase(lx_item.getCodesetid())
				|| "0".equalsIgnoreCase(lx_item.getCodesetid())) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM&&this.token_type!=this.ODDVAR&&this.token_type!=this.DATEVALUE) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		
		if(this.token_type!=this.DATEVALUE)
		{
			if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
		    {
				Putback();
				SError(E_MUSTBEDATE);
				return false;
			}
		}
		else
			this.Field.setValue(this.token);
		
		qs_item = this.Field;
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM&&this.token_type!=this.ODDVAR&&this.token_type!=this.DATEVALUE) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		
		if(this.token_type!=this.DATEVALUE)
		{
			if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
		    {
				Putback();
				SError(E_MUSTBEDATE);
				return false;
			}
		}
		else
			this.Field.setValue(this.token);
		
		zz_item = this.Field;
		if (!Get_Token())
			return false; 
		if (tok != S_RPARENTHESIS) {
			// 20140718, wangjh  扩展 参数“0|1”，1表示包含在途单据
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			if (!Get_Token())
				return false;
			if (this.token_type!=INT) {
				Putback();
				SError(E_MUSTBEINTEGER);
				return false;
			}
			if (!"0".equalsIgnoreCase(token) && !"1".equalsIgnoreCase(token)) {
				Putback();
				setStrError("此处参数只能是0或1,1表示包含在途单据.");	
				return false;
			}
			ztFlag = token;
			if (!Get_Token())
				return false;
			if (tok != S_RPARENTHESIS){
				
				
				if (tok != S_COMMA) {
					Putback();
					SError(E_LOSSCOMMA);
					return false;
				}
				if (!Get_Token())
					return false;
				if (this.token_type!=INT) {
					Putback();
					SError(E_MUSTBEINTEGER);
					return false;
				}
				if (!"0".equalsIgnoreCase(token) && !"1".equalsIgnoreCase(token)&& !"2".equalsIgnoreCase(token)) {
					Putback();
					setStrError("此处参数只能是0或1或2，0表示全部，1表示单独取本年可休天数，2表示单独取结余部分可休天数.");	
					return false;
				}
				kx_flag = token;  // 当kx_flag为0或不传 按原逻辑取可休天数，当kx_flag为1表示单独取本年可休天数；当kx_flag为2时，表示单独取结余部分可休天数
				if (!Get_Token())
					return false;
				if (tok != S_RPARENTHESIS){ 
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				
				
			}
		} 
		
		SQL.setLength(0);
		SQL.append(strSQL).append(getKqTs(1,lx_item,qs_item,zz_item, ztFlag,kx_flag));
		retValue.setValue(new Float(123));
		retValue.setValueType(FLOAT);
		 
		return Get_Token();
	}
	
	
	/**
	 * 获得申请单号对应的模板字段
	 * @return
	 */
	private String getQ1501Item()
	{
		String item_id="";
		try {
	        
         
			String tabid=StdTmpTable.toLowerCase().split("templet_")[1]; 
			TemplateTableParamBo tp=new TemplateTableParamBo(Integer.parseInt(tabid.trim()),this.con);
			String mapping=tp.getKq_field_mapping(); 
			String kqTab=tp.getKq_setid();
		//	q15~Q1501:AB115_2,Q1503:AB101_2,Q1505:AB102_2,Q15Z1:AB109_2,Q15Z3:AB110_2,Q1507:AB111_2
			if("q15".equalsIgnoreCase(kqTab))
			{
				String[] temp=mapping.split(",");
				for(int i=0;i<temp.length;i++)
				{
					if(temp[i].trim().length()>0)
					{
						if("q1501".equalsIgnoreCase(temp[i].split(":")[0]))
						{	
							item_id=temp[i].split(":")[1];
							return item_id.trim();
						}
					}
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return item_id;
	}
	
	
	/**
	 * 获得考情的可修天数|已修天数
	 * @param opt 1：可修天数  2：已修天数  3：申请时长
	 * @param item
	 * @param ztFlag 可休天数参数 0： 不含在途  1： 包含在途
	 * @param kx_flag   当kx_flag为0或不传 按原逻辑取可休天数，当kx_flag为1表示单独取本年可休天数；当kx_flag为2时，表示单独取结余部分可休天数
	 * @return
	 */
	private String getKqTs(int opt,FieldItem typeItem,FieldItem startTime,FieldItem endTime, String ztFlag,String kx_flag)throws GeneralException 
	{
		String sql="";
		try
		{ 
			/** 语法校验时，不执行具体的SQL */
			boolean bcheck = true;
			//只支持人事异动	if (StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1)  
			if (StdTmpTable != null&& TempTableName.equalsIgnoreCase(StdTmpTable)&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1)  	
				bcheck = false;
			if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算
				bcheck = false;
			if(bcheck)
				return "0";
			String seqnum_id=getQ1501Item(); //获得申请单号对应的模板字段
			DbWizard dbw = new DbWizard(this.con);
			String itemid="kxts";
			if(opt==2)
				itemid="yxts";
			String tabname=this.StdTmpTable; 
			if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算
			{		
  				    tabname=this.StdTmpTable+"_temp"; 
					StringBuffer  sql_str=new StringBuffer("");
					switch (Sql_switcher.searchDbServer()) {
					case 1: // MSSQL 
						sql_str.append("select  *  into "+StdTmpTable+"_temp from "+StdTmpTable);
						break;
					case 2:// oracle
						sql_str.append("create table "+StdTmpTable+"_temp as  select  *  from  "+StdTmpTable  );	
						break;
					}
					if(StdTmpTable_where!=null&&StdTmpTable_where.trim().length()>0)
					{
						if(StdTmpTable_where.trim().toLowerCase().indexOf("where")==0)
							sql_str.append(" "+StdTmpTable_where);
						else
						{
							if(StdTmpTable_where.trim().toLowerCase().indexOf("and")==0)
								sql_str.append(" where 1=1 "+StdTmpTable_where);
							else
								sql_str.append(" where "+StdTmpTable_where);
						}
					}
				//	if(dbw.isExistTable(tabname, false))
						dbw.dropTable(tabname);
					dbw.execute(sql_str.toString());
			}
					
			if(!dbw.isExistField(tabname,itemid,false))
			{
					/** 结果指标 */
					Table _table=new Table(tabname);
					Field	temp=new Field(itemid,itemid);
					temp.setDatatype(DataType.FLOAT);
					temp.setLength(9);
					temp.setDecimalDigits(4);
					_table.addField(temp);
					dbw.addColumns(_table);
			} 
			ContentDAO dao=new ContentDAO(this.con);
			
			StringBuffer sqlBuffer=new StringBuffer(""); 
			/*
			if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算
			{
				sqlBuffer.append("select "+StdTmpTable+".a0100"); 
	            sqlBuffer.append(","+StdTmpTable+"."+typeItem.getItemid()+","+StdTmpTable+"."+startTime.getItemid()+","+StdTmpTable+"."+endTime.getItemid());
				if(seqnum_id.length()>0)
					sqlBuffer.append(","+StdTmpTable+"."+seqnum_id);
				sqlBuffer.append(" from "+tabname+",( select * from "+this.StdTmpTable);
				
				if(StdTmpTable_where!=null&&StdTmpTable_where.trim().length()>0)
				{
					if(StdTmpTable_where.trim().toLowerCase().indexOf("where")==0)
						sqlBuffer.append(" "+StdTmpTable_where);
					else
						sqlBuffer.append(" where "+StdTmpTable_where);
				}
				sqlBuffer.append(" )  as "+this.StdTmpTable+"  where 1=1 and  "+tabname+".a0100="+this.StdTmpTable+".a0100  ");
			}
			else */
			{
				sqlBuffer.append("select a0100,basepre");
	            if("templet_".equalsIgnoreCase(tabname.substring(0,8)))
	                sqlBuffer.append(",ins_id");
	            sqlBuffer.append(","+typeItem.getItemid());
	            if("D".equalsIgnoreCase(startTime.getItemtype()))
	            	sqlBuffer.append(","+startTime.getItemid());
	            if("D".equalsIgnoreCase(endTime.getItemtype()))
	            	sqlBuffer.append(","+endTime.getItemid());
				if(seqnum_id.length()>0)
					sqlBuffer.append(","+seqnum_id);
				sqlBuffer.append(" from "+tabname+" where 1=1 ");
				if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算
				{
					
				}
				else if (!("()".equalsIgnoreCase(whereText))&& whereText.trim().length() > 0)
				{ 
						sqlBuffer.append(" and " + whereText);  
				}
			}
			RowSet rowSet=dao.search(sqlBuffer.toString());
			KqAppInterface kqAppInterface=new KqAppInterface(this.con,this.userView);
			LazyDynaBean abean=new LazyDynaBean();
			while(rowSet.next())
			{
				String seqnum_value="";
				String a0100=rowSet.getString("a0100");
				String  basepre="";
		//		if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算,临时表中无basepre字段
		//			basepre=this.DbPre;
		//		else
					basepre=rowSet.getString("basepre");
				String ins_id="0";
                if(tabname.length()>8&& "templet_".equalsIgnoreCase(tabname.substring(0,8)))
                    ins_id=rowSet.getString("ins_id");
				String kqLX=rowSet.getString(typeItem.getItemid());
				Date _startTime=null;
				 if("D".equalsIgnoreCase(startTime.getItemtype()))
				 {
					if(Sql_switcher.searchDbServer()==2)
					{
						Timestamp ta=rowSet.getTimestamp(startTime.getItemid());
						if(ta!=null)//liuyz bug31859
							_startTime=new Date(ta.getTime());
					}
					else
						_startTime=rowSet.getDate(startTime.getItemid());
				 }
				 else
				 { 
					    java.sql.Date sqlDate=java.sql.Date.valueOf(startTime.getValue().replaceAll("'",""));
					    _startTime=new java.util.Date (sqlDate.getTime());
				 }
				 
				Date _endTime=null;
				if("D".equalsIgnoreCase(endTime.getItemtype()))
				{
					if(Sql_switcher.searchDbServer()==2)
					{
						Timestamp ta=rowSet.getTimestamp(endTime.getItemid());
						if(ta!=null)//liuyz bug31859
							_endTime=new Date(ta.getTime());
					}
					else
						_endTime=rowSet.getDate(endTime.getItemid());
				 }
				else
				{ 
						    java.sql.Date sqlDate=java.sql.Date.valueOf(endTime.getValue().replaceAll("'",""));
						    _endTime=new java.util.Date (sqlDate.getTime());
					 
				}
				
				abean=new LazyDynaBean();
				if(kqLX!=null&&kqLX.trim().length()>0)
				{
					if(seqnum_id.length()>0)
					{
						seqnum_value=rowSet.getString(seqnum_id)!=null?rowSet.getString(seqnum_id):"";
						abean.set("q1501",seqnum_value);
					}
					else {
						abean.set("q1501","");
					} 
					abean.set("type",kqLX);
					abean.set("a0100",a0100);
					abean.set("nbase",basepre);
					abean.set("starttime",_startTime);
					abean.set("endtime",_endTime);
					abean.set("ztflag", ztFlag);
					abean.set("kx_flag",kx_flag);
					if(opt==2)// 0:总可休天数，1:当年已休天数；2:上年结余已休天数
						abean.set("yx_flag",kx_flag);
					
					double value=0;
					String _value=null;
					if(opt==1) //可修天数
						value=kqAppInterface.getAppCanUseDays(abean);
					else if(opt==2) //已修天数
						value=kqAppInterface.getAppUsedDays(abean);
					else if(opt==3) //申请时长
						value=kqAppInterface.getAppFactDays(abean);
				    if(value!=-1.0)
				    	_value=String.valueOf(value);
					String _sql="update "+tabname+" set "+itemid+"="+_value+" where a0100='"+a0100+"' ";
					if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算,临时表中无basepre字段
					{
						
					}
					else
					{
						_sql+=" and basepre='"+basepre+"'";
						if(!"0".equalsIgnoreCase(ins_id))
	                        _sql+=" and ins_id="+ins_id;
						if (!("()".equalsIgnoreCase(whereText))&& whereText.trim().length() > 0)
						{ 
								_sql+=" and " + whereText; 
						}
					}
					dao.update(_sql);
				} 
			} 
			 
			
			
			if (ModeFlag ==this.forSearch&&StdTmpTable != null&& StdTmpTable.length() > 0&&StdTmpTable.indexOf("templet")!=-1&&targetTable!=null&&targetTable.trim().length()>0)  //临时变量计算
			{
				 sql=" ( select "+itemid+" from "+tabname+" where "+this.TempTableName+".a0100="+tabname+".a0100 ) ";
				 this.mapUsedFieldItems=new HashMap();
			} 
			else
				sql=itemid;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return sql;
	}
	
	
	
	/**
	 * 申请时长
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_QJDAYS(RetValue retValue) throws GeneralException,SQLException {
		String strSQL = SQL.toString();
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		FieldItem lx_item =null; //请假类型指标
		FieldItem qs_item =null; //起始时间指标 
		FieldItem zz_item =null; //结束时间指标
		
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		lx_item = this.Field;
		if ("".equalsIgnoreCase(lx_item.getCodesetid())
				|| "0".equalsIgnoreCase(lx_item.getCodesetid())) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
	    {
			Putback();
			SError(E_MUSTBEDATE);
			return false;
		}
		
		qs_item = this.Field;
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
	    {
			Putback();
			SError(E_MUSTBEDATE);
			return false;
		}
		zz_item = this.Field;
		if (!Get_Token())
			return false; 
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		} 
	  
		
		
		
		SQL.setLength(0);
		SQL.append(strSQL).append(getKqTs(3,lx_item,qs_item,zz_item,"0",""));
		retValue.setValue(new Float(123));
		retValue.setValue(new Float(123));
		retValue.setValueType(FLOAT);
		return Get_Token();
	}
	
	
	/**
	 * 已修天数
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_YXDAYS(RetValue retValue) throws GeneralException,SQLException {
		String strSQL = SQL.toString(); 
		
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		} 
		FieldItem lx_item =null; //请假类型指标
		FieldItem qs_item =null; //起始时间指标 
		FieldItem zz_item =null; //结束时间指标
		String    yx_flag="0";// 取已修天数的扩展参数 当yx_flag为0或不传 按原逻辑取可休天数，当kx_flag为1表示单独取当年已休天数；当kx_flag为2时，表示上年结余已休天数
		
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		lx_item = this.Field;
		if ("".equalsIgnoreCase(lx_item.getCodesetid())
				|| "0".equalsIgnoreCase(lx_item.getCodesetid())) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
	    {
			Putback();
			SError(E_MUSTBEDATE);
			return false;
		}
		
		qs_item = this.Field;
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if(!"D".equalsIgnoreCase(((FieldItem)this.Field.cloneItem()).getItemtype()))
	    {
			Putback();
			SError(E_MUSTBEDATE);
			return false;
		}
		zz_item = this.Field;
		if (!Get_Token())
			return false; 
		
		if (tok != S_RPARENTHESIS){
			
			
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			if (!Get_Token())
				return false;
			if (this.token_type!=INT) {
				Putback();
				SError(E_MUSTBEINTEGER);
				return false;
			}
			if (!"0".equalsIgnoreCase(token) && !"1".equalsIgnoreCase(token)&& !"2".equalsIgnoreCase(token)) {
				Putback();
				SError(E_CANCHOOSEZEROTOTHREE);
				return false;
			}
			yx_flag = token;  // 当yx_flag为0或不传 按原逻辑取可休天数，当kx_flag为1表示单独取当年已休天数；当kx_flag为2时，表示上年结余已休天数
			if (!Get_Token())
				return false;
			if (tok != S_RPARENTHESIS){ 
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		
		SQL.setLength(0);
		SQL.append(strSQL).append(getKqTs(2,lx_item,qs_item,zz_item,"0",yx_flag));
		retValue.setValue(new Float(123));
		retValue.setValueType(FLOAT);
		 
		return Get_Token();
	}
	
	
	/**
	 * 取上月实发工资人数（薪资类别号，归属单位指标，条件）
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_SYSFGZRS(RetValue retValue) throws GeneralException,SQLException {
		
		String  str1 = "", str2 = "", str3 = "";
		HashMap str1FieldsMap = null;
		RetValue hold = new RetValue();
		String strSQL = SQL.toString();
		LazyDynaBean abean = new LazyDynaBean();
		if (!getGzParamValue(abean, retValue)) {
			return false;
		} 
		//SQL_SELECT2(Field1, strWhere, nSQLtype)
		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_GZRS(abean));
		retValue.setValue(new Integer(123));
		retValue.setValueType(INT);
		return Get_Token();
	}
	
	private String SQL_GZRS(LazyDynaBean abean)
	{
		String str1=(String)abean.get("str1"); //薪资类别号
		String str2=(String)abean.get("str2"); //归属单位指标
		String str3=(String)abean.get("str3"); //条件
		String result="";
		try
		{
			FieldItem field1 = new FieldItem();
			if (ModeFlag == forNormal)
				field1.setItemid("SELECT_" + (usedFieldMap.size() + 1));
			else
				field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
			field1.setItemdesc("实发人数");
			field1.setDecimalwidth(0); 
		 	field1.setItemlength(10);
			field1.setItemtype("N");
			field1.setCodesetid("0");
			field1.setVarible(2);// nIsVar := 2;
			mapUsedFieldItems.put(field1.getItemid(), field1);
			usedFieldMap.put(field1.getItemid(), field1);
			
			
			if (this.ymc == null) {
				Date sysdate = new Date();
				this.ymc = new YearMonthCount(DateUtils.getYear(sysdate), DateUtils
						.getMonth(sysdate), DateUtils.getDay(sysdate), 1);
			}
			int year=this.ymc.getYear();
			int month=this.ymc.getMonth();
			if(this.ymc.getMonth()==1)
			{
				year=year-1;
				month=12;
				//this.ymc.setYear(this.ymc.getYear()-1);
				//this.ymc.setMonth(12);
			}
			else
			{
				month=month-1;
				//this.ymc.setMonth(this.ymc.getMonth()-1); 
			}
			
			StringBuffer strSQL = new StringBuffer("");
			if (ModeFlag == forNormal)
			{
				 
				//	strSQL.append("( select a from ");
				//	strSQL.append(" ( select count(*) a ,"+str2+"   from salaryhistory   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
				//	strSQL.append(" "+Sql_switcher.year("a00z2")+"="+year+" and "+Sql_switcher.month("a00z2")+"="+month+"  "+str3+"  group  by  "+str2+")  dd where "+TempTableName+".b0110=dd."+str2+" ");
				//	strSQL.append(" ) ");  
					
					
					
					strSQL.append("( select a from ");
					strSQL.append(" ( select count(*) a ,"+str2+"  from (select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryhistory   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
					strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
					
					strSQL.append(" union all select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryarchive   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
					strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
					
					strSQL.append("    ) b  group  by  "+str2+")  dd where "+TempTableName+".b0110=dd."+str2+" ");
					strSQL.append(" ) "); 
					
					
					result=strSQL.toString();
				
			}
			else
			{
				String goalItem = field1.getItemid();
				
				strSQL.append("UPDATE "+TempTableName+" SET "+field1.getItemid()+"=( ");
				strSQL.append("select a from  ");
				
				strSQL.append(" ( select count(*) a ,"+str2+"  from (select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryhistory   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
				strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
			
				strSQL.append(" union all select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryarchive   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
				strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
				
				strSQL.append(" ) b  group  by  "+str2+")  dd where "+TempTableName+".b0110=dd."+str2+" ");
				
				strSQL.append(" ) WHERE EXISTS  ( ");
				strSQL.append("select NULL from  ");
			
				strSQL.append(" ( select count(*) a ,"+str2+"  from (select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryhistory   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
				strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
				
				strSQL.append(" union all select  distinct a0100,upper(nbase) as nbase,"+str2+"   from salaryarchive   where   sp_flag='06' and  salaryid in ("+str1+") and  ");
				strSQL.append(" "+Sql_switcher.year("a00z0")+"="+year+" and "+Sql_switcher.month("a00z0")+"="+month+"  "+str3);
				
				strSQL.append(" ) b  group  by  "+str2+")  dd where "+TempTableName+".b0110=dd."+str2+" ) ");
				
				SQLS.add(strSQL.toString());
				
			
				
				
				result = Sql_switcher.isnull(field1.getItemid(), "0");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		
		
		return result;
	}
	

	/**
	 *  取上月实发工资人数（薪资类别号，归属单位指标，条件）
	 *   分组汇总(岗位工资,总和,单位名称,2,当前列表,部门="010101" 且 薪级="01")
	 */

	// 取得表达式中的各参数值
	private boolean getGzParamValue(LazyDynaBean abean, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		SQL.setLength(0); 
		if (!Get_Token())
			return false; 
		if (this.token_type!=QUOTE) {
			Putback();
			SError(E_LOSSQUOTE);
			return false;
		}
		abean.set("str1", this.token); 
		if (!Get_Token())
			return false; 
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (this.token_type!=FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		abean.set("str2",this.Field.getItemid()); 
		
		if (!Get_Token())
			return false;
		if (tok == S_RPARENTHESIS)
		{
			abean.set("str3","");
			return true;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		
		SQL.setLength(0); // 条件
		HashMap _map=(HashMap)this.mapUsedFieldItems.clone();
		ArrayList setList=(ArrayList)UsedSets.clone();
		if (!Get_Token()) {
			return false;
		}  
		if (!level0(retValue)) {
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		this.mapUsedFieldItems=_map;
		this.UsedSets=setList;
		abean.set("str3"," and ( "+ SQL.toString()+" )"); 
		 
	 
		return true;
	}
	
	
	
	/**
	 * 执行存储过程
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_PROCEDURE(RetValue retValue) throws GeneralException,SQLException {
		
		try
		{
			isFuncPROCEDURE=true;
			ContentDAO dao = new ContentDAO(this.con);
			ArrayList param_list=new ArrayList();
			if (!getProParamValue(param_list, retValue)) {
				return false;
			}
			
			String pro_name="";  //存储过程名
			LazyDynaBean _bean=(LazyDynaBean)param_list.get(0);
			pro_name=((String)_bean.get("value")).toUpperCase();
			
			String validateSql1="";
			String validateSql2="";
			String validateSql3="";
			switch (Sql_switcher.searchDbServer()) {
			case 1: // MSSQL 
				validateSql1="select   count(*)  from   sysobjects   where   ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and "; 
				validateSql1+=" id = object_id(N'[dbo].[" + pro_name + "]'))";
				validateSql2="select count(*) b from syscolumns where ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and ";
				validateSql2+= " id = object_id(N'[dbo].[" + pro_name + "]'))";
				validateSql3="select * from syscolumns where ID in (SELECT id FROM sysobjects as a WHERE OBJECTPROPERTY(id, N'IsProcedure') = 1 and ";
				validateSql3+= " id = object_id(N'[dbo].[" + pro_name + "]')) order by colorder "; 
				break;
			case 2:// oracle
				validateSql1="SELECT count(*) FROM all_objects WHERE object_type='PROCEDURE' AND object_name='"+pro_name+"'";
				validateSql2="select count(*) b from user_arguments where object_name = '" +pro_name+ "'";
				validateSql3="select argument_name, PLS_TYPE from user_arguments  where object_name = '"+pro_name+"' order by position";
				break;
			}
			
			//判断有无存储过程名
			int nn=0;
			RowSet rowSet=dao.search(validateSql1);
			if(rowSet.next())
				nn=rowSet.getInt(1);
			if(nn==0)
			{
				Putback();
				SError(E_PRO_NO);
				return false; 
			}
			//判断存储过程参数个数
			nn=0;
			rowSet=dao.search(validateSql2);
			if(rowSet.next())
				nn=rowSet.getInt(1);
			if(nn!=param_list.size()-1)
			{
				Putback();
				SError(E_PRO_LOSSPARAM);
				return false; 
			}
			StringBuffer sqlCall = new StringBuffer("{call  " + pro_name);
			nn=0;
			rowSet=dao.search(validateSql3);
			StringBuffer buf_str=new StringBuffer("");
			while(rowSet.next())
			{
				nn++;
				String type="A";
				if(Sql_switcher.searchDbServer()==1)//MSSQL
				{
					int xtype=rowSet.getInt("xtype");
					if(xtype==61)
						type="D";
					if(xtype==48||xtype==52||xtype==56||xtype==127)
						type="I";
					if(xtype==62||xtype==106||xtype==108)
						type="N";
				}
				else
				{
					String PLS_TYPE=rowSet.getString("PLS_TYPE");
					if("NUMBER".equalsIgnoreCase(PLS_TYPE)|| "FLOAT".equalsIgnoreCase(PLS_TYPE))
						type="N";
					if("DATE".equalsIgnoreCase(PLS_TYPE))
						type="D";
					if("INTEGER".equalsIgnoreCase(PLS_TYPE))
						type="I";
				}
				
				_bean=(LazyDynaBean)param_list.get(nn);
				String _type=(String)_bean.get("type");
				String _value=(String)_bean.get("value");
				 
			 
				if("A".equals(type)&&!"当前表".equals(_value)&&!"用户名".equals(_value)&&!"7".equalsIgnoreCase(_type)&&!"4".equalsIgnoreCase(_type))
				{
					Putback();
					SError(E_PRO_TYPEERROR);
					return false; 
				}
				if("N".equals(type)&&!("6".equalsIgnoreCase(_type)|| "5".equalsIgnoreCase(_type)))
				{
					Putback();
					SError(E_PRO_TYPEERROR);
					return false; 
				}
				if("D".equals(type)&&!"截止日期".equals(_value)&&!"9".equalsIgnoreCase(_type))
				{
					Putback();
					SError(E_PRO_TYPEERROR);
					return false; 
				}
				if("I".equals(type)&&!"5".equalsIgnoreCase(_type))
				{
					Putback();
					SError(E_PRO_TYPEERROR);
					return false; 
				}
				_bean.set("type", type);
				buf_str.append(",?"); 
			}
			
			if(nn>0)
			{
				sqlCall.append("("+buf_str.substring(1)+")");
			}
			sqlCall.append("}");
			
			if(!isVerify)
			{
				CallableStatement cstmt = null; // 存储过程
				cstmt = this.con.prepareCall(sqlCall.toString());
				for(int i=1;i<param_list.size();i++)
				{
					_bean=(LazyDynaBean)param_list.get(i);
					String type=(String)_bean.get("type");
					String value=(String)_bean.get("value");
					if("A".equals(type))
					{
						if("当前表".equalsIgnoreCase(value))
							cstmt.setString(i,StdTmpTable); 
						else if("用户名".equalsIgnoreCase(value))
							cstmt.setString(i,this.userView.getUserName()); 
						else  
							cstmt.setString(i,value); 
					}
					if("N".equals(type))
					{
						cstmt.setFloat(i,Float.parseFloat(value));
					}
					if("D".equals(type))
					{
						
						if("截止日期".equalsIgnoreCase(value))
						{
							String strDate = "";
							if (userView == null) {
								strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
										.substring(0, 10);
							} else {
								if ("HrpWarn".equalsIgnoreCase(userView.getUserName())) // 预警建的虚拟userview
									strDate = com.hrms.frame.utility.DateStyle.getSystemTime()
											.substring(0, 10);
								else
									strDate = com.hrms.hjsj.sys.ConstantParamter
											.getAppdate(userView.getUserName());
							}
							value=strDate.replaceAll("-",".");
						} 
						String[] tmp = StringUtils.split(value, ".");
						Calendar d=Calendar.getInstance();
						d.set(Calendar.YEAR,Integer.parseInt(tmp[0]));
						d.set(Calendar.MONTH,Integer.parseInt(tmp[1])-1);
						d.set(Calendar.DATE,Integer.parseInt(tmp[2]));
						java.sql.Date sql_d=new java.sql.Date(d.getTimeInMillis());
						cstmt.setDate(i,sql_d);
					}
					if("I".equals(type))
					{
						cstmt.setInt(i, Integer.parseInt(value));
					}
				} 
				cstmt.execute();
				
				if (cstmt != null)
					cstmt.close();
				this.SQL.setLength(0);
				this.SQL.append("已执行存储过程");
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false; 
		}
		return Get_Token();
	}
	
	/**
	 * 执行存储过程(过程名: 参数1, 参数2, 参数3, ...)  参数中可用"当前表"
	 * 分组汇总 分组汇总(岗位工资,总和,单位名称,2,当前列表,部门="010101" 且 薪级="01")
	 */

	// 取得表达式中的各参数值
	private boolean getProParamValue(ArrayList value_list, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		 
		int n=1;
		while(true)
		{
			SQL.setLength(0); // 条件
			if (!Get_Token())
				return false;
			LazyDynaBean abean=new LazyDynaBean();
			
			if(n!=1)
			{
				if (tok == S_RPARENTHESIS)
					return true;
			}
			
			if(n==2&&tok != S_COLON)
			{
				Putback();
				SError(E_SYNTAX);
				return false;
			}
			else 
			{ 
				if(n%2==0&&n!=2)
				{
					if (tok != S_COMMA) {
						Putback();
						SError(E_LOSSCOMMA);
						return false;
					}
				}
			}
			
			if(n==1)
			{
				if (token_type != STRVALUE) {
					Putback();
					SError(E_SYNTAX);
					return false;
				}
				PROCEDURE_NAME=this.token.trim();
				abean.set("value",this.token.trim());
				abean.set("type",String.valueOf(token_type).trim());
				value_list.add(abean);
			}
			else if (tok != S_COMMA&&tok != S_COLON)
			{
				abean.set("value",this.token);
				abean.set("type",String.valueOf(token_type));
				value_list.add(abean);
			}
			n++;
		}  
	}
	
	
	
	private LazyDynaBean sequenceParamBean=null;
	private void  Func_Sequence_exc(RetValue retValue)throws GeneralException,SQLException
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.con);
			DbWizard dbw = new DbWizard(this.con);
			if(this.StdTmpTable.trim().length()==0)
				return;
			if(sequenceParamBean==null)
				return;
			String _item_id=((String)sequenceParamBean.get("_item_id")).trim();          //参考指标
			int    length=Integer.parseInt((String)sequenceParamBean.get("length"));
			String  sequenceID=(String)sequenceParamBean.get("sequenceID");
			String  _ori_item_id=((String)sequenceParamBean.get("_ori_item_id")).trim();  //目标指标
			String  sortNo=(String)sequenceParamBean.get("sortNo"); //按截取内容分类编号
			/*
			if (brun("alter table  "+this.StdTmpTable+" drop column SEQUENCE_NUMBER", dbw))
			{
				dao.update("alter table  "+this.StdTmpTable+" drop column SEQUENCE_NUMBER");
			}
			dao.update("alter table  "+this.StdTmpTable+" add  SEQUENCE_NUMBER "+ Sql_switcher.getFieldType('A',30,0));
			*/
			if(!dbw.isExistField(this.StdTmpTable, "SEQUENCE_NUMBER",false))
			{
				dao.update("alter table  "+this.StdTmpTable+" add  SEQUENCE_NUMBER "+ Sql_switcher.getFieldType('A',30,0));
			}
			else
			{
				String upd_str="update "+this.StdTmpTable+"  set  SEQUENCE_NUMBER="+_ori_item_id+" where 1=1 ";
			
				if(whereText!=null&&whereText.length()>0&&!("()".equalsIgnoreCase(whereText)))
						upd_str+=" and "+this.whereText;
				dao.update(upd_str); 
			}
			 
			String key="A0100";
			String dbpre_item="";
			String ins_item="";
			if (InfoGroupFlag == forPerson) 
			{
				/*
				if(this.DbPre!=null&&this.DbPre.trim().length()>0)
				{
					if (TempTableName.length() > 5&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")|| TempTableName
									.toLowerCase().endsWith("gzsp") ))
						dbpre_item="nbase";
					else
						dbpre_item="basepre";
				}
				else */
				if(TempTableName.length()>8&& TempTableName.toLowerCase().indexOf("templet_")!=-1) //人事异动
				{ 
					dbpre_item="basepre";
				}
			}
			if("templet_".equalsIgnoreCase(TempTableName.toLowerCase().substring(0,8)))
			{
				ins_item="ins_id";
			}
			
			if (InfoGroupFlag == forUnit) 
				 key="B0110";
			if (InfoGroupFlag == this.forPosition) 
				 key="E01A1";
			String sql="select * from "+this.StdTmpTable+" where 1=1 ";
			/*
			if (InfoGroupFlag == forPerson&&this.DbPre!=null&&this.DbPre.trim().length()>0) 
			{
				if (TempTableName.length() >5&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName
								.toLowerCase().endsWith("gzsp") ))
					sql+=" and lower(nbase)='"+this.DbPre.toLowerCase()+"'";
				else
					sql+=" and lower(basepre)='"+this.DbPre.toLowerCase()+"'";
			}*/
			if(whereText!=null&&whereText.length()>0&&!("()".equalsIgnoreCase(whereText)))
				sql+=" and "+this.whereText;
			
	  
			IDGenerator idg=new IDGenerator(2,this.con); 
			RowSet rowSet=dao.search(sql);
			HashMap map=new HashMap();
			String seq_no="";
			String sql2="update "+this.StdTmpTable+" set SEQUENCE_NUMBER=? where "+key+"=? ";
			if(dbpre_item.length()>0)
				sql2+=" and lower("+dbpre_item+")=? ";
			if(ins_item.length()>0)
				sql2+=" and ins_id=? ";
			
			if(whereText!=null&&whereText.length()>0&&!("()".equalsIgnoreCase(whereText)))
				sql2+=" and "+this.whereText;
			
			ArrayList list=new ArrayList();
			ArrayList tempList=new ArrayList();
			while(rowSet.next())
			{
				seq_no="";
				String _ori_item_value=rowSet.getString(_ori_item_id);
				String _value=rowSet.getString(_item_id);
				if(_value==null)
					_value="";
				String key_value=rowSet.getString(key);
				String db_value="";
				if(dbpre_item.length()>0)
					db_value=rowSet.getString(dbpre_item);
				if(_ori_item_value!=null&&_ori_item_value.trim().length()>0)
					continue;
				String ins_id="";
				if(ins_item.length()>0)
					ins_id="_"+rowSet.getInt(ins_item);
				if(map.get(key_value+db_value+ins_id)==null)
				{
					if(length!=0&&_value.length()>length)
					{
						_value=_value.substring(0, length);
					}
					if(_value.length()>0&& "1".equals(sortNo)&&length>0)
					{
						try
						{
							seq_no=idg.getId(sequenceID+"_"+_value);
						}
						catch(Exception ee)
						{
							seq_no=idg.getId(sequenceID);
						}
					}
					else
						seq_no=idg.getId(sequenceID);
					tempList=new ArrayList();
					tempList.add(_value+seq_no);
					//tempList.add(seq_no);
					tempList.add(key_value);
					if(dbpre_item.length()>0)
					{
						if(this.DbPre!=null&&this.DbPre.trim().length()>0)
							tempList.add(this.DbPre.toLowerCase());
						else if(TempTableName.length()>8&& TempTableName.toLowerCase().indexOf("templet_")!=-1) //人事异动
						{
							tempList.add(rowSet.getString(dbpre_item).toLowerCase());
						} 
					}
					if(ins_item.length()>0)
					{
						tempList.add(rowSet.getInt(ins_item));
					}
					list.add(tempList);
					map.put(key_value+db_value+ins_id, "1");
				}
			}
			dao.batchUpdate(sql2, list);
			if(rowSet!=null)
				rowSet.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	/** _ori_item_id,_item_id
	 * 序号(目标指标，参考值指标,2,"A01.A0104");
	 */
	private boolean getParamValue_sequence(LazyDynaBean abean, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		HashMap _usedFieldMap = this.mapUsedFieldItems;
		ArrayList _usedSets = this.UsedSets;
		
		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if ("D".equalsIgnoreCase(Field.getItemtype())|| "M".equalsIgnoreCase(Field.getItemtype())) {
			Putback();
			SError(E_MUSTBESTRING);
			return false;
		}
		abean.set("_ori_item_id", Field.getItemid());
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if ("D".equalsIgnoreCase(Field.getItemtype())|| "M".equalsIgnoreCase(Field.getItemtype())) {
			Putback();
			SError(E_MUSTBESTRING);
			return false;
		}
		abean.set("_item_id", Field.getItemid());
		
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		abean.set("length",this.token);
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != QUOTE) {
			Putback();
			SError(E_MUSTBESTRING);
			return false;
		}
		abean.set("sequenceID",this.token);
		
		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT||!("0".equals(this.token)|| "1".equals(this.token))) {
			Putback();
			SError(E_SYNTAX);
			return false;
		}
		abean.set("sortNo",this.token); //按截取内容分类编号
		 
		
		this.mapUsedFieldItems = new HashMap();
		this.mapUsedFieldItems = _usedFieldMap;
		this.UsedSets.clear();
		this.UsedSets = _usedSets;
		if (!Get_Token())
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		return true;
	}
	
	/**
	 * 序号(源指标,目标指标,2,"A01.A0104")
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_Sequence(RetValue retValue)throws GeneralException,SQLException
	{
		
		if (ModeFlag == this.forSearch) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		
		sequenceParamBean=new LazyDynaBean();
		getParamValue_sequence(sequenceParamBean,retValue);
		
		FieldItem field1 = new FieldItem();
		field1.setItemid("SEQUENCE_NUMBER");
		field1.setItemdesc("序号");
		field1.setDecimalwidth(0);
		field1.setItemlength(30);
		field1.setItemtype("A");
		field1.setCodesetid("0");
		field1.setVarible(2);
	
	 
		result = Sql_switcher.isnull(field1.getItemid(), "''");
		SQL.append(result);
		retValue.setValue("''");
		retValue.setValueType(this.STRVALUE);
		return Get_Token();
	}
	
	
	
	/**
	 * 取自于
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean isGetFrom=false;
	private boolean Func_GetFrom(RetValue retValue)throws GeneralException,SQLException
	{
		  
		SQL.append("NULL");
		isGetFrom=true;
		
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
 	
		if (!Get_Token())
			return false;
		
		if (!Get_Token())
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		
		retValue.setValue("''");
		retValue.setValueType(this.STRVALUE);
		return Get_Token();
	}
	
	
	/**
	 * 函数名:统计月数(日期指标,条件表达式)
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_StatMonth(RetValue retValue) throws GeneralException,SQLException {
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		
		
		String str = SQL.toString();
		LazyDynaBean abean = new LazyDynaBean();
		if (!getParamValue_statMonth(abean, retValue)) {
			return false;
		}

		SQL.setLength(0);
		SQL.append(str);
		String tempTableName2 ="t#"+this.userView.getUserName()+"_sf_stat1"; // "statTime_" + this.TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
			tempTableName2 = "##" + tempTableName2;
		String result = "";
		FieldItem field1 = new FieldItem();
		field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));

		field1.setItemdesc("统计月数");
		field1.setDecimalwidth(0);
		field1.setItemlength(10);
		field1.setItemtype("N");
		field1.setCodesetid("0");
		field1.setVarible(2);// nIsVar := 2;
		mapUsedFieldItems.put(field1.getItemid(), field1);
		usedFieldMap.put(field1.getItemid(), field1);

	 
		executeStatMonthSqls(abean, tempTableName2, field1);
		result =GetCurMenu(true, field1);
	//	result = Sql_switcher.isnull(field1.getItemid(), "0");
		SQL.append(result);
		retValue.setValue(new Integer(123));
		retValue.setValueType(INT);
	 
		return Get_Token();
	}
	
	
	
	
	/**
	 * 产生统计月数的sql语句
	 * 
	 * @param abean
	 * @param tablename2
	 */
	public void executeStatMonthSqls(LazyDynaBean abean, String tablename2,
			FieldItem field1) {
		String str_d = (String) abean.get("str_d");
		String current_set = (String) abean.get("current_set");
		String str_cond = (String) abean.get("str_cond");
		HashMap strcondFieldsMap = (HashMap) abean.get("strcondFieldsMap");
		String current_fieldStr=(String)abean.get("current_fieldStr");
		 
		String table_name=current_set;
		String _tempSql="drop table " + tablename2;
		if (DBType == Constant.ORACEL)
			_tempSql+=" purge";
		SQLS.add(_tempSql);
		String _tempTableName3 = "t#"+this.userView.getUserName()+"_sf_stat3"; //"statTime3_" + this.TempTableName;
		String _tempTableName2 = "t#"+this.userView.getUserName()+"_sf_stat2"; //"statTime2_" + this.TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
		{
			_tempTableName2 = "##" + _tempTableName2;
			_tempTableName3 = "##" + _tempTableName3;
		}
		_tempSql="drop table " + _tempTableName3;
		if (DBType == Constant.ORACEL)
			_tempSql+=" purge";
		SQLS.add(_tempSql);
		_tempSql="drop table " + _tempTableName2;
		if (DBType == Constant.ORACEL)
			_tempSql+=" purge";
		SQLS.add(_tempSql);
		StringBuffer sql0=new StringBuffer("");
		StringBuffer sql_where=new StringBuffer("");
		if (InfoGroupFlag == forPerson) { 
			if (!("()".equalsIgnoreCase(whereText))
					&& whereText.trim().length() > 0)
				sql_where.append(" where  A0100 in "
						+ whereText);
			table_name=this.DbPre+table_name;	
		} else if (InfoGroupFlag == forUnit) {
			 
			if (whereText != null && whereText.trim().length() > 0
					&& !("()".equalsIgnoreCase(whereText)))
				sql_where.append(" where " + "B0110 in "
						+ whereText);
		} else if (InfoGroupFlag == forPosition) {
			if (whereText != null && whereText.trim().length() > 0
					&& !("()".equalsIgnoreCase(whereText)))
				sql_where.append(" where " + "E01A1 in "
						+ whereText);
		}
		if(DBType==Constant.ORACEL)
			sql0.append("create table " + _tempTableName2 + " as select "+current_fieldStr+" from "+table_name+sql_where.toString());
		else 
			sql0.append("select "+current_fieldStr+" into "+_tempTableName2+" from "+table_name+sql_where.toString());
		SQLS.add(sql0.toString());
		
		if("A01".equalsIgnoreCase(current_set)|| "B01".equalsIgnoreCase(current_set)|| "K01".equalsIgnoreCase(current_set))
		{
			String str="alter table "+_tempTableName2+" ADD i9999 "+Sql_switcher.getFieldType('N', 8,0);
			SQLS.add(str);
			SQLS.add("update "+_tempTableName2+" set i9999=1");
 		}
		
		
		FieldItem fieldTemp = null;
		if (strcondFieldsMap.size()>0) {
			StringBuffer _sql = new StringBuffer("");
			Iterator it = strcondFieldsMap.keySet().iterator();
			while (it.hasNext()) {
				fieldTemp = (FieldItem)((FieldItem) strcondFieldsMap.get((String) it.next())).cloneItem();
			
				if(fieldTemp.getFieldsetid().equalsIgnoreCase(current_set))
					continue;
				
				String fieldname = fieldTemp.getItemid().toUpperCase();
				if (fieldname.indexOf("SELECT_") != -1)
					continue;
				if (fieldname.indexOf("GET_") != -1) 
					continue;
				if (fieldname.indexOf("STD_") != -1)
					continue;
				
				StringBuffer strR = new StringBuffer();
				StringBuffer strSQL = new StringBuffer();
				strR.append(fieldname);
				strR.append(" ");
				strR.append(Sql_switcher.getFieldType(fieldTemp.getItemtype()
						.charAt(0), fieldTemp.getItemlength(), fieldTemp
						.getDecimalwidth()));

				strSQL.append(" alter table ");
				strSQL.append(_tempTableName2);
				strSQL.append(" drop column ");
				strSQL.append(fieldname);
				// run_SQL(strSQL.toString());
				this.SQLS.add(strSQL.toString());
				strSQL.setLength(0);
				strSQL.append("alter table ");
				strSQL.append(_tempTableName2);
				strSQL.append(" ADD ");
				strSQL.append(strR.toString());
				this.SQLS.add(strSQL.toString());
				_sql.setLength(0);
				
				
				String joinStr1="";
				String joinStr2="";
				String select_str="";
				String tbName=fieldTemp.getFieldsetid();
				if (InfoGroupFlag == forPerson) { 
					joinStr1="a.a0100=b.a0100";
					joinStr2=_tempTableName2+".a0100=aa.a0100";
					tbName=this.DbPre+tbName;
					select_str="a0100,"+fieldname;
				} else if (InfoGroupFlag == forUnit) {
					joinStr1="a.b0110=b.b0110";
					joinStr2=_tempTableName2+".b0110=aa.b0110";
					select_str="b0110,"+fieldname;
				} else if (InfoGroupFlag == forPosition) {
					joinStr1="a.e01a1=b.e01a1";
					joinStr2=_tempTableName2+".e01a1=aa.e01a1";
					select_str="e01a1,"+fieldname;
				}
				
				
				if (fieldTemp.getVarible()== 0)
				{
					_sql.append("update "+_tempTableName2+" set "+fieldname+"=(select aa."+fieldname+" from  ");
					_sql.append("(select "+select_str+" from "+tbName+" a where a.i9999=(select max(b.i9999) from "+tbName+" b where "+joinStr1+") ) aa where "+joinStr2+" ) ");
					_sql.append("where exists ( select null  from  ");
					_sql.append("(select "+select_str+" from "+tbName+" a where a.i9999=(select max(b.i9999) from "+tbName+" b where "+joinStr1+") ) aa where "+joinStr2+" ) ");
					this.SQLS.add(_sql.toString());
				}
				else if(this.StdTmpTable!=null&&this.StdTmpTable.length()>0)
				{
					_sql.append("update "+_tempTableName2+" set "+fieldname+"=(select "+fieldname+" from "+StdTmpTable+" aa where "+joinStr2+" ) ");
					_sql.append("where exists ( select null  from  ");
					_sql.append( StdTmpTable+" aa where "+joinStr2+" ) ");
					this.SQLS.add(_sql.toString());
				}
				
			}
		}
		
		String key="";
		if (InfoGroupFlag == forPerson) { 
			key="a0100";
		} else if (InfoGroupFlag == forUnit) {
			key="b0110";
		} else if (InfoGroupFlag == forPosition) {
			key="e01a1";
		}
		
		if(DBType==Constant.ORACEL)
			this.SQLS.add("create table " + _tempTableName3 + " as select * from "+_tempTableName2);
		else
			this.SQLS.add("select * into "+_tempTableName3+" from "+_tempTableName2);
		
		String str=" and "+_tempTableName2+".i9999="+_tempTableName3+".i9999";
		
		this.SQLS.add("delete from "+_tempTableName2+"   where not exists (select null from "+_tempTableName3+"   where  "+str_cond+ " and  "+_tempTableName2+"."+key+"="+_tempTableName3+"."+key+str+") ");
		StringBuffer sql=new StringBuffer("");
		
		if(DBType==Constant.ORACEL)
		{
			sql.append(" create table "+tablename2+" as select a.*  from "+_tempTableName2+" a ");
			sql.append(" where a.i9999=(select min(i9999) from "+_tempTableName2+" b where a."+key+"=b."+key+" and a."+str_d+"=b."+str_d+" ) ");
		}
		else
		{
			sql.append(" select a.*  into "+tablename2+" from "+_tempTableName2+" a ");
			sql.append(" where a.i9999=(select min(i9999) from "+_tempTableName2+" b where a."+key+"=b."+key+" and a."+str_d+"=b."+str_d+" ) ");
		}
		this.SQLS.add(sql.toString());
		
		sql.setLength(0);
		sql.append("update  " + this.TempTableName + " set  "
				+ field1.getItemid() + "=( select d from  ");
		sql.append(" (select count(" + key + ") d,"+key+" from "
				+ tablename2 +"  group by  "+key+" ) a where a."+key+"="
				+ this.TempTableName
				+ "."+key+" ) where exists (  select null from  ");
		sql.append(" (select count(" + key + ") d,"+key+" from "
				+ tablename2+"   group by  "+key+" ) a where a."+key+"="
				+ this.TempTableName
				+ "."+key+"  ) ");
		SQLS.add(sql.toString());
		
		
	}
	
	
	
	
	
	/**
	 * 统计月数(日期指标,条件表达式)
	 */

	// 取得表达式中的各参数值
	private boolean getParamValue_statMonth(LazyDynaBean abean, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		HashMap _usedFieldMap = this.mapUsedFieldItems;
		ArrayList _usedSets = this.UsedSets;
		
		String fieldsetid = "";
		SQL.setLength(0); // 条件
		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if (!"D".equalsIgnoreCase(Field.getItemtype())) {
			Putback();
			SError(E_MUSTBETIMEFIELD);
			return false;
		}
		String str_d= Field.getItemid();
		abean.set("str_d", Field.getItemid());
		fieldsetid =Field.getFieldsetid();
		abean.set("current_set",fieldsetid);
		

		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		this.mapUsedFieldItems = new HashMap();
		SQL.setLength(0); // 条件
		if (!Get_Token()) {
			return false;
		}
		if (tok == S_RPARENTHESIS) {
			abean.set("str_cond", "1=1");
			abean.set("strcondFieldsMap",new HashMap());
		} else {

			boolean _bDivFlag = bDivFlag; // 除数标示
			bDivFlag = false;
			this.mapUsedFieldItems = new HashMap();
			if (!level0(retValue)) {
				return false;
			}
			abean.set("str_cond", SQL.toString());
			bDivFlag = _bDivFlag;
			abean.set("strcondFieldsMap", this.mapUsedFieldItems); // 如果为非当前列表 有效

			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		
		HashMap strcondFieldsMap=(HashMap)abean.get("strcondFieldsMap");
		Set keySet=strcondFieldsMap.keySet();
		FieldItem fieldTemp = null;
		
		StringBuffer fieldStr=new StringBuffer("");
		if (InfoGroupFlag == forPerson) {
			fieldStr.append("A0100");
		} else if (InfoGroupFlag == forUnit) {
			fieldStr.append("B0110");
		} else if (InfoGroupFlag == forPosition) {
			fieldStr.append("E01A1");
		}
		for(Iterator t=keySet.iterator();t.hasNext();)
		{
			String key=(String)t.next();
			fieldTemp=(FieldItem)strcondFieldsMap.get(key);
			if (fieldTemp.getVarible()==0)
			{
				if(fieldTemp.getFieldsetid().equalsIgnoreCase(fieldsetid))
					fieldStr.append(","+fieldTemp.getItemid());
			}
		}
		if((","+fieldStr.toString()+",").toLowerCase().indexOf(","+str_d.toLowerCase()+",")==-1)
			fieldStr.append(","+str_d);
		if(!("A01".equalsIgnoreCase(fieldsetid)|| "B01".equalsIgnoreCase(fieldsetid)|| "K01".equalsIgnoreCase(fieldsetid)))
				fieldStr.append(",i9999");
			
		abean.set("current_fieldStr",fieldStr.toString());
		
		this.mapUsedFieldItems = new HashMap();
		this.mapUsedFieldItems = _usedFieldMap;
		this.UsedSets.clear();
		this.UsedSets = _usedSets;
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * 函数名: 统计时间(开始时间, 结束时间, 条件, 表达式()) ; // 表达式中只能用这两个时间或其它常量) ) 表达式不支持临时变量
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_StatTime(RetValue retValue) throws GeneralException,
			SQLException {

		/**
		 * str1:开始时间 str2:结束时间 str3:条件 str4:表达式
		 */
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}

		HashMap str1FieldsMap = null;
		RetValue hold = new RetValue();
		String str = SQL.toString();
		LazyDynaBean abean = new LazyDynaBean();
		if (!getParamValue_statTime(abean, retValue)) {
			return false;
		}

		SQL.setLength(0);
		SQL.append(str);
		String tempTableName2 ="t#"+this.userView.getUserName()+"_sf_stat1"; // "statTime_" + this.TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
			tempTableName2 = "##" + tempTableName2;
		String result = "";
		FieldItem field1 = new FieldItem();
		field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));

		field1.setItemdesc("统计时间");
		field1.setDecimalwidth(0);
		field1.setItemlength(10);
		field1.setItemtype("N");
		field1.setCodesetid("0");
		field1.setVarible(2);// nIsVar := 2;
		mapUsedFieldItems.put(field1.getItemid(), field1);
		usedFieldMap.put(field1.getItemid(), field1);

		String goalItem = field1.getItemid();

		/*
		 * StringBuffer strSQL=new StringBuffer(""); strSQL.append("UPDATE
		 * ").append(TempTableName); strSQL.append(" SET
		 * ").append(TempTableName).append(".").append( goalItem).append("=");
		 * strSQL.append(getSubSql_groupStat(tempTableName2,TempTableName,str5,Sql_switcher.isnull("goupValue",
		 * "0"))); strSQL.append(" where exists
		 * "+getSubSql_groupStat(tempTableName2,TempTableName,str5,"null")); if
		 * (!strSQL.toString().equals("")) SQLS.add(strSQL.toString());
		 */
		executeStatTimeSqls(abean, tempTableName2, field1);

		result = Sql_switcher.isnull(field1.getItemid(), "0");
		SQL.append(result);
		retValue.setValue(new Integer(123));
		retValue.setValueType(INT);
		return Get_Token();
	}

	public boolean isSequence(String name) {
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(this.con);

			RowSet rowSet = dao
					.search("select sequence_name from user_sequences where lower(sequence_name)='"
							+ name.toLowerCase() + "'");
			if (rowSet.next())
				flag = true;
			if(rowSet!=null)
				rowSet.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 产生统计时间的sql语句
	 * 
	 * @param abean
	 * @param tablename2
	 */
	public void executeStatTimeSqls(LazyDynaBean abean, String tablename2,
			FieldItem field1) {
		String str1 = (String) abean.get("str1");
		String str2 = (String) abean.get("str2");
		String str3 = (String) abean.get("str3");
		HashMap str3FieldsMap = (HashMap) abean.get("str3FieldsMap");

		String str4 = (String) abean.get("str4");
		String _tempSql="drop table " + tablename2;
		if (DBType == Constant.ORACEL)
			_tempSql+=" purge";
		SQLS.add(_tempSql);
		String _tempTableName2 ="t#"+this.userView.getUserName()+"_sf_stat2"; // "statTime2_" + this.TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
			_tempTableName2 = "##" + _tempTableName2;

		FieldItem fieldTemp = null;
		if (str3FieldsMap.size() > 0 && this.isSupportVar
				&& this.InfoGroupFlag == YksjParser.forPerson) {
			String tmptable ="t#"+this.userView.getUserName() + "_sf_mid"; // this.userView.getUserName() + "midtable";
			StringBuffer _sql = new StringBuffer("");
			Iterator it = str3FieldsMap.keySet().iterator();
			while (it.hasNext()) {
				fieldTemp = (FieldItem) str3FieldsMap.get((String) it.next());
				String fieldname = fieldTemp.getItemid().toUpperCase();
				if (fieldTemp.getVarible() != 1)
					continue;
				if (fieldname.indexOf("SELECT_") != -1)
					continue;
				if (fieldname.indexOf("STD_") != -1)
					continue;

				StringBuffer strR = new StringBuffer();
				StringBuffer strSQL = new StringBuffer();
				strR.append(fieldname);
				strR.append(" ");
				strR.append(Sql_switcher.getFieldType(fieldTemp.getItemtype()
						.charAt(0), fieldTemp.getItemlength(), fieldTemp
						.getDecimalwidth()));

				strSQL.append(" alter table ");
				strSQL.append(this.TempTableName);
				strSQL.append(" drop column ");
				strSQL.append(fieldname);
				// run_SQL(strSQL.toString());
				this.SQLS.add(strSQL.toString());
				strSQL.setLength(0);
				strSQL.append("alter table ");
				strSQL.append(this.TempTableName);
				strSQL.append(" ADD ");
				strSQL.append(strR.toString());
				this.SQLS.add(strSQL.toString());

				_sql.setLength(0);
				_sql.append("update " + this.TempTableName + " set "
						+ fieldname + "=");
				_sql.append(" ( select " + fieldname + " from " + tmptable
						+ " where " + this.TempTableName + ".a0100=" + tmptable
						+ ".a0100 ) where exists ");
				_sql.append(" ( select null from " + tmptable + " where "
						+ this.TempTableName + ".a0100=" + tmptable
						+ ".a0100 ) ");
				this.SQLS.add(_sql.toString());
			}
		}

		FieldItem item = DataDictionary.getFieldItem(str1.toLowerCase());
		String setid = item.getFieldsetid();
		String tb_name = this.DbPre + setid;
		DbWizard db = new DbWizard(this.con);
		StringBuffer sql = new StringBuffer("");
		switch (DBType) {
		case Constant.ORACEL:
			if (isSequence("xxxx" + this.userView.getUserName())) {
				SQLS.add("drop sequence xxxx" + this.userView.getUserName());
			}
			SQLS.add("create sequence xxxx" + this.userView.getUserName()
					+ " increment by 1 start with 1");

			sql.append(" create table " + tablename2 + " as  select " + tb_name
					+ ".A0100, " + tb_name + ".I9999," + str1
					+ " as bdate, case when " + str2 + " is null then "
					+ Sql_switcher.today() + " else " + str2
					+ " end as edate, 1000000 as id ,  0 as nflag, 0 as nid  ");
			sql.append(" from  " + tb_name + "," + this.TempTableName
					+ " where " + tb_name + ".a0100=" + this.TempTableName
					+ ".a0100");
			sql.append(" AND  " + str1 + " is not null  and " + str1
					+ "<=(case when " + str2 + " is null then "
					+ Sql_switcher.today() + " else " + str2 + " end ) and ( "
					+ str3 + " )"); // -- and 条件 bdate比edate大的是非法数据, 不考虑
			sql.append(" order by " + tb_name + ".A0100, " + str1 + " ");
			SQLS.add(sql.toString());
			
			sql.setLength(0);
			sql.append(" delete from " + tablename2 + " where not exists (select null from ( ");
			sql.append(" select a.* from " + tablename2 + " a where a.i9999=(select min(i9999) from " + tablename2 + " b where a.a0100=b.a0100 and a.bdate=b.bdate and a.edate=b.edate)"); 
			sql.append(" ) c where " + tablename2 + ".a0100=c.a0100 and " + tablename2 + ".i9999=c.i9999 ) ");
			SQLS.add(sql.toString());		 

			SQLS.add("update " + tablename2 + " set id=xxxx"
					+ this.userView.getUserName() + ".nextval");
			break;
		default:
			sql
					.append(" select "
							+ tb_name
							+ ".A0100, "
							+ tb_name
							+ ".I9999,"
							+ str1
							+ " as bdate, case when "
							+ str2
							+ " is null then  "
							+ Sql_switcher.today()
							+ "  else "
							+ str2
							+ " end as edate, identity(int, 1,1) as id ,  0 as nflag, 0 as nid  into  "
							+ tablename2);
			sql.append(" from  " + tb_name + "," + this.TempTableName
					+ " where " + tb_name + ".a0100=" + this.TempTableName
					+ ".a0100");
			sql.append(" AND " + str1 + " is not null  and " + str1
					+ "<=(case when " + str2 + " is null then  "
					+ Sql_switcher.today() + "  else " + str2 + " end ) and ( "
					+ str3 + " )"); // -- and 条件 bdate比edate大的是非法数据, 不考虑
			sql.append(" order by " + tb_name + ".A0100, " + str1 + " ");
			SQLS.add(sql.toString());
			
			sql.setLength(0);
			sql.append(" delete from " + tablename2 + " where not exists (select null from ( ");
			sql.append(" select a.* from " + tablename2 + " a where a.i9999=(select min(i9999) from " + tablename2 + " b where a.a0100=b.a0100 and a.bdate=b.bdate and a.edate=b.edate)"); 
			sql.append(" ) c where " + tablename2 + ".a0100=c.a0100 and " + tablename2 + ".i9999=c.i9999 ) ");
			SQLS.add(sql.toString());	
			break;
		}

		SQLS.add("update " + tablename2 + " set nflag=0, nid=id");
		sql.setLength(0);
		switch (DBType) {
		case Constant.ORACEL:
			sql.append(" update " + tablename2 + " B set nflag=(select 1 ");
			sql.append(" from " + tablename2 + " A ");
			sql.append(" where A.A0100=B.A0100 AND A.id<b.id AND B.bdate >=A.bdate AND B.bdate <=A.edate+1 ) where exists (select null");
			sql.append(" from " + tablename2 + " A ");
			sql.append(" where A.A0100=B.A0100 AND A.id<b.id AND B.bdate >=A.bdate AND B.bdate <=A.edate+1 ) ");
			SQLS.add(sql.toString());
			sql.setLength(0);

			sql.append(" update " + tablename2
					+ " C set C.nid=(select D.nid FROM ");
			sql.append(" ( select A.id, max(B.nid) as nid from " + tablename2
					+ " A,  (select id as nid from " + tablename2
					+ " where   "+Sql_switcher.isnull("nflag","0")+"=0) B ");
			sql.append(" where A.id > B.nid and nflag = 1 ");
			sql.append(" group by A.id )  D ");
			sql
					.append(" WHERE C.id=D.id and "+Sql_switcher.isnull("nflag","0")+"=1 ) where exists ( select null FROM ");
			sql.append(" ( select A.id, max(B.nid) as nid from " + tablename2
					+ " A,  (select id as nid from " + tablename2
					+ " where nflag=0) B ");
			sql.append(" where A.id > B.nid and "+Sql_switcher.isnull("nflag","0")+" = 1 ");
			sql.append(" group by A.id )  D ");
			sql.append(" WHERE C.id=D.id and "+Sql_switcher.isnull("nflag","0")+"=1 ) ");
			SQLS.add(sql.toString());
			sql.setLength(0);

			sql
					.append("update "
							+ tablename2
							+ "  a set a.nid=(select min(b.nid) from "
							+ tablename2
							+ " b  where a.a0100=b.a0100 and a.bdate=b.bdate and a.edate=b.edate) ");
			sql
					.append(" where exists (select null from "
							+ tablename2
							+ " b  where a.a0100=b.a0100 and a.bdate=b.bdate and a.edate=b.edate) ");
			SQLS.add(sql.toString());
			sql.setLength(0);
			_tempSql="drop table " + _tempTableName2;
			if (DBType == Constant.ORACEL)
				_tempSql+=" purge";
			SQLS.add(_tempSql);
			sql.append("create table  " + _tempTableName2
					+ "  as select A0100, nid, min(bdate) as " + str1
					+ ", max(edate) as " + str2 + " ");
			sql.append(" from  " + tablename2);
			sql.append(" group by A0100, nid ");
			;
			SQLS.add(sql.toString());
			sql.setLength(0);
			sql.append("update  " + this.TempTableName + " set  "
					+ field1.getItemid() + "=( select d from  ");
			sql.append(" (select sum(" + str4 + ") d,a0100 from "
					+ _tempTableName2 + " group by a0100) a where a.a0100="
					+ this.TempTableName
					+ ".a0100 ) where exists (  select null from  ");
			sql.append(" (select sum(" + str4 + ") d,a0100 from "
					+ _tempTableName2 + " group by a0100) a where a.a0100="
					+ this.TempTableName + ".a0100 ) ");
			SQLS.add(sql.toString());

			break;
		default:
			sql.append(" update B set nflag=1 ");
			sql.append(" from " + tablename2 + " A, " + tablename2 + " B ");
			sql
					.append(" where A.A0100=B.A0100 AND A.id<b.id AND B.bdate >=A.bdate AND B.bdate <=A.edate+1 ");
			SQLS.add(sql.toString());
			sql.setLength(0);

			sql.append(" update C set C.nid=D.nid	FROM ");
			sql.append(" " + tablename2
					+ " C,  ( select A.id, max(B.nid) as nid from "
					+ tablename2 + " A,  (select id as nid from " + tablename2
					+ " where "+Sql_switcher.isnull("nflag","0")+"=0) B ");
			sql.append(" where A.id > B.nid and "+Sql_switcher.isnull("nflag","0")+" = 1 ");
			sql.append(" group by A.id )  D ");
			sql.append(" WHERE C.id=D.id and "+Sql_switcher.isnull("nflag","0")+"=1 ");
			SQLS.add(sql.toString());
			sql.setLength(0);

			sql
					.append("update "
							+ tablename2
							+ "    set  nid=(select min(b.nid) from "
							+ tablename2
							+ " b  where "+tablename2+".a0100=b.a0100 and "+tablename2+".bdate=b.bdate and "+tablename2+".edate=b.edate) ");
			sql
					.append(" where exists (select null from "
							+ tablename2
							+ " b  where "+tablename2+".a0100=b.a0100 and "+tablename2+".bdate=b.bdate and "+tablename2+".edate=b.edate) ");
			SQLS.add(sql.toString());
			sql.setLength(0);
			_tempSql="drop table " + _tempTableName2;
			if (DBType == Constant.ORACEL)
				_tempSql+=" purge";
			SQLS.add(_tempSql);
			sql.append("select A0100, nid, min(bdate) as " + str1
					+ ", max(edate) as " + str2 + " into  " + _tempTableName2);
			sql.append(" from  " + tablename2);
			sql.append(" group by A0100, nid ");
			;
			SQLS.add(sql.toString());
			sql.setLength(0);
			sql.append("update  " + this.TempTableName + " set  "
					+ field1.getItemid() + "=( select d from  ");
			sql.append(" (select sum(" + str4 + ") d,a0100 from "
					+ _tempTableName2 + " group by a0100) a where a.a0100="
					+ this.TempTableName + ".a0100 )");
			SQLS.add(sql.toString());

			break;
		}

		// select sum(dfadsf) from kdfdsf group by a0100
	}

	/**
	 * 统计时间(开始时间, 结束时间, 条件, 表达式()) ;
	 */

	// 取得表达式中的各参数值
	private boolean getParamValue_statTime(LazyDynaBean abean, RetValue retValue)
			throws GeneralException, SQLException {
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		HashMap _usedFieldMap = this.mapUsedFieldItems;
		ArrayList _usedSets = this.UsedSets;
		String fieldsetid = "";
		SQL.setLength(0); // 条件
		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if (!"D".equalsIgnoreCase(Field.getItemtype())) {
			Putback();
			SError(E_MUSTBETIMEFIELD);
			return false;
		}
		abean.set("str1", Field.getItemid());
		fieldsetid = ((FieldItem) Field.cloneItem()).getFieldsetid();

		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		if (!Get_Token())
			return false;
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if (!"D".equalsIgnoreCase(Field.getItemtype())) {
			Putback();
			SError(E_MUSTBETIMEFIELD);
			return false;
		}
		abean.set("str2", Field.getItemid());

		if (!(((FieldItem) Field.cloneItem()).getFieldsetid())
				.equalsIgnoreCase(fieldsetid)) {
			Putback();
			SError(E_MUSTBESAMESET);
			return false;
		}

		if (!Get_Token())
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		this.mapUsedFieldItems = new HashMap();
		SQL.setLength(0); // 条件
		if (!Get_Token()) {
			return false;
		}
		if (tok == S_COMMA) {
			abean.set("str3", "1=1");
		} else {

			boolean _bDivFlag = bDivFlag; // 除数标示
			bDivFlag = false;
			this.mapUsedFieldItems = new HashMap();
			if (!level0(retValue)) {
				return false;
			}
			abean.set("str3", SQL.toString());
			bDivFlag = _bDivFlag;
			abean.set("str3FieldsMap", this.mapUsedFieldItems); // 如果为非当前列表 有效

			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
		}
		SQL.setLength(0); // 条件
		if (!Get_Token())
			return false;
		boolean _bDivFlag = bDivFlag; // 除数标示
		bDivFlag = false;
		this.mapUsedFieldItems = new HashMap();
		if (!level0(retValue)) {
			return false;
		}
		bDivFlag = _bDivFlag;
		abean.set("str4", this.SQL.toString());
		abean.set("str4FieldsMap", this.mapUsedFieldItems); // 如果为非当前列表 有效

		this.mapUsedFieldItems = new HashMap();
		this.mapUsedFieldItems = _usedFieldMap;
		this.UsedSets.clear();
		this.UsedSets = _usedSets;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		return true;
	}
	
	
	boolean isPartTimeJobParam=false;
	/**
	 * 取兼职信息
	 * @param retValue
	 * @return
	 */
	private boolean Func_PartTimeJob(RetValue retValue)throws GeneralException, SQLException
	{
		
		String str, str1="";
		LazyDynaBean paramBean=new LazyDynaBean();
		
		//BS界面人员信息浏览、人员信息维护的标签修改
		 Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
		 String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
		 String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");//兼职子集
		 /**兼职单位字段*/
		 String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
		 //兼职部门
		 String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept"); 
		 //兼任兼职
		 String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
		 /**任免标识字段*/
		 String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint"); 
		 //兼任排序
		 String order_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"order");
		 /**兼职内容显示格式*/
		 String format_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"format");
		 if(setid.length()==0||pos_field.length()==0||appoint_field.length()==0)//||order_field.length()==0||format_field.length()==0)
		 {
			 	SError(E_PARTTIMEJOBPARAM);
				return false;
		 }
		
		 paramBean.set("flag",flag);
		 paramBean.set("setid",setid);
		 paramBean.set("unit_field",unit_field);
		 paramBean.set("dept_field",dept_field);
		 paramBean.set("pos_field",pos_field);
		 paramBean.set("appoint_field",appoint_field);
		 paramBean.set("order_field",order_field);
		 paramBean.set("format_field",format_field);
		
		
		RetValue hold = new RetValue();
		str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		this.isPartTimeJobParam=true;
		
		
		if (!Get_Token())
			return false;
		if (tok != S_RPARENTHESIS) { 
		
			/*
			if (!level0(retValue))
				return false;
			
			if (!retValue.IsStringType()) {
				SError(E_UNKNOWNSTR);
				return false;
			}
			str1 =(String)retValue.get("RetValueKey");
			*/
		 
			str1=this.token;
			if (!Get_Token())
				return false;
			if("单位部门".equalsIgnoreCase(str1)|| "单位".equalsIgnoreCase(str1)|| "部门".equalsIgnoreCase(str1))
			{
				if("单位部门".equalsIgnoreCase(str1))
				{
					 if(unit_field.length()==0||dept_field.length()==0)
					 {
						 	SError(E_PARTTIMEJOBPARAM);
							return false;
					 }
				}
				else if("单位".equalsIgnoreCase(str1))
				{
					 if(unit_field.length()==0)
					 {
						 	SError(E_PARTTIMEJOBPARAM);
							return false;
					 }
				}
				else if("部门".equalsIgnoreCase(str1))
				{
					 if(dept_field.length()==0)
					 {
						 	SError(E_PARTTIMEJOBPARAM);
							return false;
					 }
				}
			}
			else
			{
				SError(E_UNKNOWNSTR);
				return false;
			}
		}
		this.isPartTimeJobParam=false;
		 
		SQL.setLength(0);
		SQL.append(str);
		if (InfoGroupFlag != forPerson||isVerify|| "false".equalsIgnoreCase(flag))
		{
			SQL.append("''");
		}
		else
		{
			SQL.append(getPartTimeJobInfo(str1,paramBean));
		}
		
		
		
		
		
		retValue.setValue("");
		retValue.setValueType(STRVALUE);
		return Get_Token(); 
	}
	
	/**
	 * 获得兼职信息
	 * @param param1
	 * @return
	 */
	private String getPartTimeJobInfo(String param1,LazyDynaBean paramBean)
	{
		String result = "";
		
		try
		{
		
			ContentDAO dao = new ContentDAO(this.con);
			FieldItem field1 = new FieldItem();
			field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
			result = field1.getItemid();
			field1.setItemdesc("otherJobInfo");
			field1.setItemlength(200); 
			field1.setItemtype("A");
			field1.setCodesetid("0");
			field1.setVarible(2);// nIsVar := 2;
			mapUsedFieldItems.put(field1.getItemid(), field1);
			
			result=field1.getItemid();
			
			String setid=(String)paramBean.get("setid");
			String unit_field=(String)paramBean.get("unit_field");
			String dept_field=(String)paramBean.get("dept_field");
			String pos_field=(String)paramBean.get("pos_field");
			FieldItem posItem=DataDictionary.getFieldItem(pos_field);
			String codesetid="@K";
			if(posItem!=null)
				codesetid=posItem.getCodesetid();
			String flag=(String)paramBean.get("flag");
			String order_field=(String)paramBean.get("order_field");
			String format_field=(String)paramBean.get("format_field");
			String appoint_field=(String)paramBean.get("appoint_field");
			
			String tableName ="t#"+this.userView.getUserName()+"_otherJob";  
			createMidTable(field1,tableName);
			RowSet rowSet=dao.search("select * from "+tableName);
			RowSet rowSet2=null;
			StringBuffer sql=new StringBuffer("");
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.con);
			
			String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
				display_e0122="0";
			
			String seprartor=sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122, "sep");
			seprartor=seprartor!=null&&seprartor.length()>0?seprartor:"/";
			StringBuffer context=new StringBuffer("");
			ArrayList valueList=new ArrayList();
			while(rowSet.next())
			{
				sql.setLength(0);
				context.setLength(0);
				String nbase=rowSet.getString("nbase");
				String a0100=rowSet.getString("a0100");
				String setName=nbase+setid;
				
				if(order_field!=null&&order_field.trim().length()>0)
				{
					if("单位部门".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select min("+Sql_switcher.isnull(order_field,"-1")+") "+order_field+"_2,a0100,"+dept_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0' group by "+dept_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+dept_field+"=b."+dept_field+"   order by  "+order_field+"_2,"+Sql_switcher.isnull(order_field,"-1")+" ");
						 
					}
					else if("单位".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select min("+Sql_switcher.isnull(order_field,"-1")+") "+order_field+"_2,a0100,"+unit_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0'  group by "+unit_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+unit_field+"=b."+unit_field+"   order by  "+order_field+"_2,"+Sql_switcher.isnull(order_field,"-1")+" ");
						 
					}
					else if("部门".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select min("+Sql_switcher.isnull(order_field,"-1")+") "+order_field+"_2,a0100,"+dept_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0'  group by "+dept_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+dept_field+"=b."+dept_field+"   order by  "+order_field+"_2,"+Sql_switcher.isnull(order_field,"-1")+" ");
						 
					}
					else
					{
						sql.append("select "+setName+".* from "+setName);
						sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0'   order by  "+Sql_switcher.isnull(order_field,"-1")+" ");
						 
					}
				}
				else
				{
					if("单位部门".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select  a0100,"+dept_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0' group by "+dept_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+dept_field+"=b."+dept_field+"   ");
						 
					}
					else if("单位".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select a0100,"+unit_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0'  group by "+unit_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+unit_field+"=b."+unit_field+" ");
						 
					}
					else if("部门".equalsIgnoreCase(param1))
					{
						 sql.append("select "+setName+".* from "+setName+",");
						 sql.append("( select a0100,"+dept_field+" from "+setName+" where a0100='"+a0100+"' and  "+appoint_field+"='0'  group by "+dept_field+",a0100 ) b ");
						 sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0' and  "+setName+".a0100=b.a0100  and  "+setName+"."+dept_field+"=b."+dept_field+"  ");
						 
					}
					else
					{
						sql.append("select "+setName+".* from "+setName);
						sql.append(" where "+setName+".a0100='"+a0100+"' and "+setName+"."+appoint_field+"='0'  ");
						 
					}
				}
				
				
				rowSet2=dao.search(sql.toString());
				String groupValue="";
				int i=0;
				boolean begin=true;
				while(rowSet2.next())
				{
					String unit_field_value=rowSet2.getString(unit_field)!=null?rowSet2.getString(unit_field):"";
					String dept_field_value=rowSet2.getString(dept_field)!=null?rowSet2.getString(dept_field):"";
					String pos_field_value=rowSet2.getString(pos_field)!=null?rowSet2.getString(pos_field):"";
					pos_field_value=AdminCode.getCodeName(codesetid,pos_field_value);
					String format_field_value="";
					if(format_field!=null&&format_field.trim().length()>0)
					{
						format_field_value=rowSet2.getString(format_field)!=null?rowSet2.getString(format_field):"";
					 	format_field_value=format_field_value.replaceAll("\\\\n","\n");
					}
					if(param1.trim().length()>0)
					{
					
						if(i==0)
						{
							if("单位部门".equalsIgnoreCase(param1))
								groupValue=unit_field_value+dept_field_value;
							else if("部门".equalsIgnoreCase(param1))
								groupValue=dept_field_value;
							else if("单位".equalsIgnoreCase(param1))
								groupValue=unit_field_value;
						}
						 
						
						String groupValue_2="";
						if("单位部门".equalsIgnoreCase(param1))
							groupValue_2=unit_field_value+dept_field_value;
						else if("部门".equalsIgnoreCase(param1))
							groupValue_2=dept_field_value;
						else if("单位".equalsIgnoreCase(param1))
							groupValue_2=unit_field_value;
						
						if(groupValue_2.equals(groupValue))
						{
							if(begin)
							{
								if("单位部门".equalsIgnoreCase(param1))
								{
									unit_field_value=AdminCode.getCodeName("UN",unit_field_value);
									
									if(Integer.parseInt(display_e0122)==0)
										dept_field_value=AdminCode.getCodeName("UM",dept_field_value);
									else
									{ 
										CodeItem item=AdminCode.getCode("UM",dept_field_value,Integer.parseInt(display_e0122));
						    	    	if(item!=null) 
						    	    		dept_field_value=item.getCodename(); 
						    	    	else 
						    	    		dept_field_value=AdminCode.getCodeName("UM",dept_field_value);  
									}
									
									context.append(unit_field_value+seprartor+dept_field_value+seprartor+pos_field_value+" "+format_field_value);
								}
								else if("部门".equalsIgnoreCase(param1))
								{ 
									if(Integer.parseInt(display_e0122)==0)
										dept_field_value=AdminCode.getCodeName("UM",dept_field_value);
									else
									{ 
										CodeItem item=AdminCode.getCode("UM",dept_field_value,Integer.parseInt(display_e0122));
						    	    	if(item!=null) 
						    	    		dept_field_value=item.getCodename(); 
						    	    	else 
						    	    		dept_field_value=AdminCode.getCodeName("UM",dept_field_value);  
									}
									
									context.append(dept_field_value+seprartor+pos_field_value+" "+format_field_value);
								}
								else if("单位".equalsIgnoreCase(param1))
								{
									unit_field_value=AdminCode.getCodeName("UN",unit_field_value);
									context.append(unit_field_value+seprartor+pos_field_value+" "+format_field_value);
								}
								begin=false;
							}
							else
							{
								
								context.append(pos_field_value+" "+format_field_value);
							}
						}
						else
						{
							if("单位部门".equalsIgnoreCase(param1))
							{
								unit_field_value=AdminCode.getCodeName("UN",unit_field_value);
								if(Integer.parseInt(display_e0122)==0)
									dept_field_value=AdminCode.getCodeName("UM",dept_field_value);
								else
								{ 
									CodeItem item=AdminCode.getCode("UM",dept_field_value,Integer.parseInt(display_e0122));
					    	    	if(item!=null) 
					    	    		dept_field_value=item.getCodename(); 
					    	    	else 
					    	    		dept_field_value=AdminCode.getCodeName("UM",dept_field_value);  
								}
								context.append(unit_field_value+seprartor+dept_field_value+seprartor+pos_field_value+" "+format_field_value);
							}
							else if("部门".equalsIgnoreCase(param1))
							{ 
								if(Integer.parseInt(display_e0122)==0)
									dept_field_value=AdminCode.getCodeName("UM",dept_field_value);
								else
								{ 
									CodeItem item=AdminCode.getCode("UM",dept_field_value,Integer.parseInt(display_e0122));
					    	    	if(item!=null) 
					    	    		dept_field_value=item.getCodename(); 
					    	    	else 
					    	    		dept_field_value=AdminCode.getCodeName("UM",dept_field_value);  
								}
								context.append(dept_field_value+seprartor+pos_field_value+" "+format_field_value);
							}
							else if("单位".equalsIgnoreCase(param1))
							{
								unit_field_value=AdminCode.getCodeName("UN",unit_field_value);
								context.append(unit_field_value+seprartor+pos_field_value+" "+format_field_value);
							}
							begin=false;
							groupValue=groupValue_2;
							
						}
					}
					else
						context.append(pos_field_value+" "+format_field_value);
					i++;
				}
				ArrayList tempList=new ArrayList();
				tempList.add(context.toString()); 
				tempList.add(a0100);
				tempList.add(nbase.toLowerCase());
				valueList.add(tempList);
				
			}
			 
			dao.batchUpdate("update "+tableName+" set  "+field1.getItemid()+"=? where a0100=? and lower(nbase)=?",valueList);
			 
			
			if (this.ModeFlag == 0) {
				if (StdTmpTable != null
						&& TempTableName.equalsIgnoreCase(StdTmpTable)
						&& StdTmpTable.length() > 0) {
					StringBuffer strSQL = new StringBuffer(""); 

					strSQL.append("(select ");
					strSQL.append(result);
					strSQL.append(" from " + tableName + " ");
					strSQL.append(" where " + tableName + ".A0100=");
					strSQL.append(StdTmpTable);
					strSQL.append(".A0100");
					strSQL.append(" and " + tableName + ".NBASE=" + StdTmpTable);
					if (TempTableName.length() > 5
							&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")  || TempTableName
									.toLowerCase().endsWith("gzsp")))
						strSQL.append(".nbase");
					else if("Q03".equalsIgnoreCase(StdTmpTable))
						strSQL.append(".nbase and "+tableName+".Q03Z0="+StdTmpTable+".Q03z0");
					else
						strSQL.append(".basepre");
					strSQL.append(")");
					result = strSQL.toString();
				}
			} 
			else
			{
				 
					String goalItem = field1.getItemid();
					StringBuffer strSQL = new StringBuffer("");
					strSQL.append("UPDATE ").append(TempTableName);
					strSQL.append(" SET ").append(TempTableName).append(".")
							.append(goalItem).append("=").append(
									" ( select " + tableName + ".")
							.append(goalItem);
					strSQL.append(" From " + tableName); 
					strSQL.append(" where " + tableName + ".a0100="
								+ this.TempTableName + ".a0100 "); 
					strSQL.append(" ) where exists (select null  ");
					strSQL.append(" From " + tableName); 
					strSQL.append(" where " + tableName + ".a0100="
								+ this.TempTableName + ".a0100 "); 
					strSQL.append(" )");
					if (!"".equals(strSQL.toString()))
						SQLS.add(strSQL.toString());
				 
			}  
		}
		catch(Exception  e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	
	private void createMidTable(FieldItem field1,String tableName)
	{
		DbWizard dbWizard = new DbWizard(this.con);
		FieldItem fieldItem = null;
	 
		Table table = new Table(tableName, tableName);  
		table.addField(field1.cloneField());
		
		Field obj=new Field("NBASE", "NBASE");
		obj.setDatatype(DataType.STRING);
		obj.setLength(8);
		obj.setKeyable(true);	
		obj.setNullable(false);
		table.addField(obj);
		
		obj=new Field("A0100", "A0100");
		obj.setDatatype(DataType.STRING);
		obj.setLength(8);
		obj.setKeyable(true);			
		obj.setNullable(false);
		table.addField(obj); 
		try { 
			ContentDAO dao = new ContentDAO(this.con);
		//	if (dbWizard.isExistTable(table.getName(), false)) 
			{ 
				dbWizard.dropTable(table.getName());
			} 
			dbWizard.createTable(table);
			
			StringBuffer sqlBuffer = new StringBuffer(); 
			sqlBuffer.append("insert into " + tableName + "");
			// 人员表 
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {
				sqlBuffer.append("(NBASE,A0100)");
				sqlBuffer.append(" select ");
				if (TempTableName.length() > 5
						&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName
								.toLowerCase().endsWith("gzsp"))) {
					sqlBuffer.append("nbase,A0100");
				} 
				else{
					sqlBuffer.append("basepre,A0100");
				}
				sqlBuffer.append(" FROM " + StdTmpTable);
				if (!("()".equalsIgnoreCase(whereText))
						&& whereText.trim().length() > 0)
					sqlBuffer.append(" where " + whereText);
			} else {
				 
					sqlBuffer.append("(NBASE,A0100)");
					sqlBuffer.append(" select '" + DbPre + "'," + DbPre
							+ "A01.A0100  from "); 
					sqlBuffer.append(this.DbPre + "A01"); 
					if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
					{
						sqlBuffer.append(" where exists (" + this.existWhereText +" ) ");
					}
					else
					{
						if (!("()".equalsIgnoreCase(whereText))
								&& whereText.trim().length() > 0)
							sqlBuffer.append(" where " + DbPre + "A01.A0100 in "
									+ whereText);
					}
				 
			}
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {

			} else {
				if (InfoGroupFlag == forPerson
						&& (DbPre == null || DbPre.trim().length() == 0))
					return  ;
			} 
			dao.update(sqlBuffer.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	

	// /////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 计算工作日
	 */
	int wordDay_num=0;
	boolean  s_has_filed=false; //起始日期是否为公式
	boolean e_has_field=false; //结束日期是否为公式
	private boolean Func_CalWorkDays(int DatePart, RetValue retValue)
			throws GeneralException, SQLException {
		wordDay_num++;
		
		  s_has_filed=false;
		  e_has_field=false;
		
		String str, str1, str2, str3 = "不含节假日";
		RetValue hold = new RetValue();
		str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false; 
		
		//2015-12-17 dengcan 
		HashMap tmpMap=(HashMap)this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems=new HashMap();
		if (!level0(retValue))
		{
			if (!retValue.IsDateType()) {
				SError(E_MUSTBEDATE);
				return false;
			}
			return false;
		}
		str1 = SQL.toString();
		if (!retValue.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		//2015-12-17 dengcan 
		if(this.mapUsedFieldItems.size()>0)
			this.s_has_filed=true;
		tmpMap.putAll(this.mapUsedFieldItems);
		this.mapUsedFieldItems=tmpMap;
		
		
		
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;

		//2015-12-17 dengcan 
		tmpMap=(HashMap)this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems=new HashMap();
		
		if (!level0(hold))
			return false; 
		
		//2015-12-17 dengcan 
		if(this.mapUsedFieldItems.size()>0)
			this.e_has_field=true;
		tmpMap.putAll(this.mapUsedFieldItems);
		this.mapUsedFieldItems=tmpMap;
		
		if (tok != S_RPARENTHESIS) {
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			} else {
				if (!Get_Token())
					return false;
				if (tok != S_HOLIDAY) {
					Putback();
					SError(E_UNKNOWNSTR);
					return false;
				}
				str3 = this.token;
				if (!Get_Token())
					return false;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
			}

		}
		if (!hold.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		str2 = SQL.toString();

		SQL.setLength(0);
		SQL.append(str);
		SQL.append(calWorkDays(str1.trim(), str2.trim(), str3.trim()));

		retValue.setValue(new Integer(123));
		retValue.setValueType(INT);
		// CurFuncNum = 0;
		// return true;
		return Get_Token();
	}

	private String calWorkDays(String str1, String str2, String str3) {
		// str1="2008.09.25";
		// str2="2008.02.02";
		String result = "";
		FieldItem field1 = new FieldItem();
		field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
		result = field1.getItemid();
		field1.setItemdesc("workday");
		field1.setItemlength(10);
		field1.setDecimalwidth(0);
		field1.setItemtype("N");
		field1.setCodesetid("0");
		field1.setVarible(2);// nIsVar := 2;
		mapUsedFieldItems.put(field1.getItemid(), field1);
		// 创建临时表
		if (InfoGroupFlag == forPerson || InfoGroupFlag == forUnit)
			createWorkDayTempTable(field1, str1, str2, str3);
		if ("N".equalsIgnoreCase(field1.getItemtype())
				&& result.indexOf("SELECT_") != -1)
			result = Sql_switcher.isnull(result, "0");

	    if (this.ModeFlag == 0)
		{ 
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {
				StringBuffer strSQL = new StringBuffer("");

				String tableName ="t#"+this.userView.getUserName()+"_wd"+this.wordDay_num;  // this.userView.getUserName() + "workDayTable"+this.wordDay_num;
				if (DBType == Constant.MSSQL && isTempTable)
					tableName = "##" + tableName;

				strSQL.append("(select ");
				strSQL.append(result);
				strSQL.append(" from " + tableName + " ");
				strSQL.append(" where " + tableName + ".A0100=");
				strSQL.append(StdTmpTable);
				strSQL.append(".A0100");
				strSQL.append(" and lower(" + tableName + ".NBASE)=lower(" + StdTmpTable);
				if (TempTableName.length() > 5
						&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName
								.toLowerCase().endsWith("gzsp") ))
				{
					strSQL.append(".nbase) ");
					strSQL.append(" and "+tableName+".a00z0="+StdTmpTable+".a00z0 ");
					strSQL.append(" and "+tableName+".a00z1="+StdTmpTable+".a00z1  ");
				}
				else if("Q03".equalsIgnoreCase(StdTmpTable))
					strSQL.append(".nbase) and "+tableName+".Q03Z0="+StdTmpTable+".Q03z0");
				//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
				else if(TempTableName.length() >8&&TempTableName.toLowerCase().matches("^templet_\\d+")){
					strSQL.append(".basepre) and "+tableName+".ins_id="+StdTmpTable+".ins_id ");
				}
				else
					strSQL.append(".basepre)");
				strSQL.append(")");
				result = strSQL.toString();
			}
		}
	    
		return result;
	}

    
	private void createWorkDayTempTable(FieldItem field1, String str1,
			String str2, String str3) {

		DbWizard dbWizard = new DbWizard(this.con);
		FieldItem fieldItem = null;
		String tableName ="t#"+this.userView.getUserName()+"_wd"+this.wordDay_num; // this.userView.getUserName() + "workDayTable"+this.wordDay_num;
		
		Table table = new Table(tableName, tableName);
		if (this.isTempTable)
			table.setBTemporary(true);
		if (DBType == Constant.MSSQL && isTempTable)
			tableName = "##" + tableName;

		table.addField(field1.cloneField());

		
		Field obj=new Field("betweenDays", "betweenDays");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);	
		table.addField(obj);
		
		/*
		fieldItem = new FieldItem("betweenDays", "betweenDays");
		fieldItem.setItemdesc("betweenDays");
		fieldItem.setItemtype("N");
		fieldItem.setItemlength(10);
		table.addField(fieldItem.cloneField());
		 */
		
		obj=new Field("startDate", "startDate");
		obj.setDatatype(DataType.DATE);
		obj.setKeyable(false);		
		table.addField(obj);
		
		obj=new Field("endDate", "endDate");
		obj.setDatatype(DataType.DATE);
		obj.setKeyable(false);		
		table.addField(obj);
		
		/*
		fieldItem = new FieldItem("startDate", "startDate");
		fieldItem.setItemdesc("startDate");
		fieldItem.setItemtype("D");
		table.addField(fieldItem.cloneField());
		fieldItem = new FieldItem("endDate", "endDate");
		fieldItem.setItemdesc("endDate");
		fieldItem.setItemtype("D");
		table.addField(fieldItem.cloneField());
		 */
		obj=new Field("I9999", "I9999");
		obj.setDatatype(DataType.INT);
		obj.setKeyable(false);	
		table.addField(obj);
		/*
		fieldItem = new FieldItem("I9999", "I9999");
		fieldItem.setItemdesc("I9999");
		fieldItem.setItemlength(10);
		fieldItem.setItemtype("N");
		table.addField(fieldItem.cloneField());
		 */
		
		obj=new Field("NBASE", "NBASE");
		obj.setDatatype(DataType.STRING);
		obj.setLength(8);
		obj.setKeyable(true);	
		obj.setNullable(false);
		table.addField(obj);
		
		obj=new Field("A0100", "A0100");
		obj.setDatatype(DataType.STRING);
		obj.setLength(8);
		obj.setKeyable(true);			
		obj.setNullable(false);
		table.addField(obj);
		
		if("Q03".equalsIgnoreCase(StdTmpTable))
		{
			obj=new Field("Q03Z0", "Q03Z0");
			obj.setDatatype(DataType.STRING);
			obj.setLength(30);
			obj.setKeyable(true);			
			obj.setNullable(false);
			table.addField(obj);
		}
		
		//薪资模块
		if (StdTmpTable != null
				&& TempTableName.equalsIgnoreCase(StdTmpTable)
				&& StdTmpTable.length() > 0&&(StdTmpTable.toLowerCase().indexOf("salary") != -1||StdTmpTable.toLowerCase().endsWith("_gz") || StdTmpTable
						.toLowerCase().endsWith("gzsp")))
		{
			obj=new Field("a00z0", "a00z0");
			obj.setDatatype(DataType.DATE);
			obj.setKeyable(true);	
			obj.setNullable(false);
			table.addField(obj);
			
			obj=new Field("a00z1", "a00z1");
			obj.setDatatype(DataType.INT);
			obj.setKeyable(true);	
			obj.setNullable(false);
			table.addField(obj);
		} 
		//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
		else if (StdTmpTable != null&& TempTableName.equalsIgnoreCase(StdTmpTable)&& StdTmpTable.length() >8&&StdTmpTable.toLowerCase().matches("^templet_\\d+"))
		{
			obj=new Field("ins_id", "ins_id");
			obj.setDatatype(DataType.INT);
			obj.setKeyable(true);	
			obj.setNullable(false);
			table.addField(obj);
		}
		obj=new Field("B0110", "B0110");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);			
		table.addField(obj);
		obj=new Field("E0122", "E0122");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);	
		table.addField(obj);
		obj=new Field("E01A1", "E01A1");
		obj.setDatatype(DataType.STRING);
		obj.setLength(30);
		obj.setKeyable(false);		
		table.addField(obj);
		
		/*
		fieldItem = new FieldItem("NBASE", "NBASE");
		fieldItem.setItemdesc("NBASE");
		fieldItem.setItemtype("A");
		fieldItem.setItemlength(8);
		fieldItem.setKeyable(true);
		fieldItem.setNullable(false);
		table.addField(fieldItem.cloneField());

		fieldItem = new FieldItem("A0100", "A0100");
		fieldItem.setItemdesc("A0100");
		fieldItem.setItemtype("A");
		fieldItem.setItemlength(8);
		fieldItem.setKeyable(true);
		fieldItem.setNullable(false);
		table.addField(fieldItem.cloneField());

		fieldItem = new FieldItem("B0110", "B0110");
		fieldItem.setItemdesc("B0110");
		fieldItem.setItemtype("A");
		fieldItem.setItemlength(30);
		table.addField(fieldItem.cloneField());

		fieldItem = new FieldItem("E0122", "E0122");
		fieldItem.setItemdesc("E0122");
		fieldItem.setItemtype("A");
		fieldItem.setItemlength(30);
		table.addField(fieldItem.cloneField());

		fieldItem = new FieldItem("E01A1", "E01A1");
		fieldItem.setItemdesc("E01A1");
		fieldItem.setItemtype("A");
		fieldItem.setItemlength(30);
		table.addField(fieldItem.cloneField());
		 */
		try {
			// System.out.println(dbWizard.isExistTable(table));
			ContentDAO dao = new ContentDAO(this.con);
		//	if (dbWizard.isExistTable(table.getName(), false)) 
			{

				// dao.delete("delete from workDayTable",new ArrayList());
				dbWizard.dropTable(table.getName());
			}
			dbWizard.createTable(table);
			if (str1.length() == 10 && str1.indexOf(".") != -1)
				str1 = str1.replaceAll("-", ".");
			if (str2.length() == 10 && str2.indexOf(".") != -1)
				str2 = str2.replaceAll("-", ".");
			if (importDateToTable(field1, str1, str2)) {
		//		RowSet rowSet = dao.search("select  *  from " + tableName+ " where betweenDays<>0 and b0110 is not null and  betweenDays<10000 ");
				RowSet rowSet = dao.search("select  *  from " + tableName+ " where betweenDays<>0  and  betweenDays<10000 ");
				
				HashMap restWeekMap = search_RestOfWeek();
				ArrayList kqFeastList = new ArrayList();
	//			if (str3.equals("不含节假日"))  20151106
					kqFeastList = getKqFeastList();
				HashMap turn_restMap = search_turn_rest(); // 取得单位倒休记录
				while (rowSet.next()) {
					int ds = rowSet.getInt("betweenDays"); // 间隔天数
					Date startDate = rowSet.getDate("startDate");
					Date endDate = rowSet.getDate("endDate");
					String q03z0="";
					if("Q03".equalsIgnoreCase(this.StdTmpTable))
						q03z0=rowSet.getString("Q03z0");
					String b0110 = rowSet.getString("b0110")!=null?rowSet.getString("b0110"):"";
					LazyDynaBean restOfWeekBean = null;
					String pp_b0110=b0110;  //匹配公休日的单位  20151211 dengcan
					if (restWeekMap.get("un"+b0110.toLowerCase()) != null) {
						restOfWeekBean = (LazyDynaBean) restWeekMap.get("un"+b0110
								.toLowerCase());
						pp_b0110=b0110.toLowerCase();
					} else  { 
						for(int i=1;i<=b0110.length();i++)
						{
							String tmp=b0110.substring(0,b0110.length()-i).toLowerCase(); 
							 if (restWeekMap.get("un"+tmp) != null) {
								 restOfWeekBean = (LazyDynaBean) restWeekMap.get("un"+tmp);
								 pp_b0110=tmp;
					 			break;
							}
						} 
				//		if (restWeekMap.get("un") != null)
				//				restOfWeekBean = (LazyDynaBean) restWeekMap.get("un"); 
					}
					ArrayList turn_rest = new ArrayList();
					if (turn_restMap.get("un"+pp_b0110.toLowerCase()) != null) {
						turn_rest = (ArrayList) turn_restMap.get("un"+pp_b0110.toLowerCase());
					}
					else if (turn_restMap.get("un") != null) {
						turn_rest = (ArrayList) turn_restMap.get("un");
					}
			/*		else
					{
						for(int i=1;i<b0110.length();i++)
						{
							String aa=b0110.substring(0,b0110.length()-i); 
							 if (turn_restMap.get("un"+aa) != null) {
								turn_rest = (ArrayList) turn_restMap.get("un");
								break;
							}
						}
					}*/
					int workDays = getWordDays(ds, b0110, startDate, endDate,
							restOfWeekBean, kqFeastList, turn_rest,str3);
					// XXXXX
				//	System.out.println(rowSet.getString("a0100")+"   workDays="+workDays);
					String sql = "update " + tableName + " set "
							+ field1.getItemid() + "=" + workDays + " where ";
					if (InfoGroupFlag == forPerson) {
						String a0100 = rowSet.getString("a0100");
						sql += " a0100='" + rowSet.getString("a0100") + "'";
						sql += " and lower(nbase)='" + rowSet.getString("nbase").toLowerCase() + "'";
						if("Q03".equalsIgnoreCase(this.StdTmpTable)&&q03z0!=null)
						{
							 
							sql+=" and q03z0='"+q03z0+"'";
						}
						if(StdTmpTable != null && TempTableName.equalsIgnoreCase(StdTmpTable) && (this.StdTmpTable.toLowerCase().indexOf("salary")!=-1||this.StdTmpTable.toLowerCase().endsWith("_gz")|| this.StdTmpTable
								.toLowerCase().endsWith("gzsp") )&&(this.targetTable==null||this.targetTable.indexOf("_gz_mid")==-1))
						{
							sql+=" and a00z1="+rowSet.getInt("a00z1");
							Date d=rowSet.getDate("a00z0");
							Calendar dd=Calendar.getInstance();
							dd.setTime(d);
							sql+=" and "+Sql_switcher.year("a00z0")+"="+dd.get(Calendar.YEAR);
							sql+=" and "+Sql_switcher.month("a00z0")+"="+(dd.get(Calendar.MONTH)+1);
						}
						//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
						else  if (StdTmpTable != null&& TempTableName.equalsIgnoreCase(StdTmpTable)&& StdTmpTable.length() >8&&StdTmpTable.toLowerCase().matches("^templet_\\d+"))
						{
							sql+=" and ins_id="+rowSet.getString("ins_id");
						}

					} else if (InfoGroupFlag == forUnit) {

						sql += " b0110='" + rowSet.getString("b0110") + "'";
					}
					dao.update(sql);
				}
				rowSet.close();
			}

			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {

			} else {
				String goalItem = field1.getItemid();
				StringBuffer strSQL = new StringBuffer("");
				strSQL.append("UPDATE ").append(TempTableName);
				strSQL.append(" SET ").append(TempTableName).append(".")
						.append(goalItem).append("=").append(
								" ( select " + tableName + ".")
						.append(goalItem);
				strSQL.append("  ");
				strSQL.append(strCurSet2());
				strSQL.append(" ) where exists (select null  ");
				strSQL.append(strCurSet2() + " )");
				if (!"".equals(strSQL.toString()))
					SQLS.add(strSQL.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String strCurSet2() {
		String tableName ="t#"+this.userView.getUserName()+"_wd"+this.wordDay_num; // this.userView.getUserName() + "workDayTable"+this.wordDay_num;
		if (DBType == Constant.MSSQL && isTempTable)
			tableName = "##" + tableName;
		StringBuffer sub_sql = new StringBuffer(" From " + tableName);
		if (InfoGroupFlag == forPerson) {
			sub_sql.append(" where " + tableName + ".a0100="
					+ this.TempTableName + ".a0100 ");
		} else if (InfoGroupFlag == forUnit) {
			sub_sql.append(" where " + tableName + ".b0110="
					+ this.TempTableName + ".b0110 ");
		}
		return sub_sql.toString();
	}

	public static void main(String[] args) {
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.set(Calendar.YEAR, 2009);
		aCalendar.set(Calendar.MONTH, 2);
		aCalendar.set(Calendar.DATE, 22);
		// aCalendar.setTime(startDate);
		// 计算此日期是一周中的哪一天
		int fw = aCalendar.get(Calendar.DAY_OF_WEEK);
		// System.out.println(fw);
		String sss="sdf_gzss";
		System.out.println(sss.endsWith("_gz"));
		
		HashMap aa=new HashMap();
		aa.put("aaa","zzzz");
		
		HashMap dd=new HashMap();
		dd=(HashMap)aa.clone();
		aa=new HashMap();
		
		aa.put("ddd","1111");
		dd.putAll(aa);
		
		aa=dd;
		System.out.println("111111");
		
		
	}

	/**
	 * 
	 * @param ds
	 * @param b0110
	 * @param startDate
	 * @param endDate
	 * @param restOfWeekBean
	 * @param kqFeastList
	 * @param turn_rest
	 * @param str3  "不含节假日"、"含节假日"
	 * @return
	 */
	public int getWordDays(int ds, String b0110, Date startDate, Date endDate,
			LazyDynaBean restOfWeekBean, ArrayList kqFeastList,
			ArrayList turn_rest,String str3) {
		
		int nW = ds; // 工作日
		// 下面把公休日去了 nD: 整除数， nM 余数 fw 第一天为周几
		int nD = ds / 7;
		int nM = ds % 7;
		Calendar aCalendar = Calendar.getInstance();
		aCalendar.setTime(startDate);
		// 计算此日期是一周中的哪一天
		int fw = aCalendar.get(Calendar.DAY_OF_WEEK);
		if (fw == 1)
			fw = 7;
		else
			fw--;
		if (restOfWeekBean != null) {
			String rest_weeks = (String) restOfWeekBean.get("rest_weeks");
			String[] temps = rest_weeks.split(",");
			for (int i = 0; i < temps.length; i++) {
				if (temps[i].length() > 0) {
					int nK = Integer.parseInt(temps[i]);
					if (nK >= fw && nK < (fw + nM))
						nW = nW - (nD + 1);
					else
						nW = nW - nD;
				}
			}
		}
		// 去节假日
		if ("不含节假日".equals(str3)) // 20151106
			nW = nW - getFeastDays(startDate, endDate, kqFeastList,restOfWeekBean,turn_rest);
		else if ("含节假日".equals(str3)) // 20151106
		{
			nW = nW + getFeastRestDays(startDate, endDate, kqFeastList,restOfWeekBean);
		}
		// 处理倒休日
		nW = nW + getTurnWeekDays(startDate, endDate, turn_rest,kqFeastList,str3);

		return nW;
	}

	// 返回倒休天数, 公休上班日-倒休日
	public int getTurnWeekDays(Date startDate, Date endDate, ArrayList turn_rest,ArrayList kqFeastList,String str3) {

		LazyDynaBean abean=null;
		HashMap feastMap=new HashMap(); 
	//	if (str3.equals("含节假日")) 
		{
			for (int nJ = 0; nJ < kqFeastList.size(); nJ++) {
				abean = (LazyDynaBean) kqFeastList.get(nJ);
				String str = (String) abean.get("feast_dates");
				String[] temps = str.split(",");
				String sF="";
				for (int j = 0; j < temps.length; j++) {
					sF = temps[j];
					if (sF.trim().length() == 0)
						continue;
					// 带年的
					if (sF.length() > 6) {
						feastMap.put(StringUtils.split(sF, ".")[0]+StringUtils.split(sF, ".")[1]+StringUtils.split(sF, ".")[2],"1");
	
					} else { // 不带年
						String _sF=StringUtils.split(sF, ".")[0]+StringUtils.split(sF, ".")[1];
						feastMap.put(_sF,"1");
					}
				}
			}
		}
		
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd"); 
		SimpleDateFormat df2=new SimpleDateFormat("MMdd"); 
		int nD = 0; 
		for (int i = 0; i < turn_rest.size(); i++) {
			abean = (LazyDynaBean) turn_rest.get(i);
			Date week_date = (Date) abean.get("week_date");

			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(week_date);
			aCalendar.set(Calendar.HOUR_OF_DAY, 11);
			aCalendar.set(Calendar.MINUTE, 0);
			aCalendar.set(Calendar.SECOND, 0);
			Calendar sCalendar = Calendar.getInstance();
			sCalendar.setTime(startDate);
			sCalendar.set(Calendar.HOUR_OF_DAY, 0);
			sCalendar.set(Calendar.MINUTE, 0);
			sCalendar.set(Calendar.SECOND, 1);
			Calendar eCalendar = Calendar.getInstance();
			eCalendar.setTime(endDate);
			eCalendar.set(Calendar.HOUR_OF_DAY, 23);
			eCalendar.set(Calendar.MINUTE, 59);
			eCalendar.set(Calendar.SECOND, 59);
			if (aCalendar.getTimeInMillis() >= sCalendar.getTimeInMillis()
					&& aCalendar.getTimeInMillis() <= eCalendar
							.getTimeInMillis())
			{
				if(feastMap.get(df.format(week_date))==null&&feastMap.get(df2.format(week_date))==null)
					nD++;
			}
		}
		for (int i = 0; i < turn_rest.size(); i++) {
			abean = (LazyDynaBean) turn_rest.get(i);
			Date turn_date = (Date) abean.get("turn_date");

			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(turn_date);
			aCalendar.set(Calendar.HOUR_OF_DAY, 11);
			aCalendar.set(Calendar.MINUTE, 0);
			aCalendar.set(Calendar.SECOND, 0);
			Calendar sCalendar = Calendar.getInstance();
			sCalendar.setTime(startDate);
			sCalendar.set(Calendar.HOUR_OF_DAY, 0);
			sCalendar.set(Calendar.MINUTE, 0);
			sCalendar.set(Calendar.SECOND, 1);
			Calendar eCalendar = Calendar.getInstance();
			eCalendar.setTime(endDate);
			eCalendar.set(Calendar.HOUR_OF_DAY, 23);
			eCalendar.set(Calendar.MINUTE, 59);
			eCalendar.set(Calendar.SECOND, 59);
			if (aCalendar.getTimeInMillis() >= sCalendar.getTimeInMillis()
					&& aCalendar.getTimeInMillis() <= eCalendar
							.getTimeInMillis())
				nD--;
		}
		return nD;
	}

	
	/**
	 * 获得起始-结束时间内所有的公休日日期
	 * @param startDate   起始时间
	 * @param endDate    结束时间
	 * @param restOfWeekBean  公休日 
	 * @return
	 */
	private HashMap getAllRestDay(Date startDate, Date endDate,LazyDynaBean restOfWeekBean)
	{
			HashMap allRestDayMap=new HashMap();
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(startDate);
			Calendar eCalendar = Calendar.getInstance();
			eCalendar.setTime(endDate);
			
			String rest_weeks="";
			if (restOfWeekBean != null) {
				rest_weeks = ","+((String) restOfWeekBean.get("rest_weeks")).trim()+",";
			}
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd"); 
			SimpleDateFormat df2=new SimpleDateFormat("MMdd"); 
			while(aCalendar.compareTo(eCalendar)<1)  //a比c早,返回-1, a与c相同,返回0   ,a比c晚,返回1
			{
				// 计算此日期是一周中的哪一天
				int fw = aCalendar.get(Calendar.DAY_OF_WEEK);
				if (fw == 1)
					fw = 7;
				else
					fw--;
				if(rest_weeks.indexOf(","+fw+",")!=-1)
				{
					allRestDayMap.put(df.format(aCalendar.getTime()),"1");
					allRestDayMap.put(df2.format(aCalendar.getTime()),"1");
				}
				aCalendar.add(Calendar.DAY_OF_MONTH,1);
			} 
			return allRestDayMap; 
	}
	
	
	// 取得时间区间的节假日(与公休日重叠的)
		public int getFeastRestDays(Date startDate, Date endDate, ArrayList kqFeastList,LazyDynaBean restOfWeekBean) {

			HashMap allRestDayMap=getAllRestDay(startDate,endDate,restOfWeekBean); //获得起始-结束时间内所有的公休日日期
			
			String sF, sMS, sME; // 当前假日，开始，结束日期串(不含年）
			Date dt = new Date();
			int nY = 0; // 年份差
			int nW = 0;
			SimpleDateFormat f = new SimpleDateFormat("MM.dd");
			sMS = f.format(startDate);
			sME = f.format(endDate);
			nY = startDate.getYear() - endDate.getYear();
			LazyDynaBean abean = null;
			SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd"); 
			for (int nJ = 0; nJ < kqFeastList.size(); nJ++) {
				abean = (LazyDynaBean) kqFeastList.get(nJ);
				String str = (String) abean.get("feast_dates");
				String[] temps = str.split(",");
				for (int j = 0; j < temps.length; j++) {
					sF = temps[j];
					if (sF.trim().length() == 0)
						continue;
					// 带年的
					if (sF.length() > 6) {
						Calendar aCalendar = Calendar.getInstance();
						aCalendar.set(Calendar.YEAR, Integer.parseInt(StringUtils
								.split(sF, ".")[0]));
						aCalendar.set(Calendar.MONTH, Integer.parseInt(StringUtils
								.split(sF, ".")[1])-1);
						aCalendar.set(Calendar.DATE, Integer.parseInt(StringUtils
								.split(sF, ".")[2]));
						aCalendar.set(Calendar.HOUR_OF_DAY, 9);
						aCalendar.set(Calendar.MINUTE, 0);
						aCalendar.set(Calendar.SECOND, 0);
						if(allRestDayMap.get(df.format(aCalendar.getTime()))!=null)
							nW++; 

					} else { // 不带年
						String _sF=StringUtils.split(sF, ".")[0]+StringUtils.split(sF, ".")[1];
						if(allRestDayMap.get(_sF)!=null)
								nW++;
					}
				}
			}
			return nW;
		}
	
	
	
	// 取得时间区间的节假日
	public int getFeastDays(Date startDate, Date endDate, ArrayList kqFeastList,LazyDynaBean restOfWeekBean,ArrayList turn_rest) {

		HashMap allRestDayMap=getAllRestDay(startDate,endDate,restOfWeekBean); //获得起始-结束时间内所有的公休日日期
		SimpleDateFormat df=new SimpleDateFormat("yyyyMMdd");
		LazyDynaBean abean=null;
/*
		for (int i = 0; i < turn_rest.size(); i++) {
			abean = (LazyDynaBean) turn_rest.get(i);
			Date week_date = (Date) abean.get("week_date");
			allRestDayMap.remove(df.format(week_date)); 
		}
	*/	
		String sF, sMS, sME; // 当前假日，开始，结束日期串(不含年）
		Date dt = new Date();
		int nY = 0; // 年份差
		int nW = 0;
		SimpleDateFormat f = new SimpleDateFormat("MM.dd");

		sMS = f.format(startDate);
		sME = f.format(endDate);
		nY = startDate.getYear() - endDate.getYear(); 
		for (int nJ = 0; nJ < kqFeastList.size(); nJ++) {
			abean = (LazyDynaBean) kqFeastList.get(nJ);
			String str = (String) abean.get("feast_dates");
		 
			String[] temps = str.split(",");
			for (int j = 0; j < temps.length; j++) {
				sF = temps[j];
				if (sF.trim().length() == 0)
					continue;
				// 带年的
				if (sF.length() > 6) {
					Calendar aCalendar = Calendar.getInstance();
					aCalendar.set(Calendar.YEAR, Integer.parseInt(StringUtils
							.split(sF, ".")[0]));
					aCalendar.set(Calendar.MONTH, Integer.parseInt(StringUtils
							.split(sF, ".")[1])-1);
					aCalendar.set(Calendar.DATE, Integer.parseInt(StringUtils
							.split(sF, ".")[2]));
					aCalendar.set(Calendar.HOUR_OF_DAY, 9);
					aCalendar.set(Calendar.MINUTE, 0);
					aCalendar.set(Calendar.SECOND, 0);
					Calendar sCalendar = Calendar.getInstance();
					sCalendar.setTime(startDate);
					sCalendar.set(Calendar.HOUR_OF_DAY, 0);
					sCalendar.set(Calendar.MINUTE, 0);
					sCalendar.set(Calendar.SECOND, 1);
					Calendar eCalendar = Calendar.getInstance();
					eCalendar.setTime(endDate);
					eCalendar.set(Calendar.HOUR_OF_DAY, 23);
					eCalendar.set(Calendar.MINUTE,59);
					eCalendar.set(Calendar.SECOND, 59);
					if (aCalendar.getTimeInMillis() >= sCalendar.getTimeInMillis()&& aCalendar.getTimeInMillis() <= eCalendar.getTimeInMillis())
					{
						if(allRestDayMap.get(df.format(aCalendar.getTime()))==null)
							nW++;
					}
						

				} else { // 不带年
					String _sF=StringUtils.split(sF, ".")[0]+StringUtils.split(sF, ".")[1];
					if(nY==0&&CompareStr(sMS, sF) && CompareStr(sF, sME))
					{
						if(allRestDayMap.get(_sF)==null)
							nW++;
					}
					else
					{
						if(nY==-1)
						{
							if (CompareStr(sMS, sF) && CompareStr(sF,"12.31"))
							{
								if(allRestDayMap.get(_sF)==null)
									nW++;
							}
							if (CompareStr("01.01", sF) && CompareStr(sF,sME))
							{
								if(allRestDayMap.get(_sF)==null)
									nW++;
							}
						}
						else if(nY<-1)
						{
							nW=nW-nY-1;
							if (CompareStr(sMS, sF) && CompareStr(sF,"12.31"))
							{
								if(allRestDayMap.get(_sF)==null)
									nW++;
							}
							if (CompareStr("01.01", sF) && CompareStr(sF,sME))
							{
								if(allRestDayMap.get(_sF)==null)
									nW++;
							}
						}
						
					}
					
					/*
					
					// sME >= sMS 如: 05.01-06.01
					if (CompareStr(sMS, sME)) {
						nW = nW + nY;
						// sF>=sMS and sF<=sME
						if (CompareStr(sMS, sF) && CompareStr(sF, sME))
							nW++;
					} else {// 09.12-03.12
						nW = nW + nY - 1;
						// sF>=sMS or sF<=sME
						if (CompareStr(sMS, sF) || CompareStr(sF, sME))
							nW++;
					}
					
					*/
				}

			}
		}
		return nW;
	}

	public boolean CompareStr(String a, String b) {
		boolean flag = true;
		String[] temp1 = StringUtils.split(a, ".");
		String[] temp2 = StringUtils.split(b, ".");
		if (Integer.parseInt(temp1[0]) > Integer.parseInt(temp2[0]))
			flag = false;
		else if (Integer.parseInt(temp1[0]) == Integer.parseInt(temp2[0])
				&& Integer.parseInt(temp1[1]) > Integer.parseInt(temp2[1]))
			flag = false;
		return flag;
	}

	// 取得节假日列表
	public ArrayList getKqFeastList() {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.con);
			LazyDynaBean abean = null;
			RowSet rowSet = dao
					.search("select * from kq_feast order by feast_id");
			while (rowSet.next()) {

				String feast_dates = rowSet.getString("feast_dates") != null ? rowSet
						.getString("feast_dates").toString()
						: "";
				String feast_id = rowSet.getString("feast_id") != null ? rowSet
						.getString("feast_id").toString() : "";
				abean = new LazyDynaBean();
				abean.set("feast_id", feast_id);
				abean.set("feast_dates", feast_dates);
				list.add(abean);
			}
			rowSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 取得单位倒休记录
	 * 
	 * @return
	 */
	public HashMap search_turn_rest() {
		HashMap map = new HashMap();
		try {
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rowSet = dao
					.search("SELECT * FROM kq_turn_rest order by b0110");
			String a_b0110 = "";
			ArrayList list = new ArrayList();
			LazyDynaBean abean = null;
			while (rowSet.next()) {

				String b0110 = rowSet.getString("b0110");
				if (a_b0110.length() == 0) {
					a_b0110 = b0110;
				}
				Date week_date = rowSet.getDate("week_date");
				Date turn_date = rowSet.getDate("turn_date");
				abean = new LazyDynaBean();
				abean.set("week_date", week_date);
				abean.set("turn_date", turn_date);
				if (!a_b0110.equalsIgnoreCase(b0110)) {
					map.put(a_b0110.toLowerCase(), list);
					list = new ArrayList();
					a_b0110 = b0110;
				}

				list.add(abean);
			}
			map.put(a_b0110.toLowerCase(), list);
			rowSet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/***************************************************************************
	 * 取得单位的公休日
	 **************************************************************************/
	public HashMap search_RestOfWeek() {

		HashMap restOfWeekMap = new HashMap();
		RowSet rowSet = null;
		String save_sql = "SELECT B0110, rest_weeks from kq_restofweek";
		ContentDAO dao = new ContentDAO(this.con);
		try {
			LazyDynaBean abean = null;
			rowSet = dao.search(save_sql);
			while (rowSet.next()) {

				String rest_weeks = rowSet.getString("rest_weeks") != null ? rowSet
						.getString("rest_weeks").toString()
						: "";
				String b0110_field = rowSet.getString("b0110") != null ? rowSet
						.getString("b0110").toString() : "";
				abean = new LazyDynaBean();
				abean.set("b0110", b0110_field);
				abean.set("rest_weeks", rest_weeks);
				restOfWeekMap.put(b0110_field.toLowerCase(), abean);
			}
			rowSet.close();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return restOfWeekMap;
	}

	// 往工作日临时表中写入数据
	private boolean importDateToTable(FieldItem field1, String str1, String str2) {
		try {

			StringBuffer sqlBuffer = new StringBuffer();
			String tableName ="t#"+this.userView.getUserName()+"_wd"+this.wordDay_num; // this.userView.getUserName() + "workDayTable"+this.wordDay_num;
			if (DBType == Constant.MSSQL && isTempTable)
				tableName = "##" + tableName;
			sqlBuffer.append("insert into " + tableName + "");
			// 人员表
			String table = "";
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {
				if("Q03".equalsIgnoreCase(StdTmpTable))
					sqlBuffer.append("(NBASE,A0100,Q03Z0,B0110,E0122)");
				else
				{
					if (TempTableName.length() > 5
							&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")  || TempTableName
									.toLowerCase().endsWith("gzsp")))
						sqlBuffer.append("(NBASE,A0100,a00z0,a00z1,B0110,E0122)");
					//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
					else if(TempTableName.length() >8&&TempTableName.toLowerCase().matches("^templet_\\d+")){
						sqlBuffer.append("(NBASE,A0100,B0110,E0122,ins_id)");
					}						
					else	
						sqlBuffer.append("(NBASE,A0100,B0110,E0122)");
				}
				sqlBuffer.append(" select ");
				if (TempTableName.length() > 5
						&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName
								.toLowerCase().endsWith("gzsp"))) {
					sqlBuffer.append("nbase,A0100,a00z0,a00z1,B0110,E0122");
				} 
				else if("Q03".equalsIgnoreCase(StdTmpTable))
				{
					sqlBuffer.append("nbase,A0100,Q03z0,B0110,E0122");
				} 
				//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
				else if(TempTableName.length() >8&&TempTableName.toLowerCase().matches("^templet_\\d+")){
					sqlBuffer.append("basepre,A0100,B0110_1,E0122_1,ins_id");
				}
				else {
					sqlBuffer.append("basepre,A0100,B0110_1,E0122_1");
				}
				sqlBuffer.append(" FROM " + StdTmpTable);
				if (!("()".equalsIgnoreCase(whereText))
						&& whereText.trim().length() > 0)
					sqlBuffer.append(" where " + whereText);
			} else {
				if (InfoGroupFlag == forPerson) {
					sqlBuffer.append("(NBASE,A0100,B0110,E0122)");
					sqlBuffer.append(" select '" + DbPre + "'," + DbPre
							+ "A01.A0100," + DbPre + "A01.B0110," + DbPre
							+ "A01.E0122 from ");
					table = this.DbPre + "A01";
					sqlBuffer.append(table);
					
					if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
					{
						sqlBuffer.append(" where exists (" + this.existWhereText +" ) ");
					}
					else
					{
						if (!("()".equalsIgnoreCase(whereText))
								&& whereText.trim().length() > 0)
							sqlBuffer.append(" where " + DbPre + "A01.A0100 in "
									+ whereText);
					}
				} else if (InfoGroupFlag == forUnit) {
					sqlBuffer.append("(B0110)");
					sqlBuffer.append(" select B01.B0110 from ");
					table = "B01";
					sqlBuffer.append(table);
					
					if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
					{
						sqlBuffer.append(" where exists (" + this.existWhereText +" ) ");
					}
					else
					{
						if (whereText != null && whereText.trim().length() > 0
								&& !("()".equalsIgnoreCase(whereText)))
							sqlBuffer.append(" where " + "B01.B0110 in "
									+ whereText);
					}
				} else if (InfoGroupFlag == forPosition) {
					sqlBuffer.append("(E01A1)");
					sqlBuffer.append(" select K01.E01A1 from ");
					table = "K01";
					sqlBuffer.append(table);
					if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
					{
						sqlBuffer.append(" where exists (" + this.existWhereText +" ) ");
					}
					else
					{
						if (whereText != null && whereText.trim().length() > 0
								&& !("()".equalsIgnoreCase(whereText)))
							sqlBuffer.append(" where " + "K01.E01A1 in "
									+ whereText);
					}
				}
			}
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {

			} else {
				if (InfoGroupFlag == forPerson
						&& (DbPre == null || DbPre.trim().length() == 0))
					return true;
			}

			ContentDAO dao = new ContentDAO(this.con);
			dao.update(sqlBuffer.toString());
			// 日期常量
			StringBuffer strSQL = new StringBuffer("");
			if (StdTmpTable != null
					&& TempTableName.equalsIgnoreCase(StdTmpTable)
					&& StdTmpTable.length() > 0) {

				String a_str = " FROM " + StdTmpTable + " where  "
						+ StdTmpTable + ".a0100=" + tableName + ".a0100";
				if (TempTableName.length() > 5
						&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz")  || TempTableName
								.toLowerCase().endsWith("gzsp")))
				{
					a_str += " and " + StdTmpTable + ".nbase=" + tableName+ ".nbase";
					a_str += " and " + StdTmpTable + ".a00z0=" + tableName+ ".a00z0";
					a_str += " and " + StdTmpTable + ".a00z1=" + tableName+ ".a00z1";
				}
				else if("Q03".equalsIgnoreCase(this.StdTmpTable))
					a_str += " and " + StdTmpTable + ".nbase=" + tableName
					+ ".nbase and "+this.StdTmpTable+".q03z0="+tableName+".q03z0";
				//liuyz bug 29754 批量审批同一个人的多个单子计算后台报错
				else if(this.TempTableName.length() >8&&this.TempTableName.toLowerCase().matches("^templet_\\d+"))
				{
					a_str += " and " + StdTmpTable + ".basepre=" + tableName
					+ ".nbase and "+ StdTmpTable + ".ins_id=" + tableName
					+ ".ins_id ";
				}
				else
					a_str += " and " + StdTmpTable + ".basepre=" + tableName
							+ ".nbase";
				
				if (!("()".equalsIgnoreCase(whereText))
						&& whereText.trim().length() > 0)
					a_str+=" and " + whereText;
				
				if (str1.indexOf("-") != -1&&isDateString(str1.replaceAll("'",""))&&!this.s_has_filed) {  //2015-12-17 dengcan 
					strSQL.append("update " + tableName + " set startDate="
							+ str1);
				} else {
					strSQL.setLength(0);
					strSQL.append("UPDATE " + tableName + "");
					strSQL.append(" SET startDate=(SELECT ").append(
							Sql_switcher.charToDate(Sql_switcher.dateToChar(str1.toUpperCase())));
					strSQL.append(a_str + " ) where exists (select null ");
					strSQL.append(a_str + " )");
				}
				dao.update(strSQL.toString());
				// 日期常量
				strSQL.setLength(0);
				if (str2.indexOf("-") != -1&&isDateString(str2.replaceAll("'",""))&&!this.e_has_field) { //2015-12-17 dengcan 
					strSQL.append("update " + tableName + " set endDate="
							+ str2);
				} else {

					strSQL.append("UPDATE " + tableName + "");
					strSQL.append(" SET endDate=(SELECT ").append(
							str2.toUpperCase());
					strSQL.append(a_str + " ) where exists (select null ");
					strSQL.append(a_str + " )");

				}
				dao.update(strSQL.toString());
			} else {
				//如果是今天，也得加上，否则在数据字典中也找不到
				if ((str1.indexOf("-") != -1&&isDateString(str1.replaceAll("'",""))&&!this.s_has_filed) || str1.equalsIgnoreCase(Sql_switcher.today())) {
					strSQL.append("update " + tableName + " set startDate="
							+ str1);
				} else {
					if (DataDictionary.getFieldItem(str1.toLowerCase()) != null) {
						FieldItem item = DataDictionary.getFieldItem(str1
								.toLowerCase());
						String strCurSet = item.getFieldsetid();
						strSQL.setLength(0);
						strSQL.append("UPDATE " + tableName + "");
						strSQL.append(" SET startDate=(SELECT ").append(
								str1.toUpperCase());
						strSQL.append(" FROM ");
						strSQL.append(getCurrentSet(strCurSet, tableName));
						strSQL.append(" ) where exists (select null FROM  ");
						strSQL.append(getCurrentSet(strCurSet, tableName)
								+ " )");
					} else
						return false;
				}
				dao.update(strSQL.toString());
				// 日期常量
				strSQL.setLength(0);
				if ((str2.indexOf("-") != -1&&isDateString(str2.replaceAll("'",""))&&!this.e_has_field) || str1.equalsIgnoreCase(Sql_switcher.today())) {
					strSQL.append("update " + tableName + " set endDate="
							+ str2);
				} else {
					if (DataDictionary.getFieldItem(str2.toLowerCase()) != null) {
						FieldItem item = DataDictionary.getFieldItem(str2
								.toLowerCase());
						String strCurSet = item.getFieldsetid();
						strSQL.setLength(0);
						strSQL.append("UPDATE " + tableName + "");
						strSQL.append(" SET endDate=(SELECT ").append(
								str2.toUpperCase());
						strSQL.append(" FROM ");
						strSQL.append(getCurrentSet(strCurSet, tableName));
						strSQL.append(" ) where exists (select null FROM  ");
						strSQL.append(getCurrentSet(strCurSet, tableName)
								+ " )");
					} else
						return false;
				}
				dao.update(strSQL.toString());
			}

			strSQL.setLength(0);
			
			strSQL.append("update " + tableName + " set betweenDays=("
					+ Sql_switcher.diffDays(Sql_switcher.charToDate(Sql_switcher.dateToChar("endDate")), 
							Sql_switcher.charToDate(Sql_switcher.dateToChar("startDate")))
							+ "+1) where ");
			strSQL.append(" startDate is not null and endDate is not null  and endDate>=startDate");
			
			dao.update(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("update " + tableName + " set betweenDays=0 where ");
			strSQL
					.append(" startDate is  null or endDate is  null  or endDate<startDate");
			dao.update(strSQL.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getCurrentSet(String tempTable, String goalTable) {
		StringBuffer sql_sub = new StringBuffer("");
		if (tempTable.charAt(0) == 'A') {
			if ("A01".equalsIgnoreCase(tempTable))
				sql_sub.append(" " + DbPre + tempTable + " where " + goalTable
						+ ".A0100=" + DbPre + tempTable + ".A0100 ");
			else
				sql_sub.append(" ( select a.* from " + DbPre + tempTable
						+ "  a where a.I9999=(select max( b.I9999 ) from "
						+ DbPre + tempTable + " b where a.a0100=b.a0100)) "
						+ DbPre + tempTable + " where " + goalTable + ".A0100="
						+ DbPre + tempTable + ".A0100 ");
		} else if (tempTable.charAt(0) == 'B') {
			if ("B01".equalsIgnoreCase(tempTable))
				sql_sub.append(" " + tempTable + " where " + goalTable
						+ ".B0110=" + tempTable + ".B0110 ");
			else
				sql_sub.append("  ( select a.* from " + tempTable
						+ "  a where a.I9999=(select max( b.I9999 ) from "
						+ tempTable + " b where a.B0110=b.B0110)) " + tempTable
						+ " where " + goalTable + ".B0110=" + tempTable
						+ ".B0110 ");
		} else if (tempTable.charAt(0) == 'K') {
			if ("K01".equalsIgnoreCase(tempTable))
				sql_sub.append(" " + tempTable + " where " + goalTable
						+ ".E01A1=" + tempTable + ".E01A1 ");
			else
				sql_sub.append("  ( select a.* from " + tempTable
						+ "  a where a.I9999=(select max( b.I9999 ) from "
						+ tempTable + " b where a.E01A1=b.E01A1))  "
						+ tempTable + " where " + goalTable + ".E01A1="
						+ tempTable + ".E01A1 ");
		}
		return sql_sub.toString();
	}

	// ////////////////////////////////////////////////////////////////////////////////////

	private boolean Func_DateDiff(int DatePart, RetValue retValue)
			throws GeneralException, SQLException {
		String str, str1, str2;
		RetValue hold = new RetValue();
		str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		if (!retValue.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;

		if (!level0(hold))
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!hold.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		str2 = SQL.toString();
		SQL.setLength(0);
		SQL.append(str + "(");
		switch (DatePart) {
		// 处理年数
		case FUNCYEARS: {
			SQL.append(Sql_switcher.diffYears(str1, str2));
			retValue.setValue(new Integer(retValue.diffYear(hold)));
			retValue.setValueType(INT);
			break;
		}
			// 处理月数
		case FUNCMONTHS: {
			SQL.append(Sql_switcher.diffMonths(str1, str2));

			retValue.setValue(new Integer(retValue.diffMonth(hold)));
			retValue.setValueType(INT);
			break;
		}
			// 处理天数
		case FUNCDAYS: {
			SQL.append(Sql_switcher.diffDays(str1, str2));
			retValue.setValue(new Integer(retValue.diffDay(hold)));
			retValue.setValueType(INT);
			break;
		}
			// 处理季数
		case FUNCQUARTERS: {
			SQL.append(Sql_switcher.diffQuarters(str1, str2));
			retValue.setValue(new Integer(retValue.diffMonth(hold) / 3));
			retValue.setValueType(INT);
			break;
		}
			// 处理周数
		case FUNCWEEKS: {
			SQL.append(Sql_switcher.diffWeeks(str1, str2));
			retValue.setValue(new Integer(retValue.diffDay(hold) / 7));
			retValue.setValueType(INT);
			break;
		}
		}
		SQL.append(")");
		return Get_Token();
	}

	private boolean Func_DateAdd(int DatePart, RetValue retValue)
			throws GeneralException, SQLException {
		String str, str1, str2;
		RetValue retValue1 = new RetValue();
		str = SQL.toString();
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		if (!retValue.IsDateType()) {
			SError(E_MUSTBEDATE);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue1))
			return false;
		str2 = SQL.toString();
		if (!retValue1.isIntType()) {
			SError(E_MUSTBEINTEGER);
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		SQL.append(str);
		switch (DatePart) {
		// 增加年数
		case FUNCADDYEAR: {
			SQL.append(Sql_switcher.addYears(str1, str2));
			retValue.addYear(retValue1);
			break;
		}
			// 增加月数
		case FUNCADDMONTH: {
			
		//	SQL.append(Sql_switcher.addMonths(str1, str2));
			if(Sql_switcher.searchDbServer()==2) //dengcan(2015-07-06)   2015-04-30+ 6月=2015-10-31 与sqlserver 不一致
			{
				String s="";
				s=" case when to_char(ADD_MONTHS("+str1+", "+str2+"),'dd')-to_char("+str1+",'dd')>0 then  ";
				s+=" to_date(to_char(ADD_MONTHS("+str1+", "+str2+")-(to_char(ADD_MONTHS("+str1+", "+str2+"),'dd')-to_char("+str1+",'dd')),'yyyy-mm-dd HH24:MI:SS'),'yyyy-mm-dd HH24:MI:SS') ";
				s+=" else  "+Sql_switcher.addMonths(str1, str2)+"  END ";
				SQL.append(s);
			}
			else
				SQL.append(Sql_switcher.addMonths(str1, str2));
			retValue.addMonth(retValue1);
			break;
		}
			// 增加日数
		case FUNCADDDAY: {
			SQL.append(Sql_switcher.addDays(str1, str2));
			retValue.addDay(retValue1);
			break;
		}
			// 增加季数
		case FUNCADDQUARTER: {
			SQL.append(Sql_switcher.addQuarters(str1, str2));
			retValue.addQuarter(retValue1);
			break;
		}
			// 增加周数
		case FUNCADDWEEK: {
			SQL.append(Sql_switcher.addWeeks(str1, str2));
			retValue.addWeek(retValue1);
			break;
		}
		}

		return Get_Token();
	}

	private boolean Func_Math(int FuncNum, RetValue retValue)
			throws GeneralException, SQLException {
		String str = null, str1;// str2=null, strfmt=null;
		int nLength = 0;
		// x:Extended;
		// i:integer;
		RetValue retValue1 = new RetValue();
		str = SQL.toString();
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		if (!(retValue.isFloatType() || retValue.isIntType())) {
			SError(E_MUSTBENUMBER);
			return false;
		}

		if (FuncNum == FUNCROUND) { // 四舍五入有两个参数
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			SQL.setLength(0);
			if (!Get_Token())
				return false;
			if (!level0(retValue1))
				return false;
			// str2 = WhereCond.toString();
			if (!retValue1.isIntType()) {
				SError(E_MUSTBEINTEGER);
				return false;
			}
			nLength = ((Integer) retValue1.getValue()).intValue();
		}
		
		int fen=-1;   //逢分进元  可能会设置的逢多少分进元，默认不设置
		if (FuncNum == FUNCYUAN) {
			if (tok == S_COMMA) {
				SQL.setLength(0);
				if (!Get_Token())
					return false;
				if (!level0(retValue1))
					return false;
				if (!retValue1.isIntType()) {
					SError(E_MUSTBEINTEGER);
					return false;
				}
				fen = ((Integer) retValue1.getValue()).intValue();
				if(fen<1||fen>99)
				{ 
					SError(E_INTSCOPE);
					return false;
				}
				
			}
		}
		
		
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		SQL.append(str);
		switch (FuncNum) {
		case FUNCINT: {
			if(Sql_switcher.searchDbServer() == 1 && "h2".equalsIgnoreCase(dataBaseType)) {
				SQL.append(" TRUNC(" + str1 + ")");
			}else
				SQL.append(Sql_switcher.toInt(str1));
			break;
		}
		case FUNCROUND: {
			SQL.append(Sql_switcher.round(str1, nLength));
			break;
		}
		case FUNCSANQI: {
			SQL.append(Sql_switcher.sanqi(" " + str1));
			break;
		}
		case FUNCYUAN: {
			SQL.append(yuan(str1,fen));
			break;
		}
		case FUNCJIAO: {
			SQL.append(Sql_switcher.jiao(str1));
			break;
		}
		}

		switch (FuncNum) {
		case FUNCINT:
			if (retValue.isFloatType()) {
				retValue.setValue(new Integer(((Float) retValue.getValue())
						.intValue()));
				retValue.setValueType(INT);
			}
			break;
		case FUNCROUND: {
			if (retValue.isFloatType()) {
				// 四舍五入bug！！！
				// retValue.setValue(new Integer(((Float)
				// retValue.getValue()).intValue()));
				if (nLength > 0) {
					retValue.setValueType(FLOAT);
					if (retValue.getValue() instanceof Integer) {
						retValue.setValue(new Float((String) retValue
								.getValue()));
					} else
						retValue.setValue(((Float) retValue.getValue()));
				} else {
					retValue.setValueType(INT);
					retValue.setValue(new Integer(((Float) retValue.getValue())
							.intValue()));
				}
			}
			break;
		}

		case FUNCSANQI: {
			if (retValue.isFloatType()) {
				// Float FTemp = (Float) retValue.getValue();
				// retValue.setValue(new Float(().intValue()));
			}
			break;
		}
		case FUNCYUAN: {
			float fYuan;
			if (retValue.isFloatType()) {
				fYuan = ((Float) retValue.getValue()).floatValue();
				fYuan = Math.round(fYuan * 100) / 100;// 逢分进元 ??
				retValue.setValue(new Float(fYuan));
			}
			break;
		}
		case FUNCJIAO: {
			float fYuan;
			if (retValue.isFloatType()) {
				fYuan = ((Float) retValue.getValue()).floatValue();
				fYuan = (fYuan * 10) / 10;// 逢角进元 ？？
				retValue.setValue(new Float(fYuan));
			}
			break;
		}
		}

		return Get_Token();
	}
	
	
	 
	public static String yuan(String expr,int fen)
	{
	    StringBuffer strvalue = new StringBuffer();
	    
	    String temp="0";
	    if(fen!=-1)
	    	temp=PubFunc.divide(String.valueOf(fen),"100",2);
	    switch (Sql_switcher.searchDbServer())
	    {
	    case 2:
	    case 3:
	      if(fen==-1)
	      { 
		      strvalue.append(" CASE WHEN ");
		      strvalue.append(expr);
		      strvalue.append("-TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0)<>0 THEN ");
		      strvalue.append("(TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0)+1) ELSE ");
		      strvalue.append("TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0) END");
	      }
	      else
	      {
	    	  strvalue.append(" CASE WHEN ");
		      strvalue.append(expr);
		      strvalue.append("-TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0)>="+temp+" THEN ");
		      strvalue.append("(TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0)+1) ELSE ");
		      strvalue.append("TRUNC(");
		      strvalue.append(expr);
		      strvalue.append(",0) END");
	      }
	      break;
	    default:
	      if(fen==-1)
		  { 
		      strvalue.append(" CASE WHEN ");
		      strvalue.append(expr);
		      strvalue.append("-CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT)<>0 THEN ");
		      strvalue.append("(CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT)+1) ELSE ");
		      strvalue.append("CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT) END");
		  }
	      else
	      {
	    	  strvalue.append(" CASE WHEN ");
		      strvalue.append(expr);
		      strvalue.append("-CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT)>="+temp+" THEN ");
		      strvalue.append("(CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT)+1) ELSE ");
		      strvalue.append("CAST(");
		      strvalue.append(expr);
		      strvalue.append(" AS INT) END");
	      }
	      break;
	    }

	    return strvalue.toString();
	}
	
	
	
	
	

	private boolean Func_String(int FuncNum, RetValue retValue)
			throws GeneralException, SQLException {
		String str = "", str1 = "", str2 = "", str3 = "";
		RetValue retValue1 = new RetValue();
		RetValue retValue2 = new RetValue();
		str = SQL.toString();
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		if (!retValue.IsStringType()) {
			Putback();
			SError(E_MUSTBESTRING);
		}
		if ((FuncNum == FUNCLEFT) || (FuncNum == FUNCRIGHT)
				|| (FuncNum == FUNCSUBSTR)) {
			if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
			SQL.setLength(0);
			if (!Get_Token())
				return false;
			if (!level0(retValue1))
				return false;
			//str2 = SQL.toString();
			if (!retValue1.isIntType()) {
				SError(E_MUSTBEINTEGER);
				return false;
			}
			//在计算左串和右串的时候的时候可能会有第二个参数为负数left(a,b),right(a,b)当b为负数的时候报错
			str2 = "(CASE WHEN (" + SQL.toString() + ") < 0 THEN 0 ELSE (" + SQL.toString() + ") END)";
			if (FuncNum == FUNCSUBSTR) {
				if (tok != S_COMMA) {
					Putback();
					SError(E_LOSSCOMMA);
				}
				SQL.setLength(0);
				if (!Get_Token())
					return false;
				if (!level0(retValue2))
					return false;
				//str3 = SQL.toString();
				if (!retValue2.isIntType()) {
					SError(E_MUSTBEINTEGER);
					return false;
				} 
				//在计算子串的时候可能会有第三个参数为负数substring(a,b,c),当c为负数的时候报错
				str3 = "(CASE WHEN (" + SQL.toString() + ") < 0 THEN 0 ELSE (" + SQL.toString() + ") END)";
			}
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		SQL.append(str);
		switch (FuncNum) {
		case FUNCTRIM: {
			SQL.append(Sql_switcher.trim(str1));
			break;
		}
		case FUNCLTRIM: {
			SQL.append(Sql_switcher.ltrim(str1));
			break;
		}
		case FUNCRTRIM: {
			SQL.append(Sql_switcher.rtrim(str1));
			break;
		}
		case FUNCLEN: {
			SQL.append(Sql_switcher.length(str1));
			break;
		}
		case FUNCLEFT: {
			SQL.append(Sql_switcher.left(str1, str2));
			break;
		}
		case FUNCRIGHT: {
			SQL.append(Sql_switcher.right(str1, str2));
			break;
		}
		case FUNCSUBSTR: {
			SQL.append(Sql_switcher.substr(str1, str2, str3));
			break;
		}
		}
		switch (FuncNum) {
		case FUNCTRIM: {
			retValue.setValue(((String) retValue.getValue()).trim());
			break;
		}
		case FUNCLTRIM: {// 模拟String的LeftTrim()!!
			String strTemp = (String) retValue.getValue();
			retValue.setValue(strTemp
					.substring(strTemp.indexOf(strTemp.trim())));
			break;
		}
		case FUNCRTRIM: {// 模拟String的RightTrim()!!
			String strTemp = (String) retValue.getValue();
			retValue.setValue(strTemp.substring(0, strTemp.indexOf(strTemp
					.trim())
					+ strTemp.trim().length()));
			break;
		}
		case FUNCLEN: {
			retValue.setValue(new Integer(((String) retValue.getValue())
					.length()));
			retValue.setValueType(INT);
			break;
		}
		case FUNCLEFT: {
			int i = ((Integer) retValue1.getValue()).intValue(); 
			/* 第二个参数用函数时这个判断就没有意义了，定义公式时人为判断吧
			//zxj 20170311 左串第二参数小于等于0没意义
			if(i<=0) {
			    Putback();
                SError(E_LESSTHANOREQUALZERO);
                return false;
			}
			 */   
			String str11 = (String) retValue.getValue();
			if (str11 != null && !"".equals(str11.trim())&&str11.length()>=i) { //20141124 dengcan
				retValue.setValue(((String) retValue.getValue())
						.substring(0, i));
			}
			break;
		}
		case FUNCRIGHT: {
			int i = ((Integer) retValue1.getValue()).intValue();
			/* 第二个参数用函数时这个判断就没有意义了，定义公式时人为判断吧
			//zxj 20170311 右串第二参数小于等于0没意义
			if(i<=0) {
                Putback();
                SError(E_LESSTHANOREQUALZERO);
                return false;
            }
			*/
			int iLength = ((String) retValue.getValue()).length();
			iLength = iLength > i ? iLength - i : 0;
			retValue.setValue(((String) retValue.getValue()).substring(iLength));
			break;
		}
		case FUNCSUBSTR: {
			// System.out.println(retValue.getValue());
			int i = ((Integer) retValue1.getValue()).intValue(); // - 1;
			int j = ((Integer) retValue2.getValue()).intValue();
			/* 第三个参数用函数时这个判断就没有意义了，定义公式时人为判断吧
			//zxj 20170311 子串第三参数小于等于0没意义
			if(j<=0) {
                Putback();
                SError(E_LESSTHANOREQUALZERO);
                return false;
            }
            */
			j = i + ((Integer) retValue2.getValue()).intValue();
			String str11 = (String) retValue.getValue();
			if (str11 != null && !"".equals(str11.trim()) && str11.length() > j) {
				retValue.setValue(((String) retValue.getValue())
						.substring(i, j));
			}
		}
		}

		return Get_Token();
	}

	private boolean Func_Convert(int FuncNum, RetValue retValue)
			throws GeneralException, SQLException {
		String str = "", str1 = "",str2="";
		str = SQL.toString();
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str1 = SQL.toString();
		switch (FuncNum) {

			case FUNCCTOD: {
				if (!retValue.IsStringType()) {
					Putback();
					SError(E_MUSTBESTRING);
					return false;
				}
				break;
			}
			case FUNCCTOI: {
				if (!retValue.IsStringType()) {
					Putback();
					SError(E_MUSTBESTRING);
					return false;
				}
				break;
			}
			case FUNCDTOC: {
				if (!retValue.IsDateType()) {
					Putback();
					SError(E_MUSTBEDATE);
					return false;
				}
				break;
			}
			case FUNCITOC: {
				if (!(retValue.isIntType() || retValue.isFloatType())) {
					Putback();
					SError(E_MUSTBENUMBER);
					return false;
				}
				break;
			}
		}
		
		if(FuncNum==FUNCCTOD&&tok ==S_COMMA) //字符转日期 写上时间
		{
			RetValue retValue2 = new RetValue();
			SQL.setLength(0); // expr1 &,
			if (!Get_Token())
				return false;
			if (!level0(retValue2))
				return false;
			str2 = SQL.toString();
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		else if(FuncNum==FUNCDTOC&&tok ==S_COMMA) //日期转字符  写上格式
		{
			RetValue retValue2 = new RetValue();
			SQL.setLength(0); // expr1 &,
			if (!Get_Token())
				return false;
			if (!level0(retValue2))
				return false;
			str2 = SQL.toString();
			str2=str2.trim();
			if(!("'YYYY-MM-DD'".equals(str2)|| "'YYYY-MM-DD HH24'".equals(str2)|| "'YYYY-MM-DD HH24:MI'".equals(str2)|| "'YYYY-MM-DD HH24:MI:SS'".equals(str2)))
			{
				Putback();
				SError(E_FUNCDTOCERROR);
				return false;
			} 
			
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		else
		{
		
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		SQL.setLength(0);
		SQL.append(str);
		switch (FuncNum) {
		case FUNCCTOD: {
			if(str2.length()>0&&Sql_switcher.searchDbServer()==2)
			{ 
				 StringBuffer strvalue=new StringBuffer("to_date(");
		         strvalue.append(str1);
		         strvalue.append(","+str2+")");
		         //20150206 dengc
		         SQL.append(" CASE WHEN LENGTH("+str1+")>4  THEN  "+strvalue);
				 SQL.append(" ELSE NULL END ");
		        // SQL.append(strvalue);
			}
			else
			{
				/**syl 56345 字符转日期 转换不正确*/
				if(Sql_switcher.searchDbServer()==Constant.ORACEL)  //20150206 dengc
				{
//					SQL.append(" CASE WHEN   ( SUBSTR("+ str1 + ",5,1)='.' OR SUBSTR("+ str1 + ",5,1)='-' )  THEN  "+Sql_switcher.charToDate(str1));
//					SQL.append(" ELSE NULL END ");
					//基于字符转日期 有函数，其定义可能为：字符转日期(日期转字符(拟请假起始日期)+" "+拟请假起始小时+":"+拟请假起始分钟)
					if(str1!=null&&str1.trim().indexOf(" ")!=-1){
						if(str1.indexOf(":")!=2){
							String[] str1_arr=str1.split(":");
							//23:22
							if(str1_arr.length==2){
								SQL.append("to_date("+str1+",'yyyy.MM.dd HH24:mi')");
							//23:22:33
							}else if(str1_arr.length==3){
								SQL.append("to_date("+str1+",'yyyy.MM.dd HH24:mi:ss')");
							}else{
								//默认
								SQL.append("to_date("+str1+",'yyyy.MM.dd HH24')");
							}
						}
					}else{
						SQL.append("to_date("+str1+",'yyyy.MM.dd')");
					}
				}
				else
					SQL.append(Sql_switcher.charToDate(str1));
			}
			retValue.setValue("'2006.06.30'");
			retValue.setValueType(DATEVALUE);
			break;
		}
		case FUNCCTOI: {
			SQL.append(Sql_switcher.charToFloat(str1));
			// retValue.setValue(new Integer(
			// Integer.parseInt(str1.trim())));
			retValue.setValue(new Float(1));
			retValue.setValueType(this.FLOAT);
			break;
		}
		case FUNCDTOC: {
			//"YYYY-MM-DD     YYYY-MM-DD HH24   YYYY-MM-DD HH24:MI   YYYY-MM-DD HH24:MI:SS"
			int format_num=0; //日期格式
			if(str2.length()>0)
			{
				if("'YYYY-MM-DD'".equals(str2))
						format_num=1;
				else if("'YYYY-MM-DD HH24'".equals(str2))
						format_num=2;
				else if("'YYYY-MM-DD HH24:MI'".equals(str2))
					    format_num=3;
				else if("'YYYY-MM-DD HH24:MI:SS'".equals(str2))
					    format_num=4; 
			}
			
			if(str2.length()==0||(str.length()>0&&format_num==0))
				SQL.append(Sql_switcher.isnull(Sql_switcher.dateToChar(str1),"''"));//日期转字符直接用Sql_switcher.isnull(XX,"''")
			else 
			{
				if(Sql_switcher.searchDbServer()==1)  //MSSQL
				{
					if(format_num==1)
						format_num=str2.trim().length()-2; 
					else
						format_num=str2.trim().length()-4; 
					SQL.append("ISNULL(LEFT(CONVERT(VARCHAR,");
					SQL.append(str1);
					SQL.append(",121),"+format_num+"),'')");
				}
				else //ORACLE
				{
					SQL.append("NVL(TO_CHAR(");
					SQL.append(str1);
					SQL.append(","+str2.trim()+"),'')");
				}
				
			}
			
			retValue.setValue(str1);
			retValue.setValueType(STRVALUE);
			break;
		}
		case FUNCITOC: {
			if(Sql_switcher.searchDbServer()==1)
			{
				String temp="CASE WHEN "+str1+"=CEILING("+str1+") THEN CAST(CAST("+str1+" AS INT) AS VARCHAR) "
	             + " ELSE REPLACE(RTRIM(REPLACE(CAST(CAST("+str1+" AS numeric(20,10)) AS VARCHAR), '0',' ')  ), ' ','0') END ";
				SQL.append(temp);
			}
			else
			{
				//SQL.append(Sql_switcher.floatToChar(str1));
				SQL.append("to_char("+str1+")");
			}
			retValue.setValue(str1);
			retValue.setValueType(STRVALUE);
			break;
		}
		}
		return Get_Token();
	}

	private boolean Func_IIF(RetValue retValue) throws GeneralException,
			SQLException {
		String str;
		RetValue retValue1 = new RetValue(), retValue2 = new RetValue();
		if (!"IIF".equalsIgnoreCase(token)) {
			return Func_CIIF(retValue);
		}
		str = SQL.toString(); // (
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		SQL.setLength(0); // Logic expression &,
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str = str + " CASE WHEN " + SQL;

		if (!retValue.isBooleanType()) {
			Putback();
			SError(E_MUSTBEBOOL);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		SQL.setLength(0); // expr1 &,
		if (!Get_Token())
			return false;
		if (!level0(retValue1))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		str = str + " THEN " + SQL;

		SQL.setLength(0); // expr2 & )
		if (!Get_Token())
			return false;
		if (!level0(retValue2))
			return false;
		if (!(retValue1.IsSameType(retValue2) || retValue1.IsNullType() || retValue2
				.IsNullType())) {
			Putback();
			SError(E_NOTSAMETYPE);
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
		}
		str = str + " ELSE " + SQL + " END";
		if (((Boolean) retValue.getValue()).booleanValue()) {
			retValue.setValue(retValue1.getValue());
		} else {
			retValue.setValue(retValue2.getValue());
		}
		retValue.setValueType(retValue1.getValueType());
		SQL.setLength(0);
		SQL.append(str);
		return Get_Token();
	}

	private boolean Func_CIIF(RetValue retValue) throws GeneralException,
			SQLException {
		String str;
		RetValue retValue1 = new RetValue(), retValue2 = new RetValue();
		str = SQL.toString(); // (
		SQL.setLength(0); // Logic expression &,
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		str = str + " CASE WHEN " + SQL;
		if (!retValue.isBooleanType()) {
			Putback();
			SError(E_MUSTBEBOOL);
			return false;
		}

		if (tok != S_THEN) {
			Putback();
			SError(E_LOSSTHEN);
			return false;
		}

		SQL.setLength(0); // expr1 &,
		if (!Get_Token())
			return false;
		if (!level0(retValue1))
			return false;
		if (tok != S_ELSE) {
			Putback();
			SError(E_LOSSELSE);
			return false;
		}
		str = str + " THEN " + SQL;

		SQL.setLength(0); // expr2 & )
		if (!Get_Token())
			return false;
		if (!level0(retValue2))
			return false;
		if (!(retValue1.IsSameType(retValue2) || retValue1.IsNullType() || retValue2
				.IsNullType())) {
			Putback();
			SError(E_NOTSAMETYPE);
			return false;
		}
		if (tok != S_END) {
			Putback();
			SError(E_LOSSEND);
			return false;
		}
		str = str + " ELSE " + SQL + " END";
		// System.out.println(retValue.getValue());
		if (((Boolean) retValue.getValue()).booleanValue()) {
			retValue.setValue(retValue1.getValue());
			retValue.setValueType(retValue1.getValueType());
		} else {
			retValue.setValue(retValue2.getValue());
			retValue.setValueType(retValue2.getValueType());
		}
		
		SQL.setLength(0);
		SQL.append(str);
		return Get_Token();
	}

	private boolean Func_CASE(RetValue retValue) throws GeneralException,
			SQLException {
		String str = "";
		RetValue retValue1 = new RetValue(), retValue2 = new RetValue();
		int ntok;
		String tempSql="";
		tempSql+=" "+SQL.toString();
		ntok = 0;
		// retValue:=NULL;
		retValue.setValue("NULL");
		retValue.setValueType(NULLVALUE);
		str = str + " CASE ";
		if (!Get_Token())
			return false; // 如果
		do {
			SQL.setLength(0);
			if ((tok == S_END) || (tok == S_ELSE))
				break;
			if (tok != FUNCIIF) {
				Putback();
				SError(E_LOSSIIF);
				return false;
			}
			if (!Get_Token())
				return false; // Logic expression &,
			if (!level0(retValue1))
				return false;
			// System.out.println(retValue1.getValue() + "," +
			// retValue1.getTypeString());
			if (!retValue1.isBooleanType()) {
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}
			str = str + " WHEN " + SQL;

			if (tok != S_THEN) {
				Putback();
				SError(E_LOSSTHEN);
				return false;
			}
			SQL.setLength(0); // exp &,
			if (!Get_Token())
				return false;
			if (!level0(retValue2))
				return false;
			ntok = tok;
			if (!(retValue.IsSameType(retValue2) || retValue.IsNullType() || retValue2
					.IsNullType())) {
				Putback();
				SError(E_NOTSAMETYPE);
				return false;
			}
			 
			if (retValue2.isBooleanType()) {
				Putback();
				SError(E_NOTBEBOOL);
				return false;
			}
			
			
			str = str + " THEN " + SQL;
			
			// System.out.println(retValue1.getValue());
			if (((Boolean) retValue1.getValue()).booleanValue()) {
				retValue.setValue(retValue2.getValue());
				retValue.setValueType(retValue2.getValueType());
			}
		} while (ntok == FUNCIIF);
		 
		
		if (retValue.IsNullType()) {
			retValue.setValue(retValue2.getValue());
			retValue.setValueType(retValue2.getValueType());
		}
		if (tok == S_ELSE) { // ELSE
			SQL.setLength(0); // expr1 &,
			if (!Get_Token())
				return false;
			if (!level0(retValue))
				return false;
			str = str + " ELSE " + SQL;

			if (retValue.isBooleanType()) {
				Putback();
				SError(E_NOTBEBOOL);
				return false;
			}
			
			if (tok != S_END) {
				Putback();
				SError(E_LOSSEND);
				return false;
			}
		}
		if (tok != S_END) {
			Putback();
			SError(E_LOSSEND);
			return false;
		}
		str = str + " END";
		SQL.setLength(0); 
		SQL.append(tempSql);
	//	System.out.println( str.toString());
		SQL.append(str);
		return Get_Token();
	}

	private boolean IsMainSet(String strSet) {
		// System.out.println(strSet.substring(1, 3));
		if(strSet.toLowerCase().indexOf("a01")!=-1)
			return true;
		return "01".equals(strSet.substring(1, 3));
	}

	/**
	 * 分段计算
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 */
	private boolean Func_SelectSetSum(RetValue retValue)
			throws GeneralException, SQLException {
		FieldItem Field1 = new FieldItem();
		RetValue retValue1 = new RetValue();
		String strWhere = "", strSQL = "";

		HashMap usedFieldItems = (HashMap) this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems.clear();
		FieldSet _fieldSet=null;
		if(this.FieldSet!=null)
			_fieldSet=this.FieldSet.cloneItem();
		
		ArrayList usedSets=new ArrayList();
		if(this.UsedSets!=null)
			usedSets=(ArrayList)this.UsedSets.clone();
	
		HashMap expUsedFieldItems = null;
		HashMap whereUsedFieldItems = null;
		

		if (this.ModeFlag != this.forSearch) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		strSQL = SQL.toString();
		SQL.setLength(0);

		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		} 
		String _tempTableName=this.TempTableName;
		String _tempTable="T_FD_" + TempTableName;
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				_tempTable = "##" + _tempTable;
		}
		this.TempTableName=_tempTable;
		
		/** 指标 */
		if (!Get_Token())
			return false;

		if (!level0(retValue))
			return false;

		this.TempTableName=_tempTableName;
		String str1 = SQL.toString();
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}

		if (!Get_Token())
			return false;
		if (tok != S_BASESET) {
			Putback();
			SError(E_LOSSBASESET);
			return false;
		}

		/** 指标 */
		if (!Get_Token())
			return false;
		if (token_type != FIELDSET) {
			Putback();
			SError(E_LOSSSET);
			return false;
		}
		if (IsMainSet(this.FieldSet.getFieldsetid())) {
			Putback();
			SError(E_MUSTBESUBSET);
			return false;
		}
		if (!Get_Token())
			return false;

		expUsedFieldItems = (HashMap) this.mapUsedFieldItems.clone();
		this.mapUsedFieldItems.clear();
		if (tok == S_SATISFY) {

			SQL.setLength(0); // 条件
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			if (!level0(retValue1)) {
				Field1 = null;
				return false;
			}
			if (!retValue1.isBooleanType()) {
				Field1 = null;
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}

			if (!validateIsSetField(this.mapUsedFieldItems, this.FieldSet)) {
				SError(E_NOSETITEM);
				return false;
			}
			strWhere = SQL.toString();
			whereUsedFieldItems = (HashMap) this.mapUsedFieldItems.clone();
		}

		SQL.setLength(0);
		this.mapUsedFieldItems.clear();
		mapUsedFieldItems = usedFieldItems;
		SQL.append(strSQL).append(
				Func_SelectSetSum(expUsedFieldItems, whereUsedFieldItems,
						strWhere, str1));

		if (retValue.isFloatType()) {
			retValue.setValue(new Float(123.0));
		} else {
			retValue.setValue(new Integer(123));
		}
		this.FieldSet=_fieldSet;
		this.UsedSets=usedSets;
		CurFuncNum = 0;

		return true;
	}

	// 分段计算
	private String Func_SelectSetSum(HashMap expUsedFieldItems,
			HashMap whereUsedFieldItems, String strWhere, String str1) {
		String result = "";
		FieldItem field1 = new FieldItem();
		field1.setItemtype("N");
		field1.setItemlength(10);
		field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
		result = field1.getItemid();
		mapUsedFieldItems.put(field1.getItemid(), field1);

		
		creatTempTable(expUsedFieldItems,whereUsedFieldItems,strWhere);
		createFdSql(strWhere,result,str1);
		
		
		if (result.indexOf("SELECT_") != -1)
			result = Sql_switcher.isnull(result, "0");
		return result;
	}
	
	
	private void createFdSql(String strWhere,String strAs,String str1) 
	{
		String _tempTable = "T_FD_" + TempTableName;
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				_tempTable = "##" + _tempTable;
		}
		String strKeyField="";
		if (InfoGroupFlag == forPerson)
			strKeyField="A0100";
		if (InfoGroupFlag == forPosition) 
			strKeyField="E01A1";
		if (InfoGroupFlag == forUnit) 
			strKeyField="B0110";
		
		String tempSql="select sum("+Sql_switcher.isnull(str1,"0")+") fdvalue,"+strKeyField+" from "+_tempTable+" where  1=1 and "+strWhere+" group by "+strKeyField;
		 
		StringBuffer strSQL=new StringBuffer("");
		strSQL.setLength(0);
		strSQL.append("UPDATE ").append(TempTableName);
		strSQL.append(" SET ").append(TempTableName).append(".").append(
				strAs).append("=(SELECT aa.").append("fdvalue");
		strSQL.append(" FROM ").append(" ( "+tempSql+" ) aa");
		strSQL.append(" WHERE ").append(TempTableName).append(".").append(strKeyField).append("=aa.").append(strKeyField).append(" )");
		strSQL.append(" WHERE EXISTS ( SELECT  NULL  ");
		strSQL.append(" FROM ").append(" ( "+tempSql+" ) aa");
		strSQL.append(" WHERE ").append(TempTableName).append(".").append(strKeyField).append("=aa.").append(strKeyField).append(" )"); 
		this.SQLS.add(strSQL.toString());
	}

	private void creatTempTable(HashMap expUsedFieldItems,
			HashMap whereUsedFieldItems, String strWhere) {
		String _tempTable = "T_FD_" + TempTableName;
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				_tempTable = "##" + _tempTable;
		}
		if (DBType == Constant.MSSQL) {
			SQLS.add("DROP TABLE " + _tempTable);
		} else {
		//	if (this.isTempTable)
		//		SQLS.add("truncate table " + _tempTable); 
			SQLS.add("DROP TABLE " + _tempTable+" purge");
		}
		StringBuffer strSQL = new StringBuffer("");
		String setid = this.getDbPreTable(this.FieldSet.getFieldsetid());
		if (DBType == Constant.ORACEL) {
			strSQL.setLength(0);
			strSQL.append("CREATE ");
			if (this.isTempTable)
				strSQL.append(" GLOBAL TEMPORARY ");
			strSQL.append(" TABLE  ").append(_tempTable);
			if (this.isTempTable)
				strSQL.append(" On Commit Preserve Rows ");
			strSQL.append(" AS SELECT *  FROM ").append(
					setid);
		} else {
			strSQL.setLength(0);
			strSQL.append("SELECT * INTO ").append(_tempTable);
			strSQL.append(" FROM ").append(setid);
		}
		SQLS.add(strSQL.toString());

		ArrayList otherSetFieldList = new ArrayList(); // 其他子集字段
		ArrayList tmpFieldList = new ArrayList(); // 临时表中字段
		for (int i = 0; i < 2; i++) {
			Iterator it = null;
			if (i == 0)
				it = expUsedFieldItems.keySet().iterator();
			if (i == 1)
				it = whereUsedFieldItems.keySet().iterator();
			while (it.hasNext()) {
				String key=(String)it.next();
				FieldItem fld =null;
				if (i == 0)
					fld=(FieldItem)expUsedFieldItems.get(key);
				else
					fld=(FieldItem)whereUsedFieldItems.get(key);
				String type = fld.getItemtype();
				strSQL.setLength(0);
				if (!this.FieldSet.getFieldsetid().equalsIgnoreCase(
						fld.getFieldsetid())) {
				//	otherSetFieldList.add(fld.getItemid());
					String sAdd = fld.getItemid()
							+ " "
							+ Sql_switcher.getFieldType(type.charAt(0), fld
									.getItemlength(), fld.getDecimalwidth());
					switch (DBType) {
					case Constant.MSSQL:
					case Constant.ORACEL:
						strSQL.append("alter table ");
						strSQL.append(_tempTable);
						strSQL.append(" add ");
						strSQL.append(sAdd);
						break;
					case Constant.DB2:
						strSQL.append("alter table ");
						strSQL.append(_tempTable);
						strSQL.append(" add column ");
						strSQL.append(sAdd);
						break;
					}
					SQLS.add(strSQL.toString());
				}

				FieldItem _item = DataDictionary.getFieldItem(fld.getItemid());
				if (_item != null) {
					if (!this.FieldSet.getFieldsetid().equalsIgnoreCase(
							_item.getFieldsetid())) {
						otherSetFieldList.add(_item.getItemid());
					}
				} else {
					tmpFieldList.add(fld.getItemid());
				}
			}
		}
		strSQL.setLength(0);
		String sAdd = " setSum " + Sql_switcher.getFieldType('N', 18, 6);
		switch (DBType) {
		case Constant.MSSQL:
		case Constant.ORACEL:
			strSQL.append("alter table ");
			strSQL.append(_tempTable);
			strSQL.append(" add ");
			strSQL.append(sAdd);
			break;
		case Constant.DB2:
			strSQL.append("alter table ");
			strSQL.append(_tempTable);
			strSQL.append(" add column ");
			strSQL.append(sAdd);
			break;
		}
		SQLS.add(strSQL.toString());
		// 导入临时表其他数据
		impTmpOtherData(otherSetFieldList,tmpFieldList);
		

	}

	/**
	 * 导入临时表其它数据
	 * @param otherSetFieldList
	 * @param tmpFieldList
	 */
	private void impTmpOtherData(ArrayList otherSetFieldList,ArrayList tmpFieldList)
	{
		String _tempTable = "T_FD_" + TempTableName;
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				_tempTable = "##" + _tempTable;
		}
		for(int i=0;i<otherSetFieldList.size();i++)
		{
			String itemid=(String)otherSetFieldList.get(i);
			FieldItem _item = DataDictionary.getFieldItem(itemid);  
			String strKeyField=getKeyField(_item.getFieldsetid());  
			String sDB=_item.getFieldsetid();
			sDB = this.getDbPreTable(sDB);
			StringBuffer _sDB=new StringBuffer("");
			if (!IsMainSet(sDB))
			{
				_sDB.append(" ( SELECT "+sDB+".*  FROM "+sDB+", ");
				_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sDB+" GROUP BY "+strKeyField+" ) AB ");
				_sDB.append(" WHERE "+sDB+"."+strKeyField+"=AB."+strKeyField+" AND "+sDB+".I9999=AB.I9999 )  "+sDB);
			}
			else
				_sDB.append(sDB);
			String tempSQL="update "+_tempTable+" set  "+itemid+"=(select "+itemid+" from "+sDB+" where "+sDB+"."+strKeyField+"="+_tempTable+"."+strKeyField+"  ) ";
			tempSQL+=" where exists (select null from "+sDB+" where "+sDB+"."+strKeyField+"="+_tempTable+"."+strKeyField+"  )";
			SQLS.add(tempSQL); 
		}
		
		for(int i=0;i<tmpFieldList.size();i++)
		{
			String strKeyField="";
			if (InfoGroupFlag == forPerson)
				strKeyField="A0100";
			if (InfoGroupFlag == forPosition) 
				strKeyField="E01A1";
			if (InfoGroupFlag == forUnit) 
				strKeyField="B0110";
			String itemid=(String)tmpFieldList.get(i);
			String tempSQL="update "+_tempTable+" set  "+itemid+"=(select "+itemid+" from "+this.TempTableName+" where "+this.TempTableName+"."+strKeyField+"="+_tempTable+"."+strKeyField+"  ) ";
			tempSQL+=" where exists (select null from "+this.TempTableName+" where "+this.TempTableName+"."+strKeyField+"="+_tempTable+"."+strKeyField+"  )";
			SQLS.add(tempSQL); 
		}
		
		//如果分段计算的是执行标准函数，需加入SQLS中
		if (FSTDSQLS!= null && FSTDSQLS.size() > 0)
		{
			for (int i = 0; i < FSTDSQLS.size(); i++) {
				String tmp = ((String) (FSTDSQLS.get(i))).toUpperCase();	 
				SQLS.add(tmp); 
		    }
		}	
		this.FSTDSQLS=new ArrayList();
	}
	
	
	/**
	 * 判断 分段计算条件 涉及的指标是否属于子集里的。
	 * 
	 * @param map
	 * @return
	 */
	private boolean validateIsSetField(HashMap map, FieldSet fieldset) {
		java.util.Set keySet = map.keySet();
		FieldItem tempItem = null;
		for (Iterator t = keySet.iterator(); t.hasNext();) {
			tempItem = (FieldItem) map.get((String) t.next());
			if (!tempItem.getFieldsetid().equalsIgnoreCase(
					fieldset.getFieldsetid()))
				return false;
		}
		return true;
	}
	
	/**
	 * 统计表单数据
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_SelectSubset(RetValue retValue) throws GeneralException,SQLException {
		FieldItem Field1 = new FieldItem();
		RetValue retValue1 = new RetValue();
		String strWhere = "", strSQL = "";
		int nSQLtype = 0;
		
		if ("统计表单子集".equals(token)) {
			return Func_Cselect_Sub(retValue);
		}
		
		strSQL = SQL.toString();
		CurFuncNum = FUNCSELECT;
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		if (!Get_Token())
			return false; // 指标
		
		if (!level0(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		
		if (IsMainSet(Field.getFieldsetid())) {
			Putback();
			SError(E_MUSTBESUBSET);
			return false;
		}
		Field1 = new FieldItem();
		if (retValue.IsStringType()) {
			Field1.setItemtype("A");
			Field1.setItemlength(80);
		}
		if (retValue.IsDateType()) {
			Field1.setItemtype("D");
			Field1.setItemlength(10);
		}
		if (retValue.isIntType()) {
			Field1.setItemtype("N");
			Field1.setItemlength(10);
		}
		if (retValue.isFloatType()) {
			Field1.setItemtype("N");
			Field1.setItemlength(10);
		}
		Field1 = (FieldItem) Field.cloneItem();
		Field1.setItemid(SQL.toString());
		
		SQL.setLength(0); // 条件
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		if (!level0(retValue1)) {
			Field1 = null;
			return false;
		}
		if (tok != S_COMMA) {
			Field1 = null;
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!retValue1.isBooleanType()) {
			Field1 = null;
			Putback();
			SError(E_MUSTBEBOOL);
			return false;
		}
		strWhere = SQL.toString();
		
		SQL.setLength(0); // SELECT 类型
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		nSQLtype = tok;
		if (!((tok == S_FIRST) || (tok == S_LAST) || (tok == S_MAX)
				|| (tok == S_MIN) || (tok == S_MAX) || (tok == S_AVG)
				|| (tok == S_COUNT) || (tok == S_SUM))) {
			Field1 = null;
			Putback();
			SError(E_MUSTBESQLSYMBOL);
			return false;
		}
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Field1 = null;
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		
		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_SELECT(Field1, strWhere, nSQLtype));
		switch (nSQLtype) {
			case S_SUM:
			case S_AVG:
			case S_COUNT: {
			
				// 需要判断 retValue的原数据类型
				if (retValue.isFloatType()) {
					retValue.setValue(new Float(123.0));
				} else {
					retValue.setValue(new Integer(123));
				}
			}
		}
		CurFuncNum = 0;
		return true;
	}
			
	private boolean Func_SELECT(RetValue retValue) throws GeneralException,
			SQLException {
		FieldItem Field1 = new FieldItem();
		RetValue retValue1 = new RetValue();
		String strWhere = "", strSQL = "";
		int nSQLtype = 0;

		if ("统计".equals(token)) {
			return Func_CSELECT(retValue);
		}

		strSQL = SQL.toString();
		CurFuncNum = FUNCSELECT;
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		if (!Get_Token())
			return false; // 指标

		if (!level0(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		if (IsMainSet(Field.getFieldsetid())) {
			Putback();
			SError(E_MUSTBESUBSET);
			return false;
		}
		Field1 = new FieldItem();
		if (retValue.IsStringType()) {
			Field1.setItemtype("A");
			Field1.setItemlength(80);
		}
		if (retValue.IsDateType()) {
			Field1.setItemtype("D");
			Field1.setItemlength(10);
		}
		if (retValue.isIntType()) {
			Field1.setItemtype("N");
			Field1.setItemlength(10);
		}
		if (retValue.isFloatType()) {
			Field1.setItemtype("N");
			Field1.setItemlength(10);
		}
		Field1 = (FieldItem) Field.cloneItem();
		Field1.setItemid(SQL.toString());

		SQL.setLength(0); // 条件
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		if (!level0(retValue1)) {
			Field1 = null;
			return false;
		}
		if (tok != S_COMMA) {
			Field1 = null;
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!retValue1.isBooleanType()) {
			Field1 = null;
			Putback();
			SError(E_MUSTBEBOOL);
			return false;
		}
		strWhere = SQL.toString();

		SQL.setLength(0); // SELECT 类型
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		nSQLtype = tok;
		if (!((tok == S_FIRST) || (tok == S_LAST) || (tok == S_MAX)
				|| (tok == S_MIN) || (tok == S_MAX) || (tok == S_AVG)
				|| (tok == S_COUNT) || (tok == S_SUM))) {
			Field1 = null;
			Putback();
			SError(E_MUSTBESQLSYMBOL);
			return false;
		}
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
		if (tok != S_RPARENTHESIS) {
			Field1 = null;
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token()) {
			Field1 = null;
			return false;
		}
 
		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_SELECT(Field1, strWhere, nSQLtype));
		switch (nSQLtype) {
		case S_SUM:
		case S_AVG:
		case S_COUNT: {

			// 需要判断 retValue的原数据类型
			if (retValue.isFloatType()) {
				retValue.setValue(new Float(123.0));
			} else {
				retValue.setValue(new Integer(123));
			}
		}
		}
		CurFuncNum = 0;
		return true;
	}

	
	private boolean isStatVar=false; //是否是 统计临时变量函数 
	private boolean Func_CSELECT(RetValue retValue) throws GeneralException,
			SQLException {
		String strSQL = "", strWhere = "";// , strFldName = "";
		FieldItem Field1 = new FieldItem();
		
		boolean parent_hasVarSelectCondition=false;
		if(isFuncSelectCondition)//统计函数条件包含统计函数时
			parent_hasVarSelectCondition=true;

		int nSQLtype;
		RetValue retValue1 = new RetValue();
		strSQL = SQL.toString();
		SQL.setLength(0);
		CurFuncNum = FUNCSELECT; 
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}
		
		
		if (!Get_Token())
			return false; // 指标
		
		if(this.token_type==this.ODDVAR)  //临时变量    统计 yk1=事假,yk2=病假  满足 月(a41z0)=12 且 年(a41z0)=年(截止日期)-1 的最近第一条记录
		{
			isStatVar=true;
			this.statVarList=new ArrayList();
			ArrayList ykList=new ArrayList();
			String _setid="";
			while(true)
			{
				FieldItem _ykItem=(FieldItem)this.Field.clone();
				SQL.setLength(0);
				
				if (!Get_Token())
					return false;
				if(tok!= S_EQUAL) // !=等号
				{
					Putback();
					SError(E_SYNTAX);
					return false;
				}
				if (!Get_Token())
					return false;
				if (!level0(retValue))
					return false;
				if (IsMainSet(Field.getFieldsetid())) {
					Putback();
					SError(E_MUSTBESUBSET);
					return false;
				}
				if(_setid.length()==0)
					_setid=Field.getFieldsetid();
				else if(!_setid.equalsIgnoreCase(Field.getFieldsetid()))
				{
					Putback();
					SError(E_MUSTBESAMESET);
					return false;
				} 
				
				Field1 = new FieldItem();
				Field1.setItemdesc(Field.getItemdesc());
				Field1.setItemid(SQL.toString());
				Field1.setItemlength(Field.getItemlength());
				Field1.setItemtype(Field.getItemtype());
				Field1.setCodesetid(Field.getFieldsetid());
				Field1.setFieldsetid(Field.getFieldsetid());
				if (retValue.IsStringType()) {
					Field1.setItemtype("A");
					Field1.setItemlength(80);
				}
				if (retValue.IsDateType()) {
					Field1.setItemtype("D");
					Field1.setItemlength(10);
				}
				if (retValue.isIntType()) {
					Field1.setItemtype("N");
					Field1.setItemlength(10);
				}
				if (retValue.isFloatType()) {
					Field1.setItemtype("N");
					Field1.setItemlength(10);
					Field1.setDecimalwidth(Field.getDecimalwidth());
				}
				// strFldName =
				/**
				 * 分析SQL是否为指标，如果为指标时，前面需要加上子集名或库前缀 直接根据SQL串的长度来区分
				 */
				if (SQL.length() == 5)
					GetCurMenu(true, Field1);
				else
					GetCurMenu(getAddTableName(), Field1);
				
				LazyDynaBean _bean=new LazyDynaBean();
				_bean.set("_var", _ykItem);
				_bean.set("_item", Field1);
				this.statVarList.add(_ykItem.getItemid());
				
				if (tok == S_SATISFY) 
				{
					ykList.add(_bean);
					break;
				}
				if (tok != S_COMMA) {
					Putback();
					SError(E_LOSSCOMMA);
					return false;
				}
				
				ykList.add(_bean);
				if (!Get_Token())
					return false;
			}
		 
			SQL.setLength(0); // 条件
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			isFuncSelectCondition=true;  //是否是统计函数定义的条件,解决条件中定义了函数造成SQL指标未明确定义列问题
			hasVarSelectCondition=false;
			if (!level0(retValue1)) {
				Field1 = null;
				return false;
			}
			isFuncSelectCondition=false;  //是否是统计函数定义的条件,解决条件中定义了函数造成SQL指标未明确定义列问题
			if (!retValue1.isBooleanType()) {
				Field1 = null;
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}
			strWhere = SQL.toString();
			nSQLtype = tok;
			if (!((tok == S_FIRST) || (tok == S_LAST) || (tok == S_MAX)
					|| (tok == S_MIN) || (tok == S_MAX) || (tok == S_AVG)
					|| (tok == S_COUNT) || (tok == S_SUM))) {
				Field1 = null;
				Putback();
				SError(E_MUSTBESQLSYMBOL);
				return false;
			}
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			this.isStatMultipleVar=true;
			if(strWhere!=null&&strWhere.trim().length()>0&&!"TRUE".equals(strWhere)) //20141126   dengcan 统计条件没加括号
				strWhere=" ( "+strWhere+" ) ";
			SQL_SELECT2(ykList, strWhere, nSQLtype);
			SQL.setLength(0);
			SQL.append(strSQL).append("NULL"); //(SQL_SELECT(Field1, strWhere, nSQLtype));
			switch (nSQLtype) {
			case S_SUM:
			case S_AVG:
			case S_COUNT: {
				// retValue=123;
				retValue.setValue(new Integer(123));
				retValue.setValueType(INT);
			}
			}
			CurFuncNum = 0;
			
			return true;
		}
		else
		{
			isStatVar=false;
			ArrayList usedsets=this.UsedSets;
			this.UsedSets=new ArrayList();
			
			if (!level0(retValue))
				return false;
			if (tok != S_SATISFY) {
				Putback();
				SError(E_LOSSSATISFY);
				return false;
			}
			if (IsMainSet(Field.getFieldsetid())) {
				Putback();
				SError(E_MUSTBESUBSET);
				return false;
			}
			// FieldCopy(Field,Field1);
			// Field1=Field.clone();???????????????????????????????????????
			Field1.setItemdesc(Field.getItemdesc());
			Field1.setItemid(/* Field.getItemid() */SQL.toString());
			Field1.setItemlength(Field.getItemlength());
			Field1.setItemtype(Field.getItemtype());
			Field1.setCodesetid(Field.getFieldsetid());
			Field1.setFieldsetid(Field.getFieldsetid());
	
			if (retValue.IsStringType()) {
				Field1.setItemtype("A");
				Field1.setItemlength(80);
			}
			if (retValue.IsDateType()) {
				Field1.setItemtype("D");
				Field1.setItemlength(10);
			}
			if (retValue.isIntType()) {
				Field1.setItemtype("N");
				Field1.setItemlength(10);
			}
			if (retValue.isFloatType()) {
				Field1.setItemtype("N");
				Field1.setItemlength(10);
				Field1.setDecimalwidth(Field.getDecimalwidth());
				// Field1.nFldDec :=2;??????????????
			}
			// strFldName =
			/**
			 * 分析SQL是否为指标，如果为指标时，前面需要加上子集名或库前缀 直接根据SQL串的长度来区分
			 */
			if (SQL.length() == 5)
				GetCurMenu(true, Field1);
			else
				GetCurMenu(getAddTableName(), Field1);
	
			SQL.setLength(0); // 条件
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			isFuncSelectCondition=true;  //是否是统计函数定义的条件,解决条件中定义了函数造成SQL指标未明确定义列问题	
			hasVarSelectCondition=false;
			HashMap _mapUsedFieldItems =(HashMap)this.mapUsedFieldItems.clone();     // 所用到的指标代号 
			if (!level0(retValue1)) {
				Field1 = null;
				return false;
			}
		  
			//统计函数 实现 临时变量作为条件 20160701
			if(StdTmpTable != null && StdTmpTable.length() > 0)
			{
				DbWizard dbWizard = new DbWizard(this.con);
				Iterator it = this.mapUsedFieldItems.keySet().iterator(); 
				FieldItem fieldTemp=null;
				while (it.hasNext()) {
					String _key=(String)it.next();
					if(_mapUsedFieldItems.get(_key)==null)
					{
						fieldTemp = (FieldItem) this.mapUsedFieldItems.get(_key);
						StringBuffer _strSQL=new StringBuffer("");
						if(fieldTemp.getVarible()==1)
						{
							String fieldname = fieldTemp.getItemid().toUpperCase();
							if(!dbWizard.isExistField(StdTmpTable, fieldname, false))
								continue;
							 
							if (!(TempTableName.equalsIgnoreCase(StdTmpTable) || TempTableName.length() == 0))
							{
								String varcond = getVarCond(StdTmpTable);
								String key="A0100";
								if(this.InfoGroupFlag ==YksjParser.forUnit)
								{ 
									varcond=" 1=1";
									key="B0110";
								}
								else if(InfoGroupFlag == YksjParser.forPosition)
								{ 
									varcond=" 1=1";
									key="E01A1";
								}  
								_strSQL.append("update ");
								_strSQL.append(TempTableName+" set ");
								
								_strSQL.append(TempTableName);
								_strSQL.append(".");
								_strSQL.append(fieldname);
								_strSQL.append("=( select ");
								_strSQL.append(StdTmpTable);
								_strSQL.append(".");
								_strSQL.append(fieldname); 
								_strSQL.append(" from ");
								_strSQL.append(StdTmpTable);
								_strSQL.append(" where ");
								_strSQL.append(TempTableName);
								_strSQL.append("."+key+"=");
								_strSQL.append(StdTmpTable);
								_strSQL.append("."+key+"");
								if(StdTmpTable_where!=null&&StdTmpTable_where.trim().length()>0)//bug 33029 再查流程中表时可能会返回多条数据报错
								{
									//bug38216 人事异动流程中计算 传递的sql会包含exists t_wf_task_objlink 这种特殊情况，需要特殊处理
									if(StdTmpTable_where.toLowerCase().indexOf("exists")!=-1&&StdTmpTable_where.toLowerCase().indexOf("t_wf_task_objlink")!=-1) {
										_strSQL.append( StdTmpTable_where);
									}else {
										int whereIndex=StdTmpTable_where.trim().toLowerCase().indexOf("where");
										if(whereIndex==0)
											_strSQL.append( StdTmpTable_where);
										else{
											_strSQL.append(" and ");
											_strSQL.append( StdTmpTable_where.trim().substring(whereIndex+5));
										}
									}
								}
									/** 库过滤条件 */
								_strSQL.append(" and ");
								_strSQL.append(varcond +" ) where  exists (select null ");
								_strSQL.append(" from ");
								_strSQL.append(StdTmpTable);
								_strSQL.append(" where ");
								_strSQL.append(TempTableName);
								_strSQL.append("."+key+"=");
								_strSQL.append(StdTmpTable);
								_strSQL.append("."+key+"");
								if(StdTmpTable_where!=null&&StdTmpTable_where.trim().length()>0) //bug 33029 在查流程中表时可能会返回多条数据报错
								{
									//bug38216 人事异动流程中计算 传递的sql会包含exists t_wf_task_objlink 这种特殊情况，需要特殊处理
									if(StdTmpTable_where.toLowerCase().indexOf("exists")!=-1&&StdTmpTable_where.toLowerCase().indexOf("t_wf_task_objlink")!=-1) {
										_strSQL.append( StdTmpTable_where);
									}else {
										int whereIndex=StdTmpTable_where.trim().toLowerCase().indexOf("where");
										if(whereIndex==0)
											_strSQL.append( StdTmpTable_where);
										else{
											_strSQL.append(" and ");
											_strSQL.append( StdTmpTable_where.trim().substring(whereIndex+5));
										}
									}
								}
									/** 库过滤条件 */
								_strSQL.append(" and ");
								_strSQL.append(varcond +" ) ");
								SQLS.add(_strSQL.toString());
								hasVarSelectCondition=true;
							}
						}
					}
				} 
			}
			isFuncSelectCondition=false;
			
			if (!retValue1.isBooleanType()) {
				Field1 = null;
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}
			strWhere = SQL.toString();
	
			nSQLtype = tok;
			if (!((tok == S_FIRST) || (tok == S_LAST) || (tok == S_MAX)
					|| (tok == S_MIN) || (tok == S_MAX) || (tok == S_AVG)
					|| (tok == S_COUNT) || (tok == S_SUM))) {
				Field1 = null;
				Putback();
				SError(E_MUSTBESQLSYMBOL);
				return false;
			}
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
	
			SQL.setLength(0);
			 
			if(parent_hasVarSelectCondition)
				hasVarSelectCondition=true;
			
			if(strWhere!=null&&strWhere.trim().length()>0&&!"TRUE".equals(strWhere)) //20141126   dengcan 统计条件没加括号
				strWhere=" ( "+strWhere+" ) ";
			SQL.append(strSQL).append(SQL_SELECT(Field1, strWhere, nSQLtype));
			switch (nSQLtype) {
			case S_SUM:
			case S_AVG:
			case S_COUNT: {
				// retValue=123;
				retValue.setValue(new Integer(123));
				retValue.setValueType(INT);
			}
			}
			
			 
			 
			 
			String temp="";
			for (int k = 0; k < UsedSets.size(); k++) {
				String sSetName = (String) UsedSets.get(k);
				boolean flag=false;
				for(int j=0;j<usedsets.size();j++)
				{
					temp=(String)usedsets.get(j);
					if(temp.equalsIgnoreCase(sSetName))
						flag=true;
				}
				if(!flag)
					usedsets.add(sSetName);
			}
			this.UsedSets=new ArrayList();
			UsedSets=usedsets; 
			
   
   
			CurFuncNum = 0;
			return true;
		}
//		return true;
	}
	
	
	private void  SQL_SELECT2(ArrayList ykList, String strWhere, int nSQLtype) {
		StringBuffer strMaxMin = new StringBuffer();
		StringBuffer strKeyField = new StringBuffer();
		 
		StringBuffer strInto = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strCurSetTableName = new StringBuffer();
	//	StringBuffer strCurMenu = new StringBuffer();
		boolean bExist = false;
		 
		LazyDynaBean _bean=(LazyDynaBean)ykList.get(0);
		FieldItem _item=(FieldItem)_bean.get("_item");

		// 加上库前缀
		strCurSetTableName.append(GetCurSet(_item));

		if ("TRUE".equals(strWhere)) {
			strWhere = "1=1";
		}
		LazyDynaBean _bean2=null;
		FieldItem _item2=null;
		ArrayList _list=new ArrayList();
		for(int i=0;i<ykList.size();i++)
		{
			 _bean=(LazyDynaBean)ykList.get(i);
			 _item=(FieldItem)_bean.get("_item");
			 _item2=(FieldItem)_bean.get("_var");
			 
			 _bean2=new LazyDynaBean();
			 _bean2.set("strCurMenu",_item.getItemid());
			 
			 _item.setItemdesc(_item.getItemid() + "_" + strWhere.trim() + "_"
					+ nSQLtype);// 是否有此SELECT函数
			 _item.setVarible(2);
	
			 
			Iterator it = mapUsedFieldItems.values().iterator();
			while (it.hasNext()) {
				FieldItem usedTemp = (FieldItem) it.next();
				if (_item.getItemdesc().equals(usedTemp.getItemdesc())) {
					_item.setItemid(usedTemp.getItemid());
					bExist = true;
				}
			}
			if (!bExist) {
				_item.setItemid(_item2.getItemid());
			}
			mapUsedFieldItems.put(_item2.getItemid(), _item2);
			if (nSQLtype == S_COUNT) {
				_item.setItemtype("N");
				_item.setItemlength(10);
			}
			
			_bean2.set("strAs",_item.getItemid());
			_list.add(_bean2);
		}
		
		
		
		
		
		
		
		strInto.setLength(0);
		strInto.append("T_").append(TempTableName);
		if (DBType == Constant.MSSQL) {
			strInto.setLength(0);
			if (this.isTempTable)
				strInto.append("##T_").append(TempTableName);
			else
				strInto.append("T_").append(TempTableName);
		}

		String _tempTable = "TT_" + TempTableName;
		if (DBType == Constant.MSSQL && this.isTempTable)
			_tempTable = "##TT_" + TempTableName;
 
		strKeyField.setLength(0);
		strKeyField.append(getKeyField(_item.getFieldsetid())); 
		
		if (DBType == Constant.MSSQL) {
			SQLS.add("DROP TABLE " + strInto.toString());
		} else {
		//	if (this.isTempTable)
		//		SQLS.add("truncate table " + strInto.toString());
			SQLS.add("DROP TABLE " + strInto.toString()+" purge");
		}

		if (nSQLtype == S_FIRST) {
			strMaxMin.setLength(0);
			strMaxMin.append("MIN");
		} else if (nSQLtype == S_LAST) {
			strMaxMin.setLength(0);
			strMaxMin.append("MAX");
		}

		strSQL.setLength(0);
		strSQL.append("SELECT ").append(strCurSetTableName).append('.').append(
				strKeyField);
		for(int i=0;i<_list.size();i++)
		{
			_bean2=(LazyDynaBean)_list.get(i);
			String strCurMenu=(String)_bean2.get("strCurMenu");
			String strAs=(String)_bean2.get("strAs");
			
			strSQL.append(',');
			switch (nSQLtype) {
			case S_FIRST: {
				strSQL.append(" (").append(strCurMenu).append(')');
				break;
			}
			case S_LAST: {
				strSQL.append(" (").append(strCurMenu).append(')');
				break;
			}
			case S_MAX: {
				if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
					strSQL.append(" MAX(").append(
							strCurSetTableName + "." + strCurMenu).append(')');
				else
					strSQL.append(" MAX(").append(strCurMenu).append(')');
				break;
			}
			case S_MIN: {
				if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
					strSQL.append(" MIN(").append(
							strCurSetTableName + "." + strCurMenu).append(')');
				else
					strSQL.append(" MIN(").append(strCurMenu).append(')');
				break;
			}
			case S_SUM: {
				if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
					strSQL.append(" SUM(") // chenmengqing added
											// ./*strCurSetTableName + "." +*/
											// for统计函数去掉
							.append(strCurSetTableName + "." + strCurMenu).append(
									')');
				else
					strSQL.append(" SUM(") // chenmengqing added
											// ./*strCurSetTableName + "." +*/
											// for统计函数去掉
							.append(strCurMenu).append(')');
				break;
			}
			case S_AVG: {
				if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
					strSQL.append(" AVG(").append(
							strCurSetTableName + "." + strCurMenu).append(')');
				else
					strSQL.append(" AVG(").append(strCurMenu).append(')');
				break;
			}
			}
			
			strSQL.append(" AS ").append(strAs);
		}
		
		
		 
		
		
		boolean isCreateTable=false;
		
		switch (nSQLtype) {
		case S_FIRST:
		case S_LAST: {

			if (DBType == Constant.MSSQL)
				SQLS.add("DROP TABLE " + _tempTable);
			else {
			//	if (this.isTempTable)
			//		SQLS.add("truncate TABLE " + _tempTable);
				SQLS.add("DROP TABLE " + _tempTable+" purge");
			}

			if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				strSQL.append("CREATE ");
				if (this.isTempTable)
					strSQL.append(" GLOBAL TEMPORARY ");
				strSQL.append(" TABLE  ").append(_tempTable);
				if (this.isTempTable)
					strSQL.append(" On Commit Preserve Rows ");
				strSQL.append(" AS SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append("(")
						.append(strCurSetTableName).append(".I9999) AS I9999")
						.append(" FROM ").append(strCurSetTableName)
						.append(",").append(TempTableName);

				/** 处理条件中涉及相关子集情况 */
				String sSetName;
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						strSQL.append(",");
						strSQL.append(sSetName);
					}
				}

				strSQL.append(" WHERE ").append(strCurSetTableName).append(".")
						.append(strKeyField).append("=").append(TempTableName)
						.append(".").append(strKeyField);
				/** 处理条件中涉及相关子集情况 */
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						strSQL.append(" AND ");
						strSQL.append(strCurSetTableName);
						strSQL.append(".");
						strSQL.append(strKeyField);
						strSQL.append("=");
						strSQL.append(sSetName);
						strSQL.append(".");
						strSQL.append(strKeyField);
					}
				}

				strSQL.append(" AND ").append(strWhere).append(" GROUP BY ")
						.append(strCurSetTableName).append(".").append(
								strKeyField);
			} else {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append("(")
						.append(strCurSetTableName).append(".I9999) AS I9999");
				strSQL.append(" INTO ").append(_tempTable);
				strSQL.append(" FROM ").append(strCurSetTableName);
				/** 处理条件中涉及相关子集情况 */
				String sSetName;
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						strSQL.append(" LEFT JOIN ");
						strSQL.append(sSetName);
						strSQL.append(" ON ");
						strSQL.append(strCurSetTableName);
						strSQL.append(".");
						strSQL.append(strKeyField);
						strSQL.append("=");
						strSQL.append(sSetName);
						strSQL.append(".");
						strSQL.append(strKeyField);
					}
				}
				
				strSQL.append(" LEFT JOIN ").append(TempTableName);
				strSQL.append(" ON ").append(strCurSetTableName).append(".")
						.append(strKeyField).append("=").append(TempTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
			}
			SQLS.add(strSQL.toString());
			SQLS.add("create index "+_tempTable+"_index  on "+_tempTable+" ("+getKeyField(_item.getFieldsetid())+",I9999)");
			 
			
			if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				strSQL.append("CREATE ");
				if (this.isTempTable)
					strSQL.append(" GLOBAL TEMPORARY ");
				strSQL.append(" TABLE ").append(strInto);
				if (this.isTempTable)
					strSQL.append(" On Commit Preserve Rows ");
				strSQL.append(" AS (SELECT ").append(strCurSetTableName)
						.append(".").append(strKeyField);
				
				for(int i=0;i<_list.size();i++)
				{
					_bean2=(LazyDynaBean)_list.get(i);
					String strCurMenu=(String)_bean2.get("strCurMenu");
					String strAs=(String)_bean2.get("strAs");
					strSQL.append(",").append(
									" (").append(strCurMenu).append(")").append(
									" AS ").append(strAs);
				
				}
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(",").append(_tempTable);
				strSQL.append(" WHERE ").append(_tempTable).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" AND ").append(strCurSetTableName).append(
						".I9999=").append(_tempTable).append(".I9999)");
			} else {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField);
				for(int i=0;i<_list.size();i++)
				{
					_bean2=(LazyDynaBean)_list.get(i);
					String strCurMenu=(String)_bean2.get("strCurMenu");
					String strAs=(String)_bean2.get("strAs");
					strSQL.append(",").append(" (").append(
									strCurMenu).append(")").append(" AS ").append(
									strAs);
				}
				strSQL.append(" INTO ").append(strInto);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" LEFT JOIN  ").append(_tempTable);
				strSQL.append(" ON ").append(_tempTable).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strCurSetTableName).append(
						".I9999=").append(_tempTable).append(".I9999");
			}
			isCreateTable=true;
			break;
		}
		case S_MAX:
		case S_MIN:
		case S_SUM:
			{
				if (DBType == Constant.ORACEL) {
					StringBuffer buffer = new StringBuffer(strCurSetTableName
							.toString());
					if (UsedSets.size() >= 2) {
						for (int i = 0; i < UsedSets.size(); i++) {
							String sDB = (String) UsedSets.get(i);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a') {

								sDB = this.getDbPreTable(sDB);
							}
							if (sDB.equals(strCurSetTableName.toString())) {
								continue;
							}
							buffer.append(" LEFT Join " + sDB + " ON "
									+ strCurSetTableName + '.' + strKeyField
									+ '=' + sDB + '.' + strKeyField + " ");
						}
					} else {

					}
					// System.out.println(strSQL);
					// strSQL.setLength(0);
					
					strSQL.append(" FROM ").append(buffer);
					strSQL.append(" WHERE ");
					strSQL.append(TempTableName);
					strSQL.append(".");
					strSQL.append(strKeyField + "="
							+ strCurSetTableName + "." + strKeyField + " and "
							+ strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);

					String str = " update " + " " + TempTableName + " set ("
							+ strKeyField;
					for(int i=0;i<_list.size();i++)
					{
						_bean2=(LazyDynaBean)_list.get(i);
						String strCurMenu=(String)_bean2.get("strCurMenu");
						String strAs=(String)_bean2.get("strAs");
						str+="," + strAs;
					}
					str+=")=( "
							+ strSQL.toString() + ")  ";
					// if (UsedSets.size()<2)
					str += "  where exists  ( select null "
							+ strSQL.substring(strSQL.indexOf(" FROM")) + ") ";
					strSQL.setLength(0);
					strSQL.append(str);
				} else {
					StringBuffer buffer = new StringBuffer(strCurSetTableName
							.toString());
					if (UsedSets.size() >= 2) {
						for (int i = 0; i < UsedSets.size(); i++) {
							String sDB = (String) UsedSets.get(i);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a') {

								sDB = this.getDbPreTable(sDB);
							}
							if (sDB.equals(strCurSetTableName.toString())) {
								continue;
							}
							buffer.append(" LEFT Join " + sDB + " ON "
									+ strCurSetTableName + '.' + strKeyField
									+ '=' + sDB + '.' + strKeyField + " ");
						}
					} else {
						buffer.append(" LEFT JOIN ").append(TempTableName);
						buffer.append(" ON ").append(TempTableName).append(".")
								.append(strKeyField).append("=").append(
										strCurSetTableName).append(".").append(
										strKeyField);
					}
					strSQL.append(" INTO ")
							.append(strInto);
					strSQL.append(" FROM ");
					strSQL.append(buffer);
					strSQL.append(" WHERE ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
					isCreateTable=true;
				}
				break;
			}
		case S_AVG: {
			{
				if (DBType == Constant.ORACEL) {

					if (this.isTempTable)
						strSQL.insert(0, "CREATE   GLOBAL TEMPORARY  TABLE   "
								+ strInto + "  On Commit Preserve Rows   AS ");
					else
						strSQL
								.insert(0, "CREATE   TABLE   " + strInto
										+ " AS ");

				
					strSQL.append(" FROM ").append(strCurSetTableName);
					strSQL.append(" ,").append(TempTableName);

					/** 处理条件中涉及相关子集情况 */
					String sSetName;
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.isPerson())
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							strSQL.append(",");
							strSQL.append(sSetName);
						}
					}
					strSQL.append(" WHERE ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);

					/** 处理条件中涉及相关子集情况 */
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a')
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							strSQL.append(" AND ");
							strSQL.append(strCurSetTableName);
							strSQL.append(".");
							strSQL.append(strKeyField);
							strSQL.append("=");
							strSQL.append(sSetName);
							strSQL.append(".");
							strSQL.append(strKeyField);
						}
					}

					strSQL.append(" AND ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
				} else {
					strSQL.append(" INTO ")
							.append(strInto);
					strSQL.append(" FROM ").append(strCurSetTableName);
					strSQL.append(" LEFT JOIN ").append(TempTableName);
					strSQL.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);

					/** 处理条件中涉及相关子集情况 */
					String sSetName;
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a')
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							strSQL.append(" LEFT JOIN ");
							strSQL.append(sSetName);
							strSQL.append(" ON ");
							strSQL.append(strCurSetTableName);
							strSQL.append(".");
							strSQL.append(strKeyField);
							strSQL.append("=");
							strSQL.append(sSetName);
							strSQL.append(".");
							strSQL.append(strKeyField);
						}
					}
					strSQL.append(" WHERE ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
				}
				isCreateTable=true;
			}
			break;
		}
			// 统计个数时不能与主集左联结，否则没有记录时统计成1
		case S_COUNT: {

			if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField);
				
				for(int i=0;i<_list.size();i++)
				{
					_bean2=(LazyDynaBean)_list.get(i);
					String strCurMenu=(String)_bean2.get("strCurMenu");
					String strAs=(String)_bean2.get("strAs");
					strSQL.append(",COUNT(*) AS ").append(
									strAs);
				}
				StringBuffer buffer = new StringBuffer(strCurSetTableName
						.toString());
				if (UsedSets.size() >= 2) {
					for (int i = 0; i < UsedSets.size(); i++) {
						String sDB = (String) UsedSets.get(i);
						if (Field.getFieldsetid().charAt(0) == 'A'
								|| Field.getFieldsetid().charAt(0) == 'a') {

							sDB = this.getDbPreTable(sDB);
						}
						if (sDB.equals(strCurSetTableName.toString())) {
							continue;
						}
						buffer.append(" LEFT Join " + sDB + " ON "
								+ strCurSetTableName + '.' + strKeyField + '='
								+ sDB + '.' + strKeyField + " ");
					}
				} else {
					buffer.append(" LEFT JOIN ").append(TempTableName);
					buffer.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);
				}

				strSQL.append(" FROM ").append(buffer.toString()).append(
						" WHERE ").append(strWhere);
				// strSQL.append(" GROUP BY ").append(strKeyField);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);

				if (this.isTempTable)
					SQLS.add("CREATE  GLOBAL TEMPORARY TABLE "
							+ strInto.toString()
							+ " On Commit Preserve Rows   AS ("
							+ strSQL.toString() + ")");
				else
					SQLS.add("CREATE   TABLE " + strInto.toString() + "  AS ("
							+ strSQL.toString() + ")");
				isCreateTable=true;
			} else {
				isCreateTable=true;
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField);
				
				for(int i=0;i<_list.size();i++)
				{
					_bean2=(LazyDynaBean)_list.get(i);
					String strCurMenu=(String)_bean2.get("strCurMenu");
					String strAs=(String)_bean2.get("strAs");
				 
			 
					strSQL.append(",COUNT(*) AS ").append(
								strAs);
				}
				strSQL.append(" INTO ").append(strInto);
				StringBuffer buffer = new StringBuffer(strCurSetTableName
						.toString());
				if (UsedSets.size() >= 2) {
					for (int i = 0; i < UsedSets.size(); i++) {
						String sDB = (String) UsedSets.get(i);
						if (Field.getFieldsetid().charAt(0) == 'A'
								|| Field.getFieldsetid().charAt(0) == 'a') {

							sDB = this.getDbPreTable(sDB);
						}
						if (sDB.equals(strCurSetTableName.toString())) {
							continue;
						}
						buffer.append(" LEFT Join " + sDB + " ON "
								+ strCurSetTableName + '.' + strKeyField + '='
								+ sDB + '.' + strKeyField + " ");
					}
				} else {
					buffer.append(" LEFT JOIN ").append(TempTableName);
					buffer.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);
				}

				strSQL.append(" FROM ").append(buffer.toString()).append(
						" WHERE ").append(strWhere);
				// strSQL.append(" GROUP BY ").append(strKeyField);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
			}
			break;
		}
		}
		SQLS.add(strSQL.toString());
		
		String _str="";
		for(int i=0;i<_list.size();i++)
		{
			_bean2=(LazyDynaBean)_list.get(i);
			String strCurMenu=(String)_bean2.get("strCurMenu");
			String strAs=(String)_bean2.get("strAs");
			_str+=","+strAs;
		}
		if(isCreateTable)
		{
			//SQLS.add("create index "+strInto+"_index  on "+strInto+" ("+_str.substring(1)+","+strKeyField+")");
			SQLS.add("create index "+strInto+"_index  on "+strInto+" ("+strKeyField+")");
		}
		 
		 
		
		strSQL.setLength(0);
		switch (DBType) {
		case 2:
			if (!(nSQLtype == S_SUM || nSQLtype == S_MAX || nSQLtype == S_MIN)) {
				strSQL.setLength(0);
				
				String _str1="";
				String _str2="";
				for(int i=0;i<_list.size();i++)
				{
					_bean2=(LazyDynaBean)_list.get(i);
					String strCurMenu=(String)_bean2.get("strCurMenu");
					String strAs=(String)_bean2.get("strAs");
					_str1+=","+strAs;
					_str2+=","+strInto+"."+strAs;
				}
				
				
				
				strSQL.append("UPDATE ").append(TempTableName);
				strSQL.append(" SET ").append("("+_str1.substring(1)+")")
						.append("=(SELECT ").append(_str2.substring(1));
				strSQL.append(" FROM ").append(strInto);
				strSQL.append(" WHERE ").append(TempTableName).append(".")
						.append(strKeyField).append("=").append(strInto)
						.append(".").append(strKeyField).append(")");
			}
			break;
		
		case 1: {
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName);
			strSQL.append(" SET ");
			
			String _str1="";
			for(int i=0;i<_list.size();i++)
			{
				_bean2=(LazyDynaBean)_list.get(i);
				String strCurMenu=(String)_bean2.get("strCurMenu");
				String strAs=(String)_bean2.get("strAs");
				_str1+=","+TempTableName+"."+strAs+"="+strInto+"."+strAs;
				
			}
			strSQL.append(_str1.substring(1));
			 
			strSQL.append(" FROM ").append(TempTableName).append(" LEFT JOIN ")
					.append(strInto);
			strSQL.append(" ON ").append(TempTableName).append(".").append(
					strKeyField).append("=").append(strInto).append(".")
					.append(strKeyField);
			break;
		}
		case 0: {
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName)
					.append(" LEFT JOIN ").append(strInto);
			strSQL.append(" ON ").append(TempTableName).append(".").append(
					strKeyField).append("=").append(strInto).append(".")
					.append(strKeyField);
			strSQL.append(" SET ");
			
			String _str1="";
			for(int i=0;i<_list.size();i++)
			{
				_bean2=(LazyDynaBean)_list.get(i);
				String strCurMenu=(String)_bean2.get("strCurMenu");
				String strAs=(String)_bean2.get("strAs");
				_str1+=","+TempTableName+"."+strAs+"="+strInto+"."+strAs;
				
			}
			
			strSQL.append(_str1.substring(1));
			
			break;
		}
		}
		if (!"".equals(strSQL.toString()))
			SQLS.add(strSQL.toString());

	}
	
	private boolean Func_Cselect_Sub(RetValue retValue) throws GeneralException,SQLException {
		String strSQL = "", strWhere = "";// , strFldName = "";
		FieldItem Field1 = new FieldItem();
		try {
			if(isFuncSelectCondition) {//统计函数条件包含统计函数时，对于统计表单数据函数不支持
				Putback();
				SError(E_CANTINCLUDECOMPLEX);
				return false;
			}
			
			int nSQLtype;
			RetValue retValue1 = new RetValue();
			
			boolean subSet_flag = false;
			if (!Get_Token()) {
				return false;
			}
			
			if (!Get_Token())
				return false; // 指标
			
			if(this.token_type==this.ODDVAR) {
				Putback();
				SError(E_CANTINCLUDECOMPLEX);
				return false;
			}
			
			isStatVar=false;
			ArrayList usedsets=this.UsedSets;
			this.UsedSets=new ArrayList();
			
			if (!level0(retValue))
				return false;
			
			if (tok != S_SATISFY) {//汉字：满足
				Putback();
				SError(E_LOSSSATISFY);
				return false;
			}
			
			Field1.setItemdesc(Field.getItemdesc());
			Field1.setItemid(/* Field.getItemid() */SQL.toString());
			Field1.setItemlength(Field.getItemlength());
			Field1.setItemtype(Field.getItemtype());
			Field1.setCodesetid(Field.getFieldsetid());
			Field1.setFieldsetid(Field.getFieldsetid());
		
			if (retValue.IsStringType()) {
				Field1.setItemtype("A");
				Field1.setItemlength(80);
			}
			if (retValue.IsDateType()) {
				Field1.setItemtype("D");
				Field1.setItemlength(10);
			}
			if (retValue.isIntType()) {
				Field1.setItemtype("N");
				Field1.setItemlength(10);
			}
			if (retValue.isFloatType()) {
				Field1.setItemtype("N");
				Field1.setItemlength(10);
				Field1.setDecimalwidth(Field.getDecimalwidth());
			}
			/**
			 * 分析SQL是否为指标，如果为指标时，前面需要加上子集名或库前缀 直接根据SQL串的长度来区分
			 */
			
			isSelectSubSet = true;
			if (SQL.length() == 5)
				GetCurMenu(true, Field1);
			else
				GetCurMenu(getAddTableName(), Field1);
		
			SQL.setLength(0); // 条件
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			isFuncSelectCondition=true;  //是否是统计函数定义的条件,解决条件中定义了函数造成SQL指标未明确定义列问题	
			hasVarSelectCondition=false;
			HashMap _mapUsedFieldItems =(HashMap)this.mapUsedFieldItems.clone();     // 所用到的指标代号 
			if (!level0(retValue1)) {
				Field1 = null;
				return false;
			}
		  
			isFuncSelectCondition=false;
			
			if (!retValue1.isBooleanType()) {
				Field1 = null;
				Putback();
				SError(E_MUSTBEBOOL);
				return false;
			}
			strWhere = SQL.toString();
		
			nSQLtype = tok;
			if (!((tok == S_FIRST) || (tok == S_LAST) || (tok == S_MAX)
					|| (tok == S_MIN) || (tok == S_MAX) || (tok == S_AVG)
					|| (tok == S_COUNT) || (tok == S_SUM))) {
				Field1 = null;
				Putback();
				SError(E_MUSTBESQLSYMBOL);
				return false;
			}
			if (!Get_Token()) {
				Field1 = null;
				return false;
			}
			
			StringBuffer sql_h2 = new StringBuffer();
			sql_h2.setLength(0);
			
			if(!isVerify) {
				if(strWhere!=null&&strWhere.trim().length()>0&&!"TRUE".equals(strWhere)) //20141126   dengcan 统计条件没加括号
					strWhere=" ( "+strWhere+" ) ";
				
				sql_h2.append(strSQL).append(SQL_select_subSet(Field1, strWhere, nSQLtype));
				H2JdbcUtil.executeUpdate(sql_h2.toString(), new ArrayList());
			}
			switch (nSQLtype) {
			case S_SUM:
			case S_AVG:
			case S_COUNT: {
				// retValue=123;
				retValue.setValue(new Integer(123));
				retValue.setValueType(INT);
			}
			}
			
			String temp="";
			for (int k = 0; k < UsedSets.size(); k++) {
				String sSetName = (String) UsedSets.get(k);
				boolean flag=false;
				for(int j=0;j<usedsets.size();j++)
				{
					temp=(String)usedsets.get(j);
					if(temp.equalsIgnoreCase(sSetName))
						flag=true;
				}
				if(!flag)
					usedsets.add(sSetName);
			}
			this.UsedSets=new ArrayList();
			UsedSets=usedsets; 
			isSelectSubSet = false;
			CurFuncNum = 0;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	private String SQL_select_subSet(FieldItem field1, String strWhere, int nSQLtype) {
		StringBuffer strKeyField = new StringBuffer();
		StringBuffer strInto = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strCurSetTableName = new StringBuffer();
		StringBuffer strCurMenu = new StringBuffer();

		// 加上库前缀
		strCurSetTableName.append(GetCurSet(field1));

		strCurMenu.append(field1.getItemid()); // GetCurMenu(TRUE,Field1);

		if ("TRUE".equals(strWhere) || StringUtils.isBlank(strWhere)) {
			strWhere = "1=1";
		}

		strKeyField.setLength(0);
		strKeyField.append(getKeyField(field1.getFieldsetid()));// Field1.cSetName);

		strSQL.append("update " + targetTable + " set " + targetField + "=");
		
		String flag = "";
		
		switch (nSQLtype) {
			case S_FIRST: {
				flag = "min";
				break;
			}
			case S_LAST: {
				flag = "max";
				break;
			}
			case S_MAX: {
				flag = " MAX(" + strCurMenu + ')';
				break;
			}
			case S_MIN: {
				flag = " MIN(" + strCurMenu + ')';
				break;
			}
			case S_SUM: {
				flag = " SUM(" + strCurMenu + ')';
				break;
			}
			case S_AVG: {
				flag = " AVG(" + strCurMenu + ')';
				break;
			}
			case S_COUNT: {
				flag = " count(*)";
				break;
			}
		}

		switch (nSQLtype) {
			case S_FIRST:
			case S_LAST: {
				strSQL.append("(select " + strCurMenu + " from " + StdTmpTable + " a " + " where " + " seqnum = (select "
						+ flag + "(seqnum) from " + StdTmpTable + " b where a.objectid=b.objectid and " + strWhere + ")" + " and a.objectid="
						+ targetTable + ".objectid and " + strWhere + ")");
				break;
			}
			case S_MAX:
			case S_MIN:
			case S_SUM:
			case S_AVG:
			case S_COUNT: {
				strSQL.append("(select " + flag + " from " + StdTmpTable + " b " + "where " + targetTable
						+ ".objectid=b.objectid" + " and " + strWhere + ")");
				break;
			}
		}
		
		return strSQL.toString();
	}
	
	
	

	private boolean Func_CGET(RetValue retValue) throws GeneralException,
			SQLException {
		String strSQL = "", strInteger = "";
		int nDirection;
		RetValue retValue1 = new RetValue();
		strSQL = SQL.toString();
		CurFuncNum = FUNCGET;
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}

		if (!Get_Token())
			return false; // 取一个指标
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if (!level0(retValue))
			return false;
		if (IsMainSet(Field.getFieldsetid())) {
			Putback();
			SError(E_MUSTBESUBSET);
			return false;
		}
		nDirection = tok;
		if (!((tok == S_INCREASE) || (tok == S_DECREASE))) {
			Putback();
			SError(E_MUSTBEGETSYMBOL);
			return false;
		}

		SQL.setLength(0);
		if (!Get_Token())
			return false; // 取一个指标
		if (!level0(retValue1))
			return false;
		if (!retValue1.isIntType()) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		strInteger = SQL.toString(); // chenmengqing add "()" for +2
		if (tok != S_GETEND) {
			Putback();
			SError(E_LOSSGETEND);
			return false;
		}

		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_GET(Field, strInteger, nDirection));

		CurFuncNum = 0;
		return Get_Token();// then exit; //if FFlag=forOddVar,必须结束
	}

	private boolean Func_GET(RetValue retValue) throws GeneralException,
			SQLException {
		String strSQL = "", strInteger = "";
		int nDirection;
		RetValue retValue1 = new RetValue();
		if ("取".equals(token)) {
			Func_CGET(retValue);
			return true;// 如果是中文跳出
		}
		CurFuncNum = FUNCGET;
		strSQL = SQL.toString();
		if (ModeFlag == forNormal) {
			Putback();
			SError(E_GETSELECT);
			return false;
		}

		if (!Get_Token())
			return false; // 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		if (!Get_Token())
			return false; // 取一个指标
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}

		if (!level0(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (IsMainSet(Field.getFieldsetid())) {
			Putback();
			SError(E_MUSTBESUBSET);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 取一个整数
		if (!level0(retValue1))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		if (!retValue1.isIntType()) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		strInteger = SQL.toString();

		SQL.setLength(0);
		// GET类型分INCREASE、DECREASE
		if (!Get_Token())
			return false;
		nDirection = tok;
		if (!((tok == S_INCREASE) || (tok == S_DECREASE))) {
			Putback();
			SError(E_MUSTBEGETSYMBOL);
			return false;
		}
		if (!Get_Token())
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_GET(Field, strInteger, nDirection));
		if (!Get_Token())
			return false;
		// if FFlag=forOddVar,必须结束
		CurFuncNum = 0;
		return true;
	}

	private boolean Func_Maxmin(int FuncNum, RetValue retValue)
			throws GeneralException, SQLException {
		String str, str1, str2;
		RetValue retValue1 = new RetValue();
		str = SQL.toString(); // (
		if (!Get_Token())
			return false;
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}

		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		str1 = SQL.toString();

		SQL.setLength(0); // expr2 & )
		if (!Get_Token())
			return false;
		if (!level0(retValue1))
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		str2 = SQL.toString();

		if (!(retValue.IsSameType(retValue1) || retValue.IsNullType() || retValue
				.IsNullType())) {
			Putback();
			SError(E_NOTSAMETYPE);
			return false;
		}
		SQL.setLength(0);
		switch (FuncNum) {
		case FUNCMAX: {
			SQL.append(str);
			SQL.append(" CASE WHEN (");
			SQL.append(str1);
			SQL.append(")>(");
			SQL.append(str2);
			SQL.append(") THEN ");
			SQL.append(str1);
			SQL.append(" ELSE ");
			SQL.append(str2);
			SQL.append(" END");
			break;
		}
		case FUNCMIN: {
			SQL.append(str);
			SQL.append(" CASE WHEN (");
			SQL.append(str1);
			SQL.append(")<(");
			SQL.append(str2);
			SQL.append(") THEN ");
			SQL.append(str1);
			SQL.append(" ELSE ");
			SQL.append(str2);
			SQL.append(" END");
			break;
		}
		}
		switch (FuncNum) {
		case FUNCMAX: {
			int _type=retValue.getValueType();
			if (retValue.Smaller(retValue1)) {
				retValue.setValueType(_type);
				retValue.setValue(retValue1.getValue());
			}
			break;
		}

		case FUNCMIN: {
			int _type=retValue.getValueType();
			if (retValue.Greater(retValue1)) {
				retValue.setValueType(_type);
				retValue.setValue(retValue1.getValue());
			}
			break;
		}
		}
		return Get_Token();
	}

	/**
	 * 代码转名称
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 */
	private boolean Func_CTON(RetValue retValue) throws GeneralException {
		String strSQL;
		boolean b;
		b = (!"~".equals(token));
		CurFuncNum = FUNCCTON;
		strSQL = SQL.toString();
		SQL.setLength(0);
		if (b) {
			if (!Get_Token())
				return false;
			// 处理左括号
			if (tok != S_LPARENTHESIS) {
				Putback();
				SError(E_LOSSLPARENTHESE);
				return false;
			}
		}
		if (!Get_Token())
			return false;
		// 取一个指标
		if (token_type != FIELDITEM) {
			Putback();
			SError(E_MUSTBEFIELDITEM);
			return false;
		}
		if (b) {
			if (!Get_Token())
				return false; // 取)
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		if (("0".equals(Field.getCodesetid()) || (""
				.equals(Field.getCodesetid())))) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		if (!Get_Token())
			return false;
		retValue.setValue("");
		retValue.setValueType(STRVALUE);
		SQL.setLength(0);
		SQL.append(strSQL).append(SQL_CTON(Field));
		return true;
	}

	/**
	 * 代码转名称2(表达式,代码类)
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 */
	private boolean Func_CTON2(RetValue retValue) throws GeneralException,
			SQLException {
		String strSQL = "";
		String strExpr = "";
		String strCode = "";
		int layNum=1;        //显示层级
		String splitSign=""; //分隔符

	/*	if (ModeFlag == forSearch) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}*/

		CurFuncNum = FUNCCTON2;
		strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 表达式 */
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		strExpr = SQL.toString();
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 取代码类 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		strCode = SQL.toString().trim();
		strCode = strCode.replaceAll("'", "");
		/** 取右括号 */
		if (tok != S_RPARENTHESIS) {
			
			if (tok== S_COMMA) {
				/** 取层级 */
				SQL.setLength(0);
				if (!Get_Token())
					return false;
				if (!level6(retValue))
					return false;
				String layNum_str = SQL.toString().trim();
				Pattern pattern = Pattern.compile("[0-9]+");
				if(pattern.matcher(layNum_str).matches())
				{
					layNum=Integer.parseInt(layNum_str);
					if(layNum<=10)
					{
						if (tok== S_COMMA) {
							SQL.setLength(0);
							if (!Get_Token())
								return false;
							if (!level6(retValue))
								return false;
							splitSign = SQL.toString().trim();
							splitSign = splitSign.replaceAll("'", "");
							if (tok != S_RPARENTHESIS) {
								Putback();
								SError(E_LOSSRPARENTHESE);
								return false;
							}
							
						}
						else if (tok != S_RPARENTHESIS)
						{
							Putback();
							SError(E_LOSSRPARENTHESE);
							return false;
						}
					}
					else
					{
						Putback();
						SError(E_INTSCOPE2);
						return false;
					}
				}
				else
				{
					Putback();
					SError(E_MUSTBEINTEGER);
					return false;
				} 
			}
			else
			{
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
		}
		if ("".equalsIgnoreCase(strCode) || "0".equalsIgnoreCase(strCode)) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		if (!Get_Token())
			return false;
		retValue.setValue("");
		retValue.setValueType(STRVALUE);
		SQL.setLength(0);
		SQL.append(strSQL);
		SQL.append(SQL_CTON2(strExpr, strCode,layNum,splitSign));
		return true;
	}

	/**
	 * 取余数
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 */
	private boolean Func_MOD(RetValue retValue) throws GeneralException,
			SQLException {
		String strSQL;
		String FSQL = "";
		String sL, sR;
		CurFuncNum = FUNCMOD;
		strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		// 处理第一个参数
		if (!Get_Token())
			return false;
		// 可能参数为负数，直接跳到level6缺少了level5的S_MINUS判断
		if (!level5(retValue))
			return false;
		sL = SQL.toString();
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		// 处理第二个参数
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level5(retValue))
			return false;
		sR = SQL.toString();
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		switch (Sql_switcher.searchDbServer()) {
		case Constant.MSSQL: {
			FSQL = sL + " % NullIF(" + sR + ",0)"; // 考虑被除数为0的情况,chenmengqing
													// added 20080401
			break;
		}
		case Constant.ORACEL: {
			FSQL = "MOD(Round(" + sL + ",0),NullIF(Round(" + sR + ",0),0))";
			break;
		}
		case Constant.DB2: {
			FSQL = "MOD(Round(" + sL + ",0),NullIF(Round(" + sR + ",0),0))";
			break;
		}
		}
		SQL.setLength(0);
		SQL.append(strSQL);
		SQL.append(" " + FSQL);
		return true;
	}

	/**
	 * 分析某指标是否为空值 IS NULL
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_ISNNULL(RetValue retValue) throws GeneralException,
			SQLException {
		CurFuncNum = FUNCISNULL;
		String tempSql = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;

		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		SQL.setLength(0);
		SQL.append(tempSql.toString());
	
		if("A".equalsIgnoreCase(item.getItemtype()))
			SQL.append(" NullIF("); 
		if (CurFuncNum == FUNCSELECT||isFuncSelectCondition) { 
			if (item.getFieldsetid() == null|| "".equals(item.getFieldsetid()))
				SQL.append(item.getItemid());
			else {
				// 如果是临时变量 取得是单位或职位的数据 不应该加库前缀 2008-12-24 dengcan
				if (item.getFieldsetid().toUpperCase().charAt(0) == 'B'
						|| item.getFieldsetid().toUpperCase().charAt(0) == 'K')
					SQL.append( item.getFieldsetid() + "." + item.getItemid());
				else
					SQL.append(DbPre + Field.getFieldsetid() + "."+ Field.getItemid());
			} 
		}
		else		
			SQL.append(item.getItemid());
		if("A".equalsIgnoreCase(item.getItemtype()))
			SQL.append(",'')");
		
		SQL.append(" IS NULL ");
		retValue.setValueType(LOGIC);
		retValue.setValue(new Boolean(true));
		CurFuncNum = 0;
		return true;
	}

	/**
	 * 幂函数
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_POWER(RetValue retValue) throws GeneralException,
			SQLException {
		String sD = null;
		String sM = null;
		CurFuncNum = FUNCPOWER;
		String strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 底数 */
		if (!Get_Token())
			return false;
		if (!level3(retValue))
			return false;
		sD = SQL.toString();
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 幂数 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level3(retValue))
			return false;
		sM = SQL.toString();
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;

		SQL.setLength(0);
		SQL.append(strSQL);
		SQL.append("POWER(");
		SQL.append(sD);
		SQL.append(",");
		SQL.append(sM);
		SQL.append(")");

		CurFuncNum = 0;
		return true;
	}

	
	
	
	/**
	 * 数值转代码
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_DTOCODE(RetValue retValue) throws GeneralException,SQLException {
  
		String strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;
		int valueType = retValue.getValueType();
		if(valueType != INT && valueType != FLOAT) {
			Putback();
			SError(E_MUSTBENUMBER);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String itemid = SQL.toString(); 
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (this.token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		if (!level6(retValue))
			return false;
		int _length = Integer.parseInt(SQL.toString().trim());
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		
		StringBuffer sSQL = new StringBuffer();
		
		if(_length==0)
			sSQL.append("''");
		else if(_length==1)
			sSQL.append(itemid);
		else
		{
			if(DBType==Constant.ORACEL)
			{
				sSQL.append(" case when INSTR("+itemid+",'.')=0 then case");	
				for(int n=_length-1;n>0;n--)
				{
					StringBuffer s=new StringBuffer("");
					for(int j=0;j<n;j++)
						s.append("0");
					sSQL.append(" when LENGTH("+itemid+")="+(_length-n)+" then '"+s.toString()+"'||to_char("+itemid+") ");
				}
				sSQL.append(" else to_char("+itemid+") ");
				sSQL.append(" end ");  
				sSQL.append(" else case ");
				for(int n=_length-1;n>0;n--)
				{
					StringBuffer s=new StringBuffer("");
					for(int j=0;j<n;j++)
						s.append("0");
					sSQL.append(" when LENGTH(SUBSTR("+itemid+",0,INSTR("+itemid+",'.')-1))="+(_length-n)+" then '"+s.toString()+"'||SUBSTR("+itemid+",0,INSTR("+itemid+",'.')-1) ");
				}
				sSQL.append(" else SUBSTR("+itemid+",0,INSTR("+itemid+",'.')-1) ");
				sSQL.append(" end ");      
				sSQL.append(" end   ");
			}
			else
			{
				/*if(item != null && item.getDecimalwidth()==0)  //整形
				{
					sSQL.append(" case ");
					for(int n=_length-1;n>0;n--)
					{
						StringBuffer s=new StringBuffer("");
						for(int j=0;j<n;j++)
							s.append("0");
						sSQL.append(" when  datalength(convert(varchar(20),"+itemid+"))="+(_length-n)+" then '"+s.toString()+"'+convert(varchar(20),"+itemid+") ");
					}
					sSQL.append(" else  convert(varchar(20),"+itemid+") ");
					sSQL.append(" end  ");
				}
				else
				{*/
					
					sSQL.append(" case when CHARINDEX('.',"+itemid+")=0 then case");	
					
						for(int n=_length-1;n>0;n--)
						{
							StringBuffer s=new StringBuffer("");
							for(int j=0;j<n;j++)
								s.append("0");
							sSQL.append(" when  datalength(convert(varchar(20),"+itemid+"))="+(_length-n)+" then '"+s.toString()+"'+convert(varchar(20),"+itemid+")");
						}
						sSQL.append("  else   convert(varchar(20),"+itemid+") ");
						sSQL.append("  end  ");
					
					sSQL.append(" else case ");			
						for(int n=_length-1;n>0;n--)
						{
							StringBuffer s=new StringBuffer("");
							for(int j=0;j<n;j++)
								s.append("0");
							sSQL.append(" when  datalength(Substring(convert(varchar(20),"+itemid+"),0,CHARINDEX('.',"+itemid+")))="+(_length-n)+" then '"+s.toString()+"'+Substring(convert(varchar(20),"+itemid+"),0,CHARINDEX('.',"+itemid+")) ");
						}
						sSQL.append("  else  Substring(convert(varchar(20),"+itemid+"),0,CHARINDEX('.',"+itemid+")) ");
						sSQL.append("  end  ");
					sSQL.append("  end  ");
					
					
				//}
				
			}
			
		}
		SQL.setLength(0);
		SQL.append(strSQL);
		SQL.append(sSQL.toString());
		retValue.setValue("");
		retValue.setValueType(STRVALUE);
 
		return true;
	}
	
	
	
	
	
	
	
	
	/**
	 * 数字转汉字函数 NumConversion(指标名称,1|2|3) 含义：将指标值中的数字转换成汉字 参数=1：表示将数字替换成（○、一、二…九）
	 * 参数=2：表示将数字替换成（零、壹、贰…玖）
	 * 参数=3： 表示将数字转成大写金额（叁佰陆拾柒元柒角肆分）
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_CNTC(RetValue retValue) throws GeneralException,
			SQLException {
		String sNum1 = "○一二三四五六七八九";
		String sNum2 = "零壹贰叁肆伍陆柒捌玖";
		CurFuncNum = FUNCCNTC;
		String strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;
		if (item != null && ("M".equalsIgnoreCase(item.getItemtype())
				|| "".equalsIgnoreCase(item.getItemtype()))) {
			Putback();
			SError(E_NOTSAMETYPE);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		
		//zxj 20160810 将内部嵌套的公式解析成的sql暂存，转金额时需支持公式嵌套
	    String strSQLParam = SQL.toString();
	    
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (this.token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}

		if (!level6(retValue))
			return false;
		
		String sMode = SQL.toString().trim();
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		
		if (!Get_Token())
			return false;
		
		StringBuffer sSQL = new StringBuffer();
		if (!"3".equalsIgnoreCase(sMode)) {
    		if ("A".equalsIgnoreCase(item.getItemtype())) {
    			sSQL.append(item.getItemid());
    		} else {
    			switch (DBType) {
    			case Constant.DB2:
    				sSQL.append("Char(");
    				break;
    			case Constant.ORACEL:
    				sSQL.append("To_Char(");
    				break;
    			default:
    				sSQL.append("Convert(Varchar,");
    				break;
    			}
    			sSQL.append(strSQLParam);
    			sSQL.append(")");
    		}
		
    		StringBuffer cnNum = new StringBuffer();
    		if ("1".equalsIgnoreCase(sMode))
    			cnNum.append(sNum1);
    		else
    			cnNum.append(sNum2);
    		String tmp = sSQL.toString();
    		sSQL.setLength(0);
    		for (int i = 0; i < 10; i++) {
    			sSQL.setLength(0);
    			sSQL.append("replace(");
    			sSQL.append(tmp);
    			sSQL.append(",'");
    			sSQL.append(i);
    			sSQL.append("','");
    			sSQL.append(cnNum.charAt(i));
    			sSQL.append("')");
    			tmp = sSQL.toString();
    		}
		} else { //转金额
		    if (item != null) {
		        //函数包裹的不能是字符型指标（字符型指标需要嵌套其它公式转成数值才可使用）
		        if (strSQLParam.trim().equalsIgnoreCase("ISNULL(" + item.getItemid() + ",'')")
		                || strSQLParam.trim().equalsIgnoreCase("NVL(" + item.getItemid() + ",'')")) {
		            Putback();
		            SError(E_NOTSAMETYPE);
		            return false;
                }
		        
		        //函数包裹的不能是日期型指标（日期型指标需要嵌套其它公式转成数值才可使用）
		        if (strSQLParam.trim().equalsIgnoreCase(item.getItemid()) && "D".equalsIgnoreCase(item.getItemtype())) {
		            Putback();
                    SError(E_NOTSAMETYPE);
                    return false;
		        }
		    }
		    createCapitalRMBFunc();
		    switch (DBType) {
            case Constant.DB2:
                sSQL.append("CapitalRMB_V170629(");
                break;
            case Constant.ORACEL:
                sSQL.append("CapitalRMB_V170629(");
                break;
            default:
                sSQL.append("dbo.CapitalRMB_V170629(");
                break;
            }
            sSQL.append(strSQLParam);
            sSQL.append(")");
		}
		SQL.setLength(0);
		SQL.append(strSQL);
		SQL.append(sSQL.toString());
		retValue.setValue("");
		retValue.setValueType(STRVALUE);
		CurFuncNum = 0;
		return true;
	}
	
	/**
	 * 生成数据库数字转金额函数
	 * @Title: createCapitalRMBFunc   
	 * @Description: 生成数据库数字转金额函数
	 */
	private void createCapitalRMBFunc() {
	    //库里已经有数字转金额函数，不需要再创建了
	    if(isExistDBObject("CapitalRMB_V170629"))
	        return;
	    
	    String sqlFunc = "";
	    if (DBType == Constant.MSSQL) {
	        sqlFunc = this.getCapitalRMBFuncMSSQL();
	    } else if (DBType == Constant.ORACEL) {
	        sqlFunc = this.getCapitalRMBFuncOracle();
	    }
	    ContentDAO dao = new ContentDAO(this.con);
        try {
            dao.update(sqlFunc);
        } catch(Exception e) {
            //e.printStackTrace();
        }
    }
	
	/**
	 * 判断数据库中是否存在某个对象
	 * @Title: isExistDBObject   
	 * @Description: 判断数据库中是否存在某个对象   
	 * @param objName 对象名：如存储过程名，触发器名，函数名等
	 * @return
	 */
    private boolean isExistDBObject(String objName) {
        boolean isExists = false;

        StringBuffer sql = new StringBuffer();
        if (DBType == Constant.ORACEL)
            sql.append("select * from user_objects where object_name = '" + objName.toUpperCase() + "'");
        else if (DBType == Constant.MSSQL)
            sql.append("select * from dbo.sysobjects where id = object_id(N'[dbo].[" + objName + "]')");

        ContentDAO dao = new ContentDAO(this.con);
        ResultSet rs = null;
        try {
            rs = dao.search(sql.toString());
            isExists = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeDbObj(rs);
        }

        return isExists;
    }
    
    /**
     * MSSQL版数字转金额函数
     * @Title: getCapitalRMBFuncMSSQL   
     * @Description: MSSQL版数字转金额函数   
     * @return
     */
	private String getCapitalRMBFuncMSSQL() {
	    StringBuffer sqlFunc = new StringBuffer();
	    sqlFunc.append("Create Function CapitalRMB_V170629(@LowerMoney Decimal(38,4))\n");
        sqlFunc.append("Returns Varchar(200) --返回的大写金额的字符\n");
        sqlFunc.append("As \n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("  Declare @LowerStr Varchar(50) --小写金额\n");
        sqlFunc.append("  Declare @UpperStr Varchar(200) --大写金额\n");
        sqlFunc.append("  Declare @UpperTmp Varchar(15) --大写金额的临时字符串\n");
        sqlFunc.append("  Declare @i Int --递增量 \n");
        sqlFunc.append("  Declare @LowerLen Int --小写金额的总长度\n");
        sqlFunc.append("  Declare @negative int --负数\n");
        sqlFunc.append("  If(@LowerMoney<0)\n");
        sqlFunc.append("    Set @negative = 1;\n");
        sqlFunc.append("  else\n");
        sqlFunc.append("    set @negative = 0;\n");
        sqlFunc.append("  Set @LowerStr = @LowerMoney --把Decimal型的值全部赋给字符串变量 注:(赋值过去的话如8 在字符串变量中是显示8.0000 因为小数位精确到四位,没有的话，它会自动补0)\n");
        sqlFunc.append("  Set @LowerStr = Replace(@LowerStr,'.','') --把小数点替换成空字符 --精确到小数点的四位 角分厘毫\n");
        sqlFunc.append("  Set @LowerLen = Len(@LowerStr) --获取小写金额的总长度(包括四个小数位) \n");
        sqlFunc.append("  Select @i = 1,@UpperStr = '',@UpperTmp = '' --设置默认初始值\n");
        sqlFunc.append("  While @i <= @LowerLen \n");
        sqlFunc.append("  Begin\n");
        sqlFunc.append("    Set @UpperTmp = Case\n");
        sqlFunc.append("    When SubString(@LowerStr,@LowerLen - @i + 1,1) = '0' And @i = 5 And (Convert(Int,Right(@LowerStr,4)) = 0 Or @LowerLen > 5) Then\n");
        sqlFunc.append("      '元' --注：如果个位为0的话,并且四位小数都是0或者它的长度超过5(也就是超过元)，则为元 \n");
        sqlFunc.append("    Else \n");
        sqlFunc.append("        + Case SubString(@LowerStr,@LowerLen - @i + 1,1) --看当前位是数字几,就直接替换成汉字繁体大写 \n");
        sqlFunc.append("            When '0' Then '零'\n");
        sqlFunc.append("            When '1' Then '壹' \n");
        sqlFunc.append("            When '2' Then '贰'\n");
        sqlFunc.append("            When '3' Then '叁'\n");
        sqlFunc.append("            When '4' Then '肆'\n");
        sqlFunc.append("            When '5' Then '伍'\n");
        sqlFunc.append("            When '6' Then '陆'\n");
        sqlFunc.append("            When '7' Then '柒'\n");
        sqlFunc.append("            When '8' Then '捌'\n");
        sqlFunc.append("            When '9' Then '玖'\n");
        sqlFunc.append("          End \n");
        sqlFunc.append("        + Case @i \n");
        sqlFunc.append("            When 1 Then '毫'\n");
        sqlFunc.append("            When 2 Then '厘'\n");
        sqlFunc.append("            When 3 Then '分'\n");
        sqlFunc.append("            When 4 Then '角'\n");
        sqlFunc.append("            When 5 Then '元'\n");
        sqlFunc.append("            When 9 Then '万'\n");
        sqlFunc.append("            When 13 Then '亿'\n");
        sqlFunc.append("            When 17 Then '兆'\n");
        sqlFunc.append("            When 21 Then '京'\n");
        sqlFunc.append("            When 25 Then '垓'\n");
        sqlFunc.append("            When 29 Then '杼'\n");
        sqlFunc.append("            When 33 Then '穰'\n");
        sqlFunc.append("            When 37 Then '沟' --Decimal型最大长度是38 后面的就不用再考虑了\n");
        sqlFunc.append("          Else+ Case @i%4\n");
        sqlFunc.append("            When 2 Then '拾' --拾 6 10 14 18 22 26 30 34 38 …………\n");
        sqlFunc.append("            When 3 Then '佰' --佰 7 11 15 19 23 27 31 35 39 …………\n");
        sqlFunc.append("            When 0 Then '仟' --仟 8 12 16 20 24 28 32 36 40 …………\n");
        sqlFunc.append("          End \n");
        sqlFunc.append("        End \n");
        sqlFunc.append("    End \n");
        sqlFunc.append("    Set @UpperStr = Isnull(@UpperTmp,'') + Isnull(@UpperStr,'')\n");
        sqlFunc.append("    Set @i = @i + 1 \n");
        sqlFunc.append("End \n");
        sqlFunc.append("If Convert(Int,Right(@LowerStr,4)) = 0 Set @UpperStr = Left(@UpperStr,Len(@UpperStr)-8) + '整' --判断小数位数是不是都是0,是0就可以取整\n");
        sqlFunc.append("While Patindex('%零[仟佰拾角分厘毫零]%',@UpperStr) <> 0 --把零拾或零佰或零零变成一个零 \n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("Set @UpperStr = stuff(@UpperStr,patindex('%零[仟佰拾角分厘毫零]%',@UpperStr),2,'零')\n");
        sqlFunc.append("End \n");
        sqlFunc.append("While Patindex('%[沟穰杼垓京兆亿万]零[沟穰杼垓京兆亿万]%',@UpperStr) <> 0 --把零万或零亿的清空掉\n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("Select @UpperStr = Stuff(@UpperStr,Patindex('%[沟穰杼垓京兆亿万]零[沟穰杼垓京兆亿万]%',@UpperStr)+1,2,'')\n");
        sqlFunc.append("End \n");
        sqlFunc.append("While Patindex('%[仟佰拾]零[沟穰杼垓京兆亿万]%',@UpperStr) <> 0 --把类似拾零万或佰零万或仟零万中间的零清空掉\n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("Select @UpperStr = Stuff(@UpperStr,Patindex('%[仟佰拾]零[沟穰杼垓京兆亿万]%',@UpperStr)+1,1,'')\n");
        sqlFunc.append("End \n");
        sqlFunc.append("If Patindex('%_零[元]%',@UpperStr) <> 0 --把类似拾零元或百零元中间的零清空掉\n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("Select @UpperStr = Stuff(@UpperStr,Patindex('%_零[元]%',@UpperStr) + 1,1,'')\n");
        sqlFunc.append("End \n");
        sqlFunc.append("Else If (Patindex('零[元]%',@UpperStr) <> 0) And (Convert(Int,Right(@LowerStr,4)) <> 0)--判断当前否是零元开头，并且后面的四个小数不为0\n");
        sqlFunc.append("Begin \n");
        sqlFunc.append("Select @UpperStr = Stuff(@UpperStr,Patindex('零[元]%',@UpperStr),2,'') --把零元清空掉\n");
        sqlFunc.append("End \n");
        sqlFunc.append("If Right(@UpperStr,1) = '零' Set @UpperStr = Left(@UpperStr,Len(@UpperStr)-1) --如果最后一位是零也清空掉\n");
        sqlFunc.append("If @UpperStr = '元整' Set @UpperStr = '零' + @UpperStr --如果只是0的话，就显示零元整 \n");
        sqlFunc.append("  IF @negative = 1 SET @UpperStr = '负'+@UpperStr");
        sqlFunc.append("\n");
        sqlFunc.append("Return @UpperStr --返回大写金额 \n"); 
        sqlFunc.append("End\n");
        return sqlFunc.toString();
	}
	
	/**
	 * ORACLE版数字转金额函数
	 * @Title: getCapitalRMBFuncOracle   
	 * @Description: ORACLE版数字转金额函数   
	 * @return
	 */
	private String getCapitalRMBFuncOracle() {
	    StringBuffer sqlFunc = new StringBuffer();
	    sqlFunc.append("create or replace\n");
	    sqlFunc.append("function CapitalRMB_V170629\n");
	    sqlFunc.append("(LowerMoney Decimal)\n");
	    sqlFunc.append("return varchar2 is Result varchar2(200);\n");
	    sqlFunc.append("  LowerStr Varchar2(50); --小写金额\n");
	    sqlFunc.append("  UpperStr Varchar2(200); --大写金额\n");
	    sqlFunc.append("  UpperTmp Varchar2(15); --大写金额的临时字符串\n");
	    sqlFunc.append("  i Int; --递增量\n");
	    sqlFunc.append("  LowerLen Int; --小写金额的总长度\n");
	    sqlFunc.append("  curStr Varchar2(1);\n");
	    sqlFunc.append("  negative int; --负数\n");
	    sqlFunc.append("begin\n");
	    sqlFunc.append("  negative := 0;\n");
	    sqlFunc.append("  if (LowerMoney is not null and LowerMoney < 0) then\n");
	    sqlFunc.append("    negative := 1;\n");
	    sqlFunc.append("  end if;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  LowerStr := ltrim(to_char(LowerMoney, '9999999999999.9999')); --把Decimal型的值全部赋给字符串变量 注:(赋值过去的话如8 在字符串变量中是显示8.0000 因为小数位精确到四位,没有的话，它会自动补0)\n");
	    sqlFunc.append("  LowerStr := Replace(LowerStr,'.',''); --把小数点替换成空字符 --精确到小数点的四位 角分厘毫\n");
	    sqlFunc.append("  LowerLen := Length(LowerStr); --获取小写金额的总长度(包括四个小数位)\n");
	    sqlFunc.append("  i := 1; UpperStr := ''; UpperTmp := ''; --设置默认初始值\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  While i <= LowerLen LOOP\n");
	    sqlFunc.append("  Begin\n");
	    sqlFunc.append("    curStr := substr(LowerStr,LowerLen - i + 1,1);\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("    UpperTmp := Case\n");
	    sqlFunc.append("        When curStr = '0' And i = 5 And (cast(substr(LowerStr,length(LowerStr)-3) as integer) = 0 Or LowerLen > 5) Then '元' --注：如果个位为0的话,并且四位小数都是0或者它的长度超过5(也就是超过元)，则为元\n");
	    sqlFunc.append("        ELSE\n");
	    sqlFunc.append("          CASE --看当前位是数字几,就直接替换成汉字繁体大写\n");
	    sqlFunc.append("            When curStr='0' Then '零'\n");
	    sqlFunc.append("            When curStr='1' Then '壹'\n");
	    sqlFunc.append("            When curStr='2' Then '贰'\n");
	    sqlFunc.append("            When curStr='3' Then '叁'\n");
	    sqlFunc.append("            When curStr='4' Then '肆'\n");
	    sqlFunc.append("            When curStr='5' Then '伍'\n");
	    sqlFunc.append("            When curStr='6' Then '陆'\n");
	    sqlFunc.append("            When curStr='7' Then '柒'\n");
	    sqlFunc.append("            When curStr='8' Then '捌'\n");
	    sqlFunc.append("            When curStr='9' Then '玖'\n");
	    sqlFunc.append("          End\n");
	    sqlFunc.append("       || Case\n");
	    sqlFunc.append("          When i=1 Then '毫'\n");
	    sqlFunc.append("          When i=2 Then '厘'\n");
	    sqlFunc.append("          When i=3 Then '分'\n");
	    sqlFunc.append("          When i=4 Then '角'\n");
	    sqlFunc.append("          When i=5 Then '元'\n");
	    sqlFunc.append("          When i=9 Then '万'\n");
	    sqlFunc.append("          When i=13 Then '亿'\n");
	    sqlFunc.append("          When i=17 Then '兆'\n");
	    sqlFunc.append("          When i=21 Then '京'\n");
	    sqlFunc.append("          When i=25 Then '垓'\n");
	    sqlFunc.append("          When i=29 Then '杼'\n");
	    sqlFunc.append("          When i=33 Then '穰'\n");
	    sqlFunc.append("          When i=37 Then '沟' --Decimal型最大长度是38 后面的就不用再考虑了\n");
	    sqlFunc.append("        Else\n");
	    sqlFunc.append("          Case\n");
	    sqlFunc.append("            When mod(i,4)=2 Then '拾' --拾 6 10 14 18 22 26 30 34 38 …………\n");
	    sqlFunc.append("            When mod(i,4)=3 Then '佰' --佰 7 11 15 19 23 27 31 35 39 …………\n");
	    sqlFunc.append("            When mod(i,4)=0 Then '仟' --仟 8 12 16 20 24 28 32 36 40 …………\n");
	    sqlFunc.append("          End\n");
	    sqlFunc.append("      End\n");
	    sqlFunc.append("    End;\n");
	    sqlFunc.append("    UpperStr := NVL(UpperTmp,'') || NVL(UpperStr,'');\n");
	    sqlFunc.append("    i := i + 1;\n");
	    sqlFunc.append("  end;\n");
	    sqlFunc.append("  End LOOP;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  --判断小数位数是不是都是0,是0就可以取整\n");
	    sqlFunc.append("  If (cast(substr(LowerStr,length(LowerStr)-3) as integer) = 0) THEN\n");
	    sqlFunc.append("    UpperStr := substr(UpperStr,1,length(UpperStr)-8) || '整';\n");
	    sqlFunc.append("  end if;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  --把零拾或零佰或零零变成一个零\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零毫','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零厘','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零分','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零角','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零拾','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零佰','零');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零仟','零');\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  While (INSTR(UpperStr,'零零') <> 0) LOOP\n");
	    sqlFunc.append("  Begin\n");
	    sqlFunc.append("    UpperStr := Replace(UpperStr,'零零','零');\n");
	    sqlFunc.append("  End;\n");
	    sqlFunc.append("  END LOOP;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  --把类似拾零万或佰零万或仟零万中间的零清空掉\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'拾零万','拾万');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'佰零万','佰万');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'仟零万','仟万');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'拾零亿','拾亿');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'佰零亿','佰亿');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'仟零亿','仟亿');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'拾零兆','拾兆');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'佰零兆','佰兆');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'仟零兆','仟兆');\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("   --把类似拾零元或百零元中间的零清空掉\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'拾零元','拾元');\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'拾零元','拾元');\n");
	    sqlFunc.append("  --把零万或零亿的清空掉\n");
        sqlFunc.append("  UpperStr := Replace(UpperStr,'零万','');\n");
        sqlFunc.append("  UpperStr := Replace(UpperStr,'零亿','');\n");
        sqlFunc.append("\n");
	    sqlFunc.append("  If (INSTR(UpperStr, '零元') = 1) And (cast(substr(LowerStr,length(LowerStr)-3) as integer) <> 0) then --判断当前否是零元开头，并且后面的四个小数不为0\n");
	    sqlFunc.append("  Begin\n");
	    sqlFunc.append("    UpperStr := Replace(UpperStr,'零元','');\n");
	    sqlFunc.append("  END;\n");
	    sqlFunc.append("  END IF;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  UpperStr := Replace(UpperStr,'零元','元');\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  --如果最后一位是零也清空掉\n");
	    sqlFunc.append("  If substr(UpperStr,-1,1) = '零' THEN\n");
	    sqlFunc.append("    UpperStr := substr(UpperStr, 1, length(UpperStr)-1);\n");
	    sqlFunc.append("  END IF;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  --如果只是0的话，就显示零元整\n");
	    sqlFunc.append("  If UpperStr = '元整' or UpperStr='整' THEN\n");
	    sqlFunc.append("    UpperStr := '零元整';\n");
	    sqlFunc.append("  END IF;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  IF negative = 1 THEN UpperStr := '负'||UpperStr; END IF;\n");
	    sqlFunc.append("\n");
	    sqlFunc.append("  Result := UpperStr; --返回大写金额\n");
	    sqlFunc.append("  return(Result);\n");
	    sqlFunc.append("end;\n");
	    return sqlFunc.toString();
    }

	/**
	 * 执行标准函数
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_Standard(RetValue retValue) throws GeneralException,
			SQLException {
		String strOldSQL = SQL.toString();

		CurFuncNum = FUNCSTANDARD;
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		/** 标准号 */
		if (!level6(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String stdid = SQL.toString();
		/** 横向指标一 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem hitem = this.Field;
		if(hitem!=null)
			gz_stdFieldMap.put(hitem.getItemid().toLowerCase(),"1");
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 横向指标二 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem shitem = this.Field;
		if(shitem!=null)
			gz_stdFieldMap.put(shitem.getItemid().toLowerCase(),"1");
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 纵向指标一 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem vitem = this.Field;
		if(vitem!=null)
			gz_stdFieldMap.put(vitem.getItemid().toLowerCase(),"1");
		
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 纵向指标二 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem svitem = this.Field;
		if(svitem!=null)
			gz_stdFieldMap.put(svitem.getItemid().toLowerCase(),"1");
		
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		/** 对标准表进行检查 */
		SalaryStandardBo stdbo =null;
		if(this.flg!=2&&this.varList.size()>0&&this.isSupportVar) //如果不是从报表里计算临时变量
		{
			stdbo= new SalaryStandardBo(this.con, stdid, "",this.varList);
		}
		else
			stdbo= new SalaryStandardBo(this.con, stdid, "");
		
		
		if (!checkStdId(stdbo))
			return false;
		if (!checkStdMenu(stdbo, hitem, 1))
			return false;
		if (!checkStdMenu(stdbo, shitem, 2))
			return false;
		if (!checkStdMenu(stdbo, vitem, 3))
			return false;
		if (!checkStdMenu(stdbo, svitem, 4))
			return false;
		/** 检查结束 */
		FieldItem rmenu = stdbo.getR_item();
		if ("A".equalsIgnoreCase(rmenu.getItemtype())) {
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		} else if ("N".equalsIgnoreCase(rmenu.getItemtype())) {
			retValue.setValueType(FLOAT);
			// retValue.setValueType(NULLVALUE);
			if (rmenu.getDecimalwidth() == 0)
				retValue.setValue(new Integer(0));
			else
				retValue.setValue(new Float(10.1));
		} else {
			retValue.setValueType(NULLVALUE);
		}

		/** 具体调用 */
		boolean bflag = "".equalsIgnoreCase(strOldSQL);// ?
		String strR = SQL_Standard(stdbo, hitem, shitem, vitem, svitem, bflag);
		SQL.setLength(0);
		SQL.append(strOldSQL);// ?
		SQL.append(strR);
		CurFuncNum = 0;
		return true;
	}

	/**
	 * 具体处理标准的函数
	 * 
	 * @param stdbo
	 * @param hitem
	 * @param shitem
	 * @param vitem
	 * @param svitem
	 * @param bflag
	 * @return
	 * @throws GeneralException
	 */
	private String SQL_Standard(SalaryStandardBo stdbo, FieldItem hitem,
			FieldItem shitem, FieldItem vitem, FieldItem svitem, boolean bflag)
			throws GeneralException {
		String fieldname = "";
		try {
			/** 计算过滤条件 */
			String strWhere = "";
			/** 语法校验时，不执行具体的SQL */
			boolean bcheck = false;
			if ((StdTmpTable == null || StdTmpTable.length() == 0))
				bcheck = true;
			DbWizard dbw = new DbWizard(this.con);
			/** 结果指标 */
			FieldItem rmenu = (FieldItem) stdbo.getR_item().cloneItem();
			rmenu.setFieldsetid(TempTableName);
			rmenu.setItemid("std_" + StdFieldList.size());
			rmenu.setVarible(1);
			mapUsedFieldItems.put(rmenu.getItemid(), rmenu);
			StdFieldList.add(rmenu);
			fieldname = rmenu.getItemid();
	//		if (!bcheck)   //5月13日用于数据联动支持工资标准功能
			{
				/** 创建计算字段 */
				StringBuffer strR = new StringBuffer();
				StringBuffer strSQL = new StringBuffer();
				strR.append(fieldname);
				strR.append(" ");
				strR.append(Sql_switcher.getFieldType(rmenu.getItemtype()
						.charAt(0), rmenu.getItemlength(), rmenu
						.getDecimalwidth()));

				switch (DBType) {
				case Constant.DB2:
					/** 暂时不支持 */
					break;
				case Constant.ORACEL:
					strSQL.append(" alter table ");
					strSQL.append(TempTableName);
					strSQL.append(" drop column ");
					strSQL.append(fieldname);
					// run_SQL(strSQL.toString());
					FSTDSQLS.add(strSQL.toString());
					strSQL.setLength(0);
					strSQL.append("alter table ");
					strSQL.append(TempTableName);
					strSQL.append(" ADD ");
					strSQL.append(strR.toString());
					FSTDSQLS.add(strSQL.toString());

					// run_SQL(strSQL.toString());
					break;
				default:
					strSQL.append(" alter table ");
					strSQL.append(TempTableName);
					strSQL.append(" drop column ");
					strSQL.append(fieldname);
					// run_SQL(strSQL.toString());
					FSTDSQLS.add(strSQL.toString());

					strSQL.setLength(0);
					strSQL.append("alter table ");
					strSQL.append(TempTableName);
					strSQL.append(" ADD ");
					strSQL.append(strR.toString());
					FSTDSQLS.add(strSQL.toString());
					// run_SQL(strSQL.toString());
					break;
				}

				/** 重新计算相关日期型或数值型区间范围的值 */
				StringBuffer buf = new StringBuffer();
				if (!stdbo.checkHVField(buf))
					throw new GeneralException(buf.toString());
				/** 把标准横纵坐标为日期型或数值型指标，加至薪资表中 */
				stdbo.setNbase(this.DbPre);
				ArrayList list = stdbo.addStdItemIntoTable(TempTableName,
						FSTDSQLS);
				// 20100318 dengcan
				/*
				 * String item_id=""; if(shitem!=null){
				 * item_id=shitem.getItemid(); if(item_id.length()>5)
				 * item_id=item_id.substring(0,5); } //
				 * if((shitem!=null&&!shitem.getItemid().equalsIgnoreCase(stdbo.getS_hitem().getItemid())&&shitem.getVarible()==1))
				 * if(item_id.length()>0&&!item_id.equalsIgnoreCase(stdbo.getS_hitem().getItemid())) {
				 * item_id=stdbo.getS_hitem().getItemid();
				 * if(stdbo.getS_hitem().isChangeAfter()||stdbo.getS_hitem().isChangeBefore())
				 * item_id=stdbo.getS_hitem().getItemid()+"_"+stdbo.getS_hitem().getNChgstate();
				 * FSTDSQLS.add("update "+TempTableName+" set
				 * "+item_id+"="+shitem.getItemid()); }
				 * 
				 * item_id=""; if(svitem!=null){ item_id=svitem.getItemid();
				 * if(item_id.length()>5) item_id=item_id.substring(0,5); } //
				 * if(svitem!=null&&!svitem.getItemid().equalsIgnoreCase(stdbo.getS_vitem().getItemid())&&svitem.getVarible()==1)
				 * if(item_id.length()>0&&!item_id.equalsIgnoreCase(stdbo.getS_vitem().getItemid())) {
				 * item_id=stdbo.getS_vitem().getItemid();
				 * if(stdbo.getS_hitem().isChangeAfter()||stdbo.getS_hitem().isChangeBefore())
				 * item_id=stdbo.getS_vitem().getItemid()+"_"+stdbo.getS_vitem().getNChgstate();
				 * FSTDSQLS.add("update "+TempTableName+" set
				 * "+item_id+"="+svitem.getItemid()); }
				 */
				
				stdbo.updateStdItem(list, TempTableName, FSTDSQLS);
				/** 关联更新串 */
				String joinon = stdbo.getStandardJoinOn(TempTableName, 2);

				String str="";
				if ("N".equalsIgnoreCase(rmenu.getItemtype()))
					str="(CASE WHEN "+Sql_switcher.datalength(Sql_switcher.isnull("GZ_ITEM.STANDARD","''"))+"=0 THEN '0' ELSE gz_item.standard END)";
				else
					str="(CASE WHEN "+Sql_switcher.datalength(Sql_switcher.isnull("GZ_ITEM.STANDARD","''"))+"=0 THEN '' ELSE gz_item.standard END)";
				switch (Sql_switcher.searchDbServer()) {
				case 1: // MSSQL
					// dbw.updateRecord(this.TempTableName,
					// "gz_item",joinon,this.TempTableName+"."+fieldname+"=gz_item.standard",
					// strWhere, ""); 
					FSTDSQLS.add(Sql_switcher.getUpdateSqlTwoTable(
							this.TempTableName, "gz_item", joinon,
							this.TempTableName + "." + fieldname
									+ "="+str, strWhere, ""));
					break;
				case 2:// oracle
					if ("N".equalsIgnoreCase(rmenu.getItemtype())) {
						// dbw.updateRecord(this.TempTableName,
						// "gz_item",joinon,this.TempTableName+"."+fieldname+"=to_number(gz_item.standard)",
						// strWhere, "");
						FSTDSQLS.add(Sql_switcher.getUpdateSqlTwoTable(
								this.TempTableName, "gz_item", joinon,
								this.TempTableName + "." + fieldname
										+ "=to_number("+str+")",
								strWhere, ""));
					} else {
						// dbw.updateRecord(this.TempTableName,
						// "gz_item",joinon,this.TempTableName+"."+fieldname+"=gz_item.standard",
						// strWhere, "");
						FSTDSQLS.add(Sql_switcher.getUpdateSqlTwoTable(
								this.TempTableName, "gz_item", joinon,
								this.TempTableName + "." + fieldname
										+ "="+str+"", strWhere, ""));
					}
					break;
				case 3:// db2
					if ("N".equalsIgnoreCase(rmenu.getItemtype())) {
						// dbw.updateRecord(this.TempTableName,
						// "gz_item",joinon,this.TempTableName+"."+fieldname+"=double(gz_item.standard)",
						// strWhere, "");
						FSTDSQLS.add(Sql_switcher.getUpdateSqlTwoTable(
								this.TempTableName, "gz_item", joinon,
								this.TempTableName + "." + fieldname
										+ "=double("+str+")",
								strWhere, ""));
					} else {
						// dbw.updateRecord(this.TempTableName,
						// "gz_item",joinon,this.TempTableName+"."+fieldname+"=gz_item.standard",
						// strWhere, "");
						FSTDSQLS.add(Sql_switcher.getUpdateSqlTwoTable(
								this.TempTableName, "gz_item", joinon,
								this.TempTableName + "." + fieldname
										+ "="+str, strWhere, ""));
					}
					break;
				}

			}// if bcheck end.
			fieldname = GetCurMenu(getAddTableName(), rmenu);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

		return fieldname;
	}
	
	
    /**
     * 就近套级套档(标准表号,结果指标,1|2)
     * 1:横向指标 2：纵向指标
     * @param retValue
     * @return
     * @throws GeneralException
     * @throws SQLException
     */
	private boolean Func_NearGetGrade(RetValue retValue)
			throws GeneralException, SQLException { 
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		/** 标准号 */
		if (!level6(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String stdid = SQL.toString();
		/** 对标准表进行检查 */
		SalaryStandardBo stdbo = new SalaryStandardBo(this.con, stdid, "");
		if (!checkStdId(stdbo))
			return false;
		if (!checkTwoDim(stdbo))
			return false; 
		/** 检查结束 */
		
		/** 结果指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem result_item = this.Field;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		/** 取横|纵指标值标识 */
		if (!level6(retValue))
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		String flag = SQL.toString();
		SQL.setLength(0);
		SQL.append(SQL_NearGetGrade(stdbo, result_item,Integer.parseInt(flag.trim())));
		
		FieldItem dimMenu=null;
		if("1".equals(flag.trim())) //横向指标
		{ 
			dimMenu=stdbo.getHDimMenu();
		}
		else
		{
			dimMenu=stdbo.getVDimMenu();
		} 
		if (dimMenu != null && "A".equalsIgnoreCase(dimMenu.getItemtype())) {
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		} else if (dimMenu != null && "N".equalsIgnoreCase(dimMenu.getItemtype())) {
			retValue.setValueType(FLOAT);
			retValue.setValueType(NULLVALUE);
		} else {
			retValue.setValueType(NULLVALUE);
		}
		return true;
	}
	
	
	
	/**
	 * 就近套级套档
	 * 
	 * @param SalaryStandardBo
	 *            标准表号
	 * @param result_item
	 *            结果指标
	 * @param flag
	 *            1:横向指标 2：纵向指标 
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private String SQL_NearGetGrade(SalaryStandardBo stdbo, FieldItem result_item,
			int flag) throws GeneralException,
			SQLException {
		StringBuffer strR = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		String oldTempTableName = TempTableName;
		// TempTableName=StdTmpTable;
		String strResult = null;
		FieldItem hDimMenu = stdbo.getHDimMenu();
		FieldItem vDimMenu = stdbo.getVDimMenu();
		
		FieldItem fldR = new FieldItem();
		fldR.setFieldsetid(TempTableName);
		fldR.setItemtype(stdbo.getHDimMenu().getItemtype());
		fldR.setItemid("std_" + StdFieldList.size());
		if(flag==1) //横向指标
		{
			fldR.setItemlength(stdbo.getHDimMenu().getItemlength());
			fldR.setDecimalwidth(stdbo.getHDimMenu().getDecimalwidth());
		}
		else
		{
			fldR.setItemlength(stdbo.getVDimMenu().getItemlength());
			fldR.setDecimalwidth(stdbo.getVDimMenu().getDecimalwidth());
		}
		
		fldR.setVarible(2);
		fldR.setItemdesc(fldR.getItemid() + "_" + stdbo.getStandID().trim()
				+ "_" + result_item.getItemid() + "_" +flag);
		// mapUsedFieldItems.put(fldR.getItemid(),fldR);
		strResult = fldR.getItemid();
		StdFieldList.add(fldR);

		strR.append(fldR.getItemid());
		strR.append(" ");
		strR.append(Sql_switcher.getFieldType(fldR.getItemtype().charAt(0),
				fldR.getItemlength(), fldR.getDecimalwidth()));
	  
 
		switch (DBType) {
		case Constant.ORACEL:
			strSQL.append(" alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(fldR.getItemid());
			FSTDSQLS.add(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" ADD ");
			strSQL.append(strR);
			FSTDSQLS.add(strSQL.toString()); 
			FSTDSQLS.add("alter table "+TempTableName+" drop column cz_"+StdFieldList.size());
			FSTDSQLS.add("alter table "+TempTableName+" add cz_"+StdFieldList.size()+" number(20,2) ");
			FSTDSQLS.add("alter table "+TempTableName+" drop column standard_"+StdFieldList.size());
			FSTDSQLS.add("alter table "+TempTableName+" add standard_"+StdFieldList.size()+" varchar2(20) "); 
			break;
		default:
			strSQL.append(" alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(fldR.getItemid());
			FSTDSQLS.add(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" ADD ");
			strSQL.append(strR);
			FSTDSQLS.add(strSQL.toString());
			FSTDSQLS.add("alter table "+TempTableName+" drop column cz_"+StdFieldList.size());
			FSTDSQLS.add("alter table "+TempTableName+" add cz_"+StdFieldList.size()+" float ");
			FSTDSQLS.add("alter table "+TempTableName+" drop column standard_"+StdFieldList.size());
			FSTDSQLS.add("alter table "+TempTableName+" add standard_"+StdFieldList.size()+" varchar(20) ");
			break;
		}
	
		String group_str="a0100";
		String join_str=TempTableName+".a0100=fff.a0100";
		if(ModeFlag == forNormal)
		{
			if (TempTableName.length() > 5&& (TempTableName.toLowerCase().indexOf("salary") != -1 ||TempTableName.toLowerCase().endsWith("_gz") || TempTableName.toLowerCase().endsWith("gzsp") ))
			{
				group_str+=",nbase";
				join_str+=" and "+TempTableName+".nbase=fff.nbase";
			}
			else
			{
				group_str+=",basepre";
				join_str+=" and "+TempTableName+".basepre=fff.basepre";
			}
		}
		
		strSQL.setLength(0);
		strSQL.append("update "+TempTableName+" set cz_"+StdFieldList.size()+"=( select cz from ( ");
		strSQL.append("select min(abs("+TempTableName+"."+result_item.getItemid()+"-");
		if(DBType==Constant.ORACEL)
			strSQL.append(Sql_switcher.sqlToInt("gz_item.standard"));
		else
			strSQL.append("convert(float,gz_item.standard)");
		strSQL.append(")) cz,"+group_str+"   from "+TempTableName+",gz_item where id="+stdbo.getStandID().trim()+" group by "+group_str+" )");
		strSQL.append(" fff where "+join_str+") ");
		FSTDSQLS.add(strSQL.toString());
		strSQL.setLength(0);
		strSQL.append("update "+TempTableName+" set standard_"+StdFieldList.size()+"=( select standard from ( ");
		strSQL.append("select  max(gz_item.standard) standard,"+group_str+" ");
		strSQL.append("from "+TempTableName+",gz_item where id="+stdbo.getStandID().trim()+"  and abs("+TempTableName+"."+result_item.getItemid()+"-");
		if(DBType==Constant.ORACEL)
			strSQL.append(Sql_switcher.sqlToInt("gz_item.standard"));
		else
			strSQL.append("convert(float,gz_item.standard)");
		strSQL.append(")="+TempTableName+".cz_"+StdFieldList.size()+" group by "+group_str+" ) fff  where "+join_str+") ");
		FSTDSQLS.add(strSQL.toString());
		 
		String sbz = "";
		if(flag==1)
		{
			if ("1".equalsIgnoreCase(hDimMenu.getState()))
				sbz = "gz_item.hvalue";
			else
				sbz = "gz_item.s_hValue";
		}
		else
		{
			if ("3".equalsIgnoreCase(vDimMenu.getState()))
				sbz = "gz_item.vValue";
			else
				sbz = "gz_item.s_vValue";
		}
		
		strSQL.setLength(0);
		strSQL.append("update "+TempTableName+" set "+fldR.getItemid()+"=( ");
		strSQL.append("select max("+sbz+") from gz_item where id="+stdbo.getStandID().trim()+"  and gz_item.standard="+TempTableName+".standard_"+StdFieldList.size()+" ");
		strSQL.append("group by standard) ");
		FSTDSQLS.add(strSQL.toString()); 
		
		TempTableName = oldTempTableName;
		return strResult;
	}
	
	
	
	
	

	/**
	 * 就近就高或就近就低函数 就近就高(标准表号, 纵向指标, 结果指标) 就近就低(标准表号, 纵向指标, 结果指标)
	 * 
	 * @param retValue
	 * @param mode
	 *            低|高 true|false
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_NearByHight(RetValue retValue, boolean mode)
			throws GeneralException, SQLException {
		if (mode)
			CurFuncNum = FUNCNEARBYHIGH;
		else
			CurFuncNum = FUNCNEARBYLOW;
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		if (token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		/** 标准号 */
		if (!level6(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String stdid = SQL.toString();
		/** 纵向指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem vitem = this.Field;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 结果指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem ritem = this.Field;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		/** 对标准表进行检查 */
		SalaryStandardBo stdbo = new SalaryStandardBo(this.con, stdid, "");
		if (!checkStdId(stdbo))
			return false;
		if (!checkTwoDim(stdbo))
			return false;
		if (!checkStdMenu(stdbo, vitem, 3))
			return false;
		if (!checkStdMenu(stdbo, ritem, 5))
			return false;
		/** 检查结束 */

		FieldItem hmenu = stdbo.getHitem();
		if (hmenu != null && "A".equalsIgnoreCase(hmenu.getItemtype())) {
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		}
		if (hmenu == null&&stdbo.getS_hitem()!=null)
		{
			if("A".equalsIgnoreCase(stdbo.getS_hitem().getItemtype()))
			{
				retValue.setValue("");
				retValue.setValueType(STRVALUE);
			}
			else if("N".equalsIgnoreCase(stdbo.getS_hitem().getItemtype()))
			{
				retValue.setValueType(FLOAT);
				retValue.setValueType(NULLVALUE);
			}
				
		}
		else if (hmenu != null && "N".equalsIgnoreCase(hmenu.getItemtype())) {
			retValue.setValueType(FLOAT);
			retValue.setValueType(NULLVALUE);
		} else {
			retValue.setValueType(NULLVALUE);
		}

		SQL.setLength(0);
		SQL.append(SQL_NearByHight(stdbo, vitem, ritem, mode));
		CurFuncNum = 0;
		return true;
	}

	/**
	 * 检查标准表中涉及的纵向指标及结果指标 与标准表定义的指标是否一致
	 * 
	 * @param stdbo
	 * @param item
	 * @param flag
	 *            （横向一｜横二｜纵一｜纵二）１｜２｜３｜４｜５
	 * @return
	 * @throws GeneralException
	 */
	private boolean checkStdMenu(SalaryStandardBo stdbo, FieldItem item,
			int flag) throws GeneralException {
		FieldItem menu = null;
		boolean b = false;
		switch (flag) {
		case 1:
			menu = stdbo.getHitem();
			if (item != null)
				stdbo.setHfactor(item.getItemid());// cmq added at 20080105
			break;
		case 2:
			menu = stdbo.getS_hitem();
			if (item != null)
				stdbo.setS_hfactor(item.getItemid());// cmq added at 20080105
			break;
		case 3:
			menu = stdbo.getVitem();
			if (item != null)
				stdbo.setVfactor(item.getItemid()); // cmq added at 20080105
			break;
		case 4:
			menu = stdbo.getS_vitem();
			if (item != null)
				stdbo.setS_vfactor(item.getItemid()); // cmq added at 20080105
			break;
		case 5:
			menu = stdbo.getR_item();
			break;
		}
		if (item == null || menu == null)
			return true;
		if (item != null && menu != null) {
			b = item.getItemtype().equalsIgnoreCase(menu.getItemtype());
			/** 变化前｜变化后标识0,1,2 */
			menu.setNChgstate(item.getNChgstate());
			if (b && item.isChar())
				b = item.getCodesetid().equalsIgnoreCase(menu.getCodesetid());
			/** 数值型 */
			if (!b && flag == 5) {
				b = (item.getItemtype().equalsIgnoreCase(menu.getItemtype()) && (!"D".equalsIgnoreCase(item
						.getItemtype())));
			}
		}
		if (!b) {
			Putback();
			SError(E_MENUNOTMATCH);
			return false;
		} else {
			// menu=(FieldItem)item.cloneItem();
			/*
			 * cs // 增加不在一个子集中指标的能力 if (Fld.cSetName <> TempTableName) and
			 * (FindMenu(Fld.cFldName, UsedFields)=-1) then begin Field1 :=
			 * TMenu.Create; FieldCopy(Fld,Field1); USedFields.Add(Field1); end;
			 */
		}
		return true;
	}

	/**
	 * 检查标准是否为二维标准表,且必须是代码型
	 * 
	 * @param stdbo
	 * @return
	 * @throws GeneralException
	 */
	private boolean checkTwoDim(SalaryStandardBo stdbo) throws GeneralException {
		boolean bflag = stdbo.isTwoDimension();
		FieldItem hitem = stdbo.getHDimMenu();
		FieldItem vitem = stdbo.getVDimMenu();
		boolean bresult = bflag && !"0".equalsIgnoreCase(hitem.getCodesetid())
				&& !"0".equalsIgnoreCase(vitem.getCodesetid());
		if (!bresult) {
			Putback();
			SError(E_STDNDARDNOTTWODIM);
			return false;
		}
		return true;
	}

	/**
	 * 检查标准表是否存在
	 * 
	 * @param stdbo
	 * @return
	 * @throws GeneralException
	 */
	private boolean checkStdId(SalaryStandardBo stdbo) throws GeneralException {
		if (!stdbo.isExist()) {
			Putback();
			SError(E_STDNDARDNOTEXIST);
			return false;
		}
		return true;
	}

	/**
	 * 就近就高｜低函数
	 * 
	 * @param SalaryStandardBo
	 *            标准表号
	 * @param vitem
	 *            纵向指标
	 * @param ritem
	 *            结果指标
	 * @param mode
	 *            就高就低标识
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private String SQL_NearByHight(SalaryStandardBo stdbo, FieldItem vitem,
			FieldItem ritem, boolean mode) throws GeneralException,
			SQLException {
		StringBuffer strR = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		String oldTempTableName = TempTableName;
		// TempTableName=StdTmpTable;
		String strResult = null;
		FieldItem hDimMenu = stdbo.getHDimMenu();
		FieldItem vDimMenu = stdbo.getVDimMenu();
		FieldItem fldR = new FieldItem();
		fldR.setFieldsetid(TempTableName);
		fldR.setItemtype(stdbo.getHDimMenu().getItemtype());
		fldR.setItemid("std_" + StdFieldList.size());
		fldR.setItemlength(stdbo.getHDimMenu().getItemlength());
		fldR.setDecimalwidth(stdbo.getHDimMenu().getDecimalwidth());
		fldR.setVarible(2);
		fldR.setItemdesc(fldR.getItemid() + "_" + stdbo.getStandID().trim()
				+ "_" + vitem.getItemid() + "_" + ritem.getItemid());
		// mapUsedFieldItems.put(fldR.getItemid(),fldR);
		strResult = fldR.getItemid();
		StdFieldList.add(fldR);

		strR.append(fldR.getItemid());
		strR.append(" ");
		strR.append(Sql_switcher.getFieldType(fldR.getItemtype().charAt(0),
				fldR.getItemlength(), fldR.getDecimalwidth()));

		switch (DBType) {
		case Constant.DB2:
			/** 暂时不支持 */

			break;
		case Constant.ORACEL:
			strSQL.append(" alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(fldR.getItemid());
			FSTDSQLS.add(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" ADD ");
			strSQL.append(strR);
			FSTDSQLS.add(strSQL.toString());
			break;
		default:
			strSQL.append(" alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(fldR.getItemid());
			FSTDSQLS.add(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" ADD ");
			strSQL.append(strR);
			FSTDSQLS.add(strSQL.toString());
			break;
		}
		String sbzH = "", sbzV = "";
		if ("1".equalsIgnoreCase(hDimMenu.getState()))
			sbzH = "gz_item.hvalue";
		else
			sbzH = "gz_item.s_hValue";

		if ("3".equalsIgnoreCase(vDimMenu.getState()))
			sbzV = "gz_item.vValue";
		else
			sbzV = "gz_item.s_vValue";
		if (mode)// 就近就高
		{
			strSQL.setLength(0);
			switch (DBType) {
			case Constant.DB2:
			case Constant.ORACEL:
				strSQL.append("update ");
				strSQL.append(TempTableName);
				strSQL.append(" SET ");
				strSQL.append(fldR.getItemid());
				strSQL.append("=(select h from (select ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" as v, ");
				strSQL.append(ritem.getItemid());
				strSQL.append(" as r,min(");
				strSQL.append(sbzH);
				strSQL.append(") as h ");
				strSQL.append(" from ");
				strSQL.append(TempTableName);
				strSQL.append(",gz_item ");
				strSQL.append(" where gz_item.id=");
				strSQL.append(stdbo.getStandID());
				strSQL.append(" and ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" = ");
				strSQL.append(sbzV);
				strSQL.append(" and ");
				strSQL.append(Sql_switcher.sqlToInt("gz_item.standard"));
				strSQL.append(">");
				strSQL.append(ritem.getItemid());
				strSQL.append(" group by ");
				strSQL.append(vitem.getItemid());
				strSQL.append(",");
				strSQL.append(ritem.getItemid());
				strSQL.append(") a");
				strSQL.append(" where ");
				strSQL.append(vitem.getItemid());
				strSQL.append("=a.v and a.r=");
				strSQL.append(ritem.getItemid());
				strSQL.append(")");
				FSTDSQLS.add(strSQL.toString());
				break;
			default:
				strSQL.append("update ");
				strSQL.append(TempTableName);
				strSQL.append(" SET ");
				strSQL.append(fldR.getItemid());
				strSQL.append("=A.H ");
				strSQL.append(" from (select ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" as v,");
				strSQL.append(ritem.getItemid());
				strSQL.append(" as r,min(");
				strSQL.append(sbzH);
				strSQL.append(") as H ");
				strSQL.append(" from ");
				strSQL.append(TempTableName);
				strSQL.append(",gz_item ");
				strSQL.append(" where gz_item.id=");
				strSQL.append(stdbo.getStandID());
				strSQL.append(" and ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" = ");
				strSQL.append(sbzV);
				strSQL.append(" and convert(float,gz_item.standard)>");
				strSQL.append(ritem.getItemid());
				strSQL.append(" and gz_item.standard<>'' ");
				strSQL.append(" group by ");
				strSQL.append(vitem.getItemid());
				strSQL.append(",");
				strSQL.append(ritem.getItemid());
				strSQL.append(") a ");
				strSQL.append(" where ");
				strSQL.append(vitem.getItemid());
				strSQL.append("=a.v and a.r=");
				strSQL.append(ritem.getItemid());
				FSTDSQLS.add(strSQL.toString());
				break;
			}
		} else// 就近就低
		{
			strSQL.setLength(0);
			switch (DBType) {
			case Constant.DB2:
			case Constant.ORACEL:
				strSQL.append("update ");
				strSQL.append(TempTableName);
				strSQL.append(" SET ");
				strSQL.append(fldR.getItemid());
				strSQL.append("=(select h from (select ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" as v, ");
				strSQL.append(ritem.getItemid());
				strSQL.append(" as r,max(");
				strSQL.append(sbzH);
				strSQL.append(") as h ");
				strSQL.append(" from ");
				strSQL.append(TempTableName);
				strSQL.append(",gz_item ");
				strSQL.append(" where gz_item.id=");
				strSQL.append(stdbo.getStandID());
				strSQL.append(" and ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" = ");
				strSQL.append(sbzV);
				strSQL.append(" and ");
				strSQL.append(Sql_switcher.sqlToInt("gz_item.standard"));
				strSQL.append("<=");
				strSQL.append(ritem.getItemid());
				strSQL.append(" group by ");
				strSQL.append(vitem.getItemid());
				strSQL.append(",");
				strSQL.append(ritem.getItemid());
				strSQL.append(") a");
				strSQL.append(" where ");
				strSQL.append(vitem.getItemid());
				strSQL.append("=a.v and a.r=");
				strSQL.append(ritem.getItemid());
				strSQL.append(")");
				FSTDSQLS.add(strSQL.toString());
				break;
			default:
				strSQL.append("update ");
				strSQL.append(TempTableName);
				strSQL.append(" SET ");
				strSQL.append(fldR.getItemid());
				strSQL.append("=A.H ");
				strSQL.append(" from (select ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" as v,");
				strSQL.append(ritem.getItemid());
				strSQL.append(" as r,max(");
				strSQL.append(sbzH);
				strSQL.append(") as H ");
				strSQL.append(" from ");
				strSQL.append(TempTableName);
				strSQL.append(",gz_item ");
				strSQL.append(" where gz_item.id=");
				strSQL.append(stdbo.getStandID());
				strSQL.append(" and ");
				strSQL.append(vitem.getItemid());
				strSQL.append(" = ");
				strSQL.append(sbzV);
				strSQL.append(" and convert(float,gz_item.standard)<=");
				strSQL.append(ritem.getItemid());
				strSQL.append(" and gz_item.standard<>'' ");
				strSQL.append(" group by ");
				strSQL.append(vitem.getItemid());
				strSQL.append(",");
				strSQL.append(ritem.getItemid());
				strSQL.append(") a ");
				strSQL.append(" where ");
				strSQL.append(vitem.getItemid());
				strSQL.append("=a.v and a.r=");
				strSQL.append(ritem.getItemid());
				FSTDSQLS.add(strSQL.toString());
				break;
			}
		}
		TempTableName = oldTempTableName;
		return strResult;
	}

	/**
	 * 代码调整(指标,增量指标,极大值,极小值)
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_CODEADJUST(RetValue retValue) throws GeneralException,
			SQLException {
		CurFuncNum = FUNCCODEADJUST;
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;
		if ("".equalsIgnoreCase(item.getCodesetid())
				|| "0".equalsIgnoreCase(item.getCodesetid())) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		} 
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 增量指标必须是数值型 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem fldadjust = this.Field;
		if (!(fldadjust.isInt())) {
			Putback();
			SError(E_MUSTBEINTEGERMENU);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (token_type != QUOTE) {
			Putback();
			SError(E_LOSSQUOTE);
			return false;
		}
		/** 最大值 */
		if (!level6(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String sMax = SQL.toString().trim();
		sMax = sMax.replaceAll("'", "");
		if (!Get_Token())
			return false;
		if (token_type != QUOTE) {
			Putback();
			SError(E_LOSSQUOTE);
			return false;
		}
		/** 最小值 */
		SQL.setLength(0);
		if (!level6(retValue))
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		String sMin = SQL.toString().trim();
		sMin = sMin.replaceAll("'", "");
		if (!Get_Token())
			return false;

		SQL.setLength(0);
		SQL.append(SQL_CodeAdjust(item, fldadjust, sMax, sMin));
		return true;
	}
	
	/**
	 * 上一级代码(指标名称,[代码类]）
	 * @param retValue
	 * @param mode
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_SUPERIORCODE(RetValue retValue, int mode)
	throws GeneralException, SQLException {
		String str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		/** 指标 */
		if (!Get_Token())
			return false;
	   
		String str1 =this.token; 
		String codesetid="";
		if (IsFieldItem(str1)) { 
			FieldItem item=(FieldItem)this.Field.clone();
			
			if (!Get_Token())
				return false;
			if (tok == S_RPARENTHESIS)
			{
				codesetid=item.getCodesetid();
				if(codesetid==null|| "0".equals(codesetid)||codesetid.trim().length()==0)
				{
					Putback();
					SError(E_LOSSCOMMA);
					return false;
				}
				if (!Get_Token())
					return false;
			}
			else if (tok== S_COMMA) {
				
				if (!Get_Token())
					return false;
		        if (!level6(retValue))
		            return false;
		        codesetid = SQL.toString().trim();
		        codesetid = codesetid.replaceAll("'", "");
		        /*
				if (token_type != STRVALUE) {
					Putback();
					SError(E_MUSTBEINTEGER);
					return false;
				}
				codesetid=this.token;
				
				if (!Get_Token())
					return false;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				*/
				if (!Get_Token())
					return false;
			}
			else if (tok != S_COMMA) {
				Putback();
				SError(E_LOSSCOMMA);
				return false;
			}
		}
		else {
			SError(E_NOTFINDFIELD);
			return false;
		}
	
		
//	yyyyyyy
        retValue.setValue("");
        retValue.setValueType(STRVALUE);
		//String _value=this.SQL.toString();
		SQL.setLength(0);
		SQL.append(SQL_SUPERIORCODE(this.Field,codesetid));
		return true; 
	}

	   /**
     * 
     * @param Field 
     *            指标
     * @param Codeid
     *            代码类
     * @return 上一级代码 
     */
    private String SQL_SUPERIORCODE(FieldItem Field,String Codeid) {
        String Codeitem = "";
        FieldItem Field1 = new FieldItem();
        StringBuffer strSQL = new StringBuffer(), strAs = new StringBuffer(), strKeyField = new StringBuffer(), strSetTableName = new StringBuffer(), strFrom = new StringBuffer(), strLeft = new StringBuffer(), strSet = new StringBuffer();
        Codeitem = GetCodeItemTableName(Codeid);

        if (ModeFlag == forSearch) {//查询
            
            String cCalcFld = "SUPERCODE_" + Field.getItemid();
            strAs.setLength(0);            
            strAs.append(cCalcFld);

            strKeyField.setLength(0);
            strKeyField.append(getKeyField(Field.getFieldsetid()));

            strSetTableName.setLength(0);
            strSetTableName.append(GetCurSet(Field));

            if (FindMenu(strAs.toString(), mapUsedFieldItems)) {
                return strAs.toString();
            }

            // FieldCopy(Field,Field1);
            Field1 = (FieldItem) Field.cloneItem();// ?????????????????????
            Field1.setItemid(strAs.toString());
            Field1.setVarible(2);// nIsVar := 2;
            /** 代码转字符函数,cmq added */
            if("UN".equalsIgnoreCase(Codeid))
                Field1.setItemlength(200);
            else
                Field1.setItemlength(70);
            // USedFields.Add(Field1);
            mapUsedFieldItems.put(Field1.getItemid(), Field1);
            String _tempTable = "T_" + TempTableName;
            if (DBType == Constant.MSSQL && this.isTempTable)
                _tempTable = "##T_" + TempTableName;
            if (!IsMainSet(Field.getItemid())) {// then //处理最后一条记录
                if (DBType == Constant.MSSQL) {
                    if (this.isTempTable)
                        SQLS.add("DROP TABLE ##T_" + TempTableName);
                    else
                        SQLS.add("DROP TABLE T_" + TempTableName);
                } else {
               //     if (this.isTempTable)
               //         SQLS.add("truncate table T_" + TempTableName);
                    SQLS.add("DROP TABLE T_" + TempTableName+" purge");
                }

                if (DBType == 3) {
                    strSQL.setLength(0);
                    strSQL.append("CREATE TABLE T_").append(TempTableName)
                            .append(" AS (SELECT ").append(strKeyField).append(
                                    ",MAX(I9999) AS MAX_I9999 ");
                    strSQL.append(" FROM ").append(strSetTableName);
                    strSQL.append(" GROUP BY ").append(strKeyField).append(
                            ") DEFINITION ONLY");
                    SQLS.add(strSQL.toString());
                 
                    
                    strSQL.setLength(0);
                    strSQL.append("INSERT INTO T_").append(TempTableName)
                            .append(" SELECT ").append(strKeyField).append(
                                    ",MAX(I9999) AS MAX_I9999 ");
                    strSQL.append(" FROM ").append(strSetTableName);
                    strSQL.append(" GROUP BY ").append(strKeyField);
                    SQLS.add(strSQL.toString());
                    
                    SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
                     
                    
                } else if (DBType == Constant.ORACEL) {
                    strSQL.setLength(0);
                    strSQL.append("CREATE ");
                    if (this.isTempTable)
                        strSQL.append(" GLOBAL TEMPORARY ");
                    strSQL.append(" TABLE  T_").append(TempTableName);
                    if (this.isTempTable)
                        strSQL.append("  On Commit Preserve Rows ");
                    strSQL.append(" AS (SELECT ").append(strKeyField).append(
                            ",MAX(I9999) AS MAX_I9999 ");
                    strSQL.append(" FROM ").append(strSetTableName);
                    strSQL.append(" GROUP BY ").append(strKeyField)
                            .append(") ");
                    SQLS.add(strSQL.toString());
                 
                    SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
                     
                } else {
                    strSQL.setLength(0);
                    if (this.isTempTable) {
                        strSQL.append("SELECT ").append(strKeyField).append(
                                ",MAX(I9999) AS MAX_I9999 INTO ##T_").append(
                                TempTableName);
                    } else {
                        strSQL.append("SELECT ").append(strKeyField).append(
                                ",MAX(I9999) AS MAX_I9999 INTO T_").append(
                                TempTableName);
                    }
                    strSQL.append(" FROM ").append(strSetTableName);
                    strSQL.append(" GROUP BY ").append(strKeyField);
                    SQLS.add(strSQL.toString());
                    if (this.isTempTable) 
                        SQLS.add("create index T_"+TempTableName+"_index  on ##T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
                    else
                        SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
                 
                }
                strSQL.setLength(0);
                strSQL.append("UPDATE ").append(TempTableName);
                if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                    strLeft.setLength(0);
                    strLeft.append(" WHERE ").append(TempTableName).append(".")
                            .append(strKeyField).append("=").append(_tempTable)
                            .append(".").append(strKeyField).append(")");

                    strSet.setLength(0);
                    strSet.append(" SET ").append(TempTableName).append(
                            ".I9999=(SELECT ").append(_tempTable).append(
                            ".MAX_I9999 ");

                    strFrom.setLength(0);
                    strFrom.append(" From ").append(_tempTable);
                } else {
                    strLeft.setLength(0);
                    strLeft.append(" LEFT JOIN ").append(_tempTable).append(
                            " ON ").append(TempTableName).append(".").append(
                            strKeyField).append("=").append(_tempTable).append(
                            ".").append(strKeyField);

                    strSet.setLength(0);
                    strSet.append(" SET ").append(TempTableName).append(
                            ".I9999=").append(_tempTable).append(".MAX_I9999");

                    strFrom.setLength(0);
                    strFrom.append(" From ").append(TempTableName);
                }

                switch (DBType) {
                case 3:
                case 2: {
                    strSQL.append(strSet).append(strFrom).append(strLeft);
                    break;
                }
                case 1: {
                    strSQL.append(strSet).append(strFrom).append(strLeft);
                    break;
                }
                case 0: {
                    strSQL.append(strLeft).append(strSet);
                }
                }
                SQLS.add(strSQL.toString());
            }
            strSQL.setLength(0);
            strSQL.append("UPDATE ").append(TempTableName); // 将内容拷到临时表中
            if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                strLeft.setLength(0);
                strLeft.append(" WHERE ").append(TempTableName).append(".")
                        .append(strKeyField).append("=")
                        .append(strSetTableName).append(".")
                        .append(strKeyField);
                if (!IsMainSet(Field.getFieldsetid())) {// Is Sub Set
                    strLeft.append(" AND ").append(TempTableName).append(
                            ".I9999=").append(strSetTableName)
                            .append(".I9999)");
                } else {
                    strLeft.append(")");
                }
                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(".SUPERCODE_")
                        .append(Field.getItemid()).append("=(SELECT ").append(
                                strSetTableName).append(".").append(
                                Field.getItemid());

                strFrom.setLength(0);
                strFrom.append(" From ").append(strSetTableName);
            } else {
                strLeft.setLength(0);
                strLeft.append(" LEFT JOIN ").append(strSetTableName).append(
                        " ON ").append(TempTableName).append(".").append(
                        strKeyField).append("=").append(strSetTableName)
                        .append(".").append(strKeyField);
                if (!IsMainSet(Field.getFieldsetid())) {// Is Sub Set
                    strLeft.append(" AND ").append(TempTableName).append(
                            ".I9999=").append(strSetTableName).append(".I9999");
                }
                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(".SUPERCODE_")
                        .append(Field.getItemid()).append("=").append(
                                strSetTableName).append(".").append(
                                Field.getItemid());

                strFrom.setLength(0);
                strFrom.append(" From ").append(TempTableName);

            }

            switch (DBType) {
            case 3:
            case 2: {
                strSQL.append(strSet).append(strFrom).append(strLeft);
                break;
            }
            case 1: {
                strSQL.append(strSet).append(strFrom).append(strLeft);
                break;
            }
            case 0: {
                strSQL.append(strLeft).append(strSet);
                break;
            }
            }
            SQLS.add(strSQL.toString());
            strSQL.setLength(0);
            strSQL.append("UPDATE ").append(TempTableName); //上一级代码
            if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                strLeft.setLength(0);
                strLeft.append(" WHERE ").append(TempTableName)
                        .append(".SUPERCODE_").append(Field.getItemid()).append("=")
                        .append(Codeitem).append(".CODEITEMID");
   
                strLeft.append(")");
        
                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(".SUPERCODE_")
                        .append(Field.getItemid()).append("=(SELECT ").append(
                                Codeitem).append(".parentid");

                strFrom.setLength(0);
                strFrom.append(" FROM ").append(Codeitem);
            } else {
                strLeft.setLength(0);
                strLeft.append(" LEFT JOIN ").append(Codeitem).append(" ON ")
                        .append(TempTableName).append(".SUPERCODE_").append(
                                Field.getItemid()).append("=").append(Codeitem)
                        .append(".CODEITEMID");

                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(".SUPERCODE_")
                        .append(Field.getItemid()).append("=").append(Codeitem)
                        .append(".parentid");

                strFrom.setLength(0);
                strFrom.append(" FROM ").append(TempTableName);
            }

            strSQL.append(strSet).append(strFrom).append(strLeft);
            if (!"ORGANIZATION".equalsIgnoreCase(Codeitem)){               
                
                if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                    strSQL.append(" AND ").append(Codeitem).append(".CODESETID =")
                    .append("'").append(Codeid).append("')");      
                    strSQL.append(" where Exists(SELECT 1 FROM ").append(Codeitem).append(strLeft)
                    .append(" AND ").append(Codeitem).append(".CODESETID =").append("'").append(Codeid).append("')");
                } else {
                    strSQL.append(" WHERE ").append(Codeitem)
                    .append(".CODESETID =").append("'").append(Codeid)
                    .append("'");
                }
            }
            SQLS.add(strSQL.toString());
           // if(Codeid.equalsIgnoreCase("UM"))
            //    SQLS.add(strSQL.toString().replaceAll("UM","UN"));
            result =Sql_switcher.isnull("SUPERCODE_" + Field.getItemid(),"''");
        } else // 单表
        {
            String sConFld = "SUPERCODE_" + FCTONSQLS.size();
            String strR ="";
            
            if (DBType == Constant.MSSQL)
                strR = sConFld + Sql_switcher.getFieldType('M', 200, 0);
            else
            {
                strR = sConFld + Sql_switcher.getFieldType('A', 200, 0);

            }
             
            strSQL.setLength(0);
            strSQL.append("alter table ");
            strSQL.append(TempTableName);
            strSQL.append(" drop column ");
            strSQL.append(sConFld);
            FCTONSQLS.add(strSQL.toString());

            strSQL.setLength(0);
            strSQL.append("alter table ");
            strSQL.append(TempTableName);
            strSQL.append(" add ");
            strSQL.append(strR);
            FCTONSQLS.add(strSQL.toString());

            strSQL.setLength(0);
            strSQL.append("UPDATE ").append(TempTableName); // 将代码转成名称
            if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                strLeft.setLength(0);
                strLeft.append(" WHERE ").append(TempTableName).append(".")
                        .append(Field.getItemid()).append("=").append(Codeitem)
                        .append(".CODEITEMID");

                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(
                        ".LLEEFFTT=(SELECT ").append(Codeitem).append(
                        ".parentid");

                strFrom.setLength(0);
                strFrom.append(" FROM ").append(Codeitem);
            } else {
                strLeft.setLength(0);
                strLeft.append(" LEFT JOIN ").append(Codeitem).append(" ON ")
                        .append(TempTableName).append(".").append(
                                Field.getItemid()).append("=").append(Codeitem)
                        .append(".CODEITEMID");

                strSet.setLength(0);
                strSet.append(" SET ").append(TempTableName).append(
                        ".LLEEFFTT=").append(Codeitem).append(".parentid");

                strFrom.setLength(0);
                strFrom.append(" FROM ").append(TempTableName);
            }
            strSQL.append(strSet).append(strFrom).append(strLeft);
            if (!"ORGANIZATION".equalsIgnoreCase(Codeitem)){         
                if ((DBType == Constant.ORACEL) || (DBType == 3)) {
                    strSQL.append(" AND ").append(Codeitem).append(".CODESETID =")
                    .append("'").append(Codeid).append("')");
                } else {
                    strSQL.append(" WHERE ").append(Codeitem)
                    .append(".CODESETID =").append("'").append(Codeid)
                    .append("'");
                }
            }
            String cSql= strSQL.toString().replaceAll("LLEEFFTT", sConFld);
            //tmp = strSQL.toString().replaceAll("LLEEFFTT", strLeft);
            FCTONSQLS.add(cSql);
           // replaceCTONLeft(sConFld);
            result = sConFld;
            
            if (DBType == Constant.MSSQL&&ModeFlag==this.forNormal)
            {
                if("UN".equalsIgnoreCase(Codeid)|| "UM".equalsIgnoreCase(Codeid))
                {
                    result = "CAST("+sConFld+" as varchar(200))";
                }
                else
                {
                    result = "CAST("+sConFld+" as varchar(70))";
                }
            }
            
        }
        return result;
    }

	

	/**
	 * 前一个代码（指标,增量,极值)
	 * 
	 * @param retValue
	 * @param mode
	 *            (前一个代码|后一个代码)(-1|1) 0：兼容代码变档
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_CODEADD(RetValue retValue, int mode)
			throws GeneralException, SQLException {
		int flag = 1;
		CurFuncNum = FUNCCODEADD;
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;
		if ("".equalsIgnoreCase(item.getCodesetid())
				|| "0".equalsIgnoreCase(item.getCodesetid())) {
			Putback();
			SError(E_MUSTBECODEFIELD);
			return false;
		}
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		/** 增量,必须是整数正或负 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (tok == S_MINUS)// 可以考虑不用负数，要不然和前|后代码函数让用户误解
		{

			flag = -1;
			if (!Get_Token())
				return false;
		}

		if (this.token_type != INT) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		if (!level6(retValue))
			return false;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		String sAdd = SQL.toString().trim();

		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (token_type != QUOTE) {
			Putback();
			SError(E_LOSSQUOTE);
			return false;
		}

		if (!level6(retValue))
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		String sMax = SQL.toString().trim();
		sMax = sMax.replaceAll("'", "");
		if (!Get_Token())
			return false;
		SQL.setLength(0);
		int nadd = Integer.parseInt(sAdd);
		if (nadd == 0)
			SQL.append(item.getItemid());
		else {
			SQL.append(SQL_CodeAdd(item, flag * nadd * mode, sMax));
		}
		CurFuncNum = 0;
		return true;
	}

	/**
	 * 代码调整
	 * 
	 * @param item
	 * @param fldadjust
	 * @param sMax
	 * @param sMin
	 * @return
	 * @throws GeneralException
	 */
	private String SQL_CodeAdjust(FieldItem item, FieldItem fldadjust,
			String sMax, String sMin) throws GeneralException {
		StringBuffer strSQL = new StringBuffer();
		String sTmpTb = "T_" + TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
			sTmpTb = "##T_" + TempTableName;

		++nCodeAddTime;
		FieldItem item0 = null;
		item0 = (FieldItem) item.cloneItem();
		item0.setFieldsetid(TempTableName);
		item0.setItemid("CodeADD_" + nCodeAddTime);
		String sRowFld = "CodeRow_" + nCodeAddTime;
		String sAdjFld = "CodeAdj";
		switch (DBType) {
		case Constant.DB2:
			/** 暂时不支挂 */
			break;
		case Constant.ORACEL:
			strSQL.append("create ");
			if (this.isTempTable)
				strSQL.append(" GLOBAL TEMPORARY ");
			strSQL.append(" TABLE  ");
			strSQL.append(sTmpTb);
			if (this.isTempTable)
				strSQL.append(" On Commit Preserve Rows  ");
			strSQL.append(" as select codeitemid,rownum rowid1 ");
			strSQL.append(" from codeitem where codesetid='");
			strSQL.append(item.getCodesetid());
			strSQL.append("'");
			break;
		default:
			strSQL
					.append("select codeitemid,Identity(int ,1,1) as rowid1 into ");
			strSQL.append(sTmpTb);
			strSQL.append(" from codeitem where codesetid='");
			strSQL.append(item.getCodesetid());
			strSQL.append("'");
			break;
		}
		if (sMax.length() > 0) {
			strSQL.append(" and ");
			strSQL.append(" codeitemid<='");
			strSQL.append(sMax);
			strSQL.append("'");
		}
		if (sMin.length() > 0) {
			strSQL.append(" and ");
			strSQL.append(" codeitemid>='");
			strSQL.append(sMin);
			strSQL.append("'");
		}
	//	if (DBType != Constant.MSSQL && this.isTempTable)
	//		FSTDSQLS.add("truncate table " + sTmpTb);
		if (DBType == Constant.ORACEL)
				FSTDSQLS.add("drop table " + sTmpTb+" purge");
		else
			FSTDSQLS.add("drop table " + sTmpTb);
		FSTDSQLS.add(strSQL.toString());

		/** 增加临时处理用到的指标 */
		String sConFld = item0.getItemid();
		String strR = sConFld
				+ Sql_switcher.getFieldType('A', item0.getItemlength(), 0);
		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" drop column ");
		strSQL.append(sConFld);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" add ");
		strSQL.append(strR);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" drop column ");
		strSQL.append(sRowFld);
		FSTDSQLS.add(strSQL.toString());

		strR = sRowFld + Sql_switcher.getFieldType('N', 10, 0);
		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" add ");
		strSQL.append(strR);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" drop column ");
		strSQL.append(sAdjFld);
		FSTDSQLS.add(strSQL.toString());

		strR = sAdjFld + Sql_switcher.getFieldType('N', 10, 0);
		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" add ");
		strSQL.append(strR);
		FSTDSQLS.add(strSQL.toString());

		/** 和CS语法分析器有区别 */
		strSQL.setLength(0);
		strSQL.append("update ");
		strSQL.append(TempTableName);
		strSQL.append(" set ");
		strSQL.append(sAdjFld);
		strSQL.append("=");
		strSQL.append(fldadjust.getItemid());
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("update ");
		strSQL.append(TempTableName);
		strSQL.append(" set ");
		strSQL.append(item0.getItemid());
		strSQL.append("=");
		strSQL.append(item.getItemid());
		FSTDSQLS.add(strSQL.toString());

		switch (DBType) {
		case Constant.DB2:
			/** 暂时先不支持 */
			break;
		case Constant.ORACEL:
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append("(select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			// strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".");
			strSQL.append("codeitemid)");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sRowFld);
			strSQL.append("+");
			strSQL.append(sAdjFld);

			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append("(select codeitemid from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1)");
			FSTDSQLS.add(strSQL.toString());

			/** 处理运算后超过极值的，直接改为极值 */
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMax);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(") and ");
			strSQL.append(sAdjFld);
			strSQL.append(">0");
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMin);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(") and ");
			strSQL.append(sAdjFld);
			strSQL.append("<0");
			FSTDSQLS.add(strSQL.toString());
			break;
		default:
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".");
			strSQL.append("codeitemid");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sRowFld);
			strSQL.append("+");
			strSQL.append(sAdjFld);
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".codeitemid from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1");
			FSTDSQLS.add(strSQL.toString());

			/** 处理运算后超过极值的，直接改为极值 */
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMax);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(") and ");
			strSQL.append(sAdjFld);
			strSQL.append(">0");
			strSQL.append("");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMin);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(") and ");
			strSQL.append(sAdjFld);
			strSQL.append("<0");
			strSQL.append("");
			FSTDSQLS.add(strSQL.toString());

			break;
		}

		return item0.getItemid();
	}

	/**
	 * @param item
	 *            代码型指档
	 * @param nstep
	 *            步长
	 * @param sMax
	 *            极值代码项
	 * @throws GeneralException
	 */
	private String SQL_CodeAdd(FieldItem item, int nstep, String sMax)
			throws GeneralException {
		StringBuffer strSQL = new StringBuffer();
		String sTmpTb = "T_" + TempTableName;
		if (DBType == Constant.MSSQL && isTempTable)
			sTmpTb = "##T_" + TempTableName;

		++nCodeAddTime;
		FieldItem item0 = null;
		item0 = (FieldItem) item.cloneItem();
		item0.setFieldsetid(TempTableName);
		item0.setItemid("CodeADD_" + nCodeAddTime);
		String sRowFld = "CodeRow_" + nCodeAddTime;
		switch (DBType) {
		case Constant.DB2:
			break;
		case Constant.ORACEL:
			strSQL.append("create ");
			if (this.isTempTable)
				strSQL.append(" GLOBAL TEMPORARY ");
			strSQL.append(" TABLE  ");
			strSQL.append(sTmpTb);
			if (this.isTempTable)
				strSQL.append("  On Commit Preserve Rows ");
			strSQL.append("  as select codeitemid,rownum rowid1 ");
			strSQL.append(" from codeitem where codesetid='");
			strSQL.append(item.getCodesetid());
			strSQL.append("'");
			break;
		default:
			strSQL
					.append("select codeitemid,Identity(int ,1,1) as rowid1 into ");
			strSQL.append(sTmpTb);
			strSQL.append(" from codeitem where codesetid='");
			strSQL.append(item.getCodesetid());
			strSQL.append("'");
			break;
		}

		if (sMax.length() > 0) {
			if (nstep > 0)
				strSQL.append(" and codeitemid<='");
			else
				strSQL.append(" and codeitemid>='");
			strSQL.append(sMax);
			strSQL.append("'");
		}
		strSQL.append(" order by codeitemid");

	//	if (DBType != Constant.MSSQL && this.isTempTable)
	//		FSTDSQLS.add("truncate table " + sTmpTb);
		if (DBType == Constant.ORACEL)
			FSTDSQLS.add("drop table " + sTmpTb+" purge");
		else
			FSTDSQLS.add("drop table " + sTmpTb);

		FSTDSQLS.add(strSQL.toString());
		/** 增加临时处理用到的指标 */
		String sConFld = item0.getItemid();
		String strR = sConFld
				+ Sql_switcher.getFieldType('A', item0.getItemlength(), 0);
		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" drop column ");
		strSQL.append(sConFld);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" add ");
		strSQL.append(strR);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" drop column ");
		strSQL.append(sRowFld);
		FSTDSQLS.add(strSQL.toString());

		strR = sRowFld + Sql_switcher.getFieldType('N', 10, 0);
		strSQL.setLength(0);
		strSQL.append("alter table ");
		strSQL.append(TempTableName);
		strSQL.append(" add ");
		strSQL.append(strR);
		FSTDSQLS.add(strSQL.toString());

		strSQL.setLength(0);
		strSQL.append("update ");
		strSQL.append(TempTableName);
		strSQL.append(" set ");
		strSQL.append(item0.getItemid());
		strSQL.append("=");
		strSQL.append(item.getItemid());
		FSTDSQLS.add(strSQL.toString());

		switch (DBType) {
		case Constant.DB2:
			/** 暂时先不支持 */
			break;
		case Constant.ORACEL:
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append("(select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			// strSQL.append(sRowFld);
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".");
			strSQL.append("codeitemid)");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sRowFld);
			strSQL.append("+(");
			strSQL.append(nstep);
			strSQL.append(")");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append("(select codeitemid from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1)");
			FSTDSQLS.add(strSQL.toString());

			/** 处理运算后超过极值的，直接改为极值 */
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMax);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(")");
			FSTDSQLS.add(strSQL.toString());
			break;
		default:
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".");
			strSQL.append("codeitemid");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sRowFld);
			strSQL.append("+(");
			strSQL.append(nstep);
			strSQL.append(")");
			FSTDSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".codeitemid from ");
			strSQL.append(sTmpTb);
			strSQL.append(" where ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(sRowFld);
			strSQL.append("=");
			strSQL.append(sTmpTb);
			strSQL.append(".rowid1");
			FSTDSQLS.add(strSQL.toString());

			/** 处理运算后超过极值的，直接改为极值 */
			strSQL.setLength(0);
			strSQL.append("update ");
			strSQL.append(TempTableName);
			strSQL.append(" set ");
			strSQL.append(TempTableName);
			strSQL.append(".");
			strSQL.append(item0.getItemid());
			strSQL.append("='");
			strSQL.append(sMax);
			strSQL.append("'");
			strSQL.append(" where not (");
			strSQL.append(sRowFld);
			strSQL.append(" is null ) and not ");
			strSQL.append(sRowFld);
			strSQL.append(" in (select rowid1 from ");
			strSQL.append(sTmpTb);
			strSQL.append(")");
			FSTDSQLS.add(strSQL.toString());

			break;
		}

		return item0.getItemid();
	}

	
	/**
	 * 取对象指标值
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_OBJECTFIELD(RetValue retValue) throws GeneralException,
	SQLException {
	
		String str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		/** 指标 */
		if (!Get_Token())
			return false;
	   
		String str1 =this.token; 
		if (IsFieldItem(str1)) { 
				bracketsFieldMap.put(token,(FieldItem)this.Field.clone()); 
				mapUsedFieldItems.put(this.Field.getItemid(),(FieldItem)this.Field.clone());
				String _value="";
				if(bracketsFieldValueMap!=null&&bracketsFieldValueMap.size()>0&&bracketsFieldValueMap.get(token)!=null)
				{
					 _value=(String)bracketsFieldValueMap.get(token);
				}
				FieldItem _item=(FieldItem)bracketsFieldMap.get(token);
				if("A".equalsIgnoreCase(_item.getItemtype())|| "M".equalsIgnoreCase(_item.getItemtype()))
				{
					if (this.DBType == Constant.ORACEL && "".equals(_value))
						SQL_ADD("NULL");
					else
						SQL_ADD("'" + _value + "'");
					retValue.setValue(token);
					retValue.setValueType(STRVALUE);
				}
				else if("N".equalsIgnoreCase(_item.getItemtype()))
				{
					if("".equals(_value))
						_value="0";
					SQL_ADD(_value);
					if(_item.getDecimalwidth()>0)
					{
						retValue.setValue(Float.valueOf(_value));
						retValue.setValueType(FLOAT);
					}
					else
					{
						retValue.setValue(Integer.valueOf(_value));
						retValue.setValueType(INT);
					}
				}
				else if("D".equalsIgnoreCase(_item.getItemtype()))
				{
					//_value="'2006.06.28'"
					if("".equals(_value))
						_value="1900.01.01";
					SQL_ADD(Sql_switcher.dateValue(_value));
					retValue.setValue(token);
					retValue.setValueType(DATEVALUE);
				} 
			  
		}
		else {
			SError(E_NOTFINDFIELD);
			return false;
		}
		
		if (!Get_Token())
			return false;
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
	 
		if (!Get_Token())
			return false;
		String _value=this.SQL.toString();
		SQL.setLength(0);
		SQL.append(str+_value);
		return true;
	}
	
	/**
	 * 取岗位值
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	
	private boolean Func_E01A1VALUE(RetValue retValue) throws GeneralException,
			SQLException {
		String str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		/** 指标 */
		if (!Get_Token())
			return false;
	  
		if (!level0(retValue))
			return false;
		String str1 = SQL.toString();
		FieldItem resultField=(FieldItem)this.Field.clone();
		String temp="";
		String cond_str="";
		if(this.Field!=null)
			temp=this.Field.getItemid();
		if (tok == S_COMMA)
		{
			/*if(temp.length()>0)
				str1=str1.replaceAll(temp,  TempTableName+"."+temp);*/
			this.isGetK01a1Param=true; //是否是取岗位值参数
			if (!Get_Token())
				return false;
			
			if (token_type != FIELDITEM||this.Field.getFieldsetid().toUpperCase().trim().charAt(0)!='K') {
				Putback();
				SError(E_MUSTBEFIELDITEMPOS);
				return false;
			}
			String str2=this.Field.getItemid();
			resultField=(FieldItem)this.Field.clone();
			/*if (!level0(retValue))
				return false;
			String str2 = SQL.toString();*/
			if (!Get_Token())
				return false;
			 
			if (tok == S_COMMA)
			{
				SQL.setLength(0);
				if (!Get_Token())
					return false;
				if (!level0(retValue))
					return false; 
				cond_str= SQL.toString();
				if(cond_str.trim().length()>0)
					cond_str=" and "+cond_str;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				
			}
			else if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			
			
			String value = "";
			String userSet=resultField.getFieldsetid();
			FieldSet vo = DataDictionary.getFieldSetVo(userSet);
			String z0 = userSet + "Z0";
			String z1 = userSet + "Z1";
			String monthOfz0 = Sql_switcher.month(z0);
			String yearOfz0 = Sql_switcher.year(z0);
			
			boolean isOtherField=false;
			for(int i=0;i<condItemK01a1List2.size();i++)
			{
				FieldItem item=(FieldItem)condItemK01a1List2.get(i);
				if(!item.getFieldsetid().equalsIgnoreCase(userSet))
					isOtherField=true;
			}
			
			if(!"K01".equalsIgnoreCase(userSet))
			{
				//0：一般子集  	1：按月变化子集  	2：按年变化子集
				if (ymc != null && ("2".equals(vo.getChangeflag().trim())|| "1".equals(vo.getChangeflag().trim()))) {
					value = "(select * from " + userSet + " a where 1=1 ";
					 if("1".equals(vo.getChangeflag().trim())&&cond_str.length()==0)
						 value+= " and "+monthOfz0 + "="+ ymc.getMonth(); 
					 if(cond_str.length()==0)  
						 value+= " and " + yearOfz0 + "="+ ymc.getYear() ;
					 
					 value+= " and  a.I9999=(select max(b.I9999) from " + userSet+ " b ";
					 
					 if(cond_str.length()>0&&isOtherField)
						 value+=","+TempTableName;
					 
					 value+=" where 1=1 "; 
					 if("1".equals(vo.getChangeflag().trim())&&cond_str.length()==0)
						  value+= " and " + monthOfz0 + "="+ ymc.getMonth() ;
					 if(cond_str.length()==0)  
						  value+= " and " + yearOfz0 + "="+ ymc.getYear() ;
					 value+= " and a.E01A1=b.E01A1 ";
					 for(int i=0;i<condItemK01a1List.size();i++)
					 {
						 FieldItem item=(FieldItem)condItemK01a1List.get(i);
						 if(item.getItemid().equalsIgnoreCase(str2))
							 continue;
						 if("D".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"))+"="+Sql_switcher.isnull("b."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"));  
						 else  if("A".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),"''")+"="+Sql_switcher.isnull("b."+item.getItemid(),"''");  
						 else 
							 value+=" and a."+item.getItemid()+"=b."+item.getItemid();
					 } 
					 if(cond_str.length()>0)//有条件，即第三个参数
					 {
						 if(isOtherField)
						 {
							 if(str1.indexOf("(")!=-1)
							 {
								 
								int i=str1.lastIndexOf("(");
								String str_1=str1.substring(0,i);
								String str_2=str1.substring(i+1);
								String _str1=str_1+"("+TempTableName+"."+str_2;
								value+=" and b.E01A1="+_str1+cond_str;
							 }
							 else
								 value+=" and b.E01A1="+TempTableName+"."+str1+" "+cond_str;
						 }
						 else
						 {
							 value+=cond_str;
						 }
					 }
					 value+=" )  ) "+userSet;
				 } else {

					 value = "(select * from " + userSet
					 		+ " a where a.I9999=(select max(b.I9999) from " + userSet+" b";
					 
					 if(cond_str.length()>0&&isOtherField)
						 value+=","+TempTableName;
					 value+= "  where " + "a.E01A1=b.E01A1";
					
					 for(int i=0;i<condItemK01a1List.size();i++)
					 {
						 FieldItem item=(FieldItem)condItemK01a1List.get(i);
						 if(item.getItemid().equalsIgnoreCase(str2))
							 continue;
						 if("D".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"))+"="+Sql_switcher.isnull("b."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"));  
						 else  if("A".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),"''")+"="+Sql_switcher.isnull("b."+item.getItemid(),"''");  
						 else
							 value+=" and a."+item.getItemid()+"=b."+item.getItemid();
					 }
					 if(cond_str.length()>0)
					 {
						 if(isOtherField)
						 {
							 if(str1.indexOf("(")!=-1)
							 {
								 int i=str1.lastIndexOf("(");
								 String str_1=str1.substring(0,i);
								 String str_2=str1.substring(i+1);
								 String _str1=str_1+"("+TempTableName+"."+str_2;
								 value+=" and b.E01A1="+_str1+cond_str;
							 }
							 else
								 value+=" and b.E01A1="+TempTableName+"."+str1+" "+cond_str;
						 }
						 else
							 value+=cond_str;
					 
					 }
					 value+=" ))" + userSet;
				 }
			}
			else
			{
				value = userSet;
			}
			SQL.setLength(0);
			if(str1.indexOf("(")!=-1)
			{
				int i=str1.lastIndexOf("(");
				String str_1=str1.substring(0,i);
				String str_2=str1.substring(i+1);
				str1=str_1+"("+TempTableName+"."+str_2;
				SQL.append(str + "(select "+str2+" from "+value+" where "+userSet+".E01A1="+str1+cond_str+")");
			}
			else
			{
				SQL.append(str + "(select "+str2+" from "+value+" where "+userSet+".E01A1="+TempTableName+"."+str1+cond_str+")");
			}
			if (!Get_Token())
				return false;
			condItemK01a1List=new ArrayList();
			condItemK01a1List2=new ArrayList();
		}
		else
		{
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			if (!Get_Token())
				return false;
			
			SQL.setLength(0);
			SQL.append(str + str1);
		}
		
		if("A".equalsIgnoreCase(resultField.getItemtype())|| "M".equalsIgnoreCase(resultField.getItemtype()))
		{ 
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		}
		else if("N".equalsIgnoreCase(resultField.getItemtype()))
		{
			
			if(resultField.getDecimalwidth()>0)
			{
				retValue.setValue(Float.valueOf("0"));
				retValue.setValueType(FLOAT);
			}
			else
			{
				retValue.setValue(Float.valueOf("0"));
				retValue.setValueType(INT);
			}
		}
		else if("D".equalsIgnoreCase(resultField.getItemtype()))
		{
			//_value="'2006.06.28'"
			
			String	_value="1900.01.01";
			SQL_ADD(Sql_switcher.dateValue(_value));
			retValue.setValue(token);
			retValue.setValueType(DATEVALUE);
		} 
		this.isGetK01a1Param=false;
		return true;
	}
	
	
	
	
	
	
	/**
	 * 取部门值
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	
	private boolean Func_E0122VALUE(RetValue retValue) throws GeneralException,
			SQLException {
		String str = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		String str1 = SQL.toString();
		FieldItem resultField=(FieldItem)this.Field.clone();
		String cond_str="";
		if (tok == S_COMMA)
		{
			
			this.isGetB0110Param=true; //是否是取部门值参数
			if (!Get_Token())
				return false;
			if (token_type != FIELDITEM||this.Field.getFieldsetid().toUpperCase().trim().charAt(0)!='B') {
				Putback();
				SError(E_MUSTBEFIELDITEMUN);
				return false;
			}
			String str2=this.Field.getItemid();
			resultField=(FieldItem)this.Field.clone();
			if (!Get_Token())
				return false;
			
			if (tok == S_COMMA)
			{
				SQL.setLength(0);
				if (!Get_Token())
					return false;
				if (!level0(retValue))
					return false; 
				cond_str= SQL.toString();
				if(cond_str.trim().length()>0)
					cond_str=" and "+cond_str;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				
			}
			else if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			
			
			String value = "";
			String userSet=resultField.getFieldsetid();
			FieldSet vo = DataDictionary.getFieldSetVo(userSet);
			String z0 = userSet + "Z0";
			String z1 = userSet + "Z1";
			String monthOfz0 = Sql_switcher.month(z0);
			String yearOfz0 = Sql_switcher.year(z0);
			
			boolean isOtherField=false;
			 for(int i=0;i<condItemList2.size();i++)
			 {
				 FieldItem item=(FieldItem)condItemList2.get(i);
				 if(!item.getFieldsetid().equalsIgnoreCase(userSet))
					 isOtherField=true;
			 }
			 
			if(!"B01".equalsIgnoreCase(userSet))
			{
				 
				if (ymc != null && ("2".equals(vo.getChangeflag().trim())|| "1".equals(vo.getChangeflag().trim()))&&this.FSource.indexOf("截止日期")==-1) {
				 value = "(select * from " + userSet + " a where 1=1 ";
			//	 if(vo.getChangeflag().trim().equals("1"))
				 if("1".equals(vo.getChangeflag().trim())&&cond_str.length()==0)	 //20150914 为何年月变化子集要固定按业务日期来获得值，不通过条件定义？
					 value+= " and "+monthOfz0 + "="+ ymc.getMonth(); 
				 if(cond_str.length()==0)  //20150914 为何年月变化子集要固定按业务日期来获得值，不通过条件定义？
					 value+= " and " + yearOfz0 + "="+ ymc.getYear() ;
				 value+= " and  a.I9999=(select max(b.I9999) from " + userSet+ " b ";
				 
				 if(cond_str.length()>0&&isOtherField)
					 value+=","+TempTableName;
				 
				  
				  value+=" where 1=1 "; 
			//   if(vo.getChangeflag().trim().equals("1"))
				  if("1".equals(vo.getChangeflag().trim())&&cond_str.length()==0)	 //20150914 为何年月变化子集要固定按业务日期来获得值，不通过条件定义？
					  value+= " and " + monthOfz0 + "="+ ymc.getMonth() ;
				  if(cond_str.length()==0)  //20150914 为何年月变化子集要固定按业务日期来获得值，不通过条件定义？
					  value+= " and " + yearOfz0 + "="+ ymc.getYear() ;
				  value+= " and " + "a.b0110=b.b0110 ";
				 
				 
				 for(int i=0;i<condItemList.size();i++)
				 {
					 FieldItem item=(FieldItem)condItemList.get(i);
					 if(item.getItemid().equalsIgnoreCase(str2))
						 continue;
					 if("D".equalsIgnoreCase(item.getItemtype()))
						 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"))+"="+Sql_switcher.isnull("b."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"));  
					 else  if("A".equalsIgnoreCase(item.getItemtype()))
						 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),"''")+"="+Sql_switcher.isnull("b."+item.getItemid(),"''");  
					 else 
						 value+=" and a."+item.getItemid()+"=b."+item.getItemid();
				 } 
				 if(cond_str.length()>0)
				 {
					 if(isOtherField)
					 {
						 if(str1.indexOf("(")!=-1)
						 {
							 
							int i=str1.lastIndexOf("(");
							String str_1=str1.substring(0,i);
							String str_2=str1.substring(i+1);
							String _str1=str_1+"("+TempTableName+"."+str_2;
						//	 String _str1=str1.replaceAll("\\(","\\("+TempTableName+".");
							 value+=" and b.b0110="+_str1+cond_str;
						 }
						 else
							 value+=" and b.b0110="+TempTableName+"."+str1+" "+cond_str;
					 }
					 else
					 {
						 value+=cond_str;
					 }
				 }
				 value+=" )  ) "+userSet;
				 } else {
					 value = "(select * from " + userSet
					 		+ " a where a.I9999=(select max(b.I9999) from " + userSet+" b";
					 
					 if(cond_str.length()>0&&isOtherField)
						 value+=","+TempTableName;
					 value+= "  where " + "a.b0110=b.b0110 ";
					
					 for(int i=0;i<condItemList.size();i++)
					 {
						 FieldItem item=(FieldItem)condItemList.get(i);
						 if(item.getItemid().equalsIgnoreCase(str2))
							 continue;
						 if("D".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"))+"="+Sql_switcher.isnull("b."+item.getItemid(),Sql_switcher.charToDate("'1901-01-01'"));  
						 else  if("A".equalsIgnoreCase(item.getItemtype()))
							 value+=" and "+Sql_switcher.isnull("a."+item.getItemid(),"''")+"="+Sql_switcher.isnull("b."+item.getItemid(),"''");  
						 else
							 value+=" and a."+item.getItemid()+"=b."+item.getItemid();
					 }
					 if(cond_str.length()>0)
					 {
						 if(isOtherField)
						 {
							 if(str1.indexOf("(")!=-1)
							 {
								 int i=str1.lastIndexOf("(");
								 String str_1=str1.substring(0,i);
								 String str_2=str1.substring(i+1);
								 String _str1=str_1+"("+TempTableName+"."+str_2;
								 //String _str1=str1.replaceAll("\\(","\\("+TempTableName+".");
								 value+=" and b.b0110="+_str1+cond_str;
							 }
							 else
								 value+=" and b.b0110="+TempTableName+"."+str1+" "+cond_str;
						 }
						 else
							 value+=cond_str;
					 
					 }
					 value+=" ))" + userSet;
					 	
				 
				 
				 }
			}
			else
			{
				value =userSet;
			}
			SQL.setLength(0);
			if(str1.indexOf("(")!=-1)
			{
			 
					int i=str1.lastIndexOf("(");
					String str_1=str1.substring(0,i);
					String str_2=str1.substring(i+1);
					str1=str_1+"("+TempTableName+"."+str_2;
					//str1=str1.replaceAll("\\(","\\("+TempTableName+".");
					SQL.append(str + "(select "+str2+" from "+value+" where "+userSet+".b0110="+str1+cond_str+")");
				 
		 
			}
			else
			{
			 
					SQL.append(str + "(select "+str2+" from "+value+" where "+userSet+".b0110="+TempTableName+"."+str1+cond_str+")");
			 
			}
			if (!Get_Token())
				return false;
			
			condItemList2=new ArrayList();
			condItemList=new ArrayList();
		}
		else
		{
			if (InfoGroupFlag == forPerson)
				SQLS.add("update " + this.TempTableName
						+ " set b0110=e0122 where e0122 is not null ");
			
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			if (!Get_Token())
				return false;
			
			SQL.setLength(0);
			SQL.append(str + str1);
		}
		
		
		if("A".equalsIgnoreCase(resultField.getItemtype())|| "M".equalsIgnoreCase(resultField.getItemtype()))
		{ 
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		}
		else if("N".equalsIgnoreCase(resultField.getItemtype()))
		{
			
			if(resultField.getDecimalwidth()>0)
			{
				retValue.setValue(Float.valueOf("0"));
				retValue.setValueType(FLOAT);
			}
			else
			{
				retValue.setValue(Float.valueOf("0"));
				retValue.setValueType(INT);
			}
		}
		else if("D".equalsIgnoreCase(resultField.getItemtype()))
		{
			//_value="'2006.06.28'"
			
		//	String	_value="1900.01.01";
		//	SQL_ADD(Sql_switcher.dateValue(_value));
			retValue.setValue(token);
			retValue.setValueType(DATEVALUE);
		} 
		
		
		this.isGetB0110Param=false;
		return true;
	}

	/**
	 * 业务单位
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 * 业务模块标识           
	 */
	private boolean Func_BusiUnit(RetValue retValue) throws GeneralException,
			SQLException {
		String sM = null;
		CurFuncNum = BUSIUNIT;
		String strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 标识 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level3(retValue))
			return false;
		sM = SQL.toString().trim();
		if (!retValue.isIntType()) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}

		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;

		SQL.setLength(0);
		SQL.append(strSQL);

		String unit_ids = "";
		if (this.userView != null)
			unit_ids = this.userView.getUnitIdByBusi(sM);
		if (unit_ids != null && unit_ids.trim().length() > 0) {
			String[] temps = unit_ids.split("`");
			StringBuffer un = new StringBuffer("");
			for (int i = 0; i < temps.length; i++) {
				if (temps[i].trim().length() > 0) {
					String temp = temps[i];
					String pre = temp.substring(0, 2);
					String value = temp.substring(2);
					// if(pre.equalsIgnoreCase("UN"))
					{
						un.append(",'" + value + "'");
					}

				}
			}
			if (un.length() > 0) {
				SQL = SQL.append(" (" + un.substring(1) + ")");
			} else
				SQL = SQL.append(" ('##') ");

		} else
			SQL = SQL.append(" ('##') ");
		
		retValue.setValueType(STRVALUE);
		CurFuncNum = 0;
		return true;
	}	
	
	
	/**
	 * 本单位 兼容 业务单位函数 本单位(1)
	 * 
	 * @param  1:工资发放  2:工资总额  3:所得税
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_ThisUnit2(RetValue retValue) throws GeneralException,
			SQLException {
		
		if (!Get_Token())
			return false;
		// 处理左括号
		boolean isThisUnit=false;
		if (tok == S_LPARENTHESIS) {
			String sM = null;
			if (!Get_Token())
				return false;
			if (tok == S_RPARENTHESIS) {
				isThisUnit=true;
			}
			else
			{
				if (token_type != INT) {
					Putback();
					SError(E_MUSTBEINTEGER);
					return false;
				}
				sM=this.token;
				if (!Get_Token())
					return false;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				if (!Get_Token())
					return false;
				
				String unit_ids = "";
				if (this.userView != null)
					unit_ids = this.userView.getUnitIdByBusi(sM);
				if (unit_ids != null && unit_ids.trim().length() > 0) {
					String[] temps = unit_ids.split("`");
					StringBuffer un = new StringBuffer("");
					for (int i = 0; i < temps.length; i++) {
						if (temps[i].trim().length() > 0) {
							String temp = temps[i];
							String pre = temp.substring(0, 2);
							String value = temp.substring(2);
							// if(pre.equalsIgnoreCase("UN"))
								un.append(",'" + ("".equals(value)?" ":value) + "'");
						}
					}
					if (un.length() > 0) { 
						SQL = SQL.append(" (" + un.substring(1) + ")");
					} else
						SQL = SQL.append(" ('##') ");
				} else
					SQL = SQL.append(" ('##') ");
			}
		}
		else
			isThisUnit=true; //本单位
		
		if(isThisUnit)
		{
			String unit_ids = "";
			if (this.userView != null)
				unit_ids = this.userView.getUnit_id();
			if (unit_ids != null && unit_ids.trim().length() > 0&&!"UN".equalsIgnoreCase(unit_ids.trim())) {
				String[] temps = unit_ids.split("`");
				StringBuffer un = new StringBuffer("");
				for (int i = 0; i < temps.length; i++) {
					if (temps[i].trim().length() > 0) {
						String temp = temps[i];
						String pre = temp.substring(0, 2);
						String value = temp.substring(2);
						// if(pre.equalsIgnoreCase("UN"))
							un.append(",'" + ("".equals(value)?" ":value) + "'");
					}
				}
				if (un.length() > 0) {
					SQL = SQL.append(" (" + un.substring(1) + ")");
				} else
					SQL = SQL.append(" ('##') ");
	
			} else
				SQL = SQL.append(" ('##') ");
		}
		retValue.setValue("''");
		retValue.setValueType(STRVALUE);
		
		if(isThisUnit)
		{
			if (!Get_Token())
				return false;
		}
		return true;
	}
	
	
	
	
	
	/**
	 * 本单位
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_ThisUnit(RetValue retValue) throws GeneralException,
			SQLException {

		String unit_ids = "";
		if (this.userView != null)
			unit_ids = this.userView.getUnit_id();
		if (unit_ids != null && unit_ids.trim().length() > 0&&!"UN".equalsIgnoreCase(unit_ids.trim())) {
			String[] temps = unit_ids.split("`");
			StringBuffer un = new StringBuffer("");
			for (int i = 0; i < temps.length; i++) {
				if (temps[i].trim().length() > 0) {
					String temp = temps[i];
					String pre = temp.substring(0, 2);
					String value = temp.substring(2);
					// if(pre.equalsIgnoreCase("UN"))
					{
						un.append(",'" + value + "'");
					}

				}
			}
			if (un.length() > 0) {
				SQL = SQL.append(" (" + un.substring(1) + ")");
			} else
				SQL = SQL.append(" ('##') ");

		} else
			SQL = SQL.append(" ('##') ");

		retValue.setValue("''");
		retValue.setValueType(STRVALUE);

		if (!Get_Token())
			return false;

		return true;
	}

	/**
	 * 归属日期
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_SalaryA00Z0(RetValue retValue)
			throws GeneralException, SQLException {
		RowSet rowSet = null;
		String a00z0_fielditem = "";
		try {
			String strSQL = SQL.toString();
			SQL.setLength(0);
			// CurFuncNum=FUNCSALARYA00Z0;
			if (!Get_Token())
				return false;
			/*if (tok == S_LPARENTHESIS) {
				if (!Get_Token())
					return false;
				if (tok != S_RPARENTHESIS) {
					Putback();
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				if (!Get_Token())
					return false;
			}*/
			if (tok == S_LPARENTHESIS) {
				if (!Get_Token())
					return false;
				
				if (tok == S_RPARENTHESIS) {
					if (!Get_Token())
						return false;
				}else {
					if (!level0(retValue))
						return false;
					
					a00z0_fielditem = SQL.toString();
					
					if(!"'a00z0'".equalsIgnoreCase(a00z0_fielditem.trim()) && !"'a00z2'".equalsIgnoreCase(a00z0_fielditem.trim())) {
						Putback();
						SError(E_JUSTA00Z0ORA00Z2);
						return false;
					}
					if (!Get_Token())
						return false;
				}
			}
			
			SQL.setLength(0);
			String date = "";
			String where = "";
			String tablename = "";
			// 针对只有包含薪资的做处理
			if(TempTableName.toLowerCase().indexOf("salary") != -1) {
				where = this.whereText;
				tablename = this.TempTableName;
			}else if(TempTableName.toLowerCase().indexOf("salary") == -1 && StringUtils.isNotBlank(this.StdTmpTable) && 
					this.StdTmpTable.toLowerCase().indexOf("salary") != -1) {
				where = this.StdTmpTable_where;
				tablename = this.StdTmpTable;
			}else {
				a00z0_fielditem = "";
			}
			
			date = getDate(a00z0_fielditem, where, tablename);
			if(StringUtils.isNotBlank(date)) {
				date = datePad(date);
			}
			boolean isSalary = false;
			// 如果是其他情况还是按照原先逻辑
			if(StringUtils.isBlank(date)) {
				if (this.ymc == null) {
					Date sysdate = new Date();
					this.ymc = new YearMonthCount(DateUtils.getYear(sysdate), DateUtils
							.getMonth(sysdate), DateUtils.getDay(sysdate), 1);
				}
				date = datePad(this.ymc.getDate());
				if (TempTableName != null && TempTableName.length() > 5
					&& (TempTableName.toLowerCase().indexOf("salary") != -1||TempTableName.toLowerCase().endsWith("_gz") || TempTableName.toLowerCase().endsWith("gzsp"))) {
					SQL.setLength(0);
					SQL.append("a00z0");
					isSalary = true;
				}
			}
			if(!isSalary) {
				SQL.append(Sql_switcher.dateValue(date));
			}
			strSQL += SQL.toString();
			SQL.setLength(0);
			SQL.append(strSQL);
	
			retValue.setValue(datePad(date));
			retValue.setValueType(DATEVALUE);
			// CurFuncNum=0;
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return true;
	}
	
	private String getDate(String a00z0_fielditem, String strcond, String gz_tablename) {
		RowSet rowSet = null;
		ContentDAO dao=new ContentDAO(this.con);
		String date = "";
		try {
			if(StringUtils.isNotBlank(a00z0_fielditem)) {
				StringBuffer sf = new StringBuffer();
				sf.append("select " + Sql_switcher.dateToChar(a00z0_fielditem.replace("'", "")) + " from "+gz_tablename+" where 1=1 ");
				if(StringUtils.isNotBlank(strcond.toString())) {
					sf.append("and " + strcond);
				}
				sf.append(" order by dbid, a0000, A00Z0, A00Z1 ");
				// oracle中select XX FROM XX WHERE ROWNUM<= 1 ORDER BY XXX时，会导致顺序不对，查全部的，然后只取第一个值
				rowSet=dao.search(sf.toString());
				if(rowSet.next()) {
					date = rowSet.getString(1);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rowSet);
		}
		return date;
	}
	
	/**
	 * 登录用户名
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 *             标识＝１，帐号 ＝２全称
	 */
	private boolean Func_LoginName(RetValue retValue) throws GeneralException,
			SQLException {
		String sM = null;
		CurFuncNum = FUNCLOGINNAME;
		String strSQL = SQL.toString();
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 标识 */
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		if (!level3(retValue))
			return false;
		sM = SQL.toString().trim();
		if (!retValue.isIntType()) {
			Putback();
			SError(E_MUSTBEINTEGER);
			return false;
		}
		int nM = Integer.parseInt(sM);
		if (!(nM == 1 || nM == 2 || nM == 3)) {
			Putback();
			SError(E_MUSTBEONETWO);
			return false;
		}

		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;

		SQL.setLength(0);
		SQL.append(strSQL);
		if (nM == 1) {
			SQL.append("'");
			SQL.append(this.userView.getUserName());
			SQL.append("'");
			retValue.setValue(this.userView.getUserName());
		} else if (nM == 2) {
			SQL.append("'");
			SQL.append(this.userView.getUserFullName());
			SQL.append("'");
			retValue.setValue(this.userView.getUserFullName());
		} else if (nM == 3) {
			String zzString = "";
			
			SQL.append("'");
			SQL.append(zzString);
			SQL.append("'");
			retValue.setValue(zzString);
		}

		retValue.setValueType(STRVALUE);
		CurFuncNum = 0;
		return true;
	}

	
	/**
	 * 报批人信息
	 * 
	 * @param retValue
	 * @param flag : UN  UM  @K
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 *            
	 */
	private boolean Func_AppealInfo(RetValue retValue,String flag) throws GeneralException,
			SQLException {
		String sM = null;
		String strSQL = SQL.toString(); 
		SQL.setLength(0);
		SQL.append(strSQL);
		if ("UN".equalsIgnoreCase(flag)) {
			SQL.append("'");
			SQL.append(this.userView.getUserOrgId());
			SQL.append("'");
			retValue.setValue(this.userView.getUserOrgId());
		} else if ("UM".equalsIgnoreCase(flag)) {
			SQL.append("'");
			SQL.append(this.userView.getUserDeptId());
			SQL.append("'");
			retValue.setValue(this.userView.getUserDeptId());
		}
		else if ("@K".equalsIgnoreCase(flag))
		{
			SQL.append("'");
			SQL.append(this.userView.getUserPosId());
			SQL.append("'");
			retValue.setValue(this.userView.getUserPosId());
		}

		retValue.setValueType(STRVALUE);
		if (!Get_Token())
			return false;
		return true;
	}
	
	/*
	 * function TYKSJParser.Func_SalaryA00Z0(var retValue: Variant): BOOL; var
	 * SystemTime: TSystemTime; begin result := FALSE;
	 * 
	 * if not Get_Token() then exit; if tok = S_LPARENTHESIS then begin if not
	 * Get_Token() then exit; if tok <> S_RPARENTHESIS then begin Putback();
	 * SError(E_LOSSRPARENTHESE); exit end; if not Get_Token() then exit; end;
	 * 
	 * try retValue := StrToDate(FSalaryA00Z0); except GetLocalTime(SystemTime);
	 * retValue := SystemTimeToDateTime(SystemTime); end;
	 * 
	 * case nNETWORK of 3:FSQL := FSQL +' TO_DATE('''+
	 * FormatDateTime('yyyy-mm-dd hh:mm:ss',retValue)+''',''yyyy-mm-dd
	 * HH24:MI:SS'')'; 2:FSQL := FSQL +' TO_DATE('''+
	 * FormatDateTime('yyyy.mm.dd',retValue)+''',''yyyy.mm.dd'')'; 1:FSQL :=
	 * FSQL +' '''+ FormatDateTime('yyyy.mm.dd',retValue)+''''; 0:FSQL := FSQL +'
	 * #'+ FormatDateTime('yyyy-mm-dd',retValue)+'#'; end; result := TRUE; end;
	 * 
	 */
	/**
	 * 
	 * @param retValue
	 * @param flag
	 *            =1 历史记录最初指标值（指标,逻辑表达式） =2 上一个历史记录指标值（指标,逻辑表达式）
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_HistoryMenu(RetValue retValue, int flag)
			throws GeneralException, SQLException {
		if (flag == 1)
			CurFuncNum = FUNCHISFIRSTMENU;
		else
			CurFuncNum = FUNCHISPRIORMENU;
		SQL.setLength(0);
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		if (!Get_Token())
			return false;
		if (!level6(retValue))
			return false;
		FieldItem item = this.Field;
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}

		if (!Get_Token())
			return false;
		SQL.setLength(0);
		RetValue retValue1 = new RetValue();
		if (!level0(retValue1))
			return false;
		if (!retValue1.isBooleanType()) {
			Putback();
			SError(E_MUSTBEBOOL);
			return false;
		}
		String strWhere = SQL.toString();

		SQL.setLength(0);
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);
			return false;
		}
		if (!Get_Token())
			return false;
		ArrayList tmplist = new ArrayList();
		SQL.setLength(0);
		SQL.append(SelectHisMenu(tmplist, item, strWhere, flag));
		SQLS.clear();
		for (int i = 0; i < tmplist.size(); i++)
			SQLS.add(tmplist.get(i));
		CurFuncNum = 0;
		return true;
	}

	/**
	 * 
	 * @param list
	 * @param item
	 * @param strwhere
	 * @param flag
	 *            =1最近第一条 =2最近的上一条
	 * @return
	 */
	private String SelectHisMenu(ArrayList list, FieldItem item,
			String strwhere, int flag) {

		String strvalue = "";
		FieldItem fldR = new FieldItem();
		fldR.setFieldsetid(TempTableName);
		fldR.setItemtype(item.getItemtype());
		fldR.setCodesetid(item.getCodesetid());
		fldR.setItemlength(item.getItemlength());
		fldR.setDecimalwidth(item.getDecimalwidth());
		fldR.setItemid("SELECT_1");
		strvalue = fldR.getItemid();

		String sOldTmpTb = TempTableName;
		String sMidTb = "T_" + TempTableName;
		String sSubTb = "TS_" + TempTableName;
		if (DBType == Constant.MSSQL && isTempTable) {
			sOldTmpTb = TempTableName;
			sMidTb = "##T_" + TempTableName;
			sSubTb = "##TS_" + TempTableName;
		}

		StringBuffer sflds = new StringBuffer();
		sflds.append("A0100,I9999");
		String sfrom = DbPre + item.getFieldsetid();

		Iterator it = mapUsedFieldItems.keySet().iterator();
		ArrayList fieldlist = new ArrayList();
		while (it.hasNext()) {
			FieldItem fld = (FieldItem) mapUsedFieldItems.get(it.next());

			if (item.getFieldsetid().equalsIgnoreCase(fld.getFieldsetid())) {
				sflds.append(",");
				sflds.append(fld.getItemid());
				fieldlist.add(fld.getItemid());
				// mapUsedFieldItems.remove(it.next());
			}
		}
		for (int i = 0; i < fieldlist.size(); i++)
			mapUsedFieldItems.remove(fieldlist.get(i));

		// fieldlist.clear();
		for (int i = UsedSets.size() - 1; i >= 0; i--) {
			if (item.getFieldsetid().equalsIgnoreCase((String) UsedSets.get(i))) {
				// fieldlist.add(item.getFieldsetid());
				UsedSets.remove(i);
			}
		}// for i loop end.

		/** 构建和取值指标同一子集的指标列表 */
		StringBuffer strsql = new StringBuffer();
		if (DBType == Constant.MSSQL)
			list.add(" drop table " + sMidTb);
		else {
		//	if (this.isTempTable)
		//		list.add(" truncate table " + sMidTb);
			list.add(" drop table " + sMidTb+" purge");
		}
		switch (DBType) {
		case Constant.MSSQL:
			strsql.append("select ");
			strsql.append(sflds.toString());
			strsql.append(" into ");
			strsql.append(sMidTb);
			strsql.append(" from ");
			strsql.append(sfrom);
			strsql.append(" where a0100 in (select a0100 from ");
			strsql.append(TempTableName);
			strsql.append(")");
			list.add(strsql.toString());
			
			list.add("create index "+sMidTb+"_index  on "+sMidTb+" (a0100,i9999)");
 
			
			break;
		case Constant.ORACEL:
			strsql.append(" create ");
			if (this.isTempTable)
				strsql.append(" GLOBAL TEMPORARY ");
			strsql.append(" TABLE  ");
			strsql.append(sMidTb);
			if (this.isTempTable)
				strsql.append(" On Commit Preserve Rows ");
			strsql.append(" as select ");
			strsql.append(sflds);
			strsql.append(" from ");
			strsql.append(sfrom);
			strsql.append(" where a0100 in (select a0100 from ");
			strsql.append(TempTableName);
			strsql.append(")");
			list.add(strsql.toString());
			
			list.add("create index "+sMidTb+"_index  on "+sMidTb+" (a0100,i9999)");
			 
			break;
		case Constant.DB2:
			strsql.append("select ");
			strsql.append(sflds);
			strsql.append(" from ");
			strsql.append(sfrom);
			strsql.append(" where a0100 in (select a0100 from ");
			strsql.append(TempTableName);
			strsql.append(")");
			list.add(" create table " + sMidTb + " as (" + strsql.toString()
					+ ") definition only");
			list.add("insert into " + sMidTb + strsql.toString());
			
			list.add("create index "+sMidTb+"_index  on "+sMidTb+" (a0100,i9999)");
			 
			break;
		}
		/** 把不同子集的指标加进临时表 */
		it = mapUsedFieldItems.keySet().iterator();
		while (it.hasNext()) {
			FieldItem fld = (FieldItem) mapUsedFieldItems.get(it.next());
			String type = fld.getItemtype();
			strsql.setLength(0);
			if (!item.getFieldsetid().equalsIgnoreCase(fld.getFieldsetid())) {
				String sAdd = fld.getItemid()
						+ " "
						+ Sql_switcher.getFieldType(type.charAt(0), fld
								.getItemlength(), fld.getDecimalwidth());
				switch (DBType) {
				case Constant.MSSQL:
				case Constant.ORACEL:
					strsql.append("alter table ");
					strsql.append(sMidTb);
					strsql.append(" add ");
					strsql.append(sAdd);
					break;
				case Constant.DB2:
					strsql.append("alter table ");
					strsql.append(sMidTb);
					strsql.append(" add column ");
					strsql.append(sAdd);
					break;
				}
				list.add(strsql.toString());
			}
		}
		TempTableName = sMidTb;
		/** 标准表临时 */
		/***/
		SQL_SubSet();
		TempTableName = sOldTmpTb;
		for (int i = 0; i < SQLS.size(); i++)
			list.add(SQLS.get(i));
		sflds.setLength(0);

		sflds
				.append("A0100,Max(I9999) as MaxI9,Min(I9999) as MinI9,0 as flag ");
		if (DBType == Constant.ORACEL)
			list.add(" drop table " + sSubTb+" purge");
		else
			list.add(" drop table " + sSubTb);
		createTmpTb(sflds.toString(), sSubTb, sMidTb + " where " + strwhere
				+ " group by A0100", list);
		
		list.add("create index "+sSubTb+"_index  on "+sSubTb+" (a0100,MaxI9,MinI9)");
		 
		strsql.setLength(0);
		switch (DBType) {
		case Constant.MSSQL:
		case Constant.ORACEL:
			strsql.append("alter table ");
			strsql.append(sSubTb);
			strsql.append(" add MidI9 Integer ");
			break;
		case Constant.DB2:
			strsql.append("alter table ");
			strsql.append(sSubTb);
			strsql.append(" add column MidI9 Int ");
			break;
		}
		list.add(strsql.toString());

		strsql.setLength(0);
		if (DBType == Constant.MSSQL) {
			if (isTempTable) {
				list.add("drop table ##T" + sSubTb);
				createTmpTb(
						"A.A0100,Max(I9999) as MidI9 ",
						"##T" + sSubTb,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and B.I9999>A.MinI9 and B.I9999<A.MaxI9 and not("
								+ strwhere + " ) group by A.A0100", list);
				list.add("create index T"+sSubTb+"_index  on ##T"+sSubTb+" (a0100,MidI9)");
			} else {
				list.add("drop table T" + sSubTb);
				createTmpTb(
						"A.A0100,Max(I9999) as MidI9 ",
						"T" + sSubTb,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and B.I9999>A.MinI9 and B.I9999<A.MaxI9 and not("
								+ strwhere + " ) group by A.A0100", list);
				list.add("create index T"+sSubTb+"_index  on T"+sSubTb+" (a0100,MidI9)");
				 
			}
		} else {
		//	if (this.isTempTable)
		//		list.add("truncate table T" + sSubTb);
			list.add("drop table T" + sSubTb+" purge");
			createTmpTb(
					"A.A0100,Max(I9999) as MidI9 ",
					"T" + sSubTb,
					sSubTb
							+ " A,"
							+ sMidTb
							+ " B where A.A0100=B.A0100 and B.I9999>A.MinI9 and B.I9999<A.MaxI9 and not("
							+ strwhere + " ) group by A.A0100", list);
			list.add("create index T"+sSubTb+"_index  on T"+sSubTb+" (a0100,MidI9)");
			 
		}
		
		

		/** 将MidI置为范围不符条件的最大的I9999 */
		strsql.setLength(0);
		switch (DBType) {
		case Constant.MSSQL:
			strsql.append("update ");
			strsql.append(" A ");
			strsql.append(" set A.MidI9=B.MidI9 from ");
			strsql.append(sSubTb);
			if (this.isTempTable)
				strsql.append(" A ,##T");
			else
				strsql.append(" A,T");
			strsql.append(sSubTb);
			strsql.append(" B where A.A0100=B.A0100");
			break;
		case Constant.DB2:
		case Constant.ORACEL:
			strsql.append("update ");
			strsql.append(sSubTb);
			strsql.append(" set MidI9=(select B.MidI9 from T");
			strsql.append(sSubTb);
			strsql.append(" B where ");
			strsql.append(sSubTb);
			strsql.append(".A0100=B.A0100");
			break;
		}
		list.add(strsql.toString());
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				list.add("drop table ##T" + sSubTb);
			else
				list.add("drop table T" + sSubTb);
		} else {
		//	if (this.isTempTable)
		//		list.add("truncate table T" + sSubTb);
			list.add("drop table T" + sSubTb+" purge");
		}
		if (flag == 1)// 最近第一条
		{
			strsql.setLength(0);
			strsql.append("update ");
			strsql.append(sSubTb);
			strsql.append(" set MidI9=MidI9+1 where not MidI9 is null");
			list.add(strsql.toString());

			strsql.setLength(0);
			strsql.append("update ");
			strsql.append(sSubTb);
			strsql
					.append(" set MidI9=MinI9 where (maxI9=MinI9) or (midI9 is null)");
			list.add(strsql.toString());

			if (DBType == Constant.MSSQL) {
				String a_table = "T" + sSubTb;
				if (this.isTempTable)
					a_table = "##T" + sSubTb;
				createTmpTb(
						"A.A0100,Min(I9999) as MidI9 ",
						a_table,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and B.I9999>=A.MidI9 and B.I9999<=A.MaxI9 group by A.A0100",
						list);
				list.add("create index T"+sSubTb+"_index  on ##T"+sSubTb+" (a0100,MidI9)");
				 
			} else{
				createTmpTb(
						"A.A0100,Min(I9999) as MidI9 ",
						"T" + sSubTb,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and B.I9999>=A.MidI9 and B.I9999<=A.MaxI9 group by A.A0100",
						list);
			list.add("create index T"+sSubTb+"_index  on T"+sSubTb+" (a0100,MidI9)");
		 }
			list.add("delete from " + TempTableName);

			strsql.setLength(0);
			strsql.append("insert into ");
			strsql.append(TempTableName);
			strsql.append("(A0100," + fldR.getItemid() + ")");
			if (DBType == Constant.MSSQL) {
				if (this.isTempTable)
					strsql.append(" select A.A0100," + item.getItemid()
							+ " from ##T" + sSubTb);
				else
					strsql.append(" select A.A0100," + item.getItemid()
							+ " from T" + sSubTb);
			} else
				strsql.append(" select A.A0100," + item.getItemid() + " from T"
						+ sSubTb);
			strsql.append(" A," + sMidTb);
			strsql.append(" B where A.A0100=B.A0100 and B.I9999=A.MidI9 ");
			list.add(strsql.toString());

			if (DBType == Constant.MSSQL) {
				if (this.isTempTable)
					list.add("drop table ##T" + sSubTb);
				else
					list.add("drop table T" + sSubTb);
				list.add("drop table " + sSubTb);
				list.add("drop table " + sMidTb);
			} else {
			/*	if (this.isTempTable) {
					list.add("truncate table T" + sSubTb);
					list.add("truncate table " + sSubTb);
					list.add("truncate table " + sMidTb);
				}*/
				list.add("drop table T" + sSubTb+" purge");
				list.add("drop table " + sSubTb+" purge");
				list.add("drop table " + sMidTb+" purge");
			}
		} else {
			strsql.setLength(0);
			strsql.append("update ");
			strsql.append(sSubTb);
			strsql
					.append(" set MidI9=MinI9-1 where (maxI9=minI9) or (midI9 is null)");
			list.add(strsql.toString());

			if (DBType == Constant.MSSQL) {
				String a_table = "T" + sSubTb;
				if (this.isTempTable)
					a_table = "##T" + sSubTb;
				createTmpTb(
						"A.A0100,Max(I9999) as MidI9",
						a_table,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and  B.I9999<=A.MidI9 group by A.A0100",
						list);
				list.add("create index "+a_table+"_index  on  "+a_table+" (a0100,MidI9)");
				 
			} 
			else
			{
				createTmpTb(
						"A.A0100,Max(I9999) as MidI9",
						"T" + sSubTb,
						sSubTb
								+ " A,"
								+ sMidTb
								+ " B where A.A0100=B.A0100 and  B.I9999<=A.MidI9 group by A.A0100",
						list);
				list.add("create index T"+sSubTb+"_index  on T"+sSubTb+" (a0100,MidI9)");
				 
			}
			list.add("delete from " + TempTableName);

			strsql.setLength(0);
			strsql.append("insert into ");
			strsql.append(TempTableName);
			strsql.append("(A0100," + fldR.getItemid() + ")");
			if (DBType == Constant.MSSQL) {
				if (this.isTempTable)
					strsql.append(" select A.A0100," + item.getItemid()
							+ " from ##T" + sSubTb);
				else
					strsql.append(" select A.A0100," + item.getItemid()
							+ " from T" + sSubTb);
			} else
				strsql.append(" select A.A0100," + item.getItemid() + " from T"
						+ sSubTb);
			strsql.append(" A," + sMidTb);
			strsql.append(" B where A.A0100=B.A0100 and B.I9999=A.MidI9 ");
			list.add(strsql.toString());
			if (DBType == Constant.MSSQL) {
				if (this.isTempTable)
					list.add("drop table ##T" + sSubTb);
				else
					list.add("drop table T" + sSubTb);
				list.add("drop table " + sSubTb);
				list.add("drop table " + sMidTb);
			} else {
			/*	if (this.isTempTable) {
					list.add("truncate table T" + sSubTb);
					list.add("truncate table " + sSubTb);
					list.add("truncate table " + sMidTb);
				}*/
				list.add("drop table T" + sSubTb+" purge");
				list.add("drop table " + sSubTb+" purge");
				list.add("drop table " + sMidTb+" purge");
			}
		}
		SQLS.clear();
		UsedSets.clear();
		mapUsedFieldItems.clear();
		mapUsedFieldItems.put(fldR.getItemid(), fldR);

		return strvalue;
	}

	private void createTmpTb(String sSel, String sInto, String sAFrom,
			ArrayList list) {
		StringBuffer strsql = new StringBuffer();
		switch (DBType) {
		case Constant.MSSQL:
			strsql.append(" select ");
			strsql.append(sSel);
			strsql.append(" into ");
			strsql.append(sInto);
			strsql.append(" from ");
			strsql.append(sAFrom);
			list.add(strsql.toString());
			break;
		case Constant.ORACEL:
			strsql.append(" create ");
			if (this.isTempTable)
				strsql.append(" GLOBAL TEMPORARY ");
			strsql.append(" TABLE   ");
			strsql.append(sInto);
			if (this.isTempTable)
				strsql.append(" On Commit Preserve Rows ");
			strsql.append(" as select ");
			strsql.append(sSel);
			strsql.append(" from ");
			strsql.append(sAFrom);
			list.add(strsql.toString());
			break;
		case Constant.DB2:
			strsql.append("select ");
			strsql.append(sSel);
			strsql.append(" from ");
			strsql.append(sAFrom);
			list.add(" create table " + sInto + " as (" + strsql.toString()
					+ ") definition only ");
			list.add("insert into " + sInto + strsql.toString());
			break;
		}
	}

	/**
	 * 按职位统计人数 函数 ?
	 * 
	 * @param retValue
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	private boolean Func_STATP(RetValue retValue) throws GeneralException,
			SQLException {
		return true;
	}

	/**
	 * 取
	 * 
	 * @param strSet
	 * @return
	 */
	private String getKeyField(String strSet) {
		if (strSet.startsWith("A"))
			return "A0100";
		if (strSet.startsWith("K"))
			return "E01A1";
		if (strSet.startsWith("B"))
			return "B0110";
		return null;
	}
	

	/**
	 * 取专项附加值
	 * @param strSet
	 * @return
	 * @throws GeneralException 
	 */
	private boolean Func_SpecialAddAmount(RetValue retValue) throws GeneralException {
		StringBuffer sql = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.con);
		CallableStatement cstmt = null; // 存储过程
		RowSet rowSet=null;
		String salaryid = "";
		String declare_type = "";
		String range = "";
		
		String str = SQL.toString();
		if (!Get_Token())
			return false;
		// 处理左括号
		if (tok != S_LPARENTHESIS) {
			Putback();
			SError(E_LOSSLPARENTHESE);
			return false;
		}
		/** 指标 */
		if (!Get_Token())
			return false;
		declare_type = getDeclareType(tok);
		if(StringUtils.isBlank(declare_type)) {
			Putback();
			SError(E_UNKNOWNSTR);
			return false;
		}
		if (!Get_Token())
			return false;
		
		//没有第二个参数不对
		if (tok != S_COMMA) {
			Putback();
			SError(E_LOSSCOMMA);
			return false;
		}
		
		if (!Get_Token())
			return false;
		if (!"当月".equalsIgnoreCase(token) && !"累计".equalsIgnoreCase(token)) {
			Putback();
			setStrError("此处参数只能是当月或者累计");	
			return false;
		}
		range = getDeclareType(tok);
		
		if (!Get_Token())
			return false;
		
		if (tok != S_RPARENTHESIS) {
			Putback();
			SError(E_LOSSRPARENTHESE);//缺少右括号
			return false;
		}
		
		if (!Get_Token())
			return false;
		
		try {
			//判断是否存在该存储过程
	        if (DbWizard.dbflag == Constant.ORACEL) {
	            sql.append("select status from user_objects where object_type = 'PROCEDURE' and upper(object_name)='GETSPECIALDECLARATIONMONEY'");
	        } else {
	            sql.append("select * from dbo.sysobjects where id = object_id(N'[dbo].[GetSpecialDeclarationMoney]') ");
	            sql.append("and OBJECTPROPERTY(id, N'IsProcedure') = 1");
	        }
	        rowSet = dao.search(sql.toString());
	        boolean flag = rowSet.next();
	        if (!flag) {//没有存储过程
	        	Putback();
				SError(E_UPDATETRANSFERLIBRARY);//取专项附加额函数需要转库大师需升级至7.22版本以上！
				return false;
	        }
	        if(isVerify)//如果是校验审核，不执行存储过程
				return true;
	        if (flag && zxfj_propertyMap.size() > 0) {
	        	cstmt = this.con.prepareCall("{call GetSpecialDeclarationMoney(?,?,?,?,?,?,?,?)}");
	            cstmt.setString(1, declare_type);//所有专项的合计（00）、不含大病、继续教育其它专项的合计（10）、子女教育（01）、继续教育（02）、住房租金（03）、房贷利息（04）、大型医疗（05）、赡养老人（06）
                cstmt.setString(2, range);//取值范围 （基于薪资表的A00Z0字段判断月份）  current:当月 cumulative:按月份累计专项额（大病医疗、继续教育专项在12月份汇算清缴时统一扣除）
                cstmt.setString(3, (String)zxfj_propertyMap.get("zxfj_gz_tab"));//薪资表名称 salaryhistory 用户名_salary_薪资账套ID   
                cstmt.setString(4, (String)zxfj_propertyMap.get("zxfj_sql_filter"));//薪资表待计算的人员范围 例：and (salaryid=12 and sp_flag=’03’)
                cstmt.setString(5, (String)zxfj_propertyMap.get("zxfj_target_item"));//薪资表待写入专项额的目标指标
                cstmt.setString(6, (String)zxfj_propertyMap.get("zxfj_tax_date_item"));//计税时间指标
                cstmt.setString(7, (String)zxfj_propertyMap.get("zxfj_id"));//薪资表id  
                cstmt.setString(8, this.userView.getUserName());//username
	            //执行
	            cstmt.execute();
	            
	            SQL.setLength(0);
	            SQL.append(str + (String)zxfj_propertyMap.get("zxfj_target_item"));//将目标指标返回到sql
	        }
	       
		}catch(Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rowSet);
			PubFunc.closeDbObj(cstmt);
		}
		return true;
	}
	
	private String getDeclareType(int type) {
		String str = "";
		switch(type) {
		case ALLTAX: //所有专项的合计
			str = "00";
			break;
		case NOTSICK_EDU_SUM: //不含大病、继续教育其它专项的合计
			str = "10";
			break;
		case CHILD_EDU: //子女教育
			str = "01";
			break;
		case CON_EDU: //继续教育
			str = "02";
			break;
		case HOUSING_RENT: //住房租金
			str = "03";
			break;
		case MORTGAGE_RATE: //房贷利息
			str = "04";
			break;
		case LARGE_SCALE: //大型医疗
			str = "05";
			break;
		case SUPPORT_OLDER: //赡养老人
			str = "06";
			break;
		case CURRENTMONTH: //当月
			str = "current";
			break;
		case CUMULATIVE: //按月份累计专项额
			str = "cumulative";
			break;
		}
		return str;
	}
	
	private String getDbPreTable(String strSet) {
		String value = "";
		if (strSet.startsWith("A") || strSet.startsWith("a")) {
			value = DbPre + strSet;
		} else {
			value = strSet;
		}
		return value;
	}

	private void createTempTable() {
		DbWizard dbWizard = new DbWizard(this.con);
		Table table = null;
		if (DBType == Constant.MSSQL
				&& "##".equals(TempTableName.substring(0, 2))) {
			table = new Table(TempTableName.substring(2), TempTableName
					.substring(2));
		} else
			table = new Table(TempTableName, TempTableName);
		if (this.isTempTable)
			table.setBTemporary(true);

		Collection collection = mapUsedFieldItems.values();
		Iterator it = collection.iterator();
		StringBuffer strBuffer = new StringBuffer();
		boolean flg = false;
		boolean isExistField=false;
		while (it.hasNext()) {
			FieldItem fieldItem1 = (FieldItem) it.next();
			String itemid = fieldItem1.getItemid();
			if(itemid.equalsIgnoreCase(targetField))
				isExistField=true;
			if (InfoGroupFlag == forPerson) {
				if ("A0100".equalsIgnoreCase(itemid)
						|| "B0110".equalsIgnoreCase(itemid)
						|| "E0122".equalsIgnoreCase(itemid)
						|| "E01A1".equalsIgnoreCase(itemid))  //20100330
					continue;
			}

			if (InfoGroupFlag == forUnit) {
				if ("B0110".equalsIgnoreCase(itemid)
						|| "E0122".equalsIgnoreCase(itemid))
					continue;
			}
			if (InfoGroupFlag == forPosition) {
				if ("B0110".equalsIgnoreCase(itemid)
						|| "E0122".equalsIgnoreCase(itemid)
						|| "E01A1".equalsIgnoreCase(itemid))
					continue;
			}

			// if (!fieldItem1.getItemid().equalsIgnoreCase("B0110")&&
			// !fieldItem1.getItemid().equalsIgnoreCase("E0122")&&
			// !fieldItem1.getItemid().equalsIgnoreCase("E01A1"))
			{

				Field a_field = fieldItem1.cloneField();
				/** 当类型为整型或float型,长度设为50 建表时会报错 dengcan 07/12/03 */
				// if(a_field.getDatatype()!=4&&a_field.getDatatype()!=6)
				// a_field.setLength(50);
				table.addField(a_field);
				// fieldItem1.setItemlength(50);
				// table.addField(fieldItem1.cloneField());
				if (flg == false) {
					strBuffer.append(getDbPreTable(fieldItem1.getFieldsetid())
							+ "." + fieldItem1.getItemid());
					flg = true;
				} else {
					strBuffer.append(","
							+ getDbPreTable(fieldItem1.getFieldsetid()) + "."
							+ fieldItem1.getItemid());
				}
			}
		}
		FieldItem fieldItem = null;
		if (targetField != null && targetField.length() > 0&&!isExistField) {

			fieldItem = new FieldItem(targetField, targetField);
			fieldItem.setItemtype(targetFieldDataType);
			fieldItem.setItemlength(targetFieldLen);
			fieldItem.setDecimalwidth(targetFieldDecimal);
			table.addField(fieldItem.cloneField());
		}

		fieldItem = new FieldItem("I9999", "I9999");
		fieldItem.setItemlength(10);
		fieldItem.setItemtype("N");
		table.addField(fieldItem.cloneField());
		if (InfoGroupFlag == forPerson) {
			fieldItem = new FieldItem("A0100", "A0100");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(8);
			fieldItem.setKeyable(true);
			fieldItem.setNullable(false);
			table.addField(fieldItem.cloneField());

			fieldItem = new FieldItem("B0110", "B0110");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());

			fieldItem = new FieldItem("E0122", "E0122");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
			
			fieldItem = new FieldItem("E01A1", "E01A1");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());

		} else if (InfoGroupFlag == forUnit) {
			fieldItem = new FieldItem("B0110", "B0110");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
			fieldItem = new FieldItem("E0122", "E0122");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
		} else if (InfoGroupFlag == forPosition) {
			fieldItem = new FieldItem("B0110", "B0110");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
			fieldItem = new FieldItem("E01A1", "E01A1");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
			fieldItem = new FieldItem("E0122", "E0122");
			fieldItem.setItemtype("A");
			fieldItem.setItemlength(50);
			table.addField(fieldItem.cloneField());
		}
		try {
	//	 System.out.println(dbWizard.isExistTable(table));
		//	if (dbWizard.isExistTable(table.getName(), false)) 
			{
				dbWizard.dropTable(table);
			}
			dbWizard.createTable(table);
			TempTableName = table.getName();
			
			
			
			if (InfoGroupFlag == forPerson) {
				dbWizard.execute("create index "+TempTableName+"_index  on  "+TempTableName+" (A0100,I9999)");
			}
			else
			{
				if (InfoGroupFlag == forUnit)
					dbWizard.execute("create index "+TempTableName+"_index  on  "+TempTableName+" (B0110,E0122,I9999)");
				else
					dbWizard.execute("create index "+TempTableName+"_index  on  "+TempTableName+" (B0110,E01A1,I9999)");
			}
		 
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}

		// System.out.println(strBuffer.toString());

		fillTempTable(strBuffer.toString());
	}

	// private String maxI9999(String table, String key, String userSet) {
	// String value = "";
	// FieldSet vo = DataDictionary.getFieldSetVo(userSet);
	// String z0 = userSet + "Z0";
	// String z1 = userSet + "Z1";
	// String monthOfz0 = Sql_switcher.month(z0);
	// String yearOfz0 = Sql_switcher.year(z0);
	// if (ymc != null && vo.getChangeflag().trim().equals("2")) {
	// value = "(select * from " + table + " where " + monthOfz0 + "="
	// + ymc.getMonth() + " and " + yearOfz0 + "="
	// + ymc.getYear() + " and " + z1 + "=" + ymc.getCount();
	// } else {
	// value = "(select * from " + table
	// + " a where a.I9999=(select max(I9999) from " + table
	// + " b where " + "a" + key + "=b" + key + "))" + table;
	//
	// }
	// return value;
	//
	// }

	private void fillTempTable(String fieldList) {
		if (!"".equals(fieldList.trim())) {
			fieldList = "," + fieldList;
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("insert into " + TempTableName);
		// 人员表
		String table = "";
		if (InfoGroupFlag == forPerson) {
			sqlBuffer.append("(A0100,B0110,E0122,E01A1)");
			sqlBuffer.append(" select " + DbPre + "A01.A0100," + DbPre
					+ "A01.B0110," + DbPre + "A01.E0122," + DbPre + "A01.E01A1 from ");
			table = this.DbPre + "A01";
			sqlBuffer.append(table);
			if(existWhereText!=null&&existWhereText.trim().length()>0)
			{
				sqlBuffer.append(" where exists ( "+this.existWhereText+" )");
			}
			else
			{
				if (!("()".equalsIgnoreCase(whereText))
						&& whereText.trim().length() > 0)
					sqlBuffer.append(" where " + DbPre + "A01.A0100 in "
							+ whereText);
			}
			
		} else if (InfoGroupFlag == forUnit) {
			sqlBuffer.append("(B0110)");
			sqlBuffer.append(" select B01.B0110 from ");
			table = "B01";
			sqlBuffer.append(table);
			if(existWhereText!=null&&existWhereText.trim().length()>0)
			{
				sqlBuffer.append(" where exists ( "+this.existWhereText+" )");
			}
			else
			{
				if (whereText != null && whereText.trim().length() > 0
						&& !("()".equalsIgnoreCase(whereText)))
					sqlBuffer.append(" where " + "B01.B0110 in " + whereText);
			}
			
		} else if (InfoGroupFlag == forPosition) {
			sqlBuffer.append("(E01A1)");
			sqlBuffer.append(" select K01.E01A1 from ");
			table = "K01";
			sqlBuffer.append(table);
			if(existWhereText!=null&&existWhereText.trim().length()>0)
			{
				sqlBuffer.append(" where exists ( "+this.existWhereText+" )");
			}
			else
			{
				if (whereText != null && whereText.trim().length() > 0
						&& !("()".equalsIgnoreCase(whereText)))
					sqlBuffer.append(" where " + "K01.E01A1 in " + whereText);
			}
			
		}
		// boolean flg = false;
		// for (int i = 0; i < UsedSets.size(); i++) {
		// String userSet = getDbPreTable((String) UsedSets.get(i));
		// if (table.trim().equalsIgnoreCase(userSet)) {
		// continue;
		// }
		// String joinKey = "." + getKeyField((String) UsedSets.get(i));
		// if (flg == false) {
		// sqlBuffer.append(table);
		// flg = true;
		// }
		//
		// sqlBuffer.append(" left join ").append(
		// maxI9999(userSet, joinKey, (String) UsedSets.get(i)));
		//
		// sqlBuffer.append(" on ").append(table + joinKey + "=").append(
		// userSet).append(joinKey);
		// }

		try {
			ContentDAO dao = new ContentDAO(this.con);
			log.debug("fillTempTable方法sql:{},InfoGroupFlag:{}",sqlBuffer.toString(),InfoGroupFlag);
			dao.update(sqlBuffer.toString());//todo
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void SQL_SubSet() {

		String strCurSet = "", strCurMenu = "", strSet1 = "", strSet2 = "", strJoin = "", strKey = "";
		StringBuffer strFldList = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strSQLSet = new StringBuffer();
		StringBuffer strSQLFrom = new StringBuffer();
		StringBuffer strSQLLeft = new StringBuffer();
		StringBuffer strSQLOn = new StringBuffer();
		FieldItem fieldTemp = null;

		int count = 0;
		/** *************临时表得到条件字段的数据********************* */
		/** chenmengqing added 20071226 added */
		boolean bHaveVar = false; // 是否存在变量
		StringBuffer sLocH = new StringBuffer();
		StringBuffer strV1 = new StringBuffer();
		StringBuffer strV2 = new StringBuffer();
		StringBuffer strVFld = new StringBuffer();
		DbWizard dbWizard = new DbWizard(this.con); 
		StringBuffer mssql_table=new StringBuffer("");
		if (UsedSets.size() == 0 && mapUsedFieldItems.size() > 0) {
			Iterator it = mapUsedFieldItems.keySet().iterator();
			
			while (it.hasNext()) {
				fieldTemp = (FieldItem) mapUsedFieldItems.get(it.next());
				String fieldname = fieldTemp.getItemid().toUpperCase();
				if (fieldname.indexOf("SELECT_") != -1)
					continue;
				if (fieldname.indexOf("STD_") != -1)
					continue;
				if (fieldname.indexOf("CTON_") != -1)
					continue;
				if(!dbWizard.isExistField(StdTmpTable, fieldname, false))
					continue;
				
				String sfld = "," + fieldTemp.getItemid().toUpperCase() + ",";
				if (sLocH.indexOf(sfld) != -1)
					continue;
				sLocH.append(sfld);
				switch (DBType) {
				case Constant.ORACEL:

				case Constant.DB2:
					bHaveVar = true;
					strV1.append(fieldname);
					strV1.append(",");
					if(fieldTemp.getVarible() == 1&&StdTmpTable.length() >8&&StdTmpTable.toLowerCase().matches("^templet_\\d+")) //表单审批一人多条记录时，临时变量调来临时变量报错 2017-08-30
						strV2.append("max("+fieldname+")");
					else	
						strV2.append(fieldname);
					strV2.append(",");
					break;
				case Constant.MSSQL:
					bHaveVar = true;
					if (strVFld.length() == 0) {
						strVFld.append(TempTableName);
						strVFld.append(".");
						strVFld.append(fieldname);
						strVFld.append("=");
						if(fieldTemp.getVarible() == 1&&StdTmpTable.length() >8&&StdTmpTable.toLowerCase().matches("^templet_\\d+")) //表单审批一人多条记录时，临时变量调来临时变量报错 2017-08-30
						{ 
							mssql_table.append(",max("+StdTmpTable);
							mssql_table.append(".");
							mssql_table.append(fieldname+") as "+fieldname);
						} 
						
						{
							strVFld.append(StdTmpTable);
							strVFld.append(".");
							strVFld.append(fieldname);
						}
					} else {
						strVFld.append(",");
						strVFld.append(TempTableName);
						strVFld.append(".");
						strVFld.append(fieldname);
						strVFld.append("=");
						if(fieldTemp.getVarible() == 1&&StdTmpTable.length() >8&&StdTmpTable.toLowerCase().matches("^templet_\\d+")) //表单审批一人多条记录时，临时变量调来临时变量报错 2017-08-30
						{
							mssql_table.append(",max("+StdTmpTable);
							mssql_table.append(".");
							mssql_table.append(fieldname+") as "+fieldname);
						}
						
						{
							strVFld.append(StdTmpTable);
							strVFld.append(".");
							strVFld.append(fieldname);
						}
					}
					break;
				}
			}
		}
		// chenmengqing added end.
		for (int i = 0; i < UsedSets.size(); i++) {
			count++;
			strSet1 = "";
			strSet2 = "";
			// 初始化fldList
			strFldList.setLength(0);
			Iterator it = mapUsedFieldItems.keySet().iterator();
			while (it.hasNext()) {
				fieldTemp = (FieldItem) mapUsedFieldItems.get(it.next());
				// 全局变量付值
				Field = fieldTemp;
				if (fieldTemp.getVarible() == 0)// 如果不为临时变量 20080421 for 国际电视
				{
					if (fieldTemp.getFieldsetid().equals(UsedSets.get(i))/*
																			 * &&
																			 * fieldTemp.getVarible() ==
																			 * 0
																			 */) {
						if (ModeFlag == forSearch) {
							if (!fieldTemp.isOrg() && !fieldTemp.isPos())
								strCurSet = DbPre + UsedSets.get(i);
							else
								strCurSet = (String) UsedSets.get(i);
						} else if (ModeFlag == forPerson) {
							strCurSet = (String) UsedSets.get(i);
						}

						strCurMenu = strCurSet + '.'
								+ Field.getItemid().toUpperCase();
						if ((DBType == Constant.ORACEL) || (DBType == 3)) {
							strSet1 = strSet1 + TempTableName + '.'
									+ Field.getItemid().toUpperCase() + ',';
							strSet2 = strSet2 + strCurSet + '.'
									+ Field.getItemid().toUpperCase() + ',';
						} else {
							if (strFldList.length() == 0) {
								strFldList.append(TempTableName).append('.')
										.append(Field.getItemid()).append('=')
										.append(strCurMenu);
							} else {
								strFldList
										.append(',')
										.append(TempTableName)
										.append('.')
										.append(Field.getItemid().toUpperCase())
										.append('=').append(strCurMenu);
							}
						}
					} else
						continue;
				} else {
					String fieldname = fieldTemp.getItemid().toUpperCase();
					if (fieldname.indexOf("SELECT_") != -1)
						continue;
					if (fieldname.indexOf("GET_") != -1) // 20081010 dengcan
						continue;
					if (fieldname.indexOf("STD_") != -1)
						continue;
					if (fieldname.indexOf("CTON_") != -1)
						continue;
					if(!dbWizard.isExistField(StdTmpTable, fieldname, false))
						continue;
					String sfld = "," + fieldTemp.getItemid().toUpperCase()
							+ ",";
					if (sLocH.indexOf(sfld) != -1)
						continue;
					sLocH.append(sfld);
					switch (DBType) {
					case Constant.ORACEL:
					case Constant.DB2:
						bHaveVar = true;
						strV1.append(fieldname);
						strV1.append(",");
						strV2.append(fieldname);
						strV2.append(",");
						break;
					case Constant.MSSQL:
						bHaveVar = true;
						if (strVFld.length() == 0) {
							strVFld.append(TempTableName);
							strVFld.append(".");
							strVFld.append(fieldname);
							strVFld.append("=");
							strVFld.append(StdTmpTable);
							strVFld.append(".");
							strVFld.append(fieldname);
						} else {
							strVFld.append(",");
							strVFld.append(TempTableName);
							strVFld.append(".");
							strVFld.append(fieldname);
							strVFld.append("=");
							strVFld.append(StdTmpTable);
							strVFld.append(".");
							strVFld.append(fieldname);
						}
						break;
					}// 临时变量 end.
				}
			}

			strJoin = '.' + getKeyField((String) UsedSets.get(i));
			strKey = getKeyField((String) UsedSets.get(i));
			// 如果是Oracle或db2去逗号
			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strSet1 = strSet1.substring(0, strSet1.length() - 1);
				strSet2 = strSet2.substring(0, strSet2.length() - 1);
			}

			FieldSet vo = DataDictionary
					.getFieldSetVo((String) UsedSets.get(i));
			// System.out.println(ymc +"-------集" + UsedSets.get(i));
			if (ymc != null
					&& ("1".equals(vo.getChangeflag().trim()) || "2".equals(vo
							.getChangeflag().trim()))
					&& isYearMonthCount) {
				String z0 = strCurSet + "." + UsedSets.get(i) + "Z0";
				String z1 = strCurSet + "." + UsedSets.get(i) + "Z1";
				String monthOfz0 = Sql_switcher.month(z0);
				String yearOfz0 = Sql_switcher.year(z0);
				strSQL.setLength(0);
				if (DBType == Constant.ORACEL || DBType == 3) {
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName).append(" set (");
					strSQL.append(strSet1);
					strSQL.append(") =(SELECT ");
					strSQL.append(strSet2);
					strSQL.append(" FROM ");
					strSQL.append(strCurSet);
					strSQL.append(" where ");
					strSQL.append(TempTableName).append(strJoin);
					strSQL.append("=").append(strCurSet).append(strJoin);
					strSQL.append(" and ").append(monthOfz0).append("=")
							.append(ymc.getMonth());
					strSQL.append(" and ").append(yearOfz0).append("=");
					strSQL.append(ymc.getYear()).append(" and ").append(z1)
							.append("=");
					strSQL.append(ymc.getCount() + " )");
					SQLS.add(strSQL.toString());
				} else// MSSQL
				{
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName).append(" set ");
					strSQL.append(strFldList).append(" from ").append(
							TempTableName);
					strSQL.append(" LEFT JOIN ").append(strCurSet);
					strSQL.append(" on ").append(TempTableName).append(strJoin);
					strSQL.append("=").append(strCurSet).append(strJoin);
					strSQL.append(" where ").append(monthOfz0).append("=")
							.append(ymc.getMonth());
					strSQL.append(" and ").append(yearOfz0).append("=");
					strSQL.append(ymc.getYear()).append(" and ").append(z1)
							.append("=");
					strSQL.append(ymc.getCount() + " ");
					SQLS.add(strSQL.toString());
				}
			} else {

	 			
				String _tempTable = "T_" + TempTableName;
				if (DBType == Constant.MSSQL && this.isTempTable)
					_tempTable = "##T_" + TempTableName;

				if (!IsMainSet((String) UsedSets.get(i))) {
					
				/*	
					
					if (DBType == Constant.MSSQL) {
						if (this.isTempTable)
							SQLS.add("drop table ##T_" + TempTableName);
						else
							SQLS.add("drop table T" + TempTableName);
					} else {
						if (this.isTempTable)
							SQLS.add("TRUNCATE table T_" + TempTableName);
						SQLS.add("drop table T_" + TempTableName);
					}
					if (DBType == 3) {
						strSQL.append("CREATE TABLE T_").append(TempTableName)
								.append(" AS (SELECT ").append(strKey);
						strSQL.append(",MAX(I9999) AS MAX_I9999 ").append(
								" FROM ").append(strCurSet)
								.append(" GROUP BY ");
						strSQL.append(strKey).append(") DEFINITION ONLY");
						SQLS.add(strSQL.toString());
						strSQL.setLength(0);
						strSQL.append("INSERT INTO T_").append(TempTableName)
								.append(" SELECT ").append(strKey).append(
										",MAX(I9999) AS MAX_I9999 ");
						strSQL.append(" FROM ").append(strCurSet);
						strSQL.append(" GROUP BY ").append(strKey);
						SQLS.add(strSQL.toString());
						
						SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKey+",MAX_I9999)");
						 
						
						strSQL.setLength(0);
					} else if (DBType == 2) {
						strSQL.setLength(0);
						strSQL.append("CREATE ");
						if (this.isTempTable)
							strSQL.append(" GLOBAL TEMPORARY ");
						strSQL.append(" TABLE   ").append(_tempTable);
						if (this.isTempTable)
							strSQL.append(" On Commit Preserve Rows ");
						strSQL.append(" AS SELECT ").append(strKey);
						strSQL.append(",MAX(I9999) AS MAX_I9999 ").append(
								" FROM ").append(strCurSet);

						//----------       2008-11-14 dengcan 添加条件，减少记录，加快效率  
						if (InfoGroupFlag == forPerson
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"A")) {
							if (!(whereText.equalsIgnoreCase("()"))
									&& whereText.trim().length() > 0)
								strSQL.append(" where  A0100 in " + whereText);
						} else if (InfoGroupFlag == forUnit
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"B")) {
							if (whereText != null
									&& whereText.trim().length() > 0
									&& !(whereText.equalsIgnoreCase("()")))
								strSQL.append(" where B0110 in " + whereText);
						} else if (InfoGroupFlag == forPosition
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"K")) {
							if (!(whereText.equalsIgnoreCase("()"))
									&& whereText.trim().length() > 0)
								strSQL.append(" where E01A1 in " + whereText);
						}
						//------------------    end  --------------- 
						strSQL.append(" GROUP BY ");
						strSQL.append(strKey);
						SQLS.add(strSQL.toString());
						
						
						SQLS.add("create index "+_tempTable+"_index  on "+_tempTable+" ("+strKey+",MAX_I9999)");
						 
						strSQL.setLength(0);
					} else {
						strSQL.setLength(0);
						strSQL.append("SELECT ");
						strSQL.append(strKey);
						strSQL.append(",MAX(I9999) AS MAX_I9999 INTO ");
						strSQL.append(_tempTable);
						strSQL.append(" FROM ");
						strSQL.append(strCurSet);

						//----------       2008-11-14 dengcan 添加条件，减少记录，加快效率  
						if (InfoGroupFlag == forPerson
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"A")) {
							if (!(whereText.equalsIgnoreCase("()"))
									&& whereText.trim().length() > 0)
								strSQL.append(" where  A0100 in " + whereText);
						} else if (InfoGroupFlag == forUnit
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"B")) {
							if (whereText != null
									&& whereText.trim().length() > 0
									&& !(whereText.equalsIgnoreCase("()")))
								strSQL.append(" where B0110 in " + whereText);
						} else if (InfoGroupFlag == forPosition
								&& strCurSet.substring(0, 1).equalsIgnoreCase(
										"K")) {
							if (!(whereText.equalsIgnoreCase("()"))
									&& whereText.trim().length() > 0)
								strSQL.append(" where E01A1 in " + whereText);
						}
						//------------------    end  ----------------- 

						strSQL.append(" GROUP BY ");
						strSQL.append(strKey);
						SQLS.add(strSQL.toString());
						
						SQLS.add("create index "+_tempTable+"_index  on "+_tempTable+" ("+strKey+",MAX_I9999)");
						 
						strSQL.setLength(0);
					}

					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == 2) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(TempTableName).append(
								".I9999=(SELECT ").append(_tempTable).append(
								".MAX_I9999");
						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(_tempTable);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						strSQLOn.append(" WHERE ").append(TempTableName)
								.append(strJoin).append("=").append(_tempTable)
								.append(strJoin).append(")");
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(TempTableName).append(
								".I9999=").append(_tempTable).append(
								".MAX_I9999");

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" LEFT JOIN ").append(_tempTable);

						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(_tempTable).append(
								strJoin);
					}
 
					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
	*/				
					
					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET (")
								.append(strSet1.toUpperCase()).append(
										")=(SELECT ").append(
										strSet2.toUpperCase());

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ");
						strSQLFrom.append(strCurSet);

						strSQLFrom.append(",(SELECT "+strKey+",MAX(I9999) AS MAX_I9999  FROM "+strCurSet);
						if (InfoGroupFlag == forPerson
								&& "A".equalsIgnoreCase(
                                strCurSet.substring(0, 1))) {
							if (!("()".equalsIgnoreCase(whereText))
									&& whereText.trim().length() > 0)
								strSQLFrom.append(" where  A0100 in " + whereText);
						} else if (InfoGroupFlag == forUnit
								&& "B".equalsIgnoreCase(
                                strCurSet.substring(0, 1))) {
							if (whereText != null
									&& whereText.trim().length() > 0
									&& !("()".equalsIgnoreCase(whereText)))
								strSQLFrom.append(" where B0110 in " + whereText);
						} else if (InfoGroupFlag == forPosition
								&& "K".equalsIgnoreCase(
                                strCurSet.substring(0, 1))) {
							if (!("()".equalsIgnoreCase(whereText))
									&& whereText.trim().length() > 0)
								strSQLFrom.append(" where E01A1 in " + whereText);
						} 
						strSQLFrom.append(" GROUP BY "+strKey+") ab "); //dengcan 2012-4-26
						
						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						
						strSQLOn.append(" WHERE  ");
						strSQLOn.append(TempTableName)
								.append(strJoin).append("=");
						strSQLOn.append(strCurSet).append(strJoin).append(" AND ");
						
						strSQLOn.append(strCurSet+"."+strKey+"=ab."+strKey+" and "+strCurSet+".i9999=ab.MAX_I9999 ) "); // and 
						
						/*
						strSQLOn.append(" WHERE ").append(TempTableName)
								.append(strJoin).append("=");
						strSQLOn.append(strCurSet).append(strJoin).append(" AND ");
						*/
					//	strSQLOn.append(TempTableName).append(".I9999=");
					//	strSQLOn.append(strCurSet).append(".I9999)");
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(strFldList);
						// System.out.println(strFldList);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
					//	strSQLLeft.append(" LEFT JOIN ").append(strCurSet);

						if (!IsMainSet(strCurSet))
						{
							strSQLLeft.append("  LEFT JOIN  ( SELECT "+strCurSet+".*  FROM "+strCurSet+", ");
							strSQLLeft.append("(SELECT "+strKey+",MAX(I9999) I9999  FROM "+strCurSet);
							if (InfoGroupFlag == forPerson
									&& "A".equalsIgnoreCase(
                                    strCurSet.substring(0, 1))) {
								if (!("()".equalsIgnoreCase(whereText))
										&& whereText.trim().length() > 0)
									strSQLLeft.append(" where  A0100 in " + whereText);
							} else if (InfoGroupFlag == forUnit
									&& "B".equalsIgnoreCase(
                                    strCurSet.substring(0, 1))) {
								if (whereText != null
										&& whereText.trim().length() > 0
										&& !("()".equalsIgnoreCase(whereText)))
									strSQLLeft.append(" where B0110 in " + whereText);
							} else if (InfoGroupFlag == forPosition
									&& "K".equalsIgnoreCase(
                                    strCurSet.substring(0, 1))) {
								if (!("()".equalsIgnoreCase(whereText))
										&& whereText.trim().length() > 0)
									strSQLLeft.append(" where E01A1 in " + whereText);
							} 
							strSQLLeft.append(" GROUP BY "+strKey+" ) AB ");
							strSQLLeft.append(" WHERE "+strCurSet+"."+strKey+"=AB."+strKey+" AND "+strCurSet+".I9999=AB.I9999 )   "+strCurSet);
							
							
						}
						else
							strSQLLeft.append(" LEFT JOIN ").append(strCurSet);
						
						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(strCurSet).append(
								strJoin);
					/*	strSQLOn.append(" AND ").append(TempTableName).append(
								".I9999=").append(strCurSet).append(".I9999");*/
					}
					/*
					 * System.out.println(strSQL.append(strSQLSet).append(strSQLFrom)
					 * .append(strSQLLeft).append(strSQLOn).toString());
					 */
					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
					strSQL.setLength(0);
					
					
					
				} else {
					
					
					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET (").append(strSet1).append(
								")=(SELECT ").append(strSet2);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(strCurSet);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						strSQLOn.append(" WHERE ").append(TempTableName)
								.append(strJoin);
						strSQLOn.append("=").append(strCurSet).append(strJoin);
						if (!IsMainSet((String) UsedSets.get(i))) {
							strSQLOn.append(" AND ").append(TempTableName)
									.append(".I9999=").append(strCurSet)
									.append(".I9999").append(")");
						} else {
							strSQLOn.append(")");
						}
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(strFldList);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" LEFT JOIN ").append(strCurSet);

						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(strCurSet).append(
								strJoin);
						if (!IsMainSet((String) UsedSets.get(i))) {
							strSQLOn.append(" AND ").append(TempTableName)
									.append(".I9999=").append(strCurSet)
									.append(".I9999");
						}
					}
					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
					strSQL.setLength(0);
					
				}
			}
			
			
			
			
			
			
			
			
			
			
		}// for loop end.

		if (bHaveVar && StdTmpTable != null && StdTmpTable.length() > 0&&!this.isStatMultipleVar()) {
			strSQL.setLength(0);
			switch (DBType) {
			case Constant.ORACEL:
			case Constant.DB2:
				strV1.setLength(strV1.length() - 1);
				strV2.setLength(strV2.length() - 1);
				break;
			}
			if (!(TempTableName.equalsIgnoreCase(StdTmpTable) || TempTableName
					.length() == 0)) {
				String varcond = getVarCond(StdTmpTable);
		 		
				String key="A0100";
				if(this.InfoGroupFlag ==YksjParser.forUnit)
				{
					varcond=" 1=1";
					key="B0110";
				}
				else if(InfoGroupFlag == YksjParser.forPosition)
				{
					varcond=" 1=1";
					key="E01A1";
				} 
				varcond+=StdTmpTable_where; //当前列表的 数据范围  2014-04-01  dengcan
				
				switch (DBType) {
				case Constant.ORACEL:
				case Constant.DB2:
					strSQL.append("update ");
					strSQL.append(TempTableName);
					strSQL.append(" set (");
					strSQL.append(strV1);
					strSQL.append(")=");
					strSQL.append("(select ");
					strSQL.append(strV2);
					strSQL.append(" from ");
					strSQL.append(StdTmpTable);
					strSQL.append(" where ");
					strSQL.append(TempTableName);
					strSQL.append("."+key+"=");
					strSQL.append(StdTmpTable);
					strSQL.append("."+key+"");
					/** 库过滤条件 */
					strSQL.append(" and ");
					strSQL.append(varcond);

					strSQL.append(")");
					break;
				case Constant.MSSQL:
					strSQL.append("update ");
					strSQL.append(TempTableName);
					strSQL.append(" set ");
					strSQL.append(strVFld.toString());
					strSQL.append(" from ");
					
					if(mssql_table.length()>0) //sqlserver 聚合不应出现在 UPDATE 语句。
						strSQL.append(" ( select "+mssql_table.substring(1)+","+key+" from "+StdTmpTable+" where "+varcond+" group by "+key+" ) "+StdTmpTable);
					else
						strSQL.append(StdTmpTable); 
					strSQL.append(" where ");
					strSQL.append(TempTableName);
					strSQL.append("."+key+"=");
					strSQL.append(StdTmpTable);
					strSQL.append("."+key+"");
					
					if(mssql_table.length()==0)
					{
						strSQL.append(" and ");
						strSQL.append(varcond);
					}
					break;
				}
				SQLS.add(strSQL.toString());
			}
		} 
		if (mapUsedFieldItems.size() > 0&&this.flg==2&& this.isSupportVar&&(StdTmpTable == null || StdTmpTable.length() ==0)) {
			String tmptable ="t#"+this.userView.getUserName() + "_sf_mid"; // this.userView.getUserName() + "midtable";
			StringBuffer _sql = new StringBuffer("");
			Iterator it = mapUsedFieldItems.keySet().iterator();
			
			String _str="";
			if(this.InfoGroupFlag == YksjParser.forPerson)
				_str=this.TempTableName + ".a0100=" + tmptable+ ".a0100";
			else if (InfoGroupFlag == YksjParser.forUnit)
				_str=this.TempTableName + ".b0110=" + tmptable+ ".b0110";
			else if (InfoGroupFlag == YksjParser.forPosition)
				_str=this.TempTableName + ".e01a1=" + tmptable+ ".e01a1";
			while (it.hasNext()) {
				fieldTemp = (FieldItem) mapUsedFieldItems.get(it.next());
				String fieldname = fieldTemp.getItemid().toUpperCase();
				if (fieldTemp.getVarible() != 1)
					continue;
				if (fieldname.indexOf("SELECT_") != -1)
					continue;
				if (fieldname.indexOf("STD_") != -1)
					continue;
				_sql.setLength(0);
				_sql.append("update " + this.TempTableName + " set "
						+ fieldname + "=");
				_sql.append(" ( select " + fieldname + " from " + tmptable
						+ " where " +_str+ " ) where exists ");
				_sql.append(" ( select null from " + tmptable + " where "
						+_str+ " ) ");
				this.SQLS.add(_sql.toString());
			}
		}
		
		
		
	}

	/**
	 * 求得临时变量更新条件,仅考虑了工资变动
	 * 
	 * @return
	 */
	private String getVarCond(String tableName) {
		StringBuffer buf = new StringBuffer();
		if (tableName.length() > 5
				&& (tableName.toLowerCase().indexOf("salary") != -1||tableName.toLowerCase().endsWith("_gz") 
						|| tableName.toLowerCase().endsWith("gzsp") || tableName.toLowerCase().indexOf("_ys_") != -1))
			buf.append("upper(nbase)='");
		else
			buf.append("upper(basepre)='");
		buf.append(DbPre.toUpperCase());
		buf.append("'");
		return buf.toString();
	}

	/**
	 * 如果ＳＱＬ语句为drop table，则需要分析此表是否存在，存在 才应执行此语句
	 * 
	 * @param sql
	 * @param dbw
	 * @return
	 */
	private boolean brun(String sql,DbWizard dbw)
	{
		boolean bflag=false;
		sql=sql.trim();
		sql=sql.toLowerCase();
		
		if(sql.toLowerCase().startsWith("drop ")&&DBType==Constant.ORACEL&&sql.toLowerCase().indexOf("sequence")!=-1) // 如果是oracle
																														// 的sequence,则不需要判断
			return true;
		if(sql.startsWith("drop "))
		{
			int idx=sql.lastIndexOf(" table ");
			if(idx!=-1)
			{
				String tablename=sql.substring(idx+6,sql.length());
				//oralce dm 数据库表面后面可能有 purge 判断是否要放到回收站
				int index = tablename.indexOf("purge");
				if(index!=-1){
					tablename = tablename.substring(0,index);
				}
				bflag=dbw.isExistTable(tablename.trim(), false);
			}
		}
		else if(this.con!=null&&sql.toLowerCase().indexOf("alter")!=-1&&sql.toLowerCase().indexOf(" table ")!=-1&&sql.toLowerCase().indexOf(" drop ")!=-1&&sql.toLowerCase().indexOf(" column ")!=-1)
		{
			RowSet rowSet=null;
			try
			{
				int from_index=sql.toLowerCase().indexOf(" table ");
				int to_index=sql.toLowerCase().indexOf(" drop ");
				String tablename=sql.substring(from_index+6,to_index);
				
				from_index=sql.indexOf(" column ");
				String columnName=sql.toLowerCase().substring(from_index+7).trim();
				boolean flag=false;
				dao=new ContentDAO(this.con);
				rowSet=dao.search("select * from "+tablename+" where 1=2");
				ResultSetMetaData mt=rowSet.getMetaData();
				for(int i=0;i<mt.getColumnCount();i++)
				{
					String _columnName=mt.getColumnName(i+1).toLowerCase().trim();
					if(_columnName.equalsIgnoreCase(columnName))
						flag=true;
				}
				return flag;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				try
				{
					if(rowSet!=null)
						rowSet.close();
				}
				catch(Exception ee)
				{
					
				}
			}
			
		}
		else
			bflag=true;
		return bflag;
	}

	/**
	 * 内部批量执行SQL语句
	 * 
	 * @throws GeneralException
	 */
	private void run_SQLS() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.con);
		DbWizard dbw = new DbWizard(this.con);
		for (int i = 0; i < SQLS.size(); i++) {
			try {

				//   System.out.println("str="+this.FSource);
				//    System.out.println("ssss----->" + i + " " + (String)SQLS.get(i));

				String sql = (String) SQLS.get(i);
				if (brun(sql, dbw)) {
					//dm 数据库使用dao.update 执行select 会报非法sql
					if(sql!=null && sql.toLowerCase().startsWith("select ")){
						dao.search(sql);
					}else{
						dao.update((String) SQLS.get(i));
					}
				}
			} catch (SQLException e) {
				// e.printStackTrace();
				Category.getInstance(this.getClass()).debug(e.getMessage());
			}
		}
		dao=null;
		dbw=null;
		// if(getSQL().length()>0)//chenmengqing added 20071226
		// excuteSql();
	}

	/**
	 * 直接更新目标表字段
	 */
	private void excuteSql() {
		ContentDAO dao = new ContentDAO(this.con);
		/** ************修改临时表操作字段的数据******************* */
		String strJoin = "";
		if (InfoGroupFlag == forPerson) {
			strJoin = ".A0100";
		} else if (InfoGroupFlag == forPosition) {
			strJoin = ".E01A1";
		} else if (InfoGroupFlag == forUnit) {
			strJoin = ".B0110";
		}

		StringBuffer sqlText = new StringBuffer();
		sqlText.append("update ");
		sqlText.append(TempTableName);
		sqlText.append(" set ");
		sqlText.append(targetField);
		sqlText.append("=");
		sqlText.append(getSQL());
		try {
			if (TempTableName != null && TempTableName.trim().length() > 0
					&& targetField != null && targetField.trim().length() > 0)
			{
				if(isStatVar=true&& "NULL".equalsIgnoreCase(getSQL())) //如果是统计 临时变量=xx,临时变量=xxx 函数
				{
					isStatVar=false;
				}
				else
				{
			//		System.out.println("="+sqlText.toString());
					dao.update(sqlText.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** 代码进行翻译转换 */
		/*
		 * chenmengqing added at 20070920 if (codeset != null &&
		 * !codeset.trim().equals("")&&!codeset.trim().equals("0")) {
		 * sqlText.setLength(0); sqlText.append("update ");
		 * sqlText.append(TempTableName); sqlText.append(" set ");
		 * sqlText.append(targetField); sqlText.append("=codeitem.codeitemdesc
		 * from codeitem," + TempTableName); sqlText.append(" where " +
		 * TempTableName + "." + targetField + "=codeitem.codeitemid");
		 * sqlText.append(" and codeitem.codesetid='" + codeset + "'"); try {
		 * dao.update(sqlText.toString());
		 *  } catch (Exception e) { e.printStackTrace(); } }
		 */
		/** ***************以上都是对临时表数据的操作******************** */
		if(this.isStatMultipleVar())  /** 统计临时变量 */
		{
			
			sqlText.setLength(0);
			sqlText.append("update ");
			sqlText.append(targetTable);
			sqlText.append(" set ");
			if (DBType == Constant.ORACEL || DBType == 3) {
				
				StringBuffer _str=new StringBuffer("");
				for(int i=0;i<this.statVarList.size();i++)
				{
					_str.append(","+(String)this.statVarList.get(i));
				}
				
				sqlText.append("("+_str.substring(1)+")");
				sqlText.append("=(").append("select ").append(_str.substring(1))
						.append(" from ").append(TempTableName);
				sqlText.append(" where ").append(TempTableName).append(strJoin);
				sqlText.append("=").append(targetTable).append(strJoin);
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) {
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
				sqlText.append(")");
				sqlText.append("where " + targetTable + strJoin + " in (select "
						+ TempTableName + strJoin + " from " + TempTableName + ")");
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) {
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
			} else {
				StringBuffer _str=new StringBuffer("");
				for(int i=0;i<this.statVarList.size();i++)
				{
					_str.append(","+targetTable+"."+(String)this.statVarList.get(i)+"="+TempTableName+"."+(String)this.statVarList.get(i));
				}
				sqlText.append(_str.substring(1));
				 
				sqlText.append(" from " + targetTable);
				sqlText.append(", " + TempTableName);
				sqlText.append(" where " + targetTable + strJoin);
				sqlText.append("=" + TempTableName + strJoin);
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) {
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
			}
			
		}
		else
		{
			sqlText.setLength(0);
			sqlText.append("update ");
			sqlText.append(targetTable);
			sqlText.append(" set ");
			if (DBType == Constant.ORACEL || DBType == 3) {
				String targetFieldDate = targetField;
				/*
				 * if
				 * (targetFieldDataType!=null&&this.targetFieldDataType.trim().equalsIgnoreCase("D") &&
				 * this.flg == 1) { targetFieldDate =
				 * Sql_switcher.dateToChar(targetField); }
				 */// 2008/06/13 orcale 由于生成的语句为 set AAAAA =(select
							// TO_CHAR(AAAAA,'YYYY-MM-DD') 类型不匹配,所以注释掉 dengcan
				sqlText.append("("+targetTable+"."+targetField+")");
				sqlText.append("=(").append("select ").append(targetFieldDate)
						.append(" from ").append(TempTableName);
				sqlText.append(" where ").append(targetTable).append(strJoin);
				sqlText.append("=").append(TempTableName).append(strJoin);
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) { 
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
				sqlText.append(")");
				
				sqlText.append("where exists (select null  from " + TempTableName+" where "+targetTable+strJoin+ "="+ TempTableName + strJoin+")");
				
			//	sqlText.append("where " + targetTable + strJoin + " in (select "+ TempTableName + strJoin + " from " + TempTableName + ")");
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) { 
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
			} else {
	
				// 待修改
				if (targetFieldDataType != null
						&& "D".equalsIgnoreCase(targetFieldDataType.trim())) {
					Sql_switcher.charToDate("a");
				}
				sqlText.append(targetField);
				sqlText.append("=");
				String value = "";
				if (this.VarType == DATEVALUE) {
					value = Sql_switcher.dateToChar(TempTableName + "."
							+ targetField, "yyyy.mm.dd");
				} else {
					value = TempTableName + "." + targetField;
				}
				sqlText.append(value);
				sqlText.append(" from " + targetTable);
				sqlText.append(", " + TempTableName);
				sqlText.append(" where " + targetTable + strJoin);
				sqlText.append("=" + TempTableName + strJoin);
				if ((flg == 2||flg == 5)&&InfoGroupFlag == forPerson) { 
					sqlText.append(" and UPPER(NBASE)='" + DbPre.toUpperCase() + "'");
				}
			}
		}
		// sqlText.append(" left join " + TempTableName);
		// sqlText.append(" on ").append(targetTable).append(strJoin);
		// sqlText.append("=").append(TempTableName).append(strJoin);
		// 

		try {
			if (getRenew_term() != null && getRenew_term().length() > 0) {
				sqlText.append(" and " + getRenew_term());
			}
			// System.out.println(sqlText.toString());
			if (targetTable != null && targetTable.trim().length() > 0
					&& TempTableName != null
					&& TempTableName.trim().length() > 0) {
				// &&DbPre!=null&&DbPre.trim().length()>0) //当在职位管理和机构管理的花名册中
				// 就有问题了，所以注释掉 //DENGCAN
				// System.out.println(sqlText.toString());
				dao.update(sqlText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String GetCurSet(FieldItem Field) {
		String result = Field.getFieldsetid();
		if (Field.getFieldsetid().startsWith("A")) {
			result = DbPre + Field.getFieldsetid();
		}
		return result;
	}

	private String GetCurMenu(boolean bAddSet, FieldItem Field) {
		String str;
		StringBuffer result = new StringBuffer();
		if (!bAddSet) {
			str = Field.getItemid();
		} else {
			if (Field.getFieldsetid() == null
					|| "".equals(Field.getFieldsetid()))
				str = Field.getItemid();
			else {
				// 如果是临时变量 取得是单位或职位的数据 不应该加库前缀 2008-12-24 dengcan
				if (Field.getFieldsetid().toUpperCase().charAt(0) == 'B'
						|| Field.getFieldsetid().toUpperCase().charAt(0) == 'K')
					str = Field.getFieldsetid() + "." + Field.getItemid();
				else
					str = DbPre + Field.getFieldsetid() + "."
							+ Field.getItemid();
			}
		}
		result.setLength(0);
		result.append(str);
		// System.out.println( Field.getItemtype());
		if (Field.isInt() || Field.isFloat()) {
			switch (DBType) {
			case 1: {
				result.setLength(0);
		/*		if (bDivFlag)
					result.append("NullIF(").append(str).append(",0)");
				else  */
					result.append("ISNULL(").append(str).append(",0)");
				break;
			}
			case 2: {
				result.setLength(0);
		/*		if (bDivFlag)
					result.append("NullIF(").append(str).append(",0)");
				else  */
					result.append("NVL(").append(str).append(",0)");
				break;
			}
			case 3: {
				result.setLength(0);
		/*		if (bDivFlag)
					result.append("NullIF(").append(str).append(",0)");
				else  */
					result.append("COALESCE(").append(str).append(",0)");
				break;
			}
			}
		}
		if (Field.isChar()) {
			switch (DBType) {
			case 1: {
				result.setLength(0);
				result.append("ISNULL(").append(str).append(",'')");
				break;
			}
			case 2: {
				result.setLength(0);
				result.append("NVL(").append(str).append(",'')");
				break;
			}
			case 3: {
				result.setLength(0);
				result.append("COALESCE(").append(str).append(",'')");
				break;
			}
			}
		}
		return result.toString();
	}

	private boolean FindMenu(String str, HashMap Fields) {
		FieldItem Field;
		Iterator it = Fields.values().iterator();
		while (it.hasNext()) {
			Field = (FieldItem) it.next();
			if ((Field.getItemid().equals(str))
					|| (Field.getItemid().equals("现" + str))) {
				return true;
			}
		}
		return false;
	}

	private String SQL_GET(FieldItem Field, String strInteger, int nDirection) {
		// System.out.println("--->" + Field.getItemdesc());
		StringBuffer strSQL = new StringBuffer(), strAs = new StringBuffer(), strInto = new StringBuffer(), strCurSet = new StringBuffer(), strCurMenu = new StringBuffer(), strJOINField = new StringBuffer(), strMaxMin = new StringBuffer(), strDESC = new StringBuffer(), strSQLSet = new StringBuffer(), strSQLFrom = new StringBuffer(), strSQLLeft = new StringBuffer();
		strAs.setLength(0);
		String value = strInteger.trim();
		if (value.charAt(0) == '+' || value.charAt(0) == '-')
			value = value.substring(1);
		int ns = Integer.parseInt(value.trim());
		strAs.append("GET_").append(Field.getItemid()).append("_")
				.append(ns/* strInteger.trim() */).append("_");
		// strAs.append("GET_").append(Field.getItemid()).append("_");
		/** 去掉前面符号 */
		strInteger = value;

		strCurSet.setLength(0);
		// System.out.println(Field.getItemdesc());
		strCurSet.append(GetCurSet(Field));
		strCurMenu.setLength(0);
		strCurMenu.append(GetCurMenu(true, Field));
		// System.out.println(strCurMenu);
		if (nDirection == S_INCREASE) {
			strAs.append("I");
		} else {
			strAs.append("D");
		}
		if (FindMenu(strAs.toString(), mapUsedFieldItems)) {
			return strAs.toString();
		}
		// 此Get函数未出现过
		FieldItem Field1 = new FieldItem();
		Field1.setItemdesc(Field.getItemdesc());
		Field1.setItemid(Field.getItemid());
		Field1.setItemlength(Field.getItemlength());
		Field1.setDecimalwidth(Field.getDecimalwidth());
		Field1.setItemtype(Field.getItemtype());
		Field1.setCodesetid(Field.getCodesetid());
		Field1.setFieldsetid(Field.getFieldsetid());
		Field1.setVarible(2);// nIsVar := 2;

		Field1.setItemid(strAs.toString());
		mapUsedFieldItems.put(Field1.getItemid(), Field1);// UsedFields.Add(Field1);

		strInto.setLength(0);
		strInto.append("T_").append(TempTableName);
		if (DBType == Constant.MSSQL) {
			strInto.setLength(0);
			if (this.isTempTable)
				strInto.append("##T_").append(TempTableName);
			else
				strInto.append("T_").append(TempTableName);
		}

		String key="A0100";
		strJOINField.setLength(0);
		strJOINField.append(".").append(getKeyField(Field1.getFieldsetid()));
		key=getKeyField(Field1.getFieldsetid());
		
		if (nDirection == S_INCREASE) {
			strMaxMin.setLength(0);
			strMaxMin.append(" MAX");

			strDESC.setLength(0);
			strDESC.append(" ORDER BY ").append(strCurSet).append(strJOINField)
					.append(",").append(strCurSet).append(".I9999 ASC");
		} else {
			strMaxMin.setLength(0);
			strMaxMin.append(" MIN");

			strDESC.setLength(0);
			strDESC.append(" ORDER BY ").append(strCurSet).append(strJOINField)
					.append(",").append(strCurSet).append(".I9999 DESC");
		}
		if (DBType == Constant.MSSQL) {
			if (this.isTempTable)
				SQLS.add("DROP TABLE ##T_" + TempTableName);
			else
				SQLS.add("DROP TABLE T_" + TempTableName);
		} else {
		//	if (this.isTempTable)
		//		SQLS.add("TRUNCATE TABLE T_" + TempTableName);
			SQLS.add("DROP TABLE T_" + TempTableName+" purge");
		}

		if (DBType == 3) {
			strSQL.append("SELECT ").append(strCurSet).append(strJOINField)
					.append(",").append(strMaxMin).append("(")
					.append(strCurSet).append(".I9999) AS MAX_I9999");
			strSQL.append(" FROM ").append(strCurSet);
			strSQL.append(",").append(TempTableName).append(" WHERE ").append(
					strCurSet).append(strJOINField).append("=").append(
					TempTableName).append(strJOINField);
			strSQL.append(" AND (").append(strCurSet).append(
					".I9999 IN (SELECT ").append(strCurSet).append(".I9999");
			strSQL.append(" FROM ").append(strCurSet).append(" WHERE ").append(
					strCurSet).append(strJOINField).append("=").append(
					TempTableName).append(strJOINField).append(strDESC).append(
					" FETCH FIRST ").append(strInteger).append(" ROWS ONLY ))");
			strSQL.append(" GROUP BY ").append(strCurSet).append(strJOINField);
			strSQL.append(" HAVING COUNT(*)>=").append(strInteger);

			SQLS.add("CREATE TABLE " + strInto + " AS (" + strSQL.toString()
					+ ") DEFINITION ONLY");

			String strTemp = strSQL.toString();
			strSQL.setLength(0);
			strSQL.append("INSERT INTO ").append(strInto).append(" ").append(
					strTemp);
		} else if (DBType == Constant.ORACEL) {
			strSQL.setLength(0);
			strSQL.append("CREATE ");
			if (this.isTempTable)
				strSQL.append(" GLOBAL TEMPORARY ");
			strSQL.append(" TABLE ").append(strInto);
			if (this.isTempTable)
				strSQL.append("  On Commit Preserve Rows ");
			strSQL.append(" AS SELECT A").append(strJOINField).append(
					", A.I9999 AS MAX_I9999 ");
			strSQL.append("FROM ").append(
					"   (SELECT "+key+", I9999, ROWNUM rid FROM (SELECT ")
					.append(strCurSet).append(strJOINField).append(
							", I9999 FROM ").append(strCurSet).append(
							" ORDER BY "+key+", I9999) ) A,");
			strSQL.append("   (SELECT "+key+", I9999, ROWNUM rid FROM (SELECT ")
					.append(strCurSet).append(strJOINField).append(
							", I9999 FROM ").append(strCurSet).append(
							" ORDER BY "+key+", I9999) ) B,");
			strSQL.append("   ").append(TempTableName).append(" H ");
			strSQL.append("WHERE A").append(strJOINField).append("(+)=H")
					.append(strJOINField).append(" AND A").append(strJOINField)
					.append("=B").append(strJOINField);
			if (nDirection == S_INCREASE) {
				strSQL.append(" AND A.rid>=B.rid ");
			} else {
				strSQL.append(" AND A.rid<=B.rid ");
			}
			strSQL.append("GROUP BY A").append(strJOINField).append(
					",A.I9999 HAVING COUNT(*)=").append(strInteger);
			// {
			// strSQL:="CREATE TABLE "+ strInto + " AS ";
			// strSQL:=strSQL+"SELECT
			// "+strCurSet+strJOINField+","+strMaxMin+"("+strCurSet+".I9999) AS
			// MAX_I9999";
			// strSQL:=strSQL+" FROM "+ strCurSet;
			// strSQL:=strSQL+","+FTempTableName+" WHERE
			// "+strCurSet+strJOINField+"(+)="+FTempTableName+strJOINField;
			// strSQL:=strSQL+" AND "+ strCurSet+".I9999 IN (SELECT I9999 FROM
			// (SELECT " + strCurSet + ".I9999 ";
			// strSQL:=strSQL+" FROM "+ strCurSet+" WHERE
			// "+strCurSet+".A0100="+FTempTableName+".A0100";
			// strSQL:=strSQL+strDESC+") WHERE ROWNUM <="+strInteger+ ")";
			// strSQL:=strSQL+" GROUP BY "+ strCurSet+strJOINField;
			// strSQL:=strSQL+" HAVING COUNT(*)>="+strInteger;
			// }
		} else {
			strSQL.setLength(0);
			strSQL.append("SELECT ").append(strCurSet).append(strJOINField)
					.append(",").append(strMaxMin).append("(")
					.append(strCurSet).append(".I9999) AS MAX_I9999");
			strSQL.append(" INTO ").append(strInto.toString());
			strSQL.append(" FROM ").append(strCurSet);
			strSQL.append(" LEFT JOIN ").append(TempTableName).append(" ON ")
					.append(strCurSet).append(strJOINField).append("=").append(
							TempTableName).append(strJOINField);
			strSQL.append(" WHERE (").append(strCurSet).append(
					".I9999 IN (SELECT TOP ").append(strInteger).append(" ")
					.append(strCurSet).append(".I9999");
			strSQL.append(" FROM ").append(strCurSet).append(" WHERE ").append(
					strCurSet).append(strJOINField).append("=").append(
					TempTableName).append(strJOINField).append(strDESC + "))");
			strSQL.append(" GROUP BY ").append(strCurSet).append(strJOINField);
			strSQL.append(" HAVING COUNT(*)>=").append(strInteger);
		}
		SQLS.add(strSQL.toString());
		SQLS.add("create index "+strInto+"_index  on "+strInto+" ("+getKeyField(Field1.getFieldsetid())+",MAX_I9999)");
		 
		strSQL.setLength(0);
		strSQL.append("UPDATE ").append(TempTableName);
		if ((DBType == Constant.ORACEL) || (DBType == 3)) {
			strSQLSet.setLength(0);
			strSQLSet.append(" SET ").append(TempTableName).append(
					".I9999=(SELECT  ").append(strInto.toString()).append(
					".MAX_I9999");

			strSQLFrom.setLength(0);
			strSQLFrom.append(" FROM ").append(strInto.toString());

			strSQLLeft.setLength(0);
			strSQLLeft.append(" WHERE ").append(TempTableName).append(
					strJOINField).append("=").append(strInto.toString())
					.append(strJOINField).append(")");
		} else {
			strSQLSet.setLength(0);
			strSQLSet.append(" SET ").append(TempTableName).append(".I9999=")
					.append(strInto.toString()).append(".MAX_I9999");

			strSQLFrom.setLength(0);
			strSQLFrom.append(" FROM ").append(TempTableName);

			strSQLLeft.setLength(0);
			strSQLLeft.append(" LEFT JOIN ").append(strInto.toString()).append(
					" ON ").append(TempTableName).append(strJOINField).append(
					"=").append(strInto.toString()).append(strJOINField);
		}

		SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom).append(strSQLLeft)
				.toString());

		strSQL.setLength(0);
		strSQL.append("UPDATE ").append(TempTableName);

		// System.out.println(strCurMenu);
		if ((DBType == Constant.ORACEL) || (DBType == 3)) {
			strSQLSet.setLength(0);
			strSQLSet.append(" SET  ").append(strAs).append("=(SELECT ")
					.append(strCurMenu);

			strSQLFrom.setLength(0);
			strSQLFrom.append(" FROM ").append(strCurSet);

			strSQLLeft.setLength(0);
			strSQLLeft.append(" WHERE ").append(TempTableName).append(
					strJOINField).append("=").append(strCurSet).append(
					strJOINField).append(" AND ").append(TempTableName).append(
					".I9999=").append(strCurSet).append(".I9999)");
		} else {
			strSQLSet.setLength(0);
			strSQLSet.append(" SET  ").append(strAs).append("=").append(
					strCurMenu);

			strSQLFrom.setLength(0);
			strSQLFrom.append(" FROM ").append(TempTableName);

			strSQLLeft.setLength(0);
			strSQLLeft.append(" LEFT JOIN ").append(strCurSet).append(" ON ")
					.append(TempTableName).append(strJOINField).append("=")
					.append(strCurSet).append(strJOINField).append(" AND ")
					.append(TempTableName).append(".I9999=").append(strCurSet)
					.append(".I9999");
		}

		SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom).append(strSQLLeft)
				.toString());

		return strAs.toString();
	}

	private String SQL_SELECT(FieldItem field1, String strWhere, int nSQLtype) {
		StringBuffer strMaxMin = new StringBuffer();
		StringBuffer strKeyField = new StringBuffer();
		StringBuffer strAs = new StringBuffer();
		StringBuffer strInto = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strCurSetTableName = new StringBuffer();
		StringBuffer strCurMenu = new StringBuffer();
		boolean bExist = false;
		String result = "";

		// 加上库前缀
		strCurSetTableName.append(GetCurSet(field1));

		strCurMenu.append(field1.getItemid()); // GetCurMenu(TRUE,Field1);
		// //2003.4.22 Zdb工资累计

		if ("TRUE".equals(strWhere)) {
			strWhere = "1=1";
		}
		// Field1.cHz := Field1.cFldName +
		// '_'+trim(strWhere)+'_'+inttostr(nSQLtype);//是否有此SELECT函数
		field1.setItemdesc(field1.getItemid() + "_" + strWhere.trim() + "_"
				+ nSQLtype);// 是否有此SELECT函数

		// Field1.nIsVar :=2;
		field1.setVarible(2);

		Iterator it = mapUsedFieldItems.values().iterator();
		while (it.hasNext()) {
			FieldItem usedTemp = (FieldItem) it.next();
			if (field1.getItemdesc().equals(usedTemp.getItemdesc())) {
				field1.setItemid(usedTemp.getItemid());
				bExist = true;
			}
		}
		if (!bExist) {
			field1.setItemid("SELECT_" + (mapUsedFieldItems.size() + 1));
		}
		result = field1.getItemid();
		//20150110 dengcan
	    if (nSQLtype == S_COUNT) {
              field1.setItemtype("N"); 
              field1.setItemlength(10);
        }
		
	    if (bExist)
		{
			if ("N".equalsIgnoreCase(field1.getItemtype())
					&& result.indexOf("SELECT_") != -1)
				result = Sql_switcher.isnull(result, "0");
			return result;
		}
		mapUsedFieldItems.put(field1.getItemid(), field1);
/*
		if (nSQLtype == S_COUNT) {
			field1.setItemtype("N"); 
			field1.setItemlength(10);
		}*/
		strAs.setLength(0);
		strAs.append(field1.getItemid());

		strInto.setLength(0);
		strInto.append("T_").append(TempTableName);
		if (DBType == Constant.MSSQL) {
			strInto.setLength(0);
			if (this.isTempTable)
				strInto.append("##T_").append(TempTableName);
			else
				strInto.append("T_").append(TempTableName);
		}

		String _tempTable = "TT_" + TempTableName;
		if (DBType == Constant.MSSQL && this.isTempTable)
			_tempTable = "##TT_" + TempTableName;
		
	//	if(DBType==Constant.ORACEL)
	//		_tempTable = "TT_" + TempTableName+"_v";
		

		strKeyField.setLength(0);
		strKeyField.append(getKeyField(field1.getFieldsetid()));// Field1.cSetName);
		if (DBType == Constant.MSSQL) {
			SQLS.add("DROP TABLE " + strInto.toString());
		} else {
		//	if (this.isTempTable)
		//		SQLS.add("truncate table " + strInto.toString());
			SQLS.add("DROP TABLE " + strInto.toString()+" purge");
		}

		if (nSQLtype == S_FIRST) {
			strMaxMin.setLength(0);
			strMaxMin.append("MIN");
		} else if (nSQLtype == S_LAST) {
			strMaxMin.setLength(0);
			strMaxMin.append("MAX");
		}

		strSQL.setLength(0);
		strSQL.append("SELECT ").append(strCurSetTableName).append('.').append(
				strKeyField).append(',');
		// String dropTable = "";// 用来标志是否要删除临时表
		switch (nSQLtype) {
		case S_FIRST: {
			strSQL.append(" (").append(strCurMenu).append(')');
			break;
		}
		case S_LAST: {
			strSQL.append(" (").append(strCurMenu).append(')');
			break;
		}
		case S_MAX: {
			if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
				strSQL.append(" MAX(").append(
						strCurSetTableName + "." + strCurMenu).append(')');
			else
				strSQL.append(" MAX(").append(strCurMenu).append(')');
			break;
		}
		case S_MIN: {
			if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
				strSQL.append(" MIN(").append(
						strCurSetTableName + "." + strCurMenu).append(')');
			else
				strSQL.append(" MIN(").append(strCurMenu).append(')');
			break;
		}
		case S_SUM: {
			if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
				strSQL.append(" SUM(") // chenmengqing added
										// ./*strCurSetTableName + "." +*/
										// for统计函数去掉
						.append(strCurSetTableName + "." + strCurMenu).append(
								')');
			else
				strSQL.append(" SUM(") // chenmengqing added
										// ./*strCurSetTableName + "." +*/
										// for统计函数去掉
						.append(strCurMenu).append(')');
			break;
		}
		case S_AVG: {
			if (strCurMenu.length() == 5)// 根据指标名长度是否为5来区分是否需要加子集名
				strSQL.append(" AVG(").append(
						strCurSetTableName + "." + strCurMenu).append(')');
			else
				strSQL.append(" AVG(").append(strCurMenu).append(')');
			break;
		}
		}
		
		boolean isCreateTable=false;
		
		switch (nSQLtype) {
		case S_FIRST:
		case S_LAST: {

			
			if (DBType == Constant.MSSQL)
				SQLS.add("DROP TABLE " + _tempTable);
			else {
				SQLS.add("DROP TABLE " + _tempTable+" purge");
			}
			
		//	if (DBType == Constant.MSSQL||DBType ==Constant.DB2)
		//		SQLS.add("DROP TABLE " + _tempTable);
			
			if (DBType == 3) {
				strSQL.setLength(0);
				strSQL.append("CREATE TABLE T").append(strInto).append(
						" AS (SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append('(')
						.append(strCurSetTableName).append(".I9999) AS I9999");
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField).append(") DEFINITION ONLY");
				SQLS.add(strSQL.toString());

				strSQL.setLength(0);
				strSQL.append("INSERT INTO T").append(strInto).append(
						" SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append("(")
						.append(strCurSetTableName).append(".I9999) AS I9999");
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" LEFT JOIN ").append(TempTableName);
				strSQL.append(" ON ").append(strCurSetTableName).append(".")
						.append(strKeyField).append("=").append(TempTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
			} else if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				
			
				strSQL.append("CREATE ");
				if (this.isTempTable)
					strSQL.append(" GLOBAL TEMPORARY ");
				strSQL.append(" TABLE  ").append(_tempTable);
				if (this.isTempTable)
					strSQL.append(" On Commit Preserve Rows ");
			
			//	strSQL.append(" create or replace view ").append(_tempTable);
				strSQL.append(" AS SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append("(")
						.append(strCurSetTableName).append(".I9999) AS I9999")
						.append(" FROM ").append(strCurSetTableName);
				
				if(hasVarSelectCondition)//统计条件包含临时变量 20171017
					strSQL.append(",").append(TempTableName);

				/** 处理条件中涉及相关子集情况 */
				String sSetName;
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						
						if (!IsMainSet(sSetName))
						{
							StringBuffer _sDB=new StringBuffer("");
							_sDB.append(" ( SELECT "+sSetName+".*  FROM "+sSetName+", ");
							_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sSetName+" GROUP BY "+strKeyField+" ) AB ");
							_sDB.append(" WHERE "+sSetName+"."+strKeyField+"=AB."+strKeyField+" AND "+sSetName+".I9999=AB.I9999 )   "+sSetName);
							
							
							strSQL.append(",");
							strSQL.append(_sDB.toString());
						}
						else
						{
							strSQL.append(",");
							strSQL.append(sSetName);
						}
					}
				}

				strSQL.append(" WHERE 1=1 ");
				if(hasVarSelectCondition)//统计条件包含临时变量 20171017
				{
					strSQL.append(" AND "+strCurSetTableName).append(".")
							.append(strKeyField).append("=").append(TempTableName)
							.append(".").append(strKeyField);
				}
				/** 处理条件中涉及相关子集情况 */
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						strSQL.append(" AND ");
						strSQL.append(strCurSetTableName);
						strSQL.append(".");
						strSQL.append(strKeyField);
						strSQL.append("=");
						strSQL.append(sSetName);
						strSQL.append(".");
						strSQL.append(strKeyField);
					}
				}

				strSQL.append(" AND ").append(strWhere).append(" GROUP BY ")
						.append(strCurSetTableName).append(".").append(
								strKeyField);
			} else {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(
						"." + strKeyField + ",").append(strMaxMin).append("(")
						.append(strCurSetTableName).append(".I9999) AS I9999");
				strSQL.append(" INTO ").append(_tempTable);
				strSQL.append(" FROM ").append(strCurSetTableName);
				/** 处理条件中涉及相关子集情况 */
				String sSetName;
				if (this.UsedSets.size() >= 2) {
					for (int k = 0; k < UsedSets.size(); k++) {
						sSetName = (String) UsedSets.get(k);
						if (Field.isPerson())
							sSetName = DbPre + UsedSets.get(k);
						if (sSetName.equalsIgnoreCase(strCurSetTableName
								.toString()))
							continue;
						if (strWhere.indexOf(sSetName) == -1)
							continue;
						
						StringBuffer _sDB=new StringBuffer("");
						if (!IsMainSet(sSetName))
						{
							_sDB.append(" ( SELECT "+sSetName+".*  FROM "+sSetName+", ");
							_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sSetName+" GROUP BY "+strKeyField+" ) AB ");
							_sDB.append(" WHERE "+sSetName+"."+strKeyField+"=AB."+strKeyField+" AND "+sSetName+".I9999=AB.I9999 )  "+sSetName);
							
						}
						else
							_sDB.append(sSetName);
						
						strSQL.append(" LEFT JOIN ");
				//		strSQL.append(sSetName);
						strSQL.append(_sDB.toString());
						strSQL.append(" ON ");
						strSQL.append(strCurSetTableName);
						strSQL.append(".");
						strSQL.append(strKeyField);
						strSQL.append("=");
						strSQL.append(sSetName);
						strSQL.append(".");
						strSQL.append(strKeyField);
					}
				}
				/*
				 * if UsedSets.Count >= 2 then begin for i := 0 to
				 * UsedSets.Count -1 do begin sDB:=UsedSets[i]; if
				 * Field.cSetName[1]='A' then sDB:=FDBPre+UsedSets[i]; if
				 * sDB=strCurSetTableName then Continue; if Pos(sDB, strWhere)=0
				 * then Continue; strSQL := strSQL + ' LEFT Join '+ sDB + ' ON ' +
				 * strCurSetTableName+'.'+strKeyField+'='+sDB+'.'+ strKeyField+' ';
				 * end; end;
				 */
				if(hasVarSelectCondition)//统计条件包含临时变量 20171017
				{
					strSQL.append(" LEFT JOIN ").append(TempTableName);
					strSQL.append(" ON ").append(strCurSetTableName).append(".")
							.append(strKeyField).append("=").append(TempTableName)
							.append(".").append(strKeyField);
				}
				
				strSQL.append(" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
			}
			SQLS.add(strSQL.toString());
		//	if(DBType!=Constant.ORACEL)
			SQLS.add("create index "+_tempTable+"_index  on "+_tempTable+" ("+getKeyField(field1.getFieldsetid())+",I9999)");
			 
			if (DBType == 3) {
				strSQL.setLength(0);
				strSQL.append("CREATE TABLE ").append(strInto).append(
						" AS (SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField).append(",").append(" (").append(
								strCurMenu).append(")").append(" AS ").append(
								strAs);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(",T").append(strInto);
				strSQL.append(" WHERE T").append(strInto).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" AND ").append(strCurSetTableName).append(
						".I9999=T").append(strInto).append(
						".I9999) DEFINITION ONLY");
				SQLS.add(strSQL.toString());

				strSQL.setLength(0);
				strSQL.append("INSERT INTO ").append(strInto)
						.append(" SELECT ").append(strCurSetTableName).append(
								".").append(strKeyField).append(",").append(
								" (").append(strCurMenu).append(")").append(
								" AS ").append(strAs);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(",T").append(strInto);
				strSQL.append(" WHERE T").append(strInto).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" AND ").append(strCurSetTableName).append(
						".I9999=T").append(strInto).append(".I9999");
			} else if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				strSQL.append("CREATE ");
				if (this.isTempTable)
					strSQL.append(" GLOBAL TEMPORARY ");
				strSQL.append(" TABLE ").append(strInto);
				if (this.isTempTable)
					strSQL.append(" On Commit Preserve Rows ");
				strSQL.append(" AS (SELECT ").append(strCurSetTableName)
						.append(".").append(strKeyField).append(",").append(
								" (").append(strCurMenu).append(")").append(
								" AS ").append(strAs);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(",").append(_tempTable);
				strSQL.append(" WHERE ").append(_tempTable).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" AND ").append(strCurSetTableName).append(
						".I9999=").append(_tempTable).append(".I9999)");
			} else {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField).append(",").append(" (").append(
								strCurMenu).append(")").append(" AS ").append(
								strAs).append(" INTO ").append(strInto);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" LEFT JOIN  ").append(_tempTable);
				strSQL.append(" ON ").append(_tempTable).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strCurSetTableName).append(
						".I9999=").append(_tempTable).append(".I9999");
			}
			
			isCreateTable=true;
			break;
		}
		case S_MAX:
		case S_MIN:
		case S_SUM:
			if (DBType == 3) {

				strSQL.setLength(0);
				strSQL.append(" AS ").append(strAs);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" LEFT JOIN ").append(TempTableName);
				strSQL.append(" ON ").append(TempTableName).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
				// String str = strSQL.toString();
				strSQL.setLength(0);
				strSQL.append("INSERT INTO ").append(strInto).append(" ")
						.append(strSQL);

			} else {
				if (DBType == Constant.ORACEL) {
					// System.out.println(strInto);
					// SQLS.add("drop table " + TempTableName);
					StringBuffer buffer = new StringBuffer(strCurSetTableName
							.toString());
					if (UsedSets.size() >= 2) {
						for (int i = 0; i < UsedSets.size(); i++) {
							String sDB = (String) UsedSets.get(i);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a') {

								sDB = this.getDbPreTable(sDB);
							}
							if (sDB.equals(strCurSetTableName.toString())) {
								continue;
							}
							
							StringBuffer _sDB=new StringBuffer("");
							if (!IsMainSet(sDB))
							{
								_sDB.append(" ( SELECT "+sDB+".*  FROM "+sDB+", ");
								_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sDB+" GROUP BY "+strKeyField+" ) AB ");
								_sDB.append(" WHERE "+sDB+"."+strKeyField+"=AB."+strKeyField+" AND "+sDB+".I9999=AB.I9999 )  "+sDB);
							}
							else
								_sDB.append(sDB);
							buffer.append(" LEFT Join " + _sDB.toString() + " ON "
									+ strCurSetTableName + '.' + strKeyField
									+ '=' + sDB + '.' + strKeyField + " ");
						}
					} 
					
					if(hasVarSelectCondition)//统计条件包含临时变量 20171017
					{
						buffer.append(" LEFT JOIN ").append(TempTableName);
						buffer.append(" ON ").append(TempTableName).append(".")
								.append(strKeyField).append("=").append(
										strCurSetTableName).append(".").append(
										strKeyField);
					}
					
					// System.out.println(strSQL);
					// strSQL.setLength(0);
					strSQL.append(" AS ").append(strAs);
					strSQL.append(" FROM ").append(buffer);
					strSQL.append(" WHERE ");
					strSQL.append(TempTableName);
					strSQL.append(".");
					strSQL.append(/* " WHERE temp_su." + */strKeyField + "="
							+ strCurSetTableName + "." + strKeyField + " and "
							+ strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);

					String str = " update " + " " + TempTableName + " set ("
							+ strKeyField + "," + strAs + ")=( "
							+ strSQL.toString() + ")  ";
					// if (UsedSets.size()<2)
					str += "  where exists  ( select null "
							+ strSQL.substring(strSQL.indexOf(" FROM")) + ") ";
					strSQL.setLength(0);
					strSQL.append(str);
				} else {
					StringBuffer buffer = new StringBuffer(strCurSetTableName
							.toString());
					if (UsedSets.size() >= 2) {
						for (int i = 0; i < UsedSets.size(); i++) {
							String sDB = (String) UsedSets.get(i);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a') {

								sDB = this.getDbPreTable(sDB);
							}
							if (strCurSetTableName.toString().toLowerCase().indexOf(sDB.toLowerCase()) != -1) {
								continue;
							}
							
							StringBuffer _sDB=new StringBuffer("");
							if (!IsMainSet(sDB))
							{
								_sDB.append(" ( SELECT "+sDB+".*  FROM "+sDB+", ");
								_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sDB+" GROUP BY "+strKeyField+" ) AB ");
								_sDB.append(" WHERE "+sDB+"."+strKeyField+"=AB."+strKeyField+" AND "+sDB+".I9999=AB.I9999 )   "+sDB);
							}
							else
								_sDB.append(sDB);
							buffer.append(" LEFT Join " + _sDB.toString() + " ON "
									+ strCurSetTableName + '.' + strKeyField
									+ '=' + sDB + '.' + strKeyField + " ");
						}
					} 
					
					if(hasVarSelectCondition)//统计条件包含临时变量 20171017
					{
						buffer.append(" LEFT JOIN ").append(TempTableName);
						buffer.append(" ON ").append(TempTableName).append(".")
								.append(strKeyField).append("=").append(
										strCurSetTableName).append(".").append(
										strKeyField);
					}
					strSQL.append(" AS ").append(strAs).append(" INTO ")
							.append(strInto);
					strSQL.append(" FROM ");
					strSQL.append(buffer);
					strSQL.append(" WHERE ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
					isCreateTable=true;
				}
				break;
			}
		case S_AVG: {
			if (DBType == 3) {
				strSQL.setLength(0);
				strSQL.append(" AS ").append(strAs);
				strSQL.append(" FROM ").append(strCurSetTableName);
				strSQL.append(" LEFT JOIN ").append(TempTableName);
				strSQL.append(" ON ").append(TempTableName).append(".").append(
						strKeyField).append("=").append(strCurSetTableName)
						.append(".").append(strKeyField);
				strSQL.append(" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);

				SQLS.add("CREATE TABLE " + strInto.toString() + " AS ("
						+ strSQL.toString() + ") DEFINITION ONLY");

				strSQL.setLength(0);
				strSQL.append("INSERT INTO ").append(strInto).append(" ")
						.append(strSQL);
				isCreateTable=true;
			} else {
				if (DBType == Constant.ORACEL) {
					isCreateTable=true;
					if (this.isTempTable)
						strSQL.insert(0, "CREATE   GLOBAL TEMPORARY  TABLE   "
								+ strInto + "  On Commit Preserve Rows   AS ");
					else
						strSQL
								.insert(0, "CREATE   TABLE   " + strInto
										+ " AS ");

					strSQL.append(" AS ").append(strAs);// .append(" INTO
														// ").append(strInto);
					strSQL.append(" FROM ").append(strCurSetTableName);
					strSQL.append(" ,").append(TempTableName);

					/** 处理条件中涉及相关子集情况 */
					String sSetName;
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.isPerson())
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							
							StringBuffer _sDB=new StringBuffer("");
							if (!IsMainSet(sSetName))
							{
								_sDB.append(" ( SELECT "+sSetName+".*  FROM "+sSetName+", ");
								_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sSetName+" GROUP BY "+strKeyField+" ) AB ");
								_sDB.append(" WHERE "+sSetName+"."+strKeyField+"=AB."+strKeyField+" AND "+sSetName+".I9999=AB.I9999 )   "+sSetName);
							}
							else
								_sDB.append(sSetName);
							
							strSQL.append(",");
						//	strSQL.append(sSetName);
							strSQL.append(_sDB.toString());
						}
					}
					strSQL.append(" WHERE ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);

					/** 处理条件中涉及相关子集情况 */
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a')
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							strSQL.append(" AND ");
							strSQL.append(strCurSetTableName);
							strSQL.append(".");
							strSQL.append(strKeyField);
							strSQL.append("=");
							strSQL.append(sSetName);
							strSQL.append(".");
							strSQL.append(strKeyField);
						}
					}

					strSQL.append(" AND ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
				} else {
					isCreateTable=true;
					strSQL.append(" AS ").append(strAs).append(" INTO ")
							.append(strInto);
					strSQL.append(" FROM ").append(strCurSetTableName);
					strSQL.append(" LEFT JOIN ").append(TempTableName);
					strSQL.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);

					/** 处理条件中涉及相关子集情况 */
					String sSetName;
					if (this.UsedSets.size() >= 2) {
						for (int k = 0; k < UsedSets.size(); k++) {
							sSetName = (String) UsedSets.get(k);
							if (Field.getFieldsetid().charAt(0) == 'A'
									|| Field.getFieldsetid().charAt(0) == 'a')
								sSetName = DbPre + UsedSets.get(k);
							if (sSetName.equalsIgnoreCase(strCurSetTableName
									.toString()))
								continue;
							if (strWhere.indexOf(sSetName) == -1)
								continue;
							
							StringBuffer _sDB=new StringBuffer("");
							if (!IsMainSet(sSetName))
							{
								_sDB.append(" ( SELECT "+sSetName+".*  FROM "+sSetName+", ");
								_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sSetName+" GROUP BY "+strKeyField+" ) AB ");
								_sDB.append(" WHERE "+sSetName+"."+strKeyField+"=AB."+strKeyField+" AND "+sSetName+".I9999=AB.I9999 )   "+sSetName);
							}
							else
								_sDB.append(sSetName);
							
							
							
							strSQL.append(" LEFT JOIN ");
							strSQL.append(_sDB.toString());
							//	strSQL.append(sSetName);
							strSQL.append(" ON ");
							strSQL.append(strCurSetTableName);
							strSQL.append(".");
							strSQL.append(strKeyField);
							strSQL.append("=");
							strSQL.append(sSetName);
							strSQL.append(".");
							strSQL.append(strKeyField);
						}
					}
					strSQL.append(" WHERE ").append(strWhere);
					strSQL.append(" GROUP BY ").append(strCurSetTableName)
							.append(".").append(strKeyField);
				}
			}
			break;
		}
			// 统计个数时不能与主集左联结，否则没有记录时统计成1
		case S_COUNT: {

			if (DBType == 3) {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField).append(",COUNT(*) AS ").append(
								strAs);
				strSQL.append(" FROM ").append(strCurSetTableName).append(
						" WHERE ").append(strWhere);
				strSQL.append(" GROUP BY ").append(strKeyField);
				SQLS.add("CREATE TABLE " + strInto.toString() + " AS ("
						+ strSQL.toString() + ")DEFINITION ONLY");

				strSQL.setLength(0);
				strSQL.append("INSERT INTO ").append(strInto).append(" ")
						.append(strSQL);
			} else if (DBType == Constant.ORACEL) {
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField).append(",COUNT(*) AS ").append(
								strAs);

				StringBuffer buffer = new StringBuffer(strCurSetTableName
						.toString());
				if (UsedSets.size() >= 2) {
					for (int i = 0; i < UsedSets.size(); i++) {
						String sDB = (String) UsedSets.get(i);
						if (Field.getFieldsetid().charAt(0) == 'A'
								|| Field.getFieldsetid().charAt(0) == 'a') {

							sDB = this.getDbPreTable(sDB);
						}
						if (sDB.equals(strCurSetTableName.toString())) {
							continue;
						}
						
						StringBuffer _sDB=new StringBuffer("");
						if (!IsMainSet(sDB))
						{
							_sDB.append(" ( SELECT "+sDB+".*  FROM "+sDB+", ");
							_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sDB+" GROUP BY "+strKeyField+" ) AB ");
							_sDB.append(" WHERE "+sDB+"."+strKeyField+"=AB."+strKeyField+" AND "+sDB+".I9999=AB.I9999 )   "+sDB);
						}
						else
							_sDB.append(sDB);
						buffer.append(" LEFT Join " + _sDB.toString() + " ON "
								+ strCurSetTableName + '.' + strKeyField + '='
								+ sDB + '.' + strKeyField + " ");
					}
				}
				
				if(hasVarSelectCondition)//统计条件包含临时变量 20171017
				{
					buffer.append(" LEFT JOIN ").append(TempTableName);
					buffer.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);
				}

				strSQL.append(" FROM ").append(buffer.toString()).append(
						" WHERE ").append(strWhere);
				// strSQL.append(" GROUP BY ").append(strKeyField);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);

				if (this.isTempTable)
					SQLS.add("CREATE  GLOBAL TEMPORARY TABLE "
							+ strInto.toString()
							+ " On Commit Preserve Rows   AS ("
							+ strSQL.toString() + ")");
				else
					SQLS.add("CREATE   TABLE " + strInto.toString() + "  AS ("
							+ strSQL.toString() + ")");
				isCreateTable=true;
				
			} else {
				isCreateTable=true;
				strSQL.setLength(0);
				strSQL.append("SELECT ").append(strCurSetTableName).append(".")
						.append(strKeyField).append(",COUNT(*) AS ").append(
								strAs).append(" INTO ").append(strInto);

				StringBuffer buffer = new StringBuffer(strCurSetTableName
						.toString());
				if (UsedSets.size() >= 2) {
					for (int i = 0; i < UsedSets.size(); i++) {
						String sDB = (String) UsedSets.get(i);
						if (Field.getFieldsetid().charAt(0) == 'A'
								|| Field.getFieldsetid().charAt(0) == 'a') {

							sDB = this.getDbPreTable(sDB);
						}
						if (sDB.equals(strCurSetTableName.toString())) {
							continue;
						}
						
						StringBuffer _sDB=new StringBuffer("");
						if (!IsMainSet(sDB))
						{
							_sDB.append(" ( SELECT "+sDB+".*  FROM "+sDB+", ");
							_sDB.append("(SELECT "+strKeyField+",MAX(I9999) I9999  FROM "+sDB+" GROUP BY "+strKeyField+" ) AB ");
							_sDB.append(" WHERE "+sDB+"."+strKeyField+"=AB."+strKeyField+" AND "+sDB+".I9999=AB.I9999 )   "+sDB);
						}
						else
							_sDB.append(sDB);
						buffer.append(" LEFT Join " + _sDB.toString() + " ON "
								+ strCurSetTableName + '.' + strKeyField + '='
								+ sDB + '.' + strKeyField + " ");
					}
				}
				
				if(hasVarSelectCondition)//统计条件包含临时变量 20171017
				{
					buffer.append(" LEFT JOIN ").append(TempTableName);
					buffer.append(" ON ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(
									strCurSetTableName).append(".").append(
									strKeyField);
				}
				strSQL.append(" FROM ").append(buffer.toString()).append(
						" WHERE ").append(strWhere);
				// strSQL.append(" GROUP BY ").append(strKeyField);
				strSQL.append(" GROUP BY ").append(strCurSetTableName).append(
						".").append(strKeyField);
			}
			break;
		}
		}
		SQLS.add(strSQL.toString());
	 	if(isCreateTable)
	 		SQLS.add("create index "+strInto+"_index  on "+strInto+" ("+strKeyField+")");
		 
		
		strSQL.setLength(0);
		switch (DBType) {
		case 2:
			if (!(nSQLtype == S_SUM || nSQLtype == S_MAX || nSQLtype == S_MIN)) {
				strSQL.setLength(0);
				strSQL.append("UPDATE ").append(TempTableName);
				strSQL.append(" SET ").append(TempTableName).append(".")
						.append(strAs).append("=(SELECT ").append(strInto)
						.append(".").append(strAs);
				strSQL.append(" FROM ").append(strInto);
				strSQL.append(" WHERE ").append(TempTableName).append(".")
						.append(strKeyField).append("=").append(strInto)
						.append(".").append(strKeyField).append(")");
			}
			break;
		case 3: {
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName);
			strSQL.append(" SET ").append(TempTableName).append(".").append(
					strAs).append("=(SELECT ").append(strInto).append(".")
					.append(strAs);
			strSQL.append(" FROM ").append(strInto);
			strSQL.append(" WHERE ").append(TempTableName).append(".").append(
					strKeyField).append("=").append(strInto).append(".")
					.append(strKeyField).append(")");
			break;
		}
		case 1: {
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName);
			strSQL.append(" SET ").append(TempTableName).append(".").append(strAs);
			if("D".equalsIgnoreCase(field1.getItemtype())) {//如果isnull(日期,'')日期型为空的会被置为1900-01-01，这里对日期型的不再处理
				strSQL.append("=").append(strInto).append(".")
				.append(strAs);
			}else if("N".equalsIgnoreCase(field1.getItemtype())) {//isnull(数值,0)
				strSQL.append("=ISNULL(").append(strInto).append(".")
				.append(strAs).append(",0)");
			}else {
				strSQL.append("=ISNULL(").append(strInto).append(".")
				.append(strAs).append(",'')");
			}
			strSQL.append(" FROM ").append(TempTableName).append(" LEFT JOIN ")
					.append(strInto);
			strSQL.append(" ON ").append(TempTableName).append(".").append(
					strKeyField).append("=").append(strInto).append(".")
					.append(strKeyField);
			break;
		}
		case 0: {
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName)
					.append(" LEFT JOIN ").append(strInto);
			strSQL.append(" ON ").append(TempTableName).append(".").append(
					strKeyField).append("=").append(strInto).append(".")
					.append(strKeyField);
			strSQL.append(" SET ").append(TempTableName).append(".").append(
					strAs).append("=").append(strInto).append(".")
					.append(strAs);
			break;
		}
		}
		if (!"".equals(strSQL.toString()))
			SQLS.add(strSQL.toString());

		if ("N".equalsIgnoreCase(field1.getItemtype())
				&& result.indexOf("SELECT_") != -1)
			result = Sql_switcher.isnull(result, "0");
		return result;
	}

	private String GetCodeItemTableName(String strCodeSet) {
		if (("UN".equals(strCodeSet)) || ("UM".equals(strCodeSet))
				|| ("@K".equals(strCodeSet))) {
			return "ORGANIZATION";
		} else {
			return "CODEITEM";
		}
	}

	private String SQL_CTON(FieldItem Field) {
		String Codeid = "", Codeitem = "";
		FieldItem Field1 = new FieldItem();

		StringBuffer strSQL = new StringBuffer(), strAs = new StringBuffer(), strKeyField = new StringBuffer(), strSetTableName = new StringBuffer(), strFrom = new StringBuffer(), strLeft = new StringBuffer(), strSet = new StringBuffer();

		Codeid = Field.getCodesetid();
		Codeitem = GetCodeItemTableName(Codeid);

		if (ModeFlag == forSearch) {// then //从库中取数据
			strAs.setLength(0);
			strAs.append("CTON_").append(Field.getItemid());

			strKeyField.setLength(0);
			strKeyField.append(getKeyField(Field.getFieldsetid()));

			strSetTableName.setLength(0);
			strSetTableName.append(GetCurSet(Field));

			if (FindMenu(strAs.toString(), mapUsedFieldItems)) {
				return strAs.toString();
			}

			// FieldCopy(Field,Field1);
			Field1 = (FieldItem) Field.cloneItem();// ?????????????????????
			Field1.setItemid(strAs.toString());
			Field1.setVarible(2);// nIsVar := 2;
			/** 代码转字符函数,cmq added */
			if("UN".equalsIgnoreCase(Codeid))
				Field1.setItemlength(255);
			else
				Field1.setItemlength(70);
			// USedFields.Add(Field1);
			mapUsedFieldItems.put(Field1.getItemid(), Field1);
			String _tempTable = "T_" + TempTableName;
			if (DBType == Constant.MSSQL && this.isTempTable)
				_tempTable = "##T_" + TempTableName;
			if (!IsMainSet(Field.getItemid())) {// then //处理最后一条记录
				if (DBType == Constant.MSSQL) {
					if (this.isTempTable)
						SQLS.add("DROP TABLE ##T_" + TempTableName);
					else
						SQLS.add("DROP TABLE T_" + TempTableName);
				} else {
				//	if (this.isTempTable)
				//		SQLS.add("truncate table T_" + TempTableName);
					SQLS.add("DROP TABLE T_" + TempTableName+" purge");
				}

				if (DBType == 3) {
					strSQL.setLength(0);
					strSQL.append("CREATE TABLE T_").append(TempTableName)
							.append(" AS (SELECT ").append(strKeyField).append(
									",MAX(I9999) AS MAX_I9999 ");
					strSQL.append(" FROM ").append(strSetTableName);
					strSQL.append(" GROUP BY ").append(strKeyField).append(
							") DEFINITION ONLY");
					SQLS.add(strSQL.toString());
				 
					
					strSQL.setLength(0);
					strSQL.append("INSERT INTO T_").append(TempTableName)
							.append(" SELECT ").append(strKeyField).append(
									",MAX(I9999) AS MAX_I9999 ");
					strSQL.append(" FROM ").append(strSetTableName);
					strSQL.append(" GROUP BY ").append(strKeyField);
					SQLS.add(strSQL.toString());
					
					SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
					 
					
				} else if (DBType == Constant.ORACEL) {
					strSQL.setLength(0);
					strSQL.append("CREATE ");
					if (this.isTempTable)
						strSQL.append(" GLOBAL TEMPORARY ");
					strSQL.append(" TABLE  T_").append(TempTableName);
					if (this.isTempTable)
						strSQL.append("  On Commit Preserve Rows ");
					strSQL.append(" AS (SELECT ").append(strKeyField).append(
							",MAX(I9999) AS MAX_I9999 ");
					strSQL.append(" FROM ").append(strSetTableName);
					strSQL.append(" GROUP BY ").append(strKeyField)
							.append(") ");
					SQLS.add(strSQL.toString());
				 
					SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
					 
				} else {
					strSQL.setLength(0);
					if (this.isTempTable) {
						strSQL.append("SELECT ").append(strKeyField).append(
								",MAX(I9999) AS MAX_I9999 INTO ##T_").append(
								TempTableName);
					} else {
						strSQL.append("SELECT ").append(strKeyField).append(
								",MAX(I9999) AS MAX_I9999 INTO T_").append(
								TempTableName);
					}
					strSQL.append(" FROM ").append(strSetTableName);
					strSQL.append(" GROUP BY ").append(strKeyField);
					SQLS.add(strSQL.toString());
					if (this.isTempTable) 
						SQLS.add("create index T_"+TempTableName+"_index  on ##T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
					else
						SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKeyField+",MAX_I9999)");
				 
				}
				strSQL.setLength(0);
				strSQL.append("UPDATE ").append(TempTableName);
				if ((DBType == Constant.ORACEL) || (DBType == 3)) {
					strLeft.setLength(0);
					strLeft.append(" WHERE ").append(TempTableName).append(".")
							.append(strKeyField).append("=").append(_tempTable)
							.append(".").append(strKeyField).append(")");

					strSet.setLength(0);
					strSet.append(" SET ").append(TempTableName).append(
							".I9999=(SELECT ").append(_tempTable).append(
							".MAX_I9999 ");

					strFrom.setLength(0);
					strFrom.append(" From ").append(_tempTable);
				} else {
					strLeft.setLength(0);
					strLeft.append(" LEFT JOIN ").append(_tempTable).append(
							" ON ").append(TempTableName).append(".").append(
							strKeyField).append("=").append(_tempTable).append(
							".").append(strKeyField);

					strSet.setLength(0);
					strSet.append(" SET ").append(TempTableName).append(
							".I9999=").append(_tempTable).append(".MAX_I9999");

					strFrom.setLength(0);
					strFrom.append(" From ").append(TempTableName);
				}

				switch (DBType) {
				case 3:
				case 2: {
					strSQL.append(strSet).append(strFrom).append(strLeft);
					break;
				}
				case 1: {
					strSQL.append(strSet).append(strFrom).append(strLeft);
					break;
				}
				case 0: {
					strSQL.append(strLeft).append(strSet);
				}
				}
				SQLS.add(strSQL.toString());
			}
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName); // 将内容拷到临时表中
			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strLeft.setLength(0);
				strLeft.append(" WHERE ").append(TempTableName).append(".")
						.append(strKeyField).append("=")
						.append(strSetTableName).append(".")
						.append(strKeyField);
				if (!IsMainSet(Field.getFieldsetid())) {// Is Sub Set
					strLeft.append(" AND ").append(TempTableName).append(
							".I9999=").append(strSetTableName)
							.append(".I9999)");
				} else {
					strLeft.append(")");
				}
				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(".CTON_")
						.append(Field.getItemid()).append("=(SELECT ").append(
								strSetTableName).append(".").append(
								Field.getItemid());

				strFrom.setLength(0);
				strFrom.append(" From ").append(strSetTableName);
			} else {
				strLeft.setLength(0);
				strLeft.append(" LEFT JOIN ").append(strSetTableName).append(
						" ON ").append(TempTableName).append(".").append(
						strKeyField).append("=").append(strSetTableName)
						.append(".").append(strKeyField);
				if (!IsMainSet(Field.getFieldsetid())) {// Is Sub Set
					strLeft.append(" AND ").append(TempTableName).append(
							".I9999=").append(strSetTableName).append(".I9999");
				}
				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(".CTON_")
						.append(Field.getItemid()).append("=").append(
								strSetTableName).append(".").append(
								Field.getItemid());

				strFrom.setLength(0);
				strFrom.append(" From ").append(TempTableName);

			}

			switch (DBType) {
			case 3:
			case 2: {
				strSQL.append(strSet).append(strFrom).append(strLeft);
				break;
			}
			case 1: {
				strSQL.append(strSet).append(strFrom).append(strLeft);
				break;
			}
			case 0: {
				strSQL.append(strLeft).append(strSet);
				break;
			}
			}
			SQLS.add(strSQL.toString());
			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName); // 将代码转成名称
			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strLeft.setLength(0);
				strLeft.append(" WHERE ").append(TempTableName)
						.append(".CTON_").append(Field.getItemid()).append("=")
						.append(Codeitem).append(".CODEITEMID");

				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(".CTON_")
						.append(Field.getItemid()).append("=(SELECT ").append(
								Codeitem).append(".CODEITEMDESC");

				strFrom.setLength(0);
				strFrom.append(" FROM ").append(Codeitem);
			} else {
				strLeft.setLength(0);
				strLeft.append(" LEFT JOIN ").append(Codeitem).append(" ON ")
						.append(TempTableName).append(".CTON_").append(
								Field.getItemid()).append("=").append(Codeitem)
						.append(".CODEITEMID");

				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(".CTON_")
						.append(Field.getItemid()).append("=").append(Codeitem)
						.append(".CODEITEMDESC");

				strFrom.setLength(0);
				strFrom.append(" FROM ").append(TempTableName);
			}

			strSQL.append(strSet).append(strFrom).append(strLeft);

			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strSQL.append(" AND ").append(Codeitem).append(".CODESETID =")
						.append("'").append(Codeid).append("')");
				// WJH 2013-12-11 Oracle库需要加WHERE，否则部门赋值，单位时就清空了。
				strSQL.append(" where Exists(SELECT 1 FROM ").append(Codeitem).append(strLeft)
					.append(" AND ").append(Codeitem).append(".CODESETID =").append("'").append(Codeid).append("')");
			} else {
				strSQL.append(" WHERE ").append(Codeitem)
						.append(".CODESETID =").append("'").append(Codeid)
						.append("'");
			}
			SQLS.add(strSQL.toString());
			if("UM".equalsIgnoreCase(Codeid))
				SQLS.add(strSQL.toString().replaceAll("UM","UN"));
			result =Sql_switcher.isnull("CTON_" + Field.getItemid(),"' '");
		} else // 单表
		{
			String sConFld = "CTON_" + FCTONSQLS.size();
			String strR ="";
			
			if (DBType == Constant.MSSQL)
				strR = sConFld + Sql_switcher.getFieldType('M', 200, 0);
			else
			{
				if("UN".equalsIgnoreCase(Codeid))
					strR = sConFld + Sql_switcher.getFieldType('A', 200, 0);
				else
					strR = sConFld + Sql_switcher.getFieldType('A', 70, 0);
			}
			 
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(sConFld);
			FCTONSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" add ");
			strSQL.append(strR);
			FCTONSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			strSQL.append("UPDATE ").append(TempTableName); // 将代码转成名称
			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strLeft.setLength(0);
				strLeft.append(" WHERE ").append(TempTableName).append(".")
						.append(Field.getItemid()).append("=").append(Codeitem)
						.append(".CODEITEMID");

				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(
						".LLEEFFTT=(SELECT ").append(Codeitem).append(
						".CODEITEMDESC");

				strFrom.setLength(0);
				strFrom.append(" FROM ").append(Codeitem);
			} else {
				strLeft.setLength(0);
				strLeft.append(" LEFT JOIN ").append(Codeitem).append(" ON ")
						.append(TempTableName).append(".").append(
								Field.getItemid()).append("=").append(Codeitem)
						.append(".CODEITEMID");

				strSet.setLength(0);
				strSet.append(" SET ").append(TempTableName).append(
						".LLEEFFTT=").append(Codeitem).append(".CODEITEMDESC");

				strFrom.setLength(0);
				strFrom.append(" FROM ").append(TempTableName);
			}
			strSQL.append(strSet).append(strFrom).append(strLeft);

			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strSQL.append(" AND ").append(Codeitem).append(".CODESETID =")
						.append("'").append(Codeid).append("')");
			} else {
				strSQL.append(" WHERE ").append(Codeitem)
						.append(".CODESETID =").append("'").append(Codeid)
						.append("'");
			}
			FCTONSQLS.add(strSQL.toString());
			replaceCTONLeft(sConFld);
			result = sConFld;
			
			if (DBType == Constant.MSSQL&&ModeFlag==this.forNormal)
			{
				if("UN".equalsIgnoreCase(Codeid)|| "UM".equalsIgnoreCase(Codeid))
				{
					result = "CAST("+sConFld+" as varchar(200))";
				}
				else
				{
					result = "CAST("+sConFld+" as varchar(70))";
				}
			}
			
		}
		return result;
	}

	/**
	 * 
	 * @param strExpr
	 *            表达式
	 * @param Codeid
	 *            相关代码类
	 * @return
	 */
	private String SQL_CTON2(String strExpr, String Codeid,int layNum,String splitSign ) {
		/** 代码表名称 */
		String Codeitem = GetCodeItemTableName(Codeid);
		StringBuffer strSQL = new StringBuffer(), strFrom = new StringBuffer(), strLeft = new StringBuffer(), strSet = new StringBuffer();

	//	if (ModeFlag == forSearch) {// 

	//	} else// 单表
		{
			String sConFld = "CTON_" + FCTONSQLS.size();
			
			
			
			String strR ="";
			if (DBType == Constant.MSSQL&&ModeFlag==this.forNormal)
				strR = sConFld + Sql_switcher.getFieldType('M', 200, 0);
			else
			{
				if("UN".equalsIgnoreCase(Codeid)|| "UM".equalsIgnoreCase(Codeid))
					strR=sConFld + Sql_switcher.getFieldType('A', 200, 0);
				else
					strR=sConFld + Sql_switcher.getFieldType('A', 70, 0);
			}
			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" drop column ");
			strSQL.append(sConFld);
			FCTONSQLS.add(strSQL.toString());
			
			FieldItem field1 = new FieldItem();
		    field1.setItemid(sConFld);
			field1.setItemdesc("代码转名称2"); 
			field1.setDecimalwidth(0);
			
			if (DBType == Constant.MSSQL&&ModeFlag==this.forNormal)
			{
				field1.setItemtype("M");
			}
			else
			{
				if("UN".equalsIgnoreCase(Codeid)|| "UM".equalsIgnoreCase(Codeid))
					field1.setItemlength(200);
				else
					field1.setItemlength(70); 
				field1.setItemtype("A");
			}
			
			
			field1.setVarible(2);
			field1.setCodesetid("0");
			mapUsedFieldItems.put(sConFld, field1);
			usedFieldMap.put(sConFld, field1);

			strSQL.setLength(0);
			strSQL.append("alter table ");
			strSQL.append(TempTableName);
			strSQL.append(" add ");
			strSQL.append(strR);
			FCTONSQLS.add(strSQL.toString());

			strSQL.setLength(0);
			StringBuffer strAs=new StringBuffer("");
			if(layNum==1)
			{
					strSQL.append("UPDATE ").append(TempTableName); // 将代码转成名称
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strLeft.setLength(0);
						strLeft.append(" WHERE ").append(strExpr).append("=").append(
								Codeitem).append(".CODEITEMID");
		
						strSet.setLength(0);
						strSet.append(" SET ").append(TempTableName).append(
								".LLEEFFTT=(SELECT ").append(Codeitem).append(
								".CODEITEMDESC");
		
						strFrom.setLength(0);
						strFrom.append(" FROM ").append(Codeitem);
					} else {
						strLeft.setLength(0);
						strLeft.append(" LEFT JOIN ").append(Codeitem).append(" ON ")
								.append(strExpr).append("=").append(Codeitem).append(
										".CODEITEMID");
		
						strSet.setLength(0);
						strSet.append(" SET ").append(TempTableName).append(
								".LLEEFFTT=").append(Codeitem).append(".CODEITEMDESC");
		
						strFrom.setLength(0);
						strFrom.append(" FROM ").append(TempTableName);
					}
					strSQL.append(strSet).append(strFrom).append(strLeft);
		
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSQL.append(" AND ").append(Codeitem).append(".CODESETID =")
								.append("'").append(Codeid).append("')");
					} else {
						strSQL.append(" WHERE ").append(Codeitem)
								.append(".CODESETID =").append("'").append(Codeid)
								.append("'");
					} 
			}
			else
			{
				strSQL.append("UPDATE ").append(TempTableName); // 将代码转成名称
				strSet.setLength(0);
				strSet.append("(SELECT O.Id, A.codeitemdesc,"+Sql_switcher.isnull("A.layer","1")+"  layer FROM (SELECT Id FROM (SELECT "+strExpr+" AS Id FROM "+TempTableName+") A GROUP BY ID) O, "+Codeitem+" A ");
				strSet.append(" WHERE codesetid='"+Codeid+"' AND A.codeitemid=Id) M "); //, [sExpr, FTempTableName, Codeitem, CodeID]);
				
				 
				if ( DBType != 2 ) {
					 String str="";
					 if (splitSign.length()==0)
					 {
						 strAs.append(" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+M.codeitemdesc end AS codeitemdesc ");
						 str=" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+M.codeitemdesc end ";
					 }
			         else
			         {
			        	 strAs.append(" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+'"+splitSign+"'+M.codeitemdesc end AS codeitemdesc ");
			        	 str=" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+'"+splitSign+"'+M.codeitemdesc end ";
			         }
					 for(int i=1;i<layNum;i++)
					 {
						 String  temp_str= " (SELECT Id,"+strAs.toString()+",MIN("+Sql_switcher.isnull("A.layer","1")+") layer FROM "+Codeitem+" A,"+strSet.toString() ;
						 temp_str+=" WHERE codesetid='"+Codeid+"' and LEFT(Id, len(codeitemid))=codeitemid and ("+Sql_switcher.isnull("A.layer","1")+"=M.layer-1 or M.layer=1 ) GROUP BY ID,"+str+" ) M ";
						 strSet.setLength(0);
						 strSet.append(temp_str);
					 }
					 strSQL.append(" SET " + TempTableName + ".LLEEFFTT=M.CODEITEMDESC  FROM ");
					 strSQL.append(strSet.toString()+"  WHERE M.Id="+strExpr);
				}
				else
				{
						String str="";
					    if (splitSign.length()==0)
					    {
					    	strAs.append(" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+M.codeitemdesc end AS codeitemdesc ");
					    	str=" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc+M.codeitemdesc end ";
					    }
				         else
				         {
				        	 strAs.append(" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc||'"+splitSign+"'||M.codeitemdesc end AS codeitemdesc ");
				        	 str=" case M.layer when 1 then M.codeitemdesc else A.codeitemdesc||'"+splitSign+"'||M.codeitemdesc end ";
				         }
					 for(int i=1;i<layNum;i++)
					 {
						 String  temp_str= " (SELECT Id,"+strAs.toString()+", MIN("+Sql_switcher.isnull("A.layer","1")+") layer FROM "+Codeitem+" A,"+strSet.toString() ;
						 temp_str+=" WHERE codesetid='"+Codeid+"' and SUBSTR(Id,1, LENGTH(codeitemid))=codeitemid and ("+Sql_switcher.isnull("A.layer","1")+"=M.layer-1 or M.layer=1 ) GROUP BY ID,"+str+" ) M ";
						 strSet.setLength(0);
						 strSet.append(temp_str);
					 }
					 strSQL.append(" SET " + TempTableName + ".LLEEFFTT=(SELECT M.CODEITEMDESC  FROM ");
					 strSQL.append(strSet.toString()+"  WHERE M.Id="+strExpr+" ) ");
				} 
				
			}
			 
			FCTONSQLS.add(strSQL.toString());
			replaceCTONLeft(sConFld);
			result = sConFld;
			
			if (DBType == Constant.MSSQL&&ModeFlag==this.forNormal)
			{
				if("UN".equalsIgnoreCase(Codeid)|| "UM".equalsIgnoreCase(Codeid))
				{
					result = "CAST("+sConFld+" as varchar(200))";
				}
				else
				{
					result = "CAST("+sConFld+" as varchar(70))";
				}
			}

		}
		return result;
	}

	/**
	 * 
	 * @param strLeft
	 */
	private void replaceCTONLeft(String strLeft) {
		for (int i = 0; i < FCTONSQLS.size(); i++) {
			String tmp = (String) FCTONSQLS.get(i);
			tmp = tmp.replaceAll("LLEEFFTT", strLeft);
			FCTONSQLS.set(i, tmp);
		}// for i loop end.
	}

	public String getSQL() {
		return SQL.toString();
	}

	public ArrayList getSQLS() {
		return SQLS;
	}

	public void init() {
		FError = false;// 错误标志
		UsedSets.clear();// 所用到的子集编号setid
		mapUsedFieldItems.clear();// 所用到的指标代号
		SQLS.clear();//
		SQL.setLength(0);// 待返回生成的查询语句
		setStrError("");// 清空错误信息
		FCTONSQLS.clear();// 代码转名称函数用到的Sql串
		/** 代码调整及就近就高函数 */
		FSTDSQLS.clear();
		StdFieldList.clear();
		budgetGroupCheckFld = null;
	}

	/**
	 * 公式校验函数
	 * 
	 * @param str
	 *            待校验的公式
	 * @return 公式正确与否 注意：如果校验发现公式错误，需要调用getStrError()获取相关错误信息
	 */
	public boolean Verify(String str) {
		try {
			bVerify = true;
			run(str);
		} catch (GeneralException e) {
			Category.getInstance(this.getClass()).debug(e.getMessage());
			return false;
		} catch (SQLException e) {
			// e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param str
	 *            表达式
	 * @param ymc
	 *            年月次对象
	 * @param targetField
	 *            操作字段
	 * @param targetTable
	 *            操作表名称
	 * @param dao
	 * @param whereText
	 *            过滤条件
	 * @param con
	 *            数据库连结
	 * @param targetFieldDataType
	 *            字段类型
	 * @param targetFieldLen
	 *            字段长度
	 * @param flg
	 *            1.是高级花名册 2.是报表变量 3.条件过滤  4.人事异动计算  5.薪资计算
	 * @param codeset
	 *            代码编号
	 * @return
	 */
	public String run(String str, YearMonthCount ymc, String targetField,
			String targetTable, ContentDAO dao, String whereText,
			Connection con, String targetFieldDataType, int targetFieldLen,
			int flg, String codeset) {
		this.codeset = codeset;
		String s = "";
		this.flg = flg;
		this.targetTable = targetTable;
		this.targetField = targetField;
		// this.setStdTmpTable(targetTable);
		this.targetFieldLen = targetFieldLen;
		this.ymc = ymc;
		this.dao = dao;
		this.con = con;
		this.setWhereText(whereText);
		this.targetFieldDataType = targetFieldDataType;
		try {
		 	if((this.flg==2)&&this.isSupportVar)  //报表支持临时变量嵌套临时变量
		 		excecuteVarTable(str,"");
		 	
			s = run(str);
		} catch (Exception e) {
			 e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
		}
		return s;
	}

	/**
	 * 
	 * @param str
	 *            表达式
	 * @param ymc
	 *            年月次对象
	 * @param targetField
	 *            操作字段
	 * @param targetTable
	 *            操作表名称
	 * @param dao
	 * @param whereText
	 *            过滤条件
	 * @param con
	 *            数据库连结
	 * @param targetFieldDataType
	 *            字段类型
	 * @param targetFieldLen
	 *            字段长度
	 * @param targetFieldDecimal
	 *            小数位
	 * @param flg
	 *            1.是高级花名册 2.是报表变量 3.条件过滤
	 * @param codeset
	 *            代码编号
	 * @return
	 */
	public String run(String str, YearMonthCount ymc, String targetField,
			String targetTable, ContentDAO dao, String whereText,
			Connection con, String targetFieldDataType, int targetFieldLen,
			int targetFieldDecimal, int flg, String codeset) {
		this.codeset = codeset;
		String s = "";
		this.flg = flg;
		this.targetTable = targetTable;
		this.targetField = targetField;
		// this.setStdTmpTable(targetTable);
		this.targetFieldLen = targetFieldLen;
		if ("N".equalsIgnoreCase(targetFieldDataType))
			this.targetFieldDecimal = targetFieldDecimal;
		this.ymc = ymc;
		this.dao = dao;
		this.con = con;
		this.setWhereText(whereText);
		this.targetFieldDataType = targetFieldDataType;
		try {
			log.info("计算公式：{}",str);
			s = run(str);
		} catch (Exception e) {
			// e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
		}
		return s;
	}

	/**
	 * 调用者通过getSQLS()取得sql子句，temp_XXX临时表中，不会产生目标表子集中涉及到的字段，便与调用者自个组装sql语句，解决计算历史记录问题
	 * （调用模块:信息维护模块中的计算历史记录）
	 * 
	 * @param str
	 * @param targetField
	 *            操作字段
	 * @param targetTable
	 *            操作表名称
	 * @return
	 * @author dengcan 2008-11-28
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public String run(String str, String targetTable) throws GeneralException,
			SQLException {
		String s = "";
		try {
			s = run(str);
			DbWizard dbw = new DbWizard(this.con);
			Table table = new Table(TempTableName);
			ContentDAO dao = new ContentDAO(this.con);
			RowSet rowSet = dao.search("select * from " + this.TempTableName
					+ " where 1=2");
			ResultSetMetaData data = rowSet.getMetaData();
			ArrayList filedList = DataDictionary.getFieldList(targetTable
					.toLowerCase(), Constant.USED_FIELD_SET);
			int num = 0;
			for (int i = 0; i < data.getColumnCount(); i++) {
				String columnName = data.getColumnName(i + 1);
				if ("i9999".equalsIgnoreCase(columnName)
						|| "a0100".equalsIgnoreCase(columnName)
						|| "e01a1".equalsIgnoreCase(columnName)
						|| "b0110".equalsIgnoreCase(columnName))
					continue;
				for (int j = 0; j < filedList.size(); j++) {
					FieldItem fielditem = (FieldItem) filedList.get(j);
					if (fielditem.getItemid().equalsIgnoreCase(columnName)) { 
						if(str.indexOf("取部门值(")!=-1&&("UM".equalsIgnoreCase(fielditem.getCodesetid()) || "UN".equalsIgnoreCase(fielditem.getCodesetid()))) // 20150624 dengcan 人员信息维护计算公式: 取部门值(部门,单位考核名称) 报错
							break; 
						table.addField(fielditem);
						num++;
						break;
					}
				}
			}
			if (num > 0)
				dbw.dropColumns(table);
			data = null;
			rowSet.close();
		} catch (Exception e) {
		//	e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
		}

		return s;
	}
	
	
	/**
	 * run函数用到 where条件限制，
	 * @param str
	 * @param where_str
	 * @param exists   true:用到存在函数     false:用到 in函数
	 * @return
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public String run_bywhere(String str,String where_str) throws GeneralException, SQLException 
	{
		String s="";  
		this.setWhereText(where_str);
		try {
			s = run(str);

		} catch (Exception e) {
			// e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return s;
	}
	
	

	/**
	 * 通用公式解析函数 注意，该函数不会执行复杂公式(临时表数据更新) 需要调用者通过getSQLS()获取后自行执行更新 简单公式无需关注临时表更新
	 * 
	 * @param str
	 *            待解析的公式
	 * @return 模拟计算公式的结果，该值一般不能直接使用，实际的解析结果sql使用getSQL()方法获取
	 * @throws SQLException
	 */
	public String run(String str) throws GeneralException, SQLException {
		str = PubFunc.keyWord_reback(str);
		
		if (Sql_switcher.searchDbServer()== Constant.ORACEL)
			isTempTable = false;
		this.wordDay_num=1;
		this.GroupStatNum=1;
		RetValue retValue = new RetValue();
		str = str.replaceAll("单位编码", "单位名称");
	/*	str = str.replaceAll("职位编码", "职位名称");
		// cmq 把职位名称换成"岗位名称" at 20091222
		if (ResourceFactory.getProperty("e01a1.label").equalsIgnoreCase("岗位名称")) {
			str = str.replaceAll("职位名称", "岗位名称");
		} */
		setFSource(str);
		init();// 清空初始化

		if ((TempTableName == null) && (ModeFlag == forSearch)) {
			SError(E_LOSSTEMPTABLENAME);
		}

		if (!Get_Expr(retValue)) {
			return null;
		}

		if (ModeFlag == forSearch) {
			createTempTable();
			SQL_SubSet();
	 		run_SQLS();
			run_STDSQL();// ?临时变量中存在执行标准函数
		 
			
			
			run_CTONSQL(); //代码转换函数
			
			if (getSQL().length() > 0)// chenmengqing added 20070115
				excuteSql();

		}
		/** 执行代码转换函数 */
		if (ModeFlag == forNormal) {
			run_CTONSQL();
			/** 代码调整, */
			run_STDSQL();
			
			if(sequenceParamBean!=null)  //序号函数
			{
				Func_Sequence_exc(retValue);
			}
			
			
		}

		if (bVerify)
			return "";
		if (retValue.isIntType() || retValue.isFloatType()) {
			if (VarType != FLOAT && VarType != INT) {
				SError(E_FNOTSAMETYPE);
			}
		}

		// public static int INT=5;
		// public static int FLOAT=6;
		// public static int STRVALUE=7;
		// public static int LOGIC=8;
		// public static int DATEVALUE=9;
		// public static int NULLVALUE=10;
		// public static int ODDVAR=11;
		// System.out.println(retValue.getValueType());
		if (retValue.IsDateType()) {
			if (VarType != DATEVALUE) {
				SError(E_FNOTSAMETYPE);
			}
		}
		if (retValue.IsStringType()) {
			if (VarType != STRVALUE) {
				SError(E_FNOTSAMETYPE);
			}
		}
		if (retValue.isBooleanType()) {
			if (VarType != LOGIC) {
				SError(E_FNOTSAMETYPE);
			}
		}
		resultDataType=retValue.getTypeString();
		ResultString = retValue.ValueToString();
		// System.out.println(UsedSets);
		return result;
	}

	/**
	 * chenmengqing added. forNormal，单表计算
	 * 
	 * @param str
	 *            计算公式
	 * @param conn
	 *            数据库连接
	 * @param strWhere
	 *            计算条件
	 * @param tempName
	 *            临时表的名称
	 * @return 返回转换后的SQL表达式
	 * @throws GeneralException
	 */
	public String run(String str, Connection conn, String strWhere,
			String tempName) throws GeneralException {
		String s = "";
		this.setWhereText(strWhere);
		this.setTempTableName(tempName);
		this.setStdTmpTable(tempName);
		this.con = conn;
		try {
			s = run(str);

		} catch (Exception e) {
			// e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return s;
	}

	/**
	 * 执行代码转名称函数,forNormal状态
	 * 
	 * @throws GeneralException
	 */
	private void run_CTONSQL() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.con);
		int idx = 0;
		try {
			/** 计算条件串 */
			int len = this.whereText.length();

			if (FCTONSQLS == null || FCTONSQLS.size() == 0)
				return;
			DbWizard dbw=new DbWizard(this.con);
			for (int i = 0; i < FCTONSQLS.size(); i++) {
				String tmp = ((String) (FCTONSQLS.get(i))).toUpperCase();
				idx = tmp.indexOf("ALTER");
				if (idx == -1) {
					if (len > 2)// (条件)
					{
						if (ModeFlag == forSearch)
						{
							if (InfoGroupFlag == forPerson) {
								if ((DBType == Constant.ORACEL) || (DBType == 3))
									tmp = tmp + " where A0100 in " + this.whereText;
								else
									tmp = tmp + " and A0100 in " + this.whereText;
							} else if (InfoGroupFlag == forUnit) {
								if ((DBType == Constant.ORACEL) || (DBType == 3))
									tmp = tmp + " where B0110 in " + this.whereText;
								else
									tmp = tmp + " and B0110 in " + this.whereText;
							} else if (InfoGroupFlag == forPosition) {
								if ((DBType == Constant.ORACEL) || (DBType == 3))
									tmp = tmp + " where E01A1 in " + this.whereText;
								else
									tmp = tmp + " and E01A1 in " + this.whereText;
							}
						}
						else 
						{
							if ((DBType == Constant.ORACEL) || (DBType == 3)) {
								tmp = tmp + " where " + this.whereText;
							} else {
								tmp = tmp + " and " + this.whereText;
							}
							
							
							
						//	if(tmp.toUpperCase().indexOf("WHERE")==-1)
						//		tmp = tmp + " where " + this.whereText;
						//	else
						//		tmp = tmp + " and " + this.whereText;
						}
					}
				}
				
				try {
					if (brun(tmp, dbw))
					{
				//	  	System.out.println("------"+tmp); 
						dao.update(tmp);
					}
					
				} catch (Exception ex) {
					ex.printStackTrace();
					String message=ex.toString();
					if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
					{
						throw GeneralExceptionHandler.Handle(ex);
					}
					
				}
			}// for i loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

	/**
	 * 执行SQL语句
	 * 
	 * @param strSQL
	 */
	private boolean run_SQL(String strSQL) {
		ContentDAO dao = new ContentDAO(this.con);
		boolean bflag = false;
		try {
			dao.update(strSQL);
			bflag = true;
		} catch (Exception ex) {
			;// ex.printStackTrace();
		}
		return bflag;
	}

	/**
	 * 执行标准或代码调整
	 * 
	 * @throws GeneralException
	 */
	private void run_STDSQL() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.con);
		DbWizard dbw = new DbWizard(this.con);
		int idx = 0;
		try {
			/** 计算条件串 */
			if (this.whereText == null)
				this.whereText = "";
			int len = this.whereText.length();

			if (FSTDSQLS == null || FSTDSQLS.size() == 0)
				return;
			for (int i = 0; i < FSTDSQLS.size(); i++) {
				String tmp = ((String) (FSTDSQLS.get(i))).toUpperCase();
				// idx=tmp.indexOf("ALTER");
				// if((tmp.indexOf("ALTER")==-1)||(tmp.indexOf("DROP")==-1))
				// //调整代码函数
				// {
				// if(len>2)//(条件)
				// tmp=tmp+" and "+this.whereText;
				// }
				try { 
					if (brun(tmp, dbw)) {
	//				 	 System.out.println(this.FSource+"---"+tmp);
						dao.update(tmp);
					}
				} catch (Exception ex) {
					 //ex.printStackTrace();
					 throw GeneralExceptionHandler.Handle(ex);
				}
			}// for i loop end.
		} catch (Exception ex) {
			ex.printStackTrace();
			String message=ex.toString();
			if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
			{
				throw GeneralExceptionHandler.Handle(ex);
			}
		}
	}

	// 判断如果sql语句是 删除某字段功能，如果表中没有此字段，就不执行该sql语句
	public boolean isDropColumnFiled(String sql, ContentDAO dao) {
		boolean flag = true;
		String temp_sql = sql.toLowerCase();
		try {
			if (temp_sql.indexOf("drop column") != -1) {
				int s = temp_sql.indexOf("alter table");
				int e = temp_sql.indexOf("drop column");
				if (s != -1 && e != -1) {
					String table_name = temp_sql.substring(s + 11, e).trim();
					String field = temp_sql.substring(e + 11).trim();

					RowSet rowSet = dao.search("select * from " + table_name
							+ " where 1=2");
					ResultSetMetaData mt = rowSet.getMetaData();
					boolean isValue = false;
					for (int i = 0; i < mt.getColumnCount(); i++) {
						if (mt.getColumnName(i + 1).equalsIgnoreCase(field))
							isValue = true;

					}
					flag = isValue;
					mt = null;
					rowSet.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag = true;
		}
		return flag;
	}

	/**
	 * 取得公式涉及到的指标信息列表
	 * 
	 * @param str
	 *            公式
	 * @author dengcan
	 * @serialData 2007.06.08
	 * @return
	 */
	public ArrayList getFormulaFieldList(String str) throws GeneralException,
			SQLException {
		
	 
		this.isVerify = true; // 不参与真正计算
		ArrayList list = new ArrayList();
		RetValue retValue = new RetValue();
		setFSource(str);
		init();// 清空初始化

		if ((TempTableName == null) && (ModeFlag == forSearch)) {
			SError(E_LOSSTEMPTABLENAME);
		}

		if (!Get_Expr(retValue)) {
			return null;
		}
		HashMap setMap = new HashMap();

		if (ModeFlag == forSearch) {
			Collection collection = mapUsedFieldItems.values();
			Iterator it = collection.iterator();
			while (it.hasNext()) {
				FieldItem fieldItem1 = (FieldItem) it.next();
				if (!"B0110".equalsIgnoreCase(fieldItem1.getItemid())
						&& !"E0122".equalsIgnoreCase(fieldItem1.getItemid())
						&& !"E01A1".equalsIgnoreCase(fieldItem1.getItemid())) {

					setMap.put(fieldItem1.getFieldsetid().toLowerCase(), "1");
					Field a_field = fieldItem1.cloneField();
					a_field.setLength(50);
					list.add(fieldItem1);
				}
			}

		}
		if (setMap.size() > 1 && this.isSingleTalbe)
			list = new ArrayList();

		return list;
	}

	/**
	 * 取得公式涉及到的指标信息列表
	 * 
	 * @param str
	 *            公式
	 * @author dengcan
	 * @serialData 2007.06.08
	 * @return
	 */
	public ArrayList getFormulaFieldList1(String str) throws GeneralException,
			SQLException {
		ArrayList list = new ArrayList();
		RetValue retValue = new RetValue();
		setFSource(str);
		init();// 清空初始化
        this.isVerify=true;
		if ((TempTableName == null) && (ModeFlag == forSearch)) {
			SError(E_LOSSTEMPTABLENAME);
		}

		if (!Get_Expr(retValue)) {
			return null;
		}
		HashMap setMap = new HashMap();

		if (ModeFlag == forSearch) {
			Collection collection = mapUsedFieldItems.values();
			Iterator it = collection.iterator();
			while (it.hasNext()) {
				FieldItem fieldItem1 = (FieldItem) it.next();
				// if (!fieldItem1.getItemid().equalsIgnoreCase("B0110")
				// && !fieldItem1.getItemid().equalsIgnoreCase("E0122")
				// && !fieldItem1.getItemid().equalsIgnoreCase("E01A1")) {

				// setMap.put(fieldItem1.getFieldsetid().toLowerCase(),"1");
				Field a_field = fieldItem1.cloneField();
				a_field.setLength(50);
				list.add(fieldItem1);
				// }
			}

		}
		if (setMap.size() > 1)
			list = new ArrayList();

		return list;
	}

	public boolean Get_Expr(RetValue retValue) throws GeneralException,
			SQLException {

		if (!Get_Token())
			return false;
		if (!level0(retValue))
			return false;
		Putback();

		if (nCurPos != nFSourceLen) {
			SError(E_SYNTAX);
			return false;
		}
		return true;
	}

	private void Putback() {
		nCurPos = nCurPos - token.length();
	}

	private boolean level0(RetValue retValue) throws GeneralException,
			SQLException {
		RetValue hold = new RetValue();
		int Op = 0;
		if (!level1(retValue)) {
			return false;
		}

		while ((tok == S_AND) || (tok == S_OR)) {
			Op = tok;
			if (tok == S_AND) {
				SQL.append(" AND ");
			} else {
				SQL.append(" OR ");
			}
			if (!Get_Token())
				return false;
			if (!level1(hold))
				return false;
			if (!retValue.isBooleanType() && hold.isBooleanType()&&(retValue.getValueType()!=NULLVALUE&&hold.getValueType()!=NULLVALUE)) {
				SError(E_MUSTBEBOOL);
				return false;
			}
			if (!Arith(Op, retValue, hold))
				return false;
		}
		return true;
	}

	/**
	 * 数学操作
	 * 
	 * @param op
	 * @param retValue
	 * @param hold
	 * @return
	 * @throws GeneralException
	 */
	public boolean Arith(int op, RetValue retValue, RetValue hold)
			throws GeneralException {

		/*
		 * Object obj1 = retValue.getValue(); if (obj1 instanceof Integer) {
		 * Integer i = (Integer)obj1; System.out.println("obj1_int" +
		 * i.intValue());
		 *  } else if (obj1 instanceof Float) { Float f = (Float)obj1;
		 * System.out.println("obj1_float=" + f.floatValue()); }
		 * 
		 * Object obj2 = hold.getValue(); if (obj2 instanceof Integer) { Integer
		 * i = (Integer)obj2; System.out.println("obj2_int" + i.intValue());
		 *  } else if (obj2 instanceof Float) { Float f = (Float)obj2;
		 * System.out.println("obj2_float=" + f.floatValue()); }
		 */
		// System.out.println(retValue.getValue() + ":" +
		// retValue.getValueType()
		// + " " + hold.getValue() + ":" + hold.getValueType());
		if (!(retValue.IsSameType(hold) || retValue.IsNullType() || hold
				.IsNullType())) {
			SError(E_NOTSAMETYPE);
			return false;
		}
		switch (op) {
		case S_ADD: {
			retValue.add(hold);
			break;
		}
		case S_MINUS: {
			retValue.minus(hold);
			break;
		}
		case S_MULTIPLY: {
			retValue.multiply(hold);
			break;
		}
		case S_DIVISION: {
			retValue.division(hold);
			break;
		}
		case S_DIV: {
			if (!retValue.Div(hold)) {
				SError(E_MUSTBEINTEGER);
				return false;
			}
			break;
		}
		case S_MOD: {
			if (!retValue.Mod(hold)) {
				SError(E_MUSTBEINTEGER);
				return false;
			}
			break;
		}
		case S_IN:
		case S_LIKE: {
			// in (8,9,10)
			if(op==S_LIKE)
			{
			 	if (!retValue.IsStringType()) {
					SError(E_MUSTBESTRING);
					return false;
				} 
			}
			retValue.setValue(new Boolean(true));
			retValue.setValueType(8);
			break;
		}
		case S_AND: {
			if (!retValue.And(hold)) {
				SError(E_MUSTBEBOOL);
				return false;
			}
			break;
		}
		case S_OR: {
			if (!retValue.Or(hold)) {
				SError(E_MUSTBEBOOL);
				return false;
			}
			break;
		}
		case S_EQUAL: {
			if (!retValue.Equal(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}
		case S_NOTEQUAL: {
			if (!retValue.NotEqual(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}
		case S_GREATER: {
			if (!retValue.Greater(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}
		case S_NOTGREATER: {
			if (!retValue.NotGreater(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}
		case S_SMALLER: {
			if (!retValue.Smaller(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}
		case S_NOTSMALLER: {
			if (!retValue.NotSmaller(hold)) {
				SError(E_NOTSAMETYPE);
				return false;
			}
			break;
		}

		default:
			break;
		}
		return true;
	}

	private void SQL_ADD(String str) {
		SQL.append(" ");
		SQL.append(str);
	}

	boolean isNull_MSSQL=false;  // 公式定义 xxxx=空   ||  xxxx<>空   20141102  dengcan
	private boolean level1(RetValue retValue) throws GeneralException,
			SQLException {
		int nOp = 0;
		RetValue hold = new RetValue();
		String str="";
		String current_sql=this.SQL.toString();
		String sql_str="";
		this.SQL.setLength(0);
		if (!level2(retValue))
			return false;
		RetValue factorRetValue=(RetValue)retValue.clone();
		sql_str=this.SQL.toString();
		this.SQL.setLength(0);
		this.SQL.append(current_sql+sql_str); 
		
		this.isNull_MSSQL=false;   //20141102  dengcan 
		if ((tok == S_LIKE) || (tok == S_IN)) {
			if(tok == S_IN)
				isInFunc=true;
			nOp = tok;
			SQL_ADD(" "+token+" ");
			if (!Get_Token())
				return false;
			if(nOp == S_LIKE)
			{
				this.token=this.token.replaceAll("\\*","%");
				this.token=this.token.replaceAll("\\?","_");
			} 
			
			
			
			if (!level2(hold))
				return false;
			if (!Arith(nOp, retValue, hold))
				return false;
		}
		if ((tok == S_EQUAL) || (tok == S_GREATER) || (tok == S_SMALLER)) {
			
			switch (tok) {
			case S_EQUAL: { 
				nOp = S_EQUAL;
				boolean tokenIsNull=false; //是否=空   20141103 dengcan   如果 拟审批意见备注=空
				if (!Get_Token())
					return false;
		 		if (("NULL".equalsIgnoreCase(token)) || ("空".equals(token)&&token_type!=QUOTE)|| (this.DBType == Constant.ORACEL && "".equals(token))) {
					if(factorRetValue.getValueType()==this.STRVALUE&&this.DBType == Constant.MSSQL) //20141102  dengcan
					{
						this.isNull_MSSQL=true;  
						SQL_ADD("=");
					}
					else
						SQL_ADD("IS");
					
					if(this.DBType==Constant.MSSQL&&factorRetValue.getValueType()==this.MEMO)   //20141103 dengcan   如果 拟审批意见备注=空
					{
						this.SQL.setLength(0);
						this.SQL.append(current_sql+" ( "+sql_str+" IS "); 
						tokenIsNull=true;
					}
		 		
		 		} else {
					if(factorRetValue.getValueType()==this.STRVALUE&&this.DBType == Constant.MSSQL) //20141102  dengcan
					{
						if ("NULL".equalsIgnoreCase(token)|| "空".equals(token)|| "".equals(token))
							this.isNull_MSSQL=true;  
					}
					SQL_ADD("=");
				}
				if (!level2(hold))
					return false;
				this.isNull_MSSQL=false;   //20141102  dengcan
				
				if (!Arith(nOp, retValue, hold))
					return false;
				
				if(tokenIsNull)   //20141103 dengcan   如果 拟审批意见备注=空
				{
					this.SQL.append(" or CAST("+sql_str+" as varchar(200))='' ");  
					this.SQL.append(" ) ");
				}
				
				break;
			}
			case S_GREATER: {
				if (!Get_Token())
					return false;
				nOp = S_GREATER;
				if (tok == S_EQUAL) {
					SQL_ADD(">=");
					if (!Get_Token())
						return false;
					nOp = S_NOTEQUAL;
				} else {
					SQL_ADD(">");
				}
				if (!level2(hold))
					return false;
				if (!Arith(nOp, retValue, hold)) {
					return false;
				}
				break;
			}
			case S_SMALLER: {
				
				this.SQL.setLength(0);
				this.SQL.append(current_sql+" ( "+sql_str); 
				
				
				if (!Get_Token())
					return false;
				nOp = S_SMALLER;
				boolean tokenIsNull=false; //是否<>空
				if ((tok == S_EQUAL) || (tok == S_GREATER)) {
					nOp = S_NOTEQUAL;
					str = "<" + token;
					if (tok == S_EQUAL) {
						nOp = S_NOTGREATER;
					}
					if (!Get_Token())
						return false;
					
					if ("NULL".equalsIgnoreCase(token)||  "空".equals(token)|| "".equals(token))
					{
						tokenIsNull=true; 
					}
					
				 	if ("NULL".equalsIgnoreCase(token)|| ("空".equals(token))|| (this.DBType == Constant.ORACEL && "".equals(token))) {
				 		if(factorRetValue.getValueType()==this.STRVALUE&&this.DBType == Constant.MSSQL) //20141102  dengcan
						{
				 			this.isNull_MSSQL=true;  
							SQL_ADD(str);
						}
						else
							SQL_ADD("IS NOT");
					} else {
						if(factorRetValue.getValueType()==this.STRVALUE&&this.DBType == Constant.MSSQL) //20141102  dengcan
						{
							if ("NULL".equalsIgnoreCase(token)|| "空".equals(token)|| "".equals(token))
								this.isNull_MSSQL=true;  
						}
						SQL_ADD(str);
					}
				} else {
					SQL_ADD("<");
				}
				 
				if (!level2(hold))
					return false;
				
				this.isNull_MSSQL=false;   //20141102  dengcan
				//如果条件定义的是  xxxxx<>'aaaa',需将为空值的数据也包含在内
				if("<>".equalsIgnoreCase(str)&&!tokenIsNull)
				{
					/* 2017-7-5 zhanghua 在oracle情况下 如果sql_str返回值为数字型 不能使用nullif(sql_str,'') 将其与空字符串做比较。
					if(Sql_switcher.searchDbServer()==2)
						this.SQL.append(" or nullif("+sql_str+",'') is null ");
					else */
					if(factorRetValue.getValueType()==this.STRVALUE||factorRetValue.getValueType()==this.DATEVALUE)
					{
						if(factorRetValue.getValueType()==this.STRVALUE)
							this.SQL.append(" or nullif("+sql_str+",'') is null "); 
						else
							this.SQL.append(" or "+sql_str+" is null ");
					} 
				}
				else if("<>".equalsIgnoreCase(str)&&tokenIsNull&&this.DBType == Constant.MSSQL&&factorRetValue.getValueType()==this.MEMO)  //20141103 dengcan   如果 拟审批意见备注=空
				{
					this.SQL.append(" and  CAST("+sql_str+" as varchar(200))<>'' ");
				}
				this.SQL.append(" ) ");
				if (!Arith(nOp, retValue, hold)) {
					return false;
				}
				break;
			}
			default:
				break;
			}
		}
		return true;
	}

	private boolean level2(RetValue retValue) throws GeneralException,
			SQLException {
		int nOp = 0;
		RetValue hold = new RetValue();
		if (!level3(retValue))
			return false;
		while ((tok == S_ADD) || (tok == S_MINUS)) {
			nOp = tok;
			if ((retValue.IsStringType()) && ((DBType == Constant.ORACEL) || (DBType == 3))) {
				SQL_ADD("||");
			} else {
				SQL_ADD(token);
			}
			if (!Get_Token())
				return false;
			if (!level3(hold))
				return false;
			if (!Arith(nOp, retValue, hold))
				return false;
		}
		return true;
	}

	private boolean level3(RetValue retValue) throws GeneralException,
			SQLException {
		
		boolean _bDivFlag=this.bDivFlag;
		if(_bDivFlag)
			SQL_ADD("NullIF(");
		// 给 / 的除数加零判断 只有效一次.
		this.bDivFlag = false;
		int nOp = 0;
		RetValue hold = new RetValue();
		String cTmp = "";
		if (!level4(retValue))
			return false;
		// 孙新加
		if (((DBType == Constant.ORACEL) || (DBType == 3))
				&& ((tok == S_MOD) || (tok == S_DIV))
				|| ((DBType == 1) && (tok == S_DIV))) {
			if (tok == S_MOD)
				SError(E_USEFUNCMOD); // 请使用函数:取余数(除数, 被除数);
			else
				SError(E_USEFUNCINT);
			return false;
		}
		if ((tok == S_MULTIPLY) || (tok == S_DIVISION) || (tok == S_MOD)
				|| (tok == S_DIV)) {
			
			if(_bDivFlag)
			{
				SQL_ADD(",0)");
				_bDivFlag=false;
			}
			// 给 / 的除数加零判断 只有效一次.
			//bDivFlag = false;
			if (tok != S_MULTIPLY)
				this.bDivFlag = true;
			
			nOp = tok;
			switch (tok) {
				case S_DIV: {
					if (DBType == 3) {
						cTmp = SQL.toString();
						SQL.setLength(0);
					} else {
						SQL_ADD("\\");// 原来是"\\"
					}
					break;
				}
				case S_MOD: {
					switch (DBType) {
					case 1:
					case 2:
						cTmp = SQL.toString();
						SQL.setLength(0);
						break;
					case 3: {
						cTmp = SQL.toString();
						SQL.setLength(0);
						break;
					}
					}
					break;
				}
	
				default:
					SQL_ADD(token);
					break;
			} 
			if (!Get_Token())
				return false;

			if (!level3(hold))
				return false;
 
			if (!Arith(nOp, retValue, hold))
				return false;
			if (DBType == 1 && nOp == S_MOD) {
				String strTemp = SQL.toString();
				SQL.setLength(0);
				SQL.append(cTmp + " % NullIF(" + strTemp + ",0)"); // 考虑被除数为0的情况,chenmengqing 
			//	SQL.append(("cast(" + cTmp + " as int) % " + "cast("+ strTemp + " as int)"));
			}
			if ((DBType == 3) && ((nOp == S_MOD) || (nOp == S_DIV))) {
				if (nOp == S_MOD) {
					String strTemp = SQL.toString();
					SQL.setLength(0);
					SQL.append("MOD(INT(");
					SQL.append(cTmp);
					SQL.append("),INT(");
					SQL.append(strTemp);
					SQL.append("))");
				} else {
					String strTemp = SQL.toString();
					SQL.setLength(0);
					SQL.append("INT(");
					SQL.append(cTmp);
					SQL.append(")/");
				 	SQL.append(strTemp);
					 
				}
			}
		//	bDivFlag = false;
		}
		
		if(_bDivFlag)
			SQL_ADD(",0)");
		
		return true;
	}

	private boolean level4(RetValue retValue) throws GeneralException,
			SQLException {
		int nOp = 0;
		RetValue hold = new RetValue();
		if (!level5(retValue))
			return false;
		if (tok == S_POWER) {
			SQL_ADD(token);
			nOp = tok;
			if (!Get_Token())
				return false;
			if (!level4(hold))
				return false;
			if (!Arith(nOp, retValue, hold))
				return false;
		}
		return true;
	}

	private boolean level5(RetValue retValue) throws GeneralException,
			SQLException {
		int nOp = 0;
		if ((tok == S_ADD) || (tok == S_MINUS) || (tok == S_NOT)) {
			if (tok == S_NOT) {
				SQL_ADD(" NOT ");
			} else {
				SQL_ADD(token);
			}
			nOp = tok;
			if (!Get_Token())
				return false;
		}
		if (!level6(retValue))
			return false;
		if ((nOp == S_MINUS) || (nOp == S_NOT)) {
			if (!Unary(nOp, retValue))
				return false;
		}
		return true;
	}

	private boolean level6(RetValue retValue) throws GeneralException,
			SQLException {
		boolean isLPARENTHESIS=false;
		if ((tok == S_LPARENTHESIS) && (token_type == DELIMITER)) {
			if(tok == S_LPARENTHESIS)
				isLPARENTHESIS=true;
			SQL.append("(");
			if (!Get_Token())
				return false;
			if (!level0(retValue))
				return false;
			
			if(this.isInFunc&&tok==23) // in (7,8,9)  处理这种情况
			{ 
				while(true)
				{
					if("，".equals(this.token))
					{
						Putback();
						SError(E_USEQJCOMMA);
						return false; 
					}
					this.SQL.append(this.token);
					if (!Get_Token())
						return false;
					if (!level0(retValue))
						return false;
					if (tok == S_RPARENTHESIS)
						break;
				}
				
			}
			
			if (tok != S_RPARENTHESIS) {
				Putback();
				SError(E_LOSSRPARENTHESE);
				return false;
			}
			SQL.append(" )");
		
			if(isInFunc&&isLPARENTHESIS) // in (7,8,9)  处理这种情况
			{
				isInFunc=false;
			}
			
			if (!Get_Token()) {
				return false;
			}
		} else if (!Primitive(retValue)) {
			return false;
		}

		return true;
	}

	private boolean ProcFunction(RetValue retValue) throws GeneralException,
			SQLException {
		boolean b = false;
		switch (tok) {
		case FUNCTODAY:
			b = Func_TodayPart(FUNCTODAY, retValue);
			break;

		case FUNCTOWEEK:
			b = Func_TodayPart(FUNCTOWEEK, retValue);
			break;
		case FUNCTOMONTH:
			b = Func_TodayPart(FUNCTOMONTH, retValue);
			break;
		case FUNCTOQUARTER:
			b = Func_TodayPart(FUNCTOQUARTER, retValue);
			break;
		case FUNCTOYEAR:
			b = Func_TodayPart(FUNCTOYEAR, retValue);
			break;
		case FUNCYEAR:
			b = Func_DatePart(FUNCYEAR, retValue);
			break;
		case FUNCMONTH:
			b = Func_DatePart(FUNCMONTH, retValue);
			break;
		case FUNCDAY:
			b = Func_DatePart(FUNCDAY, retValue);
			break;
		case FUNCQUARTER:
			b = Func_DatePart(FUNCQUARTER, retValue);
			break;
		case FUNCWEEK:
			b = Func_DatePart(FUNCWEEK, retValue);
			break;
		case FUNCWEEKDAY:
			b = Func_DatePart(FUNCWEEKDAY, retValue);
			break;
		case FUNCYEARS:
			b = Func_DateDiff(FUNCYEARS, retValue);
			break;
		case FUNCMONTHS:
			b = Func_DateDiff(FUNCMONTHS, retValue);
			break;
		case FUNCDAYS:
			b = Func_DateDiff(FUNCDAYS, retValue);
			break;
		case FUNCQUARTERS:
			b = Func_DateDiff(FUNCQUARTERS, retValue);
			break;
		case FUNCWEEKS:
			b = Func_DateDiff(FUNCWEEKS, retValue);
			break;
		case FUNCADDYEAR:
			b = Func_DateAdd(FUNCADDYEAR, retValue);
			break;
		case FUNCADDMONTH:
			b = Func_DateAdd(FUNCADDMONTH, retValue);
			break;
		case FUNCADDDAY:
			b = Func_DateAdd(FUNCADDDAY, retValue);
			break;
		case FUNCADDQUARTER:
			b = Func_DateAdd(FUNCADDQUARTER, retValue);
			break;
		case FUNCADDWEEK:
			b = Func_DateAdd(FUNCADDWEEK, retValue);
			break;
		case FUNCAGE:
			// System.out.println(retValue.getValue());
			b = Func_CalcAge(FUNCAGE, retValue);
			break;
		case FUNCAPPAGE:
			b = Func_CalcAge(FUNCAPPAGE, retValue);
			break;
		case FUNWORKDAYS:
			b = Func_CalWorkDays(FUNWORKDAYS, retValue);
			break;
		case FUNPARTTIMEJOB:
			b = Func_PartTimeJob(retValue);
			break;
		case GROUPSTAT:
			/** 分组汇总函数 */
			b = Func_GroupStat(retValue);
			break;
		case STATTIME:
			/** 统计时间 */
			b = Func_StatTime(retValue);
			break;
		case STATMONTH:	
			/** 统计月数 */
			b = Func_StatMonth(retValue);
			break;
		case SEQUENCE:
			/** 序号 */
			b = Func_Sequence(retValue);
			break;
		case GETFROM:
			/** 取自于 */
			b = Func_GetFrom(retValue);
			break;
		case FUNCAPPMONTHAGE:
			b = Func_CalcAge(FUNCAPPMONTHAGE, retValue);
			break;
		case FUNCAPPWORKAGE:
			b = Func_CalcAge(FUNCAPPWORKAGE, retValue);
			break;
		case FUNCWORKAGE:
			b = Func_CalcAge(FUNCWORKAGE, retValue);
			break;
		case FUNCMONTHAGE:
			b = Func_CalcAge(FUNCMONTHAGE, retValue);
			break;
		case FUNCAPPDATE:
			b = Func_AppDate(retValue);
			break;
		case FUNCINT:
			b = Func_Math(FUNCINT, retValue);
			break;
		case FUNCROUND:
			b = Func_Math(FUNCROUND, retValue);
			break;
		case FUNCSANQI:
			b = Func_Math(FUNCSANQI, retValue);
			break;
		case FUNCYUAN:
			b = Func_Math(FUNCYUAN, retValue);
			break;
		case FUNCJIAO:
			b = Func_Math(FUNCJIAO, retValue);
			break;
		case FUNCTRIM:
			b = Func_String(FUNCTRIM, retValue);
			break;
		case FUNCLTRIM:
			b = Func_String(FUNCLTRIM, retValue);
			break;
		case FUNCRTRIM:
			b = Func_String(FUNCRTRIM, retValue);
			break;
		case FUNCLEN:
			b = Func_String(FUNCLEN, retValue);
			break;
		case FUNCLEFT:
			b = Func_String(FUNCLEFT, retValue);
			break;
		case FUNCRIGHT:
			b = Func_String(FUNCRIGHT, retValue);
			break;
		case FUNCSUBSTR:
			b = Func_String(FUNCSUBSTR, retValue);
			break;
		case FUNCCTOD:
			b = Func_Convert(FUNCCTOD, retValue);
			break;
		case FUNCCTOI:
			b = Func_Convert(FUNCCTOI, retValue);
			break;
		case FUNCDTOC:
			b = Func_Convert(FUNCDTOC, retValue);
			break;
		case FUNCITOC:
			b = Func_Convert(FUNCITOC, retValue);
			break;
		case FUNCCTON:
			b = Func_CTON(retValue);
			break;
		case FUNCCTON2:
			b = Func_CTON2(retValue);
			break;
		case FUNCIIF: 
			b = Func_IIF(retValue);
			break;
		case FUNCCASE:
			b = Func_CASE(retValue);
			break;
		case FUNCMAX:
			b = Func_Maxmin(FUNCMAX, retValue);
			break;
		case FUNCMIN:
			b = Func_Maxmin(FUNCMIN, retValue);
			break;
		case FUNCGET:
			b = Func_GET(retValue);
			break;
		case FUNCSELECTSUBSET:
			b = Func_SelectSubset(retValue);
			break;
		case FUNCSELECT:
			b = Func_SELECT(retValue);
			break;
		case FUNCSELECTSET:
			b = Func_SelectSetSum(retValue);
			break;
		case FUNCSTATP:
			b = Func_STATP(retValue);
			break;
		case FUNCMOD:
			b = Func_MOD(retValue);
			break;
		case FUNCISNULL:
			b = Func_ISNNULL(retValue);
			break;
		case FUNCPOWER:
			b = Func_POWER(retValue);
			break;
		case FUNCCNTC:
			b = Func_CNTC(retValue);
			break;
		case FUNCDTOCODE:	
			b = Func_DTOCODE(retValue);
			break;
		case FUNCCODEADD:
			b = Func_CODEADD(retValue, 1);
			break;
		case FUNCCODENEXT:
			b = Func_CODEADD(retValue, 1);
			break;
		case FUNCCODEPRIOR:
			b = Func_CODEADD(retValue, -1);
			break;
		case SUPERIORCODE:
			b = Func_SUPERIORCODE(retValue, -1);
			break;
		case FUNCCODEADJUST:
			b = Func_CODEADJUST(retValue);
			break;
		case FUNCNEARBYHIGH:
			b = Func_NearByHight(retValue, true);
			break;
		case FUNCNEARBYLOW:
			b = Func_NearByHight(retValue, false);
			break;
		case FUNCSTANDARD:
			b = Func_Standard(retValue);
			break;
		case FUNCHISFIRSTMENU:
			b = Func_HistoryMenu(retValue, 1);
			break;
		case FUNCHISPRIORMENU:
			b = Func_HistoryMenu(retValue, 2);
			break;
		case FUNCSALARYA00Z0:
			b = Func_SalaryA00Z0(retValue);
			break;
		case FUNCLOGINNAME:
			b = Func_LoginName(retValue);
			break;
		case FUNCAPPEALUN:
			b = Func_AppealInfo(retValue,"UN");
			break;
		case FUNCAPPEALUM:
			b = Func_AppealInfo(retValue,"UM");
			break;
		case FUNCAPPEALPOS:
			b = Func_AppealInfo(retValue,"@K");
			break;
		case THISUNIT:
			b = Func_ThisUnit2(retValue);
			break;
		case BUSIUNIT:
			b = Func_BusiUnit(retValue);
			break;			
		case E0122VALUE:
			b = Func_E0122VALUE(retValue);
			break;
		case E01A1VALUE:
			b = Func_E01A1VALUE(retValue);
			break;
		case FUNCPROCEDURE:
			b = Func_PROCEDURE(retValue);
			break;
		case FUNCOBJECTFIELD:
			b = Func_OBJECTFIELD(retValue);
			break;
		case FUNBUDGETSUM:
			b = Func_BUDGETSUM(retValue);
			break;
		case FUNSYGZRS:
			b = Func_SYSFGZRS(retValue);
			break;
		case FUNKXDAYS:  
			b = Func_KXDAYS(retValue);
			break; 
		case FUNYXDAYS:  
			b = Func_YXDAYS(retValue);
			break;
		case FUNQJDAYS:
			b = Func_QJDAYS(retValue);
			break;
		case FUNJJTJTD:
			b =Func_NearGetGrade(retValue);
			break;
		case SPECIALADDAMOUNT:
			b = Func_SpecialAddAmount(retValue);
			break;
		}
		return b;
	}

	private boolean InputWindow(String strCaption, String msg, String inputValue) {
		return true;
	}

	private boolean ProcFieldItem(RetValue retValue) {
		// System.out.println(Field.getItemid() + " " + Field.getItemdesc());
		switch (ModeFlag) {
		case forNormal: {
			SQL.append(" ");
			SQL.append(/* Field.getItemSqlExpr(DbPre, false) */GetCurMenu(getAddTableName(),
					Field));// GetCurMenu(FALSE,Field);
			
			if (!UsedSets.contains(Field.getFieldsetid())) {
				UsedSets.add(Field.getFieldsetid());
			}
			if (!mapUsedFieldItems.containsKey(Field.getItemid())) {
				mapUsedFieldItems.put(Field.getItemid(), Field);
			}
			
			
			break;
		}
		case forSearch: {
			if (!UsedSets.contains(Field.getFieldsetid())) {
				UsedSets.add(Field.getFieldsetid());
			}
			if (!mapUsedFieldItems.containsKey(Field.getItemid())) {
				mapUsedFieldItems.put(Field.getItemid(), Field);
			}

			if (CurFuncNum == FUNCSELECT||isFuncSelectCondition) {
				SQL.append(" ");
				SQL.append(/* Field.getItemSqlExpr(DbPre, true) */GetCurMenu(
						true, Field));// GetCurMenu(FALSE,Field); chenmengqing
										// added at 20080401

			} else {
				SQL.append(" ");
				SQL.append(/* Field.getItemSqlExpr(DbPre, false) */GetCurMenu(
						getAddTableName(), Field));// GetCurMenu(false,Field);
			}
		}
		}
		if (Field.isInt()) {

			retValue.setValue(new Integer(10));
			retValue.setValueType(INT);
		} else if (Field.isFloat()) {
			retValue.setValue(new Float(10.0));
			retValue.setValueType(FLOAT);
		} else if (Field.isChar()) {
			retValue.setValue("");
			retValue.setValueType(STRVALUE);
		} else if (Field.isDate()) {
			retValue.setValue("#2006.07.21#");
			retValue.setValueType(DATEVALUE);
		} else if(Field.isMemo()) //20141103 dengcan   如果 拟审批意见备注=空
		{
			retValue.setValue("");
			retValue.setValueType(MEMO);
		}
		return true;
	}

	private boolean Primitive(RetValue retValue) throws GeneralException,
			SQLException {
		// boolean b = true;
		FieldItem field1 = null;
		try {
			switch (token_type) {
			case INT: {
				SQL_ADD(token);
				retValue.setValue(Integer.valueOf(token));
				retValue.setValueType(INT);
				if (!Get_Token())
					return false;
				break;
			}
			case FLOAT: {
				SQL_ADD(token);
				retValue.setValue(Float.valueOf(token));
				retValue.setValueType(FLOAT);
				if (!Get_Token())
					return false;
				break;
			}
			case LOGIC: {
				if (tok == S_TRUE) {
					SQL_ADD("TRUE");
					retValue.setValue(new Boolean(true));
					retValue.setValueType(LOGIC);
				} else {
					SQL_ADD("FALSE");
					retValue.setValue(new Boolean(false));
					retValue.setValueType(LOGIC);
				}
				if (!Get_Token())
					return false;
				break;
			}
			case QUOTE: {
				// SQL_ADD(token);
				if (this.DBType == Constant.ORACEL && "".equals(token))
					SQL_ADD("NULL");
				else
					SQL_ADD("'" + token + "'");
				retValue.setValue(token);
				retValue.setValueType(STRVALUE);
				if (!Get_Token())
					return false;
				break;
			}
			case NULLVALUE: {
				if(this.isNull_MSSQL) //20141102 dengcan
					SQL_ADD("''");
				else
					SQL_ADD("NULL");
				retValue.setValue("NULL");
				retValue.setValueType(NULLVALUE);
				if (CurFuncNum == FUNCSTANDARD)
					this.Field = null;
				if (!Get_Token())
					return false;
				break;
			}
			case DATEVALUE: {
				SQL_ADD(token);
				// // 此刻经过
				// 的处理，token=="'2006.06.28'";
				// Sql_switcher.dateValue(token);
				retValue.setValue(token);
				retValue.setValueType(DATEVALUE);
				if (!Get_Token())
					return false;
				break;
			}

			case ODDVAR: {
				SQL.append(" ").append(GetCurMenu(getAddTableName(), Field));
				if (!FindMenu(Field.getItemid(), mapUsedFieldItems)) {
					field1 = (FieldItem) Field.cloneItem();
					mapUsedFieldItems.put(field1.getItemid(), field1);
				}
				if ("N".equals(Field.getItemtype())) {
					retValue.setValue(new Integer(1));
					retValue.setValueType(INT);
				} else if ("D".equals(Field.getItemtype())) {
					retValue.setValue("'2002.05.14'");
					retValue.setValueType(DATEVALUE);
				} else if ("A".equals(Field.getItemtype())) {
					retValue.setValue("");
					retValue.setValueType(STRVALUE);
				}
				if (!Get_Token())
					return false;
				break;
			}

			case FIELDITEM: {
			/*
				if(isFIELDITEM2&&bracketsFieldValueMap!=null&&bracketsFieldValueMap.size()>0&&bracketsFieldValueMap.get("["+token+"]")!=null)
				{
					String _value=(String)bracketsFieldValueMap.get("["+token+"]");
					FieldItem _item=(FieldItem)bracketsFieldMap.get("["+token+"]");
					if(_item.getItemtype().equalsIgnoreCase("A")||_item.getItemtype().equalsIgnoreCase("M"))
					{
						if (this.DBType == Constant.ORACEL && token.equals(""))
							SQL_ADD("NULL");
						else
							SQL_ADD("'" + _value + "'");
						retValue.setValue(token);
						retValue.setValueType(STRVALUE);
					}
					else if(_item.getItemtype().equalsIgnoreCase("N"))
					{
						SQL_ADD(_value);
						if(_item.getDecimalwidth()>0)
						{
							retValue.setValue(Float.valueOf(_value));
							retValue.setValueType(FLOAT);
						}
						else
						{
							retValue.setValue(Integer.valueOf(_value));
							retValue.setValueType(INT);
						}
					}
					else if(_item.getItemtype().equalsIgnoreCase("D"))
					{
						//_value="'2006.06.28'"
						
						SQL_ADD(Sql_switcher.dateValue(_value));
						retValue.setValue(token);
						retValue.setValueType(DATEVALUE);
					} 
				}
				else*/
				
				{
					retValue.setValue(token);
					ProcFieldItem(retValue); 
				}
				if (!Get_Token()) {
					return false;
				}
				break;
			}

			case FUNC: {

				return ProcFunction(retValue);
			}
			default: {
				Putback();
				SError(E_SYNTAX);
				return false;
			}
			}
		} catch (Exception e) {
			if(!FError)
				SError(E_SYNTAX);
			throw GeneralExceptionHandler.Handle(e);
		}

		return true;
	}

	private boolean Unary(int nOp, RetValue retValue) throws GeneralException {
		switch (nOp) {
		case S_MINUS: {
			retValue.MinusValue();
			break;
		}
		case S_NOT: {
			if (!retValue.isBooleanType()) {
				SError(E_MUSTBEBOOL);
				return false;
			}
			retValue.NotValue();
		}
		}
		return true;
	}

	private boolean Get_Token() throws GeneralException {
		// System.out.println(this.FSource + " " + token);
		// System.out.println("--------->" + FSource.charAt(nCurPos));
		String str;
		tok = 0;
		token = "";
		token_type = 0;
	//	isFIELDITEM2=false;
		// 判断当前指针是否以指向最后一个字符的后面，即公式已经解析完。
		if (nCurPos == FSource.length()) {
			tok = S_FINISHED; // 结束
			token_type = DELIMITER; // 分隔符类型
			return true;
		}

		// 处理空格、回车、换行等不参与分析的字符。
		while (nCurPos < FSource.length()
				&& (FSource.charAt(nCurPos) == ' '
						|| FSource.charAt(nCurPos) == '\r'
						|| FSource.charAt(nCurPos) == '\t' || FSource
						.charAt(nCurPos) == '\n')) {
			nCurPos++;
		}

		// 处理注释( //开始 换行结束,需要考虑超过字串长度 新增 nCurPos+2<=FSource.length() &&)
		if (nCurPos + 2 <= FSource.length() && FSource.charAt(nCurPos) == '/'
				&& FSource.charAt(nCurPos + 1) == '/') {
			nCurPos += 2;
			while (!(nCurPos == FSource.length() || (FSource.charAt(nCurPos) == '\n'))) {
				nCurPos++;
			}

			if (nCurPos != nFSourceLen) { // 不等于总长度
				nCurPos++;
				while(nCurPos< nFSourceLen) // 解决注释后有多个 \n\n\n 问题 20150923
				{
					if(FSource.charAt(nCurPos) == '\n')
						nCurPos++;
					else 
						break;
				}

			}
		}

		// 判断当前指针是否以指向最后一个字符的后面，即公式已经解析完。
		if (nCurPos == nFSourceLen) {
			tok = S_FINISHED; // 结束
			token_type = DELIMITER; // 分隔符
			return true;
		}

		// 处理分隔符
		int nPos = "+-*/\\%^;:,=<>()；：，".indexOf(FSource.charAt(nCurPos));
		if (nPos >= 0) {
			switch (nPos) {
			case 0: {
				tok = S_ADD;// 加
				break;
			}
			case 1: {
				tok = S_MINUS; // 减
				break;
			}
			case 2: {
				tok = S_MULTIPLY; // 乘
				break;
			}
			case 3: {
				tok = S_DIVISION; // 除
				break;
			}
			case 4: {
				tok = S_DIV; // 整除
				break;
			}
			case 5: {
				tok = S_MOD; // 求模
				break;
			}
			case 6: {
				tok = S_POWER; // 幂
				break;
			}
			case 7: {// 半角 分号
				tok = S_SEMICOLON; // 分号
				break;
			}
			case 8: {// 半角 冒号
				tok = S_COLON; // 冒号
				break;
			}
			case 9: { // 半角 逗号
				tok = S_COMMA; // 逗号
				break;
			}
			case 10: {
				tok = S_EQUAL; // 等号
				break;
			}
			case 11: {
				tok = S_SMALLER; // 小于号
				break;
			}
			case 12:
				tok = S_GREATER; // 大于号
				break;
			case 13: {
				tok = S_LPARENTHESIS;// 左括号
				break;
			}
			case 14: {
				tok = S_RPARENTHESIS;// 右括号
				break;
			}
			case 15: {// 全角 分号
				tok = S_SEMICOLON; // 分号
				break;
			}
			case 16: {// 全角 冒号
				tok = S_COLON; // 冒号
				break;
			}
			case 17: {// 全角 逗号
				tok = S_COMMA; // 逗号
				break;
			}

			default:
				break;
			}

			token = "" + FSource.charAt(nCurPos);
			token_type = DELIMITER; // 分隔符
			nCurPos++;

			return true;

		}

		// 左引号
		if (FSource.charAt(nCurPos) == '"') {
			token = "";
			nCurPos++;
			while (!((nCurPos == nFSourceLen) || (FSource.charAt(nCurPos) == '"'))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			int nTemp = nCurPos == nFSourceLen ? 2 : 1; // 是否到了结尾
			nCurPos++;
			if (FSource.charAt(nCurPos - nTemp) != '"') {
				Putback();
				SError(E_LOSSQUOTE);
				return false;
			}
			token_type = QUOTE; // 引用类型
			return true;
		}

		/** ***** sunx有关单引号*********** */
		if (FSource.charAt(nCurPos) == '\'') {
			token = "";
			nCurPos++;
			while (!((nCurPos == nFSourceLen) || (FSource.charAt(nCurPos) == '\''))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			
			int nTemp = nCurPos == nFSourceLen ? 2 : 1;
			nCurPos++;
			if (FSource.charAt(nCurPos - nTemp) != '\'') {
				Putback();
				SError(E_LOSSQUOTE);
				return false;
			}
			token_type = QUOTE;
			return true;
		}

		/** ***** sunx有关单引号end*********** */
		// 处理方括号，指标
		if (FSource.charAt(nCurPos) == '[') {
			token = "";
			nCurPos++;
			while (!((nCurPos == nFSourceLen) || (FSource.charAt(nCurPos) == ']'))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			int nTemp = nCurPos == nFSourceLen ? 2 : 1;
			nCurPos++;
			if (FSource.charAt(nCurPos - nTemp) != ']') {
				SError(E_LOSSBRACK1);
				return false;
			}
 
			if (IsFieldItem(token)) {
				token_type = FIELDITEM;
	//			bracketsFieldMap.put("["+token+"]",(FieldItem)this.Field.clone());
	//			isFIELDITEM2=true;
				return true;
			} else {
				SError(E_NOTFINDFIELD);
				return false;
			}
		}

		// 处理花括号
		if (FSource.charAt(nCurPos) == '{') {
			token = "";
			nCurPos++;
			while (!((nCurPos == nFSourceLen) || (FSource.charAt(nCurPos) == '}'))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			int nTemp = nCurPos == nFSourceLen ? 2 : 1;
			nCurPos++;
			if (FSource.charAt(nCurPos - nTemp) != '}') {
				SError(E_LOSSBRACK2);
				return false;
			}
			if (IsOddVar(token)) {
				token_type = ODDVAR;
				return true;
			} else {
				SError(E_NOTFINDODDVAR);
				return false;
			}
		}

		// 处理数字
		if (IsDigit(FSource.charAt(nCurPos))) {
			token = "" + FSource.charAt(nCurPos);
			nCurPos++;
			while (nCurPos != nFSourceLen && IsDigit(FSource.charAt(nCurPos))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			token_type = INT;
			if (nCurPos != nFSourceLen && FSource.charAt(nCurPos) == '.') {

				token = token + FSource.charAt(nCurPos);
				nCurPos++;

				/** zhang feng jin 修改 */
				if (nCurPos == nFSourceLen) {
					SError(E_MUSTBENUMBER);
					return false;
				}
				/** zhang feng jin 修改 */

				if (!IsDigit(FSource.charAt(nCurPos))) {
					SError(E_MUSTBENUMBER);
					return false;
				}

				while (nCurPos != nFSourceLen
						&& IsDigit(FSource.charAt(nCurPos))) {

					token = token + FSource.charAt(nCurPos);
					nCurPos++;
				}
				token_type = FLOAT;
			}
			return true;
		}

		// 处理日期常量
		if (FSource.charAt(nCurPos) == '#') {
			token = "";
			nCurPos++;
			while (!((nCurPos == nFSourceLen) || (FSource.charAt(nCurPos) == '#'))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			int nTemp = nCurPos == nFSourceLen ? 2 : 1;
			nCurPos++;
			if (FSource.charAt(nCurPos - nTemp) != '#') {
				SError(E_MUSTBEDATE);
				return false;
			}

			token = token.replaceAll("/", ".");
			token = token.replaceAll("-", ".");
			token = token.replaceAll(",", ".");
			int count = 0;
			for (int i = 0; i < token.length(); i++) {
				if (token.charAt(i) == '.')
					count++;
			}
			if (count == 1) {
				String[] temps=token.split("\\.");
				if(temps.length==1)
				{
					SError(E_MUSTBEDATE);
					return false;
				}
				if(Integer.parseInt(temps[0])>12)
				{
					SError(E_MUSTBEDATE);
					return false;
				}
				token = parseMonthDay(token);
			}
			/** 不是合法的日期串 */
			if (!isDateString(token)) {
				SError(E_MUSTBEDATE);
				return false;
			}
			token = datePad(token);
			if(isFuncPROCEDURE) //如果是执行存储过程，不需要转换
			{
				
			}
			else
				token = Sql_switcher.dateValue(token);
			token_type = DATEVALUE;
			return true;
		}

		// 处理代码转名称
		if (FSource.charAt(nCurPos) == '~') {
			token_type = STRVALUE;
			token = "" + FSource.charAt(nCurPos);
			nCurPos++;
			return Lookup(token);
		}

		// 判断字符含义
		if (IsAlpha(FSource.charAt(nCurPos))
				|| isChiness(FSource.charAt(nCurPos))) {

			token = "" + FSource.charAt(nCurPos);
			nCurPos++;
			while ((nCurPos != nFSourceLen)
					&& !IsDelimiter(FSource.charAt(nCurPos))) {
				token = token + FSource.charAt(nCurPos);
				nCurPos++;
			}
			if ("现".equals(token) || ("拟".equals(token))) {
				str = token;
				if ((FSource.charAt(nCurPos) != '(')
						&& (FSource.charAt(nCurPos) != '[')) {
					SError(E_LOSSLPARENTHESE);
					return false;
				}
				nCurPos++;
				token = "";
				while (!((FSource.charAt(nCurPos) == ')')
						|| (FSource.charAt(nCurPos) == ']') || (nCurPos == nFSourceLen))) {
					token = token + FSource.charAt(nCurPos);
					nCurPos++;
				}
				token = str + token;
				if ((FSource.charAt(nCurPos) != ')')
						|| (FSource.charAt(nCurPos) != ']')) {
					SError(E_LOSSRPARENTHESE);
					return false;
				}
				nCurPos++;
				if (IsFieldItem(token)) {
					token_type = FIELDITEM;
					return true;
				} else {
					SError(E_NOTFINDFIELD);
					return false;
				}
			}
			token_type = STRVALUE;
			if(this.isGetFrom)
				return true;
			if (token_type == STRVALUE) {
				if (!Lookup(token)) {
					return false;
				}
			}
			return true;

		}

		return false;
	}

	/**
	 * 把日期串补成10 yyyy.mm.dd
	 * 
	 * @param datestr
	 * @return
	 */
	private String datePad(String datestr) {
		// datestr="#"+datestr+"#"; //for 定义公式为#2008-10-20#出错 at 2008-09-03
		if (datestr.length() == 10)
			return datestr;
		datestr = datestr.replaceAll("-", ".");
		String[] tmp = StringUtils.split(datestr, ".");
		StringBuffer buf = new StringBuffer();
		buf.append(tmp[0]);
		buf.append(".");
		if (tmp[1].length() == 1) {
			buf.append("0");
			buf.append(tmp[1]);
		} else
			buf.append(tmp[1]);
		buf.append(".");
		if (tmp[2].length() == 1) {
			buf.append("0");
			buf.append(tmp[2]);
		} else
			buf.append(tmp[2]);
		return buf.toString();
	}

	/**
	 * 当用户只输入月和日时，年默认为今年。 例如输入#1.1#返回 year-01-01
	 * 
	 * @param monthDay
	 * @return
	 */
	private String parseMonthDay(String monthDay) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		String year = dateFormat.format(now).toString();
		monthDay = monthDay.replaceAll("#", "");
		monthDay = monthDay.trim();
		String[] monthDayArray = monthDay.split("\\.");
		for (int i = 0; i < monthDayArray.length; i++) {
			while (monthDayArray[i].length() < 2) {
				monthDayArray[i] = "0" + monthDayArray[i];
			}
		}
		String datestr = "";
		datestr = year + "." + monthDayArray[0] + "." + monthDayArray[1];
		// if(monthDayArray.length>0) cmq changed at 20080628
		// datestr = year +"."+ monthDayArray[0]+"." + monthDayArray[1];
		// else if(monthDayArray.length>1)
		// datestr = year +"."+ monthDayArray[0]+"." + monthDayArray[1];
		return datestr;
	}

	private boolean isChiness(char c) {
		return (int) c > 127;
	}

	/**
	 * 是否为合法的日期串
	 * 
	 * @param date
	 * @return
	 */
	private boolean isDateString(String strdate) {
		boolean bflag = true;
		try {
			if(strdate.indexOf("TO_DATE")!=-1&&strdate.indexOf("YYYY-MM-DD")!=-1)
				return true;
			if(strdate.indexOf("DATEADD")!=-1)
				return true;
			strdate = strdate.replaceAll("\\.", "-");
			Date date = DateStyle.parseDate(strdate);
			if (date == null)
				bflag = false;
		} catch (Exception ex) {
			bflag = false;
		}
		return bflag;
	}

	/**
	 * 判断字符串代表什么。可能是函数、关键字、指标等。
	 * 
	 * @param str
	 * @return
	 * @throws GeneralException
	 */
	private boolean Lookup(String str) throws GeneralException { // 关键字指标函数
		// 临时变量
		tok = 0;
		// System.out.println("--->" + str);
		token_type = 0;
		 
		if (IsKey(str) || IsFunction(str) || IsFieldItem(str) || IsOddVar(str)
				|| isFieldSet(str)) {
			// System.out.println("true-->" + this.token_type);
			return true;
		} else {
			// System.out.println("false-->" + this.token_type);
			
			if(this.isFuncPROCEDURE&&PROCEDURE_NAME.length()==0)
			{
				token_type= STRVALUE;
				return true;
			}
			else
			{
				SError(E_UNKNOWNSTR);
				return false;
			}
		}
	}

	/**
	 * 判断是否是字符
	 */
	private boolean IsAlpha(char c) {
		return "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
				.indexOf(c) >= 0;
	}

	/**
	 * 判断是否是关键字
	 */
	private boolean IsKey(String str) {
		if ("NULL".equalsIgnoreCase(str) || "空".equals(str)) {
			tok = S_NULL;
			token_type = NULLVALUE;
			return true;
		}

		if ("AND".equalsIgnoreCase(str) || ("且".equalsIgnoreCase(str)))
			tok = S_AND;
		else if ("OR".equalsIgnoreCase(str) || ("或".equalsIgnoreCase(str)))
			tok = S_OR;
		else if ("NOT".equalsIgnoreCase(str) || ("非".equalsIgnoreCase(str)))
			tok = S_NOT;
		else if ("DIV".equalsIgnoreCase(str))
			tok = S_DIV;
		else if ("MOD".equalsIgnoreCase(str))
			tok = S_MOD;
		else if ("LIKE".equalsIgnoreCase(str))
			tok = S_LIKE;
		else if ("IN".equalsIgnoreCase(str))
			tok = S_IN;
		token_type = DELIMITER;
		if (tok != 0)
			return true;

		token_type = LOGIC;
		if ("TRUE".equalsIgnoreCase(str) || ("真".equalsIgnoreCase(str)))
			tok = S_TRUE;
		else if ("FALSE".equalsIgnoreCase(str) || ("假".equalsIgnoreCase(str)))
			tok = S_FALSE;
		if (tok != 0)
			return true;

		if ("THEN".equalsIgnoreCase(str) || ("那么".equalsIgnoreCase(str))) {

			tok = S_THEN;
			token_type = DELIMITER;
			return true;
		}
		if ("ELSE".equalsIgnoreCase(str) || ("否则".equalsIgnoreCase(str))) {
			tok = S_ELSE;
			token_type = DELIMITER;
			return true;
		}

		if ("END".equalsIgnoreCase(str) || ("结束".equalsIgnoreCase(str))) {
			tok = S_END;
			token_type = DELIMITER;
			return true;
		}

		if ("FIRST".equalsIgnoreCase(str) || ("的最初第一条记录".equalsIgnoreCase(str))) {
			tok = S_FIRST;
			token_type = DELIMITER;
			return true;
		}
		if ("LAST".equalsIgnoreCase(str) || ("的最近第一条记录".equalsIgnoreCase(str))) {
			tok = S_LAST;
			token_type = DELIMITER;
			return true;
		}
		if ("MAX".equalsIgnoreCase(str) || ("的最大值".equalsIgnoreCase(str))
				|| "最大值".equalsIgnoreCase(str)) {
			tok = S_MAX;
			token_type = DELIMITER;
			return true;
		}
		if ("MIN".equalsIgnoreCase(str) || ("的最小值".equalsIgnoreCase(str))
				|| "最小值".equalsIgnoreCase(str)) {
			tok = S_MIN;
			token_type = DELIMITER;
			return true;
		}
		if ("SUM".equalsIgnoreCase(str) || ("的总和".equalsIgnoreCase(str))
				|| "总和".equalsIgnoreCase(str)) {
			tok = S_SUM;
			token_type = DELIMITER;
			return true;
		}
		if ("AVG".equalsIgnoreCase(str) || ("的平均值".equalsIgnoreCase(str))
				|| "平均值".equalsIgnoreCase(str)) {
			tok = S_AVG;
			token_type = DELIMITER;
			return true;
		}
		if ("COUNT".equalsIgnoreCase(str) || ("的个数".equalsIgnoreCase(str))
				|| "个数".equalsIgnoreCase(str) || "人数".equalsIgnoreCase(str)) {
			tok = S_COUNT;
			token_type = DELIMITER;
			return true;
		}
		if ("INCREASE".equalsIgnoreCase(str) || ("最初第".equalsIgnoreCase(str))) {
			tok = S_INCREASE;
			token_type = DELIMITER;
			return true;
		}

		if ("当前列表".equalsIgnoreCase(str)|| "当前表".equalsIgnoreCase(str)) {
			tok = S_CURRENTTABLE;
			token_type = DELIMITER;
			return true;
		}
		if ("当前人员库".equalsIgnoreCase(str)) {
			tok = S_CURRENTA01;
			token_type = DELIMITER;
			return true;
		} else if ("含节假日".equalsIgnoreCase(str)
				|| "不含节假日".equalsIgnoreCase(str)) {
			tok = S_HOLIDAY;
			token_type = DELIMITER;
			return true;
		}
		
		
		if (this.isPartTimeJobParam&&("单位部门".equalsIgnoreCase(str)
				|| "单位".equalsIgnoreCase(str)|| "部门".equalsIgnoreCase(str))) {
			tok = S_PARTTIMEPARAM;
			token_type = DELIMITER;
			return true;
		}
		
		

		if ("DECREASE".equalsIgnoreCase(str) || ("最近第".equalsIgnoreCase(str))) {
			tok = S_DECREASE;
			token_type = DELIMITER;
			return true;
		}
		if ("条记录".equals(str)) {
			tok = S_GETEND;
			token_type = DELIMITER;
			return true;
		}

		if ("满足".equals(str)) {
			tok = S_SATISFY;
			token_type = DELIMITER;
			return true;
		}

		if ("基于子集".equals(str)) {
			tok = S_BASESET;
			token_type = DELIMITER;
			return true;
		}
		if ("所有专项的合计".equals(str)) {
			tok = ALLTAX;
			token_type = DELIMITER;
			return true;
		}else if ("不含大病、继续教育其它专项的合计".equals(str)) {
			tok = NOTSICK_EDU_SUM;
			token_type = DELIMITER;
			return true;
		}else if ("子女教育".equals(str)) {
			tok = CHILD_EDU;
			token_type = DELIMITER;
			return true;
		}else if ("继续教育".equals(str)) {
			tok = CON_EDU;
			token_type = DELIMITER;
			return true;
		}else if ("住房租金".equals(str)) {
			tok = HOUSING_RENT;
			token_type = DELIMITER;
			return true;
		}else if ("住房贷款利息".equals(str)) {
			tok = MORTGAGE_RATE;
			token_type = DELIMITER;
			return true;
		}else if ("大病医疗".equals(str)) {
			tok = LARGE_SCALE;
			token_type = DELIMITER;
			return true;
		}else if ("赡养老人".equals(str)) {
			tok = SUPPORT_OLDER;
			token_type = DELIMITER;
			return true;
		}else if ("当月".equals(str)) {
			tok = CURRENTMONTH;
			token_type = DELIMITER;
			return true;
		}else if ("累计".equals(str)) {
			tok = CUMULATIVE;
			token_type = DELIMITER;
			return true;
		}
		return false;
	}

	private boolean IsFunction(String str) {
		if ("FORMULA".equalsIgnoreCase(str) || "公式".equalsIgnoreCase(str)
				|| "执行公式".equalsIgnoreCase(str))
			tok = FUNCFORMULA;
		if ("YEAR".equalsIgnoreCase(str) || ("年".equalsIgnoreCase(str)))
			tok = FUNCYEAR;
		else if ("MONTH".equalsIgnoreCase(str) || ("月".equalsIgnoreCase(str)))
			tok = FUNCMONTH;
		else if ("DAY".equalsIgnoreCase(str) || ("日".equalsIgnoreCase(str)))
			tok = FUNCDAY;
		else if ("TODAY".equalsIgnoreCase(str) || ("今天".equalsIgnoreCase(str)))
			tok = FUNCTODAY;
		else if ("TOWEEK".equalsIgnoreCase(str) || ("本周".equalsIgnoreCase(str)))
			tok = FUNCTOWEEK;
		else if ("TOMONTH".equalsIgnoreCase(str)
				|| ("本月".equalsIgnoreCase(str)))
			tok = FUNCTOMONTH;
		else if ("TOQUARTER".equalsIgnoreCase(str)
				|| ("本季度".equalsIgnoreCase(str)))
			tok = FUNCTOQUARTER;
		else if ("TOYEAR".equalsIgnoreCase(str) || ("今年".equalsIgnoreCase(str)))
			tok = FUNCTOYEAR;
		else if ("AGE".equalsIgnoreCase(str) || ("年龄".equalsIgnoreCase(str)))
			tok = FUNCAGE;
		else if ("APPAGE".equalsIgnoreCase(str)
				|| ("到截止日期年龄".equalsIgnoreCase(str))
				|| ("年龄1".equalsIgnoreCase(str)))
			tok = FUNCAPPAGE;
		else if ("MONTHAGE".equalsIgnoreCase(str)
				|| ("到月年龄".equalsIgnoreCase(str)))
			tok = FUNCMONTHAGE;
		else if ("APPMONTHAGE".equalsIgnoreCase(str)
				|| ("到月年龄1".equalsIgnoreCase(str)))
			tok = FUNCAPPMONTHAGE;
		else if ("WORKAGE".equalsIgnoreCase(str)
				|| ("工龄".equalsIgnoreCase(str)))
			tok = FUNCWORKAGE;
		else if ("APPWORKAGE".equalsIgnoreCase(str)
				|| ("工龄1".equalsIgnoreCase(str)))
			tok = FUNCAPPWORKAGE;
		else if ("YEARS".equalsIgnoreCase(str) || ("年数".equalsIgnoreCase(str)))
			tok = FUNCYEARS;
		else if ("MONTHS".equalsIgnoreCase(str) || ("月数".equalsIgnoreCase(str)))
			tok = FUNCMONTHS;
		else if ("DAYS".equalsIgnoreCase(str) || ("天数".equalsIgnoreCase(str)))
			tok = FUNCDAYS;
		else if ("QUARTERS".equalsIgnoreCase(str)
				|| ("季度数".equalsIgnoreCase(str)))
			tok = FUNCQUARTERS;
		else if ("WEEKS".equalsIgnoreCase(str) || ("周数".equalsIgnoreCase(str)))
			tok = FUNCWEEKS;
		else if ("ADDYEAR".equalsIgnoreCase(str)
				|| ("增加年数".equalsIgnoreCase(str)))
			tok = FUNCADDYEAR;
		else if ("ADDQUARTER".equalsIgnoreCase(str)
				|| ("增加季度数".equalsIgnoreCase(str)))
			tok = FUNCADDQUARTER;
		else if ("ADDMONTH".equalsIgnoreCase(str)
				|| ("增加月数".equalsIgnoreCase(str)))
			tok = FUNCADDMONTH;
		else if ("ADDWEEK".equalsIgnoreCase(str)
				|| ("增加周数".equalsIgnoreCase(str)))
			tok = FUNCADDWEEK;
		else if ("ADDDAY".equalsIgnoreCase(str)
				|| ("增加天数".equalsIgnoreCase(str)))
			tok = FUNCADDDAY;
		else if ("QUARTER".equalsIgnoreCase(str)
				|| ("季度".equalsIgnoreCase(str)))
			tok = FUNCQUARTER;
		else if ("WEEK".equalsIgnoreCase(str) || ("周".equalsIgnoreCase(str)))
			tok = FUNCWEEK;
		else if ("WEEKDAY".equalsIgnoreCase(str)
				|| ("星期".equalsIgnoreCase(str)))
			tok = FUNCWEEKDAY;
		else if ("APPDATE".equalsIgnoreCase(str)
				|| ("截止日期".equalsIgnoreCase(str)))
			tok = FUNCAPPDATE;
		else if ("WORKDAYS".equalsIgnoreCase(str)
				|| ("工作日".equalsIgnoreCase(str)))
			tok = FUNWORKDAYS;
		else if ("取兼职信息".equalsIgnoreCase(str))
			tok = FUNPARTTIMEJOB;
		else if ("INT".equalsIgnoreCase(str) || ("取整".equalsIgnoreCase(str)))
			tok = FUNCINT;
		else if ("ROUND".equalsIgnoreCase(str)
				|| ("四舍五入".equalsIgnoreCase(str)))
			tok = FUNCROUND;
		else if ("SANQI".equalsIgnoreCase(str)
				|| ("三舍七入".equalsIgnoreCase(str)))
			tok = FUNCSANQI;
		else if ("YUAN".equalsIgnoreCase(str) || ("逢分进元".equalsIgnoreCase(str)))
			tok = FUNCYUAN;
		else if ("JIAO".equalsIgnoreCase(str) || ("逢分进角".equalsIgnoreCase(str)))
			tok = FUNCJIAO;
		else if ("TRIM".equalsIgnoreCase(str) || ("去空格".equalsIgnoreCase(str)))
			tok = FUNCTRIM;
		else if ("LTRIM".equalsIgnoreCase(str)
				|| ("去左空格".equalsIgnoreCase(str)))
			tok = FUNCLTRIM;
		else if ("RTRIM".equalsIgnoreCase(str)
				|| ("去右空格".equalsIgnoreCase(str)))
			tok = FUNCRTRIM;
		else if ("LEN".equalsIgnoreCase(str) || ("串长".equalsIgnoreCase(str)))
			tok = FUNCLEN;
		else if ("LEFT".equalsIgnoreCase(str) || ("左串".equalsIgnoreCase(str)))
			tok = FUNCLEFT;
		else if ("RIGHT".equalsIgnoreCase(str) || ("右串".equalsIgnoreCase(str)))
			tok = FUNCRIGHT;
		else if ("SUBSTR".equalsIgnoreCase(str) || ("子串".equalsIgnoreCase(str)))
			tok = FUNCSUBSTR;
		else if ("CTOD".equalsIgnoreCase(str)
				|| ("字符转日期".equalsIgnoreCase(str)))
			tok = FUNCCTOD;
		else if ("CTOI".equalsIgnoreCase(str)
				|| ("字符转数值".equalsIgnoreCase(str)))
			tok = FUNCCTOI;
		else if ("DTOC".equalsIgnoreCase(str)
				|| ("日期转字符".equalsIgnoreCase(str)))
			tok = FUNCDTOC;
		else if ("ITOC".equalsIgnoreCase(str)
				|| ("数值转字符".equalsIgnoreCase(str)))
			tok = FUNCITOC;
		else if ("数值转代码".equalsIgnoreCase(str))
			tok = FUNCDTOCODE;
		else if ("NumConversion".equalsIgnoreCase(str)
				|| ("数字转汉字".equalsIgnoreCase(str)))
			tok = FUNCCNTC;
		else if ("CTON".equalsIgnoreCase(str)
				|| ("代码转名称".equalsIgnoreCase(str))
				|| ("~".equalsIgnoreCase(str)))
			tok = FUNCCTON;
		else if ("CTON2".equalsIgnoreCase(str)
				|| ("代码转名称2".equalsIgnoreCase(str)))
			tok = FUNCCTON2;
		else if ("IIF".equalsIgnoreCase(str) || ("如果".equalsIgnoreCase(str)))
			tok = FUNCIIF;
		else if ("CASE".equalsIgnoreCase(str) || ("分情况".equalsIgnoreCase(str)))
			tok = FUNCCASE;
		else if ("GETMAX".equalsIgnoreCase(str)
				|| ("较大值".equalsIgnoreCase(str)))
			tok = FUNCMAX;
		else if ("GETMIN".equalsIgnoreCase(str)
				|| ("较小值".equalsIgnoreCase(str)))
			tok = FUNCMIN;
		else if ("GET".equalsIgnoreCase(str) || ("取".equalsIgnoreCase(str)))
			tok = FUNCGET;
		else if ("统计表单子集".equalsIgnoreCase(str))
			tok = FUNCSELECTSUBSET;
		else if ("SELECT".equalsIgnoreCase(str) || ("统计".equalsIgnoreCase(str)))
			tok = FUNCSELECT;
		else if ("分段计算".equalsIgnoreCase(str))
			tok = FUNCSELECTSET;
		else if ("STATP".equalsIgnoreCase(str)
				|| ("按职位统计人数".equalsIgnoreCase(str)))
			tok = FUNCSTATP;
		else if ("EXECUTESTANDARD".equalsIgnoreCase(str)
				|| ("执行标准".equalsIgnoreCase(str)))
			tok = FUNCSTANDARD;
		else if ("NEARBYHIGH".equalsIgnoreCase(str)
				|| ("就近就高".equalsIgnoreCase(str)))
			tok = FUNCNEARBYHIGH;
		else if ("NEARBYLOW".equalsIgnoreCase(str)
				|| ("就近就低".equalsIgnoreCase(str)))
			tok = FUNCNEARBYLOW;
		else if ("CodeUpDown".equalsIgnoreCase(str)
				|| ("代码变档".equalsIgnoreCase(str)))
			tok = FUNCCODEADD;
		else if ("CodeUpDownN".equalsIgnoreCase(str)
				|| ("后一个代码".equalsIgnoreCase(str)))
			tok = FUNCCODENEXT;
		else if ("CodeUpDownP".equalsIgnoreCase(str)
				|| ("前一个代码".equalsIgnoreCase(str)))
			tok = FUNCCODEPRIOR;
		else if ("上一级代码".equalsIgnoreCase(str))
			tok = SUPERIORCODE;
		else if ("CodeAdjuest".equalsIgnoreCase(str)
				|| ("代码调整".equalsIgnoreCase(str)))
			tok = FUNCCODEADJUST;
		else if ("FuncHisPriorMenu".equalsIgnoreCase(str)
				|| ("上一个历史记录指标值".equalsIgnoreCase(str)))
			tok = FUNCHISPRIORMENU;
		else if ("FuncHisFirstMenu".equalsIgnoreCase(str)
				|| ("历史记录最初指标值".equalsIgnoreCase(str)))
			tok = FUNCHISFIRSTMENU;
		else if ("FUNCMOD".equalsIgnoreCase(str) || "取余数".equalsIgnoreCase(str))
			tok = FUNCMOD;
		else if ("ISNULL".equalsIgnoreCase(str) || "为空".equalsIgnoreCase(str))
			tok = FUNCISNULL;
		else if ("POWER".equalsIgnoreCase(str) || "幂".equalsIgnoreCase(str))
			tok = FUNCPOWER;
		else if ("SalaryA00Z0".equalsIgnoreCase(str)
				|| "归属日期".equalsIgnoreCase(str))
			tok = FUNCSALARYA00Z0;
		else if ("LoginName".equalsIgnoreCase(str)
				|| "登录用户名".equalsIgnoreCase(str))
			tok = FUNCLOGINNAME;
		else if ("报批人单位".equalsIgnoreCase(str))
			tok = FUNCAPPEALUN;
		else if ("报批人部门".equalsIgnoreCase(str))
			tok = FUNCAPPEALUM;
		else if ("报批人岗位".equalsIgnoreCase(str))
			tok = FUNCAPPEALPOS;
		else if ("本单位".equalsIgnoreCase(str))
			tok = THISUNIT;
		else if ("业务单位".equalsIgnoreCase(str))
			tok = BUSIUNIT;		
		else if ("分组汇总".equalsIgnoreCase(str))
			tok = GROUPSTAT;
		else if ("统计时间".equalsIgnoreCase(str))
			tok = STATTIME;
		else if ("统计月数".equalsIgnoreCase(str)|| "GroupMonths".equalsIgnoreCase(str))
			tok = STATMONTH;
		else if ("序号".equalsIgnoreCase(str))
			tok = SEQUENCE;
		else if ("取自于".equalsIgnoreCase(str))
			tok = GETFROM;
		else if ("取部门值".equalsIgnoreCase(str)|| "GetE0122Value".equalsIgnoreCase(str))
			tok = E0122VALUE;
		else if ("取岗位值".equalsIgnoreCase(str)|| "GetE01a1Value".equalsIgnoreCase(str))
			tok = E01A1VALUE;
		else if ("取部门值".equalsIgnoreCase(str)|| "GetE0122Value".equalsIgnoreCase(str))
			tok = E0122VALUE;
		else if ("取上月实发工资人数".equalsIgnoreCase(str))
			tok = FUNSYGZRS;
		else if ("执行存储过程".equalsIgnoreCase(str))
			tok = FUNCPROCEDURE;
		else if ("对象指标".equalsIgnoreCase(str))
			tok = FUNCOBJECTFIELD;
		else if ("预算汇总".equalsIgnoreCase(str))
			tok = FUNBUDGETSUM;
		else if ("可休天数".equalsIgnoreCase(str))
			tok = FUNKXDAYS;
		else if ("已休天数".equalsIgnoreCase(str))
			tok = FUNYXDAYS;
		else if ("申请时长".equalsIgnoreCase(str))
			tok = FUNQJDAYS;
		else if ("就近套级套档".equalsIgnoreCase(str))
			tok = FUNJJTJTD;
		else if ("取专项附加额".equalsIgnoreCase(str))
			tok = SPECIALADDAMOUNT;
		if (tok != 0) {
			token_type = FUNC;
			return true;
		} else {
			return false;
		}
	}

	private boolean isFieldSet(String strToken) {
		try {
			if (DataDictionary.getFieldSetVo(strToken.toLowerCase()) != null) {
				this.FieldSet = DataDictionary
						.getFieldSetVo(strToken.toLowerCase());
				token_type = FIELDSET;
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean IsFieldItem(String strToken) {
		try {
			if(this.isGetB0110Param||this.isGetK01a1Param) //如果是取部门值函数 || 如果是取岗位值函数 (人事异动计算公式用到该函数时，指标列表不会含有 结果指标)
			{
				
				if("职位".equalsIgnoreCase(strToken)|| "职位编码".equalsIgnoreCase(strToken)|| "职位名称".equalsIgnoreCase(strToken)
						|| "拟职位".equalsIgnoreCase(strToken)|| "拟职位编码".equalsIgnoreCase(strToken)|| "拟职位名称".equalsIgnoreCase(strToken)
						|| "现职位".equalsIgnoreCase(strToken)|| "现职位编码".equalsIgnoreCase(strToken)|| "现职位名称".equalsIgnoreCase(strToken))
				{
					if ("岗位名称".equalsIgnoreCase(ResourceFactory.getProperty("e01a1.label"))) {
						strToken = strToken.replaceAll("职位编码", "岗位名称");
						strToken = strToken.replaceAll("职位名称", "岗位名称");
						strToken = strToken.replaceAll("职位", "岗位名称");
					}
				}
				
				
				ArrayList allUsedFields = DataDictionary.getAllFieldItemList(
						Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				for (int i = 0; i < allUsedFields.size(); i++) {
					Field = (FieldItem) allUsedFields.get(i);
					String itemdesc=Field.getItemdesc();
					itemdesc=itemdesc.replaceAll("〔","(").replaceAll("〕",")");
					itemdesc=itemdesc.replaceAll("％","%"); //20161017 dengcan 企业年金单位正常缴费8.33％
					if (Field.getItemid().equalsIgnoreCase(strToken)) {
							if(this.isGetB0110Param&& "B".equalsIgnoreCase(Field.getFieldsetid().substring(0,1)))
								condItemList.add((FieldItem)Field.clone()); 
							if(this.isGetB0110Param)
								condItemList2.add((FieldItem)Field.clone()); 
							if(this.isGetK01a1Param&& "K".equalsIgnoreCase(Field.getFieldsetid().substring(0,1)))//岗位
								condItemK01a1List.add((FieldItem)Field.clone()); 
							if(this.isGetK01a1Param)
								condItemK01a1List2.add((FieldItem)Field.clone()); 
							token_type = FIELDITEM;
							return true;
					} 
					else if (Field.getItemdesc().equalsIgnoreCase(strToken)||itemdesc.equalsIgnoreCase(strToken)||("职位名称".equalsIgnoreCase(Field.getItemdesc())&&"岗位名称".equalsIgnoreCase(strToken))) {
							if ("b0110".equalsIgnoreCase(Field.getItemid())
												&& InfoGroupFlag == forUnit) {
											FieldItem a_item = (FieldItem) Field.cloneItem();
											a_item.setFieldsetid("B01");
											Field = a_item;
								}
							if ("e01a1".equalsIgnoreCase(Field.getItemid())
									&& InfoGroupFlag == this.forPosition) {
								FieldItem a_item = (FieldItem) Field.cloneItem();
								a_item.setFieldsetid("K01");
								Field = a_item;
							}
							
							if(this.isGetB0110Param&& "B".equalsIgnoreCase(Field.getFieldsetid().substring(0,1)))
								condItemList.add((FieldItem)Field.clone()); 
							if(this.isGetB0110Param)
								condItemList2.add((FieldItem)Field.clone()); 
							if(this.isGetK01a1Param&& "K".equalsIgnoreCase(Field.getFieldsetid().substring(0,1)))//岗位
								condItemK01a1List.add((FieldItem)Field.clone()); 
							if(this.isGetK01a1Param)
								condItemK01a1List2.add((FieldItem)Field.clone()); 
							token_type = FIELDITEM;
							return true;
					} 
				}
			}
			
			 
			
			ArrayList FFields = getFieldItems();	
			for (int i = 0; i < FFields.size(); i++) {
				Field = (FieldItem) FFields.get(i);
				if(Field == null)
					continue;
				// System.out.println(Field.getItemid() + ":" +
				// Field.getItemdesc());
				if (Field.getVarible() == 0) {// 是指标
					String itemdesc=Field.getItemdesc();
					itemdesc=itemdesc.replaceAll("〔","(").replaceAll("〕",")");
					itemdesc=itemdesc.replaceAll("％","%"); //20161017 dengcan 企业年金单位正常缴费8.33％
					if (Field.getItemid().equalsIgnoreCase(strToken)) {
						token_type = FIELDITEM;
						return true;
					} else if (Field.getItemdesc().equalsIgnoreCase(strToken)||itemdesc.equalsIgnoreCase(strToken)||("职位名称".equalsIgnoreCase(Field.getItemdesc())&&"岗位名称".equalsIgnoreCase(strToken))||("e0122".equalsIgnoreCase(Field.getItemid())&&("部门名称".equalsIgnoreCase(strToken)||"部门".equalsIgnoreCase(strToken)))) {
						if ("b0110".equalsIgnoreCase(Field.getItemid())
								&& InfoGroupFlag == forUnit) {
							FieldItem a_item = (FieldItem) Field.cloneItem();
							a_item.setFieldsetid("B01");
							Field = a_item;
						}
						
						if ("e0122".equalsIgnoreCase(Field.getItemid())
								&& InfoGroupFlag ==this.forPosition) {
							FieldItem a_item = (FieldItem) Field.cloneItem();
							a_item.setFieldsetid("K01");
							Field = a_item;
						}
						
						
						if ("e01a1".equalsIgnoreCase(Field.getItemid())
								&& InfoGroupFlag == this.forPosition) {
							FieldItem a_item = (FieldItem) Field.cloneItem();
							a_item.setFieldsetid("K01");
							Field = a_item;
						}
						token_type = FIELDITEM;
						
						if(this.isGetB0110Param)
							condItemList2.add((FieldItem)Field.clone()); 
						if(this.isGetK01a1Param)
							condItemK01a1List2.add((FieldItem)Field.clone()); 
						if(this.isSelectSubSet)//是否校验统计表单子集的条件，记录下对应的指标
							subSetConditionFieldItemList.add((FieldItem)Field.clone());
						
						return true;
					} else if ((('现' + Field.getItemdesc()).equalsIgnoreCase(strToken)||('现' +itemdesc).equalsIgnoreCase(strToken)||(("职位名称".equalsIgnoreCase(Field.getItemdesc()) || "现职位名称".equalsIgnoreCase(Field.getItemdesc()))&&"现岗位名称".equalsIgnoreCase(strToken))||("e0122_1".equalsIgnoreCase(Field.getItemid())&&("现部门名称".equalsIgnoreCase(strToken)||"现部门".equalsIgnoreCase(strToken))))
							&& Field.isChangeBefore()) {
						token_type = FIELDITEM;
						
						if(this.isGetB0110Param)
							condItemList2.add((FieldItem)Field.clone()); 
						if(this.isGetK01a1Param)
							condItemK01a1List2.add((FieldItem)Field.clone()); 
						if(this.isSelectSubSet)//是否校验统计表单子集的条件，记录下对应的指标
							subSetConditionFieldItemList.add((FieldItem)Field.clone());
						
						return true;
	
					} else if ((('拟' + Field.getItemdesc()).equalsIgnoreCase(strToken)||('拟' +itemdesc).equalsIgnoreCase(strToken)||(("职位名称".equalsIgnoreCase(Field.getItemdesc()) || "拟职位名称".equalsIgnoreCase(Field.getItemdesc()))&&"拟岗位名称".equalsIgnoreCase(strToken))||("e0122_2".equalsIgnoreCase(Field.getItemid())&&("拟部门名称".equalsIgnoreCase(strToken)||"拟部门".equalsIgnoreCase(strToken))))
							&& Field.isChangeAfter()) {
						token_type = FIELDITEM;
						
						if(this.isGetB0110Param)
							condItemList2.add((FieldItem)Field.clone()); 
						if(this.isGetK01a1Param)
							condItemK01a1List2.add((FieldItem)Field.clone()); 
						if(this.isSelectSubSet)//是否校验统计表单子集的条件，记录下对应的指标
							subSetConditionFieldItemList.add((FieldItem)Field.clone());
						
						return true;
					}
				}else if(Field.getVarible() == 2 && FSource.indexOf("统计表单子集") > -1){//统计表单子集 中 判断子集是否存在
					String itemdesc=Field.getItemdesc() + "的";
					if (itemdesc.equalsIgnoreCase(strToken)) {
						return true;
					}
				}
			}
			
			
			
			if("职位".equalsIgnoreCase(strToken)|| "职位编码".equalsIgnoreCase(strToken)|| "职位名称".equalsIgnoreCase(strToken)
					|| "拟职位".equalsIgnoreCase(strToken)|| "拟职位编码".equalsIgnoreCase(strToken)|| "拟职位名称".equalsIgnoreCase(strToken)
					|| "现职位".equalsIgnoreCase(strToken)|| "现职位编码".equalsIgnoreCase(strToken)|| "现职位名称".equalsIgnoreCase(strToken))
			{
				if ("岗位名称".equalsIgnoreCase(ResourceFactory.getProperty("e01a1.label"))) {
					strToken = strToken.replaceAll("职位编码", "岗位名称");
					strToken = strToken.replaceAll("职位名称", "岗位名称");
					strToken = strToken.replaceAll("职位", "岗位名称");
					
					
					
					for (int i = 0; i < FFields.size(); i++) {
						Field = (FieldItem) FFields.get(i);
						String itemdesc=Field.getItemdesc();
						itemdesc=itemdesc.replaceAll("〔","(").replaceAll("〕",")");
						// System.out.println(Field.getItemid() + ":" +
						// Field.getItemdesc());
						if (Field.getVarible() == 0) {// 是指标
							
							if (Field.getItemid().equalsIgnoreCase(strToken)) {
								token_type = FIELDITEM;
								return true;
							} else if (Field.getItemdesc().equalsIgnoreCase(strToken)||itemdesc.equalsIgnoreCase(strToken)||("职位名称".equalsIgnoreCase(Field.getItemdesc())&&"岗位名称".equalsIgnoreCase(strToken))) {
								if ("b0110".equalsIgnoreCase(Field.getItemid())
										&& InfoGroupFlag == forUnit) {
									FieldItem a_item = (FieldItem) Field.cloneItem();
									a_item.setFieldsetid("B01");
									Field = a_item;
								}
								if ("e01a1".equalsIgnoreCase(Field.getItemid())
										&& InfoGroupFlag == this.forPosition) {
									FieldItem a_item = (FieldItem) Field.cloneItem();
									a_item.setFieldsetid("K01");
									Field = a_item;
								}
								token_type = FIELDITEM;
								return true;
							} else if ((('现' + Field.getItemdesc()).equalsIgnoreCase(strToken)||('现' +itemdesc).equalsIgnoreCase(strToken)||("职位名称".equalsIgnoreCase(Field.getItemdesc())&&"现岗位名称".equalsIgnoreCase(strToken)))
									&& Field.isChangeBefore()) {
								token_type = FIELDITEM;
								return true;
	
							} else if ((('拟' + Field.getItemdesc()).equalsIgnoreCase(strToken)||('拟' +itemdesc).equalsIgnoreCase(strToken)||("职位名称".equalsIgnoreCase(Field.getItemdesc())&&"拟岗位名称".equalsIgnoreCase(strToken)))
									&& Field.isChangeAfter()) {
								token_type = FIELDITEM;
								return true;
							}
	
						}
					}
					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		 
		
		/** 如果未找到指标，则为空,chenmengqing added 20071015 */
		Field = null;
		return false;
	}

	private boolean IsOddVar(String strToken) {
		try {
			ArrayList FFields = getFieldItems();
			for (int i = 0; i < FFields.size(); i++) {
				/* FieldItem */Field = (FieldItem) FFields.get(i);
				if(Field == null)
					continue;
				if (Field.getVarible() == 1) { // 是变量
					if (Field.getItemid().equalsIgnoreCase(strToken)) {
						token_type = ODDVAR;
						
						if(this.isSelectSubSet)//是否校验统计表单子集的条件，记录下对应的指标
							subSetConditionFieldItemList.add((FieldItem)Field.clone());
						return true;
					} else if (Field.getItemdesc().equalsIgnoreCase(strToken)) {
						token_type = ODDVAR;
						
						if(this.isSelectSubSet)//是否校验统计表单子集的条件，记录下对应的指标
							subSetConditionFieldItemList.add((FieldItem)Field.clone());
						return true;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		/** 如果未找到指标，则为空,chenmengqing added 20071015 */
		Field = null;
		return false;
	}

	private boolean IsDelimiter(char c) {
		boolean b = "+-*/%^;:,=<>()；，： \n\t\r".indexOf(c) >= 0;
		return b;
	}

	private boolean IsDigit(char c) {

		return "0123456789".indexOf("" + c) >= 0;
	}

	public void setFSource(String strCSource) {
		/** 对数值型字段,如果表达式为空的话,自动给它赋0,chenmengqing addad */
		strCSource = strCSource.replaceAll("单位编码", "单位名称");
/*		strCSource = strCSource.replaceAll("职位编码", "职位名称");
		// cmq 把职位名称换成"岗位名称" at 20091222 
		if (ResourceFactory.getProperty("e01a1.label").equalsIgnoreCase("岗位名称")) {
			strCSource = strCSource.replaceAll("职位名称", "岗位名称");
		}*/
		if (VarType == FLOAT || VarType == INT) {
			if (strCSource.length() == 0)
				strCSource = "0";
		}
		// cmq end. at 20070910
		FSource = strCSource;
		nFSourceLen = FSource.length();
		nCurPos = 0;
	}

	public String getResult() {
		return result;
	}

	/**
	 * 获得计算公式错误描述信息
	 * 
	 * @return
	 */
	public String getStrError() {
		return strError;
	}

	public boolean getFError() {
		return FError;
	}

	private void setStrError(String strError) {
		this.strError = strError;
		// System.out.println(strError);
	}

	private void SError(int nErrorNo) throws GeneralException {
		String strMsg = null;
		FError = true;
		switch (nErrorNo) {
		case E_PRO_NO: {
			strMsg = "无此存储过程";
			break;
		}
		case E_PRO_LOSSPARAM: {
			strMsg = "存储过程参数个数不对";
			break;
		}
		case E_PRO_TYPEERROR: {
			strMsg = "此处参数类型不对";
			break;
		}
		case E_LOSSQUOTE: {
			strMsg = "此处缺少引号";
			break;
		}
		case E_LOSSBASESET: {
			strMsg = "此处缺少基于子集关键字";
			break;
		}
		case E_NOSETITEM: {
			strMsg = "此处条件不是子集里的指标";
			break;
		}
		case E_LOSSSET: {
			strMsg = "此处缺少子集";
			break;
		}
		case E_LOSSLPARENTHESE: {
			strMsg = "此处缺少左括号";
			break;
		}
		case E_LOSSRPARENTHESE: {
			strMsg = "此处缺少右括号";
			break;
		}
		case E_USEQJCOMMA: {
			strMsg = "此处不能用全角逗号";
			break;
		}
		case E_LOSSCOMMA: {
			strMsg = "此处缺少逗号";
			break;
		}
		case E_LOSSCOLON: {
			strMsg = "此处缺少冒号";
			break;
		}
		case E_LOSSSEMICOLON: {
			strMsg = "此处缺少分号";
			break;
		}
		case E_LOSSEND: {
			strMsg = "此处缺少结束";
			break;
		}
		case E_SYNTAX: {
			strMsg = "此处语法错误";
			break;
		}
		case E_NOTSAMETYPE: {
			strMsg = "数据类型不一致";
			break;
		}
		case E_NOTBEBOOL:  {
			strMsg = "此处不可以是逻辑型";
			break;
		}
		case E_FNOTSAMETYPE: {
			strMsg = "公式左边和右边数据类型不一致";
			break;
		}
		case E_MUSTBEDATE: {
			strMsg = "此处必须是日期型，格式为#yyyy.mm.dd#，如#2002.5.16#";
			break;
		}
		case E_MUSTBEINTEGER: {
			strMsg = "此处必须数是整型";
			break;
		}
		case E_INTSCOPE2: {
			strMsg = "此处整型数值范围必须是（1-10）";
			break;
		}
		case E_INTSCOPE: {
			strMsg = "此处整型数值范围必须是（1-99）";
			break;
		}
		case E_MUSTBENUMBER: {
			strMsg = "此处必须是数值型";
			break;
		}
		case E_MUSTBEBOOL: {
			strMsg = "此处必须是逻辑型";
			break;
		}
		case E_MUSTBESTRING: {
			strMsg = "此处必须是字符型";
			break;
		}
		case E_UNKNOWNSTR: {
			strMsg = "此处有未知字符串";
			break;
		}
		case E_PARTTIMEJOBPARAM: {
			strMsg = "兼职参数设置不完整";
			break;
		}
		case E_GETSELECT: {
			strMsg = "统计、取历史记录和代码转名称能在条件中使用，在临时变量中要单独使用";
			break;
		}
		case E_MUSTBESQLSYMBOL: {
			strMsg = "此处必须是SELECT类型符号：FIRST(的最初第一条记录)，LAST（的最近第一条记录），MAX（的最大值），MIN（的最小值），SUM（的总和），AVG（的平均值），COUNT（的个数）";
			break;
		}
		case E_MUSTBEGETSYMBOL: {
			strMsg = "此处必须是GET类型符号：INCREASE（最初第）、DECREASE（最近第）";
			break;
		}
		case E_MUSTBEFIELDITEM: {
			strMsg = "此处必须是指标";
			break;
		}
		case E_MUSTBEFIELDITEMUN: {
			strMsg = "此处必须是单位信息集指标";
			break;
		}
		case E_MUSTBEFIELDITEMPOS: {
			strMsg = "此处必须是岗位信息集指标";
			break;
		}
		case E_MUSTBEONEFLDSET: {
			strMsg = "条件表达和统计指标必须是同一个子集或主集指标";
			break;
		}
		case E_MUSTBETIMEFIELD: {
			strMsg = "此处必须是时间指标";
			break;
		}
		case E_MUSTBESAMESET: {
			strMsg = "此处必须是同一子集的指标";
			break;
		}
		case E_MUSTBECODEFIELD: {
			strMsg = "此处必须是代码型指标";
			break;
		}
		case E_MUSTBEINTEGERMENU: {
			strMsg = "此处必须是整型指标或临时变量";
			break;
		}
		case E_MUSTBESUBSET: {
			strMsg = "此处必须子集指标，不能是主集指标";
			break;
		}
		case E_NOTFINDFIELD: {
			strMsg = "没有此指标";
			break;
		}
		case E_NOTFINDODDVAR: {
			strMsg = "不是临时变量";
			break;
		}
		case E_LOSSBRACK1: {
			strMsg = "指标缺右方括号";
			break;
		}
		case E_LOSSBRACK2: {
			strMsg = "指标缺右大括号";
			break;
		}
		case E_LOSSTHEN: {
			strMsg = "缺少那么";
			break;
		}
		case E_LOSSELSE: {
			strMsg = "缺少否则";
			break;
		}
		case E_LOSSIIF: {
			strMsg = "缺少如果";
			break;
		}
		case E_LOSSTEMPTABLENAME: {
			strMsg = "临时表不能为空";
			break;
		}
		case E_LOSSGETEND: {
			strMsg = "缺少取历史记录结束串：条记录";
			break;
		}
		case E_LOSSEQUALE: {
			strMsg = "此处缺少等号";
			break;
		}
		case E_LOSSASIGN: {
			strMsg = "此处缺少赋值号";
		}
		case E_FIELDSETNOTFOUNT: {
			// strMsg = "此处指标集错误";
		}
		case E_USEFUNCMOD: {
			strMsg = "Oracle与DB2不支持'%'，请使用'取余数(除数, 被除数)'函数";
			break;
		}
		case E_USEFUNCINT: {
			strMsg = "SQL、Oracle、DB2不支持'\'，请使用'取整(数值1)/取整(数值2)";
			break;
		}
		case E_FUNCTON2: {
			strMsg = "代码转名称2,目前不能用于临时变量之中，可以用代码转名称函数代替!";
			break;
		}
		case E_STDNDARDNOTEXIST: {
			strMsg = "标准表不存在!";
			break;
		}
		case E_STDNDARDNOTTWODIM: {
			strMsg = "标准表必须是二维！";
			break;
		}
		case E_MENUNOTMATCH: {
			strMsg = "传入的指标与标准中的指标类型不匹配!";
			break;
		}
		case E_MUSTBEONETWO: {
			strMsg = "参数必须为1或2!";
			break;
		}
		case E_BUDGERGRPNOSAME: {
			strMsg = "同一个公式中不同预算汇总的分组指标必须相同!";
			break;
		}
		case E_FUNCDTOCERROR:{
			strMsg = "此处仅支持如下格式：YYYY-MM-DD | YYYY-MM-DD HH24 | YYYY-MM-DD HH24:MI | YYYY-MM-DD HH24:MI:SS";
			break;
		}
		case E_LESSTHANOREQUALZERO: {
		    strMsg = "参数应大于0！";
		    break;
		}
		case E_UPDATETRANSFERLIBRARY: {
		    strMsg = "取专项附加额函数需用7.2.1及以上转库大师，库维护处右键执行个税专项附加存储过程维护！";
		    break;
		}
		case E_CANTINCLUDECOMPLEX: {
		    strMsg = "统计表单子集函数不能包含复杂的定义规则，仅支持四则运算";
		    break;
		}
		case E_CANCHOOSEZEROTOTHREE: {
		    strMsg = "此处参数只能是0或1或2，0表示全部，1表示当年已休天数，2表示上年结余已休天数.";
		    break;
		}
		case E_JUSTA00Z0ORA00Z2: {
		    strMsg = "归属函数仅能选择带引号的'a00z0'或者'a00z2'";
		    break;
		}
		default: {
			strMsg = "未知错误";
			break;
		}
		}
		if (nCurPos <= 200) {
			nCurPos = nCurPos < 0 ? 0 : nCurPos;
			if (nCurPos >= FSource.length())
				strMsg = FSource.substring(0, FSource.length()) + "^^^^"
						+ strMsg;
			else
				strMsg = FSource.substring(0, nCurPos) + "^^^^" + strMsg;

		} else {
			strMsg = FSource.substring(nCurPos - 199, nCurPos) + "^^^^"
					+ strMsg;
		}
		setStrError(strMsg);
		throw new GeneralException(strMsg);

	}

	public ArrayList getFCTONSQLS() {
		return FCTONSQLS;
	}

	public boolean isFError() {
		return FError;
	}

	public void setInfoGroupFlag(int infoGroupFlag) {
		InfoGroupFlag = infoGroupFlag;
	}

	public void setModeFlag(int modeFlag) {
		ModeFlag = modeFlag;
	}

	public void setDbPre(String dbPre) {
		DbPre = dbPre;
	}

	public void setDBType(int dbType) {
		DBType = dbType;
	}

	public void setTempTableName(String tempTableName) {
		TempTableName = tempTableName;
	}

	private ArrayList getFieldItems() {
		return fieldItems;
	}

	public void setFieldItems(ArrayList fieldItems) {
		this.fieldItems = fieldItems;
	}

	public String getResultString() {
		return ResultString;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public void setMapUsedFieldItems(HashMap mapUsedFieldItems) {
		this.mapUsedFieldItems = mapUsedFieldItems;
	}

	public int getInfoGroupFlag() {
		return InfoGroupFlag;
	}

	public ArrayList getUsedSets() {
		return UsedSets;
	}

	public void setUsedSets(ArrayList usedSets) {
		UsedSets = usedSets;
	}

	public HashMap getMapUsedFieldItems() {
		return mapUsedFieldItems;
	}

	public void setWhereText(String whereText) {
		if(whereText!=null&&whereText.trim().length()>0)
			this.whereText = "(" + whereText + ")";
	}

	public String getTargetFieldDataType() {
		return targetFieldDataType;
	}

	public void setTargetFieldDataType(String targetFieldDataType) {
		this.targetFieldDataType = targetFieldDataType;
	}

	public int getVarType() {
		return VarType;
	}

	public void setVarType(int varType) {
		VarType = varType;
	}

	/** *************sun.xin*************** */
	/**
	 * 没有userView的构造器
	 */
	/*
	 * public YksjParser(ArrayList fieldItemList, int modeFlag, int varType, int
	 * infoGroup, String strTempTableName, String dbPre) {
	 *  // 得到用户信息及权限 setUserView(null);
	 *  // 得到指标列表 setFieldItems(fieldItemList);
	 *  // 设置查询模式是Normal还是Search setModeFlag(modeFlag);
	 * 
	 * setInfoGroupFlag(infoGroup); setVarType(varType); //
	 * setVariableType(varType);
	 * 
	 * setDbPre(dbPre); if (strTempTableName == null ||
	 * strTempTableName.trim().length() < 1) strTempTableName = "Ht";
	 * setTempTableName(strTempTableName);
	 *  // 设置数据库类型 setDBType(Sql_switcher.searchDbServer()); TempTableName =
	 * "temp_warn";
	 *  }
	 */

	/**
	 * 对条件过滤的run方法
	 * 
	 * @param str
	 * @param ymc
	 * @param targetField//以取掉
	 * @param targetTable//以取掉
	 * @param dao
	 * @param whereText
	 * @param con
	 * @param targetFieldDataType
	 * @param flg//以取掉
	 * @param codeset
	 * @return
	 */

	// 执行 对条件过滤的run方法：run_Where() 是否支持临时变量 isSupportVar=false;
	public String run_Where(String str, YearMonthCount ymc, String targetField,
			String targetTable, ContentDAO dao, String whereText,
			Connection con, String targetFieldDataType, String codeset) {
		this.con = con;
		if (this.isSupportVar && this.InfoGroupFlag == YksjParser.forPerson) // 支持临时变量
			excecuteVarTable(str, "(" + whereText + ")");
		this.codeset = codeset;
		String s = "";
		/* this.flg = flg; */
		this.targetTable = targetTable;
		this.targetField = targetField;
		this.ymc = ymc;
		this.dao = dao;
		this.setWhereText(whereText);
		this.targetFieldDataType = targetFieldDataType;
		try {
			s = run_where(str);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return s;
	}

	private void excecuteVarTable(String str, String where_str) {
		try {
			str = PubFunc.keyWord_reback(str);
			String currym = ConstantParamter.getAppdate(this.userView
					.getUserName());
			String stry = currym.substring(0, 4);
			String strm = currym.substring(5, 7);
			String strc = "1";
			YearMonthCount ymc = new YearMonthCount(Integer.parseInt(stry),
					Integer.parseInt(strm), Integer.parseInt(strc));

			ContentDAO dao = new ContentDAO(this.con);
			ArrayList list = getInvolveVarList(str);
			if(this.flg!=2) //如果不是从报表里计算临时变量
				this.varList=(ArrayList)list.clone();
			fieldItems.addAll(list);
			ArrayList usedlist = initUsedFields();
			usedlist.addAll((ArrayList) list.clone());

			String tmptable ="t#"+this.userView.getUserName() + "_sf_mid"; // this.userView.getUserName() + "midtable";
			if(this.InfoGroupFlag == YksjParser.forPerson)
				createMidTable(usedlist, tmptable, "A0100");
			else if(this.InfoGroupFlag == YksjParser.forUnit)
				createMidTable(usedlist, tmptable, "B0110");
			else if(this.InfoGroupFlag == YksjParser.forPosition)
				createMidTable(usedlist, tmptable, "E01A1");
	//		dao.update("create index "+tmptable+"_index_yk  on "+tmptable+" (A0100)");
			 
			StringBuffer buf = new StringBuffer("");
			buf.append("insert into ");
			buf.append(tmptable);
			if(this.InfoGroupFlag == YksjParser.forPerson)
			{
				buf.append("(A0000,A0100,B0110,E0122,A0101,basepre) select A0000,A0100,B0110,E0122,A0101,'"
								+ this.DbPre + "' FROM ");
				buf.append(this.DbPre + "A01");
				if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
				{
					buf.append(" where exists (" + this.existWhereText +" ) ");
				}
				else
				{
					if (!("()".equalsIgnoreCase(whereText))
							&& whereText.trim().length() > 0) {
						buf.append(" where a0100 in " + whereText);
					}
				}
			}
			else if(this.InfoGroupFlag == YksjParser.forUnit)
			{
				buf.append("(B0110) select B0110 FROM ");
				buf.append("B01");
				if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
				{
					buf.append(" where exists (" + this.existWhereText +" ) ");
				}
				else
				{
					if (!("()".equalsIgnoreCase(whereText))
							&& whereText.trim().length() > 0) {
						buf.append(" where b0110 in " + whereText);
					}
				}
			}
			else if(this.InfoGroupFlag == YksjParser.forPosition)
			{
				buf.append("(E01A1) select E01A1 FROM ");
				buf.append("K01");
				if(this.existWhereText!=null&&this.existWhereText.trim().length()>0)
				{
					buf.append(" where exists (" + this.existWhereText +" ) ");
				}
				else
				{
					if (!("()".equalsIgnoreCase(whereText))
							&& whereText.trim().length() > 0) {
						buf.append(" where E01A1 in " + whereText);
					}
				}
			}
			 
			dao.update(buf.toString());
			String _stdTmpTable = this.getStdTmpTable();
			int _varType = this.getVarType();
			this.setStdTmpTable(tmptable);
			 
			String _targetTable=this.targetTable;
			String _targetField=this.targetField;
				// this.setStdTmpTable(targetTable);
			int _targetFieldLen=this.targetFieldLen;
			String _whereText=this.whereText;
			String _targetFieldDataType=this.targetFieldDataType;
	 	    int _flg=this.flg;
	 	    String _codeset=this.codeset;
			
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				String fldtype = item.getItemtype();
				String fldname = item.getItemid();
				String formular = item.getFormula();
				
				if(formular.indexOf("取自于")!=-1)
				{
					return;
				}
				
				this.setVarType(getDataType(fldtype));
				run(formular, ymc, fldname, tmptable, dao, "", this.con,
						fldtype, item.getItemlength(), 1, item.getCodesetid());
			}
			
			this.flg=_flg;
			this.codeset=_codeset;
			this.targetTable=_targetTable;
			this.targetField=_targetField;
			this.targetFieldLen=_targetFieldLen;
			this.whereText=_whereText;
			this.targetFieldDataType=_targetFieldDataType; 
			this.setVarType(_varType);
			this.setStdTmpTable(_stdTmpTable);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 数值类型进行转换
	 * 
	 * @param type
	 * @return
	 */
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
		//	if (dbw.isExistTable(tablename, false))
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
			Field field = new Field("userflag", "userflag");
			field.setLength(50);
			field.setDatatype(DataType.STRING);
			table.addField(field);
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
		FieldItem fielditem=null;
		if(this.InfoGroupFlag == YksjParser.forPerson)
		{		
			/** 人员排序号 */
			fielditem = new FieldItem("A01", "A0000");
			fielditem.setItemdesc("a0000");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("N");
			fielditem.setItemlength(9);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/** 人员编号 */
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
			fielditem.setItemlength(50);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
			/** 姓名 */
			fielditem = new FieldItem("A01", "A0101");
			fielditem.setItemdesc("姓名");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(80);
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
	
			fielditem = new FieldItem("A01", "basepre");
			fielditem.setItemdesc("人员库");
			fielditem.setCodesetid("0");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		}
		else if(this.InfoGroupFlag == YksjParser.forUnit)
		{
			fielditem = new FieldItem("B01", "B0110");
			fielditem.setItemdesc("单位名称");
			fielditem.setCodesetid("UN");
			fielditem.setItemtype("A");
			fielditem.setItemlength(30);
			fielditem.setDecimalwidth(0);
			fieldlist.add(fielditem);
		}
		else if(this.InfoGroupFlag == YksjParser.forPosition)
		{
			fielditem = new FieldItem("E01A1", "E01A1");
			fielditem.setItemtype("A");
			fielditem.setCodesetid("@K");
			fielditem.setItemlength(30);
			fieldlist.add(fielditem);
		}
		return fieldlist;
	}

	/**
	 * 取得公式涉及到的临时变量 目前只支持人员信息里定义的临时变量
	 * 
	 * @param str
	 * @return
	 */
	private ArrayList getInvolveVarList(String str) {
		ArrayList list = new ArrayList();
		try {
			ContentDAO dao = new ContentDAO(this.con);
			ArrayList tempList = new ArrayList();
			
			String sql=" select * from midvariable where nflag=3 and templetid=0  order by sorting ";
			if(this.func_module_sql!=null&&this.func_module_sql.trim().length()>0) //人事异动
			{ 
				sql=this.func_module_sql+" order by sorting";
			}
			RowSet rset = dao
					.search(sql);
			while (rset.next()) {
				FieldItem item = new FieldItem();
				item.setItemid(rset.getString("cname"));
				item.setFieldsetid("");// 没有实际含义
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				item.setCodesetid(rset.getString("codesetid"));
				switch (rset.getInt("ntype")) {
				case 1://
					item.setItemtype("N");
					break;
				case 2:
				case 4:// 代码型
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				tempList.add(item);
			}
			
			if(this.flg==2&&this.varList.size()>0)
			{
				tempList=(ArrayList)this.varList.clone();
			}
			HashMap varMap = new HashMap();
			searchVar(tempList, str, varMap);
			if (varMap.size() > 0) {
				Set keySet = varMap.keySet();
				StringBuffer _str = new StringBuffer("");
				for (Iterator t = keySet.iterator(); t.hasNext();) {
					_str.append(",'" + (String) t.next() + "'");
				}
				
				if(this.flg==2&&this.varList.size()>0)
				{
					for(int i=0;i<this.varList.size();i++)
					{
						FieldItem item=(FieldItem)this.varList.get(i);
						String itemid=item.getItemid();
						if(_str.indexOf("'"+itemid.toLowerCase()+"'")!=-1)
						{
							list.add(item);
						}
						
					}
				}
				else
				{
					String _str2=" select * from midvariable where nflag=3 and templetid=0  and lower(cname) in ("
						+ _str.substring(1).toLowerCase() + ")  order by sorting ";
					if(this.func_module_sql!=null&&this.func_module_sql.trim().length()>0) //人事异动
					{ 
						 
							_str2=this.func_module_sql+" and lower(cname) in ("+ _str.substring(1).toLowerCase() + ") ";
							_str2+=" order by sorting"; 
					}
					rset = dao.search(_str2);
					while (rset.next()) {
						FieldItem item = new FieldItem();
						item.setItemid(rset.getString("cname"));
						item.setFieldsetid("");// 没有实际含义
						item.setItemdesc(rset.getString("chz"));
						item.setItemlength(rset.getInt("fldlen"));
						item.setDecimalwidth(rset.getInt("flddec"));
						item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
						item.setCodesetid(rset.getString("codesetid"));
						switch (rset.getInt("ntype")) {
						case 1://
							item.setItemtype("N");
							break;
						case 2:
						case 4:// 代码型
							item.setItemtype("A");
							break;
						case 3:
							item.setItemtype("D");
							break;
						}
						item.setVarible(1);
						list.add(item);
					}
				}
			}

			if (rset != null)
				rset.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void searchVar(ArrayList midList, String formualr_str, HashMap varMap) {
		FieldItem item;
		for (int j = 0; j < midList.size(); j++) {
			item = (FieldItem) midList.get(j);
			String item_id = item.getItemid().toLowerCase();
			
			String item_desc = item.getItemdesc().trim().toLowerCase();
			String formula = item.getFormula();
			if ((formualr_str.toLowerCase().indexOf(item_desc) != -1 || formualr_str.toLowerCase().indexOf(item_id) != -1)
					&& varMap.get(item_id) == null) {
				varMap.put(item_id, "1"); 
				searchVar(midList, formula, varMap);

			}

		}

	}

	/**
	 * 公式校验函数
	 * 
	 * @param str
	 *            待校验的公式
	 * @return 公式正确与否 注意：如果校验发现公式错误，需要调用getStrError()获取相关错误信息
	 * @throws GeneralException 
	 */
	public boolean Verify_where(String str) throws GeneralException {
		try {
			this.isVerify = true;
			bVerify = true; // 语法效验默认为正确

			RetValue retValue = new RetValue(); // 返回值类型分析类
			str = PubFunc.keyWord_reback(str);
			setFSource(str); // 设置全局的要分析的表达式

			init();// 清空初始化

			// 如果临时表名称为空并且查询对象类型为forSearch
			if ((TempTableName == null) && (ModeFlag == forSearch)) {
				SError(E_LOSSTEMPTABLENAME); // 临时表不能为空
			}

			if (!Get_Expr(retValue)) {
				if (retValue.isIntType() || retValue.isFloatType()) {
					if (VarType != FLOAT && VarType != INT) {
						SError(E_FNOTSAMETYPE);
					}
				}
				if (retValue.IsDateType()) {
					if (VarType != DATEVALUE) {
						SError(E_FNOTSAMETYPE);
					}
				}
				if (retValue.IsStringType()) {
					if(!this.isGetFrom)
					{
						if (VarType != STRVALUE) {
							SError(E_FNOTSAMETYPE);
						}
					}
				} 
				if (retValue.isBooleanType()) {
					if (VarType != LOGIC) {
						SError(E_FNOTSAMETYPE);
					}
				}
				return false;
			}
			if (retValue.isIntType() || retValue.isFloatType()) {
				if (VarType != FLOAT && VarType != INT) {
					SError(E_FNOTSAMETYPE);
				}
			}
			if (retValue.IsDateType()) {
				if (VarType != DATEVALUE) {
					SError(E_FNOTSAMETYPE);
				}
			}
			if (retValue.IsStringType()) {
				if(!this.isGetFrom)
				{
					if (VarType != STRVALUE) {
						SError(E_FNOTSAMETYPE);
					}
				}
			}
			// System.out.println(VarType+"------"+LOGIC);
			if (retValue.isBooleanType()) {
				if (VarType != LOGIC) {
					SError(E_FNOTSAMETYPE);
				}
			}

		} catch (GeneralException e) {
			Category.getInstance(this.getClass()).debug(e.getMessage());
			//throw GeneralExceptionHandler.Handle(e);
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 公式校验函数
	 * 
	 * @param str
	 *            待校验的公式(不校验返回类型)
	 * @author dengcan
	 * @return 公式正确与否 注意：如果校验发现公式错误，需要调用getStrError()获取相关错误信息
	 */
	public boolean Verify_whereNoRetTypte(String str) {
		try {
			str = PubFunc.keyWord_reback(str);
			bVerify = true; // 语法效验默认为正确

			RetValue retValue = new RetValue(); // 返回值类型分析类

			setFSource(str); // 设置全局的要分析的表达式

			init();// 清空初始化

			// 如果临时表名称为空并且查询对象类型为forSearch
			if ((TempTableName == null) && (ModeFlag == forSearch)) {
				SError(E_LOSSTEMPTABLENAME); // 临时表不能为空
			}

			if (!Get_Expr(retValue)) {
				return false;
			}
		} catch (GeneralException e) {
			Category.getInstance(this.getClass()).debug(e.getMessage());
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			Category.getInstance(this.getClass()).debug(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param str
	 * @return getSQL()条件语句，TempTableName 临时表格的名字
	 * @throws GeneralException
	 * @throws SQLException
	 */
	public String run_where(String str) throws GeneralException, SQLException {
		str = PubFunc.keyWord_reback(str);
		RetValue retValue = new RetValue();
		str = str.replaceAll("单位编码", "单位名称");
/*		str = str.replaceAll("职位编码", "职位名称");
		// cmq 把职位名称换成"岗位名称" at 20091222
		if (ResourceFactory.getProperty("e01a1.label").equalsIgnoreCase("岗位名称")) {
			str = str.replaceAll("职位名称", "岗位名称");
		}*/
		setFSource(str);
		init();// 清空初始化

		if ((TempTableName == null) && (ModeFlag == forSearch)) {
			SError(E_LOSSTEMPTABLENAME);
		}

		if (!Get_Expr(retValue)) {
			return null;
		}

		if (ModeFlag == forSearch) {
			createTempTable();
			SQL_SubSet_where();
			//代码转名称2函数将值塞入FCTONSQLS，如果不执行run_CTONSQL，导致临时表最终结果没有算
			//但是这仅仅对于调用run_where方法的时候获取getTempTableName临时表，和临时表关联查询代码转名称才能使用，否则就是使用不了sunjian 2018-9-19
			run_CTONSQL();
			/** 代码调整, */
			run_STDSQL();
			
		}
		if (bVerify)
			return "";

		if (retValue.isIntType() || retValue.isFloatType()) {
			if (VarType != FLOAT && VarType != INT) {
				SError(E_FNOTSAMETYPE);
			}
		}
		if (retValue.IsDateType()) {
			if (VarType != DATEVALUE) {
				SError(E_FNOTSAMETYPE);
			}
		}
		if (retValue.IsStringType()) {
			if (VarType != STRVALUE) {
				SError(E_FNOTSAMETYPE);
			}
		}
		// System.out.println(VarType+"------"+LOGIC);
		if (retValue.isBooleanType()) {
			if (VarType != LOGIC) {
				SError(E_FNOTSAMETYPE);
			}
		}

		ResultString = retValue.ValueToString();
		return result;
	}

	private void SQL_SubSet_where() {
		// this.targetField
		// SQLS.clear();
		// System.out.println(this.SQLS);
		String strCurSet = "", strCurMenu = "", strSet1 = "", strSet2 = "", strJoin = "", strKey = "";
		StringBuffer strFldList = new StringBuffer();
		StringBuffer strSQL = new StringBuffer();
		StringBuffer strSQLSet = new StringBuffer();
		StringBuffer strSQLFrom = new StringBuffer();
		StringBuffer strSQLLeft = new StringBuffer();
		StringBuffer strSQLOn = new StringBuffer();
		FieldItem fieldTemp = null;
		int count = 0;

		if (mapUsedFieldItems.size() > 0 && this.isSupportVar
				&& this.InfoGroupFlag == YksjParser.forPerson) {
			String tmptable ="t#"+this.userView.getUserName() + "_sf_mid"; // this.userView.getUserName() + "midtable";
			StringBuffer _sql = new StringBuffer("");
			Iterator it = mapUsedFieldItems.keySet().iterator();
			while (it.hasNext()) {
				fieldTemp = (FieldItem) mapUsedFieldItems.get(it.next());
				String fieldname = fieldTemp.getItemid().toUpperCase();
				if (fieldTemp.getVarible() != 1)
					continue;
				if (fieldname.indexOf("SELECT_") != -1)
					continue;
				if (fieldname.indexOf("STD_") != -1)
					continue;
				_sql.setLength(0);
				_sql.append("update " + this.TempTableName + " set "
						+ fieldname + "=");
				_sql.append(" ( select " + fieldname + " from " + tmptable
						+ " where " + this.TempTableName + ".a0100=" + tmptable
						+ ".a0100 ) where exists ");
				_sql.append(" ( select null from " + tmptable + " where "
						+ this.TempTableName + ".a0100=" + tmptable
						+ ".a0100 ) ");
				this.SQLS.add(_sql.toString());
			}
		}

		/** *************临时表得到条件字段的数据********************* */
		for (int i = 0; i < UsedSets.size(); i++) {
			count++;
			strSet1 = "";
			strSet2 = "";
			// 初始化fldList
			strFldList.setLength(0);
			Iterator it = mapUsedFieldItems.keySet().iterator();
			while (it.hasNext()) {
				fieldTemp = (FieldItem) mapUsedFieldItems.get(it.next());
				// 全局变量付值
				Field = fieldTemp;
				if (fieldTemp.getFieldsetid().equals(UsedSets.get(i))
						&& fieldTemp.getVarible() == 0) {
					if (ModeFlag == forSearch) {
						if (!fieldTemp.isOrg() && !fieldTemp.isPos())
							strCurSet = DbPre + UsedSets.get(i);
						else
							strCurSet = (String) UsedSets.get(i);
					} else if (ModeFlag != forPerson) {
						strCurSet = (String) UsedSets.get(i);
					}

					strCurMenu = strCurSet + '.' + Field.getItemid();
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSet1 = strSet1 + TempTableName + '.'
								+ Field.getItemid() + ',';
						strSet2 = strSet2 + strCurSet + '.' + Field.getItemid()
								+ ',';
					} else {
						if (strFldList.length() == 0) {
							strFldList.append(TempTableName).append('.')
									.append(Field.getItemid()).append('=')
									.append(strCurMenu);
						} else {
							strFldList.append(',').append(TempTableName)
									.append('.').append(Field.getItemid())
									.append('=').append(strCurMenu);
						}
					}
				}
			}

			strJoin = '.' + getKeyField((String) UsedSets.get(i));
			strKey = getKeyField((String) UsedSets.get(i));
			// 如果是Oracle或db2去逗号
			if ((DBType == Constant.ORACEL) || (DBType == 3)) {
				strSet1 = strSet1.substring(0, strSet1.length() - 1);
				strSet2 = strSet2.substring(0, strSet2.length() - 1);
			}

			FieldSet vo = DataDictionary
					.getFieldSetVo((String) UsedSets.get(i));
			// System.out.println(ymc +"-------集" + UsedSets.get(i));
			if (ymc != null
					&& ("1".equals(vo.getChangeflag().trim()) || "2".equals(vo
							.getChangeflag().trim()))) {
				String z0 = strCurSet + "." + UsedSets.get(i) + "Z0";
				String z1 = strCurSet + "." + UsedSets.get(i) + "Z1";
				String monthOfz0 = Sql_switcher.month(z0);
				String yearOfz0 = Sql_switcher.year(z0);
				strSQL.setLength(0);
				if ((DBType == Constant.ORACEL) || (DBType == 3)) {

					strSQL.append("UPDATE ");
					strSQL.append(TempTableName).append(" set (").append(
							strSet1).append(")=(SELECT ").append(strSet2);
					strSQL.append(" from ");
					strSQL.append(strCurSet);

					strSQL.append(" where ").append(TempTableName).append(
							strJoin);
					strSQL.append("=").append(strCurSet).append(strJoin);
					strSQL.append("  and ").append(monthOfz0).append("=")
							.append(ymc.getMonth());
					strSQL.append(" and ").append(yearOfz0).append("=");
					strSQL.append(ymc.getYear()).append(" and ").append(z1)
							.append("=");
					strSQL.append(ymc.getCount()
							+ " ) where exists ( select null  from ");
					strSQL.append(strCurSet);

					strSQL.append(" where ").append(TempTableName).append(
							strJoin);
					strSQL.append("=").append(strCurSet).append(strJoin);
					strSQL.append("  and ").append(monthOfz0).append("=")
							.append(ymc.getMonth());
					strSQL.append(" and ").append(yearOfz0).append("=");
					strSQL.append(ymc.getYear()).append(" and ").append(z1)
							.append("=");
					strSQL.append(ymc.getCount() + " ) ");
				} else {
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName).append(" set ");
					strSQL.append(strFldList).append(" from ").append(
							TempTableName);
					strSQL.append(" LEFT JOIN ").append(strCurSet);
					strSQL.append(" on ").append(TempTableName).append(strJoin);
					strSQL.append("=").append(strCurSet).append(strJoin);
					strSQL.append(" where ").append(monthOfz0).append("=")
							.append(ymc.getMonth());
					strSQL.append(" and ").append(yearOfz0).append("=");
					strSQL.append(ymc.getYear()).append(" and ").append(z1)
							.append("=");
					strSQL.append(ymc.getCount() + " ");
				}
				SQLS.add(strSQL.toString());
			} else {
				if (!IsMainSet((String) UsedSets.get(i))) {
	
	/*
					String _tempTable = "T_" + TempTableName;
					if (DBType == Constant.MSSQL && this.isTempTable)
						_tempTable = "##T_" + TempTableName;

					
					if (DBType == Constant.MSSQL) {
						if (this.isTempTable)
							SQLS.add("drop table ##T_" + TempTableName);
						else
							SQLS.add("drop table T_" + TempTableName);
					} else {
						if (this.isTempTable)
							SQLS.add("truncate table T_" + TempTableName);
						SQLS.add("drop table T_" + TempTableName);
					}
	*/				
    /*
					if (DBType == 3) {
						strSQL.append("CREATE GLOBAL TEMPORARY TABLE T_")
								.append(TempTableName).append(" AS (SELECT ")
								.append(strKey);
						strSQL.append(",MAX(I9999) AS MAX_I9999 ").append(
								" FROM ").append(strCurSet)
								.append(" GROUP BY ");
						strSQL.append(strKey).append(") DEFINITION ONLY");
						SQLS.add(strSQL.toString());
						strSQL.setLength(0);
						strSQL.append("INSERT INTO T_").append(TempTableName)
								.append(" SELECT ").append(strKey).append(
										",MAX(I9999) AS MAX_I9999 ");
						strSQL.append(" FROM ").append(strCurSet);
						strSQL.append(" GROUP BY ").append(strKey);
						SQLS.add(strSQL.toString());
						
						SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKey+",MAX_I9999)");
						 
						
						strSQL.setLength(0);
					} else if (DBType == 2) {
						strSQL.setLength(0);
						strSQL.append("CREATE ");
						if (this.isTempTable)
							strSQL.append(" GLOBAL TEMPORARY ");
						strSQL.append(" TABLE  T_").append(TempTableName);
						if (this.isTempTable)
							strSQL.append("  On Commit Preserve Rows ");
						strSQL.append(" AS SELECT ").append(strKey);
						strSQL.append(",MAX(I9999) AS MAX_I9999 ").append(
								" FROM ").append(strCurSet);
						strSQL.append(" GROUP BY ");
						strSQL.append(strKey);
						SQLS.add(strSQL.toString());
						
						SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKey+",MAX_I9999)");
						 
						
						strSQL.setLength(0);
					} else {
						strSQL.setLength(0);
						strSQL.append("SELECT ");
						strSQL.append(strKey);
						if (this.isTempTable)
							strSQL.append(",MAX(I9999) AS MAX_I9999 INTO ##T_");
						else
							strSQL.append(",MAX(I9999) AS MAX_I9999 INTO T_");

						strSQL.append(TempTableName);
						strSQL.append(" FROM ");
						strSQL.append(strCurSet);
						strSQL.append(" GROUP BY ");
						strSQL.append(strKey);
						SQLS.add(strSQL.toString());
						
						if (this.isTempTable)
							SQLS.add("create index T_"+TempTableName+"_index  on ##T_"+TempTableName+" ("+strKey+",MAX_I9999)");
						else
							SQLS.add("create index T_"+TempTableName+"_index  on T_"+TempTableName+" ("+strKey+",MAX_I9999)");
					 
						strSQL.setLength(0);
					}
     
					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == 2) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(TempTableName);
						strSQLSet.append(".I9999=(SELECT ").append(_tempTable)
								.append(".MAX_I9999");

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ");
						strSQLFrom.append(_tempTable);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						strSQLOn.append(" WHERE ").append(TempTableName)
								.append(strJoin).append("=").append(_tempTable)
								.append(strJoin).append(")");
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(TempTableName).append(
								".I9999=").append(_tempTable).append(
								".MAX_I9999");

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" LEFT JOIN ").append(_tempTable);

						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(_tempTable).append(
								strJoin);
					}

					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
							
		*/						
					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET (").append(strSet1).append(
								")=(SELECT ").append(strSet2);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ");
						strSQLFrom.append(strCurSet);

						strSQLFrom.append(",(SELECT "+strKey+",MAX(I9999) AS MAX_I9999  FROM "+strCurSet+" GROUP BY "+strKey+") ab ");
						
						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						
						strSQLOn.append(" WHERE  ");
						strSQLOn.append(TempTableName)
								.append(strJoin).append("=");
						strSQLOn.append(strCurSet).append(strJoin).append(" AND ");
						strSQLOn.append(" "+strCurSet+"."+strKey+"=ab."+strKey+" and "+strCurSet+".i9999=ab.MAX_I9999 ) "); // and
						/*
						strSQLOn.append(" WHERE ");
						strSQLOn.append(TempTableName)
								.append(strJoin).append("=");
						strSQLOn.append(strCurSet).append(strJoin).append(" AND "); */
					//	strSQLOn.append(TempTableName).append(".I9999=");
					//	strSQLOn.append(strCurSet).append(".I9999)");
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(strFldList);
						// System.out.println(strFldList);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" LEFT JOIN ");
				//		strSQLLeft.append(strCurSet);

						if (!IsMainSet(strCurSet))
						{
							strSQLLeft.append(" ( SELECT "+strCurSet+".*  FROM "+strCurSet+", ");
							strSQLLeft.append("(SELECT "+strKey+",MAX(I9999) I9999  FROM "+strCurSet+" GROUP BY "+strKey+" ) AB ");
							strSQLLeft.append(" WHERE "+strCurSet+"."+strKey+"=AB."+strKey+" AND "+strCurSet+".I9999=AB.I9999 )   "+strCurSet);
						}
						else
							strSQLLeft.append(strCurSet);
						
						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(strCurSet).append(
								strJoin);
					/*	strSQLOn.append(" AND ").append(TempTableName).append(
								".I9999=").append(strCurSet).append(".I9999");*/
					}
					/*
					 * System.out.println(strSQL.append(strSQLSet).append(strSQLFrom)
					 * .append(strSQLLeft).append(strSQLOn).toString());
					 */
					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
					strSQL.setLength(0);
				} else {
					strSQL.setLength(0);
					strSQL.append("UPDATE ");
					strSQL.append(TempTableName);
					if ((DBType == Constant.ORACEL) || (DBType == 3)) {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET (").append(strSet1).append(
								")=(SELECT ").append(strSet2);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(strCurSet);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" ");

						strSQLOn.setLength(0);
						strSQLOn.append(" WHERE ").append(TempTableName)
								.append(strJoin);
						strSQLOn.append("=").append(strCurSet).append(strJoin);
						if (!IsMainSet((String) UsedSets.get(i))) {
							strSQLOn.append(" AND ").append(TempTableName)
									.append(".I9999=").append(strCurSet)
									.append(".I9999").append(")");
						} else {
							strSQLOn.append(")");
						}
					} else {
						strSQLSet.setLength(0);
						strSQLSet.append(" SET ").append(strFldList);

						strSQLFrom.setLength(0);
						strSQLFrom.append(" FROM ").append(TempTableName);

						strSQLLeft.setLength(0);
						strSQLLeft.append(" LEFT JOIN ").append(strCurSet);

						strSQLOn.setLength(0);
						strSQLOn.append(" ON ").append(TempTableName).append(
								strJoin).append("=").append(strCurSet).append(
								strJoin);
						if (!IsMainSet((String) UsedSets.get(i))) {
							strSQLOn.append(" AND ").append(TempTableName)
									.append(".I9999=").append(strCurSet)
									.append(".I9999");
						}
					}
					SQLS.add(strSQL.append(strSQLSet).append(strSQLFrom)
							.append(strSQLLeft).append(strSQLOn).toString());
					strSQL.setLength(0);
				}
			}
		}
		
		DbWizard dbw=new DbWizard(this.con);
		for (int i = 0; i < SQLS.size(); i++) {
			try {
				String sql = (String) SQLS.get(i);
				if(brun(sql,dbw))
				{
			//		System.out.println("--------->" + i + " " + (String)SQLS.get(i));
					//dm 数据库使用dao.update 执行select 会报非法sql
					if(sql!=null && sql.toLowerCase().startsWith("select ")){
						dao.search(sql);
					}else{
						dao.update(sql);
					}
				}
			} catch (SQLException e) {
				// e.printStackTrace();
			}
		}
	}

	public String getTempTableName() {
		return TempTableName;
	}

	/** ********添加一个补充过滤条件********** */
	private String renew_term;

	public String getRenew_term() {
		return renew_term;
	}

	public void setRenew_term(String renew_term) {
		this.renew_term = renew_term;
	}

	public void setVarName(String varName) {
		VarName = varName;
	}

	public String getStdTmpTable() {
		return StdTmpTable;
	}

	public void setStdTmpTable(String stdTmpTable) {
		StdTmpTable = stdTmpTable;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public void setTargetFieldDecimal(int targetFieldDecimal) {
		this.targetFieldDecimal = targetFieldDecimal;
	}

	public boolean isYearMonthCount() {
		return isYearMonthCount;
	}

	public void setYearMonthCount(boolean isYearMonthCount) {
		this.isYearMonthCount = isYearMonthCount;
	}

	public YearMonthCount getYmc() {
		return ymc;
	}

	public void setYmc(YearMonthCount ymc) {
		this.ymc = ymc;
	}

	public boolean isSingleTalbe() {
		return isSingleTalbe;
	}

	public void setSingleTalbe(boolean isSingleTalbe) {
		this.isSingleTalbe = isSingleTalbe;
	}

	public boolean isVerify() {
		return isVerify;
	}

	public void setVerify(boolean isVerify) {
		this.isVerify = isVerify;
	}

	public boolean isSupportVar() {
		return isSupportVar;
	}

	public void setSupportVar(boolean isSupportVar) {
		this.isSupportVar = isSupportVar;
	}
	
	public void setSupportVar(boolean isSupportVar,String func_module_sql) {
		this.isSupportVar = isSupportVar;
		this.func_module_sql=func_module_sql;
	}

	public boolean isStatMultipleVar() {
		return isStatMultipleVar;
	}

	public void setStatMultipleVar(boolean isStatMultipleVar) {
		this.isStatMultipleVar = isStatMultipleVar;
	}

	public ArrayList getStatVarList() {
		return statVarList;
	}

	public void setStatVarList(ArrayList statVarList) {
		this.statVarList = statVarList;
	}

	public HashMap getGz_stdFieldMap() {
		return gz_stdFieldMap;
	}

	public void setGz_stdFieldMap(HashMap gz_stdFieldMap) {
		this.gz_stdFieldMap = gz_stdFieldMap;
	}

	public ArrayList getVarList() {
		return varList;
	}

	public void setVarList(ArrayList varList) {
		this.varList = varList;
	}
    public HashMap getUsedFieldMap()
    {
    	return this.usedFieldMap;
    }

	public String getExistWhereText() {
		return existWhereText;
	}

	public void setExistWhereText(String existWhereText) {
		this.existWhereText = existWhereText;
	}

	public HashMap getBracketsFieldValueMap() {
		return bracketsFieldValueMap;
	}

	public void setBracketsFieldValueMap(HashMap bracketsFieldValueMap) {
		this.bracketsFieldValueMap = bracketsFieldValueMap;
	}

	public HashMap getBracketsFieldMap() {
		return bracketsFieldMap;
	}

	public void setBracketsFieldMap(HashMap bracketsFieldMap) {
		this.bracketsFieldMap = bracketsFieldMap;
	}

	public String getStdTmpTable_where() {
		return StdTmpTable_where;
	}

	public void setStdTmpTable_where(String stdTmpTable_where) {
		StdTmpTable_where = stdTmpTable_where;
	}

	/** 解析结果中，指标前是否加子集名 */
	public boolean getAddTableName(){
		return addTableName;
	}
	
	/**
	 * 解析结果中，指标前是否加子集名
	 * @param addTableName
	 */ 
	public void setAddTableName(boolean addTableName){
		this.addTableName = addTableName;
	}
	
	public void setZxfj_propertyMap(HashMap<String, String> zxfj_propertyMap) {
		this.zxfj_propertyMap = zxfj_propertyMap;
	}
	
	public void setDataBaseType(String dataBaseType) {
		this.dataBaseType = dataBaseType;
	}
	
	public ArrayList getSubSetConditionFieldItemList() {
		return subSetConditionFieldItemList;
	}
}
