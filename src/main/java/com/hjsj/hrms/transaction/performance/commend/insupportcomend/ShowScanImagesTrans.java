package com.hjsj.hrms.transaction.performance.commend.insupportcomend;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @author liweichao
 *
 */
public class ShowScanImagesTrans extends IBusiness{

	/**查看扫面图像 和 删除服务器临时图像*/
	public void execute() throws GeneralException {
		StringBuffer scans=new StringBuffer();
		File tempFile = null;
        String filename="";
		createTempDir();//创建临时目录
		
		//是否删除生成的临时图像scanFlag=1是
		String scanFlag=(String) this.getFormHM().get("scanFlag");
		this.getFormHM().put("scanFlag", "");
		InputStream in = null;
		java.io.FileOutputStream fout = null;
        try {
        	if(scanFlag!=null&& "1".equals(scanFlag)){
        		String scanArray=(String) this.getFormHM().get("scanArray");
        		String separator = System.getProperty("file.separator");//分隔符
        		String tmpdir = System.getProperty("java.io.tmpdir");   //临时目录
        		
        		if(scanArray!=null&&scanArray.length()>0){
        			String[] sc=scanArray.split(",");
					for (int j = 0; j < sc.length; j++) {
						if(sc[j]!=null&&sc[j].length()>0){
							File file = new File(tmpdir + separator + sc[j]);
							//删除文件
							if (file.isFile())
    							file.delete();
						}
					}
        		}
        	}else{
	        	ContentDAO dao =new ContentDAO(this.getFrameconn());
	            StringBuffer strsql = new StringBuffer();
	            String plan_id=(String)this.getFormHM().get("p0201");
	            String object_id=(String)this.getFormHM().get("a0100");
	            String object_nbase=(String)this.getFormHM().get("nbase");
	            
	            //测试sql strsql.append("select ext,Ole from UsrA00 where Flag='P'");
	            strsql.append("select content,ext from per_scanimage");
	            strsql.append(" where image_id in(select image_id from per_scanimage_object where plan_type=1");
	            strsql.append(" and plan_id="+plan_id);
	            strsql.append(" and object_id='"+object_id+"'");
	            strsql.append(" and object_nbase='"+object_nbase+"')");
	            this.frecset=dao.search(strsql.toString());
	
	            while (this.frecset.next()) {
	                //createTempFile(name,postfix,path);
	                tempFile = File.createTempFile("scan-", this.frecset.getString("ext"),
	                        new File(System.getProperty("java.io.tmpdir")));
	                in = this.frecset.getBinaryStream("content");
	                fout = new java.io.FileOutputStream(tempFile);
	                
	                int len;
	                if(in!=null){
	                	byte buf[] = new byte[1024];
	                	while ((len = in.read(buf, 0, 1024)) != -1) {
	                		fout.write(buf, 0, len);
	                	}
	                	filename= tempFile.getName(); 
	                	scans.append(filename+",");
	                }
	                fout.close();
	            }
        	}
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PubFunc.closeResource(fout);  
            PubFunc.closeResource(in);  
        }
        this.getFormHM().put("scans", scans.toString());
	}
	
	/** 创建临时文件夹 */
    public static void createTempDir() {
        String tempDirName = System.getProperty("java.io.tmpdir");
        if (tempDirName == null) {
            throw new RuntimeException("Temporary directory system property (java.io.tmpdir) is null.");
        }
        // create the temporary directory if it doesn't exist
        File tempDir = new File(tempDirName);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }
}
