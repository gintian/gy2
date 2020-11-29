package com.hjsj.hrms.transaction.sys.options.parttimeparamset;


import com.hjsj.hrms.businessobject.sys.options.parttimeparamset.ParttimeSetBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:InitParttimeParamSetTrans.java</p>
 * <p>Description:兼职参数设置</p>
 * <p>Company:HJSJ<p>
 * <p>Create time: 2007.06.08 13:00:00 pm</p>
 * @author lizhenwei
 * @version 4.0
 */

public class InitParttimeParamSetTrans extends IBusiness{
	public void execute() throws GeneralException{
		try{
			ParttimeSetBo bo = new ParttimeSetBo(this.getFrameconn());
			ArrayList setList = bo.getSetList();
			
			String setid = " ";
			String unit=" ";
			String appoint=" ";
			String flag="false";
			String part_pos="";
			String dept="";
			String order="";
			String format="";
			String takeup_quota="";
			String occupy_quota="";
			Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
			ArrayList list = new ArrayList();
			list.add("flag");//启用标识
			list.add("unit");//兼职单位标识
			list.add("setid");//兼职子集
			list.add("appoint");//任免标识
			list.add("pos");//任免职务
			list.add("dept");//兼职部门			
			list.add("order");//排序
			list.add("format");//兼职内容显示格式
			list.add("takeup_quota");//兼职占用岗位编制：1占用，0或null 则不占用
			list.add("occupy_quota");//兼职占用单位部门编制：1占用，0或null 则不占用
			HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
			if(map!=null&& map.size()!=0){
				if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
					flag=(String)map.get("flag");//启用标识
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
					unit=(String)map.get("unit");//兼职单位标识
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
					setid=(String)map.get("setid");//兼职子集
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0)
					appoint=(String)map.get("appoint");//任免标识
				if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0)
					part_pos=(String)map.get("pos");//任免职务
				if(map.get("dept")!=null && ((String)map.get("dept")).trim().length()>0)
					dept=(String)map.get("dept");//兼职部门
				if(map.get("order")!=null && ((String)map.get("order")).trim().length()>0)
					order=(String)map.get("order");//兼职排序
				if(map.get("format")!=null && ((String)map.get("format")).trim().length()>0)
					format=(String)map.get("format");//兼职格式
				if(map.get("takeup_quota")!=null && ((String)map.get("takeup_quota")).trim().length()>0)
					takeup_quota=(String)map.get("takeup_quota");//兼职岗位占用编制
				if(map.get("occupy_quota")!=null && ((String)map.get("occupy_quota")).trim().length()>0)
				    occupy_quota=(String)map.get("occupy_quota");//兼职单位部门占用编制
			}
			if("false".equalsIgnoreCase(flag)||" ".equalsIgnoreCase(setid))//zgd 2014-3-3 未设置兼职子集项，则其它项也不能设置，此时状态为未启用
			{
				flag="1";
			}else
			{
				flag="2";
			}
			ArrayList unitList =bo.getUnitList(setid);
			ArrayList appointList = bo.getAppointList(setid);
			ArrayList itemlist=bo.getCodeitemList(setid,"0");
			ArrayList nitemlist=bo.getNitemList(setid);
			ArrayList poslist=bo.getPosList(setid);
			this.getFormHM().put("flag",flag);
			this.getFormHM().put("unit",unit);
			this.getFormHM().put("setid",setid);
			this.getFormHM().put("appoint",appoint);
			this.getFormHM().put("setList",setList);
			this.getFormHM().put("unitList",unitList);
			this.getFormHM().put("appointList",appointList);
			this.getFormHM().put("itemlist", itemlist);
			this.getFormHM().put("nitemlist", nitemlist);
			this.getFormHM().put("poslist",poslist);
			this.getFormHM().put("pos", part_pos);
			this.getFormHM().put("dept",dept);
			this.getFormHM().put("order", order);
			this.getFormHM().put("format",format);
			this.getFormHM().put("takeup_quota", takeup_quota);
			this.getFormHM().put("occupy_quota", occupy_quota);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}