package com.hjsj.hrms.transaction.report.report_collect;

import com.hjsj.hrms.businessobject.report.reportCollect.ReportCollectBo;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 
 * <p>Title:</p>
 * <p>Description:校验上报信息</p>
 * <p>Company:hjsj</p>
 * <p>create time:Jun 13, 2006:1:13:58 PM</p>
 * @author dengcan
 * @version 1.0
 *
 */
public class ReportCollectValidateTrans extends IBusiness {

	public void execute() throws GeneralException {
		String operate=(String)this.getFormHM().get("operate");
		ReportCollectBo reportCollectBo=new ReportCollectBo(this.getFrameconn());
		if ("1".equals(operate) || "5".equals(operate) || "2".equals(operate)
				|| "6".equals(operate)) // 直属单位汇总 简单条件//复杂条件 基层汇总
		{
			ArrayList unitcodeArray = (ArrayList) this.getFormHM().get(
					"unitcodeArray");

			ArrayList tabArray = (ArrayList) this.getFormHM().get("tabArray");
			//if(reportCollectBo.isPopedom(unitcodeArray,tabArray))
			{				
				ArrayList returnInfo = reportCollectBo.isNotAppeal(unitcodeArray,
						tabArray);
				this.getFormHM().put("returnInfo", returnInfo);
				if ("2".equals(operate) || "6".equals(operate))
					this.getFormHM().put("unitcodeArray", unitcodeArray);
				this.getFormHM().put("isPopedom","1");
			}
		//	else
		//		this.getFormHM().put("isPopedom","0");
			
		}
		else if("3".equals(operate)|| "7".equals(operate))  //所有基层单位汇总|逐层汇总
		{
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			ArrayList unitcodeArray=new ArrayList();
			//取得某单位的所有基层单位
			ArrayList unitcodeList=new ArrayList();
			ArrayList tabArray=(ArrayList)this.getFormHM().get("tabArray");	
			//所有表都应该是一个表类下的
			
			if("3".equals(operate))
			{
				unitcodeList=ttorganization.getGrassRootsUnit((String)this.getFormHM().get("unitcode"));
				RecordVo vo=null;
				for(Iterator t=unitcodeList.iterator();t.hasNext();)
				{
					vo=(RecordVo)t.next();	
					unitcodeArray.add(vo.getString("unitcode")+"§"+vo.getString("unitname"));
				}
			}	
			else if("7".equals(operate))
			{
				String unitcode=(String)this.getFormHM().get("unitcode");
				String tsortid=getTsortid(tabArray);
				unitcodeList=ttorganization.getAllSubUnit1(unitcode, tsortid);
				unitcodeArray=unitcodeList;
			}
			
			//if(reportCollectBo.isPopedom(unitcodeArray,tabArray))
			{	
				ArrayList returnInfo=reportCollectBo.isNotAppeal(unitcodeArray,tabArray);
			//	this.getFormHM().put("unitcodeArray",unitcodeArray);  //当填报单位过多达5000以上时，此处是个瓶颈，所以去掉
				this.getFormHM().put("returnInfo",returnInfo);
				this.getFormHM().put("isPopedom","1");
			}
			//else
			//	this.getFormHM().put("isPopedom","0");
		}
		else if("4".equals(operate)) //直属汇总-基层汇总比较
		{
			ArrayList tabArray=(ArrayList)this.getFormHM().get("tabArray");			
			TTorganization ttorganization=new TTorganization(this.getFrameconn());
			//取得所有基层单位
			ArrayList basic_unitcodeList=ttorganization.getGrassRootsUnit((String)this.getFormHM().get("unitcode"));
			//取得所有直属单位
			ArrayList  unitChildcodeList=ttorganization.getUnderUnitList((String)this.getFormHM().get("unitcode"));
			HashSet set=new HashSet();
			for(Iterator t=basic_unitcodeList.iterator();t.hasNext();)
			{
				RecordVo vo=(RecordVo)t.next();	
				set.add(vo.getString("unitcode")+"§"+vo.getString("unitname"));
			}
			for(Iterator t=unitChildcodeList.iterator();t.hasNext();)
			{
				DynaBean vo=(DynaBean)t.next();	
				set.add(((String)vo.get("unitcode"))+"§"+((String)vo.get("unitname")));
			}
			ArrayList unitList=new ArrayList();
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String temp=(String)t.next();	
				unitList.add(temp);
			}
			//if(reportCollectBo.isPopedom(unitList,tabArray))
			{	
				ArrayList returnInfo=reportCollectBo.isNotAppeal(unitList,tabArray);
				this.getFormHM().put("unitcodeArray",unitList);
				this.getFormHM().put("returnInfo",returnInfo);
				this.getFormHM().put("isPopedom","1");
			}
			//else
			//	this.getFormHM().put("isPopedom","0");
		}
		
		this.getFormHM().put("operate",operate);

	}
	public String getTsortid(ArrayList tablist){
		String tsortid="";
		StringBuffer sql_table=new StringBuffer();
		for(Iterator t=tablist.iterator();t.hasNext();)
		{		
			String temp=SafeCode.decode((String)t.next());
			String[] tt=temp.split("§");
			sql_table.append(" or tabid="+tt[0]);
		}
		StringBuffer sql=new StringBuffer();
		sql.append(" select tsortid from tname where ");
		sql.append(sql_table.substring(3));
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql.toString());
			if(this.frowset.next()){
				tsortid=this.frowset.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tsortid;
		
	}
}
