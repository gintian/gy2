package com.hjsj.hrms.module.template.historydata.formcorrelation.templatetoolbar.businessobject;

import com.hjsj.hrms.module.template.historydata.formcorrelation.utils.javabean.TemplateParam;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ButtonInfo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:TemplateLayoutBo.java</p>
 * <p>Description>:展现工具栏按钮</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-08-23 上午10:36:32</p>
 * <p>@version: 7.0</p>
 */
@SuppressWarnings("all")
public class TemplateToolBarBo {

	private UserView userview;
	private Connection conn=null;
	public TemplateToolBarBo(Connection conn,UserView userview){
		this.conn = conn;
		this.userview=userview;
	}
	/** todo 考虑按人事、薪资、保险等模块区分权限
	 * 人事异动主界面功能按钮 需要参数 
	 * @return
	 */
	public ArrayList getAllToolButtonList(TemplateParam tableParamBo,HashMap formMap)throws GeneralException{
		//获取参数
		ArrayList toolButtonList = new ArrayList();
		try{
			ButtonInfo wordButton = new ButtonInfo(ResourceFactory.getProperty("button.outword"),"outword(1,1)");
			toolButtonList.add(wordButton);
			wordButton.setIcon("/images/outword.png");
            
            ButtonInfo pdfButton = new ButtonInfo(ResourceFactory.getProperty("template_new.preview")//"预览"
                    ,"outPdf(1,1)");
            toolButtonList.add(pdfButton);
            pdfButton.setIcon("/images/outpdf.png");
            
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return toolButtonList;
	}
}
