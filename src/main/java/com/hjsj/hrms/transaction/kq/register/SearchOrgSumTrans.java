package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchOrgSumTrans extends IBusiness
{
    public void execute()throws GeneralException
    {
   	   HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	   String code=(String)hm.get("code");   	   
   	   String kind=(String) this.getFormHM().get("kind");
   	   ArrayList datelist=(ArrayList)this.getFormHM().get("datelist");
   	   String kq_duration=(String)this.getFormHM().get("kq_duration");
   	   String kq_period=(String)this.getFormHM().get("kq_period");   	   
	   String b0110=code;
	   HashMap maps=new HashMap();
	   //转换小时 1=默认；2=HH:MM
		String selectys=(String)hm.get("selectys");
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
	 
	   if(datelist==null||datelist.size()<=0)
		{
			 datelist =RegisterDate.getKqDurationList(this.getFrameconn());
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
		 if(b0110==null||b0110.length()<=0)
		 {
			 b0110=RegisterInitInfoData.getKqPrivCodeValue(userView);
		 }
		 if(kind==null||kind.length()<=0)
		 {
			 kind="2";
		 }		 
		 if("2".equals(kind))
		 {
			    /*ArrayList a0100whereIN= new ArrayList();
				for(int i=0;i<userView.getPrivDbList().size();i++)
				{
					String dbase=userView.getPrivDbList().get(i).toString();
					String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
					a0100whereIN.add(whereA0100In);
				}
					*//************得到部门权限**********//*

				String whereE0122=OrgRegister.selcet_kq_OrgId(start_date,end_date,"e0122",a0100whereIN,b0110);
				ArrayList orgide0122List=OrgRegister.getQrgE0122List(this.frameconn,whereE0122,"e0122");
				StringBuffer b0110s=new StringBuffer();
				b0110s.append("'"+b0110+"'");
				for(int i=0;i<orgide0122List.size();i++)
				{
					b0110s.append(", '");
					b0110s.append(orgide0122List.get(i).toString());
					b0110s.append("'");
				}				
				b0110=b0110s.toString();*/
		 }else
		 {
			 b0110=code;
		 }
		 list=OrgRegister.newFieldItemListQ09(list,codesetid);
		 KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
			ArrayList kq_dbase_list=kqUtilsClass.getKqPreList();	
			ArrayList a0100whereIN = new ArrayList();
			for(int i=0;i<kq_dbase_list.size();i++)
			{
				String dbase=kq_dbase_list.get(i).toString();
				String whereA0100In=RegisterInitInfoData.getWhereINSql(this.userView,dbase);
				
				a0100whereIN.add(whereA0100In);
			}
		 ArrayList sqllist=OrgRegister.getSumSqlstrLike(list,kq_duration, b0110, "Q09",a0100whereIN);
		 maps=count_Leave();
		 this.getFormHM().put("sqlstr", sqllist.get(0).toString());	 
		 this.getFormHM().put("strwhere", sqllist.get(1).toString());		  
		 this.getFormHM().put("columns", sqllist.get(2).toString()); 
		 this.getFormHM().put("orderby"," order by b0110");
		 this.getFormHM().put("kq_duration",kq_duration);
		 this.getFormHM().put("fielditemlist", list);		 
		 this.getFormHM().put("code",code);		 
		 this.getFormHM().put("orgsumvali","");
		 this.getFormHM().put("kq_period",kq_period);
		 this.getFormHM().put("datelist",datelist);
		 this.getFormHM().put("kqItem_hash",maps);
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
