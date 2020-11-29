package com.hjsj.hrms.transaction.gz.gz_self.tax;

import com.hjsj.hrms.businessobject.gz.gz_self.tax.ParaseGz_tax_mode;
import com.hjsj.hrms.businessobject.gz.gz_self.tax.SelTaxSel;
import com.hjsj.hrms.businessobject.gz.gz_self.tax.SelfTaxSQL;
import com.hjsj.hrms.businessobject.performance.workdiary.WeekUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.jdom.JDOMException;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelfTaxShowTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ContentDAO dao=new ContentDAO(this.frameconn);
		UserView uv=this.getUserView();
//		DataDictionary.refresh();
		HashMap hm=this.getFormHM();
		HashMap reqhm=(HashMap) hm.get("requestPamaHM");
		String flag = (String)this.getFormHM().get("flag");
		if("noself".equals(flag)){
			String userbase=(String)reqhm.get("userbase");
			String a0100=(String)reqhm.get("a0100");
			uv=getUserViewObj(userbase,a0100);
		}
		ParaseGz_tax_mode ptm=null;
		String modeset="";
		String sumynse="0.00";
		String sumsds="0.00";
		if(this.userView.getA0100()==null||userView.getA0100().trim().length()<1)
			throw new GeneralException(ResourceFactory.getProperty("selfservice.module.pri"));
		try {
			ptm=new ParaseGz_tax_mode(dao);
			modeset=ptm.getGz_tax_mode();
			hm.put("modeset",modeset);
//			System.out.println(ptm);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String str_tax_date="";
		String str_nian="";
		SelTaxSel sts=new SelTaxSel();
		SelfTaxSQL stsql=new SelfTaxSQL();
		String startime="";
		String endtime="";
		String[] sql=new String[4];
		if(reqhm.containsKey("action")){
			if(reqhm.containsKey("timefield")){
				if(reqhm.containsKey("startime")){
//				时间段查询
					startime=(String) reqhm.get("startime");
					endtime=(String) reqhm.get("endtime");
					reqhm.remove("startime");
					reqhm.remove("endtime");
					str_tax_date=sts.getTax_DateSelStr(dao,"",uv);
					str_nian=sts.getYearSelStr(dao,"",uv);
					hm.put("ymd","0");
					hm.put("startime",startime);
					hm.put("endtime",endtime);
				}else{
//					按年查询
					String nian=(String) hm.get("nian");
					if("0".equals(nian)){
						Date dd=new Date(System.currentTimeMillis());
						StringBuffer ssss=new StringBuffer();
						ssss.append(dd.getYear()+1900);
						nian=ssss.toString();
					}
					startime=nian+"-01-01";
					endtime=nian+"-12-31";
					str_tax_date=sts.getTax_DateSelStr(dao,"",uv);
					str_nian=sts.getYearSelStr(dao,nian,uv);
					hm.put("ymd","1");
				}
				sql=stsql.getSqlStr(dao,uv,"gz_tax_mx",startime,endtime);
				String sumsql=stsql.getSumSql(uv,startime,endtime);
				List dynalist=null;
				try{
				 dynalist=(ArrayList)dao.searchDynaList(sumsql);	
				}catch(Exception xe){
					xe.printStackTrace();
					throw GeneralExceptionHandler.Handle(new GeneralException("","日期输入错误！","",""));
					
				}
				finally{
					
					Date cud=new Date(System.currentTimeMillis());
					//hm.put("startime",cud.toString()); //chenmengqing added 
					//hm.put("endtime",cud.toString());
				}
				if(dynalist.size()>0){
					DynaBean dy=(DynaBean)dynalist.get(0);
					sumynse=(String) dy.get("sumynse");
					sumsds=(String) dy.get("sumsds");
				}
				reqhm.remove("timefield");
			}else{
//			按计税时间查询
				String tax_date=(String) hm.get("tax_date");
				String stime=tax_date+"-01";
				String etime=tax_date+"-31";				
				WeekUtils weekUtils=new WeekUtils();
				int yearnum=Integer.parseInt(tax_date.substring(0,4));				
				int monthnum=Integer.parseInt(tax_date.substring(5));
				etime=weekUtils.lastMonthStr(yearnum, monthnum);
				sql=stsql.getSqlStr(dao,uv,"gz_tax_mx",stime,etime);
//				sql=stsql.getSqlStr(dao,uv,"gz_tax_mx",tax_date);
				String sumsql=stsql.getSumSql(uv,stime,etime);				
				List dynalist=(ArrayList)dao.searchDynaList(sumsql);
				if(dynalist.size()>0){
					DynaBean dy=(DynaBean)dynalist.get(0);
					sumynse=(String) dy.get("sumynse");
					sumsds=(String) dy.get("sumsds");
				}
				str_tax_date=sts.getTax_DateSelStr(dao,tax_date,uv);
				str_nian=sts.getYearSelStr(dao,"",uv);
				hm.put("ymd","2");
			}
			reqhm.remove("action");
		}else{
//			初始化查询所有个人所得税纪录
			sql=stsql.getSqlStr(dao,uv,"gz_tax_mx");
			String sumsql=stsql.getSumSql(uv);
			List dynalist=(ArrayList)dao.searchDynaList(sumsql);
			if(dynalist.size()>0){
				DynaBean dy=(DynaBean)dynalist.get(0);
				sumynse=(String) dy.get("sumynse");
				sumsds=(String) dy.get("sumsds");
			}
			str_tax_date=sts.getTax_DateSelStr(dao,"",uv);
			str_nian=sts.getYearSelStr(dao,"",uv);
			hm.put("ymd","3");
		}
		if(sql[1]==null||sql[1].trim().length()<=0)
		{
			throw GeneralExceptionHandler.Handle(new GeneralException("","个税明细表结构未选指标！","",""));
		}
		hm.put("sumynse",sumynse);
		hm.put("sumsds",sumsds);
		hm.put("sql",sql[0]);
		hm.put("column",sql[1]);
		hm.put("where",sql[2]+" and flag=1");
		hm.put("orderby",sql[3]);
		hm.put("str_tax_date",str_tax_date);
		hm.put("str_nian",str_nian);
		hm.put("fieldlist",stsql.getFieldList("gz_tax_mx",dao));
	}
	private UserView getUserViewObj(String userbase,String a0100){
		UserView userView =null;
		try{
			DbNameBo dbbo = new DbNameBo(this.frameconn);
			ContentDAO dao = new ContentDAO(this.frameconn);
			String usernameField = dbbo.getLogonUserNameField();
			String username="";
			this.frowset= dao.search("SELECT " + usernameField + " FROM  " + userbase + "A01 WHERE a0100='" + a0100 + "'");
			if(this.frowset.next()){
				username = this.frowset.getString(usernameField);
			}
			userView = new UserView(username,this.frameconn);
			userView.canLogin();
		}catch(Exception e){
			e.printStackTrace();
		}
		return userView;
	}
}
