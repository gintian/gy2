package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.KQ_Parameter;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import javax.sql.RowSet;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SyncCardData {
    private static Category cat = Category.getInstance("com.hjsj.hrms.businessobject.kq.machine.SyncCardData");
    private Connection conn;
    private Connection _syConn;
    private String sync_base="";//同步数据库
    private String sync_post="";//同步数据库端口
    private String sync_url="";//同步数据库地址
    private String sync_table="";//同步表  
    private String sync_basetype="";//同步表类型
    private String sync_pass="";//同步表密码
    private String sync_user="";//同步表用户名
    private String sync_space = "";// 表空间
    private String mailto = ""; //同步异常通知邮件地址
    
    public SyncCardData(Connection conn)
    {
        this.conn=conn;
    }
    
    public boolean isAllowSync()
    {
        boolean isCorrect = false;
        String sync_carddata = SystemConfig.getPropertyValue("syncKqcardData");
        sync_carddata = sync_carddata == null || sync_carddata.length() <= 0 ? "false" : sync_carddata;
        if("true".equals(sync_carddata))
        {
            this.sync_base = getContent("SYNC_BASE", "UN");
            if(sync_base == null || sync_base.length() <= 0) {
                return false;
            }
            
            this.sync_post = getContent("SYNC_POST", "UN");
            if(sync_post == null || sync_post.length() <= 0) {
                return false;
            }
            
            this.sync_url = getContent("SYNC_URL", "UN");
            if(sync_url == null || sync_url.length() <= 0) {
                return false;
            }
            
            this.sync_table = getContent("SYNC_TABLE", "UN");
            if(sync_table == null || sync_table.length() <= 0) {
                return false;
            }
            
            this.sync_basetype = getContent("SYNC_BASETYPE", "UN");
            if(sync_basetype == null || sync_basetype.length() <= 0) {
                return false;
            }
            
            this.sync_pass = getContent("SYNC_PASS", "UN");
            if(sync_pass == null || sync_pass.length() <= 0) {
                return false;
            }
            
            this.sync_user = getContent("SYNC_USER", "UN");
            if(sync_user == null || sync_user.length() <= 0) {
                return false;
            }
            
            isCorrect = true;
        }
        return isCorrect;
    }
    
    /**
     * 判断是否连接
     * @return
     */
    public boolean isAllowSync(LazyDynaBean bean)
    {
        boolean isCorrect=true;

        this.sync_base = (String) bean.get("dbname");// 同步数据库
        if (this.sync_base == null || this.sync_base.length() <= 0) {
            isCorrect = false;
        }
        
        this.sync_post = (String) bean.get("port");// 同步数据库端口
        if (this.sync_post == null || this.sync_post.length() <= 0) {
            isCorrect = false;
        }
        
        this.sync_url = (String) bean.get("ip");// 同步数据库地址
        if (this.sync_url == null || this.sync_url.length() <= 0) {
            isCorrect = false;
        }
        
        this.sync_table = (String) bean.get("source");// 同步表
        if (this.sync_table == null || this.sync_table.length() <= 0) {
            isCorrect = false;
        }
        
        if (!sync_table.contains(".")&& this.sync_basetype!=null&& "oracle".equalsIgnoreCase(this.sync_basetype)) {
            this.sync_space = (String) bean.get("space");// 同步表空间
            if (this.sync_space != null && this.sync_space.length() > 0) {
                this.sync_table = this.sync_space + "." + this.sync_table;
            }
        }
        
        this.sync_basetype = (String) bean.get("dbtype");// 同步表类型
        if (this.sync_basetype == null || this.sync_basetype.length() <= 0) {
            isCorrect = false;
        }
        
        this.sync_pass = (String) bean.get("pwd");// 同步表密码
        if (this.sync_pass == null || this.sync_pass.length() <= 0) {
            isCorrect = false;
        }
        
        this.sync_user = (String) bean.get("user");// 同步表用户名
        if (this.sync_user == null || this.sync_user.length() <= 0) {
            isCorrect = false;
        }
        
        if (!isCorrect) {
            isAllowSync();
        }
        return isCorrect;
    }
    
    /**
     * 判断连接是否成功
     * @return
     */
    private boolean isConnection()
    {
        boolean isCorrect=false;        
        try
        {   String driverClassName="";
            String url="";
            if(this.sync_basetype!=null&&("mssql".equalsIgnoreCase(this.sync_basetype)|| "SQLSERVER".equalsIgnoreCase(sync_basetype)))
            {
                driverClassName="com.microsoft.sqlserver.jdbc.SQLServerDriver";
                url="jdbc:sqlserver://"+this.sync_url+":"+this.sync_post+";databaseName="+this.sync_base+"";
            }else if(this.sync_basetype!=null&& "oracle".equalsIgnoreCase(this.sync_basetype))
            {
                driverClassName="oracle.jdbc.driver.OracleDriver";
                url="jdbc:oracle:thin:@"+this.sync_url+":"+this.sync_post+":"+this.sync_base+"";
            } else if (this.sync_basetype!=null&& "mysql".equalsIgnoreCase(this.sync_basetype)) {
                driverClassName="com.mysql.jdbc.Driver";
                url="jdbc:mysql://"+this.sync_url+":"+this.sync_post+"/"+this.sync_base+"";
            } else {
                return false;
            }
            
            Class.forName(driverClassName).newInstance();
            System.out.println("同步驱动地址"+url);
            cat.error("同步驱动地址"+url);            
            cat.debug("同步驱动地址"+url); 
            this._syConn=DriverManager.getConnection(url,this.sync_user,this.sync_pass);
            isCorrect=true;
        }
        catch(ClassNotFoundException ee)
        {
            ee.printStackTrace();
        }
        catch(Exception ee)
        {
        	//如果是mysql且连接失败,则尝试指定字符集UTF-8重新链接
        	 if (this.sync_basetype!=null&& "mysql".equalsIgnoreCase(this.sync_basetype)) {
        		String msqlUrlWithEncoding="jdbc:mysql://"+this.sync_url+":"+this.sync_post+"/"+this.sync_base+"";
        		//解决别名无法取到的问题
        		msqlUrlWithEncoding+="?useOldAliasMetadataBehavior=true";
                //字符集编码问题,解决com.mysql.jdbc.exceptions.MySQLSyntaxErrorExcepteion:Unknown character set 'macce'
        		msqlUrlWithEncoding+="&useunicode=true&characterEncoding=UTF-8";
        		try {
					this._syConn=DriverManager.getConnection(msqlUrlWithEncoding,this.sync_user,this.sync_pass);
					 isCorrect=true;
				} catch (SQLException e) {
					e.printStackTrace();
				}
 				
        	 }else{
        		 ee.printStackTrace();
        	 }
            
        }           
        return isCorrect;
    }
    
    /**
     * 同步刷卡数据
     * @param start_date
     * @param start_time
     * @param end_date
     * @param end_time
     * @return
     * @throws GeneralException
     * @throws Exception
     */
    public boolean sycnCardData(String start_date,String start_time,String end_date,String end_time)throws GeneralException, Exception
    {
        boolean isCorrect = false;
        
        UserView uv = new UserView("syncKq", this.conn);
        uv.setUserFullName("syncKq");
        uv.setUserName("syncKq");
        uv.canLogin(false);
        
        ArrayList dblist = null;
        String cardno = null;
        String temp_Tab = null;
        
        KqCardData kqCardData = new KqCardData(uv, this.conn);
        
        try{
            if(isConnection())
            {
                System.out.println("数据连接正常");
                cat.error("数据连接正常");            
                cat.debug("数据连接正常");        
                
                String originality_Tab = "kq_originality_data";
                temp_Tab = kqCardData.createTemp(originality_Tab);
                
                System.out.println("创建临时表成功表名为+" + temp_Tab);
                cat.error("创建临时表成功表名为+" + temp_Tab);
                cat.debug("创建临时表成功表名为+" + temp_Tab); 
                
                getSyncCardData(start_date,start_time,end_date,end_time, temp_Tab, kqCardData);
                
                KQ_Parameter kq_paramter = new KQ_Parameter();
                String xmlContent = kq_paramter.search_KQ_PARAMETER(conn);
                ArrayList kq_conlist = kq_paramter.ReadParameterXml(xmlContent);    
                if(kq_conlist!=null && kq_conlist.size()>0)
                {
                    if(start_date!=null && start_date.length()>0) {
                        start_date=start_date.replaceAll("-","\\.");
                    }
                     
                    if(end_date!=null && end_date.length()>0) {
                        end_date=end_date.replaceAll("-","\\.");
                    }
                        
                    cardno = kq_conlist.get(0).toString();
                     
                    dblist = RegisterInitInfoData.getUNDase(this.conn);
                    isCorrect = kqCardData.insert_Sync_kq_originality_data2(dblist,cardno, temp_Tab);
                     
                    //检查刷卡数据重复情况，如有重复，通知相关人员
                    if (kqCardData.checkRepeatCards(dblist, cardno, temp_Tab)) {
                        sendSyncExceptMsg(kqCardData);
                    }
                       
                     System.out.println("考勤刷卡数据同步完毕");
                }else
                {
                    System.out.println("没有得到考勤参数");
                    cat.error("没有得到考勤参数");          
                    cat.debug("没有得到考勤参数");  
                    throw GeneralExceptionHandler.Handle(new GeneralException("没有得到考勤参数！"));
                }
            }else
            {
                cat.error("数据连接不正常");           
                cat.debug("数据连接不正常");       
                System.out.println("数据连接不正常");
                
                sendMail("无法连接刷卡数据视图所在数据库!");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            
            //检查刷卡数据重复情况，如有重复，通知相关人员
            if (kqCardData.checkRepeatCards(dblist, cardno, temp_Tab)) {
                sendSyncExceptMsg(kqCardData);
            }
        }finally
        {
            KqUtilsClass.dropTable(this.conn, temp_Tab);
            KqUtilsClass.closeDBResource(_syConn);
        }           
        return isCorrect;
    }
    
    /**
     * 发送刷卡数据同步异常信息（暂时只发送重复卡号情况）
     * @Title: sendSyncExceptMail   
     * @Description:    
     * @param kqCardData
     */
    private void sendSyncExceptMsg(KqCardData kqCardData) {
        ArrayList repeatCards = kqCardData.getRepeatCards();
        if (0 == repeatCards.size()) {
            return;
        }
        
        try {
            StringBuffer mailContent = new StringBuffer();
            mailContent.append("异常名称：<strong><red>考勤卡号重复分配</red></strong><br>");
            mailContent.append("异常详情：以下考勤卡号重复分配给了多人，请及时前往ehr系统检查处理<br>");
            
            //将卡号重复情况记录到日志、打印到控制台
            cat.error("以下考勤卡号重复分配给了多人，请及时前往ehr系统检查处理");          
            cat.debug("以下考勤卡号重复分配给了多人，请及时前往ehr系统检查处理");       
            System.out.println("以下考勤卡号重复分配给了多人，请及时前往ehr系统检查处理");
            
            for (int i = 0; i < repeatCards.size(); i++) {
                mailContent.append(repeatCards.get(i)).append("<br>");
                
                cat.error(repeatCards.get(i));          
                cat.debug(repeatCards.get(i));       
                System.out.println(repeatCards.get(i));
            }
            
            sendMail(mailContent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendMail(String msg) {
        if (null == mailto || "".equals(mailto)) {
            return;
        }
        
        try {
            //发送异常情况邮件给后台作业参数中指定的邮箱
            EMailBo bo = new EMailBo(this.conn,true,"");
            try {
                String mailMsg = "您好！<br><br>同步刷卡数据发生异常情况，请及时处理！<br><br>";
                bo.sendEmail("刷卡数据同步异常！", mailMsg + msg, "", "", mailto);
            } finally {
                bo.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     * @param start_date
     * @param start_time
     * @param end_date
     * @param end_time
     * @return
     */
    private ArrayList getSyncCardData(String start_date,String start_time,String end_date,String end_time, String temp_Tab, KqCardData kqCardData)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        
        StringBuffer sqlstr=new StringBuffer();
        ArrayList syncList=new ArrayList();
        ResultSet rs = null;
        Statement stmt = null;      
        try {    
            start_date=start_date.replaceAll("-", ".");
            end_date=end_date.replaceAll("-", ".");
            
            DbWizard dbw = new DbWizard(this._syConn);
            
            //是否有cardtime字段，日期型存放的刷卡时间
            boolean haveCardTimeField = false;
            haveCardTimeField = dbw.isExistField(this.sync_table, "CardTime", false);
            
            //是否存在补刷卡原因字段
            boolean haveCardCauseField = dbw.isExistField(this.sync_table, "cause", false);
            kqCardData.setSyncCardCause(haveCardCauseField);
            
            sqlstr.append("select distinct ");
            
            if (dbw.isExistField(this.sync_table,"ScopeID", false)){
                sqlstr.append(" (ScopeID + StaffID) StaffID,");
            }else{
                sqlstr.append(" StaffID,");
            }
            
            if(!haveCardTimeField) {
                sqlstr.append("DataDate,DataTime,");
            } else {
                sqlstr.append("CardTime,");
            }
            
            if(haveCardCauseField) {
                sqlstr.append("cause,");
            }
            
            sqlstr.append("CrDoorNo from " + this.sync_table);
            sqlstr.append(" where 1=1");
            
            //zxj 20160909 增加条件，只取符合格式要求的数据
            if(!haveCardTimeField) {
                //oracle mysql
                String lenFunc = "length";
                if(this.sync_basetype!=null && ("mssql".equalsIgnoreCase(this.sync_basetype) || "SQLSERVER".equalsIgnoreCase(sync_basetype)))
                {
                    lenFunc = "len";
                }
                //sqlserver mysql
                String subStrFunc = "SUBSTRING";
                if (this.sync_basetype!=null && "oracle".equalsIgnoreCase(this.sync_basetype)) {
                    subStrFunc = "SUBSTR";
                }
                
                sqlstr.append(" and ").append(lenFunc).append("(DataDate)=10");
                sqlstr.append(" and ").append(lenFunc).append("(DataTime)=5");
                //日期合法性
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,1,4)>'2000'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,1,4)<'2100'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,5,1)='.'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,6,2)<='12'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,6,2)>='01'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,8,1)='.'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,9,2)<='31'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataDate,9,2)>='01'");
                //时间合法性
                sqlstr.append(" and ").append(subStrFunc).append("(DataTime,1,2)<='23'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataTime,1,2)>='00'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataTime,4,2)<='59'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataTime,4,2)>='00'");
                sqlstr.append(" and ").append(subStrFunc).append("(DataTime,3,1)=':'");
                
                //日期、时间是两个字段的，分别比较日期和时间字符串
                if(!end_date.equals(start_date)) {
                    sqlstr.append(" and ((DataDate='"+start_date+"'");
                    sqlstr.append(" and DataTime>='"+start_time+"')");
                    sqlstr.append(" or (DataDate='"+end_date+"'");
                    sqlstr.append(" and DataTime<='"+end_time+"')");
                    sqlstr.append(" or (DataDate>'"+start_date+"' and DataDate<'"+end_date+"'))");
                 } else {
                     sqlstr.append(" and DataDate>='"+start_date+"'");
                     sqlstr.append(" and DataTime>='"+start_time+"'");
                     sqlstr.append(" and DataDate<='"+end_date+"'");
                     sqlstr.append(" and DataTime<='"+end_time+"'");
                 }
            } else { //日期、时间是一个字段的，直接比较日期类型数据
                if(start_time.length()==5) {
                    start_time = start_time + ":00";
                }
                
                if(end_time.length()==5) {
                    end_time = end_time + ":59";
                }
                
                String starttimeStr = start_date + " " + start_time;
                String endtimeStr = end_date + " " + end_time;
                sqlstr.append(" and CardTime>=");
                if ("mssql".equalsIgnoreCase(this.sync_basetype) || "SQLSERVER".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("'").append(starttimeStr).append("'");
                } else if ("oracle".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("TO_DATE('" + starttimeStr + "', 'YYYY-MM-DD HH24:MI:SS')");
                } else if ("mysql".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("str_to_date('"+ starttimeStr + "', '%Y.%m.%d %H:%i:%s')");
                }
                
                sqlstr.append(" and CardTime <= ");
                if ("mssql".equalsIgnoreCase(this.sync_basetype) || "SQLSERVER".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("'").append(endtimeStr).append("'");
                } else if ("oracle".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("TO_DATE('" + endtimeStr + "', 'YYYY-MM-DD HH24:MI:SS')");
                } else if ("mysql".equalsIgnoreCase(this.sync_basetype)) {
                    sqlstr.append("str_to_date('"+ endtimeStr + "', '%Y.%m.%d %H:%i:%s')");
                }
            }
            
            System.out.println("考勤同步取的数据"+sqlstr.toString());
            cat.error("考勤同步取的数据"+sqlstr.toString());    
            if (cat.isDebugEnabled()) {
                cat.debug("考勤同步取的数据"+sqlstr.toString());
            }
            
            stmt = this._syConn.createStatement();
            rs = stmt.executeQuery(sqlstr.toString());    
            int count = 0;
            String strCardTime = "";
            String strCardCause = "";
            while(rs.next())
            {
                ArrayList list =new ArrayList();//a0100,nbase,machine_no,card_no,work_date,work_time
                list.add("A");
                list.add("A");
                list.add(rs.getString("CrDoorNo") != null ? rs.getString("CrDoorNo").trim() : null);             
                list.add(rs.getString("StaffID") != null ? rs.getString("StaffID").trim() : null);  
                
                //日期和时间分放到两个字段里
                if(!haveCardTimeField)
                {
                    list.add(rs.getString("DataDate").trim() != null ? rs.getString("DataDate").trim() : null);
                    list.add(rs.getString("DataTime").trim() != null ? rs.getString("DataTime").trim() : null);
                }
                else//日期和时间放在一个日期型字段里
                {
                    strCardTime = dateFormat.format(rs.getTimestamp("CardTime"));
                    list.add(strCardTime.substring(0, 10));
                    list.add(strCardTime.substring(11));
                }
                
                //刷卡原因
                if (haveCardCauseField) {
                    strCardCause = rs.getString("cause");
                    if (strCardCause != null && strCardCause.trim().length() > 250) {
                        strCardCause = strCardCause.trim().substring(0, 250);
                    }
                    list.add(strCardCause);
                }
                
                syncList.add(list);
                
                if (syncList.size() >= 5000) {
                    kqCardData.inser_kq_originality_data_temp(syncList,temp_Tab);
                    count += syncList.size();
                    syncList.clear();
                    System.out.println("向中间表中插入了" + count + "条数据");
                }
            }
            
            if (syncList.size() > 0) {
                kqCardData.inser_kq_originality_data_temp(syncList,temp_Tab);
                count += syncList.size();
                syncList.clear();
                System.out.println("向中间表中插入了" + count + "条数据，插入完毕！");
            }
            
            if(count>0)
            {
                cat.error("考勤同步取的数据"+count);            
                if (cat.isDebugEnabled()) {
                    cat.debug("考勤同步取的数据"+count);
                }
                System.out.println("考勤同步取的数据"+count);
            }             
            else
            {
                System.out.println("考勤同步没有取得数据");   
            }
               
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("考勤同步取的数据"+e.toString());
            cat.error("考勤同步失败："+e.toString());          
            if (cat.isDebugEnabled()) {
                cat.debug("考勤同步失败："+e.toString());
            }
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
            KqUtilsClass.closeDBResource(stmt);
            KqUtilsClass.closeDBResource(_syConn);
            
        }
        return syncList;
    }
    
    /**
     * 
     * @param name
     * @param code
     * @return
     */
    private String getContent(String name,String code)
    {
        StringBuffer sql=new StringBuffer();
        sql.append("select content,status from kq_parameter where ");
        sql.append(" upper(name)='"+name.toUpperCase()+"' and b0110='UN'");
        ContentDAO dao=new ContentDAO(this.conn);
        String content="";
        RowSet rs=null;
        try
        {
            rs=dao.search(sql.toString());
            if(rs.next())
            {
                content=rs.getString("content");
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }finally
        {
            KqUtilsClass.closeDBResource(rs);
        }
        return content;
    }
    
    public void setMailTo(String mailTo) {
        this.mailto = mailTo;
    }
}
