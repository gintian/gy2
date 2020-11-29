package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 未刷卡人员
 *<p>Title:SearchNoCardDataTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Sep 11, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class SearchNoCardDataTrans  extends IBusiness {

    public void execute() throws GeneralException 
	{
        ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
        String a_code=(String)this.getFormHM().get("a_code");
	    String start_date=(String)this.getFormHM().get("start_date");
	    String start_hh=(String)this.getFormHM().get("start_hh");
	    String start_mm=(String)this.getFormHM().get("start_mm");
	    String end_date=(String)this.getFormHM().get("end_date");
	    String end_hh=(String)this.getFormHM().get("end_hh");
	    String end_mm=(String)this.getFormHM().get("end_mm");	
	    String sp_flag=(String)this.getFormHM().get("sp_flag");
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		this.getFormHM().put("select_flag",select_flag);
		this.getFormHM().put("select_name",select_name);
	    if(datelist!=null&&datelist.size()>0)
	    {
	    	 CommonData vo=(CommonData)datelist.get(0);	  	  
	    	if(start_date==null||start_date.length()<=0)
				start_date=vo.getDataValue();	
			if(end_date==null||end_date.length()<=0)
				end_date=vo.getDataValue();	
	    }else
	    {
	    	if(start_date==null||start_date.length()<=0)
				start_date=PubFunc.getStringDate("yyyy.MM.dd");	
			if(end_date==null||end_date.length()<=0)
				end_date=PubFunc.getStringDate("yyyy.MM.dd");	
	    }
	    if(start_hh==null||start_hh.length()<=0)
			start_hh="00";
		if(start_mm==null||start_mm.length()<=0)
			start_mm="00";		
		if(end_hh==null||end_hh.length()<=0)
			end_hh="59";
		if(end_mm==null||end_mm.length()<=0)
			end_mm="59";
		String start_tiem=start_hh+":"+start_mm;
		String end_tiem=end_hh+":"+end_mm;
	    ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");	 
	    String select_pre=(String)this.getFormHM().get("select_pre");		
		if(select_pre==null||select_pre.length()<=0)
		{
			if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
				select_pre=kq_dbase_list.get(0).toString();
		}
		ArrayList sql_db_list=new ArrayList();
		if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		{
			sql_db_list.add(select_pre);
		}else
		{
			sql_db_list=kq_dbase_list;
		}
		this.getFormHM().put("select_pre",select_pre);
		KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
        
        if(kq_dbase_list==null||kq_dbase_list.size()<=0)
    		throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义人员库","",""));
        KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		String where_c=kqUtilsClass.getWhere_C("1","a0101",select_name);
		String sqlstr="";
		KqParameter kq_paramter = new KqParameter(this.getFormHM(),this.userView,this.userView.getUserOrgId(),this.getFrameconn());
		String kq_type=kq_paramter.getKq_type();
		sqlstr=kqCardData.getNoCardSQL(sql_db_list,a_code,start_date,end_date,start_tiem,end_tiem,where_c,kq_type); 
    	this.getFormHM().put("column","nbase,a0100,a0101,b0110,e0122,e01a1,a0000");
	    this.getFormHM().put("sqlstr",sqlstr);
	    this.getFormHM().put("orderby","order by i,b0110,e0122,a0000"); 	    
	    this.getFormHM().put("a_code",a_code);
	    this.getFormHM().put("start_date",start_date);
	    this.getFormHM().put("start_hh",start_hh);
	    this.getFormHM().put("start_mm",start_mm);
	    this.getFormHM().put("end_date",end_date);
	    this.getFormHM().put("end_hh",end_hh);
	    this.getFormHM().put("end_mm",end_mm);
	    //岗位是否隐藏
	    KqParameter para = new KqParameter();
	    if ("1".equalsIgnoreCase(para.getKq_orgView_post())) 
	    	this.getFormHM().put("viewPost", "kq");
	    else
	    	this.getFormHM().put("viewPost", "");
	}
}
