package com.hjsj.hrms.transaction.gz.gz_data;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryPropertyBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>
 * Title:查询薪资表数据交易
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:2009-9-10:上午09:44:45
 * </p>
 * 
 * @author fanzhiguo
 * @version 4.0
 */
public class SearchGzTableDataTrans extends IBusiness
{

	public void execute() throws GeneralException
	{

		// String flow_flag=(String)this.getFormHM().get("flow_flag");
		String salaryid = (String) this.getFormHM().get("salaryid");
		String a_code = (String) this.getFormHM().get("a_code");
		String showUnitCodeTree="0";  //是否按操作单位来显示树
		String flag = (String) this.getFormHM().get("flag");
		flag = flag != null && flag.trim().length() > 0 ? flag : "";
		/**人员过滤条件编号，全部为all*/
		String swhere="";
		String condid=(String)this.getFormHM().get("cond_id_str");
		if(condid==null|| "".equalsIgnoreCase(condid))
			condid=(String)this.getFormHM().get("condid");	
		if(condid==null|| "".equalsIgnoreCase(condid))
			condid="all";
		/** 项目过滤条件号，全部为all */
		String itemid = (String) this.getFormHM().get("itemid");
		if (itemid == null || "".equalsIgnoreCase(itemid))
			itemid = "all";
		/** 解决当选择项目过滤，进行导入操作，导入或返回后项目过滤为全部问题lizhenwei add */
		if (((HashMap) this.getFormHM().get("requestPamaHM")).get("import") != null)
		{
			itemid = (String) (((HashMap) this.getFormHM().get("requestPamaHM")).get("import"));
			((HashMap) this.getFormHM().get("requestPamaHM")).remove("import");
		}
		try
		{
			/** 薪资类别 */
			SalaryTemplateBo gzbo = new SalaryTemplateBo(this.getFrameconn(), Integer.parseInt(salaryid), this.userView);
			showUnitCodeTree=gzbo.getControlByUnitcode();
			String comflag = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.COMPARE_FIELD);
			// String priv_mode1="0";//是否出现数据比对菜单
			// if(comflag!=null&&!comflag.equals(""))
			// priv_mode1 = "1";
			if (salaryid == null || "-1".equalsIgnoreCase(salaryid))
			{
				throw new GeneralException(ResourceFactory.getProperty("error.notdefine.salaryid"));
			}

			gzbo.synchronismSalarySet();
			gzbo.syncGzTableStruct();
			if (gzbo.getManager() != null && gzbo.getManager().trim().length() > 0)
				gzbo.update("update " + gzbo.getGz_tablename() + " set sp_flag2='01' where sp_flag2 is null");
			// 当表类的条件和权限在建完数据后改动了,需进行权限及条件过滤(需在变动比对中去掉)
			// gzbo.delNoConditionData2(gzbo.getGz_tablename());

			/** 项目过滤 */
			ArrayList fieldlist = gzbo.getFieldlist();
			
			/* 自助平台—员工信息-数据上报，项目过滤，新建保存后，选择了很多指标项，确定后，回到的界面只显示了两列，需要重新刷新才可以 2014-10-24 xiaoyun start */
			String filterid = gzbo.getFiltersIds(salaryid);
			ArrayList itemfilterlist = gzbo.getItemFilterList(filterid);
			/* 自助平台—员工信息-数据上报，项目过滤，新建保存后，选择了很多指标项，确定后，回到的界面只显示了两列，需要重新刷新才可以 2014-10-24 xiaoyun end */
			/*
			 * for(int i=0;i<fieldlist.size();i++) { Field
			 * field=(Field)fieldlist.get(i); //
			 * if(field.getName().equalsIgnoreCase("a00z0")||field.getName().equalsIgnoreCase("a00z1")||field.getName().equalsIgnoreCase("nbase")) //
			 * field.setVisible(true); }
			 */
			// String
			// gz_module=(String)((HashMap)this.getFormHM().get("requestPamaHM")).get("gz_module");
			String gz_module = "0";
			SalaryPropertyBo bo = new SalaryPropertyBo(this.getFrameconn(), salaryid, Integer.parseInt(gz_module), this.getUserView());
			String priv_mode = bo.getCtrlparam().getValue(SalaryCtrlParamBo.PRIV_MODE, "flag");
			if (priv_mode == null || "".equals(priv_mode))
			{
				priv_mode = "0";
			}
			if (!"all".equalsIgnoreCase(itemid))
				fieldlist = gzbo.filterItemList(fieldlist, itemid);
			String pro_field = (String) this.getFormHM().get("proright_str");
			this.getFormHM().put("proright_str", pro_field);
			if (!(pro_field == null || "".equalsIgnoreCase(pro_field)))
			{
				fieldlist = gzbo.filterItemsList(gzbo.getFieldlist(), pro_field);
			} else if ("new".equalsIgnoreCase(itemid) && "".equalsIgnoreCase(pro_field))
			{
				fieldlist = gzbo.getFieldlist();
			}

			 if(!"all".equalsIgnoreCase(condid))
				 swhere=gzbo.getFilterWhere(condid,gzbo.getGz_tablename());

			if (gzbo.isApprove())
			{
				fieldlist = gzbo.filterList(fieldlist, flag);
			}
			StringBuffer filterWhl = new StringBuffer(""); // 过滤条件--sql

			String salaryIsSubed = "true"; // 薪资是否为已提交状态
			if (gzbo.isSalaryPayed2())
				salaryIsSubed = "false";
			String manager = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			this.getFormHM().put("manager", manager != null ? manager : "");

			/** 如果为操作用户同时薪资已确认的数据不让修改 */
			if (manager.length() > 0 && !manager.equalsIgnoreCase(this.userView.getUserName()) && "true".equals(salaryIsSubed))
			{
				gzbo.setFieldlist_read(fieldlist);
				this.getFormHM().put("isEditDate", "false");
			} else
				this.getFormHM().put("isEditDate", "true");

			// system.properties salaryitem=false前台计算项不能编辑
			if (SystemConfig.getPropertyValue("salaryitem") != null && "true".equalsIgnoreCase(SystemConfig.getPropertyValue("salaryitem")))
				gzbo.setFieldlist_read2(fieldlist);

			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("subFlag", gzbo.getSubFlag());
			this.getFormHM().put("falg", flag);

			/** 数据过滤 */
			StringBuffer buf = new StringBuffer();
			buf.append("select * from ");
			buf.append(gzbo.getGz_tablename());
			// buf.append(",dbname where
			// upper("+gzbo.getGz_tablename()+".nbase)=upper(dbname.pre) ");
			// //dengcan
			buf.append(" where 1=1 ");

			if (!(a_code == null || "".equalsIgnoreCase(a_code)))
			{
//
//				
//				
//				if (manager != null && manager.length() > 0 && !manager.equalsIgnoreCase(this.userView.getUserName())&&showUnitCodeTree.equals("0"))
//				{
//					if (a_code.substring(2).length() == 0 && priv_mode.equals("1"))
//					{
//						if (this.userView.getManagePrivCode().length() == 0)
//							a_code = "UN";
//						else if (this.userView.getManagePrivCode().equals("@K"))
//							a_code = gzbo.getUnByPosition(this.userView.getManagePrivCodeValue());
//						else
//							a_code = this.userView.getManagePrivCode() + this.userView.getManagePrivCodeValue();
//					}
//				}
				String codesetid = a_code.substring(0, 2);
				String value = a_code.substring(2);
				if ("UN".equalsIgnoreCase(codesetid)&&value.trim().length()>0)
				{
					// buf.append(" where (B0110 like '");
					if("1".equals(showUnitCodeTree))
					{
						String orgid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"orgid");
						orgid = orgid != null ? orgid : "";
						String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
						deptid = deptid != null ? deptid : "";
						StringBuffer tempBuf=new StringBuffer("");
						tempBuf.append("");
						if(orgid.length()>0)
						{
							tempBuf.append(" or "+orgid+" like '"+value+"%' ");
						}
						if(deptid.length()>0)
						{
							tempBuf.append(" or "+deptid+" like '"+value+"%' ");
						}
						if(tempBuf.length()>0)
						{
							buf.append(" and ( "+tempBuf.substring(3)+" )");
							filterWhl.append(" and ( "+tempBuf.substring(3)+" )");
						}
					}
					else
					{
					
						buf.append(" and (B0110 like '");
						buf.append(value);
						buf.append("%'");
						filterWhl.append(" and (B0110 like '" + value + "%'");
						if ("".equalsIgnoreCase(value))
						{
							buf.append(" or B0110 is null");
							filterWhl.append(" or B0110 is null");
						}
						buf.append(")");
						filterWhl.append(")");
					}
				}
				if ("UM".equalsIgnoreCase(codesetid)&&value.trim().length()>0)
				{
					if("1".equals(showUnitCodeTree))
					{
						String deptid = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SUM_FIELD,"deptid");
						deptid = deptid != null ? deptid : "";
						StringBuffer tempBuf=new StringBuffer("");
						tempBuf.append("");
						if(deptid.length()>0)
						{
							tempBuf.append(" or "+deptid+" like '"+value+"%' ");
						}
						if(tempBuf.length()>0)
						{
							buf.append(" and ( "+tempBuf.substring(3)+" )");
							filterWhl.append(" and ( "+tempBuf.substring(3)+" )");
						}
					}
					else
					{
						buf.append(" and E0122 like '");
						buf.append(value);
						buf.append("%'");
						filterWhl.append(" and E0122 like '" + value + "%'");
					}
				}
				
				
				
			}

			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName())&&!this.userView.isSuper_admin()){
				String unitIdByBusiOutofPriv = SystemConfig.getPropertyValue("unitIdByBusiOutofPriv");
				String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
				if(b_units.length()==0&&(unitIdByBusiOutofPriv!=null&& "1".equals(unitIdByBusiOutofPriv))){
					/**导入数据*/
					String dbpres=gzbo.getTemplatevo().getString("cbase");
					/**应用库前缀*/
					String[] dbarr=StringUtils.split(dbpres, ",");
					StringBuffer sub_str=new StringBuffer("");
					for(int i=0;i<dbarr.length;i++)
					{
						String pre=dbarr[i];
						if(!this.userView.isSuper_admin()&&this.userView.getDbpriv().toString().toLowerCase().indexOf(","+pre.toLowerCase()+",")==-1)
						{
							sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and 1=2 )");
						}
						else
						{
							sub_str.append(" or (upper("+gzbo.getGz_tablename()+".nbase)='"+pre.toUpperCase()+"' and upper(" + gzbo.getGz_tablename() + ".a0100) in (select a0100 "+this.userView.getPrivSQLExpression(pre, false)+" ) )");
						}
					}
					if(sub_str.length()>0)
					{
						buf.append(" and ( "+sub_str.substring(3)+" )");
						filterWhl.append(" and ( "+sub_str.substring(3)+" )");
					}
				}else{
					String privsql = gzbo.getPrivSQL("", "", salaryid, b_units);
					buf.append(" and ("+privsql+")");
					filterWhl.append(" and ("+privsql+")");
				}
			}
