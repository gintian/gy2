package com.hjsj.hrms.transaction.train.request;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:培训班</p>
 * <p>Description:删除培训班</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:2007-12-13 下午06:07:55</p>
 * @author lilinbing
 * @version 4.0
 */
public class DelTrainTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String tablename = (String)this.getFormHM().get("tablename");
		tablename=tablename!=null&&tablename.length()>0?tablename:"";
		
		String itemid = (String)this.getFormHM().get("itemid");
		itemid=itemid!=null&&itemid.length()>0?itemid:"";
		
		String keyid = (String)this.getFormHM().get("keyid");
		keyid=keyid!=null&&keyid.length()>0?keyid:"";
		
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String flag = "no";
		String pivItemid = "";
		if("r37".equalsIgnoreCase(tablename)) {
			pivItemid = "r3703";
		} else if("r41".equals(tablename.toLowerCase())) {
			pivItemid = "r4103";
		}
		
		try {
			StringBuffer buf = new StringBuffer();
			buf.append("delete from ");
			buf.append(tablename);
			buf.append(" where "+itemid+"=?");
			if(!this.userView.isSuper_admin()){
				if(StringUtils.isEmpty(pivItemid)) {
					buf.append(" and 1=2");
				} else {
					String whereStr = TrainCourseBo.getUnitIdByBusiWhere(this.userView);
					whereStr = whereStr.replaceFirst("where", "and");
					buf.append(" and " + pivItemid + " in (select r3101 from r31 where 1=1 " + whereStr + ")");
				}
			}
			
			ArrayList valuelist = new ArrayList();
			String arr[] = keyid.split(",");
			for(int i=0;i<arr.length;i++){
				String id = PubFunc.decrypt(SafeCode.decode(arr[i]));
				if(id!=null&&id.length()>0){
					ArrayList list = new ArrayList();
					list.add(id);
					valuelist.add(list);
				}
			}
			
			dao.batchUpdate(buf.toString(),valuelist);
			flag="ok";
		}  catch (SQLException e) {
			e.printStackTrace();
		}
		this.getFormHM().put("flag",flag);
		
	}

}
