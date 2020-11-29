package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;

public class DeleteAllRoleTrans extends IBusiness {
	public void execute() throws GeneralException 
	 {
		ContentDAO dao=new ContentDAO (this.getFrameconn());
		String basetype = (String)this.formHM.get("basetype");
		String sqlrole = "select * from t_sys_role";
		String sqlstruct = "select base_id from law_base_struct where basetype = "+ basetype;
		String sqlfile = "select file_id from law_base_file";
		ArrayList baselist = new ArrayList();
		ArrayList filelist = new ArrayList();
		try {
			RowSet rs1=dao.search(sqlstruct);
			while(rs1.next()){
				baselist.add(rs1.getString("base_id"));
			}
			RowSet rs2 = dao.search(sqlrole);
			LawDirectory lawDirectory=new LawDirectory();
			if(baselist.size()>0)
				sqlfile +=" where base_id in (";
			for(int j=0;j<baselist.size();j++){
				String base_id = (String)baselist.get(j);
				sqlfile+="'"+base_id+"',";
		    	 while(rs2.next())
		         {
			    	 String role_id= rs2.getString("role_id");
			    	 if(role_id==null||role_id.length()<=0)
						 continue;
			    	 SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
					 String res_str=privbo.getWarn_str();
					 ResourceParser parser = null;
					 if("1".equalsIgnoreCase(basetype))
						 parser=new ResourceParser(res_str,IResourceConstant.LAWRULE);
					 if("5".equalsIgnoreCase(basetype))
						 parser=new ResourceParser(res_str,IResourceConstant.DOCTYPE);
					 if("4".equalsIgnoreCase(basetype))
						 parser=new ResourceParser(res_str,IResourceConstant.KNOWTYPE);
	                 String role_str=parser.getContent();
	                 role_str=lawDirectory.updateRoleContent(role_str,base_id);
	                 parser.reSetContent(role_str);
					 res_str=parser.outResourceContent();								
					 saveResourceString(role_id,GeneralConstant.ROLE,res_str);
		         }
		    	 rs2.beforeFirst();
			}
			sqlfile = sqlfile.substring(0,sqlfile.length()-1);
			sqlfile +=")";
			rs2.beforeFirst();
			if(baselist.size()>0){
				RowSet rs3 = dao.search(sqlfile);
				while(rs3.next()){
					filelist.add(rs3.getString("file_id"));
				}
			}
			for(int j=0;j<filelist.size();j++){
				String base_id = (String)filelist.get(j);
		    	 while(rs2.next())
		         {
			    	 String role_id= rs2.getString("role_id");
			    	 if(role_id==null||role_id.length()<=0)
						 continue;
			    	 SysPrivBo privbo=new SysPrivBo(role_id,GeneralConstant.ROLE,this.getFrameconn(),"warnpriv");
					 String res_str=privbo.getWarn_str();
					 ResourceParser parser=new ResourceParser(res_str,IResourceConstant.LAWRULE_FILE);	
	                 String role_str=parser.getContent();
	                 role_str=lawDirectory.updateRoleContent(role_str,base_id);
	                 parser.reSetContent(role_str);
					 res_str=parser.outResourceContent();								
					 saveResourceString(role_id,GeneralConstant.ROLE,res_str);
		         }
		    	 rs2.beforeFirst();
			}
			
	    	 this.getFormHM().put("sp_result","保存成功！");
	     }catch(Exception e)
	     {
	    	 this.getFormHM().put("sp_result","保存失败！");
	    	 e.printStackTrace();
	     }
	     
	 }
	private void saveResourceString(String role_id,String flag,String res_str)
	 {
	        if(res_str==null)
	        	res_str="";
	        RecordVo vo=new RecordVo("t_sys_function_priv");
	        vo.setString("id",role_id);
	        vo.setString("status",flag/*GeneralConstant.ROLE*/);
	        vo.setString("warnpriv",res_str);
	        SysPrivBo sysbo=new SysPrivBo(vo,this.getFrameconn());
	        sysbo.save();        
	 }
}
