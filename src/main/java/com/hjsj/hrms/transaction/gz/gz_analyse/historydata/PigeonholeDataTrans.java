package com.hjsj.hrms.transaction.gz.gz_analyse.historydata;

import com.hjsj.hrms.businessobject.gz.gz_analyse.HistoryDataBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 3020130025
 * <p>Title:PigeonholeDataTrans.java</p>
 * <p>Description>:PigeonholeDataTrans.java</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Jun 18, 2009 11:39:49 AM</p>
 * <p>@version: 4.0</p>
 * <p>@author: LiZhenWei
 */
public class PigeonholeDataTrans extends IBusiness{

	public void execute() throws GeneralException {
		try
		{
			String ot=(String)this.getFormHM().get("ot");
			String type=(String)this.getFormHM().get("type");
			String startDate=(String)this.getFormHM().get("startDate");
			String endDate=(String)this.getFormHM().get("endDate");
			String salaryid=(String)this.getFormHM().get("id");
			
			String[] salaryids2 = salaryid.split("`");
			//如果用户没有当前薪资类别的资源权限   20141008  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			for (int i = 0; i < salaryids2.length; i++)
			{
			    String _salaryid = salaryids2[i];
			    if(_salaryid!=null&&_salaryid.trim().length()>0)
			    { 
					safeBo.isSalarySetResource(_salaryid,null);
			    }
			}
			
			HistoryDataBo bo = new HistoryDataBo(this.getFrameconn(),salaryid,this.userView);
			
			String b_units=this.userView.getUnitIdByBusiOutofPriv("1");// 1:工资发放  2:工资总额  3:所得税
			if(b_units.length()==0){
				if(!(this.userView.isSuper_admin()|| "1".equals(this.userView.getGroupId())))  //20160229  dengcan
				{
					if("0".equals(ot))
						throw GeneralExceptionHandler.Handle(new Exception("管理员没有给您授权业务范围，您无权归档数据！"));
					else if("1".equals(ot))
						throw GeneralExceptionHandler.Handle(new Exception("管理员没有给您授权业务范围，您无权还原数据！"));
					else if("2".equals(ot))
						throw GeneralExceptionHandler.Handle(new Exception("管理员没有给您授权业务范围，您无权删除归档数据！"));
				}
			}
			
			
			if("0".equals(ot))
			{
	    		bo.syncSalaryarchiveStrut();
	    		bo.syncSalaryTaxArchiveStrut();
	    		bo.pigeonholeHistoryData(type, salaryid, startDate, endDate, userView);
			}
			else if("1".equals(ot))
			{
				ContentDAO dao = new ContentDAO(this.frameconn);
				StringBuffer sql = new StringBuffer("");
				sql.append("  select a.A00Z0,a.A00Z1,a.A00Z2,a.A00Z3,a.A0101 from (");
				sql.append(" select A00Z0,A00Z1,A00Z2,A00Z3,A0101,A0100,NBASE from salaryhistory ");
				sql.append(" where ");
				String where = bo.getWhereSQL(type,startDate, endDate, userView, dao,1);
				sql.append(where);
				sql.append(")a,salaryarchive sa where a.A00Z0=sa.A00Z0 and a.A00Z1=sa.A00Z1 and a.A00Z2=sa.A00Z2  and a.A00Z3=sa.A00Z3 and  a.A0100=sa.A0100 and  a.NBASE=sa.NBASE");
				RowSet rs = dao.search(sql.toString());
				if(rs.next()){
					ExportExcelUtil excelUtil = new ExportExcelUtil(this.frameconn);
					String fileName = "薪资分析还原重复数据_"+this.userView.getUserName() + ".xls";
					ArrayList<LazyDynaBean> headList = new ArrayList<LazyDynaBean>();
					LazyDynaBean bean = new LazyDynaBean();
					bean.set("content", "归属日期");// 列头名称
					bean.set("itemid", "A00Z0");// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "D");// 该列数据类型	
					bean.set("dateFormat", "yyyy-MM");
					headList.add(bean);
					
					bean = new LazyDynaBean();
					bean.set("content", "归属次数");// 列头名称
					bean.set("itemid", "A00Z1");// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "N");// 该列数据类型	
					headList.add(bean);
					
					bean = new LazyDynaBean();
					bean.set("content", "发放日期");// 列头名称
					bean.set("itemid", "A00Z2");// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "D");// 该列数据类型	
					bean.set("dateFormat", "yyyy-MM");
					headList.add(bean);
					
					bean = new LazyDynaBean();
					bean.set("content", "发放次数");// 列头名称
					bean.set("itemid", "A00Z3");// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "N");// 该列数据类型	
					headList.add(bean);
					
					bean = new LazyDynaBean();
					bean.set("content", "姓名");// 列头名称
					bean.set("itemid", "A0101");// 列头代码
					bean.set("codesetid", "0");// 列头代码
					bean.set("decwidth", "0");// 列小数点后面位数
					bean.set("colType", "A");// 该列数据类型	
					headList.add(bean);
					excelUtil.exportExcelBySql(fileName,"",null, headList,sql.toString(), null,0);
					fileName = SafeCode.encode(PubFunc.encrypt(fileName));
					this.getFormHM().put("fileName", fileName);
				}else{
					bo.pigeonholeArchiveData(type, salaryid, startDate, endDate, userView);
				}
			}
			else if("2".equals(ot)) //删除归档数据
			{
				bo.deleteArchiveData(type, salaryid, startDate, endDate, userView);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		
	}

}
