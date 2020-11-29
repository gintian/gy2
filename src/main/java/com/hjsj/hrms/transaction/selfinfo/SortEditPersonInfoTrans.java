/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;


import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.businessobject.sys.options.otherparam.OtherParam;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * @author wlh
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SortEditPersonInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
    	try {
    	HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
    	String flag=(String)hm.get("flag");
    	String typeoforg = (String)hm.get("orgtype");
    	if("vorg".equalsIgnoreCase(typeoforg)){                     //虚拟机构不能新增人员，提示前台    2013-10-17  赵国栋
			try {
				throw new Exception(ResourceFactory.getProperty("selfinfo.orgtype.vorg.noaddpeople"));
			} catch (Exception e) {
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
    	}
    	
    	String setprv="";
    	boolean deptvalue=false;
    	boolean posvalue=false;
    	String rownums="";
    	Sys_Oth_Parameter othparam=new Sys_Oth_Parameter(this.getFrameconn());
    	rownums=othparam.getValue(Sys_Oth_Parameter.EDIT_COLUMNS);
    	String units=othparam.getValue(Sys_Oth_Parameter.UNITS);
		String place=othparam.getValue(Sys_Oth_Parameter.PLACE);
		String infosort=othparam.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
		//tianye add 读取分页参数
		String num_per_page=othparam.getValue(Sys_Oth_Parameter.NUM_PER_PAGE);
		if(num_per_page==null|| "".equals(num_per_page)){
			num_per_page="21";
		}
		this.getFormHM().put("num_per_page",num_per_page);
		//end
		//身份证指标
		String chk = othparam.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name"); 
		//身份证验证是否启用 
        String chkvalid = othparam.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
        if("1".equals(chkvalid))
            this.getFormHM().put("idcardfield", chk);
		
		String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");
		InfoUtils infoUtils=new InfoUtils();
		
		if(!("infoself".equalsIgnoreCase(flag) && userView.getStatus()!=4))
		{
			String userbase=(String)this.getFormHM().get("userbase");//人员库
			String setname=(String)this.getFormHM().get("setname");  //获得入录子集的名称
			String tablename=userbase + setname;                     //表的名称
			String A0100=(String)this.getFormHM().get("a0100");      //获得人员ID
			String I9999=(String)this.getFormHM().get("i9999");
			String actiontype=(String)this.getFormHM().get("actiontype");
			String personsort=(String)this.getFormHM().get("personsort");
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			if (hm != null && hm.containsKey("insert")) {
				String tempi9 = (String) hm.get("insert");
				I9999 = tempi9;
				hm.remove("insert");
				hm.put("insert1", I9999);
			} else {
				hm.remove("insert1");
			}
			
			if(("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100)) && !"infoself".equalsIgnoreCase(flag))
				actiontype="new";
			else if(A0100==null||A0100.length()==0){
				throw new GeneralException("","无人员信息!","","");
			}else if(!("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))){
				if("infoself".equalsIgnoreCase(flag)){
				    A0100=userView.getUserId();
				}else{
					CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
					userbase=checkPrivSafeBo.checkDb(userbase);
					A0100=checkPrivSafeBo.checkA0100("", userbase, A0100, "");
					setname=checkPrivSafeBo.checkFieldSet(userbase, setname, A0100, Constant.EMPLOY_FIELD_SET, dao);
				}
			}else if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			    A0100=userView.getUserId();
			if("new".equals(actiontype) && "A01".equals(setname))
				A0100="A0100";
			
			String sub_type=infoUtils.getOneselfFenleiType(userbase, A0100, fenlei_priv, dao);//人员分类
			List infoFieldList=null;
			List infoSetList=null;
			if("infoself".equalsIgnoreCase(flag)){
			  infoFieldList=userView.getPrivFieldList(setname,0);   //获得当前子集的所有属性
			  infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET,0);   //获得所有权限的子集
			}
			else
			{
				if(sub_type!=null&&sub_type.length()>0)
				{
					//得到分类授权子集
					infoFieldList=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type);
					infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET);
					if(infoFieldList==null||infoFieldList.size()<=0)//如果分类中得不到指标则用默认权限的
						infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
					if(infoSetList==null||infoSetList.size()<=0)
						infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
						
				}else
				{
					infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
					infoSetList=userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);   //获得所有权限的子集
				}			  
			}
			
			//子集指标分类 过滤  如果选择的是的全部的时候，查一下人员具体分类  guodd 14-10-30 
			boolean isExistData=false;
			String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
			StringBuffer rstrsql=new StringBuffer();
			rstrsql.append("select * from ");
			rstrsql.append(tablename);
			rstrsql.append(" where A0100='");
			rstrsql.append(A0100);
			rstrsql.append("'");
			List rs = ExecuteSQL.executeMyQuery(rstrsql.toString(),this.getFrameconn(),true);
			isExistData=!rs.isEmpty();
			LazyDynaBean rec = null;
			if(isExistData){
				rec = (LazyDynaBean)rs.get(0);
				if( (personsort==null || "all".equalsIgnoreCase(personsort)) && personsortfield!=null && !"infoself".equalsIgnoreCase(flag) )
				     personsort=rec.get(personsortfield.toLowerCase())!=null?rec.get(personsortfield.toLowerCase()).toString():null;
			}
			// 子集指标分类 过滤end
			infoSetList=new SortFilter().getSortPersonFilterSet(infoSetList,personsort,this.getFrameconn());
			infoFieldList=new SortFilter().getSortPersonFilterField(infoFieldList,personsort,this.getFrameconn());			
			infoSetList=new SortFilter().getPersonDBFilterSet(infoSetList, userbase,this.getFrameconn());
			infoFieldList=new SortFilter().getPersonDBFilterField(infoFieldList, userbase,this.getFrameconn());
			
			List infoFieldViewList=new ArrayList();                  //保存处理后的属性
			ArrayList savePos=null;
			//List rs=null;
			try
			{
				String statevalue=ResourceFactory.getProperty("info.appleal.state0");
				//4626 记录录入：不给用户授权任何子集权限，点击修改，系统报空指针错误   jingq upd 2014.10.23
				if(infoFieldList!=null&&!infoFieldList.isEmpty())
			    {
					
					String belong="";//设置的所属党团工会指标 xuj2010-2-25
					if("new".equals(actiontype)){
						String returnvalue = (String)this.getFormHM().get("returnvalue");
						if("64".equals(returnvalue)){
							ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
							String belongparty = xml.getValue("belongparty");
							belongparty = belongparty!=null&&belongparty.length()>0?belongparty:"";
							belong=belongparty;
						}else if("65".equals(returnvalue)){
							ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
							String belongmember = xml.getValue("belongmember");
							belongmember = belongmember!=null&&belongmember.length()>0?belongmember:"";
							belong=belongmember;
						}else if("66".equals(returnvalue)){
							ConstantXml xml = new ConstantXml(this.frameconn,"PARTY_PARAM");
							String belongmeet = xml.getValue("belongmeet");
							belongmeet = belongmeet!=null&&belongmeet.length()>0?belongmeet:"";
							belong=belongmeet;
						}
					}
					for(int i=0;i<infoFieldList.size();i++)                            //字段的集合
					{
						FieldItem fieldItem=(FieldItem)infoFieldList.get(i);							
						if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
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
							fieldItemView.setItemid(fieldItem.getItemid());
							fieldItemView.setItemlength(fieldItem.getItemlength());
							fieldItemView.setItemtype(fieldItem.getItemtype());
							fieldItemView.setModuleflag(fieldItem.getModuleflag());
							fieldItemView.setState(fieldItem.getState());
							fieldItemView.setUseflag(fieldItem.getUseflag());
							if(SystemConfig.getPropertyValue("staffid_set")!=null)  //汉口银行，工号自动生成，所以此指标置灰不可用  2013-11-30  dengcan
							{
								String staffid_set=SystemConfig.getPropertyValue("staffid_set");
								if(staffid_set.length()>0)
								{
									String[] temps=staffid_set.split(":");
									String temp=temps[1].split("_")[0];
									if(temp!=null&&temp.trim().equalsIgnoreCase(fieldItem.getItemid()))
										fieldItemView.setPriv_status(1);
									else
										fieldItemView.setPriv_status(fieldItem.getPriv_status());
								}
								else
									fieldItemView.setPriv_status(fieldItem.getPriv_status());
							}
							else
								fieldItemView.setPriv_status(fieldItem.getPriv_status());
							fieldItemView.setSequencename(fieldItem.getSequencename());
							fieldItemView.setSequenceable(fieldItem.isSequenceable());
							fieldItemView.setVisible(fieldItem.isVisible());
				            //在struts用来表示换行的变量
							fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
							if("update".equals(actiontype) && isExistData)
							{
								if(i==0)
								{
									String state= rec.get("state")!=null?rec.get("state").toString():"";
									if(state==null || "0".equals(state))
									{
										statevalue=ResourceFactory.getProperty("info.appleal.state0");
										statevalue=ResourceFactory.getProperty("info.appleal.state0");
									}else if("1".equals(state))
									{	
										statevalue=ResourceFactory.getProperty("info.appleal.state1");
										statevalue=ResourceFactory.getProperty("info.appleal.state1");
									}else if("2".equals(state))
									{	
										statevalue=ResourceFactory.getProperty("info.appleal.state2");
										statevalue=ResourceFactory.getProperty("info.appleal.state2");
									}else if("3".equals(state))
									{
										statevalue=ResourceFactory.getProperty("info.appleal.state3");
										statevalue=ResourceFactory.getProperty("info.appleal.state3");
								    }
								}
							   
								if(isExistData)
								{
									if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
									{
										if(!"0".equals(fieldItem.getCodesetid()))
										{
											String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
											//System.out.println("itemida" + fieldItem.getItemid() + "typea" + fieldItem.getItemtype());
											if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0){
											  // 原来代码仅一句fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),codevalue)!=null?AdminCode.getCode(fieldItem.getCodesetid(),codevalue).getCodename():"");
											//tianye update start
											//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
											String name = "";
											if(!"e0122".equalsIgnoreCase(fieldItem.getItemid())){
												CodeItem codeItem = InfoUtils.getUMOrUN(fieldItem.getCodesetid(),codevalue);
												name = (codeItem!=null ? codeItem.getCodename(): "");
											}else{
												name = AdminCode.getCodeName(fieldItem.getCodesetid(), codevalue);
											}
											fieldItemView.setViewvalue(name);
										//end
										    }else
										       fieldItemView.setViewvalue("");
											if("b0110".equalsIgnoreCase(fieldItem.getItemid()))
											{
												this.getFormHM().put("orgparentcode",userView.isSuper_admin()?userView
														.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
												if(codevalue!=null && codevalue.trim().length()>0)
												{
													if(this.compareMpriv(codevalue, userView.isSuper_admin()?userView
															.getManagePrivCodeValue():  userView.getUnitIdByBusi("4")))
													{
													  this.getFormHM().put("deptparentcode",codevalue);
													  this.getFormHM().put("posparentcode",codevalue);
													}else
													{
														 this.getFormHM().put("deptparentcode",userView.isSuper_admin()?userView
																	.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
														 this.getFormHM().put("posparentcode",userView.isSuper_admin()?userView
																	.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
													}
													deptvalue=true;
												}
												if(units!=null&& "1".equals(units))//参数设置单位必填
												{
													fieldItem.setFillable(true);
													fieldItemView.setFillable(true);
												}
											}
											 if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
											 {
											 	 if(deptvalue==false)
												   this.getFormHM().put("deptparentcode",userView.getUnitIdByBusi("4"));
												 if(codevalue!=null && codevalue.trim().length()>0)
												 {
													 if(this.compareMpriv(codevalue, userView.isSuper_admin()?userView
																.getManagePrivCodeValue():  userView.getUnitIdByBusi("4")))
												     {
													    this.getFormHM().put("posparentcode",codevalue);
												     }else
												     {
												    	this.getFormHM().put("posparentcode",userView.isSuper_admin()?userView
																.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
												     }
													posvalue=true;
												 }
											 }
											if("e01a1".equalsIgnoreCase(fieldItem.getItemid()))
											{
													if(place!=null&& "1".equals(place))//参数设置单位必填
													{
														fieldItem.setFillable(true);
														fieldItemView.setFillable(true);
													}
													
											}
											if(deptvalue==false && posvalue==false)
											{
											  if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
												 this.getFormHM().put("deptparentcode",userView.isSuper_admin()?userView
															.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											  if("e01a1".equalsIgnoreCase(fieldItem.getItemid()))
												 this.getFormHM().put("posparentcode",userView.isSuper_admin()?userView
															.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											}else if(deptvalue==false)
											{
												if("e0122".equalsIgnoreCase(fieldItem.getItemid()))
													 this.getFormHM().put("deptparentcode",userView.isSuper_admin()?userView
																.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											}
											
										}
										else
										{
											fieldItemView.setViewvalue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");
										}
										fieldItemView.setValue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");						
									}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
									{
	                                   int itemlen =  fieldItem.getItemlength();
	                                    String value =rec.get(fieldItem.getItemid()).toString();
	                                    if ((value !=null) && (value.length()>=itemlen)){
	                                        fieldItemView.setViewvalue(new FormatValue().format(
	                                                        fieldItem,value));  
	                                        fieldItemView.setValue(new FormatValue().format(
	                                                        fieldItem,value)); 
	                                    }
	                                    else {                                      
	                                        fieldItemView.setViewvalue("");
	                                        fieldItemView.setValue("");
	                                        fieldItemView.setOldvalue("");  
	                                    }
									}
									else                                                          //数值类型的有待格式化处理
									{
										fieldItemView.setValue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"",fieldItem.getDecimalwidth()));						
									}
								}
					
							}else if("new".equalsIgnoreCase(actiontype))
							{
								String kind=(String)this.getFormHM().get("kind");
								String code=(String)this.getFormHM().get("code");
								if(savePos ==null)
								{
									if (kind == null||"1|".equalsIgnoreCase(code)) {//点击查询后 code 被置成1|了
										String codes =userView.getUnitIdByBusi("4");//tian ye 
										String fristCodeType = "";
										String fristCodeValue = "";
										String[] tempCode = codes.split("`");
										if(!"".equals(codes.trim())){
											if (tempCode.length!= 0)
											fristCodeType = tempCode[0].substring(0,2);
											fristCodeValue = tempCode[0].substring(2);
										}
										savePos = getMangerStationPos(fristCodeValue,fristCodeType );
									}else
									{
										savePos=getStationPos(code,kind);
									}
								}
								
								this.getFormHM().put("orgparentcode",userView.isSuper_admin()?userView
										.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
								this.getFormHM().put("deptparentcode",userView.isSuper_admin()?userView
										.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
								this.getFormHM().put("posparentcode",userView.isSuper_admin()?userView
										.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
								if("b0110".equalsIgnoreCase(fieldItem.getItemid())&&belong.length()==0)
								{
									for(int n=0;n<savePos.size();n++)
									{
										StationPosView posview=(StationPosView)savePos.get(n);
										if("b0110".equalsIgnoreCase(posview.getItem()))
										{
											fieldItemView.setValue(posview.getItemvalue());
											fieldItemView.setViewvalue(posview.getItemviewvalue());
											this.getFormHM().put("orgparentcode",userView.isSuper_admin()?userView
													.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											this.getFormHM().put("deptparentcode",userView.isSuper_admin()?userView
													.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											this.getFormHM().put("posparentcode",userView.isSuper_admin()?userView
													.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
										}
									}
									if(units!=null&& "1".equals(units))//参数设置单位必填
									{
										fieldItem.setFillable(true);
										fieldItemView.setFillable(true);
									}
									
								}else if("e0122".equalsIgnoreCase(fieldItem.getItemid())&&belong.length()==0)
								{
									for(int n=0;n<savePos.size();n++)
									{
										StationPosView posview=(StationPosView)savePos.get(n);
										if("e0122".equalsIgnoreCase(posview.getItem()))
										{
											fieldItemView.setValue(posview.getItemvalue());
											fieldItemView.setViewvalue(posview.getItemviewvalue());
										}
									}
									
								}else if("e01a1".equalsIgnoreCase(fieldItem.getItemid())&&belong.length()==0)
								{
									for(int n=0;n<savePos.size();n++)
									{
										StationPosView posview=(StationPosView)savePos.get(n);
										if("e01a1".equalsIgnoreCase(posview.getItem()))
										{
											fieldItemView.setValue(posview.getItemvalue());
											fieldItemView.setViewvalue(posview.getItemviewvalue());
										}
									}
									if(place!=null&& "1".equals(place))//参数设置单位必填
									{
											fieldItem.setFillable(true);
											fieldItemView.setFillable(true);
									}									
								}else if(belong.equalsIgnoreCase(fieldItem.getItemid())){//为所属党团工会指标自动赋值  xuj2010-2-25
									String a_code=(String)hm.get("a_code");
									if(a_code!=null&&a_code.length()>2){
										fieldItemView.setValue(a_code.substring(2,a_code.length()));
										fieldItemView.setViewvalue(AdminCode.getCodeName(a_code.substring(0,2), a_code.substring(2)));
									}
								}
							}
							else
							{
								fieldItemView.setValue("");
							}
							fieldItemView.setFillable(fieldItem.isFillable());
							infoFieldViewList.add(fieldItemView);
						}
					}
				}
				/**chenmengqing added 20051017*/
				setprv=getEditSetPriv(infoSetList,infoFieldList,setname);	
				
			}catch(Exception e){
			   e.printStackTrace();
			   throw GeneralExceptionHandler.Handle(e);
			}finally{					
			   this.getFormHM().put("a0100",A0100);
			   this.getFormHM().put("i9999",I9999);
			   this.getFormHM().put("a0000", (String)hm.get("i9999"));
			   this.getFormHM().put("actiontype",actiontype);
			   this.getFormHM().put("setprv",setprv);
		       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
		       this.getFormHM().put("infosetlist",infoSetList);
		       this.getFormHM().put("rownums", rownums);
		       String writable=(String) hm.get("writeable");
		       hm.remove("writeable");
		       this.getFormHM().put("writeable",writable);
		       this.getFormHM().put("infosort", infosort);
		       
		       if("new".equalsIgnoreCase(actiontype)) {
		    	   this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.insert"));
		       } else {
		    	   this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.update"));
		       }
		       
	       }
			if(infosort!=null&& "1".equals(infosort))
			{
				infoSort(infoFieldViewList,infoSetList, setname);
			}else
			{
				this.getFormHM().put("mainsort", "");
				this.getFormHM().put("infosort", "");
			}		
			/********兼职********/
			ArrayList list = new ArrayList();
			list.add("flag");
			list.add("unit");
			list.add("setid");
			list.add("appoint");
			HashMap map = othparam.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
			if(map!=null&& map.size()!=0){
	    		String part_flag="";
				if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
					part_flag=(String)map.get("flag");
				String part_unit="",part_setid="";
				if(part_flag!=null&& "true".equals(part_flag))
				{
					if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
						part_unit=(String)map.get("unit");
					if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
						part_setid=(String)map.get("setid");				
				}		
				this.getFormHM().put("part_unit", part_unit.toLowerCase());
		    	this.getFormHM().put("part_setid", part_setid);
			}
	    	/********兼职结束********/
		}else
		{
			throw new GeneralException("","非自助平台用户!","","");
		}		
		String posparentcode=(String)this.getFormHM().get("posparentcode");
		String deptparentcode=(String)this.getFormHM().get("deptparentcode");
		String orgparentcode=(String)this.getFormHM().get("orgparentcode");
		if(posparentcode==null||posparentcode.length()<=0)
		   this.getFormHM().put("posparentcode", hm.get("posparentcode"));
		if(deptparentcode==null||deptparentcode.length()<=0)
		   this.getFormHM().put("deptparentcode", hm.get("deptparentcode"));
		if(orgparentcode==null||orgparentcode.length()<=0)
		  this.getFormHM().put("orgparentcode", hm.get("orgparentcode"));
			this.getFormHM().put("idType", othparam.getValue(Sys_Oth_Parameter.CHK_IdTYPE));
			this.getFormHM().put("idTypeValue", othparam.getIdTypeValue());
			OtherParam op=new OtherParam(this.getFrameconn());
			Map cardMap=op.serachAtrr("/param/formual[@name='bycardno']");
			//是否启用身份证关联结算
			String cardflag = "false";
			if(cardMap!=null&&cardMap.size()==6){
				cardflag=(String) cardMap.get("valid");
			}
			this.getFormHM().put("cardflag", cardflag);
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	
	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList,List infoFieldList,String setname)
	{
		String setpriv="0";
		boolean bflag=false;
		/**先根据子集分析*/
		for(int p=0;p<infoSetList.size();p++)
		{
			FieldSet fieldset=(FieldSet)infoSetList.get(p);
			if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
			{
				setpriv=String.valueOf(fieldset.getPriv_status());
				break;
			}
		}	
		if("2".equals(setpriv))
			return setpriv;		
		/**分析指标*/
		//4626 记录录入：不给用户授权任何子集权限，点击修改，系统报空指针错误   jingq upd 2014.10.23
		if(infoFieldList!=null){
			for(int i=0;i<infoFieldList.size();i++)                            //字段的集合
			{
			    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
			    if(fieldItem.getPriv_status()==2)
			    {
			    	bflag=true;
			    	break;
			    }
			}
		}
		if(bflag)
			return "3";
		else
			return setpriv;
	}
	
	private ArrayList getStationPos(String code,String kind)
	{
		ArrayList poslist=new ArrayList();
		String pre="";
		if("0".equals(kind))
			pre="@K";
		else if("1".equals(kind))
			pre="UM";
		else
			pre="UN";
	    Connection conn = null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
			if("UN".equals(pre))
			{
				strsql.append("select * from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");		
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				this.frowset =db.search(strsql.toString());	
				if(this.frowset.next())
				{
					StationPosView posview=new StationPosView();
					posview.setItem("b0110");
					posview.setItemvalue(this.frowset.getString("codeitemid"));
					posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
					poslist.add(posview);
				}
			}
			else
			{
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				while(!"UN".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");					
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						StationPosView posview=new StationPosView();
						pre=this.frowset.getString("codesetid");
						if("@K".equalsIgnoreCase(pre))
						{
							if(ispos==false)
							{
							  posview.setItem("e01a1");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  ispos=true;
							  poslist.add(posview);
							}
						}else if("UM".equalsIgnoreCase(pre))
						{
							if(isdep==false)
							{
							  posview.setItem("e0122");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  isdep=true;
							  poslist.add(posview);
							}
						}else if("UN".equalsIgnoreCase(pre))
						{
							if(isorg==false)
							{
	  						  posview.setItem("b0110");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  isorg=true;
							  poslist.add(posview);
							}
						}
						code=this.frowset.getString("parentid");	
					}			
				}				
			  }
			}catch (Exception sqle){
				sqle.printStackTrace();
			}				

		return poslist;
	}
	private ArrayList getMangerStationPos(String code,String pre)
	{
		ArrayList poslist=new ArrayList();
	    Connection conn = null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
			if("UN".equals(pre))
			{
				strsql.append("select * from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");		
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				this.frowset =db.search(strsql.toString());	
				if(this.frowset.next())
				{
					StationPosView posview=new StationPosView();
					posview.setItem("b0110");
					posview.setItemvalue(this.frowset.getString("codeitemid"));
					posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
					poslist.add(posview);
				}
			}
			else
			{
				conn=this.getFrameconn();
				ContentDAO db=new ContentDAO(conn);
				while(!"UN".equalsIgnoreCase(pre))
				{
					strsql.delete(0,strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");	
					this.frowset =db.search(strsql.toString());	//执行当前查询的sql语句	
					if(this.frowset.next())
					{
						StationPosView posview=new StationPosView();
						pre=this.frowset.getString("codesetid");
						if("@K".equalsIgnoreCase(pre))
						{
							if(ispos==false)
							{
							  posview.setItem("e01a1");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  ispos=true;
							  poslist.add(posview);
							}
						}else if("UM".equalsIgnoreCase(pre))
						{
							if(isdep==false)
							{
							  posview.setItem("e0122");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  isdep=true;
							  poslist.add(posview);
							}
						}else if("UN".equalsIgnoreCase(pre))
						{
							if(isorg==false)
							{
	  						  posview.setItem("b0110");
							  posview.setItemvalue(this.frowset.getString("codeitemid"));
							  posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
							  isorg=true;
							  poslist.add(posview);
							}
						}
						code=this.frowset.getString("parentid");	
					}
					else
					{
						break;
					}
				}				
			  }
			}catch (Exception sqle){
				sqle.printStackTrace();
			}				

		return poslist;
	}
	private void infoSort(List infoFieldViewList,List infoSetList, String setName)
    {
    	SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());		
		ArrayList subsort_list=infoxml.getView_tag(setName);//主集分类		
		ArrayList set_list=infoxml.getView_tag("SET_A");
		if(set_list==null||set_list.size()<=0)
		{
			this.getFormHM().put("infosort", "");
		}
		if(subsort_list==null||subsort_list.size()<=0)
		{
			this.getFormHM().put("mainsort", "");
		}else
		{
			this.getFormHM().put("mainsort", "1");
		}
		/**********主集************/
        List infolist=null;
        ArrayList fieldList = new ArrayList();
        StringBuffer fieldId_t = new StringBuffer(",");
        HashMap hm=new HashMap();
        if(subsort_list!=null&&subsort_list.size()>0)
        {
        	String sortName="";
        	for(int i=0;i<subsort_list.size();i++)
        	{
        		infolist=new ArrayList();
        		sortName=subsort_list.get(i).toString();
        		if(sortName!=null&& "未分类指标".equals(sortName))
            	{
            		StringBuffer infoFielditem=new StringBuffer();
            		String  iSortName="";
            		for(int n=0;n<subsort_list.size();n++)
            		{
            			    iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
            				infoFielditem.append(infoxml.getView_value(setName, iSortName)+",");;
            		}        		
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem.toString(),false);
            		
            	}else if(i==subsort_list.size()-1)
            	{
            		String infoFielditem=infoxml.getView_value(setName, sortName);
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
            		StringBuffer infoFielditems=new StringBuffer();
            		String  iSortName="";
            		for(int n=0;n<subsort_list.size();n++)
            		{
            			    iSortName=subsort_list.get(n)!=null?subsort_list.get(n).toString():"";
            				infoFielditems.append(infoxml.getView_value(setName, iSortName)+",");;
            		}        		
            		List no_infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditems.toString(),false);
            		for(int s=0;s<no_infolist.size();s++)
            		{
            			infolist.add(no_infolist.get(s));
            		}
            	}
        		else
            	{
            		
            		String infoFielditem=infoxml.getView_value(setName, sortName);
            		infolist=infoxml.getInfoSortFielditem(infoFieldViewList,infoFielditem,true);
            	}
        		//过滤一个指标属于多个分类的情况，当一个指标属于多个分类时，指标值在第一个加载的分类中显示
        		for(int m = 0; m < infolist.size(); m++) {
					FieldItemView fieldItemView = (FieldItemView) infolist.get(m);
					String fieldId = fieldItemView.getItemid();
					if(fieldId_t.indexOf(fieldId.toUpperCase()) > -1) {
						infolist.remove(m);
						m--;
					} else 
						fieldId_t.append(fieldId.toUpperCase() + ",");
				}
        		
        		FieldItemView fi = new FieldItemView();
				fi.setItemid("#####");
				fi.setItemdesc(sortName);
				fi.setItemlength(i);
				fi.setVisible(false);
				fieldList.add(fi);
				fieldList.addAll(infolist);
        		hm.put(sortName, infolist);
        	}
        	
        	this.getFormHM().put("infofieldlist", fieldList);
        }
        /**********子集*********/
		this.getFormHM().put("subsort_list", subsort_list);
		this.getFormHM().put("infoMap", hm);
    }
	
	private boolean compareMpriv(String codevalue,String priv){
		boolean flag = false;
		if(priv.length()>2){
			String[] tmp = priv.split("`");
			for(int i=0;i<tmp.length;i++){
				if(tmp[i].length()>=2){
					if(codevalue.compareTo(tmp[i].substring(2))>=0){
						flag= true;
						break;
					}
				}
			}
		}
		return flag;
	}
}
