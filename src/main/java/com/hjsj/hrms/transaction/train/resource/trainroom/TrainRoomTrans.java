package com.hjsj.hrms.transaction.train.resource.trainroom;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TrainRoomTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String day = (String)hm.get("day");
		
		String fieldId = (String) this.getFormHM().get("fieldId");
		fieldId = PubFunc.decrypt(SafeCode.decode(fieldId));
		String fieldName = (String) this.getFormHM().get("fieldName");
		String year = (String)this.getFormHM().get("year");
		String month = (String)this.getFormHM().get("month");
		
		ArrayList list = DataDictionary.getFieldList("r61", Constant.USED_FIELD_SET);
		for (int i = 0; i < list.size(); i++) {
			FieldItem item = (FieldItem) list.get(i);
			if (!item.isVisible())
				list.remove(i--);
			
			if("r1001".equalsIgnoreCase(item.getItemid()) || "r6113".equalsIgnoreCase(item.getItemid())
					|| "E01A1".equalsIgnoreCase(item.getItemid()) || "nbase".equalsIgnoreCase(item.getItemid())
					|| "A0100".equalsIgnoreCase(item.getItemid()))
				list.remove(i--);
		}
		
		String columns="r1001,nbase,a0100,a0101,b0110,e0122,r6101,r6103,r6105,r6107,r6109,r6111,flag";
		String strsql = "";
		if(Sql_switcher.searchDbServer() == Constant.ORACEL){
			strsql="select r1001,nbase,a0100,a0101,b0110,e0122,to_char(r6101,'yyyy-MM-dd hh24:mi:ss') r6101,to_char(r6103,'yyyy-MM-dd hh24:mi:ss') r6103,r6105,r6107,r6109,r6111";
		}else{
			strsql="select r1001,nbase,a0100,a0101,b0110,e0122,r6101,r6103,r6105,r6107,r6109,r6111";
		}
		//flag=1 申请的结束时间>当前时间   管理人员只能对flag=1的记录进行操作
		strsql+=",(select 1 from R61 r where r.R1001=R61.R1001 and r.A0100=R61.A0100 and r.Nbase=R61.Nbase and r.R6101=R61.R6101 and r.R6103=R61.R6103 and r.R6103>"+Sql_switcher.dateValue(DateUtils.FormatDate(new Date(), "yyyy-MM-dd HH:mm:ss"))+") flag";
		String order_by = " order by a0100,r1001,r6101 desc";
		StringBuffer strwhere = new StringBuffer();
		strwhere.append(" from r61 where r6111 in ('02','03') and r1001='"+fieldId+"'");
		strwhere.append(" and "+Sql_switcher.year("r6101")+"='"+year+"'");
		strwhere.append(" and "+Sql_switcher.month("r6101")+"='"+month+"'");
		if(day!=null&&day.trim().length()>0){
			day = day.length()<2?"0"+day:day;
			strwhere.append(" and "+Sql_switcher.day("r6101")+"='"+day+"'");
		}
		
		this.formHM.put("fieldName", SafeCode.decode(fieldName));
		this.formHM.put("itemList", list);
		this.formHM.put("columns", columns);
		this.formHM.put("strsql", strsql);
		this.formHM.put("strwhere", strwhere.toString());
		this.formHM.put("order_by", order_by);
		
		Sys_Oth_Parameter sysoth = new Sys_Oth_Parameter(this.getFrameconn());
		String uplevel = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);// 显示部门层数
		if (uplevel == null || uplevel.length() == 0)
			uplevel = "0";
		this.getFormHM().put("uplevel", uplevel);
	}
}