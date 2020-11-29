package com.hjsj.hrms.transaction.performance.kh_system.kh_template;

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
 * <p>Title:ValidateFileTrans.java</p>
 * <p>Description>:ValidateFileTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2008-8-5 上午11:20:31</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class ValidateFileTrans extends IBusiness
{

	public void execute() throws GeneralException 
	{
	    InputStream stream=null;
		try
		{
			KhTemplateBo bo = new KhTemplateBo(this.getFrameconn());
			FormFile file = (FormFile)this.getFormHM().get("templatefile");
			boolean isfile=FileTypeUtil.isFileTypeEqual(file);
			if(!isfile){
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
			}
			//ArrayList list = bo.analyseFileData(file,"per_template.xml", "template_id", "name");
			//this.getFormHM().put("templatelist",list);
			String subsys_id = (String)this.getFormHM().get("subsys_id");
			ArrayList tableNameList = bo.getTableNameList("khtemplate",subsys_id);
			bo.createTreeTable(tableNameList);
			HashMap currentTimeField = new HashMap();
			currentTimeField.put("create_date", "1");
			currentTimeField.put("modify_date", "1");
			stream=file.getInputStream();
			String flag =bo.prepareDataToInsert(stream, "per_template_set.xml", tableNameList, currentTimeField);
			TreeItemView treeItem=new TreeItemView();
			treeItem.setName("root");
			treeItem.setRootdesc("root");
			treeItem.setTitle("root");
			treeItem.setIcon("/images/add_all.gif");	
			treeItem.setTarget("il_body");
			String rootdesc=ResourceFactory.getProperty("lable.kh.template");
				
		    treeItem.setRootdesc(rootdesc);
			treeItem.setText(rootdesc); 
		    treeItem.setLoadChieldAction("/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid=-1&flag=1");
		    treeItem.setAction("javascript:void(0)");
		    treeItem.setXml("/performance/kh_system/kh_template/kh_template_tree/ImportDataTreeServlet?parentid=-1&flag=1");
			
		    this.getFormHM().put("tree",treeItem.toJS());
		    this.getFormHM().put("flag", flag);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally
        {
                 PubFunc.closeIoResource(stream);
        }

	}

}
