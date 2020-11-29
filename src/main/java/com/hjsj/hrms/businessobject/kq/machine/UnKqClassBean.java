package com.hjsj.hrms.businessobject.kq.machine;

import javax.sql.RowSet;

public class UnKqClassBean {
    private RowSet rs=null;  
    private String class_id;
    private String onduty_card_1;
    private String offduty_card_1;
    private String onduty_card_2;
    private String offduty_card_2;	
    private String onduty_card_3;
    private String offduty_card_3;
    private String onduty_card_4;
    private String offduty_card_4; 
    private String onduty_start_1;
    private String onduty_1;
    private String be_late_for_1;
    private String absent_work_1;
    private String onduty_end_1;
    private String rest_start_1;
    private String rest_end_1;
    private String offduty_start_1;
    private String leave_early_absent_1;
    private String leave_early_1;    
    private String offduty_1;
    private String offduty_end_1; 
    private String onduty_flextime_1;
    private String offduty_flextime_1;
	  //2
    private String onduty_start_2;
    private String onduty_2;
    private String be_late_for_2;
    private String absent_work_2;
    private String onduty_end_2;
    private String rest_start_2;
    private String rest_end_2;
    private String offduty_start_2;
    private String leave_early_absent_2;
    private String leave_early_2;    
    private String offduty_2;
    private String offduty_end_2; 
    private String onduty_flextime_2;
    private String offduty_flextime_2;
    
    private String onduty_start_3;
    private String onduty_3;
    private String be_late_for_3;
    private String absent_work_3;
    private String onduty_end_3;
    private String rest_start_3;
    private String rest_end_3;
    private String offduty_start_3;
    private String leave_early_absent_3;
    private String leave_early_3;    
    private String offduty_3;
    private String offduty_end_3; 
    private String onduty_flextime_3;
    private String offduty_flextime_3;
    
