package com.hjsj.hrms.businessobject.sys.options;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.interfaces.xmlparameter.XmlParameter;
import com.hjsj.hrms.transaction.sys.warn.ColumnBean;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

public class SelfSalaryInfo {

	private Connection conn; // 数据库链接

	private String a0100; // 人员编号
	private String usrPre; // 人员库前缀
	private ArrayList showFieldItemList = new ArrayList();// 显示指标项
	private ArrayList sumFieldItemList = new ArrayList();// 行合计项
	private ArrayList fieldSetList = new ArrayList();// 所选子集
	private String fieldsetid;
	private String query_field;// 过滤项
	private String changeflag;
	private String operateFlag; // 操作类型(月/季度/年/时间段)1,2,3,4
	private String year; // 年份
	private String month; // 月份
	private String quarter; // 季度(1,2,3,4)
    private String prv_flag;//权限指标 区分自助和业务
	private String startTime; // 起始时间
	private String endTime; // 终止时间

	private int startYear; // 起始年
	private int startMonth;// 起始月
	private int startDate; // 起始日
	private int endYear; // 终止年
	private int endMonth; // 终止月
	private int endDate; // 终止日
	private UserView userView;
	private String year_restrict = "";
	private boolean flag = false; // 标识薪酬明细中是否只选择了主集指标
	private String A01;
	private String title;
	private ArrayList yearlist = new ArrayList();

	public ArrayList getYearlist() {
		return yearlist;
	}

