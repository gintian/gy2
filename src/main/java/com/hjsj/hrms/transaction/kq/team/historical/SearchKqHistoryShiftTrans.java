package com.hjsj.hrms.transaction.kq.team.historical;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.options.kq_class.KqClassConstant;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterDate;
import com.hjsj.hrms.businessobject.kq.team.historical.KqShiftClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 排班历史记录
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Feb 27, 2008</p> 
 *@author LanShiYe
 *@version 4.0
 */
public class SearchKqHistoryShiftTrans extends IBusiness implements KqClassConstant{
	public void execute() throws GeneralException
	{
	   String a_code=(String)this.getFormHM().get("a_code");	
	   //xiexd 2014.09.19历史排班
       if(a_code!=null&&!"".equals(a_code))
       {
         	String a_str = a_code.substring(0, 2);
       	if(!"UN".equals(a_str)&&!"@K".equals(a_str)&&!"UM".equals(a_str)&&!"EP".equals(a_str)&&!"GP".equals(a_str))
       	{            	
       		a_code = PubFunc.decrypt(a_code);
       	}
       }
	   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
	   String nbase=(String)hm.get("nbase");
	   String select_flag=(String)this.getFormHM().get("select_flag");
	   String select_name=(String)this.getFormHM().get("select_name");
	   if("请输入姓名、工号或考勤卡号".equalsIgnoreCase(select_name))
       	select_name="";
	   this.getFormHM().put("select_flag",select_flag);
	   this.getFormHM().put("select_name",select_name);
	   String select_pre=(String)this.getFormHM().get("select_pre");
	   if(select_pre==null||select_pre.length()<=0)
		   select_pre="all";	   
	   ArrayList session_y_list=new ArrayList();
	   if(session_y_list==null||session_y_list.size()<=0)
	   {
		   session_y_list=RegisterDate.arcYaer(this.getFrameconn(),"1");
	   }
	   if(session_y_list==null||session_y_list.size()<=0)
		   throw GeneralExceptionHandler.Handle(new GeneralException("人员排班信息无归档数据！"));
	   String session_y=(String)this.getFormHM().get("session_y");
	   String session_y_old=(String)this.getFormHM().get("session_y_old");
	   
	   boolean flag=true;
	   for(int i=0;i<session_y_list.size();i++){
		   CommonData vo=(CommonData)session_y_list.get(i);
		   if(session_y.equals(vo.getDataName())){
			   flag=false;  
		   }
	   }	   
	   if(flag)
	   {
		  CommonData vo=(CommonData)session_y_list.get(0);
		  session_y=vo.getDataName();
	   }
	   if(session_y_old==null||session_y_old.length()<=0)
		   session_y_old="";
	   ArrayList duration_list=RegisterDate.arcDuration(this.getFrameconn(),session_y,"1");
	   if(duration_list==null||duration_list.size()<=0)
		   throw GeneralExceptionHandler.Handle(new GeneralException("没有考勤封存期间"));
	   String session_m=(String)this.getFormHM().get("session_m");
	   flag=true;
	   for(int i=0;i<duration_list.size();i++){
		   CommonData vo=(CommonData)duration_list.get(i);
		   if(session_m.equals(vo.getDataName())){
		       i=duration_list.size();
		       flag=false;
		   }   
	   }
	   if(flag){
		   CommonData vo=(CommonData)duration_list.get(0);
		   session_m=vo.getDataName();
	   }
	   session_y_old=session_y;
	   String cur_date=session_y+"-"+session_m;
	   this.getFormHM().put("session_y_list",session_y_list);//封存年list
	   this.getFormHM().put("duration_list",duration_list);//封存年list
	   this.getFormHM().put("session_y", session_y);//年
	   this.getFormHM().put("session_m", session_m);//期间
	   this.getFormHM().put("session_y_old", session_y_old);//校验是否年换了
       ArrayList  datelist=RegisterDate.getOneSealDurationDateList(this.getFrameconn(),cur_date,"1");
       if(a_code==null||a_code.length()<=0)
       {
    	   a_code="UN";
       }
       KqShiftClass kqShiftClass=new KqShiftClass(this.getFrameconn(),this.userView);
       String state=(String)this.getFormHM().get("state");
       if(state==null||state.length()<=0)
    	   state="0";
       String table_html="";
       ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());
	   String b0110=managePrivCode.getPrivOrgId();  
	   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(), this.userView);
	   
	   ArrayList kq_dbase_list = kqUtilsClass.getKqPreList();
	   //取输入查询条件
       String where_c = kqShiftClass.getSelWhere(select_flag, select_name, nbase);
	   kqShiftClass.setWhere_c(where_c);
	   
	   String code=getCodeFormA_code(a_code);
       String kind=getKindFormA_code(a_code);
	   String select_a0100 = "";
       String select_nbase = "";
       if("0".equals(state))
       {
    	   if(nbase==null||nbase.length()<=0)
           {
        	   
        	   if(select_flag!=null&& "1".equals(select_flag))
      		   {
        		   nbase=select_pre;
      		   }else
      		   {
      			  ArrayList kq_base_list=RegisterInitInfoData.getB0110Dase(this.formHM,this.userView,this.getFrameconn(),b0110);
      		      if(kq_base_list!=null&&kq_base_list.size()>0)
         	      {
         		    nbase=kq_base_list.get(0).toString();
         	      }else
         	      {
         		   throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.dbase.nosave"),"",""));
         	      }
      		   }
           }else
           {
        	   if(select_flag!=null&& "1".equals(select_flag))
      		   {
        		   if(select_pre!=null&&select_pre.length()>0&&!"0".equals(select_pre)&&!"all".equals(select_pre))
        		   {
        			   nbase=select_pre;
        		   }
      		   }
           }
    	   // 57205 表格方式支持查询姓名
    	   if (!"".equals(select_name) && select_flag != null && "1".equals(select_flag)) {
               ArrayList nbaseList = new ArrayList();
               if ("all".equalsIgnoreCase(select_pre)) {
                   nbaseList.addAll(kq_dbase_list);
               } else {
                   nbaseList.add(select_pre);
               }
               com.hjsj.hrms.businessobject.kq.team.KqShiftClass kqShiftClassTeam = new com.hjsj.hrms.businessobject.kq.team.KqShiftClass(this.getFrameconn(), this.userView);
               ArrayList list = kqShiftClassTeam.getUserRecord(select_name, kq_dbase_list, code, kind, "");
               if (list.size() >= 1) {
            	   String str = (String) list.get(0);
            	   select_a0100 = str.split("'")[0];
            	   select_nbase = str.split("'")[1];
               }
               if (StringUtils.isNotBlank(select_a0100) && StringUtils.isNotBlank(select_nbase)) {
                   table_html = kqShiftClass.returnShiftHtml(datelist, "EP" + select_a0100, select_nbase);
               } else {
                   table_html = kqShiftClass.returnShiftHtml(datelist, a_code, nbase);
               }
           } else {
               //点选组织机构、班组、人员时
               table_html = kqShiftClass.returnShiftHtml(datelist, a_code, nbase);
           }
    	   //table_html=kqShiftClass.returnShiftHtml(datelist,a_code,nbase);
       }else
       {
    	   String curpage=(String)this.getFormHM().get("curpage");
    	   if(curpage==null||curpage.length()<=0)
   		      curpage="1";
    	   int cp=Integer.parseInt(curpage);
    	   ArrayList db_list=new ArrayList();    	   
    	   if(select_flag!=null&& "1".equals(select_flag)&&!"all".equals(select_pre)&&!"0".equals(select_pre))
  		   {
    		   db_list.add(select_pre); 
  		   }else
  		   {
  			 if(nbase==null||nbase.length()<=0)
          	   db_list=RegisterInitInfoData.getB0110Dase(this.formHM,this.userView,this.getFrameconn(),b0110);
             else
          	   db_list.add(nbase); 
  		   }
    	   //29614 linbz 记录模式下where_c是传的需要查询的值，然后单独拼接sql
           kqShiftClass.setWhere_c(select_name);
    	   table_html=kqShiftClass.returnRecordHtml(datelist,a_code,db_list,cp,10);
       }
       this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
       this.getFormHM().put("state",state);
       this.getFormHM().put("table_html",table_html);
       this.getFormHM().put("a_code",a_code);
       this.getFormHM().put("nbase",nbase);
       this.getFormHM().put("datelist",datelist);
       // 32132 查询历史数据也增加 人员详细信息
       if (StringUtils.isNotBlank(select_a0100) && StringUtils.isNotBlank(select_nbase)) {
           this.getFormHM().put("code_mess", kqUtilsClass.getACodeDesc("EP" + select_a0100, select_nbase));
       } else {
           this.getFormHM().put("code_mess", kqUtilsClass.getACodeDesc(a_code, nbase));
       }
	
	}
	
    public String getCodeFormA_code(String a_code)
    {
   	 if(a_code==null||a_code.length()<=0)
   		 a_code="UN";
   	 String code="";
   	 if(a_code.indexOf("UN")!=-1||a_code.indexOf("UM")!=-1||a_code.indexOf("@K")!=-1) 
   		if(a_code.length()>2)
   		{
   			code=a_code.substring(2);
   		}
   	 return code;
    }
    public String getKindFormA_code(String a_code)
    {
   	 if(a_code==null||a_code.length()<=0)
   		 a_code="UN";
   	 String kind="";
   	 if(a_code.indexOf("UN")!=-1)
   		 kind="2";
   	 else if(a_code.indexOf("UM")!=-1)
   	 {
   		 kind="1";
   	 }else if(a_code.indexOf("@K")!=-1)
   	 {
   		 kind="0";
   	 }
   	 return kind;
    }
}
