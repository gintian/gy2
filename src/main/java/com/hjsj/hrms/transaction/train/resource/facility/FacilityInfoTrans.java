package com.hjsj.hrms.transaction.train.resource.facility;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class FacilityInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try{
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			//区分是否第一次进入
			String tmp = (String) hm.get("fieldId");
			hm.remove("fieldId");
			
			String fieldId = (String) this.getFormHM().get("fieldId");
			fieldId = PubFunc.decrypt(SafeCode.decode(fieldId));

			String fieldName = (String) this.getFormHM().get("fieldName");

			String startdate = (String)this.getFormHM().get("startdate");
			String enddate = (String)this.getFormHM().get("enddate");
			
			if(tmp!=null&&tmp.length()>0){
				Date date = new Date();
				startdate =  DateUtils.FormatDate(date,"yyyy-MM") + "-01";
				enddate = DateUtils.FormatDate(date,"yyyy-MM-dd");
				this.formHM.put("startdate", startdate);
				this.formHM.put("enddate", enddate);
			}
			if(startdate!=null&&startdate.length()>=10&&enddate!=null&&enddate.length()>=10){
				Date _t1 = DateUtils.getDate(startdate, "yyyy-MM-dd");
				Date _t2 = DateUtils.getDate(enddate, "yyyy-MM-dd");
				if(_t1.getTime()>_t2.getTime()){
					startdate = DateUtils.FormatDate(_t2,"yyyy-MM-dd");
					enddate = DateUtils.FormatDate(_t1,"yyyy-MM-dd");
				}
			}
			
			ArrayList list = DataDictionary.getFieldList("r59", Constant.USED_FIELD_SET);
			if(list == null){
				throw new GeneralException(ResourceFactory.getProperty("train.R59.nofound"));
			}
			
			for (int i = 0; i < list.size(); i++) {
				FieldItem item = (FieldItem) list.get(i);
				if (!item.isVisible())
					list.remove(i--);
				if("R5900".equalsIgnoreCase(item.getItemid()) || "R1101".equalsIgnoreCase(item.getItemid())
						|| "E01A1".equalsIgnoreCase(item.getItemid()) || "nbase".equalsIgnoreCase(item.getItemid())
						|| "A0100".equalsIgnoreCase(item.getItemid()) || "nbase_R".equalsIgnoreCase(item.getItemid())
						|| "A0100_R".equalsIgnoreCase(item.getItemid()) || "E01A1_R".equalsIgnoreCase(item.getItemid()))
					if(i >= 0){					
						list.remove(i--);
					}
			}
			
			//使用记录编号，数量，借出日期，借出人员库，借出人编码，借出人姓名，借出人单位，借出人部门，返还日期，返还人员库，返还人姓名，返还人单位，返还人部门，备注，操作员，操作日前
			String columns="r5900,r5901,r5903,nbase,a0100,a0101,b0110,e0122,r5905,nbase_r,a0100_r,a0101_r,b0110_r,e0122_r,r5911,r5913,r5907,r5909";
			String strsql="select r5900,r5901,r5903,nbase,a0100,a0101,b0110,e0122,r5905,nbase_r,a0100_r,a0101_r,b0110_r,e0122_r,r5911,r5913,r5907,r5909";
			String order_by = " order by r5909 desc"; // order by r5903 desc
			StringBuffer strwhere = new StringBuffer();
			strwhere.append(" from r59 where r1101='"+fieldId+"'");
			if(startdate!=null&&startdate.length()>=10){
				strwhere.append(" and (r5903>="+Sql_switcher.dateValue(startdate)+" or r5905>="+Sql_switcher.dateValue(startdate)+")");
			}
			if(enddate!=null&&enddate.length()>=10){
				strwhere.append(" and (r5903<="+Sql_switcher.dateValue(enddate)+" or r5905<="+Sql_switcher.dateValue(enddate)+")");
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
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}