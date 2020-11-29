package com.hjsj.hrms.businessobject.performance.nworkplan;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class WorkPlanSpBo {
	
	private Connection connection;
	
	private UserView userView;
	
	RecordVo basevo=null;
	
	private String state;
	
	public RecordVo getBasevo() {
		return basevo;
	}
	public void setBasevo(RecordVo basevo) {
		this.basevo = basevo;
	}

	String belong_type="0";
	/**
	 * 构造方法，初始化一些参数
	 * @param connection
	 * @param userView
	 * @param belong_type
	 * @param state
	 */
	public WorkPlanSpBo(Connection connection,UserView userView,String belong_type,String state){
		this.connection=connection;
		this.userView=userView;
		this.belong_type=belong_type;
		this.state=state;
		this.basevo=new RecordVo(this.userView.getDbname()+"A01");
		this.basevo.setString("a0100", this.userView.getA0100());
		try {
			ContentDAO dao = new ContentDAO(this.connection);
			this.basevo=dao.findByPrimaryKey(basevo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public ArrayList getDataList(String month,String season,String sp_type,String week,String content,String name){
		ArrayList list = new ArrayList();
		RowSet rowSet = null;
		try {
			StringBuffer sql = new StringBuffer();
			String c01sc = this.basevo.getString("c01sc");
			String e0122=this.basevo.getString("e0122");
			String pe0122=this.getParentId();
			String tSQL = this.getTYSQL();
			sql.append(" select * from p01 where ");
			sql.append(tSQL);
			//周
			if("1".equals(state)){
				sql.append(" and p0100 in (select p0100 from per_diary_content ");
				if(content!=null){
					sql.append(" where per_diary_content.content like '%"+content+"%' ");
				}
				if(name!=null&&!"".equals(name)){
					sql.append("  and per_diary_content.principal='"+name+"' ");			
				}
				sql.append(") and p0115 <> '01' ");
				if(!"all".equals(year)){
					sql.append(" and "+Sql_switcher.year("p0104")+" = '"+year+"'");
				}
				if(!"all".equals(week)&&!"".equals(week)){
					String[] str=new String[1];
					str=week.split("--");
					String week_begin=str[0];
					String week_end=str[1];
					sql.append(" and p01.p0104= "+Sql_switcher.dateValue(week_begin)+" and p01.p0106= "+Sql_switcher.dateValue(week_end));
				}				
				if(sp_type!=null&&!"".equals(sp_type)&&!"all".equals(sp_type)){
					sql.append(" and p01.p0115='"+sp_type+"' ");
				}
				sql.append(" order by p0104  desc");
				//月
			}else if("2".equals(state)){
				sql.append(" and p01.p0100 in (select p0100 from per_diary_content ");
				
				if(content!=null){
					sql.append(" where per_diary_content.content like '%"+content+"%' ");
				}
				if(name!=null&&!"".equals(name)){
					sql.append("  and per_diary_content.principal='"+name+"' ");			
				}

				
				sql.append(") and p0115 <> '01' ");				
				if(!"all".equals(year)){
					sql.append(" and "+Sql_switcher.year("p0104")+" = '"+year+"'");
				}
				if(!"all".equals(month)){
					sql.append(" and "+Sql_switcher.month("p0104")+"= '"+Integer.parseInt(month)+"'");
				}
				if(sp_type!=null&&!"".equals(sp_type)&&!"all".equals(sp_type)){
					sql.append(" and p01.p0115='"+sp_type+"' ");
				}
				sql.append(" order by p0104  desc");
				//季
			}else if("3".equals(state)){
				if(content!=null&&!"".equals(content)){
					sql.append(" and p01.p0100 in (select p0100 from per_diary_file  where per_diary_file.name like '%"+content+"%' )");
				}
				if(name!=null&&!"".equals(name)){
					sql.append(" and p01.a0101 like '"+name+"%' ");			
				}
				sql.append(" and p0115 <> '01' ");
				if(!"all".equals(year)){
					sql.append(" and  "+Sql_switcher.year("p0104")+"= '"+year+"'");
				}
				if(!"all".equals(season)){
					if("1".equals(season)){
						sql.append(" and "+Sql_switcher.month("p0104")+"='01' and "+Sql_switcher.month("p0106")+"='03'");
					}else if("2".equals(season)){
						sql.append(" and "+Sql_switcher.month("p0104")+"='04' and "+Sql_switcher.month("p0106")+"='06'");
					}else if("3".equals(season)){
						sql.append(" and "+Sql_switcher.month("p0104")+"='07' and "+Sql_switcher.month("p0106")+"='09'");
					}else if("4".equals(season)){
						sql.append(" and "+Sql_switcher.month("p0104")+"='10' and "+Sql_switcher.month("p0106")+"='12'");
					}
					
				}
				if(sp_type!=null&&!"".equals(sp_type)&&!"all".equals(sp_type)){
					sql.append(" and p01.p0115='"+sp_type+"' ");
				}
				
				sql.append(" order by p0104  desc");
				//年
			}else if("4".equals(state)){
				if(content!=null&&!"".equals(content)){
					sql.append(" and p01.p0100 in (select p0100 from per_diary_file where per_diary_file.name like '%"+content+"%' )");
				}
				if(name!=null&&!"".equals(name)){
					sql.append(" and p01.a0101 like '"+name+"%' ");			
				}
				sql.append(" and p0115 <> '01' ");				
				if(!"all".equals(year)){
					sql.append(" and  "+Sql_switcher.year("p0104")+"='"+year+"'");
				}
				if(sp_type!=null&&!"".equals(sp_type)&&!"all".equals(sp_type)){
					sql.append(" and p01.p0115='"+sp_type+"' ");
				}
				
				sql.append(" order by p0104  desc");
			}
			ContentDAO dao = new ContentDAO(connection);
			rowSet=dao.search(sql.toString());
			while(rowSet.next()){
				String b0110=AdminCode.getCodeName("UN", rowSet.getString("b0110"));
				String e012=AdminCode.getCodeName("UM", rowSet.getString("e0122"));
				String a0101=rowSet.getString("a0101");
				String code=rowSet.getString("a0100");//人员编码
				String curr_user=rowSet.getString("curr_user");//当前审批人
				String p0115=AdminCode.getCodeName("23", rowSet.getString("p0115"));//审批标志
				String isRead="1";//判断是查看(1)还是审批(2)
				if("已报批".equals(p0115)&&("01".equals(c01sc)|| "02".equals(c01sc))){
					isRead="2";
				}
				String p0100=rowSet.getString("p0100");//主键
				String belong_type=rowSet.getString("belong_type");//
				String start_time=String.valueOf(rowSet.getDate("p0104")) .substring(5, 7);
				String zhouqi="";
				if("1".equals(state)){
					zhouqi=String.valueOf(rowSet.getDate("p0104")).substring(0, 10)+"--"+String.valueOf(rowSet.getDate("p0106")).substring(0, 10);
				}else if("2".equals(state)){
					zhouqi="第"+start_time+"月";
				}else if("3".equals(state)){
					if("01".equals(start_time)){
						zhouqi="第一季";
					}else if("04".equals(start_time)){
						zhouqi="第二季";
					}else if("07".equals(start_time)){
						zhouqi="第三季";
					}else if("10".equals(start_time)){
						zhouqi="第四季";
					}
				}else if("4".equals(state)){
					zhouqi=String.valueOf(rowSet.getDate("p0104")).substring(0, 4)+"年";
				}
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("b0110",b0110);
				bean.set("e0122",e012);
				bean.set("a0101",a0101);
				bean.set("p0115",p0115);
				bean.set("zhouqi",zhouqi);
				bean.set("code",code);
				bean.set("curr_user",curr_user);
				bean.set("isRead",isRead);
				bean.set("p0100",p0100);
				bean.set("belong_type",belong_type);
				bean.set("a0101", rowSet.getString("a0101")==null?"":rowSet.getString("a0101"));
				list.add(bean);
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
	private String currWeek;
	public String getCurrWeek() {
		return currWeek;
	}
	public void setCurrWeek(String currWeek) {
		this.currWeek = currWeek;
	}
	/**
	 * 得到周区间列表和默认显示的周区间
	 * @param year
	 * @return
	 */
	public ArrayList getWeekList(String year){
        ArrayList list = new ArrayList();
		RowSet rowSet=null;
	    
		try {
			CommonData cd0=new CommonData("all","全部");
			list.add(cd0);
			Calendar calendar = Calendar.getInstance();
			StringBuffer buffer=new StringBuffer();
			buffer.append(" select  p0104,p0106 ");
			buffer.append(","+Sql_switcher.year("p0104")+"*10000+"+Sql_switcher.month("p0104")+"*100+"+Sql_switcher.day("p0104")+" as ip04");
			buffer.append(","+Sql_switcher.year("p0106")+"*10000+"+Sql_switcher.month("p0106")+"*100+"+Sql_switcher.day("p0106")+" as ip06");
			buffer.append(" from p01 where "+this.getTYSQL());
			buffer.append(" and p0115 <> '01' ");
			buffer.append(" order by p0104 desc,p0106  desc");
			ContentDAO dao = new ContentDAO(connection);
			rowSet =  dao.search(buffer.toString());
			String key="";
			String value="";
			HashMap map = new HashMap();
			String currWeek="";
			String cWeek="";
			int currInt =  calendar.get(Calendar.YEAR)*10000+(calendar.get(Calendar.MONTH)+1)*100+calendar.get(Calendar.DAY_OF_MONTH);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			while(rowSet.next()){
				key=format.format(rowSet.getDate("p0104"))+"--"+format.format(rowSet.getDate("p0106"));
				value=format.format(rowSet.getDate("p0104"))+"--"+format.format(rowSet.getDate("p0106"));	
				if(map.get(key)!=null){
					continue;
				}
				int ip04=rowSet.getInt("ip04");
				int ip06=rowSet.getInt("ip06");
				if(ip04<=currInt&&ip06>=currInt)
				{
					currWeek=value;
				}
				cWeek=value;
				map.put(key, value);
				CommonData cd2=new CommonData(key,value);//前台展示 值，后台传输 
				list.add(cd2);
			}
			if(currWeek!=null&&!"".equals(currWeek)) {
                cWeek=currWeek;
            }
			this.setCurrWeek(cWeek);
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
	 * 一段通用SQL
	 * @return
	 */
	public String getTYSQL(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(" belong_type="+belong_type+" ");
		buffer.append(" and state="+state);
		if(year!=null&&!"".equals(year)) {
            buffer.append(" and ("+Sql_switcher.year("p0104")+"="+year+" or "+Sql_switcher.year("p0106")+"="+year+")");
        }
		if(this.basevo.getString("c01sc")==null|| "".equals(this.basevo.getString("c01sc"))) {
            buffer.append(" 1=2 ");
        } else if("01".equals(this.basevo.getString("c01sc"))|| "02".equals(this.basevo.getString("c01sc"))){
			buffer.append(" and e0122 like '"+this.basevo.getString("e0122")+"%'");
		}else if("03".equals(this.basevo.getString("c01sc"))|| "04".equals(this.basevo.getString("c01sc"))){
			if("0".equals(belong_type)){//个人，自己处室的
				buffer.append(" and e0122='"+this.basevo.getString("e0122")+"' ");
			}else if("1".equals(belong_type)){//处室的
				buffer.append(" and e0122='"+this.basevo.getString("e0122")+"' ");
			}else if("2".equals(belong_type)){//部门的
				buffer.append(" and e0122='"+this.getParentId()+"' ");
			}
		}else if("05".equals(this.basevo.getString("c01sc"))|| "06".equals(this.basevo.getString("c01sc"))){
			if("0".equals(belong_type)){//个人，自己处室的
				buffer.append(" and UPPER(nbase)='"+this.userView.getDbname().toUpperCase()+"' and a0100='"+this.userView.getA0100()+"' ");
			}else if("1".equals(belong_type)){//处室的
				buffer.append(" and e0122='"+this.basevo.getString("e0122")+"' ");
			}else if("2".equals(belong_type)){//部门的
				buffer.append(" and e0122='"+this.getParentId()+"' ");
			}
		}else{
			buffer.append(" 1=2 ");
		}
		return buffer.toString();
	}
	private String year;
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	private String month;
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	/**
	 * 得到显示的年列表
	 * @return
	 */
	public ArrayList getYearList(){
		ArrayList list = new ArrayList();
		RowSet rowSet=null;
		try {
			Calendar calendar = Calendar.getInstance();
			StringBuffer buffer=new StringBuffer();
			buffer.append(" select  distinct "+Sql_switcher.year("p0104")+" as p0104 from p01 where ");
			buffer.append(this.getTYSQL());
			buffer.append(" order by p0104 desc");
			ContentDAO dao = new ContentDAO(connection);
			rowSet =  dao.search(buffer.toString());
			String key="";
			String value="";
			HashMap map = new HashMap();
			String currYear="";
			while(rowSet.next()){
				key=rowSet.getString("p0104");
				value=key;
				currYear=value;
				if(map.get(key)!=null){
					continue;
				}
				map.put(key, value);
				CommonData obj2=new CommonData(key,value);//前台展示 值，后台传输 
				list.add(obj2);
			}
			String yy=calendar.get(Calendar.YEAR)+"";
			String month = (calendar.get(Calendar.MONTH)+1)+"";
			String currMonth="";
			if(map.containsKey(yy)){
				currYear=yy;
			}else {
				CommonData obj2=new CommonData(yy,yy);//前台展示 值，后台传输 
				list.add(obj2);
				currYear=yy;
			}
			this.setMonth(month);
			this.setYear(currYear);
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
	 * 取得所在部门的父部门，如果没有，返回本部门
	 * @return
	 */
	public String getParentId(){
		String parentidString="";
		RowSet rowSet=null;
	    try {
			String sqlString="select codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+this.basevo.getString("e0122")+"' and UPPER(codesetid)='UM')";
			ContentDAO dao = new ContentDAO(connection);
			rowSet=dao.search(sqlString);
			while(rowSet.next()){
				parentidString=rowSet.getString("codeitemid")==null?"":rowSet.getString("codeitemid");
			}
			if(parentidString==null|| "".equals(parentidString)) {
                parentidString=this.basevo.getString("e0122");
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
		return parentidString;
	}
	/**
	 * 审批状态列表
	 * @return
	 */
	public static ArrayList getSpList (){
		ArrayList sptypelist=new ArrayList();//审批状态
		String key="";
		String value="";
		for(int i=0;i<4;i++){
			if(i==0){
				key="all";
				value="全部";
			}
			if(i==1){
				key="02";
				value="已报批";
			}
			if(i==2){
				key="03";
				value="已批";
			}
			if(i==3){
				key="07";
				value="驳回";
			}
			CommonData obj=new CommonData(key,value);//前台展示 值，后台传输 键
			sptypelist.add(obj);
		}
		return sptypelist;
	}
	/**
	 * 月列表
	 * @return
	 */
	public static ArrayList getMonthList(){
		ArrayList monthlist=new ArrayList();//月
		//月份列
		String key="";
		String value="";
		for(int i=0;i<13;i++){
			if(i==0){
				key="all";
				value="全部";
			}else if(i<10){
				key="0"+String.valueOf(i);
				value=key;
			}else{
				key=String.valueOf(i);
				value=key;
			}
			CommonData obj=new CommonData(key,value);//前台展示 值，后台传输 键
			monthlist.add(obj);
		}
		return monthlist;
	}
	/**
	 * 季度列表
	 * @return
	 */
	public static ArrayList getSeasonList(){
		ArrayList seasonlist = new ArrayList();
		//季份列
		String key="";
		String value="";
		for(int i=0;i<5;i++){
			if(i==0){
				key="all";
				value="全部";
			}else{
			    key=String.valueOf(i);
			    value=key;
			}
			CommonData obj=new CommonData(key,value);//前台展示 值，后台传输 键
			seasonlist.add(obj);
		}
		return seasonlist;
	}
	

}
