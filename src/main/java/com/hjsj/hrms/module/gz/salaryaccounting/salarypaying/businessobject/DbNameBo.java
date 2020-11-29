package com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzItemVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class DbNameBo {

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
     * 薪资发放/薪资报批 总额控制备份数据
     *  @param conn 连接
     * @param userview 操作人
     * @param gz_tablename 临时表名
     * @param salaryid  薪资帐套ID
     * @param manager  共享帐套管理员(不是共享帐套为空) 
     * @param approveObject  报批给谁
     * @param filterWhl  报批记录的筛选条件 
     * @param gzbo 
     * @throws GeneralException
     */
    synchronized static  public  void autoAddZ1_total(UserView userview,String gz_tablename,String salaryid,String manager,String gtandardGzItemStr,String approveObject,boolean isSp)throws GeneralException
    {
    	Connection conn=null;
    	RowSet rowSet=null;
        try
        {
            conn = AdminDb.getConnection();
            ContentDAO dao = new ContentDAO(conn);
            if(manager!=null&&manager.trim().length()>0)
                dao.update("update "+gz_tablename+" set userflag='"+manager+"'");
            else
                dao.update("update "+gz_tablename+" set userflag='"+userview.getUserName()+"'");
            String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
            StringBuffer strsql=new StringBuffer("");
            if(Sql_switcher.searchDbServer()==2)
                strsql.append("create table "+table+" as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3 ");
            else 
                strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z3  into "+table);
            strsql.append("  from  "+gz_tablename+" ss,salaryhistory gm where ");
            strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
        //  strsql.append(" and lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"'  and ( ss.sp_flag='07' or ss.sp_flag='01')  "); 
            // 多次发放同一人，最后一次导入这个人 导致他的a00z1是1，然后会造成salaryhistory前1次的数据被删
            strsql.append(" and ( lower(gm.userflag)<>'"+userview.getUserName().toLowerCase()+"' or ( lower(gm.userflag)='"+userview.getUserName().toLowerCase()+"'  and  ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  )) and ( ss.sp_flag='07' or ss.sp_flag='01')  ");

            DbWizard dbw=new DbWizard(conn);
          //  if(dbw.isExistTable(table,false))
            {
                dbw.dropTable(table);
           
            }  
            dao.update(strsql.toString()); 
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
            rowSet=dao.search("select * from "+gz_tablename+" where 1=2");
            ResultSetMetaData metaData=rowSet.getMetaData();
            StringBuffer s1=new StringBuffer(",salaryid,curr_user,dbid,e0122_o,b0110_o");
            StringBuffer s2=new StringBuffer(","+salaryid+",'"+approveObject+"',dbid,e0122_o,b0110_o");
            for(int i=1;i<=metaData.getColumnCount();i++)
            {               
                    if(gtandardGzItemStr.indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1)
                        continue;
                    
                    
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
            del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  ");
            if(!isSp) //考虑重复提交
            	del.append(" and ( sp_flag='07' or sp_flag='01' or sp_flag='06')  ) ");
            else
            	del.append(" and ( sp_flag='07' or sp_flag='01')  ) ");
            del.append(" and salaryid="+salaryid);
            String[] atemps=gz_tablename.toLowerCase().split("_salary_");
            del.append(" and lower(userflag)='");  //20100323
            del.append(atemps[0].toLowerCase());
            del.append("'");
            dao.delete(del.toString(),new ArrayList());
            
            String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+gz_tablename;
            if(!isSp) //考虑重复提交
            	sql0+=" where  ( sp_flag='07' or sp_flag='01' or sp_flag='06' )";
            else
            	sql0+=" where  ( sp_flag='07' or sp_flag='01' )";
            dao.update(sql0);
            
            strsql.setLength(0);
            String temp_name="t#"+userview.getUserName()+"_gz"; //"salaryhistorybak_"+userview.getUserName();
        //    if(dbw.isExistTable(temp_name,false))
            dbw.dropTable(temp_name);
            
            if(Sql_switcher.searchDbServer()==2)
                strsql.append("create table  "+temp_name+"  as select *");
            else
                strsql.append("select * into "+temp_name+" ");
            strsql.append(" from salaryhistory where exists (select null from "+gz_tablename);
            strsql.append(" where lower("+gz_tablename+".nbase)=lower(salaryhistory.nbase) and "+gz_tablename+".a0100=salaryhistory.a0100 ");
            if(!isSp)
                strsql.append(" and  "+gz_tablename+".a00z0=salaryhistory.a00z0 and "+gz_tablename+".a00z1=salaryhistory.a00z1 and ( "+gz_tablename+".sp_flag='07' or "+gz_tablename+".sp_flag='06' or "+gz_tablename+".sp_flag='01'  ) ) ");  //  加上06 ,考虑重复提交  
            else
                strsql.append(" and  "+gz_tablename+".a00z0=salaryhistory.a00z0 and "+gz_tablename+".a00z1=salaryhistory.a00z1 and ( "+gz_tablename+".sp_flag='07' or "+gz_tablename+".sp_flag='01'  ) ) ");  //or "+gz_tablename+".sp_flag='06'  加上06 ,考虑重复提交  
            strsql.append(" and salaryid="+salaryid);
            strsql.append(" and lower(userflag)='");
            strsql.append(userview.getUserName().toLowerCase());
            strsql.append("'");
            dao.update(strsql.toString());
            
            // 问题：按查询提交，先提交A，再提交B，B提交后A数据在salaryhistory中被删除
            // 原因：由于temp_name中的sp_flag都是02导致
            // 解决：将temp_name按照临时表（XX_salary_XX）的凡是结束状态的重新复制sp_flag
            strsql.setLength(0);
            strsql.append("update " + temp_name + " set sp_flag=(select sp_flag from "+gz_tablename);
            strsql.append(" where lower("+gz_tablename+".nbase)=lower(" + temp_name +".nbase) and "+gz_tablename+".a0100=" + temp_name + ".a0100 ");
            strsql.append(" and  "+gz_tablename+".a00z0=" + temp_name + ".a00z0 and "+gz_tablename+".a00z1=" + temp_name + ".a00z1 and "+gz_tablename+".sp_flag='06' ) ");
            strsql.append(" where exists (select null from "+gz_tablename);
            strsql.append(" where lower("+gz_tablename+".nbase)=lower(" + temp_name +".nbase) and "+gz_tablename+".a0100=" + temp_name + ".a0100 ");
            strsql.append(" and  "+gz_tablename+".a00z0=" + temp_name + ".a00z0 and "+gz_tablename+".a00z1=" + temp_name + ".a00z1 and "+gz_tablename+".sp_flag='06' ) ");
            dao.update(strsql.toString());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            throw GeneralExceptionHandler.Handle(ex);
        }
        finally
        {
        	PubFunc.closeDbObj(rowSet);
        	PubFunc.closeDbObj(conn);
        }
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
    synchronized static  public  void autoAddZ1_subhistory(UserView userview,String gz_tablename,String salaryid,String manager,boolean isAppeal,boolean isUpdateTax,SalaryCtrlParamBo ctrlparam,ArrayList gzitemlist,String filterWhl)throws GeneralException
    {
    	Connection conn=null;
    	try
    	{
    			conn = AdminDb.getConnection();
    			autoAddZ1(conn,userview,gz_tablename,salaryid,manager,isAppeal,isUpdateTax);
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
    					if(itemvo.getInitflag()==4)
    						continue;
    					if(fields.length()==0)
    						fields.append(itemvo.getFldname());
    					else
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
    				if(StringUtils.isNotBlank(manager)) {
    					buf.append("Appprocess,");//如果是共享账套需要加上审批意见
    				}
    				buf.append(fields.toString());
    				buf.append(") select userflag,");
    				buf.append(salaryid);
    				buf.append(",sp_flag,e0122_o,b0110_o,dbid,");//Appprocess
    				if(StringUtils.isNotBlank(manager)) {
    					buf.append("Appprocess,");//如果是共享账套需要加上审批意见
    				}
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
    	finally
    	{
    		PubFunc.closeDbObj(conn);
    	}
    
    }
    
    /**
     * 判断薪资临时表数据是否在历史表已存在，如存在次数自动加1
     *@param conn 连接
     * @param userview 操作人
     * @param gz_tablename 临时表名
     * @param salaryid  薪资帐套ID
     * @param manager  共享帐套管理员(不是共享帐套为空)   
     * @param isAppeal  是否是报批操作调用
     * @throws GeneralException
     * 此方法用来解决临时表归属次数和历史表重复问题。针对临时表存在一人多条的情况，如果临时表归属次数比历史表大，
     * 那么使用临时表归属次数（反之使用历史表中最大归属次数）加上每个人的重复的次数，第一次重复加一，第二次加二。
     * 如果是sql2000 那么如果不存在一人多条归属次数重复的情况，则使用历史表最大归属次数加1，否则使用历史表最大归属次数加上临时表中的归属次数
     */
    synchronized static  public  void autoAddZ1(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,boolean isAppeal,boolean isUpdateTax)throws GeneralException
    {
    	  String withNoLock="";
          if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
              withNoLock=" WITH(NOLOCK) ";
        RowSet rowSet=null;
        ContentDAO dao = new ContentDAO(conn);

        try
        {
            DatabaseMetaData dbMeta = conn.getMetaData();
            boolean isOk=false;
            if(Sql_switcher.searchDbServer()==2){
                isOk=true;
            }else{
                //针对sql2005及以上版本，和oracle使用新的生成a00z1的方法，解决a00z1成倍增长 zhanghua 2018-1-3
                int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
                if(version!=8){
                    isOk=true;
                }
            }
        	 String[] _temps=gz_tablename.toLowerCase().split("_salary_");
             if(manager!=null&&manager.length()>0)
                 dao.update("update "+gz_tablename+" set userflag='"+manager+"'");
             else
                 dao.update("update "+gz_tablename+" set userflag='"+userview.getUserName()+"'");
             String table="t#"+userview.getUserName()+"_gz_1"; //userview.getUserName()+"_tempZ1Table";
             StringBuffer strsql=new StringBuffer("");
             if(Sql_switcher.searchDbServer()==2) {
                 strsql.append("create table " + table + " as select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z2,ss.a00z3,0 as a00z4 ,st.maxA00Z1,row_number() OVER (PARTITION BY ss.a0100,ss.NBASE ORDER BY ss.A00Z1 ) AS num ");
             }
             else {
                 strsql.append("select ss.nbase,ss.a0100,ss.a00z1,ss.a00z0,ss.a00z2,ss.a00z3,0 as a00z4,st.maxA00Z1, ");
                 if(isOk)
                     strsql.append(" row_number() OVER (PARTITION BY ss.a0100,ss.NBASE ORDER BY ss.A00Z1 ) AS num  ");//查询出每个人重复的次数
                 else
                     strsql.append(" 0 as num ");
                 strsql.append(" into " + table);
             }
             strsql.append("  from  "+gz_tablename+" ss"+withNoLock+",salaryhistory gm "+withNoLock+" ,(SELECT a0100,nbase,MAX(a00z1) AS maxA00Z1 FROM "+gz_tablename+" GROUP BY a0100,nbase ) st  where ");
             strsql.append(" gm.salaryid="+salaryid+" and lower(ss.nbase)=lower(gm.nbase) and ss.a0100=gm.a0100 and ss.a00z0=gm.a00z0 and ss.a00z1=gm.a00z1 ");
             strsql.append(" AND ss.A0100=st.A0100 AND LOWER(ss.NBASE) = LOWER(st.NBASE) ");
             strsql.append(" and ( lower("+Sql_switcher.isnull("gm.userflag","''")+")<>'"+_temps[0].toLowerCase()+"' ");
             strsql.append(" or (  lower("+Sql_switcher.isnull("gm.userflag","''")+")='"+_temps[0].toLowerCase()+"' and ( gm.a00z2<>ss.a00z2 or gm.a00z3<>ss.a00z3 )  ) ) ");
             if(isAppeal)
            	 strsql.append(" and ( ss.sp_flag='07' or ss.sp_flag='01')  ");
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
                 if(isOk) {
                     dao.update(" update " + table + " set a00z4=(CASE WHEN a00z4<maxA00z1 THEN maxA00z1 ELSE a00z4 END) +num ");
                 }else{//sql2000 如果一人只有一条重复归属次数，那么最大历史表归属次数加一，否则用最大临时表归属次数加最大历史表归属次数。
                     dao.update(" UPDATE t#su_gz_1 SET num=1 WHERE (SELECT COUNT(1) AS num FROM t#su_gz_1 as t WHERE t#su_gz_1.a0100=t.a0100 AND UPPER(t#su_gz_1.nbase)=UPPER(t.nbase)  GROUP BY a0100,nbase)=1 ");
                     dao.update(" update " + table + " set a00z4=(CASE WHEN a00z4<maxA00z1 THEN maxA00z1 ELSE a00z4 END) +1 where num=1 ");
                     dao.update(" update " + table + " set a00z4=a00z4+a00z1 where num=0 ");
                 }
                 strsql.setLength(0);
                 strsql.append("update "+gz_tablename+" set a00z1=(select a00z4 from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 "); 
                 strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
                 strsql.append(" where exists (select null from "+table+" where   "+table+".a0100="+gz_tablename+".a0100 "); 
                 strsql.append(" and "+table+".a00z1="+gz_tablename+".a00z1 and "+table+".a00z0="+gz_tablename+".a00z0 and lower("+table+".nbase)=lower("+gz_tablename+".nbase)) ");
                 dao.update(strsql.toString());
                 if(isUpdateTax&&dbw.isExistTable("gz_tax_mx",false))
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
     *薪资报批
     *判断临时表人员的归属年月次数在历史表相同帐套下是否有相同记录(别人发的) ,如相同需将归属次数自动加1,再将临时表数据导入到历史表中,防止主健冲突
     * @param conn 连接
     * @param userview 操作人
     * @param gz_tablename 临时表名
     * @param salaryid  薪资帐套ID
     * @param manager  共享帐套管理员(不是共享帐套为空) 
     * @param approveObject  报批给谁
     * @param filterWhl  报批记录的筛选条件
     * @param itemStr  
     * @throws GeneralException
     */
    synchronized static  public  void autoAppeal(Connection conn,UserView userview,String gz_tablename,String salaryid,String manager,String approveObject,String filterWhl,String itemStr)throws GeneralException
    {
        RowSet rowSet=null;
        try
        {
            /** 同步历史表和薪资临时表 */
            String withNoLock="";
            if(Sql_switcher.searchDbServer()!=2) //针对SQLSERVER 无需考虑锁表
                withNoLock=" WITH(NOLOCK) ";
            ContentDAO dao = new ContentDAO(conn);
            String toName=DbNameBo.getNameByUsername(approveObject,conn);
            String fromName=DbNameBo.getNameByUsername(userview.getUserName(),conn);
            autoAddZ1(conn,userview,gz_tablename,salaryid,manager,true,true);
            //------------------------------------------------------------------
            
            String groupName="";
            if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0)
                rowSet=dao.search("select b0110,e0122 from "+userview.getDbname()+"A01 where a0100='"+userview.getA0100()+"'");
            else
                rowSet=dao.search("select groupName from operuser,usergroup where operuser.groupid=usergroup.groupid and username='"+userview.getUserName()+"'");
            if(rowSet.next())
            {
                if(userview.getA0100()!=null&&userview.getA0100().trim().length()>0)
                {
                    String b0110=rowSet.getString("b0110")!=null?rowSet.getString("b0110").trim():"";
                    String e0122=rowSet.getString("e0122")!=null?rowSet.getString("e0122").trim():"";
                    if(e0122.length()>0)
                        groupName=AdminCode.getCodeName("UM",e0122);
                    else if(b0110.length()>0)
                        groupName=AdminCode.getCodeName("UN",b0110);
                }
                else
                    groupName=rowSet.getString(1);
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
                    if(itemStr.indexOf("/"+metaData.getColumnName(i).toUpperCase()+"/")==-1)
                        continue;
                    
                    
                    if("sp_flag".equalsIgnoreCase(metaData.getColumnName(i)))
                    {
                        s1.append(","+metaData.getColumnName(i));
                        s2.append(",'02'");
                    } 
                    else if("appprocess".equalsIgnoreCase(metaData.getColumnName(i))&&Sql_switcher.searchDbServer()!=2)
                    {
                        s1.append(","+metaData.getColumnName(i));
//                        // userview.getUserName  改为  userview.getUserFullName()  //2013-11-27  dengc  将用户名改为姓名
//                        s2.append(",case when  sp_flag='07' then  "+Sql_switcher.isnull(Sql_switcher.sqlToChar(metaData.getColumnName(i)),"''")+Sql_switcher.concat()+"'   \r\n"+ResourceFactory.getProperty("button.appeal")+": "+currentTime+"\n  "+groupName+" "+userview.getUserFullName()+" "+ResourceFactory.getProperty("gz_new.gz_accounting.appealto")+" "+toName+"'"); 
//                        s2.append(" else '"+ResourceFactory.getProperty("button.appeal")+": "+currentTime+"\n  "+groupName+" "+fromName+" "+ResourceFactory.getProperty("gz_new.gz_accounting.appealto")+" "+toName+"'  end  ");
                        s2.append(","+Sql_switcher.isnull(Sql_switcher.sqlToChar(metaData.getColumnName(i)),"''")+Sql_switcher.concat()+"'   \r\n"+ResourceFactory.getProperty("button.appeal")+": "+currentTime+"\n  "+groupName+" "+userview.getUserFullName()+" "+ResourceFactory.getProperty("gz_new.gz_accounting.appealto")+" "+toName+"'"); 
                        
                    }
                    else
                    { 
                            s1.append(","+metaData.getColumnName(i));
                            s2.append(","+metaData.getColumnName(i)); 
                    }
            }
            s1.append(",appuser");
            s2.append(",';"+userview.getUserName()+";'");//20151224 dengcan
            
            
            if(Sql_switcher.searchDbServer()==2) //oracle  先在临时表里更新填报过程信息
            {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String appprocess,nbase,a0100,year,month,z1,sp_flag;
                StringBuffer sql3=new StringBuffer("select appprocess,a0100,nbase,a00z0,a00z1,sp_flag from "+gz_tablename+" where ( sp_flag='07' or sp_flag='01' ) ");
                if(filterWhl.length()>0)
                    sql3.append(filterWhl);
                rowSet=dao.search(sql3.toString());
                sql3.setLength(0);
                sql3.append("update "+gz_tablename+" set appprocess=? where a0100=? and lower(nbase)=? and  "+Sql_switcher.year("a00z0")+"=?");
                sql3.append(" and  "+Sql_switcher.month("a00z0")+"=? and a00z1=?");
                if(filterWhl.length()>0)
                    sql3.append(filterWhl);
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
                        appprocess=ResourceFactory.getProperty("button.appeal")+": "+currentTime+"\n  "+groupName+" "+fromName+" "+ResourceFactory.getProperty("gz_new.gz_accounting.appealto")+" "+toName;
                    else if("07".equals(sp_flag)) //批准
                        appprocess+="   \r\n"+ResourceFactory.getProperty("button.appeal")+": "+currentTime+"\n  "+groupName+" "+fromName+" "+ResourceFactory.getProperty("gz_new.gz_accounting.appealto")+" "+toName;
                        
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
            
            //删掉历史表临时表需上报至历史表，在历史表中重复的数据
            StringBuffer del=new StringBuffer("delete from salaryhistory where exists (select * from "+gz_tablename);
            del.append(" where a0100=salaryhistory.a0100 ");
            if(filterWhl.length()>0)
                del.append(filterWhl);
            del.append(" and  upper(nbase)=upper(salaryhistory.nbase) and  a00z0=salaryhistory.a00z0 and  a00z1=salaryhistory.a00z1  and ( sp_flag='07' or sp_flag='01') ) ");
            del.append(" and salaryid="+salaryid);
            
            String[] atemps=gz_tablename.toLowerCase().split("_salary_");
            del.append(" and lower(userflag)='");  //20100323
            del.append(atemps[0].toLowerCase());
            del.append("'");
            dao.delete(del.toString(),new ArrayList());
            
            //将临时表里起草\驳回的薪资记录复制到salaryhistory表中
            String sql0="insert into salaryhistory ("+s1.substring(1)+") select "+s2.substring(1)+" from "+gz_tablename+" where  ( sp_flag='07' or sp_flag='01' )";
            if(filterWhl.length()>0)
                sql0+=filterWhl; 
            dao.update(sql0);
            
            
            s1.setLength(0);
			if(Sql_switcher.searchDbServer()!=Constant.ORACEL)  //非ORACLE库，需将审判过程同步到临时表中
			{
				s1.append("update    "+gz_tablename+"   set   "+gz_tablename+".appprocess= salaryhistory.appprocess "); 
				s1.append(" from   salaryhistory");
				s1.append(" where  salaryhistory.a0100="+gz_tablename+".a0100 and ");
				s1.append(" upper(salaryhistory.nbase)=upper("+gz_tablename+".nbase) ");
				s1.append(" and  salaryhistory.a00z0="+gz_tablename+".a00z0 and ");
				s1.append(" salaryhistory.a00z1="+gz_tablename+".a00z1  and salaryhistory.salaryid="+salaryid+" and  ( "+gz_tablename+".sp_flag='07' or "+gz_tablename+".sp_flag='01' )");
				if(filterWhl.length()>0)
					s1.append(filterWhl);
				dao.update(s1.toString());
			} 
			
            
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            String message=ex.toString();
            if(message.indexOf(ResourceFactory.getProperty("tablegrid.summary.max"))!=-1&&message.indexOf("8060")!=-1&&Sql_switcher.searchDbServer()==1)
            { 
                PubFunc.resolve8060(conn,"salaryhistory");
                throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz_new.dbname.reopt")+"!"));
            }
            else
                throw GeneralExceptionHandler.Handle(ex);
             
        }
        finally
        {
            try
            {
                if(rowSet!=null)
                    rowSet.close();
            }
            catch(Exception ee)
            {
                ee.printStackTrace();
            }
        }
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
            rowSet=dao.search("select a0100,nbase,fullname   from operuser,usergroup where operuser.groupid=usergroup.groupid and username=?",Arrays.asList(new Object[]{username}));
            if(rowSet.next())
            {
                String a0100=rowSet.getString("a0100")!=null?rowSet.getString("a0100").trim():"";
                String nbase=rowSet.getString("nbase")!=null?rowSet.getString("nbase").trim():"";
                String fullname=rowSet.getString("fullname")!=null?rowSet.getString("fullname").trim():""; 
                if(a0100.length()>0&&nbase.length()>0)
                {
                    rowSet=dao.search("select a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
                    if(rowSet.next())
                        name=rowSet.getString("a0101");
                }
                else if(fullname.length()>0)
                {
                    name=fullname;
                }
                
            } 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        	PubFunc.closeDbObj(rowSet);
        }
        return name;
    }
    
}
