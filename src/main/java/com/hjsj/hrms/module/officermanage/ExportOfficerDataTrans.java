package com.hjsj.hrms.module.officermanage;

import com.hjsj.hrms.module.officermanage.businessobject.CardViewService;
import com.hjsj.hrms.module.officermanage.businessobject.impl.CardViewServiceImpl;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import net.sf.ezmorph.bean.MorphDynaBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/***
 * 导出lrmx类型文件
 */
public class ExportOfficerDataTrans extends IBusiness {
	boolean dateSource = false;// 区分从表格控件取参数与查数据库参数 表格控件参数 代码型指标不需翻译直接取数据
	@Override
	public void execute() throws GeneralException {
		
		try {
			TableDataConfigCache cache = (TableDataConfigCache) this.userView.getHm().get("OfficerManage_OfficerView");
			String flag = (String) this.getFormHM().get("flag");// false 导出部分 all 导出全部
			String type=(String)this.getFormHM().get("type");//word/pdf/xml
			String filetype=(String)this.getFormHM().get("filetype");// filetype: all 多人一文档 1：一人一文档 
			ArrayList<DynaBean> data = null;
			CardViewService bo=new CardViewServiceImpl(this.userView,this.frameconn);
			if ("false".equals(flag)) {// 部分
				dateSource = true;
				data = (ArrayList) this.getFormHM().get("data");
				StringBuffer sbf=new StringBuffer(); 
				sbf.append(" where guidkey in (");
				ArrayList list=new ArrayList();
				for(int i=0;i<data.size();i++) {
					MorphDynaBean bean=(MorphDynaBean)data.get(i);
					sbf.append("?");
					if(i<data.size()-1)
						sbf.append(",");
					list.add(bean.get("guidkey").toString());
				}
				sbf.append(")");
				if(cache!=null&&cache.getTableSql()!=null) {
					data = (ArrayList<DynaBean>)ExecuteSQL.executePreMyQuery("select * from ( "+cache.getTableSql()+" )A"+sbf.toString(), list, this.frameconn);
				}else {
					data = (ArrayList<DynaBean>)ExecuteSQL.executePreMyQuery("select * from  om_officer_muster "+sbf.toString(), list, this.frameconn);
				}
				String filename="";
				if("xml".equals(type)) {
					filename=bo.outFile(data);
				}else if("word".equals(type)||"pdf".equals(type)) {
					filename=bo.outwordOrPdf(data,type,filetype);
				}
				this.getFormHM().put("filename", filename);
				this.getFormHM().put("Typeflag", true);
			}else if("exp_current".equals(flag)) {
				ArrayList<String> list=new ArrayList<String>();
				MorphDynaBean bean=(MorphDynaBean)this.getFormHM().get("data");
				list.add(bean.get("nbase").toString().toUpperCase());
				list.add(bean.get("guidkey").toString());
				data= (ArrayList<DynaBean>)ExecuteSQL.executePreMyQuery("select * from om_officer_muster where nbase=? and guidkey=?", list, this.frameconn);
				if(data!=null&&data.size()==1) {
					DynaBean data_bean=(DynaBean) data.get(0);
					data_bean.set("a0100",bean.get("A0100").toString());
					String filename="";
					if("xml".equals(type)) {
						filename=bo.outFile(data);
					}else if("word".equals(type)||"pdf".equals(type)) {
						filename=bo.outwordOrPdf(data,type,filetype);
					}
					this.getFormHM().put("filename", filename);
					this.getFormHM().put("Typeflag", true);
				}
			} else {
			
				if(cache!=null&&cache.getTableSql()!=null) {
					StringBuffer sql = new StringBuffer();
					sql.append(" select * from (" +cache.getTableSql()+") A where 1=1");
					if (StringUtils.isNotEmpty(cache.getFilterSql())) {
						sql.append(cache.getFilterSql());
					}
					if (StringUtils.isNotEmpty(cache.getQuerySql())) {
						sql.append(cache.getQuerySql());
					}
					if (StringUtils.isNotEmpty(cache.getSortSql())) {
						sql.append(cache.getSortSql());
					}
					data = (ArrayList<DynaBean>) ExecuteSQL.executeMyQuery(sql.toString(), this.frameconn);
				}else {
					StringBuffer sbf=bo.getOfficerSql("",true);
					data = (ArrayList)ExecuteSQL.executeMyQuery("select om_officer_muster.* from  om_officer_muster right join ("+sbf.toString().substring(0, sbf.length()-10)+")AA on AA.A0100=om_officer_muster.A0100 and AA.guidkey=om_officer_muster.guidkey ", this.frameconn);
				}
				
				String filename="";
				if("xml".equals(type)) {
					filename=bo.outFile(data);
				}else if("word".equals(type)||"pdf".equals(type)) {
					filename=bo.outwordOrPdf(data,type,filetype);
				}
				this.getFormHM().put("filename", filename);
				this.getFormHM().put("Typeflag", true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.getFormHM().put("errorMsg", e.getMessage());
			this.getFormHM().put("Typeflag", false);
			
		}
	}
}
