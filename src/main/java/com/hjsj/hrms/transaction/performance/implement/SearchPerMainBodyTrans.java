package com.hjsj.hrms.transaction.performance.implement;

import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchPerMainBodyTrans extends IBusiness
{

	private String getFirstMainBody(ArrayList dblist)
	{
		CommonData vo = (CommonData) dblist.get(0);
		return vo.getDataValue();
	}



	public void execute() throws GeneralException
	{

		ArrayList mainBodySortList = new ArrayList(); // 与考核计划相关联的主体类别集合
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String objectID = (String) hm.get("objectID"); // 得到考核对象id;
		StringBuffer sql_str = new StringBuffer(" from per_mainbody,per_mainbodyset where per_mainbody.body_id=per_mainbodyset.body_id "); // 查询考核对象的主体条件语句

		ContentDAO dao = new ContentDAO(this.getFrameconn());

		// 得到选中的考核计划id
		String dbpre = (String) this.getFormHM().get("dbpre");
		sql_str.append(" and per_mainbody.plan_id=" + dbpre);

		/* 得到所选的主体类id */
		String mainBodyID = (String) this.getFormHM().get("mainBodyID");
		/** b.status=1 得到有效的考核主体类型 */
		StringBuffer sql = new StringBuffer("select b.body_id,b.name from per_plan_body a ,per_mainbodyset b where a.body_id=b.body_id ");
		sql.append(" and a.plan_id=" + dbpre + " order by b.body_id");

		try
		{
			this.frowset = dao.search(sql.toString());
			CommonData commonData = new CommonData("-all", "全部");
			mainBodySortList.add(commonData);
			while (this.frowset.next())
			{
				String bodySortID = this.frowset.getString("body_id");
				String bodySortName = this.frowset.getString("name");
				CommonData aCommonData = new CommonData(bodySortID, bodySortName);
				mainBodySortList.add(aCommonData);
			}

			if (mainBodyID == null || "".equals(mainBodyID))
			{
				mainBodyID = getFirstMainBody(mainBodySortList);
				this.getFormHM().put("mainBodyID", mainBodyID);
			}
			if("-all".equals(mainBodyID)){
				sql_str.append(" and per_mainbody.object_id='" + objectID + "'");
			}else{
				sql_str.append(" and per_mainbody.body_id=" + mainBodyID + " and per_mainbody.object_id='" + objectID + "'");
			}
			
			
			ExamPlanBo bo = new ExamPlanBo(this.frameconn);
			String khobjname = bo.getKhObjName(objectID, dbpre);
			this.getFormHM().put("khobjname",khobjname);
		}
		catch (Exception e)
		{
			e.printStackTrace();

		} finally
		{
			this.getFormHM().put("objectID", objectID);
			this.getFormHM().put("where_str2", sql_str.toString());
			this.getFormHM().put("mainBodySortList", mainBodySortList);
		}

	}

}
