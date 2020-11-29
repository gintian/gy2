package com.hjsj.hrms.transaction.general.impev;

import com.hjsj.hrms.businessobject.general.impev.ImportantEvBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChImpEvTrans extends IBusiness {

	public void execute() throws GeneralException {
		String pt_kpi_role_id = "";
		try{
			pt_kpi_role_id = SystemConfig.getProperty("pt_kpi_role_id");
		}catch(Exception e){}
		// TODO Auto-generated method stub
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		
		String checkflag = (String)this.getFormHM().get("checkflag");
		checkflag=checkflag!=null&&checkflag.trim().length()>0?checkflag:"0";
		
		String fromdate = (String)this.getFormHM().get("fromdate");
		fromdate=fromdate!=null?fromdate:"";
		
		String todate = (String)this.getFormHM().get("todate");
		todate=todate!=null?todate:"";
		
		String a_code = (String)hm.get("a_code");
		a_code=a_code!=null?a_code:"";
		hm.remove("a_code");
		
		ArrayList list = DataDictionary.getFieldList("P06",Constant.USED_FIELD_SET);
		ArrayList fieldlist = new ArrayList();
		ImportantEvBo imv = new ImportantEvBo(this.userView,this.frameconn);
		StringBuffer sqlstr = new StringBuffer("select ");
		StringBuffer cloums = new StringBuffer();
		StringBuffer wherestr= new StringBuffer();
		StringBuffer orderby= new StringBuffer();
		for(int i=0;i<list.size();i++){
			FieldItem fielditem = (FieldItem)list.get(i);
			cloums.append(fielditem.getItemid()+",");
			if(!fielditem.isVisible())
				continue;
			fieldlist.add(fielditem);
		}
		String wheresql = imv.whereCodeStr(a_code);
		if(wheresql.trim().length()>0){
			if(this.userView.haveRoleId(pt_kpi_role_id)){//部门KPI管理员角色 普天个性需求
				wherestr.append("from P06 where (");
				wherestr.append(wheresql);
				wherestr.append(" and p0600 in(select p0600 from per_keyevent_actor");
				wherestr.append(" where A0100='");
				wherestr.append(this.userView.getUserId());
				wherestr.append("' and NBASE='"+this.userView.getDbname()+"')) and P0609='1'");
			}else{
				wherestr.append("from P06 where (");
				wherestr.append(wheresql);
				wherestr.append(" or p0600 in(select p0600 from per_keyevent_actor");
				wherestr.append(" where A0100='");
				wherestr.append(this.userView.getUserId());
				wherestr.append("' and NBASE='"+this.userView.getDbname()+"')) and P0609='1'");
			}
		}else{
			wherestr.append("from P06 where");
			wherestr.append(" p0600 in(select p0600 from per_keyevent_actor");
			wherestr.append(" where A0100='");
			wherestr.append(this.userView.getUserId());
			wherestr.append("' and NBASE='"+this.userView.getDbname()+"') and P0609='1'");
		}
		if("1".equals(checkflag)){
			if(fromdate.trim().length()<1||todate.trim().length()<1){
				if(fromdate.trim().length()>1){
					wherestr.append(" and P0603>="+Sql_switcher.dateValue(fromdate+" 00:00:00"));
				}else if(todate.trim().length()>1){
					wherestr.append(" and P0603<="+Sql_switcher.dateValue(todate+" 23:59:59"));
				}
			}else{
				wherestr.append(" and P0603");
				wherestr.append(" BETWEEN ");
				wherestr.append(Sql_switcher.dateValue(fromdate+" 00:00:00"));
				wherestr.append(" AND "+Sql_switcher.dateValue(todate+" 23:59:59"));
			}
		}
		sqlstr.append(cloums.substring(0, cloums.length()-1));
		//System.out.println(wherestr.toString());
		orderby.append("order by P0603 desc");
		
		
		
		/**
		 * 换hrms:extenditerate标签分页，并去除html样式
		 */
		try {
	//	ImportantEvBo bo=new ImportantEvBo(userView, this.frameconn);
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
					}else{
						if("p0600".equalsIgnoreCase(fielditem.getItemid())){
							bean.set(fielditem.getItemid(), PubFunc.encrypt(rs.getString(fielditem.getItemid())!=null?rs.getString(fielditem.getItemid()):""));
						}else
							bean.set(fielditem.getItemid(), rs.getString(fielditem.getItemid())!=null?rs.getString(fielditem.getItemid()):"");
					}
				}
			}
			resultList.add(bean);
		}
		this.getFormHM().put("sqlstr", sqlstr.toString());
		this.getFormHM().put("cloums", cloums.substring(0, cloums.length()-1).toString());
		this.getFormHM().put("wherestr", wherestr.toString());
		this.getFormHM().put("orderby", orderby.toString());
		this.getFormHM().put("checkflag",checkflag);
		this.getFormHM().put("todate", todate);
		this.getFormHM().put("fromdate",fromdate);
		this.getFormHM().put("fieldlist", fieldlist);
		this.getFormHM().put("a_code",a_code);
		this.getFormHM().put("resultList", resultList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
