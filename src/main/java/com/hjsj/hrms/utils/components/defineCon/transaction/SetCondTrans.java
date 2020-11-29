package com.hjsj.hrms.utils.components.defineCon.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineCon.businessobject.DefineConditionBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * 项目名称：hcm7.x
 * 类名称：SetCondTrans 
 * 类描述：条件定义初始化
 * 创建人：sunming
 * 创建时间：2015-7-21
 * @version
 */
public class SetCondTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		//模块号 为0时薪资发放--批量修改;   1：薪资发放计算公式;3:薪资属性提成工资数据范围  2:人事异动模块批量修改-条件组件
		String imodule = (String) hm.get("imodule");
		String opt = (String) hm.get("opt");
		DefineConditionBo bo=new DefineConditionBo(this.getFrameconn(),this.userView);
		if("0".equals(imodule)|| "1".equals(imodule)){
			//薪资id
			String id = (String)hm.get("primarykey");
			id=PubFunc.decrypt(SafeCode.decode(id));
			id=id!=null&&id.length()>0?id:"";
			
			//条件
			String conditions = (String)hm.get("conditions");
			conditions=conditions!=null&&conditions.length()>0?conditions:"";
			conditions = "undefined".equalsIgnoreCase(conditions)?"":conditions;
			hm.put("salaryid",id);
			hm.put("conditions",PubFunc.keyWord_reback(SafeCode.decode(conditions)));
			hm.put("itemlist",bo.conditionsList(id,imodule));
		}
		/**
		 * gaohy,
		 * 新增人事异动模块批量修改-条件组件
		 */
		else if("2".equals(imodule)){
			//人事异动id
			String id = (String)hm.get("primarykey");
			//条件
			String conditions = (String)hm.get("conditions");
			conditions=conditions!=null&&conditions.length()>0?conditions:"";
			conditions = "undefined".equalsIgnoreCase(conditions)?"":conditions;
			hm.put("conditions",PubFunc.keyWord_reback(SafeCode.decode(conditions)));
			hm.put("itemlist",bo.conditionsList(id,imodule));
			
		
		}else if("3".equals(imodule)){
			if("0".equals(opt)){
				String itemsetid = (String)this.getFormHM().get("itemsetid");
				this.getFormHM().put("itemlist", bo.getFieldList(itemsetid.toUpperCase()));
			}else if("1".equals(opt)){
				this.getFormHM().put("itemlist", bo.getUsedFieldSet());
			}
		}
	}

}
