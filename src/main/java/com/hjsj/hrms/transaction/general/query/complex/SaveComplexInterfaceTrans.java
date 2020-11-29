package com.hjsj.hrms.transaction.general.query.complex;

import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
/**
 * 保存
 *<p>Title:</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 4, 2008</p> 
 *@author sunxin
 *@version 4.0
 */
public class SaveComplexInterfaceTrans  extends IBusiness {
	
	
	public void execute() throws GeneralException 
	{
		//String id=(String)this.getFormHM().get("complex_id");
		String complex_expr=(String)this.getFormHM().get("complex_expr");
		complex_expr = PubFunc.keyWord_reback(complex_expr);
		complex_expr = PubFunc.reBackWord(complex_expr);
		String complex_name=(String)this.getFormHM().get("complex_name");
		if(complex_expr==null||complex_expr.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","没有定义公式！","",""));
		if(complex_name==null||complex_expr.length()<=0)
			throw GeneralExceptionHandler.Handle(new GeneralException("","定义公式名称不能为空！","",""));
		String info="";
		String ids="";
		try
		{
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			int id=DbNameBo.getPrimaryKey("gwhere","id","",this.getFrameconn());
			RecordVo vo=new RecordVo("gwhere");
			vo.setInt("id", id);
			vo.setString("name", complex_name);	
			vo.setString("lexpr", complex_expr);
			vo.setString("type","1");
		    dao.addValueObject(vo);
		    ids=String.valueOf(id);
			
			/*//计算
			ArrayList alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
			ArrayList dbaselist=userView.getPrivDbList();    
			if(dbaselist==null||dbaselist.size()<=0)
				throw GeneralExceptionHandler.Handle(new GeneralException("","没有授权人员库！","",""));
			String userTableName="";
			String tempTableName="";
			String FSQL="";
			for(int i=0;i<dbaselist.size();i++)
			{
				YksjParser yp = new YksjParser(
		    			getUserView()//Trans交易类子类中可以直接获取userView
		    			,alUsedFields
		    			,YksjParser.forSearch
		    			,YksjParser.LOGIC//此处需要调用者知道该公式的数据类型
		    			,YksjParser.forPerson
		    			,"gw",dbaselist.get(i).toString());
				YearMonthCount ycm = null;	
				yp.run_Where(complex_expr, ycm,"","", dao, "",this.getFrameconn(),"A", null); 
				FSQL=yp.getSQL();
				tempTableName = yp.getTempTableName();
				userTableName=this.userView.getUserName()+dbaselist.get(i).toString()+"Result";
                dao.delete("delete from "+userTableName+"", new ArrayList());
				StringBuffer sql=new StringBuffer();
				sql.append("insert into "+userTableName+"(a0100)");
                sql.append("(select a0100 from " + tempTableName + "");
                sql.append(" where " + yp.getSQL()+")");
                dao.insert(sql.toString(), new ArrayList());                
			}*/
			info="ok";
		}catch(Exception e)
		{
		  e.printStackTrace();	
		  info="xx";
		}
		this.getFormHM().clear();
		this.getFormHM().put("info", info);	
		this.getFormHM().put("id", ids);
	}

}
