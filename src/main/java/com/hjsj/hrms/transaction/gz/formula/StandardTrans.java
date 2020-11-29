package com.hjsj.hrms.transaction.gz.formula;

import com.hjsj.hrms.businessobject.gz.FormulaBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 *<p>Title:</p> 
 *<p>Description:计算公式</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:${date}:${time}</p> 
 *@author lilinbing
 *@version 4.0
**/
public class StandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			hm.remove("opt");

			
			String standardID="";
			String pkg_id="";
			String hfactor="";
			String s_hfactor="";
			String vfactor="";
			String s_vfactor="";
			String item="";
			String hcontent="";
			String vcontent="";
			if("alert".equals(opt)){
				GzStandardItemVo vo=(GzStandardItemVo)this.getFormHM().get("gzStandardItemVo");
				String gzStandardName=(String)this.getFormHM().get("gzStandardName");
				opt=(String)this.getFormHM().get("opt");
				standardID=(String)this.getFormHM().get("standardID");
				FormulaBo formulabo = new FormulaBo();
				pkg_id=formulabo.pkgId(this.frameconn,standardID);
				GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),this.userView);
				bo.saveSalaryStandard(vo,pkg_id,gzStandardName,opt,standardID);
			}else{
				standardID=(String)hm.get("standardID");
				hm.remove("standardID");
				FormulaBo formulabo = new FormulaBo();
				pkg_id=formulabo.pkgId(this.frameconn,standardID);
			}
			if(opt==null||!"edit".equals(opt))
			{
				if("alert".equals(opt)){
					GzStandardItemVo vo=(GzStandardItemVo)this.getFormHM().get("gzStandardItemVo");
					String gzStandardName=(String)this.getFormHM().get("gzStandardName");
					pkg_id=(String)this.getFormHM().get("pkg_id");
					opt=(String)this.getFormHM().get("opt");
					standardID=(String)this.getFormHM().get("standardID");
					GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),this.userView);
					bo.saveSalaryStandard(vo,pkg_id,gzStandardName,opt,standardID);
				}
				
				 hfactor=(String)this.getFormHM().get("hfactor");
				 s_hfactor=(String)this.getFormHM().get("s_hfactor");
				 vfactor=(String)this.getFormHM().get("vfactor");
				 s_vfactor=(String)this.getFormHM().get("s_vfactor");
				 item=(String)this.getFormHM().get("item");
				 hcontent=(String)this.getFormHM().get("hcontent");
				 vcontent=(String)this.getFormHM().get("vcontent");
				 opt="new";
				 standardID="";
			}
			else if("edit".equals(opt))
			{
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String sql = "select * from gz_stand_history where id='"+standardID+"' and pkg_id='"+pkg_id+"'";
				this.frowset=dao.search(sql);
				String gzStandardName="";
				if(this.frowset.next())
				{
					 hfactor=this.frowset.getString("hfactor");
					 s_hfactor=this.frowset.getString("s_hfactor");
					 vfactor=this.frowset.getString("vfactor");
					 s_vfactor=this.frowset.getString("s_vfactor");
					 item=this.frowset.getString("item");
					 hcontent=this.frowset.getString("hcontent");
					 vcontent=this.frowset.getString("vcontent");
					 gzStandardName=this.frowset.getString("name");
					 String desc = "";
					if(item!=null&&item.length()>0){
						FieldItem fielditem = DataDictionary.getFieldItem(item);
						desc = fielditem.getItemdesc();	
					}
					this.getFormHM().put("desc",desc);
				}
				
				String hfactor_name = "";
				if(hfactor!=null&&hfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(hfactor);
					hfactor_name = fielditem.getItemdesc();	
				}
				String s_hfactor_name = "";
				if(s_hfactor!=null&&s_hfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(s_hfactor);
					s_hfactor_name = fielditem.getItemdesc();	
				}
				String vfactor_name = "";
				if(vfactor!=null&&vfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(vfactor);
					vfactor_name = fielditem.getItemdesc();	
				}
				String s_vfactor_name = "";
				if(s_vfactor!=null&&s_vfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(s_vfactor);
					s_vfactor_name = fielditem.getItemdesc();	
				}
				
				String desc = "";
				if(item!=null&&item.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(item);
					desc = fielditem.getItemdesc();	
				}
				
				
				this.getFormHM().put("hfactor",hfactor);
				this.getFormHM().put("s_hfactor",s_hfactor);
				this.getFormHM().put("vfactor",vfactor);
				this.getFormHM().put("s_vfactor",s_vfactor);
				this.getFormHM().put("item",item);
				this.getFormHM().put("hcontent",hcontent);
				this.getFormHM().put("vcontent",vcontent);
				this.getFormHM().put("hfactor_name",hfactor_name);
				this.getFormHM().put("s_hfactor_name",s_hfactor_name);
				this.getFormHM().put("vfactor_name",vfactor_name);
				this.getFormHM().put("s_vfactor_name",s_vfactor_name);
				this.getFormHM().put("desc",desc);
				
				this.getFormHM().put("opt",opt);
				this.getFormHM().put("standardID",standardID);
				this.getFormHM().put("gzStandardName",gzStandardName);
			}
			
			GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,standardID,opt,pkg_id);
			bo.init();
			GzStandardItemVo vo=bo.getGzStandardItemVo();
			bo.setLocked(false);
			String html = bo.getHtml(vo);
			
			this.getFormHM().put("pkg_id",pkg_id);
			this.getFormHM().put("gzStandardItemHtml",html);
			this.getFormHM().put("gzItemList",vo.getGzItemList());
			this.getFormHM().put("gzStandardItemVo",vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
