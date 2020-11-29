/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.servlet.ServletUtilities;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;

/**
 *<p>Title:保存照片</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2010-3-13:下午12:57:53</p> 
 *@author xieguiquan
 *@version 4.0
 */
public class SavePictureTrans extends IBusiness {

	public void execute() throws GeneralException {
		String tabid=(String)this.getFormHM().get("tabid");
		String gridno=(String)this.getFormHM().get("gridno");	
		String tablename=(String)this.getFormHM().get("tablename");
		FormFile file=(FormFile)this.getFormHM().get("picturefile");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
			String fname=file.getFileName();
			if(fname!=null)
			{
		  	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt,fname.length());
		   	 	if(ext!=null && !".bmp".equalsIgnoreCase(ext) && !".jpg".equalsIgnoreCase(ext) && !".jpeg".equalsIgnoreCase(ext)&&!".gif".equalsIgnoreCase(ext))
		   	 	{
		   	 	      throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.info.nophoto"),"",""));
		   	 	}
		   	    //判断文件后缀是否正确  wangcq 2014-12-24
				if(!FileTypeUtil.isFileTypeEqual(file)){
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.info.noalterextension"),"",""));
				}
	   	 	}
			if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
				throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			RecordVo vo = new RecordVo(tablename);			
			updateDAO(vo,file,dao,tabid,gridno,tablename);					
    	}
    	catch(GeneralException e)
		{
    		 e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(e);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void updateDAO(RecordVo vo, FormFile file,ContentDAO dao,String tabid,String gridno,String tablename) throws GeneralException {
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		String filename="";
		try 
		{   
			vo.setInt("tabid",Integer.parseInt(tabid));
			vo.setInt("gridno",Integer.parseInt(gridno));
			if(bflag)
			{
		   	 	String fname=file.getFileName();
		   	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt,fname.length());
			 	/**blob字段保存,数据库中差异*/
			 	switch(Sql_switcher.searchDbServer())
			 	{
			 	   case Constant.ORACEL:
					 	Blob blob = getOracleBlob(vo, file,tablename,tabid,gridno);
					 	vo.setObject("content",blob);
			 			break;
			 	   default:
						byte[] data=ImageBO.imgByte(file, ext);	//wangcq 2014-12-24处理图形文件，返回处理后的流或字节			
						vo.setObject("content",data);
			 			break;
			 	}	
				filename=ServletUtilities.createOleFile(file);
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			dao.updateValueObject(vo);
//			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)//wangcq 2014-12-24不需要单独处理
//			{
//				RecordVo updatevo=new RecordVo(tablename);
//				updatevo.setInt("tabid",Integer.parseInt(tabid));
//				updatevo.setInt("gridno",Integer.parseInt(gridno));
//			 	Blob blob = getOracleBlob(updatevo, file,tablename,tabid,gridno);
//			 	updatevo.setObject("content",blob);	
//			 	cat.debug("reply_insert_boardvo=" + updatevo.toString());
//				dao.updateValueObject(updatevo);
//			}
			this.getFormHM().put("photofile",filename );
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
	private Blob getOracleBlob(RecordVo vo, FormFile file,String tablename,String tabid,String gridno) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select content from ");
		strSearch.append(tablename);
		strSearch.append(" where tabid=");
		strSearch.append(tabid);
		strSearch.append(" and gridno=");
		strSearch.append(gridno);
		strSearch.append(" ");
	
		strSearch.append("  FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(tablename);
		strInsert.append(" set content=EMPTY_BLOB() where tabid=");
		strInsert.append(tabid);
		strInsert.append(" and gridno=");
		strInsert.append(gridno);
		strInsert.append("");
	
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    //wangcq 2014-12-24处理图形文件，返回处理后的流或字节
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),ImageBO.imgStream(file, file.getFileName().substring(file.getFileName().indexOf("."),file.getFileName().length()))); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}	
}
