/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.pos.standardpos;

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
import com.hrms.hjsj.sys.*;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
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
		String filename="";
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
				String codeitemdesc=(String)this.getFormHM().get("codeitemdesc");//岗/职位名称
				corcode=corcode!=null&&corcode.length()>0?corcode:"";
				String len=(String)this.getFormHM().get("len");
				len=len!=null&&len.length()>0?len:"30";
				FieldItemView fieldItemV=new FieldItemView();
				/*fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.syscode"));
				fieldItemV.setItemid("codeitemid");
				fieldItemV.setItemtype("A");
			    fieldItemV.setPriv_status(1);	
			    fieldItemV.setViewvalue(codeitemid);
			 	fieldItemV.setValue(codeitemid);	
			 	fieldItemV.setItemlength(Integer.parseInt(len));
			 	fieldItemV.setCodesetid("0");
			 	fieldItemV.setFillable(true);
			 	infoFieldViewList.add(fieldItemV);*/
			 	
			 	/*****岗位代码*****/
			 	
			 	int pos_code_len=30;
			 	String pos_code_desc="";
			 	FieldItem item=null;
				if(pos_code_field!=null&&pos_code_field.length()>0&&!"#".equals(pos_code_field))
				{
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
			 	infoFieldViewList.add(fieldItemV);			 	
			 	fieldItemV=new FieldItemView();
				fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.doty.pos"));
				fieldItemV.setItemtype("A");
				fieldItemV.setItemid("codeitemdesc");
			    fieldItemV.setPriv_status(1);	
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
				strsql.append("select * from ");
				strsql.append(setname);
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
			    //fieldItemV.setCodesetid("0");
			    fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.pos.posname"));
			    if(isExistData)
				{
			    	if(!"vorg".equals(orgtype))
			    	{
			    		LazyDynaBean rec=(LazyDynaBean)rs.get(0);
						String codevalue=rec.get("e0122")!=null?rec.get("e0122").toString():"";
						fieldItemV.setItemid("E0122");
						fieldItemV.setValue(codevalue);
						if(codevalue !=null && codevalue.trim().length()>0)
						   fieldItemV.setViewvalue(AdminCode.getCode("UM",codevalue)!=null?AdminCode.getCode("UM",codevalue).getCodename():"");
					    else
						   fieldItemV.setViewvalue("");
			    	}else
			    	{
			    		AddOrgInfo addOrgInfo=new AddOrgInfo(this.getFrameconn());
			    		StationPosView posview=getStationVPos(code,this.getFrameconn());
			    		if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
				 		{
				 			fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
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
						   fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
				    		//fieldItemV.setViewvalue(posview.getItemviewvalue());
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
				 			fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
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
			    /*for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
				{
					    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
					    if(fieldItem.getPriv_status() !=0  && (fieldItem.getItemid().equalsIgnoreCase("e01a1"))) 
					    {
					    	FieldItemView fieldItemView=new FieldItemView();
					    	fieldItemView.setItemdesc(fieldItem.getItemdesc());
					    	fieldItemView.setItemtype("A");
					    	//fieldItemView.setCodesetid("@K");
					    	fieldItemView.setPriv_status(1);
					    	if(code !=null && code.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
					    	{
					    		//StationPosView posview=getStationK(code,this.getFrameconn());
							  fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),code)!=null?AdminCode.getCode(fieldItem.getCodesetid(),code).getCodename():"");
					    	  fieldItemView.setValue(AdminCode.getCode(fieldItem.getCodesetid(),code)!=null?AdminCode.getCode(fieldItem.getCodesetid(),code).getCodename():"");
					    		//fieldItemView.setValue(posview.getItemvalue());
					    		//fieldItemView.setViewvalue(posview.getItemviewvalue());
					    	}
					    	else
					    	{
					    		fieldItemView.setValue("");
						    	fieldItemView.setViewvalue("");
					    	}					    		
					    	infoFieldViewList.add(fieldItemView);
					    	break;
					    }
				   }*/
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
			/*for(int p=0;p<infoSetList.size();p++)
			{
				FieldSet fieldset=(FieldSet)infoSetList.get(p);
				if(setname.equalsIgnoreCase(fieldset.getFieldsetid()))
				{
					setprv=String.valueOf(fieldset.getPriv_status());
					break;
				}
			}*/
			setprv=getEditSetPriv(infoSetList,infoFieldList,setname);
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
				pre=rs.getString("codesetid");
				if("UM".equalsIgnoreCase(pre)||code.equals(rs.getString("parentid")))
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
				if("UM".equalsIgnoreCase(pre))
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
					if("UM".equalsIgnoreCase(pre))
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
	    String setname="h01";
		String setprv="0";
		String a_code=(String)this.getFormHM().get("a_code");
		String kind=(String)this.getFormHM().get("kind");		
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.JOB_FIELD_SET);   //获得所有权限的子集	
		List infoFieldViewList=new ArrayList(); 
		String filename="";
		if(a_code.length()==2)
		{
		   throw GeneralExceptionHandler.Handle(new Exception("不能在根目录下新建基准岗位！请先选择左侧的分类！"));
		}
		String codesetid = a_code.substring(0,2);
		String parentid= a_code.substring(2);
		getCodeitem(parentid,codesetid);
		String codeitemid=(String)this.getFormHM().get("codeitemid");//系统代码
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
	 	
	 	fieldItemV=new FieldItemView();
		fieldItemV.setItemdesc(ResourceFactory.getProperty("kq.item.name"));
		fieldItemV.setItemtype("A");
		fieldItemV.setItemid("codeitemdesc");
	    fieldItemV.setPriv_status(2);	
	    fieldItemV.setViewvalue("");
	 	fieldItemV.setValue("");	
	 	fieldItemV.setItemlength(50);
	 	fieldItemV.setCodesetid("0");
	 	fieldItemV.setFillable(true);
	 	infoFieldViewList.add(fieldItemV);
	 	
	 	for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
		{
		    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
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
    private void getCodeitem(String code,String codesetid)throws GeneralException
    {
    	
		String first="1";
		int len=30;
		StringBuffer strsql=new StringBuffer();
		strsql.append("select codesetid,codeitemid,codeitemdesc,parentid,childid,a0000,corcode from codeitem where parentid='");
		strsql.append(code);
		strsql.append("' and codesetid='"+codesetid);
		strsql.append("' and codeitemid<>parentid ");
		strsql.append(" order by a0000");
		
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
					    this.getFormHM().put("codeitemid",codeitemid);
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
		    
		      if(code!=null && code.trim().length()>0)
		      {
		        len=30-code.trim().length();
		    	this.getFormHM().put("first",first);
		      }
		     
		      AddOrgInfo addOrgInfo=new AddOrgInfo();
		      codeitemid=addOrgInfo.getChildCodeitemid(codeitemid,code,len);
		      this.getFormHM().put("codeitemid",codeitemid);
		    }
		}catch(Exception e){
		   e.printStackTrace();
		   throw GeneralExceptionHandler.Handle(e);
		}
		this.getFormHM().put("len",String.valueOf(len));	
		//this.getFormHM().put("codeitemid","");
		this.getFormHM().put("codesetid",codesetid);
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
}
