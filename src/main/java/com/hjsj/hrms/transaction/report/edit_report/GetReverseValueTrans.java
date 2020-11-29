package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.ReverseFindHtmlBo;
import com.hjsj.hrms.businessobject.report.TnameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:反查
 * </p>
 * <p>
 * Company:hjsj
 * </p>
 * <p>
 * create time:Jun 28, 2006:2:11:40 PM
 * </p>
 * 
 * @author dengcan
 * @version 1.0
 * 
 */
public class GetReverseValueTrans extends IBusiness {

	public void execute() throws GeneralException {

		try {

			
			
			HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
			UserView _userview=null;
			String usrID = (String) hm.get("username");
			String userName = (String) hm.get("username");
			usrID = SafeCode.decode(usrID);
			userName = SafeCode.decode(userName);
			hm.remove("username");
			String tabid = (String) hm.get("tabid");
			if(userName==null|| "".equals(userName)){
				userName = this.getUserView().getUserName();
				_userview = this.getUserView();
				if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			}else{
				_userview=new UserView(userName,this.getFrameconn());
				_userview.canLogin();
				userView =_userview;
			}
			if(usrID==null|| "".equals(usrID)){
				usrID = this.getUserView().getUserId();
			}
			String gridName = (String) hm.get("gridName");

			if(StringUtils.equals((String)hm.get("b_query2"),"query")){//wangcq 2014-12-24 进入自动生成模块删除报表分析的数据
				hm.remove("b_query2");   
				hm.remove("dbname");    //移除报表分析里的人员库数据
			}

			int pageNum = Integer.parseInt((String) hm.get("pageNum"));
			int count =0;
			
			if(hm.get("count")!=null&&!"".equals((String)hm.get("count")))
			{
				String temp=PubFunc.round((String) hm.get("count"),0);
				if(temp.length()>10)
					throw new GeneralException(ResourceFactory.getProperty("edit_report.info5")+"!");
				else
				{
					long a_long=Long.parseLong(temp);
					if(a_long>2147483647L||a_long<-2147483648L)
						throw new GeneralException(ResourceFactory.getProperty("edit_report.info5")+"!");
					else
						count=Integer.parseInt(temp);
				}
			}
			
			if(hm.get("unitcode")!=null&&hm.get("unitcode").toString().length()>0){
				LazyDynaBean bean =getUserBean(hm.get("unitcode").toString());
				userName = bean.get("username")==null?userName:bean.get("username").toString();
				String password = bean.get("password")==null?"":bean.get("password").toString();
				
					_userview=new UserView(userName,password,this.getFrameconn());
					_userview.canLogin();
				usrID = _userview.getUserId();
				hm.remove("unitcode");
			}
			
			// 权限控制
			String conditionSql = "";
			TnameBo tnameBo = new TnameBo(this.getFrameconn(), tabid, usrID,
					userName, "view");
			ArrayList tableTermList = tnameBo.getTableTermList();
			if(hm.get("dbname")!=null&&!"".equals((String)hm.get("dbname")))
			{//报表分析中用到得反查，传来的参数，修改指定人员库
				ArrayList list = new ArrayList();
				String temp = (String)hm.get("dbname");
				if(temp.startsWith(","))
					temp=temp.substring(1,temp.length());
				if(temp.endsWith(","))
					temp=temp.substring(0,temp.length()-1);
				String temps [] =temp.split(",");
				for(int i=0;i<temps.length;i++){
					list.add(temps[i]);
				}
				tnameBo.setDbList(list);
				tnameBo.getTgridBo().setDbList(list);   //wangcq 2014-12-20 报表分析，统一人员库
//				hm.remove("dbname");     //报表分析用完后，必须删除，否则影响自动生成里的反查  //wangcq 2014-12-24报表分析反查后继续查看下页出现问题，不能马上删除
			}
			//liuy 2015-2-13 6807：cs扫描库设置为本报表设置，bs自动取数/反查：对1号表取数后反查，反查不对 start
			//update by xiegh on 20180828 bug:30945
			String appdate = (String)hm.get("appdate");
			tnameBo.setAppdate(appdate);
			
			String start = (String)hm.get("start");
            tnameBo.setStartdate(start);
            if(StringUtils.isEmpty(start))
            	start = tnameBo.getStartdate();
            tnameBo.getTgridBo().setStartdate(start);
            
			String dbpreStr = (String)hm.get("dbpreStr");
			
		    if(StringUtils.isNotEmpty(dbpreStr)) {
                ArrayList db = new ArrayList();
                String[]  pre=dbpreStr.split(",");
                if(null!=dbpreStr&&!"".equals(dbpreStr)&&!"undefined".equals(dbpreStr)){//add by xiegh on 20170929 bug:31793
	                for(int i=0;i<pre.length;i++)db.add(pre[i]);
                    tnameBo.setDbList(db);
                }
            }
            //liuy 2015-2-13 end
			ArrayList dbList = tnameBo.getDbList(); // 扫描库
			
			//兼容统计口径
			String scopeid = (String)hm.get("scopeid");
			String units = "";
			if(scopeid!=null&&scopeid.length()>0&&!"0".equals(scopeid)){
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				RecordVo vo = new RecordVo("tscope");
				vo.setInt("scopeid", Integer.parseInt(scopeid));
				try{
					vo = dao.findByPrimaryKey(vo);
					 units = vo.getString("units");
					}catch(Exception e2){
						
					}
			}
			String result = tnameBo.getResult(); // 是否从结果表里取数
			boolean isResult = true;
			if (result != null && "true".equals(result))
				isResult = false;
			HashMap tableTermsMap = new HashMap();
			HashMap factorListMap = new HashMap();    //项目格条件
			HashSet tableTermFactorSet=new HashSet();
			
			// 证明有表条件

			for (int i = 0; i < dbList.size(); i++) {
				String pre = (String) dbList.get(i);
				// 表条件控制
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
//						起始日期 §§ 截止日期
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
					/*	if ((!_userview.isSuper_admin())) {
							strwhere = _userview.getPrivSQLExpression(
									tableTerms[4] + "|" + tableTerms[3], pre,
									false, isResult, new ArrayList());
						} else*/
						{
							String factor = tableTerms[3];
							boolean create_date=false;
							if("6".equals(tableTerms[0]))
						    	create_date=true;
							if(factor.indexOf("create_date")!=-1){
                                factor=factor.replaceAll("create_date", "createtime");    //数据字典中没有create_data字段，换成同样数据类型的createtime     wangcq  2014-11-12
                            }
							FactorList factorlist = new FactorList(
									tableTerms[4], factor, pre, false,
									false, isResult, 1, userName,"t#"+this.userView.getUserName()+"_tjb_A");
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
								ArrayList<FieldItem> fieldList = factorlist.getFieldList();
								tableTermsConditionSql.delete(0, tableTermsConditionSql.length());
								tableTermsConditionSql.append(" union  select A0100 from ")
										.append("(select a0100,");
								for(FieldItem item:fieldList) {
									tableTermsConditionSql.append(item.getItemid().replaceAll(pre+"t#","t#").replaceAll("createtime","create_date")+",");
								}
								tableTermsConditionSql.setLength(tableTermsConditionSql.length()-1);
								tableTermsConditionSql.append(" from hr_emp_hisdata,hr_hisdata_list where hr_emp_hisdata.id=hr_hisdata_list.id) hr_emp_hisdata where " + strwhere);
							}else{
								tableTermsConditionSql.append(" union  select "
										+ pre + "A01.A0100 " + strwhere);
							}
						}
//						tableTermsConditionSql.append(" union  select " + pre
//								+ "A01.A0100 " + strwhere);
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
							tablesql+= /*tablesql.substring(0, tablesql.lastIndexOf(")"))+*/" "+term3+" ";//tablesql= tablesql.substring(0, tablesql.lastIndexOf(")"))+" "+term3+")";
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
						 tableTermsMap.put(pre.toUpperCase(), sql); //bug:31754 反查不对  因为map 的key区分大小写    没有取到项目单元格条件 20170921
					}
					
				}
			}

			if (tableTermFactorSet.size() > 0) {
				//tableTermsMap.clear();
				setBorK_terms(tableTermsMap,tableTermFactorSet,tableTermList);
				
			}
			tnameBo.setFactorListMap(factorListMap);
			
			
			
			int i = Integer.parseInt(gridName.substring(1, gridName
					.indexOf("_")));
			int j = Integer.parseInt(gridName
					.substring(gridName.indexOf("_") + 1));
			ReverseFindHtmlBo reverseFindHtmlBo = new ReverseFindHtmlBo(this.getFrameconn(),this.userView);
			reverseFindHtmlBo.setTabid(tabid);
			String html = reverseFindHtmlBo.getReverseHtml(usrID, userName,
					conditionSql, tableTermsMap, i, j, tnameBo, count, pageNum,_userview);
			String flag="1";
			if("null".equals(html))
			{
				flag = "0";
//				throw new GeneralException("如果该单元格定义了计算公式或定义统计（取值）方法为统计非个数时，则不支持反查！");
			}else if("b".equals(html)){//编号行列
				flag = "2";
			}
			this.getFormHM().put("reverseSql", reverseFindHtmlBo.getSql());
			this.getFormHM().put("setMap_str", reverseFindHtmlBo.getSetMap_str());
			this.getFormHM().put("fieldItem_str",reverseFindHtmlBo.getFieldItem_str());
			this.getFormHM().put("scanMode",reverseFindHtmlBo.getScanMode());
			
			this.getFormHM().put("reverseHtml", html);			
			this.getFormHM().put("flag",flag);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

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
//
//				int flag = 0;
//				if (!isR && isK) {
//					flag = 3;
//				} else if (!isR && isB) {
//					flag = 2;
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

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LazyDynaBean getUserBean(String uid){
		String sql="select username,password from operuser   where unitcode='"+uid+"'";
		ContentDAO dao = new ContentDAO(this.getFrameconn());		
		RowSet rs;
		LazyDynaBean bean = new LazyDynaBean();
		try {
			rs = dao.search(sql);
			if(rs.next()){
				bean.set("username", rs.getString("username"));
				bean.set("password", rs.getString("password")==null?"": rs.getString("password"));
			}	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
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
	
	

}
