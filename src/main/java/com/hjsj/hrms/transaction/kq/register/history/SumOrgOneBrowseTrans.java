package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.ManagePrivCode;
import com.hjsj.hrms.businessobject.kq.register.HistoryBrowse;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SumOrgOneBrowseTrans extends IBusiness {
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String sessiondate=(String)this.getFormHM().get("sessiondate");//考勤期间		
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		ArrayList sessionlist=(ArrayList)this.getFormHM().get("sessionlist");
		ArrayList kq_dbase_list = (ArrayList)this.getFormHM().get("kq_dbase_list");
		//转换小时 1=默认；2=HH:MM
		String selectys=(String)hm.get("selectys");
		HashMap maps=new HashMap();
		if(selectys==null|| "".equals(selectys))
		{
			String selectyis=(String)this.getFormHM().get("selectys");
			if(selectyis==null|| "".equals(selectyis)){
				selectys="1";
			}else
			{
				selectys=selectyis;
			}
			
		}
	 this.getFormHM().put("selectys",selectys);
	 
		if(kq_dbase_list==null||kq_dbase_list.size()<=0)
		{
			kq_dbase_list=userView.getPrivDbList();  
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
		String b0110="";
		if(code.length()<=0){
			ManagePrivCode managePrivCode=new ManagePrivCode(userView,this.getFrameconn());			
			code=managePrivCode.getPrivOrgId();
			b0110="UN"+code;
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
		 ArrayList fielditemlist = DataDictionary.getFieldList("Q03",
					Constant.USED_FIELD_SET);
			 ArrayList list= OrgRegister.newFieldItemList(fielditemlist);			 
			 String codesetid="UN";
			 if(!userView.isSuper_admin()) 
	        {
			 if("UM".equals(RegisterInitInfoData.getKqPrivCode(userView)))
				codesetid="UM";	
	        }
			 list=OrgRegister.newFieldItemListQ09(list,codesetid);
			 ArrayList a0100whereIN= new ArrayList();
			 String b0100s="";
			 for(int i=0;i<kq_dbase_list.size();i++)
			 {
						String dbase=kq_dbase_list.get(i).toString();
						String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
						a0100whereIN.add(whereA0100In);
			 }
			 String whereE0122=OrgRegister.selcet_kq_AllOrgId("e0122",a0100whereIN,code);
			 ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
			 StringBuffer b0110Str=new StringBuffer();		
			 for(int i=0;i<orgide0122List.size();i++)
			 {
					b0110Str.append("'"+orgide0122List.get(i).toString()+"',");
			 }
			
			 if(code!=null&&code.length()>0)
			 {
					b0110Str.append("'"+code+"',");
			 }			 
			 if(b0110Str.toString()!=null&&b0110Str.toString().length()>0)
			 {
				 b0100s= b0110Str.toString().substring(0,b0110Str.length()-1);	
			 }	
			 
			 ArrayList sqllist=OrgRegister.getSqlstrHistory(list,b0100s,cur_course, b0110, "Q09");
		
		maps=count_Leave();
		this.getFormHM().put("kqItem_hash",maps);
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());		
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition","9`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","9");
		this.getFormHM().put("returnURL","/kq/register/history/sumorgbrowsedata.do?b_search=link");
		this.getFormHM().put("fielditemlist", list);
		this.getFormHM().put("kq_dbase_list",kq_dbase_list);
		this.getFormHM().put("code",code);
		this.getFormHM().put("kind",kind);
		this.getFormHM().put("sessionlist",sessionlist);
		this.getFormHM().put("datelist",datelist);		
		this.getFormHM().put("coursedate",cur_course);		
		this.getFormHM().put("start_date",start_date);
		this.getFormHM().put("end_date",end_date);
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
				if(rs!=null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
	    	return hashM;	    	
	 }
}
