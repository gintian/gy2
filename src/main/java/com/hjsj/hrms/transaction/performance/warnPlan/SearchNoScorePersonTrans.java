package com.hjsj.hrms.transaction.performance.warnPlan;

import com.hjsj.hrms.businessobject.performance.markStatus.MarkStatusBo;
import com.hjsj.hrms.transaction.performance.LoadXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaMap;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * <p>Title:SearchNoScorePersonTrans.java</p>
 * <p>Description:展示预警提醒未打分的人员</p>
 * <p>Company:hjsj</p>
 * <p>create time:2012-05-24 09:41:14</p>
 * @author JinChunhai
 * @version 6.0
 */

public class SearchNoScorePersonTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
				
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");		
		String plan_id = (String)hm.get("plan_id"); // 预警计划编号
		ArrayList objStatusList = new ArrayList();
		
		MarkStatusBo bo = new MarkStatusBo(this.getFrameconn(),this.userView);
		RecordVo planVo = bo.getPlanVo(plan_id);
		ArrayList scoreStatusList = bo.getMarkStatusList(plan_id,this.getUserView(),false,"$1","0","","0");
		
		LoadXml loadxml = new LoadXml(this.frameconn, plan_id);
		Hashtable params = loadxml.getDegreeWhole();
		String gradeByBodySeq = (String)params.get("GradeByBodySeq"); // 按考核主体顺序号控制评分流程(True, False默认为False)				 
		String noApproveTargetCanScore = (String)params.get("NoApproveTargetCanScore"); // 目标卡未审批也允许打分 True, False, 默认为 False
		HashMap scoreManagerMap = new HashMap();
		if(gradeByBodySeq!=null && gradeByBodySeq.trim().length()>0 && "True".equalsIgnoreCase(gradeByBodySeq) && planVo.getInt("method")==2)
		{
			scoreManagerMap = getScoreManagerMap(plan_id,noApproveTargetCanScore);
		}
		
		LazyDynaMap dynaMap = null;
		for(int i=0;i<scoreStatusList.size();i++)
		{
			dynaMap=(LazyDynaMap)scoreStatusList.get(i);
			String noMark = (String)dynaMap.get("noMark"); 
			String marking = (String)dynaMap.get("marking");
			String notMark = (String)dynaMap.get("notMark");
			String marked = (String)dynaMap.get("marked");
			
			if(gradeByBodySeq!=null && gradeByBodySeq.trim().length()>0 && "True".equalsIgnoreCase(gradeByBodySeq) && planVo.getInt("method")==2)
			{
				if(scoreManagerMap.get((String)dynaMap.get("object_id"))==null)
					continue;
			}
			
			boolean haveMarked = false;
			String objScoreStatus = "";
			if(marked!=null && marked.trim().length()>0)
			{
				marked = marked.substring(0, marked.length() - 1);
				String[] matters = marked.split("、");
				for (int k = 0; k < matters.length; k++)
				{
					objScoreStatus += matters[k]+"（已评分）、";
				}
				haveMarked = true;
			}
			if(marking!=null && marking.trim().length()>0)
			{
				marking = marking.substring(0, marking.length() - 1);
				String[] matters = marking.split("、");
				for (int k = 0; k < matters.length; k++)
				{
					objScoreStatus += matters[k]+"（正评分）、";
				}
				haveMarked = false;
			}
			if(noMark!=null && noMark.trim().length()>0)
			{
				noMark = noMark.substring(0, noMark.length() - 1);				
				String[] matters = noMark.split("、");
				for (int k = 0; k < matters.length; k++)
				{
					if(matters[k].indexOf(">")!=-1)
					{
						matters[k] = matters[k].substring(matters[k].indexOf(">")+1,matters[k].length());
						matters[k] = matters[k].substring(0,matters[k].indexOf("<"));
					}					
					objScoreStatus += matters[k]+"（未评分）、";
				}
				haveMarked = false;
			}						
			if(notMark!=null && notMark.trim().length()>0)
			{
				notMark = notMark.substring(0, notMark.length() - 1);
				String[] matters = notMark.split("、");
				for (int k = 0; k < matters.length; k++)
				{
					if(matters[k].indexOf(">")!=-1)
					{
						matters[k] = matters[k].substring(matters[k].indexOf(">")+1,matters[k].length());
						matters[k] = matters[k].substring(0,matters[k].indexOf("<"));
					}
					objScoreStatus += matters[k]+"（不评分）、";
				}
				haveMarked = false;
			}			
			if(haveMarked) // 过滤掉已评完分的考核主体
				continue;
			
			LazyDynaMap dynaBean = new LazyDynaMap();			
			String userName = (String)dynaMap.get("userName");
			if(userName.indexOf(">")!=-1)
			{
				userName = userName.substring(userName.indexOf(">")+1,userName.length());
				userName = userName.substring(0,userName.indexOf("<"));
			}			
			dynaBean.set("userName",userName);
			dynaBean.set("e0122",(String)dynaMap.get("e0122"));
			dynaBean.set("object_id",(String)dynaMap.get("object_id"));
			dynaBean.set("objScoreStatus",objScoreStatus);			
			objStatusList.add(dynaBean);
		}
		
		this.getFormHM().put("setlist",objStatusList);
		this.getFormHM().put("plan_id",plan_id);
		this.getFormHM().put("plan_name",planVo.getString("name"));
	}

	// 查找登录用户范围内的考核对象轮到哪个考核主体打分了
	public HashMap getScoreManagerMap(String plan_id,String noApproveTargetCanScore)
	{
		HashMap map = new HashMap();
		RowSet rowSet = null;
		try
		{
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();	
						
			sql.append("select mainbody_id,a0101 from per_mainbody b where b.plan_id="+plan_id+" and exists (select null from ");
			sql.append("    (  select pm.object_id,min(seq) seq from per_mainbody pm ");
			sql.append("       where plan_id="+plan_id+" "+getUserViewPrivWhere(this.userView,"pm")+" ");
			if(noApproveTargetCanScore!=null && noApproveTargetCanScore.trim().length()>0 && "False".equalsIgnoreCase(noApproveTargetCanScore))
				sql.append("   and pm.object_id in ( select object_id from per_object where plan_id="+plan_id+" and sp_flag='03') ");
			sql.append("       and "+ Sql_switcher.isnull("pm.seq", "0") +" is not null " );
			sql.append("       and pm.status<>2 group by pm.object_id ");
			sql.append("    ) a ");
			sql.append(" where a.object_id=b.object_id and a.seq=b.seq ) and b.status<>2  ");			
			
			rowSet = dao.search(sql.toString());
			while(rowSet.next())
			{
				map.put(rowSet.getString("mainbody_id"),rowSet.getString("a0101"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 根据用户权限获得计划列表 先看操作单位 再看管理范围
	 * 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
	 */
	public String getUserViewPrivWhere(UserView userView,String base)
	{
		String str = "";
//		if (!userView.isSuper_admin())
		{
			StringBuffer buf = new StringBuffer();
			String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
			if (operOrg!=null && operOrg.length() > 3)
			{
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrg.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if ("UN".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or "+base+".b0110 like '" + temp[i].substring(2) + "%'");
					else if ("UM".equalsIgnoreCase(temp[i].substring(0, 2)))
						tempSql.append(" or "+base+".e0122 like '" + temp[i].substring(2) + "%'");
				}
				buf.append(" and ( " + tempSql.substring(3) + " ) ");
			} 
			else if((!userView.isAdmin()) && (!"UN`".equalsIgnoreCase(operOrg))) // 按照管理范围走
			{
				String codeid = userView.getManagePrivCode();
				String codevalue = userView.getManagePrivCodeValue();
				String a_code = codeid + codevalue;

				if (a_code.trim().length() > 0)// 说明授权了
				{
					if ("UN".equalsIgnoreCase(a_code))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串
						buf.append(" and 1=1 ");
					else if("UN".equalsIgnoreCase(codeid))
						buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
					else if("UM".equalsIgnoreCase(codeid))
						buf.append(" and "+base+".e0122 like '" + codevalue + "%'");
					else if("@K".equalsIgnoreCase(codeid))
						buf.append(" and "+base+".e01a1 like '" + codevalue + "%'");
					else
						buf.append(" and "+base+".b0110 like '" + codevalue + "%'");
						
				} else
					buf.append(" and 1=2 ");
			}
			str = buf.toString();
		}

		return str;		
	}
	
}
