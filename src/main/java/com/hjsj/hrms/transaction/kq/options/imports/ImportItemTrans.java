package com.hjsj.hrms.transaction.kq.options.imports;

import com.hjsj.hrms.businessobject.kq.options.imports.SearchImportBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * <p>
 * Title:考勤规则新增导入指标
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Apr 26, 2010:1:15:27 PM
 * </p>
 * 
 * @author wangyao
 * @version 1.0
 * 
 */
public class ImportItemTrans extends IBusiness {

    public void execute() throws GeneralException {
        HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
        String it = (String) hm.get("akq_item");
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RecordVo vo = new RecordVo("kq_item");

        boolean g_no = getOther_param("kq_item");
        if (!g_no)
            createDataAnalyseTmp("kq_item");

        try {
            vo.setString("item_id", it);
            vo = dao.findByPrimaryKey(vo);
            String fielditemid = vo.getString("fielditemid");
            String temidtype = gettemidtype(fielditemid); // 用来确定指标类型
            ArrayList list = getSubSetList(); // 来源子集内容
            this.getFormHM().put("mainlist", list);
            this.getFormHM().put("temidtype", temidtype);

            /** 取xml **/
            SearchImportBo searchImportbo = new SearchImportBo(this.getFrameconn(), it);
            String subset = searchImportbo.getValue("subset");
            if ("".equals(subset) || subset.length() < 0)
                subset = "#";

            String field = searchImportbo.getValue("field");
            if ("".equals(field) || field.length() < 0)
                field = "#";

            String begindate = searchImportbo.getValue("begindate");
            if ("".equals(begindate) || begindate.length() < 0)
                begindate = "#";

            String enddate = searchImportbo.getValue("enddate");
            if ("".equals(enddate) || enddate.length() < 0)
                enddate = "#";

            this.getFormHM().put("subset", subset);
            this.getFormHM().put("field", field);
            this.getFormHM().put("begindate", begindate);
            this.getFormHM().put("enddate", enddate);
            if ("".equals(subset) || subset.length() < 0) {
                String subset1 = "";
                if (0 < list.size()) {
                    CommonData dataobj = (CommonData) list.get(0);
                    subset1 = dataobj.getDataValue();
                }

                ArrayList listitemid = getitemidList(temidtype, subset1); // 指标
                this.getFormHM().put("itemidlist", listitemid);

                ArrayList listbegindate = getitemidList("D", subset1); // 开始时间
                this.getFormHM().put("listdate", listbegindate);
            } else {
                ArrayList listitemid = getitemidList(temidtype, subset); // 指标
                this.getFormHM().put("itemidlist", listitemid);
                ArrayList listbegindate = getitemidList("D", subset); // 开始时间
                this.getFormHM().put("listdate", listbegindate);
            }
            this.getFormHM().put("akq_item", it);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用来确定指标类型
     * 
     * @param fielditemid
     * @return
     */
    private String gettemidtype(String fielditemid) {
        String type = "";
        ContentDAO dao = new ContentDAO(this.getFrameconn());
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select itemtype from t_hr_busifield"); 
        sql.append(" where UPPER(itemid)='" + fielditemid.toUpperCase() + "'");
        try {
            rs = dao.search(sql.toString());
            if (rs.next())
                type = rs.getString("itemtype");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return type;
    }

    /**
     * 来源子集内容
     * 
     * @return
     */
    private ArrayList getSubSetList() {
        ArrayList list = new ArrayList();
        
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();        
        sql.append("select fieldsetid,customdesc from fieldset");
        sql.append(" where fieldsetid like 'A%' and useflag='1'");
        sql.append(" order by fieldsetid");
        
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs = dao.search(sql.toString());
            
            CommonData dataobj = new CommonData("#", "请选择");
            list.add(dataobj);
            while (rs.next()) {
                dataobj = new CommonData(rs.getString("fieldsetid"), rs.getString("customdesc"));
                list.add(dataobj);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return list;
    }

    /**
     * 指标类型
     * 
     * @param type
     * @param fieldsetid
     * @return
     */
    private ArrayList getitemidList(String type, String fieldsetid) {
        ArrayList list = new ArrayList();
        
        RowSet rs = null;
        StringBuffer sql = new StringBuffer();        
        sql.append("select itemid,itemdesc from fielditem");
        sql.append(" where fieldsetid='" + fieldsetid + "' and itemtype='" + type + "' and useflag='1'");
        
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs = dao.search(sql.toString());
            
            CommonData dataobj = new CommonData("#", "请选择");
            list.add(dataobj);
            while (rs.next()) {
                dataobj = new CommonData(rs.getString("itemid"), rs.getString("itemdesc"));
                list.add(dataobj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return list;
    }

    /**
     * 查看 kq_item表的other_param字段是否存在
     * 
     * @param table
     * @return
     */
    private boolean getOther_param(String table) {
        boolean flag = true;
        if (!checkFieldSave(table, "other_param")) {
            flag = false;
        }
        return flag;
    }

    private boolean checkFieldSave(String table, String field_name) {
        boolean isCorrect = false;
        
        RowSet rs = null;
        String sql = "select * from " + table + " where 1=2";
        try {
            ContentDAO dao = new ContentDAO(this.getFrameconn());
            rs = dao.search(sql.toString());
            
            ResultSetMetaData rm = rs.getMetaData();
            int column_count = rm.getColumnCount();
            for (int i = 1; i <= column_count; i++) {
                String column_name = rm.getColumnName(i);
                if (column_name == null || column_name.length() <= 0)
                    column_name = "";
                if (column_name.equalsIgnoreCase(field_name)) {
                    isCorrect = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return isCorrect;
    }

    /*
     * 创建kq_item表的other_param字段
     */
    private String createDataAnalyseTmp(String table_name) throws GeneralException {
        DbWizard dbWizard = new DbWizard(this.getFrameconn());

        Field temp = new Field("other_param", "导入子集");
        temp.setDatatype(DataType.CLOB);
        temp.setKeyable(false);
        temp.setVisible(false);

        Table table = new Table(table_name);
        table.addField(temp);

        dbWizard.addColumns(table);

        DBMetaModel dbmodel = new DBMetaModel(this.getFrameconn());
        dbmodel.reloadTableModel(table_name);

        return table_name;
    }

}
