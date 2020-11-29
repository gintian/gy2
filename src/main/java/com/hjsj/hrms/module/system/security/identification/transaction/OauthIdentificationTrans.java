package com.hjsj.hrms.module.system.security.identification.transaction;

import com.hjsj.hrms.transaction.mobileapp.binding.BindingConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * <p>Title: OauthIdentificationTrans </p>
 * <p>Description: </p>
 * <p>Company: hjsj</p>
 * <p>Create Time: 2015-7-8 下午1:15:38</p>
 * @author jingq
 * @version 1.0
 */
public class OauthIdentificationTrans extends IBusiness{

	private static final long serialVersionUID = 1L;

	@Override
    @SuppressWarnings("unchecked")
	public void execute() throws GeneralException {
		StringBuffer sql = new StringBuffer();
		String username = "";
		String flag = "ok";
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = sdf.format(new Date());
			String selsql = "select username,mobile_bind_id from t_sys_login_user_info where username in (";
			sql.append("update t_sys_login_user_info set mobile_oauth_time = ");
			if(Sql_switcher.searchDbServer()==2)//【11406】App登陆认证：用户通过移动app登陆，人工激活，业务用户点击认证按钮报错，无法认证
				sql.append("to_date('"+time+"','yyyy-mm-dd hh24:mi:ss')");
			else
				sql.append("'"+time+"'");
			sql.append(",mobile_is_oauth = '1' where username in (");
			username = (String) this.getFormHM().get("username");
			if(username==null||"".equals(username)){
				ArrayList<MorphDynaBean> list = (ArrayList<MorphDynaBean>) this.getFormHM().get("oauth");
				for (int i = 0; i < list.size(); i++) {
					DynaBean row = list.get(i);
					username = (String) row.get("username");
					String mobile_is_oauth = (String) row.get("mobile_is_oauth");
					if("1".equals(mobile_is_oauth))
						flag = "confirm";
					sql.append("'"+username+"'");
					selsql += "'"+username+"'";
					if(i<list.size()-1){
						sql.append(",");
						selsql += ",";
					}
				}
			} else {
				sql.append("'"+username+"'");
				selsql += "'"+username+"'";
			}
			sql.append(") and mobile_is_oauth <> '1'");
			selsql += ")";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			dao.update(sql.toString());
			this.frowset = dao.search(selsql);
			String bind_id = "";
			while(this.frowset.next()){
				username = this.frowset.getString("username");
				bind_id = this.frowset.getString("mobile_bind_id");
				BindingConstant.smsAouth(username, bind_id);
			}
		} catch (Exception e){
			flag = e.getMessage();
			e.printStackTrace();
		}
		this.getFormHM().put("result", flag);
	}

}
