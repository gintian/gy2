package com.hjsj.hrms.transaction.gz.gz_accounting.tax;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.io.File;
import java.util.ArrayList;
/**
 * 
 *<p>Title:TaxExportTemplateTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Apr 1, 2008</p> 
 *@author FengXiBin
 *@version 4.0
 */
public class TaxExportTemplateTrans extends IBusiness{
	
	public void execute()throws GeneralException 
	{
		String path=(String)this.getFormHM().get("path");
		if(path!=null&&!"".equals(path))
		{
		    path = PubFunc.decrypt(SafeCode.decode((String)this.getFormHM().get("path")));
            ArrayList fileNameList = this.getFileNameList(path);
	    	ArrayList templateList = this.getTemplateList(path);
	    	String condtionsql=(String)this.getFormHM().get("condtionsql");
	    	condtionsql=PubFunc.keyWord_reback(condtionsql);
	    	/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 start */
	    	condtionsql = PubFunc.decrypt(SafeCode.decode(condtionsql));
	    	/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 end */
	    	this.getFormHM().put("fileNameList",fileNameList);
	    	this.getFormHM().put("templateList",templateList);
	    	this.getFormHM().put("checkResult",this.checkTemplate(path));
	    	/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 start */
	    	this.getFormHM().put("condtionsql", condtionsql);
	    	/* 安全问题 sql-in-url 所得税管理 xiaoyun 2014-9-12 end */
		}
		else
			this.getFormHM().put("checkResult", "0");
		
	}
	public String checkTemplate(String path)
	{
		String ret = "";
		File files=new File(path);	
		File fileItem[] =files.listFiles();
		if(fileItem.length>0)
		{		
			ret = "1";
		}else
			ret = "0";
		return ret;
	}
	public ArrayList getTemplateList(String path)
	{
		ArrayList filelist = new ArrayList();
		File t_file=new File(path);		
		if(!(t_file.isDirectory()))
		{
			t_file.mkdirs();			
		}
		File files=new File(path);	
		File fileItem[] =files.listFiles();
		LazyDynaBean bean = new LazyDynaBean();
		bean.set("filename","请选择...");
		filelist.add(bean);
		if(fileItem.length>0)
		{
			for(int i=0;i<fileItem.length;i++)
			{
				bean = new LazyDynaBean();
				bean.set("type","1");
				bean.set("filename",fileItem[i].getName());
				filelist.add(bean);
			}
		}
		return filelist;
	}
	public ArrayList getFileNameList(String path)
	{
		ArrayList filelist = new ArrayList();
		File t_file=new File(path);		
		if(!(t_file.isDirectory()))
		{
			t_file.mkdirs();			
		}
		File files=new File(path);	
		File fileItem[] =files.listFiles();
		if(fileItem.length>0)
		{
			for(int i=0;i<fileItem.length;i++)
			{
				LazyDynaBean bean = new LazyDynaBean();
				bean.set("filename",fileItem[i].getName());
				filelist.add(bean);
			}
		}
		return filelist;
	}

}
