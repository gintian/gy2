/*
 * Created on 2005-11-9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.hire.zp_persondb;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.sql.PreparedStatement;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SavePersonenrollPhotoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		///System.out.println("userbase");
		String userbase=(String)this.getFormHM().get("userbase");
		userbase = PubFunc.hireKeyWord_filter(userbase);
        FormFile file = (FormFile)this.getFormHM().get("picturefile");
        String A0100=(String)this.getFormHM().get("a0100");
        A0100 = PubFunc.hireKeyWord_filter(A0100);
        String filesort=(String)this.getFormHM().get("filesort");
        filesort = PubFunc.hireKeyWord_filter(filesort);
        String filetitle=(String)this.getFormHM().get("filetitle");
        DbSecurityImpl dbS = new DbSecurityImpl();
        try{
     	   deleteDAO(A0100,userbase);
     	}catch(Exception e)
 		{}
        String fname = file.getFileName();
        int indexInt = fname.lastIndexOf(".");
		String ext = fname.substring(indexInt + 1, fname.length());
		if (indexInt <= 0) {
			ext = "";
		} else {

		}
        InputStream streamIn = null;
        int filesize = 0;
        try{
        	streamIn = file.getInputStream();
        	filesize = file.getFileSize();
        	
        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	PubFunc.closeIoResource(streamIn);
        }
        String recordid=new StructureExecSqlString().getUserI9999(userbase+ "A00",A0100,"A0100",this.getFrameconn());
            /**
             * 上传照片信息
             */
		StringBuffer strsql=new StringBuffer();
		strsql.append("insert into ");
		strsql.append(userbase + "A00");
		strsql.append("(A0100,I9999,Title,Ole,flag,ext,id) values(?,?,?,?,?,?,?)");
        	try( PreparedStatement pstmt = this.getFrameconn().prepareStatement(strsql.toString())){


                pstmt.setString(1, A0100);
    			pstmt.setInt(2, Integer.parseInt(recordid));
    			pstmt.setString(3, filetitle);
    			pstmt.setBinaryStream(4, streamIn, filesize);
    			pstmt.setString(5, filesort);
    			pstmt.setString(6, "." + ext);
    			pstmt.setInt(7, 0);
    			// 打开Wallet
    			dbS.open(this.frameconn, strsql.toString());
    			pstmt.executeUpdate();
    			pstmt.close();
			    streamIn = null;
            }catch(Exception e){
    	        e.printStackTrace();
    	        throw GeneralExceptionHandler.Handle(e);
            }finally{
            	try {
            		// 关闭Wallet
            		dbS.close(this.frameconn);
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        }  
	 private void deleteDAO(String A0100,String userbase) throws GeneralException
	  {
	     StringBuffer deletesql=new StringBuffer();
	     deletesql.append("delete from ");
	     deletesql.append(userbase);
	     deletesql.append("a00 where a0100='");
	     deletesql.append(A0100);
	     deletesql.append("' and flag='P'");
	     try{
	        new ExecuteSQL().execUpdate(deletesql.toString(),this.getFrameconn());
	     }catch(Exception e){
	     	e.printStackTrace();
	     }
	     
	  }
}
