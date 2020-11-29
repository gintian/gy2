package com.hjsj.hrms.transaction.performance;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.performance.kh_plan.ExamPlanBo;
import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <p>Title:SearchPerObjectTrans.java</p>
 * <p>Description>:考核表分发</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2011-4-26 下午03:56:27</p>
 * <p>@version: 5.0</p>
 * <p>@author: JinChunhai
 */

public class SearchPerObjectTrans extends IBusiness
{

	private String getFirstDbase(ArrayList dblist)
	{
		CommonData vo = (CommonData) dblist.get(0);
		return vo.getDataValue();
	}

	public void execute() throws GeneralException
	{
		ArrayList dblist = new ArrayList();
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		StringBuffer sql = new StringBuffer(" from per_object  where  ");
		String object_type = "2"; // 考核类别 1：团队 2：人员 3：单位 4：部门
		String busitype = "0";//0:绩效   1：能力素质
		try
		{
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String a_code = (String) hm.get("a_code");
			hm.remove("a_code");
			this.getFormHM().put("a_code", a_code);
			String dbpre = (String) this.getFormHM().get("dbpre");
			String organization = userView.getUserOrgId();
			ArrayList dblist2 = userView.getPrivDbList();
			boolean isUsr = false; // 是否有在职人员库的权限
			for (Iterator t = dblist2.iterator(); t.hasNext();)
			{
				String temp = (String) t.next();
				if ("Usr".equals(temp))
				{
					isUsr = true;
					break;
				}
			}
			isUsr = true;// 将程序改为不受数据库权限的控制 2010-07-13 修改
			// 得到绩效考核计划列表
			String perPlanSql = "select plan_id,b0110,plan_visibility,name,status,template_id from per_plan where status in (3,4,5,6,8) ";
			
			// 权限过滤
			StringBuffer priv = new StringBuffer();
			if (!this.userView.isSuper_admin()) 
			{			
				//权限范围内计划
				TrainCourseBo bo = new TrainCourseBo(this.userView);
				String unit = bo.getUnitIdByBusi();
				
				//不是全权
				if(!"UN`".equals(unit))
				{
					//不是0权限，加权限控制
					if(!"".equals(unit))
					{				
						String []units = unit.split("`");

						priv.append(" and (");
						for (int i = 0; i < units.length; i++) 
						{
							if (i != 0) 
							{
								priv.append(" or ");
							} 
							String b0110s = units[i].substring(2);
							priv.append("b0110 like '");
							priv.append(b0110s);
							priv.append("%'");
						}
						priv.append(" or b0110='' or "+Sql_switcher.isnull("b0110", "'-1'")+"='-1'");
						priv.append(")");
					}
					else
					{
						priv.append(" or b0110='' or "+Sql_switcher.isnull("b0110", "'-1'")+"='-1'");
					}
				}
			}
			perPlanSql += (priv);
			
			perPlanSql += " order by " + Sql_switcher.isnull("a0000", "999999999") + " asc,plan_id desc";
			
			this.frowset = dao.search(perPlanSql);
			ArrayList temp = new ArrayList();
			
			ExamPlanBo bo = new ExamPlanBo(this.frameconn);
			HashMap map = bo.getPlansByUserView(this.userView, "");
			
			// 得到管理范围
			String a_codeid = userView.getManagePrivCode();
			String a_codevalue = userView.getManagePrivCodeValue();
			String manageCode = a_codeid + a_codevalue;

			//得到所有的计划列表
			while (this.frowset.next())
			{	
				if(map.get(this.getFrowset().getString("plan_id"))==null)
		    		continue;
				
				String plan_id = this.getFrowset().getString("plan_id");
				String name = this.getFrowset().getString("name");
				int status = this.getFrowset().getInt("status");
				String[] tempString =
				{ plan_id, String.valueOf(status).trim() };
				temp.add(tempString);
				String aStatus = "";
				if (status == 3)
					aStatus = "已发布";
				else if (status == 4)
					aStatus = "已启动";
				else if (status == 5)
					aStatus = "暂停";
				else if (status == 6)
					aStatus = "已开始评估";
				else if (status == 8)
					aStatus = "已分发";

/*				String b0110 = this.getFrowset().getString("b0110");
				if (!b0110.equalsIgnoreCase("HJSJ"))
				{
					// 非公共资源计划 根据新加的字段plan_visibility来控制 如果=0(管理范围为本级和上级可见) =1(管理范围为本级和下级组织单元可见)
					String plan_visibility = this.getFrowset().getString("plan_visibility") == null ? "0" : this.getFrowset().getString("plan_visibility");
					String operOrg = userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
					if (operOrg.length() > 2)
					{
						if(operOrg.equalsIgnoreCase("UN`"))//授权了全部 
						{
				
						}else
						{
							boolean isHavaUpperOrg=false;//存在上级操作单位
							boolean isHavaLowerOrg=false;//存在下级操作单位
							String[] operOrgs = operOrg.split("`");
							for (int i = 0; i < operOrgs.length; i++)
							{							
								if (operOrgs[i].substring(0, 2).equalsIgnoreCase("UN") || operOrgs[i].substring(0, 2).equalsIgnoreCase("UM"))
								{
									String operOrgCode = operOrgs[i].substring(2);
									if (b0110.length() < operOrgCode.length())
									{
										if (operOrgCode.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 操作单位是其下级
										{
											isHavaLowerOrg=true;
										} else
											// 不属于上下级关系
											continue;
									} else if (b0110.length() > operOrgCode.length())
									{
										if (b0110.substring(0, operOrgCode.length()).equalsIgnoreCase(operOrgCode))// 操作单位是其上级
										{
											isHavaUpperOrg=true;
										} else
											// 不属于上下级关系
											continue;
									} else if (b0110.length() == operOrgCode.length())
									{
										if (!b0110.equalsIgnoreCase(operOrgCode))// 不属于上下级关系
											continue;
										else
										{//相同的情况
											isHavaUpperOrg=true;
											isHavaUpperOrg=true;
										}
									}
								}								
							}
							
							if (isHavaUpperOrg==false && isHavaLowerOrg==true && plan_visibility.equals("0"))//只是设置了下级操作单位
								continue;
							else if(isHavaUpperOrg==false && isHavaLowerOrg==false)// 不属于上下级关系
								continue;
							else if(isHavaUpperOrg==true && isHavaUpperOrg==false)
							{
								
							}
							else if(isHavaUpperOrg==true && isHavaLowerOrg==true)
							{
								
							}
						}						
					} else
					{
						if (manageCode.trim().length() > 0)// 说明授权了
						{
							if (manageCode.equalsIgnoreCase("UN"))// 说明授权了组织结构节点 此时userView.getManagePrivCodeValue()得到空串 范围最大
							{
								
							} else
							{
								if (b0110.length() < a_codevalue.length())
								{
									if (a_codevalue.substring(0, b0110.length()).equalsIgnoreCase(b0110))// 管理范围是其下级
									{
										if (plan_visibility.equals("0"))
											continue;
									} else
										// 不属于上下级关系
										continue;
								} else if (b0110.length() > a_codevalue.length())
								{
									if (b0110.substring(0, a_codevalue.length()).equalsIgnoreCase(a_codevalue))// 管理范围是其上级
									{
										
									} else
										// 不属于上下级关系
										continue;
								} else if (b0110.length() == a_codevalue.length())
								{
									if (!b0110.equalsIgnoreCase(a_codevalue))// 不属于上下级关系
										continue;
								}
							}
						} else
							continue;
					}
				}
*/
				
				
				CommonData vo = new CommonData(plan_id, plan_id + "." + name + "(" + aStatus + ")");
				dblist.add(vo);
			}

			
			
			if (a_code == null || "".equals(a_code))
				a_code = "UN";

			if (dbpre == null || "".equals(dbpre))
			{
				if (dblist.size() > 0)
					dbpre = getFirstDbase(dblist);
				else
					dbpre = "0";
			}

			// 给出选中的计划的状态；
			for (Iterator t = temp.iterator(); t.hasNext();)
			{
				String[] vo = (String[]) t.next();
				if (dbpre.equals(vo[0]))
					this.getFormHM().put("status", vo[1]);
			}
			this.getFormHM().put("dbpre", dbpre);
			String plan_b0110 = "HJSJ";
			String method = "1";
			String templateId = "";
			this.frowset = dao.search("select object_type,b0110,method,template_id,busitype from per_plan where plan_id=" + dbpre);
			if (this.frowset.next())
			{
				object_type = this.frowset.getString(1);
				plan_b0110 = this.frowset.getString(2);
				method = this.frowset.getString(3);
				templateId = this.frowset.getString(4);
				busitype = this.frowset.getString(5);
			}
			this.getFormHM().put("planMthod", method);
			this.getFormHM().put("plan_b0110", plan_b0110);
			this.getFormHM().put("templateId", templateId);
			String codesetid = a_code.substring(0, 2);
			String codevalue = a_code.substring(2);
			sql.append(" plan_id=" + dbpre);

			if (codevalue.length() > 0)
			{
				if ("UN".equals(codesetid))
				{
					sql.append(" and B0110 like '" + codevalue + "%'");
				}
				if ("2".equals(object_type))
				{
					if ("UM".equals(codesetid))
					{
						sql.append(" and E0122 like '" + codevalue + "%'");
					} else if ("@K".equals(codesetid))
					{
						sql.append(" and E01A1 like '" + codevalue + "%'");
					}
				} else if (!"2".equals(object_type))
				{
					// if(codesetid.equals("UM"))
					// {
					sql.append(" and object_id like '" + codevalue + "%'");
					// }
				}

			}

			/** ******** 获得组织权限 ************* */
			// if(object_type.equals("2"))
			// {
			// /* 权限控制 */
			// if(!userView.isSuper_admin()&&isUsr)
			// {
			// String conditionSql=" select A0100 "+userView.getPrivSQLExpression("Usr",true);
			// sql.append(" and object_id in ("+conditionSql+" )");
			// }
			// else if(!userView.isSuper_admin()&&!isUsr)
			// {
			// sql.append(" and 1=2 ");
			// }
			// }
			// else if(!object_type.equals("2"))
			// {
			// if(!userView.isSuper_admin())
			// {
			// sql.append(" and object_id like '"+userView.getManagePrivCodeValue()+"%'");
			// }
			// }
			/** * ---------end----------- */

			PerformanceImplementBo pb = new PerformanceImplementBo(this.getFrameconn());
			String objWhere = pb.getPrivWhere(userView);// 根据用户权限先得到一个考核对象的范围
//			if (!userView.isSuper_admin())
				sql.append(" and object_id in (select object_id from per_object where plan_id=" + dbpre + " " + objWhere + ")");

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			this.getFormHM().put("mainBodyID", "");
			this.getFormHM().put("where_str", sql.toString());
			this.getFormHM().put("Dblist", dblist);
			this.getFormHM().put("objectType", object_type);
			this.getFormHM().put("busitype", busitype);
			if (userView.isSuper_admin())
				this.getFormHM().put("managerstr", "");
			else
				this.getFormHM().put("managerstr", userView.getManagePrivCodeValue());
		}

	}

}
