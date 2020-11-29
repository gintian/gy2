/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.orginfo;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddIniOrgInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		    String setname="b01";
		    List rs=null;
		    String fieldprv="";
			String code=(String)this.getFormHM().get("code");
			String kind=(String)this.getFormHM().get("kind");
			String I9999=(String)this.getFormHM().get("i9999");
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM"); 
			String busiPriv = (String)hm.get("busiPriv");
			busiPriv=busiPriv==null?"":busiPriv;
			String orgtype=(String)this.getFormHM().get("orgtype");
			if(orgtype==null||orgtype.length()<=0)
				orgtype="org";
			String setprv="0";
			String fieldname="";
			String prefield="";
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
			List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
			List infoSetList=userView.getPrivFieldSetList(Constant.UNIT_FIELD_SET);   //获得所有权限的子集
			AddOrgInfo addOrgInfo=new AddOrgInfo(this.getFrameconn());
			infoSetList=addOrgInfo.filtrationFieldSet(infoSetList);
			List infoFieldViewList=new ArrayList();                  //保存处理后的属性
			try
			{
				//System.out.println("code " + code);
				ContentDAO dao=new ContentDAO(this.getFrameconn());
				String busi = this.getBusi_org_dept(busiPriv);
				if(busi.length()>2){
					StringBuffer sb = new StringBuffer();
					sb.append("select min(b0110) as b0110 from b01 where 1=2");
			    	String[] org_depts = busi.split("`");
					for(int i=0;i<org_depts.length;i++){
						String org_dept = org_depts[i];
						if(org_dept.length()>2){
							sb.append(" or b0110='"+org_dept.substring(2)+"'");
						}
					}
					this.frowset = dao.search(sb.toString());
				}
				else if(userView.getManagePrivCodeValue() !=null && userView.getManagePrivCodeValue().length()>0)
					this.frowset = dao.search("select min(b0110) as b0110 from b01 where b0110 is not null and "+Sql_switcher.isnull("b0110","'_'")+"<>'_' and b0110 like '" + userView.getManagePrivCodeValue() + "%'");
				else
					this.frowset = dao.search("select min(b0110) as b0110 from b01 where b0110 is not null and "+Sql_switcher.isnull("b0110","'_'")+"<>'_'");
				if(this.frowset.next())
					code=this.frowset.getString("b0110");
				//System.out.println("code " + code);
				if(code==null || code.trim().length()==0)
				{
					if(busi.length()>2){
						StringBuffer sb = new StringBuffer();
						sb.append("select min(codeitemid) as b0110 from organization where 1=2");
				    	String[] org_depts = busi.split("`");
						for(int i=0;i<org_depts.length;i++){
							String org_dept = org_depts[i];
							if(org_dept.length()>2){
								sb.append(" or codeitemid='"+org_dept.substring(2)+"'");
							}
						}
						this.frowset = dao.search(sb.toString());
					}
					else if(userView.getManagePrivCodeValue() !=null && userView.getManagePrivCodeValue().length()>0)
						this.frowset = dao.search("select min(codeitemid) as b0110 from organization where codeitemid is not null and "+Sql_switcher.isnull("codeitemid","'_'")+"<>'_' and codeitemid like '" + userView.getManagePrivCodeValue() + "%' and codesetid<>'@K'");
					else
						this.frowset = dao.search("select min(codeitemid) as b0110 from organization where codeitemid is not null and "+Sql_switcher.isnull("codeitemid","'_'")+"<>'_'  and codesetid<>'@K'");
					if(this.frowset.next())
						code=this.frowset.getString("b0110");
				}
				//System.out.println(code);
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
					 fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.org.orgname"));
					 fieldItemV.setItemtype("A");
					 fieldItemV.setPriv_status(1);				    	
					 if(code !=null && code.trim().length()>0)
					 {
					 	if(!"vorg".equals(orgtype))
					 	{
					 		 fieldItemV.setViewvalue(AdminCode.getCode(prefield,code)!=null?AdminCode.getCode(prefield,code).getCodename():"");
						 	 fieldItemV.setValue(AdminCode.getCode(prefield,code)!=null?AdminCode.getCode(prefield,code).getCodename():"");					    	
					 	}else
					 	{
					 		HashMap hash=addOrgInfo.getVOrgMess(code);
					 		fieldItemV.setViewvalue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
						 	fieldItemV.setValue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");	
					 	}
					 }
					 else
					 {
					 	fieldItemV.setValue("");
					 	fieldItemV.setViewvalue("");
					 }					    		
					infoFieldViewList.add(fieldItemV);
					//System.out.println(strsql.toString());
					rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
					isExistData=!rs.isEmpty();

					for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
					{
					    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
					    //System.out.println(fieldItem.getItemid());
						if(fieldItem.getPriv_status() !=0  && (!("2".equals(kind) && "b0110".equalsIgnoreCase(fieldItem.getItemid()))) && !"b0110".equalsIgnoreCase(fieldItem.getItemid()) && !("1".equals(kind) && "b0110".equalsIgnoreCase(fieldItem.getItemid())))                //只加在有读写权限的指标
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
                            //在struts用来表示换行的变量
							fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
							j++;
							if(isExistData)
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
										if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
										   fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),codevalue)!=null?AdminCode.getCode(fieldItem.getCodesetid(),codevalue).getCodename():"");
									    else
									       fieldItemView.setViewvalue("");
									}
									else
									{
										//System.out.println("itemid" + fieldItem.getItemid() + "type" + fieldItem.getItemtype());
										fieldItemView.setViewvalue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");
									}
									fieldItemView.setValue(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"");						
								}else if("D".equals(fieldItem.getItemtype()))                 //日期型有待格式化处理
								{
									if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==10)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,10)));
									}else if(this.getFrowset().getString(/*fieldItem.getItemid()*/"B0110")!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
									}else if(this.getFrowset().getString(/*fieldItem.getItemid()*/"B0110")!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
									{
										fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
									    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
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
			/*	for(int p=0;p<infoSetList.size();p++)
				{
					FieldSet fieldset=(FieldSet)infoSetList.get(p);
					if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
					{
						setprv=String.valueOf(fieldset.getPriv_status());
						break;
					}
				}*/
				setprv=getEditSetPriv(infoSetList,infoFieldList,setname);			
				infoFieldViewList=addOrgInfo.filtrationFieldItem(infoFieldViewList);
			}catch(Exception e){
			   e.printStackTrace();
			}finally{
			   //this.getFormHM().put("fieldprv",fieldprv);
			   this.getFormHM().put("setprv",setprv);
			   this.getFormHM().put("code",code);
			   this.getFormHM().put("i9999",I9999);
			   this.getFormHM().put("setname",setname);
			   this.getFormHM().put("edittype","new");
		       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
		       this.getFormHM().put("infosetlist",infoSetList); 
		       this.getFormHM().put("kind",kind);
		       this.getFormHM().put("orgFieldID", orgFieldID.toUpperCase());
		       this.getFormHM().put("type",type);
		       this.getFormHM().put("contentField",contentType);
		       this.getFormHM().put("contentFieldValue",type);
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
		for(int i=0;i<infoFieldList.size();i++)                            //字段的集合
		{
		    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
		    if(fieldItem.getPriv_status()==2)
		    {
		    	bflag=true;
		    	break;
		    }
		}
		if(bflag)
			return "3";
		else
			return setpriv;
	}
	
	private String getBusi_org_dept(String busiPriv){
		String busi="";
		if("1".equals(busiPriv)){
		int status = this.userView.getStatus();
		if (!this.userView.isSuper_admin() /*&& 0 == status*/) {// 非超级用户组下业务用户
			String busi_org_dept = "";
			try {
				/*ContentDAO dao = new ContentDAO(this.getFrameconn());
				String sql = "select busi_org_dept from operuser where username='"
						+ this.userView.getUserName() + "'";
				this.frecset = dao.search(sql);
				while (this.frecset.next()) {
					busi_org_dept = Sql_switcher.readMemo(this.frecset,
							"busi_org_dept");
				}*/
				busi_org_dept = this.userView.getUnitIdByBusi("4");
				if (busi_org_dept.length() > 0) {
					/*String str[] = busi_org_dept.split("\\|");
					for (int i = 0; i < str.length; i++) {// 1,UNxxx`UM9191`
						String tmp = str[i];
						String ts[] = tmp.split(",");
						if (ts.length == 2) {
							if("4".equals(ts[0])){
							busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(ts[1]);
								break;
							}
						}
					}*/
					busi = com.hjsj.hrms.utils.PubFunc.getTopOrgDept(busi_org_dept);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {// 1,UNxxx`UM9191`|2,UNxxx`UM9191`

			}
		}
		}
		return busi;
	}
}
