/**
 * 
 */
package com.hjsj.hrms.businessobject.general.ftp;

import com.hrms.struts.constant.SystemConfig;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileOutputStream;

/**
 *<p>Title:FtpAccessBo</p> 
 *<p>Description:ftp访问，存和取</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-10-11:下午04:37:13</p> 
 *@author cmq
 *@version 4.0
 */
public class FtpAccessBo {
	
	private FTPClient ftp;
	
	public FtpAccessBo() {
		ftp = new FTPClient();
		try
		{
			String serverIP = SystemConfig.getProperty("ftpserver");
			String userName = SystemConfig.getProperty("ftp_user");
			String passWord = SystemConfig.getProperty("ftp_pwd");
			String port =  SystemConfig.getProperty("ftpport");		
			ftp.setDefaultPort(Integer.parseInt(port));
			ftp.connect(serverIP);
			ftp.login(userName,passWord);	
			ftp.setFileType(FTP.BINARY_FILE_TYPE); 
			ftp.enterLocalPassiveMode(); 			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	/**
	 * 取得ftp服务器上的文件
	 * @param filename
	 */
	public String getFile(String filename,String ext)
	{
	 String outfilename=null;	
	 try
	 {
		if(ftp.isConnected())
		{
	        File tempFile = null;
	   	    FileOutputStream fout=null; 
	        //BufferedOutputStream buffOut=null;	   	    
	        try
	        {
                tempFile = File.createTempFile("e_archive", ext, new File(System.getProperty("java.io.tmpdir")));	
                fout = new java.io.FileOutputStream(tempFile);
                //buffOut=new BufferedOutputStream(fout);
                ftp.retrieveFile(filename, fout);
	            
	            outfilename=System.getProperty("java.io.tmpdir")+System.getProperty("file.separator")+tempFile.getName();
	        }catch(Exception e){
	            e.printStackTrace();
	        }finally{
	            try{
	                if(fout!=null) {
                        fout.close();
                    }
	                if(ftp!=null)
	                {
	                	ftp.logout();
	                	ftp.disconnect();
	                }
	            }catch(Exception e){
	                	e.printStackTrace();
	            }
	        }
		}
	 }
	 catch(Exception ex)
	 {
		 ex.printStackTrace();
	 }
	 return outfilename;
	}
}
