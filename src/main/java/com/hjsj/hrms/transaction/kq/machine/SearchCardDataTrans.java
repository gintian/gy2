package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.machine.KqCardData;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 刷卡数据
 * <p>Title:SearchCardDataTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Dec 19, 2006 4:30:22 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SearchCardDataTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		try {
    		String cur_flag=(String)this.getFormHM().get("cur_flag");
    		//zxj 20150825 安全漏洞
    	    String a_code=(String)this.getFormHM().get("a_code");
    	    if (null != a_code && !"".equals(a_code)
    	            && !a_code.startsWith("UN") && !a_code.startsWith("UM") && !a_code.startsWith("@K") 
    	            && !a_code.startsWith("GP") && !a_code.startsWith("EP"))
    	        a_code = PubFunc.decryption(a_code);
    	    
    	    ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
    	    String start_date=(String)this.getFormHM().get("start_date");
    	    String start_hh=(String)this.getFormHM().get("start_hh");
    	    String start_mm=(String)this.getFormHM().get("start_mm");
    	    String end_date=(String)this.getFormHM().get("end_date");
    	    String end_hh=(String)this.getFormHM().get("end_hh");
    	    String end_mm=(String)this.getFormHM().get("end_mm");	
    	    String sp_flag=(String)this.getFormHM().get("sp_flag");
    		String select_flag=(String)this.getFormHM().get("select_flag");
    		String select_name=(String)this.getFormHM().get("select_name");
    		String select_type = (String)this.getFormHM().get("select_type");
    		String into_flag=(String)this.getFormHM().get("into_flag");
    		String datafrom=(String)this.getFormHM().get("datafrom");
    		String nbase=(String)this.getFormHM().get("nbase");
    		String iscommon=(String)this.getFormHM().get("iscommon");//接收签到点信息
    		//System.out.println("提交到业务中的iscommon的值："+iscommon);
    		String return_start_date = (String) this.getFormHM().get("return_start_date");
    		String return_end_date = (String) this.getFormHM().get("return_end_date");
    		if(return_start_date != null && return_start_date.length() > 0)
    			start_date = return_start_date;
    		if (return_end_date != null && return_end_date.length() > 0)
    			end_date = return_end_date;
    		this.getFormHM().remove("return_start_date");
    		this.getFormHM().remove("return_end_date");
    		
    		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
    		String view ="0";
    		if(hm.get("view")!=null){
    			this.getFormHM().put("view", hm.get("view"));
    			view = (String)this.getFormHM().get("view");
    			hm.remove("view");
    		}
    		this.getFormHM().put("view", view);
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
    	    ArrayList list = RegisterDate.getKqDayList(this.getFrameconn());
    	    String start_d = list.get(0).toString().replace(".", "-");
    	    if("1".equals(view))
    	    	start_date = start_d;
    	    if(start_hh==null||start_hh.length()<=0)
    			start_hh="00";
    		if(start_mm==null||start_mm.length()<=0)
    			start_mm="00";		
    		if(end_hh==null||end_hh.length()<=0)
    			end_hh="23";
    		if(end_mm==null||end_mm.length()<=0)
    			end_mm="59";
    		String start_time=start_hh+":"+start_mm;
    		String end_time=end_hh+":"+end_mm;
    	    ArrayList kq_dbase_list=(ArrayList)this.getFormHM().get("kq_dbase_list");	 
    	    String select_pre=(String)this.getFormHM().get("select_pre");		
    		if(select_pre==null||select_pre.length()<=0)
    		{
    			KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    			ArrayList kq_db_list=kqUtilsClass.getKqNbaseList(kq_dbase_list);
    			if(kq_db_list!=null&&kq_db_list.size()>0)
    			{
    				CommonData dd =(CommonData)kq_db_list.get(0);
    				select_pre=dd.getDataValue();
    			}
    		}
    		ArrayList sql_db_list=new ArrayList();
    		if(a_code.indexOf("EP")!=-1)
    		{
    			sql_db_list.add(nbase);
    		}else
    		{
    			if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
    			{
    				sql_db_list.add(select_pre);
    			}else
    			{
    //				sql_db_list.add(kq_dbase_list.get(0));
    				sql_db_list = kq_dbase_list;
    			}
    		}
    		if ("1".equals(view)) {
    			select_pre = "all";
    			sql_db_list = kq_dbase_list;
    		}
    		
    		this.getFormHM().put("select_pre",select_pre);
    		/**********
    		 * cur_flag=1 查看刷卡数据
    		 * cur_flag=2 查看未刷卡人员
    		 * cur_flag=3 查看外出人员
    		 * **************/
            if(cur_flag==null||cur_flag.length()<=0)
            	cur_flag="1";
            KqCardData kqCardData=new KqCardData(this.userView,this.getFrameconn());
    		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
    		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
    		HashMap hashmap = para.getKqParamterMap();
    		String g_no = (String) hashmap.get("g_no");
    //		String cardno = (String) hashmap.get("cardno");
    		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
            if(kq_dbase_list==null||kq_dbase_list.size()<=0)
        		throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.nbase.no"),"",""));
            
            KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    		String where_c = "";
    		if("0".equals(select_type)){
    			where_c=kqUtilsClass.getWhere_C("1","a0101",select_name);
    		}else if("1".equals(select_type)){
    			where_c=kqUtilsClass.getWhere_C("1",g_no,select_name);
    		}else{
    			where_c=kqUtilsClass.getWhere_C("1","card_no",select_name);
    		}
    		
    		if(sp_flag==null||sp_flag.length()<=0){
    		    if(!"1".equals(view))
    		        sp_flag="all";
    		    else{
    		        sp_flag="02";
    		        this.getFormHM().put("sp_flag", sp_flag);
    		    }
    		}else if (sp_flag != null && !"".equals(sp_flag) && "1".equals(view)) 
    		{
    			sp_flag="02";
    	        this.getFormHM().put("sp_flag", sp_flag);
    		}
    		
    		if("02".equals(sp_flag))
    			where_c=where_c+" and sp_flag='02'";
    		else if("03".equals(sp_flag))
    			where_c=where_c+" and sp_flag='03'";
    	    else if("07".equals(sp_flag))
    	    	where_c=where_c+" and sp_flag='07'";
    		
    		if(into_flag!=null&&into_flag.length()>0&&!"all".equals(into_flag))
    		{
    			where_c=where_c+" and " + Sql_switcher.isnull("inout_flag", "0") + "='" + into_flag + "'";
    		}
    		
    		boolean haveIscommon = kqCardData.showIScommon();
    		//将签到点信息加入查询的SQL中
    		if(iscommon!=null && iscommon.length()>0 && !"all".equals(iscommon))
    		{
    			if(haveIscommon)
    			{
    				where_c = where_c + " and " + Sql_switcher.isnull("iscommon", "1") + "='" + iscommon + "'";
    				
    				if ((Constant.MSSQL == Sql_switcher.searchDbServer()) && "1".equals(iscommon)) {
    					where_c = where_c + " OR iscommon=''";
    				}
    			}
    		}
    		
    		if(datafrom!=null&&datafrom.length()>0&&datafrom.indexOf("1")!=-1&&!"2".equals(cur_flag))
    			where_c=where_c+" and datafrom='1'";
    		
    		if ("11".equals(datafrom)) 
    		{
    			where_c += " and (location like '%补刷%' or location like '%补签%')";
    		}
    		
    		String sqlstr="";
    		if("1".equals(cur_flag))
                sqlstr=kqCardData.getSQL1(sql_db_list,a_code,start_date,end_date,start_time,end_time,where_c,""); 		
    		else if("2".equals(cur_flag))
    		{
    			sqlstr=kqCardData.getOutEmpSql1(sql_db_list,a_code,start_date,end_date,start_time,end_time,where_c);
    		}
    		
    		String inoutCardCountInfo = kqCardData.colectInOutemps(sql_db_list,a_code,start_date,end_date,start_time,end_time,where_c);
            this.getFormHM().put("inout_str", inoutCardCountInfo);
            
            String column=kqCardData.getKq_originality_column2();
            this.getFormHM().put("column",column);        
            this.getFormHM().put("sqlstr",sqlstr);
            this.getFormHM().put("orderby","order by b0110,e0122,e01a1,a0100,work_date,work_time"); 
            this.getFormHM().put("cur_flag",cur_flag);
            this.getFormHM().put("a_code",a_code);
            this.getFormHM().put("start_date",start_date);
        	this.getFormHM().put("start_hh",start_hh);
        	this.getFormHM().put("start_mm",start_mm);
        	this.getFormHM().put("end_date",end_date);
        	this.getFormHM().put("end_hh",end_hh);
        	this.getFormHM().put("end_mm",end_mm);
            this.getFormHM().put("nbase", nbase);
            
            if(haveIscommon)
            {
            	 this.getFormHM().put("signs", "1");
            }else
            {
            	this.getFormHM().put("signs", "0");
            }
            this.getFormHM().put("iscommon", iscommon);//写入签到点信息
           
    		/* -----------显示部门层数-------------------------------------------------- */
    		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());       //
    	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);         //
    	    if(uplevel==null||uplevel.length()==0)                                     //
    	    		uplevel="0";                                                       //
    	    this.getFormHM().put("uplevel",uplevel);                                   //
    	    /* ------------显示部门层数------------------------------------------------- */
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
	}
}
