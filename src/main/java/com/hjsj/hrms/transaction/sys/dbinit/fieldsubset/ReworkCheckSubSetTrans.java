package com.hjsj.hrms.transaction.sys.dbinit.fieldsubset;

import com.hjsj.hrms.businessobject.sys.fieldsubset.SubSetBo;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
/**
 * 
 * <p>Title:修改检查子集名称</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 4, 2008:4:58:33 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class ReworkCheckSubSetTrans extends IBusiness{

	public void execute() throws GeneralException {
		try{
			String msg = "1";
			String fieldsetdesc = (String)this.getFormHM().get("fieldsetdesc");
			String customdesc = (String)this.getFormHM().get("customdesc");
			String code = (String)this.getFormHM().get("code");
			SubSetBo subset = new SubSetBo(this.getFrameconn());
			
			boolean flag=true;
			if(subset.checkupfieldname(code, fieldsetdesc)){
				flag=false;
				msg=ResourceFactory.getProperty("kjg.error.clew");
			}
			if(subset.checkupcustom(code, customdesc)){
				flag = false;
				msg=ResourceFactory.getProperty("kjg.error.clew");
			}
			
			// 刷新系统参数中的子集信息
			SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.frameconn);
			//判断子集名称是否与子集分类名称相同     guodd  add   2014.12.26
			String tag = "set_a";
			ArrayList list = infoxml.getView_tag(tag);   //获取子集分类名称集合
			for (int i = 0; i < list.size(); i++) {
				if(customdesc.equals(list.get(i))){
					flag=false;
					msg=ResourceFactory.getProperty("kjg.error.clew"); 
					break;
				}
			}
			if("1".equals(msg))//检查通过，再同步排序
				infoxml.reOrederSet();
			this.getFormHM().put("msg", msg);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
