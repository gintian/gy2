package com.hjsj.hrms.utils.components.defineformula.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 保存计算条件
 * 项目名称：hcm7.x
 * 类名称：SaveCalculaCondTrans 
 * 类描述： 
 * 创建人：zhaoxg
 * 创建时间：Aug 3, 2015 9:28:36 AM
 * 修改人：zhaoxg
 * 修改时间：Aug 3, 2015 9:28:36 AM
 * 修改备注： 
 * @version
 */
public class SaveCalculaCondTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		try {
			HashMap hm = this.getFormHM();
			ContentDAO dao = new ContentDAO(this.frameconn);
			String itemid = (String)hm.get("itemid");//公式id
			itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
			
			String id = (String)hm.get("id");//薪资类别id,人事异动模版Id
			id=id!=null&&id.trim().length()>0?id:"";
			
			String groupId=(String)hm.get("groupid");
			groupId=groupId!=null&&groupId.length()>0?groupId:"";
			
			String module = (String) this.getFormHM().get("module");//模块号，1是薪资
			
			String conditions = (String)hm.get("conditions");//计算公式内容
			conditions=conditions!=null&&conditions.trim().length()>0?conditions:"";
			conditions = SafeCode.decode(conditions);		
			conditions = PubFunc.keyWord_reback(conditions);
			ArrayList list = new ArrayList();
			if("1".equals(module)){
				id = PubFunc.decrypt(SafeCode.decode(id));
				String sqlstr = "update salaryformula set cond=? where salaryid=? and itemid=?";
				list.add(conditions);
				list.add(id);
				list.add(itemid);
				dao.update(sqlstr,list);
			}else if("3".equals(module)){//人事异动,gaohy,2016-1-5
				String sqlstr = "update gzAdj_formula set cFactor=? where TabId=? and Id=?";
				list.add(conditions);
				list.add(id);
				list.add(groupId);
				dao.update(sqlstr,list);
			}
			hm.put("info","ok");
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
