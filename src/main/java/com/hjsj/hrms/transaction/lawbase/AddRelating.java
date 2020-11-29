package com.hjsj.hrms.transaction.lawbase;

import com.hjsj.hrms.businessobject.param.DocumentParamXML;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddRelating extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		HashMap hm = (HashMap)this.getFormHM().get("requestPamaHM");
		
		String pri = (String)hm.get("pri");
		pri=pri!=null&&pri.trim().length()>0?pri:"1";
		hm.remove("pri");
		this.getFormHM().put("priv",pri);
		
		String chkflag = (String)hm.get("chkflag");
		chkflag=chkflag!=null&&chkflag.trim().length()>0?chkflag:"0";
		this.getFormHM().put("checkflag",chkflag);
		hm.remove("chkflag");
		
		ArrayList selectrname = new ArrayList();
		if("0".equals(chkflag)){
			String fileid = (String)hm.get("a_id");
			fileid= PubFunc.decrypt(fileid);
			this.getFormHM().put("a_id",fileid);

			ContentDAO dao = new ContentDAO(this.getFrameconn());
			DocumentParamXML documentParamXML = new DocumentParamXML(this.getFrameconn());
			String codesetid=documentParamXML.getValue(DocumentParamXML.FILESET,"setid");
			String codeitemid=documentParamXML.getValue(DocumentParamXML.FILESET,"fielditem");
			ArrayList employelist = new ArrayList();
			ArrayList dblist = new ArrayList();
			String dbsql = "select Pre from DBName";
			try {
				this.frowset = dao.search(dbsql);
				while(this.frowset.next()){
					dblist.add(this.frowset.getString("Pre"));
				}
				for(int i=0;i<dblist.size();i++){
					String sql  = "select distinct a0100 from "+dblist.get(i)+codesetid+" where "+codeitemid+" = '"+fileid+"'";
					this.frowset  = dao.search(sql);
					while(this.frowset.next()){
						employelist.add(this.frowset.getString("a0100"));
					}
					for(int j=0;j<employelist.size();j++){
						CommonData data = new CommonData();
						String datasql = "select a0100,a0101 from "+dblist.get(i)+"A01 where a0100 = '"+employelist.get(j)+"'";
						this.frowset = dao.search(datasql);
						while(this.frowset.next()){
							data.setDataName(this.frowset.getString("a0101"));
							data.setDataValue(dblist.get(i)+this.frowset.getString("a0100"));
							selectrname.add(data);
						}
					}
					employelist.clear();
				}
			} catch (SQLException e) {e.printStackTrace();}
		}
		else if("8".equalsIgnoreCase(chkflag))
		{
			String z0501=(String)hm.get("z0501");
			/**安全改造,这里的数据z0501被加密了，需要解密回来**/
			try
			{
				if(!"batch".equalsIgnoreCase(z0501))
				{
					z0501 = PubFunc.decrypt(z0501);
		    		String id=z0501.split("/")[0];
			    	String cloumn=z0501.split("/")[1];
		        	RecordVo vo = new RecordVo("z05");
     		    	vo.setString("z0501", id);
		        	ContentDAO dao = new ContentDAO(this.getFrameconn());
		        	this.frowset=dao.search("select pre from dbname");
		        	HashMap map = new HashMap();
		        	while(this.frowset.next())
		        	{
		        		map.put(this.frowset.getString(1).toUpperCase(), "1");
		        	}
		        	vo=dao.findByPrimaryKey(vo);
		        	String a0100=vo.getString(cloumn);
		        	if(a0100!=null&&!"".equals(a0100))
		        	{
		    	    	//a0100=a0100.substring(0,a0100.length()-1).replace(",", "','");
		        		String[] arr=a0100.split(",");
		        		for(int i=0;i<arr.length;i++)
		        		{
		        			if(arr[i]==null|| "".equals(arr[i]))
		        				continue;
		        			String sql="";
		        			String temp="";
		        			if(map.get(arr[i].substring(0,3).toUpperCase())!=null)
		        			{
		    	        	   sql = "select a0100,a0101 from "+arr[i].substring(0,3)+"A01 where a0100 ='"+arr[i].substring(3)+"'";
		    	        	   temp=arr[i].substring(0,3);
		        			}else{
		        				sql = "select a0100,a0101 from usra01 where a0100='"+arr[i]+"'";
		        				temp="USR";
		        			}
		             		this.frowset=dao.search(sql);
		        	    	while(this.frowset.next())
		        	    	{
		        	    		CommonData data = new CommonData(temp+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
		        	    		selectrname.add(data);
		        	      	}
		        		}
		        	}
		    	}
				String dbpre=(String)hm.get("dbpre_str");
				this.getFormHM().put("dbpre_str", dbpre);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}else if("11".equalsIgnoreCase(chkflag)){
			String nbase=(String) hm.get("nbase");
			String p0100=(String) hm.get("p0100");
			if(p0100.indexOf(",")!=-1&&p0100.split(",").length<2){
				p0100=p0100.substring(0, p0100.indexOf(","));
			}
			if(p0100.indexOf(",")!=-1&&p0100.split(",").length>1){
				this.getFormHM().put("selectrname",selectrname);
				return ;
			}
			String sql="select a0100,a0101 from per_diary_actor where p0100 ='"+p0100+"' and state='1'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				String str = "select str_value from constant where constant='SS_LOGIN'";
				RowSet rs = dao.search(str);
				if(rs.next())
					nbase = Sql_switcher.readMemo(rs, "str_value");
				this.getFormHM().put("dbpre_str",nbase);
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					CommonData data = new CommonData("Usr"+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
					selectrname.add(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else if("12".equalsIgnoreCase(chkflag)){
			String p0600=(String) hm.get("p0600");
			if(p0600.indexOf(",")!=-1&&p0600.split(",").length<2){
				p0600=p0600.substring(0, p0600.indexOf(","));
			}
			if(p0600.indexOf(",")!=-1&&p0600.split(",").length>1){
				this.getFormHM().put("selectrname",selectrname);
				return ;
			}
			String sql="select a0100,a0101 from per_keyevent_actor where p0600 ='"+p0600+"'";
			ContentDAO dao = new ContentDAO(this.getFrameconn());
			try {
				this.frowset=dao.search(sql);
				while(this.frowset.next())
				{
					CommonData data = new CommonData("Usr"+this.frowset.getString("a0100"),this.frowset.getString("a0101"));
					selectrname.add(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		this.getFormHM().put("selectrname",selectrname);
	}

}
