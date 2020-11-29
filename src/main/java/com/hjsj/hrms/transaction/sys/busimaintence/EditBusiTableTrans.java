package com.hjsj.hrms.transaction.sys.busimaintence;

import com.hjsj.hrms.businessobject.sys.busimaintence.BusiSelStr;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.util.HashMap;
/**
 * 
 * <p>Title:字典维护(修改子集)</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Sep 17, 2008:3:11:47 PM</p>
 * @author wangyao
 * @version 1.0
 *
 */
public class EditBusiTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm=this.getFormHM();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String mid = (String)this.getFormHM().get("mid");
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String returnvalue1=(String) reqhm.get("from");
		reqhm.remove("from");
		hm.put("returnvalue1",returnvalue1);
		RecordVo busiTable=new RecordVo("t_hr_busiTable");
		if(reqhm.containsKey("fieldsetid")){     //如果此映射包含对于指定的键的映射关系，则返回 true。
		String fieldsetid=(String) reqhm.get("fieldsetid");
		reqhm.remove("fieldsetid");
		busiTable.setString("fieldsetid",fieldsetid);
		try {
			busiTable=dao.findByPrimaryKey(busiTable);  //查找数据对象
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("没有找到子集！");
		}
		String[] infogroup=busiTable.getString("classpre").split(",");
		hm.put("selcheck",BusiSelStr.getInfoGroup(infogroup));
		hm.put("busiTable",busiTable);
		}else{
			busiTable=(RecordVo) hm.get("busiTable");
			String changeflag = busiTable.getString("changeflag"); //年月,new
			String fid = busiTable.getString("fieldsetid"); //代号 ,new
			String[] classper=(String[]) hm.get("classper");
			String classpre="";
			if(classper!=null){
				for(int i=0;i<classper.length;i++){
					if(i==0){
						classpre=classpre+classper[i];
					}else{
						classpre=classpre+","+classper[i];
					}
				}
			}
			busiTable.setString("classpre",classpre);
			try {
				dao.updateValueObject(busiTable);  //批量更新数据;
				FieldSet fs = DataDictionary.getFieldSetVo(fid.toUpperCase());
				if(fs!=null){
					fs.setCustomdesc(busiTable.getString("customdesc"));
					fs.setFieldsetdesc(busiTable.getString("fieldsetdesc"));
				}
//				String fieldsetid=(String) reqhm.get("fieldsetid");
//				reqhm.remove("fieldsetid");
				BusiSelStr bss = new BusiSelStr();
				int Sids = bss.initfield(fid, this.getFrameconn());
				bss.rebirth(fid,changeflag,Sids,mid,this.getFrameconn());
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("修改子集异常！");
			}
		}
	}

}
