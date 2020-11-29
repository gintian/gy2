package com.hjsj.hrms.transaction.sys.options.template;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

public class DeleteTemplateTrans extends IBusiness{
	public void execute() throws GeneralException{
		String[] template_id = (String[])this.getFormHM().get("selected_template_id_array");
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		//String b_delete =(String)hm.get("b_delete");
		StringBuffer str_sql = new StringBuffer();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
        for(int i=0;i<template_id.length;i++){
			str_sql.append(" or template_id ="+template_id[i]);
		}
        
        try{
        	dao.delete("delete from t_sys_msgtemplate where "+str_sql.substring(3),new ArrayList());
        }catch(Exception e){
        	e.printStackTrace();
        }
		
			
	hm.remove("b_delete");		
		
	}
	

}
