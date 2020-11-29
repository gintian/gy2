package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * 刷卡数据
 * <p>Title:KqCardData.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 19, 2006 5:21:12 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class KqCardData {
    private static Category cat = Category.getInstance("com.hjsj.hrms.businessobject.kq.machine.KqCardData");
    private Connection conn;
    private UserView userView;
    private String kq_originality_data="kq_originality_data";
    private String type="";
    private String location="";
    private ArrayList repeatCards = new ArrayList();
    //同步第三方刷卡原因
    private boolean isSyncCardCause = false;
    
   public KqCardData()
   {
       
   }
   public KqCardData(UserView userView,Connection conn)
   {
       this.conn=conn;
       this.userView=userView;
   }
   public KqCardData(UserView userView,Connection conn,String type,String location)
   {
       this.conn=conn;
       this.userView=userView;
       this.type=type;
       this.location=location;
   }
 
   public String getACode()
   {
       String codeSet=RegisterInitInfoData.getKqPrivCode(userView);
       String codeid=RegisterInitInfoData.getKqPrivCodeValue(userView);
       if(this.userView.isSuper_admin())
       {
           codeSet="UN";
           codeid=this.userView.getUserOrgId();
       }else
       {
           if(codeSet==null||codeSet.length()>0)
           {
               codeid=this.userView.getUserOrgId();
               
               if(codeid==null||codeid.length()>0)
               {
                   codeid=this.userView.getUserDeptId();
                   codeSet="UM";  
               }else
               {
                   codeSet="UN";  
               }
           }
       }
       
       return codeSet+codeid;
   }
   /**
    * 刷卡数据 第一次进入 改为得到用户 权限
    * @return
    */
   public String getACodefull()
   {
//     String codeSet=this.userView.getManagePrivCode();
//     String codeid=this.userView.getManagePrivCodeValue();
       String codeSet=RegisterInitInfoData.getKqPrivCode(userView);
       String codeid=RegisterInitInfoData.getKqPrivCodeValue(userView);
       if(this.userView.isSuper_admin())
       {
           codeSet="UN";
           codeid="";//this.userView.getUserOrgId();
       }else
       {
           if(codeSet==null||codeSet.length()>0)
           {
//             codeid=this.userView.getUserOrgId();
//             codeid=this.userView.getManagePrivCodeValue();
               codeid=RegisterInitInfoData.getKqPrivCodeValue(userView);
//             if(codeid==null||codeid.length()>0)
//             {
//                 codeid=this.userView.getUserDeptId();
//                 codeSet="UM";  
//             }else
//             {
//                 codeSet="UN";  
//             }
           }
       }
       
       return codeSet+codeid;
   }
   /**
    * 编辑一个考勤机原始数据的FieldItem集
    * @return
    */
   public ArrayList machineDataFieldlist(boolean isInout_flag)
    {
       ArrayList list =new ArrayList ();
       FieldItem fielditem=new FieldItem();
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("hrms.a0100"));
       fielditem.setItemid("a0100");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("0");
       fielditem.setVisible(false);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
       fielditem.setItemid("nbase");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("@@");
       list.add(fielditem);  
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("b0110.label"));
       fielditem.setItemid("b0110");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("UN");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("e0122.label"));
       fielditem.setItemid("e0122");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("UM");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("label.title.name"));
       fielditem.setItemid("a0101");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("0");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.location"));
       fielditem.setItemid("location");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.machine_no"));
       fielditem.setItemid("machine_no");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
       KqParameter para = new KqParameter(this.userView, "", this.conn);
       String g_no = para.getG_no();
       //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
       fielditem=new FieldItem();
       fielditem.setItemdesc("工号");
       fielditem.setItemid(g_no.toLowerCase());
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("考勤卡号");
       fielditem.setItemid("card_no");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.work_date"));
       fielditem.setItemid("work_date");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.work_time"));
       fielditem.setItemid("work_time");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.status"));
       fielditem.setItemid("status");
       fielditem.setItemtype("A");
       fielditem.setVisible(false);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷原因");
       fielditem.setItemid("oper_cause");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷操作员");
       fielditem.setItemid("oper_user");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷时间");
       fielditem.setItemid("oper_time");
       fielditem.setItemtype("D");
       fielditem.setItemlength(20);
       fielditem.setDisplaywidth(20);
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补卡机器");
       fielditem.setItemid("oper_mach");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       // 判断是否显示 iscommon
       if(this.showIScommon()){
           fielditem=new FieldItem();
           fielditem.setItemdesc("正常出勤点签到");  // 签到点内考勤， 正常出勤点签到
           fielditem.setItemid("iscommon");
           fielditem.setItemtype("A");
           fielditem.setVisible(true);
           fielditem.setCodesetid("0");
           list.add(fielditem);
       }
       fielditem=new FieldItem();
       fielditem.setItemdesc("进出标志");
       fielditem.setItemid("inout_flag");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("审批标志");
       fielditem.setItemid("sp_flag");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("23");
       list.add(fielditem);
       return list;
    }
   
   public ArrayList machineDataFieldlist1(boolean isInout_flag)
    {
       ArrayList list =new ArrayList ();
       FieldItem fielditem=new FieldItem();
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("hrms.a0100"));
       fielditem.setItemid("a0100");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("0");
       fielditem.setVisible(false);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("hrms.nbase"));
       fielditem.setItemid("nbase");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("@@");
       list.add(fielditem);  
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("b0110.label"));
       fielditem.setItemid("b0110");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("UN");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("e0122.label"));
       fielditem.setItemid("e0122");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("UM");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("label.title.name"));
       fielditem.setItemid("a0101");
       fielditem.setItemtype("A");
       fielditem.setCodesetid("0");
       fielditem.setVisible(true);
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.location"));
       fielditem.setItemid("location");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.machine_no"));
       fielditem.setItemid("machine_no");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("考勤卡号");
       fielditem.setItemid("card_no");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.work_date"));
       fielditem.setItemid("work_date");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.work_time"));
       fielditem.setItemid("work_time");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc(ResourceFactory.getProperty("kq.card.status"));
       fielditem.setItemid("status");
       fielditem.setItemtype("A");
       fielditem.setVisible(false);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷原因");
       fielditem.setItemid("oper_cause");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷操作员");
       fielditem.setItemid("oper_user");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补刷时间");
       fielditem.setItemid("oper_time");
       fielditem.setItemtype("D");
       fielditem.setItemlength(20);
       fielditem.setDisplaywidth(20);
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("补卡机器");
       fielditem.setItemid("oper_mach");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("0");
       list.add(fielditem);
    // 判断是否显示 iscommon
       if(this.showIScommon()){
           fielditem=new FieldItem();
           fielditem.setItemdesc("正常出勤点签到");  // 签到点内考勤， 正常出勤点签到
           fielditem.setItemid("iscommon");
           fielditem.setItemtype("A");
           fielditem.setVisible(true);
           fielditem.setCodesetid("0");
           list.add(fielditem);
       }
       fielditem=new FieldItem();
       fielditem.setItemdesc("进出标志");
       fielditem.setItemid("inout_flag");
       fielditem.setItemtype("A");
//     if(isInout_flag)
         fielditem.setVisible(true);
