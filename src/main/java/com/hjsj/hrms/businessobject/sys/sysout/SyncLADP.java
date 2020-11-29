package com.hjsj.hrms.businessobject.sys.sysout;

import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import org.apache.log4j.Category;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 同步ladp
 * <p>
 * Title:SyncLADP.java
 * </p>
 * <p>
 * Description>:SyncLADP.java
 * </p>
 * <p>
 * Company:HJSJ
 * </p>
 * <p>
 * Create Time:Oct 14, 2010 4:07:15 PM
 * </p>
 * <p>
 * 
 * @version: 5.0
 *           </p>
 *           <p>
 * @author: s.xin
 */
public class SyncLADP {

	private String hr_only_field = "";

	public SyncLADP(String hr_only_field) {
		this.hr_only_field = hr_only_field;
	}

	public void syncLadp() {
		if (hr_only_field == null || hr_only_field.length() <= 0) {
            return;
        }
		String LDAPBASEDN=SystemConfig.getPropertyValue("LDAPBASEDN");// 获得配置文件信息
		if(LDAPBASEDN==null||LDAPBASEDN.length()<=0) {
            LDAPBASEDN="ou=602users";
        }
		Connection conn = null;
		try {
			conn = (Connection) AdminDb.getConnection();			
			OrgLdap org = new OrgLdap(hr_only_field, conn);
			boolean isSync=org.exeOrg(LDAPBASEDN, org.getOrgInfo(), org.getUserInfo());
			if(isSync) {
                Category.getInstance("com.hjsj.hrms.businessobject.sys.sysout.SyncLADP").error("同步LDAP完成！");
            } else {
                Category.getInstance("com.hjsj.hrms.businessobject.sys.sysout.SyncLADP").error("同步LDAP失败！");
            }
		} catch (GeneralException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
                    conn.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}