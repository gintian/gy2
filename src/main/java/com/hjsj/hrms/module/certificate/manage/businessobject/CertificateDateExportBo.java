package com.hjsj.hrms.module.certificate.manage.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class CertificateDateExportBo {
    private Connection conn = null;
    private UserView userView = null;
    public CertificateDateExportBo(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
    }
    
    /**
     * 获取导出需要的表头的合并列
     * @param fieldList 表头的列头
     * @return
     */
    public ArrayList<LazyDynaBean> getExcleMergedList(ArrayList<ColumnsInfo> fieldList) {
        ArrayList<LazyDynaBean> mergedList = new ArrayList<LazyDynaBean>();
        int num = 0;
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            ColumnsInfo columnsInfo = fieldList.get(i);
            ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if(!childColumns.isEmpty()) {
                LazyDynaBean bean = new LazyDynaBean();
                //设置合并列的起始行
                bean.set("fromRowNum", 0);
                //设置合并列的起始列
                bean.set("fromColNum", ind + num);
                //设置合并列的终止行
                bean.set("toRowNum", 0);
                //设置合并列的终止列
                bean.set("toColNum", ind + num + childColumns.size() - 1);
                //设置合并列的名称
                bean.set("content", columnsInfo.getColumnDesc());
                mergedList.add(bean);
                num = num + childColumns.size() - 1;
            }
            
            ind++;
        }
        
        return mergedList;

    }
    /**
     * 获取导出需要的表头数据
     * @param fieldList 表格中的列
     * @param mergedList 合并列
     * @param flag 是否是合并列中包含的列
     * @param index 为了防止excel表头列序号出错 fromColNum
     * @return
     */
    public ArrayList<LazyDynaBean> getExcleHeadList(ArrayList fieldList, ArrayList<LazyDynaBean> mergedList, boolean flag ,int index) {
        int num = 0;
        ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
        int ind = 0;
        for(int i = 0; i < fieldList.size(); i++) {
            ColumnsInfo columnsInfo = (ColumnsInfo) fieldList.get(i);
            ArrayList<ColumnsInfo> childColumns = columnsInfo.getChildColumns();
            //合并列在表头中为hashmap，非hashmap的都为非合并列
            if(!childColumns.isEmpty()) {
                //获取合并列中包含的指标
                headList.addAll(getExcleHeadList(childColumns, mergedList, true, ind + num));
                num = num + childColumns.size();
            } else {
                ColumnsInfo info = (ColumnsInfo) fieldList.get(i);
                LazyDynaBean bean = new LazyDynaBean();
                String itemid = info.getColumnId();
                if(StringUtils.isEmpty(itemid) || "a0100".equalsIgnoreCase(itemid) 
                        || "nbase".equalsIgnoreCase(itemid) || "imgpic".equalsIgnoreCase(itemid)
                        || "i9999".equalsIgnoreCase(itemid) || 4 == columnsInfo.getLoadtype())
                    continue;
                
                bean.set("itemid", itemid);
                bean.set("content", info.getColumnDesc()+ "");
                bean.set("codesetid", info.getCodesetId());
                bean.set("colType", info.getColumnType());
                bean.set("decwidth", info.getDecimalWidth() + "");
                if(mergedList != null && mergedList.size() > 0){
                    if(flag) {
                        //设置合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 1);
                        bean.set("toRowNum", 1);
                        //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + index);
                        bean.set("toColNum", ind + index);
                    } else {
                        //设置非合并列中包含的指标起始与终止的行
                        bean.set("fromRowNum", 0);
                        bean.set("toRowNum", 1);
                        //设置指标的起始与终止的列
                        bean.set("fromColNum", ind + num);
                        bean.set("toColNum", ind + num);
                    }
                }

                headList.add(bean);
                ind++;
            }
        }
        
        return headList;

    }
    /**
     * 获取栏目设置的排序指标拼接排序的sql
     * @param dbname 招聘人员库
     * @param scheme_id 
     * @return
     */
    public String getOrderSql(String dbname, String scheme_id) {
        String orderSql = "";
        RowSet rs = null;
        try{
            String sql = "select 1 from t_sys_table_scheme " +
                    "where submoduleid = '"+scheme_id+"' and is_share = 0 and username = '"
                    + this.userView.getUserName() + "'";
            
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            if(rs.next())
                sql = "select itemid,fieldsetid,is_order from t_sys_table_scheme_item where scheme_id = (" +
                        "select scheme_id from t_sys_table_scheme " +
                        "where submoduleid = '"+scheme_id+"' and is_share = '0' and username = '"
                    + this.userView.getUserName() + "') and is_display = '1' " +
                            "and is_order<>0 order by displayorder";
            else
                sql = "select itemid,fieldsetid,is_order from t_sys_table_scheme_item where scheme_id = (" +
                        "select scheme_id from t_sys_table_scheme " +
                        "where submoduleid = '"+scheme_id+"' and is_share = '1') " +
                        "and is_display = '1' and is_order<>0 order by displayorder";
                    
             rs = dao.search(sql);
             while(rs.next()) {
                 String itemid = rs.getString("itemid");
                 String fieldsetid = rs.getString("fieldsetid");
                 String isOrder = rs.getString("is_order");
                 
                 if(StringUtils.isNotEmpty(fieldsetid) && fieldsetid.startsWith("A"))
                     orderSql += dbname + fieldsetid + "." + itemid;
                 else
                     orderSql += itemid;
                 
                 if("1".equalsIgnoreCase(isOrder))
                     orderSql += ",";
                 
                 if("2".equalsIgnoreCase(isOrder))
                     orderSql += " desc,";
             }
            
             if(orderSql.endsWith(","))
                 orderSql = orderSql.substring(0, orderSql.length() - 1);
             
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(rs);
        }

        return orderSql;
    }
}
