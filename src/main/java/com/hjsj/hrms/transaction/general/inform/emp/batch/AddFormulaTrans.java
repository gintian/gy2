package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.gz.TempvarBo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author lilinbing
 * @version 1.0
 * 
 */
public class AddFormulaTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String infor = (String)reqhm.get("infor");
		String issetid=(String)reqhm.get("issetid");
		String setid=(String)reqhm.get("setid");
		infor=infor!=null&&infor.trim().length()>0?infor:"1";
		TempvarBo tempvarbo = new TempvarBo();
		ArrayList fieldsetlist = tempvarbo.fieldList(this.userView,infor);
		if("1".equals(infor)){
            fieldsetlist = this.userView.getPrivFieldSetList(Constant.USED_FIELD_SET);
        }
		if(issetid!=null&& "1".equals(issetid))
		{
			if(fieldsetlist.size()>0)
			{
				if((fieldsetlist.get(0))  instanceof CommonData )
				{
					ArrayList list = new ArrayList();
					for(int i=0;i<fieldsetlist.size();i++)
					{
						CommonData cd = (CommonData)fieldsetlist.get(i);
						if(cd.getDataValue().equalsIgnoreCase(setid))
							list.add(cd);
					}
					fieldsetlist=list;
				}
			}
		}
		if ("4".equals(infor)) {
			ArrayList list = new ArrayList();
			for(int i=0;i<fieldsetlist.size();i++)
			{
				CommonData cd = (CommonData)fieldsetlist.get(i);
				if("A01".equalsIgnoreCase(cd.getDataValue())) {
					continue;
				} else {
					list.add(cd);
				}
			}
			fieldsetlist=list;
		}
		if("1".equals(infor)){
			if(fieldsetlist!=null){
				ArrayList list = new ArrayList();
				for(int i=0;i<fieldsetlist.size();i++){
					FieldSet fs = (FieldSet)fieldsetlist.get(i);
					/*if("1".equalsIgnoreCase(this.userView.analyseTablePriv(fs.getFieldsetid()))){//读权限
						continue;
					}*/
					if("A00".equalsIgnoreCase(fs.getFieldsetid())){
						continue;
					}
					CommonData cd = new CommonData(fs.getFieldsetid(),fs.getCustomdesc());
					list.add(cd);
				}
				fieldsetlist=list;
			}
        }
		this.getFormHM().put("fieldsetlist",fieldsetlist);
		this.getFormHM().put("infor", infor);
	}

}
