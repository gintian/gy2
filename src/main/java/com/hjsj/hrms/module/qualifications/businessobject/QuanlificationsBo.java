package com.hjsj.hrms.module.qualifications.businessobject;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.VfsFileEntity;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import org.apache.axis.utils.StringUtils;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * 评审条件
 * create by liubq	
 * time  2015-11-19 11:19:49
 * 
 **/
public class QuanlificationsBo {

	// 基本属性
	private Connection conn = null;
	private UserView userview;
	public QuanlificationsBo(Connection conn, UserView userview){
		this.conn = conn;
		this.userview=userview;
	}

    /**
     * 当前用户的单位编码
     * @param busiId 
     * 
     * @param user
     * @return
     */
    public String getWhereSql(String busiId){ 
    	StringBuffer wheresql = new StringBuffer();
    	String b0110str = this.userview.getUnitIdByBusi(busiId);
    	wheresql.append(" where (1=2");
    	if("UN`".equals(b0110str)){
    		wheresql.append(" or 1=1 ");
    	}else{
    		String[] sp = b0110str.split("`");
    		for(int i =0;i<sp.length;i++){
    			String unit = sp[i];
    			if(unit.contains("UN")){
    				unit=unit.replace("UN", "");
    			}
    			if(unit.contains("UM")){
    				unit=unit.replace("UM", "");
    			}
    			int length = unit.length();
    			wheresql.append(" or b0110 like '"+unit+"%'");
    			for(int j =1;j<(length/2);j++){
        			wheresql.append(" or b0110 = '"+unit.substring(length-(2*j))+"'");
    			}
    			wheresql.append(" or nullif(b0110,'') is null");
    		}		
    	}
    	wheresql.append(")");
    	return wheresql.toString();
    }
    public ArrayList getAttachmentList(String condition_id) throws GeneralException{//获取列表
    	//获取列表
    	//20/3/6 xus vfs改造
    	VfsModulesEnum vfsModulesEnum = VfsModulesEnum.ZC;
    	
    	condition_id = PubFunc.encrypt(condition_id);
    	
    	List<VfsFileEntity> fileEntityGroup = null;
    	ArrayList al = new ArrayList();
    	try {
    		fileEntityGroup = VfsService.getFileEntityGroup(condition_id, vfsModulesEnum);
    		for(VfsFileEntity vfsFileEntity:fileEntityGroup) {
    			LazyDynaBean ldb= new LazyDynaBean();
    			String fileid = vfsFileEntity.getFileid();
    			String houzhui = vfsFileEntity.getExtension();
    			String filename = vfsFileEntity.getName();
    			if("xls".equalsIgnoreCase(houzhui)|| "xlsx".equalsIgnoreCase(houzhui)){
					ldb.set("src","/images/excell.png");
				}else if("doc".equalsIgnoreCase(houzhui)|| "docx".equalsIgnoreCase(houzhui)){
					ldb.set("src","/images/word.png");
				}else if("ppt".equalsIgnoreCase(houzhui)|| "pptx".equalsIgnoreCase(houzhui)){
					ldb.set("src","/images/ppt.png");
				}else if("pdf".equalsIgnoreCase(houzhui)){
					ldb.set("src","/images/PDF.png");
				}else if("txt".equalsIgnoreCase(houzhui)){
					ldb.set("src","/images/txt.png");
				}
    			ldb.set("houzhui",houzhui);
				ldb.set("name",filename);
				ldb.set("fileid",fileid);
				al.add(ldb);
    		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return al;
	}

	public boolean canOper(String user,String unit){
		boolean flag = false;
		if(user==null||"".equals(user)){
			flag = false;
		}else if("UN`".equals(user)){
			flag = true;
		}else{
			user = user.replace("UN", "");
			user = user.replace("UM", "");
			String[] users = user.split("`");
			for(int i =0;i<users.length;i++){
				int len = 0;
				if(!StringUtils.isEmpty(unit)){
					len = unit.length();
				}
				if(len<=users[i].length()){
					if(unit.equals(users[i]))
						flag = true;
				}else{
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
}
