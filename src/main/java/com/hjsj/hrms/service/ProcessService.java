/**
 * 
 */
package com.hjsj.hrms.service;

import com.hjsj.hrms.businessobject.kq.kqself.AnnualApply;
import com.hjsj.hrms.businessobject.kq.machine.KqParam;
import com.hjsj.hrms.businessobject.org.SyncOrgInfo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.businessobject.sys.sysout.SyncBo;
import com.hjsj.hrms.service.ladp.PareXmlUtils;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.ejb.idfactory.IDFactoryBean;
import com.hrms.struts.exception.GeneralException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Administrator
 * 
 */
public class ProcessService {

	
//	private Category cat = Category.getInstance(this.getClass().getName());
	private Log cat = LogFactory.getLog("interface");
	
	/**
	 * 流程集成
	 * @param username
	 * @param password
	 * @param type
	 * @param xml
	 * @return 校验用户名、密码 JinChunhai 2013.10.12
	 */	
	public String syncProcess(String username,String password,String type, String xml) 
	{
		String flag = "1";
		try 
		{
			if(username==null||username.length()<=0||password==null||password.length()<=0)
			{
				return returnMessLog("传入的校验用户名密码不能为空！",1,"");
			}
			boolean isCheck = cheakCode(username,password);
			if(!isCheck)
			{
				return returnMessLog("传入的校验用户名密码错误！",1,"");
			}
			// 调用原来的流程集成方法
			flag = syncProcess(type, xml);
			
		} catch (Exception e) {
 			e.printStackTrace();
		}
//		System.out.println("ehr流程返回值xml:"+flag);
		return flag;
	}	
	/**
	 * 流程集成
	 * 
	 * @param type
	 * @param xml
	 * @return
	 */		
	public String syncProcess(String type, String xml) {
		Connection conn = null;
		RowSet rs = null;
		String flag = "1";
		try {
			conn = AdminDb.getConnection();
			
		//	System.out.println("OA传入的流程标记-----"+type+"---OA传入XML字符串=====" + xml);
		//	cat.debug("OA传入的流程标记-----"+type+"---OA传入XML字符串=====" + xml);
		//	cat.error("OA传入的流程标记-----"+type+"---OA传入XML字符串=====" + xml);
			
			if("SYNC_UNIT".equalsIgnoreCase(type)) // 单位信息同步
			{/*
			    // 传送过来的xml串
				<?xml version="1.0" encoding="GB2312"?>
				<root>
				  <record>
				  	<hr_num>OA系统流水号</hr_num>
				    <!--单位信息-->
				    <subid>32432</subid><!--OA系统中单位编码-->
				    <sname>二手房集团</sname><!--单位名称-->
				    <pid>111</pid><!--OA系统中单位的父机构编码-->
				    <disporder >122</disporder><!--顺序号-->
				    <canceled>1</canceled>
				    <flag>系统标志， 1：新增；2：更新；当更新时需要自动检测<canceled>值，自动处理是否停用 </flag>，增加flag=3，当该单位确认不需要时，删除，其下级节点的父亲节点也与删掉。
				  </record>
				  ......
				</root>
				
				// 返回字符串
				<?xml version="1.0" encoding="GB2312"?>
				<root>
  					<record>
      					<hr_num>OA系统流水号</hr_num>
      					<!--单位编码-->
      					<subid>32432</subid>
				        <sname>二手房集团</sname><!--单位名称-->
				        <!--是否保存成功标识-->
				        <flag>0为成功，1为不成功</flag>
				        <erroinfo>找不到父部门</erroinfo><!--失败原因 -->
                   </record>

                   ......
               </root>
			*/	
				// 返回字符串
				StringBuffer retunValue = new StringBuffer();
				//需要返回的XML字符串
				retunValue.append("<?xml version='1.0' encoding='GB2312'?> ");
				retunValue.append("	<root>");				
				
				SyncOrgInfo so = new SyncOrgInfo();
				ArrayList list = getMapList(xml, "/root/record");
				for (int i = 0; i < list.size(); i++) 
				{
					Map map = (Map) list.get(i);
					// OA系统流水号
					String hr_num = (String) map.get("hr_num");
					// OA系统中单位编码
					String subid = (String) map.get("subid");
					// OA系统单位名称
					String sname = (String) map.get("sname");
					// OA系统中单位的父机构编码
					String pid = (String) map.get("pid");
					if(pid==null || pid.trim().length()<=0 || "0".equalsIgnoreCase(pid))
						pid = "";
					// OA系统顺序号
					String disporder = (String) map.get("disporder");
					// OA系统单位封存标识
					String canceled = (String) map.get("canceled");
					boolean booleanFlag = false;
					if("1".equalsIgnoreCase(canceled))
						booleanFlag = true;
					// OA系统单位封存时间
					String canceldate = (String) map.get("canceldate");						
					// 系统标志
					String oaflag = (String) map.get("flag"); // 1：新增；2：更新；当更新时需要自动检测<canceled>值，自动处理是否停用 flag=3，当该部门确认不需要时，删除，其下级节点的父亲节点也与删掉
					
					String sucssflag = "1"; // 
					String erroinfo = ""; // 同步失败原因
					if("1".equalsIgnoreCase(oaflag))
					{
						if(isExistUnit("UN",subid,conn)) // 如果oaflag为新增，但是此机构已经存在，那么执行更新操作
						{
							erroinfo = so.update("UN",sname,subid,pid,"",disporder,booleanFlag,canceldate);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	update方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      corCode  转换代码
							//	    canceled  是否撤销标识 true：撤销 false：不撤销
							//       a0000   顺序号
						}
						else
						{
							erroinfo = so.insert("UN",sname,subid,pid,"",disporder);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	insert方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//  orgName 组织机构名称  ，例如“人力资源部”
							//  parentID 父机构id，希望是转换代码，					
							//  corCode  转换代码
							//  a0000   顺序号
						}											
					}
					else if("2".equalsIgnoreCase(oaflag))
					{
						if(!isExistUnit("UN",subid,conn)) // 如果oaflag为更新，但是此机构不存在，那么执行新增操作
						{
							erroinfo = so.insert("UN",sname,subid,pid,"",disporder);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	insert方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//  orgName 组织机构名称  ，例如“人力资源部”
							//  parentID 父机构id，希望是转换代码，					
							//  corCode  转换代码
							//  a0000   顺序号
						}
						else
						{
							erroinfo = so.update("UN",sname,subid,pid,"",disporder,booleanFlag,canceldate);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	update方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      corCode  转换代码
							//	    canceled  是否撤销标识 true：撤销 false：不撤销
							//       a0000   顺序号
						}						
					}
					else if("3".equalsIgnoreCase(oaflag))
					{
						erroinfo = so.delete(subid,"UN");
						if("ok".equalsIgnoreCase(erroinfo))
							sucssflag = "0";
						//	delete方法参数：corCode  转换代码
					}				 					
					
					retunValue.append("<record>");
					retunValue.append("	<hr_num>"+hr_num+"</hr_num>");
					retunValue.append(" <subid>"+subid+"</subid>");
					retunValue.append(" <sname>"+sname+"</sname>");
					retunValue.append("	<flag>"+sucssflag+"</flag>"); // 0为成功，1为不成功
					retunValue.append("	<erroinfo>"+erroinfo+"</erroinfo>"); // 失败原因
					retunValue.append("</record>");					
				}

				retunValue.append("	</root>");
				flag = retunValue.toString();	
				
			}
			else if ("SYNC_DEPT".equalsIgnoreCase(type)) // 部门信息同步
			{
			/*
			    // 传送过来的xml串
				<?xml version="1.0" encoding="GB2312"?>
				<root>
				  <record>
				    <hr_num>OA系统传入的流水号</hr_num>
				    <depid>1233</depid>
				    <dname >人力资源部</dname >
				    <pid>111</pid> <!-- 父节点名称，如果父节点为单位，请填写单位编码，如果为部门，请填写部门编码-->
				    <sid>11</sid> <!--所属分部Id 即ehr中的单位id-->            
				    <disporder>122</disporder><!--顺序号-->
				    <canceled>1</canceled>
				    <flag>系统标志，1：新增；2：更新；当更新时需要自动检测<canceled>值，自动处理是否停用</flag>增加flag=3，当该部门确认不需要时，删除，其下级节点的父亲节点也与删掉。				
				  </record>
				  ......
				</root>
				
				// 返回字符串
				<?xml version="1.0" encoding="GB2312"?>
				<root>
				   <record>
				      <hr_num>OA系统传入的流水号</hr_num>
				      <!--部门编码-->
				      <depid>1233</depid>
				      <dname>人力资源部</dname ><!--部门名称 -->
				      <!--是否保存成功标识-->
				      <flag>0为成功，1为不成功</flag>
				      <erroinfo>找不到父部门</erroinfo><!--失败原因 -->
				   </record>
				   ......
				</ root>
			*/	
				// 返回字符串
				StringBuffer retunValue = new StringBuffer();
				//需要返回的XML字符串
				retunValue.append("<?xml version='1.0' encoding='GB2312'?> ");
				retunValue.append("	<root>");				
				
				SyncOrgInfo so = new SyncOrgInfo();
				ArrayList list = getMapList(xml, "/root/record");
				for (int i = 0; i < list.size(); i++) 
				{
					Map map = (Map) list.get(i);
					// OA系统流水号
					String hr_num = (String) map.get("hr_num");
					// OA系统中部门编码
					String depid = (String) map.get("depid");
					// OA系统部门名称
					String dname = (String) map.get("dname");
					// OA系统中部门的父机构编码
					String pid = (String) map.get("pid");					
					// OA系统中部门所属单位的机构编码
					String sid = (String) map.get("sid");
					
					if(pid==null || pid.trim().length()<=0 || "0".equalsIgnoreCase(pid))
						pid = "";
										
					// OA系统顺序号
					String disporder = (String) map.get("disporder");
					// OA系统部门封存标识
					String canceled = (String) map.get("canceled");
					boolean booleanFlag = false;
					if("1".equalsIgnoreCase(canceled))
						booleanFlag = true;
					// OA系统部门封存时间
					String canceldate = (String) map.get("canceldate");					
					// 系统标志
					String oaflag = (String) map.get("flag"); // 1：新增；2：更新；当更新时需要自动检测<canceled>值，自动处理是否停用 flag=3，当该部门确认不需要时，删除，其下级节点的父亲节点也与删掉
										
					String sucssflag = "1"; // 
					String erroinfo = ""; // 同步失败原因
					if("1".equalsIgnoreCase(oaflag))
					{
						if(isExistUnit("UM",depid,conn)) // 如果oaflag为新增，但是此机构已经存在，那么执行更新操作
						{
							erroinfo = so.update("UM",dname,depid,pid,sid,disporder,booleanFlag,canceldate);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	update方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      corCode  转换代码
							//       a0000   顺序号
						}
						else
						{
							erroinfo = so.insert("UM",dname,depid,pid,sid,disporder);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";						
							//	insert方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      codeLength  本机代码长度，默认为2，
							//      corCode  转换代码
							//       a0000   顺序号
						}												
					}
					else if("2".equalsIgnoreCase(oaflag))
					{
						if(!isExistUnit("UM",depid,conn)) // 如果oaflag为更新，但是此机构不存在，那么执行新增操作
						{
							erroinfo = so.insert("UM",dname,depid,pid,sid,disporder);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";						
							//	insert方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      codeLength  本机代码长度，默认为2，
							//      corCode  转换代码
							//       a0000   顺序号
						}
						else
						{
							erroinfo = so.update("UM",dname,depid,pid,sid,disporder,booleanFlag,canceldate);
							if("ok".equalsIgnoreCase(erroinfo))
								sucssflag = "0";
							//	update方法参数：type   组织机构类型 值为"UM" "UN" "@K" ,
							//      orgName 组织机构名称  ，例如“人力资源部”
							//      parentID 父机构id，希望是转换代码，
							//      corCode  转换代码
							//       a0000   顺序号
						}
					}
					else if("3".equalsIgnoreCase(oaflag))
					{
						erroinfo = so.delete(depid,"UM");
						if("ok".equalsIgnoreCase(erroinfo))
							sucssflag = "0";
						//	delete方法参数：corCode  转换代码
					}				 					
					
					retunValue.append("<record>");
					retunValue.append("	<hr_num>"+hr_num+"</hr_num>");
					retunValue.append(" <depid>"+depid+"</depid>");
					retunValue.append(" <dname>"+dname+"</dname>");
					retunValue.append("	<flag>"+sucssflag+"</flag>"); // 0为成功，1为不成功
					retunValue.append("	<erroinfo>"+erroinfo+"</erroinfo>"); // 失败原因
					retunValue.append("</record>");					
				}

				retunValue.append("	</root>");
				flag = retunValue.toString();
				
			}
			else if ("SYNC_YD".equalsIgnoreCase(type)) {// 宁夏伊品异动流程
				// <?xml version="1.0" encoding="GB2312"?>
				// <infos>
				// <info>
				// <user_gh>异动人员工号</user_gh>
				// <user_namecn>异动人员中文名</user_namecn>
				// <type_name>异动类型</type_name>
				// <xdept_name>现部门名称</xdept_name>
				// <xdept_bh>现部门编号</xdept_bh>
				// <xzw_name>现职位名称</xzw_name>
				// <xzw_bh>现职位编号</xzw_bh>
				// <xdept_gz>现部门工资级别</xdept_gz>
				// <ndept_name>拟调部门名称</ndept_name>
				// <ndept_bh>拟调部门编号</ndept_bh>
				// <nzw_name>拟调职位名称</nzw_name>
				// <nzw_bh>拟调职位编号</nzw_bh>
				// <ndept_gz>拟调部门工资级别</ndept_gz>
				// <cause_text>异动情况说明</cause_text>
				// <jj_date>交接日期</jj_date>
				// <send_date>系统发送日期</send_date>
				// </info>
				// </infos>
				// 异动流程：通过OA员工异动流程，通过两个系统的集成，在HER系统人事信息管理中生成员工内部岗位异动工作经历，并自动进行人事信息的调整。
				ArrayList list = getMapList(xml, "/infos/info");
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					// 工号
					String ghValue = (String) map.get("user_gh");

					// 工号字段
					String ghField = SystemConfig
							.getPropertyValue("gonghaofieldname");

					// 人员编号及人员库
					Map str = getA0100AndNbaseMap(ghField, ghValue, conn);

					String a0100 = (String) str.get("a0100");
					String nbase = (String) str.get("nbase");

					if (a0100 == null || a0100.length() <= 0) {
						return "1";
					}

					String updateSql = "update " + nbase
							+ "A01 set e0122=?,e01a1=? where a0100=?";
					ArrayList dataList = new ArrayList();
					dataList.add(map.get("ndept_bh"));
					dataList.add(map.get("nzw_bh"));
					dataList.add(a0100);
					if (!updateData(updateSql, dataList, conn)) {
						return "1";
					}

					int i9999 = getI9999("A", nbase, a0100, "A42", conn);
					RecordVo vo = new RecordVo(nbase.toLowerCase() + "a42");

					// 人员编码
					vo.setString("a0100", a0100);
					// i9999
					vo.setInt("i9999", i9999 + 1);
					// 异动前部门 A4201
					vo
							.setString("a4201", map.get("xdept_bh").toString()
									.trim());
					// 异动前职位 A4202
					vo.setString("a4202", map.get("xzw_bh").toString().trim());
					// 异动后部门 A4207
					vo
							.setString("a4207", map.get("ndept_bh").toString()
									.trim());
					// 异动后职位 A4208
					vo.setString("a4208", map.get("nzw_bh").toString().trim());
					// 异动时间 A4209
					Date ydDate = DateUtils.getDate(map.get("send_date")
							.toString(), "yyyy-MM-dd HH:mm:ss");
					vo.setDate("a4209", ydDate);
					// 异动原因 A420A
					vo.setObject("a420a", map.get("cause_text").toString()
							.trim());

					if (!addData(vo, conn)) {
						return "1";
					}

				}

				flag = "0";

			} else if ("SYNC_KQ".equalsIgnoreCase(type)) {// 宁夏伊品请假流程
				// <?xml version="1.0" encoding="GB2312"?>
				// <infos>
				// <info>
				// <main_id> OA主文档ID</main_id>
				// <main_subject> OA审批主题</main_subject>
				// <main_bh> OA审批单编号</main_bh>
				// <user_gh>请假人员工号</user_gh>
				// <user_namecn>OA人员对应中文名</user_namecn>
				// <type_name>请假类型</type_name>
				// <start_date>起始日期</start_date>yyyy-MM-dd
				// <end_date>结束日期</end_date>
				// <start_time>开始时间HH:mm:ss</start_time >
				// <end_time>结束时间HH:mm:ss</end_time>
				// <day_am>上午</day_am>
				// <day_pm>下午</day_pm>
				// <day_num>请假天数</day_num>
				// <cause_text>请假事由</cause_text>
				// <send_date>系统发送日期</send_date>
				// </info>
				// </infos>
				// 请假流程：通过OA请假流程，在HER系统中生成员工请假经历，超过十天的假期，异动到待岗人员库中。
//				1、假期管理中必须有开始时间和结束时间

				ArrayList list = getMapList(xml, "/infos/info");
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					// 工号
					String ghValue = (String) map.get("user_gh");

					// 工号字段
					String ghField = SystemConfig
							.getPropertyValue("gonghaofieldname");
					
					// 请假天数
					String day_num = (String) map.get("day_num");

					// 人员编号及人员库
					Map str = getA0100AndNbaseMap(ghField, ghValue, conn);

					String a0100 = (String) str.get("a0100");
					String nbase = (String) str.get("nbase");
					String e0122 = (String) str.get("e0122");
					String e01a1 = (String) str.get("e01a1");
					String b0110 = (String) str.get("b0110");
					String a0101 = (String) str.get("a0101");
					String kq_startStr = map.get("start_date") + " "
							+ map.get("start_time");
					String kq_endStr = map.get("end_date") + " "
							+ map.get("end_time");

					Date kq_start = DateUtils.getDate(kq_startStr,
							"yyyy-MM-dd HH:mm:ss");
					Date kq_end = DateUtils.getDate(kq_endStr,
							"yyyy-MM-dd HH:mm:ss");

					Date sysDate = DateUtils.getDate(map.get("send_date")
							.toString(), "yyyy-MM-dd HH:mm:ss");

					if (a0100 == null || a0100.length() <= 0) {
						return "1";
					}

					// id Q15.Q1501
					IDFactoryBean idFactory = new IDFactoryBean();
					String id = idFactory.getId("Q15.Q1501", "", conn);

					AnnualApply annualApply = new AnnualApply(null, conn);
					float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则
					String leavetime_rule = KqParam.getInstance().getLeavetimeRule(conn, b0110);

					// 请假类型
					String typeName = (String) map.get("type_name");
					String sels = getKqItem(typeName, conn);

					// 保存
					RecordVo vo = new RecordVo("q15");
					// 单号
					vo.setString("q1501", id);

					// Nbase 人员库
					vo.setString("nbase", nbase);
					// B0110 单位
					vo.setString("b0110", b0110);
					// E0122 部门
					vo.setString("e0122", e0122);
					// E01A1 职位
					vo.setString("e01a1", e01a1);
					// A0101 姓名
					vo.setString("a0101", a0101);
					// A0100 人员编号
					vo.setString("a0100", a0100);
					// I9999 顺序号
					// Q1503 请假类型
					vo.setString("q1503", sels);
					// Q1505 申请日期
					// Q15Z1 起始时间
					vo.setDate("q15z1", kq_start);
					// Q15Z3 结束时间
					vo.setDate("q15z3", kq_end);
					// Q1507 请假事由
					vo.setString("q1507", map.get("cause_text").toString());
					// Q1509 部门领导
					// Q1511 部门领导意见
					// Q1513 单位领导
					// Q1515 单位领导意见
					// Q15Z0 审批结果
					vo.setString("q15z0", "01");
					// Q15Z5 审批状态
					vo.setString("q15z5", "03");
					// Q1504 参考班次
					// Q15Z7 审批时间
					vo.setDate("q15z7", sysDate);
					// Q1517
					// Q1519 原请假单号
					// state 状态
					// history 假期扣减信息

					if (!addData(vo, conn)) {
						return "1";
					}

					if (KqParam.getInstance().isHoliday(conn, b0110, sels)) {
						HashMap kqItem_hash = annualApply.count_Leave(sels);

						String start = DateUtils.format(kq_start,
								"yyyy.MM.dd HH:mm:ss");
						String end = DateUtils.format(kq_end,
								"yyyy.MM.dd HH:mm:ss");
						float leave_tiem = annualApply.getHistoryLeaveTime(
								kq_start, kq_end, a0100, nbase, b0110, kqItem_hash,
								holiday_rules);
						String history = annualApply.upLeaveManage(a0100, nbase,
								sels, start, end, leave_tiem, "1", b0110, kqItem_hash,
								holiday_rules);
						String sql = "update q15 set history='" + history
								+ "' where q1501='" + id + "'";
						
						updateData(sql, new ArrayList(), conn);
					}
					
					double dayNum = Double.parseDouble(day_num);
					if (dayNum > 10) {
						// 移库
						DbNameBo bo = new DbNameBo(conn);
						bo.moveDataBetweenBase2(a0100, nbase, "dgs", "1");
					}

				}

				flag = "0";

			} else if ("SYNC_CT".equalsIgnoreCase(type)) {// 伊品辞退流程
				// <?xml version="1.0" encoding="GB2312"?>
				// <infos>
				// <info>
				// <main_id> OA主文档ID</main_id>
				// <main_subject> OA审批主题</main_subject>
				// <main_questuser>审批单申请人</main_questuser>
				// <main_bh> OA审批单编号</main_bh>
				// <send_date>系统发送日期</send_date>
				// <subinfo>
				// <user_gh>辞退人员工号</user_gh>
				// <user_namecn> OA人员对应中文名</user_namecn>
				// <user_zw>人员职位</user_zw>
				// <user_ctrq>辞退日期</user_ctrq>
				// <user_ctsy>辞退原因</user_ctsy>
				// <user_tk>辞退条款</user_tk>
				// </subinfo>
				// <subinfo>
				// ……
				// </subinfo>
				// </info>
				// </infos>
				// 辞退流程：OA辞退流程中的人员，在中集成异动到离职库中，备注“矿工辞退人员”。
				
				PareXmlUtils xmlUtils = new PareXmlUtils(xml);
				ArrayList list = new ArrayList();
				List nodeList = xmlUtils.getNodes("/infos/info");
				

				// 工号字段
				String ghField = SystemConfig
						.getPropertyValue("gonghaofieldname");
				// 是否同步到SAP系统
				String syncField = SystemConfig
						.getPropertyValue("issyncsapfieldname");
				
				for (int i = 0; i < nodeList.size(); i++) {
					Element infos = (Element) nodeList.get(i);
					
					List subList = infos.getChildren("subinfo");
					
					String sendDate = infos.getChildText("send_date");
					ArrayList dataList = new ArrayList();
					for (int j = 0; j < subList.size(); j++) {
						Element e = (Element) subList.get(j);
						
						// 工号
						String ghValue = e.getChildText("user_gh");
						
						// 人员编号及人员库
						Map str = getA0100AndNbaseMap(ghField, ghValue, conn);

						String a0100 = (String) str.get("a0100");
						String nbase = (String) str.get("nbase");

						if (a0100 == null || a0100.length() <= 0) {
							return "1";
						}

						int i9999 = getI9999("A", nbase, a0100, "AA3", conn);
						RecordVo vo = new RecordVo(nbase.toLowerCase() + "aa3");

						// 人员编码
						vo.setString("a0100", a0100);
						// i9999
						vo.setInt("i9999", i9999 + 1);
						// 离职日期
						Date ydDate = DateUtils.getDate(sendDate, "yyyy-MM-dd HH:mm:ss");
						vo.setDate("aa305", ydDate);
						// 离职类型 A4202
						vo.setString("aa307", "0203");
						// 离职原因 AA309
						vo.setString("aa309", "矿工辞退人员");

						if (!addData(vo, conn)) {
							return "1";
						}
						
						String updateSql = "update " +nbase+ "A01 set " + syncField + "='2' where a0100='" + a0100 + "'";
						updateData(updateSql, new ArrayList(), conn);
						
						// 移库
						DbNameBo bo = new DbNameBo(conn);
						bo.moveDataBetweenBase2(a0100, nbase, "Ret", "1");
					}
				}
				
				flag = "0";

			} else if ("SYNC_GZ".equalsIgnoreCase(type)) {// 宁夏伊品工资变更流程
//				 <?xml version="1.0" encoding="GB2312"?>
//				 <infos>
//				 <info>
//				 <main_id>主文档ID</main_id>
//				 <main_subject> OA审批主题</main_subject>
//				 <main_bh> OA审请单编号</main_bh>
//				 <cause_text>变更原因说明</cause_text>
//				 <quest_date>变更请求日期</quest_date>
//				 <send_date>系统发送日期</send_date>
//				 <subinfo>
//				 <xdept_name>部门名称</xdept_name>
//				 <xdept_bh>部门编号</xdept_bh>
//				 <xbg_je>金额</xbg_je>
//				 <cause_bz>变更备注</cause_bz>
//				 </subinfo>
//				 <subinfo>
//				 ……
//				 </subinfo>
//				
//				 </info>
//				 </infos>
				// 工资总额变更流程：OA工资总额变更流程确定的总额，在中可以引用。BA4
				PareXmlUtils xmlUtils = new PareXmlUtils(xml);
				ArrayList list = new ArrayList();
				List nodeList = xmlUtils.getNodes("/infos/info");
				for (int i = 0; i < nodeList.size(); i++) {
					Element infos = (Element) nodeList.get(i);

					// 变更原因
					String reson = infos.getChildText("cause_text");
					// 日期
					Date date = DateUtils.getDate(infos
							.getChildText("quest_date"), "yyyy-MM-dd");

					List subList = infos.getChildren("subinfo");

					ArrayList dataList = new ArrayList();
					for (int j = 0; j < subList.size(); j++) {
						Element e = (Element) subList.get(j);
						RecordVo vo = new RecordVo("ba4");
						// 日期
						vo.setDate("ba401", date);
						// 部门
						vo.setString("ba402", e.getChildText("xdept_name"));
						vo.setString("b0110", e.getChildText("xdept_bh"));
						// 金额
						vo.setNumber("ba403", e.getChildText("xbg_je"));
						// 备注
						vo.setString("ba410", e.getChildText("cause_bz"));
						// 原因
						vo.setString("ba411", reson);

						int i9999 = getI9999("B", "", e
								.getChildText("xdept_bh"), "BA4", conn);
						vo.setInt("i9999", i9999 + 1);

						dataList.add(vo);

					}

					ContentDAO dao = new ContentDAO(conn);
					dao.addValueObject(dataList);
				}

				flag = "0";

			} else if ("SYNC_GW".equalsIgnoreCase(type)) {// 宁夏伊品岗位增减编
//				 <?xml version="1.0" encoding="GB2312"?>
//				 <infos>
//				 <info>
//				 <main_subject> OA审批主题</main_subject>
//				 <main_bh> OA审请单编号</main_bh>
//				 <user_namecn>申请人员中文名</user_namecn>
//				 <type_name>增减编类型</type_name>
//				 <nzw_name>岗位名称</nzw_name>
//				 <nzw_bh>岗位编号</nzw_bh>
//				 <xpeople_num>现人员数</xpeople_num>
//				 <zbpeople_num>增补人员数</zbpeople_num>
//				 <dg_date>到岗日期</dg_date>
//				 <quest_text>增减原因</quest_text>
//				 <jyxc_text>建议薪酬</jyxc_text>
//				 <sex>性别要求</sex>
//				 <age_fw>年龄范围</age_fw>
//				 <marriage_xx>婚姻状况</marriage_xx>
//				 <major_yq>专业要求</major_yq>
//				 <zd_college>最低学历</zd_college>
//				 <gz_jy>工作经验</gz_jy>
//				 <jn_zc>技能职称</jn_zc>
//				 <qt_yq>其它要求</qt_yq>
//				 <send_date>系统发送日期</send_date>
//				 </info>
//				 </infos>
				// 增减编流程：OA中增减编流程通过后，在中可以实现编制的汇总和更新。

				String sql = "select str_value from constant where constant='PS_WORKOUT'";
				ContentDAO dao = new ContentDAO(conn);
				rs = dao.search(sql);

				String kSet = "";
				String fixNumField = "";

				if (rs.next()) {
					String str = rs.getString("str_value");
					if (str == null || str.length() <= 0) {
						return flag;
					} else {
						String[] strs = str.split("\\|");
						kSet = strs[0];
						if (strs.length < 2 || strs[1] == null
								|| strs[1].length() <= 0) {
							return flag;
						} else {
							fixNumField = strs[1].split(",")[0];
						}
					}
				} else {
					return flag;
				}

				ArrayList list = getMapList(xml, "/infos/info");
				for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					String types = map.get("type_name").toString();
					
					if (!"增编".equals(types) && !"减编".equals(types)) {
						return "0";
					}
					int i9999 = getI9999("K", "", map.get("nzw_bh").toString(),
							kSet, conn);
					RecordVo vo = new RecordVo(kSet);

					// 人员编码
					vo.setString("e01a1", map.get("nzw_bh").toString().trim());
					// i9999
					vo.setInt("i9999", i9999);
					vo = findData(vo, conn);
					if (vo != null) {
						int zbNum = Integer.parseInt(map.get("zbpeople_num")
								.toString());
						int oldNum = vo.getInt(fixNumField.toLowerCase());
						if ("增编".equals(types)) {
							vo
									.setInt(fixNumField.toLowerCase(), oldNum
											+ zbNum);
						} else if ("减编".equals(types)) {
							vo
									.setInt(fixNumField.toLowerCase(), oldNum
											- zbNum);
						}

						if (!updateData(vo, conn)) {
							return "1";
						}

					} else {
						return "1";
					}
				}

