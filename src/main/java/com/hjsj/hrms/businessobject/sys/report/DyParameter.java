package com.hjsj.hrms.businessobject.sys.report;

import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.HashMap;

public class DyParameter {
	 private static HashMap hashmap = new HashMap();
	 public DyParameter(){
	 }
	 public static void reloadParameter(int parameter,String param,Connection conn){
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(conn);
			String paramvalue=sysbo.getValue(parameter,param);
			paramvalue=paramvalue!=null&&paramvalue.trim().length()>0?paramvalue:"";

			LazyDynaBean dynaBean = new LazyDynaBean();
			dynaBean.set(param,paramvalue);
			hashmap.put("lazyDynaBean_photo",dynaBean);
	 }
	 public static void refresh(int parameter,String param,Connection conn){
		 if(hashmap.size()>0) {
             hashmap.clear();
         }
		 reloadParameter(parameter,param,conn);
	 }
	 public static LazyDynaBean getParameter(int parameter,String param,Connection conn){
		 if(hashmap.size()>0){
			 LazyDynaBean lazyDynaBean = (LazyDynaBean)hashmap.get("lazyDynaBean_photo");
			 if(lazyDynaBean.get(param)!=null){
				 return (LazyDynaBean)hashmap.get("lazyDynaBean_photo");
			 }else{
				 reloadParameter(parameter,param,conn);
				 return (LazyDynaBean)hashmap.get("lazyDynaBean_photo");
			 }
		 }else{
			 reloadParameter(parameter,param,conn);
			 return (LazyDynaBean)hashmap.get("lazyDynaBean_photo");
		 }
	 }
}
