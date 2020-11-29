package com.hjsj.hrms.businessobject.workplan;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsService;

import javax.sql.RowSet;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * 沟通，公共类
 * 
 * 简要说明类的作用
 * 其它补充说明
 * <p>Title: WorkPlanCommunicationBo </p>
 * <p>Company: hjsj</p>
 * <p>create time  2014-7-31 下午06:03:09</p>
 * @author guoby
 * @version 1.0
 */
public class WorkPlanCommunicationBo {
	
	private Connection conn;
	private UserView userView;
	private String systemPath;
	
	public WorkPlanCommunicationBo(Connection conn, UserView userView){
		this.userView = userView;
		this.conn = conn;
	}
	
	/**
	 *  查询沟通信息
	 *  
	 * @param type  计划/任务/总结
	 * @param objectId  计划/任务/总结 的编号
	 * @return
	 */
	public ArrayList queryAllMessage(String type,String objectId){
		
		ArrayList queryMessResultList = new ArrayList();
		if (null == type || "".equals(type.trim()) || null == objectId || "".equals(objectId.trim())) {
            return queryMessResultList;
        }
		
		StringBuffer queryMessSql = new StringBuffer();
		queryMessSql.append("select nbase,a0100,id,type,content,create_fullname,create_time,to_name ");
		queryMessSql.append("from per_project_discussion ");
		queryMessSql.append("where type =" + type);
		queryMessSql.append(" and object_id=" + objectId);
		queryMessSql.append(" order by create_time");

		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			WorkPlanBo pb = new WorkPlanBo(conn, userView);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			rs = dao.search(queryMessSql.toString());
			while(rs.next()){
				ArrayList list = new ArrayList();
				list.add(rs.getString("create_fullname"));
				list.add(rs.getString("content"));
				//list.add(rs.getString("create_time"));
				Date createTime = rs.getDate("create_time");
				if(Sql_switcher.searchDbServer() == 2){
					Timestamp ta = rs.getTimestamp("create_time");
					if(ta != null){
						createTime = new Date(ta.getTime());
					}
				}
				list.add(df.format(createTime));
				list.add(WorkPlanUtil.encryption(rs.getInt("id")+""));
				list.add(rs.getString("to_name")); 
				
				String photoUrl = pb.getPhotoPath(rs.getString("nbase"), rs.getString("a0100"));
				list.add(photoUrl);
				list.add(rs.getInt("type")+"");
				
				if(this.userView.getA0100().equalsIgnoreCase(rs.getString("a0100")) && this.userView.getDbname().equalsIgnoreCase(rs.getString("nbase"))){
					list.add("1");
				}else{
					list.add("0");
				}
	
				queryMessResultList.add(list);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			WorkPlanUtil.closeDBResource(rs);
		}
		
		return queryMessResultList;
	}
	
