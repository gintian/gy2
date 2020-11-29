package com.hjsj.hrms.transaction.train.resource;

import com.hjsj.hrms.utils.FileTypeUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;

import java.io.InputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FileUpLoadTrans extends IBusiness {

	public void execute() throws GeneralException {

	    HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String)hm.get("flag");
		flag=flag!=null&&flag.trim().length()>0?flag:"";
		hm.remove("flag");
		
		String itemid = (String)hm.get("r0701");
		itemid=itemid!=null&&itemid.trim().length()>0?itemid:"";
		itemid = PubFunc.decrypt(SafeCode.decode(itemid));
		itemid = PubFunc.keyWord_filter(itemid);
		hm.remove("itemid");
		
		String type = (String)hm.get("type");
		type=type!=null&&type.trim().length()>0?type:"";
		type = PubFunc.keyWord_filter(type);
		hm.remove("type");
		
		String myself = (String)hm.get("myself");
		myself=myself!=null&&myself.trim().length()>0?myself:"";
		myself = PubFunc.keyWord_filter(myself);
		hm.remove("myself");
    	InputStream in = null;
		try{
    		if("load".equalsIgnoreCase(flag)){
    			ContentDAO dao = new ContentDAO(this.getFrameconn());
    			FormFile file=(FormFile)getFormHM().get("picturefile");
    			getFormHM().put("picturefile",null);
    			
    			if(!FileTypeUtil.isFileTypeEqual(file)){
    			    throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("error.fileuploaderror")));
    			}
    			
    			int size=file.getFileSize();
    			if(0 == size)
    				throw new GeneralException(ResourceFactory.getProperty("error.uploadfilempty"));
    				
    			if(size/1024>100*1024){
    				throw new GeneralException("上传文件太大，请不要超过100M");
    			}
    
    			String lfilename = file.getFileName();
    			String filetxt = lfilename.substring(lfilename.lastIndexOf("."));// 扩展名
    			
    			String filename = (String) this.getFormHM().get("filename");
    			filename = PubFunc.keyWord_filter(filename);
    			this.getFormHM().put("filename", "");
    			filename = filename != null ? filename : "";
    			if (filename.length() < 1 && lfilename.length() > 0)
    				filename = lfilename.substring(0, lfilename.lastIndexOf("."));
    
    			String user_name = this.userView.getUserName();
    			// 判断是否有关联的自助用户，有则直接插入自助用户名
    			if (this.userView.getUserFullName() != null
    					&& !"".equals(this.userView.getUserFullName())) {
    				user_name = this.userView.getUserFullName();
    			} else if ( this.userView.getA0100() != null
    					&& this.getUserView().getDbname() != null
    					&& !"".equals(this.userView.getDbname())
    					&& !"".equals(this.userView.getA0100())) {
    				// 判断关联信息是否存在当前人员库中
    				try {
    					// 查询当前人员姓名信息
    					String sql_ = "select A0101 from "
    							+ this.userView.getDbname() + "A01 where A0100='"
    							+ this.userView.getA0100() + "'";
    					this.frowset = dao.search(sql_);
    					if (this.frowset.next()) {
    						// 当人员姓名不为空时，则插入人员库用户名
    						if (this.frowset.getString("A0101") != null && !"".equals(this.frowset.getString("A0101"))) {
    							user_name = this.frowset.getString("A0101");
    						}
    					}				
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    
    			RecordVo vo = new RecordVo("tr_res_file");
    			IDGenerator idg = new IDGenerator(2, this.getFrameconn());
    			String feast_id = idg.getId("tr_res_file.fileid");
    			vo.setInt("fileid", Integer.parseInt(feast_id));
    			vo.setString("name", filename);
    			vo.setString("ext", filetxt);
    			vo.setString("create_user", user_name);
    			Date date = DateUtils.getSqlDate(Calendar.getInstance());
    			vo.setDate("create_time", date);
    			if ("0".equalsIgnoreCase(type)) {
    				String codeitemid = getCodeitemid(dao, itemid);
    				codeitemid = codeitemid != null
    						&& codeitemid.trim().length() > 0 ? codeitemid : "";
    			}
    			vo.setString("url", "no");
    			vo.setString("r0701", itemid);
    			vo.setString("type", type);
    			String userName = this.userView.getUserName();
    			VfsFiletypeEnum vfsFiletypeEnum = VfsFiletypeEnum.doc;
    			VfsModulesEnum vfsModulesEnum = VfsModulesEnum.PX;
    			VfsCategoryEnum vfsCategoryEnum = VfsCategoryEnum.other;
    			String fileName = file.getFileName();
    			in = file.getInputStream();
    			String fieldId = VfsService.addFile(userName, vfsFiletypeEnum, vfsModulesEnum, vfsCategoryEnum,
    					"", in, fileName, "", false);
    			vo.setString("file_id", fieldId);
    			dao.addValueObject(vo);
    		}else if("delete".equalsIgnoreCase(flag)){
    			String checkvalue = (String)hm.get("checkvalue");
    			checkvalue=checkvalue!=null&&checkvalue.trim().length()>0?checkvalue.substring(0, checkvalue.length()-1):"";
    			String[] id = checkvalue.split(",");
    			int n = 0;
    			String checkvalues = "";
    			ArrayList list = new ArrayList();
    			
    			for(int i = 0; i< id.length; i++){
    			    if(n > 0)
    			        checkvalues += ",";
    			    checkvalues += PubFunc.decrypt(SafeCode.decode(id[i]));
    			    n++;
    			    
    			    if(n == 1000){
    			        list.add(checkvalues);
    			        checkvalues = "";
    			        n = 0;
    			    }
    			}
    			
    			if(checkvalues != null && checkvalues.length()>0){
                    list.add(checkvalues);
                }

    			hm.remove("checkvalue");
    			ArrayList sqlList = new ArrayList();
    			ContentDAO dao = new ContentDAO(this.getFrameconn());
    			for(int i = 0; i < list.size(); i++){
    				StringBuffer sql = new StringBuffer();
    				sql.append("select file_id from tr_res_file where fileid in (");
    				sql.append(list.get(i) + ")");
    				sql.append(" and r0701='" + itemid + "'");
    				this.frowset = dao.search(sql.toString());
    				String fileid = "";
    				while(this.frowset.next()) {
    					fileid = this.frowset.getString("file_id");
    					if (StringUtils.isNotEmpty(fileid)) {
    						VfsService.deleteFile(this.getUserView().getUserName(), fileid);
    					}
    				}
    				
    			    StringBuffer delsql = new StringBuffer();
                    delsql.append("delete from tr_res_file where r0701='");
                    delsql.append(itemid);
                    delsql.append("' and fileid in(");
                    delsql.append(list.get(i));
                    delsql.append(")");
                    
                    sqlList.add(delsql.toString());
    			}
    			
    			try {
    				dao.batchUpdate(sqlList);
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    		}
    
    	}catch (Exception e) {
		    e.printStackTrace();
		    throw GeneralExceptionHandler.Handle(e);
        } finally {
        	PubFunc.closeResource(in);
        }
		
		StringBuffer strwhere = new StringBuffer();
        strwhere.append("from tr_res_file where r0701='");
        strwhere.append(itemid);
        strwhere.append("' and (type='");
        if("0".equals(type)){
            strwhere.append("' or type is null or type='");
        }
        strwhere.append(type);
        strwhere.append("')");
        
		StringBuffer sqlstr = new StringBuffer();
        sqlstr.append("select fileid,name,ext,url,create_user,create_time");
		this.getFormHM().put("filesql",sqlstr.toString());
		this.getFormHM().put("columns", "fileid,name,ext,url,create_user,create_time");
		this.getFormHM().put("strwhere", strwhere.toString());
		this.getFormHM().put("itemid", SafeCode.encode(PubFunc.encrypt(itemid)));
		this.getFormHM().put("myself", myself);
	}
	
	private String getCodeitemid(ContentDAO dao,String itemid){
		String codeitemid = "";
		RecordVo vo = new RecordVo("R07");
		vo.setString("r0701", itemid);
		try {
			vo = dao.findByPrimaryKey(vo);
			codeitemid = vo.getString("r0700");
			codeitemid = codeitemid!=null?codeitemid:"";
		} catch (GeneralException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return codeitemid;
	}
}
