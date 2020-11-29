package com.hjsj.hrms.module.recruitment.recruitflow.transaction;

import com.hjsj.hrms.businessobject.infor.multimedia.PhotoImgBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 进入流程环节时查询环节权限
 * @author Administrator
 *
 */
public class SearchAuthorityTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
		try{
			String linkid = (String) this.formHM.get("linkid");
			ContentDAO dao = new ContentDAO(this.frameconn);
			StringBuffer sql = new StringBuffer("select node_id,custom_name");
			sql.append(",member_type,role_id,pos_id,emp_id ");
			sql.append(" from zp_flow_links where id='"+linkid+"'");
			this.frowset = dao.search(sql.toString());
			String member_type = "";
			String role_id = "";
			String pos_id = "";
			String emp_id = "";
			if(this.frowset.next()){
				member_type=this.frowset.getString("member_type");
				role_id=this.frowset.getString("role_id");
				pos_id=this.frowset.getString("pos_id");
				emp_id=this.frowset.getString("emp_id");
			}
			HashMap<String,String> map = new HashMap<String,String>();
			ArrayList memList = new ArrayList();
			if(StringUtils.isNotEmpty(member_type)){
				String[] split = member_type.split(",");	
				for (String member : split) {
					memList.add(member);
				}
			}
			ArrayList list = new ArrayList();
			ArrayList roleList = new ArrayList();
			ArrayList hadrole = new ArrayList();
			if(StringUtils.isNotEmpty(role_id)){
				sql.setLength(0);
				sql.append("select role_id,role_name ");
				sql.append(" from t_sys_role");
				sql.append(" where role_id=?");
				String[] split = role_id.split(",");
				String role_id2 = "";
				for (String id : split) {
					map = new HashMap<String,String>();
					list.clear();
					list.add(id);
					this.frowset = dao.search(sql.toString(),list);
					if(this.frowset.next()){
						role_id2 = PubFunc.encrypt(this.frowset.getString("role_id"));
						map.put("id", role_id2);
						map.put("name", this.frowset.getString("role_name"));
						roleList.add(map);
						hadrole.add(role_id2);
					}
				}
			}
			ArrayList posList = new ArrayList();
			ArrayList hadpos = new ArrayList();
			if(StringUtils.isNotEmpty(pos_id)){
				String[] split = pos_id.split(",");	
				String pos_id2 = "";
				for (String id : split) {
					CodeItem code = AdminCode.getCode("@K",id );
					if(code==null)
						continue;
					map = new HashMap<String,String>();
					pos_id2 = PubFunc.encrypt(code.getCcodeitem());
					map.put("id", pos_id2);
					map.put("name", code.getCodename());
					posList.add(map);
					hadpos.add(pos_id2);
				}
			}
			ArrayList empList = new ArrayList();
			ArrayList hademp = new ArrayList();
			if(StringUtils.isNotEmpty(emp_id)){
				String[] split = emp_id.split(",");
				String emp_id2 = "";
				String nbase = "";
				String a0100 = "";
				for (String nid : split) {
					nbase = nid.substring(0, 3);
					a0100 = nid.substring(3);
					sql.setLength(0);
					sql.append("select a0101 ");
					sql.append(" from "+nbase+"A01 ");
					sql.append(" where a0100="+a0100);
					this.frowset = dao.search(sql.toString());
					map = new HashMap<String,String>();
					if(this.frowset.next()){
						emp_id2 = PubFunc.encrypt(nid);
						map.put("id", emp_id2);
						map.put("name", this.frowset.getString("a0101"));
						map.put("photo",getPhotoPath(nbase,a0100));
						empList.add(map);
						hademp.add(emp_id2);
					}
				}
			}
			this.formHM.put("search", "search");
			this.formHM.put("roleList", roleList);
			this.formHM.put("posList", posList);
			this.formHM.put("empList", empList);
			this.formHM.put("hadrole", hadrole);
			this.formHM.put("hadpos", hadpos);
			this.formHM.put("hademp", hademp);
			this.formHM.put("hadmem", memList);
			this.formHM.put("linkid", linkid);
		}catch(Exception e){
			e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e); 
		}
	}
	
	//从选人控件copy过来的
    private String getPhotoPath(String nbase, String a0100) {
        PhotoImgBo imgBo = new PhotoImgBo(this.frameconn);
        return imgBo.getPhotoPathLowQuality(nbase, a0100);
    }

}
