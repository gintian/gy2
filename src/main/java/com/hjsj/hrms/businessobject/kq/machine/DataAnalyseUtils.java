package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.*;

public class DataAnalyseUtils implements DateAnalyseImp {
    private Connection conn;
    private UserView   userView;
    private String     kq_dkind    = "dkind";
    private ContentDAO dao;
    private String     analyseType = "";

    public DataAnalyseUtils(Connection conn, UserView userView) {
        this.conn = conn;
        this.userView = userView;
        this.dao = new ContentDAO(this.conn);
    }

    /**
     * 建立数据分析临时表
     * @param table_name
     * @return
     * @throws GeneralException
     */
    public String createDataAnalyseTmp(String table_name) throws GeneralException {

        KqUtilsClass kqUtilsClass = new KqUtilsClass(this.conn);

        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(table_name);
        
        Table table = new Table(table_name);
        StringBuffer sql = new StringBuffer();
        sql.append("q03,kq_class");
        StringBuffer columns = new StringBuffer();
        columns.append("q03.*,kq_class.*");
        kqUtilsClass.createTempTable(sql.toString(), table_name, columns.toString(), "1=2", "");
        Field temp = new Field("card_no", "工作卡号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("card_time", "工作时间");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(500);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        temp1 = new Field(this.kq_dkind, "日期类型");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(20);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        temp = new Field("flag", "有效状态");//是否生效
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        if (!dbWizard.isExistField("q03", "IsOk", false)) {
            temp = new Field("IsOk", "是否正常");//是否正常
            temp.setDatatype(DataType.STRING);
            temp.setLength(50);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        temp = new Field("LackCard", "缺刷标记");//缺刷标记
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        //首钢增加指标 1为正常 0不正常
        temp = new Field("ISNormal", "正常标记");//缺刷标记
        temp.setDatatype(DataType.INT);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);

        temp = new Field("cur_user", "当前操作人员");//当前操作人员
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.addColumns(table);
            /*****删除主键****/
            table = new Table(table_name);
            Field d_temp = new Field("class_id", "班次编号");
            table.addField(d_temp);
            d_temp = new Field("name", "班次名称");
            table.addField(d_temp);
            /*d_temp= new Field("a0100","人员编号");	  
            table.addField(d_temp);
            d_temp=new Field("nbase","人员库");	  
            table.addField(d_temp);
            d_temp=new Field("q03z0","日期");		  
            table.addField(d_temp);*/
            dbWizard.dropColumns(table);
            table = new Table(table_name);
            Field a_temp = new Field("class_id", "班次编号");
            a_temp.setDatatype(DataType.INT);
            a_temp.setLength(8);
            a_temp.setKeyable(false);
            a_temp.setVisible(false);
            table.addField(a_temp);
            a_temp = new Field("name", "班次名称");
            a_temp.setDatatype(DataType.STRING);
            a_temp.setLength(200);
            a_temp.setKeyable(false);
            a_temp.setVisible(false);
            table.addField(a_temp);
            dbWizard.addColumns(table);
            Table pk_table = new Table(table_name);
            Field pk_temp = new Field("a0100", "人员编号");
            pk_temp.setLength(8);
            pk_temp.setKeyable(true);
            temp.setNullable(false);
            temp.setDatatype(DataType.STRING);
            pk_table.addField(pk_temp);
            pk_temp = new Field("nbase", "人员库");
            pk_temp.setLength(8);
            pk_temp.setKeyable(true);
            temp.setNullable(false);
            temp.setDatatype(DataType.STRING);
            pk_table.addField(pk_temp);
            pk_temp = new Field("q03z0", "日期");
            pk_temp.setLength(10);
            pk_temp.setKeyable(true);
            temp.setNullable(false);
            temp.setDatatype(DataType.STRING);
            pk_table.addField(pk_temp);
            dbWizard.addPrimaryKey(pk_table);
        } catch (Exception e) {
            //zxj 20140726 此处出现主键已存在异常不影响使用，屏蔽错误信息
            //e.printStackTrace();
        }

        /**重新加载数据模型*/
        DBMetaModel dbmodel = new DBMetaModel(conn);
        dbmodel.reloadTableModel(table_name);
        //System.out.println(table_name);
        return table_name;
    }

