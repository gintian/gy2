package com.hjsj.hrms.transaction.performance.evaluation.uniformRate;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.achivement.Permission;
import com.hjsj.hrms.businessobject.performance.uniformRate.UniformRateBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>
 * Title:统一打分
 * </p>
 * <p>
 * Description:获取页面所要的参数
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 28, 2008:10:39:23 AM
 * </p>
 * 
 * @author wangyao
 * @version 1.0
 */
public class UniformRateTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		try
		{
			String planid = (String) this.getFormHM().get("planid");
			String templateid = (String) this.getFormHM().get("templateid");
			UniformRateBo pe = new UniformRateBo(this.getFrameconn(), planid, templateid);
			ArrayList pointList = pe.getPointList(templateid);
			this.getFormHM().put("pointList", pointList);

			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			String code = (String) hm.get("code");
			hm.remove("code");

			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			String whl = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
			if (code != null && !"-1".equals(code))
			{
				if (AdminCode.getCodeName("UN", code) != null && AdminCode.getCodeName("UN", code).length() > 0)
					whl += " and b0110 like '" + code + "%'";
				else if (AdminCode.getCodeName("UM", code) != null && AdminCode.getCodeName("UM", code).length() > 0)
					whl += " and e0122 like '" + code + "%'";

			}

			String order_str = (String) this.getFormHM().get("order_str");
			if (order_str == null || order_str.length() == 0)
				order_str = " order by a0000";

			int object_type=pb.getPlanVo(planid).getInt("object_type");
			LoadXml loadxml = new LoadXml(this.frameconn, planid);
			Hashtable planParameter = loadxml.getDegreeWhole();
			String keepDecimal = (String) planParameter.get("KeepDecimal");
			String EvalOutLimitStdScore = (String) planParameter.get("EvalOutLimitStdScore");//评分时得分不受标准分限制True, False
			
			Permission pointPrivBean=new Permission(this.frameconn,this.userView);
			String sql = "select * from per_result_" + planid + " where 1=1 " + whl + order_str;
			ContentDAO dao = new ContentDAO(this.frameconn);
			ArrayList dataList = new ArrayList();
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				LazyDynaBean bean = new LazyDynaBean();
				String b0110 =  this.frowset.getString("b0110") == null ? "" : this.frowset.getString("b0110");
				String e0122 = this.frowset.getString("e0122") == null ? "" : this.frowset.getString("e0122");
				String e01a1 =  this.frowset.getString("e01a1") == null ? "" : this.frowset.getString("e01a1");
				String a0101 = this.frowset.getString("a0101") == null ? "" : this.frowset.getString("a0101");
				String object_id = this.frowset.getString("object_id") == null ? "" : this.frowset.getString("object_id");
				
			
				
				for(int i=0;i<pointList.size();i++)
				{
					LazyDynaBean pointBean =(LazyDynaBean)pointList.get(i);
					String pointid = (String)pointBean.get("point_id");
					String maxScore = (String)pointBean.get("MaxScore");					
				    boolean right = true;
				    if(object_type!=2)
				    	right = pointPrivBean.getPrivPoint("", object_id, pointid);	
				    else
				    	right = pointPrivBean.getPrivPoint(b0110, e0122, pointid);
				    
					bean.set("C_"+pointid, PubFunc.round(new Float(this.frowset.getFloat("C_"+pointid)).toString(), Integer.parseInt(keepDecimal)));
				    bean.set(pointid+"_priv", right==true?"1":"0");		
				    bean.set("plan_id", planid);	
				    bean.set("MaxScore", maxScore);	
				}	
				 b0110 = AdminCode.getCodeName("UN", b0110);
				 e0122 = AdminCode.getCodeName("UM", e0122);
				 e01a1 = AdminCode.getCodeName("@K", e01a1);
				bean.set("b0110", b0110);
				bean.set("e0122", e0122);
				bean.set("e01a1", e01a1);
				bean.set("a0101", a0101);
				bean.set("object_id", object_id);
				bean.set("EvalOutLimitStdScore", EvalOutLimitStdScore);	
				dataList.add(bean);
			}
			this.getFormHM().put("rateList", dataList);
			
			
			
			
//			sql = pe.getSql(planid, pointList);
//			sql = sql + whl + order_str;
//			this.getFormHM().put("sql", sql);
//
//			ArrayList rateList = pe.getRateList(pointList);
//			this.getFormHM().put("rateList", rateList);

//			String per_result = pe.getPer_result(planid);
//			this.getFormHM().put("per_result", per_result);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}