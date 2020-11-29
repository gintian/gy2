package com.hjsj.hrms.module.gz.utils;

import com.hjsj.hrms.module.gz.salaryaccounting.salarypaying.businessobject.SalaryTemplateBo;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
/**
 * 薪资项目有关的公用业务类
 * @author zhaoxg
 * 2015-5-19
 */
public class SalarySetBo {
	/**库链接*/
	private Connection conn=null;
	/**薪资类别号*/
	private int salaryid=-1;
	/**登录用户*/
	private UserView userview;
	/**工资管理员，对共享类别有效*/
	private String   manager="";
	/**薪资控制参数*/
	private SalaryCtrlParamBo ctrlparam=null;
	public SalarySetBo(Connection conn, int salaryid,UserView userview) {
		this.conn = conn;
		this.salaryid = salaryid;
		this.userview=userview;
		ctrlparam=new SalaryCtrlParamBo(this.conn,this.salaryid);
		this.manager=ctrlparam.getValue(SalaryCtrlParamBo.SHARE_SET, "user");
	}
	/**
	 * 查询薪资类别对应的项目
	 * @return
	 */
	public ArrayList searchGzItem() 
	{
		ArrayList list=new ArrayList();
		StringBuffer strread=new StringBuffer();
		/**只读字段*/
		strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,");
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		SalaryTemplateBo bo = new SalaryTemplateBo(this.conn,this.userview);
		ArrayList setList = bo.getSalaryItemList("", salaryid+"",1);	
		FieldItem fielditem = new FieldItem();
		try
		{
			boolean isOk=false;
//			加上报审标识
			if(this.manager.length()>0)
			{
				fielditem.setItemid("sp_flag2");
				fielditem.setItemdesc("报审状态");
				fielditem.setItemtype("A");
				fielditem.setReadonly(true);
				fielditem.setItemlength(50);
				fielditem.setCodesetid("23");
				list.add(fielditem);				
				isOk=true;
			}

			/**加上审批标识*/
			fielditem = new FieldItem();
			fielditem.setItemid("sp_flag");
			fielditem.setItemdesc(ResourceFactory.getProperty("label.gz.sp"));
			fielditem.setItemtype("A");
			fielditem.setReadonly(true);
			fielditem.setItemlength(50);
			fielditem.setCodesetid("23");
			fielditem.setVisible(false);
			if(isApprove())
			{
				isOk=true;
				fielditem.setVisible(true);
			}else if(this.manager.length() == 0 && !isApprove()){
				fielditem.setVisible(true);
			}
			list.add(fielditem);
			
			if(isOk)
			{
				/**加上审批意见*/
				fielditem = new FieldItem();
				fielditem.setItemid("appprocess");
				fielditem.setItemdesc("审批意见");
				fielditem.setItemtype("M");
				fielditem.setReadonly(true);
				fielditem.setAlign("left");
				list.add(fielditem);
			}
			//追加标记
			fielditem = new FieldItem();
			fielditem.setItemid("add_flag");
			fielditem.setItemdesc("追加标记");
			fielditem.setItemtype("N");
			fielditem.setReadonly(false);
			fielditem.setAlign("left");
			list.add(fielditem);
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
			String hiddenField="a0100,a0000,";
			if(this.ctrlparam!=null){
				String a01z0Flag=this.ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有
				if(a01z0Flag==null|| "0".equals(a01z0Flag))
				{
					hiddenField+="a01z0,";
				}
			}
			for(int i=0;i<setList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean) setList.get(i);
				String itemid=(String) bean.get("itemid");
				if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
				{
					FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(_tempItem==null)
						continue;					
				}
				fielditem = new FieldItem();
				fielditem.setItemid(itemid);
				fielditem.setItemdesc((String) bean.get("itemdesc"));
				String type=(String) bean.get("itemtype");
				String codesetid=(String) bean.get("codesetid");
				fielditem.setFieldsetid((String) bean.get("fieldsetid"));
				fielditem.setCodesetid(codesetid);
				/**字段为代码型,长度定为50*/
				if("A".equals(type))
				{
					fielditem.setItemtype(type);
					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						fielditem.setItemlength(Integer.parseInt(bean.get("itemlength").toString()));						
					else
						fielditem.setItemlength(50);
					fielditem.setAlign("left");
				}
				else if("M".equals(type))
				{
					fielditem.setItemtype(type);
					fielditem.setAlign("left");					
				}
				else if("N".equals(type))
				{
					if("a00z1".equalsIgnoreCase(itemid)|| "a00z3".equalsIgnoreCase(itemid))
						fielditem.setItemlength(4);	
					else
						fielditem.setItemlength(Integer.parseInt(bean.get("itemlength").toString()));	
					int ndec=Integer.parseInt(bean.get("decwidth").toString());
					fielditem.setDecimalwidth(ndec);				
					if(ndec>0)
					{
						fielditem.setItemtype(type);
						fielditem.setFormat("####."+format.toString().substring(0,ndec));
					}
					else
					{
						fielditem.setItemtype(type);
						fielditem.setFormat("####");						
					}	
					fielditem.setAlign("right");	
				}	
				else if("D".equals(type))
				{
					fielditem.setItemlength(Integer.parseInt(bean.get("itemlength").toString()));
					fielditem.setItemtype(type);
					fielditem.setFormat("yyyy.MM.dd");
					fielditem.setAlign("left");						
				}	
				else
				{
					fielditem.setItemtype("A");
					fielditem.setItemlength(Integer.parseInt(bean.get("itemlength").toString()));
					fielditem.setAlign("left");						
				}
				/**对人员库标识，采用“@@”作为相关代码类*/
				if("nbase".equalsIgnoreCase(itemid))
				{
					fielditem.setCodesetid("@@");
					fielditem.setReadonly(true);
				}
				if(hiddenField.indexOf(itemid.toLowerCase())!=-1)
					fielditem.setVisible(false);
				fielditem.setSortable(true);
				/**设置只读字段*/
				int idx=strread.indexOf(itemid.toUpperCase());
				if(idx!=-1)
					fielditem.setReadonly(true);
				else
				{
					/**分析指标权限*/
					if("1".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						fielditem.setReadonly(true); //读权限
					}
					if(!("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						fielditem.setVisible(false);//无权限
					}	
					if("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))//由于栏目设置不能隐藏传值 暂时直接给与人员库权限 zhanghua 2017-6-2
						fielditem.setVisible(true);					
				}
				list.add(fielditem);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return list;
	}
	
	/**
	 * 查询薪资类别对应的项目
	 * @return
	 */
	public ArrayList<Field> searchGzItem2() 
	{
		ArrayList<Field> list=new ArrayList<Field>();
		StringBuffer strread=new StringBuffer();
		/**只读字段*/
		strread.append("SP_FLAG,B0110,E0122,A0101,A00Z2,A00Z3,");
		StringBuffer format=new StringBuffer();	
		format.append("###################");		
		SalaryTemplateBo bo = new SalaryTemplateBo(this.conn,this.userview);
		ArrayList setList = bo.getSalaryItemList("", salaryid+"",1);	
		Field field = null;
		try
		{
			boolean isOk=false;
//			加上报审标识
			if(this.manager.length()>0)
			{
				field=new Field("sp_flag2","报审状态");
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				
				isOk=true;
			}
			if(isApprove())
			{
				/**加上审批标识*/
				field=new Field("sp_flag",ResourceFactory.getProperty("label.gz.sp"));
				field.setLength(50);
				field.setCodesetid("23");
				field.setDatatype(DataType.STRING);
				field.setReadonly(true);
				list.add(field);
				isOk=true;
			}
			if(isOk)
			{
				/**加上审批意见*/
				field=new Field("appprocess","审批意见");
				field.setDatatype(DataType.CLOB);
				field.setAlign("left");		
				field.setReadonly(true);
				list.add(field);
			}		
			//追加标记
			field=new Field("add_flag","追加标记");
			field.setDatatype(DataType.INT);
			field.setAlign("left");
			field.setVisible(false);
			list.add(field);
			String temp_str="'B0110','A00Z1','A00Z0','A00Z2','A00Z3','A0000','A0100','NBASE','A01Z0','A0101','E0122','E01A1'";
			String hiddenField="a0100,a0000,";
			if(this.ctrlparam!=null){
				String a01z0Flag=this.ctrlparam.getValue(SalaryCtrlParamBo.A01Z0,"flag");  // 是否显示停发标识  1：有
				if(a01z0Flag==null|| "0".equals(a01z0Flag))
				{
					hiddenField+="a01z0,";
				}
			}
			for(int i=0;i<setList.size();i++)
			{
				LazyDynaBean bean = (LazyDynaBean) setList.get(i);
				String itemid=(String) bean.get("itemid");
				if(temp_str.indexOf("'"+itemid.toUpperCase()+"'")==-1)
				{
					FieldItem _tempItem=DataDictionary.getFieldItem(itemid.toLowerCase());
					if(_tempItem==null)
						continue;					
				}
				field = new Field(itemid, (String) bean.get("itemdesc"));
				String type=(String) bean.get("itemtype");
				String codesetid=(String) bean.get("codesetid");
				int itenLength = Integer.parseInt(bean.get("itemlength").toString());
				field.setCodesetid(codesetid);
				/**字段为代码型,长度定为50*/
				if("A".equals(type))
				{
					field.setDatatype(DataType.STRING);

					if(codesetid==null|| "0".equals(codesetid)|| "".equals(codesetid))
						field.setLength(itenLength);						
					else
						field.setLength(50);
					field.setAlign("left");
				}
				else if("M".equals(type))
				{
					field.setDatatype(DataType.CLOB);
					field.setAlign("left");					
				}
				else if("N".equals(type))
				{

					field.setLength(itenLength);
					int ndec = Integer.parseInt(bean.get("decwidth").toString());
					field.setDecimalDigits(ndec);					
					if(ndec>0)
					{
						field.setDatatype(DataType.FLOAT);						
						field.setFormat("####."+format.toString().substring(0,ndec));
					}
					else
					{
						field.setDatatype(DataType.INT);							
						field.setFormat("####");						
					}
					field.setAlign("right");					
				}	
				else if("D".equals(type))
				{
					field.setLength(20);
					//field.setDatatype(DataType.STRING);
					field.setDatatype(DataType.DATE);
					field.setFormat("yyyy.MM.dd");
					field.setAlign("right");						
				}	
				else
				{
					field.setDatatype(DataType.STRING);
					field.setLength(Integer.valueOf(bean.get("decwidth").toString()));
					field.setAlign("left");						
				}
				/**对人员库标识，采用“@@”作为相关代码类*/
				if("nbase".equalsIgnoreCase(itemid))
				{
					field.setCodesetid("@@");
					field.setReadonly(true);
				}
				if(hiddenField.indexOf(itemid.toLowerCase())!=-1)
					field.setVisible(false);


				field.setSortable(true);
				/**设置只读字段*/
				int idx=strread.indexOf(itemid.toUpperCase());
				if(idx!=-1)
					field.setReadonly(true);
				else
				{
					/**分析指标权限*/
					if("1".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						field.setReadonly(true); //读权限
					}
					if(!("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid)|| "nbase".equalsIgnoreCase(itemid))&& "0".equalsIgnoreCase(this.userview.analyseFieldPriv(itemid)))
					{
						field.setVisible(false);//无权限
					}	
					if("a00z0".equalsIgnoreCase(itemid)|| "a00z1".equalsIgnoreCase(itemid))
						field.setVisible(true);
					
				}
				list.add(field);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		return list;
	}
	
	/**
	 *当前薪资类别是否需要审批
	 */
	public boolean isApprove()
	{
		boolean bflag=false;
		String flow_flag=ctrlparam.getValue(SalaryCtrlParamBo.FLOW_CTRL, "flag");
		if("1".equalsIgnoreCase(flow_flag))
			bflag=true;
		return bflag;
	}
	/**
	 * 取得显示页面字段
	 * @param fieldList
	 * @return
	 */
	public ArrayList<ColumnsInfo> toColumnsInfo(ArrayList<FieldItem> fieldList){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{		
			String lockStr = ",a0101,b0110,e0122,nbase,a00z0,a00z1,sp_flag,sp_flag2,appprocess,";//默认锁列的字段
			String hiddenStr = ",a00z0,a00z1,a00z2,a00z3,";//默认隐藏的字段（栏目设置可以设置成显示）
			String iskey = ",a0100,nbase,a00z0,a00z1,a00z2,a00z3,";//没条数据的唯一id，用于跨页全选
			
			for(int i=0;i<fieldList.size();i++){
				FieldItem item = (FieldItem) fieldList.get(i);
				if("add_flag".equalsIgnoreCase(item.getItemid())){
					continue;
				}
				ColumnsInfo info = new ColumnsInfo(item);
				info.setLoadtype(ColumnsInfo.LOADTYPE_BLOCK);
				if("N".equalsIgnoreCase(item.getItemtype())) {//对数值型的做特殊处理，为空的显示为0薪资发放
					info.setDefaultValue("0");
				}else if("D".equalsIgnoreCase(item.getItemtype())) {
					info.setTextAlign("left");// 日期型居左
				}
				if("UM".equalsIgnoreCase(item.getCodesetid())) {//部门可以选择单位和部门
					info.setCodeSetValid(false);
				}
				if(!isApprove()&&StringUtils.isBlank(this.manager)&& "appprocess".equalsIgnoreCase(item.getItemid())) //20160817 dengcan  如果薪资账套不审批，则不出现审批过程列
					continue;
				if(iskey.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
					info.setKey(true);
				}
				if("a0100".equalsIgnoreCase(item.getItemid())|| "a0000".equalsIgnoreCase(item.getItemid())){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
					//info.setKey(true);
				}else if(!item.isVisible()){
					continue;//由于栏目设置不能传入没有权限的字段，所以取消 zhanghua 2017-6-2
					//不显示的数据只加载数据不显示sunjian 20170523
//					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//隐藏此字段
//					System.out.println(item.getItemdesc());
				}
				if("sp_flag".equalsIgnoreCase(item.getItemid())|| "sp_flag2".equalsIgnoreCase(item.getItemid()))
				{
					info.setOperationData(getSpOperationData(item.getItemid()));
					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD);
				}
				if("a0100".equalsIgnoreCase(item.getItemid())|| "a0000".equalsIgnoreCase(item.getItemid())){//加密
					info.setEncrypted(true);
				}
				if(lockStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){//锁列
					info.setLocked(true);
				}
				if(item.isReadonly()){
					info.setEditableValidFunc("false");//不允许编辑
				}else{
					info.setEditableValidFunc("GzGlobal.clickCell");
				}
				if(!"a00z1".equalsIgnoreCase(item.getItemid())&&!"a00z3".equalsIgnoreCase(item.getItemid())&&"N".equalsIgnoreCase(item.getItemtype()))
					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
				if("a00z0".equalsIgnoreCase(item.getItemid())){//归属日期 yyyy-MM-dd 
					info.setColumnLength(10);
					info.setAllowBlank(false);
				}
				if("a00z2".equalsIgnoreCase(item.getItemid())){//发放日期 yyyy-MM-
					info.setColumnLength(7);
				}
				if("a00z1".equalsIgnoreCase(item.getItemid())){//归属次数
					info.setAllowBlank(false);//不允许为空
				}
				if(hiddenStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
			//		info.setFromDict(false);
			//		info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);//隐藏此字段 
					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
				}
				if("SP_FLAG".equalsIgnoreCase(item.getItemid())){//审核状态列宽
					info.setColumnWidth(65);
				}else{
					info.setColumnWidth(item.getItemdesc().length()*20<100?100:item.getItemdesc().length()*20);
				}
				if(!this.userview.isSuper_admin()&&!"1".equals(this.userview.getGroupId()))
				{
					if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())))
					{
						info.setCtrltype("3");
						info.setNmodule("1");
					}
				}
				if(!"A00".equalsIgnoreCase(item.getFieldsetid()))//A00字段不存在于数据字典。
					info.setFieldsetid(item.getFieldsetid());
			//	else  20160918 dengcan
			//		info.setFieldsetid("none");
				//人员库过滤，有几个人 员库的权限，就列出几个人员库  sunjian 2017-7-15 暂时取消此功能，人员移库后有问题 zhanghua
//				if("nbase".equalsIgnoreCase(item.getItemid())) {
//					ArrayList<CommonData> operationData = fieldFilter(item,"");
//					info.setOperationData(operationData);
//				}
				list.add(info);				
				if("nbase".equalsIgnoreCase(item.getItemid())){
					FieldItem _item = (FieldItem) item.clone();
					_item.setItemid("NBASE1");
					ColumnsInfo _info = new ColumnsInfo(_item);					
					_info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
					_info.setKey(true);
					_info.setEncrypted(true);
					list.add(_info);
				}
				if("a00z0".equalsIgnoreCase(item.getItemid().toLowerCase())|| "a00z1".equalsIgnoreCase(item.getItemid().toLowerCase())){
					FieldItem _item = (FieldItem) item.clone();
					_item.setItemid(item.getItemid().toLowerCase()+"1");
					ColumnsInfo _info = new ColumnsInfo(_item);					
					_info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
					list.add(_info);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	
	/**
	 * 审批状态
	 * @return
	 */
	private ArrayList getSpOperationData(String itemid)
	{
		 ArrayList<CommonData> spOperationData = new ArrayList<CommonData>();
         CommonData cd = new CommonData();
         cd.setDataName("起草");
         cd.setDataValue("01");
         spOperationData.add(cd);
         cd = new CommonData();
         cd.setDataName("已报批");
         cd.setDataValue("02");
         spOperationData.add(cd);
         if("sp_flag".equalsIgnoreCase(itemid)){//报审没有已批状态 zhanghua 2017-8-16
        	 cd = new CommonData();
	         cd.setDataName("已批");
	         cd.setDataValue("03");
	         spOperationData.add(cd);
         }
         cd = new CommonData();
         cd.setDataName("结束");
         cd.setDataValue("06");
         spOperationData.add(cd);
         cd = new CommonData();
         cd.setDataName("驳回");
         cd.setDataValue("07");
         spOperationData.add(cd);
//         cd = new CommonData();//没见过什么叫报审状态。 zhanghua 2017-8-16
//         cd.setDataName("报审");
//         cd.setDataValue("08");
//         spOperationData.add(cd);
         return spOperationData;   
	}
	
	
	/**
	 * 取得薪资审批明细页面显示的字段
	 * @param fieldList
	 * @return
	 */
	public ArrayList<ColumnsInfo> toSpColumnsInfo(ArrayList<FieldItem> fieldList){
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{		
			String lockStr = ",a0101,b0110,e0122,sp_flag,";//默认锁列的字段
			String hiddenStr = ",a00z0,a00z1,a00z2,a00z3,";//默认隐藏的字段（栏目设置可以设置成显示）
			String iskey = ",a0100,nbase,a00z0,a00z1,a00z3,a00z2,";//没条数据的唯一id，用于跨页全选
			
			FieldItem fielditem = new FieldItem();
			fielditem.setItemid("curr_user");
			fielditem.setItemdesc("当前审批人");
			fielditem.setItemtype("A");
			fielditem.setReadonly(true);
			fielditem.setItemlength(50); 
			fieldList.add(fielditem);
			
			for(int i=0;i<fieldList.size();i++){
				FieldItem item = (FieldItem) fieldList.get(i);
				ColumnsInfo info = new ColumnsInfo(item);
				if("N".equalsIgnoreCase(item.getItemtype()))//对数值型的做特殊处理，为空的显示为0薪资审批明细
					info.setDefaultValue("0");
				
				if("UM".equalsIgnoreCase(item.getCodesetid())) {//部门可以选择单位和部门（审批界面）
					info.setCodeSetValid(false);
				}
				if(iskey.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
					info.setKey(true);
				}
				if("a0100".equalsIgnoreCase(item.getItemid())|| "add_flag".equalsIgnoreCase(item.getItemid())|| "curr_user".equalsIgnoreCase(item.getItemid())|| "a0000".equalsIgnoreCase(item.getItemid())){
					info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
					//info.setKey(true);
				}else if(!item.isVisible()){
					continue;//由于栏目设置不能传入没有权限的字段，所以取消和薪资发放一样
					//info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);//隐藏此字段
				}
				if("a0100".equalsIgnoreCase(item.getItemid())){//加密
					info.setEncrypted(true);
				}
				if(lockStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){//锁列
					info.setLocked(true);
				}
				if(item.isReadonly()){
					info.setEditableValidFunc("false");//不允许编辑
				}else{
					info.setEditableValidFunc("spCollectScope.clickCell");
				}
				if(!"a00z1".equalsIgnoreCase(item.getItemid())&&!"a00z3".equalsIgnoreCase(item.getItemid())&&"N".equalsIgnoreCase(item.getItemtype()))
					info.setSummaryType(ColumnsInfo.SUMMARYTYPE_SUM);
				if("a00z0".equalsIgnoreCase(item.getItemid())){//归属日期 yyyy-MM-dd 
					info.setColumnLength(10);
				}
				if("a00z2".equalsIgnoreCase(item.getItemid())){//发放日期 yyyy-MM-
					info.setColumnLength(7);
				}
				if(hiddenStr.indexOf(","+item.getItemid().toLowerCase()+",")!=-1){
		//			info.setLoadtype(ColumnsInfo.LOADTYPE_HIDDEN);//隐藏此字段
					info.setLoadtype(ColumnsInfo.LOADTYPE_ALWAYSLOAD_HIDE);
				}
				
				if(item!=null&&("UN".equalsIgnoreCase(item.getCodesetid())|| "@K".equalsIgnoreCase(item.getCodesetid())|| "UM".equalsIgnoreCase(item.getCodesetid())))
				{
					info.setCtrltype("0"); 
				}
				//审批 报审 状态添加下拉列表过滤条件
				if("sp_flag".equalsIgnoreCase(item.getItemid())|| "sp_flag2".equalsIgnoreCase(item.getItemid()))
				{
					info.setOperationData(getSpOperationData(item.getItemid()));
				}
				//人员库过滤，有几个人 员库的权限，就列出几个人员库  sunjian 2017-7-15 暂时取消此功能，人员移库后有问题 zhanghua
//				if("nbase".equalsIgnoreCase(item.getItemid())) {
//					ArrayList<CommonData> operationData = fieldFilter(item,"");
//					info.setOperationData(operationData);
//				}
				info.setColumnWidth(item.getItemdesc().length()*20<100?100:item.getItemdesc().length()*20);
				list.add(info);
				if("nbase".equalsIgnoreCase(item.getItemid())){
					FieldItem _item = (FieldItem) item.clone();
					_item.setItemid("nbase1");
					ColumnsInfo _info = new ColumnsInfo(_item);					
					_info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);//只加载数据
					_info.setKey(true);
					_info.setEncrypted(true);
					list.add(_info);
				}
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 挑选出已选择的数据（其中部门，单位，姓名和唯一性指标为固定指标，必须显示的）
	 * @Title: toShowColumnsInfo   
	 * @Description:    
	 * @param @param columnsList所有的
	 * @param @param checkedItemid选中的字段
	 * @param @param onlyname唯一性指标
	 * @param @return 
	 * @return ArrayList<ColumnsInfo>  
	 * @author sunjian
	 * @date 2017-05-31  
	 * @throws
	 */
	public ArrayList<LazyDynaBean> toShowColumnsInfo(ArrayList<LazyDynaBean> columnsList, String checkedItemid, String onlyname){
		ArrayList<LazyDynaBean> list = new ArrayList<LazyDynaBean>();
		//姓名，部门，单位为固定显示的指标,如果有唯一性指标，则唯一性指标也是必选的，通过设置了excel_template_limit和export_limits的时候单位，部门，姓名改为可选择的，不是必选的  sunjian 2017-06-24
		//String showColumns = "";
		for(LazyDynaBean lazyDynaBean : columnsList) {
			String itemid = lazyDynaBean.get("itemid").toString().toUpperCase();
			//是否在选中的id里面，是否是上面showColumns中的字段，或者是否是唯一性指标
			if(checkedItemid.toUpperCase().indexOf(itemid) != -1 || itemid.equalsIgnoreCase(onlyname)) {
				list.add(lazyDynaBean);
			}
		}
		return list;
	}
	
	/**
	 * 取得栏目设置过滤条件待选代码项，根据属性设置的权限进行过滤
	 * @Title: nbaseFilter   
	 * @Description:    
	 * @param @return 
	 * @return ArrayList<CommonData>    
	 * @throws
	 */
//	private ArrayList<CommonData> fieldFilter(FieldItem field,String sqlWhere) {
//		//查出该账套的所有的人员库前缀
//		SalaryTemplateBo gzbo = new SalaryTemplateBo(this.conn,this.salaryid,this.userview);
//		ArrayList<CommonData> operationData = new ArrayList<CommonData>();
//		CommonData cd = null;
//		RowSet rowSet=null;
//		try{
//			ContentDAO dao = new ContentDAO(this.conn);
//			String itemid=field.getItemid();
//			String code=field.getCodesetid();
//			if(itemid.equalsIgnoreCase("NBASE")){
//				String cbase = gzbo.getTemplatevo().getString("cbase");
//				String[] cbaseArray = cbase.split(",");
//				String content = "";
//				for(int i = 0; i < cbaseArray.length; i++) {
//					content += "'" + cbaseArray[i] + "',";
//				}
//				rowSet = dao.search("select dbname as name,upper(pre) as value from dbname where pre in (" + content.substring(0,content.length()-1) + ")");
//			}else if(StringUtils.isNotBlank(code)&&!code.equals("0")){
//				String str="select codeitemdesc as name ,codeitemid as value from codeitem where upper(codesetid)='"+code.toUpperCase()+"' "+ sqlWhere;
//				rowSet=dao.search(str);
//			}
//			while(rowSet.next()) {
//				cd = new CommonData();
//				cd.setDataName(rowSet.getString("name"));
//				cd.setDataValue(rowSet.getString("value"));
//				operationData.add(cd);
//			}
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			PubFunc.closeIoResource(rowSet);
//		}
//		return operationData;
//	}
}
