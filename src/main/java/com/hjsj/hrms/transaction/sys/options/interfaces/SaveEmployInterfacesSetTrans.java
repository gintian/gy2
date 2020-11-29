package com.hjsj.hrms.transaction.sys.options.interfaces;

import com.hjsj.hrms.businessobject.sys.options.interfaces.SetInterfacesXml;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
/**
 * 人事平台接口人员
 * @author Owner
 *
 */
public class SaveEmployInterfacesSetTrans extends IBusiness {

	public void execute() throws GeneralException 
	{
		ArrayList filedlist=(ArrayList)this.getFormHM().get("fielditemlist");	
		String impmode=(String)this.getFormHM().get("impmode");
		String chitemid=(String)this.getFormHM().get("chitemid");
		String expmode=(String)this.getFormHM().get("expmode");
		ArrayList beanlist=new ArrayList();
		for(int i=0;i<filedlist.size();i++)
		{
			LazyDynaBean bean=new LazyDynaBean();	
			FieldItem fielditem=(FieldItem)filedlist.get(i);						
			bean.set("src", fielditem.getItemid());
			bean.set("dest", fielditem.getValue());
			String value=fielditem.getValue();
			String codesetid="";
			if(value==null||value.length()<=0)
			{
				codesetid="0";
			}else
			{
				fielditem=DataDictionary.getFieldItem(fielditem.getValue());
				if(fielditem!=null)
					codesetid=fielditem.getCodesetid();
			}	
			 
			
			bean.set("codesetid", codesetid);
			beanlist.add(bean);
		}
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","hr_service");
		String xmlContent="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			vo=dao.findByPrimaryKey(vo);
			if(vo!=null)
				xmlContent=vo.getString("str_value");
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
		}
		SetInterfacesXml setInterfacesXml=new SetInterfacesXml(xmlContent);
		String path="/param";
		setInterfacesXml.saveParamAttribute(path, "param", "impmode", impmode);
		setInterfacesXml.saveParamAttribute(path, "param", "expmode", expmode);
		path="/param/user";
		setInterfacesXml.saveParamAttribute(path, "user", "keyfield", chitemid);
		path="/param/user";
		setInterfacesXml.saveParamAttribute(path,"user", beanlist);		
		setInterfacesXml.saveParameter(this.getFrameconn());
	}

}
