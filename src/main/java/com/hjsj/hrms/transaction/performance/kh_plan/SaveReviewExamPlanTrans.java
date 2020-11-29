package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * 保存审阅考核计划 支持批量审阅
 */
public class SaveReviewExamPlanTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		String agree_idea = (String) this.getFormHM().get("paramStr");
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String approve_result = (String) hm.get("approve_result");
		String plan_ids = (String) hm.get("plan_ids");
		plan_ids = plan_ids.replaceAll("／", "/");
		plan_ids = plan_ids.substring(0, plan_ids.length() - 1);
		String[] plans = plan_ids.split("/");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String nowDate = PubFunc.getStringDate("yyyy-MM-dd");
		
		
//		agree_idea字段最长为200 为了防止超过这个长度在此做截取处理
		if(agree_idea.getBytes().length>199)
		{
			byte[] temp=new byte[199];
			for(int i=0;i<199;i++)
				temp[i]=agree_idea.getBytes()[i];
			agree_idea=new String (temp);	
		}	
		
		
		try
		{
			String sql = "update per_plan set agree_user=?,agree_date=?,status=?,approve_result=?,agree_idea=? where plan_id=?";
			ArrayList list = new ArrayList();
			for (int i = 0; i < plans.length; i++)
			{
				
				String plan_id = plans[i];

//				RecordVo vo = new RecordVo("per_plan");
//				vo.setString("plan_id", plan_id);
//
//				vo = dao.findByPrimaryKey(vo);
//				String nowDate = PubFunc.getStringDate("yyyy-MM-dd");
//				vo.setString("agree_user", this.getUserView().getUserName());
//				vo.setDate("agree_date", nowDate);
				
				ArrayList list1 = new ArrayList();				
				list1.add(this.getUserView().getUserFullName());
				list1.add(java.sql.Date.valueOf(nowDate));			
				
				// 不同意,将报批状态改为起草状态
				if ("0".equals(approve_result))
				{
//					vo.setString("status", "0");
//					vo.setString("approve_result", "0");
//					vo.setString("agree_idea", agree_idea);
//					dao.updateValueObject(vo);
					
					list1.add(new Integer("0"));
					list1.add("0");
					list1.add(agree_idea);
					
					
				}
				// 同意,将报批状态改为已批状态
				else if ("1".equals(approve_result))
				{
//					vo.setString("status", "2");
//					vo.setString("approve_result", "1");
//					vo.setString("agree_idea", agree_idea);
//					dao.updateValueObject(vo);
					
					list1.add(new Integer("2"));
					list1.add("1");
					list1.add(agree_idea);
					
				} else
					return;
				list1.add(new Integer(plan_id));
				list.add(list1);
			}
			dao.batchUpdate(sql, list);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		// StringBuffer strSql = new StringBuffer();
		// // 不同意,将报批状态改为起草状态
		// if (approve_result.equals("0"))
		// strSql.append("update per_plan set status='0',approve_result='0',agree_idea='" + agree_idea + "' ");
		// // 同意,将报批状态改为已批状态
		// else if (approve_result.equals("1"))
		// strSql.append("update per_plan set status='2',approve_result='1',agree_idea='" + agree_idea + "' ");
		// else
		// return;
		//
		// strSql.append("where plan_id=" + plan_id);
		//
		// try
		// {
		// dao.update(strSql.toString());
		// } catch (SQLException e)
		// {
		// throw GeneralExceptionHandler.Handle(e);
		// }
	}

}
