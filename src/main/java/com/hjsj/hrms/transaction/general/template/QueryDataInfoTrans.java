package com.hjsj.hrms.transaction.general.template;

import com.hjsj.hrms.businessobject.general.template.TemplateTableBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.DatabaseMetaData;
import java.util.HashMap;

/**
 * 
 * <p>Title:QueryDataInfoTrans.java</p>
 * <p>Description:查找模板内的人员</p> 
 * <p>Company:hjsj</p> 
 * create time at:2014-2-20 上午10:38:04 
 * @author dengcan
 * @version 6.x
 */
public class QueryDataInfoTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String name=(String)this.getFormHM().get("name");
			String tabid=(String)this.getFormHM().get("tabid");
			String ins_id=(String)this.getFormHM().get("ins_id");
			String opt=(String)this.getFormHM().get("opt");  // 1:按名称查询   2：按ID值查询
			String strsql=(String)this.userView.getHm().get("sql_filter");
			int index_from=strsql.toString().indexOf("from");
			int order_index=strsql.lastIndexOf("order by ");
			String order_sql=strsql.substring(order_index);
			String sub_sql=strsql.substring(6,order_index);//去掉了 select 去掉了 order by
			TemplateTableBo tablebo=new TemplateTableBo(this.getFrameconn(),Integer.parseInt(tabid),this.userView);
			String returnInfo="";  //nbase`a0100`ins_id`pageCount
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
            DatabaseMetaData dbMeta = this.frameconn.getMetaData();
            int version=dbMeta.getDatabaseMajorVersion();  //  sql2000=8    sql2005=9    sql2008=10    sql2012=11
			if(version==8){
				HashMap paramMap = new HashMap();
				paramMap.put("strsql", strsql);
				paramMap.put("opt", opt);
				paramMap.put("tablebo", tablebo);
				paramMap.put("ins_id", ins_id);
				paramMap.put("name", name);
				returnInfo=this.queryInforForSql2K(paramMap);
			}else{
				if("1".equals(opt))
				{
					String item_id="a0101_1";
					if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
						item_id="codeitemdesc_1";
					
					strsql="select x.* from  ( select row_number() over ("+order_sql+") as cn,"+sub_sql+") x  where "+item_id+" like '%"+name+"%' ";
					
				}
				else
				{
					String basepre=(String)this.getFormHM().get("basepre");
					String a0100=(String)this.getFormHM().get("a0100");
					if(tablebo.getInfor_type()==1)
					{
						strsql="select x.* from  ( select row_number() over ("+order_sql+") as cn,"+sub_sql+") x  where a0100='"+a0100+"' and lower(basepre)='"+basepre.toLowerCase()+"' ";
					}
					else if(tablebo.getInfor_type()==2)
					{
						strsql="select x.* from  ( select row_number() over ("+order_sql+") as cn,"+sub_sql+") x  where b0110 ='"+a0100+"' ";
					}
					else if(tablebo.getInfor_type()==3)
					{
						strsql="select x.* from  ( select row_number() over ("+order_sql+") as cn,"+sub_sql+") x  where e01a1 ='"+a0100+"' ";
					}
					
				}
				
				this.frowset=dao.search(strsql);
				if(this.frowset.next())
				{
					int cn=this.frowset.getInt("cn");
					if(tablebo.getInfor_type()==1)  //人员
						returnInfo=this.frowset.getString("basepre")+"`"+this.frowset.getString("a0100");
					else if(tablebo.getInfor_type()==2)  //单位部门
						returnInfo=this.frowset.getString("B0110")+"`"+this.frowset.getString("B0110");
					else if(tablebo.getInfor_type()==3)  //岗位
						returnInfo=this.frowset.getString("E01A1")+"`"+this.frowset.getString("E01A1");
					
					if("0".equals(ins_id))
						returnInfo+="`0";
					else
						returnInfo+="`"+this.frowset.getInt("ins_id");				
					int mode=cn%40;
					int _pageCount=cn/40;
					if(mode>0)
						_pageCount++;
					returnInfo+="`"+_pageCount;
				}
			}	
			this.getFormHM().put("returnInfo",returnInfo);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
		/**为sql2K做的分页查询，人员定位**/
		public String queryInforForSql2K(HashMap paramMap){
			String returnInfo="";
			try{
				String querySql="";
				String opt =(String) paramMap.get("opt");
				TemplateTableBo tablebo = (TemplateTableBo) paramMap.get("tablebo");
				String strsql=(String) paramMap.get("strsql");
				String ins_id=(String) paramMap.get("ins_id");
				ContentDAO dao = new ContentDAO(this.getFrameconn());
				if("1".equals(opt)){
					String name=(String) paramMap.get("name");
					String item_id="a0101_1";
					if(tablebo.getInfor_type()==2||tablebo.getInfor_type()==3)
						item_id="codeitemdesc_1";
					querySql=strsql;
					this.frowset=dao.search(querySql);
					String queryValue="";
					int countNum =0;
					while(this.frowset.next()){
						queryValue=this.frowset.getString(item_id);
						countNum++;
						if(queryValue.indexOf(name)!=-1){
							returnInfo=this.getReturnInfo(tablebo, this.frowset, ins_id, countNum);
							break;
						}
					}
				}else{
					String basepre=(String)this.getFormHM().get("basepre");
					String a0100=(String)this.getFormHM().get("a0100");
					querySql=strsql;
					this.frowset=dao.search(querySql);
					String queryBasepre="";
					String queryA0100="";
					int countNum =0;
					while(this.frowset.next()){
						countNum++;
						if(tablebo.getInfor_type()==1){
							queryBasepre=this.frowset.getString("basepre");
							queryA0100=this.frowset.getString("a0100");
							if(queryBasepre.equals(basepre)&&queryA0100.equals(a0100)){
								returnInfo=this.getReturnInfo(tablebo, this.frowset, ins_id, countNum);
								break;
							}
						}else if(tablebo.getInfor_type()==2){
							queryA0100=this.frowset.getString("B0110");
							if(queryA0100.equals(a0100)){
								returnInfo=this.getReturnInfo(tablebo, this.frowset, ins_id, countNum);
								break;
							}
						}else if(tablebo.getInfor_type()==3){
							queryA0100=this.frowset.getString("E01A1");
							if(queryA0100.equals(a0100)){
								returnInfo=this.getReturnInfo(tablebo, this.frowset, ins_id, countNum);
								break;
							}
						}
					}
			}
			}catch(Exception e){
				e.printStackTrace();
			}
			return returnInfo;
		}
		/**
		 * 
		* @Title: getReturnInfo 
		* @Description:得到拼接后的返回信息
		* @param @param tablebo tablebo业务类对象
		* @param @param rs 查询数据集
		* @param @param ins_id 任务流程号
		* @param @param countNum 该人员在查询中的位置
		* @return String    返回类型 
		* @throws
		 */
		public String getReturnInfo(TemplateTableBo tablebo,RowSet rs,String ins_id,int countNum){
			String returnInfo="";
			try{
				if(tablebo.getInfor_type()==1)  //人员
					returnInfo=rs.getString("basepre")+"`"+rs.getString("a0100");
				else if(tablebo.getInfor_type()==2)  //单位部门
					returnInfo=rs.getString("B0110")+"`"+rs.getString("B0110");
				else if(tablebo.getInfor_type()==3)  //岗位
					returnInfo=rs.getString("E01A1")+"`"+rs.getString("E01A1");
				if("0".equals(ins_id))
					returnInfo+="`0";
				else
					returnInfo+="`"+rs.getInt("ins_id");				
				int mode=countNum%40;
				int _pageCount=countNum/40;
				if(mode>0)
					_pageCount++;
				returnInfo+="`"+_pageCount;
			}catch(Exception e){
				e.printStackTrace();
			}
			return returnInfo;
		}
}
