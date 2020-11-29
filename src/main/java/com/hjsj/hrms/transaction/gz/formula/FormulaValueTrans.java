package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class FormulaValueTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		String itemid=(String)hm.get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		String salaryid = (String)hm.get("salaryid");
		salaryid=salaryid!=null&&salaryid.length()>0?salaryid:"";
		
		String useflag = (String)hm.get("useflag");
		useflag=useflag!=null&&useflag.length()>0?useflag:"";
		
		String runflag = (String)hm.get("runflag");
		runflag=runflag!=null&&runflag.length()>0?runflag:"";

		FormulaBo formulabo = new FormulaBo();
		if(useflag!=null&&useflag.length()>0){
			formulabo.alertUseflag(this.frameconn,useflag,salaryid,itemid);
		}
		
		if(runflag!=null&&runflag.length()>0){
			ContentDAO dao = new ContentDAO(this.frameconn);
			RecordVo vo=new RecordVo("salaryformula");
			vo.setInt("salaryid", Integer.parseInt(salaryid));
			vo.setInt("itemid", Integer.parseInt(itemid));
			try {
				vo = dao.findByPrimaryKey(vo);	
			} catch(SQLException e) {
				e.printStackTrace();
			}
			String value = vo.getString("runflag");//=0,计算公式 =1,执行标准 =2,执行税表
			if("0".equals(value)){
				value = "公式";
			}else if("1".equals(value)){
				value = "标准";
			}else if("2".equals(value)){
				value = "税率表";
			}
			StringBuffer context = new StringBuffer();
			SalaryTemplateBo bo = new SalaryTemplateBo(this.frameconn);
			String name = bo.getSalaryName(salaryid);
			context.append("执行："+name+"（"+salaryid+"）修改计算公式："+value+"--->公式<br>");
			this.getFormHM().put("@eventlog", context.toString());
			
			formulabo.alertRunflag(this.frameconn,runflag,salaryid,itemid);
		}else{
			runflag = formulabo.runFlag(this.frameconn,salaryid,itemid);
		}
		
		
		String formulavalue = formulabo.formulavalue(this.frameconn,salaryid,itemid);
		formulavalue=SafeCode.encode(formulavalue);

		hm.put("formulavalue",formulavalue);
		
		hm.put("runflag",runflag);

		hm.put("standid",formulabo.standId(this.frameconn,salaryid,itemid));
	}

}
