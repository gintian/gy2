package com.hjsj.hrms.transaction.gz.gz_budget.budget_allocation;

import com.hjsj.hrms.businessobject.gz.gz_budget.budget_add.BudgetAddBo;
import com.hjsj.hrms.businessobject.gz.gz_budget.budget_rule.options.BudgetSysBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.DateStyle;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;

import javax.sql.RowSet;
import java.util.ArrayList;
/**
 * 
 *  保存预算交易类
 * <p>Title:SaveBudgetTrans.java</p>
 * <p>Description>:</p>
 * <p>Company:HJSJ</p>
 * <p>Create Time:Oct 24, 2012 5:12:34 PM</p>
 * <p>@version: 5.0</p>
 * 
 */
public class SaveBudgetTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		
		try{
			 ContentDAO dao=new ContentDAO(this.getFrameconn());
			 LazyDynaBean codesetbean = (LazyDynaBean) this.getFormHM().get("vo");//获得前台页面传来的对象
			 String yearnum=(String) codesetbean.get("yearnum");//获得前台弹出页面传来的值
			 String budgettype=(String) codesetbean.get("budgettype");
			 String firstmonth=(String) codesetbean.get("firstmonth");
			 String bb203=(String) codesetbean.get("bb203");//预算总额
			 BudgetSysBo sysbo=new BudgetSysBo(this.getFrameconn(),this.userView);
			 String tabName = ((String)sysbo.getSysValueMap().get("ysze_set")).toLowerCase();//获得预算总额子集表名
			 String suoYin = ((String)sysbo.getSysValueMap().get("ysze_idx_menu")).toLowerCase();//获得预算总额的索引指标
			 String zongE = ((String)sysbo.getSysValueMap().get("ysze_ze_menu")).toLowerCase();//获得预算总额指标
			 String zhuangTai= ((String)sysbo.getSysValueMap().get("ysze_status_menu")).toLowerCase();//获得预算状态指标
			 String units = (String)sysbo.getSysValueMap().get("units");//获得一级预算单位编码

			 BudgetAddBo addBo=new BudgetAddBo(this.getFrameconn(), this.userView);//调用新增预算业务类
			 String codeitemid=addBo.getCodeitemid();//*获得顶级单位编码
//		     String unitname=this.getUnitname();//获得一级预算单位名称列
			 RecordVo vo=new RecordVo("gz_budget_index");//索引表对象
			 RecordVo ro=new RecordVo(tabName);//预算总额表对象
			 String sqll="select count(*) from gz_budget_index";
			 RowSet rs1=dao.search(sqll);
			 int number=0;
			 while(rs1.next()){
				 number=rs1.getInt(1);
			 }

			 int i9999=addBo.getI9999(this.getFormHM().get("vo"));
			 IDGenerator idg=new IDGenerator(2,this.getFrameconn());
	         String id=idg.getId("gz_budget_index.budget_id");
	         vo.setString("budget_id",id);
	         vo.setInt("i9999", i9999+1);
	         vo.setString("spflag", "01");
			 vo.setString("yearnum", yearnum);
			 vo.setString("budgettype", budgettype);
			 vo.setString("firstmonth", firstmonth);	
			 vo.setDate("adjustdate", DateStyle.getSystemTime());
			 vo.setString("curuser", this.userView.getUserName());
			 vo.setString("extattr", units);
			 dao.addValueObject(vo);
			 
	            //最近的一次预算Id 
             String oldLatelyId="0";
             sqll="select budget_id from gz_budget_index where budgetType in (1,2,3) " 
                    + " and budget_id <>" +String.valueOf(id)
                    +" and ((yearnum <"+yearnum+") or (yearnum = "+yearnum+" and firstmonth<="+firstmonth+" ) )"
                    +" order by yearNum desc,I9999 desc";
             rs1=dao.search(sqll);
             if (rs1.next()){
                 oldLatelyId=String.valueOf(rs1.getInt(1));
             }
             
             String[] str=(codeitemid+","+units).split(",");
             /* wangrd 屏蔽 2013-12-12
			 int idd=addBo.getId(codeitemid);
			 ro.setString(suoYin, id);
			 ro.setString(zongE, bb203);
			 ro.setString(tabName+"z0", yearnum+"-"+firstmonth+"-01"+"  00:00:00");
			 ro.setInt(tabName+"z1", number+1);
			 ro.setString(zhuangTai, "01");
			 ro.setDate("createtime", DateStyle.getSystemTime());
			 ro.setString("createusername", this.userView.getUserName());
			 
			 ro.setInt("i9999", idd);//顺序号
			 ro.setString("b0110", codeitemid);//单位编码 ?????????????????
			 dao.addValueObject(ro);		 
			 */
             //写入总额记录
			for(int i=0;i<str.length;i++){				 
				 String B0110=str[i];
				 if ("".equals(B0110)) continue;
				 if (",".equals(B0110)) continue;
				 
				 ro=new RecordVo(tabName);
				 int iid=addBo.getId(B0110);
            	 /* 薪资预算-新建预算，报文字与格式字符串不匹配 xiaoyun 2014-10-27 start */
				 //ro.setString(tabName+"z0", yearnum+"-"+firstmonth+"-01"+"  00:00:00");
				 ro.setDate(tabName+"z0", yearnum+"-"+firstmonth+"-01"+"  00:00:00");
             	 /* 薪资预算-新建预算，报文字与格式字符串不匹配 xiaoyun 2014-10-27 end */

				 ro.setInt(tabName+"z1", number+1);
				 ro.setString(suoYin, Integer.parseInt(id)+"");
				 ro.setString(zhuangTai, "01");
				 ro.setDate("createtime", DateStyle.getSystemTime());
				 ro.setString("createusername", this.userView.getUserName());
				 
				 ro.setInt("i9999", iid);
				 ro.setString("b0110", B0110);//单位编码  
                 if (codeitemid.equals(B0110)) //顶级节点    
                     ro.setString(zongE, bb203);
				 dao.addValueObject(ro);
			}
			//写入预算参数记录，只处理最高节点 继承上次预算的参数		
	        String paramset =  ((String)sysbo.getSysValueMap().get("ysparam_set")).toLowerCase();
	        String paramIndexMenu =  ((String)sysbo.getSysValueMap().get("ysparam_idx_menu")).toLowerCase();
	        String paramz0 = paramset+"Z0";
	        String paramz1 = paramset+"Z1";	
	        String extflds="";
	        ArrayList list=DataDictionary.getFieldList(paramset,Constant.USED_FIELD_SET);
	        FieldItem f=null;
	        for(int i=0;i<list.size();i++) {
	            f=(FieldItem)list.get(i);
	            if (paramz0.equalsIgnoreCase(f.getItemid())) continue;
	            if (paramz1.equalsIgnoreCase(f.getItemid())) continue;
	            if (paramIndexMenu.equalsIgnoreCase(f.getItemid())) continue;	
	            if (extflds.length()!=0)
	                extflds+=",";
	            extflds += f.getItemid();
	        } 
	        String B0110=codeitemid;
	        if ((firstmonth.length()<2)&& (Integer.parseInt(firstmonth)<10)) 
	            firstmonth ="0"+firstmonth;
            String z0=yearnum+"-"+firstmonth+"-01"+"  00:00:00";
            String z1=String.valueOf(number+1);
            String I9999= String.valueOf(addBo.getCanShuId (B0110));
            String sql="insert into "+paramset
                +"(B0110,I9999,"+paramz0+","+paramz1+","+paramIndexMenu
                +",CreateUserName,CreateTime,"+extflds+") " 
                +" select '"+B0110+"',"+I9999+","
                +Sql_switcher.dateValue(z0)+","+z1+","+String.valueOf(id)+","
                +"'"+userView.getUserName()+"',"+Sql_switcher.sqlNow()+","+extflds
                + " from "+paramset
                + " where "+paramIndexMenu + "="+oldLatelyId+" and B0110 = '" + B0110 +"'"
                + " and i9999="
                + "(select max(i9999) from " + paramset
                + " where "+paramIndexMenu + "="+oldLatelyId+" and B0110 = '" + B0110 +"')";                 
           dao.update(sql);
      
			
		}catch(Exception e){
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}
	

	}
	
	
	
	

}
