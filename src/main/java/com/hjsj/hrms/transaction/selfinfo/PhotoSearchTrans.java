package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 头像目录查询初始化
 * @author tianye
 *
 */
public class PhotoSearchTrans extends IBusiness {


	public void execute() throws GeneralException {
		PhotoImgBo photoImgBo = new PhotoImgBo(this.frameconn);
		String x=(String)this.getFormHM().get("x");
		if(x==null||x.length()==0){
			this.getFormHM().put("x", "50");
			this.getFormHM().put("y", "50");
			this.getFormHM().put("width", "100");
			this.getFormHM().put("height", "100");
		}
		int status = 0;
		status=userView.getStatus();
		boolean bself=true;
		if(status!=4)
		{
			String a0100=userView.getA0100();
			if(a0100==null||a0100.length()==0)
			{
				bself=false;
			}
		}
		if(!bself){
			throw new GeneralException(ResourceFactory.getProperty("employ.no.use.model"));
		}
		//查找上传的头像文件是否存在
		Map<String,String> fileMap = searchHeadPic();
		if(fileMap != null && fileMap.size()>0){
			this.getFormHM().put("photoname", fileMap.get("highimage"));
			this.getFormHM().put("lowimage", fileMap.get("lowimage"));
			this.getFormHM().put("photoType", fileMap.get("ext"));
			this.getFormHM().put("scale", "");
		}else{
			//首次设置默认显示此人的人事档案照片  jingq add  2014.08.19
			fileMap = getUsrPhoto();
			if(fileMap !=null && fileMap.size()>0){
				this.getFormHM().put("photoname", fileMap.get("highimage"));
				this.getFormHM().put("lowimage", fileMap.get("lowimage"));
				this.getFormHM().put("photoType", fileMap.get("ext"));
				this.getFormHM().put("scale", "");
			} else {
				this.getFormHM().put("photoname", "");
				this.getFormHM().put("scale", "false");//scale为false时,页面不显示截图
			}
		}
	}
	/**
	 *
	 * @Title: getUsrPhoto
	 * @Description:获取当前用户人事档案照片
	 * @param @return
	 * @return Map<String,String>
	 * @author zhangh
	 */
	public Map<String,String>  getUsrPhoto(){
		Map<String,String> fileMap = new HashMap<String,String>();
		String dbname = this.userView.getDbname()+"A00";
		String usr = this.userView.getA0100();
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		String ext = "";
		String fileid = "";
		InputStream inputStream = null;
		InputStream input = null;
		String userName = this.userView.getUserName();
		VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.multimedia;
		VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.personnel;
		String fileName = "";
		try {
			//先获取人员的guidkey
			String guidkey = this.userView.getGuidkey();
			ContentDAO dao = new ContentDAO(this.frameconn);
			sql.append("select fileid,ext,Ole from ");
			sql.append(dbname);
			sql.append(" where A0100 = '");
			sql.append(usr);
			sql.append("' and UPPER(flag) = 'P'");
			rs = dao.search(sql.toString());
			if(rs.next()){
				ext = rs.getString("ext");
				fileName = this.userView.getUserFullName() + ext;
				fileid = rs.getString("fileid");
				inputStream = rs.getBinaryStream("Ole");
				input = rs.getBinaryStream("Ole");
				fileMap.put("ext",ext.substring(1,ext.length()));
			}
			if(input !=null) {
				//没有获取到文件id，需要先将证件照上传到VFS，然后再将证件照复制一份放到hr_multimedia_file
				if(StringUtils.isBlank(fileid)){
					//没有获取到文件id，需要上传到VFS中
					if(inputStream !=null) {
						VfsModulesEnum vfsModulesEnum = VfsModulesEnum.YG;
						fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
								guidkey, inputStream, fileName, "", false);
						//将文件id更新到A00中
						dao.update("update " + dbname + " set fileid='"+fileid+"' where A0100 ='"+usr+"' and UPPER(flag) = 'P' ");
					}
				}
				VfsModulesEnum vfsModulesEnum = VfsModulesEnum.XT;
				//将人员头像上传到VFS
				fileid = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
						guidkey, input, fileName, "", false);
				rs = dao.search("select mainguid from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'");
				if(rs.next()){
					//将文件id更新到hr_multimedia_file
					dao.update("update hr_multimedia_file set path='"+fileid+"' where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'");
				}else{
					RecordVo vo = new RecordVo("hr_multimedia_file");
					IDGenerator idg = new IDGenerator(2, this.frameconn);
					String id = idg.getId("hr_multimedia_file.id");
					vo.setInt("id", Integer.parseInt(id));
					vo.setInt("displayorder", Integer.parseInt(id));
					vo.setString("mainguid", guidkey);
					vo.setString("childguid", guidkey);
					vo.setString("nbase", this.userView.getDbname());
					vo.setString("a0100", this.userView.getA0100());
					vo.setString("dbflag", "A");
					vo.setString("class", "P");
					vo.setString("path", fileid);
					vo.setString("ext", ext);
					vo.setString("srcfilename", fileName);
					vo.setString("createusername", userName);
					Date date = DateUtils.getSqlDate(Calendar.getInstance());
					vo.setDate("createtime", date);
					dao.addValueObject(vo);
				}
				fileMap.put("highimage",fileid);
				fileMap.put("lowimage",fileid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			PubFunc.closeResource(rs);
			PubFunc.closeIoResource(inputStream);
		}
		return fileMap;
	}
	/**
	 *
	 * @Title: createPhoto
	 * @Description: copy照片
	 * @param @param olddir
	 * @param @param newdir
	 * @return void
	 * @author jingq
	 */
	public void createPhoto(String olddir,String newdir){
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(olddir);
			out = new FileOutputStream(newdir);
			byte[] bytes = new byte[1024];
			int v = -1;
			while((v = in.read(bytes, 0, 1024))!=-1){
				out.write(bytes, 0, v);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			PubFunc.closeResource(out);
			PubFunc.closeResource(in);
		}
	}

	/**
	 * 查找头像文件
	 * @return
	 */
	private Map<String,String> searchHeadPic(){
		Map<String,String> fileMap = new HashMap<String,String>();
		RowSet rs = null;
		String sql = "";
		String guidkey = "";
		String path = "";
		//上传的完整头像
		String highimage = "";
		//切割后的低分辨率头像
		String lowimage = "";
		//文件扩展名
		String ext = "";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			guidkey = userView.getGuidkey();
			sql = "select path,ext from hr_multimedia_file where mainguid='"+guidkey+"' and childguid='"+guidkey+"' and class='P'";
			rs = dao.search(sql);
			if(rs.next()){
				path = rs.getString("path");
				String [] arr = path.split(",");
				//path里存有两张照片的文件,第一张上传的完整头像，第二张低分辨率的头像，中间用逗号分隔
				if(arr != null){
					if(arr.length ==1){
						highimage = arr[0];
					}else if(arr.length ==2){
						highimage = arr[0];
						lowimage = arr[1];
					}
				}
				ext = rs.getString("ext");
				if(ext.contains(".")){
					ext = ext.substring(ext.lastIndexOf(".") + 1).toLowerCase();
				}
				fileMap.put("highimage",highimage);
				if(StringUtils.isBlank(lowimage)){
					lowimage = highimage;
				}
				fileMap.put("lowimage",lowimage);
				fileMap.put("ext",ext);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			PubFunc.closeDbObj(rs);
		}
		return fileMap;
	}
}