//     else
//         fielditem.setVisible(false); 
       fielditem.setCodesetid("0");
       list.add(fielditem);
       fielditem=new FieldItem();
       fielditem.setItemdesc("审批标志");
       fielditem.setItemid("sp_flag");
       fielditem.setItemtype("A");
       fielditem.setVisible(true);
       fielditem.setCodesetid("23");
       list.add(fielditem);
       return list;
    }
   
   public String getSQL(ArrayList kq_nbase_list,String a_code,String cur_date,ArrayList datelist)
   {
       String column=getKq_originality_column();
       StringBuffer sqlstr=new StringBuffer();
       CommonData vo=(CommonData)datelist.get(0);
       String start_date=vo.getDataValue();
       vo=(CommonData)datelist.get(datelist.size()-1);
       String end_date=vo.getDataValue();
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           sqlstr.append("select "+column+" ");
           sqlstr.append(" from "+this.kq_originality_data);
           sqlstr.append(" where 1=1 and ");           
           sqlstr.append(getCodeItemWhere(a_code,nbase));
           if(cur_date!=null&&!"all".equals(cur_date))
           {
               sqlstr.append(" and work_date='"+cur_date+"'");
           }else
           {
               sqlstr.append(" and work_date>='"+start_date+"'");
               sqlstr.append(" and work_date<='"+end_date+"'");
           }
           
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");         
           sqlstr.append(" UNION ");
       }
       sqlstr.setLength(sqlstr.length()-7); 
       return sqlstr.toString();       
   }    
   public String getSQL(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c)
   {
       String column=getKq_originality_column();
       StringBuffer sqlstr=new StringBuffer();
       if(start_date!=null&&start_date.length()>0) {
           start_date=start_date.replaceAll("-","\\.");
       }
       if(end_date!=null&&end_date.length()>0) {
           end_date=end_date.replaceAll("-","\\.");
       }
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           sqlstr.append("select "+i+" as i,"+column+" ");
           sqlstr.append(" from "+this.kq_originality_data);
           sqlstr.append(" where  nbase='"+nbase+"'");  
           if(a_code!=null&&a_code.length()>2) {
               sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           }
           //修改 不等于当天时间
           if(!end_date.equals(start_date))
           {
               sqlstr.append(" and ((work_date='"+start_date+"'");
               sqlstr.append(" and work_time>='"+start_time+"')");
               sqlstr.append(" or (work_date='"+end_date+"'");
               sqlstr.append(" and work_time<='"+end_time+"')");
               sqlstr.append(" or (work_date>'"+start_date+"' and work_date<'"+end_date+"'))");
           }else
           {
               sqlstr.append(" and work_date>='"+start_date+"'");
               sqlstr.append(" and work_time>='"+start_time+"'");
               sqlstr.append(" and work_date<='"+end_date+"'");
               sqlstr.append(" and work_time<='"+end_time+"'"); 
           }
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           if(!this.userView.isSuper_admin())
           {
               if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and "+this.kq_originality_data+".a0100="+nbase+"A01.a0100)");
               } else {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where "+this.kq_originality_data+".a0100="+nbase+"A01.a0100)");
               }
            
           }
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+") ");        
           sqlstr.append(" UNION ");
       }
       sqlstr.setLength(sqlstr.length()-7); 
       return sqlstr.toString();       
   }
   
   private String getUserAccount() {
       String account = "";
       
       String nbase = this.userView.getDbname();
       String a0100 = this.userView.getA0100();
       if (null == nbase || "".equals(nbase) || null == a0100 || "".equals(a0100)) {
           return account;
       }
       
       StringBuffer sb1 = new StringBuffer();       
       ContentDAO dao = new ContentDAO(this.conn);
        
       RowSet rs = null;
       try {
           DbNameBo dbbo = new DbNameBo(this.conn);
           String username = dbbo.getLogonUserNameField().toUpperCase();
           sb1.append("select " + username);
           sb1.append(" from " + this.userView.getDbname() + "A01");
           sb1.append(" where A0100='" + this.userView.getA0100() + "'");
           
           rs = dao.search(sb1.toString());
           if(rs.next()) {
               account = rs.getString(1);
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }finally{
           KqUtilsClass.closeDBResource(rs);
       }
       
       return account;
   }
   
   public String getSQL1(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c,String view)
   {
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        g_no = checkGnoFld(g_no);
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        //判断用户是否是考勤员角色，如果是考勤员角色就走考勤员权限
        String dbname = userView.getDbname();
        String a0100 = userView.getA0100();
        String ischecker = "0";
        ischecker = RegisterInitInfoData.ischecker(dbname, a0100, conn);
        
        boolean isSuper = this.userView.isSuper_admin();
        String account = getUserAccount();
        
       String columnjoin="Q.a0100,Q.nbase,a0101,b0110,e0122,e01a1,location,machine_no,card_no,work_date,work_time,status,inout_flag,oper_cause,oper_user,";
       if(kq_nbase_list.size()>0 && !"".equals(g_no)) {
           columnjoin = columnjoin + g_no + ",";
       }
       
       if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
           columnjoin += Sql_switcher.dateToChar("oper_time", "yyyy-MM-dd HH24:mi");
       } else if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
           columnjoin += "convert(varchar(16),oper_time,20)";
       }
       
       columnjoin += " oper_time,oper_mach,sp_flag,datafrom,curr_user";
       if(showIScommon()){ // 判断是否存在字段 iscommon
           columnjoin += ",iscommon ";
       }
       
       String column = getKq_originality_column2();
       StringBuffer sqlstr = new StringBuffer();
       
       if(start_date!=null && start_date.length()>0) {
           start_date = start_date.replaceAll("-","\\.");
       }
       
       if(end_date!=null && end_date.length()>0) {
           end_date = end_date.replaceAll("-","\\.");
       }
       
       StringBuffer joinTable = new StringBuffer();
       StringBuffer joinTable2 = new StringBuffer();
       for(int i = 0; i < kq_nbase_list.size(); i++){
           String nbase = kq_nbase_list.get(i).toString();
           String whereIN = RegisterInitInfoData.getWhereINSql(userView,nbase);
           String tCodeItemWhere = getCodeItemWhere(a_code,nbase);
           if(whereIN!=null && (whereIN.indexOf("WHERE")!=-1 || whereIN.indexOf("where")!=-1)) {
               whereIN += " AND " + tCodeItemWhere;
           } else {
               whereIN += " WHERE " + tCodeItemWhere;
           }
           
           if(joinTable.length() > 0){
               joinTable.append(" UNION ");
               joinTable2.append(" UNION ");
           }
               
           joinTable.append("SELECT '" + i + "' i,A0100,'" + nbase + "' nbase");
           if(!"".equals(g_no)) {
               joinTable.append("," + g_no);
           }
           joinTable.append(whereIN);
           
           joinTable2.append("SELECT '" + i + "' i,A0100,'" + nbase + "' nbase");
           if(!"".equals(g_no)) {
               joinTable2.append("," + g_no);
           }
           joinTable2.append(" from " + nbase + "A01");
       }
       
       if(kq_nbase_list.size()>0) {
           sqlstr.append("select i,"+column+" from ");
       } else {
           sqlstr.append("select "+column+" from ");
       }
       
       if((isSuper || "1".equals(ischecker)) && !"1".equals(view)){
           sqlstr.append("(SELECT i," + columnjoin + " FROM " + this.kq_originality_data + " Q INNER JOIN (" + joinTable + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase " +
                   "and Q.sp_flag <> '01'");
       }else{
           if(joinTable.length()>0){
               sqlstr.append("(SELECT i," + columnjoin + " FROM " + this.kq_originality_data + " Q INNER JOIN (" + joinTable + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase ");
               sqlstr.append(" and Q.sp_flag <> '01' and ( Q.curr_user is null or Q.curr_user = 'null' or Q.sp_flag='03')");
           }else{
               sqlstr.append("(SELECT " + columnjoin + " FROM " + this.kq_originality_data + " Q where ");
               sqlstr.append(" Q.sp_flag <> '01' and ( Q.curr_user is null or Q.curr_user = 'null' or Q.sp_flag='03')");
           }
           
           if(account != null && !"".equals(account)){
               sqlstr.append(" and " + Sql_switcher.isnull("Q.curr_user", "'" + account + "1'") + "<>'" + account + "'");
               sqlstr.append(" union all "+"SELECT i," + columnjoin + " FROM " + this.kq_originality_data + " Q INNER JOIN (" + joinTable2 + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase ");
               sqlstr.append("and Q.curr_user ='"+account+"'" );
           }
       }
       sqlstr.append(") B");
       sqlstr.append(" WHERE ");
       //修改 不等于当天时间
       if(!end_date.equals(start_date))
       {
           sqlstr.append(" ((work_date='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"')");
           sqlstr.append(" or (work_date='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"')");
           sqlstr.append(" or (work_date>'"+start_date+"' and work_date<'"+end_date+"'))");
       }else
       {
           sqlstr.append(" work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'"); 
       }
       if(where_c != null && where_c.length() > 0){
           sqlstr.append(where_c);
       }  
       return sqlstr.toString();   
   }
   
   public String getNoCardSQL(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c,String kq_type)
   {
       StringBuffer sqlstr=new StringBuffer();
       if(start_date!=null&&start_date.length()>0) {
           start_date=start_date.replaceAll("-","\\.");
       }
       if(end_date!=null&&end_date.length()>0) {
           end_date=end_date.replaceAll("-","\\.");
       }
       String z1 =start_date+" "+start_time;
       String z3=end_date+" "+end_time;
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           String where_code=getCodeItemWhere(a_code,nbase);
           sqlstr.append("select "+i+" as i,'"+nbase+"' as nbase,a0100,a0101,b0110,e0122,e01a1,a0000 ");
           sqlstr.append(" from ");
           sqlstr.append(nbase+"A01 where 1=1 and ");
           sqlstr.append(where_code);
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           sqlstr.append(" and "+kq_type+"='02'");
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");
           sqlstr.append(" and NOT EXISTS (");
           sqlstr.append("select 1 ");
           sqlstr.append(" from "+this.kq_originality_data);
           sqlstr.append(" where "+this.kq_originality_data+".a0100="+nbase+"A01.a0100 and ");         
           sqlstr.append(where_code);
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");  
           sqlstr.append(")");
           /***公出没有刷卡的***/
           sqlstr.append(" and NOT EXISTS(");
           sqlstr.append(" select 1 from q13 where q13.a0100="+nbase+"A01.a0100 and ");
           sqlstr.append(where_code);
           sqlstr.append(" and ((q13z1>="+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q13z1<="+Sql_switcher.dateValue(z3)+")");    
           sqlstr.append(" or (q13z3>"+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q13z3<"+Sql_switcher.dateValue(z3)+")"); 
           sqlstr.append(" or (q13z1<="+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q13z3>="+Sql_switcher.dateValue(z3)+")");
           sqlstr.append(")");
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");  
           sqlstr.append(")");
           /***请假没有刷卡的***/
           sqlstr.append(" and NOT EXISTS(");
           sqlstr.append(" select 1 from q15 where q15.a0100="+nbase+"A01.a0100 and ");
           sqlstr.append(where_code);
           sqlstr.append(" and ((q15z1>="+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q15z1<="+Sql_switcher.dateValue(z3)+")");    
           sqlstr.append(" or (q15z3>"+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q15z3<"+Sql_switcher.dateValue(z3)+")"); 
           sqlstr.append(" or (q15z1<="+Sql_switcher.dateValue(z1));
           sqlstr.append(" and q15z3>="+Sql_switcher.dateValue(z3)+")");
           sqlstr.append(")");
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");           
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+") ");            
           sqlstr.append(")");
           sqlstr.append(" UNION ");
       }
       sqlstr.setLength(sqlstr.length()-7); 
       return sqlstr.toString();       
   }
   public String getSQLWhere(ArrayList kq_nbase_list,String a_code,String cur_date,ArrayList datelist)
   {
       
       StringBuffer sqlstr=new StringBuffer();
       sqlstr.append(" from "+this.kq_originality_data);
       sqlstr.append(" where 1=1 ");           
       
       if(cur_date!=null&&!"all".equals(cur_date))
       {
           sqlstr.append(" and work_date='"+cur_date+"'");
       }else
       {
           String start_date=datelist.get(0).toString();
           String end_date=datelist.get(datelist.size()-1).toString();
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
       }
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           if(i>0)
           {
               sqlstr.append(" or ");  
           }else
           {
                sqlstr.append(" and ( ");   
           }
           sqlstr.append(" (UPPER(nbase)='"+nbase.toUpperCase()+"'");
           sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           sqlstr.append(" and a0100 in(select "+nbase+"A01.a0100 "+whereIN+")) "); 
           if(i==kq_nbase_list.size()-1) {
               sqlstr.append(")");
           }
       }       
       return sqlstr.toString();
       
   }
   public String getSQLWhere(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_in,String select_pre,String into_flag,String iscommon,String sp_flag,String datafrom)
   {
       
       StringBuffer sqlstr=new StringBuffer();
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        g_no = g_no.toLowerCase();
//      String cardno = (String) hashmap.get("cardno");
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        String column = getKq_originality_column2();
        String ischecker = "0";
        ischecker = RegisterInitInfoData.ischecker(this.userView.getDbname(), this.userView.getA0100(), this.conn);
        boolean isSuper = this.userView.isSuper_admin();
        String account = "";
        if(!isSuper){
            String a01001 = this.userView.getA0100();
            String nbase1 = this.userView.getDbname();
            account = getUserAccount(a01001,nbase1);
        }
       String columnjoin="Q.a0100,Q.nbase,a0101,b0110,e0122,e01a1,location,machine_no,card_no,work_date,work_time,status,inout_flag,oper_cause,oper_user,";
       if (column.indexOf(g_no) != -1)
       {
           columnjoin+=g_no+",";
       }
       if (column.indexOf("iscommon") != -1)
    {
           columnjoin+="iscommon,";
    }
       if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
           columnjoin+=Sql_switcher.dateToChar("oper_time", "yyyy-MM-dd HH24:mi");
       } else if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
           columnjoin+="convert(varchar(16),oper_time,20)";
       }
       columnjoin+=" oper_time,oper_mach,sp_flag,datafrom,curr_user";
       if (null != select_pre && select_pre.length() > 0) {
           if(!"all".equalsIgnoreCase(select_pre)){
               kq_nbase_list.clear();
               kq_nbase_list.add(select_pre);
           }
       }
       StringBuffer joinTable = new StringBuffer();
       for(int i=0;i<kq_nbase_list.size();i++){
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           String tCodeItemWhere = getCodeItemWhere(a_code,nbase);
           if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
               whereIN += " AND " + tCodeItemWhere;
           } else {
               whereIN += " WHERE " + tCodeItemWhere;
           }
           
           if(joinTable.length() > 0){
               if (column.indexOf(g_no) != -1)
               {
                   joinTable.append(" UNION SELECT '" + i + "' i,A0100,'" + nbase + "' nbase," + g_no + whereIN);
               }else {
                   joinTable.append(" UNION SELECT '" + i + "' i,A0100,'" + nbase + "' nbase " + whereIN);
            }
           }else{
               if (column.indexOf(g_no) != -1)
               {
                   joinTable.append(" SELECT '" + i + "' i,A0100,'" + nbase + "' nbase," + g_no + whereIN);
               }else {
                   joinTable.append(" SELECT '" + i + "' i,A0100,'" + nbase + "' nbase " + whereIN);
            }          }
       }
       sqlstr.append("FROM (SELECT i," + columnjoin + " FROM " + this.kq_originality_data + " Q INNER JOIN (" + joinTable + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase AND Q.sp_flag <> '01'" );
       if(isSuper || "1".equals(ischecker) || "".equals(account) || null == account){
           sqlstr.append(")  B where ");
       }else{
           sqlstr.append(" AND ( Q.curr_user is null or Q.curr_user = 'null' or Q.curr_user = '" + account + "')) B where ");
       }
       //修改 不等于当天时间
       if(!end_date.equals(start_date))
       {
           sqlstr.append(" ((work_date='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"')");
           sqlstr.append(" or (work_date='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"')");
           sqlstr.append(" or (work_date>'"+start_date+"' and work_date<'"+end_date+"'))");
       }else
       {
           sqlstr.append(" work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'"); 
       }
       
       //增加 =1 只导出补刷卡数据
       if(datafrom!=null && datafrom.length()>0 && datafrom.startsWith("1")) {
           sqlstr.append(" and datafrom='1'");
           
           if ("11".equals(datafrom)) 
           {
               sqlstr.append(" and (location like '%补刷%' or location like '%补签%')");
           }
       }
       
       sqlstr.append(" " + where_in);
       if(!"all".equalsIgnoreCase(into_flag)) {
           sqlstr.append(" and inout_flag = '"+into_flag+"'");
       }
       if(!"all".equalsIgnoreCase(iscommon)&&showIScommon()) {
           sqlstr.append(" and iscommon = '"+iscommon+"'");
       }
       if(!"all".equalsIgnoreCase(sp_flag)) {
           sqlstr.append(" and sp_flag = '"+sp_flag+"'");
       }
       return sqlstr.toString();
       
   }
   
   private String checkGnoFld(String g_no) {
       DbWizard db = new DbWizard(this.conn);
       if(g_no == null || (!"".equals(g_no) && !db.isExistField("UsrA01", g_no, false))) {
           g_no = "";
       }
       
       return g_no;
   }
   /**
    * 得到考勤机原始数据的columns
    * @return
    */
   public String getKq_originality_column()
   {
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        g_no = g_no.toLowerCase();
        g_no = checkGnoFld(g_no);
        
//      String cardno = (String) hashmap.get("cardno");
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
       String column="a0100,nbase,a0101,b0110,e0122,e01a1,location,machine_no,";
       if(!"".equals(g_no)) {
           column = column + g_no + ",";
       }
       column = column + "card_no,work_date,work_time,status,inout_flag,oper_cause,oper_user,";
       
       if(Sql_switcher.searchDbServer() == Constant.ORACEL) {
           column += Sql_switcher.dateToChar("oper_time", "yyyy-MM-dd HH24:mi");
       } else if(Sql_switcher.searchDbServer() == Constant.MSSQL) {
           column += "convert(varchar(16),oper_time,20)";
       }
       
       column += " oper_time,oper_mach,sp_flag";
       return column;
   }
   
   public String getKq_originality_column2()
   {
       KqParameter para = new KqParameter(this.userView, "", this.conn);
       String g_no = para.getG_no();
       g_no = checkGnoFld(g_no.toLowerCase());
       
       String column = "a0100,nbase,a0101,b0110,e0122,location,machine_no,";
       
       ArrayList kq_db_list = this.userView.getPrivDbList();
       if(kq_db_list.size()>0 && !"".equals(g_no)) {
           column = column + g_no + ",";
       }
       
       column = column + "card_no,work_date,work_time,status,inout_flag,oper_cause,oper_user,oper_time,oper_mach,sp_flag";
       if(showIScommon()){ // 判断是否存在字段 iscommon
           column+=",iscommon";
       }

       return column;
   }
   
   public String getCodeItemWhere(String a_code,String nbase)
    {
        String where="";
        String org_str="";
        if(a_code!=null&&a_code.length()>0)
        {
            String codesetid=a_code.substring(0,2);
            if("UN".equalsIgnoreCase(codesetid))
            {
                org_str="b0110";
            }else if("UM".equalsIgnoreCase(codesetid))
            {
                org_str="e0122";
            }else if("@K".equalsIgnoreCase(codesetid))
            {
                org_str="e01a1";
            }else if("GP".equalsIgnoreCase(codesetid))
            {
                org_str="a0100";
            }else if("EP".equalsIgnoreCase(codesetid))
            {
                org_str="a0100";
            }
            if(a_code.length()>=3)
            {
                String codeitemid=a_code.substring(2);              
                if(codeitemid!=null&&codeitemid.length()>0)
                {
                    if("GP".equalsIgnoreCase(codesetid))
                    {
                        where=getEmploys_Group(codeitemid,nbase);
                    }else if("@K".equalsIgnoreCase(codesetid))
                    {
                        where=getEmploys_e01a1(codeitemid,nbase);
                    }else if("EP".equalsIgnoreCase(codesetid))
                    {
                        where = "a0100 = '" + codeitemid + "'";
                    }else 
                    {
                        where=org_str+" like '"+codeitemid+"%'";
                    }
                    
                }
            }else
            {
                where=org_str+" like '%'";
            }
            
        }else {
            return "1=1";
        }
        return where;
    }
   
   /**
    * 得到一个组的员工编号
    * @param parentid
    * @return
    */
   private String getEmploys_Group(String group_id,String nbase)
   {
     StringBuffer strsql=new StringBuffer();   
      strsql.append("a0100 in (");
      strsql.append("select a0100 from kq_group_emp");
      strsql.append(" where group_id='"+group_id+"' and UPPER(nbase)='"+nbase.toUpperCase()+"')");
    
     return strsql.toString();
   }
   /**
    * 得到一个职位的一个员工编号
    * @param parentid
    * @return
    */
   private String getEmploys_e01a1(String e0122,String nbase)
   {
     StringBuffer strsql=new StringBuffer();   
     strsql.append("a0100 in (");
     strsql.append("select a0100 from "+nbase+"A01");
     strsql.append(" where e01a1='"+e0122+"')");    
     return strsql.toString();
   }
   /**
    * 考勤日期的下拉菜单
    * @param datelist 当前考勤期间的所有日期 
    *        CommonData 
    *        getDataValue  值
    *        getDataName  显示
    * @param  registerdate 当前日期    
    * @return 
    *        select的html代码   
    */
   public  String getDateSelectHtml(ArrayList datelist,String registerdate)
   {
    StringBuffer selecthtml= new StringBuffer();
    selecthtml.append("<select name='cur_date' size='1' onchange='javascript:change(2)'>");
    if(registerdate==null||registerdate.length()<=0)
        {
        CommonData vo=(CommonData)datelist.get(0);
            registerdate=vo.getDataValue();
        }
    String rest_state=ResourceFactory.getProperty("kq.date.work");
    selecthtml.append("<option value='all'>");
    selecthtml.append("<"+ResourceFactory.getProperty("kq.machine.message.all")+">");
    selecthtml.append("</option>");
    for(int i=0;i<datelist.size();i++)
    {
        CommonData vo=(CommonData)datelist.get(i);
        String style="";            
        if(vo.getDataName().indexOf(rest_state)!=-1)
        {
            style="style='COLOR: #000000'";
        }else
        {
            style="style='COLOR: #FF0000'";
        }
        if(registerdate.equals(vo.getDataValue().trim()))
        {
            selecthtml.append("<option value="+vo.getDataValue()+" "+style+" selected='selected'>");
        }else
        {
            selecthtml.append("<option value="+vo.getDataValue()+" "+style+">");
        }
        selecthtml.append(vo.getDataName());
        selecthtml.append("</option>");
    }
    selecthtml.append("</select> ");
    return selecthtml.toString();
   }
  
   /**
     * 返回一条记录的list
     * @param line
     * @param hashM
     * @param machine_no
     * @return 顺序是:a0100,机器号,卡号,日期,时间
     * 
     */
    public ArrayList getFileValue(String  line,HashMap hashM,String machine_no)throws GeneralException 
    {
        ArrayList list = new ArrayList();
        if(line==null || line.length()<=0) {
            return list;
        }
        
        if(hashM == null) {
            return list;
        }
        
        String s_str = "";
        String e_str = "";
        int s_int = 0;
        int e_int = 0;
        String str = "";
        SimpleDateFormat myFmt1 = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat myFmt2 = new SimpleDateFormat("HH:mm");
        
        try
        {
            /*****机器号******/
            list.add("A");
            list.add("A");
            
            String status = (String)hashM.get("status");
            if(status != null && "1".equals(status))
            {
                list.add(machine_no);
            }else
            {
                s_str = (String)hashM.get("machine_s");
                e_str = (String)hashM.get("machine_e");
                s_int = Integer.parseInt(s_str)-1;      
                e_int = Integer.parseInt(e_str);
                str = line.substring(s_int,e_int);
                str = str != null && str.length() > 0 ? str.trim() :"";
                list.add(str);
            }
            
            /******卡号*******/
            s_str = (String) hashM.get("card_s");
            e_str = (String) hashM.get("card_e");
            s_int = Integer.parseInt(s_str) - 1;
            e_int = Integer.parseInt(e_str);
            str = line.substring(s_int, e_int);
            str = str != null && str.length() > 0 ? str.trim() : "";
            list.add(str);

            /*******日期*****/
            String date = "";
            s_str = (String) hashM.get("year_s");
            e_str = (String) hashM.get("year_e");
            s_int = Integer.parseInt(s_str) - 1;
            e_int = Integer.parseInt(e_str);
            str = line.substring(s_int, e_int);
            str = str != null && str.length() > 0 ? str.trim() : "";
            date = str;
            
            s_str = (String) hashM.get("md_s");
            e_str = (String) hashM.get("md_e");
            s_int = Integer.parseInt(s_str) - 1;
            e_int = Integer.parseInt(e_str);
            str = line.substring(s_int, e_int);
            str = str != null && str.length() > 0 ? str.trim() : "";
            if (str.length() > 4 && str.indexOf(".") != -1) {
                date = date + "." + str.substring(0, 2) + "." + str.substring(3, 5);
            } else {
                date = date + "." + str.substring(0, 2) + "." + str.substring(2, 4);
            }
            myFmt1.parse(date);
            list.add(date);

            /*******时间********/
            s_str = (String) hashM.get("hm_s");
            e_str = (String) hashM.get("hm_e");
            s_int = Integer.parseInt(s_str) - 1;
            e_int = Integer.parseInt(e_str);
            str = line.substring(s_int, e_int);
            str = str != null && str.length() > 0 ? str.trim() : "";
            String tiem = "";
            if (str.length() > 4 && str.indexOf(":") != -1) {
                tiem = str.substring(0, 2) + ":" + str.substring(3, 5);
            } else {
                tiem = str.substring(0, 2) + ":" + str.substring(2, 4);
            }
            myFmt2.parse(tiem);
            list.add(tiem);
        }catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }       
        
        return list;
    }
    /**
     * 向考勤刷卡原始表添加纪录
     * @param card_list
     * @param dblist
     * @param cardno_field
     * @throws GeneralException
     */
    public boolean  insert_kq_originality_data(ArrayList card_list,ArrayList dblist,String cardno_field)throws GeneralException 
    {
         if(cardno_field==null||cardno_field.length()<=0) {
             return false;
         }
         
         boolean isCorrect = false;
         String originality_Tab = "kq_originality_data"; 
         String temp_Tab = createTemp(originality_Tab);
         
         isCorrect = inser_kq_originality_data_temp(card_list,temp_Tab);
         if(!isCorrect) {
             return false;
         }
         
         isCorrect = upTemp_originality_emp_data(dblist,cardno_field,temp_Tab);
         if(!isCorrect) {
             return false;
         }
         
         if(checkCardIFRepeat(temp_Tab))
         {
             isCorrect = upTemp_originality_loaction_data(temp_Tab,"kq_machine_location");   
             if(!isCorrect) {
                 return false;
             }
             
             isCorrect = pickUPTemp(originality_Tab,temp_Tab); 
             if(!isCorrect) {
                 return false;
             }
         }else
         {
             isCorrect=false;
         }
         KqUtilsClass.dropTable(conn, temp_Tab);   
         return isCorrect;
    }
    
    /**
     * 向考勤刷卡原始表添加纪录
     * @param card_list
     * @param dblist
     * @param cardno_field
     * @throws GeneralException
     */
    public boolean  insert_Sync_kq_originality_data(ArrayList card_list,ArrayList dblist,String cardno_field)throws GeneralException 
    {
         if(cardno_field==null||cardno_field.length()<=0) {
             return false;
         }
         boolean isCorrect=false;
         String originality_Tab="kq_originality_data"; 
         String temp_Tab=createTemp(originality_Tab);
         //System.out.println("创建临时表成功表名为+"+originality_Tab);
         cat.error("创建临时表成功表名为+"+originality_Tab);          
         cat.debug("创建临时表成功表名为+"+originality_Tab);  
         isCorrect=inser_kq_originality_data_temp(card_list,temp_Tab);
         if(!isCorrect) {
             return false;
         }
         isCorrect=upTemp_originality_emp_data(dblist,cardno_field,temp_Tab);
         if(!isCorrect) {
             return false;
         }
         if(checkCardIFRepeat(temp_Tab))
         {
             isCorrect=upTemp_originality_loaction_data(temp_Tab,"kq_machine_location");   
             if(!isCorrect) {
                 return false;
             }
             isCorrect=pickUPTemp(originality_Tab,temp_Tab); 
             if(!isCorrect) {
                 return false;
             }
         }else
         {
             isCorrect=false;
         }
         KqUtilsClass.dropTable(conn, temp_Tab);   
        //System.out.println("返回同步结果+"+isCorrect);
         cat.error("返回同步结果+"+isCorrect);            
         cat.debug("返回同步结果+"+isCorrect);
         return isCorrect;
    }
    
    /**
     * 向考勤刷卡原始表添加纪录
     * @param card_list
     * @param dblist
     * @param cardno_field
     * @throws GeneralException
     */
    public boolean  insert_Sync_kq_originality_data2(ArrayList dblist,String cardno_field, String temp_Tab)throws GeneralException 
    {
        if(cardno_field==null || cardno_field.length()<=0) {
            return false;
        }
         
        boolean isCorrect = false;
        String originality_Tab = "kq_originality_data"; 
        
        isCorrect = upTemp_originality_emp_data(dblist,cardno_field,temp_Tab);
        if(!isCorrect) {
            return false;
        }
         
        if(checkCardIFRepeat(temp_Tab))
        {
            isCorrect = upTemp_originality_loaction_data(temp_Tab,"kq_machine_location");   
            if(!isCorrect) {
                return false;
            }
            
            isCorrect = pickUPTemp(originality_Tab,temp_Tab); 
            if(!isCorrect) {
                return false;
            }
        }else
        {
            isCorrect=false;
        }
        //KqUtilsClass.dropTable(conn, temp_Tab);
        
        System.out.println("返回同步结果+"+isCorrect);
        cat.error("返回同步结果+"+isCorrect);         
        cat.debug("返回同步结果+"+isCorrect);
        
        return isCorrect;
    }
    
    /**
     * 刷卡数据同步时得到检查重复卡号的sql语句
     * @Title: getRepeatCardSQL   
     * @Description:    
     * @param dblist 考勤人员库列表
     * @param cardNOField 主集中考勤卡号指标
     * @param tempTab 同步刷卡数据时的临时表（其中包括卡号、刷卡日期、刷卡时间等）
     * @return
     */
    private String getRepeatCardSQL(ArrayList dblist, String cardNOField, String tempTab) {
        if (null == dblist || 0 >= dblist.size() 
                || null == cardNOField || "".equals(cardNOField.trim())
                || null == tempTab || "".equals(tempTab)
             ) {
            return "";
        }
        
        /* 查找重复卡号示例
        select C01VF from
        (select C01VF from UsrA01 where C01VF is not null 
         union all 
        select C01VF from RetA01 where C01VF is not null          
         ) card
        group by C01VF having count(C01VF)>1
        */
        
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT card_no FROM ").append(tempTab);
        sql.append(" WHERE card_no IN (");
        sql.append("SELECT ").append(cardNOField).append(" FROM (");
        for (int i = 0; i < dblist.size(); i++) {
            String nbase = (String)dblist.get(i);
            sql.append(" SELECT ").append(cardNOField);
            sql.append(" FROM ").append(nbase).append("A01");
            sql.append(" WHERE ").append(cardNOField).append(" IS NOT NULL");
            if (Constant.MSSQL == Sql_switcher.searchDbServer()) {
                sql.append(" AND ").append(cardNOField).append("<>''");
            }
            
            if (i < dblist.size() - 1) {
                sql.append(" UNION ALL ");
            }
        }
        sql.append(") A ");
        sql.append(" GROUP BY ").append(cardNOField);
        sql.append(" HAVING COUNT(").append(cardNOField).append(")>1");
        sql.append(")");
        
        return sql.toString();
    }
    
    public boolean checkRepeatCards(ArrayList dblist, String cardNOField, String tempTab) {
        boolean repeated = false;
        
        repeatCards.clear();
        
        String sql = getRepeatCardSQL(dblist, cardNOField, tempTab);
        if (null == sql || "".equals(sql)) {
            return repeated;
        }
        
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            while (rs.next()) {
                repeatCards.add(rs.getString("card_no"));
            }
            
            repeated = 0 < repeatCards.size();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return repeated;
    }
    
    /**
     * 判断是否有人重复用卡
     * @param temp_Tab
     */
    private boolean checkCardIFRepeat(String temp_Tab) throws GeneralException 
    {
        String sql = "select card_no from (SELECT DISTINCT card_no,a0100 from " + temp_Tab + ") A group by card_no having count(card_no)>1";
        cat.error("检查卡号重复="+sql);           
        cat.debug("检查卡号重复="+sql);
        
        //是否有重复卡号，默认没有
        boolean hasRepeatCard = false;
        RowSet rs = null;
        try
        {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql);
            hasRepeatCard = rs.next();
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        
        if(hasRepeatCard)
        {
            System.out.println("考勤人员重复分配了同一卡号，请检查=");
            cat.error("考勤人员重复分配了同一卡号，请检查");         
            cat.debug("考勤人员重复分配了同一卡号，请检查=");
            throw new GeneralException("","考勤人员重复分配了同一卡号，请检查！","","");
        }
            
        //返回检查结果   通过（无重复卡号），不通过（有重复卡号）
        return !hasRepeatCard;
    }
    /**
     * 从临时表中记录原始刷卡信息
     * @param DestTab
     * @param SrcTab
     */
    public boolean pickUPTemp(String DestTab,String SrcTab)
    {
        boolean isCorrect=false;
        
        ContentDAO dao = new ContentDAO(this.conn);  
        
        StringBuffer sql = new StringBuffer();      
        sql.append("INSERT INTO "+DestTab);
        sql.append(" (A0100,nbase,A0101,B0110,E0122,E01A1,location,machine_no,card_no,work_date,work_time,");
        if(this.isSyncCardCause()) {
            sql.append("oper_cause,");
        }
        sql.append("sp_flag,status,inout_flag)");
        sql.append(" SELECT DISTINCT A0100,nbase,A0101,B0110,E0122,E01A1,");
        sql.append("MIN(location),MIN(machine_no),MIN(card_no),work_date,work_time,");
        if(this.isSyncCardCause()) {
            sql.append("MIN(oper_cause),");
        }
        sql.append("'03',MIN(status),MIN(inout_flag)");
        sql.append(" FROM "+SrcTab+" a");
        sql.append(" where");
        sql.append(" "+Sql_switcher.isnull("a.nbase", "'##'")+"<>'##'");
        sql.append(" and a.nbase<>'A'");
        sql.append(" and NOT EXISTS(SELECT 1 FROM kq_originality_data b");
        sql.append(" WHERE a.nbase=b.nbase AND a.A0100=b.A0100");
        sql.append(" AND a.work_date=b.work_date AND a.work_time=b.work_time)");
        sql.append(" GROUP BY nbase,A0100,work_date,work_time,A0101,B0110,E0122,E01A1");
        try
        {
            //System.out.println("写入原始表"+sql.toString());
            cat.error("开始写入原始表"+sql.toString());            
            cat.debug("开始写入原始表"+sql.toString());
            dao.update(sql.toString());         
            //dropTable(SrcTab);    
            isCorrect=true;
        }catch(Exception e)
        {
            e.printStackTrace();
            //System.out.println("写入原始表出错"+sql.toString());
            cat.error("写入原始表出错"+sql.toString());            
            cat.debug("写入原始表出错"+sql.toString());
        }
        return isCorrect;   
        
    }   
    /**
     * 添加员工原始刷卡信息到临时表
     * @param list二维的
     * list的顺序是:机器号,卡号,日期,时间
     */
    public boolean inser_kq_originality_data_temp(ArrayList list,String table_name)throws GeneralException 
    {
        StringBuffer sql=new StringBuffer();
        sql.append("insert into "+table_name+" ");
        sql.append("(a0100,nbase,machine_no,card_no,work_date,work_time,");
        if(this.isSyncCardCause()) {
            sql.append("oper_cause,");
        }
        sql.append("inout_flag)");
        sql.append(" values(?,?,?,?,?,?,");
        if(this.isSyncCardCause()) {
            sql.append("?,");
        }
        sql.append("0)");
        boolean isCorrect =false;
        ContentDAO dao=new ContentDAO(this.conn);
        try
        {
            dao.batchInsert(sql.toString(),list);
            isCorrect=true;
        }catch(Exception e)
        {
            throw  GeneralExceptionHandler.Handle(e); 
        }
        return isCorrect;
    }
    
    /**
     * 同步人员信息
     * @param dblist
     * @param card_field
     * @param DestTab
     * @param SrcTab
     * @throws GeneralException
     */
    public boolean  upTemp_originality_emp_data(ArrayList dblist,String cardno_field,String DestTab)throws GeneralException 
    {
        if(dblist==null||dblist.size()<=0) {
            return false;
        }
        
        boolean isCorrect = false;
        
        DbWizard dbWizard = new DbWizard(this.conn);
        
        ContentDAO dao = new ContentDAO(this.conn);
        String nbase = "";
        try
        {
            String repeatCardSQL = getRepeatCardSQL(dblist, cardno_field, DestTab);
            for(int i=0; i<dblist.size(); i++)
            {
                nbase = dblist.get(i).toString();       
                String srcTab = nbase + "A01";//源表
                
                if (!dbWizard.isExistTable(srcTab, false)) {
                    continue;
                }
                
                String strJoin = DestTab+".card_no="+srcTab+"."+cardno_field;//关联串  xxx.field_name=yyyy.field_namex,....
                //更新串  xxx.field_name=yyyy.field_namex,....
                StringBuffer strSet = new StringBuffer();
                strSet.append(DestTab+".nbase='"+nbase+"'`"+DestTab+".a0100="+srcTab+".a0100`");
                strSet.append(DestTab+".a0101="+srcTab+".a0101`"+DestTab+".e01a1="+srcTab+".e01a1`");
                strSet.append(DestTab+".B0110="+srcTab+".B0110`"+DestTab+".E0122="+srcTab+".E0122");
                String strDWhere =  DestTab + ".card_no NOT IN (" + repeatCardSQL + ")";//更新目标的表过滤条件
                //String strSWhere="("+cardno_field+" is not null or "+cardno_field+"<>'')";//源表的过滤条件
                 String strSWhere = Sql_switcher.isnull(cardno_field, "'##'")+"<>'##'";
                String update = Sql_switcher.getUpdateSqlTwoTable(DestTab,srcTab,strJoin,strSet.toString(),strDWhere,strSWhere);
                update = KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
                //System.out.println("关联人员="+update);
                cat.error("关联人员="+update);          
                cat.debug("关联人员="+update);
                dao.update(update);
            }
            isCorrect=true;
        }catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("","考勤人员重复分配了同一卡号，请检查！","",""));
        }       
        return isCorrect;
    }
    /**
     * 同步临时表机器信息
     * @param cardno_field
     * @param DestTab
     * @param SrcTab
     * @throws GeneralException
     */
    public boolean upTemp_originality_loaction_data(String DestTab,String SrcTab)throws GeneralException 
    {
        ContentDAO dao = new ContentDAO(this.conn);
        boolean isCorrect = false;
        try
        {   
            String destTab = DestTab;//目标表
            String srcTab = "(SELECT machine_no,MAX(location) AS location, MAX(inout_flag) AS inout_flag FROM kq_machine_location GROUP BY machine_no) A";//源表
            String strJoin = DestTab+".machine_no=A.machine_no";//关联串  xxx.field_name=yyyy.field_namex,....
            String  strSet = DestTab+".location=A.location`"+DestTab+".inout_flag=A.inout_flag";//更新串  xxx.field_name=yyyy.field_namex,....
            String strDWhere = "";//更新目标的表过滤条件
            String strSWhere = " A.machine_no IS NOT NULL";//源表的过滤条件  
            String update = Sql_switcher.getUpdateSqlTwoTable(destTab,srcTab,strJoin,strSet,strDWhere,strSWhere);
            update = KqUtilsClass.repairSqlTwoTable(srcTab,strJoin,update,strDWhere,"");
            dao.update(update);
            
            if(this.type!=null && "Machine".equalsIgnoreCase(this.type))
            {
                if(this.location!=null && this.location.length()>0)
                {
                    update = "update "+destTab+" set location='"+this.location+"'";
                    dao.update(update);
                }
            }
            
            isCorrect = true;
        }
        catch(Exception e)
        {
            throw GeneralExceptionHandler.Handle(e);
        }
        
        return isCorrect;
    }
    
    public String createTemp(String SrcTab)
    {
        
        String DestTab = getTmpTableName(this.userView.getUserName());
        KqUtilsClass.dropTable(conn, DestTab); 
        
        StringBuffer sql=new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        try
        {
            switch(Sql_switcher.searchDbServer())
            {
                  case Constant.MSSQL:
                  {
                      sql.append("select * Into "+DestTab+" from "+SrcTab); 
                      sql.append(" where 1=2");
                      dao.update(sql.toString());
                      break;
                  }
                  case Constant.ORACEL:
                  { 
                      sql.append("Create Table "+DestTab);
                      sql.append("  as select *  from "+SrcTab); 
                      sql.append(" where 1=2");
                      dao.update(sql.toString());
                      break;
                  }
                  case Constant.DB2:
                  {
                      sql.append("Create Table "+DestTab);
                      sql.append("  AS (select *  from "+SrcTab); 
                      sql.append(" where 1=2) DEFINITION ONLY");
                      dao.update(sql.toString());
                      sql=new StringBuffer();
                      sql.append("INSERT INTO "+DestTab);
                      sql.append(" select *  from "+SrcTab); 
                      sql.append(" where 1=2)");
                      dao.update(sql.toString());
                      break;
                  }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }       
        /**重新加载数据模型*/

        DBMetaModel dbmodel=new DBMetaModel(conn);
        dbmodel.reloadTableModel(DestTab);    
        return DestTab;
    }

    /**
     * 新建临时表的名字
     * **/
    private String getTmpTableName(String UserId) 
    {
        StringBuffer tablename=new StringBuffer();
        StringBuffer strSrc=new StringBuffer();
        strSrc.append("123456789");//2009-09-23，修改为4位数字
        Random random=new Random(System.currentTimeMillis()); 
        int codelen=6;
        StringBuffer checkNum=new StringBuffer();
        int index=0;
        for(int i=0;i<codelen;i++)
        {
                index=random.nextInt(9);
                checkNum.append(strSrc.charAt(index));
        }   
       
        tablename.append("t#");
        tablename.append(UserId);
        tablename.append("_kqCard");
        
        return tablename.toString();
    }
    
    public String getColectInOutSQL(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c,String inout_flag)
    {
       StringBuffer sqlstr=new StringBuffer();
       if(start_date!=null&&start_date.length()>0) {
           start_date=start_date.replaceAll("-","\\.");
       }
       
       if(end_date!=null&&end_date.length()>0) {
           end_date=end_date.replaceAll("-","\\.");
       }
       
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           sqlstr.append("select count(*) as count from ");
           sqlstr.append("(");
           sqlstr.append("select nbase,a0100");
           sqlstr.append(" FROM kq_originality_data a");
           sqlstr.append(" WHERE  work_date"+Sql_switcher.concat()+"work_time=");
           sqlstr.append(" (SELECT MAX(work_date"+Sql_switcher.concat()+"work_time) FROM kq_originality_data b");
           sqlstr.append(" WHERE a.nbase=b.nbase AND a.A0100=b.A0100 and ");
           sqlstr.append(getCodeItemWhere(a_code,nbase));
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           if(!this.userView.isSuper_admin())
           {
               if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and b.a0100="+nbase+"A01.a0100)");
               } else {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where b.a0100="+nbase+"A01.a0100)");
               }
            
           }
           sqlstr.append(")");
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+")) ");    
           sqlstr.append(" and ");
           sqlstr.append(getCodeItemWhere(a_code,nbase));
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
          if(!this.userView.isSuper_admin())
           {
               if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and a.a0100="+nbase+"A01.a0100)");
               } else {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where a.a0100="+nbase+"A01.a0100)");
               }
            
           }
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+") "); 
           sqlstr.append(" and "+Sql_switcher.isnull("inout_flag","0")+"="+inout_flag+"");
           sqlstr.append(" group by nbase,A0100) aaa");                
           sqlstr.append(" UNION ");
       }
       sqlstr.setLength(sqlstr.length()-7); 
       return sqlstr.toString();       
    }
    
    public String getColectInOutSQL(String nbase,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c,String inout_flag)
    {
          StringBuffer sqlstr=new StringBuffer();
          if(start_date!=null&&start_date.length()>0) {
              start_date=start_date.replaceAll("-","\\.");
          }
          if(end_date!=null&&end_date.length()>0) {
              end_date=end_date.replaceAll("-","\\.");
          }
          String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           
           sqlstr.append("select count(*) as count");
           sqlstr.append(" FROM kq_originality_data a");
           sqlstr.append(" WHERE  work_date"+Sql_switcher.concat()+"work_time=");
           sqlstr.append(" (SELECT MAX(work_date"+Sql_switcher.concat()+"work_time) FROM kq_originality_data b");
           sqlstr.append(" WHERE a.nbase=b.nbase AND a.A0100=b.A0100 ");
           if(a_code!=null&&a_code.length()>2) {
               sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           }
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and nbase='"+nbase+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           if(!this.userView.isSuper_admin())
           {
              if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                  sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and b.a0100="+nbase+"A01.a0100)");
              } else {
                  sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where b.a0100="+nbase+"A01.a0100)");
              }
        
            }
           sqlstr.append(")");
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+")) ");    
           if(a_code!=null&&a_code.length()>2) {
               sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           }
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and nbase='"+nbase+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
          if(!this.userView.isSuper_admin())
          {
           if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
               sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and a.a0100="+nbase+"A01.a0100)");
           } else {
               sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where a.a0100="+nbase+"A01.a0100)");
           }
        
          }
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+") "); 
           sqlstr.append(" and "+Sql_switcher.isnull("inout_flag","0")+"="+inout_flag+"");
       return sqlstr.toString();       
    }
    
    public String getColectInOutSQL1(ArrayList nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c,String inout_flag)
    {
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        g_no = checkGnoFld(g_no);
        // String cardno = (String) hashmap.get("cardno");
        // ** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        String ischecker = "0";
        ischecker = RegisterInitInfoData.ischecker(para.getNbase(), this.userView.getA0100(), this.conn);
        boolean isSuper = this.userView.isSuper_admin();
        String account = "";
        if(!isSuper){
            String a01001 = this.userView.getA0100();
            String nbase1 = this.userView.getDbname();
            StringBuffer sb1 = new StringBuffer();
            DbNameBo dbbo = new DbNameBo(this.conn);
            String username = dbbo.getLogonUserNameField().toUpperCase();
            sb1.append("select "+username+" from "+nbase1+"A01 where A0100='"+a01001+"'");
            ContentDAO cd = new ContentDAO(this.conn);
            RowSet rs = null;
            try {
                 if("".equals(a01001) || "".equals(nbase1)) {
                     account = null;
                 } else{
                    rs = cd.search(sb1.toString());
                    while(rs.next()){
                        account = rs.getString(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                KqUtilsClass.closeDBResource(rs);
            }
        }
        String columnjoin = "Q.a0100,Q.nbase,a0101,b0110,e0122,e01a1,location,machine_no,";
        if (!"".equals(g_no)) {
            columnjoin += g_no + ",";
        }
        columnjoin += "card_no,work_date,work_time,status,inout_flag,oper_cause,oper_user,";
        
        if (Sql_switcher.searchDbServer() == Constant.ORACEL) {
            columnjoin += Sql_switcher.dateToChar("oper_time",  "yyyy-MM-dd HH24:mi");
        } else if (Sql_switcher.searchDbServer() == Constant.MSSQL) {
            columnjoin += "convert(varchar(16),oper_time,20)";
        }
        
        columnjoin += " oper_time,oper_mach,sp_flag,datafrom,curr_user,iscommon";
//      String column = getKq_originality_column2();
        StringBuffer sqlstr = new StringBuffer();
        if (start_date != null && start_date.length() > 0) {
            start_date = start_date.replaceAll("-", "\\.");
        }
        
        if (end_date != null && end_date.length() > 0) {
            end_date = end_date.replaceAll("-", "\\.");
        }
        
        StringBuffer joinTable = new StringBuffer();
        StringBuffer joinTable1 = new StringBuffer();
        for (int i = 0; i < nbase_list.size(); i++) {
            String nbase = (String)nbase_list.get(i);
            String whereIN = RegisterInitInfoData.getWhereINSql(userView, nbase);
            String tCodeItemWhere = getCodeItemWhere(a_code, nbase);
            if (whereIN != null
                    && (whereIN.indexOf("WHERE") != -1 || whereIN.indexOf("where") != -1)) {
                whereIN += " AND " + tCodeItemWhere;
            } else {
                whereIN += " WHERE " + tCodeItemWhere;
            }
            
            joinTable.append("SELECT "+i+" as i,A0100,'" + nbase + "' nbase");
            if (!"".equals(g_no)) {
                joinTable.append("," + g_no);
            }
            joinTable.append(whereIN);
            
            joinTable1.append("SELECT "+i+" as i,A0100,'" + nbase + "' nbase");
            if (!"".equals(g_no)) {
                joinTable1.append("," + g_no);
            }
            joinTable1.append(" FROM " + nbase + "A01");//审批关系
            
            if (i != nbase_list.size()-1) {
                joinTable.append(" union ");
                joinTable1.append(" union ");
            }
        }
        sqlstr.append("select COUNT(1) count from ");
        sqlstr.append("(SELECT " + columnjoin + " FROM "
                + this.kq_originality_data + " Q INNER JOIN (" + joinTable
                + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase AND Q.sp_flag <> '01'");
        if(isSuper || "1".equals(ischecker) || null == account || account.length()<0 || this.userView.isSuper_admin()) {
            sqlstr.append(")B");
        } else{
            sqlstr.append(" AND ( Q.curr_user is null or Q.curr_user = 'null')"); //or Q.curr_user = '" + account + "'))B");
            if (account != null && account.length() > 0) 
            {
                sqlstr.append(" UNION ALL ");
                sqlstr.append("SELECT " + columnjoin + " FROM "
                        + this.kq_originality_data + " Q INNER JOIN (" + joinTable1
                        + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase AND Q.sp_flag <> '01' AND  Q.curr_user = '" + account + "'");
            }
            sqlstr.append(")B");
        }

        sqlstr.append(" WHERE ");
        // 修改 不等于当天时间
        if (!end_date.equals(start_date)) {
            sqlstr.append(" ((work_date='" + start_date + "'");
            sqlstr.append(" and work_time>='" + start_time + "')");
            sqlstr.append(" or (work_date='" + end_date + "'");
            sqlstr.append(" and work_time<='" + end_time + "')");
            sqlstr.append(" or (work_date>'" + start_date + "' and work_date<'"
                    + end_date + "'))");
        } else {
            sqlstr.append(" work_date>='" + start_date + "'");
            sqlstr.append(" and work_time>='" + start_time + "'");
            sqlstr.append(" and work_date<='" + end_date + "'");
            sqlstr.append(" and work_time<='" + end_time + "'");
        }
        if (where_c != null && where_c.length() > 0) {
            sqlstr.append(where_c);
        }
        sqlstr.append(" and "+Sql_switcher.isnull("inout_flag","0")+"="+inout_flag+"");
        return sqlstr.toString();   
    }
    
    public String getOutEmpSql(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c)
    {
       String column=getKq_originality_column();
       StringBuffer sqlstr=new StringBuffer();
       
       if(start_date!=null&&start_date.length()>0) {
           start_date=start_date.replaceAll("-","\\.");
       }
       
       if(end_date!=null&&end_date.length()>0) {
           end_date=end_date.replaceAll("-","\\.");
       }
       
       for(int i=0;i<kq_nbase_list.size();i++)
       {
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           sqlstr.append("select "+i+" as i,"+column+" from ");
           sqlstr.append("(");
           sqlstr.append("select nbase,a0100,MAX(B0110) AS B0110,MAX(E0122) AS E0122,MAX(E01A1) AS E01A1,");
           sqlstr.append(" MAX(A0101) AS A0101,MAX(work_date) AS work_date,MAX(work_time) AS work_time,MAX(inout_flag) AS inout_flag,");
           sqlstr.append("MAX(location) as location,MAX(machine_no) as machine_no,MAX(card_no) as card_no,MAX(status) as status,MAX(oper_cause) as oper_cause,");
           sqlstr.append("MAX(oper_user) as oper_user,MAX(oper_time) as oper_time,MAX(oper_mach) as oper_mach,MAX(sp_flag) as sp_flag");
           sqlstr.append(" FROM kq_originality_data a");
           sqlstr.append(" WHERE  work_date"+Sql_switcher.concat()+"work_time=");
           sqlstr.append(" (SELECT MAX(work_date"+Sql_switcher.concat()+"work_time) FROM kq_originality_data b");
           sqlstr.append(" WHERE a.nbase=b.nbase AND a.A0100=b.A0100  ");
           if(a_code!=null&&a_code.length()>2) {
               sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           }
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           if(!this.userView.isSuper_admin())
           {
               if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and b.a0100="+nbase+"A01.a0100)");
               } else {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where b.a0100="+nbase+"A01.a0100)");
               }
            
           }
           sqlstr.append(")");
          // sqlstr.append(" and a0100 in(select a0100 "+whereIN+")) ");    
           if(a_code!=null&&a_code.length()>2) {
               sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
           }
           sqlstr.append(" and work_date>='"+start_date+"'");
           sqlstr.append(" and work_time>='"+start_time+"'");
           sqlstr.append(" and work_date<='"+end_date+"'");
           sqlstr.append(" and work_time<='"+end_time+"'");        
           sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
           if(where_c!=null&&where_c.length()>0) {
               sqlstr.append(" "+where_c+"");
           }
           if(!this.userView.isSuper_admin())
           {
               if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and a.a0100="+nbase+"A01.a0100)");
               } else {
                   sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where a.a0100="+nbase+"A01.a0100)");
               }
            
           }
           //sqlstr.append(" and a0100 in(select a0100 "+whereIN+") "); 
           sqlstr.append(" and "+Sql_switcher.isnull("inout_flag","0")+"=-1");
           sqlstr.append(" group by nbase,A0100) aaa");                
           sqlstr.append(" UNION ");
       }
       sqlstr.setLength(sqlstr.length()-7); 
       return sqlstr.toString();
    }
    
    public String getOutEmpSql1(ArrayList kq_nbase_list,String a_code,String start_date,String end_date,String start_time,String end_time,String where_c){
       String column=getKq_originality_column();
       StringBuffer sqlstr=new StringBuffer();
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
        KqParameter para = new KqParameter(this.userView, "", this.conn);
        String g_no = para.getG_no();
        g_no = checkGnoFld(g_no);
//      String cardno = (String) hashmap.get("cardno");
        //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
       if(start_date!=null&&start_date.length()>0) {
           start_date=start_date.replaceAll("-","\\.");
       }
       if(end_date!=null&&end_date.length()>0) {
           end_date=end_date.replaceAll("-","\\.");
       }
      StringBuffer columnjoin = new StringBuffer();
      columnjoin.append("Q.nbase,Q.a0100,MAX(B0110) AS B0110,MAX(E0122) AS E0122,MAX(E01A1) AS E01A1,");
      if(showIScommon()){ // 判断是否存在字段 iscommon
         columnjoin.append("iscommon,");
      }
      columnjoin.append(" MAX(A0101) AS A0101,MAX(work_date) AS work_date,MAX(work_time) AS work_time,MAX(inout_flag) AS inout_flag,");
      columnjoin.append("MAX(location) as location,MAX(machine_no) as machine_no,MAX(card_no) as card_no,MAX(status) as status,MAX(oper_cause) as oper_cause,");
      columnjoin.append("MAX(oper_user) as oper_user,MAX(oper_time) as oper_time,MAX(oper_mach) as oper_mach,MAX(sp_flag) as sp_flag,A." + g_no);
      StringBuffer joinTable=new StringBuffer();
      for(int i=0;i<kq_nbase_list.size();i++){
           String nbase=kq_nbase_list.get(i).toString();
           String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
           String tCodeItemWhere = getCodeItemWhere(a_code,nbase);
           if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1)) {
               whereIN += " AND " + tCodeItemWhere;
           } else {
               whereIN += " WHERE " + tCodeItemWhere;
           }
           if(joinTable.length() > 0){
               joinTable.append(" UNION SELECT '" + i + "' i,A0100,'" + nbase + "' nbase," + g_no + whereIN);
           }else{
               joinTable.append("SELECT '" + i + "' i,A0100,'" + nbase + "' nbase," + g_no + whereIN);
           }
       }
       sqlstr.append("select i,"+column+" from ");
       StringBuffer leftJoin = new StringBuffer();
       leftJoin.append("SELECT * FROM " + this.kq_originality_data);
       leftJoin.append(" WHERE ");
       //修改 不等于当天时间
        leftJoin.append(" work_date>='"+start_date+"'");
        leftJoin.append(" and work_time>='"+start_time+"'");
        leftJoin.append(" and work_date<='"+end_date+"'");
        leftJoin.append(" and work_time<='"+end_time+"'");
        leftJoin.append(" and "+Sql_switcher.isnull("inout_flag","0")+"=-1");
       if(where_c != null && where_c.length() > 0){
           leftJoin.append(where_c);
       }
       sqlstr.append("(SELECT i," + columnjoin + " FROM (" + leftJoin + ") Q INNER JOIN (" + joinTable + ") A ON Q.A0100=A.A0100 AND Q.nbase = A.nbase GROUP BY i,Q.nbase,Q.a0100,A." + g_no + ") B ");
