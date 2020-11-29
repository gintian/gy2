package com.hjsj.hrms.businessobject.org.autostatic.confset;

import com.hjsj.hrms.utils.TimeScope;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class DataSynchroBo {
	private UserView uv;
	private String subsetstr;
	private ContentDAO dao;
	private String dbname;
	private String getyear;
	private String getmonth;
	private String changeflag;
	private String equalz0Counttimetwo;
	private ArrayList sqllist = new ArrayList();
	//调用的功能模块标识，=1：数据联动调用；=0：其他功能模块，默认为0
	private String module;
	
	public ArrayList getSqllist() {
		return sqllist;
	}
	public void setSqllist(ArrayList sqllist) {
		this.sqllist = sqllist;
	}
	public DataSynchroBo(){
		
	}
	public DataSynchroBo(UserView uv,String subsetstr,ContentDAO dao,String dbname,String getyear,String getmonth,String changeflag){		
		try {
			this.uv = uv;
			this.subsetstr = subsetstr;
			this.dao = dao;
			this.dbname = dbname;
			this.getyear = getyear;
			this.getmonth = getmonth;
			this.changeflag = changeflag;
			this.equalz0Counttimetwo = this.getequalz0time("b."+subsetstr,getyear,getmonth,changeflag);
			itemidList(subsetstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 获得所有 expression 不为空的子集,以及该子集对应的display标识
	 * @param subsetstr
	 */
	public void itemidList(String subsetstr){
		 ArrayList itemidlist =new ArrayList();
		 String itemidsql = "";
		 if(Sql_switcher.searchDbServer()== Constant.ORACEL)
		 {
			 itemidsql = "select displayid,expression from fielditem where expression is not null and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
		 }else
		 {
			 itemidsql = "select displayid,expression from fielditem where expression not like '' and fieldsetid = '"+subsetstr+"' and useflag = 1 ";		 
		 }
		// 获得所有 expression 不为空的子集,以及该子集对应的display标识
		 try {
			itemidlist = dao.searchDynaList(itemidsql);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSqllist(itemidlist);	
	}
	/**
	 * 判断表 : 单位|职位
	 * @param subsetstr
	 * @return
	 */
	public String subsetStr(String subsetstr){
		String sub = "";
		if(subsetstr!=null&&subsetstr.trim().length()>0) {
            sub = subsetstr.substring(0,1);
        }
		return sub;
	}
	/**
	 * 获得表的连接字段
	 * @param subsetstr
	 * @return
	 */
	public String id(String subsetstr){
		String id = "";
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
			id = "B0110";        // 表的连接字段
		}else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			id = "E01A1";         // 表的连接字段
		}
		return id;
	}
	/**
	 * 获得表的别名
	 * @param subsetstr
	 * @return
	 */
	public String tableaLias(String subsetstr){
		String tablealias = "";
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
			tablealias = "b";	       // 表的别名	
		}else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			tablealias = "k";         // 表的别名	
		}
		return tablealias;
	}
	/**
	 * 获得子表的主表
	 * @param subsetstr
	 * @return
	 */
	public String superTable(String subsetstr){
		String supertable = "";
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))){
			supertable = "B01";	        // 单位表的主表
		}else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			supertable = "K01";        // 职位表的主表
		}
		return supertable;
	}
	/**
	 * 删除原有记录
	 * @param subsetstr
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 */
	public void delSql(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		 StringBuffer sb = new StringBuffer();
		 ArrayList itemidlist =new ArrayList();
		 String itemidsql = "";
		 if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
             itemidsql = "select itemid from fielditem where expression is not null and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         } else {
             itemidsql = "select itemid from fielditem where expression not like '' and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         }
		 // 获得所有 expression 不为空的子集,以及该子集对应的display标识
		 try {
			itemidlist = dao.searchDynaList(itemidsql);
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				sb.append(","+itemid+"=null ");
			}
			String delsql = " update "+subsetstr+"set "+sb.substring(1).toString()+" where "+this.getequalz0time(subsetstr,getyear,getmonth,changeflag);
			dao.update(delsql);
//			System.out.println(delsql);		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 删除原有记录
	 * @param subsetstr
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 */
	public void deleteSql(String subsetstr,String getyear,String getmonth,String changeflag,String opertype)
	{
		 StringBuffer sb = new StringBuffer();
		 ArrayList itemidlist =new ArrayList();
		 String itemidsql = "";
		 if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
             itemidsql = "select itemid,expression  from fielditem where expression is not null and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         } else {
             itemidsql = "select itemid,expression  from fielditem where expression not like '' and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         }
//		 System.out.println(itemidsql);		
			// 获得所有 expression 不为空的子集,以及该子集对应的display标识
		 try {
			itemidlist = dao.searchDynaList(itemidsql);
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				String itemid = dynabean.get("itemid").toString();
				String expression = dynabean.get("expression").toString();
				if(!("3".equalsIgnoreCase(expression.substring(0,1).toString())))
				{
					if(expression.substring(0,1).toString().equalsIgnoreCase(opertype))
					{
						sb.append(","+itemid+"=null ");
					}
				}
				
			}
			if(!(sb==null || "".equals(sb.toString())))
			{
				String delsql = " update "+subsetstr+" set "+sb.substring(1).toString()+" where "+this.getequalz0time(subsetstr,getyear,getmonth,changeflag);
//				System.out.println(delsql);		
				dao.update(delsql);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getExpressionList(String subsetstr,String opertype){
		 ArrayList itemidlist =new ArrayList();
		 String itemidsql = "";
		 if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
             itemidsql = "select itemid,expression from fielditem where expression is not null and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         } else {
             itemidsql = "select itemid,expression from fielditem where expression not like '' and fieldsetid = '"+subsetstr+"' and useflag = 1 ";
         }

		 // 获得所有 expression 不为空的子集,以及该子集对应的display标识
		 try {
			itemidlist = dao.searchDynaList(itemidsql);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSqllist(itemidlist);	
	}
	/**
	 * 判断该表是否有记录
	 * @param subsetstr
	 * @return
	 */
	public String judgeSql(String subsetstr)
	{
		String judgestr = "";
		String judgetable = "";
//		if(subsetstr.substring(0,1).equals("B") || subsetstr.substring(0,1).equals("b")){			
//			judgetable =  "select B0110 from "+subsetstr; 
//		}else if(subsetstr.substring(0,1).equals("K") || subsetstr.substring(0,1).equals("k")){
//			judgetable =  "select E01A1 from "+subsetstr; 
//		}
		judgetable =  "select * from "+subsetstr; 
		String tempstr = "";
		int aa = 0;
		try {
			RowSet rs = dao.search(judgetable);
			if(rs.next())
			{
				judgestr = "insert";				
			}else {
                judgestr = "inserts";
            }
//			while(rs.next()){
//				if(subsetstr.substring(0,1).equals("B") || subsetstr.substring(0,1).equals("b")){			
//					aa += 1;
//				}else if(subsetstr.substring(0,1).equals("K") || subsetstr.substring(0,1).equals("k")){
//					//tempstr = rs.getString("E01A1");
//					aa += 1;
//				}
//				if(tempstr=="" || tempstr.equals(""))
//					judgestr = "inserts";
//				else
//				{
//					if(tempstr=="0" || tempstr.equals("0"))
//						judgestr = "inserts";
//					else
//						judgestr = "insert";
//				}
//				
//			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
//		System.out.println(judgestr);	
		return judgestr;
	}
	/**
	 * 
	 * @param subsetstr
	 * @param alUsedFields
	 * @return
	 */
	public int getinfoGroup(String subsetstr,ArrayList alUsedFields)
	{
		int infoGroup = 0;
		if("B".equals(subsetstr.substring(0,1))|| "b".equals(subsetstr.substring(0,1))){
			infoGroup = 3; // forUnit 单位
			alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		}
		else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			infoGroup = 1; // forPosition 职位
			ArrayList unitFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
			ArrayList positionFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
			for (Iterator t = unitFieldList.iterator(); t.hasNext();) {
				FieldItem fielditem = (FieldItem) t.next();
				alUsedFields.add(fielditem);
			}	
			for (int a=0;a<positionFieldList.size();a++) {
				FieldItem afielditem = (FieldItem)positionFieldList.get(a);					
				alUsedFields.add(afielditem);
			}
		}
		return infoGroup;
	}
	/**
	 * 
	 * @param subsetstr
	 * @param alUsedFields
	 * @return
	 */
	public ArrayList getalUsedFields(String subsetstr,ArrayList alUsedFields)
	{
		int infoGroup = 0;
		if("B".equals(subsetstr.substring(0,1))|| "b".equals(subsetstr.substring(0,1))){
			infoGroup = 3; // forUnit 单位
			alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.UNIT_FIELD_SET);
		}
		else if("K".equals(subsetstr.substring(0,1)) || "k".equals(subsetstr.substring(0,1))){
			infoGroup = 1; // forPosition 职位
			ArrayList unitFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.UNIT_FIELD_SET);
			ArrayList positionFieldList= DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET,Constant.POS_FIELD_SET);
			for (Iterator t = unitFieldList.iterator(); t.hasNext();) {
				FieldItem fielditem = (FieldItem) t.next();
				alUsedFields.add(fielditem);
			}	
			for (int a=0;a<positionFieldList.size();a++) {
				FieldItem afielditem = (FieldItem)positionFieldList.get(a);					
				alUsedFields.add(afielditem);
			}
		}
		return alUsedFields;
	}
	/**
	 * 获得数据类型
	 * @param fieldtype
	 * @param varType
	 * @return
	 */
	public int getvarType(String fieldtype,int varType)
	{
		if ("D".equals(fieldtype)) {
            varType = 9;
        } else if ("A".equals(fieldtype) || "M".equals(fieldtype)) {
            varType = 7;
        } else if ("N".equals(fieldtype)) {
            varType = 6;
        } else {
            varType = 6;
        }
		return varType;
	}
	/**
	 * 获得要进行的操作 eg: 求个数，求和 等等
	 * @param operstr
	 * @return
	 */
	public String getoperstr(String operstr)
	{
		if("0".equals(operstr))
		{
			operstr = "count";
		}
		if("1".equals(operstr))
		{
			operstr = "sum";
		}
		else if("2".equals(operstr))
		{
			operstr = "min";
		}
		else if("3".equals(operstr))
		{
			operstr = "max";
		}
		else if("4".equals(operstr))
		{
			operstr = "avg";
		}
		return operstr;
	}
	/**
	 * 将中文字符转换成要操作的字段
	 * @param uv
	 * @param alUsedFields
	 * @param varType
	 * @param infoGroup
	 * @param c_expr
	 * @param FSQL
	 * @return
	 */
	public String getFQL(UserView uv,ArrayList alUsedFields,int varType,int infoGroup,String c_expr,String FSQL)
	{
		try
		{
			YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,varType,infoGroup,"","");
			yp.run(c_expr);   
	        FSQL=yp.getSQL();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return FSQL;
	}
	/**
	 * 过滤中文字符
	 * @param uv
	 * @param alUsedFields
	 * @param varType
	 * @param infoGroup
	 * @param c_expr
	 * @param FSQL
	 * @return
	 */
	public boolean getContrlFQL(UserView uv,ArrayList alUsedFields,int varType,int infoGroup,String c_expr, String FSQL,boolean ret)
	{
		try
		{
			YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,varType,infoGroup,"","");
			yp.run(c_expr);   
	        FSQL=yp.getSQL();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			ret = false;
		}
		return ret;
	}
	/**
	 * 获得统计条件的Expr
	 * @param expr
	 * @param temp
	 * @return
	 */
	public String getExpr(String[] expr,String temp)
	{
//		 获得操作条件的字段
		 String secondpart = expr[1].toString();								
		 int splitnum = secondpart.split("::").length;
		 if(splitnum>0)
		 {
			 if(splitnum==1){
				 temp="";    // expression 表达式为空	
			 }
			 else
			 {
				 for(int x=1;x<splitnum+1;x++)
				 {
					 if(x==2)
					 {
						 temp = secondpart.split("::")[1].toString();	// 部分expression 表达式	
					 }
				 }
			 }									 
		 }	
		 return temp;
	}
	/**
	 * 获得统计条件的Expression
	 * @param expr
	 * @param temp
	 * @return
	 */
	public String getSubClass(String[] expr,StringBuffer sb,String temp,String subclass)
	{
		String thirdpart = expr[2].toString();
		 int splitnum = thirdpart.split("::").length;
		 if(splitnum>0){
			 for(int x=1;x<splitnum+1;x++)
			 {
				 if(x==1){
					 String factor = thirdpart.split("::")[0].toString();     // 获得条件的个数和关系
						if(factor.length()>0)
						{
							sb.append(factor+"|"+temp);
						}
						else{
							sb.append(""); // expression 表达式为空	
						}
				 }
				 else if(x==2){
					 thirdpart = thirdpart.split("::")[1].toString();   // 是否包含下级
						int splitsub = thirdpart.split("-").length;
						if(splitsub <=1){
							subclass = thirdpart;
						}
				 }
			 }
		 }
		 return subclass;
	}	
	/**
	 * 获得统计条件的Expression
	 * @param expr
	 * @param temp
	 * @return
	 */
	public StringBuffer getExpression(String[] expr,StringBuffer sb,String temp,String subclass)
	{
		String thirdpart = expr[2].toString();
		 int splitnum = thirdpart.split("::").length;
		 if(splitnum>0){
			 for(int x=1;x<splitnum+1;x++)
			 {
				 if(x==1){
					 String factor = thirdpart.split("::")[0].toString();     // 获得条件的个数和关系
						if(factor.length()>0)
						{
							sb.append(factor+"|"+temp);
						}
						else{
							sb.append(""); // expression 表达式为空	
						}
				 }
				 else if(x==2){
					 thirdpart = thirdpart.split("::")[1].toString();   // 是否包含下级
						int splitsub = thirdpart.split("-").length;
						if(splitsub <=1){
							subclass = thirdpart;
						}
				 }
			 }
		 }
		 return sb;
	}	
	/**
	 * 对没有记录的表的插入
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param uv
	 * @param insertz0time
	 * @param supertable
	 * @return
	 */
	public String inserts(String subsetstr,String id,String getz0,String time,String systime,String insertz0time,String supertable)
	{
		String[] dbnamearray ;
		int index  = dbname.split(",").length; 
		String dbnamestr = "";
		StringBuffer conditionsb = new StringBuffer();
		StringBuffer temp = new StringBuffer();
		if(index>1) // 有多个库前缀
		{
			dbnamearray = this.dbname.split(",");
			conditionsb.append(" and (");
			for(int t=0;t<index;t++)
			{
				dbnamestr = dbnamearray[t];
				if(dbnamestr!=null&&dbnamestr.trim().length()>0){
					temp.append(" or CodeItemid in (select b0110 from "+dbnamestr+"a01 where b0110= CodeItemid or e0122 = CodeItemid) ");
					temp.append(" or  CodeItemid in (select e0122 from "+dbnamestr+"a01 where b0110 = CodeItemid or  e0122 = CodeItemid)  ");
				}
			}
			if(temp!=null&&temp.length()>0){
				conditionsb.append(temp.substring(3).toString());
				conditionsb.append(" or CodeItemid in (select "+id+" from "+supertable+"  )");
				conditionsb.append(")");
			}
		}else
		{
			dbnamestr = this.dbname;
			conditionsb.append(" and (CodeItemid in (select b0110 from "+dbnamestr+"a01 where b0110= CodeItemid or e0122 = CodeItemid) ");
			conditionsb.append(" or  CodeItemid in (select e0122 from "+dbnamestr+"a01 where b0110 = CodeItemid or  e0122 = CodeItemid) ");
			conditionsb.append(" or CodeItemid in (select "+id+" from "+supertable+")");
			conditionsb.append(")");
		}
		StringBuffer  consql = new StringBuffer();		
		consql.append("insert into "+subsetstr+"("+id+","+subsetstr+"Z0,"+subsetstr+"Z1,i9999,ID,createtime,createusername)");
		consql.append(" Select Distinct Organization.CodeItemid,"+getz0+",1,1,"+time+","+systime+",");
		consql.append(" '"+uv.getUserName()+"' from Organization WHERE CodeItemid NOT IN (SELECT ");
		consql.append(id+" from (select "+id+" from "+subsetstr+" WHERE "+insertz0time+") y) ");
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1)))
		{
			consql.append("and  CodeSetId In ('UM','UN') ");			
		}
		else
		{
			consql.append("and  CodeSetId='@K' ");
		}
		consql.append(conditionsb.toString());
		return consql.toString();
	}
	/**
	 * 对没有相应年月记录的表的插入
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param uv
	 * @param insertz0time
	 * @param supertable
	 * @return
	 */
	public String insert(String subsetstr,String id,String getz0,String time,String systime,String insertz0time,String supertable)
	{
		String[] dbnamearray ;
		int index  = dbname.split(",").length; 
		String dbnamestr = "";
		StringBuffer conditionsb = new StringBuffer();
		StringBuffer temp = new StringBuffer();
		if(index>1) // 有多个库前缀
		{
			dbnamearray = this.dbname.split(",");
			conditionsb.append(" and (");
			for(int t=0;t<index;t++)
			{
				dbnamestr = dbnamearray[t].toString();
				temp.append(" or CodeItemid in (select b0110 from "+dbnamestr+"a01 where b0110= CodeItemid or e0122 = CodeItemid) ");
				temp.append(" or  CodeItemid in (select e0122 from "+dbnamestr+"a01 where b0110 = CodeItemid or  e0122 = CodeItemid)  ");
			}
			conditionsb.append(temp.substring(3).toString());
			conditionsb.append(" or CodeItemid in (select "+id+" from "+supertable+"  )");
			conditionsb.append(")");
		}else
		{
			dbnamestr = this.dbname;
			conditionsb.append(" and (CodeItemid in (select b0110 from "+dbnamestr+"a01 where b0110= CodeItemid or e0122 = CodeItemid) ");
			conditionsb.append(" or  CodeItemid in (select e0122 from "+dbnamestr+"a01 where b0110 = CodeItemid or  e0122 = CodeItemid) ");
			conditionsb.append(" or CodeItemid in (select "+id+" from "+supertable+")");
			conditionsb.append(")");
		}
		StringBuffer consql = new StringBuffer();
		consql.append("insert into "+subsetstr+"("+id+","+subsetstr+"Z0,"+subsetstr+"Z1,i9999,ID,createtime,createusername)");
		consql.append(" Select Distinct Organization.CodeItemid,"+getz0+",1,(select max(i9999) from ");
		consql.append(subsetstr+")+1 as i9999,"+time+","+systime+",");
		consql.append(" '"+uv.getUserName()+"' from Organization WHERE CodeItemid NOT IN (SELECT ");
		consql.append(id+" from (select "+id+" from "+subsetstr+" WHERE "+insertz0time+") y) ");
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1)))
		{
			consql.append("and  CodeSetId In ('UM','UN') ");			
		}
		else
		{
			consql.append("and  CodeSetId='@K' ");
		}
		consql.append(conditionsb.toString());
		return consql.toString();
	}
	/**
	 * 删除多余数据
	 * @param subsetstr
	 * @param id
	 * @param equalz0time
	 * @param supertable
	 */
	public void deleteGarbageDate(String subsetstr,String id,String equalz0time,String supertable)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" delete "+subsetstr);
		sb.append(" where "+equalz0time);
		sb.append(" and "+id+" not in( select "+id+" from "+supertable+")");
		try{
//			System.out.println(sb.toString());
			dao.update(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 计算项目，数据更新
	 * @param subsetstr
	 * @param countsetsb
	 * @param systime
	 * @param uv
	 * @param countsb
	 * @param equalz0time
	 * @param id
	 * @return
	 */
	public String countupdate(String subsetstr,String systime,String FSQL,String tablealias,String field,String equalz0time,String id)
	{
		StringBuffer countsqlsb = new StringBuffer();
		countsqlsb.append("update "+subsetstr+ " set "+field+"=");
		countsqlsb.append("(select sum("+FSQL+") as "+tablealias+"");
		countsqlsb.append(" from "+subsetstr+" "+tablealias);
		countsqlsb.append(" where "+equalz0time);
		countsqlsb.append(" and "+subsetstr+"."+id+"="+tablealias+"."+id+")");
		countsqlsb.append(",ModTime = "+systime+",ModUserName=");
		countsqlsb.append(" '"+uv.getUserName()+"' ");
		countsqlsb.append(" where "+equalz0time);
		String priv = "";
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			countsqlsb.append(" and "+id+" like '"+priv+"%' ");
		}
		countsqlsb.append("");
		return countsqlsb.toString();
	}
	/**
	 * 计算项目，数据更新的全部
	 * @param countgo
	 * @param judgestr
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param insertz0time
	 * @param supertable
	 * @param FSQL
	 * @param tablealias
	 * @param field
	 * @param equalz0time
	 */
	public void getcountsql(boolean countgo,String judgestr,String subsetstr,String id,String getz0,String time,
			String systime,String insertz0time,String supertable,String FSQL,String tablealias,String field,
			String equalz0time)
	{
		String countsql = "";
		try
		{
			if(countgo){
				countsql = this.countupdate(  subsetstr, systime, FSQL, tablealias, field, equalz0time, id);
//				System.out.println(countsql);
				dao.update(countsql);					
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 汇总项目，数据更新
	 * @param subsetstr
	 * @param collsetsb
	 * @param systime
	 * @param uv
	 * @param collectionsb
	 * @param id
	 * @param equalz0time
	 * @return
	 */
	public String collupdate(String subsetstr,String field,String tablealias,String systime,
			String id,String equalz0time)
	{
		String itemid = "B0110";
		if("K".equalsIgnoreCase(subsetstr.substring(0,1))) {
            itemid = "E01A1";
        }
		
		StringBuffer collsqlsb = new StringBuffer();
		collsqlsb.append(" update "+subsetstr+" set "+field+"=");
		collsqlsb.append("(select sum("+field+") as "+field);
		collsqlsb.append(" from "+subsetstr+" bo,organization org ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            collsqlsb.append(" where rpad(bo."+id+",length("+subsetstr+"."+id+"))="+subsetstr+"."+id);
        } else {
            collsqlsb.append(" where left(bo."+id+",len("+subsetstr+"."+id+"))="+subsetstr+"."+id);
        }
		collsqlsb.append(" and (org.codeitemid=org.childid or");
		collsqlsb.append(" (select count(codesetid) from organization ");
		collsqlsb.append("where codeitemid = org.childid and codesetid='@K')>0 ");
		collsqlsb.append("or (select count("+itemid+") from "+subsetstr);
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            collsqlsb.append(" where rpad("+itemid+",length(bo."+itemid+"))=bo."+itemid+")<2");
        } else {
            collsqlsb.append(" where left("+itemid+",len(bo."+itemid+"))=bo."+itemid+")<2");
        }
		collsqlsb.append(" and "+equalz0time);
		collsqlsb.append(") and ");
		collsqlsb.append(equalz0time);
		collsqlsb.append(" and org.codeitemid=bo."+itemid+" and org.codesetid<>'@K')");
		collsqlsb.append(" where "+equalz0time);
		collsqlsb.append(" and "+itemid+" not in(");
		collsqlsb.append("select "+itemid);
		collsqlsb.append(" from "+subsetstr+" bo,organization org ");
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            collsqlsb.append(" where rpad(bo."+id+",length("+subsetstr+"."+id+"))="+subsetstr+"."+id);
        } else {
            collsqlsb.append(" where left(bo."+id+",len("+subsetstr+"."+id+"))="+subsetstr+"."+id);
        }
		collsqlsb.append(" and (org.codeitemid=org.childid or");
		collsqlsb.append(" (select count(codesetid) from organization ");
		collsqlsb.append("where codeitemid = org.childid and codesetid='@K')>0 ");
		collsqlsb.append("or (select count("+itemid+") from "+subsetstr);
		if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
            collsqlsb.append(" where rpad("+itemid+",length(bo."+itemid+"))=bo."+itemid+")<2");
        } else {
            collsqlsb.append(" where left("+itemid+",len(bo."+itemid+"))=bo."+itemid+")<2");
        }
		collsqlsb.append(" and "+equalz0time);
		collsqlsb.append(") and ");
		collsqlsb.append(equalz0time);
		collsqlsb.append(" and  ModUserName=");
		collsqlsb.append(" '"+uv.getUserName()+"'");
		collsqlsb.append(" and org.codeitemid=bo."+itemid+" and org.codesetid<>'@K')");
		String priv = "";
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			collsqlsb.append(" and "+id+" like '"+priv+"%' ");
		}
		return collsqlsb.toString();
	}
	public void countSql(String subsetstr,String field,String tablealias,String systime,
			String id,String equalz0time){
		int level = maxLevel();
		String itemid = "B0110";
		if("K".equalsIgnoreCase(subsetstr.substring(0,1))) {
            itemid = "E01A1";
        }
		for(int i=level-1;i>0;i--){
			StringBuffer collsqlsb = new StringBuffer();
			collsqlsb.append(" update "+subsetstr+" set "+field+"=");
			collsqlsb.append("(select sum("+field+") as "+field);
			collsqlsb.append(" from "+subsetstr+" bo,organization org ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                collsqlsb.append(" where rpad(bo."+id+",length("+subsetstr+"."+id+"))="+subsetstr+"."+id);
            } else {
                collsqlsb.append(" where left(bo."+id+",len("+subsetstr+"."+id+"))="+subsetstr+"."+id);
            }
			collsqlsb.append(" and bo."+itemid+"<>"+subsetstr+"."+id);
			collsqlsb.append(" and "+equalz0time);
			collsqlsb.append(" and org.grade='"+(i+1)+"'");
			collsqlsb.append(" and org.codeitemid=bo."+itemid+" and org.codesetid<>'@K')");
			collsqlsb.append(" where "+equalz0time);
			collsqlsb.append(" and "+itemid+" in(");
			collsqlsb.append("select codeitemid from organization where grade='"+i);
			collsqlsb.append("' and codesetid<>'@K')");
			collsqlsb.append(" and (select count(bo."+itemid+")");
			collsqlsb.append(" from "+subsetstr+" bo,organization org ");
			if(Sql_switcher.searchDbServer()==Constant.ORACEL) {
                collsqlsb.append(" where rpad(bo."+id+",length("+subsetstr+"."+id+"))="+subsetstr+"."+id);
            } else {
                collsqlsb.append(" where left(bo."+id+",len("+subsetstr+"."+id+"))="+subsetstr+"."+id);
            }
			collsqlsb.append(" and bo."+itemid+"<>"+subsetstr+"."+id);
			collsqlsb.append(" and "+equalz0time);
			collsqlsb.append(" and org.grade='"+(i+1)+"')>0");

			try {
				dao.update(collsqlsb.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 汇总项目，数据更新
	 * @param judgestr
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param insertz0time
	 * @param supertable
	 * @param tablealias
	 * @param field
	 * @param equalz0time
	 */
	public void getcollsql(String judgestr,String subsetstr,String id,String getz0,String time,
			String systime,String insertz0time,String supertable,String tablealias,String field,
			String equalz0time)
	{
//		String collsql = "";
		try
		{
//			collsql = this.collupdate( subsetstr, field, tablealias, systime, id, equalz0time);						
//			dao.update(collsql);
			countSql(subsetstr, field, tablealias, systime, id, equalz0time);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * 统计项目，求个数，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param conditiontempsb
	 * @param tablealias
	 * @param id
	 * @param privsql
	 * @param expression
	 * @param y
	 * @return
	 */
	public StringBuffer conselectsingle(String subsetstr,int t,int index,StringBuffer conditiontempsb,String field,String id,String privsql,String expression)
	{		
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))) // 单位表
		{
			if(t!=index-1) // 不是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+field+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union ");
					conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
					if(this.uv.isSuper_admin())
					{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                            conditiontempsb.append(" where e0122 is not null and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");
                        } else {
                            conditiontempsb.append(" where e0122 not like '' and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");
                        }

					}else{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                            conditiontempsb.append(" and e0122 is not null and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");
                        } else {
                            conditiontempsb.append(" and e0122 not like '' and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");
                        }

					}
				}
				else
				{
					
					if(Sql_switcher.searchDbServer()== Constant.ORACEL){
						conditiontempsb.append(" and "+id+" is not null  group by "+id+" union  ");
						conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
						conditiontempsb.append(" and e0122 is not null and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");					
					}else{
						conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union  ");
						conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
						conditiontempsb.append(" and e0122 not like ''  and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 union all ");
				
					}
				}
				
			}
			else // 是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+field+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union ");
					conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
					if(this.uv.isSuper_admin())
					{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                            conditiontempsb.append(" where e0122 is not null  and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
                        } else {
                            conditiontempsb.append(" where e0122 not like ''  and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
                        }
						
					}else{
						if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                            conditiontempsb.append(" and e0122 is not null  and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
                        } else {
                            conditiontempsb.append(" and e0122 not like ''  and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
                        }
						
					}
					
				}
				else
				{
					
					if(Sql_switcher.searchDbServer()== Constant.ORACEL){
						conditiontempsb.append(" and "+id+" is not null  group by "+id+" union ");
						conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
						conditiontempsb.append(" and e0122 is not null and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
					}else{
						conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union ");
						conditiontempsb.append(" select count(*)as "+field+",e0122 " +privsql);
						conditiontempsb.append(" and e0122 not like '' and e0122 not in (select codeitemId from  organization where codesetid = 'UN') group by e0122 ");
					}
				}									
			}
		}
		else  // 职位表
		{
			if(t!=index-1) // 不是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+field+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" union all ");
				}
				else
				{
					if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                        conditiontempsb.append(" and "+id+" is not null  group by "+id+" union all ");
                    } else {
                        conditiontempsb.append(" and "+id+" not like ''  group by "+id+" union all ");
                    }
				}
				
			}
			else // 是最后一个库前缀
			{
				conditiontempsb.append(" select count(*)as "+field+"," +id+" " +privsql);
				if(expression == null || "".equals(expression))
				{
					conditiontempsb.append("  group by "+id+" ");
				}
				else
				{
					if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                        conditiontempsb.append(" and "+id+" is not null  group by "+id+" ");
                    } else {
                        conditiontempsb.append(" and "+id+" not like ''  group by "+id+" ");
                    }
				}									
			}
		}
		
