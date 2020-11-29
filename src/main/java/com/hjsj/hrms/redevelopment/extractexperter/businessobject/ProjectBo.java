package com.hjsj.hrms.redevelopment.extractexperter.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.businessobject.TableFactoryBO;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnConfig;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * @Title:        ProjectBo.java
 * @Description:  项目信息管理公用类
 * @Company:      hjsj     
 * @Create time:  2015-11-26 下午01:52:48
 * @author        chenxg
 * @version       1.0
 */
public class ProjectBo {

    Connection conn;
    UserView userview;

    public ProjectBo(Connection conn, UserView userview) {
        this.conn = conn;
        this.userview = userview;

    }

    /**
     * @Title: getColumnList
     * @Description: 查询列表表头信息
     * @param @return
     * @return ArrayList
     * @throws GeneralException
     */
    public ArrayList<ColumnsInfo> getColumnList() throws GeneralException {

        ArrayList list = new ArrayList();
        ArrayList columnList = new ArrayList();
        TableFactoryBO tableBo = new TableFactoryBO("re_project_00001", userview, conn);
        HashMap scheme = tableBo.getTableLayoutConfig();
        if (scheme != null) {
            Integer scheme_str = (Integer) scheme.get("schemeId");
            int schemeId = scheme_str.intValue();
            ArrayList columnConfigLst = tableBo.searchCombineColumnsConfigs(schemeId, null);
            list = columnConfigLst;
        } else {
            // 获取业务字典里所有相关列
            ArrayList<String> column = getColumn("n03");
            for (int i = 0; i < column.size(); i++) {
                String itemId = (String) column.get(i);
                list.add(itemId);
            }
        }

        try {

            String mergedesc = "";
            int mergedescIndex = 0;
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                String itemId = "";
                ColumnsInfo info = null;
                FieldItem item = null;
                if (scheme != null) {
                	ColumnConfig column = (ColumnConfig) list.get(i);
                	itemId = column.getItemid();
                    if (true) {
                        // 当前用户有自定义栏目设置时
                        item = DataDictionary.getFieldItem(itemId, "n03");
                        if (item != null) {
                            info = new ColumnsInfo(item);
                            info.setColumnDesc(column.getDisplaydesc());
                            if (column.getIs_lock() == "1") {
                                info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
                            } else if (column.getIs_lock() == "0") {
                                info.setLoadtype(ColumnsInfo.LOADTYPE_NOTLOAD);
                            }
                            if(!"n0311".equals(itemId) && !"n0312".equals(itemId))
                            {
                           	info.setEditableValidFunc("true");
                            }
                           
                            info.setLoadtype(Integer.parseInt(("".equals((String) column.getIs_lock()) || (String) column.getIs_lock() == null) ? "0" : (String) column.getIs_lock()));
                            info.setColumnWidth(column.getDisplaywidth());
                            info.setTextAlign(column.getAlign() + "");
                            String order = "";
                            if ("1".equalsIgnoreCase(column.getIs_order())) {
                                order = "true";
                            } else {
                                order = "false";
                            }

                            info.setSortable(Boolean.parseBoolean(order));
                            if (column.getIs_sum() == "1") {
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
                            } else if (column.getIs_sum() == "2") {
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_AVERAGE);
                            } else if (column.getIs_sum() == "3") {
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MIN);
                            } else if (column.getIs_sum() == "4") {
                                info.setSummaryType(ColumnsInfo.SUMMARYTYPE_MAX);
                            }

                            if ("0".equalsIgnoreCase(column.getIs_fromdict())) {
                                info.setFromDict(Boolean.parseBoolean("false"));
                            }

                            if (column.getMergedesc() != null && column.getMergedesc().length() > 0) {
                                if (mergedesc.equalsIgnoreCase(column.getMergedesc()) && mergedescIndex == i - 1) {
                                    ArrayList tableheadlist = new ArrayList();
                                    tableheadlist.add(columnList.get(mergedescIndex - num));
                                    tableheadlist.add(info);
                                    HashMap topHead = new HashMap();
                                    topHead.put("text", mergedesc);
                                    topHead.put("items", tableheadlist);
                                    columnList.remove(mergedescIndex - num);// 当合并时移除最后一列
                                    columnList.add(topHead);
                                    num += 1;
                                    continue;
                                } else {
                                    mergedesc = column.getMergedesc();
                                    mergedescIndex = i;
                                }
                            }
                        }

                    } else {
                        item = DataDictionary.getFieldItem(itemId);
                        info = new ColumnsInfo(item);
                        info.setLocked(true);
                    }

                } else {
                    itemId = (String) list.get(i);
                    item = DataDictionary.getFieldItem(itemId, "n03");
                    info = new ColumnsInfo(item);
                }
             
                columnList.add(info);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }

        return columnList;
    }

    /**
     * 项目信息管理主界面拼接buttonList的方法
     * 
     * @return
     * @throws GeneralException
     */
    public ArrayList getButtonList() throws GeneralException {
        ArrayList buttonList = new ArrayList();
        try {
            // 新增按钮
            buttonList.add(new ButtonInfo("创建项目", "projectInfor.addProject"));
            // 删除按钮
            buttonList.add(new ButtonInfo("删除项目", "projectInfor.deleteProject"));
            // 保存按钮
            buttonList.add(new ButtonInfo("保存", "projectInfor.saveProject"));
            // 抽选专家按钮
            buttonList.add(new ButtonInfo("抽选专家", "projectInfor.selectedExperts"));
            // 短信提醒按钮
          //  buttonList.add(new ButtonInfo("短信提醒", "projectInfor.sendMessage"));

        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return buttonList;
    }

    /**
     * 根据业务字典获取职位所有参数列
     * 
     * @return
     */
    public ArrayList<String> getColumn(String tablename) {
        ArrayList<String> columnList = new ArrayList<String>();
        try {
            ArrayList fieldList = DataDictionary.getFieldList(tablename, Constant.USED_FIELD_SET);
            for (int i = 0; i < fieldList.size(); i++) {
                FieldItem item = (FieldItem) fieldList.get(i);
                if("0".equals(item.getState()))
                	continue;
                
                String colunmName = item.getItemid();
                if (item != null && "1".equals(item.getState()) && "1".equals(item.getUseflag()))
                    columnList.add(colunmName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columnList;
    }

    /**
     * 获取含有输入字符的部门id
     * 
     * @param name
     *            输入的字符
     * @return
     */
    public String getOrgIds(String name) {
        String orgids = "";
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select codeitemid");
            sql.append(" from organization");
            sql.append(" where codesetid = 'UM'");
            sql.append(" and (codeitemid in (");
            sql.append(" select n0304");
            sql.append(" from n03)");
            sql.append(" or codeitemid in (");
            sql.append(" select n0305");
            sql.append(" from n03))");
            sql.append(" and codeitemdesc like '%" + name + "%'");
            ContentDAO dao = new ContentDAO(conn);
            rs = dao.search(sql.toString());
            while (rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                orgids += ",'" + codeitemid + "'";
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }
        return orgids;
    }

}
