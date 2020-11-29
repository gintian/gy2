package com.hjsj.hrms.transaction.sys.dbinit;

import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
 * <p>Title:构库时查看表名是否存在</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Apr 29, 2009:11:25:19 AM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class SeeTableNameTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String msg = "1";
		String tableid = (String)this.getFormHM().get("tableid");
		String tablename = (String)this.getFormHM().get("tablename");
		
		boolean flag=true;
		if(this.checkupfieldname(tableid, tablename)){
			flag = false;
			msg=ResourceFactory.getProperty("kjg.error.clew");
		}else{
			SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.frameconn);
			//判断子集名称是否与子集分类名称相同     guodd  add   2014.12.26
			String tag = "set_a";
			ArrayList list = infoxml.getView_tag(tag);   //获取子集分类名称集合
			for (int i = 0; i < list.size(); i++) {
				if(tablename.equals(list.get(i))){
					flag=false;
					msg=ResourceFactory.getProperty("kjg.error.clew"); 
					break;
				}
			}
		}
		this.getFormHM().put("msg", msg);
	}
	
	//检查表名是否存在
	public boolean checkupfieldname(String id,String name){
		boolean flag=false;
		try{
			String sqls = "select customdesc from fieldset where customdesc= '"+name+"' and fieldsetid <>'"+id+"'";
			ContentDAO dao= new ContentDAO(this.getFrameconn());
			RowSet rs = dao.search(sqls);
			while(rs.next()){
				flag = true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return flag;
	}

}
