package com.hjsj.hrms.servlet.orgtree;

import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Category;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.RowSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AddVOrgNodetreeServlet extends HttpServlet {
	/*
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	private Category cat = Category.getInstance(this.getClass());

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuffer sbXml = new StringBuffer();
		String params = req.getParameter("params");
		String issuperuser = req.getParameter("issuperuser");
		String parentid = req.getParameter("parentid");
		String manageprive = req.getParameter("manageprive");
		String action = req.getParameter("action");
		String target = req.getParameter("target");
		String treetype = req.getParameter("treetype");
		String orgtype = req.getParameter("orgtype");
		String loadtype = req.getParameter("loadtype");
		String fromtype=req.getParameter("fromtype");
		String isRecordEntry=req.getParameter("isRecordEntry");
		isRecordEntry = isRecordEntry != null && !"".equals(isRecordEntry.trim()) ? isRecordEntry : "";
		fromtype = fromtype != null && !"".equals(fromtype.trim()) ? fromtype : "";
		
		/** 加载选项 * =0（单位|部门|职位） * =1 (单位|部门) * =2 (单位) * */
		loadtype = loadtype != null && loadtype.trim().length() > 0 ? loadtype: "0";
		String droit = req.getParameter("droit");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = req.getParameter("backdate");
		backdate = backdate != null && backdate.length() > 9 ? backdate : sdf.format(new Date());
		if (orgtype == null || orgtype.length() <= 0)
			orgtype = "org";
		UserView userView = (UserView) req.getSession().getAttribute("userView");
		String busiPriv = req.getParameter("busiPriv");
		try {
			if("entry".equals(isRecordEntry)||"browse".equals(isRecordEntry))
				sbXml.append(loadOrgItemNodes(params, issuperuser, parentid,
						manageprive, action, target, treetype, orgtype, backdate,
						userView,busiPriv, droit, loadtype, fromtype,isRecordEntry));
			else
				sbXml.append(loadOrgItemNodes(params, issuperuser, parentid,
						manageprive, action, target, treetype, orgtype, backdate,
						userView,busiPriv, droit, loadtype, fromtype));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e);
		}
		cat.debug("catalog xml" + sbXml.toString());
		resp.setContentType("text/xml;charset=utf-8");
		resp.getWriter().write(sbXml.toString());
		resp.getWriter().close();
	}

	private String loadOrgItemNodes(String params, String issuperuser,
			String parentid, String manageprive, String action, String target,
			String treetype, String orgtype, String backdate, UserView userView,String busiPriv, String droit, String loadtype, String fromtype)
			 throws Exception {
		return loadOrgItemNodes(params, issuperuser, parentid,
				manageprive, action, target, treetype, orgtype, backdate,
				userView,busiPriv, droit, loadtype, fromtype,"");
	}
	private String loadOrgItemNodes(String params, String issuperuser,
			String parentid, String manageprive, String action, String target,
			String treetype, String orgtype, String backdate, UserView userView,String busiPriv, String droit, String loadtype, String fromtype,String isRecordEntry)
			throws Exception {
		StringBuffer strXml = new StringBuffer();
		List rs = new ArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String virtualOrgSet=SystemConfig.getPropertyValue("virtualOrgSet");//获取资源文件内容
		/*
		 * if(orgtype==null||orgtype.length()<=0||!orgtype.equalsIgnoreCase("vorg"))
		 * rs=ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,issuperuser,parentid,manageprive));
		 * getVorgTreeXml(rs,manageprive,parentid);
		 */
		rs = ExecuteSQL.executeMyQuery(getLoadTreeQueryString(params,
				issuperuser, parentid, manageprive, backdate, userView,busiPriv, droit, fromtype));

		HashMap<String,Integer> parentidMap=null;
		if (!rs.isEmpty()) {
			ArrayList<String> codeitemidList=new ArrayList<String>();
			for (int i = 0; i < rs.size(); i++) {
				DynaBean rec = (DynaBean) rs.get(i);
				String codeitemid = rec.get("codeitemid") != null ? rec.get("codeitemid").toString() : "";
				if(StringUtils.isNotBlank(codeitemid)){
					codeitemidList.add(codeitemid);
				}

			}

			strXml.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<TreeNode>\n");
			for (int i = 0; i < rs.size(); i++) {
				TreeItemView treeitem = new TreeItemView();
				DynaBean rec = (DynaBean) rs.get(i);
				String org_type = rec.get("orgtype") != null ? rec.get("orgtype").toString() : "";
				String image = "";
				String codeitemid = rec.get("codeitemid") != null ? rec.get("codeitemid").toString() : "";
				// String codeitemdesc=rec.get("codeitemdesc")!=null?new
				// String(rec.get("codeitemdesc").toString().getBytes("GBK"),"ISO-8859-1"):"";
				String codeitemdesc = rec.get("codeitemdesc") != null ? com.hrms.frame.codec.SafeCode.encode(rec.get("codeitemdesc").toString()): "";
				String codesetid = rec.get("codesetid") != null ? rec.get("codesetid").toString() : "";
				String view_chart=rec.get("view_chart")!=null?rec.get("view_chart").toString():"";
				if ("2".equalsIgnoreCase(loadtype)) {
					if ("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid))
						continue;
				}
				if ("1".equalsIgnoreCase(loadtype)) {
					if ("@K".equalsIgnoreCase(codesetid))
						continue;
				}
				treeitem.setName(codesetid + codeitemid);
              
//				String codeitemidz = PubFunc.encryption(codeitemid);
//				String codesetidz = PubFunc.encryption(codesetid);
				treeitem.setText(codeitemdesc);
				treeitem.setTitle(codeitemdesc);
				treeitem.setTarget(target);
				if (rec.get("codesetid") != null&& "UN".equals(rec.get("codesetid"))) {
//                    String whl=" and " + Sql_switcher.dateValue(backdate)   + " between start_date and end_date ";
//                    if (loadtype.equalsIgnoreCase("2")) {
//                        whl=whl+" and codesetid in ('UN')";
//                    }
//                    else if (loadtype.equalsIgnoreCase("1")) {
//                        whl=whl+" and codesetid in ('UN','UM')";
//                    }
//                    else {
//                        if ("org".equals(treetype)){
//                            whl=whl+" and codesetid in ('UN','UM')";
//                        }
//                    }
//                    String sql = " select '1' from organization where parentid='"+codeitemid+"'"+whl
//                    +" union select '1' from vorganization where parentid='"+codeitemid+"'"+whl;
//
//					List list = ExecuteSQL.executeMyQuery(sql);
					if(parentidMap==null){
						parentidMap=this.mapOrgParentid(backdate,loadtype,treetype,codeitemidList);
					}
					if(parentidMap.containsKey(codeitemid)&&parentidMap.get(codeitemid)!=0)
						treeitem.setXml("/common/vorg/loadtree?params=child&amp;orgtype="
										+ org_type
										+ "&amp;treetype="
										+ treetype
										+ "&amp;parentid="
										+ codeitemid
										+ "&amp;loadtype="
										+ loadtype
										+ "&amp;kind=2&amp;issuperuser="
										+ issuperuser
										+ "&amp;manageprive="
										+ manageprive
										+ "&amp;action="
										+ action
										+ "&amp;target="
										+ target
										+ "&amp;backdate=" + backdate
										+"&amp;fromtype="+fromtype
										+"&amp;isRecordEntry="+isRecordEntry);

					if ("vorg".equals(org_type)/* &&!orgtype.equals("vorg") */) {
						if(isOrgMapAction(action) &&"1".equals(view_chart)){
							image = "/images/b_vroot_h.gif";
						}else
							image = "/images/b_vroot.gif";
					} else {
						if (sdf.parse(sdf.format(new Date())).compareTo(
								sdf.parse((String) rec.get("end_date"))) <= 0) {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								image = "/images/unit_h.gif";
							}else
								image = "/images/unit.gif";
						} else {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								image = "/images/b_unit_h.gif";
							}else
								image = "/images/b_unit.gif";
						}

					}
					treeitem.setIcon(image);
					if("pm".equals(fromtype))///**岗位素质模型中调用，不显示虚拟机构*/
						treeitem.setAction("/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&amp;a_code="+codesetid+codeitemid);
					else if ("javascript:void(0)".equals(action))
						treeitem.setAction(action);
					else if ("duty".equals(treetype))
							if("vorg".equalsIgnoreCase(org_type)&&(virtualOrgSet!=null&&!"".equals(virtualOrgSet))){
								if("true".equals(isRecordEntry))
									treeitem.setAction("../../module/org/virtualorg/VirtualRecordEntry.html?br_query=link&code="+codeitemid);
								else
									treeitem.setAction("../../module/org/VirtualMember.html?b_query=link&code="+codeitemid);
							}else
								treeitem.setAction("showerrorinfo.do?b_search=link&amp;orgtype="
										+ org_type
										+ "&amp;code="
										+ codeitemid
										+ "&amp;kind=2"
										+ "&amp;backdate="
										+ backdate
										+ "&amp;query=&amp;idordesc=");
					else{
						//查看虚拟组织成员
							if("vorg".equalsIgnoreCase(org_type)&&(virtualOrgSet!=null&&!"".equals(virtualOrgSet))){
								if("entry".equals(isRecordEntry))
									treeitem.setAction("../../module/org/virtualorg/VirtualRecordEntry.html?br_query=link&code="+codeitemid);
								else if("browse".equals(isRecordEntry))
									treeitem.setAction("../../module/org/VirtualMember.html?b_query=link&code="+codeitemid);
								else
									treeitem.setAction(action + "?b_search=link&amp;code="
											+ codeitemid + "&amp;kind=2&amp;orgtype="
											+ org_type + "" + "&amp;backdate=" + backdate
											+ "&amp;query=&amp;idordesc=");
							}else
								treeitem.setAction(action + "?b_search=link&amp;code="
								+ codeitemid + "&amp;kind=2&amp;orgtype="
								+ org_type + "" + "&amp;backdate=" + backdate
								+ "&amp;query=&amp;idordesc=");
					}
					strXml.append(treeitem.toChildNodeJS() + "\n");
				} else if (rec.get("codesetid") != null&& "UM".equals(rec.get("codesetid"))&& !"noum".equals(treetype)) {
					String childid = rec.get("childid") != null ? rec.get("childid").toString() : "";
					if (!codeitemid.equalsIgnoreCase(childid)){
						
//						String sql = "select '1' from organization where parentid='"
//							+codeitemid+"'"+" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date ";
//						sql+="union select '1' from vorganization where parentid='"
//							+codeitemid+"'"+" and " + Sql_switcher.dateValue(backdate) + " between start_date and end_date ";
//						if (loadtype.equalsIgnoreCase("1") || "org".equals(treetype))
//							sql = sql +" and codesetid='UM'";
//
//						List arr = ExecuteSQL.executeMyQuery(sql);
						if(parentidMap==null){
							parentidMap=this.mapOrgParentid(backdate,loadtype,treetype,codeitemidList);
						}
						if(parentidMap.containsKey(codeitemid)&&parentidMap.get(codeitemid)!=0)
							treeitem.setXml("/common/vorg/loadtree?params=child&amp;orgtype="
											+ org_type
											+ "&amp;treetype="
											+ treetype
											+ "&amp;parentid="
											+ codeitemid
											+ "&amp;loadtype="
											+ loadtype
											+ "&amp;kind=1&amp;issuperuser="
											+ issuperuser
											+ "&amp;manageprive="
											+ manageprive
											+ "&amp;action="
											+ action
											+ "&amp;target="
											+ target
											+ "&amp;backdate=" + backdate
											+"&amp;fromtype="+fromtype
											+"&amp;isRecordEntry="+isRecordEntry);
						
					}
					if ("vorg".equals(org_type)/* &&!orgtype.equals("vorg") */) {
						if(isOrgMapAction(action) &&"1".equals(view_chart)){
							image = "/images/vdept_h.gif";
						}else
							image = "/images/vdept.gif";
					} else {
						if (sdf.parse(sdf.format(new Date())).compareTo(
								sdf.parse((String) rec.get("end_date"))) <= 0) {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								image = "/images/dept_h.gif";
							}else
								image = "/images/dept.gif";
						} else {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								image = "/images/b_dept_h.gif";
							}else
								image = "/images/b_dept.gif";
						}
					}
					treeitem.setIcon(image);
					if("pm".equals(fromtype))
						treeitem.setAction("/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&amp;a_code="+codesetid+codeitemid);
					else if ("javascript:void(0)".equals(action))
						treeitem.setAction(action);
					else if ("duty".equals(treetype))
						if("vorg".equalsIgnoreCase(org_type)&&(virtualOrgSet!=null&&!"".equals(virtualOrgSet))){
							if("entry".equals(isRecordEntry))
								treeitem.setAction("../../module/org/virtualorg/VirtualRecordEntry.html?br_query=link&code="+codeitemid);
							else if("browse".equals(isRecordEntry))
								treeitem.setAction("../../module/org/VirtualMember.html?b_query=link&code="+codeitemid);
							else
								treeitem.setAction("showerrorinfo.do?b_search=link&amp;orgtype="
										+ org_type
										+ "&amp;code="
										+ codeitemid
										+ "&amp;kind=1"
										+ "&amp;backdate="
										+ backdate
										+ "&amp;query=&amp;idordesc=");
						}else
						treeitem.setAction("showerrorinfo.do?b_search=link&amp;orgtype="
										+ org_type
										+ "&amp;code="
										+ codeitemid
										+ "&amp;kind=1"
										+ "&amp;backdate="
										+ backdate
										+ "&amp;query=&amp;idordesc=");
					else
						if("vorg".equalsIgnoreCase(org_type)&&(virtualOrgSet!=null&&!"".equals(virtualOrgSet))){
							if("entry".equals(isRecordEntry))
								treeitem.setAction("../../module/org/virtualorg/VirtualRecordEntry.html?br_query=link&code="+codeitemid);
							else if("browse".equals(isRecordEntry))
								treeitem.setAction("../../module/org/VirtualMember.html?b_query=link&code="+codeitemid);
							else
								treeitem.setAction(action + "?b_search=link&amp;code="
										+ codeitemid + "&amp;kind=1&amp;orgtype="
										+ org_type + "" + "&amp;backdate=" + backdate
										+ "&amp;query=&amp;idordesc=");
						}else
						treeitem.setAction(action + "?b_search=link&amp;code="
								+ codeitemid + "&amp;kind=1&amp;orgtype="
								+ org_type + "" + "&amp;backdate=" + backdate
								+ "&amp;query=&amp;idordesc=");
					strXml.append(treeitem.toChildNodeJS() + "\n");
				} else if (rec.get("codesetid") != null&& "@K".equals(rec.get("codesetid"))&& (!"org".equals(treetype) && !"noum".equals(treetype))) {
					// treeitem.setXml("/common/vorg/loadtree?params=child&amp;treetype="+
					// treetype + "&amp;parentid=" + codeitemid +
					// "&amp;kind=0&amp;issuperuser=" + issuperuser +
					// "&amp;manageprive=" + manageprive + "&amp;action=" +
					// action + "&amp;target=" + target);
					if ("vorg".equals(org_type)/* &&!orgtype.equals("vorg") */) {
						if(isOrgMapAction(action) &&"1".equals(view_chart)){
							treeitem.setIcon("/images/vpos_l_h.gif");
						}else
							treeitem.setIcon("/images/vpos_l.gif");
					} else {
						if (sdf.parse(sdf.format(new Date())).compareTo(sdf.parse((String) rec.get("end_date"))) <= 0) {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								treeitem.setIcon("/images/pos_l_h.gif");
							}else
								treeitem.setIcon("/images/pos_l.gif");
						} else {
							if(isOrgMapAction(action) &&"1".equals(view_chart)){
								treeitem.setIcon("/images/b_pos_1_h.gif");
							}else
								treeitem.setIcon("/images/b_pos_1.gif");
						}
					}
                    if("pm".equals(fromtype))
                    	treeitem.setAction("/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&amp;a_code="+codesetid+codeitemid);
                    else if ("javascript:void(0)".equals(action))
						treeitem.setAction(action);
                    else if("vorg".equalsIgnoreCase(org_type)&&(virtualOrgSet!=null&&!"".equals(virtualOrgSet))){//修改点击岗位链接跳转虚拟机构查询编辑界面
                    	if("entry".equals(isRecordEntry))
							treeitem.setAction("../../module/org/virtualorg/VirtualRecordEntry.html?br_query=link&code="+codeitemid);
						else if("browse".equals(isRecordEntry))
							treeitem.setAction("../../module/org/VirtualMember.html?b_query=link&code="+codeitemid);
						else
							treeitem.setAction(action
									+ "?b_search=link&amp;orgtype=" + org_type
									+ "&amp;code=" + codeitemid + "&amp;kind=0"
									+ "&amp;backdate=" + backdate
									+ "&amp;query=&amp;idordesc=");
                    }else
						treeitem.setAction(action
								+ "?b_search=link&amp;orgtype=" + org_type
								+ "&amp;code=" + codeitemid + "&amp;kind=0"
								+ "&amp;backdate=" + backdate
								+ "&amp;query=&amp;idordesc=");
					strXml.append(treeitem.toChildNodeJS() + "\n");
				}
			}
			strXml.append("</TreeNode>\n");
			return strXml.toString();
		}
		return strXml.toString();

	}

	//zxj add 20140603 组织机构图中隐藏机构在树上需要有特殊标记
	private boolean isOrgMapAction(String action) {
	    return "showorgmap.do".equalsIgnoreCase(action) 
	        || "showyFilesOrgMap.do".equalsIgnoreCase(action);
	}
	
	/*
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	private String getLoadTreeQueryString(String params, String isSuperuser,
			String parentid, String managepriv, String backdate,
			UserView userView,String busiPriv, String droit, String fromtype) {
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'org' as orgtype,a0000,end_date,view_chart ");
		strsql.append(" FROM organization ");
		if (params != null && "root".equals(params)) {
			String busi = this.getBusi_org_dept(busiPriv, userView);
			if((droit==null||(droit != null && !"0".equals(droit)))&&busi.length()>2){
				strsql.append(" WHERE (1=2");
				String[] org_depts = busi.split("`");
				for(int i=0;i<org_depts.length;i++){
					String org_dept = org_depts[i];
					if(org_dept.length()>2){
						strsql.append(" or (codesetid='"+org_dept.substring(0,2)+"' and codeitemid='"+org_dept.substring(2)+"')");
					}else{
						strsql.append(" or (parentid=codeitemid)");
					}
				}
				strsql.append(")");//add by wangchaoqun on 2014-10-23 org_dept数据多时，几个并列的条件并没有被放在一起
			}else if (droit != null && "0".equals(droit)) {
				strsql.append(" WHERE codeitemid=parentid ");
			} else {
				if ("1".equals(isSuperuser)) {
					strsql.append(" WHERE codeitemid=parentid ");
				} else {

					if ((managepriv != null && managepriv.trim().length() == 2)) {
						strsql.append(" WHERE codeitemid=parentid ");
					} else if ((managepriv != null && managepriv.trim().length() >= 2)) {
						managepriv = managepriv.substring(2, managepriv.length());
						strsql.append(" WHERE codeitemid='");
						strsql.append(managepriv);
						strsql.append("'");
					} else {
						strsql.append(" WHERE 1=2");
					}
				}
			}

		} else {
			strsql.append(" WHERE parentid='");
			strsql.append(parentid);
			strsql.append("'");
			strsql.append(" AND codeitemid<>parentid ");
		}
		strsql.append(" and " + Sql_switcher.dateValue(backdate)
				+ " between start_date and end_date ");
		/**岗位素质模型中调用，不显示虚拟机构*/
		if(!"pm".equalsIgnoreCase(fromtype))
		{
	    	strsql.append(" union ");
    		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype,a0000,end_date,view_chart ");
	    	strsql.append(" FROM vorganization ");
	    	if (params != null && "root".equals(params)) {

	    		String busi = this.getBusi_org_dept(busiPriv, userView);
		    	if(busi.length()>2){
		    		strsql.append(" WHERE 1=2");
		    		String[] org_depts = busi.split("`");
		    		for(int i=0;i<org_depts.length;i++){
		    			String org_dept = org_depts[i];
			    		if(org_dept.length()>2){
			    			strsql.append(" or (codesetid='"+org_dept.substring(0,2)+"' and codeitemid='"+org_dept.substring(2)+"')");
			    		}
		    		}
	    		}else if (droit != null && "0".equals(droit)) {
	    			strsql.append(" WHERE codeitemid=parentid ");
	    		} else {
	    			if ("1".equals(isSuperuser)) {
		    			strsql.append(" WHERE codeitemid=parentid ");
		    		} else {

		    			if ((managepriv != null && managepriv.trim().length() == 2)) {
		     				strsql.append(" WHERE codeitemid=parentid ");
		    			} else if ((managepriv != null && managepriv.trim().length() >= 2)) {
		    				managepriv = managepriv.substring(2, managepriv.length());
		    				strsql.append(" WHERE codeitemid='");
			    			strsql.append(managepriv);
			    			strsql.append("'");
			    		} else {
			    			strsql.append(" WHERE 1=2");
		    			}
	    			}
	    		}

	    	} else {
	     		strsql.append(" WHERE parentid='");
	    		strsql.append(parentid);
	    		strsql.append("'");
	     		strsql.append(" AND codeitemid<>parentid ");
	    	}
    		strsql.append(" and " + Sql_switcher.dateValue(backdate)
	     			+ " between start_date and end_date ");
		}
		//机构名称优先显示归档的历史数据的名称
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT org.codesetid,case when " + Sql_switcher.isnull("his.codeitemdesc", "'#'"));
		sql.append("<>'#' then his.codeitemdesc else org.codeitemdesc end codeitemdesc,");
		sql.append("org.codeitemid,org.parentid,org.childid,org.orgtype,org.a0000,end_date,view_chart from (");
		sql.append(strsql);
		sql.append(") org left join (");
		sql.append("select * from hr_org_history where catalog_id = (");
		sql.append("select MIN(catalog_id) from hr_org_history where catalog_id > '" + backdate.replace("-", "") + "')) his");
		sql.append(" on org.codeitemid=his.codeitemid and org.codesetid=his.codesetid");
		sql.append(" ORDER BY org.a0000,org.codeitemid,org.orgtype");
		return sql.toString();
	}

	/**
	 * 虚拟表结构SQL
	 * 
	 * @param params
	 * @param issuperuser
	 * @param parentid
	 * @param manageprive
	 * @return
	 */
	private String getLoadVorgTreeQueryString(String manageprive,
			String parentid) {
		StringBuffer strsql = new StringBuffer();
		strsql.append("SELECT codesetid,codeitemdesc,codeitemid,parentid,childid,'vorg' as orgtype ");
		strsql.append(" FROM vorganization ");
		if (manageprive != null && manageprive.length() > 1) {
			manageprive = manageprive.substring(2, manageprive.length());
			strsql.append(" WHERE ");
			strsql.append(" parentid='" + parentid + "'");
			strsql.append(" order by A0000");

		} else {
			strsql.append(" WHERE 1=2");
		}
		return strsql.toString();
	}

	private void getVorgTreeXml(List rs, String manageprive, String parentid) {
		List v_rs = ExecuteSQL.executeMyQuery(getLoadVorgTreeQueryString(
				manageprive, parentid));
		if (!v_rs.isEmpty()) {
			for (int i = 0; i < v_rs.size(); i++) {
				DynaBean rec = (DynaBean) v_rs.get(i);
				rs.add(rec);
			}
		}
	}

	private String getBusi_org_dept(String busiPriv, UserView userView) {
		String busi = "";
		if ("1".equals(busiPriv)) {
			int status = userView.getStatus();
			if (!userView.isSuper_admin() /*&& 0 == status*/) {// 非超级用户组下业务用户
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					/*conn = com.hrms.frame.utility.AdminDb.getConnection();
					ContentDAO dao = new ContentDAO(conn);
					String sql = "select busi_org_dept from operuser where username='"
							+ userView.getUserName() + "'";
					rs = dao.search(sql);
					while (rs.next()) {
						busi_org_dept = Sql_switcher.readMemo(rs,
								"busi_org_dept");
					}*/
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						/*String str[] = busi_org_dept.split("\\|");
						for (int i = 0; i < str.length; i++) {// 1,UNxxx`UM9191`
							String tmp = str[i];
							String ts[] = tmp.split(",");
							if (ts.length == 2) {
								if("4".equals(ts[0])){
								busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(ts[1]);
									break;
								}
							}
						}*/
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
			}
		}
		return busi;
	}
	/**
	 * 获取所示机构是否存在下级机构
	 * @param backdate
	 * @param loadtype
	 * @param treetype
	 * @param codeitemidList 需要获取下级机构的机构id
	 * @return
	 * @throws GeneralException
	 * @author ZhangHua
	 * @date 17:34 2018/12/13
	 */
	private HashMap<String,Integer> mapOrgParentid(String backdate,String loadtype,String treetype,ArrayList<String> codeitemidList) throws GeneralException {

		HashMap<String,Integer> map=new HashMap<String, Integer>();
		List list=null;
		try{
			StringBuffer strSql=new StringBuffer();

			String whl=" and " + Sql_switcher.dateValue(backdate)   + " between start_date and end_date ";
			if ("2".equalsIgnoreCase(loadtype)) {
				whl=whl+" and codesetid in ('UN')";
			}
			else if ("1".equalsIgnoreCase(loadtype)) {
				whl=whl+" and codesetid in ('UN','UM')";
			}
			else {
				if ("org".equals(treetype)){
					whl=whl+" and codesetid in ('UN','UM')";
				}
			}
			StringBuffer strCode=new StringBuffer();
			strCode.append("(");
			for (String id : codeitemidList) {
				strCode.append("'").append(id).append("',");
			}
			strCode.deleteCharAt(strCode.length()-1);
			strCode.append(")");
			strSql.append(" select parentid ,sum(num) as num from (");
			strSql.append(" select parentid,count(*) as num from organization where ");
			strSql.append(" parentid in ").append(strCode);
			strSql.append(whl).append(" GROUP BY parentid ");
			strSql.append(" union all  ");
			strSql.append(" select parentid,count(*) as num from vorganization where  ");
			strSql.append(" parentid in ").append(strCode);
			strSql.append(whl).append(" GROUP BY parentid ");
			strSql.append(") t group by parentid");

			list=ExecuteSQL.executeMyQuery(strSql.toString());
			if (!list.isEmpty()) {
				for (int i = 0; i < list.size(); i++) {
					DynaBean rec = (DynaBean) list.get(i);
					String id=(String) rec.get("parentid");
					Double z=Double.parseDouble((String) rec.get("num"));
					map.put(id,z.intValue());
				}
			}


		}catch (Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return map;

	}

}
