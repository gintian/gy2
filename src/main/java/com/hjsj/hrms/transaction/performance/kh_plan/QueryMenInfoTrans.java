package com.hjsj.hrms.transaction.performance.kh_plan;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:查询人员所属机构信息</p>
 * <p>Company:HJHJ</p>
 * <p>Create time:Jul 19, 2010</p>
 * @author JinChunhai
 * @version 5.0
 */

public class QueryMenInfoTrans extends IBusiness
{

	private HashMap descmap = new HashMap();
	private HashMap parentmap = new HashMap();

	public void execute() throws GeneralException
	{
		try
		{
			String name = PubFunc.getStr(SafeCode.decode((String) this.getFormHM().get("name")));
			String dbname = (String) this.getFormHM().get("dbname");
			String opt = (String)this.getFormHM().get("opt");
			if ("9".equals(opt)){//需从所有认证库查,考核模块都是只传在职库usr， wangrd 2015-03-18
		        RecordVo login_vo=ConstantParamter.getConstantVo("SS_LOGIN");
		        if(login_vo!=null) 
		            dbname = login_vo.getString("str_value").toLowerCase();
			}
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String orgLink = "";
			ArrayList orgLinks = new ArrayList();// 相同姓名的若干人
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.getFrameconn());
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
			
			FieldItem item = DataDictionary.getFieldItem(onlyname);
			if(onlyname!=null && onlyname.trim().length()>0 && !"#".equals(onlyname))
			{				
				String useFlag = item.getUseflag(); 
				if("0".equalsIgnoreCase(useFlag))
					throw new GeneralException("定义的唯一性指标未构库,请构库后再进行此操作！");	
			}
			
			String[] strs = dbname.split(",");
			String whl = "";
			if (item != null)
			{
				whl = " OR " + item.getItemid() + " like '%" + name + "%'";
			}

			PerformanceImplementBo pb=new PerformanceImplementBo(this.frameconn);	
			String objPrivWhere = pb.getPrivWhere(this.userView);// 根据用户权限先得到一个考核对象的范围			
			

			
			String plan_id = (String)this.getFormHM().get("planid");
			String plan_b0110 = "";			
			String khObjCopyed="";
			if("5".equals(opt) || "8".equals(opt))
				khObjCopyed=(String)this.getFormHM().get("khObjCopyed");
			String oldPlan_id="";
			if("2".equals(opt))
				oldPlan_id=(String)this.getFormHM().get("oldPlan_id");
			StringBuffer plan_b0110_whl = new StringBuffer();
			if("0".equals(opt))
			{				
				plan_b0110 = pb.getPlanVo(plan_id).getString("b0110");
				if(!"hjsj".equalsIgnoreCase(plan_b0110))//考核对象受计划所属机构的限制
				{
						if(AdminCode.getCode("UM",plan_b0110)!=null)
							plan_b0110_whl.append(" and e0122 like '"+plan_b0110+"%' ");
						else if(AdminCode.getCode("UN",plan_b0110)!=null)
							plan_b0110_whl.append(" and b0110 like '"+plan_b0110+"%' ");
				}
			}
			
			if(("1".equals(opt) || "9".equals(opt)|| "12".equals(opt)))
			{
				String accordPriv = (String)this.getFormHM().get("accordPriv");
				if( "false".equalsIgnoreCase(accordPriv))
					objPrivWhere="";
			}
			
			/* opt=0 考核实施/考核对象/手工选择 登录用户权限范围内的考核对象（包括选人和选机构）非公共资源计划还要在计划b0110内 
			 * opt=6 考核关系/手工选择 		   选择用户权限内的人 
			 * opt=1 考核实施/指定考核主体/手工选择 选择用户权限内的人（通过参数accordPriv来控制是否限制用户管理范围）
			 * opt=9 考核关系/指定考核主体/手工选择 选择用户权限内的人（通过参数accordPriv来控制是否限制用户管理范围）
			 * opt=2 绩效评估/显示/手工选择 选择计划中考核对象且在登录用户范围内的对象
			 * opt=5 考核表分发/复制主体给  选择计划中考核对象且在登录用户范围内的对象 且不包括被复制的考核对象
			 * opt=3 绩效实施/设置动态主体权重 选择计划中考核对象且在登录用户范围内的对象
			 * opt=4 绩效实施/设置动态指标权重 选择计划中考核对象且在登录用户范围内的对象
			 * opt=7 绩效实施/目标卡制定      选择计划中考核对象且在登录用户范围内的对象
			 * opt=8 自助服务/绩效考评/目标考核/目标卡制定/复制目标卡至     选择计划中考核对象且在登录用户范围内的对象 且不包括被复制的考核对象
			 * opt=12 人事异动 自定义审批流程 
			 */
			
			HashMap planObjsMap = new HashMap();// 可选择的考核对象
			if ("2".equals(opt) || "5".equals(opt) || "8".equals(opt))// 绩效评估/显示/手工选择 选择计划中考核对象且在登录用户范围内的对象
			{
				String sql0 = "";
				if("2".equals(opt))
					sql0 = "select object_id from per_object where plan_id=" + plan_id + " " + objPrivWhere + " and object_id in (select object_id from per_object where plan_id=" + oldPlan_id + " " + objPrivWhere + ") " ;				
				else
					sql0 = "select object_id from per_object where plan_id=" + plan_id + " " + objPrivWhere;
				this.frowset = dao.search(sql0);
				while (	this.frowset.next())
					planObjsMap.put(this.frowset.getString(1), "");
			}
			
			
			
			String sql = "select codeitemid,parentid,codeitemdesc from organization ";
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				descmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("codeitemdesc"));
				parentmap.put(this.frowset.getString("codeitemid"), this.frowset.getString("parentid"));
			}

			for (int i = 0; i < strs.length; i++)
			{
				if (strs[i].length() > 0)
				{

					// 添加应用库
					String dbStr = "";
					this.frowset = dao.search("select dbname from dbname where lower(pre)='" + strs[i].toLowerCase() + "'");
					if (this.frowset.next())
						dbStr += "/" + this.frowset.getString("dbname");

					String strSql = "select b0110,e0122,e01a1,a0100  from " + strs[i] + "A01 where (a0101 like '%" + name + "%' " + whl+") "+ objPrivWhere;
					if("9".equals(opt))
						strSql+=" order by b0110,e0122,e01a1 ";
					if("0".equals(opt))
						strSql+=plan_b0110_whl.toString();
					this.frowset = dao.search(strSql);
					while (this.frowset.next())
					{
						String e01a1 = this.frowset.getString("e01a1");
						String e0122 = this.frowset.getString("e0122");
						String b0110 = this.frowset.getString("b0110");
						String a0100 = this.frowset.getString("a0100");
						
						if ("2".equals(opt) || "5".equals(opt) || "8".equals(opt))
						{
							if(planObjsMap.get(a0100)==null)
								continue;
							if("5".equals(opt)&&khObjCopyed.equals(a0100))
								continue;
							if("8".equals(opt)&&khObjCopyed.equals(a0100))
								continue;
						}
						if (e01a1 != null && e01a1.length() > 0 && AdminCode.getCodeName("@K", e01a1)!=null && !"".equals(AdminCode.getCodeName("@K", e01a1)))
							orgLink = getSuperOrgLink(e01a1, "@K");
						else if (e0122 != null && e0122.length() > 0 && AdminCode.getCodeName("UM", e0122)!=null && !"".equals(AdminCode.getCodeName("UM", e0122)))
							orgLink = getSuperOrgLink(e0122, "UM");
						else if (b0110 != null && b0110.length() > 0 && AdminCode.getCodeName("UN", b0110)!=null && !"".equals(AdminCode.getCodeName("UN", b0110)))
							orgLink = getSuperOrgLink(b0110, "UN");
						if (orgLink.length() > 0)
							orgLink += dbStr;
						
						//if(orgLinks.size()==0)//模糊查询时要查询出所有人   郭峰修改
							orgLinks.add(orgLink);
					}
				}
			}
			this.getFormHM().put("orgLinks", orgLinks);
		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

	public String getSuperOrgLink2(String codeitemid, String codesetid)
	{
		StringBuffer org_str = new StringBuffer("");
		try
		{
			String itemid = codeitemid;
			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet frowset = null;
			while (true)
			{
				frowset = dao.search("select codeitemid,codeitemdesc from organization where codeitemid=(select parentid  from organization where codeitemid='" + itemid + "')");
				if (frowset.next())
				{
					String code_item_id = frowset.getString("codeitemid");
					if (code_item_id.equals(itemid))
						break;
					else
					{
						org_str.append("/" + frowset.getString("codeitemdesc"));
						itemid = code_item_id;
					}
				}
			}
			if (frowset != null)
				frowset.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return org_str.toString();
	}

	public String getSuperOrgLink(String codeitemid, String codesetid)
	{
		StringBuffer org_str = new StringBuffer("");
		try
		{
			String itemid = codeitemid;
			org_str.append(AdminCode.getCodeName(codesetid, itemid));
			while (true)
			{
				String parentid = (String) this.parentmap.get(itemid);
				if (parentid.equals(itemid))
					break;
				else
				{
					org_str.append("/" + this.descmap.get(parentid));
					itemid = parentid;
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return org_str.toString();
	}

}
