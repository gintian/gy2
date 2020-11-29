package com.hjsj.hrms.transaction.sys.dbinit.indexexport;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
 * <p>Title:指标导出页面生成</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 8, 2008:2:18:17 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class ExportTableTrans extends IBusiness{

	public void execute() throws GeneralException {
		ArrayList exportlist = this.getindexexport(); 
		this.getFormHM().put("exportlist", exportlist);
	}
	
	public ArrayList getindexexport(){
		ArrayList list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		CommonData da = new CommonData();
		try{
			EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
			VersionControl ver_ctrl=new VersionControl();	
			switch(Sql_switcher.searchDbServer())
			{
				case Constant.MSSQL:
				{
					buf.append("select * from fieldset order by SubString(fieldsetid,1,1),displayorder");
					RowSet st = dao.search(buf.toString());
					while(st.next()){
						if(st.getString("fieldsetid").startsWith("Y")||st.getString("fieldsetid").startsWith("W")||st.getString("fieldsetid").startsWith("V")||st.getString("fieldsetid").startsWith("H")){
							if(!lockclient.isHaveBM(31))
								continue;
							if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
								continue;
						}
						if(st.getString("fieldsetid").startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
							continue;
						da = new CommonData();
						da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
						da.setDataValue(st.getString("fieldsetid"));
						list.add(da);
					}
					break;
				}
				case Constant.ORACEL:
				{
					buf.append("select * from fieldset order by SubStr(fieldsetid,1,1),displayorder");
					RowSet st = dao.search(buf.toString());
					while(st.next()){
						if(st.getString("fieldsetid").startsWith("Y")||st.getString("fieldsetid").startsWith("W")||st.getString("fieldsetid").startsWith("V")||st.getString("fieldsetid").startsWith("H")){
							if(!lockclient.isHaveBM(31))
								continue;
							if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
								continue;
						}
						if(st.getString("fieldsetid").startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
							continue;
						da = new CommonData();
						da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
						da.setDataValue(st.getString("fieldsetid"));
						list.add(da);
					}
					break;
				}
				case Constant.DB2:
				{
					buf.append("select * from fieldset order by SubStr(fieldsetid,1,1),displayorder");
					RowSet st = dao.search(buf.toString());
					while(st.next()){
						if(st.getString("fieldsetid").startsWith("Y")||st.getString("fieldsetid").startsWith("W")||st.getString("fieldsetid").startsWith("V")||st.getString("fieldsetid").startsWith("H")){
							if(!lockclient.isHaveBM(31))
								continue;
							if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
								continue;
						}
						if(st.getString("fieldsetid").startsWith("H")&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
							continue;
						da = new CommonData();
						da.setDataName(st.getString("fieldsetid")+" "+st.getString("customdesc"));
						da.setDataValue(st.getString("fieldsetid"));
						list.add(da);
					}
					break;
				}
			}
	
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

}