    private String onduty_start_4;
    private String onduty_4;
    private String be_late_for_4;
    private String absent_work_4;
    private String onduty_end_4;
    private String rest_start_4;
    private String rest_end_4;
    private String offduty_start_4;
    private String leave_early_absent_4;
    private String leave_early_4;    
    private String offduty_4;
    private String offduty_end_4; 
    private String onduty_flextime_4;
    private String offduty_flextime_4;
	//other
    private String night_shift_start;
    private String night_shift_end;
    private String zeroflag;
    private String domain_count;
    private String work_hours;
    private String zero_absent;
    private String one_absent;
   
    
    private String org_id;
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public UnKqClassBean(){}
	public UnKqClassBean(RowSet rs)
	{
	   this.rs=rs;
	   try
	   {
		   this.class_id=this.rs.getString("class_id");
		   this.onduty_card_1=this.rs.getString("onduty_card_1");
		   this.offduty_card_1=this.rs.getString("offduty_card_1");
		   this.onduty_card_2=this.rs.getString("onduty_card_2");
		   this.offduty_card_2=this.rs.getString("offduty_card_2");	
		   this.onduty_card_3=this.rs.getString("onduty_card_3");
		   this.offduty_card_3=this.rs.getString("offduty_card_3");
		   this.onduty_card_4=this.rs.getString("onduty_card_4");
		   this.offduty_card_4=this.rs.getString("offduty_card_4"); 
		   this.onduty_start_1=this.rs.getString("onduty_start_1");
		   this.onduty_1=this.rs.getString("onduty_1");
		   this.onduty_flextime_1=this.rs.getString("onduty_flextime_1");
		   this.be_late_for_1=this.rs.getString("be_late_for_1");
		   this.absent_work_1=this.rs.getString("absent_work_1");
		   this.onduty_end_1=this.rs.getString("onduty_end_1");
		   this.rest_start_1=this.rs.getString("rest_start_1");
		   this.rest_end_1=this.rs.getString("rest_end_1");
		   this.offduty_start_1=this.rs.getString("offduty_start_1");
		   this.leave_early_absent_1=this.rs.getString("leave_early_absent_1");
		   this.leave_early_1=this.rs.getString("leave_early_1");    
		   this.offduty_1=this.rs.getString("offduty_1");
		   this.offduty_flextime_1=this.rs.getString("offduty_flextime_1");
		   this.offduty_end_1=this.rs.getString("offduty_end_1"); 
			  //2
		   this.onduty_start_2=this.rs.getString("onduty_start_2");
		   this.onduty_2=this.rs.getString("onduty_2");
		   this.onduty_flextime_2=this.rs.getString("onduty_flextime_2");
		   this.be_late_for_2=this.rs.getString("be_late_for_2");
		   this.absent_work_2=this.rs.getString("absent_work_2");
		   this.onduty_end_2=this.rs.getString("onduty_end_2");
		   this.rest_start_2=this.rs.getString("rest_start_2");
		   this.rest_end_2=this.rs.getString("rest_end_2");
		   this.offduty_start_2=this.rs.getString("offduty_start_2");
		   this.leave_early_absent_2=this.rs.getString("leave_early_absent_2");
		   this.leave_early_2=this.rs.getString("leave_early_2");    
		   this.offduty_2=this.rs.getString("offduty_2");
		   this.offduty_flextime_2=this.rs.getString("offduty_flextime_2");
		   this.offduty_end_2=this.rs.getString("offduty_end_2"); 
		    
		   this.onduty_start_3=this.rs.getString("onduty_start_3");
		   this.onduty_3=this.rs.getString("onduty_3");
		   this.onduty_flextime_3=this.rs.getString("onduty_flextime_3");
		   this.be_late_for_3=this.rs.getString("be_late_for_3");
		   this.absent_work_3=this.rs.getString("absent_work_3");
		   this.onduty_end_3=this.rs.getString("onduty_end_3");
		   this.rest_start_3=this.rs.getString("rest_start_3");
		   this.rest_end_3=this.rs.getString("rest_end_3");
		   this.offduty_start_3=this.rs.getString("offduty_start_3");
		   this.leave_early_absent_3=this.rs.getString("leave_early_absent_3");
		   this.leave_early_3=this.rs.getString("leave_early_3");    
		   this.offduty_3=this.rs.getString("offduty_3");
		   this.offduty_flextime_3=this.rs.getString("offduty_flextime_3");
		   this.offduty_end_3=this.rs.getString("offduty_end_3"); 
		   
		   this.onduty_start_4=this.rs.getString("onduty_start_4");
		   this.onduty_4=this.rs.getString("onduty_4");
		   this.onduty_flextime_4=this.rs.getString("onduty_flextime_4");
		   this.be_late_for_4=this.rs.getString("be_late_for_4");
		   this.absent_work_4=this.rs.getString("absent_work_4");
		   this.onduty_end_4=this.rs.getString("onduty_end_4");
		   this.rest_start_4=this.rs.getString("rest_start_4");
		   this.rest_end_4=this.rs.getString("rest_end_4");
		   this.offduty_start_4=this.rs.getString("offduty_start_4");
		   this.leave_early_absent_4=this.rs.getString("leave_early_absent_4");
		   this.leave_early_4=this.rs.getString("leave_early_4");    
		   this.offduty_4=this.rs.getString("offduty_4");
		   this.offduty_flextime_4=this.rs.getString("offduty_flextime_4");
		   this.offduty_end_4=this.rs.getString("offduty_end_4"); 
		   
			//other
		   this.night_shift_start=this.rs.getString("night_shift_start");
		   this.night_shift_end=this.rs.getString("night_shift_end");
		   //this.zeroflag=this.rs.getString("zeroflag");
		   //this.domain_count=this.rs.getString("domain_count");
		   this.work_hours=this.rs.getString("work_hours");
		   this.zero_absent=this.rs.getString("zero_absent");
		   //this.one_absent=this.rs.getString("one_absent");		   
	   }catch(Exception e)
	   {
		  e.printStackTrace(); 
	   }	   
	}
	
