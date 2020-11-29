package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossPayManagement;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:2011-6-15 9:59:35:${time}</p>
 * @author dml
 * @version 1.0
 * 
 */
public class FengCunTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		ArrayList list=(ArrayList)hm.get("position_set_record");
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map =bo.getValuesMap();
		HashMap ma=bo.getMap();
		String ctrl_peroid=(String)map.get("ctrl_peroid");//年月控制标识=1按年，=0按月,2按季
		ContentDAO dao=null;
		try{
			GrossPayManagement gross = new GrossPayManagement(this.getFrameconn(),"GZ_PARAM");
			ArrayList spflaglist = (ArrayList)ma.get("sp");
			String fc_flag=(String)map.get("fc_flag");
			StringBuffer info=new StringBuffer("");
			for(int i=0;i<list.size();i++){
				RecordVo rmap=(RecordVo)list.get(i);
				String b011=rmap.getString("b0110");
				String setname=rmap.getModelName();
				String sp=rmap.getString(spflaglist.get(0).toString().toLowerCase());
				if(("04".equalsIgnoreCase(sp)) || ("03".equalsIgnoreCase(sp))){
					
				}else{
					info.append(ResourceFactory.getProperty("gz.account.only.yipi"));
				}
				if(info.length()>1){
					throw GeneralExceptionHandler.Handle(new Exception(info.toString()));
				}
			}
			for(int i=0;i<list.size();i++){
				RecordVo rmap=(RecordVo)list.get(i);
				String b011=rmap.getString("b0110");
				String setname=rmap.getModelName();
				String sp=rmap.getString(spflaglist.get(0).toString().toLowerCase());
				StringBuffer sql=new StringBuffer("update ");
				sql.append(setname);
				sql.append(" set ");
				sql.append(fc_flag);
				sql.append("='1'");
				sql.append(" where b0110='");
				sql.append(rmap.getString("b0110"));
				sql.append("' and ");
				if(("04".equalsIgnoreCase(sp)) || ("03".equalsIgnoreCase(sp))){
					if("0".equalsIgnoreCase(ctrl_peroid)){//月
						String aaa=rmap.getString("aaaa");
						String month=aaa.substring(aaa.indexOf(".")+1);
						String year=aaa.substring(0, aaa.indexOf("."));
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(year);
						sql.append(" and ");
						sql.append(Sql_switcher.month(setname+"z0"));
						sql.append("=");
						sql.append(month);
						sql.append(" and ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
					if("1".equalsIgnoreCase(ctrl_peroid)){//年
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(rmap.getString(setname+"z0b"));
						sql.append(" and  ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
					if("2".equalsIgnoreCase(ctrl_peroid)){//季度
						String jidu=rmap.getString(setname+"z0b");
						String month=this.getMonth(jidu);
						String aaa=rmap.getString("aaaa");
						String year=aaa.substring(0,aaa.indexOf("."));
						sql.append(Sql_switcher.year(setname+"z0"));
						sql.append("=");
						sql.append(year);
						sql.append(" and ");
						sql.append(Sql_switcher.month(setname+"z0"));
						sql.append(" in");
						sql.append(month);
						sql.append(" and ");
						sql.append(setname+"z1='");
						sql.append(rmap.getString(setname+"z1"));
						sql.append("'");
					}
				}
				dao=new ContentDAO(this.frameconn);
				dao.update(sql.toString());
			}
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
	}
	public String getMonth(String ss) {
		String month="";
		if("第一季度".equalsIgnoreCase(ss)){
			month="(1,2,3)";
		}else if("第二季度".equalsIgnoreCase(ss)){
			month="(4,5,6)";
		}else if("第三季度".equalsIgnoreCase(ss)){
			month="(7,8,9)";
		}else if("第四季度".equalsIgnoreCase(ss)){
			month="(10,11,12)";
		}
		
		return month;
	}
}
