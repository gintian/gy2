/*
 * Created on 2005-6-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.common.CmykToRgbBo;
import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.ImageBO;
import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.OracleBlobUtils;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.File;
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
public class SavePhotoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		
		HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String)reqhm.get("flag");//zgd 2014-3-20 判断执行的是清除还是保存操作
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		String fileurl = (String)reqhm.get("fileurl");//zgd 2014-5-15 获取地址url
		fileurl=fileurl!=null&&fileurl.trim().length()>0?fileurl:"";
		String userbase=(String)this.getFormHM().get("userbase");
		RecordVo vo = new RecordVo(userbase.toLowerCase()+ "a00");
		String A0100=(String)this.getFormHM().get("a0100");
		if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100=userView.getUserId();
		try {
		    //liuy 2014-7-18 begin
		    //根据GUID_KEY算出存放照片的文件目录
		    PhotoImgBo photoImgBo = new PhotoImgBo(frameconn);
		    //String str1 = photoImgBo.getPhotoRootDir();
		    //String str2 = photoImgBo.getPhotoRelativeDir(userbase, A0100);
		    //【4729】为兼容老版本，不设置文件存放目录时，也可以上传头像   jingq add 2014.11.4
		    String str1 = "";
		    String str2 = "";
		    try{
		        str1 = photoImgBo.getPhotoRootDir();
		        str2 = photoImgBo.getPhotoRelativeDir(userbase, A0100);
		    } catch (Exception e){
		        
		    }
		    String saveFile = str1 + str2;
		    //判断存放照片的文件夹是否存在，不存在则创建
		    File tempDir = new File(saveFile);
		    if (!tempDir.exists()) {
		        tempDir.mkdirs();
		    }//end
		    
		    ContentDAO dao=new ContentDAO(this.getFrameconn());
		    FormFile file=(FormFile)this.getFormHM().get("picturefile");
		    if(!"reset".equalsIgnoreCase(flag)){
		        if(!FileTypeUtil.isFileTypeEqual(file))
		            throw new GeneralException("", ResourceFactory.getProperty("error.fileuploaderror"), "", "");
		        
		        String photo_maxSize = (String) this.getFormHM().get("photo_maxsize");
		        photo_maxSize = StringUtils.isEmpty(photo_maxSize) ? "-1" : photo_maxSize;
		        int maxSize = Integer.parseInt(photo_maxSize);
		        boolean CMYK = false;
		        if(!"".equals(fileurl)){//zgd 2014-5-15 验证照片模式的。照片只支持RGB模式，不支持CMYK模式。（CMYK模式照片为打印模式）
		            CMYK = CmykToRgbBo.isCMYK(fileurl);
		        }
		        
		        if(CMYK){
		            throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.info.typeofcmyk"),"",""));
		        }
		        if ( vo== null) 
		            return;
		        if(file==null){
		            return;
		        }
		        String fname=file.getFileName();
		        if(fname!=null&&!"".equals(fname)){
		            int indexInt=fname.lastIndexOf(".");
		            String ext=fname.substring(indexInt,fname.length());
		            if(ext!=null && !".bmp".equalsIgnoreCase(ext) && !".jpg".equalsIgnoreCase(ext) && !".jpeg".equalsIgnoreCase(ext))
		                throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("workbench.info.nophoto"),"",""));
		        }
		        
		        if(maxSize > 0 && (maxSize * 1024) < file.getFileSize())
		            throw GeneralExceptionHandler.Handle(new GeneralException("","上传文件大小超过管理员定义大小，请修正！上传文件上限"+maxSize+"KB!","",""));
		        
		    }
            
    	   deleteDAO(A0100,userbase,saveFile);
    	   if(!"reset".equalsIgnoreCase(flag)){
    	       insertDAO(vo,file,dao,A0100,userbase);
    	   }
    	}catch(Exception e) {
    		e.printStackTrace();
    		 throw GeneralExceptionHandler.Handle(e);
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
		InputStream is = null;
		Blob blob= null;
		try{
		    String fileName = file.getFileName();
		    String ext = fileName.substring(fileName.lastIndexOf("."));
			is = ImageBO.imgStream(file, ext);
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
		//System.out.println("update sql="+strInsert.toString());
	    OracleBlobUtils blobutils=new OracleBlobUtils(this.getFrameconn());
		blob=blobutils.readBlob(strSearch.toString(),strInsert.toString(),is); //readBlob(strSearch.toString(),strInsert.toString(),file.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
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
	private void insertDAO(RecordVo vo, FormFile file, ContentDAO dao, String userid, String userbase)
			throws GeneralException {
		boolean bflag = true;
		if (file == null || file.getFileSize() == 0)
			bflag = false;
		int recid = Integer.parseInt(new StructureExecSqlString().getUserI9999(userbase + "a00", userid, "A0100", this.getFrameconn()));
		InputStream in = null;
		try {
			vo.setString("a0100", userid);
			vo.setInt("i9999", recid);
			vo.setString("flag", "P");
			vo.setInt("id", 0);
			vo.setDate("createtime", DateStyle.getSystemTime());
			vo.setDate("modtime", DateStyle.getSystemTime());
			vo.setString("createusername", userView.getUserName());
			vo.setString("modusername", userView.getUserName());
			if (bflag) {
				String fname = file.getFileName();
				in = file.getInputStream();
				int indexInt = fname.lastIndexOf(".");
				String ext = fname.substring(indexInt, fname.length());
				vo.setString("ext", ext);
				/** blob字段保存,数据库中差异 */
				switch (Sql_switcher.searchDbServer()) {
				case Constant.ORACEL:
					break;
				default:
					byte[] data = ImageBO.imgByte(file, ext);
					vo.setObject("ole", data);
					break;
				}

				VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
				String userName = this.userView.getUserName();
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
				VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
				String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
						getGuidKey(userbase, userid), in, fname, "", false);
				vo.setString("fileid", fieldId);
			}
			cat.debug("reply_insert_boardvo=" + vo.toString());
			dao.addValueObject(vo);
			if(bflag && Sql_switcher.searchDbServer()==Constant.ORACEL) {
				RecordVo updatevo=new RecordVo(userbase + "a00");
				updatevo.setString("a0100",userid);
				updatevo.setInt("i9999",recid);
			 	Blob blob = getOracleBlob(updatevo, file,userbase,userid,recid);
			 	updatevo.setObject("ole",blob);			
				dao.updateValueObject(updatevo);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			throw GeneralExceptionHandler.Handle(ee);
		} finally {
			PubFunc.closeResource(in);
		}
	}

	private void deleteDAO(String A0100, String userbase, String saveFile) throws GeneralException {
		try {
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer();
			sql.append("select fileid from ");
			sql.append(userbase);
			sql.append("a00 where a0100=?");
			sql.append(" and flag='P'");
			ArrayList<String> paramList = new ArrayList<String>();
			paramList.add(A0100);
			this.frowset = dao.search(sql.toString(), paramList);
			if (this.frowset.next()) {
				String fileid = this.frowset.getString("fileid");
				if(StringUtils.isNotEmpty(fileid)) {
					VfsService.deleteFile(this.userView.getUserName(), this.frowset.getString("fileid"));
				}
			}
			// liuy 2014-7-18 begin
			PhotoImgBo photoImgBo = new PhotoImgBo(frameconn);
			// 删除时只删除人员照片，不删除人员设置的头像 guodd 2016-06-20
			// haosl update 2018-1-30 同时删除低分辨率图片，否则okr的头像不会同步修改
			photoImgBo.delFileByName(saveFile, "photo,low_img");
			// liuy end
			StringBuffer deletesql = new StringBuffer();
			deletesql.append("delete from ");
			deletesql.append(userbase);
			deletesql.append("a00 where a0100=?");
			deletesql.append(" and flag='P'");
			dao.delete(deletesql.toString(), paramList);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
