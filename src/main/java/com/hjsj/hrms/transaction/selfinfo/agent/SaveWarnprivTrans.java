package com.hjsj.hrms.transaction.selfinfo.agent;

import com.hjsj.hrms.businessobject.sys.DbSecurityImpl;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SaveWarnprivTrans extends IBusiness {
	Document a_doc = null;
	
	public void execute() throws GeneralException {
		PreparedStatement ps = null;
		DbSecurityImpl dbs = new DbSecurityImpl();
		try {
    		String str_value = "";
            String id=(String)this.getFormHM().get("id");
            ElementWork(id);
            String warn_str=(String)this.getFormHM().get("selstr");
            String []warn_str_array = warn_str.split(",");
            String rsbd_nums = "";
            String gzbd_nums = "";
            String ins_bd_nums = "";
            
            for (int i = 0; i < warn_str_array.length; i++) {
    			String warn_id = warn_str_array[i];
    			if(warn_id.indexOf("#")!=-1){
    				String res_flag=warn_id.substring(0, warn_id.indexOf("#"));
    				String wid = warn_id.substring(warn_id.indexOf("#")+1);
    				//修改代理业务授权不能保存  upd by hej 2015/12/9
    				if(res_flag.equalsIgnoreCase(String.valueOf(IResourceConstant.RSBD))){
    			    	rsbd_nums+=","+wid;
    			    }
    				else if("rsbd".equalsIgnoreCase(res_flag)){
    			    	rsbd_nums+=","+wid;
    			    }
    				else if(res_flag.equalsIgnoreCase(String.valueOf(IResourceConstant.GZBD))){
    			    	gzbd_nums+=","+wid;
    			    }
    				else if("gzdb".equalsIgnoreCase(res_flag)){
    					gzbd_nums+=","+wid;
    			    }
    				else if(res_flag.equalsIgnoreCase(String.valueOf(IResourceConstant.INS_BD))){
    					ins_bd_nums+=","+wid;
    			    }
    				else if("ins_bd".equalsIgnoreCase(res_flag)){
    					ins_bd_nums+=","+wid;
    			    }
    			}
    		}
            if(!"".equals(rsbd_nums)){
    	    	rsbd_nums+=",";
    	    }
    	    if(!"".equals(gzbd_nums)){
    	    	gzbd_nums+=",";
    	    }
    	    if(!"".equals(ins_bd_nums)){
    	    	ins_bd_nums+=",";
    	    }
            Element root = null;
    		Element plannode = null;
            if(this.a_doc==null)
    		{
    			root = new Element("Per_Parameters");
    			Element ele = new Element("warn_priv");
            	ele.setAttribute("rsbd", rsbd_nums);
            	ele.setAttribute("gzbd", gzbd_nums);
            	ele.setAttribute("ins_bd", ins_bd_nums);
            	root.addContent(ele);
            	plannode = ele;	        							
    		}
    		else
    		{
    			root = this.a_doc.getRootElement();
    			plannode = root.getChild("warn_priv");
    		}
            if(plannode==null)
            {       		
            	Element ele = new Element("warn_priv");
            	ele.setAttribute("rsbd", rsbd_nums);
            	ele.setAttribute("gzbd", gzbd_nums);
            	ele.setAttribute("ins_bd", ins_bd_nums);
            	root.addContent(ele);
            	plannode = ele;
            }else
            {
            	// 删除节点 work_records
            	root.removeChildren("warn_priv");
            	Element ele = new Element("warn_priv");
            	ele.setAttribute("rsbd", rsbd_nums);
            	ele.setAttribute("gzbd", gzbd_nums);
            	ele.setAttribute("ins_bd", ins_bd_nums);
            	root.addContent(ele);
            	plannode = ele;
            }
            if(this.a_doc==null)
    		{
    			Document myDocument = new Document(root);
    			XMLOutputter outputter = new XMLOutputter();
    			Format format = Format.getPrettyFormat();
    			format.setEncoding("UTF-8");
    			outputter.setFormat(format);
    			str_value = outputter.outputString(myDocument);
    		}
    		else
    		{
    			XMLOutputter outputter=new XMLOutputter();
    	    	Format format=Format.getPrettyFormat();
    			format.setEncoding("UTF-8");
    			outputter.setFormat(format);
    			str_value = outputter.outputString(this.a_doc);
    		}
            
            String sql = "update agent_set set warnpriv=? where id="+id;
            ps = this.getFrameconn().prepareStatement(sql);
            switch(Sql_switcher.searchDbServer())
    		{
    			case Constant.MSSQL:
    				ps.setString(1, str_value);
    				break;
    			case Constant.ORACEL:
    				ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
    				          getBytes())), str_value.length());
    				break;
    			case Constant.DB2:
    				ps.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
    				          getBytes())), str_value.length());
    				break;
    		}
            
            dbs.open(this.frameconn, sql);
    		ps.executeUpdate();						
        } catch (Exception e) {
            e.printStackTrace();
            throw GeneralExceptionHandler.Handle(e);
        } finally {
            com.hjsj.hrms.utils.PubFunc.closeResource(ps);
            dbs.close(this.frameconn);
		}
	 }
	/* private void saveWarnPriv(String id,String warn_str) throws GeneralException
	 {

		    RecordVo vo=new RecordVo("agent_set");
			vo.setInt("id", Integer.parseInt(id));	
			ContentDAO dao=new ContentDAO(this.getFrameconn());
	        try
		    {
		    	vo=dao.findByPrimaryKey(vo);
		    	vo.setString("warnpriv", warn_str);
		    	dao.updateValueObject(vo);
		    }
		    catch(Exception  ex)
		    {
		    	  throw GeneralExceptionHandler.Handle(new GeneralException("没有找到对应的代理用户记录！"));
		    } 
	 }*/
	 public void ElementWork(String id)
	    {

			String sql = "select warnpriv from agent_set where id="+id;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try
			{
			    ps = this.getFrameconn().prepareStatement(sql);
			    rs = ps.executeQuery();
			    String xmlContext = "";
		
			    if (rs.next())
			    {
			    	xmlContext = Sql_switcher.readMemo(rs, "warnpriv"); // PubFunc.nullToStr(rs.getString("parameter_content"));
			    }
		
			    if (xmlContext!=null && xmlContext.trim().length()>0 && !"".equals(xmlContext.trim()))
			    {
					this.a_doc = PubFunc.generateDom(xmlContext);
			    }
			} catch (Exception e)
			{
			    e.printStackTrace();
			}finally {
				PubFunc.closeResource(rs);
				PubFunc.closeResource(ps);
			}
	    }
}

