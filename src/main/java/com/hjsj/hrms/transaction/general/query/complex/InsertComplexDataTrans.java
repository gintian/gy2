package com.hjsj.hrms.transaction.general.query.complex;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.options.SaveInfo_paramXml;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.ArrayList;

public class InsertComplexDataTrans  extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		String complex_expr=(String)this.getFormHM().get("complex_expr");
		String comple_db=(String)this.getFormHM().get("comple_db");
		String fromFlag=(String)this.getFormHM().get("fromFlag");
		if(complex_expr==null||complex_expr.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义公式！","",""));
		try
		{
			complex_expr=PubFunc.keyWord_reback(complex_expr);
			ContentDAO dao=new ContentDAO(this.getFrameconn());			
			//计算
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			InfoUtils infoUtils=new InfoUtils();
			alUsedFields.addAll(infoUtils.getMidVariableList("3", "0", this.getFrameconn()));
			ArrayList dbaselist=userView.getPrivDbList();    
			if(dbaselist==null||dbaselist.size()<=0)
				throw GeneralExceptionHandler.Handle(new GeneralException("","没有授权人员库！","",""));
			if(comple_db == null || "".equals(comple_db) || "ALL".equals(comple_db)){ //全部用户库数据(对人员要加权限)
				dbaselist=userView.getPrivDbList();  
			}else
			{
				dbaselist=new ArrayList();
				dbaselist.add(comple_db);
			}
			String userTableName="";
			String tempTableName="";
			String FSQL="";
			complex_expr=PubFunc.getStr(complex_expr);
			String privCodeValue = "";
			String privCode = "";
            if(!this.userView.isSuper_admin()) {
            	privCodeValue = this.userView.getManagePrivCodeValue();
            	privCode = this.userView.getManagePrivCode();
            }
            
            String kind = "2";
            if("UM".equalsIgnoreCase(privCode))
            	kind = "1";
            else if("@k".equalsIgnoreCase(privCode))
            	kind = "0";
	         
			for(int i=0;i<dbaselist.size();i++)
			{
				String whereA0100In=infoUtils.getWhereSQLExists(this.frameconn,this.userView,dbaselist.get(i).toString(),privCodeValue,true,kind,"org","","All");
				YksjParser yp = new YksjParser(
		    			getUserView()//Trans交易类子类中可以直接获取userView
		    			,alUsedFields
		    			,YksjParser.forSearch
		    			,YksjParser.LOGIC//此处需要调用者知道该公式的数据类型
		    			,YksjParser.forPerson
		    			,"gw",dbaselist.get(i).toString());
				YearMonthCount ycm = null;	
				yp.setSupportVar(true);  //支持临时变量
		//		complex_expr="姓名<>\"\" 且 性别=\"1\"";
				yp.run_Where(complex_expr, ycm,"","", dao, "",this.getFrameconn(),"A", null); 
				FSQL=yp.getSQL();
				tempTableName = yp.getTempTableName();
				StringBuffer sql=new StringBuffer();
				if(this.userView.getStatus()==4)
				{
					String tabldName = "t_sys_result";
					Table table = new Table(tabldName);
					DbWizard dbWizard = new DbWizard(this.getFrameconn());
					if (dbWizard.isExistTable(table)) {
						String str = "delete from " + tabldName+" where flag=0 and UPPER(username)='"+userView.getUserName().toUpperCase()+"'";
						str+=" and UPPER(nbase)='"+dbaselist.get(i).toString().toUpperCase()+"'";
					    dao.delete(str, new ArrayList());
					    sql.append("insert into " + tabldName);
					    sql.append("(username,nbase,obj_id,flag) ");
					    sql.append("select '"+userView.getUserName()+"' as username,'"+dbaselist.get(i).toString()+"' as nbase,A0100 as obj_id, 0 as flag");
					    sql.append(" from "+tempTableName+" where "+yp.getSQL());
					    if(!this.userView.isSuper_admin())
					    	sql.append(" and   a0100 in(select "+dbaselist.get(i).toString() + "a01.a0100 from ("+whereA0100In+") " + dbaselist.get(i).toString() + "a01 )");
					}
				}
				else
				{
		     		userTableName=this.userView.getUserName()+dbaselist.get(i).toString()+"Result";
                    dao.delete("delete from "+userTableName+"", new ArrayList());
			    	
		    		sql.append("insert into "+userTableName+"(a0100)");
                    sql.append("(select a0100 from " + tempTableName + "");
                    sql.append(" where (" + yp.getSQL()+") ");//zgd 2013-12-25 添加括号，将sql中的OR顺序正确排列，防止与下面的and顺序搞混。
                    if(!this.userView.isSuper_admin())
                    	sql.append(" and   a0100 in(select "+dbaselist.get(i).toString() + "a01.a0100  from ("+whereA0100In+") " + dbaselist.get(i).toString() + "a01 )");
                    sql.append(")");
				}
                dao.insert(sql.toString(), new ArrayList());                
			}
		}catch(Exception e)
		{
		  e.printStackTrace();	
		}
		String fieldstr=new SaveInfo_paramXml(this.getFrameconn()).getInfo_paramNode("browser");
		if(fieldstr!=null&&fieldstr.length()>0){
		
		}else{
			fieldstr=",B0110,E0122,E01A1,A0101,UserName";
			
		}
		ArrayList infoFieldList=new ArrayList();
		infoFieldList=userView.getPrivFieldList("A01");   //获得当前子集的所有属性
		ArrayList fields=new ArrayList();
//
		String[] f=fieldstr.split(",");
		for(int i=0;i<f.length;i++){
			for(int j=0;j<infoFieldList.size();j++){
				FieldItem fieldItem_O=(FieldItem)infoFieldList.get(j);
				FieldItem fieldItem=(FieldItem)fieldItem_O.clone();
				fieldItem.setDisplaywidth(fieldItem.getDisplaywidth()*12);
					
				if(fieldItem.getPriv_status() !=0)                //只加在有读写权限的指标
				{
					if(f[i].equalsIgnoreCase(fieldItem.getItemid()))
					{
						fields.add(fieldItem);
					}
				}
			}
		}
		this.getFormHM().put("browsefields",fields);		
		this.getFormHM().put("fieldstr", fieldstr);
		this.getFormHM().put("comple_db", comple_db);
		this.getFormHM().put("dbpre", "");
		this.getFormHM().put("complex_expr", complex_expr);
	}

}
