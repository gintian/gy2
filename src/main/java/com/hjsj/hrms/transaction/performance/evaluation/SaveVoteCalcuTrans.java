package com.hjsj.hrms.transaction.performance.evaluation;

import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.*;

/**
 * <p>Title:SaveVoteCalcuTrans.java</p>
 * <p>Description:考核评估/票数计算</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-05-08 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0 
 */

public class SaveVoteCalcuTrans extends IBusiness
{
	
    public void execute() throws GeneralException
    {
		String planid = (String)this.getFormHM().get("planid");
		String voteScoreDecimal = (String)this.getFormHM().get("voteScoreDecimal");
		String voteDecimal = (String)this.getFormHM().get("voteDecimal");
		ArrayList list = (ArrayList)this.getFormHM().get("planbodylist");
		HashMap map = new HashMap();
		for(int i=0;i<list.size();i++)
		{
		    LazyDynaBean abean =(LazyDynaBean)list.get(i);
		    String body_id = (String)abean.get("body_id");
		    String voterank = (String)abean.get("voterank");
		    voterank = voterank.length()==0?"0":PubFunc.round(voterank, Integer.parseInt(voteDecimal));
		    map.put(body_id,voterank);
		}
		//更新PER_BODYVOTE_xxx表的总分字段
		ArrayList updateSql = new ArrayList();
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		RecordVo planVo = getPlanVo(planid);
		String per_comTable = "per_grade_template"; // 绩效标准标度
		if(String.valueOf(planVo.getInt("busitype"))!=null && String.valueOf(planVo.getInt("busitype")).trim().length()>0 && planVo.getInt("busitype")==1)
			per_comTable = "per_grade_competence"; // 能力素质标准标度
		String strSql = "select grade_template_id,gradedesc from "+per_comTable+"";
		try
		{
		    this.frowset = dao.search(strSql);
		    while (this.frowset.next())
		    {
				StringBuffer buf = new StringBuffer();
				buf.append("update PER_BODYVOTE_" + planid + " set S_");
		
				LazyDynaBean abean = new LazyDynaBean();
				String grade_template_id = this.frowset.getString("grade_template_id");
				buf.append(grade_template_id + "=0");
		
				Set set = map.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();)
				{
				    String body_id = (String) iter.next();
				    if("-1".equals(body_id))
				    	body_id="X1";
				    String col = "B_B" + body_id + "_G" + grade_template_id;
				    buf.append("+" + Sql_switcher.isnull(col, "0") + "*" + (String) map.get(body_id));
				}
				updateSql.add(buf.toString());
		    }
	
		    for (int j = 0; j < updateSql.size(); j++)
		    {
				String str = (String) updateSql.get(j);
				dao.update(str);
		    }
		    
		    //更新per_plan_body表字段voterank
		    String sql = "update per_plan_body set voterank=? where plan_id="+planid+" and body_id=?";
		    Set set = map.keySet();
		    ArrayList list1 = new ArrayList();
		    for (Iterator iter = set.iterator(); iter.hasNext();)
		    {
				ArrayList list2 = new ArrayList();
				String body_id = (String) iter.next();
				String voterank = (String) map.get(body_id);	
				list2.add(voterank);
				list2.add(body_id);
				list1.add(list2);
		    }
		    dao.batchUpdate(sql, list1);	
		    
		} catch (Exception e)
		{
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
		}
		
		//更新参数 总分精度  权重精度
		
		LoadXml parameter_content = new LoadXml(this.getFrameconn(), planid);
		Hashtable params = parameter_content.getDegreeWhole();
	
		HashMap rootAttributes = new HashMap();		
		rootAttributes.put("VoteScoreDecimal", voteScoreDecimal);
		rootAttributes.put("VoteDecimal", voteDecimal);
		parameter_content.saveAttributes(this.getFrameconn(), rootAttributes,new HashMap(),new HashMap(),new ArrayList(),new ArrayList(),new HashMap(),planid);	
		
    }
    
    /**
	 * 获得计划信息
	 * @param planid
	 * @return
	 */
	public RecordVo getPlanVo(String planid)
	{
		RecordVo vo=new RecordVo("per_plan");
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			vo.setInt("plan_id",Integer.parseInt(planid));
			vo=dao.findByPrimaryKey(vo);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return vo;
	}
    
}
