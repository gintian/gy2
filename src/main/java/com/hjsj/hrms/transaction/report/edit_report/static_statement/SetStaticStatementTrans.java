/**
 * 
 */
package com.hjsj.hrms.transaction.report.edit_report.static_statement;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Title:查询功能列表</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:Oct 29, 2008:3:15:01 PM</p>
 * @author xgq
 * @version 1.0
 * 
 */
public class SetStaticStatementTrans extends IBusiness {
    /**
	 */
	
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
		String scopeid=(String)hm.get("scopeid");
		String startscopeid=(String)hm.get("startscopeid");
		hm.remove("startscopeid");
		String unitsowner="";
		String unit="";
		ArrayList list=new ArrayList();
		String ownerunits="";
		String units="";
		LazyDynaBean bean;
		try{
			String sql ="";
			if(startscopeid!=null&& "-1".equals(startscopeid))
			 sql="select * from tscope   order by displayid ";
			else 
				sql="select * from tscope where scopeid= "+scopeid;
			ContentDAO dao=new ContentDAO(this.frameconn);
			
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				units=Sql_switcher.readMemo(this.frowset, "units");
				ownerunits=this.frowset.getString("owner_unit");
			}
		
			if(ownerunits==null||ownerunits.trim().length()==0){
				unitsowner="";
			}else{
				unitsowner=AdminCode.getCodeName(ownerunits.substring(0, 2),ownerunits.substring(2,ownerunits.length()) );
			}
			//if(units.trim().length()!=0){
			if(units.trim().length()!=0&&!"-1".equals(startscopeid)){//liuy 2015-1-22 6915：设置统计口径报表，没有统计口径时，删除机构报错
				String[] temp=units.split("`");
				for(int i=0;i<temp.length;i++){
					if(temp[i].length()>2){
					bean=new LazyDynaBean();
					unit=AdminCode.getCodeName(temp[i].substring(0,2),temp[i].substring(2,temp[i].length()));
					bean.set("units",unit);
					bean.set("unitcode", temp[i].substring(2,temp[i].length()));
					list.add(bean);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		this.getFormHM().put("unitcodelist", list);
		this.getFormHM().put("scopeownerunitid", ownerunits);
		this.getFormHM().put("scopeownerunit", unitsowner);
		this.getFormHM().put("scopeid", scopeid);
	}
		
}