//			if(showUnitCodeTree.equals("1"))
//			{
//				String unitcodes=this.userView.getUnit_id();  //UM010101`UM010105`
//				String whl_str=gzbo.getWhlByUnits();
//				buf.append(whl_str);
//				filterWhl.append(whl_str); 
//			}
//			else if (manager != null && manager.length() > 0 && !manager.equalsIgnoreCase(this.userView.getUserName()) && !this.userView.isSuper_admin())
//			{
//
//				/** 导入数据 */
//				String dbpres = gzbo.getTemplatevo().getString("cbase");
//				/** 应用库前缀 */
//				String[] dbarr = StringUtils.split(dbpres, ",");
//				StringBuffer sub_str = new StringBuffer("");
//				for (int i = 0; i < dbarr.length; i++)
//				{
//					String pre = dbarr[i];
//					if (!this.userView.isSuper_admin() && this.userView.getDbpriv().toString().toLowerCase().indexOf("," + pre.toLowerCase() + ",") == -1)
//					{
//						sub_str.append(" or (upper(" + gzbo.getGz_tablename() + ".nbase)='" + pre.toUpperCase() + "'  and 1=2 )");
//					} else
//					{
//						sub_str.append(" or (upper(" + gzbo.getGz_tablename() + ".nbase)='" + pre.toUpperCase() + "'  and a0100 in (select a0100 "
//								+ this.userView.getPrivSQLExpression(pre, false) + " ) )");
//					}
//
//				}
//				if (sub_str.length() > 0)
//				{
//					buf.append(" and ( " + sub_str.substring(3) + " )");
//					filterWhl.append(" and ( " + sub_str.substring(3) + " )");
//				}
//			}

			if(swhere.length()>0)
			{
				buf.append(" and ");
				buf.append("("+swhere+")");
				filterWhl.append(" and ("+swhere+")");
			}

			/**人员筛选*/
			String empfiltersql = (String)this.getFormHM().get("empfiltersql");
			/* 自助服务-人员信息-数据上报-新建查询条件 xiaoyun 2014-10-24 start */
			empfiltersql = PubFunc.decrypt(empfiltersql);
			/* 自助服务-人员信息-数据上报-新建查询条件 xiaoyun 2014-10-24 end */
			if(!(empfiltersql==null || "".equals(empfiltersql)))
			{
				buf.append(" and ("+empfiltersql+") ");
				filterWhl.append(" and ("+empfiltersql+")");
			}
			else
			{
				if("new".equalsIgnoreCase(condid))
				{
					condid="all";
				}
			}
			
			/* 安全问题：自助服务-员工信息-数据上报 sql加密 xiaoyun 2014-10-23 start */
			this.userView.getHm().put("gz_filterWhl",filterWhl.toString());			
			this.getFormHM().put("filterWhl",PubFunc.encrypt(filterWhl.toString()));
			//this.getFormHM().put("filterWhl",filterWhl.toString());
			/* 安全问题：自助服务-员工信息-数据上报 sql加密 xiaoyun 2014-10-23 end */
			this.getFormHM().put("empfiltersql","");

			/** 人员排序 */
			String sort_fields = (String) this.getFormHM().get("sort_table_detail");
			this.getFormHM().put("sort_table_detail", "");
			if (sort_fields != null && sort_fields.length() > 0)
			{
				String orderby = gzbo.getorderbystr(sort_fields);
				buf.append(" order by " + orderby);
				gzbo.sortemp(sort_fields, this.userView.getUserName(), salaryid);
				this.getFormHM().put("order_by", orderby);
			} else
			{
				String orderby = (String) this.getFormHM().get("order_by");
				if (orderby != null && orderby.length() > 0)
				{
					if ("sync".equalsIgnoreCase(orderby))
					{
						// buf.append(" order by nbase, A0000, A00Z0, A00Z1");
						String order_str = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER);
						if (order_str != null && order_str.trim().length() > 0)
							buf.append(" order by " + order_str);
						else
							buf.append(" order by  dbname.dbid,a0000, A00Z0, A00Z1");
					} else
						buf.append(" order by " + orderby);

				} else
				{
					// buf.append(" order by b0110,e0122,dbname.dbid,a0000,
					// A00Z0, A00Z1");
					String order_str = gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.DEFAULT_ORDER);
					if (order_str != null && order_str.trim().length() > 0)
						buf.append(" order by " + order_str);
					else
						buf.append(" order by  a0000, A00Z0, A00Z1");

				}
			}
			/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 start */
			//this.getFormHM().put("sql", buf.toString());
			this.getFormHM().put("sql", PubFunc.encrypt(buf.toString()));
			/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 end */
			this.getFormHM().put("gz_tablename", gzbo.getGz_tablename());
			if (gzbo.isApprove())
				this.getFormHM().put("appflag", "true");
			else
				this.getFormHM().put("appflag", "false");

			/** 分析当前薪资表处于什么状态，结束 */
			if (!gzbo.isApprove())
				this.getFormHM().put("bedit", "true");
			else if (gzbo.isApprove() && gzbo.isSubCondition())
				this.getFormHM().put("bedit", "true");
			else
				this.getFormHM().put("bedit", "false");
			this.getFormHM().put("salaryIsSubed", salaryIsSubed);
			/** 人员条件过滤，及项目过滤 */
			/* 自助平台—员工信息-数据上报，项目过滤，新建保存后，选择了很多指标项，确定后，回到的界面只显示了两列，需要重新刷新才可以 2014-10-24 xiaoyun start */
			//String filterid = gzbo.getFiltersIds(salaryid);
			//ArrayList itemfilterlist = gzbo.getItemFilterList(filterid);
			/* 自助平台—员工信息-数据上报，项目过滤，新建保存后，选择了很多指标项，确定后，回到的界面只显示了两列，需要重新刷新才可以 2014-10-24 xiaoyun end */
			 ArrayList manfilterlist=gzbo.getManFilterList();
			/** 删除垃圾数据 */
			gzbo.deleteSpareData();
			String  verify_ctrl=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL);
			if(verify_ctrl==null||verify_ctrl.trim().length()==0) //是否按审核条件控制
				verify_ctrl="0";
			if("1".equals(verify_ctrl))
			{
				String verify_ctrl_ff=bo.getCtrlparam().getValue(SalaryCtrlParamBo.VERIFY_CTRL,"verify_ctrl_ff");
				if(verify_ctrl_ff!=null&&verify_ctrl_ff.length()>0)
					verify_ctrl=verify_ctrl_ff;
			}
			this.getFormHM().put("verify_ctrl",verify_ctrl);
			this.getFormHM().put("itemlist", itemfilterlist);
			// if(condid==null||condid.equalsIgnoreCase(""))
			 this.getFormHM().put("condid",condid);
			 this.getFormHM().put("condlist", manfilterlist);
			// if(itemid==null||itemid.equalsIgnoreCase(""))
			if ("all".equalsIgnoreCase(itemid) && pro_field != null && pro_field.trim().length() > 1)
				itemid = "new";
			this.getFormHM().put("itemid", itemid);
			/** lizhenwei add */
			this.getFormHM().put("salaryid", salaryid);
			this.getFormHM().put("a_code", a_code == null ? "" : a_code);
			/** lizhenwei add end */

			this.getFormHM().put("userid", this.getUserView().getUserId());
			this.getFormHM().put("isAppealData", gzbo.getIsAppealData());
			// this.getFormHM().put("flow_flag",flow_flag);
			// this.getFormHM().put("priv_mode",priv_mode1);
			this.getFormHM().put("nbase", gzbo.getTemplatevo().getString("cbase"));

			this.getFormHM().put("isNotSpFlag2Records", gzbo.getIsNotSpFlag2Records());

			// returnFlag 0：返回薪资发放的类别界面 1：返回部门月奖金界面
			HashMap requestPamaHM = (HashMap) this.getFormHM().get("requestPamaHM");
			String returnFlag = (String) requestPamaHM.get("returnFlag");
			requestPamaHM.remove("returnFlag");
			returnFlag = returnFlag == null ? "0" : returnFlag;
			this.getFormHM().put("returnFlag", returnFlag);

			ArrayList musterList = new ArrayList(); // 当前工资类别定义的高级花名册
			String sql = "select * from muster_name where nModule=14 and nPrint=-1 or nPrint=" + salaryid;
			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sql);
			while (this.frowset.next())
			{
				if(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())||this.userView.isHaveResource(IResourceConstant.HIGHMUSTER,  this.frowset.getString("tabid")))//用户自定义表，加上权限限制
				{				
					LazyDynaBean bean = new LazyDynaBean();				
					bean.set("tabid", this.frowset.getString("tabid"));
					bean.set("cname", this.frowset.getString("cname"));
					musterList.add(bean);
				}
			}
			this.getFormHM().put("musterList", musterList);
			this.getFormHM().put("showUnitCodeTree",showUnitCodeTree);
			if ("1".equals(returnFlag))
			{
				String year = (String) requestPamaHM.get("theyear");
				String month = (String) requestPamaHM.get("themonth");
				String operOrg = (String) requestPamaHM.get("orgcode");
				String isleafOrg = (String) requestPamaHM.get("isleafOrg");
				String isAllDistri = (String) requestPamaHM.get("isAllDistri");
				String isOnlyLeafOrgs = (String) requestPamaHM.get("isOnlyLeafOrgs");
				String isOrgCheckNo = (String) requestPamaHM.get("isOrgCheckNo");

				requestPamaHM.remove("theyear");
				requestPamaHM.remove("themonth");
				requestPamaHM.remove("orgcode");
				requestPamaHM.remove("isleafOrg");
				requestPamaHM.remove("isAllDistri");
				requestPamaHM.remove("isOrgCheckNo");

				this.getFormHM().put("theyear", year);
				this.getFormHM().put("themonth", month);
				this.getFormHM().put("operOrg", operOrg);
				this.getFormHM().put("isLeafOrg", isleafOrg);
				this.getFormHM().put("isAllDistri", isAllDistri);
				this.getFormHM().put("isOnlyLeafOrgs", isOnlyLeafOrgs);
				this.getFormHM().put("isOrgCheckNo", isOrgCheckNo);

				String isLeafOrgReport = "0";
				String isLeafOrgDistri = "0";
				ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
				String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
				String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
				String checkUn_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "checkUn_field");// 奖金核算单位标识指标
				String dist_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "dist_field");// 下发标识指标

				String busiField = setid + "z0";
				String dateWhl = " and " + Sql_switcher.year(busiField) + "=" + year + " and " + Sql_switcher.month(busiField) + "=" + month;
				if ("1".equals(isleafOrg))
				{
					sql = "select * from " + setid + " where b0110='" + operOrg + "' and " + rep_field + "='1'" + dateWhl;
					this.frowset = dao.search(sql);
					if (this.frowset.next())
					{
						isLeafOrgReport = "1";
						isLeafOrgDistri = "1";
					} else
					{
						sql = "select * from " + setid + " where b0110='" + operOrg + "' and " + dist_field + "='1'" + dateWhl;
						this.frowset = dao.search(sql);
						if (this.frowset.next())
							isLeafOrgDistri = "1";
					}
				} else if ("1".equals(isOrgCheckNo))// 非叶子节点中的核算单位为否的标志
				{
					sql = "select * from " + setid + " where b0110='" + operOrg + "' and " + checkUn_field + "='2' and " + rep_field + "='1'" + dateWhl;
					this.frowset = dao.search(sql);
					if (this.frowset.next())
						isLeafOrgReport = "1";
				}else //普通的中间机构 看它的直接孩子是否处于上报状态
				{
					sql="select count(*) from " + setid + " where b0110 in (";
					sql+="select codeitemid from organization where  codesetid in ('UM','UN') and parentid ='"+operOrg + "') ";
					sql+=" and (" + rep_field + "='2' or "+ rep_field +" is null )"+ dateWhl;
					this.frowset = dao.search(sql);
					if (this.frowset.next())
					{
						if(this.frowset.getInt(1)==0)
							isLeafOrgReport = "1";
					}
				}
				this.getFormHM().put("isLeafOrgReport", isLeafOrgReport);
				this.getFormHM().put("isLeafOrgDistri", isLeafOrgDistri);
			}

		} catch (Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
