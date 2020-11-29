package com.hjsj.hrms.transaction.general.template.templatelist;

import com.hjsj.hrms.businessobject.general.template.TemplateListBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 检查能否撤销、划转
 * @author xieguiquan
 *01 8, 2011
 */
public class CanDoItTrans extends IBusiness {

	public void execute() throws GeneralException {
		ArrayList orgcodeitemid=new ArrayList();
		String infor_type =(String)this.getFormHM().get("infor_type");
		String table_name =(String)this.getFormHM().get("table_name");
		//String hmuster_sql = (String)this.getFormHM().get("hmuster_sql");
		String operationtype = (String)this.getFormHM().get("operationtype");
		String tabid = (String)this.getFormHM().get("tabid");
		String d="",maxstartdate="",msg=""; 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		if(true)
//			throw new GeneralException("该单位不能重复合并！");
		try{
		
//			Calendar calendar = Calendar.getInstance();
//			calendar.add(Calendar.DATE, 0);
			
//			String date = sdf.format(calendar.getTime());
			ContentDAO dao = new ContentDAO(this.frameconn);
			//是否排除流程中进行合并，划转，撤销？
			this.frecset=dao.search(" select * from " +table_name+" where submitflag=1");
			while(this.frecset.next()){
					if(infor_type!=null&& "2".equals(infor_type))
						orgcodeitemid.add(this.frecset.getString("b0110"));
					else
						orgcodeitemid.add(this.frecset.getString("e01a1"));
			}
			DbWizard dbwizard=new DbWizard(this.getFrameconn());
			TemplateListBo bo=new TemplateListBo(tabid,this.getFrameconn(),this.userView);
			ArrayList templateSetList = bo.getAllCell();
			boolean flag1=false;
			boolean flag2=false;
			boolean flag3=false;
			for(int j=0;j<templateSetList.size();j++){
				LazyDynaBean abean = (LazyDynaBean)templateSetList.get(j);
				if(abean!=null&& "start_date_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
					flag1=true;
				if(abean!=null&& "codeitemdesc_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
					flag2=true;
				if(abean!=null&& "parentid_2".equalsIgnoreCase(abean.get("field_name").toString().trim()+"_"+abean.get("chgstate").toString().trim()))
					flag3=true;
			}
			if(!flag1||!dbwizard.isExistField(table_name, "start_date_2",false)){
				msg="模板中不存在变化后生效日期!\\n";
			}
			if("8".equals(operationtype)&&(!flag2||!dbwizard.isExistField(table_name, "codeitemdesc_2",false))){
				msg+="模板中不存在变化后组织单元名称!";
			}
			if("3".equals(infor_type)&& "9".equals(operationtype)&&(!flag3||!dbwizard.isExistField(table_name, "parentid_2",false))){
				msg+="模板中不存在变化后上级组织单元名称!";
			}
			if("2".equals(infor_type)||("3".equals(infor_type)&& "8".equals(operationtype))){
				ArrayList transferorglist =new ArrayList();
				this.frecset=dao.search("select * from "+table_name+" where submitflag=1");
				String selectall=",";
				String selectcomb = "";
				while(this.frecset.next()){ 
						if(infor_type!=null&& "2".equals(infor_type)){
							transferorglist.add(this.frecset.getString("b0110"));
							selectall+=this.frecset.getString("b0110")+",";
							if(this.frecset.getString("b0110").equals(this.frecset.getString("to_id"))){
								selectcomb+=this.frecset.getString("b0110")+",";
							}
						}
						else{
							transferorglist.add(this.frecset.getString("e01a1"));
							selectall+=this.frecset.getString("e01a1")+",";
							if(this.frecset.getString("e01a1").equals(this.frecset.getString("to_id"))){
								selectcomb+=this.frecset.getString("e01a1")+",";
						} 
					}
				}
				//已经存在被合并项
				if(selectcomb.length()>0&& "".equals(msg)&& "8".equals(operationtype)){
					msg="选项中存在已合并后的编码的记录,不能再次进行合并操作！";
				}
				if(selectcomb.length()>0&& "".equals(msg)&& "9".equals(operationtype)){
					msg="选项中存在已划转后的编码的记录,不能再次进行划转操作！";
				}
				//合并项之间不能存在上下级关系
				int size1 = transferorglist.size();
				transferorglist =getLazyDynaBeanToRecordVo(transferorglist);
				if("2".equals(infor_type)&&size1!=transferorglist.size()&& "".equals(msg)){
					msg="选项中存在上下级关系的记录,请不要选择有上下级关系的记录！";
				}
				//单位不能和部门合并
				if("8".equals(operationtype)&&transferorglist.size()>1){
					
					ArrayList trans = getGradeRecord2(transferorglist);
					if(trans.size()>0&& "".equals(msg)){
						msg="选项中存在单位和部门,请不要同时选择单位和部门！";
					}
				}
				//合并 必须是同级之间的合并
				if("8".equals(operationtype)&&transferorglist.size()>1){
					
					transferorglist = getGradeRecord(transferorglist);
					if(transferorglist.size()>0&& "".equals(msg)){
						msg="选项中上一级节点必须相同,请不要选择上一级节点不同的记录！";
					}
				}
			}
			
			if("8".equals(operationtype)&&orgcodeitemid.size()==1)
			{
				msg="单个机构不允许执行合并操作!";
			}
			
			if(orgcodeitemid.size()>0){
				for(int i=0;i<orgcodeitemid.size();i++){
					String sql = "select start_date from organization where codeitemid='"+(String)orgcodeitemid.get(i)+"'";
					this.frecset = dao.search(sql);
					if(this.frecset.next()){
						java.sql.Date temp = this.frecset.getDate("start_date");
						d = sdf.format(temp);
					}
						if("".equals(maxstartdate)){
							maxstartdate=d;
						}else{
							Date d1=sdf.parse(d);
							Date d2=sdf.parse(maxstartdate);
							if(d1.compareTo(d2)>0)
								maxstartdate=d;
						}
					if("".equals(msg))
					msg="ok";
				}
			}else{
				if("".equals(msg))
					msg="equals";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		//	throw GeneralExceptionHandler.Handle(e);
		}finally{
			Calendar calendar = Calendar.getInstance();
			//calendar.add(Calendar.DATE, -1);
			String date = sdf.format(calendar.getTime());
			if(date.equalsIgnoreCase(maxstartdate)){
				msg ="date";
			}
			
			
			this.getFormHM().put("msg", msg);
			this.getFormHM().put("maxstartdate", maxstartdate);
		}
	}
	private ArrayList getLazyDynaBeanToRecordVo(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
	
		for(int i=0;i<transferorglist.size();i++)
	    {
	   	    String codeitemid=(String)transferorglist.get(i);
	   	    StringBuffer strsql = new StringBuffer();//合并单位：存在上下级单位时，不能合并到下级单位。（解决出现断树状态）
	   		for(int j=0;j<transferorglist.size();j++)
		    {
	   			if(i==j){
	   				
	   			}else{
	   			 String codeitemid2=(String)transferorglist.get(j);
	   			 strsql.append(" and codeitemid not like '"+codeitemid2+"%' ");
	   			}
		   	    
		    }
			StringBuffer sql=new StringBuffer();   
			String table="organization";
			sql.append("select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from "+table+"");
			sql.append(" where codeitemid='"+codeitemid+"' "+strsql+"");
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo = null;
			try {
				rs=dao.search(sql.toString());
				if(rs.next())
				{
					vo=new RecordVo("organization");
					vo.setString("codesetid",rs.getString("codesetid"));
					vo.setString("codeitemdesc",rs.getString("codeitemdesc"));
					vo.setString("parentid",rs.getString("parentid"));
					vo.setString("childid",rs.getString("childid"));
					vo.setString("codeitemid",rs.getString("codeitemid"));
					vo.setInt("grade", rs.getInt("grade"));
				}else{
				//	throw GeneralExceptionHandler.Handle(new GeneralException("","虚拟机构不许合并，操作失败！","",""));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	    if(vo==null)
	   	    	continue;
	   	    list.add(codeitemid);
	    }
		return list;
	}
	private ArrayList getGradeRecord(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
		String str ="";
		try {
		for(int i=0;i<transferorglist.size();i++)
	    {
			str+= "'"+transferorglist.get(i)+"'"+",";
	    }
			str = str.substring(0,str.length()-1);
		String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") and codeitemid=parentid";
		
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo = null;
			
				rs=dao.search(sql.toString());
				int i=0;
				while(rs.next())
				{
					i++;
				}
				if(transferorglist.size()!=i&&i!=0){//存在顶级结点与非顶级结点
					return transferorglist;
				}else{
					if(transferorglist.size()==i){
						return list;
					}else{
						sql ="select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") ";
						String parentid="";
						rs=dao.search(sql.toString());
						int j=0;
						while(rs.next())
						{
							if(("'"+parentid+"'").indexOf("'"+rs.getString("parentid")+"'")==-1){
								parentid+="'"+rs.getString("parentid")+"'"+",";
								j++;
							}
						}
						if(j!=1){
							return transferorglist;
						}
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return list;
	}
	private ArrayList getGradeRecord2(ArrayList transferorglist) throws GeneralException{
		ArrayList list = new ArrayList();
		String str ="";
		try {
		for(int i=0;i<transferorglist.size();i++)
	    {
			str+= "'"+transferorglist.get(i)+"'"+",";
	    }
			str = str.substring(0,str.length()-1);
		String sql = "select codesetid,codeitemdesc,parentid,childid,codeitemid,grade from  organization where codeitemid in ("+str+") ";
		
			RowSet rs=null;
			ContentDAO dao=new ContentDAO(this.frameconn);
			RecordVo vo = null;
			
				rs=dao.search(sql.toString());
				int i=0;
				String codesetid="";
				while(rs.next())
				{
					if(("'"+codesetid+"'").indexOf("'"+rs.getString("codesetid")+"'")==-1){
						codesetid+="'"+rs.getString("codesetid")+"'"+",";
						i++;
					}
					if(i!=1){
						return transferorglist;
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return list;
	}
}
