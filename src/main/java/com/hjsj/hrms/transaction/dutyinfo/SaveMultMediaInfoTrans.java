/*
 * Created on 2005-11-14
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
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
import java.util.HashMap;

/**
 * 
 *<p>Title:SaveMultMediaInfoTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Dec 7, 2007</p> 
 *@author huaitao
 *@version 4.0
 */
public class SaveMultMediaInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		//String userbase=(String)this.getFormHM().get("userbase");
		//String A0100=(String)this.getFormHM().get("a0100");
		String code=(String)this.getFormHM().get("code");
		String kind = (String)this.getFormHM().get("kind");
		String filetitle=(String)this.getFormHM().get("filetitle");
		String filesort=(String)this.getFormHM().get("filesort");
		FormFile file=(FormFile)this.getFormHM().get("file");
		RecordVo vo = null;
		if("0".equalsIgnoreCase(kind))
			vo = new RecordVo("k00");
		else //if(kind.equalsIgnoreCase("1"))
			vo = new RecordVo("b00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
		insertDAO(vo,file,dao,kind,code,filetitle,filesort);
	}
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String kind,String code,String filetitle,String filesort) throws GeneralException {
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=0;
		if("0".equalsIgnoreCase(kind))
			recid=Integer.parseInt(new StructureExecSqlString().getUserI9999("k00",code,"e01a1",this.getFrameconn()));
		else //if(kind.equalsIgnoreCase("1"))
			recid=Integer.parseInt(new StructureExecSqlString().getUserI9999("b00",code,"b0110",this.getFrameconn()));
		try 
		{   
			if("0".equalsIgnoreCase(kind))
				vo.setString("e01a1",code);
			else //if(kind.equalsIgnoreCase("1"))
				vo.setString("b0110",code);
			vo.setInt("i9999",recid);
			vo.setString("flag",filesort);
			vo.setString("title",filetitle);
			
			//vo.setString("state",0);
			vo.setDate("createtime",DateStyle.getSystemTime());
			vo.setDate("modtime",DateStyle.getSystemTime());
			vo.setString("createusername",userView.getUserName());
			vo.setString("modusername",userView.getUserName());
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
						vo.setObject("ole",data);
			 			break;
			 	}				
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			dao.addValueObject(vo);
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				RecordVo updatevo=null;
				if("0".equalsIgnoreCase(kind)){
					updatevo=new RecordVo("k00");
					updatevo.setString("e01a1",code);
				}
				else {//if(kind.equalsIgnoreCase("1")){
					updatevo=new RecordVo("b00");
					updatevo.setString("b0110",code);
				}
					
				updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob(updatevo, file,kind,code,recid);
			 	updatevo.setObject("ole",blob);	
				dao.updateValueObject(updatevo);
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
	private Blob getOracleBlob(RecordVo vo, FormFile file,String kind,String code,int recid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		InputStream is = null;
		Blob blob;
		strSearch.append("select ole from ");
		if("0".equalsIgnoreCase(kind)){
			strSearch.append("k00");
			strSearch.append(" where e01a1='");
			strSearch.append(code);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
		}else {//if(kind.equalsIgnoreCase("1")){
			strSearch.append("b00");
			strSearch.append(" where b0110='");
			strSearch.append(code);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
		}
		StringBuffer strInsert=new StringBuffer();
		if("0".equalsIgnoreCase(kind)){
			strInsert.append("update  ");
			strInsert.append("k00");
			strInsert.append(" set ole=EMPTY_BLOB() where e01a1='");
			strInsert.append(code);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}else {//if(kind.equalsIgnoreCase("1")){
			strInsert.append("update  ");
			strInsert.append("b00");
			strInsert.append(" set ole=EMPTY_BLOB() where b0110='");
			strInsert.append(code);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		}
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    try{
    	    is=file.getInputStream();
    		blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    }finally{
	        PubFunc.closeResource(is);
	    }
		return blob;
	}
}
