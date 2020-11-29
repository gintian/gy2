package com.hjsj.hrms.transaction.kq.options.sign_point;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.List;

public class GetPersonListTrans extends IBusiness {

	public void execute() throws GeneralException {
         
		String personId = (String)this.getFormHM().get("personId");
        List personList = new ArrayList();
        
        if(personId.trim().length()<1){
        	this.formHM.put("personList", personList);
        	return;
        }
        
        try{
        	
        	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn(),this.userView);
            ArrayList nbase=kqUtilsClass.getKqPreList();
        	//获取拼音简码的字段
        	Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
        	String pinyin_field=sysbo.getValue(Sys_Oth_Parameter.PINYIN_FIELD_SEARCH);
        	//获取唯一性指标的字段
        	String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");
        	
        	StringBuffer sqlStr = new StringBuffer();
        	
        	ContentDAO dao = new ContentDAO(frameconn);
        	for (int i = 0; i < nbase.size(); i++)
			{
        		sqlStr.append(" select '"+nbase.get(i)+"' nbase,a0100,a0101 from "+nbase.get(i)+"A01 where ");
        		//sqlStr.append(" where "+pinyin_field+" like '%"+personId+"%' or "+onlyname+" like '%"+personId+"%' or  a0101 LIKE '%" + personId + "%' ");
        		if(null != pinyin_field && pinyin_field.length()>0)
        			sqlStr.append(pinyin_field+" like '%"+personId+"%' or ");
        		
        		if(null != onlyname && onlyname.length()>0)
        			sqlStr.append(onlyname+" like '%"+personId+"%' or ");
        		
        		sqlStr.append(" a0101 like '%" + personId + "%' ");
        		
        		sqlStr.append(" union all ");
			}
        	
        	sqlStr.delete(sqlStr.length()-11, sqlStr.length());
				this.frowset = dao.search(sqlStr.toString());
				
				while(this.frowset.next()){
					CommonData cdata = new CommonData(this.frowset.getString("nbase")+"`"+this.frowset.getString("a0100"), this.frowset.getString("a0101"));
					personList.add(cdata);
				}
				
        }catch(Exception e){
        	e.printStackTrace();
        }
		this.formHM.put("personList", personList);
	}

}
