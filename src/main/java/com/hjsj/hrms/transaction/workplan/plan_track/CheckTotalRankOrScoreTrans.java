package com.hjsj.hrms.transaction.workplan.plan_track;

import com.hjsj.hrms.businessobject.workplan.WorkPlanUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title:权重或分值之和</p>
 * <p>Description:检验权重或分值之和是否为100(funcId: 9028000740)</p>
 * <p>Company:HJSJ</p>
 * <p>Create time:2015-4-16:下午14:56:28</p>
 * @author 刘蒙
 * @version 1.0
 */
public class CheckTotalRankOrScoreTrans extends IBusiness {
	
	private static final long serialVersionUID = 3586556280688053951L;

	public void execute() throws GeneralException {
		try {
			String planId = (String) formHM.get("planId");
			List objectIds = (List) formHM.get("objectIds"); // 为空表示检查所有的考核对象
			
			if ((planId == null) || ("0".equals(planId)) || ("".equals(planId))) {return;}
			List up = new ArrayList(); // 超出总和的人
			List down = new ArrayList(); // 低于总和的人
			String status ="";
			int object_type=0;
			String [] arrPlanId= planId.split(",");
		    for (int k=0;k<arrPlanId.length;k++){
				planId = arrPlanId[k];
				if (",".equals(planId) || "0".equals(planId) || ("".equals(planId))) 
				{continue;}
				Integer iPlanId = Integer.valueOf(planId);

				// per_plan.object_type(1=团队, 2=人员)
				RecordVo plan = new RecordVo("per_plan");
				plan.setInt("plan_id", iPlanId.intValue());
				plan = new ContentDAO(frameconn).findByPrimaryKey(plan);
				object_type = plan.getInt("object_type");
				
				// 查询计划模板的类型(per_template.status): '0' = 分值, '1' = 权重
				status  = status(iPlanId);
				if (status == null || "".equals(status)) {return;}
				
				// 考核对象的权重或分值总和
				List ids = decrypt(objectIds);
				List total = total(iPlanId, ids, object_type);				

				for (int i = 0, len = total.size(); i < len; i++) {
					LazyDynaBean bean = (LazyDynaBean) total.get(i);
					Object object = bean.get("a0101");	//
					if ("0".equals(status)) {
						Double totalScore = ((Double) bean.get("totalScore")).doubleValue();
						if (totalScore > 100) {
							if(!up.contains(object)){
								up.add(object);
							}
							
						} else if (totalScore < 100 || totalScore == null) {
							if(!up.contains(object)){
								down.add(bean.get("a0101"));
							}
						}
					} else if ("1".equals(status)) {
						Double totalRank = ((Double) bean.get("totalRank")).doubleValue();
						if (totalRank > 1) {
							if(!up.contains(object)){
								up.add(bean.get("a0101"));
							}
						} else if (totalRank < 1 || totalRank == null) {
							if(!up.contains(object)){
								down.add(bean.get("a0101"));
							}
						}
					}
				}
				if("1".equals(status)){
					queryTotalRankisNull(ids,iPlanId, down, object_type);
				}
		    	
		    }

			formHM.put("up", up);
			formHM.put("down", down);
			formHM.put("status", status);
			formHM.put("object_type", String.valueOf(object_type));
		} catch (Exception ex) {
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	
	//将权重为null的加入权重不足1的集合中
	private List queryTotalRankisNull(List ids,Integer iPlanId, List down, int object_type){
		StringBuffer objectIds = new StringBuffer();
		for (int i = 0, len = ids.size(); i < len; i++) {
			String id= (String)ids.get(i);
			if(object_type == 2){
				id=id.substring(3);
			}
			if (i > 0) {objectIds.append(",");}
			objectIds.append("'"+id.toUpperCase()+"'");
		}
		ContentDAO dao = new ContentDAO(frameconn);
		StringBuffer sql = new StringBuffer();
		if(object_type == 2){
			sql.append("select A0101 from per_object where object_id not in (select  a0100 from P04 where plan_id =?) and plan_id =?");
			if (ids.size() > 0) {
				sql.append(" AND upper(object_id) IN");
				sql.append("(").append(objectIds).append(")");
			}
		}else{
			sql.append("select A0101 from per_object where object_id not in (select  b0110 from P04 where plan_id =?) and plan_id =?");
			if (ids.size() > 0) {
				sql.append(" AND upper(object_id) IN");
				sql.append("(").append(objectIds).append(")");
			}
		}
		try {
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[]{iPlanId, iPlanId}));
			while(frowset.next()){
				down.add(frowset.getString("a0101"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeDbObj(frecset);
		}
		return down;
	}
	
	// 标准分值(p0413)和权重(p0415)的总和
	private List total(Integer planId, List ids, int object_type) throws Exception {
		List result = new ArrayList();
		ContentDAO dao = new ContentDAO(frameconn);
		
		StringBuffer sql = new StringBuffer();
		if(object_type==2){
			sql.append("SELECT SUM(p0413) totalScore,SUM(p0415) totalRank,");
			sql.append("nbase").append(Sql_switcher.concat()).append("a0100 objectId");
			sql.append(",a0101 FROM p04 WHERE plan_id=?");
			
			if (ids.size() > 0) {
				sql.append(" AND upper(nbase").append(Sql_switcher.concat()).append("a0100) IN");
				sql.append("('").append(join(ids, "','")).append("')");
			}
			
			sql.append(" GROUP BY ");
			sql.append("nbase").append(Sql_switcher.concat()).append("a0100");
			sql.append(",a0101");
		}else{
			sql.append("SELECT SUM(p0413) totalScore,SUM(p0415) totalRank,");
			sql.append("b0110").append(" objectId");
			sql.append(",a0101 FROM p04 WHERE plan_id=?");
			
			if (ids.size() > 0) {
				sql.append(" AND upper(B0110)").append(" IN");
				sql.append("('").append(join(ids, "','")).append("')");
			}
			
			sql.append(" GROUP BY ");
			sql.append("b0110");
			sql.append(",a0101");
		}
		
		
		try {
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[] { planId }));
			while (frowset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				
				double totalScore = frowset.getDouble("totalScore");
				double totalRank = frowset.getDouble("totalRank");
				String objectId = frowset.getString("objectId");
				String a0101 = frowset.getString("a0101");
				
				bean.set("totalScore", Double.valueOf(totalScore));
				bean.set("totalRank", Double.valueOf(totalRank));
				bean.set("objectId", objectId);
				bean.set("a0101", a0101);
				
				result.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			PubFunc.closeDbObj(frecset);
		}
		
		return result;
	}
	
	// 模板类型
	private String status(Integer planId) throws Exception {
		ContentDAO dao = new ContentDAO(frameconn);
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT t.status FROM per_plan p");
		sql.append(" LEFT JOIN per_template t ON p.template_id = t.template_id");
		sql.append(" WHERE p.plan_id = ?");
		
		try {
			frowset = dao.search(sql.toString(), Arrays.asList(new Object[] { planId }));
			if (frowset.next()) {
				return frowset.getString("status");
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			PubFunc.closeDbObj(frecset);
		}
	}
	
	// 将集合内编码的id解码后返回
	private List decrypt(List src) {
		List dest = new ArrayList();
		
		for (int i = 0, len = src.size(); i < len; i++) {
			String id = (String) src.get(i);
			if (id != null && !"".equals(id.trim())) {
				dest.add(WorkPlanUtil.decryption(id));
			}
		}
		
		return dest;
	}
	
	// 将集合中的元素拼成字符串，元素之间用指定分隔符连接
	private String join(List list, String separator) {
		StringBuffer result = new StringBuffer();
		
		for (int i = 0, len = list.size(); i < len; i++) {
			if (i > 0) {result.append(separator);}
			String id= (String)list.get(i);
			result.append(id.toUpperCase());
		}
		
		return result.toString();
	}

}
