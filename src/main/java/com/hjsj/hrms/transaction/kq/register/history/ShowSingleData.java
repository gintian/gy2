package com.hjsj.hrms.transaction.kq.register.history;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowSingleData  extends  IBusiness{
    public void execute()throws GeneralException
    {
   	  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	  String userbase = (String) this.getFormHM().get("userbase");
      userbase = PubFunc.decrypt(userbase);
	  String sessiondate = (String) hm.get("sessiondate"); 		 
	  String a0100=(String) this.getFormHM().get("a0100");
      a0100 = PubFunc.decrypt(a0100);
	  String cur_year=(String)this.getFormHM().get("cur_year");
	  ArrayList yearlist=RegisterDate.sessionYaer(this.frameconn,"1");
	  if(cur_year==null||cur_year.length()<=0)
	  {
		  if(yearlist!=null&&yearlist.size()>0)
			{			
				if(sessiondate!=null&&sessiondate.length()>0)
				{
					cur_year=sessiondate.substring(0,4);
				}else{
					CommonData vo = (CommonData) yearlist.get(0);
					cur_year=vo.getDataValue();
				}			
			}else{
					
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
			}  
	  }
		
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlist= RegisterInitInfoData.newFieldOneList(fieldlist);
		ArrayList sqllist=SingOpintion.getOneYearSQLStr(fielditemlist,userbase,a0100,cur_year,"Q05");
		 getSingleMessage(userbase,a0100);
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition","5`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","5");
		this.getFormHM().put("returnURL","/kq/register/history/showsingle_month.do?b_browse=link");
		 this.getFormHM().put("singfielditemlist", fielditemlist);	
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("cur_year",cur_year);
		this.getFormHM().put("a0100",a0100);
		this.getFormHM().put("userbase",userbase);
    }
    /**
     * 
     * @param userbase
     * @param A0100
     */
    public void getSingleMessage(String userbase,String A0100){
   	 StringBuffer sql=new StringBuffer();
   	 sql.append("select b0110,e0122,e01a1,a0101 ");
   	 sql.append(" from "+userbase+"A01 ");
   	 sql.append(" where a0100='"+A0100+"'");    	 
   	 String b0110="";
   	 String e0122="";
   	 String e01a1="";
   	 String a0101="";
   	 ContentDAO dao = new ContentDAO(this.getFrameconn());
		 try {
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next()){
				b0110=(String)this.frowset.getString("b0110");
				e0122=(String)this.frowset.getString("e0122");
				e01a1=(String)this.frowset.getString("e01a1");
				a0101=(String)this.frowset.getString("a0101"); 				
			}
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		 ArrayList restList=IfRestDate.search_RestOfWeek(b0110,userView,this.getFrameconn());
		 String rest_date=restList.get(0).toString();
		 this.getFormHM().put("rest_date",rest_date);
		 String b0110_value=b0110;
		 this.getFormHM().put("b0110_value",b0110_value);		
		 this.getFormHM().put("b0110",b0110);
		 this.getFormHM().put("e0122",e0122);
		 this.getFormHM().put("e01a1",e01a1);
		 this.getFormHM().put("a0101",a0101); 		
    }
	

}
