package com.hjsj.hrms.transaction.org.orginfo;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 检查能否撤销、划转
 * @author xujian
 *Mar 17, 2010
 */
public class CanDoItTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList orgcodeitemid=(ArrayList)this.getFormHM().get("orgcodeitemid");
		String d="",maxstartdate="",msg="no";  
		try{
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, 0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(calendar.getTime());
			ContentDAO dao = new ContentDAO(this.frameconn);
			for(int i=0;i<orgcodeitemid.size();i++){
				String sql = "select start_date from organization where codeitemid='"+(String)orgcodeitemid.get(i)+"'";
				this.frecset = dao.search(sql);
				if(this.frecset.next()){
					java.sql.Date temp = this.frecset.getDate("start_date");
					d = sdf.format(temp);
				}
				if(date.equalsIgnoreCase((d))){
					msg="equals";
					break;
				}else{
					if("".equals(maxstartdate)){
						maxstartdate=d;
					}else{
						Date d1=sdf.parse(d);
						Date d2=sdf.parse(maxstartdate);
						if(d1.compareTo(d2)>0)
							maxstartdate=d;
					}
				}
				msg="ok";
			}
			
		}catch(Exception e){
			e.printStackTrace();
			//throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("maxstartdate", maxstartdate);
		}
	}

}
