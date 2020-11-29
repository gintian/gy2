package com.hjsj.hrms.transaction.sys.cms;

import com.hjsj.hrms.module.recruitment.parameter.businessobject.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.*;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

public class UploadLogoTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
			String type=(String)map.get("type");
			type=type==null?"0":type;
			FormFile file= (FormFile)this.getFormHM().get("logofile");
			FormFile twoFile=(FormFile)this.getFormHM().get("twoFile");
			FormFile oneFile=(FormFile)this.getFormHM().get("oneFile");
			String path=SafeCode.decode((String)this.getFormHM().get("path"));
			if(file!=null)
	    		this.uploadFile(file, path);
			if(twoFile!=null)
				this.uploadFile(twoFile, path);
			if(oneFile!=null)
				this.uploadFile(oneFile, path);
			if("1".equals(type))
			{
				String lfType=(String)this.getFormHM().get("lfType");
				String hbType=(String)this.getFormHM().get("hbType");
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
				parameterXMLBo.setLogoFileType(lfType, hbType);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}
	public void reductionFile(InputStream inputStream,String filePath) throws GeneralException
	  {
		  FileOutputStream fos=null;
		  BufferedOutputStream bos=null;
		  BufferedInputStream bis =null;
		  try   
		  {   
				  ZipInputStream   in   =   new   ZipInputStream(inputStream);  
				  boolean logoFlag=true;
				  boolean webFlag=true;
				  boolean centerflag=true;
				  int BUFFER = 1024;
				  java.util.zip.ZipEntry   entry   =   null;   
			      while ((entry =in.getNextEntry())!=null)   
			      {     
			    	  //会把目录作为一个file读出一次，所以只建立目录就可以，之下的文件还会被迭代到。
					    if (entry.isDirectory())
					    {
					     new File(filePath, entry.getName()).mkdirs(); 
					     continue; 
					    }
					   // BufferedReader ain=new BufferedReader(new InputStreamReader(in));
					   // bis = new BufferedInputStream(zipFile.getInputStream(entry)); 
					    File file = new File(filePath, entry.getName()); 
					    //加入这个的原因是zipfile读取文件是随机读取的，这就造成可能先读取一个文件
					    //而这个文件所在的目录还没有出现过，所以要建出目录来。
					    File parent = file.getParentFile(); 
					    if(parent != null && (!parent.exists())){
					    parent.mkdirs(); 
					    }
					    bis = new BufferedInputStream(in); 
						String entryName=entry.getName(); 
						 //zp_logo.gif---zp_banner_web.gif
						if("header_logo.jpg".equalsIgnoreCase(entryName))
						{
							logoFlag=false;
						}
						if("header_right.jpg".equalsIgnoreCase(entryName))
						{
							webFlag=false;
						}
						if("header_center.jpg".equalsIgnoreCase(entryName))
						{
							centerflag=false;
						}
						 fos = new FileOutputStream(file); 
						 bos = new BufferedOutputStream(fos,BUFFER); 
						   
						 int count; 
						 byte data[] = new byte[BUFFER]; 
						 while ((count = bis.read(data, 0, BUFFER)) != -1)
					    {
						    bos.write(data, 0, count); 
					   }
						 try
						  {
							  if(bos!=null)
							  {
								  bos.flush();
							  }
							  if(bis!=null)
							  {
								  bis.reset();
							  }
							  if(fos!=null)
							  {
								  fos.flush();
							  }
						  }
						  catch(Exception e)
						  {
							  e.printStackTrace();
						  }
						 in.closeEntry();      
			  }
			      if(logoFlag||webFlag||centerflag)
			      {
			    	  throw GeneralExceptionHandler.Handle(new Exception("上传文件中没找到所需文件"));
			      }
			      
			      
			  in.close();
		    
		  }   
		  catch   (IOException   e)   {   
			  e.printStackTrace();
		  }  
		  finally
		  {
		  	  PubFunc.closeResource(bos);
			  PubFunc.closeResource(bis);
			  PubFunc.closeResource(fos);
		  }
	  }
	public void uploadFile(FormFile formfile,String path)
	{
		 FileOutputStream fos=null;
		 BufferedOutputStream bos=null;
		 BufferedInputStream bis =null;
		 InputStream _in = null;
		try
		{
			_in = formfile.getInputStream();
			 File file = new File(path, formfile.getFileName()); 
			 int BUFFER = 1024; 
			 bis = new BufferedInputStream(_in); 
			 fos = new FileOutputStream(file); 
			 bos = new BufferedOutputStream(fos,BUFFER); 
			 int count; 
			 byte data[] = new byte[BUFFER]; 
			 while ((count = bis.read(data, 0, BUFFER)) != -1)
		    {
			    bos.write(data, 0, count); 
		   }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		  {
			  PubFunc.closeIoResource(bos);
			  PubFunc.closeIoResource(bis);
			  PubFunc.closeIoResource(fos);
			  PubFunc.closeIoResource(_in);
		  }
	}

}
