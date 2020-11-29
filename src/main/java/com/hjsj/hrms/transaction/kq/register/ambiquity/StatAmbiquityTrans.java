package com.hjsj.hrms.transaction.kq.register.ambiquity;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StatAmbiquityTrans extends IBusiness{
     private String error_return="/kq/register/daily_register.do?b_search=link&action=daily_registerdata.do";
    public void execute() throws GeneralException
    {
       String error_message="";
       String error_flag="0";
       //String stat_type=(String)this.getFormHM().get("stat_type");  
       String stat_type="1";
       ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");
       String select_flag=(String)this.getFormHM().get("select_flag");
        String select_name=(String)this.getFormHM().get("select_name");
        String select_pre=(String)this.getFormHM().get("select_pre");
        this.getFormHM().put("select_flag",select_flag);
        this.getFormHM().put("select_name",select_name);     
       String code=(String)this.getFormHM().get("code");
       String kind=(String)this.getFormHM().get("kind");
       if (kind == null)
           kind = "";
       
       if(kq_dbase_list==null||kq_dbase_list.size()<=0)
       {
           KqUtilsClass kqUtilsClass = new KqUtilsClass(this.getFrameconn(),this.userView);
           kq_dbase_list = kqUtilsClass.getKqPreList();
       }   
       
       String q03z0="";
       ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
       String kq_period="";       
       if("0".equals(stat_type.trim()))
       {
           //按考勤期间范围内          
           String coursedate=(String)this.getFormHM().get("coursedate");
           q03z0=coursedate.substring(0,4);
           ArrayList datelist=RegisterDate.getKqDate(this.getFrameconn(),coursedate);
           String start_date=datelist.get(0).toString();
           String end_date=datelist.get(datelist.size()-1).toString();
           getCourseCollect(q03z0,start_date,end_date,kq_dbase_list,fielditemlist);
           kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);
       }else if("1".equals(stat_type.trim()))
       {
           //按期间范围内
           String count_duration=(String)this.getFormHM().get("count_duration");
           q03z0=count_duration.substring(0,4);
           String stat_start=(String)this.getFormHM().get("stat_start");
           String stat_end=(String)this.getFormHM().get("stat_end");
           stat_start=stat_start.replaceAll("-","\\.");
           stat_end=stat_end.replaceAll("-","\\.");
           stat_start=RegisterInitInfoData.getDateStr(stat_start);
           stat_end=RegisterInitInfoData.getDateStr(stat_end);
           
           getCourseCollect(q03z0,stat_start,stat_end,kq_dbase_list,fielditemlist);
           kq_period=CollectRegister.getMonthRegisterDate(stat_start,stat_end);
       }else
       {
           //抛异常
           error_message=ResourceFactory.getProperty("kq.register.collect.lost");   
           this.getFormHM().put("error_message",error_message);
           this.getFormHM().put("error_return",this.error_return);  
           this.getFormHM().put("error_flag","3");
           return;
          // throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"",""));
       }
       
       ArrayList fieldlist=CollectRegister.ambiFieldItemList(fielditemlist);
       FieldItem fielditem=new FieldItem();
       fielditem.setFieldsetid("Q05");
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
       fielditem.setItemid("scope");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("0");
       fielditem.setVisible(false);
       fieldlist.add(fielditem); 
       
       RegisterInitInfoData registerInfoData = new RegisterInitInfoData();
       fieldlist = registerInfoData.getNewItemList(fieldlist);
       
       ArrayList sql_db_list=new ArrayList();
       if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
       {
            sql_db_list.add(select_pre);
       }else
       {
            sql_db_list=kq_dbase_list;
       }
       KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
       String where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
       ArrayList sqllist = CollectRegister.getSqlstr2(fieldlist,sql_db_list,q03z0,code,kind,"Q05",this.userView,"all",where_c);  
       this.getFormHM().put("sqlstr", sqllist.get(0).toString());
       this.getFormHM().put("columns", sqllist.get(3).toString());
       this.getFormHM().put("strwhere", sqllist.get(1).toString());       
       this.getFormHM().put("orderby", sqllist.get(2).toString());
       this.getFormHM().put("fielditemlist", fieldlist);
       this.getFormHM().put("duration",q03z0);
       this.getFormHM().put("code",code);
       this.getFormHM().put("kind",kind);    
       this.getFormHM().put("condition","5`"+sqllist.get(4).toString());
       this.getFormHM().put("relatTableid","5");
       this.getFormHM().put("kq_period",kq_period);
       this.getFormHM().put("error_flag",error_flag);     
       this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
       //不定期 展现如果是2 不展现不定期
       String flag="2";
       this.getFormHM().put("flag", flag);
    }
    /**
     * 汇总统计
     * **/
    public void getCourseCollect(String q03z0,String start_date,String end_date,ArrayList kq_dbase_list,ArrayList fielditemlist)throws GeneralException
    {
        StringBuffer statcolumn=new StringBuffer();
       StringBuffer insertcolumn=new StringBuffer();
       StringBuffer un_statcolumn=new StringBuffer();
       StringBuffer un_insertcolumn=new StringBuffer();
       int num=0;
       int un_num=0;
       for(int i=0;i<fielditemlist.size();i++){
         FieldItem fielditem=(FieldItem)fielditemlist.get(i);
      //类型为N的时候，如果指标为主集中的指标也不能计算
         boolean booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc());
         {
             if("N".equals(fielditem.getItemtype()))
             {
                 
                 if(!"i9999".equals(fielditem.getItemid()))
                 {
                    int want_sum= CollectRegister.getWant_Sum(fielditem.getItemid(),this.getFrameconn());
                
                    if(want_sum==1)
                    {
                       statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
                       insertcolumn.append(""+fielditem.getItemid()+",");
                     
                    }
                    un_statcolumn.append("sum("+fielditem.getItemid()+") as "+fielditem.getItemid()+",");
                    un_insertcolumn.append(""+fielditem.getItemid()+",");
                 }
              } 
         }
       }
       
       String statcolumnstr="";
       String insertcolumnstr="";     
       if(statcolumn.toString()!=null&statcolumn.toString().length()>0)
       {
          int l=statcolumn.toString().length()-1;
          statcolumnstr=statcolumn.toString().substring(0,l);
          l=insertcolumn.toString().length()-1;   
          insertcolumnstr=insertcolumn.toString().substring(0,l);   
       }else
       {
          int l=un_statcolumn.toString().length()-1;
          statcolumnstr=un_statcolumn.toString().substring(0,l);
          l=un_insertcolumn.toString().length()-1;        
          insertcolumnstr=un_insertcolumn.toString().substring(0,l);
          num=un_num;
       }  
        KqParameter para=new KqParameter(this.userView,"",this.getFrameconn());
        HashMap hashmap =para.getKqParamterMap();
        String kq_type=(String)hashmap.get("kq_type");//考勤方式字段
       //插入汇总人员记录
        String kq_period=CollectRegister.getMonthRegisterDate(start_date,end_date);         
       //主集中的指标,汇总过来但是不能更改
        String mainindex=getmainsql();
        String mainindex1=getmainsql2();
        CollectRegister collectRegister=new CollectRegister();
        collectRegister.setConn(this.frameconn);
        for(int r=0;r<kq_dbase_list.size();r++)
        {
           String nbase=kq_dbase_list.get(r).toString();       
           String whereIN=RegisterInitInfoData.getWhereINSql(this.userView,nbase);    
           //synchronizationInitQ05(nbase,whereIN,q03z0);
           //String whereB0110=RegisterInitInfoData.selcet_OrgId(nbase,"b0110",whereIN);
           //ArrayList orgidb0110List=OrgRegister.getQrgE0122List(this.getFrameconn(),whereB0110,"b0110");
           ContentDAO dao=new ContentDAO(this.getFrameconn());
           boolean isCorrect=true;
           if(!userView.isSuper_admin())
             {
                 //for(int s=0;s<orgidb0110List.size();s++)
                 //{
                    // String b0110_one=orgidb0110List.get(s).toString();
                     boolean if_delete=delRecord(nbase,"",q03z0,whereIN);          
                     if(if_delete)
                     {
                         //isCorrect=collectRecord2(dao,nbase,start_date,end_date,b0110_one,fielditemlist,whereIN,q03z0,mainindex,mainindex1,insertcolumnstr,statcolumnstr,kq_period);
                         isCorrect=collectRecord2(dao,nbase,start_date,end_date,"",fielditemlist,whereIN,q03z0,kq_type,insertcolumnstr,statcolumnstr,kq_period,mainindex,mainindex1);
                     }else
                     {
                           isCorrect=false;
                           String error_message=ResourceFactory.getProperty("kq.register.collect.lost");    
                           this.getFormHM().put("error_message",error_message);
                           this.getFormHM().put("error_return",this.error_return);  
                           this.getFormHM().put("error_flag","3");
                           return;   //抛出删除失败
                        //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"",""));    
                     }
                      
                    
                 //}
            }else
            {
                //ArrayList b0100list=RegisterInitInfoData.getAllBaseOrgid(nbase,"b0110",whereIN,this.getFrameconn());
                //for(int t=0;t<b0100list.size();t++)
                //{
                    //String b0100=b0100list.get(t).toString();
                    boolean if_delete=delRecord(nbase,"",q03z0,whereIN);           
                    if(if_delete)
                    {
                        //isCorrect=collectRecord2(dao,nbase,start_date,end_date,b0100,fielditemlist,whereIN,q03z0,mainindex,mainindex1,insertcolumnstr,statcolumnstr,kq_period);
                        isCorrect=collectRecord2(dao,nbase,start_date,end_date,"",fielditemlist,whereIN,q03z0,kq_type,insertcolumnstr,statcolumnstr,kq_period,mainindex,mainindex1);
                    }else{
                         //抛出删除失败
                           String error_message=ResourceFactory.getProperty("kq.register.collect.lost");    
                           this.getFormHM().put("error_message",error_message);
                           this.getFormHM().put("error_return",this.error_return);  
                           this.getFormHM().put("error_flag","3");
                           return;   //抛出删除失败
                          //throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.collect.lost"),"",""));    
                    }
                       
                //}
                
            }
       }
       return;
    }    
    public boolean collectRecord2(ContentDAO dao,String userbase,String start_date,String end_date,String code, ArrayList fielditemlist,String whereIN,String kq_duration,String kq_typeField,String insertcolumnstr,String statcolumnstr,String kq_period,String mainindex,String mainindex1)throws GeneralException{
           boolean isCorrect=true;
           //建立一张临时表
          /*  String table_name="kqtemp_collect_"+this.userView.getUserName();      
            KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
            kqUtilsClass.dropTable(table_name);
            kqUtilsClass.createTempTable("q05", table_name, "q05.*","1=2","");  */
           String table_name="q05";    
           //拼写sum的sql语句       
           
           //插入汇总人员记录，修改：Q03Z3为从Q03表查询出来不为常量值‘0’
            
            StringBuffer sql= new StringBuffer();
            //主集中的指标
            
            if(!"".equals(mainindex)||mainindex.length()>0)
            {
                sql.append("insert into "+table_name+"(a0100,nbase,q03z0,"+insertcolumnstr+", scope, Q03Z5,"+mainindex+")");
//              sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01' from Q03");
                sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','01',"+mainindex1+" from Q03");
                sql.append(" where nbase='"+userbase+"'");
                sql.append(" and Q03Z0 >= '"+start_date+"'");
                sql.append(" and Q03Z0 <= '"+end_date+"'");    
                sql.append(" and b0110 like '"+code+"%'");         
                //sql.append(" and a0100 in(select a0100 "+whereIN+")");
                if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                {
                   sql.append(" and EXISTS(select a0100 "+whereIN+" and "+userbase+"A01.a0100=q03.a0100)");
                } else{
                   sql.append(" and EXISTS(select a0100 "+whereIN+" where "+userbase+"A01.a0100=q03.a0100)");
                }   
//              sql.append(" and Q03Z5 in ('01','07') GROUP BY  A0100 ");
                sql.append("  GROUP BY  A0100 ");
            }else
            {
                sql.append("insert into "+table_name+"(a0100,nbase,q03z0,"+insertcolumnstr+", scope, Q03Z5)");
//              sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','0','01' from Q03");
                sql.append("select  a0100,'"+userbase+"','"+kq_duration+"',"+statcolumnstr+",'"+kq_period+"','01' from Q03");
                sql.append(" where nbase='"+userbase+"'");
                sql.append(" and Q03Z0 >= '"+start_date+"'");
                sql.append(" and Q03Z0 <= '"+end_date+"'");    
                sql.append(" and b0110 like '"+code+"%'");         
                //sql.append(" and a0100 in(select a0100 "+whereIN+")");
                if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                {
                   sql.append(" and EXISTS(select a0100 "+whereIN+" and "+userbase+"A01.a0100=q03.a0100)");
                } else{
                   sql.append(" and EXISTS(select a0100 "+whereIN+" where "+userbase+"A01.a0100=q03.a0100)");
                }
//              sql.append(" and Q03Z5 in ('01','07') GROUP BY  A0100 ");
                sql.append("  GROUP BY  A0100 ");
            }
            
            
            try
            {
                dao.insert(sql.toString(), new ArrayList());
                                    
                sql.delete(0, sql.length());
                String destTab=table_name;//目标表xxxxxxxxxxx
                String srcTab=userbase+"A01";//源表
                String kqtypeSet="";//修改考勤方式
                if(kq_typeField!=null&&kq_typeField.length()>0){
                    kqtypeSet="`"+destTab+".q03z3="+srcTab+"."+kq_typeField+"";
                    String strJoin=destTab+".A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
                    String strSet=""+destTab+".q03z3="+srcTab+"."+kq_typeField+"`"+destTab+".a0101="+srcTab+".a0101";
                    String strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".q03z5='01'  and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                    strDWhere = strDWhere + " and (" + destTab + ".a0101 is null or " + destTab + ".a0101 = '')";
                    String strSWhere=srcTab+".b0110 like '"+code+"%'";//源表的过滤条件  
                    String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                    String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                    //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
                    update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
                    dao.update(update);
                }
                //同步日明细单位部门到月汇总
                destTab=table_name;//目标表xxxxxxxxxxx
                srcTab="q03";//源表
                String strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                String strSet=destTab+".b0110="+srcTab+".b0110`"+destTab+".e0122="+srcTab+".e0122`"+destTab+".e01a1="+srcTab+".e01a1";//更新串  xxx.field_name=yyyy.field_namex,....
                String strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'  and scope='"+kq_period+"' and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                strDWhere = strDWhere + " and (" + destTab + ".b0110 is null or " + destTab + ".b0110 = '')";   
                //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
                String strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 = '"+end_date+"' and "+srcTab+".b0110 like '"+code+"%' and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+strSWhere;
                String othWhereSql="";
                if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                {
                    othWhereSql="EXISTS(select a0100 "+whereIN+" and "+destTab+".a0100="+userbase+"A01.a0100)";
                }else{
                    othWhereSql="EXISTS(select a0100 "+whereIN+" where "+destTab+".a0100="+userbase+"A01.a0100)";
                }   
                //update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);                 
                //System.out.println(update);
                dao.update(update);                 
                //如果同步最后一天的单位部门没有,就同步最大部门的
                 switch(Sql_switcher.searchDbServer())
                 {
                      case Constant.MSSQL:
                      {
                          srcTab=userbase+"A01";//源表
                          strSet=destTab+".B0110="+srcTab+".B0110`"+destTab+".E0122="+srcTab+".E0122`"+destTab+".E01A1="+srcTab+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
                          strJoin=destTab+".A0100="+srcTab+".A0100";
                          strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'and scope='"+kq_period+"'  and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
                          //strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                          if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                          {
                              strSWhere="EXISTS(select a0100 "+whereIN+" and "+srcTab+".a0100="+destTab+".a0100)";
                          }else{
                              strSWhere="EXISTS(select a0100 "+whereIN+" where "+srcTab+".a0100="+destTab+".a0100)";
                          }                           
                          update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                          //System.out.println(update);
                          dao.update(update);
                          break;
                      }
                      case Constant.ORACEL:
                      {
                          strSet=destTab+".b0110=Max("+srcTab+".b0110)`"+destTab+".e0122=Max("+srcTab+".e0122)`"+destTab+".e01a1=Max("+srcTab+".e01a1)`"+destTab+".a0101=Max("+srcTab+".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
                          strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                          strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and scope='"+kq_period+"' and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                            //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
                          strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 >= '"+start_date+"' and "+srcTab+".Q03Z0<='"+end_date+"' and "+srcTab+".b0110 like '"+code+"%' and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                          update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                          strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                          //othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                          if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                          {
                                othWhereSql="EXISTS(select a0100 "+whereIN+" and "+destTab+".a0100="+userbase+"A01.a0100)";
                          }else{
                                othWhereSql="EXISTS(select a0100 "+whereIN+" where "+destTab+".a0100="+userbase+"A01.a0100)";
                          }
                          update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);                             
                          dao.update(update);   
                          break;
                      }
                      case Constant.DB2:
                      {
                          strSet=destTab+".b0110=Max("+srcTab+".b0110)`"+destTab+".e0122=Max("+srcTab+".e0122)`"+destTab+".e01a1=Max("+srcTab+".e01a1)`"+destTab+".a0101=Max("+srcTab+".a0101)";//更新串  xxx.field_name=yyyy.field_namex,....
                          strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                          strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and scope='"+kq_period+"'  and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
                          strDWhere=strDWhere+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                            //String strSWhere=srcTab+".b0110 ='"+code+"'";//源表的过滤条件  
                          strSWhere=srcTab+".nbase='"+userbase+"'  and "+srcTab+".Q03Z0 >= '"+start_date+"' and "+srcTab+".Q03Z0<='"+end_date+"' and "+srcTab+".b0110 like '"+code+"%' and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
                          update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                          strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".b0110", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e0122", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".e01a1", "'kong'")+"='kong'";
                          strJoin=strJoin+" and "+Sql_switcher.isnull(destTab+".a0101", "'kong'")+"='kong'";
                          //othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                          if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
                          {
                                othWhereSql="EXISTS(select a0100 "+whereIN+" and "+destTab+".a0100="+userbase+"A01.a0100)";
                          }else{
                                othWhereSql="EXISTS(select a0100 "+whereIN+" where "+destTab+".a0100="+userbase+"A01.a0100)";
                          }
                          update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
                          dao.update(update);   
                          break;
                      }
                      
                 }
                
                // 月汇总时，将dbid及a0000一块添加到q05表中
                KqUtilsClass utils=new KqUtilsClass(this.getFrameconn());
                if (utils.addColumnToKq("q05")) {
                    StringBuffer where = new StringBuffer();
                    where.append(" where nbase='"+userbase+"'");   
                    where.append(" and b0110 like '"+code+"%'");     
                    where.append(" and q03z0 ='"+kq_duration+"'");  
                    where.append(" and a0100 in(select a0100 "+whereIN+")");
                    utils.updateQ05(start_date, where.toString());
                }
                
                //同步q03
                sql.delete(0, sql.length());
                destTab="q03";//目标表xxxxxxxxxxx
                srcTab="q05";//源表
                strJoin=destTab+".A0100="+srcTab+".A0100 and "+destTab+".nbase="+srcTab+".nbase";//关联串  xxx.field_name=yyyy.field_namex,....
                strSet=destTab+".q03z5="+srcTab+".q03z5";//更新串  xxx.field_name=yyyy.field_namex,....
                strDWhere=destTab+".nbase='"+userbase+"' and "+destTab+".Q03Z0 >= '"+start_date+"' and "+destTab+".Q03Z0 <= '"+end_date+"' and "+destTab+".a0100 in(select a0100 "+whereIN+")";//更新目标的表过滤条件
