package com.hjsj.hrms.module.workplan.yearplan.transaction;

import com.hjsj.hrms.module.workplan.yearplan.businessobject.YearPlanBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * 创建保存年度计划 changxy 
 * */
public class CreateYearPlanTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		YearPlanBo bo=new YearPlanBo(this.userView,this.frameconn);
		
		boolean flag=false;
		try {
			String typeflag=(String)this.getFormHM().get("typeflag");
			if("taskAssign".equals(typeflag)){//任务指派
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("bean");
				flag=bo.addTaskAssign(bean);
				
			}else if("getfielditems".equals(typeflag)){//获得创建计划的items
				ArrayList fielditems = DataDictionary.getFieldList("p17", Constant.ALL_FIELD_SET);
				this.getFormHM().put("fielditems", fielditems);
			}else{//保存任务
				MorphDynaBean bean = (MorphDynaBean)this.getFormHM().get("bean");
				HashMap map = PubFunc.DynaBean2Map(bean);
				String p1700=(String)map.get("p1700");//计划号
				String name=this.userView.getUserFullName();
				map.put("p1743", "0"+(String)map.get("p1743"));
				if(StringUtils.isBlank(p1700)){
					map.put("p1733", name);//创建人
					map.put("p1735", detilDate(null));//创建日期
				}
				if(p1700!=null&&!"".equals(p1700)){//编辑
					map.put("p1700", PubFunc.decrypt(p1700));
					map.put("p1737", name);//修改人
					map.put("p1739",detilDate(null));
					
					flag=bo.editYearPlan(map);
				}else{
					flag=bo.createYearPlan(map);
				}
			}
			this.getFormHM().put("flag", flag);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
	/**
	 * 处理日期格式
	 * */
	public String detilDate(String time){
		String dates="";
		try {
			if(time!=null){
				SimpleDateFormat sdf1=new SimpleDateFormat("yy-m-d");
				SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-mm-dd");
				Date date=sdf1.parse(time);
				dates=sdf2.format(date);
			}else{
				SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dates=sdf1.format(new Date());
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return dates;
	}
}

