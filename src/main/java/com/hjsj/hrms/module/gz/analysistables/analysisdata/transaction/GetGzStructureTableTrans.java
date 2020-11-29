package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzStructureTableService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.GetGzStructureTableServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
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
public class GetGzStructureTableTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.frameconn, this.userView);
		GetGzStructureTableService service = new GetGzStructureTableServiceImpl(this.userView,this.frameconn,gzAnalysisUtil);
		JSONObject returnJson = new JSONObject();
		boolean fisrt_in = true;
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
				String year = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "year"));//年份（第一次没值传入）
				String salaryid = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "salaryid"));//薪资账套（默认全部）
				String fieldid = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "fieldid"));//分析项（第一次没值传入 默认选第一个）
				String codeitemid = String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "codeitemid"));//分类项（第一次没值传入  默认 部门）
				boolean showNumberOfPeople = (Boolean) gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "showNumberOfPeople");//是否显示每月人数
				boolean collect = (Boolean) gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "collect");//是否按层级汇总 
				int lay = Integer.parseInt(String.valueOf(gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "lay")));//层级值
				
				
				@SuppressWarnings("unused")//高级选项 //新开发暂时未考虑
				//String advancedid = jsonObj.getString("advancedid");
				
				boolean isRefresh = (Boolean) gzAnalysisUtil.getCacheConfig(map_config, jsonObj, "isRefresh");//如果选择薪资类别，需要刷新分析项和分类项
				result.put("isRefresh", isRefresh);
				result.put("year", year);
				result.put("salaryid", salaryid);
				result.put("fieldid", fieldid);
				result.put("codeitemid", codeitemid);
				result.put("showNumberOfPeople", showNumberOfPeople);
				result.put("collect", collect);
				result.put("lay", lay);
				//一些初始的判断
				result.put("not_enc_rsid", rsid);
				result.put("not_enc_rsdtlid", rsdtlid);
				// 根据是否显示每月人数设置栏目设置中的这列的显示与否
				gzAnalysisUtil.setDisplayByShowNumberPerson(showNumberOfPeople);
				HashMap ctrlParamMap = gzAnalysisUtil.getCtrlParam2Map(rsid, rsdtlid);
				String verifying = (String)ctrlParamMap.get("verifying");//是否包含审批数据
				String nbases=(String)ctrlParamMap.get("nbase");//人员库
				String salaryids=(String)ctrlParamMap.get("salaryids");
				if(StringUtils.isNotBlank(year) || StringUtils.isNotBlank(salaryid)) {
					fisrt_in = false;
				}
				if(StringUtils.isBlank(salaryids) || StringUtils.isBlank(nbases)) {
					result.put("yearList", new ArrayList());
					result.put("year", "");
					result.put("salarySetList", new ArrayList());
					result.put("fieldList", new ArrayList());//分析项集合 
					result.put("fieldid", "");
					result.put("codeItemList", new ArrayList());//分类项集合
					result.put("codeitemid", "");
					result.put("levelSum_list", new ArrayList());
				}
				//第一次进入时，没有年份，具体账套，分析项和分类项，找去(或者选择了某个薪资项目，也得刷新一下分析项和分类项)
				else if(fisrt_in || isRefresh) {
					// 切换类别号时，需要重新判断类别是否存在
					if(("," + salaryids + ",").indexOf("," + salaryid + ",") == -1 && !"all".equalsIgnoreCase(salaryid)) {
						salaryid = salaryids.split(",")[0];
						result.put("salaryid", salaryid);
					}
					if(fisrt_in || "all".equalsIgnoreCase(salaryid)) {
						salaryid = salaryids;
					}
					
					//获取年份
					HashMap<String, String> map = new HashMap<String, String>();
					ArrayList year_list = gzAnalysisUtil.getYearList(salaryid, nbases, 1);
					ArrayList<HashMap<String, String>> year_list_val = new ArrayList<HashMap<String, String>>();
					// 年份是否包含默认选择的年份 
					boolean hasDefaultYear = false;
					for(int i = 0; i < year_list.size(); i++) {
						map = new HashMap<String, String>();
						String year_tem = (String)year_list.get(i);
						map.put("id", year_tem);
						map.put("name", year_tem);
						year_list_val.add(map);
						if(year_tem.equalsIgnoreCase(year)) {
							hasDefaultYear = true;
						}
					}
					result.put("yearList", year_list_val);
					if(StringUtils.isBlank(year) || !hasDefaultYear)
						result.put("year", year_list.size()>0?year_list.get(0):"");
					//获取分析项和分类项
					result.putAll(service.findAllSalaryItem(rsdtlid, salaryid, fieldid, codeitemid, nbases, verifying, (String) result.get("year"), lay));
					
					//薪资类别
					ArrayList salaryid_list = new ArrayList();
					HashMap map_t=new HashMap();
					map_t.put("salaryid", "all");
					map_t.put("cname", ResourceFactory.getProperty("label.all"));
					salaryid_list.add(map_t);
					salaryid_list.addAll(service.getSalarySetList(salaryids));
					result.put("salarySetList", salaryid_list);
					if(fisrt_in) {
						result.put("salaryid", (String)(((HashMap)salaryid_list.get(0)).get("salaryid")));
					}
				}
				
				//以上已经将分析项，分类项，年份，薪资类别全部放入map中，接下来拼sql，查数据
				if(this.formHM.get("jsonStr") != null) {
					HashMap result_data_column = service.getAllData(result, showNumberOfPeople, collect, lay, (StringUtils.isNotBlank(salaryid) && !StringUtils.equalsIgnoreCase("all", salaryid))?salaryid:salaryids, nbases, verifying,filterSql, orderSql);
					ArrayList<LazyDynaBean> list_data = (ArrayList<LazyDynaBean>)result_data_column.get("list_data");
					ArrayList<ColumnsInfo> list_column = (ArrayList<ColumnsInfo>)result_data_column.get("list_column");
					result.put("dataTableConfig", service.getTableConfig(list_data, list_column));
					
	                result.put("dataobjs", list_data);
	                result.put("totalCount", list_data.size());
	                this.userView.getHm().put("GzItemSummary_config", result);
				}else {
					ArrayList<LazyDynaBean> list_data = new ArrayList<LazyDynaBean>(); 
					// 如果是通过排序，过滤等进来的需要重新查数据库
					if(StringUtils.isNotBlank(orderSql) || StringUtils.isNotBlank(filterSql)) {
						HashMap result_data_column = service.getAllData(result, showNumberOfPeople, collect, lay, (StringUtils.isNotBlank(salaryid) && !StringUtils.equalsIgnoreCase("all", salaryid))?salaryid:salaryids, nbases, verifying,filterSql, orderSql);
						list_data = (ArrayList<LazyDynaBean>)result_data_column.get("list_data");
					}else {
						list_data = (ArrayList<LazyDynaBean>)map_config.get("dataobjs");
					}
					//sublist 方法 start从0开始，包含开头不包含结尾
	                int start = limit*(page-1);
	                int toIndex = start+limit;
	                int totalCount = list_data.size();
	                if(toIndex>totalCount) {
	                    toIndex = totalCount;
	                }
					this.formHM.put("dataobjs", list_data.subList(start,toIndex));
					this.formHM.put("totalCount", (Integer)map_config.get("totalCount"));
				}
			}else if("2".equals(transType)) {
				String tableName = jsonObj.getString("tableName");
				TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("gzStructure");
				ArrayList<ColumnsInfo> columns_list = tableCache.getDisplayColumns();
				ArrayList<LazyDynaBean> dataList = tableCache.getTableData();
				
				String fielname = service.export_data(rsid, rsdtlid, columns_list, dataList,tableName);
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
			
		}catch (GeneralException e) {
			e.printStackTrace();
			return_code = "fail";
			return_msg = e.getErrorDescription();
		}finally {
			returnJson.put("return_code", return_code);
			returnJson.put("return_msg", return_msg);
		}
		this.getFormHM().put("returnStr", returnJson);
	}

}
