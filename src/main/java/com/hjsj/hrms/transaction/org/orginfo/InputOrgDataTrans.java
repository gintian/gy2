package com.hjsj.hrms.transaction.org.orginfo;

import com.hjsj.hrms.businessobject.kq.KqUtilsClass;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.CodeItem;
import com.hrms.hjsj.sys.ConstantParamter;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.FieldSet;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.struts.upload.FormFile;

import javax.sql.RowSet;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 组织编码数据导入
 *<p>Title:InputOrgDataTrans.java</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Oct 11, 2007</p> 
 *@author sunxin
 *@version 4.0
 */
public class InputOrgDataTrans extends IBusiness{
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public void execute()throws GeneralException{
    	String orgid=(String)this.getFormHM().get("input_orgid");
    	orgid = orgid==null|| "".equals(orgid)?"root":orgid;
    	String privcode = this.userView.getManagePrivCode();
    	String privcodevalue = this.userView.getManagePrivCodeValue();
    	
    	ArrayList busOrg = new ArrayList();
    	if(privcode.length()<1)
    		return ;
    	
    	boolean issuper = this.userView.isSuper_admin();
    	String busiid;
		if("root".equals(orgid) && !this.userView.isSuper_admin())
    		busiid  =  this.userView.getUnitIdByBusi("4");
    	else
    		busiid = orgid;
    	
    		String[] busiids = busiid.split("`");
    		for(int i=0;i<busiids.length;i++){
    			if("UN".equals(busiids[i].toUpperCase())){
    				  issuper = true;
    				break;
    			}
    			busOrg.add(busiids[i].substring(2));
    		}
    	
    	/*try{
	    	if("root".equals(orgid)){
	    		if(privcodevalue.length()>0){
	    			String sql = "select codesetid,codeitemid from organization t1 where exists (select codeitemid from organization t2 where t2.codeitemid='"+privcodevalue+"' and t2.parentid<>'"+privcodevalue+"' and t1.codeitemid=t2.parentid)";
	    			ContentDAO dao=new ContentDAO(this.getFrameconn());
	    			this.frowset = dao.search(sql);
	    			if(this.frowset.next()){
	    				orgid=this.frowset.getString("codesetid")+this.frowset.getString("codeitemid");
	    			}
	    		}
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}*/
    	if(orgid.length()<=2)
    	{
    		return;
    	}
//    	try{
//    		ContentDAO dao=new ContentDAO(this.getFrameconn());
//    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    		String sql = "select end_date from organization where codeitemid='"+strCodeid+"' and (end_date>"+Sql_switcher.dateValue(sdf.format(new Date()))+" or "+Sql_switcher.isnull("end_date", Sql_switcher.dateValue("9999-12-31"))+"="+Sql_switcher.dateValue("9999-12-31")+")";
//    		this.frecset = dao.search(sql);
//    		boolean f = true;
//    		while(this.frecset.next()){
//    			f = false;
//    		}
//    		if(f&&this.userView.getVersion()>=50){
//    			throw GeneralExceptionHandler.Handle(new GeneralException("","未能导入数据，您选择的机构可能已被合并、划转或撤销！","",""));
//    		}
//    	}catch(Exception e){
//    		throw GeneralExceptionHandler.Handle(e);
//    	}
    	/**********上传的数据按格式分成数组，然后放到list中***********/
    	ArrayList list=new ArrayList();
    	FormFile file = (FormFile) this.getFormHM().get("file");
    	BufferedReader buf=null;
    	boolean isCorrect =true;
    	String orgtype = orgid.substring(0, 2);
    	RecordVo vo = new RecordVo("organization");
    	String desclength=(String)vo.getAttrLens().get("codeitemdesc");
    	String errorMess = "数据导入失败！可能的原因：导入数据格式不正确。";
    	try
    	{
    		
    		InputStream inStream=file.getInputStream();
    		InputStreamReader isr=new InputStreamReader(inStream); 
    		buf=new BufferedReader(isr);
    		String c;     		
    		while((c=buf.readLine())!=null){ 
             //System.out.println(c);
    			/*String [] codeitems=c.split("\t");
    			if(codeitems.length!=3)
    			{
    				isCorrect=false;
    				break;
    			}*/
    			if(!(c!=null&&c.trim().length()>0))
    				continue;
    			
    			// 使用\t分割可以避免机构名称中有空格时报错问题 chent 20180313 update 
    			String[] arr = c.split("\t");
    			int num = arr.length;
//    			
    			String[] codeitems = new String[6];
    			int index = 0;
    			if(num<3||num>6){
    				isCorrect=false;
    				break;
    			}
    			for(int i=0; i<arr.length; i++) {
					String tmpstr = arr[i];
    				if(index>2){
    					if(num==6){
    						if(index>3){//日期
    							tmpstr = this.checkdate(tmpstr);
    							if(!"false".equals(tmpstr)){
    								codeitems[index]=tmpstr;
    							}else{
    								index++;
    								continue;
    							}
    						}else{//转换代码
    							codeitems[index]=tmpstr;
    						}
    					}
    				}else{
    					if(index==2){
    						codeitems[index] = PubFunc.splitString(tmpstr,Integer.parseInt(desclength));
    					}else{
    						codeitems[index] = tmpstr;
    					}
    				}
    				index++;
    			}
    			if("@K".equals(orgtype)){
    				errorMess = "岗位下不能导入机构！";
    				throw new Exception(errorMess);
    			}
    			if("UM".equals(orgtype) && "UN".equalsIgnoreCase(codeitems[0])){
    				errorMess = "部门下不能导入单位！";
    				throw new Exception(errorMess);
    			}
    				
    			if(codeitems[3]==null){
    				codeitems[3]="";
    			}
    			if(codeitems[4]==null){
    				codeitems[4]="1949-10-01";
    			}
    			if(codeitems[5]==null){
    				codeitems[5]="9999-12-31";
    			}
    			list.add(codeitems);   			
    		}    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		throw GeneralExceptionHandler.Handle(new GeneralException("",errorMess,"",""));
    	}finally
    	{
    		try
    		{
    			if(buf!=null)
        			buf.close();
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	}
    	if(!isCorrect)
    		throw GeneralExceptionHandler.Handle(new GeneralException("",errorMess,"",""));
    	/**********生成临时表***********/
    	String table_name=createTempTable();    	
    	//把数据放到临时表中
    	if("root".equals(orgid) && issuper)
    	{
    			/******操作临时表*********/
        		inputAllCodeItem(list,table_name);//把数据放入临时表中
        		deleteRepeatRecord(table_name);
        		deleteAllOrganization(); //清除原组织机构的所有信息  
        		
        		inputDataFromTemp(table_name);//把临时表的信息放入组织机构表中
        		// 注释掉 updateA0000。这是重置a0000操作，会导致导入后的机构被重置了，顺序就不是导出前的顺序了 chent 20180329 delete
        		//updateA0000();//对A000重新排序
        		updateTableGrade();//对grade重置
        		dorpTable(table_name);//删除临时表
        		inputToAdminCode();//更新数据字典
    	}else
    	{
    		/******操作临时表*********/
    			 for(int i=0;i<busOrg.size();i++){
    				    inputCodeItem(busOrg.get(i).toString(),list,table_name);//把数据放到临时表中
    		    		deleteRepeatRecord(table_name);
    		    		deleteParentIdsAllChildid(busOrg.get(i).toString());//把选定id组织机构下面所有孩子信息都给删除
    		    		reOrderA0000(busOrg.get(i).toString(),list.size(),table_name);//修改临时表和组织机构表的a0000
    		    		upStrCodeItemChildid(busOrg.get(i).toString(),table_name);//修改选定组织id的孩子id
    		    		
    		    		inputDataFromTemp(table_name);//把临时表的信息放入组织机构表中
    		    		updateTableGrade();//对grade重置
    		    		initTable(table_name);
    		    		inputToAdminCode1(busOrg.get(i).toString());//更新数据字典
    			 }
    			 
    			 dorpTable(table_name);//删除临时表
    	}
//    	inputDataFromTemp(table_name);//把临时表的信息放入组织机构表中
//    	if(orgid.equals("root"))
//    		updateA0000();//对A000重新排序
//    	updateTableGrade();//对grade重置
//
//    	dorpTable(table_name);//删除临时表
//    	if(orgid.equals("root"))
//    		inputToAdminCode();//更新数据字典
//    	else
//    		inputToAdminCode1(strCodeid);//更新数据字典
    	//inputToAdminCode1(strCodeid);//更新数据字典
    	this.delRecord(orgid);
	}
    
    /**
     *  清空表数据
     * @param table_name 表名
     */
    public void initTable(String table_name){
    	try{
    		
    	     ContentDAO dao = new ContentDAO(this.frameconn);
    	     String sql = " delete "+table_name;
    	     dao.update(sql);
    	     
    	}catch(SQLException ex){
    		
    		ex.printStackTrace();
    	}
    }
    
    
    
    /**
     * 删除子集主集不存在机构的记录
     */
    private void delRecord(String orgid){
    	try{
    		String codeitemid = "";
    		if(!"root".equals(orgid)){
    			codeitemid = orgid.substring(2);
    		}else{
    			codeitemid=this.userView.getManagePrivCodeValue();
    		}
	    	ContentDAO dao = new ContentDAO(this.frameconn);
	    	ArrayList list = DataDictionary.getFieldSetList(1, 2);
	    	for(int i=list.size()-1;i>=0;i--){
	    		FieldSet set = (FieldSet)list.get(i);
	    		String setid = set.getFieldsetid();
	    		String sql = "delete from "+setid+" where b0110 like '"+codeitemid+"%' and not exists (select codeitemid from organization where codeitemid=b0110)";
	    		dao.update(sql);
	    	}
	    	list = DataDictionary.getFieldSetList(1, 3);
	    	for(int i=list.size()-1;i>=0;i--){
	    		FieldSet set = (FieldSet)list.get(i);
	    		String setid = set.getFieldsetid();
	    		String sql = "delete from "+setid+" where e01a1 like '"+codeitemid+"%' and not exists (select codeitemid from organization where codeitemid=e01a1)";
	    		dao.update(sql);
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    /**
     * 删除所有id
     *
     */
    private void deleteAllOrganization()
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	try
    	{
    		dao.delete("delete from ORGANIZATION where codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'",new ArrayList());
    		dao.delete("delete from vORGANIZATION where codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'",new ArrayList());
    		dao.delete("delete from K01 where e01a1 like '"+this.userView.getManagePrivCodeValue()+"%'",new ArrayList());
    		dao.delete("delete from B01 where b0110 like '"+this.userView.getManagePrivCodeValue()+"%'",new ArrayList());
    	}catch(Exception e)
    	{
    	  e.printStackTrace();	
    	}
    	
    	
    }
    /**
     * 清除选定组织id下面的所有信息
     * @param parentid
     */
    private void deleteParentIdsAllChildid(String parentid)
    {
    	/*ArrayList childList=new ArrayList();
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	childList.add(parentid);    	
    	for(int i=0;i<childList.size();i++)
    	{
    		String codeitemid=childList.get(i).toString();
    		childList(codeitemid,dao,childList);
    	}*/
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	String del="delete from ORGANIZATION where codeitemid like '"+parentid+"%' and codeitemid<>'"+parentid+"'";    	
    	try
    	{
    		/*for(int i=0;i<childList.size();i++)
    		{
    			ArrayList list=new ArrayList();
    			list.add(childList.get(i));
    			dao.delete(del,list);
    		} */
    		dao.update(del);
    		del="delete from vORGANIZATION where codeitemid like '"+parentid+"%' and codeitemid<>'"+parentid+"'";    	
    		dao.update(del);
    		del="delete from b01 where b0110 like '"+parentid+"%' and b0110<>'"+parentid+"'";    	
    		dao.update(del);
    		del="delete from k01 where e01a1 like '"+parentid+"%' and e01a1<>'"+parentid+"'";    	
    		dao.update(del);
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    private ArrayList childList(String parentid,ContentDAO dco,ArrayList childlist)
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("select codeitemid from ORGANIZATION where parentid='"+parentid+"' and parentid<>codeitemid order by codeitemid");
    	try
    	{
    		RowSet rs=dco.search(sql.toString());
    		while(rs.next())
    		{
    			childlist.add(rs.getString("codeitemid"));
    		}
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return childlist;
    }
    /**
     * 给临时表插入数据
     * @param orgid
     * @param list
     * @param table_name
     */
    private void  inputCodeItem(String orgid,ArrayList list,String table_name)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());   
    	HashMap map=getOrgidData(orgid);
    	StringBuffer insql=new StringBuffer();
    	insql.append("insert into "+table_name+" ");
    	insql.append("(codesetid,codeitemid,codeitemdesc,corcode,parentid,childid,grade,a0000,start_date,end_date) values ");
    	insql.append("(?,?,?,?,?,?,?,?,?,?)");
		try
		{
			ArrayList insertList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String [] codeitems=(String[])list.get(i);
				ArrayList onelist=new ArrayList();
    			onelist.add(codeitems[0]);
    			onelist.add(orgid+codeitems[1]);
    			onelist.add(codeitems[2]);
    			onelist.add(codeitems[3]);
    			onelist.add(orgid);
    			onelist.add(orgid+codeitems[1]);
    			onelist.add(new Integer(map.get("grade")+""));
    			onelist.add(new Integer(i+1));
    			java.sql.Date start_date = new java.sql.Date(sdf.parse(codeitems[4]).getTime());
    			onelist.add(start_date);
    			java.sql.Date end_date = new java.sql.Date(sdf.parse(codeitems[5]).getTime());
    			onelist.add(end_date);
    			insertList.add(onelist);
			}
			dao.batchInsert(insql.toString(),insertList);
			
		}catch(Exception e)
		{
			e.printStackTrace();			
		}finally{
			operateTemp(table_name);
		}
    }
    private void inputAllCodeItem(ArrayList list,String table_name)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer insql=new StringBuffer();
    	insql.append("insert into "+table_name+" ");
    	insql.append("(codesetid,codeitemid,codeitemdesc,corcode,parentid,childid,grade,a0000,start_date,end_date) values ");
    	insql.append("(?,?,?,?,?,?,?,?,?,?)");
		try
		{
			ArrayList insertList=new ArrayList();
			for(int i=0;i<list.size();i++)
			{
				String [] codeitems=(String[])list.get(i);
				ArrayList onelist=new ArrayList();
    			onelist.add(codeitems[0]);
    			onelist.add(codeitems[1]);
    			onelist.add(codeitems[2]);
    			onelist.add(codeitems[3]);
    			onelist.add(codeitems[1]);
    			onelist.add(codeitems[1]);
    			onelist.add(new Integer(1));
    			onelist.add(new Integer(i+1));
    			java.sql.Date start_date = new java.sql.Date(sdf.parse(codeitems[4]).getTime());
    			onelist.add(start_date);
    			java.sql.Date end_date = new java.sql.Date(sdf.parse(codeitems[5]).getTime());
    			onelist.add(end_date);
    			insertList.add(onelist);
			}
			dao.batchInsert(insql.toString(),insertList);
			
		}catch(Exception e)
		{
			e.printStackTrace();			
		}finally{
			operateTemp(table_name);
		}
    }
    /**
     * 操作临时表
     * @param orgid
     * @param table_name
     */
    private void operateTemp(String table_name)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());    	
    	StringBuffer sql=new StringBuffer();
    	sql.append("select codeitemid,grade from "+table_name+" order by codeitemid");
    	try
    	{
    		String codeitemid="";
    		int grade=0;
    		this.frowset=dao.search(sql.toString());
    		while(this.frowset.next())
    		{
    			codeitemid=this.frowset.getString("codeitemid");
    			grade=this.frowset.getInt("grade");
    			setTempParentid(dao,codeitemid,table_name);
    			setTempGrade(dao,codeitemid,table_name,grade);
    		}
    		this.frowset.beforeFirst();
    		while(this.frowset.next())
    		{
    			codeitemid=this.frowset.getString("codeitemid");
    			setTempChildid(dao,codeitemid,table_name);
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 修改临时表的父子关系
     * @param dao
     * @param codeitemid
     * @param table_name
     */
    private void setTempParentid(ContentDAO dao,String codeitemid,String table_name)
    {
       try
       {
    	   String up="update "+table_name+" set parentid='"+codeitemid+"' where codeitemid like '"+codeitemid+"%' and codeitemid<>'"+codeitemid+"'";
    	   dao.update(up);
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }	
    }
    /**
     * 修改临时表的父子关系
     * @param dao
     * @param codeitemid
     * @param table_name
     */
    private void setTempChildid(ContentDAO dao,String codeitemid,String table_name)
    {
       try
       {
    	   String sql="select codeitemid from "+table_name+" where parentid='"+codeitemid+"' and parentid<>codeitemid  order by codeitemid";
    	   RowSet  rst=dao.search(sql);
    	   String childid="";
    	   if(rst.next())
    	   {
    		   childid=rst.getString("codeitemid");
    		   if(childid!=null&&childid.length()>0)
    		   {
    			   String up="update "+table_name+" set childid='"+childid+"' where codeitemid ='"+codeitemid+"'";
    			   dao.update(up); 
    		   }
    	   }
    	   
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }	
    }
    /**
     * 修改临时表的父子关系
     * @param dao
     * @param codeitemid
     * @param table_name
     */
    private void setTempGrade(ContentDAO dao,String codeitemid,String table_name,int grade)
    {
       try
       {
    	   String up="update "+table_name+" set grade='"+(grade+1)+"' where codeitemid like '"+codeitemid+"%' and codeitemid<>'"+codeitemid+"'";
    	   dao.update(up);
       }catch(Exception e)
       {
    	   e.printStackTrace();
       }	
    }   
    /**
     * 生成临时表
     * @return
     */
    private String createTempTable()
    {
    	String table_name="t#"+this.userView.getUserName()+"_hr_org";	
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    	kqUtilsClass.dropTable(table_name);
    	kqUtilsClass.createTempTable("ORGANIZATION", table_name, "*","1=2","");	
    	return table_name;
    }
    /**
     * 删除表
     * @param table_name
     */
    private void dorpTable(String table_name)
    {
    	KqUtilsClass kqUtilsClass=new KqUtilsClass(this.getFrameconn());
    	kqUtilsClass.dropTable(table_name);
    }
    /**
     * 得到选定组织id的其他信息
     * @param orgid
     * @return
     */
    private HashMap getOrgidData(String orgid)
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("select grade,a0000 FROM ORGANIZATION where codeitemid='"+orgid+"'");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	HashMap map=new HashMap();
    	try
    	{
    		this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    			int grade=this.frowset.getInt("grade");
    			int a0000=this.frowset.getInt("a0000");
    			map.put("grade",new Integer(grade+1));
    			map.put("a0000",new Integer(a0000+1));
    		}
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return map;
    } 
    /**
     * 重新排序a0000
     * @param parentid
     * @param num
     * @param table_name
     */
    private void reOrderA0000(String parentid,int num,String table_name)
    {
    	StringBuffer sql=new StringBuffer();
    	sql.append("select a0000 from ORGANIZATION where codeitemid='"+parentid+"'");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	String a0000="";
    	try
    	{
    		this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    			a0000=this.frowset.getString("a0000");
    		String up="update ORGANIZATION set a0000=a0000+"+num+" where a0000>"+a0000;
    		String up_temp="update "+table_name+" set a0000=a0000+"+a0000;
    		dao.update(up_temp);//修改临时表的a0000
    		dao.update(up);//修改组织机构表
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 修改选定节点的孩子id
     * @param parentid
     * @param table_name
     */
    private void upStrCodeItemChildid(String parentid,String table_name)
    {
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer sql=new StringBuffer();
    	sql.append("select codeitemid from "+table_name+" where parentid='"+parentid+"' order by codeitemid");
    	String childid="";
    	try
    	{
    		this.frowset=dao.search(sql.toString());
    		if(this.frowset.next())
    		{
    			childid=this.frowset.getString("codeitemid");
    		    if(childid!=null&&childid.length()>0)
    		    {
    		    	String up="update ORGANIZATION set childid='"+childid+"' where codeitemid='"+parentid+"'";
    		    	dao.update(up);
    		    }
    		}    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    } 
    /**
     * 把临时表的信息插入到组织机构表中
     * @param table_name
     */
    private void inputDataFromTemp(String table_name)
    {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	StringBuffer sql=new StringBuffer();
    	sql.append("insert into ORGANIZATION (codesetid,codeitemid,codeitemdesc,corcode,parentid,childid,grade,a0000,start_date,end_date)");
    	sql.append(" select codesetid,codeitemid,codeitemdesc,corcode,parentid,childid,grade,a0000,"+/*Sql_switcher.dateValue(sdf.format(new Date()))+" as"*/" start_date,"+/*Sql_switcher.dateValue("9999-12-31")+" as "*/"end_date from "+table_name);
    	sql.append(" where not exists");
    	sql.append("(select 1 from ORGANIZATION where ORGANIZATION.codeitemid="+table_name+".codeitemid)");
    	try
    	{
    		dao.insert(sql.toString(),new ArrayList());
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    /**
     * 想数据字典中加入导入数据
     * @param list
     */
    private void inputToAdminCode(String orgid,ArrayList list){
    	HashMap map=getOrgidData(orgid);
    	for(int i=0;i<list.size();i++)
		{
			String [] codeitems=(String[])list.get(i);
			CodeItem item=new CodeItem();
			item.setCodeid(codeitems[0]);
			item.setCodeitem(codeitems[1]);
			item.setCodename(PubFunc.splitString(codeitems[2],50));
			item.setPcodeitem(codeitems[1]);
			item.setCcodeitem(codeitems[1]);
			item.setCodelevel(map.get("grade")+"");
			AdminCode.addCodeItem(item);
			AdminCode.updateCodeItemDesc(codeitems[0],codeitems[1],PubFunc.splitString(codeitems[2],50));
		}
    }
    private void inputToAdminCode(){
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	String sql = "select * from organization where codeitemid like '"+this.userView.getManagePrivCodeValue()+"%'";
    	try {
    		this.frecset = dao.search(sql);
			RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
			String  pos_code_field = null;
			String  unit_code_field = null;
			if(pos_code_field_constant_vo!=null)
			{
				 pos_code_field=pos_code_field_constant_vo.getString("str_value");
				 
			}
			
			RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
			if(unit_code_field_constant_vo!=null)
			{
				 unit_code_field=unit_code_field_constant_vo.getString("str_value");
			}
	    	while(this.frecset.next())
			{
				CodeItem item=new CodeItem();
				String codesetid = this.frecset.getString("codesetid");
				String codeitemid = this.frecset.getString("codeitemid");
				String codeitemdesc = this.frecset.getString("codeitemdesc");
				String parentid = this.frecset.getString("parentid");
				String childid = this.frecset.getString("childid");
				String grade = this.frecset.getString("grade");
				String corcode = this.frecset.getString("corcode");
				item.setCodeid(codesetid);
				item.setCodeitem(codeitemid);
				item.setCodename(codeitemdesc);
				item.setPcodeitem(parentid);
				item.setCcodeitem(childid);
				item.setCodelevel(grade);
				AdminCode.addCodeItem(item);
				ResultSet rs = null;
				try{
    				Calendar calendar = Calendar.getInstance();
    				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				String date = sdf2.format(calendar.getTime());
    				if("@K".equalsIgnoreCase(codesetid)){
    					
    					if(pos_code_field_constant_vo!=null)
    					{
    						FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
    					  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
    						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update K01 set e0122='"+parentid+"',"+pos_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into K01(e01a1,e0122,"+pos_code_field+",createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
    					  }else{
      						sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update K01 set e0122='"+parentid+"',createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into K01(e01a1,e0122,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
      					}
    					}else{
    						sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
  						  rs = dao.search(sql);
  						  if(rs.next()){
  							  sql = "update K01 set e0122='"+parentid+"',createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
  						  }else{
  							  sql = "insert into K01(e01a1,e0122,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
  						  }
  						  dao.update(sql);
    					}
    				}else{
    					if(unit_code_field_constant_vo!=null)
    					{
    						FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
    					  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
    						  sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update B01 set "+unit_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into B01(b0110,"+unit_code_field+",createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
    					  }else{
      						sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
      					}
    					}else{
    						sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
  						  rs = dao.search(sql);
  						  if(rs.next()){
  							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
  						  }else{
  							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
  						  }
  						  dao.update(sql);
    					}
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				if(rs!=null)
    					rs.close();
    			}
			}
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void inputToAdminCode1(String orgid,ArrayList list){
    	HashMap map=getOrgidData(orgid);
    	for(int i=0;i<list.size();i++)
		{
			String [] codeitems=(String[])list.get(i);
			CodeItem item=new CodeItem();
			item.setCodeid(codeitems[0]);
			item.setCodeitem(orgid+codeitems[1]);
			item.setCodename(PubFunc.splitString(codeitems[2],50));
			item.setPcodeitem(orgid);
			item.setCcodeitem(codeitems[1]);
			item.setCodelevel(map.get("grade")+"");
			AdminCode.addCodeItem(item);
			AdminCode.updateCodeItemDesc(codeitems[0],orgid+codeitems[1],PubFunc.splitString(codeitems[2],50));
		}
    	//AdminCode.refreshCodeTable();
    }
    private void inputToAdminCode1(String orgid){
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	String sql = "select * from organization where codeitemid like '"+orgid+"%'";
    	try {
			this.frecset = dao.search(sql);
			RecordVo pos_code_field_constant_vo=ConstantParamter.getRealConstantVo("POS_CODE_FIELD",this.getFrameconn());
			String  pos_code_field = null;
			String  unit_code_field = null;
			if(pos_code_field_constant_vo!=null)
			{
				 pos_code_field=pos_code_field_constant_vo.getString("str_value");
				 
			}
			
			RecordVo unit_code_field_constant_vo=ConstantParamter.getRealConstantVo("UNIT_CODE_FIELD",this.getFrameconn());
			if(unit_code_field_constant_vo!=null)
			{
				 unit_code_field=unit_code_field_constant_vo.getString("str_value");
			}
	    	while(this.frecset.next())
			{
				CodeItem item=new CodeItem();
				String codesetid = this.frecset.getString("codesetid");
				String codeitemid = this.frecset.getString("codeitemid");
				String codeitemdesc = this.frecset.getString("codeitemdesc");
				String parentid = this.frecset.getString("parentid");
				String childid = this.frecset.getString("childid");
				String grade = this.frecset.getString("grade");
				String corcode = this.frecset.getString("corcode");
				corcode = corcode!=null?corcode:"";
				item.setCodeid(codesetid);
				item.setCodeitem(codeitemid);
				item.setCodename(codeitemdesc);
				item.setPcodeitem(parentid);
				item.setCcodeitem(childid);
				item.setCodelevel(grade);
				AdminCode.addCodeItem(item);
				ResultSet rs = null;
				try{
    				Calendar calendar = Calendar.getInstance();
    				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    				String date = sdf2.format(calendar.getTime());
    				if("@K".equalsIgnoreCase(codesetid)){
    					
    					if(pos_code_field_constant_vo!=null)
    					{
    						FieldItem pos_code_fieldItem = DataDictionary.getFieldItem(pos_code_field);
    					  if(pos_code_field!=null&&pos_code_field.length()>1 && pos_code_fieldItem != null && "1".equals(pos_code_fieldItem.getUseflag())){
    						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update K01 set e0122='"+parentid+"',"+pos_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into K01(e01a1,e0122,"+pos_code_field+",createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
    					  }else{
	    						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
		  						  rs = dao.search(sql);
		  						  if(rs.next()){
		  							  sql = "update K01 set e0122='"+parentid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
		  						  }else{
		  							  sql = "insert into K01(e01a1,e0122,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
		  						  }
		  						  dao.update(sql);
	    					}
    					}else{
    						  sql = "select e01a1 from K01 where e01a1='"+codeitemid.toUpperCase()+"'";
	  						  rs = dao.search(sql);
	  						  if(rs.next()){
	  							  sql = "update K01 set e0122='"+parentid+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+" where e01a1='"+codeitemid.toUpperCase()+"'";
	  						  }else{
	  							  sql = "insert into K01(e01a1,e0122,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+parentid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
	  						  }
	  						  dao.update(sql);
    					}
    				}else{
    					if(unit_code_field_constant_vo!=null)
    					{
    						FieldItem unit_code_fieldItem = DataDictionary.getFieldItem(unit_code_field);
    					  if(unit_code_field!=null&&unit_code_field.length()>1 && unit_code_fieldItem != null && "1".equals(unit_code_fieldItem.getUseflag())){
    						  sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
    						  rs = dao.search(sql);
    						  if(rs.next()){
    							  sql = "update B01 set "+unit_code_field+"='"+corcode+"', createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
    						  }else{
    							  sql = "insert into B01(b0110,"+unit_code_field+",createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+corcode+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
    						  }
    						  dao.update(sql);
    					  }else{
      						sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
	  						  rs = dao.search(sql);
	  						  if(rs.next()){
	  							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
	  						  }else{
	  							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
	  						  }
	  						  dao.update(sql);
    					  }
    					}else{
    						sql = "select b0110 from B01 where b0110='"+codeitemid.toUpperCase()+"'";
	  						  rs = dao.search(sql);
	  						  if(rs.next()){
	  							  sql = "update B01 set createusername='"+this.userView.getUserName()+"',modusername='"+this.userView.getUserName()+"',createtime="+Sql_switcher.dateValue(date)+",modtime="+Sql_switcher.dateValue(date)+"  where b0110='"+codeitemid.toUpperCase()+"'";
	  						  }else{
	  							  sql = "insert into B01(b0110,createusername,modusername,createtime,modtime) values ('"+codeitemid.toUpperCase()+"','"+this.userView.getUserName()+"','"+this.userView.getUserName()+"',"+Sql_switcher.dateValue(date)+","+Sql_switcher.dateValue(date)+")";
	  						  }
	  						  dao.update(sql);
    					}
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    			}finally{
    				if(rs!=null)
    					rs.close();
    			}
			}
    	} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void updateA0000(){
    	ContentDAO dao = new ContentDAO(this.frameconn);
    	StringBuffer sql = new StringBuffer();
    	HashMap map = new HashMap();
    	ArrayList ilist = new ArrayList();
    	ArrayList slist = new ArrayList();
    	
	    	sql.append("select codeitemid from organization order by codeitemid ");
	    	try {
	    		int index = 1;
				this.frowset = dao.search(sql.toString());
				while(this.frowset.next()){
					map.put(this.frowset.getString(1),new Integer(index));
					ilist.add(this.frowset.getString(1));
					//ilist.add(index+"");
					//alist.add(ilist);
					index++;
				}
				for(int i=0;i<map.size();i++){
					sql.setLength(0);
					sql.append(" update organization set a0000 ='"+map.get(ilist.get(i))+"' where codeitemid ='"+ilist.get(i)+"'");
					dao.update(sql.toString());
					slist.add(sql.toString());
				}
				
				//dao.batchUpdate(slist);
			} catch (SQLException e) {
				e.printStackTrace();
			}
    	
    }
    
	private void updateTableGrade(){
		int i = 1;
    	StringBuffer sql = new StringBuffer();
    	ContentDAO dao=new ContentDAO(this.getFrameconn());
    	//sql.append("update organization set grade = '1' where codeitemid=parentid ");
    	int number = this.updateTempTableGrade(i,sql,"organization",dao);
    	i++;
    	while(number>0)
    	{
    		number = this.updateTempTableGrade(i,sql,"organization",dao);
    		i++;
    	}
	}
	private int updateTempTableGrade(int i,StringBuffer sql,String table,ContentDAO dao)
    {
    	 int number = 0;
    	 int gradevalue=i+1;
    	 try
    	 {
    		 sql.append("update ");
    	     sql.append(table);
    	     sql.append(" ");
    	     sql.append("set grade = "+gradevalue+" ");
    	     sql.append("where codeitemid<>parentid and ");
    	     sql.append("parentid in (select codeitemid  ");
    	     sql.append("from ");
    	     sql.append(table);
    	     sql.append(" ");			     
    	     sql.append("where ");
    	     sql.append("grade = "+i+" )");    	     
    	     number = dao.update(sql.toString());
    	     sql.delete(0,sql.length());
    	     
    	 }catch(Exception e)
    	 {
    		 e.printStackTrace();
    	 }	     
	     return number;
    }
	
	/**
	 * 去除临时表重复记录
	 * @param table_name
	 */
	private void deleteRepeatRecord(String table_name){
		ContentDAO dao = new ContentDAO(this.frameconn);	
	    	String sql="delete from "+table_name+" where codeitemid in (select codeitemid from "+table_name+" group by codeitemid having count(codeitemid) > 1) and a0000 not in (select min(a0000) from "+table_name+" group by codeitemid having count(codeitemid)>1)";
	    	try {
	    		dao.update(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	private String checkdate(String str){
		boolean flag = true;
		//String str="2010年";
		if(str.length()<4){
			return "false";
		}
		if(str.length()==4){
			Pattern p= Pattern.compile("^(\\d{4})$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str+"-01-01";
			}else{
				return "false";
			}
		}
		if(str.length()<6){
			Pattern p= Pattern.compile("^(\\d{4})年$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				return str.replace("年", "-")+"01-01";
			}else{
				return "false";
			}
		}
		if(str.length()==7){
			if(str.indexOf("月")!=-1){
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					if(str.indexOf("月")!=-1){
						return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
					}else{
						return str.replace("年", "-").replace(".", "-")+"-01";
					}
				}else{
					return "false";
				}
			}else{
				Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])$");
				Matcher m = p.matcher(str);
				if(m.matches()){
					return str.replace("年", "-").replace(".", "-")+"-01";
				}else{
					return "false";
				}
			}
		}
		if(str.length()<8){//2010年3  2010年3月
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				if(str.indexOf("月")!=-1){
					return str.replace("年", "-").replace(".", "-").replace("月", "-")+"01";
				}else{
					return str.replace("年", "-").replace(".", "-")+"-01";
				}
			}else{
				return "false";
			}
		}
		if(str.length()==8){//2010年3  2010年3月1
			Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])*$");
			Matcher m = p.matcher(str);
			if(m.matches()){
				str =str.replace("年", "-").replace(".", "-").replace("月", "-");
				if(str.lastIndexOf("-")==str.length()){
					if(str.length()<10){
						return str+"01";
					}
				}else{
					String[] temps=str.split("-");
					if(temps.length>2){
						String t="false";
						if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
							int year = Integer.parseInt(temps[0]);
							int month=Integer.parseInt(temps[1]);
							int day=Integer.parseInt(temps[2]);
							switch(month){
							case 1:
							case 3:
							case 5:
							case 7:
							case 8:
							case 10:
							case 12:
							{
								if(1<=day&&day<=31){
									t=str;
								}
								break;
							}
							case 4:
							case 6:
							case 9:
							case 11:
							{
								if(1<=day&&day<=30){
									t=str;
								}
								break;
							}
							case 2:
							{
								 if(isLeapYear(year)){
									 if(1<=day&&day<=29){
											t=str;
									}
								 }else{
									 if(1<=day&&day<=28){
											t=str;
									}
								 }
								 break;
							}
								
							}
						}
						return t;
					}else{
						return "false";
					}
					
					
				}
			}else{
				return "false";
			}
		}
		Pattern p= Pattern.compile("^(\\d{4})[-.年]([0]*\\d{1}|1[0-2])[-.月]([0]*\\d{1}|[12]\\d{1}|3[01])[日]*$");
		Matcher m = p.matcher(str);
		if(m.matches()){
			String temp=str.replace("年", "-").replace(".", "-").replace("月", "-").replace("日", "");
			String[] temps = temp.split("-");
			String t="false";
			if(temps[0].length()>0&&temps[1].length()>0&&temps[2].length()>0){
				int year = Integer.parseInt(temps[0]);
				int month=Integer.parseInt(temps[1]);
				int day=Integer.parseInt(temps[2]);
				switch(month){
				case 1:
				case 3:
				case 5:
				case 7:
				case 8:
				case 10:
				case 12:
				{
					if(1<=day&&day<=31){
						t=temp;
					}
					break;
				}
				case 4:
				case 6:
				case 9:
				case 11:
				{
					if(1<=day&&day<=30){
						t=temp;
					}
					break;
				}
				case 2:
				{
					 if(isLeapYear(year)){
						 if(1<=day&&day<=29){
								t=temp;
						}
					 }else{
						 if(1<=day&&day<=28){
								t=temp;
						}
					 }
					 break;
				}
					
				}
			}
			return t;
		}else{
			return "false";
		}
	}
	private boolean isLeapYear(int year){
		boolean t=false;
		if(year%4==0){
			   if(year%100!=0){
				   t=true;
			   }else if(year%400==0){
				   t=true;
			   }
		  }
		return t;
	}

}
