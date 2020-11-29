package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.lawbase.LawDirectory;
import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 修改规章制度的权限
 * @author Owner
 *
 */
public class UpdateLawBaseRoleTrans extends IBusiness {
	 public void execute() throws GeneralException 
	 {
		 ArrayList list=(ArrayList)this.getFormHM().get("selectedrolelist");	
		 String base_id=(String)this.getFormHM().get("base_id");
		// xuj add 2013-2-2  解决如果分类id加密后需转换明文
			Pattern pattern = Pattern.compile("[0-9]*");
	        Matcher isNum = pattern.matcher(base_id);
			if(!isNum.matches()&&!"root".equals(base_id)&&!"null".equals(base_id)&&base_id!=null&&base_id.length()>0){
				base_id = PubFunc.decrypt(SafeCode.decode(base_id));
			}
		 String basetype=(String)this.getFormHM().get("basetype");
		 if(base_id==null||base_id.length()<=0)
			 return;
	     if(list==null)
	        return ;
	     try
	     {
	    	 LawDirectory lawDirectory=new LawDirectory();
	    	 for(int i=0;i<list.size();i++)
	         {
		    	 RecordVo vo=(RecordVo)list.get(i);
		    	 String role_id= vo.getString("role_id");
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
	    	 //this.getFormHM().put("sp_result","保存成功！");
	     }catch(Exception e)
	     {
	    	 //this.getFormHM().put("sp_result","保存失败！");
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
