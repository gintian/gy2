package com.hjsj.hrms.utils.components.querybox.transaction;

import com.hjsj.hrms.utils.components.querybox.businessobject.QueryboxBo;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方案查询
 * @author wangzj
 * 2015-06-30 10:08:00
 */
public class QueryBoxTrans extends IBusiness{
	
	private static final long serialVersionUID = 1L;

	public void execute() throws GeneralException {
		// 查询类型,1为查询，2为保存
		String type = (String) this.getFormHM().get("type");
		// 模块id
		String subm = (String) this.getFormHM().get("subModuleId");
		
		if (("1".equals(type) || "2".equals(type)) && subm == null) {
			throw new GeneralException("参数submoduleid不能为空，请填写submoduleid");
		} 
		List list = new ArrayList();
		
		try {
			QueryboxBo bo = new QueryboxBo(this.frameconn, userView);
			ContentDAO dao = new ContentDAO(this.frameconn);
			if ("1".equals(type)) {
				
				list = bo.queryPlanList(subm);
				// 返回查询方案数据
				this.getFormHM().put("querySchemeData", list);
				
			} else if ("2".equals(type)){
				// 获取数据
				// 查询方案名称
				String plan_name = (String)this.getFormHM().get("name");
				
				//添加方案前 先查询数据库是否有记录  存在：前台提示名称已存在，不存在：保存方法   25449 wangb1 20170505
				List voList=new ArrayList();
				voList.add(subm);
				voList.add(plan_name);
				voList.add(userView.getUserName());
				this.setFrowset(dao.search("select submoduleid,plan_name from t_sys_table_query_plan where submoduleid=? and plan_name=? and username=? ",voList));
				if(this.getFrowset().next()){
					this.getFormHM().put("exist","true");
					return;
				}
				
				
				// 条件表达式式子
				String expression = (String) this.getFormHM().get("exp");
				expression=StringUtils.isNotEmpty(expression)?SafeCode.decode(expression):"";
				expression=expression.replace("＋", "+").replace("＊", "*");
				// 因子表达式
				String conditem = (String) this.getFormHM().get("cond");
				// 获取新的ID
				IDFactoryBean bean = new IDFactoryBean();
				String id = bean.getId("t_sys_table_query_plan.query_plan_id", "", this.frameconn);
				
				// 默认为不共享
				int is_share = 0;
				// 用户名
				String userName = this.getUserView().getUserName();
				
				// 组装数据
				RecordVo vo = new RecordVo("t_sys_table_query_plan");
				vo.setInt("query_plan_id", Integer.parseInt(id));
				vo.setString("submoduleid", subm);
				vo.setInt("condtype", 1);
				vo.setString("expression", expression);
				vo.setString("plan_name", plan_name);
				vo.setInt("is_share", is_share);
				vo.setString("username", userName);
				vo.setString("conditem", conditem);
				
				// 保存到数据库中
				dao.addValueObject(vo);
				// 保存成功后重新刷新一下页面，所以重新查询一次
				list = bo.queryPlanList(subm);
				// 返回查询方案数据
				this.getFormHM().put("querySchemeData", list);
				this.getFormHM().put("status", "1");
				this.getFormHM().put("id", id);
				
			} else if ("3".equals(type)){
				// 获取方案id
				int id = Integer.parseInt( this.getFormHM().get("id").toString());
				String sql = "delete from t_sys_table_query_plan where query_plan_id=?";
				list.clear();
				list.add(id);
				
				// 删除
				dao.delete(sql, list);
				this.getFormHM().put("status", "1");
				
			} else if ("4".equals(type)){
				// 获取方案id
				ArrayList objList = (ArrayList) this.getFormHM().get("objs");
				Map map = new HashMap();
				if (objList.size() > 0) {
					for (int i = 0; i < objList.size(); i++) {
						net.sf.ezmorph.bean.MorphDynaBean bean = (net.sf.ezmorph.bean.MorphDynaBean)objList.get(i);
						String value = AdminCode.getCodeName((String)bean.get("codeset"), (String)bean.get("value"));
						if("UM".equalsIgnoreCase((String)bean.get("codeset"))&&(value == null|| "".equals(value)))
							value = AdminCode.getCodeName("UN", (String)bean.get("value"));
						value = value == null ? "" : value;
						map.put((String)bean.get("cond"), (String)bean.get("value") + "`" + value);
					}
				}
				
				this.getFormHM().put("map", map);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			if ("1".equals(type)) {
				this.getFormHM().put("querySchemeData", new ArrayList());
			} else {
				this.getFormHM().put("status", "0");
			}
			throw new GeneralException(e.getMessage());
		}
	}
}
