package com.hjsj.hrms.transaction.stat;

import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 保存信息集设置
 * @author xujian
 *Mar 23, 2010
 */
public class SaveArchiveTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String dbname = (String) this.getFormHM().get("dbname");
		String condition = (String) this.getFormHM().get("condition");
		condition=condition==null?"":condition;
		String unit=(String)this.getFormHM().get("unit");
		unit=unit==null?"":unit;
		String seasonal =(String)this.getFormHM().get("seasonal");
		seasonal=seasonal==null?"":seasonal;
		ArrayList volist = (ArrayList)this.getFormHM().get("volist");
		String id=(String)this.getFormHM().get("id");;
		String auto=(String)this.getFormHM().get("auto");
		auto=auto==null?"":auto;
		String dept_level=(String)this.getFormHM().get("dept_level");
		dept_level=dept_level==null?"":dept_level;
		String ctrl=(String)this.getFormHM().get("ctrl");
		ctrl=ctrl==null?"":ctrl;
		String unit_level=(String)this.getFormHM().get("unit_level");
		unit_level=unit_level==null?"":unit_level;
		if("0".equals(auto)){
			auto="";
			unit_level="";
			ctrl="";
			dept_level="";
		}
		if("0".equals(ctrl)){
			ctrl="";
			dept_level="";
		}
		String sql = "update sname set archive_type='"+seasonal+"',archive_set='"+unit+"',nbase='"+dbname+"',condid='" + condition +"',archive=null where id='"+id+"'";
		String msg="error";
		try{
			ContentDAO dao = new ContentDAO(this.frameconn);
			dao.update(sql);
			if("1".equals(auto)){
				ArchiveXml xml = new ArchiveXml(this.frameconn,id,"");
				xml.setValue("auto", auto);
				xml.setValue("unit_level", unit_level);
				xml.setValue("dept_ctrl", ctrl);
				xml.setValue("dept_level", dept_level);
				xml.saveStrValue();
			}
			sql = "update SLegend set archive_field=? where id=? and norder=?";
			if(volist!=null){
				for(int i=0;i<volist.size();i++){
					LazyDynaBean ldb = (LazyDynaBean)volist.get(i);
					id=(String)ldb.get("id");
					String norder = (String)ldb.get("norder");
					String archive_field =(String)ldb.get("archive_field");
					ArrayList values = new ArrayList();
					values.add(archive_field);
					values.add(id);
					values.add(norder);
					dao.update(sql, values);
				}
			}
			msg = "ok";
		}catch(Exception e){
			e.printStackTrace();
			msg = "error";
		}finally{
			this.getFormHM().put("msg", msg);
		}
	
	}

}