//		System.out.println(conditiontempsb.toString());
		return conditiontempsb;
		
	}
//	/**
//	 * 逐级汇总的部分SQL
//	 * @param subsetstr
//	 * @param sum
//	 * @param y
//	 * @param equalz0time
//	 * @return
//	 */
//	public String getcontent(String subsetstr,String field,String equalz0time)
//	{
//		StringBuffer retsql = new StringBuffer();
//		retsql.append(",(select sum("+field+") from "+subsetstr+" a where a.b0110 in ");
//		retsql.append(" ( select codeitemid from organization where codeitemid ");
//		retsql.append(" like (b.b0110+'%') and (grade>(select grade from organization ");
//		retsql.append(" where codeitemid=b.b0110)  or ");
//		retsql.append(" grade=(select grade from organization where codeitemid=b.b0110))");
//		retsql.append(" ) and "+equalz0time);
//		retsql.append(" ) as "+field  );
////		System.out.println(retsql.toString());
//		return retsql.toString();
//	}
	/**
	 * 统计项目，求个数，多个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param conditiontempsb
	 * @param id
	 * @return
	 */
	public StringBuffer conselectedbs (int y,StringBuffer conditionsb,StringBuffer countsqlsb,String field,StringBuffer conditiontempsb,String id)
	{
		conditionsb.append(", ( select n."+id+",sum("+field+")as "+field+" from ("+conditiontempsb.toString()+" )n group by n."+id+") t ");
//		System.out.println(countsqlsb.toString());
		return conditionsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param id
	 * @param y
	 * @param tablealias
	 * @param dbnamestr
	 * @param sumconformulatempsb
	 * @param conformulastr
	 * @param congroupbystr
	 * @param conformulatabstr
	 * @param countsbstr
	 * @param conwherestr
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectkinds( String id,String field,String dbnamestr ,String conformulastr,String congroupbystr,String conformulatabstr,String countsbstr,String conwherestr,String privsql) 
	{
		StringBuffer retsb = new StringBuffer();
		retsb.append(" select a."+id+","+Sql_switcher.sqlNull("s."+field,0)+" as "+field+" from "+dbnamestr+"a01 a ");
		retsb.append(",( select "+countsbstr+conformulastr+" from "+conformulatabstr);
		retsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x");
		retsb.append(" where "+ conwherestr);
		retsb.append(" group by "+congroupbystr+")  s");
		if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
            retsb.append(" where a.a0100 = s.a0100 and "+id+" is not null ");
        } else {
            retsb.append(" where a.a0100 = s.a0100 and "+id+" not like '' ");
        }
//		System.out.println(sumconformulatempsb.toString());
		return retsb;
	}
	
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀，对多个库处理
	 * @param t
	 * @param index
	 * @param id
	 * @param y
	 * @param tablealias
	 * @param dbnamestr
	 * @param sumconformulatempsb
	 * @param conformulastr
	 * @param congroupbystr
	 * @param conformulatabstr
	 * @param countsbstr
	 * @param conwherestr
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectkindstwo(int t,int index, String id,String field,String dbnamestr, StringBuffer sumconformulatempsb,String conformulastr,String congroupbystr,String conformulatabstr,String countsbstr,String conwherestr,String privsql) 
	{
		StringBuffer retsb = new StringBuffer();
		if(t!=index-1) // 不是最后一个库前缀
		{									
			retsb.append(sumconformulatempsb.toString()+" union all select a.e0122,"+Sql_switcher.sqlNull("s."+field,0)+" as "+field+" from "+dbnamestr+"a01 a ");
			retsb.append(",( select "+countsbstr+conformulastr+" from "+conformulatabstr);
			retsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x");
			retsb.append(" where "+ conwherestr);
			retsb.append(" group by "+congroupbystr+")  s");
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                retsb.append(" where a.a0100 = s.a0100 and e0122 is not null ");
            } else {
                retsb.append(" where a.a0100 = s.a0100 and e0122 not like '' ");
            }
			//sumconformulatempsb.append(" group by a."+id+",s"+y+"."+tablealias+y+" union all " );
			retsb.append(" union all " );
		}
		else  // 是最后一个库前缀
		{
			retsb.append(sumconformulatempsb.toString()+" union all select a.e0122,"+Sql_switcher.sqlNull("s."+field,0)+" as "+field+" from "+dbnamestr+"a01 a ");
			retsb.append(",( select "+countsbstr+conformulastr+" from "+conformulatabstr);
			retsb.append(" ( select "+dbnamestr+"a01.a0100 "+privsql+" ) x");
			retsb.append(" where "+ conwherestr);
			retsb.append(" group by "+congroupbystr+") s");
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                retsb.append(" where a.a0100 = s.a0100 and e0122 is not null ");
            } else {
                retsb.append(" where a.a0100 = s.a0100 and e0122 not like '' ");
            }
		}
