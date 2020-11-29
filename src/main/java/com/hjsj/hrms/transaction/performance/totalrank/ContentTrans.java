package com.hjsj.hrms.transaction.performance.totalrank;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.util.ArrayList;

public class ContentTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			
			String model=(String)this.getFormHM().get("model");
			String desc="综合评定";
			ConstantXml cx = new ConstantXml(this.getFrameconn(),"ZYXY_PARAM","Params");
			String  kh_set = "";
			if("2".equalsIgnoreCase(model))
			{
				kh_set=cx.getTextValue("/Params/kh_set_look");
				desc="查询使用";
			}else{
				kh_set=cx.getTextValue("/Params/kh_set");
			}
			
			kh_set=kh_set!=null&&kh_set.trim().length()>0?kh_set:"";
			if(kh_set.length()<1)
				throw new GeneralException(ResourceFactory.getProperty("请设置一下"+desc+"子集后在访问此页面"));
			String setArr[] = kh_set.split(",");
			ArrayList setlist= new ArrayList();
			for(int i=0;i<setArr.length;i++){
				String setid = setArr[i];
				if(setid!=null&&setid.trim().length()>0){
					if(!this.userView.isSuper_admin()){
						String priv = this.getUserView().analyseTablePriv(setid);
						if("0".equals(priv))
							continue;
						ArrayList checklist=this.userView.getPrivFieldList(setid, Constant.USED_FIELD_SET);
						if(checklist.size()<1)
							continue;
					}
					FieldSet fieldset=DataDictionary.getFieldSetVo(setid);
					CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
					setlist.add(temp);
				}
			}
			String setid = "";
			if(setlist.size()>0){
				CommonData temp=(CommonData)setlist.get(0);
				if(temp!=null)
					setid = temp.getDataValue();
			}
			
			this.getFormHM().put("setlist",setlist);
			this.getFormHM().put("setid", setid);
			this.getFormHM().put("sortitem", "");
			this.getFormHM().put("sortid", "1");
			this.getFormHM().put("highsearch", "");
			this.getFormHM().put("dbname", "");
			this.getFormHM().put("itemid", "");
			this.getFormHM().put("sortid", "");
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
