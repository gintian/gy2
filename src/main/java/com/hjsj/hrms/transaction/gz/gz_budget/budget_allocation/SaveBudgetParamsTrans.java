package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_add.BudgetAddBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_allocation.BudgetAllocBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SaveBudgetParamsTrans extends IBusiness {

	public void execute() throws GeneralException {
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 String tab_name=(String) this.getFormHM().get("tab_name");
		 String budget_id=(String) this.getFormHM().get("budget_id");
		 String b0110=(String) this.getFormHM().get("b0110");	
		 BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
		 String suoYin = ((String)sysbo.getSysValueMap().get("ysparam_idx_menu")).toLowerCase();
		 StringBuffer ssl=new StringBuffer();
		 RowSet rows=null;
		 try {
			 String riqi=this.getDate();
			 RecordVo ro=new RecordVo(tab_name);//预算总额参数表对象
			 BudgetAddBo addBo=new BudgetAddBo(this.getFrameconn(), this.userView);//调用新增预算业务类
			 String codeitemid=addBo.getCodeitemid();//顶级单位
//			 int idd=addBo.getCanShuId(codeitemid);//顺序号
			 int num=addBo.getNum(riqi,b0110);
			 int i999=addBo.getCanShuId(b0110);
			 String units = (String)sysbo.getSysValueMap().get("units");//获得一级预算单位编码
			 String[] str=units.split(",");
			 ssl.append("select * from "+tab_name+" where b0110='"+b0110+"' and "+suoYin+"="+budget_id);
			 rows=dao.search(ssl.toString());
			 ro.setString(suoYin, budget_id.toString());
			 ro.setDate(tab_name+"z0", riqi);
			 ro.setDate("createtime", DateStyle.getSystemTime());
			 ro.setString("createusername", this.userView.getUserName());
			 ro.setInt("i9999", i999);//顺序号
			 ro.setString("b0110", b0110);//单位编码 
			 if(!rows.next()){		
				 ro.setInt(tab_name+"z1", num+1);  // 增1
				 dao.addValueObject(ro);
			 }else{
				 ro.setInt(tab_name+"z1", num);
				 dao.updateValueObject(ro);
			 }

			String a="";
			String b="";
			ArrayList filedList=(ArrayList) this.getFormHM().get("fieldList");
			LazyDynaBean bean = new LazyDynaBean();
			String sField="";
            for(int i=0;i<filedList.size();i++)
            {            
                 bean=(LazyDynaBean) filedList.get(i);
                 String itemid =(String) bean.get("itemid");
                 String value=(String) bean.get("value");
                 String itemtype =(String) bean.get("itemtype");
                 if (value!=null){      
                     value=value.trim();
                     if("".equals(value.trim())){
                         value=null;
                     }
                     else{  
                         if ("N".equals(itemtype)){
                             try{
                                 double dValue =Double.parseDouble(value);
                             }
                             catch (Exception e){
                                 value=null; 
                                 e.printStackTrace();
                             }
                             
                         }
                         else {                              
                             value= "'"+value.trim()+"'";
                         }
                     }
                 }
                 
                 if ((value==null) ){value="null";}  
                 if ("".equals(sField)){
                     sField= itemid+"="+value;                       
                 }
                 else {
                     sField= sField+","+itemid+"="+value;                        
                 }          

            }   
			 StringBuffer sql=new StringBuffer();
			 sql.append("update "+tab_name+" set ");
			 sql.append(sField);
			 sql.append( " where b0110 like '"+b0110+"%' and ");
			 sql.append(suoYin+"="+budget_id.toString());
			 dao.update(sql.toString());
			// System.out.println(sql);
//			for(int i=0;i<filedList.size();i++)
//			{	
//				 StringBuffer  sql=new StringBuffer();
//				 bean=(LazyDynaBean) filedList.get(i);
//				 a =(String) bean.get("itemid");
//				 b=(String) bean.get("value");
//				 if(b==""){
//					b=null;
//					 sql.append("update "+tab_name+" set ");
//					 sql.append(a+"=");
//					 sql.append(b+" where b0110 like '"+b0110+"%' and ");
//					 sql.append(suoYin+"="+budget_id.toString());
//				 }else{
//					 sql.append("update "+tab_name+" set ");
//					 sql.append(a+"=");
//					 sql.append("'"+b+"'"+" where b0110 like '"+b0110+"%' and ");
//					 sql.append(suoYin+"="+budget_id.toString());
//				 }
//					dao.update(sql.toString());
//					
//			}
			 String sqll="select count(*) from gz_budget_index";
			 RowSet rs1=dao.search(sqll);
			 int number=0;
			 while(rs1.next()){
				 number=rs1.getInt(1);
			 }
			 StringBuffer  sbl=new StringBuffer();
			 sbl.append("update "+tab_name+" set ");
			 sbl.append(tab_name+"z1"+"='");
			 sbl.append(number+"' where b0110 = '"+b0110+"' and ");
			 sbl.append(suoYin+"="+budget_id.toString());
			 dao.update(sbl.toString());
			} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public String getDate(){
		 ContentDAO dao=new ContentDAO(this.getFrameconn());
		 BudgetAddBo addBo=new BudgetAddBo(this.getFrameconn(), this.userView);//调用新增预算业务类
		 String codeitemid;
		 BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
		 String tabName = ((String)sysbo.getSysValueMap().get("ysze_set")).toLowerCase();
		 String id = ((String)sysbo.getSysValueMap().get("ysze_idx_menu")).toLowerCase();
		 RowSet rs=null;
		 String riqi="";
		try {
			codeitemid = addBo.getCodeitemid();
			BudgetAllocBo allocBo=new BudgetAllocBo(this.getFrameconn(),this.userView);
			Integer budget_id = allocBo.getCurrentBudgetId(); 
			String sql="select "+tabName+"z0" +" from "+tabName+"  where "+ id+"="+budget_id+" and b0110='"+codeitemid+"'";
			try {
				rs=dao.search(sql);
				while(rs.next()){
	                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd"); 
	                if (rs.getDate(tabName+"z0")!=null)
	                  riqi = df.format(rs.getDate(tabName+"z0"));
				//	riqi=rs.getString(tabName+"z0");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw GeneralExceptionHandler.Handle(e);
			}
	
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}//顶级单位

		
		return riqi;
	}

}
	
