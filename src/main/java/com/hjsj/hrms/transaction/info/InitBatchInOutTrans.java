package com.hjsj.hrms.transaction.info;

import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * 显示批量导入界面
 * @author xujian
 *Apr 21, 2010
 */
public class InitBatchInOutTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList dataList = new ArrayList();
		try{
			
			ArrayList fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
			if(fieldsetlist!=null){
				for(int i=0;i<fieldsetlist.size();i++){
					FieldSet fs = (FieldSet)fieldsetlist.get(i);
					if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限
						continue;
					}
					if("A00".equalsIgnoreCase(fs.getFieldsetid())){
						continue;
					}
					CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
					dataList.add(cd);
				}
			}
            if(dataList.size()==0)
			{
			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("workbench.info.batchinout.nopriv")));
			}
            this.getFormHM().put("fieldSetDataList", dataList);
            
            /**
   		  * 身份证    zhaogd 2013-11-27 获取自定义唯一性指标集内容
   		  */
         HashMap hm=this.getFormHM();
   		 GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
   		 ArrayList chklist = gf.getUsedFieldBySetNameTransOutNum("A01",this.userView);
   		 hm.put("fielditemlist",chklist);
			
		}catch(Exception e){
		    throw GeneralExceptionHandler.Handle(e);
		}
	}

}
