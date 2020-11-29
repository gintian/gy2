package com.hjsj.hrms.utils.sys;

import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ValidateDateFilled {
	private ArrayList fieldlist;
	private String fielsetid;
	private String fieldsetdesc;
/**
 * 构造函数
 * @param uv UerView 对象
 * @param fieldsetid 主集或子集id
 */
	public ValidateDateFilled(UserView uv,String fieldsetid){
		this.setFieldlist(uv,fieldsetid);
		this.fieldlist=this.getFieldlist();
		this.fielsetid=fieldsetid;
		this.setFieldsetdesc(fieldsetid);
	}
	/**
	 * 根据输入的dynabeanlist
	 * @param dylist
	 * @return 字符串，字符串显示的未填写信息
	 */
	public String  getValidate(ArrayList dylist){
		StringBuffer sb=new StringBuffer();
		HashMap hm=new HashMap();
		for(Iterator it=dylist.iterator();it.hasNext();){
			DynaBean dynabean=(DynaBean) it.next();
			for(Iterator its=this.fieldlist.iterator();its.hasNext();){
				FieldItem fi=(FieldItem) its.next();
				String value=dynabean.get(fi.getItemid()).toString();
				if(value==null||value.length()<=0){
					if(!hm.containsKey(fi.getItemdesc())){
						hm.put(fi.getItemdesc(),fi.getItemdesc());
						sb.append(this.fieldsetdesc+"中的<"+fi.getItemdesc()+">\\n");
					}
				}
			}
			
		}
		if(sb.toString().length()>0)
			sb.append("指标必须填写！\\n");
		return sb.toString();
	}
	/**
	 * 根据传入的rowset
	 * @param rs
	 * @return字符串，字符串显示的未填写信息
	 */
	public String getValidate(RowSet rs){
		StringBuffer sb=new StringBuffer();
		HashMap hm=new HashMap();
		try {
			while(rs.next()){
				for(Iterator its=this.fieldlist.iterator();its.hasNext();){
					FieldItem fi=(FieldItem) its.next();
					String value=rs.getString(fi.getItemid());
					if(value==null||value.length()<=0){
						if(!hm.containsKey(fi.getItemdesc())){
							hm.put(fi.getItemdesc(),fi.getItemdesc());
							sb.append(this.fieldsetdesc+"中的<"+fi.getItemdesc()+">\\n");
						}
						
					}
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(sb.toString().length()>0)
			sb.append("指标必须填写！\\n");
		return sb.toString();
	}
	/**
	 * 返回必须填写的指标list
	 * @return
	 */
	public ArrayList getFieldlist() {
		
		return fieldlist;
	}
	/**
	 * 设置必须填写的指标list
	 * @param uv
	 * @param fieldsetid
	 */
	private void setFieldlist(UserView uv,String fieldsetid) {
		ArrayList rtlist=new ArrayList();
		ArrayList fieldlist=uv.getPrivFieldList(fieldsetid,0);
		
		for(Iterator it=fieldlist.iterator();it.hasNext();){
			FieldItem fi=(FieldItem) it.next();
			if(fi.isFillable()){
				rtlist.add(fi);
			}
		}
		this.fieldlist = rtlist;
	}
	public String getFielsetid() {
		return fielsetid;
	}
	public String getFieldsetdesc() {
		return fieldsetdesc;
	}
	private  void setFieldsetdesc(String fieldsetid) {
		String fieldsetdesc="";
		FieldSet fs=DataDictionary.getFieldSetVo(fieldsetid);
		fieldsetdesc=fs.getFieldsetdesc();
		this.fieldsetdesc = fieldsetdesc;
	}
	
	
}