				flag = "0";
			}else{    // 人力资源管理系统与流程管理集成
				cat.error("传入XML字符串--------->>>>>" + xml);
				//返回字符串
				StringBuffer retunValue = new StringBuffer();
				boolean istrue = true;
				
				//进入流程管理 先解析传入的xml字符串(主要是获取里面的值,为配置的xml文件赋值) 然后再解析配置的xml文件
				SyncBo bo = new SyncBo(conn);
				File file = bo.getFilePath(type + ".xml");
				
				if (file == null) {
					cat.error("找不到xml文件--------->>>>>在config目录下无法找到" + type + ".xml文件，请将该文件放到config目录下");
					return "在config目录下无法找到" + type + ".xml文件";
				}
				
				PareXmlUtils utils = new PareXmlUtils(file);
				//根节点名称
				String rootName = utils.getTextValue("/proce/parse/rootname");
				if (rootName == null || rootName.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置根节点");
					return type + ".xml中没有配置根节点";
				}
				
				//一个完整记录节点名称
				String recordsName = utils.getTextValue("/proce/parse/recordsname");
				if (recordsName == null || recordsName.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有记录节点");
					return type + ".xml中没有配置记录节点";
				}
				
				//子集节点名称
				String subRecordsName = utils.getTextValue("/proce/parse/subrecords");
				
				//记录ID节点 必须是主集节点(唯一标识)
				String returnPk = utils.getTextValue("/proce/parse/returnpk");
				
				if (returnPk == null || returnPk.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置记录ID节点");
					return type + ".xml中没有配置记录ID节点";
				}
				
				
				//返回XML编码
				String returnCharSet = utils.getTextValue("/proce/parse/returncharset");				
				if (returnCharSet == null || returnCharSet.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置返回值xml编码");
					return type + ".xml中没有配置返回值xml编码";
				}				
				//返回值根节点名称
				String returnRoot = utils.getTextValue("/proce/parse/returnroot");				
				if (returnRoot == null || returnRoot.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置返回值xml根节点");
					return type + ".xml中没有配置返回值xml根节点";
				}				
				//返回记录节点名称
				String returnRecord = utils.getTextValue("/proce/parse/returnrecord");				
				if (returnRecord == null || returnRecord.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置返回值记录节点名称");
					return type + ".xml中没有配置返回值记录节点名称";
				}						
				//返回值的ID节点名称(上处取到的唯一标识 ,一条记录一个唯一标识)
				String returnIdNode = utils.getTextValue("/proce/parse/returnidnode");				
				if (returnIdNode == null || returnIdNode.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置返回值的ID节点名称");
					return type + ".xml中没有配置返回值的ID节点名称";
				}								
				//返回值flag节点名称 ,valueture为成功时的值，valuefalse为失败时的值
				String returnFlagNode = utils.getTextValue("/proce/parse/returnflag");				
				if (returnFlagNode == null || returnFlagNode.length() <= 0) {
					cat.error("配置文件错误--------->>>>>" + type + ".xml中没有配置返回值flag节点名称");
					return type + ".xml中没有配置返回值flag节点名称";
				}				
				//成功时候的返回值
				String returnValueTure = utils.getAttributeValue("/proce/parse/returnflag", "valueture");				
				//失败时候的返回值
				String returnValueFalse = utils.getAttributeValue("/proce/parse/returnflag", "valuefalse");		
				
				//返回值的其它信息节点名称 ，多个节点用分号分割 ，冒号前面部分为xml传入节点名称 ，冒号后面部分为返回xml节点名称
				String returnothernode = utils.getTextValue("/proce/parse/returnothernode");	
				HashMap xmlMap = new HashMap(); // key传入xml节点  value返回xml节点
				if(returnothernode!=null && returnothernode.trim().length()>0)
				{
					String [] otherNodes = returnothernode.split(";");					
					for (int i = 0; i < otherNodes.length; i++)
					{
						String [] otherNode = otherNodes[i].split(":");
						xmlMap.put(otherNode[0], otherNode[1]);
					}
				}
								
				//需要返回的XML字符串
				String erroinfo = "同步成功";
				retunValue.append("<?xml version='1.0' ");
				retunValue.append(" encoding='" + returnCharSet + "'?>");
				retunValue.append("<" + returnRoot + ">");
				
				//解析传入的xml字符串   mainMap---->对应传入主集信息
				Map mainMap = null;
				PareXmlUtils xmlUtils = new PareXmlUtils(xml);
				List mainLists = xmlUtils.getNodes("/"+rootName.trim()+"/"+recordsName);
				List subLists = null;
				List subRecordLists = null;
				Map subMap = null;
				//所有的子集对象
				Map subAllMaps = null;
				Map subAllMap = null;
				Map sqlValueMap = null;
				Map subSqlMap = null;
				ArrayList allSubRecordsList = null;
 				String path = "";
 				Map mainIdPKMap = null;
 				Map subIdPKMap = null;
 				Map mainColumnMap = null;
 				Map subColumnMap = null;
 				String identityName = "";
 				String identityValue = "";
 				
 				boolean isFiltering = false;
 				boolean subIsFitering = false;
 				//移库成功表示
 				//boolean ismove = false;
 				if(null != mainLists &&mainLists.size() > 0 ){
 							//更改JDBC事物的默认提交方式
 							conn.setAutoCommit(false);
							 for(int j = 0 ; j < mainLists.size() ; j ++ ){
								 subAllMaps = new HashMap();
								 subAllMap = new HashMap();
								 sqlValueMap = new HashMap();
								 mainMap = new HashMap();
								 mainIdPKMap = new HashMap();
								 subIdPKMap = new HashMap();
								 subColumnMap = new HashMap();
								 mainColumnMap = new HashMap();
								Element parentElement = (Element)mainLists.get(j);
								List childList = parentElement.getChildren();
								childList = parentElement.getChildren();
								for(int k = 0 ; k < childList.size() ; k ++ ){
									Element childElement = (Element)childList.get(k);
									//判断子集还是主集 
									if(!subRecordsName.equalsIgnoreCase(childElement.getName().trim())){
										mainMap.put(childElement.getName(), childElement.getValue()==null?"":childElement.getValue());
									}else{
										//<subrecords id="A04"> (subId ---->A04)
										//一个subMap代表一个   <subrecords id="A04"> 对象
										allSubRecordsList = new ArrayList();
										String subId = childElement.getAttributeValue("id");
										//subMap.put("subId", subId);
										//一个子集中的所有record
										subLists = childElement.getChildren();
										for(int o = 0 ; o < subLists.size() ; o ++){
											subMap = new HashMap();
											Element subRecordElement = (Element)subLists.get(o);
											List subRecordChildList = subRecordElement.getChildren();
											for(int oo = 0 ; oo < subRecordChildList.size() ; oo ++){
												Element subRecordChildElement = (Element)subRecordChildList.get(oo);
												subMap.put(subRecordChildElement.getName(), subRecordChildElement.getValue()==null?"":subRecordChildElement.getValue());
											}
											allSubRecordsList.add(subMap);
										}
										subAllMaps.put(subId, allSubRecordsList);
									}
								}
								//主集转码后的集合
								HashMap map = new HashMap();
								//子集转码后的集合
								HashMap subCodeMap = new HashMap();
								//读取主集在XML中的配置  获取转码信息存入MAP集合中 后面用的时候方便取
								path = "/proce/handledata/main/value" ;
								map = getTransFromXmlByParam(path,utils, mainMap,subCodeMap,sqlValueMap,"1","", conn);
								//List mainList = utils.getNodes("/proce/handledata/main/value");
								
								//读取XML文件中配置的数据库表与数据库之间映射关系 先得到所有table节点集合
								List mainTableList = utils.getNodes("/proce/mappings/main/table");
								//表名
								String mainTableName = "";
								//执行何种操作(insert/update/delete/insertorupdate)
								String mainTableOpt  = "";
								//数据保护条件,指哪些数据不需要更新
								String mainTableProtectSql = "";
								//数据过滤条件 如: sex='男'  --->> where sex != '男'
								String mainTableFilterSql = "";
								List mainTableChildList = null;
								for(int m = 0 ; m < mainTableList.size() ; m ++ ){
									Element tableElement = (Element)mainTableList.get(m);
									mainTableName = tableElement.getAttributeValue("name");
									mainTableOpt = tableElement.getAttributeValue("opt");
									mainTableProtectSql = tableElement.getAttributeValue("protect_sql");
									mainTableFilterSql = tableElement.getAttributeValue("filter_sql");
									
									RecordVo mainTableVo = new RecordVo(mainTableName);
									//得到table节点下面的孩子节点的集合 遍历
									mainTableChildList = tableElement.getChildren();
									RowSet rss = null;
									for(int n = 0 ; n < mainTableChildList.size() ; n ++){
										Element tableChildElement = (Element)mainTableChildList.get(n);
										//需要操作的列
										String tableChildName = tableChildElement.getAttributeValue("name");
										//值对应的id，值的类型应该与数据库里的字段类型对应a0100.a0100
										String tableChildValueId = tableChildElement.getAttributeValue("valueid");
										//是否为对方传入的唯一标识 Y 是  N 否
										String tableChildIsPk = tableChildElement.getAttributeValue("ispk");
										//主集新增 需要得到数据库中主集必填项 比如A0100最大值加1 在SQL中配置 然后获取
											if( tableChildValueId.toUpperCase().indexOf( ".".toUpperCase()) != -1){
												String [] tableChildValues = tableChildValueId.split("[.]");
												rss = (RowSet)map.get(tableChildValues[0].toString());
												try {
													if(null != rss){
														if(rss.getRow() > 0){
															//如果是主键  则把对应的唯一标识的列以及值 保存起来 主要用于insertorupdate时做判断
															if("Y".equalsIgnoreCase(tableChildIsPk.trim())){
																identityName = tableChildName;
																identityValue = rss.getString(tableChildValues[1]);
																mainIdPKMap.put(identityName, identityValue);
															}
															if("insertorupdate".equalsIgnoreCase(mainTableOpt)){
																if(!"T".equalsIgnoreCase(tableChildIsPk.trim()))
																{
																	if(rss!=null && rss.getString(tableChildValues[1])!=null && rss.getString(tableChildValues[1]).trim().length()>0)
																		mainColumnMap.put(tableChildName, rss.getString(tableChildValues[1]));
																	else
																		mainColumnMap.put(tableChildName, "");
																}
															}
															mainTableVo.setString(tableChildName, rss.getString(tableChildValues[1].toString()));
															sqlValueMap.put(tableChildValues[1], rss.getString(tableChildValues[1]));
														}
													}
												} catch (Exception e) {
													istrue = false;
													erroinfo = "主集--->>从rowset对象中取值时出现异常,事物回滚...";
													cat.error("主集--->>从rowset对象中取值时出现异常,事物回滚...");
													e.printStackTrace();
												}
											}else{
												if(!"#&#".equalsIgnoreCase(map.get(tableChildValueId).toString()))
												{
													if("Y".equalsIgnoreCase(tableChildIsPk.trim())){
														identityName = tableChildName;
														if(map!=null && map.get(tableChildValueId)!=null && map.get(tableChildValueId).toString().trim().length()>0)
															identityValue = map.get(tableChildValueId).toString();
														mainIdPKMap.put(identityName, identityValue);
													}
													if("insertorupdate".equalsIgnoreCase(mainTableOpt)){
														if(!"T".equalsIgnoreCase(tableChildIsPk.trim()))
														{														
															if(map!=null && map.get(tableChildValueId)!=null && map.get(tableChildValueId).toString().trim().length()>0)
																mainColumnMap.put(tableChildName, map.get(tableChildValueId).toString());
															else
																mainColumnMap.put(tableChildName, "");														
														}
													}
													if(map!=null && map.get(tableChildValueId)!=null && map.get(tableChildValueId).toString().trim().length()>0)
														mainTableVo.setObject(tableChildName, map.get(tableChildValueId));
													else
														mainTableVo.setString(tableChildName, "");	
												}
											}
									}
									
									try {
										//主集过滤条件
										if(!"".equals(mainTableFilterSql.trim())){
											//之前用的是mainMap 现替换为map map中存的值更完善
											if(isExecuteSql(mainTableFilterSql, map, subMap, sqlValueMap, conn, "1")){
												isFiltering = true;
												cat.error("主集--->>数据过滤条件:'" + mainTableFilterSql + "'成立,本条记录不执行任何操作...");
											}
										}
										//主集保护条件
										if(!"".equals(mainTableProtectSql)){
											if(isProtecting(mainTableProtectSql, mainTableName, mainIdPKMap, conn)){
												isFiltering = true;
												cat.error("主集--->>数据保护条件:'" + mainTableProtectSql + "'成立,本条记录不执行任何操作...");
											}
										}
									} catch (Exception e) {
										cat.error("主集--->>执行数据过滤条件/数据保护条件时出现异常,事物回滚...");
										erroinfo = "主集--->>执行数据过滤条件/数据保护条件时出现异常,事物回滚...";
										istrue = false;
										e.printStackTrace();
									}
									
									ContentDAO dao = new ContentDAO(conn);
									try {
										int num = 0 ;
										if(!isFiltering){
										//新增/修改/删除/新增OR修改
										if("insert".equalsIgnoreCase(mainTableOpt.trim())){
											num = dao.addValueObject(mainTableVo);
											cat.debug("主集--->>执行新增操作,新增了" + num + "条数据...");
										}else if("update".equalsIgnoreCase(mainTableOpt.trim())){
											num = dao.updateValueObject(mainTableVo);
											cat.debug("主集--->>执行修改操作,修改了" + num + "条数据...");
										}else if("delete".equalsIgnoreCase(mainTableOpt.trim())){
											num = dao.deleteValueObject(mainTableVo);
											cat.debug("主集--->>执行删除操作,删除了" + num + "条数据...");
										}else if("insertorupdate".equalsIgnoreCase(mainTableOpt.trim())){
											//新增or修改的时候 根据对方的唯一标识进数据库中查询 如果存在 则更新 不存在 则新增
											if(identityName!=null && identityName.trim().length()>0 && identityValue!=null && identityValue.trim().length()>0)
											{
												try 
												{
													//存在
													if(this.isExist(mainTableName, mainIdPKMap, conn)){
														if(mainIdPKMap.size() > 0){
															num = updateInfoByMap(mainTableName, mainColumnMap, mainIdPKMap, conn);
															//num = dao.updateValueObject(mainTableVo);
															cat.debug("主集--->>执行新增或修改操作,已经存在当前记录,执行修改操作,修改了" + num + "条数据...");
														}else{
															cat.error("主集--->>没有配置唯一标识,无法进行insertorupdate操作");
														}
													}else{
														num = dao.addValueObject(mainTableVo);
														cat.debug("主集--->>执行新增或修改操作,没有存在当前记录,执行新增操作,新增了" + num + "条数据...");
													}
												} catch (Exception e) {
													istrue = false;
													cat.error("主集--->>执行新增或修改操作,出现异常,事物回滚...");
													erroinfo = "主集--->>执行新增或修改操作,出现异常,事物回滚...";
													e.printStackTrace();
												}
											}else{
												cat.error(mainTableName + "------->>>新增或者修改时唯一标识为空,无法进行'insertorupdate'操作");
											}
										}
										}
									} catch (Exception e2) {
										istrue = false;
										cat.error("主集--->>执行'"+mainTableOpt+"'操作时,抛出异常,事物回滚...");
										erroinfo = "主集--->>执行'"+mainTableOpt+"'操作时,抛出异常,事物回滚...";
										e2.printStackTrace();
										/*try {
											if(conn != null){
												conn.rollback(); //出现sql异常 回滚事物
											}
										} catch (Exception e) {
											e.printStackTrace();
										}*/
									}
								}
								
								//System.out.println("map:"+map);
								//System.out.println("mainMap:" + mainMap);
								//System.out.println("sqlValueMap:" + sqlValueMap);
								
								//读取XML文件中配置的需要执行的sql语句 如果需要执行 则执行
								if(istrue){
									List mainSqlList = utils.getNodes("/proce/sqls/main/sql");
									if( null != mainSqlList && mainSqlList.size() > 0 ){
										for(int i = 0 ; i < mainSqlList.size() ; i ++){
											Element sqlElement = (Element)mainSqlList.get(i);
											String mainSqlCond = sqlElement.getAttributeValue("cond");
												try {
													//是否需要执行sql语句
													//之前用的是mainMap 现替换为map map中存的值更完善
													if(isExecuteSql(mainSqlCond, map, subMap, sqlValueMap, conn, "1")){
														String mainSql = sqlElement.getText();
														//执行主集里面的sql语句
														if(initExecuteSql(mainSql, map, subMap, sqlValueMap,new HashMap(), conn)){
															cat.debug("主集--->>'" + mainSql + "' 执行成功...");
														}else{
															cat.error("主集--->>'" + mainSql + "' 执行失败...");
														}
													}else{
														cat.error("主集--->>'"+mainSqlCond+"'条件不成立,不执行配置的sql语句...");
													}
												} catch (Exception e) {
													cat.error("主集--->>执行判断是否执行sql语句/执行主集里面的sql语句时出现异常,事物回滚...");
													erroinfo = "主集--->>执行判断是否执行sql语句/执行主集里面的sql语句时出现异常,事物回滚...";
													istrue = false;
													e.printStackTrace();
												}
											if(!istrue){
												break;
											}
										}
									}
								}else{
									cat.error("主集--->>istrue:false--->>所以配置需要执行的sql语句不执行,事物回滚...");
									erroinfo = "主集--->>istrue:false--->>所以配置需要执行的sql语句不执行,事物回滚...";
								}
								
								//如果是请假流程 则读取更改假期管理
								List vacationList = utils.getNodes("/proce/vacation/main/execute");
								if(vacationList != null && vacationList.size() > 0){
									for( int i = 0 ; i < vacationList.size() ; i ++ ){
										Element vacationElement = (Element)vacationList.get(i);
										String vacationCond = vacationElement.getAttributeValue("conf");
										try {
											//之前用的是mainMap 现替换为map map中存的值更完善
											if(isExecuteSql(vacationCond, map, subMap, sqlValueMap, conn, "1")){
												String a0100 = vacationElement.getAttributeValue("a0100");
												String nbase = vacationElement.getAttributeValue("nbase");
												String sels = vacationElement.getAttributeValue("sels");
												String start = vacationElement.getAttributeValue("startStr");
												String end = vacationElement.getAttributeValue("endStr");
												String b0110 = vacationElement.getAttributeValue("b0110");
												String q1501 = vacationElement.getAttributeValue("q1501");
												if(!"".equals(a0100.trim()) && !"".equals(nbase.trim())
													&&!"".equals(sels.trim()) && !"".equals(start.trim()) &&
													!"".equals(end.trim()) && !"".equals(b0110.trim()) && !"".equals(q1501.trim())){
													float leave_time = 	vacationOperating(a0100, nbase, sels, start, end, b0110, q1501, map, sqlValueMap, conn);
													/*System.out
															.println("leave_time:"+leave_time);*/
													if("NaN".equalsIgnoreCase(leave_time+"".trim())){
														istrue = false;
														cat.error("请假流程-->>'" + a0100+"'没有排班,流程回滚...");
														erroinfo = "请假流程-->>'" + a0100+"'没有排班,流程回滚...";
													//	System.out
													//			.println("请假流程-->>'" + a0100+"'没有排班,流程回滚...");
													}
												}else{
													cat.error("请假流程-->>配置参数有空值,不执行操作...");
												}
											}
										} catch (Exception e) {
											istrue = false;
											cat.error("请假流程-->>执行操作时出现异常,事物回滚...");
											erroinfo = "请假流程-->>执行操作时出现异常,事物回滚...";
											e.printStackTrace();
										}
									}
								}else{
									cat.error("主集-->>没有配置更改假期管理...");
								}
								
								if(!istrue){
									retunValue.append("<" + returnRecord + ">");									
									Set set = xmlMap.keySet();
									java.util.Iterator te = set.iterator();
									while (te.hasNext()) 
									{
										String strKey = (String) te.next(); // 键值key传入xml节点  
										String strValue = (String) xmlMap.get(strKey); // value返回xml节点
										
										retunValue.append("<" + strValue + ">");
										if(map!=null && map.get(strKey)!=null && map.get(strKey).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(strKey).toString()))
											retunValue.append(map.get(strKey).toString());
										else
											retunValue.append("");
										retunValue.append("</" + strValue + ">");
									}									
									retunValue.append("<" + returnIdNode + ">");
									if(map!=null && map.get(returnPk)!=null && map.get(returnPk).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(returnPk).toString()))
										retunValue.append(map.get(returnPk).toString());
									else
										retunValue.append("");									
									retunValue.append("</" + returnIdNode + ">");
									retunValue.append("<" + returnFlagNode + ">");
									if(istrue){
										retunValue.append(returnValueTure);
									}else{
										retunValue.append(returnValueFalse);
									}
									retunValue.append("</" + returnFlagNode + ">");									
									retunValue.append("<erroinfo>");
									retunValue.append(erroinfo);
									retunValue.append("</erroinfo>");									
									retunValue.append("</" + returnRecord + ">");
									break;
								}
								
								//如果子集定义了节点 则读取子集转换类型 没有则不读取
									List subList = utils.getNodes("/proce/mappings/sub");
									path = "/proce/handledata/sub";
									//XML的sub节点
										for(int jj = 0 ; jj < subList.size() ; jj ++ ){
											if(!istrue){
												break;
											}
											Element subSubNode = (Element)subList.get(jj);
											//XML每个sub的ID 
											String subId = subSubNode.getAttributeValue("id");
											//一个主集里面的子集里面的所有Record
											allSubRecordsList = (ArrayList)subAllMaps.get(subId);
											//subAllMap = (HashMap)subAllMaps.get(subId);
											if( null != allSubRecordsList && allSubRecordsList.size() > 0 ){
											for( int sb = 0 ; sb < allSubRecordsList.size() ; sb ++ ){
												if(!istrue){
													break;
												}
											Map	subHashMap = (HashMap)allSubRecordsList.get(sb);
											//之前用的是mainMap 现替换为map map中存的值更完善
											subCodeMap = getTransFromXmlByParam(path, utils, map, subHashMap, sqlValueMap,"2",subId, conn);
											List subTableList = subSubNode.getChildren();
											for(int ii = 0 ; ii < subTableList.size() ; ii ++ ){
												subSqlMap = new HashMap();
												subIdPKMap = new HashMap();
												subColumnMap = new HashMap();
												if(!istrue){
													break;
												}
												Element subTableElement = (Element)subTableList.get(ii);
												mainTableName = subTableElement.getAttributeValue("name");
												mainTableOpt = subTableElement.getAttributeValue("opt");
												mainTableProtectSql = subTableElement.getAttributeValue("protect_sql");
												mainTableFilterSql = subTableElement.getAttributeValue("filter_sql");
												RecordVo subTableVo = new RecordVo(mainTableName);
												mainTableChildList = subTableElement.getChildren();
												if(istrue){
													for(int x = 0 ; x < mainTableChildList.size() ; x ++){
														Element tableChildElement = (Element)mainTableChildList.get(x);
														//需要操作的列
														String tableChildName = tableChildElement.getAttributeValue("name");
														//值对应的id，值的类型应该与数据库里的字段类型对应
														String tableChildValueId = tableChildElement.getAttributeValue("valueid");
														//是否为主键，Y为是，N为否，默认值为N。主键不必与数据库表中的主键对应，只要可以唯一确定一条记录即可
														String tableChildIsPk = tableChildElement.getAttributeValue("ispk");
															//如果有引入sql类型的数据
															if(tableChildValueId.toUpperCase().indexOf(".".toUpperCase()) != -1){
																String [] tableChildIds = tableChildValueId.split("[.]");
																//主集里面使用的sql存入的所有值 可根据键取出对应值使用
																//if(sqlValueMap != null &&sqlValueMap.size() > 0 ){
																	if(null != sqlValueMap.get(tableChildIds[0])){
																		subTableVo.setObject(tableChildIds[0], sqlValueMap.get(tableChildIds[0]));
																		if("Y".equalsIgnoreCase(tableChildIsPk.trim())){
																			identityName = tableChildIds[0];
																			identityValue = sqlValueMap.get(tableChildIds[0]).toString();
																			subIdPKMap.put(identityName, identityValue);
																		}
																	}else{
																		RowSet subRs = (RowSet)subCodeMap.get(tableChildIds[0]);
																		try {
																			if(null != subRs){
																				if( subRs.getRow() > 0 ){
																					//带点的参数全部放到sqlValueMap中去 取的时候根据键来取值
																					subSqlMap.put(tableChildIds[1], subRs.getString(tableChildIds[1]));
																					subTableVo.setString(tableChildName, subRs.getString(tableChildIds[1]));
																					if("Y".equalsIgnoreCase(tableChildIsPk.trim())){
																						identityName = tableChildName;
																						identityValue = subRs.getString(tableChildIds[1]);
																						subIdPKMap.put(identityName, identityValue);
																					}
																					if("insertorupdate".equalsIgnoreCase(mainTableOpt.trim())){
																						if(!"T".equalsIgnoreCase(tableChildIsPk.trim()))
																						{
																							if(subRs!=null && subRs.getString(tableChildIds[1])!=null && subRs.getString(tableChildIds[1]).toString().trim().length()>0)
																								subColumnMap.put(tableChildName,subRs.getString(tableChildIds[1]));
																							else
																								subColumnMap.put(tableChildName,"");
																						}
																					}
																				}
																			}
																		} catch (Exception e) {
																			istrue = false;
																			cat.error("子集--->>从rowset对象中取值时,出现异常,事物回滚...");
																			erroinfo = "子集--->>从rowset对象中取值时,出现异常,事物回滚...";
																			e.printStackTrace();
																		}
																	}
															}else
															{
																if(!"#&#".equalsIgnoreCase(subCodeMap.get(tableChildValueId).toString()))
																{
																	subTableVo.setObject(tableChildName, subCodeMap.get(tableChildValueId));
																	if("Y".equalsIgnoreCase(tableChildIsPk.trim())){
																		identityName = tableChildName;
																		identityValue = subCodeMap.get(tableChildValueId).toString();
																		subIdPKMap.put(identityName, identityValue);
																	}
																	if("insertorupdate".equalsIgnoreCase(mainTableOpt.trim())){
																		if(!"T".equalsIgnoreCase(tableChildIsPk.trim()))
																		{
																			if(subCodeMap!=null && subCodeMap.get(tableChildValueId)!=null && subCodeMap.get(tableChildValueId).toString().trim().length()>0)
																				subColumnMap.put(tableChildName,subCodeMap.get(tableChildValueId).toString());
																			else
																				subColumnMap.put(tableChildName,"");																	
																		}
																	}
																}else{
																	cat.error("子集---->>subCodeMap为空,取不到key对应的值...");
																}
															}
													}
													
													try {
														//子集过滤条件
														if(!"".equals(mainTableFilterSql)){
															//之前用的是mainMap 现替换为map map中存的值更完善
															if(isExecuteSql(mainTableFilterSql, map, subMap, sqlValueMap, conn, "2")){
																cat.error("子集--->>过滤条件'" + mainTableFilterSql + "'成立,本条数据不执行任何操作...");
																subIsFitering = true;
															}
														}
														//子集保护条件
														if(!"".equals(mainTableProtectSql)){
															if(isProtecting(mainTableProtectSql, mainTableName, subIdPKMap, conn)){
																cat.error("子集--->>保护条件'" + mainTableProtectSql + "'成立,本条数据不执行任何操作...");
																subIsFitering = true;
															}
														}
													} catch (Exception e) {
														cat.error("子集--->>子集过滤条件/子集保护条件异常,事物回滚...");
														erroinfo = "子集--->>子集过滤条件/子集保护条件异常,事物回滚...";
														istrue = false;
														e.printStackTrace();
													}
													
													ContentDAO dao = new ContentDAO(conn);
													try {
														//如果过滤/保护条件不成立 则执行操作  成立 则不执行表操作
														if(!subIsFitering){
															int num = 0 ;
															if("insert".equalsIgnoreCase(mainTableOpt.trim())){
																	num = dao.addValueObject(subTableVo);
																	cat.debug("子集--->>执行新增操作,新增了" + num + "条记录...");
															}else if("update".equalsIgnoreCase(mainTableOpt.trim())){
																	num = dao.updateValueObject(subTableVo);
																	cat.debug("子集--->>执行修改操作,修改了" + num + "条记录...");
															}else if("delete".equalsIgnoreCase(mainTableOpt.trim())){
																	num = dao.deleteValueObject(subTableVo);
																	cat.debug("子集--->>执行删除操作,删除了" + num + "条记录...");
															}else if("insertorupdate".equalsIgnoreCase(mainTableOpt.trim())){
																//新增or修改的时候 根据对方的唯一标识进数据库中查询 如果存在 则更新 不存在 则新增
																if(null != subIdPKMap && subIdPKMap.size() > 0){
																	try {
																		if(this.isExist(mainTableName, subIdPKMap, conn)){
																			//num = dao.updateValueObject(subTableVo);
																			if(subIdPKMap.size() > 0){
																				num = updateInfoByMap(mainTableName, subColumnMap, subIdPKMap, conn);
																				cat.debug("子集--->>执行新增或修改操作,当前记录已经存在,执行新增操作,新增了" + num + "条记录...");
																			}else{
																				cat.error("子集--->>没有配置'" + mainTableName + "'表的唯一标识,不执行insertorupdate操作...");
																			}
																		}else{
																			num = dao.addValueObject(subTableVo);
																			cat.debug("子集--->>执行新增或修改操作,当前记录不存在,执行修改操作,修改了" + num + "条记录...");
																		}
																	} catch (Exception e) {
																		istrue = false;
																		cat.error("子集--->>执行判断是否存在(根据对方传入的唯一标识)/新增或修改操作,出现异常,事物回滚...");
																		erroinfo = "子集--->>执行判断是否存在(根据对方传入的唯一标识)/新增或修改操作,出现异常,事物回滚...";
																		e.printStackTrace();
																	}
																	//存在 
																}else{
																	cat.error("子集-"+mainTableName+"->>唯一标识为Y的为空,不执行增删改等操作...");
																}
															}
														}
													} catch (Exception e2) {
														cat.error("子集-" + mainTableName + "->>执行'" + mainTableOpt + "' 操作时候出现异常,事物回滚...");
														erroinfo = "子集-" + mainTableName + "->>执行'" + mainTableOpt + "' 操作时候出现异常,事物回滚...";
														istrue = false;
														e2.printStackTrace();
														/*try {
															if(conn != null){
																conn.rollback(); //出现sql异常 回滚事物
															}
														} catch (Exception e) {
															e.printStackTrace();
														}*/
												}
											}
											
												//读取子集需要执行的sql 可能有多个子集
												List subSqlList = utils.getNodes("/proce/sqls/sub");
												for( int i = 0 ; i < subSqlList.size() ; i ++ ){
													Element subSqlElement = (Element)subSqlList.get(i);
													String subIds = subSqlElement.getAttributeValue("id");
													if(subId.equalsIgnoreCase(subIds)){
													ArrayList subSqlArrayList = (ArrayList)subAllMaps.get(subIds);
													//一个子集所有的sql
													List childSubSqls = subSqlElement.getChildren();
													//if(null != subSqlArrayList && subSqlArrayList.size() > 0){
														//for( int sub = 0 ; sub < subSqlArrayList.size() ; sub ++ ){
															//Map subSqlValueMap = (HashMap)subSqlArrayList.get(sub);
															for(int k = 0 ; k < childSubSqls.size() ; k ++ ){
																//一个子集的每一条sql
																Element childSubeElement = (Element)childSubSqls.get(k);
																String subSqlCond = childSubeElement.getAttributeValue("cond");
																try {
																	//是否执行sql语句
																	//之前用的是mainMap 现替换为map map中存的值更完善
																	if(isExecuteSql(subSqlCond, map, subCodeMap, sqlValueMap, conn, "2")){ //subSqlValueMap --->subhashmap
																		String subSqlValue = childSubeElement.getText();
																		//执行子集里面的sql
																		if(initExecuteSql(subSqlValue, map, subCodeMap, sqlValueMap,subSqlMap, conn)){
																			cat.debug("子集ID:" + subIds + "的第" + (ii+1) + "条记录执行成功...");
																		}else{
																			cat.error("子集ID:" + subIds + "的第" + (ii+1) + "条记录执行失败...");
																		}
																	}else{
																		cat.error("子集ID:-" + subIds + "的第" + (ii+1) + "条记录不执行sql语句...");
																	}
																} catch (Exception e) {
																	cat.error("子集ID:-" + subIds + "的第" + (ii+1) + "条记录执行 是否执行sql语句/执行子集里面的sql语句操作时,出现异常,事物回滚...");
																	erroinfo = "子集ID:-" + subIds + "的第" + (ii+1) + "条记录执行 是否执行sql语句/执行子集里面的sql语句操作时,出现异常,事物回滚...";
																	istrue = false;
																	e.printStackTrace();
																}
															}
															if(!istrue){
																break;
															}
													//	}
													//}
												}	
												}
										}//
									}
								}
							}
										
										if(!istrue){
											retunValue.append("<" + returnRecord + ">");
											Set set = xmlMap.keySet();
											java.util.Iterator te = set.iterator();
											while (te.hasNext()) 
											{
												String strKey = (String) te.next(); // 键值key传入xml节点  
												String strValue = (String) xmlMap.get(strKey); // value返回xml节点
												
												retunValue.append("<" + strValue + ">");
												if(map!=null && map.get(strKey)!=null && map.get(strKey).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(strKey).toString()))
													retunValue.append(map.get(strKey).toString());
												else
													retunValue.append("");												
												retunValue.append("</" + strValue + ">");
											}
											retunValue.append("<" + returnIdNode + ">");
											if(map!=null && map.get(returnPk)!=null && map.get(returnPk).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(returnPk).toString()))
												retunValue.append(map.get(returnPk).toString());
											else
												retunValue.append("");											
											retunValue.append("</" + returnIdNode + ">");
											retunValue.append("<" + returnFlagNode + ">");
											if(istrue){
												retunValue.append(returnValueTure);
											}else{
												retunValue.append(returnValueFalse);
											}
											retunValue.append("</" + returnFlagNode + ">");
											retunValue.append("<erroinfo>");
											retunValue.append(erroinfo);
											retunValue.append("</erroinfo>");																					
											retunValue.append("</" + returnRecord + ">");
											break;
										}
										
										/*
										if(!istrue){
											retunValue.append("<" + returnRecord + ">");
											retunValue.append("<" + returnIdNode + ">");
											retunValue.append(map.get(returnPk).toString());
											retunValue.append("</" + returnIdNode + ">");
											retunValue.append("<" + returnFlagNode + ">");
											if(istrue){
												retunValue.append(returnValueTure);
											}else{
												retunValue.append(returnValueFalse);
											}
											retunValue.append("</" + returnFlagNode + ">");
											retunValue.append("</" + returnRecord + ">");
											break;
										}*/
										
										//主集子集移库
										if(istrue){
											//读取XML文件中配置的移库信息 主集移库
											List mainMoveList = utils.getNodes("/proce/javacode/main/move");
											if( null != mainMoveList && mainMoveList.size() > 0 ){
												for( int i = 0 ; i < mainMoveList.size() ; i ++ ){
													Element mainMoveElement = (Element)mainMoveList.get(i);
													String mainMoveCond = mainMoveElement.getAttributeValue("conf");
													//是否需要执行移库操作
													//之前用的是mainMap 现替换为map map中存的值更完善
													if(isExecuteSql(mainMoveCond, map, subMap, sqlValueMap, conn, "1")){
														String mainFrom = mainMoveElement.getAttributeValue("from");
														String mainTo = mainMoveElement.getAttributeValue("to");
														String a0100 = mainMoveElement.getAttributeValue("a0100");
														String mainFormStr = "";
														String mainToStr = "";
														String a0100Str = "";
														if(!"".equals(mainFrom.trim()) && 
																!"".equals(mainTo.trim()) &&
																!"".equals(a0100.trim())){
															//有冒号的表示有引用XML中配置的值
															if(mainFrom.toUpperCase().indexOf(":".toUpperCase()) != -1){
																if(mainFrom.toUpperCase().indexOf(".".toUpperCase()) != -1){
																	String [] mainForms = mainFrom.substring(1,mainFrom.length()).split(",");
																	mainFormStr = sqlValueMap.get(mainForms[0]).toString();
																}else{
																	//之前用的是mainMap 现替换为map map中存的值更完善
																	mainFormStr = map.get(mainFrom.substring(1,mainFrom.length())).toString();
																}
															}else{
																mainFormStr = mainFrom;
															}
															
															if(mainTo.toUpperCase().indexOf(":".toUpperCase()) != -1){
																if(mainTo.toUpperCase().indexOf(".".toUpperCase()) != -1){
																	String [] mainTos = mainTo.substring(1,mainTo.length()).split(",");
																	mainToStr = sqlValueMap.get(mainTos[0]).toString();
																}else{
																	//之前用的是mainMap 现替换为map map中存的值更完善
																	mainToStr = map.get(mainTo.substring(1,mainTo.length())).toString();
																}
															}else{
																mainToStr = mainTo;
															}
															
															if(a0100.toUpperCase().indexOf(":".toUpperCase()) != -1){
																if(a0100.toUpperCase().indexOf(".".toUpperCase()) != -1){
																	String [] a0100s = a0100.substring(1,a0100.length()).split("[.]");
																	a0100Str = sqlValueMap.get(a0100s[0]).toString();
																}else{
																	//之前用的是mainMap 现替换为map map中存的值更完善
																	a0100Str = map.get(a0100.substring(1,a0100.length())).toString();
																}
															}else{
																a0100Str = a0100;
															}
															//移库操作
															DbNameBo dbNameBo = new DbNameBo(conn);
															String userName = dbNameBo.moveDataBetweenBase2(a0100Str, mainFormStr, mainToStr, "1");
															if(!"".equals(userName.trim())){
																//ismove = true;
																istrue = true;
															}/*else{
																ismove = true;
																istrue = false;
															}*/
															break;
														}
													}else{
														cat.error("主集--->>第" + (i+1) + "条移库操作条件不成立,不执行移库操作...");
													}
												}
											}
										}
										
										//如果移库成功 则不继续执行下面的操作
										/*if(ismove){
											retunValue.append("<" + returnRecord + ">");
											retunValue.append("<" + returnIdNode + ">");
											retunValue.append(map.get(returnPk).toString());
											retunValue.append("</" + returnIdNode + ">");
											retunValue.append("<" + returnFlagNode + ">");
											if(istrue){
												retunValue.append(returnValueTure);
											}else{
												retunValue.append(returnValueFalse);
											}
											break;
										}*/
										
									/*	if(istrue){
										//如果子集没有配置移库信息 主集配置了移库信息 读取子集移库信息 是否操作
										List subMoveList = utils.getNodes("/proce/javacode/sub");
											if( null != subMoveList && subMoveList.size() > 0 ){
												for( int i = 0 ; i < subMoveList.size() ; i ++ ){
													Element subMoveElement = (Element)subMoveList.get(i);
													String subId = subMoveElement.getAttributeValue("id");
													List subChidMoveList = subMoveElement.getChildren();
													if( null != subChidMoveList && subChidMoveList.size() > 0 ){
														for( int k = 0 ; k < subChidMoveList.size() ; k ++ ){
															Element childSubMoveElement = (Element)subChidMoveList.get(k);
															String subMoveConf = childSubMoveElement.getAttributeValue("conf");
															//是否需要执行移库操作
															if(isExecuteSql(subMoveConf, mainMap, subMap, sqlValueMap, conn, "1")){
																String subFrom = childSubMoveElement.getAttributeValue("from");
																String subTo = childSubMoveElement.getAttributeValue("to");
																String a0100 = childSubMoveElement.getAttributeValue("a0100");
																String subFormStr = "";
																String subToStr = "";
																String a0100Str = "";
																if(!"".equals(subFrom.trim()) && 
																		!"".equals(subTo.trim()) &&
																		!"".equals(a0100.trim())){
																	//有冒号的表示有引用XML中配置的值
																	if(subFrom.toUpperCase().indexOf(":".toUpperCase()) != -1){
																		if(subFrom.toUpperCase().indexOf(".".toUpperCase()) != -1){
																			String [] subForms = subFrom.substring(1,subFrom.length()).split(",");
																			subFormStr = sqlValueMap.get(subForms[0]).toString();
																		}else{
																			subFormStr = mainMap.get(subFrom.substring(1,subFrom.length())).toString();
																		}
																	}else{
																		subFormStr = subFrom;
																	}
																	
																	if(subTo.toUpperCase().indexOf(":".toUpperCase()) != -1){
																		if(subTo.toUpperCase().indexOf(".".toUpperCase()) != -1){
																			String [] mainTos = subTo.substring(1,subTo.length()).split(",");
																			subToStr = sqlValueMap.get(mainTos[0]).toString();
																		}else{
																			subToStr = mainMap.get(subTo.substring(1,subTo.length())).toString();
																		}
																	}else{
																		subToStr = subTo;
																	}
																	
																	if(a0100.toUpperCase().indexOf(":".toUpperCase()) != -1){
																		if(a0100.toUpperCase().indexOf(".".toUpperCase()) != -1){
																			String [] a0100s = a0100.substring(1,a0100.length()).split("[.]");
																			a0100Str = sqlValueMap.get(a0100s[0]).toString();
																		}else{
																			a0100Str = mainMap.get(a0100.substring(1,a0100.length())).toString();
																		}
																	}else{
																		a0100Str = a0100;
																	}
																	//移库操作
																	DbNameBo dbNameBo = new DbNameBo(conn);
																	String userName = dbNameBo.moveDataBetweenBase2(a0100Str, subFormStr, subToStr, "1");
																	break;
																}
															}else{
																cat.error("子集--->>第" + (k+1) + "条移库操作条件不成立,不执行移库操作...");
															}
														}
													}
												}
											}
										//}
								}*/
								retunValue.append("<" + returnRecord + ">");
								Set set = xmlMap.keySet();
								java.util.Iterator te = set.iterator();
								while (te.hasNext()) 
								{
									String strKey = (String) te.next(); // 键值key传入xml节点  
									String strValue = (String) xmlMap.get(strKey); // value返回xml节点
									
									retunValue.append("<" + strValue + ">");
									if(map!=null && map.get(strKey)!=null && map.get(strKey).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(strKey).toString()))
										retunValue.append(map.get(strKey).toString());
									else
										retunValue.append("");
									retunValue.append("</" + strValue + ">");
								}
								retunValue.append("<" + returnIdNode + ">");
