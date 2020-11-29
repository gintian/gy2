package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>Title:SearchMustWriteScoreTrans.java</p>
 * <p>Description>:评分说明必填高级设置</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Sep 09, 2011 10:10:46 AM</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchMustWriteScoreTrans extends IBusiness {
	
	public void execute() throws GeneralException {
		try {
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String status = (String) hm.get("status");
			String plan_id = (String) hm.get("plan_id");
			String tplId = (String) hm.get("tplId");
			
			String upIsValid = (String) hm.get("upIsValid");
			String downIsValid = (String) hm.get("downIsValid");
			String upDegreeId = (String) hm.get("upDegreeId");
			String downDegreeId = (String) hm.get("downDegreeId");
			String excludeDegree = (String) hm.get("excludeDegree");
			
			// 必填指标
			List requiredField = new ArrayList();
			ContentDAO dao = new ContentDAO(frameconn);
			StringBuffer strSql = new StringBuffer();
			strSql.append("select point_id,pointname from per_point where point_id in (");
			strSql.append("select distinct point_id from per_template_point where item_id in (");
			strSql.append("select item_id from per_template_item where Template_id='");
			strSql.append(tplId);
			strSql.append("')) and pointkind='0'");
			
			this.frowset = dao.search(strSql.toString());
			while (frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("point_id", "C_" + frowset.getString("point_id"));
				bean.set("pointname", frowset.getString("pointname"));
				bean.set("value", "");
				requiredField.add( bean);
			}
			
			this.getFormHM().put("status", status);
			this.getFormHM().put("upIsValid", upIsValid);
			this.getFormHM().put("upDegreeId", upDegreeId);
			this.getFormHM().put("downIsValid", downIsValid);
			this.getFormHM().put("downDegreeId", downDegreeId);
			this.getFormHM().put("excludeDegree", excludeDegree);
			this.getFormHM().put("requiredField", requiredField);
			
			LoadXml lx = new LoadXml(this.frameconn, plan_id);
			List MustFillOptionsList = (List) lx.getDegreeWhole().get("MustFillOptionsList");
			if (MustFillOptionsList != null && MustFillOptionsList.size() > 0) {
				LazyDynaBean requiredFieldBean = (LazyDynaBean) MustFillOptionsList.get(MustFillOptionsList.size() - 1);
				this.getFormHM().put("requiredFieldStr", requiredFieldBean.get("PointId"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		}
	}
			
}
