/**
 * 
 */
package com.hjsj.hrms.transaction.gz.sort;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPkgBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 保存人员默认排序
 * 
 * @author xujian 2009-9-14
 * 
 */
public class SaveDefOrder extends IBusiness {

	/**
	 * 
	 */
	public SaveDefOrder() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		Connection conn = null;
		try {
			String salaryid = (String) this.getFormHM().get("salaryid");
			String sortitem = (String) this.getFormHM().get("sortitem");
			String items[] = sortitem.split("`");
			StringBuffer orderby = new StringBuffer();
			for (int i = 0; i < items.length; i++) {
				String item = items[i];
				String properties[] = item.split(":");
				if (properties.length == 3) {
					orderby.append(properties[0]);
					if ("0".equalsIgnoreCase(properties[2])) {
						orderby.append(" desc,");
					} else if ("1".equalsIgnoreCase(properties[2])) {
						orderby.append(" asc,");
					}
				} else {
					continue;
				}
			}
			String sqlorderby = null;
			if (orderby.length() == 0) {
				sqlorderby = "";
			} else {
				sqlorderby = orderby.substring(0, orderby.length() - 1);
			}
			// 如果没选A0000, A00Z0, A00Z1，把这三个指标做为默认排序指标放到最后
			/*if (sqlorderby.toUpperCase().indexOf("A0000") == -1) {
				if (sqlorderby.equals("")) {
					sqlorderby += "A0000";
				} else
					sqlorderby += ",A0000";
			}
			if (sqlorderby.toUpperCase().indexOf("A00Z0") == -1) {
				sqlorderby += ",A00Z0";
			}
			if (sqlorderby.toUpperCase().indexOf("A00Z1") == -1) {
				sqlorderby += ",A00Z1";
			}*/
			SalaryCtrlParamBo scpb = new SalaryCtrlParamBo(this.getFrameconn(),
					Integer.parseInt(salaryid));
			scpb.setValue(SalaryCtrlParamBo.DEFAULT_ORDER,"user_name",this.userView.getUserName(), sqlorderby);
			scpb.saveParameter();
			if(sortitem.length()>0)
				this.formHM.put("msg", "默认排序创建成功！");
			else
				this.formHM.put("msg", "默认排序清除成功！");
			/** 薪资类别 */
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(),
					Integer.parseInt(salaryid), this.userView);
			/** 人员同步 */
			gzbo.syncGzEmp(this.userView.getUserName(), salaryid);
			conn = AdminDb.getConnection();
			SalaryPkgBo salaryPkgBo = new SalaryPkgBo(conn,
					this.userView);
			salaryPkgBo.synSalaryTable(salaryid, gzbo.getGz_tablename());
		} catch (Exception e) {
			e.printStackTrace();
			this.formHM.put("msg", "操作失败！");
		}finally{
			if(conn!=null)
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

}