//								System.out.println(returnPk);
								if(map!=null && map.get(returnPk)!=null && map.get(returnPk).toString().trim().length()>0 && !"#&#".equalsIgnoreCase(map.get(returnPk).toString()))
									retunValue.append(map.get(returnPk).toString());
								else
									retunValue.append("");								
								retunValue.append("</" + returnIdNode + ">");
								retunValue.append("<" + returnFlagNode + ">");
								if(istrue){
									conn.setAutoCommit(true);
									conn.commit(); 
//									if("SYNCDATA_LZ".equalsIgnoreCase(type)){
//										retunValue.append("<![CDATA[http://172.16.64.145:9001/CheckLogin?url="+URLEncoder.encode("general/template/edit_form.do?b_query=link&taskid=0&businessModel=0&returnflag=9&sp_flag=1&ins_id=0&tabid=90")+"]]>");
//									}else{
//										retunValue.append(returnValueTure);
//									}
									retunValue.append(returnValueTure);
								}else{
									retunValue.append(returnValueFalse);
								}
								retunValue.append("</" + returnFlagNode + ">");
								retunValue.append("<erroinfo>");
								retunValue.append(erroinfo);
								retunValue.append("</erroinfo>");
								retunValue.append("</" + returnRecord + ">");
							 }
				}
				
				retunValue.append("</" + returnRoot + ">");
				flag = retunValue.toString();
			}
		} catch (Exception e) {
 			e.printStackTrace();

		} finally {
			
			try {
				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		System.out.println("ehr流程返回值xml:"+flag);
		cat.error("ehr流程返回值xml:"+flag);
		return flag;
	}

	// 根据转换代码判断是否存在组织机构
	private boolean isExistUnit(String type,String corCode, Connection conn) 
	{
		boolean flag = false;
		RowSet rs = null;
		try 
		{
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select codeitemid,codesetid from organization where corCode=? and codesetid=? ";
			ArrayList list = new ArrayList();
			list.add(corCode);
			list.add(type);
			rs = dao.search(sql, list);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		return flag;
	}
	
	private String getKqItem(String item_name, Connection conn) {
		RowSet rs = null;
		String item = "";
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search("select item_id from kq_item where item_name='"
					+ item_name + "'");
			if (rs.next()) {
				item = rs.getString("item_id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return item;
	}

	private boolean updateData(String sql, ArrayList list, Connection conn) {
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.update(sql, list);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	private boolean addData(RecordVo vo, Connection conn) {
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.addValueObject(vo);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	private boolean updateData(RecordVo vo, Connection conn) {
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(conn);
			dao.updateValueObject(vo);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	private RecordVo findData(RecordVo vo, Connection conn) {
		boolean flag = false;
		try {
			ContentDAO dao = new ContentDAO(conn);
			vo = dao.findByPrimaryKey(vo);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return vo;
	}

	/**
	 * 获取解析的值
	 * 
	 * @param xml
	 * @param nodePath
	 * @return
	 */
	private ArrayList getMapList(String xml, String nodePath) {
		PareXmlUtils xmlUtils = new PareXmlUtils(xml);
		ArrayList list = new ArrayList();
		List nodeList = xmlUtils.getNodes(nodePath);
		for (int i = 0; i < nodeList.size(); i++) {
			Map map = new HashMap();
			Element el = (Element) nodeList.get(i);
			List li = el.getChildren();
			if (li != null) {
				for (int j = 0; j < li.size(); j++) {
					Element e = (Element) li.get(j);
					String value = e.getText();
					if (value == null) {
						value = "";
					}
					map.put(e.getName(), value);
				}
			}

			list.add(map);
		}

		return list;
	}

	/**
	 * 获得人员编号及人员库
	 * 
	 * @param fieldName
	 * @param fieldValue
	 * @return A0100:nbase
	 */
	private Map getA0100AndNbaseMap(String fieldName, String fieldValue,
			Connection conn) {
		RowSet rs = null;
		Map str = new HashMap();
		try {
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select pre from dbname";
			rs = dao.search(sql);
			StringBuffer buff = new StringBuffer();
			ArrayList list = new ArrayList();
			while (rs.next()) {
				buff.append("select a0100,'");
				buff.append(rs.getString("pre"));
				buff.append("' nbase,b0110,e0122,e01a1,a0101 from ");
				buff.append(rs.getString("pre"));
				buff.append("a01 where ");
				buff.append(fieldName);
				buff.append("=?");
				list.add(fieldValue);
				if (!rs.isLast()) {
					buff.append(" union ");
				}
			}

			rs = dao.search(buff.toString(), list);

			if (rs.next()) {

				str.put("a0100", rs.getString("a0100"));
				str.put("nbase", rs.getString("nbase"));
				str.put("b0110", rs.getString("b0110"));
				str.put("e0122", rs.getString("e0122"));
				str.put("e01a1", rs.getString("e01a1"));
				str.put("a0101", rs.getString("a0101"));

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return str;
	}

	/**
	 * 获得i9999
	 * 
	 * @param nbase
	 * @param a0100
	 * @param setid
	 * @param conn
	 * @return
	 */
	private int getI9999(String type, String nbase, String a0100, String setid,
			Connection conn) {
		int i9999 = 1;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			StringBuffer buff = new StringBuffer();
			buff.append("select max(i9999)  i9999 from ");
			if ("A".equalsIgnoreCase(type)) {
				buff.append(nbase);
				buff.append(setid);
			} else if ("B".equalsIgnoreCase(type) || "K".equalsIgnoreCase(type)) {
				buff.append(setid);
			}
			buff.append(" where ");
			if ("A".equalsIgnoreCase(type)) {
				buff.append("a0100='");
			} else if ("B".equalsIgnoreCase(type)) {
				buff.append("b0110='");
			} else if ("K".equalsIgnoreCase(type)) {
				buff.append("e01a1='");
			}
			buff.append(a0100);
			buff.append("'");
			rs = dao.search(buff.toString());
			if (rs.next()) {
				String str = rs.getString("i9999");
				str = str == null ? "0" : str;
				i9999 = Integer.parseInt(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return i9999;
	}

	public String updateEnabled(String userId, String codeitemid, String flag,
			String type) {
		String returnFlag = "1";
		Connection conn = null;
		RowSet rs = null;

		try {
			conn = AdminDb.getConnection();

			// 宁夏伊品与OA系统同步人员
			if ("SYNCHR".equalsIgnoreCase(type)) {

				String hrId = userId;
				// 是否为oa账户的字段
				String isoahrfieldname = SystemConfig
						.getPropertyValue("isoahrfieldname");

				// 工号
				String gonghao = SystemConfig
						.getPropertyValue("gonghaofieldname");

				// 审批通过
				ContentDAO dao = new ContentDAO(conn);

				String a0100 = "";
				String nbase = "";

				StringBuffer sql = new StringBuffer();

				sql.append("select pre from dbname");

				rs = dao.search(sql.toString());
				sql.delete(0, sql.length());
				while (rs.next()) {
					sql.append("select  ");
					sql.append("a0100,");
					sql.append("'");
					sql.append(rs.getString("pre"));
					sql.append("' nbase from ");
					sql.append(rs.getString("pre"));
					sql.append("A01 where ");
					sql.append(gonghao);
					sql.append("='");
					sql.append(hrId);
					sql.append("'");

					if (!rs.isLast()) {
						sql.append(" union ");
					}
				}

				// 查询a0100与nbase
				rs = dao.search(sql.toString());

				if (rs.next()) {
					a0100 = rs.getString("a0100");
					nbase = rs.getString("nbase");
				} else {
					return returnFlag;
				}

				if (a0100 == null || a0100.length() <= 0 || nbase == null
						|| nbase.length() <= 0) {
					return returnFlag;
				}
				String sqls = "update " + nbase + "a01 set " + isoahrfieldname
						+ "='" + flag + "' where a0100='" + a0100 + "'";
				dao.update(sqls);

				sqls = "update t_hr_view set oa=0 where a0100='" + a0100
						+ "' and upper(nbase_0)='" + nbase.toUpperCase() + "'";
				dao.update(sqls);
				returnFlag = "0";
				// sql = "update t_hr_view_log set flag=0 ";
			} else if ("SYNCORG".equalsIgnoreCase(type)) { // 宁夏伊品与OA系统同步机构
				String isoaorgfieldname = SystemConfig
						.getPropertyValue("isoaorgfieldname");

				String orgId = userId;
				
				if (!isExistOrg(orgId, "b0110", conn)) {
					return returnFlag;
				}
				
				

				String sql = "update b01 set " + isoaorgfieldname + "='" + flag
						+ "' where b0110='" + orgId + "'";
				ContentDAO dao = new ContentDAO(conn);
				dao.update(sql);
				sql = "update t_org_view set oa=0 where b0110_0='" + orgId
						+ "'";
				dao.update(sql);

				returnFlag = "0";
			} else {
				return returnFlag;
			}

		} catch (Exception e) {
			e.printStackTrace();
			returnFlag = "1";
		} finally {
			try {

				if (rs != null) {
					rs.close();
				}

				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return returnFlag;
	}
	
	private boolean isExistOrg(String id, String fieldId, Connection conn) {
		boolean flag = false;
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			String sql = "select * from b01 where " + fieldId + "=?";
			ArrayList list = new ArrayList();
			list.add(id);
			rs = dao.search(sql, list);
			if (rs.next()) {
				flag = true;
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	public String getCodeIdByCodeDesc(String codeSetId , String codeDesc, Connection conn){
		String codeId = "";
		StringBuffer sb = new StringBuffer("select codeitemid ");
		if("UM".equalsIgnoreCase(codeSetId) ||
				"UN".equalsIgnoreCase(codeSetId) ||
				"@K".equalsIgnoreCase(codeSetId)){
			sb.append("from organization");
		}else{
			sb.append("from codeitem");
		}
		//sb.append(" select codeitemid from codeitem ");
		sb.append(" where codesetid = '" + codeSetId + "' and ");
		sb.append(" codeitemdesc = '" + codeDesc + "' ");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				codeId = rs.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return codeId;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 *  通过传入的参数 ， 得到XML中配置的转换类型的一个MAP集合
	 *  @param sqlValueMap 主集执行sql查出来的保存到Map中的值
	 * */
	public HashMap getTransFromXmlByParam(String path,PareXmlUtils utils , Map mainMap , Map subMap,Map sqlValueMap,String isMainOrSub,String subId, Connection conn){
		List mainList = utils.getNodes(path);
		HashMap map = new HashMap();
		//主集
		if("1".equalsIgnoreCase(isMainOrSub.trim())){
			for(int i = 0 ; i < mainList.size() ; i ++){
				Element e = (Element)mainList.get(i);
				String mainNodeId = e.getAttributeValue("id");
				String mainNodeType = e.getAttributeValue("type");
				String mainNodeName = e.getAttributeValue("nodename");
				if(mainNodeName!=null && mainMap.get(mainNodeName)==null && !"sql".equalsIgnoreCase(mainNodeType.trim()))
				{
					map.put(mainNodeId, "#&#");
				}else
				{
					if("common".equalsIgnoreCase(mainNodeType.trim())){
						
						if(!"".equalsIgnoreCase(mainNodeName.trim())){
							System.out.println(mainNodeId+"--" + mainMap.get(mainNodeName) );
								map.put(mainNodeId, mainMap.get(mainNodeName));
						}
					}else if("sql".equalsIgnoreCase(mainNodeType.trim())){
						String mainNodeSql = e.getAttributeValue("sql");
						if(!"".equalsIgnoreCase(mainNodeSql.trim())){
							try {
								RowSet rs = getObjectByParam( mainNodeSql, map,map, sqlValueMap, conn);
								if(null != rs){
									if(rs.getRow() > 0 ){
										map.put(mainNodeId, rs);
									}
								}
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
					}else if("idfactory".equalsIgnoreCase(mainNodeType.trim())){
						String mainSequenceName = e.getAttributeValue("sequence_name");
						IDFactoryBean idFactory = new IDFactoryBean();
						if(!"".equalsIgnoreCase(mainSequenceName.trim())){
							String id;
							try {
								id = idFactory.getId(mainSequenceName, "", conn);
								map.put(mainNodeId, id);
							} catch (GeneralException e1) {
								e1.printStackTrace();
							}
						}
					}else if("tocode".equalsIgnoreCase(mainNodeType.trim())){
						String mainNodeSetId = e.getAttributeValue("setid");
						String mainNodeDescId = e.getAttributeValue("descid");
						System.out.println(mainNodeSetId + ":" + mainNodeDescId);
						if(!"".equalsIgnoreCase(mainNodeSetId.trim()) && !"".equalsIgnoreCase(mainNodeDescId.trim())){
							System.out.println(mainNodeSetId + " >>>>"  + mainNodeDescId + "~~~~~" );
							//mainMap 换成 map
							System.out.println(map);
							
							if(map==null || map.get(mainNodeDescId)==null || "#&#".equalsIgnoreCase(map.get(mainNodeDescId).toString()))							
								map.put(mainNodeId, "#&#");															
							else
							{
								String mainStr = map.get(mainNodeDescId).toString();
								if(mainStr==null || mainStr.trim().length()<=0)
									map.put(mainNodeId, "");
								else
									map.put(mainNodeId, getCodeIdByCodeDesc(mainNodeSetId, map.get(mainNodeDescId).toString(), conn));
							}
						}
					}else if("todesc".equalsIgnoreCase(mainNodeType.trim())){
						String mainNodeSetId = e.getAttributeValue("setid");
						String mainCodeId = e.getAttributeValue("codeid");
						if(!"".equalsIgnoreCase(mainNodeSetId.trim()) && !"".equalsIgnoreCase(mainCodeId))
						{
							map.put(mainNodeId, AdminCode.getCodeName(mainNodeSetId, mainMap.get(mainCodeId).toString()));
						}
					}else if("chartodate".equalsIgnoreCase(mainNodeType.trim())){
						String mainNodeFormate = e.getAttributeValue("formate");
						String charId = e.getAttributeValue("charid");
						if(!"".equalsIgnoreCase(mainNodeFormate.trim()) && !"".equalsIgnoreCase(charId.trim()))
						{
							SimpleDateFormat format = new SimpleDateFormat(mainNodeFormate);
							try 
							{							
								if(map!=null && mainMap.get(charId)!=null && mainMap.get(charId).toString().trim().length()>0)							
									map.put(mainNodeId, format.parse(mainMap.get(charId).toString()));							
								else							
									map.put(mainNodeId, null);														
								
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
					}else if("datetochar".equalsIgnoreCase(mainNodeType.trim())){
						String mainNodeFormate = e.getAttributeValue("formate");
						String dateId = e.getAttributeValue("dateid");
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						if(!"".equalsIgnoreCase(mainNodeFormate.trim()) && !"".equalsIgnoreCase(dateId))
						{
							Date date;
							try {
								date = format.parse(dateId);
								format = new SimpleDateFormat(mainNodeFormate);
								map.put(mainNodeId, format.format(date));
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
						}
					}else if("chartoint".equalsIgnoreCase(mainNodeType.trim())){
						String charId = e.getAttributeValue("charid");
						if(!"".equalsIgnoreCase(charId.trim()))
						{
							if(mainMap==null || mainMap.get(charId)==null)							
								map.put(mainNodeId, "#&#");															
							else
							{
								String mainStr = mainMap.get(charId).toString();
								if(mainStr==null || mainStr.trim().length()<=0)
									mainStr = "0";
								if(mainStr!=null && mainStr.trim().length()>0 && mainStr.indexOf(".")!=-1)
									mainStr = mainStr.substring(0,mainStr.indexOf("."));
								
								map.put(mainNodeId, Integer.valueOf(mainStr));
							}
							//map.put(mainNodeId,Integer.parseInt(mainMap.get(charId).toString()) + "");
						}
					}else if("chartofloat".equalsIgnoreCase(mainNodeType.trim())){
						String charId = e.getAttributeValue("charid");
						if(!"".equalsIgnoreCase(charId.trim())){
							
							if(mainMap==null || mainMap.get(charId)==null)							
								map.put(mainNodeId, "#&#");															
							else
							{
								String mainStr = mainMap.get(charId).toString();
								if(mainStr==null || mainStr.trim().length()<=0)
									mainStr = "0.0";
								map.put(mainNodeId, Float.valueOf(mainStr));
							}														
							//map.put(mainNodeId, Float.parseFloat(mainMap.get(charId).toString()) ); JDK1.4用不了 所以换成上面那种方式
						}
					}else if("chartodouble".equalsIgnoreCase(mainNodeType.trim())){
						String charId = e.getAttributeValue("charid");
						if(!"".equalsIgnoreCase(charId.trim())){
							
							if(mainMap==null || mainMap.get(charId)==null)							
								map.put(mainNodeId, "#&#");															
							else
							{
								String mainStr = mainMap.get(charId).toString();
								if(mainStr==null || mainStr.trim().length()<=0)
									mainStr = "0.0";
								map.put(mainNodeId, Double.valueOf(mainStr));
							}
							//map.put(mainNodeId, Double.parseDouble(mainMap.get(charId).toString()) + "");
						}
					}
				}
			}
		}
		else if("2".equalsIgnoreCase(isMainOrSub.trim()))
		{
			for(int i = 0 ; i < mainList.size() ; i ++ )
			{
				Element e = (Element)mainList.get(i);
				String subIds = e.getAttributeValue("id");
				if(subIds.equalsIgnoreCase(subId)){
					List subIdList = e.getChildren();
					for(int j = 0 ; j < subIdList.size() ; j ++ ){
						Element e1 = (Element)subIdList.get(j);
						String subNodeId = e1.getAttributeValue("id");
						String subNodeType = e1.getAttributeValue("type");
						String subNodeName = e1.getAttributeValue("nodename");
						if(subNodeName!=null && subMap.get(subNodeName)==null && !"sql".equalsIgnoreCase(subNodeType.trim()))
						{
							map.put(subNodeId, "#&#");
						}else
						{
							if("common".equalsIgnoreCase(subNodeType.trim())){
								
								if(!"".equalsIgnoreCase(subNodeName.trim())){
									map.put(subNodeId, subMap.get(subNodeName));
								}
							}else if("sql".equalsIgnoreCase(subNodeType.trim())){
								String subNodeSql = e1.getAttributeValue("sql");
								if(!"".equalsIgnoreCase(subNodeSql.trim())){
									try {
										RowSet rs = getObjectByParam( subNodeSql, mainMap,map, sqlValueMap, conn);
										if(null != rs){
											if(rs.getRow() > 0 ){
												map.put(subNodeId, rs);
											}
										}
									} catch (Exception e2) {
										e2.printStackTrace();
									}
								}
							}else if("idfactory".equalsIgnoreCase(subNodeType.trim())){
								String subSequenceName = e1.getAttributeValue("sequence_name");
								IDFactoryBean idFactory = new IDFactoryBean();
								if(!"".equalsIgnoreCase(subSequenceName.trim())){
									String id;
									try {
										id = idFactory.getId(subSequenceName, "", conn);
										map.put(subNodeId, id);
									} catch (GeneralException e3) {
										e3.printStackTrace();
									}
								}
							}else if("tocode".equalsIgnoreCase(subNodeType.trim())){
								String subNodeSetId = e1.getAttributeValue("setid");
								String subNodeDescId = e1.getAttributeValue("descid");
								// mainMap 换成 map JinChunhai 2013.09.26 
								if(!"".equalsIgnoreCase(subNodeSetId.trim()) && !"".equalsIgnoreCase(subNodeDescId.trim()))
								{	
									if(map==null || map.get(subNodeDescId)==null || "#&#".equalsIgnoreCase(map.get(subNodeDescId).toString()))							
										map.put(subNodeId, "#&#");															
									else
									{
										String mainStr = map.get(subNodeDescId).toString();
										if(mainStr==null || mainStr.trim().length()<=0)
											map.put(subNodeId, "");
										else
											map.put(subNodeId, getCodeIdByCodeDesc(subNodeSetId, map.get(subNodeDescId).toString(), conn));
									}
								}																					
							}else if("todesc".equalsIgnoreCase(subNodeType.trim())){
								String subNodeSetId = e1.getAttributeValue("setid");
								String subCodeId = e1.getAttributeValue("codeid");
								if(!"".equalsIgnoreCase(subNodeSetId.trim()) && !"".equalsIgnoreCase(subCodeId))
								{
									map.put(subNodeId, AdminCode.getCodeName(subNodeSetId, mainMap.get(subCodeId).toString()));
								}
							}else if("chartodate".equalsIgnoreCase(subNodeType.trim())){
								String subNodeFormate = e1.getAttributeValue("formate");
								String charId = e1.getAttributeValue("charid");
								if(!"".equalsIgnoreCase(subNodeFormate.trim()) && !"".equalsIgnoreCase(charId.trim()))
								{
									SimpleDateFormat format = new SimpleDateFormat(subNodeFormate);
									try 
									{
										if(mainMap!=null && mainMap.get(charId)!=null && mainMap.get(charId).toString().trim().length()>0)								
											map.put(subNodeId, format.parse(mainMap.get(charId).toString()));
										else
											map.put(subNodeId, null);
											
									} catch (ParseException e4) {
										e4.printStackTrace();
									}
								}
							}else if("datetochar".equalsIgnoreCase(subNodeType.trim())){
								String subNodeFormate = e1.getAttributeValue("formate");
								String dateId = e1.getAttributeValue("dateid");
								SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
								if(!"".equalsIgnoreCase(subNodeFormate.trim()) && !"".equalsIgnoreCase(dateId))
								{
									Date date;
									try {
										date = format.parse(dateId);
										format = new SimpleDateFormat(subNodeFormate);
										map.put(subNodeId, format.format(date));
									} catch (ParseException e5) {
										e5.printStackTrace();
									}
								}
							}else if("chartoint".equalsIgnoreCase(subNodeType.trim())){
								String charId = e1.getAttributeValue("charid");
								if(!"".equalsIgnoreCase(charId.trim())){
										//map.put(subNodeId, Integer.parseInt(mainMap.get(charId).toString()));
									
									if(mainMap==null || mainMap.get(charId)==null)							
										map.put(subNodeId, "#&#");															
									else
									{
										String mainStr = mainMap.get(charId).toString();
										if(mainStr==null || mainStr.trim().length()<=0)
											mainStr = "0";
										if(mainStr!=null && mainStr.trim().length()>0 && mainStr.indexOf(".")!=-1)
											mainStr = mainStr.substring(0,mainStr.indexOf("."));
										
										map.put(subNodeId, Integer.valueOf(mainStr));
									}										
								}
							}else if("chartofloat".equalsIgnoreCase(subNodeType.trim())){
								String charId = e1.getAttributeValue("charid");
								if(!"".equalsIgnoreCase(charId.trim())){
									
									if(mainMap==null || mainMap.get(charId)==null)							
										map.put(subNodeId, "#&#");															
									else
									{
										String mainStr = mainMap.get(charId).toString();
										if(mainStr==null || mainStr.trim().length()<=0)
											mainStr = "0.0";
										map.put(subNodeId, Float.valueOf(mainStr));
									}
									//map.put(subNodeId, Float.parseFloat(mainMap.get(charId).toString()));
								}
							}else if("chartodouble".equalsIgnoreCase(subNodeType.trim())){
								String charId = e1.getAttributeValue("charid");
								if(!"".equalsIgnoreCase(charId.trim())){
									
									if(mainMap==null || mainMap.get(charId)==null)							
										map.put(subNodeId, "#&#");															
									else
									{
										String mainStr = mainMap.get(charId).toString();
										if(mainStr==null || mainStr.trim().length()<=0)
											mainStr = "0.0";
										map.put(subNodeId, Double.valueOf(mainStr));
									}
									//	map.put(subNodeId, Double.parseDouble(mainMap.get(charId).toString()));
								}
							}
						}
					}
				}
			}
		}
		
		return map;
	}
	
	/**
	 * 通过传入的参数 得到一个ROWSET对象
	 * */
	public RowSet getObjectByParam(String sql ,Map mainMap,Map subMap, Map sqlValueMap, Connection conn){
		System.out.println(mainMap);
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			if(sql.toUpperCase().indexOf(":".toUpperCase()) != -1){
				String str = initSql(sql);
				str = str.substring(0,str.length() - 1 );
				String [] strs = str.split(",");
				for(int i = 0 ; i < strs.length ; i ++ ){
					if(strs[i].toUpperCase().indexOf(".".toUpperCase()) != -1){
						String [] ids = strs[i].substring(1,strs[i].length()).split("[.]");
//						if(null != sqlValueMap.get(ids[1])){
//							sql = sql.replace(strs[i], sqlValueMap.get(ids[1]).toString());
//						}else{
//							//System.out.println("没有取到值" + strs[1]);
//							cat.error("得到ROWSET对象时,没有取到值-->>" + strs[1]);
//						}
						
						if(null != mainMap.get(ids[0])){
							ResultSet map = (ResultSet)mainMap.get(ids[0]);
							sql = sql.replace(strs[i], map.getString(ids[1]));
						}if(null != subMap.get(ids[0])){
							ResultSet map = (ResultSet)subMap.get(ids[0]);
							sql = sql.replace(strs[i], map.getString(ids[1]));
						}else{
							//System.out.println("没有取到值" + strs[1]);
							cat.error("得到ROWSET对象时,没有取到值-->>" + strs[1]);
						}
						
					}else{
						String strString = strs[i].toString().substring(1,strs[i].toString().length());
						if(null != mainMap.get(strString)){
							sql = sql.replace(strs[i], mainMap.get(strs[i].substring(1,strs[i].length())).toString());
						}else if(null != subMap.get(strString)){
							sql = sql.replace(strs[i], subMap.get(strs[i].substring(1,strs[i].length())).toString());
						}
					}
				}
				cat.error(sql);
				rs = dao.search(sql);
			}else{
				rs = dao.search(sql);
			}
			if(rs.next()){
				return rs;
			}else{
				cat.error("主集/子集--->>得到的rowset对象为空... sql : " + sql );
				//System.out.println("主集/子集--->>得到的rowset对象为空------>>" + sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析SQL中的条件  赋值
	 * */
	public String initSql(String sql ){
		StringBuffer sb = new StringBuffer();
		if(sql.length() < 0 || sql == null ){
			return null;
		}
		sql = sql.trim();
		sql = sql + "";
		byte [] charStr = sql.getBytes();
		int i = 0 ;
		boolean flag = false;
		boolean isAppend = false;
		while( i < charStr.length ){
			if ((char) (charStr[i]) == ':') {
				flag = true;
			}
			if(flag){
				if ((char) (charStr[i]) == ' ' || (char) (charStr[i]) == '　'
					|| (char) (charStr[i]) == '\''
					|| (char) (charStr[i]) == '}'
					|| (char) (charStr[i]) == '%'
					|| (char) (charStr[i]) == ')'
					|| (char) (charStr[i]) == '('
					|| (char) (charStr[i]) == ','
					|| (char) (charStr[i]) == '='
					|| (char) (charStr[i]) == '>'
					|| (char) (charStr[i]) == '<'
					|| (char) (charStr[i]) == '!'
					|| (char) (charStr[i]) == '+'
					|| (char) (charStr[i]) == '-'
					|| (char) (charStr[i]) == '*'
					|| (char) (charStr[i]) == '/'
					//|| (char) (charStr[i]) == '.' 由于可以调用主集sql执行的对象 所以此处就不能有点
					|| (char) (charStr[i]) == ']'
					|| (char) (charStr[i]) == '\t'
					|| (char) (charStr[i]) == '\r'
					|| (char) (charStr[i]) == '\n'
					) {
				  flag = false;
				  isAppend = true;
			    }else{
			    	sb.append((char)charStr[i]);
			    }
		    }
			if(isAppend){
				sb.append(",");
				isAppend = false;
			}
			i++;
		}
 		return sb.toString();
	}
	
	/**
	 *  传入的唯一标识是否已在库中存在
	 * @throws SQLException 
	 * */
	public boolean isExist(String tableName ,Map idPKMap , Connection conn) throws SQLException{
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		sb.append("select  * from " );
		sb.append(tableName);
		sb.append(" where 1=1 ");
		//sb.append(" where " + column + " = ");
		//Set set = idPKMap.keySet();
		/*for(Object key : set){
			sb.append(" and " + key + " = '");
			sb.append(idPKMap.get(key) + "'");
		}*/
		Iterator it = idPKMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			sb.append(" and " + key + " = '");
			sb.append(value + "'");
		}
		//sb.append(" ' " + value + "'");
		ContentDAO dao = new ContentDAO(conn);
		if(dao.search(sb.toString()).next()){
			flag = true;
		}
		return flag;
	}
   
	/**
	 *  判断是否需要执行sql语句
	 * @throws SQLException 
	 * */
	public boolean isExecuteSql(String cont ,Map mainMap,Map subMap, Map sqlValueMap, Connection conn ,String isMainOrSub) throws SQLException{
		String [] s = new String[2];
		boolean istrue = true;
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		if(cont!=null && cont.trim().length()>0 && !"".equals(cont.trim())){
			//此处以后维护需要加上oracle库判断  JinChunhai 2014.04.29			
			if (Sql_switcher.searchDbServer() == Constant.ORACEL)
				sb.append("select 1 from dual ");  
			else
				sb.append("select 1 ");			
			if(cont.toUpperCase().indexOf(":".toUpperCase()) != -1){
				String str = initSql(cont);
				str = str.substring(0,str.length() - 1 );
				String [] strs = str.split(",");
				for(int i = 0 ; i < strs.length ; i ++ ){
					if(strs[i].toUpperCase().indexOf(".".toUpperCase()) != -1){
						String [] ids = strs[i].substring(1,strs[i].length()).split("[.]");
						cont = cont.replace(strs[i], sqlValueMap.get(ids[1]).toString());
					}else{
						//1 主集  2 子集
						String strString = strs[i].toString().substring(1,strs[i].toString().length());
						if(null != mainMap.get(strString) && null != mainMap.get(strString).toString()){
							cont = cont.replace(strs[i],"'"+ mainMap.get(strs[i].substring(1,strs[i].length())).toString()+"'");
						}else if(null != subMap.get(strString) && null != subMap.get(strString).toString()){
							cont = cont.replace(strs[i], "'"+subMap.get(strs[i].substring(1,strs[i].length())).toString() + "'");
						}else{
							cat.error("主集/子集判断是否需要执行sql语句--->>取值失败");
						}
					}
				}
			}
			sb.append(" where " + cont);
				
			//try {
				ContentDAO dao = new ContentDAO(conn);
				if(dao.search(sb.toString()).next()){
					flag = true;
				}
			//} catch (Exception e) {
			//	istrue = false;
			//	e.printStackTrace();
			//}
		}else{
			//如果为空 则默认执行sql语句
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 	解析需要执行的sql语句 并且执行 抛出异常在外面捕获
	 * */
	public boolean initExecuteSql(String sql , Map mainMap,Map subMap, Map sqlValueMap,Map subSqlMap, Connection conn )throws Exception{
		DbSecurityImpl dbS = new DbSecurityImpl();
		boolean flag = true;
		if(!"".equals(sql.trim())){
			if(sql.toUpperCase().indexOf(":".toUpperCase()) != -1){
				String str = initSql(sql);
				if(",".equalsIgnoreCase(str.substring(str.length()-1, str.length()))){
					str = str.substring(0,str.length() -1 );
				}
				String [] strs = str.split(",");
				for( int i = 0 ; i < strs.length ; i ++ ){
					if(strs[i].toUpperCase().indexOf(".".toUpperCase()) != -1){
						String [] ids = strs[i].substring(1,strs[i].length()).split("[.]");
						//String [] ids = strs[i].split("[.]");
						if(null != sqlValueMap.get(ids[0]) || null != sqlValueMap.get(ids[1])){
							sql = sql.replace(strs[i], sqlValueMap.get(ids[1]).toString());
						}else if(null != subSqlMap.get(ids[1])){
							//子集执行的时候有个问题  比如I9999.I9999 暂时先不管了
							sql = sql.replace(strs[i], subSqlMap.get(ids[1]).toString());
						}else{
							cat.error("主集/子集解析需要执行的sql--->>取值失败");
						}
					}else{
						String strString = strs[i].toString().substring(1,strs[i].toString().length());
						if(null !=  mainMap.get(strs[i].substring(1,strs[i].length()))){
							sql = sql.replace(strs[i], mainMap.get(strs[i].substring(1,strs[i].length())).toString());
						}else if(null != subMap.get(strs[i].substring(1,strs[i].length()))){
							sql = sql.replace(strs[i], subMap.get(strs[i].substring(1,strs[i].length())).toString());
						}else{
							cat.error("主集/子集解析需要执行的sql--->>取值失败");
						}
					}
				}
			}
		//	try {
					ContentDAO dao = new ContentDAO(conn);
					// 打开Wallet
					dbS.open(conn, sql);
					// 此处只执行增删改sql语句 不执行查询语句
					dao.update(sql);
					flag = true;
		//	} catch (Exception e) {
			//	flag = false;
				/*try {
					if(conn != null){
						conn.rollback(); //出现sql异常 回滚事物
					}
				} catch (Exception w) {
					w.printStackTrace();
				}*/
		//		e.printStackTrace();
		//		flag = false;
		//	}
		}else{
			flag = false;
		}
		try {
			// 关闭Wallet
			dbS.close(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	/**
	 * 数据过滤条件
	 * */
	//public boolean isFiltering(String filterSql ){
		
	//}
	
	/**
	 * 数据保护条件
	 * @throws SQLException 
	 * */
	public boolean isProtecting(String protect , String tableName ,Map idPkMap ,Connection conn) throws SQLException{
		boolean flag = false;
		DbSecurityImpl dbS = new DbSecurityImpl();
		StringBuffer sb = new StringBuffer();
		//生成临时表 得兼容oracle库
		sb.append("if exists(");
		sb.append("select * from tempdb.dbo.sysobjects where id = object_id(N'tempdb..##newtable')");
		sb.append("and type='U') \n");
		sb.append("drop table ##newtable \n");
		sb.append(" select * into ##newtable from " + tableName);
		sb.append(" where " + protect);
		ContentDAO dao = new ContentDAO(conn);
		// 打开Wallet
		dbS.open(conn, sb.toString());
		dao.update(sb.toString());
		//通过唯一标识查询临时表中是否有符合条件的内容 如果有 返回true
		StringBuffer str = new StringBuffer();
		str.append("select * from ##newtable where 1=1 ");
		/*jdk1.5
		 * Set set = idPkMap.keySet();
		for(Object key : set){
				str.append(" and " + key +" = ");
				str.append("'" + idPkMap.get(key) + "' ");
		}*/
		Iterator it = idPkMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry)it.next();
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			str.append(" and " + key +" = ");
			str.append("'" + value + "' ");
		}
		//str.append(" where " + identityName + " = ");
		//str.append("'" + identityValue + "'");
		if(dao.search(str.toString()).next()){
			flag = true;
		}
		try {
			// 关闭Wallet
			dbS.close(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}
	
	
	/**
	 * 新增或修改时，修改用sql update
	 * */
	public int updateInfoByMap(String tableName , Map columnMap , Map whereMap, Connection conn)throws SQLException{
		int returnValue = 0;
		StringBuffer sb = new StringBuffer();
		sb.append("update " + tableName);
		sb.append(" set ");
		//Set set = columnMap.keySet();
		/*jdk1.5
		 * for(Object key : set){
			sb.append(key + " = ");
			sb.append("'" + columnMap.get(key) + "' ,");
		}*/
		Iterator it = columnMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			String str = (String)value;
			if("#&#".equalsIgnoreCase(str))
			{
				continue;
			}			
			sb.append(key + " = ");
			if(value==null || "".equals(value) || value.toString().trim().length()<=0)
				sb.append("null ,");
			else
				sb.append("'" + value + "' ,");
		}
		String str = sb.substring(0,(sb.toString().length() - 1));
		StringBuffer s = new StringBuffer();
		s.append(" where 1=1 ");
		it = whereMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next(); 
			Object key = entry.getKey(); 
			Object value = entry.getValue(); 
			s.append(" and " + key + "=");
			s.append("'" + value + "' ");
		}
		/*set = whereMap.keySet();
		for(Object key : set){
			s.append("and " + key + "=");
			s.append("'" + whereMap.get(key) + "'");
		}*/
		ContentDAO dao = new ContentDAO(conn);
		System.out.println("sql================"+str + s);
		returnValue = dao.update(str + s);
		return returnValue;
	}
	
	/**
	 * 请假流程 ， 更改假期管理
	 * */
	public float vacationOperating(String a0100 , String nbase , String sels , String start , String end , String b0110,String q1501 ,Map mainMap , Map sqlValueMap , Connection conn)throws Exception{
		if(a0100.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(a0100.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = a0100.substring(1,a0100.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						a0100 = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值a0100:" + a0100 + "...");
					}
				}
 			}else{
				if(null != mainMap.get(a0100.substring(1,a0100.length()))){
					a0100 = mainMap.get(a0100.substring(1,a0100.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值a0100:" + a0100 + "...");
				}
			}
		}
		if(nbase.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(nbase.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = nbase.substring(1,nbase.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						nbase = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值nbase:" + nbase + "...");
					}
				}
 			}else{
				if(null != mainMap.get(nbase.substring(1,nbase.length()))){
					nbase = mainMap.get(nbase.substring(1,nbase.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值nbase:" + nbase + "...");
				}
			}
		}
		if(sels.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(sels.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = sels.substring(1,sels.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						sels = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值sels:" + sels + "...");
					}
				}
 			}else{
				if(null != mainMap.get(sels.substring(1,sels.length()))){
					sels = mainMap.get(sels.substring(1,sels.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值sels:" + sels + "...");
				}
			}
		}
		if(start.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(start.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = start.substring(1,start.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						start = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值start:" + start + "...");
					}
				}
 			}else{
				if(null != mainMap.get(start.substring(1,start.length()))){
					start = mainMap.get(start.substring(1,start.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值start:" + start + "...");
				}
			}
		}
		if(end.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(end.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = end.substring(1,end.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						end = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值end:" + end + "...");
					}
				}
 			}else{
				if(null != mainMap.get(end.substring(1,end.length()))){
					end = mainMap.get(end.substring(1,end.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值end:" + end + "...");
				}
			}
		}
		if(b0110.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(b0110.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = b0110.substring(1,b0110.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						b0110 = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值b0110:" + b0110 + "...");
					}
				}
 			}else{
				if(null != mainMap.get(b0110.substring(1,b0110.length()))){
					b0110 = mainMap.get(b0110.substring(1,b0110.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值b0110:" + b0110 + "...");
				}
			}
		}
		if(q1501.toUpperCase().indexOf(":".toUpperCase()) != -1){
			if(q1501.toUpperCase().indexOf(".".toUpperCase()) != -1){
				String [] str = q1501.substring(1,q1501.length()).split("[.]");
				if(null != sqlValueMap && sqlValueMap.size() > 0){
					if(null != sqlValueMap.get(str[1])){
						q1501 = sqlValueMap.get(str[1]).toString();
					}else{
						cat.error("请假流程--->>没有找到对应的值q1501:" + q1501 + "...");
					}
				}
 			}else{
				if(null != mainMap.get(q1501.substring(1,q1501.length()))){
					q1501 = mainMap.get(q1501.substring(1,q1501.length())).toString();
				}else{
					cat.error("请假流程--->>没有找到对应的值q1501:" + q1501 + "...");
				}
			}
		}
		
		float leave_time = 0 ;
		AnnualApply annualApply = new AnnualApply(null, conn);
		float[] holiday_rules = annualApply.getHoliday_minus_rule();// 年假假期规则
		String leavetime_rule = KqParam.getInstance().getLeavetimeRule(conn, b0110);
		if (KqParam.getInstance().isHoliday(conn, b0110, sels)) {
			HashMap kqItem_hash = annualApply.count_Leave(sels);

			/*String start = DateUtils.format(kq_start,
					"yyyy.MM.dd HH:mm:ss");
			String end = DateUtils.format(kq_end,
					"yyyy.MM.dd HH:mm:ss");*/
			Date kq_start = DateUtils.getDate(start,"yyyy-MM-dd HH:mm:ss");
			Date kq_end = DateUtils.getDate(end,"yyyy-MM-dd HH:mm:ss");
			leave_time = annualApply.getHistoryLeaveTime(
					kq_start, kq_end, a0100, nbase, b0110, kqItem_hash,
					holiday_rules);
			String history = annualApply.upLeaveManage(a0100, nbase,
					sels, start, end, leave_time, "1", b0110, kqItem_hash,
					holiday_rules);
			String sql = "update q15 set history='" + history
					+ "' where q1501='" + q1501 + "'";
			
			updateData(sql, new ArrayList(), conn);
		}
		return leave_time;
	}
	
	private String returnMessLog(String mess,int flag,String errorElementStr)
	{
		StringBuffer strxml = new StringBuffer();
		strxml.append("<?xml version='1.0' encoding='GB2312'?> ");
		strxml.append("	<root>");
		strxml.append("  <record>");
		strxml.append("	 <flag>"+flag+"</flag>"); // 0为成功，1为不成功
		if(flag==0)
		   strxml.append("<erroinfo>查询成功</erroinfo>");
		else if(flag==1)
		   strxml.append("<erroinfo>"+mess+"</erroinfo>");	
		if(errorElementStr!=null&&errorElementStr.length()>0)
			strxml.append(errorElementStr);
		strxml.append("  </record>");	
		strxml.append("	</root>");
		return strxml.toString();
	}
	
	/**
	 * 校验operuser中的用户名密码
	 * @param hjusername
	 * @param password
	 * @return
	 */
	private boolean cheakCode(String hjusername,String password)
	{
		boolean isCorrect = false;
		Connection conn = null;	
		RowSet rs = null;
		ArrayList list = new ArrayList();
		try
   	    {
   		   conn = AdminDb.getConnection();   		   
   		   String sql = "select 1 from operuser where username=? and password=? ";
   		   list.add(hjusername);
   		   list.add(password);
   		   ContentDAO dao =new ContentDAO(conn);
   		   rs = dao.search(sql, list);
   		   if(rs.next())
   		   {
   			  isCorrect = true;
   		   }
   		   
   	    }catch(Exception e)
    	{
  		   e.printStackTrace();
  		  
   	    }finally
  	     {
  		    try{
  		    	if(rs!=null)
  		    		rs.close();
				if (conn != null){
					conn.close();
				}
			}catch (SQLException sql){
				
			}
        }
   	    return isCorrect;		
	}
	
}
