package com.hjsj.hrms.businessobject.performance.nworkplan.season;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.transaction.kq.month_kq.MonthKqBean;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class NewWorkPlanBo {
	private Connection conn = null;
	private UserView userView = null;
	
	public NewWorkPlanBo(Connection conn , UserView userView){
		this.conn = conn;
		this.userView = userView;
	}
	
	/**
	 * 判断当前年月所在的季度
	 * @param month
	 * */
	
	public String getSeasonByMonth(int month){
		String season = "";
		switch (month) {
		case 1:
			season = "1";
			break;
		case 2:
			season = "1";
			break;
		case 3:
			season = "1";
			break;
		case 4:
			season = "2";
			break;
		case 5:
			season = "2";
			break;
		case 6:
			season = "2";
			break;
		case 7:
			season = "3";
			break;
		case 8:
			season = "3";
			break;
		case 9:
			season = "3";
			break;
		case 10:
			season = "4";
			break;
		case 11:
			season = "4";
			break;
		case 12:
			season = "4";
			break;
		default:
			break;
		}
		return season;
	}
	
	/**
	 * 	通过当前季度 得到季度开始月份和结束月份
	 * */
	public String getMonthsBySeason(String season){
		String months = "" ;
		if("1".equals(season)){
			months = "1,3";
		}else if("2".equals(season)){
			months = "4,6";
		}else if("3".equals(season)){
			months = "7,9";
		}else if("4".equals(season)){
			months = "10,12";
		}
		return months;
	}
	
	/**
	 * 	通过切换的年或者季 得到对应的P0100
	 * */
	public String getP0100ByCheckSeason(int year ,int season , String isdept , String type ){
		String p0100 = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select p0100 from p01 ");
		sb.append(" where ");
		sb.append(Sql_switcher.year("p0104") + "=" + year);
		if("1".equals(type)){//季报				
			sb.append(" and time = " + season);
			sb.append(" and state = 3");
		}else if("2".equals(type)){//年报
			sb.append(" and state = 4");
		}
		if("1".equals(isdept)){
			//sb.append(" and belong_type = 1 ");
			if(this.isChuZhang(this.userView.getA0100())){
				sb.append(" and ((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
				sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
				sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
			}else{
				sb.append(" and (belong_type = 0 or belong_type = null)");
			}
		}else if("2".equals(isdept)){//如果是部门
			sb.append(" and belong_type = 2 ");
			sb.append(" and e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname())+"'");
			
		}
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			System.out.println(sb.toString());
			rs = dao.search(sb.toString());
			if(rs.next()){
				p0100 = rs.getString("p0100");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p0100;
	}
	/**
	 *  进入季报时候 根据年份 季度 得到当前登录人的季报内容
	 * */
	public String getMessageByYearAndSeason(int year , int season , String opt , String p0100 , String isdept){
		String message = "";
		StringBuffer sb = new StringBuffer("select p0103 from p01 ");
		if("1".equals(isdept)){
			if("1".equals(opt)){
				sb.append(" where a0100 = '");
				sb.append(this.userView.getA0100() + "' and ");
				sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
				if(this.isChuZhang(this.userView.getA0100())){
					sb.append("((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
					sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
					sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
					sb.append(" and ");
				}else{
					sb.append("(belong_type = 0 or belong_type = null) and ");
				}
			}else if("2".equals(opt)){
				//sb.append(a0100 + "' and ");
				//sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
				//sb.append(" where p0100 = '" + p0100 + "'");
				sb.append(" where ((a0100 = ( select a0100 from p01 where p0100 = '" + p0100 + "' )");
				sb.append(" and nbase = ( select nbase from p01 where p0100 = '" + p0100 + "' ) and belong_type = 0)");
				sb.append(" or (belong_type = 1 and e0122 = ( select e0122 from p01 where ");
				sb.append(" p0100 = '" + p0100 + "' ))) ");
				sb.append(" and ");
			}
		}else if("2".equals(isdept)){
			sb.append(" where belong_type = 2 ");
			if("1".equals(opt)){
				sb.append(" and e0122 ='" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				sb.append("' and ");
			}else if("2".equals(opt)){
				sb.append(" and e0122 = ( select codeitemid from organization where ");
				sb.append(" codeitemid = (select parentid from organization where");
				sb.append(" codeitemid = (select e0122 from usra01 where a0100 = (select");
				sb.append(" a0100 from p01 where p0100 = '"+p0100+"')))");
				sb.append(" and codesetid = 'UM') and ");
			}
		}
		sb.append(Sql_switcher.year("p0104") + "=");
		sb.append(year + " and time = " + season);
		sb.append(" and state = 3");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			System.out.println(sb.toString());
			rs = dao.search(sb.toString());
			if(rs.next()){
				message = Sql_switcher.readMemo(rs, "p0103");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * 	进入年报的时候 根据年份得到当前登录人的年报内容
	 * */
	public String getYearMessageByYear(int year , String opt , String p0100,String isdept){
		String message = "";
		StringBuffer sb = new StringBuffer(" select p0103 from p01");
		if("1".equals(isdept)){
			if("1".equals(opt)){
				sb.append(" where a0100 = '");
				sb.append(this.userView.getA0100() + "' and ");
				sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
				if(this.isChuZhang(this.userView.getA0100())){ //如果是处长
					sb.append("((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
					sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
					sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
					sb.append(" and ");
					//sb.append("belong_type = 1 and ");
				}else{										   //如果是职员
					sb.append("(belong_type = 0 or belong_type = null) and ");
				}
			}else if("2".equals(opt)){
				//sb.append(" where p0100 = '" + p0100 + "'");
				//sb.append(" where a0100 = ( select a0100 from p01 where p0100 = '" + p0100 + "')");
				//sb.append(" and nbase = ( select nbase from p01 where p0100 = '" + p0100 + "') and ");
				//sb.append(" belong_type = 1 and ");
				sb.append(" where ((a0100 = ( select a0100 from p01 where p0100 = '" + p0100 + "' )");
				sb.append(" and nbase = ( select nbase from p01 where p0100 = '" + p0100 + "' ) and belong_type = 0)");
				sb.append(" or (belong_type = 1 and e0122 = ( select e0122 from p01 where ");
				sb.append(" p0100 = '" + p0100 + "' ))) ");
				sb.append(" and ");
			}
		}else if("2".equals(isdept)){
			sb.append(" where belong_type = 2 ");
			if("1".equals(opt)){
				sb.append(" and e0122 ='" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				sb.append("' and ");
			}else if("2".equals(opt)){
				sb.append(" and e0122 = ( select codeitemid from organization where ");
				sb.append(" codeitemid = (select parentid from organization where");
				sb.append(" codeitemid = (select e0122 from usra01 where a0100 = (select");
				sb.append(" a0100 from p01 where p0100 = '"+p0100+"')))");
				sb.append(" and codesetid = 'UM') and ");
			}
		}
		sb.append(Sql_switcher.year("p0104") + "=");
		sb.append(year + " and state = 4");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				message = Sql_switcher.readMemo(rs, "p0103");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 *  保存季报 (一人 一年 一季一条记录)
	 * */
	public boolean SaveSeason(String message , Date startDate , Date endDate,int season,int year,String type,String opt,String p0100,String isdept){
		boolean flag = false;
		ContentDAO dao = new ContentDAO(this.conn);
		if(this.IsHaving(season,year,type,opt,p0100,isdept)){ //当前季度在P01表中没有存在就新增 有则修改
			RecordVo vo = new RecordVo("P01");
			vo.setInt("p0100", getMaxP01());
			if("1".equals(opt)){      //自己进入
				if("1".equals(isdept)){       //个人
						vo.setString("b0110", this.userView.getUserOrgId());    //登录用户单位编码
						vo.setString("e0122", this.userView.getUserDeptId());   //登录用户部门编码
						vo.setString("e01a1", this.userView.getUserPosId());    //登录用户岗位编码
						vo.setString("a0101", this.userView.getUserFullName()); //登录用户名字
						vo.setString("nbase", this.userView.getDbname());       //登录用户人员库
						vo.setString("a0100", this.userView.getA0100());        //人员编码
						//if(this.isChuZhang(this.userView.getA0100())){ //如果是处长 1
						//		vo.setString("belong_type", "1");
						//}else{                                         //如果是职员 2
								vo.setString("belong_type", "0");          
						//}
				}else if("2".equals(isdept)){ //部门
					vo.setString("belong_type", "2");
					vo.setString("e0122", this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				}
			}
			else if("2".equals(opt)){ //从团队进入 只有主任可以保存 修改记录 也只能改部门的
				vo.setString("a0100", this.userView.getA0100());
				vo.setString("e0122", this.userView.getUserDeptId());
				vo.setString("belong_type", "2");
			}
			if("1".equals(type)){	    //季报			
				vo.setInt("time", season);
				vo.setInt("state", 3);
			}else if("2".equals(type)){ //年报
				vo.setInt("state", 4);
			}
			vo.setString("p0103", message);
			vo.setDate("p0104", startDate);
			vo.setDate("p0106", endDate);
			vo.setString("p0115", "01");
			vo.setInt("log_type", 2);
			try {
				if(dao.addValueObject(vo) > 0){
					flag = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			StringBuffer sb = new StringBuffer("update p01 set ");
			sb.append("p0103 = '");
			sb.append(message + "'");
			if("1".equals(isdept)){
				if("1".equals(opt)){				
					sb.append(" where a0100 = '");
					sb.append(this.userView.getA0100() + "' and ");
					sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
					sb.append(Sql_switcher.year("p0104") + "=" + year);
					if("1".equals(type)){				
						sb.append(" and time = " + season);
						sb.append(" and state = 3");
					}else if("2".equals(type)){
						sb.append(" and state = 4");
					}
					if(this.isChuZhang(this.userView.getA0100())){
						sb.append(" and ((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
						sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
						sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
						//sb.append(" and ");
						//sb.append(" and belong_type = 1");
					}else{
						sb.append(" and belong_type = 0");
					}
				}else if("2".equals(opt)){
					//sb.append(a0100 + "' and ");
					//sb.append(" nbase = '" + nbase + "' and ");
					sb.append(" where p0100 = '" + p0100 + "'");
				}
			}else if("2".equals(isdept)){
				sb.append(" where belong_type = 2 and ");
				sb.append(Sql_switcher.year("p0104") + "=" + year);
				if("1".equals(type)){				
					sb.append(" and time = " + season);
					sb.append(" and state = 3");
				}else if("2".equals(type)){
					sb.append(" and state = 4");
				}
				sb.append(" and e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				sb.append("'");
			}
			try {
				System.out.println(sb.toString());
				if( dao.update(sb.toString()) > 0 ){
					flag = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	/**
	 *  保存季报之前判断当前季度是否已经写过季报 ， 如果已经写过则更新内容 如果没有 则新建(待优化 如果是从团队总结进来的 则只能修改)
	 * */
	public boolean IsHaving(int season,int year,String type,String opt,String p0100,String isdept){
		StringBuffer sb = new StringBuffer("select * from p01");
		if("1".equals(isdept)){
			if("1".equals(opt)){
				if("1".equals(type)){			
					sb.append(" where state = 3 and ");
					sb.append("time = " + season);
				}else if("2".equals(type)){
					sb.append(" where state = 4 ");
				}
				sb.append(" and "+Sql_switcher.year("p0104") + "=" + year);
				sb.append(" and a0100 = '");
				sb.append(this.userView.getA0100() + "' and ");
				sb.append("nbase = '" + this.userView.getDbname() + "'");
				//if(this.isChuZhang(this.userView.getA0100())){
				//	sb.append(" and belong_type = 1");
				//}else{
					sb.append(" and belong_type = 0");
				//}
			}else if("2".equals(opt)){
				sb.append(" where p0100 = '" + p0100 + "'");
				//sb.append(a0100 + "' and ");
				//sb.append("nbase = '" + nbase + "'");
			}
		}else if("2".equals(isdept)){
			if("1".equals(type)){			
				sb.append(" where state = 3 and ");
				sb.append("time = " + season);
			}else if("2".equals(type)){
				sb.append(" where state = 4 ");
			}
			sb.append(" and "+Sql_switcher.year("p0104") + "=" + year);
			sb.append(" and e0122= '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
			sb.append("' and belong_type = 2 ");
		}
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 	提取上一季度的内容到本季度中
	 * */
	
	public String getOldMessageByYearSeason(int year ,int season){
		if(season == 1){ //如果是第一季 则提取上一年的最后一季度的季报内容
			season = 4;
			year = year - 1;
		}
		String message = "";
		StringBuffer sb = new StringBuffer("select p0103 from p01 ");
		sb.append(" where a0100 = '");
		sb.append(this.userView.getA0100() + "' and ");
		sb.append(Sql_switcher.year("p0104") + "=" + year);
		sb.append(" and state = 3 and time = " + (season-1));
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				message = Sql_switcher.readMemo(rs, "p0103");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return message;
	}
	
	/**
	 *  提取上一年的年报到本年中
	 * */
	public String getOldMessageByYear(int year){
		String message = "";
		StringBuffer sb = new StringBuffer("select p0103 from p01 ");
		sb.append(" where a0100 = '");
		sb.append(this.userView.getA0100() + "' and ");
		sb.append(Sql_switcher.year("p0104") + "=" + (year-1));
		sb.append(" and state = 4");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				message = Sql_switcher.readMemo(rs, "p0103");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return message;
	}
	
	/**
	 * 	根据年份 季度 得到当前登录人某年某季的季报的状态
	 * */
	public String getStatesByYearSeason(int year ,int season , String opt ,String p0100,String isdept){
		String states = "";
		RowSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select codeitemdesc , codeitemid from codeitem ");
		sb.append(" where codesetid = '23' and codeitemid = (");
		sb.append(" select p0115 from p01 ");
		if("1".equals(isdept)){        //个人进入
			if("1".equals(opt)){       //入口是自己
				sb.append(" where a0100 = '");
				sb.append(this.userView.getA0100() + "' and ");
				sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
				if(this.isChuZhang(this.userView.getA0100())){ //职员 belong_type = 0||null 处长 1
					//sb.append(" belong_type = 1 and ");
					sb.append("((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
					sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
					sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
					sb.append(" and ");
				}else{
					sb.append(" (belong_type = 0 or belong_type = null) and ");
				}
			}else if("2".equals(opt)){ //入口是团队总结 进入人员的时候只查处的
				//sb.append( a0100 + " ' and ");
				//sb.append(" nbase = '" + nbase + "' and ");
				//sb.append(" where p0100 = '" + p0100 + "') ");
				sb.append(" where a0100 = ( select a0100 from p01 where p0100 = '" + p0100 + "') and ");
				sb.append(" nbase = ( select nbase from p01 where p0100 = '" + p0100 + "') and ");
				sb.append(" belong_type = 1 and ");
			}
		}else if("2".equals(isdept)){  //部门进入 
			sb.append(" where belong_type = 2 and ");
			if("1".equals(opt)){
				sb.append("e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				sb.append("' and ");
			}else if("2".equals(opt)){ //团队总结进入部门
				sb.append("e0122 = ( select codeitemid from organization where ");
				sb.append(" codeitemid = (select parentid from organization where");
				sb.append(" codeitemid = (select e0122 from usra01 where a0100 = (select");
				sb.append(" a0100 from p01 where p0100 = '"+p0100+"')))");
				sb.append(" and codesetid = 'UM') and ");
			}
		}
		sb.append(Sql_switcher.year("p0104") + "=" + year);
		sb.append(" and state = 3 and time = " + season + " )");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			System.out.println(sb.toString());
			rs = dao.search(sb.toString());
			if(rs.next()){
				states = rs.getString("codeitemdesc") + "," + rs.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return states;
	}
	
	/**
	 *  得到某人的父部门ID
	 * */
	public String getParentDeptIdByUser(String a0100,String nbase){
		StringBuffer sb = new StringBuffer();
		sb.append(" select codeitemid from organization where ");
		sb.append(" codeitemid = (select parentid from organization where");
		sb.append(" codeitemid = (select e0122 from "+nbase+"a01 where a0100 = '"+a0100+"'))");
		sb.append(" and codesetid = 'UM'");
		RowSet rs = null;
		String parentid = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				parentid = rs.getString("codeitemid");
				if(parentid==null || "".equals(parentid)){
					parentid = this.userView.getUserDeptId();
				}
			}else{
				parentid = this.userView.getUserDeptId();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parentid;
	}
	
	/**
	 *  根据年份 得到当前登录人某年的年报状态
	 * */
	public String getStatesByYear(int year , String opt , String p0100,String isdept){
		String states = "";
		RowSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select codeitemdesc , codeitemid from codeitem ");
		sb.append(" where codesetid = '23' and codeitemid = (");
		sb.append(" select p0115 from p01 ");
		if("1".equals(isdept)){
			if("1".equals(opt)){
				sb.append(" where a0100 = '");
				sb.append(this.userView.getA0100() + "' and ");
				sb.append(" nbase = '" + this.userView.getDbname() + "' and ");
				if(this.isChuZhang(this.userView.getA0100())){ //职员 belong_type = 0||null 处长 1
					//sb.append(" belong_type = 1 and ");
					sb.append("((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
					sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
					sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
					sb.append(" and ");
				}else{
					sb.append(" (belong_type = 0 or belong_type = null) and ");
				}
			}else if("2".equals(opt)){
				//sb.append(a0100 + "' and ");
				//sb.append(" nbase = '" + nbase + "' and ");
				//sb.append(" where p0100 = '" + p0100 + "')");
				sb.append(" where a0100 = ( select a0100 from p01 where p0100 = '" + p0100 + "') and ");
				sb.append(" nbase = ( select nbase from p01 where p0100 = '" + p0100 + "') and ");
				sb.append(" belong_type = 1 and ");
			}
		}else if("2".equals(isdept)){
			sb.append(" where belong_type = 2 and ");
			if("1".equals(opt)){
				sb.append("e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
				sb.append("' and ");
			}else if("2".equals(opt)){
				sb.append("e0122 = ( select codeitemid from organization where ");
				sb.append(" codeitemid = (select parentid from organization where");
				sb.append(" codeitemid = (select e0122 from usra01 where a0100 = (select");
				sb.append(" a0100 from p01 where p0100 = '"+p0100+"')))");
				sb.append(" and codesetid = 'UM') and ");
			}
		}
		sb.append(Sql_switcher.year("p0104") + "=" + year);
		sb.append(" and state = 4)");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			System.out.println(sb.toString());
			rs = dao.search(sb.toString());
			if(rs.next()){
				states = rs.getString("codeitemdesc") + "," + rs.getString("codeitemid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return states;
	}
	/**
	 *  得到P01表中P0100最大值加1
	 * */
	public int getMaxP01(){
		int max = 0;
		RowSet rs = null;
		StringBuffer sb = new StringBuffer(" select max(p0100) as p0100 from p01");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				max = rs.getInt("p0100") + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return max;
	}
	
	/**
	 * 	前台内容字数不得超过70个
	 * */
	public static String checkContent(String content){
		if(content.length() > 70){
			content = content.substring(0,70) + "...";
		}
		return content;
	}
	
	/**
	 * 	根据传入的人员 查找是否有直接上级 判断显示按钮 (团队总结进入)
	 * */
	public boolean isShowButton(String a0100 , String nbase , String relation){
		StringBuffer sb = new StringBuffer();
		sb.append(" select mainbody_id from t_wf_mainbody ");
		sb.append(" where sp_grade = '9'");
		sb.append(" and object_id = '" + nbase+a0100 + "'");
		sb.append(" and relation_id = '");
		sb.append(relation + "'");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if(dao.search(sb.toString()).next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 	根据定义的审批关系 得到当前审批人(一个或者多个) 放入集合中
	 * */
	public ArrayList getCurrUsersById(String relation){
		ArrayList list = new ArrayList();
		MonthKqBean beans = null;
		RowSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append("select mainbody_id ,a0101 from t_wf_mainbody ");
		sb.append(" where relation_id = '");
		sb.append(relation + "'");
		sb.append(" and sp_grade = '9' ");//直接上级
		sb.append(" and object_id = '");
		sb.append(this.userView.getDbname()+this.userView.getA0100() + "'");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			//System.out.println(sql);
			while(rs.next()){
				beans = new MonthKqBean();
				beans.setCurrUser(rs.getString("mainbody_id"));
				beans.setItemdesc(rs.getString("a0101"));
				list.add(beans);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	/**
	 * 报批之前判断 当前人 当前年 当前季(季报 年报则是对应年)是否是否是对应的状态
	 * */
	public boolean isBaoPi(int year , int season , String  type , String opt , String p0100,String isdept){
		StringBuffer sb = new StringBuffer();
		RowSet rs = null;
		sb.append(" select * from p01");
		if("1".equals(isdept)){
			if("1".equals(opt)){
				sb.append(" where a0100 = '");
				sb.append(this.userView.getA0100() + "' and nbase = '" + this.userView.getDbname() + "'");
				sb.append(" and p0115 not in ('01','07')");
				sb.append(" and " + Sql_switcher.year("p0104") + "=" + year);
				if("1".equals(type)){  //季报
					sb.append(" and state = 3");
					sb.append(" and time = " + season);
				}else if("2".equals(type)){
					sb.append(" and state = 4");
				}
				if(this.isChuZhang(this.userView.getA0100())){
					sb.append(" and belong_type = 1");
				}else{
					sb.append(" and (belong_type = 0 or belong_type = null )");
				}
			}else if("2".equals(opt)){ //计划总结进入 条件 已报批 和我是当前审批人的时候
				//sb.append(a0100 + "' and nbase = '" + nbase + "'");
				sb.append(" where p0100 = '" + p0100 + "' ");
				sb.append(" and p0115 not in ('02') and curr_user = '");
				sb.append(this.getUsername(this.userView.getDbname() + this.userView.getA0100()) + "' ");
			}
		}else if("2".equals(isdept)){
			sb.append(" where belong_type = 2 ");
			sb.append(" and p0115 not in ('01','07')");
			sb.append(" and " + Sql_switcher.year("p0104") + "=" + year);
			if("1".equals(type)){  //季报
				sb.append(" and state = 3");
				sb.append(" and time = " + season);
			}else if("2".equals(type)){
				sb.append(" and state = 4");
			}
			sb.append(" and e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
			sb.append("'");
		}
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 	取得登录用户名
	 * */
	public String getUsername(String a0100)
	{
		RecordVo user_vo = new RecordVo(""+(a0100.substring(0,3)).toUpperCase()+"A01");
		user_vo.setString("a0100",a0100.substring(3));
		String name = this.userView.getUserName();
		try 
		{
			ContentDAO dao = new ContentDAO(this.conn);
			
			String usernamefield = ConstantParamter.getLoginUserNameField().toLowerCase();
			
			if(dao.isExistRecordVo(user_vo))
			{
				user_vo=dao.findByPrimaryKey(user_vo);
				if(user_vo!=null)
				{
					name = user_vo.getString(usernamefield);					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	/**
	 *  根据传入编码 取得对应库的人员信息
	 * */
	public String getUserNameByCode(String a0100){
		String userName = "";
		String nbase = a0100.substring(0,3);
		RowSet rs = null;
		String code = a0100.substring(3,a0100.length());
		String sql = "select a0101 from "+nbase+"a01 where a0100 = '"+code+"'";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if(rs.next()){
				userName = rs.getString("a0101");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}
	
	/**
	 *  根据A0100 查找当前登录人的岗位 类别(职员还是处长)
	 * */
	public boolean isChuZhang(String a0100){
		StringBuffer sb = new StringBuffer();
		sb.append(" select c01sc from usra01 ");
		sb.append(" where a0100 = '" + a0100 + "'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				if(!"03".equals(rs.getString("c01sc")) && !"04".equals(rs.getString("c01sc"))){
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 *  根据A0100 查找当前登录人的岗位 类别
	 * */
	public String getC01scById(String a0100){
		String c01sc = "";
		StringBuffer sb = new StringBuffer();
		sb.append(" select c01sc from usra01 ");
		sb.append(" where a0100 = '" + a0100 + "'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				c01sc = rs.getString("c01sc");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c01sc;
	}
	
	/**
	 * 	报批季报 年报
	 * */
	public boolean BaoPi(int year , int season , String currName , String userName , String type , String opt , String p0100,String isdept){
		StringBuffer sb = new StringBuffer();
		if("1".equals(isdept)){  //个人进入
			sb.append("update p01 set p0115 = '02',");
			sb.append("curr_user = '" + userName + "',");
			//如果夸部门了 或者 我的岗位类别大于我报的人的岗位类别(比如处长和职员)
			if(!this.userView.getUserDeptId().equals(this.getCurrDeptByCurr(currName))
					|| Integer.parseInt(this.getC01scById(this.userView.getA0100())) < 
					Integer.parseInt(this.getC01scById(currName.substring(3,currName.length())))){
				sb.append("belong_type = 1,");
			}
			sb.append("appuser = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("appuser"),"''")+Sql_switcher.concat() + "'");
			sb.append(userName +";'");
			if("1".equals(opt)){ //自己进入
				sb.append(" where " + Sql_switcher.year("p0104") + "=" + year);
				if("1".equals(type)){
					sb.append(" and time = " + season);
					sb.append(" and state = 3 ");
				}else if("2".equals(type)){
					sb.append(" and state = 4 ");
				}
				sb.append(" and a0100 = '" + this.userView.getA0100() + "' and ");
				sb.append("nbase = '" + this.userView.getDbname() + "'");
				//if(this.isChuZhang(this.userView.getA0100())){ //职员 belong_type = 0||null 处长 1
				//	sb.append(" and belong_type = 1");
				//}else{
					sb.append(" and (belong_type = 0 or belong_type = null)");
				//}
			}else if("2".equals(opt)){//领导进入
				//sb.append(" and a0100 = '" + a0100 + "' and ");
				//sb.append("nbase = '" + nbase);
				sb.append(" where p0100 = '" + p0100 + "' ");
			}
		}else if("2".equals(isdept)){  // 部门进入 
			sb.append(" update p01 set p0115 = '02',");
			sb.append("appuser = " + Sql_switcher.isnull(Sql_switcher.sqlToChar("appuser"),"''")+Sql_switcher.concat() + "'");
			sb.append(this.getUsername(this.userView.getDbname() + this.userView.getA0100()) +";'");
			sb.append(" where " + Sql_switcher.year("p0104") + "=" + year);
			if("1".equals(type)){
				sb.append(" and time = " + season);
				sb.append(" and state = 3 ");
			}else if("2".equals(type)){
				sb.append(" and state = 4 ");
			}
			sb.append(" and e0122 = '" + this.getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname()));
			sb.append("'");
			sb.append(" and belong_type = 2");
		}
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			//System.out.println(sb.toString());
			if(dao.update(sb.toString()) > 0 ){				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 部门汇总(季报、年报) 
	 * */
	public String getHuizongInfo(String type , String year , String season ,String isdept){
		String message = "";
		RowSet rs = null;
		StringBuffer sb = new StringBuffer();
		sb.append(" select p0103 from p01 ");
		sb.append(" where " + Sql_switcher.year("p0104") + "=" + year);
		if("1".equals(type)){
			sb.append(" and time = " + season);
			sb.append(" and state = 3");
		}else if("2".equals(type)){
			sb.append(" and state = 4");
		}
		sb.append(" and curr_user = '" + this.getUsername(this.userView.getDbname() + this.userView.getA0100()) + "' ");
		if("1".equals(isdept)){
			//一个部门 已报批状态 当前审批人是我  belong_type = 0 || null 
			sb.append(" and (belong_type = 0 or belong_type = null) and p0115 = '02'");
			sb.append(" and e0122 = '" + this.userView.getUserDeptId() + "'");
		}else if("2".equals(isdept)){
			sb.append(" and belong_type = 1 and p0115 = '02'");
		}
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			while(rs.next()){
				message += Sql_switcher.readMemo(rs, "p0103") + " ";
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * 	得到当前审批人的部门(主要用于处长报批是否夸部门报批)
	 * */
	public String getCurrDeptByCurr(String currUser){
		String e0122 = "";
		String nbase = currUser.substring(0,3);
		String a0100 = currUser.substring(3,currUser.length());
		StringBuffer sb = new StringBuffer();
		sb.append(" select e0122 from " + nbase + "a01 ");
		sb.append(" where a0100 = '" + a0100 + "'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				e0122 = rs.getString("e0122");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return e0122;
	}
	
	
	
	/**
	 *  通过P0100得到进入的是年还是季(主用于团队进入)
	 * */
	public String isYearOrSeason(String p0100){
		StringBuffer sb = new StringBuffer();
		sb.append(" select state from p01 ");
		sb.append(" where p0100 = '" + p0100 + "'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				if(3 == rs.getInt("state")){ //季
					return "1";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "2";
	}
	
	/**
	 * 	导出月度总结(季报 月报 年报均可调用)
	 * */
	public String creatExcel(String year, String season, String startMonth ,String endMonth ,String isdept,String a0100,String nbase,String departid,String parentid,String type) throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建新的Excel 工作簿
		HSSFSheet sheet = wb.createSheet();
		// sheet.setProtect(true);

		HSSFFont font1 = wb.createFont(); //设置样式
		font1.setFontHeightInPoints((short) 20);
//		font1.setBoldweight((short) 500);
		font1.setBold(true);
		font1.setColor(HSSFFont.COLOR_NORMAL);
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setFont(font1);
		style2.setAlignment(HorizontalAlignment.CENTER);
		style2.setVerticalAlignment(VerticalAlignment.TOP);
		style2.setWrapText(true);
		style2.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style2.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style2.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style2.setBorderBottom(BorderStyle.valueOf((short)1));
		// style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// style2.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFFont font2 = wb.createFont();
		font2.setFontHeightInPoints((short) 10);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font2);
		style1.setAlignment(HorizontalAlignment.LEFT);
		style1.setVerticalAlignment(VerticalAlignment.TOP);
		style1.setWrapText(true);
		style1.setBorderLeft(BorderStyle.valueOf((short)1));   //设置左边框   
        style1.setBorderRight(BorderStyle.valueOf((short)1));   //设置有边框   
        style1.setBorderTop(BorderStyle.valueOf((short)1));   //设置下边框 
        style1.setBorderBottom(BorderStyle.valueOf((short)1));
		// style1.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));// 文本格式

		HSSFCellStyle styleN = dataStyle(wb);
		styleN.setAlignment(HorizontalAlignment.RIGHT);
		styleN.setWrapText(true);
		HSSFDataFormat df = wb.createDataFormat();
		styleN.setDataFormat(df.getFormat(decimalwidth(0)));

		HSSFCellStyle styleCol0 = dataStyle(wb);
		HSSFFont font0 = wb.createFont();
		font0.setFontHeightInPoints((short) 5);
		styleCol0.setFont(font0);
		// styleCol0.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleCol0_title = dataStyle(wb);
		styleCol0_title.setFont(font2);
		// styleCol0_title.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));//
		// 文本格式
		// styleCol0_title.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// styleCol0_title.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);

		HSSFCellStyle styleF1 = dataStyle(wb);
		styleF1.setAlignment(HorizontalAlignment.RIGHT);
		HSSFFont font3 = wb.createFont(); //设置样式
		font3.setFontHeightInPoints((short) 3);
		styleF1.setFont(font3);
		styleF1.setWrapText(true);
		HSSFDataFormat df1 = wb.createDataFormat();
		styleF1.setDataFormat(df1.getFormat(decimalwidth(1)));

		HSSFCellStyle styleF2 = dataStyle(wb);
		styleF2.setAlignment(HorizontalAlignment.RIGHT);
		styleF2.setFont(font3);
		styleF2.setWrapText(true);
		HSSFDataFormat df2 = wb.createDataFormat();
		styleF2.setDataFormat(df2.getFormat(decimalwidth(2)));

		HSSFCellStyle styleF3 = dataStyle(wb);
		styleF3.setAlignment(HorizontalAlignment.RIGHT);
		styleF3.setFont(font3);
		styleF3.setWrapText(true);
		HSSFDataFormat df3 = wb.createDataFormat();
		styleF3.setDataFormat(df3.getFormat(decimalwidth(3)));

		HSSFCellStyle styleF4 = dataStyle(wb);
		styleF4.setAlignment(HorizontalAlignment.RIGHT);
		styleF4.setFont(font3);
		styleF4.setWrapText(true);
		HSSFDataFormat df4 = wb.createDataFormat();
		styleF4.setDataFormat(df4.getFormat(decimalwidth(4)));

		HSSFCellStyle styleF5 = dataStyle(wb);
		styleF5.setAlignment(HorizontalAlignment.RIGHT);
		styleF5.setFont(font3);
		styleF5.setWrapText(true);
		HSSFDataFormat df5 = wb.createDataFormat();
		styleF5.setDataFormat(df5.getFormat(decimalwidth(5)));
		sheet.setColumnWidth(Short.parseShort(String.valueOf(0)),(short)40000);
		HSSFRow row = sheet.getRow(0);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CONTENT FROM PER_DIARY_CONTENT ");
		sql.append("WHERE P0100 IN ");
		sql.append("(");
		sql.append("SELECT P0100 FROM P01");
		sql.append(" WHERE " + Sql_switcher.year("P0104") + "=" + year);
		sql.append(" AND " + Sql_switcher.month("P0104") + " >="+startMonth);
		sql.append(" AND " + Sql_switcher.month("P0104") + " <="+endMonth);
		sql.append(" AND STATE = 2");
		if("1".equals(isdept)){//如果是个人
			if(this.isChuZhang(this.userView.getA0100())){//处长
				sql.append(" and belong_type = 1 and e0122='"+departid+"'");
			}else{
				sql.append(" and (belong_type = 0 or belong_type = null) ");
				sql.append(" and a0100='"+a0100+"' and nbase ='"+nbase+"'");
			}
		}else if("2".equals(isdept)){//部门
			sql.append(" and belong_type = 2 and e0122='"+parentid+"'");
		}
		sql.append(")");
		sql.append(" AND LOG_TYPE = 2");
		if (row == null) {
			row = sheet.createRow(0);
		}
		row.setHeight((short)600);
		HSSFCell cell = null;
		String months = this.getMonthsBySeason(season);
		StringBuffer sb = new StringBuffer();
		if(row.getRowNum() == 0){//第一行
			if("1".equals(type)){				
				if(!"".equals(months.trim())){				
					String [] month = months.split(",");
					sb.append(year + "年 "  + month[0] + "月 - " + month[1] + "月总结");
				}
			}else if("3".equals(type)){
				sb.append(year + "年" + startMonth + "月总结");
			}else if("2".equals(type)){
				sb.append(year + "年年度总结");
			}
			cell = row.getCell(0);
			if (cell == null) {
				cell = row.createCell(0);
			}
			cell.setCellValue(cellStr(sb.toString()));
			cell.setCellStyle(style2);
			
			row = sheet.createRow(1); 
			row.setHeight((short) 6000);
		}
		
		StringBuffer ss = new StringBuffer();
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			//System.out.println(sql.toString());
			rs = dao.search(sql.toString());
			int i = 0;
			while(rs.next()){
				i++;
				ss.append("  "+i+"、"+Sql_switcher.readMemo(rs, "content")+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		cell = row.getCell(0);
		if(cell == null){
			cell = row.createCell(0);
		}
		cell.setCellValue(ss.toString());
		cell.setCellStyle(style1);
		
		String outName =  "season"+ PubFunc.getStrg() + ".xls";
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(System
					.getProperty("java.io.tmpdir")
					+ System.getProperty("file.separator") + outName);
			wb.write(fileOut);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(wb);
		}
		outName = outName.replace(".xls", "#");
		return outName;

	}
	
	public HSSFRichTextString cellStr(String context) {
		HSSFRichTextString textstr = new HSSFRichTextString(context);
		return textstr;
	}

	public String decimalwidth(int len) {

		StringBuffer decimal = new StringBuffer("0");
		if (len > 0) {
            decimal.append(".");
        }
		for (int i = 0; i < len; i++) {
			decimal.append("0");
		}
		decimal.append("_ ");
		return decimal.toString();
	}

	public HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
		HSSFCellStyle style = workbook.createCellStyle();
		// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		return style;
	}
	
	/**
	 * 2013-03-14(新加) 删除
	 * */
	public boolean deleteFileInfo(String p0100,String fileId){
		StringBuffer sb = new StringBuffer();
		ArrayList list = new ArrayList();
		sb.append("delete from per_diary_file");
		sb.append(" where p0100 = ? and file_id = ?");
		list.add(p0100);
		list.add(fileId);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if(dao.delete(sb.toString(), list) > 0){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 判断per_diary_file表中是否有p0100=p0100的数据
	 * **/
	public boolean isHaveP0100(String p0100){
		StringBuffer sb = new StringBuffer();
		ArrayList list = new ArrayList();
		sb.append("select * from per_diary_file");
		sb.append(" where p0100 = '"+p0100+"'");
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sb.toString());
			if(rs.next()){//如果还有其他文件关联此p0100
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	
	}
	/**
	 * 删除时删除p01表
	 * **/
	public boolean deleteP01Info(String p0100){

		StringBuffer sb = new StringBuffer();
		ArrayList list = new ArrayList();
		sb.append("delete from p01");
		sb.append(" where p0100 = ?");
		list.add(p0100);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if(dao.delete(sb.toString(), list) > 0){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 上传之前判断是否在P01表中已经有记录
	 * */
	public boolean isHavingInfo(String year ,String season,String type ){
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from p01 where ");
		sb.append(Sql_switcher.year("P0104") + "=" + year);
		if("1".equals(type)){//季
			sb.append(" and time = " + season );
			sb.append(" and state = 3");
		}else if("2".equals(type)){//年
			sb.append(" and state = 4");
		}
		sb.append(" and a0100 = '" + this.userView.getA0100() + "'");
		sb.append(" and nbase = '" + this.userView.getDbname() + "'");
		if(this.isChuZhang(this.userView.getA0100())){
			sb.append(" and ((belong_type = 1 and e0122 = '" + this.userView.getUserDeptId() + "')");
			sb.append(" or (a0100 = '" + this.userView.getA0100() + "' and ");
			sb.append(" nbase = '" + this.userView.getDbname() + "' and belong_type = 0))");
		}else{
			sb.append(" and (belong_type = 0 or belong_type = null)");
		}
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 	上传时 如果没有记录 则新增一条记录
	 * */
	public String InsertInfoToP01(String isdept,String type ,int season,int year,Date startDate , Date endDate){
		String p0100 = "";
		RecordVo vo = new RecordVo("P01");
		vo.setInt("p0100", getMaxP01());
		int tmpp0100 = getMaxP01();
		vo.setString("b0110", this.userView.getUserOrgId());    //登录用户单位编码
		if("2".equals(isdept)){
			String tmpdepartid = getParentDeptIdByUser(this.userView.getA0100(), this.userView.getDbname());
			vo.setString("e0122", tmpdepartid);   //登录用户部门编码
		}else if("1".equals(isdept)){
			vo.setString("e0122", this.userView.getUserDeptId());   //登录用户部门编码
		}
		
		vo.setString("e01a1", this.userView.getUserPosId());    //登录用户岗位编码
		vo.setString("a0101", this.userView.getUserFullName()); //登录用户名字
		vo.setString("nbase", this.userView.getDbname());       //登录用户人员库
		vo.setString("a0100", this.userView.getA0100());        //人员编码
		if("2".equals(isdept)){
			vo.setString("belong_type", "2");
		}else if("1".equals(isdept)){
			if(this.isChuZhang(this.userView.getA0100())){ //如果是处长 1
				vo.setString("belong_type", "1");
			}else{                                         //如果是职员 2
					vo.setString("belong_type", "0");          
			}
		}
		if("1".equals(type)){	    //季报			
			vo.setInt("time", season);
			vo.setInt("state", 3);
		}else if("2".equals(type)){ //年报
			vo.setInt("state", 4);
		}
		vo.setDate("p0104", startDate);
		vo.setDate("p0106", endDate);
		vo.setString("p0115", "01");
		vo.setInt("log_type", 2);//1:计划 2：总结
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if(dao.addValueObject(vo) > 0){
				p0100 = tmpp0100 + "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p0100;
	}
	
	/**
	 *  上传时先新增一条记录到文件表中去
	 * */
	public int InsertInToFile(String p0100){
		int fileId = 0;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			RecordVo vo=new RecordVo("per_diary_file");
			fileId = DbNameBo.getPrimaryKey("per_diary_file","file_id",this.conn);
			vo.setInt("file_id", fileId);
			vo.setInt("p0100", Integer.parseInt(p0100));
			dao.addValueObject(vo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileId;
	}
	
	/**
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Blob getOracleBlob(FormFile file,String tablename,int fileId) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select content from ");
		strSearch.append(tablename);
		strSearch.append(" where file_id=");
		strSearch.append(fileId);		
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set content=EMPTY_BLOB() where file_id=");
		strInsert.append(fileId);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.conn);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream()); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
	
	/**
	 * 	提交功能 只改状态
	 * */
	public boolean commitInfo(String p0100){
		StringBuffer sb = new StringBuffer();
		sb.append("update p01 set p0115 = '02' where ");
		sb.append("p0100 = " + p0100);
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			if(dao.update(sb.toString()) > 0 ){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	/**得到本月总天数**/
	public int getTotalDay(String strDate){
		String dayCount = "-1";
		String currentYear = strDate.split("-")[0];
		String currentMonth = strDate.split("-")[1];
		GregorianCalendar ca = new GregorianCalendar();
		java.util.Date date = null;
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			String tmpdate = currentYear+"-"+currentMonth+"-"+"01";
			date=sf.parse(tmpdate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ca.setTime(date);
		int week = ca.get(Calendar.DAY_OF_WEEK)-1;//该月1号是周几
		WeekUtils wu = new WeekUtils();
		String tmp[] = wu.lastMonthStr(Integer.parseInt(currentYear),Integer.parseInt(currentMonth)).split("-");
		int tmpdayCount = Integer.parseInt(tmp[2]);//该月有几天
		return tmpdayCount;
	}
	public String[] getDetailByP0100(String p0100){
		String[] str = new String[3];
		try{
			StringBuffer sb = new StringBuffer("");
			sb.append("select a0100,nbase,e0122 from p01 where p0100='"+p0100+"'");
			RowSet rs  = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				str[0] = rs.getString("a0100")==null?"":rs.getString("a0100");
				str[1] = rs.getString("nbase")==null?"":rs.getString("nbase");
				str[2] = rs.getString("e0122")==null?"":rs.getString("e0122");
			}
			if(rs!=null){
				rs.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	public String[] getYearAndSeason(String p0100,String state){
		String[] str = new String[2];
		StringBuffer sb = new StringBuffer("");
		sb.append("select "+Sql_switcher.year("p0104")+" year,"+"time from p01");
		sb.append(" where state="+state);
		sb.append(" and p0100='"+p0100+"'");
		try{
			RowSet rs  = null;
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				str[0] = rs.getString("year");
				str[1] = rs.getString("time")==null?"":rs.getString("time");
			}
			if(rs!=null){
				rs.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return str;
	}
}
