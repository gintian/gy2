package com.hjsj.hrms.utils.components.functionWizard.businessobject;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
/**
 * 
 * 项目名称：hcm7.x
 * 类名称：FunctionWizardbo 
 * 类描述： 函数向导组件展现业务类
 * 创建人：zhaoxg
 * 创建时间：Oct 22, 2015 2:06:36 PM
 * 修改人：zhaoxg
 * 修改时间：Oct 22, 2015 2:06:36 PM
 * 修改备注： 
 * @version
 */
public class FunctionWizardbo {
	
	private Connection conn=null; 
	private UserView userview;
	public  FunctionWizardbo(Connection conn,UserView userview){
		this.conn = conn; 
		this.userview=userview;
	}
	/**
	 * 
	 * @Title: outMainpTree   
	 * @Description:生成树节点
	 * @param @param treeid 节点ie
	 * @param @param opt 1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
	 * @param @param mode “xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
	 * @param @param type 临时变量 type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
	 * @param @return
	 * @param @throws GeneralException 
	 * @return ArrayList 
	 * @author:zhaoxg   
	 * @throws
	 */
	public ArrayList outMainpTree(String treeid,String opt,String mode, String type) throws GeneralException {
		int i = Integer.parseInt(treeid);
		ArrayList list = new ArrayList();
		switch (i) {
			case 0:
				list = outNumericalTree();
				break;
			case 1:
				list = outStrTree(opt,mode);
				break;
			case 2:
				list = outDateTree(opt);
				break;
			case 3:
				list = outTransferTree();
				break;
			case 4:
				list = outVolatileTree(opt,mode,type);
				break;
			case 5:
				list = outConstantsTree();
				break;
			case 6:
				list = outLogicTree();
				break;
			case 7:
				list = outOperatorsTree();
				break;
			case 8:
				list = outRelationsTree();
				break;
			case 9:
				list = outOtherTree();
				break;
			case 10:
				list = outSalaryTree(opt,mode);
				break;
			case 11:
				list = outKqTree(opt,mode);
				break;
			default:
				list = new ArrayList();
		}

		return list;
	}
	/**
	 * 创建数值函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outNumericalTree() throws GeneralException {
		ArrayList list = new ArrayList();
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

		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("desc", text[i]);
				map.put("icon", "/images/bm.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}
	/**
	 * 创建字符串函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outStrTree(String opt,String mode) throws GeneralException {
		ArrayList list = new ArrayList();
		String id2="";
		String text2 ="";
		if("2".equalsIgnoreCase(opt)&&mode!=null&&mode.length()>0&&"rsyd_jsgs".equalsIgnoreCase(mode)){
			 id2 = "A_str0,A_str1,A_str2,A_str3_2_2,A_str4,A_str5_2,A_str6_2,A_vol9_6_2_10_2";
		}else{
		 id2= "A_str0,A_str1,A_str2,A_str3_2_2,A_str4,A_str5_2,A_str6_2";
		}
		String id[] = id2.split(",");
		if(mode!=null&&mode.length()>0&&"rsyd_jsgs".equalsIgnoreCase(mode)){
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

		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm2.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建日期函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outDateTree(String opt ) throws GeneralException {
		ArrayList list = new ArrayList();
		String [] id;
		String [] text;
		if("3".equals(opt)){
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
							+ "(['A00Z0'|'A00Z2'])" ,
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
							+ "(['A00Z0'|'A00Z2'])" ,
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
		
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				if (i > 19 && i < 25 || i == 6 || i == 11) {
					map.put("icon", "/images/bm1.gif");
				} else {
					map.put("icon", "/images/bm.gif");
				}
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建转换函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outTransferTree() throws GeneralException {
		ArrayList list = new ArrayList();
		String[] id = { "T_str0_22", "T_str1", "T_data2", "T_num3", "T_vol7_2",
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
				ResourceFactory.getProperty("org.maip.code.subscript")};

		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				if (i == 0) {
					map.put("icon", "/images/bm1.gif");
				} else if (i == 1) {
					map.put("icon", "/images/bm.gif");
				} else {
					map.put("icon", "/images/bm2.gif");
				}
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建类型不定函数树
	 * @param opt //1.薪资 2.人事异动 3.绩效管理 4.招聘 5.临时变量 6.数据联动 7.考勤...
	 * @param mode /具体模块自定义标识 “xzgl_jsgs”具体功能点标识，xzgl_jsgs代表薪资计算公式调用
	 * @param type 临时变量 type：入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outVolatileTree(String opt,String mode,String type) throws GeneralException {
		ArrayList list = new ArrayList();
		String id[];
		String text[];
		if(mode!=null&&mode.startsWith("jixiao_aoto"))
			opt = "3";	
		if("2".equals(opt)){
			String[] id2 = { "V_vol6","V_vols7_2","V_vols8_9", "VV_vol0", "VV_vol1", "V_vol2_6", "V_vol3_6",
					"V_vol4_2_1", "V_vsub7_3_3", "V_vol5_3_3", "V_vol1_8_9_1_1","V_volu9_20","V_volp7_20","V_vols6_3_11_10_10","V_vols8_10","V_vol9_3","V_volq1"};
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
					ResourceFactory.getProperty("org.maip.subset.name"),
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
		}else if("3".equals(opt)){
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
						"V_vol4_2_1", "V_vol5_3_3","V_volc1_8_9_1_1","V_vol9","V_vols6_3_11_10_10","V_vols8_10"};
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
						ResourceFactory.getProperty("org.maip.getPartTimeJobInfo")
						
				};
				id=id2;
				text=text2;
			}
		}else if("4".equals(opt)){
			String[] id2 = { "V_vol6","V_vols7_2","V_vols8_9", "VV_vol0", "VV_vol1", "V_vol2_6", "V_vol3_6",
					"V_vol4_2_1", "V_vol5_3_3","V_vol1_8_9_1_1","V_volu9_20","V_volp7_20","V_vols6_3_11_10_10","V_vols8_10","V_vol9_3"};
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
					ResourceFactory.getProperty("org.maip.getParentCode")
			};
			id=id2;
			text=text2;
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

		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm4.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建常量函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outConstantsTree() throws GeneralException {
		ArrayList list = new ArrayList();
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
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm5.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建逻辑操作符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outLogicTree() throws GeneralException {
		ArrayList list = new ArrayList();
		String[] id = { "LL_log0", "LL_log1", "LL_log2" };
		String[] text = { ResourceFactory.getProperty("kq.wizard.even"),
				ResourceFactory.getProperty("kq.wizard.and"),
				ResourceFactory.getProperty("kq.wizard.not") };
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm6.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建算术运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outOperatorsTree() throws GeneralException {
		ArrayList list = new ArrayList();
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
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm7.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建关系运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outRelationsTree() throws GeneralException {
		ArrayList list = new ArrayList();
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
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm8.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建关系运算符函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outOtherTree() throws GeneralException {
		ArrayList list = new ArrayList();
		String[] id = { "EE_oth0", "EE_oth1", "EE_oth2", "EE_oth3" };

		String[] text = {
				"( )" + ResourceFactory.getProperty("org.maip.brackets"),
				"[ ]" + ResourceFactory.getProperty("org.maip.bracketed"),
				"{ }" + ResourceFactory.getProperty("org.maip.big.brackets"),
				"//" + ResourceFactory.getProperty("kq.wizard.note") };
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm9.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}

	/**
	 * 创建工资函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outSalaryTree(String opt,String mode) throws GeneralException {
		ArrayList list = new ArrayList();
		String id2="";
		String text2 ="";
		if("6".equals(opt)&&mode!=null&&mode.length()>0){
			 id2 = "S_stan0";
			 text2=ResourceFactory.getProperty("kq.wizard.implement.standards");
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
					ResourceFactory.getProperty("history.initial.index.value")+"@@@"+
					ResourceFactory.getProperty("historical.record.index.value")+"@@@"+
		 			ResourceFactory.getProperty("get.last.month.salary.people");
		 			 
		}
		if(!"5".equals(opt)) {//临时变量暂时不支持取专项附加额函数
			id2 += ",S_pert1";
			text2 += "@@@" + ResourceFactory.getProperty("label.specialAddAmount");
		}
		String id[] = id2.split(",");
		String[] text =text2.split("@@@");
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm10.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 创建考勤函数树
	 * 
	 * @return xmls
	 * @throws Exception
	 */
	public ArrayList outKqTree(String opt,String mode) throws GeneralException {
		ArrayList list = new ArrayList();
		String[] id = { "K_item0_21_12_15_16", "K_item1_21_12_16", "K_item2_21_12"};
		String[] text = {
				ResourceFactory.getProperty("kq.wizard.kxts"),
				ResourceFactory.getProperty("kq.wizard.yxts"),
				ResourceFactory.getProperty("kq.wizard.qjts") };
		try {
			HashMap map = null;
			for(int i=0;i<id.length;i++){
				map = new HashMap();
				map.put("id", id[i]);
				map.put("text", text[i]);
				map.put("icon", "/images/bm10.gif");
				list.add(map);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return list;
	}
	 /**
     * 查询薪资项目子集
     * @param salaryid 薪资id
     * @return retlist
     * @throws GeneralException
     */
	public ArrayList functionList(String salaryid){
		ArrayList fieldsetlist = new ArrayList();
		try{
			HashMap map = new HashMap();
//			map.put("id", "");
//			map.put("name", "");
//			fieldsetlist.add(map);
			ArrayList listitem= getMidVariableList(salaryid);
			HashSet   hs   =   new   HashSet();   
			if(listitem!=null){
				for(int j=0;j<listitem.size();j++){
					FieldItem item = (FieldItem)listitem.get(j);
					hs.add(item.getFieldsetid());
				}
			}
			String[] arr = (String[])hs.toArray(new String[0]);   
			Arrays.sort(arr);
			
			for(int j=0;j<arr.length;j++){
				map = new HashMap();
				String fieldsetid = arr[j];
				FieldSet fieldset = DataDictionary.getFieldSetVo(fieldsetid);
				if(fieldset!=null){
					if(fieldset==null)
						continue;
					if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
						continue;
					}else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
						continue;
					}else  if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
						continue;
					}
					map.put("id", fieldset.getFieldsetid());
					map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
					fieldsetlist.add(map);
				}	 
			}
			
			map = new HashMap();
			map.put("id", "vartemp");
			map.put("name", ResourceFactory.getProperty("menu.gz.variable"));
			fieldsetlist.add(map);
		}catch(Exception e){
			e.printStackTrace();
		}
		return fieldsetlist;
	}
	
	 /**
     * 查询子集
     * @param dao
     * @return retlist
     * @throws GeneralException
     */
	public ArrayList functionList(){
		ArrayList fieldsetlist = new ArrayList();
		CommonData obj1=new CommonData("","");
		fieldsetlist.add(obj1);
		HashMap map = new HashMap();
		ArrayList listset = new ArrayList();
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.EMPLOY_FIELD_SET));
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET));
		listset.addAll(DataDictionary.getFieldSetList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET));
			
		for(int i=0;i<listset.size();i++){
			 FieldSet fieldset = (FieldSet)listset.get(i);
			 if(fieldset==null)
				 continue;
			 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
				 continue;
			 }
			 if(this.userview.analyseTablePriv(fieldset.getFieldsetid())==null)
				 continue;
			 if("0".equals(this.userview.analyseTablePriv(fieldset.getFieldsetid())))
				 continue;
			 
			 map = new HashMap();
			 map.put("id", fieldset.getFieldsetid());
			 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
			 fieldsetlist.add(map);
		}
		map = new HashMap();
		map.put("id", "vartemp");
		map.put("name", ResourceFactory.getProperty("menu.gz.variable"));
		fieldsetlist.add(map);
		return fieldsetlist;
	}
	
	/**
	 * 从临时变量中取得对应指标列表
	 * @return FieldItem对象列表
	 * @throws GeneralException
	 */
	private ArrayList getMidVariableList(String salaryid){
		ArrayList fieldlist=new ArrayList();
		try{
			ContentDAO dao=new ContentDAO(this.conn);
			StringBuffer buf = new StringBuffer();
			if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				String fieldsetid = (String) this.userview.getHm().get("fieldsetid");
				String sqlstr="select * from fielditem ";
				if(fieldsetid!=null&&fieldsetid.trim().length()>0){
					sqlstr+=" where fieldsetid='"+fieldsetid+"' and useflag = '1'";
				}
				RowSet rset=dao.search(sqlstr);
				while(rset.next()){
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("ITEMID"));
					item.setItemdesc(rset.getString("ITEMDESC"));
					item.setFieldsetid(rset.getString("FIELDSETID"));
					item.setItemlength(rset.getInt("ITEMLENGTH"));
					item.setFormula(Sql_switcher.readMemo(rset, "AuditingFormula"));
					item.setDecimalwidth(rset.getInt("DECIMALWIDTH"));
					item.setItemtype(rset.getString("ITEMTYPE"));
					item.setCodesetid(rset.getString("CODESETID"));
					item.setVarible(0);
					fieldlist.add(item);
				}
				rset.close();
			}else{
				buf.append("select * from salaryset");
				if(salaryid!=null&&!"all".equalsIgnoreCase(salaryid))
					buf.append(" where salaryid='"+salaryid+"'");
				RowSet rset=dao.search(buf.toString());
				while(rset.next()){
					FieldItem item=new FieldItem();
					item.setItemid(rset.getString("ITEMID"));
					item.setItemdesc(rset.getString("ITEMDESC"));
					item.setFieldsetid(rset.getString("FIELDSETID"));
					item.setItemlength(rset.getInt("ITEMLENGTH"));
					item.setFormula(Sql_switcher.readMemo(rset, "FORMULA"));
					item.setDecimalwidth(rset.getInt("DECWIDTH"));
					item.setItemtype(rset.getString("ITEMTYPE"));
					item.setVarible(1);
					fieldlist.add(item);
				}
				rset.close();
			}
			buf.setLength(0);
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,cstate from ");
			if("-2".equals(salaryid)){//数据采集模块，zhaoxg 2013-9-12 add 薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
				buf.append(" midvariable where nflag=5 and templetid=0 ");
				String fieldsetid = (String) this.userview.getHm().get("fieldsetid");
				if(fieldsetid!=null&&fieldsetid.length()>0){
					buf.append(" and (cstate is null or cstate='");
					buf.append(fieldsetid);
					buf.append("')");
				}
			}else if("-2".equals(salaryid)){
				buf.append(" midvariable where nflag=4 and templetid=0 ");
				buf.append(" and cstate='");
				buf.append(-1);
				buf.append("'");
			}else{
				buf.append(" midvariable where nflag=0 and templetid=0 ");
				if(salaryid!=null&&!"all".equalsIgnoreCase(salaryid)){
					buf.append(" and (cstate is null or cstate='");
					buf.append(salaryid);
					buf.append("')");
				}
			}
			RowSet rset=dao.search(buf.toString());
			while(rset.next())
			{
				FieldItem item=new FieldItem();
				item.setItemid(rset.getString("cname"));
				if("-2".equals(salaryid)){ //薪资总额的计算公式编号为-1，数据采集的计算公式编号为-2
					String fieldsetid = (String) this.userview.getHm().get("fieldsetid");
					item.setFieldsetid(fieldsetid);//没有实际含义
				}else if("-1".equals(salaryid)){
					GzAmountXMLBo bo = new GzAmountXMLBo(this.conn,1);
					HashMap map =bo.getValuesMap();
					String fieldsetid = ((String)map.get("setid")).length()>0?(String)map.get("setid"):"A01";
					item.setFieldsetid(fieldsetid);
				}else{
					item.setFieldsetid("A01");//没有实际含义
				}
				item.setItemdesc(rset.getString("chz"));
				item.setItemlength(rset.getInt("fldlen"));
				item.setDecimalwidth(rset.getInt("flddec"));
				item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
				switch(rset.getInt("ntype")) 
				{
				case 1://
					item.setItemtype("N");
					break;
				case 2:
					item.setItemtype("A");
					break;
				case 4:
					item.setItemtype("A");
					break;
				case 3:
					item.setItemtype("D");
					break;
				}
				item.setVarible(1);
				fieldlist.add(item);
			}// while loop end.
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return fieldlist;
	}
	/**
	 * 获取单位编码
	 * @return
	 */
	public ArrayList functionListunit(){
		ArrayList fieldsetlistunit = new ArrayList();
		try{
			ArrayList listset = new ArrayList();
			HashMap map = new HashMap();
//			map.put("id", "");
//			map.put("name", "");
//			fieldsetlistunit.add(map);
			listset = this.userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 map = new HashMap();
				 map.put("id", fieldset.getFieldsetid());
				 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistunit.add(map);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fieldsetlistunit;
	}
	/**
	 * 
	 * @return
	 */
	public ArrayList functionListpos(){
		ArrayList fieldsetlistpos = new ArrayList();
		try{
			ArrayList listset = new ArrayList();
			HashMap map = new HashMap();
//			map.put("id", "");
//			map.put("name", "");
//			fieldsetlistpos.add(map);
			listset = this.userview.getPrivFieldSetList(Constant.POS_FIELD_SET);
			for(int i=0;i<listset.size();i++){
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 map = new HashMap();
				 map.put("id", fieldset.getFieldsetid());
				 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlistpos.add(map);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return fieldsetlistpos;
	}
	/**
	 * 取得“代码转名称2”这个公式的下拉列表
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeListForFormula(String itemid){
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		map.put("id", "");
		map.put("name", "");
		list.add(map);
		if(itemid!=null && itemid.length()>0){
			FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
			if (fielditem==null && itemid.contains("_")){//人事异动指标 后面带_1 _2
			    itemid=itemid.substring(0, itemid.indexOf("_"));
			    fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
			}
			String codesetid ="";
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				map = new HashMap();
				map.put("id", codesetid);
				map.put("name", codesetid);
				list.add(map);
			}
		}	
		return list;
	}
	/**
	 * 查询标准表
	 * 
	 * @param dao
	 * @return retlist
	 * @throws GeneralException
	 */
	public ArrayList standList() {
		ContentDAO dao=new ContentDAO(this.conn);
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("select id,name,hfactor,vfactor,s_hfactor,s_vfactor,item from gz_stand_history");
		sql.append(" where pkg_id in(select pkg_id from gz_stand_pkg where status='1')");
		String unitid = "XXXX";
		StringBuffer tt = new StringBuffer();
		if(this.userview.isSuper_admin())
		{
			unitid="";
			tt.append(" or 1=1 ");
		}
		else
		{
			if(this.userview.getUnit_id()!=null&&this.userview.getUnit_id().trim().length()>2)
			{
				if(this.userview.getUnit_id().length()==3)
				{
					unitid="";
					tt.append(" or 1=1 ");
				}
				else
				{
			    	unitid=this.userview.getUnit_id();
			    	String[] unit_arr = unitid.split("`");
			    	for(int i=0;i<unit_arr.length;i++)
			    	{
			    		 
			    		if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2)
			    			continue;
			    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
			    	}
				}
			}
			else{
				if(this.userview.getManagePrivCode()!=null&&this.userview.getManagePrivCode().trim().length()>0)
				{
					if(this.userview.getManagePrivCodeValue()==null|| "".equals(this.userview.getManagePrivCodeValue().trim()))
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else{
				    	unitid=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
				    	tt.append(" or b0110 like '%,"+this.userview.getManagePrivCodeValue()+"%'");
					}
				}
			}
		}
		if(tt.toString().length()>0)
		{
			if(this.userview.isSuper_admin()|| "".equals(unitid))
			{
				
			}else
			{
				sql.append(" and (");
				sql.append("("+tt.toString().substring(3)+")");
				sql.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "''")+"=''");
				sql.append(")");
			}
		}
		if("XXXX".equals(unitid))
		{
			sql.append(" and "+Sql_switcher.isnull("b0110", "''")+"=''");
		}
		sql.append(" order by id");
		ArrayList dylist = null;
		HashMap map = new HashMap();
		try {
			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String hfactor = dynabean.get("hfactor").toString();
				hfactor = hfactor != null ? hfactor : "";
				String vfactor = dynabean.get("vfactor").toString();
				vfactor = vfactor != null ? vfactor : "";
				String s_hfactor = dynabean.get("s_hfactor").toString();
				s_hfactor = s_hfactor != null ? s_hfactor : "";
				String s_vfactor = dynabean.get("s_vfactor").toString();
				s_vfactor = s_vfactor != null ? s_vfactor : "";
				map = new HashMap();
				map.put("id", hfactor + ":" + vfactor + ":"
						+ s_hfactor + ":" + s_vfactor + ":"
						+ dynabean.get("name").toString() + ":"
						+ dynabean.get("item").toString() + ":"
						+ dynabean.get("id").toString());
				map.put("name", dynabean.get("id")
						.toString()
						+ "." + dynabean.get("name").toString());
				retlist.add(map);
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
//		map = new HashMap();
//		map.put("id", "");
//		map.put("name", "");
//		retlist.add(0, map);
		return retlist;
	}
	/**
	 * 根据子标获取代码
	 * @return ArrayList 
	 * @throws Exception
	 */
	public ArrayList codeList(String itemid){
		ArrayList list = new ArrayList();
		HashMap map = new HashMap();
		FieldItem fielditem = (FieldItem)DataDictionary.getFieldItem(itemid);
		String codesetid ="";
		if(fielditem==null){
		    fielditem=getMidVariableList(itemid,"0");	
		}
		try {
			if(fielditem!=null){
				codesetid = fielditem.getCodesetid();
				if(fielditem.isCode()||codesetid.trim().length()>0){
					if(codesetid!=null||codesetid.trim().length()>0){
						StringBuffer sqlstr = new StringBuffer();
						if("@K".equalsIgnoreCase(codesetid)|| "UM".equalsIgnoreCase(codesetid)|| "UN".equalsIgnoreCase(codesetid)){
							sqlstr.append("select codeitemid,codeitemdesc from organization where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' order by a0000");
						}else if("@@".equalsIgnoreCase(codesetid)){
							sqlstr.append("select Pre as codeitemid,DBName as codeitemdesc from dbname");
						}else
						{
							sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='"); 
							sqlstr.append(codesetid);
							sqlstr.append("' and invalid=1");
							if(AdminCode.isRecHistoryCode(codesetid)){//按照是否有效和有效时间来卡住  zhaoxg add 2014-8-14
								String bosdate = DateStyle.dateformat(new Date(), "yyyy-MM-dd");
								sqlstr.append(" and " + Sql_switcher.dateValue(bosdate) + " between start_date and end_date ");
							}
							sqlstr.append(" order by a0000");
						}
						ArrayList dylist = null;
						ContentDAO dao = new ContentDAO(this.conn);
						dylist = dao.searchDynaList(sqlstr.toString());
						for(Iterator it=dylist.iterator();it.hasNext();){
							map = new HashMap();
							DynaBean dynabean=(DynaBean)it.next();
							String codeitemid = dynabean.get("codeitemid").toString();
							String codeitemdesc = dynabean.get("codeitemdesc").toString();
							map.put("id", codeitemid);
							map.put("name", codeitemid+":"+codeitemdesc);
							list.add(map);
						}
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						list.add(0,map);
					}else{
						map = new HashMap();
						map.put("id", "");
						map.put("name", "");
						list.add(map);
					}
				}else{
					map = new HashMap();
					map.put("id", "");
					map.put("name", "");
					list.add(map);
				}
			}else{
				map = new HashMap();
				map.put("id", "");
				map.put("name", "");
				list.add(map);
				if("escope".equals(itemid)){
					map = new HashMap();
					map.put("id", "1");
					map.put("name", "1"+":"+"离休人员");
					list.add(map);
					map = new HashMap();
					map.put("id", "2");
					map.put("name", "2"+":"+"退休人员");
					list.add(map);
					map = new HashMap();
					map.put("id", "3");
					map.put("name", "3"+":"+"内退人员");
					list.add(map);
					map = new HashMap();
					map.put("id", "4");
					map.put("name", "4"+":"+"遗嘱");
					list.add(map);
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
     * 从临时变量中取得对应指标列表
     * @return FieldItem对象列表
     * @throws GeneralException
     */
    public FieldItem getMidVariableList(String itemid,String nflag){
        FieldItem item=null;
        try{
            StringBuffer buf=new StringBuffer();
            buf.append("select nid,chz,ntype,cvalue,fldlen,flddec,codesetid from ");
            buf.append(" midvariable where nflag="+nflag+"  and  ");
            Pattern pattern = Pattern.compile("[0-9]+");
            if(pattern.matcher(itemid.trim()).matches()) //整型 用nid
            {
                buf.append(" nid="+itemid);
            }
            else
                buf.append(" cname='"+itemid+"'"); 
            ContentDAO dao=new ContentDAO(this.conn);
            RowSet rset=dao.search(buf.toString());
            if(rset.next())
            {
                item=new FieldItem();
                item.setItemid(rset.getString("nid"));
                item.setFieldsetid("A01");//没有实际含义
                item.setItemdesc(rset.getString("chz"));
                item.setItemlength(rset.getInt("fldlen"));
                item.setDecimalwidth(rset.getInt("flddec"));
                item.setFormula(Sql_switcher.readMemo(rset, "cvalue"));
                switch(rset.getInt("ntype"))
                {
                case 1://
                    item.setItemtype("N");
                    item.setCodesetid("0");
                    break;
                case 2:
                    item.setItemtype("A");
                    item.setCodesetid("0");
                    break;
                case 3:
                    item.setItemtype("D");
                    item.setCodesetid("0");
                    break;
                case 4:
                    item.setItemtype("A");
                    item.setCodesetid(rset.getString("codesetid"));
                    break;
                }
                item.setVarible(1);
                
            }// while loop end.
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return item;
    }
	/**
	 * 查询标准表
	 * 
	 * @param dao
	 * @return retlist
	 * @throws SQLException 
	 * @throws GeneralException
	 */
	public ArrayList standidList() throws SQLException {
		ArrayList retlist = new ArrayList();
		StringBuffer sql = new StringBuffer();
		HashMap map = new HashMap();
		sql.append("select id,name,hfactor,vfactor,s_hfactor,s_vfactor,item from gz_stand");
		sql.append(" where ((nullif(hfactor,'') is not null or nullif(s_hfactor,'') is not null)");
		sql.append(" and  (nullif(vfactor,'') is not null or nullif(s_vfactor,'') is not null ) ) ");
		sql.append(" and ((nullif(hfactor,'') is not null or hfactor is  null) or (nullif(s_hfactor,'') is not null or s_hfactor is null))");
		sql.append(" and ((nullif(vfactor,'') is not null or vfactor is null) or (nullif(s_vfactor,'') is not null or s_vfactor is null))");
		sql.append(" and nullif(item,'') is not null and item is not null");
		//归属单位限制
		StringBuffer sql2 = new StringBuffer();
		sql2.append("select id from gz_stand_history");
		sql2.append(" where pkg_id in(select pkg_id from gz_stand_pkg where status='1')");
		
		ArrayList dylist = null;
		try {
			ContentDAO dao=new ContentDAO(this.conn);
			String unitid = "XXXX";
			StringBuffer tt = new StringBuffer();
			if(this.userview.isSuper_admin())
			{
				unitid="";
				tt.append(" or 1=1 ");
			}
			else
			{
				if(this.userview.getUnit_id()!=null&&this.userview.getUnit_id().trim().length()>2)
				{
					if(this.userview.getUnit_id().length()==3)
					{
						unitid="";
						tt.append(" or 1=1 ");
					}
					else
					{
				    	unitid=this.userview.getUnit_id();
				    	String[] unit_arr = unitid.split("`");
				    	for(int i=0;i<unit_arr.length;i++)
				    	{
				    		if(unit_arr[i]==null|| "".equals(unit_arr[i])||unit_arr[i].length()<2)
				    			continue;
				    		tt.append(" or b0110 like '%,"+unit_arr[i].substring(2)+"%' ");
				    	}
					}
				}
				else{
					if(this.userview.getManagePrivCode()!=null&&this.userview.getManagePrivCode().trim().length()>0)
					{
						if(this.userview.getManagePrivCodeValue()==null|| "".equals(this.userview.getManagePrivCodeValue().trim()))
						{
							unitid="";
							tt.append(" or 1=1 ");
						}
						else{
					    	unitid=this.userview.getManagePrivCode()+this.userview.getManagePrivCodeValue();
					    	tt.append(" or b0110 like '%,"+this.userview.getManagePrivCodeValue()+"%'");
						}
					}
					else//没有范围
					{
						
					}
				}
			}
			if(tt.toString().length()>0)
			{
				if(this.userview.isSuper_admin()|| "".equals(unitid))
				{
					
				}else
				{
					sql2.append(" and (");
					sql2.append("("+tt.toString().substring(3)+")");
					sql2.append(" or UPPER(b0110)='UN' or "+Sql_switcher.isnull("b0110", "''")+"=''");
					sql2.append(")");
				}
			}
			if("XXXX".equals(unitid))
			{
				sql2.append(" and "+Sql_switcher.isnull("b0110", "''")+"=''");
			}
			if(dao.search(sql2.toString()).next()){
				sql.append(" and id in ( ");
				sql.append(sql2+" )");
			}

			dylist = dao.searchDynaList(sql.toString());
			for (Iterator it = dylist.iterator(); it.hasNext();) {
				DynaBean dynabean = (DynaBean) it.next();
				String hfactor = dynabean.get("hfactor").toString();
				hfactor = hfactor != null ? hfactor : "";
				String vfactor = dynabean.get("vfactor").toString();
				vfactor = vfactor != null ? vfactor : "";
				String s_hfactor = dynabean.get("s_hfactor").toString();
				s_hfactor = s_hfactor != null ? s_hfactor : "";
				String s_vfactor = dynabean.get("s_vfactor").toString();
				s_vfactor = s_vfactor != null ? s_vfactor : "";
				String item = dynabean.get("item").toString();
				item = item != null ? item : "";
				boolean check = true;
				if (hfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary.getFieldItem(hfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (vfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary.getFieldItem(vfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (s_hfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary
							.getFieldItem(s_hfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (s_vfactor.trim().length() > 0) {
					FieldItem fielditem = DataDictionary
							.getFieldItem(s_vfactor);
					if (fielditem != null) {
						if (!fielditem.isCode()) {
							check = false;
						}
					} else {
						check = false;
					}
				}
				if (check) {
					map = new HashMap();
					map.put("id", hfactor + ":" + vfactor
							+ ":" + s_hfactor + ":" + s_vfactor + ":"
							+ dynabean.get("name").toString() + ":"
							+ dynabean.get("item").toString() + ":"
							+ dynabean.get("id").toString());
					map.put("name", dynabean.get("id").toString()
							+ "." + dynabean.get("name").toString());
					retlist.add(map);
				}
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		}
//		map = new HashMap();
//		map.put("id", "");
//		map.put("name", "");
//		retlist.add(0, map);
		return retlist;
	}
/**
 * gaohy
 * @param tableid
 * @return 获得人事异动-向导组件-模版指标
 */
	private ArrayList itemList(String tableid){
		ArrayList itemlist = new ArrayList();
		if(tableid.length()>0){
			try {
				String stritem="";
				TemplateTableBo changebo = new TemplateTableBo(this.conn,Integer.parseInt(tableid),this.userview);
				ArrayList list = changebo.getAllFieldItem();
				HashMap map = changebo.getSub_domain_map();
				HashMap field_name_map = changebo.getField_name_map();
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					if(fielditem.isChangeAfter()){
						if(stritem.indexOf(fielditem.getItemid()+"_2")!=-1)
							continue;
						stritem+=fielditem.getItemid()+"_2,";
						String itemdesc =ResourceFactory.getProperty("inform.muster.to.be")+fielditem.getItemdesc();
						fielditem.setItemdesc(itemdesc);
						fielditem.setItemid(fielditem.getItemid()+"_2");
					}else if(fielditem.isChangeBefore()){
						//多个变化前加上_id
						String sub_domain_id="";
						if(map!=null&&map.get(""+i)!=null&&map.get(""+i).toString().trim().length()>0){
						
						sub_domain_id ="_"+(String)map.get(""+i);
						}
						if(stritem.indexOf(fielditem.getItemid()+sub_domain_id+"_1")!=-1)
							continue;
						if(field_name_map!=null&&field_name_map.get(fielditem.getItemid()+sub_domain_id+"_1")!=null)
							continue;
						stritem+=fielditem.getItemid()+sub_domain_id+"_1,";
						if(sub_domain_id!=null&&sub_domain_id.length()>0){
							fielditem.setItemid(fielditem.getItemid()+"_"+map.get(""+i)+"_1 ");
							fielditem.setItemdesc(""+map.get(""+i+"hz"));
							}else{
								fielditem.setItemid(fielditem.getItemid()+"_1");	
							}
				//		if(!fielditem.getFieldsetid().equalsIgnoreCase("A01")){
							String itemdesc=ResourceFactory.getProperty("inform.muster.now")+fielditem.getItemdesc();
							fielditem.setItemdesc(itemdesc);
				//		}
						
					}
					itemlist.add(fielditem);
				}
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return itemlist;
	}
	/**
	 * 招聘模块
	 * @param keyid
	 * @return
	 * @throws GeneralException 
	 */
	public ArrayList fieldSetList(String keyid) throws GeneralException {
		ArrayList fieldsetlist = new ArrayList();
		 try {
			 HashMap map = new HashMap();
//			 map.put("id", "");
//			 map.put("name", "");
//			 fieldsetlist.add(map);
			
			ArrayList listset = new ArrayList();
			if("1".equals(keyid))
				listset = this.userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			else if("2".equals(keyid))
				listset = this.userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET);
			else if("3".equals(keyid))
				listset = this.userview.getPrivFieldSetList(Constant.POS_FIELD_SET);
			else if("4".equals(keyid))
				listset = this.userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			else if("5".equals(keyid)){
				FieldSet fieldset= DataDictionary.getFieldSetVo("r45");
				fieldset.setUseflag("1");
				listset.add(fieldset);
			}else if("7".equals(keyid)){
				listset = this.userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
				listset.addAll(this.userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET));
				listset.addAll(this.userview.getPrivFieldSetList(Constant.POS_FIELD_SET));
			}else if("8".equals(keyid)){
				listset = this.userview.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
				listset.addAll(this.userview.getPrivFieldSetList(Constant.UNIT_FIELD_SET));
			}
			
			for(int i=0;i<listset.size();i++){
				 map = new HashMap();
				 FieldSet fieldset = (FieldSet)listset.get(i);
				 if(fieldset==null)
					 continue;
				 if("A00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("B00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }else  if("K00".equalsIgnoreCase(fieldset.getFieldsetid())){
					 continue;
				 }
				 map.put("id", fieldset.getFieldsetid());
				 map.put("name", fieldset.getFieldsetid()+"-"+fieldset.getCustomdesc());
				 fieldsetlist.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return fieldsetlist;
	}
	
	/**
	 * 单独查出指定的临时变量
	 * @Title: findVariable   
	 * @Description:    
	 * @param @param salaryid
	 * @param @return 
	 * @return ArrayList    
	 * @throws
	 * @author sunjian
	 * @date 2017-06-15
	 */
	public ArrayList findVariable(String keyid,String opt,String type) {
		ArrayList fieldlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		StringBuffer buf = new StringBuffer();
		HashMap map = new HashMap();
		try {
			buf.append("select cname,chz,ntype,cvalue,fldlen,flddec,cstate from  midvariable where 1=1 ");
			if(StringUtils.isNotBlank(type)) {//如果type不为空则说明是从临时变量进来
				if("1".equals(type) && !"all".equalsIgnoreCase(keyid)){//入口标识  1：薪资  2：薪资总额  3：人事异动  4...其他    区别临时变量时opt无法分别出是什么
					keyid = PubFunc.decrypt(SafeCode.decode(keyid));
					buf.append(" and templetid=0  and (cstate is null or cstate='");
					buf.append(keyid);
					buf.append("')");
				}else if("3".equals(type)) {//人事异动通过templateid进行确定
					buf.append(" and nflag=0 and templetId <> 0 and (templetid ='");
					buf.append(keyid);
					buf.append("' or cstate = '1')");
				}
			}else if(StringUtils.isNotBlank(keyid)) {
				if("1".equals(opt) && !"all".equalsIgnoreCase(keyid)){
					buf.append(" and templetid=0  and (cstate is null or cstate='");
					buf.append(keyid);
					buf.append("')");
				}else if("2".equals(opt)) {//人事异动通过templateid进行确定
					buf.append(" and nflag=0 and templetId <> 0 and (templetid ='");
					buf.append(keyid);
					buf.append("' or cstate = '1')");
				}
			}
			RowSet rset=dao.search(buf.toString());
			while(rset.next()) {
				map = new HashMap();
				map.put("name", rset.getString("chz"));
				map.put("id", rset.getString("chz"));
				fieldlist.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fieldlist;
	}
}
