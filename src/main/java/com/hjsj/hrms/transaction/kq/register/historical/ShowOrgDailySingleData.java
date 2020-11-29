package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowOrgDailySingleData extends  IBusiness{

	public void execute()throws GeneralException
    {
   	  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	  String b0110 = (String) this.getFormHM().get("b0110");
   	  b0110 = PubFunc.decrypt(b0110);
	  String year = (String) hm.get("year");
	  String duration=(String) hm.get("duration");
	  hm.remove("year");
	  hm.remove("duration");
	
	  String cur_year=(String)this.getFormHM().get("cur_year");
	  String cur_duration=(String)this.getFormHM().get("cur_duration");
//	  
//	  if((cur_year==null||cur_year.length()<=0)&&(cur_duration==null||cur_duration.length()<=0))
//	  {
//		  if(yearlist!=null&&yearlist.size()>0&&durationlist!=null&&durationlist.size()>0)
//			{			
//				if(year!=null&&year.length()>0&&duration!=null&&duration.length()>0)
//				{
//					cur_year=year;
//					cur_duration=duration;
//				}else{
//					CommonData vy = (CommonData) yearlist.get(0);
//					cur_year=vy.getDataValue();
//					CommonData vd = (CommonData) yearlist.get(0);
//					cur_duration=vd.getDataValue();
//				}			
//			}else{
//					
//					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.session.nohistory"),"",""));
//			}  	
//	  }
	  if(year!=null&&year.length()==4){
		  cur_year=year;
	  }
	  if(duration!=null&&duration.length()==2){
		  cur_duration=duration;
	  }
	   ArrayList yearlist=RegisterDate.yearDate(this.frameconn,"1");
	   ArrayList durationlist=RegisterDate.durationDate(this.frameconn,"1",cur_year);
	   ArrayList datelist=RegisterDate.getKqDate(this.getFrameconn(),cur_year+"-"+cur_duration,1);
	    String start_date = datelist.get(0).toString(); 		 
		String end_date=datelist.get(1).toString();
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlist= OrgRegister.newFieldItemList(fieldlist);//SingOpintion.newFieldOneList(fieldlist);
		ArrayList sqllist=SingOpintion.getOneOrgMothSQLStr(fielditemlist,b0110,start_date,end_date,"Q07_arc");		 
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition","7`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","7");
		this.getFormHM().put("returnURL","/kq/register/historical/orgdailysingle.do?b_browse=link");
		 this.getFormHM().put("singfielditemlist", fielditemlist);	
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("durationlist",durationlist);
		this.getFormHM().put("b0110",b0110);
		this.getFormHM().put("cur_year",cur_year);
		this.getFormHM().put("cur_duration",cur_duration);
		String org_name=AdminCode.getCodeName("UN", b0110);
		if(org_name==null||org_name.length()<=0)
			org_name=AdminCode.getCodeName("UM", b0110);
		this.getFormHM().put("org_name",org_name);
    }

}
