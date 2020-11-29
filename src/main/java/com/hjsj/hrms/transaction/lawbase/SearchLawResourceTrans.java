/**
 * 
 */
package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.sys.SysPrivBo;
import com.hjsj.hrms.constant.GeneralConstant;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.ResourceParser;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

/**
 * <p>Title:SearchLawResourceTrans</p>
 * <p>Description:查询用户拥有的规章制度分类号</p> 
 * <p>Company:hjsj</p> 
 * create time at:Sep 23, 200611:54:17 AM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchLawResourceTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String flag=(String)this.getFormHM().get("flag");
			String roleid=(String)this.getFormHM().get("roleid");
			String res_flag=(String)this.getFormHM().get("res_flag");
			if(flag==null|| "".equals(flag))
	            flag=GeneralConstant.ROLE;
			if(res_flag==null|| "".equals(res_flag))
				res_flag="0";
			/**资源类型*/
			int res_type=Integer.parseInt(res_flag);
			/**采用预警字段作为其资源控制字段*/
			/**当前被授权用户拥有的资源*/
			SysPrivBo privbo=new SysPrivBo(roleid,flag,this.getFrameconn(),"warnpriv");
			String res_str=privbo.getWarn_str();
			ResourceParser parser=new ResourceParser(res_str,res_type);
			String [] contents = parser.getContent().split(",");
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<contents.length;i++){
				if(contents[i].length()>0)
					sb.append(PubFunc.encrypt(contents[i])+",");
			}
			/**1,2,3*/
			String str_content=","+sb.toString();
			this.getFormHM().put("law_dir",str_content);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
		
	}

}
