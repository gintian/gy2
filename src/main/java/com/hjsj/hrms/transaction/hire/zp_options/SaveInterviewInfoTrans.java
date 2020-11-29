/*
 * Created on 2005-9-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_options;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
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
import java.sql.SQLException;

/**
 * <p>Title:SaveInterviewInfoTrans</p>
 * <p>Description:保存面试资料</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 20, 2005</p>
 * @author fengxin
 * @version 1.0
 * 
 */
public class SaveInterviewInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		RecordVo vo=(RecordVo)this.getFormHM().get("testQuestionvo");
        if(vo==null)
            return;
        String flag=(String)this.getFormHM().get("flag");
        String pos_id =(String)this.getFormHM().get("pos_id");
        FormFile file = (FormFile)this.getFormHM().get("file");
        ContentDAO dao=new ContentDAO(this.getFrameconn()); 
        if("1".equals(flag))
        {
            /**
             * 新增发部招聘岗位，进行保存处理
             */
        	insertDAO(vo,file,dao,pos_id);
        }
        else if("0".equals(flag))
        {
	        /**
	         * 点编辑链接后，进行保存处理
	         */
	        update(vo, file);
        }  	

	}
	
	private void update(RecordVo vo,FormFile file)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		PreparedStatement pstmt=null;
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;		
        InputStream inputStream=null;
        DbSecurityImpl dbS = new DbSecurityImpl();
        try
        {
            inputStream=file.getInputStream();
			 if(bflag)
			 {
				 strsql.append("update zp_pos_test set name = ?,description = ?,pos_id = ?,test_questions = ?,ext = ? where test_id = ?");
			 }
			 else
			 {
				 strsql.append("update zp_pos_test set name = ?,description = ?,pos_id = ? where test_id = ?");
			 }
			 pstmt=this.getFrameconn().prepareStatement(strsql.toString());
			 pstmt.setString(1, vo.getString("name"));
			 pstmt.setString(2, vo.getString("description"));
			 pstmt.setString(3, vo.getString("pos_id"));
			 if(bflag)
			 {
				 	/**blob字段保存,数据库中差异*/
				 	switch(Sql_switcher.searchDbServer())
				 	{
				 	   case Constant.ORACEL:
						 	Blob blob = getOracleBlob(vo, file);
				 			pstmt.setBlob(4,blob);
				 			break;
				 	   default:
							pstmt.setBinaryStream(4,inputStream,file.getFileSize());
				 			break;
				 	}
					String fname=file.getFileName();
		   	 		int indexInt=fname.lastIndexOf(".");
		   	 		String ext=fname.substring(indexInt+1,fname.length());			
		   	 		pstmt.setString(5,ext);		
					pstmt.setInt(6,Integer.parseInt(vo.getString("test_id")));		   	 		
			 }
			 else
			 {
			 	pstmt.setInt(4, Integer.parseInt(vo.getString("test_id")));				 
			 }
			// 打开Wallet
			 dbS.open(this.frameconn, strsql.toString());
			 pstmt.executeUpdate();
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);				
		}
		finally
		{
			try {
				// 关闭Wallet
				dbS.close(this.frameconn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		    PubFunc.closeResource(inputStream);  
			try
			{
				if(pstmt!=null)
					pstmt.close();
			}
			catch(SQLException ee)
			{
				ee.printStackTrace();
			}
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
		strSearch.append("select test_questions from zp_pos_test where test_id='");
		strSearch.append(vo.getString("test_id"));
		strSearch.append("' FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  zp_pos_test set test_questions=EMPTY_BLOB() where test_id='");
		strInsert.append(vo.getString("test_id"));
		strInsert.append("'");
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    Blob blob = null;
	    InputStream is = null;
	    try{
	       is = file.getInputStream();
	       blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());  
	    }catch(Exception e){
	        e.printStackTrace();
	    }
	    finally{
	        PubFunc.closeIoResource(is);
	    }
		
		return blob;
	}
    
	
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String pos_id) throws GeneralException {
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		try 
		{
			IDGenerator idg=new IDGenerator(2,this.getFrameconn());
			String test_id=idg.getId("zp_pos_test.test_id"); 
			vo.setInt("test_id",Integer.parseInt(test_id));
			vo.setString("pos_id",pos_id);
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
						byte[] data=file.getFileData();				
						vo.setObject("test_questions",data);
			 			break;
			 	}				
			}
			dao.addValueObject(vo);
			if(Sql_switcher.searchDbServer()==Constant.ORACEL&&bflag)
			{
				RecordVo updatevo=new RecordVo("zp_pos_test");
				updatevo.setInt("test_id",Integer.parseInt(test_id));
			 	Blob blob = getOracleBlob(updatevo, file);
			 	updatevo.setObject("test_questions",blob);			
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