//              strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"' and "+srcTab+".q03z5='01'  and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  
                strSWhere=srcTab+".nbase='"+userbase+"' and "+srcTab+".q03z0='"+kq_duration+"' and "+srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  去掉"+srcTab+".q03z5='01'
                //strSWhere="";
                update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
                othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+")";
                //strJoin=strJoin+" and "+destTab+".nbase='"+userbase+"' and "+destTab+".q03z0='"+kq_duration+"' and "+destTab+".b0110 ='"+code+"'";
                update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
                //System.out.println("---"+update);
                dao.update(update);
            }catch(Exception e)
            {
                isCorrect=false;
                e.printStackTrace();
            }
            return isCorrect;
           
       }
    /**
     * 得到改统计下的所有人员
     * */
    public ArrayList getA0100List(String nbase,String start_date,String end_date,String code,String whereIN)throws GeneralException
    {
        StringBuffer sqlstr= new StringBuffer();
       sqlstr.append("select distinct a0100 from Q03");
       sqlstr.append(" where nbase='"+nbase+"'");
       sqlstr.append(" and Q03Z0 >= '"+start_date+"'");
       sqlstr.append(" and Q03Z0 <= '"+end_date+"'");   
       sqlstr.append(" and e0122 like '"+code+"%'");               
       sqlstr.append(" and a0100 in(select a0100 "+whereIN+")");
       //sqlstr.append(" and Q03Z5 not in ('01','07','08')");
       ContentDAO dao = new ContentDAO(this.getFrameconn());
       ArrayList a0100list=new ArrayList();
       try{
         this.frowset = dao.search(sqlstr.toString());
         while(frowset.next())
         {
            a0100list.add(this.frowset.getString("a0100"));
         }
      }catch(Exception e){
         throw GeneralExceptionHandler.Handle(e); 
      }
      return a0100list;
    }
    /**********对统计过的记录清除纪录*********
     * 
     * @param userbase  数据库前缀
     * @param collectdate  操作时间
     * @param code 部门   
     * @param userbase  数据库前缀
     * @return 是否清除成功
     *
    * *****/
   public boolean delRecord(String userbase,String code,String q03z0,String whereIN){
       boolean iscorrect=false;
       try{    
       ContentDAO dao = new ContentDAO(this.getFrameconn());
       //判断是否已经汇总过
       StringBuffer delete_kq_Sum=new StringBuffer();
       delete_kq_Sum.append("delete from Q05 where");
       delete_kq_Sum.append(" nbase='"+userbase+"'");
       //delete_kq_Sum.append(" and b0110='"+code+"'");
       delete_kq_Sum.append(" and Q03Z0='"+q03z0+"'");
       delete_kq_Sum.append(" and a0100 in(select a0100 from q03"); 
       delete_kq_Sum.append(" where nbase='"+userbase+"'");        
       delete_kq_Sum.append(" and b0110 like '"+code+"%'");        
       if(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)
           delete_kq_Sum.append(" and  EXISTS(select a0100 "+whereIN+" and "+userbase+"A01.a0100=q03.a0100)");
       else
          delete_kq_Sum.append(" and  EXISTS(select a0100 "+whereIN+" where "+userbase+"A01.a0100=q03.a0100)");
       delete_kq_Sum.append(")");      
       ArrayList dellist=new ArrayList();      
        dao.delete(delete_kq_Sum.toString(),dellist);   
         iscorrect=true;
       }catch(Exception e){
           e.printStackTrace();
       }
       return iscorrect;
   }
   
   public static String getWhereSQL(String userbase,String code,String start_date,String end_date,String whereIN,String tablename){
        StringBuffer wheresql=new StringBuffer();
            wheresql.append(" from "+tablename+" ");        
            wheresql.append(" where Q03Z0 >= '"+start_date+"'");
            wheresql.append(" and Q03Z0 <= '"+end_date+"%'");   
            wheresql.append(" and e0122 like '"+code+"%'");
            wheresql.append(" and nbase='"+userbase+"'");
            wheresql.append("and a0100 in(select a0100 "+whereIN+")");
            //wheresql.append(" and Q03Z5 not in ('01','07','08')");
            return wheresql.toString();
       }
   /**
    * 断Q03中那些指标是从A01主集中取得的
    * @param itemtype
    * @param itemid
    * @param itemdesc
    * @return
    */
   public boolean getindexA01(String itemtype,String itemid,String itemdesc)
   {
    boolean field=true;
    itemtype=itemtype.toUpperCase();
    itemid=itemid.toUpperCase();
    ContentDAO dao = new ContentDAO(this.getFrameconn());
    RowSet rs=null;
    String sql="select itemid from fielditem where fieldsetid='A01' and itemid='"+itemid+"' and itemtype='"+itemtype+"' and itemdesc='"+itemdesc+"'";
    try
    {
        rs=dao.search(sql.toString());
        while(rs.next())
        {
            String itemi = rs.getString("itemid");
            if(!"A0101".equals(itemi)&&!"E0122".equals(itemi))
            {
                if(itemi!=null&&itemi.length()>0)
                {
                    field=false;
                }
            }
        }
    }catch(Exception e)
    {
        e.printStackTrace();
    }finally
    {
        if(rs!=null)
        {
            try
            {
                rs.close();
            }catch(SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
    return field;
   }
   /**
      * 判断Q03是否从主集中导入指标
      * destTab=Q03
      * srcTab=Q05
      * @return
      */
     public String getmainsql()
     {
         String selectSQL="";
         StringBuffer sql = new StringBuffer();
         ContentDAO dao = new ContentDAO(this.getFrameconn());
         ArrayList list = new ArrayList();
         sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
         RowSet rowSet=null;
         try
         {
             rowSet=dao.search(sql.toString());
             while(rowSet.next())
             {
                 ArrayList noblist = new ArrayList();
                 String itemid = rowSet.getString("itemid");
                 String itemtype = rowSet.getString("itemtype");
                 String itemdesc = rowSet.getString("itemdesc");
                 noblist.add(itemid);
                 noblist.add(itemtype);
                 noblist.add(itemdesc);
                 list.add(noblist);
             }
             for(int i=0;i<list.size();i++)
             {
                 ArrayList lists=(ArrayList)list.get(i);
                 String nobitemid=(String)lists.get(0);
                 String nobitemtype=(String)lists.get(1);
                 String nobitemdesc=(String)lists.get(2);
                 sql.setLength(0);
                 sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
                 sql.append("and itemid='"+nobitemid+"' and itemtype='"+nobitemtype+"' and itemdesc='"+nobitemdesc+"'");
                 rowSet=dao.search(sql.toString());
                 while(rowSet.next())
                 {
                     String itemi = rowSet.getString("itemid");
                     if(!"A0101".equals(itemi)&&!"A0100".equals(itemi)&&!"B0110".equals(itemi)&&!"E0122".equals(itemi)&&!"E01A1".equals(itemi))
                     {
                         selectSQL+=itemi+",";
                     }
                 }
             }
             if (selectSQL.length() > 0) {
                 selectSQL=selectSQL.substring(0,selectSQL.length()-1);
             }
         }catch(Exception e)
         {
             e.printStackTrace();
         }finally
         {
             if(rowSet!=null)
             {
                 try
                 {
                     rowSet.close();
                 }catch(SQLException e)
                 {
                     e.printStackTrace();
                 }
             }
         }
         return selectSQL;
     }
     public String getmainsql2()
     {
         String selectSQL="";
         StringBuffer sql = new StringBuffer();
         ContentDAO dao = new ContentDAO(this.getFrameconn());
         ArrayList list = new ArrayList();
         sql.append("select itemid,itemtype,itemdesc from fielditem where fieldsetid='A01' and useflag='1'");
         RowSet rowSet=null;
         try
         {
             rowSet=dao.search(sql.toString());
             while(rowSet.next())
             {
                 ArrayList noblist = new ArrayList();
                 String itemid = rowSet.getString("itemid");
                 String itemtype = rowSet.getString("itemtype");
                 String itemdesc = rowSet.getString("itemdesc");
                 noblist.add(itemid);
                 noblist.add(itemtype);
                 noblist.add(itemdesc);
                 list.add(noblist);
             }
             for(int i=0;i<list.size();i++)
             {
                 ArrayList lists=(ArrayList)list.get(i);
                 String nobitemid=(String)lists.get(0);
                 String nobitemtype=(String)lists.get(1);
                 String nobitemdesc=(String)lists.get(2);
                 sql.setLength(0);
                 sql.append("select itemid from t_hr_busifield where fieldsetid='Q03' and useflag='1' ");
                 sql.append("and itemid='"+nobitemid+"' and itemtype='"+nobitemtype+"' and itemdesc='"+nobitemdesc+"'");
                 rowSet=dao.search(sql.toString());
                 while(rowSet.next())
                 {
                     String itemi = rowSet.getString("itemid");
                     if(!"A0101".equals(itemi)&&!"A0100".equals(itemi)&&!"B0110".equals(itemi)&&!"E0122".equals(itemi)&&!"E01A1".equals(itemi))
                     {
                         selectSQL+="MAX("+itemi+"),";
                     }
                 }
             }
             if (selectSQL.length() > 0) {
                 selectSQL=selectSQL.substring(0,selectSQL.length()-1);
             }
         }catch(Exception e)
         {
             e.printStackTrace();
         }finally
         {
             if(rowSet!=null)
             {
                 try
                 {
                     rowSet.close();
                 }catch(SQLException e)
                 {
                     e.printStackTrace();
                 }
             }
         }
         return selectSQL;
     }
     public void synchronizationInitQ05(String nbase,String whereIN,String kq_duration)throws GeneralException
        {
            
             StringBuffer sql=new StringBuffer();
             sql.append("delete from q05 where q03z0='"+kq_duration+"' and nbase='"+nbase+"'");
             sql.append(" and not exists(select * from "+nbase+"A01 where q05.a0100="+nbase+"A01.a0100)");
             
             String destTab="q05";//目标表
             String srcTab=nbase+"A01";//源表
             String strJoin="q05.A0100="+srcTab+".A0100";//关联串  xxx.field_name=yyyy.field_namex,....
             String  strSet="q05.B0110="+srcTab+".B0110`q05.E0122="+srcTab+".E0122`q05.E01A1="+srcTab+".E01A1";//更新串  xxx.field_name=yyyy.field_namex,....
             String strDWhere="q05.nbase='"+nbase+"' and q05.q03z0='"+kq_duration+"'";//更新目标的表过滤条件
             //String strSWhere=srcTab+".a0100 in(select a0100 "+whereIN+")";//源表的过滤条件  
//           String strSWhere="exists (select a0100 "+whereIN+" and q05.a0100="+nbase+"a01.a0100)";//源表的过滤条件  以前
             String strSWhere="";
             if(!userView.isSuper_admin())
             {
                 strSWhere="exists (select a0100 "+whereIN+" and q05.a0100="+nbase+"a01.a0100)";//源表的过滤条件
             }
             String update=Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);    
             String othWhereSql=destTab+".a0100 in(select a0100 "+whereIN+") ";
             update=KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,othWhereSql);
             this.cat.error(sql.toString());
             this.cat.error(update);
             //System.out.println(sql.toString());
             //System.out.println(update);
             ContentDAO dao = new ContentDAO(this.getFrameconn());
            try {   
                dao.delete(sql.toString(), new ArrayList());
                dao.update(update);
                sql.delete(0, sql.length());
                sql.append("delete from q05 where q03z0='"+kq_duration+"' and nbase='"+nbase+"'");
                sql.append(" and exists(select TT.a0100 from (select "+nbase+"A01.a0100 from "+nbase+"A01,q03 where  q03.a0100="+nbase+"A01.a0100 and q03.b0110<>"+nbase+"A01.b0110)TT where TT.a0100=q05.a0100)");
                dao.delete(sql.toString(), new ArrayList());
                //System.out.println(sql.toString());
            } catch (Exception e) {
                e.printStackTrace();
                //throw GeneralExceptionHandler.Handle(e);
            }
        }
}