//		System.out.println(sumconformulatempsb.toString());
		return retsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，多个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param conditiontempsb
	 * @param id
	 * @return
	 */
	public StringBuffer conselectedbss(int t,int index ,StringBuffer fieldtemp,String id,String operstr,String field )
	{
		StringBuffer retsb = new StringBuffer();
		if(t!=index-1) // 不是最后一个库前缀
		{
			retsb.append(",( select w."+id+","+operstr+"(w."+field+") as "+field+" from (");	
			retsb.append(fieldtemp.toString()+")"+"w group by w."+id+" union all " );
		}
		else
		{
			retsb.append(",( select w."+id+","+operstr+"(w."+field+") as "+field+" from (");	
			retsb.append(fieldtemp.toString()+")"+"w group by w."+id+") t"+" " );
		}
//		System.out.println(countsqlsb.toString());
		return retsb;
	}
	/**
	 * 统计项目，求个数，一个库前缀
	 * @param y
	 * @param conum
	 * @param conditionsb
	 * @param countsqlsb
	 * @param tablealias
	 * @param id
	 * @param fieldcondition
	 * @param privsql
	 * @return
	 */
	public StringBuffer conselectedb (String subsetstr ,StringBuffer conditionsb ,String field,String id,String fieldcondition,String fieldconditiontwo,String privsql)
	{
		if("B".equals(subsetstr.substring(0,1)) || "b".equals(subsetstr.substring(0,1))) // 单位表
		{
			conditionsb.append(", ( select count(*)as "+field+"," +id+" " +privsql);
			conditionsb.append(fieldcondition+" group by "+id+" union ");
			conditionsb.append(" select count(*)as "+field+",e0122 " +privsql);
			conditionsb.append(fieldconditiontwo+" group by e0122 ) t ");
		}
		else
		{
			conditionsb.append(", ( select count(*)as "+field+"," +id+" " +privsql);
			conditionsb.append(fieldcondition+" group by "+id+" ) t ");
		}		
//		System.out.println(countsqlsb.toString());
		return conditionsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，一个库前缀
	 * @param y
	 * @param conum
	 * @param sumconformulasb
	 * @param sumconformulatempsb
	 * @param id
	 * @param operstr
	 * @param tablealias
	 * @param dbname
	 * @param countsqlsb
	 * @return
	 */
	public StringBuffer condbssingle( StringBuffer sumconformulasb,StringBuffer sumconformulatempsb,String id,String operstr,String dbname,String field)
	{
		sumconformulasb.append(",( select a."+id.toUpperCase());
		sumconformulasb.append(","+operstr+"(s."+field.toUpperCase()+")");
		sumconformulasb.append(" as "+field.toUpperCase()+" from "+dbname+"A01 a ");
		sumconformulasb.append(sumconformulatempsb.toString());
//		System.out.println(sumconformulasb.toString());
		return sumconformulasb;
	}
	
	public StringBuffer condbssingles(String jointemp,String id,String operstr,String dbname,String field)
	{
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(",( select a."+id+","+operstr+"(s."+field+")"+" as "+field+" from "+dbname+"a01 a ");
		sqlsb.append(jointemp);
		if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
            sqlsb.append(" )  s where a.a0100 = s.a0100 and B0110 is not null group by "+id);
        } else {
            sqlsb.append(" )  s where a.a0100 = s.a0100 and B0110 not like '' group by "+id);
        }
//		System.out.println(sumconformulasb.toString());
		return sqlsb;
	}
	
	public StringBuffer condbssinglestwo(StringBuffer countsqlsb,String jointemp,String id,String operstr,String dbname,String field)
	{
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append(countsqlsb.toString()+" union select a.e0122,"+operstr+"(s."+field+")"+" as "+field+" from "+dbname+"a01 a ");
		sqlsb.append(jointemp);
		if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
            sqlsb.append(" )  s where a.a0100 = s.a0100 and e0122 is not null group by a.e0122 ) t");
        } else {
            sqlsb.append(" )  s where a.a0100 = s.a0100 and e0122 not like '' group by a.e0122 ) t");
        }
