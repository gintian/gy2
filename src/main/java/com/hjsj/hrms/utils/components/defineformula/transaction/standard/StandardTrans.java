package com.hjsj.hrms.utils.components.defineformula.transaction.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.defineformula.businessobject.DefineFormulaBo;
import com.hjsj.hrms.utils.components.defineformula.businessobject.GzStandardItemBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
/**
 * 项目名称 ：ehr
 * 类名称：StandardTrans
 * 类描述：标准表
 * 创建人： lis
 * 创建时间：2016-2-3
 */
public class StandardTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM();
			
			String standardID=(String)hm.get("standardID");//标准表id
			if(StringUtils.isBlank(standardID))
				standardID = "";
			
			String pkg_id="";
			String hfactor="";//横向栏目id
			String hfactor_name = "";//横向栏目名称
			String s_hfactor="";//横向子栏目id
			String s_hfactor_name = "";//横向子栏目名称
			String vfactor="";//纵向栏目id
			String vfactor_name = "";//纵向栏目名称
			String s_vfactor="";//纵向子栏目id
			String s_vfactor_name = "";//纵向子栏目名称
			String item="";//目标指标id
			String desc = "";//目标指标名称
			String hcontent="";//横向内容
			String vcontent="";//纵向内容
			
			DefineFormulaBo formulabo = new DefineFormulaBo(this.getFrameconn(), this.userView);
			pkg_id=formulabo.pkgId(this.frameconn,standardID);
			
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
				 
				 if(hfactor!=null&&hfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(hfactor);
					hfactor_name = fielditem.getItemdesc();	
				 }
				
				 if(s_hfactor!=null&&s_hfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(s_hfactor);
					s_hfactor_name = fielditem.getItemdesc();	
				 }
				
				 if(vfactor!=null&&vfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(vfactor);
					vfactor_name = fielditem.getItemdesc();	
				 }
				
				 if(s_vfactor!=null&&s_vfactor.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(s_vfactor);
					s_vfactor_name = fielditem.getItemdesc();	
				 }
				
				 if(item!=null&&item.length()>0){
					FieldItem fielditem = DataDictionary.getFieldItem(item);
					desc = fielditem.getItemdesc();	
				 }
				 if(StringUtils.isNotBlank(hfactor))
				   this.getFormHM().put("hfactor_name",hfactor_name + "（" + hfactor + "）");
				 else
					 this.getFormHM().put("hfactor_name","");
				 if(StringUtils.isNotBlank(s_hfactor))
					 this.getFormHM().put("s_hfactor_name",s_hfactor_name + "（" + s_hfactor + "）");
				 else
					 this.getFormHM().put("s_hfactor_name","");
				 if(StringUtils.isNotBlank(vfactor))
					   this.getFormHM().put("vfactor_name",vfactor_name + "（" + vfactor + "）");
					 else
						 this.getFormHM().put("vfactor_name","");
				 if(StringUtils.isNotBlank(s_vfactor))
					   this.getFormHM().put("s_vfactor_name",s_vfactor_name + "（" + s_vfactor + "）");
					 else
						 this.getFormHM().put("s_vfactor_name","");
				 if(StringUtils.isNotBlank(item))
					   this.getFormHM().put("desc",desc + "（" + item + "）");
					 else
						 this.getFormHM().put("desc","");
				
				GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,standardID,"edit",pkg_id);
				bo.init();
				GzStandardItemVo vo=bo.getGzStandardItemVo();
				bo.setLocked(false);
				String html = bo.getHtml(vo);
				this.getFormHM().put("gzStandardName",gzStandardName);
				this.getFormHM().put("gzStandardItemHtml",html);
			}else{
				this.getFormHM().put("hfactor_name","");
				this.getFormHM().put("s_hfactor_name","");
				this.getFormHM().put("vfactor_name","");
				this.getFormHM().put("s_vfactor_name","");
				this.getFormHM().put("desc","");
				this.getFormHM().put("gzStandardName",ResourceFactory.getProperty("label.select")+"...");//请选择
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
