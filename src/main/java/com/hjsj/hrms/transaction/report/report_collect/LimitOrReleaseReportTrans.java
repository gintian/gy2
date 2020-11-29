package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.hire.SendEmail;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

/**
 * 
 * <p>Title:</p>
 * <p>Description:封帐或解封</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 22, 2006:11:49:47 AM</p>
 * @author 邓灿
 * @version 1.0
 *
 */
public class LimitOrReleaseReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String tabid=(String)this.getFormHM().get("tabid");
		String unitcode=(String)this.getFormHM().get("unitcode");
		String type=null;
		String flag="";
		if(hm.get("flag")!=null){
			flag=(String)hm.get("flag");
		}else
			flag="1";
		if("1".equals(flag)){
			if(hm!=null&&hm.get("type")!=null)
				type=(String)hm.get("type");    // 1：封帐 2:打回
			String desc=" ";
			if(this.getFormHM().get("desc")!=null)
			{
				desc=(String)this.getFormHM().get("desc");
				desc=desc.replaceAll("&&","\r\n");
			}
			TnameBo tnameBo=new TnameBo(this.getFrameconn());
		
			int status=3;  //封存
			if(type!=null) 
			{
				status=2; //打回
			
			}
			tnameBo.upOrInsertReport_ctrl(unitcode,tabid,desc,status);
			//发送邮件
			if(status==2)
			{
				SendEmail sendEmail=new SendEmail();
				sendEmail.setInfo();
				ArrayList infoList=tnameBo.getSendMailInfo(unitcode,this.getUserView().getUserName(),desc,tabid);
				for(int i=0;i<infoList.size();i++)
				{
					LazyDynaBean abean=(LazyDynaBean)infoList.get(i);
					String sendTo=(String)abean.get("sendTo");
					String subject=(String)abean.get("subject");
					String context=(String)abean.get("context");
					if(sendTo.length()>0)
						sendEmail.send(sendTo,subject,context);
				
				}
			}
		}else{
			try{
				String tsort=(String)hm.get("tsort");
				String selfUnitcode=(String)hm.get("selfUnitcode");
				String[] unitcodes=(String[])this.getFormHM().get("right_fields");
				TnameBo tnameBo=new TnameBo(this.getFrameconn());
				String desc=" ";
				if(this.getFormHM().get("desc")!=null)
				{
					desc=(String)this.getFormHM().get("desc");
					desc=desc.replaceAll("&&","\r\n");
				}
				HashMap hmp=new HashMap();
				HashMap hmm=new HashMap();
				HashSet sset=new HashSet();
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				for(int i=0;i<unitcodes.length;i++){
					HashSet set=new HashSet();
					if(tnameBo.isReject(unitcodes[i], tsort)){
						
					}else{
						tnameBo.setUnitsStatus(tsort, unitcodes[i], selfUnitcode,this.userView.getUserName(),desc,set,hmm, unitcodes[i],sset);
						String sql="update treport_ctrl set status = 2,currappuser = username,description='"+desc+"' where tabid in (select tabid from tname where tsortid="+tsort+") and unitcode='"+unitcodes[i]+"' and status=1";
						dao.update(sql);
					}
				}
				for(Iterator it=hmm.entrySet().iterator();it.hasNext();){
					Map.Entry entry = (Map.Entry) it.next();
					String keys = (String) entry.getKey();
					String values = (String)entry.getValue();
					String unitsname=this.findUnits(values);
					String sql1="select * from treport_ctrl where tabid in(select tabid from tname where tsortid="+tsort+")and unitcode='"+keys+"'";
					this.frowset=dao.search(sql1);
					while(this.frowset.next()){
						String tabid1=this.frowset.getString("tabid");
						String descds=Sql_switcher.readMemo(this.frowset, "description");
						String descriptionq=descds+"\r\n"+unitsname.substring(0,unitsname.length()-1)+"因："+desc+" 被驳回！";
						String sql="update treport_ctrl set status = 2,currappuser = username,description='"+descriptionq+"' where tabid="+tabid1+" and unitcode='"+keys+"' and status=1";
						try {
							dao.update(sql);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					String description=unitsname.substring(0,unitsname.length()-1)+"因："+desc+" 被驳回！";
					
					
					tnameBo.sendMail(keys, this.userView.getUserName(), description, tsort);
				}
	//			for(Iterator it=set.iterator();it.hasNext();){
	//				String code=(String)it.next();
	//				if(code!=null&&code.length()!=0){
	//					tnameBo.sendMail(code, this.userView.getUserName(), desc, tsort);
	//				}
	//			}
			}catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	public String findUnits(String unitcodes){
		String units="";
		
		String parents="";
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		String sql="select * from tt_organization where unitcode in("+unitcodes.substring(0,unitcodes.length()-1)+") ";
		try {
			TnameBo tnameBo=new TnameBo(this.getFrameconn());
			
			Calendar d=Calendar.getInstance();
			int yy=d.get(Calendar.YEAR);
			int mm=d.get(Calendar.MONTH)+1;
			int dd=d.get(Calendar.DATE);
			String date = tnameBo.getBusinessDate();
			if(date!=null&&date.trim().length()>0)
			{
				d.setTime(Date.valueOf(date));
				yy=d.get(Calendar.YEAR);
				mm=d.get(Calendar.MONTH)+1;
				dd=d.get(Calendar.DATE);
			}
			StringBuffer ext_sql = new StringBuffer();
			ext_sql.append(" and ( "+Sql_switcher.year("end_date")+">"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+">"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("end_date")+"="+yy+" and "+Sql_switcher.month("end_date")+"="+mm+" and "+Sql_switcher.day("end_date")+">="+dd+" ) ) ");
			ext_sql.append(" and ( "+Sql_switcher.year("start_date")+"<"+yy);
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"<"+mm+" ) ");
			ext_sql.append(" or ( "+Sql_switcher.year("start_date")+"="+yy+" and "+Sql_switcher.month("start_date")+"="+mm+" and "+Sql_switcher.day("start_date")+"<="+dd+" ) ) ");
			this.frowset=dao.search(sql+ext_sql.toString());
			while(this.frowset.next()){
				units+=this.frowset.getString("unitname")+",";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return units;
	}
	
}
