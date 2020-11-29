package com.hjsj.hrms.module.system.distributedreporting.setscheme.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 指标对应——获取用于对应的hr系统的代码项
 * @author Caoqy
 * @date 2019-3-20 15:11:44
 *
 */
public class GetFieldItemTrans extends IBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4928017495102807432L;

	@Override
	public void execute() throws GeneralException {
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		ArrayList<HashMap> setlist = new ArrayList<HashMap>();
		HashMap<String,String> map = null;
		RowSet rs = null;
		String itemid = (String) this.getFormHM().get("itemid");
		if(StringUtils.isBlank(itemid)) {
			return;
		}
		itemid = itemid.toUpperCase();
		try {
			rs = dao.search("SELECT codeitemid,codeitemdesc FROM codeitem WHERE codesetid = (SELECT codesetid FROM fielditem WHERE upper(itemid)='"+itemid+"')");
			while(rs.next()) {
				map = new HashMap<String,String>();
				String codeitemdesc = rs.getString("codeitemdesc");
				String codeitemid = rs.getString("codeitemid").toUpperCase();
				map.put("codeitemid", codeitemid);
				map.put("codeitemdesc", codeitemdesc);
				setlist.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeResource(rs);
		}
		this.getFormHM().put("list", setlist);
	}

}
