package com.hjsj.hrms.transaction.media;

import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SaveMultMediaInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
	    try {
	        HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
	        String userbase=(String)this.getFormHM().get("userbase");
	        //返回参数用来判断新增的多媒体文件是否需要报批
	        String returnvalue=(String)this.getFormHM().get("returnvalue");
	        String A0100=(String)this.getFormHM().get("a0100");
	        String filetitle=(String)this.getFormHM().get("filetitle");
	        //2014.11.7 xxd 文件上传参数过滤
	        filetitle = PubFunc.hireKeyWord_filter(filetitle);
	        String filesort=(String)this.getFormHM().get("filesort");
	        filesort = PubFunc.hireKeyWord_filter(filesort);
	        FormFile file=(FormFile)this.getFormHM().get("file");
	        if(!FileTypeUtil.isFileTypeEqual(file))
	            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("error.fileuploaderror"),"",""));
	        
	        int maxSize = Integer.parseInt((String) this.getFormHM().get("multimedia_maxsize"));
	        if(maxSize > 0 && maxSize < (file.getFileSize()/1024))
	            throw GeneralExceptionHandler.Handle(new GeneralException("","上传文件大小超过管理员定义大小，请修正！上传文件上限"+maxSize+"KB!","",""));
	        
	        RecordVo vo = new RecordVo(userbase.toLowerCase()+ "a00");
	        if("notself".equals(reqhm.get("flag"))&&"A0100".equals(A0100)){
	            
	        }else{
	            if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
	                A0100=userView.getUserId();
	        }
	        if ( vo== null) 
	            return;
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
	        //insert(vo,  file);
	        insertDAO(vo,file,dao,A0100,userbase,filetitle,filesort,returnvalue);
	    } catch (Exception e) {
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
	private void insertDAO(RecordVo vo, FormFile file,ContentDAO dao,String userid,String userbase,String filetitle,String filesort,
	        String returnvalue) throws GeneralException {
		boolean bflag=true;
		if(file==null||file.getFileSize()==0)
			bflag=false;
		int recid=Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00",userid,"A0100",this.getFrameconn()));
		InputStream streamIn = null;
		try {   
			streamIn = file.getInputStream();
			String userName = this.userView.getUserName();
            VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
            VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
            VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
            String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum, getGuidKey(userbase, userid), 
            		streamIn, file.getFileName(), "", false);	
            vo.setString("fileid",fieldId);
			vo.setString("a0100",userid);
			vo.setInt("i9999",recid);
			vo.setString("flag",filesort);
			vo.setString("title",filetitle);
			//returnvalue=null：员工管理-记录录入中的多媒体新增，=2：自助服务-员工信息-信息维护中的多媒体新增
			if(returnvalue == null || "2".equalsIgnoreCase(returnvalue))
			    //将状态直接设置为批准
			    vo.setInt("state",3);
			    
			vo.setDate("createtime",DateStyle.getSystemTime());
			vo.setDate("modtime",DateStyle.getSystemTime());
			vo.setString("createusername",userView.getUserName());
			vo.setString("modusername",userView.getUserName());
			if(bflag) {
		   	 	String fname=file.getFileName();
		   	 	int indexInt=fname.lastIndexOf(".");
		   	 	String ext=fname.substring(indexInt,fname.length());
		   	 	String fileType = "'.html','.htm','.php','.php2','.php3','.php4','.php5','.phtml','.pwml',"
		   	 			+ "'.inc','.asp','.aspx','.ascx','.jsp','.cfm','.cfc','.pl','.bat','.exe','.com','.dll',"
		   	 			+ "'.vbs','.js','.reg','.cgi','.htaccess','.asis','.sh','.shtml','.shtm','.phtm'";
		   	 	if(fileType.contains("'" + ext.toLowerCase() + "'"))
		   	 		throw new GeneralException("", "不允许上传" + ext + "类型文件！", "", "");
		   	 	
				vo.setString("ext",ext);
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			dao.addValueObject(vo);
		}	
		catch(Exception ee)
		{
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);			
		} finally {
			PubFunc.closeResource(streamIn);
		}
	}
	/**
	 * @param vo
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private Blob getOracleBlob(RecordVo vo, FormFile file,String userbase,String userid,int recid) throws FileNotFoundException, IOException {
		StringBuffer strSearch=new StringBuffer();
		strSearch.append("select ole from ");
		strSearch.append(userbase);
		strSearch.append("a00 where a0100='");
		strSearch.append(userid);
		strSearch.append("' and i9999=");
		strSearch.append(recid);
		strSearch.append(" FOR UPDATE");
		
		StringBuffer strInsert=new StringBuffer();
		strInsert.append("update  ");
		strInsert.append(userbase);
		strInsert.append("a00 set ole=EMPTY_BLOB() where a0100='");
		strInsert.append(userid);
		strInsert.append("' and i9999=");
		strInsert.append(recid);
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		Blob blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream()); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		return blob;
	}

	/**
	 * 获取人员主集的guidkey
	 * 
	 * @param nbase
	 *            人员库
	 * @param a0100
	 *            人员编号
	 * @return
	 */
	private String getGuidKey(String nbase, String a0100) {
		String guid = "";
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("select GUIDKEY from ");
			sb.append(nbase + "A01");
			sb.append(" where a0100=?");
			ArrayList<String> paramList = new ArrayList<>();
			paramList.add(a0100);

			ContentDAO dao = new ContentDAO(this.frameconn);
			this.frowset = dao.search(sb.toString(), paramList);
			StringBuffer stmp = new StringBuffer();
			stmp.append("update  ");
			stmp.append(nbase + "A01");
			stmp.append(" set GUIDKEY =?");
			stmp.append(" where a0100=?");
			stmp.append(" and guidkey is null ");
			if (this.frowset.next()) {
				guid = this.frowset.getString("guidkey");
				if (StringUtils.isEmpty(guid)) {
					UUID uuid = UUID.randomUUID();
					guid = uuid.toString();
					paramList.add(0, guid);
					dao.update(stmp.toString(), paramList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return guid;
	}
}
