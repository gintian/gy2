package com.hjsj.hrms.module.card.transaction;

import com.hjsj.hrms.businessobject.performance.PerformanceImplementBo;
import com.hjsj.hrms.businessobject.ykcard.CardConstantSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.lang.StringUtils;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 给打印预览发送显示数据
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 */
public class SearchPrintviewPersonTran extends IBusiness {

	/* (non-Javadoc)
	 * @see com.hrms.struts.facade.transaction.IBusiness#execute()
	 */
	@Override
    public void execute() throws GeneralException {
		String dbname=(String)this.getFormHM().get("dbname");
		String inforkind=(String)this.getFormHM().get("inforkind");
		String plan_id=(String)this.getFormHM().get("plan_id");
		String a0100=(String)this.getFormHM().get("a0100");
		String A0100="";
		if(StringUtils.isNotEmpty(a0100)&&a0100.indexOf("`")>-1) {
			dbname=a0100.split("`")[0];
			A0100=a0100.split("`")[1];
		}
		ArrayList<String> selectlist=(ArrayList<String>)this.getFormHM().get("selectArry");
		StringBuffer sql=new StringBuffer();
		ArrayList personlist=new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		RowSet rs=null;
		DbWizard db=new DbWizard(this.frameconn);
		try{
			//界面选中打印预览显示对应人员
			if(selectlist!=null&&selectlist.size()>0) {
				String tag="";
				String[] arry=null;
				CommonData dataobj=null;
				for(String obj:selectlist) {
					if(StringUtils.isNotEmpty(obj)&&obj.indexOf("`")>-1) {
						arry=obj.split("`");
						if("1".equals(inforkind)) {
							tag="<NBASE>"+arry[0]+"</NBASE><ID>"+arry[1]+"</ID><NAME>"+arry[2]+"</NAME>";
							dataobj=new CommonData(arry[1],tag);
						}else {
							tag="<NBASE></NBASE><ID>"+arry[0]+"</ID><NAME>"+arry[1]+"</NAME>";
							dataobj=new CommonData(arry[0],tag);
						}
						personlist.add(dataobj);
					}
				}
			 this.getFormHM().put("personlist",personlist);
			 this.getFormHM().put("flagType", true);
			 
			 return;
			}
			
			if(db.isExistTable("t_card_result", false)||"5".equals(inforkind)) {
				sql.setLength(0);
				if("1".equals(inforkind)) {
					ArrayList dbList=this.userView.getPrivDbList();
					if(dbList==null)
						dbList.add(this.userView.getDbname());
					for(int i=0;i<dbList.size();i++) {
						sql.append("select A.objid,B.A0101 as objDesc,B.A0000,'"+dbList.get(i).toString()+"' as dbname ");
						sql.append("from (");
						sql.append("select objid from t_card_result ");
						sql.append("where username='"+this.userView.getUserName()+"'");
						sql.append(" and flag=1 ");
						sql.append(" and nbase='"+dbList.get(i).toString()+"'");
						sql.append(")A,");
						sql.append(dbList.get(i).toString()+"A01 B where A.objid=B.A0100" );
						if(!this.userView.isSuper_admin()){
							sql.append(" and A.objid in (select A0100 ");
							sql.append(	this.userView.getPrivSQLExpression(dbList.get(i).toString(), false));//业务用户 人员权限修改后临时表对应人员范围未修改，现关联人员权限查询临时表数据
							sql.append(")");
						}
						if(i<dbList.size()-1)
							sql.append(" union all ");
					}
					
				}else if("2".equals(inforkind)) {
					sql.append("select A.objid,B.codeitemdesc as objDesc,B.A0000 ");
					sql.append("from (");
					sql.append("select objid from t_card_result ");
					sql.append("where username='"+this.userView.getUserName()+"'");
					sql.append(" and flag=2 ");
					sql.append(" and status="+this.userView.getStatus());
					sql.append(")A,");
					sql.append("organization B where A.objid=B.codeitemid");
					
				}else if("4".equals(inforkind)) {
					sql.append("select A.objid,B.codeitemdesc as objDesc,B.codeitemid as A0000 ");
					sql.append("from (");
					sql.append("select objid from t_card_result ");
					sql.append("where username='"+this.userView.getUserName()+"'");
					sql.append(" and flag=4 ");
					sql.append(" and status="+this.userView.getStatus());
					sql.append(")A,");
					sql.append("organization B where B.codeitemid=A.objid");
				}else if("5".equals(inforkind)) {
					sql.append("select object_id as objid,a0101 as objDesc,object_id as A0000 from per_result_"+plan_id);
			    	PerformanceImplementBo bo=new PerformanceImplementBo(this.frameconn,this.userView,plan_id);
			    	String where=bo.getPrivWhere(this.userView);		    	
			    	if(where!=null&&where.length()>0)
			    		sql.append(" where 1=1 "+where);
				}else if("6".equals(inforkind)) {
					String codeset=new CardConstantSet().getStdPosCodeSetId();
		    		sql.append("select A.objid as objid,B.codeitemdesc as objDesc,B.A0000 as A0000"
		    				+" from t_card_result A ,CodeItem B "
		    				+" where B.codeitemid=A.objid "
		    				+" and B.codesetid='"+codeset+"'"
		    				+" and A.flag=6"// 基准岗位
		    				+" and A.status="+this.userView.getStatus()
		    				+" and A.username='"+userView.getUserName()+"' ");
				}
			}
			 if(sql!=null&&sql.length()>0)
			 {
				 	if("1".equals(inforkind)) {
				 		rs=dao.search(sql.toString());
				 	}
				 	else {
				 		rs=dao.search(sql.toString()+" order by A0000");
				 	}
					String tag="";
					int num=0;
					while(rs.next()) {
						if("1".equals(inforkind)) {
							tag="<NBASE>"+rs.getString("dbname")+"</NBASE><ID>"+rs.getString(1)+"</ID><NAME>"+rs.getString(2)+"</NAME>";
						}
						else {
							tag="<NBASE></NBASE><ID>"+rs.getString(1)+"</ID><NAME>"+rs.getString(2)+"</NAME>";
						}
						CommonData dataobj=new CommonData(rs.getString(1),tag);
						personlist.add(dataobj);
						num++;
						if(num>500) {
							break;
						}
						if(rs.getString(1).equals(A0100)) {
							break;
						}
					}
					//自助用户无任何权限范围时
					if(personlist.size()<=0&&"1".equals(inforkind)&&this.userView.getStatus()==4) {
						tag="<NBASE>"+this.userView.getDbname()+"</NBASE><ID>"+this.userView.getA0100()+"</ID><NAME>"+this.userView.getUserFullName()+"</NAME>";
						CommonData dataobj=new CommonData(this.userView.getA0100(),tag);
						personlist.add(dataobj);
					}
					if(StringUtils.isNotEmpty(A0100)&&this.userView.getA0100().equals(A0100)) {// 58587
						personlist=new ArrayList();
						tag="<NBASE>"+this.userView.getDbname()+"</NBASE><ID>"+this.userView.getA0100()+"</ID><NAME>"+this.userView.getUserFullName()+"</NAME>";
						CommonData dataobj=new CommonData(this.userView.getA0100(),tag);
						personlist.add(dataobj);
					}
			 }
			 this.getFormHM().put("personlist",personlist);
			 this.getFormHM().put("flagType", true);
	    }catch(Exception ex)
		{
	    	this.getFormHM().put("flagType", false);
	    	this.getFormHM().put("eMsg", ex.getMessage());
			ex.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rs);
		}
	}

}
