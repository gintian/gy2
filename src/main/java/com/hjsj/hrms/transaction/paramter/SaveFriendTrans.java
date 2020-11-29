/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.paramter;

import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
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
import java.sql.PreparedStatement;

/**
 * <p>Title:SaveFriendTrans</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveFriendTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	@Override
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("friendvo");
        if(vo==null) {
			return;
		}
        String flag=(String)this.getFormHM().get("flag");
        FormFile file = (FormFile)this.getFormHM().get("file");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**新增友情链接，进行保存处理*/
        	insertDAO(vo,file,dao);
        }
        else if("0".equals(flag))
        {
	        /**点编辑链接后，进行保存处理*/
	        update(vo, file);
        }  	
		/**清空*/
		vo.removeValues();
	}
	
	private void update(RecordVo vo,FormFile file)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		PreparedStatement pstmt=null;
		boolean bflag=true;
		if(file==null||file.getFileSize()==0) {
			bflag=false;
		}
		try
		{
			 if(bflag)
			 {
				 strsql.append("update hr_friend_website set url = ?,name = ?,log_icon =?, ext=? where site_id=?");
			 }
			 else
			 {
				 strsql.append("update hr_friend_website set url = ?,name = ? where site_id=?");
			 }
			 pstmt=this.getFrameconn().prepareStatement(strsql.toString());
			 pstmt.setString(1, vo.getString("url"));
			 pstmt.setString(2, vo.getString("name"));
			 if(bflag)
			 {
				 	String fname=file.getFileName();
		   	 		int indexInt=fname.lastIndexOf(".");
		   	 		String ext=fname.substring(indexInt+1,fname.length());	
		   	 		
				 	/**blob字段保存,数据库中差异*/
				 	switch(Sql_switcher.searchDbServer())
				 	{
				 	   case Constant.ORACEL:
						 	Blob blob = getOracleBlob(vo, file);
				 			pstmt.setBlob(3,blob);
				 			break;
				 	   default:
				 		    //保存图片处理 jingq upd 2014.09.26
				 		    InputStream im = ImageBO.imgStream(file, ext);
							pstmt.setBinaryStream(3,im,im.available());
				 			break;
				 	}
						
		   	 		pstmt.setString(4,ext);
		   	 		pstmt.setInt(5, Integer.parseInt(vo.getString("site_id")));	
			 }
			 else
			 {
			 	pstmt.setInt(3, Integer.parseInt(vo.getString("site_id")));			 
			 }
			 pstmt.executeUpdate();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);				
		}
		finally
		{
			PubFunc.closeResource(pstmt);
		}
	}
	
	 /**
     * 
     * @param vo
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
	private Blob getOracleBlob(RecordVo vo, FormFile file) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select log_icon from hr_friend_website where site_id='");
		strSearch.append(vo.getString("site_id"));
		strSearch.append("' FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  hr_friend_website set log_icon=EMPTY_BLOB() where site_id='");
		strInsert.append(vo.getString("site_id"));
		strInsert.append("'");
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    //保存图片处理 jingq upd 2014.09.26
	    String fname = file.getFileName();
	    String ext = fname.substring(fname.lastIndexOf(".")+1, fname.length());
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),/*file.getInputStream()*/ImageBO.imgStream(file, ext)); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}
    
	
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao) throws GeneralException {
		boolean bflag=true;
		
        
		 if(file==null||file.getFileSize()==0) {
			 bflag=false;
		 }
		try 
		{	
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String id=PubFunc.NullToZero(idg.getId("hr_friend_web.site_id"));
			vo.setInt("site_id",Integer.parseInt(id));
			vo.setInt("flag",1);
			
			if(bflag)
			{
				String fname=file.getFileName();
	   	 		int indexInt=fname.lastIndexOf(".");
	   	 		String ext=fname.substring(indexInt+1,fname.length());			
	   	 		vo.setString("ext",ext);
			 	/**blob字段保存,数据库中差异*/
			 	switch(Sql_switcher.searchDbServer())
			 	{
			 	   case Constant.ORACEL:
			 			break;
			 	   default:
						//byte[] data=file.getFileData();	
			 		    byte[] data = ImageBO.imgByte(file, ext);
						vo.setObject("log_icon",data);
			 			break;
			 	}	
			}
			dao.addValueObject(vo);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&bflag)
			{
				RecordVo updatevo=new RecordVo("hr_friend_website");
				updatevo.setInt("site_id",Integer.parseInt(id));
			 	Blob blob = getOracleBlob(updatevo, file);
			 	updatevo.setObject("log_icon",blob);			
				dao.updateValueObject(updatevo);
			}
		}	
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);			
		}
	}


}
