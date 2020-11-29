package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.businessobject.general.impev.ImportantEvBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ImportantEvTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try{
			String checkflag = (String)this.getFormHM().get("checkflag");
			checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"0";
			
			String fromdate = (String)this.getFormHM().get("fromdate");
			fromdate=fromdate!=null?fromdate:"";
			
			String todate = (String)this.getFormHM().get("todate");
			todate=todate!=null?todate:"";
			
			ArrayList list = DataDictionary.getFieldList("p06",Constant.USED_FIELD_SET);
			ArrayList fieldlist = new ArrayList();
			StringBuffer sqlstr = new StringBuffer("select ");
			StringBuffer cloums = new StringBuffer();
			StringBuffer wherestr= new StringBuffer();
			StringBuffer orderby= new StringBuffer();
			if(list!=null){
				for(int i=0;i<list.size();i++){
					FieldItem fielditem = (FieldItem)list.get(i);
					cloums.append(fielditem.getItemid()+",");
					if(!fielditem.isVisible())
						continue;
					
					if("N".equalsIgnoreCase(fielditem.getItemtype())&&fielditem.getDecimalwidth()>2)
						fielditem.setDecimalwidth(2);
					fieldlist.add(fielditem);
				}
			}
			wherestr.append("from p06 where A0100='");
			wherestr.append(this.userView.getUserId());
			wherestr.append("' and NBASE='");
			wherestr.append(this.userView.getDbname());
			wherestr.append("'");
			if("1".equals(checkflag)){
				if(fromdate.trim().length()<1||todate.trim().length()<1){ 
					if(fromdate.trim().length()>1)
						wherestr.append(" and P0603>="+Sql_switcher.dateValue(fromdate+" 00:00:00"));
					if(todate.trim().length()>1)
						wherestr.append(" and P0603<="+Sql_switcher.dateValue(todate+" 23:59:59"));
				}else{
					wherestr.append(" and P0603");
					wherestr.append(" BETWEEN ");
					wherestr.append(Sql_switcher.dateValue(fromdate+" 00:00:00"));
					wherestr.append(" AND "+Sql_switcher.dateValue(todate+" 23:59:59"));
				}
			}
			sqlstr.append(cloums.length()>0?cloums.substring(0, cloums.length()-1):"");
			orderby.append("order by P0603 desc");
			
			/**
			 * 换hrms:extenditerate标签分页，并去除html样式
			 */
//			ImportantEvBo bo=new ImportantEvBo(userView, this.frameconn);
			ArrayList resultList=new ArrayList();
			String sql=sqlstr.toString()+" "+wherestr.toString()+" "+orderby.toString();
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			ResultSet rs=null;
			rs=dao.search(sql);
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				LazyDynaBean bean=new LazyDynaBean();
				if(list!=null){
					for(int i=0;i<list.size();i++){
						FieldItem fielditem=(FieldItem)list.get(i);
						if("M".equalsIgnoreCase(fielditem.getItemtype())){
							bean.set(fielditem.getItemid(), ImportantEvBo.delHTMLTag(rs.getString(fielditem.getItemid())));
						}else if("D".equalsIgnoreCase(fielditem.getItemtype())){//针对oracle库
						 
							Date d= rs.getDate(fielditem.getItemid());
							if(d!=null)
							{
								bean.set(fielditem.getItemid(),df.format(d));
							}
							else
								bean.set(fielditem.getItemid(),""); 
						}
						else{ 
							bean.set(fielditem.getItemid(), rs.getString(fielditem.getItemid())!=null?rs.getString(fielditem.getItemid()):"");
						}
					}
				}
				resultList.add(bean);
			}
			
			
			
			
			
			this.getFormHM().put("sqlstr", sqlstr.toString());
			this.getFormHM().put("cloums", cloums.length()>0?cloums.substring(0, cloums.length()-1):"");
			this.getFormHM().put("wherestr", wherestr.toString());
			this.getFormHM().put("orderby", orderby.toString());
			this.getFormHM().put("checkflag",checkflag);
			this.getFormHM().put("todate", todate);
			this.getFormHM().put("fromdate",fromdate);
			this.getFormHM().put("fieldlist", fieldlist);
			this.getFormHM().put("resultList", resultList);
		}catch(Exception e){
			e.printStackTrace();
		}
	}


}
