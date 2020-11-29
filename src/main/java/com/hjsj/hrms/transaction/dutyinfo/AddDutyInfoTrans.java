/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.businessobject.org.AddOrgInfo;
import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.common.StationPosView;
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
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
public class AddDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		String edit_flag=(String)this.getFormHM().get("edit_flag");//从信息维护中新建或是修改
		if(edit_flag==null||edit_flag.length()<=0)
			edit_flag="edit";
		if("new".equals(edit_flag))
			newCreatDutyInfo();
		else
			editPosInfo();
		this.getFormHM().put("edit_flag", edit_flag);//新增修改标志
	}
	
	private void editPosInfo()throws GeneralException
	{
		
		String pos_code_field="";	
	    RecordVo vo= ConstantParamter.getRealConstantVo("POS_CODE_FIELD");//岗位代码
	    if(vo!=null)
	    {
	    	pos_code_field=vo.getString("str_value");	    	
	    }
		List rs=null;
		//String setname=(String)this.getFormHM().get("setname");  //获得入录子集的名称
	    String setname="k01";
		String setprv="0";
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String I9999=(String)this.getFormHM().get("i9999");
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.POS_FIELD_SET);   //获得所有权限的子集
		List infoFieldViewList=new ArrayList();  
		String orgtype=(String)this.getFormHM().get("orgtype");
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";//保存处理后的属性
		
		if(code==null|| "".equals(code))
		{
		  throw GeneralExceptionHandler.Handle(new Exception("请点击左边机构树中的职位信息"));
		}
		try
		{
			getCodeItemInfo(code);
			if("k01".equals(setname))  //添加岗位属性
			{
				String codeitemid=(String)this.getFormHM().get("codeitemid");//系统代码
				String corcode=(String)this.getFormHM().get("corcode");//岗/职位代码
				FieldItem posFi = DataDictionary.getFieldItem(pos_code_field);
				if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field)
						&& posFi != null && !"0".equals(posFi.getUseflag())) {
					corcode = getCorcode(codeitemid, pos_code_field); //解决有时候岗位代码有值却传不过来的问题
				}
				String codeitemdesc=(String)this.getFormHM().get("codeitemdesc");//岗/职位名称
				corcode=corcode!=null&&corcode.length()>0?corcode:"";
				String len=(String)this.getFormHM().get("len");
				len=len!=null&&len.length()>0?len:"30";
				FieldItemView fieldItemV=new FieldItemView();
			 	/*****岗位代码*****/
			 	int pos_code_len=30;
			 	String pos_code_desc="";
			 	FieldItem item=null;
				if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field)
						&& posFi != null && !"0".equals(posFi.getUseflag())) {
					item=DataDictionary.getFieldItem(pos_code_field);
					pos_code_len=item.getItemlength();
					pos_code_desc=item.getItemdesc();
				}
			 	fieldItemV=new FieldItemView();	 	
			 	if(pos_code_desc!=null&&pos_code_desc.length()>0)
			 		fieldItemV.setItemdesc(pos_code_desc);	
			 	else
				  fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.code"));
			 	
				fieldItemV.setItemtype("A");
			    fieldItemV.setPriv_status(2);	
			    fieldItemV.setItemid("corcode");
			    fieldItemV.setViewvalue(corcode);
			 	fieldItemV.setValue(corcode);	
			 	fieldItemV.setItemlength(pos_code_len);
			 	fieldItemV.setCodesetid("0");
			 	if(item!=null)
			 		fieldItemV.setFillable(item.isFillable());
			 	
			 	if(posFi != null && !"0".equals(posFi.getUseflag()))
			 		infoFieldViewList.add(fieldItemV);			 	
			 	
			 	fieldItemV=new FieldItemView();
				fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.pos"));
				fieldItemV.setItemtype("A");
				fieldItemV.setItemid("codeitemdesc");
				if(this.userView.getFuncpriv().toString().indexOf("231101020")!=-1||userView.isSuper_admin()){//zgd 2014-5-4 当用户有“修改岗位名称”权限时，才让修改
					fieldItemV.setPriv_status(2);	
			    }else{
			    	fieldItemV.setPriv_status(1);	
			    }
			    fieldItemV.setViewvalue(codeitemdesc);
			 	fieldItemV.setValue(codeitemdesc);	
			 	RecordVo rvo = new RecordVo("organization");
				  Map lenmap = rvo.getAttrLens();
				  int codeitemdesclen = Integer.parseInt((String)lenmap.get("codeitemdesc"));
			 	fieldItemV.setItemlength(codeitemdesclen);
			 	fieldItemV.setCodesetid("0");
			 	fieldItemV.setFillable(true);
			 	infoFieldViewList.add(fieldItemV);
			}
			if(!infoFieldList.isEmpty())
		    {
				boolean isExistData=false;
				StringBuffer strsql=new StringBuffer();
				strsql.append("select " + setname + ".*,parentid as e0122_p from ");
				strsql.append(setname + " left join organization on " + setname + ".E01A1=organization.codeitemid");
				strsql.append(" where E01A1='");
				strsql.append(code);
				strsql.append("'");
				if(!"01".equals(setname.substring(1,3)))   //如果子集的修改则条件有I9999
				{
					strsql.append(" and I9999=");
					strsql.append(I9999);
				}
				rs = ExecuteSQL.executeMyQuery(strsql.toString(),this.getFrameconn());
				isExistData=!rs.isEmpty();

			    FieldItemView fieldItemV=new FieldItemView();						
			    fieldItemV.setPriv_status(1);
			    fieldItemV.setItemtype("A");
			    fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.pos.posname"));
			    if(isExistData)
				{
			    	if(!"vorg".equals(orgtype))
			    	{
			    		LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						String codevalue=rec.get("e0122_p")!=null?rec.get("e0122_p").toString():"";
						fieldItemV.setItemid("E0122");
						fieldItemV.setValue(codevalue);
						if(codevalue !=null && codevalue.trim().length()>0)//通过组织机构的parentid进行查询，如果仅通过岗位表中进行查询，显示不对，这里如果没有部门则显示单位 sunjian 2017-9-7
						   fieldItemV.setViewvalue(AdminCode.getCode("UM",codevalue)!=null?AdminCode.getCode("UM",codevalue).getCodename():AdminCode.getCode("UN",codevalue)!=null?AdminCode.getCode("UN",codevalue).getCodename():"");
					    else
						   fieldItemV.setViewvalue("");
			    	}else
			    	{
			    		AddOrgInfo addOrgInfo=new AddOrgInfo(this.getFrameconn());
			    		StationPosView posview=getStationVPos(code,this.getFrameconn());
			    		if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
				 		{
			    			fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"");
				 			fieldItemV.setValue(posview.getItemvalue());
				 		}				 		  
				 		else
				 		{
				 			HashMap hash=addOrgInfo.getVOrgMess(code);
					 		fieldItemV.setViewvalue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
						 	fieldItemV.setValue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
				 		}
				 		
			    	}
			    	
				}
			    else
			    {
			    	if(!"vorg".equals(orgtype))
			    	{
			    		fieldItemV.setItemid("E0122");
				    	StationPosView posview=getStationPos(code,this.getFrameconn());
				    	fieldItemV.setValue(posview.getItemvalue());
				    	if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
				    		fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"");
						else
						   fieldItemV.setViewvalue("");
			    	}else
			    	{
			    		fieldItemV.setItemid("E0122");
			    		AddOrgInfo addOrgInfo=new AddOrgInfo(this.getFrameconn());
			    		StationPosView posview=getStationVPos(code,this.getFrameconn());
				 		HashMap hash=addOrgInfo.getVOrgMess(code);
				 		if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
				 		{
				 			fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"");
				 			fieldItemV.setValue(posview.getItemvalue());
				 		}				 		  
				 		else
				 		{  
				 			fieldItemV.setViewvalue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
				 			fieldItemV.setValue((String)hash.get("codeitemdesc")!=null?(String)hash.get("codeitemdesc"):"");
				 		}
					 	
			    	}
			    	
			    }
				infoFieldViewList.add(fieldItemV);
				  for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
				  {
				    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);	
				    if(fieldItem.getItemid().equals(pos_code_field))
				    	continue;
					if(fieldItem.getPriv_status() !=0&& (!("0".equals(kind) && ("e01a1".equalsIgnoreCase(fieldItem.getItemid())|| "e0122".equalsIgnoreCase(fieldItem.getItemid())))))                //只加在有读写权限的指标
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
						fieldItemView.setPriv_status(fieldItem.getPriv_status());
						fieldItemView.setFillable(fieldItem.isFillable());
                        //在struts用来表示换行的变量
						 fieldItemView.setRowflag(String.valueOf(infoFieldList.size()+3));
						j++;
						if(isExistData)
						{
							LazyDynaBean rec=(LazyDynaBean)rs.get(0);
							if("A".equals(fieldItem.getItemtype()) || "M".equals(fieldItem.getItemtype()))
							{
								if(!"0".equals(fieldItem.getCodesetid()))
								{
									String codevalue=rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"";
									if(codevalue !=null && codevalue.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0) {
										CodeItem codeItem = AdminCode.getCode(fieldItem.getCodesetid(),codevalue);
										if(codeItem == null && "UM".equalsIgnoreCase(fieldItem.getCodesetid())) {
											codeItem = AdminCode.getCode("UN", codevalue);
										}
										
										String viewValue = "";
										if(codeItem != null) {
											viewValue = codeItem.getCodename();
										}
										
										fieldItemView.setViewvalue(viewValue);
									} else
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
								}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								}else if(rec.get(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,7)));
								}
								else
	                            {
	                            	fieldItemView.setViewvalue("");
								    fieldItemView.setValue("");
	                            }
							} else {
								fieldItemView.setValue(PubFunc.DoFormatDecimal(rec.get(fieldItem.getItemid())!=null?rec.get(fieldItem.getItemid()).toString():"",fieldItem.getDecimalwidth()));						
							}
						}
						infoFieldViewList.add(fieldItemView);
					}
				}
			}
			setprv=getEditSetPriv(infoSetList,infoFieldList,setname);
			
			String ps_superior="";
			RecordVo ps_superior_vo=ConstantParamter.getRealConstantVo("PS_SUPERIOR",this.getFrameconn());
			if(ps_superior_vo!=null)
			{
			  ps_superior=ps_superior_vo.getString("str_value");
			}
			this.getFormHM().put("ps_superior",ps_superior.toLowerCase());
		}catch(Exception e){
		   e.printStackTrace();
		}finally{
			this.getFormHM().put("setprv",setprv);
		   this.getFormHM().put("code",code);
		   this.getFormHM().put("i9999",I9999);
		   this.getFormHM().put("setname",setname);
	       this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
	       this.getFormHM().put("infosetlist",infoSetList); 
	       this.getFormHM().put("edittype","new");	       
       }
	}
	private StationPosView getStationPos(String code,Connection conn)
	{
		//System.out.println("pos" + code + kind);
		String pre="@K";	
		Statement stmt = null;
		ResultSet rs=null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
		    ContentDAO db=new ContentDAO(conn);
			while(!"UM".equalsIgnoreCase(pre))
			{
			  strsql.delete(0,strsql.length());
			  strsql.append("select * from organization");
			  strsql.append(" where codeitemid='");
			  strsql.append(code);
			  strsql.append("'");					
			  rs =db.search(strsql.toString());	//执行当前查询的sql语句	
			 if(rs.next())
			 {
				pre=rs.getString("codesetid");//编辑的时候如果没有上级部门值，则显示单位值  sunjian 2017-9-7
				if("UM".equalsIgnoreCase(pre)||"UN".equalsIgnoreCase(pre)||code.equals(rs.getString("parentid")))
				{
					StationPosView posview=new StationPosView();
					posview.setItem("e0122");
					posview.setItemvalue(rs.getString("codeitemid"));
					posview.setItemviewvalue(rs.getString("codeitemdesc"));
					return posview;
				}
				code=rs.getString("parentid");				
			 }			
			}	
		    }catch (SQLException sqle){
				sqle.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}	
		    
		return null;
	}
	private StationPosView getStationVPos(String code,Connection conn)
	{
		//System.out.println("pos" + code + kind);
		String pre="@K";	
		Statement stmt = null;
		ResultSet rs=null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
		    ContentDAO db=new ContentDAO(conn);
			while(!"UM".equalsIgnoreCase(pre))
			{
			  strsql.delete(0,strsql.length());
			  strsql.append("select * from vorganization");
			  strsql.append(" where codeitemid='");
			  strsql.append(code);
			  strsql.append("'");					
			  rs =db.search(strsql.toString());	//执行当前查询的sql语句	
			 if(rs.next())
			 {
				pre=rs.getString("codesetid");
				if("UM".equalsIgnoreCase(pre)||"UN".equalsIgnoreCase(pre))
				{
					StationPosView posview=new StationPosView();
					posview.setItem("e0122");
					posview.setItemvalue(rs.getString("codeitemid"));
					posview.setItemviewvalue(rs.getString("codeitemdesc"));
					return posview;
				}
				code=rs.getString("parentid");				
			 }else
			 {
				 strsql.delete(0,strsql.length());
				  strsql.append("select * from organization");
				  strsql.append(" where codeitemid='");
				  strsql.append(code);
				  strsql.append("'");					
				  rs =db.search(strsql.toString());	//执行当前查询的sql语句	
				 if(rs.next())
				 {
					pre=rs.getString("codesetid");
					if("UM".equalsIgnoreCase(pre)||"UN".equalsIgnoreCase(pre))
					{
						StationPosView posview=new StationPosView();
						posview.setItem("e0122");
						posview.setItemvalue(rs.getString("codeitemid"));
						posview.setItemviewvalue(rs.getString("codeitemdesc"));
						return posview;
					}
					code=rs.getString("parentid");		
				 }
			 }			
			}
		    }catch (SQLException sqle){
				sqle.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}	

		return null;
	}
	private StationPosView getStationK(String code,Connection conn)
	{
		//System.out.println("pos" + code + kind);
		//String pre="@K";	
		ResultSet rs=null;
		StringBuffer strsql=new StringBuffer();
		try{
		    ContentDAO db=new ContentDAO(conn);
			//while(!"UM".equalsIgnoreCase(pre))
			//{
			  strsql.delete(0,strsql.length());
			  strsql.append("select * from organization");
			  strsql.append(" where codeitemid='");
			  strsql.append(code);
			  strsql.append("'");					
			  rs =db.search(strsql.toString());	//执行当前查询的sql语句	
			 if(rs.next())
			 {
				//pre=rs.getString("codesetid");
				//if("UM".equalsIgnoreCase(pre))
				//{
					StationPosView posview=new StationPosView();
					posview.setItem("e0122");
					posview.setItemvalue(rs.getString("codeitemid"));
					posview.setItemviewvalue(rs.getString("codeitemdesc"));
					return posview;
				//}
				//code=rs.getString("parentid");				
			 }			
			//}
		    }catch (SQLException sqle){
				sqle.printStackTrace();
			}finally
			{
				if(rs!=null)
					try {
						rs.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}	

		return null;
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
	
	/**********新建岗位***********/
    private void newCreatDutyInfo()throws GeneralException
    {
    	List rs=null;
		//String setname=(String)this.getFormHM().get("setname");  //获得入录子集的名称
	    String setname="k01";
		String setprv="0";
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");		
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.POS_FIELD_SET);   //获得所有权限的子集	
		List infoFieldViewList=new ArrayList();  
		String orgtype=(String)this.getFormHM().get("orgtype");
		if(orgtype==null||orgtype.length()<=0)
			orgtype="org";//保存处理后的属性

		if(code==null|| "".equals(code))
			throw GeneralExceptionHandler.Handle(new Exception("请点击左边机构树中的职位信息"));
		
		try {
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frecset = dao.search("select * from organization where codeitemid='"+code+"'");
			if(!this.frecset.next()){
				throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构下不许新增岗位，操作失败！","",""));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getCodeitem(code,kind);
		String codeitemid=(String)this.getFormHM().get("codeitemid");//系统代码
		String corcode=(String)this.getFormHM().get("corcode");//岗/职位代码
		corcode=corcode!=null&&corcode.length()>0?corcode:"";
		String len=(String)this.getFormHM().get("len");
		len=len!=null&&len.length()>0?len:"30";
		FieldItemView fieldItemV=new FieldItemView();
		fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.syscode"));
		fieldItemV.setItemid("codeitemid");
		fieldItemV.setItemtype("A");
	    fieldItemV.setPriv_status(2);	
	    fieldItemV.setViewvalue(codeitemid);
	 	fieldItemV.setValue(codeitemid);	
	 	fieldItemV.setItemlength(Integer.parseInt(len));
	 	fieldItemV.setCodesetid("0");
	 	fieldItemV.setFillable(true);
	 	infoFieldViewList.add(fieldItemV);
	 	
	 	/*****岗位代码*****/
	 	RecordVo pos_vo= ConstantParamter.getRealConstantVo("POS_CODE_FIELD");//岗位代码
	 	int pos_code_len=30;
	 	String pos_code_field="";
	 	String pos_code_desc="";
	 	FieldItem item = null;
		if(pos_vo!=null)
		{
		    	pos_code_field=pos_vo.getString("str_value");
		    	if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field))
		    	{
		    		item=DataDictionary.getFieldItem(pos_code_field);
		    		if(item!=null){
		    			pos_code_len=item.getItemlength();
		    			pos_code_desc=item.getItemdesc();
		    		}
		    	}
		}
	 	fieldItemV=new FieldItemView();	 	
	 	if(pos_code_desc!=null&&pos_code_desc.length()>0)
	 		fieldItemV.setItemdesc(pos_code_desc);	
	 	else
		  fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.code"));		
		fieldItemV.setItemtype("A");
	    fieldItemV.setPriv_status(2);	
	    fieldItemV.setItemid("corcode");
	    fieldItemV.setViewvalue(corcode);
	 	fieldItemV.setValue(corcode);	
	 	fieldItemV.setItemlength(pos_code_len);
	 	fieldItemV.setCodesetid("0");
	 	if(item!=null) {
	 		fieldItemV.setFillable(item.isFillable());
	 		infoFieldViewList.add(fieldItemV);
	 	}
	 	
	 	fieldItemV=new FieldItemView();
		fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.pos"));
		fieldItemV.setItemtype("A");
		fieldItemV.setItemid("codeitemdesc");
	    fieldItemV.setPriv_status(2);	
	    fieldItemV.setViewvalue("");
	 	fieldItemV.setValue("");	
	 	fieldItemV.setItemlength(50);
	 	fieldItemV.setCodesetid("0");
	 	fieldItemV.setFillable(true);
	 	infoFieldViewList.add(fieldItemV);
	 	
	 	fieldItemV=new FieldItemView();						
	    fieldItemV.setPriv_status(1);
	    fieldItemV.setItemtype("A");
	    fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.pos.posname"));
	    fieldItemV.setItemid("E0122");
    	StationPosView posview=getStationPos(code,this.getFrameconn());
    	fieldItemV.setValue(posview.getItemvalue());
    	if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
    		fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():AdminCode.getCode("UN",posview.getItemvalue())!=null?AdminCode.getCode("UN",posview.getItemvalue()).getCodename():"");
    		//fieldItemV.setViewvalue(posview.getItemviewvalue());
		else
		   fieldItemV.setViewvalue("");
		infoFieldViewList.add(fieldItemV);
	 	for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
		{
		    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
		    if(fieldItem.getItemid().equals(pos_code_field))
		    	continue;
		    if(fieldItem.getPriv_status() !=0 && !"e01a1".equalsIgnoreCase(fieldItem.getItemid())&& !"e0122".equalsIgnoreCase(fieldItem.getItemid()))                //只加在有读写权限的指标
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
			    fieldItemView.setRowflag(String.valueOf(infoFieldList.size()+3));
			    fieldItemView.setViewvalue("");
			    fieldItemView.setValue("");
			    infoFieldViewList.add(fieldItemView);
			}
		}
	 	setprv=getEditSetPriv(infoSetList,infoFieldList,setname);	 
	 	this.getFormHM().put("setprv",setprv);
		this.getFormHM().put("edittype","new");
		this.getFormHM().put("setname",setname);
	    this.getFormHM().put("infofieldlist",infoFieldViewList);            //压回页面
	    this.getFormHM().put("infosetlist",infoSetList); 
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
		     
		      AddOrgInfo addOrgInfo=new AddOrgInfo();
		      codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
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
    public void getCodeItemInfo(String code)
    {
    	StringBuffer strsql=new StringBuffer();
		strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from organization where codeitemid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid ");
		strsql.append("union select codesetid,codeitemid,codeitemdesc,parentid,childid,state,grade,a0000,groupid,'org' as flag,corcode from vorganization where codeitemid='");
		strsql.append(code);
		strsql.append("' and codeitemid<>parentid ");
		strsql.append(" order by codeitemid desc");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		int len=30;
		try{	
			this.frowset=dao.search(strsql.toString());
			boolean b = false;
			while(this.frowset.next())
			{		
				b = true;				
			    String chilecode=this.frowset.getString("codesetid");
			    String codeitemid=this.frowset.getString("codeitemid");
			    String corcode=this.frowset.getString("corcode");
			    String parentid=this.frowset.getString("parentid");
			    String codeitemdesc=this.frowset.getString("codeitemdesc");
			    if(chilecode!=null)
			    {
			    	//if(codesetid.equalsIgnoreCase(chilecode)){
				    	if(parentid!=null)
				    	{
				    		len=codeitemid.trim().length()-parentid.trim().length();
				    	}
				    	else
				    	{
				    		len=codeitemid.trim().length();
				    	}				    	
				    	this.getFormHM().put("codeitemdesc",codeitemdesc);
					    this.getFormHM().put("codeitemid",codeitemid);
					    this.getFormHM().put("corcode",corcode);
					    this.getFormHM().put("len",String.valueOf(len));
					    return ;
			    	
			    }				
		    }
		}catch(Exception e)
		{
		   e.printStackTrace();	
		}
		this.getFormHM().put("codeitemdesc","");
	    this.getFormHM().put("codeitemid","");
	    this.getFormHM().put("corcode","");
    }
    
    public String getCorcode(String codeitemid,String itemid){
    	String corcode="";
    	 String sql = "select "+itemid+" from K01 where e01a1 ='"+codeitemid+"'";
    	 //System.out.println(sql);
    	 try{
    		 ContentDAO dao = new ContentDAO(frameconn);
    		 this.frowset = dao.search(sql);
    		 if(frowset.next())
    			 corcode = frowset.getString(itemid);
    	 }catch(SQLException e){
    		 e.printStackTrace();
    	 }
    	return corcode;
    }
}
