package com.hjsj.hrms.module.qualifications.transaction;


import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

public class AddConditionTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		String module_type = (String) this.getFormHM().get("module_type");
		String zc_series = (String)this.getFormHM().get("zc_series");
		IDFactoryBean idf = new IDFactoryBean();
		String condition_id = idf.getId("zc_condition.condition_id","",this.frameconn);		
		this.getFormHM().put("condition_id", PubFunc.encrypt(condition_id));
		//获取当前时间
		java.util.Date dt = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		
		String sql = "insert into zc_condition (condition_id,zc_series,description,create_time,create_user,create_fullname,modify_time,b0110,module_type) "
				+ "values(?,?,?,?,?,?,?,?,?)";
		ContentDAO dao = new ContentDAO(this.frameconn);
		ArrayList list = new ArrayList();
		list.add(condition_id);
		list.add(zc_series);
		list.add("");
		list.add(DateUtils.getSqlDate(currentTime,"yyyy-MM-dd"));
		list.add(getUserView().getUserName());
		list.add(getUserView().getUserFullName());
		list.add(DateUtils.getSqlDate(currentTime,"yyyy-MM-dd"));
		String busiId = "9";
		if("2".equals(module_type))
			busiId = "10";
		String unit = getUserView().getUnitIdByBusi(busiId);
		if(unit.length()>0){
			String[] units = unit.split("`");
			if(units[0].contains("UN")){
				units[0]=units[0].replace("UN", "");
			}
			if(units[0].contains("UM")){
				units[0]=units[0].replace("UM", "");
			}
			list.add(units[0]);
		}
		list.add(module_type);
		try {
			dao.insert(sql, list);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("revalue", "success");
	}

		
}
