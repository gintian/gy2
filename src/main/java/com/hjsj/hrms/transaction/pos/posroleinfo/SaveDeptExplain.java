package com.hjsj.hrms.transaction.pos.posroleinfo;

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

/**
 * 
 *<p>Title:SaveDeptExplain.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 6, 2009:2:39:21 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveDeptExplain extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String codesetid=(String)this.getFormHM().get("codesetid");
		String filetitle=(String)this.getFormHM().get("filetitle");
		FormFile file=(FormFile)this.getFormHM().get("file");
		RecordVo vo = new RecordVo("K00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
		insertDAO(vo,file,dao,codesetid,filetitle);
	}
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String codesetid,String filetitle) throws GeneralException {
		boolean bflag=true;
		/**哪个模块，L为廉政风险防范模块，Z为全员职位说明书*/
		String modular = (String) this.getFormHM().get("modular");
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=9999;
		/**将i9999值固定，廉政风险文档里面每个职位只有一个文档 2009-2-9*/
		if (modular != null && "L".equals(modular.trim())) {
			recid=9998;
		}
		try 
		{   
			vo.setString("e01a1",codesetid);
			vo.setInt("i9999",recid);
			if (modular != null && "L".equals(modular.trim())) {
				vo.setString("flag","l");
			} else {
				vo.setString("flag","K");
			}
			vo.setString("title",filetitle);
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
			
			String sql = "select * from K00 where e01a1='"+codesetid+"' and i9999='"+recid+"'";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				dao.updateValueObject(vo);
			}else{
				dao.addValueObject(vo);
			}
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				RecordVo updatevo=new RecordVo("K00");
				vo.setString("e01a1",codesetid);
				updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob( file,codesetid,recid);
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
	private Blob getOracleBlob(FormFile file,String codesetid,int recid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select ole from ");
		strSearch.append("k00 where e01a1='");
		strSearch.append(codesetid);
		strSearch.append("' and i9999=");
		strSearch.append(recid);
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append("k00 set ole=EMPTY_BLOB() where e01a1='");
		strInsert.append(codesetid);
		strInsert.append("' and i9999=");
		strInsert.append(recid);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
	    InputStream in = null;
	    try{
	    	in = file.getInputStream();
	    	Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
	    	return blob;
	    }finally{
	    	PubFunc.closeResource(in);
	    }
	}
}
