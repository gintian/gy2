package com.hjsj.hrms.transaction.kq.machine;

import com.hjsj.hrms.businessobject.kq.machine.SyncCardData;
import com.hjsj.hrms.businessobject.param.DocumentSyncBo;
import com.hjsj.hrms.businessobject.param.DocumentSyncXML;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.log4j.Category;

import java.util.Date;
import java.util.List;
/**
 * 手动同步考勤数据
 * <p>Title:SyncKqcardDataTrans.java</p>
 * <p>Description>:SyncKqcardDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Nov 18, 2010 11:03:09 PM</p>
 * <p>@version: 5.0</p>
 * <p>@author: s.xin
 */
public class SyncKqcardDataTrans  extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	private static Category cat = Category.getInstance("com.hjsj.hrms.transaction.kq.machine.SyncKqcardDataTrans");
	public void execute() throws GeneralException 
	{
		 String start_date=(String)this.getFormHM().get("start_date");
		 String start_hh=(String)this.getFormHM().get("start_hh");
		 String start_mm=(String)this.getFormHM().get("start_mm");
		 String end_date=(String)this.getFormHM().get("end_date");
		 String end_hh=(String)this.getFormHM().get("end_hh");
		 String end_mm=(String)this.getFormHM().get("end_mm");
		 if(start_hh==null||start_hh.length()<=0)
				start_hh="00";
		 if(start_mm==null||start_mm.length()<=0)
				start_mm="00";		
		 if(end_hh==null||end_hh.length()<=0)
				end_hh="23";
		 if(end_mm==null||end_mm.length()<=0)
				end_mm="59";
		 String start_time=start_hh+":"+start_mm;
		 String end_time=end_hh+":"+end_mm;
		 SyncCardData syncCardData=new SyncCardData(this.getFrameconn());
		 DocumentSyncBo bo = new DocumentSyncBo(this.getFrameconn());
		 DocumentSyncXML xml = new DocumentSyncXML(this.getFrameconn(), bo.getConnXML());
			// 获得数据库信息
		 List list = xml.getBeanList("/datasources/datasource");
		 if(!list.isEmpty())
		 {
			 for (int i = 0; i < list.size(); i++) {
					LazyDynaBean bean = (LazyDynaBean) list.get(i);
					String status = (String) bean.get("status");
					status = status == null ? "0" : status;
					if("1".equals(status) && syncCardData.isAllowSync(bean)) {
						try {
							syncCardData.sycnCardData(start_date,start_time,end_date,end_time);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							cat.error("考勤数据同步发生异常:"+e.toString());
							cat.error("考勤数据同步发生异常", e);
							throw GeneralExceptionHandler.Handle(new GeneralException("考勤数据同步发生异常！"));
						}
					}
				}		
		 }else
		 {
			 if(syncCardData.isAllowSync())
				{
					Date date=new Date();
					end_date=DateUtils.format(date, "yyyy.MM.dd");
					String _time=DateUtils.format(date, "HH:mm");
					Date _start_date=DateUtils.addDays(date, -1);
					start_date=DateUtils.format(_start_date, "yyyy.MM.dd");
					try {
						syncCardData.sycnCardData(start_date,_time,end_date,_time);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		 }
	}

}
