package com.hjsj.hrms.module.gz.salarytype.transaction.moneystyle;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.Arrays;

/**
 * 项目名称 ：ehr7.x
 * 类名称：SaveMoneyStyleDetailTrans
 * 类描述：保存货币详细
 * 创建人： lis
 * 创建时间：2015-12-3
 */
public class SaveMoneyStyleDetailTrans extends IBusiness{

	@Override
    public void execute() throws GeneralException {
		try
		{
			MorphDynaBean formValues=(MorphDynaBean)this.getFormHM().get("formValues");
			String cname=(String)formValues.get("cname");
			double nitemid = 0;
			String nstyleid=(String)formValues.get("nstyleid");
			String beforenitemid=(String)formValues.get("beforenitemid");
			StringBuffer sql = new StringBuffer();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			StringBuffer sb = new StringBuffer();
			if(StringUtils.isBlank(beforenitemid))
			{
				//新增
				nitemid=Double.valueOf((String)formValues.get("nitemid"));
				StringBuffer str = new StringBuffer();
				str.append("select nitemid from moneyitem where nitemid =? and nstyleid =? ");
				RowSet rs = dao.search(str.toString(),Arrays.asList(nitemid,nstyleid));
				if(rs.next()){
					sb.append("面值为【"+rs.getString("nitemid")+"】的已经存在！");
				}
				if(sb.length()>0){
					this.getFormHM().put("sb", sb.toString());
					return;
				}
				sql.append("insert into moneyitem (nstyleid,nitemid,cname,nflag) values (?,?,?,?)");
				dao.insert(sql.toString(),Arrays.asList(nstyleid,nitemid,cname,"1"));
			}
			else
			{
				//修改
				nitemid=Double.valueOf(formValues.get("nitemid").toString());
				StringBuffer str = new StringBuffer();
				str.append("select nitemid from moneyitem where nitemid = ? and nstyleid =? and nitemid<>? ");
				RowSet rs = dao.search(str.toString(),Arrays.asList(nitemid,nstyleid,beforenitemid));
				if(rs.next()){
					sb.append("面值为【"+rs.getString("nitemid")+"】的已经存在！");
				}
				if(sb.length()>0){
					this.getFormHM().put("sb", sb.toString());
					return;
				}
				sql.append("update moneyitem set nitemid=?");
				sql.append(",cname=?");
				sql.append(" where nstyleid=?");
				sql.append(" and nitemid=?");
				dao.update(sql.toString(),Arrays.asList(nitemid,cname,nstyleid,beforenitemid));
			}
			this.getFormHM().put("sb", sb.toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
}
