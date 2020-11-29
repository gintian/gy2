/**
 * 
 */
package com.hjsj.hrms.transaction.sys;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p> 
 * <p>Company:hjsj</p> 
 * create time at:Jul 14, 20063:09:50 PM
 * @author chenmengqing
 * @version 4.0
 */
public class SaveUserModulePrivTrans extends IBusiness {
	/**
	 * 保存授权信息
	 * @param userlist
	 * @param fieldlist
	 * @param type
	 * @throws Exception
	 */
	private void saveModulePriv(ArrayList userlist,ArrayList fieldlist,int type)throws Exception
	{
        DynaBean dbean=null;
        String name=null;
        String fieldname=null;
        FieldItem item=null;
        String value=null;
        int idx=0;
        /**暂定20个业务模块*/
        StringBuffer module=new StringBuffer();
        module.append("00000000000000000000000");
        try
        {
	        ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(type==0)//operuser
			{
				ArrayList templist=new ArrayList();
				for(int i=0;i<userlist.size();i++)
				{
					dbean=(LazyDynaBean)userlist.get(i);
					name=(String)dbean.get("username");
					for(int j=0;j<fieldlist.size();j++)
					{
						item=(FieldItem)fieldlist.get(j);
						fieldname=item.getItemid();
						value=(String)dbean.get(fieldname);
						if(value==null|| "".equals(value))
							value="0";
						idx=Integer.parseInt(fieldname.substring(1));
						module.setCharAt(idx,value.charAt(0));
					}
					RecordVo vo=new RecordVo("operuser");
					vo.setString("username",name);
					vo.setString("module_ctrl",module.toString());
					templist.add(vo);
	
				}
				dao.updateValueObject(templist);
			}
			else
			{
				String dbpre=null;
				for(int i=0;i<userlist.size();i++)
				{
					dbean=(LazyDynaBean)userlist.get(i);
					name=(String)dbean.get("a0100");
					dbpre=(String)dbean.get("dbpre");
					for(int j=0;j<fieldlist.size();j++)
					{
						item=(FieldItem)fieldlist.get(j);
						fieldname=item.getItemid();
						value=(String)dbean.get(fieldname);
						if(value==null|| "".equals(value))
							value="0";
						idx=Integer.parseInt(fieldname.substring(1));
						module.setCharAt(idx,value.charAt(0));
					}
					RecordVo vo=new RecordVo(dbpre+"a01");
					vo.setString("a0100",name);
					vo.setString("groups",module.toString());
					dao.updateValueObject(vo);				
				}
			}
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ee);
        }
	}
	
	public void execute() throws GeneralException {
		 int type=0;
		 try
		 {
			/**控制显示前台的页签*/ 
			String flag=(String)this.getFormHM().get("flag");
			if(flag==null|| "".equals(flag))
				flag="1";
			if("1".equals(flag))
				type=1;
			else
				type=0;
			ArrayList list=(ArrayList)this.getFormHM().get("fieldlist");
			ArrayList userlist=(ArrayList)this.getFormHM().get("list");
			if(list==null||userlist==null)
				return;
			saveModulePriv(userlist,list,type);
		 }
		 catch(Exception ex)
		 {
			 ex.printStackTrace();
			 throw GeneralExceptionHandler.Handle(ex);
		 }
	}

}