	public String getAbsent_work_1() {
		return absent_work_1;
	}
	public void setAbsent_work_1(String absent_work_1) {
		this.absent_work_1 = absent_work_1;
	}
	public String getAbsent_work_2() {
		return absent_work_2;
	}
	public void setAbsent_work_2(String absent_work_2) {
		this.absent_work_2 = absent_work_2;
	}
	public String getAbsent_work_3() {
		return absent_work_3;
	}
	public void setAbsent_work_3(String absent_work_3) {
		this.absent_work_3 = absent_work_3;
	}
	public String getAbsent_work_4() {
		return absent_work_4;
	}
	public void setAbsent_work_4(String absent_work_4) {
		this.absent_work_4 = absent_work_4;
	}	
	public String getBe_late_for_1() {
		return be_late_for_1;
	}
	public void setBe_late_for_1(String be_late_for_1) {
		this.be_late_for_1 = be_late_for_1;
	}
	public String getBe_late_for_2() {
		return be_late_for_2;
	}
	public void setBe_late_for_2(String be_late_for_2) {
		this.be_late_for_2 = be_late_for_2;
	}
	public String getBe_late_for_3() {
		return be_late_for_3;
	}
	public void setBe_late_for_3(String be_late_for_3) {
		this.be_late_for_3 = be_late_for_3;
	}
	public String getBe_late_for_4() {
		return be_late_for_4;
	}
	public void setBe_late_for_4(String be_late_for_4) {
		this.be_late_for_4 = be_late_for_4;
	}
	
	public String getClass_id() {
		return class_id;
	}
	public void setClass_id(String class_id) {
		this.class_id = class_id;
	}
	public String getDomain_count() {
		return domain_count;
	}
	public void setDomain_count(String domain_count) {
		this.domain_count = domain_count;
	}	
	
