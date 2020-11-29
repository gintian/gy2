/**
 * 
 */
package com.hjsj.hrms.businessobject.report.user_defined_reoprt;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hrms.frame.utility.AdminDb;

import java.sql.Connection;

/**
 * 获得薪资共享管理员名称
 * <p>
 * Title:SalaryShareAdmin.java
 * </p>
 * <p>
 * Description>:获得薪资共享管理员名称
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:2011-02-22 09:43:32
 * </p>
 * <p>
 * 
 * @version: 1.0
 *           </p>
 *           <p>
 * @author: wangzhongjun
 */

public class SalaryShareAdmin implements IJavaCode{

	@Override
    public String getValue(String param) {
		Connection conn = null;
		String admin = "";
		try {
			conn = AdminDb.getConnection();
			SalaryCtrlParamBo ctrlparam = new SalaryCtrlParamBo(conn,Integer.parseInt(param));
			admin = ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		admin = admin == null ? "" : admin;
		return admin;
	}
	
}
