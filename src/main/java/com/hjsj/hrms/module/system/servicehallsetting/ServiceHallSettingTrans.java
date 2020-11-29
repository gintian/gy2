package com.hjsj.hrms.module.system.servicehallsetting;

import com.hjsj.hrms.module.card.businessobject.YkcardStaticBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 服务大厅方案设计保存交易类
 * @author guodd
 * Date: 2019年1月26日
 */
public class ServiceHallSettingTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		String transType = (String)this.formHM.get("transType");
		
		try {
			
			ContentDAO dao = new ContentDAO(this.frameconn);
			
			if("queryData".equals(transType)) {
				ArrayList groupList = getServiceData(this.frameconn,this.frowset);
				this.formHM.put("serviceData",groupList);
			}else if("addService".equals(transType)) {
				/*新增服务*/
				DynaBean saveData = (DynaBean)this.formHM.get("saveData");
				HashMap realData = PubFunc.DynaBean2Map(saveData);
				RecordVo vo = new RecordVo("t_sys_servicehall");
				// 获取新的ID
				IDFactoryBean bean = new IDFactoryBean();
				String serviceidStr = bean.getId("t_sys_servicehall.service_id", "", this.frameconn);
				int serviceid = Integer.parseInt(serviceidStr);
				vo.setInt("service_id", serviceid);
				vo.setObject("tabname",realData.get("tabname"));
				vo.setObject("type",realData.get("type"));
				vo.setObject("icon",realData.get("icon"));
				vo.setObject("tabid",realData.get("tabid"));
				vo.setObject("linkurl",realData.get("linkurl"));
				vo.setObject("taborder",realData.get("taborder"));
				vo.setObject("groupname",realData.get("groupname"));
				vo.setObject("grouporder",realData.get("grouporder"));
				dao.addValueObject(vo);
				this.formHM.put("serviceid", serviceid);
			}else if("updateService".equals(transType)){
				/*更新服务*/
				DynaBean saveData = (DynaBean)this.formHM.get("saveData");
				HashMap realData = PubFunc.DynaBean2Map(saveData);
				RecordVo vo = new RecordVo("t_sys_servicehall");
				vo.setInt("service_id", Integer.parseInt(realData.get("serviceid").toString()));
				vo.setObject("tabname",realData.get("tabname"));
				vo.setObject("type",realData.get("type"));
				vo.setObject("icon",realData.get("icon"));
				vo.setObject("tabid",realData.get("tabid"));
				vo.setObject("linkurl",realData.get("linkurl"));
				dao.updateValueObject(vo);
			}else if("delService".equals(transType)) {
				/*删除服务*/
				DynaBean saveData = (DynaBean)this.formHM.get("saveData");
				HashMap realData = PubFunc.DynaBean2Map(saveData);
				RecordVo vo = new RecordVo("t_sys_servicehall");
				vo.setObject("service_id",realData.get("serviceid"));
				dao.deleteValueObject(vo);
			}else if("sortService".equals(transType)) {
				/*服务排序*/
				List sortData = (List)this.formHM.get("saveData");
				String sql = "update t_sys_servicehall set  taborder=? where service_id=?";
				ArrayList values = new ArrayList();
				
				for(int i=0;i<sortData.size();i++) {
					DynaBean service = (DynaBean)sortData.get(i);
					ArrayList value = new ArrayList();
					value.add(service.get("taborder"));
					value.add(service.get("serviceid"));
					values.add(value);
				}
				dao.batchUpdate(sql, values);
			}else if("editGroup".equals(transType)) {
				/*编辑分类名称*/
				DynaBean saveData = (DynaBean)this.formHM.get("saveData");
				//HashMap realData = PubFunc.DynaBean2Map(saveData);
				String sql = "update t_sys_servicehall set  groupname=? where groupname=?";
				ArrayList values = new ArrayList();
				values.add(saveData.get("newgroupname"));
				values.add(saveData.get("oldgroupname"));
				dao.update(sql, values);
			}else if("delGroup".equals(transType)) {
				/*删除分类*/
				DynaBean saveData = (DynaBean)this.formHM.get("saveData");
				String sql = "delete from t_sys_servicehall where groupname=?";
				ArrayList values = new ArrayList();
				values.add(saveData.get("groupname"));
				dao.delete(sql, values);
			}else if("sortGroup".equals(transType)) {
				/*分类排序*/
				List sortData = (List)this.formHM.get("saveData");
				String sql = "update t_sys_servicehall set grouporder=? where groupname=?";
				ArrayList values = new ArrayList();
				for(int i=0;i<sortData.size();i++) {
					DynaBean service = (DynaBean)sortData.get(i);
					ArrayList value = new ArrayList();
					value.add(service.get("grouporder"));
					value.add(service.get("groupname"));
					values.add(value);
				}
				dao.batchUpdate(sql, values);
			}
			this.formHM.put("result", true);
		}catch(Exception e) {
			e.printStackTrace();
			this.formHM.put("result", false);
		}
	}
	
	/**
	 * 获取设置方案数据。在com.hjsj.hrms.utils.components.homewidget.servicehall.SearchTemplateTrans有调用此方法
	 * @param conn
	 * @param rs
	 * @return
	 */
	public ArrayList getServiceData(Connection conn,RowSet rs) {
		ArrayList groupList = new ArrayList();
		/*初始化查询数据*/
		String sql = "select * from t_sys_servicehall order by grouporder,taborder";
		ContentDAO dao = new ContentDAO(conn);
		try {
			rs = dao.search(sql);
			String currentGroupName = null;
			ArrayList serviceList = new ArrayList();
			while(rs.next()) {
				String groupname = rs.getString("groupname");
				if(currentGroupName!=null && !groupname.equals(currentGroupName)) {
					HashMap group = new HashMap();
					group.put("groupname", currentGroupName);
					group.put("services",serviceList);
					groupList.add(group);
					serviceList = new ArrayList();
				}
				currentGroupName = groupname;
				HashMap service = new HashMap();
				service.put("serviceid", rs.getInt("service_id"));
				service.put("tabname", rs.getString("tabname"));
				service.put("type", rs.getInt("type"));
				service.put("icon", rs.getString("icon"));
				service.put("tabid", rs.getInt("tabid"));
				service.put("linkurl", rs.getString("linkurl"));
				service.put("tempname",getTemplateName(rs.getInt("tabid"),rs.getInt("type"),conn));
				serviceList.add(service);
			}
			if(serviceList.size()>0) {
				HashMap group = new HashMap();
				group.put("groupname", currentGroupName);
				group.put("services",serviceList);
				groupList.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return groupList;
	}
	
	
	
	private String getTemplateName(int tabid,int type,Connection conn) {
		String tableName = "";
		if(type==1) {
			RecordVo vo = TemplateStaticDataBo.getTableVo(tabid, conn);
			if(vo==null)
				return "";
			tableName = tabid+":"+vo.getString("name");
		}else if(type==2) {
			List list;
			try {
				list = YkcardStaticBo.getRname(tabid+"",conn);
				if(list.size()==0)
					return "";
				LazyDynaBean rec= (LazyDynaBean)list.get(0);
				tableName = tabid+":"+rec.get("name");
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}
			
		}else {
			
		}
		return tableName;
	}

}
