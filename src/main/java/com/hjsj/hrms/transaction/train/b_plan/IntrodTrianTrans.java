package com.hjsj.hrms.transaction.train.b_plan;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.businessobject.train.TrainPlanBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:培训计划</p>
 * <p>Description:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class IntrodTrianTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
//		 TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String r2501 = (String)hm.get("r2501");
		r2501=r2501!=null?r2501:"";
		hm.remove("r2501");
		
		String model = (String)hm.get("model");
		model=model!=null?model:"1";
		hm.remove("model");
		
		String b0110 = (String)hm.get("b0110");
		b0110=b0110!=null?b0110:"";
		hm.remove("b0110");
		
		String e0122 = (String)hm.get("e0122");
		e0122=e0122!=null?e0122:"";
		hm.remove("e0122");
		
		if(!this.userView.isSuper_admin()){
		    TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		    if(!bo.checkPlanPiv(r2501, this.userView))
		        throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("train.info.chang.nopiv")));
		}
		
		ArrayList list = new ArrayList();
		ArrayList fieldlist = DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
		StringBuffer buf = new StringBuffer();
		StringBuffer wherestr = new StringBuffer();
		StringBuffer columns = new StringBuffer();
		buf.append("select ");
		for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if(!fielditem.isVisible())
			 continue;
			
			if("r3125".equalsIgnoreCase(fielditem.getItemid()))
				buf.append("(select R2502 from R25 where R2501=r31.R3125) as r3125");
			else
				buf.append(fielditem.getItemid());
			
			columns.append(fielditem.getItemid());
			buf.append(",");
			columns.append(",");
			if("r3101".equalsIgnoreCase(fielditem.getItemid())){
				list.add(0,fielditem);
			}else
				list.add(fielditem);
		}
		
		wherestr.append(" from r31 where r3127='03' and (r3125 is null or r3125='')");
		if(b0110!=null&&b0110.trim().length()>0)
			wherestr.append(" and b0110='"+b0110+"'");
//		if(e0122!=null&&e0122.trim().length()>0)
//			wherestr.append(" and e0122='"+e0122+"'");
		
		TrainPlanBo bo = new TrainPlanBo(this.frameconn);
		if(!bo.checkPlanPiv(r2501, this.userView))
		    wherestr.append(" and 1=2");
		else if(!this.userView.isSuper_admin()){
		    String where = TrainCourseBo.getUnitIdByBusiStrWhere(this.userView);
		    wherestr.append(" " + where.replaceFirst("where", "and"));
		}
		
		this.getFormHM().put("tablename","r31");
		this.getFormHM().put("itemlist",list);
		this.getFormHM().put("model",model);
		this.getFormHM().put("r2501",r2501);
		this.getFormHM().put("sql",buf.substring(0,buf.length()-1));
		this.getFormHM().put("wherestr",wherestr.toString());
		this.getFormHM().put("columns",columns.substring(0,columns.length()-1));
	}

}
