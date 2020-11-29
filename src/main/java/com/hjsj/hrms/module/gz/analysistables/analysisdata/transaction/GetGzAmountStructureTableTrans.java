package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.GetGzAmountStructureTableService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.GetGzAmountStructureTableServiceImpl;
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
 * 工资总额构成分析表
 * 1.某一年度工资总额构成分析  在工资总额构成分析分析界面，指定某一年度、截止月份，对某一年度截止某一月份进行工资总额构成分析。
 * 2.部分人员工资总额构成比对分析
 *	进入工资总额构成分析分析界面后，默认显示为全部人员，选择“部分”后，工具栏增加两个选项，选择不同的分类指标、指标项，对某一部分人员进行工资总额分析
 * 3.增长额=本年度值-上年度值，增长率=增长额/上年值*100 ，当上年值为空时增长率为0
 * 4.数值型指标参与计算时需将空值默认为0
 *
 */
public class GetGzAmountStructureTableTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		GetGzAmountStructureTableService service = new GetGzAmountStructureTableServiceImpl(this.userView,this.frameconn);
		GzAnalysisUtil gzAnalysisUtil = new GzAnalysisUtil(this.frameconn, this.userView);
		JSONObject returnJson = new JSONObject();
		HashMap<String, Object> result = new HashMap<String, Object>();//第一次进入时，对应分析项，分类项
		String return_code="success";
		String return_msg = "success";
		try {
			String jsonStr = (String)this.formHM.get("jsonStr");
			JSONObject jsonObj = JSONObject.fromObject(jsonStr);//获取前台json数据
			String transType = jsonObj.getString("transType");//1:生成台账报表数据 2:导出excel
			
			String rsid = jsonObj.getString("rsid");//报表种类编号(加密)
			String rsdtlid = jsonObj.getString("rsdtlid");//报表编号(加密)
			rsid = PubFunc.decrypt(rsid);
			rsdtlid = PubFunc.decrypt(rsdtlid);
			if("1".equals(transType)) {
				String year = jsonObj.getString("year");//年份（第一次没值传入）
				String month = jsonObj.getString("month");//月份（第一次默认12）
				String fieldid = jsonObj.getString("fieldid");//分析项
				String codevalue = jsonObj.getString("codevalue");//代码值
				boolean selectAll= jsonObj.getBoolean("selectAll");//是否是选择全部
				String fieldListId= jsonObj.get("fieldListId") == null?"" : jsonObj.getString("fieldListId");
				
				month = StringUtils.isBlank(month)?"12":month;//默认是12月
				result.put("year", year);
				result.put("month", month);
				result.put("fieldid", fieldid);
				result.put("codevalue", codevalue);
				result.put("fieldListId", fieldListId);
				
				//准备数据
				//需要加密的参数，否则调用取数范围可能有问题
				result.put("not_enc_rsid", rsid);
				result.put("not_enc_rsdtlid", rsdtlid);
				HashMap ctrlParamMap = gzAnalysisUtil.getCtrlParam2Map(rsid, rsdtlid);
				String verifying = (String)ctrlParamMap.get("verifying");//是否包含审批数据
				String nbases=(String)ctrlParamMap.get("nbase");//人员库
				String salaryids=(String)ctrlParamMap.get("salaryids");
				
				verifying = StringUtils.isNotBlank(verifying)?verifying:"0";
				//获取年份
				HashMap<String, String> map = new HashMap<String, String>();
				ArrayList year_list = gzAnalysisUtil.getYearList(salaryids, nbases, Integer.parseInt(verifying));
				ArrayList<HashMap<String, String>> year_list_val = new ArrayList<HashMap<String, String>>();
				
				boolean isExists = false;
				for(int i = 0; i < year_list.size(); i++) {
					map = new HashMap<String, String>();
					String year_tem = (String)year_list.get(i);
					map.put("id", year_tem);
					map.put("name", (String)year_list.get(i));
					year_list_val.add(map);
					if(year.equalsIgnoreCase(year_tem)) {
						isExists = true;
					}
				}
				result.put("yearList", year_list_val);
				if(StringUtils.isBlank(year) || !isExists) 
					result.put("year", year_list.size()>0?year_list.get(0):"");
					
				result.putAll(service.findAllSalaryItem(rsdtlid, salaryids, fieldid, codevalue));
				year = (String)result.get("year");
				year = StringUtils.isBlank(year)?"0":year;
				
				fieldid = (String)result.get("fieldid");
				
				//接下来拼sql，查数据
				HashMap result_data_column = service.getAllData(selectAll, rsdtlid, Integer.parseInt(year), Integer.parseInt(month), 
													StringUtils.isBlank(fieldid)?"":fieldid.split("`")[0], (String)result.get("codevalue"), salaryids, nbases, verifying);
				ArrayList<LazyDynaBean> list_data = (ArrayList<LazyDynaBean>)result_data_column.get("list_data");
				ArrayList<ColumnsInfo> list_column = (ArrayList<ColumnsInfo>)result_data_column.get("list_column");
				result.put("dataTableConfig", service.getTableConfig(list_data, list_column, PubFunc.encrypt(rsdtlid)));
			}else if("2".equals(transType)) {
				String tableName = jsonObj.getString("tableName");
				TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("gzAmountStructure_" + PubFunc.encrypt(rsdtlid));
				ArrayList<ColumnsInfo> columns_list = tableCache.getDisplayColumns();
				ArrayList<LazyDynaBean> dataList = tableCache.getTableData();
				
				String fielname = service.export_data(rsid, rsdtlid,columns_list, dataList,tableName);
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
