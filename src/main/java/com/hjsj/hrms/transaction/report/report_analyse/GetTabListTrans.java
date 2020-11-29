package com.hjsj.hrms.transaction.report.report_analyse;

import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.util.HashMap;
import java.util.Map;

public class GetTabListTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String sortid=(String)this.getFormHM().get("sortid");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			RecordVo selfVo=null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null) //自助用户关联业务用户
				selfVo=ttorganization.getSelfUnit(userView.getS_userName());
			else
				selfVo=ttorganization.getSelfUnit(userView.getUserName());
			TnameBo tnamebo  = new TnameBo(this.getFrameconn());
			HashMap scopeMap = tnamebo.getScopeMap();
			java.util.Iterator it = scopeMap.entrySet().iterator();
			String tabids = "";
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				String keys = (String) entry.getKey();
				tabids+= keys+",";
				
			}
			if(tabids.length()>0)
				tabids=tabids.substring(0,tabids.length()-1);
			StringBuffer sql = new StringBuffer();
			sql.append("select tc.tabid,tname.name from treport_ctrl tc,tname where tc.tabid=tname.tabid and tc.unitcode='"+selfVo.getString("unitcode")+"'  and tname.tsortid="+sortid+" ");
			
			if(tabids.length()>0)
				sql.append(" and tname.tabid not in("+tabids+") ");
			sql.append(" order by tc.tabid ");
			StringBuffer strsql=new StringBuffer("");
			strsql.append("select reporttypes,analysereports from tt_organization  where  unitcode='");
			strsql.append(selfVo.getString("unitcode"));
			strsql.append("'");				
			RowSet rs =dao.search(strsql.toString());
			String analysereports ="";
			if(rs.next())
			{
				 analysereports = Sql_switcher.readMemo(rs,"analysereports");
			}
			if(analysereports!=null&&analysereports.length()>0){
				String reports [] =	analysereports.split(",");
				String reportids ="";
				 ttorganization=new TTorganization(this.getFrameconn());
				HashMap reportmap =  ttorganization.getReportTsort();
				for(int i=0;i<reports.length;i++){
					if(reports[i].trim().length()>0&&reportmap.get(reports[i].trim())!=null&&reportmap.get(reports[i].trim()).equals(sortid)&&reportmap.get(reports[i].trim())!=null){
						reportids+=reports[i].trim()+",";
					}
				}
				if(reportids.length()>1){
					reportids = reportids.substring(0,reportids.length()-1);
					sql.setLength(0);
					sql.append("select tabid,name from tname where ");
					sql.append(" tabid in ("+reportids+")");
					if(tabids.length()>0)
						sql.append(" and tname.tabid not in("+tabids+") ");
					sql.append(" order by tabid ");
				}
				}
			this.frowset=dao.search(sql.toString());
		//	ArrayList list=new ArrayList();
			StringBuffer str=new StringBuffer("");
			while(this.frowset.next())
			{
				//CommonData data=new CommonData(this.frowset.getString(1),this.frowset.getString(2));
				//list.add(data);
				if(userView.isHaveResource(com.hrms.hjsj.sys.IResourceConstant.REPORT ,this.frowset.getString("tabid")))
					str.append("#~#"+this.frowset.getString(1)+"@#@("+this.frowset.getString(1)+")"+this.frowset.getString(2));
			}
			if(str.length()>3)
				this.getFormHM().put("str",SafeCode.encode(str.substring(3)));
			else
				this.getFormHM().put("str","");
			//this.getFormHM().put("tablist",list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
