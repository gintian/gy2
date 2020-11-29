package com.hjsj.hrms.module.gz.analyse.historydata.transaction;

import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.SalaryHistoryDataService;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.impl.SalaryHistoryDataServiceImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title InitSalaryHistoryDataTrans
 * @Description 初始化薪资历史数据
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class InitSalaryHistoryDataTrans extends IBusiness {
    @Override
    public void execute() throws GeneralException {
        Map return_data = new HashMap();
        try {
            //薪资类别号
            String salaryId=(String)this.getFormHM().get("salaryId");
            salaryId = PubFunc.decrypt(SafeCode.decode(salaryId));
            //薪资日期 未加密
            String salaryDate = (String)this.getFormHM().get("salary_date");
            //发放次数 未加密
            String count = (String)this.getFormHM().get("count");
            // 页面区分
            String transType = (String)this.getFormHM().get("transType");
            //获取区别薪资0和保险1
            String gz_module = (String)this.getFormHM().get("gz_module");
            //lyd固定：0  页面区分 0:薪资发放  1:审批  2:上报参数
            String viewtype = (String)this.getFormHM().get("viewtype");
            //查询组件返回条件集合
            MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("customParams");
            String querySql = "";
            String appdate = "";
            //最大发放次数
            String maxCount = "";
            String currentCount ="";
            ArrayList dateList = new ArrayList();
            //次数map：最大次数、次数列表list
            Map countMap = new HashMap();
            List countList = new ArrayList();
            String condSql = "";
            SalaryHistoryDataService initSalaryHistoryData = new SalaryHistoryDataServiceImpl(frameconn, userView);
            if (StringUtils.equalsIgnoreCase(transType, "init")) {
                //初始化获取薪资类别编号
                salaryId = initSalaryHistoryData.getSalaryId();
                //没有任何薪资账套权限
                if (StringUtils.isBlank(salaryId)) {
                    this.formHM.put("return_code", "fail");
                    this.formHM.put("return_msg", "noPrivData");
                    return;
                }
                //初始化获取发放日期
                appdate = initSalaryHistoryData.getAppdate(salaryId, "history");
                //初始化获取归属次数
                countMap = initSalaryHistoryData.getCount(salaryId,appdate,"history");
                maxCount = (String) countMap.get("maxCount");
                //获取日期组件初始化年月数据
                dateList = initSalaryHistoryData.getDateList(salaryId,appdate,"history");
                List columnsFieldList =initSalaryHistoryData.getColumnsFieldList(salaryId);
                querySql = initSalaryHistoryData.getSqldata(columnsFieldList,transType,salaryId,appdate,maxCount,new ArrayList<String>(),"","","");

                currentCount = maxCount;
            } else if(StringUtils.equalsIgnoreCase(transType, "history")||StringUtils.equalsIgnoreCase(transType, "achieve")){
                //获取是否从日期组件选择日期标志
                boolean isSelectDate = (Boolean) this.getFormHM().get("isSelectDate");
                //获取是否从发放次数组件选择次数标志
                boolean isChangeCount = (Boolean) this.getFormHM().get("isChangeCount");
                appdate = initSalaryHistoryData.getAppdate(salaryId, transType);
                countMap = initSalaryHistoryData.getCount(salaryId,appdate,transType);
                maxCount = (String) countMap.get("maxCount");
                List columnsFieldList =initSalaryHistoryData.getColumnsFieldList(salaryId);
                currentCount = maxCount;
                if(isChangeCount){
                    currentCount = count;
                    appdate = salaryDate;
                    countMap = initSalaryHistoryData.getCount(salaryId,appdate,transType);
                }
                if(isSelectDate){
                    appdate = salaryDate;
                    countMap = initSalaryHistoryData.getCount(salaryId,appdate,transType);
                    currentCount = (String) countMap.get("maxCount");
                }
                //获取日期组件初始化年月数据
                dateList = initSalaryHistoryData.getDateList(salaryId,appdate,transType);
                //如果选择日期或次数标志为true，发放日期取salaryDate，发放次数取countDecrypt
                querySql = initSalaryHistoryData.getSqldata(columnsFieldList,transType,salaryId,appdate,currentCount,new ArrayList<String>(),"","","");
            }else if (bean != null) {
                currentCount = count;
                salaryId = (String) bean.get("salaryId");
                salaryId = PubFunc.decrypt(SafeCode.decode(salaryId));
                appdate = (String) bean.get("appdate");
                appdate = PubFunc.decrypt(SafeCode.decode(appdate));
                String count_temp = (String) bean.get("count");
                count_temp = PubFunc.decrypt(SafeCode.decode(count_temp));
                transType = (String) bean.get("transType");
                ArrayList<String> valuesList = (ArrayList) this.getFormHM().get("inputValues");
                String searchType = (String) this.getFormHM().get("type");
                String exp = (String) this.getFormHM().get("exp");
                String cond = (String) this.getFormHM().get("cond");
                List columnsFieldList =initSalaryHistoryData.getColumnsFieldList(salaryId);
                querySql = initSalaryHistoryData.getSqldata(columnsFieldList,transType,salaryId,appdate,count_temp,valuesList,searchType,exp,cond);
                condSql = ((SalaryHistoryDataServiceImpl)initSalaryHistoryData).getCondSql(valuesList,searchType,exp,cond,salaryId);
            }

            //工资报表
            ArrayList<LazyDynaBean> reportList = initSalaryHistoryData.getReportList(salaryId);
            //表格主页面
            String tableConfig = initSalaryHistoryData.getSalaryHistoryTableConfig(transType, salaryId, appdate, querySql);
            if(condSql.length()>0){//页面模糊查询
                TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get("salary_"+salaryId);
                tableCache.setQuerySql(" and ( "+condSql.replaceAll("salaryData.", "")+" ) ");//去掉表名，防止表格工具追加后报错
            }
            //薪资账套
            List salaryTypeList = initSalaryHistoryData.getSalaryType();
            if (StringUtils.isNotBlank(appdate)) {
                appdate = appdate.substring(0,10);
            }
            return_data.put("tableConfig", tableConfig);
            return_data.put("salaryId_encrypt", SafeCode.encode(PubFunc.encrypt(salaryId)));
            return_data.put("appdate_encrypt", StringUtils.isBlank(appdate) ? "" : SafeCode.encode(PubFunc.encrypt(appdate)));
            return_data.put("count_encrypt", StringUtils.isBlank(currentCount) ? "" : SafeCode.encode(PubFunc.encrypt(currentCount)));
            return_data.put("reportList", reportList);
            return_data.put("salaryTypeList", salaryTypeList);
            return_data.put("dateList", dateList);

            if (MapUtils.isNotEmpty(countMap)) {
                countList = (ArrayList) countMap.get("countList");
            }
            this.formHM.put("countList", countList);
            this.formHM.put("appdate", StringUtils.isBlank(appdate) ? "" : appdate);
            this.formHM.put("count", StringUtils.isBlank(currentCount) ? "" : currentCount);
            //报表输出用到的参数
            this.formHM.put("gz_module",SafeCode.encode(PubFunc.encrypt(gz_module)));
            this.formHM.put("tablesubModuleId","salary_"+salaryId);
            this.formHM.put("viewtype",SafeCode.encode(PubFunc.encrypt(viewtype)));

            this.formHM.put("return_code", "success");
            this.formHM.put("return_data", return_data);
        } catch (GeneralException e) {
            e.printStackTrace();
            this.formHM.put("return_code", "fail");
            this.formHM.put("return_msg", e.getErrorDescription());
        }
    }
}
