package com.hjsj.hrms.transaction.general.template.outtemplate;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.servlet.http.HttpSession;
import javax.sql.RowSet;
import java.util.ArrayList;

public class CreateTemplateOutDataTrans extends IBusiness {

	public void execute() throws GeneralException {
		/**gaohy
		  * 新增情况
		  * 新增求视图情况，并增加{#问卷计划号#}和{# CURRENT_ID#}
		  * **/
		String questionid = (String)this.getFormHM().get("questionid");
        String current_id = (String)this.getFormHM().get("current_id");
        String filetype = (String)this.getFormHM().get("filetype");
        this.getFormHM().put("questionid", questionid);
        this.getFormHM().put("current_id", current_id);
        this.getFormHM().put("filetype", filetype);
		String id=(String)this.getFormHM().get("id");
		String tabid=(String)this.getFormHM().get("tabid");
		RowSet rset =null;
		try{
	       HttpSession session=(HttpSession)this.getFormHM().get("session");
		   String filename=ServletUtilities.createTemplateFile(id,"0",session);
		   /**为了解决任意文件下载漏洞,需要将文件名进行加密begin**/
			filename=PubFunc.encrypt(filename);
		   this.getFormHM().put("templatefile",filename);
		   this.getFormHM().put("tabid",tabid);
			//根据模版id查询模版名称。
		    String SearchNameSql = "select name from t_wf_template where tp_id=?";
			ArrayList sqlList = new ArrayList();
			sqlList.add(id);
			ContentDAO dao = new ContentDAO(this.frameconn);
			rset= dao.search(SearchNameSql, sqlList);
			String fileName=this.userView.getUserName();
			if(rset.next())
				fileName=rset.getString("name")==null?fileName:rset.getString("name")+"_"+fileName;
			this.getFormHM().put("fileName",PubFunc.encrypt(fileName));
		}catch(Exception e)
		{
			e.printStackTrace();
			 throw GeneralExceptionHandler.Handle(e);
		}
		finally {
			PubFunc.closeDbObj(rset);
		}
	}

}
