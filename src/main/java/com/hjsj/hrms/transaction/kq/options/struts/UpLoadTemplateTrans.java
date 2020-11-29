package com.hjsj.hrms.transaction.kq.options.struts;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
/**
 * 
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jun 28, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class UpLoadTemplateTrans extends IBusiness {

	public void execute() throws GeneralException {
		InputStream inputstream = null;
		try
		{
			String path=(String)this.getFormHM().get("path");
			String template_type=(String)this.getFormHM().get("template_type");
			template_type = PubFunc.hireKeyWord_filter(template_type);
			produceFolder(path);
			FormFile form_file = (FormFile) getFormHM().get("importfile");
			if(!FileTypeUtil.isFileTypeEqual(form_file)){
				throw new Exception(ResourceFactory.getProperty("error.common.upload.invalid"));
			}
			
			if(form_file!=null)
			{
				String desc=form_file.getFileName();
				inputstream=form_file.getInputStream();
				FileOutputStream   fileoutputstream   =  null;
				try{					
					fileoutputstream = new   FileOutputStream(path+"//templatefile//kq//"+desc+""); 
					int k;
					while((k = inputstream.read()) != -1) 
						fileoutputstream.write(k);
					fileoutputstream.close();
				}finally{
					PubFunc.closeIoResource(fileoutputstream);
				}
				String name="";				
				if(template_type!=null&& "Q11".equalsIgnoreCase(template_type))
					name="Kq_template_Q11";
				else if(template_type!=null&& "Q13".equalsIgnoreCase(template_type))
					name="Kq_template_Q13";
				else if(template_type!=null&& "Q15".equalsIgnoreCase(template_type))
					name="Kq_template_Q15";
				else 
					return;
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if(isSaveTemplate(path,dao,name,desc))
			    {
					StringBuffer up=new StringBuffer();
					up.append("update kq_parameter set ");
					up.append("content='"+path+"//templatefile//kq'");
					up.append(",status='1'");
					up.append(",description='"+desc+"'");
					up.append(" where name='"+name+"'");
					up.append(" and B0110='UN'");
					dao.update(up.toString());
			    }
				else
				{
					StringBuffer insert=new StringBuffer();
					insert.append("insert into kq_parameter (name,B0110,content,description,status)");
					insert.append(" values (?,?,?,?,?)");
					ArrayList list=new ArrayList();
					list.add(name);
					list.add("UN");
					list.add(path+"//templatefile//kq");
					list.add(desc);
					list.add("1");
					dao.insert(insert.toString(),list);
				}
			}
			
		}catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeResource(inputstream);
		}
		this.getFormHM().put("tab_name", "tab8");
	}
	/**
	 * 判断路径是否存在
	 * @param path
	 */
	public  void produceFolder(String path)
	{
		if(!(new File(path+"\\templatefile").isDirectory()))
		{
			new File(path+"\\templatefile").mkdirs();
		}
		if(!(new File(path+"\\templatefile\\kq").isDirectory()))
		{
			new File(path+"\\templatefile\\kq").mkdirs();
		}
	}
	private boolean isSaveTemplate(String path,ContentDAO dao,String name,String desc)
	{
		boolean isCorrect=false;
		try{
			String sql="select description from kq_parameter where b0110='UN' and name='"+name+"'";
			this.frowset=dao.search(sql);
			if(this.frowset.next())
			{
				isCorrect=true;				
				File file=new File(SafeCode.decode(path+"\\templatefile\\kq\\"+this.frowset.getString("description")));
				if(file.exists())
					file.delete();
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return isCorrect;
	}
}
