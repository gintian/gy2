/**
 * 
 */
package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * <p>Title:SearchRoleListTrans</p>
 * <p>Description:查询角色列表交易</p> 
 * <p>Company:hjsj</p> 
 * create time at:Oct 19, 200612:04:21 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SearchRoleListTrans extends IBusiness {

	public void execute() throws GeneralException {
		String sql="select role_id,role_name,role_desc from t_sys_role where valid=1  order by norder";
		ArrayList fieldlist=new ArrayList();
		Field field=new Field("role_id","role_id");
		field.setDatatype(DataType.STRING);
		field.setVisible(false);
		field.setKeyable(true);
		field.setLength(20);
		fieldlist.add(field);
		field=new Field("role_name",ResourceFactory.getProperty("column.name"));
		field.setDatatype(DataType.STRING);
		field.setLength(30);
		fieldlist.add(field);
		field=new Field("role_desc",ResourceFactory.getProperty("column.desc"));
		field.setDatatype(DataType.STRING);
		field.setLength(250);
		fieldlist.add(field);
		//this.getFormHM().put("sql",sql);
		this.getFormHM().put("fieldlist",fieldlist);
		ArrayList list=new ArrayList();
        try
	    {
        	StringBuffer strsql=new StringBuffer();
        	ContentDAO dao=new ContentDAO(this.getFrameconn());
            this.frowset = dao.search(sql.toString());

	      
	      while(this.frowset.next())
	      {
	          RecordVo vo=new RecordVo("T_SYS_ROLE");
	          vo.setString("role_id",this.getFrowset().getString("role_id"));	          
	          vo.setString("role_name",this.getFrowset().getString("role_name"));
	          vo.setString("role_desc",PubFunc.toHtml(this.getFrowset().getString("role_desc")));	          
	          vo.setString("valid","0");	          
	          list.add(vo);
	      }
	    }
	    catch(SQLException sqle)
	    {
	      sqle.printStackTrace();
	      throw GeneralExceptionHandler.Handle(sqle);
	    }
	    finally
	    {
	        this.getFormHM().put("rolelist",list);	        
	    }
	}

}