    public void checkAnalyseTempTab(String tablename) {
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
        ArrayList fieldList = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
        Table table = null;
        DbWizard dbw = new DbWizard(this.conn);
        try {
            DBMetaModel dbmodel = new DBMetaModel(this.conn);
            for (int i = 0; i < fieldList.size(); i++) {
                FieldItem fielditem = (FieldItem) fieldList.get(i);
                if ("1".equals(fielditem.getUseflag()) && !fielditem.isKeyable()) {
                    String itemid = fielditem.getItemid();
                    if (!"nbase".equalsIgnoreCase(itemid) && !"a0100".equalsIgnoreCase(itemid)
                            && !"a0101".equalsIgnoreCase(itemid) && !"q03z0".equalsIgnoreCase(itemid)
                            && !"b0110".equalsIgnoreCase(itemid) && !"e0122".equalsIgnoreCase(itemid)
                            && !"e01a1".equalsIgnoreCase(itemid) && !"q03z3".equalsIgnoreCase(itemid)
                            && !"q03z5".equalsIgnoreCase(itemid) && !"state".equalsIgnoreCase(itemid)
                            && !"q03z1".equalsIgnoreCase(itemid) && !"i9999".equalsIgnoreCase(itemid)) {
                        table = new Table(tablename);
                        if (!reconstructionKqField.checkFieldSave(tablename, itemid)) {
                            //System.out.println(itemid+"---"+tablename);
//                            Field field = new Field(itemid, itemid);
                            //zxj changed 20140402 setDataType要接受的是数据库字段类型，不是“N","A","D","M"!!!
//                            field.setDatatype(fielditem.getItemtype());
//                            field.setLength(fielditem.getItemlength());
                            
                            table.addField(fielditem);
                            dbw.addColumns(table);
                            dbmodel.reloadTableModel(tablename);
                        } else if (!reconstructionKqField.checkFieldType(tablename, itemid, fielditem.getItemtype())) {
                            Field field = new Field(itemid, itemid);
                            //field.setDatatype(fielditem.getItemtype());
                            //field.setLength(fielditem.getItemlength());
                            table.addField(field);
                            dbw.dropColumns(table);
                            //dbmodel.reloadTableModel(tablename);
                            table = new Table(tablename);
                            table.addField(fielditem);
                            dbw.addColumns(table);
                            dbmodel.reloadTableModel(tablename);
                        }
                    }

                }
            }
            if (!reconstructionKqField.checkFieldSave(tablename, "cur_user")) {
                table = new Table(tablename);
                Field field = new Field("cur_user", "当前操作人员");//当前操作人员
                field.setDatatype(DataType.STRING);
                field.setLength(50);
                field.setKeyable(false);
                field.setVisible(false);
                table.addField(field);
                dbw.addColumns(table);
                dbmodel.reloadTableModel(tablename);
            }

            /*此处不需要频繁修改表结构，否则并发数据处理会死锁
            table = new Table(tablename);
            Field temp = new Field("IsOk", "是否正常");//是否正常
            temp.setDatatype(DataType.STRING);
            temp.setLength(50);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
            dbw.alterColumns(table);
            dbmodel.reloadTableModel(tablename);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除不用的字段
     * @param tablename
     * @throws GeneralException
     */
    public void dropAnalyseTempTabCloumn(String tablename) throws GeneralException {
        //I9999,state,zeroflag,domain_count,one_absent
        DBMetaModel dbmodel = new DBMetaModel(this.conn);
        DbWizard dbw = new DbWizard(this.conn);
        Table table = new Table(tablename);
        Field temp = null;
        boolean iscorrect = false;
        ReconstructionKqField reconstructionKqField = new ReconstructionKqField(this.conn);
        if (reconstructionKqField.checkFieldSave(tablename, "i9999")) {
            temp = new Field("I9999", "I9999");
            table.addField(temp);
            iscorrect = true;
        }
        if (reconstructionKqField.checkFieldSave(tablename, "state")) {
            temp = new Field("state", "state");
            table.addField(temp);
            iscorrect = true;
        }
        if (reconstructionKqField.checkFieldSave(tablename, "zeroflag")) {
            temp = new Field("zeroflag", "zeroflag");
            table.addField(temp);
            iscorrect = true;
        }
        if (reconstructionKqField.checkFieldSave(tablename, "domain_count")) {
            temp = new Field("domain_count", "domain_count");
            table.addField(temp);
            iscorrect = true;
        }
        if (reconstructionKqField.checkFieldSave(tablename, "one_absent")) {
            temp = new Field("one_absent", "one_absent");
            table.addField(temp);
            iscorrect = true;
        }
        if (iscorrect) {
            dbw.dropColumns(table);
            dbmodel.reloadTableModel(tablename);
        }

    }

    /**
      * 建立时间临时表
      * @return
      */
    public String createTimeTemp() throws GeneralException {
        //		 String table_name="analyse_time_temp_"+userView.getUserName()+"_"+this.userView.getUserId();
        //更改把后面 id去掉，要不出现中文的时候名字就会过长
        String table_name = "analyse_" + userView.getUserName();//time_temp_  changed at 20091203 cmq 去掉
        table_name = table_name.toLowerCase();
       
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(table_name);
        
        Table table = new Table(table_name);
        Field temp = new Field("orgid", "组织编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        Field temp1 = new Field("sDate", "考勤日期");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(20);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        table.addField(temp1);
        Field temp2 = new Field(this.kq_dkind, "标志");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(2);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        table.addField(temp2);
        try {
            if (!dbWizard.isExistTable(table_name.toLowerCase(), false)) {
                dbWizard.createTable(table);
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //throw GeneralExceptionHandler.Handle(e);
        }
        return table_name;
    }

    /**
     * 返回单位编号
     * @return
     * @throws Exception
     */
    public String getB0110(String code, String kind) throws GeneralException {
        String b0110 = code;
        String codesetid = "";
        if ("1".equals(kind) || "0".equals(kind)) {
            codesetid = code;
            do {
                String codeset[] = RegisterInitInfoData.getB0100(b0110, conn);
                if (codeset != null && codeset.length >= 0) {
                    codesetid = codeset[0];
                    b0110 = codeset[1];
                }
            } while (!"UN".equals(codesetid));

        }
        return b0110;
    }

    /**
     * 更新人员的考勤方式//以q03为标准
     * @param table_temp
     * @throws GeneralException
     */
    public void synchronizationInitTemp_Table(String table_temp) throws GeneralException {
        String destTab = table_temp;//目标表
        String srcTab = "q03";//源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab
                + ".q03z0=" + srcTab + ".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
        //strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".q03z3","'kq'")+"<>"+Sql_switcher.isnull(srcTab+".q03z3","'kq'");
        String strSet = destTab + ".q03z3=" + srcTab + ".q03z3";//更新串  xxx.field_name=yyyy.field_namex,....
        String strDWhere = "";//更新目标的表过滤条件		 
        String strSWhere = "";//源表的过滤条件  
        //		 strSWhere=strSWhere+" and "+Sql_switcher.isnull(destTab+".q03z3","'kq'")+"<>"+Sql_switcher.isnull(srcTab+".q03z3","'kq'");
        //更改<> q03 的时候就没有数据，所以要 = 
        strSWhere = Sql_switcher.isnull(destTab + ".q03z3", "'kq'") + "<>" + Sql_switcher.isnull(srcTab + ".q03z3", "'kq'");
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        //System.out.println("更新人员的考勤方式--->"+update);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            //dao.update(update);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
    }

    /**
     * 更新人员的考勤方式//以q03为标准
     * @param table_temp
     * @throws GeneralException
     */
    public void synchronizationInitTemp_Table(String nbase, String table_temp, String codewhere, String start_date,
            String end_date) throws GeneralException {
        String destTab = table_temp;//目标表
        String srcTab = "q03";//源表
        String strJoin = destTab + ".A0100=" + srcTab + ".A0100 and " + destTab + ".nbase=" + srcTab + ".nbase and " + destTab
                + ".q03z0=" + srcTab + ".q03z0";//关联串  xxx.field_name=yyyy.field_namex,....
        //strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".q03z3","'kq'")+"<>"+Sql_switcher.isnull(srcTab+".q03z3","'kq'");
        String strSet = destTab + ".q03z3=" + srcTab + ".q03z3";//更新串  xxx.field_name=yyyy.field_namex,....
        String strDWhere = destTab + ".q03z0>='" + start_date + "' and " + destTab + ".q03z0<='" + end_date + "' and cur_user='"
                + this.userView.getUserName() + "'";//更新目标的表过滤条件	
        if (nbase != null && nbase.length() == 3) {
            strDWhere = strDWhere + " and " + destTab + ".nbase='" + nbase + "'";
        }
        if (codewhere != null && codewhere.length() > 0) {
        	if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
        		strDWhere = strDWhere + " and (" + codewhere.replaceAll("nbase", destTab + ".nbase").replaceAll("a0100", destTab + ".a0100") + ")";
        	} else {
                strDWhere = strDWhere + " and " + destTab + "." + codewhere + "";
        	}
        }
        String strSWhere = "";//源表的过滤条件  		
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(" " + srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='" + end_date + "'");
        if (nbase != null && nbase.length() == 3) {
            strBuf.append(" and " + srcTab + ".nbase='" + nbase + "'");
        }
        //		
        strBuf.append(" and " + Sql_switcher.isnull(srcTab + ".q03z3", "'kq'") + "<>" + "'kq'");
        if (codewhere != null && codewhere.length() > 0) {
        	if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
        		strBuf.append(" and (" + codewhere.replaceAll("nbase", srcTab + ".nbase").replaceAll("a0100", srcTab + ".a0100")).append(")");
        	} else {
        		strBuf.append(" and " + srcTab + "." + codewhere + "");
        	}
        }
        strSWhere = strBuf.toString();
        String update = Sql_switcher.getUpdateSqlTwoTable(destTab, srcTab, strJoin, strSet, strDWhere, strSWhere);
        //System.out.println(update);
        if (nbase != null && nbase.length() > 0) {
            strJoin = strJoin + " and " + srcTab + ".nbase='" + nbase + "'";
        }
        strJoin = strJoin + " and " + srcTab + ".q03z0>='" + start_date + "' and " + srcTab + ".q03z0<='" + end_date + "'";
        if (codewhere != null && codewhere.length() > 0) {
        	if (codewhere.contains("nbase") && codewhere.contains("a0100")) {
        		strJoin = strJoin + " and (" + codewhere.replaceAll("nbase", srcTab + ".nbase").replaceAll("a0100", srcTab + ".a0100") + ")";
        	} else {
        		strJoin = strJoin + " and " + srcTab + "." + codewhere + "";
        	}
        }
        update = KqUtilsClass.repairSqlTwoTable(srcTab, strJoin, update, strDWhere, "");
        //System.out.println(update);		 
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update(update, new ArrayList());
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("同步考勤处理考勤方式数据出错！"));
        }
    }

    /**
     * 考勤规则的一个hashmap集
     * @return
     * @throws GeneralException
     */
    public HashMap count_Leave() throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,item_name,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hashM = new HashMap();
        String fielditemid = "";
        try {
            rs = dao.search(kq_item_sql);
            while (rs.next()) {
                HashMap hashm_one = new HashMap();
                if (rs.getString("fielditemid") == null || rs.getString("fielditemid").length() <= 0) {
                    continue;
                }
                ArrayList fielditemlist = DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
                for (int i = 0; i < fielditemlist.size(); i++) {
                    FieldItem fielditem = (FieldItem) fielditemlist.get(i);
                    fielditemid = rs.getString("fielditemid");
                    if (fielditemid.equalsIgnoreCase(fielditem.getItemid())) {
                        //System.out.println(fielditemid+"---------"+fielditem.getItemid());
                        hashm_one.put("fielditemid", rs.getString("fielditemid"));
                        String has_rest = rs.getString("has_rest") != null && rs.getString("has_rest").length() > 0 ? rs
                                .getString("has_rest") : "0";
                        String has_feast = rs.getString("has_feast") != null && rs.getString("has_feast").length() > 0 ? rs
                                .getString("has_feast") : "0";
                        hashm_one.put("has_rest", has_rest);
                        hashm_one.put("has_feast", has_feast);
                        hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                        hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
                        hashm_one.put("item_name", PubFunc.DotstrNull(rs.getString("item_name")));
                        hashM.put(rs.getString("item_id"), hashm_one);
                        continue;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashM;
    }

    /**
     * 通过班次名称查找考勤规则
     * @param name
     * @return
     * @throws GeneralException
     */
    public HashMap getKqItemByNameFromDB(String name) throws GeneralException {
        RowSet rs = null;
        String kq_item_sql = "select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";
        kq_item_sql = kq_item_sql + " where item_name='" + name + "'";
        ContentDAO dao = new ContentDAO(this.conn);
        HashMap hashm_one = new HashMap();
        try {
            rs = dao.search(kq_item_sql);
            if (rs.next()) {
                hashm_one.put("fielditemid", rs.getString("fielditemid"));
                hashm_one.put("has_rest", PubFunc.DotstrNull(rs.getString("has_rest")));
                hashm_one.put("has_feast", PubFunc.DotstrNull(rs.getString("has_feast")));
                hashm_one.put("item_unit", PubFunc.DotstrNull(rs.getString("item_unit")));
                hashm_one.put("sdata_src", PubFunc.DotstrNull(rs.getString("sdata_src")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashm_one;
    }

    /**
        * 临时异常表
        *
        */
    public void ceartFExceptCardTab(String tempFExceptCardTab, String analyseType) {
        if (analyseType != null && "101".equals(analyseType)) {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(tempFExceptCardTab, false)) {
                ceartFExceptCardTab(tempFExceptCardTab);
            }
        } else {
            ceartFExceptCardTab(tempFExceptCardTab);
        }
    }

    public void ceartFExceptCardTab(String tempFExceptCardTab) {

        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(tempFExceptCardTab);
        
        Table table = new Table(tempFExceptCardTab);
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0101", "人员姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("b0110", "单位编码");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e0122", "部门编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e01a1", "职位编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("card_no", "卡号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("work_date", "日期");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("work_time", "时间");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("machine_no", "机器编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("location", "地址");
        temp.setDatatype(DataType.STRING);
        temp.setLength(100);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("ExceptType", "异常类型");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("flag", "状态");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FTranOverTimeTab := 'kqtmp_' + m_rUser.cUserName + '_tranovertime';
    /**
     * 建立延时加班临时表
     */
    public void createTranOverTimeTab(String tempFTranOverTimeTab, String analyseType) {
        if (analyseType != null && "101".equals(analyseType)) {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(tempFTranOverTimeTab, false)) {
                createTranOverTimeTab(tempFTranOverTimeTab);
            }
        } else {
            createTranOverTimeTab(tempFTranOverTimeTab);
        }
    }

    public void createTranOverTimeTab(String tempFTranOverTimeTab) {

        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(tempFTranOverTimeTab);
        
        Table table = new Table(tempFTranOverTimeTab);
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0101", "人员姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("b0110", "单位编码");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e0122", "部门编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e01a1", "职位编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("begin_date", "开始时间");
        temp.setDatatype(DataType.DATETIME);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("end_date", "结束时间");
        temp.setDatatype(DataType.DATETIME);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("overtime_type", "加班类型");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("timelen", "加班时长");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        /**/
        ArrayList list = DataDictionary.getFieldList("Q11", Constant.USED_FIELD_SET);
        for (Iterator it = list.iterator(); it.hasNext();) {
            FieldItem item = (FieldItem) it.next();
            if ("休息扣除数".equals(item.getItemdesc())) {
                temp = new Field("restLen", "休息扣除数");
                temp.setDatatype(DataType.INT);
                temp.setKeyable(false);
                temp.setVisible(false);
                table.addField(temp);
            }
        }
        temp = new Field("status", "状态");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 休息日转加班 创建表
     * @param cardToOverTimeTab
     * @param analyseType
     */
    public void createCardToOverTimeTab(String cardToOverTimeTab, String analyseType) {
        if (analyseType != null && "101".equals(analyseType)) {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(cardToOverTimeTab, false)) {
                createCardToOverTimeTab(cardToOverTimeTab);
            }
        } else {
            createCardToOverTimeTab(cardToOverTimeTab);
        }
    }

    /**
     * 休息日转加班 创建表
     * @param cardToOverTimeTab
     */
    public void createCardToOverTimeTab(String cardToOverTimeTab) {
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(cardToOverTimeTab);
        
        Table table = new Table(cardToOverTimeTab);
        Field temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0101", "人员姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("b0110", "单位编码");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e0122", "部门编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e01a1", "职位编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("begin_date", "开始时间");
        temp.setDatatype(DataType.DATETIME);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("end_date", "结束时间");
        temp.setDatatype(DataType.DATETIME);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("overtime_type", "加班类型");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("time_len", "加班时长");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        String existQ11xx = KqUtilsClass.getFieldByDesc("Q11", "休息扣除数");
        if (existQ11xx != null && existQ11xx.length() > 0) {
            temp = new Field("q11xx", "休息扣除数");
            temp.setDatatype(DataType.INT);
            temp.setKeyable(false);
            temp.setVisible(false);
            table.addField(temp);
        }
        temp = new Field("status", "状态");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 业务申请与实际刷卡情况表
     *
     */
    /**
     * 建立延时加班临时表
     */
    public void createCompareBusiWithFactTab(String createCompareBusiWithFactTab, String analyseType) {
        if (analyseType != null && "101".equals(analyseType)) {
            DbWizard dbWizard = new DbWizard(this.conn);
            if (!dbWizard.isExistTable(createCompareBusiWithFactTab, false)) {
                createCompareBusiWithFactTab(createCompareBusiWithFactTab);
            }
        } else {
            createCompareBusiWithFactTab(createCompareBusiWithFactTab);
        }
    }

    public void createCompareBusiWithFactTab(String tempFBusiCompareTab) {
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(tempFBusiCompareTab);
        
        Table table = new Table(tempFBusiCompareTab);
        Field temp = new Field("id", "id");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        temp.setNullable(false);
        table.addField(temp);
        temp = new Field("appid", "申请id");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        temp.setNullable(false);
        table.addField(temp);
        temp = new Field("supplement", "");//补申请单的编号（与appid相连的第二条申请的appid，为了与前一条合并处理）
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("a0101", "人员姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("g_no", "工号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("b0110", "单位编码");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e0122", "部门编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("e01a1", "职位编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("q03z0", "工作日期");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("busi_begin", "业务开始时间");
        temp.setDatatype(DataType.DATE);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("busi_end", "业务开始时间");
        temp.setDatatype(DataType.DATE);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("busi_timelen", "业务时长");
        temp.setDatatype(DataType.FLOAT);
        temp.setDecimalDigits(4);
        temp.setLength(15);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("fact_begin", "实际开始时间");
        temp.setDatatype(DataType.DATE);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("fact_end", "实际开始时间");
        temp.setDatatype(DataType.DATE);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("fact_timelen", "实际时长");
        temp.setDatatype(DataType.FLOAT);
        temp.setDecimalDigits(4);
        temp.setLength(15);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("busi_type", "类型");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("status", "状态");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        temp = new Field("flag", "状态");
        temp.setDatatype(DataType.INT);
        temp.setKeyable(false);
        temp.setVisible(false);
        table.addField(temp);
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建业务申请记录临时表
     * @param tmpBusiDataTab
     */
    public void createTmpBusiDataTab(String tmpBusiDataTab) {
        DbWizard dbWizard = new DbWizard(this.conn);
        
        //删除已存在的表
        Table table = new Table(tmpBusiDataTab);

        Field temp = new Field("busiTab", "busiTab");
        temp.setDatatype(DataType.STRING);
        temp.setLength(10);
        //temp.setKeyable(true);
        //temp.setNullable(false);
        table.addField(temp);

        temp = new Field("applyID", "applyID");
        temp.setDatatype(DataType.STRING);
        temp.setLength(12);
        //temp.setKeyable(true);
        //temp.setNullable(false);
        table.addField(temp);

        temp = new Field("nbase", "人员库");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        table.addField(temp);

        temp = new Field("a0100", "人员编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(8);
        table.addField(temp);

        temp = new Field("a0101", "人员姓名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        table.addField(temp);

        temp = new Field("b0110", "单位编码");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        table.addField(temp);

        temp = new Field("e0122", "部门编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        table.addField(temp);

        temp = new Field("e01a1", "职位编号");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        table.addField(temp);

        temp = new Field("applyType", "applyType");
        temp.setDatatype(DataType.STRING);
        temp.setLength(30);
        table.addField(temp);

        temp = new Field("fromTime", "业务开始时间");
        temp.setDatatype(DataType.DATE);
        table.addField(temp);

        temp = new Field("toTime", "业务开始时间");
        temp.setDatatype(DataType.DATE);
        table.addField(temp);

        temp = new Field("restLen", "restLen");
        temp.setDatatype(DataType.FLOAT);
        temp.setDecimalDigits(4);
        temp.setLength(15);
        table.addField(temp);

        temp = new Field("refShift", "refShift");
        temp.setDatatype(DataType.INT);
        temp.setLength(8);
        table.addField(temp);

        temp = new Field("applyReason", "applyReason");
        temp.setDatatype(DataType.STRING);
        temp.setLength(255);
        table.addField(temp);

        
        try {
            dbWizard.createTable(table);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到所有班次信息
     * @return
     */
    public HashMap getAllKqClass() {
        String sql = "select * from kq_class where class_id<>0";
        HashMap map = new HashMap();
        RowSet rs = null;
        try {
            rs = this.dao.search(sql);
            while (rs.next()) {
                UnKqClassBean classbean = new UnKqClassBean(rs);
                map.put(rs.getString("class_id"), classbean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return map;
    }

    public String tranUnitToHours(HashMap item_hs) {
        String itemUnit = (String) item_hs.get("item_unit");
        String fielditemid = (String) item_hs.get("fielditemid");
        String chgItemValue = "";
        chgItemValue = Sql_switcher.isnull(fielditemid, "0");
        if (itemUnit.equals(unit_HOUR)) {

        } else if (itemUnit.equals(unit_MINUTE)) {
            chgItemValue = "(" + chgItemValue + "/60)";
        } else if (itemUnit.equals(unit_DAY)) {
            chgItemValue = "(" + chgItemValue + "*work_hours)";
        } else {
            chgItemValue = "";
        }
        return chgItemValue;
    }

    /**
    * 组成申请数据的sql
    * 
    * @param tab
    * @param fDayDataTmp
    * @param sTdate
    * @param eTdate
    * @param otherField
    * @param codewhere
    * @return
    */
    public String getBusiSQL(String tab, String fDayDataTmp, String sTdate, String eTdate, String otherField, String codewhere) {
        StringBuffer sql = new StringBuffer();
        if ("q15".equalsIgnoreCase(tab) && "q1519".equalsIgnoreCase(otherField)) {
            sql.append("select q1501 as applyID ,q1503 as applyType,q1507 as applyContent,q15z1 FromTime,q15z3 as ToTime");
            sql.append(" from q15 where q15z0='01' and q15z5='03'");
            sql.append(" AND EXISTS(SELECT 1 FROM Q15 t WHERE q15z0='01' and q15z5='03' AND q15.q1519 = t.q1501");

            if (codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            sql.append(kq_app_dateSQL(tab, sTdate, eTdate));
            if ("q15".equalsIgnoreCase(tab) && !"q1519".equalsIgnoreCase(otherField)) {
                sql.append(" and " + Sql_switcher.isnull("q1517", "0") + "=0");

            }

            if ("101".equals(this.analyseType)) {
                String pub_desT_where = "cur_user='" + this.userView.getUserName() + "' ";
                sql.append(" and  EXISTS(select a0100 from ");
                sql.append(fDayDataTmp);
                sql.append(" where ");
                sql.append(fDayDataTmp);
                sql.append(".a0100=");
                sql.append(tab);
                sql.append(".a0100 and ");
                sql.append(fDayDataTmp + ".nbase=" + tab + ".nbase and ");
                sql.append(pub_desT_where + ")");

            } else {
                sql.append(" and  EXISTS(select a0100 from " + fDayDataTmp + " where " + fDayDataTmp + ".a0100=" + tab
                        + ".a0100 and " + fDayDataTmp + ".nbase=" + tab + ".nbase)");
            }
            sql.append(") and " + Sql_switcher.isnull("q1517", "0") + "=1");
        } else {
            sql.append("select '" + tab + "' as applyTable, ");
            sql.append(tab + "01 as applyID ,");
            sql.append(tab + "03 as applyType,");
            sql.append(tab + "07 as applyContent,");
            sql.append(tab + "z1 FromTime,");
            sql.append(tab + "z3 as ToTime,");
            sql.append("nbase,A0100,B0110,E0122,E01A1,A0101");

            if (otherField != null && otherField.length() > 0) {
                sql.append("," + otherField);
            }

            sql.append(" from " + tab + " where");
            sql.append(" " + tab + "z0='01' and " + tab + "z5='03' ");

            if (codewhere != null && codewhere.length() > 0) {
                sql.append(" and " + codewhere);
            }

            sql.append(kq_app_dateSQL(tab, sTdate, eTdate));

            if ("q15".equalsIgnoreCase(tab) && !"q1519".equalsIgnoreCase(otherField)) {
                sql.append(" and " + Sql_switcher.isnull("q1517", "0") + "=0");
            }

            //延时加班、休息日转加班申请不用比对
            if ("q11".equalsIgnoreCase(tab)) {
                sql.append(" and q1107<>'延时加班'");
                sql.append(" and q1107<>'休息日转加班'");
            }

            if ("101".equals(this.analyseType)) {
                sql.append(" and  EXISTS(select a0100 from " + fDayDataTmp);
                sql.append(" where " + fDayDataTmp + ".a0100=" + tab + ".a0100 and ");
                sql.append(fDayDataTmp + ".nbase=" + tab + ".nbase");
                sql.append(" and cur_user='" + this.userView.getUserName() + "')");

            } else {
                sql.append(" and  EXISTS(select a0100 from " + fDayDataTmp);
                sql.append(" where " + fDayDataTmp + ".a0100=" + tab + ".a0100 and ");
                sql.append(fDayDataTmp + ".nbase=" + tab + ".nbase)");
            }
        }
        return sql.toString();
    }

    /**
     * 返回开始时间业务表的时间范围的where
     * 
     * @param app_type
     * @param start_date
     * @param end_date
     * @return
     */
    private String kq_app_dateSQL(String app_type, String start_date, String end_date) {
        StringBuffer selectSQL = new StringBuffer();
        selectSQL.append(" and (");
        selectSQL.append(app_type + "z1 <= " + Sql_switcher.dateValue(end_date + " 23:59:59"));
        selectSQL.append(" and " + app_type + "z3 > " + Sql_switcher.dateValue(start_date + " 00:00:00"));
        selectSQL.append(")");
        return selectSQL.toString();
    }

    /**
     * 判断申请类型
     * @param apptype
     * @param element
     * @return
     */
    public boolean checkAppType(String apptype, String element) {
        if (element == null) {
            return false;
        }
        if (apptype == null) {
            return false;
        }
        String f_element = element.substring(0, 1);
        if (!apptype.equals(f_element)) {
            return false;
        }
        return true;
    }

    /**
     * 相差分钟
     * @param start_date
     * @param end_date
     * @return
     */
    public long getPartMinute(Date start_date, Date end_date) {
        int sY = DateUtils.getYear(start_date);
        int sM = DateUtils.getMonth(start_date);
        int sD = DateUtils.getDay(start_date);
        int sH = DateUtils.getHour(start_date);
        int smm = DateUtils.getMinute(start_date);

        int eY = DateUtils.getYear(end_date);
        int eM = DateUtils.getMonth(end_date);
        int eD = DateUtils.getDay(end_date);
        int eH = DateUtils.getHour(end_date);
        int emm = DateUtils.getMinute(end_date);
        GregorianCalendar d1 = new GregorianCalendar(sY, sM - 1, sD, sH, smm);
        GregorianCalendar d2 = new GregorianCalendar(eY, eM - 1, eD, eH, emm);
        Date date1 = d1.getTime();
        Date date2 = d2.getTime();
        long l1 = date1.getTime();
        long l2 = date2.getTime();
        long part = (l2 - l1) / (60 * 1000L);
        return part;
    }

    /**
     * 相差小时
     * @param start_date
     * @param end_date
     * @return
     */
    public float getHourSpan(Date start_date, Date end_date) {
        int sY = DateUtils.getYear(start_date);
        int sM = DateUtils.getMonth(start_date);
        int sD = DateUtils.getDay(start_date);
        int sH = DateUtils.getHour(start_date);
        int smm = DateUtils.getMinute(start_date);

        int eY = DateUtils.getYear(end_date);
        int eM = DateUtils.getMonth(end_date);
        int eD = DateUtils.getDay(end_date);
        int eH = DateUtils.getHour(end_date);
        int emm = DateUtils.getMinute(end_date);
        GregorianCalendar d1 = new GregorianCalendar(sY, sM - 1, sD, sH, smm);
        GregorianCalendar d2 = new GregorianCalendar(eY, eM - 1, eD, eH, emm);
        Date date1 = d1.getTime();
        Date date2 = d2.getTime();
        long l1 = date1.getTime();
        long l2 = date2.getTime();
        long part = (l2 - l1) / (60 * 1000L);
        float f = Float.parseFloat(part + "");
        f = f / 60;
        return f;
    }

    public Date[] checkNoCardForBusi(Date busiB, Date busiE, RowSet cardData, Date cardB, Date cardE) {
        cardB = busiB;
        cardE = busiE;
        Date[] cardDs = new Date[2];
        try {
            if (cardData.next()) {
                cardDs[0] = busiB;
                String work_date = cardData.getString("work_date");
                String work_time = cardData.getString("work_time");
                //					cardDs[1]=DateUtils.getDate(work_date+" "+work_time,"yyyy.MM.dd HH:mm");  生成时间有问题， wangy
                work_date = work_date.replaceAll("\\.", "-");
                cardDs[1] = DateUtils.getTimestamp(work_date + " " + work_time, "yyyy-MM-dd HH:mm");
            } else {
                cardDs[0] = busiB;
                cardDs[1] = busiE;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cardDs;
    }

    public Date[] checkCardDataForBusi(Date busiB, Date busiE, RowSet cardData, boolean isOverTime, Date cardB, Date cardE,
            UnKqClassBean classbean) {
        Date[] cardDs = new Date[2];
        if (cardData == null) {
            return cardDs;
        }
        float timeLenB = -1;
        float timeLenE = -1;
        boolean findB = false;
        boolean findE = false;
        String work_date = "";
        String work_time = "";
        int rowCount = 0;
        Date preTime = null;
        Date cardTime;
        try {
            cardData.last();
            rowCount = cardData.getRow();
            cardData.first();
            if (rowCount == 2) //有两次刷卡，则认为是加班的开始与结束
            {
                work_date = cardData.getString("work_date");
                work_time = cardData.getString("work_time");
                cardB = DateUtils.getDate(work_date + " " + work_time, "yyyy.MM.dd HH:mm");
                cardData.next();
                work_date = cardData.getString("work_date");
                work_time = cardData.getString("work_time");
                cardE = DateUtils.getDate(work_date + " " + work_time, "yyyy.MM.dd HH:mm");
            } else if (rowCount == 1)//有一次刷卡
            {
                work_date = cardData.getString("work_date");
                work_time = cardData.getString("work_time");
                cardTime = DateUtils.getDate(work_date + " " + work_time, "yyyy.MM.dd HH:mm");
                if (getPartMinute(cardTime, busiB) >= 0)//在申请开始时间前
                {
                    cardB = cardTime;
                } else if (getPartMinute(cardTime, busiE) <= 0)//在申请结束时间后
                {
                    cardE = cardTime;
                } else //在申请时段内
                {
                    timeLenB = getHourSpan(busiB, cardTime);
                    timeLenE = getHourSpan(cardTime, busiE);
                    if (timeLenB <= timeLenE) {
                        cardB = cardTime;
                    } else {
                        cardE = cardTime;
                    }
                }
            } else //有多次刷卡，查找与申请起止时间最接近的
            {
                cardData.beforeFirst();
                while (cardData.next()) {
                    if (findB && findE) {
                        break;
                    }

                    work_date = cardData.getString("work_date");
                    work_time = cardData.getString("work_time");
                    cardTime = DateUtils.getDate(work_date + " " + work_time, "yyyy.MM.dd HH:mm");

                    //inoutFlag=cardData.getString("inoutFlag");
                    if (!findB)//起始时间还未找到
                    {
                        if (Math.abs(getPartMinute(cardTime, busiB)) < 0.01)//绝对时间相差较小
                        {
                            cardB = cardTime;
                            findB = true;
                            timeLenB = -1;
                            timeLenE = -1;
                        } else if (getPartMinute(cardTime, busiB) <= 0)//刷卡迟于申请
                        {
                            if (timeLenB == -1) {
                                cardB = cardTime;
                                findB = true;
                                continue;//数据集保持在当前，下一循环开始寻找结束刷卡时间
                            } else {
                                timeLenE = getPartMinute(busiB, cardTime);
                                if (timeLenE > timeLenB)//上一次更接近申请 
                                {
                                    cardB = preTime;
                                    findB = true;
                                    timeLenB = -1;
                                    timeLenE = -1;
                                    cardData.previous();
                                } else {
                                    cardB = cardTime;
                                    findB = true;
                                    timeLenB = -1;
                                    timeLenE = -1;
                                    preTime = null;
                                    continue;//数据集保持在当前，下一循环开始寻找结束刷卡时间
                                }
                            }
                        } else {
                            timeLenB = getPartMinute(cardTime, busiB); //记录下本次刷卡情况，下一循环将与之比较
                            preTime = cardTime;
                            if (timeLenB == -1) {
                                timeLenB = getPartMinute(cardTime, busiB);
                                preTime = cardTime;
                                cardB = cardTime;
                            } else {
                                timeLenE = getPartMinute(cardTime, busiB);
                                preTime = cardTime;
                                cardB = cardTime;
                                timeLenB = timeLenE;
                            }
                        }
                    } else//结束时间还未找到
                    {
                        if (Math.abs(getPartMinute(cardTime, busiE)) < 0.01)//绝对时间相差较小
                        {
                            cardE = cardTime;
                            break;
                        } else if (getPartMinute(cardTime, busiE) < 0)//刷卡晚于申请结束时间
                        {
                            cardE = cardTime;
                            findE = true;
                            break;
                        } else//刷卡早于申请结束时间
                        {
                            if (timeLenB == -1)//第一次寻找结束时间，记录下本次刷卡情况，下一循环将与之比较
                            {
                                timeLenB = getPartMinute(cardTime, busiE);
                                preTime = cardTime;
                                cardE = cardTime;
                            } else//与上一次刷卡数据比较
                            {
                                timeLenE = getPartMinute(cardTime, busiE);
                                if (timeLenB < timeLenE) //上一次更接近申请结束时间
                                {
                                    cardE = preTime;
                                    findE = true;
                                    break;
                                } else//本次更接近，则继续寻找
                                {
                                    timeLenB = timeLenE;
                                    preTime = cardTime;
                                    cardE = cardTime;
                                }
                            }
                        }
                    }
                }
            }
            cardDs[0] = cardB;
            cardDs[1] = cardE;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cardDs;
    }

    /**
     * 得到指定数据，的数据集
     * @param analyse
     * @param nbase
     * @param a0100
     * @param q03z0
     * @return
     */
    public KqEmpClassBean getOneFEmpQry(String analyse, String nbase, String a0100, String q03z0) {
        StringBuffer sql = new StringBuffer();
        sql.append("select * from " + analyse);
        sql.append(" where nbase='" + nbase + "' and a0100='" + a0100 + "' and q03z0='" + q03z0 + "'");
        KqEmpClassBean bean = null;
        RowSet rs = null;
        try {
            rs = this.dao.search(sql.toString());
            if (rs.next()) {
                bean = new KqEmpClassBean();
                bean = bean.getKqEmpClassBean(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return bean;
    }

    public String getNewID(String table) {
        String id = "";
        RowSet rs = null;
        try {
            String sql = "select Max(id) as id from " + table;
            rs = this.dao.search(sql);
            if (rs.next()) {
                id = rs.getString("id");
                if (id != null && id.length() > 0) {
                    id = (Integer.parseInt(id) + 1) + "";
                } else {
                    id = "1";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return id;
    }

    /**
     * 数据处理集中处理 异常处理把人员数据放到一张表在传入存储过程 wangy
     * @param table
     * @return
     */
    public String analyseTableTmp(String table) throws GeneralException {
        String table_name = table;
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(table_name);
        
        Table tables = new Table(table_name);
        Field temp = new Field("User_Name", "用户名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        tables.addField(temp);
        Field temp1 = new Field("NBase", "人员库前缀");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(3);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        tables.addField(temp1);
        Field temp2 = new Field("A0100", "人员编号");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(8);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        tables.addField(temp2);
        try {
            dbWizard.createTable(tables);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return table_name;
    }

    /**
     * 数据处理集中处理 异常处理把时间放到一张表在传入存储过程 wangy
     * @param tabled
     * @return
     * @throws GeneralException
     */
    public String analysedateTableTmp(String tabled) throws GeneralException {
        String table_name = tabled;
        table_name = table_name.toLowerCase();
        DbWizard dbWizard = new DbWizard(this.conn);
        dbWizard.dropTable(table_name);
        
        Table tables = new Table(table_name);
        Field temp = new Field("User_Name", "用户名");
        temp.setDatatype(DataType.STRING);
        temp.setLength(50);
        temp.setKeyable(false);
        temp.setVisible(false);
        tables.addField(temp);
        Field temp1 = new Field("BEGIN_DATE", "处理起始日期");
        temp1.setDatatype(DataType.STRING);
        temp1.setLength(10);
        temp1.setKeyable(false);
        temp1.setVisible(false);
        tables.addField(temp1);
        Field temp2 = new Field("END_DATE", "处理结束日期");
        temp2.setDatatype(DataType.STRING);
        temp2.setLength(10);
        temp2.setKeyable(false);
        temp2.setVisible(false);
        tables.addField(temp2);
        try {
            dbWizard.createTable(tables);
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return table_name;
    }

    public String getAnalyseType() {
        return analyseType;
    }

    public void setAnalyseType(String analyseType) {
        this.analyseType = analyseType;
    }
}
