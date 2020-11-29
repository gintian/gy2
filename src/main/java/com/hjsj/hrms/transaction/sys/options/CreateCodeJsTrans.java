/**
 * 
 */
package com.hjsj.hrms.transaction.sys.options;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>Title:CreateCodeJsTrans</p>
 * <p>Description:创建前台代码字典</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-1-24:9:05:26</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class CreateCodeJsTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String jsdir=(String)this.getFormHM().get("jsdir");
		cat.debug("javascript directory="+jsdir);
	    String pattern="\r\n";		
		StringBuffer strC=new StringBuffer();
		strC.append("var dm=new Array();");
		strC.append(pattern);
		ArrayList list=AdminCode.getCodeItemList();
		for(int i=0;i<list.size();i++)
		{
			CodeItem item=(CodeItem)list.get(i);
			strC.append("dm[dm.length]={id:\"");
			strC.append(item.getCodeid());
			strC.append("\",");
			strC.append("value:\"");
			strC.append(item.getCodeitem());
			strC.append("\",");
			strC.append("name:\"");
			strC.append(item.getCodename());
			strC.append("\"};");
			strC.append(pattern);			
		}
		strC.append("for(var i=0;i<dm.length;i++)");
		strC.append(pattern);
		strC.append("{");
		strC.append("dm[\"_\"+dm[i].id+dm[i].value]=dm[i];");	
		strC.append("}");		
		createJsFile(strC.toString(),jsdir);
	}

	private void createJsFile(String strC, String filepath) throws GeneralException {
		FileWriter filew = null;
		try {
			filew = new FileWriter(filepath + "/dm.js", true);
			filew.write(strC);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			throw GeneralExceptionHandler.Handle(ioe);
		} finally {
			PubFunc.closeResource(filew);
		}
	}	
}
