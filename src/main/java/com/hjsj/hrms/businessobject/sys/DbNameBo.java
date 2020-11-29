/**
 * 
 */
package com.hjsj.hrms.businessobject.sys;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.businessobject.general.inform.CorField;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzItemVo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.module.template.templatecard.businessobject.TempletChgLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * <p>Title:DbNameBo</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2005-12-15:11:23:26</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class DbNameBo {
    private Connection conn=null;
    private UserView userView=null;
    public DbNameBo(Connection conn) {
        this.conn=conn;
    }
    public DbNameBo(Connection conn,UserView userView) {
        this.conn=conn;
        this.userView = userView;
    }
    public String redundantInfo="";//超编
    //校验黑名单时，操作的人员库
    public String nbase = "";
    
    
    /**
     * 取得主键id最大值（静态加同步）
     * @param table
     * @param columnName
     * @param dbconn
     * @return
     * @throws GeneralException
     */
    static synchronized public int getPrimaryMaxKey(String table,String columnName,Connection dbconn)throws GeneralException
    {
        int num=0;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            RowSet rowSet=dao.search("select max("+columnName+") from "+table);
            if(rowSet.next())
            {
                num=rowSet.getInt(1);
            }
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return num;
    }
    
    
    
    /** 追加记录至个税明细表 */
    static synchronized public void appendRecordTaxMx(String sql,String tablename,String column,Connection dbconn)throws GeneralException
    {
        int num=0;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            RowSet rowSet=dao.search("select max("+column+") from "+tablename);
            if(rowSet.next())
            {
                num=rowSet.getInt(1);
            }
            sql=sql.replaceAll("X~X",String.valueOf(num));
            dao.update(sql);
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    /**----------------     薪资追加个税明细       ------------------------*/
    
    //追加记录至个税明细表
    static synchronized public void  appendRecordTaxMx2(LazyDynaBean paramBean ,Connection conn,UserView userview)
    {
        
        try
        {
            updateMusterRecidx(conn,userview);
            HashMap salarySetMap=(HashMap)paramBean.get("salarySetMap"); //getSalarySetMap();
            LazyDynaBean dbean=(LazyDynaBean)paramBean.get("dbean");
            String gz_tablename=(String)paramBean.get("gz_tablename");
            String salaryid=(String)paramBean.get("salaryid");
            String tax_date_item=(String)paramBean.get("tax_date_item");
            String declare_date_item=(String)paramBean.get("declare_date_item");
            String tax_mode_item=(String)paramBean.get("tax_mode_item");
            String tax_mode=(String)paramBean.get("tax_mode");
            String desc_item=(String)paramBean.get("desc_item");
            ArrayList taxfldlist=(ArrayList)paramBean.get("taxfldlist");
            String k_base=(String)paramBean.get("k_base");
            String calculation=(String)paramBean.get("calculation");
            boolean isDeptControl=((Boolean)paramBean.get("isDeptControl")).booleanValue();
            SalaryCtrlParamBo ctrlparam=(SalaryCtrlParamBo)paramBean.get("ctrlparam");

            ContentDAO dao = new ContentDAO(conn);
            int maxid=getPrimaryMaxKey("gz_tax_mx","tax_max_id",conn);;

            String fieldname=(String)dbean.get("itemname");

            StringBuffer insert_sql=new StringBuffer("insert into gz_tax_mx (tax_max_id,salaryid,nbase,a0100,a00z2,a00z3,a00z0,a00z1,a0000,b0110,e0122,a0101,userflag");
            String tableName=gz_tablename;
            StringBuffer sql=new StringBuffer("select gtt.id+"+maxid+","+salaryid+",gtt.nbase,gtt.a0100,s.a00z2,s.a00z3,s.a00z0,s.a00z1,s.a0000,s.b0110,s.e0122,s.a0101");

            sql.append(",s.userflag");
        /*  if(this.manager.length()>0&&!this.userview.getUserName().equalsIgnoreCase(this.manager))
                    sql.append(",'"+this.manager+"'");
            else
                    sql.append(",'"+this.userview.getUserName()+"'");
            */
            if(tax_date_item!=null&&tax_date_item.length()>0)
            {
                sql.append(",s."+tax_date_item);      /**计税时间指标*/
                insert_sql.append(",tax_date");
            }
            if(declare_date_item!=null&&declare_date_item.length()>0)
            {
                sql.append(",s."+declare_date_item);  /**报税时间指标*/
                insert_sql.append(",declare_tax");
            }
            if(tax_mode_item!=null&&tax_mode_item.length()>0)
            {
               // sql.append(",s."+tax_mode_item);      /**计算方式指标*/
            	sql.append(","+tax_mode);
            	insert_sql.append(",taxmode");
            }
            if(desc_item!=null&&desc_item.length()>0)
            {
                sql.append(",s."+desc_item);          /**纳税项目说明*/
                insert_sql.append(",description");
            }
            String tax_unit=(String)paramBean.get("tax_unit");  // 1:有计税单位
            if(tax_unit!=null&&tax_unit.trim().length()>0)
            {
            		insert_sql.append(",taxunit");
            		sql.append(",s."+tax_unit);
            }


            sql.append(",gtt.taxitem,"+Sql_switcher.isnull("gtt.ynsd","0")+"+"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+",gtt.sskcs,"+k_base);
            if("Y".equals(calculation))//累计预扣法
            {
                sql.append(","+k_base+"*kcbase_num,gtt.sl,");
            } else {
                sql.append(",0,gtt.sl,");
            }
           //sql.append("case when "+Sql_switcher.round("gtt.gs", 2)+"<0 then 0 else "+Sql_switcher.round("gtt.gs", 2)+" end ");//如果业务字段存在则取该指标实际小数位，否则默认4位 zhaoxg add 2016-12-19
            sql.append(Sql_switcher.round("gtt.gs", 2));

            if(!gz_tablename.equalsIgnoreCase("t#"+userview.getUserName()+"_gzsp")) {
                sql.append(",0");
            } else {
                sql.append(",1");
            }
        //  sql.append(",0");
            insert_sql.append(",taxitem,ynse,sskcs,basedata,lj_basedata,Sl,Sds,flag");
            for(int i=0;i<taxfldlist.size();i++)
            {
                Field field=(Field)taxfldlist.get(i);
                if(salarySetMap.get(field.getName().toLowerCase())!=null)
                {
                    insert_sql.append(","+field.getName());
                    sql.append(",s."+field.getName());
                }
            }

            //按业务划分操作单位 所得税管理根据指定的dept_id字段进行权限控制
            if(isDeptControl)
            {
                if(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim().length()>0)
                {
                    insert_sql.append(",deptid");
                    sql.append(",S.");
                    sql.append(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim());
                }
            }

            insert_sql.append(",ynse_field");
            sql.append(",'"+fieldname/*this.ynse_item*/+"'");


            if("Y".equals(calculation))//累计预扣法
            {
            	insert_sql.append(",ljsde,ljse");

            	sql.append(",("+Sql_switcher.sqlNull("gtt.ynsd",0)+"+"+Sql_switcher.sqlNull("gtt.gs",0)+"+"+Sql_switcher.sqlNull("gtt.ynse_sd",0)+"-"+k_base+"*kcbase_num)");
            //	sql.append(",("+Sql_switcher.sqlNull("gtt.yjgs",0)+"+"+Sql_switcher.sqlNull("gtt.gs",0)+")");
            	//2019/11/05
            	sql.append(" ,ljse ");
            }
            insert_sql.append(",ynssde");
            //sql.append(",("+Sql_switcher.isnull("gtt.ynsd","0")+"+"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+"-"+k_base+")");
            sql.append(",(case when yyjl=1 then "+Sql_switcher.isnull("gtt.ynsd","0")+"+"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+" else "+Sql_switcher.isnull("gtt.ynsd","0")+"+"+Sql_switcher.round(Sql_switcher.isnull("gtt.gs","0"),2)+"-"+k_base+" end )");

            sql.append(" from t#"+userview.getUserName()+" gtt,"+tableName+" s ");
            sql.append(" where gtt.a0100=s.a0100 and gtt.nbase=s.nbase and gtt.tax_z0=s.a00z0 and gtt.tax_z1=s.a00z1 ");
            if(tax_unit!=null&&tax_unit.trim().length()>0) {
                sql.append(" and gtt.taxunit="+Sql_switcher.sqlNull("s."+tax_unit,"-1"));
            }
            dao.update(insert_sql.toString()+") "+sql.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }



    /***
     * 追加记录至个税明细表
     * @throws GeneralException
     */
    static synchronized public void appendRecordTaxMx(LazyDynaBean paramBean ,Connection conn,
            UserView userview)throws GeneralException
    {
        try
        {
            String tmp2_name=(String)paramBean.get("tmp2_name");
            LazyDynaBean dbean=(LazyDynaBean)paramBean.get("dbean");
            String desc_item=(String)paramBean.get("desc_item");
            boolean isDeptControl=((Boolean)paramBean.get("isDeptControl")).booleanValue();
            String gz_tablename=(String)paramBean.get("gz_tablename");
            String manager=(String)paramBean.get("manager");
            RecordVo templatevo=(RecordVo)paramBean.get("templateVo");
            SalaryCtrlParamBo ctrlparam=(SalaryCtrlParamBo)paramBean.get("ctrlparam");
            String controlByUnitcod=(String)paramBean.get("controlByUnitcod");
            String whlByUnits=(String)paramBean.get("whlByUnits");
            String k_base=(String)paramBean.get("k_base");
            String declare_date_item=(String)paramBean.get("declare_date_item");
            String salaryid=(String)paramBean.get("salaryid");
            String tax_mode=(String)paramBean.get("tax_mode");
            String calculation=(String)paramBean.get("calculation");
            ArrayList taxfldlist=(ArrayList)paramBean.get("taxfldlist");
            DbWizard dbw=new DbWizard(conn);
            String fieldname=(String)dbean.get("itemname");
            /**设置临时表主键*/
            setTmp2PrimaryKey(tmp2_name,conn,userview);
            /**取得个税明细表额外定义的字段*/
            StringBuffer extFlds=new StringBuffer();
            StringBuffer extValues=new StringBuffer();
            getExtFlds(extFlds,extValues,conn,gz_tablename,taxfldlist);
            /**项目描述*/
            if(!(desc_item==null||desc_item.length()==0))
            {
                extFlds.append(",description");
                extValues.append(",S.");
                extValues.append(desc_item);
            }

            //按业务划分操作单位 所得税管理根据指定的dept_id字段进行权限控制
            if(isDeptControl)
            {
                if(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT)!=null&&ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim().length()>0)
                {
                    extFlds.append(",deptid");
                    extValues.append(",S.");
                    extValues.append(ctrlparam.getValue(SalaryCtrlParamBo.LS_DEPT).trim());
                }
            }

            String tax_unit=(String)paramBean.get("tax_unit");  // 1:有计税单位
            if("1".equals(tax_unit))
            {
            	 extFlds.append(",taxunit");
                 extValues.append(",nullif(temp2.taxunit,'-1')");
            }

            /**求个税明细表中tax_max_id字段最大值*/

            String aflag=ctrlparam.getValue(SalaryCtrlParamBo.PRIV_MODE,"flag");  // 人员范围权限过滤标志  1：有
            //共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
            String atableName="t#"+userview.getUserName()+"_gzsp"; //this.userview.getUserName()+"_sp_data";
            if(!gz_tablename.equalsIgnoreCase(atableName)&&manager.length()>0&&!userview.getUserName().equalsIgnoreCase(manager)&&aflag!=null&& "1".equals(aflag))
            {
                String dbpres=templatevo.getString("cbase");
                //应用库前缀
                String[] dbarr=StringUtils.split(dbpres, ",");
                for(int i=0;i<dbarr.length;i++)
                {
                    String pre=dbarr[i];
                    StringBuffer tempSql=new StringBuffer(" where upper(temp2.nbase)='"+pre.toUpperCase()+"'" );
                    //权限过滤
                    if("1".equals(controlByUnitcod))
                    {
                        String whl_str=whlByUnits;
                        if(whl_str.length()>0)
                        {
                            tempSql.append(" and  temp2.a0100 in ( select a0100 from "+gz_tablename+" where 1=1 "+whl_str+" and  upper(nbase)='"+pre.toUpperCase()+"' ) ");
                        }
                    }
                    else
                    {
                        String whereIN=InfoUtils.getWhereINSql(userview,pre);
                        whereIN="select a0100 "+whereIN;
                        tempSql.append(" and temp2.a0100 in ( "+whereIN+" )");
                    }
              //    int nmax=getMaxTax_maxid();
                    StringBuffer buf=new StringBuffer();
                    buf.append("insert into gz_tax_mx(tax_max_id,NBASE,A0100,A00Z2,A00Z3,A00Z0,A00Z1,A0000,B0110,E0122,A0101,userflag,");
                    buf.append("tax_date,taxitem,sskcs,ynse,basedata,lj_basedata,sl,sds,");
                    buf.append("declare_tax,salaryid,taxmode,flag,ynse_field");

                    if("Y".equals(calculation))//累计预扣法
                    {
                    	 buf.append(",ljsde,ljse");
                    }
                    buf.append(",ynssde");
                    buf.append(extFlds.toString());
                    //20190626
                    buf.append(",allowance");
                    buf.append(")");
                    buf.append(" select tax_max_id+X~X");
                //  buf.append(nmax);
                    buf.append(",S.NBASE,S.A0100,S.A00Z2,S.A00Z3,S.A00Z0,S.A00Z1,S.A0000,");
                    buf.append("S.B0110,S.E0122,S.A0101,'"+manager+"',");
                    buf.append("temp2.tax_date,temp2.taxitem,");
                    /*
                    if(calculation.equals("Y"))//累计预扣法
                    	buf.append("gz_taxrate_item.sskcs*12,temp2.ynse,");
                    else*/
                    	buf.append("gz_taxrate_item.sskcs,temp2.ynse,");
                    buf.append(k_base+",");

                    if("Y".equals(calculation))//累计预扣法
                    {
                        buf.append(k_base+"*kcbase_num");
                    } else {
                        buf.append("0");
                    }

                    buf.append(",gz_taxrate_item.sl,");
        			FieldItem item=DataDictionary.getFieldItem(fieldname);
        			int len = 4;
        			if(item!=null){
        				len = item.getDecimalwidth()>4?4:item.getDecimalwidth();//因为gz_tax_mx中sds长度固定是4
        			}
                    buf.append("case when "+Sql_switcher.round("temp2.sds", len)+"<0 then 0 else "+Sql_switcher.round("temp2.sds", len)+" end ");//如果业务字段存在则取该指标实际小数位，否则默认4位 zhaoxg add 2016-12-19
                    buf.append(",S.");
                    buf.append(declare_date_item);
                    buf.append(",");
                    buf.append(salaryid);
                    buf.append(",'");
                    buf.append(tax_mode);
                    buf.append("',0,'"+fieldname+"'"); //    this.ynse_item+"'");

                    if("Y".equals(calculation))//累计预扣法
                    {
                    	 buf.append(",("+Sql_switcher.sqlNull("temp2.taxableSum",0)+"+"+Sql_switcher.sqlNull("temp2.ynse",0)+"-"+k_base+"*kcbase_num)");
                    	// buf.append(","+Sql_switcher.sqlNull("temp2.sds",0)+"+"+Sql_switcher.sqlNull("temp2.sdssum",0));
                    	//2019/11/05
                    	 buf.append(",temp2.ljse");
                    }
                    buf.append(",(case when yyjl=1 then "+Sql_switcher.sqlNull("temp2.ynse",0)+" else "+Sql_switcher.sqlNull("temp2.ynse",0)+"-"+k_base+" end)");

                    buf.append(extValues.toString());
                    //20190626
                    buf.append(",allowance");
                    buf.append(" from (");
                    buf.append(tmp2_name);
                    buf.append(" temp2 left join gz_taxrate_item ");
                    buf.append(" on temp2.taxitem=gz_taxrate_item.taxitem)");
                    buf.append(" left join ");
                    buf.append(gz_tablename);
                    buf.append(" S ");
                    buf.append(" on temp2.NBASE=S.NBASE and temp2.A0100=S.A0100 and ");
                    buf.append(" temp2.A00Z0=S.A00Z0 and temp2.A00Z1=S.A00Z1 ");
                    buf.append(tempSql.toString());
                    appendRecordTaxMx(buf.toString(),"gz_tax_mx","tax_max_id",conn);
                //  dbw.execute(buf.toString());

                }
            }
            else
            {

            //  int nmax=getMaxTax_maxid();
                StringBuffer buf=new StringBuffer();
                buf.append("insert into gz_tax_mx(tax_max_id,NBASE,A0100,A00Z2,A00Z3,A00Z0,A00Z1,A0000,B0110,E0122,A0101,userflag,");
                buf.append("tax_date,taxitem,sskcs,ynse,basedata,lj_basedata,sl,sds,");
                buf.append("declare_tax,salaryid,taxmode,flag,ynse_field");
                if("Y".equals(calculation))//累计预扣法
                {
                	 buf.append(",ljsde,ljse");
                }
                buf.append(",ynssde");
                buf.append(extFlds.toString());
              //20190626
                buf.append(",allowance");
                buf.append(")");
                buf.append(" select tax_max_id+X~X");
            //  buf.append(nmax);
                buf.append(",S.NBASE,S.A0100,S.A00Z2,S.A00Z3,S.A00Z0,S.A00Z1,S.A0000,");
                buf.append("S.B0110,S.E0122,S.A0101,S.userflag,"); //          '"+this.userview.getUserName()+"',");
                buf.append("temp2.tax_date,temp2.taxitem,");
               /*
                if(calculation.equals("Y"))//累计预扣法
                	buf.append("gz_taxrate_item.sskcs*12,temp2.ynse,");
                else */
                	buf.append("gz_taxrate_item.sskcs,temp2.ynse,");
                buf.append(k_base+",");
                if("Y".equals(calculation))//累计预扣法
                {
                    buf.append(k_base+"*kcbase_num");
                } else {
                    buf.append(k_base);
                }

                buf.append(",gz_taxrate_item.sl,");
            //  buf.append(Sql_switcher.round("temp2.sds", 2));
                buf.append("case when "+Sql_switcher.round("temp2.sds", 2)+"<0 then 0 else "+Sql_switcher.round("temp2.sds", 2)+" end ");//如果业务字段存在则取该指标实际小数位，否则默认4位 zhaoxg add 2016-12-19
                buf.append(",S.");
                buf.append(declare_date_item);
                buf.append(",");
                buf.append(salaryid);
                buf.append(",'");
                buf.append(tax_mode);


                if(!gz_tablename.equalsIgnoreCase(atableName)) {
                    buf.append("',0");
                } else {
                    buf.append("',1");
                }
            //  buf.append(",'"+this.ynse_item+"'");
                buf.append(",'"+fieldname+"'");

                if("Y".equals(calculation))//累计预扣法
                {
                	 buf.append(",("+Sql_switcher.sqlNull("temp2.taxableSum",0)+"+"+Sql_switcher.sqlNull("temp2.ynse",0)+"-"+k_base+"*kcbase_num)");
                //	 buf.append(","+Sql_switcher.sqlNull("temp2.sds",0)+"+"+Sql_switcher.sqlNull("temp2.sdssum",0));
                	//2019/11/05
                	 buf.append(",temp2.ljse");
                }
             // buf.append(",("+Sql_switcher.sqlNull("temp2.ynse",0)+"-"+k_base+")");
                buf.append(",(case when yyjl=1 then "+Sql_switcher.sqlNull("temp2.ynse",0)+" else "+Sql_switcher.sqlNull("temp2.ynse",0)+"-"+k_base+" end)");
                buf.append(extValues.toString());
                //20190626
                buf.append(",allowance");
                buf.append(" from (");
                buf.append(tmp2_name);
                buf.append(" temp2 left join gz_taxrate_item ");
                buf.append(" on temp2.taxitem=gz_taxrate_item.taxitem)");
                buf.append(" left join ");
                buf.append(gz_tablename);
                buf.append(" S ");
                buf.append(" on temp2.NBASE=S.NBASE and temp2.A0100=S.A0100 and ");
                buf.append(" temp2.A00Z0=S.A00Z0 and temp2.A00Z1=S.A00Z1 ");
                appendRecordTaxMx(buf.toString(),"gz_tax_mx","tax_max_id",conn);
                //dbw.execute(buf.toString());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }


    /**
     * 更新gz_taxTable序号
     * @param name
     */
    static synchronized public void updateMusterRecidx(Connection conn,UserView userview)// throws GeneralException
    {
        StringBuffer strsql=new StringBuffer();
        try
        {
            DbWizard db=new DbWizard(conn);
            ContentDAO dao = new ContentDAO(conn);
            String tablename="t#"+userview.getUserName(); //"gz_taxTable_"+this.userview.getUserName();
            switch(Sql_switcher.searchDbServer())
            {
                case Constant.MSSQL:
                    strsql.append("alter table "+tablename+" ");
                    strsql.append(" add xxx int identity(1,1)");
                    break;
                default:
                        if(isSequence(Sql_switcher.searchDbServer(),userview.getUserName()+"xxx",conn))
                        {
                            db.execute("drop sequence "+userview.getUserName()+"xxx");
                        }
                        strsql.append("create sequence "+userview.getUserName()+"xxx increment by 1 start with 1");
                    break;
            }

            if(Sql_switcher.searchDbServer()==Constant.MSSQL)
            {
                RowSet rowSet=dao.search("select * from "+tablename+" where 1=2");
                ResultSetMetaData mt=rowSet.getMetaData();
                boolean isExist=false;
                for(int i=0;i<mt.getColumnCount();i++)
                {
                    if("xxx".equalsIgnoreCase(mt.getColumnName(i+1))) {
                        isExist=true;
                    }
                }
                if(!isExist) {
                    db.execute(strsql.toString());
                }
            }
            else {
                db.execute(strsql.toString());
            }
            strsql.setLength(0);
            switch(Sql_switcher.searchDbServer())
            {
                case Constant.MSSQL:
                    strsql.append("update "+tablename+" set id=xxx");
                    break;
                case Constant.DB2:
                    strsql.append("update "+tablename+" set id=nextval for "+userview.getUserName()+"xxx");
                    break;
                case Constant.ORACEL:
                    strsql.append("update "+tablename+" set id="+userview.getUserName()+"xxx.nextval");
                    break;
                default:
                    strsql.append("update "+tablename+" set id=xxx");
                    break;
            }
            db.execute(strsql.toString());
            strsql.setLength(0);
            switch(Sql_switcher.searchDbServer())
            {
                case Constant.MSSQL:
                    strsql.append("alter table "+tablename+" drop column xxx");
                    break;
                default:
                    strsql.append(" drop sequence "+userview.getUserName()+"xxx");
                    break;
            }
            db.execute(strsql.toString());
        }
        catch(Exception ex)
        {

        }

    }


    static synchronized public boolean isColumn(String tableName,String columnname,Connection conn)
    {
        boolean flag=false;
        ContentDAO dao = new ContentDAO(conn);
        try
        {
            RowSet rowSet=dao.search("select * from "+tableName+" where 1=2");
            ResultSetMetaData data=rowSet.getMetaData();
            for(int i=0;i<data.getColumnCount();i++)
            {
                if(data.getColumnName(i+1).equalsIgnoreCase(columnname))
                {
                    flag=true;
                    break;
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return flag;
    }

    static synchronized public boolean isSequence(int dbflag,String name,Connection conn)
    {
        boolean flag=false;
        try
        {
            ContentDAO dao = new ContentDAO(conn);
            if(dbflag==Constant.ORACEL){
                RowSet rowSet=dao.search("select sequence_name from user_sequences where lower(sequence_name)='"+name+"'");
                if(rowSet.next()) {
                    flag=true;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 设置临时表的主键字段，自动增长类型
     */
    static synchronized  private void setTmp2PrimaryKey(String tmp2_name,Connection conn,UserView userview)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        DbWizard dbw=new DbWizard(conn);
        try
        {

            if(isColumn(tmp2_name,"tax_max_id",conn))
            {
                Table table=new Table(tmp2_name);
                Field field=new Field("tax_max_id","tax_max_id");
                field.setDatatype(DataType.INT);
                field.setLength(10);
                table.addField(field);
                dbw.dropColumns(table);
            }

        //  if(!isColumn(tmp2_name,"tax_max_id",conn))
            {
                switch(Sql_switcher.searchDbServer())
                {
                case 1://MSSQL
                    buf.append("alter table ");
                    buf.append(tmp2_name);
                    buf.append(" add tax_max_id int identity(1,1)");
                    dbw.execute(buf.toString());
                    break;
                case 2://ORACLE
                case 3://DB2
                    Table table=new Table(tmp2_name);
                    Field field=new Field("tax_max_id","tax_max_id");
                    field.setDatatype(DataType.INT);
                    field.setLength(10);
                    table.addField(field);
                    dbw.addColumns(table);

                    if(isSequence(Sql_switcher.searchDbServer(),userview.getUserName()+"s_tax_max_id",conn))
                    {
                         dbw.execute("drop sequence "+userview.getUserName()+"s_tax_max_id");
                    }
                    buf.append("create sequence "+userview.getUserName()+"s_tax_max_id increment by 1 start with 1");
                    dbw.execute(buf.toString());

                    buf.setLength(0);
                    buf.append("update ");
                    buf.append(tmp2_name);
                    buf.append(" set tax_max_id=");
                    buf.append(Sql_switcher.sql_NextVal(userview.getUserName()+"s_tax_max_id"));
                    dbw.execute(buf.toString());
                    buf.setLength(0);
                    buf.append("drop sequence "+userview.getUserName()+"s_tax_max_id");
                    dbw.execute(buf.toString());
                    break;
                }//switch end.
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    /**
     * 取得动态字段更新语句
     * @param extflds
     * @param extvalues
     */
    static synchronized public void getExtFlds(StringBuffer extflds,StringBuffer extvalues,Connection conn,String gz_tablename,ArrayList taxfldlist)
    {
        try
        {

            ContentDAO dao=new ContentDAO(conn);
            RowSet rowSet=dao.search("select * from gz_tax_mx where 1=2");
            ResultSetMetaData mt=rowSet.getMetaData();
            int columns=mt.getColumnCount();
            HashMap map=new HashMap();
            for(int i=0;i<columns;i++)
            {
                map.put(mt.getColumnName(i+1).toLowerCase(),"1");
            }

        //  RecordVo vo=new RecordVo("gz_tax_mx");
            RecordVo vo2=new RecordVo(gz_tablename.toLowerCase());
            String str=",NBASE,A0100,A00Z2,A00Z3,A00Z0,A00Z1,A0000,B0110,E0122,A0101,";
            for(int i=0;i<taxfldlist.size();i++)
            {
                Field field=(Field)taxfldlist.get(i);
                String fieldname=field.getName();
                if(str.indexOf(","+fieldname.toUpperCase()+",")!=-1) {
                    continue;
                }
            //  if(vo.hasAttribute(fieldname.toLowerCase())&&vo2.hasAttribute(fieldname.toLowerCase()))
                if(map.get(fieldname.toLowerCase())!=null&&vo2.hasAttribute(fieldname.toLowerCase()))
                {
                    extflds.append(",");
                    extflds.append(fieldname);
                    extvalues.append(",");
                    extvalues.append("S.");
                    extvalues.append(fieldname);
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }


    /**--------------------  end ---------------------*/



    /**
     * 新增记录（静态加同步）
     * @param table
     * @param columnName
     * @param dbconn
     * @author dengcan
     * @return
     * @throws GeneralException
     */
    static synchronized public  void insertNewRecord(String table,String columnName,Connection dbconn,ArrayList info)throws GeneralException
    {
        int num=0;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            RowSet rowSet=dao.search("select max("+columnName+") from "+table);
            if(rowSet.next())
            {
                num=rowSet.getInt(1);
            }
            num++;
            Calendar d=Calendar.getInstance();
            String str1="";
            String str2="";
            LazyDynaBean abean=null;
            ArrayList valueList=new ArrayList();
            for(int i=0;i<info.size();i++)
            {
                d=Calendar.getInstance();
                abean=(LazyDynaBean)info.get(i);
                String itemid=(String)abean.get("itemid");
                String type=(String)abean.get("type");
                String decimal=(String)abean.get("decimal");
                String value=(String)abean.get("value");
                str1+=","+itemid;
                str2+=",?";
                if(value==null||value.trim().length()==0) {
                    valueList.add(null);
                } else
                {
                    if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type)) {
                        valueList.add(value);
                    } else if("D".equalsIgnoreCase(type))
                    {
                        String[] temp=value.split("-");
                        d.set(Calendar.YEAR,Integer.parseInt(temp[0]));
                        d.set(Calendar.MONTH,Integer.parseInt(temp[1])-1);
                        d.set(Calendar.DATE,Integer.parseInt(temp[2]));
                        valueList.add(d.getTime());
                    }
                    else if("N".equalsIgnoreCase(type))
                    {
                        if("0".equals(decimal)) {
                            valueList.add(new Integer(value));
                        } else {
                            valueList.add(new Double(value));
                        }
                    }

                }
            }
            StringBuffer sql=new StringBuffer("insert into "+table+"("+columnName+str1+") values ("+num+str2+")");
            dao.insert(sql.toString(),valueList);
            if(rowSet!=null) {
                rowSet.close();
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }




    /**
     * 取得主键id递增值（静态加同步）
     * @param table
     * @param columnName
     * @param dbconn
     * @return
     * @throws GeneralException
     */
    static synchronized public  int getPrimaryKey(String table,String columnName,Connection dbconn)throws GeneralException
    {
        int num=0;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            RowSet rowSet=dao.search("select max("+columnName+") from "+table);
            if(rowSet.next())
            {
                num=rowSet.getInt(1);
            }
            num++;

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return num;
    }
    /**
     * 求下一条记录的主键值
     * @param table         表名
     * @param columnName    列表
     * @param strWhere      条件
     * @param dbconn
     * @return
     * @throws GeneralException
     */
    static synchronized  public  int getPrimaryKey(String table,String columnName,String strWhere,Connection dbconn)throws GeneralException
    {
        int num=0;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            StringBuffer buf=new StringBuffer();
            buf.append("select max(");
            buf.append(columnName);
            buf.append(") from ");
            buf.append(table);
            buf.append(" ");
            buf.append(strWhere);
            RowSet rowSet=dao.search(buf.toString());
            if(rowSet.next())
            {
                num=rowSet.getInt(1);
            }
            num++;

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return num;
    }

    /**
     * 薪资发放/薪资报批 总额控制备份数据
     * @param conn
     * @param userview
     * @param gz_tablename
     * @param salaryid
     * @param manager
     * @param gzbo
     * @param approveObject
     * @throws GeneralException
     */
    synchronized static  public  void autoAddZ1_total(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,SalaryTemplateBo gzbo,String approveObject,boolean isSp)throws GeneralException
    {
        try
        {

            ContentDAO dao = new ContentDAO(conn);
            if(manager!=null&&manager.trim().length()>0) {
                dao.update("update "+gz_tablename+" set userflag='"+manager+"'");
            } else {
                dao.update("update "+gz_tablename+" set userflag='"+userview.getUserName()+"'");
            }
            String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
            StringBuffer strsql=new StringBuffer("");
            if(Sql_switcher.searchDbServer()==2) {
                strsql.append("create table "+table+" as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3 ");
            } else {
                strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  into "+table);
            }
            strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
            strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
            //strsql.append(" and lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"'  and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
            // 多次发放同一人，最后一次导入这个人 导致他的a00z1是1，然后会造成salaryhistory前1次的数据被删
            strsql.append(" and ( lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"' or ( lower(gm.userflag)='"+userview.getUserName().toLowerCase()+"'  and gm.sp_flag='06'  )) and ( ss.sp_flag='07' or ss.sp_flag='01')  ");


            DbWizard dbw=new DbWizard(conn);
            if(dbw.isExistTable(table,false))
            {
                dbw.dropTable(table);
            /*  dbw.execute("delete from "+table);
                strsql.setLength(0);
                strsql.append("insert into "+table+" (nbase,a0100,a00z1,a00z0,a00z3) ");
                strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  ");
                strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
                strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
                strsql.append(" and lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"'  and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
                dbw.execute(strsql.toString());*/
            }

        //  else
            {
                dao.update(strsql.toString());
            }



            strsql.setLength(0);
            strsql.append("update "+table+" set a00z3=(select max(salaryhistory.a00z1)+1 from salaryhistory where salaryid="+salaryid+" and  "+table+".a0100=salaryhistory.a0100 ");
            strsql.append(" and "+table+".a00z0=salaryhistory.a00z0 and lower("+table+".nbase)=lower(salaryhistory.nbase) group by salaryhistory.a0100  ) ");
            dao.update(strsql.toString());

            strsql.setLength(0);
            strsql.append("update "+gz_tablename+" set a00z1=(select a00z3 from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
            strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
            strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
            strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
            dao.update(strsql.toString());
            //------------------------------------------------------------------
            RowSet rowSet=dao.search("select * from "+gz_tablename+" where 1=2");
            ResultSetMetaData metaData=rowSet.getMetaData();
            StringBuffer s1=new StringBuffer(",salaryid,curr_user");
            StringBuffer s2=new StringBuffer(","+salaryid+",'"+approveObject+"'");
            for(int i=1;i<=metaData.getColumnCount();i++)
            {
                    if(gzbo.getStandardGzItemStr().indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1) {
                        continue;
                    }


                    if("sp_flag".equalsIgnoreCase(metaData.getColumnName(i)))
                    {
                        s1.append(","+metaData.getColumnName(i));
                        s2.append(",'02'");
                    }
                    else
                    {

                            s1.append(","+metaData.getColumnName(i));
                            s2.append(","+metaData.getColumnName(i));

                    }
            }
            StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+gz_tablename);
            del.append(" where a0100=salaryhistory.a0100 ");
            del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  and ( sp_flag='07' or sp_flag='01')  ) ");
            del.append(" and salaryid="+salaryid);
            String[] atemps=gz_tablename.toLowerCase().split("_salary_");
            del.append(" and lower(userflag)='");  //20100323
            del.append(atemps[0].toLowerCase());
            del.append("'");
            dao.delete(del.toString(),new ArrayList());

            String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+gz_tablename+" where  ( sp_flag='07' or sp_flag='01' )";
            dao.update(sql0);

            strsql.setLength(0);
            String temp_name="t#"+userview.getUserName()+"_gz"; //"salaryhistorybak_"+userview.getUserName();
            if(dbw.isExistTable(temp_name,false)) {
                dbw.dropTable(temp_name);
            }

            if(Sql_switcher.searchDbServer()==2) {
                strsql.append("create table  "+temp_name+"  as select *");
            } else {
                strsql.append("select * into "+temp_name+" ");
            }
            strsql.append(" from salaryhistory where exists (select null from "+gz_tablename);
            strsql.append(" where lower("+gz_tablename+".nbase)=lower(salaryhistory.nbase) and "+gz_tablename+".a0100=salaryhistory.a0100 ");
            if(!isSp) {
                strsql.append(" and  "+gz_tablename+".a00z0=salaryhistory.a00z0 and "+gz_tablename+".a00z1=salaryhistory.a00z1 and ( "+gz_tablename+".sp_flag='07' or "+gz_tablename+".sp_flag='06' or "+gz_tablename+".sp_flag='01'  ) ) ");  //  加上06 ,考虑重复提交
            } else {
                strsql.append(" and  "+gz_tablename+".a00z0=salaryhistory.a00z0 and "+gz_tablename+".a00z1=salaryhistory.a00z1 and ( "+gz_tablename+".sp_flag='07' or "+gz_tablename+".sp_flag='01'  ) ) ");  //or "+gz_tablename+".sp_flag='06'  加上06 ,考虑重复提交
            }
            strsql.append(" and salaryid="+salaryid);
            strsql.append(" and lower(userflag)='");
            strsql.append(userview.getUserName().toLowerCase());
            strsql.append("'");
            dao.update(strsql.toString());


            // ---------------------------- 考虑重复提交
            if(!isSp)
            {
                del.setLength(0);
                del.append("delete from salaryhistory where exists (select * from "+gz_tablename);
                del.append(" where a0100=salaryhistory.a0100 ");
                del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  and  sp_flag='06'  ) ");
                del.append(" and salaryid="+salaryid);
                atemps=gz_tablename.toLowerCase().split("_salary_");
                del.append(" and lower(userflag)='");  //20100323
                del.append(atemps[0].toLowerCase());
                del.append("'");
                dao.delete(del.toString(),new ArrayList());

                sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+gz_tablename+" where  ( sp_flag='06')";
                dao.update(sql0);
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }

    /**
     * 单位调整 划转、新增时使用 改变编码规则 2014-04-16 guodd
     * @param code 单位调整 划转到某个单位的id 或者新增机构的父节点
     * @param SrcCode  b0110 划转的机构id
     * @param conn
     * @param codesetid  如果是新增机构这个参数需要，否则传NULL
     * @return
     * @throws GeneralException
     */
    synchronized static public LazyDynaBean getCodeitem(String code,String SrcCode,Connection conn,String codesetid) throws GeneralException{
        LazyDynaBean ldb = null;
        RowSet rowset = null;
        try {
            ldb = getCodeitem(code,conn);

            if(ldb.getMap().containsKey("recode") && "1".equals(ldb.get("recode"))){

                if(codesetid != null && codesetid.trim().length()>0){
                    if("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid)) {
                        ldb.set("codeitemid", code+"01");
                    } else if("@K".equalsIgnoreCase(codesetid)) {
                        ldb.set("codeitemid", code+"001");
                    }
                }else{

                    String codeid = "";
                    String strsql="select parentid from organization where codeitemid='"+SrcCode+"'";
                    ContentDAO dao = new ContentDAO(conn);
                      rowset=dao.search(strsql);
                      String StrParentId="";
                      if(rowset.next()) {
                          StrParentId=rowset.getString("parentid");
                      }
                      for(int i=0;i<SrcCode.length()-StrParentId.length();i++){
                          codeid+="0";
                      }
                      if(codeid.length()>0){
                           codeid+="1";
                           codeid = codeid.substring(1);
                           ldb.set("codeitemid", code+codeid);
                      }

                }
            }
        } catch (GeneralException e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
                try {
                    if(rowset!=null) {
                        rowset.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return ldb;
    }

    //生成组织机构的 codeitemid and a0000
    synchronized static public LazyDynaBean getCodeitem(String code,Connection conn)throws GeneralException
    {
        //2014-04-16 guodd 生成codeitemid 方法标志
        String recode = "";//如果是外部类调用此方法则不用理会此参数(非本类getCodeitem(String code,String SrcCode,Connection conn)方法)

        String _codeitemid="";
        String codeitemids=",";
        String error="";
        String _a0000="";
        StringBuffer strsql=new StringBuffer();
        strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
        strsql.append(code);
        strsql.append("'  ");
        strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where parentid='");
        strsql.append(code);
        strsql.append("' ");
        strsql.append(" order by codeitemid desc");
        ContentDAO dao=new ContentDAO(conn);
        RowSet frowset=null;
        try{
            frowset=dao.search(strsql.toString());
            boolean b = false;
            AddOrgInfo addOrgInfo=new AddOrgInfo();
            while(frowset.next())
            {
                String chilecode=frowset.getString("codesetid");
                String codeitemid=frowset.getString("codeitemid");
                String codeitemdesc = frowset.getString("codeitemdesc");
                if(codeitemid!=null&&codeitemid.equals(code)){
                    error=codeitemdesc+"下的机构已经超出所能建的最大数！";
                    continue;
                }
                if(chilecode!=null)
                {
                    codeitemids+=codeitemid+",";

                }
            }
            frowset=dao.search(strsql.toString());
            while(frowset.next())
            {
                b = true;
                String chilecode=frowset.getString("codesetid");
                String codeitemid=frowset.getString("codeitemid");
                String corcode=frowset.getString("corcode");
                if(codeitemid!=null&&codeitemid.equals(code)&&codeitemids.length()>1)//liuyz 31619 顶级机构添加第一个自己点时不continue，
                {
                    continue;
                }
                if(chilecode!=null)
                {
                        _codeitemid=code+addOrgInfo.GetNext(codeitemid,code);
                        if(codeitemids.indexOf(","+_codeitemid+",")==-1){
                             break;
                        }
                        _codeitemid="";
                }
            }
            if(b&&_codeitemid.length()==0){

                throw GeneralExceptionHandler.Handle(new GeneralException("",error,"",""));
            }
            if(b){
                frowset=dao.search("select max(a0000)  from organization where parentid='"+code+"' and codeitemid<>parentid ");
                if(frowset.next()) {
                    _a0000=String.valueOf(frowset.getInt(1)+1);
                }

            }else
            {
                _codeitemid=code+"01";
                frowset=dao.search("select max(a0000)  from organization where codeitemid='"+code+"'  ");
                if(frowset.next()) {
                    _a0000=String.valueOf(frowset.getInt(1)+1);
                }
                recode = "1";
            }
        }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            try
            {
                if(frowset!=null) {
                    frowset.close();
                }
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        }
        LazyDynaBean bean=new LazyDynaBean();
        bean.set("codeitemid",_codeitemid);
        bean.set("a0000",_a0000);
        bean.set("recode", recode);
        return bean;
    }
    //生成组织机构的 codeitemid and a0000 xgq
    synchronized static public LazyDynaBean getCodeitemByRoot(Connection conn)throws GeneralException
    {
        String _codeitemid="";
        String _a0000="";
        StringBuffer strsql=new StringBuffer();
        strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where ");
        strsql.append(" codeitemid=parentid ");
        strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where ");
        strsql.append(" codeitemid=parentid ");
        strsql.append(" order by codeitemid desc");
        ContentDAO dao=new ContentDAO(conn);
        RowSet frowset=null;
        try{
            frowset=dao.search(strsql.toString());
            boolean b = false;
            while(frowset.next())
            {
                b = true;
                String chilecode=frowset.getString("codesetid");
                String codeitemid=frowset.getString("codeitemid");
                String corcode=frowset.getString("corcode");
                if(chilecode!=null)
                {
                        AddOrgInfo addOrgInfo=new AddOrgInfo();
                        _codeitemid=addOrgInfo.GetNext(codeitemid,"");
                        break;
                }
            }
            if(b){
                frowset=dao.search("select max(a0000)  from organization  ");
                if(frowset.next()) {
                    _a0000=String.valueOf(frowset.getInt(1)+1);
                }


            }else
            {
                _codeitemid="01";
                frowset=dao.search("select max(a0000)  from organization   ");
                if(frowset.next()) {
                    _a0000=String.valueOf(frowset.getInt(1)+1);
                } else {
                    _a0000="1";
                }
            }
        }catch(Exception e){
           e.printStackTrace();
           throw GeneralExceptionHandler.Handle(e);
        }
        finally
        {
            try
            {
                if(frowset!=null) {
                    frowset.close();
                }
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        }
        LazyDynaBean bean=new LazyDynaBean();
        bean.set("codeitemid",_codeitemid);
        bean.set("a0000",_a0000);
        return bean;
    }

    //新建组织记录
    synchronized static void insertOrgInf(LazyDynaBean infoBean,Connection conn)
    {
        try
        {
            ContentDAO dao=new ContentDAO(conn);
            RowSet rowSet=dao.search("select childid,layer,grade from organization where codeitemid='"+(String)infoBean.get("parentid")+"'");
            int layer=0;
            String childid="";
            int grade=0;
            boolean doInitLayer= false;
            if(rowSet.next())
            {
                if(rowSet.getString("layer")!=null) {
                    layer=rowSet.getInt("layer");
                }
                if(rowSet.getString("childid")!=null) {
                    childid=rowSet.getString("childid");
                }
                if(rowSet.getString("grade")!=null) {
                    grade=rowSet.getInt("grade");
                }
            }
             AddOrgInfo ao=new AddOrgInfo(conn);

            int level_A0000=0;
            ArrayList para_list=new ArrayList();
            para_list.add((String)infoBean.get("parentid"));
            para_list.add((String)infoBean.get("codesetid"));
            rowSet=dao.search("select max(levelA0000) from organization where parentid=? and codesetid=?", para_list);
            if(rowSet.next()) {
            	level_A0000=rowSet.getInt(1);
            }
            level_A0000++;
            layer = ao.getLayer((String)infoBean.get("parentid"),""+infoBean.get("codeitemid"), (String)infoBean.get("codesetid"));
            if(0==layer){
                doInitLayer=true;
            }
            if(childid.length()==0||childid.equals(infoBean.get("parentid"))){
                dao.update("update organization set childid='"+(String)infoBean.get("codeitemid")+"' where codeitemid='"+(String)infoBean.get("parentid")+"'");
            }

            RecordVo recordVo=new RecordVo("organization");
            recordVo.setString("codesetid",(String)infoBean.get("codesetid"));
            recordVo.setString("codeitemid",(String)infoBean.get("codeitemid"));
            recordVo.setString("codeitemdesc",(String)infoBean.get("codeitemdesc"));
            if(infoBean.get("corcode")!=null) {
                recordVo.setString("corcode",(String)infoBean.get("corcode"));
            }
            recordVo.setString("parentid",(String)infoBean.get("parentid"));
            recordVo.setString("childid",(String)infoBean.get("codeitemid"));
            recordVo.setInt("a0000", new Integer((String)infoBean.get("a0000")).intValue());
                recordVo.setInt("layer", layer);
            recordVo.setDate("end_date","9999-12-31");
            Date startDate=(Date)infoBean.get("start_date");
            recordVo.setDate("start_date",startDate);
        //  recordVo.setString("start_date",(String)infoBean.get("start_date"));
            if(grade!=0) {
                recordVo.setInt("grade", new Integer(++grade).intValue());
            } else {
                recordVo.setInt("grade", 1);
            }
            dao.addValueObject(recordVo);
             if(doInitLayer){//重置层级
                    ao.executeInitLayer();
                }
            dao.update("update organization set levelA0000="+(level_A0000)+" where codesetid='"+(String)infoBean.get("codesetid")+"' and codeitemid='"+(String)infoBean.get("codeitemid")+"'");
            //自动更新数据字典里相关数据 xgq

                     CodeItem item=new CodeItem();
                      Map lenmap = recordVo.getAttrLens();
                      int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
                     item.setCodeid(recordVo.getString("codesetid").toUpperCase());
                     item.setCodeitem(recordVo.getString("codeitemid").toUpperCase());
                     item.setCodename(PubFunc.splitString(recordVo.getString("codeitemdesc"),codeitemdesclen));
                     item.setPcodeitem(recordVo.getString("parentid").toUpperCase());
                     item.setCcodeitem(recordVo.getString("childid").toUpperCase());
                     item.setCodelevel(recordVo.getString("grade"));
                     AdminCode.addCodeItem(item);
                     AdminCode.updateCodeItemDesc(recordVo.getString("codesetid").toUpperCase(), recordVo.getString("codeitemid").toUpperCase(),PubFunc.splitString(recordVo.getString("codeitemdesc"),codeitemdesclen));


            if(rowSet!=null) {
                rowSet.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    //机构异动 新建、划转、合并模板入库写入主集
    synchronized static  public  String expDataIntoArchiveEmpMainSetBK(Connection conn,UserView userview,int ins_id,String setid,String srcTab,LazyDynaBean beanInfo,ArrayList fieldlist,String corcodeField)throws GeneralException
    {
        String[] strOrgid=null;
        try
        {
            String keyVlaue=(String)beanInfo.get("keyValue");
            String codeitemdesc=(String)beanInfo.get("codeitemdesc");
            String parentid=(String)beanInfo.get("parentid");
            String codesetid=(String)beanInfo.get("codesetid");
            String crocode=(String)beanInfo.get("corcode");


            ContentDAO dao=new ContentDAO(conn);
            String desTab=setid;
            ArrayList paralist=new ArrayList();

            strOrgid=new String[2];
            StringBuffer strsql=new StringBuffer();
            LazyDynaBean orgInfo=null;
            if(parentid!=null&&parentid.length()>0){
             orgInfo=getCodeitem(parentid,"",conn,codesetid);
            }else{
                orgInfo=getCodeitemByRoot(conn);
            }
            strOrgid[0]=(String)orgInfo.get("codeitemid");
            strOrgid[1]=(String)orgInfo.get("a0000");

            beanInfo.set("codeitemid", strOrgid[0]);
            beanInfo.set("a0000",strOrgid[1]);
            if(parentid!=null&&parentid.length()>0){
                beanInfo.set("parentid", parentid);
            }else{
            beanInfo.set("parentid", strOrgid[0]);
            }
            //往组织机构表中插入记录
            insertOrgInf(beanInfo,conn);
            TempletChgLogBo chgLogBo=new TempletChgLogBo(conn, userview);
            chgLogBo.updateOrganizationNewCodeitemId(strOrgid[0], keyVlaue);

            StringBuffer strIns_s=new StringBuffer();
            StringBuffer strIns_d=new StringBuffer();
            int n=0;
            for(int i=0;i<fieldlist.size();i++)
            {
                String cname=(String)fieldlist.get(i);
                if("codesetid".equalsIgnoreCase(cname)|| "codeitemdesc".equalsIgnoreCase(cname)|| "corcode".equalsIgnoreCase(cname)|| "parentid".equalsIgnoreCase(cname)|| "start_date".equalsIgnoreCase(cname)) {
                    continue;
                }

                if(n!=0)
                {
                    strIns_s.append(",");
                    strIns_d.append(",");
                }
                strIns_s.append(fieldlist.get(i));
                strIns_s.append("_2");
                strIns_d.append(fieldlist.get(i));
                n++;
            }

            strsql.append("insert into ");
            strsql.append(desTab);
            if("@K".equalsIgnoreCase(codesetid)) {
                strsql.append(" (e01a1");
            } else {
                strsql.append(" (b0110");
            }
            if(corcodeField.length()>0&&(","+strIns_d.toString()+",").indexOf(","+corcodeField+",")==-1) {
                strsql.append(","+corcodeField);
            }

            strsql.append(",createusername,createtime");  //dengcan 2009-5-13
            if(strIns_d.length()>0)
            {
                strsql.append(",");
                strsql.append(strIns_d);
            }
            //新建岗位时需要填写E0122
            if("@K".equalsIgnoreCase(codesetid)){
                strsql.append(",E0122");
            }
            strsql.append(",modtime) select ?");
            if(corcodeField.length()>0&&(","+strIns_d.toString()+",").indexOf(","+corcodeField+",")==-1) {
                strsql.append(",?");
            }
            strsql.append(",'"+userview.getUserName()+"',"+Sql_switcher.sqlNow()); //dengcan 2009-5-13
            if(strIns_s.length()>0)
            {
                strsql.append(",");
                strsql.append(strIns_s);
            }
            if("@K".equalsIgnoreCase(codesetid)){
                strsql.append(",'"+parentid+"'");
            }
            strsql.append(","+Sql_switcher.sqlNow()+" from ");
            strsql.append(srcTab);
            if("@K".equalsIgnoreCase(codesetid)) {
                strsql.append(" where e01a1=? ");
            } else {
                strsql.append(" where b0110=? ");
            }
            paralist.add(strOrgid[0]);
            if(corcodeField.length()>0&&(","+strIns_d.toString()+",").indexOf(","+corcodeField+",")==-1) {
                paralist.add(crocode);
            }
        //  paralist.add(Integer.parseInt(strOrgid[1]));
            paralist.add(keyVlaue);
            if(ins_id!=0)
            {
                strsql.append(" and ins_id=?");
                paralist.add(Integer.valueOf(ins_id));
            }
            dao.update(strsql.toString(),paralist);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return strOrgid[0];
    }


    //人事异动调入模板 入库写入主集
    synchronized static  public  String expDataIntoArchiveEmpMainSet(Connection conn,UserView userview,int ins_id,String desPre,String setid,String srcTab,String a0100,ArrayList fieldlist)throws GeneralException
    {
        String[] strA0100A0000=null;
        try
        {
            ContentDAO dao=new ContentDAO(conn);
            DbWizard dbw=new DbWizard(conn);
            String desTab=desPre+setid;
            ArrayList paralist=new ArrayList();
            strA0100A0000=new String[2];
            int na0100=1;
            int na0000=1;
            RowSet rset=null;
            StringBuffer strsql=new StringBuffer();
        //  BaseInfoBo infobo=new BaseInfoBo(this.conn,this.userview,1);
        //  String[] strA0100A0000=infobo.getMaxA0100A0000(desPre);
            String idflag = SystemConfig.getPropertyValue("idgenerator");
            if ("1".equals(idflag)) {
            	StringBuffer buf=new StringBuffer();
            	StringBuffer buf1=new StringBuffer();
            	ArrayList list=new ArrayList();
                //使用IDGenerator生成a0100，集群环境下该类会使用存储过程取数，保证a0100不冲突
            	strA0100A0000[0] = IDGenerator.getKeyId(desTab, "a0100", 8);

                //取最大顺序号，暂不考虑并发重复问题
                buf.append("select max(a0000) as a0000 from ");
                buf.append(desTab);
                rset = dao.search(buf.toString());
                if(rset.next())
                {
                	strA0100A0000[1] = String.valueOf(rset.getInt("a0000") + 1);
                }
                StringBuffer strIns_s=new StringBuffer();
                for(int i=0;i<fieldlist.size();i++)
	            {
	                if(i!=0)
	                {
	                    strIns_s.append(",");
	                }
	                strIns_s.append(fieldlist.get(i));
	                strIns_s.append("_2");
	            }

                strsql.append(" "+srcTab+".a0100='");
                strsql.append(strA0100A0000[0]);
                strsql.append("'");
                if(ins_id!=0)
	            {
	                strsql.append(" and "+srcTab+".ins_id='");
	                strsql.append(Integer.valueOf(ins_id));
	                strsql.append("'");
	            }

                buf1.append(" "+desTab+".a0100='");
                buf1.append(strA0100A0000[0]);
                buf1.append("'");
                String strcond=strsql.toString();
                String strcond1 = buf1.toString();

                StringBuffer set_str=new StringBuffer("");
				StringBuffer set_st2=new StringBuffer("");
				String middle = "";
				if(Sql_switcher.searchDbServer()==2) {
                    middle="`";
                } else {
                    middle=",";
                }
				set_str.append(desTab+".a0000="+srcTab+".a0000"+middle);
				set_str.append(desTab+".createusername="+srcTab+".createusername"+middle);
				set_str.append(desTab+".createtime="+srcTab+".createtime"+middle);
                for(int j=0;j<fieldlist.size();j++){
                	String temp=(String)fieldlist.get(j);
    				set_st2.append(","+temp+"=null");
    				set_str.append(desTab+"."+temp+"="+srcTab+"."+temp+"_2");
    				if(Sql_switcher.searchDbServer()==2) {
                        set_str.append("`");
                    } else {
                        set_str.append(",");
                    }
            	}
                if(set_str.length()>0) {
                    set_str.setLength(set_str.length()-1);
                }

				dao.update("update "+desTab+" set a0000=null,createusername=null,createtime=null,"+set_st2.substring(1)+" where a0100="+strA0100A0000[0]);
				String srcTab1 = "(select '"+strA0100A0000[0]+"' as a0100,"+strA0100A0000[1]+" as a0000,'"+userview.getUserName()+"' as createusername,";
				srcTab1+=Sql_switcher.sqlNow()+" as createtime,";
				srcTab1+=ins_id!=0?Integer.valueOf(ins_id)+" as ins_id,"+strIns_s+" from "+srcTab+" where "+srcTab+".A0100='"+a0100+"'":" "+strIns_s+" from "+srcTab+" where "+srcTab+".A0100='"+a0100+"'";
				srcTab1+=ins_id!=0?" and "+srcTab+".ins_id="+Integer.valueOf(ins_id)+" ) "+srcTab:" ) "+srcTab;
				dbw.updateRecord(desTab,srcTab1,desTab+".A0100="+srcTab+".A0100", set_str.toString(), strcond1, strcond);

            }else{
            	//非集群环境
	            strsql.append("select  "+Sql_switcher.isnull("max(a0100)", "0")+" as maxa0100,"+Sql_switcher.isnull("max(a0000)", "0")+" as maxa0000 from ");
	            strsql.append(desPre);
	            strsql.append("A01");
	            rset=dao.search(strsql.toString());
	            if(rset.next())
	            {
	                String stra0100=rset.getString("maxa0100");
	                na0100=Integer.parseInt(stra0100)+1;
	                na0000=rset.getInt("maxa0000")+1;
	            }
	            strA0100A0000[0]=StringUtils.leftPad(String.valueOf(na0100),8,'0');
	            strA0100A0000[1]=String.valueOf(na0000);
	            StringBuffer strIns_s=new StringBuffer();
	            StringBuffer strIns_d=new StringBuffer();
	            strsql.setLength(0);
	            for(int i=0;i<fieldlist.size();i++)
	            {
	                if(i!=0)
	                {
	                    strIns_s.append(",");
	                    strIns_d.append(",");
	                }
	                strIns_s.append(fieldlist.get(i));
	                strIns_s.append("_2");
	                strIns_d.append(fieldlist.get(i));
	            }

	            strsql.append("insert into ");
	            strsql.append(desTab);
	            strsql.append(" (a0100,a0000");
	            strsql.append(",createusername,createtime");  //dengcan 2009-5-13
	            if(strIns_d.length()>0)
	            {
	                strsql.append(",");
	                strsql.append(strIns_d);
	            }
	            strsql.append(") select ?,?");
	            strsql.append(",'"+userview.getUserName()+"',"+Sql_switcher.sqlNow()); //dengcan 2009-5-13
	            if(strIns_s.length()>0)
	            {
	                strsql.append(",");
	                strsql.append(strIns_s);
	            }
	            strsql.append(" from ");
	            strsql.append(srcTab);
	            strsql.append(" where a0100=? ");
	            paralist.add(strA0100A0000[0]);
	            paralist.add(strA0100A0000[1]);
	            paralist.add(a0100);
	            if(ins_id!=0)
	            {
	                strsql.append(" and ins_id=?");
	                paralist.add(Integer.valueOf(ins_id));
	            }
	            dao.update(strsql.toString(),paralist);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return strA0100A0000[0];
    }


    /**
     * 判断薪资临时表数据是否在历史表已存在，如存在次数自动加1,同时将数据提交至历史表中
     * @param conn
     * @param userview
     * @param gz_tablename
     * @param salaryid
     * @param manager
     * @param isUpdateTax
     * @param ctrlparam
     * @param gzitemlist
     * @param filterWhl
     * @author dc
     * @throws GeneralException
     */
    synchronized static  public  void autoAddZ1_subhistory(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,boolean isUpdateTax,SalaryCtrlParamBo ctrlparam,ArrayList gzitemlist,String filterWhl)throws GeneralException
    {
    	autoAddZ1_ff(conn,userview,gz_tablename,salaryid, manager,isUpdateTax);
    	try
    	{
    			String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
    		//	ContentDAO dao=new ContentDAO(this.conn);
    			DbWizard dbw=new DbWizard(conn);
    			if(!"1".equalsIgnoreCase(flow_flag))  //不需要审批
    			{
    				/**所有项目*/

    				StringBuffer fields=new StringBuffer();
    				StringBuffer buf=new StringBuffer();
    				for(int i=0;i<gzitemlist.size();i++)
    				{
    					GzItemVo itemvo=(GzItemVo)gzitemlist.get(i);
    					if(itemvo.getInitflag()==4) {
                            continue;
                        }
    					if(fields.length()==0) {
                            fields.append(itemvo.getFldname());
                        } else
    					{
    						fields.append(",");
    						fields.append(itemvo.getFldname());
    					}
    				}
    				/**删除当前用户处理的历史记录*/
    				buf.append("delete from salaryhistory ");
    				buf.append(" where exists (select null from ");
    				buf.append(gz_tablename);
    				buf.append(" a where salaryhistory.a00z0=a.a00z0 and salaryhistory.a00z1=a.a00z1 and upper(salaryhistory.nbase)=upper(a.nbase) and ");
    				buf.append(" salaryhistory.a0100=a.a0100 "+filterWhl.replaceAll(gz_tablename,"a")+"  )");
    				buf.append(" and salaryid=");
    				buf.append(salaryid);

    				String[] atemps=gz_tablename.toLowerCase().split("_salary_");  //20100323
    				buf.append(" and lower(userflag)='");
    				buf.append(atemps[0].toLowerCase());
    				buf.append("'");
    				dbw.execute(buf.toString());

    				/**把数据归档到薪资历史表中去*/
    				buf.setLength(0);
    				buf.append("insert into salaryhistory");
    				buf.append("(userflag,salaryid,sp_flag,e0122_o,b0110_o,dbid,");//Appprocess
    				buf.append(fields.toString());
    				buf.append(") select userflag,");
    				buf.append(salaryid);
    				buf.append(",sp_flag,e0122_o,b0110_o,dbid,");//Appprocess
    				buf.append(fields.toString());
    				buf.append(" from ");
    				buf.append(gz_tablename+" where 1=1 "+filterWhl);
    				dbw.execute(buf.toString());
    			}
    	}
    	catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

    }


    synchronized static  public  void autoAddZ1_ff(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,boolean isUpdateTax)throws GeneralException
    {
  	  String withNoLock="";
      if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
      {
          withNoLock=" WITH(NOLOCK) ";
      }
    RowSet rowSet=null;
    ContentDAO dao = new ContentDAO(conn);
    try
    {
    	 String[] _temps=gz_tablename.toLowerCase().split("_salary_");
         if(manager!=null&&manager.length()>0) {
             dao.update("update "+gz_tablename+" set userflag='"+manager+"'");
         } else {
             dao.update("update "+gz_tablename+" set userflag='"+userview.getUserName()+"'");
         }
         String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
         StringBuffer strsql=new StringBuffer("");
         if(Sql_switcher.searchDbServer()==2) {
             strsql.append("create table "+table+" as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z2,ss.a00z3,0 as a00z4,1 as i9999  ");
         } else {
             strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z2,ss.a00z3,0 as a00z4,1 as i9999   into "+table);
         }
         strsql.append("  from  "+gz_tablename+" ss"+withNoLock+",salaryhistory gm "+withNoLock+" where ");
         strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
         strsql.append(" and ( lower("+Sql_switcher.isnull("gm.userflag","''")+")<>'"+_temps[0].toLowerCase()+"' ");
         strsql.append(" or (  lower("+Sql_switcher.isnull("gm.userflag","''")+")='"+_temps[0].toLowerCase()+"' and ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  ) ) ");



         DbWizard dbw=new DbWizard(conn);
         if(dbw.isExistTable(table,false))
         {
             dbw.dropTable(table);
         }
         dao.update(strsql.toString());


         strsql.setLength(0);

         strsql.append("update "+table+" set a00z4=(select max(salaryhistory.a00z1) from salaryhistory where salaryid=?");
         strsql.append(" and  "+table+".a0100=salaryhistory.a0100 ");
         strsql.append(" and "+table+".a00z0=salaryhistory.a00z0 and lower("+table+".nbase)=lower(salaryhistory.nbase)");

         strsql.append(" and ( lower("+Sql_switcher.isnull("salaryhistory.userflag","''")+")<>'"+_temps[0].toLowerCase()+"' ");
         strsql.append(" or (  lower("+Sql_switcher.isnull("salaryhistory.userflag","''")+")='"+_temps[0].toLowerCase()+"' and ( salaryhistory.a00z2<>"+table+".a00z2 or salaryhistory.a00z3<>"+table+".a00z3 )  ) ) ");

         strsql.append(" group by salaryhistory.a0100  ) ");
         dao.update(strsql.toString(),Arrays.asList(new Object[] {Integer.valueOf(salaryid) }));

         {
             strsql.setLength(0);
             strsql.append(" update "+table+" set a00z4=( ");
             strsql.append(" select 0  from ");
             strsql.append(" (select min(a00z1) a00z1,a00z0,a0100,nbase from "+table+"  ");
             strsql.append("   group by a00z0,a0100,nbase ) b  ");
             strsql.append(" where "+table+".a0100=b.a0100 and "+table+".a00z0=b.a00z0 and lower("+table+".nbase)=lower(b.nbase)  and b.a00z1>a00z4 ) where exists (select null ");
             strsql.append(" from   (select min(a00z1) a00z1,a00z0,a0100,nbase from "+table+"  ");
             strsql.append("   group by a00z0,a0100,nbase ) b  ");
             strsql.append(" where "+table+".a0100=b.a0100 and "+table+".a00z0=b.a00z0 and lower("+table+".nbase)=lower(b.nbase)  and b.a00z1>a00z4 ) ");
             dao.update(strsql.toString());

             dao.update(" delete from "+table+" where a00z4=0");
             dao.update(" update "+table+" set a00z4=a00z4+a00z1 ");

             RowSet rs = dao.search("select a0100 from "+table+" where 1=1  group by a0100 HAVING COUNT(a0100)>1");
             while(rs.next()){
            	 int i=1;
            	 RowSet rs1 = dao.search("select * from "+table+" where a0100 = '"+rs.getString("a0100")+"' order by i9999");
            	 while(rs1.next()){
            		 dao.update("update "+table+" set i9999 = "+i+" where a0100 = '"+rs1.getString("a0100")+"' and  a00z0 = '"+rs1.getString("a00z0")+"' and lower(nbase) = '"+rs1.getString("nbase").toLowerCase()+"' and  a00z1 = '"+rs1.getString("a00z1")+"'");
            		 i++;
            	 }
             }

             //解决a00z4与临时表a00z1相同   如果有多条重复的话，目前还没有好的办法解决  zhaoxg add 2016-10-12
             strsql.setLength(0);
             strsql.append(" update "+table+" set a00z4=(select max(a00z1) from  "+gz_tablename+" where "+gz_tablename+".A0100="+table+".a0100 ");
             strsql.append(" and lower("+gz_tablename+".nbase)=lower("+table+".nbase)  and  "+gz_tablename+".a00z0="+table+".a00z0  ) ");
             strsql.append(" where exists (select null from  "+gz_tablename+" where "+gz_tablename+".A0100="+table+".a0100 ");
             strsql.append(" and lower("+gz_tablename+".nbase)=lower("+table+".nbase)  and  "+gz_tablename+".a00z0="+table+".a00z0 ");
             strsql.append(" and "+gz_tablename+".a00z1="+table+".a00z4 ) ");
             dao.update(strsql.toString());
             dao.update(" update "+table+" set a00z4=a00z4+i9999 ");

             strsql.setLength(0);
             strsql.append("update "+gz_tablename+" set a00z1=(select a00z4 from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
             strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
             strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
             strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
             int n=dao.update(strsql.toString());
             if(isUpdateTax)
             {
                 strsql.setLength(0);
                 strsql.append("update gz_tax_mx set a00z1=(select a00z4 from "+table+" where   "+table+".a0100=gz_tax_mx.a0100 ");
                 strsql.append(" and "+table+".a00z1=gz_tax_mx.a00z1 and "+table+".a00z0=gz_tax_mx.a00z0 and lower("+table+".nbase)=lower(gz_tax_mx.nbase) ) ");
                 strsql.append(" where  salaryid="+salaryid+" and lower(userflag)='"+_temps[0].toLowerCase()+"' and exists (select null from "+table+" where   "+table+".a0100=gz_tax_mx.a0100 ");
                 strsql.append(" and "+table+".a00z1=gz_tax_mx.a00z1 and "+table+".a00z0=gz_tax_mx.a00z0 and lower("+table+".nbase)=lower(gz_tax_mx.nbase) ) ");
                 dao.update(strsql.toString());
             }
         }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
        throw GeneralExceptionHandler.Handle(ex);
    }
    finally
    {
       PubFunc.closeDbObj(rowSet);
    }
}



    /**
     * 批量从薪资历史数据表取得已发放的数据
     * @param type     =1同月上次   =2上月同次数据 =4某年某月某次
     * @param itemlist 引入的薪资项目列表
     * @return
     */
    synchronized static public boolean batchImportFromHisGzTable(Connection conn,HashMap map,UserView userview,String gz_tablename,int salaryid,String manager
            ,RecordVo templatevo,String controlByUnitcode,String whl_str,boolean isApprove,
            String type,ArrayList itemlist,String year,String month,String count,SalaryCtrlParamBo bo)throws GeneralException
    {
        boolean bflag=true;
        String tablename="salaryhistory";
        try
        {
            String tempTable="t#"+userview.getUserName()+"_gz";
            String strym=null;
            int nc=0,ny=0,nm=0;
            /**求得当前处理年月和次数*/
            //getYearMonthCount("01");

            String currym=(String)map.get("ym");
            String currcount=(String)map.get("count");
            if("1".equals(type))//=1同月上次
            {
                nc=Integer.parseInt(currcount)-1;
                strym=currym;
            }
            else if("2".equals(type))
            {
                nc=Integer.parseInt(currcount);
                String[] tmp=StringUtils.split(currym,"-");
                ny=Integer.parseInt(tmp[0]);
                nm=Integer.parseInt(tmp[1]);
                if(nm==1)
                {
                    ny=ny-1;
                    nm=12;
                }
                else
                {
                    nm=nm-1;
                }
                strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
            }
            else if("4".equals(type))
            {
                nc=Integer.parseInt(count);
                ny=Integer.parseInt(year);
                nm=Integer.parseInt(month);
                strym=String.valueOf(ny)+"-"+String.valueOf(nm)+"-"+"01";
            }
            /**更新串*/
            StringBuffer strupdate=new StringBuffer();
            StringBuffer field_str=new StringBuffer("");
            String read_field=bo.getValue(SalaryCtrlParamBo.READ_FIELD);
            if(read_field==null|| "".equals(read_field)) {
                read_field="0";
            }
            for(int i=0;i<itemlist.size();i++)
            {
                String _itemid=((String)itemlist.get(i)).toLowerCase().trim();
                if(DataDictionary.getFieldItem(_itemid)!=null)
                {
                    if("0".equals(read_field))
                    {
                        if(!"2".equals(userview.analyseFieldPriv(_itemid))) {
                            continue;
                        }
                    }else
                    {
                        if(!"2".equals(userview.analyseFieldPriv(_itemid))&&!"1".equals(userview.analyseFieldPriv(_itemid))) {
                            continue;
                        }
                    }
                }

                strupdate.append(gz_tablename);
                strupdate.append(".");
                strupdate.append(itemlist.get(i));
                strupdate.append("=");
                strupdate.append(tempTable);
                strupdate.append(".");
                strupdate.append(itemlist.get(i));
                strupdate.append("`");

                field_str.append(","+itemlist.get(i));
            }//for loop end.

            if(strupdate.length()>0) {
                strupdate.setLength(strupdate.length()-1);
            } else {
                return bflag;
            }
            SalaryTemplateBo gzbo=new SalaryTemplateBo(conn,salaryid,userview);
            String strwhere1=" salaryhistory.A00Z2="+Sql_switcher.dateValue(strym)+" and salaryhistory.A00Z3="+nc+" and salaryhistory.salaryid="+salaryid+"";
            boolean temp=gzbo.isHaveHistory(strwhere1);
            if(!temp){
                tablename="salaryarchive";
            }


            //建临时表优化效率
            DbWizard dbw=new DbWizard(conn);

            if(dbw.isExistTable(tempTable)) {
                dbw.dropTable(tempTable);
            }
            StringBuffer str_sql=new StringBuffer("");
            if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                str_sql.append("create Table "+tempTable+" as ");
            }
            str_sql.append(" select "+field_str.substring(1)+",A0100,NBASE ");
            if(Sql_switcher.searchDbServer()!=Constant.ORACEL) {
                str_sql.append(" into "+tempTable);
            }
            str_sql.append(" from "+tablename+" where ");
            str_sql.append(""+tablename+".A00Z2=");
            str_sql.append(Sql_switcher.dateValue(strym));
            str_sql.append(" and "+tablename+".A00Z3=");
            str_sql.append(nc);
            str_sql.append(" and "+tablename+".salaryid=");
            str_sql.append(salaryid);

            //当上次发放某人有多条记录时会出错
            str_sql.append(" and exists (select null from (");
            str_sql.append(" select max(a00z1) a00z1, a0100,lower(nbase) nbase  from "+tablename+" where "+tablename+".A00Z2="+Sql_switcher.dateValue(strym));
            str_sql.append(" and "+tablename+".A00Z3="+nc+" and "+tablename+".salaryid="+salaryid+" group by a0100,lower(nbase)");
            str_sql.append(" ) a where "+tablename+".a00z1=a.a00z1 and "+tablename+".a0100=a.a0100 and lower("+tablename+".nbase)=lower(a.nbase) )");




            if(manager.length()>0&&!userview.getUserName().equalsIgnoreCase(manager))
            {
                if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                    dbw.execute("create Table "+tempTable+" as select "+field_str.substring(1)+",A0100,NBASE from "+tablename+" where 1=2");
                } else {
                    dbw.execute(" select "+field_str.substring(1)+",A0100,NBASE into "+tempTable+" from "+tablename+" where 1=2");
                }
            }
            else {
                dbw.execute(str_sql.toString());
            }



            //共享薪资类别，其他操作人员引入数据 (dengcan 2008/6/26 )
            if(manager.length()>0&&!userview.getUserName().equalsIgnoreCase(manager))//&&aflag!=null&&aflag.equals("1"))
            {
                String dbpres=templatevo.getString("cbase");
                //应用库前缀
                String[] dbarr=StringUtils.split(dbpres, ",");
                for(int i=0;i<dbarr.length;i++)
                {
                    String pre=dbarr[i];

                    dbw.execute("delete from "+tempTable);
                    StringBuffer _sql=new StringBuffer("");
                    _sql.append("insert into "+tempTable+" ("+field_str.substring(1)+",A0100,NBASE) select "+field_str.substring(1)+",A0100,NBASE from "+tablename+" where ");
                    _sql.append(""+tablename+".A00Z2=");
                    _sql.append(Sql_switcher.dateValue(strym));
                    _sql.append(" and "+tablename+".A00Z3=");
                    _sql.append(nc);
                    _sql.append(" and "+tablename+".salaryid=");
                    _sql.append(salaryid+" and upper("+tablename+".NBASE)='"+pre.toUpperCase()+"'");

                    //当上次发放某人有多条记录时会出错

                    _sql.append(" and exists (select null from (");
                    _sql.append(" select max(a00z1) a00z1, a0100   from "+tablename+" where "+tablename+".A00Z2="+Sql_switcher.dateValue(strym));
                    _sql.append(" and "+tablename+".A00Z3="+nc+" and "+tablename+".salaryid="+salaryid+"  and upper("+tablename+".NBASE)='"+pre.toUpperCase()+"' group by a0100 ");
                    _sql.append(" ) a where "+tablename+".a00z1=a.a00z1 and "+tablename+".a0100=a.a0100   )");



                    //权限过滤
                    String _whereIN="";
                    if("1".equals(controlByUnitcode))
                    {
                        if(whl_str.length()>0) {
                            _sql.append(whl_str);
                        }
                    }
                    else
                    {
                        _whereIN=InfoUtils.getWhereINSql(userview,pre);
                        _whereIN="select a0100 "+_whereIN;
                        _sql.append(" and "+tablename+".a0100 in ( "+_whereIN+" )");
                    }
                    dbw.execute(_sql.toString());


                    //连接串
                    StringBuffer strjoin=new StringBuffer();
                    strjoin.append(gz_tablename);
                    strjoin.append(".A0100=");
                    strjoin.append(tempTable);
                    strjoin.append(".A0100 and upper(");
                    strjoin.append(gz_tablename);
                    strjoin.append(".NBASE)='"+pre.toUpperCase()+"'");


                    //条件串
                    StringBuffer strwhere=new StringBuffer(" 1=1 ");

                    //需要审批
                    if(isApprove)
                    {
                        strwhere.append(" and ");
                        strwhere.append(gz_tablename);
                        strwhere.append(".");
                        strwhere.append("sp_flag in('01','07')");
                    }
//                  共享薪资类别，其他操作人员引入数据
                    if(manager.length()>0&&!userview.getUserName().equalsIgnoreCase(manager))
                    {
                        strwhere.append(" and "+gz_tablename+".sp_flag2 in ('01','07')");
                    }


                    StringBuffer stdwhere=new StringBuffer(" exists ( select null from "+tempTable+" where  "+strjoin.toString());
                    stdwhere.append(" and "+strwhere.toString()+" )");

                    dbw.updateRecord(gz_tablename,tempTable, strjoin.toString(), strupdate.toString(),stdwhere.toString(),strwhere.toString());


                }
            }
            else
            {
                /**连接串*/
                StringBuffer strjoin=new StringBuffer();
                strjoin.append(gz_tablename);
                strjoin.append(".A0100=");
                strjoin.append(tempTable);
                strjoin.append(".A0100 and upper(");
                strjoin.append(gz_tablename);
                strjoin.append(".NBASE)=upper(");
                strjoin.append(tempTable);
                strjoin.append(".NBASE) ");
                /**条件串*/
                StringBuffer strwhere=new StringBuffer(" 1=1 ");

                /**需要审批*/
                if(isApprove)
                {
                    strwhere.append(" and ");
                    strwhere.append(gz_tablename);
                    strwhere.append(".");
                    strwhere.append("sp_flag in('01','07')");
                }
//              共享薪资类别，其他操作人员引入数据
                if(manager.length()>0&&!userview.getUserName().equalsIgnoreCase(manager))
                {
                    strwhere.append(" and "+gz_tablename+".sp_flag2 in ('01','07')");
                }
                StringBuffer stdwhere=new StringBuffer(" exists ( select null from "+tempTable+" where  "+strjoin.toString());
                stdwhere.append(" and "+strwhere.toString()+" )");

                dbw.updateRecord(gz_tablename,tempTable, strjoin.toString(), strupdate.toString(),stdwhere.toString(),strwhere.toString());
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            bflag=false;
            throw GeneralExceptionHandler.Handle(ex);
        }
        return bflag;
    }



    /**
     * 根据用户名获得关联用户的姓名，如没有关联用户则获得用户的全称，否则得到用户名
     * @param username
     * @author dengc
     * @serialData 2013-11-27
     * @return
     */
    static public String getNameByUsername(String username,Connection conn)
    {
        String name=username;
        RowSet rowSet=null;
        try
        {
            ContentDAO dao = new ContentDAO(conn);
            rowSet=dao.search("select a0100,nbase,fullname   from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+username+"'");
            if(rowSet.next())
            {
                String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100").trim():"";
                String nbase=rowSet.getString("nbase")!=null?rowSet.getString("nbase").trim():"";
                String fullname=rowSet.getString("fullname")!=null?rowSet.getString("fullname").trim():"";
                if(a0100.length()>0&&nbase.length()>0)
                {
                    rowSet=dao.search("select a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
                    if(rowSet.next()) {
                        name=rowSet.getString("a0101");
                    }
                }
                else if(fullname.length()>0)
                {
                    name=fullname;
                }

            }
            if(rowSet!=null) {
                rowSet.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return name;
    }



    synchronized static  public  void autoAddZ1(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,SalaryTemplateBo gzbo,String approveObject,String filterWhl)throws GeneralException
    {
        RowSet rowSet=null;
        try
        {
            /** 同步历史表和薪资临时表 */
            String withNoLock="";
            if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
            {
                withNoLock=" WITH(NOLOCK) ";
            }
            ContentDAO dao = new ContentDAO(conn);
            String toName=DbNameBo.getNameByUsername(approveObject,conn);
            String fromName=DbNameBo.getNameByUsername(userview.getUserName(),conn);
            String[] _temps=gz_tablename.toLowerCase().split("_salary_");
            if(manager!=null&&manager.length()>0) {
                dao.update("update "+gz_tablename+" set userflag='"+manager+"'");
            } else {
                dao.update("update "+gz_tablename+" set userflag='"+userview.getUserName()+"'");
            }
            String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
            StringBuffer strsql=new StringBuffer("");
            if(Sql_switcher.searchDbServer()==2) {
                strsql.append("create table "+table+" as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3 ");
            } else {
                strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  into "+table);
            }
            strsql.append("  from  "+gz_tablename+" ss"+withNoLock+",salaryhistory gm "+withNoLock+" where ");
            strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
            strsql.append(" and ( lower("+Sql_switcher.isnull("gm.userflag","''")+")<>'"+_temps[0].toLowerCase()+"' ");
            strsql.append(" or (  lower("+Sql_switcher.isnull("gm.userflag","''")+")='"+_temps[0].toLowerCase()+"' and ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  ) ) ");
            strsql.append(" and ( ss.sp_flag='07' or ss.sp_flag='01')  ");


            DbWizard dbw=new DbWizard(conn);
            if(dbw.isExistTable(table,false))
            {
                dbw.dropTable(table);
            /*  dbw.execute("delete from "+table);
                strsql.setLength(0);
                strsql.append("insert into "+table+" (nbase,a0100,a00z1,a00z0,a00z3) ");
                strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  ");
                strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
                strsql.append(" gm.salaryid="+salaryid+" and ss.nbase=gm.nbase and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
                strsql.append(" and lower("+Sql_switcher.isnull("gm.userflag","''")+")<>'"+_temps[0].toLowerCase()+"' and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
                dbw.execute(strsql.toString());*/
            }
        //  else
            {
                dao.update(strsql.toString());
            }

            int count=0;
            rowSet=dao.search("select count(a0100) from "+table);
            if(rowSet.next()) {
                ;
            }
                count=rowSet.getInt(1);

            if(count>0)
            {
                strsql.setLength(0);

                /* 2013-11-05  邓灿注销
                strsql.append(" update "+table+" set a00z3=( ");
                strsql.append(" select b.a00z1  from ");
                strsql.append(" (select max(a00z1) a00z1,a00z0,a0100,nbase from salaryhistory where ");
                strsql.append(" lower("+Sql_switcher.isnull("userflag","''")+")<>'"+_temps[0].toLowerCase()+"' and  ");
                strsql.append(" salaryid="+salaryid+"  group by a00z0,a0100,nbase ) b  ");
                strsql.append(" where "+table+".a0100=b.a0100 and "+table+".a00z0=b.a00z0 and lower("+table+".nbase)=lower(b.nbase)  ) ");
                dao.update(strsql.toString());
                */


                strsql.append("update "+table+" set a00z3=(select max(salaryhistory.a00z1) from salaryhistory where salaryid="+salaryid);
                strsql.append(" and  "+table+".a0100=salaryhistory.a0100 ");
                strsql.append(" and "+table+".a00z0=salaryhistory.a00z0 and lower("+table+".nbase)=lower(salaryhistory.nbase) group by salaryhistory.a0100  ) ");
                dao.update(strsql.toString());
                dao.update("update "+table+" set a00z3=a00z3+1");






                strsql.setLength(0);
                strsql.append(" update "+table+" set a00z3=( ");
                strsql.append(" select b.a00z1+1  from ");
                strsql.append(" (select max(a00z1) a00z1,a00z0,a0100,nbase from "+gz_tablename+"  ");
                strsql.append("   group by a00z0,a0100,nbase ) b  ");
                strsql.append(" where "+table+".a0100=b.a0100 and "+table+".a00z0=b.a00z0 and lower("+table+".nbase)=lower(b.nbase)  and b.a00z1+1>a00z3 ) where exists (select null ");
                strsql.append(" from   (select max(a00z1) a00z1,a00z0,a0100,nbase from "+gz_tablename+"  ");
                strsql.append("   group by a00z0,a0100,nbase ) b  ");
                strsql.append(" where "+table+".a0100=b.a0100 and "+table+".a00z0=b.a00z0 and lower("+table+".nbase)=lower(b.nbase)  and b.a00z1+1>a00z3 ) ");
                dao.update(strsql.toString());

            //  dao.update("update "+table+" set a00z3=a00z3+a00z1 ");  //2013-11-05  邓灿注销



                /*
                strsql.setLength(0);
                strsql.append("update "+table+" set a00z3=(select max(salaryhistory.a00z1)+1 from salaryhistory where salaryid="+salaryid+" and  "+table+".a0100=salaryhistory.a0100 ");
                strsql.append(" and "+table+".a00z0=salaryhistory.a00z0 and  lower("+table+".nbase)=lower(salaryhistory.nbase) group by salaryhistory.a0100 ) ");
                dao.update(strsql.toString());

                strsql.setLength(0);
                strsql.append("update "+table+" set a00z3=(select max("+gz_tablename+".a00z1)+1 from "+gz_tablename+" where  "+table+".a0100="+gz_tablename+".a0100 ");
                strsql.append(" and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase) group by "+gz_tablename+".a0100 having max("+gz_tablename+".a00z1)+1>"+table+".a00z3    ) ");
                strsql.append(" where exists ( select null from "+gz_tablename+" where  "+table+".a0100="+gz_tablename+".a0100 ");
                strsql.append(" and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase) group by "+gz_tablename+".a0100 having max("+gz_tablename+".a00z1)+1>"+table+".a00z3     )");
                dao.update(strsql.toString());
                */

            //  dao.update("update "+table+" set a00z3=a00z3+1");

                strsql.setLength(0);
                strsql.append("update "+gz_tablename+" set a00z1=(select a00z3 from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
                strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
                strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 ");
                strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
                dao.update(strsql.toString());

                strsql.setLength(0);
                strsql.append("update gz_tax_mx set a00z1=(select a00z3 from "+table+" where   "+table+".a0100=gz_tax_mx.a0100 ");
                strsql.append(" and "+table+".a00z1=gz_tax_mx.a00z1 and "+table+".a00z0=gz_tax_mx.a00z0 and lower("+table+".nbase)=lower(gz_tax_mx.nbase) ) ");
                strsql.append(" where  salaryid="+salaryid+" and lower(userflag)='"+_temps[0].toLowerCase()+"' and exists (select null from "+table+" where   "+table+".a0100=gz_tax_mx.a0100 ");
                strsql.append(" and "+table+".a00z1=gz_tax_mx.a00z1 and "+table+".a00z0=gz_tax_mx.a00z0 and lower("+table+".nbase)=lower(gz_tax_mx.nbase) ) ");
                dao.update(strsql.toString());
            //  String sql2="update gz_tax_mx  set a00z1=? where lower(nbase)='"+nbase.toLowerCase()+"' and lower(userflag)='"+_temps[0].toLowerCase()+"' and a0100=? "
            //  +" and "+Sql_switcher.year("a00z0")+"=? and "+Sql_switcher.month("a00z0")+"=? and a00z1=? and salaryid="+this.salaryid;
            //  dao.batchUpdate(sql2, updateList);

            }

            //------------------------------------------------------------------

            String groupName="";
            if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0) {
                rowSet=dao.search("select b0110,e0122 from "+userview.getDbname()+"A01 where a0100='"+userview.getA0100()+"'");
            } else {
                rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+userview.getUserName()+"'");
            }
            if(rowSet.next())
            {
                if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0)
                {
                    String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110").trim():"";
                    String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122").trim():"";
                    if(e0122.length()>0) {
                        groupName=AdminCode.getCodeName("UM",e0122);
                    } else if(b0110.length()>0) {
                        groupName=AdminCode.getCodeName("UN",b0110);
                    }
                }
                else {
                    groupName=rowSet.getString(1);
                }
            }
            Calendar d=Calendar.getInstance();
            SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy.MM.dd HH:mm");
            String currentTime=dateFormat.format(d.getTime()); //当前时间




            rowSet=dao.search("select * from "+gz_tablename+" where 1=2");
            ResultSetMetaData metaData=rowSet.getMetaData();
            StringBuffer s1=new StringBuffer(",salaryid,curr_user,dbid,e0122_o,b0110_o");
            StringBuffer s2=new StringBuffer(","+salaryid+",'"+approveObject+"',dbid,e0122_o,b0110_o");
            for(int i=1;i<=metaData.getColumnCount();i++)
            {
                    if(gzbo.getStandardGzItemStr().indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1) {
                        continue;
                    }


                    if("sp_flag".equalsIgnoreCase(metaData.getColumnName(i)))
                    {
                        s1.append(","+metaData.getColumnName(i));
                        s2.append(",'02'");
                    }
                /*  else if(metaData.getColumnName(i).equalsIgnoreCase("appuser")) //临时表中无此字段
                    {
                        s1.append(","+metaData.getColumnName(i));
                        s2.append(",'"+userview.getUserName()+";'"+Sql_switcher.concat()+Sql_switcher.isnull(metaData.getColumnName(i),"''"));
                    } */
                    else if("appprocess".equalsIgnoreCase(metaData.getColumnName(i))&&Sql_switcher.searchDbServer()!=2)
                    {
                        s1.append(","+metaData.getColumnName(i));
                        // userview.getUserName  改为  userview.getUserFullName()  //2013-11-27  dengc  将用户名改为姓名
                        s2.append(",case when  sp_flag='07' then  "+Sql_switcher.isnull(Sql_switcher.sqlToChar(metaData.getColumnName(i)),"''")+Sql_switcher.concat()+"'   \r\n报批: "+currentTime+"\n  "+groupName+" "+userview.getUserFullName()+" 报批给 "+toName+"'");
                        s2.append(" else '报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName+"'  end  ");
                    //  s2.append(","+Sql_switcher.isnull(Sql_switcher.sqlToChar(metaData.getColumnName(i)),"''")+Sql_switcher.concat()+"'\r\n报批: "+currentTime+"\n  "+groupName+" "+userview.getUserName()+" 报批给 "+approveObject+"'");
                    }
                    else
                    {
                            s1.append(","+metaData.getColumnName(i));
                            s2.append(","+metaData.getColumnName(i));
                    }
            }
            s1.append(",appuser");
            s2.append(",';"+userview.getUserName()+";'");


            if(Sql_switcher.searchDbServer()==2)
            {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String appprocess,nbase,a0100,year,month,z1,sp_flag;
                StringBuffer sql3=new StringBuffer("select appprocess,a0100,nbase,a00z0,a00z1,sp_flag from "+gz_tablename+" where ( sp_flag='07' or sp_flag='01' ) ");
                if(filterWhl.length()>0) {
                    sql3.append(filterWhl);
                }
                rowSet=dao.search(sql3.toString());
                sql3.setLength(0);
                sql3.append("update "+gz_tablename+" set appprocess=? where a0100=? and lower(nbase)=? and  "+Sql_switcher.year("a00z0")+"=?");
                sql3.append(" and  "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
                if(filterWhl.length()>0) {
                    sql3.append(filterWhl);
                }
                ArrayList list = new ArrayList();


                while(rowSet.next())
                {
                    ArrayList param = new ArrayList();
                    sp_flag=rowSet.getString("sp_flag");
                    appprocess=Sql_switcher.readMemo(rowSet,"appprocess");
                    a0100=rowSet.getString("a0100");
                    nbase=rowSet.getString("nbase");
                    year=df.format(rowSet.getDate("a00z0")).split("-")[0];
                    month=df.format(rowSet.getDate("a00z0")).split("-")[1];
                    z1=rowSet.getString("a00z1");
                    if("01".equals(sp_flag))      //报批
                    {
                        appprocess="报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName;
                    } else if("07".equals(sp_flag)) //批准
                    {
                        appprocess+="   \r\n报批: "+currentTime+"\n  "+groupName+" "+fromName+" 报批给 "+toName;
                    }

//                  java.io.Reader clobReader = new StringReader(appprocess);
//                  pstmt2.setCharacterStream(1, clobReader, appprocess.length());
                    param.add(appprocess);
                    param.add(a0100);
                    param.add(nbase.toLowerCase());
                    param.add(Integer.parseInt(year));
                    param.add(Integer.parseInt(month));
                    param.add(Integer.parseInt(z1));
                    list.add(param);
                }
                dao.batchUpdate(sql3.toString(), list);
                rowSet.close();

            }




        //  SalaryTemplateBo.autoAddZ1(conn, userview, gz_tablename, salaryid+"");
            StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+gz_tablename);
            del.append(" where a0100=salaryhistory.a0100 ");
            if(filterWhl.length()>0) {
                del.append(filterWhl);
            }
            del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  and ( sp_flag='07' or sp_flag='01') ) ");
            del.append(" and salaryid="+salaryid);

            String[] atemps=gz_tablename.toLowerCase().split("_salary_");
            del.append(" and lower(userflag)='");  //20100323
            del.append(atemps[0].toLowerCase());
            del.append("'");
            dao.delete(del.toString(),new ArrayList());

            String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+gz_tablename+" where  ( sp_flag='07' or sp_flag='01' )";
            if(filterWhl.length()>0) {
                sql0+=filterWhl;
            }
    //      System.out.println(sql0);
            dao.update(sql0);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            String message=ex.toString();
            if(message.indexOf("最大")!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
            {
                PubFunc.resolve8060(conn,"salaryhistory");
                throw GeneralExceptionHandler.Handle(new Exception("请重新执行相关操作!"));
            }
            else {
                throw GeneralExceptionHandler.Handle(ex);
            }

        }
        finally
        {
            try
            {
                if(rowSet!=null) {
                    rowSet.close();
                }
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        }
    }

    //插入薪资历史记录
    synchronized static public   void appendExtendLog(String username,int salaryid,String ymd,String count,Connection dbconn)
    {
        StringBuffer buf=new StringBuffer();
        try
        {

            ContentDAO dao=new ContentDAO(dbconn);
            /**先删除当前用户，当前薪资类别正在处于起草状态的记录*/
            buf.append("delete from gz_extend_log where salaryid=? and username=? and sp_flag='01'");
            ArrayList paralist=new ArrayList();
            paralist.add(String.valueOf(salaryid));
            paralist.add(username);
            dao.update(buf.toString(),paralist);


            /**增加一条起草状态的记录*/
            int maxid=0;
            IDGenerator idg = new IDGenerator(2,dbconn);
            maxid=Integer.parseInt(IDGenerator.getKeyId("gz_extend_log", "id", 1));
            /*

            RowSet rowSet=dao.search("select max(id) from gz_extend_log");
            if(rowSet.next())
            {
                maxid=rowSet.getInt(1);
            }
            maxid++;
            */


            RecordVo vo=new RecordVo("gz_extend_log");
            vo.setInt("id", maxid);
            vo=dao.findByPrimaryKey(vo);
            vo.setString("username", username);
            vo.setString("sp_flag", "01");
            vo.setInt("salaryid", salaryid);
            vo.setInt("a00z3",Integer.parseInt(count));
            vo.setDate("a00z2",ymd);
            dao.updateValueObject(vo);


        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }




    /**
     * 插入主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    synchronized static public  String insertMainSetA0100(String table,Connection dbconn)throws GeneralException
    {
        HashMap map = appendMainSetA0100(table, dbconn);
        return (String)map.get("A0100");
    }
    
    /**
     * 插入主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    synchronized static public  String insertMainSetA0100(String table,String guidkey,Connection dbconn)throws GeneralException
    {
        HashMap map = appendMainSetA0100(table,guidkey,dbconn);
        return (String)map.get("A0100");
    }

    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    synchronized static public  HashMap appendMainSetA0100(String table,String guidkey,Connection dbconn)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        int a0100 = 1;
        int a0000 = 1;
        String stra0100 = "00000001";
        RowSet rset=null;
        try
        {
            ContentDAO dao = new ContentDAO(dbconn);
            ArrayList list=new ArrayList();
            String idflag = SystemConfig.getPropertyValue("idgenerator");
            if ("1".equals(idflag)) {
                //使用IDGenerator生成a0100，集群环境下该类会使用存储过程取数，保证a0100不冲突
                //人员移库 新增记录时，默认 带入guidkey,防止移入人员新生成guidkye
                stra0100 = IDGenerator.getKeyId(table+","+guidkey, "a0100", 8);

                //取最大顺序号，暂不考虑并发重复问题
                buf.append("select max(a0000) as a0000 from ");
                buf.append(table);
                rset = dao.search(buf.toString());
                if(rset.next())
                {
                    a0000 = rset.getInt("a0000") + 1;
                }

                buf.setLength(0);
                buf.append("update ");
                buf.append(table);
                buf.append(" set a0000=?");
                buf.append(" where a0100=?");
                list.add(new Integer(a0000));
                list.add(stra0100);

            } else {
                //非集群环境下，a0100自己取
                buf.append("select max(a0100) as a0100,max(a0000) as a0000 from ");
                buf.append(table);
                rset = dao.search(buf.toString());
                if(rset.next())
                {
                    String a0100value = rset.getString("a0100");
                    if(a0100value!=null && a0100value.trim().length()>0) {
                        a0100 = Integer.parseInt(a0100value) + 1;
                    }

                    a0000 = rset.getInt("a0000")+1;
                }

                stra0100 = StringUtils.leftPad(String.valueOf(a0100), 8,"0");

                buf.setLength(0);
                buf.append("insert into ");
                buf.append(table);
                buf.append(" (a0100,a0000,guidkey) values(?,?,?)");
                list.add(stra0100);
                list.add(new Integer(a0000));
                list.add(guidkey);
            }
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally{
            PubFunc.closeDbObj(rset);
        }

        HashMap map = new HashMap();
        map.put("A0100", stra0100);
        map.put("A0000", String.valueOf(a0000));

        return map;
    }
    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    synchronized static public  HashMap appendMainSetA0100(String table,Connection dbconn)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        int a0100 = 1;
        int a0000 = 1;
        String stra0100 = "00000001";
        RowSet rset=null;
        try
        {
            ContentDAO dao = new ContentDAO(dbconn);
            ArrayList list=new ArrayList();
            String idflag = SystemConfig.getPropertyValue("idgenerator");
            if ("1".equals(idflag)) {
                //使用IDGenerator生成a0100，集群环境下该类会使用存储过程取数，保证a0100不冲突
                stra0100 = IDGenerator.getKeyId(table, "a0100", 8);

                //取最大顺序号，暂不考虑并发重复问题
                buf.append("select max(a0000) as a0000 from ");
                buf.append(table);
                rset = dao.search(buf.toString());
                if(rset.next())
                {
                    a0000 = rset.getInt("a0000") + 1;
                }

                buf.setLength(0);
                buf.append("update ");
                buf.append(table);
                buf.append(" set a0000=?");
                buf.append(" where a0100=?");
                list.add(new Integer(a0000));
                list.add(stra0100);

            } else {
                //非集群环境下，a0100自己取
                buf.append("select max(a0100) as a0100,max(a0000) as a0000 from ");
                buf.append(table);
                rset = dao.search(buf.toString());
                if(rset.next())
                {
                    String a0100value = rset.getString("a0100");
                    if(a0100value!=null && a0100value.trim().length()>0) {
                        a0100 = Integer.parseInt(a0100value) + 1;
                    }

                    a0000 = rset.getInt("a0000")+1;
                }

                stra0100 = StringUtils.leftPad(String.valueOf(a0100), 8,"0");

                buf.setLength(0);
                buf.append("insert into ");
                buf.append(table);
                buf.append(" (a0100,a0000) values(?,?)");
                list.add(stra0100);
                list.add(new Integer(a0000));
            }
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally{
        	PubFunc.closeDbObj(rset);
        }

        HashMap map = new HashMap();
        map.put("A0100", stra0100);
        map.put("A0000", String.valueOf(a0000));

        return map;
    }
    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    synchronized static public  HashMap appendMainSetA0100(String table,String B0110,String E0122,
            String E01A1,Connection dbconn)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        int a0100=1;
        int a0000=1;
        HashMap map=new HashMap();
        String stra0100="00000001";
        RowSet rset=null;
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            buf.append("select max(A0100) a0100,max(a0000) a0000 from ");
            buf.append(table);
            rset=dao.search(buf.toString());
            if(rset.next())
            {
                String a0100value = rset.getString("a0100");
                if(a0100value!=null&&a0100value.trim().length()>0) {
                    a0100=Integer.parseInt(rset.getString("a0100"))+1;
                } else {
                    a0100=1;
                }
                a0000=rset.getInt("a0000")+1;
            }
            stra0100=StringUtils.leftPad(String.valueOf(a0100), 8,"0");
            FieldItem B0110Item = DataDictionary.getFieldItem("B0110");
            FieldItem E0122Item = DataDictionary.getFieldItem("E0122");
            FieldItem E01A1Item = DataDictionary.getFieldItem("E01A1");

            buf.setLength(0);
            buf.append("insert into ");
            buf.append(table);
            buf.append("(a0000,a0100");
            if(B0110Item!=null) {
                buf.append(",b0110");
            }
            if(E0122Item!=null) {
                buf.append(",e0122");
            }
            if(E01A1Item!=null) {
                buf.append(",e01a1");
            }
            buf.append(") values(?,?");
            if(B0110Item!=null) {
                buf.append(",?");
            }
            if(E0122Item!=null) {
                buf.append(",?");
            }
            if(E01A1Item!=null) {
                buf.append(",?");
            }
            buf.append(")");
            ArrayList list=new ArrayList();
            list.add(new Integer(a0000));
            list.add(stra0100);
            if(B0110Item!=null) {
                list.add(B0110);
            }
            if(E0122Item!=null) {
                list.add(E0122);
            }
            if(E01A1Item!=null) {
                list.add(E01A1);
            }
            dao.update(buf.toString(), list);
            map.put("A0100", stra0100);
            map.put("A0000", String.valueOf(a0000));
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally{
        	PubFunc.closeDbObj(rset);
        }
        return map;
    }


    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean appendMainSetA0100(String table,String UN_code,String UM_code)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.replaceAll("A01","").replaceAll("a01","");
            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
            String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            String flagstr="";
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                if(ctrl_type.equals("1")){
                                    flagstr+=unitdesc+umitdesc+list.get(2)+"人数超编!\\n";
                                }else{
                                    flagstr+=unitdesc+list.get(2)+"人数超编!\\n";
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }
            */
            if(flagstr!=null&&flagstr.trim().length()>1){
                checkflag = false;
                if("warn".equals(mode)){

                }else {redundantInfo=flagstr;
                throw GeneralExceptionHandler.Handle(new GeneralException("",flagstr,"",""));
             }
            }
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 修改主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean updateMainSetA0100(String table,String UN_code,String UM_code,RecordVo vo_old,String A0100)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.substring(0,3);

            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,dbpre+"A01");
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1&&table.substring(4,6).equals("01")){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,A0100,(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                ContentDAO dao=new ContentDAO(this.conn);
                                if(vo_old!=null){
                                    ArrayList listvalue = new ArrayList();
                                    listvalue.add(vo_old);
                                    dao.updateValueObject(listvalue);
                                }else{
                                    dao.update("delete from "+table+" where A0100='"+A0100+"'");
                                }
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }

            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    public  boolean updateMainSetA0100(String table,String UN_code,String UM_code,RecordVo vo_old,String A0100,ArrayList msglist)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.substring(0,3);
            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
            String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,dbpre+"A01");
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1&&table.substring(4,6).equals("01")){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,A0100,(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                ContentDAO dao=new ContentDAO(this.conn);
                                if(!"warn".equals(mode))
                                if(vo_old!=null){
                                    ArrayList listvalue = new ArrayList();
                                    listvalue.add(vo_old);
                                    dao.updateValueObject(listvalue);
                                }else{
                                    dao.update("delete from "+table+" where A0100='"+A0100+"'");
                                }
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+umitdesc+list.get(2)+"人数超编!");
                                    }else
                                        throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+list.get(2)+"人数超编!");
                                    }else
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 修改子集人员信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean updateMainSetA0100(String table,String UN_code,String UM_code,RecordVo vo_old,
            String A0100,String I9999)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.substring(0,3);

            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,dbpre+"A01");
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                ContentDAO dao=new ContentDAO(this.conn);
                                if(fieldset.isMainset()){
                                    if(vo_old!=null){
                                        ArrayList listvalue = new ArrayList();
                                        listvalue.add(vo_old);
                                        dao.updateValueObject(listvalue);
                                    }else
                                        dao.update("delete from "+table+" where A0100='"+A0100+"'");
                                }else{
                                    if(vo_old!=null){
                                        ArrayList listvalue = new ArrayList();
                                        listvalue.add(vo_old);
                                        dao.updateValueObject(listvalue);
                                    }else
                                        dao.update("delete from "+table+" where A0100='"+A0100+"' and I9999='"+I9999+"'");
                                }
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }
            */
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 追加人员信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean appendMainSetA0100(String table,String UN_code,String UM_code,
            String A0100,String I9999)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.substring(0,3);

            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,dbpre+"A01");
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1&&table.substring(4,6).equals("01")){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");//计划人数
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");//实有
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");//有效无效
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");//统计公式
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");//控制方法
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                ContentDAO dao=new ContentDAO(this.conn);
                                if(fieldset.isMainset())
                                    dao.update("delete from "+table+" where A0100='"+A0100+"'");
                                else
                                    dao.update("delete from "+table+" where A0100='"+A0100+"' and I9999='"+I9999+"'");
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else
                        {
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }
            */
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 追加人员信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean appendMainSetA0100(String table,String UN_code,String UM_code,
            String A0100,String I9999,ArrayList msglist)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.substring(0,3);
            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
            String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,dbpre+"A01");
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1&&table.substring(4,6).equals("01")){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");//计划人数
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");//实有
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");//有效无效
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");//统计公式
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");//控制方法
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                ContentDAO dao=new ContentDAO(this.conn);
                                if(!"warn".equals(mode))
                                if(fieldset.isMainset())
                                    dao.update("delete from "+table+" where A0100='"+A0100+"'");
                                else
                                    dao.update("delete from "+table+" where A0100='"+A0100+"' and I9999='"+I9999+"'");
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+umitdesc+list.get(2)+"人数超编!");
                                    }else
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+list.get(2)+"人数超编!");
                                    }else
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else
                        {
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }
            */
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 是否要进行编制控制
     * @return
     */
    public boolean checkOrgMaint(){
        boolean checkflag = false;
        PosparameXML pos = new PosparameXML(conn);
        String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
        setid=setid!=null&&setid.trim().length()>0?setid:"";
        ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
        for(int i=0;i<planitemlist.size();i++){
            String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
            if(flag!=null&& "1".equals(flag)){
                checkflag = true;
                break;
            }
        }
        return checkflag;
    }


    /** ----------------------  人事异动判断是否超编   ------------------------------  */


    /**
     * 是否要进行编制控制
     * @return
     */
    public boolean checkOrgMaint2(){
        boolean checkflag = false;
        PosparameXML pos = new PosparameXML(conn);//得到constant表中UNIT_WORKOUT对应的参数
        String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");//得到编制子集
        setid=setid!=null&&setid.trim().length()>0?setid:"";
        ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);//对应amounts下所有ctrl_item属性的planitem值的list
        for(int i=0;i<planitemlist.size();i++){
            String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");//ctrl_item下flag的属性值
            if(flag!=null&& "1".equals(flag)){
                checkflag = true;
                break;
            }
        }
        if(checkflag){
            return true;
        }
        //职位编制控制
        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
        String zwvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"pos");//组织机构/参数设置/编制参数设置/岗位编制/是否需要岗位编制控制
        if(zwvalid!=null&& "true".equalsIgnoreCase(zwvalid))
        {
            RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);//岗位编制管理的参数
            if(ps_workout_vo!=null)
            {
              String  ps_workout=ps_workout_vo.getString("str_value"); ////K01|K0114,K0111
              if(ps_workout!=null&&ps_workout.length()>0)
              {
                  String[] temps=ps_workout.split("\\|");
                  if(temps.length==2)
                  {
                      String zw_set=temps[0];
                      if(temps[1].split(",").length==2)
                      {
                          checkflag = true;
                      }
                  }
              }

            }
        }

        return checkflag;
    }



    /**
     * 检验是否超编
     * @param recordlist
     * @return
     */
    synchronized public  String validateOverBz(ArrayList recordlist)
    {
        String error = "";
        PosparameXML pos = new PosparameXML(conn);//UNIT_WORKOUT下的xml
        String dbunum = "";
        String a0100str = "";
        for(int i=0;i<recordlist.size();i++){
            LazyDynaBean rec=(LazyDynaBean)recordlist.get(i);
            String dbpre = (String)rec.get("dbpre");
            String UN_code = (String)rec.get("UN_code");
            String UM_code = (String)rec.get("UM_code");
            String K_code = (String)rec.get("K_code");
            String K_code2 = (String)rec.get("K_code2");
            K_code=K_code!=null&&K_code.trim().length()>0?K_code:"no";
            K_code2=K_code2!=null&&K_code2.trim().length()>0?K_code2:"no";
            UM_code=UM_code!=null&&UM_code.trim().length()>0?UM_code:"no";
            String a0100 = (String)rec.get("a0100");
            if(dbunum.indexOf(dbpre+"::"+UN_code+"::"+UM_code+"::"+K_code+"::"+K_code2+",")==-1){
                dbunum+=dbpre+"::"+UN_code+"::"+UM_code+"::"+K_code+"::"+K_code2+",";
            }
            if(a0100str.indexOf(a0100+",")==-1) {
                a0100str+="'"+a0100+"',";
            }
        }


        //职位编制控制
        Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
        String zwvalid=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"pos");////组织机构/参数设置/编制参数设置/岗位编制/是否需要岗位编制控制
        String  zw_set="";//岗位编制子集
        String  ps_workfixed="";//定员数
        if(zwvalid!=null&& "true".equalsIgnoreCase(zwvalid))
        {
            RecordVo ps_workout_vo=ConstantParamter.getRealConstantVo("PS_WORKOUT",conn);//岗位编制管理
            if(ps_workout_vo!=null)
            {
              String ps_workout=ps_workout_vo.getString("str_value"); ////K01|K0114,K0111
              if(ps_workout!=null&&ps_workout.length()>0)
              {
                  String[] temps=ps_workout.split("\\|");
                  if(temps.length==2)
                  {
                      zw_set=temps[0];
                      if(temps[1].split(",").length==2)
                      {
                          ps_workfixed= temps[1].split(",")[0];
                      }
                  }
              }
            }
        }
        String dbunumArr[] = dbunum.split(",");
        for(int i=0;i<dbunumArr.length;i++){
            if(dbunumArr[i]!=null&&dbunumArr[i].trim().length()>0){
                String arr[] = dbunumArr[i].split("::");
                if(arr.length==5){
                    error = checkAddRecord2(pos,arr[0],arr[1], "no".equals(arr[2])?"":arr[2], "no".equals(arr[3])?"":arr[3], "no".equals(arr[4])?"":arr[4],recordlist,zw_set,ps_workfixed);
                    if(error!=null&&error.trim().length()>5) {
                        break;
                    }
                }
            }
        }

        return error.trim().length()>5?error:"yes";
    }

    /**检查是否超编  分职位、兼职、部门、单位几种情况*/
    public  String checkAddRecord2(PosparameXML pos,String dbpre,String UN_code,String UM_code,String K_code,String K_code2,ArrayList recordlist,String pos_set,String pos_plan_item)
    {
        String checkflag = "";
        try{
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");//要控制的人员库
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
            //职位编制控制
            if(pos_set.length()>0&&pos_plan_item.length()>0&&K_code.trim().length()>0)
            {
                if(isOverNumber_Pos(recordlist,K_code,pos_set,pos_plan_item,dbs))
                {
                    checkflag =AdminCode.getCodeName("@K",K_code)+"人数超编!";
                }
            }
            //兼职职位编制控制
            if(pos_set.length()>0&&pos_plan_item.length()>0&&K_code2.trim().length()>0)
            {
                if(isOverNumber_Pos2(recordlist,K_code2,pos_set,pos_plan_item,dbs))
                {
                    checkflag =AdminCode.getCodeName("@K",K_code2)+"人数超编!";
                }
            }
            ///开始检查部门、单位编制控制
            if(checkflag.length()==0)
            {
                String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");//编制子集
                String sp_flag=pos.getValue(PosparameXML.AMOUNTS,"sp_flag");//审批状态
                setid=setid!=null&&setid.trim().length()>0?setid:"";
                FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
                String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");//需要控制部门编制
                ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";


                String unitdesc=AdminCode.getCodeName("UN",UN_code);
                if("".equals(unitdesc)){
                    unitdesc=AdminCode.getCodeName("UM",UN_code);
                }
                if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                    unitdesc = orgCodeName(UN_code);
                }
                String umitdesc=AdminCode.getCodeName("UN",UM_code);
                if("".equals(umitdesc)){
                    umitdesc=AdminCode.getCodeName("UM",UM_code);
                }
                if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                    umitdesc = orgCodeName(UM_code);
                }
                /*其他地方控制超编 wangrd 2013-09-27
                if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){//模板中的人员库正好是编制控制要控制的人员库
                    ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                    for(int i=0;i<planitemlist.size();i++){
                        String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                        String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                        String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                        String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                        String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                        String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                        cond=cond!=null&&cond.trim().length()>0?cond:"new";
                        if(flag.equals("1") && cond!=null&&cond.trim().length()>0){
                            ArrayList list = exprDep2(sp_flag,fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1),recordlist);
                            //list的格式为  list(0):统计条件  list(1)：编制总数   list(2):统计条件的名字（或总），list(3)：实有人数   list(4)：编制总数   ,list(5)：=0：超编 =1：没超编（当然，这只是List的一种情况）
                            if(list!=null&&list.size()>=6)
                            {
                                if(list.get(5)!=null&&list.get(5).equals("0"))
                                {
                                    if(ctrl_type.equals("1")){
                                        checkflag = unitdesc+"/"+umitdesc+"/"+list.get(2)+"人数超编!";
                                    }else{
                                        checkflag = unitdesc+"/"+list.get(2)+"人数超编!";
                                    }
                                }
                            }else{
                                //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                            }
                        }
                    }
                }*/
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return checkflag;
    }


    /**
     * 判断职位是否超编
     * @param recordList
     * @return
     */
    public boolean isOverNumber_Pos(ArrayList recordList,String K_code,String pos_set,String pos_plan_item,String dbs)
    {
        boolean flag=false;
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            float plan_count=-1;

            StringBuffer buf = new StringBuffer();
            buf.append("select ");
            buf.append(pos_plan_item);
            buf.append(" from ");
            buf.append(pos_set+" a ");
            buf.append(" where e01a1='");
            buf.append(K_code);
            buf.append("'");
            if(!"K01".equalsIgnoreCase(pos_set.trim())){
                buf.append(" and I9999=(select max(I9999) from "+pos_set+" where e01a1=a.e01a1)");
            }
            RowSet rs  = dao.search(buf.toString());
            if(rs.next()){
                plan_count=rs.getFloat(1);
            }
            if(plan_count!=-1)//&&plan_count!=0)
            {
                int count = 0;
                String[] dbarr = dbs.split(",");//dbs是要控制的人员库
                for(int i=0;i<dbarr.length;i++){//检查所有的要控制的库，看看该岗位已经有多少个人了
                    String dbpre = dbarr[i];
                    String code=" where "+dbpre+"A01.e01a1 like '"+K_code+"%'";

                    if(dbpre!=null&&dbpre.trim().length()>0){
                        String wherestr = " from "+dbpre+"A01";
                        StringBuffer sqlstr = new StringBuffer();
                        sqlstr.append("select count("+dbpre+"A01.A0100) as counts ");
                        sqlstr.append(wherestr);
                        if(code!=null&&code.trim().length()>0) {
                            sqlstr.append(code);
                        }
                         rs = dao.search(sqlstr.toString());
                         if(rs.next()){
                                count+=rs.getInt("counts");
                         }
                    }
                }
                if(count>plan_count)//xieguiquan
                {
                    count=(int)plan_count;
                }
                LazyDynaBean _bean=null;
                String _dbs=","+dbs.toLowerCase()+",";
                int _num=0;
                for(int i=0;i<recordList.size();i++)
                {
                    _bean=(LazyDynaBean)recordList.get(i);
                    String dbpre=((String)_bean.get("dbpre")).toLowerCase();
                    String a0100=(String)_bean.get("a0100");
                    String un_code=(String)_bean.get("UN_code");
                    String um_code=(String)_bean.get("UM_code");
                    String _K_code=(String)_bean.get("K_code");
                    if(_dbs.indexOf(","+dbpre+",")!=-1)
                    {
                        if(_K_code!=null&&_K_code.trim().length()>0){

                                if(("/"+_K_code).indexOf("/"+K_code)!=-1) {
                                    _num++;
                                }
                        }
                    }
                }
                count+=_num;

                if(count>plan_count) {
                    flag=true;
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
    /**
     * 判断兼职职位是否超编
     * @param recordList
     * @return
     */
    public boolean isOverNumber_Pos2(ArrayList recordList,String K_code,String pos_set,String pos_plan_item,String dbs)
    {
        boolean flag=false;
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            float plan_count=-1;
            String item_p_id ="";
            RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
            if(ps_workparttime_vo!=null){
                 String zw_ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
                 FieldItem item_p=DataDictionary.getFieldItem(zw_ps_workparttime);
                 if(item_p!=null&&!"0".equalsIgnoreCase(item_p.getUseflag())){
                     item_p_id=item_p.getItemid();
                 }
            }
            StringBuffer buf = new StringBuffer();
            buf.append("select ");
            buf.append(pos_plan_item);
            if(item_p_id!=null&&!"".equals(item_p_id.trim())) {
                buf.append(","+item_p_id +" ");
            }
            buf.append(" from ");
            buf.append(pos_set+" a ");
            buf.append(" where e01a1='");
            buf.append(K_code);
            buf.append("'");
            if(!"K01".equalsIgnoreCase(pos_set.trim())){
                buf.append(" and I9999=(select max(I9999) from "+pos_set+" where e01a1=a.e01a1)");
            }
            RowSet rs  = dao.search(buf.toString());
            float parttime =0;
            if(rs.next()){
                plan_count=rs.getFloat(1);
                if(item_p_id.length()>0){
                    parttime=rs.getFloat(item_p_id);
                }
            }
            if(plan_count!=-1)//&&plan_count!=0)
            {
                int count = 0;
                String[] dbarr = dbs.split(",");
                for(int i=0;i<dbarr.length;i++){
                    String dbpre = dbarr[i];
                    String code=" where "+dbpre+"A01.e01a1 like '"+K_code+"%'";

                    if(dbpre!=null&&dbpre.trim().length()>0){
                        String wherestr = " from "+dbpre+"A01";
                        StringBuffer sqlstr = new StringBuffer();
                        sqlstr.append("select count("+dbpre+"A01.A0100) as counts ");
                        sqlstr.append(wherestr);
                        if(code!=null&&code.trim().length()>0) {
                            sqlstr.append(code);
                        }
                         rs = dao.search(sqlstr.toString());
                         if(rs.next()){
                                count+=rs.getInt("counts");
                         }
                    }
                }

                count += parttime;
                if(count>plan_count)//xieguiquan
                {
                    count=(int)plan_count;
                }
                LazyDynaBean _bean=null;
                String _dbs=","+dbs.toLowerCase()+",";
                int _num1 = 0;
                for(int i=0;i<recordList.size();i++)
                {
                    _bean=(LazyDynaBean)recordList.get(i);
                    String dbpre=((String)_bean.get("dbpre")).toLowerCase();
                    String a0100=(String)_bean.get("a0100");
                    String _K_code=(String)_bean.get("K_code");//职务
                    String _K_code2=(String)_bean.get("K_code2");//兼职
                    //判断兼职职务同时又是某个人的职务
                    if(_K_code2!=null&&_K_code!=null&&_K_code2.trim().length()>0&&_K_code2.equals(_K_code)){
                        if(dbpre!=null&&dbpre.trim().length()>0){
                            String code=" where "+dbpre+"A01.e01a1 = '"+_K_code2+"' and a0100='"+a0100+"'";
                            String wherestr = " from "+dbpre+"A01";
                            StringBuffer sqlstr = new StringBuffer();
                            sqlstr.append("select "+dbpre+"A01.A0100  ");
                            sqlstr.append(wherestr);
                                sqlstr.append(code);
                             rs = dao.search(sqlstr.toString());

                    if(!rs.next()){
                    if(_dbs.indexOf(","+dbpre+",")!=-1&&_K_code2!=null&&_K_code2.length()>0)
                    {
                        if(_K_code2!=null&&_K_code2.trim().length()>0){

                            if(("/"+_K_code2).indexOf("/"+K_code)!=-1) {
                                _num1++;
                            }
                    }
                    }
                    }
                    }
                    }
                }
                count+=_num1;
                int _num2 = 0;

                //兼职控制编制
                Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
                /**兼职参数*/
                String partflag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
                //兼职岗位占编 1：占编
                String takeup_quota=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
                String ps_parttime="0";
                if("true".equalsIgnoreCase(partflag)&&"1".equals(takeup_quota)){
                    ps_parttime="1";
                }
                String pos_ctrl=sysbo.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
                String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");
                if("true".equals(pos_ctrl)&&"1".equals(ps_parttime)){
                    String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");//兼职子集
                String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");//兼任兼职
                FieldItem pos_field_item = DataDictionary.getFieldItem(pos_field);
                FieldItem appoint_field_item = DataDictionary.getFieldItem(appoint_field);
                if(pos_field.length()>0&&appoint_field.length()>0&&pos_field_item!=null&& "1".equals(pos_field_item.getUseflag())&&appoint_field_item!=null&& "1".equals(appoint_field_item.getUseflag())){
                    for(int i=0;i<recordList.size();i++)
                {
                    _bean=(LazyDynaBean)recordList.get(i);
                    String dbpre=((String)_bean.get("dbpre")).toLowerCase();
                    String a0100=(String)_bean.get("a0100");
                    String _K_code2=(String)_bean.get("K_code2");//兼职
                    //判断是否已存在兼职职务
                    rs=dao.search("select "+pos_field+" from "+dbpre+setid+" where a0100='"+a0100+"' and "+appoint_field+"='0' and "+pos_field+"='"+_K_code2+"'");
                    if(!rs.next()){
                    if(_dbs.indexOf(","+dbpre+",")!=-1&&_K_code2!=null&&_K_code2.length()>0)
                    {
                        if(_K_code2!=null&&_K_code2.trim().length()>0){

                            if(("/"+_K_code2).indexOf("/"+K_code)!=-1) {
                                _num2++;
                            }
                    }
                    }
                    }
                }
                    count+=_num2;

                }
                }
                if(count>plan_count) {
                    flag=true;
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return flag;
    }
    /**得到是否超编的信息，以及统计条件的信息。所有的信息存放在List中*/
    public ArrayList exprDep2(String sp_flag,FieldSet fieldset,String planitem,String realitem,String id,String dbs,String UN_code,
            String UM_code,String ctrl_type,String method,String cond,String except_a0100,int order,ArrayList recordList)throws GeneralException{
        ArrayList list = new ArrayList();
        list.add(id);//统计条件
        list.add(planitem);//编制总数
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            int count=0;//实有人数
            if(id!=null&& "new".equalsIgnoreCase(id)){
                list.add("总");
                count = countRealPerson(dao,dbs,UN_code,UM_code,ctrl_type,except_a0100);//计算实有人数
            }else{//根据统计条件计算实有人数
                RecordVo vo = new RecordVo("LExpr");
                vo.setInt("id",Integer.parseInt(id));
                try{
                    vo = dao.findByPrimaryKey(vo);
                }catch(Exception e){
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("org.static.item.setup.static")+order+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n"));
                }
                list.add(vo.getString("name"));
                String[] dbarr = dbs.split(",");
                for(int i=0;i<dbarr.length;i++){
                    String dbpre = dbarr[i];
                    String code = "";
                    if(UM_code!=null&&UM_code.trim().length()>0){
                        if("1".equals(ctrl_type)){
                            code=" and "+dbpre+"A01.E0122 like '"+UM_code+"%'";
                        }else{
                            code=" and "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                        }
                    }else{
                        if(UN_code!=null&&UN_code.trim().length()>0){
                            code=" and "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                        }
                    }

                    if(dbpre!=null&&dbpre.trim().length()>0){
                        FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
                                dbpre, false, false, true, 1, "su");
                        String wherestr = factor.getSqlExpression();
                        StringBuffer sqlstr = new StringBuffer();
                        sqlstr.append("select count("+dbpre+"A01.A0100) as counts ");
                        sqlstr.append(wherestr);
                        if(code!=null&&code.trim().length()>0) {
                            sqlstr.append(code);
                        }
                        if(except_a0100!=null&&except_a0100.length()>0) {
                            sqlstr.append(" and a0100<>'"+except_a0100+"'");
                        }
                        RowSet rs  = dao.search(sqlstr.toString());
                        if(rs.next()){
                            count+=rs.getInt("counts");
                        }
                    }
                }
            }
            ////检查模板中有几个该部门下的人
            LazyDynaBean _bean=null;
            String _dbs=","+dbs.toLowerCase()+",";
            int _num=0;
            for(int i=0;i<recordList.size();i++)
            {
                _bean=(LazyDynaBean)recordList.get(i);
                String dbpre=((String)_bean.get("dbpre")).toLowerCase();
                String a0100=(String)_bean.get("a0100");
                String un_code=(String)_bean.get("UN_code");
                String um_code=(String)_bean.get("UM_code");

                if(_dbs.indexOf(","+dbpre+",")!=-1)
                {
                    if(UM_code!=null&&UM_code.trim().length()>0){
                        if("1".equals(ctrl_type)){
                            if(um_code!=null&&um_code.trim().length()>0)
                            {
                                if(("/"+um_code).indexOf("/"+UM_code)!=-1) {
                                    _num++;
                                }

                            }
                        }else{
                            if(un_code!=null&&un_code.trim().length()>0)
                            {
                                if(("/"+un_code).indexOf("/"+UN_code)!=-1) {
                                    _num++;
                                }

                            }
                        }
                    }else{
                        if(UN_code!=null&&UN_code.trim().length()>0){
                            if(un_code!=null&&un_code.trim().length()>0)
                            {
                                if(("/"+un_code).indexOf("/"+UN_code)!=-1) {
                                    _num++;
                                }

                            }
                        }
                    }

                }
            }
            count+=_num;

            list.add(count+"");
            float bcount=-1;//编制总数
            StringBuffer buf = new StringBuffer();
            buf.append("select ");
            buf.append(planitem);
            buf.append(" from ");
            buf.append(fieldset.getFieldsetid()+" a ");
            if("1".equals(ctrl_type)){//需要控制部门编制
                if(UM_code!=null&&UM_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UM_code);
                    buf.append("'");
                }else if(UN_code!=null&&UN_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UN_code);
                    buf.append("'");
                }else {
                    buf.append(" where 1=2");
                }
            }else{//不需要控制部门编制
                if(UN_code!=null&&UN_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UN_code);
                    buf.append("'");
                }else {
                    buf.append(" where 1=2");
                }
            }
            buf.append(" and "+sp_flag+"='03' ");
            if(!fieldset.isMainset()){
                buf.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where "+sp_flag+"='03'  and B0110=a.B0110)");
            }
            RowSet rs  = dao.search(buf.toString());
            if(rs.next()){
                bcount=rs.getFloat(planitem);
            }


            if(bcount==-1)//||bcount==0)
            {
                list.add("0");
                list.add("1");
            }
            else
            {
                list.add(bcount+"");
                if(method!=null&& "1".equals(method)){//控制方法按百分比
                    float realcount = countRealitem(dao,fieldset,realitem,UN_code,UM_code,ctrl_type,cond);
                    if(realcount>0){
                        BigDecimal b1 = new BigDecimal(realcount+"");
                        BigDecimal b2 = new BigDecimal(count+"");
                        double d = b2.divide(b1,2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        if(d>1){
                            list.add("0");
                        }else{
                            list.add("1");
                        }
                    }else{
                        list.add("1");
                    }
                }else{//控制方法按个数
                    if(count>bcount){
                        list.add("0");
                    }else{
                        list.add("1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }

    /** --------------------------  end  -----------------------------*/


    /**
     * 批量增加
     * @param recordlist
     * @param tablelist 涉及的表list
     *  @param flag 记录是否删除 0.必须删除 1.不删除 2.超编了才删除
     * @return
     */
    synchronized public  String batchAddRecord(ArrayList recordlist,ArrayList tablelist,String flag)
    {
        String error = "";
        PosparameXML pos = new PosparameXML(conn);
        String dbunum = "";
        String a0100str = "";
        for(int i=0;i<recordlist.size();i++){
            LazyDynaBean rec=(LazyDynaBean)recordlist.get(i);
            String dbpre = (String)rec.get("dbpre");
            String UN_code = (String)rec.get("UN_code");
            String UM_code = (String)rec.get("UM_code");
            UM_code=UM_code!=null&&UM_code.trim().length()>0?UM_code:"no";
            String a0100 = (String)rec.get("a0100");

            if(dbunum.indexOf(dbpre+"::"+UN_code+"::"+UM_code+",")==-1){
                dbunum+=dbpre+"::"+UN_code+"::"+UM_code+",";
            }
            if(a0100str.indexOf(a0100+",")==-1) {
                a0100str+="'"+a0100+"',";
            }
        }
        String dbunumArr[] = dbunum.split(",");
        for(int i=0;i<dbunumArr.length;i++){
            if(dbunumArr[i]!=null&&dbunumArr[i].trim().length()>0){
                String arr[] = dbunumArr[i].split("::");
                if(arr.length==3){
                    error = checkAddRecord(pos,arr[0],arr[1], "no".equals(arr[2])?"":arr[2]);
                    if(error!=null&&error.trim().length()>5) {
                        break;
                    }
                }
            }
        }
        if("0".equals(flag)||("2".equals(flag)&&error.trim().length()>5)){
            ContentDAO dao = new ContentDAO(this.conn);
            a0100str=a0100str.substring(0,a0100str.length()-1);
            StringBuffer sqlstr = new StringBuffer();
            for(int i=0;i<tablelist.size();i++){
                String tablename = (String)tablelist.get(i);
                if(tablename!=null&&tablename.trim().length()>0){
                    sqlstr.setLength(0);
                    sqlstr.append("delete from ");
                    sqlstr.append(tablename);
                    sqlstr.append(" where A0100 in(");
                    sqlstr.append(a0100str);
                    sqlstr.append(")");
                    try {
                        dao.update(sqlstr.toString());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return error.trim().length()>5?error:"yes";
    }

    public  String checkAddRecord(PosparameXML pos,String dbpre,String UN_code,String UM_code)
    {
        String checkflag = "";
        try{
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";

            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1") && cond!=null&&cond.trim().length()>0){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0"))
                            {
                                if(ctrl_type.equals("1")){
                                    checkflag = unitdesc+umitdesc+list.get(2)+"人数超编!";
                                }else{
                                    checkflag = unitdesc+list.get(2)+"人数超编!";
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }
                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return checkflag;
    }
    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean appendMainSetA0100(String table,String UN_code,String UM_code,String A0100)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
            String dbpre = table.replaceAll("A01","").replaceAll("a01","");

            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,table);
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }
            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1") && cond!=null&&cond.trim().length()>0){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));//编制管理
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0")){
                                delA01(A0100,table);
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else
                        {
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }


                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 追加主集信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean appendMainSetA0100(String table,String UN_code,String UM_code,String A0100,ArrayList msglist)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
            String dbpre = table.replaceAll("A01","").replaceAll("a01","");
            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
            String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,table);
                UN_code=orgarr[0];
                if(UM_code==null||UM_code.trim().length()<1){
                    UM_code=orgarr[1];
                }
            }
            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1") && cond!=null&&cond.trim().length()>0){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));//编制管理
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0")){

                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+umitdesc+list.get(2)+"人数超编!");
                                    }else{
                                        delA01(A0100,table);
                                        throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                    }
                                }else{
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+list.get(2)+"人数超编!");
                                    }else{
                                        delA01(A0100,table);
                                        throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                    }
                                }
                            }
                        }else
                        {
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }


                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    /**
     * 移动信息,比如UsrA01,RetA01等,静态加同步，对类，
     * 只有一个线程可以访问此方法，要不然会造成主键冲突
     * @param table　应用库前缀+A01
     * @return
     */
    public  boolean removeMainSetA0100(String table,String UN_code,String UM_code,String A0100)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.replaceAll("A01","").replaceAll("a01","");

            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,table);
                if(orgarr!=null&&orgarr.length>1)
                {
                    UN_code=orgarr[0];
                    if(UM_code==null||UM_code.trim().length()<1){
                        UM_code=orgarr[1];
                    }
                }
            }
            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")&&cond!=null&&cond.trim().length()>0){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0")){
                                delAllRecord(dbpre,A0100);
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }

                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    public  boolean removeMainSetA0100(String table,String UN_code,String UM_code,String A0100,ArrayList msglist)throws GeneralException
    {
        boolean checkflag = true;
        try{
            PosparameXML pos = new PosparameXML(conn);
            String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
            setid=setid!=null&&setid.trim().length()>0?setid:"";
            FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
            String ctrl_type = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
            ctrl_type=ctrl_type!=null&&ctrl_type.trim().length()>0?ctrl_type:"0";
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"Usr,";
            String dbpre = table.replaceAll("A01","").replaceAll("a01","");
            Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(conn);
            String mode=sysoth.getValue(Sys_Oth_Parameter.WORKOUT,"mode");
            if(UN_code==null||UN_code.trim().length()<1){
                String orgarr[] = getOrg(A0100,table);
                if(orgarr!=null&&orgarr.length>1)
                {
                    UN_code=orgarr[0];
                    if(UM_code==null||UM_code.trim().length()<1){
                        UM_code=orgarr[1];
                    }
                }
            }
            String unitdesc=AdminCode.getCodeName("UN",UN_code);
            if("".equals(unitdesc)){
                unitdesc=AdminCode.getCodeName("UM",UN_code);
            }
            if((unitdesc==null||unitdesc.trim().length()<1)&&UN_code!=null&&UN_code.trim().length()>0){
                unitdesc = orgCodeName(UN_code);
            }
            String umitdesc=AdminCode.getCodeName("UN",UM_code);
            if("".equals(umitdesc)){
                umitdesc=AdminCode.getCodeName("UM",UM_code);
            }
            if((umitdesc==null||umitdesc.trim().length()<1)&&UM_code!=null&&UM_code.trim().length()>0){
                umitdesc = orgCodeName(UM_code);
            }
            /*其他地方控制超编 wangrd 2013-09-27
            if(fieldset!=null&&dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
                ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
                for(int i=0;i<planitemlist.size();i++){
                    String planitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"planitem");
                    String realitem = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"realitem");
                    String flag = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"flag");
                    String cond = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
                    String method = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"method");
                    String statics = pos.getTextValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString());
                    cond=cond!=null&&cond.trim().length()>0?cond:"new";
                    if(flag.equals("1")&&cond!=null&&cond.trim().length()>0){
                        ArrayList list = exprDep(fieldset,planitem,realitem,cond,dbs,UN_code,UM_code,ctrl_type,method,statics,"",(i+1));
                        if(list!=null&&list.size()>=6)
                        {
                            if(list.get(5)!=null&&list.get(5).equals("0")){
                                if(!"warn".equals(mode))
                                    delAllRecord(dbpre,A0100);
                                if(ctrl_type.equals("1")){
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+umitdesc+list.get(2)+"人数超编!");
                                    }else
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+umitdesc+list.get(2)+"人数超编!","",""));
                                }else{
                                    checkflag = false;
                                    if("warn".equals(mode)){
                                        msglist.add(unitdesc+list.get(2)+"人数超编!");
                                    }else
                                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+list.get(2)+"人数超编!","",""));
                                }
                            }
                        }else{
                            //throw GeneralExceptionHandler.Handle(new GeneralException("","编制管理定义错误!","",""));
                        }

                    }
                }
            }*/
        } catch(Exception ex){
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return checkflag;
    }
    public void delAllRecord(String dbpre,String A0100){
        ContentDAO dao = new ContentDAO(this.conn);
        ArrayList fieldsetlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET);
        for(int j=0;j<fieldsetlist.size();j++){
            FieldSet fieldset = (FieldSet)fieldsetlist.get(j);
            try {
                dao.update("delete from "+dbpre+fieldset.getFieldsetid()+" where A0100='"+A0100+"'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public String orgCodeName(String codeid){
        String codename="";
        StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select codeitemdesc from organization where codeitemid='"+codeid+"'");
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs=null;
        try {
            rs = dao.search(sqlstr.toString());
            if(rs.next()){
                codename=rs.getString("codeitemdesc");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return codename;
    }
    public ArrayList exprDep(FieldSet fieldset,String planitem,String realitem,String id,String dbs,String UN_code,
            String UM_code,String ctrl_type,String method,String cond,String except_a0100,int order)throws GeneralException{
        ArrayList list = new ArrayList();
        list.add(id);
        list.add(planitem);
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            int count=0;
            if(id!=null&& "new".equalsIgnoreCase(id)){
                list.add("总");
                count = countRealPerson(dao,dbs,UN_code,UM_code,ctrl_type,except_a0100);
            }else{
                RecordVo vo = new RecordVo("LExpr");
                vo.setInt("id",Integer.parseInt(id));
                try{
                    vo = dao.findByPrimaryKey(vo);
                }catch(Exception e){
                    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("org.static.item.setup.static")+order+ResourceFactory.getProperty("org.static.item.setup.static.m")+"\n"));
                }
                list.add(vo.getString("name"));
                String[] dbarr = dbs.split(",");
                for(int i=0;i<dbarr.length;i++){
                    String dbpre = dbarr[i];
                    String code = "";
                    if(UM_code!=null&&UM_code.trim().length()>0){
                        if("1".equals(ctrl_type)){
                            code=" and "+dbpre+"A01.E0122 like '"+UM_code+"%'";
                        }else{
                            code=" and "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                        }
                    }else{
                        if(UN_code!=null&&UN_code.trim().length()>0){
                            code=" and "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                        }
                    }

                    if(dbpre!=null&&dbpre.trim().length()>0){
                        FactorList factor = new FactorList(vo.getString("lexpr"), vo.getString("factor"),
                                dbpre, false, false, true, 1, "su");
                        String wherestr = factor.getSqlExpression();
                        StringBuffer sqlstr = new StringBuffer();
                        sqlstr.append("select count("+dbpre+"A01.A0100) as counts ");
                        sqlstr.append(wherestr);
                        if(code!=null&&code.trim().length()>0) {
                            sqlstr.append(code);
                        }
                        if(except_a0100!=null&&except_a0100.length()>0) {
                            sqlstr.append(" and a0100<>'"+except_a0100+"'");
                        }
                        RowSet rs  = dao.search(sqlstr.toString());
                        if(rs.next()){
                            count+=rs.getInt("counts");
                        }
                    }
                }
            }
            list.add(count+"");
            float bcount=0;
            StringBuffer buf = new StringBuffer();
            buf.append("select ");
            buf.append(planitem);
            buf.append(" from ");
            buf.append(fieldset.getFieldsetid()+" a ");
            if("1".equals(ctrl_type)){
                if(UM_code!=null&&UM_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UM_code);
                    buf.append("'");
                }else if(UN_code!=null&&UN_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UN_code);
                    buf.append("'");
                }else {
                    buf.append(" where 1=2");
                }
            }else{
                if(UN_code!=null&&UN_code.trim().length()>0){
                    buf.append(" where B0110='");
                    buf.append(UN_code);
                    buf.append("'");
                }else {
                    buf.append(" where 1=2");
                }
            }
            if(!fieldset.isMainset()){
                buf.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
            }
            RowSet rs  = dao.search(buf.toString());
            if(rs.next()){
                bcount=rs.getFloat(planitem);
            }
            list.add(bcount+"");

            if(method!=null&& "1".equals(method)){
                float realcount = countRealitem(dao,fieldset,realitem,UN_code,UM_code,ctrl_type,cond);
                if(realcount>0){
                    BigDecimal b1 = new BigDecimal(realcount+"");
                    BigDecimal b2 = new BigDecimal(count+"");
                    double d = b2.divide(b1,2,BigDecimal.ROUND_HALF_UP).doubleValue();
                    if(d>bcount){
                        list.add("0");
                    }else{
                        list.add("1");
                    }
                }else{
                    list.add("1");
                }
            }else{
                if(count>bcount){
                    list.add("0");
                }else{
                    list.add("1");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
        return list;
    }
    /**计算实有人数*/
    private int countRealPerson(ContentDAO dao,String dbs,String UN_code,String UM_code,String ctrl_type,String except_a0100){
        int count = 0;
        String[] dbarr = dbs.split(",");
        for(int i=0;i<dbarr.length;i++){
            String dbpre = dbarr[i];
            String code = "";
            if(UM_code!=null&&UM_code.trim().length()>0){
                if("1".equals(ctrl_type)){
                    code=" where "+dbpre+"A01.E0122 like '"+UM_code+"%'";
                }else{
                    code=" where "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                }
            }else{
                if(UN_code!=null&&UN_code.trim().length()>0){
                    code=" where "+dbpre+"A01.B0110 like '"+UN_code+"%'";
                }
            }

            if(dbpre!=null&&dbpre.trim().length()>0){
                String wherestr = " from "+dbpre+"A01";
                StringBuffer sqlstr = new StringBuffer();
                sqlstr.append("select count("+dbpre+"A01.A0100) as counts ");
                sqlstr.append(wherestr);
                if(code!=null&&code.trim().length()>0) {
                    sqlstr.append(code);
                }
                 if(except_a0100!=null&&except_a0100.length()>0) {
                     sqlstr.append(" and a0100<>'"+except_a0100+"'");
                 }
                RowSet rs;
                try {
                    rs = dao.search(sqlstr.toString());
                    if(rs.next()){
                        count+=rs.getInt("counts");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return count;
    }
    private float countRealitem(ContentDAO dao,FieldSet fieldset,String realitem,
            String UN_code,String UM_code,String ctrl_type,String cond){
        float count = 0;
        StringBuffer buf = new StringBuffer();
        String FSQL = this.getFQL(realitem, cond);
        BatchBo batchBo = new BatchBo();
        buf.append("select ");
        if(FSQL.indexOf("SELECT_")!=-1){
            buf.append("(select ");
            buf.append(FSQL);
            buf.append(" from ");
            buf.append(batchBo.getTempTable(userView));
            buf.append(" where B0110=a.B0110)");
        }else{
            buf.append(FSQL);
        }
        buf.append(" as "+realitem+" from ");
        buf.append(fieldset.getFieldsetid()+" a ");
        if("1".equals(ctrl_type)){
            if(UM_code!=null&&UM_code.trim().length()>0){
                buf.append(" where B0110='");
                buf.append(UM_code);
                buf.append("'");
            }else if(UN_code!=null&&UN_code.trim().length()>0){
                buf.append(" where B0110='");
                buf.append(UN_code);
                buf.append("'");
            }else {
                buf.append(" where 1=2");
            }
        }else{
            if(UN_code!=null&&UN_code.trim().length()>0){
                buf.append(" where B0110='");
                buf.append(UN_code);
                buf.append("'");
            }else {
                buf.append(" where 1=2");
            }
        }
        if(!fieldset.isMainset()){
            buf.append(" and I9999=(select max(I9999) from "+fieldset.getFieldsetid()+" where B0110=a.B0110)");
        }
        RowSet rs;
        try {
            rs = dao.search(buf.toString());
            if(rs.next()){
                count=rs.getFloat(realitem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }
    private void delA01(String A0100,String table){
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            dao.update("delete from "+table+" where A0100='"+A0100+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String[] getOrg(String A0100,String table){
        String[] org = {"",""};
        ContentDAO dao = new ContentDAO(this.conn);
        try {
            RowSet rs = dao.search("select B0110,E0122 from "+table+" where A0100='"+A0100+"'");
            if(rs.next()){
                org[0]=rs.getString("B0110");
                org[1]=rs.getString("E0122");
            }else
            {
                org[0]="";
                org[1]="";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return org;
    }
    public boolean checkUpdate(RecordVo vo_old,RecordVo vo_new,String setid)throws GeneralException{
        boolean checkflag=false;
        ContentDAO dao = new ContentDAO(this.conn);
        String factor = itemidStr(dao).toLowerCase();
        ArrayList itemlist = DataDictionary.getFieldList(setid,Constant.USED_FIELD_SET);
        if(factor!=null&&factor.length()>1){
            for(int j=0;j<itemlist.size();j++){
                FieldItem fielditem = (FieldItem)itemlist.get(j);
                String itemid = fielditem.getItemid().toLowerCase();
                if(factor.indexOf(itemid)!=-1){
                    String old_value=vo_old.getString(fielditem.getItemid().toLowerCase());
                    old_value=old_value!=null?old_value:"";
                    String new_value=vo_new.getString(fielditem.getItemid().toLowerCase());
                    new_value=new_value!=null?new_value:"";
                    if(new_value!=old_value){
                        checkflag=true;
                        break;
                    }
                }
            }
        }else{
            checkflag=true;
        }
        return checkflag;
    }
    private String getFQL(String itemid,String c_expr){
        String FSQL="";
        c_expr = SafeCode.decode(c_expr);
        try{
            if(this.userView==null) {
                this.userView = new UserView("su",this.conn);
            }
            FieldItem fielditem = DataDictionary.getFieldItem(itemid);
            ArrayList alUsedFields = DataDictionary.getFieldList(fielditem.getFieldsetid(),Constant.USED_FIELD_SET);
            YksjParser yp = new YksjParser(this.userView,alUsedFields,YksjParser.forSearch,YksjParser.FLOAT,3,"","");
            yp.setCon(conn);
            yp.run(c_expr);
            FSQL=yp.getSQL();
        }catch(Exception e){
            e.printStackTrace();
        }
        return FSQL;
    }
    private String itemidStr(ContentDAO dao)throws GeneralException{
        String itemarr ="";
        PosparameXML pos = new PosparameXML(conn);
        String setid=pos.getValue(PosparameXML.AMOUNTS,"setid");
        setid=setid!=null&&setid.trim().length()>0?setid:"";
        ArrayList planitemlist = pos.getChildList(PosparameXML.AMOUNTS,setid);
        for(int i=0;i<planitemlist.size();i++){
            String id = pos.getChildValue(PosparameXML.AMOUNTS,setid,planitemlist.get(i).toString(),"static");
            if(id==null||id.length()<1) {
                continue;
            }
            RecordVo vo = new RecordVo("LExpr");
            vo.setInt("id",Integer.parseInt(id));
            try {
                vo = dao.findByPrimaryKey(vo);

            }catch(Exception e){

            }
            if(vo!=null) {
                itemarr+=vo.getString("factor")+"`";
            } else {
                throw GeneralExceptionHandler.Handle(new GeneralException("","编制参数设置项目公式有误请联系管理员！","",""));
            }
        }
        return itemarr;
    }

    /**
     * 插入子集记录,在最后插入一条记录
     * @param table  子集名称，带应用库前缀
     * @param a0100　人员编号
     * @param dbconn
     * @return
     * @throws GeneralException
     */
    /*synchronized*/ static public  String insertSubSetA0100(String table,String a0100,Connection dbconn)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        String stri9999="1";
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            /**取得子集记录最大值*/
            buf.append("select max(i9999) i9999 from ");
            buf.append(table);
            buf.append(" where a0100=?");
            ArrayList list=new ArrayList();
            list.add(a0100);

            RowSet rset=dao.search(buf.toString(),list);
            if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
            /**先插入子集记录*/
            buf.setLength(0);
            buf.append("insert into ");
            buf.append(table);
            buf.append("(a0100,I9999) values(?,?)");
            list.clear();
            list.add(a0100);
            list.add(stri9999);
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return stri9999;
    }

    /**
     * 插入子集记录,在最后插入一条记录
     * @param table     子集名称，带应用库前缀
     * @param a0100　    人员编号
     * @param dbconn    数据库连接
     * @param username  用户名
     * @return
     * @throws GeneralException
     */
    /*synchronized*/ static public  String insertSubSet(String table,String keyValue,Connection dbconn,String username,int infor_type)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        String stri9999="1";
        try
        {
            String keyField="a0100";
            if(infor_type==2) {
                keyField="b0110";
            } else if(infor_type==3) {
                keyField="e01a1";
            }
            ContentDAO dao=new ContentDAO(dbconn);
            /**取得子集记录最大值*/
            buf.append("select max(i9999) i9999 from ");
            buf.append(table);
            buf.append(" where "+keyField+"=?");
            ArrayList list=new ArrayList();
            list.add(keyValue);

            RowSet rset=dao.search(buf.toString(),list);
            if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
            /**先插入子集记录*/
            buf.setLength(0);
            buf.append("insert into ");
            buf.append(table);
            buf.append("("+keyField+",I9999,createtime,createusername) values(?,?,");
            buf.append(Sql_switcher.sqlNow());
            buf.append(",?)");
            list.clear();
            list.add(keyValue);
            list.add(stri9999);
            list.add(username);
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return stri9999;
    }

    /**
     * 插入子集记录,在最后插入一条记录
     * @param table     子集名称，带应用库前缀
     * @param a0100　    人员编号
     * @param dbconn    数据库连接
     * @param username  用户名
     * @return
     * @throws GeneralException
     */
    /*synchronized*/ static public  String insertSubSetA0100(String table,String a0100,Connection dbconn,String username)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        String stri9999="1";
        try
        {
            ContentDAO dao=new ContentDAO(dbconn);
            /**取得子集记录最大值*/
            buf.append("select max(i9999) i9999 from ");
            buf.append(table);
            buf.append(" where a0100=?");
            ArrayList list=new ArrayList();
            list.add(a0100);

            RowSet rset=dao.search(buf.toString(),list);
            if(rset.next()) {
                stri9999=String.valueOf(rset.getInt("i9999")+1);
            }
            /**先插入子集记录*/
            buf.setLength(0);
            buf.append("insert into ");
            buf.append(table);
            buf.append("(a0100,I9999,createtime,createusername) values(?,?,");
            buf.append(Sql_switcher.sqlNow());
            buf.append(",?)");
            list.clear();
            list.add(a0100);
            list.add(stri9999);
            list.add(username);
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return stri9999;
    }

    /**
     *
     * @param table
     * @param a0100 人员编号
     * @param currI9999 在当前记录序号前插入一条记录
     * @param dbconn
     * @return
     * @throws GeneralException
     */
    /*synchronized*/ static public  String insertSubSetA0100(String table,String a0100,String currI9999,Connection dbconn)throws GeneralException
    {
        StringBuffer buf=new StringBuffer();
        String stri9999="1";
        try
        {
            ArrayList list=new ArrayList();
            list.add(a0100);
            ContentDAO dao=new ContentDAO(dbconn);
            if(!("".equalsIgnoreCase(currI9999)|| "0".equalsIgnoreCase(currI9999)))
            {
                /**把当前记录以后的记录中I9999+1*/
                buf.append("update ");
                buf.append(table);
                buf.append(" set I9999=I9999+1");
                buf.append(" where A0100=? and I9999>=");
                buf.append(currI9999);
                dao.update(buf.toString(), list);
                stri9999=currI9999;
            }
            else//当前记录为空时
            {
                /**取得子集记录最大值*/
                buf.append("select max(i9999) i9999 from ");
                buf.append(table);
                buf.append(" where a0100=?");
                //list.add(a0100);
                RowSet rset=dao.search(buf.toString(),list);
                if(rset.next()) {
                    stri9999=String.valueOf(rset.getInt("i9999")+1);
                }
            }
            /**先插入子集记录*/
            buf.setLength(0);
            buf.append("insert into ");
            buf.append(table);
            buf.append("(a0100,I9999) values(?,?)");
            list.clear();
            list.add(a0100);
            list.add(stri9999);
            dao.update(buf.toString(), list);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return stri9999;
    }

    /**
     * 取得当前表的字段串
     * @param tablename
     * @return   a0111,a0202,a0304
     */
    private String getFieldColumns(String tablename)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
        for(int i=0;i<fieldlist.size();i++)
        {
            FieldItem item=(FieldItem)fieldlist.get(i);
            buf.append(item.getItemid());
            buf.append(",");
        }
        /**去掉","*/
        if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
        return buf.toString();
    }
    /**
     * 取得当前表的字段串
     * @param tablename
     * @return   a0111,a0202,a0304
     */
    private String getSelectFieldColumns(String tablename)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=DataDictionary.getFieldList(tablename,Constant.USED_FIELD_SET);
        for(int i=0;i<fieldlist.size();i++)
        {
            FieldItem item=(FieldItem)fieldlist.get(i);
            if("D".equalsIgnoreCase(item.getItemtype())){
                buf.append(Sql_switcher.charToDate(item.getItemid()));
            }else {
                buf.append(item.getItemid());
            }
            buf.append(",");
        }
        /**去掉","*/
        if(buf.length()>0) {
            buf.setLength(buf.length()-1);
        }
        return buf.toString();
    }

    /**移库操作 如从usra01移库到reta01
     * A00...AXX中的数据
     * @param srca0100 源库中需要移动的人员编号
     * @param srcbase  源库名
     * @param destbase 目标库名
     * @param conn     数据库连接
     * @param move_person 人员移库后是否删除原库中的信息,1删除(默认值),0保留
     * @return 返回值 为目标库生成人员编号
     */
        public String moveDataBetweenBase2(String srca0100,String srcbase ,String destbase,String move_person)throws GeneralException
        {
        String a0100="";
        /**目标库主集*/
        StringBuffer buf=new StringBuffer();
        String maindest=destbase+"A01";
        String mainsrc=srcbase+"A01";
        String subdest=null;
        String subsrc=null;
        String fieldsetid=null;
        String fieldstrs=null;
        FieldSet fieldset=null;
        ArrayList paralist=new ArrayList();
        try
        {
            DbWizard dbw =new DbWizard(this.conn);
            ContentDAO dao=new ContentDAO(this.conn);
            /**查询*/
            RecordVo vo=new RecordVo(mainsrc);
            vo.setString("a0100", srca0100);
            vo=dao.findByPrimaryKey(vo);
            /**先插入主集编号*/
            a0100=insertMainSetA0100(maindest,vo.getString("guidkey"),this.conn);
            ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            for(int i=0;i<setlist.size();i++)
            {
                fieldset=(FieldSet)setlist.get(i);
                if(fieldset.isMainset())
                {
                    if(!isHaveA0100(mainsrc,srca0100)) {
                        continue;
                    }
                    if (dbw.isExistField(mainsrc, "GUIDKEY", false)){//源表有，目标表也需要加
                        addGuidKeyField(dbw,maindest);
                    }

                    RecordVo dest_vo=new RecordVo(maindest);
                    if(vo!=null)
                    {
                        dest_vo.setValues(vo.getValues());
                        dest_vo.setString("a0100", a0100);
                        dest_vo.removeValue("a0000");
                        dao.updateValueObject(dest_vo);
                    }
                }
                else//子集
                {
                    fieldsetid=fieldset.getFieldsetid();
                    subdest=destbase+fieldsetid;
                    subsrc=srcbase+fieldsetid;
                    boolean bHaveGuid =false;
                    if (dbw.isExistField(subsrc, "GUIDKEY", false)){//源表有，目标表也需要加
                        addGuidKeyField(dbw,subdest);
                        if (dbw.isExistField(subdest, "GUIDKEY", false)) {
                            bHaveGuid=true;
                        }
                    }

                    /**先清空目标对应的人员编号的子集记录,子集可能会存在有头案记录*/
                    buf.append("delete from ");
                    buf.append(subdest);
                    buf.append(" where a0100=?");
                    paralist.add(a0100);
                    dao.update(buf.toString(), paralist);
                    buf.setLength(0);
                    paralist.clear();
                    /**取得当前表字段列表*/
                    if("A00".equalsIgnoreCase(fieldsetid)) //多媒体子集
                    {
                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
                        buf.append(a0100);
                        buf.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    else //其它子集
                    {
                        fieldstrs=getFieldColumns(fieldsetid);
                        if (bHaveGuid){
                            if (fieldstrs.length()>0){
                                fieldstrs=fieldstrs+",guidkey";
                            }
                            else {
                                fieldstrs=fieldstrs+"guidkey";
                            }
                        }

                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(") select '");
                        buf.append(a0100);
                        buf.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(" from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    dao.update(buf.toString());
                }
                buf.setLength(0);
            }//for i loop end.
            /**清空原库*/
            if("1".equals(move_person))
            {
                for(int i=0;i<setlist.size();i++)
                {
                    /**清零*/
                    buf.setLength(0);
                    paralist.clear();
                    /**开始删除记录*/
                    fieldset=(FieldSet)setlist.get(i);
                    fieldsetid=fieldset.getFieldsetid();
                    subsrc=srcbase+fieldsetid;
                    buf.append("delete from ");
                    buf.append(subsrc);
                    buf.append(" where a0100=?");
                    paralist.add(srca0100);
                    dao.update(buf.toString(), paralist);
                }
                changeBusiTableData(srca0100,srcbase,a0100,destbase); //修改业务表相关数据
            }
            else  //当不清除原有信息时，需修改a01表里 人员状态字段的值。使其为录用状态 （解决招聘调用模板的问题）
            {
                ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
                HashMap map=parameterXMLBo.getAttributeValues();
                if(map.get("resume_state") !=null && ((String)map.get("resume_state")).trim().length()>0)
                {
                    String resumeStateFieldIds=(String)map.get("resume_state");
                    RecordVo _vo=new RecordVo(srcbase+"a01");
                    if(_vo.hasAttribute(resumeStateFieldIds.toLowerCase()))
                    {
                        dao.update("update "+srcbase+"A01 set "+resumeStateFieldIds+"='43' where a0100='"+srca0100+"'  ");
                    }

                }
                //复制附件表 2014-05-04  wangrd
                copyBusiTableData(srca0100,srcbase,a0100,destbase);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return a0100;
    }


    private void addGuidKeyField(DbWizard dbw,String tablename )
    {
        try{
            if (!dbw.isExistField(tablename, "GUIDKEY", false)) {
                Table table = new Table(tablename);
                Field field = new Field("GUIDKEY","人员唯一标识");
                field.setDatatype(DataType.STRING);
                field.setKeyable(false);
                field.setLength(38);
                table.addField(field);
                dbw.addColumns(table);
            }
        }
        catch (Exception e ){
           e.printStackTrace();
        }
     }

    /**
     * A00...AXX中的数据
     * @param srca0100 源库中需要移动的人员编号
     * @param srcbase  源库名
     * @param destbase 目标库名
     * @param conn     数据库连接
     * @return 返回值 为目标库生成人员编号
     */
    public String moveDataBetweenBase3(String srca0100,String srcbase ,String destbase)throws GeneralException
    {
        String a0100="";
        /**目标库主集*/
        StringBuffer buf=new StringBuffer();
        String maindest=destbase+"A01";
        String mainsrc=srcbase+"A01";
        String subdest=null;
        String subsrc=null;
        String fieldsetid=null;
        String fieldstrs=null;
        FieldSet fieldset=null;
        ArrayList paralist=new ArrayList();
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            RecordVo dest_vo=new RecordVo(maindest);
            /**查询*/
            RecordVo vo=new RecordVo(mainsrc);
            vo.setString("a0100", srca0100);
            vo=dao.findByPrimaryKey(vo);
            /**先插入主集编号*/
            a0100=insertMainSetA0100(maindest,vo.getString("guidkey"),this.conn);
            ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            for(int i=0;i<setlist.size();i++)
            {
                fieldset=(FieldSet)setlist.get(i);
                if(fieldset.isMainset())
                {
                    if(vo!=null)
                    {
                        dest_vo.setValues(vo.getValues());
                        dest_vo.setString("a0100", a0100);
                        dest_vo.removeValue("a0000");
                        dao.updateValueObject(dest_vo);
                    }
                }
                else//子集
                {
                    fieldsetid=fieldset.getFieldsetid();
                    subdest=destbase+fieldsetid;
                    subsrc=srcbase+fieldsetid;
                    /**先清空目标对应的人员编号的子集记录,子集可能会存在有头案记录*/
                    buf.append("delete from ");
                    buf.append(subdest);
                    buf.append(" where a0100=?");
                    paralist.add(a0100);
                    dao.update(buf.toString(), paralist);
                    buf.setLength(0);
                    paralist.clear();
                    /**取得当前表字段列表*/
                    if("A00".equalsIgnoreCase(fieldsetid)) //多媒体子集
                    {
                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
                        buf.append(a0100);
                        buf.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    else //其它子集
                    {
                        fieldstrs=getFieldColumns(fieldsetid);

                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(") select '");
                        buf.append(a0100);
                        buf.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(" from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    dao.update(buf.toString());
                }
                buf.setLength(0);
            }//for i loop end.

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return a0100;
    }

    public boolean isHaveA0100(String tablename,String a0100) throws GeneralException
    {
        boolean isHave = false;
        RowSet rowSet = null;
        try
        {
            String sql = "SELECT a0100 FROM "+tablename+" WHERE a0100 = '" + a0100 + "'";
            ContentDAO dao = new ContentDAO(this.conn);
            rowSet = dao.search(sql);
            if(rowSet.next())
            {
                isHave=true;
            }
        }
        catch(Exception sqle)
        {
           sqle.printStackTrace();
          throw GeneralExceptionHandler.Handle(sqle);
        }
	    finally{
        	PubFunc.closeDbObj(rowSet);
        }
        return isHave;
    }

    public void deleteBusiTableData(ContentDAO dao,String tableName,String desc_a0100,String desc_nbase)throws GeneralException
    {
        try
        {
            DbWizard db = new DbWizard(this.conn);
            ArrayList params = new ArrayList();
            StringBuffer sql = new StringBuffer();
            
            if(!"Q35".equalsIgnoreCase(tableName) ||
                    (!db.isExistField("Q35", "guidkey", false) && db.isExistField("Q35", "a0100", false))) {
                sql.append("delete from ").append(tableName);
                sql.append(" where a0100=?");
                sql.append(" and upper(nbase)=?");
                
                params.add(desc_a0100);
                params.add(desc_nbase.toUpperCase());                
                
               dao.update(sql.toString(), params);
            } else {
            	// 57602 移库操作 需保留Q35（新考勤）的记录 不需要删除
//                if(db.isExistField("Q35", "guidkey", false)) {
//                    sql.setLength(0);
//                    sql.append("delete from Q35");
//                    sql.append(" where guidkey=(select guidkey from ").append(desc_nbase).append("A01");
//                    sql.append(" where a0100=?)");
//                    
//                    params.clear();
//                    params.add(desc_a0100);
//                }
            }
            
            if (sql.toString().length()>0) {
                dao.update(sql.toString(), params);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

   /**
 * @Title: CopyBusiTableData
 * @Description:  复制业务表
 * @param @param src_a0100
 * @param @param src_nbase
 * @param @param desc_a0100
 * @param @param desc_nbase
 * @param @throws GeneralException
 * @return void
 * @author:wangrd
 * @throws
*/
public void copyBusiTableData(String src_a0100,String src_nbase,String desc_a0100,String desc_nbase)throws GeneralException
    {
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            StringBuffer buf = new StringBuffer();
            buf.append("select * from hr_multimedia_file");
            buf.append(" where dbflag='A'");
            buf.append(" and nbase ='").append(src_nbase).append("'");
            buf.append(" and a0100='").append(src_a0100).append("'");
            buf.append(" order by displayorder");
            RowSet frowset = dao.search(buf.toString());
            while (frowset.next()){
                String mainguid=frowset.getString("mainguid");
                String childguid=frowset.getString("childguid");
                String filetype=frowset.getString("class");
                String topic=frowset.getString("topic");
                String description=frowset.getString("description");
                String path=frowset.getString("path");
                String filename=frowset.getString("filename");
                String ext=frowset.getString("ext");
                String srcfilename=frowset.getString("srcfilename");
                String createusername=frowset.getString("createusername");

                RecordVo vo = new RecordVo("hr_multimedia_file");
                IDGenerator idg=new IDGenerator(2,this.conn);
                String id=idg.getId("hr_multimedia_file.id");
                vo.setInt("id",Integer.parseInt(id));
                vo.setInt("displayorder", Integer.parseInt(id));
                vo.setString("mainguid", mainguid);
                vo.setString("childguid", childguid);
                vo.setString("nbase", desc_nbase);
                vo.setString("a0100", desc_a0100);
                vo.setString("dbflag", "A");
                vo.setString("class", filetype);
                vo.setString("topic", topic);
                vo.setString("description", description);
                vo.setString("path", path);
                vo.setString("filename", filename);
                vo.setString("ext", ext);
                vo.setString("srcfilename", srcfilename);
                vo.setString("createusername", createusername);
                Date date = DateUtils.getSqlDate(Calendar.getInstance());
                vo.setDate("createtime",date);
                dao.addValueObject(vo);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    /**
     * 修改业务表相关数据
     * @param src_a0100
     * @param src_nbase
     * @param desc_a0100
     * @param desc_nbase
     */
    public void  changeBusiTableData(String src_a0100,String src_nbase,String desc_a0100,String desc_nbase)throws GeneralException
    {
        RowSet rowSet = null;
        try
        {
        	ArrayList list = DataDictionary.getDbpreList();//不从数据库去取，直接从DataDictionary拿 sunjian

            for(int i = 0; i < list.size(); i++) {
            	if(list.get(i).toString().equalsIgnoreCase(desc_nbase)) {
            		desc_nbase = list.get(i).toString();
            		break;
            	}
            }
            //2019-3-29 wangrd 移库的后台作业问题：1、没有传userview，2、desc_nbase全是小写，直接取dbname表中的。
            boolean isNewVersion=true;
            if (this.userView!=null) {
            	isNewVersion=this.userView.getVersion()>=70?true:false;
            }
            String gz_desc_nbase=isNewVersion?desc_nbase:desc_nbase.toUpperCase();
            ContentDAO dao=new ContentDAO(this.conn);
            DbWizard dbwizard = new DbWizard(this.conn);

            rowSet=dao.search("select distinct userflag,salaryid from salary_mapping where a0100='"+src_a0100+"' and lower(nbase)='"+src_nbase.toLowerCase()+"'");
            while(rowSet.next())
            {
                String userflag=rowSet.getString("userflag");
                String salaryid=rowSet.getString("salaryid");
                String tablename=userflag+"_salary_"+salaryid;
                if(dbwizard.isExistTable(tablename, false)&&dbwizard.isExistField(tablename, "nbase", false))
                {
                    deleteBusiTableData(dao,tablename,desc_a0100,desc_nbase);
                    dao.update("update "+tablename+" set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                }
            }

            deleteBusiTableData(dao,"SalaryHistory",gz_desc_nbase,desc_nbase);
            deleteBusiTableData(dao,"gz_tax_mx",gz_desc_nbase,desc_nbase);
            deleteBusiTableData(dao,"salary_mapping",gz_desc_nbase,desc_nbase);
            deleteBusiTableData(dao,"Q03",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("Q03_arc", false)) {
                deleteBusiTableData(dao,"Q03_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"Q05",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("Q05_arc", false)) {
                deleteBusiTableData(dao,"Q05_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"Q15",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("Q15_arc", false)) {
                deleteBusiTableData(dao,"Q15_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"Q11",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("Q11_arc", false)) {
                deleteBusiTableData(dao,"Q11_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"Q13",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("Q13_arc", false)) {
                deleteBusiTableData(dao,"Q13_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"Q17",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"Q19",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"Q25",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"Q31",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"Q33",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"Q35",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"kq_employ_shift",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("kq_employ_shift_arc", false)) {
                deleteBusiTableData(dao,"kq_employ_shift_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"kq_originality_data",desc_a0100,desc_nbase);
			if(dbwizard.isExistTable("kq_originality_data_arc", false)) {
                deleteBusiTableData(dao,"kq_originality_data_arc",desc_a0100,desc_nbase);
            }
            deleteBusiTableData(dao,"t_hr_mydata_chg",desc_a0100,desc_nbase);
            deleteBusiTableData(dao,"hr_multimedia_file",desc_a0100,desc_nbase);
            //处理培训模块相关的表
            if (dbwizard.isExistTable("R04", false)) {
                deleteBusiTableData(dao, "R04", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("R47", false)) {
                deleteBusiTableData(dao, "R47", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("R55", false)) {
                deleteBusiTableData(dao, "R55", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("R59", false)) {
                deleteBusiTableData(dao, "R59", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("R61", false)) {
                deleteBusiTableData(dao, "R61", desc_a0100, desc_nbase);
            }

            // deleteBusiTableData(dao,"tr_attendence",desc_a0100,desc_nbase);

            if (dbwizard.isExistTable("tr_award_exchange", false)) {
                deleteBusiTableData(dao, "tr_award_exchange", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_cardtime", false)) {
                deleteBusiTableData(dao, "tr_cardtime", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_course_comments", false)) {
                deleteBusiTableData(dao, "tr_course_comments", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_exam_answer", false)) {
                deleteBusiTableData(dao, "tr_exam_answer", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_selected_course", false)) {
                deleteBusiTableData(dao, "tr_selected_course", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_selected_course_scorm", false)) {
                deleteBusiTableData(dao, "tr_selected_course_scorm", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_selected_lesson", false)) {
                deleteBusiTableData(dao, "tr_selected_lesson", desc_a0100, desc_nbase);
            }

            if (dbwizard.isExistTable("tr_selfexam_paper", false)) {
                deleteBusiTableData(dao, "tr_selfexam_paper", desc_a0100, desc_nbase);
            }

            /**********删除标准考核关系记录***************/
            /*if("usr".equalsIgnoreCase(src_nbase)){
                dao.update(" delete from per_mainbody_std where mainbody_id='"+src_a0100+"' or object_id='"+src_a0100+"' ");
                dao.update(" delete from per_object_std where object_id='"+src_a0100+"'");
            }*/ //缺陷1204 考核实施，已经设置好了考核关系，然后将考核主体进行了移库，结果考核主体和指标权限删除不了了。   赵国栋 2014-7-7  修改为“移库时不处理绩效的表”

            //删除招聘关联数据
            RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
            String dbname="";
            if(vo!=null) {
                dbname=vo.getString("str_value");
            }
            if(dbname!=null&&dbname.trim().length()>0&&dbname.equalsIgnoreCase(src_nbase))
            {
                if(dbwizard.isExistTable("zp_pos_tache", false)) {
                    dao.update("delete from zp_pos_tache where a0100='"+src_a0100+"'");        //候选人应聘职位对应表
                }

                if(dbwizard.isExistTable("z05", false)) {
                    dao.update("delete from z05 where a0100='"+src_a0100+"'");                //面试安排信息表
                }

                if(dbwizard.isExistTable("zp_test_template", false)) {
                    dao.update("delete from zp_test_template   where a0100='"+src_a0100+"'");//测评信息表
                }

                if(dbwizard.isExistTable("zp_comment_info", false)) {
                    dao.update("delete from zp_comment_info where a0100='"+src_a0100+"'");  //应聘人员评语表
                }

                if(dbwizard.isExistTable("zp_resume_pack", false)) {
                    dao.update("delete from zp_resume_pack   where a0100='"+src_a0100+"'");//收藏夹
                }
            }

            if(dbwizard.isExistTable("SalaryHistory", false)) {
                dao.update("update SalaryHistory set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            if(dbwizard.isExistTable("gz_tax_mx", false)) {
                dao.update("update gz_tax_mx set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            if(dbwizard.isExistTable("salaryarchive", false))
            {
                deleteBusiTableData(dao,"salaryarchive",desc_a0100,gz_desc_nbase);
                dao.update("update  salaryarchive set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if(dbwizard.isExistTable("taxarchive", false))
            {
                deleteBusiTableData(dao,"taxarchive",desc_a0100,gz_desc_nbase);
                dao.update("update  taxarchive set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }


            if(dbwizard.isExistTable("salary_mapping", false)) {
                dao.update("update salary_mapping set NBase ='"+gz_desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            // 更新虚拟人员表
            if(dbwizard.isExistTable("t_vorg_staff", false)) {
                dao.update("delete from t_vorg_staff where a0100='"+desc_a0100+"' and upper(DBase)='"+desc_nbase.toUpperCase()+"'");
                dao.update("update t_vorg_staff set DBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(DBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //培训模块
            //更新培训学员表
            if (dbwizard.isExistTable("R04", false)) {
                dao.update("delete from R40 where R4001='" + desc_a0100 + "' and upper(NBase)='" + desc_nbase.toUpperCase() + "'");
                dao.update("update R40 set NBase ='" + desc_nbase + "', R4001='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and R4001='" + src_a0100 + "'");
            }
            // 更新培训教师表
            if (dbwizard.isExistTable("R04", false)) {
                dao.update("update R04 set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100 + "'");
            }
            // 更新培训考勤汇总表
            if (dbwizard.isExistTable("R47", false)) {
                dao.update("update R47 set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100 + "'");
            }
            // 更新参考人员表
            if (dbwizard.isExistTable("R55", false)) {
                dao.update("update R55 set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100 + "'");
            }
            // 更新培训设施使用表
            if (dbwizard.isExistTable("R59", false)) {
                dao.update("update R59 set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100 + "'");
            }
            // 更新培训场所使用表
            if (dbwizard.isExistTable("R61", false)) {
                dao.update("update R61 set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100 + "'");
            }

            // dao.update("update tr_attendence set nbase ='"+desc_nbase.toUpperCase()+"', a0100='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and a0100='"+src_a0100+"'");
            // 更新兑换奖品表
            if (dbwizard.isExistTable("tr_award_exchange", false)) {
                dao.update("update tr_award_exchange set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }
            // 更新培训考勤签到表
            if (dbwizard.isExistTable("tr_cardtime", false)) {
                dao.update("update tr_cardtime set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='" + src_a0100
                        + "'");
            }
            // 更新课程评论表
            if (dbwizard.isExistTable("tr_course_comments", false)) {
                dao.update("update tr_course_comments set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }
            // 更新考试答案表
            if (dbwizard.isExistTable("tr_exam_answer", false)) {
                dao.update("update tr_exam_answer set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }
            // 更新选课课件表
            if (dbwizard.isExistTable("tr_selected_course", false)) {
                dao.update("update tr_selected_course set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }
            // 更新选课scorm课件表
            if (dbwizard.isExistTable("tr_selected_course_scorm", false)) {
                dao.update("update tr_selected_course_scorm set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase()
                        + "' and a0100='" + src_a0100 + "'");
            }
            // 更新选课表
            if (dbwizard.isExistTable("tr_selected_lesson", false)) {
                dao.update("update tr_selected_lesson set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }
            // 更新自测考试表
            if (dbwizard.isExistTable("tr_selfexam_paper", false)) {
                dao.update("update tr_selfexam_paper set nbase ='" + desc_nbase + "', a0100='" + desc_a0100 + "'  where Upper(NBase)='" + src_nbase.toUpperCase() + "' and a0100='"
                        + src_a0100 + "'");
            }

            //考勤模块
            // 更新考勤日明细
            if (dbwizard.isExistTable("Q03", false)) {
                dao.update("update  Q03 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("Q03_arc", false)) {
                dao.update("update  Q03_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤月汇总
            if (dbwizard.isExistTable("Q05", false)) {
                dao.update("update  Q05 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("Q05_arc", false)) {
                dao.update("update  Q05_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

           // 更新考勤请假表
            if (dbwizard.isExistTable("Q15", false)) {
                dao.update("update  Q15 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("Q15_arc", false)) {
                dao.update("update  Q15_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤加班表
            if (dbwizard.isExistTable("Q11", false)) {
                dao.update("update  Q11 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("Q11_arc", false)) {
                dao.update("update  Q11_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤公出表
            if (dbwizard.isExistTable("Q13", false)) {
                dao.update("update  Q13 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("Q13_arc", false)) {
                dao.update("update  Q13_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤假期管理表
            if (dbwizard.isExistTable("Q17", false)) {
                dao.update("update  Q17 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤调班表
            if (dbwizard.isExistTable("Q19", false)) {
                dao.update("update  Q19 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤调休表
            if (dbwizard.isExistTable("Q25", false)) {
                dao.update("update  Q25 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤个人休假计划申请表
            if (dbwizard.isExistTable("Q31", false)) {
                dao.update("update  Q31 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新考勤调休明细表
            if (dbwizard.isExistTable("Q33", false)) {
                dao.update("update  Q33 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新月度考勤数据明细表(国网)
            if (!dbwizard.isExistField("Q35", "guidkey", false) && dbwizard.isExistField("Q35", "A0100", false)) {
                dao.update("update  Q35 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            // 更新员工排班信息表
            if (dbwizard.isExistTable("kq_employ_shift", false)) {
                dao.update("update  kq_employ_shift set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("kq_employ_shift_arc", false)) {
                dao.update("update  kq_employ_shift_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //更新员工原始刷卡信息
            if (dbwizard.isExistTable("kq_originality_data", false)) {
                dao.update("update  kq_originality_data set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }
            if (dbwizard.isExistTable("kq_originality_data_arc", false)) {
                dao.update("update  kq_originality_data_arc set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //更新班组信息
            if (dbwizard.isExistTable("kq_group_emp", false)) {
                dao.update("update  kq_group_emp set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //更新存储不直接入库人员修改的信息
            if (dbwizard.isExistTable("t_hr_mydata_chg", false)) {
                dao.update("update  t_hr_mydata_chg set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //更新附件表
            if (dbwizard.isExistTable("hr_multimedia_file", false)) {
                dao.update("update  hr_multimedia_file set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
            }

            //更新工作计划、工作总结表
            if (dbwizard.isExistTable("p07", false)){
               try
                {
                   //工作计划表
                   deleteBusiTableData(dao, "p07", desc_a0100, desc_nbase);
                   dao.update("update  p07 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                   //工作总结表
                   deleteBusiTableData(dao, "p01", desc_a0100, desc_nbase);
                   dao.update("update  p01 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                   //工作任务映射表
                   deleteBusiTableData(dao, "per_task_map", desc_a0100, desc_nbase);
                   dao.update("update  per_task_map set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                   //工作任务表
                   deleteBusiTableData(dao, "p09", desc_a0100, desc_nbase);
                   dao.update("update  p09 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                   //任务评分表
                   deleteBusiTableData(dao, "per_task_evaluation ", desc_a0100, desc_nbase);
                   dao.update("update  per_task_evaluation  set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                   dao.update("delete from per_task_evaluation where evaluator_a0100='"+desc_a0100+"' and upper(evaluator_nbase)='"+desc_nbase.toUpperCase()+"'");
                   dao.update("update  per_task_evaluation  set evaluator_nbase ='"+desc_nbase+"', evaluator_a0100 ='"+desc_a0100+"'  where Upper(evaluator_nbase)='"+src_nbase.toUpperCase()+"' and evaluator_a0100='"+src_a0100+"'");
                   //工作沟通表
                   deleteBusiTableData(dao, "per_project_discussion", desc_a0100, desc_nbase);
                   dao.update("update  per_project_discussion set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");

                   //项目工时-项目成员表
                   deleteBusiTableData(dao, "p13", desc_a0100, desc_nbase);
                   dao.update("update  p13 set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");

                   deleteBusiTableData(dao, "per_opt_history", desc_a0100, desc_nbase);
                   dao.update("update  per_opt_history set NBase ='"+desc_nbase+"', A0100 ='"+desc_a0100+"'  where Upper(NBase)='"+src_nbase.toUpperCase()+"' and A0100='"+src_a0100+"'");
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

            }

            //人员移库后角色和权限转移  guodd 2017-11-29
            String privTransferSql = "update t_sys_function_priv set id=? where Upper(id)=?";
            ArrayList params = new ArrayList();
            params.add(desc_nbase+desc_a0100);
            params.add((src_nbase+src_a0100).toUpperCase());
            dao.update(privTransferSql, params);

            privTransferSql = "update t_sys_staff_in_role set staff_id=? where Upper(staff_id)=?";
            dao.update(privTransferSql,params);
            //更新人事异动未结束的单据包含此人的数据
            String _static="static";
            if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
            	_static="static_o";
            }
            String sql = "select a.ins_id,a.tabid,b."+_static+" from t_wf_instance a,template_table b where a.tabid=b.tabid and a.finished=2 order by a.tabid";
            rowSet = dao.search(sql);
            while(rowSet.next()) {
            	int ins_id = rowSet.getInt("ins_id");
            	int tabid = rowSet.getInt("tabid");
            	int templateStatic = rowSet.getInt(_static);
            	//人员模板
            	if(templateStatic!=10&&templateStatic!=11
            	        && dbwizard.isExistTable("templet_"+tabid, false)) {
            		dao.update("update templet_"+tabid+" set basepre ='"+desc_nbase+"', a0100 ='"+desc_a0100+"'  where upper(basepre)='"+src_nbase.toUpperCase()+"' and a0100='"+src_a0100+"' and ins_id="+ins_id);
            	}
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }finally{
            if(rowSet!=null) {
                try {
                    rowSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * A00...AXX中的数据
     * @param srca0100 源库中需要移动的人员编号
     * @param srcbase  源库名
     * @param destbase 目标库名
     * @param conn     数据库连接
     * @return 返回值 为目标库生成人员编号
     */
    public String moveDataBetweenBase(String srca0100,String srcbase ,String destbase)throws GeneralException
    {
        String a0100="";
        /**目标库主集*/
        StringBuffer buf=new StringBuffer();
        String maindest=destbase+"A01";
        String mainsrc=srcbase+"A01";
        String subdest=null;
        String subsrc=null;
        String fieldsetid=null;
        String fieldstrs=null;
        FieldSet fieldset=null;
        ArrayList paralist=new ArrayList();
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            /**先插入主集编号*/
            a0100=insertMainSetA0100(maindest,this.conn);
            ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            for(int i=0;i<setlist.size();i++)
            {
                fieldset=(FieldSet)setlist.get(i);
                if(fieldset.isMainset())
                {
                    RecordVo dest_vo=new RecordVo(maindest);
                    /**查询*/
                    RecordVo vo=new RecordVo(mainsrc);
                    vo.setString("a0100", srca0100);
                    vo=dao.findByPrimaryKey(vo);
                    if(vo!=null)
                    {
                        dest_vo.setValues(vo.getValues());
                        dest_vo.setString("a0100", a0100);
                        dest_vo.removeValue("a0000");
                        dao.updateValueObject(dest_vo);
                    }
                }
                else//子集
                {
                    fieldsetid=fieldset.getFieldsetid();
                    subdest=destbase+fieldsetid;
                    subsrc=srcbase+fieldsetid;
                    /**先清空目标对应的人员编号的子集记录,子集可能会存在有头案记录*/
                    buf.append("delete from ");
                    buf.append(subdest);
                    buf.append(" where a0100=?");
                    paralist.add(a0100);
                    dao.update(buf.toString(), paralist);
                    buf.setLength(0);
                    paralist.clear();
                    /**取得当前表字段列表*/
                    if("A00".equalsIgnoreCase(fieldsetid)) //多媒体子集
                    {
                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
                        buf.append(a0100);
                        buf.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    else //其它子集
                    {
                        fieldstrs=getFieldColumns(fieldsetid);

                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(") select '");
                        buf.append(a0100);
                        buf.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(" from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    dao.update(buf.toString());
                }
                buf.setLength(0);
            }//for i loop end.
            /**清空原库*/
            for(int i=0;i<setlist.size();i++)
            {
                /**清零*/
                buf.setLength(0);
                paralist.clear();
                /**开始删除记录*/
                fieldset=(FieldSet)setlist.get(i);
                fieldsetid=fieldset.getFieldsetid();
                subsrc=srcbase+fieldsetid;
                buf.append("delete from ");
                buf.append(subsrc);
                buf.append(" where a0100=?");
                paralist.add(srca0100);
                dao.update(buf.toString(), paralist);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return a0100;
    }
    public String checkOnlyName(String A0100,String dbpre,String toDbpre){
        String check="true";
        try {
            Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
            String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
            inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
            String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
            approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
            
            String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); //身份证指标
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name"); //验证唯一性指标
            String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");//身份证验证是否启用
            String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");//唯一性验证是否启用
            String dbchk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","db");//验证身份证适用的人员库
            dbchk=StringUtils.isNotEmpty(dbchk)?dbchk:DataDictionary.getDbpreString();
            String dbonly = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","db");//验证唯一性适用的人员库
            dbonly=StringUtils.isNotEmpty(dbonly)?dbonly:DataDictionary.getDbpreString();
            String idType = sysbo.getValue(Sys_Oth_Parameter.CHK_IdTYPE); // 证件类型指标
            String idTypeDefaultValue = sysbo.getIdTypeValue();// 身份证件类型默认值
            OtherParam op=new OtherParam(this.conn);
            Map cardMap=op.serachAtrr("/param/formual[@name='bycardno']");
            //是否启用身份证关联结算
            String cardflag = "false";
            if(cardMap!=null&&cardMap.size()==6){
                cardflag=(String) cardMap.get("valid");
            }
            String idTypeValue = "";
            RecordVo vo_old = getRecordVoA01(dbpre+"A01",A0100);
            if(StringUtils.isNotBlank(idType)) {
                idTypeValue = vo_old.getString(idType);
            }
            
            String fieldvalue = "";
            if("1".equals(uniquenessvalid)&&onlyname!=null&&onlyname.length()>0){
                if(dbonly.toUpperCase().indexOf(toDbpre.toUpperCase())!=-1){
                    fieldvalue = vo_old.getString(onlyname.toLowerCase());
                    if(fieldvalue!=null&&fieldvalue.trim().length()>0){
                        check = checkOnlyName(dbonly,"A01",onlyname,fieldvalue,"",dbpre);
                    }
                    //唯一性指标为空或格式不对时，直接返回false，不再进行身份证号的校验
                    if("false".equalsIgnoreCase(check)) {
                        return check;
                    }
                }
            }
            
            if(chk!=null&&chk.length()>0) {
                fieldvalue = vo_old.getString(chk.toLowerCase());
            }
            if (StringUtils.isNotBlank(chk)&&((StringUtils.isNotEmpty(idTypeDefaultValue)&&idTypeDefaultValue.equals(idTypeValue))||StringUtils.isEmpty(idType))) {
                if(dbchk.toUpperCase().indexOf(toDbpre.toUpperCase())!=-1 && !PubFunc.idCardValidate(fieldvalue)) {
                    check = "false";
                }
            }
            if("true".equals(cardflag)&&chk!=null&&chk.length()>0&&((StringUtils.isNotEmpty(idTypeDefaultValue)&&idTypeDefaultValue.equals(idTypeValue))||StringUtils.isEmpty(idType))){
                if(dbchk.toUpperCase().indexOf(toDbpre.toUpperCase())!=-1){
                    CorField cof = new CorField();
                    String sexitemId = cof.getItemid(CorField.SEX_ITEMID, this.conn);
                    String sex = "";
                    if(StringUtils.isNotEmpty(sexitemId)) {
                        sex = vo_old.getString(sexitemId);
                    }
                    
                    sex=sex!=null&&sex.trim().length()>0?sex:"";
                    
                    String birthdayItemId = cof.getItemid(CorField.BIRTHDAY_ITEMID, this.conn);
                    String birthday = "";
                    if(StringUtils.isNotEmpty(birthdayItemId)) {
                        birthday = vo_old.getString(birthdayItemId);
                    }
                    
                    birthday=birthday!=null&&birthday.trim().length()>0?birthday:"";
                    if(birthday.trim().length()>0){
                        WeekUtils weekUtils = new WeekUtils();
                        birthday=weekUtils.dateTostr(weekUtils.strTodate(birthday));
                        birthday=birthday.replaceAll("-", "").replaceAll("\\.","");
                    }
                    
                    if(fieldvalue!=null&&fieldvalue.trim().length()>0){
                        
                        String chkflag = checkIdNumber(fieldvalue,birthday,sex);
                        String arr[]= chkflag.split(":");
                        if(arr.length==2){
                            if("false".equalsIgnoreCase(arr[0])) {
                                check = arr[1];
                            }
                        }
                    }
                }
            }
            if("1".equals(chkvalid)&& "true".equalsIgnoreCase(check)&&chk!=null&&chk.length()>0){
                if(dbchk.toUpperCase().indexOf(toDbpre.toUpperCase())!=-1) {
                    check = checkOnlyName(dbchk,"A01",chk,fieldvalue,"",dbpre);
                    if("true".equalsIgnoreCase(check)){
                        check = checkOnlyName(dbchk,"A01",chk,changeCardID(fieldvalue, ""),"",dbpre);
                    }
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
        }
        return check;
    }
    /**
     * A00...AXX中的数据
     * @param srca0100 源库中需要移动的人员编号
     * @param srcbase  源库名
     * @param destbase 目标库名
     * @param conn     数据库连接
     * @return 返回值 为目标库生成人员编号
     */
    public String moveDataBetweenBase(String srca0100,String srcbase,String destbase,String check)throws GeneralException
    {
        String a0100="";
        /**目标库主集*/
        StringBuffer buf=new StringBuffer();
        String maindest=destbase+"A01";
        String mainsrc=srcbase+"A01";
        String subdest=null;
        String subsrc=null;
        String fieldsetid=null;
        String fieldstrs=null;
        FieldSet fieldset=null;
        ArrayList paralist=new ArrayList();
        String b0110 = "";
        String e0122 = "";
        String e01a1 = "";
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            /**先插入主集编号*/
            a0100=insertMainSetA0100(maindest,this.conn);
            ArrayList setlist=DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
            for(int i=0;i<setlist.size();i++)
            {
                fieldset=(FieldSet)setlist.get(i);
                if(fieldset.isMainset())
                {
                    RecordVo dest_vo=new RecordVo(maindest);
                    /**查询*/
                    RecordVo vo=new RecordVo(mainsrc);
                    vo.setString("a0100", srca0100);
                    vo=dao.findByPrimaryKey(vo);
                    if(vo!=null)
                    {
                        b0110 = vo.getString("b0110");
                        e0122 = vo.getString("e0122");
                        e01a1 = vo.getString("e01a1");
                        dest_vo.setValues(vo.getValues());
                        dest_vo.setString("a0100", a0100);
                        dest_vo.removeValue("a0000");
                    }
                    dao.updateValueObject(dest_vo);
                }
                else//子集
                {
                    fieldsetid=fieldset.getFieldsetid();
                    subdest=destbase+fieldsetid;
                    subsrc=srcbase+fieldsetid;
                    /**先清空目标对应的人员编号的子集记录,子集可能会存在有头案记录*/
                    buf.append("delete from ");
                    buf.append(subdest);
                    buf.append(" where a0100=?");
                    paralist.add(a0100);
                    dao.update(buf.toString(), paralist);
                    buf.setLength(0);
                    paralist.clear();
                    /**取得当前表字段列表*/
                    if("A00".equalsIgnoreCase(fieldsetid)) //多媒体子集
                    {
                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName) select '");
                        buf.append(a0100);
                        buf.append("',I9999,Title,Ole,Flag,State,Id,ext,CreateTime,ModTime,CreateUserName,ModUserName from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    else //其它子集
                    {
                        fieldstrs=getFieldColumns(fieldsetid);

                        buf.append("insert into ");
                        buf.append(subdest);
                        buf.append("(A0100,I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(") select '");
                        buf.append(a0100);
                        buf.append("',I9999,State,Id,CreateUserName,CreateTime,ModUserName,ModTime");
                        if(fieldstrs.length()>0)
                        {
                            buf.append(",");
                            buf.append(fieldstrs);
                        }
                        buf.append(" from ");
                        buf.append(subsrc);
                        buf.append(" where A0100='" + srca0100 + "'");
                    }
                    try{
                        dao.update(buf.toString());
                    }catch(Exception ex)
                    {}
                }
                buf.setLength(0);
            }//for i loop end.
            removeMainSetA0100(maindest,b0110,e0122,a0100);
            if(e01a1!=null&&e01a1.trim().length()>0){
                boolean inflag=overWorkOut(maindest,e01a1,1);
                //其他地方控制超编 wangrd 2013-09-27
                inflag=false;
                String unitdesc=AdminCode.getCodeName("@K",e01a1);
                if(inflag){
                    throw GeneralExceptionHandler.Handle(new GeneralException("",unitdesc+"人数超编！","",""));
                }
            }
            /**清空原库*/
            if("1".equals(check)){
                for(int i=0;i<setlist.size();i++)
                {
                    /**清零*/
                    buf.setLength(0);
                    paralist.clear();
                    /**开始删除记录*/
                    fieldset=(FieldSet)setlist.get(i);
                    fieldsetid=fieldset.getFieldsetid();
                    subsrc=srcbase+fieldsetid;
                    buf.append("delete from ");
                    buf.append(subsrc);
                    buf.append(" where a0100=?");
                    paralist.add(srca0100);
                    dao.update(buf.toString(), paralist);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        return a0100;
    }

    /**
     * 从兼职子集中取得人员列表  from XXXA01 ....
     * @param userview ,根据机构树进行过滤
     * @param dbpre
     * @param vorgcode
     * @return 返回值为空，则不用进行兼职子集内容的过滤
     */

    public String getQueryFromPartTime(UserView userview,String dbpre,String vorgcode)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             /**权限过滤*/
             strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
             buf.append(strWhere);
             String app_set=dbpre+setid;
             if(strWhere.toUpperCase().indexOf("WHERE")==-1)
             {
                    buf.append(" where "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             else
             {
                    buf.append(" and "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             buf.append(app_set);
             buf.append(" where ");
             buf.append(unit_field);
             buf.append(" = '"); //like 改成 = 某人在兼职子集中录入是部门代码，选中单位节点是出现此，部门也出现此人
             buf.append(vorgcode);
             buf.append("' and ");
             /*
             buf.append(" like '"); //= 改成 like
             buf.append(vorgcode);
             buf.append("%' and ");
             */
             buf.append(appoint_field);
             buf.append("='0')");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 从兼职子集中取得人员列表  from XXXA01 ....(like)
     * @param userview ,根据机构树进行过滤
     * @param dbpre
     * @param vorgcode
     * @return 返回值为空，则不用进行兼职子集内容的过滤
     */

    public String getQueryFromPartTimeLike(UserView userview,String dbpre,String vorgcode)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             /**权限过滤*/
             strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
             buf.append(strWhere);
             String app_set=dbpre+setid;
             if(strWhere.indexOf("WHERE")==-1)
             {
                    buf.append(" where "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             else
             {
                    buf.append(" and "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             buf.append(app_set);
             buf.append(" where ");
             buf.append(appoint_field);
             buf.append("='0'");

             if(vorgcode!=null&&vorgcode.length()>0)
             {
                 buf.append(" and "+unit_field);
                 buf.append(" like '"); //= 改成 like
                 buf.append(vorgcode);
                 buf.append("%'");
             }
             buf.append(")");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 从兼职子集中取得人员列表  from XXXA01 ....(like)
     * @param userview ,根据机构树进行过滤
     * @param dbpre
     * @param vorgcode
     * @return 返回值为空，则不用进行兼职子集内容的过滤
     */

    public String getQueryFromPartTimeLike(UserView userview,String dbpre,String vorgcode,boolean isLike)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             /**权限过滤*/
             strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
             buf.append(strWhere);
             String app_set=dbpre+setid;
             if(strWhere.indexOf("WHERE")==-1)
             {
                    buf.append(" where "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             else
             {
                    buf.append(" and "+dbpre+"A01.a0100 in (select a0100 from  ");
             }
             buf.append(app_set);
             buf.append(" where ");
             buf.append(appoint_field);
             buf.append("='0'");

             if(vorgcode!=null&&vorgcode.length()>0)
             {
                 buf.append(" and "+unit_field);
                 if(isLike)
                 {
                     buf.append(" like '"); //= 改成 like
                     buf.append(vorgcode);
                     buf.append("%'");
                 }else
                 {
                     buf.append(" = '"); //= 改成 like
                     buf.append(vorgcode);
                     buf.append("'");
                 }

             }
             buf.append(")");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 将高级条件转换为兼职条件
     * @param userView
     * @param conn
     * @return
     */
	private String condpriv2part(UserView userView,String app_set,String unit_field, String appoint_field, String dept_field,String dbpre){
        RowSet rs = null;
        String condpriv2part="";
        try
        {
            if(!userView.isSuper_admin()){
                String sql = "";
                /*if(userView.getStatus()!=0){
                    sql = " select condpriv from t_sys_function_priv where upper(id)='"+(userView.getDbname()+userView.getUserId()).toUpperCase()+"'";
                }else{
                    sql = " select condpriv from t_sys_function_priv where id='"+userView.getUserId()+"'";
                }
                ContentDAO dao = new ContentDAO(conn);
                rs=dao.search(sql);
                if(rs.next())*/
                {
                    //String condpriv=rs.getString("condpriv");
                    String condpriv=userView.getPrivExpression();
                    if(condpriv!=null&&condpriv.length()>0){
                        String tmps[]=condpriv.split("\\|");
                        if(tmps.length==2){
                            String expr=tmps[1].toUpperCase();
                            expr = expr.replaceAll("B0110", unit_field).replaceAll("E0122", dept_field);
                            String factor=tmps[0];
                            //bhistory 传true 处理兼职有多条在任的情况 wangrd 2014-02-25
                            FactorList factorslist=new FactorList(factor,expr,dbpre,true,false,true,1,userView.getUserId());
                            //System.out.println(factorslist.getSqlExpression());
                            sql = factorslist.getSqlExpression();
                            //condpriv2part = sql.substring(0,(sql.indexOf(".I9999")-11));

	    					//zxj 20151203 权限内的兼职，并且兼职任免标识为“任”
	    					if(appoint_field!=null&&appoint_field.length()>0)
	    					{
	    						sql = sql + " and "+app_set+"."+ appoint_field + "='0'";
	    					}
                            condpriv2part=sql;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return condpriv2part;
    }
    /**
     * 将高级条件转换为兼职条件
     * @param userView
     * @param conn
     * @return
     */
	private String condpriv2part(UserView userView,String app_set,String unit_field, String appoint_field, String dept_field,String pos_field,String dbpre){
        RowSet rs = null;
        String condpriv2part="";
        try
        {
            if(!userView.isSuper_admin()){
                String sql = "";
                /*if(userView.getStatus()!=0){
                    sql = " select condpriv from t_sys_function_priv where upper(id)='"+(userView.getDbname()+userView.getUserId()).toUpperCase()+"'";
                }else{
                    sql = " select condpriv from t_sys_function_priv where id='"+userView.getUserId()+"'";
                }
                ContentDAO dao = new ContentDAO(conn);
                rs=dao.search(sql);
                if(rs.next())*/
                {
                    //String condpriv=rs.getString("condpriv");
                    String condpriv=userView.getPrivExpression();
                    if(condpriv!=null&&condpriv.length()>0){
                        String tmps[]=condpriv.split("\\|");
                        if(tmps.length==2){
                            String expr=tmps[1].toUpperCase();
                            expr = expr.replaceAll("B0110", unit_field).replaceAll("E0122", dept_field).replace("E01A1", pos_field);
                            String factor=tmps[0];
                            FactorList factorslist=new FactorList(factor,expr,dbpre,true,false,true,1,userView.getUserId());
                            //System.out.println(factorslist.getSqlExpression());
                            sql = factorslist.getSqlExpression();
                            //condpriv2part = sql.substring(0,(sql.indexOf(".I9999")-11));

	    					//zxj 20151203 权限内的兼职，并且兼职任免标识为“任”
	    					if(appoint_field!=null&&appoint_field.length()>0)
	    					{
	    						sql = sql + " and "+app_set+"."+ appoint_field + "='0'";
	    					}
                            condpriv2part=sql;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return condpriv2part;
    }
    /**
     * 用exists从兼职子集中取得人员列表  from XXXA01 ....(like)
     * @param userview
     * @param dbpre
     * @param vorgcode
     * @param isLike
     * @return
     */
    public String getQueryFromPartTimeLikeExists(UserView userview,String dbpre,String vorgcode,boolean isLike,String main_Tablename,String kind)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             //兼职部门
             String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
             //兼职职位
             String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset = DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }

             FieldItem appointFieldItem = DataDictionary.getFieldItem(appoint_field);
             if(appointFieldItem==null || "0".equalsIgnoreCase(appointFieldItem.getUseflag())) {
                 return "";
             }

             FieldItem unitFieldItem = DataDictionary.getFieldItem(unit_field);
             FieldItem dept_fielditem = DataDictionary.getFieldItem(dept_field);
             FieldItem pos_fielditem = DataDictionary.getFieldItem(pos_field);

             /**权限过滤*/
             String app_set=dbpre+setid;
             buf.append(" select 1 from  ");
             buf.append(app_set +" JZ");
             buf.append(" where ");
             buf.append("JZ."+appoint_field);
             buf.append("='0'");

             if(vorgcode!=null&&vorgcode.length()>0)
             {
                 buf.append(" and (1=2");

                 if(unitFieldItem!=null && !"0".equalsIgnoreCase(unitFieldItem.getUseflag())) {
                     buf.append(" or ");
                     buf.append("JZ.").append(unit_field);
                     if(isLike)
                     {
                         buf.append(" like '"); //= 改成 like
                         buf.append(vorgcode);
                         buf.append("%'");
                     }else
                     {
                         buf.append(" in ('"); //= 改成 like
                         buf.append(vorgcode.replace(",", "','"));
                         buf.append("')");
                     }
                 }

                 if(dept_fielditem!=null&&!"0".equalsIgnoreCase(dept_fielditem.getUseflag()))
                 {
                     buf.append(" or ");
                     buf.append(" JZ."+dept_field);
                     if(isLike)
                     {
                         buf.append(" like '"); //= 改成 like
                         buf.append(vorgcode);
                         buf.append("%'");
                     }else
                     {
                         buf.append(" in ('"); //= 改成 like
                         buf.append(vorgcode.replace(",", "','"));
                         buf.append("')");
                     }
                 }

                 if(pos_fielditem!=null&&!"0".equalsIgnoreCase(pos_fielditem.getUseflag())&&"@K".equalsIgnoreCase(pos_fielditem.getCodesetid()))
                 {
                     buf.append(" or ");
                     buf.append(" JZ."+pos_field);
                     if(isLike)
                     {
                         buf.append(" like '"); //= 改成 like
                         buf.append(vorgcode);
                         buf.append("%'");
                     }else
                     {
                         buf.append(" in ('"); //= 改成 like
                         buf.append(vorgcode.replace(",", "','"));
                         buf.append("')");
                     }
                 }
                 buf.append(" )");
             }
             buf.append(" and "+main_Tablename+".a0100=JZ.a0100");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 只得到权限范围内的兼职人员
     * @param userview
     * @param dbpre
     * @param code
     * @param kind
     * @param isLike
     * @return
     */
    public String getQueryFromPartTimeLike(UserView userview,String dbpre,String code,String kind,boolean isLike)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        kind=kind!=null&&kind.length()>0?kind:"";
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             //兼职部门
             String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
             if("1".equals(kind))
             {
                 fielditem=DataDictionary.getFieldItem(dept_field);
                 if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                     return "";
                 }
             }
             /**权限过滤*/
               String expr="1";
               String factor="";
               if("2".equals(kind))
               {
                 factor="B0110=";
                 if(code!=null && code.length()>0)
                 {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                 }
                 else
                 {
                    expr="1+2";
                    factor+=code;
                    factor+="%`B0110=`";
                 }
               }
               else if("1".equals(kind)){
                    factor="E0122=";
                 if(code!=null && code.length()>0)
                 {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                 }
                 else
                 {
                    expr="1+2";
                   factor+=code;
                   factor+="%`E0122=`";
                 }
               }
               else if("0".equals(kind)){
                    factor="E01A1=";
                    if(code!=null && code.length()>0)
                    {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                    }
                     else
                   {
                      expr="1+2";
                      factor+=code;
                      factor+="%`E01A1=`";
                    }
             }
             strWhere=userview.getPrivSQLExpression(expr+"|"+factor,dbpre,false,true,fieldlist);
             String app_set=dbpre+setid;
             buf.append("select "+app_set+".a0100 from  ");
             buf.append(app_set);
             buf.append(" where 1=1 ");
             if(code!=null&&code.length()>0)
             {
                 if("1".equals(kind)) {
                     buf.append(" and "+app_set+"."+dept_field);
                 } else {
                     buf.append(" and "+app_set+"."+unit_field);
                 }
                 if(isLike)
                 {
                     buf.append(" like '"); //= 改成 like
                     buf.append(code);
                     buf.append("%'");
                 }else
                 {
                     buf.append(" = '"); //= 改成 like
                     buf.append(code);
                     buf.append("'");
                 }
             }
             if(appoint_field!=null&&appoint_field.length()>0)
             {
                 buf.append(" and "+app_set+"."+appoint_field);
                 buf.append("='0'");
             }
             if("2".equals(kind)) {
                 buf.append(" and "+app_set+"."+unit_field+" in(select b0110 "+strWhere+")");
             } else if(kind.endsWith("1")) {
                 buf.append(" and "+app_set+"."+unit_field+" in(select e0122 "+strWhere+")");
             } else if(kind.endsWith("0")) {
                 buf.append(" and "+app_set+"."+unit_field+" in(select e01a1 "+strWhere+")");
             }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 只得到权限范围内的兼职人员
     * @param userview
     * @param dbpre
     * @param code
     * @param kind
     * @param isLike
     * @return
     */
    public String getQueryFromPartTimeLikeExists(UserView userview,String dbpre,String code,String kind,boolean isLike,String main_Tablename)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        //if(userview.isBhighPriv())//判断是否是高级授权的用户
            //isLike=false;
        kind=kind!=null&&kind.length()>0?kind:"";
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
            //兼职部门
             String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept");
             //兼职职位
             String pos_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"pos");
             FieldItem dept_fielditem=null;
             FieldItem pos_fielditem=null;
             if(dept_field!=null&&dept_field.length()>0)
             {
                 dept_fielditem=DataDictionary.getFieldItem(dept_field);
             }
             if(pos_field!=null&&pos_field.length()>0)
             {
                 pos_fielditem=DataDictionary.getFieldItem(pos_field);
             }
             /*if(kind.equals("1"))
             {
                 if(dept_fielditem==null||dept_fielditem.getUseflag().equalsIgnoreCase("0"))
                     return "";
             }*/
             /**权限过滤*/
               String expr="1";
               String factor="";
               String infostr="B";
               if("2".equals(kind))
               {
                 factor="B0110=";
                 if(code!=null && code.length()>0)
                 {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                 }
                 else
                 {
                    expr="1+2";
                    factor+=code;
                    factor+="%`B0110=`";
                 }
               }
               else if("1".equals(kind)){
                    factor="B0110=";
                 if(code!=null && code.length()>0)
                 {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                 }
                 else
                 {
                    expr="1+2";
                   factor+=code;
                   factor+="%`B0110=`";
                 }
               }
               else if("0".equals(kind)){
                    factor="E01A1=";
                    if(code!=null && code.length()>0)
                    {
                     factor+=code;
                     if(isLike) {
                         factor+="%`";
                     } else {
                         factor+="`";
                     }
                    }
                     else
                   {
                      expr="1+2";
                      factor+=code;
                      factor+="%`E01A1=`";
                   }
                   infostr="K";
             }
             if(expr==null||expr.length()<=0||factor==null||factor.length()<=0) {
                 return "";
             } else {
                 strWhere=userview.getPrivSQLExpression(expr+"|"+factor,infostr,false,true,fieldlist);
             }
            /* FactorList factorlist=new FactorList(expr,factor.toString(),dbpre,false,false,true,infostr,"");
             strWhere=factorlist.getSqlExpression();*/
             //System.out.println(strWhere);
             //strWhere=strWhere.replaceAll(dbpre+"A01", main_Tablename);
             //main_Tablename=dbpre+"A01";
             if(strWhere.indexOf("WHERE")==-1)
             {
                 strWhere=strWhere+" where ";
             }
             else
             {
                 strWhere=strWhere+" and ";
             }
             String app_set=dbpre+setid;
             String condpriv2part="";
             if(pos_fielditem!=null&&!"0".equalsIgnoreCase(pos_fielditem.getUseflag())&&"@K".equalsIgnoreCase(pos_fielditem.getCodesetid())) {
                 condpriv2part = this.condpriv2part(userview, app_set, unit_field, appoint_field, dept_field,pos_field, dbpre);
             } else {
                 condpriv2part = this.condpriv2part(userview, app_set, unit_field, appoint_field, dept_field, dbpre);
             }
             if(condpriv2part.length()>0){
                // buf.append("select "+app_set+".a0100 from "+app_set+" where exists(select a0100 "+condpriv2part+")");
                 //wangrd 2014-02-25 增加主集标识"+dbpre+"A01."，处理兼职有多条在任的情况。
                 buf.append("select "+app_set+".a0100 from "+app_set+" where A0100 in (select "+dbpre+"A01."+"a0100 "+condpriv2part+")");
             }else{
                 buf.append("select "+app_set+".a0100 from  ");
                 buf.append(app_set);
                 buf.append(" where 1=1 ");
                 buf.append("and a0100 in(select a0100 "+userview.getPrivSQLExpression(dbpre, false)+") ");
             }
             if(code!=null&&code.length()>0)
             {
                 buf.append(" and ("+app_set+"."+unit_field);
                 if(isLike)//partisLike
                 {
                     buf.append(" like '"); //= 改成 like
                     buf.append(code);
                     buf.append("%'");
                 }else
                 {
                     buf.append(" = '"); //= 改成 like
                     buf.append(code);
                     buf.append("'");
                 }
                 if(dept_field!=null&&dept_field.length()>0)
                 {
                     if(dept_fielditem!=null&&!"0".equalsIgnoreCase(dept_fielditem.getUseflag()))
                     {
                         buf.append(" or "+app_set+"."+dept_field);
                         if(isLike)//partisLike
                         {
                             buf.append(" like '"); //= 改成 like
                             buf.append(code);
                             buf.append("%'");
                         }else
                         {
                             buf.append(" = '"); //= 改成 like
                             buf.append(code);
                             buf.append("'");
                         }
                     }
                 }
                 if(pos_fielditem!=null&&!"0".equalsIgnoreCase(pos_fielditem.getUseflag())&&"@K".equalsIgnoreCase(pos_fielditem.getCodesetid()))
                 {
                     buf.append(" or "+app_set+"."+pos_field);
                     if(isLike)
                     {
                         buf.append(" like '"); //= 改成 like
                         buf.append(code);
                         buf.append("%'");
                     }else
                     {
                         buf.append(" = '"); //= 改成 like
                         buf.append(code);
                         buf.append("'");
                     }
                 }
                 buf.append(")");
             }
             if(appoint_field!=null&&appoint_field.length()>0)
             {
                 buf.append(" and "+app_set+"."+appoint_field);
                 buf.append("='0'");
             }
             /*
             if(!kind.equals("0"))
             {
                 if(dept_field!=null&&dept_field.length()>0)
                 {
                     buf.append(" and exists(select b0110 "+strWhere+" ("+app_set+"."+unit_field+"=B01.b0110 ");
                     if(dept_fielditem!=null&&!dept_fielditem.getUseflag().equalsIgnoreCase("0"))
                     {
                         buf.append(" or "+app_set+"."+dept_field+"=B01.b0110");
                     }
                     buf.append("))");
                 }

                 else
                     buf.append(" and exists(select b0110 "+strWhere+" "+app_set+"."+unit_field+"=B01.b0110)");
             }
             else if(kind.endsWith("0")){
                 if(pos_fielditem!=null&&!pos_fielditem.getUseflag().equalsIgnoreCase("0")&&"@K".equalsIgnoreCase(pos_fielditem.getCodesetid()))
                     buf.append(" and exists(select e01a1 "+strWhere+" "+app_set+"."+pos_field+"=K01.e01a1)");
                 else
                     buf.append(" and exists(select e01a1 "+strWhere+" "+app_set+"."+unit_field+"=K01.e01a1)");
             }
             */
             buf.append(" and "+main_Tablename+".a0100="+app_set+".a0100");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 从兼职子集中取得人员列表  from XXXA01 ....(like)
     * @param userview ,根据机构树进行过滤
     * @param dbpre
     * @param vorgcode
     * @return 返回值为空，则不用进行兼职子集内容的过滤
     */

    public String getQueryFromPartLike(UserView userview,String dbpre,String vorgcode)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             /**权限过滤*/
//           strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
//           buf.append(strWhere);
             String app_set=dbpre+setid;
             buf.append("  "+dbpre+"A01.a0100 in (select a0100 from  ");
             buf.append(app_set);
             buf.append(" where ");
             buf.append(appoint_field);
             buf.append("='0'");

             if(vorgcode!=null&&vorgcode.length()>0)
             {
                 buf.append(" and "+unit_field);
                 buf.append(" like '"); //= 改成 like
                 buf.append(vorgcode);
                 buf.append("%'");
             }
             buf.append(")");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 从兼职子集中取得人员列表  from XXXA01 ....(like)
     * @param userview ,根据机构树进行过滤
     * @param dbpre
     * @param vorgcode
     * @return 返回值为空，则不用进行兼职子集内容的过滤
     */

    public String getQueryFromPartLike(UserView userview,String dbpre,
            String vorgcode,String mainsql)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
             Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
             /**兼职参数*/
             String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
             if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
                 return "";
             }
             String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
             /**兼职单位字段*/
             String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit");
             /**任免标识字段*/
             String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

             /**分析此定义的数据集和指标是否构库*/
             FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
             if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
                 return "";
             }
             FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             fielditem=DataDictionary.getFieldItem(appoint_field);
             if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
                 return "";
             }
             /**权限过滤*/
//           strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
//           buf.append(strWhere);
             String app_set=dbpre+setid;
             buf.append("  "+dbpre+"A01.a0100 in (select a0100 from  ");
             buf.append(app_set);
             buf.append(" where ");
             buf.append(appoint_field);
             buf.append("='0'");

             if(vorgcode!=null&&vorgcode.length()>0)
             {
                 buf.append(" and "+unit_field);
                 buf.append(" like '"); //= 改成 like
                 buf.append(vorgcode);
                 buf.append("%'");
             }
//           if(mainsql!=null&&mainsql.trim().length()>0){
//               buf.append(" and "+mainsql);
//           }
             buf.append(")");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return buf.toString();
    }
    /**
     * 从虚拟组织机构中查询人员的过滤条件，包括权限过滤 from XXXA01 ....
     * state=1 在职  =2不在职
     * @param userview
     * @param dbpre 应用库前缀
     * @param vorgcode  虚拟组织编码
     * @return
     */
    public String getQueryFromVorg(UserView userview,String dbpre,String vorgcode)
    {
        StringBuffer buf=new StringBuffer();
        ArrayList fieldlist=new ArrayList();
        String strWhere=null;
        try
        {
            strWhere=userview.getPrivSQLExpression("",dbpre,false,true,fieldlist);
            buf.append(strWhere);
            if(strWhere.indexOf("WHERE")==-1) {
                buf.append(" where "+dbpre+"A01.A0100  in (select a0100 from t_vorg_staff where state=1 and b0110='");
            } else {
                buf.append(" and "+dbpre+"A01.A0100  in (select a0100 from t_vorg_staff where state=1 and b0110='");
            }
            buf.append(vorgcode);
            buf.append("' and lower(dbase)='");
            buf.append(dbpre.toLowerCase());
            buf.append("')");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return buf.toString();
    }

    /**
     *
     * @param pos_id
     * @param ncount
     * @param ctrl_value  K04|K0XXX,K0YYY
     * @return
     */
    private boolean overPosWorkOut(String pos_id,int ncount,String ctrl_value)
    {
        boolean bflag=false;
        try
        {
            if("".equalsIgnoreCase(pos_id)) {
                return false;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return false;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return false;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);//定员人数
            if("".equalsIgnoreCase(a_field)) {
                return false;
            }
            String b_field=tmp.substring(idx+1);//实际人数
            if("".equalsIgnoreCase(b_field)) {
                return false;
            }
            /**指标未构库也按不控制编制处理*/
            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return false;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return false;
            }
            StringBuffer buf = new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            RowSet rset=null;
            float amount=0f;//定员人数
            float realcount=0f;//实际人数

            buf.append("select ");
            buf.append(item_a.getItemid());
            buf.append("  as item_a,");
            buf.append(item_b.getItemid());
            buf.append(" as item_b from ");
            buf.append(bxx);
            buf.append(" where e01a1='"+pos_id+"'");
//          paralist.add(pos_id);
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" e01a1='"+pos_id+"'"+this.getId(set.getChangeflag())+")");
//              paralist.add(pos_id);
            }

            rset=dao.search(buf.toString());
            if(rset.next())
            {
                amount=rset.getFloat("item_a");
                realcount=rset.getFloat("item_b");
            }
            /**编制数小于实有人数+新增人数时，则超编*/
            //(定员人数<实际人数+新增人数时)
            if(amount<realcount+ncount) {
                bflag=true;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return bflag;
    }
    /**
     *
     * @param pos_id
     * @param ncount
     * @param ctrl_value  K04|K0XXX,K0YYY
     * @return
     */
    private boolean overPosWorkOut(String pos_id,int ncount,String ctrl_value,String ps_parttime)
    {
        boolean bflag=false;
        try
        {
            if("".equalsIgnoreCase(pos_id)) {
                return false;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return false;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return false;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);//定员人数
            if("".equalsIgnoreCase(a_field)) {
                return false;
            }
            String b_field=tmp.substring(idx+1);//实际人数
            if("".equalsIgnoreCase(b_field)) {
                return false;
            }
            /**指标未构库也按不控制编制处理*/
            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return false;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return false;
            }
            String item_p_id="";
            if("1".equals(ps_parttime)){
                RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
                if(ps_workparttime_vo!=null){
                     String zw_ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
                     FieldItem item_p=DataDictionary.getFieldItem(zw_ps_workparttime);
                     if(item_p!=null&&!"0".equalsIgnoreCase(item_p.getUseflag())){
                         item_p_id=item_p.getItemid();
                     }
                }
            }
            StringBuffer buf = new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            RowSet rset=null;
            float amount=0f;//定员人数
            float realcount=0f;//实际人数
            float parttime=0f;
            buf.append("select ");
            if(item_p_id.length()>0){
                buf.append(item_p_id+",");
            }
            buf.append(item_a.getItemid());
            buf.append("  as item_a,");
            buf.append(item_b.getItemid());
            buf.append(" as item_b from ");
            buf.append(bxx);
            buf.append(" where e01a1='"+pos_id+"'");
//          paralist.add(pos_id);
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" e01a1='"+pos_id+"'"+this.getId(set.getChangeflag())+")");
//              paralist.add(pos_id);
            }

            rset=dao.search(buf.toString());
            if(rset.next())
            {
                amount=rset.getFloat("item_a");
                realcount=rset.getFloat("item_b");
                if(item_p_id.length()>0){
                    parttime=rset.getFloat(item_p_id);
                }
            }
            if(item_p_id.length()>0){
            /**编制数小于实有人数+新增人数时+兼职数，则超编*/
                if(amount<realcount+ncount+parttime)
                {
                    bflag=true;
                }
            }else{
                /**编制数小于实有人数+新增人数时，则超编*/
                //(定员人数<实际人数+新增人数时)
                if(amount<realcount+ncount) {
                    bflag=true;
                }
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return bflag;
    }

    private boolean overPosWorkOut(String pos_id,int ncount,String ctrl_value,String ps_parttime,String mode)
    {
        boolean bflag=false;
        try
        {
            if("".equalsIgnoreCase(pos_id)) {
                return false;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return false;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return false;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);//定员人数
            if("".equalsIgnoreCase(a_field)) {
                return false;
            }
            String b_field=tmp.substring(idx+1);//实际人数
            if("".equalsIgnoreCase(b_field)) {
                return false;
            }
            /**指标未构库也按不控制编制处理*/
            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return false;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return false;
            }
            String item_p_id="";
            if("1".equals(ps_parttime)){
                RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
                if(ps_workparttime_vo!=null){
                     String zw_ps_workparttime= ps_workparttime_vo.getString("str_value").toUpperCase();
                     FieldItem item_p=DataDictionary.getFieldItem(zw_ps_workparttime);
                     if(item_p!=null&&!"0".equalsIgnoreCase(item_p.getUseflag())){
                         item_p_id=item_p.getItemid();
                     }
                }
            }
            StringBuffer buf = new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            RowSet rset=null;
            float amount=0f;//定员人数
            float realcount=0f;//实际人数
            float parttime=0f;
            buf.append("select ");
            if(item_p_id.length()>0){
                buf.append(item_p_id+",");
            }
            buf.append(item_a.getItemid());
            buf.append("  as item_a,");
            buf.append(item_b.getItemid());
            buf.append(" as item_b from ");
            buf.append(bxx);
            buf.append(" where e01a1='"+pos_id+"'");
//          paralist.add(pos_id);
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" e01a1='"+pos_id+"'"+this.getId(set.getChangeflag())+")");
//              paralist.add(pos_id);
            }

            rset=dao.search(buf.toString());
            if(rset.next())
            {
                amount=rset.getFloat("item_a");
                realcount=rset.getFloat("item_b");
                if(item_p_id.length()>0){
                    parttime=rset.getFloat(item_p_id);
                }
            }
            if(item_p_id.length()>0){
            /**编制数小于实有人数+新增人数时+兼职数，则超编*/
            if(amount<realcount+ncount+parttime){
                bflag=true;
                if("warn".equals(mode)){
                    this.dateLinkage(pos_id, ncount, "+");
                }
            }else{
                this.dateLinkage(pos_id, ncount, "+");
            }
            }else{
                /**编制数小于实有人数+新增人数时，则超编*/
                //(定员人数<实际人数+新增人数时)
                if(amount<realcount+ncount) {
                    bflag=true;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return bflag;
    }

    /**
     * @param org_id
     * @param ncount
     * @param ctrl_value B07|B0XXX,B0YYY
     * @return
     */
    private boolean overUnitWorkOut(String org_id,int ncount,String ctrl_value)
    {
        boolean bflag=false;
        try
        {
            if("".equalsIgnoreCase(org_id)) {
                return false;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return false;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return false;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);
            if("".equalsIgnoreCase(a_field)|| "#".equals(a_field)) {
                return false;
            }
            String b_field=tmp.substring(idx+1);
            if("".equalsIgnoreCase(b_field)|| "#".equals(b_field)) {
                return false;
            }
            /**指标未构库也按不控制编制处理*/
            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return false;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return false;
            }
            StringBuffer buf=new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            RowSet rset=null;
            float amount=0f;
            float realcount=0f;
            buf.append("select ");
            buf.append(item_a.getItemid());
            buf.append(" as item_a,");
            buf.append(item_b.getItemid());
            buf.append(" as item_b from ");
            buf.append(bxx);
            buf.append(" where b0110='"+org_id+"' ");
//          paralist.add(org_id);
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" b0110='"+org_id+"')");
//              paralist.add(org_id);
            }

            rset=dao.search(buf.toString());
            if(rset.next())
            {
                amount=rset.getFloat("item_a");
                realcount=rset.getFloat("item_b");
            }
            /**编制数小于实有人数+新增人数时，则超编*/
            if(amount<realcount+ncount) {
                bflag=true;
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return bflag;
    }
    /**
     * @param ncount 新增人数
     * @param pos_id职位编码
     * @param tablename 表名
     * @return 返回真的为超编
     * @throws GeneralException
     */
    public boolean overWorkOut(String tablename,String pos_id,int ncount)throws GeneralException
    {
        boolean pos_flag=false;

        PosparameXML pos = new PosparameXML(conn);
        String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs");
        dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
        tablename=tablename!=null?tablename:"";
        String dbpre = tablename.replaceAll("A01","").replaceAll("a01","");

        if(dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){
            Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(conn);
            String pos_ctrl=othparam.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");
            /*B07|B0XXX,B0YYY*/
            RecordVo pos_vo_ctrl=ConstantParamter.getRealConstantVo("PS_WORKOUT", conn);
            if("true".equals(pos_ctrl)&&(!(pos_vo_ctrl==null|| "".equals(pos_vo_ctrl.getString("str_value")))))
            {
                /**兼职参数*/
                String flag=othparam.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");//是否启用，true启用
                //兼职岗位占编 1：占编
                String takeup_quota=othparam.getValueS(Sys_Oth_Parameter.PART_TIME,"takeup_quota");
                String ps_parttime="0";
                if("true".equals(flag)&&"1".equals(takeup_quota)){
                    ps_parttime="1";
                }
                pos_flag=overPosWorkOut(pos_id,ncount,pos_vo_ctrl.getString("str_value"),ps_parttime);
            }
        }
        return pos_flag;
    }
    /**
     * 根据库前缀取得对应vo
     * @param dbpre
     * @return
     * @throws GeneralException
     */
    public RecordVo getDbNameVo(String dbpre)throws GeneralException
    {
        RecordVo vo=new RecordVo("dbname");
        StringBuffer sql=new StringBuffer();
        sql.append("select dbid,dbname,pre from dbname where pre='");
        sql.append(dbpre);
        sql.append("'");
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet recset=null;
        try
        {
            recset=dao.search(sql.toString());
            if(recset.next())
            {
                vo.setInt("dbid",recset.getInt("dbid"));
                vo.setString("dbname",recset.getString("dbname"));
                vo.setString("pre",recset.getString("pre"));
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
//         try
//         {
//          if(recset!=null)
//              recset.close();
//         }
//         catch(Exception ee)
//         {
//             ee.printStackTrace();
//         }
        }
        return vo;
    }

    /**
     * 求复杂查询条件,把查询结果放至查询结果信息表
     * 查询结果表命名规则：
     * 用户名+库前缀+Result   人员查询结果
     * 用户+B+Result         单位查询结果
     * 用户名+KResult        职位查询结果
     * @param userview      用户名
     * @param dbpre         应用库前缀
     * @param alUsedFields  字段列表
     * @param cond          条件表达式
     * @param modetype      查人员|职位|部门|单位
     * @return
     */
    public String getComplexCond(UserView userview,String dbpre,ArrayList alUsedFields,String cond,int modetype)
    {
        StringBuffer buf=new StringBuffer();
        StringBuffer inbuf=new StringBuffer();
        StringBuffer sql=new StringBuffer();
        String infor="A";
        String tablename=null;
        String strsql=null;
        try
        {
            inbuf.append("insert into ");
            cond=cond.replaceAll("@","\"");
            ContentDAO dao=new ContentDAO(this.conn);
            switch(modetype)
            {
            case IParserConstant.forPerson:
                infor="A";
                tablename=userview.getUserName()+dbpre+"result";
                inbuf.append(tablename);
                inbuf.append(" (a0100) select a0100 from ");
                sql.append("a0100 in (select a0100 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            case IParserConstant.forPosition:
                infor="K";
                tablename=userview.getUserName()+"kresult";
                inbuf.append(tablename);
                inbuf.append(" (e01a1) select e01a1 from ");
                sql.append(" e01a1 in (select e01a1 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            case IParserConstant.forUnit:
                infor="B";
                tablename=userview.getUserName()+"bresult";
                inbuf.append(tablename);
                inbuf.append(" (b0110) select b0110 from ");
                sql.append(" b0110 in (select b0110 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            }
            /**清空表*/


            Table table = new Table(tablename);
            if(modetype==IParserConstant.forPerson){
                FieldItem a0100item = DataDictionary.getFieldItem("A0100");
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(a0100item);
                table.addField(b0110item);
            }else if(modetype==IParserConstant.forUnit){
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(b0110item);
            }else if(modetype==IParserConstant.forPosition){
                FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
                table.addField(e01a1item);
            }else{
                FieldItem a0100item = DataDictionary.getFieldItem("A0100");
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(a0100item);
                table.addField(b0110item);
            }
            DbWizard dbWizard = new DbWizard(this.conn);
            try{
                if(dbWizard.isExistTable(tablename,false)) {
                    dbWizard.dropTable(tablename);
                }
            }catch(Exception e){
            }
            dbWizard.createTable(table);

            /*

            buf.append("delete from ");
            buf.append(tablename);
            dao.update(buf.toString());
            */
            int infoGroup = modetype; // forPerson 人员
            int varType = 8; // logic
            String whereIN=InfoUtils.getWhereINSql(userview,dbpre);
            whereIN="select a0100 "+whereIN;
            YksjParser yp = new YksjParser( userview ,alUsedFields,
                    YksjParser.forSearch, varType, infoGroup, "Ht", dbpre);
            YearMonthCount ymc=null;
            yp.run_Where(cond, ymc,"","", dao, whereIN,this.conn,infor, null);
            strsql=yp.getSQL();

            String tempTableName = yp.getTempTableName();
            inbuf.append(tempTableName);
            if(!(strsql==null|| "".equals(strsql)))
            {
                inbuf.append(" where ");
                inbuf.append(strsql);
            }
            /**把查询结果导入查询结果表中*/
            dao.update(inbuf.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sql.toString();

    }
    /**
     * 求复杂查询条件,把查询结果放至查询结果信息表
     * 查询结果表命名规则：
     * 用户名+库前缀+Result   人员查询结果
     * 用户+B+Result         单位查询结果
     * 用户名+KResult        职位查询结果
     * @param userview      用户名
     * @param dbpre         应用库前缀
     * @param alUsedFields  字段列表
     * @param cond          条件表达式
     * @param modetype      查人员|职位|部门|单位
     * @return
     */
    public String getComplexCondByRSYD(UserView userview,String dbpre,ArrayList alUsedFields,String cond,int modetype,String templetid)
    {
        StringBuffer buf=new StringBuffer();
        StringBuffer inbuf=new StringBuffer();
        StringBuffer sql=new StringBuffer();
        String infor="A";
        String tablename=null;
        String strsql=null;
        try
        {
            inbuf.append("insert into ");
            cond=cond.replaceAll("@","\"");
            ContentDAO dao=new ContentDAO(this.conn);
            switch(modetype)
            {
            case IParserConstant.forPerson:
                infor="A";
                tablename=userview.getUserName()+dbpre+"result";
                inbuf.append(tablename);
                inbuf.append(" (a0100) select a0100 from ");
                sql.append("a0100 in (select a0100 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            case IParserConstant.forUnit:
                infor="B";
                tablename=userview.getUserName()+"bresult";
                inbuf.append(tablename);
                inbuf.append(" (b0110) select b0110 from ");
                sql.append(" b0110 in (select b0110 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            case IParserConstant.forPosition:
                infor="K";
                tablename=userview.getUserName()+"kresult";
                inbuf.append(tablename);
                inbuf.append(" (e01a1) select e01a1 from ");
                sql.append(" e01a1 in (select e01a1 from ");
                sql.append(tablename);
                sql.append(")");
                break;
            }


            Table table = new Table(tablename);
            if(modetype==IParserConstant.forPerson){
                FieldItem a0100item = DataDictionary.getFieldItem("A0100");
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(a0100item);
                table.addField(b0110item);
            }else if(modetype==IParserConstant.forUnit){
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(b0110item);
            }else if(modetype==IParserConstant.forPosition){
                FieldItem e01a1item = DataDictionary.getFieldItem("E01A1");
                table.addField(e01a1item);
            }else{
                FieldItem a0100item = DataDictionary.getFieldItem("A0100");
                FieldItem b0110item = DataDictionary.getFieldItem("B0110");
                table.addField(a0100item);
                table.addField(b0110item);
            }
            DbWizard dbWizard = new DbWizard(this.conn);
            try{
                if(dbWizard.isExistTable(tablename,false)) {
                    dbWizard.dropTable(tablename);
                }
            }catch(Exception e){
            }
            dbWizard.createTable(table);

            /**清空表*/
            /*
            buf.append("delete from ");
            buf.append(tablename);
            dao.update(buf.toString());
            */
            TemplateTableBo tablebo=new TemplateTableBo(this.conn,Integer.parseInt(templetid),userview);
            String no_priv_ctrl=tablebo.getNo_priv_ctrl(); //手工选人、条件选人不按管理范围过滤, 0按管理范围过滤(默认值),1不按
            int infoGroup = modetype; // forPerson 人员
            int varType = 8; // logic
            String whereIN=InfoUtils.getWhereINSql(userview,dbpre);
            whereIN="select a0100 "+whereIN;
            if("1".equals(no_priv_ctrl)) {
                whereIN="";
            }
            YksjParser yp = new YksjParser( userview ,alUsedFields,
                    YksjParser.forSearch, varType, infoGroup, "Ht", dbpre);
            YearMonthCount ymc=null;
                yp.setSupportVar(true,"select  *  from   midvariable where nflag=0 and templetid= "+templetid);  //支持临时变量
            yp.run_Where(cond, ymc,"","", dao, whereIN,this.conn,infor, null);
            strsql=yp.getSQL();

            String tempTableName = yp.getTempTableName();
            inbuf.append(tempTableName);
            if(!(strsql==null|| "".equals(strsql)))
            {
                inbuf.append(" where ");
                inbuf.append(strsql);
            }
            /**把查询结果导入查询结果表中*/
            dao.update(inbuf.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return sql.toString();

    }
    /**
     * 根据传过来的应用库缀字符串列表
     * @param dbprelist 前缀列表
     * @return 返回对应库前缀对象列表
     * @throws GeneralException
     */
    public ArrayList getDbNameVoList(ArrayList dbprelist)throws GeneralException
    {
        if(dbprelist.size()==0)
        {
            throw new GeneralException("",ResourceFactory.getProperty("muster.label.dbname.size"),"","");
        }
        StringBuffer dbpre=new StringBuffer();
        for(int i=0;i<dbprelist.size();i++)
        {
            dbpre.append("'");
            dbpre.append((String)dbprelist.get(i));
            dbpre.append("',");
        }
        if(dbpre.length()>0) {
            dbpre.setLength(dbpre.length()-1);
        }


        StringBuffer sql=new StringBuffer();
        sql.append("select dbid,dbname,pre from dbname where pre in (");
        sql.append(dbpre);
        sql.append(")");
        sql.append(" order by dbid");
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet recset=null;
        ArrayList list=new ArrayList();
        try
        {
            recset=dao.search(sql.toString());
            while(recset.next())
            {
                RecordVo vo=new RecordVo("dbname");
                vo.setInt("dbid",recset.getInt("dbid"));
                vo.setString("dbname",recset.getString("dbname"));
                vo.setString("pre",recset.getString("pre"));
                list.add(vo);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
//         try
//         {
//          if(recset!=null)
//              recset.close();
//         }
//         catch(Exception ee)
//         {
//             ee.printStackTrace();
//         }
        }
        return list;
    }
    /**
     * 求登录用户名字段
     * @return
     * @throws GeneralException
     */
    public String getLogonUserNameField()
    {
    	String defaultUserNameFld = "username";

		RecordVo param_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if(param_vo==null|| "".equals(param_vo.getString("str_value"))) {
            return defaultUserNameFld;
        }

		String user_pwd=param_vo.getString("str_value");
		int idx=user_pwd.indexOf(",");
		if(idx==-1) {
            return defaultUserNameFld;
        }

		String username=user_pwd.substring(0,idx).trim();
		if ("#".equals(username) || "".equals(username)) {
            return defaultUserNameFld;
        }

		//zxj 20150921 进一步判断用户名指标是否存在
		FieldItem usernameItem = DataDictionary.getFieldItem(username, "A01");
		if (null == usernameItem || !"1".equals(usernameItem.getUseflag())) {
            return defaultUserNameFld;
        }

		return username;
    }
    /**
     * 登记口令字段
     * @return
     * @throws GeneralException
     */
    public String getLogonPassWordField()
    {
        String defaultUserPwdFld = "userpassword";

		RecordVo param_vo=ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
		if(param_vo==null|| "".equals(param_vo.getString("str_value"))) {
            return defaultUserPwdFld;
        }

		String user_pwd=param_vo.getString("str_value");
		int idx=user_pwd.indexOf(",");
		if(idx==-1) {
            return defaultUserPwdFld;
        }

		String pwd=user_pwd.substring(idx+1).trim();
		if ("#".equals(pwd)|| "".equals(pwd)) {
            return defaultUserPwdFld;
        }

		//zxj 20150921 进一步判断密码指标是否存在
		FieldItem userpwdItem = DataDictionary.getFieldItem(pwd, "A01");
		if (null == userpwdItem || !"1".equals(userpwdItem.getUseflag())) {
            return defaultUserPwdFld;
        }

		return pwd;
    }

    /**
     * 帐号锁定字段
     * @return
     * @throws GeneralException
     */
    public String getLogonLockField()
    {
        return getConstantParameterValue("SS_LOGIN_LOCK_FIELD", "A01", "");
    }

    /**
     * 移库时修改薪资历史记录表中的记录
     * @param A0100
     * @param a0100str
     * @param dbname
     * @param pre
     */
    public void updateSalaryHistory(String A0100,String a0100str,String dbname,String pre){
        StringBuffer buf = new StringBuffer();
        ArrayList listvalue = new ArrayList();
        ArrayList list = new ArrayList();
        listvalue.add(a0100str);
        listvalue.add(pre);
        listvalue.add(A0100);
        listvalue.add(dbname);
        list.add(listvalue);
        buf.append("update SalaryHistory set A0100=?,NBASE=?");
        buf.append(" where A0100=? and NBASE=?");
        ContentDAO dao=new ContentDAO(this.conn);
        try {
            dao.batchUpdate(buf.toString(),list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 移库时修改薪资历史,即以考勤记录表中的记录
     * @param listvalue
     */
    public void updateSalaryPre(ArrayList listvalue)throws GeneralException{
        for(int i=0;i<listvalue.size();i++)
        {
            ArrayList list=(ArrayList)listvalue.get(i);
            String src_a0100=(String)list.get(2);
            String src_nbase=(String)list.get(3);
            String desc_a0100=(String)list.get(0);
            String desc_nbase=(String)list.get(1);
            changeBusiTableData(src_a0100,src_nbase,desc_a0100,desc_nbase);
        }
        /*ArrayList tablelist = salaryTable();
        for(int i=0;i<tablelist.size();i++){
            if(tablelist.get(i)!=null&&tablelist.get(i).toString().length()>1){
                StringBuffer buf = new StringBuffer();
                if(tablelist.get(i).toString().equalsIgnoreCase("t_vorg_staff")){
                    buf.append("update "+ (String)tablelist.get(i)+" set A0100=?,dbase=?");
                    buf.append(" where A0100=? and dbase=?");
                }else{
                    buf.append("update "+ (String)tablelist.get(i)+" set A0100=?,NBASE=?");
                    buf.append(" where A0100=? and NBASE=?");
                }
                ContentDAO dao=new ContentDAO(this.conn);
                try {
                    dao.batchUpdate(buf.toString(),listvalue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }*/

    }

    public ArrayList salaryTable(){
        ArrayList tablelist = new ArrayList();
        DbWizard dbwizard = new DbWizard(this.conn);
        if(dbwizard.isExistTable("SalaryHistory", false)) {
            tablelist.add("SalaryHistory");
        }
        if(dbwizard.isExistTable("gz_tax_mx", false)) {
            tablelist.add("gz_tax_mx");
        }
        if(dbwizard.isExistTable("t_vorg_staff", false)) {
            tablelist.add("t_vorg_staff");
        }

        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
            rs = dao.search("select salaryid,username from gz_extend_log group by salaryid, username");
            while(rs.next()){
                String salaryid = rs.getString("salaryid");
                salaryid=salaryid!=null?salaryid:"";
                String username = rs.getString("username");
                username=username!=null?username:"";
                String tablename = username+"_salary_"+salaryid;
                if(dbwizard.isExistTable(tablename, false)) {
                    tablelist.add(tablename);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tablelist;
    }

    /**
     * 取得电子信箱字段名
     * @return
     */
    public String getEmailField()
    {
        return getConstantParameterValue("SS_EMAIL", "A01", "");
    }

    /**
     * 取得存在移动电话对应的字段
     * @return
     */
    public String getMobilePhoneField()
    {
        return getConstantParameterValue("SS_MOBILE_PHONE", "A01", "");
    }

    /**
     * 取constant中的保存的参数值（系统参数指定的一些指标）
     * @Title: getConstantParameterValue
     * @Description: 比如邮箱、移动电话等指标
     * @param constant 参数名
     * @param fieldSetId 参数所在的子集
     * @param defaultValue 默认值
     * @return 指标（不存在则返回空字符串）
     */
    private String getConstantParameterValue(String constant, String fieldSetId, String defaultValue) {
        RecordVo param_vo=ConstantParamter.getConstantVo(constant);
        if(param_vo==null|| "".equals(param_vo.getString("str_value"))) {
            return defaultValue;
        }

        String strValue = param_vo.getString("str_value");
        strValue = strValue == null ? "" : strValue.trim();
        if(strValue == null || "".equals(strValue) || "#".equals(strValue)) {
            return defaultValue;
        }

        //进一步判断指标是否存在
        FieldItem item = DataDictionary.getFieldItem(strValue, fieldSetId);
        if (null == item || !"1".equals(item.getUseflag())) {
            return defaultValue;
        }

        return strValue;
    }

    /**
     * 取得全部登记用户库前缀列表，
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllLoginDbNameList()throws GeneralException
    {
        StringBuffer sql=new StringBuffer();
        RecordVo param_vo=ConstantParamter.getConstantVo("SS_LOGIN");
        if(param_vo==null|| "".equals(param_vo.getString("str_value"))) {
            return null;
        }
        String str_dbpre=param_vo.getString("str_value").toUpperCase();
		//sunming2015.06.15 调整人员库显示顺序
		sql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet recset=null;
        ArrayList list=new ArrayList();
        try
        {
            recset=dao.search(sql.toString());
            while(recset.next())
            {
                String pre=recset.getString("pre").toUpperCase();
                if(str_dbpre.indexOf(pre)==-1) {
                    continue;
                }
                RecordVo vo=new RecordVo("dbname");
                vo.setInt("dbid",recset.getInt("dbid"));
                vo.setString("dbname",recset.getString("dbname"));
                vo.setString("pre",recset.getString("pre"));
                list.add(vo);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
//         try
//         {
//          if(recset!=null)
//              recset.close();
//         }
//         catch(Exception ee)
//         {
//             ee.printStackTrace();
//         }
        }
        return list;

    }
    /**
     * 取得全库的人员库列表
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllDbNameVoList()throws GeneralException
    {

        StringBuffer sql=new StringBuffer();
        sql.append("select dbid,dbname,pre from dbname");
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet recset=null;
        ArrayList list=new ArrayList();
        try
        {
            recset=dao.search(sql.toString());
            while(recset.next())
            {
                RecordVo vo=new RecordVo("dbname");
                vo.setInt("dbid",recset.getInt("dbid"));
                vo.setString("dbname",recset.getString("dbname"));
                vo.setString("pre",recset.getString("pre"));
                list.add(vo);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
//         try
//         {
//          if(recset!=null)
//              recset.close();
//         }
//         catch(Exception ee)
//         {
//             ee.printStackTrace();
//         }
        }
        return list;
    }
    /**
     * 取得全库的人员库列表
     * @return
     * @throws GeneralException
     */
    public RecordVo getRecordVoA01(String tablename,String A0100){
        RecordVo vo=new RecordVo(tablename);
        vo.setString("a0100",A0100);
        ContentDAO dao=new ContentDAO(this.conn);
        try {
            vo = dao.findByPrimaryKey(vo);
            ArrayList fieldlist = DataDictionary.getFieldList(tablename.substring(3),Constant.USED_FIELD_SET);
            for(int i=0;i<fieldlist.size();i++){
                FieldItem fielditem = (FieldItem)fieldlist.get(i);
                if("N".equals(fielditem.getItemtype()))
                {
                    if(vo.getString(fielditem.getItemid())==null||vo.getString(fielditem.getItemid()).length()<1){
                        //vo.setString(fielditem.getItemid(),"");
                    }
                }else{
                    if(vo.getString(fielditem.getItemid())==null||vo.getString(fielditem.getItemid()).length()<1){
                        if ("D".equals(fielditem.getItemtype())){//田野添加判断日期类型不能设置为空字符串，更新入库后变为一个默认值了
                            //vo.setString(fielditem.getItemid(),null);
                        }else{
                            vo.setString(fielditem.getItemid(),"");
                        }
                    }
                }

            }
        } catch (GeneralException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch(RuntimeException e){
            e.printStackTrace();
            if(e.getMessage().indexOf("the attribute of Model is not exist!")!=-1) {
                new RuntimeException(e.getMessage()+"   ----   "+e.getMessage().replaceAll("the attribute of Model is not exist!", "请检查表<"+tablename+">中是否存在此指标!")).printStackTrace();
            }
        }
        return vo;
    }
    /**
     * 取得全库的人员库列表
     * @return
     * @throws GeneralException
     */
    public RecordVo getRecordVoA01(String tablename,String A0100,int I9999){
        RecordVo vo=new RecordVo(tablename);
        ContentDAO dao=new ContentDAO(this.conn);
        I9999=I9999>0?I9999:1;
        if(checkValue(dao,tablename,A0100,I9999)){
            vo.setString("a0100",A0100);
            vo.setInt("i9999",I9999);

            try {
                vo = dao.findByPrimaryKey(vo);
                ArrayList fieldlist = DataDictionary.getFieldList(tablename.substring(3),Constant.USED_FIELD_SET);
                for(int i=0;i<fieldlist.size();i++){
                    FieldItem fielditem = (FieldItem)fieldlist.get(i);
                    if(vo.getString(fielditem.getItemid())==null||vo.getString(fielditem.getItemid()).length()<1){
                        if ("D".equals(fielditem.getItemtype())){//田野添加判断日期类型不能设置为空字符串，更新入库后变为一个默认值了
                            //vo.setString(fielditem.getItemid(),null);
                        }else{
                            vo.setString(fielditem.getItemid(),"");
                        }
                    }
                }
            } catch (GeneralException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            vo=null;
        }
        return vo;
    }
    private boolean checkValue(ContentDAO dao,String tablename,String A0100,int I9999){
        boolean checkflag = false;
        try {
            RowSet rs = dao.search("select A0100 from "+tablename+" where A0100='"+A0100+"' and I9999='"+I9999+"'");
            if(rs.next()){
                checkflag=true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return checkflag;
    }

    /**
     * 取得全库的人员库列表授权范围的人员库
     * @return
     * @throws GeneralException
     */
    public ArrayList getAllDbNameVoList(UserView userview)throws GeneralException
    {

        StringBuffer sql=new StringBuffer();
        sql.append("select dbid,dbname,pre from dbname order by dbid");
        ContentDAO dao=new ContentDAO(this.conn);
        RowSet recset=null;
        ArrayList list=new ArrayList();
        try
        {
            recset=dao.search(sql.toString());
            while(recset.next())
            {
                if(!userview.hasTheDbName(recset.getString("pre"))) {
                    continue;
                }
                RecordVo vo=new RecordVo("dbname");
                vo.setInt("dbid",recset.getInt("dbid"));
                vo.setString("dbname",recset.getString("dbname"));
                vo.setString("pre",recset.getString("pre"));
                list.add(vo);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
//         try
//         {
//          if(recset!=null)
//              recset.close();
//         }
//         catch(Exception ee)
//         {
//             ee.printStackTrace();
//         }
        }
        return list;
    }
    /**
     * 联动组织和职位数据
     * @param org_id 机构编码
     * @param pos_id 职位编码
     * @param ncount 数量
     * @param flag   增加还是减少
     * = '+'
     * = '-'
     */
    public void dateLinkage(String org_id,String pos_id,int ncount,String flag){

        Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(conn);

        String org_ctrl=othparam.getValueS(Sys_Oth_Parameter.WORKOUT, "unit");

        String pos_ctrl=othparam.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");

        //ConstantParamter param=new ConstantParamter();
        /*K04|K0XXX,K0YYY*/
        RecordVo org_vo_ctrl=ConstantParamter.getRealConstantVo("UNIT_WORKOUT", conn);
        if("true".equals(org_ctrl)&&(!(org_vo_ctrl==null|| "".equals(org_vo_ctrl.getString("str_value")))))
        {
//          org_flag=overUnitWorkOut(org_id,ncount,org_vo_ctrl.getString("str_value"));
            datelinkunit(org_id,ncount,org_vo_ctrl.getString("str_value"),flag);
        }

        /*B07|B0XXX,B0YYY*/
        RecordVo pos_vo_ctrl=ConstantParamter.getRealConstantVo("PS_WORKOUT", conn);
        if("true".equals(pos_ctrl)&&(!(pos_vo_ctrl==null|| "".equals(pos_vo_ctrl.getString("str_value")))))
        {
            datelinkpos(pos_id,ncount,pos_vo_ctrl.getString("str_value"),flag);
        }


    }

    /**
     * 联动兼职数据
     * @param pos_id 职位编码
     * @param ncount 数量
     * @param flag   增加还是减少
     * = '+'
     * = '-'
     */
    public void dateLinkage(String pos_id,int ncount,String flag){

        Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(conn);
        String pos_ctrl=othparam.getValueS(Sys_Oth_Parameter.WORKOUT, "pos");

        /*K04|K0XXX,K0YYY*/
        RecordVo pos_vo_ctrl=ConstantParamter.getRealConstantVo("PS_WORKOUT", conn);
        RecordVo ps_workparttime_vo=ConstantParamter.getRealConstantVo("PS_WORKPARTTIME",conn);
        if("true".equals(pos_ctrl)&&ps_workparttime_vo!=null&&ps_workparttime_vo.getString("str_value").length()>0&&(!(pos_vo_ctrl==null|| "".equals(pos_vo_ctrl.getString("str_value")))))
        {
            datelinkpos(pos_id,ncount,pos_vo_ctrl.getString("str_value"),flag,ps_workparttime_vo.getString("str_value"));
        }


    }

    /**
     * 联动单位数据
     * @param org_id
     * @param ncount
     * @param ctrl_value
     * @param flag
     */
    public void datelinkunit(String org_id,int ncount,String ctrl_value,String flag){
        try
        {
            if("".equalsIgnoreCase(org_id)) {
                return ;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return ;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return ;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);
            if("".equalsIgnoreCase(a_field)|| "#".equals(a_field)) {
                return ;
            }
            String b_field=tmp.substring(idx+1);
            if("".equalsIgnoreCase(b_field)|| "#".equals(b_field)) {
                return ;
            }
            /**指标未构库也按不控制编制处理*/
            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return ;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return ;
            }
            StringBuffer buf=new StringBuffer();
            ContentDAO dao=new ContentDAO(conn);
            buf.append("update "+bxx);
            buf.append(" set ");
            buf.append(item_b.getItemid());
            buf.append("=");
            buf.append(item_b.getItemid()+flag+ncount);
            buf.append(" where b0110='"+org_id+"' ");
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" b0110='"+org_id+"'"+this.getId(set.getChangeflag())+")");
            }

            dao.update(buf.toString());

            /**编制数小于实有人数+新增人数时，则超编*/

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 根据部门编码直接查询上级单位编码
     * @param deptid
     * @return
     */
    public static String findUnitCodeByDeptId(String deptid)
    {
        CodeItem item=AdminCode.getCode("UN", deptid);
        String orgid="";
        if(item==null)
        {
            item=AdminCode.getCode("UM", deptid);
            orgid=findUnitCodeByDeptId(item.getPcodeitem());
            if(orgid.equalsIgnoreCase(item.getPcodeitem())) {
                return orgid;
            }
        }
        else
        {
            orgid=item.getPcodeitem();
        }
        return orgid;
    }

    /**
     * 联动职位数据
     * @param pos_id
     * @param ncount
     * @param ctrl_value
     * @param flag
     */
    public void datelinkpos(String pos_id,int ncount,String ctrl_value,String flag){
        try
        {
            if("".equalsIgnoreCase(pos_id)) {
                return ;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return ;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return ;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);
            if("".equalsIgnoreCase(a_field)) {
                return ;
            }
            String b_field=tmp.substring(idx+1);
            if("".equalsIgnoreCase(b_field)) {
                return ;
            }
            /**指标未构库也按不控制编制处理*/

            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if(item_a ==null || "0".equalsIgnoreCase(item_a.getUseflag())) {
                return ;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if(item_b ==null || "0".equalsIgnoreCase(item_b.getUseflag())) {
                return ;
            }
            StringBuffer buf=new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            buf.append("update ");
            buf.append(bxx);
            buf.append("  set ");
            buf.append(item_b.getItemid());
            buf.append("= "+item_b.getItemid()+flag+ncount);
            buf.append(" where e01a1='"+pos_id+"'");
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" e01a1='"+pos_id+"'"+this.getId(set.getChangeflag())+")");
//              paralist.add(pos_id);
            }
            //System.out.println(buf.toString());
            int issd=dao.update(buf.toString());

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    /**
     * 联动兼职数据
     * @param pos_id
     * @param ncount
     * @param ctrl_value
     * @param flag
     */
    public void datelinkpos(String pos_id,int ncount,String ctrl_value,String flag,String parttime_field){
        try
        {
            if("".equalsIgnoreCase(pos_id)) {
                return ;
            }
            int idx=ctrl_value.indexOf("|");
            /**单位编制子集*/
            String bxx=ctrl_value.substring(0,idx);
            /**如果子集未构库，则按不控制编制处理*/
            FieldSet set=DataDictionary.getFieldSetVo(bxx);
            if("0".equalsIgnoreCase(set.getUseflag())) {
                return ;
            }
            String tmp=ctrl_value.substring(idx+1);
            /**如果未定义编制控制指标，也按不控制编制处理*/
            if(tmp==null|| "".equalsIgnoreCase(tmp)) {
                return ;
            }
            idx=tmp.indexOf(",");
            String a_field=tmp.substring(0, idx);
            if("".equalsIgnoreCase(a_field)) {
                return ;
            }
            String b_field=tmp.substring(idx+1);
            if("".equalsIgnoreCase(b_field)) {
                return ;
            }
            /**指标未构库也按不控制编制处理*/

            FieldItem item_a=DataDictionary.getFieldItem(a_field);
            if("0".equalsIgnoreCase(item_a.getUseflag())) {
                return ;
            }
            FieldItem item_b=DataDictionary.getFieldItem(b_field);
            if("0".equalsIgnoreCase(item_b.getUseflag())) {
                return ;
            }
            FieldItem item_p=DataDictionary.getFieldItem(parttime_field);
            if("0".equalsIgnoreCase(item_p.getUseflag())) {
                return ;
            }
            StringBuffer buf=new StringBuffer();
//          ArrayList paralist=new ArrayList();
            ContentDAO dao=new ContentDAO(conn);
            buf.append("update ");
            buf.append(bxx);
            buf.append("  set ");
            buf.append(parttime_field);
            buf.append("= "+parttime_field+flag+ncount);
            buf.append(" where e01a1='"+pos_id+"'");
            if(!set.isMainset())
            {
                buf.append(" and I9999=(select Max(I9999) from ");
                buf.append(bxx);
                buf.append(" where ");
                buf.append(" e01a1='"+pos_id+"'"+this.getId(set.getChangeflag())+")");
//              paralist.add(pos_id);
            }
            //System.out.println(buf.toString());
            int issd=dao.update(buf.toString());

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * 查询表内某字段是否是唯一值
     * @param tablename
     * @param columnname
     * @return true 表示有重复值
     */
    public boolean JudgeUniqueness(String tablename,String columnname,String values){
        boolean b = false;
        String sql = "select "+columnname+" from "+tablename+" where "
        +columnname+" is not NULL and "+columnname+"='"+values+"'";
        ContentDAO dao = new ContentDAO(conn);
        try {
            RowSet rs = dao.search(sql);
            if(rs.next()){
                b = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return b;
    }

    public String checkOnlyName(String dbs,String fieldsetid,String columnname,
            String values,String A0100,String dbpre){
        String b = "true";
        dbs=dbs!=null?dbs:"";
        String dbArr[] = dbs.split(",");
        if(fieldsetid!=null&&fieldsetid.trim().length()>0&&dbArr!=null&&dbArr.length>0){
            for(int i=0;i<dbArr.length;i++){
                if(dbArr[i]!=null&&dbArr[i].trim().length()>1){
                    if(dbArr[i].equalsIgnoreCase(dbpre)) {
                        continue;
                    }
                    b = checkOnlyName(dbArr[i]+fieldsetid,columnname,values,A0100,false);
                    if(!"true".equalsIgnoreCase(b)){
                        break;
                    }
                }
            }
        }
        return b;
    }
    
    /**
     * 校验不同人员库指标是否有重复值（区分大小写）
     * @param dbs
     * @param fieldsetid
     * @param columnname
     * @param values
     * @param A0100
     * @return
     */
    public String checkOnlyName(String dbs,String fieldsetid,String columnname,String values,String A0100){
    	boolean ignoreCase = false;
        return uniquenessCheck(dbs, fieldsetid, columnname, values, A0100, ignoreCase);
    }
    
	/**
	 * 校验不同人员库中是否有重复指标
	 * @param dbs
	 * @param fieldsetid
	 * @param columnname
	 * @param values
	 * @param A0100
	 * @param ignoreCase 是否忽略大小写
	 * @return
	 */
	public String uniquenessCheck(String dbs, String fieldsetid, String columnname, String values, String A0100,
			boolean ignoreCase) {
		String b = "true";
        dbs=dbs!=null?dbs:"";
        String dbArr[] = dbs.split(",");
        if(fieldsetid!=null&&fieldsetid.trim().length()>0&&dbArr!=null&&dbArr.length>0){
            for(int i=0;i<dbArr.length;i++){
                if(dbArr[i]!=null&&dbArr[i].trim().length()>1){
                    String newA0100 = A0100;
                    //校验黑名单时，当前操作的人员库与黑名单人员库不同，就不需要过滤掉与当前的人员编号相同的人员
                    if(StringUtils.isNotEmpty(this.nbase) && !this.nbase.equalsIgnoreCase(dbArr[i].trim())) {
                        newA0100 = "";
                    }

                    b = checkOnlyName(dbArr[i]+fieldsetid,columnname,values,newA0100,ignoreCase);
                    if(!"true".equalsIgnoreCase(b)){
                        break;
                    }
                }
            }
        }
        //黑名单人员库校验完成后需要将当前操作的人员库清空
        this.setNbase("");

        return b;
	}
    
    /**
     * 查询表内某字段是否是唯一值
     * @param tablename 表名
     * @param columnname 指标id
     * @param values 值
     * @param ignoreCase 忽略大小写
     * @return true 表示有重复值
     */
    private String checkOnlyName(String tablename,String columnname,String values,String A0100,boolean ignoreCase){

    	String b = "true";
    	if(StringUtils.isBlank(values)) {
            return b;
        }
    	ArrayList<String> param = new ArrayList<String>();
        StringBuffer sql = new StringBuffer();
        sql.append("select A0101,B0110,E0122,E01A1 from ");
        sql.append(tablename);
        sql.append(" where ");
        sql.append(columnname);
        sql.append(" is not NULL and ");
        if(!ignoreCase) {
        	sql.append(columnname);
        	sql.append("=?");
        	param.add(values);
        }else {
        	sql.append("upper("+columnname+")");
        	sql.append("=?");
        	param.add(values.toUpperCase());
        }
        if(A0100!=null&&A0100.trim().length()>0) {
        	sql.append(" and A0100<>?");
        	param.add(A0100);
        }
        sql.append(" order by A0000");
        ContentDAO dao = new ContentDAO(conn);
        RowSet rs =null;
        try {
            rs = dao.search(sql.toString(), param);
            if(rs.next()){
			    //zxj 20160314 取人员所在人员库（只在从人员主集子集中查询时）
			    String dbName = "";
			    if (tablename.length()==6 && "A".equalsIgnoreCase(tablename.substring(3, 4))) {
			        String nbase = tablename.substring(0, 3);
			        RecordVo dbVo = getDbNameVo(nbase);
			        dbName = dbVo.getString("dbname");
			        b = dbName == null ? "" : dbName;
			    }

                String A0101 = rs.getString("A0101");
                String B0110 = rs.getString("B0110");
                String E0122 = rs.getString("E0122");
                String E01A1 = rs.getString("E01A1");
                CodeItem item = AdminCode.getCode("UN",B0110);
                if(item!=null&&item.getCodename()!=null&&item.getCodename().trim().length()>0){
					if("true".equals(b)) {
                        b=item.getCodename();
                    } else {
                        b+="/"+item.getCodename();
                    }
                }
                item = AdminCode.getCode("UM",E0122);
                if(item!=null&&item.getCodename()!=null&&item.getCodename().trim().length()>0){
                    if("true".equals(b)) {
                        b=item.getCodename();
                    } else {
                        b+="/"+item.getCodename();
                    }
                }
                item = AdminCode.getCode("@K",E01A1);
                if(item!=null&&item.getCodename()!=null&&item.getCodename().trim().length()>0){
                    if("true".equals(b)) {
                        b=item.getCodename();
                    } else {
                        b+="/"+item.getCodename();
                    }
                }
                FieldItem item_b=DataDictionary.getFieldItem(columnname);
                String desc="";
                if(item_b!=null) {
                    desc = item_b.getItemdesc();
                }
                if("true".equals(b)){
                    b=A0101;
                }else{
                    b+="/"+A0101;
                }
                b = desc+values+"已经被["+b+"]占用!";
            }
		} catch (Exception e) {
            e.printStackTrace();
        }
	    finally{
        	PubFunc.closeDbObj(rs);
        }
        return b;
    }


    /**
     * 验证 身份 证号是否符合 规则
     * @param id
     * @return
     */
    public String checkID(String id)
    {
        String info="";
        int len = id.length();
        if (len == 15||len==18) {
             String birth_id ="";
             if(len == 15) {
                 birth_id=id.substring(6, 12);// 6位日期
             } else {
                 birth_id = id.substring(6, 14);// 8位日期
             }
            // 性别为男，最后一位是奇数
            String temp = id.substring(14);// 最后一位代表性别
            if(len==18) {
                temp = id.substring(16, 17);
            }

            String patternStr="[0-9]+";
            boolean result = Pattern.matches(patternStr, birth_id);
            if(!result)
            {
                info = "身份证号输入错误 信息！";
                return info;
            }

            if(birth_id.length()==8)
            {
                int year=Integer.parseInt(birth_id.substring(0,4));
                if(year<1900||year>2050)
                {
                    info = "身份证号输入错误 信息！";
                    return info;
                }
            }
            else {
                birth_id="19"+birth_id;
            }
            int month=Integer.parseInt(birth_id.substring(4, 6));
            int day=Integer.parseInt(birth_id.substring(6));
            if(month>12||month<=0)
            {
                info = "身份证号输入错误 信息！";
                return info;
            }
            if(day>31||day<=0)
            {
                info = "身份证号输入错误 信息！";
                return info;
            }
            result = Pattern.matches(patternStr, temp);
            if(!result)
            {
                info = "身份证号输入错误 信息！";
                return info;
            }
            if (info.length()<1){//严格校验 20160805 wangrd bug21301
                String tmp = checkIdNumber(id,birth_id,temp);
                if (!tmp.contains("true")){
                    info = "身份证号输入错误 信息！";
                }
            }

        }
        else {
            info = "身份证号长度错误，只能是15位或18位！";
        }
        return info;
    }



    /**
     * 校验身份证号
     * @param id    身份证号，包括15位和18位
     * @param birthday  出生日期8位(20071207)
     * @param sex   性别,男为奇数  1，女为偶数  2
     * @return
     */
    public String checkIdNumber(String id, String birthday, String sex) {

        char[] ai = { '1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2' };

        String result = "";

        int len = id.length();

        if (len == 15) {

            result = checkId_15(id, birthday, sex);

        } else if (len == 18) {

            result = checkId_18(id, birthday, sex);

            if(result.indexOf("true")!=-1){
                int mod = checkBit(id);
                if(!id.substring(17).toUpperCase().equalsIgnoreCase(ai[mod]+"")) {
                    result = "false:身份证的校验位错误！";
                }
            }

        } else {

            result = "false:身份证号长度错误，只能是15位或18位！";

        }

        return result;
    }

    public String checkId_15(String id, String birthday, String sex) {

        String result = "";

        String birth_id = id.substring(6, 12);// 6位日期
        if(birthday!=null&&birthday.trim().length()>6){
            String birth = birthday.substring(2);

            if (birth_id.equals(birth)) {

                // 检验日期，出生日期与身份证中的出生日期相符，然后检验性别

                if ("1".equals(sex)) {

                    // 性别为男，最后一位是奇数

                    String temp = id.substring(14);// 最后一位代表性别

                    int isex = Integer.parseInt(temp);

                    if (isex % 2 == 1) {// 除2余数为1

                        result = "true:身份证号通过验证";

                    } else {

                        result = "false:身份证号与性别不相符！";

                    }

                } else if ("2".equals(sex)) {

                    // 性别为女，最后一位是偶数

                    String temp = id.substring(14);// 最后一位代表性别

                    int isex = Integer.parseInt(temp);

                    if (isex % 2 == 0) {// 除2余数为0
                        result = "true:身份证号通过验证";

                    } else {

                        result = "false:身份证号与性别不相符！";

                    }

                } else {

                    result = "true:身份证号通过验证";

                }

            } else {

                // 出生日期与身份证中的出生日期不相符

                result = "false:身份证号码与出生日期不相符！";

            }
        }else{
            if ("1".equals(sex)) {

                // 性别为男，最后一位是奇数

                String temp = id.substring(14);// 最后一位代表性别

                int isex = Integer.parseInt(temp);

                if (isex % 2 == 1) {// 除2余数为1

                    result = "true:身份证号通过验证";

                } else {

                    result = "false:身份证号与性别不相符！";

                }

            } else if ("2".equals(sex)) {

                // 性别为女，最后一位是偶数

                String temp = id.substring(14);// 最后一位代表性别

                int isex = Integer.parseInt(temp);

                if (isex % 2 == 0) {// 除2余数为0
                    result = "true:身份证号通过验证";

                } else {

                    result = "false:身份证号与性别不相符！";

                }

            } else {

                result = "true:身份证号通过验证";

            }
        }

        return result;
    }

    public String checkId_18(String id, String birthday, String sex) {

        String result = "";

        String birth_id = id.substring(6, 14);// 8位日期

        if(birthday!=null&&birthday.trim().length()>6){

            if (birth_id.equals(birthday)) {

                // 检验日期，出生日期与身份证中的出生日期相符，然后检验性别

                if ("1".equals(sex)) {

                    // 性别为男，最后一位是奇数

                    String temp = id.substring(16, 17);// 倒数第二位代表性别

                    int isex = Integer.parseInt(temp);

                    if (isex % 2 == 1) {// 除2余数为1

                        result = "true:身份证号通过验证";

                    } else {

                        result = "false:身份证号与性别不相符！";

                    }

                } else if ("2".equals(sex)) {

                    // 性别为女，最后一位是偶数

                    String temp = id.substring(16, 17);// 倒数第二位代表性别

                    int isex = Integer.parseInt(temp);

                    if (isex % 2 == 0) {// 除2余数为0

                        result = "true:身份证号通过验证";

                    } else {

                        result = "false:身份证号与性别不相符！";

                    }

                } else {
                     result = "true:身份证号通过验证";
                }

            } else {
                // 出生日期与身份证中的出生日期不相符
                result = "false:身份证号码与出生日期不相符！";
            }
        } else {
            if ("1".equals(sex)) {

                // 性别为男，最后一位是奇数

                String temp = id.substring(16, 17);// 倒数第二位代表性别

                int isex = Integer.parseInt(temp);

                if (isex % 2 == 1) {// 除2余数为1

                    result = "true:身份证号通过验证";

                } else {

                    result = "false:身份证号与性别不相符！";

                }

            } else if ("2".equals(sex)) {

                // 性别为女，最后一位是偶数

                String temp = id.substring(16, 17);// 倒数第二位代表性别

                int isex = Integer.parseInt(temp);

                if (isex % 2 == 0) {// 除2余数为0

                    result = "true:身份证号通过验证";

                } else {

                    result = "false:身份证号与性别不相符！";

                }

            } else {
                 result = "true:身份证号通过验证";
            }
        }

        return result;

    }
    public int checkBit(String id) {

        int[] wi = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

        String lowerId = id.substring(0, 17);

        int sum = 0;

        for (int i = 1; i < lowerId.length() + 1; i++) {

            sum = sum + wi[i - 1]

                    * (Integer.parseInt(lowerId.substring(i - 1, i)));

        }

        int mod = sum % 11;

        return mod;

    }
    public String  changeCardID(String personIDCode,String birthday) {
         if ( personIDCode != null&&personIDCode.trim().length() == 15 ) {
             return cardId15to18(personIDCode,birthday);
         }else if ( personIDCode != null&&personIDCode.trim().length() == 18 ) {
             return cardId18to15(personIDCode);
         }else {
             return personIDCode;
         }
    }
    /**
     * 身份证15位转18位
     * @param personIDCode 身份证
     * @param birthday 出生日期
     * @return
     */
    public String cardId15to18(String personIDCode,String birthday){
        if ( personIDCode == null || personIDCode.trim().length() != 15 ) {
          return personIDCode;
        }
        String year2 = "19";
        if(birthday!=null&&birthday.length()>2) {
            year2 = birthday.substring(0,2);
        }
        String id17 = personIDCode.substring(0,6) + year2 + personIDCode.substring(6,15);  //15为身份证补'19'

        char[] code = {'1','0','X','9','8','7','6','5','4','3','2'};  //11个
        int[] factor={0, 2,4,8, 5,10,9,7, 3,6,1,2, 4,8,5,10, 9,7}; //18个;
        int[] idcd = new int[18];
        int     i;
        int     j;
        int     sum;
        int     remainder;

        for (i=1; i<18; i++)
        {
          j = 17 - i ;

          Pattern p = Pattern.compile("^-?[1-9]\\d*$");
          Matcher m = p.matcher(id17.substring(j, j+1));
          boolean b = m.matches();
          if(b) {
              idcd[i] = Integer.parseInt(id17.substring(j, j+1));
          }
        }

        sum = 0;
        for (i=1; i<18; i++)
        {
          sum = sum  + idcd[i] * factor[i];
        }
        remainder = sum%11;
        String lastCheckBit = String.valueOf(code[ remainder ]);
        return id17 + lastCheckBit;


    }
    public String cardId18to15(String personIDCode){
        if ( personIDCode == null || personIDCode.trim().length() != 18 ) {
            return personIDCode;
        }
        return personIDCode.substring(0,6)+personIDCode.substring(8,17);
    }
    public char doVerify(String id){
        if(id.trim().length()==15) {
            id = cardId15to18(id,"");
        }
        if(id.trim().length()!=18) {
            return 'n';
        }
        id=id.toUpperCase();
        char pszSrc[]=id.toCharArray();
        int iS = 0;
        int iW[]={7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        char szVerCode[] = new char[]{'1','0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int i;
        for(i=0;i<17;i++){
            iS += (int)(pszSrc[i]-'0')* iW[i];
        }
        int iY = iS%11;
        if(pszSrc[17]!=szVerCode[iY]) {
            return 'n';
        }
        return szVerCode[iY];
    }

    /**
     * 删除人员业务表数据
     * 删除人员的业务数据时，暂时将把人员库前缀转为大写的代码去掉，否则有可能影响删除人员的速度
     * @param nbase
     * @param a0100
     * @throws GeneralException
     */
    public void delBusiTableData(String nbase, String a0100) throws GeneralException {
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            // 考勤模块
            // 删除考勤日明细
            dao.update("delete from Q03 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤月汇总
            dao.update("delete from Q05 where NBase='" + nbase + "'  and A0100='" + a0100 + "'");
            // 删除考勤请假表
            dao.update("delete from Q15 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤加班表
            dao.update("delete from Q11 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤公出表
            dao.update("delete from Q13 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤假期管理表
            dao.update("delete from Q17 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除调班
            dao.update("delete from Q19 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤调休表
            dao.update("delete from Q25 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤个人休假计划申请表
            dao.update("delete from Q31 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除考勤调休明细表
            dao.update("delete from Q33 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除月度考勤数据明细表(国网)
            DbWizard db = new DbWizard(this.conn);
            if(!db.isExistField("Q35", "guidkey", false) && db.isExistField("Q35", "A0100", false)) {
                dao.update("delete from Q35 where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            }
            // 57602 删除人员操作时 不需要删除Q35（新考勤）记录中的数据
//            else if(db.isExistField("Q35", "guidkey", false))
//                dao.update("delete from Q35 where guidkey=(select guidkey from " + nbase + "A01 where a0100='" + a0100 + "')");
            
            // 删除员工排班信息表
            dao.update("delete from kq_employ_shift where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除员工班组对应信息表
            dao.update("delete from kq_group_emp where NBase='" + nbase + "' and A0100='" + a0100 + "'");
            // 删除员工原始刷卡信息
            dao.update("delete from kq_originality_data where NBase='" + nbase + "' and A0100='" + a0100 + "'");

            dao.update("delete from t_sys_function_priv where (id='" + nbase.toUpperCase() + a0100 + "'"
                    + " or id='" + nbase.toLowerCase() + a0100 + "' or id='" + nbase + a0100 + "') and status=4");
            dao.update("delete from t_sys_staff_in_role where (staff_id='" + nbase.toUpperCase() + a0100 + "'"
                    + " or staff_id='" + nbase.toLowerCase() + a0100 + "' or staff_id='" + nbase + a0100 + "'"
                    + ") and status=1");

            // 删除薪资发放临时表里的映射数据
            dao.update("delete from salary_mapping where (NBase='" + nbase.toUpperCase() + "'"
                    + " or NBase='" + nbase + "' or NBase='" + nbase.toLowerCase() + "')"
                    + " and A0100='" + a0100 + "'");            
            
            // 删除个税申报数据
            if(db.isExistTable("declare_infor", false)) {
                dao.update("delete from declare_sub where declare_id in (select id from declare_infor where guidkey=(select guidkey from " + nbase + "A01 where a0100='" + a0100 + "'))");
                dao.update("delete from declare_infor where guidkey=(select guidkey from " + nbase + "A01 where a0100='" + a0100 + "')");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
    }
    
    private String getId(String changeflag){
        String year = Calendar.getInstance().get(Calendar.YEAR)+"";
        String month = (Calendar.getInstance().get(Calendar.MONTH)+1)+"";
        if("2".equals(changeflag)){
            return " and id='"+year+"'";
        }else if("1".equals(changeflag)){
            if(month!=null&&Integer.parseInt(month)>9) {
                return " and id='"+year+"."+month+"'";
            } else {
                return " and id='"+year+".0"+month+"'";
            }
        }else{
            return "";
        }
    }
    
    /**
     * 
     * @param tablename 子集
     * @param a0100 人员编号
     * @param pos_field 兼任兼职指标
     * @param pos_value 兼任兼职值
     * @param appoint_field 任免指标
     * @return
     */
    public boolean checkParttimeCount(String tablename,String a0100,String pos_field,String pos_value,String appoint_field,String mode){
        boolean flag = false;
        ResultSet rs = null;
        try{
            PosparameXML pos = new PosparameXML(conn); 
            String dbs = pos.getValue(PosparameXML.AMOUNTS,"dbs"); 
            dbs=dbs!=null&&dbs.trim().length()>0?dbs:"";
            tablename=tablename!=null?tablename:"";
            String dbpre = tablename.substring(0,3);
    
            if(dbs.toUpperCase().indexOf(dbpre.toUpperCase())!=-1){ 
                RecordVo pos_vo_ctrl=ConstantParamter.getRealConstantVo("PS_WORKOUT", conn);    
                if((!(pos_vo_ctrl==null|| "".equals(pos_vo_ctrl.getString("str_value")))))
                {
                    ContentDAO dao = new ContentDAO(this.conn);
                    rs=dao.search("select "+pos_field+" from "+tablename+" where a0100='"+a0100+"' and "+appoint_field+"='0' and "+pos_field+"='"+pos_value+"'");
                    if(!rs.next()){
                        flag=overPosWorkOut(pos_value,1,pos_vo_ctrl.getString("str_value"),"1",mode);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        	PubFunc.closeResource(rs);
        }
        return flag;
    }

    /*
     * 获取删除单位或部门时 更新兼职 表 在任状态的 sql
     */
    
    
    public String getUpdateJZSql(String orgcode){
        Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
         /**兼职参数*/
         String flag=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"flag");
         if(flag==null|| "".equalsIgnoreCase(flag)|| "false".equalsIgnoreCase(flag)) {
             return "";
         }
         String setid=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"setid");
         /**兼职单位字段*/
         String unit_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"unit"); 
         //兼职部门
         String dept_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"dept"); 
         //兼职 职务
         String pos_field = sysbo.getValueS(Sys_Oth_Parameter.PART_TIME, "pos");
         
         
         /**任免标识字段*/
         String appoint_field=sysbo.getValueS(Sys_Oth_Parameter.PART_TIME,"appoint");

         /**分析此定义的数据集和指标是否构库*/
         FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
         if(fieldset==null|| "0".equalsIgnoreCase(fieldset.getUseflag())) {
             return "";
         }
         FieldItem fielditem=DataDictionary.getFieldItem(unit_field);
         if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
             return "";
         }
         fielditem=DataDictionary.getFieldItem(appoint_field);
         if(fielditem==null|| "0".equalsIgnoreCase(fielditem.getUseflag())) {
             return "";
         }
         FieldItem dept_fielditem=DataDictionary.getFieldItem(dept_field);
         
         FieldItem pos_fielditem = DataDictionary.getFieldItem(pos_field);
         
         /**权限过滤*/               
         String app_set="USR"+setid;
         
         StringBuffer updateSql = new StringBuffer();//;"update "+app_set+" set "+appoint_field+"=1 where "+unit_field+"='"+orgcode+"' ";
          updateSql.append("update "+app_set+" set "+appoint_field+"=1 where ");
          updateSql.append(unit_field+" like '"+orgcode+"%' ");
          if(dept_field.length()>0) {
              updateSql.append(" or "+dept_field+" like '"+orgcode+"%'");
          }
          
          //添加 兼职职务 判断  指标存在、已构库、关联岗位代码    guodd 2014-11-26
        if(pos_fielditem!=null && "1".equalsIgnoreCase(pos_fielditem.getUseflag()) && "@K".equalsIgnoreCase(pos_fielditem.getCodesetid())) {
            updateSql.append(" or "+pos_field+" like '"+orgcode+"%'");
        }
        
         return updateSql.toString();
    }
    
    public void setNbase(String nbase) {
        this.nbase = nbase;
    }
    
}
