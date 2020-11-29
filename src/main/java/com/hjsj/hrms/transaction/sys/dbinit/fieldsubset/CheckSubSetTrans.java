package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:验证子集代码是否重名字</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:</p>
 * @author wangyao
 * @version 1.0
 *
 */

public class CheckSubSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try
		{
			String msg = "1";
			String code = (String)this.getFormHM().get("code");
			String name = (String)this.getFormHM().get("name");
			SubSetBo subset = new SubSetBo(this.getFrameconn());
			
			boolean flag=true;
			if(subset.checkcode(code)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			if(subset.checkname(name)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew"); 
			}
			//判断子集名称是否与子集分类名称相同     jingq  add   2014.5.8
			String tag = "set_a";
			SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
			ArrayList list = infoxml.getView_tag(tag);   //获取子集分类名称集合
			for (int i = 0; i < list.size(); i++) {
				if(name.equals(list.get(i))){
					flag=false;
					msg=ResourceFactory.getProperty("kjg.error.clew"); 
					break;
				}
			}
			this.getFormHM().put("msg",msg);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

}
