package com.hjsj.hrms.transaction.report.auto_fill_report;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.businessobject.report.formulaAnalyse.ReportOperationFormulaAnalyse;
import com.hjsj.hrms.businessobject.report.tt_organization.TTorganization;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminDb;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BatchGetDateTrans extends IBusiness {

	public void execute() throws GeneralException {
		
	//	System.out.println("执行时间开始："+ PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss "));
		
		StringBuffer info = new StringBuffer("");
		ContentDAO dao=new ContentDAO(this.frameconn);
		String dbpreStr ="";
		String appdate="";
		String start="";
		try {
			String usrID = this.getUserView().getUserId();
			String userName = "";
			String zxgflag = "";
			try {
					if (isApprove1(this.userView.getUserName())) {
						userName = this.userView.getUserName();
						zxgflag = "0";
						userView = new UserView(userName, this.frameconn);
						userView.canLogin();
					} else {
						userName = approve();// 不是负责人，找是不是有人报表给他
						zxgflag = "4";
						if(userName==null|| "".equals(userName)){
							userName = this.userView.getUserName();
						}
						userView = new UserView(userName, this.frameconn);
						userView.canLogin();
					}


			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayList selectids = (ArrayList) this.getFormHM().get("selectid");
			// 权限控制
			String conditionSql = "";			
			TnameBo tnameBo=null;
			String updateflag = (String)this.getFormHM().get("updateflag");
			String operateObject = (String)this.getFormHM().get("operateObject");
			String home = (String)this.getFormHM().get("home");
			
			
			
			
			for (Iterator t = selectids.iterator(); t.hasNext();) {
				String tabid = (String) t.next();
				
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
				
				tnameBo = new TnameBo(this.getFrameconn(), tabid,
						usrID, userName, "view");
				appdate = (String)this.getFormHM().get("appdate");
				if(StringUtils.isNotEmpty(appdate))
				    tnameBo.setAppdate(appdate);
                start = (String)this.getFormHM().get("startdate");
                if(StringUtils.isNotEmpty(start)){
                    tnameBo.setStartdate(start);
                    tnameBo.getTgridBo().setStartdate(start);
				}
                dbpreStr = (String)this.getFormHM().get("dbprelist");
                if(StringUtils.isNotEmpty(dbpreStr)) {
                    ArrayList db = new ArrayList();
                    String[]  pre=dbpreStr.split(",");
                    for(int i=0;i<pre.length;i++)
                        db.add(pre[i].toUpperCase());
                    tnameBo.setDbList(db);
                }

				ArrayList tableTermList = tnameBo.getTableTermList();
				//兼容统计口径
				String scopeid = (String)this.getFormHM().get("scopeid");
				String units = "";
				if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
					RecordVo vo = new RecordVo("tscope");
					vo.setInt("scopeid", Integer.parseInt(scopeid));
					try{
					vo = dao.findByPrimaryKey(vo);
					 units = vo.getString("units");
					}catch(Exception e2){
						
					}
				}
				ArrayList dbList = tnameBo.getDbList(); // 扫描库
				String result = tnameBo.getResult(); // 是否从结果表里取数
				boolean isResult = true;
				if (result != null && "true".equals(result))
					isResult = false;
				HashMap tableTermsMap = new HashMap();    //项目格条件对应相应的sql
				HashMap factorListMap = new HashMap();    //项目格条件
				HashSet tableTermFactorSet=new HashSet();
				
				for (int i = 0; i < dbList.size(); i++) {
					String pre = (String) dbList.get(i);
					// 表条件控制--人员库
					StringBuffer tableTermsConditionSql = new StringBuffer("");
					for (int a = 0; a < tableTermList.size(); a++) {
						String[] tableTerms = (String[]) tableTermList.get(a);
						if (tableTerms[3].length() > 1) {
							tableTermFactorSet.addAll(tnameBo.getTgridBo().getFactorSet(tableTerms[3]));
							// 调用陈总提供的表达式分析器的到sql语句
							if(tableTerms[3].length()>12&&tableTerms[3].indexOf("$THISUNIT[]")!=-1)
							{
								
								
								if(userView.isSuper_admin())
									tableTerms[3]=tableTerms[3].replaceAll("\\$THISUNIT\\[\\]","*");
								else
								{
									String unit_ids=this.userView.getUnit_id();
									if(unit_ids==null||unit_ids.trim().length()==0|| "UN".equalsIgnoreCase(unit_ids.trim()))
									{
										tableTerms[3]=tableTerms[3].replaceAll("\\$THISUNIT\\[\\]","##");
									}
									else
									{
										String[] temps=unit_ids.split("`");
										StringBuffer un=new StringBuffer("");
										for(int j=0;j<temps.length;j++)
										{
											if(temps[j].trim().length()>0)
											{
												String temp=temps[j];
												String pre2=temp.substring(0,2);
												String value=temp.substring(2);
												if("UN".equalsIgnoreCase(pre2))
												{
													un.append("|"+value+"*");
												}
												else
												{
													un.append("|"+getUnByUm(value,this.getFrameconn())+"*");
												}
											}
											
										}
										if(un.length()>0)
										{
											if(un.length()==1){//条件为本单位且操作单位为全部，这时传*，sql拼成like ‘%’ 形式  zhaoxg add 2013-12-31
												tableTerms[3]=tableTerms[3].replaceAll("\\$THISUNIT\\[\\]","*");
											}else{
												tableTerms[3]=tableTerms[3].replaceAll("\\$THISUNIT\\[\\]",un.substring(1));
											}											
										}
										else
											tableTerms[3]=tableTerms[3].replaceAll("\\$THISUNIT\\[\\]","##");
										
										
									}
						
								}
							}
//							起始日期 §§ 截止日期
							if(tableTerms[3].length()>12&&(tableTerms[3].indexOf("$APPSTARTDATE[]")!=-1||tableTerms[3].indexOf("$APPDATE[]")!=-1))
							{
								Calendar d=Calendar.getInstance();
								String startdate=tnameBo.getStartdate();
								if(startdate==null||startdate.length()==0)
									startdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
								String _startdate=startdate.replaceAll("-","\\.");
								if(tableTerms[3].indexOf("$APPSTARTDATE[]")!=-1)  //起始日期
									tableTerms[3]=tableTerms[3].replaceAll("\\$APPSTARTDATE\\[\\]",_startdate);
							
								String _appdate=tnameBo.getAppdate();
								if(tnameBo.getAppdate()==null)
									_appdate=d.get(Calendar.YEAR)+"-"+(d.get(Calendar.MONTH)+1)+"-"+d.get(Calendar.DATE);
								_appdate=_appdate.replaceAll("-","\\.");	 
								if(tableTerms[3].indexOf("$APPDATE[]")!=-1) //截止日期
									tableTerms[3]=tableTerms[3].replaceAll("\\$APPDATE\\[\\]",_appdate);
							}
							String strwhere = "";
						/*	if ((!userView.isSuper_admin())) {
								userView.setVarTableName(this.userView.getUserName()+"AStatic");
								strwhere = userView.getPrivSQLExpression(
										tableTerms[4] + "|" + tableTerms[3],
										pre, false, isResult, new ArrayList());
								 
							} else*/
							
							{
							    String expr = tableTerms[4];
							    String factor = tableTerms[3];
							    boolean create_date=false;
							    if("6".equals(tableTerms[0]))
							    	create_date=true;
                                if(factor.indexOf("create_date")!=-1){
                                    factor=factor.replaceAll("create_date", "createtime");    //数据字典中没有create_data字段，换成同样数据类型的createtime     wangcq  2014-11-12
                                }
                                boolean bhis =false;//是否取历史 xiegh add 20171025 bug:28496
                                if(null!=tableTerms[5]&&!"".equals(tableTerms[5])&&tableTerms[5].contains("<MODE>1</MODE>"))
                                	bhis = true;
								FactorList factorlist = new FactorList(
										expr, factor, pre,
										bhis, false, isResult, 1, userName,"t#"+this.userView.getUserName()+"_tjb_A");
								factorListMap.put(pre.toUpperCase(), factorlist);
								if(create_date)
								    strwhere = factorlist.getSingleTableSqlExpression("hr_emp_hisdata");
								else
								    strwhere = factorlist.getSqlExpression();
								if(strwhere.indexOf(pre+"t#")!=-1)
									strwhere = strwhere.replaceAll(pre+"t#","t#");
								if(create_date){
								    strwhere = strwhere.replaceAll("createtime","create_date");
								}
								if("6".equals(tableTerms[0])){         //判断扫描库是否为历史时点，是则将快照相关的表加入     wangcq  2014-11-12
									tableTermsConditionSql.delete(0, tableTermsConditionSql.length());
									tableTermsConditionSql.append(" union  select A0100 from " +
											"(select b.create_date,b.description,b.snap_fields,a.*  from hr_emp_hisdata a,hr_hisdata_list b where a.id=b.id) hr_emp_hisdata where " + strwhere);//xiegh add 报错：多次为 'hr_emp_hisdata' 指定了列 'id'
								}else{
									tableTermsConditionSql.append(" union  select "
											+ pre + "A01.A0100 " + strwhere);
								}
							}
					//		strwhere=" FROM "+pre+"A01 LEFT JOIN suAStatic ON suAStatic.a0100="+pre+"A01.a0100 WHERE (yk33>0) and lower(suAStatic.nbase)='"+pre.toLowerCase()+"' ";
//							tableTermsConditionSql.append(" union  select "
//									+ pre + "A01.A0100 " + strwhere);
						}

					}

					if (tableTermsConditionSql.length() > 2) {
						//自动取数条件
						String sql="";
						if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
							String tablesql=tableTermsConditionSql.toString();
							
							String units2 [] = units.split("`");
							String term3 ="and (";
							StringBuffer temp = new StringBuffer();
							for(int j =0;j<units2.length;j++){
								if(units2[j].indexOf("UN")!=-1){
									temp.append(" or "+pre+"A01.B0110 like '"+units2[j].substring(2)+"%' ") ;
								}
								if(units2[j].indexOf("UM")!=-1){
									temp.append(" or "+pre+"A01.E0122 like '"+units2[j].substring(2)+"%' ") ;
								}
							}
							if(temp.length()>3)
								term3+=temp.toString().substring(3)+")";
							if(term3.length()>5)
								tablesql+= /*tablesql.substring(0, tablesql.lastIndexOf(")"))+*/" "+term3+" ";//
							 sql = "select * from ("
									+ tablesql.substring(6)
									+ " ) aaa ";
						}else{
						 sql = "select * from ("
								+ tableTermsConditionSql.substring(6)
								+ " ) aaa ";
						}
						tableTermsMap.put(pre.toUpperCase(), sql);
					}else{
						String sql="";
						if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
							String tablesql=" select "+ pre + "A01.A0100 FROM "+pre+"A01 where 1=1  ";
							
							String units2 [] = units.split("`");
							String term3 ="and (";
							StringBuffer temp = new StringBuffer();
							for(int j =0;j<units2.length;j++){
								if(units2[j].indexOf("UN")!=-1){
									temp.append(" or "+pre+"A01.B0110 like '"+units2[j].substring(2)+"%' ") ;
								}
								if(units2[j].indexOf("UM")!=-1){
									temp.append(" or "+pre+"A01.E0122 like '"+units2[j].substring(2)+"%' ") ;
								}
							}
							if(temp.length()>3)
								term3+=temp.toString().substring(3)+")";
							if(term3.length()>5)
								tablesql+=  term3;
							else{
								tablesql="";
							}
							if(tablesql.length()>0)
							 sql = "select * from ("
									+ tablesql
									+ " ) aaa ";
							 tableTermsMap.put(pre.toUpperCase(), sql);
						}
						
					}
				}
				
				if (tableTermFactorSet.size() > 0) {
//					tableTermsMap.clear();
					setBorK_terms(tableTermsMap,tableTermFactorSet,tableTermList);
					
				}
				tnameBo.setFactorListMap(factorListMap);
				
				// info 0:成功 1:指标没有构库 2.插入数据出错 3.批量取数错误zs 4.没有设置条件
				int a_info = tnameBo.auto_fill_report(usrID, userName,
						conditionSql, tableTermsMap,this.getUserView(),updateflag);
				if (a_info == 0)
				{
					info.append(ResourceFactory.getProperty("edit_report.table")+ tabid + ":" +ResourceFactory.getProperty("auto_fill_report.getDataSuccess")+"! \\n");
					TTorganization tt_organization=new TTorganization(this.getFrameconn());
					RecordVo a_selfVo=tt_organization.getSelfUnit(this.getUserView().getUserName());//可以不用加有效日期 xgq 
					if(a_selfVo!=null){
					String unitcode=a_selfVo.getString("unitcode");
					//String operateObject = (String)this.getFormHM().get("operateObject");
					//String unitcode1 = (String)this.getFormHM().get("unitcode");
					dao.update("update treport_ctrl set status='"+zxgflag+"' where tabid="+tabid+" and unitcode='"+unitcode+"'");
					}
					if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
					dao.update("update tb"+tabid+" set scopeid="+scopeid+" where  username='"+this.getUserView().getUserName()+"'");	
					}
					//home不为空报表取数时会自动调用公式计算（表内表间等）
					if(home!=null&&!"null".equals(home)&&!"".equals(home)){
						ReportOperationFormulaAnalyse reportOperationFormulaAnalyse=null;
						ArrayList formulaList = new ArrayList();
						RowSet rs1 = dao.search("select * from tformula where tabid="+tabid+" order by  expid ");
						while(rs1.next()){
							RecordVo vo=new RecordVo("tformula");
							vo.setInt("expid",rs1.getInt("expid"));	
							vo.setInt("tabid",rs1.getInt("tabid"));
							vo.setString("cname",rs1.getString("cname"));
							vo.setString("lexpr",rs1.getString("lexpr"));
							vo.setString("rexpr",rs1.getString("rexpr"));
							vo.setInt("colrow",rs1.getInt("colrow"));
							formulaList.add(vo);
						}
						if("1".equals(operateObject))
							reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),tabid,formulaList,Integer.parseInt(operateObject),userView.getUserName());
						else
							reportOperationFormulaAnalyse=new ReportOperationFormulaAnalyse(this.getFrameconn(),tabid,formulaList,Integer.parseInt(operateObject),userView.getUnit_id());
						reportOperationFormulaAnalyse.setUserView(this.userView);
						String info0=reportOperationFormulaAnalyse.reportFormulaAnalyse();
					}
					
				}
				else if (a_info == 1)
					info.append(ResourceFactory.getProperty("edit_report.table")+ tabid + ":" + ResourceFactory.getProperty("auto_fill_report.batchFillData.info1")+"! \\n");
				else if (a_info == 2)
					info.append(ResourceFactory.getProperty("edit_report.table")+ tabid + ":" +ResourceFactory.getProperty("auto_fill_report.batchFillData.info2")+ "! \\n");
				else if (a_info == 3)
					info.append(ResourceFactory.getProperty("edit_report.table")+ tabid + ":" + ResourceFactory.getProperty("auto_fill_report.batchFillData.info3")+"! \\n");
				else if (a_info ==4)
					info.append(ResourceFactory.getProperty("edit_report.table")+ tabid + ":" +ResourceFactory.getProperty("auto_fill_report.batchFillData.info4")+ "! \\n");
			}
			
		//	System.out.println("执行时间结束："+ PubFunc.getStringDate("yyyy-MM-dd hh:mm:ss "));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			//liuy 2015-2-13 6807：cs扫描库设置为本报表设置，bs自动取数/反查：对1号表取数后反查，反查不对 start
			if(!"".equals(dbpreStr))
				this.getFormHM().put("dbpreStr", dbpreStr);	
			if(!"".equals(appdate))
				this.getFormHM().put("appdate", appdate);	
			if(!"".equals(start))
				this.getFormHM().put("start", start);
			//liuy 2015-2-13 end
			this.getFormHM().put("info", info.toString());	
					
		}
	}
	
	
	
	
	
	
	//设置表条件---单位 或 职位
	public void setBorK_terms(HashMap tableTermsMap,HashSet tableTermFactorSet,ArrayList tableTermList)
	{
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		TgridBo gridBo=new TgridBo(this.getFrameconn());
		RowSet recset = null;
		try {
			
				StringBuffer itemid_str = new StringBuffer("");
				boolean isB = false;
				boolean isK = false;
				boolean isR = false;
//				for (Iterator t1 = tableTermFactorSet.iterator(); t1.hasNext();) {
//					itemid_str.append(",'" + ((String) t1.next()).trim() + "'");
//				}
//				recset = dao
//						.search("select fieldsetid from fielditem where itemid in ( "
//								+ itemid_str.substring(1) + " )");
//				while (recset.next()) {
//					String tt = recset.getString(1);
//					if (tt.substring(0, 1).equals("A")) {
//						isR = true;
//						break;
//					} else if (tt.substring(0, 1).equals("B"))
//						isB = true;
//					else if (tt.substring(0, 1).equals("K"))
//						isK = true;
//				}
				String[] aa = (String[]) tableTermList.get(0);
				if ("1".equals(aa[0])) {
				isR = true;
				
				} else if ("2".equals(aa[0])|| "3".equals(aa[0])|| "4".equals(aa[0]))
				isB = true;
				else if ("5".equals(aa[0]))
				isK = true;
				
				int flag = 0;
				if (!isR && isK) {
					flag = 3;
				} else if (!isR && isB) {
					flag = 2;
				}
				if (flag != 0) {
					StringBuffer a_tableTermsConditionSql = new StringBuffer("");
					
					boolean isHistory=false;
					for (int a = 0; a < tableTermList.size(); a++) {
						String[] tableTerms = (String[]) tableTermList.get(a);
						if (tableTerms[3].length() > 1&&tableTerms[5].length()>0&& "1".equals(gridBo.getCexpr2Context(2,tableTerms[5]))) {
							isHistory=true;
						}
						else 
						{
							isHistory=false;
						}
					}
					isHistory=false;//暂时不考虑历史数据  zhaoxg 2013-4-27
					if(isHistory)
					{
						if (flag == 3)
							tableTermsMap.put("K_history", tableTermList);
						else if (flag == 2)
							tableTermsMap.put("B_history", tableTermList);
					}
					else
					{ 
						for (int a = 0; a < tableTermList.size(); a++) {
							String[] tableTerms = (String[]) tableTermList.get(a);
							if (tableTerms[3].length() > 1) {
								String strwhere = "";
								FactorList factorlist = new FactorList(
										tableTerms[4], tableTerms[3], "", true,
										false,true, flag, this.getUserView().getUserName(),"t#"+this.userView.getUserName()+"_tjb_B");
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
							else if (flag == 2){
								tableTermsMap.put("Usr", "select a0100 from  (select * from usra01 ) aaa");
								tableTermsMap.put("B", sql);
							}
						}
					}
				}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//根据部门找单位
	public String getUnByUm(String umCode,Connection conn)
	{
		String un="##";
		try
		{
			ContentDAO dao=new ContentDAO(conn);
			RowSet recset=null;
			while(true)
			{
				String sql="select codesetid,codeitemid from organization where codeitemid=(select parentid from organization where codeitemid='"+umCode+"')";
				recset=dao.search(sql);
				if(recset.next())
				{
					if("UN".equalsIgnoreCase(recset.getString("codesetid")))
					{
						un=recset.getString("codeitemid");
						break;
					}
					else
						umCode=recset.getString("codeitemid");
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return un;
	}
	/**
	 * 判断当前用户是否负责报表  zhaoxg 2013-2-17
	 * @param username
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public boolean isApprove1(String username) throws GeneralException, SQLException{
		boolean isapprove = false;
		Connection conn = AdminDb.getConnection();
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		ResultSet rs = null;
		try{

			String sql = "select username from operUser,tt_organization  where operUser.unitcode=tt_organization.unitcode";
			rs = dao.search(sql.toString());
			while(rs.next()){
				if(username.equals(rs.getString("username"))){
					isapprove = true;
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return isapprove;
	}
	/**
	 * 获取报批人信息   zhaoxg 2013-2-17
	 * @param userName
	 * @return
	 * @throws GeneralException 
	 * @throws SQLException 
	 */
	public String approve() throws GeneralException, SQLException{
		String approve = "";

		ResultSet rs = null;
		Connection conn = AdminDb.getConnection();	
		ContentDAO dao = null;
        dao = new ContentDAO(conn);
		try{
			String sql = "select appuser,username from treport_ctrl";
			rs = dao.search(sql.toString());
			while(rs.next()){
				String appuser = rs.getString("appuser");
				if(appuser!=null){
					String[] aa = appuser.split(";");
					for(int i=0;i<aa.length;i++){
						if(aa[i].equals(this.userView.getUserFullName())){
							approve = rs.getString("username");
						}
					}
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return approve;
	}
}