//     for(int i=0;i<kq_nbase_list.size();i++)
//     {
//         String nbase=kq_nbase_list.get(i).toString();
//         String whereIN=RegisterInitInfoData.getWhereINSql(userView,nbase);
//         sqlstr.append("select "+i+" as i,"+column+" from ");
//         sqlstr.append("(");
//         sqlstr.append("select nbase,a0100,MAX(B0110) AS B0110,MAX(E0122) AS E0122,MAX(E01A1) AS E01A1,");
//         sqlstr.append(" MAX(A0101) AS A0101,MAX(work_date) AS work_date,MAX(work_time) AS work_time,MAX(inout_flag) AS inout_flag,");
//         sqlstr.append("MAX(location) as location,MAX(machine_no) as machine_no,MAX(card_no) as card_no,MAX(status) as status,MAX(oper_cause) as oper_cause,");
//         sqlstr.append("MAX(oper_user) as oper_user,MAX(oper_time) as oper_time,MAX(oper_mach) as oper_mach,MAX(sp_flag) as sp_flag");
//         sqlstr.append(" FROM kq_originality_data a");
//         sqlstr.append(" WHERE  work_date"+Sql_switcher.concat()+"work_time=");
//         sqlstr.append(" (SELECT MAX(work_date"+Sql_switcher.concat()+"work_time) FROM kq_originality_data b");
//         sqlstr.append(" WHERE a.nbase=b.nbase AND a.A0100=b.A0100  ");
//         if(a_code!=null&&a_code.length()>2)
//           sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
//         sqlstr.append(" and work_date>='"+start_date+"'");
//         sqlstr.append(" and work_time>='"+start_time+"'");
//         sqlstr.append(" and work_date<='"+end_date+"'");
//         sqlstr.append(" and work_time<='"+end_time+"'");        
//         sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
//         if(where_c!=null&&where_c.length()>0)
//             sqlstr.append(" "+where_c+"");
//         if(!this.userView.isSuper_admin())
//         {
//             if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
//                 sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and b.a0100="+nbase+"A01.a0100)");
//              else
//                sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where b.a0100="+nbase+"A01.a0100)");
//          
//         }
//         sqlstr.append(")");
//        // sqlstr.append(" and a0100 in(select a0100 "+whereIN+")) ");    
//         if(a_code!=null&&a_code.length()>2)
//           sqlstr.append(" and "+getCodeItemWhere(a_code,nbase));
//         sqlstr.append(" and work_date>='"+start_date+"'");
//         sqlstr.append(" and work_time>='"+start_time+"'");
//         sqlstr.append(" and work_date<='"+end_date+"'");
//         sqlstr.append(" and work_time<='"+end_time+"'");        
//         sqlstr.append(" and UPPER(nbase)='"+nbase.toUpperCase()+"'");
//         if(where_c!=null&&where_c.length()>0)
//             sqlstr.append(" "+where_c+"");
//         if(!this.userView.isSuper_admin())
//         {
//             if(whereIN!=null&&(whereIN.indexOf("WHERE")!=-1||whereIN.indexOf("where")!=-1))
//                 sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" and a.a0100="+nbase+"A01.a0100)");
//              else
//                sqlstr.append(" and EXISTS(select "+nbase+"A01.a0100 "+whereIN+" where a.a0100="+nbase+"A01.a0100)");
//          
//         }
//         //sqlstr.append(" and a0100 in(select a0100 "+whereIN+") "); 
//         sqlstr.append(" and "+Sql_switcher.isnull("inout_flag","0")+"=-1");
//         sqlstr.append(" group by nbase,A0100) aaa");                
//         sqlstr.append(" UNION ");
//     }
       return sqlstr.toString();
    }
    
    public ArrayList getMachineList()
    {
        StringBuffer sql=new StringBuffer();
        ArrayList machinelist=new ArrayList();
        sql.append("select name,location_id from kq_machine_location");
        ContentDAO dao=new ContentDAO(this.conn);
        CommonData vo = new CommonData() ;
        vo.setDataName("");
        vo.setDataValue("");
        machinelist.add(vo);
        RowSet rs=null;
        try
        {
            rs=dao.search(sql.toString());
            while(rs.next())
            {
                vo = new CommonData() ;
                vo.setDataName(rs.getString("name"));
                vo.setDataValue(rs.getString("location_id"));
                machinelist.add(vo);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return machinelist;
    }
    /**
     * 是否显示进出类型
     * @return
     */
    public boolean isViewInout_flag()
    {
        boolean isCorrect=false;
        String sql="select 1 from kq_machine_location where "+Sql_switcher.isnull("inout_flag", "'0'")+"<>'0'";
        RowSet rs=null;
        try
        {
            ContentDAO dao=new ContentDAO(this.conn);
            rs=dao.search(sql);
            if(rs.next()) {
                isCorrect=true;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return isCorrect;
    }
    /**
     * 
     * @param type_id
     * @param cardno_len  select id_length from id_factory where sequence_name='kq_cards.card_no'
     * @param card_len_str 考勤机卡号长度
     * @return
     * @throws GeneralException
     */
    public HashMap getFile_Rule(String type_id,String cardno_len,String card_len_str)throws GeneralException 
    {
        HashMap hashM=new HashMap();
        //if(type_id.equals("5")&&(cardno_len!=null&&cardno_len.length()>0))
        //{
        //  card_len_str=cardno_len;
        //}
        int card_len=0;
        if(card_len_str!=null&&card_len_str.length()>0) {
            card_len=Integer.parseInt(card_len_str);
        }
        try
        {
            if("1".equals(type_id))//科密
            {
                if(card_len>0)
                {
                    int to=card_len;
                    hashM.put("card_s","1");
                    hashM.put("card_e",card_len+"");
                    to=card_len+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");  
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");               
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","19");
                    hashM.put("machine_e","20");
                    hashM.put("card_s","1");
                    hashM.put("card_e","5");
                    hashM.put("year_s","11");
                    hashM.put("year_e","14");
                    hashM.put("md_s","15");
                    hashM.put("md_e","18");
                    hashM.put("hm_s","6");
                    hashM.put("hm_e","10");
                    hashM.put("status","0");
                }
                
            }else if("2".equals(type_id))//舒特
            {
                if(card_len>0)
                {
                    int to=card_len;
                    hashM.put("card_s","1");                    
                    hashM.put("card_e",card_len+"");
                    to=card_len+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","20");
                    hashM.put("machine_e","21");
                    hashM.put("card_s","1");
                    hashM.put("card_e","6");
                    hashM.put("year_s","12");
                    hashM.put("year_e","15");
                    hashM.put("md_s","16");
                    hashM.put("md_e","19");
                    hashM.put("hm_s","7");
                    hashM.put("hm_e","11");
                    hashM.put("status","0");
                }
                
            }else if("3".equals(type_id))//舒特10位
            {
                 /*卡号：1～10, 时分：11～15, 年份：16～19,月日：20～23,机号：24～25*/
                if(card_len>0)
                {
                    int to=card_len;                    
                    hashM.put("card_s","1");                    
                    hashM.put("card_e",to+"");
                    to=card_len+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");                   
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","24");
                    hashM.put("machine_e","25");
                    hashM.put("card_s","1");
                    hashM.put("card_e","10");
                    hashM.put("year_s","16");
                    hashM.put("year_e","19");
                    hashM.put("md_s","20");
                    hashM.put("md_e","23");
                    hashM.put("hm_s","11");
                    hashM.put("hm_e","15");
                    hashM.put("status","0");
                }               
            }else if("4".equals(type_id) || "0".equals(type_id))//3、华达拉斯 0、自定义考勤机
            {
                /*卡号：1～10  时分：19～23  年份：11～14  月日：15～18  机号：24～25*/
                if(card_len>0)
                {
                    int to=card_len;    
                    hashM.put("card_s","1");
                    hashM.put("card_e",to+"");
                    to=card_len+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");                   
                    hashM.put("status","0");
                }else
                {
                    hashM.put("card_s","1");
                    hashM.put("card_e","10");
                    hashM.put("year_s","11");
                    hashM.put("year_e","14");
                    hashM.put("md_s","15");
                    hashM.put("md_e","18");
                    hashM.put("hm_s","19");
                    hashM.put("hm_e","23");
                    hashM.put("machine_s","24");
                    hashM.put("machine_e","25");                    
                    hashM.put("status","0");
                }
                
            }else if("5".equals(type_id)|| "12".equals(type_id))//4、中控指纹机（注：卡号可能扩充，需根据传入的卡号长度来确定，默认5位）
            {
                /*卡号：1～5,   时分：6～10,年份：11～14,月日：15～18, 机号：19～20*/
                //if(cardno_len==null||cardno_len.trim().length()<=0)
                //{
                    if(card_len>0)
                    {
                        int to=card_len;                            
                        hashM.put("card_s","1");
                        hashM.put("card_e",to+"");
                        to=card_len+1;
                        hashM.put("hm_s",to+"");
                        to=to+4;
                        hashM.put("hm_e",to+"");
                        to=to+1;
                        hashM.put("year_s",to+"");
                        to=to+3;
                        hashM.put("year_e",to+"");
                        to=to+1;
                        hashM.put("md_s",to+"");
                        to=to+3;
                        hashM.put("md_e",to+"");
                        to=to+1;
                        hashM.put("machine_s",to+"");
                        to=to+1;
                        hashM.put("machine_e",to+"");                       
                        hashM.put("status","0");
                    }else
                    {
                        hashM.put("machine_s","19");
                        hashM.put("machine_e","20");
                        hashM.put("card_s","1");
                        hashM.put("card_e","5");
                        hashM.put("year_s","11");
                        hashM.put("year_e","14");
                        hashM.put("md_s","15");
                        hashM.put("md_e","18");
                        hashM.put("hm_s","6");
                        hashM.put("hm_e","10");
                        hashM.put("status","0");
                    }
                    /*}else
                {
                    int c_len=Integer.parseInt(cardno_len);
                    if(c_len>5)
                    {
                        int var=c_len-5;
                        hashM.put("card_s","1");
                        hashM.put("card_e",c_len+"");
                        hashM.put("hm_s",(6+var)+"");
                        hashM.put("hm_e",(10+var)+"");
                        hashM.put("year_s",(11+var)+"");
                        hashM.put("year_e",(14+var)+"");
                        hashM.put("md_s",(15+var)+"");
                        hashM.put("md_e",(18+var)+"");
                        hashM.put("machine_s",(19+var)+"");
                        hashM.put("machine_e",(20+var)+"");
                        hashM.put("status","0");
                    }else
                    {
                        hashM.put("machine_s","19");
                        hashM.put("machine_e","20");
                        hashM.put("card_s","1");
                        hashM.put("card_e","5");
                        hashM.put("year_s","11");
                        hashM.put("year_e","14");
                        hashM.put("md_s","15");
                        hashM.put("md_e","18");
                        hashM.put("hm_s","6");
                        hashM.put("hm_e","10");
                        hashM.put("status","0");
                    }
                }*/
            }else if("6".equals(type_id))//科密KD
            {
                if(card_len>0)
                {
                    int to=card_len;                        
                    hashM.put("card_s","1");
                    hashM.put("card_e",to+"");
                    to=card_len+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");                   
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","19");
                    hashM.put("machine_e","20");
                    hashM.put("card_s","1");
                    hashM.put("card_e","5");
                    hashM.put("year_s","11");
                    hashM.put("year_e","14");
                    hashM.put("md_s","15");
                    hashM.put("md_e","18");
                    hashM.put("hm_s","6");
                    hashM.put("hm_e","10");
                    hashM.put("status","0");
                }               
            }else if("7".equals(type_id))//GS**系列
            {
                if(card_len>0)
                {
                    int to=card_len;
                    hashM.put("machine_s","14");
                    hashM.put("machine_e","16");                    
                    hashM.put("year_s","1");
                    hashM.put("year_e","4");
                    hashM.put("md_s","5");
                    hashM.put("md_e","8");
                    hashM.put("hm_s","9");
                    hashM.put("hm_e","13");
                    hashM.put("card_s","17");
                    to=17+card_len;
                    hashM.put("card_e",to+"");
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","14");
                    hashM.put("machine_e","16");
                    hashM.put("card_s","17");
                    hashM.put("card_e","23");
                    hashM.put("year_s","1");
                    hashM.put("year_e","4");
                    hashM.put("md_s","5");
                    hashM.put("md_e","8");
                    hashM.put("hm_s","9");
                    hashM.put("hm_e","13");
                    hashM.put("status","0");
                }
                
            }else if("8".equals(type_id))//立方考勤机（卡号长度不定）
            {
                int c_len=Integer.parseInt(cardno_len);
                if(c_len==8)
                {
                    hashM.put("machine_s","22");
                    hashM.put("machine_e","23");
                    hashM.put("card_s","1");
                    hashM.put("card_e","8");
                    hashM.put("year_s","14");
                    hashM.put("year_e","17");
                    hashM.put("md_s","18");
                    hashM.put("md_e","21");
                    hashM.put("hm_s","9");
                    hashM.put("hm_e","13");
                    hashM.put("status","0");
                }else
                {
                    if(card_len>0)
                    {
                        int to=card_len;
                        hashM.put("card_s","1");
                        hashM.put("card_e",to+"");
                        to=card_len+1;
                        hashM.put("hm_s",to+"");
                        to=to+4;
                        hashM.put("hm_e",to+"");
                        to=to+1;
                        hashM.put("year_s",to+"");
                        to=to+3;
                        hashM.put("year_e",to+"");
                        to=to+1;
                        hashM.put("md_s",to+"");
                        to=to+3;
                        hashM.put("md_e",to+"");
                        to=to+1;
                        hashM.put("machine_s",to+"");
                        to=to+1;
                        hashM.put("machine_e",to+"");
                        hashM.put("status","0");
                    }else
                    {
                        hashM.put("card_s","1");
                        hashM.put("card_e","10");
                        hashM.put("hm_s","11");
                        hashM.put("hm_e","15");
                        hashM.put("year_s","16");
                        hashM.put("year_e","19");
                        hashM.put("md_s","20");
                        hashM.put("md_e","23");
                        hashM.put("machine_s","24");
                        hashM.put("machine_e","25");
                        hashM.put("status","0");
                    }
                    
                }
                
            }else if("9".equals(type_id))//披克考勤机
            {
                if(card_len>0)
                {
                    int to=card_len;                    
                    hashM.put("card_s","1");
                    hashM.put("card_e",to+"");
                    to=card_len+1;
                    hashM.put("hm_s",to+"");
                    to=to+4;
                    hashM.put("hm_e",to+"");
                    to=to+1;
                    hashM.put("year_s",to+"");
                    to=to+3;
                    hashM.put("year_e",to+"");
                    to=to+1;
                    hashM.put("md_s",to+"");
                    to=to+3;
                    hashM.put("md_e",to+"");
                    to=to+1;
                    hashM.put("machine_s",to+"");
                    to=to+1;
                    hashM.put("machine_e",to+"");                   
                    hashM.put("status","0");
                }else
                {
                    hashM.put("machine_s","22");
                    hashM.put("machine_e","23");
                    hashM.put("card_s","1");
                    hashM.put("card_e","8");
                    hashM.put("year_s","14");
                    hashM.put("year_e","17");
                    hashM.put("md_s","18");
                    hashM.put("md_e","21");
                    hashM.put("hm_s","9");
                    hashM.put("hm_e","13");
                    hashM.put("status","0");
                }
                
            }else if("10".equals(type_id)) //汉王
            {
                if(card_len<=0)
                {
                    card_len=6;
                }
                int to=card_len;
                hashM.put("card_s","1");
                hashM.put("card_e",to+"");          
                to=card_len+1;
                hashM.put("hm_s",to+"");
                to=to+4;
                hashM.put("hm_e",to+"");
                to=to+1;
                hashM.put("year_s",to+"");
                to=to+3;
                hashM.put("year_e",to+"");
                to=to+1;
                hashM.put("md_s",to+"");
                to=to+3;
                hashM.put("md_e",to+"");
                to=to+1;
                hashM.put("machine_s",to+"");
                to=to+1;
                hashM.put("machine_e",to+"");               
                hashM.put("status","0");             
            }else if("11".equals(type_id))//4、威尔考勤机（注：卡号可能扩充，需根据传入的卡号长度来确定，默认5位）
            {
                /*卡号：1～5,   时分：6～10,年份：11～14,月日：15～18, 机号：19～20*/
                if(cardno_len==null||cardno_len.trim().length()<=0)
                {
                    if(card_len>0)
                    {
                        int to=card_len;                            
                        hashM.put("card_s","1");
                        hashM.put("card_e",to+"");
                        to=card_len+1;
                        hashM.put("hm_s",to+"");
                        to=to+4;
                        hashM.put("hm_e",to+"");
                        to=to+1;
                        hashM.put("year_s",to+"");
                        to=to+3;
                        hashM.put("year_e",to+"");
                        to=to+1;
                        hashM.put("md_s",to+"");
                        to=to+3;
                        hashM.put("md_e",to+"");
                        to=to+1;
                        hashM.put("machine_s",to+"");
                        to=to+1;
                        hashM.put("machine_e",to+"");                       
                        hashM.put("status","0");
                    }else
                    {
                        hashM.put("machine_s","19");
                        hashM.put("machine_e","20");
                        hashM.put("card_s","1");
                        hashM.put("card_e","5");
                        hashM.put("year_s","11");
                        hashM.put("year_e","14");
                        hashM.put("md_s","15");
                        hashM.put("md_e","18");
                        hashM.put("hm_s","6");
                        hashM.put("hm_e","10");
                        hashM.put("status","0");
                    }
                }else
                {
                    int c_len=Integer.parseInt(cardno_len);
                    if(c_len>5)
                    {
                        int var=c_len-5;
                        hashM.put("card_s","1");
                        hashM.put("card_e",c_len+"");
                        hashM.put("hm_s",(6+var)+"");
                        hashM.put("hm_e",(10+var)+"");
                        hashM.put("year_s",(11+var)+"");
                        hashM.put("year_e",(14+var)+"");
                        hashM.put("md_s",(15+var)+"");
                        hashM.put("md_e",(18+var)+"");
                        hashM.put("machine_s",(19+var)+"");
                        hashM.put("machine_e",(20+var)+"");
                        hashM.put("status","0");
                    }else
                    {
                        hashM.put("machine_s","19");
                        hashM.put("machine_e","20");
                        hashM.put("card_s","1");
                        hashM.put("card_e","5");
                        hashM.put("year_s","11");
                        hashM.put("year_e","14");
                        hashM.put("md_s","15");
                        hashM.put("md_e","18");
                        hashM.put("hm_s","6");
                        hashM.put("hm_e","10");
                        hashM.put("status","0");
                    }
                }
            }else
            {
                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.error.type_id"),"",""));
            }
            
        }catch(Exception e)
        {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.machine.type_id.error"),"",""));
        }
        return hashM;
    }
    
    /**
     * 获取某个人的登陆账号
     * @param a0100
     * @param nbase
     * @return
     */
    public String getUserAccount(String a0100,String nbase){
        String account = "";
        StringBuffer sb1 = new StringBuffer();
        if (!("".equals(a0100) || null == a0100 || "".equals(nbase) || null == nbase)) {
            DbNameBo dbbo = new DbNameBo(this.conn);
            String username = dbbo.getLogonUserNameField().toUpperCase();
            sb1.append("select "+username+" from "+nbase+"A01 where A0100='"+a0100+"'");
            ContentDAO cd = new ContentDAO(this.conn);
            RowSet rs = null;
            try {
                if("".equals(a0100) || "".equals(nbase)) {
                    account = null;
                } else{
                    rs = cd.search(sb1.toString());
                    while(rs.next()){
                        account = rs.getString(1);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                KqUtilsClass.closeDBResource(rs);
            }
        }
        return account;
    }
    
    public boolean CheckCardData(String line, HashMap hashM){
        String s_str = "";
        String e_str = "";
        int s_int = 0;
        int e_int = 0;
        String str = "";
        
        /*******卡号********/
        s_str = (String) hashM.get("card_s");
        e_str = (String) hashM.get("card_e");
        s_int = Integer.parseInt(s_str) - 1;
        e_int = Integer.parseInt(e_str);
        str = line.substring(s_int, e_int);
        str = str != null && str.length() > 0 ? str.trim() : "";
        if ("".equals(str)) {
            return false;
        }
        
        /*******日期********/
        String date = "";
        s_str = (String) hashM.get("year_s");
        e_str = (String) hashM.get("year_e");
        s_int = Integer.parseInt(s_str) - 1;
        e_int = Integer.parseInt(e_str);
        str = line.substring(s_int, e_int);
        str = str != null && str.length() > 0 ? str.trim() : "";
        if ("".equals(str)) {
            return false;
        }
        
        date = str;
        if (Integer.parseInt(date) <= 1949 || Integer.parseInt(date) >= 2100) {
            return false;
        }

        s_str = (String) hashM.get("md_s");
        e_str = (String) hashM.get("md_e");
        s_int = Integer.parseInt(s_str) - 1;
        e_int = Integer.parseInt(e_str);
        str = line.substring(s_int, e_int);
        str = str != null && str.length() > 0 ? str.trim() : "";

        StringBuffer sb = new StringBuffer();
        sb.append(str);
        if (str.length() > 4) {
            sb.deleteCharAt(2);
        }
        String m = sb.substring(0, 2);
        String d = sb.substring(2, 4);
        if (Integer.parseInt(m) > 12 || Integer.parseInt(d) > 31) {
            return false;
        }

        /*******时间********/
        s_str = (String) hashM.get("hm_s");
        e_str = (String) hashM.get("hm_e");
        s_int = Integer.parseInt(s_str) - 1;
        e_int = Integer.parseInt(e_str);
        str = line.substring(s_int, e_int);
        str = str != null && str.length() > 0 ? str.trim() : "";
        if ("".equals(str)) {
            return false;
        }

        sb.setLength(0);
        sb.append(str);
        if (str.length() > 4) {
            sb.deleteCharAt(2);
        }
        String h = sb.substring(0, 2);
        String mi = sb.substring(2, 4);
        if (Integer.parseInt(h) > 24 || Integer.parseInt(mi) > 59) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 返回文本文件刷卡数据对应规则
     * @param file_num
     * @throws GeneralException
     */
    public HashMap getFile_Rule(String file_num)throws GeneralException 
    {
        HashMap hashM = new HashMap();
        
        StringBuffer sql = new StringBuffer();
        sql.append("select rule_id,rule_name,machine_s,machine_e,card_s,card_e,");
        sql.append("year_s,year_e,md_s,md_e,hm_s,hm_e,status from kq_data_rule");
        sql.append(" where rule_id='" + file_num + "'");
        
        int max = 0;
        int cont = 0;
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.conn);
            rs = dao.search(sql.toString());
            if (rs.next()) {
                hashM.put("machine_s", rs.getString("machine_s"));
                hashM.put("machine_e", rs.getString("machine_e"));
                hashM.put("card_s", rs.getString("card_s"));
                hashM.put("card_e", rs.getString("card_e"));
                hashM.put("year_s", rs.getString("year_s"));
                hashM.put("year_e", rs.getString("year_e"));
                hashM.put("md_s", rs.getString("md_s"));
                hashM.put("md_e", rs.getString("md_e"));
                hashM.put("hm_s", rs.getString("hm_s"));
                hashM.put("hm_e", rs.getString("hm_e"));
                hashM.put("status", rs.getString("status"));
                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("card_s") != null && rs.getString("card_s").length() > 0) {
                    cont = Integer.parseInt(rs.getString("card_s"));
                }
                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("year_s") != null && rs.getString("year_s").length() > 0) {
                    cont = Integer.parseInt(rs.getString("year_s"));
                }
                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("status") == null || !"1".equals(rs.getString("status"))) {
                    if (rs.getString("machine_s") != null && rs.getString("machine_s").length() > 0) {
                        cont = Integer.parseInt(rs.getString("machine_s"));
                    }
                    if (max < cont) {
                        max = cont;
                    }
                    if (rs.getString("machine_e") != null && rs.getString("machine_e").length() > 0) {
                        cont = Integer.parseInt(rs.getString("machine_e"));
                    }
                    if (max < cont) {
                        max = cont;
                    }
                }
                if (rs.getString("md_s") != null && rs.getString("md_s").length() > 0) {
                    cont = Integer.parseInt(rs.getString("md_s"));
                }
                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("md_e") != null && rs.getString("md_e").length() > 0) {
                    cont = Integer.parseInt(rs.getString("md_e"));
                }

                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("hm_s") != null && rs.getString("hm_s").length() > 0) {
                    cont = Integer.parseInt(rs.getString("hm_s"));
                }
                if (max < cont) {
                    max = cont;
                }
                if (rs.getString("hm_e") != null && rs.getString("hm_e").length() > 0) {
                    cont = Integer.parseInt(rs.getString("hm_e"));
                }
                if (max < cont) {
                    max = cont;
                }
                hashM.put("max", max + "");
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.work.error"),"",""));
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        return hashM;
    }
    
    /**
     * 统计人数inout_flag int （1） 出入标志 -1：出 0：不限，1：进
     * @param kq_nbase_list
     * @param a_code
     * @param start_date
     * @param end_date
     * @param start_time
     * @param end_time
     * @param where_c
     */
    public String colectInOutemps(ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time,
            String end_time, String where_c) {
        StringBuffer str = new StringBuffer();
        ContentDAO dao = new ContentDAO(this.conn);
        
        int count = getCardDataCount(dao, kq_nbase_list, a_code, start_date, end_date, start_time, end_time, where_c, "-1");
        str.append("外出刷卡：" + count + " 人次&nbsp;&nbsp;");
        
        count = getCardDataCount(dao, kq_nbase_list, a_code, start_date, end_date, start_time, end_time, where_c, "1");
        str.append("进入刷卡：" + count + " 人次&nbsp;&nbsp;");
        
        count = getCardDataCount(dao, kq_nbase_list, a_code, start_date, end_date, start_time, end_time, where_c, "0");
        str.append("不限刷卡：" + count + " 人次");            
            
        return str.toString();
    }
    
    private int getCardDataCount(ContentDAO dao, ArrayList kq_nbase_list, String a_code, String start_date, String end_date, String start_time,
            String end_time, String where_c, String inoutFlag) {
        int count = 0;
        
        String sqlstr = getColectInOutSQL1(kq_nbase_list, a_code, start_date, end_date, start_time, end_time, where_c, inoutFlag);
        RowSet rs = null;
        try {
            rs = dao.search(sqlstr);
            if (rs.next()) {
                count = rs.getInt("count");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rs);
        }
        
        return count;
    }
    
    /**
     * 
     * 判断是否存在 iscommon 字段
     * @return
     */
    public boolean showIScommon() {
        boolean result = false;
        
        RowSet rset = null;
        try {
            DbWizard dw = new DbWizard(this.conn);

            // 判断是否存在iscommon字段，不存在则退出   //dw.isExistField("kq_originality_data", "iscommon",false) 方法二
            ReconstructionKqField rk = new ReconstructionKqField(this.conn);
            if (!rk.checkFieldSave("kq_originality_data", "iscommon")) {
                return false;
            }
            
            // 有字段，就判断是否存在 kq_sign_point表
            if (!dw.isExistTable("kq_sign_point",false)) {
                return false;
            }
                
            // 存在表 kq_sign_point，表中是否有数据，查询是否有数据
            ContentDAO dao = new ContentDAO(this.conn);
            String sql = "select count(*) as cnt from kq_sign_point";
            rset = dao.search(sql);
            
            if (rset.next()) {// 表格中有签到地点
                result = 0 < rset.getInt("cnt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            KqUtilsClass.closeDBResource(rset);
        }
        return result;
    }
    
    public ArrayList getRepeatCards() {
        return repeatCards;
    }
    
    public boolean isSyncCardCause() {
        return isSyncCardCause;
    }
   
    public void setSyncCardCause(boolean isSyncCardCause) {
        this.isSyncCardCause = isSyncCardCause;
    }
}
