package com.hjsj.hrms.transaction.train.resource.course.ability;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * @Title:        TrainAbilitySearchTrans.java 
 * @Description:  查看培训课程关联的素质指标
 * @Company:      hjsj     
 * @Create time:  2014-5-22 下午05:08:49 
 * @author:       chenxg
 * @version:      6.x
 */
public class TrainAbilitySearchTrans extends IBusiness {

	public void execute() throws GeneralException {

		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String r5000 = (String) hm.get("r5000");
		r5000 = PubFunc.decrypt(SafeCode.decode(r5000));
		ArrayList list = new ArrayList();
		String pivflag = "false";
		
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select a.point_id,b.seq,b.pointsetid,pointname,pointkind,b.validflag");
			sql.append(" from per_point_course a,per_point b,per_pointset c");
			sql.append(" where a.point_id=b.point_id");
			sql.append(" and b.pointsetid=c.pointsetid");
			sql.append(" and r5000='" + r5000 + "'");

			String where = TrainCourseBo.getUnitIdByBusiWhere(this.userView);

			if (where != null && where.length() > 0) {
				where = where.replaceAll("b0110", "c.b0110").substring(7);
				sql.append(" and (" + where + ")");
			}
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql.toString());
			while(this.frowset.next()){
				
				LazyDynaBean bean = new LazyDynaBean();	
				bean.set("point_id", this.frowset.getString("point_id"));
				bean.set("seq", this.frowset.getString("seq"));
				bean.set("pointsetid", this.frowset.getString("pointsetid"));
				bean.set("pointname", this.frowset.getString("pointname"));
				bean.set("pointkind", this.frowset.getString("pointkind"));
				bean.set("validflag", AdminCode.getCodeName("51",this.frowset.getString("validflag")));
				list.add(bean);
				
			}
			where = where.replaceAll("c.b0110", "r5020");
			sql.delete(0, sql.length());
			sql.append("select 1 from r50");
			sql.append(" where r5000='"+r5000+"'");
			
            if (!this.userView.isSuper_admin()) {
                sql.append(" and (" + where);
                sql.append(" or r5020 is null");
                sql.append(" or r5020='')");
            }
            
			this.frowset = dao.search(sql.toString());
			if(this.frowset.next())
			    pivflag = "true";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.getFormHM().put("abilitylist", list);
		this.getFormHM().put("r5000", SafeCode.encode(PubFunc.encrypt(r5000)));
		this.getFormHM().put("pivflag", pivflag);
	}

}
