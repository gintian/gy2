package com.hjsj.hrms.transaction.browse.history;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * <p>Title:SnapshotTrans.java</p>
 * <p>Description>:SnapshotTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 19, 2010 5:19:40 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: LiWeichao</p>
 */
public class SnapshotTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList setlist=new ArrayList();
		ArrayList rightlist=new ArrayList();
		try{
		ArrayList uvlist=this.getUserView().getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		for (int i = 0; i < uvlist.size(); i++) {
			FieldSet field=(FieldSet) uvlist.get(i);
			if("A00".equalsIgnoreCase(field.getFieldsetid()))
				continue;
			CommonData obj=new CommonData(field.getFieldsetid(),field.getCustomdesc());
			setlist.add(obj);
		}
		String left_fields="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		this.frowset=dao.search("select str_value from Constant where Upper(Constant)='HISPOINT_PARAMETER'");
		if(frowset.next()){
			ConstantXml xml = new ConstantXml(this.frameconn,"HISPOINT_PARAMETER","Emp_HisPoint");
			left_fields =xml.getTextValue("/Emp_HisPoint/Struct");
		}else{
			//设置的快照指标
			frowset = dao.search("select str_value from Constant where Upper(Constant)='EMP_HISDATA_STRUCT'");
			if(frowset.next())
				left_fields=frowset.getString("str_value");
		}
		
		String rightStr=/*(String)this.getFormHM().get("snap_norm")*/left_fields;
		this.getFormHM().remove("snap_norm");
		if(rightStr==null||rightStr.length()<=0)
			rightStr="";		
		String[] right_f=rightStr.split(",");
		for(int m=0;m<right_f.length;m++){
			if(right_f[m].length()>0){
				FieldItem fi=DataDictionary.getFieldItem(right_f[m]);
				if(fi==null)
					continue;
				CommonData obj=new CommonData(fi.getItemid(),fi.getItemdesc());
				rightlist.add(obj);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		this.getFormHM().put("setlist",setlist);
		this.getFormHM().put("rightlist",rightlist);
		}
	}

}
