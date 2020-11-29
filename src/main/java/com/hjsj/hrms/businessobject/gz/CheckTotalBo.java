package com.hjsj.hrms.businessobject.gz;

import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/**
 * 检测导入薪资总额是否超出上级单位或部门
 * @author Owner
 *
 */
public class CheckTotalBo {
	private Connection conn = null;
	private UserView userView = null;
	private ArrayList itemlist = new ArrayList();
	private String fc_flag="";
	private HashMap unitcode=new HashMap();
	public CheckTotalBo(Connection conn,UserView userView,ArrayList itemlist){
		this.conn = conn;
		this.userView = userView;
		this.itemlist = itemlist;
		init();
	}
	public HashMap getUnitcode() {
		return unitcode;
	}
	public void setUnitcode(HashMap unitcode) {
		this.unitcode = unitcode;
	}
	/**
	 * 创建临时表
	 *
	 */
	private void init(){
		Table table=new Table("T#"+this.userView.getUserName()+"_gz");
		DbWizard dbWizard=new DbWizard(this.conn);
		if(dbWizard.isExistTable(table.getName(),false)){
			dbWizard.dropTable(table);
		}
		for(int i=0;i<itemlist.size();i++){
			String itemid = (String)itemlist.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if(fielditem!=null){
				table.addField(fielditem);
			}
		}
		try {
			dbWizard.createTable(table);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 数据导入临时表进行检测是否超出上级单位或部门
	 * @param valuelist //导入的值
	 * @param tablename  //薪资总额表
	 * @param ctrl_peroid  //年月控制标识=1按年，=0按月,=2按季度
	 * @param ctrl_type  //是否控制到部门，０控制，１不控制
	 * @return
	 */
	public String checkTatal(ArrayList valuelist,String tablename,String ctrl_peroid,String ctrl_type){
		String checkflag = "ok";
		ContentDAO dao = new ContentDAO(this.conn);
		StringBuffer addsql = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select B0110,");
		if("1".equals(ctrl_peroid)){
			sqlstr.append(Sql_switcher.year(tablename+"Z0")+" as aaaa");
		}else if("0".equals(ctrl_peroid)){
			if(Sql_switcher.searchDbServer() == Constant.ORACEL||Sql_switcher.searchDbServer() == Constant.DB2){
				sqlstr.append("to_char("+tablename+"Z0,'YYYY-MM') as aaaa ");
			}else{
				sqlstr.append("ltrim(str("+Sql_switcher.year(tablename+"Z0"));
				sqlstr.append("))+'.'+ltrim(str("+Sql_switcher.month(tablename+"Z0")+")) as aaaa");
			}
		}else{
			sqlstr.append(Sql_switcher.quarter(tablename+"Z0")+" as aaaa");
		}
		String tabname = "T#"+this.userView.getUserName()+"_gz";
		
		addsql.append("update ");
		addsql.append(tabname);
		addsql.append(" set ");
		String[] itemArr = new String[itemlist.size()-2];
		int n=0;
		for(int i=0;i<itemlist.size();i++){
			String itemid = (String)itemlist.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if(fielditem!=null){
				if(!itemid.equalsIgnoreCase(tablename+"Z0")&&!"B0110".equalsIgnoreCase(itemid)){
					addsql.append(itemid);
					addsql.append("=?,");
					
					sqlstr.append(",(case when (select sum("+itemid+") from");
					sqlstr.append(" "+tabname+" b ");
					sqlstr.append("where b.B0110 in(select codeitemid from organization ");
					sqlstr.append("where parentid="+tabname+".B0110 and codeitemid<>parentid ");
					if("1".equals(ctrl_type)){
						sqlstr.append(" and codesetid='UN'");
					}
					sqlstr.append(") and b."+tablename+"z0="+tabname+".");
					sqlstr.append(tablename+"z0)>"+Sql_switcher.isnull(itemid,"0"));
					sqlstr.append(" then 1 else 0 end) as "+itemid);
					itemArr[n] = itemid;
					n++;
				}
			}
		}
		String sql = addsql.toString().substring(0,addsql.toString().length()-1);
		addsql.setLength(0);
		addsql.append(sql);
		addsql.append(" where ");
		addsql.append("B0110=? and ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
			addsql.append("to_char("+tablename+"Z0,'yyyy-mm-dd')=?");
		else
			addsql.append(tablename+"Z0=?");
		sqlstr.append(" from ");
		sqlstr.append(tabname);
		if("1".equals(ctrl_type)){
			sqlstr.append(" where B0110 in(select codeitemid from organization where codesetid='UN')");
		}
		try {
			insertTemp(dao,tablename,tabname,valuelist);
			dao.batchUpdate(addsql.toString(), valuelist);
			RowSet rs = dao.search(sqlstr.toString());
			String B0110 = "";
			String z0 = "";
			boolean flag = true;
			int values = 0;
			String itemid="";
			while(rs.next()){
				B0110 = rs.getString("B0110");
				if(this.unitcode.get(B0110)==null){
					continue;
				}
				z0 = rs.getString("aaaa");
				for(int i=0;i<itemArr.length;i++){
					itemid = itemArr[i];
					values = rs.getInt(itemid);
					if(values>0){
						flag = false;
						break;
					}
				}
				if(!flag)
					break;
			}
			if(!flag){
				FieldItem fielditem = DataDictionary.getFieldItem(itemid);
				CodeItem codeitem = AdminCode.getCode("UN",B0110);
				if(codeitem!=null){
					checkflag = codeitem.getCodename()+"年月标识为"+quarter(z0)+","+fielditem.getItemdesc()+"的值设置过小,小于下级部门的总额";
				}else{
					codeitem = AdminCode.getCode("UM",B0110);
					if(codeitem!=null)
						checkflag = codeitem.getCodename()+"年月标识为"+quarter(z0)+","+fielditem.getItemdesc()+"的值设置过小,小于下级部门的总额";
				}
			}
			Table table=new Table("T#"+this.userView.getUserName()+"_gz");
			DbWizard dbWizard=new DbWizard(this.conn);
			if(dbWizard.isExistTable(table.getName(),false)){
				dbWizard.dropTable(table);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return checkflag;
	}
	/**
	 * 处理导入的数据,使其符合导入的格式
	 * @param ctrl_peroid //年月控制标识=1按年，=0按月=2按季度
	 * @param list //导入的值
	 * @param itemlist //导入包含的字段
	 * @param getyear 年份
	 * @return
	 */
	public ArrayList ctrlToDate(String ctrl_peroid,ArrayList list,ArrayList itemlist,String getyear){
		ArrayList valuelist = new ArrayList();
		if("1".equals(ctrl_peroid)){
			
			for(int i=0;i<list.size();i++){
				ArrayList listvalue = (ArrayList)list.get(i);
				if(listvalue!=null&&listvalue.size()>0){
					int m=0;
					ArrayList alist = new ArrayList();
					ArrayList blist = new ArrayList();
					for(int j=0;j<itemlist.size();j++){
						String itemid = (String)itemlist.get(j);
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String values = (String)listvalue.get(j);
							if("N".equalsIgnoreCase(fielditem.getItemtype())){
								
								if(fielditem.getDecimalwidth()>0){
									double dl = Double.parseDouble(values);
									double d = moreThan(dl,12); 
									dl = dl-d;
									alist.add((dl/12+d)+"");
									blist.add((dl/12)+"");
								}else{
									int d = Integer.parseInt(values);
									String a = (d/12+d%12)+"";
									String b = d/12+"";
									alist.add(a+"");
									blist.add(b+"");
								}
							}else{
								alist.add(values);
								blist.add(values);
							}
							if(fielditem.getItemid().equalsIgnoreCase(fielditem.getFieldsetid()+"Z0")){
								m=j;
							}
						}
					}
					for(int n=1;n<13;n++){
						String date = "";
						if(n>9)
							date = getyear+"-"+n+"-01";
						else
							date = getyear+"-0"+n+"-01";
						if(n==1){
							ArrayList clist = new ArrayList();
							for(int u=0;u<alist.size();u++){
								clist.add((String)alist.get(u));
							}
							clist.set(m,date);
							valuelist.add(clist);
						}else{
							ArrayList clist = new ArrayList();
							for(int u=0;u<blist.size();u++){
								clist.add((String)blist.get(u));
							}
							clist.set(m,date);
							valuelist.add(clist);
						}
					}
				}
			}
		}else if("2".equals(ctrl_peroid)){
			for(int i=0;i<list.size();i++){
				ArrayList listvalue = (ArrayList)list.get(i);
				if(listvalue!=null&&listvalue.size()>0){
					int m=0;
					String q = "";
					ArrayList alist = new ArrayList();
					ArrayList blist = new ArrayList();
					for(int j=0;j<itemlist.size();j++){
						String itemid = (String)itemlist.get(j);
						FieldItem fielditem = DataDictionary.getFieldItem(itemid);
						if(fielditem!=null){
							String values = (String)listvalue.get(j);
							if("N".equalsIgnoreCase(fielditem.getItemtype())){
								
								if(fielditem.getDecimalwidth()>0){
									double dl = Double.parseDouble(values);
									double d = moreThan(dl,3); 
									dl = dl-d;
									alist.add((dl/3+d)+"");
									blist.add((dl/3)+"");
								}else{
									int d = Integer.parseInt(values);
									String a = (d/3+d%3)+"";
									String b = d/3+"";
									alist.add(a+"");
									blist.add(b+"");
								}
							}else{
								alist.add(values);
								blist.add(values);
							}
							if(fielditem.getItemid().equalsIgnoreCase(fielditem.getFieldsetid()+"Z0")){
								q = values;
								m=j;
							}
						}
					}
					String arr[] = quarter(q,getyear);
					for(int n=0;n<3;n++){
						String date = arr[n];
						if(n==0){
							ArrayList clist = new ArrayList();
							for(int u=0;u<alist.size();u++){
								clist.add((String)alist.get(u));
							}
							clist.set(m,date);
							valuelist.add(clist);
						}else{
							ArrayList clist = new ArrayList();
							for(int u=0;u<blist.size();u++){
								clist.add((String)blist.get(u));
							}
							clist.set(m,date);
							valuelist.add(clist);
						}
					}
				}
			}
		}else{
			valuelist = list;
		}
		return valuelist;
	}
	public String[] quarter(String q,String getyear){
		String arr[] = new String[3];
		if("第一季度".equalsIgnoreCase(q)){
			arr[0]=getyear+"-01-01";
			arr[1]=getyear+"-02-01";
			arr[2]=getyear+"-03-01";
		}else if("第二季度".equalsIgnoreCase(q)){
			arr[0]=getyear+"-04-01";
			arr[1]=getyear+"-05-01";
			arr[2]=getyear+"-06-01";
		}else if("第三季度".equalsIgnoreCase(q)){
			arr[0]=getyear+"-07-01";
			arr[1]=getyear+"-08-01";
			arr[2]=getyear+"-09-01";
		}else if("第四季度".equalsIgnoreCase(q)){
			arr[0]=getyear+"-10-01";
			arr[1]=getyear+"-11-01";
			arr[2]=getyear+"-12-01";
		}
		return arr;
	}
	public String quarter(String q){
		String str = "";
		if("1".equals(q)){
			str = "第一季度";
		}else if("2".equals(q)){
			str = "第二季度";
		}else if("3".equals(q)){
			str = "第三季度";
		}else if("4".equals(q)){
			str = "第四季度";
		}else{
			str = q;
		}
		return str;
	}
	public double moreThan(double dOrig,int m){
		int nTemp = (int)dOrig; 
		int nResult = nTemp % m; 
		double dResult = dOrig - nTemp + nResult; 
		return dResult;
	}
	/**
	 * 从薪资总额中导入原始数据到临时表
	 * @param dao
	 * @param tablename //薪资总额表
	 * @param temptable //临时表
	 * valuelist   //excel中的数据
	 * ctrl_period //年月控制标识=1按年，=0按月=2按季度
	 */
	private void insertTemp(ContentDAO dao,String tablename,String temptable,ArrayList valuelist){
		StringBuffer insql = new StringBuffer();
		//找出年份和月份  不用管是按月，按季或者是按年控制。因为valuelist全都已经处理好了。
		ArrayList volist = new ArrayList();
		ArrayList tempYearList = new ArrayList();//存放excel表中涉及到的年份
		ArrayList tempMonthList = new ArrayList();//存放excel表中涉及到的月份
		String time = "";
		for(int i=0;i<valuelist.size();i++){
			volist = (ArrayList)valuelist.get(i);
			if(volist!=null&&volist.size()>2){
				time = (String)volist.get(volist.size()-1);
				if(time!=null && !"".equals(time)){
					String[] temparray = time.split("-");
					String tempyear = temparray[0];
					String tempmonth = temparray[1];
					if(!"".equals(tempyear)){
						if(!tempYearList.contains(tempyear)){
							tempYearList.add(tempyear);
						}
					}
					if(!"".equals(tempmonth)){
						if(!tempMonthList.contains(tempmonth)){
							tempMonthList.add(tempmonth);
						}
					}
				}
			}
		}
		//把tempMonthList组合成适合sql语句的形式
		StringBuffer sqlstr = new StringBuffer("(");
		for(int i=0;i<tempMonthList.size();i++){
			sqlstr.append(tempMonthList.get(i)+",");
		}
		sqlstr.setLength(sqlstr.length()-1);
		sqlstr.append(")");
		
		insql.append("insert into ");
		insql.append(temptable);
		insql.append("(");
		String colum = "";
		for(int i=0;i<itemlist.size();i++){
			String itemid = (String)itemlist.get(i);
			FieldItem fielditem = DataDictionary.getFieldItem(itemid);
			if(fielditem!=null){
				colum += itemid+",";
			}
		}
		colum = colum.substring(0,colum.length()-1);
		insql.append(colum);
		insql.append(")select ");
		insql.append(colum);
		insql.append(" from ");
		insql.append(tablename);
		insql.append(" where ");
		if(this.fc_flag!=null&&this.fc_flag.length()!=0){
			/* 薪资管理-薪资总额-导入数据,报(直属单位年月标识为2014-11，加班工资总额值过小，小于下级部门的总额) xiaoyun 2014-10-25 start */
			insql.append("(");
			insql.append(this.fc_flag+"=2 or "+this.fc_flag+" is null");
			insql.append(")");
			/* 薪资管理-薪资总额-导入数据,报(直属单位年月标识为2014-11，加班工资总额值过小，小于下级部门的总额) xiaoyun 2014-10-25 end */
			insql.append(" and ");
		}
		insql.append(Sql_switcher.year(tablename+"Z0")+"="+tempYearList.get(0));
		insql.append(" and ");
		insql.append(Sql_switcher.month(tablename+"Z0")+" in "+sqlstr);
		try {
			dao.update(insql.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 检查是否导入的数据在库中已经发布
	 * @param dao
	 * @param tablename //薪资总额表名
	 * @param valuelist //导入值
	 * @param spflagid //审批字段
	 * @return
	 */
	public boolean checkSpflag(ContentDAO dao,String tablename,ArrayList valuelist,String spflagid){
		boolean spflag = false;
		StringBuffer b0110str = new StringBuffer();
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select ");
		sqlstr.append(spflagid);
		sqlstr.append(" from ");
		sqlstr.append(tablename);
		sqlstr.append(" where (");
		sqlstr.append(spflagid);
		sqlstr.append("='04' or ");
		sqlstr.append(spflagid);
		sqlstr.append("='02' or ");
		sqlstr.append(spflagid);
		sqlstr.append("='03' ) "); 
		String mintime = "";
		String maxtime = "";
		String b0110 = "";
		String time = "";
		ArrayList volist = new ArrayList();
		WeekUtils wu = new WeekUtils();
		for(int i=0;i<valuelist.size();i++){
			volist = (ArrayList)valuelist.get(i);
			if(volist!=null&&volist.size()>2){
				b0110 = (String)volist.get(volist.size()-2);
				time = (String)volist.get(volist.size()-1);
				if(b0110str.indexOf(b0110)==-1){
					b0110str.append("'");
					b0110str.append(b0110);
					b0110str.append("',");
				}
				if(mintime==null||mintime.length()<1){
					mintime = time;
					maxtime = time;
				}else{
					if(WeekUtils.compareTime(wu.strTodate(mintime), wu.strTodate(time))){
						mintime  = time;
					}
					if(WeekUtils.compareTime(wu.strTodate(time), wu.strTodate(maxtime))){
						maxtime  = time;
					}
				}
			}
		}
		if(b0110str.length()>1){
			sqlstr.append(" and B0110 in(");
			sqlstr.append(b0110str.substring(0, b0110str.length()-1));
			sqlstr.append(")");
		}
		if(mintime.length()>0&&maxtime.length()>0){
			WorkdiarySQLStr wss=new WorkdiarySQLStr();
			sqlstr.append(" and ");
			sqlstr.append(wss.getDataValue(tablename+"Z0",">=",mintime));
			sqlstr.append(" and ");
			sqlstr.append(wss.getDataValue(tablename+"Z0","<=",maxtime));
		}
		if(this.fc_flag!=null&&fc_flag.length()!=0){
			sqlstr.append(" and " +this.fc_flag+"=2");
		}
		try {
			RowSet rs = dao.search(sqlstr.toString());
			if(rs.next()){
				spflag = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return spflag;
	}
	public void getParent(HashMap parent){
		 Iterator it= parent.keySet().iterator();
		
		 while (it.hasNext()){
			 String key=(String)it.next();
			 this.findparent(parent, key);
		 }
	}
	public void findparent(HashMap parent,String unitcode){
		StringBuffer sql=new StringBuffer();
		 sql.append("select parentid from organization where codeitemid='");
		 sql.append(unitcode);
		 sql.append("' and codeitemid<>parentid");
		 ContentDAO dao=new ContentDAO(this.conn);
		 RowSet rs=null;
		 try {
			rs=dao.search(sql.toString());
			if(rs.next()){
				String codeitemid=rs.getString(1);
				if(this.unitcode.get(codeitemid)!=null){
					
				}else{
					this.unitcode.put(codeitemid, "1");
				}
				findparent(this.unitcode,codeitemid);
			}else{
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getFc_flag() {
		return fc_flag;
	}
	public void setFc_flag(String fc_flag) {
		this.fc_flag = fc_flag;
	}
}
