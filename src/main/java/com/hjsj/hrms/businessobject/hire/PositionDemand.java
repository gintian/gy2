package com.hjsj.hrms.businessobject.hire;

import com.hjsj.hrms.businessobject.hire.zp_options.ZpCondTemplateXMLBo;
import com.hjsj.hrms.businessobject.sys.EMailBo;
import com.hjsj.hrms.businessobject.sys.SmsBo;
import com.hjsj.hrms.module.utils.exportexcel.ExportExcelUtil;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.db.DBMetaModel;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.*;
import com.hrms.hjsj.utils.FactorList;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.constant.SystemConfig;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import javax.sql.RowSet;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
public class PositionDemand {
	private Connection conn=null;
	private UserView userView=null;
	/**数据库服务器标志*/
    private static int dbflag=Constant.MSSQL;
	static
	{
	       dbflag=Sql_switcher.searchDbServer();

	}	
	
	
	public PositionDemand(Connection conn) {
		this.conn=conn;
	}
	public PositionDemand(Connection conn,UserView userView) {
		this.conn=conn;
		this.userView=userView;
	}
	
	//根据id返回组织的类型
	public String getCodesetByID(String codeid)
	{
		String codesetid="";
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			RowSet rowSet=dao.search("select * from organization where codeitemid='"+codeid+"'");
			if(rowSet.next()) {
                codesetid=rowSet.getString("codesetid");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return codesetid;
	}
	
	
	
//	根据id找直属单位
	public String getUnitCode(String codeid)
	{
		String un_code_id="";
		boolean flag=true;
		ContentDAO dao=null;
		try
		{
			RowSet rowSet=null;
			dao=new ContentDAO(this.conn);
			while(flag)
			{
				rowSet=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeid+"')");
				if(rowSet.next())
				{
					String codesetid=rowSet.getString("codesetid");
					String codeitemid=rowSet.getString("codeitemid");
					codeid=codeitemid;
					if("UN".equalsIgnoreCase(codesetid))
					{
						un_code_id=codeitemid;
						flag=false;
					}
				}
				else {
                    flag=false;
                }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return un_code_id;
	}
	
	public RecordVo getRecordVo(RecordVo vo,ArrayList resumeFieldList)
	{
		LazyDynaBean rec0=(LazyDynaBean)resumeFieldList.get(1);
		String avalue=(String)rec0.get("value");
		String acodesetid=getCodesetByID(avalue);
		if("UN".equals(acodesetid))
		{
			vo.setString("z0321",avalue);
		}
		else if("UM".equals(acodesetid))
		{
			vo.setString("z0325",avalue);
			vo.setString("z0321",getUnitCode(avalue));
		}
		
		LazyDynaBean rec1=(LazyDynaBean)resumeFieldList.get(2);
		vo.setString("z0311",(String)rec1.get("value"));
		String firstlevelid=SystemConfig.getPropertyValue("firstlevelfield");
		String second=SystemConfig.getPropertyValue("secondlevelfield");
		this.getLink(avalue, 1);
		for(int i=3;i<resumeFieldList.size();i++)
		{
			LazyDynaBean rec=(LazyDynaBean)resumeFieldList.get(i);
			String itemtype=(String)rec.get("itemtype");
			String itemid=(String)rec.get("itemid");
			String value=(String)rec.get("value");
			String decimalwidth=(String)rec.get("decimal");
			String codesetid=(String)rec.get("codesetid");		
			if(codesetid!=null&& "UM".equalsIgnoreCase(codesetid))
			{
				if(value==null|| "".equals(value))
				{
		    		if(firstlevelid!=null&&!"".equals(firstlevelid)&&firstlevelid.equalsIgnoreCase(itemid))
		    		{
			    		
			    		if(linkMap.get((maxLay+""))!=null)
			    		{
			    			String ff=(String)linkMap.get(maxLay+"");
			    			vo.setString(itemid.toLowerCase(),((String)linkMap.get(maxLay+"")));
			    		}
			    		else {
                            vo.setString(itemid.toLowerCase(),"");
                        }
		    		}
		    		 if(second!=null&&!"".equals(second)&&second.equalsIgnoreCase(itemid))
			    	{
		    			 if(linkMap.get((maxLay-1)+"")!=null)
				    		{
				    			vo.setString(itemid.toLowerCase(),((String)linkMap.get((maxLay-1)+"")));
				    		}
				    		else {
                             vo.setString(itemid.toLowerCase(),"");
                         }
			    	}
		    		else
		    		{
		    			vo.setString(itemid.toLowerCase(),value);
		    		}
				}
				else
				{
					vo.setString(itemid.toLowerCase(),value);
				}
				
			}
			else if(value!=null&&value.trim().length()>0)
			{
				if("A".equals(itemtype))
				{
					vo.setString(itemid.toLowerCase(),value);
				}
				else if("D".equals(itemtype))
				{
					String[] dd=value.split("-");
					Calendar d=Calendar.getInstance();
					d.set(Calendar.YEAR,Integer.parseInt(dd[0]));
					d.set(Calendar.MONTH,Integer.parseInt(dd[1])-1);
					d.set(Calendar.DATE,Integer.parseInt(dd[2]));
					vo.setDate(itemid.toLowerCase(),d.getTime());
				}
				else if("M".equals(itemtype))
				{
					vo.setString(itemid.toLowerCase(),value);
				}
				else if("N".equals(itemtype))
				{
					if("0".equals(decimalwidth))
					{
						vo.setInt(itemid.toLowerCase(),Integer.parseInt(value));
					}
					else
					{
						vo.setDouble(itemid.toLowerCase(),Double.parseDouble(value));
					}
				}
			}
			else if("A".equals(itemtype)|| "M".equals(itemtype)) {
                vo.setString(itemid.toLowerCase(),null);
            }
		}
		return vo;
	}
	private HashMap linkMap = new HashMap();
	private int maxLay=0;
	public void getLink(String value,int lay)
	{
		try
		{
			
			String sql = "select codeitemid,parentid from organization where codesetid='UM' and codeitemid='"+value+"'";
		    ContentDAO dao = new ContentDAO(this.conn);
		    RowSet rs = dao.search(sql);
		    while(rs.next())
		    {
		    	linkMap.put(lay+"", value);
		    	getLink(rs.getString("parentid"),++lay);
		    }
		    if(lay>maxLay&&linkMap.get(lay+"")!=null) {
                maxLay=lay;
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getSecondUM(String value)
	{
		String avalue="";
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			RowSet rowSet=dao.search("select codeitemid from  organization where codesetid='UM' and parentid='"+value+"'");
			if(rowSet.next())
			{
				if(rowSet.getString("codeitemid")!=null) {
                    avalue=rowSet.getString("codeitemid");
                }
			}
			else
			{
				avalue=value;
			}
			rowSet.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return avalue;
	}
	
	
	
	/**
	 * 
	 * @param opt   //  0:添加  1：修改
	 * @param positionDemandDescList 	用工需求 指标列表
	 * @param isRevert   		是否回复 0：不回复  1：回复
	 * @param mailTemplateID    //邮件模版号
	 */
	public String  addPositionDemand(String opt,ArrayList positionDemandDescList,String isRevert,String mailTemplateID,String z0301)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		String az0301="";
		try
		{
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(conn,"1");
			HashMap map=parameterXMLBo.getAttributeValues();
			String hireMajor="";
			if(map!=null&&(map.get("hireMajor"))!=null)
			{
				hireMajor=(String)map.get("hireMajor");
			}
			String hireMajorCode="";
			if(map!=null&&(map.get("hireMajorCode"))!=null)
			{
				hireMajorCode=(String)map.get("hireMajorCode");
			}
			
			//邮件回复
			HashMap nodeMap=new HashMap();
			LazyDynaBean abean=new LazyDynaBean();
			if("0".equals(isRevert)) {
                abean.set("flag","false");
            } else {
                abean.set("flag","true");
            }
			abean.set("template",mailTemplateID);
			nodeMap.put("answer_mail",abean);
			
			RecordVo vo=new RecordVo("z03");
			if("0".equals(opt))
			{
				 IDGenerator idg=new IDGenerator(2,this.conn);
    			 String a_z0301=idg.getId("Z03.Z0301");
    			 az0301=a_z0301;
    			 vo.setString("z0301",a_z0301);	
    			 z0301=a_z0301;
    			 vo.setDate("z0307",Calendar.getInstance().getTime());
				 //vo=getRecordVo(vo,positionDemandDescList);
    			 LazyDynaBean rec0=(LazyDynaBean)positionDemandDescList.get(1); //author:dengcan
    				String avalue=(String)rec0.get("value");
    				String acodesetid=getCodesetByID(avalue);
    				if("UN".equals(acodesetid))
    				{
    					vo.setString("z0321",avalue);
    				}
    				else if("UM".equals(acodesetid))
    				{
    					vo.setString("z0325",avalue);
    					vo.setString("z0321",getUnitCode(avalue));
    				}
    				
    				LazyDynaBean rec1=(LazyDynaBean)positionDemandDescList.get(2); //author:dengcan
    				vo.setString("z0311",(String)rec1.get("value"));
    				HashMap kValueMap = this.getKInfoMap((String)rec1.get("value"));
    				String firstlevelid=SystemConfig.getPropertyValue("firstlevelfield");
    				String second=SystemConfig.getPropertyValue("secondlevelfield");
    				this.getLink(avalue, 1);
    				String z0336="";
    				for(int i=0;i<positionDemandDescList.size();i++)
    				{
    					if(i==1||i==2) {
                            continue;
                        }
    					LazyDynaBean rec=(LazyDynaBean)positionDemandDescList.get(i);
    					String itemtype=(String)rec.get("itemtype");
    					String itemid=(String)rec.get("itemid");
    					String value = (String)rec.get("value");
    					if(itemid.equals(hireMajor) && !"".equals(hireMajorCode) && hireMajorCode!=null){
    						value = getViewValue(hireMajorCode,value);
    					}
    					String decimalwidth=(String)rec.get("decimal");
    					String codesetid=(String)rec.get("codesetid");	
    					if("z0336".equalsIgnoreCase(itemid)) {
                            z0336=value;
                        }
    					if(codesetid!=null&& "UM".equalsIgnoreCase(codesetid))
    					{
    						if(value==null|| "".equals(value))
    						{
    				    		if(firstlevelid!=null&&!"".equals(firstlevelid)&&firstlevelid.equalsIgnoreCase(itemid))
    				    		{
    					    		
    					    		if(linkMap.get((maxLay+""))!=null)
    					    		{
    					    			vo.setString(itemid.toLowerCase(),((String)linkMap.get(maxLay+"")));
    					    		}
    					    		else {
                                        vo.setString(itemid.toLowerCase(),"");
                                    }
    				    		}
    				    		else if(second!=null&&!"".equals(second)&&second.equalsIgnoreCase(itemid))
    					    	{
    				    			 if(linkMap.get((maxLay==1?1:(maxLay-1))+"")!=null)
    						    		{
    						    			vo.setString(itemid.toLowerCase(),((String)linkMap.get((maxLay==1?1:(maxLay-1))+"")));
    						    		}
    						    		else {
                                         vo.setString(itemid.toLowerCase(),"");
                                     }
    					    	}
    				    		else
    				    		{
    				    			vo.setString(itemid.toLowerCase(),value);
    				    		}
    						}
    						else
    						{
    							vo.setString(itemid.toLowerCase(),value);
    						}
    						
    					}
    					else if(value!=null&&value.trim().length()>0)
    					{
    						if("A".equals(itemtype))
    						{
    							vo.setString(itemid.toLowerCase(),value);
    						}
    						else if("D".equals(itemtype))
    						{
    							String[] dd=value.split("-");
    							Calendar d=Calendar.getInstance();
    							d.set(Calendar.YEAR,Integer.parseInt(dd[0]));
    							d.set(Calendar.MONTH,Integer.parseInt(dd[1])-1);
    							d.set(Calendar.DATE,Integer.parseInt(dd[2]));
    							vo.setDate(itemid.toLowerCase(),d.getTime());
    						}
    						else if("M".equals(itemtype))
    						{
    							vo.setString(itemid.toLowerCase(),value);
    						}
    						else if("N".equals(itemtype))
    						{
    							if("0".equals(decimalwidth))
    							{
    								vo.setInt(itemid.toLowerCase(),Integer.parseInt(value));
    							}
    							else
    							{
    								vo.setDouble(itemid.toLowerCase(),Double.parseDouble(value));
    							}
    						}
    					}
    					else if("A".equals(itemtype)|| "M".equals(itemtype)) {
                            vo.setString(itemid.toLowerCase(),null);
                        }
    				}
				 vo.setString("z0319","01");
				 /*******************引入岗位协议***********************/
				 if(kValueMap!=null&&!"01".equals(z0336))
 				 {
 					SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
 					Set keySet = kValueMap.keySet();
 					for(Iterator t=keySet.iterator();t.hasNext();)
 					{
 						String key=(String)t.next();
 						String value=(String)kValueMap.get(key);
 						if(value!=null&&value.length()>0)
 						{
 							FieldItem item = DataDictionary.getFieldItem(key.toLowerCase());
 							if("N".equals(item.getItemtype()))
 							{
 								if(item.getDecimalwidth()==0) {
                                    vo.setInt(key, Integer.parseInt(value));
                                } else {
                                    vo.setDouble(key,Double.parseDouble(value));
                                }
 							}else if("D".equals(item.getItemtype()))
 							{
 								vo.setDate(key, fomat.parse(value));
 							}else {
                                vo.setString(key,value);
                            }
 						}
 					}
 				 }
				 dao.addValueObject(vo);
				
				 
				 DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.conn,"-1");				
				 bo.createParamXml("answer_mail",nodeMap,z0301);
				 
			}
			else
			{
				az0301=z0301;
				vo.setString("z0301",z0301);
				vo=dao.findByPrimaryKey(vo);
				//vo=getRecordVo(vo,positionDemandDescList);
				 LazyDynaBean rec0=(LazyDynaBean)positionDemandDescList.get(1);
 				String avalue=(String)rec0.get("value");
 				String acodesetid=getCodesetByID(avalue);
 				if("UN".equals(acodesetid))
 				{
 					vo.setString("z0321",avalue);
 				}
 				else if("UM".equals(acodesetid))
 				{
 					vo.setString("z0325",avalue);
 					vo.setString("z0321",getUnitCode(avalue));
 				}
 				
 				LazyDynaBean rec1=(LazyDynaBean)positionDemandDescList.get(2);
 				vo.setString("z0311",(String)rec1.get("value"));
 				String firstlevelid=SystemConfig.getPropertyValue("firstlevelfield");
 				String second=SystemConfig.getPropertyValue("secondlevelfield");
 				this.getLink(avalue, 1);
 				for(int i=0;i<positionDemandDescList.size();i++)
 				{
 					if(i==1||i==2) {
                        continue;
                    }
 					LazyDynaBean rec=(LazyDynaBean)positionDemandDescList.get(i);
 					String itemtype=(String)rec.get("itemtype");
 					String itemid=(String)rec.get("itemid");
 					String value=(String)rec.get("value");
 					String decimalwidth=(String)rec.get("decimal");
 					String codesetid=(String)rec.get("codesetid");	
 					if(itemid.equals(hireMajor) && !"".equals(hireMajorCode) && hireMajorCode!=null){
						value = getViewValue(hireMajorCode,value);
					}
 					if(codesetid!=null&& "UM".equalsIgnoreCase(codesetid))
 					{
 						if(value==null|| "".equals(value))
 						{
 				    		if(firstlevelid!=null&&!"".equals(firstlevelid)&&firstlevelid.equalsIgnoreCase(itemid))
 				    		{
 					    		
 					    		if(linkMap.get((maxLay+""))!=null)
 					    		{
 					    			String ff=(String)linkMap.get(maxLay+"");
 					    			vo.setString(itemid.toLowerCase(),((String)linkMap.get(maxLay+"")));
 					    		}
 					    		else {
                                    vo.setString(itemid.toLowerCase(),"");
                                }
 				    		}
 				    		 if(second!=null&&!"".equals(second)&&second.equalsIgnoreCase(itemid))
 					    	{
 				    			 if(linkMap.get((maxLay-1)+"")!=null)
 						    		{
 						    			vo.setString(itemid.toLowerCase(),((String)linkMap.get((maxLay-1)+"")));
 						    		}
 						    		else {
                                     vo.setString(itemid.toLowerCase(),"");
                                 }
 					    	}
 				    		else
 				    		{
 				    			vo.setString(itemid.toLowerCase(),value);
 				    		}
 						}
 						else
 						{
 							vo.setString(itemid.toLowerCase(),value);
 						}
 						
 					}
 					else if(value!=null&&value.trim().length()>0)
 					{
 						if("A".equals(itemtype))
 						{
 							vo.setString(itemid.toLowerCase(),value);
 						}
 						else if("D".equals(itemtype))
 						{
 							String[] dd=value.split("-");
 							Calendar d=Calendar.getInstance();
 							d.set(Calendar.YEAR,Integer.parseInt(dd[0]));
 							d.set(Calendar.MONTH,Integer.parseInt(dd[1])-1);
 							d.set(Calendar.DATE,Integer.parseInt(dd[2]));
 							vo.setDate(itemid.toLowerCase(),d.getTime());
 						}
 						else if("M".equals(itemtype))
 						{
 							vo.setString(itemid.toLowerCase(),value);
 						}
 						else if("N".equals(itemtype))
 						{
 							if("0".equals(decimalwidth))
 							{
 								vo.setInt(itemid.toLowerCase(),Integer.parseInt(value));
 							}
 							else
 							{
 								vo.setDouble(itemid.toLowerCase(),Double.parseDouble(value));
 							}
 						}
 					}
 					else if("A".equals(itemtype)|| "M".equals(itemtype)) {
                        vo.setString(itemid.toLowerCase(),null);
                    }
 				}
				dao.updateValueObject(vo);
				
				DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.conn,z0301);
				bo.updateNode("answer_mail",nodeMap,z0301);
			}
		
		
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return az0301;
	}
	public String  modifyPositionDemand(ArrayList positionDemandDescList,String z0301)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		String az0301="";
		try
		{
			RecordVo vo=new RecordVo("z03");
			az0301=z0301;
			vo.setString("z0301",z0301);
			vo=dao.findByPrimaryKey(vo);
			vo=getRecordVo(vo,positionDemandDescList);
			dao.updateValueObject(vo);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return az0301;
	}
	
	
	/**
	 * 得到邮件模版列表
	 * @return
	 */
	public ArrayList getMailTemplateList()
	{
		ArrayList mailTemplateList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		try
		{
			mailTemplateList.add(new CommonData("","--选择邮件模板--"));
			
			RowSet rowSet=dao.search("select template_id,name from t_sys_msgtemplate where template_type=0");
			while(rowSet.next())
			{
				mailTemplateList.add(new CommonData(rowSet.getString("template_id"),rowSet.getString("name")));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return mailTemplateList;
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 判断该代码是否是多层的
	 * @param multilayerCodeSetMap
	 * @param codeSetID
	 * @return 0：一层  1：多层
	 */
	public String getIsMore(HashMap multilayerCodeSetMap,String codeSetID)
	{
		String isMore="0";
		try
		{
			if("0".equals(codeSetID)|| "UN".equals(codeSetID)|| "UM".equals(codeSetID)|| "@K".equals(codeSetID)) {
                return isMore;
            }
			
			if(multilayerCodeSetMap!=null&&multilayerCodeSetMap.get(codeSetID)!=null) {
                isMore="1";
            }
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return isMore;
	}
	
	
	/**
	 * 取得系统中所有多层代码 的信息
	 * @param flag 0: 代码 1：单位 
	 * @return
	 */
	public HashMap getMultilayerCodeSetMap()
	{
		HashMap map=new HashMap();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			rowSet=dao.search("select distinct codesetid from codeitem  where codeitemid<>parentid");
			while(rowSet.next()) {
                map.put(rowSet.getString("codesetid"),"1");
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * 根据 应聘条件 组成sql语句
	 * @param posConditionList
	 * @return
	 */
	public String getSqlByCondition(ArrayList posConditionList,String dbname,String upValue,String vague)
	{
		StringBuffer sql=new StringBuffer("");
		StringBuffer sql_select=new StringBuffer("select distinct "+dbname+"A01.a0100 ");
		StringBuffer sql_from=new StringBuffer("");
		StringBuffer sql_where=new StringBuffer("");
		
		
		HashMap setMap=new HashMap();
		setMap.put("a01","1");
		for(int j=0;j<posConditionList.size();j++)
   		{
   			
   			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(j);
   			String itemid=(String)abean.get("itemid");
   			String setname=(String)abean.get("setname");
   			String codesetid=(String)abean.get("codesetid");
   			String s_value=(String)abean.get("s_value");
   			String e_value=(String)abean.get("e_value");
   			String isMore=(String)abean.get("isMore");
   			String itemtype=(String)abean.get("fieldType");
   			String flag=(String)abean.get("flag");			//false|true,(按区间｜不按区间)
   			String type=(String)abean.get("type");			// 1:以上   0：未填
   			String view_s_value=(String)abean.get("view_s_value");
   			String view_e_value=(String)abean.get("view_e_value");
		    s_value=PubFunc.getStr(s_value);
		    e_value=PubFunc.getStr(e_value);
		    view_s_value=PubFunc.getStr(view_s_value);
		    view_e_value=PubFunc.getStr(view_e_value);
   			boolean isFlag=false;       //单独的子集指标    			
   			{
   				LazyDynaBean abean0=null;
   				String setname0="";
   				LazyDynaBean abean2=null;
   				String setname2="";
   				if((j+1)<posConditionList.size())
   				{
   					abean2=(LazyDynaBean)posConditionList.get(j+1);
   					setname2=(String)abean2.get("setname");
   					
   				}
   				if(j!=0)
   				{
   					abean0=(LazyDynaBean)posConditionList.get(j-1);
   					setname0=(String)abean0.get("setname");
   				}
   				if(abean0==null&&abean2==null)//abean0=null意味着j=0 abean2=null意味着是最后一个两者同时等于null说明只有一条过滤条件
                {
                    isFlag=true;
                } else if((abean0==null&&abean2!=null)&&!setname2.equals(setname)) {
                    isFlag=true;
                } else if((abean0!=null&&abean2==null)&&!setname0.equals(setname)) {
                    isFlag=true;
                } else if(abean0!=null&&abean2!=null&&!setname2.equals(setname)&&!setname0.equals(setname)) {
                    isFlag=true;
                }
   			}
   			
   		//	setMap.put(setname.toLowerCase(),"1");  			
   			if("A01".equalsIgnoreCase(setname)||isFlag)
   			{
   				if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid)&& "1".equals(isMore))
   				{
   					if(view_s_value==null||view_s_value.trim().length()==0) {
                        s_value="";
                    }
   					if(view_e_value==null||view_e_value.trim().length()==0) {
                        e_value="";
                    }
   				}
   				if(s_value.trim().length()>0||e_value.trim().length()>0) {
                    setMap.put(setname.toLowerCase(),"1");
                }
   				sql_where.append(getSql(type,flag,itemtype,codesetid,s_value,e_value,itemid,vague));	
   			}
   			else
   			{
   				int a=j+1;
   				int size=1;   //子集指标个数
   				while(a<posConditionList.size())
   				{
   					LazyDynaBean abean1=(LazyDynaBean)posConditionList.get(a);
   		   			String itemid1=(String)abean1.get("itemid");
   		   			if(itemid1.equals(itemid)) {
                        break;
                    }
   					size++;
   					a++;
   				}
   				sql_where.append(" and ( ");
   				StringBuffer tempSql=new StringBuffer("");
   				int c=0;
   			//	for(int c=0;c<3;c++)
   				{
   					boolean isValue=false;
   					for(int b=0;b<size;b++)
   					{
   						LazyDynaBean abean2=(LazyDynaBean)posConditionList.get(j+b+c*size);
   						
   						String itemtype2=(String)abean2.get("fieldType");  			   			
   			   			String view_s_value2=(String)abean2.get("view_s_value");
   			   			String s_value2=(String)abean2.get("s_value");
   			   			String codesetid2=(String)abean2.get("codesetid");
   			   			String isMore2=(String)abean2.get("isMore");
   			   			String setname2=(String)abean2.get("setname");
   			   			String type2=(String)abean2.get("type");			// 1:以上   0：未填
	   			   		if("A".equalsIgnoreCase(itemtype2)&&!"0".equals(codesetid2)&& "1".equals(isMore2))
	   	   				{
	   	   					if(view_s_value2==null||view_s_value2.trim().length()==0) {
                                s_value2="";
                            }
	   	   				}
	   			   		if(s_value2!=null&&s_value2.trim().length()>0)
	   			   		{	
	   			   			isValue=true;
	   			   			setMap.put(setname2.toLowerCase(),"1");
	   			   			continue;//不知道  这里为什么写成break，如果写成break就会导致存在这种代码类的时候，后面的子集的无法被拼接到sql
	   			   			//break;
	   			   		}
   					}
   					if(isValue)
   					{
	   					StringBuffer temp2=new StringBuffer("");
	   					for(int b=0;b<size;b++)
	   					{
	   						LazyDynaBean abean2=(LazyDynaBean)posConditionList.get(j+b+c*size);
	   						String itemid2=(String)abean2.get("itemid");
	   						String itemtype2=(String)abean2.get("fieldType");  			   			
	   			   			String view_s_value2=(String)abean2.get("view_s_value");
	   			   			String s_value2=(String)abean2.get("s_value");
	   			   			String codesetid2=(String)abean2.get("codesetid");
	   			   			String flag2=(String)abean2.get("flag");	
	   			   			String e_value2=(String)abean2.get("e_value");
	   			   			String isMore2=(String)abean2.get("isMore");
	   			   			String setname2=(String)abean2.get("setname");
	   			   			String type2=(String)abean2.get("type");			// 1:以上   0：未填
		   			   		if("A".equalsIgnoreCase(itemtype2)&&!"0".equals(codesetid2)&& "1".equals(isMore2))
		   	   				{
		   	   					if(view_s_value2==null||view_s_value2.trim().length()==0) {
                                    s_value2="";
                                }
		   	   				}
		   			   		if(s_value2!=null&&s_value2.trim().length()>0)
		   			   		{
				   			   	if("A".equalsIgnoreCase(itemtype2))
				   			   	{
				   			   		if("0".equals(codesetid2))
				   			   		{
				   			   			if("1".equals(vague))
				   			   			{
				   			   				if(s_value2.indexOf("|")!=-1)
				   			   				{
				   			   					String[] temps=s_value2.split("\\|");
				   			   					StringBuffer ss=new StringBuffer("");
				   			   					for(int n=0;n<temps.length;n++)
				   			   					{
				   			   						if(temps[n].trim().length()>0)
				   			   						{
				   			   							ss.append(" or "+itemid2+" like '%"+temps[n]+"%'");
				   			   							
				   			   						}
				   			   					}
				   			   					temp2.append(" and ( "+ss.substring(3)+" )");
				   			   				}
				   			   				else {
                                                temp2.append(" and "+itemid2+" like '%"+s_value2+"%'");
                                            }
				   			   			
				   			   			}	
				   			   			else
				   			   			{
					   			   			if(s_value2.indexOf("|")!=-1)
				   			   				{
				   			   					String[] temps=s_value2.split("\\|");
				   			   					StringBuffer ss=new StringBuffer("");
				   			   					for(int n=0;n<temps.length;n++)
				   			   					{
				   			   						if(temps[n].trim().length()>0)
				   			   						{
				   			   							ss.append(" or "+itemid2+"='"+temps[n]+"'");
				   			   							
				   			   						}
				   			   					}
				   			   					temp2.append(" and ( "+ss.substring(3)+" )");
				   			   				}
				   			   				else {
                                                temp2.append(" and "+itemid2+"='"+s_value2+"'");
                                            }
				   			   			}
				   			   		}
				   			   		else  //代码
				   			   		{
				   			   			if(type2!=null&& "1".equals(type2))  //以上
				   			   			{
				   			   				temp2.append(" and "+itemid2+"<='"+s_value2+"'");
				   			   			}
				   			   			else
				   			   			{
											temp2.append(" and "+itemid2+"='"+s_value2+"'");	
				   			   			}
				   			   			
				   			   		}
				   			   	//	temp2.append(" and "+itemid2+"='"+s_value2+"'");
				   			   	
				   			   	}
				   			   	else if("N".equalsIgnoreCase(itemtype2))
								{
									temp2.append(" and ( "+itemid2+"="+s_value2);
									if(Float.parseFloat(s_value2)==0) {
                                        temp2.append(" or "+itemid2+" is null ");
                                    }
									temp2.append(" ) ");
								}
								else if("D".equalsIgnoreCase(itemtype2))
								{
									s_value2=s_value2.replaceAll("\\.","-");
									if("false".equalsIgnoreCase(flag2)){
										temp2.append(getSql(type2,flag2,itemtype2,codesetid2,s_value2,e_value2,itemid2,vague));
									}else{
										temp2.append(" and "+getDataValue(itemid2,"=",s_value2));
									}
								}
		   			   		}
		   			   		
	   					}
	   					
	   					
	   					
	   					tempSql.append(" or ( "+temp2.substring(4)+" ) ");
	   					
   					}	   				
   				}
   				if(tempSql.length()>0) {
                    sql_where.append(tempSql.substring(3));
                } else {
                    sql_where.append(" 1=1 ");
                }
   				
   				
   				sql_where.append(" )");
   				
   				j=j+size*3-1;
   			}
   		}
		
		Set set=setMap.keySet();
		StringBuffer sql_where0=new StringBuffer("");
		for(Iterator t=set.iterator();t.hasNext();)
		{
			String setName=(String)t.next();
			sql_from.append(","+dbname+setName);
			if(!"a01".equalsIgnoreCase(setName)) {
                sql_where0.append(" and "+dbname+"a01.a0100="+dbname+setName+".a0100");
            }
		}
		sql.append(sql_select.toString());
		sql.append(" from ");
		sql.append(sql_from.substring(1));
		sql.append(" where ");
		
		if(sql_where0.length()>2)
		{
			sql.append(sql_where0.substring(4));
			sql.append(sql_where.toString());
			
		}
		else
		{
			if(sql_where.length()>2) {
                sql.append(sql_where.substring(4));
            } else {
                sql.append(" 1=1 ");
            }
		}
		return sql.toString();
	}
	/**
	 * 根据复杂模板，取得SQL语句
	 * @param templateid
	 * @param zpk
	 * @param view
	 * @return
	 */
	public String getComplexTemplateSQL(String templateid,String zpk,UserView view)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.conn);
			HashMap map = bo.getFactorExpr(templateid);
			String expr=(String)map.get("expr");
			String factor=(String)map.get("factor");
			String fieldsetid="a01";
			if(factor!=null&&!"".equals(factor))
			{
				String[] arr=factor.split("`");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i])) {
                        continue;
                    }
					FieldItem item=DataDictionary.getFieldItem(arr[i].toUpperCase());
					if(item!=null)
					{
						fieldsetid=item.getFieldsetid();
						break;
					}
				}
			}
			FactorList factorslist=new FactorList(expr,factor,zpk,false ,false,true,1,view.getUserId());
            String strwhere=factorslist.getSqlExpression();
            buf.append(" select distinct "+zpk+fieldsetid+".a0100 ");
            buf.append(strwhere);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	
	
	
	
	public String getSql(String type,String flag,String itemtype,String codesetid,String s_value,String e_value,String itemid,String vague)
	{
		StringBuffer sql_where=new StringBuffer("");
		if("true".equals(flag))   //false|true,(按区间｜不按区间)
		{
			if(s_value!=null&&s_value.trim().length()>0)
			{
				if("1".equals(type))         // 1:以上   0：未填
				{
					if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
					{
							sql_where.append(" and "+itemid+"<='"+s_value+"'");
					}
					else if("A".equalsIgnoreCase(itemtype)&& "0".equals(codesetid))
					{
						if("1".equals(vague)) {
                            sql_where.append(" and "+itemid+" like '%"+s_value+"%'");
                        } else {
                            sql_where.append(" and "+itemid+"='"+s_value+"'");
                        }
				
					}
			        else if("N".equalsIgnoreCase(itemtype))
					{
							sql_where.append(" and ( "+itemid+">="+s_value);
							if(Float.parseFloat(s_value)==0) {
                                sql_where.append(" or "+itemid+" is null ");
                            }
							sql_where.append(" ) ");
					
					}
					else if("D".equalsIgnoreCase(itemtype))
					{
						s_value=s_value.replaceAll("\\.","-");
						sql_where.append(" and "+getDataValue(itemid,">=",s_value));
					}
				}
				else
				{
					if("A".equalsIgnoreCase(itemtype))
					{	///代码性 兼容模糊查询 zzk 2013/12/10
						if(!"0".equals(codesetid)){
							if("1".equals(vague))
							{
								if(s_value.indexOf("|")!=-1)
	   			   				{
	   			   					String[] temps=s_value.split("\\|");
	   			   					StringBuffer ss=new StringBuffer("");
	   			   					for(int n=0;n<temps.length;n++)
	   			   					{
	   			   						if(temps[n].trim().length()>0)
	   			   						{
	   			   							ss.append(" or "+itemid+" like '"+temps[n]+"%'");
	   			   							
	   			   						}
	   			   					}
	   			   					sql_where.append(" and ( "+ss.substring(3)+" )");
	   			   				}
	   			   				else {
                                    sql_where.append(" and "+itemid+" like '"+s_value+"%'");
                                }
							}
							else
							{
		   			   			if(s_value.indexOf("|")!=-1)
	   			   				{
	   			   					String[] temps=s_value.split("\\|");
	   			   					StringBuffer ss=new StringBuffer("");
	   			   					for(int n=0;n<temps.length;n++)
	   			   					{
	   			   						if(temps[n].trim().length()>0)
	   			   						{
	   			   							ss.append(" or "+itemid+"='"+temps[n]+"'");
	   			   							
	   			   						}
	   			   					}
	   			   					sql_where.append(" and ( "+ss.substring(3)+" )");
	   			   				}
	   			   				else {
                                    sql_where.append(" and "+itemid+"='"+s_value+"'");
                                }
							}
						}
						else 
						{
							if("1".equals(vague))
							{
								if(s_value.indexOf("|")!=-1)
	   			   				{
	   			   					String[] temps=s_value.split("\\|");
	   			   					StringBuffer ss=new StringBuffer("");
	   			   					for(int n=0;n<temps.length;n++)
	   			   					{
	   			   						if(temps[n].trim().length()>0)
	   			   						{
	   			   							ss.append(" or "+itemid+" like '%"+temps[n]+"%'");
	   			   							
	   			   						}
	   			   					}
	   			   					sql_where.append(" and ( "+ss.substring(3)+" )");
	   			   				}
	   			   				else {
                                    sql_where.append(" and "+itemid+" like '%"+s_value+"%'");
                                }
							}
							else
							{
		   			   			if(s_value.indexOf("|")!=-1)
	   			   				{
	   			   					String[] temps=s_value.split("\\|");
	   			   					StringBuffer ss=new StringBuffer("");
	   			   					for(int n=0;n<temps.length;n++)
	   			   					{
	   			   						if(temps[n].trim().length()>0)
	   			   						{
	   			   							ss.append(" or "+itemid+"='"+temps[n]+"'");
	   			   							
	   			   						}
	   			   					}
	   			   					sql_where.append(" and ( "+ss.substring(3)+" )");
	   			   				}
	   			   				else {
                                    sql_where.append(" and "+itemid+"='"+s_value+"'");
                                }
							}
						}
							
					}
					else if("N".equalsIgnoreCase(itemtype))
					{
							sql_where.append(" and ( "+itemid+"="+s_value);
							if(Float.parseFloat(s_value)==0) {
                                sql_where.append(" or "+itemid+" is null ");
                            }
							sql_where.append(" ) ");
								
					}
					else if("D".equalsIgnoreCase(itemtype))
					{
						s_value=s_value.replaceAll("\\.","-");
						sql_where.append(" and "+getDataValue(itemid,"=",s_value));
					}
				}
			}
		}
		else
		{
			if(s_value!=null&&s_value.trim().length()>0)
			{
				if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
				{
						sql_where.append(" and "+itemid+"<='"+s_value+"'");
				}
				else if("A".equalsIgnoreCase(itemtype)&& "0".equals(codesetid))
				{
				//	if(vague.equals("1"))
				//		sql_where.append(" and "+itemid+" like '%"+s_value+"%'");
				//	else
						sql_where.append(" and "+itemid+">='"+s_value+"'");
				}
				else if("N".equalsIgnoreCase(itemtype))
				{
						sql_where.append(" and ( "+itemid+">="+s_value);
						if(Float.parseFloat(s_value)==0) {
                            sql_where.append(" or "+itemid+" is null ");
                        }
						sql_where.append(" ) ");
				}
				else if("D".equalsIgnoreCase(itemtype))
				{
					s_value=s_value.replaceAll("\\.","-");
					sql_where.append(" and "+getDataValue(itemid,">=",s_value));
				}
			}
			if(e_value!=null&&e_value.trim().length()>0)
			{
				if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid))
				{
						sql_where.append(" and "+itemid+">='"+e_value+"'");
				}
				else if("A".equalsIgnoreCase(itemtype)&& "0".equals(codesetid))
				{
			//		if(vague.equals("1"))
			//			sql_where.append(" and "+itemid+" like '%"+e_value+"%'");
			//		else
						sql_where.append(" and "+itemid+"<='"+e_value+"'");
				
				}
				else if("N".equalsIgnoreCase(itemtype))
				{
						sql_where.append(" and ( "+itemid+"<="+e_value);
						if(Float.parseFloat(s_value)==0) {
                            sql_where.append(" or "+itemid+" is null ");
                        }
						sql_where.append(" ) ");
				}
				else if("D".equalsIgnoreCase(itemtype))
				{
					e_value=e_value.replaceAll("\\.","-");
					sql_where.append(" and "+getDataValue(itemid,"<=",e_value));
				}
			}
		}
		return sql_where.toString();
	}
	
	
/**
 * 时间取年月日分别比较
 * @param fielditemid
 * @param operate
 * @param value
 * @return
 */
	public String getDataValue(String fielditemid,String operate,String value)
	{
		StringBuffer a_value=new StringBuffer("");		
		try
		{

				if("=".equals(operate))
				{
					a_value.append("(");
					a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" and ");
					a_value.append(Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" and ");
					a_value.append(Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(" ) ");
				}
				else 
				{
					a_value.append("(");
					if(">=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+">"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+">"+value.substring(5,7)+" ) or ( ");						
					}
					else if("<=".equals(operate))
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+"<"+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"<"+value.substring(5,7)+" ) or ( ");						
					}
					else
					{
						
						a_value.append(Sql_switcher.year(fielditemid)+operate+value.substring(0,4)+" or ( ");
						a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+operate+value.substring(5,7)+" ) or ( ");
						
					}
					a_value.append(Sql_switcher.year(fielditemid)+"="+value.substring(0,4)+" and "+Sql_switcher.month(fielditemid)+"="+value.substring(5,7)+" and "+Sql_switcher.day(fielditemid)+operate+value.substring(8));
					a_value.append(") ) ");
				}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return a_value.toString();
	}
	
	
	
	
	public ArrayList getParamConditionList(ArrayList posConditionList)
	{
		ArrayList list=new ArrayList();
		
		for(int j=0;j<posConditionList.size();j++)
   		{
   			
   			LazyDynaBean abean=(LazyDynaBean)posConditionList.get(j);
   			String itemid=(String)abean.get("itemid");
   			String setname=(String)abean.get("setname");
   			String codesetid=(String)abean.get("codesetid");
   			String s_value=(String)abean.get("s_value");
   			String e_value=(String)abean.get("e_value");
   			String isMore=(String)abean.get("isMore");
   			String itemtype=(String)abean.get("fieldType");
   			String flag=(String)abean.get("flag");			//false|true,(按区间｜不按区间)
   			String type=(String)abean.get("type");			// 1:以上   0：未填
   			String view_s_value=(String)abean.get("view_s_value");
   			String view_e_value=(String)abean.get("view_e_value");
		
   			boolean isFlag=false;       //单独的子集指标    			
   			{
   				LazyDynaBean abean0=null;
   				String setname0="";
   				LazyDynaBean abean2=null;
   				String setname2="";
   				if((j+1)<posConditionList.size())
   				{
   					abean2=(LazyDynaBean)posConditionList.get(j+1);
   					setname2=(String)abean2.get("setname");
   					
   				}
   				if(j!=0)
   				{
   					abean0=(LazyDynaBean)posConditionList.get(j-1);
   					setname0=(String)abean0.get("setname");
   				}
   				if(abean0==null&&abean2==null) {
                    isFlag=true;
                } else if((abean0==null&&abean2!=null)&&!setname2.equals(setname)) {
                    isFlag=true;
                } else if((abean0!=null&&abean2==null)&&!setname0.equals(setname)) {
                    isFlag=true;
                } else if(abean0!=null&&abean2!=null&&!setname2.equals(setname)&&!setname0.equals(setname)) {
                    isFlag=true;
                }
   			}
   			
   			if("A01".equalsIgnoreCase(setname)||isFlag)
   			{
   				
   				LazyDynaBean a_bean=new LazyDynaBean();
   				a_bean.set("name",itemid);
   				if(s_value!=null)
   				{
	   				if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid)&& "1".equals(isMore))
	   				{
	   					if(view_s_value!=null&&view_s_value.trim().length()>0) {
                            a_bean.set("s_value",s_value.trim());
                        } else {
                            a_bean.set("s_value","");
                        }
	   				}
	   				else
	   				{
	   					a_bean.set("s_value",s_value);
	   				}
   				}
   				else {
                    a_bean.set("s_value","");
                }
   				
   				a_bean.set("flag",flag);
   				if("true".equals(flag))
   				{
   					a_bean.set("e_value","");
   					if(type!=null)
   					{
   						a_bean.set("type",type);
   					}
   					else {
                        a_bean.set("type","0");
                    }
   				}
   				else
   				{
   					if(e_value!=null)
   	   				{
   		   				if("A".equalsIgnoreCase(itemtype)&&!"0".equals(codesetid)&& "1".equals(isMore))
   		   				{
   		   					if(view_e_value!=null&&view_e_value.trim().length()>0) {
                                a_bean.set("e_value",e_value.trim());
                            } else {
                                a_bean.set("e_value","");
                            }
   		   				}
   		   				else
   		   				{
   		   					a_bean.set("e_value",e_value);
   		   				}
   	   				}
   	   				else {
                        a_bean.set("e_value","");
                    }
   					a_bean.set("type","0");
   					
   				}
   				list.add(a_bean);
   			}
   			else
   			{
   				int a=j+1;
   				int size=1;   //子集指标个数
   				while(a<posConditionList.size())
   				{
   					LazyDynaBean abean1=(LazyDynaBean)posConditionList.get(a);
   		   			String itemid1=(String)abean1.get("itemid");
   		   			if(itemid1.equals(itemid)) {
                        break;
                    }
   					size++;
   					a++;
   				}
   				
   				for(int b=0;b<size;b++)
   				{
   					LazyDynaBean abean2=(LazyDynaBean)posConditionList.get(j+b);
   			//		LazyDynaBean abean3=(LazyDynaBean)posConditionList.get(j+b+size);
   			//		LazyDynaBean abean4=(LazyDynaBean)posConditionList.get(j+b+size*2);
   					
   					String a_name=(String)abean2.get("itemid");
   					String a_flag="true";
   					String a_type=(String)abean2.get("type");			// 1:以上   0：未填;
   					String a_s_value=(String)abean2.get("s_value");   // getValue(abean2,abean3,abean4,"s");
   					String a_e_value="";    // getValue(abean2,abean3,abean4,"e");
   					
   					LazyDynaBean a_bean=new LazyDynaBean();
   	   				a_bean.set("name",a_name);
   	   				a_bean.set("flag",a_flag);
   	   				a_bean.set("type",a_type);
   	   				a_bean.set("s_value",a_s_value);
   	   				a_bean.set("e_value",a_e_value);
   	   				list.add(a_bean);
   				}
   				j=j+size*3-1;
   			}
   		}
		return list;
	}
	
	
	
	
	
	public String getValue(LazyDynaBean abean2,LazyDynaBean abean3,LazyDynaBean abean4,String flag)
	{
			String value="";
			String a_itemtype=(String)abean2.get("fieldType");
			String a_codesetid=(String)abean2.get("codesetid");  		   		
  			String a_isMore=(String)abean2.get("isMore");
		    if("A".equalsIgnoreCase(a_itemtype)&&!"0".equals(a_codesetid)&& "1".equals(a_isMore))
  			{
  					String view_s_value2=(String)abean2.get("view_"+flag+"_value");
  					String view_s_value3=(String)abean3.get("view_"+flag+"_value");
  					String view_s_value4=(String)abean4.get("view_"+flag+"_value");
  					
  					if(view_s_value2!=null&&view_s_value2.trim().length()>0) {
                        value+=(String)abean2.get(flag+"_value")+",";
                    } else {
                        value+="#,";
                    }
  					
  					if(view_s_value3!=null&&view_s_value3.trim().length()>0) {
                        value+=(String)abean3.get(flag+"_value")+",";
                    } else {
                        value+="#,";
                    }
  				
  					if(view_s_value4!=null&&view_s_value4.trim().length()>0) {
                        value+=(String)abean4.get(flag+"_value")+",";
                    } else {
                        value+="#,";
                    }
  			}
  			else
  			{
  				String s_value2=(String)abean2.get(flag+"_value");
				String s_value3=(String)abean3.get(flag+"_value");
				String s_value4=(String)abean4.get(flag+"_value");
				value+=s_value2+","+s_value3+","+s_value4+",";
				
  			}
		    value=value.substring(0,value.length()-1);
		
		    return value;
		
	}
	
	
	/**
	 * 
	 * @param itemid
	 * @param itemdesc
	 * @param s_value
	 * @param e_value
	 * @param flag  false|true,(按区间｜不按区间)
	 * @param type  1: 以上
	 * @param setName
	 * @param fieldType
	 * @param codesetid
	 * @param employNetPortalBo
	 * @param multilayerCodeSetMap
	 * @return
	 */
	public LazyDynaBean getBean(String itemid,String itemdesc,String s_value,String e_value,String flag,String type,String setName,String fieldType,String codesetid,EmployNetPortalBo employNetPortalBo,HashMap multilayerCodeSetMap)
	{
		
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("itemid",itemid);
		abean.set("itemdesc",itemdesc);
		abean.set("s_value",s_value);
		if("A".equalsIgnoreCase(fieldType)&&!"0".equals(codesetid)&&s_value.trim().length()>0) {
            abean.set("view_s_value",AdminCode.getCodeName(codesetid,s_value));
        } else {
            abean.set("view_s_value","");
        }
		abean.set("e_value",e_value);
		if("A".equalsIgnoreCase(fieldType)&&!"0".equals(codesetid)&&e_value.trim().length()>0) {
            abean.set("view_e_value",AdminCode.getCodeName(codesetid,e_value));
        } else {
            abean.set("view_e_value","");
        }
		
		abean.set("flag",flag);
		abean.set("type",type);
		abean.set("setname",setName);
		abean.set("fieldType",fieldType);
		abean.set("codesetid",codesetid);
		abean.set("isMore","1");
		if("A".equalsIgnoreCase(fieldType)&&!"0".equals(codesetid))
		{
			String isMore=getIsMore(multilayerCodeSetMap,codesetid);
			if("0".equals(isMore))
			{
				abean.set("options",employNetPortalBo.getOptions(codesetid));
				abean.set("isMore","0");
			}
		}
		return abean;
	}
	
	
	private LazyDynaBean getCopyLazyDynaBean(LazyDynaBean abean)
	{
		LazyDynaBean aabean=new LazyDynaBean();
		aabean.set("itemid",(String)abean.get("itemid"));
		aabean.set("itemdesc",(String)abean.get("itemdesc"));
		aabean.set("s_value",(String)abean.get("s_value"));
		aabean.set("view_s_value",(String)abean.get("view_s_value"));
		aabean.set("e_value",(String)abean.get("e_value"));
		aabean.set("view_e_value",(String)abean.get("view_e_value"));
		
		aabean.set("flag",(String)abean.get("flag"));
		aabean.set("type",(String)abean.get("type"));
		aabean.set("setname",(String)abean.get("setname"));
		aabean.set("fieldType",(String)abean.get("fieldType"));
		aabean.set("codesetid",(String)abean.get("codesetid"));
		aabean.set("isMore",(String)abean.get("isMore"));		
		if(abean.get("options")!=null) {
            aabean.set("options",(ArrayList)abean.get("options"));
        }
		return aabean;
	}
	
	
	
	
	/**
	 * 得到简历筛选条件 列表
	 * @param type  simple：简单　　　general：复杂
	 * @return
	 */
	public ArrayList getPosConditionList(String node)
	{	
		ArrayList list=new ArrayList();
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn);
			HashMap multilayerCodeSetMap=getMultilayerCodeSetMap();
			
			ZpCondTemplateXMLBo xmlBo=new ZpCondTemplateXMLBo(this.conn);
			HashMap map=xmlBo.getS_GMap(node);
			StringBuffer whl=new StringBuffer("");
			Set set=map.keySet();
			boolean isCreateTime=false;
			ZpCondTemplateXMLBo bo = new ZpCondTemplateXMLBo(this.conn);
			ArrayList list1 =new ArrayList();//dml 2011年8月22日11:33:14
			if("simple".equalsIgnoreCase(node)){
				list1= bo.getAttributeValues("0");
			}else{
				list1= bo.getAttributeValues("1");
			}
			
			
			for(Iterator t=set.iterator();t.hasNext();)
			{
				String fieldName=(String)t.next();
				whl.append(",'");
				whl.append(fieldName);
				whl.append("'");
				if("createtime".equalsIgnoreCase(fieldName)) {
                    isCreateTime=true;
                }
			}
			if(whl.length()>1)
			{
				RowSet rowSet=dao.search("select * from fielditem where UPPER(itemid) in ( "+whl.substring(1).toUpperCase()+" ) order by fieldsetid ");
				
				int n=0;
				while(rowSet.next())
				{
					
					String itemid=rowSet.getString("itemid");
					String itemdesc=rowSet.getString("itemdesc");
					String itemtype=rowSet.getString("itemtype");
					String codesetid=rowSet.getString("codesetid");
					String fieldsetid=rowSet.getString("fieldsetid");
					if(!"A01".equalsIgnoreCase(fieldsetid)&&n==0)
					{
						if(isCreateTime)
						{ 
							n++;
							String aitemid="createtime";
							String aitemdesc="简历入库时间";
							String aitemtype="D";
							String acodesetid="0";
							String afieldsetid="A01";	
							LazyDynaBean abean=(LazyDynaBean)map.get(aitemid.toLowerCase());
							LazyDynaBean abeans=getBean(aitemid,aitemdesc,(String)abean.get("s_value"),(String)abean.get("e_value"),(String)abean.get("flag"),"0",afieldsetid,aitemtype,acodesetid,employNetPortalBo,multilayerCodeSetMap);		
							list.add(abeans);
						}
					}
					LazyDynaBean abean=(LazyDynaBean)map.get(itemid.toLowerCase());
					
					LazyDynaBean abeans=getBean(itemid,itemdesc,(String)abean.get("s_value"),(String)abean.get("e_value"),(String)abean.get("flag"),"0",fieldsetid,itemtype,codesetid,employNetPortalBo,multilayerCodeSetMap);		
					list.add(abeans);
					
				}
				
				if(isCreateTime&&n==0)
				{
					String aitemid="createtime";
					String aitemdesc="简历入库时间";
					String aitemtype="D";
					String acodesetid="0";
					String afieldsetid="A01";	
					LazyDynaBean abean=(LazyDynaBean)map.get(aitemid.toLowerCase());
					LazyDynaBean abeans=getBean(aitemid,aitemdesc,(String)abean.get("s_value"),(String)abean.get("e_value"),(String)abean.get("flag"),"0",afieldsetid,aitemtype,acodesetid,employNetPortalBo,multilayerCodeSetMap);		
					list.add(abeans);
				}
				ArrayList list2=new ArrayList();//dml 2011年8月22日11:59:12
				for(int i=0;i<list1.size();i++){
					LazyDynaBean bean = (LazyDynaBean)list1.get(i);
			    	String id=(String)bean.get("name");
					for(int j=0;j<list.size();j++){
						LazyDynaBean bean1 = (LazyDynaBean)list.get(j);
						String itemid=(String)bean1.get("itemid");
						if(itemid.equalsIgnoreCase(id)){
							list2.add(bean1);
							
						}else{
							continue;
						}
					}
				}
				list=null;
				list=list2;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	/**
	 *	重组条件列表 
	 * @param type  0：简单条件　　　1：复杂条件
	 * @param z0301  -1：初始化  -2 去掉默认值
	 */
	public ArrayList getResetPosConditionList(String type,String z0301)
	{
		String node="";
		if("0".equals(type)){
			node="simple";
		}
		if("1".equals(type)){
			node="general";
		}
		ArrayList list=new ArrayList();
		
		try
		{
			ArrayList conditionList=getPosConditionList(node);
			ArrayList param_conditionList=new ArrayList();
			if(!"-1".equals(z0301)&&!"-2".equals(z0301))
			{
				DemandCtrlParamXmlBo bo=new DemandCtrlParamXmlBo(this.conn,z0301);
				HashMap conditionMap=bo.getAttributeValues(node);
				param_conditionList=(ArrayList)conditionMap.get(node);
			}
			for(int i=0;i<conditionList.size();i++)
			{
				LazyDynaBean abean=(LazyDynaBean)conditionList.get(i);
				String setname=(String)abean.get("setname");
				String itemid=(String)abean.get("itemid");
				if("A01".equalsIgnoreCase(setname))
				{
					if(param_conditionList!=null)
					{
		    			if(param_conditionList.size()>0&&param_conditionList.size()==conditionList.size())
		    			{
		    				LazyDynaBean aa_bean=(LazyDynaBean)param_conditionList.get(i);
			    			String itemid_para=(String)aa_bean.get("name");
			    			if(itemid_para.equalsIgnoreCase(itemid))
			    			{
			     				String s_value=(String)aa_bean.get("s_value");
				    			String e_value=(String)aa_bean.get("e_value");						
				    			String flag=(String)aa_bean.get("flag");
				    			String atype=(String)aa_bean.get("type");
							
							
					    		abean.set("flag",flag);
					    		abean.set("s_value",s_value);
						    	if("A".equals((String)abean.get("fieldType"))&&!"0".equals((String)abean.get("codesetid"))&&s_value.trim().length()>0) {
                                    abean.set("view_s_value",AdminCode.getCodeName((String)abean.get("codesetid"),s_value.trim()));
                                }
					    		if("true".equalsIgnoreCase(flag))
					    		{
					    			abean.set("type",atype);
					    			abean.set("e_value","");
					    		}
					    		else
					    		{	
					    			abean.set("type","0");
					    			abean.set("e_value",e_value);
					    			if("A".equals((String)abean.get("fieldType"))&&!"0".equals((String)abean.get("codesetid"))&&e_value.trim().length()>0) {
                                        abean.set("view_e_value",AdminCode.getCodeName((String)abean.get("codesetid"),e_value.trim()));
                                    }
				    			}
			    			}	
		    			}
					}
					
					if("-2".equals(z0301))
					{
						abean.set("s_value","");
						abean.set("e_value","");
						abean.set("view_s_value","");
						abean.set("view_e_value","");
					}
					list.add(abean);
				}
				else
				{
					int n=i;
					ArrayList list_temp=new ArrayList();
					list_temp.add(abean);
					while(n+1<conditionList.size())
					{
						LazyDynaBean abean1=(LazyDynaBean)conditionList.get(n+1);				
						String setname1=(String)abean1.get("setname");
						if(setname1.equalsIgnoreCase(setname))
						{
							list_temp.add(abean1);
							n++;
						}
						else {
                            break;
                        }
					}
					if(list_temp.size()==1)
					{
						LazyDynaBean a_bean=(LazyDynaBean)list_temp.get(0);
						String itemid_temp=(String)a_bean.get("itemid");
						
						if(param_conditionList.size()>0&&param_conditionList.size()==conditionList.size())
						{
							LazyDynaBean aa_bean=(LazyDynaBean)param_conditionList.get(i);
							String itemid_para=(String)aa_bean.get("name");
							if(itemid_temp.equalsIgnoreCase(itemid_para))
							{
								String s_value=(String)aa_bean.get("s_value");
								String e_value=(String)aa_bean.get("e_value");						
								String flag=(String)aa_bean.get("flag");
								String atype=(String)aa_bean.get("type");
								
								
								a_bean.set("flag",flag);
								a_bean.set("s_value",s_value);
								if("A".equals((String)a_bean.get("fieldType"))&&!"0".equals((String)a_bean.get("codesetid"))&&s_value.trim().length()>0) {
                                    a_bean.set("view_s_value",AdminCode.getCodeName((String)a_bean.get("codesetid"),s_value.trim()));
                                }
								if("true".equalsIgnoreCase(flag))
								{
									a_bean.set("type",atype);
									a_bean.set("e_value","");
								}
								else
								{	
									a_bean.set("type","0");
									a_bean.set("e_value",e_value);
									if("A".equals((String)a_bean.get("fieldType"))&&!"0".equals((String)a_bean.get("codesetid"))&&e_value.trim().length()>0) {
                                        a_bean.set("view_e_value",AdminCode.getCodeName((String)a_bean.get("codesetid"),e_value.trim()));
                                    }
								}
							}
						}
						
						if("-2".equals(z0301))
						{
							abean.set("s_value","");
							abean.set("e_value","");
							abean.set("view_s_value","");
							abean.set("view_e_value","");
						}
						list.add(a_bean);

					}
					else
					{

							int c=0;
							for(Iterator t=list_temp.iterator();t.hasNext();)
							{
								LazyDynaBean a_bean=getCopyLazyDynaBean((LazyDynaBean)t.next());
								String itemid_temp=(String)a_bean.get("itemid");
								if(param_conditionList.size()>0&&param_conditionList.size()==conditionList.size())
								{
									LazyDynaBean aa_bean=(LazyDynaBean)param_conditionList.get(i+c);
									String itemid_para=(String)aa_bean.get("name");
									if(itemid_temp.equalsIgnoreCase(itemid_para))
									{
										String s_value=(String)aa_bean.get("s_value");
										String e_value=(String)aa_bean.get("e_value");							
										String flag=(String)aa_bean.get("flag");
										String atype=(String)aa_bean.get("type");
										
										a_bean.set("flag",flag);
									    a_bean.set("s_value",s_value);
										if("A".equals((String)a_bean.get("fieldType"))&&!"0".equals((String)a_bean.get("codesetid"))&&!"#".equals(s_value.trim())) {
                                            a_bean.set("view_s_value",AdminCode.getCodeName((String)a_bean.get("codesetid"),s_value.trim()));
                                        }
										a_bean.set("type",atype);
										a_bean.set("e_value","");	
									}
								}
							    a_bean.set("show","1");
								if("-2".equals(z0301))
								{
									abean.set("s_value","");
									abean.set("e_value","");
									abean.set("view_s_value","");
									abean.set("view_e_value","");
								}
								list.add(a_bean);							
								c++;
							}
					}
					i=n;
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	////////////////////////////////////////////////////////////////////
	public ArrayList getOptionsSelf(String orgID)
	{
		ArrayList list=new ArrayList();
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rowSet = null;
		try
		{
			String sql="select * from organization where parentid='"+orgID+"' and codesetid='@K' and codeitemid in (select E01A1 from K01)";
	
			rowSet=dao.search(sql);			
			while(rowSet.next())
			{
				
				LazyDynaBean abean=new LazyDynaBean();
				abean.set("value",rowSet.getString("codeitemid"));
				abean.set("name",rowSet.getString("codeitemdesc"));
				list.add(abean);
			}
			LazyDynaBean abean1=new LazyDynaBean();
			abean1.set("value","-1");
			abean1.set("name","新添职位...");
			list.add(abean1);
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	
	
/**
 * 用工需求列
 * @param z03_list
 * @param z0301
 * @return
 */
	public ArrayList getPositionDemandDescList(ArrayList z03_list,String z0301)
	{
		ArrayList positionDemandDescList=new ArrayList();
		boolean isValue=false;
		try
		{
			RowSet rowSet=null;
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
			HashMap map=parameterXMLBo.getAttributeValues();
			/**招聘需求上报进行工资总额控制*/
			String isCtrlReportGZ="0";
			/**招聘需求上报进行编制控制*/
			String isCtrlReportBZ="0";
			//对外应聘指标
			String foreignItem ="";
			/**职位最高工资标准*/
			if(map!=null&&map.get("isCtrlReportGZ")!=null)
			{
				isCtrlReportGZ=(String)map.get("isCtrlReportGZ");
			}
			if(map!=null&&map.get("isCtrlReportBZ")!=null)
			{
				isCtrlReportBZ=(String)map.get("isCtrlReportBZ");
			}
			//获取简历解析对外应聘指标
			if(map!=null&&map.get("resumeAnalysisMap")!=null){
				HashMap resumeAnalysisMap = (HashMap) map.get("resumeAnalysisMap");
				foreignItem = (String) resumeAnalysisMap.get("resumeAnalysisForeignJob");
			}
			ContentDAO dao=new ContentDAO(this.conn);
			
			HashMap msMap=new HashMap();
		    rowSet=dao.search("select ITEMMEMO,ITEMID from t_hr_busiField where fieldsetid='Z03' ");
		    while(rowSet.next())
		    {
		    	String itemmemo=Sql_switcher.readMemo(rowSet,"itemmemo");
		    	msMap.put(rowSet.getString("itemid").toLowerCase(), itemmemo);
		    }
			
			
			String orgViewValue="";
			String posViewValue="";
			String orgValue="";
			String posValue="";
			String z0336Value="";
			if(!"-1".equals(z0301))
			{
				
				rowSet=dao.search("select *  from z03 where z0301='"+z0301+"'");
				if(rowSet.next())
				{
					isValue=true;
					posValue=rowSet.getString("z0311");
					posViewValue=AdminCode.getCodeName("@K",posValue);
					
					String codesetid="UM";
					orgValue=rowSet.getString("z0325");
					if(orgValue==null||orgValue.length()==0)//dml有z0325不为空""
					{
						orgValue=rowSet.getString("z0321");
						codesetid="UN";
					}
					orgViewValue=AdminCode.getCodeName(codesetid,orgValue);;
				}
			}
			
			EmployNetPortalBo employNetPortalBo=new EmployNetPortalBo(this.conn);
			HashMap multilayerCodeSetMap=employNetPortalBo.getMultilayerCodeSetMap();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
			
			if(isValue)
			{
				String z0311=rowSet.getString("z0311");
			//	StringBuffer subSql=new StringBuffer("select a.codeitemdesc,a.codeitemid,b.codeitemdesc,b.codeitemid from ");
			//	subSql.append(" (select * from organization where codeitemid=(select parentid from organization where codeitemid='"+z0311+"')) a ");
			//	subSql.append(" ,organization b where b.parentid=a.codeitemid and b.codeitemid='"+z0311+"' ");
				 
			//	RowSet rowSet2=dao.search(subSql.toString());
			//	if(rowSet2.next())
				{
			//		orgViewValue=rowSet2.getString(1);
			//		orgValue=rowSet2.getString(2);
					
			//		posViewValue=rowSet2.getString(3);
			//		posValue=rowSet2.getString(4);
				}
				
				z0336Value=rowSet.getString("z0336");
			}
			
			
			//招聘渠道  author:dengc 
			FieldItem a_item=DataDictionary.getFieldItem("Z0336","Z03");
			LazyDynaBean abean0=new LazyDynaBean();
			abean0.set("isMore","0");        //0：一层代码  1：多层代码
			abean0.set("itemid",a_item.getItemid());
			abean0.set("itemdesc",a_item.getItemdesc());
			abean0.set("itemtype",a_item.getItemtype());
			abean0.set("codesetid",a_item.getCodesetid());
			abean0.set("length",String.valueOf(a_item.getItemlength()));
			abean0.set("decimal",String.valueOf(a_item.getDecimalwidth()));
			if(z0336Value!=null&&z0336Value.trim().length()>0) {
                abean0.set("viewvalue",AdminCode.getCodeName("35", z0336Value));
            } else {
                abean0.set("viewvalue","");
            }
			abean0.set("value",z0336Value==null?"":z0336Value);
			abean0.set("mustfill","1");
			if(msMap.get("z0336")!=null) {
                abean0.set("desc",(String)msMap.get("z0336"));
            } else {
                abean0.set("desc","");
            }
			abean0.set("options",employNetPortalBo.getOptions("35"));
			positionDemandDescList.add(abean0);
			
			
			//所属机构
			a_item=DataDictionary.getFieldItem("z0325","Z03");
			String _str="";
			if(msMap.get("z0325")!=null) {
                _str=(String)msMap.get("z0325");
            }
			 
			abean0=getDynaBean("orgID",a_item.getItemdesc(),"A","UM","10","10",orgViewValue,orgValue,_str);
			abean0.set("isMore","1");        //0：一层代码  1：多层代码
			/**必填项设置*/
			abean0.set("mustfill", "1");
			positionDemandDescList.add(abean0);
			
			
			//职位名称
			a_item=DataDictionary.getFieldItem("z0311","Z03");
			_str="";
			if(msMap.get("z0311")!=null) {
                _str=(String)msMap.get("z0311");
            }
			LazyDynaBean abean1=getDynaBean("posID",a_item.getItemdesc(),"A","@K","10","10",posViewValue,posValue,_str);
			abean1.set("isMore","0");        //0：一层代码  1：多层代码
			/**必填项设置*/
			/*if(a_item.isFillable())
				abean1.set("mustfill","1");
			else
				abean1.set("mustfill","0");*/
			abean1.set("mustfill","1");
			if(orgValue.trim().length()>0) {
                abean1.set("options",getOptionsSelf(orgValue));
            }
		/*	if(a_item.isVisible())*/
			positionDemandDescList.add(abean1);
			

			
			
			ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			
			for(int i=0;i<list.size();i++)
			{
				FieldItem item=(FieldItem)list.get(i);
				String itemid=item.getItemid();
				if("ctrl_param".equalsIgnoreCase(itemid)|| "z0101".equalsIgnoreCase(itemid)|| "z0325".equalsIgnoreCase(itemid)|| "z0321".equalsIgnoreCase(itemid)|| "z0311".equalsIgnoreCase(itemid)|| "z0301".equalsIgnoreCase(itemid)|| "z0327".equalsIgnoreCase(itemid)|| "z0319".equalsIgnoreCase(itemid)|| "z0323".equalsIgnoreCase(itemid)) {
                    continue;
                }
				if("-1".equals(z0301)&& "z0315".equalsIgnoreCase(itemid)) {
                    continue;
                }
				if("z0336".equalsIgnoreCase(itemid)) {
                    continue;
                }
				if("0".equals(item.getState())&&!"z0329".equalsIgnoreCase(itemid)&&!"z0331".equalsIgnoreCase(itemid))//有效起始日期、有效结束日期设置为不能隐藏
                {
                    continue;
                } else
				{
					String itemtype=item.getItemtype();
					String codesetid=item.getCodesetid();
					String isMore=employNetPortalBo.getIsMore(multilayerCodeSetMap,codesetid);
					if("z0303".equalsIgnoreCase(itemid)|| "z0305".equalsIgnoreCase(itemid)) {
                        isMore="1";
                    }
					
					LazyDynaBean abean=new LazyDynaBean();
					abean.set("isMore",isMore);        //0：一层代码  1：多层代码
					abean.set("itemid",itemid);
					if(foreignItem.equalsIgnoreCase(itemid)){
						abean.set("itemdesc","对外发布岗位名称");
					}else{
						abean.set("itemdesc",item.getItemdesc());
					}
					
					abean.set("itemtype",item.getItemtype());
					abean.set("codesetid",item.getCodesetid());
					abean.set("length",String.valueOf(item.getItemlength()));
					abean.set("decimal",String.valueOf(item.getDecimalwidth()));
					if(msMap.get(itemid.toLowerCase())!=null) {
                        abean.set("desc",(String)msMap.get(itemid.toLowerCase()));
                    } else {
                        abean.set("desc","");
                    }
					abean.set("viewvalue","");
					abean.set("value","");
					if("z0316".equalsIgnoreCase(itemid)&&("1".equalsIgnoreCase(isCtrlReportBZ)|| "1".equals(isCtrlReportGZ)))
					{
						abean.set("mustfill","1");
					}
					else
					{
				    	/**必填项设置*/
				    	if(item.isFillable()) {
                            abean.set("mustfill","1");
                        } else{
				    		if("z0313".equalsIgnoreCase(itemid)|| "z0329".equalsIgnoreCase(itemid)|| "z0331".equalsIgnoreCase(itemid)){
				    			abean.set("mustfill","1");
				    		}else{
				    			abean.set("mustfill","0");
				    		}
				    		
				    	}
					}
					if("0".equals(isMore)&&!"0".equals(item.getCodesetid()))
			     	{
			    		abean.set("options",employNetPortalBo.getOptions(codesetid));
			    	}
					if(isValue)
					{
						if("A".equals(item.getItemtype()))
						{
							if("0".equals(item.getCodesetid()))
							{
								if(rowSet.getString(itemid)!=null) {
                                    abean.set("value",rowSet.getString(itemid));
                                } else {
                                    abean.set("value","");
                                }
							}
							else
							{
								abean.set("value",rowSet.getString(itemid));
								abean.set("viewvalue",AdminCode.getCodeName(item.getCodesetid(),rowSet.getString(itemid)));
							}
						}
						else if("D".equals(item.getItemtype()))
						{
							if(rowSet.getDate(itemid)!=null)
							{
								abean.set("value",dateFormat.format(rowSet.getDate(itemid)));
							}
							else {
                                abean.set("value","");
                            }
						}
						else if("N".equals(item.getItemtype()))
						{
							if(rowSet.getString(itemid)!=null) {
                                abean.set("value",rowSet.getString(itemid));
                            }
						}
						else if("M".equals(item.getItemtype()))
						{
							abean.set("value",Sql_switcher.readMemo(rowSet,itemid));
						}												
					}
					positionDemandDescList.add(abean);
							
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return positionDemandDescList;
		
	}
	
	public LazyDynaBean getDynaBean(String itemid,String itemdesc,String itemtype,String codesetid,String length,String decimal,String viewvalue,String value,String desc)
	{
		LazyDynaBean abean=new LazyDynaBean();
		abean.set("desc",desc);
		abean.set("itemid",itemid);
		abean.set("itemdesc",itemdesc);
		abean.set("itemtype",itemtype);
		abean.set("codesetid",codesetid);
		abean.set("length",length);
		abean.set("decimal",decimal);
		abean.set("viewvalue",viewvalue);
		abean.set("value",value);
		return abean;
	}
	
	
	
	
	
	/**
	 * 将空闲职位信息生成用工需求信息
	 * @param sparePositionMap 空闲职位信息对应map
	 * @param positionIDs	   空闲职位id
	 */
	public void InsertSparePosition(HashMap sparePositionMap,String[] positionIDs,UserView userview)
	{
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		String z0301="";
		IDGenerator idg=new IDGenerator(2,this.conn);
		ArrayList list=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
		try
		{
			
			GregorianCalendar d = new GregorianCalendar();
		//	String a_creatData=d.get(Calendar.YEAR)+"."+(d.get(Calendar.MONTH)+1)+"."+d.get(Calendar.DATE); //创建日期
			

			 RecordVo vo=new RecordVo("Z03");	
			 String firstlevelid=SystemConfig.getPropertyValue("firstlevelfield");
			 String second=SystemConfig.getPropertyValue("secondlevelfield");
			 String creatname=userview.getUserFullName()!=null&&userview.getUserFullName()!=""?userview.getUserFullName():userview.getUserName();
			 for (int i = 0; i < positionIDs.length; i++) {
				String positionID = positionIDs[i];
				LazyDynaBean aDynaBean = (LazyDynaBean) sparePositionMap.get(positionID);
				this.getLink((String)aDynaBean.get("UMID"), 1);
				z0301 = idg.getId("Z03.Z0301");
				vo.setString("z0301", z0301);				
				vo.setString("z0309", creatname);      //创建人
				vo.setInt("z0317", 0);								//使用状态
				vo.setString("z0319","01");							//审批状态
				vo.setString("z0311",positionID);                   //需求岗位
				vo.setInt("z0313",Integer.parseInt(PubFunc.round((String)aDynaBean.get("planNumber"),0))-Integer.parseInt(PubFunc.round((String)aDynaBean.get("actualNumber"),0)));//需求人数
				vo.setString("z0321",(String)aDynaBean.get("UNID"));//需求单位
				vo.setString("z0325",(String)aDynaBean.get("UMID"));  //需求部门
				vo.setDate("z0307",d.getTime());				   //创建时间
				vo.setString("z0336", "02");						//dml 招聘渠道默认为社会招聘 2011-04-01
				if(firstlevelid!=null&&!"".equals(firstlevelid))
	    		{
					FieldItem fitem = DataDictionary.getFieldItem(firstlevelid);
					if(fitem!=null&& "z03".equalsIgnoreCase(fitem.getFieldsetid()))
					{
						if(linkMap.get((maxLay+""))!=null)
			    		{
			    			vo.setString(firstlevelid.toLowerCase(),((String)linkMap.get(maxLay+"")));
			    		}
			    		else {
                            vo.setString(firstlevelid.toLowerCase(),"");
                        }
					}
	    		}
	    		if(second!=null&&!"".equals(second))
		    	{
	    			FieldItem fitem = DataDictionary.getFieldItem(second);
	    			if(fitem!=null&& "z03".equalsIgnoreCase(fitem.getFieldsetid()))
	    			{
	    				if(linkMap.get(((maxLay==1?1:(maxLay-1))+""))!=null)
	    				{
			    			vo.setString(second.toLowerCase(),((String)linkMap.get((maxLay==1?1:(maxLay-1))+"")));
			    		}
			    		else {
                            vo.setString(second.toLowerCase(),"");
                        }
	    			}
		    	}
				 /*******************引入岗位协议***********************/
	    		HashMap kValueMap = this.getKInfoMap(positionID);
				 if(kValueMap!=null)
				 {
					SimpleDateFormat fomat = new SimpleDateFormat("yyyy-MM-dd");
					Set keySet = kValueMap.keySet();
					for(Iterator t=keySet.iterator();t.hasNext();)
					{
						String key=(String)t.next();
						String value=(String)kValueMap.get(key);
						if(value!=null&&value.length()>0)
						{
							FieldItem item = DataDictionary.getFieldItem(key.toLowerCase());
							if("N".equals(item.getItemtype()))
							{
								if(item.getDecimalwidth()==0) {
                                    vo.setInt(key, Integer.parseInt(value));
                                } else {
                                    vo.setDouble(key,Double.parseDouble(value));
                                }
							}else if("D".equals(item.getItemtype()))
							{
								vo.setDate(key, fomat.parse(value));
							}else {
                                vo.setString(key,value);
                            }
						}
					}
				 }
	    		
				dao.addValueObject(vo);

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 取得某组织下的空闲职位
	 * @param codeid 组织id
	 * @return
	 */
	public ArrayList getSparePositionInfo(String codeid)
	{
		ArrayList list=new ArrayList();
		ArrayList sparePositionList=new ArrayList();
		HashMap   sparePositionMap=new HashMap();
		String    actualNumberFieldName="";
		String    planNumberFieldName="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		try
		{
			String actualNumberField="";  //实有人数指标
			String planNumberField="";    //编制人数指标
			String fieldSetid="";         //子集id
			recset=dao.search("select * from constant where constant='PS_WORKOUT'");
			if(recset.next())
			{
				//K01|K0111,K0114
				String str_value=recset.getString("str_value");
				String[] temp=str_value.split("\\|");
				fieldSetid=temp[0];
				String[] temp2=temp[1].split(",");
				actualNumberField=temp2[1];
				planNumberField=temp2[0];
			}
			if(actualNumberField!=null&&actualNumberField.length()!=0){
				if("#".equalsIgnoreCase(actualNumberField)){
					throw new GeneralException("没有设置岗位定员数指标！");
				}
			}
			if(planNumberField!=null&&planNumberField.length()!=0){
				if("#".equalsIgnoreCase(planNumberField)){
					throw new GeneralException("没有设置岗位实有人数指标！");
				}
			}
			if(fieldSetid.trim().length()>0)
			{
				recset=dao.search("select itemid,itemdesc from fielditem where itemid='"+actualNumberField+"' or itemid='"+planNumberField+"'");
				while(recset.next())
				{
					if(recset.getString("itemid").equalsIgnoreCase(actualNumberField)) {
                        actualNumberFieldName=recset.getString("itemdesc");
                    }
					if(recset.getString("itemid").equalsIgnoreCase(planNumberField)) {
                        planNumberFieldName=recset.getString("itemdesc");
                    }
				}
				StringBuffer sql=new StringBuffer("select organization.codeitemid,organization.codeitemdesc,");
				sql.append(fieldSetid+"."+actualNumberField+","+fieldSetid+"."+planNumberField+" from organization,"+fieldSetid);
				sql.append(",(select max(i9999) i9999,e01a1 from "+fieldSetid+" group by e01a1) T where organization.codeitemid="+fieldSetid+".E01A1 and   organization.codesetid='@K' ");
				sql.append(" and "+fieldSetid+".i9999=T.i9999 and "+fieldSetid+".e01a1=T.e01a1");
				if(codeid.indexOf("`")!=-1)
				{
					String [] temp=codeid.split("`");
					StringBuffer t_buf = new StringBuffer();
					for(int i=0;i<temp.length;i++)
					{
						if(temp[i]==null|| "".equals(temp[i])|| "null".equals(temp[i])) {
                            continue;
                        }
						t_buf.append(" or organization.codeitemid like '"+temp[i].substring(2)+"%'");
					}
					if(t_buf!=null&&t_buf.toString().length()>0) {
                        sql.append(" and ("+t_buf.toString().substring(3)+")");
                    }
				}
				else if(!"".equals(codeid))
				{
					
		    		sql.append(" and organization.codeitemid like '"+codeid.substring(2)+"%'");
				}
				
				String today = DateStyle.dateformat(new java.util.Date(),"yyyy-MM-dd");
				sql.append(" and ").append(Sql_switcher.dateValue(today)).append(" between organization.start_date and organization.end_date ");
				
				recset=dao.search(sql.toString());
				while(recset.next())
				{
					String a_codeitemid=recset.getString("codeitemid");
					LazyDynaBean abean=new LazyDynaBean();
					
					float actualNumber=0;
					float planNumber=0;
					if(recset.getString(actualNumberField)!=null) {
                        actualNumber=recset.getFloat(actualNumberField);
                    }
					if(recset.getString(planNumberField)!=null) {
                        planNumber=recset.getFloat(planNumberField);
                    }
					
					if(actualNumber>=planNumber) {
                        continue;
                    }
					String un_um_unid=getUN_UM_UNid_byPositionID(a_codeitemid);
					String[] tempDesc=un_um_unid.split("/");
					abean.set("UN",tempDesc[0]);
					abean.set("UNID",tempDesc[2]);
					abean.set("UM",tempDesc[1]);
					abean.set("UMID",tempDesc[3]);
					//部门分层级显示
				    CodeItem codeitem=AdminCode.getCode("UM", tempDesc[3],2);
				    if(codeitem!=null) {
                        abean.set("UM",codeitem.getCodename());
                    }

					abean.set("@K",recset.getString("codeitemdesc"));
					abean.set("actualNumber",PubFunc.round(String.valueOf(actualNumber), 0));
					abean.set("planNumber",PubFunc.round(String.valueOf(planNumber),0));
					abean.set("codeitemid",a_codeitemid);
					
					sparePositionList.add(abean);
					sparePositionMap.put(a_codeitemid,abean);
				}
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		list.add(sparePositionList);
		list.add(sparePositionMap);
		list.add(actualNumberFieldName+"~"+planNumberFieldName);
		return list;
	}
	
	
	
	
	/**
	 * 得到用户的直属单位
	 * @param userView
	 * @return
	 */
	public String getUnitID(UserView userView)
	{
		String unitID="";
		if("UN".equals(userView.getManagePrivCode()))
		{
			unitID=userView.getManagePrivCodeValue();
		}
		else
		{
			String temp=getUN_UM_UNid_byPositionID(userView.getManagePrivCodeValue());
			String[] tem=temp.split("/");
			unitID=tem[2];
		}
		
		return unitID;
	}
	
	/**
	 * 得到用户的直属单位
	 * @param userView
	 * @return
	 * update dml2012-3-31 11:42:49
	 */
	public ArrayList getUnitIDList(UserView userView) throws GeneralException
	{
		
		ArrayList unitList=new ArrayList();
		try
		{
			  ContentDAO dao=new ContentDAO(this.conn);			 
			  String unit=userView.getUnitIdByBusi("7");
			  if(unit==null|| "".equals(unit)|| "UN".equalsIgnoreCase(unit)){
				  throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
			  }
			  if(unit.trim().length()==3){//业务用户去操作单位的时候可能会出现全部 的情况
				  
					unitList.add("");
					return unitList;
			  }			  
			  unit = PubFunc.getTopOrgDept(unit);
			  String[] temp=unit.split("`");
			  
			 for(int i=0;i<temp.length;i++){
				 unitList.add(temp[i].substring(2));
			 }
			/** 
			 * 因增加业务管理范围修改方法，被注释重新修改为以上方法。可能考虑不周全 dml 2012年3月31日11:40:01
			 * */
//			 /**业务用户，取其操作单位*/
//			  if(userView.getStatus()==0){
//				String unitID=userView.getUnit_id();
//				/**没有操作单位*/
//				if(unitID==null||unitID.equals("")||unitID.equalsIgnoreCase("UN"))
//				{
//					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//				}
//				/**操作单位为全部*/
//				if(unitID.trim().length()==3)
//				{
//					unitList.add("");
//					return unitList;
//				}
//				/**一个或者多个操作单位*/
//				String[] temps=unitID.split("`");
//				for(int i=0;i<temps.length;i++)
//				{
//					if(temps[i]==null||temps[i].equals(""))
//						continue;
//					if(temps[i].substring(0,2).equalsIgnoreCase("UN"))
//						unitList.add(temps[i].substring(2));
//					else
//					{
//		    			 RowSet recset=null;
//		    			 boolean isOk=true;
//		    			 String codeitemid=temps[i].substring(2);
//		    			 while(isOk)
//			    		{
//			    			recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')");
//			    			if(recset.next())
//			    			{
//				    			codeitemid=recset.getString("codeitemid");								
//				    			if(recset.getString("codesetid").equalsIgnoreCase("UN"))
//				     			{
//				    				isOk=false;
//									
//			 	    			}			
//			    			}	
//			    		}
//			    		unitList.add(codeitemid);
//					}
//				}
//			}/**自助用户，取其管理范围代码*/
//			else if(userView.getStatus()==4)
//			{
//				String codeset=userView.getManagePrivCode();
//				String codevalue=userView.getManagePrivCodeValue();
//				/**没有管理范围*/
//				if(codeset==null||codeset.equals(""))
//				{
//					throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//				}
//				else
//				{
//					/**管理范围为单位*/
//					if(codeset.equalsIgnoreCase("UN"))
//					{
//						unitList.add(codevalue==null?"":codevalue);
//					}
//					else
//					{
//						 RowSet recset=null;
//		    			 boolean isOk=true;
//		    			 while(isOk)
//			    		{
//			    			recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codevalue+"')");
//			    			if(recset.next())
//			    			{
//			    				codevalue=recset.getString("codeitemid");								
//				    			if(recset.getString("codesetid").equalsIgnoreCase("UN"))
//				     			{
//				    				isOk=false;
//									
//			 	    			}			
//			    			}	
//			    		}
//			    		unitList.add(codevalue);
//					}
//				}
//			}
	/*		if(unitID!=null&&unitID.length()>2)
			{
				String[] temps=unitID.split("`");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].substring(0,2).equalsIgnoreCase("UN"))
						unitList.add(temps[i].substring(2));
					else
					{
						boolean isOk=true;
						ContentDAO dao=new ContentDAO(this.conn);
						RowSet recset=null;
						String codeitemid=temps[i].substring(2);
						
						while(isOk)
						{
							recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')");
							if(recset.next())
							{
								codeitemid=recset.getString("codeitemid");								
								if(recset.getString("codesetid").equalsIgnoreCase("UN"))
								{
										isOk=false;
									
								}			
							}	
						}
						unitList.add(codeitemid);
					}
					
				}
				
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return unitList;
	}
	
	
	/**
	 * 得到用户的直属单位
	 * @param userView
	 * @return
	 */
	public ArrayList getUnitIDList2(UserView userView) throws GeneralException
	{
		
		ArrayList unitList=new ArrayList();
		try
		{
			 ContentDAO dao=new ContentDAO(this.conn);
			 /**业务用户，取其操作单位*/
			 String unit=userView.getUnitIdByBusi("7");
			  if(unit==null|| "".equals(unit)|| "UN".equalsIgnoreCase(unit)){
				  unitList.add("-1");
				  return unitList;
			  }
			  if(unit.trim().length()==3){//业务用户去操作单位的时候可能会出现全部 的情况
				  
					unitList.add("");
					return unitList;
			  }			  
			  unit = PubFunc.getTopOrgDept(unit);
			  String[] temp=unit.split("`");
			  
			 for(int i=0;i<temp.length;i++){
				 unitList.add(temp[i].substring(2));
			 }
			 
			 /**
				 * modify dml 2012-3-31 15:50:33
				 * reason 因增加业务管理范围导致权限规则改边
				 * */
//			if(userView.getStatus()==0)
//			{
//				String unitID=userView.getUnit_id();
//				/**没有操作单位*/
//				if(unitID==null||unitID.equals("")||unitID.equalsIgnoreCase("UN"))
//				{
//					//throw GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					unitList.add("-1");
//					return unitList;
//				}
//				/**操作单位为全部*/
//				if(unitID.trim().length()==3)
//				{
//					unitList.add("");
//					return unitList;
//				}
//				/**一个或者多个操作单位*/
//				String[] temps=unitID.split("`");
//				for(int i=0;i<temps.length;i++)
//				{
//					if(temps[i]==null||temps[i].equals(""))
//						continue;
//					
//					unitList.add(temps[i].substring(2));
//				}
//			}/**自助用户，取其管理范围代码*/
//			else if(userView.getStatus()==4)
//			{
//				String codeset=userView.getManagePrivCode();
//				String codevalue=userView.getManagePrivCodeValue();
//				/**没有管理范围*/
//				if(codeset==null||codeset.equals(""))
//				{
//					//hrow GeneralExceptionHandler.Handle(new Exception("您没有设置招聘模块的管理范围"));
//					unitList.add("-1");
//					return unitList;
//				}
//				else
//				{
//					
//						unitList.add(codevalue==null?"":codevalue);
//				}
//			}
	/*		if(unitID!=null&&unitID.length()>2)
			{
				String[] temps=unitID.split("`");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].substring(0,2).equalsIgnoreCase("UN"))
						unitList.add(temps[i].substring(2));
					else
					{
						boolean isOk=true;
						ContentDAO dao=new ContentDAO(this.conn);
						RowSet recset=null;
						String codeitemid=temps[i].substring(2);
						
						while(isOk)
						{
							recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')");
							if(recset.next())
							{
								codeitemid=recset.getString("codeitemid");								
								if(recset.getString("codesetid").equalsIgnoreCase("UN"))
								{
										isOk=false;
									
								}			
							}	
						}
						unitList.add(codeitemid);
					}
					
				}
				
			}*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
		return unitList;
	}
	
	/**
	 * 通过职位id找到其所在的部门和单位及单位的id
	 * @param codeitemid
	 * @return
	 */
	public String getUN_UM_UNid_byPositionID(String codeitemid)
	{
		String un="";
		String um="";
		String UNid="";
		String UMid="";
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		try
		{
			boolean isOk=true;
			
			while(isOk)
			{
				recset=dao.search("select * from organization where codeitemid=(select parentid from organization where codeitemid='"+codeitemid+"')");
				if(recset.next())
				{
					codeitemid=recset.getString("codeitemid");
					if("UM".equalsIgnoreCase(recset.getString("codesetid")))
					{
						if(um.length()==0)
						{
							um=recset.getString("codeitemdesc");
							UMid=recset.getString("codeitemid");
						}
					}
					if("UN".equalsIgnoreCase(recset.getString("codesetid")))
					{
						if(un.length()==0)
						{
							un=recset.getString("codeitemdesc");
							UNid=recset.getString("codeitemid");
							isOk=false;
						}
					}
				}	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if("".equals(um) && "".equals(UMid)){//如果岗位直接挂在单位下
			um = " ";
			UMid = " ";
		}
		return un+"/"+um+"/"+UNid+"/"+UMid;
	}
	
	
	
	/**
	 * 得到 每个职位需求 实招人数。
	 * @param z0101
	 * @param dbname
	 * @return
	 */
	public HashMap getPositionActualNum(String z0101,String dbname)
	{
		HashMap map=new HashMap();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		try
		{
			StringBuffer sql=new StringBuffer("select count(zpt.a0100) num,zpt.zp_pos_id from zp_pos_tache zpt,"+dbname+"a01");
						sql.append(" where zpt.a0100="+dbname+"a01.a0100 and zpt.zp_pos_id in (select z0301 from z03 where z0101='"+z0101+"' ) ");
						sql.append(" and "+dbname+"a01.state='43' group by zpt.zp_pos_id");
			
			recset=dao.search(sql.toString());
			while(recset.next())
			{
				map.put(recset.getString("zp_pos_id"),recset.getString("num"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	
	
	
	/**
	 * 列出已审批，但未引用的需求以及已引入本计划中的需求；
	 * @param fieldList
	 * @param z0101
	 * @return
	 */
	public ArrayList getDataList(ArrayList fieldList,String z0101,String operate,String dbname)
	{
		ArrayList dataList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		HashMap actualNumMap=new HashMap();
		try
		{
			if(operate!=null) {
                actualNumMap=getPositionActualNum(z0101,dbname);
            }
			StringBuffer sql=new StringBuffer("select * from z03 ");
			if(operate==null)
			{
				sql.append(" where z0319<>'01' and  z0319<>'02' and z0319<>'07' ");
                sql.append(" and ( z0101 is null or z0101='' or  z0101='"+z0101+"' )  order by z0101 desc,z0319  ");
			}
			else
			{
				sql.append(" where z0101='"+z0101+"' ");
			}
			
			
			recset=dao.search(sql.toString());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy.MM.dd"); 
			while(recset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
				
					if("0".equals(item.getCodesetid())&&("A".equals(item.getItemtype())|| "N".equals(item.getItemtype())))   //字符型 或 数字
					{
						if("actualNum".equalsIgnoreCase(item.getItemid()))
						{
							if(actualNumMap.get(recset.getString("z0301"))!=null)
							{
								abean.set(item.getItemid(),(String)actualNumMap.get(recset.getString("z0301")));
							}
							else {
                                abean.set(item.getItemid(),"0");
                            }
						}
						else
						{
							if(recset.getString(item.getItemid())!=null) {
                                abean.set(item.getItemid(),recset.getString(item.getItemid()));
                            } else {
                                abean.set(item.getItemid(),"");
                            }
						}
					}
					if(!"0".equals(item.getCodesetid())&& "A".equals(item.getItemtype()))  //代码型的
					{
						if(recset.getString(item.getItemid())!=null&&recset.getString(item.getItemid()).trim().length()>0) {
                            abean.set(item.getItemid(),AdminCode.getCodeName(item.getCodesetid(),recset.getString(item.getItemid())));
                        } else {
                            abean.set(item.getItemid(),"");
                        }
					}
					if("0".equals(item.getCodesetid())&& "M".equals(item.getItemtype()))   //大字段
					{
						if(recset.getString(item.getItemid())!=null) {
                            abean.set(item.getItemid(),Sql_switcher.readMemo(recset,item.getItemid()));
                        } else {
                            abean.set(item.getItemid(),"");
                        }
					}
					if("0".equals(item.getCodesetid())&& "D".equals(item.getItemtype()))   //日期
					{
						if(recset.getDate(item.getItemid())!=null)
						{
							Date date=recset.getDate(item.getItemid());
							abean.set(item.getItemid(),bartDateFormat.format(date));
						}
						else {
                            abean.set(item.getItemid(),"");
                        }
					}
				}
				dataList.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return dataList;
	}
	
	
	public ArrayList getDataList2(ArrayList fieldList,String z0101,String operate,String dbname,UserView userView)
	{
		ArrayList dataList=new ArrayList();
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;	
		HashMap actualNumMap=new HashMap();
		try
		{
			if(operate!=null) {
                actualNumMap=getPositionActualNum(z0101,dbname);
            }
			StringBuffer sql=new StringBuffer("select z03.*,z0319 z0319a,z03.z0311 z0311a from z03 ");
			if(operate==null)
			{
				sql.append(" where z0319<>'01' and  z0319<>'02' and z0319<>'07' ");
                sql.append(" and ( z0101 is null or z0101='' or  z0101='"+z0101+"' )  ");
			}
			else
			{
				sql.append(" where z0101='"+z0101+"' ");
			}
			String codeid="";
			/**加入招聘计划所属单位的限制*/
			RecordVo vo = new RecordVo("z01");
			vo.setString("z0101",z0101);
			vo=dao.findByPrimaryKey(vo);
			String z0105=vo.getString("z0105")==null?"":vo.getString("z0105");
			/**所属单位限制,原来是有限制的，现在考虑所属单位基本不用，，没有数据，所以先去掉吧2009-07-04*/
			//sql.append(" and z0303 like '"+z0105+"%' ");
			if(userView.isSuper_admin()|| "1".equals(userView.getGroupId())){
				
			}
			else{
				codeid= userView.getUnitIdByBusi("7");
				if(codeid==null||codeid.trim().length()==0)
				{
					sql.append(" and 1=2 ");
				}else if(codeid.trim().length()==3)
				{
					codeid="";
				}else if(codeid.indexOf("`")==-1)
				{
            		codeid=codeid.substring(2);
          //  		sql.append(" and z0325 like '"+codeid+"%' ");   //当岗位挂在单位下时，会查询不到  2013-11-17 dengc
            		sql.append(" and z0311 like '"+codeid+"%' "); 
				}
				else
				{
					StringBuffer tempsql=new StringBuffer("");
					String[] temp=codeid.split("`");
					for(int i=0;i<temp.length;i++)
					{
						if(temp[i]==null|| "".equals(temp[i])) {
                            continue;
                        }
						if(temp[i].startsWith("UN")||temp[i].startsWith("UM")){
							tempsql.append(" or Z0311 like '"+temp[i].substring(2)+"%'");
						//	tempsql.append(" or Z0325 like '"+temp[i].substring(2)+"%'");   //当岗位挂在单位下时，会查询不到  2013-11-17 dengc
						}else{
							tempsql.append(" or Z0311 like '"+temp[i]+"%'");
						//	tempsql.append(" or Z0325 like '"+temp[i]+"%'");   //当岗位挂在单位下时，会查询不到  2013-11-17 dengc
						}
						
					}
					sql.append(" and ( "+tempsql.substring(3)+" ) ");
				}
				
				
//				if(userView.getStatus()==0/*operateType.equals("user")*/)
//				{
//					codeid=userView.getUnit_id();
//					if(codeid==null||codeid.trim().length()==0)
//					{
//						sql.append(" and 1=2 ");
//					}else if(codeid.trim().length()==3)
//					{
//						codeid="";
//					}
//					else if(codeid.indexOf("`")==-1)
//					{
//	            		codeid=codeid.substring(2);
//	            		sql.append(" and z0325 like '"+codeid+"%' ");
//					}
//					else
//					{
//						StringBuffer tempsql=new StringBuffer("");
//						String[] temp=codeid.split("`");
//						for(int i=0;i<temp.length;i++)
//						{
//							if(temp[i]==null||temp[i].equals(""))
//								continue;
//							tempsql.append(" or Z0325 like '"+temp[i].substring(2)+"%'");
//						}
//						sql.append(" and ( "+tempsql.substring(3)+" ) ");
//					}
//				}
//				else
//				{
//					String codesetid="";
//					codeid=userView.getManagePrivCodeValue();
//					codesetid=userView.getManagePrivCode();
//					if((codesetid==null||codesetid.trim().length()==0)&&(codeid==null||codeid.trim().length()==0))
//					{
//						sql.append(" and 1=2 ");
//					}
//					else 
//					{ 
//						if(codeid==null||codeid.equals(""))
//						{
//							
//						}
//						else
//						{
//							sql.append(" and z0325 like '"+codeid+"%' ");
//						}
//						
//					}
//				}
			}
			sql.append(" order by z0101 desc,z0319 ");
			recset=dao.search(sql.toString());
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy.MM.dd"); 
			while(recset.next())
			{
				LazyDynaBean abean=new LazyDynaBean();
				for(int i=0;i<fieldList.size();i++)
				{
					FieldItem item=(FieldItem)fieldList.get(i);
					if("0".equals(item.getCodesetid())&&("A".equals(item.getItemtype())|| "N".equals(item.getItemtype())))   //字符型 或 数字
					{
						if("actualNum".equalsIgnoreCase(item.getItemid()))
						{
							if(actualNumMap.get(recset.getString("z0301"))!=null)
							{
								abean.set(item.getItemid(),(String)actualNumMap.get(recset.getString("z0301")));
							}
							else {
                                abean.set(item.getItemid(),"0");
                            }
						}
						else
						{
							if(recset.getString(item.getItemid())!=null) {
                                abean.set(item.getItemid(),recset.getString(item.getItemid()));
                            } else {
                                abean.set(item.getItemid(),"");
                            }
						}
					}
					if(!"0".equals(item.getCodesetid())&& "A".equals(item.getItemtype()))  //代码型的
					{
						if(recset.getString(item.getItemid())!=null&&recset.getString(item.getItemid()).trim().length()>0) {
                            abean.set(item.getItemid(),AdminCode.getCodeName(item.getCodesetid(),recset.getString(item.getItemid())));
                        } else {
                            abean.set(item.getItemid(),"");
                        }
					}
					if("0".equals(item.getCodesetid())&& "M".equals(item.getItemtype()))   //大字段
					{
						if(recset.getString(item.getItemid())!=null) {
                            abean.set(item.getItemid(),Sql_switcher.readMemo(recset,item.getItemid()));
                        } else {
                            abean.set(item.getItemid(),"");
                        }
					}
					if("0".equals(item.getCodesetid())&& "D".equals(item.getItemtype()))   //日期
					{
						if(recset.getDate(item.getItemid())!=null)
						{
							Date date=recset.getDate(item.getItemid());
							abean.set(item.getItemid(),bartDateFormat.format(date));
						}
						else {
                            abean.set(item.getItemid(),"");
                        }
					}
				}
				abean.set("z0319a",recset.getString("z0319a"));
				abean.set("z0311a",recset.getString("z0311a"));
				dataList.add(abean);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return dataList;
	}
	
	
	
	
	
/** *******************************重新排序************************************* */	
	
	
	
	
	
	
	/**
	 * 对单表数据进行重新排序
	 * @param taxisColumns 排序列号
	 * @param tableName	   表名
	 * @param fasion	   排序方式 //1：升序  0：降序
	 * @param primaryKey   主键名称
	 */
/*	public boolean  afreshTaxis(ArrayList taxisColumns,String tableName,String fasion,String primaryKey)
	{
		boolean isSuccess=true;
		ContentDAO dao=new ContentDAO(this.conn);
		RowSet recset=null;		
		try
		{
			String a_fasion="ASC";
			if(fasion.equals("0"))
				a_fasion="desc";
			StringBuffer ext_sql=new StringBuffer("");
			for(int i=0;i<taxisColumns.size();i++)
			{
				ext_sql.append(","+(String)taxisColumns.get(i)+" "+a_fasion);
			}
			String sql="select * from "+tableName+" order by "+ext_sql.substring(1);
			recset=dao.search(sql);
			updateMusterRecidx(tableName,primaryKey);
		}
		catch(Exception e)
		{
			isSuccess=false;
			e.printStackTrace();
		}
		return isSuccess;
	}*/
	
	
	
	
	
	
	
	
	
	
	/**
	 * 更新表的主键序号
	 * @param name		 表名称
	 * @param columnName 主键名称
	 */
	public void updateMusterRecidx(String name,String columnName)throws GeneralException
	{
		StringBuffer strsql=new StringBuffer();
		try
		{
			DbWizard db=new DbWizard(this.conn);
			Table table=getTable(name,columnName);
			db.dropPrimaryKey(table);
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" add xxx int identity(1,1)");
				break;
			default:				
				strsql.append("create sequence xxx increment by 1 start with 1");
				break;
			}
			
			db.execute(strsql.toString());
			strsql.setLength(0);
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set "+columnName+"=xxx");
				break;
			case Constant.DB2:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set "+columnName+"=nextval for xxx");			
				break;
			case Constant.ORACEL:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set "+columnName+"=xxx.nextval");					
				break;
			default:
				strsql.append("update ");
				strsql.append(name);
				strsql.append(" set "+columnName+"=xxx");
				break;
			}	
			db.execute(strsql.toString());	
			strsql.setLength(0);			
			switch(dbflag)
			{
			case Constant.MSSQL:
				strsql.append("alter table ");
				strsql.append(name);
				strsql.append(" drop column xxx");
				break;
			default:
				strsql.append(" drop sequence xxx");
				break;
			}		
			db.execute(strsql.toString());	
			//导完数据后，再建主键
			db.addPrimaryKey(table);
			//重新加载数据模型
			DBMetaModel dbmodel=new DBMetaModel(this.conn);
			dbmodel.reloadTableModel(name);
			
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		    throw GeneralExceptionHandler.Handle(ex);			
		}
	
	}
	
	
	
	
/***************************************************************************************/	
	

	public Table getTable(String name,String primaryKey)
	{
		DbWizard db=new DbWizard(this.conn);
		Table table=new Table(name);			
		ArrayList list=DataDictionary.getFieldList(name,Constant.USED_FIELD_SET);
		for(int i=0;i<list.size();i++)
		{
			FieldItem item=(FieldItem)list.get(i);
			Field field=(Field)item.cloneField();
			if(item.getItemid().equalsIgnoreCase(primaryKey))
			{
				field.setKeyable(true);
			}
			table.addField(field);
		}
		
		return table;
	}
	
	private Document doc;
	/**
	 * 组建xml参数
	 * @param a0100
	 * @param nbase
	 * @param content
	 * @param z0301
	 * @param sp_flag
	 * @return
	 */
	public String createXML(UserView view,String content,String z0301,String sp_flag)
	{
		StringBuffer buf = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			StringBuffer temp_xml=new StringBuffer();
			temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
			temp_xml.append("<root>");
			temp_xml.append("</root>");	
			 StringBuffer sql=new StringBuffer("select reasons  from z03 where z0301='"+z0301+"'");
				
			 RowSet rowSet=dao.search(sql.toString());
			 boolean isValue=false;
			 String xml="";
			 if(rowSet.next())
			 {
				 String a_xml=Sql_switcher.readMemo(rowSet,"reasons");
				 if(a_xml.trim().length()>0)
				 {
					xml=a_xml.toString();
				 }
				 else {
                     xml=temp_xml.toString();
                 }
			 }
			 if(xml.trim().length()<=0) {
                 xml=temp_xml.toString();
             }
			 doc = PubFunc.generateDom(xml);
			 Element root = this.doc.getRootElement();
			 {
					
					
				 Element element=new Element("rec");
				 /**可以取a0100和Dbname*/
				 if(view.getA0100()==null|| "".equals(view.getA0100()))
				 {
					 element.setAttribute("a0100"," ");
					 element.setAttribute("name",view.getUserFullName());
				 }
				 else
				 {
					 element.setAttribute("a0100",view.getA0100());
					 element.setAttribute("name",getName(view.getA0100(),view.getDbname()));
				 }
				 
				 SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				 element.setAttribute("date",df.format(new java.util.Date()));
				 element.setAttribute("sp_flag",sp_flag);
				 element.setText(content);
				 root.addContent(element);
			 }
			 XMLOutputter outputter=new XMLOutputter();
			 Format format=Format.getPrettyFormat();
			 format.setEncoding("UTF-8");
			 outputter.setFormat(format);
			 buf.append(outputter.outputString(doc));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 组建xml参数
	 * @param a0100
	 * @param nbase
	 * @param content
	 * @param z0301
	 * @param sp_flag
	 * @return
	 */
	public String createXMLTarget(UserView view,String content,String z0301,String sp_flag,String target)
	{
		StringBuffer buf = new StringBuffer();
		ContentDAO dao = new ContentDAO(this.conn);
		try
		{
			StringBuffer temp_xml=new StringBuffer();
			temp_xml.append("<?xml version='1.0' encoding='GB2312' ?>");
			temp_xml.append("<root>");
			temp_xml.append("</root>");	
			 StringBuffer sql=new StringBuffer("select reasons  from z03 where z0301='"+z0301+"'");
				
			 RowSet rowSet=dao.search(sql.toString());
			 boolean isValue=false;
			 String xml="";
			 if(rowSet.next())
			 {
				 String a_xml=Sql_switcher.readMemo(rowSet,"reasons");
				 if(a_xml.trim().length()>0)
				 {
					xml=a_xml.toString();
				 }
				 else {
                     xml=temp_xml.toString();
                 }
			 }
			 if(xml.trim().length()<=0) {
                 xml=temp_xml.toString();
             }
			 doc = PubFunc.generateDom(xml);
			 Element root = this.doc.getRootElement();
			 {
					
					
				 Element element=new Element("rec");
				 /**可以取a0100和Dbname*/
				 if(view.getA0100()==null|| "".equals(view.getA0100()))
				 {
					 element.setAttribute("a0100"," ");
					 element.setAttribute("name",view.getUserFullName());
				 }
				 else
				 {
					 element.setAttribute("a0100",view.getA0100());
					 element.setAttribute("name",getName(view.getA0100(),view.getDbname()));
				 }
				 
				 SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
				 element.setAttribute("date",df.format(new java.util.Date()));
				 element.setAttribute("sp_flag",sp_flag);
				 element.setAttribute("target",target);
				 element.setText(content);
				 root.addContent(element);
			 }
			 XMLOutputter outputter=new XMLOutputter();
			 Format format=Format.getPrettyFormat();
			 format.setEncoding("UTF-8");
			 outputter.setFormat(format);
			 buf.append(outputter.outputString(doc));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 取得人员的基本信息
	 * @param a0100
	 * @param nbase
	 * @return
	 */
	public String getName(String a0100,String nbase)
	{
		String str="";
		try
		{
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search("select * from "+nbase+"A01 where a0100='"+a0100+"'");
			 if(rowSet.next())
			 {
				 if(rowSet.getString("b0110")!=null) {
                     str+=AdminCode.getCodeName("UN",rowSet.getString("b0110"));
                 }
				 str+="/";
				 if(rowSet.getString("e0122")!=null) {
                     str+=AdminCode.getCodeName("UM",rowSet.getString("e0122"));
                 }
				 str+="/";
				 if(rowSet.getString("e01a1")!=null) {
                     str+=AdminCode.getCodeName("@K",rowSet.getString("e01a1"));
                 }
				 str+="/";
				 str+=rowSet.getString("a0101");
			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 保存新建的xml参数
	 * @param z0301
	 * @param xml
	 */
	public void saveXML(String z0301,String xml)
	{
		try
		{
			String sql = "update z03 set reasons=? where z0301='"+z0301+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList list = new ArrayList();
			list.add(xml);
			dao.update(sql,list);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
/**
 * zzk 查看当前用户是否是选中记录的当前操作人员
 * @param z0301
 * @param userView
 */
	public void checkCanOperate(String z0301,UserView userView)throws GeneralException
	{
		try
		{	
			ResultSet res=null;
			if(!userView.isSuper_admin()&&!userView.haveTheRoleProperty("8")){
				String sql ="select * from z03 where z0301='"+z0301+"'";
				ContentDAO dao = new ContentDAO(this.conn);
				res=dao.search(sql);
				String flag="0";
				if(res.next()){
					String currappuser=res.getString("currappuser");
					String currappusername=res.getString("currappusername");
			    	if(currappuser!=null&&!"".equals(currappuser.trim())){
		    		if(currappuser.length()>3){
						if("USR".equalsIgnoreCase(currappuser.substring(0,3))){//自助用户
							if(!userView.getA0100().equalsIgnoreCase(currappuser.substring(3))){
								flag="1";
							}
						}else{
			    			if(!userView.getUserName().equalsIgnoreCase(currappuser)){

			    				flag="1";
			    			}
						}
		    		}else{
		    			if(!userView.getUserName().equalsIgnoreCase(currappuser)){
		    				flag="1";
		    			}
		    		}
		    	}else{
		    		String name=userView.getUserFullName()!=null&&!"".equals(userView.getUserFullName().trim())?userView.getUserFullName():userView.getUserName();
	    			if(!name.equalsIgnoreCase(currappusername)){
	    				flag="1";
	    			}
		    	}
			    	
			    	
			    	if("1".equals(flag)){
			    	    String z0321=res.getString("z0321");//需求单位
			    	    String z0325=res.getString("z0325");//需求部门
			    	    String z0311=res.getString("z0311");//需求岗位 
			    	    String z0336=res.getString("z0336");//招聘渠道
			    	    String clientName=SystemConfig.getPropertyValue("clientName");
			    	    String z03Z7="";
			    	    if(clientName!=null&& "hkyh".equalsIgnoreCase(clientName)){//为汉口银行做的专版
			    	    	z03Z7= res.getString("z03Z7");//招聘类型
			    	    }
			    	    z0311=AdminCode.getCode("@K",z0311)!=null?AdminCode.getCode("@K",z0311).getCodename():"";
						ParameterXMLBo bo2=new ParameterXMLBo(this.conn,"1");
						HashMap map=bo2.getAttributeValues();
						String hireMajor="";//招聘专业
						if(map.get("hireMajor")!=null&&((String)map.get("hireMajor")).length()>0) {
                            hireMajor=(String)map.get("hireMajor");
                        }
						hireMajor=res.getString(hireMajor);
						String message="";
						if(!"".equals(z03Z7)){
							message=AdminCode.getCode("ZD",z03Z7)!=null?AdminCode.getCode("ZD",z03Z7).getCodename():"";
						}else{
							message=AdminCode.getCode("35",z0336)!=null?AdminCode.getCode("35",z0336).getCodename():"";
						}
			  			if("01".equals(z0336)){
			  			    if("".equalsIgnoreCase(message))//保留这些东西防止有人删除代码类
                            {
                                message = "校园招聘";
                            }
			  			    
			  				message += "-需求专业:"+hireMajor+" \n当前操作人为:"+currappusername+"; 您无权进行操作!";
			  			}else{
			  			    if("".equalsIgnoreCase(message)) {
                                message = "社会招聘";
                            }
			  			  
			  				message += "-需求岗位:"+z0311+" \n当前操作人为:"+currappusername+"; 您无权进行操作!";
			  			}
			  			 throw GeneralExceptionHandler.Handle(new Exception(message));
			    	}
			 }
				
		
		}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	}
	/**
	 * 设置当前操作用户
	 * @param z0301
	 * @param appcurrUser
	 */
	public void updateCurrAppUser(String z0301,String appcurrUser)
	{
		try
		{
			String sql  = "update z03 set currappuser='"+appcurrUser+"' where z0301='"+z0301+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			dao.update(sql);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 或得驳回对象
	 * @param z0301
	 * @return
	 * @throws GeneralException 
	 */
	public String getRejectTarget(String z0301) throws GeneralException{
		String target="";
		String sql = "select z0311,z0321,z0325,appuser from z03 where z0301='"+z0301+"'";
		ContentDAO dao = new ContentDAO(this.conn);
		RowSet rs=null;
		String a0101_name="";
		String a0100="";
		String fullname="";
		try {
			rs = dao.search(sql);
			String appuser="";
			while(rs.next())
			{
				if(rs.getString("appuser")==null){
					appuser=null;
				}else{
					appuser=rs.getString("appuser").trim();
				}
			}
			if(appuser==null|| "".equals(appuser)) {
                throw GeneralExceptionHandler.Handle(new Exception("没有驳回对象，无需驳回!"));
            }
			
			if(appuser.startsWith(",")) {
                appuser=appuser.substring(1);
            }
			String[] appUserArr=appuser.split(",");
			String info ="";
			if(appUserArr.length>1)
			{
	           info = appUserArr[1]; 
			}
			else
			{
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
				HashMap map=parameterXMLBo.getAttributeValues();
				String moreLevelSP="0";
				if(map!=null&&map.get("moreLevelSP")!=null) {
                    moreLevelSP=(String)map.get("moreLevelSP");
                }
				info = appUserArr[0]; 
				if("1".equals(moreLevelSP))//支持多级审批时 审批人仅剩一个时无须驳回
                {
                    throw GeneralExceptionHandler.Handle(new Exception("没有驳回对象，无需驳回!"));
                }
			}
			String app_user=appuser.substring(appUserArr[0].length());
			if(app_user.startsWith(",")) {
                app_user=app_user.substring(1);
            }
			dao.update("update z03 set z0319='07' ,appuser='"+app_user+"',currappuser='"+info+"' where z0301='"+z0301+"'");
			
			String type = getUserType(info);

			/** 是自助用户 */
			if ("1".equalsIgnoreCase(type)) {
				sql = "select b0110,e0122,e01a1,A0101 from " + info.substring(0, 3) + "A01 where A0100='" + info.substring(3) + "'";
				rs = dao.search(sql);
				if (rs.next()) {
					// b0110_name=this.frowset.getString("b0110")!=null?this.frowset.getString("b0110"):"";
					// e0122_name=this.frowset.getString("e0122")!=null?this.frowset.getString("e0122"):"";
					// e01a1_name=this.frowset.getString("e01a1")!=null?this.frowset.getString("e01a1"):"";
					a0101_name = rs.getString("a0101") != null ? rs.getString("a0101") : "";
					// b0110_name=AdminCode.getCodeName("UN",b0110_name);
					// e0122_name=AdminCode.getCodeName("UM",e0122_name);
					// e01a1_name=AdminCode.getCodeName("@K",e01a1_name);
					// if(!b0110_name.equals(""))
					// target+=b0110_name+"/";
					// if(!e0122_name.equals(""))
					// target+=e0122_name+"/";
					// if(!e01a1_name.equals(""))
					// target+=e01a1_name+"/";
					if (!"".equals(a0101_name)) {
                        target += a0101_name;
                    }
				}
				target = target + ":"+info;
			} else {
				// 业务用户
				sql = "select nbase,a0100,fullname,username from OperUser where username='" + info + "'";
				rs = dao.search(sql);
				if (rs.next()) {
					a0100 = rs.getString("a0100") != null ? rs.getString("a0100") : "";
					fullname = rs.getString("fullname") != null ? rs.getString("fullname") : "";
					String nbase = rs.getString("nbase") != null ? rs.getString("nbase") : "";
					if (!"".equals(a0100)) {
						sql = "select a0101 from " + nbase + "A01 where a0100='" + a0100 + "'";
						ResultSet res = dao.search(sql);
						if (res.next()) {
							target = res.getString("a0101");
						}
					} else if (!"".equals(fullname)) {
						target = fullname;
					} else {
						target = info;
					}
				}
				target = target + ":"+info;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return target;
	
	}
	public HashMap getNbaseMap()
	{
		HashMap map = new HashMap();
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search("select pre from dbname ");
			while(rs.next())
			{
				map.put(rs.getString("pre").toUpperCase(), "1");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public void rejectByLayer(String z0301,UserView userView,String url_p)
	{
		try
		{
			String sql = "select z0311,z0321,z0325,appuser,currappuser from z03 where z0301='"+z0301+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			String currappuser="";
			while(rs.next())
			{
				currappuser=rs.getString("currappuser");
			}
		
			HashMap nbaseMap = this.getNbaseMap();
			String info =currappuser;
			if(info.length()>3)
			{
				String pre=info.substring(0,3);
				/**是自助用户*/
				if(nbaseMap.get(pre.toUpperCase())!=null)
				{
					String content=this.getRejectEmailContent(userView.getUserFullName(), z0301);
					content+="<br><br><a href='"+url_p+"hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id="+z0301+"&appfwd=1'>自动登录操作页面</a>";
					this.sendMessage(content, info.substring(3), info.substring(0,3),2);
				//	this.updateCurrAppUser(z0301, info);
				}
				else
				{
					String name="";
					if(userView.getUserFullName()==null||userView.getUserFullName().length()==0){
						name=userView.getUserName();
					}else{
						name=userView.getUserFullName();
					}
					String content=this.getRejectEmailContent(name, z0301);
					content+="<br><br><a href='"+url_p+"hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id="+z0301+"&appfwd=1'>自动登录操作页面</a>";
					this.setOperuserMessage(content, info, 2);
				//	this.updateCurrAppUser(z0301, info);
				}
			}
			else
			{
				String name="";
				if(userView.getUserFullName()==null||userView.getUserFullName().length()==0){
					name=userView.getUserName();
				}else{
					name=userView.getUserFullName();
				}
				String content=this.getRejectEmailContent(name, z0301);
				content+="<br><br><a href='"+url_p+"hire/demandPlan/positionDemand/auto_logon_sp.do?b_query=query&id="+z0301+"&appfwd=1'>自动登录操作页面</a>";
				this.setOperuserMessage(content, info, 2);
			//	this.updateCurrAppUser(z0301, info);
			}
//			StringBuffer appuserContent = new StringBuffer("");
//			for(int i=0;i<appUserArr.length;i++)
//			{
//				if(i!=0)
//				{
//					appuserContent.append(appUserArr[i]+",");
//				}
//			}
//			if(appuserContent.toString().length()>0)
//			{
//				appuserContent.setLength(appuserContent.length()-1);
//			}
//			String updateSQL = "update z03 set appuser='"+appuserContent.toString()+"' where z0301='"+z0301+"'";
//			dao.update(updateSQL);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 短信发送驳回通知
	 * @param z0301
	 * @return
	 * @author dml2011-05-13
	 */
	public void rejectByMessage(String z0301,UserView userView){
		try
		{
			String sql = "select z0311,z0321,z0325,currappuser from z03 where z0301='"+z0301+"'";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			String currappuser="";
			while(rs.next())
			{
				currappuser=rs.getString("currappuser");
			}
	
			HashMap nbaseMap = this.getNbaseMap();
			String info =currappuser;
		
			String name="";
			if(info.length()>3)
			{
				String pre=info.substring(0,3);
				/**是自助用户*/
				if(nbaseMap.get(pre.toUpperCase())!=null)
				{
					String content=this.getRejectMessageContent(userView.getUserFullName(), z0301);
					this.sendShortMessage(content, info.substring(3), info.substring(0,3), 1, userView.getUserFullName());
//					this.updateCurrAppUser(z0301, info);
				}
				else
				{
					
					if(userView.getUserFullName()==null||userView.getUserFullName().length()==0){
						name=userView.getUserName();
					}else{
						name=userView.getUserFullName();
					}
					String content=this.getRejectMessageContent(name, z0301);
					this.sendShortMessage(content, info, "", 2, userView.getUserFullName());
//					this.updateCurrAppUser(z0301, info);
				}
			}
			else
			{
				
				if(userView.getUserFullName()==null||userView.getUserFullName().length()==0){
					name=userView.getUserName();
				}else{
					name=userView.getUserFullName();
				}
				String content=this.getRejectMessageContent(name, z0301);
				this.sendShortMessage(content, info, "", 2, userView.getUserFullName());
//				this.updateCurrAppUser(z0301, info);
			}
//			StringBuffer appuserContent = new StringBuffer("");
//			for(int i=0;i<appUserArr.length;i++)
//			{
//				if(i!=0)
//				{
//					appuserContent.append(appUserArr[i]+",");
//				}
//			}
//			if(appuserContent.toString().length()>0)
//			{
//				appuserContent.setLength(appuserContent.length()-1);
//			}
//			String updateSQL = "update z03 set appuser='"+appuserContent.toString()+"' where z0301='"+z0301+"'";
//			dao.update(updateSQL);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 取得各个审批人的操作记录
	 * @param z0301
	 * @return
	 */
	public String getReasons(String z0301)
	{
		StringBuffer return_buf = new StringBuffer("");
		try
		{
			 StringBuffer buf = new StringBuffer("");
			 StringBuffer sql=new StringBuffer("select reasons  from z03 where z0301='"+z0301+"'");
			 ContentDAO dao = new ContentDAO(this.conn);
			 RowSet rowSet=dao.search(sql.toString());
			 String content="";
			 while(rowSet.next())
			 {
			    content=Sql_switcher.readMemo(rowSet,"reasons");
			 }
			 if(content.trim().length()<=0)
			 {
				 StringBuffer temp_xml=new StringBuffer();
				 temp_xml.append("<?xml version='1.0' encoding='UTF-8' ?>");
				 temp_xml.append("<root>");
				 temp_xml.append("</root>");
				 content=temp_xml.toString();
			 }
	    	 doc = PubFunc.generateDom(content);
	    	 Element root = this.doc.getRootElement();
	    	 List list=root.getChildren();
			 LazyDynaBean abean=null;
			 for(Iterator t=list.iterator();t.hasNext();)
			 {
					Element element=(Element)t.next();
					if(element.getAttributeValue("target")!=null||"03".equals(element.getAttributeValue("sp_flag"))){
						if("02".equals(element.getAttributeValue("sp_flag")))
						{
							
							return_buf.append("\r\n"+element.getAttributeValue("name")+"  报批给 "+element.getAttributeValue("target"));
							return_buf.append("\r\n   时间："+element.getAttributeValue("date"));
							if(!"".equals(element.getText())) {
                                return_buf.append("\r\n   审批意见：  "+element.getText()+"");
                            }
							return_buf.append("\r\n-----------------------------------------------------------------------");
						}
						else if("07".equals(element.getAttributeValue("sp_flag")))
						{
							
							return_buf.append("\r\n"+element.getAttributeValue("name")+"  驳回给 "+element.getAttributeValue("target"));
							return_buf.append("\r\n   时间："+element.getAttributeValue("date"));
							return_buf.append("\r\n   驳回原因：  "+element.getText()+"");
							return_buf.append("\r\n-----------------------------------------------------------------------");
							
						}
						else if("03".equals(element.getAttributeValue("sp_flag")))
						{
							return_buf.append("\r\n"+element.getAttributeValue("name")+"   批准 ");
							return_buf.append("\r\n   时间："+element.getAttributeValue("date"));  
						    if(!"".equals(element.getText())) {
                                return_buf.append("\r\n   审批意见：  "+element.getText()+"");
                            }
						    return_buf.append("\r\n-----------------------------------------------------------------------");
						} 
						
					}else{
						return_buf.append(element.getAttributeValue("name")+"   ");
						return_buf.append(element.getAttributeValue("date")+"   ");
						return_buf.append(AdminCode.getCodeName("23",element.getAttributeValue("sp_flag"))+"\r\n");
						return_buf.append("       "+element.getText()+"\r\n");
						return_buf.append("-----------------------------------------------------------------------\r\n");
					}

			 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return return_buf.toString();
	}
	/**
	 * 取得发送邮件的内容
	 * @param formName
	 * @param toName
	 * @param z0301
	 * @return
	 */
	public String getEmailContent(String formName,String z0301)
	{
		StringBuffer buf = new StringBuffer();
		FieldItem item=new FieldItem();;
		try
		{
			
			
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301",z0301);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			StringBuffer content=new StringBuffer("");
			//content.append(toName+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>");
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			String plan=vo.getString("z0336");
			String hiremajor="";
			if("01".equals(plan)){
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
				HashMap mp=parameterXMLBo.getAttributeValues();
				if(mp!=null&&mp.get("hireMajor")!=null)
				 {
					hiremajor=(String)mp.get("hireMajor");
				 }
				if(hiremajor==null||hiremajor.length()==0){
					content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 招聘需求，请您批阅！");
				}else{
					item=DataDictionary.getFieldItem(hiremajor);
					if(item.isCode()){
						content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName(item.getCodesetid(), vo.getString(hiremajor))+"\" 招聘需求，请您批阅！");
						
					}else{
						content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+vo.getString(hiremajor)+"\" 招聘需求，请您批阅！");
					}
				}
				
			}else{
				content.append("&nbsp;&nbsp;&nbsp;&nbsp;"+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 招聘需求，请您批阅！");
			}
			buf.append(content);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 招聘报批发送短信
	 * formName 报批人
	 * z0301 招聘需求
	 * @author dml 2011-05-13
	 * 
	 * */
	public String sendMessageContent(String formName,String z0301){
		StringBuffer buf = new StringBuffer();
		FieldItem item=new FieldItem();
		try
		{
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301",z0301);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
			String plan=vo.getString("z0336");
			StringBuffer content=new StringBuffer("");
			if("01".equals(plan)){
				ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.conn);
				HashMap mp=parameterXMLBo.getAttributeValues();
				String hiremajor="";
				if(mp!=null&&mp.get("hireMajor")!=null)
				 {
					hiremajor=(String)mp.get("hireMajor");
				 }
				if(hiremajor==null||hiremajor.length()==0){
					content.append("      "+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 招聘需求，请您批阅！");
				}else{
					item=DataDictionary.getFieldItem(hiremajor);
					if(item.isCode()){
						content.append("      "+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName(item.getCodesetid(), vo.getString(hiremajor))+"\" 招聘需求，请您批阅！");
						
					}else{
						content.append("      "+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+vo.getString(hiremajor)+"\" 招聘需求，请您批阅！");
					}
				}
				
			}else{
				content.append("      "+formName+"已向您报批\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 招聘需求，请您批阅！");
			}
			buf.append(content);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	/**
	 * 驳回的邮件内容
	 * @param formName
	 * @param z0301
	 * @return
	 */
	public String getRejectEmailContent(String formName,String z0301)
	{
		StringBuffer buf = new StringBuffer();
		try
		{
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301",z0301);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			StringBuffer content=new StringBuffer("");
			content.append("&nbsp;&nbsp;&nbsp;&nbsp;您报批给&nbsp;&nbsp;"+formName+"&nbsp;&nbsp;的招聘需求\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 已被驳回，请您查阅！");
			buf.append(content);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 发送短信内容
	 * @param content
	 * @param a0100
	 * @param nbase
	 * @author dml 2011-05-13
	 */
	public String getRejectMessageContent(String formName,String z0301){
		StringBuffer buf = new StringBuffer();
		try
		{
		
			RecordVo vo = new RecordVo("z03");
			vo.setString("z0301",z0301);
			ContentDAO dao = new ContentDAO(this.conn);
			vo=dao.findByPrimaryKey(vo);
			StringBuffer content=new StringBuffer("");
			content.append("您报批给"+formName+"的招聘需求\""+AdminCode.getCodeName("UN", vo.getString("z0321"))+"/"+AdminCode.getCodeName("UM", vo.getString("z0325"))+"/"+AdminCode.getCodeName("@K", vo.getString("z0311"))+"\" 已被驳回，请您查阅！");
			buf.append(content);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return buf.toString();
	}
	/**
	 * 发送邮件内容
	 * @param content
	 * @param a0100
	 * @param nbase
	 */
	public void sendMessage(String content,String a0100,String nbase,int sptype)
	{
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			EMailBo bo = new EMailBo(this.conn,true,"");
			String fromaddr=this.getFromAddr();
			RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			String email_field=avo.getString("str_value");
			String sql= "select a0101,"+email_field;			
			sql+=" from "+nbase+"A01 where a0100='"+a0100+"'";
			String email="";
			String a0101="";
			if(email_field!=null&&email_field.length()>0)
			{
				RowSet rset=dao.search(sql);
				if(rset.next())
				{
					a0101=rset.getString("a0101")+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>";
					if(email_field!=null&&email_field.length()>0) {
                        email=rset.getString(email_field);
                    }
				}
			}
			if(email!=null&&email.length()>0&&fromaddr!=null&&fromaddr.length()>0)
			{
				String emailTitle="招聘需求报批";
				if(sptype==2) {
                    emailTitle="招聘需求驳回";
                }
				bo.sendEmail(emailTitle,a0101+content,"",fromaddr,email);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 短信通知
	 * @param message 短信内容
	 * @param a0100   接收短信对象
	 * @param nbase   接收对象人员库
	 * @param sender   发送人员
	 * @param sptype   区分业务用户还是自助用户
	 * @author dml 2011-05-13
	 * */
	public void sendShortMessage(String menssage ,String a0100,String nbase,int sptype ,String sender){
		try
		{
			ContentDAO dao = new ContentDAO(this.conn);
			EMailBo bo = new EMailBo(this.conn,true,"");
			SmsBo smsbo=new SmsBo(this.conn);
			LazyDynaBean dyvo=new LazyDynaBean();
			RecordVo avo=ConstantParamter.getRealConstantVo("SS_MOBILE_PHONE");
			String phonenum=avo.getString("str_value");
			if(sptype==1){//自主用户
				
				String sql= "select a0101,"+phonenum;			
				sql+=" from "+nbase+"A01 where a0100='"+a0100+"'";
				String num="";
				String a0101="";
				dyvo.set("sender",sender);
				dyvo.set("msg",menssage);
				
				if(phonenum!=null&&phonenum.length()!=0){
					RowSet rset=dao.search(sql);
					if(rset.next())
					{
						dyvo.set("receiver",rset.getString("a0101"));
						if(phonenum!=null&&phonenum.length()>0)	{	
							num=rset.getString(phonenum);
							if(num!=null&&num.length()!=0) {
                                dyvo.set("phone_num",rset.getString(phonenum));
                            } else{
								throw GeneralExceptionHandler.Handle(new Exception("报批人员没有填写手机号码！发送不成功！"));
							}
						}
					}
				}
			}else{//业务用户
				RecordVo userVo = new RecordVo("operuser");
				userVo.setString("username",a0100);
				userVo=dao.findByPrimaryKey(userVo);
				String ra0100=userVo.getString("a0100");
				String rnbase=userVo.getString("nbase");
				String userphone=userVo.getString("phone");
				String phone="";
				if(ra0100==null|| "".equals(ra0100))//业务用户没关联自主用户
				{
					phone=userVo.getString("phone");
					if(userVo.getString("fullname")!=null&&!"".equals(userVo.getString("fullname"))) {
                        dyvo.set("receiver", userVo.getString("fullname"));
                    } else {
                        dyvo.set("receiver", userVo.getString("username"));
                    }
					if(phone!=null&&phone.length()!=0) {
                        dyvo.set("phone_num",phone);
                    } else{
						 throw GeneralExceptionHandler.Handle(new Exception("报批用户没有填写手机号码！发送不成功！"));
					}
				}else{
					String sql= "select a0101,"+phonenum;			
					sql+=" from "+rnbase+"A01 where a0100='"+ra0100+"'";
					String a0101="";
					if(phonenum!=null&&phonenum.length()!=0){
						RowSet rset=dao.search(sql);
						if(rset.next())
						{
							dyvo.set("receiver",rset.getString("a0101"));
							if(phonenum!=null&&phonenum.length()>0)	{	
								phone=rset.getString(phonenum);
							}
						}
					}
					if(phone!=null&&phone.length()!=0){
						dyvo.set("phone_num",phone);
					}
					else{
						if(userVo.getString("phone")!=null&&userVo.getString("phone").length()!=0){
							dyvo.set("phone_num",userVo.getString("nbase"));
						}else{
							throw GeneralExceptionHandler.Handle(new Exception("报批用户没有填写手机号码！发送不成功！"));
						}
					}
				}
				dyvo.set("sender",sender);
				dyvo.set("msg",menssage);
			}
			ArrayList list=new ArrayList();
			list.add(dyvo);
			smsbo.batchSendMessage(list);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

/**
 * 给业务用户发送邮件
 * @param content
 * @param userName
 */
	public void setOperuserMessage(String content,String userName,int sptype)
	{
		try
		{
			RecordVo userVo = new RecordVo("operuser");
			userVo.setString("username",userName);
			ContentDAO dao = new ContentDAO(this.conn);
			userVo=dao.findByPrimaryKey(userVo);
			String a0100=userVo.getString("a0100");
			String nbase=userVo.getString("nbase");
			EMailBo bo = new EMailBo(this.conn,true,"");
			String fromaddr=this.getFromAddr();
			RecordVo avo=ConstantParamter.getRealConstantVo("SS_EMAIL");
			String email_field=avo.getString("str_value");
			String email="";
			String a0101="";
			/**没关联人员*/
			if(a0100==null|| "".equals(a0100))
			{
				email=userVo.getString("email");
				if(userVo.getString("fullname")!=null&&!"".equals(userVo.getString("fullname"))) {
                    a0101=userVo.getString("fullname")+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>";
                } else {
                    a0101=userVo.getString("username")+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>";
                }
			}
			/**关联人员，按照关联的人员发送*/
			else
			{
				String sql= "select a0101,"+email_field;			
				sql+=" from "+nbase+"A01 where a0100='"+a0100+"'";
				if(email_field!=null&&email_field.length()>0)
				{
					RowSet rset=dao.search(sql);
					if(rset.next())
					{
						a0101=rset.getString("a0101")+":<br>&nbsp;&nbsp;&nbsp;&nbsp;您好!<br>";
						if(email_field!=null&&email_field.length()>0) {
                            email=rset.getString(email_field);
                        }
					}
				}
			}
			if(email==null|| "".equals(email)) {
                return;
            }
			String emailTitle="招聘需求报批";
			if(sptype==2) {
                emailTitle="招聘需求驳回";
            }
			bo.sendEmail(emailTitle,a0101+content,"",fromaddr,email);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 从系统邮件服务器设置中得到发送邮件的地址
	 * @return
	 */
	public String getFromAddr() throws GeneralException 
	{
		String str = "";
        RecordVo stmp_vo=ConstantParamter.getConstantVo("SS_STMP_SERVER");
        if(stmp_vo==null) {
            return "";
        }
        String param=stmp_vo.getString("str_value");
        if(param==null|| "".equals(param)) {
            return "";
        }
        try
        {
	        Document doc = PubFunc.generateDom(param);
	        Element root = doc.getRootElement();
	        Element stmp=root.getChild("stmp");	
	        str=stmp.getAttributeValue("from_addr");
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        	throw GeneralExceptionHandler.Handle(ex);
        }  
        return str;
	}
	public String getName(String userid)
	{
		String name="";
		try
		{
			String zpFld = "";
			RecordVo login_vo = ConstantParamter.getConstantVo("SS_LOGIN_USER_PWD");
			if (login_vo != null)
			{
			    String login_name = login_vo.getString("str_value");
				int idx = login_name.indexOf(",");
				if (idx != -1) {
                    zpFld = login_name.substring(0, idx);
                }
			 }
			if ("".equals(zpFld)|| "#".equals(zpFld)) {
                zpFld = "username";
            }
			ArrayList list = this.getpreList();
			ContentDAO dao = new ContentDAO(this.conn);
			for(int i=0;i<list.size();i++)
			{
				String pre= (String)list.get(i);
				String sql = "select a0101 from "+pre+"a01 where "+zpFld+"='"+userid+"'";
				RowSet rs = dao.search(sql);
				while(rs.next())
				{
					name=rs.getString("a0101");
					return name;
				}
			}
			String sql = "select username,fullname from operuser where username='"+userid+"'";
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				if(rs.getString("fullname")!=null&&!"".equals(rs.getString("fullname"))) {
                    name=rs.getString("fullname");
                } else {
                    name=rs.getString("username");
                }
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	public ArrayList getpreList()
	{
		ArrayList list = new ArrayList();
		try
		{
			String sql = "select * from dbname";
			ContentDAO dao = new ContentDAO(this.conn);
			RowSet rs = dao.search(sql);
			while(rs.next())
			{
				list.add(rs.getString("pre"));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 根据审核人数生成招聘订单
	 * @param z03list
	 * @param z04list
	 * @param shrs
	 * @param z0301
	 */
	public void addHireOrder(ArrayList z03list,ArrayList z04list,int shrs,String z0301,UserView userView)
	{
	  try
	  {
		  ArrayList voList = new ArrayList();
		  RecordVo vo = new RecordVo("z03");
		  vo.setString("z0301",z0301);
		  ContentDAO dao = new ContentDAO(this.conn);
		  vo=dao.findByPrimaryKey(vo);
		  String z0412=this.getName(vo.getString("z0309"));
		  HashMap z03Map = this.getFieldInfo(z03list);
		  for(int i=0;i<shrs;i++)
		  {
			  // 'Z0414', 10, 'A', '人力资源负责人帐号'
			  //'Z0416', 11, 'A', '人力资源负责人',,,'Z0409', 8, 'A', '招聘负责人帐号','Z0412', 9, 'A', '招聘负责人'
			  RecordVo avo = new RecordVo("z04");
			  IDGenerator idg=new IDGenerator(2,this.conn);
 			  String a_z0400=idg.getId("Z04.Z0400");
			  for(int j=0;j<z04list.size();j++)
			  {
				  FieldItem item=(FieldItem)z04list.get(j);
				  if("z0400".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0400",a_z0400);
				  } 
				  else if("z0409".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0409",vo.getString("z0309"));
				  }
				  else if("z0407".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0407",z0301);
				  }
				  else if("z0403".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0403",vo.getString("z0311"));
				  }
				  else if("z0404".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0404",vo.getString("z0321"));
				  }
				  else if("z0405".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0405",vo.getString("z0325"));
				  }
				  else if("z0402".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setDate("z0402",Calendar.getInstance().getTime());
				  }
				  else if("z0412".equalsIgnoreCase(item.getItemid()))
				  {
					  avo.setString("z0412",z0412);
				  }
				  else
				  {
					  if(z03Map.get(item.getItemdesc().toUpperCase()+item.getItemtype().toUpperCase()+item.getCodesetid().toUpperCase())!=null)
					  {
						  String itemid=(String)z03Map.get(item.getItemdesc().toUpperCase()+item.getItemtype().toUpperCase()+item.getCodesetid().toUpperCase());
			     		  if("A".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype()))
			    		  {
			    			  avo.setString(item.getItemid(), vo.getString(itemid));
			    		  }
			    		  else if("D".equalsIgnoreCase(item.getItemtype()))
				    	  {
						     if(vo.getDate(itemid)!=null) {
                                 avo.setDate(item.getItemid(), vo.getDate(itemid));
                             }
				    	  }
				    	  else if("N".equalsIgnoreCase(item.getItemtype()))
				    	  {
				    		  if(vo.getInt(itemid)!=0) {
                                  avo.setInt(item.getItemid(), vo.getInt(itemid));
                              }
				    	  }
					  }
				  }
			  }
			  /**订单是否结束标志*/
			  avo.setString("z0410", "2");
			  voList.add(avo);
		  }
		  dao.addValueObject(voList);
	  }
	  catch(Exception e)
	  {
		  e.printStackTrace();
	  }
	}
	public HashMap getFieldInfo(ArrayList z03list)
	{
		HashMap map = new HashMap();
		try
		{
			for(int i=0;i<z03list.size();i++)
			{
				FieldItem item = (FieldItem)z03list.get(i);
				map.put(item.getItemdesc().toUpperCase()+item.getItemtype().toUpperCase()+item.getCodesetid().toUpperCase(), item.getItemid());
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}
	public ArrayList getGroupFieldList()
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList alist=DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			for(int i=0;i<alist.size();i++)
			{
				FieldItem item=(FieldItem)alist.get(i);
				String itemid=item.getItemid();
				if("ctrl_param".equalsIgnoreCase(itemid)|| "z0101".equalsIgnoreCase(itemid)|| "z0301".equalsIgnoreCase(itemid)|| "D".equalsIgnoreCase(item.getItemtype())|| "M".equalsIgnoreCase(item.getItemtype())) {
                    continue;
                }
				if("0".equals(item.getState())) {
                    continue;
                }
				CommonData cd = new CommonData();
				cd.setDataName(item.getItemdesc());
				cd.setDataValue(item.getItemid()+"/"+item.getItemtype().toUpperCase());
				list.add(cd);
			}
					
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public String exportGroupCountFile(String groupField,String countField,UserView userView,String where)
	{
		String fileName=userView.getUserName()+"_"+PubFunc.getStrg()+".xls";
		HSSFWorkbook workbook = null;
		RowSet rs = null;
		FileOutputStream fileOut = null;
		try
		{
			String[] group=groupField.split("`");
			String[] count=countField.split("`");
			StringBuffer sql = new StringBuffer("");
			StringBuffer groupSQL=new StringBuffer("");
			workbook = new HSSFWorkbook();
			HSSFRow row=null;
			HSSFCell csCell=null;
			HSSFCellStyle headstyle = workbook.createCellStyle();
			HSSFSheet sheet = workbook.createSheet();
			HSSFFont font = workbook.createFont();
			font.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			headstyle.setFont(font);
			headstyle.setAlignment(HorizontalAlignment.CENTER);
			headstyle.setBorderBottom(BorderStyle.THIN);
			headstyle.setBottomBorderColor(HSSFColor.BLACK.index);
			headstyle.setBorderLeft(BorderStyle.THIN);
			headstyle.setLeftBorderColor(HSSFColor.BLACK.index);
			headstyle.setBorderRight(BorderStyle.THIN);
			headstyle.setRightBorderColor(HSSFColor.BLACK.index);
			headstyle.setBorderTop(BorderStyle.THIN);
			headstyle.setTopBorderColor(HSSFColor.BLACK.index);
			int n=0;
			row=sheet.createRow(n);
			n++;
			sql.append("select ");
			StringBuffer orderby=new StringBuffer("");
			for(int i=0;i<group.length;i++)
			{
				String ele=group[i];
				sql.append(ele.split("/")[0]+",");
				groupSQL.append(ele.split("/")[0]);
				orderby.append(ele.split("/")[0]);
				if(i!=group.length-1)
				{
					groupSQL.append(",");
					orderby.append(",");
				}
				csCell=row.createCell(i);
				csCell.setCellStyle(headstyle);
				csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
				HSSFRichTextString textstr = new HSSFRichTextString(ele.split("/")[2]);
				csCell.setCellValue(textstr);
			}
			for(int i=0;i<count.length;i++)
			{
				String ele=count[i];
				sql.append(" sum("+ele.split("/")[0]+") as "+ele.split("/")[0]+",");
				csCell=row.createCell(group.length+i);
				csCell.setCellStyle(headstyle);
				csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
				HSSFRichTextString textstr = new HSSFRichTextString(ele.split("/")[2]);
				csCell.setCellValue(textstr);
			}
			sql.setLength(sql.length()-1);
			sql.append(" from z03 left join z01 on z03.z0101=z01.z0101 ");
			if(where!=null&&!"".equals(where))
			{
				if(where.indexOf("order by")!=-1) {
                    where = where.substring(0,where.indexOf("order by"));
                }
				sql.append(" "+where);
			}
			sql.append(" group by "+groupSQL+" order by "+orderby);
			ContentDAO dao = new ContentDAO(this.conn);
			HSSFCellStyle bodystyle = workbook.createCellStyle();
			HSSFFont bodyfont = workbook.createFont();
			bodyfont.setColor(HSSFFont.COLOR_NORMAL);
			bodystyle.setFont(bodyfont);
			bodystyle.setAlignment(HorizontalAlignment.CENTER);
			bodystyle.setBorderBottom(BorderStyle.THIN);
			bodystyle.setBottomBorderColor(HSSFColor.BLACK.index);
			bodystyle.setBorderLeft(BorderStyle.THIN);
			bodystyle.setLeftBorderColor(HSSFColor.BLACK.index);
			bodystyle.setBorderRight(BorderStyle.THIN);
			bodystyle.setRightBorderColor(HSSFColor.BLACK.index);
			bodystyle.setBorderTop(BorderStyle.THIN);
			bodystyle.setTopBorderColor(HSSFColor.BLACK.index);
			rs=dao.search(sql.toString());
			double[] countArr=new double[count.length];
			while(rs.next())
			{
				row=sheet.createRow(n);
				for(int i=0;i<group.length;i++)
				{
					String ele=group[i];
					csCell=row.createCell(i);
					csCell.setCellStyle(bodystyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
					FieldItem item = DataDictionary.getFieldItem(ele.split("/")[0]);
					if(item==null)
					{
				    	HSSFRichTextString textstr = new HSSFRichTextString(rs.getString(ele.split("/")[0])==null?"":rs.getString(ele.split("/")[0]));
				    	csCell.setCellValue(textstr);
					}
					else
					{
						String codesetid=item.getCodesetid();
						if("0".equals(codesetid))
						{
							HSSFRichTextString textstr = new HSSFRichTextString(rs.getString(ele.split("/")[0])==null?"":rs.getString(ele.split("/")[0]));
					    	csCell.setCellValue(textstr);
						}
						else
						{
							HSSFRichTextString textstr = new HSSFRichTextString(rs.getString(ele.split("/")[0])==null?"":AdminCode.getCodeName(item.getCodesetid(), rs.getString(ele.split("/")[0])));
					    	csCell.setCellValue(textstr);
						}
					}
				}
				for(int i=0;i<count.length;i++)
				{
					String ele=count[i];
					csCell=row.createCell(group.length+i);
					csCell.setCellStyle(bodystyle);
					csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					FieldItem item = DataDictionary.getFieldItem(ele.split("/")[0]);
					if(item==null)
					{
						countArr[i]+=rs.getDouble(ele.split("/")[0]);
				    	csCell.setCellValue(rs.getDouble(ele.split("/")[0]));
					}
					else
					{
						int scale=item.getDecimalwidth();
						csCell.setCellValue(Double.parseDouble(PubFunc.round(rs.getDouble(ele.split("/")[0])+"", scale)));
						countArr[i]+=Double.parseDouble(PubFunc.round(rs.getDouble(ele.split("/")[0])+"", scale));
					}
				}
				n++;
			}
			row=sheet.createRow(n);
			HSSFCellStyle totalstyle = workbook.createCellStyle();
			HSSFFont totalfont = workbook.createFont();
			totalfont.setColor(HSSFFont.COLOR_NORMAL);
			font.setBold(true);
			totalstyle.setFont(totalfont);
			totalstyle.setAlignment(HorizontalAlignment.CENTER);
			totalstyle.setBorderBottom(BorderStyle.THIN);
			totalstyle.setBottomBorderColor(HSSFColor.BLACK.index);
			totalstyle.setBorderLeft(BorderStyle.THIN);
			totalstyle.setLeftBorderColor(HSSFColor.BLACK.index);
			totalstyle.setBorderRight(BorderStyle.THIN);
			totalstyle.setRightBorderColor(HSSFColor.BLACK.index);
			totalstyle.setBorderTop(BorderStyle.THIN);
			totalstyle.setTopBorderColor(HSSFColor.BLACK.index);
			totalstyle.setAlignment(HorizontalAlignment.RIGHT);
	    	totalstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);   
	    	totalstyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index);  
			for(int i=0;i<group.length;i++)
			{
				if(i==0)
				{
		    	    csCell=row.createCell(i);
		    	   
		        	csCell.setCellStyle(totalstyle);
		        	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		        	HSSFRichTextString textstr = new HSSFRichTextString(ResourceFactory.getProperty("planar.stat.total"));
	        	    csCell.setCellValue(textstr);
	        	    ExportExcelUtil.mergeCell(sheet, n,(short)0, n, (short)(group.length-1));
				}
				else
				{
					csCell=row.createCell(i);
		        	csCell.setCellStyle(totalstyle);
		        	csCell.setCellType(HSSFCell.CELL_TYPE_STRING);
		        	HSSFRichTextString textstr = new HSSFRichTextString("");
	        	    csCell.setCellValue(textstr);
	        	    
				}
	            	
			}
	    	for(int i=0;i<count.length;i++)
			{
				csCell=row.createCell(group.length+i);
				csCell.setCellStyle(totalstyle);
				csCell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				csCell.setCellValue(countArr[i]);
			}
			for(int i = 0; i <=(group.length+count.length); i++)
			{
				sheet.setColumnWidth(Short.parseShort(String.valueOf(i)),(short)6000);
			}
			for(int i = 0; i <=n; i++)
			{
			    row = sheet.getRow(i);
				if(row==null) {
                    row = sheet.createRow(i);
                }
			    row.setHeight((short) 400);
			}
			fileOut = new FileOutputStream(System.getProperty("java.io.tmpdir")+ System.getProperty("file.separator") + fileName);
			workbook.write(fileOut);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			PubFunc.closeResource(rs);
			PubFunc.closeResource(fileOut);
			PubFunc.closeResource(workbook);
		}
		return fileName;
	}
	//--------------------------------------------------------------------------------------------------------------
	public String getDemand_post(String param)
	{
		StringBuffer buf = new StringBuffer("<table width='100%' align='center' border='0' cellspacing='0'  cellpadding='0'  class='ListTable common_border_color'>");
		buf.append("<tr><td align='center' width='40%' class='t_cell_locked2 common_background_color common_border_color'>用工需求表</td><td align='center' width='0%' class='t_cell_locked3 common_background_color common_border_color'>岗位指标</td>");
		buf.append("<td align='center' width='20%' class='t_cell_locked1 common_background_color common_border_color' >操作</td></tr>");
		try{
			
			if(param!=null&&!"".equals(param.trim())){
				String[] arr=param.split(",");
				for(int i=0;i<arr.length;i++)
				{
					if(arr[i]==null|| "".equals(arr[i].trim())|| "`".equals(arr[i])) {
                        continue;
                    }
					String[] tmp=arr[i].split("`");
					FieldItem z03=DataDictionary.getFieldItem(tmp[0].toLowerCase());
					FieldItem k01=DataDictionary.getFieldItem(tmp[1].toLowerCase());
					buf.append("<tr><td align='center' width='40%' class='t_cell_lockedg_l common_border_color'>"+z03.getItemdesc()+("("+z03.getItemid().toUpperCase()+")")+"</td>");
					buf.append("<td width='0%' class='t_cell_lockedg common_border_color' align='center'>"+k01.getItemdesc()+"("+k01.getItemid().toUpperCase()+")"+"</td>");
					buf.append("<td width='20%' align='center' class='t_cell_locked_r common_border_color'><a href=\"javascript:save('3','"+(z03.getItemid()+"`"+k01.getItemid())+"');\"><img src='/images/del.gif' border='0'/></a></td>");
					buf.append("</tr>");
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		buf.append("</table>");
		return buf.toString();
	}
	public ArrayList getZ03Field(String param)
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList fieldList = DataDictionary.getFieldList("Z03",Constant.USED_FIELD_SET);
			ParameterXMLBo bo = new ParameterXMLBo(this.conn,"1");
			HashMap map = bo.getAttributeValues();
          /*  String param=bo.getParam();*/
            param=param==null?"":param;
			String hire_object="";
			String hireMajor="";
			if(map.get("hire_object")!=null)
			{
				hire_object=(String)map.get("hire_object");
			}
			if(map.get("hireMajor")!=null)
			{
				hireMajor=(String)map.get("hireMajor");
			}
			for(int i=0;i<fieldList.size();i++)
			{
				FieldItem item=(FieldItem)fieldList.get(i);
				if("0".equals(item.getState())) {
                    continue;
                }
				if(item.getItemid().equalsIgnoreCase(hire_object)||item.getItemid().equalsIgnoreCase(hireMajor)) {
                    continue;
                }
				if("z0301".equalsIgnoreCase(item.getItemid())|| "z0319".equalsIgnoreCase(item.getItemid())|| "z0303".equalsIgnoreCase(item.getItemid())
						|| "z0305".equalsIgnoreCase(item.getItemid())|| "z0309".equalsIgnoreCase(item.getItemid())|| "z0311".equalsIgnoreCase(item.getItemid())
					|| "z0321".equalsIgnoreCase(item.getItemid())|| "z0325".equalsIgnoreCase(item.getItemid())|| "z0329".equalsIgnoreCase(item.getItemid())
					|| "z0331".equalsIgnoreCase(item.getItemid())|| "z0316".equalsIgnoreCase(item.getItemid())|| "z0336".equalsIgnoreCase(item.getItemid())
					|| "z0101".equalsIgnoreCase(item.getItemid())|| "z0307".equalsIgnoreCase(item.getItemid())|| "z0327".equalsIgnoreCase(item.getItemid())) {
                    continue;
                }
				if(param.toUpperCase().indexOf(item.getItemid().toUpperCase())!=-1) {
                    continue;
                }
				CommonData cd = new CommonData(item.getItemid(),item.getItemdesc());
				list.add(cd);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getKSetList()
	{
		ArrayList list = new ArrayList();
		try
		{
			ArrayList fieldsetlist = DataDictionary.getFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);//userView.getPrivFieldSetList(Constant.USED_FIELD_SET, Constant.POS_FIELD_SET);//
			CommonData acd = new CommonData("","请选择...");
			list.add(acd);
			for(int i=0;i<fieldsetlist.size();i++)
			{
				FieldSet set=(FieldSet)fieldsetlist.get(i);
				if("k00".equalsIgnoreCase(set.getFieldsetid())) {
                    continue;
                }
				CommonData cd = new CommonData();
				cd.setDataName(set.getCustomdesc());
				cd.setDataValue(set.getFieldsetid());
				list.add(cd);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public ArrayList getKItemList(String fieldsetid,String zItemId,String param)
	{
		ArrayList list = new ArrayList();
		try
		{
			if(fieldsetid==null|| "".equals(fieldsetid)) {
                return list;
            }
			FieldItem zFieldItem = null;
			if(zItemId!=null&&zItemId.length()>0) {
                zFieldItem = DataDictionary.getFieldItem(zItemId.toLowerCase());
            }
			/*ParameterXMLBo bo = new ParameterXMLBo(this.conn,"1");
            String param=bo.getParam();*/
            param=param==null?"":param;
            ArrayList fieldItemList = DataDictionary.getFieldList(fieldsetid,Constant.USED_FIELD_SET);
            for(int i=0;i<fieldItemList.size();i++)
            {
            	FieldItem item = (FieldItem)fieldItemList.get(i);
            	if(item.getState()!=null&& "0".equals(item.getState())) {
                    continue;
                }
            	if(param.toUpperCase().indexOf(item.getItemid().toUpperCase())!=-1) {
                    continue;
                }
            	if(zFieldItem!=null)
            	{
            		if(!item.getItemtype().equalsIgnoreCase(zFieldItem.getItemtype())) {
                        continue;
                    }
            	}
            	CommonData cd = new CommonData(item.getItemid(),item.getItemdesc());
            	list.add(cd);
            }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return list;
	}
	public void saveOrDelParam(String param)
	{
		try
		{
			ParameterXMLBo bo = new ParameterXMLBo(this.conn,"1");
			bo.saveOrDelParam(param);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public HashMap getKInfoMap(String z0311)
	{
		HashMap map =null;
		RowSet rs = null;
	  try
	  {
		  ParameterXMLBo bo = new ParameterXMLBo(this.conn,"1");
		  String param=bo.getParam();
		  if(param!=null&&param.trim().length()>0)
		  {
			  HashMap setMap = new HashMap();
			  StringBuffer from = new StringBuffer(" from K01 ");
			  StringBuffer select = new StringBuffer(" select 1 ");
			  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			  String[] arr=param.split(",");
			  for(int i=0;i<arr.length;i++)
			  {
				  if(arr[i]==null|| "".equals(arr[i])|| "`".equals(arr[i])) {
                      continue;
                  }
				  String[] tmp=arr[i].split("`");
				  FieldItem kItem=DataDictionary.getFieldItem(tmp[1].toLowerCase());
				  if(kItem!=null)
				  {
					  if(setMap.get(kItem.getFieldsetid().toLowerCase())==null&&!"K01".equalsIgnoreCase(kItem.getFieldsetid()))
					  {
						  from.append(" left join (select * from "+kItem.getFieldsetid());
						  from.append(" a where i9999=(select max(i9999) from "+kItem.getFieldsetid()+" b where a.e01a1=b.e01a1) ");
						  from.append(" and a.e01a1='"+z0311+"') "+kItem.getFieldsetid()+" on K01.e01a1="+kItem.getFieldsetid()+".e01a1");
						  setMap.put(kItem.getFieldsetid().toLowerCase(), "1");
					  }
					  select.append(","+kItem.getFieldsetid()+"."+kItem.getItemid());
				  } 
			  }
			  from.append(" where K01.e01a1='"+z0311+"'");
			  ContentDAO dao = new ContentDAO(this.conn);
			  rs = dao.search(select.toString()+" "+from.toString());
			  while(rs.next())
			  {
				  map = new HashMap();
				  for(int i=0;i<arr.length;i++)
				  {
					  if(arr[i]==null|| "".equals(arr[i])|| "`".equals(arr[i])) {
                          continue;
                      }
					  String[] tmp=arr[i].split("`");
					  FieldItem zItem=DataDictionary.getFieldItem(tmp[0].toLowerCase());
					  FieldItem kItem=DataDictionary.getFieldItem(tmp[1].toLowerCase());
					  if("N".equalsIgnoreCase(kItem.getItemtype()))
					  {
						  String value="";
						  if(rs.getString(kItem.getItemid())!=null)
						  {
							  int deci=kItem.getDecimalwidth();
							  if(deci==0) {
                                  value=rs.getInt(kItem.getItemid())+"";
                              } else {
                                  value=PubFunc.round(rs.getString(kItem.getItemid()), deci);
                              }
						  }
						  map.put(zItem.getItemid().toLowerCase(), value);	  
					  }
					  else if("D".equalsIgnoreCase(kItem.getItemtype()))
					  {
						  String value="";
						  if(rs.getDate(kItem.getItemid())!=null) {
                              ;
                          }
						     value=format.format(rs.getDate(kItem.getItemid()));
						  map.put(zItem.getItemid().toLowerCase(), value);
					  }else
					  {
						  String value="";
						  if(rs.getString(kItem.getItemid())!=null) {
                              value=rs.getString(kItem.getItemid());
                          }
						  map.put(zItem.getItemid().toLowerCase(), value);
					  }
				  }
			  }
		  }
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }finally
	  {
		  try
		  {
			  if(rs!=null) {
                  rs.close();
              }
		  }catch(Exception e)
		  {
			  e.printStackTrace();
		  }
	  }
	  return map;
	}
	public String getViewValue(String hireMajorCode,String itemids){
		String str = "";
		try{
			if("".equals(itemids)){
				str = itemids;
				return str;
			}
			itemids = itemids.replaceAll("，", ",");
			String[] temp = itemids.split(",");
			for(int i=0;i<temp.length;i++){
				if("".equals(AdminCode.getCodeName(hireMajorCode, temp[i])) || AdminCode.getCodeName(hireMajorCode, temp[i])==null){
					//如果已经是汉字了
					str = itemids;
					return str;
				}else {
                    str += AdminCode.getCodeName(hireMajorCode, temp[i])+",";
                }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * z03增加列currappusername  记录当前操作人员zzk
	 * @param z0301
	 * @throws GeneralException
	 */
	public void addCurrappusername(String z0301) throws GeneralException{
        String sql="select * from z03 where currappusername is null ";
        if(z0301!=null&&z0301.trim().length()>0){
            sql+=" and z0301='"+z0301+"' ";
        }
		String batchUpdateSql="update z03 set currappusername=? where z0301=?";
		ArrayList batchUpdatelist=new ArrayList();
		ArrayList innerlist=new ArrayList();
		ResultSet res=null;
		ContentDAO dao=new ContentDAO(this.conn);
		ResultSet resultSet=null;
		try {
			DbWizard dbWizard = new DbWizard(this.conn);
			DBMetaModel dbmodel = new DBMetaModel(this.conn);
			Table table = new Table("z03");
			if (!dbWizard.isExistField("z03", "currappusername", false))
			{
				Field obj = new Field("currappusername");
				obj.setDatatype(DataType.STRING);
				obj.setLength(20);
				//obj.setKeyable(true);
				table = new Table("z03");
				table.addField(obj);
				dbWizard.addColumns(table);// 更新列
				dbmodel.reloadTableModel("z03");
			}
			
			resultSet=dao.search(sql);
			HashMap nbaseMap = this.getNbaseMap();
			while(resultSet.next()){
				String currappuser=resultSet.getString("currappuser");
				String Z0301=resultSet.getString("Z0301");//用工申请序号
				String Z0309=resultSet.getString("Z0309");//创建人
				innerlist=new ArrayList();
				String name="";
				if(currappuser==null|| "".equals(currappuser)){
					name=Z0309;
				}else{
					String info =currappuser;
					String fullName="";//全称
					String loginName="";//业务用户登录名
					String selfName="";//自助用户姓名
					String nbase="";  
					String a0100="";       ///优先级 fullName>selfName>loginName
					res=dao.search("select username,fullname,nbase,a0100 from OperUser where username='"+currappuser+"'");
					if(res.next()){
						fullName=res.getString("fullname");
						loginName=currappuser;
						nbase=res.getString("nbase");
						a0100=res.getString("a0100");
					}
					if(nbase!=null&&a0100!=null&&a0100.length()>0){
						res=dao.search("select a0101 from "+nbase+"A01 where a0100='"+a0100+"'");
						if(res.next()){
							selfName=res.getString("a0101");
						}
					}
					if(fullName!=null&&fullName.trim().length()>0){
						name=fullName;
					}else{
						if(selfName!=null&&selfName.trim().length()>0){
							name=selfName;
						}else{
							name=loginName;
						}
					}
					if(info.length()>3)
					{
						String pre=info.substring(0,3);
						/**是自助用户*/
						if(nbaseMap.get(pre.toUpperCase())!=null)
						{
							res=dao.search("select a0101 from "+pre+"A01 where a0100='"+info.substring(3)+"'");
							if(res.next()){
								name=res.getString("a0101");
							}
						}
						else
						{
							//name=info;
						}
					}else{
						//name=info;
					}
				}
				innerlist.add(name);
				innerlist.add(Z0301);
				batchUpdatelist.add(innerlist);
				
			}
			dao.batchUpdate(batchUpdateSql, batchUpdatelist);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	} 
	/**
	 * 获取驳回对象的类型
	 * @param username
	 * @return type 1：自助用户|4：业务用户
	 */
	public String getUserType(String username) {
		String type = "";
		RowSet rs = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			rs = dao.search("select username from operuser where username='" + username + "'");
			if (rs.next()) {
				type = "4";
			} else {
                type = "1";
            }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
                    rs.close();
                }
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return type;

	}
}
