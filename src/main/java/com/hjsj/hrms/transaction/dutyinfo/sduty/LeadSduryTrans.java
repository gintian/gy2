package com.hjsj.hrms.transaction.dutyinfo.sduty;


import com.hjsj.hrms.businessobject.duty.LinkSdutyBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class LeadSduryTrans extends IBusiness{

	Date backdate = new Date();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	Savepoint savepoint = null;
	
	public void execute() throws GeneralException {
		
		String orgid = this.getFormHM().get("orgid").toString().substring(2);
		   this.getFormHM().remove("orgid");
		String checkChild = this.getFormHM().get("checkChild").toString();
		if("1".equals(checkChild)){
			 String isHas = checkHasChild(orgid);
			 this.getFormHM().put("isHas", isHas);
			 return;
		}
		   
		   
		String dutyid = this.getFormHM().get("dutyid").toString();
		   this.getFormHM().remove("dutyid");
		if(!"root".equals(dutyid)){
			dutyid = dutyid.substring(2);
		}
		String dutydesc = this.getFormHM().get("dutydesc").toString();
		   this.getFormHM().remove("dutydesc");
		String check = this.getFormHM().get("check").toString();
		   this.getFormHM().remove("check");
		   String insertOrgflag = (String)this.getFormHM().get("insertOrg");  // self只在自己下面插入岗位；all自己下面所有部门都插入岗位
		   this.getFormHM().remove("insertOrg");
			
		LinkSdutyBo lsb = null; 
		
		try{	
				//获取设置的对应指标
				RecordVo vo = ConstantParamter.getConstantVo("POS_STANDARD");
				String xml = vo.getString("str_value");
				
				//获取岗位和基准岗位关联指标
				vo = ConstantParamter.getRealConstantVo("PS_C_JOB");
			    String linkdutyitemid = vo.getString("str_value");
			  //获取基准岗位codesetid
			    vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
			    String codesetid = vo.getString("str_value");
			    
			    ContentDAO dao =  new ContentDAO(this.frameconn); 
				
				String tag="";
				if("true".equals(check)){
					//检查机构下是否已经有引入的岗位
					String linktag = checkExist(orgid, linkdutyitemid, codesetid, dutyid, dao,insertOrgflag);
					this.getFormHM().put("linktag",linktag);
					
				}else{
					
					String dutytype = (String)this.getFormHM().get("dutytype");  //0为分类，1为叶子节点
					   this.getFormHM().remove("dutytype");
					if("root".equals(dutyid))
						dutytype="0";
					String sqltag = this.getFormHM().get("sqltag").toString();
					   this.getFormHM().remove("sqltag");
						
					//frameconn.setAutoCommit(false);
					//savepoint =  frameconn.setSavepoint(); 
					
				    try{
				    
						    lsb = new LinkSdutyBo(xml, linkdutyitemid, sqltag, frameconn);
						  //覆盖原有岗位 将名称也修改  wusy
						    if("over".equals(sqltag)){
						    	String codeitemidstr = orgid + lsb.getcode(dutyid, dutydesc, orgid);
						    	String sqlstr = "update organization set codeitemdesc = ? where parentid = ? and codeitemid = ?";
						    	dao.update(sqlstr, Arrays.asList(new Object[]{dutydesc, orgid, codeitemidstr}));
						    }
						    String flag = lsb.checkDOC();
						    if("0".equals(flag))
						    	this.getFormHM().put("tag", "1");
						    else{
								if("self".equals(insertOrgflag))
										   insertManyduty(dutytype,codesetid,dutyid,dutydesc,orgid,dao,lsb);
								else
								           insertManyorg(dutytype,codesetid,dutyid,dutydesc,orgid,dao,lsb);
								
							//	frameconn.commit();
							
								this.getFormHM().put("tag","ok");
						    }
				    }catch(Exception ex){
				    	ex.printStackTrace();
				    //	frameconn.rollback(savepoint);

						if(ex.toString().indexOf("不可设置为基准岗位对应指标")!=-1){
							this.getFormHM().put("tag", "4");
						}else{

							this.getFormHM().put("tag", "3");
						}
				    }finally{
				    	lsb.clearUp();
				    //	if(!frameconn.getAutoCommit())
					//		frameconn.setAutoCommit(true);
				    }
					    
						
				}
				
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void insertManyduty(String dutytype,String codesetid,String dutyid,
			                   String dutydesc,String orgid,ContentDAO dao,LinkSdutyBo lsb) throws Exception {
		lsb.getgrade(orgid);
		
		if("0".equals(dutytype)){
		      ArrayList arr = new ArrayList();
		      StringBuffer sqlstr = new StringBuffer();
		      
		      //查询出不在有效期内的岗位
		      sqlstr.append(" select codeitemid from codeitem where codesetid='"+codesetid+"' and ");
		      sqlstr.append(Sql_switcher.dateValue(sdf.format(backdate)));
              sqlstr.append(" not between start_date and end_date ");
              if(!"root".equals(dutyid))
                sqlstr.append(" and parentid like '"+dutyid+"%'");
              this.frecset = dao.search(sqlstr.toString());
              while(this.frecset.next()){
                  arr.add(this.frecset.getString("codeitemid"));
              }
              
			  sqlstr.delete(0, sqlstr.length());
			  
			  sqlstr.append("select codeitemid,codeitemdesc from codeitem where codesetid='");
			  sqlstr.append(codesetid +"' ");
			  if(!"root".equals(dutyid))
			       sqlstr.append(" and parentid like '"+dutyid+"%'");
			  sqlstr.append(" and codeitemid=childid and ");
			  sqlstr.append(Sql_switcher.dateValue(sdf.format(backdate)));
			  sqlstr.append(" between start_date and end_date ");
			//过滤掉上级节点不在有效期内的叶子节点
			  for(int i=0;i<arr.size();i++){
			      sqlstr.append(" and parentid not like '"+arr.get(i)+"%' ");
			  }
			  this.frecset = dao.search(sqlstr.toString());
			  while(this.frecset.next()){
				  lsb.linksdtuy(this.frecset.getString("codeitemid"),this.frecset.getString("codeitemdesc"), orgid);
			  }
			  
			  sqlstr.delete(0, sqlstr.length());
			  //导入所有子节点被撤销的节点（所有子节点被撤销那么本节点相当于叶子节点需要导入）
			  sqlstr.append("select codeitemid,codeitemdesc from (");
			  sqlstr.append(" select codeitemid,codeitemdesc,(select COUNT(*) from codeitem where codesetid='"+codesetid+"' and parentid=c.codeitemid and parentid<>codeitemid and "+Sql_switcher.dateValue(sdf.format(backdate))+" between start_date and end_date) num ");
			  sqlstr.append(" from codeitem c where codesetid='"+codesetid+"' and  childid<>codeitemid");
			  sqlstr.append(" ) b where num=0");
			  this.frecset = dao.search(sqlstr.toString());
			  while(this.frecset.next()){
			      lsb.linksdtuy(this.frecset.getString("codeitemid"),this.frecset.getString("codeitemdesc"), orgid);
              }
		}else{
			
			 lsb.linksdtuy(dutyid, dutydesc, orgid);
		}
			
	}
	
	public void insertManyorg(String dutytype,String codesetid,String dutyid,String dutydesc,String orgid,ContentDAO dao,LinkSdutyBo lsb) throws Exception{
		StringBuffer sqlstr = new StringBuffer(" select codeitemid from organization where parentid like '");
		   sqlstr.append(orgid);
		   sqlstr.append("' and codesetid='UM' and ");
		   sqlstr.append(Sql_switcher.dateValue(sdf.format(backdate)));
		   sqlstr.append(" between start_date and end_date order by a0000");
		   this.frowset = dao.search(sqlstr.toString());
		   
		   int i=1;
		   while(frowset.next()){
			   insertManyduty(dutytype, codesetid, dutyid, dutydesc, frowset.getString("codeitemid"), dao, lsb);
			   i++;
			   if(i%10 == 0){
				   lsb.clearUp();
			   }
		   }
	}
	
	public String checkExist(String orgid,String linkdutyitemid,String codesetid,String dutyid, ContentDAO dao,String insertOrgtype) throws SQLException{
		StringBuffer sqlstr = new StringBuffer();
		sqlstr.append("select '1' from k01 where e01a1 in (select codeitemid from organization where "); 
		
		if("all".equals(insertOrgtype))
		    sqlstr.append(" codeitemid like '"+orgid+"%' ");
		else
		    sqlstr.append(" parentid='"+orgid+"' ");
		
		sqlstr.append(" and "+Sql_switcher.dateValue(sdf.format(backdate)));
		sqlstr.append(" between start_date and end_date and codesetid='@K'");
		
		if("all".equals(insertOrgtype))
		    sqlstr.append(" and parentid<>'"+orgid+"' ");
		    
		sqlstr.append(") and ");
		sqlstr.append(linkdutyitemid);
		sqlstr.append(" in (select codeitemid from codeitem where codesetid='");
		sqlstr.append(codesetid+"' and "+Sql_switcher.dateValue(sdf.format(backdate)));
		sqlstr.append(" between start_date and end_date ");
		if(!"root".equals(dutyid))
		   sqlstr.append("and codeitemid like '"+dutyid+"%' ");
		sqlstr.append("and childid=codeitemid)");
		
		this.frowset = dao.search(sqlstr.toString());
		
		if(this.frowset.next()){
			return "2";
		}
		
		return "1";
		//String sql = "select '1' from k01 where e01a1 in (select codeitemid from organization where parentid like '%' and backdate between start_date and end_date) and k011h in (select codeitemid from codeitem where codesetid='To' and backdate between start_date and end_date and codeitemid like '%' and childid=codeitemid)";
	}
	
	private String checkHasChild(String orgid){
		String num="0";
		try{
		ContentDAO dao = new ContentDAO(frameconn);
		String sql = "select '1' from organization where parentid like '"+orgid+"%' and codesetid='UM'";
		frowset = dao.search(sql);
		if(frowset.next())
			num = "n";
		}catch (Exception e) {
			e.printStackTrace();
		}
         return num;		
	}
}
