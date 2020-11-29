package com.hjsj.hrms.transaction.hire.demandPlan.positionDemand;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

/**
 * 
*    
* 
* 类名称：ExportDemandZipTrans   
* 类描述：  导出加密zip压缩包 
* 创建人：akuan   
* 创建时间：Aug 5, 2013 9:21:59 AM   
* 修改人：akuan   
* 修改时间：Aug 5, 2013 9:21:59 AM   
* 修改备注：   
* @version    
*
 */

public class ExportDemandZipTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String zipName="";
		String name="";
		String info="ok";
		String outname="";
		try{
		   /**安全平台改造,防止任意文件下载漏洞,将加密的文件名解密回来**/
		   name=PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("name")));
		   name=name.substring(0, name.lastIndexOf(".")+1)+"zip";
		   outname=name;
		   zipName=System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + name;
		   /*删除生成的zip文件 防止temp文件夹冗杂*/
	       File zipFileName = new File(zipName);
	       if(zipFileName.exists()){
	    	   zipFileName.delete();
	       }
		   name= System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + PubFunc.decrypt(SafeCode.decode((String) this.getFormHM().get("name")));
		   String clientName=SystemConfig.getPropertyValue("clientName");
	       ZipFile zipFile = new ZipFile(zipName); //
	       ArrayList fileAddZip = new ArrayList(); // 向zip包中添加文件集合   
	       fileAddZip.add(new File(name)); // 向zip包中添加一个文件   
	       final ZipParameters parameters = new ZipParameters(); // 设置zip包的一些参数集合
	       if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)){
	           parameters.setEncryptFiles(true); // 是否设置密码（此处设置为：是）
	           parameters.setPassword("hjsj2013"); // 压缩包密码
	           parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD); // 加密级别
	       }
	       parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE); // 压缩方式(默认值)   
	       parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL); // 普通级别（参数很多）   
	       zipFile.createZipFile(fileAddZip, parameters); // 创建压缩包完成 
	       File file=new File(name);
	       /*删除生成的excel文件 防止temp文件夹冗杂*/
	       if(file.exists()){
	    	   file.delete();
	       }
	       
		}catch(Exception e){	
			info="error";
			e.printStackTrace();	
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			/**安全平台改造,防止任意文件下载漏洞**/
			this.getFormHM().put("name", SafeCode.encode(PubFunc.encrypt(outname)));
			this.getFormHM().put("info", info);
		}

	}

}
