/*
 * Created on 2005-12-17
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.posbusiness;

import com.hjsj.hrms.businessobject.train.TrainCourseBo;
import com.hjsj.hrms.utils.sys.LibraryStructureListener;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SaveUpdatePosBusinessTrans extends IBusiness {

	public void execute() throws GeneralException {
		String codeitemid = (String) this.getFormHM().get("codeitemid");
		String codeitemdesc = (String) this.getFormHM().get("codeitemdesc");
		String corcode = (String)this.getFormHM().get("corcode");
		String codesetid = (String) this.getFormHM().get("codesetid");
		String validateflag = (String)this.getFormHM().get("validateflag");
		String start_date = (String)this.getFormHM().get("start_date");
		String end_date = (String)this.getFormHM().get("end_date");
		String fromflag = (String)this.getFormHM().get("fromflag");
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.delete(0, sqlstr.length());
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ArrayList sqlarr = new ArrayList();
			sqlstr.append("select * from codeitem where codesetid=");
			sqlstr.append("?");
			sqlstr.append(" and codeitemdesc=");
			sqlstr.append("?");
			sqlstr.append("");
			sqlarr.add(codesetid);
			sqlarr.add(codeitemdesc);
			this.frowset = dao.search(sqlstr.toString(),sqlarr);
			TrainCourseBo tbo = new TrainCourseBo(userView);
			String busiPriv = "";
			// 培训模块-知识点
			if ("3".equals(fromflag))
			  busiPriv=tbo.getUnitIdByBusi();
			
			if (this.frowset.next()) {
				/*sqlstr.delete(0, sqlstr.length());
				sqlarr.clear();
				sqlstr.append("select * from codeitem where codesetid=");
				sqlstr.append("?");
				sqlstr.append(" and codeitemdesc=");
				sqlstr.append("?");
				sqlstr.append(" and codeitemid=?");
				sqlarr.add(codesetid);
				sqlarr.add(codeitemdesc);
				sqlarr.add(codeitemid);
				this.frecset = dao.search(sqlstr.toString(),sqlarr);
				if(!this.frecset.next()){
					throw GeneralExceptionHandler
					.Handle(new GeneralException(
							"",
							ResourceFactory
									.getProperty("label.posbusiness.adderrorsname"),
							"", ""));
				}else*/{
					if(validateflag!=null&& "1".equals(validateflag)){//修改有时间表示的代码类 xuj2009-10-10
						sqlstr.delete(0, sqlstr.length());
						sqlarr.clear();
						sqlstr.append("update codeitem set codeitemdesc=");
						sqlstr.append("?");
						sqlstr.append(",b0110=?,start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)+",corcode=? where codeitemid=");
						sqlstr.append("?");
						sqlstr.append(" and codesetid=");
						sqlstr.append("?");
						sqlstr.append("");
						sqlarr.add(codeitemdesc);
						sqlarr.add(busiPriv);
						sqlarr.add(corcode);
						sqlarr.add(codeitemid);
						sqlarr.add(codesetid);
					}else{
						sqlstr.delete(0, sqlstr.length());
						sqlarr.clear();
						sqlstr.append("update codeitem set codeitemdesc=");
						sqlstr.append("?");
						sqlstr.append(",b0110=?,corcode=? where codeitemid=");
						sqlstr.append("?");
						sqlstr.append(" and codesetid=");
						sqlstr.append("?");
						sqlstr.append("");
						sqlarr.add(codeitemdesc);
						sqlarr.add(busiPriv);
						sqlarr.add(corcode);
						sqlarr.add(codeitemid);
						sqlarr.add(codesetid);
					}
					dao.update(sqlstr.toString(),sqlarr);
					this.getFormHM().put("isrefresh", "update");
				}
				
			} else {
				if(validateflag!=null&& "1".equals(validateflag)){//修改有时间表示的代码类 xuj2009-10-10
					sqlstr.delete(0, sqlstr.length());
					sqlarr.clear();
					sqlstr.append("update codeitem set codeitemdesc=");
					sqlstr.append("?");
					sqlstr.append(",b0110=?,start_date="+Sql_switcher.dateValue(start_date)+",end_date="+Sql_switcher.dateValue(end_date)+",corcode=? where codeitemid=");
					sqlstr.append("?");
					sqlstr.append(" and codesetid=");
					sqlstr.append("?");
					sqlstr.append("");
					sqlarr.add(codeitemdesc);
					sqlarr.add(busiPriv);
					sqlarr.add(corcode);
					sqlarr.add(codeitemid);
					sqlarr.add(codesetid);
				}else{
					sqlstr.delete(0, sqlstr.length());
					sqlarr.clear();
					sqlstr.append("update codeitem set codeitemdesc=");
					sqlstr.append("?");
					sqlstr.append(",b0110=?,corcode=? where codeitemid=");
					sqlstr.append("?");
					sqlstr.append(" and codesetid=");
					sqlstr.append("?");
					sqlstr.append("");
					sqlarr.add(codeitemdesc);
					sqlarr.add(busiPriv);
					sqlarr.add(corcode);
					sqlarr.add(codeitemid);
					sqlarr.add(codesetid);
				}
				dao.update(sqlstr.toString(),sqlarr);
				this.getFormHM().put("isrefresh", "update");
			}
			 AdminCode.updateCodeItemDesc(codesetid,codeitemid,codeitemdesc);

			/*通知系统代码进行了更新*/
			LibraryStructureListener.updateCode(AdminCode.getCode(codesetid,codeitemid));
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

}
