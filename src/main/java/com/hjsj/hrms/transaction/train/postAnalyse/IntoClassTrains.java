package com.hjsj.hrms.transaction.train.postAnalyse;

import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class IntoClassTrains extends IBusiness {

		public void execute() throws GeneralException {
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String userbase=(String)this.getFormHM().get("dbpre");
			String a0100=(String)hm.get("a0100");
			a0100=SafeCode.decode(a0100);
			String codeitemid = "";
			String b0110="";
			String[] a0100s=a0100.split(",");
			ContentDAO dao=new ContentDAO(this.frameconn);
		try{
			for (int i = 0; i < a0100s.length; i++) {
				if (a0100s[i] != null && a0100s[i].length() > 0) {
					this.frowset = dao.search("select r41.r4118 r4118,r40.B0110 B0110 from r41 join r40 on r40.r4005=r41.r4103 where r40.R4001='" + a0100s[i]
									+ "' and r40.B0110=(select b0110 from " + userbase + "a01 where a0100='" + a0100s[i] + "')");
					while (this.frowset.next()) {
						codeitemid += "'" + this.frowset.getString("r4118") + "',";
						b0110 += "'" + this.frowset.getString("B0110") + "',";
					}

				}
			}
			ArrayList list = new ArrayList();
			ArrayList fieldlist = DataDictionary.getFieldList("r31",Constant.USED_FIELD_SET);
			ArrayList fields=new ArrayList();
			StringBuffer buf = new StringBuffer();
			StringBuffer wherestr = new StringBuffer();
			StringBuffer columns = new StringBuffer();
			buf.append("select ");
			for(int i=0;i<fieldlist.size();i++){
				FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if ("r3101".equalsIgnoreCase(fielditem.getItemid()) || "r3130".equalsIgnoreCase(fielditem.getItemid())
					|| "r3113".equalsIgnoreCase(fielditem.getItemid()) || "r3114".equalsIgnoreCase(fielditem.getItemid())
					|| "r3115".equalsIgnoreCase(fielditem.getItemid()) || "r3110".equalsIgnoreCase(fielditem.getItemid())
					|| "r3116".equalsIgnoreCase(fielditem.getItemid())){
				fields.add(fielditem);
				buf.append(fielditem.getItemid());
				columns.append(fielditem.getItemid());
				buf.append(",");
				columns.append(",");
			}
				if("r3101".equalsIgnoreCase(fielditem.getItemid())){
					list.add(0,fielditem);
			} 
			}
			String buff = buf.substring(0,buf.length()-1).toString();
			wherestr.append(" from r31  where r3127='04'");
			Date date = new Date();
			SimpleDateFormat f1 = new SimpleDateFormat("yyyyMMdd");
			String date1 = f1.format(date);
			wherestr.append(" and (" + Sql_switcher.year("R3113") + "*10000+"
					+ Sql_switcher.month("R3113") + "*100+"
					+ Sql_switcher.day("R3113") + ")<=" + date1);
			wherestr.append(" and (" + Sql_switcher.year("R3114") + "*10000+"
					+ Sql_switcher.month("R3114") + "*100+"
					+ Sql_switcher.day("R3114") + ")>=" + date1);
			
			if(codeitemid!=null&&codeitemid.length()>0)
				wherestr.append(" and exists(select 1 from  r41 where r31.r3101=r41.R4103 and r4118 in ("+codeitemid.substring(0, codeitemid.length()-1)+"))");
			if(b0110!=null&&b0110.trim().length()>0)
				wherestr.append(" and b0110 in ("+b0110.substring(0, b0110.length()-1)+")");
			this.getFormHM().put("sqlstr",buff+wherestr.toString());
			this.getFormHM().put("where", "");
			this.getFormHM().put("browsefields", fields);
			this.getFormHM().put("cloumn", columns.substring(0, columns.length()-1));
		}catch (Exception e) {
			e.printStackTrace();
		}
			
		}

}
