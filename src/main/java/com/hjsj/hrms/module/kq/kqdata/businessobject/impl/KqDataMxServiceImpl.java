package com.hjsj.hrms.module.kq.kqdata.businessobject.impl;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.item.businessobject.KqItemService;
import com.hjsj.hrms.module.kq.config.item.businessobject.impl.KqItemServiceImpl;
import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.config.shifts.businessobject.ShiftsService;
import com.hjsj.hrms.module.kq.config.shifts.businessobject.impl.ShiftsServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataAppealMainService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.kq.util.KqUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

/**
 *  考勤明细业务实现类
 * @author haosl
 *
 */
public class KqDataMxServiceImpl implements KqDataMxService {
	private UserView userView;
	private Connection conn;
	private String exceptFields = "";
	
	public String getExceptFields() {
		return exceptFields;
	}

	public void setExceptFields(String exceptFields) {
		this.exceptFields = exceptFields;
	}

	public KqDataMxServiceImpl(UserView userView, Connection frameconn) {
		this.userView = userView;
		this.conn = frameconn;
	}

	@Override
    public ArrayList<LazyDynaBean> listQ35(String sqlWhere, ArrayList parameterList, String sqlSort) throws GeneralException {
	    ContentDAO dao = new ContentDAO(this.conn);
        ArrayList<LazyDynaBean> dataList = new ArrayList<LazyDynaBean>();
        StringBuffer strSql = new StringBuffer();
        RowSet rs = null;
        ResultSetMetaData rsetmd = null;
        try {
            strSql.append("SELECT * FROM Q35 where 1=1 ");
            if (sqlWhere != null) {
                strSql.append(sqlWhere);
            }
            if (StringUtils.isNotBlank(sqlSort)) {
                strSql.append(" ORDER BY ").append(sqlSort);
            } else {
                strSql.append(" ORDER BY Kq_year,Kq_duration ");
            }
            ArrayList pList = new ArrayList();
            if (parameterList != null) {
                pList.addAll(parameterList);
            }
            rs = dao.search(strSql.toString(), pList);
            rsetmd = rs.getMetaData();
            int cols = rsetmd.getColumnCount();
            ArrayList<FieldItem> fieldItemlist = DataDictionary.getFieldList("Q35", 1);
            while(rs.next()) {
              LazyDynaBean bean = new LazyDynaBean();
              for(int i = 1; i <= cols; ++i) {
                 String fieldname = rsetmd.getColumnName(i).toLowerCase();
                 FieldItem itemNow = null;
                 for(int j=0; j<fieldItemlist.size(); j++) {
                	 FieldItem item = fieldItemlist.get(j);
                	 if(fieldname.equalsIgnoreCase(item.getItemid())){
                		 itemNow = item;
                	 }
                 }
                 String temp = this.getValueByFieldType(rs, rsetmd, i, itemNow);
                 bean.set(fieldname.toLowerCase(), temp);
              }
              dataList.add(bean);
          }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.date.error.dbacessrror")));
        } 
        return dataList;
	}
	/**
	 * 获得字段类型
	 */
    private String getValueByFieldType(ResultSet rset, ResultSetMetaData rsetmd, int j, FieldItem itemNow) throws SQLException {
    	
    	String temp = null;
    	switch(rsetmd.getColumnType(j)) {
	    	case Types.DATE:
	    	case Types.TIMESTAMP:
	    		// 57125 日期型指标按格式显示
	    		String format = "";
	    		switch(itemNow.getItemlength()) {
	    			case 18:
	    				format = "yyyy-MM-dd HH:mm:ss";
	    				break;
	    			case 16:
	    				format = "yyyy-MM-dd HH:mm";
	    				break;
	    			case 10:
	    				format = "yyyy-MM-dd";
	    				break;
	    			case 7:
	    				format = "yyyy-MM";
	    				break;
	    			case 4:
	    				format = "yyyy";
	    				break;
	    			default:
	    				format = "yyyy-MM-dd";
	    		}
	    		temp = DateUtils.FormatDate(rset.getDate(j), format);
	    		break;
	    	case Types.CLOB:
	    		temp = Sql_switcher.readMemo(rset, rsetmd.getColumnName(j));
	    		break;
	    	case Types.BLOB:
	    		temp = "binary file";
	    		break;
	    	default:
	    		temp = rset.getString(j);
    	}
    	return temp==null?"":temp;
	}
    /**
     * 获取表格控件配置
     * getTableConfig
     * @param jsonObj		前台传递的参数的json对象
     * @param showMxBtn		是否显示明细按钮
     * @param confirmFlag	员工是否需要确认
     * @param showDetailFlag	方案设置是否显示日明细"true"/"false"
     * @param role			当前登录用户角色 1/2/3/4
     * @return
     * @throws GeneralException
     * @date 2019年4月22日 下午5:02:14
     * @author 
     */
	@Override
    public String getTableConfig(JSONObject jsonObj, boolean showMxBtn, Integer confirmFlag, String showDetailFlag, int role) throws GeneralException {
		String kq_duration = jsonObj.getString("kq_duration");
		String showMx = jsonObj.getString("showMx");
		String kq_year = jsonObj.getString("kq_year");
		String org_id = jsonObj.getString("org_id");
		String scheme_id = jsonObj.getString("scheme_id");
		String viewtype = jsonObj.getString("viewtype");
		String cbase = jsonObj.getString("cbase");
		try {
			if(StringUtils.isNotBlank(scheme_id))
				scheme_id = PubFunc.decrypt(scheme_id); 
			ArrayList<ColumnsInfo> columnList = this.getColumnList(showMx,kq_duration,kq_year,scheme_id,confirmFlag);
			String datasql = this.getTableSql(kq_year, kq_duration, org_id, scheme_id, cbase);
			// 51114 sql为空表示没有库权限抛出提示信息
			if(StringUtils.isEmpty(datasql)) {
	        	throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("kq.data.nopiv")));
	        }
			//是指不显示的字段
			this.setExceptFields(",guidkey,");
			TableConfigBuilder builder = new TableConfigBuilder("kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")+scheme_id, columnList, "kqdatamx"+("true".equals(showMx)?"Mx":"NoneMx"), userView, conn);
			builder.setDataSql(datasql);//数据查询sql语句
			builder.setOrderBy("order by a0000 asc");//排序语句
			builder.setAutoRender(false);//是否自动渲染表格到页面
			builder.setSelectable(false);//选框
			builder.setEditable(true);//表格编辑
			builder.setPageSize(20);//每页条数
			builder.setScheme(true);//使用栏目设置
			// 只有人事处考勤员才有栏目设置功能
			if(role == KqDataUtil.role_Clerk && this.userView.hasTheFunction("272030208"))
				builder.setSetScheme(true);
			else
				builder.setSetScheme(false);
			
			builder.setColumnFilter(true);
			builder.setSchemePosition("kqdatamx_schemeSetting"+("true".equals(showMx)?"Mx":"NoneMx"));
			builder.setSchemeSaveCallback("kqDataMx_me.schemeSetting_callBack");
			ArrayList buttonList = this.getButtonList(org_id, showMx, showMxBtn, confirmFlag, scheme_id, viewtype, showDetailFlag);//得到操作按钮
			builder.setTableTools(buttonList);//表格工具栏功能
			return builder.createExtTableConfig();
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			
		}
	}
	/**
	 * 获得功能按钮
	 * getButtonList
	 * @param orgId
	 * @param showMx
	 * @param showMxBtn		是否显示明细按钮
	 * @param confirmFlag
	 * @param schemeId
	 * @param viewtype		0上报页面，1 审批页面
	 * @param showDetailFlag	方案设置是否显示日明细"true"/"false"
	 * @return
	 * @throws GeneralException
	 */
	private ArrayList getButtonList(String orgId, String showMx, boolean showMxBtn, Integer confirmFlag
			, String schemeId, String viewtype, String showDetailFlag) throws GeneralException {
		ArrayList buttonList = new ArrayList();
		try {
			ArrayList<LazyDynaBean> menuList = new ArrayList<LazyDynaBean>();
			LazyDynaBean buttonInfo = new LazyDynaBean();
			//输出Excel
			String idSuff = ("true").equals(showMx)?"Mx":"NoneMx";
			// 45652 区分上报页面 与审批页面的各个导出功能授权
			boolean isDataApp = "0".equals(viewtype);
			boolean isDataSp = "1".equals(viewtype);
			if((this.userView.hasTheFunction("272030108") && isDataApp) 
					|| (this.userView.hasTheFunction("272030209") && isDataSp)){	
				buttonInfo.set("id", "importcollect"+idSuff);
				buttonInfo.set("text", ResourceFactory.getProperty("kq.date.mx.import"));
				buttonInfo.set("handler", "kqDataMx_me.importExcel('collect')");
				menuList.add(buttonInfo);
			}
			if((this.userView.hasTheFunction("272030102") && isDataApp) 
					|| (this.userView.hasTheFunction("272030203") && isDataSp)){
				buttonInfo = new LazyDynaBean();
				buttonInfo.set("id", "exportexcel"+idSuff);
				buttonInfo.set("text", ResourceFactory.getProperty("kq.date.mx.button.exportexcel"));
				buttonInfo.set("handler", "kqDataMx_me.exportExcel('')");
				buttonInfo.set("icon","/images/export.gif");
				menuList.add(buttonInfo);
			}
//			SchemeMainService schemeService = new SchemeMainServiceImpl(conn, userView);
//			HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(PubFunc.encrypt(schemeId));
//			int detailFlag = Integer.parseInt(String.valueOf(schemeMap.get("day_detail_enabled")));
			// 44646 若方案中  显示日明细数据 参数 则增加输出日明细功能
			if("true".equalsIgnoreCase(showDetailFlag)) {
				if((this.userView.hasTheFunction("272030103") && isDataApp) 
						|| (this.userView.hasTheFunction("272030204") && isDataSp)){
					buttonInfo = new LazyDynaBean();
					buttonInfo.set("id", "exportdaily"+idSuff);
					buttonInfo.set("text", ResourceFactory.getProperty("menu.out.label")+ResourceFactory.getProperty("kq.date.mx.name"));
					buttonInfo.set("handler", "kqDataMx_me.exportExcel('daily')");
					buttonInfo.set("icon","/images/export.gif");
					menuList.add(buttonInfo);
				}
			}
			if((this.userView.hasTheFunction("272030104") && isDataApp) 
					|| (this.userView.hasTheFunction("272030205") && isDataSp)){	
				buttonInfo = new LazyDynaBean();
				buttonInfo.set("id", "exportcollect"+idSuff);
				buttonInfo.set("text", ResourceFactory.getProperty("menu.out.label")+ResourceFactory.getProperty("kq.date.mx.collect"));
				buttonInfo.set("handler", "kqDataMx_me.exportExcel('collect')");
				buttonInfo.set("icon","/images/export.gif");
				menuList.add(buttonInfo);
			}
			
			if(menuList.size()>0) {
				//功能导航
				String menu = KqDataUtil.getMenuStr(ResourceFactory.getProperty("kq.date.mx.button.navigation"),"navbar"+idSuff,menuList);
				buttonList.add(menu);
			}
	
			//新建按钮
			ButtonInfo createBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.createnew"),"kqDataMx_me.createNewdata");
			createBtn.setId("createBtn"+idSuff);
			buttonList.add(createBtn);
            //删除按钮
			if (this.userView.hasTheFunction("272030106")) {
	            ButtonInfo deleteBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.delete"),"kqDataMx_me.deletePerson");
	            deleteBtn.setId("deleteBtn"+idSuff);
	            buttonList.add(deleteBtn);
			}
			String[] orgArr = orgId.split(",");
			//不是针对单个所属机构的都不需要显示下面的按钮 haosl
			if(orgArr.length==1) {
	            //人员增减
	            ButtonInfo staffChangeBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.staffchange"),"kqDataMx_me.staffChange()");
	            staffChangeBtn.setId("staffChangeBtn"+idSuff);
	            buttonList.add(staffChangeBtn);
				//计算
				if ((this.userView.hasTheFunction("272030101") && isDataApp) 
						|| (this.userView.hasTheFunction("272030201") && isDataSp)) {
					ButtonInfo calulateBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.calulate"),"kqDataMx_me.kqDataSp('compute')");
					calulateBtn.setId("calulateBtn"+idSuff);
					buttonList.add(calulateBtn);
				}
				//启用员工确认时才显示发布按钮
				if (confirmFlag == 1) {
					ButtonInfo publishBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.publish"),"kqDataMx_me.kqDataPublish()");
					publishBtn.setId("publishBtn"+idSuff);
					buttonList.add(publishBtn);
				}
				//报批
				ButtonInfo appealBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.appeal"),"kqDataMx_me.kqDataSp('appeal')");
				appealBtn.setId("appealBtn"+idSuff);
				buttonList.add(appealBtn);
				//退回
				ButtonInfo rejectBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.reject"),"kqDataMx_me.kqDataSp('reject')");
				rejectBtn.setId("rejectBtn"+idSuff);
				buttonList.add(rejectBtn);
				//同意
				ButtonInfo agreeBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.agree"),"kqDataMx_me.kqDataSp('approve')");
				agreeBtn.setId("agreeBtn"+idSuff);
				buttonList.add(agreeBtn);
				//提交归档 45770  不需要上报人事处则不走功能授权
				if ((this.userView.hasTheFunction("272030202") && isDataSp) 
						|| (this.userView.hasTheFunction("272030105") && isDataApp)) {
					ButtonInfo submitBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.submit"),"kqDataMx_me.kqDataSp('submit')");
					submitBtn.setId("submitBtn"+idSuff);
					buttonList.add(submitBtn);
				}
				// 重置 已批准已归档数据
				if (this.userView.hasTheFunction("2720300")) {
					ButtonInfo resetBtn = new ButtonInfo(ResourceFactory.getProperty("kq.kq_rest.reset"),"kqDataMx_me.kqDataReset()");
					resetBtn.setId("resetBtn"+idSuff);
					buttonList.add(resetBtn);
				}
			}
			//返回
			ButtonInfo returnBtn = new ButtonInfo(ResourceFactory.getProperty("kq.date.mx.button.return"),"kqDataMx_me.closeMainPanel");
			returnBtn.setId("returnBtn"+idSuff);
			buttonList.add(returnBtn);
			
			//输入查询框
			ButtonInfo queryBox = new ButtonInfo();
			queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
			FieldItem fieldItem = DataDictionary.getFieldItem("only_field", "q35");
			KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
			String filedName = kqDataUtil.getOnlyFieldName(conn);
			queryBox.setText(ResourceFactory.getProperty("kq.date.mx.button.inputquery")+(StringUtils.isBlank(filedName)?fieldItem.getItemdesc():filedName)+ResourceFactory.getProperty("kq.date.mx.button.query"));
			queryBox.setFunctionId("KQ00021204");
			buttonList.add(queryBox);
			buttonList.add("->");
			if(!showMxBtn)
				return buttonList;
			//日明细开关
			String src = "../../../module/kq/images/kq_off.png";
			String title=ResourceFactory.getProperty("kq.date.mx.showmx")+ResourceFactory.getProperty("kq.date.mx.name");
			String value = "1";
			if("true".equalsIgnoreCase(showMx)) {
				src="../../../module/kq/images/kq_on.png";
				title=ResourceFactory.getProperty("kq.date.mx.hiddenmx")+ResourceFactory.getProperty("kq.date.mx.name");
				value = "0";
			}
			StringBuffer html = new StringBuffer();
			html.append("<div style='float:right;width:100px;text-align:right;margin:3px 0 0 0;'><span style='line-height:24px;position:relative;bottom:1px;right:3px;'>");
			html.append(ResourceFactory.getProperty("kq.date.mx.name")+"</span><img onclick='kqDataMx_me.showMxClickFn(\""+value+"\")' title='"+title+"' style='position:relative;top:2px;width:40px;cursor:pointer;' src='"+src+"'/></div>");
			buttonList.add(html.toString());
			if("true".equalsIgnoreCase(showMx)){
	            StringBuffer mxDetail = new StringBuffer();
	            mxDetail.append("<div id='mxDetailDiv' style='border:1px #c5c5c solid !important;'></div>");
	            buttonList.add(mxDetail.toString());
	        }
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return buttonList;
	}
	@Override
    public ArrayList<ColumnsInfo> getColumnList(String showMx, String kq_duration, String kq_year, String schemeId
			, Integer confirmFlag) throws GeneralException {
		ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
		try {
		   	// 业务字典指标
	        String subModuleId = "kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")+schemeId;
	        //将私有栏目设置改为公有，并删除其他栏目设置
	        setSchemePulic(subModuleId);
	        KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
			//兼容期间为6时改成06这种情况
			if(kq_duration.length()==1) {
				kq_duration = Integer.parseInt(kq_duration)<10?"0"+kq_duration:kq_duration;
			}
			LazyDynaBean periodBean = kqDataUtil.getDatesByKqDuration(this.conn, kq_year, kq_duration);
			Date kqStart = null;
			Date kqEnd = null;
			Calendar kqStartCal =Calendar.getInstance();
			Calendar kqEndCal =Calendar.getInstance();
			if(periodBean!=null) {
				kqStart = (Date) periodBean.get("kq_start");
				kqEnd = (Date) periodBean.get("kq_end");
				kqStartCal.setTime(kqStart);
				kqEndCal.setTime(kqEnd);
			}
			// 60840  不勾选参数：“允许修改统计项、计算项”那么设置了统计项的月汇总需要控制不可以修改
			String enableModifyFielditemids = ",";
			KqPrivForHospitalUtil kqPriv = new KqPrivForHospitalUtil(this.userView, this.conn);
			if(!"1".equals(kqPriv.getEnable_modify())){
				KqItemService itemService = new KqItemServiceImpl(userView, conn);
				ArrayList<LazyDynaBean> kqItems = itemService.listKqItem("", new ArrayList<String>(), "displayorder,item_id");
				for(LazyDynaBean one : kqItems) {
					if("1".equals((String)one.get("item_type"))) {
						enableModifyFielditemids += (String)one.get("fielditemid") + ",";
					}
				}
				enableModifyFielditemids = enableModifyFielditemids.toLowerCase();
			}
	
			ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Q35", 1);
			String ignoreFi = this.ignoreFielditemid(schemeId);
			
	        subModuleId += "_onlysave";
			TableFactoryBO tfb = new TableFactoryBO(subModuleId, userView, conn);
			HashMap layoutConfig = tfb.getTableLayoutConfig();
			//有栏目设置则读取栏目设置的配置
	        if(null != layoutConfig){
	        	boolean isAdded_31 = false;
	    		ArrayList<String> columnStr = new ArrayList<String>();
	        	Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
	        	String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");//是否定义唯一性指标 0：没定义
	            String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//唯一性指标值
				ArrayList columnsConfigs = tfb.getTableColumnConfig((Integer)layoutConfig.get("schemeId"));
				ColumnConfig cc = null;
				ColumnsInfo ci = null;
				String columnId = "";
				Boolean mergeLockState = null;
				String mergeDesc = "";
				ArrayList<ColumnsInfo> mergeColumns = new ArrayList<ColumnsInfo>();
				int mergeIndex = 0;
				boolean isAdded = false; //是否已经添加过日期列
	            Map<String,ColumnConfig> columnsMap = new HashMap<String,ColumnConfig>();
	            for(int i=0;i<columnsConfigs.size();i++) {
	                cc = (ColumnConfig)columnsConfigs.get(i);
	                columnId = cc.getItemid();
	                columnsMap.put(columnId,cc);
	            }
				StringBuffer addedColumn = new StringBuffer();
				addedColumn.append(",");
				int width = 83;
				for(int i=0;i<columnsConfigs.size();i++) {
					cc = (ColumnConfig)columnsConfigs.get(i);
					columnId = cc.getItemid();
					if("confirm".equals(columnId)){
					    columnId = "confirm_";
	                }
					//日期列特殊处理，不需要从栏目设置读取
					if(columnId.toUpperCase().startsWith("Q35")
							&& StringUtils.isNumericSpace(columnId.substring(3))
							&& Integer.parseInt(columnId.substring(3))>=1
							&& Integer.parseInt(columnId.substring(3))<=31) {
						if(isAdded)
							continue;
						isAdded = true;
	                    //没有日明细时，补上日明细
	                    if(confirmFlag !=0 && !columnsMap.containsKey("confirm_")){
	                        ci = new ColumnsInfo();
	                        FieldItem fieldItem = DataDictionary.getFieldItem("confirm","Q35");
	                        ci.setColumnDesc(fieldItem.getItemdesc());
	                        ci.setRendererFunc("kqDataMx_me.confirmRender");
	                        ci.setColumnWidth(80);
	                        ci.setColumnId("confirm_");
	                        ci.setColumnType("A");
	                        ci.setTextAlign("center");
	                        ci.setFilterable(true);
	                        ci.setEditableValidFunc("false");
	                        ArrayList<CommonData> list = new ArrayList<CommonData>();
	                        list.add(new CommonData("0", ResourceFactory.getProperty("kq.date.mx.confirm0")));
	                        list.add(new CommonData("1", ResourceFactory.getProperty("kq.date.mx.confirm1")));
	                        list.add(new CommonData("2", ResourceFactory.getProperty("kq.date.mx.confirm2")));
	                        ci.setOperationData(list);
	                        columns.add(ci);
	                    }
	
						//组装日明细列头
						int temp=1;
						for(;!kqStartCal.after(kqEndCal);kqStartCal.add(Calendar.DATE, 1)) {
							int day = kqStartCal.get(Calendar.DAY_OF_MONTH);
							int month = kqStartCal.get(Calendar.MONTH)+1;
							int week = kqStartCal.get(Calendar.DAY_OF_WEEK);
							String weekStr = kqDataUtil.getWeekDesc(week);
							String dayStr = day<10?"0"+day:String.valueOf(day);
							String monthStr = month<10?"0"+month:String.valueOf(month);
							String  tempStr = temp<10?"0"+temp:String.valueOf(temp);
							ci = getColumnsInfo("Q35"+tempStr+"_", monthStr+"."+dayStr, 50, "", "A", 0, 0);
							ci.setFilterable(false);
							if(columnsMap.containsKey("q35"+tempStr) && columnsMap.get("q35"+tempStr).getDisplaywidth()>0){
	                            width = columnsMap.get("q35"+tempStr).getDisplaywidth();
	                        }
							
							ColumnsInfo child = getColumnsInfo("Q35"+tempStr,weekStr, width, "", "A", 0, 0);
							child.setFilterable(false);
							child.setQueryable(false);
							child.setRendererFunc("kqDataMx_me.rMxDataRender");
							child.setEditableValidFunc("false");
							if(!"true".equals(showMx)) {
								child.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
							}
							columnStr.add("Q35"+tempStr);
							ci.addChildColumn(child);
							columns.add(ci);
							temp++;
						}
						continue;
					}
					
					/**
					 * 57209 增加该校验 保证在保存栏目设置不会缺少指标
					 */
					if(isAdded && !isAdded_31 && columnStr.size() < 31) {
						int temp = columnStr.size();
						for(;temp < 31;kqStartCal.add(Calendar.DATE, 1)) {
							temp++;
							mergeDesc = DateUtils.format(kqStartCal.getTime(), "yyyy-MM-dd");
							int day = kqStartCal.get(Calendar.DAY_OF_MONTH);
							int month = kqStartCal.get(Calendar.MONTH)+1;
							int week = kqStartCal.get(Calendar.DAY_OF_WEEK);
							String weekStr = kqDataUtil.getWeekDesc(week);
							String dayStr = day<10?"0"+day:String.valueOf(day);
							String monthStr = month<10?"0"+month:String.valueOf(month);
							String  tempStr = temp<10?"0"+temp:String.valueOf(temp);
							ci = getColumnsInfo("Q35"+tempStr+"_", monthStr+"."+dayStr, 50, "", "A", 0, 0);
							ci.setFilterable(false);
							
							// 已存在栏目设置的与保持的宽度一致
							ColumnsInfo child = getColumnsInfo("Q35"+tempStr,weekStr, width, "", "A", 0, 0);
							child.setFilterable(false);
							child.setQueryable(false);
							child.setEditableValidFunc("false");
							child.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
							columnStr.add("Q35"+tempStr);
							ci.addChildColumn(child);
							columns.add(ci);
						}
						isAdded_31 = true;
					}
					
					addedColumn.append(columnId.toLowerCase()+",");
					ci = new ColumnsInfo();
					ci.setColumnId(columnId);
					// 61553 放开过滤功能
					ci.setFilterable(true);
					//加过滤属性
					if("a0101".equalsIgnoreCase(columnId)
							||"b0110".equalsIgnoreCase(columnId)
							||"e0122".equalsIgnoreCase(columnId)
							||"e01a1".equalsIgnoreCase(columnId)
	                        ||"only_field".equalsIgnoreCase(columnId)
	                        ||"confirm_".equalsIgnoreCase(columnId)) {
						ci.setEditableValidFunc("false");
					}
					//点击跳转出勤异常显示页面
					if ("a0101".equalsIgnoreCase(columnId)) {
						ci.setRendererFunc("kqDataMx_me.getKqExcept");
					}
	                if("confirm_".equalsIgnoreCase(columnId)){
	                    if(confirmFlag == 0) {
	                       continue;
	                    }
	                    ArrayList<CommonData> list = new ArrayList<CommonData>();
	                    list.add(new CommonData("0", ResourceFactory.getProperty("kq.date.mx.confirm0")));
	                    list.add(new CommonData("1", ResourceFactory.getProperty("kq.date.mx.confirm1")));
	                    list.add(new CommonData("2", ResourceFactory.getProperty("kq.date.mx.confirm2")));
	                    ci.setOperationData(list);
	                    ci.setRendererFunc("kqDataMx_me.confirmRender");
	                }
	
					if("1".equals(cc.getIs_display())){
						ci.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
					}else{
						//如果此列为 总是加载数据列，设置为只加载数据
						if(ci.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD || ci.getLoadtype()==ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE)
							ci.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
						else
							ci.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
						
						if(cc.getMergedesc()==null || cc.getMergedesc().length()==0)
							cc.setMergedesc(mergeDesc);
					}
				    	
					//数据布局  左对齐、居中、 右对齐
					switch(cc.getAlign()){
					case 1:
						ci.setTextAlign("left");
						break;
					case 2:
						ci.setTextAlign("center");
						break;
					case 3:
						ci.setTextAlign("right");
						break;
					}
					    
					//数据布局  左对齐、居中、 右对齐
					if(cc.getDisplaywidth()>0)
						ci.setColumnWidth(cc.getDisplaywidth());
					//排序方式 无，正序，倒序
					ci.setOrdertype(cc.getIs_order());
					//是否汇总
					if("1".equals(cc.getIs_sum()))
						ci.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
					else
						ci.setSummaryType(0);
	                FieldItem fi = DataDictionary.getFieldItem("confirm_".equals(columnId)?"confirm":columnId,"Q35");
	                // 先校验是否为null
	                if(null == fi) {
                        continue;
                    }
					if(cc.getDisplaydesc()!=null && cc.getDisplaydesc().length()>0) {
	                    ci.setColumnDesc(cc.getDisplaydesc());
	                    ci.setColumnRealDesc(cc.getItemdesc());
                        ci.setColumnType(fi.getItemtype());
                        ci.setDecimalWidth(fi.getDecimalwidth());
                        ci.setCodesetId(fi.getCodesetid());
	                }else{
	                    if("only_field".equalsIgnoreCase(columnId) || "only_field_".equalsIgnoreCase(columnId)){
	                        String filedName = kqDataUtil.getOnlyFieldName(conn);
	                        ci.setEditableValidFunc("false");
	                        if(StringUtils.isNotBlank(filedName)) {
	                            ci.setColumnDesc(filedName);
	                            ci.setColumnRealDesc(filedName);
	                        }else{
	                            ci.setColumnDesc(fi.getItemdesc());
	                        }
	                    }else {
	                        ci.setColumnDesc(fi.getItemdesc());
	                        ci.setCodesetId(fi.getCodesetid());
	                        ci.setColumnType(fi.getItemtype());
	                        ci.setDecimalWidth(fi.getDecimalwidth());
	                        ci.setColumnLength(fi.getItemlength());
	                    }
	                }
					
					if("confirm_".equals(columnId))
						ci.setColumnType("A");
					 
					ci.displayIndex=i;
					
					if("only_field".equalsIgnoreCase(columnId) || "only_field_".equalsIgnoreCase(columnId)) {
						if(StringUtils.isNotEmpty(onlyname)) {
							ci.setColumnId("only_field_");
						} else {
							ci.setColumnId("only_field");
						}
					}
					// 57492 设置单位 部门 岗位 权限
					if(",B0110,E0122,E01A1,".contains(""+columnId.toUpperCase()+",")) {
						ci.setCtrltype("3");
						ci.setNmodule("11");
					}else {
						ci.setCtrltype("0");
					}
					//锁列
					if("1".equals(cc.getIs_lock()))
						ci.setLocked(true);
					else
						ci.setLocked(false);
					// 设置不可编辑
					if(enableModifyFielditemids.contains(","+columnId.toLowerCase()+",")) {
						ci.setEditableValidFunc("false");
					}
					//如果不是二级表头
					if(cc.getMergedesc()==null || cc.getMergedesc().length()==0){
						if(!mergeColumns.isEmpty()){
							ColumnsInfo compositedColumn = new ColumnsInfo();
							compositedColumn.setColumnDesc(mergeDesc);
							compositedColumn.setLocked(mergeLockState);
							compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
							columns.add(compositedColumn);
							
							mergeColumns.clear();
							mergeDesc = "";
							mergeLockState = null;
							mergeIndex = 0;
						}
						columns.add(ci);
						continue;
					}
					
					// 如果 复合表头 list 为空， 或者   与上一个 columns 复合表头名称相同，则添加进复合表头list
					if(mergeColumns.size()==0 || (cc.getMergedesc().equals(mergeDesc) &&  i == mergeIndex+1)){
						mergeColumns.add(ci);
						mergeDesc = cc.getMergedesc();
						mergeIndex = i;
						if(mergeLockState==null || mergeLockState.booleanValue())
							mergeLockState = Boolean.valueOf(ci.isLocked());
						continue;
					}else if(!cc.getMergedesc().equals(mergeDesc)){
						//如果 复合表头名称跟上一次的不一样了，那就是一个新的复合表头。将以前的 复合表头list 里的column 组合成复合表头，存入 newColumns 中，并重新初始化复合表头的一些参数
						ColumnsInfo compositedColumn = new ColumnsInfo();
						compositedColumn.setColumnDesc(mergeDesc);
						compositedColumn.setLocked(mergeLockState);
						compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
						columns.add(compositedColumn);
						
						mergeColumns.clear();
						
						mergeColumns.add(ci);
						mergeDesc = cc.getMergedesc();
						mergeLockState = Boolean.valueOf(ci.isLocked());
						mergeIndex = i;
					}else{//剩下的情况就不需要处理了，都是不符合复合表头定义规则的，直接当做普通列
						columns.add(ci);
					}
				}
				if(!mergeColumns.isEmpty()){
					ColumnsInfo compositedColumn = new ColumnsInfo();
					compositedColumn.setColumnDesc(mergeDesc);
					compositedColumn.setLocked(mergeLockState);
					compositedColumn.setChildColumns((ArrayList)mergeColumns.clone());
					columns.add(compositedColumn);
				}
				//补齐业务字典中的字段
				ColumnsInfo columnsInfo = null;
				for(int i=0;i<fieldList.size();i++) {
					FieldItem fi = fieldList.get(i);
					String itemId = fi.getItemid();
					// 去除没有启用的指标
					if (!"1".equals(fi.getUseflag())) {
						continue;
					}
					// 去除隐藏的指标
					if (!"1".equals(fi.getState())) {
						continue;
					}
					// 去除不需要的指标
					if (exceptFields.indexOf("," + itemId.toLowerCase() + ",") != -1) {
						continue;
					}
	
					if(StringUtils.isEmpty(itemId))
						continue;
					//去掉忽略的自定义指标
					if(ignoreFi.indexOf(","+itemId.toLowerCase()+",")!=-1
							||addedColumn.toString().indexOf(","+itemId.toLowerCase()+",")!=-1
	                        ||"confirm".equalsIgnoreCase(itemId)) {
						continue;
					}
					if(itemId.toUpperCase().startsWith("Q35")
							&& StringUtils.isNumericSpace(itemId.substring(3))
							&& Integer.parseInt(itemId.substring(3))>=1
							&& Integer.parseInt(itemId.substring(3))<=31) {
						continue;
					}
					// 57492 设置单位 部门 岗位 权限
					if(",B0110,E0122,E01A1,".contains(""+itemId.toUpperCase()+",")) {
						ci.setCtrltype("3");
						ci.setNmodule("11");
					}
					columnsInfo = getColumnsInfoByFi(fi, 100);
					columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);
					editAble(columnsInfo);
					columns.add(columnsInfo);
				}
				
			}else {
				//没有栏目设置则走默认配置
				columns.addAll(getDefaultColumns(showMx, schemeId, kqStartCal, kqEndCal, fieldList, ignoreFi, confirmFlag, enableModifyFielditemids));
			}
			
			ColumnsInfo guidkey = getColumnsInfo("guidkey", "", 70, "", "A", 0, 0);
	        guidkey.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
	        columns.add(guidkey);
	        ColumnsInfo confirmhiden = getColumnsInfo("confirm", "", 70, "", "A", 0, 0);
	        confirmhiden.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
	        columns.add(confirmhiden);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return columns;
	}
	
	private ArrayList<ColumnsInfo> getDefaultColumns(String showMx, String schemeId, Calendar kqStartCal, Calendar kqEndCal
			, ArrayList<FieldItem> fieldList, String ignoreFi, Integer confirmFlag, String enableModifyFielditemids) throws GeneralException {
        KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
		ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
		ColumnsInfo columnsInfo = null;
		boolean isAdded = false;
		boolean isAdded_31 = false;
		ArrayList<String> columnStr = new ArrayList<String>();
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
		//是否定义唯一性指标 0：没定义
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		//唯一性指标值
        String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		for(int i=0;i<fieldList.size();i++) {
			FieldItem fi = fieldList.get(i);
			String itemId = fi.getItemid();
			// 去除没有启用的指标
			if (!"1".equals(fi.getUseflag())) {
				continue;
			}
			// 去除隐藏的指标
			if (!"1".equals(fi.getState())) {
				continue;
			}
			// 去除不需要的指标
			if (exceptFields.indexOf("," + itemId.toLowerCase() + ",") != -1) {
				continue;
			}
			
			//去掉忽略的自定义指标
			if(ignoreFi.indexOf(","+itemId.toLowerCase()+",")!=-1
					&& !"q3533".equalsIgnoreCase(itemId.toLowerCase())
					&& !"q3535".equalsIgnoreCase(itemId.toLowerCase()))
				continue;
			
			if(StringUtils.isEmpty(itemId))
				continue;
			//日期列需要特殊处理
			if(itemId.toUpperCase().startsWith("Q35")
					&& StringUtils.isNumericSpace(itemId.substring(3))
					&& Integer.parseInt(itemId.substring(3))>=1
					&& Integer.parseInt(itemId.substring(3))<=31) {
				if(isAdded || "false".equalsIgnoreCase(showMx))
					continue;
				isAdded = true;
				//组装日明细列头
				int temp=1;
				for(;!kqStartCal.after(kqEndCal);kqStartCal.add(Calendar.DATE, 1)) {
					int day = kqStartCal.get(Calendar.DAY_OF_MONTH);
					int month = kqStartCal.get(Calendar.MONTH)+1;
					int week = kqStartCal.get(Calendar.DAY_OF_WEEK);
					String weekStr = kqDataUtil.getWeekDesc(week);
					String dayStr = day<10?"0"+day:String.valueOf(day);
					String monthStr = month<10?"0"+month:String.valueOf(month);
					String  tempStr = temp<10?"0"+temp:String.valueOf(temp);
					columnsInfo = getColumnsInfo("Q35"+tempStr+"_", monthStr+"."+dayStr, 50, "", "A", 0, 0);
					columnsInfo.setFilterable(false);
					
					ColumnsInfo child = getColumnsInfo("Q35"+tempStr,weekStr, 83, "", "A", 0, 0);
					child.setFilterable(false);
					child.setQueryable(false);
					child.setRendererFunc("kqDataMx_me.rMxDataRender");
					child.setEditableValidFunc("false");
					if(!"true".equals(showMx)) {
						child.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
					}
					columnStr.add("Q35"+tempStr);
					columnsInfo.addChildColumn(child);
					columns.add(columnsInfo);
					temp++;
				}
				continue;
				
			}
			/**
			 * 57209 增加该校验 保证在保存栏目设置不会缺少指标
			 */
			if(isAdded && !isAdded_31 && columnStr.size() < 31) {
				int temp = columnStr.size();
				for(;temp < 31;kqStartCal.add(Calendar.DATE, 1)) {
					temp++;
					int day = kqStartCal.get(Calendar.DAY_OF_MONTH);
					int month = kqStartCal.get(Calendar.MONTH)+1;
					int week = kqStartCal.get(Calendar.DAY_OF_WEEK);
					String weekStr = kqDataUtil.getWeekDesc(week);
					String dayStr = day<10?"0"+day:String.valueOf(day);
					String monthStr = month<10?"0"+month:String.valueOf(month);
					String  tempStr = temp<10?"0"+temp:String.valueOf(temp);
					columnsInfo = getColumnsInfo("Q35"+tempStr+"_", monthStr+"."+dayStr, 50, "", "A", 0, 0);
					columnsInfo.setFilterable(false);
					
					ColumnsInfo child = getColumnsInfo("Q35"+tempStr,weekStr, 83, "", "A", 0, 0);
					child.setFilterable(false);
					child.setQueryable(false);
					child.setEditableValidFunc("false");
					child.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
					columnStr.add("Q35"+tempStr);
					columnsInfo.addChildColumn(child);
					columns.add(columnsInfo);
				}
				isAdded_31 = true;
			}
			columnsInfo = getColumnsInfoByFi(fi, 100);
			//以下列锁定
			if("B0110".equalsIgnoreCase(itemId)
					||"E0122".equalsIgnoreCase(itemId)
					|| "E01A1".equalsIgnoreCase(itemId)
					|| "A0101".equalsIgnoreCase(itemId)) {
				columnsInfo.setLocked(true);
				columnsInfo.setEditableValidFunc("false");
			}
			// 点击跳转出勤异常显示页面
			if ("a0101".equalsIgnoreCase(itemId)) {
				columnsInfo.setRendererFunc("kqDataMx_me.getKqExcept");
			}
			if("confirm".equalsIgnoreCase(itemId)) {
                if(confirmFlag == 0) {
                    continue;
                }
                columnsInfo.setRendererFunc("kqDataMx_me.confirmRender");
                columnsInfo.setColumnWidth(80);
                columnsInfo.setColumnId("confirm_");
                columnsInfo.setColumnType("A");
                columnsInfo.setTextAlign("center");
                columnsInfo.setEditableValidFunc("false");
                ArrayList<CommonData> list = new ArrayList<CommonData>();
                list.add(new CommonData("0", ResourceFactory.getProperty("kq.date.mx.confirm0")));
                list.add(new CommonData("1", ResourceFactory.getProperty("kq.date.mx.confirm1")));
                list.add(new CommonData("2", ResourceFactory.getProperty("kq.date.mx.confirm2")));
                columnsInfo.setOperationData(list);
			}
			if("only_field".equalsIgnoreCase(itemId) || "only_field_".equalsIgnoreCase(itemId)) {
				String filedName = kqDataUtil.getOnlyFieldName(conn);
				if(StringUtils.isNotEmpty(onlyname))
					 columnsInfo.setColumnId("only_field_");
					
				columnsInfo.setLocked(true);
				columnsInfo.setEditableValidFunc("false");
				if(StringUtils.isNotBlank(filedName)) {
					columnsInfo.setColumnRealDesc(filedName);
					columnsInfo.setColumnDesc(filedName);
				}
			}
			editAble(columnsInfo);
			// 57492 设置单位 部门 岗位 权限
			if(",B0110,E0122,E01A1,".contains(""+itemId.toUpperCase()+",")) {
				columnsInfo.setCtrltype("3");
				columnsInfo.setNmodule("11");
			}
			// 设置不可编辑
			if(enableModifyFielditemids.contains(","+itemId.toLowerCase()+",")) {
				columnsInfo.setEditableValidFunc("false");
			}
			columns.add(columnsInfo);
		}
		
		return columns;
	}

	/**
	 * 判断指标是否可以编辑
	 * memoFields应急中心机构考勤员可以修改的备注指标
	 * confirm_field 个人确认说明指标
	 * 如果两个指标配置相同则，以考勤员为主
	 * 通过system参数取
	 */
	private void editAble(ColumnsInfo columnsInfo) {
        String memoFields = SystemConfig.getPropertyValue("hlwyjzx_memo").trim();
        String confirmField = SystemConfig.getPropertyValue("confirm_memo").trim();
		//如果此列是个人确认说明则不允许管理员编辑
		if(StringUtils.isNotBlank(confirmField) && !confirmField.equalsIgnoreCase(memoFields)
				&& confirmField.equalsIgnoreCase(columnsInfo.getColumnId())) {
			columnsInfo.setEditableValidFunc("false");
		}
	}
	
	/**
	 * 获得表格控件需要的sql
	 * @return
	 */
	@Override
    public String getTableSql(String kq_year, String kq_duration, String orgId, String scheme_id, String cbase) {
		/**
		 * 61671 经讨论取消关联主集获取唯一性指标，直接取q35即可
		 * 
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
		//是否定义唯一性指标 0：没定义
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		//唯一性指标值
        String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
        //凡是显示在本月的机构，那么点击进入后查看数据时，不控制任何数据范围权限或限制（如管理范围、考勤人员库等），只要是本机构的数据都要显示，以保证与当时历史所做数据基本一致
        //ArrayList<String> dbNames = KqPrivForHospitalUtil.getB0110Dase(this.userView, this.conn);
		ArrayList<String> dbNames =DataDictionary.getDbpreList();
		String selectSql = "select ";
		if(StringUtils.isNotBlank(onlyname)) 
			selectSql += "a01."+onlyname+" only_field_,";
		// a01.guidkey=Q35.guidkey and 
		*/
		String selectSql = "select case when (confirm is null or confirm='') then 0 else confirm end as confirm_, q35.* from Q35 ";
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" where kq_year='").append(kq_year).append("' and kq_duration='").append(kq_duration)
			.append("' and scheme_id='").append(scheme_id).append("'");
		
		if(StringUtils.isNotEmpty(orgId)) {
			String[] orgArr = orgId.split(",");
			whereSql.append(" and (");
			for(int i=0;i<orgArr.length;i++) {
				String id = orgArr[i];
				if(StringUtils.isNotBlank(id)){
					id = PubFunc.decrypt(id);
				}
				whereSql.append(" Q35.Org_id like '").append(id).append("%' or");
			}
			whereSql.setLength(whereSql.length()-2);
			whereSql.append(")");
		}
		
		return selectSql + whereSql.toString();
	}
	/**初始化列对象ColumnsInfo
	 * @param fi
	 * @param columnWidth
	 * @return
	 */
	private ColumnsInfo getColumnsInfoByFi(FieldItem fi, int columnWidth){
		ColumnsInfo co = new ColumnsInfo();
		
		String itemid = fi.getItemid();
		String itemdesc = fi.getItemdesc();
		String codesetId = fi.getCodesetid();
		String columnType = fi.getItemtype();
		int columnLength = fi.getItemlength();
		int decimalWidth = fi.getDecimalwidth();// 小数位
		co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
				columnType, columnLength, decimalWidth);

		return co;
	}
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc, int columnWidth, String codesetId
			, String columnType, int columnLength, int decimalWidth) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		if(!"b0110,e0122,e01a1".contains(columnId.toLowerCase())) {
		    columnsInfo.setCtrltype("0");
        }
		
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		columnsInfo.setAllowBlank(true);// 编辑时是否可以为空
		columnsInfo.setQueryable(true);
		// 61553 过滤功能（日明细单元格不支持过滤）
		columnsInfo.setFilterable(true);
		if("A".equals(columnType) || "M".equals(columnType)
				|| "D".equals(columnType)) {
			columnsInfo.setTextAlign("left");
		}else if("N".equals(columnType)){
			columnsInfo.setTextAlign("right");
		}
		return columnsInfo;
	}

	@Override
    public List<LazyDynaBean> getClassAndItemsOrder(String schemeId, String model) throws GeneralException {
		SchemeMainService schemeService = new SchemeMainServiceImpl(conn, userView);
		ShiftsService shiftsService = new ShiftsServiceImpl(userView,conn);
		KqItemService itemService = new KqItemServiceImpl(userView, conn);
		HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(schemeId);
		ArrayList<String> parameterList = new ArrayList<String>();
		List<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		ArrayList<LazyDynaBean> kqItems = new ArrayList<LazyDynaBean>();
		ArrayList<LazyDynaBean> kqClazzs = new ArrayList<LazyDynaBean>();
		String itemIds = schemeMap.get("item_ids");
		String classIds = schemeMap.get("class_ids");
		// 如果方案没选择休息班次 则默认放在第一个
		if(!(","+classIds+",").contains(",0,"))
			classIds = "0," + classIds;
		if(StringUtils.isNotBlank(itemIds)) {
			StringBuffer sqlWherebuf = new StringBuffer();
			sqlWherebuf.append(" and item_id in (");
			String[] idArr = itemIds.split(",");
			for(int i=0;i<idArr.length;i++) {
				sqlWherebuf.append("?,");
				parameterList.add(idArr[i]);
			}
			sqlWherebuf.setLength(sqlWherebuf.length()-1);
			sqlWherebuf.append(")");
			kqItems = itemService.listKqItem(sqlWherebuf.toString(), parameterList, "displayorder,item_id");
		}
		if(StringUtils.isNotBlank(classIds)){
			parameterList.clear();
			StringBuffer sqlWherebuf = new StringBuffer();
			sqlWherebuf.append(" and class_id in (");
			String[] idArr = classIds.split(",");
			for(int i=0;i<idArr.length;i++) {
				sqlWherebuf.append("?,");
				parameterList.add(idArr[i]);
			}
			sqlWherebuf.setLength(sqlWherebuf.length()-1);
			sqlWherebuf.append(")");
			kqClazzs = shiftsService.listKq_class(sqlWherebuf.toString(), parameterList, "seq asc");
		}
		// 53947 单独校验休息班次 
		boolean restBool = false;
		for(LazyDynaBean bean : kqClazzs) {
			if("0".equals(String.valueOf(bean.get("class_id")))){
				restBool = true;
				break;
			}
		}
		// 如果没有休息班次 则默认加上
		if(!restBool) {
			RowSet rs = null;
			try {
				ContentDAO dao = new ContentDAO(this.conn);
				rs = dao.search("SELECT * FROM kq_class where class_id='0' ");
				if (rs.next()) {
					KqDataUtil kqDataUtil = new KqDataUtil(userView);
					LazyDynaBean bean = new LazyDynaBean(); 
					bean.set("class_id", KqDataUtil.nullif(rs.getString("class_id")));
					bean.set("onduty_1", KqDataUtil.nullif(rs.getString("onduty_1")));
					bean.set("offduty_1", KqDataUtil.nullif(rs.getString("offduty_1")));
					bean.set("onduty_2", KqDataUtil.nullif( rs.getString("onduty_2")));
					bean.set("offduty_2", KqDataUtil.nullif(rs.getString("offduty_2")));
					bean.set("onduty_3", KqDataUtil.nullif(rs.getString("onduty_3")));
					bean.set("offduty_3", KqDataUtil.nullif(rs.getString("offduty_3")));
					bean.set("name", KqDataUtil.nullif(rs.getString("name")));
					bean.set("abbreviation", KqDataUtil.nullif(rs.getString("abbreviation")));
					bean.set("color", KqDataUtil.nullif(rs.getString("color")));
					bean.set("symbol", KqDataUtil.nullif(rs.getString("symbol")));
					bean.set("statistics_type", KqDataUtil.nullif(rs.getString("statistics_type")));
					bean.set("domain_count", rs.getInt("domain_count"));
					bean.set("work_hours", rs.getFloat("work_hours"));
					bean.set("org_id", KqDataUtil.nullif(rs.getString("org_id")));
					bean.set("remarks", KqDataUtil.nullif(rs.getString("remarks")));
					bean.set("is_validate", KqDataUtil.nullif(rs.getString("is_validate")));
					bean.set("seq", rs.getInt("seq"));
					kqClazzs.add(0, bean);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				PubFunc.closeDbObj(rs);
			}
		}
		// 班次信息
		if("1".equals(model)) {
			String[] idArr = classIds.split(",");
			for(int i=0;i<idArr.length;i++) {
				for(LazyDynaBean bean : kqClazzs) {
					//对class 排序
					String classId = String.valueOf(bean.get("class_id"));
					if(idArr[i].equals(classId)){
						bean.set("id", "C"+classId);
						list.add(bean);
						break;
					}
				}
			}
		}// 考勤项目信息
		else if("2".equals(model)) {
			String[] itemIdArr = itemIds.split(",");
			for(int i=0;i<itemIdArr.length;i++) {
				for(LazyDynaBean bean : kqItems) {
					//排除定义了计算公式  || 51100导入指标  的项目
					String itemId = String.valueOf(bean.get("item_id"));
					if(itemId.equals(itemIdArr[i])) {
						if(StringUtils.isNotEmpty(String.valueOf(bean.get("c_expr"))) 
								|| StringUtils.isNotEmpty(String.valueOf(bean.get("other_param"))))
							break;
						bean.set("id", "I"+String.valueOf(bean.get("item_id")));
						list.add(bean);
						break;
					}
				}
			}
		}// 班次+项目
		else if("0".equals(model)) {
			// 具有统计属性的班次
			String classDescs = ",";
			String[] idArr = classIds.split(",");
			for(int i=0;i<idArr.length;i++) {
				for(LazyDynaBean bean : kqClazzs) {
					//对class 排序
					String classId = String.valueOf(bean.get("class_id"));
					if(idArr[i].equals(classId)){
						bean.set("id", "C"+classId);
						String statistics_type = String.valueOf(bean.get("statistics_type"));
						if(StringUtils.isNotBlank(statistics_type)) {
							classDescs += AdminCode.getCodeName("85", statistics_type) + ",";
						}
						list.add(bean);
						break;
					}
				}
			}
			String[] itemIdArr = itemIds.split(",");
			for(int i=0;i<itemIdArr.length;i++) {
				for(LazyDynaBean bean : kqItems) {
					//排除定义了计算公式  || 51100导入指标  的项目
					String itemId = String.valueOf(bean.get("item_id"));
					if(itemId.equals(itemIdArr[i])) {
						if(StringUtils.isNotEmpty(String.valueOf(bean.get("c_expr"))) 
								|| StringUtils.isNotEmpty(String.valueOf(bean.get("other_param"))))
							break;
						bean.set("id", "I"+String.valueOf(bean.get("item_id")));
						// 校验有统计指标的考勤项目 与班次中统计属性描述 一致的 不再重复添加考勤项目
						String fielditemid = String.valueOf(bean.get("fielditemid"));
						if(StringUtils.isNotBlank(fielditemid)) {
							FieldItem fi = DataDictionary.getFieldItem(fielditemid, "Q35");
							if(fi == null || "0".equals(fi.getUseflag()))
								break;
							if(classDescs.contains(","+fi.getItemdesc()+","))
								break;
						}
						
						list.add(bean);
						break;
					}
				}
			}
		}
		
		return list;
	}
	@Override
    public List<LazyDynaBean> getAllClassAndItems() throws GeneralException {
		ShiftsService shiftsService = new ShiftsServiceImpl(userView,conn);
		KqItemService itemService = new KqItemServiceImpl(userView, conn);
		ArrayList<LazyDynaBean> kqItems = itemService.listKqItem("", new ArrayList<String>(), "displayorder,item_id");
		ArrayList<LazyDynaBean> kqClazzs = shiftsService.listKq_class("", new ArrayList<String>(), "seq asc");
		for (LazyDynaBean bean : kqClazzs) {
			bean.set("id", "C"+bean.get("class_id"));
		}
		for (LazyDynaBean bean : kqItems) {
			bean.set("id", "I"+bean.get("item_id"));
			bean.set("item_type", (String)bean.get("item_type"));
			bean.set("fielditemid", (String)bean.get("fielditemid"));
		}
		kqClazzs.addAll(kqItems);
		return kqClazzs;
	}
	@Override
    public HashMap<String,LazyDynaBean> getClassAndItems(String schemeId, String model) throws GeneralException {
		ShiftsService shiftsService = new ShiftsServiceImpl(userView,conn);
		KqItemService itemService = new KqItemServiceImpl(userView, conn);
		ArrayList<String> parameterList = new ArrayList<String>();
		HashMap<String,LazyDynaBean> map = new HashMap<String,LazyDynaBean>();
		ArrayList<LazyDynaBean> kqItems = new ArrayList<LazyDynaBean>();
		ArrayList<LazyDynaBean> kqClazzs = new ArrayList<LazyDynaBean>();
		/**
		 * 兼容获取全部班次和项目  目前有三处用到/导出excel/导出日明细/下发本人确认
		 */
		if(StringUtils.isBlank(schemeId)) {
			kqItems = itemService.listKqItem("", new ArrayList<String>(), "displayorder,item_id");
			kqClazzs = shiftsService.listKq_class("", new ArrayList<String>(), "seq asc");
		}else {
			// 有方案id按该方案获取
			SchemeMainService schemeService = new SchemeMainServiceImpl(conn, userView);
			HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(schemeId);
			String itemIds = schemeMap.get("item_ids");
			String classIds = schemeMap.get("class_ids");
			if(StringUtils.isNotBlank(itemIds)) {
				StringBuffer sqlWherebuf = new StringBuffer();
				sqlWherebuf.append(" and item_id in (");
				String[] idArr = itemIds.split(",");
				for(int i=0;i<idArr.length;i++) {
					sqlWherebuf.append("?,");
					parameterList.add(idArr[i]);
				}
				sqlWherebuf.setLength(sqlWherebuf.length()-1);
				sqlWherebuf.append(")");
				kqItems = itemService.listKqItem(sqlWherebuf.toString(), parameterList, "displayorder,item_id");
			}
			if(StringUtils.isNotBlank(classIds)){
				parameterList.clear();
				StringBuffer sqlWherebuf = new StringBuffer();
				sqlWherebuf.append(" and class_id in (");
				String[] idArr = classIds.split(",");
				for(int i=0;i<idArr.length;i++) {
					sqlWherebuf.append("?,");
					parameterList.add(idArr[i]);
				}
				sqlWherebuf.setLength(sqlWherebuf.length()-1);
				sqlWherebuf.append(")");
				kqClazzs = shiftsService.listKq_class(sqlWherebuf.toString(), parameterList, "seq asc");
			}
		}
		if("1".equals(model)) {
			for(LazyDynaBean bean : kqClazzs) {
				map.put("C"+bean.get("class_id"), bean);
			}
		}else if("2".equals(model)) {
			for(LazyDynaBean bean : kqItems) {
				//排除定义了计算公式的项目
				if(StringUtils.isNotEmpty(String.valueOf(bean.get("c_expr"))))
					continue;
				map.put("I"+bean.get("item_id"), bean);
			}
		}else if("0".equals(model)) {
			for(LazyDynaBean bean : kqClazzs) {
				map.put("C"+bean.get("class_id"), bean);
			}
			for(LazyDynaBean bean : kqItems) {
				//排除定义了计算公式的项目
				if(StringUtils.isNotEmpty(String.valueOf(bean.get("c_expr"))))
					continue;
				map.put("I"+bean.get("item_id"), bean);
			}
		}
		
		return map;
	}

	@Override
    public void saveKqDataMx(String scheme_id, String kq_duration, String kq_year, String guidkey, JSONObject paramValue
			, String orgId, String enableModifys) throws Exception{
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dw = new DbWizard(this.conn);
			orgId = PubFunc.decrypt(orgId);
			//获得考勤期间
			ArrayList parameterList = new ArrayList();
			parameterList.add(kq_year);
			parameterList.add(kq_duration);
			PeriodService periodService = new PeriodServiceImpl(userView, conn);
			ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
			LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
			Date kqStart = null;
			Date kqEnd = null;
			Calendar kqStartCal =Calendar.getInstance();
			Calendar kqEndCal =Calendar.getInstance();
			List<String> sqls = new ArrayList<String>();
			List<List<String>> valusList = new ArrayList<List<String>>();
			//遍历要更新的字段和值
			Iterator iterator = paramValue.keys();
			ArrayList<String> recerverList = new ArrayList<String>();
			HashMap a01Map = null;
			while(iterator.hasNext()) {
				String item_id = (String)iterator.next();
				String value = paramValue.getString(item_id);
				List values = new ArrayList();
				if(StringUtils.isBlank(item_id) || !dw.isExistField("q35", item_id)) {
					throw new  Exception(ResourceFactory.getProperty("kq.date.error.save"));
				}
				FieldItem fi = DataDictionary.getFieldItem(item_id);
				String itemType = fi.getItemtype();
				 if("A".equalsIgnoreCase(itemType)||"M".equals(itemType)){
				 	boolean flag = true;
				 	//日明细列特殊处理，保存日明细字段时校验value的有效性
                    if(item_id.toUpperCase().startsWith("Q35")
                            && StringUtils.isNumericSpace(item_id.substring(3))
                            && Integer.parseInt(item_id.substring(3))>=1
                            && Integer.parseInt(item_id.substring(3))<=31) {
						if(StringUtils.isNotBlank(value)) {
							String[] vals = value.split(",");
							for(String s : vals) {
								if(!s.toLowerCase().startsWith("c") && !s.toLowerCase().startsWith("i")) {
									flag = false;
									break;
								}
								// 55748 校验不可编辑的考勤项目
								if((enableModifys+",").contains(","+s+",")) {
									throw new  Exception(ResourceFactory.getProperty("kq.date.appeal.error.enableModifys"));
								}
							}
						}
						if(flag) {
							//保存日明细时，需要更新对应的 考勤kq_day_detail（日考勤打卡数据表）表的记录
							if(periodBean!=null) {
								kqStart = (Date) periodBean.get("kq_start");
								kqEnd = (Date) periodBean.get("kq_end");
								kqStartCal.setTime(kqStart);
								kqEndCal.setTime(kqEnd);
								kqEndCal.add(Calendar.DATE, 1);
							}
							//计算出考勤日期
							String itemid_="q3501";
							for(;!kqStartCal.after(kqEndCal);) {
								if(item_id.equals(itemid_))
									break;
								int day = Integer.parseInt(itemid_.substring(3))+1;
								String dayStr = day<10?"0"+day:String.valueOf(day);
								itemid_="q35"+dayStr;
								kqStartCal.add(Calendar.DATE, 1);
							}
							//先删除kq_day_detail相应记录
							String delSql = "delete from kq_day_detail where guidkey=? and org_id=? and kq_date=? and scheme_id=?";
							List dayValues = new ArrayList();
							dayValues.add(guidkey);
							dayValues.add(orgId);
							dayValues.add(DateUtils.getSqlDate(kqStartCal));
							dayValues.add(scheme_id);
							dao.delete(delSql,dayValues);
							if(!StringUtils.isBlank(value)) {
								String[] ids = value.split(",");
								for(int j=0;j<ids.length;j++) {
									String id = ids[j];
									int type = "C".equals(id.substring(0, 1))?2:1;
									String id_ = id.substring(1);
									//插入记录
									String insertSql = "insert into kq_day_detail(scheme_id,guidkey,org_id,kq_date,type,item_id,create_time) "
											+ "values(?,?,?,?,?,?,?)";
									dayValues.clear();
									dayValues.add(scheme_id);
									dayValues.add(guidkey);
									dayValues.add(orgId);
									dayValues.add(DateUtils.getTimestamp(kqStartCal));
									dayValues.add(type);
									dayValues.add(id_);
                                    dayValues.add(DateUtils.getTimestamp(new Date()));
									dao.insert(insertSql, dayValues);
								}
							}
						}
					}
					 //验证 值无效的话就不保存该字段
					 if(!flag)
						continue;
					 if(!"0".equals(fi.getCodesetid())) {
						 try{//有可不选，则值为"`","`"的话插入""
							 value = StringUtils.isEmpty(value)?value.toString():value.toString().split("`")[0];
							}catch(Exception e){
								value = "";
							}
					 }
					 values.add(StringUtils.isBlank(value)?" ":value);
						
                }else if("N".equalsIgnoreCase(itemType)){
                	//len=0位整形，len>0位小数类型  haosl 2017-07-14
                	if(StringUtils.isBlank(
                			value)) {
                		values.add(null);
                	}else {
	                	int len = fi.getDecimalwidth();
	                	try {
	                		if(len > 0){
		                		double dvalue = Double.parseDouble(value);
		                		values.add(dvalue);
		                	}else {
		                		int intvalue = Integer.parseInt(value);
		                		values.add(intvalue);
		                	}
						} catch (Exception e) {
							//不数字时不保存
							return;
						}
	                	
                	}
                }else if("D".equals(itemType)){
					Date date = null;
					if(StringUtils.isNotBlank(value)){
						value = value.toString().replaceAll("-","\\.");
						String patten = "yyyy.MM.dd";
						if(fi.getItemlength()==18){
                            patten = "yyyy.MM.dd HH:mm:ss";
                        }else if(fi.getItemlength()==16){
                            patten = "yyyy.MM.dd HH:mm";
                        }else if(fi.getItemlength()==7){
                            patten = "yyyy.MM";
                        }else if(fi.getItemlength()==4){
                            patten = "yyyy";
                        }
					    date = DateUtils.getTimestamp(value.toString(),patten);
					}
					values.add(date);
				}else {
                	values.add(value);
                }
				 values.add(scheme_id);
				 values.add(kq_duration);
				 values.add(kq_year);
				 values.add(guidkey);
				 values.add(orgId+"%");
				 String sql = "update Q35 set "+item_id+"=? where scheme_id=? and kq_duration=? and kq_year=? and guidkey=? and org_id like ?";
				 sqls.add(sql);
				 valusList.add(values);
				 //待确认时，待办置为已办 45796 由于之前优化确认后可以再次更改日明细规则这里没有同步更改
				 if("confirm".equalsIgnoreCase(item_id) && "2".equals(value)) {
					//获得机构人员
					KqDataAppealMainService appealService = new KqDataAppealMainServiceImpl(userView, conn);
					a01Map = appealService.getA01Items(Integer.parseInt(scheme_id), kq_year, kq_duration, orgId);;
					if(a01Map.containsKey(guidkey)) {
						LazyDynaBean a01Bean = (LazyDynaBean) a01Map.get(guidkey);
						if(a01Bean!=null) {
							 String nbase = (String) a01Bean.get("nbase");
				            String receiver = nbase.toUpperCase().substring(0, 1) + nbase.toLowerCase().substring(1) + (String) a01Bean.get("a0100");
				            recerverList.add(receiver);
						}
					}
				 }
			}
			//批量更新Q35表数据
			dao.batchUpdate(sqls, valusList);
			//将确认待办置为已办
			if(recerverList.size()>0) {
				 KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
			     kqDataUtil.kqFinishPengdingTask(this.conn, Integer.parseInt(scheme_id), kq_year, kq_duration, KqDataUtil.TASKTYPE_CONFIRM
			    		 , KqDataUtil.role_Agency_Clerk, recerverList, null);
			}
		} catch (Exception e) {
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 
	 * @param scheme_id 方案id
	 * @return 在Q35中要忽略显示的字段   返回格式 ",xxxx,xx,xxx,";
	 * @throws Exception
	 */
	private String ignoreFielditemid(String scheme_id){
		ContentDAO dao = new ContentDAO(this.conn);
		String ingoreField = ",";
		RowSet rs = null;
		try {
			String sql = "select item_ids from kq_scheme where scheme_id=?";
			ArrayList<String> values = new ArrayList<String>();
			values.add(scheme_id);
			rs = dao.search(sql,values);
			
			// 50802 如果没查到 说明一个项目也没选，则需要全部过滤掉
			values.clear();
			sql = "SELECT fielditemid FROM kq_item ";
			
			if(rs.next()) {
				String item_ids = rs.getString("item_ids");
				if(StringUtils.isNotBlank(item_ids)) {
					values.clear();
					String[] ids = item_ids.split(",");
					// 51105 直接通过itemid查 即可 不知为何多一步 SELECT fielditemid FROM kq_item WHERE fielditemid NOT IN (  )
					sql = "SELECT fielditemid FROM kq_item WHERE item_id NOT IN (";
					int i = 0;
					for(String id : ids) {
						if(i>0)
							sql+=",";
						sql += "?";
						values.add(id);
						i++;
					}
					sql += ")";
				}
			}
			// 查询方案里未选中的考勤项目
			rs = dao.search(sql, values);
			while(rs.next()) {
				String fielditemid = rs.getString("fielditemid");
				if(StringUtils.isNotBlank(fielditemid))
					ingoreField += fielditemid.toLowerCase()+",";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return ingoreField;
	}

	@Override
    public Map getChangeStaffs(String scheme_id, String kq_year, String kq_duration, String orgId, int limit, int page
			, String type, LazyDynaBean shemeBean) throws GeneralException{
        Map returnMap = new HashMap();
        RowSet rowSet  = null;
        try {
        	Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(conn);
        	int totalCount = 0;
        	List<LazyDynaBean> beans = new ArrayList<LazyDynaBean>();
        	KqDataUtil kqDataUtil = new KqDataUtil(userView);
        	ContentDAO dao = new ContentDAO(conn);
//            ArrayList org_scopeList = schemeMainService.getFillingAgencysTree(scheme_id);
        	if(null == shemeBean) {
	        	SchemeMainService schemeMainService = new SchemeMainServiceImpl(conn, userView);
	            ArrayList parameterList = new ArrayList();
	            parameterList.add(scheme_id);
	            ArrayList<LazyDynaBean> shemeBeanlist = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
	            shemeBean = shemeBeanlist.get(0);
        	}
            String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
            String cond = "Null".equalsIgnoreCase(String.valueOf(shemeBean.get("cond"))) ? "" : String.valueOf(shemeBean.get("cond"));
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");//是否定义唯一性指标 0：没定义
            String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//唯一性指标值
            // 考勤期间开始结束日期
            LazyDynaBean dateBean = kqDataUtil.getDatesByKqDuration(conn, kq_year, kq_duration);
            String kqStartDateStr = DateUtils.format((Date) dateBean.get("kq_start"), "yyyy-MM-dd");
            String kqEndDateStr = DateUtils.format((Date) dateBean.get("kq_end"), "yyyy-MM-dd");
//            String kqDateStr = kq_year+"-"+kq_duration;
            
            StringBuffer strSql = new StringBuffer();
            strSql.append("select a0000,$nbase$,b0110,e0122,e01a1,a0101,guidkey,");
            if (StringUtils.isNotBlank(onlyname)) {
                strSql.append("$table$.").append(onlyname).append(" as only_field");
            }else{
                strSql.append("''").append(" as only_field");
            }

            // 考勤开始结束日期 > “轮岗部门”>考勤参数之“考勤部门”>人员所在部门（E0122）
            KqPrivForHospitalUtil kqPrivForHospitalUtil = new KqPrivForHospitalUtil(this.userView, this.conn);
            // 优先级1 是否配置考勤开始结束日期
//            boolean hasStartEndDate = false;
            // 考勤开始日期
            String kqStartDateField = kqPrivForHospitalUtil.getKqStartDateField();
            // 考勤结束日期
            String kqEndDateField = kqPrivForHospitalUtil.getKqEndDateField();
            
            KqUtil kqUtil = new KqUtil(this.conn);
    		// 考勤开始日期 所在表
    		String startSet = kqUtil.getTableNameByFieldName(kqStartDateField);
            // 考勤结束日期 所在表
            String endSet = kqUtil.getTableNameByFieldName(kqEndDateField);
            
            boolean hasStartDate = false;
            if(StringUtils.isNotBlank(kqStartDateField) && StringUtils.isNotBlank(startSet))
            	hasStartDate = true;
            boolean hasEndDate = false;
            if(StringUtils.isNotBlank(kqEndDateField) && StringUtils.isNotBlank(endSet))
            	hasEndDate = true;
            
            //优先级2 考勤部门变动子集职中设置的轮岗部门
            //轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            //轮岗结束时间
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept))
                hasChangeSet = true;
            
            //优先级3.考勤归属部门
            String dept_org = kqPrivForHospitalUtil.getKqDeptField();
            StringBuffer orgSql = new StringBuffer();
            StringBuffer q35OrgSql = new StringBuffer();
            //变动部门sql
            StringBuffer changOrgSql = new StringBuffer();
            ArrayList<String> orgsqls = new ArrayList<String>();
            if(StringUtils.isNotBlank(orgId)){
            	orgsqls.add("'"+orgId+"%'");
                if(hasChangeSet){
                    changOrgSql.append("or cset."+changDept+" like '").append(orgId).append("%'");
                }
                q35OrgSql.append(" org_id like '"+orgId+"%' ");
                CodeItem unCode = AdminCode.getCode("UN",orgId);
                //没有设置考勤归属部门，则只考虑自身的单位或者部门
                if(StringUtils.isBlank(dept_org)) {
                    if (unCode != null) {
                        orgSql.append("or (B0110 like '").append(orgId).append("%' ");
                        orgSql.append("or E0122 like '").append(orgId).append("%' ) ");
                    } else {
                        orgSql.append("or E0122 like '").append(orgId).append("%' ");
                    }
                }else{
                    //如果配置了考勤归属部门，增减人员则需要考虑人员子集中维护了考勤机构的人员
                    if (unCode != null) {
                        orgSql.append("or (" + dept_org + " like '" + orgId + "%') ");
                        orgSql.append("or ( (" + dept_org + " is null or "+ dept_org +"='') and ");
                        orgSql.append("(B0110 like '").append(orgId).append("%' ");
                        orgSql.append("or E0122 like '").append(orgId).append("%' )) ");
                    } else {
                        orgSql.append("or (" + dept_org + " like '" + orgId + "%') ");
                        orgSql.append("or ( (" + dept_org + " is null or "+ dept_org +"='') and ");
                        orgSql.append("E0122 like '").append(orgId).append("%') ");
                    }
                }
            }else{
            	ArrayList org_scopeList = (ArrayList)shemeBean.get("org_map");
                for (int i = 0; i < org_scopeList.size(); i++) {
                    HashMap map = (HashMap) org_scopeList.get(i);
                    String org_id = String.valueOf(map.get("y_org_id"));
                    orgsqls.add("'"+org_id.substring(2)+"%'");
                    if(hasChangeSet){
                        changOrgSql.append("or cset."+changDept+" like '").append(org_id.substring(2)).append("%'");
                    }
                    //如果存在考勤归属部门 则按照 归属部门-部门-单位 的顺序取值
                    if(i==0){
                        q35OrgSql.append(" (org_id like '"+org_id+"%'");
                    }else{
                        q35OrgSql.append(" or org_id like '"+org_id+"%'");
                    }
                    //没有设置考勤归属部门，则只考虑自身的单位或者部门
                    if(StringUtils.isBlank(dept_org)) {
                        if ("un".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or (B0110 like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append("or E0122 like '").append(org_id.substring(2)).append("%' ) ");
                            orgSql.append(" ) ");
                        } else if ("um".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or E0122 like '").append(org_id.substring(2)).append("%' ");
                        }
                    }else{
                        //如果配置了考勤归属部门，增减人员则需要考虑人员子集中维护了考勤机构的人员
                        if ("un".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or ("+dept_org+" like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append("or ( (" + dept_org + " is null or "+ dept_org +"='') and ");
                            orgSql.append(" (B0110 like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append("or E0122 like '").append(org_id.substring(2)).append("%' ) ");
                            orgSql.append(" )) ");
                        } else if ("um".equalsIgnoreCase(org_id.substring(0, 2))) {
                            orgSql.append("or ("+dept_org+" like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append("or ( (" + dept_org + " is null or "+ dept_org +"='') and ");
                            orgSql.append("E0122 like '").append(org_id.substring(2)).append("%' ");
                            orgSql.append(" )) ");
                        }
                    }
                }
                if(q35OrgSql.length()>0){
                    q35OrgSql.append(")");
                }
            }
            StringBuffer sql = new StringBuffer();
            StringBuffer finalSql = new StringBuffer();

            List values = new ArrayList();
            values.add(scheme_id);
            values.add(kq_year);
            values.add(kq_duration);
            String sqls = "";
            // 46367 考勤部门 或 方案限制条件SQL
            String condWhere = "";
            StringBuffer condwhereIN = new StringBuffer();
            StringBuffer hasChangeWheresql = new StringBuffer();
            boolean isOracl = (Sql_switcher.searchDbServer() == Constant.ORACEL);
            for (String dbName : dbNameList) {
            	String tableName = dbName + "A01";
                sql.setLength(0);
                finalSql.setLength(0);
                condwhereIN.setLength(0);
                condWhere = "";
                // 变动子集的条件加入计算公式
                String hasChangeSetsql = "";
                if(hasChangeSet) {
                	hasChangeSetsql = " or "+tableName+".a0100 in (SELECT a0100 FROM "+dbName+changSetId+" z1 ";
                	hasChangeSetsql += " where ("+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM-dd")+"<='"+kqStartDateStr+"'"
            					+ " and "+Sql_switcher.dateToChar("z1."+changeEndField,"yyyy-MM-dd")+">='"+kqEndDateStr+"' "
            					+" or ((z1."+changeEndField+" is null or z1."+changeEndField+"='')"
                    			+ " and "+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"')"
                    			+ " or ("+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    			+ " and "+Sql_switcher.dateToChar("z1."+changeEndField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"')"
            					+ " or ("+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    			+ " and "+Sql_switcher.dateToChar("z1."+changeStartField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"')"
            					+ " or ("+Sql_switcher.dateToChar("z1."+changeEndField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    			+ " and "+Sql_switcher.dateToChar("z1."+changeEndField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"')"
            					+ ") ";
                	String changDeptsqlWhere = "";
            		for(int i=0;i<orgsqls.size();i++) {
            			changDeptsqlWhere += "or z1."+changDept+" LIKE "+orgsqls.get(i);
            		}
                	hasChangeSetsql += " AND ("+changDeptsqlWhere.substring(2)+" ))";
                }
                sql.append(strSql.toString().replace("$nbase$","'"+dbName+"' as nbase").replace("$table$", tableName));
                sql.append(" from ").append(tableName);
                //有变动子集的数据时，优先按照变动部门创建考勤数据
                if(hasChangeSet){
                    sql.append(" left join (select a0100,max("+changDept+") as "+changDept+" from "+dbName+changSetId+" cset");
                    sql.append(" where (");
                    sql.append(Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+"<='"+kqStartDateStr+"'"
                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM-dd")+">='"+kqEndDateStr+"' ");
                    sql.append(" or (("+changeEndField+" is null or "+changeEndField+"='')"
                    		+ " and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"') ");
                    sql.append(" or ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"') ");
                    sql.append(" or ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    		+ " and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"') ");
                    sql.append(" or ("+Sql_switcher.dateToChar(changeEndField,"yyyy-MM-dd")+">='"+kqStartDateStr+"'"
                    		+ " and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM-dd")+"<='"+kqEndDateStr+"') ");
                    sql.append(") ");
                    sql.append("and ("+changOrgSql.toString().substring(2)+")");
                    sql.append("group by a0100) cset ");
                    sql.append("on cset.a0100="+tableName+".a0100");
                    sql.append(" where (("+changOrgSql.toString().substring(2));
                    sql.append(") or ( ("+changDept+" is null or "+changDept+"='')");
                }else{
                    sql.append(" where 1=1 ");
                }
                
                // 部门条件
                sql.append(" And ("+orgSql.toString().substring(2)+ ")");
                
                if(hasChangeSet){
                	sql.append(" and not exists (");
                	sql.append("select 1 from "+dbName+changSetId +" z");
                	sql.append(" where z.A0100="+tableName+".a0100");
                	sql.append(" and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM-dd")+"<'"+kqStartDateStr+"' "
                			+ "and ("+Sql_switcher.dateToChar(changeEndField,"yyyy-MM-dd")+">'"+kqEndDateStr+"' ");
                	sql.append("or "+changeEndField+" is null or "+changeEndField+"='')");
                	sql.append(")");
                	
                	sql.append("))");
                }
                // 方案限制条件
                if (StringUtils.isNotBlank(cond)) {
                	condwhereIN.append("select "+tableName+".a0100 From ").append(dbName).append("A01");
                	condwhereIN.append(" where 1=1 ").append(" And ("+orgSql.toString().substring(2)+ ")" + hasChangeSetsql);
                	
                	condWhere = kqDataUtil.getComplexCondSql(conn, cond, dbName, condwhereIN.toString());
                	sql.append(condWhere);
                }
                // 考勤开始结束时间校验
                if(hasStartDate || hasEndDate) {
                	sql.append(" and "+tableName+".A0100 not in (");
                	if(hasStartDate) {
                		sql.append("(select C.A0100 from " +dbName+startSet+ " C"
                				+ " WHERE "+Sql_switcher.dateToChar("C."+kqStartDateField, "yyyy-MM-dd")+ ">'"+kqEndDateStr+"'");
        				if (!"a01".equalsIgnoreCase(startSet))
                            sql.append(" and C.I9999=(select max(i9999) from " + dbName+startSet + " A WHERE C.A0100=A.A0100)");
        				sql.append(")");
                	}
                	if(hasStartDate && hasEndDate)
                		sql.append(" UNION ");
                	if(hasEndDate) {
                		sql.append("(select D.A0100 from " +dbName+endSet+ " D"
                				+ " WHERE "+Sql_switcher.dateToChar("D."+kqEndDateField, "yyyy-MM-dd")+ "<'"+kqStartDateStr+"'");
        				if (!"a01".equalsIgnoreCase(endSet))
                            sql.append(" and D.I9999=(select max(i9999) from " + dbName+endSet + " A WHERE D.A0100=A.A0100)");
        				sql.append(")");
                	}
                	sql.append(")");
                }
                
                //新增人员数据
                if("add".equals(type)){
                    finalSql.append("select a1.*{dbname} from (").append(sql).append(") a1 ");
                    finalSql.append("{leftjoin} where not exists (select guidkey from Q35 a2 where a1.guidkey=a2.guidkey ");
                    finalSql.append("and a2.scheme_id=? and a2.kq_year=? and a2.kq_duration=? and ");
                    finalSql.append(q35OrgSql+")");
                    //查询总条数
                    sqls = "select count(1) as totalCount from ("+finalSql.toString().replace("{dbname}","").replace("{leftjoin}","")+") c";
                    rowSet = dao.search(sqls, values);
                    if(rowSet.next()){
                        int count =  rowSet.getInt("totalCount");
                        totalCount +=count;
                    }
                    if(limit==0 && page==0)
                        continue;
                    finalSql.append(" order by a1.a0000 asc");
                    sqls = finalSql.toString().replace("{dbname}",",dbname").replace("{leftjoin}"," left join dbname on lower(dbname.pre) = lower(a1.nbase)");
                    rowSet = dao.search(sqls, values);
                    LazyDynaBean bean = null;
                    while(rowSet.next()){
                        bean = new LazyDynaBean();
                        bean.set("nbase", KqDataUtil.nullif(rowSet.getString("nbase")));
                        bean.set("dbname", KqDataUtil.nullif(rowSet.getString("dbname")));
                        String b0110 = KqDataUtil.nullif(rowSet.getString("b0110"));
                        String e0122 = KqDataUtil.nullif(rowSet.getString("e0122"));
                        String e01a1 = KqDataUtil.nullif(rowSet.getString("e01a1"));
                        String unitName = "";
                        if(StringUtils.isNotBlank(b0110)){
                            unitName = AdminCode.getCodeName("UN",b0110);
                        }
                        String e0122Name = "";
                        if(StringUtils.isNotBlank(e0122)){
                            e0122Name = AdminCode.getCodeName("UM",e0122);
                        }
                        String e01a1Name = "";
                        if(StringUtils.isNotBlank(e01a1)){
                            e01a1Name = AdminCode.getCodeName("@K",e01a1);
                        }
                        bean.set("b0110", unitName);
                        bean.set("e0122", e0122Name);
                        bean.set("e01a1", e01a1Name);
                        bean.set("a0101", KqDataUtil.nullif(rowSet.getString("a0101")));
                        bean.set("guidkey", KqDataUtil.nullif(rowSet.getString("guidkey")));
                        if (StringUtils.isNotBlank(onlyname)) {
                            bean.set("only_field", KqDataUtil.nullif(rowSet.getString("only_field")));
                        }
                        //新增人员数据
                        beans.add(bean);
                    }
                }
                //减员数据
                else if("del".equals(type)){
                    finalSql.append("select a0000,'"+dbName+"' as nbase,b0110,e0122,e01a1,a0101,guidkey,");
                    if (StringUtils.isNotBlank(onlyname)) {
                        finalSql.append("persons.").append(onlyname).append(" as only_field");
                    }else{
                        finalSql.append("''").append(" as only_field");
                    }
                    finalSql.append(" {leftjoin} where persons.guidkey in (");
                    finalSql.append("select guidkey from Q35 where ");
                    finalSql.append(q35OrgSql);
                    finalSql.append(" and scheme_id=? and kq_year=? and kq_duration=? and not exists(");
                    finalSql.append("select a1.guidkey from (").append(sql).append(") a1 where Q35.guidkey=a1.guidkey)");
                    finalSql.append(")");
                    //查询总条数
                    sqls = "select count(1) as totalCount from ("+finalSql.toString().replace("{leftjoin}"," from "+dbName+"A01 persons")+") c";
                    rowSet = dao.search(sqls, values);
                    if(rowSet.next()){
                        int count =  rowSet.getInt("totalCount");
                        totalCount +=count;
                    }
                    if(limit==0 && page==0)
                        continue;
                    finalSql.append(" order by persons.a0000 asc");
                    sqls = finalSql.toString().replace("{leftjoin}",",dbname from "+dbName+"A01 persons "
                    		+ "left join dbname on lower(dbname.pre) = '"+dbName.toLowerCase()+"'");
                    rowSet = dao.search(sqls, values);
                    LazyDynaBean bean = null;
                    while(rowSet.next()){
                        bean = new LazyDynaBean();
                        bean.set("nbase", KqDataUtil.nullif(rowSet.getString("nbase")));
                        bean.set("dbname", KqDataUtil.nullif(rowSet.getString("dbname")));
                        String b0110 = KqDataUtil.nullif(rowSet.getString("b0110"));
                        String e0122 = KqDataUtil.nullif(rowSet.getString("e0122"));
                        String e01a1 = KqDataUtil.nullif(rowSet.getString("e01a1"));
                        String unitName = "";
                        if(StringUtils.isNotBlank(b0110)){
                            unitName = AdminCode.getCodeName("UN",b0110);
                        }
                        String e0122Name = "";
                        if(StringUtils.isNotBlank(e0122)){
                            e0122Name = AdminCode.getCodeName("UM",e0122);
                        }
                        String e01a1Name = "";
                        if(StringUtils.isNotBlank(e01a1)){
                            e01a1Name = AdminCode.getCodeName("@K",e01a1);
                        }
                        bean.set("b0110", unitName);
                        bean.set("e0122", e0122Name);
                        bean.set("e01a1",e01a1Name);
                        bean.set("a0101", KqDataUtil.nullif(rowSet.getString("a0101")));
                        bean.set("guidkey", KqDataUtil.nullif(rowSet.getString("guidkey")));
                        if (StringUtils.isNotBlank(onlyname)) {
                            bean.set("only_field", KqDataUtil.nullif(rowSet.getString("only_field")));
                        }
                        //减少人员数据
                        beans.add(bean);
                    }
                }
            }
            returnMap.put("totalCount",totalCount);
            returnMap.put("staffs",beans.subList((page-1)*limit,page*limit>beans.size()?beans.size():page*limit));
        } catch (Exception e) {
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rowSet);
        }
        return returnMap;
    }

   @Override
   public void changeStaffs (String scheme_id, String kq_year, String kq_duration, List<String> guidkeys, String opration, String orgId) throws GeneralException{
       ContentDAO dao = new ContentDAO(this.conn);
        try {
            SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.conn, this.userView);
            Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "");
            LazyDynaBean shemeBean=shemeBeanList.get(0);

            StringBuffer sql = new StringBuffer();
            List values = new ArrayList();
            //考勤归属部门
            String dept_org="";
            KqPrivForHospitalUtil kqPrivForHospitalUtil=new KqPrivForHospitalUtil(this.userView,this.conn);
            dept_org=kqPrivForHospitalUtil.getKqDeptField();
            //轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            //轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            //轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            //是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept)){
                hasChangeSet = true;
            }
            if("add".equals(opration)){
            	boolean isOracl = (Sql_switcher.searchDbServer() == Constant.ORACEL);
                sql.append(" insert into Q35(a0101,a0000,Kq_year,Kq_duration,Guidkey,Org_id,Scheme_id,B0110,E0122,E01a1,Only_field)");
                sql.append(" select a0101,$table$.a0000, ? as Kq_year,? as Kq_duration,$table$.Guidkey ,(CASE");
                //变动部门不为空时，设置变动部门下的数据
                if(hasChangeSet){
                    sql.append(" WHEN (cset."+changDept+" is not null"+(isOracl?"":(" and cset."+changDept+"<>''"))+") THEN cset."+changDept+" ");
                }
                if(StringUtils.isNotBlank(dept_org)){
                	sql.append(" WHEN ($table$."+dept_org+" is not null"+(isOracl?"":(" and $table$."+dept_org+"<>''"))+") then $table$."+dept_org
                			+" WHEN ($table$.E0122 is null or $table$.E0122='') THEN $table$.B0110 ELSE $table$.E0122 END) as Org_id,");
                	
                } else{
                    sql.append(" WHEN ($table$.E0122 is null or $table$.E0122='') THEN $table$.B0110 ELSE $table$.E0122 END) as Org_id,");
                }
                sql.append("? as Scheme_id,$table$.B0110,$table$.E0122,$table$.E01a1,");
                String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
                String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");//是否定义唯一性指标 0：没定义
                String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");//唯一性指标值
                if (StringUtils.isNotBlank(onlyname)) {
                    sql.append("$table$.").append(onlyname).append(" as Only_field ");
                } else {
                    sql.append("null").append(" as Only_field ");
                }
                String kqDuration = kq_year+"-"+kq_duration;
                // 新增时校验变动子集条件
                StringBuffer changeSql = new StringBuffer();
                changeSql.append(" where "+changDept+" like '"+orgId+"%'");
                changeSql.append(" and ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDuration+"'"
                		+" and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+">='"+kqDuration+"'");
                changeSql.append(" or (("+changeEndField+" is null or "+changeEndField+"='') "
                		+ " and "+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+"<='"+kqDuration+"')");
                changeSql.append(" or ("+Sql_switcher.dateToChar(changeStartField,"yyyy-MM")+">='"+kqDuration+"'"
                		+" and "+Sql_switcher.dateToChar(changeEndField,"yyyy-MM")+"<='"+kqDuration+"')");
                changeSql.append(")");
                changeSql.append(" group by a0100,"+changDept);
                for (String dbName : dbNameList) {
                    String tableName = dbName + "A01";
                    StringBuffer sqlNew = new StringBuffer();
                    sqlNew.append(sql.toString().replace("$table$", tableName));
                    sqlNew.append(" from ").append(tableName);
                    if(hasChangeSet){
                        sqlNew.append(" left join (SELECT a0100,"+changDept+" FROM ");
                        sqlNew.append(dbName+changSetId+ changeSql.toString() +")");
                        sqlNew.append(" cset on cset.a0100="+tableName+".a0100 ");
                    }
                    sqlNew.append(" where guidkey in(");
                    values.clear();
                    values.add(kq_year);
                    values.add(kq_duration);
                    values.add(scheme_id);
                    for(int i=0;i<guidkeys.size();i++){
                        String guidkey = guidkeys.get(i);
                        if(i>0){
                            sqlNew.append(",");
                        }
                        sqlNew.append("?");
                        values.add(guidkey);
                    }
                    sqlNew.append(")");
                    dao.update(sqlNew.toString(),values);

                   /* //更新所属机构
                    values.clear();
                    values.add(kq_year);
                    values.add(kq_duration);
                    values.add(scheme_id);
                  //  for (int i = 0; i < org_scopeList.size(); i++) {
                    //HashMap map = (HashMap) org_scopeList.get(i);
                  //  String org_id = String.valueOf(map.get("org_id"));
                    dao.update("update q35 set org_id='" + orgId + "' where  Kq_year=? and Kq_duration=? and scheme_id=? and org_id like '" 
                    + orgId + "%' ", values);
                    //}*/
                }
                //当有变动子集时需要同步新增的变动人员的历史明细数据
                if(hasChangeSet){
                    KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
                    kqDataUtil.syncchangeDeptKqData(this.conn,kq_year,kq_duration,scheme_id,guidkeys);
                }
            }
            else if("del".equals(opration)){
                values.clear();
                values.add(orgId+"%");
                values.add(scheme_id);
                values.add(kq_year);
                values.add(kq_duration);
                StringBuffer guidkeySql = new StringBuffer();
                List guidkeyVals = new ArrayList();
                sql.append("delete from Q35 where org_id like ? and Scheme_id=? and Kq_year=? and Kq_duration=? and ");
                guidkeySql.append("guidkey in (");
                for(int i=0;i<guidkeys.size();i++){
                    String guidkey = guidkeys.get(i);
                    if(i>0){
                        guidkeySql.append(",");
                    }
                    guidkeySql.append("?");
                    guidkeyVals.add(guidkey);
                }
                values.addAll(guidkeyVals);
                guidkeySql.append(")");
                sql.append(guidkeySql);
                dao.delete(sql.toString(),values);

                //删除日考勤打卡数据表的相关数据
                PeriodService periodService = new PeriodServiceImpl(userView, conn);
                parameterList.clear();
                parameterList.add(kq_year);
                parameterList.add(kq_duration);
                ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
                LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
                sql.setLength(0);
                if(periodBean!=null) {
                    Date kqStart = (Date) periodBean.get("kq_start");
                    Date kqEnd = (Date) periodBean.get("kq_end");
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    sql.append("delete from kq_day_detail where Scheme_id=? and ");
                    sql.append(Sql_switcher.dateToChar("kq_date","yyyy-MM-dd")+">=? and ");
                    sql.append(Sql_switcher.dateToChar("kq_date","yyyy-MM-dd")+"<=? and ");
                    sql.append(guidkeySql);
                    values.clear();
                    values.add(scheme_id);
                    values.add(simpleDateFormat.format(kqStart));
                    values.add(simpleDateFormat.format(kqEnd));
                    values.addAll(guidkeyVals);
                    dao.delete(sql.toString(),values);
                }

            }
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
   }

    /**
     * 考勤明细界面是手工读取公有栏目设置，表格控件的列动保存列宽时默认保存到了私有方案，
     * 这样页面就读不到改变后的列宽，所以需要在进页面发现有私有方案则更新成公有方案。
     * @param subModuleId
     */
    private void setSchemePulic(String subModuleId){
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {

            StringBuffer sql = new StringBuffer();
            sql.append("select scheme_id from t_sys_table_scheme where submoduleid like ? and is_share=0 order by scheme_id desc");
            List values = new ArrayList();
            values.add(subModuleId+"%");
            rs = dao.search(sql.toString(),values);
            int scheme_id = -1;
            values.clear();
            if(rs.next()) {
                scheme_id = rs.getInt("scheme_id");
                values.add(scheme_id);
            }

            if(values.size()>0) {
                sql.setLength(0);
                sql.append("update t_sys_table_scheme set is_share=1,submoduleid=submoduleid"+Sql_switcher.concat()+"'_onlysave' where scheme_id=?");
                dao.update(sql.toString(), values);
                //删除以前的栏目设置
                sql.setLength(0);
                values.clear();
                values.add(subModuleId+"%");
                values.add(scheme_id);
                sql.append("delete from t_sys_table_scheme_item where scheme_id in (select scheme_id from t_sys_table_scheme where submoduleid like ? and scheme_id<> ?)");
                dao.delete(sql.toString(),values);
                sql.setLength(0);
                sql.append("delete from t_sys_table_scheme where submoduleid like ? and scheme_id<> ?");
                dao.delete(sql.toString(),values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            PubFunc.closeDbObj(rs);
        }
    }

    @Override
    public Map<String,List<String>> searchChangePerData(String kq_year, String kq_duration, String org_id, String scheme_id) throws GeneralException {
        Map<String,List<String>> map = new HashMap<String,List<String>>();
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.conn, this.userView);
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            KqPrivForHospitalUtil kqPrivForHospitalUtil=new KqPrivForHospitalUtil(this.userView,this.conn);
            // 是否配置考勤开始结束日期
            // 考勤开始日期
            String kqStartDateField = kqPrivForHospitalUtil.getKqStartDateField();
            boolean hasStartDate = StringUtils.isNotBlank(kqStartDateField);
            // 考勤结束日期
            String kqEndDateField = kqPrivForHospitalUtil.getKqEndDateField();
            boolean hasEndDate = StringUtils.isNotBlank(kqEndDateField);
            
            // 考勤部门变动子集职中设置的轮岗部门
            // 轮岗子集
            String changSetId = kqPrivForHospitalUtil.getKqChangeSetid();
            // 轮岗部门
            String changDept = kqPrivForHospitalUtil.getKqChangeDeptField();
            // 轮岗开始时间
            String changeStartField = kqPrivForHospitalUtil.getKqChangeStartField();
            // 轮岗结束时间
            String changeEndField = kqPrivForHospitalUtil.getKqChangeEndField();
            // 是否配置了变动子集的变动部门
            boolean hasChangeSet = false;
            if(StringUtils.isNotBlank(changSetId) && StringUtils.isNotBlank(changDept))
                hasChangeSet = true;
            
            // 如果都没有配置则直接返回
            if(!hasStartDate && !hasEndDate && !hasChangeSet){
            	return map;
            }
            
            ArrayList parameterList = new ArrayList();
            parameterList.add(scheme_id);
            ArrayList<LazyDynaBean> shemeBeanList = schemeMainService.listKq_scheme("And scheme_id=? ", parameterList, "",kq_year,kq_duration);
            LazyDynaBean shemeBean = shemeBeanList.get(0);
            String[] dbNameList = String.valueOf(shemeBean.get("cbase")).split(",");
            
            PeriodService periodService = new PeriodServiceImpl(userView, conn);
            parameterList.clear();
            parameterList.add(kq_year);
            parameterList.add(kq_duration);
            ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
            LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
            //考勤期间开始时间
            Date kqStart = null;
            //考勤期间结束时间
            Date kqEnd = null;
            Calendar kqStartCal =Calendar.getInstance();
            Calendar kqEndCal =Calendar.getInstance();
            if(periodBean!=null) {

                kqStart = (Date) periodBean.get("kq_start");
                kqEnd = (Date) periodBean.get("kq_end");
                kqStartCal.setTime(kqStart);
                kqEndCal.setTime(kqEnd);
            }
            ArrayList<String> allDays = new ArrayList<String>();
            // 55744 应根据期间的开始结束时间获取全部天数
            int alldays = DateUtils.dayDiff(kqStart, kqEnd);
            for(int i=1; i<= alldays+1;i++){
                String temp = "q35"+(i<10?"0"+i:i+"");
                allDays.add(temp);
            }
            // 若没有变动子集配置，有考勤开始结束日期  则只取开始结束日期范围内
            if(!hasChangeSet && (hasStartDate || hasEndDate)) {
            	map = searchKqDatePerData(kqStartDateField, kqEndDateField, org_id, dbNameList, allDays, periodBean, map);
            	return map;
            }
            
            String kqStartStr = DateUtils.format(kqStart, "yyyy-MM-dd");
            String kqEndStr = DateUtils.format(kqEnd, "yyyy-MM-dd");
            //1.查询变动岗位子集中对应记录
            StringBuffer qSql = new StringBuffer();
            qSql.append("select t1.guidkey,t2.*,");
            /**
             * 说明将changeflag降序排列的意思是优先匹配有变动子集数据，用来控制日明细列的读取权限
             */
            qSql.append("CASE WHEN "+changDept+" like ? THEN 2 ELSE 1 END AS changeflag");
            qSql.append(" from (SELECT $dbname$A01.A0100,$dbname$A01.GUIDKEY FROM Q35,");
            qSql.append("$dbname$A01 WHERE Q35.guidkey = $dbname$A01.GUIDKEY AND kq_year =?");
            qSql.append(" AND kq_duration=? AND scheme_id=? AND org_id LIKE ?");
            qSql.append(") t1 ");
            qSql.append(" left join $dbname$"+changSetId+" t2  on t1.a0100=t2.a0100 ");
            // 45851 日期校验错误
            qSql.append(" and (("+Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM-dd")+"<=?");
            	qSql.append(" and "+Sql_switcher.dateToChar("t2."+changeEndField,"yyyy-MM-dd")+">=?)");
            	qSql.append(" or ((t2."+changeEndField+" is null or t2."+changeEndField+"='')and "
            			+ Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM-dd")+"<=?)");
            	qSql.append(" or ("+Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM-dd")+">=?"
            			+ " and "+Sql_switcher.dateToChar("t2."+changeStartField,"yyyy-MM-dd")+"<=?)");
            	qSql.append(" or ("+Sql_switcher.dateToChar("t2."+changeEndField,"yyyy-MM-dd")+">=?"
            			+ " and "+Sql_switcher.dateToChar("t2."+changeEndField,"yyyy-MM-dd")+"<=?)");
            	qSql.append(")");
            qSql.append(" order by t2.a0100,changeflag DESC");
            List values = new ArrayList();
            values.add(org_id+"%");
            values.add(kq_year);
            values.add(kq_duration);
            values.add(scheme_id);
            values.add(org_id+"%");
            values.add(kqStartStr);
            values.add(kqEndStr);
            values.add(kqEndStr);
            values.add(kqStartStr);
            values.add(kqEndStr);
            values.add(kqStartStr);
            values.add(kqEndStr);
            
            String sql = "";
            for (String dbName : dbNameList) {
            	sql = qSql.toString().replaceAll("\\$dbname\\$",dbName);
                rs = dao.search(sql, values);
                ArrayList<String> list = null;
                while(rs.next()){
                	// 如果a0100 为null 证明此人没有变动部门信息
                	String a0100 = rs.getString("a0100");
                	if(StringUtils.isEmpty(a0100))
                		continue;
                    String guidkey = rs.getString("guidkey");
                    String chengOrgId = rs.getString(changDept);
                    // 55263 轮岗开始结束时间不需要考虑时分秒  直接date获取就可以
                    Date startD = rs.getDate(changeStartField);
                    Date endD = rs.getDate(changeEndField);
                    Calendar startCal =Calendar.getInstance();
                    Calendar endCal =Calendar.getInstance();
                    if(!chengOrgId.startsWith(org_id)){
                        if(!map.containsKey(guidkey)){
                            list = new ArrayList(allDays);
                            map.put(guidkey,list);
                        }else{
                            list = (ArrayList<String>)map.get(guidkey);
                        }
                        if(startD!=null && kqStart!=null && kqEnd!=null){
                            //判断变动岗位的起始时间是否在考勤起始时间范围内
                            if(startD.before(kqStart)){
                                startD = kqStart;
                            }
                            if(endD == null || endD.after(kqEnd)){
                                endD = kqEnd;
                            }
                            if(startD.after(endD)){
                                continue;
                            }
                            startCal.setTime(startD);
                            endCal.setTime(endD);
                            //得到变动岗开始时间与考勤期间的开始时间相差的天数
                            int days = DateUtils.dayDiff(kqStart,startD);
                            for(int i=1;!startCal.after(endCal);startCal.add(Calendar.DATE, 1)) {
                                String temp = (i+days)<10 ? "0"+(i+days) : i+days+"";
                                list.remove("q35"+temp);
                                i++;
                            }
                        }
                    }else{
                        if(!map.containsKey(guidkey)){
                            list = new ArrayList();
                            map.put(guidkey,list);
                        }else{
                            list = (ArrayList<String>)map.get(guidkey);
                        }
                        if(startD!=null && kqStart!=null && kqEnd!=null){
                            //判断变动岗位的起始时间是否在考勤起始时间范围内
                            if(startD.before(kqStart)){
                                startD = kqStart;
                            }
                            if(endD == null || endD.after(kqEnd)){
                                endD = kqEnd;
                            }
                            if(startD.after(endD)){
                                continue;
                            }
                            startCal.setTime(startD);
                            endCal.setTime(endD);
                            //得到变动岗开始时间与考勤期间的开始时间相差的天数
                            int days = DateUtils.dayDiff(kqStart,startD);
                            for(int i=1;!startCal.after(endCal);startCal.add(Calendar.DATE, 1)) {
                                String temp = (i+days)<10 ? "0"+(i+days) : i+days+"";
                                list.add("q35"+temp);
                                i++;
                            }
                        }
                    }
                }
            }
            // 取考勤开始结束日期范围内
            if(hasStartDate || hasEndDate) {
            	map = searchKqDatePerData(kqStartDateField, kqEndDateField, org_id, dbNameList, allDays, periodBean, map);
            }
            
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }
    /**
     * 日明细可编辑范围  考勤开始结束时间校验
     * @param kqStartDateField	开始时间指标
     * @param kqEndDateField	结束时间指标
     * @param org_id			部门
     * @param dbNameList		考勤方案人员库
     * @param allDays			考勤期间内日期 指标
     * @param periodBean		期间开始结束时间
     * @param map				返回map
     * @return
     * @throws GeneralException
     * @date 2019年7月5日 下午2:45:19
     * @author linbz
     */
    public Map<String,List<String>> searchKqDatePerData(String kqStartDateField, String kqEndDateField, String org_id
    		, String[] dbNameList, ArrayList<String> allDays, LazyDynaBean periodBean, Map<String,List<String>> map) throws GeneralException {
        RowSet rs = null;
    	try {
    		if(null == periodBean) {
    			return map;
            }
    		//考勤期间开始时间
            Date kqStart = (Date) periodBean.get("kq_start");
            //考勤期间结束时间
            Date kqEnd = (Date) periodBean.get("kq_end");
    		String kqStartStr = DateUtils.format(kqStart, "yyyy-MM-dd");
    		String kqEndStr = DateUtils.format(kqEnd, "yyyy-MM-dd");
    		
    		KqUtil kqUtil = new KqUtil(this.conn);
    		// 考勤开始日期 所在表
    		String startSet = kqUtil.getTableNameByFieldName(kqStartDateField);
            // 考勤结束日期 所在表
            String endSet = kqUtil.getTableNameByFieldName(kqEndDateField);
            
            int alldays = DateUtils.dayDiff(kqStart ,kqEnd);
    		ContentDAO dao = new ContentDAO(this.conn);
    		StringBuffer sql = new StringBuffer("");
    		String sqlStr = "";
    		
    		// 考勤开始日期 校验
    		if(StringUtils.isNotBlank(startSet)) {
    			sql.append("select A.guidkey,"+kqStartDateField+" dateValue from $dbname$A01 A");
    			if (!"a01".equalsIgnoreCase(startSet)) {
    				sql.append(" left join $dbname$"+startSet+" C ");
    				sql.append(" on C.A0100=A.A0100");
//    				sql.append(" and A.I9999=(select max(i9999) from $dbname$" +startSet+ " D WHERE D.A0100=C.A0100)");
    			}
    			// 53344 基于map中数据筛选的，故这里不需要加部门条件了
//    			sql.append(" where (b0110 like '"+org_id+"%' or e0122 like '"+org_id+"%' or e01a1 like '"+org_id+"%') ");
    			sql.append(" where ");
//    			sql.append(" and ");
    			sql.append(" ("+ Sql_switcher.dateToChar(kqStartDateField,"yyyy-MM-dd")+">='"+kqStartStr+"' "
    					+ " and "+Sql_switcher.dateToChar(kqStartDateField,"yyyy-MM-dd")+"<='"+kqEndStr+"' "
    					+ " or "+Sql_switcher.dateToChar(kqStartDateField,"yyyy-MM-dd")+">'"+kqEndStr+"')");
    			if (!"a01".equalsIgnoreCase(startSet))
    				sql.append(" and C.I9999=(select max(i9999) from $dbname$" +startSet+ " D WHERE D.A0100=C.A0100)");
    			
    			for (String dbName : dbNameList) {
    				sqlStr = sql.toString().replaceAll("\\$dbname\\$", dbName);
    				rs = dao.search(sqlStr);
    				ArrayList<String> list = null;
    				while(rs.next()){
    					String guidkey = rs.getString("guidkey");
    					if(StringUtils.isBlank(guidkey))
    						continue;
    					Date dateValue = rs.getTimestamp("dateValue");
    					if(!map.containsKey(guidkey)){
    						list = new ArrayList(allDays);
    						map.put(guidkey,list);
    					}else{
    						list = (ArrayList<String>)map.get(guidkey);
    					}
    					
    					if(dateValue.after(kqEnd))
    						list.clear();
    					else {
    						// 56174 校验开始时间  
    						Calendar startCal =Calendar.getInstance();
    						startCal.setTime(kqStart);
    						Calendar endCal =Calendar.getInstance();
    						endCal.setTime(dateValue);
    						for(int i=1;startCal.before(endCal);startCal.add(Calendar.DATE, 1)) {
                                String temp = (i)<10 ? "0"+(i) : i+"";
                                list.remove("q35"+temp);
                                i++;
                            }
    					}
    				}
    			}
    		}
    		
    		// 考勤结束日期 校验
    		if(StringUtils.isNotBlank(endSet)) {
    			
    			sql.setLength(0);
    			sql.append("select A.guidkey,"+kqEndDateField+" dateValue from $dbname$A01 A");
    			if (!"a01".equalsIgnoreCase(endSet)) {
    				sql.append(" left join $dbname$"+endSet+" C ");
    				sql.append(" on C.A0100=A.A0100");
    			}
//    			sql.append(" where (b0110 like '"+org_id+"%' or e0122 like '"+org_id+"%' or e01a1 like '"+org_id+"%') ");
    			sql.append(" where ");
//    			sql.append(" and ");
    			sql.append(" ("+ Sql_switcher.dateToChar(kqEndDateField,"yyyy-MM-dd")+">='"+kqStartStr+"' "
    					+ " and "+Sql_switcher.dateToChar(kqEndDateField,"yyyy-MM-dd")+"<='"+kqEndStr+"' "
    					+ " or "+Sql_switcher.dateToChar(kqEndDateField,"yyyy-MM-dd")+"<'"+kqStartStr+"')");
    			if (!"a01".equalsIgnoreCase(endSet))
    				sql.append(" and C.I9999=(select max(i9999) from $dbname$" +endSet+ " D WHERE D.A0100=C.A0100)");
    			
    			for (String dbName : dbNameList) {
    				sqlStr = sql.toString().replaceAll("\\$dbname\\$", dbName);
    				rs = dao.search(sqlStr);
    				ArrayList<String> list = null;
    				while(rs.next()){
    					String guidkey = rs.getString("guidkey");
    					if(StringUtils.isBlank(guidkey))
    						continue;
    					Date dateValue = rs.getTimestamp("dateValue");
    					if(!map.containsKey(guidkey)){
    						list = new ArrayList(allDays);
    						map.put(guidkey,list);
    					}else{
    						list = (ArrayList<String>)map.get(guidkey);
    					}
    					
    					if(dateValue.before(kqStart)) {
    						list.clear();
    					}else {
    						// 56174 校验结束时间  
    						Calendar startCal =Calendar.getInstance();
    						startCal.setTime(dateValue);
    						Calendar endCal =Calendar.getInstance();
    						endCal.setTime(kqEnd);
    						// 56380 经讨论设置的结束时间 还是需要计算考勤的
    						int days = DateUtils.dayDiff(kqStart, dateValue) + 1;
    						for(int i=1;!startCal.after(endCal);startCal.add(Calendar.DATE, 1)) {
                                String temp = (i+days)<10 ? "0"+(i+days) : i+days+"";
                                list.remove("q35"+temp);
                                i++;
                            }
    					}
    				}
    			}
    		}
    		
    	}catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }finally {
            PubFunc.closeDbObj(rs);
        }
        return map;
    }
    @Override
    public void deletePersons(String scheme_id, String kq_year, String kq_duration, String orgId, List<String> guidkeys) throws GeneralException {
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            if(guidkeys.size()==0)
                return;
            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM Q35 WHERE SCHEME_ID=? AND KQ_YEAR=? AND KQ_DURATION=? AND ORG_ID like ? AND GUIDKEY IN (");
            List values = new ArrayList();
            values.add(scheme_id);
            values.add(kq_year);
            values.add(kq_duration);
            values.add(orgId+"%");
            for(String s : guidkeys){
                sql.append("?,");
                values.add(s);
            }
            sql.setLength(sql.length()-1);
            sql.append(")");
            dao.delete(sql.toString(),values);
            //删除
            sql.setLength(0);
            //删除kq_day_detail相应记录
            sql.append("delete from kq_day_detail where org_id like ? and "+Sql_switcher.dateToChar("kq_date","yyyy-MM")+"=? and scheme_id=? and guidkey in (");
            values.clear();
            values.add(orgId+"%");
            values.add(kq_year+"-"+kq_duration);
            values.add(scheme_id);
            for(String s : guidkeys){
                sql.append("?,");
                values.add(s);
            }
            sql.setLength(sql.length()-1);
            sql.append(")");
            dao.delete(sql.toString(),values);
        }catch (Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    @Override
    public String getSumsByScheme(String scheme_id) throws GeneralException {
        SchemeMainService schemeService = new SchemeMainServiceImpl(conn, userView);
        KqItemService itemService = new KqItemServiceImpl(userView, conn);
        HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(scheme_id);
        ArrayList<String> parameterList = new ArrayList<String>();
        HashMap<String,LazyDynaBean> map = new HashMap<String,LazyDynaBean>();
        ArrayList<LazyDynaBean> kqItems = new ArrayList<LazyDynaBean>();
        String itemIds = schemeMap.get("item_ids");
        if(StringUtils.isNotBlank(itemIds)) {
            StringBuffer sqlWherebuf = new StringBuffer();
            sqlWherebuf.append(" and item_id in (");
            String[] idArr = itemIds.split(",");
            for(int i=0;i<idArr.length;i++) {
                sqlWherebuf.append("?,");
                parameterList.add(idArr[i]);
            }
            sqlWherebuf.setLength(sqlWherebuf.length()-1);
            sqlWherebuf.append(")");
            kqItems = itemService.listKqItem(sqlWherebuf.toString(), parameterList, "displayorder,item_id");
        }
        String sumsVal = "";
        for(LazyDynaBean bean : kqItems) {
            String itemdesc = String.valueOf(bean.get("fielditemid"));
            if(StringUtils.isNotEmpty(itemdesc)){
                sumsVal+=itemdesc.toLowerCase()+",";
            }
        }
        if(sumsVal.length()>0){
            sumsVal.substring(0,sumsVal.length());
        }
        return sumsVal;
    }
    /**
	 * 获取出勤异常项目
	 * @param scheme_id
	 * @return
	 * @throws GeneralException
	 */
	@Override
    public String getSumsBySchemeCheck(String scheme_id) throws GeneralException {
		SchemeMainService schemeService = new SchemeMainServiceImpl(conn, userView);
	    KqItemService itemService = new KqItemServiceImpl(userView, conn);
        HashMap<String, String> schemeMap = schemeService.getSchemeDetailDataList(scheme_id);
        ArrayList<String> parameterList = new ArrayList<String>();
        HashMap<String,LazyDynaBean> map = new HashMap<String,LazyDynaBean>();
        ArrayList<LazyDynaBean> kqItems = new ArrayList<LazyDynaBean>();
        String itemIds = schemeMap.get("item_ids");
        if(StringUtils.isNotBlank(itemIds)) {
            StringBuffer sqlWherebuf = new StringBuffer();
            sqlWherebuf.append(" and item_id in (");
            String[] idArr = itemIds.split(",");
            for(int i=0;i<idArr.length;i++) {
            	//异常只检查：迟到、早退、旷工、公出、请假、加班
            	if (idArr[i].startsWith("0")||idArr[i].startsWith("1")||idArr[i].startsWith("3")||"21".equals(idArr[i])||"23".equals(idArr[i])||"25".equals(idArr[i])) {
            		sqlWherebuf.append("?,");
            		parameterList.add(idArr[i]);
				}
            }
            // 55906 如果不属于异常项目规则 直接返回
            if(0 == parameterList.size()) {
            	return "";
            }
            sqlWherebuf.setLength(sqlWherebuf.length()-1);
            sqlWherebuf.append(")");
            kqItems = itemService.listKqItem(sqlWherebuf.toString(), parameterList, "displayorder,item_id");
        }
        String sumsVal = "";
        for(LazyDynaBean bean : kqItems) {
            String itemdesc = String.valueOf(bean.get("fielditemid"));
            if(StringUtils.isNotEmpty(itemdesc)){
                sumsVal+=itemdesc.toLowerCase()+",";
            }
        }
        if(sumsVal.length()>0){
            sumsVal.substring(0,sumsVal.length());
        }
        return sumsVal;
    }
}
