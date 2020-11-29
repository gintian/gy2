/*
 * Created on 2005-7-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hjsj.hrms.transaction.dutyinfo;

import com.hjsj.hrms.utils.FormatValue;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.valueobject.common.FieldItemView;
import com.hjsj.hrms.valueobject.common.StationPosView;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class AddIniDutyInfoTrans extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		List rs=null;
		String setname="k01";		   
		String setprv="0";
		String code=(String)this.getFormHM().get("code");
		String kind=(String)this.getFormHM().get("kind");
		String I9999=(String)this.getFormHM().get("i9999");
		List infoFieldList=userView.getPrivFieldList(setname);   //获得当前子集的所有属性
		List infoSetList=userView.getPrivFieldSetList(Constant.POS_FIELD_SET);   //获得所有权限的子集
		List infoFieldViewList=new ArrayList();                  //保存处理后的属性
		String filename="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
				this.frowset = dao.search("select min(E01A1) as E01A1 from k01 where  E01A1 is not null and E01A1<>'' and e01a1 like '" + userView.getManagePrivCodeValue() + "%'");
			else
				this.frowset = dao.search("select min(E01A1) as E01A1 from k01 where  E01A1 is not null and E01A1<>''");
			if(this.frowset.next())
				code=this.frowset.getString("e01a1");
			if(code==null || code.trim().length()==0)
			{
				if(userView.getManagePrivCodeValue()!=null && userView.getManagePrivCodeValue().length()>0)
					this.frowset = dao.search("select min(codeitemid) as e01a1 from organization where  codeitemid is not null and codeitemid<>'' and codeitemid like '" + userView.getManagePrivCodeValue() + "%' and codesetid='@K'");
				else
					this.frowset = dao.search("select min(codeitemid) as e01a1 from organization where  codeitemid is not null and codeitemid<>''  and codesetid='@K'");
				if(this.frowset.next())
				{
					code=this.frowset.getString("E01A1");
				}
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
			    fieldItemV.setItemdesc(ResourceFactory.getProperty("workbench.pos.posname"));
			    if(isExistData)
				{
			    	LazyDynaBean rec=(LazyDynaBean)rs.get(0);
				    String codevalue=rec.get("e0122")!=null?rec.get("e0122").toString():"";
					fieldItemV.setItemid("E0122");
					fieldItemV.setValue(codevalue);
					if(codevalue !=null && codevalue.trim().length()>0)
					   fieldItemV.setViewvalue(AdminCode.getCode("UM",codevalue)!=null?AdminCode.getCode("UM",codevalue).getCodename():"");
				    else
					   fieldItemV.setViewvalue("");
				}
			    else
			    {
			    	
			    	fieldItemV.setItemid("E0122");
			    	StationPosView posview=getStationPos(code,this.getFrameconn());
			    	if(posview!=null)
			    	{
				    	fieldItemV.setValue(posview.getItemvalue());
				    	if(posview.getItemvalue() !=null && posview.getItemvalue().trim().length()>0)
							   fieldItemV.setViewvalue(AdminCode.getCode("UM",posview.getItemvalue())!=null?AdminCode.getCode("UM",posview.getItemvalue()).getCodename():"");
						    else
							   fieldItemV.setViewvalue("");
			    	}
			    }
				infoFieldViewList.add(fieldItemV);
			    	for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
					{
					    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);
					    if(fieldItem.getPriv_status() !=0  && ("e01a1".equalsIgnoreCase(fieldItem.getItemid())))
					    {
					    	FieldItemView fieldItemView=new FieldItemView();
					    	fieldItemView.setItemdesc(fieldItem.getItemdesc());
					    	fieldItemView.setItemtype("A");
					    	fieldItemView.setPriv_status(1);
					    	if(code !=null && code.trim().length()>0 && fieldItem.getCodesetid()!=null && fieldItem.getCodesetid().trim().length()>0)
					    	{
							  fieldItemView.setViewvalue(AdminCode.getCode(fieldItem.getCodesetid(),code)!=null?AdminCode.getCode(fieldItem.getCodesetid(),code).getCodename():"");
					    	  fieldItemView.setValue(AdminCode.getCode(fieldItem.getCodesetid(),code)!=null?AdminCode.getCode(fieldItem.getCodesetid(),code).getCodename():"");					    	
					    	}
					    	else
					    	{
					    		fieldItemView.setValue("");
						    	fieldItemView.setViewvalue("");
					    	}					    		
					    	infoFieldViewList.add(fieldItemView);
					    	break;
					    }
					}
				for(int i=0,j=0;i<infoFieldList.size();i++)                            //字段的集合
				{
				    FieldItem fieldItem=(FieldItem)infoFieldList.get(i);				    
					if(fieldItem.getPriv_status() !=0 && (!"e01a1".equalsIgnoreCase(fieldItem.getItemid()) &&  !( "e0122".equalsIgnoreCase(fieldItem.getItemid()))))                //只加在有读写权限的指标
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
				        //在struts用来表示换行的变量
					    fieldItemView.setRowflag(String.valueOf(infoFieldList.size()-1));
					    fieldItemView.setFillable(fieldItem.isFillable());
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
								}else if(this.getFrowset().getString(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==4)
								{
									fieldItemView.setViewvalue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								    fieldItemView.setValue(new FormatValue().format(fieldItem,rec.get(fieldItem.getItemid().toLowerCase()).toString().substring(0,4)));
								}else if(this.getFrowset().getString(fieldItem.getItemid())!=null && rec.get(fieldItem.getItemid()).toString().length()>=10 && fieldItem.getItemlength()==7)
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
	      // this.getFormHM().put("kind",kind);
       }
}
	private StationPosView getStationPos(String code,Connection conn)
	{
		
		
		String pre="@K";	
		Statement stmt = null;
		ResultSet rs=null;
		boolean ispos=false;
		boolean isdep=false;
		boolean isorg=false;
		StringBuffer strsql=new StringBuffer();
		try{
		    ContentDAO db=new ContentDAO(conn);
		    if(code!=null)
		    {
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
		    return null;
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
}
