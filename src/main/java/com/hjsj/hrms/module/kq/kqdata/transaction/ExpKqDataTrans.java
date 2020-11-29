package com.hjsj.hrms.module.kq.kqdata.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.kq.config.period.businessobject.PeriodService;
import com.hjsj.hrms.module.kq.config.period.businessobject.impl.PeriodServiceImpl;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.SchemeMainService;
import com.hjsj.hrms.module.kq.config.scheme.businessobject.impl.SchemeMainServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataMxService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.KqDataSpService;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataMxServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.impl.KqDataSpServiceImpl;
import com.hjsj.hrms.module.kq.kqdata.businessobject.util.KqDataUtil;
import com.hjsj.hrms.module.kq.util.KqPrivForHospitalUtil;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.signaturefile.businessobject.SignatureFileBo;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import sun.misc.BASE64Decoder;

import javax.sql.RowSet;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * 考勤明细 输出Excel交易类
 * 
 * @date 2018.11.06
 * @author xus
 *
 */
public class ExpKqDataTrans extends IBusiness {
	private static final long serialVersionUID = 1L;
    private String kq_year = "";
    private String kq_duration = "";
    private String org_name = "";
    private String export_type = "";
    private String photo = "";
	@Override
    public void execute() throws GeneralException {
		String jsonStr = (String)this.formHM.get("jsonStr");
		//获取前台json数据
		JSONObject jsonObj = JSONObject.fromObject(jsonStr);
		KqDataMxService service = new KqDataMxServiceImpl(this.userView,this.frameconn);
		JSONObject returnJson = new JSONObject();
		String return_code="success";
		String return_msg = "success";
		try {
			String type = jsonObj.getString("type");
			export_type = type;
			String scheme_id = jsonObj.getString("scheme_id");
			kq_duration = jsonObj.getString("kq_duration");
			kq_year = jsonObj.getString("kq_year");
			String showMx = jsonObj.getString("showMx");
			String org_id = jsonObj.getString("org_id")==null?"":(String)jsonObj.getString("org_id");//多个以逗号分隔
			if(StringUtils.isNotBlank(org_id) && !org_id.contains(",")){
				String unName = AdminCode.getCodeName("UN",PubFunc.decrypt(org_id));
				String umName = AdminCode.getCodeName("UM",PubFunc.decrypt(org_id));
				org_name = StringUtils.isBlank(unName)?umName:unName;
			}
			if("exportExcel".equals(type)) {
				//导出excel
				String fileName = doExportExl(scheme_id,kq_duration,kq_year,org_id,showMx,service);
				JSONObject obj = new JSONObject();
				obj.put("filename", SafeCode.encode(PubFunc.encrypt(fileName)));
				returnJson.put("return_data", obj);
			}else if("daily".equals(type) || "collect".equals(type)) {
				
				String fileName = doTypeExportExl(scheme_id, kq_duration, kq_year, org_id, showMx, type, service);
				/**
				 * 返回标识
				 * =1未设置月汇总统计指标
				 * =2未设置工号指标（由于工号未必填项故直接提示 请配置考勤相关参数）
				 */
				if("1".equals(fileName)) {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.date.collect.itemnull.error");
				}if("2".equals(fileName)) {
					return_code = "fail";
					return_msg = ResourceFactory.getProperty("kq.param.not");
				}else {
					JSONObject obj = new JSONObject();
					obj.put("filename", SafeCode.encode(PubFunc.encrypt(fileName)));
					returnJson.put("return_data", obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.date.error.export");
		}finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
		}
		this.formHM.put("returnStr", returnJson.toString());
		
	}
	/**
	 * 输出日明细/月汇总
	 * doTypeExportExl
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 * @param showMx
	 * @param type
	 * @param service
	 * @return
	 * @throws GeneralException
	 * @throws IOException
	 * @throws SQLException
	 * @date 2019年1月28日 下午4:14:07
	 * @author linbz
	 */
	private String doTypeExportExl(String scheme_id, String kq_duration, String kq_year, String org_id, String showMx, String type, KqDataMxService service)
			throws GeneralException, IOException, SQLException {
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.frameconn,this.userView);
        String scheme_id_ = PubFunc.decrypt(scheme_id);//解密后的方案id
        // 方案考勤员
    	String clerkName = ""; 
    	// 方案机构审核员
    	String reviewerName = "";
    	HashMap schemeDetail = new HashMap();
    	ArrayList parameterList = new ArrayList();
		parameterList.add(scheme_id_);
    	ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
    	LazyDynaBean schemeBean = schemeList.get(0);
        // 45101 如果是多个方案导出则 获取整个方案的信息
        if(org_id.split(",").length > 1) {
        	// 多个部门时不显示考勤员
        	String b0100 = (String)schemeBean.get("b0110");
			if(StringUtils.isNotBlank(b0100))
				org_name = (AdminCode.getCodeName("UN", b0100).length()==0?AdminCode.getCodeName("UM", b0100):AdminCode.getCodeName("UN", b0100));
        }// 单个方案则需从中筛选出对应的方案信息
        else {
        	// 44688 获取数据上报的考勤员方案信息方法扩展
        	String orgidStr = PubFunc.decrypt(org_id);
        	ArrayList<HashMap> listFillingAgencys = (ArrayList<HashMap>)schemeBean.get("org_map");
        	for(int i=0;i<listFillingAgencys.size();i++) {
        		HashMap map = (HashMap) listFillingAgencys.get(i);
        		String orgidValue = (String)map.get("org_id");
        		if(orgidValue.equals(orgidStr)) {
        			schemeDetail = map;
        			break;
        		}
        	}
        	reviewerName = (String)schemeDetail.get("reviewer");
        	clerkName = this.getClerkName((String)schemeDetail.get("clerk_fullname"), (String)schemeDetail.get("y_clerk_username"));
        	/**
        	 * 处理是否需要导出 机构审核人 姓名或签章
        	 */
        	HashMap dealMap = this.dealSignatureFunc(scheme_id_, orgidStr, reviewerName);
        	reviewerName = (String)dealMap.get("reviewerName");
        }
        
		KqPrivForHospitalUtil param = new KqPrivForHospitalUtil(userView, this.frameconn);
		String gNo = param.getG_no();
		// 59314  由于导出日明细、月汇总 必须用到 工号  故增加指标校验 
		if(StringUtils.isBlank(gNo)) {
			return "2";
		}
		// 44854 去掉统计项校验
//		String collectItems = "";
//		if("collect".equals(type)) 
//			collectItems = getCollectFielditemid(scheme_id_);
		
		ArrayList<ColumnsInfo> columnList = listSimpleColumn(scheme_id_);
		
		//根据栏目设置排序列头
		ArrayList<ColumnsInfo> columnListSort=sortColumn(columnList, scheme_id_, showMx);

		HashMap<String, LazyDynaBean> styleMap =service.getClassAndItems("", "0");
		ArrayList headList = listSimpleHead(columnListSort, scheme_id_, type);
		String sortSql  = " order by a0000 asc ";
		// 根据栏目设置排序
		TableDataConfigCache configCache = (TableDataConfigCache)this.userView.getHm().get("kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")
				+PubFunc.decrypt(scheme_id));
		if(configCache != null)	{
			sortSql  = (String)configCache.getSortSql();//取得oder by
		}
		//数据sql
		String sql = getSimpleSql(kq_year, kq_duration, org_id, scheme_id_, gNo, String.valueOf(schemeBean.get("cbase")));
		String sqlstr = "select row_number() over( "+sortSql+" )  as id, q.* from (" + sql.toString() + ") q "+sortSql;
		HashMap dataMap = listSimpleExcelList(sqlstr, columnListSort, showMx, type, styleMap);
		dataMap = getDealedExcelList(dataMap, styleMap);
		ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("newDataList");
		// 获取顶层机构描述
        KqPrivForHospitalUtil kp = new KqPrivForHospitalUtil(this.userView, this.frameconn);
        String orgid = kp.getTopUNCodeitemid();
		String unStr = AdminCode.getCodeName("UN", orgid);
		String orgDesc = StringUtils.isBlank(unStr) ? AdminCode.getCodeName("UM", orgid) : unStr;
		// 合并列头
		ArrayList mergeCellList = listTitleMergedCell(columnListSort, clerkName, styleMap, dataMap
				, dealedExcelList.size(), reviewerName, orgDesc);
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);// 实例化导出Excel工具类
		excelUtil.setHeadRowHeight((short)400);
		// 锁表
        excelUtil.setProtect(true);
        excelUtil.setPassword("kqdata123");
        String fileName = org_name+kq_year+kq_duration 
        		+ ("daily".equals(export_type) ? ResourceFactory.getProperty("kq.date.monthly.daily") 
        				: ResourceFactory.getProperty("kq.date.monthly.collect"));
        //fileName 加登陆用户名以免导出时Excel名称相同时，导出的数据错误
        fileName = this.userView.getUserName()+ "_"+fileName+".xls";
		//导出到excel
		excelUtil.setRowHeight((short)350);
        excelUtil.setConvertToZero(false);
        // 设置纸张参数
        excelUtil.setPrintSetup(true);
        excelUtil.setLandscape(true);
        excelUtil.setWaterRemarkContent(orgDesc);
        // 导出表格
		excelUtil.exportExcel(fileName, kq_duration+ResourceFactory.getProperty("kq.duration.yue")+ResourceFactory.getProperty("kq.datemx.table"),
				mergeCellList, headList, dealedExcelList, null, 4);
		return fileName;
	}
	/**
	 * 执行导出excel方法
	 * @param scheme_id
	 * @param kq_duration
	 * @param kq_year
	 * @param org_id
	 * @param showMx
	 * @param service
	 * @return
	 * @throws GeneralException
	 * @throws IOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	private String doExportExl(String scheme_id, String kq_duration, String kq_year, String org_id , String showMx
			, KqDataMxService service) throws GeneralException, IOException, SQLException {
        SchemeMainService schemeMainService = new SchemeMainServiceImpl(this.frameconn,this.userView);
        // 解密后的方案id
        String scheme_id_ = PubFunc.decrypt(scheme_id);
        ArrayList parameterList = new ArrayList();
		parameterList.add(scheme_id_);
    	ArrayList<LazyDynaBean> schemeList = schemeMainService.listKq_scheme(" And scheme_id=? ", parameterList, "");
    	LazyDynaBean schemeBean = schemeList.get(0);
        // 45101 如果是多个方案导出则 获取整个方案的所属单位信息
        if(org_id.split(",").length > 1) {
        	String b0100 = (String)schemeBean.get("b0110");
			if(StringUtils.isNotBlank(b0100))
				org_name = (AdminCode.getCodeName("UN", b0100).length()==0?AdminCode.getCodeName("UM", b0100):AdminCode.getCodeName("UN", b0100));
        }
        String o = String.valueOf(schemeBean.get("confirm_flag"));
        Integer confirmFlag = "null".equalsIgnoreCase(o) || o.length()==0?0:Integer.parseInt(o);
		
		ArrayList<ColumnsInfo> columnList = service.getColumnList(showMx, kq_duration, kq_year, scheme_id_,confirmFlag);
		HashMap<String, LazyDynaBean> styleMap =service.getClassAndItems("", "0");
		//将锁列的排在前面
		ArrayList<ColumnsInfo> OrderedList = new ArrayList<ColumnsInfo>();
		//添加第一列序号列
		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId("id");
		columnsInfo.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
		columnsInfo.setColumnDesc("序号");
		columnsInfo.setCodesetId("0");
		columnsInfo.setColumnType("N");
		columnsInfo.setDecimalWidth(0);
        columnsInfo.setColumnWidth(30);
		OrderedList.add(columnsInfo);
		
		for(ColumnsInfo col:columnList){
			if(col.isLocked())
				OrderedList.add(col);
		}
		for(ColumnsInfo col:columnList){
			if(!col.isLocked())
				OrderedList.add(col);
		}
		//获取excel表头list
		HashMap map = getHeadList(OrderedList,showMx);
		ArrayList headList = (ArrayList) map.get("list");
		
		TableDataConfigCache configCache = (TableDataConfigCache)this.userView.getHm().get("kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")
				+PubFunc.decrypt(scheme_id));
		String sortSql  = "";
		if(configCache != null)	{
			sortSql  = (String)configCache.getSortSql();//取得oder by
		}
		sortSql = StringUtils.isNotBlank(sortSql)?sortSql:" order by a0000 asc";
		//数据sql
		String tableSql =  "select data.* from (" + service.getTableSql(kq_year, kq_duration, org_id, scheme_id_, String.valueOf(schemeBean.get("cbase"))) + ") data " + sortSql;
		tableSql = "select row_number() over(" + sortSql + ")  as id," + tableSql.substring(6);
		HashMap dataMap = getOutExcelList(tableSql,OrderedList,showMx,styleMap);
		
		dataMap = getDealedExcelList(dataMap,styleMap);
		ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("newDataList");
        ArrayList mergeCellList = getMergedCellList(OrderedList,dataMap,showMx);
		ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);// 实例化导出Excel工具类
		excelUtil.setHeadRowHeight((short)550);
        String fileName = org_name+kq_year+kq_duration+ResourceFactory.getProperty("kq.datemx.table");
        //fileName 加登陆用户名以免导出时Excel名称相同时，导出的数据错误
        fileName =this.userView.getUserName()+ "_"+fileName+".xls";
		//导出到excel
		excelUtil.setRowHeight((short)300);
        excelUtil.setConvertToZero(false);
		excelUtil.exportExcel(fileName, kq_duration+ResourceFactory.getProperty("kq.duration.yue")+ResourceFactory.getProperty("kq.datemx.table"),
				mergeCellList, headList, dealedExcelList, null, 2);//导出表格
		return fileName;
	}

	/**
	 * 处理后的数据集合
	 * @param dataMap
	 * @param styleMap
	 * @return
	 */
	private HashMap getDealedExcelList(HashMap dataMap, HashMap<String, LazyDynaBean> styleMap) {
		@SuppressWarnings("unchecked")
		HashMap<Integer, Integer> rowMap = (HashMap<Integer, Integer>) dataMap.get("rowMap");
		@SuppressWarnings("unchecked")
		ArrayList<LazyDynaBean> dataList = (ArrayList<LazyDynaBean>) dataMap.get("dataList");
		ArrayList<LazyDynaBean> newDataList = new ArrayList<LazyDynaBean>();
		HashMap returnMap = new HashMap();
		int totalIndex = 0;
		HashMap mergedMap = new HashMap();
        String jsonStr = (String)this.formHM.get("jsonStr");
        //获取前台json数据
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        String mxDetailType = jsonObj.getString("mxDetailType");
		for(int i = 0;i<dataList.size();i++){
			int count = 0;
			if(rowMap.get(i)!=null)
				count = rowMap.get(i);
			LazyDynaBean bean = dataList.get(i);
			//根据该行数据个数，拆分该行
			for(int index = 0;index<count;index++){
				LazyDynaBean newBean = new LazyDynaBean();
				HashMap singelrowmergeMap = new HashMap();
				for (Object o :bean.getMap().keySet()) {
					String key =(String)o;
					key = key.toLowerCase();
					LazyDynaBean innerBean = (LazyDynaBean)bean.get(key);
					LazyDynaBean newInnerBean = new LazyDynaBean();
					String value = (String)innerBean.get("content");
					int fromColNum = (Integer)innerBean.get("fromColNum");
					String newValue = value;
					//日期列设置样式
					if(key.startsWith("q35")
							&& StringUtils.isNumericSpace(key.substring(3))
							&& Integer.parseInt(key.substring(3))>=1
							&& Integer.parseInt(key.substring(3))<=31
							&& StringUtils.isNotBlank(value)){
						String[] spval=value.split(","); 
						if(index==1)
							singelrowmergeMap.put(key, count);
							
						if(spval.length>index){
							newValue = spval[index];
						}else{
							newValue = "";
						}
						LazyDynaBean styleBean = styleMap.get(newValue);
						if(styleBean!=null){
							//根据不同的明细类型，显示不同的样式
							if(newValue.startsWith("C")){
                                String symbol = String.valueOf(styleBean.get("symbol"));
                                String abbreviation = String.valueOf(styleBean.get("abbreviation"));
                                String name = String.valueOf(styleBean.get("name"));
                                //字符+名称
							    if("0".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol+" ":"";
                                    newValue += StringUtils.isNotBlank(abbreviation)?abbreviation:name;
                                }else if("1".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol:(StringUtils.isNotBlank(abbreviation)?abbreviation:name);
                                }else{
                                    newValue = StringUtils.isNotBlank(abbreviation)?abbreviation:name;
                                }
							    // 输出日明细
                                if(!"exportExcel".equals(export_type)) {
                                	newValue = StringUtils.isEmpty(symbol)?abbreviation:symbol;
                                	newValue = StringUtils.isEmpty(newValue)?name:newValue;
                                }
								if(styleBean.get("color")!=null && String.valueOf(styleBean.get("color")).length()>0){
									String color = String.valueOf(styleBean.get("color"));
									HashMap colStyleMap = new HashMap();
									//避免白色的字体导出后看不见
									if("#FFFFFF".equals(color)){
										color = "#000000";
									}
									colStyleMap.put("fontColor",color.substring(1));
									colStyleMap.put("align","center");
									newInnerBean.set("singleCellStyle", colStyleMap);
								}
							}else if(newValue.startsWith("I")){
                                String symbol = styleBean.get("item_symbol")== null ?"":(String)styleBean.get("item_symbol");
                                String name = styleBean.get("item_name")== null ?"":(String)styleBean.get("item_name");
                                //字符+名称
                                if("0".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol+" "+name:name;
                                }else if("1".equals(mxDetailType)){
                                    newValue = StringUtils.isNotBlank(symbol)?symbol:name;
                                }else{
                                    newValue = name;
                                }
                                // 输出日明细
                                if(!"exportExcel".equals(export_type))
									newValue = StringUtils.isEmpty(symbol)?name:symbol;
								if(styleBean.get("item_color")!=null && String.valueOf(styleBean.get("item_color")).length()>0){
									HashMap colStyleMap = new HashMap();
									String item_color = String.valueOf(styleBean.get("item_color"));
									//避免白色的字体导出后看不见
									if("#FFFFFF".equals(item_color)){
										item_color = "#000000";
									}
									colStyleMap.put("fontColor", item_color.substring(1));
									colStyleMap.put("align","center");
									newInnerBean.set("singleCellStyle", colStyleMap);
								}
							}
						}
					}
					
					newInnerBean.set("content", newValue);
					newInnerBean.set("fromColNum", fromColNum);
					newBean.set(key, newInnerBean);
				}	
				newDataList.add(newBean);
				if(singelrowmergeMap.size()>0){
					singelrowmergeMap.put("count", count);
					mergedMap.put(totalIndex, singelrowmergeMap);
				}
				totalIndex++;
			}
		}
		returnMap.put("mergedMap", mergedMap);//合并的map
		returnMap.put("newDataList", newDataList);//处理后的数据（多条明细的拆分为多行）
		return returnMap;
	}
	/**
	 * 获取输出日明细/月汇总列头集合
	 * listSimpleExcelList
	 * @param tableSql
	 * @param columnList
	 * @param showMx
	 * @param type
	 * @param styleMap
	 * @return
	 * @throws SQLException
	 * @date 2019年1月28日 下午4:15:47
	 * @author linbz
	 */
	private HashMap listSimpleExcelList(String tableSql, ArrayList<ColumnsInfo> columnList, String showMx, String type
			, HashMap<String,LazyDynaBean> styleMap) throws SQLException {
		HashMap returnMap = new HashMap();
		ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
		HashMap<Integer,Integer> rowMap = new HashMap<Integer, Integer>();
		ContentDAO dao = new ContentDAO(this.frameconn);
		this.frowset=dao.search(tableSql);
		int index = 0;
		while(this.frowset.next()){
			int colLen = 1;
			int colIndex = 0;
			LazyDynaBean bean = new LazyDynaBean(); 
			for(int i = 0 ;i<columnList.size();i++){
				ColumnsInfo col = columnList.get(i);
				//查询结果 bean 中的key为全小写字段名
				String data = "";
				String value = "";
				String item = col.getColumnId();
				if(item.toUpperCase().startsWith("Q35")
                        && StringUtils.isNumericSpace(item.substring(3))
                        && Integer.parseInt(item.substring(3))>=1
                        && Integer.parseInt(item.substring(3))<=31){
                    value = this.frowset.getString(item);
                    //去掉无效班次和项目
                    value = deleteNotExitsScheme(value, styleMap);
                    if(value!=null&&value.indexOf(',')>-1){
                        int len = value.split(",").length;
                        if(colLen<len)
                            colLen = len;
                    }
                }else {
                	// 去掉固定的备注列
//                	if("remark".equalsIgnoreCase(item)) {
//                		value = "";
//                	}else 
                	if("confirm".equals(item)){
    					String temp = this.frowset.getString(item);
    					if(temp!=null) {
    						switch(this.frowset.getInt(item)){
    							case 1:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm1");
    								break;
    							case 2:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm2");
    								break;
    							case 0:
    								value = ResourceFactory.getProperty("kq.date.mx.confirm0");
    								break;
    						}
    					}
    					data = KqDataUtil.nullif(value);
    		            bean = setDataColumn(item,data,bean,colIndex);
    		            colIndex++;
    					continue;
    				}
            		if("A".equals(col.getColumnType())){
	                    value = this.frowset.getString(item);
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
	                }else if("D".equals(col.getColumnType())){
	                    int leng=col.getColumnLength();
	                    Date date=this.frowset.getTimestamp(item);
	                    if(date==null){
	                        value="";
	                    }else{
	                        String typef="yyyy-MM-dd H:m:s";
	                        if(leng==4){
	                        	typef="yyyy";
	                        }else if(leng==7){
	                        	typef="yyyy-MM";
	                        }else if(leng==10){
	                        	typef="yyyy-MM-dd";
	                        }else if(leng==13){
	                        	typef="yyyy-MM-dd H";
	                        }else if(leng==16){
	                        	typef="yyyy-MM-dd H:m";
	                        }else if(leng>16){
	                        	typef="yyyy-MM-dd H:m:s";
	                        }
	                        SimpleDateFormat sdf = new SimpleDateFormat(typef);
	                        value = sdf.format(date);
	                    }
	                }else{
	                    value=this.frowset.getString(item)==null?"":this.frowset.getString(item);
	                }
                }
				data = KqDataUtil.nullif(value);
	            bean = setDataColumn(item,data,bean,colIndex);
	            colIndex++;
			}
			rowMap.put(index, colLen);
			index++;
			dataList.add(bean);
		}
		returnMap.put("rowMap", rowMap);
		returnMap.put("dataList", dataList);
		return returnMap;
	}
	/**
	 * 获取excel初始数据集合
	 * @param tableSql
	 * @param columnList
	 * @param showMx 
	 * @return ArrayList
	 * @throws SQLException
	 */
	private HashMap getOutExcelList(String tableSql, ArrayList<ColumnsInfo> columnList, String showMx
			, HashMap<String,LazyDynaBean> styleMap) throws SQLException {
		HashMap returnMap = new HashMap();
		ArrayList<LazyDynaBean> dataList=new ArrayList<LazyDynaBean>();
		HashMap<Integer,Integer> rowMap = new HashMap<Integer, Integer>();
		ContentDAO dao = new ContentDAO(this.frameconn);
		this.frowset=dao.search(tableSql);
		int index = 0;
		while(this.frowset.next()){
			int colLen = 1;
			int colIndex = 0;
			LazyDynaBean bean = new LazyDynaBean(); 
			for(int i = 0 ;i<columnList.size();i++){
				ColumnsInfo col = columnList.get(i);
				/*if(col.getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
					continue;*/
				//查询结果 bean 中的key为全小写字段名
				String data = "";
				String value = "";
				ArrayList<ColumnsInfo> childColumns = col.getChildColumns();
				String item ="";
				if(childColumns.size()>0){
					ColumnsInfo cc;
					for(int k = 0;k<childColumns.size();k++){
						cc = childColumns.get(k);
						/*if(cc.getLoadtype()!=ColumnsInfo.LOADTYPE_BLOCK)
							continue;*/
						item = cc.getColumnId();
                        if(item.toUpperCase().startsWith("Q35")
                                && StringUtils.isNumericSpace(item.substring(3))
                                && Integer.parseInt(item.substring(3))>=1
                                && Integer.parseInt(item.substring(3))<=31){
                            value = this.frowset.getString(item);
                            //去掉无效班次和项目
                            value = deleteNotExitsScheme(value, styleMap);
                            if(value!=null&&value.indexOf(',')>-1){
                                int len = value.split(",").length;
                                if(colLen<len)
                                    colLen = len;
                            }
                        }else{
                            if("A".equals(cc.getColumnType())){
                                value = this.frowset.getString(item);
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
                            }else if("D".equals(cc.getColumnType())){
                                int leng=col.getColumnLength();
                                Date date=this.frowset.getDate(item);
                                if(date==null||"null".equals(date)){
                                    value="";
                                }else{
                                    String type="yyyy-MM-dd H:m:s";
                                    if(leng==4){
                                        type="yyyy";
                                    }else if(leng==7){
                                        type="yyyy-MM";
                                    }else if(leng==10){
                                        type="yyyy-MM-dd";
                                    }else if(leng==13){
                                        type="yyyy-MM-dd H";
                                    }else if(leng==16){
                                        type="yyyy-MM-dd H:m";
                                    }else if(leng>16){
                                        type="yyyy-MM-dd H:m:s";
                                    }
                                    SimpleDateFormat sdf = new SimpleDateFormat(type);
                                    value = sdf.format(date);
                                }
                            }else{
                                value=this.frowset.getString(item)==null?"":this.frowset.getString(item);
                            }
                        }
                        data = KqDataUtil.nullif(value);
                        bean = setDataColumn(item,data,bean,colIndex);
                        colIndex++;
					}
					continue;
				}else{
					item = col.getColumnId();
				}
				if("guidkey".equals(item))
					continue;
				
				if("confirm_".equals(item)){
					String temp = this.frowset.getString(item);
					if(temp!=null) {
						switch(this.frowset.getInt(item)){
							case 1:
								value = ResourceFactory.getProperty("kq.date.mx.confirm1");
								break;
							case 2:
								value = ResourceFactory.getProperty("kq.date.mx.confirm2");
								break;
							case 0:
								value = ResourceFactory.getProperty("kq.date.mx.confirm0");
								break;
						}
					}
					data = KqDataUtil.nullif(value);
		            bean = setDataColumn(item,data,bean,colIndex);
		            colIndex++;
					continue;
				}
                if("A".equals(col.getColumnType())){
                    value = this.frowset.getString(item);
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
                }else if("D".equals(col.getColumnType())){
                    int leng=col.getColumnLength();
                    Date date=this.frowset.getDate(item);
                    if(date==null||"null".equals(date)){
                        value="";
                    }else{
                        String type="yyyy-MM-dd H:m:s";
                        if(leng==4){
                            type="yyyy";
                        }else if(leng==7){
                            type="yyyy-MM";
                        }else if(leng==10){
                            type="yyyy-MM-dd";
                        }else if(leng==13){
                            type="yyyy-MM-dd H";
                        }else if(leng==16){
                            type="yyyy-MM-dd H:m";
                        }else if(leng>16){
                            type="yyyy-MM-dd H:m:s";
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat(type);
                        value = sdf.format(date);
                    }
                }else{
                    value=this.frowset.getString(item)==null?"":this.frowset.getString(item);
                }
				
				data = KqDataUtil.nullif(value);
	            bean = setDataColumn(item,data,bean,colIndex);
	            colIndex++;
			}
			rowMap.put(index, colLen);
			index++;
			dataList.add(bean);
		}
		returnMap.put("rowMap", rowMap);
		returnMap.put("dataList", dataList);
		return returnMap;
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
	 * 导出日明细/月汇总合并列
	 * listTitleMergedCell
	 * @param columnsInfo
	 * @param clerkName		方案考勤员
	 * @param styleMap
	 * @param dataSize
	 * @return
	 * @date 2019年1月28日 上午10:36:34
	 * @author linbz
	 */
	private ArrayList<LazyDynaBean> listTitleMergedCell(ArrayList columnsInfo, String clerkName, HashMap<String, LazyDynaBean> styleMap
			, HashMap dataMap, int dataSize, String reviewerName, String orgDesc) {
    	ArrayList<LazyDynaBean> mergedCellList = new ArrayList<LazyDynaBean>();
    	int allSize = columnsInfo.size();
    	boolean bool = "daily".equals(export_type);
		//Excel标题内容
        HashMap titleStyleMap = new HashMap();
		titleStyleMap.put("border",(short)0);
		titleStyleMap.put("fontSize",18);
		titleStyleMap.put("isFontBold",true);
		titleStyleMap.put("borderColor", IndexedColors.WHITE.index);
        LazyDynaBean titleBean = new LazyDynaBean();
        
        String title = orgDesc + (bool ? ResourceFactory.getProperty("kq.date.monthly.daily") : ResourceFactory.getProperty("kq.date.monthly.collect"));
        titleBean.set("content", title);// 列头名称
        titleBean.set("fromRowNum", 0);// 合并单元格从那行开始
        titleBean.set("toRowNum", 1);// 合并单元格到哪行结束
        titleBean.set("fromColNum", 0);// 合并单元格从哪列开始
        titleBean.set("toColNum", allSize-1);// 合并单元格从哪列结束
        titleBean.set("mergedCellStyleMap", titleStyleMap);// 合并单元格从哪列结束
        titleBean.set("columnLocked",true); // 锁列
        mergedCellList.add(0, titleBean);
		
        titleStyleMap = new HashMap();
		titleStyleMap.put("border", (short)0);//HSSFCellStyle.BORDER_NONE  没有生效
		titleStyleMap.put("borderColor", IndexedColors.WHITE.index);
		titleStyleMap.put("align", HorizontalAlignment.LEFT);
		
		// 是否有签章图片标识
		boolean photoBool = StringUtils.isNotBlank(photo);
		
		int sizef = photoBool ? allSize/5 : allSize/4;
		sizef = sizef<1 ? 1 : sizef;
		
		title = ResourceFactory.getProperty("label.commend.um")+ "："+org_name;
        titleBean = new LazyDynaBean();
        titleBean.set("content", title);
        titleBean.set("fromRowNum", 2);
        titleBean.set("toRowNum", 2);
        titleBean.set("fromColNum", 0);
        titleBean.set("toColNum", sizef);
        titleBean.set("columnLocked",true);
        titleBean.set("mergedCellStyleMap", titleStyleMap);
        mergedCellList.add(1, titleBean);
        title = (StringUtils.isBlank(clerkName)) ? "" : (ResourceFactory.getProperty("kq.data.sp.clerk")+ "："+clerkName);
        titleBean = new LazyDynaBean();
        titleBean.set("content", title);
        titleBean.set("columnLocked",true);
        titleBean.set("fromRowNum", 2);
        titleBean.set("toRowNum", 2);
        titleBean.set("fromColNum", sizef+1);
        titleBean.set("toColNum", sizef*2);
        titleBean.set("mergedCellStyleMap", titleStyleMap);
        mergedCellList.add(1, titleBean);
        
        title = kq_year+"年"+kq_duration+"月";
        titleBean = new LazyDynaBean();
        titleBean.set("content", title);
        titleBean.set("columnLocked",true);
        titleBean.set("fromRowNum", 2);
        titleBean.set("toRowNum", 2);
        titleBean.set("fromColNum", sizef*2+1);
        titleBean.set("toColNum", sizef*3);
        titleBean.set("mergedCellStyleMap", titleStyleMap);
        mergedCellList.add(1, titleBean);
        
        if(photoBool) {
        	titleStyleMap = new HashMap();
        	titleStyleMap.put("border", (short)0);//HSSFCellStyle.BORDER_NONE  没有生效
        	titleStyleMap.put("borderColor", IndexedColors.WHITE.index);
        	titleStyleMap.put("align", HorizontalAlignment.RIGHT);
        }
		
        title = ResourceFactory.getProperty("kq.data.sp.audit")+ "："+(photoBool ? "" : reviewerName);  
        titleBean = new LazyDynaBean();
        titleBean.set("columnLocked",true);
        titleBean.set("content", title);
        titleBean.set("fromRowNum", 2);
        titleBean.set("toRowNum", 2);
        titleBean.set("fromColNum", sizef*3+1);
        titleBean.set("toColNum", photoBool ? sizef*4 : (allSize-1));
        titleBean.set("mergedCellStyleMap", titleStyleMap);
        mergedCellList.add(1, titleBean);
        
        // 签章图片
        if(photoBool) {
        	title = "";
//        titleStyleMap.put("align", HorizontalAlignment.CENTER);
        	byte[] str = generateImage("");
        	titleBean = new LazyDynaBean();
        	titleBean.set("columnLocked",true);
        	titleBean.set("content", title);
        	titleBean.set("fromRowNum", 2);
        	titleBean.set("toRowNum", 2);
        	titleBean.set("fromColNum", sizef*4+1);
        	titleBean.set("toColNum", allSize-1);
        	titleBean.set("isPhoto", true);
        	titleBean.set("photo_bytes", str);
        	titleBean.set("mergedCellStyleMap", titleStyleMap);
        	mergedCellList.add(1, titleBean);
        }
        
        // 如果是日明细则在表格尾部增加符号说明列
        if(bool) {
        	titleStyleMap = new HashMap();  
    		titleStyleMap.put("align", HorizontalAlignment.LEFT);
    		title = ResourceFactory.getProperty("report.parse.kqfu")+ResourceFactory.getProperty("kq.class.explain")+"：";
    		String name = "";
    		String symbol = "";
    		for (Map.Entry<String, LazyDynaBean> map : styleMap.entrySet()) { 
    			String valueKey = map.getKey();
    			LazyDynaBean styleBean = map.getValue();
    			if(valueKey.startsWith("C")){
                    symbol = String.valueOf(styleBean.get("symbol"));
                    name = String.valueOf(styleBean.get("abbreviation"));
                    if(StringUtils.isEmpty(name)) 
                    	name = String.valueOf(styleBean.get("name"));
    			}else if(valueKey.startsWith("I")){
                    symbol = styleBean.get("item_symbol")== null ?"":(String)styleBean.get("item_symbol");
                    name = styleBean.get("item_name")== null ?"":(String)styleBean.get("item_name");
    			}
				title += name +"："+ symbol+"      ";
			}
    		int toRowNum = dataSize+5 + styleMap.size()/7;
            titleBean = new LazyDynaBean();
            titleBean.set("content", title);
            titleBean.set("columnLocked",true);
            titleBean.set("fromRowNum", dataSize+5);
            titleBean.set("toRowNum", toRowNum);
            titleBean.set("fromColNum", 0);
            titleBean.set("toColNum", allSize-1);
            titleBean.set("mergedCellStyleMap", titleStyleMap);
            mergedCellList.add(2, titleBean);
        }
        // 合并数据
        HashMap mergedMap = (HashMap) dataMap.get("mergedMap");
  		ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("newDataList");
  		for(Object obj:mergedMap.keySet()){
  			int index = (Integer) obj;
  			HashMap innermergeMap = (HashMap) mergedMap.get(index);
  			if(innermergeMap.size()==0)
  				continue;
  			LazyDynaBean bean = dealedExcelList.get(index);
  			for(Object o:bean.getMap().keySet()){
  				String beanKey = (String) o;
  				if(!innermergeMap.containsKey(beanKey)){
  					LazyDynaBean innerBean = (LazyDynaBean) bean.get(beanKey);
  					int fromColNum = (Integer) innerBean.get("fromColNum");
  					int count = (Integer) innermergeMap.get("count");
  					LazyDynaBean ldbean = new LazyDynaBean();
  					ldbean.set("content",(String)innerBean.get("content"));// 列头名称
  					ldbean.set("fromRowNum", index+4);// 合并单元格从那行开始
  					ldbean.set("toRowNum", index+count+1+2);// 合并单元格到哪行结束
  					ldbean.set("fromColNum", fromColNum);// 合并单元格从哪列开始
  					ldbean.set("toColNum", fromColNum);// 合并单元格从哪列结束
  					ldbean.set("columnLocked",true);// 锁列
  					mergedCellList.add(ldbean);
  				}
  			}
  		}
  		
		return mergedCellList;
    }
	/**
	 * 获取合并单元格list
	 * @param columnsInfo
	 * @param dataMap
	 * @return
	 */
	private ArrayList<LazyDynaBean> getMergedCellList(ArrayList columnsInfo, HashMap dataMap,String showMx) {
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
                if(itemid.toUpperCase().startsWith("Q35")
                        && StringUtils.isNumericSpace(itemid.substring(3))
                        && Integer.parseInt(itemid.substring(3))>=1
                        && Integer.parseInt(itemid.substring(3))<=31
                        && (ccDesc.equalsIgnoreCase(com.hrms.hjsj.sys.ResourceFactory.getProperty("kq.date.column.zliu"))
                        || ccDesc.equalsIgnoreCase(com.hrms.hjsj.sys.ResourceFactory.getProperty("kq.date.column.zri")))){
                    ldbean.set("isZhoumo", true);//是否是周末
                }
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
        String title = org_name+ResourceFactory.getProperty("kq.archive.scheme.kqconfirmletter1")+ResourceFactory.getProperty("kq.datemx.table");
        title = title.replace("{year}",kq_year).replace("{month}",kq_duration);
        titleBean.set("content", title);// 列头名称
        titleBean.set("fromRowNum", 0);// 合并单元格从那行开始
        titleBean.set("toRowNum", 0);// 合并单元格到哪行结束
        titleBean.set("fromColNum", 0);// 合并单元格从哪列开始
        titleBean.set("toColNum", colNum-2);// 合并单元格从哪列结束
        titleBean.set("mergedCellStyleMap", titleStyleMap);// 合并单元格从哪列结束
        mergedCellList.add(0,titleBean);
		
		//合并数据
		HashMap mergedMap = (HashMap) dataMap.get("mergedMap");
		ArrayList<LazyDynaBean> dealedExcelList = (ArrayList<LazyDynaBean>) dataMap.get("newDataList");
		for(Object obj:mergedMap.keySet()){
			int index = (Integer) obj;
			HashMap innermergeMap = (HashMap) mergedMap.get(index);
			if(innermergeMap.size()==0)
				continue;
			LazyDynaBean bean = dealedExcelList.get(index);
			for(Object o:bean.getMap().keySet()){
				String beanKey = (String) o;
				if(!innermergeMap.containsKey(beanKey)){
					LazyDynaBean innerBean = (LazyDynaBean) bean.get(beanKey);
					int fromColNum = (Integer) innerBean.get("fromColNum");
					int count = (Integer) innermergeMap.get("count");
					LazyDynaBean ldbean = new LazyDynaBean();
					ldbean.set("content",(String)innerBean.get("content"));// 列头名称
					ldbean.set("fromRowNum", index+2);// 合并单元格从那行开始
					ldbean.set("toRowNum", index+count+1);// 合并单元格到哪行结束
					ldbean.set("fromColNum", fromColNum);// 合并单元格从哪列开始
					ldbean.set("toColNum", fromColNum);// 合并单元格从哪列结束
					mergedCellList.add(ldbean);
				}
			}
		}
		
		return mergedCellList;
    }

	/**
	 * 获取excel表头行
	 * @param columnList
	 * @param showMx 
	 * @return
	 */
	private HashMap getHeadList(ArrayList<ColumnsInfo> columnList, String showMx) {
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
		return map;
	}
	/**
	 * 输出日明细/月汇总 列头集合
	 * listSimpleHead
	 * @param columnList
	 * @param schemeId
	 * @param type
	 * @return
	 * @date 2019年1月28日 下午4:30:15
	 * @author linbz
	 */
	private ArrayList listSimpleHead(ArrayList<ColumnsInfo> columnList, String schemeId, String type)throws GeneralException {
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		LazyDynaBean bean =null;
		int cellIndex = 0;
		// 计算月汇总指标宽度，总宽度35800
        String columnStr = "";
        KqDataSpService kqDataSpService = new KqDataSpServiceImpl(this.frameconn,this.userView);
        Map<String, String> map = kqDataSpService.getExportScheme(schemeId);
        String detailsVal = map.get("detailsVal");
        String sumsVal = map.get("sumsVal");
        if("daily".equals(export_type)){
            columnStr = ","+detailsVal+",";
        }else{
            columnStr = ","+sumsVal+",";
        }
        int totalWidth = 35800;
        int colSize = columnList.size();
        if(columnStr.indexOf(",seq,")>-1){
            totalWidth-=1200;
            colSize--;
        }
        if(columnStr.indexOf(",a0101,")>-1){
            totalWidth-=2200;
            colSize--;
        }
        int cwidth = totalWidth/colSize;
        // memo->remark 后期修改，mome 对应remark，就不改原程序了   固定的备注指标去掉
//        if(columnStr.indexOf(",memo,")>-1 && cwidth<3200){
//            totalWidth-=3200;
//            colSize--;
//            cwidth = totalWidth/colSize;
//        }
		for(int i = 0 ;i<columnList.size();i++){
			ColumnsInfo col = columnList.get(i);
			String columnid = col.getColumnId();
			int loadType = col.getLoadtype();
			bean = new LazyDynaBean();
			String content = col.getColumnDesc();
			bean.set("content", content);
			// 锁列 只读
			bean.set("columnLocked", true);
			if(loadType!=ColumnsInfo.LOADTYPE_BLOCK )
				bean.set("columnHidden", true);
			bean.set("codesetid", col.getCodesetId());
			bean.set("colType", col.getColumnType());
			bean.set("decwidth", col.getDecimalWidth()+"");
			// 序号
			if("id".equalsIgnoreCase(columnid))
				bean.set("columnWidth", 1200);
//			// 日明细备注
//			if("remark".equalsIgnoreCase(columnid))
//				bean.set("columnWidth", cwidth<3200?3200:cwidth);	
			if("a0101".equalsIgnoreCase(columnid))
				bean.set("columnWidth",2200);
			// 月汇总指标
			// 44854 去掉统计项校验   序号姓名编号三列固定列宽不需要改//remark,
			if(!(",id,a0101,").contains(","+columnid.toLowerCase()+","))
				bean.set("columnWidth", cwidth);
			bean.set("itemid", columnid);
			bean.set("fromRowNum", 3);
			bean.set("toRowNum", 4);
			bean.set("fromColNum", cellIndex);
			bean.set("toColNum", cellIndex);
			list.add(bean);
			cellIndex++;
		}
		return list;
	}
    /**
     * 去掉不在考勤方案中存在的班次或项目
     * value 选中班次项目
     *
     * @return String  去掉
     */
	private String deleteNotExitsScheme(String value,HashMap<String, LazyDynaBean> styleMap){
        if(StringUtils.isEmpty(value))
            return value;
        List<String> values = new ArrayList<String>(Arrays.asList(value.split(",")));
        Iterator<String> it = values.iterator();
        while(it.hasNext()){
            String  s = it.next();
            LazyDynaBean styleBean = styleMap.get(s);
            if(styleBean==null){
                it.remove();
            }
        }
        return StringUtils.join(values.toArray(),",");
    }
    /**
     * 输出日明细/月汇总 列集合
     * listSimpleColumn
     * @param schemeId
     * @return
     * @throws GeneralException
     * @date 2019年3月4日 下午5:00:11
     * @author linbz
     */
    private ArrayList<ColumnsInfo> listSimpleColumn(String schemeId) throws GeneralException{
        ArrayList<ColumnsInfo> columns = new ArrayList<ColumnsInfo>();
        ColumnsInfo columnsInfo = null;
	    try {
            KqDataSpService kqDataSpService = new KqDataSpServiceImpl(this.frameconn,this.userView);
            Map<String, String> map = kqDataSpService.getExportScheme(schemeId);
            String detailsVal = map.get("detailsVal");
            String sumsVal = map.get("sumsVal");
            String columnStr = "";
            if("daily".equals(export_type)){
                columnStr = ","+detailsVal+",";
            }else{
                columnStr = ","+sumsVal+",";
            }
            if(columnStr.indexOf(",seq,")>-1){
                //添加第一列序号列
                columnsInfo = getColumnsInfo("id", ResourceFactory.getProperty("label.serialnumber"), 50, "", "N", 0, 0);
                columns.add(columnsInfo);
            }
            
            ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("Q35", 1);
            KqDataUtil kqDataUtil = new KqDataUtil(this.userView);
            String onlyFieldName =  kqDataUtil.getOnlyFieldName(this.frameconn);
            boolean isAdded = false;
            for(int i=0;i<fieldList.size();i++) {
                FieldItem fi = fieldList.get(i);
                String itemId = fi.getItemid();
                // 去除没有启用的指标
                if (!"1".equals(fi.getUseflag()))
                    continue;
                // 去除隐藏的指标
                if (!"1".equals(fi.getState()))
                    continue;
                if(StringUtils.isEmpty(itemId))
                    continue;
                if ("only_field".equalsIgnoreCase(itemId)) {
                	fi.setItemdesc(onlyFieldName);
				}
                if(itemId.toUpperCase().startsWith("Q35")
                        && StringUtils.isNumericSpace(itemId.substring(3))
                        && Integer.parseInt(itemId.substring(3))>=1
                        && Integer.parseInt(itemId.substring(3))<=31
                        &&columnStr.indexOf(",dates,")>-1){
                    if(isAdded)
                        continue;
                    isAdded = true;
                    PeriodService periodService = new PeriodServiceImpl(userView, this.frameconn);
                    ArrayList parameterList = new ArrayList();
                    parameterList.add(kq_year);
                    parameterList.add(kq_duration);
                    ArrayList<LazyDynaBean> periods = periodService.listKq_duration(" and kq_year=? and kq_duration=?", parameterList, null);
                    LazyDynaBean periodBean = periods.size()>0?periods.get(0):null;
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
                    //组装日明细列头
                    int temp=1;
                    for(;!kqStartCal.after(kqEndCal);kqStartCal.add(Calendar.DATE, 1)) {
                        int day = kqStartCal.get(Calendar.DAY_OF_MONTH);
                        String dayStr = String.valueOf(day);//day<10?"0"+day:
                        String tempStr = temp<10?"0"+temp:String.valueOf(temp);
                        columnsInfo = getColumnsInfo("Q35"+tempStr, dayStr, 50, "", "A", 0, 0);
                        columns.add(columnsInfo);
                        temp++;
                    }
                    continue;
                }
                if(columnStr.indexOf(","+itemId.toLowerCase()+",")==-1)
                    continue;
                columnsInfo = getColumnsInfoByFi(fi, 100);
                // 44688  防止导出数值过长 宽度过小 会导致excel乱码，考虑到目前导出excel主要是打印看数据则这里强转为字符型
                if("N".equalsIgnoreCase(fi.getItemtype()))
                    columnsInfo.setColumnType("A");
                columns.add(columnsInfo);
            }
            // 去掉固定备注
//            if(columnStr.indexOf(",memo,")>-1){
//                columnsInfo = getColumnsInfo("remark", ResourceFactory.getProperty("report.parse.text"), 100, "", "A", 0, 0);
//                columns.add(columnsInfo);
//            }
        }catch(Exception e){
            throw GeneralExceptionHandler.Handle(e);
        }
        return columns;
    }
	/**
	 * 输出日明细/月汇总 获取SQL
	 * getSimpleSql
	 * @param kq_year
	 * @param kq_duration
	 * @param orgId
	 * @param scheme_id
	 * @param gNo
	 * @param nbases
	 * @return
	 * @date 2019年1月28日 下午4:31:42
	 * @author linbz
	 */
	public String getSimpleSql(String kq_year, String kq_duration, String orgId, String scheme_id
			, String gNo, String cbases) {
		Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.frameconn);
		//是否定义唯一性指标 0：没定义
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		//唯一性指标值
        String onlyname = "0".equals(uniquenessvalid) ? "" : sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		
		String selectSql = "select  "+gNo+" g_no";
		if(StringUtils.isNotBlank(onlyname)) 
			selectSql += ","+onlyname+" only_field_";
		selectSql += ", q35.* from Q35 ";
		StringBuffer whereSql = new StringBuffer();
		whereSql.append(" where a01.guidkey=Q35.guidkey and kq_year='"+kq_year+"' and kq_duration='"+kq_duration+"' and scheme_id='"+scheme_id+"'");
		if(StringUtils.isNotEmpty(orgId)) {
			String[] orgArr = orgId.split(",");
			whereSql.append(" and (");
			for(int i=0;i<orgArr.length;i++) {
				String id = orgArr[i];
				if(StringUtils.isNotBlank(id)){
					id = PubFunc.decrypt(id);
				}
				whereSql.append(" Q35.Org_id like '"+id+"%' or");
			}
			whereSql.setLength(whereSql.length()-2);
			whereSql.append(")");
		}
		StringBuffer sql = new StringBuffer();
		ArrayList<String> dbNames =DataDictionary.getDbpreList();
		for(int i=0;i<dbNames.size();i++) {
			String dbName = (String)dbNames.get(i);
			if(i > 0)
				sql.append(" UNION ALL ");
			sql.append(selectSql + ","+dbName+"A01 a01 ");
			sql.append(whereSql.toString()); 
		}
		return sql.toString();
	}
	/**
	 * 获取列对象
	 * getColumnsInfo
	 * @param columnId
	 * @param columnDesc
	 * @param columnWidth
	 * @param codesetId
	 * @param columnType
	 * @param columnLength
	 * @param decimalWidth
	 * @return
	 * @date 2019年1月28日 下午4:32:00
	 * @author linbz
	 */
	private ColumnsInfo getColumnsInfo(String columnId, String columnDesc,
			int columnWidth, String codesetId, String columnType,
			int columnLength, int decimalWidth) {

		ColumnsInfo columnsInfo = new ColumnsInfo();
		columnsInfo.setColumnId(columnId);
		columnsInfo.setColumnDesc(columnDesc);
		columnsInfo.setColumnWidth(columnWidth);// 显示列宽
		columnsInfo.setCodesetId(codesetId);// 指标集
		columnsInfo.setColumnType(columnType);// 类型N|M|A|D
		columnsInfo.setColumnLength(columnLength);// 显示长度
		columnsInfo.setDecimalWidth(decimalWidth);// 小数位
		if("A".equals(columnType)||
			"M".equals(columnType)||
			"D".equals(columnType)) {
			columnsInfo.setTextAlign("left");
		}else if("N".equals(columnType)){
			columnsInfo.setTextAlign("right");
		}
		return columnsInfo;
	}
	/**
	 * 通过FieldItem获取列对象
	 * getColumnsInfoByFi
	 * @param fi
	 * @param columnWidth
	 * @return
	 * @date 2019年1月28日 下午4:32:19
	 * @author linbz
	 */
	private ColumnsInfo getColumnsInfoByFi(FieldItem fi, int columnWidth){
		ColumnsInfo co = new ColumnsInfo();
		
		String itemid = fi.getItemid();
		String itemdesc = fi.getItemdesc();
		String codesetId = fi.getCodesetid();
		String columnType = fi.getItemtype();
		// 指标长度，非显示长度
		int columnLength = fi.getItemlength();
		// 小数位
		int decimalWidth = fi.getDecimalwidth();
		co = getColumnsInfo(itemid, itemdesc, columnWidth, codesetId,
				columnType, columnLength, decimalWidth);

		return co;
	}
	/**
	 * 输出月汇总 获取统计指标集合
	 * getCollectFielditemid
	 * @param scheme_id
	 * @return
	 * @date 2019年1月28日 下午4:32:47
	 * @author linbz
	 */
	private String getCollectFielditemid(String scheme_id){
		String fields = ",";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select item_ids from kq_scheme where scheme_id=?";
			List values = new ArrayList();
			values.add(scheme_id);
			rs = dao.search(sql,values);  
			if(rs.next()) {
				String item_ids = rs.getString("item_ids");
				if(StringUtils.isNotBlank(item_ids)) {
					values.clear();
					String[] ids = item_ids.split(",");
					sql = "SELECT fielditemid FROM kq_item WHERE "
							+ " fielditemid IN (SELECT fielditemid FROM kq_item WHERE item_id IN (";
					int i = 0;
					for(String id : ids) {
						if(i>0)
							sql+=",";
						sql += "?";
						values.add(id);
						i++;
					}
					sql += "))";
					rs = dao.search(sql, values);
					while(rs.next()) {
						String fielditemid = rs.getString("fielditemid");
						if(StringUtils.isNotBlank(fielditemid))
							fields += fielditemid.toLowerCase()+",";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return fields;
	}
	/**
	 * 获取考勤员名称
	 * getClerkName
	 * @param clerk_fullname	全称
	 * @param clerk_username	用户名
	 * @return
	 * @date 2019年3月1日 上午11:32:12
	 * @author linbz
	 */
	private String getClerkName(String clerk_fullname, String clerk_username){
		// 先获取全称 若没有则获取用户名 否则为空
		String clerkName = clerk_fullname;
    	clerkName = (StringUtils.isBlank(clerkName) || "null".equalsIgnoreCase(clerkName)) ? clerk_username : clerkName;
    	clerkName = "null".equalsIgnoreCase(clerkName) ? "" : clerkName;
		return clerkName;
	}
	/**
     * @Description: 将base64编码字符串转换为图片
     * @Author: 
     * @CreateTime: 
     * @param imgStr base64编码字符串
     * @return
    */
	private byte[] generateImage(String imgStr) {
		byte[] b = null;
        // 解密
        try {
        	BASE64Decoder decoder = new BASE64Decoder();
        	photo=photo.replace(" ", "");
			b = decoder.decodeBuffer(photo);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                	b[i] += 256;
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return b;
    }
	/**
	 * 处理是否需要导出 机构审核人 姓名或签章
	 * @param scheme_id
	 * @param org_id
	 * @param reviewerName
	 * @return
	 * @throws GeneralException
	 */
	private HashMap dealSignatureFunc(String scheme_id, String org_id, String reviewerName) throws GeneralException{
		
		HashMap map = new HashMap();
		try {
			// 校验签章指标是否存在
			DbWizard db = new DbWizard(this.frameconn);
            boolean bool = db.isExistField("kq_extend_log", "signature", false);
            StringBuffer sql = new StringBuffer();
            sql.append("select curr_user,sp_flag");
            if(bool)
            	sql.append(" ,signature");
            sql.append(" from kq_extend_log");
            sql.append(" where scheme_id=? and kq_year=? and kq_duration=? and org_id=?");
            
	    	ArrayList<String> values = new ArrayList<String>();
	    	values.add(scheme_id);
	    	values.add(kq_year);
	    	values.add(kq_duration);
	    	values.add(org_id);
	    	ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql.toString(), values);
			String curr_user = "";
			String signature = "";
			String sp_flag = "";
			while(this.frowset.next()) {
				curr_user = this.frowset.getString("curr_user");
				sp_flag = this.frowset.getString("sp_flag");
				if(bool)
					signature = KqDataUtil.nullif(this.frowset.getString("signature"));
			}
			// 如果是下级机构直接同意后 该用户为机构审核员4(审批状态应为同意或结束)  当为3的时候没有审核人不需考虑导出签章了
			if("1".equals(curr_user) || "2".equals(curr_user) 
					|| ("4".equals(curr_user) && ("03".equals(sp_flag) || "06".equals(sp_flag)))) {
				if(StringUtils.isNotBlank(signature)) {
					KqDataUtil kqDataUtil = new KqDataUtil(this.getUserView());
					HashMap signatureMap = kqDataUtil.getSignaturePhoto(signature, "4");
					SignatureFileBo signatureFileBo = new SignatureFileBo(this.frameconn, this.userView);
					photo = signatureFileBo.getMarkData((String) signatureMap.get("MarkID"), (String) signatureMap.get("SignatureID"), "");
				}
			}else {
				// 没有上报到人事处考勤员或审核人的不需要显示签章或姓名
				photo = "";
				reviewerName = "";
			}
			map.put("photo", photo);
			map.put("reviewerName", reviewerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return map;
	}
	/**
	 * 按照栏目设置排序列头
	 * @param columnsInfos
	 * @param schemeId
	 * @return ArrayList<ColumnsInfo>
	 * @throws GeneralException
	 * @date 2019年10月15日 16:20:15
	 * @author xuanz
	 */
	private ArrayList<ColumnsInfo> sortColumn(ArrayList<ColumnsInfo> columnsInfos,String schemeId,String showMx) throws GeneralException{
		ArrayList<ColumnsInfo> sortColumnList=new ArrayList<ColumnsInfo>();
		ColumnConfig columnConfig = null;
		String subModuleId = "kqdata_"+("true".equals(showMx)?"Mx_":"NoneMx_")+schemeId+"_onlysave";
		TableFactoryBO tableFactoryBO= new TableFactoryBO(subModuleId, this.userView,this.frameconn);
		HashMap layoutConfig = tableFactoryBO.getTableLayoutConfig();
		//有栏目设置则读取栏目设置的配置
        if(layoutConfig!=null){
        	ArrayList columnsConfigs = tableFactoryBO.getTableColumnConfig((Integer)layoutConfig.get("schemeId"));
			if ("序号".equals(columnsInfos.get(0).getColumnDesc())) {
				sortColumnList.add(columnsInfos.get(0));
			}
			for(int i=0;i<columnsConfigs.size();i++) {
				columnConfig = (ColumnConfig)columnsConfigs.get(i);
				for (ColumnsInfo columnsInfo2 : columnsInfos) {
					if (columnsInfo2.getColumnId().equalsIgnoreCase(columnConfig.getItemid())) {
						sortColumnList.add(columnsInfo2);
						break;
					}
				}
			}
			return sortColumnList;
		}else {
			return columnsInfos;
		}
	}
}
