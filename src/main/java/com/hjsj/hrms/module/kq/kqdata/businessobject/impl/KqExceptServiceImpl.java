package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.module.kq.kqdata.businessobject.KqExceptService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**  
 * <p>Title: KqExceptServiceImpl</p>  
 * <p>Description: 出勤异常查看</p>  
 * <p>Company: hjsj</p>
 * @date 2018年08月27日 下午2:47:37
 * @author xuanz  
 * @version 
 */  
public class KqExceptServiceImpl implements KqExceptService {
	private UserView userView;
	private Connection conn = null;
	
	public KqExceptServiceImpl(UserView userView, Connection conn) {
		this.userView = userView;
		this.conn = conn;
	}
	@Override
    public String getKqExcept(String guidkey, String startDate, String endDate) {
		return null;
	}
	/**
	 * 获取出勤异常sql
	 * @param guidkey
	 * @param startDate
	 * @param endDate
	 * @return
     * @date 2019年08月27日 下午2:13:20
     * @author xuanz
     */
	private String getKqExceptSql(String guidkey, String startDate, String endDate) {
		
		KqPrivForHospitalUtil kqpriv = new KqPrivForHospitalUtil(this.userView, this.conn);
		// 56723 去全部考勤人员库
		String nbases = kqpriv.getKqNbases();
		// 获取请假子集指标
  		String leaveFieldsetid = kqpriv.getLeave_setid();
  		// 获取公出子集指标
  		String officeLeaveFieldsetid = kqpriv.getOfficeleave_setid();
  		// 获取加班子集指标
  		String overtimeFieldsetid = kqpriv.getOvertime_setid();
  		StringBuffer appSql = new StringBuffer();
  		// 请假
  		FieldItem leaveStartFieldItem = null;
  		FieldItem leaveEndFieldItem = null;
  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
  			FieldItem fi =null;
  			if (StringUtils.isNotBlank(kqpriv.getLeave_type())) {
  				fi =DataDictionary.getFieldItem(kqpriv.getLeave_type(),  leaveFieldsetid);
  				appSql.append(","+leaveFieldsetid+"."+fi.getItemid());
			}
  			if (StringUtils.isNotBlank(kqpriv.getLeave_reason())) {
  				fi =DataDictionary.getFieldItem(kqpriv.getLeave_reason(),  leaveFieldsetid);
  				appSql.append(","+leaveFieldsetid+"."+fi.getItemid());
  			}
  			if (StringUtils.isNotBlank(kqpriv.getLeave_start())) {
  				leaveStartFieldItem = DataDictionary.getFieldItem(kqpriv.getLeave_start(),  leaveFieldsetid);
  				appSql.append(","+leaveFieldsetid+"."+leaveStartFieldItem.getItemid());
  			}
  			if (StringUtils.isNotBlank(kqpriv.getLeave_end())) {
  				leaveEndFieldItem =DataDictionary.getFieldItem(kqpriv.getLeave_end(),  leaveFieldsetid);
  				appSql.append(","+leaveFieldsetid+"."+leaveEndFieldItem.getItemid());
  			}
  		}
  		// 公出
  		FieldItem officeleaveStartFieldItem = null;
  		FieldItem officeleaveEndFieldItem = null;
  		if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
  			FieldItem fi =null;
  			if (StringUtils.isNotBlank(kqpriv.getOfficeleave_type())) {
  				fi =DataDictionary.getFieldItem(kqpriv.getOfficeleave_type(),  officeLeaveFieldsetid);
  				appSql.append(","+officeLeaveFieldsetid+"."+fi.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOfficeleave_reason())) {
	  			fi =DataDictionary.getFieldItem(kqpriv.getOfficeleave_reason(),  officeLeaveFieldsetid);
	  			appSql.append(","+officeLeaveFieldsetid+"."+fi.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOfficeleave_start())) {
	  			officeleaveStartFieldItem =DataDictionary.getFieldItem(kqpriv.getOfficeleave_start(),  officeLeaveFieldsetid);
	  			appSql.append(","+officeLeaveFieldsetid+"."+officeleaveStartFieldItem.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOfficeleave_end())) {
	  			officeleaveEndFieldItem =DataDictionary.getFieldItem(kqpriv.getOfficeleave_end(),  officeLeaveFieldsetid);
	  			appSql.append(","+officeLeaveFieldsetid+"."+officeleaveEndFieldItem.getItemid());
			}
  		} 
	  	// 加班 
  		FieldItem overtimeStartFieldItem = null;
  		FieldItem overtimeEndFieldItem = null;
  		if (StringUtils.isNotBlank(overtimeFieldsetid)) {
  			FieldItem fi =null;
  			if (StringUtils.isNotBlank(kqpriv.getOvertime_type())) {
  				fi =DataDictionary.getFieldItem(kqpriv.getOvertime_type(),  overtimeFieldsetid);
  				appSql.append(","+overtimeFieldsetid+"."+fi.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOvertime_reason())) {
	  			fi =DataDictionary.getFieldItem(kqpriv.getOvertime_reason(),  overtimeFieldsetid);
	  			appSql.append(","+overtimeFieldsetid+"."+fi.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOvertime_start())) {
	  			overtimeStartFieldItem =DataDictionary.getFieldItem(kqpriv.getOvertime_start(),  overtimeFieldsetid);
	  			appSql.append(","+overtimeFieldsetid+"."+overtimeStartFieldItem.getItemid());
			}
	  		if (StringUtils.isNotBlank(kqpriv.getOvertime_end())) {
	  			overtimeEndFieldItem =DataDictionary.getFieldItem(kqpriv.getOvertime_end(),  overtimeFieldsetid);
	  			appSql.append(","+overtimeFieldsetid+"."+overtimeEndFieldItem.getItemid());
			}
  		}
  		
  		StringBuffer sql = new StringBuffer();
		String[] dbnameList = StringUtils.split(nbases, ",");
        for(int i = 0; i < dbnameList.length; i++){
            String dbname = dbnameList[i];
            if(StringUtils.isBlank(dbname)) {
            	continue;
            }
            if(i > 0)
            	sql.append(" UNION ALL ");
            sql.append("select ");
            String dateString=Sql_switcher.dateToChar("kad.kq_date", "YYYY-MM-DD");
            sql.append(dateString);
            sql.append(" as kq_date,kad.Kq_status,kad.Card_data,");
            sql.append("ONDUTY_BE_LATE_1,ONDUTY_ABSENT_1,OFFDUTY_LEAVE_EARLY_1,OFFDUTY_ABSENT_1,");
            sql.append("ONDUTY_BE_LATE_2,ONDUTY_ABSENT_2,OFFDUTY_LEAVE_EARLY_2,OFFDUTY_ABSENT_2,");
            sql.append("ONDUTY_BE_LATE_3,ONDUTY_ABSENT_3,OFFDUTY_LEAVE_EARLY_3,OFFDUTY_ABSENT_3,");
            sql.append("ONDUTY_CARD_1,OFFDUTY_CARD_1,ONDUTY_CARD_2,OFFDUTY_CARD_2,ONDUTY_CARD_3,OFFDUTY_CARD_3");
            
            sql.append(appSql.toString());
            
            sql.append(" from "+ dbname +"A01 a01");
            sql.append(Sql_switcher.left_join("", "kq_analyse_data kad", "a01.GUIDKEY", "kad.guidkey"));
            if (StringUtils.isNotBlank(leaveFieldsetid)) {
            	sql.append(" left join " + dbname+leaveFieldsetid+" "+leaveFieldsetid+" on a01.A0100="+leaveFieldsetid+".A0100"
            			+ " and  "+dateString+"  between "+Sql_switcher.dateToChar(leaveFieldsetid+"."+leaveStartFieldItem.getItemid(), "YYYY-MM-DD")
            			+ " and "+Sql_switcher.dateToChar(leaveFieldsetid+"."+leaveEndFieldItem.getItemid(), "YYYY-MM-DD"));
			}
            if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
            	sql.append(" left join " + dbname+officeLeaveFieldsetid+" "+officeLeaveFieldsetid+" on a01.A0100="+officeLeaveFieldsetid+".A0100"
            			+ " and  "+dateString+"  between "
            			+ Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+officeleaveStartFieldItem.getItemid(), "YYYY-MM-DD")
            			+ " and  "+Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+officeleaveEndFieldItem.getItemid(), "YYYY-MM-DD"));
            }
            if (StringUtils.isNotBlank(overtimeFieldsetid)) {
            	sql.append(" left join " + dbname+overtimeFieldsetid+" "+overtimeFieldsetid+" on a01.A0100="+overtimeFieldsetid+".A0100"
            			+ " and "+dateString+"  between "+Sql_switcher.dateToChar(overtimeFieldsetid+"."+overtimeStartFieldItem.getItemid(), "YYYY-MM-DD")
            			+ " and  "+Sql_switcher.dateToChar(overtimeFieldsetid+"."+overtimeEndFieldItem.getItemid(), "YYYY-MM-DD"));
            }
            sql.append(" where  kad.guidkey='"+ guidkey+"'  ");
            sql.append(" and  kad.kq_date  between "+ Sql_switcher.charToDate("'"+startDate+"'")+" and "+ Sql_switcher.charToDate("'"+endDate+"'"));
