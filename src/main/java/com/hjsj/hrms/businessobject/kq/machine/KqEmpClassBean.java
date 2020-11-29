package com.hjsj.hrms.businessobject.kq.machine;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.register.KQRestOper;
import com.hrms.frame.dao.utility.DateUtils;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.util.Date;

public class KqEmpClassBean {
    private ResultSet rs=null;
    private String a0100;
    private String a0101;
    private String nbase;
    private String b0110;
    private String e0122;
    private String e01a1;
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
    private String onduty_flextime_1;
    private String be_late_for_1;
    private String absent_work_1;
    private String onduty_end_1;
    private String rest_start_1;
    private String rest_end_1;
    private String offduty_start_1;
    private String leave_early_absent_1;
    private String leave_early_1;    
    private String offduty_1;
    private String offduty_flextime_1;
    private String offduty_end_1; 
	  //2
    private String onduty_start_2;
    private String onduty_2;
    private String onduty_flextime_2;
    private String be_late_for_2;
    private String absent_work_2;
    private String onduty_end_2;
    private String rest_start_2;
    private String rest_end_2;
    private String offduty_start_2;
    private String leave_early_absent_2;
    private String leave_early_2;    
    private String offduty_2;
    private String offduty_flextime_2;
    private String offduty_end_2; 
    
    private String onduty_start_3;
    private String onduty_3;
    private String onduty_flextime_3;
    private String be_late_for_3;
    private String absent_work_3;
    private String onduty_end_3;
    private String rest_start_3;
    private String rest_end_3;
    private String offduty_start_3;
    private String leave_early_absent_3;
    private String leave_early_3;    
    private String offduty_3;
    private String offduty_flextime_3;
    private String offduty_end_3; 
    