//		System.out.println(sumconformulasb.toString());
		return sqlsb;
	}
	
	public StringBuffer condbssinglesthree(int t,String jointemp,String id,String operstr,String dbnamestr,String field)
	{
		StringBuffer sqlsb = new StringBuffer();
		if(t==0)
		{
			
			sqlsb.append("( select a."+id+","+operstr+"(s."+field+")"+" as "+field+" from "+dbnamestr+"a01 a ");
			sqlsb.append(jointemp);
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and B0110 is not null group by "+id);
            } else {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and B0110 not like '' group by "+id);
            }
//			System.out.println(sumconformulasb.toString());
		}
		else
		{			
			sqlsb.append(" select a."+id+","+operstr+"(s."+field+")"+" as "+field+" from "+dbnamestr+"a01 a ");
			sqlsb.append(jointemp);
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and B0110 is not null group by "+id);
            } else {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and B0110 not like '' group by "+id);
            }
//			System.out.println(sumconformulasb.toString());
		}
		
		return sqlsb;
	}
	public StringBuffer condbssinglesfour(int t,int index,StringBuffer countsqlsb,String jointemp,String id,String operstr,String dbname,String field)
	{
		StringBuffer sqlsb = new StringBuffer();
		if(t!=index-1) // 不是最后一个库前缀
		{		
			sqlsb.append(countsqlsb.toString()+" union select a.e0122,"+operstr+"(s."+field+")"+" as "+field+" from "+dbname+"a01 a ");
			sqlsb.append(jointemp);
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and e0122 is not null group by a.e0122 union all ");
            } else {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and e0122 not like '' group by a.e0122 union all ");
            }
//			System.out.println(sumconformulasb.toString());
		}
		else
		{
			sqlsb.append(countsqlsb.toString()+" union select a.e0122,"+operstr+"(s."+field+")"+" as "+field+" from "+dbname+"a01 a ");
			sqlsb.append(jointemp);
			if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and e0122 is not null group by a.e0122 ");
            } else {
                sqlsb.append(" ) s where a.a0100 = s.a0100 and e0122 not like '' group by a.e0122 ");
            }
