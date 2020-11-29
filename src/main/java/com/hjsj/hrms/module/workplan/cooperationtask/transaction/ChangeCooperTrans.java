package com.hjsj.hrms.module.workplan.cooperationtask.transaction;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.module.workplan.cooperationtask.businessobject.CooperationTaskBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.Arrays;

public class ChangeCooperTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void execute() throws GeneralException {
		try {
			CooperationTaskBo bo = new CooperationTaskBo(this.frameconn,this.userView);
			String id = PubFunc.decrypt((String)this.getFormHM().get("id"));
			String p1001 = (String)this.getFormHM().get("p1001");
			String p0800 = (String)this.getFormHM().get("p0800");
			String guidkey = (String)this.getFormHM().get("guidkey");
			String creater = (String)this.getFormHM().get("creater");
			String creater_sp = (String)this.getFormHM().get("creater_sp");
			String name = (String)this.getFormHM().get("name");
			String dbname = id.substring(0,3);
			String a0100 = id.substring(3);
			String coopGuidKey = bo.getGuidKey(a0100,dbname);
			if(coopGuidKey.equals(guidkey)){
				this.getFormHM().put("flag", "same");
			}else{
				String sql = "UPDATE P10 SET GUIDKE_OWNER = ?,p1015=?  WHERE P1001 = ?  AND GUIDKE_OWNER=?";
				ArrayList values = new ArrayList();
				values.add(coopGuidKey);
				values.add(name);
				values.add(PubFunc.decrypt(p1001));
				values.add(guidkey);
				ContentDAO dao = new ContentDAO(this.frameconn);
				dao.update(sql, values);
				ArrayList al = bo.getDetailByDbA0100ForP09(dbname,a0100);
				sql = "UPDATE P09 SET A0100=?,NBASE=?,P0907=?,P0909=?,P0911=?,P0913=? where P0905='01' AND P0903='"+p0800+"' AND Nbase = '"+dbname+"' AND A0100 = '"+bo.getA0100(guidkey, dbname)+"'";
				dao.update(sql, al);
				String b0110 = "";
				b0110 = new WorkPlanUtil(this.getFrameconn(), userView).getFristMainDept(dbname, a0100);
				sql = "UPDATE per_task_map SET nbase = ?,a0100=?,org_id=? WHERE P0800 ='"+p0800+"' AND flag = '1'";
				dao.update(sql, Arrays.asList(new Object[]{dbname,a0100,b0110}));
				this.getFormHM().put("flag", "different");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
