
package com.hjsj.hrms.transaction.report.report_analyse;


import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.*;

/**
 * @author 
 *
 */
public class ReportOptionTrans extends IBusiness {

	@Override
	public void execute() throws GeneralException {
		
		ArrayList list = new ArrayList();	
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		String isclose =hm.get("isclose")==null?"":(String)hm.get("isclose");
		hm.remove("isclose");
		String checkbase =hm.get("checkbase")==null?"":(String)hm.get("checkbase");
		hm.remove("checkbase");
		if(!"".equals(isclose))
		this.getFormHM().put("isclose" , isclose);
		if(!"".equals(checkbase))
		this.getFormHM().put("checkbase" , checkbase);
		isclose = (String)this.getFormHM().get("isclose");
		checkbase = (String)this.getFormHM().get("checkbase");
		DbNameBo dbvo=new DbNameBo(this.getFrameconn());
		ArrayList dblist=this.userView.getPrivDbList();
		
		dblist=dbvo.getDbNameVoList(dblist);
		
	
			for(int i = 0 ; i< dblist.size(); i++){
				LazyDynaBean abean = new LazyDynaBean();
				if(checkbase!=null&&!"".equals(checkbase)){
					
					RecordVo vo = (RecordVo)dblist.get(i);
					abean.set("pre", vo.getString("pre"));
					abean.set("dbname", vo.getString("dbname"));
					if(checkbase.indexOf(vo.getString("pre"))!=-1)
					abean.set("dbsel", "1");
					else
					abean.set("dbsel", "0");
				}else{
				RecordVo vo = (RecordVo)dblist.get(i);
				abean.set("pre", vo.getString("pre"));
				abean.set("dbname", vo.getString("dbname"));
				 abean.set("dbsel", "0");
				}
				list.add(abean);
			}
			
	//没有设置条件项的不与反查
			// 权限控制
			String conditionSql = "";
			String reportTabid="";
			if(hm.get("reportTabid")!=null&&!"".equals(hm.get("reportTabid"))){
				reportTabid = hm.get("reportTabid").toString();
				
			}else{
				reportTabid=this.getFormHM().get("reportTabid").toString();
			}
			hm.remove("reportTabid");
			TnameBo tnameBo = new TnameBo(this.getFrameconn(), reportTabid, this.getUserView().getUserId(),
					this.getUserView().getUserName(), "view");
			ArrayList tableTermList = tnameBo.getTableTermList();
			HashSet tableTermFactorSet=new HashSet();
			HashMap tableTermsMap = new HashMap();
			ArrayList dbList = tnameBo.getDbList(); // 扫描库
			String result = tnameBo.getResult(); // 是否从结果表里取数
			boolean isResult = true;
			if (result != null && "true".equals(result))
				isResult = false;
			for (int i = 0; i < dbList.size(); i++) {
				String pre = (String) dbList.get(i);
				// 表条件控制
				StringBuffer tableTermsConditionSql = new StringBuffer("");
			for (int a = 0; a < tableTermList.size(); a++) {
				String[] tableTerms = (String[]) tableTermList.get(a);
				//【59431】sql解析器支持截止时间不支持起始时间，在这里处理下
				if(tableTerms[3].length()>12&&(tableTerms[3].indexOf("$APPSTARTDATE[]")!=-1))
				{
					Calendar d= Calendar.getInstance();
					String startdate=tnameBo.getStartdate();
					if(startdate==null||startdate.length()==0)
						startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
					String _startdate=startdate.replaceAll("-","\\.");
					if(tableTerms[3].indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
						tableTerms[3]=tableTerms[3].replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
				}
				if (tableTerms[3].length() > 1) {
					tableTermFactorSet.addAll(tnameBo.getTgridBo().getFactorSet(tableTerms[3]));
					String strwhere = "";
					if ((!userView.isSuper_admin())) {
						strwhere = userView.getPrivSQLExpression(
								tableTerms[4] + "|" + tableTerms[3], pre,
								false, isResult, new ArrayList());
					} else {
						FactorList factorlist = new FactorList(
								tableTerms[4], tableTerms[3], pre, false,
								false, isResult, 1, this.getUserView().getUserName());
						strwhere = factorlist.getSqlExpression();
					}
					tableTermsConditionSql.append(" union  select " + pre
							+ "A01.A0100 " + strwhere);
				}
			}
			if (tableTermsConditionSql.length() > 2) {
				String sql = "select * from ("
						+ tableTermsConditionSql.substring(6) + " ) aaa ";
			
				tableTermsMap.put(pre, sql);
			}
			}
			if (tableTermFactorSet.size() > 0) {
				setBorK_terms(tableTermsMap,tableTermFactorSet,tableTermList);
				
			}
			String gridName = (String) hm.get("gridName");
			int i = Integer.parseInt(gridName.substring(1, gridName
					.indexOf("_")));
			int j = Integer.parseInt(gridName
					.substring(gridName.indexOf("_") + 1));
			ArrayList termList=tnameBo.getReverseValue( this.getUserView().getUserId(),this.getUserView().getUserName(),conditionSql,tableTermsMap,i,j,this.getUserView());
			if(termList.size()>0){
				//throw new GeneralException("如果该单元格定义了计算公式或定义统计（取值）方法为统计非个数时，则不支持反查！");
//				this.getFormHM().put("isclose" , "1");
//				this.getFormHM().put("checkbase" , "");
				this.getFormHM().put("flag", "1");
				this.getFormHM().put("dbnamelist" , list);
			}else
				this.getFormHM().put("flag", "0");
		
	}

	//设置表条件---单位 或 职位
	public void setBorK_terms(HashMap tableTermsMap,HashSet tableTermFactorSet,ArrayList tableTermList)
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		RowSet recset = null;
		try {
			
				StringBuffer itemid_str = new StringBuffer("");
				boolean isB = false;
				boolean isK = false;
				boolean isR = false;
				for (Iterator t1 = tableTermFactorSet.iterator(); t1.hasNext();) {
					itemid_str.append(",'" + ((String) t1.next()).trim() + "'");
				}
				recset = dao
						.search("select fieldsetid from fielditem where itemid in ( "
								+ itemid_str.substring(1) + " )");
				while (recset.next()) {
					String tt = recset.getString(1);
					if ("A".equals(tt.substring(0, 1))) {
						isR = true;
						break;
					} else if ("B".equals(tt.substring(0, 1)))
						isB = true;
					else if ("K".equals(tt.substring(0, 1)))
						isK = true;
				}

				int flag = 0;
				if (!isR && isK) {
					flag = 3;
				} else if (!isR && isB) {
					flag = 2;
				}
				if (flag != 0) {
					StringBuffer a_tableTermsConditionSql = new StringBuffer("");
					for (int a = 0; a < tableTermList.size(); a++) {
						String[] tableTerms = (String[]) tableTermList.get(a);
						if (tableTerms[3].length() > 1) {
							String strwhere = "";
							FactorList factorlist = new FactorList(
									tableTerms[4], tableTerms[3], "", false,
									false,true, flag, this.getUserView().getUserName());
							strwhere = factorlist.getSqlExpression();

							a_tableTermsConditionSql.append(" union  select ");
							if (flag == 3)
								a_tableTermsConditionSql.append("K01.E01A1 "
										+ strwhere);
							else if (flag == 2)
								a_tableTermsConditionSql.append("B01.B0110 "
										+ strwhere);
						}
					}

					if (a_tableTermsConditionSql.length() > 2) {
						String sql = "select * from ("
								+ a_tableTermsConditionSql.substring(6)
								+ " ) aaa ";
						if (flag == 3)
							tableTermsMap.put("K", sql);
						else if (flag == 2)
							tableTermsMap.put("B", sql);
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
