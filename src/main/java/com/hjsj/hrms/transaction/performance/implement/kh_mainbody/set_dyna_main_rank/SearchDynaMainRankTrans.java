package com.hjsj.hrms.transaction.performance.implement.kh_mainbody.set_dyna_main_rank;

import com.hjsj.hrms.interfaces.performance.OrgPersonByXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Title:SearchDynaMainRankTrans.java</p>
 * <p>Description: 设置动态主体权重</p>
 * <p>Company:hjsj</p>
 * <p>Create time:Jun 5, 2008:10:45:56 AM</p>
 * @author JinChunhai
 * @version 1.0
 */

public class SearchDynaMainRankTrans extends IBusiness
{

	private HashMap parentidMap = new HashMap();

	private HashMap mainbodyRankMap = new HashMap();

	ArrayList topOrgs = new ArrayList();

	public void execute() throws GeneralException
	{
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String planid = (String) hm.get("planid");
		String codeid = (String) hm.get("codeid");

		// 保存左边显示出来的机构树的顶层节点
		topOrgs = this.getTopOrgs(planid);

		ArrayList rolelist = new ArrayList();
		if (planid != null && codeid != null && !"root".equalsIgnoreCase(codeid))
		{
			String codesetid = codeid.substring(0, 2);
			String codeitemid = codeid.substring(2);
			String dyna_obj_type = "";
			if ("UN".equalsIgnoreCase(codesetid))
				dyna_obj_type = "1";
			else if ("UM".equalsIgnoreCase(codesetid))
				dyna_obj_type = "2";
			else if ("@K".equalsIgnoreCase(codesetid))
				dyna_obj_type = "3";
			else if("lb".equalsIgnoreCase(codesetid)){
				dyna_obj_type = "5";
			}
			else
			{
				dyna_obj_type = "4";
				codeitemid = codeid.substring(1);
			}

			StringBuffer sql = new StringBuffer();
			ArrayList bodyidlist = new ArrayList();
			HashMap map = new HashMap();// 存放rank,key为body_id
			HashMap defaultRank = new HashMap();// 存放默认rank,key为body_id
			HashMap mainbodysetlist = new HashMap();// 存放主体权重名称，key为body_id

			String b0110 = "", e0122 = "", e01a1 = "";
	
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try
			{

				if ("1".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '" + planid + "' and b0110 = '" + codeitemid + "'");
				else if ("2".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '" + planid + "' and e0122 = '" + codeitemid + "'");
				else if ("3".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '" + planid + "' and e01a1 = '" + codeitemid + "'");
				else if ("4".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '" + planid + "' and object_id = '" + codeitemid + "'");
				else if ("5".equals(dyna_obj_type))
					sql.append("select * from per_object where plan_id = '" + planid + "' and Body_id = '" + codeitemid + "'");
				this.frowset = dao.search(sql.toString());
				if (this.frowset.next())
				{
					b0110 = this.frowset.getString("b0110");
					e0122 = this.frowset.getString("e0122");
					e01a1 = this.frowset.getString("e01a1");
				}

				sql.setLength(0);
				sql.append("select p.* from per_plan_body p,per_mainbodyset m where p.body_id=m.body_id and p.plan_id = " + planid);// 得到默认权重
				sql.append(" order by m.seq");
				/** 得到所有主体权重 */
				this.frowset = dao.search(sql.toString());
				sql.setLength(0);

				Map bodyOptMap = new HashMap(); // 主体打分确认标识 add by 刘蒙
				
				sql.append("select * from per_mainbodyset where body_id in (");
				while (this.frowset.next())
				{
					bodyidlist.add(this.frowset.getString("body_id"));
					map.put(this.frowset.getString("body_id"), String.valueOf(this.frowset.getFloat("rank")));
					defaultRank.put(this.frowset.getString("body_id"), new Float(this.frowset.getFloat("rank")));
					sql.append("'" + this.frowset.getString("body_id") + "',");
					
					bodyOptMap.put(frowset.getString("body_id"), frowset.getInt("opt") + "");
				}
				/** 保存主体类别名称 */
				sql.setLength(sql.length() - 1);
				sql.append(") order by seq");
				this.frowset = dao.search(sql.toString());
				while (this.frowset.next())
				{
					mainbodysetlist.put(this.frowset.getString("body_id"), this.frowset.getString("name"));
				}

				sql.setLength(0);

				String perObject = "";
				CodeItem item = null;
				if ("5".equalsIgnoreCase(dyna_obj_type))
				{	String ssl ="select * from per_mainbodyset where body_id='"+codeitemid+"'";
					try(
						ResultSet rs=dao.search(ssl);
					){
						while (rs.next()) {
							//item = rs.getString("name")
							perObject = rs.getString("name");
						}
					}
				} else if("4".equalsIgnoreCase(dyna_obj_type))
				{
					sql.append(" select a0100,a0101 from UsrA01 where a0100='" + codeitemid + "'");
					this.frowset = dao.search(sql.toString());
					if (this.frowset.next())
					{
						perObject = this.frowset.getString("a0101");
					}
				}else{
					item = AdminCode.getCode(codesetid, codeitemid);
					perObject = item.getCodename();
				}

				sql.setLength(0);
				this.parentidMap = this.getParentIds();
				/**保存设置的动态主体权重*/
				this.mainbodyRankMap = this.getMainBodyRankMap(planid);
				rolelist = new ArrayList();
				/** 查询点击节点是否设置主体权重 */
				for (int i = 0; i < bodyidlist.size(); i++)
				{
					String body_id = (String) bodyidlist.get(i);
					float defaultBodyRank = ((Float) defaultRank.get(body_id)).floatValue();
					float bodyRank = defaultBodyRank;
					if ("4".equalsIgnoreCase(dyna_obj_type))// 人员节点
					{
						Float rank1 = (Float) this.mainbodyRankMap.get(codeitemid + ":" + body_id);
						if (rank1 != null && String.valueOf(rank1).trim().length()>0)//设置了动态主体权重
							bodyRank = rank1.floatValue();
						else//人员节点没有个性权重找职位的个性权重
						{
							rank1 = (Float) this.mainbodyRankMap.get(e01a1 + ":" + body_id);
							if (rank1 != null && String.valueOf(rank1).trim().length()>0)
								bodyRank = rank1.floatValue();
							else//职位节点没有设置个性权重 找部门节点的个性权重
							{
								rank1 = (Float) this.mainbodyRankMap.get(e0122 + ":" + body_id);
								if (rank1 != null && String.valueOf(rank1).trim().length()>0)
									bodyRank = rank1.floatValue();
								else//部门节点没有设置个性权重 找单位节点的个性权重
								{
									rank1 = (Float) this.mainbodyRankMap.get(b0110 + ":" + body_id);
									if (rank1 != null && String.valueOf(rank1).trim().length()>0)
										bodyRank = rank1.floatValue();
									else//单位节点没有设置个性权重 递归找单位上级节点的个性权重 直到找到为止
									{
										bodyRank = this.getBodyRank("1", planid, (String) this.parentidMap.get(b0110), body_id, defaultBodyRank);
									}
								}
							}							
						}
						
//						if (bodyRank == defaultBodyRank)
//						{
//							rank1 = (Float) this.mainbodyRankMap.get(e01a1 + ":" + body_id);
//							if (rank1 != null)
//								bodyRank = rank1.floatValue();
//						}
//						if (bodyRank == defaultBodyRank)
//						{
//							rank1 = (Float) this.mainbodyRankMap.get(e0122 + ":" + body_id);
//							if (rank1 != null)
//								bodyRank = rank1.floatValue();
//						}
//						if (bodyRank == defaultBodyRank)
//						{
//							rank1 = (Float) this.mainbodyRankMap.get(b0110 + ":" + body_id);
//							if (rank1 != null)
//								bodyRank = rank1.floatValue();
//						}
//						if (bodyRank == defaultBodyRank)// 迭代得到非默认权重的某一层上级动态权重
//							bodyRank = this.getBodyRank("1", planid, (String) this.parentidMap.get(b0110), body_id, defaultBodyRank);
					} else
						bodyRank = this.getBodyRank(dyna_obj_type, planid, codeitemid, body_id, defaultBodyRank);

					LazyDynaBean bean = new LazyDynaBean();
					bean.set("body_id", body_id);
					bean.set("perObject", perObject);
					bean.set("perMainBodySort", mainbodysetlist.get(body_id));
					bean.set("rank", Float.toString(bodyRank));
					bean.set("dyna_obj_type", dyna_obj_type);
					bean.set("pbOpt", bodyOptMap.get(body_id));
					if (!"4".equalsIgnoreCase(dyna_obj_type))
						bean.set("dyna_obj", codeitemid);
					else
						bean.set("dyna_obj", codeitemid);
					rolelist.add(bean);
				}
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
			/*
			Collections.sort(rolelist, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					LazyDynaBean bean1 = (LazyDynaBean) o1;
					LazyDynaBean bean2 = (LazyDynaBean) o2;
					return bean1.get("body_id").toString().compareTo(bean2.get("body_id").toString());
				}
			});
*/
		

		}
		this.getFormHM().put("rolelist", rolelist);
	}

	private String getSql(ArrayList list, String planid, String dyna_obj)
	{
		StringBuffer sql = new StringBuffer();
		sql.append("select * from per_dyna_bodyrank where plan_id ='" + planid + "' and dyna_obj = '" + dyna_obj + "' ");
		for (int i = 0; i < list.size(); i++)
			sql.append(" and body_id <> '" + list.get(i) + "'");
		return sql.toString();

	}

	/** 获得某个对象某个主体类别的权重 */
	private float getMainBodyRank(String planid, String dyna_obj, String body_id, float defaultRank) throws GeneralException
	{
		float rank = defaultRank;
		String sql = "select * from per_dyna_bodyrank where plan_id ='" + planid + "' and dyna_obj = '" + dyna_obj + "' and body_id=" + body_id;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
				rank = this.frowset.getFloat("rank");
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rank;

	}

	/** 取得上层的个性主体类别权重 */
	private float getBodyRank(String dyna_obj_type, String planid, String dyna_obj, String body_id, float defaultRank) throws GeneralException
	{
		float rank = defaultRank;
		for (int i = 0; i < this.topOrgs.size(); i++)
		{
			String topOrg =(String) this.topOrgs.get(i);
			if (dyna_obj != null && !"".equals(dyna_obj))
			{
				Float rank1 = (Float) this.mainbodyRankMap.get(dyna_obj + ":" + body_id);
				if (rank1 != null && String.valueOf(rank1).trim().length()>0)//设置了动态主体权重
					rank = rank1.floatValue();
				if(rank1!=null)
					return rank;
				if (dyna_obj.equals(topOrg))
					return rank;

				//while (!String.valueOf(rank).trim().equalsIgnoreCase("0.0") && !String.valueOf(defaultRank).trim().equalsIgnoreCase("0.0") && rank == defaultRank)
				while (rank == defaultRank)
				{ //默认主体权重可以设置为0  2013.11.14 pjf
					dyna_obj = (String) this.parentidMap.get(dyna_obj);
					if (dyna_obj != null && !"".equals(dyna_obj))
						rank = this.getBodyRank(dyna_obj_type, planid, dyna_obj, body_id, defaultRank);
					else
						return rank;
				}

			}
		}
	
		return rank;
	}

	private HashMap getMainBodyRankMap(String planid) throws GeneralException
	{
		HashMap rank = new HashMap();
		String sql = "select * from per_dyna_bodyrank where plan_id ='" + planid + "'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{
			this.frowset = dao.search(sql.toString());
			while (this.frowset.next())
			{
				String body_id = this.frowset.getString("body_id");
				String dyna_obj = this.frowset.getString("dyna_obj");
				rank.put(dyna_obj + ":" + body_id, new Float(this.frowset.getFloat("rank")));
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return rank;

	}

	public String getParentId(String codeitemid)
	{

		String parentId = "";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		try
		{

			String sql = "select parentid from organization where parentid!=codeitemid and  codeitemid='" + codeitemid + "'";
			this.frowset = dao.search(sql.toString());

			if (this.frowset.next())
			{
				String code = this.frowset.getString("parentid");
				if (code != null)
					parentId = code;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return parentId;
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
}
