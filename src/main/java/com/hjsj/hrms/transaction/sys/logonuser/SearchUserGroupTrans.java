/**
 * 
 */
package com.hjsj.hrms.transaction.sys.logonuser;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.Des;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;
/**
 * <p>Title:SearchUserGroupTrans</p>
 * <p>Description:查询用户及用户组信息交易</p>
 * <p>Company:hjsj</p>
 * <p>create time:2006-6-9:15:06:48</p>
 * @author chenmengqing
 * @version 1.0
 * 
 */
public class SearchUserGroupTrans extends IBusiness {

	public void execute() throws GeneralException {
		String username=(String)this.getFormHM().get("username");

		if(username==null|| "".equals(username))
			return;
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			//username=PubFunc.ToGbCode(username);
			RecordVo vo=new RecordVo("operuser");
			vo.setString("username",username);
			vo=dao.findByPrimaryKey(vo);
			if(ConstantParamter.isEncPwd(this.getFrameconn()))
			{
				Des des=new Des();
				vo.setString("password",des.DecryPwdStr(vo.getString("password")));
			}
			vo.setString("tablepriv","");
			String hz=null;
			if(vo!=null)
			{
				String dept_id=vo.getString("org_dept");
				if(dept_id==null|| "".equals(dept_id))
					vo.setString("fieldpriv", "");  //用作显示归属单位的汉字描述
				else
				{
					String[] itemarr=StringUtils.split(dept_id,"`");
					StringBuffer buf=new StringBuffer();
					for(int i=0;i<itemarr.length;i++)
					{
						if(itemarr[i].length()<2)
							continue;
						String codesetid=itemarr[i].substring(0,2);
						String value=itemarr[i].substring(2);
						if(value.length()==0)
						{
							hz="全部";	
						}
						else
						{
							hz=AdminCode.getCodeName(codesetid, value);
						}
						buf.append(hz);
						buf.append(",");
					}
					vo.setString("fieldpriv", buf.toString());
					
				}
			}
			this.getFormHM().put("user_vo",vo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
