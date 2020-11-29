package com.hjsj.hrms.businessobject.performance.nworkplan.week;

import com.hjsj.hrms.businessobject.performance.nworkplan.NworkPlanBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * WeekWorkPlanBo.java
 * Description: 国网周报bo
 * Copyright (c) Department of Research and Development/Beijing/北京世纪软件有限公司.
 * All Rights Reserved.
 * @version 1.0  
 * Mar 8, 2013 9:51:31 AM Jianghe created
 * 注：国网周计划和总结是特殊形式，起始时间和结束时间是可自己输入的，但是一定要保证，同部门的起始时间和结束时间相等，否则后续功能无法实现
 */
public class WeekWorkPlanBo {
	/**
	 * 当前登录用户
	 */
	private UserView userView;
	/**
	 * 数据库连接
	 */
	private Connection conn;
	/**
	 * 用来区分入口，=1从自己入口进入，=2从团队计划总结入口进入。
	 */
	private String look_type;
	/**
	 * 个人周计划和部门周计划的标志=0是个人周计划，=1为部门周计划
	 */
	private String enterTypeString="0";
	
	private RecordVo p01_vo = null;
	
	/**
	 * 计划总结归属类型，主要是从团队计划总结模块进入时使用
	 */
	private String belong_type="0";

	public WeekWorkPlanBo(UserView userView,Connection connection,String look_type,String p0100,String enterTypeString){
		this.userView=userView;
		this.conn=connection;
		this.look_type=look_type;
		this.enterTypeString=enterTypeString;
		if(p0100!=null&&!"".equals(p0100)){
			try{
				p01_vo = new RecordVo("p01");
				p01_vo.setInt("p0100",Integer.parseInt(p0100));
				p01_vo=new ContentDAO(conn).findByPrimaryKey(p01_vo);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 取得可查看的计划起始时间和结束时间列表
	 * @return
	 */
	public ArrayList getPlanDateList(String a0100,String year){
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			String posTypeString="-1";
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");//取得岗位类别
            }
			StringBuffer buffer = new StringBuffer();
			buffer.append(" select p0100,p0104,p0106 from p01 where ");
			buffer.append(" ("+Sql_switcher.year("p0104")+"="+year+" or "+Sql_switcher.year("p0106")+"="+year+")");
			buffer.append(" and state=1 ");
			String deptParentid=this.getParentDept();
			if("2".equals(look_type)){//从团队进入，
				buffer.append(" and p0115='02' ");
				if(posTypeString==null|| "".equals(posTypeString)){
					buffer.append(" and 1=2 ");
				}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
					
				}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else {
					buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
					buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
					buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
				}
			}else{//从自己入口进
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室和自己的
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+deptParentid+"'");
				}
			}			
			buffer.append(" order by p0106 desc ");
			ContentDAO dao = new ContentDAO(conn);
			rowSet = dao.search(buffer.toString());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next()){
				CommonData cdCommonData =new CommonData();
				String startString = format.format(rowSet.getDate("p0104"));
				String endString = format.format(rowSet.getDate("p0106"));
				cdCommonData.setDataName(startString+"--"+endString);
				cdCommonData.setDataValue(startString+"--"+endString);
				list.add(cdCommonData);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		 return list;
	}
	/**
	 * 取得所在部门（取所在部门的父部门，如果没有，返回自己部门）
	 * @return
	 */
	public String getParentDept(){
		String parentid="";
		RowSet rowSet=null;
		try{
			ContentDAO dao = new ContentDAO(conn);
			rowSet=dao.search("select codeitemid from organization where UPPER(codeitemid)=(select parentid from organization where codeitemid='"+this.userView.getUserDeptId()+"') and codesetid='UM'");
			if(rowSet.next()){
				parentid=rowSet.getString("codeitemid");
			}else{
				parentid=this.userView.getUserDeptId();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return parentid;
	}
	/**
	 * 首次进入周计划总结，取得页面显示数据的p0100和计划的开始和结束时间
	 * 规则：（1）：如果从团队进入，直接会传过来p0100 则显示这个数据
	 *       （2）：如果从人员入口，首先查找当前人的当前时间所在的计划a，通过开始时间和结束时间，找到这个计划a的下一个计划，则这个计划就是要显示的计划，如果
	 *              没有下一个计划，找当前人所在部门的所有人的，结束时间最大的那条记录，默认时间，显示这个，如果全部没有，显示为空，但用户新建总结的时候，自己输入时间，
	 *              如果用户修改了时间，要与其部门的人员进行对比，如果不一致，给出提示，
	 * @param ap0100 如果从团队进入，会传入p0100 则根据此来找
	 * @param a0100 库前缀+人员编号
	 * @return
	 */
	public HashMap getInitPlanRecord(String ap0100,String a0100,String summTime){
		RowSet rowSet = null;
		HashMap map = new HashMap();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			StringBuffer buffer = new StringBuffer();
			String p0100="";
			Date startDate=null;
			Date endDate = null;
			String isdeptother=null;
			ContentDAO dao = new ContentDAO(conn);
			if("2".equals(look_type)){//从团队进入，传入p0100
				buffer.append("select p0100,p0104,p0106 from p01 where p0100="+ap0100);
				rowSet  = dao.search(buffer.toString());
				while(rowSet.next()){
					p0100=ap0100;
					startDate=rowSet.getDate("p0104");
					endDate=rowSet.getDate("p0106");
				}
			}
			else
			{
				NworkPlanBo bo = new NworkPlanBo(this.conn);
				String posTypeString="-1";
				if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                    posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
                }
				String parentidString=this.getParentDept();
				Calendar calendar=Calendar.getInstance();
				String[] arrStrings = summTime.split("--");
				Date zje_Date=format.parse(arrStrings[1]);
				if(true){//如果当前时间所在计划有记录，则找这个计划的下一个，通过下一个计划的起始时间和本计划的结束时间
					Calendar dar = Calendar.getInstance();
					dar.setTime(zje_Date);
					buffer.setLength(0);
					int temp = dar.get(Calendar.YEAR)*10000+(dar.get(Calendar.MONTH)+1)*100+dar.get(Calendar.DAY_OF_MONTH);
					buffer.append("select p0100,p0104,p0106 from p01 where ");
					buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
					buffer.append(">"+temp+" ");
					if("0".equals(enterTypeString)){//个人
						if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
							//buffer.append(" and belong_type=1 ");
							//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");

						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+parentidString+"'");
					}
					buffer.append(" and state=1 and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=(");
					buffer.append("select MIN("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
					buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
					buffer.append(">"+temp+" ");
					if("0".equals(enterTypeString)){//个人
						if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");

						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+parentidString+"'");
					}
					buffer.append(" and state=1");
					buffer.append(")");
					rowSet=dao.search(buffer.toString());
					boolean has=false;
					while(rowSet.next()){//在当前总结时间下，有计划，取这个计划
						p0100=rowSet.getString("p0100");
						startDate=rowSet.getDate("p0104");
						endDate=rowSet.getDate("p0106");
						has=true;
					}
					if(!has){//如果没有下个计划,找同部门的在这个总结时间后的最小的那期计划，通过结束时间来判断
						buffer.setLength(0);
						buffer.append(" select p0100,p0104,p0106 from p01 where ");
						buffer.append(" state=1 and ");
						buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
						buffer.append(">"+temp+" ");
						buffer.append(" and e0122 like '"+parentidString+"%'");
						buffer.append(" and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=");
						buffer.append(" (select min("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
						buffer.append("  state=1 and ");
						buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
						buffer.append(">"+temp+"  ");
						buffer.append(" and e0122 like '"+parentidString+"%'");
						buffer.append(")");
						rowSet=dao.search(buffer.toString());
						while(rowSet.next()){
							isdeptother="1";
							startDate=rowSet.getDate("p0104");
							endDate=rowSet.getDate("p0106");
						}
					}
				}
		    }
			map.put("start_date",startDate);
			map.put("end_date", endDate);
			map.put("p0100",p0100);
			map.put("isdeptother", isdeptother);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return map;
	}
	public HashMap getInitPlanRecord(String ap0100,String a0100){
		RowSet rowSet = null;
		HashMap map = new HashMap();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			StringBuffer buffer = new StringBuffer();
			String p0100="";
			Date startDate=null;
			Date endDate = null;
			String isdeptother="0";
			ContentDAO dao = new ContentDAO(conn);
			if("2".equals(look_type)){//从团队进入，传入p0100
				buffer.append("select p0100,p0104,p0106 from p01 where p0100="+ap0100);
				rowSet  = dao.search(buffer.toString());
				while(rowSet.next()){
					p0100=ap0100;
					startDate=rowSet.getDate("p0104");
					endDate=rowSet.getDate("p0106");
				}
			}
			else
			{
				NworkPlanBo bo = new NworkPlanBo(this.conn);
				String posTypeString="-1";
				if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                    posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
                }
				String parentidString=this.getParentDept();
				Calendar calendar=Calendar.getInstance();
				Date zje_Date=calendar.getTime();
			   //如果当前时间所在计划有记录，则找这个计划的下一个，通过下一个计划的起始时间和本计划的结束时间
				Calendar dar = Calendar.getInstance();
				dar.setTime(zje_Date);
				buffer.setLength(0);
				int temp = dar.get(Calendar.YEAR)*10000+(dar.get(Calendar.MONTH)+1)*100+dar.get(Calendar.DAY_OF_MONTH);
				buffer.append("select p0100,p0104,p0106 from p01 where ");
				buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
				buffer.append(">"+temp+" ");
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						//buffer.append(" and belong_type=1 ");
						//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");

					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+parentidString+"'");
				}
				buffer.append(" and state=1 and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=(");
				buffer.append("select MIN("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
				buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
				buffer.append(">"+temp+" ");
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");

					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+parentidString+"'");
				}
				buffer.append(" and state=1");
				buffer.append(")");
				rowSet=dao.search(buffer.toString());
				boolean has=false;
				while(rowSet.next()){//在当前总结时间下，有计划，取这个计划
					p0100=rowSet.getString("p0100");
					startDate=rowSet.getDate("p0104");
					endDate=rowSet.getDate("p0106");
					has=true;
				}
				if(!has){//如果没有下个计划,找同部门的在这个总结时间后的最小的那期计划，通过结束时间来判断
					buffer.setLength(0);
					buffer.append(" select p0100,p0104,p0106 from p01 where ");
					buffer.append(" state=1 and ");
					buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
					buffer.append(">"+temp+" ");
					buffer.append(" and e0122 like '"+parentidString+"%'");
					buffer.append(" and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=");
					buffer.append(" (select min("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
					buffer.append("  state=1 and ");
					buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
					buffer.append(">"+temp+"  ");
					buffer.append(" and e0122 like '"+parentidString+"%'");
					buffer.append(")");
					rowSet=dao.search(buffer.toString());
					while(rowSet.next()){
						isdeptother="1";
						startDate=rowSet.getDate("p0104");
						endDate=rowSet.getDate("p0106");
					}
				}
			}
			map.put("start_date",startDate);
			map.put("end_date", endDate);
			map.put("p0100",p0100);
			map.put("isdeptother", isdeptother);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 找出默认显示的总结区间
	 * 如果有包含当前区间的，显示，如果没有，找最大的
	 * @return
	 */
	public String getSummTime(String a0100,String year,boolean isCurr){
		String string="";
		RowSet rowSet = null;
		try {
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			boolean hasNow=false;
			String posTypeString="-1";
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			ContentDAO dao = new ContentDAO(conn);
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");//取得岗位类别
            }
			StringBuffer buffer = new StringBuffer();
			String deptParentid=this.getParentDept();
			Calendar calendar=Calendar.getInstance();
			int temp = calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
			//buffer.append("select p0100,p0104,p0106 from p01 where ");
			if(isCurr){
				buffer.append(" select p0100,p0104,p0106 from p01 where ");
				buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
				buffer.append("<="+temp+" and ");
				buffer.append(Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106"));
				buffer.append(">="+temp);
				buffer.append(" and state=1 ");
				if("2".equals(look_type)){//从团队进入，
					buffer.append(" and p0115='02' ");
					if(posTypeString==null|| "".equals(posTypeString)){
						buffer.append(" and 1=2 ");
					}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员看处室
						buffer.append(" and belong_type=1");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else {
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3)+"'");
					}
				}else{//从自己入口进
					if("0".equals(enterTypeString)){//个人
						if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室和自己的
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}			
				buffer.append(" order by p0106 desc ");
				rowSet = dao.search(buffer.toString());
				while(rowSet.next()){
					String startString = format.format(rowSet.getDate("p0104"));
					String endString = format.format(rowSet.getDate("p0106"));
					string=startString+"--"+endString;
					hasNow=true;
				}
			}
			if(!hasNow){//没有包含现在区间的取最大的
				buffer.setLength(0);
				buffer.append(" select p0100,p0104,p0106 from p01 where ");
				buffer.append(" ("+Sql_switcher.year("p0104")+"="+year+" or "+Sql_switcher.year("p0106")+"="+year+")");
				buffer.append(" and state=1 ");
				if("2".equals(look_type)){//从团队进入，
					buffer.append(" and p0115='02' ");
					if(posTypeString==null|| "".equals(posTypeString)){
						buffer.append(" and 1=2 ");
					}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员看处室
						buffer.append(" and belong_type=1");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else {
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3)+"'");
					}
				}else{//从自己入口进
					if("0".equals(enterTypeString)){//个人
						if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室和自己的
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}
				buffer.append(" and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=");
			    buffer.append(" (select MAX("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
			    buffer.append(" ("+Sql_switcher.year("p0104")+"="+year+" or "+Sql_switcher.year("p0106")+"="+year+")");
				buffer.append(" and state=1 ");
				if("2".equals(look_type)){//从团队进入，
					buffer.append(" and p0115='02' ");
					if(posTypeString==null|| "".equals(posTypeString)){
						buffer.append(" and 1=2 ");
					}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员看处室
						buffer.append(" and belong_type=1");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else {
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3)+"'");
					}
				}else{//从自己入口进
					if("0".equals(enterTypeString)){//个人
						if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室和自己的
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}
				buffer.append(") order by p0106 desc ");
				rowSet=dao.search(buffer.toString());
				while(rowSet.next()){
					String startString = format.format(rowSet.getDate("p0104"));
					String endString = format.format(rowSet.getDate("p0106"));
					string=startString+"--"+endString;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return string;
	}
	/**
	 * 得到部门其他人的下期
	 * @param zj_e
	 * @param a0100
	 * @return
	 */
	public HashMap getSameDept(Date zj_e,String a0100){
		HashMap map = new HashMap();
		RowSet rowSet=null;
		try {
			StringBuffer buffer = new StringBuffer();
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			String posTypeString="-1";
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
            }
			String parentidString=this.getParentDept();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(zj_e);
			//起始时间比总结结束时间大，结束时间最大的记录，
			int temp = calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
			buffer.setLength(0);
			buffer.append(" select p0100,p0104,p0106 from p01 where ");
			//buffer.append(" e0122 like '"+parentidString+"%'");
			buffer.append(" state=1 and ");
			buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
			buffer.append(">"+temp+" ");
			buffer.append(" and e0122 like '"+parentidString+"%'");
			buffer.append(" and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=");
			buffer.append(" (select MAX("+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+") from p01 where ");
			//buffer.append(" e0122 like '"+parentidString+"%'");
			buffer.append(" state=1 and ");
			buffer.append(Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
			buffer.append(">"+temp+"  ");
			buffer.append(" and e0122 like '"+parentidString+"%'");
			buffer.append(")");
			ContentDAO dao = new ContentDAO(conn);
			rowSet=dao.search(buffer.toString());
			Date startDate = null;
			Date endDate = null;
			String isdeptother="0";
			while(rowSet.next()){
				//p0100=rowSet.getString("p0100");
				isdeptother ="1";
				startDate=rowSet.getDate("p0104");
				endDate=rowSet.getDate("p0106");
			}
			map.put("start_date", startDate);
			map.put("end_date", endDate);
			map.put("isdeptother", isdeptother);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 得到部门其他人的下期
	 * @param zj_e
	 * @param a0100
	 * @return
	 */
	public HashMap getSameDeptUP(Date jh_s,String a0100){
		HashMap map = new HashMap();
		RowSet rowSet=null;
		try {
			StringBuffer buffer = new StringBuffer();
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			String posTypeString="-1";
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
            }
			String parentidString=this.getParentDept();
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(jh_s);
			//结束时间比计划的起始时间小的最大那条记录
			int temp = calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
			buffer.setLength(0);
			buffer.append(" select p0100,p0104,p0106 from p01 where ");
			//buffer.append(" e0122 like '"+parentidString+"%'");
			buffer.append(" state=1 and ");
			buffer.append(Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106"));
			buffer.append("<"+temp+" ");
			buffer.append(" and e0122 like '"+parentidString+"%'");
			buffer.append(" and "+Sql_switcher.dateToChar("p0106", "yyyy-MM-dd")+"=");
			buffer.append(" (select MAX("+Sql_switcher.dateToChar("p0106", "yyyy-MM-dd")+") from p01 where ");
			//buffer.append(" e0122 like '"+parentidString+"%'");
			buffer.append(" state=1 and ");
			buffer.append(Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106"));
			buffer.append("<"+temp+"  ");
			buffer.append(" and e0122 like '"+parentidString+"%'");
			buffer.append(")");
			ContentDAO dao = new ContentDAO(conn);
			rowSet=dao.search(buffer.toString());
			Date startDate = null;
			Date endDate = null;
			String isdeptother=null;
			while(rowSet.next()){
				//p0100=rowSet.getString("p0100");
				startDate=rowSet.getDate("p0104");
				endDate=rowSet.getDate("p0106");
				isdeptother="1";
			}
			map.put("start_date", startDate);
			map.put("end_date", endDate);
			map.put("isdeptother", isdeptother);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 根据当前计划的时间，找出总结的时间，
	 * 其结束时间比当前计划起始时间小的最大值
	 * @return
	 */
	public HashMap getZJDateByPlan(String a0100,Date plS_Date,Date plE_Date,String c01sc){
		HashMap map = new HashMap();
		RowSet rowSet=null;
		try {
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			String posTypeString="-1";
			String p0100="";
			Date startDate=null;
			Date endDate=null;
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
            }
			StringBuffer buffer = new StringBuffer();
			buffer.append(" select p0100,p0104,p0106 from p01 where ");
			buffer.append(" state=1 ");
			Calendar sCalendar=Calendar.getInstance();
			sCalendar.setTime(plS_Date);
			int sint = sCalendar.get(Calendar.YEAR)*10000+(sCalendar.get(Calendar.MONTH)+1)*100+sCalendar.get(Calendar.DAY_OF_MONTH);
			buffer.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106"));
			buffer.append("<"+sint);
			String deptParentid=this.getParentDept();
			if("2".equals(look_type)){//从团队进入，
				buffer.append(" and p0115='02' ");
				if(c01sc==null|| "".equals(c01sc)){
					buffer.append(" and 1=2 ");
				}else if("01".equals(c01sc)|| "02".equals(c01sc)){//主任
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("03".equals(c01sc)|| "04".equals(c01sc)){//处长看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("05".equals(c01sc)|| "06".equals(c01sc)){//职员看处室
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else {
					buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
					buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
					buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
				}
			}else{//从自己入口进
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						//buffer.append(" and belong_type=1 ");
						//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+deptParentid+"'");
				}
			}
			buffer.append(" and "+Sql_switcher.dateToChar("p0106", "yyyy-MM-dd")+"=(");
			buffer.append(" select MAX("+Sql_switcher.dateToChar("p0106","yyyy-MM-dd")+") from p01 where ");
			buffer.append(" state=1 ");
			buffer.append(" and "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106"));
			buffer.append("<"+sint);
			if("2".equals(look_type)){//从团队进入，
				buffer.append(" and p0115='02' ");
				if(c01sc==null|| "".equals(c01sc)){
					buffer.append(" and 1=2 ");
				}else if("01".equals(c01sc)|| "02".equals(c01sc)){//主任看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("03".equals(c01sc)|| "04".equals(c01sc)){//处长看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("05".equals(c01sc)|| "06".equals(c01sc)){//职员看处室
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else {
					buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
					buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
					buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
				}
			}else{//从自己入口进
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						//buffer.append(" and belong_type=1 ");
						//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+deptParentid+"'");
				}
			}
			buffer.append(")");
			ContentDAO  dao = new ContentDAO(conn);
			rowSet=dao.search(buffer.toString());
			while(rowSet.next()){
				p0100=rowSet.getString("p0100");
				startDate=rowSet.getDate("p0104");
				endDate=rowSet.getDate("p0106");
			}
			map.put("start_date", startDate);
			map.put("end_date", endDate);
			map.put("p0100", p0100);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		return map;
	}
	/**
	 * 根据总结时间，找出计划时间，
	 * 其起始时间比总结结束时间大的最小值
	 * @return
	 */
	public HashMap getPlanDateByZJ(String a0100,Date zjS_Date,Date zjE_Date){
		HashMap map = new HashMap();
		RowSet rowSet=null;
		try {
			NworkPlanBo bo = new NworkPlanBo(this.conn);
			String posTypeString="-1";
			String p0100="";
			Date startDate=null;
			Date endDate=null;
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))) {
                posTypeString=bo.getUserDetail(this.userView.getDbname()+this.userView.getA0100(), "c01sc");//取得岗位类别
            }
			StringBuffer buffer = new StringBuffer();
			buffer.append(" select p0100,p0104,p0106 from p01 where ");
			buffer.append(" state=1 ");
			Calendar sCalendar=Calendar.getInstance();
			sCalendar.setTime(zjE_Date);
			int sint = sCalendar.get(Calendar.YEAR)*10000+(sCalendar.get(Calendar.MONTH)+1)*100+sCalendar.get(Calendar.DAY_OF_MONTH);
			buffer.append(" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
			buffer.append(">"+sint);//计划的其起始时间，要比该总结的结束时间大
			String deptParentid=this.getParentDept();
			if("2".equals(look_type)){//从团队进入，
				buffer.append(" and p0115='02' ");
				if(posTypeString==null|| "".equals(posTypeString)){
					buffer.append(" and 1=2 ");
				}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员看处室
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else {
					buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
					buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
					buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
				}
			}else{//从自己入口进
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						//buffer.append(" and belong_type=1 ");
						//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+deptParentid+"'");
				}
			}
			//buffer.append(" and p0106=(");
			buffer.append(" and "+Sql_switcher.dateToChar("p0104", "yyyy-MM-dd")+"=(");
			buffer.append(" select MIN("+Sql_switcher.dateToChar("p0104","yyyy-MM-dd")+") from p01 where ");
			buffer.append(" state=1 ");
			buffer.append(" and "+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104"));
			buffer.append(">"+sint);
			if("2".equals(look_type)){//从团队进入，
				buffer.append(" and p0115='02' ");
				if(posTypeString==null|| "".equals(posTypeString)){
					buffer.append(" and 1=2 ");
				}else if("01".equals(posTypeString)|| "02".equals(posTypeString)){//主任看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+(String)this.userView.getHm().get("e0122")+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长看部门
					if(belong_type!=null&& "0".equals(belong_type)){
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
						buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
					}else if(belong_type!=null&& "1".equals(belong_type)){
						buffer.append(" and belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else if(belong_type!=null&& "2".equals(belong_type)){
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}else if("05".equals(posTypeString)|| "06".equals(posTypeString)){//职员看处室
					buffer.append(" and belong_type=1");
					buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
				}else {
					buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
					buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3).toUpperCase()+"'");
					buffer.append(" and a0100='"+a0100.substring(3).toUpperCase()+"'");
				}
			}else{//从自己入口进
				if("0".equals(enterTypeString)){//个人
					if("03".equals(posTypeString)|| "04".equals(posTypeString)){//处长，查看自己处室
						//buffer.append(" and belong_type=1 ");
						//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
						buffer.append(" and ((belong_type=1 ");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
					}else {//其他人，均看自己的
						buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
						buffer.append(" and a0100='"+this.userView.getA0100()+"'");
						buffer.append(" and (belong_type=0 or belong_type is null)");
					}
				}else{//部门 都查看部门的
				    buffer.append(" and belong_type=2 ");
					buffer.append(" and e0122='"+deptParentid+"'");
				}
			}
			buffer.append(")");
			ContentDAO  dao = new ContentDAO(conn);
			rowSet=dao.search(buffer.toString());
			while(rowSet.next()){
				p0100=rowSet.getString("p0100");
				startDate=rowSet.getDate("p0104");
				endDate=rowSet.getDate("p0106");
			}
			map.put("start_date", startDate);
			map.put("end_date", endDate);
			map.put("p0100", p0100);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return map;
	}
	/**
	 * 校验新建时的时间输入
	 * @param syear
	 * @param smonth
	 * @param sday
	 * @param eyear
	 * @param emonth
	 * @param eday
	 * @return
	 */
	public String validateDate(String syear,String smonth,String sday,String eyear,String emonth,String eday,String a0100,String summarizeTime){
		String errorString="";
		RowSet rowSet=null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(syear==null|| "".equals(syear)||smonth==null|| "".equals(smonth)||sday==null|| "".equals(sday)||
					eyear==null|| "".equals(eyear)||emonth==null|| "".equals(emonth)||eday==null|| "".equals(eday))
			{
				errorString="计划开始时间和结束时间是必填项！";
				return errorString;
			}
			int sint=Integer.parseInt(syear)*10000+(Integer.parseInt(smonth))*100+Integer.parseInt(sday);
			int eint=Integer.parseInt(eyear)*10000+(Integer.parseInt(emonth))*100+Integer.parseInt(eday);
			if(sint>=eint){
				errorString="计划结束时间必须大于开始时间！";
				return errorString;
			}
			if(summarizeTime!=null&&!"".equals(summarizeTime)){
				String[] temp_aStrings =summarizeTime.split("--");
				Calendar zjE_Calendar=Calendar.getInstance();
				zjE_Calendar.setTime(format.parse(temp_aStrings[1]));
				int zjE_Date=zjE_Calendar.get(Calendar.YEAR)*10000+(zjE_Calendar.get(Calendar.MONTH)+1)*100+zjE_Calendar.get(Calendar.DAY_OF_MONTH);
				if(sint<=zjE_Date){
					errorString="计划的起始时间必须大于总结的结束时间！";
					return errorString;
				}
			}
			if("gw".equals(SystemConfig.getPropertyValue("clientName"))){/*
				String posTypeString="-1";
				NworkPlanBo bo = new NworkPlanBo(this.conn);
				if(SystemConfig.getPropertyValue("clientName").equals("gw"))
					posTypeString=bo.getUserDetail(a0100, "c01sc");//取得岗位类别
				String deptParentid=this.getParentDept();
				StringBuffer buffer = new StringBuffer();
				buffer.append(" select p0100,p0104,p0106 from p01 ");
				buffer.append(" where "+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+">"+sint);
				if(look_type.equals("2")){//从团队进入，
					//buffer.append(" and p0115='02' ");
					if(posTypeString==null||posTypeString.equals("")){
						buffer.append(" and 1=2 ");
					}else if(posTypeString.equals("01")||posTypeString.equals("02")){//主任看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if(posTypeString.equals("03")||posTypeString.equals("04")){//处长看部门
						buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}else if(posTypeString.equals("05")||posTypeString.equals("06")){//职员看处室
						buffer.append(" and belong_type=1");
						buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
					}else {
						buffer.append(" and (belong_type=0 or belong_type is null)");//通用的看人员
						buffer.append(" and UPPER(nbase)='"+a0100.substring(0,3)+"'");
						buffer.append(" and a0100='"+a0100.substring(3)+"'");
					}
				}else{//从自己入口进
					if(enterTypeString.equals("0")){//个人
						if(posTypeString.equals("03")||posTypeString.equals("04")){//处长，查看自己处室
							//buffer.append(" and belong_type=1 ");
							//buffer.append(" and e0122='"+this.userView.getUserDeptId()+"'");
							buffer.append(" and ((belong_type=1 ");
							buffer.append(" and e0122='"+this.userView.getUserDeptId()+"') or (belong_type=0  ");
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'))");
						}else {//其他人，均看自己的
							buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"'");
							buffer.append(" and a0100='"+this.userView.getA0100()+"'");
							buffer.append(" and (belong_type=0 or belong_type is null)");
						}
					}else{//部门 都查看部门的
					    buffer.append(" and belong_type=2 ");
						buffer.append(" and e0122='"+deptParentid+"'");
					}
				}
			*/}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(rowSet!=null) {
                    rowSet.close();
                }
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return errorString;
	}
	public HashMap getInitMap(){
		HashMap map = new HashMap();
		Calendar calendar = Calendar.getInstance();
		int week=calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_MONTH, (8-week));
		Calendar aCalendar=Calendar.getInstance();
		aCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		aCalendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH)));
		aCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
		aCalendar.add(Calendar.DAY_OF_MONTH, 6);
		map.put("start_date", calendar.getTime());
		map.put("end_date", aCalendar.getTime());
		return map;
	}
	public HashMap getJhByZjEndDate(Date zjEndDate){
		HashMap map = new HashMap();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(zjEndDate);
		int week=calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DAY_OF_MONTH, (8-week));
		Calendar aCalendar=Calendar.getInstance();
		aCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		aCalendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH)));
		aCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
		aCalendar.add(Calendar.DAY_OF_MONTH, 6);
		map.put("start_date", calendar.getTime());
		map.put("end_date", aCalendar.getTime());
		return map;
	}
	public WeekWorkPlanBo(){
		
	}
	public String getInitZJ(String jhstartdate){
		String string="";
		try{
			Calendar calendar = Calendar.getInstance();
			Calendar aCalendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			calendar.setTime(format.parse(jhstartdate));
			aCalendar.setTime(format.parse(jhstartdate));
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			aCalendar.add(Calendar.DAY_OF_MONTH, -7);
			string=format.format(aCalendar.getTime())+"--"+format.format(calendar.getTime());
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return string;
	}

	public String getBelong_type() {
		return belong_type;
	}
	public void setBelong_type(String belong_type) {
		this.belong_type = belong_type;
	}
}
