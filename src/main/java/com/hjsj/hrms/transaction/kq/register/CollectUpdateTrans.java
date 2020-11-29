package com.hjsj.hrms.transaction.kq.register;

import com.hjsj.hrms.businessobject.kq.register.RegisterInitInfoData;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class CollectUpdateTrans extends IBusiness{
	public void execute() throws GeneralException {
		HashMap hm=(HashMap)this.getFormHM();		
		try
		{		
		   ArrayList formslist=(ArrayList)hm.get("forms");
		   String columns=(String)hm.get("columns");//传入的少q03z5
		   String code=(String)hm.get("code");
		   String kind=(String)hm.get("kind");	 
		   
		   String kqItem = (String) this.getFormHM().get("kqitem");
           kqItem = kqItem == null ? "" : kqItem;
		 
		   RegisterInitInfoData initData = new RegisterInitInfoData();
		   
		   String updateSql="";
		   if(columns!=null&&columns.length()>0){
			  //得到update，SQL语句			  
			   ArrayList fielditemlist=DataDictionary.getFieldList("Q03", Constant.USED_FIELD_SET);
			   fielditemlist = RegisterInitInfoData.newFieldItemList(fielditemlist,this.userView,this.frameconn);
			   
			   RegisterInitInfoData registerInitInfoData = new RegisterInitInfoData();
			   if(kqItem.indexOf("isok") != -1){
				   fielditemlist = registerInitInfoData.getNewItemList(fielditemlist, "", this.frameconn);		
			   }else{
				   fielditemlist = registerInitInfoData.getNewItemList(fielditemlist, kqItem, this.frameconn);		
			   }
			   
			   //月汇总 保存数据过滤掉主集中的指标
			   updateSql=updateSql(fielditemlist);	
			   formslist = initData.updateFormslist(formslist, fielditemlist, columns);
		      //处理前台传过来的数据，改变为符合上面update的元素的list
		      ArrayList recordlist = initData.getFormsList(formslist,fielditemlist,this.getFrameconn());
		      
		      //判断是否可以起草保存
		      ArrayList newList =opUpdate(recordlist,code,kind);
		      if(newList!=null&&newList.size()>0){
		        ContentDAO dao = new ContentDAO(this.getFrameconn());			       
                dao.batchUpdate(updateSql,newList);
                this.getFormHM().put("type","success");
		      }else{
		    	this.getFormHM().put("type","nosave");
		      }
		   }else{
			   this.getFormHM().put("type","lost");
		   }		  
		}
		catch(Exception e){
	       e.printStackTrace();
	       throw GeneralExceptionHandler.Handle(new GeneralException("",ResourceFactory.getProperty("kq.register.error.number"),"",""));    
		}

	}	
	public String updateSql(ArrayList fielditemlist)
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("update Q05 set ");
    	for(int r=0;r<fielditemlist.size();r++)
		{
			FieldItem fielditem=(FieldItem)fielditemlist.get(r);
			//判断Q03中那些指标是从A01主集中取得的，过滤出去；A01主集导入到Q03的信息不能
			boolean booindex=getindexA01(fielditem.getItemtype(),fielditem.getItemid(),fielditem.getItemdesc());
			if(booindex)
			{
				if("A".equals(fielditem.getItemtype())|| "N".equals(fielditem.getItemtype())|| "D".equals(fielditem.getItemtype()))
				{
					if(!"state".equals(fielditem.getItemid()))
					{
						if(!"nbase".equals(fielditem.getItemid())&&!"a0100".equals(fielditem.getItemid())&&!"q03z0".equals(fielditem.getItemid()))
						{
							sql.append(fielditem.getItemid()+"=?,");
							
						}
					}
				}
			}
		} 
    	sql.setLength(sql.length()-1);
    	sql.append(" where nbase=? and a0100=? and q03z0=?");
    	return sql.toString();
    }
	/* 判断保存数据是否可以更改
	 * @param recordlist,
	 *       传递进来的处理后的多条数据记录
	 * @return 
	 *       返回验证过的List
	 * */
    public ArrayList opUpdate(ArrayList recordlist,String code,String kind){
    	
    	//如果该纪录已经提交则跳过
    	ArrayList newlist=new ArrayList();
    	for(int r=0;r<recordlist.size();r++)
    	{
    	   ArrayList record=(ArrayList)recordlist.get(r);
    	   StringBuffer sql=new StringBuffer();
    	   int i=record.size();
    	   sql.append("select Q03Z5 from Q05 where ");
    	   sql.append(" nbase='"+record.get(i-3).toString()+"'");
    	   sql.append(" and a0100='"+record.get(i-2).toString()+"'");
    	   String date=record.get(i-1).toString();    	   
    	   sql.append(" and Q03Z0 = '"+date+"'");
           if("1".equals(kind))
		   {
        	  sql.append(" and e0122 like '"+code+"%'");
		   }else if("0".equals(kind))
		   {
			   sql.append("and e01a1 like '"+code+"%'");
		   }else{
			  sql.append(" and b0110 like '"+code+"%'");	
		   }          
           ContentDAO dao = new ContentDAO(this.getFrameconn());
           try{
            this.frowset = dao.search(sql.toString());
            if(this.frowset.next()){
        	   String checkflag= (String)this.frowset.getString("Q03Z5");
        	   if("01".equals(checkflag)||"07".equals(checkflag)){  
        	     newlist.add(record);
        	   }
        	   if(checkflag==null){
        		   newlist.add("01");
        	   }
             }
           }catch(Exception e){
        	  e.printStackTrace();
           }
    	} 
        return newlist;
    }
    /**
     * 断Q03中那些指标是从A01主集中取得的
     * @param itemtype
     * @param itemid
     * @param itemdesc
     * @return
     */
    public boolean getindexA01(String itemtype,String itemid,String itemdesc)
    {
    	boolean field=true;
    	itemtype=itemtype.toUpperCase();
    	itemid=itemid.toUpperCase();
    	ContentDAO dao = new ContentDAO(this.getFrameconn());
    	RowSet rs=null;
    	String sql="select itemid from fielditem where fieldsetid='A01' and itemid='"+itemid+"' and itemtype='"+itemtype+"' and itemdesc='"+itemdesc+"'";
    	try
    	{
    		rs=dao.search(sql.toString());
    		while(rs.next())
    		{
    			String itemi = rs.getString("itemid");
    			if(!"A0101".equals(itemi)&&!"E0122".equals(itemi))
    			{
    				if(itemi!=null&&itemi.length()>0)
        			{
        				field=false;
        			}
    			}
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		if(rs!=null)
    		{
    			try
    			{
    				rs.close();
    			}catch(SQLException e)
    			{
    				e.printStackTrace();
    			}
    		}
    	}
    	return field;
    }
}
