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
/*
 * 人事平台接保存
 */
public class SaveInterfacesTrans extends IBusiness{

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		ArrayList filedlist=(ArrayList)this.getFormHM().get("fielditemlist");	
		String impmode=(String)this.getFormHM().get("impmode"); //导入
		String chitemid=(String)this.getFormHM().get("chitemid");
		String expmode=(String)this.getFormHM().get("expmode"); //导出
		String marker=(String)this.getFormHM().get("marker"); // 返回唯一标示 1：返回用户 0：返回HR平台
		
		ArrayList orgitemlist=(ArrayList)this.getFormHM().get("orgitemlist");
//		String orgchitemid=(String)this.getFormHM().get("orgchitemid");  //关联
		ArrayList beanlist=new ArrayList();
		for(int i=0;i<filedlist.size();i++)
		{
			
			FieldItem fielditem=(FieldItem)filedlist.get(i);
			if(fielditem.getViewvalue()!=null&&fielditem.getViewvalue().length()>0)
			{
				LazyDynaBean bean=new LazyDynaBean();	
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
			
		}
		ArrayList orgbeanlist=new ArrayList();
		for(int i=0;i<orgitemlist.size();i++)
		{
			
			FieldItem fielditem=(FieldItem)orgitemlist.get(i);	
			if(fielditem.getViewvalue()!=null&&fielditem.getViewvalue().length()>0)
			{
				LazyDynaBean orgbean=new LazyDynaBean();	
				orgbean.set("src", fielditem.getItemid());
				orgbean.set("dest", fielditem.getValue());
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
				orgbean.set("codesetid", codesetid);
				orgbeanlist.add(orgbean);
			}
			
		}
		RecordVo vo=new RecordVo("constant");
		vo.setString("constant","HR_SERVICE");
		String xmlContent="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			vo=dao.findByPrimaryKey(vo);
			if(dao.isExistRecordVo(vo)) {
				xmlContent=vo.getString("str_value");
			}
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
		}
		SetInterfacesXml setInterfacesXml=new SetInterfacesXml(xmlContent);
		String path="/param";
		setInterfacesXml.saveParamAttribute(path, "param", "impmode", impmode);
		setInterfacesXml.saveParamAttribute(path, "param", "expmode", expmode);
		setInterfacesXml.saveParamAttribute(path, "param", "marker", marker);
		
		path="/param/user";
		setInterfacesXml.saveParamAttribute(path, "user", "keyfield", chitemid);
		path="/param/user";
		setInterfacesXml.saveParamAttribute(path,"user", beanlist);
		path="/param/org";
//		setInterfacesXml.saveParamAttribute(path, "org", "keyfield", orgchitemid);
		setInterfacesXml.saveOrgParamAttribute(path, "org");
		path="/param/org";
		setInterfacesXml.saveParamAttribute(path,"org", orgbeanlist);
		setInterfacesXml.saveParameter(this.getFrameconn());
	}

}
