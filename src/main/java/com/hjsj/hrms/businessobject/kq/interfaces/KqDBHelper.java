package com.hjsj.hrms.businessobject.kq.interfaces;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title: KqDBHelper </p>
 * <p>Description: 考勤模块数据库操作辅助类
 * 主要用于维护考勤中的表、字段、固定记录的增减和结果修改等
 * </p>
 * <p>Company: hjsj</p>
 * <p>create time: 2013-9-10 上午11:37:16</p>
 * @author zhaoxj
 * @version 1.0
 */
public class KqDBHelper {

    private Connection conn;

    private KqDBHelper() {

    }

    public KqDBHelper(Connection conn) {
        this.conn = conn;
    }

    /**
     * @Title: addKqTypeStopCodeItem   
     * @Description:  添加暂停考勤代码到考勤方式代码项  
     */
    public void addKqTypeStopCodeItem() {
        String sql = "select 1 from codeitem" + " where codesetid='29'" + " and codeitemid='04'";

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sql);
            if (!rs.next()) {
                StringBuffer in = new StringBuffer();
                in.append("insert into codeitem (codesetid,codeitemid,codeitemdesc,parentid,childid,flag)");
                in.append(" values('29','04','暂停考勤','04','04','1')");
                dao.update(in.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
    }

    /**
     * @Title: getCommonDataListFromDB   
     * @Description: 从数据库取出记录组装成CommonData，放到List中  
     * @param @param sql
     * @param @param keyFld
     * @param @param valueFld
     * @param @return
     * @param @throws Exception 
     * @return ArrayList    
     * @throws
     */
    public ArrayList getCommonDataListFromDB(String sql, String nameFld, String valueFld) throws Exception {
        ArrayList list = new ArrayList();

        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            while (rs.next()) {
                CommonData dataobj = new CommonData(rs.getString(valueFld), rs.getString(nameFld));
                list.add(dataobj);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }

        return list;
    }

    /**
     * @Title: getCommondDataListFromDBWithEmptyItem   
     * @Description:  得到带有空选项（"请选择")的CommondData列表 
     * @param @param sql
     * @param @param keyFld
     * @param @param valueFld
     * @param @return
     * @param @throws Exception 
     * @return ArrayList    
     * @throws
     */
    public ArrayList getCommondDataListFromDBWithEmptyItem(String sql, String nameFld, String valueFld) throws Exception {
        ArrayList commondDatas = null;
        try {
            commondDatas = getCommonDataListFromDB(sql, nameFld, valueFld);

            CommonData aCommondData = new CommonData();
            aCommondData.setDataName("请选择...");
            aCommondData.setDataValue("#");
            commondDatas.add(0, aCommondData);
        } catch (Exception e) {
            throw e;
        }

        return commondDatas;
    }
    
    /**
     * @Title: getTableNameByFieldName   
     * @Description: 根据字段名得到表  
     * @param @param fieldName
     * @param @return 
     * @return String    
     * @throws
     */
    public String getTableNameByFieldName(String fieldName) {
        if(StringUtils.isBlank(fieldName)) {
            return "";
        }
        
        String tableName = "";
        StringBuffer sqlBuff = new StringBuffer();
        sqlBuff.append("select fieldsetid from fielditem");
        sqlBuff.append(" where itemid=?");
        sqlBuff.append(" and useflag='1'");
        
        ArrayList sqlParams = new ArrayList();
        sqlParams.add(fieldName);

        ResultSet rs = null;
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            rs = dao.search(sqlBuff.toString(), sqlParams);
            if (rs.next()) {
                tableName = rs.getString("fieldsetid");
                tableName = tableName == null ? "" : tableName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return tableName;
    }
    
    /**
     * @Title: getB0110ByA0100   
     * @Description: 通过人员nbase和a0100取得b0110   
     * @param @param nbase
     * @param @param a0100
     * @param @return 
     * @return String    
     * @throws
     */
    public String getEmpB0110(String nbase,String a0100){
        StringBuffer sb = new StringBuffer();
        String b0110 = "";
        sb.append("select b0110 from ");
        sb.append(nbase + "A01");
        sb.append(" where a0100 = '" + a0100 + "'");
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) 
            {
                b0110 = rs.getString("b0110");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return b0110;
    }
    
    /**
     * @Title: isRecordExist   
     * @Description: 判断记录是否存在   
     * @param @param table 表名
     * @param @param whr sql条件
     * @param @return 
     * @return boolean    
     * @throws
     */
    public boolean isRecordExist(String table, String whr) {
        return 0 < getRecordCount(table, whr);
    }
    
    /**
     * 取得表内符合条件的记录数
     * @Title: getRecordCount   
     * @Description:    
     * @param table 表名
     * @param whr   条件
     * @return
     */
    public int getRecordCount(String table, String whr) {
        int recCount = 0;
        
        StringBuffer sb = new StringBuffer();
        sb.append("select count(1) reccount from ");
        sb.append(table);
        sb.append(" where ");
        sb.append(whr);
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search(sb.toString());
            if (rs.next()) {
                recCount = rs.getInt("reccount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return recCount;
    }
    
    public String createTempTab(String SrcTab, String userName)
    {
        DbWizard dbWizard = new DbWizard(this.conn);
        
        String destTab = "t#_" + userName + "_kq_imp_dd";
        if(dbWizard.isExistTable(destTab,false))
        {
            dbWizard.dropTable(destTab);
        }
        
        StringBuffer sql = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            switch(Sql_switcher.searchDbServer())
            {
                  case Constant.MSSQL:
                  {
                      sql.append("select * Into "+destTab+" from "+SrcTab); 
                      sql.append(" where 1=2");
                      dao.update(sql.toString());
                      break;
                  }
                  case Constant.ORACEL:
                  { 
                      sql.append("Create Table "+destTab);
                      sql.append("  as select *  from "+SrcTab); 
                      sql.append(" where 1=2");
                      dao.update(sql.toString());
                      break;
                  }
                  case Constant.DB2:
                  {
                      sql.append("Create Table "+destTab);
                      sql.append("  AS (select *  from "+SrcTab); 
                      sql.append(" where 1=2) DEFINITION ONLY");
                      dao.update(sql.toString());
                      sql=new StringBuffer();
                      sql.append("INSERT INTO "+destTab);
                      sql.append(" select *  from "+SrcTab); 
                      sql.append(" where 1=2)");
                      dao.update(sql.toString());
                      break;
                  }
                  default: ;
            }
            
            /**重新加载数据模型*/            
            DBMetaModel dbmodel = new DBMetaModel(conn);
            dbmodel.reloadTableModel(destTab);    
        }catch(Exception e)
        {
            e.printStackTrace();
            destTab = "";
        }       
        
        return destTab;
    }
}
