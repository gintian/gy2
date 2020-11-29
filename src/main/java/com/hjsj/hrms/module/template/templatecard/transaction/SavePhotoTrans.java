/**
 * 
 */
package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateUtilBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateFrontProperty;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;

/**
 * <p>Title:SavePhotoTrans.java</p>
 * <p>Description>:保存照片</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2015-12-22 下午01:57:41</p>
 * <p>@version: 7.0</p>
 */
public class SavePhotoTrans extends IBusiness {

	@Override
    public void execute() throws GeneralException {
		try {
			String type = (String) this.getFormHM().get("type");
			if ("getEncryFileName".equals(type)) {// 返回加密的文件名
				String fileName = (String) this.getFormHM().get("filename");
				//fileName = PubFunc.encrypt(fileName);
				fileName = SafeCode.encode(fileName);
				this.getFormHM().put("filename", fileName);
				return;
			} else {//保存
				String fileName = (String) this.getFormHM().get("filename");
				fileName =PubFunc.decrypt(fileName);
				String fileid = (String) this.getFormHM().get("fileid");
				/*fileName =SafeCode.decode(fileName);
				*/
				String fillInfo = (String) this.userView.getHm().get("fillInfo");
				String object_id = (String) this.getFormHM().get("object_id");
				object_id = PubFunc.decrypt(object_id);//lis 20160428
	            TemplateFrontProperty frontProperty =new TemplateFrontProperty(this.getFormHM());            
	            String tabId = frontProperty.getTabId();
	            String taskId = frontProperty.getTaskId(); 
	            String moduleid = frontProperty.getModuleId();
				int i = object_id.indexOf("`");
				TemplateDataBo templateDataBo=new TemplateDataBo(this.getFrameconn(),this.userView,Integer.parseInt(tabId));
				templateDataBo.setFileName(fileName);
				if (i>0){
					String basepre=object_id.substring(0,i);
					String a0100=object_id.substring(i+1);
					try {
						if (fileName != null && fileName.length()>0) {
						    if ("0".equals(taskId)){
						    	String tablename= this.userView.getUserName()+"templet_"+tabId;
						    	if("1".equals(fillInfo)||"9".equals(moduleid)){
						    		tablename= "g_templet_"+tabId;
						    	}
						        RecordVo vo = new RecordVo(tablename);
						        templateDataBo.updateDAO(vo, fileid, a0100, basepre,"0", tablename);
						    }
						    else {
						        TemplateUtilBo utilBo = new TemplateUtilBo(this.frameconn,this.userView);
						        String tablename= "templet_"+tabId;
                                RecordVo vo = new RecordVo(tablename);
                                String ins_id= utilBo.getInsId(taskId);
                                templateDataBo.updateDAO(vo, fileid, a0100, basepre,ins_id, tablename);
						    }
						    
						}
					} catch (GeneralException e) {
						e.printStackTrace();
						throw GeneralExceptionHandler.Handle(e);
					}
				}
			}

		} catch (GeneralException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	

	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void updateDAO(RecordVo vo, String filename,String a0100,String basepre,String ins_id,
	        String tablename) throws GeneralException {
		try 
		{   
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			vo.setString("a0100",a0100);
			vo.setString("basepre",basepre);
			if (!"0".equals(ins_id)){
			    vo.setString("ins_id",ins_id);
			}
	   	 	String fname=filename;
	   	 	int indexInt=fname.lastIndexOf(".");
	   	 	String ext=fname.substring(indexInt,fname.length());
			vo.setString("ext",ext);
			String tempdir=System.getProperty("java.io.tmpdir");
			String pathFileName=tempdir+File.separator+filename;//带路径的文件名
		 	switch(Sql_switcher.searchDbServer())
		 	{
		 	   case Constant.ORACEL:
				 	Blob blob = getOracleBlob(pathFileName,tablename,a0100,basepre);
				 	vo.setObject("photo",blob);			
					dao.updateValueObject(vo);
		 			break;
		 	   default:
		 		   File file=new File(pathFileName);//要转换的文件  
		 		   FileInputStream inputStream=new FileInputStream(file);  
					vo.setObject("photo",inputStream);
					dao.updateValueObject(vo);
		 			break;
		 	}	
			
		}	
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);			
		}
	}	

	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(String pathFileName,String tablename,String a0100,String basepre) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select photo from ");
		strSearch.append(tablename);
		strSearch.append(" where a0100='");
		strSearch.append(a0100);
		strSearch.append("' and basepre='");
		strSearch.append(basepre);
		strSearch.append("' ");
		strSearch.append("  FOR UPDATE");		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set photo=EMPTY_BLOB() where a0100='");
		strInsert.append(a0100);
		strInsert.append("' and basepre='");
		strInsert.append(basepre);
		strInsert.append("'");	
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    File file=new File(pathFileName);//要转换的文件  
		FileInputStream inputStream=new FileInputStream(file);  
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),inputStream); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}	
	
}
