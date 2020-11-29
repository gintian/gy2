package com.hjsj.hrms.transaction.kq.register.historical;

import com.hjsj.hrms.businessobject.kq.register.OrgRegister;
import com.hjsj.hrms.businessobject.kq.register.history.RegisterDate;
import com.hjsj.hrms.businessobject.kq.register.sing.SingOpintion;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowOrgYearSingleData extends  IBusiness{
    public void execute()throws GeneralException
    {
   	  HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
   	  String b0110 = (String) this.getFormHM().get("b0110");
	  String year = (String) hm.get("year");
	  hm.remove("year");
	  String cur_year=(String)this.getFormHM().get("cur_year");
	  ArrayList yearlist=RegisterDate.sessionYaer(this.frameconn,"1");
       if(year!=null&&year.length()>0)
				{
					cur_year=year;
				}	
		ArrayList fieldlist = DataDictionary.getFieldList("Q03",
				Constant.USED_FIELD_SET);
		ArrayList fielditemlist= OrgRegister.newFieldItemList(fieldlist);//SingOpintion.newFieldOneList(fieldlist);
		ArrayList sqllist=SingOpintion.getOneOrgYearSQLStr(fielditemlist,b0110,cur_year,"Q09_arc");
		 
		this.getFormHM().put("sqlstr", sqllist.get(0).toString());
  		this.getFormHM().put("strwhere", sqllist.get(1).toString());
  		this.getFormHM().put("orderby", sqllist.get(2).toString());
		this.getFormHM().put("columns", sqllist.get(3).toString());	
		this.getFormHM().put("condition","9`"+sqllist.get(4).toString());
		this.getFormHM().put("relatTableid","9");
		this.getFormHM().put("returnURL","/kq/register/historical/orgyearsingle.do?b_browse=link");
		 this.getFormHM().put("singfielditemlist", fielditemlist);	
		this.getFormHM().put("yearlist",yearlist);
		this.getFormHM().put("cur_year",cur_year);
		this.getFormHM().put("b0110",b0110);
		String org_name=AdminCode.getCodeName("UN", b0110);
		if(org_name==null||org_name.length()<=0)
			org_name=AdminCode.getCodeName("UM", b0110);
		this.getFormHM().put("org_name",org_name);

    }
}
