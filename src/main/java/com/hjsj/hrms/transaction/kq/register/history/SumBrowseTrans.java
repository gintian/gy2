package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.KqParameter;
import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.HistoryBrowse;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

public class SumBrowseTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sessiondate=(String)this.getFormHM().get("sessiondate");//考勤期间	
		String year=(String)this.getFormHM().get("year");//考勤期间
		String duration=(String)this.getFormHM().get("duration");		
		ArrayList yearlist=(ArrayList)this.getFormHM().get("yearlist");
		ArrayList durationlist=(ArrayList)this.getFormHM().get("durationlist");		
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		ArrayList sessionlist=(ArrayList)this.getFormHM().get("sessionlist");
		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");	
		String select_flag=(String)this.getFormHM().get("select_flag");
		String select_name=(String)this.getFormHM().get("select_name");
		String select_type=(String)this.getFormHM().get("select_type");
		String select_pre=(String)this.getFormHM().get("select_pre");
	    this.getFormHM().put("select_flag",select_flag);
	    this.getFormHM().put("select_type","0");
		this.getFormHM().put("select_name","");
		//转换小时 1=默认；2=HH:MM
		String selectys=(String)hm.get("selectys");
		if(selectys==null|| "".equals(selectys))
		{
			selectys="1";
		}
		this.getFormHM().put("selectys",selectys);
		
		HashMap map=new HashMap();
		HashMap maps=new HashMap();
		   String code_kind="";
		   if(kind==null||kind.length()<=0)
		   {
				kind=RegisterInitInfoData.getKindValue(kind,this.userView);
				code="";
		   }		   
		   if("2".equals(kind))
		   {
				if(code == null || "".equals(code) || "UN".equals(code)){
					code=RegisterInitInfoData.getKqPrivCodeValue(userView);
				}
		   }else 
		   {
		    	code_kind=RegisterInitInfoData.getDbB0100(RegisterInitInfoData.getKqPrivCodeValue(userView),kind,map,this.userView,this.getFrameconn()); 
		   }
		   if(code==null||code.length()<=0)
				  code=this.userView.getUserOrgId();
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
		String cur_course="";
		if(sessionlist==null||sessionlist.size()<=0)
		{
			sessionlist=RegisterDate.sessionDate(this.frameconn,"1");
		}	
		if(sessionlist!=null&&sessionlist.size()>0)
		{			
			if(sessiondate!=null&&sessiondate.length()>0)
			{
				cur_course=sessiondate;
			}else{
				CommonData vo = (CommonData) sessionlist.get(0);
				cur_course=vo.getDataValue();
			}			
		}else{
				
				throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
		}
		String cur_year="";
		String cur_duration="";
		if(cur_year==null||cur_year.length()<=0)
		{
		yearlist=RegisterDate.yearDate(this.frameconn,"1");
		if(yearlist!=null&&yearlist.size()>0)
		{			
			if(year!=null&&year.length()>0)
			{
				cur_year=year;
			}else{
				CommonData vy = (CommonData) yearlist.get(0);
				cur_year=vy.getDataValue();
			}			
		}else{
				
			  	throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
		}}else
		{
			cur_year=year;
		}
		if(cur_duration==null||cur_duration.length()<=0)
		{
		durationlist=RegisterDate.durationDate(this.frameconn, "1", cur_year);
		if(durationlist!=null&&durationlist.size()>0)
		{			
			if(duration!=null&&duration.length()>0)
			{
				cur_duration=duration;
			}else{
				CommonData vd = (CommonData) durationlist.get(0);
				cur_duration=vd.getDataValue();
			}			
		}else{
				
			throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
		}}else{
			cur_duration=duration;
		}		
		
		cur_course=cur_year.length()>0&&cur_duration.length()>0?cur_year+"-"+cur_duration:cur_course;
		
		if(code==null||code.length()<=0){
			code="";
		}
		String b0110="";
		if(code.length()<=0){
			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());			
			b0110=managePrivCode.getPrivOrgId(); //LiWeichao 如果code为空 不要改变code的值了 会影响后面的条件 后面用到时取管理范围即可
			b0110="UN"+b0110;
		}else{
			b0110="UN"+code;
				
		}
		if(kind==null||kind.length()<=0)
		{
			kind="2";
		}
		ArrayList datelist=HistoryBrowse.registerdate(b0110,this.getFrameconn(),this.userView,cur_course,"1");
		String start_date="";
		String end_date="";
		if(datelist!=null&&datelist.size()>0)
		{
				CommonData vo = (CommonData) datelist.get(0);				
				start_date=vo.getDataValue();
				vo = (CommonData) datelist.get(datelist.size()-1);
				end_date=vo.getDataValue();
		}
	    ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);
		   ArrayList fieldlist=RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);
		   FieldItem fielditem=new FieldItem();
		   fielditem.setFieldsetid("Q05");
		   fielditem.setItemdesc(ResourceFactory.getProperty("kq.register.period"));
		   fielditem.setItemid("scope");
		   fielditem.setItemtype("A");
		   fielditem.setCodesetid("0");
		   fielditem.setVisible(true);
		   fieldlist.add(fielditem); 
		   FieldItem fielditem1=new FieldItem();
		   fielditem1.setFieldsetid("Q05");
		   fielditem1.setItemdesc(ResourceFactory.getProperty("kq.register.overrule"));
		   fielditem1.setItemid("overrule");
		   fielditem1.setItemtype("A");
		   fielditem1.setCodesetid("0");
		   fielditem1.setVisible(true);
		   fieldlist.add(fielditem1); 
		   ArrayList sql_db_list=new ArrayList();
		   if(select_pre!=null&&select_pre.length()>0&&!"all".equals(select_pre))
		   {
				sql_db_list.add(select_pre);
		   }else
		   {
				sql_db_list=kq_dbase_list;
		   }
		KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		KqParameter para = new KqParameter(this.userView, "", this.getFrameconn());
		HashMap hashmap = para.getKqParamterMap();
		String g_no = (String) hashmap.get("g_no");
		String cardno = (String) hashmap.get("cardno");
		//** -------------------------郑文龙---------------------- 加 工号、考勤卡号
		fieldlist = RegisterInitInfoData.isExistsG_noAndCardno("A0101", "Q03", g_no, cardno, fieldlist);
		String where_c = null;
		if("0".equals(select_type)){
			where_c = kqUtilsClass.getWhere_C(select_flag, "a0101",
					select_name);
		} else if("1".equals(select_type)){
			where_c = kqUtilsClass.getWhere_C(select_flag, g_no,
					select_name);
		}else{
			where_c = kqUtilsClass.getWhere_C(select_flag, cardno,
					select_name);
		}
