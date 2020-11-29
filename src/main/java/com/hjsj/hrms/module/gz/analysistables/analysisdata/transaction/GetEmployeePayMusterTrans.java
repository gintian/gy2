package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.EmployeePayMusterService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.EmployeePayMusterServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetEmployeePayMusterTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		String transType = (String)this.getFormHM().get("transType");
		String querytype = (String) this.getFormHM().get("type");
		EmployeePayMusterService employeePayMusterService = new EmployeePayMusterServiceImpl(this.frameconn);
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.frameconn,this.userView);
		String rsid=(String)this.getFormHM().get("rsid");
		rsid = PubFunc.decrypt(rsid);
		String rsdtlid=(String)this.getFormHM().get("rsdtlid");
		rsdtlid = PubFunc.decrypt(rsdtlid);
		HashMap ctrlParamMap = gzAnalysisUtil.getCtrlParam2Map(rsid, rsdtlid);
		String verifying = (String)ctrlParamMap.get("verifying");//是否包含审批数据
		String pyear = (String)this.getFormHM().get("year");
		String nbase=(String)ctrlParamMap.get("nbase");
		String salaryid=(String)ctrlParamMap.get("salaryids");
		String tableName = "salaryhistory";
		JSONObject returnJson = new JSONObject();
		try {
			HashMap customMap = new HashMap();
			if(StringUtils.isNotBlank(querytype)){//页面模糊查询
                StringBuffer condsql = new StringBuffer("");
                TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("employeepaymuster");
                customMap = tableCache.getCustomParamHM();
                MorphDynaBean md=(MorphDynaBean)this.getFormHM().get("customParams");
                if("1".equals(querytype)){//查询栏查询
                	String isSearch = (String) md.get("isSearch");
                	 if(!"false".equalsIgnoreCase(isSearch)) {
	                	 List values = (ArrayList) this.getFormHM().get("inputValues");
		       			 for(int i=0;i<values.size();i++){
	       					String value =SafeCode.decode(values.get(i).toString());
	       					//查询value对应的机构代码
	       					String code = this.getOrgCode(value);
	       					if (i == 0) {
	       						condsql.append(" and (");
	       					}else {
	       						condsql.append(" or ");
	       					}
	       					
	       					if(code.length()>0) {
	       						String[] codearr = code.split(",");
	       						condsql.append("( ");
	       						for(int j=0;j<codearr.length;j++) {
	       							String codeitemid = codearr[j];
	       							FieldItem e0122 = DataDictionary.getFieldItem("e0122");
	       							if(e0122!=null)
	       							{
	       								condsql.append(" e0122 like '"+codeitemid+"%' or ");
	       							}
	       							FieldItem b0110 = DataDictionary.getFieldItem("b0110");
	       							if(b0110!=null)
	       							{
	       								condsql.append(" b0110 like '"+codeitemid+"%') ");
	       							}
	       						}
	       					}else {
	       						FieldItem a0101 = DataDictionary.getFieldItem("a0101");
	       						if(a0101!=null)
	       						{
	       							condsql.append("( a0101 like '%"+value+"%' or ");
	       						}
	       						FieldItem e0122 = DataDictionary.getFieldItem("e0122");
	       						if(e0122!=null)
	       						{
	       							condsql.append(" e0122 like '%"+value+"%' or ");
	       						}
	       						FieldItem b0110 = DataDictionary.getFieldItem("b0110");
	       						if(b0110!=null)
	       						{
	       							condsql.append(" b0110 like '%"+value+"%' or ");
	       						}
	       						condsql.append(" uniqueid like '%"+value+"%') ");
	       					}
	       					if(i == values.size()-1){
	       						condsql.append(" ) ");
	       					}
	       				}
		       			customMap.put("condSql", condsql.toString());
                	}
                }else if("2".equals(querytype)){//方案查询
        			ArrayList fieldsMap=(ArrayList)md.get("fieldsMap");
        			HashMap map=new HashMap();
        			for(int i=0;i<fieldsMap.size();i++){
        				MorphDynaBean fieldMd=(MorphDynaBean)fieldsMap.get(i);
        				String itemid=fieldMd.get("itemid")+"";
        				String itemdesc=fieldMd.get("itemdesc")+"";
        				String itemtype=fieldMd.get("itemtype")+"";
        				String codesetid=fieldMd.get("codesetid")+"";
        				String useflag=fieldMd.get("useflag")+"";
        				FieldItem item = new FieldItem();
        				item.setCodesetid(codesetid);
        				item.setUseflag(useflag);
        				item.setItemtype(itemtype);
        				item.setItemid(itemid);
        				item.setItemdesc(itemdesc);
        				map.put(itemid,item);
        			}
        			 String exp = (String) this.getFormHM().get("exp");
        			 exp = SafeCode.decode(exp);
        	         String cond = (String) this.getFormHM().get("cond");
        	         cond = SafeCode.decode(cond);
        	         FactorList factor_bo=new FactorList(PubFunc.keyWord_reback(exp),PubFunc.keyWord_reback(cond.toUpperCase()),userView.getUserId(),map);
        	         String factorsql = factor_bo.getSingleTableSqlExpression("myGridData");
        	         if(StringUtils.isNotBlank(factorsql)) {
        	        	 factorsql = factorsql.replace("myGridData.", "");
        	        	 condsql.append(" and ");
        	        	 condsql.append(factorsql);
        	         }
        	         customMap.put("condSql", condsql.toString());
        		}else if("3".equals(querytype)) {//机构筛选
        			String ids = (String)this.getFormHM().get("ids");
        			String[] idsArray = ids.split("`");
        			for (int i=0;i<idsArray.length;i++) {
        				String org = PubFunc.decrypt(SafeCode.decode(idsArray[i]));
        				if (i == 0) {
        					condsql.append(" and (");
        				}else {
        					condsql.append(" or ");
        				}

        				FieldItem e0122 = DataDictionary.getFieldItem("e0122");
        				if(e0122!=null)
        				{
        					condsql.append(" e0122 like '"+org+"%' or ");
        				}
        				FieldItem b0110 = DataDictionary.getFieldItem("b0110");
        				if(b0110!=null)
        				{
        					condsql.append(" b0110 like '"+org+"%' ");
        				}
        				if(i == idsArray.length-1){
        					condsql.append(" ) ");
        				}
        			}
        			customMap.put("condSql", condsql.toString());
        		}
                HashMap return_data = new HashMap();
                this.getFormHM().put("return_code", "success");
                this.getFormHM().put("return_msg", "");
                this.getFormHM().put("return_data", return_data);
                return;
			}else {
				if("1".equals(transType)) {
					JSONObject returnData = new JSONObject();
					String intoflag=(String)this.getFormHM().get("intoflag");
					if("1".equals(intoflag)) {//查询进入
						TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("employeepaymuster");
						customMap = tableCache.getCustomParamHM();
						customMap.put("rsid", rsid);
		                customMap.put("rsdtlid", rsdtlid);
		                customMap.put("year", pyear);
		                customMap.put("verifying", verifying);
		                customMap.put("nbase", nbase);
		                customMap.put("salaryid", salaryid);
						returnData.put("flag", "1");
						returnJson.put("return_data", returnData);
						returnJson.put("return_code", "success");
			            returnJson.put("return_msg", "success");
			            this.getFormHM().put("returnStr", returnJson.toString());
						return;
					}
					ArrayList yearList = new ArrayList();
					//得到人员列表数据
					yearList = gzAnalysisUtil.getYearList(salaryid,nbase,Integer.parseInt(verifying));
					if(StringUtils.isBlank(pyear)&&yearList.size()>0) {
						pyear = (String)yearList.get(0);
					}
					//得到人员表格列
					ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
					columnList = employeePayMusterService.getEmployeePersonColumnsInfo();
					//得到人员表格对象
					TableConfigBuilder builder = new TableConfigBuilder("employeepaymuster", columnList, "GZ00000706","employeepaymuster1", userView,this.getFrameconn());
					builder.setSelectable(true);
					builder.setSortable(false);
					builder.setPageSize(20);
					builder.setTdMaxHeight(70);
					String config = builder.createExtTableConfig();
					returnData.put("tableConfig", config.toString());
					ArrayList fieldsArray = new ArrayList();
					ArrayList fieldsMap = new ArrayList();
					for(int i=0;i<columnList.size();i++) {
						ColumnsInfo column = columnList.get(i);
						LazyDynaBean item = new LazyDynaBean();
						HashMap map = new HashMap();
						if("e0122".equalsIgnoreCase(column.getColumnId())||"b0110".equalsIgnoreCase(column.getColumnId())
								||"a0101".equalsIgnoreCase(column.getColumnId())){
				            item.set("codesetid", column.getCodesetId());
				            item.set("useflag", "1");
				            item.set("itemtype", column.getColumnType());
				            item.set("itemid", column.getColumnId().toUpperCase());
				            item.set("itemdesc", column.getColumnDesc());
				            map.put("type", column.getColumnType());
				            map.put("itemid", column.getColumnId().toUpperCase());
				            map.put("itemdesc", column.getColumnDesc());
				            map.put("codesetid", column.getCodesetId());
				            map.put("codesetValid", false);
				            fieldsMap.add(item);
				            fieldsArray.add(map);
						}
					}
					returnData.put("fieldsMap", fieldsMap);
					returnData.put("fieldsArray", fieldsArray);
					ArrayList yearselectjson = new ArrayList();
					for(int i=0;i<yearList.size();i++) {
						LazyDynaBean bean = new LazyDynaBean();
						String year = (String)yearList.get(i);
						bean.set("id", year);
						bean.set("name", year);
						yearselectjson.add(bean);
					}
					returnData.put("yearselectjson", yearselectjson);
					boolean isHaveOnlyField = false;
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
					String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
		            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "valid");
		            if (!"0".equals(uniquenessvalid) && onlyname != null && !"".equals(onlyname)) {
						isHaveOnlyField = true;
					}
					returnData.put("isHaveOnlyField", isHaveOnlyField);
					returnData.put("rsid_d", rsid);
					returnData.put("rsdtlid_d", rsdtlid);
					returnData.put("flag", "0");
					returnJson.put("return_data", returnData);
					//存放到tablecahe中的参数
					customMap.put("rsid", rsid);
	                customMap.put("rsdtlid", rsdtlid);
	                customMap.put("year", pyear);
	                customMap.put("verifying", verifying);
	                customMap.put("nbase", nbase);
	                customMap.put("salaryid", salaryid);
	                TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get("employeepaymuster");
	                cache.setCustomParamHM(customMap);
				}
				else if("2".equals(transType)) {
					JSONObject returnData = new JSONObject();
					String fielditemid=(String)this.getFormHM().get("fielditemid");
					String fielditemvalue=(String)this.getFormHM().get("fielditemvalue");
					String intoflag=(String)this.getFormHM().get("intoflag");
					String objectid=(String)this.getFormHM().get("objectid");
					if(StringUtils.isNotEmpty(objectid)) {
						objectid = PubFunc.decrypt(objectid);
					}
					HashMap paramMap = new HashMap();
					paramMap.put("rsid", rsid);
					paramMap.put("rsdtlid", rsdtlid);
					paramMap.put("nbase", nbase);
					paramMap.put("salaryid", salaryid);
					paramMap.put("objectid", objectid);
					paramMap.put("fielditemid", fielditemid);
					paramMap.put("fielditemvalue", fielditemvalue);
					paramMap.put("userView", userView);
					paramMap.put("tableName", tableName);
					paramMap.put("verifying", verifying);
					paramMap.put("year", pyear);
					paramMap.put("intoflag", intoflag);
					ArrayList recordList = new ArrayList();
					ArrayList yearList = new ArrayList();
					if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)) {
						if(StringUtils.isNotBlank(pyear))
							recordList = employeePayMusterService.getRecordList(paramMap);
						yearList = gzAnalysisUtil.getYearList(salaryid,nbase,Integer.parseInt(verifying));
					}
					if("1".equals(intoflag)){//页面模糊查询
						TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("employeepaymusterdata_"+PubFunc.encrypt(rsdtlid));
						tableCache.setTableData(recordList);
						this.userView.getHm().put("employeepaymusterdata_"+PubFunc.encrypt(rsdtlid), tableCache);
						returnData.put("flag", "1");
						returnJson.put("return_data", returnData);
						returnJson.put("return_code", "success");
			            returnJson.put("return_msg", "success");
			            this.getFormHM().put("returnStr", returnJson.toString());
						return;
					} 
					//得到数据表格列
					ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
					column = employeePayMusterService.getEmployeePayMusterColumnsInfo(userView,rsid,rsdtlid);
					//得到人员表格对象
					TableConfigBuilder builder = new TableConfigBuilder("employeepaymusterdata_"+PubFunc.encrypt(rsdtlid), column, "employeepaymusterdata1_"+PubFunc.encrypt(rsdtlid), userView,this.getFrameconn());
					builder.setDataList(recordList);
					builder.setSelectable(false);
					builder.setSortable(false);
					builder.setPageSize(20);
					builder.setLockable(true);
					builder.setPageTool(false);
					builder.setTdMaxHeight(70);
					builder.setScheme(true);
					String config = builder.createExtTableConfig();
					returnData.put("tableConfig", config.toString());
					ArrayList yearselectjson = new ArrayList();
					for(int i=0;i<yearList.size();i++) {
						LazyDynaBean bean = new LazyDynaBean();
						String year_ = (String)yearList.get(i);
						bean.set("id", year_);
						bean.set("name", year_);
						yearselectjson.add(bean);
					}
					returnData.put("yearselectjson", yearselectjson);
					returnData.put("flag", "0");
					returnJson.put("return_data", returnData);
				}
				else if("3".equals(transType)) {//导出excel|pdf
					JSONObject returnData = new JSONObject();
					String objectids=(String)this.getFormHM().get("objectids");
					String intoflag=(String)this.getFormHM().get("intoflag");
					String sheetnames = (String)this.getFormHM().get("sheetnames");
					String outflag = (String)this.getFormHM().get("outflag");
					String name = (String)this.getFormHM().get("tableName");
					ArrayList recordList = new ArrayList();
					if("1".equals(outflag)) {//部分导出
						String[] sheetnamearr = sheetnames.split(",");
						if(StringUtils.isNotEmpty(objectids)) {
							String[] objectsarr = objectids.split(",");
							for(int i=0;i<objectsarr.length;i++) {
								String objectid = PubFunc.decrypt(objectsarr[i]);
								HashMap paramMap = new HashMap();
								paramMap.put("rsid", rsid);
								paramMap.put("rsdtlid", rsdtlid);
								paramMap.put("nbase", nbase);
								paramMap.put("salaryid", salaryid);
								paramMap.put("objectid", objectid);
								paramMap.put("userView", userView);
								paramMap.put("tableName", tableName);
								paramMap.put("verifying", verifying);
								paramMap.put("year", pyear);
								paramMap.put("intoflag", intoflag);
								HashMap map = new HashMap();
								ArrayList list = new ArrayList();
								if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)&&StringUtils.isNotBlank(pyear)) {
									list = employeePayMusterService.getRecordList(paramMap);
								}
								map.put(sheetnamearr[i], list);
								recordList.add(map);
							}
						}
					}else {//全部导出
						recordList = this.getAllRecordList();
					}
					TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("employeepaymusterdata_"+PubFunc.encrypt(rsdtlid));
				    ArrayList<ColumnsInfo> column = tableCache.getDisplayColumns();
			    	ReportParseVo reportVo = gzAnalysisUtil.analysePageSettingXml(rsid,rsdtlid);
	                //导出Excel
	                String fileName = gzAnalysisUtil.exportExcel(name,reportVo,recordList,column,true,false,null,"");
	                returnData.put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
	                returnJson.put("return_data", returnData);
				}else if("4".equals(transType)) {
					//页面设置控件初始化
					String opt = (String) this.getFormHM().get("opt");
					if("2".equals(opt)){
		                MorphDynaBean pagesetupValue = (MorphDynaBean)this.getFormHM().get("pagesetupValue");
		                MorphDynaBean titleValue = (MorphDynaBean) this.getFormHM().get("titleValue");
		                MorphDynaBean pageheadValue = (MorphDynaBean)this.getFormHM().get("pageheadValue");
		                MorphDynaBean pagetailidValue = (MorphDynaBean)this.getFormHM().get("pagetailidValue");
		                MorphDynaBean textValueValue = (MorphDynaBean)this.getFormHM().get("textValueValue");
		                ReportParseVo rpv = ReportParseVo.setReportDetailXml(pagesetupValue,titleValue,pageheadValue,pagetailidValue,textValueValue);
		                gzAnalysisUtil.saveXML(rpv,rsid,rsdtlid);
		            }else{
		                ReportParseVo orp = gzAnalysisUtil.analysePageSettingXml(rsid,rsdtlid);
		                this.getFormHM().put("isExcel", "0");
		                // 页面设置页签数据
		                this.getFormHM().put("Pagetype", orp.getPagetype());
		                this.getFormHM().put("Top", orp.getTop());
		                this.getFormHM().put("Left", orp.getLeft());
		                this.getFormHM().put("Top", orp.getTop());
		                this.getFormHM().put("Orientation", orp.getOrientation());
		                this.getFormHM().put("Right", orp.getRight());
		                this.getFormHM().put("Bottom", orp.getBottom());
		                this.getFormHM().put("Height", orp.getHeight());
		                this.getFormHM().put("Width", orp.getWidth());
		                // 标题页签数据
		                this.getFormHM().put("title_content", orp.getTitle_fw());
		                this.getFormHM().put("title_fontface", orp.getTitle_fn());
		                this.getFormHM().put("title_fontsize", orp.getTitle_fz());
		                this.getFormHM().put("title_fontblob", orp.getTitle_fb());
		                this.getFormHM().put("title_underline", orp.getTitle_fu());
		                this.getFormHM().put("title_fontitalic", orp.getTitle_fi());
		                this.getFormHM().put("title_delline", orp.getTitle_fs());
		                this.getFormHM().put("title_color", orp.getTitle_fc());
		                // 页头页签数据
		                this.getFormHM().put("head_left", orp.getHead_flw());
		                // System.out.print(orp.getHead_flw());
		                this.getFormHM().put("head_center", orp.getHead_fmw());
		                this.getFormHM().put("head_right", orp.getHead_frw());
		                this.getFormHM().put("head_fontblob", orp.getHead_fb());
		                this.getFormHM().put("head_underline", orp.getHead_fu());
		                this.getFormHM().put("head_fontitalic", orp.getHead_fi());
		                this.getFormHM().put("head_delline", orp.getHead_fs());
		                this.getFormHM().put("head_fontface", orp.getHead_fn());
		                this.getFormHM().put("head_fontsize", orp.getHead_fz());
		                this.getFormHM().put("head_fc", orp.getHead_fc());
		                this.getFormHM().put("head_flw_hs", orp.getHead_flw_hs());
		                this.getFormHM().put("head_fmw_hs", orp.getHead_fmw_hs());
		                this.getFormHM().put("head_frw_hs", orp.getHead_frw_hs());
		                // 页尾页签数据
		                this.getFormHM().put("tail_left", orp.getTile_flw());
		                this.getFormHM().put("tail_center", orp.getTile_fmw());
		                this.getFormHM().put("tail_right", orp.getTile_frw());
		                // this.getFormHM().put("title_content",orp.getHead_fw());
		                this.getFormHM().put("tail_fontface", orp.getTile_fn());
		                this.getFormHM().put("tail_fontsize", orp.getTile_fz());
		                this.getFormHM().put("tail_fontblob", orp.getTile_fb());
		                this.getFormHM().put("tail_underline", orp.getTile_fu());
		                this.getFormHM().put("tail_fontitalic", orp.getTile_fi());
		                this.getFormHM().put("tail_delline", orp.getTile_fs());
		                this.getFormHM().put("tail_fc", orp.getTile_fc());
		                this.getFormHM().put("tail_flw_hs", orp.getTile_flw_hs());
		                this.getFormHM().put("tail_fmw_hs", orp.getTile_fmw_hs());
		                this.getFormHM().put("tail_frw_hs", orp.getTile_frw_hs());
		                // 正文页签数据
		                this.getFormHM().put("text_fn", orp.getBody_fn());
		                this.getFormHM().put("text_fz", orp.getBody_fz());
		                this.getFormHM().put("text_fb", orp.getBody_fb());
		                this.getFormHM().put("text_fu", orp.getBody_fu());
		                this.getFormHM().put("text_fi", orp.getBody_fi());
		                this.getFormHM().put("text_fc", orp.getBody_fc());
		                this.getFormHM().put("phead_fn", orp.getThead_fn());
		                this.getFormHM().put("phead_fz", orp.getThead_fz());
		                this.getFormHM().put("phead_fb", orp.getThead_fb());
		                this.getFormHM().put("phead_fu", orp.getThead_fu());
		                this.getFormHM().put("phead_fi", orp.getThead_fi());
		                this.getFormHM().put("phead_fc", orp.getThead_fc());
		            }
				}else if("5".equals(transType)) {//设置取数范围
					JSONObject returnData = new JSONObject();
					ArrayList yearList = new ArrayList();
					if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)) {
						yearList = gzAnalysisUtil.getYearList(salaryid,nbase,Integer.parseInt(verifying));
					}
					ArrayList yearselectjson = new ArrayList();
					for(int i=0;i<yearList.size();i++) {
						LazyDynaBean bean = new LazyDynaBean();
						String year = (String)yearList.get(i);
						bean.set("id", year);
						bean.set("name", year);
						yearselectjson.add(bean);
					}
					returnData.put("yearselectjson", yearselectjson);
		            returnJson.put("return_data", returnData);
				}else if("6".equals(transType)) {//保存列宽度改变到栏目设置
					JSONObject returnData = new JSONObject();
					String codeitemid = (String)this.getFormHM().get("codeitemid");
					String submoduleid = (String)this.getFormHM().get("submoduleid");
					String width = (String)this.getFormHM().get("width");
					String isshare = (String)this.getFormHM().get("isshare");
					HashMap map = new HashMap();
					map.put("codeitemid", codeitemid);
					map.put("submoduleid", submoduleid);
					map.put("width", width);
					map.put("isshare", isshare);
					gzAnalysisUtil.setChange2SchemeSet(map);
		            returnJson.put("return_data", returnData);
				}else if("7".equals(transType)) {//保存列顺序改变到栏目设置以及指标顺序
					String is_lock = (String)this.getFormHM().get("is_lock");
					String subModuleId = (String)this.getFormHM().get("submoduleid");
					String itemid = (String)this.getFormHM().get("itemid");
					String nextid = (String)this.getFormHM().get("nextid");
                    
    				HashMap map = new HashMap();
    		    	
    				map.put("subModuleId", subModuleId);
    				map.put("itemid", itemid);
    				map.put("nextid", nextid);
    				map.put("is_lock", is_lock);
    				map.put("rsdtlid", rsdtlid);
    				map.put("rsid", rsid);
    				gzAnalysisUtil.saveCloumnMove(map);
    			}else {
	                TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get("employeepaymuster");
	                customMap = cache.getCustomParamHM();
	                rsdtlid = (String)customMap.get("rsdtlid");
	                ArrayList<LazyDynaBean> headList = employeePayMusterService.getPersonHeadlist();
	                int limit = Integer.valueOf((String)this.getFormHM().get("limit"));
	                int page = Integer.valueOf((String)this.getFormHM().get("page"));
	                String condSql = "";
	                if(customMap.containsKey("condSql")){
	                    condSql = (String)customMap.get("condSql");
	                }
	                customMap.put("condSql",condSql);
	                HashMap paramMap = (HashMap)customMap.clone();
	                paramMap.put("headList",headList);
	                paramMap.put("limit",limit);
	                paramMap.put("page",page);
	                paramMap.put("condSql",condSql);
	                paramMap.put("userview",this.userView);
	                ArrayList<LazyDynaBean> dataList = employeePayMusterService.getPersonDataList(paramMap);
	                int endIndex = page*limit;
	
	                int count = employeePayMusterService.getDataCount(paramMap);
	                this.formHM.put("dataobjs", dataList);
	                this.formHM.put("totalCount", count);
				}
			}
			returnJson.put("return_code", "success");
            returnJson.put("return_msg", "success");
            this.getFormHM().put("returnStr", returnJson.toString());
		}catch(Exception e) {
			e.printStackTrace();
			returnJson.put("return_code", "fail");
            returnJson.put("return_msg", "fail");
            this.getFormHM().put("returnStr", returnJson.toString());
            throw GeneralExceptionHandler.Handle(e);
		}
	}
	private ArrayList getAllRecordList() {
		ArrayList recordList = new ArrayList();
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			EmployeePayMusterService employeePayMusterService = new EmployeePayMusterServiceImpl(this.frameconn);
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("employeepaymuster");
			HashMap customMap = tableCache.getCustomParamHM();
	        String rsdtlid = (String)customMap.get("rsdtlid");
	        String condSql = "";
	        if(customMap.containsKey("condSql")){
	            condSql = (String)customMap.get("condSql");
	        }
	        String pre = (String) customMap.get("nbase");
	        String year = (String) customMap.get("year");
	        String verifying = (String) customMap.get("verifying");
	        String salaryid = (String) customMap.get("salaryid");
	        String rsid = (String) customMap.get("rsid");
			String sql = employeePayMusterService.getPersonSql(pre, year, salaryid, "salaryhistory", userView, verifying,"0",condSql);
	        rset = dao.search(sql);
	        while(rset.next()) {
	        	String objectid = rset.getString("objectid");
	        	String a0101 = rset.getString("a0101");
				HashMap paramMap = new HashMap();
				paramMap.put("rsid", rsid);
				paramMap.put("rsdtlid", rsdtlid);
				paramMap.put("nbase", pre);
				paramMap.put("salaryid", salaryid);
				paramMap.put("objectid", objectid);
				paramMap.put("userView", userView);
				paramMap.put("tableName", "salaryhistory");
				paramMap.put("verifying", verifying);
				paramMap.put("year", year);
				paramMap.put("intoflag", "1");
				HashMap map = new HashMap();
				ArrayList list = new ArrayList();
				if(StringUtils.isNotBlank(pre)&&StringUtils.isNotBlank(salaryid)&&StringUtils.isNotBlank(year)) {
					list = employeePayMusterService.getRecordList(paramMap);
				}
				map.put(a0101, list);
				recordList.add(map);
	        }
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return recordList;
	}
	private String getOrgCode(String value) {
		String code = "";
		RowSet rs = null;
        ContentDAO dao = new ContentDAO(this.frameconn);
		String sql = "SELECT codeitemid from organization where codeitemdesc like '%"+value+"%' and codesetid in ('UN','UM')";
		try {
			rs = dao.search(sql);
			while(rs.next()) {
				String codeitemid = rs.getString("codeitemid");
				code += codeitemid+",";
			}
			if(code.length()>0) {
				code = code.substring(0,code.length()-1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
		return code;
	}
}
