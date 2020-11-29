package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzItemSummaryTableService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.GetGzItemSummaryTableServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * 1.分类指标为空的数据也需单独统计，总计包含分类指标值为空的数据
 * 2.年份选项为统计范围内可查到的薪资或保险数据涉及的归属日期年份，按倒序排列
 * 3.平均人数=每月人数/12 ，  月均值=合计/总人数
 * 4.选中“按层级汇总”，程序自动统计分类指标项下一级代码各月的汇总值，最多提供5级以内各层代码的汇总值
 *
 */
public class GetGzItemSummaryTableTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		GetGzItemSummaryTableService service = new GetGzItemSummaryTableServiceImpl(this.userView,this.frameconn);
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.frameconn, this.userView);
		JSONObject returnJson = new JSONObject();
		HashMap<String, Object> result = new HashMap<String, Object>();//第一次进入时，对应分析项，分类项
		String return_code="success";
		String return_msg = "success";
		try {
			HashMap map_config = null;
			JSONObject jsonObj = null;   
			String filterSql = "";
			String orderSql = "";
			int limit = 0;
			int page = 0;
			if(this.formHM.get("jsonStr") != null) {
				String jsonStr = (String)this.formHM.get("jsonStr");
				jsonObj = JSONObject.fromObject(jsonStr);//获取前台json数据
			}else {
				//用作分页，过滤，排序等
				map_config = (HashMap) this.userView.getHm().get("GzItemSummary_config");
				limit = Integer.valueOf((String)this.formHM.get("limit"));
                page = Integer.valueOf((String)this.formHM.get("page"));
                String filterParam = (String)this.formHM.get("filterParam");
                String sort = (String)this.formHM.get("sort");
                orderSql = gzAnalysisUtil.createOrderBySql(sort);
                filterSql = gzAnalysisUtil.createFilterSql(filterParam);
			}
			String transType = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "transType"));//1:生成台账报表数据 2:导出excel
			String rsid = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "rsid"));//报表种类编号(加密)
			String rsdtlid = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "rsdtlid"));//报表编号(加密)
			result.put("rsid", rsid);
			result.put("rsdtlid", rsdtlid);
			result.put("transType", transType);
			
			rsid = PubFunc.decrypt(rsid);
			rsdtlid = PubFunc.decrypt(rsdtlid);
			if("1".equals(transType)) {
				String year = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "year"));//年份
				String month = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "month"));//月份
				
				String fromYear = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "fromYear"));//起始年份
				String endYear = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "endYear"));//截止年份
				boolean appointtime = (Boolean) gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "appointtime");//按照指定日期查找
				result.put("year", year);
				result.put("fromYear", fromYear);
				result.put("endYear", endYear);
				result.put("appointtime", appointtime);
				//准备数据
				result.put("not_enc_rsid", rsid);
				result.put("not_enc_rsdtlid", rsdtlid);
				HashMap ctrlParamMap = gzAnalysisUtil.getCtrlParam2Map(rsid, rsdtlid);
				String verifying = (String)ctrlParamMap.get("verifying");//是否包含审批数据
				String nbases=(String)ctrlParamMap.get("nbase");//人员库
				String salaryids=(String)ctrlParamMap.get("salaryids");
				verifying = StringUtils.isNotBlank(verifying)?verifying : "0";
				//第一次进入时，没有年份，具体账套，分析项和分类项，找去(或者选择了某个薪资项目，也得刷新一下分析项和分类项)
				//获取年份
				boolean isExists = false;
				
				StringBuffer dateJson = new StringBuffer("[");
				ArrayList A00z0List = gzAnalysisUtil.getA00Z0List(salaryids, nbases, 1);
				String max_year = "";
				String max_month = "";
				for(int i = 0; i < A00z0List.size(); i++) {
					String[] a00z0_ = ((String)A00z0List.get(i)).split("-");
					String year_tem = a00z0_[0];
					String month_tem = a00z0_[1];
					if(i > 0) {
						dateJson.append(",");
					}else {
						max_year = year_tem;
						max_month = month_tem;
					}
					dateJson.append("{year:'" + year_tem + "',monthOrder:" + month_tem + ",desc:'" + month_tem + 
										ResourceFactory.getProperty("columns.archive.month") + "',state:2}");
					if(year.equalsIgnoreCase(year_tem)) {
						isExists = true;
					}
				}
				dateJson.append("]");
				result.put("dateJson", JSONArray.fromObject(dateJson.toString()));
				if(StringUtils.isBlank(year) || !isExists) 
					result.put("year", StringUtils.isNotBlank(max_year)?max_year:"");
				
				month = StringUtils.isNotBlank(month)?month : max_month;
				result.put("month", month);
				this.userView.getHm().put("GzItemSummary_config", result);
				ArrayList<LazyDynaBean> list_data = new ArrayList<LazyDynaBean>();
				ArrayList<ColumnsInfo> list_column = new ArrayList<ColumnsInfo>();
				//if(map_config != null) {
					//以上已经将分析项，分类项，年份，薪资类别全部放入map中，接下来拼sql，查数据
					HashMap result_data_column = service.getAllData(result, salaryids, nbases, verifying, limit, page, filterSql, orderSql);
					list_data = (ArrayList<LazyDynaBean>)result_data_column.get("list_data");
					list_column = (ArrayList<ColumnsInfo>)result_data_column.get("list_column");
				//}
				result.put("dataTableConfig", service.getTableConfig(list_data, list_column, PubFunc.encrypt(rsdtlid)));
				
				//sublist 方法 start从0开始，包含开头不包含结尾
                int start = limit*(page-1);
                int toIndex = start+limit;
                int totalCount = list_data.size();
                if(toIndex>totalCount) {
                    toIndex = totalCount;
                }

				this.formHM.put("dataobjs", list_data.subList(start,toIndex));
				this.formHM.put("totalCount", list_data.size());
			}else if("2".equals(transType)) {
				String tableName = jsonObj.getString("tableName");
				TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("GzItemSummary_" + PubFunc.encrypt(rsdtlid));
				ArrayList<ColumnsInfo> columns_list = tableCache.getDisplayColumns();
				ArrayList<LazyDynaBean> dataList = tableCache.getTableData();
				
				String fielname = service.export_data(rsid, rsdtlid, columns_list, dataList, tableName);
				result.put("fileName", SafeCode.encode(PubFunc.encrypt(fielname)));
			}else if("3".equals(transType)) {//页面设置控件初始化
				String opt = jsonObj.getString("opt");
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
			}else if("5".equals(transType)) {//保存列宽度改变到栏目设置
				String codeitemid = jsonObj.getString("codeitemid");
				String submoduleid = jsonObj.getString("submoduleid");
				String width = jsonObj.getString("width");
				String isshare = jsonObj.getString("isshare");
				HashMap map = new HashMap();
				map.put("codeitemid", codeitemid);
				map.put("submoduleid", submoduleid);
				map.put("width", width);
				map.put("isshare", isshare);
				gzAnalysisUtil.setChange2SchemeSet(map);
			}else if("6".equals(transType)) {//保存列顺序改变到栏目设置以及指标顺序
				String is_lock = jsonObj.getString("is_lock");
				String subModuleId = jsonObj.getString("submoduleid");
				String itemid = jsonObj.getString("itemid");
				String nextid = jsonObj.getString("nextid");
				HashMap map = new HashMap();
		    	
				map.put("subModuleId", subModuleId);
				map.put("itemid", itemid);
				map.put("nextid", nextid);
				map.put("is_lock", is_lock);
				map.put("rsdtlid", rsdtlid);
				map.put("rsid", rsid);
				gzAnalysisUtil.saveCloumnMove(map);
			}
			returnJson.put("return_data", result);
			
		}catch (Exception e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = ResourceFactory.getProperty("kq.date.error.export");
		}finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
		}
		this.getFormHM().put("returnStr", returnJson);
	}

}