//		ArrayList sqllist = RegisterInitInfoData.getSqlstr5(fieldlist,sql_db_list,cur_course,code,kind,"Q05",this.userView,"all",where_c,this.frameconn);
		DbWizard dbWizard =new DbWizard(this.getFrameconn());
		ArrayList sqllist=null;
		if(dataInQ05(cur_course) || !dbWizard.isExistTable("Q05_arc", false)){
			sqllist = RegisterInitInfoData.getSqlstr5(fieldlist,sql_db_list,cur_course,code,kind,"Q05",this.userView,"all",where_c,this.frameconn);
		}else{
			sqllist = com.hjsj.hrms.businessobject.kq.register.history.RegisterInitInfoData.getSqlstr6(fieldlist,sql_db_list,cur_year+"-"+cur_duration,code,kind,"Q05_arc",this.userView,"all",where_c,this.frameconn);
		}
		maps=count_Leave();
		this.getFormHM().put("kqItem_hash",maps);
		this.getFormHM().put("kq_list",kqUtilsClass.getKqNbaseList(kq_dbase_list));
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("fielditemlist", fieldlist);
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("sessionlist",sessionlist);
		this.getFormHM().put("datelist",datelist);
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("durationlist",durationlist);		
		this.getFormHM().put("year",cur_year);
		this.getFormHM().put("duration",cur_duration);
		this.getFormHM().put("sessiondate",cur_course);		
		this.getFormHM().put("kq_duration", cur_course);
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
		// 涉及SQL注入直接放进userView里
		this.userView.getHm().put("kq_condition", "5`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","5");
		this.getFormHM().put("returnURL","/kq/register/history/sumbrowsedata.do?b_search=link");
		//显示部门层数
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
	    String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
	    if(uplevel==null||uplevel.length()==0)
    		uplevel="0";
        this.getFormHM().put("uplevel",uplevel);
	}
	
	/**
	  * 考勤规则的一个hashmap集
	  * @return
	  * @throws GeneralException
	  */
	 private HashMap count_Leave() throws GeneralException
	 {
	    	RowSet rs=null;	    	
	    	String kq_item_sql="select item_id,has_rest,has_feast,item_unit,fielditemid,sdata_src from kq_item";    	    	
	    	
	    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    	
	    	HashMap hashM=new HashMap();
	    	String fielditemid="";
	    	try
	    	{
	    	   rs =dao.search(kq_item_sql);
	    	   while(rs.next())
	    	   { 
	    		   HashMap hashm_one=new HashMap();	    		  
	    		   if(rs.getString("fielditemid")==null||rs.getString("fielditemid").length()<=0)
	    			   continue;
	    		   ArrayList fielditemlist = DataDictionary.getFieldList("Q03",Constant.USED_FIELD_SET);    
	    		   for(int i=0;i<fielditemlist.size();i++)
	   	    	   {
	   	   	          FieldItem fielditem=(FieldItem)fielditemlist.get(i);
	   	   	          fielditemid=rs.getString("fielditemid");	   	   	          
	   	   	          if(fielditemid.equalsIgnoreCase(fielditem.getItemid()))
	   	   	          {
	   	   	            hashm_one.put("fielditemid",rs.getString("fielditemid"));
		    		    hashm_one.put("has_rest",PubFunc.DotstrNull(rs.getString("has_rest")));
		    		    hashm_one.put("has_feast",PubFunc.DotstrNull(rs.getString("has_feast")));
		    		    hashm_one.put("item_unit",PubFunc.DotstrNull(rs.getString("item_unit")));
		    		    hashm_one.put("sdata_src",PubFunc.DotstrNull(rs.getString("sdata_src")));
		    		    hashM.put(fielditemid,hashm_one);
		    		    continue;
	   	   	          }
	   	    	   }
	    		   
	    	   }
	    	}catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		throw GeneralExceptionHandler.Handle(e);
	    	}finally{
	    		PubFunc.closeDbObj(rs);
			}
	    	return hashM;	    	
	 }
	 /**
	     * 判断某日期数据是否在Q05（员工月汇总表）中
	     * 
	     * @return
	     * @throws GeneralException
	     */
	    private boolean dataInQ05(String registerdate) throws GeneralException {
	    	boolean bool = true;
	    	RowSet rs = null;
	    	if (registerdate == null) {
	    		return false;
	    	}
	    	StringBuffer sql = new StringBuffer();
	    	sql.append("select count(q03z0) num from Q05 ");
	    	sql.append(" where q03z0=?");
	    	ArrayList<String> list = new ArrayList<String>();
	    	list.add(registerdate);
	    	ContentDAO dao = new ContentDAO(this.getFrameconn());
	    	int num = 0;
	        try {
	            rs = dao.search(sql.toString(), list);
	            while (rs.next()) {
	            	num = rs.getInt("num");
	            }
	            if(num == 0){
	            	bool = false; 
	            }            
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw GeneralExceptionHandler.Handle(e);
	        } finally {
	        	PubFunc.closeDbObj(rs);
	        }
	        return bool;
	    }
}
