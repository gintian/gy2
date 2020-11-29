package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 设置不同环节的权限
 * @author Administrator
 *
 */
public class AddAuthorityTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try{
			//增加类型 1 角色 2岗位 3成员 check类型
			String type = (String)this.formHM.get("type");
			//环节id
			String linkid = (String)this.formHM.get("linkid");
			//已存在的信息
			ArrayList<String> hadInfo = (ArrayList<String>)this.formHM.get("hadInfo");
			Object object = this.formHM.get("function");
			String function = object==null?"":(String)object;
			StringBuffer ids = new StringBuffer();
			if(hadInfo.size()>0){
				for (String id : hadInfo) {
					if(!"check".equals(type))
						ids.append(PubFunc.decrypt(id));
					else
						ids.append(id);
					ids.append(",");
				}
			}
			
			//用来返回前台
			ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
			//存放信息
			HashMap<String,String> map = new HashMap<String,String>();
			String id = "";
			String name = "";
			if(!"del".equals(function)&&!"check".equals(type)){
				//选中信息
				ArrayList<MorphDynaBean> infoList = (ArrayList)this.formHM.get("infoList");
				for (MorphDynaBean bean : infoList) {
					map = new HashMap<String,String>();
					if("1".equals(type)){
						id = (String)bean.get("role_id_e");
						name = (String)bean.get("role_name");
					}else{
						id = (String)bean.get("id");
						name = (String)bean.get("name");
					}
					hadInfo.add(id);
					ids.append(PubFunc.decrypt(id));
					ids.append(",");
					map.put("id", id);
					map.put("name", name);
					if("3".equals(type))
						map.put("photo", (String)bean.get("photo"));
					list.add(map);
				}
			}
			if(ids.toString().endsWith(","))
				ids.setLength(ids.length()-1);
			ContentDAO dao = new ContentDAO(frameconn);
			StringBuffer sql = new StringBuffer("update zp_flow_links ");
			if("1".equals(type))
				sql.append(" set role_id = ? ");
			else if("2".equals(type))
				sql.append(" set pos_id = ? ");
			else if("3".equals(type))
				sql.append(" set emp_id = ? ");
			else if("check".equals(type))
				sql.append(" set Member_type = ? ");
			
			sql.append(" where id = ? ");
			ArrayList values = new ArrayList();
			values.add(ids.toString());
			values.add(linkid);
			dao.update(sql.toString(),values);
			this.formHM.put("hadInfo", hadInfo);
			this.formHM.put("list", list);
			this.formHM.put("type", type);
			this.formHM.put("linkid", linkid);
		}catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
		}
	}
}
