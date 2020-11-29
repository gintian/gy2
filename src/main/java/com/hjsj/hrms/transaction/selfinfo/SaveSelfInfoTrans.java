/*
 * Created on 2005-5-23
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.selfinfo;

import com.hjsj.hrms.businessobject.structuresql.MyselfDataApprove;
import com.hjsj.hrms.businessobject.structuresql.StructureExecSqlString;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.ScanFormationBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SaveSelfInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException{
	    /*【48595】员工管理模块-信息浏览-申请修改 业务用户关联了人员  保存或报批后也是按业务用户处理的，所以此段代码暂时注释
	    if (StringUtils.isEmpty(this.userView.getA0100())) 
            throw GeneralExceptionHandler.Handle(new GeneralException("","非自助用户不能使用此功能！","",""));
	    */
			DbNameBo db=new DbNameBo(this.getFrameconn());
			HashMap reqhm=(HashMap) this.getFormHM().get("requestPamaHM");
			//信息修改页面是否显示保存/报批按钮，=0：不显示；=1：显示
			String viewButton = (String) this.getFormHM().get("viewbutton");
			//修改的主集或子集的名称
			String setname = (String) this.getFormHM().get("setname");
			//此参数非空时为整体报批
			String bApproveall =(String) reqhm.get("b_approveall");
			reqhm.remove("b_approveall");
			//此参数非空时是我的变动明细页面
			String bQuery =(String) reqhm.get("b_query");
			reqhm.remove("b_query");
			/*
			 * 此处为判断在自助中的信息维护页面修改自己的信息后是否需要保存：
			 * 1.当修改的信息集为人员主集（a01表）时，修改页面不显示保存和报批按钮，显示整体报批，点击整体报批按钮，不进行保存；
			 * 2.当修改的信息集为人员主集（a01表）时，修改页面显示保存和报批按钮，直接点击整体报批按钮，先保存修改的数据然后在执行报批操作；
			 * 3.当修改的信息集为人员子集（非a01表）时，修改页面中没有整天报批按钮，所以此页面直接执行保存操作；
			 * 3.当修改的信息集为人员子集（非a01表）时，有整体报批按钮的报批页面中没有保存按钮故点击整体报批时不执行保存操作直接执行报批操作；
			 * 4.当跳转的链接为我的变动明细页面或在我的变动明细页面切换状态时，不执行保存操作。
			 * 5.当修改的信息集为多媒体子集（a00）时，由于多媒体子集变动审批不在信息审核中所以有整体报批按钮的页面点击整体报批后不执行此处的报批操作。
			 */
			if(StringUtils.isNotEmpty(bQuery) || (StringUtils.isNotEmpty(bApproveall) 
			        && ((!"1".equals(viewButton) && "A01".equalsIgnoreCase(setname)) || !"A01".equalsIgnoreCase(setname)))
			                || "A00".equalsIgnoreCase(setname))
			    return;
			
			String actiontype=(String)this.getFormHM().get("actiontype");
			String A0100=(String)this.getFormHM().get("a0100");
			String I9999=(String)this.getFormHM().get("i9999");
	        
			String chg_id = "";
			String buttonvalue=(String)reqhm.get("buttonvalue");
			buttonvalue=buttonvalue!=null&&buttonvalue.trim().length()>0?buttonvalue:"1";
			reqhm.remove("buttonvalue");
			String viewbutton="1";
			String n9999="";
			if(reqhm!=null){
				n9999=(String)reqhm.get("i9999");
			}
			
			//获得主集字段所有信息，包括值，FieldItemView
			//获得fieldList
			ArrayList infofieldlist=(ArrayList)this.getFormHM().get("infofieldlist");	         
			ArrayList fieldlist = new ArrayList();
			String userbase=(String)this.getFormHM().get("userbase");
			
	        String fflag=(String)this.getFormHM().get("flag");
	        
	        if(!"notself".equals(fflag)){
	        	userbase = this.userView.getDbname();
	        	A0100 = this.userView.getA0100();
	        }else{
	        	CheckPrivSafeBo cps = new CheckPrivSafeBo(this.frameconn, this.userView);
				userbase = cps.checkDb(userbase);
				A0100 = cps.checkA0100("",userbase , A0100, "");
	        }
            
			String tablename=userbase + setname;
			try{
			//Sql_switcher sqlswitcher=new Sql_switcher();
			if("A0100".equals(A0100) || "su".equalsIgnoreCase(A0100))
				A0100=getUserId(tablename);	
			for(int i=0;i<infofieldlist.size();i++)
			{
				FieldItem fieldItem=(FieldItem)infofieldlist.get(i);
				if("#####".equalsIgnoreCase(fieldItem.getItemid()))
					continue;
				
				if(2 != fieldItem.getPriv_status()) {
				    FieldItemView FieldItemView=(FieldItemView)infofieldlist.get(i);
				    String oldValue = FieldItemView.getOldvalue();
				    oldValue = StringUtils.isEmpty(oldValue) ? FieldItemView.getViewvalue() : oldValue;
				    fieldItem.setValue(oldValue);
				}
				
				if(!setname.equalsIgnoreCase(fieldItem.getFieldsetid())){
					infofieldlist.remove(i);
					i--;
				} else {
					fieldlist.add(fieldItem);
				}
			}
			StringBuffer fields=new StringBuffer();
			StringBuffer fieldvalues=new StringBuffer();		
			String[] fieldsname=new String[fieldlist.size()];
			String[] fieldcode=new String[fieldlist.size()];
			String org_id="";
			String pos_id="";
			String value="";
			StringBuffer errorMsg = new StringBuffer();
			for(int i=0;i<fieldlist.size();i++)
			{
				FieldItem fieldItem=(FieldItem)fieldlist.get(i);
				if(fieldItem.isFillable() && StringUtils.isEmpty(fieldItem.getValue().trim())) {
					errorMsg.append(fieldItem.getItemdesc() + ResourceFactory.getProperty("workbench.info.isRequired") + "<br>");
				}
				
				fields.append(fieldItem.getItemid());
				fieldsname[i]=fieldItem.getItemid();
				//【8954】员工管理-记录录入（录入一个姓名带有英文半角括号，保存后，但是在查询的时候查询不出来这个人） jingq upd 2015.04.22
                String s =PubFunc.getStr(fieldItem.getValue());
                s = PubFunc.keyWord_reback(s);
				s = PubFunc.stripScriptXss(s);
				if(StringUtils.isNotEmpty(s)) {
				    s = PubFunc.replaceSQLkey(s);
				}
				
                fieldItem.setValue(s);
				//fieldcode[i]=fieldItem.getValue();
				if(fieldItem.isSequenceable()&&"new".equals(actiontype))
				{
					IDGenerator idg=new IDGenerator(2,this.frameconn);
					String idd=idg.getId(fieldItem.getSequencename());				
					fieldvalues.append("'"+idd+"'");
				}
				else if("D".equals(fieldItem.getItemtype()))
				{
				  fieldvalues.append(PubFunc.DateStringChange(fieldItem.getValue()));
			      fieldcode[i]=PubFunc.DateStringChange(fieldItem.getValue());				 		  
				}else if("M".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]="'" + fieldItem.getValue() + "'";					
						fieldvalues.append("'" + fieldItem.getValue() + "'");
					}
				}else if("N".equals(fieldItem.getItemtype()))
				{
					if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
					{	
						fieldcode[i]="null";
						fieldvalues.append("null");
					}
					else
					{
						fieldcode[i]=fieldItem.getValue();
						fieldvalues.append(fieldItem.getValue());
					}
				}
			    else
				{
				    if (fieldItem.getValue() == null || "null".equals(fieldItem.getValue()) || "".equals(fieldItem.getValue()))
				   {
				    	fieldcode[i]="null";
				    	fieldvalues.append("null");
				   }
				   else
				   {
				        fieldcode[i]="'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'";
				        if("b0110".equalsIgnoreCase(fieldItem.getItemid())|| "e0122".equalsIgnoreCase(fieldItem.getItemid())){
				        	org_id=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
				        }
				        if("e01a1".equalsIgnoreCase(fieldItem.getItemid())){
				        	pos_id=PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength());
				        }
						fieldvalues.append("'" + PubFunc.splitString(fieldItem.getValue(),fieldItem.getItemlength()) + "'");
					}
				}
				fields.append(",");
				fieldvalues.append(",");
			}
			
			if(StringUtils.isNotEmpty(errorMsg.toString()))
				throw new GeneralException(errorMsg.toString());
			
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
			String inputchinfor=sysbo.getValue(Sys_Oth_Parameter.INPUTCHINFOR);
			inputchinfor=inputchinfor!=null&&inputchinfor.trim().length()>0?inputchinfor:"1";
			String approveflag=sysbo.getValue(Sys_Oth_Parameter.APPROVE_FLAG);
			approveflag=approveflag!=null&&approveflag.trim().length()>0?approveflag:"1";
			
			StructureExecSqlString structureExecSqlString=new StructureExecSqlString();
			structureExecSqlString.setFieldcode(fieldcode);
			String checksave="01";
			
			ArrayList msglist = new ArrayList();
			ScanFormationBo scanFormationBo = new ScanFormationBo(this.getFrameconn(),this.userView);
			if (scanFormationBo.doScan()){
				boolean bScan=true;
				StringBuffer itemids = new StringBuffer();
				LazyDynaBean scanBean = new LazyDynaBean();
				setScanBeanList(actiontype,userbase,setname,A0100,I9999,fieldlist,itemids,scanBean);

				if ("true".equals(scanFormationBo.getPart_flag()) 
						&&    setname.equals(scanFormationBo.getPart_setid())){//兼职子集
					String part_fld="";
					part_fld=scanFormationBo.getPart_unit();
					if ((part_fld!=null) && (!"".equals(part_fld))) 
					  itemids.append(",b0110");
					part_fld=scanFormationBo.getPart_dept();
					if ((part_fld!=null) && (!"".equals(part_fld))) 
						itemids.append(",e0122");
					part_fld=scanFormationBo.getPart_pos();
					if ((part_fld!=null) && (!"".equals(part_fld))) 
						itemids.append(",e01a1");
				}
                if ((","+itemids+",").indexOf(",e01a1,")<0){
                    scanFormationBo.setPosChange(false);
                }
                
				if (scanFormationBo.needDoScan(userbase+',', itemids.toString())){
					
					scanBean.set("objecttype", "1");	
					scanBean.set("nbase", userbase);
					scanBean.set("a0100", A0100);	
					scanBean.set("ispart", "0");	
					
					if("new".equals(actiontype)){
						scanBean.set("addflag", "1");	
					}
					else {
						scanBean.set("addflag", "0");	
					}

					if ("01".equals(setname.substring(1,3))){//主集
					}
					else {
						if ("true".equals(scanFormationBo.getPart_flag()) 
								&&    setname.equals(scanFormationBo.getPart_setid())){//兼职子集
							scanBean.set("ispart", "1");	
							scanBean.set("i9999", I9999);
							String part_fld="";
							part_fld=scanFormationBo.getPart_unit();
							if ((part_fld!=null) && (!"".equals(part_fld))) 
							  if ((String)scanBean.get(part_fld)!=null) 
								  scanBean.set("b0110", (String)scanBean.get(part_fld));
							part_fld=scanFormationBo.getPart_dept();
							if ((part_fld!=null) && (!"".equals(part_fld))) 
								if ((String)scanBean.get(part_fld)!=null) 
									scanBean.set("e0122", (String)scanBean.get(part_fld));
							part_fld=scanFormationBo.getPart_pos();
							if ((part_fld!=null) && (!"".equals(part_fld))) 
								if ((String)scanBean.get(part_fld)!=null) 
									scanBean.set("e01a1", (String)scanBean.get(part_fld));
						}
						else {//普通子集 最近一条才检查
						     scanBean.set("addflag", "0");  //普通子集都为修改 
							 if("new".equals(actiontype)){
								 if(reqhm.containsKey("insert1")){//新增
									 bScan = false;
								 }
							 }
							 else {
								 if (!(getMaxI9999(userbase,setname,A0100)).equals(I9999)){//不是最近一条
									 bScan =false;
								 }
							 }
						}
					}

					if (bScan) {					
						ArrayList beanList = new ArrayList();
						beanList.add(scanBean);
						scanFormationBo.execDate2TmpTable(beanList);
						String mess=  scanFormationBo.isOverstaffs();
						if (!"ok".equals(mess)){
							if("warn".equals(scanFormationBo.getMode())){
								msglist.add(mess);
							}else{
								throw GeneralExceptionHandler.Handle(new GeneralException("",mess,"",""));
							}
						}
						
					}
				}
				
			}
			
			
			if("new".equals(actiontype)){
				if(tablename.length()==3 && "01".equals(tablename.substring(1,3)) || tablename.length()==6 && "01".equals(tablename.substring(4,6))){
						A0100=structureExecSqlString.InfoInsert("1",tablename,fields.toString(),fieldvalues.toString(),A0100,userView.getUserName(),this.getFrameconn());
						db.dateLinkage(org_id,pos_id,1,"+");
				}else{
					String tempii=n9999;
					
					if("1".equals(inputchinfor)&& "1".equals(approveflag)){/**去掉&&this.userView.getStatus()==4*/
						MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView,userbase,A0100);
						String sp_flag=mysel.getSp_flag();
						sp_flag=sp_flag!=null?sp_flag:"";
						if(!"02".equals(sp_flag)){
							ArrayList oldlist = new ArrayList();
							FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
							
							if(reqhm.containsKey("insert1")){
								reqhm.remove("insert1");
								tempii=tempii!=null&&tempii.trim().length()>0?tempii:"0";
								
								mysel.getOtherParamList1(fieldset.getFieldsetid(),tempii);
								ArrayList sequenceList = mysel.getSequenceList();
								String sequence ="1";
								if(sequenceList.size()>0){
									sequence =(Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+1)+"";
								}
								
								mysel.saveMyselfData(userbase,A0100,fieldset,fieldlist,oldlist,"insert","01",tempii,sequence);
								I9999=n9999;
							}else{
								try{
									if("I9999".equalsIgnoreCase(tempii))
										tempii="-1";
									
									mysel.getOtherParamList1(fieldset.getFieldsetid(),tempii);
									ArrayList sequenceList = mysel.getSequenceList();
									String sequence ="1";
									if(sequenceList.size()>0){
										sequence =(Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+1)+"";
									}
									mysel.saveMyselfData(userbase,A0100,fieldset,fieldlist,oldlist,"new","01",tempii,sequence);
								}catch(Exception e){
									e.printStackTrace();
								}
							}
						}else{
							checksave="02";
						}
					}else{
						I9999=structureExecSqlString.InfoInsert("1",tablename,fields.toString(),fieldvalues.toString(),A0100,userView.getUserName(),this.getFrameconn());					
						if(reqhm.containsKey("insert1")){
							reqhm.remove("insert1");
							this.updateRecord(tempii,tablename,A0100,this.getFrameconn());
						}
					}
				}
					
			}else{
				if("1".equals(inputchinfor)&& "1".equals(approveflag)){/**去掉&&this.userView.getStatus()==4*/
					MyselfDataApprove mysel = new MyselfDataApprove(this.frameconn,this.userView,userbase,A0100);
					FieldSet fieldset = DataDictionary.getFieldSetVo(setname);
					ArrayList oldlist = (ArrayList)this.getFormHM().get("itemlist");
					try{
						String checki9999 = I9999;
						if(fieldset.isMainset())
							checki9999=A0100;
						mysel.getOtherParamList(fieldset.getFieldsetid(),checki9999);
						ArrayList sequenceList = mysel.getSequenceList();
						String sequence ="1";
						if(sequenceList.size()>0){
							sequence =(Integer.parseInt((String)sequenceList.get(sequenceList.size()-1))+1)+"";
						}
						chg_id = mysel.getChg_id();
						this.getFormHM().put("chg_id", chg_id);
						mysel.getOneMyselfData(mysel.getChg_id(),fieldset.getFieldsetid(),checki9999,"update",sequence,"");
						ArrayList newFieldList = mysel.getNewValueList();
						ArrayList oldFieldList = mysel.getOldValueList();
						String checkflag = mysel.getRecord_spflag();
						
						if("0".equals(buttonvalue))
							viewbutton=buttonvalue;
						if(newFieldList.size()>0&&oldFieldList.size()>0&&("01".equals(checkflag)|| "07".equals(checkflag))){
							mysel.saveMyselfData(userbase,A0100,fieldset,updateList(fieldlist,oldlist,newFieldList),oldlist,"update",checkflag,checki9999,sequence);
						}else{
							mysel.saveMyselfData(userbase,A0100,fieldset,fieldlist,oldlist,"update","01",checki9999,sequence);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{
					new StructureExecSqlString().InfoUpdate("1",tablename,fieldsname,fieldcode,A0100,I9999,userView.getUserName(),this.getFrameconn());
				}
			}
			this.getFormHM().put("viewbutton", viewbutton);
			this.getFormHM().put("infofieldlist",infofieldlist);
			this.getFormHM().put("a0100",A0100);
			this.getFormHM().put("i9999",I9999);
			this.getFormHM().put("checksave",checksave);
			this.getFormHM().put("actiontype",actiontype);
			if(msglist.size()>0){
				StringBuffer msg = new StringBuffer();
				for(int i=0;i<msglist.size();i++){
					if(msglist.size()>1){
						msg.append((i+1)+":"+msglist.get(i)+"\\n");
					}else{
						msg.append(msglist.get(i));
					}
				}
				this.getFormHM().put("formationMsg", msg.toString());
			}else
				this.getFormHM().put("formationMsg", "");
			
			if("new".equals(actiontype)) {
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.saveSelfInsert"));
			} else {
				this.getFormHM().put("@eventlog", ResourceFactory.getProperty("workbench.info.log.saveSelfUpdate"));
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
				ContentDAO dao = new ContentDAO(this.frameconn);
				try {
					dao.update("delete from "+tablename+" where CreateTime is null and ModTime is null and CreateUserName is null and ModUserName is null and a0100='"+A0100+"'");
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			throw GeneralExceptionHandler.Handle(ex);
		}
		
		
	}
	
	private RecordVo getNewRecord(RecordVo vo_old,String fields[],String fieldvalue[]){
		ArrayList fieldlist = DataDictionary.getFieldList(vo_old.getModelName().substring(3),Constant.ALL_FIELD_SET);
		Map dateTypeMap = new HashMap();
		//田野 添加信息修改需要审批，直接入库时超编后验证修改时 时间类型的数据进行格式转换，TO_DATE(1991-01-07，YYYY-MM-DD)变成1991-01-07
	for(int i=0;i<fieldlist.size();i++){
			FieldItem fielditem = (FieldItem)fieldlist.get(i);
			if("D".equals(fielditem.getItemtype()))
			{
				dateTypeMap.put(fielditem.getItemid(), fielditem.getValue());
			}
		}
		for (int i = 0; i < fields.length; i++) {
			String itemid = fields[i];
			if(itemid!=null&&itemid.trim().length()>0){
				if(fieldvalue[i]!=null&&!"null".equals(fieldvalue[i])&&!"'null'".equals(fieldvalue[i])){
					if(dateTypeMap.containsKey(itemid)){
						String date ="";
						if(fieldvalue[i].startsWith("TO_DATE")){
							date = fieldvalue[i].substring(8,fieldvalue[i].indexOf(",")).replaceAll("'", "");
							vo_old.setDate(itemid, date);
						}else{
							vo_old.setObject(itemid,fieldvalue[i].replaceAll("'", ""));
						}
					}else{
						vo_old.setObject(itemid,fieldvalue[i].replaceAll("'", ""));
					}
				}else{
					vo_old.setObject(itemid,null);
				}
			}
		}
		return vo_old;
	}
	
	public String getUserId(String tableName) throws GeneralException{
		return DbNameBo.insertMainSetA0100(tableName,this.getFrameconn());
	}
	
	private void updateRecord(String I9999,String tablename,String A0100,Connection conn){
		
		String upsql1="update "+tablename+" set I9999=I9999+1 where I9999>="+I9999+" and a0100='"+A0100+"' ";
		String upsql="update "+tablename+" set I9999="+I9999+" where I9999=(select max(I9999) from "+tablename+" where a0100='"+A0100+"')  and a0100='"+A0100+"'";
		try{
		    ContentDAO dao = new ContentDAO(conn);
			dao.update(upsql1);
			dao.update(upsql);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private ArrayList updateList(ArrayList newFieldList,ArrayList oldFieldList,ArrayList fieldlist){
		return newFieldList;
	}
	
	private String getMaxI9999(String usrbase,String fieldsetid,String a0100) throws SQLException{
		String maxi9999 = "0";
		ContentDAO dao = new ContentDAO(this.getFrameconn());
		String sql = "select max(i9999) i9999 from "+usrbase+fieldsetid+" where a0100='"+a0100+"'";
		this.frecset = dao.search(sql);
		if(this.frecset.next())
			maxi9999 = String.valueOf(this.frecset.getInt("i9999"));
		
		return maxi9999;
	}
	
    private void setScanBeanList(String actiontype,String userbase,String setname,String A0100,String I9999,
            ArrayList fieldlist,StringBuffer scanItemIds, LazyDynaBean scanBean) {       

        scanItemIds.setLength(0);
        boolean compareFlag = "update".equals(actiontype);
        LazyDynaBean rec = null;
        if (compareFlag) {
            List rs = null;
            StringBuffer strsql = new StringBuffer();
            strsql.append("select * from ");
            strsql.append(userbase + setname);
            strsql.append(" where A0100='");
            strsql.append(A0100);
            strsql.append("'");
            if (!"A01".substring(1, 3).equals(setname.substring(1, 3))) // 如果子集的修改则条件有I9999
            {
                strsql.append(" and I9999=");
                strsql.append(I9999);
            }
            rs = ExecuteSQL.executeMyQuery(strsql.toString(), this.getFrameconn());
            boolean isExistData = !rs.isEmpty();
            compareFlag = false;
            if (isExistData) {
                rec = (LazyDynaBean) rs.get(0);
                compareFlag = true;
            }

        }

        for (int i = 0; i < fieldlist.size(); i++) {
            FieldItem fieldItem = (FieldItem) fieldlist.get(i);
            if (fieldItem.isSequenceable())
                continue;

            String itemid = fieldItem.getItemid().toLowerCase();
            String value = PubFunc.getStr(fieldItem.getValue());

            if ((value == null) || ("null".equalsIgnoreCase(value))) {
                value = "";
            }
            scanBean.set(itemid, value);

            if (compareFlag) {
                value = "";
                if ("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype())) {
                    value = rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "";
                } else if ("D".equals(fieldItem.getItemtype())) {
                    if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 10) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 10)));
                    } else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 4) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 4)));
                    } else if (rec.get(fieldItem.getItemid()) != null && rec.get(fieldItem.getItemid()).toString().length() >= 10 && fieldItem.getItemlength() == 7) {
                        value = (new FormatValue().format(fieldItem, rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0, 7)));
                    } else {
                        value = "";
                    }
                } else {
                    value = (PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid()) != null ? rec.get(fieldItem.getItemid()).toString() : "", fieldItem.getDecimalwidth()));
                }
                if (fieldItem.getValue() != null) {
                    if (fieldItem.getValue().equals(value))
                        continue;
                } else {
                    if ("".equals(value))
                        continue;
                }
            }

            if (!"".equals(scanItemIds.toString())) {
                scanItemIds.append(",");
            }
            scanItemIds.append(itemid);
 
}
}
	
}
