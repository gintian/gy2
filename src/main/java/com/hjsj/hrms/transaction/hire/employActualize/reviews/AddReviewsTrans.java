package com.hjsj.hrms.transaction.hire.employActualize.reviews;

import com.hjsj.hrms.businessobject.hire.ParameterXMLBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.text.SimpleDateFormat;
import java.util.*;

public class AddReviewsTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			String person_type=(String)this.getFormHM().get("person_type");
			String a0100=(String)this.getFormHM().get("a0100");
			//a0100 = com.hjsj.hrms.utils.PubFunc.decrypt(a0100); 增加评语时不应该在这里解密,因为有可能是同时对多个人员增加评语
			a0100 = a0100.replaceAll("＃", "#");
			String level=(String)this.getFormHM().get("level");
			String content=(String)this.getFormHM().get("content");
			String title=(String)this.getFormHM().get("title");
			String infoid=(String)this.getFormHM().get("info_id");
			ArrayList list=new ArrayList();
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			StringBuffer whl=new StringBuffer("");
			if(a0100.indexOf("#")==-1)
			{
				//这里表示只有一个a0100进入增加评语的状态，为了和多个的保持一直处于加密的状态,这里采用不解密的状态
				String tempA0100 = PubFunc.decrypt(a0100);
				whl.append(",'"+tempA0100+"'");
				list.add(tempA0100);
			}
			else
			{
				String[] temps=a0100.split("#");
				for(int i=0;i<temps.length;i++)
				{
					if(temps[i].length()>0)
					{
						//这里表示同时对多个人增加评语
						String tempA0100 = PubFunc.decrypt(temps[i]);
						whl.append(",'"+tempA0100+"'");
						list.add(tempA0100);
					}
				}
			}
			if(infoid!=null&&infoid.trim().length()>0)
			{
				RecordVo vo=new RecordVo("zp_comment_info");
				vo.setString("info_id", infoid);
				vo=dao.findByPrimaryKey(vo);
				vo.setString("title", title);
				vo.setString("content", content);
				if(vo.hasAttribute("level"))
					vo.setString("level",level);
				if(vo.hasAttribute("level_o"))
					vo.setString("level_o", level);
				dao.updateValueObject(vo);
			}
			else
			{
		    	dao.delete("delete from zp_comment_info where a0100 in ("+whl.substring(1)+") and comment_user='"+this.getUserView().getUserFullName()+"'",new ArrayList());
		    	IDGenerator idg = new IDGenerator(2, this.getFrameconn());
		    	Calendar d=Calendar.getInstance();
		     	ArrayList voList=new ArrayList();
		    	for(Iterator t=list.iterator();t.hasNext();)
		    	{
			    	String a_a0100=(String)t.next();
			    	RecordVo vo=new RecordVo("zp_comment_info");
			    	String info_id = idg.getId("zp_comment_info.info_id");
			     	vo.setString("info_id",info_id);
				    vo.setString("a0100",a_a0100);
			    	vo.setString("title",title);
				    vo.setString("content",content);
			    	if(vo.hasAttribute("level"))	
					    	vo.setString("level",level);
				    	else if(vo.hasAttribute("level_o"))
				    		vo.setString("level_o",level);
			     	vo.setDate("comment_date",d.getTime());
			    	vo.setString("comment_user",this.getUserView().getUserFullName());
			    	voList.add(vo);
		    	}
		     	dao.addValueObject(voList);
			}
			ParameterXMLBo parameterXMLBo=new ParameterXMLBo(this.getFrameconn());
			HashMap map=parameterXMLBo.getAttributeValues();
			String isRemenberExamine="0";
			if(map!=null&&map.get("isRemenberExamine")!=null)
			{
				isRemenberExamine=(String)map.get("isRemenberExamine");
			}
			 String remenberExamineSet="";
	    		if(map!=null&&map.get("remenberExamineSet")!=null)
	    		{
		    		remenberExamineSet=(String)map.get("remenberExamineSet");
	    		}
			if("1".equals(isRemenberExamine)&&remenberExamineSet!=null&&remenberExamineSet.trim().length()>0)
			{
	    		
	     		String titleF="";
	    		String contentF="";
		     	String commentuserF="";
		    	String levelF="";
		    	String commentdateF="";
		    	HashMap infoMap=null;
		    	if(map!=null)
		    	{
		    		infoMap=(HashMap)map.get("infoMap");
		    		if(infoMap!=null&&infoMap.get("title")!=null)
		    			titleF=(String)infoMap.get("title");
			    	if(infoMap!=null&&infoMap.get("content")!=null)
			    		contentF=(String)infoMap.get("content");
		    		if(infoMap!=null&&infoMap.get("level")!=null)
		    			levelF=(String)infoMap.get("level");
		    		if(infoMap!=null&&infoMap.get("comment_user")!=null)
		     			commentuserF=(String)infoMap.get("comment_user");
		    		if(infoMap!=null&&infoMap.get("comment_date")!=null)
		     			commentdateF=(String)infoMap.get("comment_date");
		     	}
		    	RecordVo vo=ConstantParamter.getConstantVo("ZP_DBNAME");
	    		String dbname="";
		    	if(vo!=null)
		    		dbname=vo.getString("str_value");
		    	else
		    		throw GeneralExceptionHandler.Handle(new Exception("请在参数设置中配置招聘人才库！"));
	    		HashMap i999Map = this.getI9999(a0100, dbname+remenberExamineSet);
	    		
	    		StringBuffer buf = new StringBuffer("");
	    		HashSet hskey=(HashSet)i999Map.get("sameKey");
	    		ArrayList alist = new ArrayList();
	    		HashMap kemap=new HashMap();
	    		boolean flag=false;
	    		int count=0;
	    		String scount="";
	    		HashMap countmap=new HashMap();
	    		for(Iterator t=list.iterator();t.hasNext();)
	    		{	
	    			/**dumeilong*/
	    			String i999="";
	    			
		    		String a_a0100=(String)t.next();
		    		
		    		if(hskey.contains(a_a0100)){
		    		
		    			if(countmap.get(a_a0100)!=null){
		    				flag=true;
		    				int count1=Integer.parseInt((String)countmap.get(a_a0100));
		    				countmap.remove(a_a0100);
		    				countmap.put(a_a0100, String.valueOf(count1+1));
		    			}else{
		    				flag=false;
		    				countmap.put(a_a0100, "0");
		    			}
		    		}
		    		if(countmap.get(a_a0100)!=null)
		    			count=Integer.parseInt((String)countmap.get(a_a0100));
		    		/***/
		    		buf.setLength(0);
		    		alist.clear();
		    		buf.append("insert into "+dbname+remenberExamineSet+" (a0100,i9999,");
		    		buf.append(titleF);
		    		if(level==null|| "".equals(level.trim()))
		    		{
		    			
		    		}
		    		else
		    		{
		    	    	buf.append(","+levelF);
		    		}
		    		buf.append(","+commentuserF+","+commentdateF);
		    		buf.append(",createtime,modtime,createusername,modusername) values (");
		    		/**dumeilong*/
		    		if(flag&&count==0){
		    			buf.append("'"+a_a0100+"',"+(String)i999Map.get(a_a0100)+",'"+title+"'");
		    		}else{
		    			if(flag&&count>=1){
			    			i999=String.valueOf(Integer.parseInt((String)i999Map.get(a_a0100))+count);
			    			buf.append("'"+a_a0100+"',"+i999+",'"+title+"'");
		    			}
		    			if(!flag){
		    				buf.append("'"+a_a0100+"',"+(String)i999Map.get(a_a0100)+",'"+title+"'");
		    			}
		    		}
		    		/***/
		    		
		    		if(level==null|| "".equals(level.trim()))
		    		{
		    			
		    		}
		    		else
		    		{
		    	    	buf.append(",'"+level+"'");
		    		}
		    		buf.append(",'"+this.getUserView().getUserFullName()+"',");
		    		if(Sql_switcher.searchDbServer()==Constant.ORACEL)
		    		{
		    			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		    			buf.append("to_date('"+format.format(new java.util.Date())+"','yyyy-mm-dd'),");
		    			//yyyy/MM/dd HH:mm:ss  日期格式的问题 月是MM 小时是大写的HH 分钟是mm 秒ss 毫秒是大写的 SS 其余的大小写不区分
		    			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			buf.append("to_date('"+format.format(new java.util.Date())+"','yyyy-MM-dd HH24:MI:ss'),");//防止格式化代码出现两次 增加24是为了防止数值必须位于1~12之间的错误
		    			buf.append("to_date('"+format.format(new java.util.Date())+"','yyyy-MM-dd HH24:MI:ss'),");//防止格式化代码出现两次 增加24是为了防止数值必须位于1~12之间的错误
		    		}
		    		else
		    		{
		    			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		    			buf.append("'"+format.format(new java.util.Date())+"',");
		    			format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    			buf.append("'"+format.format(new java.util.Date())+"',");
		    			buf.append("'"+format.format(new java.util.Date())+"',");
		    		}
		    		buf.append("'"+this.getUserView().getUserName()+"','"+this.getUserView().getUserFullName()+"')");
		    		dao.insert(buf.toString(), alist);
		    		buf.setLength(0);
		    		alist.add(content);
		    		buf.append(" update "+dbname+remenberExamineSet+" set "+contentF+"=? where ");
		    		/**dumeilong*/
		    		if(flag&&count==0){
		    			buf.append("a0100='"+a_a0100+"' and i9999="+(String)i999Map.get(a_a0100));
		    		}else{
		    			if(flag&&count>=1){
		    				buf.append("a0100='"+a_a0100+"' and i9999="+i999);
		    			}
		    			if(!flag){
		    				buf.append("a0100='"+a_a0100+"' and i9999="+(String)i999Map.get(a_a0100));
		    			}
		    			
		    		}
		    		/***/
		    		
		    		dao.update(buf.toString(), alist);
	     	 	}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	public HashMap getI9999(String a0100,String tablename)
	{
		HashMap map = new HashMap();
		try
		{
			String[] arr = a0100.split("#");
			StringBuffer buf = new StringBuffer();
			int j=0;
			String a1000="";
			HashSet same=new HashSet();
			for(int i=0;i<arr.length;i++)
			{
				if(arr[i]==null|| "".equals(arr[i]))
					continue;
				if(j!=0)
					buf.append(" union ");
				buf.append("select max(a0100) as a0100,max(i9999) as i9999 from "+tablename);
				//这里增加评语时a0100是加密的,解密回来
				String tempA0100 = PubFunc.decrypt(arr[i]);
				buf.append(" where a0100='"+tempA0100+"'");
				j++;
				if(a1000.indexOf(tempA0100)==-1){//dumeilong
					a1000+=tempA0100;
				}else{
					same.add(tempA0100);
				}
				
			}
			StringBuffer sql = new StringBuffer();
			sql.append(" select * from ("+buf.toString()+") T");
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			this.frowset=dao.search(sql.toString());
			while(this.frowset.next())
			{
				if(this.frowset.getString("a0100")==null)
					continue;
				map.put(this.frowset.getString("a0100"),(this.frowset.getInt("i9999")+1)+"");
			}
			for(int i=0;i<arr.length;i++)
			{
				String tempA0100 = PubFunc.decrypt(arr[i]);
				if(tempA0100==null|| "".equals(tempA0100))
					continue;
				if(map.containsKey(tempA0100))
					continue;
				else
				{
					map.put(tempA0100,"1");
				}
			}
			map.put("sameKey", same);//dumei
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
	}

}
