/**
 * 
 */
package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.lawbase.LawbaseExcel;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 创建汇总excel
 * 
 * @author xujian 2009-9-28
 * 
 */
public class CreateCollectExcel extends IBusiness {

	/**
	 * 
	 */
	public CreateCollectExcel() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */

	public void execute() throws GeneralException {
		ArrayList fieldlist = new ArrayList();
		ArrayList infolist = new ArrayList();
		ArrayList titlelist = new ArrayList();
		ResultSet rs = null;
		String excelfile = "";
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			String salaryid = (String) this.getFormHM().get("salaryid");
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String sum_fields_str = (String) this.getFormHM().get(
					"sum_fields_str");
			String[] sum_fields = sum_fields_str.split(",");
			fieldlist.add("b0110");
			titlelist.add("单位&部门");
			rs = this.getCollectCodeToDesc(salaryid, sum_fields_str, dao);
		/*	for (int i = 0; i < sum_fields.length; i++) {
				fieldlist.add(sum_fields[i]);
				rs.next();
				titlelist.add(rs.getString("itemdesc"));
				
			}*/
			while(rs.next())
			{
				fieldlist.add(rs.getString("itemid"));
				titlelist.add(rs.getString("itemdesc"));
			}
			
			
			fieldlist.add("sp_flag");
			titlelist.add(ResourceFactory
					.getProperty("label.gz.sp"));
			String sql = "select b0110," + sum_fields_str
					+ ",sp_flag from gz_sp_report where salaryid=" + salaryid
					+ " and userid='" + this.userView.getUserId() + "' and b0110 not like 'sum%' order by b0110";
			this.frecset = dao.search(sql);
			while (this.frecset.next()) {
				LazyDynaBean bean = new LazyDynaBean();
				for (int i = 0; i < fieldlist.size(); i++) {
					String fieldname = (String) fieldlist.get(i);
					Object fieldvalue = "";
					if ("b0110".equalsIgnoreCase(fieldname)) {
						fieldvalue = getOrgCodeToDesc(this.frecset
								.getString(fieldname), dao);
					} else if ("sp_flag".equalsIgnoreCase(fieldname)) {
						fieldvalue = getStateCodeToDesc(this.frecset
								.getString(fieldname), dao);
					} else {
						fieldvalue = String.valueOf(this.getFrecset().getBigDecimal(fieldname));
					}
					bean.set(fieldname, fieldvalue);
				}
				infolist.add(bean);
			}
			LawbaseExcel exc = new LawbaseExcel(this.getUserView());
			excelfile = exc.creatExcel1(titlelist,infolist,fieldlist);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			this.formHM.put("excelfile",SafeCode.encode(PubFunc.encrypt(excelfile)));
		}
	}

	private String getOrgCodeToDesc(String codeitemid, ContentDAO dao)
			throws Exception {
		String codeitemdesc = "";
		ResultSet rs = null;
		try {
			String sql = "select codeitemdesc from organization where codeitemid='"
					+ codeitemid + "'";
			rs = dao.search(sql);

			while (rs.next()) {
				codeitemdesc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
		}
		return codeitemdesc;
	}

	private String getStateCodeToDesc(String codeitemid, ContentDAO dao)
			throws Exception {
		String codeitemdesc = "";
		ResultSet rs = null;
		try {
			String sql = "select codeitemdesc from codeitem where codesetid='23' and codeitemid='"
					+ codeitemid + "'";
			rs = dao.search(sql);
			while (rs.next()) {
				codeitemdesc = rs.getString("codeitemdesc");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			rs.close();
		}
		return codeitemdesc;
	}

	private ResultSet getCollectCodeToDesc(String salaryid, String codeitemids,
			ContentDAO dao) throws Exception {
		String codeitemdesc = "";
		ResultSet rs = null;
		try{
		String sql = "select itemdesc,itemid from salaryset where salaryid="
				+ salaryid + " and itemid in ('" + codeitemids.replaceAll(",", "','") + "')";
		rs = dao.search(sql);
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
		}
		return rs;
	}
}
