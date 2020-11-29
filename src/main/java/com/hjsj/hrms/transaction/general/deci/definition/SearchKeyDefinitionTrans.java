package com.hjsj.hrms.transaction.general.deci.definition;

import com.hjsj.hrms.businessobject.general.deci.definition.StatCutlineBo;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.sys.EncryptLockClient;
import com.hrms.hjsj.sys.VersionControl;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.util.ArrayList;

public class SearchKeyDefinitionTrans extends IBusiness {

	
	public void execute() throws GeneralException {

		String obj=(String)this.getFormHM().get("object");//信息群标识（A/B/K）
		String nam=(String)this.getFormHM().get("nam");//关键指标分类号
	
		StringBuffer stsql=new StringBuffer();

		//获得信息群列表
		ArrayList olist=this.getList1("InformationClass","ClassPre","ClassName");
		//关键指标分类信息
		ArrayList tlist=this.getList("ds_key_factortype","typeid","name");
		
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		ArrayList list=new ArrayList();
		StatCutlineBo statCutlineBo=new StatCutlineBo(this.getFrameconn());
	    try{
	    	if((nam==null|| "".equals(nam)))//信息群标识为空
	    	{ 
	    		//默认为A
	    		stsql.append("select * from ds_key_factor where flag='A' ");
	    		String typeid="0";
	    		if(tlist.size()>0){
	    			CommonData datavo=(CommonData)tlist.get(0);
	    			typeid=datavo.getDataValue();
	    		}
	    		stsql.append(" and typeid='"+typeid+"'");	
	    	}else{
	    	   stsql.append("select * from ds_key_factor where flag='");
	    	   stsql.append(obj);
	    	   stsql.append("' and typeid='");
	    	   stsql.append(nam);
	    	   stsql.append("'");
	    	}
	    	
	        this.frowset = dao.search(stsql.toString());
	        while(this.frowset.next())
	        {
	            RecordVo vo=new RecordVo("ds_key_factor");
	            vo.setString("name",this.getFrowset().getString("name"));//名称
	            String a_formula=this.getFrowset().getString("formula");//计算公式
	            a_formula=getFormula_str(a_formula);
	            vo.setString("formula",a_formula);
	            vo.setString("description",this.getFrowset().getString("description"));//指标解释描述信息
	            vo.setString("standard_value",this.getFrowset().getString("standard_value"));//标准值
	            vo.setString("control_value",this.getFrowset().getString("control_value"));//控制值
	            vo.setString("static_method",this.getFrowset().getString("static_method"));//统计方法
	            vo.setString("field_name",this.getFrowset().getString("field_name"));//统计项目代码型指标代码
	            // vo.setString("field_name",DataDictionary.getFieldItem(this.getFrowset().getString("field_name")).getItemdesc());		            
	           
	            //代码型统计项目代码值（代码串转换为字符串形式描述串）
	            vo.setString("codeitem_value", statCutlineBo.getStatCodeValue(this.getFrowset().getString("codeitem_value"),frowset.getString("field_name")) );
	            vo.setString("factorid",this.getFrowset().getString("factorid"));//指标序号
	            list.add(vo);
	       }

		}catch(Exception sqle){
		    sqle.printStackTrace();
		    throw GeneralExceptionHandler.Handle(sqle);
		}
		    
		//关键指标信息显示列表
	    this.getFormHM().put("factorlist",list);		   
		//信息群列表
	    this.getFormHM().put("olist",olist);
	    //关键指标分类信息列表
		this.getFormHM().put("tlist",tlist);
		
		//this.getFormHM().put("tlist",this.getList("ds_key_factortype","typeid","name"));
		
		if(obj==null|| "".equals(obj)){
			 obj="A";
		}
		this.getFormHM().put("object",obj);//当前信息群标识（A）
		this.getFormHM().put("typeid","");
		this.getFormHM().put("box","");
		this.getFormHM().put("one","");
		this.getFormHM().put("two","");

	}
	
	/**
	 * 得到表的封装数据
	 * @param name 表名称
	 * @param id   标识(字段)
	 * @param expr 描述（字段）
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getList(String name,String id,String expr) throws GeneralException
	{
		StringBuffer st=new StringBuffer();
		ArrayList flist = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		 try
	        {
			    st.append("select * from ");
			    st.append(name);
			    this.frowset = dao.search(st.toString());
			    while(this.frowset.next())
			    {
			    	String classpre = this.frowset.getString(id);
			       CommonData datavo=new CommonData(classpre,this.frowset.getString(expr));
			       flist.add(datavo);
			      }
	          }
	          catch(Exception sqle)
	          {
		         sqle.printStackTrace();
		         throw GeneralExceptionHandler.Handle(sqle);            
	           }
	          
	          return flist;
	}
	/**
	 * 得到表的封装数据
	 * @param name 表名称
	 * @param id   标识(字段)
	 * @param expr 描述（字段）
	 * @return
	 * @throws GeneralException
	 */
	public ArrayList getList1(String name,String id,String expr) throws GeneralException
	{
		StringBuffer st=new StringBuffer();
		ArrayList flist = new ArrayList();
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		EncryptLockClient lockclient = (EncryptLockClient)this.getFormHM().get("lock");
		VersionControl ver_ctrl=new VersionControl();
		 try
	        {
			    st.append("select * from ");
			    st.append(name);
			    st.append(" where "+id+" in('A','B','K')");
			    this.frowset = dao.search(st.toString());
			    while(this.frowset.next())
			    {
			    	String classpre = this.frowset.getString(id);
				    	if("Y".equalsIgnoreCase(classpre)||"V".equalsIgnoreCase(classpre)||"W".equalsIgnoreCase(classpre)){
							if(!lockclient.isHaveBM(31))
								continue;
							if(!ver_ctrl.searchFunctionId("350", userView.hasTheFunction("350")))
								continue;
						}
						if("H".equalsIgnoreCase(classpre)&&!ver_ctrl.searchFunctionId("25012", userView.hasTheFunction("25012")))
							continue;
			       CommonData datavo=new CommonData(classpre,this.frowset.getString(expr));
			       flist.add(datavo);
			      }
	          }
	          catch(Exception sqle)
	          {
		         sqle.printStackTrace();
		         throw GeneralExceptionHandler.Handle(sqle);            
	           }
	          
	          return flist;
	}
	/**
	 * 计算公式代码型转换为字符串描述信息
	 * @param a_formula
	 * @return
	 */
	public String getFormula_str(String a_formula)
	{
		String formula="";
		StringBuffer sql=new StringBuffer("select itemdesc from fielditem where ");
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		if(a_formula.indexOf("/")==-1){
			sql.append(" itemid='"+a_formula+"'");
		}else{
			String[] fields=a_formula.split("/");
			sql.append(" itemid='"+fields[0]+"' union select itemdesc from fielditem where  ");
			sql.append(" itemid='"+fields[1]+"'");
		}
		try
		{
			RowSet a_frowset=dao.search(sql.toString());
			int i=0;
			while(a_frowset.next())
			{
				if(i==0)
					formula=a_frowset.getString("itemdesc");
				if(i==1)
					formula+="/"+a_frowset.getString("itemdesc");
				i++;
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return formula;
	}
	

}
