package com.hjsj.hrms.module.gz.analysistables.analysisdata.transaction;

import com.hjsj.hrms.interfaces.report.ReportParseVo;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.SummaryTableService;
import com.hjsj.hrms.module.gz.analysistables.analysisdata.businessobject.impl.SummaryTableServiceImpl;
import com.hjsj.hrms.module.gz.analysistables.util.GzAnalysisUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * 人员工资(保险)汇总表交易
 * @author haosl
 * @date 2019.08.1
 */
public class GetSummaryTableTrans extends IBusiness {
    /**
     * transType
     * 1：生成台账报表数据
     * 2：导出PDF或EXCEL
     * 3:根据条件加载页面数据
     * 4:页面设置的保存和初始化
     */
    private JSONObject param = null;

    @Override
    public void execute() throws GeneralException {
        HashMap hm = this.getFormHM();
        JSONObject returnJson = new JSONObject();
        try {
            if (hm == null) {
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("msg.system.InvalidSession")));
            }

            String type = (String) hm.get("type");
            GzAnalysisUtil gzUtil = new GzAnalysisUtil(this.frameconn, this.userView);
            SummaryTableService service = new SummaryTableServiceImpl(this.userView,this.frameconn);
            if (StringUtils.isNotBlank(type)) {
                TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get("employeePaySummaryTable");
                //获得页面的自定义参数
                HashMap customMap = catche.getCustomParamHM();
                String rsdtlid = String.valueOf(customMap.get("rsdtlid"));

                StringBuilder querySql = new StringBuilder();

                if ("1".equals(type)) {// 1:输入查询
                    ArrayList<String> valuesList = new ArrayList<String>();
                    valuesList = (ArrayList<String>) this.getFormHM().get("inputValues");// 输入的内容
                    // 快速查询
                    if (valuesList != null && valuesList.size() > 0) {
                        querySql.append(" and (");
                    }
                    GzAnalysisUtil util = new GzAnalysisUtil(this.frameconn,this.userView);
                    //查询当前报表
                    Map<String,String> onlyFldMap = util.getOnlyFldFromRsdt(rsdtlid);
                    for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
                        String queryVal = valuesList.get(i);
                        queryVal = SafeCode.decode(queryVal);// 解码
                        if (i != 0) {
                            querySql.append(" or ");
                        }
                        querySql.append("a0101 like '%" + queryVal + "%'");
                        //有唯一性指标时，模糊匹配唯一性指标数据
                        if (onlyFldMap!=null && StringUtils.isNotBlank(onlyFldMap.get("itemid"))) {
                            querySql.append(" or " + onlyFldMap.get("itemid") + " like '%" + queryVal + "%'");
                        }
                    }
                    if (valuesList != null && valuesList.size() > 0) {
                        querySql.append(") ");
                    }

                } else if ("2".equals(type)) {//方案查询
                    HashMap queryFields = catche.getQueryFields();
                    String exp = (String) this.getFormHM().get("exp");
                    exp = SafeCode.decode(exp);
                    exp = PubFunc.keyWord_reback(exp);
                    String cond = (String) this.getFormHM().get("cond");
                    cond = SafeCode.decode(cond);
                    cond = cond.replaceAll("＜", "<");
                    cond = cond.replaceAll("＞", ">");
                    if (cond.length() < 1 || exp.length() < 1) {
                        // 方案查询中选择“全部”的时候，要恢复原来的检索条件
                        customMap.put("condSql","");
                        return;
                    }
                    querySql.append(" and ");
                    FactorList parser = new FactorList(exp, cond, userView.getUserName(), queryFields);//haosl 20161014方案查询可以查询自定义指标
                    querySql.append(parser.getSingleTableSqlExpression("ct"));
                }

