package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;

/**
 * 
 * 
 *<p>Title:</p> 
 *<p>Description:工资报审</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:May 26, 2009</p> 
 *@author dengcan
 *@version 4.2
 */
public class GzReportTrans extends IBusiness {


	public void execute() throws GeneralException {
		try
		{
			
			String salaryid=(String)this.getFormHM().get("salaryid");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");  //1:驳回 2：报审 3.上报
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			if("2".equals(opt))
				gzbo.gzDataReport();
			else if("1".equals(opt))
			{
				String selectGzRecords=(String)this.getFormHM().get("selectGzRecords");
				selectGzRecords=selectGzRecords.replaceAll("＃", "#").replaceAll("／", "/"); 
				String rejectCause=(String)this.getFormHM().get("rejectCause");
				gzbo.gzDataReportReject(selectGzRecords,rejectCause);
			}
			else if("3".equals(opt)) //人员月奖金分配 上报
			{
			    String theYear = (String)this.getFormHM().get("theyear");
			    String theMonth = (String)this.getFormHM().get("themonth");
			    String operOrg = (String)this.getFormHM().get("operOrg");
			    
			    ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
			    String setid = xml.getNodeAttributeValue("/Params/BONUS_SET", "setid");// 奖金子集
			    String rep_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "rep_field");// 上报标识指标
			    ContentDAO dao = new ContentDAO(this.frameconn);
			    String busiField = setid + "z0";// 业务日期字段
			    String checkUn_field = xml.getNodeAttributeValue("/Params/BONUS_SET", "checkUn_field");// 奖金核算单位标识指标
			    
			    String whlSql =  Sql_switcher.year(busiField) + "=" + theYear + " and " + Sql_switcher.month(busiField) + "=" + theMonth;
			    String sql = "select * from "+ setid +" where "+rep_field+"='1' and b0110='"+operOrg+"' and "+whlSql;
			    this.frowset = dao.search(sql);
			    if(this.frowset.next())
				throw new GeneralException("人员月奖金已经上报！");	
			    
			    
			    HashMap orgMap = new HashMap();//存放叶子机构和结算单位标志为否的单位编码
				StringBuffer buf = new StringBuffer();
			    buf.append("select b0110 from " + setid + " where " + checkUn_field + "='2' and "+whlSql);
				this.frowset = dao.search(buf.toString());
				while (this.frowset.next())				
					orgMap.put(this.frowset.getString(1), "");
				
				buf.setLength(0);
				buf.append("select codeitemid from organization where  codesetid in ('UM','UN') ");
				buf.append(" and (childid in (select codeitemid from organization where codesetid not in ('UM','UN')) ");
				buf.append("or childid is null)");
				this.frowset = dao.search(buf.toString());
				while (this.frowset.next())				
					orgMap.put(this.frowset.getString(1), "");
			    
			    
			    
			    
				StringBuffer updateBuf = new StringBuffer();
			    //如果operOrg为叶子机构，上报所有操作单位中的叶子机构。如果operOrg为中间的核算单位标志为否的机构，只是将这一个机构置为上报状态
				/*
			    String sqlStr = "select * from organization where  codesetid in ('UM','UN') and parentid='" + operOrg + "'";
			    this.frowset = dao.search(sqlStr);
			    if(this.frowset.next())//非叶子机构
			    {			    
				    updateBuf.append("update " + setid + " set "+rep_field+"='1' where b0110='"+operOrg+"' ");					    		    	
			    }else//叶子机构
			    {
			    	String operOrgs = this.userView.getUnit_id();// 操作单位
			    	
			    	updateBuf.append("update " + setid + " set "+rep_field+"='1'  ");
					StringBuffer tempSql = new StringBuffer("");
					String[] temp = operOrgs.split("`");
					for (int i = 0; i < temp.length; i++)
					{
						if(this.isLeafOrg(temp[i].substring(2)))
							tempSql.append(" or  b0110 = '" + temp[i].substring(2) + "'");
					}
					updateBuf.append(" where ( " + tempSql.substring(3) + " ) ");		    	
			    }		*/	    
				//由于人员是按与操作单位相同设置的管理范围内的人员 将人员全部上报后就要将起所属的所有叶子操作单位和核算单位为否的机构置为上报状态
				String operOrgs = this.userView.getUnit_id();// 操作单位
		    	
		    	updateBuf.append("update " + setid + " set "+rep_field+"='1'  ");
				StringBuffer tempSql = new StringBuffer("");
				String[] temp = operOrgs.split("`");
				for (int i = 0; i < temp.length; i++)
				{
					if(orgMap.get(temp[i].substring(2))!=null)
						tempSql.append(" or  b0110 = '" + temp[i].substring(2) + "'");
				}
				updateBuf.append(" where ( " + tempSql.substring(3) + " ) ");	
			    updateBuf.append(" and "+whlSql);				
			    
//			  更新叶子机构的上报状态
			    dao.update(updateBuf.toString());	
			    //更新人员的报审状态
			    gzbo.gzDataReport2();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
	public boolean isLeafOrg(String orgCode) throws GeneralException
	{

		boolean flag = true;
		ContentDAO dao = new ContentDAO(this.frameconn);
		String sqlStr = "select * from organization where  codesetid in ('UM','UN') and parentid='" + orgCode + "'";
		try
		{
			this.frowset = dao.search(sqlStr);
			if (this.frowset.next())
				flag = false;
		} catch (Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return flag;
	}

}