	// 查询该页面中，每条信息所对应的附件
	public ArrayList queryAllUpLoadFile(String objectId) throws GeneralException{
		ArrayList myUpLoadList = new ArrayList();
		//判断是否配置了存储路径
		try {
			if(!VfsService.existPath()){
				throw new GeneralException("没有配置多媒体存储路径！");
			}
			ArrayList upLoadFileList = getAttachmentsByMsgId(objectId);
			if (null != upLoadFileList && 0 < upLoadFileList.size()) {
				for(int i=upLoadFileList.size()-1;i>=0;i--){
					ArrayList upLoadList = (ArrayList) upLoadFileList.get(i);
					String msgId = WorkPlanUtil.encryption(objectId);
					upLoadList.set(0, msgId);
					String path = (String)upLoadList.get(3);
					upLoadList.set(3, path);
					myUpLoadList.add(upLoadList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return myUpLoadList;
	}
	
	private ArrayList getAttachmentsByMsgId(String msgId) {

		ArrayList upLoadList = new ArrayList();
        StringBuffer upLoadFileSQL = new StringBuffer();
        upLoadFileSQL.append("select file_name,ext,path from per_attachment");
        upLoadFileSQL.append(" where type=4 and object_id=");
        upLoadFileSQL.append(msgId);
        
        ContentDAO dao = new ContentDAO(this.conn);
        RowSet rs = null;
        try {
			if(!VfsService.existPath()){
				throw new GeneralException("没有配置多媒体存储路径！");
			}
            rs = dao.search(upLoadFileSQL.toString());
            while(rs.next()) {
            	 ArrayList upLoadFileList = new ArrayList();
            	 upLoadFileList.add(msgId);
                 String fileName=rs.getString("file_name");
                 upLoadFileList.add(fileName);
                 upLoadFileList.add(rs.getString("ext"));
                 //path存的就是fileid
                 String path = rs.getString("path");
                 upLoadFileList.add(path);
                 upLoadList.add(upLoadFileList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            WorkPlanUtil.closeDBResource(rs);
        }
        
        return upLoadList;
	}
	
	/**
	 * 发布消息 or 消息的回复 
	 * @param type
	 * @param object_id 编号
	 * @param content 内容
	 * @param submitDate 提交时间
	 * @param to_name 回复对象
	 * @return
	 */
	public String publishMessage(String type, String object_id,
			String content,String submitDate, String to_name) {

	    String resultMsgId = "";
		RecordVo publishMessageVo = new RecordVo("per_project_discussion");
		IDGenerator idg = new IDGenerator(2, this.conn);

		ContentDAO dao = new ContentDAO(this.conn);
		try {
			String pid = idg.getId("per_project_discussion.id");
			DateFormat submitTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date date = submitTime.parse(submitDate);
			publishMessageVo.setInt("id", Integer.parseInt(pid));
			publishMessageVo.setInt("type", Integer.parseInt(type)); // 消息来源
			publishMessageVo.setInt("object_id", Integer.parseInt(object_id)); // 项目|任务|总结ID
			publishMessageVo.setString("content", content); // 消息内容

			publishMessageVo.setString("nbase",this.userView.getDbname());
			publishMessageVo.setString("a0100",this.userView.getA0100());
			publishMessageVo.setString("create_user",this.userView.getUserName());// 创建者用户名
			publishMessageVo.setString("create_fullname",this.userView.getUserFullName());// 创建者姓名

			publishMessageVo.setDate("create_time", date);// 创建时间
			publishMessageVo.setString("to_name", to_name);// 回复对象

			dao.addValueObject(publishMessageVo);
			
			resultMsgId = pid;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultMsgId;
	}
	
	// 将文件信息保存到数据库
	public boolean saveUpLoadFile(String type,String object_id,String path,String file_name,String file_name_old,String ext,String create_user) {

		RecordVo fileVo = new RecordVo("per_attachment");
		IDGenerator idg = new IDGenerator(2, this.conn);
		ContentDAO dao = new ContentDAO(this.conn);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		try {
			String id = idg.getId("per_attachment.id");
			fileVo.setInt("id", Integer.parseInt(id));
			fileVo.setInt("type", Integer.parseInt(type));
			fileVo.setInt("object_id", Integer.parseInt(object_id));
			fileVo.setString("path", path);
			fileVo.setString("file_name", file_name);
			fileVo.setString("file_name_old", file_name_old);
			fileVo.setString("ext", ext);
			fileVo.setDate("create_time", sdf.parse(sdf.format(new Date())));
			fileVo.setString("create_user", create_user);

			int resultConut = dao.addValueObject(fileVo);
			if (resultConut > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 删除沟通消息及本消息附件
	 * @Title: delMessage   
	 * @Description:    
	 * @param msgId
	 * @return
	 */
	public boolean delMessage(String msgId) {
	    boolean success = false;
	    String sql = "DELETE FROM per_project_discussion WHERE id=? and nbase=? and a0100=?";
	    
	    ArrayList params = new ArrayList();
	    params.add(msgId);
	    params.add(this.userView.getDbname());
	    params.add(this.userView.getA0100());
	    
	    ContentDAO dao = new ContentDAO(this.conn);
	    try {
	        if (0 < dao.delete(sql, params)) {
	            sql = "DELETE FROM per_attachment WHERE type=4 and object_id=?";
	            ArrayList attacParams = new ArrayList();
	            attacParams.add(msgId);
	            
	            ArrayList fileList =  this.getAttachmentsByMsgId(msgId);
	            
	            int count = dao.delete(sql, attacParams);
	            
	            if(count > 0 && fileList.size() > 0){
	            	for(int i=0;i<fileList.size();i++){
	            		 ArrayList fileUploadList = (ArrayList) fileList.get(i);
	            		// 获取附件所在位置 
	 	            	String savePath =(String) fileUploadList.get(3);
	 	            	
	 	            	// 存在文件就删除，重新添加
	 	            	File file = new File(savePath);
	 	            	if (file.isFile() && file.exists()) {
                            file.delete();
                        }
	            	}
	            }
	            
	        }
	        
	        success = true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return success;
	}
	
	/**
	 * 获取沟通信息表的最大
	 * 
	 * @return
	 */
	public int getMessageContentId() {
		int msgId = 0;
		StringBuffer buf = new StringBuffer();
		buf.append("select MAX(id) as maxId from per_project_discussion");
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs = null;
		try {
			rs = dao.search(buf.toString());
			if (rs.next()) {
				msgId = rs.getInt("maxId");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
		    WorkPlanUtil.closeDBResource(rs);
		}
		
		return msgId;
	}

	/**
	 * 通过附件id得到服务器端附件时间存放路径
	 * @Title: getAttachmentPathById   
	 * @Description:    
	 * @param id
	 * @return
	 */
	public String getAttachmentPathById(String id) throws GeneralException {
		
	    String path = getAttacmentRootDir();
	    UUID uuid = UUID.randomUUID();//改成按取guid，如果按id  每次路径都一样
        String tmpid = uuid.toString(); 
        
	    int idHash = Math.abs(tmpid.hashCode());
	    
        String dir1 = "" + idHash / 1000000 % 500;
        while (dir1.length() < 3) {
            dir1 = "0" + dir1;
        }

        String dir2 = "" + idHash / 1000 % 500;
        while (dir2.length() < 3) {
            dir2 = "0" + dir2;
        }
        
        path = path + "workplan" + File.separator + "F" + dir1 + File.separator + "F" + dir2;           
        
	    return path;
	}
	
	/**
	 * 得到系统参数配置中的多媒体路径
	 * @Title: getAttacmentRootDir   
	 * @Description: 得到系统参数配置中的多媒体路径    
	 * @return
	 * @throws GeneralException
	 * @deprecated
	 */
	public String getAttacmentRootDir() throws GeneralException {
		ConstantXml constantXml = new ConstantXml(this.conn, "FILEPATH_PARAM");
		String rootDir = constantXml.getNodeAttributeValue("/filepath", "rootpath");

		if (rootDir == null || "".equals(rootDir)) {
			throw new GeneralException("没有配置多媒体存储路径！");
		}

		rootDir = rootDir.replace("\\", File.separator);
		if (!rootDir.endsWith(File.separator)) {
            rootDir = rootDir + File.separator;
        }

		rootDir = rootDir + "doc" + File.separator;
		return rootDir;
	}
	
}
