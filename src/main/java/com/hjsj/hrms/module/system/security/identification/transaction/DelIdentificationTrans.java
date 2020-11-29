package com.hjsj.hrms.module.system.security.identification.transaction;

import com.hjsj.hrms.transaction.mobileapp.binding.BindingConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;

import java.sql.PreparedStatement;
import java.util.ArrayList;

/**
 * <p>Title: DelIdentificationTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-7 下午5:49:59</p>
 * @author jingq
 * @version 1.0
 */
public class DelIdentificationTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		StringBuffer sql = new StringBuffer();
		String username = "";
		boolean flag = true;
		PreparedStatement ps = null;
		try{
			sql.append("delete from t_sys_login_user_info where username in (");
			username = (String) this.getFormHM().get("username");
			ArrayList<String> bindlist = new ArrayList<String>();
			if(username==null||"".equals(username)){
				ArrayList<MorphDynaBean> list = (ArrayList<MorphDynaBean>) this.getFormHM().get("deletedata");
				for (int i = 0; i < list.size(); i++) {
					DynaBean row = list.get(i);
					username = (String) row.get("username");
					sql.append("'"+username+"'");
					bindlist.add(username);
					if(i<list.size()-1)
						sql.append(",");
				}
			} else {
				sql.append("'"+username+"'");
				bindlist.add(username);
			}
			sql.append(")");
			ps = this.getFrameconn().prepareStatement(sql.toString());
			ps.execute();
			BindingConstant.delAouth(bindlist);
		} catch (Exception e){
			flag = false;
			e.printStackTrace();
		} finally {
			PubFunc.closeResource(ps);
		}
		this.getFormHM().put("result", flag);
	}

}
