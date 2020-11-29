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
import java.util.HashMap;
/**
 * 
 * <p>Title:总分排名树</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Mar 13, 2009:3:38:47 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class TotalRankTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String model="1";  //1:综合评定(总分排名)  2：查询使用
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			if(hm.get("model")!=null&& "2".equals((String)hm.get("model")))
				model="2";
			String desc="综合评定";
			ConstantXml cx = new ConstantXml(this.getFrameconn(),"ZYXY_PARAM","Params");
			String  kh_set = cx.getTextValue("/Params/kh_set");
			if("2".equalsIgnoreCase(model))
			{
				kh_set=cx.getTextValue("/Params/kh_set_look");
				desc="查询使用";
			}
			
			kh_set=kh_set!=null&&kh_set.trim().length()>0?kh_set:"";
			if(kh_set.length()<1)
				throw new GeneralException(ResourceFactory.getProperty("请设置一下"+desc+"子集后再访问此页面"));
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
					if(fieldset!=null)
					{
						CommonData temp=new CommonData(fieldset.getFieldsetid(),fieldset.getCustomdesc());
						setlist.add(temp);
					}
				}
			}
			String setid = (String)this.getFormHM().get("setid");
			setid=setid!=null&&setid.trim().length()>0?setid:"";
			if(setid.length()<1&&setlist.size()>0){
				CommonData temp=(CommonData)setlist.get(0);
				if(temp!=null)
					setid = temp.getDataValue();
			}
			this.getFormHM().put("setlist",setlist);
			this.getFormHM().put("setid", setid);
			this.getFormHM().put("model",model);
		}catch(Exception ex){
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
}
