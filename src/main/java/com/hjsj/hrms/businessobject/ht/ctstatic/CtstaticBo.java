package com.hjsj.hrms.businessobject.ht.ctstatic;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.AdminDb;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CtstaticBo {
	/**数据库连接*/
	private ContentDAO dao = null;
	private UserView userView = null;
	//  是否存在e0122字段
	private boolean isExist = false;
	public CtstaticBo(){
		
	}
	public CtstaticBo(ContentDAO dao){
		this.dao=dao;
	}
	public CtstaticBo(ContentDAO dao,UserView uv){
		this.dao=dao;
		this.userView=uv;
	}
	/**
	 * 获取表单
	 * @param valuelist
	 * @param fielditem
	 * @param countall
	 * @return
	 */
	public String tabStr(ArrayList valuelist,String itemid,int countall){
		StringBuffer buf = new StringBuffer();
		FieldItem fielditem=DataDictionary.getFieldItem(itemid);
		if(fielditem==null)
			return "";
		buf.append("<table border=\"0\" width=\"100%\" cellspacing=\"0\"  cellpadding=\"0\">");
		buf.append("<tr class=\"fixedHeaderTr1\">");
		buf.append("<td align=\"center\" width=\"50%\" nowrap class=\"TableRow\" style=\"border-top:none;border-left:none;border-right:none;\">");
		buf.append(fielditem.getItemdesc());
		buf.append("</td><td align=\"center\" style=\"border-top:none;border-right:none;\" class=\"TableRow\">");
		buf.append("人数</td><td align=\"center\" style=\"border-top:none;border-right:none;\" class=\"TableRow\">百分比（％）</td></tr>");
		/*
		HashMap map = new HashMap();
		if (fielditem.isCode()) {
			map = this.getCodeItemId(fielditem.getCodesetid());
		}
		*/
		for(int i=0;i<valuelist.size();i++){
			CommonData temp=(CommonData)valuelist.get(i);
			if(temp!=null){
				buf.append("<tr>");
				if("A".equalsIgnoreCase(fielditem.getItemtype()))
					buf.append("<td style=\"border-top:none;border-left:none;border-right:none;\" class=\"RecordRow\">");
				else
					buf.append("<td align=\"right\"style=\"border-top:none;border-left:none;border-right:none;\" class=\"RecordRow\">");
				if(fielditem.isCode()){
					buf.append(temp.getDataName()); 
				}else{
					buf.append(temp.getDataName());
				}
				buf.append("</td><td align=\"right\" style=\"border-top:none;border-right:none;\" class=\"RecordRow\" nowrap>");
				if(temp.getDataValue()!=null&& "0".equals(temp.getDataValue())){
					buf.append(temp.getDataValue());
					buf.append("</td><td class=\"RecordRow\" style=\"border-top:none;border-right:none;\" align=\"right\">");
				}else{
					buf.append("<div style=\"cursor:hand;color:#0c07cf;\" onclick=\"viewItemvalue('");
					if("空".equalsIgnoreCase(temp.getDataName())) {
						buf.append("no");
					} else {
//						buf.append(temp.getDataName());
						
						if (fielditem.isCode()) {
							buf.append(temp.get("codeitemid"));
						} else {
							buf.append(temp.getDataName());
						}
						
					}
					buf.append("')\">");
					buf.append(temp.getDataValue());
					buf.append("</div></td><td class=\"RecordRow\" style=\"border-top:none;border-right:none;\" align=\"right\">");
				}
				buf.append(getPercentage(countall+"",temp.getDataValue(),3));
				buf.append("</td></tr>");
			}
		}
		buf.append("</table>");
		return buf.toString();
	}
	
	/**
	 * 根据codesetid获得所有itemid
	 * @param codeSetId
	 * @return
	 */
	private HashMap getCodeItemId(String codeSetId) {
		HashMap map = new HashMap();
		StringBuffer strsql = new StringBuffer(); 
		strsql.append("(select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from codeitem where codesetid='");
		strsql.append(codeSetId);
		strsql.append("')");
	    strsql.append(" union all ");
	    strsql.append("(select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from organization where codesetid='");
	    strsql.append(codeSetId);
	    strsql.append("')");
	    strsql.append(" union all ");
	    strsql.append("(select CodeSetID,CodeItemId,CodeItemDesc,parentid,childid from vorganization where codesetid='");
	    strsql.append(codeSetId);
	    strsql.append("')");
	    strsql.append(" order by CodeSetID,CodeItemId");
	    ResultSet rs = null;
		try {
		    rs = dao.search(strsql.toString());
		    while (rs.next()) {
		    	String key = rs.getString("CodeSetID") + rs.getString("CodeItemDesc");
		    	String value = rs.getString("CodeItemId");
		    	map.put(key, value);
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return map;
	}
	/**
	 * 根据代码集获取代码列表
	 * @param codesetid
	 * @return
	 */
	public ArrayList stType(String codesetid){
		ArrayList list = new ArrayList();
		RowSet rs = null;
		ArrayList sqllist = new ArrayList();
		sqllist.add(codesetid);
		try {
			/*代码类型是组织机构时，也要查询出对应的机构集合   wangb 20180706*/
			String sql = "";
			if("UN".equalsIgnoreCase(codesetid) || "UM".equalsIgnoreCase(codesetid) || "@K".equalsIgnoreCase(codesetid))
				sql = "select codeitemid,codeitemdesc from organization where codesetid=?";
			else
				sql = "select codeitemid,codeitemdesc from codeitem where codesetid=?";
			rs = dao.search(sql,sqllist);
			while(rs.next()){
				String codeitemid = rs.getString("codeitemid");
				String codeitemdesc = rs.getString("codeitemdesc");
				CommonData temp=new CommonData(codeitemid,codeitemdesc);
				list.add(temp);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return list;
	}
	/**
	 * 获取值
	 * @param dbname
	 * @param setid
	 * @param itemid
	 * @param countall
	 * @param a_code
	 * @return
	 */
	public ArrayList valueList(String dbname,String setid,String itemid,int countall,String a_code){
		ArrayList list = new ArrayList();
		String tabname = dbname+setid;
		FieldItem fielditem=DataDictionary.getFieldItem(itemid);
		String sqlstr = valueSqlStr(dbname,setid,fielditem,a_code);
		ArrayList typelist = groupTypeList(tabname,fielditem);
		ArrayList typevaluelist = groupTypeList(typelist,fielditem);
		try {
			int arr[] = new int[typevaluelist.size()];
			RowSet rs = dao.search(sqlstr);
			while(rs.next()){
				String values = rs.getString(fielditem.getItemid());
//				values=values!=null?values:"";
				if(values == null)
					continue;
				if(values.length() == 0)
					continue;
				for(int i=0;i<typevaluelist.size();i++){
					String groupvalue = (String)typevaluelist.get(i);
					groupvalue=groupvalue!=null?groupvalue:"";
					if(groupvalue.equalsIgnoreCase(values)){
						arr[i]+=1;
						break;
					}
				}
			}
			for(int i=0;i<typelist.size();i++){
				if(fielditem.isCode()){
					CommonData temp=(CommonData)typelist.get(i);
					if(temp!=null){
						CommonData temp1 = new CommonData();
						temp1.setDataValue(arr[i]+"");
//						temp1.setDataName(temp.getDataValue());
						temp1.setDataName(temp.getDataName());
						//代码型有重名的代码时点击个数查看详细会有问题，添加codeitemid  guodd 2018-07-20。
						temp1.put("codeitemid",temp.getDataValue());
						list.add(temp1);
						countall-=arr[i];
					}
				}else{
					String str = (String)typevaluelist.get(i);
					CommonData temp = new CommonData();
					temp.setDataName(str);
					temp.setDataValue(arr[i]+"");
					countall-=arr[i];
					list.add(temp);
				}
			}
			
			CommonData temp = new CommonData();
			temp.setDataName("空");
			temp.setDataValue(countall+"");
			list.add(temp);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}
	/**
	 * 将两组值相加
	 * @param valuelist1
	 * @param valuelist2
	 * @return
	 */
	public ArrayList addValueList(ArrayList valuelist1,ArrayList valuelist2){
		ArrayList list = new ArrayList();
		if(valuelist1!=null&&valuelist1.size()>0){
			for(int i=0;i<valuelist1.size();i++){
				CommonData temp1 = (CommonData)valuelist1.get(i);
				String value1 = temp1.getDataValue();
				value1=value1!=null?value1:"";
				if(temp1!=null){
					boolean check = true;
					for(int j=0;j<valuelist2.size();j++){
						CommonData temp2 = (CommonData)valuelist2.get(j);
						String value2 = temp2.getDataValue();
						value2=value2!=null?value2:"";
						if(temp2!=null){
							if(temp2.getDataName().equalsIgnoreCase(temp1.getDataName())){
								int n=0;
								if(value1.trim().length()>0){
									n+=Integer.parseInt(value1);
								}
								if(value2.trim().length()>0){
									n+=Integer.parseInt(value2);
								}
								temp1.setDataValue(n+"");
								list.add(temp1);
								check = false;
								break;
							}
						}
					}
					if(check){
						list.add(temp1);
					}
				}
			}
		}else{
			list = valuelist2;
		}
		return list;
	}
	/**
	 * 获取查询子集sql语句
	 * @param tabname
	 * @param fielditem
	 * @return
	 */
	private String valueSqlStr(String dbname,String fieldsetid,FieldItem fielditem,String a_code){
		StringBuffer sqlstr = new StringBuffer();
		String tabname = dbname+fieldsetid;
//		sqlstr.append("select ");
//		if(fielditem.getItemtype().equalsIgnoreCase("D")){
//			sqlstr.append(Sql_switcher.numberToChar(Sql_switcher
//					.year(fielditem.getItemid())));
//			sqlstr.append(" as ");
//		}
//		sqlstr.append(fielditem.getItemid());
//		sqlstr.append(" from ");
//		sqlstr.append(tabname);
		
		// 只统计最后一条记录，i9999最大的一条
		sqlstr.append("select ");
		if("D".equalsIgnoreCase(fielditem.getItemtype())){
			sqlstr.append(Sql_switcher.numberToChar(Sql_switcher
					.year("a."+fielditem.getItemid())));
			sqlstr.append(" as ");
			sqlstr.append(fielditem.getItemid());
		} else {
			sqlstr.append("a.");
			sqlstr.append(fielditem.getItemid());
		}
		sqlstr.append(" from ");
		sqlstr.append(tabname);
		sqlstr.append(" a right join (select max(i9999) i9999,a0100 from ");
		sqlstr.append(tabname);
		sqlstr.append("  group by a0100) b on a.i9999=b.i9999 and a.a0100=b.a0100 ");
		if(a_code!=null&&a_code.trim().length()>2){
			sqlstr.append(" where b.A0100 in(");
			sqlstr.append(whereCodeStr(a_code,dbname));
			sqlstr.append(")");
		}
		sqlstr.append(" order by ");
		sqlstr.append(fielditem.getItemid());
		
		return sqlstr.toString();
	}
	/**
	 * 获取分组
	 * @param dao
	 * @param tabname
	 * @param fielditem
	 * @return
	 */
	private ArrayList groupTypeList(String tabname,FieldItem fielditem){
		ArrayList typelist = new ArrayList();
		if("D".equalsIgnoreCase(fielditem.getItemtype())){
			typelist = yearValue(tabname,fielditem);
		}else if("A".equalsIgnoreCase(fielditem.getItemtype())){
			if(fielditem.isCode()){
				typelist = stType(fielditem.getCodesetid());
			}
		}else if("N".equalsIgnoreCase(fielditem.getItemtype())){
			typelist = groupValue(tabname,fielditem);
		}
		return typelist;
	}
	/**
	 * 获取分组
	 * @param dao
	 * @param tabname
	 * @param fielditem
	 * @return
	 */
	private ArrayList groupTypeList(ArrayList typelist,FieldItem fielditem){
		ArrayList list = new ArrayList();
		if(fielditem.isCode()){
			for(int i=0;i<typelist.size();i++){
				CommonData temp=(CommonData)typelist.get(i);
				if(temp!=null){
					list.add(temp.getDataValue());
				}
			}
		}else 
			list = typelist;
		return list;
	}
	/**
	 * 获取总人员数
	 * @param dbname 
	 * @param fieldsetid
	 * @param a_code
	 * @return
	 */
	public int countAll(String dbname,String fieldsetid,String a_code){
		int count = 0;
		String tabname=dbname+"a01";
		try {
			StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("select count( distinct A0100) from ");
			sqlstr.append(tabname);
			if(a_code!=null&&a_code.trim().length()>2){
				sqlstr.append(" where A0100 in(");
				sqlstr.append(whereCodeStr(a_code,dbname));
				sqlstr.append(")");
			}
			RowSet rs = dao.search(sqlstr.toString());
			if(rs.next()){
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * 获取整数分组数
	 * @param dao
	 * @param tabname
	 * @param fielditem
	 * @return
	 */
	private ArrayList groupValue(String tabname,FieldItem fielditem){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		buf.append(fielditem.getItemid());
		buf.append(" from ");
		buf.append(tabname);
		buf.append(" where ");
		buf.append(fielditem.getItemid());
		buf.append(" is not null ");
		buf.append(" group by ");
		buf.append(fielditem.getItemid());
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				String values = rs.getString(fielditem.getItemid());
				values = values!=null&&values.trim().length()>0?values:"";
				if(values.trim().length()>0){
					list.add(values);
				}
			}
			list = strSort(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取年分组数
	 * @param dao
	 * @param tabname
	 * @param fielditem
	 * @return
	 */
	private ArrayList yearValue(String tabname,FieldItem fielditem){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select ");
		buf.append(Sql_switcher.numberToChar(Sql_switcher
				.year(fielditem.getItemid())));
		buf.append(" as ");
		buf.append(fielditem.getItemid());
		buf.append(" from ");
		buf.append(tabname);
		buf.append(" where ");
		buf.append(fielditem.getItemid());
		buf.append(" is not null order by ");
		buf.append(fielditem.getItemid());
		try {
			HashSet hs = new HashSet();
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				String values = rs.getString(fielditem.getItemid());
				values = values!=null&&values.trim().length()>0?values:"";
				if(values.trim().length()>0){
					hs.add(values);
				}
			}
			if(hs.size()>0){
				String str = hs.toString();
				if(str!=null&&str.trim().length()>2){
					str = str.substring(1);
					str = str.substring(0,str.length()-1);
				}
				String arr[] = str.split(",");
				for(int i=0;i<arr.length;i++){
					list.add(arr[i]);
				}
				list = strSort(list);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 获取百分比
	 * @param countall 总值
	 * @param values  除值
	 * @param scale 保留几位小数位
	 * @return
	 */
	public String getPercentage(String countall,String values,int scale){
		if(countall==null||countall.trim().length()<1|| "0".equalsIgnoreCase(countall))
			return "100%";
		if(values==null||values.trim().length()<1)
			return "0%";
		if(scale<0)
			scale = 0;
		double d = Double.parseDouble(values)/Double.parseDouble(countall);
        NumberFormat nf  =  NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(scale);

        return nf.format(d);
	}
	/**
	 * 根据机构代码生成sql语句
	 * @param a_code
	 * @param dbname
	 * @return
	 */
	public String whereCodeStr(String a_code,String dbname){
		StringBuffer wherestr=new StringBuffer();
		StringBuffer sexpr=new StringBuffer();
		StringBuffer sfactor=new StringBuffer();
		if(a_code!=null&&a_code.trim().length()>1){
			String codesetid=a_code.substring(0, 2);
			String value=a_code.substring(2);

			if(value!=null&&value.trim().length()>0){
				if("UN".equalsIgnoreCase(codesetid)){
					sexpr.append("B0110=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("UM".equalsIgnoreCase(codesetid)){
					sexpr.append("E0122=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else if("@K".equalsIgnoreCase(codesetid)){
					sexpr.append("E01A1=");
					sexpr.append(value);
					sexpr.append("*`");
					sfactor.append("1");
				}else{
					String[] codearr =a_code.split(":");
					if(codearr.length==3){
						sexpr.append(codearr[1]+"=");
						sexpr.append(codearr[2]);
						sexpr.append("*`");
						sfactor.append("1");
					}
				}
			}else{
				sexpr.append("B0110=");
				sexpr.append(value);
				sexpr.append("*`B0110=`");
				sfactor.append("1+2");
			}
		}	
		/**过滤条件*/
		try {
			String strwhere=userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),
					dbname,false,true,new ArrayList());
			wherestr.append("select A0100 ");
			wherestr.append(strwhere);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return wherestr.toString();
	}
	/**
	 * 插入排序
	 * @param data
	 * @return
	 */
	public int[] sort(int[] data) {
		int temp;
		for(int i=0;i<data.length;i++){
			for(int j=i;(j>0)&&(data[j]>data[j-1]);j--){
				temp = data[j];
				data[j]=data[j-1];
				data[j-1]=temp;
			}
		}
		return data;
    }
	/**
	 * 字符串为数字的ArrayList排序
	 * @param data
	 * @return
	 */
	public ArrayList strSort(ArrayList data){
		ArrayList list = new ArrayList();
		int datanum[] = new int[data.size()];
		for(int i=0;i<data.size();i++){
			if(data.get(i)!=null&&((String)data.get(i)).length()>0){
				String values= (String)data.get(i);
				values=values.trim();
				datanum[i]=Integer.parseInt(values);
			}else{
				datanum[i]=0;
			}
		}
		int [] sortn = sort(datanum);
		for(int i=0;i<data.size();i++){
			list.add(sortn[i]+"");
		}
		return list;
	}
	/**
	 * 获取sql语句
	 * @param dbname
	 * @param fieldsetid
	 * @param a_code
	 * @param itemlist
	 * @return
	 */
	public String sqlStr(String dbname,String itemid,String a_code,String values,ArrayList itemlist,int i){
		FieldItem fielditem=DataDictionary.getFieldItem(itemid);
		StringBuffer sqlstr = new StringBuffer();
		String maintable=dbname+"A01";
		String itemtable=dbname+fielditem.getFieldsetid();
		String fields=getFields(itemlist, maintable);
		sqlstr.append("select ");
		sqlstr.append(fields);
		sqlstr.append(",'");
		sqlstr.append(i);
		sqlstr.append("' nbase,a.a0100 from ");
//		sqlstr.append(itemtable);
		
		// 只查询某人最后一条 记录，i9999最大的记录
		sqlstr.append("(select ");
		sqlstr.append("m.*");
		sqlstr.append(" from ");
		sqlstr.append(itemtable);
		sqlstr.append(" m right join (select max(i9999) i9999,a0100 from ");
		sqlstr.append(itemtable);
		sqlstr.append("  group by a0100) n on m.i9999=n.i9999 and m.a0100=n.a0100)");
		
		sqlstr.append(" a right join ");
		sqlstr.append(maintable);
		sqlstr.append(" on ");
		sqlstr.append(maintable);
		sqlstr.append(".A0100=");
		sqlstr.append(" a");
		sqlstr.append(".A0100 where ");
		if("no".equalsIgnoreCase(values)){
			sqlstr.append(" (a");
			sqlstr.append("."+itemid+" is null ");
			// 字符型的数据为空，需判断='',其它类型不需要也不可以
            if (",A,M,".contains("," + fielditem.getItemtype().toUpperCase() + ",")) {
                sqlstr.append(" or a");
                sqlstr.append("."+itemid+"=''");
            }
            sqlstr.append(")");
		}else{
			if("D".equalsIgnoreCase(fielditem.getItemtype())){
				sqlstr.append(Sql_switcher.numberToChar(Sql_switcher
						.year("a."+itemid)));
				sqlstr.append("='"+values+"'");
			}else{
				sqlstr.append(" a");
				sqlstr.append("."+itemid+"='"+values+"'");
			}
		}
		if(a_code!=null&&a_code.trim().length()>0){
			sqlstr.append(" and a.A0100 in(");
			sqlstr.append(whereCodeStr(a_code,dbname));
			sqlstr.append(")");
		}
		
		if("no".equalsIgnoreCase(values)){
			sqlstr.append(" UNION select ");
			sqlstr.append(fields);
			sqlstr.append(",'");
			sqlstr.append(i);
			sqlstr.append("' nbase," + maintable + ".a0100 from ");
			sqlstr.append(itemtable);			
			sqlstr.append(" a right join ");
			sqlstr.append(maintable);
			sqlstr.append(" on ");
			sqlstr.append(maintable);
			sqlstr.append(".A0100=");
			sqlstr.append(" a");
			sqlstr.append(".A0100 where ");
			sqlstr.append(maintable);
			sqlstr.append(".a0100 not in(select a0100 from ");
			sqlstr.append(itemtable);
			sqlstr.append(")");
			if(a_code!=null&&a_code.trim().length()>0){
				sqlstr.append(" and ");
				sqlstr.append(maintable);
				sqlstr.append(".A0100 in(");
				sqlstr.append(whereCodeStr(a_code,dbname));
				sqlstr.append(") ");
			}
		}
		
		return sqlstr.toString();
	}
	public String sqlAllStr(String dbstr,String itemid,
			String a_code,String values,ArrayList itemlist){
		StringBuffer buf = new StringBuffer();
		String dbArr[] = dbstr.split(",");
		buf.append("select * from (");
		for(int i=0;i<dbArr.length;i++){
			if(dbArr[i]!=null&&dbArr[i].trim().length()>0){
				if(i > 0){
					buf.append(" UNION ");
				}
				buf.append(sqlStr(dbArr[i],itemid, a_code, values,itemlist,i));
			}
		}
		if (isExist) {
			buf.append(") v order by nbase,e0122,a0100");
		} else {
			buf.append(") v order by nbase,a0100");
		}
		return buf.toString();
	}
	/**
	 * 求得当前数据集中的查询字段列表
	 * @param list
	 * @return
	 */
	public String getFields(ArrayList list,String maintable){
		StringBuffer buf=new StringBuffer();
		for(int i=0;i<list.size();i++){
//			Field field=(Field)list.get(i);
//			FieldItem fielditem=DataDictionary.getFieldItem(field.getName());
		    String field = (String)list.get(i);
		    FieldItem fielditem=DataDictionary.getFieldItem(field);
		    // 过滤掉大字段类型（不过滤时，sqlserver没问题，oracle有问题）
		    if (!"m".equalsIgnoreCase(fielditem.getItemtype())) {
		    	if(fielditem!=null&&fielditem.isMainSet()){
					buf.append(maintable);
					buf.append(".");
					buf.append(field);
		    	}else{
		    		buf.append(field);
		    	}
			    buf.append(",");
			    // 存在部门
			    if ("e0122".equalsIgnoreCase(fielditem.getItemid())) {
			    	this.isExist = true;
			    }
		    }
		}//for i loop end.
		buf.setLength(buf.length()-1);
		return buf.toString();
	}
	
	/**取得合同详细信息的数据*/
	public ArrayList getDataList(String sqlStr, ArrayList itemlist) {
		// 获得部门层级
		String uplevel = "";
		Connection conn = null;
		try {
			conn = AdminDb.getConnection();
			Sys_Oth_Parameter sys = new Sys_Oth_Parameter(conn);
			uplevel = sys.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
			uplevel=uplevel!=null&&uplevel.trim().length()>0?uplevel:"0";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	    ArrayList list = new ArrayList();
	    try
	    {
		RowSet rs = dao.search(sqlStr);
		while (rs.next())
		{
		    LazyDynaBean abean=new LazyDynaBean();
		    for (int i = 0; i < itemlist.size(); i++)
		    {
			String field = (String)itemlist.get(i);
			FieldItem fielditem=DataDictionary.getFieldItem(field);
			String codeset = fielditem.getCodesetid();
			String type = fielditem.getItemtype();
			String value = "";
			if("A".equals(type) && !"0".equals(codeset)) {
				value = rs.getString(field)==null?"":rs.getString(field);
				
				// 部门按层级显示
				if ("e0122".equalsIgnoreCase(fielditem.getItemid().trim())) {
					int nlevel = Integer.parseInt(uplevel);
					CodeItem codeItem = AdminCode.getCode(codeset, value, nlevel);
					if (codeItem != null) {
						value = codeItem.getCodename();
					} else {
						value = "";
					}
				} else {
					value=AdminCode.getCode(codeset, value) != null ? AdminCode.getCode(codeset, value).getCodename() : "";
				}
			}
			else if("D".equals(type))
			{
			    Date date = rs.getDate(field);
			    if(date!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				value=sdf.format(date);
			        value = new FormatValue().format(fielditem, value);
				value = PubFunc.replace(value, ".", "-");
			    }			  
			} else {
				if (!"M".equalsIgnoreCase(type))
					value = rs.getString(field)==null?"":rs.getString(field);
			}
			    abean.set(field, value);			
		    }
		    list.add(abean);
		}
	    } catch (Exception e)
	    {
		e.printStackTrace();
	    }

	return list;
    }
	/**得到变动字段中文名字*/
	public ArrayList getFldCns(ArrayList itemlist)
	{
	    ArrayList list = new ArrayList();
	    for (int i = 0; i < itemlist.size(); i++)
	    {
		String field = (String)itemlist.get(i);
		FieldItem fielditem=DataDictionary.getFieldItem(field);
		String type = fielditem.getItemtype();
		String align="left";
		if("N".equals(type))
		    align="right";
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("itemdesc", fielditem.getItemdesc());
		abean.set("itemid", field);
		abean.set("align", align);
		list.add(abean);
	    }
	    return list;
	}
}
