package com.hjsj.hrms.module.template.templatecard.transaction;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * 
* <p>Title:SearchParentTrans </p>
* <p>Description: 查询单位部门岗位级联</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Jun 1, 2016 10:06:32 AM
 */
public class SearchParentTrans extends IBusiness {
	
	@Override
    public void execute() throws GeneralException {
		
		String codesetid = (String) this.getFormHM().get("codesetid");
		String itemid = (String) this.getFormHM().get("itemid");
		String searchlevel = (String) this.getFormHM().get("searchlevel");//UN,UM, UN, UM, UN
		ContentDAO dao=new ContentDAO(this.frameconn);
		RowSet rst = null;
		StringBuffer sql = new StringBuffer();
		ArrayList returnlist = new ArrayList();
		try {
			if(("UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid))&&StringUtils.isNotBlank(searchlevel)){
				//xus 18/3/14 显示多层级等部门------BEGIN 
				boolean showLevelDept = false;
				if(this.formHM.containsKey("showLevelDept"))
					showLevelDept = (Boolean)this.formHM.get("showLevelDept");
				Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
				String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if(uplevelStr==null||uplevelStr.length()==0)
					uplevelStr="0";
				int upLevel = Integer.parseInt(uplevelStr);
				//xus 18/3/14 显示多层级等部门------END
				
				//获取系统设置的部门显示层级
				Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
				String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
				if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
					display_e0122="0";
				
				sql.append("select codeitemid,codeitemdesc,codesetid from organization where ");
				switch(Sql_switcher.searchDbServer()){
				case Constant.MSSQL:
					sql.append(" substring(?,0,len(codeitemid)+1)=codeitemid ");
					break;
				case Constant.ORACEL:
					sql.append(" substr(?,0,length(codeitemid))=codeitemid ");
					break;
				}
			
				if("@K".equals(codesetid)){
					if("UN,UM,".equals(searchlevel)){
						ArrayList param = new ArrayList();
						sql.append("and codesetid in ('UM','UN') order by codesetid,codeitemid desc");
						param.add(itemid);
						this.frowset = dao.search(sql.toString(),param);
						String flag = "";
						while(this.frowset.next()){
							String codesetids = this.frowset.getString("codesetid");
							String codeitemid = this.frowset.getString("codeitemid");
							String codeitemdesc = this.frowset.getString("codeitemdesc");
							if("UM".equals(codesetids)&&"".equals(flag)){
								flag = "UM";
							}
							if("UN".equals(codesetids)&&"".equals(flag)){
								flag = "UN";
							}
							if("UM".equals(codesetids)&&"UM".equals(flag)){
								HashMap map = new HashMap();
								flag="UN";
								map.put("codeitemid", codeitemid);
								map.put("codeitemdesc", codeitemdesc);
								map.put("codesetid", codesetids);
								String layerdesc = this.searchLevel(codeitemid,codesetids,Integer.parseInt(display_e0122));
								map.put("layerdesc", layerdesc);
								
								//xus 18/3/14 显示多层级等部门------BEGIN 
								CodeItem code = AdminCode.getCode("UM",codeitemid, upLevel);
								if(showLevelDept)
									map.put("levelName",code.getCodename());
								//xus 18/3/14 显示多层级等部门------END
								
								returnlist.add(map);
							}
							else if("UN".equals(codesetids)&&"UN".equals(flag)){
								HashMap map1 = new HashMap();
								flag="U";
								map1.put("codeitemid", codeitemid);
								map1.put("codeitemdesc", codeitemdesc);
								map1.put("codesetid", codesetids);
								map1.put("layerdesc", "");
								returnlist.add(map1);
							}
						}
					}
					else if("UN,".equals(searchlevel)||"UM,".equals(searchlevel)){
						ArrayList param = new ArrayList();
						if("UN,".equals(searchlevel))
							sql.append("and codesetid ='UN' order by codesetid,codeitemid desc");
						else
							sql.append("and codesetid ='UM' order by codesetid,codeitemid desc");
						param.add(itemid);
						this.frowset = dao.search(sql.toString(),param);
						while(this.frowset.next()){
							String codesetids = this.frowset.getString("codesetid");
							String codeitemid = this.frowset.getString("codeitemid");
							String codeitemdesc = this.frowset.getString("codeitemdesc");
							HashMap map = new HashMap();
							map.put("codeitemid", codeitemid);
							map.put("codeitemdesc", codeitemdesc);
							map.put("codesetid", codesetids);
							if("UM".equals(codesetids)){
								String layerdesc = this.searchLevel(codeitemid,codesetids,Integer.parseInt(display_e0122));
								map.put("layerdesc", layerdesc);
							}else
								map.put("layerdesc", "");
							returnlist.add(map);
							break;
						}
					}
				}else if("UM".equals(codesetid)){
					ArrayList param = new ArrayList();
					if("UN".equals(searchlevel)){
						sql.append("and codesetid ='UN' order by codesetid,codeitemid desc");
						param.add(itemid);
						this.frowset = dao.search(sql.toString(),param);
						while(this.frowset.next()){
							String codesetids = this.frowset.getString("codesetid");
							String codeitemid = this.frowset.getString("codeitemid");
							String codeitemdesc = this.frowset.getString("codeitemdesc");
							HashMap map = new HashMap();
							map.put("codeitemid", codeitemid);
							map.put("codeitemdesc", codeitemdesc);
							map.put("codesetid", codesetids);
							map.put("layerdesc", "");
							returnlist.add(map);
							break;
						}
					}
				}
			}else{
				ArrayList param = new ArrayList();
				if("UM".equalsIgnoreCase(codesetid)||"UN".equalsIgnoreCase(codesetid)||"@K".equalsIgnoreCase(codesetid)){
					sql.append("select codeitemid,codeitemdesc,codesetid from organization where ");
					switch(Sql_switcher.searchDbServer()){
					case Constant.MSSQL:
						sql.append(" substring(?,0,len(codeitemid)+1)=codeitemid ");
						break;
					case Constant.ORACEL:
						sql.append(" substr(?,0,length(codeitemid))=codeitemid ");
						break;
					}
					sql.append("and codesetid in ('UN','UM','@K') and codeitemid!=? order by codesetid,codeitemid desc");
					param.add(itemid);
					param.add(itemid);
					//xus 18/3/14 显示多层级等部门------BEGIN 
					boolean showLevelDept = false;
					if(this.formHM.containsKey("showLevelDept"))
						showLevelDept = (Boolean)this.formHM.get("showLevelDept");
					Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.frameconn);
					String uplevelStr = sysoth.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					if(uplevelStr==null||uplevelStr.length()==0)
						uplevelStr="0";
					int upLevel = Integer.parseInt(uplevelStr);
					//xus 18/3/14 显示多层级等部门------END
					
					//获取系统设置的部门显示层级
					Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.frameconn);
					String display_e0122 = sysbo.getValue(Sys_Oth_Parameter.DISPLAY_E0122);
					if(display_e0122==null|| "00".equals(display_e0122)|| "".equals(display_e0122))
						display_e0122="0";
					String flag="";
					this.frowset = dao.search(sql.toString(),param);
					while(this.frowset.next()){
						String codesetids = this.frowset.getString("codesetid");
						String codeitemid = this.frowset.getString("codeitemid");
						String codeitemdesc = this.frowset.getString("codeitemdesc");
						if("@K".equals(codesetids)){
							HashMap map = new HashMap();
							map.put("codeitemid", codeitemid);
							map.put("codeitemdesc", codeitemdesc);
							map.put("codesetid", codesetids);
							map.put("layerdesc", "");
							returnlist.add(map);
						}
						else if("UM".equals(codesetids)){
							HashMap map = new HashMap();
							map.put("codeitemid", codeitemid);
							map.put("codeitemdesc", codeitemdesc);
							map.put("codesetid", codesetids);
							String layerdesc = this.searchLevel(codeitemid,codesetids,Integer.parseInt(display_e0122));
							map.put("layerdesc", layerdesc);
							
							//xus 18/3/14 显示多层级等部门------BEGIN 
							CodeItem code = AdminCode.getCode("UM",codeitemid, upLevel);
							if(showLevelDept)
								map.put("levelName",code.getCodename());
							//xus 18/3/14 显示多层级等部门------END
							
							returnlist.add(map);
						}
						else if("UN".equals(codesetids)){
							HashMap map1 = new HashMap();
							map1.put("codeitemid", codeitemid);
							map1.put("codeitemdesc", codeitemdesc);
							map1.put("codesetid", codesetids);
							map1.put("layerdesc", "");
							returnlist.add(map1);
						}
					}
				}else{
					sql.append("select codeitemid,codeitemdesc,codesetid from codeitem where ");
					switch(Sql_switcher.searchDbServer()){
					case Constant.MSSQL:
						sql.append(" substring(?,0,len(codeitemid)+1)=codeitemid ");
						break;
					case Constant.ORACEL:
						sql.append(" substr(?,0,length(codeitemid))=codeitemid ");
						break;
					}
					sql.append("and codesetid='"+codesetid+"' and codeitemid!=? order by codesetid,codeitemid desc");
					param.add(itemid);
					param.add(itemid);
					this.frowset = dao.search(sql.toString(),param);
					while(this.frowset.next()){
						String codesetids = this.frowset.getString("codesetid");
						String codeitemid = this.frowset.getString("codeitemid");
						String codeitemdesc = this.frowset.getString("codeitemdesc");
						HashMap map = new HashMap();
						map.put("codeitemid", codeitemid);
						map.put("codeitemdesc", codeitemdesc);
						map.put("codesetid", codesetids);
						map.put("layerdesc", "");
						returnlist.add(map);
					}
				}
			}
			this.formHM.put("returnlist", returnlist);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			 PubFunc.closeDbObj(rst);
		}
	}

	private String searchLevel(String codeitemid, String codesetid, int layerLevel) {
		String layerdesc = "";
		if("UM".equalsIgnoreCase(codesetid)&&layerLevel>0){
			CodeItem item=AdminCode.getCode("UM",codeitemid,layerLevel);
			if(item!=null){
				layerdesc = item.getCodename();
    		}else{
	    		layerdesc = AdminCode.getCodeName(codesetid,codeitemid);
	    	}
		}
		return layerdesc;
	}
}
