package com.hjsj.hrms.transaction.pos.posroleinfo;

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
import java.util.Date;

/**
 * 
 *<p>Title:SaveTaskBook.java</p> 
 *<p>Description:</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:May 7, 2009:3:48:33 PM</p> 
 *@author huaitao
 *@version 1.0
 */
public class SaveTaskBook extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		String userbase = (String)this.getFormHM().get("dbname");
		String a0100=(String)this.getFormHM().get("a0100");
		String filetitle=(String)this.getFormHM().get("filetitle");
		FormFile file=(FormFile)this.getFormHM().get("file");
		RecordVo vo = new RecordVo(userbase+"a00");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
		insertDAO(vo,file,dao,userbase,a0100,filetitle);
	}
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String userbase,String a0100,String filetitle) throws GeneralException {
		boolean bflag=true,b=false;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00",a0100,"A0100",this.getFrameconn()));
		int i9999 = 0;
		try 
		{   
			vo.setString("a0100",a0100);
			vo.setInt("i9999",recid);
			vo.setString("flag","T");
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
			cat.debug("reply_insert_boardvo=" + vo.toString());
			int date = Integer.parseInt(PubFunc.FormatDate(new Date(),"yyyy"));
			java.text.SimpleDateFormat simpleDateFormat = new	java.text.SimpleDateFormat("yyyy");   
			Date d1 =  simpleDateFormat.parse(date+"");
			Date d2 =  simpleDateFormat.parse(date+1+"");
			
			String sql = "select * from "+userbase+"a00 where a0100='"+a0100+"' and flag='T' and createtime>='"+DateStyle.dateformat(d1,"yyyy-MM-dd hh:mm:ss")+"' and createtime <='"+DateStyle.dateformat(d2,"yyyy-MM-dd hh:mm:ss")+"'";
			this.frowset = dao.search(sql);
			if(this.frowset.next()){
				i9999 = this.frowset.getInt("i9999");
				vo.setInt("i9999",i9999);
				dao.updateValueObject(vo);
				b= true;
			}else{
				dao.addValueObject(vo);
			}
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL)
			{
				RecordVo updatevo=new RecordVo(userbase+"a00");
				vo.setString("a0100",a0100);
				if(b)
					updatevo.setInt("i9999",i9999);
				else
					updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob(updatevo, file,userbase,a0100,recid);
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
	private Blob getOracleBlob(RecordVo vo, FormFile file,String userbase,String a0100,int recid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		InputStream in = null;
		Blob blob = null;
		try{
			strSearch.append("select ole from ");
			strSearch.append(userbase+"a00 where a0100='");
			strSearch.append(a0100);
			strSearch.append("' and i9999=");
			strSearch.append(recid);
			strSearch.append(" FOR UPDATE");
			
			StringBuffer strInsert=new StringBuffer();
			strInsert.append("update  ");
			strInsert.append(userbase+"a00 set ole=EMPTY_BLOB() where a0100='");
			strInsert.append(a0100);
			strInsert.append("' and i9999=");
			strInsert.append(recid);
		    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		    in = file.getInputStream();
			blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),in); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch(Exception e){
			
		}finally{
		   PubFunc.closeIoResource(in);
		}
		return blob;
	}
}
