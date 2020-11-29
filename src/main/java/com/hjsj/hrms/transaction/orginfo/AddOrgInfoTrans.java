/*
 * Created on 2005-7-6
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.gz.GzAmountXMLBo;
import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddOrgInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		List rs=null;
	    String setname="b01";
		String setprv="0";
		String fieldname="";
		String prefield="";
		String code=(String)this.getFormHM().get("code");
		if(code==null||code.length()<1){
			if(!userView.isSuper_admin()){
				code=getBusi_org_dept(this.userView);
				if(code.length()>2&&code.indexOf("`")!=-1)
					code=code.substring(2,code.indexOf("`"));
				this.getFormHM().put("code", code);
			}
		}else{
			//检查 是否越权访问 gdd 14-09-24 
			String privcode = new CheckPrivSafeBo(frameconn, userView).checkOrg(code, "4");
			if(!privcode.equals(code))
				throw GeneralExceptionHandler.Handle(new Exception("您没有访问权限！"));
		}
		
		
		
		String addType = (String)this.getFormHM().get("edittype");
		if(code!=null&&code.length()>0&&"add".equalsIgnoreCase(addType)){//添加addType条件，防止编辑时也抛异常
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frecset = dao.search("select * from organization where codeitemid='"+code+"'");
				if(!this.frecset.next()){
					throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构下不许新增组织单元，操作失败！","",""));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
		String kind=(String)this.getFormHM().get("kind");
		String edittype = (String)this.getFormHM().get("edittype");
		if(kind!=null&& "0".equals(kind))
			return;
		String I9999=(String)this.getFormHM().get("i9999");
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);   //获得所有权限的子集
		AddOrgInfo addOrgInfo=new AddOrgInfo(this.getFrameconn());
		infoSetList=addOrgInfo.filtrationFieldSet(infoSetList);
		GzAmountXMLBo gzbo = new GzAmountXMLBo(this.getFrameconn(),1);//薪资总额子集 
		String xzze_setid="";
		HashMap map =gzbo.getValuesMap();
		if(map!=null)
			xzze_setid =((String)map.get("setid"));//获得薪资总额子集
		infoSetList=addOrgInfo.filtrationFieldSet(infoSetList,xzze_setid);//过滤薪资总额子集 
		
		GzAmountXMLBo xmlbo = new GzAmountXMLBo(this.getFrameconn(),0);
		String gzxg_setid = xmlbo.getValue("base_set");//工资相关子集
		infoSetList=addOrgInfo.filtrationFieldSet(infoSetList,gzxg_setid);//过滤工资相关子集
		String baox_setid = xmlbo.getValue("ins_base_set");//保险相关子集
		infoSetList=addOrgInfo.filtrationFieldSet(infoSetList,baox_setid);//过滤保险相关子集
		ConstantXml xml = new ConstantXml(this.frameconn, "GZ_BONUS", "Params");
	    String dept_jj_setid = xml.getNodeAttributeValue("/Params/BONUS_SET","setid");//过滤部门奖金子集
	    infoSetList=addOrgInfo.filtrationFieldSet(infoSetList,dept_jj_setid);//过滤部门奖金子集
		List infoFieldViewList=new ArrayList();                  //保存处理后的属性
		String orgtype=(String)this.getFormHM().get("orgtype");
		ParameterXMLBo bo = new ParameterXMLBo(this.getFrameconn());
		String org_brief=bo.getBriefParaValue();
		String orgFieldID="";
		String contentType="";
		String type="";
		if(org_brief != null && org_brief.trim().length()>0){
			String[] org_brief_Arr = org_brief.split(",");
		    orgFieldID=org_brief_Arr[0];
		    contentType=org_brief_Arr[1];
		}
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";
		try
		{
			if(!infoFieldList.isEmpty())
		    {
				boolean isExistData=false;
				StringBuffer strsql=new StringBuffer();
				strsql.append("select * from ");
				strsql.append(setname);
				strsql.append(" where B0110='");
				strsql.append(code);
				strsql.append("'");
				if(!"01".equals(setname.substring(1,3)))   //如果子集的修改则条件有I9999
				{
					strsql.append(" and I9999=");
					strsql.append(I9999);
				}
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				if("0".equals(kind))
				{
					fieldname="e01a1";
					prefield="@K";
				}
				else if("1".equals(kind))
				{
					fieldname="e0122";
					prefield="UM";
					if("@K".equalsIgnoreCase(userView.getManagePrivCode()))
					{
					  fieldname="e01a1";
					  prefield="@K";
				    }
				}
				else if("2".equals(kind))
				{
					fieldname="b0110";
					prefield="UN";
					if("UM".equalsIgnoreCase(userView.getManagePrivCode()))
					{
						fieldname="e0122";
						prefield="UM";
					}else if("@K".equalsIgnoreCase(userView.getManagePrivCode()))
					{
						fieldname="e01a1";
						prefield="@K";
					}
				}
				FieldItemView fieldItemV=new FieldItemView();
				String unit_code_field="";
				if("add".equalsIgnoreCase(edittype)){
					getCodeitem(code,kind);
					String codeitemid = (String)this.getFormHM().get("codeitemid");
					String len = (String)this.getFormHM().get("len");
					String corcode = (String)this.getFormHM().get("corcode");
					corcode = corcode!=null?corcode:"";
					
					 fieldItemV.setItemdesc(ResourceFactory.getProperty("label.org.type_org"));
					 fieldItemV.setItemtype("A");
					 fieldItemV.setPriv_status(1);
					 fieldItemV.setItemid("codesetid");
					 fieldItemV.setItemlength(50);
					 fieldItemV.setCodesetid("###");
					 fieldItemV.setFillable(true);
					 infoFieldViewList.add(fieldItemV);
					 getCodesetidlist(code);
					 fieldItemV=new FieldItemView();
					 fieldItemV.setItemdesc(ResourceFactory.getProperty("label.org.curcode"));
					 fieldItemV.setItemtype("A");
					 fieldItemV.setItemid("codeitemid");
					 fieldItemV.setPriv_status(2);	
					 fieldItemV.setViewvalue(codeitemid);
					 fieldItemV.setValue(codeitemid);	
					 fieldItemV.setItemlength(Integer.parseInt(len));
					 fieldItemV.setCodesetid("0");
					 fieldItemV.setFillable(true);
					 infoFieldViewList.add(fieldItemV);
					 
					 fieldItemV=new FieldItemView();
					 fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.org.orgname"));
					 fieldItemV.setItemtype("A");
					 fieldItemV.setPriv_status(2);
					 fieldItemV.setItemid("codeitemdesc");
					 fieldItemV.setValue("");
					 fieldItemV.setViewvalue("");
					 fieldItemV.setCodesetid("0");
					 RecordVo rvo = new RecordVo("organization");
					  Map lenmap = rvo.getAttrLens();
					  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
					 fieldItemV.setItemlength(codeitemdesclen);
					 fieldItemV.setFillable(true);
					 infoFieldViewList.add(fieldItemV);
					
				 	RecordVo unit_vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");//单位代码
				 	int unit_code_len=30;
				 	
				 	String unit_code_desc="";
				 	FieldItem item=null;
					if(unit_vo!=null)
					{
						unit_code_field=unit_vo.getString("str_value");
					    	if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
					    	{
					    		item=DataDictionary.getFieldItem(unit_code_field); 
					    		unit_code_len=item.getItemlength();
					    		unit_code_desc=item.getItemdesc();
					    	}
					}
				 	fieldItemV=new FieldItemView();	 	
				 	if(unit_code_desc!=null&&unit_code_desc.length()>0)
				 		fieldItemV.setItemdesc(unit_code_desc);	
				 	else
					  fieldItemV.setItemdesc(ResourceFactory.getProperty("lable.statistic.companycode"));		
					fieldItemV.setItemtype("A");
				    fieldItemV.setItemid("corcode");
				    fieldItemV.setViewvalue("");
				 	fieldItemV.setValue("");//corcode	
				 	fieldItemV.setItemlength(unit_code_len);
				 	fieldItemV.setCodesetid("0");
				 	if(item!=null)
				 		fieldItemV.setFillable(item.isFillable());
				 	StringBuffer itempriv = userView.getFieldpriv();
				 	
				 	if(userView.isSuper_admin()){	//超级用户写权限
				 		fieldItemV.setPriv_status(2);
				 	}else {
				 		if(itempriv.indexOf(unit_code_field.toUpperCase()+"1")!= -1 || itempriv.indexOf(unit_code_field.toUpperCase()+"2") != -1) {
						 	if(itempriv.indexOf(unit_code_field.toUpperCase()+"2")!= -1)//写权限
						 		fieldItemV.setPriv_status(2);
						 	else//读权限
						 		fieldItemV.setPriv_status(1);
						}else {
					 		fieldItemV.setPriv_status(0);
						}
				 	}
				 	infoFieldViewList.add(fieldItemV);
				 	
				 }else{
					 fieldItemV=new FieldItemView();
					 fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.org.orgname"));
					 fieldItemV.setItemtype("A");
					 fieldItemV.setPriv_status(1);
					 fieldItemV.setCodesetid("0");
					 fieldItemV.setFillable(true);
					 fieldItemV.setItemid("codeitemdesc");
					 RecordVo rvo = new RecordVo("organization");
					  Map lenmap = rvo.getAttrLens();
					  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
					 fieldItemV.setItemlength(codeitemdesclen);
					 if(code !=null && code.trim().length()>0)
					 {
						    if(!"vorg".equals(orgtype))
						 	{
						    	String codeValue = AdminCode.getCode(prefield,code)!=null?AdminCode.getCode(prefield,code).getCodename():"";
						    	//显示名称的时候，prefield是管理范围（人员范围），code显示的是有业务范围先显示业务范围，如果人员范围是单位，业务范围是部门，这样AdminCode.getCode(prefield,code)获取的是错的
						    	//暂时这里先这样处理
						    	if("UN".equals(prefield) && StringUtils.isBlank(codeValue)) {
						    		codeValue = AdminCode.getCode("UM",code)!=null?AdminCode.getCode("UM",code).getCodename():"";
						    	}
						 		 fieldItemV.setViewvalue(codeValue);
							 	 fieldItemV.setValue(codeValue);					    	
						 	}else
						 	{
						 		
						 		HashMap hash=addOrgInfo.getVOrgMess(code);
						 		fieldItemV.setViewvalue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
							 	fieldItemV.setValue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");	
						 	}	
						    //System.out.println(fieldItemV.getViewvalue());
						    //System.out.println(fieldItemV.getValue());
					 }
					 else
					 {
					 	fieldItemV.setValue("");
					 	fieldItemV.setViewvalue("");
					 }	
					infoFieldViewList.add(fieldItemV);
					
				 	RecordVo unit_vo= ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD");//单位代码
				 	int unit_code_len=30;
				 	String unit_code_desc="";
				 	FieldItem item=null;
					if(unit_vo!=null)
					{
						unit_code_field=unit_vo.getString("str_value");
				    	if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
				    	{
				    		item=DataDictionary.getFieldItem(unit_code_field);
				    		if(null != item)
				    		{
    				    		unit_code_len=item.getItemlength();
    				    		unit_code_desc=item.getItemdesc();
				    		}
				    		
				    		fieldItemV=new FieldItemView();	 	
				    		if(unit_code_desc!=null&&unit_code_desc.length()>0)
				    		    fieldItemV.setItemdesc(unit_code_desc);	
				    		else
				    		    fieldItemV.setItemdesc(ResourceFactory.getProperty("lable.statistic.companycode"));		
				    		fieldItemV.setItemtype("A");
				    		fieldItemV.setItemid("corcode");
				    		String corcode = getCorcode(code);
				    		fieldItemV.setViewvalue(corcode);
				    		fieldItemV.setValue(corcode);	
				    		fieldItemV.setItemlength(unit_code_len);
				    		fieldItemV.setCodesetid("0");
				    		if(item!=null)
				    		    fieldItemV.setFillable(item.isFillable());
				    		
				    		StringBuffer itempriv = userView.getFieldpriv();
				    		if(itempriv.indexOf(unit_code_field.toUpperCase()+"1")!= -1)
				    		    fieldItemV.setPriv_status(1);
				    		else if(itempriv.indexOf(unit_code_field.toUpperCase()+"2") != -1)
				    		    fieldItemV.setPriv_status(2);
				    		else
				    		    fieldItemV.setPriv_status(0);
				    		if(userView.isSuper_admin()){
				    		    fieldItemV.setPriv_status(2);
				    		}
				    		infoFieldViewList.add(fieldItemV);
				    	}
					}
					
				 	this.getFormHM().put("edit_flag", "update");//修改标志
				}
				
				int rowFlag = infoFieldList.size()-2;
				if("add".equalsIgnoreCase(edittype)) {
				    rowFlag = rowFlag + 2;
				}
				
				rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
				isExistData=!rs.isEmpty();
				// System.out.println(strsql.toString());
				for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
				{
				    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
				    if(unit_code_field!=null&&unit_code_field.length()>0&&!"#".equals(unit_code_field))
			    	{
				    	if(unit_code_field.equalsIgnoreCase(fieldItem.getItemid())){
				    		continue;
				    	}
			    	}
					if(fieldItem.getPriv_status() !=0 &&/* (!("2".equals(kind) && fieldItem.getItemid().equalsIgnoreCase("b0110"))) && !("1".equals(kind) && fieldItem.getItemid().equalsIgnoreCase("b0110"))*/!"b0110".equalsIgnoreCase(fieldItem.getItemid()))                //只加在有读写权限的指标
					{
						FieldItemView fieldItemView=new FieldItemView();
						fieldItemView.setAuditingFormula(fieldItem.getAuditingFormula());
						fieldItemView.setAuditingInformation(fieldItem.getAuditingInformation());
						fieldItemView.setCodesetid(fieldItem.getCodesetid());
						fieldItemView.setDecimalwidth(fieldItem.getDecimalwidth());
						fieldItemView.setDisplayid(fieldItem.getDisplayid());
						fieldItemView.setDisplaywidth(fieldItem.getDisplaywidth());
						fieldItemView.setExplain(fieldItem.getExplain());
						fieldItemView.setFieldsetid(fieldItem.getFieldsetid());
						fieldItemView.setItemdesc(fieldItem.getItemdesc());
						fieldItemView.setItemid(fieldItem.getItemid().toUpperCase());
						fieldItemView.setItemlength(fieldItem.getItemlength());
						fieldItemView.setItemtype(fieldItem.getItemtype());
						fieldItemView.setModuleflag(fieldItem.getModuleflag());
						fieldItemView.setState(fieldItem.getState());
						fieldItemView.setUseflag(fieldItem.getUseflag());
						fieldItemView.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setFillable(fieldItem.isFillable());
						fieldItemView.setInputtype(fieldItem.getInputtype());
                        //在struts用来表示换行的变量
					    fieldItemView.setRowflag(String.valueOf(rowFlag));
                        //System.out.println(fieldItem.getItemid()+"-----"+fieldItem.getItemdesc());
						j++;
						if(isExistData&&!"add".equalsIgnoreCase(edittype))//当是新增时就不充值了
						{
							
							LazyDynaBean rec=(LazyDynaBean)rs.get(0);
							if(contentType!=null&&!"".equals(contentType)&&fieldItem.getItemid().equalsIgnoreCase(contentType))
							{
								type=(String)rec.get(fieldItem.getItemid());
								if(type==null|| "".equals(type))
									type="1";
							}
							if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
							{
								if(!"0".equals(fieldItem.getCodesetid()))
								{
									String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
									
									if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0){
										 
										//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
										String name = "";
										if(!"e0122".equalsIgnoreCase(fieldItem.getItemid())){
											CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(),codevalue);
											name = (codeItem!=null ? codeItem.getCodename(): "");
										}else{
											name = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
										}
										fieldItemView.setViewvalue(name);
								    }else
								       fieldItemView.setViewvalue("");
								}
								else
								{
									//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
									fieldItemView.setViewvalue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");
								}
								
								String value = rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
								value = value.replace("\r\n", "");
								fieldItemView.setValue(value);						
							}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
							{
								if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
								}else if(/*this.getFrowset().getString(fieldItem.getItemid())!=null &&*/ rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)

								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								}else if(/*this.getFrowset().getString(fieldItem.getItemid())!=null && */rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
								}else if(rec.get(fieldItem.getItemid()).toString().length()>=0)//上面三个都不满足，那么有值就全显示吧，时分秒都显示  zhaoxg add 2016-11-30
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString()));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString()));
								}
								else
	                            {
	                            	fieldItemView.setViewvalue("");
								    fieldItemView.setValue("");
	                            }
							}
							else                                                          //数值类型的有待格式化处理
							{
								//fieldItemView.setFieldvalue(String.valueOf(this.getFrowset().getFloat(fieldItem.getItemid())));
								fieldItemView.setValue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"",fieldItem.getDecimalwidth()));						
							}
						}
						infoFieldViewList.add(fieldItemView);
					}
				}
			}
			else {
				throw GeneralExceptionHandler.Handle(new Exception("您没有任何单位指标的权限！"));
			}
			for(int p=0;p<infoSetList.size();p++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(p);
				if("b01".equalsIgnoreCase(fieldset.getFieldsetid()))
				{
					setprv=String.valueOf(fieldset.getPriv_status());
					break;
				}
			}
			infoFieldViewList=addOrgInfo.filtrationFieldItem(infoFieldViewList);
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}finally{
			this.getFormHM().put("setprv",setprv);
		   this.getFormHM().put("code",code);
		   this.getFormHM().put("edittype",edittype);
		   this.getFormHM().put("i9999",I9999);
		   this.getFormHM().put("setname",setname);
	       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
	       this.getFormHM().put("infosetlist",infoSetList); 
	       this.getFormHM().put("kind",kind);
	       this.getFormHM().put("orgFieldID", orgFieldID.toUpperCase());
	       this.getFormHM().put("type",type);
	       this.getFormHM().put("contentField",contentType);
	       this.getFormHM().put("contentFieldValue",type);
	       
       }
	}
	private void getCodeitem(String code,String kind)throws GeneralException
    {
    	
 		String codesetid="UN";
 		if("0".equals(kind))
		{
			codesetid="@K";
		}else if("2".equals(kind))
		{
			codesetid="UN";
		}else if("1".equals(kind))
		{
			codesetid="UM";
		}else
		{
			codesetid="UN";
		}
		String first="1";
		int len=30;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where parentid='");
		strsql.append(code);
		
		strsql.append("' and codeitemid<>parentid ");
		strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where parentid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid ");
		strsql.append(" order by codeitemid desc");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try{	
			this.frowset=dao.search(strsql.toString());
			boolean b = false;
			while(this.frowset.next())
			{		
				b = true;
				first="0";
				this.getFormHM().put("first",first);
			    String chilecode=this.frowset.getString("codesetid");
			    String codeitemid=this.frowset.getString("codeitemid");
			    String corcode=this.frowset.getString("corcode");
			    int grade=this.frowset.getInt("grade");
			    this.getFormHM().put("grade",String.valueOf(grade));
			    if(chilecode!=null)
			    {
			    	//if(codesetid.equalsIgnoreCase(chilecode)){
				    	if(code!=null)
				    	{
				    		len=codeitemid.trim().length()-code.trim().length();
				    	}
				    	else
				    	{
				    		len=codeitemid.trim().length();
				    	}				    	
				    	AddOrgInfo addOrgInfo=new AddOrgInfo();
					    codeitemid=addOrgInfo.GetNext(codeitemid,code);
					    if(corcode!=null&&corcode.length()>0)
					    	corcode=addOrgInfo.GetNext(corcode,code);
					    this.getFormHM().put("codeitemid",codeitemid);
					    this.getFormHM().put("corcode",corcode);
					    break;
			    	
			    }				
		    }
			if(b){
		    }else
		    {
		    	String codeitemid="";
		    	String corcode="";
		    	first="1";
		    	strsql.delete(0,strsql.length());
				strsql.append("select grade from organization where codeitemid='");
		    	strsql.append(code);
		    	strsql.append("' and codesetid='");
		        strsql.append(codesetid);
		        strsql.append("'");
		    	this.frowset=dao.search(strsql.toString());
		    	int grade=1;
		    	if(this.frowset.next())
		    	{
		    	  grade=this.frowset.getInt("grade");
		    	  grade=grade + 1;
		    	}		    	
		    	this.getFormHM().put("grade",String.valueOf(grade));
		    
		      if(code!=null && code.trim().length()>0)
		      {
		        len=30-code.trim().length();
		    	this.getFormHM().put("first",first);
		      }
		      else
		      {
		    	  
		    	  strsql.delete(0,strsql.length());
				  strsql.append("select ");
				  strsql.append(Sql_switcher.length("codeitemid"));
				  strsql.append(" as codeitemidlen from organization where parentid=codeitemid and codesetid='");
		          strsql.append(codesetid);
		          strsql.append("'");	
		          //System.out.println(strsql.toString());
			      this.frowset=dao.search(strsql.toString()); 
			      if(this.frowset.next())
			      {
			    	  //System.out.println("sss");
			    	  len=this.frowset.getInt("codeitemidlen");
			    	  this.getFormHM().put("first","0");
			    	  //String sql="select * from organization where parentid=codeitemid and codesetid='"+codesetid+"'";
			    	  String sql="select * from organization where parentid=codeitemid ";
			    	  sql=sql+" order by codeitemid desc";
			    	  this.frowset=dao.search(sql);
			    	  if(this.frowset.next())
			    	  {
			    		  codeitemid=this.frowset.getString("codeitemid");
			    		  corcode=this.frowset.getString("corcode");
			    	  }
			      }
			      else
			      {
			    	  this.getFormHM().put("first","1");
			      }
		      }
		     
		      //AddOrgInfo addOrgInfo=new AddOrgInfo();
		     // codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
		      AddOrgInfo addOrgInfo=new AddOrgInfo();
			    codeitemid=addOrgInfo.GetNext(codeitemid,code);
			    if("".equals(codeitemid))
			    	codeitemid="01";
			    if(corcode!=null&&corcode.length()>0)
			    	corcode=addOrgInfo.GetNext(corcode,code);
		      this.getFormHM().put("codeitemid",codeitemid);
		      this.getFormHM().put("corcode",corcode);
		    }
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("len",String.valueOf(len));	
		//this.getFormHM().put("codeitemid","");
		this.getFormHM().put("codesetid",codesetid);
		this.getFormHM().put("edit_flag", "new");//新增标志
    }
	
	private String getCorcode(String codeitemid){
		String corcode = "";
		StringBuffer strsql=new StringBuffer();
		strsql.append("select corcode from organization where codeitemid='");
		strsql.append(codeitemid);
		strsql.append("' ");
		strsql.append("union select corcode from vorganization where codeitemid='");
		strsql.append(codeitemid);
		strsql.append("' ");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try {
			this.frecset = dao.search(strsql.toString());
			if(this.frecset.next()){
				corcode=this.frecset.getString("corcode");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return corcode!=null?corcode:"";
	}
	
    private void getCodesetidlist(String code) throws Exception{
        ArrayList codesetidlist = new ArrayList();
        CommonData cd = null;
        String sql = "select codesetid from vorganization where codeitemid='"+code+"' union select codesetid from organization where codeitemid='"+code+"'";
        ContentDAO dao=new ContentDAO(this.getFrameconn());
        this.frecset = dao.search(sql);
        String codesetid="UN";
        if(this.frecset.next()){
            codesetid = this.frecset.getString("codesetid");
        }
        if("UN".equalsIgnoreCase(codesetid)){
            cd = new CommonData("UN","单位");
            codesetidlist.add(cd);
        }
        cd = new CommonData("UM","部门");
        codesetidlist.add(cd);
        this.getFormHM().put("codesetidlist", codesetidlist);
    }
	private String getBusi_org_dept(UserView userView) {
		String busi = "";
				String busi_org_dept = "";
				Connection conn = null;
				RowSet rs = null;
				try {
					
					busi_org_dept = userView.getUnitIdByBusi("4");
					if (busi_org_dept.length() > 0) {
						busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
					}else{
						busi=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`
					if (rs != null)
						try {
							rs.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					if (conn != null)
						try {
							conn.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
		return busi;
	}
}
