package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GrossManagBo;
import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 引入上期工资总额
* 
* 类名称：AddNextGroPayMentTrans   
* 类描述：   
* 创建人：zhaoxg   
* 创建时间：Apr 24, 2014 10:27:49 AM   
* 修改人：zhaoxg   
* 修改时间：Apr 24, 2014 10:27:49 AM   
* 修改备注：   
* @version    
*
 */
public class AddNextGroPayMentTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
				/**=0按管理范围控制=1按操作单位控制=3按模块操作单位控制*/
				String codeitemid=(String) this.getFormHM().get("codeitemid");
				String flag=(String) this.getFormHM().get("flag");
				String viewUnit=(String) this.getFormHM().get("viewUnit");
				String fieldsetid=(String) this.getFormHM().get("fieldsetid");//表名
				String filtervalue=(String) this.getFormHM().get("filtervalue");//具体的月季度
				String yearnum=(String) this.getFormHM().get("yearnum");
				GrossManagBo manbo=new GrossManagBo(this.frameconn,this.userView);
				manbo.setViewUnit(viewUnit);
				ArrayList unlist=manbo.getUN(codeitemid,flag);//获取需要引入的单位
				GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),1);
				HashMap hm = bo.getValuesMap();
				String ctrl_peroid=(String)hm.get("ctrl_peroid");
				String un = "ctrl_item";
				ArrayList dataList = (ArrayList) hm.get(un.toLowerCase());
				String fc_flag=(String)hm.get("fc_flag");
				String spflagid = ((String)hm.get("sp_flag")).length()>0?(String)hm.get("sp_flag"):"";
				StringBuffer str=new StringBuffer();
				if(fc_flag!=null&&fc_flag.length()>0){//只能修改未封存的记录
					str.append(" and "+fc_flag+"='2'");
				}
				if(spflagid!=null&&spflagid.length()>0){//只能修改起草或驳回的记录
					str.append(" and ("+spflagid+"='01' or "+spflagid+"='07')");
				}
				ArrayList list=new ArrayList();
				ArrayList _list=new ArrayList();
				ArrayList balancelist=new ArrayList();
				if(dataList.size()>0){
					for(int i=0;i<dataList.size();i++){
						LazyDynaBean bean=(LazyDynaBean) dataList.get(i);
						if("1".equals(bean.get("flag"))){//有效
							list.add(bean.get("planitem"));//计划
							_list.add(bean.get("realitem"));//实发
							balancelist.add(bean.get("balanceitem"));//剩余
						}
					}
				}
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						if(filtervalue==null||"0".equals(filtervalue)||filtervalue.length()==0|| "1".equals(ctrl_peroid)){//全部或者按年统计的
							for(int j=0;j<unlist.size();j++){
								RowSet rs = null;
								double _value=0;
								String _sql="select sum("+_list.get(i)+") as fc from "+fieldsetid+" where b0110 = '"+unlist.get(j)+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+(Integer.parseInt(yearnum)+"")+"' and "+fc_flag+"='1' ";
								if(fc_flag!=null&&fc_flag.length()>0){
									rs=dao.search(_sql);
									if(rs.next()){
										_value=rs.getDouble("fc");
									}
								}
								
								if(rs!=null){
									rs.close();
								}
								String sql="select sum("+list.get(i)+") as he from "+fieldsetid+" where b0110 = '"+unlist.get(j)+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+(Integer.parseInt(yearnum)-1+"")+"' ";
								rs = dao.search(sql);
								if(rs.next()){
									double lastValue=rs.getDouble("he");
									if(lastValue<=0){
										throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.notHaveLostAccountBalance")));
									}

					      			double value = lastValue-_value;//减掉本期封存的数据（如果有）
					      			double module = 0;
					      			StringBuffer str1= new StringBuffer();
					      			str1.append(" update "+fieldsetid+" set ");
					  				module = (double)(value%12);
					      			value=value-module;				  
					      			str1.append(list.get(i)+"="+((value/12)+module));					          			
					      			str1.append(" where    "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"=12 and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			
					      			str1.setLength(0);
					      			str1.append(" update "+fieldsetid+" set ");
					      			str1.append(list.get(i)+"="+(value/12));
					      			str1.append(" where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" in (1,2,3,4,5,6,7,8,9,10,11) and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			for(int t=1;t<13;t++){//修改剩余额
						      			str1.setLength(0);
						      			str1.append(" update "+fieldsetid+" set ");			  
						      			str1.append(balancelist.get(i)+"= (select ("+Sql_switcher.isnull((String) list.get(i), "0")+"-"+Sql_switcher.isnull((String) _list.get(i), "0")+") as "+balancelist.get(i)+" from "+fieldsetid);					          			
						      			str1.append(" where  "+Sql_switcher.month(fieldsetid+"z0")+"="+t+" and "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"'  and b0110 = '"+unlist.get(j)+"' "+str+"");
						      			str1.append(") where  "+Sql_switcher.month(fieldsetid+"z0")+"="+t+" and "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"'  and b0110 = '"+unlist.get(j)+"' "+str+"");
						      			dao.update(str1.toString());
					      			}
								}
							}
						}else if(filtervalue.length()>0&& "2".equals(ctrl_peroid)){//季度
							for(int j=0;j<unlist.size();j++){
								HashMap map=getJidu(filtervalue);
								HashMap _map=getNextJidu(filtervalue,yearnum);
								RowSet rs = null;
								double _value=0;
								String _sql="select sum("+_list.get(i)+") as fc from "+fieldsetid+" where b0110 = '"+unlist.get(j)+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+(Integer.parseInt(yearnum)+"")+"' and "+Sql_switcher.month(fieldsetid+"z0")+" in ("+map.get("flag")+","+map.get("yue")+") and "+fc_flag+"='1' ";
								if(fc_flag!=null&&fc_flag.length()>0){
									rs=dao.search(_sql);
									if(rs.next()){
										_value=rs.getDouble("fc");
									}
								}
								if(rs!=null){
									rs.close();
								}
								String sql="select sum("+list.get(i)+") as he from "+fieldsetid+" where b0110 = '"+unlist.get(j)+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+_map.get("year")+"' and "+Sql_switcher.month(fieldsetid+"z0")+" in ("+_map.get("yue")+") ";
								rs = dao.search(sql);
								if(rs.next()){
					      			double value = rs.getDouble("he")-_value;//减掉本期封存的数据（如果有）
					      			double module = 0;
					      			StringBuffer str1= new StringBuffer();
					      			str1.append(" update "+fieldsetid+" set ");
					  				module = (double)(value%3);
					      			value=value-module;				  
					      			str1.append(list.get(i)+"="+((value/3)+module));					          			
					      			str1.append(" where    "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"="+map.get("flag")+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			str1.setLength(0);
					      			str1.append(" update "+fieldsetid+" set ");
					      			str1.append(list.get(i)+"="+(value/3));
					      			str1.append(" where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" in ("+map.get("yue")+") and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			
					      			str1.setLength(0);
					      			str1.append(" update "+fieldsetid+" set ");			  
					      			str1.append(balancelist.get(i)+"= (select ("+Sql_switcher.isnull((String) list.get(i), "0")+"-"+Sql_switcher.isnull((String) _list.get(i), "0")+") as "+balancelist.get(i)+" from "+fieldsetid);					          			
					      			str1.append(" where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" = "+map.get("flag")+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			str1.append(") where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" = "+map.get("flag")+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			String[] temp=(String[]) map.get("yue").toString().split(",");
					      			for(int t=0;t<temp.length;t++){//修改剩余额
						      			str1.setLength(0);
						      			str1.append(" update "+fieldsetid+" set ");			  
						      			str1.append(balancelist.get(i)+"= (select ("+Sql_switcher.isnull((String) list.get(i), "0")+"-"+Sql_switcher.isnull((String) _list.get(i), "0")+") as "+balancelist.get(i)+" from "+fieldsetid);					          			
						      			str1.append(" where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" = "+temp[t]+" and b0110 = '"+unlist.get(j)+"' "+str+"");
						      			str1.append(") where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+" = "+temp[t]+" and b0110 = '"+unlist.get(j)+"' "+str+"");
						      			dao.update(str1.toString());
					      			}
								}
							}
						}else{
							for(int j=0;j<unlist.size();j++){
								HashMap _map=getNextyue(filtervalue,yearnum);
								RowSet rs = null;
								double _value=0;
								String _sql="select sum("+_list.get(i)+") as fc from "+fieldsetid+" where  "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"="+filtervalue+" and b0110 = '"+unlist.get(j)+"' and "+fc_flag+"='1' ";
								if(fc_flag!=null&&fc_flag.length()>0){
									rs=dao.search(_sql);
									if(rs.next()){
										_value=rs.getDouble("fc");
									}
								}
								if(rs!=null){
									rs.close();
								}
								String sql="select sum("+list.get(i)+") as he from "+fieldsetid+" where b0110 = '"+unlist.get(j)+"' and "+Sql_switcher.year(fieldsetid+"z0")+"='"+_map.get("year")+"' and "+Sql_switcher.month(fieldsetid+"z0")+" in ("+_map.get("yue")+") ";
								rs = dao.search(sql);
								if(rs.next()){
					      			double value = rs.getDouble("he")-_value;//减掉本期封存的数据（如果有）
					      			StringBuffer str1= new StringBuffer();
					      			str1.append(" update "+fieldsetid+" set ");		  
					      			str1.append(list.get(i)+"="+value);					          			
					      			str1.append(" where    "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"="+filtervalue+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
					      			
					      			str1.setLength(0);//修改剩余额
					      			str1.append(" update "+fieldsetid+" set ");			  
					      			str1.append(balancelist.get(i)+"= (select ("+Sql_switcher.isnull((String) list.get(i), "0")+"-"+Sql_switcher.isnull((String) _list.get(i), "0")+") as "+balancelist.get(i)+" from "+fieldsetid);	
					      			str1.append(" where    "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"="+filtervalue+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			str1.append(") where    "+Sql_switcher.year(fieldsetid+"z0")+"='"+yearnum+"' and "+Sql_switcher.month(fieldsetid+"z0")+"="+filtervalue+" and b0110 = '"+unlist.get(j)+"' "+str+"");
					      			dao.update(str1.toString());
								}
							}
						}
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);	
		}
	}
	/**
	 * 获取季度包含的月份
	 * @param i 季度
	 * @return
	 */
	public HashMap getJidu(String i){
		HashMap map=new HashMap();
		try {
			switch(Integer.parseInt(i)){
				case 1:  
					map.put("yue", "'1','2'");
					map.put("flag", "'3'");
					break;
				case 2:
					map.put("yue", "'4','5'");
					map.put("flag", "'6'");
					break;
				case 3:
					map.put("yue", "'7','8'");
					map.put("flag", "'9'");
					break;
				case 4:
					map.put("yue", "'10','11'");
					map.put("flag", "'12'");
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取上一个季度
	 * @param i  季度
	 * @param year  年
	 * @return
	 */
	public HashMap getNextJidu(String i,String year){
		HashMap map=new HashMap();
		try {
			switch(Integer.parseInt(i)){
				case 1:  
					map.put("yue", "10,11,12");
					map.put("year", (Integer.parseInt(year)-1+""));
					break;
				case 2:
					map.put("yue", "1,2,3");
					map.put("year", year);
					break;
				case 3:
					map.put("yue", "4,5,6");
					map.put("year", year);
					break;
				case 4:
					map.put("yue", "7,8,9");
					map.put("year", year);
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 获取上个月
	 * @param i 月
	 * @param year 年
	 * @return
	 */
	public HashMap getNextyue(String i,String year){
		HashMap map=new HashMap();
		try {	
			switch(Integer.parseInt(i)){
			case 1:  
				map.put("yue", "12");
				map.put("year", (Integer.parseInt(year)-1+""));
				break;
			case 2:
				map.put("yue", "1");
				map.put("year", year);
				break;
			case 3:
				map.put("yue", "2");
				map.put("year", year);
				break;
			case 4:
				map.put("yue", "3");
				map.put("year", year);
				break;
			case 5:
				map.put("yue", "4");
				map.put("year", year);
				break;
			case 6:
				map.put("yue", "5");
				map.put("year", year);
				break;
			case 7:
				map.put("yue", "6");
				map.put("year", year);
				break;
			case 8:
				map.put("yue", "7");
				map.put("year", year);
				break;
			case 9:
				map.put("yue", "8");
				map.put("year", year);
				break;
			case 10:
				map.put("yue", "9");
				map.put("year", year);
				break;
			case 11:
				map.put("yue", "10");
				map.put("year", year);
				break;
			case 12:
				map.put("yue", "11");
				map.put("year", year);
				break;
		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