	public void setYearlist(ArrayList yearlist) {
		this.yearlist = yearlist;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * 
	 * @param conn
	 * @param a0100
	 * @param usrPre
	 * @param operateFlag
	 * @param year
	 * @param month
	 * @param quarter
	 * @param startTime
	 * @param endTime
	 * @param userView
	 * @param fieldsetid子集名称
	 * @param title薪酬名
	 */
	public SelfSalaryInfo(Connection conn, String a0100, String usrPre,
			String operateFlag, String year, String month, String quarter,
			String startTime, String endTime, UserView userView,
			String fieldsetid, String title) throws GeneralException {
		this.conn = conn;
		this.a0100 = a0100;
		this.usrPre = usrPre;
		this.operateFlag = operateFlag;
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userView = userView;
		this.fieldsetid = fieldsetid;
		this.title = title;
		this.init(conn);
	}
	/**
	 * 
	 * @param conn
	 * @param a0100
	 * @param usrPre
	 * @param operateFlag
	 * @param year
	 * @param month
	 * @param quarter
	 * @param startTime
	 * @param endTime
	 * @param userView
	 * @param fieldsetid子集名称
	 * @param title薪酬名
	 * @param prv_flag权限指标 区分自助和业务
	 */
	public SelfSalaryInfo(Connection conn, String a0100, String usrPre,
			String operateFlag, String year, String month, String quarter,
			String startTime, String endTime, UserView userView,
			String fieldsetid, String title,String prv_flag) throws GeneralException {
		this.conn = conn;
		this.a0100 = a0100;
		this.usrPre = usrPre;
		this.operateFlag = operateFlag;
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userView = userView;
		this.fieldsetid = fieldsetid;
		this.title = title;
		this.prv_flag=prv_flag;
		this.init(conn);
	}
	/**
	 * 薪酬明细构造器
	 * 
	 * @param conn
	 *            数据库链接
	 * @param a0100
	 *            人员编号
	 * @param usrPre
	 *            人员库前缀
	 * @param operateFlag
	 *            操作标识
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param quarter
	 *            季度
	 * @param startDate
	 *            起始时间
	 * @param endDate
	 *            终止时间
	 * @param UserView
	 *            人员权限
	 */
	public SelfSalaryInfo(Connection conn, String a0100, String usrPre,
			String operateFlag, String year, String month, String quarter,
			String startTime, String endTime, UserView userView)
			throws GeneralException {
		this.conn = conn;
		this.a0100 = a0100;
		this.usrPre = usrPre;
		this.operateFlag = operateFlag;
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userView = userView;
		this.init(conn);
	}
	/**
	 * 薪酬明细构造器
	 * 
	 * @param conn
	 *            数据库链接
	 * @param a0100
	 *            人员编号
	 * @param usrPre
	 *            人员库前缀
	 * @param operateFlag
	 *            操作标识
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param quarter
	 *            季度
	 * @param startDate
	 *            起始时间
	 * @param endDate
	 *            终止时间
	 * @param UserView
	 *            人员权限
	 * @param prv_falg  区分自助、业务      
	 */
	public SelfSalaryInfo(Connection conn, String a0100, String usrPre,
			String operateFlag, String year, String month, String quarter,
			String startTime, String endTime, UserView userView,String prv_falg)
			throws GeneralException {
		this.conn = conn;
		this.a0100 = a0100;
		this.usrPre = usrPre;
		this.operateFlag = operateFlag;
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.userView = userView;
		this.prv_flag=prv_falg;
		this.init(conn);
	}
	/**
	 * 薪酬明细构造器
	 * 
	 * @param conn
	 *            数据库链接
	 * @param a0100
	 *            人员编号
	 * @param usrPre
	 *            人员库前缀
	 * @param operateFlag
	 *            操作标识
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @param quarter
	 *            季度
	 * @param startDate
	 *            起始时间
	 * @param endDate
	 *            终止时间
	 */
	public SelfSalaryInfo(Connection conn, String a0100, String usrPre,
			String operateFlag, String year, String month, String quarter,
			String startTime, String endTime) throws GeneralException {
		this.conn = conn;
		this.a0100 = a0100;
		this.usrPre = usrPre;
		this.operateFlag = operateFlag;
		this.year = year;
		this.month = month;
		this.quarter = quarter;
		this.startTime = startTime;
		this.endTime = endTime;
		this.init(conn);
	}

	/**
	 * 初始化薪酬明细设置(显示列与合计列)
	 * 
	 * @param conn
	 */
	public void init(Connection conn) throws GeneralException {
		Sys_Oth_Parameter sop = new Sys_Oth_Parameter(conn);
		// String mysalarys = sop.getValue(Sys_Oth_Parameter.MYSALARYS);

		String mysalarys = sop.getValueS(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", "title");		
		String sumItemValue = "";// 合计项
		if (mysalarys == null||"".equals(mysalarys)) {
			mysalarys = "";
			sumItemValue = "";
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"没有定义指标项！", "", ""));
		}
		String fieldsetid = "";
		String title = "";
		String[] selected_mysalarys = mysalarys.split(",");
		
		
		// System.out.println();
		// 指标子集
		ArrayList fieldsetList = new ArrayList();
		ArrayList fieldSetTempList =null;
		if("infoself".equalsIgnoreCase(this.prv_flag))
		{
			fieldSetTempList = this.userView.getPrivFieldSetList(
					Constant.EMPLOY_FIELD_SET, 0);
			
		}else
		{
			fieldSetTempList = this.userView.getPrivFieldSetList(
					Constant.EMPLOY_FIELD_SET);
		}
		

		/*
		 * for(int i=0; i< fieldSetTempList.size(); i++){ FieldSet fieldSet =
		 * (FieldSet) fieldSetTempList.get(i); String setid =
		 * fieldSet.getFieldsetid(); String setdesc =
		 * fieldSet.getFieldsetdesc(); String setChangeFlag =
		 * fieldSet.getChangeflag(); if(setChangeFlag == null){ }else{
		 * if(mysalarys.toLowerCase().indexOf(setid.toLowerCase())!=-1) {
		 * CommonData dataobj = new CommonData(setid, setdesc);
		 * fieldsetList.add(dataobj);
		 * if(fieldsetid.toLowerCase().equalsIgnoreCase(setid.toLowerCase())) {
		 * this.changeflag=setChangeFlag;
		 * 
		 * //this.operateFlag =setChangeFlag; } } } }
		 */
		if (selected_mysalarys != null && selected_mysalarys.length > 0) {
			for (int i = 0; i < selected_mysalarys.length; i++) {
				String one_salary = selected_mysalarys[i];
				String[] one_Array = one_salary.split("`");
				String setname = one_Array[0];
				String settitle = "";
				if (one_Array.length > 1) {
                    settitle = one_Array[1];
                }
				if (setname == null || setname.length() <= 0) {
                    continue;
                }
				for (int r = 0; r < fieldSetTempList.size(); r++) {
					FieldSet fieldSet = (FieldSet) fieldSetTempList.get(r);
					String setid = fieldSet.getFieldsetid();
					String setdesc = fieldSet.getFieldsetdesc();
					String setChangeFlag = fieldSet.getChangeflag();
					if (setid.indexOf(setname) != -1) {
						if (settitle != null && settitle.length() > 0) {
                            setdesc = settitle;
                        }
						CommonData dataobj = new CommonData(one_salary, setdesc);
						fieldsetList.add(dataobj);
						if (fieldsetid.toLowerCase().equalsIgnoreCase(
								setid.toLowerCase())) {
							this.changeflag = setChangeFlag;//循环 年月变化标识初始化时会有问题
						}
						/* ================ 郑文龙修改bug ================ */
						// 判断 this.fieldsetid 字段是否已经赋值		
						if (this.fieldsetid == null || this.fieldsetid.length() <= 0) {
							// 没有赋值  为字段赋默认值
							fieldsetid = one_Array[0];
							if (one_Array.length > 1) {
                                title = one_Array[1];
                            }
							this.title = title;
							this.fieldsetid = fieldsetid;
						} else {
							// 把this.fieldsetid字段的值给 fieldsetid
							fieldsetid = this.fieldsetid;
						}
						/* ================ 郑文龙修改bug ================ */	
						break;
					}

				}

			}
		}			
		
		String salary_text = sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", fieldsetid, "title", title, "");
		sumItemValue = sop.getChildText(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", fieldsetid, "title", title);
		if (sumItemValue == null) {
			sumItemValue = "";
		}
		if(fieldsetid!=null&&!"".equals(fieldsetid)) {
            this.changeflag=DataDictionary.getFieldSetVo(fieldsetid).getChangeflag(); //重新取出子集 年月标识
        }
		if (this.changeflag == null || this.changeflag.length() <= 0) {
            this.changeflag = "0";
        }
		String[] temp_showFieldItems = null;
		String[] temp_sumFieldItems = null;
		if (fieldsetList != null && fieldsetList.size() > 0) {
			temp_showFieldItems = salary_text.split(",");
			temp_sumFieldItems = sumItemValue.split(",");
		}

		this.fieldSetList = fieldsetList;
		// 过滤项
		this.query_field = sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", fieldsetid, "query_field", "title", title);
		// System.out.println("指标列表");
		if (temp_showFieldItems != null && temp_showFieldItems.length > 0) {
			for (int i = 0; i < temp_showFieldItems.length; i++) {
				String itemid = temp_showFieldItems[i].trim();
				if ("".equals(itemid)) {
				} else {
					if (this.showFieldItemList.contains(itemid)) {// 去除重复指标
					} else if ("B0110".equalsIgnoreCase(itemid)
							|| "E0122".equalsIgnoreCase(itemid)
							|| "A0101".equalsIgnoreCase(itemid)
							|| "E01A1".equalsIgnoreCase(itemid)) {
					} else {
						if(this.checkItemContext(fieldsetid,itemid.trim()))//校验对应指标列是否有值 需要changeflag changxy
                        {
                            this.showFieldItemList.add(itemid.trim());
                        }
					}
				}
			}
		}
		if (temp_sumFieldItems != null && temp_sumFieldItems.length > 0) {
			for (int i = 0; i < temp_sumFieldItems.length; i++) {
				String itemid = temp_sumFieldItems[i].trim();
				if ("".equals(itemid)) {
				} else {
					if (this.sumFieldItemList.contains(itemid)) {
					} else {
						this.sumFieldItemList.add(itemid.trim());
					}
				}

			}
		}
		ContentDAO dao = new ContentDAO(conn);
		CardConstantSet cardConstantSet = new CardConstantSet(this.userView,
				conn);
		String relating = cardConstantSet.getSearchRelating(dao);
		String b0110 = cardConstantSet.getRelatingValue(dao, this.userView
				.getA0100(), this.userView.getDbname(), relating, this.userView
				.getUserOrgId());
		XmlParameter xml = new XmlParameter("UN", b0110, "00");
		xml.ReadOutParameterXml("SS_SETCARD", conn, "all");
		this.year_restrict = xml.getYear_restrict();
		this.yearlist = getHaveYearList(fieldsetid, this.query_field);

	}

	/**
	 * 薪酬明细执行控制
	 * 
	 * @return
	 */
	public ArrayList execute() {
		ArrayList list = new ArrayList();

		ArrayList sqlList = this.getExecuteSqlList();
		list = this.executeSQLList(sqlList);

		/*
		 * System.out.println(); System.out.println(); System.out.println();
		 * for(int i=0; i<sqlList.size(); i++){ String sql =
		 * (String)sqlList.get(i); System.out.println(sql); }
		 * System.out.println(); System.out.println(); System.out.println();
		 */

		if (list.size() == 0) {
		} else {
			if (sumFieldItemList.size() == 0) {
			} else {
				list = this.executeSum(list);
			}
		}
		return list;
	}

	/**
	 * 薪酬明细列显示列表
	 * 
	 * @return
	 */
	public ArrayList showColumnList() throws GeneralException {
		ArrayList list = new ArrayList();

		/*
		 * ColumnBean columnBeanorg = new ColumnBean();
		 * columnBeanorg.setColumnName("单位"); list.add(columnBeanorg);
		 * 
		 * ColumnBean columnBeandept = new ColumnBean();
		 * columnBeandept.setColumnName("部门"); list.add(columnBeandept);
		 * 
		 * ColumnBean columnBeanzw = new ColumnBean();
		 * columnBeanzw.setColumnName("职位名称"); list.add(columnBeanzw);
		 */

		ColumnBean columnBeanname = new ColumnBean();
		columnBeanname.setColumnName("姓名");
		list.add(columnBeanname);

		/*
		 * if(this.flag){ }else{ ColumnBean columnBeanyearmonth = new
		 * ColumnBean(); columnBeanyearmonth.setColumnName("年月标识");
		 * list.add(columnBeanyearmonth);
		 * 
		 * ColumnBean columnBeanc = new ColumnBean();
		 * columnBeanc.setColumnName("次数"); list.add(columnBeanc); }
		 */
		for (int i = 0; i < this.showFieldItemList.size(); i++) {
			String itemid = (String) this.showFieldItemList.get(i);
			if(this.checkItemContext("",itemid)){
				String itemDesc = this.getItemDest(itemid);
				String itemtype = getItemType(itemid);
				ColumnBean columnBean = new ColumnBean();
				columnBean.setColumnName(itemDesc);
				columnBean.setColumnType(itemtype);
				list.add(columnBean);
			}
		}
		return list;
	}
	
	/***
	 * 查询对应列内容是否为空 为空的列不显示 
	 * changxy 20170629
	 * */
	public boolean checkItemContext(String fieldsetid,String itemid) throws GeneralException{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet rs=null;
		FieldItem item = (StringUtils.isNotEmpty(fieldsetid))?DataDictionary.getFieldItem(itemid.trim(),fieldsetid):DataDictionary.getFieldItem(itemid.trim());
		if(item==null) {
            throw GeneralExceptionHandler.Handle(new Exception("指标"+itemid.trim()+"未构库，请检查设置！"));
        }
		String setid = item.getFieldsetid();//this.getFieldSetId(itemid.trim()); // 指标集
		String sqlStr="";
		
		StringBuffer sql=new StringBuffer();
		if(!"0".equals(this.changeflag)){//年月变化标识  一般变化子集没有年月标识
			if(year!=null&&year.length()>0){
				
				sql.append(" and " + Sql_switcher.year(this.getZ0(setid))
						+ " ");
				sql.append("= '");
				sql.append(year);
				sql.append("'");
			}
			if(month!=null&&month.length()>0){
				sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
				sql.append("= '");
				sql.append(month + "'");
			}else if(quarter!=null&&quarter.length()>0){
				
				if("1".equals(quarter)){
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append(">=1");
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append("<=3");
				}else if("2".equals(quarter)){
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append(">=4");
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append("<=6");
				}else if("3".equals(quarter)){
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append(">=7");
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append("<=9");
				}else{
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append(">=10");
					sql.append(" and " + Sql_switcher.month(this.getZ0(setid)));
					sql.append("<=12");
				}
			}
			if ("4".equals(this.operateFlag) && !"0".equals(this.changeflag)) {// 时间段
				if(this.startTime.length()>0&&this.endTime.length()>0){
					sql.append(" and "+Sql_switcher.dateToChar(this.getZ0(setid))+">='"+this.startTime+"' and "+Sql_switcher.dateToChar(this.getZ0(setid))+" <= '"+this.endTime+"'");
				}
			
			}
		}
		if("N".equalsIgnoreCase(item.getItemtype())){
			sqlStr="select max("+itemid+") itemid from  "+this.usrPre+setid+" where "+itemid+" is not null and "+itemid+" <>0 and a0100='"+this.a0100+"'" ;
			if(!("".equals(setid) || "A01".equalsIgnoreCase(setid))) {
                sqlStr+=sql.toString();
            }

		}else if("D".equalsIgnoreCase(item.getItemtype())||"A".equalsIgnoreCase(item.getItemtype())){
			
			sqlStr="select max("+itemid+") itemid from  "+this.usrPre+setid+" where "+itemid+" is not null and a0100='"+this.a0100+"'" ;
			if(!("".equals(setid) || "A01".equalsIgnoreCase(setid))) {
                sqlStr+=sql.toString();
            }

		}else if("M".equalsIgnoreCase(item.getItemtype())){
			sqlStr="select max("+Sql_switcher.sqlToChar(itemid)+") itemid from  "+this.usrPre+setid+" where "+itemid+" is not null and a0100='"+this.a0100+"'" ;
			if(!("".equals(setid) || "A01".equalsIgnoreCase(setid))) {
                sqlStr+=sql.toString();
            }
		}
		try {
			rs=dao.search(sqlStr);
			while(rs.next()){
				if("N".equalsIgnoreCase(item.getItemtype())){
					double itemValue=rs.getDouble("itemid");
					if(itemValue!=0)//数值型指标改为不为0 不隐藏
                    {
                        return true;
                    }
				}else if(("D".equalsIgnoreCase(item.getItemtype())||"A".equalsIgnoreCase(item.getItemtype()))){//日期型不会为空不校验
					String itemValue="";
					if("D".equalsIgnoreCase(item.getItemtype())){
						Date date=rs.getDate("itemid");
						if(date!=null) {
                            itemValue=date.toString();
                        }
					}else{
						itemValue=rs.getString("itemid");
					}
					if(itemValue!=null&&itemValue.trim().length()>0) {
                        return true;
                    }
				}else if("M".equalsIgnoreCase(item.getItemtype())){
					String itemValue=rs.getString("itemid");
					if(itemValue!=null&&itemValue.trim().length()>0) {
                        return true;
                    }
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	

	/**
	 * 薪酬明细列显示列表
	 * 
	 * @return
	 */
	public ArrayList columnList() throws GeneralException {
		ArrayList list = new ArrayList();

		/*
		 * ColumnBean org = new ColumnBean(); org.setColumnName("B0110");
		 * org.setColumnType("L"); list.add(org);
		 * 
		 * ColumnBean dept = new ColumnBean(); dept.setColumnName("E0122");
		 * dept.setColumnType("L"); list.add(dept);
		 * 
		 * ColumnBean zw = new ColumnBean(); zw.setColumnName("E01A1");
		 * zw.setColumnType("L"); list.add(zw);
		 */

		ColumnBean name = new ColumnBean();
		name.setColumnName("A0101");
		name.setColumnType("L");
		list.add(name);

		/*
		 * if(this.flag){}else{ ColumnBean z0 = new ColumnBean();
		 * z0.setColumnName("Z0"); z0.setColumnType("R"); list.add(z0);
		 * 
		 * ColumnBean z1 = new ColumnBean(); z1.setColumnName("Z1");
		 * z1.setColumnType("R"); list.add(z1); }
		 */
		for (int i = 0; i < this.showFieldItemList.size(); i++) {
			String itemid = (String) this.showFieldItemList.get(i);

			if(this.checkItemContext("",itemid)){
				FieldItem item = DataDictionary.getFieldItem(itemid);
				ColumnBean id = new ColumnBean();
				id.setColumnName(itemid);
				String itemtype = this.getItemType(itemid.trim());
				if ("A".equalsIgnoreCase(itemtype)) {
					id.setColumnType("A");
				} else if ("M".equalsIgnoreCase(itemtype)) {
					id.setColumnType("M");
				} else {
					id.setColumnType("R");
				}
				list.add(id);
			}

		}
		return list;
	}

	/**
	 * 获得指标类型
	 * 
	 * @param itemid
	 * @return
	 */
	public String getItemType(String itemid) {
		FieldItem item = DataDictionary.getFieldItem(itemid);
		if (item == null) {
            return "";
        }
		
		return item.getItemtype();
	}

	/**
	 * 指标描述
	 * 
	 * @param itemid
	 * @return
	 */
	public String getItemDest(String itemid) {
		FieldItem item = DataDictionary.getFieldItem(itemid);
		if (item == null) {
            return "";
        }
		
		return item.getItemdesc();
	}

	/**
	 * 获得可执行的SQL 语句
	 * 
	 * @return
	 */
	public ArrayList getExecuteSqlList() {
		ArrayList sqlList = new ArrayList();
		if ("3".equalsIgnoreCase(this.operateFlag)
				&& !"0".equals(this.changeflag)) {// 月
			String sql = this.createBaseSql(this.year, this.month);
			sqlList.add(sql);
		} else if ("2".equalsIgnoreCase(this.operateFlag)
				&& !"0".equals(this.changeflag)) {// 季度
			if ("1".equals(this.quarter)) {
				for (int i = 1; i <= 3; i++) {
					String sql = this.createBaseSql(this.year, String
							.valueOf(i));
					sqlList.add(sql);
				}
			} else if ("2".equals(this.quarter)) {
				for (int i = 4; i <= 6; i++) {
					String sql = this.createBaseSql(this.year, String
							.valueOf(i));
					sqlList.add(sql);
				}
			} else if ("3".equals(this.quarter)) {
				for (int i = 7; i <= 9; i++) {
					String sql = this.createBaseSql(this.year, String
							.valueOf(i));
					sqlList.add(sql);
				}
			} else if ("4".equals(this.quarter)) {
				for (int i = 10; i <= 12; i++) {
					String sql = this.createBaseSql(this.year, String
							.valueOf(i));
					sqlList.add(sql);
				}
			}
		} else if ("1".equalsIgnoreCase(this.operateFlag)
				&& !"0".equals(this.changeflag)) {// 年
			for (int i = 1; i <= 12; i++) {
				String sql = this.createBaseSql(this.year, String.valueOf(i));
				sqlList.add(sql);
			}
		} else if ("4".equalsIgnoreCase(this.operateFlag)
				&& !"0".equals(this.changeflag)) {// 时间段
			this.startYear = Integer.parseInt(this.startTime.substring(0, 4));
			this.startMonth = Integer.parseInt(this.startTime.substring(5, 7));
			this.startDate = Integer.parseInt(this.startTime.substring(8, 10));

			this.endYear = Integer.parseInt(this.endTime.substring(0, 4));
			this.endMonth = Integer.parseInt(this.endTime.substring(5, 7));
			this.endDate = Integer.parseInt(this.endTime.substring(8, 10));

			if (this.startYear == this.endYear) {// 同一年的

				if (this.startMonth == this.endMonth) {// 同一个月
					String sql = this.createBaseSql(String
							.valueOf(this.startYear), String
							.valueOf(this.startMonth));
					sqlList.add(sql);
				} else { // 同年 不同月
					String sql1 = this.createBaseSql(String
							.valueOf(this.startYear), String
							.valueOf(this.startMonth));
					sqlList.add(sql1);

					for (int i = this.startMonth + 1; i < this.endMonth; i++) {
						String sql = this.createBaseSql(String
								.valueOf(this.startYear), String.valueOf(i));
						sqlList.add(sql);
					}
					String sql2 = this.createBaseSql(String
							.valueOf(this.endYear), String
							.valueOf(this.endMonth));
					sqlList.add(sql2);
				}

			} else if (this.startYear < this.endYear) { // 不同年

				String sql1 = this.createBaseSql(
						String.valueOf(this.startYear), String
								.valueOf(this.startMonth));
				sqlList.add(sql1);

				for (int i = this.startMonth + 1; i <= 12; i++) {// 月份限定
					String sql = this.createBaseSql(String
							.valueOf(this.startYear), String.valueOf(i));
					sqlList.add(sql);
				}

				for (int i = this.startYear + 1; i < this.endYear; i++) {// 年份限定
					for (int j = 1; j <= 12; j++) {
						String sql = this.createBaseSql(String.valueOf(i),
								String.valueOf(j));
						sqlList.add(sql);
					}
				}

				for (int i = 1; i < this.endMonth; i++) {// 月份限定
					String sql = this.createBaseSql(String
							.valueOf(this.endYear), String.valueOf(i));
					sqlList.add(sql);
				}

				String sql2 = this.createBaseSql(String.valueOf(this.endYear),
						String.valueOf(this.endMonth));
				sqlList.add(sql2);

			} else {// 错误

			}
		} else if ("0".equalsIgnoreCase(this.changeflag)) {
			String sql = this.createBaseSql();
			this.flag = true;
			sqlList.add(sql);
		}
		return sqlList;
	}

	public ArrayList executeSum(ArrayList oldList) {

		LazyDynaBean sumDynaBean = new LazyDynaBean();
		sumDynaBean.set("B0110", "合计");
		sumDynaBean.set("E0122", "");
		sumDynaBean.set("E01A1", "");
		sumDynaBean.set("A0101", "合计");
		sumDynaBean.set("Z0", "");
		sumDynaBean.set("Z1", "");

		for (int j = 0; j < this.showFieldItemList.size(); j++) {
			String itemid = (String) this.showFieldItemList.get(j);
			String value = "";
			String type=getItemType(itemid);
			int scale=0;
			if (sumFieldItemList.size() == 0) {
			} else {
				if (sumFieldItemList.contains(itemid)) {
					StringBuffer sql = new StringBuffer();
					sql.append("select ");
					for (int i = 0; i < oldList.size(); i++) {
						LazyDynaBean dynaBean = (LazyDynaBean) oldList.get(i);

						String temp = (String) dynaBean.get(itemid);
						if("N".equalsIgnoreCase(type)&&temp!=null&&temp.length()>0&&temp.indexOf(".")>-1){
							int len=temp.length()-temp.indexOf(".")-1;
							if(scale<len) {
                                scale=len;
                            }
						}
						if (temp == null || "".equals(temp.trim())) {
							sql.append("0");
							sql.append("+");
						} else {
							sql.append(temp);
							sql.append("+");
						}
					}
					sql.deleteCharAt(sql.length() - 1);
					sql.append(" as ss from ");
					sql.append(this.usrPre);
					sql.append("A01 where a0100 = '");
					sql.append(this.a0100);
					sql.append("' ");

					// System.out.println(sql);
					value = this.executeSumSql(sql.toString(),type,scale);
					if ("0".equalsIgnoreCase(value)) {
						value = "";
					}
				}
			}
			if("b0110".equalsIgnoreCase(itemid)){//合计行 默认b0110 内容为合计 当子集包含b0110取消A0101内容设置合计
				sumDynaBean.set("A0101", "");
			}
			sumDynaBean.set(itemid, value);
		}
		oldList.add(sumDynaBean);
		return oldList;
	}

	public String executeSumSql(String sql,String type,int scale) {
		String value = "";
		ContentDAO dao = new ContentDAO(this.conn);
		// System.out.println(sql);
		try {
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				value = rs.getString("ss");
				if("N".equalsIgnoreCase(type)) {
                    value=PubFunc.round(value, scale);
                }
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 执行SQL语句组
	 * 
	 * @param sql
	 * @return
	 */
	public ArrayList executeSQLList(ArrayList sqlList) {
		ArrayList list = new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		if (this.flag) {
			String sql = (String) sqlList.get(0);
			try {
				// System.out.println(sql);
				RowSet rs = dao.search(sql);
				while (rs.next()) {
					DynaBean bean = new LazyDynaBean();
					String org = rs.getString("B0110");
					String dept = rs.getString("E0122");
					String zw = rs.getString("E01A1");
					String name = rs.getString("A0101");

					org = this.getEmpOrgorDept("UN", org);
					zw = this.getEmpOrgorDept("@K", zw);
					dept = this.getEmpOrgorDept("UM", dept);
					bean.set("B0110", org);
					bean.set("E0122", dept);
					bean.set("E01A1", zw);
					bean.set("A0101", name);
					for (int i = 0; i < this.showFieldItemList.size(); i++) {
						String itemid = (String) this.showFieldItemList.get(i);

						String typeitem = getItemType(itemid);
						String itemidValue = "";
						if (typeitem != null && "D".equalsIgnoreCase(typeitem)) {
							Date itemate = rs.getDate(itemid);
							if (itemate != null) {
                                itemidValue = DateUtils.format(itemate,"yyyy.MM.dd HH:mm");
                            }
						} else {
                            itemidValue = rs.getString(itemid);
                        }
						if (itemidValue == null) {
							itemidValue = "";
						} else {
							if (itemidValue != null && itemidValue.length() > 0) {
                                itemidValue = this.getItemValueDesc(itemid,itemidValue);
                            }
						}
						if(itemidValue!=null&&!"".equals(itemidValue)) {
                            bean.set(itemid, itemidValue);
                        }
					}
					list.add(bean);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			for (int k = 0; k < sqlList.size(); k++) {
				String sql = (String) sqlList.get(k);
				// System.out.println(sql);
				try {
					RowSet rs = dao.search(sql);
					while (rs.next()) {
						DynaBean bean = new LazyDynaBean();

						String org = rs.getString("B0110");
						String dept = rs.getString("E0122");
						String zw = rs.getString("E01A1");
						String name = rs.getString("A0101");
						org = this.getEmpOrgorDept("UN", org);
						zw = this.getEmpOrgorDept("@K", zw);
						dept = this.getEmpOrgorDept("UM", dept);
						bean.set("B0110", org);
						bean.set("E0122", dept);
						bean.set("E01A1", zw);
						bean.set("A0101", name);

						if (this.flag) {
						} else {
							Date zd = rs.getDate("Z0");
							String z0 = "";
							if (zd != null) {
                                z0 = DateUtils.format(zd, "yyyy.MM.dd");
                            }
							String z1 = rs.getString("Z1");
							if (z1 == null) {
								z1 = "";
							}
							if (z0 != null) {
								z0 = z0.substring(0, 7);
							}

							if (z1.indexOf(".") != -1) {
								z1 = z1.substring(0, z1.indexOf("."));
							}

							bean.set("Z0", z0);
							bean.set("Z1", z1);
						}
						for (int i = 0; i < this.showFieldItemList.size(); i++) {
							String itemid = (String) this.showFieldItemList
									.get(i);
							String typeitem = getItemType(itemid);
							String itemidValue = "";
							if (typeitem != null
									&& "D".equalsIgnoreCase(typeitem)) {
								Date itemate = rs.getDate(itemid);
								if (itemate != null) {
                                    itemidValue = DateUtils.format(itemate,
                                            "yyyy.MM.dd HH:mm");
                                }
							} else {
                                itemidValue = rs.getString(itemid);
                            }
							if (itemidValue == null) {
								itemidValue = "";
							} else {
								if (itemidValue != null
										&& itemidValue.length() > 0) {
                                    itemidValue = this.getItemValueDesc(itemid,
                                            itemidValue);
                                }
							}
							if(itemidValue!=null&&!"".equals(itemidValue)) {
                                bean.set(itemid, itemidValue);
                            }
						}
						list.add(bean);
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 获得指标的值
	 * 
	 * @param itemid
	 * @param itemValue
	 * @return
	 */
	public String getItemValueDesc(String itemid, String itemValue) {
		String value = "";
		//liuy 2015-3-18 7971：自助服务/我的信息/我的薪酬，三个问题 begin
		FieldItem fitem = DataDictionary.getFieldItem(itemid);
		if(fitem==null)//FieldItem为空时直接返回原来的值
        {
            return itemValue;
        }
		if (fitem.isCode()) {// 代码型
			CodeItem cItem = AdminCode.getCode(fitem.getCodesetid(), itemValue);
			if(cItem != null) {
                value = cItem.getCodename();
            }
		} else {
			String itemType = fitem.getItemtype();
			if ("A".equalsIgnoreCase(itemType)) {
                value = itemValue;
            } else if ("D".equalsIgnoreCase(itemType)) {
                value = itemValue.substring(0, fitem.getItemlength());
            } else if ("N".equalsIgnoreCase(itemType)) {
                value = PubFunc.DoFormatDecimal2(itemValue, fitem.getDecimalwidth());//xuj udpate 7779齐鲁制药：数据显示方式不规范有的显示为空有的显示为零
            } else if ("M".equalsIgnoreCase(itemType)) {
                value = itemValue;
            } else {
                value = itemValue;
            }
		}//liuy 2015-3-18 end

		return value;
	}

	/**
	 * 创建基本SQL语句片段
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @return
	 */
	public String createBaseSql(String year, String month) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append(this.usrPre);
		sql.append("A01.B0110 , ");// 单位
		sql.append(this.usrPre);
		sql.append("A01.E0122 , ");// 部门
		sql.append(this.usrPre);
		sql.append("A01.E01A1 , ");// 职位名称
		sql.append(this.usrPre);
		sql.append("A01.A0101 ,");// 姓名

		String baseItemId = this.getBaseItemId(year, month);// 基准
		String baseSetId = this.getFieldSetId(baseItemId);
		// System.out.println("baseItemId=" + baseItemId);

		// 显示年月标识 和 次数
		if ("".equals(baseItemId) || "A01".equalsIgnoreCase(baseSetId)) {
			this.flag = true;// 只选了主集
		} else {
			// sql.append(baseItemId);
			sql.append(this.usrPre);
			sql.append(baseSetId);
			sql.append(".");
			sql.append(baseSetId);
			sql.append("Z0 as Z0 ,");
			// sql.append(baseItemId);
			sql.append(this.usrPre);
			sql.append(baseSetId);
			sql.append(".");
			sql.append(baseSetId);
			sql.append("Z1 as Z1 ,");

			if (this.query_field != null && this.query_field.length() > 0
					&& !this.query_field.equalsIgnoreCase(baseSetId + "Z0")) {
                sql.append(this.usrPre + baseSetId + "." + this.query_field
                        + " as Z3,");
            }
		}

		// 指标列表
		for (int i = 0; i < this.showFieldItemList.size(); i++) {
			String itemid = (String) this.showFieldItemList.get(i);
			String setid = this.getFieldSetId(itemid.trim()); // 指标集
			sql.append(this.usrPre);
			sql.append(setid);
			sql.append("." + itemid);
			sql.append(",");
		}
		/*
		 * for(int i=0 ; i<this.showFieldItemList.size(); i++){ String itemid =
		 * (String) this.showFieldItemList.get(i); String setid =
		 * this.getFieldSetId(itemid.trim()); //指标集
		 * if(setid.equalsIgnoreCase("A01")){ sql.append(this.usrPre);
		 * sql.append("A01."); sql.append(itemid); sql.append(","); }else{
		 * sql.append(itemid); //sql.append(this.usrPre); //sql.append(setid);
		 * sql.append("."); sql.append(itemid); sql.append(","); } }
		 */

		// 去掉最后的逗号
		sql.deleteCharAt(sql.length() - 1);

		sql.append(" from ");
		sql.append(this.usrPre);
		sql.append("A01 ");
		if ("".equals(baseItemId) || "A01".equalsIgnoreCase(baseSetId)) {
			// sql.deleteCharAt(sql.length()-1);
			sql.append(" where ");
			sql.append(this.usrPre);
			sql.append("A01.A0100='");
			sql.append(this.a0100);
			sql.append("'");
			return sql.toString();
		} else {

			sql.append(" left join ");
			sql.append("" + this.usrPre + baseSetId);
			sql.append(" on " + this.usrPre + "A01.a0100=" + this.usrPre
					+ baseSetId + ".a0100");
			sql.append(" ");
			sql.append(" where " + this.usrPre + baseSetId + ".a0100 ='");
			sql.append(this.a0100);
			sql.append("' and " + Sql_switcher.year(this.getZ0(baseSetId))
					+ " ");
			sql.append("= '");
			sql.append(year);
			sql.append("' and " + Sql_switcher.month(this.getZ0(baseSetId)));
			sql.append("= '");
			sql.append(month + "'");
			if ("4".equals(this.operateFlag) && !"0".equals(this.changeflag)) {// 时间段
				if (this.startYear == Integer.parseInt(year)
						&& this.startMonth == Integer.parseInt(month)
						&& this.endYear == Integer.parseInt(year)
						&& this.endMonth == Integer.parseInt(month)) {
					sql.append(" and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append("day("+this.getZ0(baseSetId)+")");
					sql.append(">= '");
					sql.append(this.startDate);
					sql.append("' and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append(this.getZ0(baseSetId));
					sql.append("< '");
					sql.append(this.endDate);
					sql.append("'");
				} else if (this.startYear == Integer.parseInt(year)
						&& this.startMonth == Integer.parseInt(month)) {
					sql.append(" and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append(this.getZ0(baseSetId));
					sql.append(">= '");
					sql.append(this.startDate);
					sql.append("' and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append(this.getZ0(baseSetId));
					sql.append("<= '31' ");

				} else if (this.endYear == Integer.parseInt(year)
						&& this.endMonth == Integer.parseInt(month)) {
					sql.append(" and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append(this.getZ0(baseSetId));
					sql.append("< '");
					sql.append(this.endDate);
					sql.append("'");
					sql.append(" and "
							+ Sql_switcher.day(this.getZ0(baseSetId)));
					// sql.append(this.getZ0(baseSetId));
					sql.append(">= '1' ");

				}
			}
			/*
			 * sql.append(" ( select a0100 , "); sql.append(baseSetId);
			 * sql.append("Z0 , "); sql.append(baseSetId); sql.append("Z1 , ");
			 * if(this.query_field!=null&&this.query_field.length()>0&&!this.query_field.equalsIgnoreCase(baseSetId+"Z0"))
			 * sql.append(this.query_field+", "); sql.append(baseItemId);
			 * sql.append(" from "); sql.append(this.usrPre);
			 * sql.append(baseSetId); sql.append(" where a0100 ='");
			 * sql.append(this.a0100); sql.append("' and
			 * "+Sql_switcher.year(this.getZ0(baseSetId))+" "); //sql.append();
			 * sql.append("= '"); sql.append(year); sql.append("' and
			 * "+Sql_switcher.month(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append("= '");
			 * sql.append(month); sql.append("' ");
			 * 
			 * if(this.operateFlag.equals("4")&&!this.changeflag.equals("0")){//时间段
			 * if(this.startYear == Integer.parseInt(year) && this.startMonth ==
			 * Integer.parseInt(month)&& this.endYear == Integer.parseInt(year) &&
			 * this.endMonth == Integer.parseInt(month)){ sql.append(" and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append("day("+this.getZ0(baseSetId)+")"); sql.append(">=
			 * '"); sql.append(this.startDate); sql.append("' and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append("< '");
			 * sql.append(this.endDate); sql.append("'"); }else
			 * if(this.startYear == Integer.parseInt(year) && this.startMonth ==
			 * Integer.parseInt(month)){ sql.append(" and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append(">= '");
			 * sql.append(this.startDate); sql.append("' and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append("<= '31' ");
			 * 
			 * }else if(this.endYear == Integer.parseInt(year) && this.endMonth ==
			 * Integer.parseInt(month)){ sql.append(" and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append("< '");
			 * sql.append(this.endDate); sql.append("'"); sql.append(" and
			 * "+Sql_switcher.day(this.getZ0(baseSetId)));
			 * //sql.append(this.getZ0(baseSetId)); sql.append(">= '1' "); } }
			 * sql.append(") "); sql.append(baseItemId);
			 */

		}

		/*
		 * for(int i=0 ; i<this.showFieldItemList.size(); i++){ String sf =
		 * (String) this.showFieldItemList.get(i); //指标项 String setid =
		 * this.getFieldSetId(sf.trim()); //指标集
		 * if(sf.equalsIgnoreCase(baseItemId)){ continue; }else{
		 * if(setid.equalsIgnoreCase("a01")){ continue; }else{ sql.append(" left
		 * join "); sql.append(" ( select a0100 , "); sql.append(setid);
		 * sql.append("Z0 , "); sql.append(setid); sql.append("Z1 , ");
		 * if(this.query_field!=null&&this.query_field.length()>0&&!this.query_field.equalsIgnoreCase(setid+"Z0"))
		 * sql.append( this.query_field+", "); sql.append(sf); sql.append(" from
		 * "); sql.append(this.usrPre); sql.append(setid); sql.append(" where
		 * a0100 ='"); sql.append(this.a0100); sql.append("' and
		 * "+Sql_switcher.year(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append("= '"); sql.append(year);
		 * sql.append("' and "+Sql_switcher.month(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append("= '");
		 * sql.append(month); sql.append("' ");
		 * 
		 * if(this.operateFlag.equals("4")&&!this.changeflag.equals("0")){//时间段
		 * if(this.startYear == Integer.parseInt(year) && this.startMonth ==
		 * Integer.parseInt(month)&& this.endYear == Integer.parseInt(year) &&
		 * this.endMonth == Integer.parseInt(month)){ sql.append(" and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append(">= '");
		 * sql.append(this.startDate); sql.append("' and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append("< '");
		 * sql.append(this.endDate); sql.append("'"); }else if(this.startYear ==
		 * Integer.parseInt(year) && this.startMonth ==
		 * Integer.parseInt(month)){ sql.append(" and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append(">= '");
		 * sql.append(this.startDate); sql.append("' and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append("<= '31' ");
		 * 
		 * }else if(this.endYear == Integer.parseInt(year) && this.endMonth ==
		 * Integer.parseInt(month)){ sql.append(" and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append("< '");
		 * sql.append(this.endDate); sql.append("'"); sql.append(" and
		 * "+Sql_switcher.day(this.getZ0(setid)));
		 * //sql.append(this.getZ0(setid)); sql.append(">= '1' "); } }
		 * sql.append(")"); sql.append(sf);
		 * 
		 * sql.append(" on "); sql.append(baseItemId); sql.append(".");
		 * sql.append(baseSetId); sql.append("Z1 = "); sql.append(sf);
		 * 
		 * sql.append("."); sql.append(setid); sql.append("Z1");
		 *  } } }
		 */

		/*
		 * sql.append(" where "); sql.append(this.usrPre);
		 * sql.append("A01.A0100='"); sql.append(this.a0100); sql.append("'");
		 */
		// System.out.println(sql.toString());
		return sql.toString();
	}

	/**
	 * 获得指标值
	 * 
	 * @param sql
	 * @param showItemId
	 * @return
	 */
	public String getFieldItemValue(String sql, String showItemId) {
		String value = "";
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				value = rs.getString(showItemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 获得代码型指标的值
	 * 
	 * @param codesetId
	 *            代码类ID
	 * @param codeItemId
	 *            代码项
	 * @return
	 */
	public String getCodeItemDesc(String codesetId, String codeItemId) {
		CodeItem codeItem = AdminCode.getCode(codesetId, codeItemId);
		if (codeItem == null) {
            return "";
        }
		
		return codeItem.getCodename();
	}

	/**
	 * 判断指标是否式代码型指标
	 * 
	 * @param itemId
	 * @return 返回代码类 或 0
	 */
	public String getItemCodeset(String itemId) {
		FieldItem item = DataDictionary.getFieldItem(itemId);
		if (item == null) {
            return "";
        }
		
		return item.getCodesetid();
	}

	/**
	 * 获得指标集ID
	 * 
	 * @param itemid
	 * @return
	 */
	public String getFieldSetId(String itemid) {
		FieldItem item = DataDictionary.getFieldItem(itemid);
		if (item == null) {
            return "";
        }
		
		return item.getFieldsetid();
	}

	/**
	 * 获得基准指标(左外链接)
	 * 
	 * @return
	 */
	public String getBaseItemId(String year, String month) {
		String baseItemId = "";
		int n = 0;
		for (int i = 0; i < this.showFieldItemList.size(); i++) {
			StringBuffer sql = new StringBuffer();
			String showItemId = (String) this.showFieldItemList.get(i);
			String setId = this.getFieldSetId(showItemId.trim());
			if ("a01".equalsIgnoreCase(setId)) { // 人员主集
				continue;
			}
			sql.append(" select count(*) as num  from ");
			sql.append(usrPre + setId);
			sql.append("  where a0100 ='");
			sql.append(a0100);
			sql.append("' and " + Sql_switcher.year(this.getZ0(setId)));
			// sql.append(this.getZ0(setId));
			sql.append("= '");
			sql.append(year);
			sql.append("' and " + Sql_switcher.month(this.getZ0(setId)));
			// sql.append(this.getZ0(setId));
			sql.append("= '");
			sql.append(month);
			sql.append("'");

			if ("4".equals(this.operateFlag) && !"0".equals(this.changeflag)) {// 时间段
				// System.out.println(year + " " + month);
				if (this.startYear == Integer.parseInt(year)
						&& this.startMonth == Integer.parseInt(month)) {
					sql.append(" and " + Sql_switcher.day(this.getZ0(setId)));
					// sql.append(this.getZ0(setId));
					sql.append(">= '");
					sql.append(this.startDate);
					sql.append("' and " + Sql_switcher.day(this.getZ0(setId)));
					// sql.append(this.getZ0(setId));
					sql.append("<= '31' ");

				} else if (this.endYear == Integer.parseInt(year)
						&& this.endMonth == Integer.parseInt(month)) {
					sql.append(" and " + Sql_switcher.day(this.getZ0(setId)));
					// sql.append(this.getZ0(setId));
					sql.append("< '");
					sql.append(this.endDate);
					sql.append("'");
					sql.append(" and " + Sql_switcher.day(this.getZ0(setId)));
					// sql.append(this.getZ0(setId));
					sql.append(">= '1' ");

				} else if (this.startYear == Integer.parseInt(year)
						&& this.startMonth == Integer.parseInt(month)
						&& this.endYear == Integer.parseInt(year)
						&& this.endMonth == Integer.parseInt(month)) {
					sql.append(" and " + Sql_switcher.day(this.getZ0(setId)));
					;
					// sql.append(this.getZ0(setId));
					sql.append(">= '");
					sql.append(this.startDate);
					sql.append("' and " + Sql_switcher.day(this.getZ0(setId)));
					// sql.append(this.getZ0(setId));
					sql.append("< '");
					sql.append(this.endDate);
					sql.append("'");
				}
			}

			/*
			 * System.out.println("获得基准指标的SQL");
			 * System.out.println(sql.toString()); System.out.println();
			 */

			int sum = this.getEmpGLNumber(sql.toString());
			if (sum >= n) {
				baseItemId = showItemId;
			}
		}
		return baseItemId;
	}

	/**
	 * 员工关联信息个数
	 * 
	 * @param sql
	 * @return
	 */
	public int getEmpGLNumber(String sql) {
		// System.out.println(sql);
		int num = 0;
		ContentDAO dao = new ContentDAO(this.conn);
		try {
			RowSet rs = dao.search(sql);
			if (rs.next()) {
				num = rs.getInt("num");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;
	}

	/**
	 * 获取规范的表达式的值,自动四舍五入
	 * 
	 * @param exprValue
	 *            表达式值
	 * @param flag
	 *            小数位
	 * @return 规范后的值
	 */
	public String formatValue(String exprValue, int flag) {

		StringBuffer sb = new StringBuffer();
		if (flag == 0) {
			sb.append("####");
		} else {
			sb.append("####.");
			for (int i = 0; i < flag; i++) {
				sb.append("0");
			}
		}
		DecimalFormat df = new DecimalFormat(sb.toString());
		String dstr = df.format(Double.parseDouble(exprValue));
		// System.out.println("传入数据=" + exprValue + "小数位=" + flag + "规范化数据为=" +
		// dstr) ;
		double dv = Double.parseDouble(dstr);
		if (dv == 0) {
			dstr = "0" + dstr;
		}
		return dstr;
	}

	/**
	 * 获取员工部门
	 * 
	 * @return
	 */
	private String getEmpOrgorDept(String codeSetId, String codeItemId) {
		return AdminCode.getCodeName(codeSetId, codeItemId);
	}

	public String getA0100() {
		return a0100;
	}

	public void setA0100(String a0100) {
		this.a0100 = a0100;
	}

	public String getUsrPre() {
		return usrPre;
	}

	public void setUsrPre(String usrPre) {
		this.usrPre = usrPre;
	}

	public String getOperateFlag() {
		return operateFlag;
	}

	public void setOperateFlag(String operateFlag) {
		this.operateFlag = operateFlag;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getQuarter() {
		return quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public ArrayList getShowFieldItemList() {
		return showFieldItemList;
	}

	public void setShowFieldItemList(ArrayList showFieldItemList) {
		this.showFieldItemList = showFieldItemList;
	}

	public ArrayList getSumFieldItemList() {
		return sumFieldItemList;
	}

	public void setSumFieldItemList(ArrayList sumFieldItemList) {
		this.sumFieldItemList = sumFieldItemList;
	}

	public int getEndDate() {
		return endDate;
	}

	public void setEndDate(int endDate) {
		this.endDate = endDate;
	}

	public int getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(int endMonth) {
		this.endMonth = endMonth;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public int getStartDate() {
		return startDate;
	}

	public void setStartDate(int startDate) {
		this.startDate = startDate;
	}

	public int getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public UserView getUserView() {
		return userView;
	}

	public void setUserView(UserView userView) {
		this.userView = userView;
	}

	public ArrayList getFieldSetList() {
		return fieldSetList;
	}

	public void setFieldSetList(ArrayList fieldSetList) {
		this.fieldSetList = fieldSetList;
	}

	public String getQuery_field() {
		return query_field;
	}

	public void setQuery_field(String query_field) {
		this.query_field = query_field;
	}

	public String getChangeflag() {
		return changeflag;
	}

	public void setChangeflag(String changeflag) {
		this.changeflag = changeflag;
	}

	public String getFieldsetid() {
		return fieldsetid;
	}

	public void setFieldsetid(String fieldsetid) {
		this.fieldsetid = fieldsetid;
	}

	/**
	 * 初始化薪酬明细设置(显示列与合计列)
	 * 
	 * @param conn
	 */
	public void init_Fieldsetid(Connection conn) throws GeneralException {
		Sys_Oth_Parameter sop = new Sys_Oth_Parameter(conn);
		this.query_field = sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", this.fieldsetid, "title", this.title, "query_field");
		String salary_text = sop.getValue(Sys_Oth_Parameter.MYSALARYS_SALARY,
				"setname", this.fieldsetid, "title", this.title, "");
		String sumitemValue = sop.getChildText(
				Sys_Oth_Parameter.MYSALARYS_SALARY, "setname", this.fieldsetid,
				"title", this.title);
		if (sumitemValue == null) {
			sumitemValue = "";
			throw GeneralExceptionHandler.Handle(new GeneralException("",
					"该子集没有定义指标项！", "", ""));
		}
		String[] temp_showFieldItems = salary_text.split(",");
		String[] temp_sumFieldItems = sumitemValue.split(",");
		ArrayList fieldSetTempList = this.userView.getPrivFieldSetList(
				Constant.EMPLOY_FIELD_SET, 0);
		for (int i = 0; i < fieldSetTempList.size(); i++) {
			FieldSet fieldSet = (FieldSet) fieldSetTempList.get(i);
			String setid = fieldSet.getFieldsetid();
			String setChangeFlag = fieldSet.getChangeflag();
			if (setChangeFlag == null) {
			} else {
				if (fieldsetid.toLowerCase().equalsIgnoreCase(
						setid.toLowerCase())) {
					this.changeflag = setChangeFlag;
					// this.operateFlag =setChangeFlag;
					break;
				}

			}
		}
		if (this.changeflag == null || this.changeflag.length() <= 0) {
            this.changeflag = "0";
        }
		for (int i = 0; i < temp_showFieldItems.length; i++) {
			String itemid = temp_showFieldItems[i].trim();
			if ("".equals(itemid)) {
			} else {
				if (this.showFieldItemList.contains(itemid)) {// 去除重复指标
				} else if ("B0110".equalsIgnoreCase(itemid)
						|| "E0122".equalsIgnoreCase(itemid)
						|| "A0101".equalsIgnoreCase(itemid)
						|| "E01A1".equalsIgnoreCase(itemid)) {
				} else {
					if(checkItemContext("",itemid.trim())) {
                        this.showFieldItemList.add(itemid.trim());
                    }
				}
			}
		}

		for (int i = 0; i < temp_sumFieldItems.length; i++) {
			String itemid = temp_sumFieldItems[i].trim();
			if ("".equals(itemid)) {
			} else {
				if (this.sumFieldItemList.contains(itemid)) {
				} else {
					this.sumFieldItemList.add(itemid.trim());
				}
			}

		}
		this.yearlist = getHaveYearList(fieldsetid, this.query_field);
	}

	public String createBaseSql() {
		StringBuffer sql = new StringBuffer();
		sql.append(" select ");
		sql.append(this.usrPre);
		sql.append("A01.B0110 , ");// 单位
		sql.append(this.usrPre);
		sql.append("A01.E0122 , ");// 部门
		sql.append(this.usrPre);
		sql.append("A01.E01A1 , ");// 职位名称
		sql.append(this.usrPre);
		sql.append("A01.A0101 ,");// 姓名

		// 指标列表
		for (int i = 0; i < this.showFieldItemList.size(); i++) {
			String itemid = (String) this.showFieldItemList.get(i);
			String setid = this.getFieldSetId(itemid.trim()); // 指标集
			if ("A01".equalsIgnoreCase(setid)) {
				sql.append(this.usrPre);
				sql.append("A01.");
				sql.append(itemid);
				sql.append(",");
			} else {
				// sql.append(itemid);
				sql.append(this.usrPre);
				sql.append(setid);
				sql.append(".");
				sql.append(itemid);
				sql.append(",");
			}
		}

		// 去掉最后的逗号
		sql.deleteCharAt(sql.length() - 1);

		sql.append(" from ");
		sql.append(this.usrPre + "A01");
		if (this.fieldsetid != null && this.fieldsetid.length() > 0) {
			sql.append(" left join ");
			sql.append("" + this.usrPre + this.fieldsetid);
			sql.append(" on " + this.usrPre + "A01.a0100=" + this.usrPre
					+ this.fieldsetid + ".a0100");
		}

		sql.append(" where ");
		sql.append(this.usrPre);
		sql.append("A01.A0100='");
		sql.append(this.a0100);
		sql.append("'");
		//liuy 2015-9-10 12568：审协北京中心：我的薪酬设置只显示某年数据后无效 begin
		//第一次点入的时候，如果薪酬表设置里的其他设置：某年以后的薪酬不为空，则加上
		if(StringUtils.isNotEmpty(year_restrict)&& "0".equalsIgnoreCase(this.changeflag)&&StringUtils.isNotEmpty(this.fieldsetid)){
			String year_restrict = this.getYear_restrict();
			sql.append(" and year(");
			sql.append(this.usrPre + this.fieldsetid +"."+ this.fieldsetid);
			sql.append("Z0) >=");
			sql.append(year_restrict);
		}
		//liuy 2015-9-10 end
		return sql.toString();
	}

	/**
	 * 获得指标名称
	 * 
	 * @param itemid
	 * @return
	 */
	public String getQueryFieldName(String itemid) {
		if (itemid == null || itemid.length() <= 0) {
            return "";
        }
		
		FieldItem item = DataDictionary.getFieldItem(itemid);
		if (item == null) {
            return "";
        }
		
		return item.getItemdesc();
	}

	public String getZ0(String setID) {
		if (this.query_field != null && this.query_field.length() > 0) {
			// return setID+"Z0";
			return this.query_field;
		} else {
			return setID + "Z0";
		}
	}

	public ArrayList getHaveYearList(String fieldsetid, String itemid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select itemid  from fielditem where");
		sql.append(" fieldsetid='" + fieldsetid.toUpperCase() + "'");
		sql.append(" and itemtype='D' and useflag='1'");
		if (itemid != null && itemid.length() > 0) {
            sql.append(" and itemid='" + itemid + "'");
        }
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList list = new ArrayList();
		try {
			RowSet rs = dao.search(sql.toString());
			itemid = "";
			if (rs.next()) {
				itemid = rs.getString("itemid");
				sql = new StringBuffer();
				sql.append("select Distinct(" + Sql_switcher.year(itemid)
						+ ") as aa");
				sql.append(" from " + this.usrPre + fieldsetid);
				sql.append(" order by aa desc");
				rs = dao.search(sql.toString());
				String year = "";
				while (rs.next()) {
					year = rs.getString("aa");
					if (year == null || year.length() <= 0
							|| "NULL".equalsIgnoreCase(year)) {
                        continue;
                    }
					if (this.year_restrict != null
							&& this.year_restrict.length() > 0) {
						if (Integer.parseInt(year) < Integer
								.parseInt(this.year_restrict)) {
                            continue;
                        }
					}
					CommonData co = new CommonData();
					co.setDataName(year);
					co.setDataValue(year);
					list.add(co);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String getYear_restrict() {
		return year_restrict;
	}

	public void setYear_restrict(String year_restrict) {
		this.year_restrict = year_restrict;
	}

	public String getPrv_flag() {
		return prv_flag;
	}

	public void setPrv_flag(String prv_flag) {
		this.prv_flag = prv_flag;
	}
}
