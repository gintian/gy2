package com.hjsj.hrms.module.jobtitle.filterconditions.transaction;

import com.hjsj.hrms.module.jobtitle.committee.businessobject.CommitteeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选条件交易类
 * @author haosl
 * @date 20160722
 */
public class FilterconditionsTrans extends IBusiness {
	
	@Override
	public void execute() throws GeneralException {
		String type = (String) this.getFormHM().get("type");
		String subModuleId = (String) this.getFormHM().get("subModuleId");
		subModuleId = subModuleId.substring(0, 7) + PubFunc.decrypt(subModuleId.substring(7));
		ContentDAO dao = new ContentDAO(this.frameconn);
		try {
			if (("1".equals(type) || "2".equals(type)) && subModuleId == null) {
				throw new GeneralException("参数submoduleid不能为空，请填写submoduleid");
			} 
			List planList = new ArrayList();
			if ("1".equals(type)) {
				planList = this.queryPlanList(subModuleId);
				// 返回查询方案数据
				this.getFormHM().put("querySchemeData", planList);
				
			} else if ("2".equals(type)){
				// 获取数据
				// 查询方案名称	
				String plan_name = (String)this.getFormHM().get("name");
				// 条件表达式式子
				String expression = (String) this.getFormHM().get("exp");
				// 因子表达式
				String conditem = (String) this.getFormHM().get("cond");
				
				
				
				// 默认为不共享
				int is_share = 0;
				// 用户名
				String userName = this.getUserView().getUserName();
				
				// 组装数据
				RecordVo vo = new RecordVo("t_sys_table_query_plan");
				vo.setString("submoduleid", subModuleId);
				vo.setInt("condtype", 1);
				vo.setString("expression", expression);
				vo.setString("plan_name", plan_name);
				vo.setInt("is_share", is_share);
				vo.setString("username", userName);
				vo.setString("conditem", conditem);
				Integer query_plan_id = (Integer)this.getFormHM().get("planId");
				// 保存到数据库中
				if(query_plan_id!=null && hasRecordById(dao, query_plan_id)){
					String sql = "delete from t_sys_table_query_plan where query_plan_id=?";
					planList.clear();
					planList.add(query_plan_id);
					vo.setInt("query_plan_id", query_plan_id);
					dao.delete(sql, planList);
					dao.addValueObject(vo);
				}else{
					// 获取新的ID
					IDFactoryBean bean = new IDFactoryBean();
					String id = bean.getId("t_sys_table_query_plan.query_plan_id", "", this.frameconn);
					vo.setInt("query_plan_id", Integer.parseInt(id));
					dao.addValueObject(vo);
				}
				
				// 保存成功后重新刷新一下页面，所以重新查询一次
				planList = this.queryPlanList(subModuleId);
				// 返回查询方案数据
				this.getFormHM().put("querySchemeData", planList);
			} else if ("3".equals(type)){
				// 获取方案id
				String ids = String.valueOf(this.getFormHM().get("ids"));
				if(ids==null || "".equals(ids.trim())){
					return;
				}
				String[] idArr = ids.split("`");
				for(String id:idArr){
					String sql = "delete from t_sys_table_query_plan where query_plan_id=?";
					planList.clear();
					if(!"".equals(id.trim())){
						planList.add(Integer.parseInt(id));
						// 删除
						dao.delete(sql, planList);
					}
				}
				
			} else if ("4".equals(type)){
				// 获取方案id
				ArrayList objList = (ArrayList) this.getFormHM().get("objs");
				Map map = new HashMap();
				if (objList.size() > 0) {
					for (int i = 0; i < objList.size(); i++) {
						net.sf.ezmorph.bean.MorphDynaBean bean = (net.sf.ezmorph.bean.MorphDynaBean)objList.get(i);
						String codesetid =(String)bean.get("codeset");
						String codeValue =(String)bean.get("value");
						String value = AdminCode.getCodeName(codesetid, codeValue);
						value = value == "" ? codeValue : value;//value==null时，则是根据手动输入进行模糊查询的
						map.put((String)bean.get("cond"), (String)bean.get("value") + "`" + value);
					}
				}
				
				this.getFormHM().put("map", map);
				
			}else if("5".equals(type)){//获得查询指标的下拉框数据
				CommitteeBo committeeBo = new CommitteeBo(this.getFrameconn(), this.userView);// 工具类
				/** 获取列头 */
				ArrayList<ColumnsInfo> columnList = committeeBo.getColumnListRandom();
				List itemList = new ArrayList();
				for(ColumnsInfo info:columnList){
					if(info.getLoadtype()==3)//隐藏列不需加载
						continue;
					if("w0109".equalsIgnoreCase( info.getColumnId())){//可聘任标识不需加载
						continue;
					}
					Map dataMap = new HashMap();
					String itemId = info.getColumnId();
					String itemDesc = info.getColumnDesc();
					String itemType = info.getColumnType();
					String codeSetId = info.getCodesetId();
					dataMap.put("itemId", itemId);
					dataMap.put("itemDesc", itemDesc);
					dataMap.put("itemType", itemType);
					dataMap.put("codeSetId", codeSetId);
					itemList.add(dataMap);
				
				}
				if(subModuleId.indexOf("zc_com_") > -1) {//评委会 时，增加【固定成员列】
					Map dataMap = new HashMap();
					dataMap.put("itemId", "fixed_member");
					dataMap.put("itemDesc", "固定成员");
					dataMap.put("itemType", "A");
					dataMap.put("codeSetId", "45");
					itemList.add(dataMap);
				}
				this.getFormHM().put("data", itemList);
				this.getFormHM().put("columnList", columnList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if ("1".equals(type)) {
				this.getFormHM().put("querySchemeData", new ArrayList());
			}else{
				this.getFormHM().put("status","0");
			}
			GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 判断数据库中是否有记录，有则更新，没有则添加
	 * @param dao
	 * @param id
	 * @return
	 * @throws Exception
	 */
	private boolean hasRecordById(ContentDAO dao,int id) throws Exception{
		String sql = "select * from t_sys_table_query_plan where query_plan_id="+id;
		try {
			RowSet rs = dao.search(sql);
			if(rs.next()){
				return true;
			}else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 按照所属模块id查询 方案列表
	 * 
	 * @param subm
	 * @return List<Map> key包括id,name,share,exp,cond
	 */
	public List queryPlanList(String subm) throws GeneralException {
		List list = new ArrayList();
		StringBuffer sql = new StringBuffer();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			sql.append("select query_plan_id,plan_name,is_share,expression,conditem from t_sys_table_query_plan where submoduleid=? ");
			sql.append("order by Query_plan_id asc");

			// 准备查询参数
			list.add(subm);

			// 查询
			rs = dao.search(sql.toString(), list);

			// 清空list
			list.clear();

			// 组装数据
			while (rs.next()) {
				Map map = new HashMap();
				map.put("id", rs.getInt("query_plan_id"));
				map.put("name", rs.getString("plan_name"));
				map.put("share", rs.getString("is_share"));
				map.put("exp", rs.getString("expression"));
				map.put("cond", rs.getString("conditem"));

				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeneralException(e.getMessage());
		} finally {
			PubFunc.closeDbObj(rs);
		}

		return list;
	}
	
}
