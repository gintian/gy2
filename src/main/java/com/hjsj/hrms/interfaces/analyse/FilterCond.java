package com.hjsj.hrms.interfaces.analyse;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class FilterCond {
	private Connection conn;
	public FilterCond(Connection conn){
		this.conn=conn;
	}
	public ArrayList getFeidlItemList(){
		ArrayList itemlist = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("SELECT min(ITEMDESC) as Hz,min(ITEMID) as itemid,max(FIELDSETID) as FieldSetId,");
		buf.append(" min(CODESETID) as CodeSetId,min(SALARYID) as SalaryId,min(SORTID) as FieldId,");
		buf.append("min(ITEMTYPE) as cType,max(ITEMLENGTH) as nLen,max(DECWIDTH) as nDec ");
		buf.append("FROM SalarySet");
		buf.append(" where ITEMID NOT LIKE 'A00%' and ITEMID<>'A0100' and ITEMID<>'NBASE'");
		buf.append(" GROUP BY itemid ORDER BY SalaryId,FieldId");
		ContentDAO dao = new ContentDAO(conn);
		try {
			RowSet rs = dao.search(buf.toString());
			while(rs.next()){
				String itemid=rs.getString("itemid");
				String hz=rs.getString("Hz");
				String cType=rs.getString("cType");
				String CodeSetId=rs.getString("CodeSetId");
				CommonData dataobj = new CommonData(itemid+":"+cType+":"+CodeSetId,hz);
				itemlist.add(dataobj);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemlist;
	}
	public String factorstr(String factor){
		StringBuffer str = new StringBuffer();
		String itemstr="";
		HashMap hm = new HashMap();
		if(factor!=null&&factor.trim().length()>0){
			String[] arr = factor.split("`");
			for(int i=0;i<arr.length;i++){
				String fa = arr[i];
				if(fa!=null&&fa.trim().length()>0){
					String eq = strEq(fa);
					if(eq!=null&&eq.trim().length()>0){
						String[] item_arr = fa.split(eq);
						if(item_arr!=null&&item_arr.length>0){
							String values = item_arr!=null&&item_arr.length==2?item_arr[1]:"";
							if(item_arr[0]!=null&&item_arr[0].trim().length()>0){
								String itemidvalue=(String)hm.get(item_arr[0]+"_itemid");
								itemidvalue=itemidvalue!=null&&itemidvalue.trim().length()>0?itemidvalue+values+" : ":values+" : ";
								String itemideq=(String)hm.get(item_arr[0]+"_eq");
								itemideq=itemideq!=null&&itemideq.trim().length()>0?itemideq+eq+":":eq+":";
								
								hm.put(item_arr[0]+"_itemid",itemidvalue);
								hm.put(item_arr[0]+"_eq",itemideq);
								itemstr+="'"+item_arr[0]+"',";
							}
						}
					}
				}
			}
		}
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("SELECT min(ITEMDESC) as Hz,min(ITEMID) as itemid,max(FIELDSETID) as FieldSetId,");
		sqlstr.append(" min(CODESETID) as CodeSetId,min(SALARYID) as SalaryId,min(SORTID) as FieldId,");
		sqlstr.append("min(ITEMTYPE) as cType,max(ITEMLENGTH) as nLen,max(DECWIDTH) as nDec ");
		sqlstr.append("FROM SalarySet");
		if(itemstr!=null&&itemstr.trim().length()>0){
			sqlstr.append(" where ITEMID in (");
			sqlstr.append(itemstr.substring(0,itemstr.length()-1)+")");
		}else{
			sqlstr.append(" where 1=2");
		}
		sqlstr.append(" GROUP BY itemid ORDER BY SalaryId,FieldId");
		try {
			RowSet rs = dao.search(sqlstr.toString());
			while(rs.next()){
				String itemid=rs.getString("itemid");
				String hz=rs.getString("Hz");
				String cType=rs.getString("cType");
				String CodeSetId=rs.getString("CodeSetId");
				String eqs = (String)hm.get(itemid+"_eq");
				String values = (String)hm.get(itemid+"_itemid");
				String[] valuesarr = values.split(":");
				String[] eqsarr = eqs.split(":");
				for(int i=0;i<valuesarr.length-1;i++){
					String itemidvalue=valuesarr[i].trim();
					String itemideqs=eqsarr[i].trim();
					str.append(itemid+":");
					str.append(hz+":");
					str.append(CodeSetId+":");
					str.append(cType+":");
					str.append(itemideqs+":");
					if(cType!=null&& "A".equalsIgnoreCase(cType)&&!"0".equals(CodeSetId)){
						str.append(itemidvalue+",");
						String desc = AdminCode.getCodeName(CodeSetId,itemidvalue);
						if("UN".equalsIgnoreCase(CodeSetId)|| "UM".equalsIgnoreCase(CodeSetId)||
								"@K".equalsIgnoreCase(CodeSetId)){
							if(desc!=null&&desc.trim().length()>0){
								str.append(desc+"`");
							}else{
								desc = AdminCode.getCodeName("UN",itemidvalue);
								if(desc!=null&&desc.trim().length()>0){
									str.append(desc+"`");
								}else{
									desc = AdminCode.getCodeName("UM",itemidvalue);
									if(desc!=null&&desc.trim().length()>0){
										str.append(desc+"`");
									}else{
										desc = AdminCode.getCodeName("@K",itemidvalue);
										str.append(desc+"`");
									}
								}
							}
						}else{
							str.append(desc+"`");
						}
					}else{
						str.append(itemidvalue+"`");
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str.toString();
	}
	public String strEq(String factor){
		String eq="";
		if(factor.indexOf(">=")!=-1)
			eq=">=";
		else if(factor.indexOf("<=")!=-1)
			eq="<=";
		else if(factor.indexOf("<>")!=-1)
			eq="<>";
		else if(factor.indexOf("=")!=-1)
			eq="=";
		else if(factor.indexOf(">")!=-1)
			eq=">";
		else if(factor.indexOf("<")!=-1)
			eq="<";
		return eq;
	}

}
