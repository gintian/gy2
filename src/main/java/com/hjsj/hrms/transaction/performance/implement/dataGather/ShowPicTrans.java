package com.hjsj.hrms.transaction.performance.implement.dataGather;

import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

/**
 * <p>Title:ShowPicTrans.java</p>
 * <p>Description:考核实施/数据采集 显示图像</p>
 * <p>Company:hjsj</p>
 * <p>create time:2010-12-17 11:11:11</p> 
 * @author JinChunhai
 * @version 1.0
 */

public class ShowPicTrans extends IBusiness 
{

	public void execute() throws GeneralException 
	{
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String picWidth = (String)hm.get("picWidth");   // 图片的宽度
		String picHeight = (String)hm.get("picHeight"); // 图片的高度
		
		String planId = (String)this.getFormHM().get("planId");
		String mainbody_id = (String)this.getFormHM().get("mainbody_id");
		mainbody_id = PubFunc.decrypt(mainbody_id);
		String object_id = (String)this.getFormHM().get("object_id");
		object_id = PubFunc.decrypt(object_id);
		
		ContentDAO dao = new ContentDAO(this.frameconn);
		java.io.FileOutputStream fout = null; 
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append("select * from per_scanimage where image_id=(");
			buf.append("select image_id from per_scanimage_object where plan_id="+planId);
			buf.append(" and mainbody_id='"+mainbody_id+"' and object_id='"+object_id+"'");
			DbWizard dbWizard = new DbWizard(this.frameconn);
			if (dbWizard.isExistField("per_scanimage_object", "plan_type", false))
				buf.append(" and plan_type=0");
			buf.append(" )");
			this.frowset=dao.search(buf.toString());
			String filepathname ="";
			if(this.frowset.next())
			{
				String ext = this.frowset.getString("ext").substring(1);
				InputStream in = this.frowset.getBinaryStream("content");

				
				File tempFile = File.createTempFile(ServletUtilities.tempFilePrefix,ext,
                        new File(System.getProperty("java.io.tmpdir")));             
                if(in==null)
                	return ;
                fout = new java.io.FileOutputStream(tempFile);                
                int len;
                byte buf1[] = new byte[1024];
            
                while ((len = in.read(buf1, 0, 1024)) != -1)                
                    fout.write(buf1, 0, len); 
                
                //fout.close();
               
                // 浏览器显示图片的时候不支持"/"做分隔符 lium
//                Properties props=System.getProperties(); //系统属性
//                if(props.getProperty("os.name").startsWith("Win"))
//                	filepathname =filepathname.replace("\\", "/");			
				
                filepathname = tempFile.getPath();
                filepathname = filepathname.replace(File.separator, "\\");
			}			
			// 客户端需要动过servlet请求图片才可以显示，而不可以使用服务器图片的物理地址 lium
			this.getFormHM().put("picSrc", "/servlet/DisplayOleContent?filePath=" + filepathname);	
			this.getFormHM().put("picWidth",picWidth);
			this.getFormHM().put("picHeight",picHeight);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		finally{
		    PubFunc.closeIoResource(fout);
		}
		
	}

}
