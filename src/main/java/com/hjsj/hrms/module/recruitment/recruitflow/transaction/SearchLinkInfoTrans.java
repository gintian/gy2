package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>
 * Title:SearchRecruitHj.java
 * </p>
 * <p>
 * Description:浏览招聘流程的具体环节
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2015-5-13 下午03:47:33
 * </p>
 * 
 * @author zx
 * @version 1.0
 *
 */
public class SearchLinkInfoTrans extends IBusiness {

    @Override
    public void execute() throws GeneralException {
    	try {
            HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String linkid = (String) hm.get("linkid");
			String sysName = (String) hm.get("sysName");
			String isParent = (String) hm.get("isParent");
			String flag = (String) hm.get("flag");
			String custom_name = "";

			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql = "select node_id,custom_name from zp_flow_links where id='"+linkid+"'";
			this.frowset = dao.search(sql);
			String nodeid = "";
			if(this.frowset.next()){
				nodeid=this.frowset.getString("node_id");
				custom_name=this.frowset.getString("custom_name");
			}
            this.getFormHM().put("linkid",linkid );
            this.getFormHM().put("flag",flag );
            this.getFormHM().put("isParent",isParent);
            this.getFormHM().put("sysName",sysName );
            this.getFormHM().put("node_id",nodeid );
            this.getFormHM().put("custom_name", PubFunc.nullToStr(custom_name));
	} catch (SQLException e) {
		e.printStackTrace();
	}
    }

}
