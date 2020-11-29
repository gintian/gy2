/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class SaveInfo_paramFieldTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String[] right_fields=(String[])this.getFormHM().get("right_fields");
		//if(right_fields==null || right_fields.length==0)
		//	return;	
		
		new SaveInfo_paramXml(this.getFrameconn()).saveInfo_paramNode("browser",right_fields,this.getFrameconn());
		String fieldstr="";
		ArrayList browseFields=new ArrayList();
		ArrayList listfield=DataDictionary.getFieldList("a01",Constant.USED_FIELD_SET);
		for(int i=0;right_fields!=null && i<right_fields.length;i++)
		{
			fieldstr+="," + right_fields[i];
			for(int j=0;j<listfield.size();j++){
				FieldItem fielditem=(FieldItem)listfield.get(j);
				if(fielditem.getItemid().equals( right_fields[i])){
					CommonData aCommonData=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
					browseFields.add(aCommonData);
				}
				
			}
		}
		
//		for(int i=0;i<listfield.size();i++)
//		{
//			FieldItem fielditem=(FieldItem)listfield.get(i);
//			if(fieldstr.toUpperCase().indexOf(fielditem.getItemid().toUpperCase())!=-1)
//			{
//				CommonData aCommonData=new CommonData(fielditem.getItemid(),fielditem.getItemdesc());
//				browseFields.add(aCommonData);
//			}
//		}
		this.getFormHM().put("browsefields",browseFields);
	    /*try{
	    	this.getFrameconn().commit();
		}catch(Exception e){			
		}*/
		//System.out.println(new SaveInfo_paramXml().getInfo_paramNode("browser"));
		//throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("system.options.employfieldsetsuccess"),"",""));
	}

}

