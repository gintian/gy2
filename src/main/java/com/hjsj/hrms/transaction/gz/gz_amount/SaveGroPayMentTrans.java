package com.hjsj.hrms.transaction.gz.gz_amount;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class SaveGroPayMentTrans extends IBusiness {

	public void execute() throws GeneralException {
		HashMap hm=this.getFormHM();
		String fieldsetid = (String)this.getFormHM().get("fieldsetid");
		ArrayList list=(ArrayList)hm.get("position_set_record");
		/**数据集字段列表*/
		ContentDAO dao=null;
		GzAmountXMLBo bo = new GzAmountXMLBo(this.getFrameconn(),2);
		HashMap map = bo.getValuesMap();
		String ctrl_type=(String)map.get("ctrl_type");//是否控制到部门，０控制，１不控制
		String spflagid = ((String)map.get("sp_flag"));
		String ctrl_by_level="0";
		if(map.get("ctrl_by_level")!=null&&!"".equals((String)map.get("ctrl_by_level")))
		{
			ctrl_by_level=(String)map.get("ctrl_by_level");
		}
		String un = "ctrl_item";
		ArrayList checkList = new ArrayList();
		ArrayList dataList = new ArrayList();
		/**计划总额，实发，剩余指标*/
		dataList=(ArrayList) map.get(un.toLowerCase());
		HashMap plan = new HashMap();
		HashMap othMap = new HashMap();
		String fc_flag=(String)map.get("fc_flag");
		for(int j=0;j<dataList.size();j++)
		{
    		LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
			String planitem = (String)bean.get("planitem");
			String realitem = (String)bean.get("realitem");
			String balanceitem = (String)bean.get("balanceitem");
			plan.put(planitem.toLowerCase(),planitem);
			othMap.put(realitem.toLowerCase(),realitem);
			othMap.put(balanceitem.toLowerCase(),balanceitem);
		}
		String ctrl_peroid=(String)map.get("ctrl_peroid");
		try{
	            dao=new ContentDAO(this.getFrameconn());
				if(!(list==null||list.size()==0)){
					/**得到所有变化的计划总额记录*/
					for(int j=0;j<list.size();j++)
					{
						RecordVo vo=(RecordVo)list.get(j);
						/**未保存到数据库中的也要效验*/
						for(int i=0;i<dataList.size();i++)
						{
							LazyDynaBean bean = new LazyDynaBean();
							bean.set("b0110", vo.getString("b0110"));
							bean.set("year",vo.getString("aaaa"));
				    		LazyDynaBean abean = (LazyDynaBean)dataList.get(i);
							String planitem = (String)abean.get("planitem");
							bean.set(planitem.toLowerCase(), vo.getString(planitem.toLowerCase()));
							bean.set("itemid",planitem);
							checkList.add(bean);
						}			
					}
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						String info=getInfo(vo);
						if(info!=null)
							throw new GeneralException(info.toString());
						
						StringBuffer info1=new StringBuffer("");
						int state=vo.getState();
						if(state==-1){
							throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.acount.alert.failure")));
						}
						/*if(vo.getString(spflagid.toLowerCase()).equals("04")){
							info1.append(ResourceFactory.getProperty("gz.acount.only.change"));	
						}
						if(info1.length()>1)
							throw GeneralExceptionHandler.Handle(new Exception(info1.toString()));*/
						if("1".equals(ctrl_by_level))
						{
					    	String info2 ="";
					    	if(fc_flag!=null&&fc_flag.length()!=0){
					    		 info2 =this.upValue(ctrl_peroid, vo, dao, plan, checkList, ctrl_type, fc_flag);
					    	}else{
					    		 info2 =upValue(ctrl_peroid,vo,dao,plan,checkList,ctrl_type);
					    	}
					    	if(!"ok".equals(info2)){
						     	throw GeneralExceptionHandler.Handle(new Exception(info2));
					    	}
                            String info3 ="";
                            if(fc_flag!=null&&fc_flag.length()!=0){
                            	info3 =this.underValue(ctrl_peroid, vo, dao, plan, checkList, ctrl_type, fc_flag);
                            }else{
                            	 info3 =underValue(ctrl_peroid,vo,dao,plan,checkList,ctrl_type);
                            }
                            if(info3!=null&&info3.length()!=0&&!"ok".equalsIgnoreCase(info3)){
							    throw GeneralExceptionHandler.Handle(new Exception(info3));
						    }
						    
						    if(fc_flag!=null&&fc_flag.length()!=0){
						    	String info4=this.checkParRecord(ctrl_peroid, vo);
							    if(info4!=null&&info4.length()!=0&&!"ok".equalsIgnoreCase(info4)){
								    throw GeneralExceptionHandler.Handle(new Exception(info4));
							    }
						    }
						}
					}
					for(int i=0;i<list.size();i++){
						RecordVo vo=(RecordVo)list.get(i);
						vo.removeValue("sp");
						/**如果记录中存在发布状态的，不予修改*/
						if(("04".equals(vo.getString(spflagid.toLowerCase()))) || ("03".equals(vo.getString(spflagid.toLowerCase()))) || ("02".equals(vo.getString(spflagid.toLowerCase()))))
						{
							continue;
						}
						String z0=vo.getString("aaaa");
						String b0110=vo.getString("b0110");
						String year =z0.substring(0,4);
						String month=z0.substring(5);
				       /**不管是按年还是按季度还是按月份的，先把值更新*/
						/**这种方法将实发额的值也改变了，导致数据错乱*/
		     			/*DBMetaModel dbmeta = new DBMetaModel();
		    			TableModel tableModel = dbmeta.searchTable(vo.getModelName());
		    		    String sql = tableModel.getUpdateSql(vo, true);
		    			sql=sql.substring(0,sql.lastIndexOf("where"))+" where "+vo.getModelName()+".b0110='"+b0110+"' and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'";
		  			    GrossManagBo gross = new GrossManagBo();
		    	        ArrayList values = gross.updateValues(vo,tableModel,vo.getModelName(),othMap);
			    	    dao.update(sql,values);*/
						if(fc_flag!=null&&fc_flag.length()!=0){
							this.updateValue(dataList, vo, ctrl_peroid, spflagid, dao, fc_flag);
						}else{
							this.updateValue(dataList, vo, ctrl_peroid, spflagid, dao);
						}
			    	    /**如果是按年或者是按季度的，将计划总额按照年或者季度平均分，*/
				       if("1".equalsIgnoreCase(ctrl_peroid))
				        {
				              for(int j=0;j<dataList.size();j++)
				              {
				                	LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
				          			String planitem = (String)bean.get("planitem");
				          			String realitem=(String)bean.get("realitem");
				          			String balanceitem=(String)bean.get("balanceitem");
				          			int m=0;
				          			
				          			
				          			double value = vo.getDouble(planitem.toLowerCase());
				          			double module = 0;
				          			StringBuffer str1= new StringBuffer();
				          			str1.append(" update "+vo.getModelName()+" set ");
				          			ArrayList lis=new ArrayList();
				          			if(fc_flag!=null&&fc_flag.length()!=0){
				          				 lis=this.getFencun(b0110, vo, ctrl_peroid, fc_flag);
				          				if(lis!=null){
				          					m=lis.size();
				          				}				          				
				          				if(m==0){
				          				
				          					module = (double)(value%12);
						          			value=value-module;				  
						          			str1.append(planitem+"="+((value/12)+module));					          			
						          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1");
						          			dao.update(str1.toString());
						          			str1.setLength(0);
						          			str1.append(" update "+vo.getModelName()+" set ");
						          			str1.append(planitem+"="+(value/12));
						          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in(2,3,4,5,6,7,8,9,10,11,12)");
						          			dao.update(str1.toString());
				          				}else{
				          					module = (double)(value%(m));
				          					value=value-module;	
				          					str1.append(planitem+"="+((value/m)+module));
				          					str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"="+lis.get(0));
				          					str1.append(" and ");
				          	     			str1.append(vo.getModelName()+"z1=");
				          	     			str1.append(vo.getString(vo.getModelName()+"z1"));
				          					dao.update(str1.toString());
				          					str1.setLength(0);
				          					if(lis.size()>1){
							          			str1.append(" update "+vo.getModelName()+" set ");
							          			str1.append(planitem+"="+(value/m));
							          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in(");
							          			for(int k=1;k<lis.size();k++){
							          				str1.append(lis.get(k));
							          				str1.append(",");
							          			}
							          			str1.setLength(str1.length()-1);
							          			str1.append(")");
							          			str1.append(" and ");
					          	     			str1.append(vo.getModelName()+"z1=");
					          	     			str1.append(vo.getString(vo.getModelName()+"z1"));
							          			dao.update(str1.toString());
				          					}
				          				}
				          			}else{
				          				module = (double)(value%12);
					          			value=value-module;				  
					          			str1.append(planitem+"="+((value/12)+module));					          			
					          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1");
					          			dao.update(str1.toString());
					          			str1.setLength(0);
					          			str1.append(" update "+vo.getModelName()+" set ");
					          			str1.append(planitem+"="+(value/12));
					          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in(2,3,4,5,6,7,8,9,10,11,12)");
					          			dao.update(str1.toString());
				          			}				          						          			
				          			str1.setLength(0);
				          			//修改总额，还要伴随着修改余额,按年控制，12个月份的余额都要修改
				          			if(fc_flag!=null&&fc_flag.length()!=0){
				          				for(int n=0;n<lis.size();n++){
				          					str1.append(" update "+vo.getModelName()+" set ");
					          		     	str1.append(balanceitem+"=(");
					          	     		str1.append(" select ("+Sql_switcher.isnull(planitem, "0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+vo.getModelName());
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					          	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+lis.get(n)+"'");					          	     		
					          	     		str1.append(" and ");
					          	     		str1.append(vo.getModelName()+"z1=");
					          	     		str1.append(vo.getString(vo.getModelName()+"z1"));
					          	     		str1.append(")");					          	     		
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					         	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+lis.get(n)+"'");	
					         	     		str1.append(" and ");
					         	     		str1.append(vo.getModelName()+"z1=");
					          	     		str1.append(vo.getString(vo.getModelName()+"z1"));	
					          	    		dao.update(str1.toString());
					          	    		str1.setLength(0);
				          				}
				          			}else{
					          			for(int n=1;n<=12;n++)
					          			{
					          	    		str1.append(" update "+vo.getModelName()+" set ");
					          		     	str1.append(balanceitem+"=(");
					          	     		str1.append(" select ("+Sql_switcher.isnull(planitem, "0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+vo.getModelName());
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					          	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+n+"'");
					          	     		str1.append(")");				          	     		
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					         	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+n+"'");			              			
					          	    		dao.update(str1.toString());
					          	    		str1.setLength(0);
					          			}
				          			}
				              }
				        }
				        else if("2".equalsIgnoreCase(ctrl_peroid))
				        {
				        	 String season = vo.getString("season"); 
				        	 String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
				        	 for(int j=0;j<dataList.size();j++)
				              {
				                	LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
				          			String planitem = (String)bean.get("planitem");
				          			String realitem=(String)bean.get("realitem");
				          			String balanceitem=(String)bean.get("balanceitem");
				          			double value = vo.getDouble(planitem.toLowerCase());
				          			double module =0;
				          			int m=0;
				          			ArrayList lis=new ArrayList();
				          			StringBuffer str1= new StringBuffer();
				          			str1.append(" update "+vo.getModelName()+" set ");
				          			if(fc_flag!=null&&fc_flag.length()!=0){
				          				lis=this.getFencun(b0110, vo, ctrl_peroid, fc_flag);
				          				if(lis!=null){
				          					m=lis.size();
				          				}
				          				if(m==0){
				          					module =(double)(value%3);
					          				value=value-module;	
					          				
					          				str1.append(planitem+"="+((value/3)+module));
					          				str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"="+season);
					          				dao.update(str1.toString());
					          				str1.setLength(0);
						          			str1.append(" update "+vo.getModelName()+" set ");
						          			str1.append(planitem+"="+(value/3));
						          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
						          			dao.update(str1.toString());
				          				}else{
				          					module =(double)(value%(m));
				          					value=value-module;	
				          					str1.append(planitem+"="+((value/(m))+module));
				          					str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"="+lis.get(0)+" and "+vo.getModelName()+"z1="+vo.getString(vo.getModelName()+"z1"));
				          					dao.update(str1.toString());
				          					str1.setLength(0);
				          					if(lis.size()>1){
				          						str1.append(" update "+vo.getModelName()+" set ");
				          						str1.append(planitem+"="+(value/m));
				          						str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in(");
				          						for(int k=1;k<lis.size();k++){
				          							str1.append(lis.get(k));
				          							str1.append(",");
				          						}
				          						str1.setLength(str1.length()-1);
				          						str1.append(") and "+vo.getModelName()+"z1="+vo.getString(vo.getModelName()+"z1"));
				          						dao.update(str1.toString());
				          					}
				          				}
				          			}else{
				          				module =(double)(value%3);
				          				value=value-module;	
				          				
				          				str1.append(planitem+"="+((value/3)+module));
				          				str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"="+season);
				          				dao.update(str1.toString());
				          				str1.setLength(0);
					          			str1.append(" update "+vo.getModelName()+" set ");
					          			str1.append(planitem+"="+(value/3));
					          			str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
					          			dao.update(str1.toString());
				          			}				          						          							          				          							          			
				          			str1.setLength(0);
				          			//Sql_switcher.toInt(arg0)
				          			//修改总额，还要伴随着修改余额,按季度控制，每个季度中的3个月份的余额都要修改
				          			if(fc_flag!=null&&fc_flag.length()!=0){
				          				
				          				for(int n=0;n<lis.size();n++){
				          					str1.append(" update "+vo.getModelName()+" set ");
					          		     	str1.append(balanceitem+"=(");
					          	     		str1.append(" select ("+Sql_switcher.isnull(planitem,"0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+vo.getModelName());
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					          	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+lis.get(n)+"'");					          	     	
					          	     		str1.append(" and ");
					          	     		str1.append(vo.getModelName()+"z1=");
					          	     		str1.append(vo.getString(vo.getModelName()+"z1"));
					          	     		str1.append(")");					          	     		
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					         	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+lis.get(n)+"'");						         	     	
					          	     		str1.append(" and ");
					          	     		str1.append(vo.getModelName()+"z1=");
					          	     		str1.append(vo.getString(vo.getModelName()+"z1"));					          	     								          	     	
					          	    		dao.update(str1.toString());
					          	    		str1.setLength(0);
				          				}
				          			}else{
					          			int x=Integer.parseInt(season);
					          			for(int n=x;n<=x+2;n++)
					          			{
					          	    		str1.append(" update "+vo.getModelName()+" set ");
					          		     	str1.append(balanceitem+"=(");
					          	     		str1.append(" select ("+Sql_switcher.isnull(planitem,"0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+vo.getModelName());
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");					          	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+n+"'");
					          	  			str1.append(")");				          	     		
					          	     		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					         	     		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+n+"'");						         	   
					          	    		dao.update(str1.toString());
					          	    		str1.setLength(0);
					          			}
				          			}
				              }
				        }
				        else{
				        	 for(int j=0;j<dataList.size();j++)
				              {
				                	LazyDynaBean bean = (LazyDynaBean)dataList.get(j);
				          			String planitem = (String)bean.get("planitem");
				          			FieldItem fielditem = DataDictionary.getFieldItem(planitem);
				          			StringBuffer str1= new StringBuffer();
				          			String _value="";
				          			if("N".equalsIgnoreCase(fielditem.getItemtype())){
					          			String value = vo.getString(planitem.toLowerCase());
										if(value.indexOf("E")!=-1)//如果输入的内容太长做的处理  zhaoxg add 2013-11-8
										{
											String temp=value.substring(0,value.indexOf("E"));
											String aa=value.substring(value.indexOf("E")+1);
											value=(new BigDecimal(Math.pow(10,Double.parseDouble(aa.trim()))).multiply(new BigDecimal(temp))).toString();
										} 
										_value=PubFunc.round(value, fielditem.getDecimalwidth());
										if(_value.indexOf(".")!=-1){
											if(_value.split("\\.")[0].length()>fielditem.getItemlength()){
													throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+_value+"整数位超过指定长度！"));
											}
										}else{
											if(_value.length()>fielditem.getItemlength()){
												throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+_value+"超过指定长度！"));
											}
										}
				          			}else{
				          				_value=vo.getString(planitem.toLowerCase());
				          			}

				          			String realitem=(String)bean.get("realitem");
				          			String balanceitem=(String)bean.get("balanceitem");
				          			str1.append(" update "+vo.getModelName()+" set ");
				          			str1.append(planitem+"='"+_value);
				          			str1.append("' where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
				          			if(fc_flag!=null&&fc_flag.length()!=0){
			          	     			str1.append(" and ");
			          	     			str1.append(vo.getModelName()+"z1=");
			          	     			str1.append(vo.getString(vo.getModelName()+"z1"));
			          	     			
			          	     		}else{
			          	     			
			          	     		}
				          			dao.update(str1.toString());
				          			str1.setLength(0);
				                	str1.append(" update "+vo.getModelName()+" set ");
	          		            	str1.append(balanceitem+"=(");
	          	            		str1.append(" select ("+Sql_switcher.isnull(planitem,"0")+"-"+Sql_switcher.isnull(realitem,"0")+") as "+balanceitem+" from "+vo.getModelName());
	          	             		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	          	            		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
	          	            		if(fc_flag!=null&&fc_flag.length()!=0){
			          	     			str1.append(" and ");
			          	     			str1.append(vo.getModelName()+"z1=");
			          	     			str1.append(vo.getString(vo.getModelName()+"z1"));
			          	     			str1.append(")");
			          	     		}else{
			          	     			str1.append(")");
			          	     		}
	          	             		str1.append(" where b0110='"+b0110+"'  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	         	            		str1.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");	
	         	            		if(fc_flag!=null&&fc_flag.length()!=0){
			          	     			str1.append(" and ");
			          	     			str1.append(vo.getModelName()+"z1=");
			          	     			str1.append(vo.getString(vo.getModelName()+"z1"));
			          	     			
			          	     		}else{
			          	     			
			          	     		}
	          	            		dao.update(str1.toString());
				              }
				        }
					}
				}
		}
		catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
			
		}
		
	}

	private String getInfo(RecordVo vo) {
		return null;
	}
	public void updateValue(ArrayList planList,RecordVo vo,String ctrl_peroid,String spflagid,ContentDAO dao,String fc_flag) throws GeneralException
	{
		try
		{
			HashMap m=new HashMap();
			for(int j=0;j<planList.size();j++)
			{
				LazyDynaBean bean = (LazyDynaBean)planList.get(j);
				String planitem = (String)bean.get("planitem");
				String realitem = (String)bean.get("realitem");
				String balanceitem=(String)bean.get("balanceitem");
				m.put(planitem.toLowerCase(), planitem);
				m.put(realitem.toLowerCase(),realitem);
				m.put(balanceitem.toLowerCase(), balanceitem);
			}
			ArrayList alist = DataDictionary.getFieldList(vo.getModelName(),Constant.USED_FIELD_SET);
			ArrayList list = new ArrayList();
			String table = vo.getModelName();
			for(int i=0;i<alist.size();i++){
				FieldItem fielditem = (FieldItem)alist.get(i);
				if(fielditem.getItemid().equalsIgnoreCase(table+"z1")||fielditem.getItemid().equalsIgnoreCase(table+"z0"))
				{
					continue;
				}
				if(fielditem.getItemid().equalsIgnoreCase(spflagid))
				{
					continue;
				}
				if(m.get(fielditem.getItemid().toLowerCase())!=null)
				{
					continue;
				}
				if("b0110".equalsIgnoreCase(fielditem.getItemid()))
				{
					continue;
				}
				if(fc_flag!=null&&fc_flag.length()!=0){
					if(fielditem.getItemid().equalsIgnoreCase(fc_flag)){
						continue;
					}
				}
				list.add(fielditem);
				
	    	}
			if(list==null||list.size()==0)
				return;
			String z0=vo.getString("aaaa");
			String b0110=vo.getString("b0110");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			StringBuffer sql = new StringBuffer();
			sql.append(" update "+vo.getModelName());
			sql.append(" set ");
			
			StringBuffer _sql = new StringBuffer();
			_sql.append(" update "+vo.getModelName());
			_sql.append(" set ");
			
			StringBuffer buf = new StringBuffer();
			ArrayList valueList = new ArrayList();
			ArrayList _valueList = new ArrayList();
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				//PubFunc.round(v, scale)
				for(int i=0;i<list.size();i++)
				{
					FieldItem fielditem = (FieldItem)list.get(i);
					String itemid = fielditem.getItemid();
					sql.append(itemid+"=");
					_sql.append(itemid+"=");
					String type = fielditem.getItemtype();
					if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type))
					{
						sql.append("'"+vo.getString(itemid.toLowerCase())+"'");	
						_sql.append("'"+vo.getString(itemid.toLowerCase())+"'");	
					}
					if("N".equalsIgnoreCase(type))
					{
//						sql.append(PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth()));
						String temp=PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth());
						if(temp.indexOf(".")!=-1){
							
							if(temp.split("\\.")[0].length()>fielditem.getItemlength()){
									throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+vo.getString(itemid.toLowerCase())+"整数位超过指定长度！"));
							}
						}else{
							if(temp.length()>fielditem.getItemlength()){
								throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+vo.getString(itemid.toLowerCase())+"超过指定长度！"));
							}
						}
					    if("1".equalsIgnoreCase(ctrl_peroid))
					    {
					    	LazyDynaBean bean = this.getValue(temp, "12");
					    	String firstValue = PubFunc.round((String) bean.get("firstValue"), fielditem.getDecimalwidth());
					    	String _value = PubFunc.round((String) bean.get("value"), fielditem.getDecimalwidth());
					    	sql.append(firstValue);
					    	_sql.append(_value);
					    }else if("2".equalsIgnoreCase(ctrl_peroid)){
					    	LazyDynaBean bean = this.getValue(temp, "3");
					    	String firstValue = PubFunc.round((String) bean.get("firstValue"), fielditem.getDecimalwidth());
					    	String _value = PubFunc.round((String) bean.get("value"), fielditem.getDecimalwidth());
					    	sql.append(firstValue);
					    	_sql.append(_value);
					    }else{
					    	sql.append(PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth()));
					    }

					}
					if("D".equalsIgnoreCase(type))
					{
						sql.append("to_date('"+vo.getString(itemid.toLowerCase())+"','YYYY-MM-DD HH:MI:SS')");
						_sql.append("to_date('"+vo.getString(itemid.toLowerCase())+"','YYYY-MM-DD HH:MI:SS')");
					}
					if(i!=list.size()-1)
					{
						sql.append(",");
						_sql.append(",");
					}
				}
				sql.append(" where b0110='");
	    		sql.append(vo.getString("b0110")+"'");
	    		if(StringUtils.isNotBlank(fc_flag)){//只保存未封存状态下数据  lis 2015-12-29
	    			sql.append(" and ");
	    			sql.append(vo.getModelName()+"z1=");
	    			sql.append(vo.getString(vo.getModelName()+"z1"));
  	     			
  	     		}
	    		sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    		
				_sql.append(" where b0110='");
	    		_sql.append(vo.getString("b0110")+"'");
	    		if(StringUtils.isNotBlank(fc_flag)){//只保存未封存状态下数据  lis 2015-12-29
	    			_sql.append(" and ");
	    			_sql.append(vo.getModelName()+"z1=");
	    			_sql.append(vo.getString(vo.getModelName()+"z1"));
  	     			
  	     		}
	    		_sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    		
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    		dao.update(sql.toString());	    				    		
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
	    		{
		    		String season = vo.getString("season"); 
	            	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
		    		dao.update(sql.toString());
		    		_sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" = "+season);
		    		dao.update(_sql.toString());
	    		}else if("1".equalsIgnoreCase(ctrl_peroid)){
	    			_sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in (2,3,4,5,6,7,8,9,10,11,12)");
	    			dao.update(_sql.toString());
	    			sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1"); 
	    			dao.update(sql.toString());   			
	    		}    
	    		
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
			
	    		{
		    		 String season = vo.getString("season"); 
	            	 String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		 sql.append("and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    		}
				dao.update(sql.toString());
				
			}
			else
			{
				for(int i=0;i<list.size();i++)
				{
					FieldItem fielditem = (FieldItem)list.get(i);
					String itemid = fielditem.getItemid();
					buf.append(itemid+"=?,");
					String type = fielditem.getItemtype();
					String value=vo.getString(itemid.toLowerCase());
					if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type))
					{
						valueList.add(value==null?"":value);
						_valueList.add(value==null?"":value);
					}
					if("N".equalsIgnoreCase(type))
					{
						value=(value==null|| "".equals(value))?"0":value.toUpperCase();
						if(value.indexOf("E")!=-1)
						{
							String temp=value.substring(0,value.indexOf("E"));
							String aa=value.substring(value.indexOf("E")+1);
							//System.out.println((new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).toString()));
							double dd=Math.pow(10.0,Double.parseDouble(aa.trim()));
							value=(new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).multiply(new BigDecimal(temp))).toString();//         Double.parseDouble(temp)*Math.pow(10,Double.parseDouble(aa));
							//values=tt+"";
						}
					    if("1".equalsIgnoreCase(ctrl_peroid))
					    {
					    	LazyDynaBean bean = this.getValue(value, "12");
					    	String firstValue = (String) bean.get("firstValue");
					    	String _value = (String) bean.get("value");
					    	valueList.add(firstValue==null?"":firstValue);
					    	_valueList.add(_value==null?"":_value);
					    }else if("2".equalsIgnoreCase(ctrl_peroid)){
					    	LazyDynaBean bean = this.getValue(value, "3");
					    	String firstValue = (String) bean.get("firstValue");
					    	String _value = (String) bean.get("value");
					    	valueList.add(firstValue==null?"":firstValue);
					    	_valueList.add(_value==null?"":_value);
					    }else{
					    	valueList.add(value==null?"":value);
					    }
					    
					}
					if("D".equalsIgnoreCase(type))
					{
//						value = Sql_switcher.dateValue(value);
						valueList.add(value==null||" NULL ".equals(value)?"":value);
						_valueList.add(value==null||" NULL ".equals(value)?"":value);
					}
					
				}
    			buf.setLength(buf.length()-1);
	    		sql.append(buf);
	    		sql.append(" where b0110='");
	    		sql.append(vo.getString("b0110")+"'");
	    		if(StringUtils.isNotBlank(fc_flag)){//只保存未封存状态下数据  lis 2015-12-29
	    			sql.append(" and ");
	    			sql.append(vo.getModelName()+"z1=");
	    			sql.append(vo.getString(vo.getModelName()+"z1"));
  	     			
  	     		}
	    		sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    		dao.update(sql.toString(),valueList);		    				    		
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
	    		{
	     			String tempsql = sql.toString();
		    		String season = vo.getString("season"); 
	            	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
		    		dao.update(sql.toString(),_valueList);
		    		tempsql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" = "+season;
		    		dao.update(tempsql.toString(),valueList);
	    		}else if("1".equalsIgnoreCase(ctrl_peroid)){
	    			String tempsql = sql.toString();
	    			sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in (2,3,4,5,6,7,8,9,10,11,12)");
	    			dao.update(sql.toString(),_valueList);
	    			tempsql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1"; 
	    			dao.update(tempsql,valueList);	    			
	    		}    		
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 一个数平分到12个月（季度三个月）上，不能整除 余数加到第一个月上  zhaoxg add 2014-12-25
	 * @param v1
	 * @param v2 年则传12，季度则传3
	 * @return
	 */
	public LazyDynaBean getValue(String v1,String v2){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			BigDecimal bd1=new BigDecimal(v1);
			BigDecimal bd2=new BigDecimal(v2);//年则传12，季度则传3
			String module = bd1.remainder(bd2).toString(); //取余数
			BigDecimal bd3=new BigDecimal(module);
			String value = bd1.subtract(bd3).toString();//减掉余数，取得能整除部分
			BigDecimal bd4=new BigDecimal(value);
			String _value = bd4.divide(bd2).toString();//减去余数后分摊到每个月份上的数
			BigDecimal bd5=new BigDecimal(_value);
			String firstValue = bd3.add(bd5).toString();//余数放到第一月份上面
			bean.set("value", _value);
			bean.set("firstValue", firstValue);
		}catch(Exception e){
			e.printStackTrace();
		}
		return bean;
	}
	/**
	 * 判断下级部门和上级部门的值
	 * @param vo
	 * @param dao
	 * @param map
	 * @param changeList
	 * @return
	 */
	private String upValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type) {
		StringBuffer b0110buf = new StringBuffer("");
		for(int i=0;i<changeList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
			String b0 = (String)bean.get("b0110");
			b0110buf.append("'");
			b0110buf.append(b0);
			b0110buf.append("',");
		}
		if(b0110buf.toString().length()>0)
			b0110buf.setLength(b0110buf.length()-1);
		StringBuffer info = new StringBuffer(); 
		String setid=vo.getModelName()+"z0"; 
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer sqlgroup = new StringBuffer(" group by ");
		StringBuffer wheresql = new StringBuffer(" where b0110 = ");
		wheresql.append("(select parentid from organization where codeitemid = '");
		wheresql.append(vo.getString("b0110"));
		wheresql.append("' )");
		wheresql.append(" and ");
	    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    if("1".equals(ctrl_peroid))
	    {
	    }	
	    else if("2".equals(ctrl_peroid))
	    {
	    	String season = vo.getString("season");
	    	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
	    	 wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    }
	    else
     	    wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			/*if(i==0)
			{
				wheresql.append(" group by ");
			}
			wheresql.append(list.get(i));*/
			/*if(i!=list.size()-1)
			{
				wheresql.append(",");
			}	*/	
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			sqlstr.append(",");
			sqlgroup.append(list.get(i));
			sqlgroup.append(",");
		}
		sqlgroup.setLength(sqlgroup.length()-1);
