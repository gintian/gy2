package com.hjsj.hrms.businessobject.general.template;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TmessageBo {
	private Connection conn=null;
	private UserView userView=null;
	private int infor_type=1;  //1:人员 2：单位 3：职位 
	private int operationtype=0; //7:撤销  8：合并  9：划转 
	private String[] msg_template=null;
	
	public TmessageBo(Connection con,UserView view,int infor_type,String[] _msg_template,int operationtype)
	{
		this.conn=con;
		this.userView=view;
		this.infor_type=infor_type;
		this.msg_template=_msg_template;
		this.operationtype=operationtype;
		if(this.msg_template==null||this.msg_template.length==0)
    	{
    		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
    		String template_id="";
			if(operationtype==7)//撤销 
            {
                template_id= sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"bolish");
            }
			if(operationtype==8)//合并 
            {
                template_id= sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"combine");
            }
			if(operationtype==9)//划转
            {
                template_id= sysbo.getValue(Sys_Oth_Parameter.ORGANIZATION,"transfer");
            }
			//系统参数定义了 机构调整业务模板，机构下的人员数据需导入相应表单中
			if(template_id!=null&&template_id.trim().length()>0) 
			{
				this.msg_template=new String[]{template_id};
			}
			
    		
    	}
	}
	
	
	/**
	 * 机构调整模板向人事异动模板下通知单
	 * @param key
	 * @param sysbo 
	 */
	public void expOrgDataIntoMessage(String strsql,String key,String msg_flag,String org_str,HashMap param,String mappingStr)
	{
		try
		{
			ContentDAO dao=new ContentDAO(this.conn);
			if(msg_flag!=null&& "1".equals(msg_flag)) //高级
			{
				
				ArrayList conefieldlist=(ArrayList)param.get("conefieldlist");
				ArrayList mag_condlist_complex=(ArrayList)param.get("mag_condlist_complex");
				ArrayList conexlist=(ArrayList)param.get("conexlist");
				/***************按规则条件定义下发通知单***************/
				String exp="";
				String msgids="";
				String where=""; 
				RowSet rsetc=null;
				String t_username="";
				String t_type = "";
				for(int i=0;i<conexlist.size();i++)
				{
					CommonData da=(CommonData)conexlist.get(i);
					LazyDynaBean _bean=(LazyDynaBean)mag_condlist_complex.get(i);
					exp=da.getDataName();
					msgids=da.getDataValue();
					if(msgids==null||msgids.length()<=0) {
                        continue;
                    }
					where=expRuleTerm(exp,conefieldlist);
					if(where==null||where.length()<=0) {
                        continue;
                    }
					this.msg_template=StringUtils.split(msgids,",");					
					if(this.infor_type==2) {
                        rsetc=dao.search(strsql.toString()+" and b0110='"+key+"' and ( "+where+" )");
                    } else if(this.infor_type==3) {
                        rsetc=dao.search(strsql.toString()+" and e01a1='"+key+"' and ( "+where+" )");
                    }
					if(rsetc.next())
					{
						t_username="";
						if(_bean!=null&&((String)_bean.get("user")).length()>0) {
                            t_username=(String)_bean.get("user");
                        }
						if(_bean!=null&&((String)_bean.get("type")).length()>0) {
                            t_type=(String)_bean.get("type");
                        }
						importPeopleToMessage(org_str,dao,t_username,mappingStr,t_type);
					}
				}
		    }else 
			{ 
		    	importPeopleToMessage(org_str,dao,"",mappingStr,"");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	

	/**
	 * 解析计算公式
	 * @param exp
	 * @param fieldlist
	 * @return
	 */
	private String expRuleTerm(String exp,ArrayList fieldlist)
	{
		//sunx
		YksjParser yp=null;		
		String FSQL="";
		try
		{
			int infoGroup=YksjParser.forPerson;
			if(this.infor_type==2) {
                infoGroup=YksjParser.forUnit;
            } else if(this.infor_type==3) {
                infoGroup=YksjParser.forPosition;
            }
			yp = new YksjParser( this.userView ,fieldlist,
						YksjParser.forNormal, YksjParser.LOGIC,infoGroup, "Ht", "");
			yp.run_where(exp);
			FSQL=yp.getSQL();			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return FSQL;
	}
	
	

	/**
	 * 如果为 合并、撤销、划转、更名业务，需将相应人员写入消息通知中
	 * @param sub_sql
	 * @param template_id
	 * @param dao
	 * @param t_type 
	 */
	public void importPeopleToMessage(String sub_sql,ContentDAO dao,String username,String mappingStr, String t_type)
	{
		RowSet rowSet=null;
		try
		{
			DbWizard dbw = new DbWizard(this.conn);
			ArrayList dblist = DataDictionary.getDbpreList();
			int nyear = 0;
			int nmonth = 0;
			nyear = DateUtils.getYear(new Date());
			nmonth = DateUtils.getMonth(new Date());
			HashMap valueMapping=new HashMap();
			
			if((mappingStr!=null&&mappingStr.trim().length()>0)&&(this.operationtype==8||this.operationtype==9))
			{
				String[] mappings=mappingStr.split(",");
				for(int i=0;i<mappings.length;i++)
				{
					if(mappings[i].trim().length()>0)
					{
						String[] temp=mappings[i].split("=");
						valueMapping.put(temp[0],temp[1]);
					}
				}
			}
			
			if(this.msg_template!=null&&this.msg_template.length>0)
			{
				
				for(int i=0;i<msg_template.length;i++)
				{
					String template_id=msg_template[i];
					
					RecordVo vo = new RecordVo("tmessage");
					vo.setString("username", username); 
					vo.setInt("state", 0);
					vo.setInt("nyear", nyear);
					vo.setInt("nmonth", nmonth);
					vo.setInt("type", 0);
					vo.setInt("flag", 0);
					vo.setInt("sourcetempid", 0);
					vo.setInt("noticetempid", Integer.parseInt(template_id));
					vo.setInt("object_type",1);
					vo.setInt("bread", 0);
					vo.setString("send_user",this.userView.getUserFullName()==""?this.userView.getUserName():this.userView.getUserFullName());//xcs modify @2013-10-21
					if(Sql_switcher.searchDbServer()!=Constant.ORACEL){
						vo.setDate("receive_time", new Date());
					}else{
						vo.setDate("receive_time", new Date());
					}
					if(dbw.isExistField("tmessage", "receivetype", false)) {
                        vo.setString("receivetype",t_type);
                    }
					StringBuffer changepre = new StringBuffer();
					StringBuffer changelast = new StringBuffer();
					StringBuffer change = new StringBuffer();
					 
					StringBuffer sql=new StringBuffer("");	 
					for (int n = 0; n < dblist.size(); n++) {
							String pre = (String) dblist.get(n);
							sql.setLength(0);
							sql.append("select a0100,a0101,b0110,e0122,e01a1 from "
									+ pre + "A01 where 1=1 ");
							sql.append(sub_sql.toString());
							rowSet = dao.search(sql.toString());
							vo.setString("db_type", pre);
							while (rowSet.next()) {
								String a0100 = rowSet.getString("a0100");
								String a0101 = rowSet.getString("a0101");
								a0101 = a0101 != null ? a0101 : "";
								String b0110 = rowSet.getString("b0110");
								String e0122 = rowSet.getString("e0122");
								String e01a1 = rowSet.getString("e01a1");
								vo.setString("a0100", a0100);
								vo.setString("a0101", a0101);
								changepre.setLength(0);
								changelast.setLength(0);
								change.setLength(0);
								if (b0110 != null && !"".equals(b0110)) {
									changepre.append("B0110=" + b0110 + ",");
									change.append("B0110,");
								}
								if (e0122 != null && !"".equals(e0122)) {
									changepre.append("E0122=" + e0122 + ",");
									change.append("E0122,");
								}
								if (e01a1 != null && !"".equals(e01a1)) {
									changepre.append("E01A1=" + e01a1 + ",");
									change.append("E01A1,");
								}
								
								if(b0110!=null&&b0110.trim().length()>0&&valueMapping.get(b0110)!=null)
								{ 
										changelast.append("B0110=" + (String)valueMapping.get(b0110) + ",");
										if (e0122 != null && !"".equals(e0122)&&valueMapping.get(e0122)!=null) {
											changelast.append("E0122=" + (String)valueMapping.get(e0122) + ",");
										}
										if (e01a1 != null && !"".equals(e01a1)&&valueMapping.get(e01a1)!=null) {
											changelast.append("E01A1=" + (String)valueMapping.get(e01a1) + ",");
										} 
								}
								else if(e0122!=null&&e0122.trim().length()>0&&valueMapping.get(e0122)!=null)
								{ 
										String _b0110=getParentid((String)valueMapping.get(e0122),"UN",dao);
										changelast.append("B0110=" +_b0110 + ",");
										changelast.append("E0122=" + (String)valueMapping.get(e0122) + ",");
										if (e01a1 != null && !"".equals(e01a1)&&valueMapping.get(e01a1)!=null) {
											changelast.append("E01A1=" + (String)valueMapping.get(e01a1) + ",");
										} 
								}
								else if(e01a1!=null&&e01a1.trim().length()>0&&valueMapping.get(e01a1)!=null)
								{ 
										String _b0110=getParentid((String)valueMapping.get(e01a1),"UN",dao);
										String _e0122=getParentid((String)valueMapping.get(e01a1),"UM",dao);
										changelast.append("B0110=" +_b0110 + ",");
										if(_e0122!=null&&_e0122.trim().length()>0) {
                                            changelast.append("E0122=" +_e0122 + ",");
                                        }
										changelast.append("E01A1=" + (String)valueMapping.get(e01a1) + ","); 
								} 
								/**考虑到机构调整给人事异动下通知单,接受模板也需要受到权限范围的控制,因此要把b0110和b0110_self给设置上**/
								/**在受到接受范围的控制的情况下,就要考虑到但单位部门岗位被合并、划转、撤销、更名的情况下
								 * 在消息表里面写数据时b0110是写源值还是目标值？如果写目标值的话,这些人还没正式调岗还没在目标的部门和岗位下,不一定是属于该部门的人员
								 * 所以不可取,才去源值，当这个部门的领导如果提前离职的情况下,可以有更高权限的人来处理
								 * **/
								String b0110_value="";
								if(e0122 != null && !"".equals(e0122)){//如果部门不为空,那么b0110应该设置为e0122
								    vo.setString("b0110", e0122);
								    b0110_value = e0122;
								}else {
								    if(b0110==null){
								        b0110="";
								    }
								    vo.setString("b0110",b0110);
								    b0110_value = b0110;
								}
								if(b0110==null||b0110.length()==0)
			                    {
			                        CodeItem item=AdminCode.getCode("UN", b0110_value);
			                        if(item==null)
			                        {
			                            for(int e=b0110_value.length();e>0;e--)
			                            {
			                                if(AdminCode.getCode("UN", b0110_value.substring(0,e))!=null)
			                                {
			                                    vo.setString("b0110_self", b0110_value.substring(0,e));
			                                    break;
			                                }
			                            }
			                        }
			                    }else{
			                        vo.setString("b0110_self", b0110);
			                    }
								
								if (a0101 != null && !"".equals(a0101)) {
									changepre.append("A0101=" + a0101 + ",");
									change.append("A0101,");
								}
								vo.setString("changelast",changelast.toString());
								vo.setString("changepre", changepre.toString());
								vo.setString("change", change.toString());
								/** max id access mssql此字段是自增长类型 */
								if (Sql_switcher.searchDbServer() != Constant.MSSQL) {
									int nid = DbNameBo.getPrimaryKey("tmessage", "id",
											this.conn);
									vo.setInt("id", nid);
								}
								dao.addValueObject(vo);
							}
					}
			
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(rowSet!=null) {
                    rowSet.close();
                }
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
			}
		}
	}
	
	
	//获得父节点
	private String getParentid(String codeitemid,String codesetid,ContentDAO dao)
	{
		String itemid="";
		RowSet rowSet=null;
		try
		{
			rowSet=dao.search("select * from organization where  codeitemid=(select parentid from organization where  codeitemid='"+codeitemid+"' )");
			if(rowSet.next())
			{
				String _codesetid=rowSet.getString("codesetid");
				String _codeitemid=rowSet.getString("codeitemid");
				String _parentid=rowSet.getString("parentid")!=null?rowSet.getString("parentid"):"";
				if(_codesetid.equalsIgnoreCase(codesetid)) {
                    itemid=_codeitemid;
                } else
				{
					if(_parentid.equalsIgnoreCase(_codeitemid)) {
                        return _codeitemid;
                    }
					getParentid(_codeitemid,_codesetid,dao);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return itemid;
	}
	
	
}
