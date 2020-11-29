package com.hjsj.hrms.transaction.performance.kh_system.kh_field;

import com.hjsj.hrms.businessobject.performance.kh_system.kh_template.KhTemplateBo;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.tree.TreeItemView;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:ImportDataTrans.java</p>
 * <p>Description:绩效管理导入考核指标</p>
 * <p>Company:hjsj</p>
 * <p>create time:2009-10-29 11:11:11</p>
 * @author JinChunhai
 * @version 1.0
 */

public class ImportDataTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{	
		InputStream in = null;
		try
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			FormFile file = (FormFile)this.getFormHM().get("fieldfile");
			boolean isfile=FileTypeUtil.isFileTypeEqual(file);
			if(!isfile){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			//ArrayList list = bo.analyseFileData(file,"per_template.xml", "template_id", "name");
			//this.getFormHM().put("templatelist",list);
			String subsys_id = (String)this.getFormHM().get("subsys_id");	
			String pointsetid = (String)this.getFormHM().get("pointsetid");
			ArrayList tableNameList = bo.getTableNameList("khpoint",subsys_id);
			bo.createTreeTable(tableNameList);
			HashMap currentTimeField = new HashMap();
			currentTimeField.put("create_date", "1");
			currentTimeField.put("modify_date", "1");
			in = file.getInputStream();
			String flag =bo.prepareDataToInsert(in, "per_pointset.xml", tableNameList, currentTimeField);
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc=ResourceFactory.getProperty("kh.field.class");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid=-1&flag=0");
		    treeItem.setAction("javascript:void(0)");
		    treeItem.setXml("/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid=-1&flag=0");
			
		    this.getFormHM().put("tree",treeItem.toJS());
		    this.getFormHM().put("flag", flag);
		    this.getFormHM().put("pointsetid", pointsetid);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
			PubFunc.closeIoResource(in);
		}
		
	}

}
