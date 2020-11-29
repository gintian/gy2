package com.hjsj.hrms.transaction.performance.implement.kh_object.set_dyna_target_rank;

import com.hjsj.hrms.interfaces.performance.OrgPersonByXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *<p>Title:SearchDynaTargetRankTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:Jul 7, 2008:9:14:03 AM</p> 
 *@author JinChunhai
 *@version 1.0
 */

public class SearchDynaTargetRankTrans extends IBusiness 
{

	private ArrayList topOrgs = new ArrayList();
	private HashMap parentidMap = new HashMap();
	private HashMap planPointRankMap = new HashMap();
	
	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String planid = (String)hm.get("planid");
		String codeid = (String)hm.get("codeid");
		if(planid!=null&&codeid!=null)
		{
			String codesetid = codeid.substring(0,2);
			String codeitemid = codeid.substring(2);
			String dyna_obj_type = "";
			if("UN".equalsIgnoreCase(codesetid))
				dyna_obj_type ="1";
			else if("UM".equalsIgnoreCase(codesetid))
				dyna_obj_type ="2";
			else if("@K".equalsIgnoreCase(codesetid))
				dyna_obj_type ="3";
			else if("lb".equalsIgnoreCase(codesetid))
				dyna_obj_type ="5";
			else 
			{
				dyna_obj_type = "4";
				codeitemid = codeid.substring(1);
			}
			// 保存左边显示出来的机构树的顶层节点
			topOrgs = this.getTopOrgs(planid);
			this.parentidMap = this.getParentIds();
			planPointRankMap = getPlanPointRankMap(planid);
			
			ArrayList rolelist = new ArrayList();
			StringBuffer sql = new StringBuffer();
			ArrayList pointIdList = new ArrayList();
			HashMap pointRankMap_default = new HashMap();
			ArrayList idlist = new ArrayList();
			ArrayList uplist = new ArrayList();
			HashMap pointNameMap = new HashMap();
			String b0110="",e0122="",e01a1="",object_id="";
			
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try 
			{
				if("1".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '"+planid+"' and b0110 = '"+codeitemid+"'");
				else if("2".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '"+planid+"' and e0122 = '"+codeitemid+"'");
				else if("3".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '"+planid+"' and e01a1 = '"+codeitemid+"'");
				else if("4".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '"+planid+"' and object_id = '"+codeitemid+"'");
				else if("5".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '"+planid+"' and body_id = '"+codeitemid+"'");
				this.frowset = dao.search(sql.toString());
				if(this.frowset.next())
				{
					b0110 = this.frowset.getString("b0110")==null?"":this.frowset.getString("b0110");
					e0122 = this.frowset.getString("e0122")==null?"":this.frowset.getString("e0122");
					e01a1 = this.frowset.getString("e01a1")==null?"":this.frowset.getString("e01a1");
					object_id = this.frowset.getString("object_id");
				}
				
				/**得到所有指标权重*/
				sql.setLength(0);
				sql.append("select pp.pointname,ptp.point_id,ptp.rank from per_template_point ptp,per_point pp where ptp.item_id in"
						+" (select item_id from per_template_item where UPPER(template_id)=(select template_id from per_plan where plan_id="+planid+")) and ptp.point_id=pp.point_id");
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next())
				{
					pointIdList.add(this.frowset.getString("point_id"));
					pointRankMap_default.put(this.frowset.getString("point_id"),new Float(this.frowset.getFloat("rank")));
					pointNameMap.put(this.frowset.getString("point_id"),this.frowset.getString("pointname"));

				}
				
				/**查询点击树本身是否设置主体权重*/
				for (int i = 0; i < pointIdList.size(); i++)
				{
					String point_id = (String) pointIdList.get(i);
					float defaultPointRank = ((Float) pointRankMap_default.get(point_id)).floatValue();
					float pointRank = defaultPointRank;
					if ("4".equalsIgnoreCase(dyna_obj_type))// 人员节点
					{
						Float rank1 = (Float) this.planPointRankMap.get(codeitemid + ":" + point_id);
						if (rank1 != null)//设置了动态主体权重
							pointRank = rank1.floatValue();
						else//人员节点没有个性权重找职位的个性权重
						{
							rank1 = (Float) this.planPointRankMap.get(e01a1 + ":" + point_id);
							if (rank1 != null)
								pointRank = rank1.floatValue();
							else//职位节点没有设置个性权重 找部门节点的个性权重
							{
								rank1 = (Float) this.planPointRankMap.get(e0122 + ":" + point_id);
								if (rank1 != null)
									pointRank = rank1.floatValue();
								else//部门节点没有设置个性权重 找单位节点的个性权重
								{
									rank1 = (Float) this.planPointRankMap.get(b0110 + ":" + point_id);
									if (rank1 != null)
										pointRank = rank1.floatValue();
									else//单位节点没有设置个性权重 递归找单位上级节点的个性权重 直到找到为止
									{
										pointRank = this.getPointRank("1", planid, (String) this.parentidMap.get(b0110), point_id, defaultPointRank);
									}
								}
							}							
						}
					} else
						pointRank = this.getPointRank(dyna_obj_type, planid, codeitemid, point_id, defaultPointRank);
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("point_id",point_id);
					bean.set("perObject",(String)pointNameMap.get(point_id));
					bean.set("dyna_obj_type",dyna_obj_type);
					bean.set("dyna_obj",codeitemid);
					bean.set("rank",String.valueOf(pointRank));
					rolelist.add(bean);				
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			Collections.sort(rolelist,new Comparator(){
				public int compare(Object o1,Object o2){
					LazyDynaBean bean1 = (LazyDynaBean)o1;
					LazyDynaBean bean2 = (LazyDynaBean)o2;
					return bean1.get("point_id").toString().compareTo(bean2.get("point_id").toString());
				}
			});
			this.getFormHM().put("rolelist",rolelist);
		}
	}
	
	private String getSql(ArrayList list,String planid,String dyna_obj)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_dyna_rank where plan_id ='"+planid+"' and dyna_obj = '"+dyna_obj+"' ");
		for(int i=0;i<list.size();i++)
			sql.append(" and point_id <> '"+list.get(i)+"'");
		return sql.toString();
		
	}
	
	public ArrayList getTopOrgs(String planid) throws GeneralException
	{
		ArrayList list = new ArrayList();
		OrgPersonByXml myOrg = new OrgPersonByXml("", "", "", planid, this.userView);
		String sql = "select codeitemid,codesetid from organization where 1=1 " + myOrg.getRootOrgNodeStr(myOrg.userview, myOrg.plan_b0110);
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql);
			while (this.frowset.next())
				list.add(this.frowset.getString(1));
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return list;
	}
	
	public HashMap getParentIds()
	{

		HashMap parentId = new HashMap();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			String sql = "select codeitemid,parentid from organization where parentid!=codeitemid ";
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
				parentId.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return parentId;
	}
	
	private HashMap getPlanPointRankMap(String planid) throws GeneralException
	{
		HashMap rank = new HashMap();
		String sql = "select * from per_dyna_rank where plan_id =" + planid ;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
			{
				String point_id = this.frowset.getString("point_id");
				String dyna_obj = this.frowset.getString("dyna_obj");
				rank.put(dyna_obj + ":" + point_id, new Float(this.frowset.getFloat("rank")));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rank;

	}
	/** 取得上层机构的个性指标权重 */
	private float getPointRank(String dyna_obj_type, String planid, String dyna_obj, String point_id, float defaultRank) throws GeneralException
	{
		float rank = defaultRank;
		for (int i = 0; i < this.topOrgs.size(); i++)
		{
			String topOrg =(String) this.topOrgs.get(i);
			if (dyna_obj != null && !"".equals(dyna_obj))
			{
				Float rank1 = (Float) this.planPointRankMap.get(dyna_obj + ":" + point_id);
				if (rank1 != null)
					rank = rank1.floatValue();
				if (dyna_obj.equals(topOrg))
					return rank;

				while (rank == defaultRank)
				{
					dyna_obj = (String) this.parentidMap.get(dyna_obj);
					if (dyna_obj != null && !"".equals(dyna_obj))
						rank = this.getPointRank(dyna_obj_type, planid, dyna_obj, point_id, defaultRank);
					else
						return rank;
				}

			}
		}
	
		return rank;
	}
	
}
