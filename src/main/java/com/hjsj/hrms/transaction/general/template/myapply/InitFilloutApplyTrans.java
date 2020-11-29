/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.myapply;

import com.hjsj.hrms.businessobject.general.template.TemplatePageBo;
import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 25, 2008:1:17:57 PM</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class InitFilloutApplyTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try
		{
			String tabid=(String)this.getFormHM().get("tabid");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			CheckPrivSafeBo checkPrivSafeBo = new CheckPrivSafeBo(this.frameconn,this.userView);
			//tabid=checkPrivSafeBo.checkResource(7, tabid);
			
			if(hm.get("businessModel")!=null)
				this.getFormHM().put("businessModel",(String)hm.get("businessModel"));
			else
				this.getFormHM().put("businessModel","0");
			hm.remove("businessModel");
			
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			//查找模板中是否有多个相同变化后子集
			tablebo.checkIsHaveMutilSub();
			/**创建或修临时表*/
			/**发起流程时才需要创建临时表,审批环节不用创建临时表*/
			tablebo.createTempTemplateTable();
			ArrayList list=tablebo.getAllTemplatePage();
			ArrayList outlist=new ArrayList();
			ArrayList noprintlist=new ArrayList();
			ArrayList mobileList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				TemplatePageBo pagebo=(TemplatePageBo)list.get(i);
				//if(pagebo.isIsprint())
				//	outlist.add(pagebo);
				//else
				//   noprintlist.add(pagebo); //以前考核意见审批表,用不打印的表格来处理，这种方式有点久妥
				
				//#模板是否显示不打印页
				if(!pagebo.isShow())
					continue;
				//bs端如果isMobile为1,则不显示 1代表专门为手机端做的模板页
				if("1".equals(pagebo.getIsMobile())){
					mobileList.add(pagebo);
				    continue;
				}
				outlist.add(pagebo);
			} 
			if (outlist.size()<1){
                if (mobileList.size()>0) {//有手机用的模板，取手机模板 wangrd 2015-05-27
                    outlist= mobileList;
                }
                else 
                  throw new GeneralException("此模板的页签显示设置错误！");
            }
			this.getFormHM().put("allow_def_flow_self",String.valueOf(tablebo.isDef_flow_self(0)));
			this.getFormHM().put("def_flow_self",tablebo.getDef_flow_self());//自定义审批流程
			this.getFormHM().put("pagelist",outlist);
			this.getFormHM().put("name",tablebo.getName());
			this.getFormHM().put("noprintlist",noprintlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}

	}

}