//			System.out.println(sumconformulasb.toString());
		}
		
		return sqlsb;
	}
	
	public StringBuffer condbssinglesfive(StringBuffer countsqlsb,String id,String operstr,String field)
	{
		StringBuffer retsb = new StringBuffer();
		retsb.append(",( select "+id+","+operstr+"("+field+") as "+field+" from ");
		retsb.append(countsqlsb+") w group by "+id+") t");
		return retsb;
	}
	/**
	 * 统计项目，求和，求最大，最小，平均值，一个库前缀
	 * @param y
	 * @param conum
	 * @param sumconformulatempsb
	 * @param id
	 * @param operstr
	 * @param tablealias
	 * @param dbname
	 * @param countsqlsb
	 * @return
	 */
	public StringBuffer condbssingletwo(StringBuffer sumconformulatempsb,String id,String operstr,String dbname,StringBuffer countsqlsb,String field)
	{
		StringBuffer sumconformulasb = new StringBuffer();
		sumconformulasb.append(" union select a.e0122,"+operstr+"(s."+field+")"+" as "+field+" from "+dbname+"a01 a ");
		sumconformulasb.append(sumconformulatempsb.toString());
		sumconformulasb.append(") t");
		countsqlsb.append(sumconformulasb.toString());
//		System.out.println(sumconformulasb.toString());
		return countsqlsb;
	}
	/**
	 * 逐级汇总的SQL
	 * @param subsetstr
	 * @param contentsetsb
	 * @param contentsb
	 * @param equalz0time
	 * @return
	 */
	public String getcontentsql(String subsetstr,String field,String id,String equalz0time,String equalz0Counttime,String B0110)
	{
		StringBuffer retsql = new StringBuffer();		
		retsql.append(" update "+subsetstr+" set "+field+"=(  ");
		retsql.append(" select "+field+" from (");
		retsql.append(" select a."+id+", sum(b."+field+") as "+field+" from ");
		retsql.append(subsetstr+" a,"+subsetstr+" b ");
		retsql.append(" where "+equalz0Counttime);
		retsql.append(" and b."+id+" like (a."+id+Sql_switcher.concat()+"'%') ");
		String priv = "";
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			retsql.append(" and b."+id+" like '"+priv+"%' ");
		}
		retsql.append("  and b."+id+" in (select codeitemid from organization where codesetid = 'UM' )");
		retsql.append(" and "+this.equalz0Counttimetwo);
		retsql.append(" group by a."+id+") c where "+subsetstr+"."+id+" = c."+id+" )");
		retsql.append(" where "+equalz0time);
		retsql.append(" and I9999=(select max(I9999) from ");
		retsql.append(subsetstr+" c where c.B0110=B07.B0110 and "+equalz0time+") ");
		retsql.append(" and ("+id+" in (select codeitemid from organization where codesetid = 'UM'))");
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			retsql.append(" and "+id+" like '"+priv+"%' ");
		}
		return retsql.toString();
		

	}
	/**
	 * 求单位的逐级汇总的SQL
	 * @param subsetstr
	 * @param field
	 * @param equalz0time
	 * @return
	 */
	public String getcontent(String subsetstr,String field,String id,String equalz0time,String equalz0Counttime,String B0110,String upB0110)
	{
		StringBuffer retsql = new StringBuffer();		
		retsql.append(" update "+subsetstr+" set "+field+"=(  ");
		retsql.append(" select "+field+" from (");
		retsql.append(" select a."+id+", sum(b."+field+") as "+field+" from ");
		retsql.append(subsetstr+" a,"+subsetstr+" b ");
		retsql.append(" where "+equalz0Counttime);
		retsql.append(" and b."+id+" like (a."+id+""+Sql_switcher.concat()+"'%')");

		String priv = "";
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			retsql.append(" and b."+id+" like '"+priv+"%' ");
		}
		retsql.append(" and ("+B0110+") ");
		retsql.append(" and b."+id+" in (select codeitemid from organization where codesetid = 'UN' )");
		retsql.append(" and "+this.equalz0Counttimetwo);
		retsql.append(" group by a."+id+" ) c  where "+subsetstr+"."+id+" = c."+id+" )");
		retsql.append(" where "+equalz0time);
		retsql.append(" and I9999=(select max(I9999) from ");
		retsql.append(subsetstr+" c where c.B0110=B07.B0110 and "+equalz0time+") ");
		retsql.append(" and ( "+id+" in (select codeitemid from organization where codesetid = 'UN'))");
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			retsql.append(" and "+id+" like '"+priv+"%' ");
		}	
		return retsql.toString();
	}
	/**
	 * 统计项目，数据更新
	 * @param subsetstr
	 * @param systime
	 * @param tablealias
	 * @param id
	 * @param equalz0time
	 * @param countsqlsb
	 * @param field
	 * @return
	 */
	public String conupdate(String subsetstr,String systime, String tablealias,
			String id ,String equalz0time ,StringBuffer countsqlsb,String field)
	{
		StringBuffer conupdatesql = new StringBuffer();
		conupdatesql.append(" update "+subsetstr+ " set "+field+"=");
		conupdatesql.append("(select t."+field);
		conupdatesql.append(" from "+subsetstr+" "+tablealias);
		conupdatesql.append(countsqlsb.toString());
		conupdatesql.append(" where "+tablealias+"."+id+"=t."+id);
		conupdatesql.append(" and "+equalz0time);
		conupdatesql.append(" and "+subsetstr+"."+id+"="+tablealias+"."+id);
		conupdatesql.append(" group by "+tablealias+"."+id);
		conupdatesql.append(", t."+field+") ");
		conupdatesql.append(",ModTime = "+systime);
		conupdatesql.append(",ModUserName='"+uv.getUserName()+"'");
		conupdatesql.append(" where "+equalz0time);
		String priv = "";
		if(!this.uv.isSuper_admin())
		{
			priv = this.uv.getManagePrivCodeValue();
			conupdatesql.append(" and "+id+" like '"+priv+"%' ");
		}
		return conupdatesql.toString();
	}
	/**
	 * 
	 * @param sumconformulatempsb
	 * @param countsbstr
	 * @param conformulastr
	 * @param conformulatabstr
	 * @param privsql
	 * @param conwherestr
	 * @param congroupbystr
	 * @param id
	 * @return
	 */
	public StringBuffer consingle(StringBuffer sumconformulatempsb,String countsbstr,String conformulastr,String conformulatabstr,String privsql,String conwherestr,String congroupbystr,String id)
	{
		sumconformulatempsb.append(",( select "+countsbstr+conformulastr+" from "+conformulatabstr);
		sumconformulatempsb.append(" ( select "+dbname+"A01.A0100 "+privsql+" ) x");
		sumconformulatempsb.append(" where "+ conwherestr);
		sumconformulatempsb.append(" group by "+congroupbystr+") s");
		if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
            sumconformulatempsb.append(" where a.A0100 = s.A0100 and "+id+" is not null ");
        } else {
            sumconformulatempsb.append(" where a.A0100 = s.A0100 and "+id+" not like '' ");
        }
		sumconformulatempsb.append(" group by a."+id);
		return sumconformulatempsb;
	}
	public StringBuffer firstconsingle( int s,String conformula,String conformulastr,String conformulatabstr,String privsql,String conwherestr,String congroupbystr,String id)
	{
		StringBuffer retsb = new StringBuffer();
		retsb.append(" left join ( select "+Sql_switcher.sqlNull(conformula,(float) 0.0)+" as "+conformula+conformulastr+" from "+conformulatabstr);
		retsb.append(" ( select "+dbname+"A01.A0100 "+privsql+" ) x");
		retsb.append(" where "+ conwherestr);
		retsb.append(" group by "+congroupbystr+") m"+s);
		retsb.append(" on "+dbname+"a01.A0100 =m"+s+".A0100");
		return retsb;
	}
	
	public StringBuffer secondconsingle( int s,String dbnamestr,String conformula,String conformulastr,String conformulatabstr,String privsql,String conwherestr,String congroupbystr,String id)
	{
		StringBuffer retsb = new StringBuffer();
		retsb.append(" left join ( select "+Sql_switcher.sqlNull(conformula,(float) 0.0)+" as "+conformula+conformulastr+" from "+conformulatabstr);
		retsb.append(" ( select "+dbnamestr+"A01.A0100 "+privsql+" ) x");
		retsb.append(" where "+ conwherestr);
		retsb.append(" group by "+congroupbystr+") m"+s);
		retsb.append(" on "+dbnamestr+"A01.A0100 =m"+s+".A0100");
		return retsb;
	}
	
	public String getjointable(String dbnamestr ,String FSQL,String field,StringBuffer jointablesb)
	{
		String ret = "";
		ret = " ,(select "+dbnamestr+"A01.A0100,"+FSQL+" as "+field+" from "+dbnamestr+"A01 "+jointablesb.toString();
		return ret;
	}
	
	/**
	 * 
	 * @param sumconformulatempsb
	 * @param countsbstr
	 * @param conformulastr
	 * @param conformulatabstr
	 * @param privsql
	 * @param conwherestr
	 * @param congroupbystr
	 * @return
	 */
	public StringBuffer consingletwo(StringBuffer sumconformulatempsb,String countsbstr,String conformulastr,String conformulatabstr,String privsql,String conwherestr,String congroupbystr)
	{
		sumconformulatempsb.append(",( select "+countsbstr+conformulastr+" from "+conformulatabstr);
		sumconformulatempsb.append(" ( select "+dbname+"A01.A0100 "+privsql+" ) x");
		sumconformulatempsb.append(" where "+ conwherestr);
		sumconformulatempsb.append(" group by "+congroupbystr+") s");
		if(Sql_switcher.searchDbServer()== Constant.ORACEL) {
            sumconformulatempsb.append(" where a.A0100 = s.A0100 and a.E0122 is not null ");
        } else {
            sumconformulatempsb.append(" where a.A0100 = s.A0100 and a.E0122 not like '' ");
        }
		sumconformulatempsb.append(" group by a.E0122");
		return sumconformulatempsb;
	}
	/**
	 * 统计项目，数据更新最终SQL
	 * @param c
	 * @param judgestr
	 * @param subsetstr
	 * @param id
	 * @param getz0
	 * @param time
	 * @param systime
	 * @param insertz0time
	 * @param supertable
	 * @param tablealias
	 * @param field
	 * @param equalz0time
	 * @param countsqlsb
	 * @param contentsb
	 */
	public void getconsql(int c,String judgestr,String subsetstr,String id,String getz0,String time,
			String systime,String insertz0time,String supertable,String tablealias,String field,
			String equalz0time,StringBuffer countsqlsb,String equalz0Counttime, String dbname)
	{
		String consql = "";
		try
		{
			consql = this.conupdate( subsetstr, systime,  tablealias, id , equalz0time , countsqlsb, field);
			FieldItem fielditem = DataDictionary.getFieldItem(field);
//			System.out.println(consql);
			if(fielditem!=null&& "1".equals(fielditem.getUseflag())){
				dao.update(consql);		
				if(c>0)
				{
					if("b".equalsIgnoreCase(subsetstr.substring(0,1)))
					{
						String[] dbnamearray ;
						int index  = dbname.split(",").length; 
						StringBuffer onetemp = new StringBuffer();
						StringBuffer twotemp = new StringBuffer();
						StringBuffer threetemp = new StringBuffer();
						String dbnamestr = "";
						
						if(index>1) // 有多个库前缀
						{
							dbnamearray = dbname.split(",");
							for(int t=0;t<index;t++)
							{
								dbnamestr = dbnamearray[t].toString();
								onetemp.append(" or b."+id+" in (select "+id+" from "+dbnamestr+"A01 ");
								onetemp.append(" where ((b."+id+"="+id+") or (b."+id+"=E0122)))");
								twotemp.append(" or "+id+"  in (select E0122 from "+dbnamestr+"A01)");
								threetemp.append(" or "+id+" in (select "+id+" from "+dbnamestr+"A01)");
							}
							consql = this.getcontentsql( subsetstr, field, id, equalz0time,equalz0Counttime,twotemp.substring(3).toString()); 
							dao.update(consql);
							consql = this.getcontent( subsetstr, field, id, equalz0time,equalz0Counttime,onetemp.substring(3).toString(),threetemp.substring(3).toString()); 
							dao.update(consql);
						}else
						{
							dbnamestr = dbname;
							onetemp.append("  b."+id+" in (select "+id+" from "+dbnamestr+"A01 ");
							onetemp.append(" where ((b."+id+"="+id+") or (b."+id+"=E0122)))");
							twotemp.append("  "+id+"  in (select E0122 from "+dbnamestr+"A01)");
							threetemp.append(" "+id+" in (select "+id+" from "+dbnamestr+"A01)");
							consql = this.getcontentsql( subsetstr, field, id, equalz0time,equalz0Counttime,twotemp.toString()); 
//							System.out.println(consql);
							dao.update(consql);
							consql = this.getcontent( subsetstr, field, id, equalz0time,equalz0Counttime,onetemp.toString(),threetemp.toString()); 
//							System.out.println(consql);
							dao.update(consql);
							
						}
					}
					
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	
	
	 /**
     * 获取部门等级
     * @param dao
     * @return list
     * @throws GeneralException
     */
	public ArrayList getLevel(){
		ArrayList list = new ArrayList();
		String sql="select grade from organization where (codesetid ='UN'or codesetid='UM') and "+Sql_switcher.length("grade")+">0 GROUP BY grade ORDER BY grade";
		ArrayList dylist = null;
		String[] arr = {"一级","二级","三级","四级","五级","六级","七级","八级","九级","十级","十一级","十二级","十三级","十四级","十五级"};
		try {
			dylist = dao.searchDynaList(sql);
			int i=0;
			for(Iterator it=dylist.iterator();it.hasNext();){
				DynaBean dynabean=(DynaBean)it.next();
				CommonData obj=new CommonData(dynabean.get("grade").toString(),arr[i]);
				list.add(obj);
				i++;
			}
			CommonData obj1=new CommonData("0","全部");
			list.add(obj1);
		}catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		list.remove(list.size()-2);
		return list;
	}
	 /**
     * 获取部门等级
     * @param dao
     * @return list
     * @throws GeneralException
     */
	public ArrayList getPosLevel(){
		ArrayList list = new ArrayList();
//		String sql="select grade from organization where codesetid ='@K' GROUP BY grade";
//		ArrayList dylist = null;
//		String[] arr = {"一级","二级","三级","四级","五级","六级","七级","八级","九级","十级"};
		try {
//			dylist = dao.searchDynaList(sql);
//			int i=0;
//			for(Iterator it=dylist.iterator();it.hasNext();){
//				DynaBean dynabean=(DynaBean)it.next();
//				CommonData obj=new CommonData(dynabean.get("grade").toString(),arr[i]);
//				list.add(obj);
//				i++;
//			}
			CommonData obj1=new CommonData("0","全部");
			list.add(obj1);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		list.remove(list.size()-2);
		return list;
	}
	 /**
     * 获取部门等级
     * @param dao
     * @return list
     * @throws GeneralException
     */
	public int maxLevel(){
		int level=0;
		String sql="select max(grade) as grade from organization where codesetid ='UN'or codesetid='UM'";
		RowSet rs = null;
		try {
			rs = dao.search(sql);
			while(rs.next()){
				level = rs.getInt("grade");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return level;
	}
	/**
	 * 获得等级sql
	 * @param grade 等级
	 * @return
	 */
	public String gradeSql(String grade){
		String gradestr = "";
		int n=Integer.parseInt(grade);
		for(int i=1;i<=n;i++){
			if(i==n){
				gradestr += i;
			}else{
				gradestr += i+",";
			}
		}
		return "("+gradestr+")";
	}
	/**
	 * 得到前台sql的where条件
	 * @param itemid  子集名称
	 * @param areavalue  部门范围
	 * @return
	 * @throws GeneralException
	 */
	public String getdiwheresql(String areavalue,String grade) throws GeneralException
	{
		StringBuffer wherestr = new StringBuffer();
		String depstr = "";
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag);
		String id="";
		if("1".equals(changeflag)){
			id+=getyear;
			if(Integer.parseInt(getmonth)>9) {
                id+="-"+getmonth;
            } else {
                id+="-0"+getmonth;
            }
		}else if("2".equals(changeflag)){
			id+=getyear;
		}
		if(areavalue!=null&&areavalue.trim().length()>0){
			depstr = "  codeitemid like '"+areavalue+"%'";
		}else{
			if(!uv.isSuper_admin()&&!"1".equals(uv.getGroupId()))
			{
				/**
				 * cmq changed at 20121003 for 单位和岗位权限范围控制规则
				 */
				if("1".equals(this.module)) {
					depstr = " codeitemid like '" + this.uv.getManagePrivCodeValue() + "%'";
	        	} else {
					String managepriv =uv.getUnitPosWhereByPriv("codeitemid") ;
					if(managepriv!=null&&managepriv.trim().length()>0) {
						depstr=managepriv;
					} else {
                        depstr="1=2";
                    }
	        	}
			}
		}
		wherestr.append(" from "+subsetstr+" a where "+equalz0time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = sdf.format(new Date());
		if(!"K".equalsIgnoreCase(subsetstr.substring(0,1))){
			wherestr.append(" and B0110 in (select codeitemid  from organization where 1=1 ");	
			wherestr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			if(depstr!=null&&depstr.trim().length()>0){
				wherestr.append(" and "+depstr);
			}
			if(grade!=null&&grade.trim().length()>0&&!"0".equals(grade)){
				wherestr.append(" and "+Sql_switcher.isnull("grade", "100")+"<="+grade);
			}		
			wherestr.append(")");
			if(id!=null&&id.trim().length()>0){
				wherestr.append(" and (Id='");
				wherestr.append(id+"' or Id='");
				wherestr.append(id.replaceAll("-",".")+"')");
			}	
			/*wherestr.append(" and I9999=(select max(I9999) from ");//xuj 2010-5-2 显示一月多次
			wherestr.append(subsetstr);
			wherestr.append(" where B0110=a.B0110 ");
			if(id!=null&&id.trim().length()>0){
				wherestr.append(" and (Id='");
				wherestr.append(id+"' or Id='");
				wherestr.append(id.replaceAll("-",".")+"')");
			}
			wherestr.append(" and ");
			wherestr.append(equalz0time);
			wherestr.append(" ) and B0110 like '"+this.uv.getManagePrivCodeValue()+"%'");*/
			/**
			 * cmq changed at 20121003 for 单位和岗位范围控制
			 * 业务范围-操作单位-人员范围
			 */
			//wherestr.append(" and B0110 like '"+this.uv.getManagePrivCodeValue()+"%'");
			wherestr.append(" and "+this.uv.getUnitPosWhereByPriv("B0110"));
			wherestr.append(" order by a0000");	
		}else{
			wherestr.append(" and E01A1 in (select codeitemid  from organization where 1=1");
			wherestr.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date ");
			if(depstr!=null&&depstr.trim().length()>0){
				wherestr.append(" and "+depstr);
			}
			if(grade!=null&&grade.trim().length()>0&&!"0".equals(grade)){
				wherestr.append(" and "+Sql_switcher.isnull("grade", "100")+"<="+grade);
			}wherestr.append(")");
			if(id!=null&&id.trim().length()>0){
				wherestr.append(" and (Id='");
				wherestr.append(id+"' or Id='");
				wherestr.append(id.replaceAll("-",".")+"')");
			}	
			/*wherestr.append(" and I9999=(select max(I9999) from ");//xuj 2010-5-2 显示一月多次
			wherestr.append(subsetstr);
			wherestr.append(" where E01A1=a.E01A1 ");
			if(id!=null&&id.trim().length()>0){
				wherestr.append(" and (Id='");
				wherestr.append(id+"' or Id='");
				wherestr.append(id.replaceAll("-",".")+"')");
			}
			wherestr.append(" and ");
			wherestr.append(equalz0time);
			wherestr.append(" ) and E01A1 like '"+this.uv.getManagePrivCodeValue()+"%'");
			*/
			/**
			 * cmq changed at 20121003 for 单位和岗位范围控制
			 * 业务范围-操作单位-人员范围
			 */			
			//wherestr.append(" and E01A1 like '"+this.uv.getManagePrivCodeValue()+"%'");
			wherestr.append(" and "+this.uv.getUnitPosWhereByPriv("E01A1"));
			wherestr.append(" order by E01A1");	
		}
		
		
		return wherestr.toString();
	}
	/**
	 * 得到前台要显示的列
	 * @param subsetstr
	 * @param viewhide
	 * @return
	 */
	public String getdiselelctsql(String viewhide)
	{
		StringBuffer selectstr = new StringBuffer("select ");
		selectstr.append(viewhide);
		if("K".equalsIgnoreCase(subsetstr.substring(0,1).trim())){
			selectstr.append(",(select max(grade) from organization where codeitemid = a.E01A1) as grade,(select a0000 from organization where codeitemid = a.E01A1) as a0000");
		}else{
			selectstr.append(",(select max(grade) from organization where codeitemid = a.B0110) as grade,(select a0000 from organization where codeitemid = a.B0110) as a0000");	
		}
		return selectstr.toString();
	}
	/**
	 * 得到时间条件
	 * @param subsetstr  子集名称
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getequalz0time(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getequalz0time = "";
		TimeScope ts = new TimeScope();
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			time = getyear;
		}
		else
		{
			time = getyear+"-"+getmonth;
		}
		getequalz0time = ts.getTimeCond(subsetstr+"Z0","=",time);
		return getequalz0time;
	}
	/**
	 * 得到时间
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getz0(String getyear,String getmonth,String changeflag)
	{
		String getz0 = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			getz0 = getyear + "-01-01";
		}
		else
		{
			getz0 = getyear+"-"+getmonth+"-01";
		}
		getz0 = Sql_switcher.dateValue(getz0);
		return getz0;
	}
	/**
	 * 获得年月标识时间
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 * @return
	 */
	public String gettime(String getyear,String getmonth,String changeflag)
	{
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			 time = getyear + "";
		}
		else
		{
			 time = getyear+"."+getmonth;
		}	
		return time;
	}
	/**
	 * 得到时间段条件
	 * @param subsetstr  子集名称
	 * @param getyear    年
	 * @param getmonth   月
	 * @param changeflag changeflag
	 * @return
	 */
	public String getz0time(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getz0time = "";
		TimeScope ts = new TimeScope();
		String startime = "";
		String endtime = "";
		int inputmonth = Integer.parseInt(getmonth);
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		// 1 是 月，2 是 年
		if("2".equals(changeflag))
		{
			startime = getyear+"-01-01";
			endtime = getyear+"-12-31";
		}
		else
		{
			startime = getyear+"-"+getmonth+"-01";
			endtime = getyear+"-"+getmonth+"-31";
		}
		getz0time = ts.gettimeScope(subsetstr+"z0",startime,endtime);
		return getz0time;
	}	

	

	/**
	 * 载入上期数据
	 * @param subsetstr
	 * @param dao
	 * @param str
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 */
	public int loadPrevData(String fieldstr,String getyear,String getmonth,String changeflag)
	{
		int num = 0;
		String[] fields = fieldstr.split(",");
		String field = "";
		String id = "";
		String time = this.getprevtime( subsetstr, getyear, getmonth, changeflag);
		String timecondition = getequalz0time( subsetstr, getyear, getmonth, changeflag);
		if("B".equalsIgnoreCase(subsetstr.substring(0,1))){
			id = "B0110";        // 单位表的连接字段
		}
		else if("K".equalsIgnoreCase(subsetstr.substring(0,1))){
			id = "E01A1";         // 职位表的连接字段
		}
		for(int i=0;i<fields.length;i++)
		{
			field = fields[i];
			StringBuffer loadsql = new StringBuffer();
			loadsql.append(" Update "+subsetstr+" set "+field+"=(");
			loadsql.append(" select "+field+" from "+subsetstr+" t ");
			loadsql.append(" where "+time+" and "+subsetstr+"."+id+"=" );
			loadsql.append("t."+id+" and I9999=(select max(I9999) from ");
			loadsql.append(subsetstr+" where "+id+"=t."+id+" and "+time+")) where "+timecondition);
			if(!this.uv.isSuper_admin()) {
                loadsql.append(" and "+id+" like '"+this.uv.getManagePrivCodeValue()+"%'");
            }
			try
			{
				num = dao.update(loadsql.toString());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}	
			if(num==0){
				break;
			}
		}
		return num;
	}
	/**
	 * 载入上期数据时间
	 * @param subsetstr
	 * @param getyear
	 * @param getmonth
	 * @param changeflag
	 * @return
	 */
	public String getprevtime(String subsetstr,String getyear,String getmonth,String changeflag)
	{
		String getequalz0time = "";
		TimeScope ts = new TimeScope();
		String time = "";
		int inputmonth = Integer.parseInt(getmonth);
		int prevmonth = inputmonth-1;
		if(inputmonth!=0 && inputmonth<10)
		{
			getmonth = "0"+inputmonth;
		}
		if("2".equals(changeflag))
		{
			time = Integer.parseInt(getyear)-1+"";
		}
		else
		{
			if(Integer.parseInt(getmonth)>1)
			{
				if(prevmonth!=0 && prevmonth<10){
					time = getyear+"-0"+prevmonth;
				}else{
					time = getyear+"-"+prevmonth;
				}
			}
			else
			{
				time = Integer.parseInt(getyear)-1+"-12";
			}
		}
		getequalz0time = ts.getTimeCond(subsetstr+"Z0","=",time);
		return getequalz0time;
	}
	
	
	public String  getconsqlmain(String c_expr,String varType,UserView uv,String time)
	{
		String  temp = "";
		try{
			if(c_expr.trim().length()>0){
				ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
				YksjParser yp = new YksjParser(uv,alUsedFields,YksjParser.forNormal,getColumType(varType),3,"","");
				yp.run(c_expr);
				temp = yp.getSQL();//公式的结果
			}
		}catch(Exception ex){
			 ex.printStackTrace();
		}
		StringBuffer  buf = new StringBuffer();
		StringBuffer where = new StringBuffer();
		StringBuffer codeset = new StringBuffer();
		String[] conformula = temp.split(","); 
		HashSet set = new HashSet();
		if(conformula.length>1){
			buf.append("select ");
			buf.append(temp +" from ");

			for(int i=0;i<conformula.length-1;i++){
				int templast = conformula[i].lastIndexOf("(")+1;
				String itemid = conformula[i].substring(templast,conformula[i].length());//得到e5809
				FieldItem fi = DataDictionary.getFieldItem(itemid);
				String fieldsetid = fi.getFieldsetid();
				
				if("a".equals(fieldsetid.substring(0,1).toLowerCase())){
					set.add(("usr"+fieldsetid));
					codeset.append("usr"+fieldsetid.toUpperCase()+".A0100='x.A0100'");
					if(!"A01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid.toUpperCase()+".I9999=(select max(I9999) from ");
							where.append(fieldsetid.toUpperCase());
							where.append(" where ");
							where.append(" Usr"+fieldsetid.toUpperCase()+".A0100='x.A0100')");
						}
					}
				}else if("b".equals(fieldsetid.substring(0,1).toLowerCase())){
					set.add((fieldsetid));
					codeset.append(fieldsetid+".B0110='x.B0110'");
					if(!"b01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid+".I9999=(select max(I9999) from ");
							where.append(fieldsetid.toUpperCase());
							where.append(" where ");
							where.append(fieldsetid+".B0110='x.B0110')");
						}
					}
				}else{
					set.add((fieldsetid)); 
					codeset.append(fieldsetid+".E01A1='x.E01A1'");
					if(!"k01".equalsIgnoreCase(fieldsetid)){
						if(isConf(fieldsetid)){
							where.append(getz0time(fieldsetid,time));
						}else{
							where.append(fieldsetid.toUpperCase()+".I9999=(select max(I9999) from ");
							where.append(fieldsetid.toUpperCase());
							where.append(" where ");
							where.append(fieldsetid+".E01A1='x.E01A1')");
						}
					}
				}
				
				if(i+1<conformula.length-1){
					codeset.append(" and ");
				}
			}
			buf.append(set.toString().substring(1,set.toString().length()-1));
			
			if(conformula.length>2){
				if(where.length()>0){
					buf.append(" where "+where.toString()+" and "+codeset);
				}else{
					buf.append(" where "+where.toString()+codeset);
				}
			}else{
				if(where.length()>0){
					buf.append(" where "+where.toString()+" and "+codeset);
				}else{
					buf.append(" where "+codeset);
				}
			}
		}else{
			buf.append(temp);
		}
		return buf.toString() ;
	}
	/**
	 * 判断子集里面是否有年月标识这个指标
	 * @param  fieldsetid 子集id
	 * @return boolean 
	 **/
	public boolean isConf(String fieldsetid){
		boolean  buf = false;
		ArrayList list = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++){
			FieldItem fi = (FieldItem)list.get(i);
			if(fi.getItemid().equalsIgnoreCase(fieldsetid+"z0")){
				buf=true;
				break;
			}
		}
		return buf ;
	}
	/**
	 * 得到时间段条件
	 * @param subsetstr  子集名称
	 * @param time    时间
	 * @return
	 */
	public String getz0time(String subsetstr,String time)
	{
		TimeScope ts = new TimeScope();
		String getz0time = ts.gettimeScope(subsetstr+"Z0",time+"-01",time+"-31");
		return getz0time;
	}
	/**
	 * 设置Field的数据类型
	 * @param type  数据类型
	 * @param decimalwidth 小数点后面值的宽度
	 * @return int 
	 **/
	public int getColumType(String type){
		int temp=1;
		if("A".equals(type)){
			temp=DataType.STRING;
		}else if("D".equals(type)){
			temp=DataType.DATE;
		}else if("N".equals(type)){
			temp=DataType.FLOAT;
		}else if("M".equals(type)){
			temp=DataType.CLOB;
		}else{
			temp=DataType.STRING;
		}
		return temp;
	}
	
	/**
	 * 统计项目
	 */
	public void consql()
	{
		RowSet rs = null;
		String getz0 = this.getz0(getyear,getmonth,changeflag);   //得到时间
		String time = this.gettime(getyear,getmonth,changeflag);  //获得年月标识时间
		String systime = Sql_switcher.sqlNow();  //  获得系统时间
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag); 
		String equalz0Counttime = this.getequalz0time("a."+subsetstr,getyear,getmonth,changeflag); 
//		String equalz0Counttimetwo = this.getequalz0time("b."+subsetstr,getyear,getmonth,changeflag);
		String insertz0time = this.getequalz0time(subsetstr+"."+subsetstr,getyear,getmonth,changeflag);
		String tablealias = this.tableaLias(subsetstr); // 表别名   eg: B04 b
		String supertable = this.superTable(subsetstr);	// 子表的主表
		try{
			 
			 ArrayList itemidlist =new ArrayList();
			 String temp = new String("");       // 组成统计条件,给getPrivSQLExpression方法
			 String id = this.id(subsetstr);         // 表的标识  eg: B04的B0110列
			 String[] expr;          			 // 获得用"|"分段截出的各个部分
			 String privsql = "";    // 通过getPrivSQLExpression方法生成的from后面的sql    
			 String typestr = "";    // 操作类型 1, 计算项目  2,统计项目  3,汇总项目
			 String operstr = "";    // 操作类型   0，求个数 1，求和  2，最小值  3，最大值  4，平均值
			 String field = "";      // 指标 eg: b0430
			 String displayid = "";  // 在fielditem表里,用类标识子集
			 String conformula = ""; // 统计公式字段
			 String contable = "";   // 统计公式所用的表
			 String c_expr = "";     // 中文字符串
			 String fieldtype = "";  // 参数类型
			 String FSQL = "";       // 获得将中文转化成的字段
			 String firstpart = "";  // 获得用"|"分段截出的第一个部分
			 String subclass = "";   // 获得expression里,包含的下级
			 String countsbstr = ""; // 统计项目里,中文转化成的字段
			 String controlz0 = "";  // 给getz0time方法的参数
			 String conformulastr = "";      // 统计项目里,获得所要查询的人员表的A0100字段			 
			 String conformulatabstr = "";   // 统计项目里,获得所要查询的人员表
			 String congroupbystr = "";      // 统计项目里,统计公式需要的groupby的字段
			 String conwherestr = "";        // 统计项目里,统计公式需要的where的字段			 
			 		
			 int y = 0;             //  控制统计项目循环
			 int fieldsplitnum = 0; //  控制对中文字符处理的循环
			 
			
			 itemidlist = this.getSqllist();
			// 获得给处理中文字符的解析器的参数			
			ArrayList alUsedFields = new ArrayList();
			int varType =6; // float
			//  删除原有记录
				
			//	判断该表是否有记录
			String judgestr = this.judgeSql(subsetstr);	
			//	取出fielditem表的expression字段，进行解析，并形成sql语句的大循环
			String insertsql ="";
			if("inserts".equals(judgestr)){
				insertsql = this.inserts( subsetstr, id, getz0, time, systime, insertz0time, supertable);
//				System.out.println(insertsql);
				dao.update(insertsql);						
			}						
			else if("insert".equals(judgestr)){
				this.deleteSql(subsetstr,getyear,getmonth,changeflag,"2");		
				insertsql = this.insert( subsetstr, id, getz0, time, systime, insertz0time, supertable);
//				System.out.println(insertsql);
				dao.update(insertsql);
			}
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				ArrayList fieldlist =new ArrayList();
				int c = 0;
				int fieldnum = 0;
				boolean congo = true;
				alUsedFields = this.getalUsedFields(subsetstr,alUsedFields);

				StringBuffer sb = new StringBuffer();     //  获得处理"查询条件"的方法（getPrivSQLExpression）的expression参数
				DynaBean dynabean=(DynaBean)it.next();
				temp = dynabean.get("expression").toString();
				if("2".equals(temp.substring(0,1)))
				{
					field = dynabean.get("expression").toString();
					displayid = dynabean.get("displayid").toString();
					
					//  获得itemid，itemtype
					StringBuffer fieldsql = new StringBuffer();
					fieldsql.append("select itemid,itemtype from fielditem where  fieldsetid = '");
					fieldsql.append(subsetstr);
					fieldsql.append("' and displayid = '");
					fieldsql.append(displayid);
					fieldsql.append("' and useflag='1' and itemtype='N' and itemid not in('");
					fieldsql.append(subsetstr);
					fieldsql.append("Z0','");
					fieldsql.append(subsetstr);
					fieldsql.append("Z1')");
//					System.out.println(fieldsql);
					rs = dao.search(fieldsql.toString());
					if(rs.next()){
						field = rs.getString("itemid").toString();
						fieldtype = rs.getString("itemtype").toString();
					}
					varType = this.getvarType(fieldtype,varType);
//				  先分三大段截取expression字段
					if(temp.length()>0)
					{
						expr = temp.split("\\|");
						int j = expr.length;
						if(j>0)
						{
							for(int k=1;k<expr.length+1;k++)
							{
								if(k==1)  // 第一段
								{
									firstpart = expr[0].toString();  
									int splitnum = firstpart.split("::").length;
									if(splitnum>0)
									{
										for(int x=1;x<splitnum+1;x++)
										{
											if(x==1){
												// 获得要进行的操作 eg: 统计项目，计算项目，汇总项目
												typestr = firstpart.split("::")[0]; 
											}
											else if(x==2){
												// 获得要进行的操作 eg: 求个数，求和 等等
												operstr = firstpart.split("::")[1]; 
												operstr = this.getoperstr(operstr);
											}
											else if(x==3)
											{
												c_expr = firstpart.split("::")[2].toString();  // 获得公式
												c_expr=c_expr.trim(); 
												if(!(c_expr==null || "".equals(c_expr)) )
												{
											        // 如果是统计项目，要找出需要操作的相应人员表  eg: 对岗位工资求和，要用usra58表
											        if(typestr == "2" || "2".equals(typestr))
											        {
											        	alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.EMPLOY_FIELD_SET);
											        	FSQL = this.getFQL( uv, alUsedFields, varType, 0, c_expr, FSQL);
														congo = this.getContrlFQL( uv, alUsedFields, varType, 0, c_expr, FSQL, congo);
											        }   
												}
											}								
										}
									}
								}
								if(k==2) // 第二段
								{
									temp = this.getExpr(expr,temp);  // 获得 expr
								}
								if(k==3)
								{
//									sb = this.getExpression(expr, sb, temp, subclass);  //  获得 expression 和 subclass
									subclass = this.getSubClass(expr, sb, temp, subclass);
								}
							}
						}
						else
						{ 
							temp=""; //  如果是汇总项目，查询条件就为空
						}
					}
//					Field fis = DataDictionary.getFieldItem(field).cloneField();
					fieldlist.add(field);
					String expression = sb.toString();
					if("2".equals(typestr))
					{
						StringBuffer countsqlsb = new StringBuffer();      // 统计项目里,update子集表的sql	
						StringBuffer conditionsb = new StringBuffer();  // 统计项目里,子集求个数的sql
						StringBuffer sumconformulasb = new StringBuffer(); // 统计项目里,多个库前缀,子集求和,求最大,最小,平均值的sql
						StringBuffer conditiontempsb = new StringBuffer(); // 统计项目里,多个库前缀,子集求个数的sql
						StringBuffer sumconformulatempsb = new StringBuffer();	// 统计项目里,一个库前缀,子集求和,求最大值数的sql
						StringBuffer jointablesb = new StringBuffer();
						StringBuffer jointablesbtemp = new StringBuffer();
						String jointemp = "";
						StringBuffer jointablesbtwotemp = new StringBuffer();
						StringBuffer contentsb = new StringBuffer();
						StringBuffer fieldtemp =  new StringBuffer();
						if(congo)
						{						
							if("1".equals(subclass))
							{
								c++;
//								contentsb.append(this.getcontent( subsetstr, field, equalz0time));
							}
							if("count".equals(operstr))  // 统计项目里的求个数
							{
								String[] dbnamearray ;
								int index  = dbname.split(",").length; 
								if(index>1) // 有多个库前缀
								{
									dbnamearray = dbname.split(",");
									for(int t=0;t<index;t++){
										String dbnamestr = dbnamearray[t].toString();
										privsql =uv.getPrivSQLExpression(expression,dbnamestr,false,fieldlist);
										//  统计项目，求个数，多个库前缀，
										conditiontempsb = this.conselectsingle(subsetstr, t, index, conditiontempsb, field, id, privsql, expression);
//										System.out.println(conditiontempsb.toString());	
									}
									countsqlsb = this.conselectedbs ( y, conditionsb, countsqlsb, field, conditiontempsb, id);
//									System.out.println(countsqlsb.toString());							
								}
								else // 只有一个库前缀
								{
									privsql =uv.getPrivSQLExpression(expression,dbname,false,fieldlist);
									String fieldcondition = " ";
									String fieldconditiontwo = " ";
									if(expression == null || "".equals(expression)){
										
										fieldcondition = " ";
										if(this.uv.isSuper_admin()) {
                                            fieldconditiontwo = " where e0122 not in (select codeitemId from  organization where codesetid = 'UN')";
                                        } else {
                                            fieldconditiontwo = " and e0122 not in (select codeitemId from  organization where codesetid = 'UN')";
                                        }
									}
									else{
										
										if(Sql_switcher.searchDbServer()== Constant.ORACEL)
										{
											fieldcondition = " and "+id+" is not null ";
											fieldconditiontwo = " and e0122 is not null ";
										}else
										{
											fieldcondition = " and "+id+" not like '' ";
											fieldconditiontwo = " and e0122 not like '' and e0122 not in (select codeitemId from  organization where codesetid = 'UN') ";
										}
											
											
									}
//								  统计项目，求个数，一个库前缀，
									countsqlsb = this.conselectedb ( subsetstr , conditionsb , field, id, fieldcondition, fieldconditiontwo, privsql);
//									System.out.println(countsqlsb.toString());
								}													
							}
							else  // 统计项目里的 求和,求最大,最小,平均值
							{
								int controlcon = 0;
								countsbstr = FSQL+" as "+field;
								String z0timevalue = "";
					        	fieldsplitnum = FSQL.split(",").length;
					        	 //  处理多个库前缀
					        	String[] dbnamearray =null;
			    				int index  = dbname.split("\\,").length; 
			    				String dbnamestr = ""; 
								if(index>1)   // 有多个库前缀
								{
									dbnamearray = dbname.split("\\,");
			    					for(int t=0;t<index;t++)
			    					{
			    						int fieldnums  = 0;
			    						dbnamestr = dbnamearray[t].toString();
			    						StringBuffer jointempsb = new StringBuffer();
			    						privsql =uv.getPrivSQLExpression(expression,dbnamestr,false,fieldlist);
			    						if(fieldsplitnum>1) //有一个或者多个中文字符
							        	{
			    							for(int s=0;s<fieldsplitnum-1;s++) //对有多个库前缀，多个中文字符的处理	
							        		{
			    								conformula = FSQL.split(",")[s].toString(); // 用","分开isnull(e5809,0.0),									        			
							        			int templast = conformula.lastIndexOf("(")+1;
							        			conformula = conformula.substring(templast,conformula.length());//得到e5809
							        			FieldItem fi = DataDictionary.getFieldItem(conformula);												        			
							        			contable = dbnamestr+fi.getFieldsetid();												        			
				    							controlz0 = fi.getFieldsetid();
				    							FieldSet fs = DataDictionary.getFieldSetVo(fi.getFieldsetid());
				    							String getchangflag = fs.getChangeflag();
				    							
				    							if("0".equalsIgnoreCase(getchangflag))
				    							{
				    								z0timevalue = "";	
				    								if(!"count".equals(operstr))
					    							{
				    									if(contable.endsWith("a01")||contable.endsWith("A01"))
				    									{
				    										z0timevalue = z0timevalue;	
				    									}else{
				    										z0timevalue = z0timevalue+" and "+contable+".I9999=(select max(I9999) from "+contable+" where "+contable+".A0100=x.A0100)";	
				    									}
				    											    							
					    							}
				    							}
				    							else
				    							{
				    								z0timevalue = this.getz0time(controlz0,getyear,getmonth,this.changeflag);
					    							z0timevalue = " and "+z0timevalue;										    							
				    							}
							        			
				    							 congroupbystr=contable+".A0100,"+contable+"."+conformula;
				    							 conwherestr=contable+".A0100=x.A0100  "+z0timevalue+" " ;									    						
				    							 conformulastr=","+contable+".A0100 ";
				    							 conformulatabstr= contable+",";										   
				    		    			
				    							 fieldnums++;	
				    							 controlcon = fieldnums;
				    							 jointablesb = this.secondconsingle(s, dbnamestr,conformula, conformulastr, conformulatabstr, privsql, conwherestr, congroupbystr, id);
				    							 jointempsb.append(jointablesb.toString());
							    				 if(s==fieldsplitnum-2)
							    				 {
							    					 jointemp = this.getjointable( dbnamestr , FSQL, field,jointempsb);
//							    					 System.out.println(jointemp);
							    					 jointablesbtwotemp=condbssinglesthree(t,jointemp, id, operstr, dbnamestr,field);	
//							    					System.out.println(jointablesbtwotemp.toString());
							    					jointablesbtwotemp=condbssinglesfour(t,index,jointablesbtwotemp,jointemp, id, operstr, dbnamestr,field);
							    					countsqlsb.append(jointablesbtwotemp.toString());
//							    					System.out.println(countsqlsb.toString());
							    				 }
							        		}
			    							
							        	}
			    						else  //对多个库前缀，一个中文字符的处理
			    						{
			    							if(fieldsplitnum == 1)   //  如果是个固定数值
			    							{
			    								 contable = dbnamestr+"A01";
			    								 congroupbystr=contable+".A0100 ";
				    							 conwherestr=contable+".A0100=x.A0100 " ;									    								
				    							 conformulastr=","+contable+".A0100 ";
				    							 conformulatabstr= contable+",";								    		    												    							 
			    							}											    							
			    						}
			    						if( fieldnums ==1 || fieldsplitnum ==1 )
			    						{
			    							sumconformulatempsb = this.conselectkinds( id, field, dbnamestr, conformulastr, congroupbystr, conformulatabstr, countsbstr, conwherestr, privsql);
											sumconformulatempsb = this.conselectkindstwo( t, index,  id, field, dbnamestr,  sumconformulatempsb, conformulastr, congroupbystr, conformulatabstr, countsbstr, conwherestr, privsql);
											fieldtemp.append(sumconformulatempsb);
											
			    						}
			    						if(controlcon<2)
			    						{
			    							countsqlsb = conselectedbss( t,index, fieldtemp, id, operstr, field);
			    						}

			    					}
			    					if(controlcon>1)
			    					{
		    							countsqlsb = this.condbssinglesfive( countsqlsb, id, operstr, field);
		    						}	
									
								}
								else  // 只有一个库前缀
								{
									privsql =uv.getPrivSQLExpression(expression,dbname,false,fieldlist);
									dbnamearray = dbname.split("\\,");
			    					dbnamestr = dbnamearray[0].toString();
			    					if(fieldsplitnum>1) //有一个或者多个中文字符
			    					{
			    						for(int s=0;s<fieldsplitnum-1;s++) //对有一个库前缀，多个中文字符的处理
			    						{	
			    							conformula = FSQL.split(",")[s].toString(); // 用","分开isnull(e5809,0.0),									        			
						        			int templast = conformula.lastIndexOf("(")+1;
						        			conformula = conformula.substring(templast,conformula.length());//得到e5809
						        			FieldItem fi = DataDictionary.getFieldItem(conformula);
						        			contable = dbnamestr+fi.getFieldsetid();			
						        			controlz0 = fi.getFieldsetid();
						        			FieldSet fs = DataDictionary.getFieldSetVo(fi.getFieldsetid());
						        			countsbstr = FSQL+" as "+conformula;
			    							String getchangflag = fs.getChangeflag();
			    							if("0".equalsIgnoreCase(getchangflag))
			    							{
			    								z0timevalue = "";		    								
			    								if(!"count".equals(operstr))
				    							{
			    									if(contable.endsWith("a01")||contable.endsWith("A01"))
			    									{
			    										z0timevalue = z0timevalue;
			    									}else{
			    										z0timevalue = z0timevalue+" and "+contable+".I9999=(select max(I9999) from "+contable+" where "+contable+".A0100=x.A0100)";
			    									}		    													
				    							}
			    							}
			    							else
			    							{
			    								z0timevalue = this.getz0time(controlz0,getyear,getmonth,this.changeflag);
				    							z0timevalue = " and "+z0timevalue;									    							
			    							}
		    								congroupbystr=contable+".A0100,"+contable+"."+conformula;
				    						conwherestr=contable+".A0100=x.A0100  "+z0timevalue;
				    						conformulastr=","+contable+".A0100 ";
				    						conformulatabstr=contable+",";
					    					
					    					fieldnum++;	
					    					jointablesb = this.firstconsingle(s, conformula, conformulastr, conformulatabstr, privsql, conwherestr, congroupbystr, id);
					    					jointablesbtemp.append(jointablesb.toString());
			    						}
			    						jointemp = this.getjointable( dbnamestr , FSQL, field,jointablesbtemp);
			    						countsqlsb=condbssingles(jointemp, id, operstr, dbname,field);
			    						countsqlsb=condbssinglestwo(countsqlsb,jointemp, id, operstr, dbname,field);
			    						
			    					}
			    					else  //  对一个库前缀，一个中文字符的处理
			    					{
			    						if(fieldsplitnum == 1)  //  如果是个固定数值
			    						{
			    							contable = dbname+"A01";
			    							congroupbystr=contable+".A0100 ";
				    						conwherestr=contable+".A0100=x.A0100 ";
				    						conformulastr=","+contable+".A0100 ";
				    						conformulatabstr=contable+",";								    	    											        												    	    								
			    						}
			    						 
				    				}
									if( (fieldnum ==1 || fieldsplitnum==1))
									{
										FSQL=FSQL!=null&&FSQL.trim().length()>0?FSQL:field;
										countsbstr = FSQL+" as "+field;
										sumconformulatempsb = this.consingle( sumconformulatempsb, countsbstr, conformulastr, conformulatabstr, privsql, conwherestr, congroupbystr, id);
										countsqlsb = this.condbssingle(sumconformulasb, sumconformulatempsb, id, operstr, dbname,field);
//										System.out.println(countsqlsb.toString());
										StringBuffer tempsb = new StringBuffer(" ");
										sumconformulatempsb = tempsb;
										sumconformulatempsb = this.consingletwo( sumconformulatempsb, countsbstr, conformulastr, conformulatabstr, privsql, conwherestr, congroupbystr);
										countsqlsb = this.condbssingletwo(sumconformulatempsb, id, operstr, dbname,countsqlsb,field);
//										System.out.println(countsqlsb.toString());
									}
									
								}							
							}
							this.getconsql( c, judgestr, subsetstr, id, getz0, time,
									 systime, insertz0time, supertable, tablealias, field,
									 equalz0time, countsqlsb, equalz0Counttime, this.dbname);

						}
					}
				}
				
				
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * 计算项目
	 */
	public void countsql()
	{
		RowSet rs = null;
		String getz0 = this.getz0(getyear,getmonth,changeflag);   //得到时间
		String time = this.gettime(getyear,getmonth,changeflag);  //获得年月标识时间
		String systime = Sql_switcher.sqlNow();  //  获得系统时间
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag); 
		String insertz0time = this.getequalz0time(subsetstr+"."+subsetstr,getyear,getmonth,changeflag);
		String tablealias = this.tableaLias(subsetstr); // 表别名   eg: B04 b
		String supertable = this.superTable(subsetstr);	// 子表的主表
		try{
			 ArrayList itemidlist =new ArrayList();
			 String temp = new String("");       // 组成统计条件,给getPrivSQLExpression方法
			 String id = this.id(subsetstr);         // 表的标识  eg: B04的B0110列
			 String[] expr;          			 // 获得用"|"分段截出的各个部分 
			 String typestr = "";    // 操作类型 1, 计算项目  2,统计项目  3,汇总项目
			 String operstr = "";    // 操作类型   0，求个数 1，求和  2，最小值  3，最大值  4，平均值
			 String field = "";      // 指标 eg: b0430
			 String displayid = "";  // 在fielditem表里,用类标识子集
			 String c_expr = "";     // 中文字符串
			 String fieldtype = "";  // 参数类型
			 String FSQL = "";       // 获得将中文转化成的字段
			 String firstpart = "";  // 获得用"|"分段截出的第一个部分
			 String subclass = "";   // 获得expression里,包含的下级		
			
			 itemidlist = this.getSqllist();
			// 获得给处理中文字符的解析器的参数			
			ArrayList alUsedFields = new ArrayList();
			int varType =6; // float
			
				
			//	判断该表是否有记录
			String judgestr = this.judgeSql(subsetstr);		
			String insertsql="";
			if("inserts".equals(judgestr)){
				insertsql = this.inserts( subsetstr, id, getz0, time, systime, insertz0time, supertable);
//				System.out.println(insertsql);
				dao.update(insertsql);			
			}						
			else if("insert".equals(judgestr)){
//			     删除原有记录
				this.deleteSql(subsetstr,getyear,getmonth,changeflag,"1");		
				insertsql = this.insert( subsetstr, id, getz0, time, systime, insertz0time, supertable);	
//				System.out.println(insertsql);					
				dao.update(insertsql);						
			}			
			//	取出fielditem表的expression字段，进行解析，并形成sql语句的大循环
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				boolean countgo = true;
				int infoGroup=this.getinfoGroup(subsetstr,alUsedFields);
				alUsedFields = this.getalUsedFields(subsetstr,alUsedFields);
				StringBuffer sb = new StringBuffer();     //  获得处理"查询条件"的方法（getPrivSQLExpression）的expression参数
				DynaBean dynabean=(DynaBean)it.next();
				temp = dynabean.get("expression").toString();
				if("1".equals(temp.substring(0,1)))
				{
					field = dynabean.get("expression").toString();
					displayid = dynabean.get("displayid").toString();
					
					//  获得itemid，itemtype
					String fieldsql = "select itemid,itemtype from fielditem where  fieldsetid like '";
					fieldsql = fieldsql + subsetstr+"' and displayid like '"+displayid+"' and expression like '"+field+"' ";
					rs = dao.search(fieldsql);
					if(rs.next()){
						field = rs.getString("itemid").toString();
						fieldtype = rs.getString("itemtype").toString();
					}
					varType = this.getvarType(fieldtype,varType);
//				  先分三大段截取expression字段
					if(temp.length()>0)
					{
						expr = temp.split("\\|");
						int j = expr.length;
						if(j>0)
						{
							for(int k=1;k<expr.length+1;k++)
							{
								if(k==1)  // 第一段
								{
									firstpart = expr[0].toString();  
									int splitnum = firstpart.split("::").length;
									if(splitnum>0)
									{
										for(int x=1;x<splitnum+1;x++)
										{
											if(x==1){
												// 获得要进行的操作 eg: 统计项目，计算项目，汇总项目
												typestr = firstpart.split("::")[0]; 
											}
											else if(x==2){
												// 获得要进行的操作 eg: 求个数，求和 等等
												operstr = firstpart.split("::")[1]; 
												operstr = this.getoperstr(operstr);
											}
											else if(x==3)
											{
												c_expr = firstpart.split("::")[2].toString();  // 获得公式
												c_expr=c_expr.trim(); 
												if(!(c_expr==null || "".equals(c_expr)) )
												{
													if(typestr == "1" || "1".equals(typestr))  //将中文字符转换成要操作的字段
													{											
														FSQL = this.getFQL( uv, alUsedFields, varType, infoGroup, c_expr, FSQL);
														countgo = this.getContrlFQL( uv, alUsedFields, varType, infoGroup, c_expr, FSQL, countgo);
													}												
												}
											}								
										}
									}
								}
								if(k==2) // 第二段
								{
									temp = this.getExpr(expr,temp);  // 获得 expr
								}
								if(k==3)
								{
//									sb = this.getExpression(expr, sb, temp, subclass);  //  获得 expression 和 subclass
									subclass = this.getSubClass(expr, sb, temp, subclass);
								}
							}
						}
						else
						{ 
							temp=""; //  如果是汇总项目，查询条件就为空
						}
					}
					if("1".equals(typestr))
					{
						this.getcountsql( countgo, judgestr, subsetstr, id, getz0, time,systime,
									insertz0time, supertable, FSQL, tablealias, field,equalz0time);	

					}
				}
				
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * 汇总项目
	 */
	public void collsql()
	{
		RowSet rs = null;
		String getz0 = this.getz0(getyear,getmonth,changeflag);   //得到时间
		String time = this.gettime(getyear,getmonth,changeflag);  //获得年月标识时间
		String systime = Sql_switcher.sqlNow();  //  获得系统时间
		String equalz0time = this.getequalz0time(subsetstr,getyear,getmonth,changeflag); 
		String insertz0time = this.getequalz0time(subsetstr+"."+subsetstr,getyear,getmonth,changeflag);
		String tablealias = this.tableaLias(subsetstr); // 表别名   eg: B04 b
		String supertable = this.superTable(subsetstr);	// 子表的主表
		try{
			 ArrayList itemidlist =new ArrayList();
			 String temp = new String("");       // 组成统计条件,给getPrivSQLExpression方法
			 String id = this.id(subsetstr);         // 表的标识  eg: B04的B0110列
			 String[] expr;          			 // 获得用"|"分段截出的各个部分  
			 String typestr = "";    // 操作类型 1, 计算项目  2,统计项目  3,汇总项目
			 String operstr = "";    // 操作类型   0，求个数 1，求和  2，最小值  3，最大值  4，平均值
			 String field = "";      // 指标 eg: b0430
			 String displayid = "";  // 在fielditem表里,用类标识子集
			 String c_expr = "";     // 中文字符串
			 String firstpart = "";  // 获得用"|"分段截出的第一个部分
			 String subclass = "";   // 获得expression里,包含的下级
			
			 itemidlist = this.getSqllist();
			
			//	判断该表是否有记录
			String judgestr = this.judgeSql(subsetstr);	
			String insertsql = "";
			if("inserts".equals(judgestr)){
				insertsql = this.inserts( subsetstr, id, getz0, time, systime, insertz0time, supertable);			
//				System.out.println(insertsql);
				dao.update(insertsql);
			}						
			else if("insert".equals(judgestr)){
//			     删除原有记录
				this.deleteSql(subsetstr,getyear,getmonth,changeflag,"3");			
				insertsql = this.insert( subsetstr, id, getz0, time, systime, insertz0time, supertable);							
//				System.out.println(insertsql);
				dao.update(insertsql);
							
			}		
			//	取出fielditem表的expression字段，进行解析，并形成sql语句的大循环	
			for(Iterator it=itemidlist.iterator();it.hasNext();){
				StringBuffer sb = new StringBuffer();     //  获得处理"查询条件"的方法（getPrivSQLExpression）的expression参数
				DynaBean dynabean=(DynaBean)it.next();
				temp = dynabean.get("expression").toString();
				if("3".equals(temp.substring(0,1)))
				{
					field = dynabean.get("expression").toString();
					displayid = dynabean.get("displayid").toString();
					
					//  获得itemid，itemtype
					String fieldsql = "select itemid,itemtype from fielditem where  fieldsetid like '";
					fieldsql = fieldsql + subsetstr+"' and displayid like '"+displayid+"' and expression like '"+field+"' ";
					rs = dao.search(fieldsql);
					if(rs.next()){
						field = rs.getString("itemid").toString();
					}
//				     先分三大段截取expression字段
					if(temp.length()>0)
					{
						expr = temp.split("\\|");
						int j = expr.length;
						if(j>0)
						{
							for(int k=1;k<expr.length+1;k++)
							{
								if(k==1)  // 第一段
								{
									firstpart = expr[0].toString();  
									int splitnum = firstpart.split("::").length;
									if(splitnum>0)
									{
										for(int x=1;x<splitnum+1;x++)
										{
											if(x==1){
												// 获得要进行的操作 eg: 统计项目，计算项目，汇总项目
												typestr = firstpart.split("::")[0]; 
											}
											else if(x==2){
												// 获得要进行的操作 eg: 求个数，求和 等等
												operstr = firstpart.split("::")[1]; 
												operstr = this.getoperstr(operstr);
											}
											else if(x==3)
											{
												c_expr = firstpart.split("::")[2].toString();  // 获得公式
												c_expr=c_expr.trim(); 											
											}								
										}
									}
								}
								if(k==2) // 第二段
								{
									temp = this.getExpr(expr,temp);  // 获得 expr
								}
								if(k==3)
								{
//									sb = this.getExpression(expr, sb, temp, subclass);  //  获得 expression 和 subclass
									subclass = this.getSubClass(expr, sb, temp, subclass);
								}
							}
						}
						else
						{ 
							temp=""; //  如果是汇总项目，查询条件就为空
						}
					}
					
					if("3".equals(typestr))
					{
						this.getcollsql( judgestr, subsetstr, id, getz0, time, systime,
									insertz0time, supertable, tablealias, field, equalz0time);
						
					}
				}
				
			}
			this.deleteGarbageDate(subsetstr, id, equalz0time, supertable);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	/**
	 * 数据联动后台数据操作
	 */
	public void getsql()
	{
		this.collsql();
		this.consql();
		this.countsql();
	}	

	public void setModule(String module) {
		this.module = module;
	}
}

