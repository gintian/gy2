package com.hjsj.hrms.module.workplan.worklog.transaction;

import com.hjsj.hrms.module.workplan.worklog.businessobject.EmployeLogBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.ibm.icu.text.SimpleDateFormat;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchEmployeLogTrans extends IBusiness{

	@Override
	public void execute() throws GeneralException {
			try {
				TableDataConfigCache tablecatch=(TableDataConfigCache)this.userView.getHm().get("employlog_00001");
				EmployeLogBo bo=new EmployeLogBo(this.userView,this.frameconn);
				String currentDate="";
				String date=(String)this.getFormHM().get("date");
				if(date!=null&&date.length()>0){//解决空指针错误 20170209 &-》&&
					SimpleDateFormat sdf1=new SimpleDateFormat("yyyy年mm月");
					SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-mm");
					Date dates=sdf1.parse(date);
					this.userView.getHm().put("date", sdf2.format(dates));
				}
					currentDate=(String)this.userView.getHm().get("date");
				

				tablecatch.setTableSql(bo.getSql(currentDate));
				StringBuffer querySql=new StringBuffer();
				StringBuffer sbf=new StringBuffer();
				ArrayList<String> valuesList=new ArrayList<String>();
				valuesList=(ArrayList<String>)this.getFormHM().get("inputValues");
                // 根据查询条件组成sql片段
                if (valuesList != null && valuesList.size() > 0) {
                    querySql.append(" and ( ");
                }
                for (int i = 0; valuesList != null && i < valuesList.size(); i++) {
                    String queryVal = valuesList.get(i);
                    queryVal = SafeCode.decode(queryVal);
                    //防止sql注入
                    queryVal = PubFunc.hireKeyWord_filter(queryVal);
                    if (i != 0) {
                        querySql.append("or ");
                    }
                    // 名称
                    querySql.append("(a0101 like '%" + queryVal + "%'");
                    // 单位部门岗位
                    List<String> itemids = this.getCodeByLikeDesc(queryVal);
                    if (itemids.size() > 0) {
                        StringBuffer itemBuf = new StringBuffer();
                        for (String itemid : itemids) {
                            itemBuf.append("'" + itemid + "',");
                        }
                        itemBuf.setLength(itemBuf.length() - 1);
                        querySql.append(" or B0110 in (" + itemBuf + ") or e0122 in (" + itemBuf + ")");
                    }
                    // 拼音简码
                    querySql.append(" or pinyin like '%" + queryVal + "%'");

                    querySql.append(")");
                }
                if (valuesList != null && valuesList.size() > 0) {
                    querySql.append(")");
                }
				tablecatch.setQuerySql(querySql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
	}
    /**
     * 通过单位或部门名称模糊查询
     *
     * @param codeDesc
     * @return
     * @throws GeneralException
     */
    private List<String> getCodeByLikeDesc(String codeDesc) throws GeneralException {
        List<String> itemidList = new ArrayList<String>();
        String sql = "select codeitemid from organization where codeitemdesc like '%" + codeDesc + "%' and codesetid in ('UN','UM')";
        RowSet rs = null;
        try {
            ContentDAO dao = new ContentDAO(this.frameconn);
            rs = dao.search(sql);
            while (rs.next()) {
                String codeitemid = rs.getString("codeitemid");
                itemidList.add(codeitemid);
            }
            return itemidList;
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            PubFunc.closeDbObj(rs);
        }
    }
}
