package com.hjsj.hrms.transaction.dtgh.party;

import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.TimeScope;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckCanBolishTran extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList codeitemidlist = (ArrayList)this.getFormHM().get("codeitemidlist");
		String codesetid = (String)this.getFormHM().get("codesetid");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String backdate = (String)this.getFormHM().get("backdate");
		backdate = backdate!=null&&backdate.length()>9?backdate:sdf.format(new Date());
		String msg="yes";
		String column="";
		try{
			if("64".equals(codesetid)||"65".equals(codesetid)||"66".equals(codesetid)){
				ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
				if("64".equals(codesetid)){
					column = xml.getValue("belongparty");
					column = column!=null&&column.length()>0?column:"";
				}else if("65".equals(codesetid)){
					column = xml.getValue("belongmember");
					column = column!=null&&column.length()>0?column:"";
				}else if("66".equals(codesetid)){
					column = xml.getValue("belongmeet");
					column = column!=null&&column.length()>0?column:"";
				}
				if(column.length()<=0)
					throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("dtgh.party.parameter.setup.belong"),"",""));
			}
			ArrayList dblist = DataDictionary.getDbpreList();
			ContentDAO dao = new ContentDAO(this.frameconn);
			for(int i=0;i<codeitemidlist.size();i++){
				String codeitemid = (String)codeitemidlist.get(i);
				//如果是当天新建节点不允许撤销
				TimeScope ts = new TimeScope();
				StringBuffer sql=new StringBuffer("select codeitemid from codeitem where codesetid='"+codesetid+"' and codeitemid='"+codeitemid+"' and "+ts.getTimeCond("start_date", "=", sdf.format(new Date())));
				this.frecset = dao.search(sql.toString());
				if(this.frecset.next()){
					msg="sameday";
					break;
				}
				if("64".equals(codesetid)||"65".equals(codesetid)||"66".equals(codesetid)){
					//查当前节点下是否有节点
					sql.setLength(0);
					sql.append("select codeitemid from codeitem where codesetid='"+codesetid+"' and parentid='"+codeitemid+"'");
					sql.append(" and "+Sql_switcher.dateValue(backdate)+" between start_date and end_date and codeitemid<>parentid");
					this.frecset = dao.search(sql.toString());
					if(this.frecset.next()){
						msg="havechild";
						break;
					}
					//查当前节点下是否有人
					sql.setLength(0);
					for(int n=0;n<dblist.size();n++){
						String dbpre = (String)dblist.get(n);
						sql.append("select a0100 from "+dbpre+"A01 where "+column+"='"+codeitemid+"' union ");
					}
					this.frecset = dao.search(sql.substring(0, sql.length()-6));
					if(this.frecset.next()){
						msg="haveperson";
						break;
					}
				}
			}
		}catch(Exception e){
			throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("msg", msg);
		}
	}

}
