package com.hjsj.hrms.interfaces.sys;

import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:HJHJ
 * </p>
 * <p>
 * Create time:${date}:${time}
 * </p>
 * 
 * @author lilinbing
 * @version 4.0
 */
public class CteateProjectXml {
	/**
	 * 创建代码树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outCodeTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			ArrayList list = (ArrayList) DataDictionary.getFieldSetList(
					Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
			for (int i = 0; i < list.size(); i++) {
				FieldSet fieldset = (FieldSet) list.get(i);

				Element child = new Element("TreeNode");
				String fieldsetid = fieldset.getFieldsetid();
				if (fieldsetid == null)
					fieldsetid = "";
				fieldsetid = fieldsetid.trim();

				child.setAttribute("id", fieldsetid);
				child.setAttribute("text", fieldset.getFieldsetdesc());
				child.setAttribute("title", fieldset.getFieldsetdesc());
				child.setAttribute("xml",
						"/org/autostatic/mainp/get_project_tree.jsp?fieldsetid="
								+ fieldsetid);
				child.setAttribute("icon", "/images/pos_l.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outProjectTree(String fieldsetid) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			ArrayList list = (ArrayList) DataDictionary.getFieldList(
					fieldsetid, Constant.USED_FIELD_SET);
			for (int i = 0; i < list.size(); i++) {
				FieldItem field = (FieldItem) list.get(i);

				Element child = new Element("TreeNode");
				String fielditemid = field.getItemid();
				fielditemid = fielditemid != null && fielditemid.length() > 0 ? fielditemid
						: "";
				fielditemid = fielditemid.trim();

				String codesetid = field.getCodesetid();
				codesetid = codesetid != null && codesetid.length() > 0 ? codesetid
						: "";
				codesetid = codesetid.trim();

				child.setAttribute("id", codesetid + "::" + fielditemid);
				child.setAttribute("text", field.getItemdesc());
				child.setAttribute("title", field.getItemdesc());
				child.setAttribute("xml",
						"/org/autostatic/mainp/get_project_tree.jsp?fielditemid="
								+ fielditemid);
				if ("UN".equalsIgnoreCase(field.getItemtype()))
					child.setAttribute("icon", "/images/unit.gif");
				else if ("UM".equalsIgnoreCase(field.getItemtype()))
					child.setAttribute("icon", "/images/dept.gif");
				else if ("@K".equalsIgnoreCase(field.getItemtype()))
					child.setAttribute("icon", "/images/pos_l.gif");
				else
					child.setAttribute("icon", "/images/table.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建公司树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outViewAreaTree(String itemid, UserView userView)
			throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer();
		Connection conn = null;
		ResultSet rset = null;
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);

		try {
			conn = AdminDb.getConnection();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate =sdf.format(new Date());
			sqlstr.append("select codesetid,codeitemid,codeitemdesc from organization where "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and (codesetid='UN' or codesetid='UM')");
			if ("root".equalsIgnoreCase(itemid)) {
				if (userView.isSuper_admin()&& "1".equals(userView.getGroupId())) {
					sqlstr.append(" AND codeitemid=parentid ");
				} 
				else 
				{
					/**
					 * cmq changed at 20121003 for 单位和岗位的权限范围控制规则
					 * 业务范围-操作单位-人员范围
					 */
					/*
					String managepriv = userView.getManagePrivCode()+ userView.getManagePrivCodeValue();
					if ((managepriv != null && managepriv.trim().length() == 2)) {
						sqlstr.append(" AND codeitemid=parentid ");
					} 
					else if ((managepriv != null && managepriv.trim().length() >= 2))
					{
						managepriv = managepriv.substring(2, managepriv.length());
						sqlstr.append(" AND codeitemid='");
						sqlstr.append(managepriv);
						sqlstr.append("'");
					} else {
						sqlstr.append(" AND 1=2");
					}
					*/
					String managepriv =userView.getUnitIdByBusi("4");//userView.getUnitPosWhereByPriv("codeitemid",1);
					if(!"".equals(managepriv) && managepriv.length()>2)
					{
						boolean super_user = false;
						String[] unitIds = managepriv.split("`");
						managepriv="";
						for(int i=0;i<unitIds.length;i++){
							String orgid = unitIds[i];
							if(orgid == null)
								continue;
							if("UN".equalsIgnoreCase(orgid)){
								super_user = true;
								managepriv =" AND codeitemid=parentid ";
								break;
							}
							for(int k=0;k<unitIds.length;k++){
								if(unitIds[k]!=null && !orgid.equals(unitIds[k]) && orgid.substring(2).indexOf(unitIds[k].substring(2))==0){
									unitIds[i]=null;
									break;
								}
							}
							if(unitIds[i]!=null)
								managepriv +="'"+unitIds[i].substring(2)+"',";
						}
					
					//managepriv =userView.getUnitPosWhereByPriv("codeitemid",1);
					    if(super_user)
					    	sqlstr.append(managepriv);
					    else
						sqlstr.append(" and codeitemid in ("+managepriv.substring(0, managepriv.length()-1)+") ");
					}
					else
						sqlstr.append(" AND 1=2");				
				}
			} else {
				sqlstr.append(" AND parentid='");
				sqlstr.append(itemid);
				sqlstr.append("'");
				sqlstr.append(" AND codeitemid<>parentid ");
			}
			sqlstr.append(" order by A0000,codeitemid");

			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(sqlstr.toString());
			while (rset.next()) {
				Element child = new Element("TreeNode");
				String codeitemid = rset.getString("codeitemid");
				String codeitemdesc = rset.getString("codeitemdesc");
				String codesetid = rset.getString("codesetid");

				child.setAttribute("id", codeitemid);
				child.setAttribute("text", codeitemdesc);
				child.setAttribute("title", codeitemdesc);
				child.setAttribute("xml",
						"/org/autostatic/confset/get_viewarea_tree.jsp?codeitemid="
								+ codeitemid);
				child.setAttribute("icon", "/images/unit.gif");

				if ("UN".equalsIgnoreCase(codesetid))
					child.setAttribute("icon", "/images/unit.gif");
				else if ("UM".equalsIgnoreCase(codesetid))
					child.setAttribute("icon", "/images/dept.gif");
				else if ("@K".equalsIgnoreCase(codesetid))
					child.setAttribute("icon", "/images/pos_l.gif");
				else
					child.setAttribute("icon", "/images/table.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ee) {
				ee.printStackTrace();
			}

		}
		return xmls.toString();
	}

	/**
	 * 创建代码类树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String objectCodeTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		Connection conn = null;

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sqlstr = "select codesetid,codesetdesc,maxlength from codeset";
			ArrayList dylist = dao.searchDynaList(sqlstr);
			;
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				Element child = new Element("TreeNode");
				String codesetid = dynabean.get("codesetid").toString();
				String codesetdesc = dynabean.get("codesetdesc").toString();
				String maxlength = dynabean.get("maxlength").toString();

				child.setAttribute("id", codesetid + "-" + maxlength);
				child.setAttribute("text", codesetdesc);
				child.setAttribute("title", codesetdesc);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/pos_l.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ee) {
				ee.printStackTrace();
			}

		}
		return xmls.toString();
	}

	/**
	 * 公司部门等级树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String levelTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer();
		Connection conn =null;
		ResultSet rset = null;
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);

		try {
			conn = AdminDb.getConnection();
			sqlstr
					.append("select grade from organization where codesetid ='UN' GROUP BY grade");
			String[] arr = {
					ResourceFactory.getProperty("hrms.interfaces.sys.level1"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level2"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level3"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level4"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level5"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level6"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level7"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level8"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level9"),
					ResourceFactory.getProperty("hrms.interfaces.sys.level10") };
			ContentDAO dao = new ContentDAO(conn);
			rset = dao.search(sqlstr.toString());
			int i = 0;
			while (rset.next()) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", rset.getString("grade"));
				child.setAttribute("text", arr[i]);
				child.setAttribute("title", arr[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/unit.gif");
				root.addContent(child);
				i++;
			}

			Element child = new Element("TreeNode");

			child.setAttribute("id", "0");
			child.setAttribute("text", "全部");
			child.setAttribute("title", "全部");
			child.setAttribute("xml", "aa");
			child.setAttribute("icon", "/images/unit.gif");
			root.addContent(child);

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		} finally {
			try {
				if (rset != null) {
					rset.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ee) {
				ee.printStackTrace();
			}

		}
		return xmls.toString();
	}

	/**
	 * 创建函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outFunctionTree(String checktemp,String mode) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "0", "1", "2", "3", "4", "10","11", "5", "6", "7", "8", "9" };
		String[] text = { ResourceFactory.getProperty("kq.formula.number"),
				ResourceFactory.getProperty("org.maip.str"),
				ResourceFactory.getProperty("kq.formula.date"),
				ResourceFactory.getProperty("kq.wizard.switch"),
				ResourceFactory.getProperty("org.maip.volatile"),
				ResourceFactory.getProperty("hrms.interfaces.sys.function"),
				ResourceFactory.getProperty("kq.wizard.kqfunc"),
				ResourceFactory.getProperty("kq.wizard.chlang"),
				ResourceFactory.getProperty("kq.wizard.boolen"),
				ResourceFactory.getProperty("kq.wizard.number"),
				ResourceFactory.getProperty("kq.wizard.option"),
				ResourceFactory.getProperty("kq.wizard.other") };
		String[] images = { "/images/bm.gif", "/images/bm2.gif",
				"/images/bm1.gif", "/images/bm3.gif", "/images/bm4.gif",
				"/images/bm10.gif","/images/bm10.gif", "/images/bm5.gif", "/images/bm6.gif",
				"/images/bm7.gif", "/images/bm8.gif", "/images/bm9.gif" };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 12; i++) {
				if (i == 5) {
					if (!"salary".equalsIgnoreCase(checktemp)
							&& !"temp".equalsIgnoreCase(checktemp)
							&& !"sjld".equalsIgnoreCase(checktemp)//增加预算公式的工资函数
							&& !"ysgs".equalsIgnoreCase(checktemp)) {
						continue;
					}
				}
				if (i == 6) {
					if(!"rsyd_jsgs".equalsIgnoreCase(mode))
						continue;
				}
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml",
						"/org/autostatic/mainp/get_function_tree.jsp?id="
								+ id[i]+"&mode="+mode+"&checktemp="+checktemp);
				child.setAttribute("icon", images[i]);
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outMainpTree(String treeid,String checktemp,String mode) throws GeneralException {
		int i = Integer.parseInt(treeid);
		String xmls = "";
		switch (i) {
		case 0:
			xmls = outNumericalTree();
			break;
		case 1:
			xmls = outStrTree(checktemp,mode);
			break;
		case 2:
			xmls = outDateTree(checktemp);
			break;
		case 3:
			xmls = outTransferTree();
			break;
		case 4:
			xmls = outVolatileTree(checktemp,mode);
			break;
		case 5:
			xmls = outConstantsTree();
			break;
		case 6:
			xmls = outLogicTree();
			break;
		case 7:
			xmls = outOperatorsTree();
			break;
		case 8:
			xmls = outRelationsTree();
			break;
		case 9:
			xmls = outOtherTree();
			break;
		case 10:
			xmls = outSalaryTree(checktemp,mode);
			break;
		case 11:
			xmls = outKqTree(checktemp,mode);
			break;
		}

		return xmls;
	}

	/**
	 * 创建数值函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outNumericalTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "N_num0", "N_num1_4", "N_num2_2", "N_num3", "N_num4_2",
				"N_num5", "NN_num6" };
		String[] text = {
				ResourceFactory.getProperty("kq.formula.int") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ")",
				ResourceFactory.getProperty("org.maip.remainder") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ","
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ")",
				ResourceFactory.getProperty("kq.wizard.round") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ","
						+ ResourceFactory.getProperty("kq.wizard.integer")
						+ ")",
				ResourceFactory.getProperty("kq.wizard.ssqr") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ")",
				ResourceFactory.getProperty("kq.wizard.ffjy") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + "[,"+ResourceFactory.getProperty("kq.wizard.jycs")+"]"+")",
				ResourceFactory.getProperty("kq.wizard.ffjj") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ")",
				ResourceFactory.getProperty("kq.wizard.mi") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 7; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建字符串函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outStrTree(String checktemp,String mode) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String id2="";
		String text2 ="";
		if(("salary".equalsIgnoreCase(checktemp)
				|| "temp".equalsIgnoreCase(checktemp))
				&&mode!=null&&mode.length()>0){
			 id2 = "A_str0,A_str1,A_str2,A_str3_2_2,A_str4,A_str5_2,A_str6_2,A_vol9_6_2_10_2";
		}else{
		 id2= "A_str0,A_str1,A_str2,A_str3_2_2,A_str4,A_str5_2,A_str6_2";
		}
		String id[] = id2.split(",");
		if(mode!=null&&mode.length()>0){
			 text2 = ""+
			ResourceFactory.getProperty("kq.wizard.qnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.lnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.rnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.zbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")@@@"+
			ResourceFactory.getProperty("kq.wizard.bunchl") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.lbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")@@@"+
			ResourceFactory.getProperty("kq.wizard.rbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")@@@"+
			ResourceFactory.getProperty("org.maip.sequence.number.value");
		}else{
			 text2 = ""+
			ResourceFactory.getProperty("kq.wizard.qnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.lnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.rnull") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.zbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")@@@"+
			ResourceFactory.getProperty("kq.wizard.bunchl") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")@@@"+
			ResourceFactory.getProperty("kq.wizard.lbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")@@@"+
			ResourceFactory.getProperty("kq.wizard.rbunch") + "("
					+ ResourceFactory.getProperty("kq.wizard.zfbd") + ","
					+ ResourceFactory.getProperty("kq.wizard.integer")
					+ ")";
		}
		String text[] =text2.split("@@@");

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < id.length; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i].trim());
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm2.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建日期函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outDateTree(String checktemp ) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String [] id;
		String [] text;
		if("jixiaoguanli".equals(checktemp)){
			String[] id2 = { "D_data0", "D_data1", "D_data2", "D_data3", "D_data4",
					"D_data5", "DD_data6", "DD_data7", "DD_data8", "DD_data9",
					"DD_data10", "DD_data11", "D_data12", "D_data13", "D_data14",
					"D_data15_1", "D_data16_1", "D_data17_1", "D_data18_1",
					"D_data19_1", "D_data20_2", "D_data21_2", "D_data22_2",
					"D_data23_2", "D_data24_2", "DD_data25","D_data26_1_7","D_data27_3"};

			String[] text2 = {
					ResourceFactory.getProperty("kq.wizard.year") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.month") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.day") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.quarter") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.week") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.weeks") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.today"),
					ResourceFactory.getProperty("kq.wizard.bweek"),
					ResourceFactory.getProperty("kq.wizard.bmonth"),
					ResourceFactory.getProperty("kq.wizard.bquarter"),
					ResourceFactory.getProperty("kq.wizard.byear"),
					ResourceFactory.getProperty("kq.wizard.edate"),
					ResourceFactory.getProperty("kq.wizard.age") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.gage") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.tmonth") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.years") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.months") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.days") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.quarters") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.weekss") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.ayear") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.amonth") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aday") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aquarter") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aweek") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("gz.columns.a00z0")
							+ "()" ,
					ResourceFactory.getProperty("kq.date.work")+"("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2[,"
							+ResourceFactory.getProperty("org.maip.hdaylogo.yes")+"|"
							+ResourceFactory.getProperty("org.maip.hdaylogo.no")+"])",
					ResourceFactory.getProperty("kq.wizard.stas.month")+"("
							+ResourceFactory.getProperty("kq.wizard.date.item")+","
							+ResourceFactory.getProperty("kq.wizard.cond.exp")+")",
					ResourceFactory.getProperty("kq.wizard.stas.time")+"("
							+ResourceFactory.getProperty("kq.card.filtrate.start")+","
							+ResourceFactory.getProperty("kq.card.filtrate.end")+","
							+ResourceFactory.getProperty("kq.wizard.term")+")"
							};
			id=id2;
			text=text2;

		}else{
			String[] id2 = { "D_data0", "D_data1", "D_data2", "D_data3", "D_data4",
					"D_data5", "DD_data6", "DD_data7", "DD_data8", "DD_data9",
					"DD_data10", "DD_data11", "D_data12", "D_data13", "D_data14",
					"D_data15_1", "D_data16_1", "D_data17_1", "D_data18_1",
					"D_data19_1", "D_data20_2", "D_data21_2", "D_data22_2",
					"D_data23_2", "D_data24_2", "DD_data25","D_data26_1_7","D_data27_3","D_data28_1_10_10"};

			String[] text2 = {
					ResourceFactory.getProperty("kq.wizard.year") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.month") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.day") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.quarter") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.week") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.weeks") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.today"),
					ResourceFactory.getProperty("kq.wizard.bweek"),
					ResourceFactory.getProperty("kq.wizard.bmonth"),
					ResourceFactory.getProperty("kq.wizard.bquarter"),
					ResourceFactory.getProperty("kq.wizard.byear"),
					ResourceFactory.getProperty("kq.wizard.edate"),
					ResourceFactory.getProperty("kq.wizard.age") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.gage") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.tmonth") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
					ResourceFactory.getProperty("kq.wizard.years") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.months") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.days") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.quarters") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.weekss") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2)",
					ResourceFactory.getProperty("kq.wizard.ayear") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.amonth") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aday") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aquarter") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("kq.wizard.aweek") + "("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + ","
							+ ResourceFactory.getProperty("kq.wizard.integer")
							+ ")",
					ResourceFactory.getProperty("gz.columns.a00z0")
							+ "()" ,
					ResourceFactory.getProperty("kq.date.work")+"("
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "1,"
							+ ResourceFactory.getProperty("kq.wizard.riqi") + "2[,"
							+ResourceFactory.getProperty("org.maip.hdaylogo.yes")+"|"
							+ResourceFactory.getProperty("org.maip.hdaylogo.no")+"])",
					ResourceFactory.getProperty("kq.wizard.stas.month")+"("
							+ResourceFactory.getProperty("kq.wizard.date.item")+","
							+ResourceFactory.getProperty("kq.wizard.cond.exp")+")",
					ResourceFactory.getProperty("kq.wizard.stas.time")+"("
							+ResourceFactory.getProperty("kq.card.filtrate.start")+","
							+ResourceFactory.getProperty("kq.card.filtrate.end")+","
							+ResourceFactory.getProperty("kq.wizard.term")+","
							+ResourceFactory.getProperty("kq.wizard.expre")+"())"
							};
			id=id2;
			text=text2;
		}
		
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < id.length; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				if (i > 19 && i < 25 || i == 6 || i == 11) {
					child.setAttribute("icon", "/images/bm1.gif");
				} else {
					child.setAttribute("icon", "/images/bm.gif");
				}
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建转换函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outTransferTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "T_str0", "T_str1", "T_data2", "T_num3", "T_vol7_2",
				"T_code4", "T_item6_5","T_num4_2", "TT_tra5" };

		String[] text = {
				ResourceFactory.getProperty("org.maip.char.date") + "("
						+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")",
				ResourceFactory.getProperty("org.maip.char.number") + "("
						+ ResourceFactory.getProperty("kq.wizard.zfbd") + ")",
				ResourceFactory.getProperty("org.maip.date.char") + "("
						+ ResourceFactory.getProperty("label.query.day") + ")",
				ResourceFactory.getProperty("org.maip.number.char") + "("
						+ ResourceFactory.getProperty("kq.wizard.szbd") + ")",
				ResourceFactory.getProperty("kq.wizard.digital.chinese")
						+ "("
						+ ResourceFactory.getProperty("menu.field")
						+ ","
						+ ResourceFactory
								.getProperty("lable.channel_detail.params")
						+ ")",
				ResourceFactory.getProperty("org.maip.code.name") + "("
						+ ResourceFactory.getProperty("field.label") + ")",
				ResourceFactory.getProperty("org.maip.code.name") + "2("
						+ ResourceFactory.getProperty("kq.wizard.expre") + ","
						+ ResourceFactory.getProperty("kq.register.codesetid")
						+"[,"+ResourceFactory.getProperty("org.maip.layerNumber")+","+ResourceFactory.getProperty("org.maip.splitSign")+"]"
						+ ")",
				ResourceFactory.getProperty("org.maip.number.code"),
				SafeCode.encode(ResourceFactory.getProperty("org.maip.code.subscript"))};

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < id.length; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				if (i == 0) {
					child.setAttribute("icon", "/images/bm1.gif");
				} else if (i == 1) {
					child.setAttribute("icon", "/images/bm.gif");
				} else {
					child.setAttribute("icon", "/images/bm2.gif");
				}
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建类型不定函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outVolatileTree(String checktemp,String mode) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String id[];
		String text[];
		if(mode!=null&&mode.startsWith("jixiao_aoto"))
			checktemp = "jixiaoguanli";	
		if("jixiaoguanli".equals(checktemp)){
			if(mode!=null&&mode.startsWith("jixiao_aoto")){
			String[] id2 = { "V_vol6","V_vols7_2","V_vols8_9", "VV_vol0", "VV_vol1", "V_vol2_6", "V_vol3_6",
					"V_vol4_2_1", "V_vol5_3_3","V_volc1_8_9_1_1","V_vol9","V_vols6_3_11_10_10","V_vol9_100"};
			String[] text2 = {
					ResourceFactory.getProperty("kq.wizard.isnull") + "("
							+ ResourceFactory.getProperty("kq.wizard.target") + ")",
					ResourceFactory.getProperty("kq.wizard.login.name") + "(1|2)",
					ResourceFactory.getProperty("kq.wizard.self.unit"),		
					ResourceFactory.getProperty("kq.wizard.ifa"),
					ResourceFactory.getProperty("org.maip.scores"),
					ResourceFactory.getProperty("kq.wizard.max") + "(exp1,exp2)",
					ResourceFactory.getProperty("kq.wizard.min") + "(exp1,exp2)",
					ResourceFactory.getProperty("org.maip.name.subscript"),
					ResourceFactory.getProperty("org.maip.stat.name") ,
					ResourceFactory.getProperty("org.maip.cond.name"),
					ResourceFactory.getProperty("org.maip.unit.value"),
					ResourceFactory.getProperty("org.maip.exec.process"),
					ResourceFactory.getProperty("org.maip.object.subscript")
				
					
			};
			id=id2;
			text=text2;
			}else{
				String[] id2 = { "V_vol6","V_vols7_2","V_vols8_9", "VV_vol0", "VV_vol1", "V_vol2_6", "V_vol3_6",
						"V_vol4_2_1", "V_vol5_3_3","V_volc1_8_9_1_1","V_vol9","V_vols6_3_11_10_10","V_vols8_10","V_volq1"};
				String[] text2 = {
						ResourceFactory.getProperty("kq.wizard.isnull") + "("
								+ ResourceFactory.getProperty("kq.wizard.target") + ")",
						ResourceFactory.getProperty("kq.wizard.login.name") + "(1|2)",
						ResourceFactory.getProperty("kq.wizard.self.unit"),		
						ResourceFactory.getProperty("kq.wizard.ifa"),
						ResourceFactory.getProperty("org.maip.scores"),
						ResourceFactory.getProperty("kq.wizard.max") + "(exp1,exp2)",
						ResourceFactory.getProperty("kq.wizard.min") + "(exp1,exp2)",
						ResourceFactory.getProperty("org.maip.name.subscript"),
						ResourceFactory.getProperty("org.maip.stat.name") ,
						ResourceFactory.getProperty("org.maip.cond.name"),
						ResourceFactory.getProperty("org.maip.unit.value"),
						ResourceFactory.getProperty("org.maip.exec.process"),
						ResourceFactory.getProperty("org.maip.getPartTimeJobInfo"),
						ResourceFactory.getProperty("org.maip.variable.getfrom")
						
				};
				id=id2;
				text=text2;
				
			}
		}else{
			String[] id2 = { "V_vol6","V_vols7_2","V_vols8_9", "VV_vol0", "VV_vol1", "V_vol2_6", "V_vol3_6",
					"V_vol4_2_1", "V_vol5_3_3","V_vol1_8_9_1_1","V_volu9_20","V_volp7_20","V_vols6_3_11_10_10","V_vols8_10","V_vol9_3","V_volq1"};
			String[] text2 = {
					ResourceFactory.getProperty("kq.wizard.isnull") + "("
							+ ResourceFactory.getProperty("kq.wizard.target") + ")",
					ResourceFactory.getProperty("kq.wizard.login.name") + "(1|2)",
					ResourceFactory.getProperty("kq.wizard.self.unit"),		
					ResourceFactory.getProperty("kq.wizard.ifa"),
					ResourceFactory.getProperty("org.maip.scores"),
					ResourceFactory.getProperty("kq.wizard.max") + "(exp1,exp2)",
					ResourceFactory.getProperty("kq.wizard.min") + "(exp1,exp2)",
					ResourceFactory.getProperty("org.maip.name.subscript"),
					ResourceFactory.getProperty("org.maip.stat.name") ,
					ResourceFactory.getProperty("org.maip.cond.name"),
					ResourceFactory.getProperty("org.maip.unit2.value"),
					ResourceFactory.getProperty("org.maip.position.value"),
					ResourceFactory.getProperty("org.maip.exec.process"),
					ResourceFactory.getProperty("org.maip.getPartTimeJobInfo"),
					ResourceFactory.getProperty("org.maip.getParentCode"),
					ResourceFactory.getProperty("org.maip.variable.getfrom")
			};
			id=id2;
			text=text2;
		}
		
		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < id.length; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm4.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建常量函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outConstantsTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "CC_con0", "CC_con1", "CC_con2", "CC_con3", "CC_con4",
				"CC_con5" };

		String[] text = {
				ResourceFactory.getProperty("kq.wizard.true"),
				ResourceFactory.getProperty("kq.wizard.flase"),
				ResourceFactory.getProperty("kq.wizard.null") + "("
						+ ResourceFactory.getProperty("kq.wizard.riqi") + ")",
				ResourceFactory.getProperty("org.maip.dates"),
				ResourceFactory.getProperty("org.maip.months"),
				ResourceFactory.getProperty("org.maip.names") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 6; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm5.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建逻辑操作符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outLogicTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "LL_log0", "LL_log1", "LL_log2" };

		String[] text = { ResourceFactory.getProperty("kq.wizard.even"),
				ResourceFactory.getProperty("kq.wizard.and"),
				ResourceFactory.getProperty("kq.wizard.not") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 3; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm6.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建算术运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outOperatorsTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "OO_opr0", "OO_opr1", "OO_opr2", "OO_opr3", "OO_opr4",
				"OO_opr5", "OO_opr6", "OO_opr7" };

		String[] text = { ResourceFactory.getProperty("kq.wizard.add"),
				ResourceFactory.getProperty("kq.wizard.dec"),
				ResourceFactory.getProperty("kq.wizard.mul"),
				ResourceFactory.getProperty("kq.wizard.divide"),
				ResourceFactory.getProperty("kq.wizard.divs"),
				ResourceFactory.getProperty("kq.wizard.div"),
				ResourceFactory.getProperty("kq.wizard.over"),
				ResourceFactory.getProperty("kq.wizard.mod") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 8; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm7.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建关系运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outRelationsTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "RR_rel0", "RR_rel1", "RR_rel2", "RR_rel3", "RR_rel4",
				"RR_rel5", "RR_rel6", "RR_rel7" };

		String[] text = {
				"=(" + ResourceFactory.getProperty("kq.formula.equal") + ")",
				">(" + ResourceFactory.getProperty("kq.formula.over") + ")",
				">=(" + ResourceFactory.getProperty("kq.formula.overo") + ")",
				"<(" + ResourceFactory.getProperty("kq.formula.lower") + ")",
				"<=(" + ResourceFactory.getProperty("kq.formula.lowero") + ")",
				"<>(" + ResourceFactory.getProperty("org.maip.not.mean") + ")",
				"LIKE(" + ResourceFactory.getProperty("kq.wizard.contain")
						+ ")",
				"IN(" + ResourceFactory.getProperty("kq.wizard.in.contain")
						+ ")" };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 8; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm8.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建关系运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outOtherTree() throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "EE_oth0", "EE_oth1", "EE_oth2", "EE_oth3" };

		String[] text = {
				"( )" + ResourceFactory.getProperty("org.maip.brackets"),
				"[ ]" + ResourceFactory.getProperty("org.maip.bracketed"),
				"{ }" + ResourceFactory.getProperty("org.maip.big.brackets"),
				"//" + ResourceFactory.getProperty("kq.wizard.note") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 4; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm9.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}

	/**
	 * 创建工资函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outSalaryTree(String checktemp,String mode) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		
		String id2="";
		String text2 ="";
		if("sjld".equals(checktemp)&&mode!=null&&mode.length()>0){
			 id2 = "S_stan0";
			 text2=ResourceFactory.getProperty("kq.wizard.implement.standards");
		//新增预算汇总公式
		}else if("ysgs".equals(checktemp)&&mode!=null&&mode.length()>0){
			id2 = "SS_sar12";
			text2 = ResourceFactory.getProperty("gz.budget.formula.name");
		}else{
		 id2= "S_stan0,S_sthl1,S_sthl2,S_tztd1,S_item3_2_4,S_item4_2_4,S_item5_7_4_5,SS_sar11,SS_sar8,SS_sar9,SS_sar10";
		 text2 = ""+
					ResourceFactory.getProperty("kq.wizard.implement.standards")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.nearest.high")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.nearest.low")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.nearest.nearTjTd")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.previous.code")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.after.code")+"@@@"+
					ResourceFactory.getProperty("kq.wizard.code.adjustment")+"@@@"+
					ResourceFactory.getProperty("sub.calculation")+"@@@"+
					// ResourceFactory.getProperty("sub.calculation"),
					// ResourceFactory.getProperty("sub.calculation")+"2",
					ResourceFactory.getProperty("history.initial.index.value")+"@@@"+
					ResourceFactory.getProperty("historical.record.index.value")+"@@@"+
		 			ResourceFactory.getProperty("get.last.month.salary.people");
		 			 
		}
		String id[] = id2.split(",");
		
		

		String[] text =text2.split("@@@");

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < id.length; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i].trim());
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm10.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}
	
	/**
	 * 创建考勤函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public String outKqTree(String checktemp,String mode) throws GeneralException {
		StringBuffer xmls = new StringBuffer();
		String[] id = { "K_num0_21_12", "K_num1_21_12", "K_num2_21_12"};

		String[] text = {
				ResourceFactory.getProperty("kq.wizard.kxts"),
				ResourceFactory.getProperty("kq.wizard.yxts"),
				ResourceFactory.getProperty("kq.wizard.qjts") };

		Element root = new Element("TreeNode");

		root.setAttribute("id", "$$00");
		root.setAttribute("text", "root");
		root.setAttribute("title", "codeitem");
		Document myDocument = new Document(root);
		try {
			for (int i = 0; i < 3; i++) {
				Element child = new Element("TreeNode");

				child.setAttribute("id", id[i]);
				child.setAttribute("text", text[i]);
				child.setAttribute("title", text[i]);
				child.setAttribute("xml", "aa");
				child.setAttribute("icon", "/images/bm10.gif");
				root.addContent(child);
			}

			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			xmls.append(outputter.outputString(myDocument));
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return xmls.toString();
	}
	
	
	
}
