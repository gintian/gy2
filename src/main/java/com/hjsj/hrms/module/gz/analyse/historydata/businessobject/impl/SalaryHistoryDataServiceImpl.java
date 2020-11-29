package com.hjsj.hrms.module.gz.analyse.historydata.businessobject.impl;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.gz.analyse.historydata.businessobject.SalaryHistoryDataService;
import com.hjsj.hrms.module.gz.analyse.historydata.dao.SalaryHistoryDataDao;
import com.hjsj.hrms.module.gz.analyse.historydata.dao.impl.SalaryHistoryDataDaoImpl;
import com.hjsj.hrms.module.gz.salaryreport.businessobject.SalaryReportBo;
import com.hjsj.hrms.module.gz.utils.SalarySetBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableConfigBuilder;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * @Title SalaryHistoryDataServiceImpl
 * @Description 薪资历史数据业务实现类
 * @Company hjsj
 * @Author wangbs
 * @Date 2020/1/13
 * @Version 1.0.0
 */
public class SalaryHistoryDataServiceImpl implements SalaryHistoryDataService {
    private Connection conn;
    private UserView userView;
    private SalaryHistoryDataDao salaryHistoryDataDao;
    /** 日志对象 */
    private static Category log = Category.getInstance(SalaryHistoryDataServiceImpl.class.getName());
    /**
     * 薪资历史数据业务实现类构造方法
     * @author wangbs
     * @param conn 数据库连接
     * @param userView 用户信息
     * @date 2020/1/13 15:35
     */
    public SalaryHistoryDataServiceImpl(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.salaryHistoryDataDao = new SalaryHistoryDataDaoImpl(conn);
    }

    /**
     * 薪资历史数据初始化方法
     * @author sheny
     * @param type history未归档  achieve归档
     * @param salaryId 薪资账套Id
     * @param appdate 薪资账套归属时间
     * @param querySql 薪资账套sql
     * @return gridconfig 初始化tablepanel
     * @date 2020/1/15 15:35
     */
    @Override
    public String getSalaryHistoryTableConfig(String type, String salaryId, String appdate, String querySql) throws GeneralException {
        String gridconfig = "";
        //获取列数据
        List columnsFieldList =this.getColumnsFieldList(salaryId);
        //获取列
        ArrayList<ColumnsInfo> columnsInfoList = (ArrayList<ColumnsInfo>) this.getColumnsList(columnsFieldList,salaryId);
        String tablesubModuleId="salary_"+salaryId;
        //创建表格对象
        TableConfigBuilder builder = new TableConfigBuilder(tablesubModuleId, columnsInfoList, "SalaryHistoryData", this.userView, this.conn);
        //设置标题
        builder.setTitle(ResourceFactory.getProperty("GzAnalyse.historydata.SalaryHistoryData.title"));
        builder.setDataSql(querySql);
        builder.setColumnFilter(true);
        //是否可以进行栏目设置
        builder.setScheme(true);
        //排序指标
        builder.setOrderBy(" order by nbase,a0000 ");
        //获取按钮
        builder.setTableTools(this.getButtonList(type,salaryId));
        builder.setLockable(true);
        builder.setSchemeSaveCallback("SalaryHistoryData.saveSchemeCallBack");
        //保存方案按钮 权限控制
        if (this.userView.isSuper_admin() || this.userView.hasTheFunction("32407405")) {
            builder.setShowPublicPlan(true);
        }
        gridconfig = builder.createExtTableConfig();
        return gridconfig;
    }

