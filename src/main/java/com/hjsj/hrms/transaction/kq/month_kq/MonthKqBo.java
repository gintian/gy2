package com.hjsj.hrms.transaction.kq.month_kq;

import com.hjsj.hrms.businessobject.kq.register.IfRestDate;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * 如果时间允许 把月度考勤表(国网)中的公用方法搬进来 便于查看统一管理代码整洁等
 * */
public class MonthKqBo {
	//数据库连接
	private Connection conn;
	private String xml;
	private Document doc;
	
	public  MonthKqBo() {
	}
		

	public MonthKqBo(Connection conn) {
		this.conn = conn;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * 判断当年月数据是否封存
	 * */
	public boolean IsFc(String year , String month){
		String sql = "select * from kq_duration where kq_year = '"+year+"' and kq_duration = '"+month+"' and finished ='1'";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 	判断kq_duration中是否已经有记录了
	 * */
	public boolean isHasRecord(String year , String month){

		String sql = "select * from kq_duration where kq_year = '"+year+"' and kq_duration = '"+month+"'";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(conn);
			rs = dao.search(sql);
			if(rs.next()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	
	}
	/**
	 * 	在引入人员之前向考勤期间表中插入 数据
	 * */
	public void insertIntoKqDuration(String year , String month){
		if(!isHasRecord(year,month)){//如果这个表中没有本年本月的记录
			if(this.IsFc(year, month)){
				StringBuffer sb = new StringBuffer();
				ArrayList list = new ArrayList();
				sb.append("insert into kq_duration ");
				sb.append("values(?,?,?,?,?,?,?)");//sysdate 
				list.add(year);
				list.add(month);
				list.add(new java.sql.Date(new Date().getTime()));//sysdate
				list.add(new java.sql.Date(new Date().getTime()));
				list.add("");
				list.add("");
				list.add("0");
				try {
					ContentDAO dao = new ContentDAO(this.conn);
					dao.insert(sb.toString(), list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void initXML (){
		//String xml = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(" select str_value  from CONSTANT where UPPER(CONSTANT)='KQ_MONTHLY'");
			if(rs.next()){
				xml = Sql_switcher.readMemo(rs,"STR_VALUE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return xml;
	}
	
	private void init() throws GeneralException{
		try {
			doc = PubFunc.generateDom(xml);
		} catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        }
	}
	/**
	 * 得到考勤默认值 此处没必要放在一起取出 所以分开写两方法 便于取出后使用
	 * */
	public String getParam()
	{
		String str="";
		try{
			this.initXML();
			this.init();
			String path="/param/Kq_Parameters";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				if(null != element.getAttributeValue("def_value")){					
					str = element.getAttributeValue("def_value");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	public String getParam1(){
		String str="";
		try{
			this.initXML();
			this.init();
			String path="/param/Kq_Parameters";
			XPath xPath = XPath.newInstance(path);
			Element element=(Element)xPath.selectSingleNode(this.doc);
			if(element!=null)
			{
				if(null != element.getAttributeValue("sp_relation")){					
					str = element.getAttributeValue("sp_relation");
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	
	public String getUserLoginNbaseInfo(){
		String nbase = "";
		
		return nbase;
	}
	
	/**
	 * 得到节假日信息 得到公休日方法直接调用考勤里面现有的方法
	 */
	public String getFeastType(){
		//ArrayList list = new ArrayList();
		//FeastType type = null;
		//String s1 = "";
		//String s2 = "";
		String s = "";
		String sql = "select * from kq_feast order by feast_id";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			while(rs.next()){
				if(","
						.equals(rs.getString("feast_dates").substring
                                (rs.getString("feast_dates").length()-1,rs.getString("feast_dates").length()))){
					s += rs.getString("feast_dates");
				}else{
					s += rs.getString("feast_dates") + ",";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	

	public int isGongXiu(int yearnum , int monthnum , int daynum , String b0110,UserView userView){
		Date date = null;
		String desc = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date=sdf.parse(yearnum+"-"+monthnum+"-"+daynum); 
		} catch (Exception e) {
		}
		GregorianCalendar cal = new GregorianCalendar(); 
		cal.setTime(date);
		 //if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY||cal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
		//	return 1;
		//}else{
		//	 return 0;
		// }
		int dates = cal.get(Calendar.DAY_OF_WEEK) -1;
		if(1 == dates){
			desc = "星期一";
		}else if(2 == dates){
			desc = "星期二";
		}else if(3 == dates){
			desc = "星期三";
		}else if(4 == dates){
			desc = "星期四";
		}else if(5 == dates){
			desc = "星期五";
		}else if(6 == dates){
			desc = "星期六";
		}else if(0 == dates){
			desc = "星期日";
		}
		//if(dates == 0){ //周末
		//	dates = 7;
		//}
		IfRestDate id = new IfRestDate();
		ArrayList list = id.search_RestOfWeek(b0110,userView,this.conn); //调用的考勤里面的方法 为避免冲突 新增了一个1方法做区别
		String restdate = list.get(0).toString();
		if(("," + restdate).indexOf(("," +desc+",")) !=-1){
			return 1;
		}else{			
			return 0;
		}
	}
	
	public int isJieJia(int year , int month , int day ){
		String m = "";
		String d = "";
		if(month <=9){
			m = "0" + month;
		}else{
			m = month + "";
		}
		if(day <=9){
			d = "0" + day;
		}else{
			d = day + "";
		}
		String date = year +"."+m+"."+d;
		String date1 = m + "." + d;
		String s = getFeastType();
		if(-1 != (","+s).indexOf((","+date+",")) || -1 != (","+s).indexOf((","+date1+","))){
			return 1;
		}else{
			return 0;
		}
	}
	
	public String getUserNameById(String userCode){
		String userName = "";
		if(!"".equals(userCode)){
		String usr = userCode.substring(0,3);
		String code = userCode.substring(3, userCode.length());
		String sql = "select a0101 from "+usr+"a01 where a0100 = '"+code+"'";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sql);
			if(rs.next()){
				userName = rs.getString("a0101");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		}
		return userName;
	}
	
	public String getImgByCodeId(String codeId){
		String img = "";
		StringBuffer sb = new StringBuffer("SELECT ITEM_SYMBOL FROM KQ_ITEM");
		sb.append(" WHERE ITEM_ID = '");
		sb.append(codeId + "'");
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search(sb.toString());
			if(rs.next()){
				if(rs.getString("ITEM_SYMBOL") != null){
					img = rs.getString("ITEM_SYMBOL");
				}else{
					img = "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}
}
