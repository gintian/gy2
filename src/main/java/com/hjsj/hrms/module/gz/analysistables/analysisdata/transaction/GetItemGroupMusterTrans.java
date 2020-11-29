package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.businessobject.sys.EchartsBo;
import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.ItemGroupMusterService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.ItemGroupMusterServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GetItemGroupMusterTrans extends IBusiness{

	
	@Override
    public void execute() throws GeneralException {
		String transType = (String)this.getFormHM().get("transType");
		ItemGroupMusterService itemGroupMusterService = new ItemGroupMusterServiceImpl(this.frameconn);
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
			if ("1".equals(transType)) {
				JSONObject returnData = new JSONObject();
				String codeitem = (String) this.getFormHM().get("codeitem");
				String codevalue = (String) this.getFormHM().get("codevalue");
				String intoflag = (String) this.getFormHM().get("intoflag");
				HashMap paramMap = new HashMap();
				paramMap.put("rsid", rsid);
				paramMap.put("rsdtlid", rsdtlid);
				paramMap.put("nbase", nbase);
				paramMap.put("salaryid", salaryid);
				paramMap.put("codeitem", codeitem);
				paramMap.put("codevalue", codevalue);
				paramMap.put("userView", userView);
				paramMap.put("tableName", tableName);
				paramMap.put("verifying", verifying);
				paramMap.put("year", pyear);
				paramMap.put("intoflag", intoflag);
				String incloudLowLevel = (String) this.getFormHM().get("incloudLowLevel");
				String group = (String) this.getFormHM().get("group");
				String showMx = (String) this.getFormHM().get("showMx");
				String accumulate = (String) this.getFormHM().get("accumulate");
				paramMap.put("incloudLowLevel", incloudLowLevel);
				paramMap.put("group", group);
				paramMap.put("showMx", showMx);
				paramMap.put("accumulate", accumulate);
				ArrayList recordList = new ArrayList();
				ArrayList yearList = new ArrayList();
				ArrayList salaryItemList = new ArrayList();
				if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)) {
					yearList = gzAnalysisUtil.getYearList(salaryid,nbase,Integer.parseInt(verifying));
					if(StringUtils.isBlank(pyear)&&yearList.size()>0) {
						pyear = (String)yearList.get(0);
						paramMap.put("year", pyear);
					}
					if(StringUtils.isNotBlank(pyear))
						recordList = itemGroupMusterService.getRecordList(paramMap);
					// 薪资账套指标项
					salaryItemList = gzAnalysisUtil.getSalaryItemList(salaryid, "AC");
				}
				if ("1".equals(intoflag)) {// 页面模糊查询
					TableDataConfigCache tableCache = (TableDataConfigCache) userView.getHm().get("itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid));
					tableCache.setTableData(recordList);
					this.userView.getHm().put("itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid), tableCache);
					// 获取分析图表数据
					ArrayList headList = itemGroupMusterService.getTableHeadlist(rsdtlid, rsid, "", "",userView);
					ArrayList echartDataList = itemGroupMusterService.getEchartDataList(recordList, headList);
					returnData.put("flag", "1");
					returnData.put("echartDataList", echartDataList);
					returnJson.put("return_data", returnData);
					returnJson.put("return_code", "success");
					returnJson.put("return_msg", "success");
		            this.getFormHM().put("returnStr", returnJson.toString());
					return;
				}
				// 得到数据表格列
				ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
				column = itemGroupMusterService.getItemGroupMusterColumnsInfo(userView, rsid, rsdtlid);
				// 得到人员表格对象
				TableConfigBuilder builder = new TableConfigBuilder("itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid), column, "itemgroupmusterdata1_"+PubFunc.encrypt(rsdtlid), userView,
						this.getFrameconn());
				builder.setDataList(recordList);
				builder.setSelectable(false);
				builder.setSortable(false);
				builder.setLockable(true);
				builder.setPageSize(30);
				builder.setPageTool(false);
				builder.setScheme(true);
				String config = builder.createExtTableConfig();
				returnData.put("tableConfig", config.toString());
				//
				ArrayList yearselectjson = new ArrayList();
				for (int i = 0; i < yearList.size(); i++) {
					LazyDynaBean bean = new LazyDynaBean();
					String year_ = (String) yearList.get(i);
					bean.set("id", year_);
					bean.set("name", year_);
					yearselectjson.add(bean);
				}
				returnData.put("yearselectjson", yearselectjson);
				ArrayList itemlist = new ArrayList();
				for (int i = 0; i < salaryItemList.size(); i++) {
					LazyDynaBean bean = new LazyDynaBean();
					HashMap map = (HashMap) salaryItemList.get(i);
					String itemid = (String) map.get("itemid");
					String itemdesc = (String) map.get("itemdesc");
					String itemcode = (String) map.get("itemcode");
					bean.set("itemcode", itemcode+"`"+itemid);
					bean.set("itemdesc", itemdesc);
					itemlist.add(bean);
				}
				returnData.put("salaryItemList", itemlist);
				// 获取分析图表数据
				ArrayList headList = itemGroupMusterService.getTableHeadlist(rsdtlid, rsid, "", "", userView);
				ArrayList echartDataList = itemGroupMusterService.getEchartDataList(recordList, headList);
				returnData.put("echartDataList", echartDataList);
				returnData.put("flag", "0");
				returnData.put("rsid_d", rsid);
				returnData.put("rsdtlid_d", rsdtlid);
				returnJson.put("return_data", returnData);
			}
			else if ("2".equals(transType)) {
				JSONObject returnData = new JSONObject();
				String charttype = (String) this.getFormHM().get("type");
				String name = (String) this.getFormHM().get("name");
				String showpercent = (String) this.getFormHM().get("showpercent");
				String group = (String) this.getFormHM().get("group");
				charttype = (charttype == null || charttype.length() == 0 || "column".equals(charttype) ? "11"
						: "pie".equals(charttype) ? "20" : "1000");
				if (this.getFormHM().get("data") == null) {
					returnData.put("dataHtml", "");
					returnJson.put("return_data", returnData);
					returnJson.put("return_code", "success");
					returnJson.put("return_msg", "success");
		            this.getFormHM().put("returnStr", returnJson.toString());
					return;
				}
				ArrayList<MorphDynaBean> datalist = (ArrayList<MorphDynaBean>) this.getFormHM().get("data");
				String option = "";
				LinkedHashMap hashMap = new LinkedHashMap();
				if(datalist.size()>0) {
					ArrayList list = new ArrayList();
					CommonData commondata = null;
					if (group == null) {
						for (MorphDynaBean obj : datalist) {
							String dataName = (String) obj.get("dataname");
							String dataValue = String.valueOf(obj.get("datavalue"));
							commondata = new CommonData();
							commondata.setDataName(dataName);
							commondata.setDataValue(dataValue);
							list.add(commondata);
						}
						hashMap.put(name, list);
					}
					int height = -1;
					if (group == null && datalist.size() > 15)
						height = 268;
					if (group != null && ((ArrayList) this.getFormHM().get("field")).size() > 15)
						height = 268;
					EchartsBo bo = new EchartsBo("", Integer.valueOf(charttype), -1, height, "false");
					bo.setNumDecimals(2);
					bo.setIsneedtitle(false);
					bo.setIssupplydecimals(true);
					boolean showPercent = "percentage".equals(showpercent) ? true : false;
					bo.setShowpercent(showPercent);
					if ("11".equals(charttype) && group == null)
						option = bo.outEchartBarXml(list, "", "");
					else if("1000".equals(charttype))
						option =  bo.outEchartLineXml(hashMap, "", "");
					else
						option = bo.outEchartPieXml(list, "", "");
					option = option.replace(",\nheight:chartHeight\n", "");
				}
				returnData.put("dataHtml", option);
				returnJson.put("return_data", returnData);
			}else if("3".equals(transType)) {//页面设置控件初始化
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
			}else if("4".equals(transType)) {//导出excel
				JSONObject returnData = new JSONObject();
				String codeitem = (String) this.getFormHM().get("codeitem");
				String codevalue = (String) this.getFormHM().get("codevalue");
				String intoflag = (String) this.getFormHM().get("intoflag");
				String chartType = (String) this.getFormHM().get("chartType");
				String name = (String) this.getFormHM().get("tableName");
				HashMap paramMap = new HashMap();
				paramMap.put("rsid", rsid);
				paramMap.put("rsdtlid", rsdtlid);
				paramMap.put("nbase", nbase);
				paramMap.put("salaryid", salaryid);
				paramMap.put("codeitem", codeitem);
				paramMap.put("codevalue", codevalue);
				paramMap.put("userView", userView);
				paramMap.put("tableName", tableName);
				paramMap.put("verifying", verifying);
				paramMap.put("year", pyear);
				paramMap.put("intoflag", intoflag);
				String incloudLowLevel = (String) this.getFormHM().get("incloudLowLevel");
				String group = (String) this.getFormHM().get("group");
				String showMx = (String) this.getFormHM().get("showMx");
				String accumulate = (String) this.getFormHM().get("accumulate");
				ArrayList recordList = new ArrayList();
				String subModuleId = "itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid);
				paramMap.put("incloudLowLevel", incloudLowLevel);
				paramMap.put("group", group);
				paramMap.put("showMx", showMx);
				paramMap.put("accumulate", accumulate);
				
				if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)&&StringUtils.isNotBlank(pyear)) {
					recordList = itemGroupMusterService.getRecordList(paramMap);
				}
				
				//ArrayList<ColumnsInfo> column = new ArrayList<ColumnsInfo>();
				//column = itemGroupMusterService.getItemGroupMusterColumnsInfo(userView, rsid, rsdtlid);
				TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("itemgroupmusterdata_"+PubFunc.encrypt(rsdtlid));
			    ArrayList<ColumnsInfo> column = tableCache.getDisplayColumns();
				ReportParseVo reportVo = gzAnalysisUtil.analysePageSettingXml(rsid,rsdtlid);
                //导出Excel
				//得到导出图表的横轴区域和数据区域位置
				int [] chartTextaera = itemGroupMusterService.getChartTextaera(recordList,column);
                String fileName = gzAnalysisUtil.exportExcel(name,reportVo,recordList,column,false,true,chartTextaera,chartType);
                returnData.put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
                returnJson.put("return_data", returnData);
			}else if("5".equals(transType)) {//设置取数范围
				JSONObject returnData = new JSONObject();
				ArrayList yearList = new ArrayList();
				ArrayList salaryItemList = new ArrayList();
				if(StringUtils.isNotBlank(nbase)&&StringUtils.isNotBlank(salaryid)) {
					yearList = gzAnalysisUtil.getYearList(salaryid,nbase,Integer.parseInt(verifying));
					// 薪资账套指标项
					salaryItemList = gzAnalysisUtil.getSalaryItemList(salaryid, "AC");
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
				ArrayList itemlist = new ArrayList();
				for (int i = 0; i < salaryItemList.size(); i++) {
					LazyDynaBean bean = new LazyDynaBean();
					HashMap map = (HashMap) salaryItemList.get(i);
					String itemid = (String) map.get("itemid");
					String itemdesc = (String) map.get("itemdesc");
					String itemcode = (String) map.get("itemcode");
					bean.set("itemcode", itemcode+"`"+itemid);
					bean.set("itemdesc", itemdesc);
					itemlist.add(bean);
				}
				returnData.put("salaryItemList", itemlist);
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
			}
			returnJson.put("return_code", "success");
            returnJson.put("return_msg", "success");
            this.getFormHM().put("returnStr", returnJson.toString());
		}catch (Exception e) {
			e.printStackTrace();
			returnJson.put("return_code", "fail");
            returnJson.put("return_msg", "fail");
            this.getFormHM().put("returnStr", returnJson.toString());
            throw GeneralExceptionHandler.Handle(e);
		}
	}

}
