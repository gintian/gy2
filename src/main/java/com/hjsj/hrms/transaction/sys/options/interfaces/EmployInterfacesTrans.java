package com.hjsj.hrms.transaction.sys.options.interfaces;

import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

public class EmployInterfacesTrans extends IBusiness {

	public void execute() throws GeneralException {		
//		RecordVo vo=new RecordVo("constant");
//		vo.setString("constant","hr_service");
//		String xmlContent="";
//		try
//		{
//			ContentDAO dao=new ContentDAO(this.getFrameconn());
//			vo=dao.findByPrimaryKey(vo);
//			if(vo!=null)
//				xmlContent=vo.getString("str_value");
//		}
//		catch(Exception ex)
//		{
//			
//			ex.printStackTrace();
//		}		
//		SetInterfacesXml setInterfacesXml=new SetInterfacesXml(xmlContent);
//		String str_path="/param/user/rec";
//		LazyDynaBean bean=setInterfacesXml.getHrService(str_path);
//		ArrayList list=getFitemList(bean);
//		this.getFormHM().put("fielditemlist", list);
//		GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
//		ArrayList chklist = gf.getFieldBySetNameTrans("A01",this.userView);
//		String chitemid=setInterfacesXml.getHrServiceParam("/param/user","keyfield");
//		this.getFormHM().put("chitemid", chitemid);
//		this.getFormHM().put("chklist",chklist);
//		String impmode=setInterfacesXml.getHrServiceParam("/param","impmode");
//		String expmode=setInterfacesXml.getHrServiceParam("/param","expmode");
//		this.getFormHM().put("impmode", impmode);
//		this.getFormHM().put("expmode", expmode);
	}
//	private void setFielditem(FieldItem fielditem,LazyDynaBean bean)
//	{
//		String itemid=fielditem.getItemid();
//		LazyDynaBean bean1=(LazyDynaBean)bean.get(itemid);
//		if(bean1!=null)
//		{
//			String dest=(String)bean1.get("dest");
//			String codesetid=(String)bean1.get("codesetid");
//			if(dest!=null&&dest.length()>0)
//			{
//				FieldItem dfielditem=DataDictionary.getFieldItem(dest);
//				fielditem.setViewvalue(dfielditem.getItemdesc());
//				fielditem.setValue(dest);
//				fielditem.setCodesetid(codesetid);
//			}
//		}
//		
//	}
//    private ArrayList getFitemList(LazyDynaBean bean)
//    {
//    	ArrayList list=new ArrayList();
//    	FieldItem fielditem=new FieldItem();
//    	/*fielditem.setItemdesc("人员关联指标");
//    	fielditem.setItemid("userId");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);*/
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("用户密码");
//    	fielditem.setItemid("userPassword");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("所在组织id");
//    	fielditem.setItemid("orgId");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("用户的中文名称");
//    	fielditem.setItemid("userName");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("员工号");
//    	fielditem.setItemid("employeeNumber");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("入职时间");
//    	fielditem.setItemid("onboardTime");
//    	fielditem.setItemtype("D");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("职务");
//    	fielditem.setItemid("title");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("性别");
//    	fielditem.setItemid("sex");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("民族");
//    	fielditem.setItemid("nation");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("籍贯");
//    	fielditem.setItemid("nativePlace");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("毕业学校");
//    	fielditem.setItemid("graduationSchool");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("学历");
//    	fielditem.setItemid("degree");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("生日");
//    	fielditem.setItemid("birthday");
//    	fielditem.setItemtype("D");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("身份证号");
//    	fielditem.setItemid("IDCard");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("电话号");
//    	fielditem.setItemid("tel");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("手机号");
//    	fielditem.setItemid("mobile");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("传真");
//    	fielditem.setItemid("fax");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("邮件");
//    	fielditem.setItemid("mail");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
//    	return list;
//    }
}

