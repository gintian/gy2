package com.hjsj.hrms.transaction.sys.options.interfaces;

import com.hjsj.hrms.businessobject.sys.options.interfaces.SetInterfacesXml;
import com.hjsj.hrms.transaction.param.GetFieldBySetNameTrans;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;

/**
 * 平台数据接口展现
 * @author Owner
 *
 */
public class SetInterfacesTrans extends IBusiness {

	public void execute() throws GeneralException {
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
			//System.out.println(xmlContent);
		}
		catch(Exception ex)
		{
			
			ex.printStackTrace();
		}		
		SetInterfacesXml setInterfacesXml=new SetInterfacesXml(xmlContent);
		//人员
		String str_path="/param/user/rec";
		LazyDynaBean bean=setInterfacesXml.getHrService(str_path);
		ArrayList list=getFitemList(bean);
		this.getFormHM().put("fielditemlist", list);
		GetFieldBySetNameTrans gf = new GetFieldBySetNameTrans();
		ArrayList chklist = gf.getFieldBySetNameTrans("A01",this.userView);
		String chitemid=setInterfacesXml.getHrServiceParam("/param/user","keyfield");
		this.getFormHM().put("chitemid", chitemid);
		this.getFormHM().put("chklist",chklist);
		//组织机构
		String org_path="/param/org/rec";
		LazyDynaBean orgbean=setInterfacesXml.getHrService(org_path);
		ArrayList orglist=getOrgFitemList(orgbean);
		this.getFormHM().put("orgitemlist", orglist);  //页面指标
		//ArrayList orgchklist = gf.getFieldBySetNameTrans("B01",this.userView); //关联
//		String orgchitemid=setInterfacesXml.getHrServiceParam("/param/org","keyfield"); //组织机构属性对应
		//this.getFormHM().put("orgchklist",orgchklist);
//		this.getFormHM().put("orgchitemid", orgchitemid);
		
		String impmode=setInterfacesXml.getHrServiceParam("/param","impmode");//导入 =0|1(不进行代码翻译|进行代码翻译)
		String expmode=setInterfacesXml.getHrServiceParam("/param","expmode");//导出 =0|1(不进行代码翻译|进行代码翻译)
		String marker=setInterfacesXml.getHrServiceParam("/param","marker");// 返回唯一标示 1：返回用户 0：返回HR平台
		
		this.getFormHM().put("impmode", impmode);
		this.getFormHM().put("expmode", expmode);
		this.getFormHM().put("marker", marker);
		
	}
	private void setFielditem(FieldItem fielditem,LazyDynaBean bean)throws GeneralException
	{
		String itemid=fielditem.getItemid();
		LazyDynaBean bean1=(LazyDynaBean)bean.get(itemid);		
		if(bean1!=null)
		{
			String dest=(String)bean1.get("dest");
			String codesetid=(String)bean1.get("codesetid");
			if(dest!=null&&dest.length()>0)
			{
				FieldItem dfielditem=DataDictionary.getFieldItem(dest);
				if(dfielditem==null)
					throw GeneralExceptionHandler.Handle(new GeneralException("请刷新数据字典！"));
				fielditem.setViewvalue(dfielditem.getItemdesc());
				fielditem.setValue(dest);
				fielditem.setCodesetid(codesetid);
			}
		}
		
	}
	//人员
    private ArrayList getFitemList(LazyDynaBean bean)throws GeneralException
    {
    	ArrayList list=new ArrayList();
    	FieldItem fielditem=new FieldItem();
    	/*fielditem.setItemdesc("人员关联指标");
    	fielditem.setItemid("userId");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);*/
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("用户密码");
    	fielditem.setItemid("userPassword");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("所在组织id");
    	fielditem.setItemid("orgId");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("用户的中文名称");
    	fielditem.setItemid("userName");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("员工号");
    	fielditem.setItemid("employeeNumber");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("入职时间");
    	fielditem.setItemid("onboardTime");
    	fielditem.setItemtype("D");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("职务");
    	fielditem.setItemid("title");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("性别");
    	fielditem.setItemid("sex");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("民族");
    	fielditem.setItemid("nation");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("籍贯");
    	fielditem.setItemid("nativePlace");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("毕业学校");
    	fielditem.setItemid("graduationSchool");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("学历");
    	fielditem.setItemid("degree");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("生日");
    	fielditem.setItemid("birthday");
    	fielditem.setItemtype("D");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("身份证号");
    	fielditem.setItemid("IDCard");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("电话号");
    	fielditem.setItemid("tel");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("手机号");
    	fielditem.setItemid("mobile");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("传真");
    	fielditem.setItemid("fax");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("邮件");
    	fielditem.setItemid("mail");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	return list;
    }
    //组织结构
    public ArrayList getOrgFitemList(LazyDynaBean bean)throws GeneralException
	{
		ArrayList list=new ArrayList();
		FieldItem fielditem=new FieldItem();
//		fielditem=new FieldItem();
//    	fielditem.setItemdesc("组织机构Id");
//    	fielditem.setItemid("orgId");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
    	
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("直属上级机构ID");
//    	fielditem.setItemid("parent");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("机构简称");
    	fielditem.setItemid("displayName");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("机构全称");
    	fielditem.setItemid("name");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("行政级别");
    	fielditem.setItemid("officialLevel");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("优先级 从0开始大数在前面");
//    	fielditem.setItemid("priority");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("联系人");
    	fielditem.setItemid("linkMan");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("电话");
    	fielditem.setItemid("tel");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("传真");
    	fielditem.setItemid("fax");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
    	fielditem=new FieldItem();
    	fielditem.setItemdesc("邮件");
    	fielditem.setItemid("mail");
    	fielditem.setItemtype("A");
    	setFielditem(fielditem,bean);
    	list.add(fielditem);
    	
//    	fielditem=new FieldItem();
//    	fielditem.setItemdesc("类型");
//    	fielditem.setItemid("style");
//    	fielditem.setItemtype("A");
//    	setFielditem(fielditem,bean);
//    	list.add(fielditem);
		return list;
	}
}
