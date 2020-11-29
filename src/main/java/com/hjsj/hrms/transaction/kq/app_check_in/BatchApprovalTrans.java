package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.app_check_in.SearchAllApp;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
/**
 * 批量批准加班汇总
 * <p>BatchApprovalTrans</p>
 * <p>Description>:BatchApprovalTrans</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2014.1.24</p>
 * <p>@author: szk
 */
public class BatchApprovalTrans extends IBusiness {

	public void execute() throws GeneralException {
		
		ArrayList selectedinfolist=(ArrayList)this.getFormHM().get("selectedinfolist");
		RowSet rs = null;
	    StringBuffer noname=new StringBuffer(); //时间不足的人名
        try
	     {
        	 ContentDAO dao=new ContentDAO(this.getFrameconn()); 

             String para = KqParam.getInstance().getDURATION_OVERTIME_MAX_LIMIT().trim();
        	 for(int i=0;i<selectedinfolist.size();i++)
             {
        		//cheakbox选定的属性
        		    LazyDynaBean rec=(LazyDynaBean)selectedinfolist.get(i); 
        		    //加班总时长
        		    Double q1 =(Double) rec.get("q1");
        		    if (para != null && (para.length() > 0) && ((Double.valueOf(para).doubleValue()) <q1.doubleValue())) 
                    {
                 	  noname.append(rec.get("a0101").toString()+"，");
                 	  continue;
                    }
        		    String a0100=rec.get("a0100").toString();
        		    String nbase=rec.get("nbase").toString();
        	        String start_date = "";
        	        String end_date = "";
        	        /**判断考勤期间*/
        	        ArrayList kqlist = RegisterDate.getKqDayList(this.getFrameconn());
        	        if (kqlist == null || kqlist.size() <= 0) {
        	            throw new GeneralException(ResourceFactory.getProperty("error.kq.please"));
        	        }
        	        else if (kqlist != null && kqlist.size() > 0) {
        	        	//开始，结束时间为当前考勤区间
        	            start_date = kqlist.get(0).toString();
        	            end_date = kqlist.get(kqlist.size() - 1).toString();
        	            if (start_date != null && start_date.length() > 0)
        	                start_date = start_date.replaceAll("\\.", "-");
        	            if (end_date != null && end_date.length() > 0)
        	                end_date = end_date.replaceAll("\\.", "-");
        	        }
        	        
        	            //人员库sql  
        	            StringBuffer nbasewhere=new StringBuffer();
        	            nbasewhere.append(" A0100 = '"+a0100+"' and nbase ='"+nbase+"'");
        	            //加班类型
        	            StringBuffer strsql = new StringBuffer();
        	            strsql.append("select q1101 from  Q11 where Q11Z5 ='02' and");
        	            strsql.append(nbasewhere);
        	            //当前考勤期间
        	            strsql.append(" and ");
        	            SearchAllApp searchAllApp = new SearchAllApp(this.getFrameconn(), this.userView);
        	            String time = searchAllApp.getWhere2("q11", start_date, end_date, "all",  "all", "1", "0");
        	            strsql.append(time);
        	            rs=dao.search(strsql.toString());
        	            //一个人的未批单号
        	            ArrayList paralist=new ArrayList();
        	            while (rs.next()) {
        	             	String q1101 = rs.getString("q1101");
        	             	ArrayList list=new ArrayList();
        	             	list.add(q1101);
        	             	paralist.add(list);
        	            }
        	            StringBuffer buf=new StringBuffer();
        	        	 buf.append("update ");
        	        	 buf.append("q11 set state='1'");
        	        	 buf.append(" where ");
        	        	 buf.append( "q1101=?");
        	        	 //将要操作的单打上标记，即state=1
        	        	 dao.batchUpdate(buf.toString(),paralist);
        	        	 //开始批准，批准同时把state置0
        	        	 BeginApproval(dao);
        	        	  
             }
       
        }
		 catch(Exception ex)
		 {
		    ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);
		 }
		 finally {
		        KqUtilsClass.closeDBResource(rs);

		 }
		if(noname.length()>0)
		{
			noname.setLength(noname.length() - 1);
			noname.append("的加班时间超过加班限额时间，请逐条审批！");
			throw new GeneralException(noname.toString());

		}

	}
    private void BeginApproval(ContentDAO dao) throws SQLException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String strDate = sdf.format(new java.util.Date());
        String w_z7="q11z7="+Sql_switcher.dateValue(strDate); //审批时间
    	dao.update("update q11 set state='0',q11z5='03',q1113='"+this.userView.getUserFullName()+"',"+w_z7+" where state ='1'");
    }
}
