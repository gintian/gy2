package com.hjsj.hrms.transaction.general.kanban;

import com.hjsj.hrms.businessobject.info.InfoUtils;
import com.hjsj.hrms.businessobject.sys.DbNameBo;
import com.hjsj.hrms.interfaces.analyse.IParserConstant;
import com.hjsj.hrms.utils.analyse.YearMonthCount;
import com.hjsj.hrms.utils.analyse.YksjParser;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import com.hrms.struts.valueobject.UserView;

import javax.sql.RowSet;
import java.sql.Connection;
import java.util.ArrayList;

public class SearchA0101Trans extends IBusiness {
	/*
	 *  //wlh修改添加检索过滤
	 */
	private String getFilterSQL(UserView uv,String BasePre,ArrayList alUsedFields,Connection conn1,String filter_factor)
	{
		String sql=" (1=1)";
		try{
			filter_factor=filter_factor.replaceAll("@","\"");
			ContentDAO dao=new ContentDAO(conn1);
			//this.filterfactor="性别 <> '1'";
			int infoGroup = 0; // forPerson 人员
			int varType = 8; // logic								
			String whereIN=InfoUtils.getWhereINSql(uv,BasePre);
			whereIN="select a0100 "+whereIN;							
			YksjParser yp = new YksjParser( uv ,alUsedFields,
					YksjParser.forSearch, varType, infoGroup, "Ht", BasePre);
			YearMonthCount ymc=null;							
			yp.run_Where(filter_factor, ymc,"","hrpwarn_resulta", dao, whereIN,conn1,"A", null);
			String tempTableName = yp.getTempTableName();
//			System.out.println("-->="+tempTableName);
			sql=yp.getSQL();
		}catch(Exception e)
		{
			e.printStackTrace();
		}	
		return sql;
	}
	private String getQueryString(String a0101,ArrayList dblist,String filter_factor)throws GeneralException
	{
		StringBuffer buf=new StringBuffer();
    	ArrayList fieldlist=new ArrayList();
		String strWhere=null;    
		String sexpr="1";
		String sfactor="A0101="+a0101+"*`";
		/**加权限过滤*/
		ArrayList alUsedFields=null;
		if(filter_factor!=null && !"undefined".equalsIgnoreCase(filter_factor)&& !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
	    { 
		  alUsedFields = DataDictionary.getAllFieldItemList(Constant.USED_FIELD_SET, Constant.ALL_FIELD_SET);
		  /**
		   *保持和以前的程序兼容，因为先前单位编码和职位编码、
		   *单位名称和职位名称未统一起来 
		   */
		  FieldItem item=new FieldItem();
		  item.setItemid("b0110");
		  item.setCodesetid("UN");
		  item.setItemdesc("单位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);

		  item=new FieldItem();
		  item.setItemid("e01a1");
		  item.setCodesetid("@K");
		  item.setItemdesc("职位编码");
		  item.setItemtype("A");
		  item.setFieldsetid("A01");
		  item.setUseflag("2");
		  item.setItemlength(30);
		  item.setItemlength(30);
		  alUsedFields.add(item);
		  
	    }
		DbNameBo dbnamebo=new DbNameBo(this.getFrameconn());
  	    for(int i=0;i<dblist.size();i++)
 	    {
  	    	RecordVo vo=(RecordVo)dblist.get(i);
  	    	String pre=vo.getString("pre");
//    		strWhere=userView.getPrivSQLExpression(sexpr+"|"+sfactor,pre,false,fieldlist);
  	    	
  	    	buf.append("select a0000, a0101,b0110,a0100,e0122, '");
  	    	buf.append(vo.getString("pre"));
  	    	buf.append("' as dbpre  ");
  	    	buf.append(" from ");
  	    	buf.append(vo.getString("pre"));
  	    	buf.append("A01");
  	    	buf.append(" where a0101 like '");
  	    	buf.append(a0101);
  	    	buf.append("%' ");
//  	    	buf.append(strWhere);  
  	    	//wlh修改添加检索过滤
     	    if(filter_factor!=null && !"null".equalsIgnoreCase(filter_factor) &&  filter_factor.trim().length()>0)
			{ 
     	    	buf.append(" and " + dbnamebo.getComplexCond(this.userView, pre, alUsedFields, filter_factor, IParserConstant.forPerson)/*getFilterSQL(userView,vo.getString("pre"),alUsedFields,this.getFrameconn(),filter_factor)*/);
			}
  	    	buf.append(" UNION ");
 	    }
  	    buf.setLength(buf.length()-7);
  	    buf.append(" order by dbpre desc,a0000");
		return buf.toString();
	}
	
	public void execute() throws GeneralException {
		String a0101=(String)this.getFormHM().get("a0101");
		a0101=SafeCode.decode(a0101);
		String filter_factor=(String)this.getFormHM().get("filter_factor");
		DbNameBo dbbo=new DbNameBo(this.getFrameconn());
		ArrayList dblist=dbbo.getAllDbNameVoList(this.userView);
		ContentDAO dao=new ContentDAO(this.getFrameconn());
		try
		{
			int i=0;
			ArrayList objlist=new ArrayList();
			if(!(a0101==null|| "".equalsIgnoreCase(a0101)))
			{
				RowSet rset=dao.search(getQueryString(a0101,dblist,filter_factor));			
				while(rset.next())
				{
					if(i>40)
						break;
					CommonData objvo=new CommonData();
					String b0110=rset.getString("e0122");
					String name=AdminCode.getCodeName("UM",b0110);
					objvo.setDataName(name+"("+rset.getString("a0101")+")");
					objvo.setDataValue(rset.getString("dbpre")+"::"+rset.getString("a0100")+"::"+rset.getString("a0101"));
					objlist.add(objvo);
					++i;
				}
			}
			this.getFormHM().put("objlist",objlist);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}

}
