/**
 * 
 */
package com.hjsj.hrms.transaction.ht.inform;

import com.hjsj.hrms.businessobject.general.info.EmpMaintenanBo;
import com.hjsj.hrms.businessobject.ht.inform.ContracInforBo;
import com.hjsj.hrms.businessobject.org.gzdatamaint.GzDataMaintBo;
import com.hjsj.hrms.businessobject.sys.ConstantXml;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.Field;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 *<p>Title:SearchDataTableTrans</p> 
 *<p>Description:查询数据表交易</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:2007-8-15:下午02:03:54</p> 
 *@author cmq
 *@version 4.0
 */
public class SearchDataTableTrans extends IBusiness {
	
	/**
	 * 求当前数据集的指标列表
	 * @param setname
	 * @param mfield
	 * @return
	 */
	private ArrayList getFieldList(String setname,String mfield){
		
		FieldSet fieldset=DataDictionary.getFieldSetVo(setname);
		GzDataMaintBo gzbo = new GzDataMaintBo(this.frameconn);
		ArrayList list = gzbo.itemList(fieldset);
		ArrayList fieldlist=new ArrayList();

		Field tempfield=new Field("A0100","A0100");
		tempfield.setDatatype(DataType.STRING);
		tempfield.setLength(8);
		tempfield.setVisible(false);
		fieldlist.add(tempfield);

		/**有排序功能时，让其对A0000字段的值可维护,也即手动排序*/
		tempfield=new Field("A0000",ResourceFactory.getProperty("kjg.gather.xuhao"));
		tempfield.setDatatype(DataType.INT);
		tempfield.setVisible(false);
		fieldlist.add(tempfield);
		
		for(int i=0;i<list.size();i++){
			Field field=(Field)list.get(i);
			String itemid=field.getName();
			
			if("0".equals(this.userView.analyseFieldPriv(itemid)))
				field.setVisible(false);
			if("1".equals(this.userView.analyseFieldPriv(itemid)))
				field.setReadonly(true);
			if(!"2".equals(this.userView.analyseTablePriv(setname)))
				field.setReadonly(true);
			if(mfield!=null&&mfield.length()>0){
				if(mfield.toLowerCase().indexOf(itemid.toLowerCase())==-1){
					field.setVisible(false);
				}else{
					field.setVisible(true);
				}
			}
			field.setSortable(true);
			field.setReadonly(true);
			fieldlist.add(field);
		}//i loop end.
		return fieldlist;
	}
	public void execute() throws GeneralException {
		try
		{
			String dbname=(String)this.getFormHM().get("dbname");
			dbname=dbname!=null&&dbname.trim().length()>0?dbname:"";
			
			String ctflag=(String)this.getFormHM().get("ctflag");
			ctflag=ctflag!=null&&ctflag.trim().length()>0?ctflag:"all";
			
			String viewsearch=(String)this.getFormHM().get("viewsearch");
			viewsearch=viewsearch!=null&&viewsearch.trim().length()>0?viewsearch:"0";
			
			
			ConstantXml csxml = new ConstantXml(this.frameconn,"HT_PARAM","Params");
			ContracInforBo ctbo = new ContracInforBo(this.frameconn,csxml);
			ctbo.setUserView(this.userView);
			ArrayList ctflaglist = ctbo.ctflagList();
			this.getFormHM().put("ctflaglist",ctflaglist);
			
			String mfield = csxml.getTextValue("/Params/mfield");
			mfield=mfield!=null&&mfield.trim().length()>0?mfield:"";
			
			String htmain = csxml.getTextValue("/Params/htmain");
			htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
			if(htmain.trim().length()<1){
				htmain = csxml.getConstantValue("HETONGMAIN");
				htmain=htmain!=null&&htmain.trim().length()>0?htmain:"";
			}

			EmpMaintenanBo embo = new EmpMaintenanBo(this.getFrameconn());
			ContentDAO dao = new ContentDAO(this.getFrameconn());

			/**对人员信息集*/
			String tablename=dbname+"A01";
			String a_code=(String)this.getFormHM().get("a_code");
			a_code=a_code!=null&&a_code.trim().length()>0?a_code:"";
			a_code= "all".equalsIgnoreCase(a_code)?"":a_code;

			StringBuffer sexpr=new StringBuffer();
			StringBuffer sfactor=new StringBuffer();
			if(a_code!=null&&a_code.trim().length()>1){
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);

				if(value!=null&&value.trim().length()>0){
					if("UN".equalsIgnoreCase(codesetid)){
						sexpr.append("B0110=");
						sexpr.append(value);
						sexpr.append("*`");
						sfactor.append("1");
					}else if("UM".equalsIgnoreCase(codesetid)){
						sexpr.append("E0122=");
						sexpr.append(value);
						sexpr.append("*`");
						sfactor.append("1");
					}else if("@K".equalsIgnoreCase(codesetid)){
						sexpr.append("E01A1=");
						sexpr.append(value);
						sexpr.append("*`");
						sfactor.append("1");
					}else{
						String[] codearr =a_code.split(":");
						if(codearr.length==3){
							sexpr.append(codearr[1]+"=");
							sexpr.append(codearr[2]);
							sexpr.append("*`");
							sfactor.append("1");
						}
					}
				}else{
					sexpr.append("B0110=");
					sexpr.append(value);
					sexpr.append("*`B0110=`");
					sfactor.append("1+2");
				}
			}	
			/**过滤条件*/
			String strwhere=userView.getPrivSQLExpression(sfactor.toString()+"|"+sexpr.toString(),dbname,false,true,new ArrayList());
			StringBuffer buf=new StringBuffer();
			buf.append("select A0100 ");
			buf.append(strwhere);
			if(viewsearch!=null&& "1".equals(viewsearch)){
				if(strwhere.indexOf("where")!=-1||strwhere.indexOf("WHERE")!=-1){
					if(this.userView.getStatus() != 4 && embo.checkResult(this.userView.getUserName(),dbname)) {
						buf.append(" and EXISTS (");
						buf.append("select A0100 from " + this.userView.getUserName()+dbname+"result");
						buf.append(" WHERE A0100=" + tablename + ".A0100)");
					} else if(this.userView.getStatus() == 4 && embo.checkResult("t_sys_result")) {
					    buf.append(" and EXISTS (");
					    buf.append("select obj_Id from t_sys_result result");
					    buf.append(" WHERE result.obj_Id=" + tablename + ".A0100");
					    buf.append(" and UPPER(result.nbase)='" + dbname.toUpperCase() + "'");
					    buf.append(" and username='" + this.userView.getUserName() +"')");
					} else {
						buf.append(" and 1=2");
					}
				}else{
				    if(this.userView.getStatus() != 4 && embo.checkResult(this.userView.getUserName(),dbname)) {
                        buf.append(" and EXISTS (");
                        buf.append("select A0100 from " + this.userView.getUserName()+dbname+"result");
                        buf.append(" WHERE A0100=" + tablename + ".A0100)");
                    } else if(this.userView.getStatus() == 4 && embo.checkResult("t_sys_result")) {
                        buf.append(" and EXISTS (");
                        buf.append("select obj_Id from t_sys_result result");
                        buf.append(" WHERE result.obj_Id=" + tablename + ".A0100");
                        buf.append(" and UPPER(result.nbase)='" + dbname.toUpperCase() + "'");
                        buf.append(" and username='" + this.userView.getUserName() +"')");
                    } else {
                        buf.append(" and 1=2");
                    }
				}
			}
			
			ArrayList list=getFieldList("A01",mfield);
			
			String htmainflagid = ctbo.getHtmainFlagID(htmain);
			
			if(htmain != null && htmain.length() > 0 && (htmainflagid == null || htmainflagid.length() < 1))
			    throw new GeneralException("", ResourceFactory.getProperty("ht.param.selhtset.error"), "", "");
			
			StringBuffer strsql=new StringBuffer();
			strsql.append("select * from ");
			strsql.append(tablename + " A ");
			strsql.append(" where EXISTS (" );
			strsql.append(buf.toString());
			strsql.append(" AND A0100=A.A0100");
			strsql.append(") ");
			if(htmainflagid!=null&&htmainflagid.length()>0&&!"all".equalsIgnoreCase(ctflag)){
				if("no".equalsIgnoreCase(ctflag)){
					//【66157】贵银问题：合同管理，在基础参数-合同信息集-选择合同标识代码类，之后在合同台账中筛选未签订，只是把该指标未维护的人筛选出来，没有子集记录的无法筛选出来，不对
					strsql.append("and NOT EXISTS (select A0100 from ");
					strsql.append(dbname+htmain);
					strsql.append(" where (");
					strsql.append(htmainflagid);
					strsql.append("!='' or ");
					strsql.append(htmainflagid);
					strsql.append(" is not null");
					strsql.append(") ");
				}else{
					strsql.append("and EXISTS (select A0100 from ");
					strsql.append(dbname+htmain);
					strsql.append(" where (");
					strsql.append(htmainflagid);
					strsql.append("='");
					strsql.append(ctflag);
					strsql.append("'");
					strsql.append(") ");
				}
				strsql.append(" AND A0100=A.A0100)");
			}
			strsql.append("order by  A0000");
			
			this.getFormHM().put("sql",strsql.toString());
			this.getFormHM().put("tablename",tablename);
			this.getFormHM().put("dbname",dbname);
			this.getFormHM().put("viewsearch",viewsearch);
			this.getFormHM().put("fieldlist",list);
			this.getFormHM().put("searchlist",searchTable(dao,"1"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	private ArrayList searchTable(ContentDAO dao,String type){
		ArrayList searchlist = new ArrayList();
		
		String sqlstr = "select id,name from LExpr where Type="+type;
		try {
			this.frowset=dao.search(sqlstr);
			int n=1;
			while(this.frowset.next()){
				if(!(this.userView.isHaveResource(IResourceConstant.LEXPR,this.frowset.getString("id"))))
                	continue;
				CommonData job=new CommonData();
				job.setDataName(this.frowset.getString("id"));
				job.setDataValue(this.frowset.getString("id")+"."+this.frowset.getString("name"));
				searchlist.add(job);
				n++;
				if(n>7)
					break;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return searchlist;
	}
}