//	          sql.append(" and  "+Sql_switcher.isnull("kad.Kq_status", "'0'") +" not in('正常','休息') ");
        }
        //【53564】 客户包（应急中心-新考勤补丁）：有重复的公出数据，在异常情况查看中需要去重
        String sqlHead="select ROW_NUMBER() OVER(ORDER BY b.kq_date) xuhao, b.* from (select a.*,row_number() over(partition by a.kq_date order by a.kq_date desc) rn from(";
        String sqlEND=") a ) b   where rn=1 order by kq_date";
        return sqlHead+ sql.toString()+sqlEND;
	}
    /**
     * 获取表格控件配置
     * getKqExceptTableConfig
     * @param guidkey
	 * @param startDate
	 * @param endDate
     * @return
     * @throws GeneralException
     * @date 2019年8月27日 下午2:02:14
     * @author  xuanz
     */
	@Override
    public String getKqExceptTableConfig(String guidkey, String startDate, String endDate) throws GeneralException{
		String config="";
		try {
			ArrayList columnList = this.getKqExceptColumns();
			String sql=this.getKqExceptSql(guidkey, startDate, endDate);
			TableConfigBuilder builder = new TableConfigBuilder("KqExcept_001", columnList, "KqExcept_001", this.userView, this.conn);
			builder.setTitle("");
			ArrayList<LazyDynaBean> dataList= this.checkListData(guidkey, startDate, endDate, this.listKqExceptData(sql));
			builder.setDataList(this.checkNormalData(dataList));
			builder.setSelectable(false);// 选框
			builder.setPageSize(31);
			builder.setScheme(false);
			builder.setSortable(false);
			config = builder.createExtTableConfig(); 
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return config;
	}
	/**
	 * 获取出勤异常列表表头
	 * @return columnList
	 */
	private ArrayList<ColumnsInfo> getKqExceptColumns(){
		  ArrayList columnList = new ArrayList();
		  try {
			  KqPrivForHospitalUtil privForHospitalUtil=new KqPrivForHospitalUtil(userView, conn);
			  ColumnsInfo columnsInfo =getColumnsInfo("xuhao", ResourceFactory.getProperty("kq.date.appeal.except.seq"), 40,"0", "N", 100, 0, "");
			  columnList.add(columnsInfo); 
//			  FieldItem fi =DataDictionary.getFieldItem("kq_date", "kq_analyse_data");
			  columnsInfo = getColumnsInfo("kq_date", ResourceFactory.getProperty("kq.date.appeal.except.kqDate"), 100, "0", "A", 100, 0, "");
			  columnList.add(columnsInfo);
			  columnsInfo = getColumnsInfo("Kq_status", ResourceFactory.getProperty("kq.date.appeal.except.status"), 100, "0", "A", 100, 0, "");
			  columnList.add(columnsInfo);
			  columnsInfo = getColumnsInfo("Card_data", ResourceFactory.getProperty("kq.date.appeal.except.cardData"), 120, "0", "A", 100, 0, "");
			  columnList.add(columnsInfo);
			  columnsInfo = getColumnsInfo("Onduty_absent", ResourceFactory.getProperty("kq.date.appeal.except.absent"), 140, "", "A", 0, 0, "");
			  ColumnsInfo childColumnsInfo = getColumnsInfo("Onduty_absent_card",ResourceFactory.getProperty("kq.date.appeal.except.startTime"), 70, "0", "A", 100, 0, "");
			  columnsInfo.addChildColumn(childColumnsInfo);
			  childColumnsInfo = getColumnsInfo("Offduty_absent_card", ResourceFactory.getProperty("kq.date.appeal.except.endTime"), 70, "0", "A", 100, 0, "");
			  columnsInfo.addChildColumn(childColumnsInfo);
			  columnList.add(columnsInfo);
			  columnsInfo = getColumnsInfo("Onduty_be_late", ResourceFactory.getProperty("kq.date.appeal.except.beLate"), 70, "", "A", 0, 0, "");
			  childColumnsInfo = getColumnsInfo("Onduty_be_late_card", ResourceFactory.getProperty("kq.date.appeal.except.startTime"), 70, "0", "A", 100, 0, "");
			  columnsInfo.addChildColumn(childColumnsInfo);
			  columnList.add(columnsInfo);
			  columnsInfo = getColumnsInfo("Offduty_leave_early",  ResourceFactory.getProperty("kq.date.appeal.except.leaveEarly"), 70, "", "A", 0, 0, "");
			  childColumnsInfo = getColumnsInfo("Offduty_leave_early_card", ResourceFactory.getProperty("kq.date.appeal.except.endTime"), 70, "0", "A", 100, 0, "");
			  columnsInfo.addChildColumn(childColumnsInfo);
			  columnList.add(columnsInfo);
			  //请假信息
			  String leaveFieldsetid=privForHospitalUtil.getLeave_setid();
		  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
		  			 columnsInfo = getColumnsInfo(leaveFieldsetid,  ResourceFactory.getProperty("kq.date.appeal.except.rest"),400, "", "A", 0, 0, "");
		  			FieldItem fi =null;
		  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_type())) {
		  				  fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_type(),  leaveFieldsetid);
		  				  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),70, fi.getCodesetid(), fi.getItemtype(), 100, 0, leaveFieldsetid);
		  				  columnsInfo.addChildColumn(childColumnsInfo);
					}
		  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_reason())) {
		  				 fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_reason(),  leaveFieldsetid);
		  				  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),100, fi.getCodesetid(), fi.getItemtype(), 100, 0, leaveFieldsetid);
		  				  columnsInfo.addChildColumn(childColumnsInfo);
		  			}
		  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_start())) {
		  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_start(),  leaveFieldsetid);
		  			  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, leaveFieldsetid);
		  			  columnsInfo.addChildColumn(childColumnsInfo);
		  			}
		  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_end())) {
		  			  fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_end(),  leaveFieldsetid);
		  			  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, leaveFieldsetid);
		  			  columnsInfo.addChildColumn(childColumnsInfo);
		  			}
		  		
		  			columnList.add(columnsInfo);
				}
			 
			 
			 
			 
			  //公出情况
		  	String  officeLeaveFieldsetid=privForHospitalUtil.getOfficeleave_setid();
			if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
				columnsInfo = getColumnsInfo(officeLeaveFieldsetid,  ResourceFactory.getProperty("kq.date.appeal.except.officeleave"),400, "", "A", 0, 0, "");
				FieldItem fi =null;		
				if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_type())) {
					  fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_type(),  officeLeaveFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),70, fi.getCodesetid(), fi.getItemtype(), 100, 0, officeLeaveFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_reason())) {
		  		  fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_reason(),  officeLeaveFieldsetid);
				  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),100, fi.getCodesetid(), fi.getItemtype(), 100, 0, officeLeaveFieldsetid);
				  columnsInfo.addChildColumn(childColumnsInfo);
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_start())) {
		  			 fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_start(),  officeLeaveFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, officeLeaveFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_end())) {
		  			 fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_end(),  officeLeaveFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, officeLeaveFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
					  columnList.add(columnsInfo);
				}
			}
			  //加班情况
			String OvertimeFieldsetid=privForHospitalUtil.getOvertime_setid();
	  		if (StringUtils.isNotBlank(OvertimeFieldsetid)) {
				columnsInfo = getColumnsInfo(OvertimeFieldsetid,  ResourceFactory.getProperty("kq.date.appeal.except.overtime"),400, "", "A", 0, 0, "");
				FieldItem fi =null;		
				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_type())) {
					  fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_type(),  OvertimeFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),70, fi.getCodesetid(), fi.getItemtype(), 100, 0, OvertimeFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
				}
				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_reason())) {
		  		  fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_reason(),  OvertimeFieldsetid);
				  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),100, fi.getCodesetid(), fi.getItemtype(), 100, 0, OvertimeFieldsetid);
				  columnsInfo.addChildColumn(childColumnsInfo);
				}
				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_start())) {
		  			 fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_start(),  OvertimeFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, OvertimeFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
				}
				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_end())) {
		  			 fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_end(),  OvertimeFieldsetid);
					  childColumnsInfo = getColumnsInfo(fi.getItemid(), fi.getItemdesc(),150, fi.getCodesetid(), fi.getItemtype(), 100, 0, OvertimeFieldsetid);
					  columnsInfo.addChildColumn(childColumnsInfo);
					  columnList.add(columnsInfo);
				}
			}
			
			
			 
			 
		  } catch (Exception e) {
			
		}
		  return columnList;
	}
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String codesetId,
	        String columnType, int columnLength, int decimalWidth, String fieldsetid) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setFieldsetid(fieldsetid);
		 columnsInfo.setReadOnly(true);// 是否只读
		if("A".equals(columnType)||
				"M".equals(columnType)||
				"D".equals(columnType)) {
				columnsInfo.setTextAlign("left");
			}else if("N".equals(columnType)){
				columnsInfo.setTextAlign("right");
				columnsInfo.setFilterable(true);
			}

		return columnsInfo;
	}
	
    /**
     * 出勤异常-导出出勤异常表
     * exportKqExceptTable
     * @param jsonObj
     * @return
     * @throws GeneralException
     * @date 2019年08月27日 下午2:00:58
     * @author xuanz
     */
	@Override
    public String exportKqExceptTable(JSONObject jsonObj) throws GeneralException {
		String fileName =this.userView.getUserName()+ "_"+"kqExcept_" +jsonObj.getString("name")+ ".xls";
		String guidkey =jsonObj.getString("guidkey");
    	String startDate =jsonObj.getString("startDate");
    	String endDate = jsonObj.getString("endDate");
		try {
			//获取出勤异常sql
			String sql=this.getKqExceptSql(guidkey, startDate, endDate);
			//获取出勤异常数据，从请假公出子集查出信息筛选
			ArrayList<LazyDynaBean> dataList= this.checkListData(guidkey, startDate, endDate, this.listKqExceptData(sql));
			//获取出勤异常列表表头
			ArrayList<ColumnsInfo> columnList=this.getKqExceptColumns();
			//获取工作分析表列头 
			ArrayList<LazyDynaBean> headList = this.getKqExceptHeadList(columnList);
			//获取导出数据HashMap
			HashMap dataMap = getOutExcelList(checkNormalData(dataList), columnList);
			ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("dataList");
			//获取合并单元格list
			ArrayList mergeCellList = getMergedCellList(columnList, dataMap);
			// 实例化导出Excel工具类
			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
			excelUtil.setHeadRowHeight((short)550);
			//导出到excel
			excelUtil.setRowHeight((short)300);
			excelUtil.setConvertToZero(false);
			excelUtil.exportExcel(fileName, "出勤异常表-"+jsonObj.getString("name"),mergeCellList, headList, dealedExcelList, null, 2);//导出表格
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}
    /**
     * 获取工作分析表列头
     * getHeadList
     * @return
     * @date 2018年12月13日 下午2:10:58
     * @author linbz
     */
    private ArrayList<LazyDynaBean> getKqExceptHeadList(ArrayList<ColumnsInfo> columnList) {
    	HashMap map = new HashMap();
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean =null;
		int cellIndex = 0;
		for(int i = 0 ;i<columnList.size();i++){
			ColumnsInfo col = columnList.get(i);
			String colId = col.getColumnId();
			//去掉guidkey列
			if("guidkey".equals(colId))
				continue;
			int loadType = col.getLoadtype();
			bean = new LazyDynaBean();
			bean.set("content", col.getColumnDesc());
			if(loadType!=ColumnsInfo.LOADTYPE_BLOCK )
				bean.set("columnHidden", true);
			bean.set("codesetid", col.getCodesetId());
			if("confirm".equals(colId)){
				bean.set("colType", "A");
			}else{
				bean.set("colType", col.getColumnType());
			}
			bean.set("decwidth", col.getDecimalWidth()+"");
			ArrayList<ColumnsInfo> childs = col.getChildColumns();
			//判断是否为考勤数据列
			if(childs.size()>0){
				for(int m = 0;m<childs.size();m++){
					ColumnsInfo column = childs.get(m);
					int loadType_ = column.getLoadtype();
					LazyDynaBean cbean = new LazyDynaBean();
					cbean.set("itemid", column.getColumnId());
					cbean.set("content", column.getColumnDesc());
					cbean.set("colType", column.getColumnType());
					cbean.set("codesetid", column.getCodesetId());
					cbean.set("decwidth",column.getDecimalWidth()+"");
					cbean.set("fromRowNum", 2);
					cbean.set("toRowNum", 2);
					cbean.set("fromColNum", cellIndex);
					cbean.set("toColNum", cellIndex);
                    cbean.set("columnWidth",column.getColumnWidth()*38);
					if(loadType_!=ColumnsInfo.LOADTYPE_BLOCK ) {
						cbean.set("columnHidden", true);
					}
					list.add(cbean);
					cellIndex++;
				}
				continue;
			}
			else{
				bean.set("itemid", col.getColumnId());
                bean.set("fromRowNum", 1);
                bean.set("toRowNum", 2);
                bean.set("fromColNum", cellIndex);
                bean.set("toColNum", cellIndex);
                bean.set("columnWidth",col.getColumnWidth()*38);
                list.add(bean);
                cellIndex++;
			}
		}
		map.put("list", list);
		return list;
        }
	/**
	 * 获取单个数据列的表格项
	 * @param columuName
	 * @param data
	 * @param bean 
	 * @param i 
	 * @return LazyDynaBean
	 */
	private LazyDynaBean setDataColumn(String columuName, String data, LazyDynaBean bean, int i) {
		LazyDynaBean dataBean = new LazyDynaBean();
       	dataBean = new LazyDynaBean();  
       	dataBean.set("content", data);
       	dataBean.set("fromColNum", i);
       	bean.set(columuName, dataBean);
		return bean;
	}
	/**
	 * 获取合并单元格list
	 * @param columnsInfo
	 * @param dataMap
	 * @return
	 */
	private ArrayList<LazyDynaBean> getMergedCellList(ArrayList columnsInfo, HashMap dataMap) {
    	ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
    	//表头合并
		int colNum = 0;
		for (int i = 0; i < columnsInfo.size(); i++) {
			ColumnsInfo columnInfo = (ColumnsInfo)columnsInfo.get(i);
			ArrayList childColumnList = (ArrayList)columnInfo.getChildColumns();
			boolean columnHidden = true;
			for(int j=0;j<childColumnList.size();j++) {
				ColumnsInfo cc = (ColumnsInfo)childColumnList.get(j);
				if(cc.getLoadtype() == ColumnsInfo.LOADTYPE_BLOCK) {
					columnHidden = false;
					break;
				}
			}
            if(childColumnList.size() > 0){//子列头大于0，则是复合列头
                ColumnsInfo cc = (ColumnsInfo)childColumnList.get(0);
                String itemid = cc.getColumnId();
                String ccDesc = cc.getColumnDesc();
				LazyDynaBean ldbean = new LazyDynaBean();
				ldbean.set("content", columnInfo.getColumnDesc());// 列头名称
				ldbean.set("columnHidden", columnHidden);// 列头名称
				ldbean.set("colType", columnInfo.getColumnType());
				ldbean.set("fromRowNum", 1);// 合并单元格从那行开始
				ldbean.set("toRowNum", 1);// 合并单元格到哪行结束
				ldbean.set("fromColNum", colNum);// 合并单元格从哪列开始
				ldbean.set("toColNum", colNum + childColumnList.size()-1);// 合并单元格从哪列结束
				mergedCellList.add(ldbean);
				colNum += childColumnList.size();//定位下次初始列
				continue;
			}else {
				colNum += 1;//定位下次初始列
				continue;
			}
		}

		//Excel标题内容
        HashMap titleStyleMap = new HashMap();
		titleStyleMap.put("border",(short)1);
		titleStyleMap.put("fontSize",20);
		titleStyleMap.put("isFontBold",true);
        LazyDynaBean titleBean = new LazyDynaBean();
        String title = "出勤异常表";
        titleBean.set("content", title);// 列头名称
        titleBean.set("fromRowNum", 0);// 合并单元格从那行开始
        titleBean.set("toRowNum", 0);// 合并单元格到哪行结束
        titleBean.set("fromColNum", 0);// 合并单元格从哪列开始
        titleBean.set("toColNum", colNum-1);// 合并单元格从哪列结束
        titleBean.set("mergedCellStyleMap", titleStyleMap);// 合并单元格从哪列结束
        mergedCellList.add(0,titleBean);
		
		return mergedCellList;
    }
	/**
	 * 获取出勤异常数据
	 * @param sql
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public ArrayList<LazyDynaBean> listKqExceptData(String sql)throws GeneralException {
		ContentDAO dao = new ContentDAO(this.conn);
		ArrayList<LazyDynaBean> allDataList = new ArrayList<LazyDynaBean>();
        RowSet rs = null;
        try {
			rs = dao.search(sql);
			while (rs.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				StringBuffer Onduty_absent_card=new StringBuffer();
				StringBuffer Offduty_absent_card=new StringBuffer();
				StringBuffer Onduty_be_late_card=new StringBuffer();
				StringBuffer Offduty_leave_early_card=new StringBuffer();
				//班次1
				if (rs.getInt("ONDUTY_BE_LATE_1")==1) {
					Onduty_be_late_card.append(rs.getString("ONDUTY_CARD_1")==null?"":rs.getString("ONDUTY_CARD_1")+",");
				}
				if (rs.getInt("ONDUTY_ABSENT_1")==1) {
					Onduty_absent_card.append(rs.getString("ONDUTY_CARD_1")==null?"":rs.getString("ONDUTY_CARD_1")+",");
				}
				if (rs.getInt("OFFDUTY_LEAVE_EARLY_1")==1) {
					Offduty_leave_early_card.append(rs.getString("OFFDUTY_CARD_1")==null?"":rs.getString("OFFDUTY_CARD_1")+",");
				}
				if (rs.getInt("OFFDUTY_ABSENT_1")==1) {
					Offduty_absent_card.append(rs.getString("OFFDUTY_CARD_1")==null?"":rs.getString("OFFDUTY_CARD_1")+",");
				}
				//班次2
				if (rs.getInt("ONDUTY_BE_LATE_2")==1) {
					Onduty_be_late_card.append(rs.getString("ONDUTY_CARD_2")==null?"":rs.getString("ONDUTY_CARD_2")+",");
				}
				if (rs.getInt("ONDUTY_ABSENT_2")==1) {
					Onduty_absent_card.append(rs.getString("ONDUTY_CARD_2")==null?"":rs.getString("ONDUTY_CARD_2")+",");
				}
				if (rs.getInt("OFFDUTY_LEAVE_EARLY_2")==1) {
					Offduty_absent_card.append(rs.getString("OFFDUTY_CARD_2")==null?"":rs.getString("OFFDUTY_CARD_2")+",");
				}
				if (rs.getInt("OFFDUTY_ABSENT_2")==1) {
					Offduty_leave_early_card.append(rs.getString("OFFDUTY_CARD_2")==null?"":rs.getString("OFFDUTY_CARD_2")+",");
				}
				//班次3
				if (rs.getInt("ONDUTY_BE_LATE_3")==1) {
					Onduty_be_late_card.append(rs.getString("ONDUTY_CARD_3")==null?"":rs.getString("ONDUTY_CARD_3"));
				}
				if (rs.getInt("ONDUTY_ABSENT_3")==1) {
					Onduty_absent_card.append(rs.getString("ONDUTY_CARD_3")==null?"":rs.getString("ONDUTY_CARD_3"));
				}
				if (rs.getInt("OFFDUTY_LEAVE_EARLY_3")==1) {
					Offduty_absent_card.append(rs.getString("OFFDUTY_CARD_3")==null?"":rs.getString("OFFDUTY_CARD_3"));
				}
				if (rs.getInt("OFFDUTY_ABSENT_3")==1) {
					Offduty_leave_early_card.append(rs.getString("OFFDUTY_CARD_3")==null?"":rs.getString("OFFDUTY_CARD_3"));
				}
				bean.set("onduty_be_late_card", Onduty_be_late_card.toString().replaceAll(",$", ""));
				bean.set("onduty_absent_card", Onduty_absent_card.toString().replaceAll(",$", ""));
				bean.set("offduty_absent_card", Offduty_absent_card.toString().replaceAll(",$", ""));
				bean.set("offduty_leave_early_card", Offduty_leave_early_card.toString().replaceAll(",$", ""));
				bean.set("xuhao", rs.getInt("xuhao"));
				bean.set("kq_date", rs.getString("kq_date"));
				bean.set("kq_status", rs.getString("Kq_status")==null?" ": rs.getString("Kq_status"));
				bean.set("card_data", rs.getString("Card_data")==null?" ":StringUtils.strip(rs.getString("Card_data"), ",") );
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 //请假信息
				 KqPrivForHospitalUtil privForHospitalUtil=new KqPrivForHospitalUtil(userView, conn);
				//获取请假子集指标
			  		String leaveFieldsetid=privForHospitalUtil.getLeave_setid();
			  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
			  			FieldItem fi =null;
			  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_type())) {
			  				 fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_type(),  leaveFieldsetid);
			  				bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
						}
			  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_reason())) {
			  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_reason(),  leaveFieldsetid);
			  				bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
			  			}
			  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_start())) {
			  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_start(),  leaveFieldsetid);
			  				bean.set(fi.getItemid(), rs.getTimestamp(fi.getItemid())== null? " " :sdf.format(rs.getTimestamp(fi.getItemid())));
			  			}
			  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_end())) {
			  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_end(),  leaveFieldsetid);
			  				bean.set(fi.getItemid(), rs.getTimestamp(fi.getItemid())== null? " " :sdf.format(rs.getTimestamp(fi.getItemid())));
			  			}
			  		
					}
			  		  ////获取公出子集指标
			  		String officeLeaveFieldsetid=privForHospitalUtil.getOfficeleave_setid();
			  		if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
			  			FieldItem fi =null;
			  			if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_type())) {
			  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_type(),  officeLeaveFieldsetid);
			  				bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
						}
				  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_reason())) {
				  			fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_reason(),  officeLeaveFieldsetid);
				  			bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
						}
				  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_start())) {
				  			fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_start(),  officeLeaveFieldsetid);
				  			bean.set(fi.getItemid(), rs.getTimestamp(fi.getItemid())== null? " " : sdf.format(rs.getTimestamp(fi.getItemid())));
						}
				  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_end())) {
				  			fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_end(),  officeLeaveFieldsetid);
				  			bean.set(fi.getItemid(), rs.getTimestamp(fi.getItemid())== null? " " : sdf.format(rs.getTimestamp(fi.getItemid())));
						}
			  		}
			  		allDataList.add(bean);
			}
		} catch (Exception e) {
			 e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
		return allDataList;
	}
	/**
	 * 从请假公出子集查出信息
	 * @param guidkey
	 * @param startDate
	 * @param endDate
	 * @param allDataList
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList<LazyDynaBean> checkListData(String guidkey, String startDate, String endDate,ArrayList<LazyDynaBean> allDataList) throws GeneralException{
		ContentDAO dao = new ContentDAO(this.conn);
		KqPrivForHospitalUtil privForHospitalUtil = new KqPrivForHospitalUtil(userView, conn);
		startDate=startDate.replace(".", "-");
		endDate=endDate.replace(".", "-");
		String leaveFieldsetid = null;
		String officeLeaveFieldsetid = null;
		String OvertimeFieldsetid = null;
		StringBuffer sql = new StringBuffer();
		// 56723 去全部考勤人员库
		String nbases = privForHospitalUtil.getKqNbases();
		String[] dbnameList = StringUtils.split(nbases, ",");
        for(int i = 0; i < dbnameList.length; i++){
            String dbname = dbnameList[i];
            if(StringUtils.isBlank(dbname)) {
            	continue;
            }
	        if(i > 0)
	        	sql.append(" UNION ALL ");
			sql.append("select a01.a0100");
			//获取请假子集指标
	  		leaveFieldsetid=privForHospitalUtil.getLeave_setid();
	  		FieldItem LeaveStartFieldItem=null;
	  		FieldItem LeaveEndFieldItem=null;
	  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
	  			FieldItem fi =null;
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_type())) {
	  				 fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_type(),  leaveFieldsetid);
	  				sql.append(","+leaveFieldsetid+"."+fi.getItemid());
				}
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_reason())) {
	  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_reason(),  leaveFieldsetid);
	  				sql.append(","+leaveFieldsetid+"."+fi.getItemid());
	  			}
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_start())) {
	  				LeaveStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_start(),  leaveFieldsetid);
		  			sql.append(","+leaveFieldsetid+"."+LeaveStartFieldItem.getItemid());
	  			}
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_end())) {
	  				LeaveEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_end(),  leaveFieldsetid);
		  			sql.append(","+leaveFieldsetid+"."+LeaveEndFieldItem.getItemid());
	  			}
	  		
			}
	  		 //获取公出子集指标
	  		officeLeaveFieldsetid=privForHospitalUtil.getOfficeleave_setid();
	  		FieldItem OfficeleaveStartFieldItem=null;
	  		FieldItem OfficeleaveEndFieldItem=null;
	  		if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
	  			FieldItem fi =null;
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_type())) {
	  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_type(),  officeLeaveFieldsetid);
	  				sql.append(","+officeLeaveFieldsetid+"."+fi.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_reason())) {
		  			fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_reason(),  officeLeaveFieldsetid);
		  			sql.append(","+officeLeaveFieldsetid+"."+fi.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_start())) {
		  			OfficeleaveStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_start(),  officeLeaveFieldsetid);
		  			sql.append(","+officeLeaveFieldsetid+"."+OfficeleaveStartFieldItem.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_end())) {
		  			OfficeleaveEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_end(),  officeLeaveFieldsetid);
		  			sql.append(","+officeLeaveFieldsetid+"."+OfficeleaveEndFieldItem.getItemid());
				}
	  		} 
	  	//获取加班子集指标
	  		OvertimeFieldsetid=privForHospitalUtil.getOvertime_setid();
	  		FieldItem OvertimeStartFieldItem=null;
	  		FieldItem OvertimeEndFieldItem=null;
	  		if (StringUtils.isNotBlank(OvertimeFieldsetid)) {
	  			FieldItem fi =null;
	  			if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_type())) {
	  				fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_type(),  OvertimeFieldsetid);
	  				sql.append(","+OvertimeFieldsetid+"."+fi.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_reason())) {
		  			fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_reason(),  OvertimeFieldsetid);
		  			sql.append(","+OvertimeFieldsetid+"."+fi.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_start())) {
		  			OvertimeStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_start(),  OvertimeFieldsetid);
		  			sql.append(","+OvertimeFieldsetid+"."+OvertimeStartFieldItem.getItemid());
				}
		  		if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_end())) {
		  			OvertimeEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_end(),  OvertimeFieldsetid);
		  			sql.append(","+OvertimeFieldsetid+"."+OvertimeEndFieldItem.getItemid());
				}
	  		} 
	  		sql.append(" from "+ dbname +"A01 a01");
	  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
	            sql.append(" left join " + dbname+leaveFieldsetid+" "+leaveFieldsetid+" on a01.A0100="+leaveFieldsetid+".A0100 ");
	            sql.append(" and ( "+Sql_switcher.dateToChar(leaveFieldsetid+"."+LeaveStartFieldItem.getItemid(), "yyyy-MM-dd")+"  between '"+startDate+"' and '"+endDate+"'");
	            sql.append(" or "+Sql_switcher.dateToChar(leaveFieldsetid+"."+LeaveEndFieldItem.getItemid(), "yyyy-MM-dd")+" between '"+startDate+"' and '"+endDate+"'");
	            sql.append(" or("+Sql_switcher.dateToChar(leaveFieldsetid+"."+LeaveStartFieldItem.getItemid(), "yyyy-MM-dd") +"< '"+startDate+"' and "+Sql_switcher.dateToChar(leaveFieldsetid+"."+LeaveEndFieldItem.getItemid(), "yyyy-MM-dd")+"> '"+endDate+"'))");
	  		}
	  		if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
	            sql.append(" left join " + dbname+officeLeaveFieldsetid+" "+officeLeaveFieldsetid+" on a01.A0100="+officeLeaveFieldsetid+".A0100 ");
	            sql.append(" and ( "+Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+OfficeleaveStartFieldItem.getItemid(), "yyyy-MM-dd")+"  between '"+startDate+"' and '"+endDate+"'");
	            sql.append(" or "+Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+OfficeleaveEndFieldItem.getItemid(), "yyyy-MM-dd")+" between '"+startDate+"' and '"+endDate+"'");
	            sql.append(" or("+Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+OfficeleaveStartFieldItem.getItemid(), "yyyy-MM-dd") +"< '"+startDate+"' and "+Sql_switcher.dateToChar(officeLeaveFieldsetid+"."+OfficeleaveEndFieldItem.getItemid(), "yyyy-MM-dd")+"> '"+endDate+"'))");
	  		}
	  		if (StringUtils.isNotBlank(OvertimeFieldsetid)) {
	            sql.append(" left join " + dbname+OvertimeFieldsetid+" "+OvertimeFieldsetid+" on a01.A0100="+OvertimeFieldsetid+".A0100 and ( "+Sql_switcher.dateToChar(OvertimeFieldsetid+"."+OvertimeStartFieldItem.getItemid(), "yyyy-MM-dd")+"  between '"+startDate+"' and '"+endDate+"' or "+Sql_switcher.dateToChar(OvertimeFieldsetid+"."+OvertimeEndFieldItem.getItemid(), "yyyy-MM-dd")+" between '"+startDate+"' and '"+endDate+"')");
			}
	  		sql.append(" where  a01.guidkey='"+ guidkey+"'  ");
	     }
	     RowSet rs = null;
	     ArrayList<LazyDynaBean> allList = new ArrayList<LazyDynaBean>();
	     ArrayList<String> dateList = getDateList(startDate, endDate);
	        try {
	        	for (String dateString : dateList) {
					rs = dao.search(sql.toString());
					while (rs.next()) {
						LazyDynaBean bean=new LazyDynaBean();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						boolean LeaveFlag=false;
						boolean OfficeleaveFlag=false;
						boolean overTimeFlag=false;
						FieldItem fi =null;
				  		if (StringUtils.isNotBlank(leaveFieldsetid)) {
				  			FieldItem leaveStartFieldItem =null;
				  			FieldItem leaveEndFieldItem =null;
				  			String leaveStartDate="";
				  			String leaveEndDate="";
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_start())) {
				  				leaveStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_start(),  leaveFieldsetid);
				  				leaveStartDate=rs.getTimestamp(leaveStartFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(leaveStartFieldItem.getItemid()));
//				  				bean.set(fi.getItemid(),leaveStartDate);
				  			}
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_end())) {
				  				leaveEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_end(),  leaveFieldsetid);
				  				leaveEndDate=rs.getTimestamp(leaveEndFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(leaveEndFieldItem.getItemid()));
//				  				bean.set(fi.getItemid(),leaveEndDate);
				  			}
				  			if (StringUtils.isNotBlank(leaveStartDate)&&StringUtils.isNotBlank(leaveEndDate)&&dateString.compareTo(leaveStartDate.substring(0,10))>=0&&dateString.compareTo(leaveEndDate.substring(0,10))<=0) {
				  				LeaveFlag=true;
				  				bean.set(leaveStartFieldItem.getItemid(),leaveStartDate);
				  				bean.set(leaveEndFieldItem.getItemid(),leaveEndDate);
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_type())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_type(),  leaveFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getLeave_reason())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getLeave_reason(),  leaveFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
							}
				  		
						}
				  		if (StringUtils.isNotBlank(officeLeaveFieldsetid)) {
				  			FieldItem OfficeleaveStartFieldItem =null;
				  			FieldItem OfficeleaveEndFieldItem =null;
				  			String OfficeleaveStartDate="";
				  			String OfficeleaveEndDate="";
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_start())) {
				  				OfficeleaveStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_start(),  officeLeaveFieldsetid);
				  				OfficeleaveStartDate=rs.getTimestamp(OfficeleaveStartFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(OfficeleaveStartFieldItem.getItemid()));
				  			}
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_end())) {
				  				OfficeleaveEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_end(),  officeLeaveFieldsetid);
				  				OfficeleaveEndDate=rs.getTimestamp(OfficeleaveEndFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(OfficeleaveEndFieldItem.getItemid()));
				  			}
				  			if (StringUtils.isNotBlank(OfficeleaveStartDate)&&StringUtils.isNotBlank(OfficeleaveEndDate)&&dateString.compareTo(OfficeleaveStartDate.substring(0,10))>=0&&dateString.compareTo(OfficeleaveEndDate.substring(0,10))<=0) {
				  				OfficeleaveFlag=true;
				  				bean.set(OfficeleaveStartFieldItem.getItemid(),OfficeleaveStartDate);
				  				bean.set(OfficeleaveEndFieldItem.getItemid(),OfficeleaveEndDate);
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_type())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_type(),  officeLeaveFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getOfficeleave_reason())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getOfficeleave_reason(),  officeLeaveFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
				  			}
				  		}
				  		if (StringUtils.isNotBlank(OvertimeFieldsetid)) {
				  			FieldItem OvertimeStartFieldItem =null;
				  			FieldItem OvertimeEndFieldItem =null;
				  			String OvertimeStartDate="";
				  			String OvertimeEndDate="";
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_start())) {
				  				OvertimeStartFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_start(),  OvertimeFieldsetid);
				  				OvertimeStartDate=rs.getTimestamp(OvertimeStartFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(OvertimeStartFieldItem.getItemid()));
//				  				bean.set(fi.getItemid(),leaveStartDate);
				  			}
				  			if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_end())) {
				  				OvertimeEndFieldItem =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_end(),  OvertimeFieldsetid);
				  				OvertimeEndDate=rs.getTimestamp(OvertimeEndFieldItem.getItemid())== null? " " :sdf.format(rs.getTimestamp(OvertimeEndFieldItem.getItemid()));
//				  				bean.set(fi.getItemid(),leaveEndDate);
				  			}
				  			if (StringUtils.isNotBlank(OvertimeStartDate)&&StringUtils.isNotBlank(OvertimeEndDate)&&dateString.compareTo(OvertimeStartDate.substring(0,10))>=0&&dateString.compareTo(OvertimeEndDate.substring(0,10))<=0) {
				  				overTimeFlag=true;
				  				bean.set(OvertimeStartFieldItem.getItemid(),OvertimeStartDate);
				  				bean.set(OvertimeEndFieldItem.getItemid(),OvertimeEndDate);
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_type())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_type(),  OvertimeFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
				  				if (StringUtils.isNotBlank(privForHospitalUtil.getOvertime_reason())) {
				  					fi =DataDictionary.getFieldItem(privForHospitalUtil.getOvertime_reason(),  OvertimeFieldsetid);
				  					bean.set(fi.getItemid(), rs.getString(fi.getItemid())== null? " " : rs.getString(fi.getItemid()));
				  				}
							}
				  		
						}
				  		if (LeaveFlag||OfficeleaveFlag||overTimeFlag) {
				  			bean.set("kq_date", dateString);
				  			allList.add(bean);
				  			dateString="";
						}
					}
				}
	        } catch (Exception e) {
				 e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        } finally {
	            PubFunc.closeDbObj(rs);
	        }
	
	    ArrayList<LazyDynaBean> newDataList=new ArrayList<LazyDynaBean>();
	    //把查出来的请假公出信息加入异常数据list
	    if (allDataList.size()<1) {
			return allList;	
		}else if(allList.size()<1){
			return allDataList;
		}else {
		    for (LazyDynaBean bean : allDataList) {
		    	String dateString1=(String)bean.get("kq_date");
		    	for (int i = 0; i < allList.size(); i++) {
					LazyDynaBean lazyDynaBean = allList.get(i);
					String dateString2=(String)lazyDynaBean.get("kq_date");
					if (dateString2.compareTo(dateString1)<0) {
						//不包含就添加
						 if(!newDataList.contains(lazyDynaBean)){                
							 newDataList.add(lazyDynaBean);
						 }
						 if ((i==allList.size()-1)&&!newDataList.contains(bean)) {
								 newDataList.add(bean);
						 }
					}
					//不包含就添加
					if (dateString2.compareTo(dateString1)>0&&!newDataList.contains(bean)) {
						newDataList.add(bean);
					}
					if (dateString2.compareTo(dateString1)==0) {
						lazyDynaBean.set("onduty_be_late_card", bean.get("onduty_be_late_card"));
						lazyDynaBean.set("onduty_absent_card", bean.get("onduty_absent_card"));
						lazyDynaBean.set("offduty_absent_card", bean.get("offduty_absent_card"));
						lazyDynaBean.set("offduty_leave_early_card", bean.get("offduty_leave_early_card"));
						lazyDynaBean.set("kq_status", bean.get("kq_status"));
						lazyDynaBean.set("card_data", bean.get("card_data"));
						//不包含就添加
						if(!newDataList.contains(lazyDynaBean)){                
							newDataList.add(lazyDynaBean);
				        }
						break;
					}
					
				}
			}
		}   
		return newDataList;
	}
	private ArrayList<String> getDateList(String startDate,String endDate){
		ArrayList<String> dateList=new ArrayList<String>();
		dateList.add(startDate);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateString=startDate;
		Date date;
		boolean flag=true;
		try {
			while(flag) {
				date = format.parse(dateString);
				Date date2=DateUtils.addDays(date, 1);
				dateString =format.format(date2);
				if (dateString.compareTo(endDate)<0) {
					dateList.add(dateString);
				}else {
					flag=false;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		dateList.add(endDate);
		return dateList;
	}
	//去除节假日公休日请假数据
	private ArrayList<LazyDynaBean> checkNormalData(ArrayList<LazyDynaBean> dataList){
		ArrayList<LazyDynaBean> newDataList= new ArrayList<LazyDynaBean>();
		int number=1;
		for (LazyDynaBean bean : dataList) {
			String status=(String) bean.get("kq_status");
			if (StringUtils.isBlank(status)) {
				bean.set("xuhao", number);
				newDataList.add(bean);
				number=number+1;
				continue;
			}
			if (!ResourceFactory.getProperty("kq.date.appeal.except.normal").equals(status)&&!ResourceFactory.getProperty("kq.date.appeal.except.reststatus").equals(status)&&!ResourceFactory.getProperty("kq.date.appeal.except.weekend").equals(status)&&!ResourceFactory.getProperty("kq.date.appeal.except.holiday").equals(status)) {
				bean.set("xuhao", number);
				newDataList.add(bean);
				number=number+1;
			}
		}
		return  newDataList;
	}
	 /**
     * 获取导出数据HashMap
     * @param dataList
     * @param columnList
     * @return
     */
    private HashMap getOutExcelList(ArrayList<LazyDynaBean> dataList, ArrayList<ColumnsInfo> columnList)  {
		HashMap returnMap = new HashMap();
		ArrayList<LazyDynaBean> newDataList=new ArrayList<LazyDynaBean>();
		HashMap<Integer, Integer> rowMap=new HashMap<Integer, Integer>();
		try {
			int index = 0;
			for (LazyDynaBean bean : dataList) {
				int colLen = 1;
				int colIndex = 0;
				LazyDynaBean newBean = new LazyDynaBean(); 
				for(int i = 0 ;i<columnList.size();i++){
					ColumnsInfo col = columnList.get(i);
					String data = "";
					String value = "";
					ArrayList<ColumnsInfo> childColumns = col.getChildColumns();
					String item ="";
					if(childColumns.size()>0){
						ColumnsInfo cc;
						for(int k = 0;k<childColumns.size();k++){
							cc = childColumns.get(k);
							item = cc.getColumnId();
							value = bean.get(item)==null?"":bean.get(item).toString();
							 if("A".equals(cc.getColumnType())){
			                     if(!"0".equals(cc.getCodesetId())){
			                         if("UN".equals(cc.getCodesetId())||"UM".equals(cc.getCodesetId())||"@K".equals(cc.getCodesetId())){
			                             if(!"".equals(AdminCode.getCodeName("UN",value)))
			                                 value=AdminCode.getCodeName("UN",value);
			                             else if(!"".equals(AdminCode.getCodeName("UM",value)))
			                                 value=AdminCode.getCodeName("UM",value);
			                             else
			                                 value=AdminCode.getCodeName("@K",value);
			                         }else
			                             value=AdminCode.getCodeName(cc.getCodesetId(),value);
			                     }
			                 }
							 data = KqDataUtil.nullif(value);
							 newBean = setDataColumn(item,data,newBean,colIndex);
			                    colIndex++;
							}
						continue;
					}else{
						item = col.getColumnId();
					}
					value = bean.get(item)==null?"":bean.get(item).toString();
					if("A".equals(col.getColumnType())){
			            if(!"0".equals(col.getCodesetId())){
			                if("UN".equals(col.getCodesetId())||"UM".equals(col.getCodesetId())||"@K".equals(col.getCodesetId())){
			                    if(!"".equals(AdminCode.getCodeName("UN",value)))
			                        value=AdminCode.getCodeName("UN",value);
			                    else if(!"".equals(AdminCode.getCodeName("UM",value)))
			                        value=AdminCode.getCodeName("UM",value);
			                    else
			                        value=AdminCode.getCodeName("@K",value);
			                }else
			                    value=AdminCode.getCodeName(col.getCodesetId(),value);
			            }
			        }
					data = KqDataUtil.nullif(value);
					newBean = setDataColumn(item,data,newBean,colIndex);
			        colIndex++;
				}
				rowMap.put(index, colLen);
				index++;
				newDataList.add(newBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("rowMap", rowMap);
		returnMap.put("dataList", newDataList);
		return returnMap;	
		}
}
