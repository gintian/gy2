package com.hjsj.hrms.transaction.sys.options.customreport;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * Title:SaveRoleTrans
 * </p>
 * <p>
 * Description:保存角色授权
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:2010-11-16
 * </p>
 * 
 * @author wangzhongjun
 * @version 1.0
 *  
 */
public class SaveRoleTrans extends IBusiness {
	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String base_ids = (String) hm.get("a_base_ids");
		String tabid = (String) hm.get("tabid");
		String num = (String) hm.get("num");
		if (tabid == null || tabid.length() == 0) {
			return;
		}
		
		if (num != null && "1".equals(num)) {
			// 删除角色授权
			deleteId(tabid);
		}
		
		String[] tab = tabid.split(",");
		StringBuffer str_value = new StringBuffer();
		for (int i = 0; i < tab.length; i++) {
			if (!userView.isHaveResource(IResourceConstant.CUSTOM_REPORT,
					tab[i]))
				continue;
			str_value.append(tab[i] + ",");
		}
		if (str_value.length() != 0)
			str_value.setLength(str_value.length() - 1);
		String[] base_id_array = base_ids.split(",");
		for (int i = 0; i < base_id_array.length; i++) {
			String roleid = base_id_array[i];
			if (roleid == null || roleid.length() <= 0)
				continue;
			SysPrivBo privbo = new SysPrivBo(roleid, GeneralConstant.ROLE, this
					.getFrameconn(), "warnpriv");
			String res_str = privbo.getWarn_str();
			ResourceParser parser = new ResourceParser(res_str,
					IResourceConstant.CUSTOM_REPORT);
			parser.addContent(str_value.toString());
			res_str = parser.outResourceContent();
			saveResourceString(roleid, GeneralConstant.ROLE, res_str);
		}
	}

	private String deleteId(String id) {		
 		String str = "";
 		ResultSet rs = null;
		try {
			List list = this.userView.getRolelist();
			if (this.userView.isSuper_admin()) {
				list = getAllRoleList();
			} else {
				list = this.userView.getRolelist();
			}
			if (list == null) {
				list = new ArrayList();
			}
			for (int i = 0; i < list.size(); i++) {
		    	String role_id = (String) list.get(i);
		    	SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
				String res_str=privbo.getWarn_str();
				ResourceParser parser = new ResourceParser(res_str,IResourceConstant.CUSTOM_REPORT);
				String role_str=parser.getContent();
                role_str=updateRoleContent(role_str,id);
                parser.reSetContent(role_str);
				res_str=parser.outResourceContent();								
				saveResourceString(role_id,GeneralConstant.ROLE,res_str);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return str;
	}
	
	private ArrayList getAllRoleList() {
		ArrayList list = new ArrayList();
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id from t_sys_function_priv where ");
		strsql.append(" status=");
		strsql.append(GeneralConstant.ROLE);
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			this.frowset = dao.search(strsql.toString());
			while (frowset.next()) {
				String id = frowset.getString("id");
				id = id == null ? "" : id;
				list.add(id);
			}
		} catch (Exception e){
			e.printStackTrace();
		} 
		return list;
	}
	
	public String updateRoleContent(String role_content,String delete_id)
    {
    	if(role_content==null||role_content.length()<=0)
    		return "";
    	if(delete_id==null||delete_id.length()<=0)
    		return role_content;
    	String []role_contents=role_content.split(",");
    	StringBuffer buf=new StringBuffer();
    	for(int i=0;i<role_contents.length;i++)
    	{
    	  String one_content=role_contents[i];
    	  if(!one_content.equalsIgnoreCase(delete_id))
    	  {
    		  buf.append(one_content+",");
    	  }
    	}
    	if(buf.length()>0)
    		buf.setLength(buf.length()-1);
    	return buf.toString();
    }
	private void saveResourceString(String role_id, String flag, String res_str) {
		if (res_str == null)
			res_str = "";
		
		StringBuffer strsql = new StringBuffer();
		strsql.append("select id from t_sys_function_priv where id='");
		strsql.append(role_id);
		strsql.append("' and status=");
		strsql.append(flag);
		try {
			ArrayList paralist = new ArrayList();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset = dao.search(strsql.toString());
			cat.debug("select sql=" + strsql.toString());

			if (this.frowset.next()) {
				paralist.add(res_str);
				strsql.setLength(0);
				strsql.append("update t_sys_function_priv set warnpriv=?");

				strsql.append(" where id='");
				strsql.append(role_id);
				strsql.append("' and status=");
				strsql.append(flag);
			} else {
				paralist.add(role_id);
				paralist.add(res_str);
				strsql.setLength(0);
				strsql
						.append("insert into t_sys_function_priv (id,warnpriv,status) values(?,?,");
				strsql.append(flag);
				strsql.append(")");
			}
			cat.debug("updat warnpriv sql=" + strsql.toString());
			dao.update(strsql.toString(), paralist);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

}
