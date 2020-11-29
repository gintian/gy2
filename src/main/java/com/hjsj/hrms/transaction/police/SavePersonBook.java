package com.hjsj.hrms.transaction.police;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 *<p>Title:SavePersonBook</p> 
 *<p>Description:保存文件</p> 
 *<p>Company:hjsj</p> 
 *<p>Create time:2010-2-8</p> 
 *@author wangzhongjun
 *@version 1.0
 */
public class SavePersonBook extends IBusiness {
	public void execute() throws GeneralException {
		String a0100=(String)this.getFormHM().get("a0100");
		String userbase = (String)this.getFormHM().get("userbase");
		String filetitle=(String)this.getFormHM().get("filetitle");
		FormFile file=(FormFile)this.getFormHM().get("mediafile");
		RecordVo vo = new RecordVo(userbase +"A00");
		if ( vo== null) 
			return;
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
	    //insert(vo,  file);
		insertDAO(vo,file,dao,a0100,filetitle,userbase);
	}
	/**
	 * 通过底层函数进行文件保存
	 * @param vo
	 * @param file
	 * @param dao
	 * @throws GeneralException
	 */
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String a0100,String filetitle,String userbase) throws GeneralException {
		boolean bflag=true,b=false;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=Integer.parseInt(new StructureExecSqlString().getOrgI9999(userbase+"A00", a0100, "a0100", this.getFrameconn()));
		int i9999 = 0;
		try 
		{   
			vo.setString("a0100",a0100);
			vo.setInt("i9999",recid);
			vo.setString("flag", "t");			
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
			SimpleDateFormat simpleDateFormat = new	SimpleDateFormat("yyyy");   
			Date d1 =  simpleDateFormat.parse(date+"");
			Date d2 =  simpleDateFormat.parse(date+1+"");
			String cycle = (String) this.getFormHM().get("cycle");
			if ("0".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				d1 = simpleDateFormat.parse(taskyear);
				d2 = simpleDateFormat.parse(String.valueOf(Integer.parseInt(taskyear) + 1));
			} else if ("1".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				String taskmonth = (String) this.getFormHM().get("taskmonth");
				SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
				d1 = fo.parse(taskyear + "-" + taskmonth + "-01");
				if (Integer.parseInt(taskmonth) == 12) {
					d2 = fo.parse(String.valueOf(Integer.parseInt(taskyear) + 1) + "-" + "01-01");
				} else {
					d2 = fo.parse(taskyear + "-" + String.valueOf(Integer.parseInt(taskmonth) + 1) + "-01");
				}
				
			} else if ("2".equals(cycle)) {
				String taskyear = (String)this.getFormHM().get("taskyear");
				String taskweek = (String) this.getFormHM().get("taskweek");
				SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
				d1 = fo.parse(taskyear + "-" +(Integer.parseInt(taskweek) * 3 -2) + "-01");
				if (Integer.parseInt(taskweek) == 4) {
					d2 = fo.parse((Integer.parseInt(taskyear)+1) + "-01-01");
				} else {
					d2 = fo.parse(taskyear + "-" +((Integer.parseInt(taskweek) + 1) * 3 -2) + "-01");
				}
			}
			Date nowDate = new Date();
			if (nowDate.getTime() >= d1.getTime() && nowDate.getTime() < d2.getTime()) {
				vo.setDate("createtime",DateStyle.getSystemTime());
			} else {
				vo.setDate("createtime",d1);
			}
			//String sql = "select * from "+userbase+"A00 where a0100='"+ a0100 +"' and createtime>='"+DateStyle.dateformat(d1,"yyyy-MM-dd hh:mm:ss")+"' and createtime <='"+DateStyle.dateformat(d2,"yyyy-MM-dd hh:mm:ss")+"'";
			String sql = "select * from "+userbase+"a00 where a0100='"+a0100+"' and createtime>="+Sql_switcher.dateValue(DateUtils.format(d1,"yyyy-MM-dd"))+" and createtime <"+Sql_switcher.dateValue(DateUtils.format(d2,"yyyy-MM-dd"))+"";
			sql += " and flag='t'";

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
				RecordVo updatevo=new RecordVo("b00");
				vo.setString("a0100",a0100);
				if(b)
				{
					updatevo.setInt("i9999",i9999);
					recid=i9999;
				}
					
				else
					updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob(updatevo, file,a0100,recid,userbase);
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
	private Blob getOracleBlob(RecordVo vo, FormFile file,String a0100,int recid,String userbase) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		InputStream is =null;
		Blob blob= null;
		try{
			is = file.getInputStream();
		strSearch.append("select ole from ");
		strSearch.append(userbase);
		strSearch.append("A00 where a0100='");
		strSearch.append(a0100);
		strSearch.append("' and i9999=");
		strSearch.append(recid);
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(userbase);
		strInsert.append("A00 set ole=EMPTY_BLOB() where a0100='");
		strInsert.append(a0100);
		strInsert.append("' and i9999=");
		strInsert.append(recid);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeIoResource(is);
		}
		return blob;
	}
}
