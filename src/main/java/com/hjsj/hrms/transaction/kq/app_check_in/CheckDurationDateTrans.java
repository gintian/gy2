package com.hjsj.hrms.transaction.kq.app_check_in;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.OperateDate;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;

/**
 * 判断开始和结束日期是否在当前考勤期间之后
 * @author Administrator
 *
 */
public class CheckDurationDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String resultStr = "ok";
		String z1 = (String) this.getFormHM().get("z1");
		String z3 = (String) this.getFormHM().get("z3");
		String z1str = (String) this.getFormHM().get("z1str");
		String z3str = (String) this.getFormHM().get("z3str");
		String cycle_num = (String) this.getFormHM().get("cycle_num");
		String flag = (String)this.getFormHM().get("flag");
		
		ArrayList rightFields = (ArrayList) this.getFormHM().get("right_fields");//周期排班、个人排班   排班对象
		
		if (!"1".equals(cycle_num) && null != cycle_num) {

			z3 = OperateDate.dateToStr(OperateDate.addDay(OperateDate.strToDate(z1, "yyyy-MM-dd"), 
					Integer.parseInt(cycle_num)),"yyyy.MM.dd");
		}else {
			z3 = z1;
		}
		String temp = "";
		if (z1 != null && z1.length() > 0) {
			if (! KqUtilsClass.comparentWithKqDuration(z1)) {
				temp = temp + z1str + "所在考勤期间已封存！";
			}
		}
		
		if (z3 != null && z3.length() > 0 && "2".equals(flag)) {
			if (! KqUtilsClass.comparentWithKqDuration(z3)) {
				temp = temp + "\r\n"+z3str + "所在考勤期间已封存！";
			}
		}
		
		Date startDate = OperateDate.strToDate(z1.replace(".", "-")+" "+"12:00", "yyyy-MM-dd HH:mm");
		Date endDate = OperateDate.strToDate(z3.replace(".", "-")+" "+"12:00", "yyyy-MM-dd HH:mm");
        
		boolean isCorrect = true;
		AnnualApply annualApply = new AnnualApply(this.userView,this.frameconn);
		String a_code=(String)this.getFormHM().get("a_code");
		String a_nbase=(String)this.getFormHM().get("nbase");
		
		String codesetid="";
		String codeitemid="";
		//UN UM @K EP @G 班组集   人员集
		if(a_code!=null&&!"UN".equalsIgnoreCase(a_code)&&a_code.indexOf("@G")==-1)
		{
			codesetid=a_code.substring(0,2); //UN,UM,@K
			codeitemid=a_code.substring(2);  //编号
		}else 
		{
			a_code  =  this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
	        String kind = "";
	        String code = "";
	        if(a_code == null||a_code.length()<= 0)
	        {
	            String privcode = RegisterInitInfoData.getKqPrivCode(userView);
	            if("UN".equalsIgnoreCase(privcode))
	                kind = "UN";
	            else if("UM".equalsIgnoreCase(privcode))
	                kind = "UM";
	            else if("@K".equalsIgnoreCase(privcode))
	                kind = "@K";
	            code = RegisterInitInfoData.getKqPrivCodeValue(userView);
	        }else
	        {
	            if(a_code.indexOf("UN")!= -1)
	            {
	                kind = "UN";
	            }else if(a_code.indexOf("UM")!= -1)
	            {
	                kind = "UM";
	            }else if(a_code.indexOf("@K")!= -1)
	            {
	                kind = "@K";
	            }
	            code = a_code.substring(2);
	        }
	        codesetid = kind;
	        codeitemid = code;
		}
		
		isCorrect = annualApply.KqDailyDataValidate(codesetid, codeitemid,a_nbase,startDate,endDate,rightFields);//判断考勤数据状态

        if (!isCorrect) 
		{
        	temp = "请求的业务日期包含的日明细数据已经提交，不可再编辑，不能做申请操作，请与考勤管理员联系！";
		}
        
		if (temp.length() > 0) {
			resultStr = temp;
		}
		
		this.getFormHM().put("resultStr", SafeCode.encode(resultStr));
		
	}
}
