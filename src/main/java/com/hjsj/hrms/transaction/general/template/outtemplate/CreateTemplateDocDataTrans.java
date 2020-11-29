/**
 * 
 */
package com.hjsj.hrms.transaction.general.template.outtemplate;

import com.hjsj.hrms.businessobject.general.template.templateanalyse.ParseHtml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminDb;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.sql.RowSet;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * @author Owner
 *
 */
public class CreateTemplateDocDataTrans extends IBusiness {
	private ArrayList getInsList(String batch_task,Connection conn)throws GeneralException
	{
		ArrayList inslist=new ArrayList();
		String[] lists=StringUtils.split(batch_task,",");
		StringBuffer strsql=new StringBuffer();
		strsql.append("select ins_id from t_wf_task where task_id in (");
		for(int i=0;i<lists.length;i++)
		{
			if(i!=0)
				strsql.append(",");
			strsql.append(lists[i]);
		}
		strsql.append(")");
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			RowSet rset=dao.search(strsql.toString());
			while(rset.next())
				inslist.add(rset.getString("ins_id"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		return inslist;
	}	
	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {

		String tabid=(String)this.getFormHM().get("tabid");
		//String tabid=request.getParameter("tabid");
		String templatefile=(String)this.getFormHM().get("templatefile");
		String filename=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator") + templatefile;
		String nid="";
		String sp_batch=(String)(String)this.getFormHM().get("sp_batch");
		if(sp_batch==null|| "".equals(sp_batch))
			sp_batch="0";//单个任务审批	
		
		String ins_id=(String)(String)this.getFormHM().get("ins_id");
		String batch_task=(String)(String)this.getFormHM().get("batch_task");
		ArrayList inslist=null;
		
		Connection conn=null;
		try{
			conn=AdminDb.getConnection();

			if("1".equals(sp_batch))
			{
				inslist=getInsList(batch_task,conn);
			}
			else
			{
				inslist=new ArrayList();
				inslist.add(ins_id);
			}			
						
			//sutemplet_39,如果实例号为０，则业务和用户名有关
			String tablename="templet_" + tabid;
			if("0".equals(ins_id))
			  tablename=userView.getUserName() + tablename;
			
  	    ParseHtml parsehtml=new ParseHtml(filename,userView,tabid,inslist,sp_batch,conn);
  	    	LazyDynaBean paramBean=new LazyDynaBean();
	    	Document doc=parsehtml.getTemplateDocument();
	    	String headstr=parsehtml.getTemplateHeadDataValue();
	    	parsehtml.executeTemplateDocument(doc,tablename,nid,paramBean);
	    	String datastr=parsehtml.outTemplateDataDocument(doc);
		   	if(datastr.indexOf("</head>")!=-1)
	    	    datastr=headstr + datastr.substring(datastr.indexOf("</head>") + "</head>".length());
	    	if(datastr.indexOf("</head>".toUpperCase())!=-1)
	    		datastr=headstr + datastr.substring(datastr.indexOf("</head>".toUpperCase()) + "</head>".toUpperCase().length());
			datastr=datastr.replaceAll("wlhxryhrp","&nbsp;");
			datastr=datastr.replaceAll("xrywlh888","<br>");
	    	try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
				pw.print(datastr);
			}
	    	this.getFormHM().put("filename",templatefile);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			try{
				if(conn!=null)
					conn.close();
			}catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		

		
	}
	

}
