package com.hjsj.hrms.module.system.personalsoduku.boxreport.transaction;

import com.hjsj.hrms.interfaces.hire.OrganizationByXml;
import com.hjsj.hrms.valueobject.database.ExecuteSQL;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 
* <p>Title:GetOrgTreeTrans </p>
* <p>Description: 获取机构树</p>
* <p>Company: hjsj</p> 
* @author hej
* @date Dec 24, 2015 5:29:52 PM
 */
public class GetOrgTreeTrans extends IBusiness{
	@Override
    public void execute() throws GeneralException {
		
		ArrayList list = new ArrayList();		
		try{		
			ContentDAO dao = new ContentDAO(this.frameconn);
			String codeid = this.getFormHM().get("node").toString();
			String codesetidselect="";
			String codevalue = "";
			String _code = "";
			String codeflag = "";
			String limitcode = "";
			String limitunit = "";
			codevalue = this.userView.getManagePrivCodeValue();//管理范围(能查看的人员)
			_code = this.userView.getManagePrivCode();
			limitunit = this.userView.getUnit_id();//操作单位
			//得到登录人的组织机构范围
			String unit = this.userView.getUnitIdByBusi("4");
			if(unit!=null&&!"".equals(unit)){
				if("UN`".equals(unit)||this.userView.isSuper_admin()){//全部范围
					codeflag = "ALL";
				}else{
					codeflag ="BUFENZ";
		         	limitcode = unit;
				}
			}else{
				if(limitunit!=null&&!"".equals(limitunit)){//操作单位
					codeflag ="BUFENC";
					limitcode = limitunit;
				}else{
					if(_code!=null&&!"".equals(_code)){//人员管理范围
						codeflag ="BUFENR";
						limitcode = codevalue;
					}else{
						codeflag = "NULL";
					}
				}
			}
			
			if(!"NULL".equals(codeflag)){
				if(!"root".equals(codeid)){
					String sql = "select codesetid from organization where codeitemid='"+codeid+"'";
					this.frowset = dao.search(sql);

					if(this.frowset.next()){
						codesetidselect = this.frowset.getString("codesetid");
					}
					else if("UM".equals(codesetidselect)){
						codesetidselect = "UM";
					}
				}else{
					codesetidselect = "UN";
				}	
				if(codesetidselect.indexOf("UN")!=-1||codesetidselect.indexOf("UM")!=-1){
					list = searchOrgCodeData(codesetidselect,codeid,dao,codeflag,limitcode);
				}
//				String str = JSON.toString(list);
			}
			this.getFormHM().clear();
			this.getFormHM().put("children",list);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	private ArrayList searchOrgCodeData(String codesetid,String code,ContentDAO dao,String codeflag,String limitcode){
		 ArrayList value = new ArrayList();
		 try {
			 if("ALL".equals(codeflag)){
				value = getchildCodeData(codesetid,code,dao);
			 }
			 if("BUFENZ".equals(codeflag)||"BUFENR".equals(codeflag)||"BUFENC".equals(codeflag)){
				 boolean multiple = true;
				 boolean doChecked = false;
				 if(!"root".equals(code)){
						String[] temp=limitcode.replace("UN","").replace("UM", "").replace("@K", "").split("`");
						boolean hasPriv = false;
						for(int i=0;i<temp.length;i++)
						{
							if(code.startsWith(temp[i]))
								hasPriv = true;
						}
						
						if(!hasPriv){
							return value;
						}
						
						value= getchildCodeData(codesetid,code,dao);
					}else{
					    String searchCodes = "";
						String[] temp=limitcode.split("`");
						HashMap map = new OrganizationByXml().getPrivMange(temp);
						for(int i=0;i<temp.length;i++)
						{
							if(map.get(temp[i].substring(2))==null)
								searchCodes+="'"+temp[i].substring(2)+"',";
						}
						searchCodes+="'code'";
						
						String codefilter =" and codesetid<>'@K' ";
						
						StringBuffer sql = new StringBuffer();
						sql.append("select codesetid,codeitemid,codeitemdesc,(select count(1) from organization where parentid=A.codeitemid ");
						sql.append(codefilter);
						sql.append(") cnum from organization A where codeitemid in (");
						sql.append(searchCodes);
						sql.append(")");
						sql.append(codefilter);
						
						List codelist = ExecuteSQL.executeMyQuery(sql.toString());
					    for(int k=0;k<codelist.size();k++){
					    	LazyDynaBean ldb = (LazyDynaBean)codelist.get(k);
					    	HashMap treeitem = new HashMap();
					    	String setid = ldb.get("codesetid").toString();
					    	treeitem.put("id",ldb.get("codeitemid"));
					    	treeitem.put("text", ldb.get("codeitemdesc"));
					    	treeitem.put("codesetid",setid);
					    	//设置图片
					    	if("UN".equals(setid))
					    		treeitem.put("icon","/images/unit.gif");
							else if("UM".equals(setid))
								treeitem.put("icon","/images/dept.gif");
							else
								treeitem.put("icon","/images/pos_l.gif");
					    	
					    	//是否叶子节点
					    	if(Integer.parseInt(ldb.get("cnum").toString())>0)
					    		treeitem.put("leaf", Boolean.FALSE);
					    	else
					    		treeitem.put("leaf", Boolean.TRUE);
					    	if(multiple)
				    			treeitem.put("checked", false);
					    	if(doChecked)
				    			treeitem.put("checked", true);
					    	value.add(treeitem);
					    }
					}
			 }
		 } catch (Exception e) {
				e.printStackTrace();
		 }
	    return value;
	    }
	
	private ArrayList getchildCodeData(String codesetid,String code,ContentDAO dao){
		 ArrayList value = new ArrayList();
	    	try{
	    		StringBuilder sql = new StringBuilder();
	    		if("UN".equals(codesetid)||"UM".equals(codesetid)){
	    			sql.append("select a0000,codeitemid,codeitemdesc,codesetid,(select count(1) from organization where parentid=Org.codeitemid and parentid<>codeitemid and codesetid<>'@K'");	
	    			sql.append(") child from organization Org where 1=1 ");
		    		if("root".equals(code)){
		    			sql.append(" and parentid=codeitemid and codesetid<>'@K'");
		    		}else{
		    			sql.append(" and parentid='"+code+"' and parentid<>codeitemid and codesetid<>'@K'");
		    		}
	    		}
	    		sql.append(" order by org.a0000");
	    		frowset = dao.search(sql.toString());
	    		while(frowset.next()){
	    			HashMap treeItem = new HashMap();
	    			if("UN".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/images/unit.gif");
	    			}else if("UM".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("icon", "/images/dept.gif");
	    			}
	    			
	    			if("UN".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("id", frowset.getString("codeitemid"));
	    				treeItem.put("codesetid", "UN");
	    			}
	    			if("UM".equalsIgnoreCase(frowset.getString("codesetid"))){
	    				treeItem.put("id", frowset.getString("codeitemid"));
	    				treeItem.put("codesetid", "UM");
	    			}
	    			
	    			treeItem.put("text", frowset.getString("codeitemdesc"));
	    			if(frowset.getInt("child")==0)
	    				treeItem.put("leaf", Boolean.TRUE);
	    			
	    			treeItem.put("checked", Boolean.FALSE);
	    			value.add(treeItem);
	    		}
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	return value;
	    }
}
