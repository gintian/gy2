package com.hjsj.hrms.transaction.ykcard;

import com.hjsj.hrms.businessobject.sys.options.SearchTableCardConstantSet;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
/**
 * 非人员库子集，不包含人员基本子集
 * <p>Title:SelectTableCardConstantSetTrans.java</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 13, 2007 7:01:45 PM</p>
 * @author sunxin
 * @version 1.0
 *
 */
public class SelectTableCardTrans extends IBusiness {

	public void execute() throws GeneralException
	{
//		选中的默认值
		SearchTableCardConstantSet tableCardConstantSet=new SearchTableCardConstantSet(this.userView,this.getFrameconn());
		ArrayList selectedList = new ArrayList();
		
		String mysalarys="";
		if(tableCardConstantSet.check()){
			Sys_Oth_Parameter sop = new Sys_Oth_Parameter(this.getFrameconn());
			mysalarys = sop.getValueS(Sys_Oth_Parameter.MYSALARYS_SALARY,"setname","title");		
		}else{
			tableCardConstantSet.insert();
		}
		if(mysalarys==null||mysalarys.length()<=0)
			mysalarys="";
		ArrayList fieldSetList = new ArrayList();
		ArrayList fieldSetTempList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
		for(int i=0; i< fieldSetTempList.size(); i++){
			FieldSet fieldSet = (FieldSet) fieldSetTempList.get(i);
			String setid = fieldSet.getFieldsetid();
			String setdesc = fieldSet.getCustomdesc();
			String setChangeFlag = fieldSet.getChangeflag();
			if(setChangeFlag == null){			
			}else{
				if(!"A01".equalsIgnoreCase(setid))
				{
					CommonData dataobj = new CommonData(setid, setdesc);
					fieldSetList.add(dataobj);					
				}
			}
		}
		String mysalaryArray[]=mysalarys.split(",");
		
		if(mysalaryArray!=null&&mysalaryArray.length>0)
		{
			for(int i=0;i<mysalaryArray.length;i++)
			{
				String one_salary=mysalaryArray[i];
				String[] one_Array=one_salary.split("`");
				String setname=one_Array[0];
				if(setname==null||setname.length()<=0)
					continue;
				for(int r=0; r< fieldSetTempList.size(); r++)
				{
					FieldSet fieldSet = (FieldSet) fieldSetTempList.get(r);
					String setid = fieldSet.getFieldsetid();
					String setdesc = fieldSet.getCustomdesc();
					if(setid.indexOf(setname)!=-1)
					{
						//--------- 郑文龙 设置字段重复
						//CommonData dataobj = new CommonData(one_salary,setdesc);
						CommonData dataobj = new CommonData(setid,setdesc);
						selectedList.add(dataobj);
						break;
					}
					
				}
				
			}
		}
		/*if(selectedList==null||selectedList.size()<=0)
			selectedList.add(new CommonData("", ""));*/
	    this.getFormHM().put("employ_field_list",fieldSetList);
	    this.getFormHM().put("selected_field_List",selectedList);
	    this.getFormHM().put("old_mysalarys",mysalarys);
	}
    
	
}
