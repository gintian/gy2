package com.hjsj.hrms.transaction.sys.warn;

import com.hjsj.hrms.businessobject.sys.warn.ContextTools;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.DynaBean;

import java.util.ArrayList;

/**
 * 预警排序
 * @author Administrator
 *
 */
public class SortWarnTrans extends IBusiness {

	public void execute() throws GeneralException {
		String wid=(String)this.getFormHM().get("wid");
		String type=(String)this.getFormHM().get("type");
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			String sql="select count(norder) from hrpwarn group by norder having COUNT(norder)>1";
			this.frowset=dao.search(sql);
			if(this.frowset.next()){
				StringBuffer strSql=new StringBuffer("select wid,norder from hrpwarn");
				strSql.append(" order by norder");
				this.frowset = dao.search(strSql.toString());
				ArrayList values=new ArrayList();
				int i=1;
				while(this.frowset.next()){
					ArrayList vs=new ArrayList();
					vs.add(new Integer(i));
					i++;
					vs.add(new Integer(this.frowset.getInt("wid")));
					values.add(vs);
				}
				sql="update hrpwarn set norder=? where wid=?";
				dao.batchUpdate(sql, values);
			}
			if("up".equals(type)){
				StringBuffer strSql=new StringBuffer("select wid,norder from hrpwarn");
				if( userView.isSuper_admin() ){//管理员
			        	//所有预警条件
			    }else{//一般用户
			        strSql.append(" where b0110='UN' or b0110 ='UN"+userView.getUserOrgId()+"'");
			    }
				strSql.append(" order by norder");
				this.frowset=dao.search(strSql.toString());
				int onorder=1;
				int norder=0;
				String owid="";
				while(this.frowset.next()){
					norder=this.frowset.getInt("norder");
					String temp=this.frowset.getString("wid");
					if(temp.equals(wid))
						break;
					owid=this.frowset.getString("wid");
					onorder=this.frowset.getInt("norder");
				}
				ArrayList values=new ArrayList();
				ArrayList vs=new ArrayList();
				vs.add(new Integer(onorder));
				vs.add(new Integer(wid));
				values.add(vs);
				vs=new ArrayList();
				vs.add(new Integer(norder));
				vs.add(new Integer(owid));
				values.add(vs);
				sql="update hrpwarn set norder=? where wid=?";
				dao.batchUpdate(sql, values);
				DynaBean dbean=(DynaBean) ContextTools.getWarnConfigCache().get(wid);
				dbean.set("norder", String.valueOf(onorder));
				dbean=(DynaBean) ContextTools.getWarnConfigCache().get(owid);
				dbean.set("norder", String.valueOf(norder));
			}else{
				StringBuffer strSql=new StringBuffer("select wid,norder from hrpwarn");
				if( userView.isSuper_admin() ){//管理员
			        	//所有预警条件
			    }else{//一般用户
			        strSql.append(" where b0110='UN' or b0110 ='UN"+userView.getUserOrgId()+"'");
			    }
				strSql.append(" order by norder desc");
				this.frowset=dao.search(strSql.toString());
				int onorder=1;
				int norder=0;
				String owid="";
				while(this.frowset.next()){
					norder=this.frowset.getInt("norder");
					String temp=this.frowset.getString("wid");
					if(temp.equals(wid))
						break;
					owid=this.frowset.getString("wid");
					onorder=this.frowset.getInt("norder");
				}
				ArrayList values=new ArrayList();
				ArrayList vs=new ArrayList();
				vs.add(new Integer(onorder));
				vs.add(new Integer(wid));
				values.add(vs);
				vs=new ArrayList();
				vs.add(new Integer(norder));
				vs.add(new Integer(owid));
				values.add(vs);
				sql="update hrpwarn set norder=? where wid=?";
				dao.batchUpdate(sql, values);
				DynaBean dbean=(DynaBean) ContextTools.getWarnConfigCache().get(wid);
				//预警设置，已禁用的预警重新排序时报错  jingq upd  2014.10.24
				if(dbean!=null){
					dbean.set("norder", String.valueOf(onorder));
				}
				dbean=(DynaBean) ContextTools.getWarnConfigCache().get(owid);
				dbean.set("norder", String.valueOf(norder));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
	}

}
