package com.hjsj.hrms.transaction.general.inform.emp.batch;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.info.SortFilter;
import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DoCountTrans extends IBusiness {

	public void execute() throws GeneralException {
		try {
    		String flag = (String)this.getFormHM().get("flag");
    		String setname = (String)this.getFormHM().get("setname");
    		String dbname = (String)this.getFormHM().get("dbname");
    		ArrayList itemid_arr = (ArrayList)this.getFormHM().get("itemid_arr");
    		int count=0;
    		String infor = (String)this.getFormHM().get("infor");
    		if("alertmore".equals(flag)){
    			if("1".equals(infor))
    				isUnique(itemid_arr);
    			String selectid= (String)this.getFormHM().get("selectid");
    			String history= (String)this.getFormHM().get("history");
    			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
    			ContentDAO dao = new ContentDAO(this.frameconn); 
    			String itemid = "";
    			String resultTable="";
    			itemid="A0100";
    			String tablename = dbname+setname;
    			if(!"1".equals(infor)){
    				tablename = setname;
    				if("0".equals(selectid)){
    					String strid = (String)this.getFormHM().get("strid");
    					if(strid.length()>0){
    						String strs[] = strid.split("`");
    						count = strs.length;
    					}
    				}else{
    					if(setname.startsWith("B")||setname.startsWith("b"))
    						itemid = "b0110";
    					else 
    						itemid = "e01a1";
    					if(!fieldset.isMainset()){
    						StringBuffer sqlstr = new StringBuffer();
    						sqlstr.append("select ");
    						sqlstr.append(" count(*) as countid from ");
    						sqlstr.append(tablename);
    						String wherestr = codeWhere(setname);
    							if(wherestr.trim().length()>1){
    								sqlstr.append(" where ");
    								sqlstr.append(wherestr);
    							}
    						if("0".equals(history)){
    							sqlstr.append(" group by ");
    							sqlstr.append(itemid);
    						}
    						
    						try {
    							RowSet rs=dao.search(sqlstr.toString());
    							if("0".equals(history)){
    								while(rs.next()){
    									count++;
    								} 
    							}else{
    								while(rs.next()){
    									count = rs.getInt("countid");
    								} 
    							}
    						} catch (SQLException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}else{
    						StringBuffer sqlstr = new StringBuffer();
    						sqlstr.append("select ");
    						sqlstr.append(" count(*) as countid from ");
    						sqlstr.append(tablename);
    						
    						sqlstr.append(" where ");
    						sqlstr.append(codeWhere(setname));
    						try {
    							RowSet rs=dao.search(sqlstr.toString());
    							
    							while(rs.next()){
    								count = rs.getInt("countid");
    							}
    						} catch (SQLException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}
    				}
    			}else{
    				resultTable=this.userView.getUserName()+dbname+"result";
    				//20141029  dengcan 人员信息批量修改增加对所选记录的更新操作
    				if("2".equals(selectid)){
    					String strid = (String)this.getFormHM().get("strid");
    					if(strid.length()>0){
    					  //zxj 20150925 传过来的数据是`分隔的
    						String strs[] = strid.split("`");
    						//外部培训中的strids中的数据用逗号分隔
    						if(strid.indexOf(",") != -1)
    						    strs = strid.split(",");
    						
    						count = strs.length;
    					}
    				}
    				else
    				{
    				
    					if(!fieldset.isMainset()){
    						StringBuffer sqlstr = new StringBuffer();
    						sqlstr.append("select ");
    						sqlstr.append(" count(*) as countid from ");
    						sqlstr.append(tablename);
    						String wherestr = codeWhere(setname,dbname);
    						if("0".equals(selectid)){
    							sqlstr.append(" where ");
    							if(wherestr.trim().length()>1){
    								sqlstr.append(wherestr);
    								sqlstr.append(" and ");
    							}
    							sqlstr.append(itemid+" in(select "+itemid+" from ");
    							sqlstr.append(resultTable+") ");
    						} 
    						else{
    							if(wherestr.trim().length()>1){
    								sqlstr.append(" where ");
    								sqlstr.append(wherestr);
    							}
    						}
    						if("0".equals(history)){
    							sqlstr.append(" group by ");
    							sqlstr.append(itemid);
    						}
    						
    						try {
    							RowSet rs=dao.search(sqlstr.toString());
    							if("0".equals(history)){
    								while(rs.next()){
    									count++;
    								} 
    							}else{
    								while(rs.next()){
    									count = rs.getInt("countid");
    								} 
    							}
    						} catch (SQLException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}else{
    						StringBuffer sqlstr = new StringBuffer();
    						sqlstr.append("select ");
    						sqlstr.append(" count(*) as countid from ");
    						sqlstr.append(tablename);
    						
    						sqlstr.append(" where ");
    						sqlstr.append(codeWhere(setname,dbname));
    						if("0".equals(selectid)){
    							if(userView.getStatus()==0){
    								sqlstr.append(" and "+itemid+" in(select "+itemid+" from ");
    								sqlstr.append(resultTable+") ");
    							}else if(userView.getStatus()==4){
    								sqlstr.append(" and A0100 in(select obj_id from ");
    								sqlstr.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0)");
    							}
    						}
    						try {
    							RowSet rs=dao.search(sqlstr.toString());
    							
    							while(rs.next()){
    								count = rs.getInt("countid");
    							}
    						} catch (SQLException e) {
    							// TODO Auto-generated catch block
    							e.printStackTrace();
    						}
    					}
    				}
    			}
    		}else if("updatecol".equals(flag)){
    			String selectid= (String)this.getFormHM().get("results");
    			String history= (String)this.getFormHM().get("history");
    			FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
    			ContentDAO dao = new ContentDAO(this.frameconn); 
    			String itemid = "";
    			String resultTable="";
    			itemid="A0100";
    			String tablename = dbname+setname;
    			resultTable=this.userView.getUserName()+dbname+"result";
    			if(!fieldset.isMainset()){
    				StringBuffer sqlstr = new StringBuffer();
    				sqlstr.append("select ");
    				sqlstr.append(" count(*) as countid from ");
    				sqlstr.append(tablename);
    				String wherestr = codeWhere(setname,dbname);
    				if("1".equals(selectid)){
    					sqlstr.append(" where ");
    					if(wherestr.trim().length()>1){
    						sqlstr.append(wherestr);
    						sqlstr.append(" and ");
    					}
    					sqlstr.append(itemid+" in(select "+itemid+" from ");
    					sqlstr.append(resultTable+") ");
    				}else{
    					if(wherestr.trim().length()>1){
    						sqlstr.append(" where ");
    						sqlstr.append(wherestr);
    					}
    				}
    				if("1".equals(history)){
    					sqlstr.append(" group by ");
    					sqlstr.append(itemid);
    				}
    				
    				try {
    					RowSet rs=dao.search(sqlstr.toString());
    					if("1".equals(history)){
    						while(rs.next()){
    							count++;
    						} 
    					}else{
    						while(rs.next()){
    							count = rs.getInt("countid");
    						} 
    					}
    				} catch (SQLException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}else{
    				StringBuffer sqlstr = new StringBuffer();
    				sqlstr.append("select ");
    				sqlstr.append(" count(*) as countid from ");
    				sqlstr.append(tablename);
    				
    				sqlstr.append(" where ");
    				sqlstr.append(codeWhere(setname,dbname));
    				if("1".equals(selectid)){
    					//tianye add 如果登陆用户是自助用户这查询t_sys_result表
    					if(userView.getStatus()==0){
    						sqlstr.append(" and "+itemid+" in(select "+itemid+" from ");
    						sqlstr.append(resultTable+") ");
    					}else if(userView.getStatus()==4){
    						sqlstr.append(" and A0100 in(select obj_id from ");
    						sqlstr.append("t_sys_result where upper(username)='"+userView.getUserName().toUpperCase()+"' and upper(nbase)='"+dbname.toUpperCase()+"' and flag=0)");
    					}
    				}
    				try {
    					RowSet rs=dao.search(sqlstr.toString());
    					
    					while(rs.next()){
    						count = rs.getInt("countid");
    					}
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		this.getFormHM().put("count", count+"");
		} catch (Exception e) {
		    e.printStackTrace();
        }	
	}

	public String codeWhere(String setname,String dbname) throws GeneralException{
		InfoUtils infoUtils=new InfoUtils();
		String personsortfield=new SortFilter().getSortPersonField(this.getFrameconn());
		String codesetid = this.userView.getManagePrivCode();
		String kind="2";
		if("UN".equalsIgnoreCase(codesetid)){
			kind="2";
		}else if("UM".equalsIgnoreCase(codesetid)){
			kind="1";
		}else if("@K".equalsIgnoreCase(codesetid)){
			kind="0";
		}
		ArrayList list = new ArrayList();
		list.add("flag");
		list.add("unit");//兼职单位
		list.add("setid");//兼职子集
		list.add("appoint");//兼职标识
		list.add("pos");//兼职职务
		String part_setid="";
		String part_unit="";
		String appoint=" ";
		String flag="";
		String part_pos="";
		//兼职处理
		Sys_Oth_Parameter sysoth=new Sys_Oth_Parameter(this.getFrameconn());
    	HashMap map = sysoth.getAttributeValues(Sys_Oth_Parameter.PART_TIME,list);
    	if(map!=null&& map.size()!=0){
			if(map.get("flag")!=null && ((String)map.get("flag")).trim().length()>0)
				flag=(String)map.get("flag");
			if(flag!=null&& "true".equalsIgnoreCase(flag))
			{
				if(map.get("unit")!=null && ((String)map.get("unit")).trim().length()>0)
					part_unit=(String)map.get("unit");
				if(map.get("setid")!=null && ((String)map.get("setid")).trim().length()>0)
					part_setid=(String)map.get("setid");
				if(map.get("appoint")!=null && ((String)map.get("appoint")).trim().length()>0)
					appoint=(String)map.get("appoint");
				if(map.get("pos")!=null && ((String)map.get("pos")).trim().length()>0)
					part_pos=(String)map.get("pos");
			}		
		}
		String term_Sql=infoUtils.getWhereSQL(this.getFrameconn(),this.userView,dbname,this.userView.getManagePrivCodeValue(),true,kind,"org",personsortfield,"All",part_unit,part_setid,appoint,"");
		
		StringBuffer sqlstr = new StringBuffer();
			sqlstr.append("A0100 in (");
			sqlstr.append(term_Sql);
			sqlstr.append(")");
		
		return sqlstr.toString();
	}
	public String codeWhere(String setname) throws GeneralException{
		String codevalue = this.userView.getManagePrivCodeValue();
		StringBuffer sqlstr = new StringBuffer();
		if(setname.startsWith("B")){
			sqlstr.append("b0110 like '"+codevalue+"%'");
		}else{
			sqlstr.append("e01a1 like '"+codevalue+"%'");
		}
		return sqlstr.toString();
	}
	private void isUnique(ArrayList itemid_arr)
	{
		try{
		boolean isUnique = false;
		Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.getFrameconn());
		String chk = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","name");//省份证
		String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一性指标
		String chkvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"1","valid");
		String uniquenessvalid = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","valid");
		if(chkvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 chkvalid="0";
		 if(uniquenessvalid==null)
			 uniquenessvalid="";
		 String chkcheck="",uniquenesscheck="";

		 if("0".equalsIgnoreCase(chkvalid)|| "".equalsIgnoreCase(chkvalid)){
			 chkcheck="";
		 }
		 else{
			 chkcheck="checked";
		 }
		 if("0".equalsIgnoreCase(uniquenessvalid)|| "".equalsIgnoreCase(uniquenessvalid)){
			 uniquenesscheck="";
		 }
		 else{
			 uniquenesscheck="checked";
		 }
		if(chk==null)
			 chk="";
		chk= chk.toLowerCase();
		if(onlyname==null)
			 onlyname = "";
		onlyname = onlyname.toLowerCase();
		for(int i=0;i<itemid_arr.size();i++){
			String itemid = itemid_arr.get(i).toString().toLowerCase();
			if(itemid.equals(chk)&& "checked".equals(chkcheck)){
				isUnique = true;
				break;
			}else if(itemid.equals(onlyname)&& "checked".equals(uniquenesscheck)){
				isUnique = true;
				break;
			}
		}
		this.getFormHM().put("isUnique", new Boolean(isUnique));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
