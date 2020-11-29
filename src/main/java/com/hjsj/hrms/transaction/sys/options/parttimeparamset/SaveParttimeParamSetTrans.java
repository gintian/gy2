package com.hjsj.hrms.transaction.sys.options.parttimeparamset;


import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

public class SaveParttimeParamSetTrans extends IBusiness{

	public void execute() throws GeneralException {
	try{
		String setid="";
		String flag="false";
		String unit="";
		String appoint="";
		String pos="";
		String dept="";
		String order="";
		String format="";
		String takeup_quota="";
		String occupy_quota="";
		if(this.getFormHM().get("setid")!= null && ((String)this.getFormHM().get("setid")).trim().length()>0)
			setid=(String)this.getFormHM().get("setid");
		if(this.getFormHM().get("unit")!= null && ((String)this.getFormHM().get("unit")).trim().length()>0)
			unit=(String)this.getFormHM().get("unit");
		if(this.getFormHM().get("appoint")!= null && ((String)this.getFormHM().get("appoint")).trim().length()>0)
			appoint=(String)this.getFormHM().get("appoint");
		if(this.getFormHM().get("flag")!= null && ((String)this.getFormHM().get("flag")).trim().length()>0)
			flag=(String)this.getFormHM().get("flag");
		if(this.getFormHM().get("pos")!= null && ((String)this.getFormHM().get("pos")).trim().length()>0)
			pos=(String)this.getFormHM().get("pos");
		if(this.getFormHM().get("dept")!= null && ((String)this.getFormHM().get("dept")).trim().length()>0)
			dept=(String)this.getFormHM().get("dept");
		if(this.getFormHM().get("order")!= null && ((String)this.getFormHM().get("order")).trim().length()>0)
			order=(String)this.getFormHM().get("order");
		if(this.getFormHM().get("format")!= null && ((String)this.getFormHM().get("format")).trim().length()>0)
			format=(String)this.getFormHM().get("format");
		if(this.getFormHM().get("takeup_quota")!= null && ((String)this.getFormHM().get("takeup_quota")).trim().length()>0)
			takeup_quota=(String)this.getFormHM().get("takeup_quota");
		if(this.getFormHM().get("occupy_quota")!= null && ((String)this.getFormHM().get("occupy_quota")).trim().length()>0)
		    occupy_quota=(String)this.getFormHM().get("occupy_quota");
		if(unit!=null&&dept!=null&&unit.length()>0&&dept.length()>0&&unit.equalsIgnoreCase(dept))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("兼职单位指标与兼职部门指标不能重复"));  
		}
		if(order!=null&&format!=null&&order.length()>0&&format.length()>0&&order.equalsIgnoreCase(format))
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("兼任排序指标与兼职内容显示格式不能重复"));  
		}
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
		
		String oldAppoint = sysoth.getValue(Sys_Oth_Parameter.PART_TIME, "appoint");
		
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"setid",setid);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"unit",unit);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"appoint",appoint);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"flag",flag);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"pos",pos);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"dept",dept);		
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"order",order);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"format",format);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"takeup_quota",takeup_quota);
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME, "occupy_quota",occupy_quota);
		/*sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"unit","E0911");
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"dept","C0903");
		sysoth.setValue(Sys_Oth_Parameter.PART_TIME,"post","C0904");*/
		sysoth.saveParameter();
		
		
		//根据    <单位部门 兼职 是否占编 and 岗位兼职是否占编 and 任免指标>    同步编制控制临时表
		ScanFormationBo sfb = new ScanFormationBo(frameconn);
		if(("1".equals(takeup_quota) || "1".equals(occupy_quota)) && (!"".equals(appoint))){
		    if(appoint.equals(oldAppoint)){
		         sfb.updateAppoint("insert", appoint);
		    }else{
		         sfb.updateAppoint("delete", oldAppoint);
		         sfb.updateAppoint("insert", appoint);
		    }
	    }else{
		    sfb.updateAppoint("delete",oldAppoint);
		}
	}catch(Exception e){
		e.printStackTrace();
	}
		
	}

}
