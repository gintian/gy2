package com.hjsj.hrms.module.projectmanage.project.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class ProjectManageBo {

    private UserView user;
    private Connection conn;
    private final String lockedFieldStr = "/p1101/p1103/p1201/"; // 默认锁定列

    public ProjectManageBo(UserView userview, Connection conn) {
        this.user = userview;
        this.conn = conn;
    }

    /**
     * 获取datemodel
     * 
     * @return
     */
    public String getDataModel() {
        StringBuffer model = new StringBuffer("[");
        ArrayList<FieldItem> fieldItemlist = DataDictionary.getFieldList("P11", Constant.USED_FIELD_SET);
        for (int i = 0; i < fieldItemlist.size(); i++) {
            FieldItem fi = (FieldItem) fieldItemlist.get(i);
            model.append("{name:'" + fi.getItemid() + "',type:'string'},");
        }

        model.append("{name:'p1201',type:'string'},");
        
        if (model.toString().endsWith(","))
            model.setLength(model.length() - 1);

        model.append("]");

        return model.toString();

    }

    /**
     * 获取用户权限
     * 
     * @return
     */
    public String getUnitIdByBusi() {
        // 1、业务用户：先取业务操作单位->操作单位->管理范围
        // 2、自助用户：先取关联的业务用户的（业务操作单位->操作单位）->自身的业务操作单位->管理范围
        String b0110 = this.user.getUnitIdByBusi("4");
        if(b0110.length() > 3 && !"UN`".equals(b0110))
            b0110 = PubFunc.getHighOrgDept(b0110.replaceAll("`", ","));
        
        return b0110.replaceAll(",", "`");

    }

    /**
     * 获取项目的json格式的数据
     * 
     * @param p1119
     *            项目阶段
     * @param where
     *            输入的查询条件
     * @return
     */
    public String[] getFields(String submoduleid) {
        ArrayList tableDataList = new ArrayList();
        ArrayList<FieldItem> headList = new ArrayList<FieldItem>();
        // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            headList = getHeadListSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            headList = getHeadListSetting(true, submoduleid);
        } else {
            headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        }

        String[] fields = new String[headList.size()];
        for(int i = 0; i < headList.size(); i++){
            FieldItem fi = headList.get(i);
            fields[i] = fi.getItemid();
        }
        return fields;
    }

    /**
     * 获取需要显示的数据
     * 
     * @param P1119
     *            项目阶段
     * @param where
     *            查询条件
     * @return
     */
    public String getDataSql(String p1119, String where, String filterWhere) {
        StringBuffer sql = new StringBuffer();
        try {
            String items = getItemids("projectmanage_0001");
            sql.append("SELECT " + items + " '' AS P1201 FROM P11");
            sql.append(" WHERE 1=1");
            if (StringUtils.isNotEmpty(p1119))
                sql.append(" AND p1119 like '" + p1119 + "%'");
            
            if (StringUtils.isNotEmpty(where))
                sql.append(" AND (" + where + ")");

            String b0110 = getWhere();
            if (StringUtils.isNotEmpty(b0110))
                sql.append(" AND (" + b0110 + ")");
            
            if (StringUtils.isNotEmpty(filterWhere))
                sql.append(filterWhere);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql.toString();
    }

    /**
     * 以用户权限拼接sql条件语句
     */
    public String getWhere() {
        StringBuffer where = new StringBuffer();
        if (this.user.isSuper_admin())
            return "";

        String b0110 = getUnitIdByBusi();
        if (b0110 != null && b0110.length() > 2 && b0110.indexOf("UN`") == -1) {
            String[] t = b0110.split("`");
            for (int i = 0; i < t.length; i++) {
                if (t != null && t[i].length() > 2) {
                    String tt = t[i].substring(2);
                    if (tt == null || tt.length() < 1)
                        continue;
                    
                    where.append(" P1121 LIKE '" + tt + "%' OR");
                }
            }
            
            where.append(" P1121='HJSJ' OR P1121 IS NULL OR P1121=''");
        } else if (b0110 == null || b0110.length() < 3)
            where.append(" 1=2");
        else if (b0110.indexOf("UN`") != -1)
            where.append(" 1=1");
        
        if (StringUtils.isNotEmpty(this.user.getDbname())
                && StringUtils.isNotEmpty(this.user.getA0100())) {
            if (!StringUtils.isEmpty(where.toString()))
                where.append(" OR");

            where.append(" P1101 IN (SELECT P1101 FROM P13 WHERE");
            String guidkey = getGuidkey(this.user.getDbname(), this.user.getA0100());
            where.append(" GUIDKEY='" + guidkey + "'");
            where.append(" AND P1311='01')");
        }
        return where.toString();
    }

    /**
     * 获取列头
     * 
     * @param ishare
     *            是否有私有方案 =false: 有私有方案 || =ture：有公有方案
     * @return
     */
    private ArrayList<FieldItem> getHeadListSetting(boolean ishare, String submoduleid) {
        ArrayList<FieldItem> list = new ArrayList<FieldItem>();
        list.add(DataDictionary.getFieldItem("p1101", "p11"));
        ContentDAO dao = new ContentDAO(this.conn);
        String strsql = "";
        if (!ishare) {
            strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '0' and username = '"
                    + this.user.getUserName() + "') and is_display = '1' order by displayorder";
        } else {
            strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '1') and is_display = '1' order by displayorder";
        }
        RowSet rset = null;
        try {
            rset = dao.search(strsql);
            while (rset.next()) {
                if("p1101".equals(rset.getString("itemid")) || "0".equals(rset.getString("is_display")))
                    continue;
                
                FieldItem fi = DataDictionary.getFieldItem(rset.getString("itemid"), "p11");
                if(fi == null||!"1".equals(fi.getUseflag()))
                	continue;
                list.add(fi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }

        return list;
    }

    /**
     * 查看当前用户名在设置方案中是否存在私有方案
     * 
     * @param submoduleid
     *            栏目设置的编号
     * @return
     */
    public boolean hasPrivateScheme(String submoduleid) {
        boolean flag = false;
        ContentDAO dao = new ContentDAO(this.conn);
        // 是否存在私有记录
        String sqlForPrivate = "select * from t_sys_table_scheme where submoduleid = '"
                + submoduleid + "' and is_share = 0 and username = '" + this.user.getUserName()
                + "'";
        RowSet rset = null;
        try {
            rset = dao.search(sqlForPrivate);
            if (rset.next()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }

        return flag;
    }

    /**
     * 栏目设置是否有公有方案
     * 
     * @param submoduleid
     *            栏目设置编号
     * @return
     */
    public boolean hasShareScheme(String submoduleid) {
        boolean flag = false;
        ContentDAO dao = new ContentDAO(this.conn);
        // 是否存在公有记录
        String sqlForShare = "select * from t_sys_table_scheme where submoduleid = '" + submoduleid
                + "' and is_share = 1";
        RowSet rset = null;
        try {
            rset = dao.search(sqlForShare);
            if (rset.next()) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }

        return flag;
    }

    /**
     * 获得EXT表格的列描述数组
     * 
     * @return
     */
    public String getPanelColumns(String submoduleid) {
        // 栏目设置是否有保存过的私有记录、公有记录
        String column_str = "";
        // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            column_str = getPanelColumnsSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            column_str = getPanelColumnsSetting(true, submoduleid);
        } else {
            column_str = getPanelColumnsDefalt();
        }

        return column_str;

    }

    /**
     * 获取列基本信息
     * 
     * @param privateOrShare
     *            是否公有方案
     * @param submoduleid
     *            栏目设置id
     * @return
     */
    private String getPanelColumnsSetting(boolean privateOrShare, String submoduleid) {
        StringBuffer column_str = new StringBuffer("[");
        String sql = "";
        if (!privateOrShare)
            sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '0' and username = '"
                    + this.user.getUserName() + "') and is_display = '1' order by displayorder";
        else
            sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '1') and is_display = '1' order by displayorder";

        RowSet rset = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rset = dao.search(sql);
            while (rset.next()) {
                String alignStr = "";
                if ("1".equals(rset.getString("align"))) {// 居左
                    alignStr = ",align:'left'";
                } else if ("2".equals(rset.getString("align"))) {// 居中
                    alignStr = ",align:'center'";
                } else if ("3".equals(rset.getString("align"))) {// 居右
                    alignStr = ",align:'right'";
                }
                
                FieldItem item = DataDictionary.getFieldItem(rset.getString("itemid"), "p11");
                if(item==null||!"1".equals(item.getUseflag()))
                	continue;
                String displaydesc = rset.getString("displaydesc");
                if(StringUtils.isEmpty(displaydesc))
                    displaydesc = item.getItemdesc();
                
                column_str.append("{text: '" + displaydesc + "',dataIndex: '"
                        + item.getItemid().toLowerCase() + "', sortable: true" + alignStr);
                column_str.append(",tooltip:'" + displaydesc + "',tooltipType:'title'");
                // 默认锁定列
                if ("1".equals(rset.getString("is_lock"))) 
                    column_str.append(",locked: true");
                
//                if ("1".equals(rset.getString("is_sum"))) {
//                    column_str.append(",summaryType: 'sum'");
//                }
                
                // 任务名称,出现展开图标,添加链接
                if ("p1103".equalsIgnoreCase(item.getItemid())) {
                    column_str.append(",xtype:'treecolumn',menuDisabled:false");
                    column_str.append(" ,columnType:'A',editor: 'textfield'");
                    column_str.append(",iconCls: 'x-tree-iconCls'");
                    column_str.append(",renderer:projectManage.addLinkToEditProjectManagePage");
                    column_str.append(",width: " + rset.getString("displaywidth") + "},");
                } else if (!"".equals(item.getCodesetid()) && !"0".equals(item.getCodesetid())) {// 代码型
                    column_str.append(",xtype:'treegridcolumn',operationData:'',columnType:'A',codesetid:'" + item.getCodesetid() + "'");
                    column_str.append(",width:" + rset.getString("displaywidth") + ",editor: {xtype:'codecomboxfield',codesetid:'"
                            + item.getCodesetid() + "',config:{maxPickerWidth:50}}");
                    column_str.append(",filterable:true,menuDisabled:false},");
                } else {
                    if ("M".equalsIgnoreCase(item.getItemtype())) {
                        column_str.append(",xtype:'treegridcolumn',operationData:'',columnType:'M'");
                        column_str.append(",filterable:true,renderer:projectManage.showRemarksData,editor: 'bigtextfield'");
                        column_str.append(",width:" + rset.getString("displaywidth") + ",filterable:true,menuDisabled:false},");
                    } else if ("N".equalsIgnoreCase(item.getItemtype())) {
                        column_str.append(",xtype:'treegridcolumn',operationData:'',columnType:'N'");
                        if ("p1113".equalsIgnoreCase(item.getItemid()) || "p1115".equalsIgnoreCase(item.getItemid())) {
                            column_str.append(",renderer:function(value,c,record){");
                            column_str.append("if(record.data.p1101 == '')return value;");
                            
                            column_str.append("var p1201 = record.data.p1201;if( p1201 != ''){");
                            if(this.user.hasTheFunction("39003")){
                                column_str.append("return \"<a href='###' onclick=projectManage.menHoursSumPage('\"+record.data.p1101+\"',");
                                column_str.append("'\"+record.data.p1103+\"','\"+record.data.p1201+\"')>\"+value+\"</a>\";");
                            }else
                                column_str.append("return value;");
                            
                            column_str.append("}else{");
                            if(this.user.hasTheFunction("39002")){
                                column_str.append("return \"<a href='###' onclick=projectManage.menHoursDetailPage('\"+");
                                column_str.append("record.data.p1101+\"','\"+record.data.p1103+\"')>\"+value+\"</a>\";");
                            }else
                                column_str.append("return value;");
                            
                            column_str.append("}},editor: 'textfield'");
                        }
                        
                        column_str.append(",width:" + rset.getString("displaywidth") + ",filterable:true,menuDisabled:false,align: 'right'},");
                    } else {
                        if("p1121".equals(item.getItemid())) 
                            column_str.append(",codesetid:'UM'");
                        
                        column_str.append(",xtype:'treegridcolumn',operationData:'',columnType:'" + item.getItemtype() + "',editor: 'bigtextfield'");
                        column_str.append(",width:" + rset.getString("displaywidth") + ",filterable:true,menuDisabled:false},");
                    } 
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }
        
        if(column_str.toString().endsWith(","))
            column_str.setLength(column_str.length() - 1);
        
        column_str.append("]");
        return column_str.toString();
    }

    /**
     * 获取列信息
     * 
     * @return
     */

    private String getPanelColumnsDefalt() {
        StringBuffer column_str = new StringBuffer("[");
        String lock_str = ",locked: true";

        ArrayList<FieldItem> headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        FieldItem p1201 = DataDictionary.getFieldItem("p1201", "p12");
        headList.add(p1201);
        FieldItem item = null;
        String text = "";
        for (int i = 0; i < headList.size(); i++) {
            item = (FieldItem) headList.get(i);
            if ("0".equals(item.getState()))
                continue;

            column_str.append("{text: '" + item.getItemdesc() + "',dataIndex: '"
                    + item.getItemid().toLowerCase() + "', sortable: false,width:"
                    + item.getFormula()); // item.getFormula()放的是列的默认宽度，
            column_str.append(",tooltip:'" + item.getItemdesc() + "',tooltipType:'title'");
            if (lockedFieldStr.indexOf("/" + item.getItemid().toLowerCase() + "/") != -1) // 默认锁定列
                column_str.append(lock_str);
            
            if ("p1103".equalsIgnoreCase(item.getItemid())) // 任务名称,出现展开图标,添加链接
            {
                column_str.append(",xtype:'treecolumn',menuDisabled:false");
                column_str.append(" ,editor: 'textfield'");
                column_str.append(",iconCls: 'x-tree-iconCls'");
                column_str.append(",renderer:projectManage.addLinkToEditProjectManagePage");
                column_str.append(",width: 200},");
            } else {
                if ("M".equalsIgnoreCase(item.getItemtype())) {
                    column_str.append(",xtype:'treegridcolumn',operationData:'',columnType:'M'");
                    column_str.append(",renderer:projectManage.showRemarksData,editor: 'bigtextfield'");
                    column_str.append(",width: 150,filterable:true,menuDisabled:false},");
                } else if ("N".equalsIgnoreCase(item.getItemtype())) {
                    column_str.append(",xtype:'treegridcolumn',width: 100,operationData:'',columnType:'N'");
                    if ("p1113".equalsIgnoreCase(item.getItemid()) || "p1115".equalsIgnoreCase(item.getItemid())){
                        column_str.append(",renderer:function(value,c,record){");
                        column_str.append("if(record.data.p1101 == '')return value;");
                        
                        column_str.append("var p1201 = record.data.p1201;if( p1201 != ''){");
                        if (this.user.hasTheFunction("39003")) {
                            column_str.append("return \"<a href='###' onclick=projectManage.menHoursSumPage('\"+record.data.p1101+\"',");
                            column_str.append("'\"+record.data.p1103+\"','\"+record.data.p1201+\"')>\"+value+\"</a>\";");
                        } else
                            column_str.append("return value;");
                        
                        column_str.append("}else{");
                        if (this.user.hasTheFunction("39002")) {
                            column_str.append("return \"<a href='###' onclick=projectManage.menHoursDetailPage('\"+");
                            column_str.append("record.data.p1101+\"','\"+record.data.p1103+\"')>\"+value+\"</a>\";");
                        } else
                            column_str.append("return value;");
                        
                        column_str.append("}},editor: 'textfield'");
                    }
                    
                    column_str.append(",filterable:true,menuDisabled:false,align: 'right'},");
                } else {
                    if("p1121".equals(item.getItemid())) 
                        column_str.append(",codesetid:'UM'");
                    else
                        column_str.append(",codesetid:'" + item.getCodesetid() + "'");
                    
                    column_str.append(",xtype:'treegridcolumn',width: 100,operationData:'',columnType:'" + item.getItemtype() + "'");
                    column_str.append(",editor: 'bigtextfield'");
                    column_str.append(",filterable:true,menuDisabled:false},");
                }
            }

        }
        
        if(column_str.toString().endsWith(","))
            column_str.setLength(column_str.length() - 1);
        
        column_str.append("]");
        return column_str.toString();
    }

    /**
     * 获取项目下的里程碑和非里程碑
     * 
     * @param p1101
     *            项目ID
     * @return
     */
    private ArrayList getChildrenList(String p1101) {
        ArrayList childrenList = new ArrayList();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (StringUtils.isEmpty(p1101) || !pattern.matcher(p1101).matches())
            return childrenList;

        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT P1101,P1201,P1203 P1103,P1205 P1105,P1207 P1107,P1209 P1109,P1211 P1111,P1213 P1113,P1215 P1115,P1217 P1117");
            sql.append(" FROM P12 WHERE P1101=");
            sql.append(p1101);
            sql.append(" ORDER BY P1201");
            ContentDAO dao = new ContentDAO(conn);
            childrenList = dao.searchDynaList(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childrenList;
    }

    /**
     * 生成项目管理页面的按钮
     * 
     * @return
     */
    public String getButtons() {
        StringBuffer buttonbuff = new StringBuffer("[");
        if (this.user.hasTheFunction("3900101")) {
            buttonbuff.append("{'xtype': 'button',text: '输出',");
            buttonbuff.append("listeners:{'click':function(){");
            buttonbuff.append("projectManage.exportExcle();}}},");
            buttonbuff.append("'-',");
        }

        if (this.user.hasTheFunction("3900102")) {
            buttonbuff.append("{'xtype': 'button','text': '新建项目',");
            buttonbuff.append("listeners:{'click':function(){var map = new HashMap();");
            buttonbuff.append("map.put(\"type\", \"add\");");
            buttonbuff.append("Ext.require('ProjectManageTemplateUL.projectmanageAdd', function(){");
            buttonbuff.append("Ext.create('ProjectManageTemplateUL.projectmanageAdd', map);");
            buttonbuff.append("});}}},");
        }

        if(this.user.hasTheFunction("3900105")){
            buttonbuff.append("{'xtype': 'button','text': '新增里程碑',");
            buttonbuff.append("listeners:{'click':function(){projectManage.addLandMark();}}},");
        }

        if(this.user.hasTheFunction("3900104")){
            buttonbuff.append("{'xtype': 'button',text: '删除',");
            buttonbuff.append("listeners:{'click':function(){projectManage.deleteProjectOrlandMark();}}},");
            buttonbuff.append("'-',");
        }

        if (this.user.hasTheFunction("3900108")) {
            buttonbuff.append("{'xtype': 'button',text: '工时统计',");
            buttonbuff.append("listeners:{'click':function(){projectManage.sumHours();}}},");
        }

        if (this.user.hasTheFunction("39004")) {
            buttonbuff.append("{'xtype': 'button',text: '员工月工时报表',");
            buttonbuff.append("listeners:{'click':function(){");
            buttonbuff.append("Ext.require('ManProjectHoursUL.ManProjectHours', function(){");
            buttonbuff.append("Ext.create('ManProjectHoursUL.ManProjectHours',{});");
            buttonbuff.append("});}}},");
        }

        if (buttonbuff.toString().endsWith(","))
            buttonbuff.setLength(buttonbuff.length() - 1);

        buttonbuff.append("]");

        return buttonbuff.toString();
    }
    /**
     * 获取查询方案需要显示的指标
     * @return
     */
    public String getFieldsArray(String submoduleid) {
        StringBuffer fieldsArray = new StringBuffer("[");
        ArrayList<FieldItem> fieldList = new ArrayList<FieldItem>();
     // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            fieldList = getHeadListSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            fieldList = getHeadListSetting(true, submoduleid);
        } else {
            fieldList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        }
        
        for(int i=0; i < fieldList.size(); i++) {
            FieldItem item = fieldList.get(i);
            if ("0".equals(item.getState()) || "M".equalsIgnoreCase(item.getItemtype()))
                continue;
            
            fieldsArray.append("{'type':'"+item.getItemtype()+"',");
            fieldsArray.append("'itemid':'"+item.getItemid()+"',");
            fieldsArray.append("'itemdesc':'"+item.getItemdesc()+"',");
            
            if ("p1121".equalsIgnoreCase(item.getItemid()))  
                fieldsArray.append("'codesetid':'UM',");
            else
                fieldsArray.append("'codesetid':'"+item.getCodesetid()+"',");
            
            String formatStr = item.getFormat();
            if ("D".equals(item.getItemtype()) && StringUtils.isEmpty(formatStr))
                formatStr = "Y-m-d";
            
            fieldsArray.append("'format':'"+ formatStr +"',");
            if("p1121".equals(item.getItemid())) {
                fieldsArray.append("'ctrltype':'3',");
                fieldsArray.append("'codesetValid':false,");
                fieldsArray.append("'nmodule':'4'},");
            } else if (!"0".equals(item.getCodesetid())) {
                fieldsArray.append("'ctrltype':'0'},");
            } else {
                fieldsArray.append("},");
            }
            
        }
        
        if(fieldsArray.toString().endsWith(","))
            fieldsArray.setLength(fieldsArray.length()-1);
        
        fieldsArray.append("]");
        return fieldsArray.toString();
    }

    /**
     * 生成查询方案
     * 
     * @return
     */
    public String getItems() {
        RowSet rs = null;
        StringBuffer items = new StringBuffer();
        try {
            int i = 0;
            items.append("[{\"xtype\":\"label\",\"text\": \"查询方案：\",},");
            items.append("{\"xtype\":\"label\",\"id\":\"label" + i + "\",\"html\":");
            items.append("\"<a href='###' onclick=\\\"projectManage.searchrProjectStage('label" + i + "','");
            items.append(PubFunc.encrypt(""));
            items.append("')\\\">全部</a>\",");
            items.append("\"style\":\"margin-left:10px;margin-right:10px;\",");
            items.append("\"cls\":\"scheme-selected-cls\"},\"-\"");
            String sql = "SELECT CODEITEMID,CODEITEMDESC FROM CODEITEM WHERE CODESETID='80' AND CODEITEMID=PARENTID";
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            while (rs.next()) {
                i++;
                items.append(",{\"xtype\":\"label\",\"id\":\"label" + i + "\",\"html\":");
                items.append("\"<a href='###' onclick=\\\"projectManage.searchrProjectStage('label" + i + "','");
                items.append(PubFunc.encrypt(rs.getString("CODEITEMID")));
                items.append("')\\\">" + rs.getString("CODEITEMDESC") + "</a>\",");
                items.append("\"style\":\"margin-left:10px;\"}");
            }

            items.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return items.toString();
    }

    /**
     * 把公有方案放到栏目默认方案
     * 
     * @param submoduleid
     *            栏目设置id
     */
    public void schemeSetting(String submoduleid) {

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rset = null;
        try {
            ArrayList<ColumnsInfo> arr = new ArrayList<ColumnsInfo>();
            LinkedHashMap<String, ColumnsInfo> map = new LinkedHashMap<String, ColumnsInfo>();

            String strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id as id from t_sys_table_scheme "
                    + "where submoduleid = '" + submoduleid + "' and is_share = '1')  order by displayorder";
            rset = dao.search(strsql);

            while (rset.next()) {
                ColumnsInfo info = new ColumnsInfo();
                info.setColumnId(rset.getString("itemid"));
                
                String itemdesc = rset.getString("itemdesc");
                if(StringUtils.isEmpty(itemdesc)){
                    FieldItem fi = DataDictionary.getFieldItem(rset.getString("itemid"));
                    itemdesc = fi.getItemdesc();
                }
                
                info.setColumnDesc(itemdesc);
                info.setLoadtype(Integer.parseInt(rset.getString("is_display")));
                info.setColumnWidth(rset.getInt("displaywidth"));
                boolean is_fromdict = false;
                if ("1".equals(rset.getString("is_fromdict"))) {
                    is_fromdict = true;
                }
                info.setFromDict(is_fromdict);
                info.setSortable(false);
                map.put(rset.getString("itemid"), info);
                arr.add(info);
            }
            TableDataConfigCache config = new TableDataConfigCache();
            config.setTableColumns(arr);
            config.setColumnMap(map);
            Integer pagesize = new Integer(20);
            config.setPageSize(pagesize);

            this.user.getHm().put(submoduleid, config);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }
    }

    /**
     * 栏目设置默认设置
     * 
     * @param submoduleid
     *            栏目设置id
     * @throws GeneralException 
     */
    public void schemeSettingDefalt(String submoduleid) throws GeneralException {
        ArrayList<FieldItem> fieldList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        if(fieldList == null || fieldList.size() < 1)
            throw new GeneralException("", "无法获取项目数据！", "", "");

        ArrayList<ColumnsInfo> arr = new ArrayList<ColumnsInfo>();
            
        ColumnsInfo info = null;
        for (int i = 0; i < fieldList.size(); i++) {
            FieldItem fi = (FieldItem) fieldList.get(i);
            // 去除隐藏的指标
            if ("0".equals(fi.getState())) {
                continue;
            }

            info = new ColumnsInfo(fi);
            info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
            info.setSortable(false);
            info.setColumnType(fi.getItemtype());
            info.setColumnDesc(fi.getItemdesc());
            if ("P1101".equalsIgnoreCase(fi.getItemid()) || "P1103".equalsIgnoreCase(fi.getItemid())) {
                info.setLocked(true);
                info.setColumnWidth(200);
            } else if ("M".equals(fi.getItemtype()))
                info.setColumnWidth(150);
            
            arr.add(info);
        }

        TableDataConfigCache config = new TableDataConfigCache();
        config.setTableColumns(arr);
        Integer pagesize = new Integer(20);
        config.setPageSize(pagesize);
        this.user.getHm().put(submoduleid, config);
    }

    /**
     * 获取导出需要的表头数据
     * 
     * @param submoduleid
     *            栏目设置id
     * @return ArrayList<LazyDynaBean> 格式数据
     */
    public ArrayList<LazyDynaBean> getExcleHeadList(String submoduleid) {
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            headList = getExcleHeadListSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            headList = getExcleHeadListSetting(true, submoduleid);
        } else {
            LazyDynaBean abean = null;
            ArrayList<FieldItem> fieldItem = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
            for (int i = 0; i < fieldItem.size(); i++) {
                abean = new LazyDynaBean();
                FieldItem fi = fieldItem.get(i);
                String itemId = fi.getItemid();
                if ("p1101".equals(itemId) || "0".equals(fi.getState()))
                    continue;

                abean.set("itemid", itemId);
                abean.set("content", fi.getItemdesc());
                if ("p1121".equals(fi.getItemid()))
                    abean.set("codesetid", "UN");
                else
                    abean.set("codesetid", fi.getCodesetid());

                abean.set("colType", fi.getItemtype());
                abean.set("decwidth", fi.getDecimalwidth() + "");

                if ("p1103".equals(itemId))
                    abean.set("columnLocked", true);

                headList.add(abean);
            }
        }
        return headList;

    }

    /**
     * 获取导出excle的列头
     * 
     * @param ishare
     *            是否有私有方案 =false: 有私有方案 || =ture：有公有方案
     * @return
     */
    private ArrayList<LazyDynaBean> getExcleHeadListSetting(boolean ishare, String submoduleid) {
        ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
        String strsql = "";
        LazyDynaBean abean = null;
        if (!ishare) {
            strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '0' and username = '"
                    + this.user.getUserName() + "') and is_display = '1' order by displayorder";
        } else {
            strsql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '1') and is_display = '1' order by displayorder";
        }
        RowSet rset = null;
        HashMap headStyleMap = new HashMap();
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rset = dao.search(strsql);
            while (rset.next()) {
                abean = new LazyDynaBean();
                String itemId = rset.getString("itemid");
                if ("p1101".equals(itemId) || "0".equals(rset.getString("is_display")))
                    continue;

                FieldItem fi = DataDictionary.getFieldItem(itemId, "p11");
                String displaydesc = rset.getString("displaydesc");
                if(StringUtils.isEmpty(displaydesc))
                    displaydesc = fi.getItemdesc();
                
                abean.set("itemid", itemId);
                abean.set("content", displaydesc);
                if ("p1121".equals(fi.getItemid()))
                    abean.set("codesetid", "UN");
                else
                    abean.set("codesetid", fi.getCodesetid());

                abean.set("colType", fi.getItemtype());
                abean.set("decwidth", fi.getDecimalwidth() + "");

                if ("p1103".equals(itemId))
                    abean.set("columnLocked", true);

                list.add(abean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }

        return list;
    }

    /**
     * 获取导出数据需要的sql语句
     * 
     * @param p1119
     *            项目阶段
     * @param where
     *            查询框或查询方案的查询条件
     * @param submoduleid
     *            栏目设置id
     * @param projectIds
     *            项目编号
     * @param milestoneIds
     *            里程碑编号
     * @return
     */
    public String getExcleSql(String p1119, String where, String projectIds, String milestoneIds) {
        String Columns = "/p1103/p1105/p1107/p1109/p1111/p1113/p1115/p1117/";
        StringBuffer sql = new StringBuffer("SELECT * FROM");
        StringBuffer sqlP11 = new StringBuffer("SELECT");
        StringBuffer sqlP12 = new StringBuffer("SELECT");
        StringBuffer sqlwhere = new StringBuffer();
        try {
            ArrayList<FieldItem> headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);

            for (int i = 0; i < headList.size(); i++) {
                FieldItem fi = headList.get(i);
                sqlP11.append(" " + fi.getItemid() + ",");
                if("p1101".equals(fi.getItemid())) {
                    sqlP11.append(" 0 AS P1201,");
                    sqlP12.append(" "  + fi.getItemid() + ",");
                    sqlP12.append(" P1201,");
                } else if (Columns.indexOf("/" + fi.getItemid() + "/") != -1) {
                    if ("p1103".equals(fi.getItemid())){
                        if(Sql_switcher.searchDbServer() == Constant.ORACEL)
                            sqlP12.append(" concat('    ', " + fi.getItemid().replaceAll("p11", "p12") + ") "  + fi.getItemid() + ",");
                        else
                            sqlP12.append(" '    ' +" + fi.getItemid().replaceAll("p11", "p12") + " "  + fi.getItemid() + ",");
                    } else
                        sqlP12.append(" " + fi.getItemid().replaceAll("p11", "p12") + " " + fi.getItemid() + ",");
                } else  if ("D".equals(fi.getItemtype()))
                    sqlP12.append(" null " + fi.getItemid() + ",");
                else  if ("N".equals(fi.getItemtype()))
                    sqlP12.append(" 0 " + fi.getItemid() + ",");
                else
                    sqlP12.append(" '' " + fi.getItemid() + ",");
            }

            if (sqlP11.toString().endsWith(","))
                sqlP11.setLength(sqlP11.length() - 1);

            if (sqlP12.toString().endsWith(","))
                sqlP12.setLength(sqlP12.length() - 1);

            sqlwhere.append(" WHERE 1=1");
            if (StringUtils.isNotEmpty(projectIds))
                sqlwhere.append(" AND P1101 IN (" + projectIds + ")");
            else {
                if (StringUtils.isNotEmpty(p1119))
                    sqlwhere.append(" AND p1119 like '" + p1119 + "%'");
    
                if (StringUtils.isNotEmpty(where))
                    sqlwhere.append(" AND (" + where + ")");
    
                String b0110 = getWhere();
                if (StringUtils.isNotEmpty(b0110))
                    sqlwhere.append(" AND (" + b0110 + ")");
            }
            
            sql.append(" (");
            sql.append(sqlP11);
            sql.append(" FROM P11");
            sql.append(sqlwhere);
            sql.append(" UNION ALL");
            sql.append(" " + sqlP12);
            sql.append(" FROM P12");
            sql.append(" WHERE P1101 IN (");
            sql.append(" SELECT P1101 FROM P11");
            sql.append(sqlwhere);
            sql.append(")");
            if (StringUtils.isNotEmpty(milestoneIds))
                sql.append(" OR P1201 IN (" + milestoneIds + ")");
            
            sql.append(") P");
            sql.append(" ORDER BY P.P1101,P.P1201");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sql.toString();
    }

    /**
     * 获取每页显示的数据条数
     * 
     * @param submoduleid
     *            栏目设置的编号
     * @return
     */
    public String getPageRows(String submoduleid) {
        String pageRows = "20";
        String sql = "";
        RowSet rs = null;
        if (hasPrivateScheme(submoduleid))
            sql = "select rows_per_page from t_sys_table_scheme where submoduleid = '"
                    + submoduleid + "' and is_share = '0' and username = '"
                    + this.user.getUserName() + "'";
        else if (hasShareScheme(submoduleid))
            sql = "select rows_per_page from t_sys_table_scheme where submoduleid = '"
                    + submoduleid + "' and is_share = '1'";
        try {
            if (StringUtils.isNotEmpty(sql)) {
                ContentDAO dao = new ContentDAO(conn);
                rs = dao.search(sql);
                if (rs.next())
                    pageRows = rs.getString("rows_per_page");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageRows;
    }

    /**
     * 日期类型格式转换（将YYYY.MM.DD转换为YYYY-MM-DD）
     * 
     * @param bean
     *            显示的数据
     * @param submoduleid
     *            栏目设置编号
     */
    public void DateStyle(LazyDynaBean bean, String submoduleid) {
        ArrayList<FieldItem> headList = new ArrayList<FieldItem>();
        // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            headList = getHeadListSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            headList = getHeadListSetting(true, submoduleid);
        } else {
            headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        }

        for (int i = 0; i < headList.size(); i++) {
            FieldItem item = headList.get(i);
            if (!"D".equalsIgnoreCase(item.getItemtype()))
                continue;

            String dateValue = (String) bean.get(item.getItemid());
            if (StringUtils.isEmpty(dateValue))
                continue;

            bean.set(item.getItemid(), dateValue.replace(".", "-"));
        }
    }

    /**
     * 获取需要合计的指标
     * 
     * @param submoduleid
     *            栏目设置编号
     * @return ArrayList<String> 需要合计的指标名称
     */
    public ArrayList<String> getSumFields(String submoduleid) {
        ArrayList<String> fields = new ArrayList<String>();
        String sql = "";
        if (hasPrivateScheme(submoduleid))
            sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '0' and username = '"
                    + this.user.getUserName() + "') and is_display = '1' order by displayorder";
        else if (hasShareScheme(submoduleid))
            sql = "select * from t_sys_table_scheme_item where scheme_id = (select scheme_id from t_sys_table_scheme where submoduleid = '"
                    + submoduleid
                    + "' and is_share = '1') and is_display = '1' order by displayorder";

        if(StringUtils.isEmpty(sql))
            return fields;
        
        RowSet rset = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rset = dao.search(sql);
            while (rset.next()) {
                if ("1".equalsIgnoreCase(rset.getString("is_sum")))
                    fields.add(rset.getString("itemid"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rset);
        }

        return fields;
    }

    /**
     * 生成合计行
     * 
     * @param sumData
     *            合计行内的指标与值
     * @param data
     *            当前页显示的一行数据
     * @param fields
     *            需要合计的指标名称
     * @return LazyDynaBean 合计行
     */
    public LazyDynaBean getSumData(ArrayList<String> fields, String where) {
        LazyDynaBean sumData = null;
        if (fields == null || fields.size() < 1)
            return sumData;

        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT ");
            for (int i = 0; i < fields.size(); i++) {
                String field = fields.get(i);
                if (StringUtils.isEmpty(field))
                    continue;

                sql.append(" SUM(" + field + ") AS " + field + ",");
            }
            
            sql.setLength(sql.length() - 1);
            sql.append(" FROM P11 WHERE 1=1");
            if (StringUtils.isNotEmpty(where))
                sql.append(" AND (" + where + ")");

            String b0110 = getWhere();
            if (StringUtils.isNotEmpty(b0110))
                sql.append(" AND (" + b0110 + ")");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if(rs.next()) {
                sumData = new LazyDynaBean();
                for (int i = 0; i < fields.size(); i++) {
                    String field = fields.get(i);
                    if (StringUtils.isEmpty(field))
                        continue;

                    sumData.set(field, PubFunc.nullToStr(rs.getString(field)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sumData;

    }
    /**
     * 检查是否有负责人是当前用户的项目
     * @return =true：有||=false：没有
     */
    public boolean isResponsible(){
        boolean flag = false;
        RowSet rs = null;
        try{
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT P1101 FROM P13 WHERE");
            sql.append(" NBASE='" + this.user.getDbname() + "'");
            sql.append(" AND A0100='" + this.user.getA0100() + "'");
            sql.append(" AND P1311='01'");
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if(rs.next())
                flag = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return flag;
        
    }
    /**
     * 解析过滤的条件生成sql查询语句
     * @param filterParam 页面设置的过滤条件
     * @return
     */
    public String createFilterSql(String filterParam) {
        if (StringUtils.isEmpty(filterParam))
            return "";
        
        JSONObject json = JSONObject.fromObject(SafeCode.keyWord_reback(filterParam));
        String itemid = json.getString("field");
        String itemtype = json.getString("itemtype");
        JSONArray factor = json.getJSONArray("factor");
        String expr = json.getString("expr");
        // 如果为空或者没有数据，返回 原sql
        if (factor == null || factor.isEmpty())
            return "";

        StringBuffer filterWhere = new StringBuffer(" and (");
        String symbol;
        String value = "";

        if ("C".equals(itemtype)) {// C代码型指标
            expr = "or";
            for (int i = 0; i < factor.size(); i++) {
                String f = factor.getString(i);
                value = f.substring(f.indexOf("`") + 1);
                filterWhere.append(itemid + " like '" + value + "%' or ");
            }
        } else if ("D".equals(itemtype)) {// 时间类型
            String plan = json.getString("plan");
            for (int i = 0; i < factor.size(); i++) {
                String f = factor.getString(i);
                symbol = f.substring(0, f.indexOf("`"));
                value = f.substring(f.indexOf("`") + 1);
                if ("custom".equals(plan)) {
                    filterWhere.append(itemid + symbol + Sql_switcher.dateValue(value) + " " + expr + " ");
                    continue;
                }

                Calendar c = Calendar.getInstance();

                if ("nextMonth".equals(symbol)) 
                    filterWhere.append(Sql_switcher.month(itemid) + "=" + (c.get(Calendar.MONTH) + 2) + " " + expr + " ");
                else if ("thisMonth".equals(symbol)) 
                    filterWhere.append(Sql_switcher.month(itemid) + "=" + (c.get(Calendar.MONTH) + 1) + " " + expr + " ");
                else if ("lastMonth".equals(symbol)) 
                    filterWhere.append(Sql_switcher.month(itemid) + "=" + c.get(Calendar.MONTH) + " " + expr + " ");
                else if ("nextYear".equals(symbol)) 
                    filterWhere.append(Sql_switcher.year(itemid) + "=" + (c.get(Calendar.YEAR) + 1) + " " + expr + " ");
                else if ("thisYear".equals(symbol)) 
                    filterWhere.append(Sql_switcher.year(itemid) + "=" + c.get(Calendar.YEAR) + " " + expr + " ");
                else if ("lastYear".equals(symbol)) 
                    filterWhere.append(Sql_switcher.year(itemid) + "=" + (c.get(Calendar.YEAR) - 1) + " " + expr + " ");
                else {
                    int nextYear = -1;
                    int lastYear = -1;
                    String nextSeason = "";
                    String thisSeason = "";
                    String lastSeason = "";
                    if (c.get(Calendar.MONTH) < 3) {
                        thisSeason = "1,2,3";
                        lastYear = c.get(Calendar.YEAR) - 1;
                    } else if (c.get(Calendar.MONTH) < 6) {
                        nextSeason = "7,8,9";
                        thisSeason = "4,5,6";
                        lastSeason = "1,2,3";
                    } else if (c.get(Calendar.MONTH) < 9) {
                        nextSeason = "10,11,12";
                        thisSeason = "7,8,9";
                        lastSeason = "4,5,6";
                    } else {
                        thisSeason = "10,11,12";
                        nextYear = c.get(Calendar.YEAR) + 1;
                    }
                    
                    if ("nextSeason".equals(symbol)) {
                        if (nextYear > 0)
                            filterWhere.append(Sql_switcher.month(itemid) + " in (1,2,3) and "
                                  + Sql_switcher.year(itemid) + "=" + nextYear + " " + expr + " ");
                        else
                            filterWhere.append(Sql_switcher.month(itemid) + " in (" + nextSeason + ") " + expr + " ");
                        
                    } else if ("thisSeason".equals(symbol)) 
                        filterWhere.append(Sql_switcher.month(itemid) + " in (" + thisSeason + ") " + expr + " ");
                    else if ("lastSeason".equals(symbol)) {
                        if (lastYear > 0)
                            filterWhere.append(Sql_switcher.month(itemid) + " in (10,11,12) and "
                                + Sql_switcher.year(itemid) + "=" + lastYear + " " + expr + " ");
                        else
                            filterWhere.append(Sql_switcher.month(itemid) + " in (" + lastSeason + ") " + expr + " ");
                    }
                }
                break;
            }
        } else if ("N".equals(itemtype)) {// int型
            for (int i = 0; i < factor.size(); i++) {
                String f = factor.getString(i);
                symbol = f.substring(0, f.indexOf("`"));
                value = f.substring(f.indexOf("`") + 1);
                filterWhere.append(itemid + symbol + value + " " + expr + " ");
            }
        } else {// M(文本)型和A(字符)型
            for (int i = 0; i < factor.size(); i++) {
                String f = factor.getString(i);
                symbol = f.substring(0, f.indexOf("`"));
                try {
                    value = URLDecoder.decode(URLDecoder.decode(f.substring(f.indexOf("`") + 1), "UTF-8"), "UTF-8");
                    value = PubFunc.hireKeyWord_filter(value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                
                if ("sta".equals(symbol)) // 开头是
                    filterWhere.append(itemid + " like '" + value + "%' " + expr + " ");
                else if ("stano".equals(symbol)) // 开头不是
                    filterWhere.append(itemid + " not like '" + value + "%' " + expr + " ");
                else if ("end".equals(symbol)) // 结尾是
                    filterWhere.append(itemid + " like '%" + value + "' " + expr + " ");
                else if ("endno".equals(symbol)) // 结尾不是
                    filterWhere.append(itemid + " not like '%" + value + "' " + expr + " ");
                else if ("cont".equals(symbol)) // 包含
                    filterWhere.append(itemid + " like '%" + value + "%' " + expr + " ");
                else if ("contno".equals(symbol)) // 不包含
                    filterWhere.append(itemid + " not like '%" + value + "%' " + expr + " ");
                else {
                    if ("=".equals(symbol) && value.indexOf("？") + value.indexOf("＊") > -2) {
                        symbol = " like ";
                        value = value.replaceAll("？", "?");
                        value = value.replaceAll("＊", "%");
                    }
                    
                    if (value.length() == 0 && "=".equals(symbol)) {
                        filterWhere.append(" (" + Sql_switcher.sqlToChar(itemid) + symbol
                                + " '' or " + Sql_switcher.sqlToChar(itemid) + " is null ) " + expr + " ");
                    } else
                        filterWhere.append(Sql_switcher.sqlToChar(itemid) + symbol + " '" + value + "' " + expr + " ");
                }
            }
        }
        
        if ("or".equals(expr))
            filterWhere.append(" 1=2 ");
        else
            filterWhere.append(" 1=1 ");
        
        filterWhere.append(" )");
        return filterWhere.toString();
    }
    /**
     * 获取guidkey
     * @param nbase 人员库
     * @param a0100 人员编号
     * @return
     */
    public String getGuidkey(String nbase, String a0100) {
        String guidkey = "";
        if (StringUtils.isEmpty(nbase) || StringUtils.isEmpty(a0100))
            return guidkey;
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT GUIDKEY");
            sql.append(" FROM " + nbase + "A01");
            sql.append(" WHERE A0100='" + a0100 + "'");
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if(rs.next())
                guidkey = rs.getString("GUIDKEY");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        
        return guidkey;
    }
    
    /**
     * 获取p11表中需要显示的指标
     * 
     * @param submoduleid
     *            栏目设置编号
     */
    public String getItemids(String submoduleid) {
        StringBuffer items = new StringBuffer();
        ArrayList<FieldItem> headList = new ArrayList<FieldItem>();
        // 是否私有方案
        if (hasPrivateScheme(submoduleid)) {
            headList = getHeadListSetting(false, submoduleid);
        } else if (hasShareScheme(submoduleid)) {
            headList = getHeadListSetting(true, submoduleid);
        } else {
            headList = DataDictionary.getFieldList("p11", Constant.USED_FIELD_SET);
        }

        for (int i = 0; i < headList.size(); i++) {
            FieldItem item = headList.get(i);
            String itemid = item.getItemid();
            if (StringUtils.isEmpty(itemid))
                continue;
            
            items.append(itemid + ",");

        }
        
        return items.toString();
    }
}