//		sqlstr.append("(select parentid from organization where codeitemid = '");
//		sqlstr.append(vo.getString("b0110"));
//		sqlstr.append("') as parentid ");
		sqlstr.append(" b0110 ");		
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
//		sqlstr.append(sqlgroup);
		sqlstr.append(" group by b0110"); 
		try {
			/**父级单位或部门的总额*/
			String sqlq="select parentid from organization where codeitemid = '"+vo.getString("b0110")+"'";
			RowSet rs =null;
			rs=dao.search(sqlq);
			String parent="";
			if(rs.next())
				parent=rs.getString("parentid");
			
			rs =dao.search(sqlstr.toString());
			/**同级单位或部门的总和，与上面的总额比较*/
			String strsql = undersql(ctrl_peroid,vo,dao,vo.getString("b0110"),map,ctrl_type);
			StringBuffer b = new StringBuffer();
			b.append(strsql);
			 if(b0110buf.toString().length()>0)
			 {
				  b.append("  and  b0110 not in(");
				  b.append(b0110buf);
				  b.append(")");
			 }
			RowSet rs1 = dao.search(b.toString());
			if(rs.next()&&rs1.next()){
				if(!parent.equals(vo.getString("b0110"))){
					for(int i=0;i<list.size();i++){
						/**父部门的总额如果改变取改变后的，如果未改变，从数据库中取*/
						String dd=(String)list.get(i);
						double sqlValue = rs.getDouble(dd);
						double value=0;
						for(int j=0;j<changeList.size();j++)
						{
							LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
							String b0 = (String)bean.get("b0110");
							String itemid = (String)bean.get("itemid");
							if(parent.equalsIgnoreCase(b0)&&itemid.equalsIgnoreCase(dd))
							{
								String tt=(String)bean.get(itemid.toLowerCase());
								if(tt==null|| "".equals(tt))
									tt="0";
								sqlValue=Double.parseDouble(tt);
							}
							
							if(b0.startsWith(parent)&&!b0.equals(parent)&&b0.length()==vo.getString("b0110").length())
							{
								 String yearmonth =(String)bean.get("year");
								 String ayear =yearmonth.substring(0,4);
								 String amonth=yearmonth.substring(5);
							     if(itemid.equalsIgnoreCase(dd)&&amonth.equals(month))
							     {
							    	
							    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
							     }
							}
						}
						double voValue = rs1.getDouble(dd)+value/*+rs1.getDouble((String)list.get(i))*/;
						if(sqlValue<voValue){
							FieldItem fielditem = DataDictionary.getFieldItem(dd);
							String codeitem = "";
							String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}else{
								desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
								if(desc!=null&&desc.trim().length()>0){
									codeitem = desc;
								}
							}
							//ghgg
							String exception = "";
							if("0".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
							}
							else if("1".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							else
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							info.append(exception+",");
							info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.max.value")+"\n");
						}
					}
				}
			}
			else
			{
			   String desc=AdminCode.getCodeName("UN",vo.getString("b0110"));
			   if(desc==null|| "".equals(desc))
			   {
				   desc = AdminCode.getCodeName("UM", vo.getString("b0110"));
			   }
			   info.append(desc+ResourceFactory.getProperty("gz.acount.noparent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	/**
	 * 上层部门跟下层部门的值对比
	 * @param vo  
	 * @return String 
	 **/
	private String underValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type) {
		StringBuffer info = new StringBuffer();
		ArrayList list = voList(vo,map);
		try {
			//System.out.println(undersql(vo,dao,"").toString());
			StringBuffer b0110buf = new StringBuffer("");
			for(int i=0;i<changeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
				String b0 = (String)bean.get("b0110");
				b0110buf.append("'");
				b0110buf.append(b0);
				b0110buf.append("',");
			}
			if(b0110buf.toString().length()>0)
				b0110buf.setLength(b0110buf.length()-1);
			String underSql = undersql(ctrl_peroid,vo,dao,"",map,ctrl_type);
			StringBuffer s = new StringBuffer();
			s.append(underSql);
			if(b0110buf.toString().length()>0)
			{
				s.append( " and b0110 not in (");
				s.append(b0110buf);
				s.append(")");
			}
			String z0=vo.getString("aaaa");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			RowSet rs = dao.search(s.toString());
			String b0110 = vo.getString("b0110");
			int childlength=this.getChildLength(b0110);
			while(rs.next()){
				for(int i=0;i<list.size();i++){
					double value=0;
					String dd=(String)list.get(i);
					for(int j=0;j<changeList.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
						String b0 = (String)bean.get("b0110");
						String itemid = (String)bean.get("itemid");
						if(b0.startsWith(b0110)&&!b0.equalsIgnoreCase(b0110)&&b0.length()==childlength)
						{
							 String yearmonth =(String)bean.get("year");
							 String ayear =yearmonth.substring(0,4);
							 String amonth=yearmonth.substring(5);
						     if(itemid.equalsIgnoreCase(dd)&&month.equals(amonth))
						     {
						    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
						     }
						}
					}
					double sqlValue = rs.getDouble((String)list.get(i))+value;
					double voValue = vo.getDouble((String)list.get(i));
					
					if(sqlValue>voValue){
						FieldItem fielditem = DataDictionary.getFieldItem((String)list.get(i));
						String codeitem = "";
						String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
						if(desc!=null&&desc.trim().length()>0){
							codeitem = desc;
						}else{
							desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}
						}
						String exception = "";
						if("0".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
						}
						else if("1".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						else
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						info.append(exception+",");
						info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.min.value")+"\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	/**
	 * 获取查询本部门下一级部门的sql语句
	 * @param vo  
	 * @return String 
	 **/
	private String undersql(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type) {
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String season = vo.getString("season");
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	/**
	 * 获取当前表计划总额字段名
	 * @param vo  
	 * @return ArrayList 
	 **/
	private ArrayList voList(RecordVo vo,HashMap map) {
		ArrayList volist = new ArrayList();
		ArrayList list = vo.getModelAttrs();
		String table = vo.getModelName();
		for(int i=0;i<list.size();i++){
			String itemid = (String)list.get(i);
			if(map.get(itemid.toLowerCase())!=null)
			{
	    		FieldItem fielditem = DataDictionary.getFieldItem(itemid);
	    		if(fielditem!=null){
	    			if(fielditem.getFieldsetid().toLowerCase().equalsIgnoreCase(table)){
		    			if("N".equals(fielditem.getItemtype())){
		    				String aa = itemid.substring(itemid.length()-2,itemid.length()).toLowerCase();
		    				if(!"z1".equals(aa)){
		    					volist.add(list.get(i));
	    					}
	    				}
    				}
    			}
			}
			
    	}
		return volist;
	}
	public String getSeasonCondation(int season)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			buf.append(season+","+(season+1)+","+(season+2));     
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	public int getChildLength(String parentid)
	{
		int length=0;
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append(" select codeitemid from organization where parentid='");
			buf.append(parentid+"' and codeitemid<>'"+parentid+"'");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			RowSet rs = null;
			rs = dao.search(buf.toString());
			while(rs.next())
			{
				String itemid = rs.getString("codeitemid");
				length=itemid.length();
				break;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return length;
	}
	public String checkParRecord(String period,RecordVo vo){
		String info="";
		StringBuffer sql=new StringBuffer();
		sql.append("select parentid from organization where codeitemid='");
		sql.append(vo.getString("b0110"));
		sql.append("'");
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql.toString());
			String parent="";
			if(this.frowset.next()){
				parent=this.frowset.getString(1);
				
			}
			String z0=vo.getString("aaaa");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			if(parent!=null&&parent.length()!=0){
				if(parent.equalsIgnoreCase(vo.getString("b0110"))){
					info+="ok";
				}else{
					sql.setLength(0);
					sql.append("select * from ");
					sql.append(vo.getModelName());
					sql.append(" where b0110='");
					sql.append(parent);
					sql.append("' and ");
					sql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
					if("0".equalsIgnoreCase(period)){
						sql.append(" and ");
						sql.append(Sql_switcher.month(vo.getModelName()+"z0"));
						sql.append("=");
						sql.append(month);
						sql.append(" and ");
						sql.append(vo.getModelName());
				    	sql.append("z1=");
				    	sql.append(vo.getString(vo.getModelName()+"z1"));
					}
					if("1".equalsIgnoreCase(period)){
						sql.append(" and ");
						sql.append(vo.getModelName());
				    	sql.append("z1=");
				    	sql.append(vo.getString(vo.getModelName()+"z1"));
					}
					if("2".equalsIgnoreCase(period)){
						sql.append("");
						String season = vo.getString("season");
				    	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
				    	sql.append(" and ");
				    	sql.append(Sql_switcher.month(vo.getModelName()+"z0"));
				    	sql.append(" in(");
				    	sql.append(seasoncondation);
				    	sql.append(")");
				    	sql.append(" and ");
				    	sql.append(vo.getModelName());
				    	sql.append("z1=");
				    	sql.append(vo.getString(vo.getModelName()+"z1"));
					}
					this.frowset=dao.search(sql.toString());
					if(this.frowset.next()){
						info+="ok";
					}else{
						info+="上级单位未包含本次记录不能保存本次记录！";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return info;
	}
	/**
	 * 获取查询本部门下一级部门的sql语句
	 * @param vo  
	 * @return String 
	 **/
	private String undersql(String ctrl_peroid,RecordVo vo,ContentDAO dao,String parentid,HashMap map,String ctrl_type,String fc_flag) {
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer wheresql = new StringBuffer(" where b0110 in ");
		wheresql.append("(select codeitemid from organization where ");
		if(parentid!=null&&parentid.trim().length()>0){
			wheresql.append("parentid = (");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("') and codeitemid<>(");
			wheresql.append("select parentid from organization where codeitemid='");
			wheresql.append(vo.getString("b0110")+"') and codeitemid<>'");
			wheresql.append(vo.getString("b0110")+"'");
		}else{
			wheresql.append("parentid = '");
			wheresql.append(vo.getString("b0110"));
			wheresql.append("' and codeitemid<>'"+vo.getString("b0110")+"'");
		}
		//是否控制到部门，０控制，１不控制
		if("1".equals(ctrl_type))
		{
			wheresql.append(" and UPPER(codesetid)= 'UN'");
		}
		wheresql.append(") ");
		if("0".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		}
		else if("1".equals(ctrl_peroid))
		{
			wheresql.append(" and ");
			wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
		}
		else if("2".equals(ctrl_peroid))
		{
			String season = vo.getString("season");
			String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
			wheresql.append(" and ");
		    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"' and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in("+seasoncondation+")");
		}
		if(fc_flag!=null&&fc_flag.length()!=0){
			wheresql.append(" and ");
			wheresql.append(vo.getModelName()+"z1");
			wheresql.append("=");
			wheresql.append(vo.getString(vo.getModelName()+"z1"));
		}
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			if(i+1<list.size()){
				sqlstr.append(",");
			}
		}
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		
		return sqlstr.toString();
	}
	/**
	 * 上层部门跟下层部门的值对比
	 * @param vo  
	 * @return String 
	 **/
	private String underValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag) {
		StringBuffer info = new StringBuffer();
		ArrayList list = voList(vo,map);
		try {
			//System.out.println(undersql(vo,dao,"").toString());
			StringBuffer b0110buf = new StringBuffer("");
			for(int i=0;i<changeList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
				String b0 = (String)bean.get("b0110");
				b0110buf.append("'");
				b0110buf.append(b0);
				b0110buf.append("',");
			}
			if(b0110buf.toString().length()>0)
				b0110buf.setLength(b0110buf.length()-1);
			String underSql ="";
			if(fc_flag!=null&&fc_flag.length()!=0){
				 underSql = this.undersql(ctrl_peroid, vo, dao, "", map, ctrl_type, fc_flag);
			}else{
				underSql = undersql(ctrl_peroid,vo,dao,"",map,ctrl_type);
			}
			
			StringBuffer s = new StringBuffer();
			s.append(underSql);
			if(b0110buf.toString().length()>0)
			{
				s.append( " and b0110 not in (");
				s.append(b0110buf);
				s.append(")");
			}
			String z0=vo.getString("aaaa");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			RowSet rs = dao.search(s.toString());
			String b0110 = vo.getString("b0110");
			int childlength=this.getChildLength(b0110);
			while(rs.next()){
				for(int i=0;i<list.size();i++){
					double value=0;
					String dd=(String)list.get(i);
					for(int j=0;j<changeList.size();j++)
					{
						LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
						String b0 = (String)bean.get("b0110");
						String itemid = (String)bean.get("itemid");
						if(b0.startsWith(b0110)&&!b0.equalsIgnoreCase(b0110)&&b0.length()==childlength)
						{
							 String yearmonth =(String)bean.get("year");
							 String ayear =yearmonth.substring(0,4);
							 String amonth=yearmonth.substring(5);
						     if(itemid.equalsIgnoreCase(dd)&&month.equals(amonth))
						     {
						    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
						     }
						}
					}
					double sqlValue = rs.getDouble((String)list.get(i))+value;
					double voValue = vo.getDouble((String)list.get(i));
					
					if(sqlValue>voValue){
						FieldItem fielditem = DataDictionary.getFieldItem((String)list.get(i));
						String codeitem = "";
						String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
						if(desc!=null&&desc.trim().length()>0){
							codeitem = desc;
						}else{
							desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}
						}
						String exception = "";
						if("0".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
						}
						else if("1".equals(ctrl_peroid))
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						else
						{
							exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
						}
						info.append(exception+",");
						info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.min.value")+"\n");
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	private String upValue(String ctrl_peroid,RecordVo vo,ContentDAO dao,HashMap map,ArrayList changeList,String ctrl_type,String fc_flag) {
		StringBuffer b0110buf = new StringBuffer("");
		for(int i=0;i<changeList.size();i++)
		{
			LazyDynaBean bean = (LazyDynaBean)changeList.get(i);
			String b0 = (String)bean.get("b0110");
			b0110buf.append("'");
			b0110buf.append(b0);
			b0110buf.append("',");
		}
		if(b0110buf.toString().length()>0)
			b0110buf.setLength(b0110buf.length()-1);
		StringBuffer info = new StringBuffer(); 
		String setid=vo.getModelName()+"z0"; 
		String z0=vo.getString("aaaa");
		String b0110=vo.getString("b0110");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer sqlgroup = new StringBuffer(" group by ");
		StringBuffer wheresql = new StringBuffer(" where b0110 = ");
		wheresql.append("(select parentid from organization where codeitemid = '");
		wheresql.append(vo.getString("b0110"));
		wheresql.append("' )");
		wheresql.append(" and ");
	    wheresql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    if("1".equals(ctrl_peroid))
	    {
	    }	
	    else if("2".equals(ctrl_peroid))
	    {
	    	String season = vo.getString("season");
	    	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
	    	 wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    }
	    else
     	    wheresql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
	    if(fc_flag!=null&&fc_flag.length()!=0){
	    	wheresql.append(" and ");
	    	wheresql.append(vo.getModelName()+"z1");
	    	wheresql.append("=");
	    	wheresql.append(vo.getString(vo.getModelName()+"z1"));
	    }
		ArrayList list = voList(vo,map);
		for(int i=0;i<list.size();i++){
			/*if(i==0)
			{
				wheresql.append(" group by ");
			}
			wheresql.append(list.get(i));*/
			/*if(i!=list.size()-1)
			{
				wheresql.append(",");
			}	*/	
			sqlstr.append(" sum(");
			sqlstr.append(list.get(i));
			sqlstr.append(") as ");
			sqlstr.append(list.get(i));
			sqlstr.append(",");
			sqlgroup.append(list.get(i));
			sqlgroup.append(",");
		}
		sqlgroup.setLength(sqlgroup.length()-1);
	//	sqlstr.append("(select parentid from organization where codeitemid = '");
	//	sqlstr.append(vo.getString("b0110"));
	//	sqlstr.append("') as parentid ");
		sqlstr.append(" b0110 "); // modify by dengcan 薪资总额保存后台报错问题 2014-10-23
				
		sqlstr.append(" from ");
		sqlstr.append(vo.getModelName());
		sqlstr.append(wheresql);
		//sqlstr.append(sqlgroup);//按年保存  下级单位和小于等于上级单位  这样写sql，每月不平均分就出现多条，那么就会比实际值小  比较的就不准确了   zhaoxg  2014-5-5
		sqlstr.append(" group by b0110"); // modify by dengcan 薪资总额保存后台报错问题 2014-10-23
		try {
			/**父级单位或部门的总额*/
			RowSet rs = dao.search(sqlstr.toString());
			/**同级单位或部门的总和，与上面的总额比较*/
			String strsql ="";
			if(fc_flag!=null&&fc_flag.length()!=0){
				strsql =this.undersql(ctrl_peroid, vo, dao, vo.getString("b0110"), map, ctrl_type, fc_flag);
			}else{
				strsql =undersql(ctrl_peroid,vo,dao,vo.getString("b0110"),map,ctrl_type);
			}
			StringBuffer b = new StringBuffer();
			b.append(strsql);
			 if(b0110buf.toString().length()>0)
			 {
				  b.append("  and  b0110 not in(");
				  b.append(b0110buf);
				  b.append(")");
			 }
			RowSet rs1 = dao.search(b.toString());
			if(rs.next()&&rs1.next()){
				if(!rs.getString("b0110").equals(vo.getString("b0110"))){ // modify by dengcan 薪资总额保存后台报错问题 2014-10-23
					for(int i=0;i<list.size();i++){
						/**父部门的总额如果改变取改变后的，如果未改变，从数据库中取*/
						String dd=(String)list.get(i);
						double sqlValue = rs.getDouble(dd);
						double value=0;
						for(int j=0;j<changeList.size();j++)
						{
							LazyDynaBean bean = (LazyDynaBean)changeList.get(j);
							String b0 = (String)bean.get("b0110");
							String itemid = (String)bean.get("itemid");
							if(rs.getString("b0110").equalsIgnoreCase(b0)&&itemid.equalsIgnoreCase(dd))// modify by dengcan 薪资总额保存后台报错问题 2014-10-23
							{
								String tt=(String)bean.get(itemid.toLowerCase());
								if(tt==null|| "".equals(tt))
									tt="0";
								sqlValue=Double.parseDouble(tt);
							}
							
							if(b0.startsWith(rs.getString("b0110"))&&!b0.equals(rs.getString("b0110"))&&b0.length()==vo.getString("b0110").length())// modify by dengcan 薪资总额保存后台报错问题 2014-10-23
							{
								 String yearmonth =(String)bean.get("year");
								 String ayear =yearmonth.substring(0,4);
								 String amonth=yearmonth.substring(5);
							     if(itemid.equalsIgnoreCase(dd)&&amonth.equals(month))
							     {
							    	
							    	 value+=Double.parseDouble(((String)(bean.get(itemid.toLowerCase())==null|| "".equals((String)bean.get(itemid.toLowerCase()))?"0":bean.get(itemid.toLowerCase()))));
							     }
							}
						}
						double voValue = rs1.getDouble(dd)+value/*+rs1.getDouble((String)list.get(i))*/;
						if(sqlValue<voValue){
							FieldItem fielditem = DataDictionary.getFieldItem(dd);
							String codeitem = "";
							String desc = AdminCode.getCodeName("UN",vo.getString("b0110"));
							if(desc!=null&&desc.trim().length()>0){
								codeitem = desc;
							}else{
								desc = AdminCode.getCodeName("UM",vo.getString("b0110"));
								if(desc!=null&&desc.trim().length()>0){
									codeitem = desc;
								}
							}
							//ghgg
							String exception = "";
							if("0".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.yearsmonth.logo")+vo.getString("aaaa");
							}
							else if("1".equals(ctrl_peroid))
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.years.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							else
							{
								exception=codeitem+ResourceFactory.getProperty("gz.acount.for.season.logo")+vo.getString(vo.getModelName()+"z0b");
							}
							info.append(exception+",");
							info.append(fielditem.getItemdesc()+ResourceFactory.getProperty("gz.acount.max.value")+"\n");
						}
					}
				}
			}
			else
			{
			   String desc=AdminCode.getCodeName("UN",vo.getString("b0110"));
			   if(desc==null|| "".equals(desc))
			   {
				   desc = AdminCode.getCodeName("UM", vo.getString("b0110"));
			   }
			   info.append(desc+ResourceFactory.getProperty("gz.acount.noparent"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(info.length()<1){
			info.append("ok");
		}
		
		return info.toString();
	}
	public ArrayList getFencun(String b0110,RecordVo vo,String period,String fc_flag){
		ArrayList n=new ArrayList();
		String z0=vo.getString("aaaa");
		String year =z0.substring(0,4);
		String month=z0.substring(5);
		
		StringBuffer sql=new StringBuffer();
		sql.append("select "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" from ");
		sql.append(vo.getModelName());
		sql.append(" where b0110='");
		sql.append(b0110);
		sql.append("' and ");
		sql.append(Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0"));
		sql.append("="+year);
		if("2".equalsIgnoreCase(period)){
	        String season = vo.getString("season"); 
	        String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
	        sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0") +"in("+seasoncondation+") and ");	
	        sql.append(fc_flag+"=2");
		}
		if("1".equalsIgnoreCase(period)){
			sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in(1,2,3,4,5,6,7,8,9,10,11,12)");
			sql.append(" and "+fc_flag+"=2");
		}
		sql.append(" and " + vo.getModelName()+"z1="+vo.getString(vo.getModelName()+"z1"));
		sql.append(" order by ");
		sql.append(Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0"));
		ContentDAO dao=new ContentDAO(this.frameconn);
		try {
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next()){
				n.add(this.frowset.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return n;
	}
	public void updateValue(ArrayList planList,RecordVo vo,String ctrl_peroid,String spflagid,ContentDAO dao)
	{
		try
		{
			HashMap m=new HashMap();
			for(int j=0;j<planList.size();j++)
			{
				LazyDynaBean bean = (LazyDynaBean)planList.get(j);
				String planitem = (String)bean.get("planitem");
				String realitem = (String)bean.get("realitem");
				String balanceitem=(String)bean.get("balanceitem");
				m.put(planitem.toLowerCase(), planitem);
				m.put(realitem.toLowerCase(),realitem);
				m.put(balanceitem.toLowerCase(), balanceitem);
			}
			ArrayList alist = DataDictionary.getFieldList(vo.getModelName(),Constant.USED_FIELD_SET);
			ArrayList list = new ArrayList();
			String table = vo.getModelName();
			for(int i=0;i<alist.size();i++){
				FieldItem fielditem = (FieldItem)alist.get(i);
				if(fielditem.getItemid().equalsIgnoreCase(table+"z1")||fielditem.getItemid().equalsIgnoreCase(table+"z0"))
				{
					continue;
				}
				if(fielditem.getItemid().equalsIgnoreCase(spflagid))
				{
					continue;
				}
				if(m.get(fielditem.getItemid().toLowerCase())!=null)
				{
					continue;
				}
				if("b0110".equalsIgnoreCase(fielditem.getItemid()))
				{
					continue;
				}
				list.add(fielditem);
				
	    	}
			if(list==null||list.size()==0)
				return;
			String z0=vo.getString("aaaa");
			String b0110=vo.getString("b0110");
			String year =z0.substring(0,4);
			String month=z0.substring(5);
			StringBuffer sql = new StringBuffer();
			sql.append(" update "+vo.getModelName());
			sql.append(" set ");
			
			StringBuffer _sql = new StringBuffer();
			_sql.append(" update "+vo.getModelName());
			_sql.append(" set ");
			
			StringBuffer buf = new StringBuffer();
			ArrayList valueList = new ArrayList();
			ArrayList _valueList = new ArrayList();
			if(Sql_switcher.searchDbServer() == Constant.ORACEL)
			{
				//PubFunc.round(v, scale)
				for(int i=0;i<list.size();i++)
				{
					FieldItem fielditem = (FieldItem)list.get(i);
					String itemid = fielditem.getItemid();
					sql.append(itemid+"=");
					_sql.append(itemid+"=");
					String type = fielditem.getItemtype();
					if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type))
					{
						sql.append("'"+vo.getString(itemid.toLowerCase())+"'");	
						_sql.append("'"+vo.getString(itemid.toLowerCase())+"'");	
					}
					if("N".equalsIgnoreCase(type))
					{
//						sql.append(PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth()));
						String temp=PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth());
						if(temp.indexOf(".")!=-1){
							
							if(temp.split("\\.")[0].length()>fielditem.getItemlength()){
									throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+vo.getString(itemid.toLowerCase())+"整数位超过指定长度！"));
							}
						}else{
							if(temp.length()>fielditem.getItemlength()){
								throw GeneralExceptionHandler.Handle(new Exception(fielditem.getItemdesc()+"列的"+vo.getString(itemid.toLowerCase())+"超过指定长度！"));
							}
						}
					    if("1".equalsIgnoreCase(ctrl_peroid))
					    {
					    	LazyDynaBean bean = this.getValue(temp, "12");
					    	String firstValue = PubFunc.round((String) bean.get("firstValue"), fielditem.getDecimalwidth());
					    	String _value = PubFunc.round((String) bean.get("value"), fielditem.getDecimalwidth());
					    	sql.append(firstValue);
					    	_sql.append(_value);
					    }else if("2".equalsIgnoreCase(ctrl_peroid)){
					    	LazyDynaBean bean = this.getValue(temp, "3");
					    	String firstValue = PubFunc.round((String) bean.get("firstValue"), fielditem.getDecimalwidth());
					    	String _value = PubFunc.round((String) bean.get("value"), fielditem.getDecimalwidth());
					    	sql.append(firstValue);
					    	_sql.append(_value);
					    }else{
					    	sql.append(PubFunc.round(vo.getString(itemid.toLowerCase()), fielditem.getDecimalwidth()));
					    }

					}
					if("D".equalsIgnoreCase(type))
					{
						sql.append("to_date('"+vo.getString(itemid.toLowerCase())+"','YYYY-MM-DD HH:MI:SS')");
						_sql.append("to_date('"+vo.getString(itemid.toLowerCase())+"','YYYY-MM-DD HH:MI:SS')");
					}
					if(i!=list.size()-1)
					{
						sql.append(",");
						_sql.append(",");
					}
				}
				sql.append(" where b0110='");
	    		sql.append(vo.getString("b0110")+"'");
	    		sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    		
				_sql.append(" where b0110='");
	    		_sql.append(vo.getString("b0110")+"'");
	    		_sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	    		
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    		dao.update(sql.toString());	    				    		
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
	    		{
		    		String season = vo.getString("season"); 
	            	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
		    		dao.update(sql.toString());
		    		_sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" = "+season);
		    		dao.update(_sql.toString());
	    		}else if("1".equalsIgnoreCase(ctrl_peroid)){
	    			_sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in (2,3,4,5,6,7,8,9,10,11,12)");
	    			dao.update(_sql.toString());
	    			sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1"); 
	    			dao.update(sql.toString());   			
	    		}    
	    		
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
			
	    		{
		    		 String season = vo.getString("season"); 
	            	 String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		 sql.append("and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+")");
	    		}
				dao.update(sql.toString());
				
			}
			else
			{
				for(int i=0;i<list.size();i++)
				{
					FieldItem fielditem = (FieldItem)list.get(i);
					String itemid = fielditem.getItemid();
					buf.append(itemid+"=?,");
					String type = fielditem.getItemtype();
					String value=vo.getString(itemid.toLowerCase());
					if("A".equalsIgnoreCase(type)|| "M".equalsIgnoreCase(type))
					{
						valueList.add(value==null?"":value);
						_valueList.add(value==null?"":value);
					}
					if("N".equalsIgnoreCase(type))
					{
						value=(value==null|| "".equals(value))?"0":value.toUpperCase();
						if(value.indexOf("E")!=-1)
						{
							String temp=value.substring(0,value.indexOf("E"));
							String aa=value.substring(value.indexOf("E")+1);
							//System.out.println((new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).toString()));
							double dd=Math.pow(10.0,Double.parseDouble(aa.trim()));
							value=(new BigDecimal(Math.pow(10.0,Double.parseDouble(aa.trim()))).multiply(new BigDecimal(temp))).toString();//         Double.parseDouble(temp)*Math.pow(10,Double.parseDouble(aa));
							//values=tt+"";
						}
					    if("1".equalsIgnoreCase(ctrl_peroid))
					    {
					    	LazyDynaBean bean = this.getValue(value, "12");
					    	String firstValue = (String) bean.get("firstValue");
					    	String _value = (String) bean.get("value");
					    	valueList.add(firstValue==null?"":firstValue);
					    	_valueList.add(_value==null?"":_value);
					    }else if("2".equalsIgnoreCase(ctrl_peroid)){
					    	LazyDynaBean bean = this.getValue(value, "3");
					    	String firstValue = (String) bean.get("firstValue");
					    	String _value = (String) bean.get("value");
					    	valueList.add(firstValue==null?"":firstValue);
					    	_valueList.add(_value==null?"":_value);
					    }else{
					    	valueList.add(value==null?"":value);
					    }
					}
					if("D".equalsIgnoreCase(type))
					{
//						value = Sql_switcher.dateValue(value);
						valueList.add(value==null||" NULL ".equals(value)?"":value);
						_valueList.add(value==null||" NULL ".equals(value)?"":value);
					}
					
				}
    			buf.setLength(buf.length()-1);
	    		sql.append(buf);
	    		sql.append(" where b0110='");
	    		sql.append(vo.getString("b0110")+"'");
	    		sql.append("  and "+Sql_switcher.year(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+year+"'");
	     		if("0".equals(ctrl_peroid))
	    		{
		    		sql.append("  and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"='"+month+"'");
		    		dao.update(sql.toString(),valueList);		    				    		
		    	}
	    		if("2".equalsIgnoreCase(ctrl_peroid))
	    		{
	     			String tempsql = sql.toString();
		    		String season = vo.getString("season"); 
	            	String seasoncondation = this.getSeasonCondation(Integer.parseInt(season));
		    		sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in ("+seasoncondation+") and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"<>"+season);
		    		dao.update(sql.toString(),_valueList);
		    		tempsql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" = "+season;
		    		dao.update(tempsql.toString(),valueList);
	    		}else if("1".equalsIgnoreCase(ctrl_peroid)){
	    			String tempsql = sql.toString();
	    			sql.append(" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+" in (2,3,4,5,6,7,8,9,10,11,12)");
	    			dao.update(sql.toString(),_valueList);
	    			tempsql+=" and "+Sql_switcher.month(vo.getModelName()+"."+vo.getModelName()+"z0")+"=1"; 
	    			dao.update(tempsql,valueList);	    			
	    		}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
