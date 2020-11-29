package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：GetComputeCondTrans 
 * 类描述：获取计算条件 
 * 创建人：zhaoxg
 * 创建时间：Jun 15, 2015 2:00:43 PM
 * 修改人：zhaoxg
 * 修改时间：Jun 15, 2015 2:00:43 PM
 * 修改备注： 
 * @version
 */
public class GetComputeCondTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			HashMap hm=this.getFormHM();		
			String id = (String)hm.get("id");//薪资类别id,或者人事异动模版id
			id=id!=null&&id.length()>0?id:"";
			
			String itemid = (String)hm.get("itemid");
			itemid=itemid!=null&&itemid.length()>0?itemid:"";
			
			String groupId=(String)hm.get("groupid");//人事异动-公式组id
			groupId=groupId!=null&&groupId.length()>0?groupId:"";
			String module = (String)this.getFormHM().get("module");//模块号，1是薪资模块
			String cond = "";		
			try {
				if("1".equals(module)){
					id = PubFunc.decrypt(SafeCode.decode(id));
					DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
					cond = bo.getComputeCond(id, itemid);
				}else if("3".equals(module)){//人事异动，gaohy,2016-1-5
					DefineFormulaBo bo = new DefineFormulaBo(this.frameconn,this.userView);
					cond = bo.getTempFormula(id, groupId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
			hm.put("conditions",SafeCode.encode(PubFunc.keyWord_filter(cond)));		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