    /**
     * 初始化获得薪资发放日期
     * @author sheny
     * @param salaryId 薪资账套Id
     * @param transType 区分是否归档
     * @return appdate
     * @date 2020/1/15 15:35
     */
    @Override
    public String getAppdate(String salaryId,String transType) throws GeneralException {
        String appdate = "";
        List sqlList = new ArrayList();
        RowSet rowSet = null;
        try {
            String tableName = "salaryhistory";
            if(StringUtils.equalsIgnoreCase(transType, "achieve")){
                tableName = "salaryarchive";
            }
            String sql = "select MAX(A00Z2) from " + tableName + " where salaryid = ?";

            sqlList.add(salaryId);
            ContentDAO dao=new ContentDAO(this.conn);
            rowSet = dao.search(sql, sqlList);
            if(rowSet.next()){
                if (rowSet.getTimestamp(1) != null) {
                    appdate = DateStyle.dateformat(rowSet.getTimestamp(1), "yyyy-MM-dd");
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getAppdateError");
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return appdate;
    }

    /**
     * 初始化获得薪资账套
     * @author sheny
     * @return salaryId 薪资账套id
     * @date 2020/1/15 15:35
     */
    @Override
    public String getSalaryId() throws GeneralException {
        String salaryId = "";
        RowSet rowSet = null;
        try {
            //查出薪资类别表中所有的薪资表
            String sql = "select salaryid from salarytemplate where CState is null order by seq asc";
            ContentDAO dao=new ContentDAO(this.conn);
            List salaryIdList = new ArrayList();
            rowSet=dao.search(sql);
            boolean isHaveResource = false;
            //取出有权限的薪资表
            while(rowSet.next()){
                salaryId = rowSet.getString(1);
                isHaveResource =  this.userView.isHaveResource(IResourceConstant.GZ_SET, salaryId);
                if(isHaveResource){
                    salaryIdList.add(salaryId);
                }
            }
            if(salaryIdList.size()>0){
                salaryId = (String) salaryIdList.get(0);
            }else{
                //代表此人没有任何薪资表权限
                salaryId = "";
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getSalaryIdError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return salaryId;
    }
    /**
     * 初始化获得薪资归属时间
     * @author sheny
     * @param salaryId 薪资账套Id
     * @param appDate 发放日期
     * @return count 薪资归属次数
     * @date 2020/1/15 15:35
     */
    @Override
    public Map getCount(String salaryId, String appDate,String transType) throws GeneralException {
        Map countMap = new HashMap();
        String count = "";
        List countList = new ArrayList();
        List sqList = new ArrayList();
        String tableName = "salaryhistory";
        if(StringUtils.equalsIgnoreCase(transType, "achieve")){
            tableName = "salaryarchive";
        }
        RowSet rowSet = null;
        StringBuffer sql = new StringBuffer();
        try {
            sql.append("select distinct A00Z3 from ").append(tableName).append(" where salaryid = ?");
            sqList.add(salaryId);
            if (StringUtils.isNotBlank(appDate)) {
                sql.append(" and a00z2= ").append(Sql_switcher.charToDate("'" + appDate + "'"));
            }
            sql.append(" order by A00Z3");
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(sql.toString(), sqList);
            while (rowSet.next()) {
                count = rowSet.getString(1);
                countList.add(count);
            }
            countMap.put("maxCount", count);
            countMap.put("countList", countList);
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getCountError");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return countMap;
    }
    /**
     * 获得薪资账套指标集
     * @author sheny
     * @param salaryId 薪资账套Id
     * @return reportList 薪资账套指标集
     * @date 2020/1/15 15:35
     */
    @Override
    public ArrayList<LazyDynaBean> getReportList(String salaryId) throws GeneralException{
        ArrayList<LazyDynaBean> reportList = new ArrayList<LazyDynaBean>();
        try {
            SalaryReportBo salaryReportBo = new SalaryReportBo(this.conn,String.valueOf(salaryId),this.userView);
            reportList = salaryReportBo.listCommonReport("0","3");//lyd 更改model为3 历史薪资数据进入
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getReportListError");
        }
        return reportList;
    }
    /**
     * 获得薪资账套集
     * @author sheny
     * @return salaryTypeList 薪资账套集
     * @date 2020/1/15 15:35
     */
    @Override
    public List getSalaryType() throws GeneralException {
        List salaryTypeList = new ArrayList();
        RowSet rowSet = null;
        try {
            String sql = "select cname,salaryid from salarytemplate where CState is null order by seq asc";
            ContentDAO dao=new ContentDAO(this.conn);
            rowSet=dao.search(sql);
            while(rowSet.next()){
                String salaryId = rowSet.getString("salaryid");
                if (this.userView.isSuper_admin() || this.userView.isHaveResource(IResourceConstant.GZ_SET, salaryId)) {
                    Map salaryType = new HashMap();
                    salaryType.put("cname", rowSet.getString("cname"));
                    salaryType.put("salaryid", salaryId);
                    salaryTypeList.add(salaryType);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getSalaryType");
        }finally {
            PubFunc.closeResource(rowSet);
        }

        return salaryTypeList;
    }
    /**
     * 编辑表单列
     * @author sheny
     * @param columnsFieldList 列头指标
     * @return columnsInfoList 规定样式后列
     * @date 2020/1/15 15:35
     */
    private List getColumnsList(List columnsFieldList,String salaryId) {
        ArrayList<ColumnsInfo> columnsInfoList = new ArrayList<ColumnsInfo>();
        columnsFieldList = this.getColumnsFieldform(columnsFieldList);
        String lockStr = ",A0101,B0110,E0122,NBASE,A00Z0,A00Z2,A0177,";//默认锁列的字段 姓名，单位名称，部门，人员库标识，归属日期，发放日期
        String hiddenStr = ",NBASE,A00Z1,A00Z3,A01Z0,";//默认隐藏的字段（栏目设置可以设置成显示）
        String iskey = ",A0100,NBASE,A00Z0,A00Z1,Z00Z2,A00Z3,";//每条数据的唯一id，用于跨页全选
        for(int i=0;i<columnsFieldList.size();i++){
            FieldItem item = (FieldItem) columnsFieldList.get(i);
            ColumnsInfo info = new ColumnsInfo(item);
            //只加载数据
//            info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
            //对数值型的做特殊处理，为空的显示为0
            if("N".equalsIgnoreCase(item.getItemtype())) {
                info.setDefaultValue("0");
            }
             //部门可以选择单位和部门
            if("UM".equalsIgnoreCase(item.getCodesetid())) {
                info.setCodeSetValid(false);
            }
            if(iskey.indexOf(","+item.getItemid()+",")!=-1){
                info.setKey(true);
            }
            //加密
            if("a0100".equalsIgnoreCase(item.getItemid())|| "a0000".equalsIgnoreCase(item.getItemid())){
                info.setEncrypted(true);
            }
            //特殊处理发放次数、归属次数对齐方式
            if("a00z1".equalsIgnoreCase(item.getItemid())|| "a00z3".equalsIgnoreCase(item.getItemid())){
                info.setTextAlign("right");
            }
            //锁列
            if(lockStr.indexOf(","+item.getItemid()+",")!=-1){
                info.setLocked(true);
            }
            //不允许编辑
            if(item.isReadonly()){
                info.setEditableValidFunc("false");
            }
             //归属日期 yyyy-MM-dd 列宽
            if("a00z0".equalsIgnoreCase(item.getItemid())){
                info.setColumnLength(10);
            }
            //发放日期 yyyy-MM-dd 列宽
            if("a00z2".equalsIgnoreCase(item.getItemid())){
                info.setColumnLength(7);
            }
            //隐藏列
            if(hiddenStr.indexOf(","+item.getItemid()+",")!=-1){
                info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
            }
            if(!this.userView.isSuper_admin()&&!"1".equals(this.userView.getGroupId())){
                if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid()))){
                    info.setCtrltype("3");
                    info.setNmodule("1");
                }
            }
            if(!"A00".equalsIgnoreCase(item.getFieldsetid())) {
                //A00字段不存在于数据字典。
                info.setFieldsetid(item.getFieldsetid());
            }
            columnsInfoList.add(info);
        }
        SalarySetBo salarySetBo = new SalarySetBo(conn,Integer.valueOf(salaryId),userView);
        /**加上审批标识*/
        FieldItem fielditem = new FieldItem();
        fielditem.setItemid("sp_flag");
        fielditem.setItemdesc(com.hrms.hjsj.sys.ResourceFactory.getProperty("label.gz.sp"));
        fielditem.setItemtype("A");
        fielditem.setReadonly(true);
        fielditem.setItemlength(50);
        fielditem.setCodesetid("23");
        fielditem.setVisible(false);
        if(salarySetBo.isApprove())
        {
            fielditem.setVisible(true);
        }else if(new com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo(this.conn,Integer.valueOf(salaryId)).getValue(com.hjsj.hrms.module.gz.utils.SalaryCtrlParamBo.SHARE_SET, "user").length() == 0 && !salarySetBo.isApprove()){
            fielditem.setVisible(true);
        }
        ColumnsInfo info = new ColumnsInfo(fielditem);
        info.setOperationData(this.getSpOperationData(fielditem.getItemid()));
        info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
        info.setQueryable(true);
        if(fielditem.isVisible()){
            columnsInfoList.add(info);
        }
        return columnsInfoList;
    }
    /**
     * 审批状态
     * @return
     */
    private ArrayList getSpOperationData(String itemid)
    {
        ArrayList<CommonData> spOperationData = new ArrayList<CommonData>();
        CommonData cd = new CommonData();
        cd.setDataName("起草");
        cd.setDataValue("01");
        spOperationData.add(cd);
        cd = new CommonData();
        cd.setDataName("已报批");
        cd.setDataValue("02");
        spOperationData.add(cd);
        if("sp_flag".equalsIgnoreCase(itemid)){//报审没有已批状态 zhanghua 2017-8-16
            cd = new CommonData();
            cd.setDataName("已批");
            cd.setDataValue("03");
            spOperationData.add(cd);
        }
        cd = new CommonData();
        cd.setDataName("结束");
        cd.setDataValue("06");
        spOperationData.add(cd);
        cd = new CommonData();
        cd.setDataName("驳回");
        cd.setDataValue("07");
        spOperationData.add(cd);
//         cd = new CommonData();//没见过什么叫报审状态。 zhanghua 2017-8-16
//         cd.setDataName("报审");
//         cd.setDataValue("08");
//         spOperationData.add(cd);
        return spOperationData;
    }
    /**
     * 获取表单列
     * @author sheny
     * @param columnsFieldList 列头指标
     * @return columnsFieldformList 表单列
     * @date 2020/1/15 15:35
     */
    private List getColumnsFieldform(List columnsFieldList) {
        List columnsFieldformList = new ArrayList();
        StringBuffer format=new StringBuffer();
        format.append("###################");
        String hiddenField="a0100,a0000,";
        for (int i = 0; i < columnsFieldList.size(); i++) {
            FieldItem fielditem = new FieldItem();
            LazyDynaBean bean = (LazyDynaBean) columnsFieldList.get(i);
            String itemid = ((String) bean.get("itemid"));
            String type = (String) bean.get("itemtype");
            String codesetid = (String) bean.get("codesetid");
            String itemlength = (String) bean.get("itemlength");
            fielditem.setItemid(itemid);
            fielditem.setItemdesc((String) bean.get("itemdesc"));
            fielditem.setFieldsetid((String) bean.get("fieldsetid"));
            fielditem.setCodesetid(codesetid);

            /**字段为代码型,长度定为50*/
            if("A".equals(type)){
                fielditem.setItemtype(type);
                if (StringUtils.isBlank(codesetid) || "0".equals(codesetid)) {
                    fielditem.setItemlength(Integer.parseInt(itemlength));
                } else {
                    fielditem.setItemlength(50);
                }
                if (StringUtils.equalsIgnoreCase(itemid, "a00z1") || StringUtils.equalsIgnoreCase(itemid, "a00z3")) {
                    fielditem.setAlign("right");
                } else {
                    fielditem.setAlign("left");
                }
            } else if("M".equals(type)){
                fielditem.setItemtype(type);
                fielditem.setAlign("left");
            } else if("N".equals(type)){
                fielditem.setItemlength(Integer.parseInt(itemlength));
                int ndec=Integer.parseInt((String) bean.get("decwidth"));
                fielditem.setDecimalwidth(ndec);
                if(ndec>0){
                    fielditem.setItemtype(type);
                    fielditem.setFormat("####."+format.toString().substring(0,ndec));
                } else {
                    fielditem.setItemtype(type);
                    fielditem.setFormat("####");
                }
                fielditem.setAlign("right");
            } else if("D".equals(type)) {
                fielditem.setItemlength(Integer.parseInt(itemlength));
                fielditem.setItemtype(type);
                fielditem.setFormat("yyyy.MM.dd");
                if ("A00Z2".equalsIgnoreCase(itemid)) {
                    fielditem.setAlign("left");
                } else {
                    fielditem.setAlign("right");
                }
            } else {
                fielditem.setItemtype("A");
                fielditem.setItemlength(Integer.parseInt(itemlength));
                fielditem.setAlign("left");
            }
            /**对人员库标识，采用“@@”作为相关代码类*/
            if("nbase".equalsIgnoreCase(itemid)){
                fielditem.setCodesetid("@@");
            }
            if(hiddenField.indexOf(itemid.toLowerCase())!=-1) {
                fielditem.setVisible(false);
            }
            fielditem.setSortable(true);
            /**设置只读字段*/
            fielditem.setReadonly(true);
            /**分析指标权限*/
            if(!("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))&& "0".equalsIgnoreCase(this.userView.analyseFieldPriv(itemid))){
                fielditem.setVisible(false);//无权限
            }
            if("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid)) {
                //由于栏目设置不能隐藏传值 暂时直接给与人员库权限
                fielditem.setVisible(true);
            }
            columnsFieldformList.add(fielditem);
        }
        return columnsFieldformList;
    }
    /**
     * 获取表单列
     * @author sheny
     * @param salaryId 薪资账套Id
     * @return columnsFieldList 表单列指标
     * @date 2020/1/15 15:35
     */
    @Override
    public List getColumnsFieldList(String salaryId) throws GeneralException {
        List columnsFieldList = new ArrayList();
        List sqList = new ArrayList();
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet rowSet = null;
        StringBuffer strread=new StringBuffer();
        /**只读字段*/
        strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,A00Z0,A00Z1,NBASE,");
        try {
            String sql = "select * from salaryset where SALARYID = ?";
            sqList.add(salaryId);
            rowSet=dao.search(sql,sqList);
            while (rowSet.next()) {
                LazyDynaBean abean = new LazyDynaBean();
                String itemId = rowSet.getString("itemid");
                String itemType= rowSet.getString("itemtype");
                if (StringUtils.equalsIgnoreCase(itemId, "A00Z3") || StringUtils.equalsIgnoreCase(itemId, "A00Z1")) {
                    itemType = "A";
                }
                abean.set("salaryid",rowSet.getString("salaryid"));
                abean.set("itemdesc",rowSet.getString("itemdesc")!=null?rowSet.getString("itemdesc"):"");
                abean.set("fieldsetid",rowSet.getString("fieldsetid")!=null?rowSet.getString("fieldsetid"):"");
                abean.set("itemtype",itemType);
                abean.set("decwidth",rowSet.getString("decwidth"));
                abean.set("codesetid",rowSet.getString("codesetid"));
                abean.set("itemid",rowSet.getString("itemid"));
                abean.set("nlock",rowSet.getString("nlock"));
                abean.set("nwidth",rowSet.getString("nwidth"));
                abean.set("itemlength",rowSet.getString("itemlength"));
                if (!StringUtils.equalsIgnoreCase(this.userView.analyseFieldPriv(rowSet.getString("itemid")), "0")||strread.indexOf(rowSet.getString("itemid").toUpperCase())!=-1) {
                    columnsFieldList.add(abean);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new GeneralException("GzAnalyse.historydata.getColumnsFieldListError");
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return columnsFieldList;
    }

    /**
     *获取业务范围权限sql
     * @param salaryId
     * @return
     */
    private String getUnitsPrivSql(String salaryId) {
        StringBuffer privSql = new StringBuffer();
        try {
            ArrayList list = this.userView.getPrivDbList();
            //String b_units = this.userView.getUnitIdByBusi("1");
            String b_units = this.userView.getUnitIdByBusiOutofPriv("1");
            String clientName = SystemConfig.getPropertyValue("clientName");
            if (clientName != null && "weichai".equalsIgnoreCase(clientName)) {
                b_units = this.userView.getUnit_id();
            }
            if (this.userView.isSuper_admin() || "1".equals(this.userView.getGroupId()) || StringUtils.equalsIgnoreCase("UN`", b_units)) {
                return " and 1=1 ";
            }
            if (list == null || list.size() < 1 || StringUtils.isEmpty(b_units) || StringUtils.equalsIgnoreCase("UN",b_units)) {
                return " and 1=2";
            }
            String b0110_item = "b0110";
            String e0122_item = "e0122";
            SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(this.conn, Integer.parseInt(salaryId));
            b0110_item = "b0110";
            e0122_item = "e0122";
            String orgid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "orgid"); //归属单位
            String deptid = ctrlparam.getValue(SalaryCtrlParamBo.SUM_FIELD, "deptid");//归属部门

            if (orgid != null && orgid.trim().length() > 0) {
                b0110_item = orgid;
                if (deptid != null && deptid.trim().length() > 0) {
                    e0122_item = deptid;
                } else {
                    e0122_item = "";
                }
            } else if (deptid != null && deptid.trim().length() > 0) {
                e0122_item = deptid;
                b0110_item = "";
            }
            String[] unitarr = b_units.split("`");
            privSql.append(" and(");
            for (int i = 0; i < unitarr.length; i++) {
                String codeid = unitarr[i];
                if (StringUtils.isEmpty(codeid)){
                    continue;
                }
                if (codeid.trim().length() > 2) {
                    if ("UN".equalsIgnoreCase(codeid.substring(0, 2))) {
                        if (b0110_item.length() > 0){
                            privSql.append(" or " + b0110_item + " like '" + codeid.substring(2) + "%' ");
                        }
                        else{
                            privSql.append(" or " + e0122_item + " like '" + codeid.substring(2) + "%' ");
                        }
                    } else if ("UM".equalsIgnoreCase(codeid.substring(0, 2)) ) {
                        if (e0122_item.length() > 0){
                            privSql.append(" or " + e0122_item + " like '" + codeid.substring(2) + "%' ");
                        } else {
                            privSql.append(" or e0122 like '" + codeid.substring(2) + "%' ");
                        }

                    }
                }
            }
            privSql.append(")");
            if (privSql.length() > 6) {
                //拼入了sql
                privSql = privSql.delete(5, 8);
            } else {
                //" and()"
                privSql.setLength(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privSql.toString();
    }
    /**
     * 搜索时获取数据sql
     * @author sheny
     * @param columnsFieldList 表单列指标
     * @param salaryId 薪资账套Id
     * @param type history 未归档 achieve 归档
     * @param appdate 薪资归属日期
     * @param count  薪资归属次数
     * @param valuesList 搜索内容
     * @param searchType 1为输入查询，2为方案查询
     * @param exp 方案查询公式
     * @param cond 方案查询数据
     * @return sqldata 表单数据sql
     * @date 2020/1/15 15:35
     */
    @Override
    public String getSqldata(List columnsFieldList, String type, String salaryId, String appdate, String count,
                             ArrayList<String> valuesList, String searchType, String exp, String cond) {
        StringBuffer sqldata = new StringBuffer();
        try {
            sqldata.append("select nbase,a0000,sp_flag,");
            for (int i = 0; i < columnsFieldList.size(); i++) {
                LazyDynaBean bean = (LazyDynaBean) columnsFieldList.get(i);
                String itemid = (String) bean.get("itemid");
                if (StringUtils.equalsIgnoreCase("nbase", itemid) || StringUtils.equalsIgnoreCase("a0000", itemid)) {
                    continue;
                }
                sqldata.append(itemid).append(",");
            }
            sqldata.deleteCharAt(sqldata.length() - 1);

            String tableName = "salaryhistory";
            if (StringUtils.equalsIgnoreCase(type, "achieve")) {
                tableName = "salaryarchive";
            }
            sqldata.append(" from ").append(tableName).append(" salaryData where SALARYID = '").append(salaryId).append("'");
            if (StringUtils.isNotBlank(appdate)) {
                sqldata.append(" and A00Z2 =").append(Sql_switcher.charToDate("'" + appdate + "'"));
            }
            if (StringUtils.isNotBlank(count)) {
                sqldata.append(" and A00Z3 = '").append(count).append("' ");
            }

            if (StringUtils.isNotBlank(searchType)) {
                String condSql = this.getCondSql(valuesList,searchType,exp,cond,salaryId);
                if (StringUtils.isNotBlank(condSql)) {
                    sqldata.append(" and (").append(condSql).append(") ");
                }
            }
            sqldata.append(this.getUnitsPrivSql(salaryId));
        }catch (Exception e){
            e.printStackTrace();
        }
        return sqldata.toString();
    }
    /**
     * 搜索时获取数据sql
     * @author sheny
     * @param valuesList 搜索内容
     * @param searchType 1为输入查询，2为方案查询
     * @param exp 方案查询公式
     * @param cond 方案查询数据
     * @return condSql 部分表单数据sql
     * @date 2020/1/15 15:35
     */
    public String getCondSql(ArrayList<String> valuesList,String searchType,String exp,String cond,String salaryId) {
        String condSql = "";
        try {
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            //是否定义唯一性指标 0：没定义
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
            //唯一性指标值
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
            String idField = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
            boolean flag = false;
            if(!"0".equals(uniquenessvalid)&&StringUtils.isNotEmpty(onlyname)&&isExistFieldItem(salaryId,onlyname) && !StringUtils.equalsIgnoreCase("a0101",onlyname)){
                flag = true;
            }
            // 查询类型，1为输入查询，2为方案查询
            if("1".equals(searchType)) {
                // 输入的内容
                StringBuffer str = new StringBuffer();
                for(int i=0;i<valuesList.size();i++){
                    String queryValue = SafeCode.decode((String) valuesList.get(i));
                    if(i==0){
                        str.append("A0101 like '%"+queryValue+"%'");
                    }else{
                        str.append(" or A0101 like '%"+queryValue+"%'");
                    }
                    if(StringUtils.isNotEmpty(idField) && isExistFieldItem(salaryId,idField) && !StringUtils.equalsIgnoreCase(idField,"a0101")){
                        str.append(" or "+idField+" like '%"+queryValue+"%'");
                    }
                    if(flag){
                        str.append(" or "+onlyname+" like '%"+queryValue+"%'");
                    }
                }
                if(valuesList.size()>0){
                    condSql += str.toString();
                }
            } else if ("2".equals(searchType)) {
                String tablesubModuleId="salary_"+salaryId;
                TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablesubModuleId);
                HashMap queryFields = tableCache.getQueryFields();
                // 解析表达式并获得sql语句
                FactorList parser = new FactorList(PubFunc.keyWord_reback(SafeCode.decode(exp)) ,PubFunc.keyWord_reback(SafeCode.decode(cond)), userView.getUserName(),queryFields);
                condSql += parser.getSingleTableSqlExpression("salaryData");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return condSql;
    }

    /**
     * sql校验
     * @author sheny
     * @param salaryId 薪资账套Id
     * @return isExist 表单数据sql
     * @date 2020/1/15 15:35
     */
    private boolean isExistFieldItem(String salaryId, String onlyname) {
        boolean isExist = false;
        RowSet rowSet = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select 1 from salaryset where salaryid=? and upper(itemid)=?";
            ArrayList list = new ArrayList();
            list.add(salaryId);
            list.add(onlyname.toUpperCase());
            rowSet = dao.search(sql, list);
            if (rowSet.next()) {
                isExist = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rowSet);
        }

        return isExist;
    }

    /**
     * 获取表单按钮
     * @author sheny
     * @param type history 未归档 achieve 归档
     * @param salaryId 薪资账套Id
     * @return buttonList 表单按钮集
     * @date 2020/1/15 15:35
     */
    private ArrayList getButtonList(String type,String salaryId) throws GeneralException {
        ArrayList buttonList = new ArrayList();
        ArrayList menulist = new ArrayList();
        try {
            if (StringUtils.equalsIgnoreCase(type, "achieve")) {
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction("32407403")) {
                    //还原按钮
                    LazyDynaBean oneBean = new LazyDynaBean();
                    oneBean.set("text", ResourceFactory.getProperty("GzAnalyse.historydata.button.historyData"));
                    oneBean.set("handler", "SalaryHistoryData.historyData");
                    menulist.add(oneBean);
                }
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction("32407404")) {
                    //删除按钮
                    LazyDynaBean oneBean = new LazyDynaBean();
                    oneBean.set("text", ResourceFactory.getProperty("GzAnalyse.historydata.button.deleteData"));
                    oneBean.set("handler", "SalaryHistoryData.deleteData");
                    menulist.add(oneBean);
                }
            } else {
                //归档按钮
                if (this.userView.isSuper_admin() || this.userView.hasTheFunction("32407401")) {
                    LazyDynaBean oneBean = new LazyDynaBean();
                    oneBean.set("text", ResourceFactory.getProperty("GzAnalyse.historydata.button.achieveData"));
                    oneBean.set("handler", "SalaryHistoryData.achieveData");
                    menulist.add(oneBean);
                }
            }
            //报表输出按钮
            LazyDynaBean oneBean = new LazyDynaBean();
            oneBean.set("text", ResourceFactory.getProperty("GzAnalyse.historydata.button.emportData"));
            oneBean.set("handler", "SalaryHistoryData.emportReport");
            menulist.add(oneBean);
            buttonList.add(this.getMenuStr(ResourceFactory.getProperty("GzAnalyse.historydata.button.menu"), menulist));
            //报表输出按钮
            ArrayList<LazyDynaBean> reportList = getReportList(salaryId);
            StringBuffer bottoStr = new StringBuffer("<jsfn>{xtype:'button',id:'common_Report_button',");
            if (reportList.size() == 1) {
                bottoStr.append("text:'" + reportList.get(0).get("text") + "',hidden:false,");
            } else {
                String hidden = "false";
                if (reportList.size() == 0) {
                    hidden = "true";
                }
                bottoStr.append("html:'").append(ResourceFactory.getProperty("GzAnalyse.historydata.button.salaryReport"));
                bottoStr.append("<img style=\"width:10px;\" src=\"/ext/ext6/resources/images/button/arrow.gif\"/>',hidden:" + hidden + ",");
            }
            bottoStr.append("handler:function() { SalaryHistoryData.salaryReport();}");
            bottoStr.append("}</jsfn>");
            buttonList.add(bottoStr.toString());
            //导出excel按钮
            ButtonInfo exportExcelButton = new ButtonInfo();
            exportExcelButton.setText(ResourceFactory.getProperty("GzAnalyse.historydata.button.salaryImport"));
            exportExcelButton.setFunctype(ButtonInfo.FNTYPE_EXPORT);
            buttonList.add(exportExcelButton);
            //buttonList.add(new ButtonInfo(ResourceFactory.getProperty("GzAnalyse.historydata.button.salaryImport"), "SalaryHistoryData.salaryImport"));
            //搜索框
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
            String idField = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");
            StringBuffer info = new StringBuffer();
            info.append("请输入姓名");
            if(StringUtils.isNotEmpty(idField) && isExistFieldItem(salaryId,idField) && !StringUtils.equalsIgnoreCase("a0101",idField)){
                info.append(",").append(DataDictionary.getFieldItem(idField).getItemdesc());
            }
            if(StringUtils.isNotEmpty(onlyname) && isExistFieldItem(salaryId,onlyname)&& !StringUtils.equalsIgnoreCase("a0101",onlyname) && !StringUtils.equalsIgnoreCase(onlyname,idField)){
                info.append(",").append(DataDictionary.getFieldItem(onlyname).getItemdesc());
            }
            ButtonInfo button = new ButtonInfo(info.toString(),"","GZ00001301");
            button.setType(ButtonInfo.TYPE_QUERYBOX);
            buttonList.add(button);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("getButtonListError");
        }

        return buttonList;
    }
    /**
             * 拼接memu表单按钮json
     * @author sheny
     * @param name 下拉框名称
     * @param list 下拉框按钮集
     * @return str memu表单按钮json
     * @date 2020/1/15 15:35
     */
    private String getMenuStr(String name,ArrayList list) throws GeneralException{
        StringBuffer str = new StringBuffer();
        try{
            if(name.length()>0){
                str.append("<jsfn>{xtype:'button',text:'"+name+"'");
            }
            str.append(",menu:{items:[");
            for(int i=0;i<list.size();i++){
                LazyDynaBean bean = (LazyDynaBean) list.get(i);
                if(i!=0)
                    str.append(",");
                str.append("{");
                if(bean.get("xtype")!=null&&bean.get("xtype").toString().length()>0)
                    str.append("xtype:'"+bean.get("xtype")+"'");
                if(bean.get("text")!=null&&bean.get("text").toString().length()>0)
                    str.append("text:'"+bean.get("text")+"'");
                if(bean.get("id")!=null&&bean.get("id").toString().length()>0)
                    str.append(",id:'"+bean.get("id")+"'");
                if(bean.get("handler")!=null&&bean.get("handler").toString().length()>0){
                    if(bean.get("xtype")!=null&& "datepicker".equalsIgnoreCase(bean.get("xtype").toString())){//时间控件单独处理一下
                        str.append(",todayTip:''");//消除今天 按钮提示文字
                        str.append(",handler:function(picker, date){"+bean.get("handler")+";}");
                    }else{
                        str.append(",handler:function(){"+bean.get("handler")+"();}");
                    }
                }
                if(bean.get("icon")!=null&&bean.get("icon").toString().length()>0)
                    str.append(",icon:'"+bean.get("icon")+"'");
                if(bean.get("value")!=null&&bean.get("value").toString().length()>0)
                    str.append(",value:"+bean.get("value")+"");
                ArrayList menulist = (ArrayList)bean.get("menu");
                if(menulist!=null&&menulist.size()>0){
                    str.append(this.getMenuStr("", (ArrayList)bean.get("menu")));
                }
                str.append("}");
            }
            str.append("]}");
            if(name.length()>0){
                str.append("}</jsfn>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GeneralException("getMenuStrError");
        }
        return str.toString();
    }

    @Override
    public void archiveSalaryHistoryData(String type, String startDate, String endDate,String salaryId) throws GeneralException {
    	try {
    		//进行数据归档
    		salaryHistoryDataDao.archiveSalaryHistoryData(type, startDate, endDate, salaryId, this.userView);
    		//个税表归档
    		pigeonholeTaxData(type,startDate,endDate,salaryId,this.userView,1);
    	}catch(Exception e) {
    		e.printStackTrace();
    		throw new GeneralException("gz.historyData.msg.archiveDataError");
    	}
    }

    /**
     *
     * @param type 0：全部 1：时间范围
     * @param startDate
     * @param endDate
     * @param salaryId
     * @return
     * @throws GeneralException
     */
    @Override
    public String revertSalaryHistoryData(String type, String startDate, String endDate,String salaryId) throws GeneralException {
        String fileName = "";
        try {
            StringBuffer sql = new StringBuffer("");
    		boolean repeatData=salaryHistoryDataDao.repeatData(type, startDate, endDate,salaryId, userView,sql);
    		if(repeatData){
    			ExportExcelUtil excelUtil = new ExportExcelUtil(this.conn);
    			fileName = ResourceFactory.getProperty("GzAnalyse.historydata.revertSalaryFileTitle")+"_"+this.userView.getUserName() + ".xls";
    			ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
    			LazyDynaBean bean = new LazyDynaBean();
    			//归属日期
    			bean.set("content", ResourceFactory.getProperty("gz.columns.a00z0"));// 列头名称
    			bean.set("itemid", "A00Z0");// 列头代码
    			bean.set("codesetid", "0");// 列头代码
    			bean.set("decwidth", "0");// 列小数点后面位数
    			bean.set("colType", "D");// 该列数据类型
    			bean.set("dateFormat", "yyyy-MM");
    			headList.add(bean);

    			//归属次数
    			bean = new LazyDynaBean();
    			bean.set("content", ResourceFactory.getProperty("gz.columns.a00z1"));// 列头名称
    			bean.set("itemid", "A00Z1");// 列头代码
    			bean.set("codesetid", "0");// 列头代码
    			bean.set("decwidth", "0");// 列小数点后面位数
    			bean.set("colType", "N");// 该列数据类型
    			headList.add(bean);

    			//发放日期
    			bean = new LazyDynaBean();
    			bean.set("content", ResourceFactory.getProperty("gz_new.gz_accounting.send_time"));// 列头名称
    			bean.set("itemid", "A00Z2");// 列头代码
    			bean.set("codesetid", "0");// 列头代码
    			bean.set("decwidth", "0");// 列小数点后面位数
    			bean.set("colType", "D");// 该列数据类型
    			bean.set("dateFormat", "yyyy-MM");
    			headList.add(bean);

    			//发放次数
    			bean = new LazyDynaBean();
    			bean.set("content", ResourceFactory.getProperty("label.gz.count"));// 列头名称
    			bean.set("itemid", "A00Z3");// 列头代码
    			bean.set("codesetid", "0");// 列头代码
    			bean.set("decwidth", "0");// 列小数点后面位数
    			bean.set("colType", "N");// 该列数据类型
    			headList.add(bean);

    			//姓名
    			bean = new LazyDynaBean();
    			bean.set("content", ResourceFactory.getProperty("label.title.name"));// 列头名称
    			bean.set("itemid", "A0101");// 列头代码
    			bean.set("codesetid", "0");// 列头代码
    			bean.set("decwidth", "0");// 列小数点后面位数
    			bean.set("colType", "A");// 该列数据类型
    			headList.add(bean);
    			excelUtil.exportExcelBySql(fileName,"",null, headList,sql.toString(), null,0);
    			fileName = SafeCode.encode(PubFunc.encrypt(fileName));
    			//return fileName;
    		}else{
    			salaryHistoryDataDao.revertSalaryHistoryData(type, startDate, endDate, salaryId, userView);
    			//还原个税明细数据
    			pigeonholeTaxData(type,startDate,endDate,salaryId,userView,2);
    		}
    	}catch(Exception e) {
    		e.printStackTrace();
    		throw new GeneralException("gz.historyData.msg.revertDataError");
    	}
    	return fileName;
    }

    @Override
    public void deleteSalaryHistoryData(String type, String startDate, String endDate,String salaryId) throws GeneralException {
    	try {
			salaryHistoryDataDao.deleteSalaryHistoryData(type, startDate, endDate, salaryId, userView);
			//删除个税归档数据
		 	pigeonholeTaxData(type,startDate,endDate,salaryId,userView,3);
    	}catch(Exception e) {
    		e.printStackTrace();
    		throw new GeneralException("gz.historyData.msg.deleteDataError");
    	}
    }

	@Override
	public void syncSalaryarchiveStrut() throws GeneralException {
        RowSet rowSet = null;
        RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rowSet =dao.search("select * from salaryarchive where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			HashMap map = new HashMap();
			for(int i=1;i<=data.getColumnCount();i++)
			{
				String columnName=data.getColumnName(i).toLowerCase();
				/**系统项不进行更改*/
				if("nbase".equalsIgnoreCase(columnName)|| "a0100".equalsIgnoreCase(columnName)|| "a00z0".equalsIgnoreCase(columnName)
						|| "a00z1".equalsIgnoreCase(columnName)|| "salaryid".equalsIgnoreCase(columnName)||
						"a00z2".equalsIgnoreCase(columnName)|| "a00z3".equalsIgnoreCase(columnName)||
						"a01z0".equalsIgnoreCase(columnName)|| "a0000".equalsIgnoreCase(columnName)||
						"b0110".equalsIgnoreCase(columnName)|| "e0122".equalsIgnoreCase(columnName)||
						"a0101".equalsIgnoreCase(columnName)|| "add_flag".equalsIgnoreCase(columnName)||
						"userflag".equalsIgnoreCase(columnName)|| "sp_flag".equalsIgnoreCase(columnName)||
						"curr_user".equalsIgnoreCase(columnName)|| "appuser".equalsIgnoreCase(columnName))
				{
					continue;
				}
				int columnType=data.getColumnType(i);
				int size=data.getColumnDisplaySize(i);
				int scale=data.getScale(i);
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("columnType", columnType+"");
				bean.set("name", columnName);
				bean.set("size", size+"");
				bean.set("scale", scale+"");
				map.put(columnName.toUpperCase(), bean);
			}
			rs = dao.search("select * from salaryhistory where 1=2");
			ResultSetMetaData meta=rs.getMetaData();
			ArrayList addList = new ArrayList();
			ArrayList modifyList = new ArrayList();
			ArrayList alertList = new ArrayList();
			HashMap alertMap = new HashMap();
			for(int j=1;j<=meta.getColumnCount();j++)
			{
				String name=meta.getColumnName(j);
				if("nbase".equalsIgnoreCase(name)|| "a0100".equalsIgnoreCase(name)|| "a00z0".equalsIgnoreCase(name)
						|| "a00z1".equalsIgnoreCase(name)|| "salaryid".equalsIgnoreCase(name)||
						"a00z2".equalsIgnoreCase(name)|| "a00z3".equalsIgnoreCase(name)||
						"a01z0".equalsIgnoreCase(name)|| "a0000".equalsIgnoreCase(name)||
						"b0110".equalsIgnoreCase(name)|| "e0122".equalsIgnoreCase(name)||
						"a0101".equalsIgnoreCase(name)|| "add_flag".equalsIgnoreCase(name)||
						"userflag".equalsIgnoreCase(name)|| "sp_flag".equalsIgnoreCase(name)||
						"curr_user".equalsIgnoreCase(name)|| "appuser".equalsIgnoreCase(name))
				{
					continue;
				}
				/**新的字段属性*/
				int stype=meta.getColumnType(j);
				/* 薪资发放-结构同步-tomcat7上报错问题 xiaoyun 2014-10-30 start */
				int ssize=meta.getColumnDisplaySize(j);
				/* 薪资发放-结构同步-tomcat7上报错问题 xiaoyun 2014-10-30 end */
				int sscale=meta.getScale(j);
				/**归档表里有这个字段，判断是否类型改变,如果改变，同步成与工资历史表相同*/
				if(map.get(name.toUpperCase())!=null)
				{
					/**以前的字段属性*/
					LazyDynaBean bean = (LazyDynaBean)map.get(name.toUpperCase());
					int type = Integer.parseInt((String)bean.get("columnType"));
					int size = Integer.parseInt((String)bean.get("size"));
					int scale = Integer.parseInt((String)bean.get("scale"));
                    if (type == stype && size >= ssize && scale >= sscale) {
                        continue;
                    }
					Field field = new Field(name,name);
					if("appprocess".equalsIgnoreCase(name))
					{
						field.setDatatype(DataType.CLOB);
					}
					else
					{
			    		field.setDatatype(DataType.sqlTypeToType(stype));
				    	field.setLength(ssize-sscale);
				    	field.setDecimalDigits(sscale);
					}
					if(DataType.sqlTypeToType(type)==DataType.sqlTypeToType(stype)){
						alertList.add(field);
						FieldItem tempItem=DataDictionary.getFieldItem(name);
						alertMap.put(name, tempItem);
					}else{
			    		modifyList.add(field);
                    }
				}
				/**归档表里没有，则加入*/
				else
				{
					Field field = new Field(name,name);
					FieldItem item=DataDictionary.getFieldItem(name.trim().toLowerCase());
					if(item!=null)
					{
						if("M".equalsIgnoreCase(item.getItemtype()))
						{
							field.setDatatype(DataType.CLOB);
						}
						else
						{
							field.setDatatype(DataType.sqlTypeToType(stype));
				    		field.setLength(ssize);
					    	field.setDecimalDigits(sscale);
						}

					}
					else if("appprocess".equalsIgnoreCase(name))
					{
						field.setDatatype(DataType.CLOB);
					}
					else
					{
				    	field.setDatatype(DataType.sqlTypeToType(stype));
			    		field.setLength(ssize-sscale);
				    	field.setDecimalDigits(sscale);
					}
					addList.add(field);
				}
			}
			DbWizard dbw=new DbWizard(this.conn);
			Table table=new Table("salaryarchive");
		    if(Sql_switcher.searchDbServer()!=2){  //不为oracle
                for (int i = 0; i < alertList.size(); i++) {
                    table.addField((Field)alertList.get(i));
                }
                if (alertList.size() > 0) {
                    dbw.alterColumns(table);
                }
                table.clear();
		    }else{
		    	SalaryTemplateBo bo=new SalaryTemplateBo(this.conn);
		    	bo.syncGzOracleField(data,alertMap,"salaryarchive");
		    }
			table.clear();
            for (int i = 0; i < modifyList.size(); i++) {
                table.addField((Field) modifyList.get(i));
            }
            if (modifyList.size() > 0) {
                dbw.dropColumns(table);
                dbw.addColumns(table);
            }
			table.clear();
			for(int i=0;i<addList.size();i++)
				table.addField((Field)addList.get(i));
            if (addList.size() > 0) {
                dbw.addColumns(table);
            }
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("gz.historyData.msg.syncHistorySalaryDataError");
		}finally {
            PubFunc.closeDbObj(rowSet);
            PubFunc.closeDbObj(rs);
        }

	}

	@Override
	public void syncSalaryTaxArchiveStrut() throws GeneralException {
        RowSet rowSet = null;
		try {
			DbWizard dbw=new DbWizard(this.conn);
			String _str="/a0100/tax_date/a00z0/a00z1/tax_max_id/salaryid/a0000/b0110/e0122/a0101/declare_tax/taxitem/sskcs/ynse/basedata/sl/sds/taxmode/description/userflag/flag/a00z2/a00z3/ynse_field/deptid/";
			ArrayList<Field> al=searchCommonItemList();
			if(!dbw.isExistTable("taxarchive", false))
			{
				Table table=new Table("taxarchive");
                for (Field field : al) {
					table.addField(field);
                }
				dbw.createTable(table);
			}
			ContentDAO dao=new ContentDAO(this.conn);
			ArrayList chgList=searchDynaItemList();

			HashMap amap=new HashMap();
            rowSet = dao.search("select * from gz_tax_mx where 1=2");
			ResultSetMetaData data=rowSet.getMetaData();
			ArrayList addList = new ArrayList();
			for(int i=1;i<=data.getColumnCount();i++)
			{
					String columnName=data.getColumnName(i).toLowerCase();
					amap.put(columnName, "1");
			}


			HashMap map=new HashMap();
			for(int i=0;i<chgList.size();i++)
			{
				Field field=(Field)chgList.get(i);
				FieldItem tempItem=DataDictionary.getFieldItem(field.getName().toLowerCase());
		    	if(tempItem!=null)
		    	{
                    if (amap.get(field.getName().toLowerCase()) == null) {
                        addList.add((FieldItem) tempItem.clone());
                    }
		    		map.put(tempItem.getItemid(),tempItem);
		    	}
			}
			if(addList.size()>0)
			{
				Table table=new Table("gz_tax_mx");
                for (int i = 0; i < addList.size(); i++) {
					table.addField(((FieldItem)addList.get(i)).cloneField());
                }
				dbw.addColumns(table);
			}

			//将固定字段加入待同步列表
            FieldItem item;
			for(Field field:al){
				item=new FieldItem();
				item.setItemid(field.getName());
				item.setItemdesc(field.getLabel());
				if(field.getDataType()==DataType.INT||field.getDataType()==DataType.FLOAT){
					item.setItemtype("N");
					item.setDecimalwidth(field.getDecimalDigits());
				}else if(field.getDataType()==DataType.DATE){
					item.setItemtype("D");
                }else if(field.getDataType()==DataType.STRING){
					item.setItemtype("A");
                }
				item.setItemlength(field.getLength());
				map.put(item.getItemid(),item);
			}
			 ArrayList alterList=new ArrayList();
			 ArrayList resetList=new ArrayList();
			 ArrayList delList=new ArrayList();


			 rowSet=dao.search("select * from taxarchive where 1=2");
			 data=rowSet.getMetaData();
			 HashMap existMap=new HashMap();

			 for(int i=1;i<=data.getColumnCount();i++)
			 {
					String columnName=data.getColumnName(i).toLowerCase();

					if(_str.indexOf("/"+columnName+"/")==-1)
					{
                        if (amap.get(columnName) == null && DataDictionary.getFieldItem(columnName) != null) {
                            delList.add(DataDictionary.getFieldItem(columnName));
                        }
					}

					existMap.put(columnName, "1");
					if(map.get(columnName)!=null)
					{
						FieldItem tempItem=(FieldItem)map.get(columnName);
						int columnType=data.getColumnType(i);
						int size=data.getColumnDisplaySize(i);
						int scale=data.getScale(i);
						switch(columnType)
						{
							case java.sql.Types.INTEGER:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if(size<tempItem.getItemlength()&&tempItem.getItemlength()<=10) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else
										resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.TIMESTAMP:
								if(!"D".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.VARCHAR:
								if("A".equals(tempItem.getItemtype()))
								{
									if(tempItem.getItemlength()>size)
										alterList.add(tempItem.cloneField());
								}
								else
									resetList.add(tempItem.cloneField());
								break;
							case java.sql.Types.DOUBLE:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else
										resetList.add(tempItem.cloneField());
								}


								break;
							case java.sql.Types.NUMERIC:
								if("N".equals(tempItem.getItemtype()))
								{
									if(tempItem.getDecimalwidth()!=scale)
										alterList.add(tempItem.cloneField());
									else if((size-scale)<tempItem.getItemlength()) //2013-11-23  如果指标长度改大了，需同步结构
									{
										alterList.add(tempItem.cloneField());
									}
								}
								if(!"N".equals(tempItem.getItemtype()))
								{
									if("A".equals(tempItem.getItemtype()))
										alterList.add(tempItem.cloneField());
									else
										resetList.add(tempItem.cloneField());
								}
								break;
							case java.sql.Types.LONGVARCHAR:
								if(!"M".equals(tempItem.getItemtype()))
								{
									resetList.add(tempItem.cloneField());
								}
								break;
						}
					}
				}

			    Table table=new Table("taxarchive");
			    if(Sql_switcher.searchDbServer()!=2)  //不为oracle
			    {
				    for(int i=0;i<alterList.size();i++){
                        table.addField((Field)alterList.get(i));
                    }
					if(alterList.size()>0){
                        dbw.alterColumns(table);
                    }
                    table.clear();
			    }
			    else
			    {
			    	SalaryTemplateBo bo = new SalaryTemplateBo(this.conn);
			    	bo.syncGzOracleField(data,map,"taxarchive");
			    }
				 for(int i=0;i<resetList.size();i++){
                    table.addField((Field)resetList.get(i));
                 }
				 if(resetList.size()>0)
				 {
					 dbw.dropColumns(table);
					 dbw.addColumns(table);
				 }

				 table.clear();
				 int n=0;
				 for(int i=0;i<addList.size();i++)
				 {
					FieldItem field=(FieldItem)addList.get(i);
					if(existMap.get(field.getItemid().toLowerCase())==null)
					{
						table.addField(field.cloneField());
						n++;
					}
				 }
				 if(n>0){
					 dbw.addColumns(table);
                 }

				 table.clear();
				 n=0;
				 for(int i=0;i<delList.size();i++)
				 {
						FieldItem field=(FieldItem)delList.get(i);
						table.addField(field.cloneField());
						n++;
				 }
				 if(n>0){
					 dbw.dropColumns(table);
                 }
				if(existMap.get("a00z2")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("A00Z2","A00Z2");
					field.setDatatype(DataType.DATE);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("a00z3")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("A00Z3","A00Z3");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("ynse_field")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("ynse_field","ynse_field");
					field.setDatatype(DataType.STRING);
					field.setLength(5);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("deptid")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("deptid","deptid");
					field.setDatatype(DataType.STRING);
					field.setLength(30);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("ynse")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("ynse","ynse");
					field.setDatatype(DataType.FLOAT);
					field.setLength(12);
					field.setDecimalDigits(4);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("userflag")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("UserFlag","UserFlag");
					field.setDatatype(DataType.STRING);
					field.setLength(50);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("declare_tax")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("declare_date","declare_date");
					field.setDatatype(DataType.DATE);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("salaryid")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("salaryid","salaryid");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("taxmode")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("taxmode","taxmode");
					field.setDatatype(DataType.STRING);
					field.setLength(10);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("description")==null)
				{
					Table tbl=new Table("taxarchive");
					Field 	field=new Field("description","description");
					field.setDatatype(DataType.STRING);
					field.setLength(200);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				if(existMap.get("flag")==null)
				{
					Table tbl=new Table("taxarchive");
					Field field=new Field("flag","flag");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					tbl.addField(field);
					dbw.addColumns(tbl);
				}
				for(int i=0;i<chgList.size();i++){//插入动态维护的字段 2016-10-13 zhanghua
					Field field=(Field)chgList.get(i);
					if(existMap.get(field.getName().toLowerCase())==null){
						Table tbl=new Table("taxarchive");
						tbl.addField(field);
						dbw.addColumns(tbl);
					}
				}
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("gz.historyData.msg.syncTaxScheduleStructureError");
		}finally {
            PubFunc.closeDbObj(rowSet);
        }

	}

	/**
	 * 取得个税明细表的固定字段列表
	 * @return
	 * @throws Exception
	 * @author sunml
	 * 2020年1月20日
	 */
	private ArrayList<Field> searchCommonItemList() throws Exception{
		ArrayList<Field> al=new ArrayList<Field>();

		Field field=new Field("NBASE","NBASE");
		field.setDatatype(DataType.STRING);
		field.setLength(3);
		al.add(field);

		field=new Field("A0100","A0100");
		field.setDatatype(DataType.STRING);
		field.setLength(8);
		al.add(field);

		field=new Field("tax_date","tax_date");
		field.setDatatype(DataType.DATE);
		al.add(field);

		field=new Field("A00Z0","A00Z0");
		field.setDatatype(DataType.DATE);
		al.add(field);

		field=new Field("A00Z1","A00Z1");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);

		field=new Field("tax_max_id","tax_max_id");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		field.setKeyable(true);
		field.setNullable(false);
		al.add(field);

		field=new Field("salaryid","salaryid");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);

		field=new Field("A0000","A0000");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);

		field=new Field("B0110","B0110");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);

		field=new Field("E0122","E0122");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);

		field=new Field("A0101","A0101");
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		al.add(field);

		field=new Field("declare_tax","declare_tax");
		field.setDatatype(DataType.DATE);
		al.add(field);

		field=new Field("taxitem","taxitem");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		al.add(field);

		field=new Field("sskcs","sskcs");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);

		field=new Field("ynse","ynse");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);

		field=new Field("basedata","basedata");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);

		field=new Field("sl","sl");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);

		field=new Field("sds","sds");
		field.setDatatype(DataType.FLOAT);
		field.setLength(12);
		field.setDecimalDigits(4);
		al.add(field);

		field=new Field("taxmode","taxmode");
		field.setDatatype(DataType.STRING);
		field.setLength(10);
		al.add(field);

		field=new Field("description","description");
		field.setDatatype(DataType.STRING);
		field.setLength(200);
		al.add(field);


		field=new Field("UserFlag","UserFlag");
		field.setDatatype(DataType.STRING);
		field.setLength(50);
		al.add(field);


		field=new Field("flag","flag");
		field.setDatatype(DataType.INT);
		field.setLength(10);
		return al;
	}

	/**
	 * 返回个税明细表动态维护的指标
	 * @return
	 * @throws Exception
	 * @author sunml
	 * 2020年1月20日
	 */
	private ArrayList searchDynaItemList() throws Exception {
		ArrayList chglist=new ArrayList();
			RecordVo ctrlvo=ConstantParamter.getRealConstantVo("GZ_TAX_MX", conn);
			if(!(ctrlvo==null|| "".equals(ctrlvo.getString("str_value"))))
			{
				Document doc=PubFunc.generateDom(ctrlvo.getString("str_value"));

				String str_path="/param/items";
				XPath xpath=XPath.newInstance(str_path);
				List childlist=xpath.selectNodes(doc);
				if(childlist.size()>0)
				{
					Element element=(Element)childlist.get(0);
					String columns=element.getText();
					String[] arr=StringUtils.split(columns, ",");
					SalaryPkgBo pkgbo=new SalaryPkgBo(this.conn,null,0);
					for(int i=0;i<arr.length;i++)
					{
						Field field=pkgbo.searchItemById(arr[i]);
						if(field!=null)
						{
							chglist.add(field);
						}
					}//for loop end.
				}//if list end.
			}//if ctrlvo end.
		return chglist;
	}

	@Override
	public void pigeonholeTaxData(String type, String startDate, String endDate, String salaryId, UserView userView, int opt) throws GeneralException {
		String tableName="t#"+userView.getUserName()+"_gz_1";
		try {
			if(opt==3) //删除个税归档表
			{
				salaryHistoryDataDao.deleteTaxData(type, startDate, endDate,salaryId, userView);
			}
			else
			{
				DbWizard dbw=new DbWizard(this.conn);
				if(dbw.isExistTable(tableName, false)){
					dbw.dropTable(tableName);
                }
				Table table=new Table(tableName);

				Field field=new Field("NBASE","NBASE");
				field.setDatatype(DataType.STRING);
				field.setLength(3);
				table.addField(field);
				field=new Field("A0100","A0100");
				field.setDatatype(DataType.STRING);
				field.setLength(8);
				table.addField(field);
				field=new Field("A00Z0","A00Z0");
				field.setDatatype(DataType.DATE);
				table.addField(field);
				field=new Field("A00Z1","A00Z1");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				field=new Field("salaryid","salaryid");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				field=new Field("tax_max_id","tax_max_id");
				field.setDatatype(DataType.INT);
				field.setLength(10);
				table.addField(field);
				if(Sql_switcher.searchDbServer()!=1)
				{
					field=new Field("id","id");
					field.setDatatype(DataType.INT);
					field.setLength(10);
					table.addField(field);
				}
				dbw.createTable(table);

				if(opt==1)//归档
				{
					salaryHistoryDataDao.archiveTaxData(tableName,type,startDate, endDate, salaryId,userView,2);
				}
				else  // 还原
				{
					salaryHistoryDataDao.revertTaxData(tableName,type,startDate, endDate,salaryId, userView,2);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
			throw new GeneralException("操作个税明细表失败！");
		}
	}

	@Override
    public boolean strutIsChange() throws GeneralException
	   {
		   boolean flag=false;
           RowSet rowSet = null;
           try
		   {
			   HashMap archiveMap = new HashMap();
			   ContentDAO dao = new ContentDAO(this.conn);
			   String sql = "select * from salaryarchive where 1=2";
			   rowSet = dao.search(sql);
			   ResultSetMetaData data=rowSet.getMetaData();
			    for(int i=1;i<=data.getColumnCount();i++)
			    {
			    	String columnName=data.getColumnName(i).toUpperCase();
			    	archiveMap.put(columnName,columnName);
			    }
			    sql = "select * from salaryhistory where 1=2";
			    rowSet=dao.search(sql);
			    for(int i=1;i<=data.getColumnCount();i++)
			    {
			    	String columnName=data.getColumnName(i).toUpperCase();
			    	if(archiveMap.get(columnName)==null)
			    	{
			    		flag=true;
			    		break;
			    	}
			    }
		   }
		   catch(Exception e)
		   {
			   e.printStackTrace();
			   throw new GeneralException("判断数据结构错误！无法还原！");
		   }finally {
               PubFunc.closeDbObj(rowSet);
           }
		   return flag;
	   }

    @Override
    public void listSalaryTemplateData(String name) throws GeneralException {

    }

    /**
     * 生成列头
     * @author liuyd
     * @return ArrayList
     * @date 2020/2/13 13:50
     */
    private ArrayList<ColumnsInfo> getSalaryTemplateColumnList(){
        ArrayList<ColumnsInfo> columnsInfoList = new ArrayList<ColumnsInfo>();
        //编号
        ColumnsInfo salaryIdInfo = getColumnsInfo("salaryid",  ColumnsInfo.LOADTYPE_ALWAYSLOAD,ResourceFactory.getProperty("GzAnalyse.historydata.num"),"A",100,"0");
        salaryIdInfo.setTextAlign("right");
        columnsInfoList.add(salaryIdInfo);

        ColumnsInfo salaryIdInfoJiaMi = getColumnsInfo("salaryidjiami",  ColumnsInfo.LOADTYPE_ONLYLOAD,"salaryIdJiaMi","A",60,"0");
        columnsInfoList.add(salaryIdInfoJiaMi);

        //薪资类别
        ColumnsInfo salaryTemplateInfo = getColumnsInfo("cname", ColumnsInfo.LOADTYPE_ALWAYSLOAD,ResourceFactory.getProperty("GzAnalyse.historydata.salaryType"),"A",398,"0");
        salaryTemplateInfo.setRendererFunc("SalaryTemplate.hideRightBorder");
        columnsInfoList.add(salaryTemplateInfo);
        return columnsInfoList;
    }

    @Override
    public String getHistoryTemplateConfig(String type,List<String> valuesList) throws GeneralException {
        String historyTemplateConfig = "";
        try{
            String subModuleId = "switchSalaryTemplate";
            //生成列头
            ArrayList<ColumnsInfo> columnsInfoList = getSalaryTemplateColumnList();
            //表格生成
            TableConfigBuilder builder = new TableConfigBuilder(subModuleId, columnsInfoList, subModuleId, userView, conn);
            builder.setRowdbclick("SalaryTemplate.rowdbclick");
            builder.setPageSize(10);
            //定义查询数据sql
            StringBuffer sql = new StringBuffer();
            ArrayList<String> sqlList = new ArrayList<String>();
            sql.append("select salaryid,cname from salarytemplate where (cstate is null or cstate='')");
            // 快速查询
            if ("1".equals(type)){
                // 拼接查询条件
                for(int i = 0; i < valuesList.size(); i++){
                    String queryVal = valuesList.get(i);
                    if(i == 0){
                        sql.append(" and (");
                    }else{
                        sql.append(" and ");
                    }
                    sql.append("(salaryid like ? or cname like ?)");
                    sqlList.add("%" + queryVal + "%");
                    sqlList.add("%" + queryVal + "%");

                    if (i == valuesList.size() - 1) {
                        sql.append(")");
                    }
                }
            }
            sql.append(" order by salaryid");
            List<DynaBean> switchSalaryTemplateList = salaryHistoryDataDao.getSwitchSalaryTemplateList(sql,sqlList);
            ArrayList<LazyDynaBean> switchSalaryTemplateDataList = new ArrayList<LazyDynaBean>();
            for(DynaBean lazyDynaBean : switchSalaryTemplateList){
                String salaryId = (String) lazyDynaBean.get("salaryid");
                if (this.userView.isSuper_admin() || this.userView.isHaveResource(IResourceConstant.GZ_SET, salaryId)) {
                    LazyDynaBean switchSalaryTemplateData = new LazyDynaBean();
                    switchSalaryTemplateData.set("salaryid",lazyDynaBean.get("salaryid"));
                    switchSalaryTemplateData.set("salaryidjiami",lazyDynaBean.get("salaryidjiami"));
                    switchSalaryTemplateData.set("cname", lazyDynaBean.get("cname"));
                    switchSalaryTemplateDataList.add(switchSalaryTemplateData);
                }
            }
            builder.setDataList(switchSalaryTemplateDataList);

            ArrayList buttonList = new ArrayList();
            //查询框
            ButtonInfo querybox = new ButtonInfo();
            querybox.setFunctionId("GZ00001308");
            querybox.setType(ButtonInfo.TYPE_QUERYBOX);
            //不显示方案查询
            querybox.setShowPlanBox(false);
            //请输入编号或名称
            querybox.setText(ResourceFactory.getProperty("GzAnalyse.historydata.searchmsg"));
            buttonList.add(querybox);
            builder.setTableTools(buttonList);
            historyTemplateConfig = builder.createExtTableConfig();

        }catch (Exception e){
            log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.historyTemplate.msg.getSalaryTemplateError",e);
            throw new GeneralException("gz.historyTemplate.msg.getSalaryTemplateError");
        }
        return historyTemplateConfig;
    }

    /**
     * 获取切换日期组件的数据
     * @param salaryId
     * @param appdate
     * @param transType
     * @return
     * @throws GeneralException
     */
    @Override
    public ArrayList getDateList(String salaryId, String appdate, String transType) throws GeneralException {
        ArrayList dateList = new ArrayList();
        StringBuffer querySql = new StringBuffer();
        ArrayList paramList = new ArrayList();
        ArrayList yearList = new ArrayList();
        RowSet rowSet = null;
        paramList.add(salaryId);
        if(StringUtils.equalsIgnoreCase(transType,"history")){
            querySql.append("select distinct A00Z2 from SalaryHistory where salaryid = ? order by A00Z2");
        }else if (StringUtils.equalsIgnoreCase(transType,"achieve")){
            querySql.append("select distinct A00Z2 from salaryarchive where salaryid = ? order by A00Z2");
        }
        /*querySql.append("select A00Z2 from (select distinct A00Z2 from SalaryHistory where salaryid = ? ");
        querySql.append("union all select distinct A00Z2 from salaryarchive where salaryid = ?) temp order by A00Z2");*/
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            /*String currentYear = appdate.substring(0,appdate.indexOf("-"));
            String currentMonth = appdate.substring(appdate.indexOf("-")+1,appdate.lastIndexOf("-"));*/
            Calendar now = Calendar.getInstance();
            //当前年
            String currentYear = now.get(Calendar.YEAR) + "";
            //当前月
            String currentMonth = now.get(Calendar.MONTH) + 1 + "";
            String tempYear = "";
            rowSet = dao.search(querySql.toString(),paramList);
            while (rowSet.next()){
                String year = DateStyle.dateformat(rowSet.getTimestamp("A00Z2"),"yyyy");
                if(!StringUtils.equalsIgnoreCase(tempYear,year)){
                    tempYear = year;
                    yearList.add(tempYear);
                }else{
                    continue;
                }
                //采用最近发放日期拼装日期数据
                /*String month = a00z2.substring(a00z2.indexOf("-")+1,a00z2.lastIndexOf("-"));
                date.put("year",year + ResourceFactory.getProperty("label.query.year"));
                date.put("monthOrder", Integer.parseInt(month));
                date.put("desc",month + ResourceFactory.getProperty("label.query.month"));
                if (StringUtils.equalsIgnoreCase(year,currentYear) && StringUtils.equalsIgnoreCase(month,currentMonth)){
                    date.put("state",1);
                }else {
                    date.put("state",2);
                }
                dateList.add(date);*/
            }
            for (int i=0;i<yearList.size();i++){
                String year = (String) yearList.get(i);
                //按照年份拼装12个月数据
                dateList = this.getMonthData(dateList,year,currentYear,currentMonth);
            }
        }catch (Exception e){
            log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.historyData.msg.getDateListError",e);
            throw new GeneralException("gz.historyData.msg.getDateListError");
        }finally {
            PubFunc.closeResource(rowSet);
        }
        return dateList;
    }

    /**
     * 拼装月数据
     * @param dateList
     * @param year
     * @param currentYear
     * @param currentMonth
     */
    private ArrayList getMonthData(ArrayList dateList, String year, String currentYear, String currentMonth) {
        for (int i=1;i<=12;i++){
            HashMap dateMap = new HashMap();
            String month = i+"";
            /*if(i<10){
                month = "0"+i;
            }*/
            dateMap.put("year",year + ResourceFactory.getProperty("label.query.year"));
            dateMap.put("monthOrder", i);
            dateMap.put("desc",i + ResourceFactory.getProperty("label.query.month"));
            if (StringUtils.equalsIgnoreCase(year,currentYear) && StringUtils.equalsIgnoreCase(month,currentMonth)){
                dateMap.put("state",1);
            }else {
                dateMap.put("state",2);
            }
            dateList.add(dateMap);
        }
        return dateList;
    }

    /**
     * 列头ColumnsInfo对象初始化
     * @param columnId id
     * @param loadType 加载类型
     * @param columnDesc 名称
     * @param columnDesc 显示列宽
     * @param codeSetId 代码集编号，如果不是代码指标传入"0"
     * @return
     */
    private static ColumnsInfo getColumnsInfo(String columnId, int loadType, String columnDesc,String columnType,int columnWidth,String codeSetId){

        ColumnsInfo columnsInfo = new ColumnsInfo();
        try {
            columnsInfo.setLoadtype(loadType);
            columnsInfo.setColumnId(columnId);
            columnsInfo.setColumnDesc(columnDesc);
            columnsInfo.setColumnType(columnType);// 类型N|M|A|D
            columnsInfo.setColumnWidth(columnWidth);//显示列宽
            columnsInfo.setCodesetId(codeSetId);
            columnsInfo.setColumnLength(50);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("hrms/module/gz/gz_resource_zh_CN.js->gz.taxTableHomePage.msg.packageColunmsError",e);
        }
        return columnsInfo;
    }
}
