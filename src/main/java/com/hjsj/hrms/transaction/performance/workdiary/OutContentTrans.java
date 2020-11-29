package com.hjsj.hrms.transaction.performance.workdiary;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;

public class OutContentTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String p0100 = (String)this.getFormHM().get("p0100");
		p0100=p0100!=null&&p0100.trim().length()>0?p0100:"";
		p0100 = PubFunc.decrypt(p0100);
		
		
		String pid = (String)this.getFormHM().get("pid");
		pid=pid!=null&&pid.trim().length()>0?pid:"";
		if(p0100.length()>0&&pid.length()>0){
			RecordVo p01Vo=new RecordVo("p01");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			p01Vo.setString("p0100",p0100);			
			try {
				p01Vo=dao.findByPrimaryKey(p01Vo);
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String memo =p01Vo.getString(pid);
			memo=memo!=null&&memo.length()>0?memo.replaceAll("\n","<br>"):"";
			
			this.getFormHM().put("content",SafeCode.encode(memo));
			FieldItem tempitem = DataDictionary.getFieldItem(pid);
			this.getFormHM().put("titles",tempitem.getItemdesc());
		}
	}

}
