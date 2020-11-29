package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hjsj.hrms.businessobject.general.inform.BatchBo;
import com.hjsj.hrms.businessobject.performance.workdiary.WorkdiarySQLStr;
import com.hjsj.hrms.businessobject.pos.posparameter.PosparameXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.TimeScope;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataCondBo {
	private String year=""; /**年*/
	private String month=""; /**月*/
	private UserView userView=null;
	private Connection conn=null;
	private String dbpre=""; /**人员库(可以有多个,以","隔开)*/
	private String fieldsetid=""; 
	private String changeflag=""; 
	private String ctrlType = "";
	private String bzSetid = ""; //编制子集
	ContentDAO dao = null;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private String backdate = sdf.format(new java.util.Date());
	public DataCondBo(UserView userView,Connection conn,String fieldsetid,
			String dbpre,String year,String month,String changeflag){
		this.userView=userView;
		this.conn=conn;
		this.fieldsetid=fieldsetid;
		this.dbpre=dbpre;
		this.year=year;
		this.month=month;
		this.changeflag=changeflag;
		this.dao = new ContentDAO(this.conn);
		PosparameXML pos = new PosparameXML(this.conn);
		ctrlType = pos.getValue(PosparameXML.AMOUNTS,"ctrl_type");
		ctrlType = ctrlType!=null &&ctrlType.length()>0?ctrlType:"0";
		bzSetid = pos.getValue(PosparameXML.AMOUNTS,"setid");
	}
	public DataCondBo(UserView userView,Connection conn){
		this.userView=userView;
		this.conn=conn;
		this.dao = new ContentDAO(this.conn);
	}
	/**
	 * 如果没有记录测新增一条记录
	 *
	 */
	private void resetDate(String flag){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append(" select codeitemid from organization where codeitemid not in(select ");
		sqlstr.append(getItemid());
		sqlstr.append(" from ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" where ");
		sqlstr.append(getequalz0time(fieldsetid+"Z0"));
		sqlstr.append(" and I9999=(select max(I9999) from ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" c where ");
		sqlstr.append(getequalz0time("c."+fieldsetid+"Z0"));
		sqlstr.append(" and c.");
		sqlstr.append(getItemid());
		sqlstr.append("=");
		sqlstr.append(fieldsetid);
		sqlstr.append(".");
		sqlstr.append(getItemid());
		sqlstr.append(")) and codesetid in(");
		sqlstr.append(getCodesetid());
		if("K".equalsIgnoreCase(fieldsetid.substring(0,1))) {
            sqlstr.append(") and codeitemid in(select E01A1 from K01)");
        } else {
            sqlstr.append(") and codeitemid in(select B0110 from B01)");
        }
		
		String[] dbArr = dbpre.split(",");
		if(dbArr!=null&&dbArr.length>0) {
            sqlstr.append(" and codeitemid in(");
        }
		StringBuffer unionstr = new StringBuffer();
		for(int i=0;i<dbArr.length;i++){
			if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
				if(unionstr.length()>0) {
                    unionstr.append(" union ");
                }
				if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
					unionstr.append(" select E01A1 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select E01A1  from K01");
				}else{
					unionstr.append(" select B0110 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select E0122 as B0110 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select B0110  from B01");
				}
			}
		}
		if(dbArr!=null&&dbArr.length>0){
			sqlstr.append(unionstr.toString());
			sqlstr.append(")");
		}
		sqlstr.append(this.doInitOrgUnit("codeitemid",flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new java.util.Date());
		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		RowSet rs = null;
		RowSet rsi = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("insert into ");
			buf.append(fieldsetid);
			buf.append(" (");
			buf.append(getItemid());
			buf.append(",I9999,");
			buf.append(fieldsetid);
			buf.append("Z0,");
			buf.append(fieldsetid);
			buf.append("Z1,CREATEUSERNAME,CREATETIME,MODUSERNAME,MODTIME,ID) values (?,?,?,?,?,?,?,?,?)");

			int maxi9999=0;
			rsi=dao.search("select max(i9999) maxi9999 from "+fieldsetid);
			while(rsi.next()){
				maxi9999=rsi.getInt("maxi9999");
				maxi9999++;
			}
			
			rs = dao.search(sqlstr.toString());
			ArrayList listvalue = new ArrayList();
			while(rs.next()){
				String B0110 = rs.getString("codeitemid");
				if(B0110!=null&&B0110.trim().length()>0){
					ArrayList list = new ArrayList();
					list.add(B0110);
					list.add(""+maxi9999);
					list.add(z0Time());
					list.add("1");
					list.add(this.userView.getUserName());
					list.add(strDate());
					list.add(this.userView.getUserName());
					list.add(strDate());
					list.add(getId());
					listvalue.add(list);
				}
			}
			dao.batchInsert(buf.toString(),listvalue);
			
			updateI9999();//将I9999等于0的置最大值
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
				if(rsi!=null) {
                    rsi.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void resetDate0(String flag){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append(" select codeitemid from organization where codeitemid not in(select ");
		sqlstr.append(getItemid());
		sqlstr.append(" from ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" where ");
		sqlstr.append("I9999=(select max(I9999) from ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" c where ");
		sqlstr.append(" c.");
		sqlstr.append(getItemid());
		sqlstr.append("=");
		sqlstr.append(fieldsetid);
		sqlstr.append(".");
		sqlstr.append(getItemid());
		sqlstr.append(")) and codesetid in(");
		sqlstr.append(getCodesetid());
		if("K".equalsIgnoreCase(fieldsetid.substring(0,1))) {
            sqlstr.append(") and codeitemid in(select E01A1 from K01)");
        } else {
            sqlstr.append(") and codeitemid in(select B0110 from B01)");
        }
		
		String[] dbArr = dbpre.split(",");
		if(dbArr!=null&&dbArr.length>0) {
            sqlstr.append(" and codeitemid in(");
        }
		StringBuffer unionstr = new StringBuffer();
		for(int i=0;i<dbArr.length;i++){
			if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
				if(unionstr.length()>0) {
                    unionstr.append(" union ");
                }
				if("K".equalsIgnoreCase(fieldsetid.substring(0,1))){
					unionstr.append(" select E01A1 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select E01A1  from K01");
				}else{
					unionstr.append(" select B0110 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select E0122 as B0110 from ");
					unionstr.append(dbArr[i]);
					unionstr.append("A01");
					unionstr.append(" union select B0110  from B01");
				}
			}
		}
		if(dbArr!=null&&dbArr.length>0){
			sqlstr.append(unionstr.toString());
			sqlstr.append(")");
		}
		sqlstr.append(this.doInitOrgUnit("codeitemid",flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new java.util.Date());
		sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		RowSet rs = null;
		RowSet rsi = null;
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("insert into ");
			buf.append(fieldsetid);
			buf.append(" (");
			buf.append(getItemid());
			buf.append(",I9999,");
			buf.append("CREATEUSERNAME,CREATETIME,MODUSERNAME,MODTIME) values (?,?,?,?,?,?)");
			int maxi9999=0;
			rsi=dao.search("select max(i9999) maxi9999 from "+fieldsetid);
			while(rsi.next()){
				maxi9999=rsi.getInt("maxi9999");
				maxi9999++;
			}
			rs = dao.search(sqlstr.toString());
			ArrayList listvalue = new ArrayList();
			while(rs.next()){
				String B0110 = rs.getString("codeitemid");
				if(B0110!=null&&B0110.trim().length()>0){
					ArrayList list = new ArrayList();
					list.add(B0110);
					list.add(""+maxi9999);
					list.add(this.userView.getUserName());
					list.add(strDate());
					list.add(this.userView.getUserName());
					list.add(strDate());
					listvalue.add(list);
				}
			}
			dao.batchInsert(buf.toString(),listvalue);
			
			updateI9999();//将I9999等于0的置最大值
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
				if(rsi!=null) {
                    rsi.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 根据项目设置生成数据
	 *
	 */
	public void runCond(String flag,String a_code){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select fieldsetid,itemid,Expression from fielditem where (fieldsetid='");
		sqlstr.append(fieldsetid);
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0, 1))) {
            sqlstr.append("' or fieldsetid like 'K%') and useflag='1' and Expression is not null order by displayid");
        } else {
            sqlstr.append("') and useflag='1' and Expression is not null order by displayid");
        }
		ArrayList list1= new ArrayList();//计算项目
		ArrayList list2= new ArrayList();//统计项目
		ArrayList list3= new ArrayList();//汇总项目
		ArrayList list4= new ArrayList();//统计汇总项目
		ArrayList list5= new ArrayList();//获取统计汇总项目中关联的子集和指标
		RowSet rowset = null;
		try {
			rowset = dao.search(sqlstr.toString());
			while(rowset.next()){
				String setid = rowset.getString("fieldsetid");
				String itemid = rowset.getString("itemid");
				if(this.userView!=null&&!"2".equals(this.userView.analyseFieldPriv(itemid))){
					continue;
				}
				String Expression = rowset.getString("Expression");
				if(Expression!=null&&Expression.trim().length()>1){
					String itemarr[] = {itemid,Expression};
					String[] exprArr =  exprDecom(Expression);
					if(exprArr!=null&&exprArr.length>0){
						if(exprArr[0]!=null&& "1".equals(exprArr[0])&&setid.equalsIgnoreCase(fieldsetid)){
							list1.add(itemarr);
						}else if(exprArr[0]!=null&& "2".equals(exprArr[0])&&setid.equalsIgnoreCase(fieldsetid)){
							list2.add(itemarr);
						}else if(exprArr[0]!=null&& "3".equals(exprArr[0])){
							if(setid.equalsIgnoreCase(fieldsetid)) {
                                list3.add(itemarr);
                            }
							if(exprArr.length==2&&exprArr[1]!=null&&exprArr[1].trim().length()>5){
								list5.add(getDep(exprArr[1],setid,itemid));
							}
						}else if(exprArr[0]!=null&& "4".equals(exprArr[0])){
							if(setid.equalsIgnoreCase(fieldsetid)) {
                                list4.add(itemarr);
                            }
							if(exprArr.length==5&&exprArr[4]!=null&&exprArr[4].trim().length()>5){
								list5.add(getDep(exprArr[4],setid,itemid));
							}
						}
					}
				}
			}
			
			
			
			if(!"0".equals(this.changeflag)){
				resetDate(flag);// 如果没有记录测新增一条记录
				setId();//设置年月变化字段
			}else{
				resetDate0(flag);
			}

			
			if("0".equals(flag)){//编制管理改为只统计汇总好当前选中节点下的数据，考虑有父节点的时候父节点数据的正确采用父节点数据-旧的当前节点数据+新的当前节点数据
				
				ArrayList templist = new ArrayList();
				if("K".equalsIgnoreCase(fieldsetid.substring(0, 1))){
					String pos[] = null;
					for(int j=0;j<list5.size();j++){
						String posArr[] = (String[])list5.get(j);
						if(posArr!=null&&posArr.length==5){
							pos = posArr;
						}
						if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
							templist.add(pos[2]);
						}
					}
				}
				if(!"K".equalsIgnoreCase(fieldsetid.substring(0, 1))){
					for(int i=0;i<list3.size();i++){
						String[] arr3 = (String[])list3.get(i);
						if(arr3!=null&&arr3.length==2){
							String pos[] = null;
							boolean f=true;
							for(int j=0;j<list5.size();j++){
								String posArr[] = (String[])list5.get(j);
								if(posArr!=null&&posArr.length==5){
									pos = posArr;
								}
								if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
									if(pos[2].equalsIgnoreCase(arr3[0])){
										templist.add(arr3[0]);
										f=false;
										break;
									}
								}
							}
							if(f){//如果执行了汇总Bxx、Kxx一起的就不在汇总Bxx的了
								templist.add(arr3[0]);
							}
						}
					}
				}
				this.doCount(a_code, "-",templist);
			}
			/**统计汇总中实现统计*/
			for(int i=0;i<list4.size();i++){
				String[] arr4 = (String[])list4.get(i);
				if(arr4!=null&&arr4.length==2){
					statPro(arr4[0],arr4[1],flag,a_code);
				}
			}
			/**统计项目*/
			for(int i=0;i<list2.size();i++){
				String[] arr2 = (String[])list2.get(i);
				if(arr2!=null&&arr2.length==2){
					statPro(arr2[0],arr2[1],flag,a_code);
				}
			}
//			/**统计汇总中实现汇总（在单位计算的同时，先统计职位的数据）*/
//			for(int i=0;i<list4.size();i++){
//				String[] arr4 = (String[])list4.get(i);
//				if(arr4!=null&&arr4.length==2){
//					summaryPro(arr4[0],null);
//				}
//			}
			
			ArrayList summrayList1 = new ArrayList();//当前所有的汇总公式 用于一次拼成sql语句汇总
			ArrayList summrayList2 = new ArrayList();
			
			ArrayList summrayList = new ArrayList();
			if(!"K".equalsIgnoreCase(fieldsetid.substring(0, 1))){
				/**汇总项目*/
				for(int i=0;i<list3.size();i++){
					String[] arr3 = (String[])list3.get(i);
					if(arr3!=null&&arr3.length==2){
						String pos[] = null;
						/*for(int j=0;j<list5.size();j++){
							String posArr[] = (String[])list5.get(j);
							if(posArr!=null&&posArr.length==5){
								if(posArr[2]!=null&&posArr[2].equalsIgnoreCase(arr3[0])){
									pos = posArr;
									break;
								}
							}
						}*/
						boolean f=true;
						for(int j=0;j<list5.size();j++){
							String posArr[] = (String[])list5.get(j);
							if(posArr!=null&&posArr.length==5){
								pos = posArr;
								//break;
							}
							if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
								//summaryPro(pos[2],pos,flag);
								if(pos[2].equalsIgnoreCase(arr3[0])){
									//summaryPro(arr3[0],null,flag);
									//@@summaryPro(pos[2],pos,flag);
									summrayList1.add(pos[2]);
									summrayList2.add(pos);
									f=false;
									break;
								}
							}
						}
						if(f){//如果执行了汇总Bxx、Kxx一起的就不在汇总Bxx的了
							//@@summaryPro(arr3[0],null,flag);
							summrayList.add(arr3[0]);
						}
						//summaryPro(arr3[0],null,flag);
					}
				}
			}
			if("K".equalsIgnoreCase(fieldsetid.substring(0, 1))){
				String pos[] = null;
				for(int j=0;j<list5.size();j++){
					String posArr[] = (String[])list5.get(j);
					if(posArr!=null&&posArr.length==5){
						pos = posArr;
						//break;
					}
					if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
						//@@summaryPro(pos[2],pos,flag);
					
						summrayList1.add(pos[2]);
						summrayList2.add(pos);
					}
				}
				/*if(pos!=null&&pos.length==5)
					summaryPro(pos[2],pos);*/
			}
			summaryPro(summrayList1,summrayList2,flag,a_code);
			summaryPro(summrayList,null,flag,a_code);
			/**计算项目*/
			for(int i=0;i<list1.size();i++){
				String[] arr1 = (String[])list1.get(i);
				if(arr1!=null&&arr1.length==2){
					calPro(arr1[0],arr1[1],flag);
				}
			}
			BatchBo batchBo = new BatchBo();
			dropTempTable(batchBo.getTempTable(userView));
			dropTempTable("t#"+this.userView.getUserName()+"_hr_org_1");
			dropTempTable("t#"+this.userView.getUserName()+"_hr_org_2");
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rowset!=null){
					rowset.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
				
		}
	}
	
	/**
	 * 根据项目设置生成数据
	 *
	 */
	public void runCond1(String pos_set,String flag,String a_code){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select fieldsetid,itemid,Expression from fielditem where (fieldsetid='");
		sqlstr.append(fieldsetid);
		if(!"K".equalsIgnoreCase(fieldsetid.substring(0, 1))&&!"".equals(pos_set)) {
            sqlstr.append("' or fieldsetid = '"+pos_set+"') and useflag='1' and Expression is not null order by displayid");
        } else {
            sqlstr.append("') and useflag='1' and Expression is not null order by displayid");
        }
		ArrayList list1= new ArrayList();//计算项目
		ArrayList list2= new ArrayList();//统计项目
		ArrayList list3= new ArrayList();//汇总项目
		ArrayList list4= new ArrayList();//统计汇总项目
		ArrayList list5= new ArrayList();//获取统计汇总项目中关联的子集和指标
		RowSet rowset=null;
		try {
			rowset = dao.search(sqlstr.toString());
			while(rowset.next()){
				String setid = rowset.getString("fieldsetid");
				String itemid = rowset.getString("itemid");
				if(this.userView!=null&&!"2".equals(this.userView.analyseFieldPriv(itemid))){
					continue;
				}
				String Expression = rowset.getString("Expression");
				if(Expression!=null&&Expression.trim().length()>1){
					String itemarr[] = {itemid,Expression};
					String[] exprArr =  exprDecom(Expression);
					if(exprArr!=null&&exprArr.length>0){
						if(exprArr[0]!=null&& "1".equals(exprArr[0])&&setid.equalsIgnoreCase(fieldsetid)){
							list1.add(itemarr);
						}else if(exprArr[0]!=null&& "2".equals(exprArr[0])&&setid.equalsIgnoreCase(fieldsetid)){
							list2.add(itemarr);
						}else if(exprArr[0]!=null&& "3".equals(exprArr[0])){
							if(setid.equalsIgnoreCase(fieldsetid)) {
                                list3.add(itemarr);
                            }
							if(exprArr.length==2&&exprArr[1]!=null&&exprArr[1].trim().length()>5){
								list5.add(getDep(exprArr[1],setid,itemid));
							}
						}else if(exprArr[0]!=null&& "4".equals(exprArr[0])){
							if(setid.equalsIgnoreCase(fieldsetid)) {
                                list4.add(itemarr);
                            }
							if(exprArr.length==5&&exprArr[4]!=null&&exprArr[4].trim().length()>5){
								list5.add(getDep(exprArr[4],setid,itemid));
							}
						}
					}
				}
			}
			
			
			
			if(!"0".equals(this.changeflag)){
				resetDate(flag);// 如果没有记录测新增一条记录
				setId();//设置年月变化字段
			}else{
				resetDate0(flag);
			}
			
			if("0".equals(flag)){//编制管理改为只统计汇总好当前选中节点下的数据，考虑有父节点的时候父节点数据的正确采用父节点数据-旧的当前节点数据+新的当前节点数据
				
				ArrayList templist = new ArrayList();
				for(int i=0;i<list3.size();i++){
					String[] arr3 = (String[])list3.get(i);
					if(arr3!=null&&arr3.length==2){
						String pos[] = null;
						boolean f=true;
						for(int j=0;j<list5.size();j++){
							String posArr[] = (String[])list5.get(j);
							if(posArr!=null&&posArr.length==5){
								pos = posArr;
							}
							if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
								if(pos[2].equalsIgnoreCase(arr3[0])){
									templist.add(arr3[0]);
									f=false;
									break;
								}
							}
						}
						if(f){//如果执行了汇总Bxx、Kxx一起的就不在汇总Bxx的了
							templist.add(arr3[0]);
						}
					}
				}
				this.doCount(a_code, "-",templist);
			}

			/**统计汇总中实现统计*/
			for(int i=0;i<list4.size();i++){
				String[] arr4 = (String[])list4.get(i);
				if(arr4!=null&&arr4.length==2){
					statPro(arr4[0],arr4[1],flag,a_code);
				}
			}
			/**统计项目*/
			for(int i=0;i<list2.size();i++){
				String[] arr2 = (String[])list2.get(i);
				if(arr2!=null&&arr2.length==2){
					statPro(arr2[0],arr2[1],flag,a_code);
				}
			}
//			/**统计汇总中实现汇总（在单位计算的同时，先统计职位的数据）*/
//			for(int i=0;i<list4.size();i++){
//				String[] arr4 = (String[])list4.get(i);
//				if(arr4!=null&&arr4.length==2){
//					summaryPro(arr4[0],null);
//				}
//			}
			
			
			ArrayList summrayList1 = new ArrayList();//当前所有的汇总公式 用于一次拼成sql语句汇总
			ArrayList summrayList2 = new ArrayList();
			
			ArrayList summrayList = new ArrayList();
			
			if(!"K".equalsIgnoreCase(fieldsetid.substring(0, 1))){
				/**汇总项目*/
				for(int i=0;i<list3.size();i++){
					String[] arr3 = (String[])list3.get(i);
					if(arr3!=null&&arr3.length==2){
						String pos[] = null;
						/*for(int j=0;j<list5.size();j++){
							String posArr[] = (String[])list5.get(j);
							if(posArr!=null&&posArr.length==5){
								if(posArr[2]!=null&&posArr[2].equalsIgnoreCase(arr3[0])){
									pos = posArr;
									break;
								}
							}
						}*/
						boolean f=true;
						for(int j=0;j<list5.size();j++){
							String posArr[] = (String[])list5.get(j);
							if(posArr!=null&&posArr.length==5){
								pos = posArr;
								//break;
							}
							if(pos!=null&&pos.length==5){//xuj但有多个指标要汇总
								//summaryPro(pos[2],pos,flag);
								if(pos[2].equalsIgnoreCase(arr3[0])){
									//summaryPro(arr3[0],null,flag);
									//@@summaryPro(pos[2],pos,flag);
									summrayList1.add(pos[2]);
									summrayList2.add(pos);
									f=false;
									break;
								}
							}
						}
						if(f){//如果执行了汇总Bxx、Kxx一起的就不在汇总Bxx的了
							//@@summaryPro(arr3[0],null,flag);
							summrayList.add(arr3[0]);
						}
						//summaryPro(arr3[0],null,flag);
					}
				}
			}
			/*if(fieldsetid.substring(0, 1).equalsIgnoreCase("K")){
				String pos[] = null;
				for(int j=0;j<list5.size();j++){
					String posArr[] = (String[])list5.get(j);
					if(posArr!=null&&posArr.length==5){
						pos = posArr;
						//break;
					}
					if(pos!=null&&pos.length==5)//xuj但有多个指标要汇总
						summaryPro(pos[2],pos,flag);
				}
				if(pos!=null&&pos.length==5)
					summaryPro(pos[2],pos);
			}*/
			
			summaryPro(summrayList1,summrayList2,flag,a_code);
			summaryPro(summrayList,null,flag,a_code);
			/**计算项目*/
			for(int i=0;i<list1.size();i++){
				String[] arr1 = (String[])list1.get(i);
				if(arr1!=null&&arr1.length==2){
					calPro(arr1[0],arr1[1],flag);
				}
			}
			BatchBo batchBo = new BatchBo();
			dropTempTable(batchBo.getTempTable(userView));
			dropTempTable("t#"+this.userView.getUserName()+"_hr_org_1");
			dropTempTable("t#"+this.userView.getUserName()+"_hr_org_2");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rowset!=null) {
                    rowset.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 统计项目
	 * @param field //修改的指标
	 * @param expr　//公式设置
	 */
	private void statPro(String field,String expr,String flag,String a_code){
		expr=expr!=null?expr:"";
		String exprArr[] = exprDecom(expr);
		if(exprArr!=null&&exprArr.length>3){
			String[] arr = exprArr[3].split("\\|");
			String lower = "1";
			if(exprArr.length==5){
				lower = exprArr[4];
			}
			String sexpr = "";
			String sfactor="";
			if(arr!=null&&arr.length==2){
				sexpr = arr[0];
				sfactor=arr[1];
			}
			if(exprArr[1]!=null&& "0".equals(exprArr[1])){ //求个数
//				if(exprArr[3]!=null&&exprArr[3].trim().length()>5)
					statUpdateNum(field,sexpr,sfactor,lower,flag,a_code);
			}else{
				String c_exArr[] = exprArr[2].split("\\|");
				if(c_exArr!=null&&c_exArr.length==2&&c_exArr[0]!=null&&c_exArr[0].trim().length()>0) {
                    statUpdateSum(field,c_exArr[0],exprArr[1],sexpr,sfactor,lower,flag,a_code);
                }
			}
		}
	}
	/**
	 * 计算项目
	 * @param field
	 * @param expr
	 */
	private void calPro(String field,String expr,String flag){
		expr=expr!=null?expr:"";
		String exprArr[] = exprDecom(expr);
		if(exprArr!=null&&exprArr.length==4){
			String c_exArr[] = exprArr[2].split("\\|");
			if(c_exArr!=null&&c_exArr.length==2) {
                countupdate(field,c_exArr[0],flag);
            }
		}
	}
	/**
	 * 汇总项目
	 * @param field
	 * @param 职位子集指标关联的单位子集和指标
	 */
    private void summaryPro(ArrayList fields, ArrayList summaryList, String flag, String a_code) {

        if (fields.size() > 0) {
            FieldItem fielditem = DataDictionary.getFieldItem((String) fields.get(0));
            String tablename = fielditem.getFieldsetid();
            String itemid = getItemid(tablename);

            // 单位编制汇总时如果不控制部门编制，汇总时则不考虑部门数据
            int unitFlag = 1;
            if (summaryList == null && "0".equals(ctrlType) && tablename.equalsIgnoreCase(bzSetid)) {
                unitFlag = 0;
            }

            int level = maxLevel(unitFlag);
            int currentlevel = 1;
            if ("0".equals(flag)) {
                currentlevel = currentLevel(a_code);
                PosparameXML pos1 = new PosparameXML(this.conn);
                // 1：是，0：否 sunjian
                String nextlevel = pos1.getValue(PosparameXML.AMOUNTS, "nextlevel");
                // 是否只控制到下一级机构，如果不是控制到下一级机构，则从最底层机构进行控制，如果是，则只对下级机构控制，仅根据下级机构算出值
                if ("1".equalsIgnoreCase(nextlevel)) {
                    level = currentlevel + 1;
                }
                
            }
            
            if (summaryList != null) {
                level += 1;
            }
            
            for (int i = level - 1; i >= currentlevel; i--) {
                StringBuffer collsqlsb = new StringBuffer();
                collsqlsb.append(" update " + tablename + " set ");
                for (int v = 0; v < fields.size(); v++) {
                    String field = (String) fields.get(v);
                    String pos[] = null;
                    if (summaryList != null) {
                        pos = (String[]) summaryList.get(v);
                    }
                    
                    fielditem = DataDictionary.getFieldItem((String) fields.get(v));
                    if (fielditem != null) {
                        collsqlsb.append(field + "=");
                        collsqlsb.append("0+(select " + Sql_switcher.isnull("sum(" + field + ")", "0"));
                        collsqlsb.append(" from " + tablename + " bo,organization org ");
                        collsqlsb.append(" where ");
                        collsqlsb.append(Sql_switcher.left("bo." + itemid + "", Sql_switcher.length(tablename + "." + itemid)));
                        collsqlsb.append("=" + tablename + "." + itemid);
                        collsqlsb.append(" and bo." + itemid + "<>" + tablename + "." + itemid);
                        if (!"0".equals(this.changeflag)) {
                            collsqlsb.append(" and " + getId("bo.Id"));
                        }
                        // 编制管理之汇总最后一条
                        if ("0".equals(flag)) {
                            collsqlsb.append(" and I9999=(select max(I9999) from ");
                            collsqlsb.append(tablename + " c where c." + itemid + "=bo." + itemid);
                            if (!"0".equals(this.changeflag)) {
                                collsqlsb.append(" and " + getId("c.Id"));
                            }
                            
                            collsqlsb.append(")");
                        }
                        
                        collsqlsb.append(" and org.grade='" + (i + 1) + "'");
                        if ("0".equals(flag)) {
                            collsqlsb.append(" and org.codeitemid like '" + a_code.substring(2) + "%'");
                        }
                        
                        collsqlsb.append(" and org.codeitemid=bo." + itemid + " and org.codesetid in(");
                        collsqlsb.append(getCodesetid(tablename, unitFlag));
                        collsqlsb.append(")" + " and " + Sql_switcher.dateValue(backdate));
                        collsqlsb.append(" between org.start_date and org.end_date ");
                        collsqlsb.append(this.doInitOrgUnit("org.codeitemid", flag) + " )");
                        if (pos != null && pos.length == 5) {
                            collsqlsb.append("+(select " + Sql_switcher.isnull("sum(" + pos[4] + ")", "0"));
                            collsqlsb.append(" from " + pos[3]);
                            collsqlsb.append(" where E01A1 in(select codeitemid from organization where codesetid='@K'");
                            collsqlsb.append("and parentid=");
                            collsqlsb.append(tablename + "." + itemid);
                            collsqlsb.append(" and " + Sql_switcher.dateValue(backdate));
                            collsqlsb.append(" between start_date and end_date");
                            
                            if ("0".equals(flag)) {
                                collsqlsb.append(" and codeitemid like '" + a_code.substring(2) + "%'");
                            }
                            
                            collsqlsb.append(")");
                            if (!"0".equals(this.changeflag)) {
                                collsqlsb.append(" and " + getId("Id"));
                            }
                            // 编制管理之汇总最后一条
                            if ("0".equals(flag)) {
                                collsqlsb.append(" and I9999=(select max(I9999) from ");
                                collsqlsb.append(pos[3] + " pos where pos.E01A1=" + pos[3] + ".E01A1 ");
                                if (!"0".equals(this.changeflag)) {
                                    collsqlsb.append(" and " + getId("Id"));
                                }
                                
                                collsqlsb.append(")");
                            }
                            
                            collsqlsb.append(")");
                        }
                        
                        collsqlsb.append(",");
                    }
                }
                
                collsqlsb.setLength(collsqlsb.length() - 1);
                collsqlsb.append(" where ");
                if (!"0".equals(this.changeflag)) {
                    collsqlsb.append(getId("Id") + " and ");
                }
                
                collsqlsb.append("I9999=(select max(I9999) from ");
                collsqlsb.append(tablename + " c where c." + itemid + "=" + tablename + "." + itemid);
                if (!"0".equals(this.changeflag)) {
                    collsqlsb.append(" and " + getId("c.Id"));
                }
                
                collsqlsb.append(")");
                collsqlsb.append(" and " + itemid + " in(");
                collsqlsb.append("select codeitemid from organization where grade='" + i);
                collsqlsb.append("' and codesetid in(" + getCodesetid(tablename, unitFlag) + ")");
                collsqlsb.append(" and ");
                collsqlsb.append(Sql_switcher.dateValue(backdate) + " between start_date and end_date ");
                if ("0".equals(flag)) {
                    collsqlsb.append(" and codeitemid like '" + a_code.substring(2) + "%'");
                }
                
                collsqlsb.append(")");
                collsqlsb.append(this.doInitOrgUnit(tablename + ".b0110", flag));
                if (summaryList == null) {
                    collsqlsb.append(" and (select count(bo." + itemid + ")");
                    collsqlsb.append(" from " + tablename + " bo,organization org ");
                    collsqlsb.append(" where ");
                    collsqlsb.append(Sql_switcher.left("bo." + itemid + "", Sql_switcher.length(tablename + "." + itemid)));
                    collsqlsb.append("=" + tablename + "." + itemid);
                    collsqlsb.append(" and bo." + itemid + "<>" + tablename + "." + itemid);
                    collsqlsb.append(" and I9999=(select max(I9999) from ");
                    collsqlsb.append(tablename + " c where c." + itemid + "=bo." + itemid);
                    if (!"0".equals(this.changeflag)) {
                        collsqlsb.append(" and " + getId("c.Id") + ")");
                        collsqlsb.append(" and " + getId("bo.Id"));
                    }
                    
                    if ("0".equals(this.changeflag)) {
                        collsqlsb.append(")");
                    }
                    
                    collsqlsb.append(" and org.grade='" + (i + 1) + "' " + " and " + Sql_switcher.dateValue(backdate)
                            + " between org.start_date and org.end_date");
                    if ("0".equals(flag)) {
                        collsqlsb.append(" and org.codeitemid like '" + a_code.substring(2) + "%'");
                    }

                    if (unitFlag < 1 && "B".equalsIgnoreCase(fieldsetid.substring(0, 1))) {
                        collsqlsb.append(" and org.codesetid='UN' and org.codeitemid=bo.B0110 ");
                    }
                    
                    collsqlsb.append(" )>0");
                }

                try {
                    dao.update(collsqlsb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if ("0".equals(flag)) {
                this.doCount(a_code, "+", fields);
            }
            
        }
    }
	public void posSummaryDep(String depid){
		
	}
	 /**
     * 获取部门等级
     * @param dao
     * @return list
     * @throws GeneralException
     */
	private int maxLevel(int unitFlag){
		int level=0;
		String sql="select max(grade) as grade from organization where "; 
		   if("B".equalsIgnoreCase(fieldsetid.substring(0,1)) && unitFlag<1) {
               sql += " codesetid in ('UN')";
           } else {
               sql += " codesetid in ('UN','UM') ";
           }
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				level = rs.getInt("grade");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return level;
	}
	/**
	 * 得到时间条件
	 */
	private String getequalz0time(String itemid){
		TimeScope ts = new TimeScope();
		String time = "";
		if("2".equals(changeflag)){
			time = year;
		}else{
			int inputmonth = Integer.parseInt(month);
			if(inputmonth>0 && inputmonth<10) {
                time = year+"-0"+month;
            } else {
                time = year+"-"+month;
            }
		}
		return ts.getTimeCond(itemid,"=",time);
	}
	private String getequalz0time(String itemid,String changflag){
		TimeScope ts = new TimeScope();
		String time = "";
		if("2".equals(changflag)){
			time = year;
		}else{
			int inputmonth = Integer.parseInt(month);
			if(inputmonth>0 && inputmonth<10) {
                time = year+"-0"+month;
            } else {
                time = year+"-"+month;
            }
		}
		return ts.getTimeCond(itemid,"=",time);
	}
	/**
	 * 得到时间条件
	 */
	private String getId(String itemid){
		String time = "";
		if("2".equals(changeflag)){
			time = year;
		}else{
			int inputmonth = Integer.parseInt(month);
			if(inputmonth>0 && inputmonth<10) {
                time = year+".0"+month;
            } else {
                time = year+"."+month;
            }
		}
		time = itemid+"='"+time+"'";
		return time;
	}
	/**
	 * 计算项目，数据更新
	 * @param field
	 * @param expr
	 * @return
	 */
	private void countupdate(String field,String expr,String flag){
		try {
			/*FieldItem fielditem = DataDictionary.getFieldItem(field);
			String mainitemid="";
			if(fielditem.getFieldsetid().substring(0,1).equalsIgnoreCase("K"))
				mainitemid="E01A1";
			else
				mainitemid="B0110";
			
			String FSQL = "";
			if(fielditem.getItemtype().equalsIgnoreCase("N"))
				 FSQL = this.getFQL(field, expr);
			else
				FSQL = this.getPosFQL(field, expr);
			BatchBo batchBo = new BatchBo();
			StringBuffer countsqlsb = new StringBuffer();
			countsqlsb.append("update "+fieldsetid+ " set ");
			if(FSQL!=null&&FSQL.trim().length()>0){
				countsqlsb.append(field+"=");
				if(fielditem.getItemtype().equalsIgnoreCase("N")){
//					if(FSQL.indexOf("SELECT_")!=-1){
						countsqlsb.append("(select ");
						countsqlsb.append(FSQL);
						countsqlsb.append(" from ");
						countsqlsb.append(batchBo.getTempTable(userView));
						countsqlsb.append(" where "+mainitemid+"="+fieldsetid+"."+mainitemid+" and i9999=(select max(i9999) from "+fieldsetid+" a where a."+this.getItemid()+"="+this.getItemid()+")),");
//					}else
//						countsqlsb.append(FSQL+",");
				}else{
					countsqlsb.append("(select ");
					countsqlsb.append(FSQL);
					countsqlsb.append(" from ");
					countsqlsb.append(batchBo.getTempTable(userView));
					countsqlsb.append(" where "+mainitemid+"="+fieldsetid+"."+mainitemid+" and i9999=(select max(i9999) from "+fieldsetid+" a where a."+this.getItemid()+"="+this.getItemid()+")),");
				}
			}
			countsqlsb.append("ModTime = "+Sql_switcher.sqlNow()+",ModUserName=");
			countsqlsb.append(" '"+this.userView.getUserName()+"' ");
			if(!"0".equals(changeflag)){
				countsqlsb.append(" where "+getequalz0time(fieldsetid+"Z0"));
				countsqlsb.append(" and Id ='"+getId()+"' and "+this.fieldsetid+".i9999=(select max(i9999) from "+this.fieldsetid+" b where b."+this.getItemid()+"="+this.fieldsetid+"."+this.getItemid()+") ");
			}
			String priv = "";
			if(!"0".equals(changeflag)){
				priv = this.userView.getManagePrivCodeValue();
				countsqlsb.append(" and "+getItemid()+" like '"+priv+"%' ");
				countsqlsb.append(this.doInitOrgUnit(this.getItemid()));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			if("0".equals(changeflag)){
				priv = this.userView.getManagePrivCodeValue();
				countsqlsb.append(" where "+getItemid()+" like '"+priv+"%' ");
				countsqlsb.append(" where "+this.doInitOrgUnit(this.getItemid()).substring(4));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			dao.update(countsqlsb.toString());
			*/
			FieldItem fielditem = DataDictionary.getFieldItem(field);
			String type=fielditem.getItemtype();
			String mainitemid="";
			int forvalue=YksjParser.forUnit;
			if("K".equalsIgnoreCase(fielditem.getFieldsetid().substring(0,1))){
				mainitemid="E01A1";
				forvalue=YksjParser.forPosition;
			}else{
				mainitemid="B0110";
			}
			String FSQL = "";
			/*if(fielditem.getItemtype().equalsIgnoreCase("N"))
				 FSQL = this.getFQL(field, expr);
			else
				FSQL = this.getPosFQL(field, expr);*/
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
					Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			YksjParser yp = new YksjParser(this.userView,alUsedFields,
					YksjParser.forSearch,getDataType(type),forvalue ,"Ht","");
			yp.setCon(conn);
			yp.setStdTmpTable(fieldsetid);
			//一般变化子集不用查XXXz0、XXXz1 2013-12-07 gdd
			if(!"0".equals(changeflag)) {
                yp.setYmc(getYMC());
            }
			yp.setYearMonthCount(true);
			StringBuffer countsqlsb_str=new StringBuffer("");
			if(!"0".equals(changeflag)){
				countsqlsb_str.append(" where "+getequalz0time(fieldsetid+"Z0"));
				if("0".equals(flag)){
					countsqlsb_str.append(" and Id ='"+getId()+"' and "+this.fieldsetid+".i9999=(select max(i9999) from "+this.fieldsetid+" b where b."+this.getItemid()+"="+this.fieldsetid+"."+this.getItemid()+") ");
				}
			}
			
			if(!"0".equals(changeflag)){ 
				countsqlsb_str.append(this.doInitOrgUnit(this.getItemid(),flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			if("0".equals(changeflag)){ 
				countsqlsb_str.append(" where "+this.doInitOrgUnit(this.getItemid(),flag).substring(4));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			if(countsqlsb_str.length()>0) {
                yp.setStdTmpTable_where(countsqlsb_str.toString());
            }
			
			
			
			
			yp.run(expr,fieldsetid);
			String tablename = yp.getTempTableName();
			FSQL = yp.getSQL();
			//BatchBo batchBo = new BatchBo();
			StringBuffer countsqlsb = new StringBuffer();
			countsqlsb.append("update "+fieldsetid+ " set ");
			if(FSQL!=null&&FSQL.trim().length()>0){
				countsqlsb.append(field+"=");
				if("N".equalsIgnoreCase(fielditem.getItemtype())){
//					if(FSQL.indexOf("SELECT_")!=-1){
						countsqlsb.append("(select ");
						countsqlsb.append(FSQL);
						countsqlsb.append(" from ");
						countsqlsb.append(tablename);
						countsqlsb.append(" where "+mainitemid+"="+fieldsetid+"."+mainitemid+"),");
//					}else
//						countsqlsb.append(FSQL+",");
				}else{
					countsqlsb.append("(select ");
					countsqlsb.append(FSQL);
					countsqlsb.append(" from ");
					countsqlsb.append(tablename);
					countsqlsb.append(" where "+mainitemid+"="+fieldsetid+"."+mainitemid+"),");
				}
				
			}
			countsqlsb.append("ModTime = "+Sql_switcher.sqlNow()+",ModUserName=");
			countsqlsb.append(" '"+this.userView.getUserName()+"' ");
			if(!"0".equals(changeflag)){
				countsqlsb.append(" where "+getequalz0time(fieldsetid+"Z0"));
				if("0".equals(flag)){
					countsqlsb.append(" and Id ='"+getId()+"' and "+this.fieldsetid+".i9999=(select max(i9999) from "+this.fieldsetid+" b where b."+this.getItemid()+"="+this.fieldsetid+"."+this.getItemid()+") ");
				}
			}
			if(!"0".equals(changeflag)){
				//priv = this.userView.getManagePrivCodeValue();
				//countsqlsb.append(" and "+getItemid()+" like '"+priv+"%' ");
				countsqlsb.append(this.doInitOrgUnit(this.getItemid(),flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			if("0".equals(changeflag)){
				//priv = this.userView.getManagePrivCodeValue();
				//countsqlsb.append(" where "+getItemid()+" like '"+priv+"%' ");
				countsqlsb.append(" where "+this.doInitOrgUnit(this.getItemid(),flag).substring(4));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			}
			
			//添加脏数据（b0110、e01a1为空）过滤.....
			if(fieldsetid.indexOf("K")==0) {
                countsqlsb.append(" and E01a1 is not null and "+Sql_switcher.length("E01a1")+">0 ");
            } else if(fieldsetid.indexOf("B")==0) {
                countsqlsb.append(" and B0110 is not null and "+Sql_switcher.length("B0110")+">0 ");
            }
			
			
			
			dao.update(countsqlsb.toString());
			if("D".equalsIgnoreCase(type)){
				dataIsNUll(dao,(String)this.fieldsetid,field);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 统计项目求个数
	 * @param field //指标
	 * @param sexpr //公式
	 * @param sfactor //因子表达式
	 * @param lower //包含下级
	 * @return
	 */
	private void statUpdateNum(String field,String sexpr,String sfactor,String lower,String flag,String a_code){
		try {
			statTempNum(sexpr,sfactor,flag,a_code);//填充临时表
			StringBuffer sqlstr = new StringBuffer();
			String temptable = "t#"+this.userView.getUserName()+"_hr_org_2";
			
			sqlstr.append("update ");
			sqlstr.append(fieldsetid);
			sqlstr.append(" set ");
			sqlstr.append(field);
			sqlstr.append("=(select count(A0100) from ");
			sqlstr.append(temptable);
			sqlstr.append(" where ");
			sqlstr.append(fieldsetid);
			sqlstr.append("."+getItemid()+"=");
			sqlstr.append(temptable);
			sqlstr.append("."+getItemid());
			if(lower!=null&& "1".equals(lower)){
				sqlstr.append(" or ");
				if("B0110".equalsIgnoreCase(getItemid())){
					if(Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2){
						sqlstr.append(" rpad(");
						sqlstr.append(temptable);
						sqlstr.append(".B0110,length(");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid()+"))=");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid());
						sqlstr.append(" or ");
						sqlstr.append(" rpad(");
						sqlstr.append(temptable);
						sqlstr.append(".E0122,length(");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid()+"))=");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid());
					}else{
						sqlstr.append(" left(");
						sqlstr.append(temptable);
						sqlstr.append(".B0110,len(");
						sqlstr.append(fieldsetid);
						sqlstr.append(".B0110))=");
						sqlstr.append(fieldsetid);
						sqlstr.append(".B0110");
						sqlstr.append(" or ");
						sqlstr.append(" left(");
						sqlstr.append(temptable);
						sqlstr.append(".E0122,len(");
						sqlstr.append(fieldsetid);
						sqlstr.append(".B0110))=");
						sqlstr.append(fieldsetid);
						sqlstr.append(".B0110");
					}
				}else{
					if(Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2){
						sqlstr.append(" rpad(");
						sqlstr.append(temptable);
						sqlstr.append("."+getItemid()+",length(");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid()+"))=");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid());
					}else{
						sqlstr.append(" left(");
						sqlstr.append(temptable);
						sqlstr.append("."+getItemid()+",len(");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid()+"))=");
						sqlstr.append(fieldsetid);
						sqlstr.append("."+getItemid());
					}
				}
			}else{
				if("B0110".equalsIgnoreCase(getItemid())){
					sqlstr.append(" or ");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid()+"=");
					sqlstr.append(temptable);
					sqlstr.append(".E0122");
				}
			}
			if(!"0".equals(changeflag)){
				sqlstr.append(") where Id='");
				sqlstr.append(getId());
				sqlstr.append("'");
				if("0".equals(flag)){
					sqlstr.append(" and I9999=(select max(I9999) from ");
					sqlstr.append(fieldsetid);
					sqlstr.append(" a where a."+getItemid()+"=");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid()+" and Id='");
					sqlstr.append(getId());
					sqlstr.append("')");
				}
			}else{
				sqlstr.append(") where ");
				sqlstr.append("I9999=(select max(I9999) from ");
				sqlstr.append(fieldsetid);
				sqlstr.append(" a where a."+getItemid()+"=");
				sqlstr.append(fieldsetid);
				sqlstr.append("."+getItemid());
				sqlstr.append(")");
			}
			/*if(!this.userView.isSuper_admin())
				sqlstr.append(" and "+getItemid()+" like '"+this.userView.getManagePrivCodeValue()+"%'");
			*/
			sqlstr.append(this.doInitOrgUnit(this.getItemid(),flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			//if("0".equals(flag))
				//sqlstr.append(" and "+this.getItemid()+ " like '"+a_code.substring(2)+"%'");
			dao.update(sqlstr.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 统计项目求个数填充临时表
	 * @param sexpr
	 * @param sfactor
	 */
	private void statTempNum(String sexpr,String sfactor,String flag,String a_code){
		try {
			/** 创建临时表 */
			DbWizard dbWizard = new DbWizard(this.conn);
			String temp_name="t#"+this.userView.getUserName()+"_hr_org_2";
			if (!dbWizard.isExistTable(temp_name, false)){  //201077 xuj 优化如果临时表已存在则只删除其中所有数据不在重新创建 此处临时表结构一样
				createMusterTempTable(temp_name,getFieldList(),dbWizard);
			}else{
				dao.update("delete from "+temp_name);
			}
			String[] dbArr = dbpre.split(",");
			StringBuffer sqlstr = new StringBuffer();
			StringBuffer insertstr = new StringBuffer();
			insertstr.append("insert into "+temp_name+"(NBASE,A0100,B0110,E0122,E01A1)");
			for(int i=0;i<dbArr.length;i++){
				if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
					String whl="";
					if(sfactor!=null&&sfactor.trim().length()>0&&sexpr!=null&&sexpr.trim().length()>0){
						if(!userView.isSuper_admin()){
							whl=userView.getPrivSQLExpression(sfactor+"|"+sexpr,dbArr[i],false,false,true,new ArrayList()) ;
						}else{
							FactorList factorslist=new FactorList(sfactor,sexpr,dbArr[i],false,false,true,1,userView.getUserId());
							whl=factorslist.getSqlExpression();
						}
					}
					if(sqlstr!=null&&sqlstr.length()>1){
						sqlstr.append(" union ");
					}
					sqlstr.append(" select '");
					sqlstr.append(dbArr[i]);
					sqlstr.append("',");
					sqlstr.append(dbArr[i]);
					sqlstr.append("A01");
					sqlstr.append(".A0100,B0110,"+dbArr[i]+"A01.E0122,"+dbArr[i]+"A01.E01A1");
					if(whl!=null&&whl.trim().length()>1){
						sqlstr.append(whl);
						sqlstr.append(this.doInitOrgUnit(flag));
						//if("0".equals(flag))
							//sqlstr.append(" and "+this.getOrgCodesetid(a_code)+" like '"+a_code.substring(2)+"%'");
					}else{
						sqlstr.append(" FROM ");
						sqlstr.append(dbArr[i]);
						sqlstr.append("A01");
						sqlstr.append(" where "+this.doInitOrgUnit(flag).substring(4));
						//if("0".equals(flag))
							//sqlstr.append(" and "+this.getOrgCodesetid(a_code)+" like '"+a_code.substring(2)+"%'");
					}
				}
			}
			insertstr.append(sqlstr.toString());
			if(insertstr!=null&&insertstr.length()>1){
				dao.update(insertstr.toString());
			}
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 统计项目求和,最大值，最小值，平均值
	 * @param field //操作指标
	 * @param expr　//计算公式
	 * @param flag　//求值类型 1.求和　2.求最小值　3.求最大值 4.求平均值
	 * @param sexpr //条件表达式
	 * @param sfactor　//因子表达式
	 * @param lower　//是否包含下级 1.包含下级　2.不包含下级
	 */
	private void statUpdateSum(String field,String expr,String flag,String sexpr,String sfactor,String lower,String f,String a_code){
		try{
			FieldItem fielditem = DataDictionary.getFieldItem(field);
			DbWizard dbWizard = new DbWizard(this.conn);
			BatchBo batchBo = new BatchBo();
			String temp_name="t#"+this.userView.getUserName()+"_hr_org_1";//目标临时表
			String tempTable=batchBo.getTempTable(userView);
			ArrayList templist =  getFieldList();
			templist.add(fielditem.cloneField());
			Field temp = new Field("I9999","I9999");
			temp.setDatatype(DataType.INT);
			temp.setCodesetid("0");
			templist.add(temp);
			createMusterTempTable(temp_name,templist,dbWizard);
			String[] dbArr = dbpre.split(",");
			for(int i=0;i<dbArr.length;i++){
				if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
					String whl="";
					if(sfactor!=null&&sfactor.trim().length()>0&&sexpr!=null&&sexpr.trim().length()>0){
						if(!userView.isSuper_admin()){
							whl=userView.getPrivSQLExpression(sfactor+"|"+sexpr,dbArr[i],false,false,true,new ArrayList()) ;
						}else{
							FactorList factorslist=new FactorList(sfactor,sexpr,dbArr[i],false,false,true,1,userView.getUserId());
							whl=factorslist.getSqlExpression();
						}
					}
					if(whl!=null&&whl.trim().length()>0){
						whl="select "+dbArr[i]+"A01.A0100 "+whl;
					}
					ArrayList alUsedFields = DataDictionary.getAllFieldItemList(
							Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
					YksjParser yp = new YksjParser(this.userView,alUsedFields,YksjParser.forSearch,getvarType("N"),YksjParser.forPerson,"Ht",dbArr[i]);
					yp.setCon(this.conn);
					yp.run(expr);
					
					insrtTempTable(tempTable,dbArr[i],yp.getMapUsedFieldItems().values(),flag,yp.getGz_stdFieldMap(),f,a_code);//填充临时表数据
					
					StringBuffer sqlstr = new StringBuffer();
					sqlstr.append("insert into ");
					sqlstr.append(temp_name);
					sqlstr.append("(NBASE,B0110,E0122,A0100,I9999,");
					sqlstr.append(field);
					sqlstr.append(")select '"+dbArr[i]+"',B0110,E0122,A0100,I9999,");
					sqlstr.append(yp.getSQL());
					sqlstr.append(" from ");
					sqlstr.append(tempTable);
					//xuj add 2010-6-30 按统计条件统计
					if(whl!=null&&whl.trim().length()>0){
						sqlstr.append(" where a0100 in ("+whl+")");
					}else {
                        sqlstr.append(" where 1=1");
                    }
					sqlstr.append(this.doInitOrgUnit(f));
					//if("0".equals(f))
						//sqlstr.append(" and "+this.getOrgCodesetid(a_code)+" like '"+a_code.substring(2)+"%'");
					
					dao.update(sqlstr.toString());
					sqlstr.setLength(0);
					sqlstr.append("update ");
					sqlstr.append(temp_name);
					sqlstr.append(" set E01A1=(select E01A1 from ");
					sqlstr.append(dbArr[i]+"A01 where "+temp_name+".A0100="+dbArr[i]+"A01.A0100) where nbase='"+dbArr[i]+"'");
					sqlstr.append(this.doInitOrgUnit(f));
					//if("0".equals(f))
						//sqlstr.append(" and "+this.getOrgCodesetid(a_code)+" like '"+a_code.substring(2)+"%'");
					
					dao.update(sqlstr.toString());
				}
			}
			
			sumPro(field,temp_name,lower,flag);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 填充计算公式中的临时表
	 * @param tempTable //临时表temp_xxx
	 * @param dbname //人员库
	 * @param collection //公式中设计的字段
	 * @param gz_standFieldMap 薪资标准涉及到的指标
	 * @param flag //求值类型 1.求和　2.求最小值　3.求最大值 4.求平均值
	 */
	private void insrtTempTable(String tempTable,String dbname,Collection collection,String flag,HashMap gz_standFieldMap,String f,String a_code){
		Iterator it = collection.iterator();
		while (it.hasNext()) {
			StringBuffer buf = new StringBuffer();
			FieldItem fieldItem = (FieldItem) it.next();
			if(fieldItem==null) {
                continue;
            }
			if(fieldItem.getVarible()!=0) {
                continue;
            }
			String itemid=fieldItem.getItemid();
			if(gz_standFieldMap.get(itemid.toLowerCase())!=null) //薪资标准涉及到的指标不需更新
            {
                continue;
            }
			
			//add by wangchaoqun on 2014-9-10  判断为人员子集时加上usr
			String tablename;
			if(fieldItem.isPerson()){
				tablename = dbname+fieldItem.getFieldsetid();
			}else{
				tablename = fieldItem.getFieldsetid();
			}
			
			FieldSet fieldset = DataDictionary.getFieldSetVo(fieldItem.getFieldsetid());
			if(fieldset!=null){
				buf.append("update "+tempTable+" set "+itemid+"=(select ");
				if(fieldset.isMainset()){
					buf.append(""+itemid+" from "+tablename);
					buf.append(" where A0100="+tempTable+".A0100 ");
				}else{
					if("0".equals(fieldset.getChangeflag())){
						buf.append(""+itemid+" from "+tablename);
						buf.append(" where A0100="+tempTable+".A0100 ");
						buf.append("and I9999=(select max(I9999) from ");
						buf.append(tablename);
						buf.append(" a where a.A0100=");
						buf.append(tablename);
						buf.append(".A0100");
						buf.append(")");
					}else{
						buf.append(" sum("+itemid+") from "+tablename);
						buf.append(" where A0100="+tempTable+".A0100 ");
						TimeScope ts = new TimeScope();
						String time = "";
						if("2".equals(this.changeflag)){
							time = year;
						}else{
							int inputmonth = Integer.parseInt(month);
							if(inputmonth>0 && inputmonth<10) {
                                time = year+"-0"+month;
                            } else {
                                time = year+"-"+month;
                            }
						}
						buf.append("and "+ts.getTimeCond(fieldItem.getFieldsetid()+"Z0","=",time));
					}
				}
			
			/*if(flag.equals("1")){
				
			}else if(flag.equals("2")){
				buf.append(" sum("+itemid+") from "+tablename);
			}else if(flag.equals("3")){
				buf.append(" sum("+itemid+") from "+tablename);
			}else if(flag.equals("4")){
				buf.append(" sum("+itemid+") from "+tablename);
			}*/
			
			//FieldItem fielditem = DataDictionary.getFieldItem(fieldItem.getFieldsetid()+"Z0");
			
			/*if(fielditem!=null&&fielditem.getItemtype().equalsIgnoreCase("D")){
				buf.append("and ");
				buf.append(getequalz0time();
				
			}*/
			
			/*	if(fieldset.getChangeflag().equals("0")&&!fieldset.isMainset()){
					buf.append("and I9999=(select max(I9999) from ");
					buf.append(tablename);
					buf.append(" a where a.A0100=");
					buf.append(tablename);
					buf.append(".A0100");
					buf.append(")");
				}else if(!fieldset.isMainset()){
					
				}
			}else
				continue;*/
				buf.append(") where 1=1 ");
				buf.append(this.doInitOrgUnit(f));
				//if("0".equals(f))
					//buf.append(" and "+this.getOrgCodesetid(a_code)+" like '"+a_code.substring(2)+"%'");
			}
			try {
				dao.update(buf.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 项目统计
	 * @param field //计算项
	 * @param temptable　//临时表
	 * @param lower　//是否包含下级
	 * @param flag //求值类型 1.求和　2.求最小值　3.求最大值 4.求平均值
	 */
	private void sumPro(String field,String temptable,String lower,String flag){
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("update ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" set ");
		sqlstr.append(field);
		sqlstr.append("=(select ");
		if("1".equals(flag)) {
            sqlstr.append("sum(");
        } else if("2".equals(flag)) {
            sqlstr.append("min(");
        } else if("3".equals(flag)) {
            sqlstr.append("max(");
        } else if("4".equals(flag)) {
            sqlstr.append("avg(");
        }
		sqlstr.append(field);
		sqlstr.append(") from ");
		sqlstr.append(temptable);
		sqlstr.append(" where ");
		sqlstr.append(fieldsetid);
		sqlstr.append("."+getItemid()+"=");
		sqlstr.append(temptable);
		sqlstr.append("."+getItemid());
		
		if(lower!=null&& "1".equals(lower)){
			if("B0110".equalsIgnoreCase(getItemid())){
				sqlstr.append(" or ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2){
					sqlstr.append(" rpad(");
					sqlstr.append(temptable);
					sqlstr.append(".B0110,length(");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110))=");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110");
					sqlstr.append(" or ");
					sqlstr.append(" rpad(");
					sqlstr.append(temptable);
					sqlstr.append(".E0122,length(");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110))=");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110");
				}else{
					sqlstr.append(" left(");
					sqlstr.append(temptable);
					sqlstr.append(".B0110,len(");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110))=");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110");
					sqlstr.append(" or ");
					sqlstr.append(" left(");
					sqlstr.append(temptable);
					sqlstr.append(".E0122,len(");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110))=");
					sqlstr.append(fieldsetid);
					sqlstr.append(".B0110");
				}
			}else{
				sqlstr.append(" or ");
				if(Sql_switcher.searchDbServer()==Constant.ORACEL||Sql_switcher.searchDbServer()==Constant.DB2){
					sqlstr.append(" rpad(");
					sqlstr.append(temptable);
					sqlstr.append("."+getItemid()+",length(");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid()+"))=");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid());
				}else{
					sqlstr.append(" left(");
					sqlstr.append(temptable);
					sqlstr.append("."+getItemid()+",len(");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid()+"))=");
					sqlstr.append(fieldsetid);
					sqlstr.append("."+getItemid()+"");
				}
			}
		}else{
			if("B0110".equalsIgnoreCase(getItemid())){
				sqlstr.append(" or ");
				sqlstr.append(fieldsetid);
				sqlstr.append(".B0110=");
				sqlstr.append(temptable);
				sqlstr.append(".E0122");
			}
		}
		if(!"0".equals(changeflag)){
			sqlstr.append(") where Id='");
			sqlstr.append(getId());
			sqlstr.append("' and I9999=(select max(I9999) from ");
			sqlstr.append(fieldsetid);
			sqlstr.append(" a where a."+getItemid()+"=");
			sqlstr.append(fieldsetid);
			sqlstr.append("."+getItemid()+" and Id='");
			sqlstr.append(getId());
			sqlstr.append("')");
		}else{
			sqlstr.append(") where ");
			sqlstr.append(" I9999=(select max(I9999) from ");
			sqlstr.append(fieldsetid);
			sqlstr.append(" a where a."+getItemid()+"=");
			sqlstr.append(fieldsetid);
			sqlstr.append("."+getItemid());
			sqlstr.append(")");
		}
		try {
			dao.update(sqlstr.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private YearMonthCount getYMC(){
		YearMonthCount ymc = null;
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select ");
		sqlstr.append(fieldsetid);
		sqlstr.append("Z0,"+fieldsetid+"Z1 from ");
		sqlstr.append(fieldsetid);
		sqlstr.append(" where ");
		sqlstr.append(getId("Id")+" and "+fieldsetid+"Z0 is not null and "+fieldsetid+"Z1 is not null");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(sqlstr.toString());
			int year=0;
			int month=0;
			int z0=0;
			if(rs.next()){
				Date date = rs.getDate(1);
				z0 = rs.getInt(2);
				year = DateUtils.getYear(date);
				month = DateUtils.getMonth(date);
			}
			ymc = new YearMonthCount(year,month,z0);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return ymc;
	}

	private void setId(){
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(fieldsetid);
		buf.append(" set Id='");
		buf.append(getId());
		buf.append("' where ");
		buf.append(getequalz0time(fieldsetid+"Z0"));
		/*buf.append(" and I9999=(select max(I9999) from ");//xuj 2010-5-1计算所有的记录
		buf.append(fieldsetid);
		buf.append(" c where ");
		buf.append(getequalz0time("c."+fieldsetid+"Z0"));
		buf.append(" and c."+getItemid()+"=");
		buf.append(fieldsetid);
		buf.append("."+getItemid()+"");
		buf.append(")");*/

		try {
			dao.update(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 新建临时表
	 * @param tableName //临时表名称
	 * @param fieldlist //临时表字段列表
	 * @param dbWizard
	 * @return
	 * @throws GeneralException
	 */

	private Table createMusterTempTable(String tableName,ArrayList fieldlist,
			DbWizard dbWizard) throws GeneralException {
		Table table = new Table(tableName);
		table.setCreatekey(false);
		for (int i = 0; i < fieldlist.size(); i++) {
            table.addField((Field) fieldlist.get(i));
        }
		if (dbWizard.isExistTable(table.getName(), false)) {
            dbWizard.dropTable(table);
        }
		dbWizard.createTable(table);
		// dbWizard.addPrimaryKey(table);
		return table;
	}
	/**
	 * 公式设置分解
	 * @param expr
	 * @return
	 */
	private String[] exprDecom(String expr){
		expr=expr!=null?expr:"";
		String[] exprArr = expr.split("::");
		return exprArr;
	}
	/**
	 * 获取统计汇总的包含下级和关联单位子集和指标
	 * @param expr
	 * @return arr 数组{是否包含下级,单位子集,单位子集的指标}
	 */
	public String[] getDep(String expr,String setid,String itemid){
		expr=expr!=null?expr:"";
		String arr[]=new String[5];
		String[] exprArr = expr.split("-");
		if(exprArr.length==2){
			arr[0]=exprArr[0]!=null?exprArr[0]:"";
			if(exprArr[1]!=null){
				String itemArr[]=exprArr[1].split("=");
				if(itemArr.length==2){
					if(itemArr[1]!=null){
						String itemArrs[]=itemArr[1].split("/");
						if(itemArrs.length==2){
							arr[1]=itemArrs[0];
							arr[2]=itemArrs[1];
						}
					}	
				}
			}
		}
		arr[3]=setid;
		arr[4]=itemid;
		return arr;
	}
	/**
	 * 临时表列表
	 * @param 
	 * @return
	 */
	private ArrayList getFieldList(){
		ArrayList list = new ArrayList();
		
		Field temp = new Field("NBASE", ResourceFactory
				.getProperty("popedom.db"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(10);
		temp.setCodesetid("0");
		list.add(temp);
		
		temp = new Field("A0100",  ResourceFactory
				.getProperty("hmuster.label.machineNo"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(20);
		temp.setCodesetid("0");
		list.add(temp);
		
		temp = new Field("B0110",  ResourceFactory
				.getProperty("hmuster.label.unitNo"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setCodesetid("UN");
		list.add(temp);
		
		temp = new Field("E0122", ResourceFactory
				.getProperty("hmuster.label.departmentNo"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setCodesetid("UM");
		list.add(temp);
		
		temp = new Field("E01A1","职位");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setCodesetid("@K");
		list.add(temp);
		
		return list;
	}
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	private int getvarType(String fieldtype){
		int varType = YksjParser.FLOAT; // float
		if ("D".equals(fieldtype)) {
            varType = YksjParser.DATEVALUE;
        } else if ("A".equals(fieldtype) || "M".equals(fieldtype)) {
            varType = YksjParser.STRVALUE;
        }
		return varType;
	}
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	private int getDateType(String fieldtype){
		int varType = DataType.FLOAT; // float
		if ("D".equals(fieldtype)) {
            varType = DataType.DATE;
        } else if ("A".equals(fieldtype) || "M".equals(fieldtype)) {
            varType = DataType.STRING;
        }
		return varType;
	}
	/**
	 * 获得年月标识时间
	 */
	private String getId(){
		if("2".equals(changeflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9) {
                return year+"."+month;
            } else {
                return year+".0"+month;
            }
		}
	}
	private String getId1(String changflag){
		if("2".equals(changflag)){
			return year;
		}else{
			if(month!=null&&Integer.parseInt(month)>9) {
                return year+"."+month;
            } else {
                return year+".0"+month;
            }
		}
	}
	/**
	 * 获取单位,部门,职位的itemid
	 * @return
	 */
	private String getItemid(){
		String itemid="B0110";
		if("K".equalsIgnoreCase(fieldsetid.substring(0,1))) {
            itemid = "E01A1";
        }
		return itemid;
	}
	/**
	 * 获取单位,部门,职位的itemid
	 * @return
	 */
	private String getItemid(String setid){
		String itemid="B0110";
		if("K".equalsIgnoreCase(setid.substring(0,1))) {
            itemid = "E01A1";
        }
		return itemid;
	}
	private void updateI9999(){
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(fieldsetid);
		buf.append(" set I9999=(select max(I9999)+1 from ");
		buf.append(fieldsetid);
		buf.append(" c where c.");
		buf.append(getItemid());
		buf.append("="+fieldsetid+".");
		buf.append(getItemid());
		buf.append(") where I9999='0'");
		buf.append(" and id='");
		buf.append(getId());
		buf.append("'");
		try {
			dao.update(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取单位,部门,职位的itemid
	 * @return
	 */
	private String getCodesetid(){
		String codesetid="'UN','UM'";
		if("K".equalsIgnoreCase(fieldsetid.substring(0,1))) {
            codesetid = "'@K'";
        }
		return codesetid;
	}
	/**
	 * 获取单位,部门,职位的itemid
	 * @return
	 */
//	private String getCodesetid(String setid){
//		String codesetid="'UN','UM'";
//		if(setid.substring(0,1).equalsIgnoreCase("K"))
//			codesetid = "'@K'";
//		return codesetid;
//	}
	
	/**
	 * 获取单位,部门,职位的itemid
	 * @return
	 */
	private String getCodesetid(String setid,int unitFlag){
		String codesetid="'UN','UM'";
		if("B".equalsIgnoreCase(setid.substring(0,1)) && unitFlag<1) {
            codesetid = " 'UN' ";
        } else if("K".equalsIgnoreCase(setid.substring(0,1))) {
            codesetid="'@K'";
        } else {
            codesetid="'UN','UM'";
        }
		
		return codesetid;
	}
	/**
	 * 获取当前日期字符串 格式xxxx-xx-xx
	 * @return String date 字符串
	 */
	private Date strDate(){
		java.util.Date date = new java.util.Date();
		return new Date(date.getTime());
	}
	/**
     * 生成正确的时间
     * @param yearnum 年
     * @param monthnum 月
     * @return 日期 格式为xxxx-xx-xx
     */
	private Date z0Time(){
		if("2".equals(changeflag)){
			return  java.sql.Date.valueOf(year+"-01-01");
		}else{
			if(month!=null&&Integer.parseInt(month)>9) {
                return  Date.valueOf(year+"-"+month+"-01");
            } else {
                return  Date.valueOf(year+"-0"+month+"-01");
            }
		}
		
	}
	/**
     * 生成正确的时间
     * @param yearnum 年
     * @param monthnum 月
     * @return 日期 格式为xxxx-xx-xx
     */
	private Date z0Time(String changflag){
		if("2".equals(changflag)){
			return  java.sql.Date.valueOf(year+"-01-01");
		}else{
			if(month!=null&&Integer.parseInt(month)>9) {
                return  Date.valueOf(year+"-"+month+"-01");
            } else {
                return  Date.valueOf(year+"-0"+month+"-01");
            }
		}
		
	}
	/**
	 * 删除临时表
	 * @param tabname
	 */
	public void dropTempTable(String tabname){
		DbWizard dbWizard = new DbWizard(this.conn);
		Table table = new Table(tabname);
		table.setCreatekey(false);
		if (dbWizard.isExistTable(table.getName(), false)) {
            dbWizard.dropTable(table);
        }
	}
	public ArrayList fieldList(String setname){
		ViewHideSortBo vsbo = new ViewHideSortBo(dao,this.userView,setname);
		String hideitemid=vsbo.getHideitemid();
		String sortitem=vsbo.getSortitem();
		
		//wangcq 2014-11-28 begin 数据库中没有数据时，自动加入所有指标
		if(sortitem.length()<1){
			if(!setname.startsWith("K")){
				FieldItem fi=DataDictionary.getFieldItem("B0110");
				sortitem = sortitem + "," + fi.getItemid();
			}else{
				FieldItem fi=new FieldItem();
				fi.setItemid("B0110");
				fi.setFieldsetid("UM");
				fi.setItemdesc(ResourceFactory.getProperty("column.sys.dept"));
				sortitem = sortitem + "," + fi.getItemid();

				FieldItem efi=DataDictionary.getFieldItem("E01A1");		
				sortitem = sortitem + "," + efi.getItemid();
			}
			FieldItem fi=new FieldItem();
			fi.setItemid("id");
			fi.setItemdesc(ResourceFactory.getProperty("hmuster.label.nybs"));
			sortitem = sortitem + "," + fi.getItemid();
			ArrayList fieldset1 = this.userView.getPrivFieldList(setname,Constant.USED_FIELD_SET);
			for(int i=0;i<fieldset1.size();i++){
				FieldItem fielditem = (FieldItem)fieldset1.get(i);
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					sortitem = sortitem + "," + fielditem.getItemid();
				}
			}
		}
		//wangcq 2014-11-28 end
		//当有新指标添加了，排序中未有此指标时(以下方法复制于xuj之前保存顺序指标)：wangcq 2014-11-27 begin
		ArrayList fieldset = DataDictionary.getFieldList(setname,Constant.USED_FIELD_SET);
		for(int i=0;i<fieldset.size();i++){
			FieldItem fielditem = (FieldItem)fieldset.get(i);
			if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
				if("0".equals(this.userView.analyseFieldPriv(fielditem.getItemid()))) {
                    continue;
                }
				if(sortitem.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
					sortitem = sortitem + "," + fielditem.getItemid();
				}
			}
		}
		//wangcq 2014-11-27 end
		ArrayList retlist=new ArrayList();
		String readonlyitem = condStat(setname);
		if(sortitem!=null&&sortitem.trim().length()>4){
			String[] sort = sortitem.split(",");
			for(int i=0;i<sort.length;i++){
				if(sort[i]!=null&&sort[i].trim().length()>0){
					if(hideitemid.trim().length()>1){
						if(hideitemid.toUpperCase().indexOf(sort[i].toUpperCase())==-1) {
                            continue;
                        }
					}
					if(!setname.startsWith("K")){
						if("id".equalsIgnoreCase(sort[i])){
							Field fielde=new Field("id",ResourceFactory.getProperty("hmuster.label.nybs"));
							fielde.setLength(10);
							fielde.setCodesetid("0");
							fielde.setDatatype(DataType.STRING);
							fielde.setReadonly(true);
							retlist.add(fielde);
						}else if("B0110".equalsIgnoreCase(sort[i])){
							FieldItem fi=DataDictionary.getFieldItem("B0110");
							Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
							field.setLength(100);
							field.setCodesetid(fi.getCodesetid());
							field.setDatatype(DataType.STRING);
							field.setReadonly(true);
							retlist.add(field);
						}else{
							FieldItem fielditem=DataDictionary.getFieldItem(sort[i]);
							if(fielditem==null||!"1".equals(fielditem.getUseflag())) {
                                continue;
                            }
							StringBuffer format=new StringBuffer();	
							Field field=new Field(fielditem.getItemid(),fielditem.getItemdesc());
							field.setLength(fielditem.getItemlength());
							field.setCodesetid(fielditem.getCodesetid());
							if("N".equals(fielditem.getItemtype())){
								field.setDecimalDigits(fielditem.getDecimalwidth());
								if(fielditem.getDecimalwidth()>0){
									for(int j=0;j<fielditem.getDecimalwidth();j++){
										format.append("#");	
									}
									field.setFormat("####."+format.toString());
								}else{
									field.setFormat("####");
								}
							}
							field.setDatatype(getDateType(fielditem.getItemtype()));
							field.setReadonly(false);
							if("0".equals(this.userView.analyseFieldPriv(field.getName()))) {
                                field.setVisible(false);
                            } else {
                                field.setVisible(true);
                            }
							if("1".equals(userView.analyseFieldPriv(field.getName()))) {
                                field.setReadonly(true);
                            } else {
                                field.setReadonly(false);
                            }
							if(readonlyitem.toLowerCase().indexOf(field.getName().toLowerCase())!=-1){
								field.setReadonly(true);
							}
							retlist.add(field);
						}
					}else{
						if("B0110".equalsIgnoreCase(sort[i])){
							FieldItem fi=DataDictionary.getFieldItem("B0110");
							Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.dept"));
							field.setLength(100);
							field.setCodesetid(fi.getCodesetid());
							field.setDatatype(DataType.STRING);
							field.setReadonly(true);
							retlist.add(field);
						}else if("id".equalsIgnoreCase(sort[i])){
							Field fielde=new Field("id",ResourceFactory.getProperty("hmuster.label.nybs"));
							fielde.setLength(10);
							fielde.setCodesetid("0");
							fielde.setDatatype(DataType.STRING);
							fielde.setReadonly(true);
							retlist.add(fielde);
						}else if("E01A1".equalsIgnoreCase(sort[i])){
							FieldItem efi=DataDictionary.getFieldItem("E01A1");
							Field fielde=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
							fielde.setLength(100);
							fielde.setCodesetid(efi.getCodesetid());
							fielde.setDatatype(DataType.STRING);
							fielde.setReadonly(true);
							retlist.add(fielde);
						}else{
							FieldItem fielditem=DataDictionary.getFieldItem(sort[i]);
							if(fielditem==null||!"1".equals(fielditem.getUseflag())) {
                                continue;
                            }
							Field field=fielditem.cloneField();
							field.setReadonly(false);
							if("0".equals(this.userView.analyseFieldPriv(field.getName()))) {
                                field.setVisible(false);
                            } else {
                                field.setVisible(true);
                            }
							if("1".equals(userView.analyseFieldPriv(field.getName()))) {
                                field.setReadonly(true);
                            } else {
                                field.setReadonly(false);
                            }
							if(readonlyitem.toLowerCase().indexOf(field.getName().toLowerCase())!=-1){
								field.setReadonly(true);
							}
							retlist.add(field);
						}
					}
				}
			}
		}else{
			if(!setname.startsWith("K")){
				if(hideitemid.trim().length()>1){
					if(hideitemid.toUpperCase().indexOf("B0110")!=-1){
						FieldItem fi=DataDictionary.getFieldItem("B0110");
						Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
						field.setLength(100);
						field.setCodesetid(fi.getCodesetid());
						field.setDatatype(DataType.STRING);
						field.setReadonly(true);
						retlist.add(field);
					}
				}else{
					FieldItem fi=DataDictionary.getFieldItem("B0110");
					Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.org"));
					field.setLength(100);
					field.setCodesetid(fi.getCodesetid());
					field.setDatatype(DataType.STRING);
					field.setReadonly(true);
					retlist.add(field);
				}
			}else{
				if(hideitemid.trim().length()>1){
					if(hideitemid.toUpperCase().indexOf("B0110")!=-1){
						FieldItem fi=DataDictionary.getFieldItem("B0110");
						Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.dept"));
						field.setLength(100);
						field.setCodesetid(fi.getCodesetid());
						field.setDatatype(DataType.STRING);
						field.setReadonly(true);
						retlist.add(field);
					}
				}else{
					FieldItem fi=DataDictionary.getFieldItem("B0110");
					Field field=new Field("B0110",ResourceFactory.getProperty("column.sys.dept"));
					field.setLength(100);
					field.setCodesetid(fi.getCodesetid());
					field.setDatatype(DataType.STRING);
					field.setReadonly(true);
					retlist.add(field);
				}
				
				if(hideitemid.trim().length()>1){
					if(hideitemid.toUpperCase().indexOf("E01A1")!=-1){
						FieldItem efi=DataDictionary.getFieldItem("E01A1");
						Field fielde=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
						fielde.setLength(100);
						fielde.setCodesetid(efi.getCodesetid());
						fielde.setDatatype(DataType.STRING);
						fielde.setReadonly(true);
						retlist.add(fielde);
					}
				}else{
					FieldItem efi=DataDictionary.getFieldItem("E01A1");
					Field fielde=new Field("E01A1",ResourceFactory.getProperty("column.sys.pos"));
					fielde.setLength(100);
					fielde.setCodesetid(efi.getCodesetid());
					fielde.setDatatype(DataType.STRING);
					fielde.setReadonly(true);
					retlist.add(fielde);
				}
			}		
			if(hideitemid.trim().length()>1){
				if(hideitemid.toUpperCase().indexOf("ID")!=-1){
					Field fielde=new Field("id",ResourceFactory.getProperty("hmuster.label.nybs"));
					fielde.setLength(10);
					fielde.setCodesetid("0");
					fielde.setDatatype(DataType.STRING);
					fielde.setReadonly(true);
					retlist.add(fielde);
				}
			}else{
				Field fielde=new Field("id",ResourceFactory.getProperty("hmuster.label.nybs"));
				fielde.setLength(10);
				fielde.setCodesetid("0");
				fielde.setDatatype(DataType.STRING);
				fielde.setReadonly(true);
				retlist.add(fielde);
			}

			List fslist = this.userView.getPrivFieldList(setname,Constant.USED_FIELD_SET);
			
			for(int i=0;i<fslist.size();i++){
				FieldItem fielditem = (FieldItem)fslist.get(i);
				if(hideitemid.trim().length()>1){
					if(hideitemid.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())==-1){
						continue;
					}
				}
				
				if(!fielditem.getItemdesc().equals(ResourceFactory.getProperty("hmuster.label.nybs"))){
					Field field=fielditem.cloneField();
					if("0".equals(userView.analyseFieldPriv(field.getName()))) {
                        field.setVisible(false);
                    } else {
                        field.setVisible(true);
                    }
					if("1".equals(userView.analyseFieldPriv(field.getName()))
							&& "1".equals(userView.analyseFieldPriv(field.getName(),1))) {
                        field.setReadonly(true);
                    } else {
                        field.setReadonly(false);
                    }
					if(readonlyitem.toLowerCase().indexOf(field.getName().toLowerCase())!=-1){
						field.setReadonly(true);
					}
					retlist.add(field);
				}
			}			
		}
		return retlist;
	}
	public String condStat(String fieldsetid){
		String readonlyitem = "";
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select itemid,Expression from fielditem where fieldsetid='");
		sqlstr.append(fieldsetid);
		sqlstr.append("'");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs= dao.search(sqlstr.toString());
			while(rs.next()){
				String itemid = rs.getString("itemid");
				String Expression = rs.getString("Expression");
				Expression=Expression!=null?Expression:"";
				if(Expression.trim().length()>0){
					if("1".equals(Expression.substring(0,1))||
							"2".equals(Expression.substring(0,1))){
						readonlyitem+=itemid+",";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return readonlyitem;
	}
	
/**
 * 编制管理走操作单位，数据联动走管理范围
 * @param itemid
 * @param flag 0编制管理 1数据联动
 * @return
 */
	public String doInitOrgUnit(String itemid,String flag){
		StringBuffer sql=new StringBuffer();
			if("1".equals(flag)){//数据联动走管理范围
				sql.append(" and (");
				//String itemid=this.getItemid();
				String codesetid=this.userView.getManagePrivCode();
				String codeitemid=this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codesetid)){
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}else if("UM".equalsIgnoreCase(codesetid)){
					//itemid="e0122";
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}else if("@K".equalsIgnoreCase(codesetid)){
					//itemid="e01a1";
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}
				sql.append("1=1)");
			}else{//编制管理走操作单位
				String orgunit=this.userView.getUnit_id();
				orgunit=orgunit.toUpperCase();
				sql.append(" and (");
				if(!"UN".equals(orgunit)){
					String str[]=orgunit.split("`");
					//String itemid=this.getItemid();
					for(int i=0;i<str.length;i++){
						if(str[i].indexOf("UN")!=-1){
							sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
						}else if(str[i].indexOf("UM")!=-1){
							//itemid="e0122";
							sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
						}else{
							continue;
						}
					}
				}
				sql.append("1=2)");
				
			}
		return sql.toString();
	}
	
	public String doInitOrgUnit(String flag){
		StringBuffer sql=new StringBuffer();
			String orgunit=this.userView.getUnit_id();
			if("1".equals(flag)){//自助用户走管理范围
				sql.append(" and (");
				String itemid="b0110";
				String codesetid=this.userView.getManagePrivCode();
				String codeitemid=this.userView.getManagePrivCodeValue();
				if("UN".equalsIgnoreCase(codesetid)){
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}else if("UM".equalsIgnoreCase(codesetid)){
					itemid="e0122";
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}else if("@K".equalsIgnoreCase(codesetid)){
					itemid="e01a1";
					sql.append(itemid+" like '"+codeitemid+"%' and ");
				}
				sql.append("1=1)");
			}else{
				orgunit=orgunit.toUpperCase();
				String str[]=orgunit.split("`");
				sql.append(" and (");
				String itemid="b0110";
				if(!"UN".equals(orgunit)){
					for(int i=0;i<str.length;i++){
						if(str[i].indexOf("UN")!=-1){
							sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
						}else if(str[i].indexOf("UM")!=-1){
							itemid="e0122";
							sql.append(itemid+" like '"+str[i].substring(2)+"%' or ");
						}else{
							continue;
						}
					}
				}
				sql.append("1=2)");
			}
		return sql.toString();
	}
	
	public void resetDate(String changflag,String unit_set,String flag){
		if(!"0".equals(changflag)){
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append(" select codeitemid from organization where codeitemid not in(select ");
			sqlstr.append("b0110");
			sqlstr.append(" from ");
			sqlstr.append(unit_set);
			sqlstr.append(" where ");
			sqlstr.append(getequalz0time(unit_set+"Z0",changflag));
			sqlstr.append(" and I9999=(select max(I9999) from ");
			sqlstr.append(unit_set);
			sqlstr.append(" c where ");
			sqlstr.append(getequalz0time("c."+unit_set+"Z0",changflag));
			sqlstr.append(" and c.");
			sqlstr.append("b0110");
			sqlstr.append("=");
			sqlstr.append(unit_set);
			sqlstr.append(".");
			sqlstr.append("b0110");
			sqlstr.append(")) and codesetid in(");
			sqlstr.append("'UN','UM'");
			sqlstr.append(") and codeitemid in(select B0110 from B01)");	
			
			String[] dbArr = dbpre.split(",");
			if(dbArr!=null&&dbArr.length>0) {
                sqlstr.append(" and codeitemid in(");
            }
			StringBuffer unionstr = new StringBuffer();
			for(int i=0;i<dbArr.length;i++){
				if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
					if(unionstr.length()>0) {
                        unionstr.append(" union ");
                    }
					{
						unionstr.append(" select B0110 from ");
						unionstr.append(dbArr[i]);
						unionstr.append("A01");
						unionstr.append(" union select E0122 as B0110 from ");
						unionstr.append(dbArr[i]);
						unionstr.append("A01");
						unionstr.append(" union select B0110  from B01");
					}
				}
			}
			if(dbArr!=null&&dbArr.length>0){
				sqlstr.append(unionstr.toString());
				sqlstr.append(")");
			}
			sqlstr.append(this.doInitOrgUnit("codeitemid",flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = sdf.format(new java.util.Date());
			sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			RowSet rs = null;
			RowSet rsi = null;
			try {
				StringBuffer buf = new StringBuffer();
				buf.append("insert into ");
				buf.append(unit_set);
			//sqlstr.append(" and codeitemid like '"+a_code+"%'");//xujian 2010-7-8  当前机构树选中节点

				buf.append(" (");
				buf.append("b0110");
				buf.append(",I9999,");
				buf.append(unit_set);
				buf.append("Z0,");
				buf.append(unit_set);
				buf.append("Z1,CREATEUSERNAME,CREATETIME,MODUSERNAME,MODTIME,ID) values (?,?,?,?,?,?,?,?,?)");
				int maxi9999=0;
				rsi=dao.search("select max(i9999) maxi9999 from "+unit_set);
				while(rsi.next()){
					maxi9999=rsi.getInt("maxi9999");
					maxi9999++;
				}
				
				rs = dao.search(sqlstr.toString());
				ArrayList listvalue = new ArrayList();
				
				while(rs.next()){
					String B0110 = rs.getString("codeitemid");
					if(B0110!=null&&B0110.trim().length()>0){
						ArrayList list = new ArrayList();
						list.add(B0110);
						list.add(""+maxi9999);
						list.add(z0Time(changflag));
						list.add("1");
						list.add(this.userView.getUserName());
						list.add(strDate());
						list.add(this.userView.getUserName());
						list.add(strDate());
						list.add(getId());
						listvalue.add(list);
					}
				}
				dao.batchInsert(buf.toString(),listvalue);
				
				updateI9999();//将I9999等于0的置最大值
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
					if(rsi!=null) {
                        rsi.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			//setId();//设置年月变化字段
			StringBuffer buf = new StringBuffer();
			buf.append("update ");
			buf.append(unit_set);
			buf.append(" set Id='");
			buf.append(getId1(changflag));
			buf.append("' where ");
			buf.append(getequalz0time(unit_set+"Z0",changflag));
			/*buf.append(" and I9999=(select max(I9999) from ");//xuj 2010-5-1计算所有的记录
			buf.append(fieldsetid);
			buf.append(" c where ");
			buf.append(getequalz0time("c."+fieldsetid+"Z0"));
			buf.append(" and c."+getItemid()+"=");
			buf.append(fieldsetid);
			buf.append("."+getItemid()+"");
			buf.append(")");*/

			try {
				dao.update(buf.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			//resetDate0();
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append(" select codeitemid from organization where codeitemid not in(select ");
			sqlstr.append("b0110");
			sqlstr.append(" from ");
			sqlstr.append(unit_set);
			sqlstr.append(" where ");
			sqlstr.append("I9999=(select max(I9999) from ");
			sqlstr.append(unit_set);
			sqlstr.append(" c where ");
			sqlstr.append(" c.");
			sqlstr.append("b0110");
			sqlstr.append("=");
			sqlstr.append(unit_set);
			sqlstr.append(".");
			sqlstr.append("b0110");
			sqlstr.append(")) and codesetid in(");
			sqlstr.append("'UN','UM'");
			sqlstr.append(") and codeitemid in(select B0110 from B01)");	
			
			String[] dbArr = dbpre.split(",");
			if(dbArr!=null&&dbArr.length>0) {
                sqlstr.append(" and codeitemid in(");
            }
			StringBuffer unionstr = new StringBuffer();
			for(int i=0;i<dbArr.length;i++){
				if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
					if(unionstr.length()>0) {
                        unionstr.append(" union ");
                    }
					{
						unionstr.append(" select B0110 from ");
						unionstr.append(dbArr[i]);
						unionstr.append("A01");
						unionstr.append(" union select E0122 as B0110 from ");
						unionstr.append(dbArr[i]);
						unionstr.append("A01");
						unionstr.append(" union select B0110  from B01");
					}
				}
			}
			if(dbArr!=null&&dbArr.length>0){
				sqlstr.append(unionstr.toString());
				sqlstr.append(")");
			}
			sqlstr.append(this.doInitOrgUnit("codeitemid",flag));//xujian 2010-5-11只对管理权限范围内的人进行统计汇总计算
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String backdate = sdf.format(new java.util.Date());
			sqlstr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			RowSet rs = null;
			RowSet rsi = null;
			try {
				StringBuffer buf = new StringBuffer();
				buf.append("insert into ");
				buf.append(unit_set);
				buf.append(" (");
				buf.append("b0110");
				buf.append(",I9999,");
				buf.append("CREATEUSERNAME,CREATETIME,MODUSERNAME,MODTIME) values (?,?,?,?,?,?)");
				int maxi9999=0;
				rsi=dao.search("select max(i9999) maxi9999 from "+unit_set);
				while(rsi.next()){
					maxi9999=rsi.getInt("maxi9999");
					maxi9999++;
				}
				rs = dao.search(sqlstr.toString());
				ArrayList listvalue = new ArrayList();
				
				while(rs.next()){
					String B0110 = rs.getString("codeitemid");
					if(B0110!=null&&B0110.trim().length()>0){
						ArrayList list = new ArrayList();
						list.add(B0110);
						list.add(""+maxi9999);
						list.add(this.userView.getUserName());
						list.add(strDate());
						list.add(this.userView.getUserName());
						list.add(strDate());
						listvalue.add(list);
					}
				}
				dao.batchInsert(buf.toString(),listvalue);
				
				updateI9999();//将I9999等于0的置最大值
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				try{
					if(rs!=null) {
                        rs.close();
                    }
					if(rsi!=null) {
                        rsi.close();
                    }
				}catch(Exception e){
					e.printStackTrace();
				}
			}
				
		}
		
	}
	
	/**
	 * 数值类型进行转换
	 * @param type
	 * @return
	 */
	private int getDataType(String type)
	{
		int datatype=0;
		switch(type.charAt(0))
		{
		case 'A':  
			datatype=YksjParser.STRVALUE;
			break;
		case 'D':
			datatype=YksjParser.DATEVALUE;
			break;
		case 'N':
			datatype=YksjParser.FLOAT;
			break;
		}
		return datatype;
	}
	
	public void dataIsNUll(ContentDAO dao,String tablename,String itemid){
		WorkdiarySQLStr wss=new WorkdiarySQLStr();
		String tempstart=wss.getDataValue(itemid,"=","1900-01-01");
		StringBuffer buf = new StringBuffer();
		buf.append("update ");
		buf.append(tablename);
		buf.append(" set ");
		buf.append(itemid);
		buf.append("=null where ");
		buf.append(tempstart);
		try {
			dao.update(buf.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 考虑有父节点的时候父节点数据的正确采用父节点数据-旧的当前节点数据+新的当前节点数据
	 * @param a_code 
	 * @param sign -、+
	 */
	private void doCount(String a_code,String sign,ArrayList fields){
		RowSet rs = null;
		a_code = a_code.substring(2);
		String selectcode=a_code;
		try{
			if(fields.size()==0) {
                return;
            }
			
			StringBuffer sb = new StringBuffer();
			FieldItem fielditem = DataDictionary.getFieldItem((String)fields.get(0));
			String tablename = fielditem.getFieldsetid();
			sb.append("update "+tablename+" set ");
			int f=0;
			ArrayList items = new ArrayList();
			for(int i=0;i<fields.size();i++){
				fielditem = DataDictionary.getFieldItem((String)fields.get(i));
				String table = fielditem.getFieldsetid();
				if(!tablename.equalsIgnoreCase(table)){
					continue;
				}
				sb.append(fielditem.getItemid()+"="+fielditem.getItemid()+sign+"?,");
				items.add(fielditem.getItemid());
			}
			sb.setLength(sb.length()-1);
			ArrayList valuelist = new ArrayList();
			StringBuffer sql = new StringBuffer(); 
			while(true&&f<10){
				sql.setLength(0);
				sql.append("select parentid from organization where parentid<>codeitemid and codeitemid='"+a_code+"' and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date");
				rs = dao.search(sql.toString());
				if(rs.next()){
					String parentid = rs.getString("parentid");
					valuelist.clear();
					sql.setLength(0);
					sql.append("select ");
					for(int n=0;n<items.size();n++){
						sql.append(((String)items.get(n))+",");
					}
					sql.setLength(sql.length()-1);
					sql.append(" from "+tablename);
					sql.append(" where b0110='"+selectcode+"' and ");
					int l=sql.length();
					if(!"0".equals(this.changeflag)) {
                        sql.append(getId("Id")+" and ");
                    }
					sql.append("I9999=(select max(I9999) from ");
					sql.append(tablename+" c where c."+getItemid(tablename)+"="+tablename+"."+getItemid(tablename));
					if(!"0".equals(this.changeflag)) {
                        sql.append(" and "+getId("c.Id"));
                    }
					sql.append(")");
					rs=dao.search(sql.toString());
					if(rs.next()){
						for(int m=0;m<items.size();m++){
							valuelist.add(rs.getObject((String)items.get(m)));
						}
					}
					valuelist.add(parentid);
					String tempsql=sb.toString()+" where b0110=? and "+sql.substring(l);
					dao.update(tempsql, valuelist);
					a_code=parentid;
				}else{
					break;
				}
				f++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private int currentLevel(String a_code){
		a_code=a_code.substring(2);
		int currentlevel =1;
		StringBuffer sql=new StringBuffer("select grade from organization where codeitemid='"+a_code+"'");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new java.util.Date());
		sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
		RowSet rs = null;
		try {
			rs = dao.search(sql.toString());
			while(rs.next()){
				currentlevel = rs.getInt("grade");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(rs!=null) {
                    rs.close();
                }
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return currentlevel;
	}
//没有用到的方法，注掉了	
//	private String getPosFQL(String field,String c_expr){
//	String FSQL="";
//	int infoGroup =3;
//	FieldItem fielditem = DataDictionary.getFieldItem(field);
//	ArrayList alUsedFields = new ArrayList();
//	ArrayList unitFieldList = new ArrayList();
//	if(fieldsetid.substring(0,1).equalsIgnoreCase("B")){
//		infoGroup = YksjParser.forUnit;
//		unitFieldList = DataDictionary.getAllFieldItemList(
//				Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
//	}else if(fieldsetid.substring(0,1).equalsIgnoreCase("K")){
//		infoGroup = YksjParser.forPosition;
//		unitFieldList = DataDictionary
//		.getAllFieldItemList(Constant.USED_FIELD_SET,
//				Constant.POS_FIELD_SET);
//	}else{
//		infoGroup =YksjParser.forPerson;
//		unitFieldList = DataDictionary.getAllFieldItemList(
//				Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
//	}
//	try{
//		for (int a = 0; a < unitFieldList.size(); a++) {
//			FieldItem afielditem = (FieldItem) unitFieldList
//					.get(a);
//			alUsedFields.add(afielditem);
//		}
//
//		YksjParser yp = new YksjParser(this.userView,alUsedFields,YksjParser.forSearch,
//				getvarType(fielditem.getItemtype()),infoGroup,"","");
//		yp.setCon(conn);
//		if(!"0".equals(this.changeflag)){
//			yp.setYmc(getYMC());
//			yp.setYearMonthCount(true);
//		}
//		yp.run(c_expr);
//        FSQL=yp.getSQL();
//	}catch(Exception e){
//		e.printStackTrace();
//	}
//	return FSQL;
//}	
	
//	private String getFQL(String field,String c_expr){
//	String FSQL="";
//	int infoGroup =3;
//	if(fieldsetid.substring(0,1).equalsIgnoreCase("B")){
//		infoGroup =3;
//	}else{
//		infoGroup =1;
//	}
//	try{
//		ArrayList unitFieldList = new ArrayList();
//		if(fieldsetid.substring(0,1).equalsIgnoreCase("B")){
//			infoGroup = YksjParser.forUnit;
//			unitFieldList = DataDictionary.getAllFieldItemList(
//					Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
//		}else if(fieldsetid.substring(0,1).equalsIgnoreCase("K")){
//			infoGroup = YksjParser.forPosition;
//			unitFieldList = DataDictionary
//			.getAllFieldItemList(Constant.USED_FIELD_SET,
//					Constant.POS_FIELD_SET);
//		}else{
//			infoGroup =YksjParser.forPerson;
//			unitFieldList = DataDictionary.getAllFieldItemList(
//					Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
//		}
//		YksjParser yp = new YksjParser(this.userView,unitFieldList,YksjParser.forSearch,getvarType("N"),infoGroup,"","");
//		yp.setCon(conn);
//		if(!"0".equals(this.changeflag)){
//			yp.setYmc(getYMC());
//			yp.setYearMonthCount(true);
//		}
//		yp.run(c_expr);   
//        FSQL=yp.getSQL();
//	}catch(Exception e){
//		e.printStackTrace();
//	}
//	return FSQL;
//}	
	
//	private String getOrgCodesetid(String a_code){
//		String itemid="b0110";
//		a_code=a_code.substring(0,2);
//		if(a_code.equalsIgnoreCase("UM")){
//			itemid="e0122";
//		}
//		return itemid;
//	}
}
