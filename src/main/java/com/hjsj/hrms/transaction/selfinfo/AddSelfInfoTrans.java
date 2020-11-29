/*
 * Created on 2005-6-1
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
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
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class AddSelfInfoTrans extends IBusiness {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		try {
		HashMap hm = (HashMap) this.getFormHM().get("requestPamaHM");
		String flag = (String) hm.get("flag");
		String personsort = (String) this.getFormHM().get("personsort");
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
		ContentDAO connDao = new ContentDAO(this.getFrameconn());
		String isAble = this.isAble(this.userView.getDbname(), userView
				.getA0100(), connDao);
		this.getFormHM().put("isAble", isAble);
		/** ******兼职******* */
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");
		list.add("setid");
		list.add("appoint");
		list.add("pos");
		HashMap map = othparam.getAttributeValues(Sys_Oth_Parameter.PART_TIME, list);
		if (map != null && map.size() != 0) {
			String part_flag = "";
			if (map.get("flag") != null && ((String) map.get("flag")).trim().length() > 0)
				part_flag = (String) map.get("flag");
			String part_unit = "", part_setid = "",part_pos="";
			if (part_flag != null && "true".equalsIgnoreCase(part_flag)) {
				if (map.get("unit") != null && ((String) map.get("unit")).trim().length() > 0)
					part_unit = (String) map.get("unit");
				if (map.get("setid") != null && ((String) map.get("setid")).trim().length() > 0)
					part_setid = (String) map.get("setid");
				if (map.get("pos") != null && ((String) map.get("pos")).trim().length() > 0)
				    part_pos = (String) map.get("pos");
			}
			this.getFormHM().put("part_unit", part_unit.toLowerCase());
			this.getFormHM().put("part_pos", part_pos.toLowerCase());
			this.getFormHM().put("part_setid", part_setid);
		}
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
		/** ******兼职结束******* */
		if (!("infoself".equalsIgnoreCase(flag) && userView.getStatus() != 4)) {
			String userbase = (String) this.getFormHM().get("userbase");// 人员库
			validateNbase(userbase);

			String setname = (String) this.getFormHM().get("setname"); // 获得入录子集的名称
			validateSetName(setname);

			String A0100 = (String) this.getFormHM().get("a0100"); // 获得人员ID
			
            if(!"infoself".equals(flag))//业务平台 A0100值为“A0100”时 为新增状态 wangrd 2013-09-23
            {
                if("A0100".equals(A0100)){
                    this.getFormHM().put("actiontype", "new");
                }
            }
			String I9999 = (String) this.getFormHM().get("i9999");
			if("infoself".equals(flag))
			{
			    userbase = userView.getDbname();
			    A0100 = userView.getA0100();
			}
			
			if(!"infoself".equalsIgnoreCase(flag)&&null != A0100 && !"".equals(A0100)&&!"A0100".equalsIgnoreCase(A0100)){
				CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
				userbase = cps.checkDb(userbase);
				A0100 = cps.checkA0100("", userbase, A0100, "");
			}
			
			if("A0100".equals(A0100))
				A0100=userView.getUserId();
			else{
				if(!"infoself".equalsIgnoreCase(flag)){
					CheckPrivSafeBo checkPrivSafeBo=new CheckPrivSafeBo(this.frameconn,this.userView);
					userbase=checkPrivSafeBo.checkDb(userbase);
					A0100=checkPrivSafeBo.checkA0100("", userbase, A0100, "");
                    setname=checkPrivSafeBo.checkFieldSet(userbase, setname, A0100, Constant.EMPLOY_FIELD_SET, dao);
				}
			}
			
			String tablename = userbase + setname; // 表的名称
			searchMessage(hm, userbase, A0100, I9999, tablename, setname,personsort, flag);
		} else {
			if (this.userView.getA0100() != null
					&& this.userView.getA0100().length() > 0) {
				String userbase = this.userView.getDbname(); // 人员库
				validateNbase(userbase);

				String setname = (String) this.getFormHM().get("setname"); // 获得入录子集的名称
				validateSetName(setname);

				String tablename = userbase + setname; // 表的名称 //表的名称
				String A0100 = userView.getA0100();
				String I9999 = (String) this.getFormHM().get("i9999");
				searchMessage(hm, userbase, A0100, I9999, tablename, setname,
						personsort, flag);
			} else
				throw new GeneralException("", "非自助平台用户!", "", "");
		}
		InfoUtils InfoUtils=new InfoUtils();
		String a01desc=InfoUtils.getFieldSetCustomdesc(this.getFrameconn(), "A01");
		this.getFormHM().put("a01desc", a01desc);
		String actiontype = (String) this.getFormHM().get("actiontype");
		if("new".equalsIgnoreCase(actiontype)) {
			this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.aooroveInsert"));
		} else {
			this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.aooroveupdate"));
		}
		
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}

	/**
	 * 安全：验证子集参数是否合法
	 * @param setName 子集
	 * @throws GeneralException
	 */
	private void validateSetName(String setName) throws GeneralException {
		FieldSet fieldSet = DataDictionary.getFieldSetVo(setName);
		if (fieldSet == null) {
			throw new GeneralException("系统中不存在子集" + setName + "！");
		}
	}

	/**
	 * 安全：验证人员库参数是否合法
	 * @param nbase 人员库
	 * @throws GeneralException
	 */
	private void validateNbase(String nbase) throws GeneralException {
		if (StringUtils.isBlank(nbase)) {
			throw new GeneralException("人员库不能为空！");
		}
		boolean nbaseExist = false;
		String aNbase = "";

		ArrayList dbPreList = DataDictionary.getDbpreList();
		for (int i=0; i<dbPreList.size(); i++) {
			aNbase = (String)dbPreList.get(i);
			if (aNbase.equalsIgnoreCase(nbase)) {
				nbaseExist = true;
				break;
			}
		}
		if (!nbaseExist) {
			throw new GeneralException("系统中不存在人员库" + nbase + "！");
		}
	}

	private void searchMessage(HashMap hm, String userbase, String A0100,
			String I9999, String tablename, String setname, String personsort,
			String flag) throws GeneralException {
		List rs = null;
		List infoFieldList = null;
		List infoSetList = null;
		boolean deptvalue = false;
		boolean posvalue = false;
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		Sys_Oth_Parameter othparam = new Sys_Oth_Parameter(this.getFrameconn());
		String units = othparam.getValue(Sys_Oth_Parameter.UNITS);
		String place = othparam.getValue(Sys_Oth_Parameter.PLACE);
		String setprv = "";
		String rownums = "";
		rownums = othparam.getValue(Sys_Oth_Parameter.EDIT_COLUMNS);
		String part_unit=(String)this.getFormHM().get("part_unit");
		String part_setid=(String)this.getFormHM().get("part_setid");
		String fenlei_priv=(String)this.getFormHM().get("fenlei_priv");//人员分类设置		
		if (hm != null && hm.containsKey("insert")) {
			String tempi9 = (String) hm.get("insert");
			I9999 = tempi9;
			hm.remove("insert");
			hm.put("insert1", I9999);
		} else {
			hm.remove("insert1");
		}
		String actiontype = (String) this.getFormHM().get("actiontype");
		if (("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
				&& !"infoself".equalsIgnoreCase(flag))
			actiontype = "new";
		else if ("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
			A0100 = userView.getUserId();
		if ("new".equals(actiontype) && "A01".equals(setname))
			A0100 = "A0100";
		InfoUtils infoUtils=new InfoUtils();
		String sub_type=infoUtils.getOneselfFenleiType(userbase, A0100, fenlei_priv, dao);//人员分类
		if ("infoself".equalsIgnoreCase(flag)) {
		    /*
             * 按分类授权获取到的子集/指标没有区分是不是员工角色特征下的，因此分类授权只能在业务上实现；
             * 自助服务员工信息要使用分类授权的话，分类授权只能在员工角色特征下的角色授权，
             * 其它地方的不能进行子集/指标的分类授权否则会显示全部的分类授权的子集/指标
             */
		    if(sub_type!=null&&sub_type.length()>0) {
                //得到分类授权子集
                infoFieldList=infoUtils.getSubPrivFieldList(this.userView,setname,sub_type, 1);
                infoSetList=infoUtils.getPrivFieldSetList(this.userView,sub_type,Constant.EMPLOY_FIELD_SET, 1);
                //如果分类中得不到指标则用默认权限的
                if(infoFieldList==null||infoFieldList.size()<=0)
                    //获得当前子集的所有属性
                    infoFieldList = userView.getPrivFieldList(setname, 0);
                //获得所有权限的子集
                if(infoSetList==null||infoSetList.size()<=0)
                    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
                    
            } else {
                // 获得当前子集的所有属性
                infoFieldList = userView.getPrivFieldList(setname, 0);
                // 获得所有权限的子集
                infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET, 0);
            }
		    
		} else {
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
			    // 获得当前子集的所有属性
			    infoFieldList = userView.getPrivFieldList(setname); 
			    // 获得所有权限的子集
			    infoSetList = userView.getPrivFieldSetList(Constant.EMPLOY_FIELD_SET);
			}
		}
		
		ArrayList infoFieldViewList = new ArrayList(); // 保存处理后的属性
		ArrayList savePos = null;
		try {
		    if (!"infoself".equalsIgnoreCase(flag)) {
		        String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
		        if(StringUtils.isNotEmpty(personsortfield)) {
		        	FieldItem fi = DataDictionary.getFieldItem(personsortfield);
		        	StringBuffer sql=new StringBuffer();
		        	sql.append("select * from ");
		        	sql.append(userbase + fi.getFieldsetid());
		        	sql.append(" where A0100='");
		        	sql.append(A0100);
		        	sql.append("'");
		        	
		        	this.frowset = dao.search(sql.toString());
		        	if(this.frowset.next()){
		        		if( (personsort == null || "all".equalsIgnoreCase(personsort)) && personsortfield!=null && !"infoself".equalsIgnoreCase(flag) )
		        			personsort = this.frowset.getString(personsortfield.toLowerCase()) != null ? this.frowset.getString(personsortfield.toLowerCase()).toString() : null;
		        	}
		        }
		        
		        personsort = StringUtils.isEmpty(personsort) ? "all" : personsort;
		        infoSetList = new SortFilter().getSortPersonFilterSet(infoSetList,
		                personsort, this.getFrameconn());
		        infoFieldList = new SortFilter().getSortPersonFilterField(
		                infoFieldList, personsort, this.getFrameconn());
		        
		        infoSetList = new SortFilter().getPersonDBFilterSet(infoSetList,
		                userbase, this.getFrameconn());
		        infoFieldList = new SortFilter().getPersonDBFilterField(
		                infoFieldList, userbase, this.getFrameconn());
		    }
		    
			String statevalue = ResourceFactory.getProperty("info.appleal.state0");
			
			if (!infoFieldList.isEmpty()) {

				boolean isExistData = false;
				if ("update".equals(actiontype)) // 若是修改取其值
				{
					StringBuffer strsql = new StringBuffer();
					strsql.append("select * from ");
					strsql.append(tablename);
					strsql.append(" where A0100='");
					strsql.append(A0100);
					strsql.append("'");
					if (!"A01".substring(1, 3).equals(setname.substring(1, 3))) // 如果子集的修改则条件有I9999
					{
						strsql.append(" and I9999=");
						strsql.append(I9999);
					}
					rs = ExecuteSQL.executeMyQuery(strsql.toString(), this
							.getFrameconn(),true);
					isExistData = !rs.isEmpty();
				}
				for (int i = 0; i < infoFieldList.size(); i++) // 字段的集合
				{
					FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
					if (fieldItem.getPriv_status() != 0) // 只加在有读写权限的指标
					{
						// 为了在选择代码时方便而压入权限码开始

						// 为了在选择代码时方便而压入权限码结束
						FieldItemView fieldItemView = new FieldItemView();
						fieldItemView.setVisible(fieldItem.isVisible());
						fieldItemView.setSequencename(fieldItem.getSequencename());
						fieldItemView.setSequenceable(fieldItem.isSequenceable());
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
						// 在struts用来表示换行的变量
						fieldItemView.setRowflag(String.valueOf(infoFieldList.size() - 1));
						if ("update".equals(actiontype) && isExistData) {
							LazyDynaBean rec = (LazyDynaBean) rs.get(0);
							if (i == 0) {
								String state = rec.get("state") != null ? rec.get("state").toString() : "";
								// fieldItemSate.setValue(state);
								if (state == null || "0".equals(state)) {
									statevalue = ResourceFactory.getProperty("info.appleal.state0");
									statevalue = ResourceFactory.getProperty("info.appleal.state0");
								} else if ("1".equals(state)) {
									statevalue = ResourceFactory.getProperty("info.appleal.state1");
									statevalue = ResourceFactory.getProperty("info.appleal.state1");
								} else if ("2".equals(state)) {
									statevalue = ResourceFactory.getProperty("info.appleal.state2");
									statevalue = ResourceFactory.getProperty("info.appleal.state2");
								} else if ("3".equals(state)) {
									statevalue = ResourceFactory.getProperty("info.appleal.state3");
									statevalue = ResourceFactory.getProperty("info.appleal.state3");
								}
							}

							if (isExistData) {
								if ("A".equals(fieldItem.getItemtype())
										|| "M".equals(fieldItem.getItemtype())) {
									if (!"0".equals(fieldItem.getCodesetid())) {
										String codevalue = rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "";
										if (codevalue != null
												&& codevalue.trim().length() > 0
												&& fieldItem.getCodesetid() != null
												&& fieldItem.getCodesetid().trim().length() > 0)
										{
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
											if(setname.equals(part_setid)&& "UM".equals(fieldItem.getCodesetid()))
											{//兼职处理单位的问题
											   if(fieldItemView.getViewvalue()==null||fieldItemView.getViewvalue().length()<=0)
											   {
												   fieldItemView.setViewvalue(AdminCode.getCode("UN",
																	codevalue) != null ? AdminCode.getCode("UN",
																	codevalue).getCodename(): "");
											   }
											}
										}											
										else
											fieldItemView.setViewvalue("");
										
										if ("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
											this.getFormHM().put("orgparentcode", userView.isSuper_admin()
											        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											if (codevalue != null && codevalue.trim().length() > 0) {
												if (this.compareMpriv(codevalue, userView.isSuper_admin()
												        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"))) {
													this.getFormHM().put("deptparentcode", codevalue);
													this.getFormHM().put("posparentcode", codevalue);
												} else {
													this.getFormHM().put("deptparentcode", userView.isSuper_admin()
													        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
													this.getFormHM().put("posparentcode", userView.isSuper_admin()
													        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
												}
												deptvalue = true;
											}
											if (units != null&& "1".equals(units))// 参数设置单位必填
											{
												fieldItem.setFillable(true);
												fieldItemView.setFillable(true);
											}
										}
										
										if ("e0122".equalsIgnoreCase(fieldItem.getItemid())) {
											if (deptvalue == false)
												this.getFormHM().put("deptparentcode",userView.getManagePrivCodeValue());
											if (codevalue != null && codevalue.trim().length() > 0) {
												if (this.compareMpriv(codevalue, userView.isSuper_admin()
												        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"))) {
													this.getFormHM().put("posparentcode", codevalue);
												} else {
													this.getFormHM().put("posparentcode", userView.isSuper_admin()
													        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
												}
												posvalue = true;
											}
										}
										if ("e01a1".equalsIgnoreCase(fieldItem.getItemid())) {
											if (place != null && "1".equals(place))// 参数设置单位必填
											{
												fieldItem.setFillable(true);
												fieldItemView.setFillable(true);
											}
										}
										if (deptvalue == false && posvalue == false) {
											if ("e0122".equalsIgnoreCase(fieldItem.getItemid()))
												this.getFormHM().put("deptparentcode", userView.isSuper_admin()
												        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
											if ("e01a1".equalsIgnoreCase(fieldItem.getItemid()))
												this.getFormHM().put("posparentcode", userView.isSuper_admin()
												        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
										} else if (deptvalue == false) {
											if ("e0122".equalsIgnoreCase(fieldItem.getItemid()))
												this.getFormHM().put("deptparentcode", userView.isSuper_admin()
												        ? userView.getManagePrivCodeValue():  userView.getUnitIdByBusi("4"));
										}
									} else {
										fieldItemView.setViewvalue(rec.get(fieldItem.getItemid()) != null 
										        ? rec.get(fieldItem.getItemid()).toString() : "");
									}
									fieldItemView.setValue(rec.get(fieldItem.getItemid()) != null 
									        ? rec.get(fieldItem.getItemid()).toString() : "");
									fieldItemView.setOldvalue(rec.get(fieldItem.getItemid()) != null 
											? rec.get(fieldItem.getItemid()).toString() : "");
								} else if ("D".equals(fieldItem.getItemtype())) // 日期型有待格式化处理
								{
								    int itemlen =  fieldItem.getItemlength();
								    String value =rec.get(fieldItem.getItemid()).toString();
								    if ((value !=null) && (value.length()>=itemlen)){
					                    fieldItemView.setViewvalue(new FormatValue().format(fieldItem,value));	
					                    fieldItemView.setValue(new FormatValue().format(fieldItem,value)); 
								    }
								    else {								        
								        fieldItemView.setViewvalue("");
                                        fieldItemView.setValue("");
                                        fieldItemView.setOldvalue("");  
								    }
								} else // 数值类型的有待格式化处理
								{
									fieldItemView.setValue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null 
									        ? rec.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
									fieldItemView.setOldvalue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null 
									        ? rec.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
								}
							}

						} else if ("new".equalsIgnoreCase(actiontype)) {
							String kind = (String) this.getFormHM().get("kind");
							String code = (String) this.getFormHM().get("code");
							if (savePos == null) {
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
								} else {
									savePos = getStationPos(code, kind);
								}
							}

							this.getFormHM().put("orgparentcode", userView.isSuper_admin() ? userView.getManagePrivCodeValue()
							        : userView.getUnitIdByBusi("4"));
							this.getFormHM().put("deptparentcode", userView.isSuper_admin() ? userView.getManagePrivCodeValue()
							        : userView.getUnitIdByBusi("4"));
							this.getFormHM().put("posparentcode", userView.isSuper_admin() ? userView.getManagePrivCodeValue()
							        :  userView.getUnitIdByBusi("4"));
							if ("b0110".equalsIgnoreCase(fieldItem.getItemid())) {
								for (int n = 0; n < savePos.size(); n++) {
									StationPosView posview = (StationPosView) savePos.get(n);
									if ("b0110".equalsIgnoreCase(posview.getItem())) {
										fieldItemView.setValue(posview.getItemvalue());
										fieldItemView.setViewvalue(posview.getItemviewvalue());
										fieldItemView.setOldvalue(posview.getItemvalue());
										fieldItemView.setViewvalue(posview.getItemviewvalue());
										this.getFormHM().put("orgparentcode", userView.isSuper_admin()
										        ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
										this.getFormHM().put("deptparentcode", userView.isSuper_admin()
										        ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
										this.getFormHM().put("posparentcode", userView.isSuper_admin()
										        ? userView.getManagePrivCodeValue() : userView.getUnitIdByBusi("4"));
									}
								}
								if (units != null && "1".equals(units))// 参数设置单位必填
								{
									fieldItem.setFillable(true);
									fieldItemView.setFillable(true);
								}

							} else if ("e0122".equalsIgnoreCase(fieldItem
									.getItemid())) {
								for (int n = 0; n < savePos.size(); n++) {
									StationPosView posview = (StationPosView) savePos.get(n);
									if ("e0122".equalsIgnoreCase(posview.getItem())) {
										fieldItemView.setValue(posview.getItemvalue());
										fieldItemView.setViewvalue(posview.getItemviewvalue());
										fieldItemView.setOldvalue(posview.getItemvalue());
										fieldItemView.setViewvalue(posview.getItemviewvalue());
									}
								}
								// this.getFormHM().put("deptparentcode",userView.getManagePrivCodeValue());

							} else if ("e01a1".equalsIgnoreCase(fieldItem.getItemid())) {
								for (int n = 0; n < savePos.size(); n++) {
									StationPosView posview = (StationPosView) savePos.get(n);
									if ("e01a1".equalsIgnoreCase(posview.getItem())) {
										fieldItemView.setValue(posview.getItemvalue());
										fieldItemView.setOldvalue(posview.getItemvalue());
										fieldItemView.setViewvalue(posview.getItemviewvalue());
									}
								}
								if (place != null && "1".equals(place))// 参数设置单位必填
								{
									fieldItem.setFillable(true);
									fieldItemView.setFillable(true);
								}

							}
						} else {
							fieldItemView.setValue("");
							fieldItemView.setOldvalue("");
						}
						fieldItemView.setFillable(fieldItem.isFillable());
						infoFieldViewList.add(fieldItemView);
					}
				}
			}
			/** chenmengqing added 20051017 */
			setprv = getEditSetPriv(infoSetList, infoFieldList, setname);

		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			String inputchinfor = othparam.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
			inputchinfor = inputchinfor != null	&& inputchinfor.trim().length() > 0 ? inputchinfor : "1";
			String approveflag = othparam.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			approveflag = approveflag != null && approveflag.trim().length() > 0 ? approveflag : "1";

			String viewbutton = "1";

			if ("1".equals(inputchinfor) && "1".equals(approveflag)) {
				MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,	this.userView, userbase, A0100);
				String keyvalue = I9999;
				if ("A01".equalsIgnoreCase(setname)) {
					keyvalue = A0100;
				}
				mysel.getOtherParamList(setname, keyvalue);
				ArrayList sequenceList = mysel.getSequenceList();
				String sequence = "1";
				if (sequenceList.size() > 0) {
					sequence = (Integer.parseInt((String) sequenceList.get(sequenceList.size() - 1)) + 1) + "";
				}
				if ("update".equalsIgnoreCase(actiontype)
						&& mysel.checkUpdate(setname, keyvalue, sequence, "update")) {
					viewbutton = "0";
				}
				this.getFormHM().put("itemlist", cloneList(infoFieldViewList));
			}

			this.getFormHM().put("strsql", "");
			this.getFormHM().put("a0100", A0100);
			this.getFormHM().put("i9999", I9999);
			this.getFormHM().put("actiontype", actiontype);
			this.getFormHM().put("setprv", setprv);
			this.getFormHM().put("infofieldlist", infoFieldViewList); // 压回页面
			this.getFormHM().put("infosetlist", infoSetList);
			this.getFormHM().put("std", this.getUserState(userView, dao));
			this.getFormHM().put("rownums", rownums);
			this.getFormHM().put("inputchinfor", inputchinfor);
			this.getFormHM().put("viewbutton", viewbutton);
			this.getFormHM().put("setname", setname);
			String infosort = (String) this.getFormHM().get("infosort");
			if(StringUtils.isEmpty(infosort)) {
				infosort=othparam.getValue(Sys_Oth_Parameter.INFOSORT_BROWSE);
			}
			
			if (infosort != null && "1".equals(infosort)) {
				infoSort(infoFieldViewList, infoSetList, setname);
			} else {
				this.getFormHM().put("mainsort", "");
				this.getFormHM().put("infosort", "");
			}
		}
	}

	private String getUserState(UserView uv, ContentDAO dao) {
		String state = "";
		String dbname = uv.getDbname();
		String userid = uv.getUserId();
		if(dbname==null||dbname.trim().length()<=0)
			return "";
		String sql = "select * from " + dbname + "A01 where a0100='" + userid
				+ "'";
		if (dbname != null && dbname.length() > 0) {
			try {
				List relist = dao.searchDynaList(sql);
				if (relist.size() > 0) {
					DynaBean dynabean = (DynaBean) relist.get(0);
					state = (String) dynabean.get("state");
				}
			} catch (GeneralException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}

	public ArrayList cloneList(ArrayList list) {
		ArrayList itemlist = new ArrayList();
		FieldItem item = null;
		for (int i = 0; i < list.size(); i++) {
			FieldItem fielditem = (FieldItem) list.get(i);
			item = (FieldItem) fielditem.clone();
			itemlist.add(item);
		}
		return itemlist;
	}

	/**
	 * 求对子集修改权限，具体算法根据子集权限和指标权限进行分析．
	 * 
	 * @param infoSetList
	 * @param infoFieldSetList
	 * @param setname
	 * @return
	 */
	private String getEditSetPriv(List infoSetList, List infoFieldList,
			String setname) {
		String setpriv = "0";
		boolean bflag = false;
		/** 先根据子集分析 */
		for (int p = 0; p < infoSetList.size(); p++) {
			FieldSet fieldset = (FieldSet) infoSetList.get(p);
			if (setname.equalsIgnoreCase(fieldset.getFieldsetid())) {
				setpriv = String.valueOf(fieldset.getPriv_status());
				break;
			}
		}
		if ("2".equals(setpriv))
			return setpriv;
		/** 分析指标 */
		for (int i = 0; i < infoFieldList.size(); i++) // 字段的集合
		{
			FieldItem fieldItem = (FieldItem) infoFieldList.get(i);
			if (fieldItem.getPriv_status() == 2) {
				bflag = true;
				break;
			}
		}
		if (bflag)
			return "3";
		else
			return setpriv;
	}

	private ArrayList getStationPos(String code, String kind) {
		// System.out.println("pos" + code + kind);
		ArrayList poslist = new ArrayList();
		String pre = "";
		if ("0".equals(kind))
			pre = "@K";
		else if ("1".equals(kind))
			pre = "UM";
		else
			pre = "UN";
		Connection conn = null;
		boolean ispos = false;
		boolean isdep = false;
		boolean isorg = false;
		StringBuffer strsql = new StringBuffer();
		try {
			if ("UN".equals(pre)) {
				strsql.append("select * from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");
				conn = this.getFrameconn();
				ContentDAO db = new ContentDAO(conn);
				this.frowset = db.search(strsql.toString());
				if (this.frowset.next()) {
					StationPosView posview = new StationPosView();
					posview.setItem("b0110");
					posview.setItemvalue(this.frowset.getString("codeitemid"));
					posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
					poslist.add(posview);
				}
			} else {
				conn = this.getFrameconn();
				ContentDAO db = new ContentDAO(conn);
				while (!"UN".equalsIgnoreCase(pre)) {
					strsql.delete(0, strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");
					this.frowset = db.search(strsql.toString()); // 执行当前查询的sql语句
					if (this.frowset.next()) {
						StationPosView posview = new StationPosView();
						pre = this.frowset.getString("codesetid");
						if ("@K".equalsIgnoreCase(pre)) {
							if (ispos == false) {
								posview.setItem("e01a1");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								ispos = true;
								poslist.add(posview);
							}
						} else if ("UM".equalsIgnoreCase(pre)) {
							if (isdep == false) {
								posview.setItem("e0122");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								isdep = true;
								poslist.add(posview);
							}
						} else if ("UN".equalsIgnoreCase(pre)) {
							if (isorg == false) {
								posview.setItem("b0110");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								isorg = true;
								poslist.add(posview);
							}
						}
						code = this.frowset.getString("parentid");
					}
				}
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
		}

		return poslist;
	}

	private ArrayList getMangerStationPos(String code, String pre) {
		ArrayList poslist = new ArrayList();
		Connection conn = null;
		boolean ispos = false;
		boolean isdep = false;
		boolean isorg = false;
		StringBuffer strsql = new StringBuffer();
		try {
			if ("UN".equals(pre)) {
				strsql.append("select * from organization");
				strsql.append(" where codeitemid='");
				strsql.append(code);
				strsql.append("'");
				conn = this.getFrameconn();
				ContentDAO db = new ContentDAO(conn);
				this.frowset = db.search(strsql.toString());
				if (this.frowset.next()) {
					StationPosView posview = new StationPosView();
					posview.setItem("b0110");
					posview.setItemvalue(this.frowset.getString("codeitemid"));
					posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
					poslist.add(posview);
				}
			} else {
				conn = this.getFrameconn();
				ContentDAO db = new ContentDAO(conn);
				while (!"UN".equalsIgnoreCase(pre)) {
					strsql.delete(0, strsql.length());
					strsql.append("select * from organization");
					strsql.append(" where codeitemid='");
					strsql.append(code);
					strsql.append("'");
					this.frowset = db.search(strsql.toString()); // 执行当前查询的sql语句
					if (this.frowset.next()) {
						StationPosView posview = new StationPosView();
						pre = this.frowset.getString("codesetid");
						if ("@K".equalsIgnoreCase(pre)) {
							if (ispos == false) {
								posview.setItem("e01a1");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								ispos = true;
								poslist.add(posview);
							}
						} else if ("UM".equalsIgnoreCase(pre)) {
							if (isdep == false) {
								posview.setItem("e0122");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								isdep = true;
								poslist.add(posview);
							}
						} else if ("UN".equalsIgnoreCase(pre)) {
							if (isorg == false) {
								posview.setItem("b0110");
								posview.setItemvalue(this.frowset.getString("codeitemid"));
								posview.setItemviewvalue(this.frowset.getString("codeitemdesc"));
								isorg = true;
								poslist.add(posview);
							}
						}
						code = this.frowset.getString("parentid");
					} else {
						break;
					}
				}
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
		}

		return poslist;
	}

	private void infoSort(List infoFieldViewList, List infoSetList, String setname) {
		SaveInfo_paramXml infoxml = new SaveInfo_paramXml(this.getFrameconn());
		ArrayList subsort_list = infoxml.getView_tag(setname);// 主集分类
		ArrayList set_list = infoxml.getView_tag("SET_A");
		if (set_list == null || set_list.size() <= 0) {
			this.getFormHM().put("infosort", "");
		} else {
			this.getFormHM().put("infosort", "1");
		}
		
		if(subsort_list==null||subsort_list.size()<=0) {
			this.getFormHM().put("mainsort", "");
		} else {
			this.getFormHM().put("mainsort", "1");
		}
		
		ArrayList fieldList = new ArrayList();
		List infolist=null;
		StringBuffer fieldId_t = new StringBuffer(",");
        if(subsort_list!=null&&subsort_list.size()>0) {
			String sortName = "";
			for (int i = 0; i < subsort_list.size(); i++) {
				infolist = new ArrayList();
				sortName = subsort_list.get(i).toString();
				if (sortName != null && "未分类指标".equals(sortName)) {
					StringBuffer infoFielditem = new StringBuffer();
					String iSortName = "";
					for (int n = 0; n < subsort_list.size(); n++) {
						iSortName = subsort_list.get(n) != null ? subsort_list.get(n).toString() : "";
						infoFielditem.append(infoxml.getView_value(setname, iSortName) + ",");
					}
					infolist = infoxml.getInfoSortFielditem(infoFieldViewList, infoFielditem.toString(), false);

				} else if (i == subsort_list.size() - 1) {
					String infoFielditem = infoxml.getView_value(setname, sortName);
					infolist = infoxml.getInfoSortFielditem(infoFieldViewList, infoFielditem, true);
					StringBuffer infoFielditems = new StringBuffer();
					String iSortName = "";
					infoFielditems.append(infoFielditem + ",");
					for (int n = 0; n < subsort_list.size(); n++) {
						iSortName = subsort_list.get(n) != null ? subsort_list.get(n).toString() : "";
						infoFielditems.append(infoxml.getView_value(setname, iSortName) + ",");
					}
					List no_infolist = infoxml.getInfoSortFielditem(infoFieldViewList, infoFielditems.toString(),
							false);
					for (int s = 0; s < no_infolist.size(); s++) {
						infolist.add(no_infolist.get(s));
					}
					infolist = reOrderinfoList(infoFieldViewList, infolist);
				} else {
					String infoFielditem = infoxml.getView_value(setname, sortName);
					infolist = infoxml.getInfoSortFielditem(infoFieldViewList, infoFielditem, true);
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
			}

			this.getFormHM().put("infofieldlist", fieldList);
		}
	}

	private String isAble(String nbase, String a0100, ContentDAO dao) {
		String isAble = "1";
		String sql = "select * from t_hr_mydata_chg where nbase='" + nbase
				+ "' and a0100='" + a0100 + "' and sp_flag='02'";
		try {
			List list = dao.searchDynaList(sql);
			if (list != null && list.size() > 0) {
				isAble = "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isAble;
	}
	
	private boolean compareMpriv(String codevalue,String priv){
		boolean flag = false;
		if(priv.length()>2){
			String[] tmp = priv.split("`");
			for(int i=0;i<tmp.length;i++){
				if(tmp[i].length()>=2){
					if(codevalue.compareTo(tmp[i].substring(2))>0){
						flag= true;
						break;
					}
				}
			}
		}
		return flag;
	}
	
	private List reOrderinfoList(List infoFieldViewList,List infolist) {
    	List infoFieldList=new ArrayList();   
    	if(infolist==null||infolist.size()<=0)
			return infoFieldList;		
    	for(int i=0;i<infoFieldViewList.size();i++)
		{
			FieldItemView fieldItemView=(FieldItemView)infoFieldViewList.get(i);
			for(int r=0;r<infolist.size();r++)
			{
				FieldItemView fieldItem=(FieldItemView)infolist.get(r);
				if(fieldItem.getItemid().equals(fieldItemView.getItemid()))
				{
					infoFieldList.add(fieldItemView.clone());
				}
			}
		}
    	return infoFieldList;
    }
}