                customMap.put("condSql",querySql.toString());

            }else{
                String transType="";
                String rsid="";
                String rsdtlid="";
                int imodule = 0;
                JSONObject json = null;
                if(hm.get("jsonStr") != null){
                    String jsonStr = (String) hm.get("jsonStr");
                    //转换为json对象
                    json = JSONObject.fromObject(jsonStr);
                    transType = json.getString("transType");
                    //生成表格数据
                    param = json.getJSONObject("param");
                    //报表种类编号
                    rsid = param.getString("rsid");
                    if (StringUtils.isNotBlank(rsid)) {
                        rsid = PubFunc.decrypt(rsid);
                    }
                    //报表编号
                    rsdtlid = param.getString("rsdtlid");
                    if (StringUtils.isNotBlank(rsdtlid)) {
                        rsdtlid = PubFunc.decrypt(rsdtlid);
                    }
                    
                    imodule = param.get("imodule")==null?0:Integer.parseInt(param.getString("imodule"));
                }


                if ("1".equals(transType)) {
                    JSONObject returnData = new JSONObject();
                    //将筛选条件存放到cache的自定义参数集合中去，方便快速查询的时候使用
                    HashMap customMap = new HashMap();
                    //=========获得统计表配置项===================

                    //==========获得年份数据，含已选年份标识==================
                    HashMap paramMap = gzUtil.getCtrlParam2Map(rsid, rsdtlid);
                    //人员库   Usr,Ret
                    String nbases = String.valueOf(paramMap.get("nbase"));
                    //账套ID  12,234,333
                    String salaryids = (String) paramMap.get("salaryids");
                    //1：含过程中数据
                    String scope = (String)paramMap.get("verifying");


                    //获得当前报表的数值型薪资项目
                    List<LazyDynaBean> itemList = service.getSalaryItem(salaryids);
                    //向前台页面返回数据
                    String itemid="";
                    if(itemList.size()>0)
                        itemid = String.valueOf(itemList.get(0).get("value"));

                    String tableConfig = "";
                    List yearList = new ArrayList();
                    if(StringUtils.isEmpty(nbases)
                            || StringUtils.isEmpty(salaryids)){
                        returnData.put("hasSetRange",false);
                    }

                    if(!StringUtils.isEmpty(nbases) && !StringUtils.isEmpty(salaryids)){
                        yearList = gzUtil.getYearList(salaryids, nbases, Integer.valueOf(scope));
                        //去重
                        for (int i = 0; i < yearList.size() - 1; i++) {
                            for (int j = yearList.size() - 1; j > i; j--) {
                                if (yearList.get(j).equals(yearList.get(i))) {
                                	yearList.remove(j);
                                }
                            }
                        }
                        Calendar cal = Calendar.getInstance();
                        String year = String.valueOf(cal.get(Calendar.YEAR));
                        if (!yearList.contains(year) && yearList.size()>0) {
                            year = String.valueOf(yearList.get(0));
                        }
                        customMap.put("year", year);
                        returnData.put("hasSetRange",true);
                    }
                    tableConfig = getTableConfig(rsid,rsdtlid,imodule);
                    customMap.put("salaryitem", itemid);
                    customMap.put("salaryids", salaryids);
                    customMap.put("rsdtlid", rsdtlid);
                    customMap.put("scope", scope);
                    customMap.put("nbases", nbases);
                    customMap.put("rsid", rsid);
                    TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get("employeePaySummaryTable");
                    cache.setCustomParamHM(customMap);

                    //============返回数据================
                    returnData.put("tableConfig", tableConfig);
                    returnData.put("yearList", yearList);
                    returnData.put("itemList", itemList);
                    returnData.put("rsdtlid_dec", rsdtlid);
                    returnData.put("rsid_dec", rsid);
                    returnJson.put("return_data", returnData);

                }
                //导出PDF或EXCEL
                else if ("2".equals(transType)) {
                    String year = param.getString("year");
                    String salaryitem = param.getString("salaryitem");
                    String talbeName = param.getString("tableName");
                    HashMap paramMap = gzUtil.getCtrlParam2Map(rsid, rsdtlid);
                    //人员库   Usr,Ret
                    String nbases = String.valueOf(paramMap.get("nbase"));
                    //账套ID  12,234,333
                    String salaryids = (String) paramMap.get("salaryids");
                    //1：含过程中数据
                    String scope = (String)paramMap.get("verifying");

                    TableDataConfigCache catche = (TableDataConfigCache) this.userView.getHm().get("employeePaySummaryTable");
                    //获得页面的自定义参数
                    HashMap customMap = catche.getCustomParamHM();
                    String sortSql = "";
                    if(customMap.containsKey("sortSql")){
                        sortSql = (String)customMap.get("sortSql");
                    }
                    String condSql = "";
                    if(customMap.containsKey("condSqlAndFilter")){
                        condSql = (String)customMap.get("condSqlAndFilter");
                    }
                    ArrayList<LazyDynaBean> dataList = service.getDataList(salaryids,year, salaryitem,nbases,scope,condSql,sortSql,0,0,-1);
                    //ArrayList<ColumnsInfo> columnList = service.getColumnList();
                    TableDataConfigCache tableCache = (TableDataConfigCache) this.userView.getHm().get("employeePaySummaryTable");
    				ArrayList<ColumnsInfo> columnList = tableCache.getDisplayColumns();
                    ReportParseVo reportVo = gzUtil.analysePageSettingXml(rsid,rsdtlid);
                    //导出Excel
                    String fileName = gzUtil.exportExcel(talbeName,reportVo,dataList,columnList,false,false,null,"");
                    //============返回数据================
                    JSONObject returnData = new JSONObject();
                    returnData.put("fileName", SafeCode.encode(PubFunc.encrypt(fileName)));
                    returnJson.put("return_data", returnData);
                } else if ("3".equals(transType)) {

                    String year = param.getString("year");
                    String itemid = param.getString("salaryitem");
                    TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get("employeePaySummaryTable");

                    //将筛选条件存放到cache的自定义参数集合中去，方便快速查询的时候使用
                    HashMap customMap = cache.getCustomParamHM();
                    List yearList = new ArrayList();
                    List<LazyDynaBean> itemList = new ArrayList<LazyDynaBean>();
                    JSONObject returnData = new JSONObject();
                    String scope="0";
                    //==========获得年份数据，含已选年份标识==================
                    HashMap ctrlMap = gzUtil.getCtrlParam2Map(rsid, rsdtlid);
                    //人员库   Usr,Ret
                    String nbases = String.valueOf(ctrlMap.get("nbase"));
                    //账套ID  12,234,333
                    String salaryids = (String) ctrlMap.get("salaryids");

                    if(ctrlMap.get("verifying")!=null && String.valueOf(ctrlMap.get("verifying")).length()>0){
                        scope = (String)ctrlMap.get("verifying");
                    }
                    if(!StringUtils.isEmpty(nbases) && !StringUtils.isEmpty(salaryids)){
                        yearList = gzUtil.getYearList(salaryids, nbases, Integer.valueOf(scope));
                        //去重
                        for (int i = 0; i < yearList.size() - 1; i++) {
                            for (int j = yearList.size() - 1; j > i; j--) {
                                if (yearList.get(j).equals(yearList.get(i))) {
                                	yearList.remove(j);
                                }
                            }
                        }
                        
                        Calendar cal = Calendar.getInstance();
                        if(StringUtils.isEmpty(year)){
                            year = String.valueOf(cal.get(Calendar.YEAR));
                            if (!yearList.contains(year) && yearList.size()>0) {
                                year = String.valueOf(yearList.get(0));
                            }
                        }
                    }
                    //获得当前报表的数值型薪资项目
                    itemList = service.getSalaryItem(salaryids);
                    //向前台页面返回数据
                    if(itemList.size()>0 && StringUtils.isEmpty(itemid))
                        itemid = String.valueOf(itemList.get(0).get("value"));

                    customMap.put("year", year);
                    customMap.put("salaryitem", itemid);
                    customMap.put("salaryids", salaryids);
                    customMap.put("rsdtlid", rsdtlid);
                    customMap.put("scope", scope);
                    customMap.put("nbases", nbases);
                    customMap.put("rsid", rsid);
                    returnData.put("yearList", yearList);
                    returnData.put("itemList", itemList);
                    returnJson.put("return_data", returnData);

                }else if("4".equals(transType)){
                    String opt = param.getString("opt");
                    //opt =2 保存xml =1 回显数据
                    if("2".equals(opt)){
                        MorphDynaBean pagesetupValue = (MorphDynaBean)this.getFormHM().get("pagesetupValue");
                        MorphDynaBean titleValue = (MorphDynaBean) this.getFormHM().get("titleValue");
                        MorphDynaBean pageheadValue = (MorphDynaBean)this.getFormHM().get("pageheadValue");
                        MorphDynaBean pagetailidValue = (MorphDynaBean)this.getFormHM().get("pagetailidValue");
                        MorphDynaBean textValueValue = (MorphDynaBean)this.getFormHM().get("textValueValue");
                        ReportParseVo rpv = ReportParseVo.setReportDetailXml(pagesetupValue,titleValue,pageheadValue,pagetailidValue,textValueValue);
                        gzUtil.saveXML(rpv,rsid,rsdtlid);
                    }else{
                        ReportParseVo orp = gzUtil.analysePageSettingXml(rsid,rsdtlid);
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
    				String codeitemid = json.getString("codeitemid");
    				String submoduleid = json.getString("submoduleid");
    				String width = json.getString("width");
    				String isshare = json.getString("isshare");
    				HashMap map = new HashMap();
    				map.put("codeitemid", codeitemid);
    				map.put("submoduleid", submoduleid);
    				map.put("width", width);
    				map.put("isshare", isshare);
    				gzUtil.setChange2SchemeSet(map);
    			}else if("6".equals(transType)) {//保存列顺序改变到栏目设置以及指标顺序
			        rsid = param.getString("rsid");
                    if (StringUtils.isNotBlank(rsid)) {
                        rsid = PubFunc.decrypt(rsid);
                    }
                    //报表编号
                    rsdtlid = param.getString("rsdtlid");
                    if (StringUtils.isNotBlank(rsdtlid)) {
                        rsdtlid = PubFunc.decrypt(rsdtlid);
                    }
                    
    				String is_lock = json.getString("is_lock");
    				String subModuleId = json.getString("submoduleid");
    				String itemid = json.getString("itemid");
    				String nextid = json.getString("nextid");
    				HashMap map = new HashMap();
    		    	
    				map.put("subModuleId", subModuleId);
    				map.put("itemid", itemid);
    				map.put("nextid", nextid);
    				map.put("is_lock", is_lock);
    				map.put("rsdtlid", rsdtlid);
    				map.put("rsid", rsid);
    				gzUtil.saveCloumnMove(map);
    			}else{

                    TableDataConfigCache cache = (TableDataConfigCache) userView.getHm().get("employeePaySummaryTable");
                    //将筛选条件存放到cache的自定义参数集合中去，方便快速查询的时候使用
                    HashMap customMap = cache.getCustomParamHM();


                    int limit = Integer.valueOf((String)hm.get("limit"));
                    int page = Integer.valueOf((String)hm.get("page"));
                    String filterParam = (String)hm.get("filterParam");
                    String condSql = "";
                    if(customMap.containsKey("condSql")){
                        condSql = (String)customMap.get("condSql");
                    }

                    String year = (String)customMap.get("year");
                    String itemid = (String)customMap.get("salaryitem");
                    String salaryids = (String)customMap.get("salaryids");
                    String scope = (String)customMap.get("scope");
                    String nbases = (String)customMap.get("nbases");
                    String sort = (String)hm.get("sort");
                    String sortSql = "";
                    if(StringUtils.isNotEmpty(sort)){
                        sortSql = this.getSortItem(sort,cache.getColumnMap());
                    }
                    //用于导出Excel时，所见即所得
                    customMap.put("sortSql",sortSql);
                    if(filterParam!=null){
                        String filterSql = this.createFilterSql(filterParam);
                        condSql+=filterSql;
                    }
                    //用于导出Excel时，所见即所得
                    customMap.put("condSqlAndFilter",condSql);
                    ArrayList dataList = new ArrayList();
                    int totalCount = 0;
                    if (StringUtils.isNotEmpty(nbases)
                            && StringUtils.isNotEmpty(salaryids)
                            && StringUtils.isNotEmpty(scope)
                            //此处应判断itemid 不为空，防止没有指标权限的用户访问时报错
                            && StringUtils.isNotEmpty(itemid)) {
                        totalCount = service.getDataCount(salaryids, year, itemid, nbases, scope, condSql);
                        dataList = service.getDataList(salaryids, year, itemid, nbases, scope, condSql, sortSql, limit, page, totalCount);
                    }
                        hm.put("dataobjs", dataList);
                        hm.put("totalCount", totalCount);

                }
            }
            //返回成功标记
            returnJson.put("return_code", "success");
            returnJson.put("return_msg", "success");
            hm.put("returnStr", returnJson.toString());
        } catch (Exception e) {
            ////返回失败标记
            e.printStackTrace();
            returnJson.put("return_code", "fail");
            returnJson.put("return_msg", "fail");
            throw GeneralExceptionHandler.Handle(e);
        }



    }

    /**
     * 表格控件配置
     * @param rsid
     * @param rsdtlid 报表编号
     * @return
     */
    private String getTableConfig(String rsid, String rsdtlid, int imodule) throws GeneralException {
        SummaryTableService service = new SummaryTableServiceImpl(this.userView, this.frameconn);
        ArrayList<ColumnsInfo> columnList = service.getColumnList();

        TableConfigBuilder builder = new TableConfigBuilder("employeePaySummaryTable", columnList,"GZ00000710", "employeePaySummaryPif", userView, frameconn);
        builder.setAutoRender(false);//是否自动渲染表格到页面
        builder.setPageSize(20);//每页条数
        builder.setSortable(true);
        builder.setSetScheme(false);
        builder.setEditable(false);
        builder.setLockable(true);
        builder.setColumnFilter(true);
        builder.setScheme(true);//使用栏目设置
        ArrayList buttonList = this.getButtonList(rsid,rsdtlid,imodule);//得到操作按钮
        builder.setTableTools(buttonList);//表格工具栏功能

        return builder.createExtTableConfig();
    }

    private ArrayList getButtonList(String rsid, String rsdtlid, int imodule) {
        ArrayList buttons = new ArrayList();
        ArrayList<LazyDynaBean> menuList = new ArrayList<LazyDynaBean>();
        GzAnalysisUtil gzUtil = new GzAnalysisUtil(this.frameconn, this.userView);
        LazyDynaBean buttonInfo = null;
        //导出Excel
        if (true) {
            buttonInfo = new LazyDynaBean();
            buttonInfo.set("id", "exportexcel");
            buttonInfo.set("text", ResourceFactory.getProperty("gz.zxdeclare.buttonExportExcel"));
            buttonInfo.set("handler", "employeePaySummaryTable_me.exportExcel()");
            buttonInfo.set("icon", "/images/export.gif");
            menuList.add(buttonInfo);
        }
        //页面设置
        if (true) {
            buttonInfo = new LazyDynaBean();
            buttonInfo.set("id", "pagesetting");
            buttonInfo.set("text", ResourceFactory.getProperty("gz.analysistable.pagesetting"));
            buttonInfo.set("icon", "/images/img_o.gif");
            buttonInfo.set("handler", "employeePaySummaryTable_me.showpagesetting()");
            menuList.add(buttonInfo);

        }
        if (menuList.size() > 0) {
            //功能导航
            String menu = GzAnalysisUtil.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), "navbar", menuList);
            buttons.add(menu);
        }
        if(gzUtil.hasTheFunction("32407104", "325040104", imodule)) {
        	ButtonInfo btn = new ButtonInfo(ResourceFactory.getProperty("gz.analysistable.setrange"), "employeePaySummaryTable_me.setRange('"+rsid+"','"+rsdtlid+"')");
        	btn.setId("setRange");
        	buttons.add(btn);
        }
        ButtonInfo queryBox = new ButtonInfo();
        queryBox.setType(ButtonInfo.TYPE_QUERYBOX);
        GzAnalysisUtil util = new GzAnalysisUtil(this.frameconn,this.userView);
        Map<String,String> onlyFldMap = util.getOnlyFldFromRsdt(rsdtlid);
        if(onlyFldMap == null){
            queryBox.setText(ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName"));
        }else{
            queryBox.setText(ResourceFactory.getProperty("gz_new.gz_accounting.inputUserName")+"、"+onlyFldMap.get("itemdesc"));
        }
        queryBox.setFunctionId("GZ00000710");
        buttons.add(queryBox);
        return buttons;
    }

    /**
     * 创建条件过滤
     * @return
     */
    private String createFilterSql(String filterParam){
        JSONObject json = JSONObject.fromObject(SafeCode.keyWord_reback(filterParam));
        String itemid = json.getString("field");
        String itemtype = json.getString("itemtype");
        JSONArray factor = json.getJSONArray("factor");
        String expr = json.getString("expr");
        //如果为空或者没有数据，返回 原sql
        if(factor==null || factor.isEmpty()){
            return "";
        }
        StringBuffer filterWhere = new StringBuffer(" and (");
        String symbol;
        String value = "";
        filterIf:if("C".equals(itemtype)){// C代码型指标
            expr = "or";
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                value = f.substring(f.indexOf("`")+1);
                filterWhere.append("UPPER("+itemid+") like '"+value.toUpperCase()+"%' or ");
            }
        }else if("D".equals(itemtype)){//时间类型
            String plan = json.getString("plan");

            if("custom".equals(plan)){
                for(int i=0;i<factor.size();i++){

                    String f = factor.getString(i);
                    symbol = f.substring(0,f.indexOf("`"));
                    value = f.substring(f.indexOf("`")+1);

                    String format = "YYYY-MM-DD HH24:mi:ss";
                    if(value.length()==4){
                        format = "YYYY";
                    }else if(value.length()==7)
                        format = "YYYY-MM";
                    else if(value.length()==10)
                        format = "YYYY-MM-DD";
                    else if(value.length()==16){
                        //当日期没有秒时，补位
                        value+=":00";
                    }

                    filterWhere.append(Sql_switcher.dateToChar(itemid, format)).append(symbol).append(" '").append(value).append("' ");
                    filterWhere.append(expr).append(" ");
                }

                break filterIf;
            }

            String f = factor.getString(0);
            symbol = f.substring(0,f.indexOf("`"));
            Calendar c = Calendar.getInstance();

            if("nextMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+2)+" "+expr+" ");
            }else if("thisMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+(c.get(Calendar.MONTH)+1)+" "+expr+" ");
            }else if("lastMonth".equals(symbol)){
                filterWhere.append(Sql_switcher.month(itemid)+"="+c.get(Calendar.MONTH)+" "+expr+" ");
            }else if("nextYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)+1)+" "+expr+" ");
            }else if("thisYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+c.get(Calendar.YEAR)+" "+expr+" ");
            }else if("lastYear".equals(symbol)){
                filterWhere.append(Sql_switcher.year(itemid)+"="+(c.get(Calendar.YEAR)-1)+" "+expr+" ");
            }else{
                int nextYear = -1;
                int lastYear = -1;
                String nextSeason = "";
                String thisSeason = "";
                String lastSeason = "";
                if(c.get(Calendar.MONTH)<3){
                    thisSeason = "1,2,3";
                    lastYear = c.get(Calendar.YEAR)-1;
                }else if(c.get(Calendar.MONTH)<6){
                    nextSeason = "7,8,9";
                    thisSeason = "4,5,6";
                    lastSeason = "1,2,3";
                }else if(c.get(Calendar.MONTH)<9){
                    nextSeason = "10,11,12";
                    thisSeason = "7,8,9";
                    lastSeason = "4,5,6";
                }else{
                    thisSeason = "10,11,12";
                    nextYear = c.get(Calendar.YEAR)+1;
                }
                if("nextSeason".equals(symbol)){
                    if(nextYear>0)
                        filterWhere.append(Sql_switcher.month(itemid)+" in (1,2,3) and "+Sql_switcher.year(itemid)+"="+nextYear+" "+expr+" ");
                    else
                        filterWhere.append(Sql_switcher.month(itemid)+" in ("+nextSeason+") "+expr+" ");
                }else if("thisSeason".equals(symbol)){
                    filterWhere.append(Sql_switcher.month(itemid)+" in ("+thisSeason+") "+expr+" ");
                }else if("lastSeason".equals(symbol)){
                    if(lastYear>0)
                        filterWhere.append(Sql_switcher.month(itemid)+" in (10,11,12) and "+Sql_switcher.year(itemid)+"="+lastYear+" "+expr+" ");
                    else
                        filterWhere.append(Sql_switcher.month(itemid)+" in ("+lastSeason+") "+expr+" ");
                }
            }
        }else if("N".equals(itemtype)){//int型
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                symbol = f.substring(0,f.indexOf("`"));
                value = f.substring(f.indexOf("`")+1);
                filterWhere.append(Sql_switcher.isnull(itemid, "0")+symbol+value+" "+expr+" ");
            }
        }else{//M(文本)型和A(字符)型
            for(int i=0;i<factor.size();i++){
                String f = factor.getString(i);
                symbol = f.substring(0,f.indexOf("`"));
                try {
                    value = URLDecoder.decode(f.substring(f.indexOf("`")+1), "UTF-8");
                    value = PubFunc.hireKeyWord_filter(value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                };
                if("sta".equals(symbol)){//开头是
                    filterWhere.append(itemid+" like '"+value+"%' "+expr+" ");
                }else if("stano".equals(symbol)){//开头不是
                    filterWhere.append(itemid+" not like '"+value+"%' "+expr+" ");
                }else if("end".equals(symbol)){//结尾是
                    filterWhere.append(itemid+" like '%"+value+"' "+expr+" ");
                }else if("endno".equals(symbol)){//结尾不是
                    filterWhere.append(itemid+" not like '%"+value+"' "+expr+" ");
                }else if("cont".equals(symbol)){//包含
                    filterWhere.append(itemid+" like '%"+value+"%' "+expr+" ");
                }else if("contno".equals(symbol)){//不包含
                    filterWhere.append(itemid+" not like '%"+value+"%' "+expr+" ");
                }else{
                    if("=".equals(symbol) &&  value.indexOf("？")+value.indexOf("＊")>-2){
                        symbol = " like ";
                        value = value.replaceAll("？", "?");
                        value = value.replaceAll("＊", "%");
                    }
                    if(value.length()==0 && "=".equals(symbol) ){
                        filterWhere.append(" ("+ Sql_switcher.sqlToChar(itemid)+symbol+" '' or "+Sql_switcher.sqlToChar(itemid)+" is null ) "+expr+" ");
                    }else
                        filterWhere.append(Sql_switcher.sqlToChar(itemid)+symbol+" '"+value+"' "+expr+" ");
                }
            }
        }
        if("or".equals(expr))
            filterWhere.append(" 1=2 ");
        else
            filterWhere.append(" 1=1 ");
        filterWhere.append(" )");
        return filterWhere.toString();
    }

    /**
     *获得排序指标
     * @param sort
     * @param columnMap  tableCache中的列缓存
     * @return
     */
    private String getSortItem(String sort,HashMap columnMap){
        sort = PubFunc.hireKeyWord_filter_reback(sort);
        JSONArray sortArray = JSONArray.fromObject(sort);
        JSONObject sortObj = sortArray.getJSONObject(0);
        String sortColumnId  = sortObj.get("property").toString();
        String sortDirection = sortObj.get("direction").toString();
        if(!columnMap.containsKey(sortColumnId)) {
            return "";
        }

        ColumnsInfo sortColumn = (ColumnsInfo)columnMap.get(sortColumnId);

        //如果显示排序类型 按照所选排序类型排序
        String sortSql = "";
        sortColumnId="ct."+sortColumnId;
        if(sortColumn.isShowSortType()){
            String sortType = this.formHM.get("sortType").toString();
            //排序类型：py=拼音；bh=笔画
            if("py".equals(sortType))
                sortSql=Sql_switcher.sortByPinYin(sortColumnId)+sortDirection;
            else
                sortSql=Sql_switcher.sortByStroke(sortColumnId)+sortDirection;
        }else if("N".equals(sortColumn.getColumnType())){
            sortSql = Sql_switcher.isnull(sortColumnId, "0")+" "+sortDirection;
        }else{
            //其他类型排序
            return sortColumnId + " " + sortDirection;
        }

        return sortSql;
    }
}
