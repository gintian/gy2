package com.hjsj.hrms.businessobject.kq.options;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.interfaces.KqDBHelper;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.DataDictionary;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 考勤规则类
 *<p>
 * Title:KqItem.java
 * </p>
 *<p>
 * Description:
 * </p>
 *<p>
 * Company:HJHJ
 * </p>
 *<p>
 * Create time:Sep 12, 2007
 * </p>
 * 
 * @author sunxin
 *@version 4.0
 */
public class KqItem {
    private Connection conn;

    public KqItem() {
    }

    public KqItem(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * 从代码类同步代码到考勤规则表
     */
    public void initSysItem() {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO kq_item(item_id,item_name,has_rest,has_feast,want_sum,item_color,displayorder)");
        sql.append(" SELECT codeitemid,codeitemdesc,0,0,0,1255255255,1 FROM codeitem");
        sql.append(" WHERE codesetid='27' AND codeitemid<>parentid");
        sql.append(" AND NOT EXISTS(SELECT 1 FROM kq_item WHERE kq_item.item_id=codeitem.codeitemid)");
        
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void leavePost() {
        if (!isLeavePostSave()) {
            isCodeitemid29Save();
        }
    }
    
    /**
     * 根据考勤规则汉字名称取得对应的日明细指标编码
     * @Title: getFieldIdByKqItemDesc   
     * @Description: 根据考勤规则汉字名称取得对应的日明细指标编码    
     * @param desc 考勤规则汉字名称
     * @return
     */
    public String getFieldIdByKqItemDesc(String desc) {
        String fieldId = "";
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT fielditemid FROM kq_item");
            sql.append(" WHERE item_name='").append(desc).append("'");
            rs = dao.search(sql.toString());
            if (rs.next()) {
                fieldId = rs.getString("fielditemid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return fieldId != null ? fieldId : "";
    }
    
    /**
     * @Title: existKqItem   
     * @Description: 是否存在符合条件的考勤规则代码   
     * @param @param whr sql条件,不带where关键字
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean existKqItem(String whr) {
        KqDBHelper kqDB = new KqDBHelper(this.conn);
        return kqDB.isRecordExist("codeitem", "codesetid='27' and " + whr);
    }

    /**
     * 判断29是否已经有用户添加的指标占用了
     * 
     */
    private void isCodeitemid29Save() {
        boolean isCorrect = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select codeitemid from codeitem where codeitemid=");
        sb.append("'29' and flag=0");
        sb.append(" and codesetid='27'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) {
                isCorrect = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        if (isCorrect) {
            if (update29()) {
                // 添加
                insertLeavePost();
            } else {
                // 强行修改
                updateLeavePost();
            }
        } else {
            insertLeavePost();
        }
    }

    private void insertLeavePost() {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into codeitem(codesetid,codeitemid,codeitemdesc,parentid,childid,flag)");
        sql.append(" values(?,?,?,?,?,?)");
        ArrayList in_list = new ArrayList();
        in_list.add("27");
        in_list.add("29");
        in_list.add("离岗时间");
        in_list.add("2");
        in_list.add("29");
        in_list.add("1");
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.insert(sql.toString(), in_list);
            sql = new StringBuffer();
            sql.append("insert into kq_item(item_id,item_name,has_rest,has_feast,want_sum,item_color,item_unit,displayorder)");
            sql.append(" values(?,?,?,?,?,?,?,?)");
            in_list = new ArrayList();
            in_list.add("29");
            in_list.add("离岗时间");
            in_list.add("0");
            in_list.add("0");
            in_list.add("1");
            in_list.add("1000000255");
            in_list.add("01");
            in_list.add("40");
            dao.insert(sql.toString(), in_list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateLeavePost() {
        StringBuffer sql = new StringBuffer();
        sql.append("update codeitem set flag='1',codeitemdesc='离岗时间'  where codeitemid=");
        sql.append("'29' and codesetid='27'");
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(sql.toString());
            sql = new StringBuffer();
            sql.append("update kq_item set item_name='离岗时间',fielditemid='',sdata_src='',");
            sql.append("s_expr='',c_expr='' where");
            sql.append(" item_id='29'");
            dao.update(sql.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改已经占用编号29的用户数据
     * 
     * @param newCodetiemid
     * @return
     */
    private boolean update29() {
        boolean isCorrect = false;
        String newCodetiemid = "";
        ArrayList list = new ArrayList();
        list.add("2A");
        list.add("2B");
        list.add("2C");
        list.add("2D");
        list.add("2E");
        list.add("2F");
        list.add("2G");
        list.add("2H");
        list.add("2I");
        list.add("2J");
        list.add("2K");
        list.add("2L");
        list.add("2M");
        list.add("2N");
        list.add("2O");
        list.add("2P");
        list.add("2Q");
        list.add("2R");
        list.add("2S");
        list.add("2T");
        list.add("2U");
        list.add("2V");
        list.add("2W");
        list.add("2X");
        list.add("2Y");
        list.add("2Z");
        for (int i = 0; i < list.size(); i++) {
            newCodetiemid = list.get(i).toString();
            if (!isuser29Save(newCodetiemid)) {
                updateUserCode(newCodetiemid);
                isCorrect = true;
                break;
            }
        }
        return isCorrect;
    }

    private void updateUserCode(String newCodetiemid) {
        StringBuffer sb = new StringBuffer();
        sb.append("update codeitem set codeitemid='" + newCodetiemid + "',childid='" + newCodetiemid + "'  where codeitemid=");
        sb.append("'29'");
        sb.append(" and codesetid='27' and flag=0");
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            dao.update(sb.toString());
            sb = new StringBuffer();
            sb.append("update kq_item set item_id='" + newCodetiemid + "'  where item_id=");
            sb.append("'29'");
            dao.update(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看新的指标id是否被占用
     * 
     * @param newCodetiemid
     * @return
     */
    private boolean isuser29Save(String newCodetiemid) {
        boolean isCorrect = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select codeitemid from codeitem where codeitemid=");
        sb.append("'" + newCodetiemid + "'");
        sb.append(" and codesetid='27' and flag=0");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) {
                isCorrect = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }

    /**
     * 判断时候已经添加了离岗时间
     * 
     * @return
     */
    private boolean isLeavePostSave() {
        boolean isCorrect = false;
        StringBuffer sb = new StringBuffer();
        sb.append("select codeitemid from codeitem where codeitemid=");
        sb.append("'29'");
        sb.append(" and codesetid='27' and flag=1");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) {
                isCorrect = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }
    
    /*
     * 是否有下级考勤项目
     * @param itemid <String> 考勤项目id
     * @return <boolean> 
     * 
     * zhaoxj 2013-07-19
     */
    public boolean haveChildKqItem(String itemid) {
        boolean haveChild = false;
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("select 1 from codeitem");
            sql.append(" where codeitemid LIKE '");
            sql.append(itemid + "%'");
            sql.append(" AND codeitemid<>'" + itemid);
            sql.append("' AND codesetid='27'");
            
            rs = dao.search(sql.toString());
            haveChild = rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return haveChild;
    }
    
    /**
     * 刷新使用了考勤规则代码类的表结构（字段长度）
     * @return
     */
    public boolean refreshTableStructUsedKqItemCodeSet(int codeItemLength) {
        boolean isOK = false;
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            StringBuilder sql = new StringBuilder();
            //查找字典里有没有长度小于当前代码长度的指标，如没有，则不需要修改
            sql.append("select itemlength from fielditem where codesetid='27'");
            sql.append(" AND itemlength<" + codeItemLength);
            sql.append(" union all");
            sql.append(" select itemlength from t_hr_busifield where codesetid='27'");
            sql.append(" AND itemlength<" + codeItemLength);
            
            rs = dao.search(sql.toString());
        
            if (!rs.next()) {
                return isOK;
            }
            
            //查找用到考勤规则代码类的表和字段
            sql.delete(0, sql.length());
            sql.append("select fieldsetid,itemid");
            sql.append(" from fielditem");
            sql.append(" where codesetid='27' and useflag='1'");
            sql.append(" AND itemlength<" + codeItemLength);
            sql.append(" union");
            sql.append(" select fieldsetid,itemid");
            sql.append(" from t_hr_busifield");
            sql.append(" where codesetid='27' and useflag='1'");
            sql.append(" AND itemlength<" + codeItemLength);
            rs = dao.search(sql.toString());
            
            ArrayList dbprelist = DataDictionary.getDbpreList();
            
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            DbWizard dbw = new DbWizard(this.conn);
            while(rs.next()){
                String fieldsetid = rs.getString("fieldsetid");
                String itemId = rs.getString("itemid");
                
                //修改物理表结构的字段长度
                if(fieldsetid.startsWith("A")){                    
                    for(int i=0;i<dbprelist.size();i++){
                        String pre = (String)dbprelist.get(i);
                        changedTableItemLen(dbmodel, dbw, pre + fieldsetid, itemId, codeItemLength);
                    }
                } else {
                    changedTableItemLen(dbmodel, dbw, fieldsetid, itemId, codeItemLength);
                }
            }
            
            //更新指标维护（主集、子集）
            sql.delete(0, sql.length());
            sql.append("update fielditem");
            sql.append(" set itemlength="+ codeItemLength);
            sql.append(" where codesetid='27'");
            sql.append(" AND itemlength<" + codeItemLength);
            dao.update(sql.toString());
            
            //更新业务字典
            sql.delete(0, sql.length());
            sql.append("update t_hr_busifield");
            sql.append(" set itemlength="+codeItemLength);
            sql.append(" where codesetid='27'");
            sql.append(" AND itemlength<" + codeItemLength);
            dao.update(sql.toString());
            
            isOK = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return isOK;        
    }
    
    private void changedTableItemLen(DBMetaModel dbmodel, DbWizard dbw, String tableName, String itemId, int codeItemLength) {
        try {
            //假期管理表(Q17)，假期类型是主键的一部分，需先去掉主键，修改完长度后，再加上主键
            if ("Q1709".equalsIgnoreCase(itemId)){
                dbw.dropPrimaryKey(tableName.toLowerCase());
            }
            
            Table table = new Table(tableName.toLowerCase());
            Field field = new Field(itemId.toLowerCase(),itemId.toLowerCase());
            field.setDatatype(DataType.STRING);
            field.setLength(codeItemLength);
            if ("Q1709".equalsIgnoreCase(itemId)) {
                field.setNullable(false);
            }
            table.addField(field);
            
            dbw.alterColumns(table);
            dbmodel.reloadTableModel(tableName.toLowerCase());
            
            //加回主键
            if ("Q1709".equalsIgnoreCase(itemId)) {
               table.clear();
               
               field = new Field("nbase");
               field.setKeyable(true);
               table.addField(field);
               
               field = new Field("A0100");
               field.setKeyable(true);
               table.addField(field);
               
               //年度
               field = new Field("Q1701");
               field.setKeyable(true);
               table.addField(field);
               
               //假期类型
               field = new Field("Q1709");
               field.setKeyable(true);
               field.setNullable(false);
               table.addField(field);
               
               dbw.addPrimaryKey(table);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    
    public String getFirstChildItemId(String parentId) {
        String childItemId = "";
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT childid FROM codeitem");
        sql.append(" WHERE codeitemid<>childid");
        sql.append(" AND codesetid='27'");
        sql.append(" AND codeitemid='" + parentId + "'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            if (rs.next()) {
                childItemId = rs.getString("childid");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return childItemId;
    }
    
    public boolean updateChildIdForParent(String parentId, String childId) {
        boolean isOK = false;
        
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE codeitem");
        sql.append(" SET childid='" + childId + "'");
        sql.append(" WHERE codesetid='27'");
        sql.append(" AND codeitemid='" + parentId + "'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(sql.toString());
            isOK = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isOK;
    }
    
    public boolean resetChildIdForParent(String oldChildId) {
        boolean isOK = false;
        
        String parentId = getParentId(oldChildId);
        if ("".equals(parentId)) {
            return true;
        }
        
        String curChildId = getFirstChildItemId(parentId);
        //父节点第一子节点不是oldChildId，无需替换子节点
        if (!curChildId.equals(oldChildId)) {
            return true;
        }
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT codeitemid FROM codeitem");
        sql.append(" WHERE codesetid='27' AND codeitemid<>parentid");
        sql.append(" AND parentid='" + parentId + "'");
        sql.append(" AND codeitemid<>'" + oldChildId + "'");
        sql.append(" ORDER BY a0000,codeitemid");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            
            String newChildId;
            if (rs.next()) {
                newChildId = rs.getString("codeitemid");
            } else {
                newChildId = parentId;
            }
            
            updateChildIdForParent(parentId, newChildId);
            
            isOK = true;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        
        return isOK;
    }
    
    private String getParentId(String childId) {
        String parentId = "";
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT parentid FROM codeitem");
        sql.append(" WHERE codesetid='27'");
        sql.append(" AND codeitemid='" + childId + "'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql.toString());
            
            if (rs.next()) {
                parentId = rs.getString("parentid");
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return parentId;
    }
}
