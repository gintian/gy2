package com.hjsj.hrms.transaction.gz.gz_accounting;

import com.hjsj.hrms.businessobject.gz.SalaryCtrlParamBo;
import com.hjsj.hrms.businessobject.gz.SalaryTemplateBo;
import com.hjsj.hrms.businessobject.sys.CheckPrivSafeBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * <p>Title:</p>
 * <p>Description:</p>
 * <p>Company:hjsj</p>
 * <p>create time:${date}:${time}</p>
 * @author JinChunhai
 * @version 5.0
 * 
 */

public class SumSalaryTrans extends IBusiness {

	public void execute() throws GeneralException {
		
//		HashMap map = (HashMap)this.getFormHM().get("requestPamaHM");
		try
		{
		 
			String opt=(String)this.getFormHM().get("opt");
			String itemid="";
			String theObj1="";
			ArrayList item_ids = new ArrayList();
			if("2".equals(opt))
			{
				item_ids = (ArrayList) this.getFormHM().get("itemids");
			}else{
				itemid=(String)this.getFormHM().get("itemid");
				theObj1=(String)this.getFormHM().get("theObj1");
			}		
			String sp=(String) this.getFormHM().get("sp");
			String salaryid=(String)this.getFormHM().get("salaryid");		
			
			//如果用户没有当前薪资类别的资源权限   20140903  dengcan
			CheckPrivSafeBo safeBo=new CheckPrivSafeBo(this.getFrameconn(),this.userView);
			safeBo.isSalarySetResource(salaryid,null);
			
			String _filterWhl=(String)this.getFormHM().get("filterWhl");
			_filterWhl=PubFunc.decrypt(_filterWhl);
	//		String a_code=(String)this.getFormHM().get("a_code");
			StringBuffer filterWhl=new StringBuffer("");
			
			ArrayList sumsalarys = new ArrayList();
			ArrayList decwidths = new ArrayList();
			String sumsalary="";
			String decwidth="";
			RowSet rowSet=null;
		 
			ContentDAO dao=new ContentDAO(this.getFrameconn());
								
			SalaryTemplateBo gzbo=new SalaryTemplateBo(this.getFrameconn(),Integer.parseInt(salaryid),this.userView);
			String manager=gzbo.getCtrlparam().getValue(SalaryCtrlParamBo.SHARE_SET, "user");
			
			String a_code="";
			if(manager!=null&&manager.length()>0&&!manager.equalsIgnoreCase(this.userView.getUserName()))
			{
				String showUnitCodeTree=gzbo.getControlByUnitcode();
				if("1".equals(showUnitCodeTree)) ////是否按操作单位来控制
				{
					 
					String whl_str=gzbo.getWhlByUnits();
					if(whl_str.length()>0)
					{ 
						filterWhl.append(whl_str);
					}
				}
				else
				{ 
					if(this.userView.getManagePrivCode().length()==0)
						a_code="UN";
					else if("@K".equals(this.userView.getManagePrivCode()))
						a_code=gzbo.getUnByPosition(this.userView.getManagePrivCodeValue());
					else
						a_code=this.userView.getManagePrivCode()+this.userView.getManagePrivCodeValue();
				}
			}
			if(a_code.length()>0)
			{
				String codesetid=a_code.substring(0, 2);
				String value=a_code.substring(2);
				if("UN".equalsIgnoreCase(codesetid))
				{
					filterWhl.append(" and (B0110 like '"+value+"%'");
					if("".equalsIgnoreCase(value))
					{
						filterWhl.append(" or B0110 is null");
					}
					filterWhl.append(")");
				}
				if("UM".equalsIgnoreCase(codesetid))
				{
					filterWhl.append(" and E0122 like '"+value+"%'");
				}
			}
			if(!"sp".equals(sp)){
				if("2".equals(opt))
				{	
					if(item_ids!=null||item_ids.size()>0)
					{									
						for(int i=0;i<item_ids.size();i++)
						{						
							String itemids=(String)item_ids.get(i);
							
							String sql= "select sum("+itemids+") sumsalary from "+gzbo.getGz_tablename()+" where 1=1 "+filterWhl.toString() ;
							if(_filterWhl!=null&&_filterWhl.trim().length()>0)
								sql+=_filterWhl;
							rowSet=dao.search(sql);						
							while(rowSet.next())
							{
								sumsalarys.add(rowSet.getString("sumsalary"));				
							}
						}					
					}				
				}else{
					String strSql=("select decwidth from salaryset where salaryid="+salaryid+" and itemid='"+itemid+"'");			
					rowSet=dao.search(strSql);
					while(rowSet.next())
					{
						decwidth=rowSet.getString("decwidth");				
					}
					
					String sql= "select sum("+itemid+") sumsalary from "+gzbo.getGz_tablename()+" where 1=1 "+filterWhl.toString() ;
					if(_filterWhl!=null&&_filterWhl.trim().length()>0)
						sql+=_filterWhl;
					rowSet=dao.search(sql);						
					while(rowSet.next())
					{
						sumsalary=rowSet.getString("sumsalary");				
					}
				}
			}else{
				if("2".equals(opt))
				{	
					if(item_ids!=null||item_ids.size()>0)
					{			
						StringBuffer buf =new StringBuffer();
						for(int i=0;i<item_ids.size();i++){
							String itemids=(String)item_ids.get(i);
							if(i<item_ids.size()-1){
								buf.append(" sum("+itemids+") as "+itemids+",");
							}else if(i==item_ids.size()-1){
								buf.append(" sum("+itemids+") as "+itemids);
							}							
						}
						String sql="select "+buf.toString()+" from salaryhistory where salaryid= "+salaryid+filterWhl.toString() ;
						if(_filterWhl!=null&&_filterWhl.trim().length()>0)
							sql+=_filterWhl;
						rowSet=dao.search(sql);	
						while(rowSet.next())
						{
							for(int i=0;i<item_ids.size();i++){
								String itemids=(String)item_ids.get(i);
								sumsalarys.add(rowSet.getString(itemids));
							}
						}								
					}				
				}else{
					String strSql=("select decwidth from salaryset where salaryid="+salaryid+" and itemid='"+itemid+"'");			
					rowSet=dao.search(strSql);
					while(rowSet.next())
					{
						decwidth=rowSet.getString("decwidth");				
					}
					
					String sql= "select sum("+itemid+") sumsalary from salaryhistory where salaryid= "+salaryid+filterWhl.toString() ;
					if(_filterWhl!=null&&_filterWhl.trim().length()>0)
						sql+=_filterWhl;
					rowSet=dao.search(sql);						
					while(rowSet.next())
					{
						sumsalary=rowSet.getString("sumsalary");				
					}
				}
			}
										
			if("2".equals(opt))
			{	
				ArrayList sumnumbers = new ArrayList();
				if(sumsalarys!=null||sumsalarys.size()>0)
				{					
					for(int i=0;i<sumsalarys.size();i++)
					{
						if(sumsalarys.get(i)==null)
						{
							sumnumbers.add(String.valueOf(0));
						}else{
							
							String sary = (String)sumsalarys.get(i);							
							if(sary.indexOf(".")!=-1)
								sumnumbers.add(sary);					
							else							
								sumnumbers.add((sary+".00"));									
						}
					}
				}								
				this.getFormHM().put("sumsalarys", sumnumbers);				
			}else{
				String sumnumbers ="";
				if(sumsalary==null)				
					sumnumbers = "0";
				else
				{
					if(sumsalary.indexOf(".")!=-1)
						sumnumbers = sumsalary;						
					else
					{
						if("2".equalsIgnoreCase(decwidth))
							sumnumbers = (sumsalary+".00");
						else
							sumnumbers = sumsalary;		
					}
				}
				this.getFormHM().put("sumsalary", sumnumbers);
				this.getFormHM().put("theObj1", theObj1);
				this.getFormHM().put("decwidth", decwidth);
			}			
			
			if(rowSet!=null)
				rowSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}			
	}	
}
