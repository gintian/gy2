/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;


/**
 *<p>Title:保存照片</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2008-2-13:下午12:57:53</p> 
 *@author cmq
 *@version 4.0
 */
public class SavePictureTrans extends IBusiness {

	public void execute() throws GeneralException {
		String basepre=(String)this.getFormHM().get("basepre");
		String A0100=(String)this.getFormHM().get("a0100");	
		String tablename=(String)this.getFormHM().get("tablename");
		String ins_id=(String)this.getFormHM().get("ins_id");
		FormFile file=(FormFile)this.getFormHM().get("picturefile");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
			String fname=file.getFileName();
			if(fname!=null)
			{
		  	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt,fname.length());
		   	 	String ext1 = fname.substring(indexInt+1);
		   	 	if(ext!=null && !".bmp".equalsIgnoreCase(ext) && !".jpg".equalsIgnoreCase(ext) && !".jpeg".equalsIgnoreCase(ext))
		   	 	{
		   	 	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.info.nophoto"),"",""));
		   	 	}
		   	 	//判断文件后缀是否跟文件类型相符合 add 20180531
		   	    InputStream inStream = file.getInputStream();
		   	 	boolean isOk = FileTypeUtil.isFileTypeEqual(inStream,ext1);
				if(!isOk) {
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.common.upload.invalid")));
				}
	   	 	}
			RecordVo vo = new RecordVo(tablename);			
			updateDAO(vo,file,dao,A0100,basepre,tablename,ins_id);
    	}
    	catch(Exception e)
		{
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
	private void updateDAO(RecordVo vo, FormFile file,ContentDAO dao,String a0100,String basepre,String tablename,String ins_id) throws GeneralException {
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		String filename="";
		try 
		{   
			vo.setString("a0100",a0100);
			vo.setString("basepre",basepre);
			/**审批表中上传照片*/
			if(!(ins_id==null||ins_id.length()==0|| "0".equalsIgnoreCase(ins_id)))
				vo.setInt("ins_id",Integer.parseInt(ins_id));
			if(bflag)
			{
		   	 	String fname=file.getFileName();
		   	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt,fname.length());
				vo.setString("ext",ext);
			 	/**blob字段保存,数据库中差异*/
			 	switch(Sql_switcher.searchDbServer())
			 	{
			 	   case Constant.ORACEL:
//					 	Blob blob = getOracleBlob(vo, file);
//					 	vo.setObject("thefile",blob);
			 			break;
			 	   default:
						byte[] data=file.getFileData();	
						data = ImageBO.imgByte(file, ext);
						vo.setObject("photo",data);
			 			break;
			 	}	
				filename=ServletUtilities.createOleFile(file);
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			dao.updateValueObject(vo);
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				RecordVo updatevo=new RecordVo(tablename);
				updatevo.setString("a0100",a0100);
				updatevo.setString("basepre",basepre);
			 	Blob blob = getOracleBlob(updatevo, file,tablename,a0100,basepre,ins_id);
			 	updatevo.setObject("photo",blob);			
				dao.updateValueObject(updatevo);
			}
			this.getFormHM().put("photofile",SafeCode.encode(PubFunc.encrypt(filename)));
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
	private Blob getOracleBlob(RecordVo vo, FormFile file,String tablename,String a0100,String basepre,String ins_id) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select photo from ");
		strSearch.append(tablename);
		strSearch.append(" where a0100='");
		strSearch.append(a0100);
		strSearch.append("' and basepre='");
		strSearch.append(basepre);
		strSearch.append("' ");
		if(!(ins_id==null||ins_id.length()==0|| "0".equalsIgnoreCase(ins_id)))
		{
			strSearch.append(" and ins_id=");
			strSearch.append(ins_id);
		}
		strSearch.append("  FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set photo=EMPTY_BLOB() where a0100='");
		strInsert.append(a0100);
		strInsert.append("' and basepre='");
		strInsert.append(basepre);
		strInsert.append("'");
		if(!(ins_id==null||ins_id.length()==0|| "0".equalsIgnoreCase(ins_id)))
		{
			strInsert.append(" and ins_id=");
			strInsert.append(ins_id);
		}		
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    String fname=file.getFileName();
   	 	int indexInt=fname.lastIndexOf(".");
   	 	String ext=fname.substring(indexInt,fname.length());
	    InputStream input = ImageBO.imgStream(file, ext);
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),input); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}	
}
