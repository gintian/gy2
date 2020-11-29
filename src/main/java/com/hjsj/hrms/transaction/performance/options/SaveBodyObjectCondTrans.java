package com.hjsj.hrms.transaction.performance.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * <p>Title:ClearMainBodyTrans.java</p>
 * <p>Description:保存或清除考核主体筛选条件</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-02-15 11:11:11</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SaveBodyObjectCondTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		// TODO Auto-generated method stub
	
		HashMap hm = this.getFormHM();
		ContentDAO dao = new ContentDAO(this.frameconn);
		String cond = (String)hm.get("cond");
		cond = SafeCode.decode(cond);
		String cexpr = (String)hm.get("cexpr");
		String flag = (String)hm.get("flag");//0：保存到考核主体分类表per_mainbodyset 1：保存到per_plan_body 
		String body_id = (String)hm.get("body_id");
		String plan_id = (String)hm.get("plan_id");
		String editflag=(String)hm.get("editflag");
		String scope = (String)hm.get("scope");
		cexpr=cexpr!=null&&cexpr.trim().length()>0?cexpr:"";
		cond = PubFunc.keyWord_reback(cond);
		cexpr = PubFunc.keyWord_reback(cexpr);
		try 
		{
			if(editflag==null|| "save".equals(editflag))
			{
				if("0".equals(flag))
				{
					RecordVo vo = new RecordVo("per_mainbodyset");
					vo.setString("body_id", body_id);
					vo = dao.findByPrimaryKey(vo);
					vo.setString("cond", cond);
					vo.setString("cexpr", cexpr);
					dao.updateValueObject(vo);
					hm.put("info","ok");
				}else if("1".equals(flag))
				{
					RecordVo vo = new RecordVo("per_plan_body");
					vo.setString("plan_id", plan_id);
					vo.setString("body_id", body_id);
					vo = dao.findByPrimaryKey(vo);
					vo.setString("cond", cond);
					vo.setString("cexpr", cexpr);
					dao.updateValueObject(vo);
					hm.put("info","ok");
				}else if("2".equals(flag))
				{
					RecordVo vo = new RecordVo("per_plan_body");
					vo.setString("plan_id", plan_id);
					vo.setString("body_id", body_id);
					vo = dao.findByPrimaryKey(vo);
					vo.setString("scope", scope);
					dao.updateValueObject(vo);
					hm.put("info","ok2");
				}
			}else
			{
				String []t=body_id.split(",");
				if("1".equals(flag))
				{
					if(t!=null&&t.length>0)
					{
						for(int i=0;i<t.length;i++)
						{
							RecordVo vo = new RecordVo("per_plan_body");
							vo.setString("plan_id", plan_id);
							vo.setString("body_id", t[i].trim());
							vo = dao.findByPrimaryKey(vo);
							vo.setString("cond", "");
							vo.setString("cexpr", "");
							dao.updateValueObject(vo);
						/*	vo = new RecordVo("per_mainbodyset");
							vo.setString("body_id",  t[i].trim());
							vo = dao.findByPrimaryKey(vo);
							vo.setString("cond", "");
							vo.setString("cexpr", "");
							dao.updateValueObject(vo);
							*/
						}
					}
					hm.put("info","ok3");
				}
			}				
			
		} catch (SQLException e) {
			e.printStackTrace();
		}			
		
	}

}
