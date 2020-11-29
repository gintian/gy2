package com.hjsj.hrms.transaction.kq.register.ambiquity;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.query.CodingAnalytical;
import com.hjsj.hrms.businessobject.kq.register.CollectRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 浏览不定期汇总数据
 * 
 * */
public class SearchAmbiquityTrans extends IBusiness{
	
	   public void execute()throws GeneralException{		   
		   HashMap pamMap = (HashMap) this.getFormHM().get("requestPamaHM");
		   String code = (String) this.getFormHM().get("code");	
		   String kind = (String) this.getFormHM().get("kind");	
		   ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");	
		   String select_flag=(String)this.getFormHM().get("select_flag");
		   String select_name=(String)this.getFormHM().get("select_name");
		   String select_pre=(String)this.getFormHM().get("select_pre");
		   this.getFormHM().put("select_flag",select_flag);
		   this.getFormHM().put("select_name",select_name);
		   String whereIN = (String) pamMap.get("selectResult");
		   
		   KqUtilsClass utils = new KqUtilsClass(this.frameconn);
		   utils.addColumnToKq("q05");
		   
		   pamMap.remove("selectResult");
		   //不定期 展现如果是2 不展现不定期
		   String flag="2";
		   this.getFormHM().put("flag", flag);
		   
		   String duration=(String)this.getFormHM().get("duration");
		   if(duration==null||duration.length()<=0)
		   {
			   String kq_duration =RegisterDate.getKqDuration(this.getFrameconn());
			   duration=kq_duration.substring(0,4);
		   }		   
		   if(code==null||code.length()<=0)
		   {
				 code="";
		   }		  
		   code=code.trim();
		   
		   HashMap map=new HashMap();
		   String code_kind="";
//		   if(kind==null||kind.length()<=0)
//		   {
		   String t_kind = RegisterInitInfoData.getKindValue("",this.userView);
		   if(kind==null || kind.length() < 1 || kind.compareTo(t_kind) > 0){
			   kind = t_kind;
			   code="";
		   }
				
//		   }
		   
		   if(kind==null||kind.length()<=0)
		   {
			   kind="2";
		   }
		   
		   if("2".equals(kind))
		   {
			   code_kind=RegisterInitInfoData.getKqPrivCodeValue(userView);
		   }else 
		   {
		    	code_kind=RegisterInitInfoData.getDbB0100(RegisterInitInfoData.getKqPrivCodeValue(userView),kind,map,this.userView,this.getFrameconn()); 
		   }
		   if(code==null||code.length()<=0)
			   code=RegisterInitInfoData.getKqPrivCodeValue(userView);
		   if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		   {
			   //kq_dbase_list=userView.getPrivDbList(); 
			   kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn()); 
		   }else
		   {
			   if(code!=null&&code.length()>0)
				{
	    			if("2".equals(kind))
	    			{
	    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code);
	    			}else if(code_kind!=null&&code_kind.length()>0)
	    			{
	    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code_kind);
	    			}else{
	    				kq_dbase_list=RegisterInitInfoData.getB0110Dase(this.getFormHM(),this.userView,this.getFrameconn(),code);
	    			}
	    		}else
	    		{
	    			kq_dbase_list=RegisterInitInfoData.getDase3(this.getFormHM(),this.userView,this.getFrameconn());
	    		}
		   }
		 //** -------------------------郑文龙---------------------- 加 工号、考勤卡号
			KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
			HashMap hashmap = para.getKqParamterMap();
			String g_no = (String) hashmap.get("g_no");
			String cardno = (String) hashmap.get("cardno");
			//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);		   		   
		   try{		  
			   ArrayList fieldlist=CollectRegister.ambiFieldItemList(fielditemlist);
			   fieldlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q05", g_no, cardno, fieldlist);
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
			   /**解决不定期汇没有得到人员库**/
			   if(select_pre==null||select_pre.length()<=0)
				{
					if(kq_dbase_list!=null&&kq_dbase_list.size()>0)
						select_pre=kq_dbase_list.get(0).toString();
				}
			   this.getFormHM().put("select_pre",select_pre);
			   /**结束**/
			   KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
			   String select_type = (String)this.formHM.get("select_type");
			 
			   String where_c = "";
			   if("2".equals(select_flag)){
					where_c = " AND" + new CodingAnalytical().analytical(whereIN);
			   }else if("1".equals(select_flag)){
				   if("0".equalsIgnoreCase(select_type)){
					   where_c=kqUtilsClass.getWhere_C(select_flag,"a0101",select_name);
				   }else if("1".equalsIgnoreCase(select_type)){
					   where_c=kqUtilsClass.getWhere_C(select_flag,g_no,select_name);
				   }else if("2".equalsIgnoreCase(select_type)){
					   where_c=kqUtilsClass.getWhere_C(select_flag,cardno,select_name);
				   }
			   }
			   this.getFormHM().put("select_flag","0");
			   ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fieldlist,sql_db_list,duration,code,kind,"Q05",this.userView,"all",where_c,this.frameconn);
			   this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
			   this.getFormHM().put("sqlstr", sqllist.get(0).toString());
			   this.getFormHM().put("columns", sqllist.get(3).toString());
			   if (whereIN == null || whereIN.length() <= 0) {
				   this.getFormHM().put("strwhere", sqllist.get(1).toString());	
			   } else {
				   this.getFormHM().put("strwhere", sqllist.get(1).toString() + where_c);
			   }
			   this.getFormHM().put("orderby", sqllist.get(2).toString());
			   this.getFormHM().put("fielditemlist", fieldlist);	
			   this.getFormHM().put("duration",duration);
			   this.getFormHM().put("code",code);
			   this.getFormHM().put("kind",kind);			  
			   this.getFormHM().put("kq_dbase_list",kq_dbase_list);
			   String kq_period = "";
			   if (whereIN == null || whereIN.length() <= 0) {
				   kq_period=getKq_period(sqllist.get(0).toString(),sqllist.get(1).toString());
			   } else {
				   kq_period=getKq_period(sqllist.get(0).toString(),sqllist.get(1).toString() + where_c);
			   }
			   // 涉及SQL注入直接放进userView里
			   this.userView.getHm().put("kq_condition", "5`"+sqllist.get(4).toString());
//			   this.getFormHM().put("condition","5`"+sqllist.get(4).toString());
			   this.getFormHM().put("relatTableid","5");
			   this.getFormHM().put("returnURL","/kq/register/ambiquity/search_ambiquitydata.do?b_search=link");
			   this.getFormHM().put("kq_period",kq_period);
		    }catch(Exception e){
			      e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e); 
		   }
		    
		    // 显示部门层数
		    Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
			String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			if (uplevel == null || uplevel.length() == 0)
				uplevel = "0";
			this.getFormHM().put("uplevel",uplevel);
	}
	private String getKq_period(String sql,String where)
	{
		String sql_A=sql+" "+where;
		String kq_period="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset=dao.search(sql_A);
			if(this.frowset.next())
			{
				kq_period=this.frowset.getString("scope");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return kq_period;
	}   
}