	public String getLeave_early_1() {
		return leave_early_1;
	}
	public void setLeave_early_1(String leave_early_1) {
		this.leave_early_1 = leave_early_1;
	}
	public String getLeave_early_2() {
		return leave_early_2;
	}
	public void setLeave_early_2(String leave_early_2) {
		this.leave_early_2 = leave_early_2;
	}
	public String getLeave_early_3() {
		return leave_early_3;
	}
	public void setLeave_early_3(String leave_early_3) {
		this.leave_early_3 = leave_early_3;
	}
	public String getLeave_early_4() {
		return leave_early_4;
	}
	public void setLeave_early_4(String leave_early_4) {
		this.leave_early_4 = leave_early_4;
	}
	public String getLeave_early_absent_1() {
		return leave_early_absent_1;
	}
	public void setLeave_early_absent_1(String leave_early_absent_1) {
		this.leave_early_absent_1 = leave_early_absent_1;
	}
	public String getLeave_early_absent_2() {
		return leave_early_absent_2;
	}
	public void setLeave_early_absent_2(String leave_early_absent_2) {
		this.leave_early_absent_2 = leave_early_absent_2;
	}
	public String getLeave_early_absent_3() {
		return leave_early_absent_3;
	}
	public void setLeave_early_absent_3(String leave_early_absent_3) {
		this.leave_early_absent_3 = leave_early_absent_3;
	}
	public String getLeave_early_absent_4() {
		return leave_early_absent_4;
	}
	public void setLeave_early_absent_4(String leave_early_absent_4) {
		this.leave_early_absent_4 = leave_early_absent_4;
	}	
	public String getNight_shift_end() {
		return night_shift_end;
	}
	public void setNight_shift_end(String night_shift_end) {
		this.night_shift_end = night_shift_end;
	}
	public String getNight_shift_start() {
		return night_shift_start;
	}
	public void setNight_shift_start(String night_shift_start) {
		this.night_shift_start = night_shift_start;
	}
	public String getOffduty_1() {
		return offduty_1;
	}
	public void setOffduty_1(String offduty_1) {
		this.offduty_1 = offduty_1;
	}
	public String getOffduty_2() {
		return offduty_2;
	}
	public void setOffduty_2(String offduty_2) {
		this.offduty_2 = offduty_2;
	}
	public String getOffduty_3() {
		return offduty_3;
	}
	public void setOffduty_3(String offduty_3) {
		this.offduty_3 = offduty_3;
	}
	public String getOffduty_4() {
		return offduty_4;
	}
	public void setOffduty_4(String offduty_4) {
		this.offduty_4 = offduty_4;
	}
	public String getOffduty_card_1() {
		return offduty_card_1;
	}
	public void setOffduty_card_1(String offduty_card_1) {
		this.offduty_card_1 = offduty_card_1;
	}
	public String getOffduty_card_2() {
		return offduty_card_2;
	}
	public void setOffduty_card_2(String offduty_card_2) {
		this.offduty_card_2 = offduty_card_2;
	}
	public String getOffduty_card_3() {
		return offduty_card_3;
	}
	public void setOffduty_card_3(String offduty_card_3) {
		this.offduty_card_3 = offduty_card_3;
	}
	public String getOffduty_card_4() {
		return offduty_card_4;
	}
	public void setOffduty_card_4(String offduty_card_4) {
		this.offduty_card_4 = offduty_card_4;
	}
	public String getOffduty_end_1() {
		return offduty_end_1;
	}
	public void setOffduty_end_1(String offduty_end_1) {
		this.offduty_end_1 = offduty_end_1;
	}
	public String getOffduty_end_2() {
		return offduty_end_2;
	}
	public void setOffduty_end_2(String offduty_end_2) {
		this.offduty_end_2 = offduty_end_2;
	}
	public String getOffduty_end_3() {
		return offduty_end_3;
	}
	public void setOffduty_end_3(String offduty_end_3) {
		this.offduty_end_3 = offduty_end_3;
	}
	public String getOffduty_end_4() {
		return offduty_end_4;
	}
	public void setOffduty_end_4(String offduty_end_4) {
		this.offduty_end_4 = offduty_end_4;
	}
	public String getOffduty_start_1() {
		return offduty_start_1;
	}
	public void setOffduty_start_1(String offduty_start_1) {
		this.offduty_start_1 = offduty_start_1;
	}
	public String getOffduty_start_2() {
		return offduty_start_2;
	}
	public void setOffduty_start_2(String offduty_start_2) {
		this.offduty_start_2 = offduty_start_2;
	}
	public String getOffduty_start_3() {
		return offduty_start_3;
	}
	public void setOffduty_start_3(String offduty_start_3) {
		this.offduty_start_3 = offduty_start_3;
	}
	public String getOffduty_start_4() {
		return offduty_start_4;
	}
	public void setOffduty_start_4(String offduty_start_4) {
		this.offduty_start_4 = offduty_start_4;
	}
	public String getOnduty_1() {
		return onduty_1;
	}
	public void setOnduty_1(String onduty_1) {
		this.onduty_1 = onduty_1;
	}
	public String getOnduty_2() {
		return onduty_2;
	}
	public void setOnduty_2(String onduty_2) {
		this.onduty_2 = onduty_2;
	}
	public String getOnduty_3() {
		return onduty_3;
	}
	public void setOnduty_3(String onduty_3) {
		this.onduty_3 = onduty_3;
	}
	public String getOnduty_4() {
		return onduty_4;
	}
	public void setOnduty_4(String onduty_4) {
		this.onduty_4 = onduty_4;
	}
	public String getOnduty_card_1() {
		return onduty_card_1;
	}
	public void setOnduty_card_1(String onduty_card_1) {
		this.onduty_card_1 = onduty_card_1;
	}
	public String getOnduty_card_2() {
		return onduty_card_2;
	}
	public void setOnduty_card_2(String onduty_card_2) {
		this.onduty_card_2 = onduty_card_2;
	}
	public String getOnduty_card_3() {
		return onduty_card_3;
	}
	public void setOnduty_card_3(String onduty_card_3) {
		this.onduty_card_3 = onduty_card_3;
	}
	public String getOnduty_card_4() {
		return onduty_card_4;
	}
	public void setOnduty_card_4(String onduty_card_4) {
		this.onduty_card_4 = onduty_card_4;
	}
	public String getOnduty_end_1() {
		return onduty_end_1;
	}
	public void setOnduty_end_1(String onduty_end_1) {
		this.onduty_end_1 = onduty_end_1;
	}
	public String getOnduty_end_2() {
		return onduty_end_2;
	}
	public void setOnduty_end_2(String onduty_end_2) {
		this.onduty_end_2 = onduty_end_2;
	}
	public String getOnduty_end_3() {
		return onduty_end_3;
	}
	public void setOnduty_end_3(String onduty_end_3) {
		this.onduty_end_3 = onduty_end_3;
	}
	public String getOnduty_end_4() {
		return onduty_end_4;
	}
	public void setOnduty_end_4(String onduty_end_4) {
		this.onduty_end_4 = onduty_end_4;
	}
	public String getOnduty_start_1() {
		return onduty_start_1;
	}
	public void setOnduty_start_1(String onduty_start_1) {
		this.onduty_start_1 = onduty_start_1;
	}
	public String getOnduty_start_2() {
		return onduty_start_2;
	}
	public void setOnduty_start_2(String onduty_start_2) {
		this.onduty_start_2 = onduty_start_2;
	}
	public String getOnduty_start_3() {
		return onduty_start_3;
	}
	public void setOnduty_start_3(String onduty_start_3) {
		this.onduty_start_3 = onduty_start_3;
	}
	public String getOnduty_start_4() {
		return onduty_start_4;
	}
	public void setOnduty_start_4(String onduty_start_4) {
		this.onduty_start_4 = onduty_start_4;
	}
	public String getOne_absent() {
		return one_absent;
	}
	public void setOne_absent(String one_absent) {
		this.one_absent = one_absent;
	}
	public String getRest_end_1() {
		return rest_end_1;
	}
	public void setRest_end_1(String rest_end_1) {
		this.rest_end_1 = rest_end_1;
	}
	public String getRest_end_2() {
		return rest_end_2;
	}
	public void setRest_end_2(String rest_end_2) {
		this.rest_end_2 = rest_end_2;
	}
	public String getRest_end_3() {
		return rest_end_3;
	}
	public void setRest_end_3(String rest_end_3) {
		this.rest_end_3 = rest_end_3;
	}
	public String getRest_end_4() {
		return rest_end_4;
	}
	public void setRest_end_4(String rest_end_4) {
		this.rest_end_4 = rest_end_4;
	}
	public String getRest_start_1() {
		return rest_start_1;
	}
	public void setRest_start_1(String rest_start_1) {
		this.rest_start_1 = rest_start_1;
	}
	public String getRest_start_2() {
		return rest_start_2;
	}
	public void setRest_start_2(String rest_start_2) {
		this.rest_start_2 = rest_start_2;
	}
	public String getRest_start_3() {
		return rest_start_3;
	}
	public void setRest_start_3(String rest_start_3) {
		this.rest_start_3 = rest_start_3;
	}
	public String getRest_start_4() {
		return rest_start_4;
	}
	public void setRest_start_4(String rest_start_4) {
		this.rest_start_4 = rest_start_4;
	}
	public RowSet getRs() {
		return rs;
	}
	public void setRs(RowSet rs) {
		this.rs = rs;
	}
	public String getWork_hours() {
		return work_hours;
	}
	public void setWork_hours(String work_hours) {
		this.work_hours = work_hours;
	}
	public String getZero_absent() {
		return zero_absent;
	}
	public void setZero_absent(String zero_absent) {
		this.zero_absent = zero_absent;
	}
	public String getZeroflag() {
		return zeroflag;
	}
	public void setZeroflag(String zeroflag) {
		this.zeroflag = zeroflag;
	}
	public String getOnduty_card(String i)
	{
	   String value="";	
	   if("1".equals(i))
	   {
		   value=this.getOnduty_card_1();
	   }else if("2".equals(i))
	   {
		   value=this.getOnduty_card_2();
	   }else if("3".equals(i))
	   {
		   value=this.getOnduty_card_3();
	   }else if("4".equals(i))
	   {
		   value=this.getOnduty_card_4();
	   }	
	   return value;
	}
	public String getOffduty_card(String i)
	{
		String value="";	
		   if("1".equals(i))
		   {
			   value=this.getOffduty_card_1();
		   }else if("2".equals(i))
		   {
			   value=this.getOffduty_card_2();
		   }else if("3".equals(i))
		   {
			   value=this.getOffduty_card_3();
		   }else if("4".equals(i))
		   {
			   value=this.getOffduty_card_4();
		   }	
		   return value;
	}
	public String getOnduty_start(String i)
	{
		String value="";	
		   if("1".equals(i))
		   {
			   value=this.getOnduty_start_1();
		   }else if("2".equals(i))
		   {
			   value=this.getOnduty_start_2();
		   }else if("3".equals(i))
		   {
			   value=this.getOnduty_start_3();
		   }else if("4".equals(i))
		   {
			   value=this.getOnduty_start_4();
		   }	
		   return value;
	}
	public String getOnduty_end(String i)
	{
		String value="";	
		   if("1".equals(i))
		   {
			   value=this.getOnduty_end_1();
		   }else if("2".equals(i))
		   {
			   value=this.getOnduty_end_2();
		   }else if("3".equals(i))
		   {
			   value=this.getOnduty_end_3();
		   }else if("4".equals(i))
		   {
			   value=this.getOnduty_end_4();
		   }	
		   return value;
	}
	public String getOffduty_start(String i)
	{
		String value="";	
		   if("1".equals(i))
		   {
			   value=this.getOffduty_start_1();
		   }else if("2".equals(i))
		   {
			   value=this.getOffduty_start_2();
		   }else if("3".equals(i))
		   {
			   value=this.getOffduty_start_3();
		   }else if("4".equals(i))
		   {
			   value=this.getOffduty_start_4();
		   }	
		   return value;
	}
	public String getOffduty_end(String i)
	{
		String value="";	
		   if("1".equals(i))
		   {
			   value=this.getOffduty_end_1();
		   }else if("2".equals(i))
		   {
			   value=this.getOffduty_end_2();
		   }else if("3".equals(i))
		   {
			   value=this.getOffduty_end_3();
		   }else if("4".equals(i))
		   {
			   value=this.getOffduty_end_4();
		   }	
		   return value;
	}
	public void getUnKqClassBean(RowSet rs)
	{
		this.rs=rs;
		   try
		   {
			   //this.class_id=rs.getString("class_id");
			   this.onduty_card_1=rs.getString("onduty_card_1");
			   this.offduty_card_1=rs.getString("offduty_card_1");
			   this.onduty_card_2=rs.getString("onduty_card_2");
			   this.offduty_card_2=rs.getString("offduty_card_2");	
			   this.onduty_card_3=rs.getString("onduty_card_3");
			   this.offduty_card_3=rs.getString("offduty_card_3");
			   this.onduty_card_4=rs.getString("onduty_card_4");
			   this.offduty_card_4=rs.getString("offduty_card_4"); 
			   this.onduty_start_1=rs.getString("onduty_start_1");
			   this.onduty_1=rs.getString("onduty_1");
			   this.onduty_flextime_1=this.rs.getString("onduty_flextime_1");
			   this.be_late_for_1=rs.getString("be_late_for_1");
			   this.absent_work_1=rs.getString("absent_work_1");
			   this.onduty_end_1=rs.getString("onduty_end_1");
			   this.rest_start_1=rs.getString("rest_start_1");
			   this.rest_end_1=rs.getString("rest_end_1");
			   this.offduty_start_1=rs.getString("offduty_start_1");
			   this.leave_early_absent_1=rs.getString("leave_early_absent_1");
			   this.leave_early_1=rs.getString("leave_early_1");    
			   this.offduty_1=rs.getString("offduty_1");
			   this.offduty_flextime_1=this.rs.getString("offduty_flextime_1");
			   this.offduty_end_1=rs.getString("offduty_end_1"); 
				  //2
			   this.onduty_start_2=rs.getString("onduty_start_2");
			   this.onduty_2=rs.getString("onduty_2");
			   this.onduty_flextime_2=this.rs.getString("onduty_flextime_2");
			   this.be_late_for_2=rs.getString("be_late_for_2");
			   this.absent_work_2=rs.getString("absent_work_2");
			   this.onduty_end_2=rs.getString("onduty_end_2");
			   this.rest_start_2=rs.getString("rest_start_2");
			   this.rest_end_2=rs.getString("rest_end_2");
			   this.offduty_start_2=rs.getString("offduty_start_2");
			   this.leave_early_absent_2=rs.getString("leave_early_absent_2");
			   this.leave_early_2=rs.getString("leave_early_2");    
			   this.offduty_2=rs.getString("offduty_2");
			   this.offduty_flextime_2=this.rs.getString("offduty_flextime_2");
			   this.offduty_end_2=rs.getString("offduty_end_2"); 
			    
			   this.onduty_start_3=rs.getString("onduty_start_3");
			   this.onduty_3=rs.getString("onduty_3");
			   this.onduty_flextime_3=this.rs.getString("onduty_flextime_3");
			   this.be_late_for_3=rs.getString("be_late_for_3");
			   this.absent_work_3=rs.getString("absent_work_3");
			   this.onduty_end_3=rs.getString("onduty_end_3");
			   this.rest_start_3=rs.getString("rest_start_3");
			   this.rest_end_3=rs.getString("rest_end_3");
			   this.offduty_start_3=rs.getString("offduty_start_3");
			   this.leave_early_absent_3=rs.getString("leave_early_absent_3");
			   this.leave_early_3=rs.getString("leave_early_3");    
			   this.offduty_3=rs.getString("offduty_3");
			   this.offduty_flextime_3=this.rs.getString("offduty_flextime_3");
			   this.offduty_end_3=rs.getString("offduty_end_3"); 
			   
			   this.onduty_start_4=rs.getString("onduty_start_4");
			   this.onduty_4=rs.getString("onduty_4");
			   this.onduty_flextime_4=this.rs.getString("onduty_flextime_4");
			   this.be_late_for_4=rs.getString("be_late_for_4");
			   this.absent_work_4=rs.getString("absent_work_4");
			   this.onduty_end_4=rs.getString("onduty_end_4");
			   this.rest_start_4=rs.getString("rest_start_4");
			   this.rest_end_4=rs.getString("rest_end_4");
			   this.offduty_start_4=rs.getString("offduty_start_4");
			   this.leave_early_absent_4=rs.getString("leave_early_absent_4");
			   this.leave_early_4=rs.getString("leave_early_4");    
			   this.offduty_4=rs.getString("offduty_4");
			   this.offduty_flextime_4=this.rs.getString("offduty_flextime_4");
			   this.offduty_end_4=rs.getString("offduty_end_4"); 
			   
				//other
			   this.night_shift_start=rs.getString("night_shift_start");
			   this.night_shift_end=rs.getString("night_shift_end");
			  // this.zeroflag=rs.getString("zeroflag");
			   //this.domain_count=rs.getString("domain_count");
			   this.work_hours=rs.getString("work_hours");
			   this.zero_absent=rs.getString("zero_absent");
			  // this.one_absent=rs.getString("one_absent");		   
		   }catch(Exception e)
		   {
			  e.printStackTrace(); 
		   }		  
		   
	}
	 public String getOnduty()
	 {
		   return this.getOnduty_1();
	 }
	 public String getOffduty()
	 {
		 
		 if(this.getOffduty_3()!=null&&this.getOffduty_3().length()>0)
		 {
			 return this.getOffduty_3();
		 }else if(this.getOffduty_2()!=null&&this.getOffduty_2().length()>0)
		 {
			 return this.getOffduty_2();
		 }else if(this.getOffduty_1()!=null&&this.getOffduty_1().length()>0)
		 {
			 return this.getOffduty_1();
		 }else
		 {
			 return "";
		 }
	 }
	public String getOffduty_flextime_1() {
		return offduty_flextime_1;
	}
	public void setOffduty_flextime_1(String offduty_flextime_1) {
		this.offduty_flextime_1 = offduty_flextime_1;
	}
	public String getOffduty_flextime_2() {
		return offduty_flextime_2;
	}
	public void setOffduty_flextime_2(String offduty_flextime_2) {
		this.offduty_flextime_2 = offduty_flextime_2;
	}
	public String getOffduty_flextime_3() {
		return offduty_flextime_3;
	}
	public void setOffduty_flextime_3(String offduty_flextime_3) {
		this.offduty_flextime_3 = offduty_flextime_3;
	}
	public String getOffduty_flextime_4() {
		return offduty_flextime_4;
	}
	public void setOffduty_flextime_4(String offduty_flextime_4) {
		this.offduty_flextime_4 = offduty_flextime_4;
	}
	public String getOnduty_flextime_1() {
		return onduty_flextime_1;
	}
	public void setOnduty_flextime_1(String onduty_flextime_1) {
		this.onduty_flextime_1 = onduty_flextime_1;
	}
	public String getOnduty_flextime_2() {
		return onduty_flextime_2;
	}
	public void setOnduty_flextime_2(String onduty_flextime_2) {
		this.onduty_flextime_2 = onduty_flextime_2;
	}
	public String getOnduty_flextime_3() {
		return onduty_flextime_3;
	}
	public void setOnduty_flextime_3(String onduty_flextime_3) {
		this.onduty_flextime_3 = onduty_flextime_3;
	}
	public String getOnduty_flextime_4() {
		return onduty_flextime_4;
	}
	public void setOnduty_flextime_4(String onduty_flextime_4) {
		this.onduty_flextime_4 = onduty_flextime_4;
	}
}