    private String onduty_start_4;
    private String onduty_4;
    private String onduty_flextime_4;
    private String be_late_for_4;
    private String absent_work_4;
    private String onduty_end_4;
    private String rest_start_4;
    private String rest_end_4;
    private String offduty_start_4;
    private String leave_early_absent_4;
    private String leave_early_4;    
    private String offduty_4;
    private String offduty_flextime_4;
    private String offduty_end_4; 
	//other
    private String night_shift_start;
    private String night_shift_end;
    private String zeroflag;
    private String domain_count;
    private String work_hours;
    private String zero_absent;
    private String one_absent;
    private String card_time;
    private String  kq_dkind;
    private String kq_type;
    private String org_id;
    private float flextimeLen;
    private String check_tran_overtime;//是否检测延时加班情况
    private int overtime_from;//下班多久后开始计加班
    private String overtime_type;//延时加班默认加班类型
	public String getCheck_tran_overtime() {
		return check_tran_overtime;
	}
	public void setCheck_tran_overtime(String check_tran_overtime) {
		this.check_tran_overtime = check_tran_overtime;
	}
	public int getOvertime_from() {
		
		return overtime_from;
	}
	public void setOvertime_from(int overtime_from) {
		this.overtime_from = overtime_from;
	}
	public String getOvertime_type() {
		if(overtime_type==null||overtime_type.length()<=0) {
            overtime_type="-1";
        }
		return overtime_type;
	}
	public void setOvertime_type(String overtime_type) {
		this.overtime_type = overtime_type;
	}
	public String getOrg_id() {
		return org_id;
	}
	public void setOrg_id(String org_id) {
		this.org_id = org_id;
	}
	public KqEmpClassBean(){}
	public KqEmpClassBean(ResultSet rs)
	{
	   this.rs=rs;
	   try
	   {
		   this.a0100=this.rs.getString("a0100");
		   this.a0101=this.rs.getString("a0101");
		   this.nbase=this.rs.getString("nbase");
		   this.b0110=this.rs.getString("b0110");
		   this.e0122=this.rs.getString("e0122");
		   this.e01a1=this.rs.getString("e01a1");
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
		   this.card_time=this.rs.getString("card_time");
		   this.kq_dkind=this.rs.getString("kq_dkind");
		   this.kq_type=this.rs.getString("kq_type");
		   this.check_tran_overtime=this.rs.getString("check_tran_overtime");//是否检测延时加班情况
		   this.overtime_from=this.rs.getInt("overtime_from");//下班多久后开始计加班
		   this.overtime_type=this.rs.getString("overtime_type");//延时加班默认加班类型
		   this.flextimeLen=CalcFlexTimeLen();//弹性时间长度
	   }catch(Exception e)
	   {
		  e.printStackTrace(); 
	   }	   
	}
	public String getA0100() {
		return a0100;
	}
	public void setA0100(String a0100) {
		this.a0100 = a0100;
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
	public String getB0110() {
		return b0110;
	}
	public void setB0110(String b0110) {
		this.b0110 = b0110;
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
	public String getCard_time() {
		return card_time;
	}
	public void setCard_time(String card_time) {
		this.card_time = card_time;
	}
	public String getClass_id() {
		return class_id;
	}
	public void setClass_id(String class_id) {
		if(class_id==null||class_id.length()<=0) {
            class_id="0";
        }
		this.class_id = class_id;
	}
	public String getDomain_count() {
		return domain_count;
	}
	public void setDomain_count(String domain_count) {
		this.domain_count = domain_count;
	}
	public String getE0122() {
		return e0122;
	}
	public void setE0122(String e0122) {
		this.e0122 = e0122;
	}
	public String getE01a1() {
		return e01a1;
	}
	public void setE01a1(String e01a1) {
		this.e01a1 = e01a1;
	}
	public String getKq_dkind() {
		return kq_dkind;
	}
	public void setKq_dkind(String kq_dkind) {
		this.kq_dkind = kq_dkind;
	}
	public String getKq_type() {
		return kq_type;
	}
	public void setKq_type(String kq_type) {
		this.kq_type = kq_type;
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
	public String getNbase() {
		return nbase;
	}
	public void setNbase(String nbase) {
		this.nbase = nbase;
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
	public ResultSet getRs() {
		return rs;
	}
	public void setRs(RowSet rs) {
		this.rs = rs;
	}
	public String getWork_hours() {
		if(work_hours==null||work_hours.length()<=0)
		{
			return 0+"";
		}else{
			return work_hours;
		}
		
	}
	public void setWork_hours(String work_hours) {
		this.work_hours = work_hours;
	}
	public String getZero_absent() {
		if(zero_absent==null||zero_absent.length()<=0)
		{
			zero_absent="0";
		}
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
	/**
	 * 返回指定上班时是否需要刷卡
	 * @param i
	 * @return
	 */
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
	   if(value==null||value.length()<=0) {
           value="";
       }
	   return value;
	}
	/**
	 * 返回指定下班时是否需要刷卡
	 * @param i
	 * @return
	 */
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
		   if(value==null||value.length()<=0) {
               value="";
           }
		   return value;
	}
	/**
	 * 是否有需要刷卡的点
	 * @return
	 */
	public boolean getIsMustCard()
	{
		boolean isCorrect=false;
		for(int i=1;i<=4;i++)
		{
			if(getOnduty_card(i+"")!=null&&!"0".equals(getOnduty_card(i+"")))
			{
				isCorrect=true;
				break;
			}else if(getOffduty_card(i+"")!=null&&!"0".equals(getOffduty_card(i+"")))
			{
				isCorrect=true;
				break;
			}	
		}
		return isCorrect;
	}
	/**
	 * 返回上班需刷卡开始时间
	 * @param i
	 * @return
	 */
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
	/**
	 * 返回上班需刷卡结束时间
	 * @param i
	 * @return
	 */
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
	/**
	 * 返回下班需刷卡开始时间
	 * @param i
	 * @return
	 */
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
	/**
	 * 返回下班需刷卡结束时间
	 * @param i
	 * @return
	 */
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
	/**
	 * 返回上班时间
	 * @return
	 */
	public String getOnduty()
	{
		   return this.getOnduty_1();
	}
	public String getOnduty(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getOnduty_1();
		}else if("2".equals(i))
		{
			   value=this.getOnduty_2();
		}else if("3".equals(i))
		{
			   value=this.getOnduty_3();
		}else if("4".equals(i))
		{
			   value=this.getOnduty_4();
		}	
		return value;		
	}
	/**
	 * 返回下班时间
	 * @return
	 */
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
	public String getOffduty(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getOffduty_1();
		}else if("2".equals(i))
		{
			   value=this.getOffduty_2();
		}else if("3".equals(i))
		{
			   value=this.getOffduty_3();
		}else if("4".equals(i))
		{
			   value=this.getOffduty_4();
		}	
		return value;	
	}
	public KqEmpClassBean getKqEmpClassBean(ResultSet rs)
	{
		KqEmpClassBean kqEmpClassBean=new KqEmpClassBean();
		this.rs=rs;
		try
		{
			   kqEmpClassBean.setA0100(this.rs.getString("a0100"));
			   kqEmpClassBean.setNbase(this.rs.getString("nbase"));
			   kqEmpClassBean.setB0110(this.rs.getString("b0110"));
			   kqEmpClassBean.setE0122(this.rs.getString("e0122"));
			   kqEmpClassBean.setE01a1(this.rs.getString("e01a1"));
			   kqEmpClassBean.setClass_id(this.rs.getString("class_id"));
			   kqEmpClassBean.setOnduty_card_1(this.rs.getString("onduty_card_1"));
			   kqEmpClassBean.setOffduty_card_1(this.rs.getString("offduty_card_1"));
			   kqEmpClassBean.setOnduty_card_2(this.rs.getString("onduty_card_2"));
			   kqEmpClassBean.setOffduty_card_2(this.rs.getString("offduty_card_2"));	
			   kqEmpClassBean.setOnduty_card_3(this.rs.getString("onduty_card_3"));
			   kqEmpClassBean.setOffduty_card_3(this.rs.getString("offduty_card_3"));
			   kqEmpClassBean.setOnduty_card_4(this.rs.getString("onduty_card_4"));
			   kqEmpClassBean.setOffduty_card_4(this.rs.getString("offduty_card_4")); 
			   kqEmpClassBean.setOnduty_start_1(this.rs.getString("onduty_start_1"));
			   kqEmpClassBean.setOnduty_1(this.rs.getString("onduty_1"));
			   kqEmpClassBean.setOnduty_flextime_1(this.rs.getString("onduty_flextime_1"));
			   kqEmpClassBean.setBe_late_for_1(this.rs.getString("be_late_for_1"));
			   kqEmpClassBean.setAbsent_work_1(this.rs.getString("absent_work_1"));
			   kqEmpClassBean.setOnduty_end_1(this.rs.getString("onduty_end_1"));
			   kqEmpClassBean.setRest_start_1(this.rs.getString("rest_start_1"));
			   kqEmpClassBean.setRest_end_1(this.rs.getString("rest_end_1"));
			   kqEmpClassBean.setOffduty_start_1(this.rs.getString("offduty_start_1"));
			   kqEmpClassBean.setLeave_early_absent_1(this.rs.getString("leave_early_absent_1"));
			   kqEmpClassBean.setLeave_early_1(this.rs.getString("leave_early_1"));    
			   kqEmpClassBean.setOffduty_1(this.rs.getString("offduty_1"));
			   kqEmpClassBean.setOffduty_flextime_1(this.rs.getString("offduty_flextime_1"));
			   kqEmpClassBean.setOffduty_end_1(this.rs.getString("offduty_end_1")); 
				  //2
			   kqEmpClassBean.setOnduty_start_2(this.rs.getString("onduty_start_2"));
			   kqEmpClassBean.setOnduty_2(this.rs.getString("onduty_2"));
			   kqEmpClassBean.setOnduty_flextime_2(this.rs.getString("onduty_flextime_2"));
			   kqEmpClassBean.setBe_late_for_2(this.rs.getString("be_late_for_2"));
			   kqEmpClassBean.setAbsent_work_2(this.rs.getString("absent_work_2"));
			   kqEmpClassBean.setOnduty_end_2(this.rs.getString("onduty_end_2"));
			   kqEmpClassBean.setRest_start_2(this.rs.getString("rest_start_2"));
			   kqEmpClassBean.setRest_end_2(this.rs.getString("rest_end_2"));
			   kqEmpClassBean.setOffduty_start_2(this.rs.getString("offduty_start_2"));
			   kqEmpClassBean.setLeave_early_absent_2(this.rs.getString("leave_early_absent_2"));
			   kqEmpClassBean.setLeave_early_2(this.rs.getString("leave_early_2"));    
			   kqEmpClassBean.setOffduty_2(this.rs.getString("offduty_2"));
			   kqEmpClassBean.setOffduty_flextime_2(this.rs.getString("offduty_flextime_2"));
			   kqEmpClassBean.setOffduty_end_2(this.rs.getString("offduty_end_2")); 
			    
			   kqEmpClassBean.setOnduty_start_3(this.rs.getString("onduty_start_3"));
			   kqEmpClassBean.setOnduty_3(this.rs.getString("onduty_3"));
			   kqEmpClassBean.setOnduty_flextime_3(this.rs.getString("onduty_flextime_3"));
			   kqEmpClassBean.setBe_late_for_3(this.rs.getString("be_late_for_3"));
			   kqEmpClassBean.setAbsent_work_3(this.rs.getString("absent_work_3"));
			   kqEmpClassBean.setOnduty_end_3(this.rs.getString("onduty_end_3"));
			   kqEmpClassBean.setRest_start_3(this.rs.getString("rest_start_3"));
			   kqEmpClassBean.setRest_end_3(this.rs.getString("rest_end_3"));
			   kqEmpClassBean.setOffduty_start_3(this.rs.getString("offduty_start_3"));
			   kqEmpClassBean.setLeave_early_absent_3(this.rs.getString("leave_early_absent_3"));
			   kqEmpClassBean.setLeave_early_3(this.rs.getString("leave_early_3"));    
			   kqEmpClassBean.setOffduty_3(this.rs.getString("offduty_3"));
			   kqEmpClassBean.setOffduty_flextime_3(this.rs.getString("offduty_flextime_3"));
			   kqEmpClassBean.setOffduty_end_3(this.rs.getString("offduty_end_3")); 
			   
			   kqEmpClassBean.setOnduty_start_4(this.rs.getString("onduty_start_4"));
			   kqEmpClassBean.setOnduty_4(this.rs.getString("onduty_4"));
			   kqEmpClassBean.setOnduty_flextime_4(this.rs.getString("onduty_flextime_4"));
			   kqEmpClassBean.setBe_late_for_4(this.rs.getString("be_late_for_4"));
			   kqEmpClassBean.setAbsent_work_4(this.rs.getString("absent_work_4"));
			   kqEmpClassBean.setOnduty_end_4(this.rs.getString("onduty_end_4"));
			   kqEmpClassBean.setRest_start_4(this.rs.getString("rest_start_4"));
			   kqEmpClassBean.setRest_end_4(this.rs.getString("rest_end_4"));
			   kqEmpClassBean.setOffduty_start_4(this.rs.getString("offduty_start_4"));
			   kqEmpClassBean.setLeave_early_absent_4(this.rs.getString("leave_early_absent_4"));
			   kqEmpClassBean.setLeave_early_4(this.rs.getString("leave_early_4"));    
			   kqEmpClassBean.setOffduty_4(this.rs.getString("offduty_4"));
			   kqEmpClassBean.setOffduty_flextime_4(this.rs.getString("offduty_flextime_4"));
			   kqEmpClassBean.setOffduty_end_4(this.rs.getString("offduty_end_4")); 
			   
				//other
			   kqEmpClassBean.setNight_shift_start(this.rs.getString("night_shift_start"));
			   kqEmpClassBean.setNight_shift_end(this.rs.getString("night_shift_end"));
			   kqEmpClassBean.setWork_hours(this.rs.getString("work_hours"));
			   kqEmpClassBean.setZero_absent(this.rs.getString("zero_absent"));
			   // kqEmpClassBean.setZeroflag(this.rs.getString("zeroflag"));
			   // kqEmpClassBean.setDomain_count(this.rs.getString("domain_count"));
			   //kqEmpClassBean.setOne_absent(this.rs.getString("one_absent"));
			   kqEmpClassBean.setCard_time(this.rs.getString("card_time"));
			   kqEmpClassBean.setKq_dkind(this.rs.getString("dkind"));
			   kqEmpClassBean.setKq_type(this.rs.getString("q03z3"));
		   }catch(Exception e)
		   {
			  e.printStackTrace(); 
		   }
		   return kqEmpClassBean;
	}
	/**
	 * 返回夜班时间时间
	 * @return
	 */
	public float getNightTimeLen(String str_date)
	{
		float itemLen=0;
		float nitemLen=0;
		
		String night_start=this.getNight_shift_start();
		String night_end=this.getNight_shift_end();
		if(night_start==null||night_start.length()<=0) {
            return 0;
        }
		if(night_end==null||night_end.length()<=0) {
            return 0;
        }
		String day=str_date;
		Date nFT=DateUtils.getDate(day+" "+night_start,"yyyy.MM.dd HH:mm");
		Date nTT=DateUtils.getDate(day+" "+night_end,"yyyy.MM.dd HH:mm");
		float n_itemLen=KQRestOper.getPartMinute(nFT,nTT);
		/*if(itemLen<0)//跨天了
		{
			nTT=DateUtils.addDays(nTT,1);
		}*/
		String strFTime="";
		String strTTime="";
		Date FTime=null;
		Date TTime=null;
		Date date=DateUtils.getDate(day,"yyyy.MM.dd");
		KqUtilsClass kqUtilsClass=new KqUtilsClass();
		boolean isCorrect=false;
		for(int i=1;i<=3;i++)
		{
			strFTime=this.getOnduty(i+"");
			strTTime=this.getOffduty(i+"");
			if((strFTime==null||strFTime.length()<=0)||(strTTime==null||strTTime.length()<=0)) {
                continue;
            }
			 FTime=DateUtils.getDate(DateUtils.format(date,"yyyy.MM.dd")+" "+strFTime,"yyyy.MM.dd HH:mm");
			 TTime=DateUtils.getDate(DateUtils.format(date,"yyyy.MM.dd")+" "+strTTime,"yyyy.MM.dd HH:mm");
			 float nightFTbj=KQRestOper.getPartMinute(nFT,TTime);
			 float nightTTbj=KQRestOper.getPartMinute(nTT,TTime);
			 if(nightFTbj>0&&nightTTbj<=0&&n_itemLen<0&&!isCorrect)
			 {
				 nTT=DateUtils.addDays(nTT,1);
				 isCorrect=true;
			 }else if(nightFTbj<0&&nightTTbj>=0&&n_itemLen<0&&!isCorrect)
			 {
				 nFT=DateUtils.addDays(nFT,-1);
				 isCorrect=true;
			 }
			 itemLen=KQRestOper.getPartMinute(FTime,TTime);
			 if(itemLen<0)//跨天了
			 {
				 TTime=DateUtils.addDays(TTime,1);
				 date=DateUtils.addDays(date,1);
			 }
			 nitemLen=nitemLen+kqUtilsClass.calcTimSpan(FTime,TTime,nFT,nTT);
		}	
		return nitemLen;
	}
	
	/**
	 * 返回加班时间
	 * @return
	 */
	public float getOverTimeLen()
	{
		float itemLen=0;
		String over_start=this.getOnduty_4();
		String over_end=this.getOffduty_4();
		if(over_start==null||over_start.length()<=0) {
            return 0;
        }
		if(over_end==null||over_end.length()<=0) {
            return 0;
        }
		itemLen=getTimeLen(over_start,over_end);
		return itemLen;		
	}
	
	public float getTimeLen(String s_Tiem,String E_Tiem)
	{
		float itemLen=0;
		Date nFT=DateUtils.getDate(s_Tiem,"HH:mm");
		Date nTT=DateUtils.getDate(E_Tiem,"HH:mm");
		itemLen=KQRestOper.getPartMinute(nFT,nTT);
		if(itemLen<0)//跨天了
		{
			Date zone_d=DateUtils.getDate("24:00","HH:mm");
			itemLen=KQRestOper.getPartMinute(nFT,zone_d);
			zone_d=DateUtils.getDate("00:00","HH:mm");
			itemLen=KQRestOper.getPartMinute(zone_d,nTT)+itemLen;
		}
		return itemLen;
	}
	public String getBe_late_for(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getBe_late_for_1();
		}else if("2".equals(i))
		{
			   value=this.getBe_late_for_2();
		}else if("3".equals(i))
		{
			   value=this.getBe_late_for_3();
		}else if("4".equals(i))
		{
			   value=this.getBe_late_for_4();
		}	
		return value;	
	}
	public String getAbsent_work(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getAbsent_work_1();
		}else if("2".equals(i))
		{
			   value=this.getAbsent_work_2();
		}else if("3".equals(i))
		{
			   value=this.getAbsent_work_3();
		}else if("4".equals(i))
		{
			   value=this.getAbsent_work_4();
		}	
		return value;	
	}
	public String getLeave_early_absent(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getLeave_early_absent_1();
		}else if("2".equals(i))
		{
			   value=this.getLeave_early_absent_2();
		}else if("3".equals(i))
		{
			   value=this.getLeave_early_absent_3();
		}else if("4".equals(i))
		{
			   value=this.getLeave_early_absent_4();
		}	
		return value;	
	}
	public String getLeave_early(String i)
	{
		String value="";	
		if("1".equals(i))
		{
			   value=this.getLeave_early_1();
		}else if("2".equals(i))
		{
			   value=this.getLeave_early_2();
		}else if("3".equals(i))
		{
			   value=this.getLeave_early_3();
		}else if("4".equals(i))
		{
			   value=this.getLeave_early_4();
		}	
		return value;	
	}
	public int getOffdutyTimeIndex()
	{
		String offTime="";
		int i=3;
		for(;i>0;i--)
		{
			offTime=this.getOffduty();
			if(offTime!=null&&!"".equals(offTime)) {
                break;
            }
				
		}
		return i;
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
	public String getOnduty_flextime_1() {
		return onduty_flextime_1;
	}
	public void setOnduty_flextime_1(String onduty_flextime_1) {
		this.onduty_flextime_1 = onduty_flextime_1;
	}
	public String getOffduty_end()
	{
		 
		 if(this.getOffduty_end_3()!=null&&this.getOffduty_end_3().length()>0)
		 {
			 return this.getOffduty_end_3();
		 }else if(this.getOffduty_end_2()!=null&&this.getOffduty_end_2().length()>0)
		 {
			 return this.getOffduty_end_2();
		 }else if(this.getOffduty_end_1()!=null&&this.getOffduty_end_1().length()>0)
		 {
			 return this.getOffduty_end_1();
		 }else
		 {
			 return "";
		 }
	}
	public float getFlextimeLen() {
		return flextimeLen;
	}
	public void setFlextimeLen(float flextimeLen) {
		this.flextimeLen = flextimeLen;
	}
	/**
	 * 弹性时间长度
	 * @return
	 */
	private float CalcFlexTimeLen()
	{
		float value=0;
		String onTime_str=this.getOnduty_1();
		if(onTime_str==null||onTime_str.length()<=0)
		{
			value=0;
			return value;
		}	
		String flexTime_str=this.getOnduty_flextime_1();
		if(flexTime_str==null||flexTime_str.length()<=0)
		{
			value=0;
			return value;
		}
		Date onTime=DateUtils.getDate("2007.10.09 "+onTime_str,"yyyy.MM.dd HH:mm");
		Date filexTime=DateUtils.getDate("2007.10.09 "+flexTime_str,"yyyy.MM.dd HH:mm");
		float itemLen=KQRestOper.getPartMinute(onTime,filexTime);
		if(itemLen<0)
		{
			filexTime=DateUtils.addDays(filexTime,1);
			itemLen=KQRestOper.getPartMinute(onTime,filexTime);
		}
		return itemLen;
	}
	public String getA0101() {
		return a0101;
	}
	public void setA0101(String a0101) {
		this.a0101 = a0101;
	}
}
