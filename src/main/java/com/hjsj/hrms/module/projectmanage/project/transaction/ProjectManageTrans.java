package com.hjsj.hrms.module.projectmanage.project.transaction;

import com.hjsj.hrms.module.projectmanage.project.businessobject.ProjectManageBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.utils.pagination.PaginationManager;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
/**
 * 项目管理页面查询交易类
 * 
 * @Title:        ProjectManageTrans.java
 * @Description:  查询项目管理页面显示数据
 * @Company:      hjsj     
 * @Create time:  2016-1-22 下午03:16:37
 * @author        chenxg
 * @version       1.0
 */
public class ProjectManageTrans extends IBusiness {

    private LinkedHashMap map = new LinkedHashMap();
    private ArrayList<ColumnsInfo> columnList = new ArrayList<ColumnsInfo>();
    @Override
    public void execute() throws GeneralException {
        try {
            ProjectManageBo bo = new ProjectManageBo(this.userView, this.frameconn);
            String b0110 = bo.getUnitIdByBusi();
            if((StringUtils.isEmpty(b0110) || b0110.length() < 3 || "UM`".equalsIgnoreCase(b0110) || "@K`".equalsIgnoreCase(b0110))
                    && !bo.isResponsible())
                throw new Exception("您没有项目工时的管理范围权限！请联系管理员。");

            // 栏目设置功能
            String submoduleid = "projectmanage_0001"; // 栏目设置区分
            String p1119 = (String) this.getFormHM().get("itemId");
            if (StringUtils.isNotEmpty(p1119))
                p1119 = PubFunc.decrypt(p1119);
            
            String deleteWhere = (String) this.getFormHM().get("deleteWhere");
            
            if("1".equalsIgnoreCase(deleteWhere) && StringUtils.isEmpty(p1119))
                this.userView.getHm().remove("projectWhere");

            this.getFormHM().put("dataJson", SafeCode.encode(""));
            String flag = (String) this.getFormHM().get("flag");
            int limit = 20;
            if (StringUtils.isEmpty(flag)) {
                this.userView.getHm().remove("projectWhere");
                 // 恢复默认方案
                 boolean recordShare = bo.hasShareScheme(submoduleid);// 公有记录
                 if(recordShare){// 公有
                     bo.schemeSetting(submoduleid);//公有方案
                 }else{
                     bo.schemeSettingDefalt(submoduleid);//默认方案
                 }
                 
                this.getFormHM().put("dataModel", SafeCode.encode(bo.getDataModel()));
                this.getFormHM().put("panelColumns", SafeCode.encode(bo.getPanelColumns(submoduleid)));
                this.getFormHM().put("projectStage", SafeCode.encode(bo.getItems()));
                this.getFormHM().put("buttons", SafeCode.encode(bo.getButtons()));
                this.getFormHM().put("fieldsArray", SafeCode.encode(bo.getFieldsArray(submoduleid)));
                this.getFormHM().put("scheme", SafeCode.encode(getScheme()));
                this.getFormHM().put("pageRows", SafeCode.encode(bo.getPageRows(submoduleid)));
            } else if ("1".equals(flag)){
                int page = Integer.parseInt((String) this.getFormHM().get("page"));
                limit = Integer.parseInt((String) this.getFormHM().get("limit"));
                String p1101 = (String) this.getFormHM().get("p1101");
                this.getFormHM().remove("p1101");
                if(StringUtils.isNotEmpty(p1101))
                    p1101 = PubFunc.decrypt(p1101);
                
                String filterParam = (String) this.getFormHM().get("filterParam");
                String filterWhere = bo.createFilterSql(filterParam);
                String where = (String) this.userView.getHm().get("projectWhere");
                if(StringUtils.isEmpty(p1101))
                    getData(page, limit, submoduleid, p1119, where, filterWhere);
                else
                    getChildren(p1101, submoduleid);
                
            } else if("2".equals(flag)){
                this.getFormHM().put("panelColumns", SafeCode.encode(bo.getPanelColumns(submoduleid)));
                this.getFormHM().put("dataModel", SafeCode.encode(bo.getDataModel()));
                this.getFormHM().put("pageRows", SafeCode.encode(bo.getPageRows(submoduleid)));
                this.getFormHM().put("fieldsArray", SafeCode.encode(bo.getFieldsArray(submoduleid)));
            }
            
            getPanelColumnsSetting();
            TableDataConfigCache config = new TableDataConfigCache();
            config.setTableColumns(this.columnList);
            config.setColumnMap(this.map);
            config.setPageSize(limit);
            if(this.map != null && this.map.size() > 0)
                this.userView.getHm().put(submoduleid, config);
                
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }
    /**
     * 是否加载栏目设置
     * @return scheme 显示栏目设置的html代码
     */
    private String getScheme() {
        String showPublicPlan = "0";
        String scheme = "";
        if (this.userView.hasTheFunction("3900100")) {
            if(this.userView.hasTheFunction("390010001"))
                showPublicPlan = "1";
            
            scheme = "<a href='javascript:void(0);' onclick='projectManage.schemeSetting(\"" + showPublicPlan + "\");'>" +
            		"<img src='/components/tableFactory/tableGrid-theme/images/Settings.png' title='栏目设置'/></a>";
        }
        
        return scheme;
    }
    
    /**
     * 分页获取数据
     * 
     * @param page
     *            第几页
     * @param limit
     *            每页有多少条数据
     * @param submoduleid
     *            栏目设置编号
     * @param p1119
     *            项目阶段
     * @param where
     *            查询条件
     */
    private void getData(int page, int limit, String submoduleid, String p1119, String where, String filterWhere) {
        try {
            int totalCount = 0;
            ProjectManageBo bo = new ProjectManageBo(this.userView, this.frameconn);
            ArrayList dataList = new ArrayList();
            PaginationManager paginationm = null;
            paginationm = new PaginationManager(bo.getDataSql(p1119, where, filterWhere), 
                    "", "", " order by p1101", bo.getFields(submoduleid), "");
            paginationm.setBAllMemo(true);
            paginationm.setPagerows(limit);
            totalCount = paginationm.getMaxrows();
            dataList = (ArrayList) paginationm.getPage(page);
            if (dataList.isEmpty() && page != 1) {
                dataList = (ArrayList) paginationm.getPage(page - 1);
            }

            ArrayList<String> fields = bo.getSumFields(submoduleid);
            ContentDAO dao = new ContentDAO(this.frameconn);
            for (int i = 0; i < dataList.size(); i++) {
                LazyDynaBean data = (LazyDynaBean) dataList.get(i);
                String p1101 = (String) data.get("p1101");
                data.set("p1101", PubFunc.encrypt(p1101));

                String p1201 = (String) data.get("p1201");
                if(StringUtils.isNotEmpty(p1201))
                    data.set("p1201", PubFunc.encrypt(p1201));
                
                getCodeName(data);
                bo.DateStyle(data, submoduleid);
                this.frowset = dao.search("SELECT 1 FROM P12 WHERE P1101=" + p1101);
                if(this.frowset.next())
                    data.set("leaf", false);
                else
                    data.set("leaf", true);
                
                data.set("id", "project" + PubFunc.encrypt(p1101));
                data.set("iconCls", "x-tree-project-iconCls");
            }
            
            if (fields != null && fields.size() > 0) {
                LazyDynaBean sumData = bo.getSumData(fields, where);
                if (sumData != null) {
                    sumData.set("leaf", true);
                    dataList.add(sumData);
                }
            }

            this.getFormHM().put("data", dataList);
            this.getFormHM().put("totalCount", totalCount);
            this.getFormHM().put("limit", limit);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 获取里程碑
     * 
     * @param p1101
     *            项目编号
     * @param submoduleid
     *            栏目设置编号           
     */
    private void getChildren(String p1101, String submoduleid) {
        ArrayList childrenList = new ArrayList();
        Pattern pattern = Pattern.compile("[0-9]*");

        try {
            if (StringUtils.isNotEmpty(p1101) && pattern.matcher(p1101).matches()) {
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT P1101,P1201,P1203 P1103,P1205 P1105,P1207 P1107,P1209 P1109,P1211 P1111,P1213 P1113,");
                sql.append("P1215 P1115,P1217 P1117 FROM P12 WHERE P1101=");
                sql.append(p1101);
                sql.append(" ORDER BY P1201");
                ContentDAO dao = new ContentDAO(this.frameconn);
                childrenList = dao.searchDynaList(sql.toString());
                ProjectManageBo bo = new ProjectManageBo(this.userView, this.frameconn);
                for (int i = 0; i < childrenList.size(); i++) {
                    LazyDynaBean data = (LazyDynaBean) childrenList.get(i);
                    p1101 = (String) data.get("p1101");
                    data.set("p1101", PubFunc.encrypt(p1101));

                    String p1201 = (String) data.get("p1201");
                    if(StringUtils.isNotEmpty(p1201))
                        data.set("p1201", PubFunc.encrypt(p1201));
                    
                    getCodeName(data);
                    bo.DateStyle(data, submoduleid);
                    formatNumber(data);
                    data.set("leaf", true);
                    data.set("iconCls", "x-tree-landmark-iconCls");
                    data.set("id", "landmark" + PubFunc.encrypt(p1201));
                }
            }
            this.getFormHM().put("data", childrenList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把代码类转换为文字
     * 
     * @param data
     *            查询出的数据
     */
    private void getCodeName(LazyDynaBean data) {
        try {
            ArrayList<FieldItem> headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
            for (int m = 0; m < headList.size(); m++) {
                FieldItem fi = headList.get(m);
                if (!"p1121".equals(fi.getItemid()) && "0".equals(fi.getCodesetid()))
                    continue;
                
                String itemValue = (String) data.get(fi.getItemid());
                String value = "";

                if(StringUtils.isNotEmpty(itemValue) && itemValue.indexOf("`") > -1)
                    itemValue = itemValue.substring(0, itemValue.indexOf("`"));
                
                if ("p1121".equals(fi.getItemid()) || "UN".equals(fi.getCodesetid()) || "UM".equals(fi.getCodesetid())) {
                    value = AdminCode.getCodeName("UN", itemValue);
                    if (StringUtils.isEmpty(value))
                        value = AdminCode.getCodeName("UM", itemValue);
                } else
                    value = AdminCode.getCodeName(fi.getCodesetid(), itemValue);

                data.set(fi.getItemid(), value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 数值类型指标格式化
     * @param data 查询出的数据
     */
    private void formatNumber(LazyDynaBean data) {
        try {
            ArrayList<FieldItem> headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
            for (int m = 0; m < headList.size(); m++) {
                FieldItem fi = headList.get(m);
                if(!"N".equalsIgnoreCase(fi.getItemtype()) || "p1101".equalsIgnoreCase(fi.getItemid()))
                    continue;
                
                String value = (String) data.get(fi.getItemid());
                value = StringUtils.isEmpty(value) ? "0" : value;
                int decimal = fi.getDecimalwidth();
                data.set(fi.getItemid(), new BigDecimal(value).setScale(decimal, BigDecimal.ROUND_HALF_UP) + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取列基本信息
     */
    private void getPanelColumnsSetting() {
        try {
            boolean flag = false;
            String schemeItemIds = ",";
            ContentDAO dao = new ContentDAO(this.frameconn);
            String sql = "select * from t_sys_table_scheme"
                + " where submoduleid = 'projectmanage_0001' and is_share = 0 and username = '"
                + this.userView.getUserName()
                + "'";
            this.frecset = dao.search(sql);
            if (this.frecset.next())
                flag = true;
            
            if(!flag)
                sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme"
                    + " where submoduleid = 'projectmanage_0001' and is_share = '0') order by displayorder";
            else
                sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme "
                    + " where submoduleid = 'projectmanage_0001' and is_share = '1' and username = '"
                    + this.userView.getUserName() + "') order by displayorder";
                
            this.frowset = dao.search(sql);
            while (this.frowset.next()) {
                String columnId = this.frowset.getString("itemid");
                String desc = this.frowset.getString("itemdesc");
                FieldItem item = DataDictionary.getFieldItem(columnId, "P11");
                if(item == null||!"1".equals(item.getUseflag()))
                	continue;
                
                schemeItemIds += columnId + ",";
                if(StringUtils.isEmpty(desc))
                	desc = item.getItemdesc();
                
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(columnId);
                info.setColumnDesc(desc);
                info.setColumnType(item.getItemtype());
                info.setLoadtype(Integer.parseInt(this.frowset.getString("is_display")));
                info.setColumnWidth(this.frowset.getInt("displaywidth"));
                boolean is_fromdict = false;
                if("1".equals(this.frowset.getString("is_fromdict"))){
                    is_fromdict = true;
                }
                info.setFromDict(is_fromdict);
                info.setSortable(false);
                this.map.put(columnId, info);
                this.columnList.add(info);
            }
            
            
            ArrayList<FieldItem> fieldItemList = DataDictionary.getFieldList("P11", Constant.USED_FIELD_SET);
            for(int i = 0; i < fieldItemList.size(); i++) {
                FieldItem fi = fieldItemList.get(i);
                String itemId = fi.getFieldsetid();
                if(schemeItemIds.indexOf("," + itemId + ",") > -1 || "p1101".equalsIgnoreCase(fi.getItemid()))
                    continue;
                    
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(fi.getItemid());
                info.setColumnDesc(fi.getItemdesc());
                info.setColumnType(fi.getItemtype());
                info.setLoadtype(1);
                info.setColumnWidth(100);
                info.setFromDict(false);
                info.setSortable(false);
                this.map.put(fi.getItemid(), info);
                this.columnList.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
