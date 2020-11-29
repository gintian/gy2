package com.hjsj.hrms.module.recruitment.headhuntermanage.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;

public class UpdateHunterUserTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		boolean result = true;
		try {
			String groupid = (String)this.getFormHM().get("huntergroupid");
			groupid = PubFunc.decrypt(groupid);
			String committype = (String)this.getFormHM().get("committype");
			String tablekey = (String)this.getFormHM().get("tablekey");
			DynaBean commitdata = (DynaBean)this.formHM.get("commitdata");
			
			String name = PubFunc.keyWord_filter((String)commitdata.get("name"));
			String username = PubFunc.keyWord_reback((String)commitdata.get("username"));
			String password = (String)commitdata.get("password");
			String email = (String)commitdata.get("email");
			String tel = (String)commitdata.get("tel");
			String phone = (String)commitdata.get("phone");
			Boolean isused  = (Boolean)commitdata.get("isused");
			Boolean isleader = (Boolean)commitdata.get("isleader");
			
			ArrayList values = new ArrayList();
			values.clear();
			values.add(name);
			values.add(password);
			values.add(email);
			values.add(tel);
			values.add(phone);
			values.add(isused.booleanValue()?"1":"2");
			values.add(isleader.booleanValue()?"1":"2");
			values.add(username);
			values.add(groupid);
			String sql = "";
			ContentDAO dao = new ContentDAO(frameconn);
			if("insert".equals(committype)){
				sql = " insert into zp_headhunter_login(name,password,email,tel,phone,isused,isleader,username,z6000) values(?,?,?,?,?,?,?,?,?) ";
				dao.insert(sql, values);
			}else{
				sql = " update zp_headhunter_login set name=?,password=?,email=?,tel=?,phone=?,isused=?,isleader=? where username=? and z6000=?";
				dao.update(sql, values);
			}
		
			//数据变动后同步一下表格数据
			sql = "select username,password,name,email,tel,phone,isused,isleader from zp_headhunter_login where z6000='"+groupid+"'";
			ArrayList datalist = (ArrayList) ExecuteSQL.executeMyQuery(sql, frameconn);
			TableDataConfigCache tableCache = (TableDataConfigCache)userView.getHm().get(tablekey);
			tableCache.setTableData(datalist);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}finally{
			this.getFormHM().put("result",new Boolean(result));
		}
		
		
	}

}
