package com.hjsj.hrms.transaction.info.leader;

import com.hjsj.hrms.businessobject.general.deci.leader.LeadarParamXML;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * 领导班子选择指标
 */
public class ExportExcel extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dataList = new ArrayList();
		try{
			
			ArrayList fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
			if(fieldsetlist!=null){
				for(int i=0;i<fieldsetlist.size();i++){
					FieldSet fs = (FieldSet)fieldsetlist.get(i);
					//liuy 2015-1-27 6993：领导班子-班子成员-导出Excel,提示没有读权限，不允许导出（实际上所有的子集和指标都给了读的权限）start
					//if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限
					if ("0".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))) {// 是读权限或者写权限的时候才加载子集
						continue;
					}
					//liuy 2015-1-27 end
					if("A00".equalsIgnoreCase(fs.getFieldsetid())){
						continue;
					}
					CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
					dataList.add(cd);
				}
			}
            if(dataList.size()==0)
			{
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("leaderteam.output.nopriv")));
			}
            
            ArrayList rightlist = new ArrayList();
            LeadarParamXML leadarParamXML=new LeadarParamXML(this.getFrameconn());
			String display_field=leadarParamXML.getTextValue(LeadarParamXML.DISPLAY);
			String[] display_fields = display_field.split(",");
			for(int i = 0; i < display_fields.length; i++){
				String fielditem = display_fields[i];
				FieldItem fi = DataDictionary.getFieldItem(fielditem);
				CommonData cd = new CommonData(fi.getItemid(), fi.getItemdesc());
				rightlist.add(cd);
			}
            this.getFormHM().put("fieldSetDataList", dataList);
            this.getFormHM().put("rightlist", rightlist);
		}catch(Exception e){
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

}
