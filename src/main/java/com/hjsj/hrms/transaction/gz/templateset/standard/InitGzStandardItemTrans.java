package com.hjsj.hrms.transaction.gz.templateset.standard;

import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemBo;
import com.hjsj.hrms.businessobject.gz.templateset.GzStandardItemVo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;

import java.util.HashMap;
/**
 * 
 *<p>Title:初始化工资标准表明细</p> 
 *<p>Description:</p> 
 *<p>Company:HJHJ</p> 
 *<p>Create time:Jul 25, 2007:4:14:25 PM</p> 
 *@author dengcan
 *@version 4.0
 */
public class InitGzStandardItemTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=(HashMap)this.getFormHM().get("requestPamaHM");
			String opt=(String)hm.get("opt");
			String standardID=(String)hm.get("standardID");
			String optType=(String)hm.get("optType");
			String typeSpec=(String)hm.get("typeSpec");
			hm.remove("optType");
			hm.remove("opt");
			hm.remove("standardID");
			hm.remove("typeSpec");
			String pkg_id=(String)this.getFormHM().get("pkg_id");
			String hfactor="";
			String s_hfactor="";
			String vfactor="";
			String s_vfactor="";
			String item="";
			String hcontent="";
			String vcontent="";
			String title="";
			if(optType==null)
			{
				if(opt==null||!"edit".equals(opt))
				{
					 hfactor=(String)this.getFormHM().get("hfactor");
					 s_hfactor=(String)this.getFormHM().get("s_hfactor");
					 vfactor=(String)this.getFormHM().get("vfactor");
					 s_vfactor=(String)this.getFormHM().get("s_vfactor");
					 item=(String)this.getFormHM().get("item");
					 hcontent=(String)this.getFormHM().get("hcontent");
					 vcontent=(String)this.getFormHM().get("vcontent");
					 //-----------都是过滤器惹的祸，转回来吧----zhaoxg 2013-6-5-----
					 hfactor = PubFunc.keyWord_reback(hfactor);
					 s_hfactor = PubFunc.keyWord_reback(s_hfactor);
					 vfactor = PubFunc.keyWord_reback(vfactor);
					 s_vfactor = PubFunc.keyWord_reback(s_vfactor);
					 item = PubFunc.keyWord_reback(item);
					 hcontent = PubFunc.keyWord_reback(hcontent);
					 vcontent = PubFunc.keyWord_reback(vcontent);
					 //----------------------
					 opt="new";
					 standardID="";
				}
				else if("edit".equals(opt))
				{
					ContentDAO dao=new ContentDAO(this.getFrameconn());
					String m_standardID=(String)hm.get("m_standardID"); 
					if(!PubFunc.decrypt(m_standardID).equalsIgnoreCase(standardID))
						throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("gz.formula.standart.overAuthority")+"!"));
					this.frowset=dao.search("select * from gz_stand_history where id="+standardID+" and pkg_id="+pkg_id);
					String gzStandardName="";
					if(this.frowset.next())
					{
						 hfactor=this.frowset.getString("hfactor");
						 s_hfactor=this.frowset.getString("s_hfactor");
						 vfactor=this.frowset.getString("vfactor");
						 s_vfactor=this.frowset.getString("s_vfactor");
						 item=this.frowset.getString("item");
						 hcontent=this.frowset.getString("hcontent");
						 vcontent=this.frowset.getString("vcontent");
						 gzStandardName=this.frowset.getString("name");
					}
					this.getFormHM().put("hfactor",hfactor);
					this.getFormHM().put("s_hfactor",s_hfactor);
					this.getFormHM().put("vfactor",vfactor);
					this.getFormHM().put("s_vfactor",s_vfactor);
					this.getFormHM().put("item",item);
					this.getFormHM().put("hcontent",hcontent);
					this.getFormHM().put("vcontent",vcontent);
					
					this.getFormHM().put("opt",opt);
					this.getFormHM().put("standardID",standardID);
					this.getFormHM().put("gzStandardName",gzStandardName);
					
					title="<table align='center' ><tr><td align='left' ><b><font size='5' >"+gzStandardName+"</font></b></td></tr></table>";
				}
				
			}
			else
			{
				hfactor=(String)this.getFormHM().get("hfactor");
				 s_hfactor=(String)this.getFormHM().get("s_hfactor");
				 vfactor=(String)this.getFormHM().get("vfactor");
				 s_vfactor=(String)this.getFormHM().get("s_vfactor");
				 item=(String)this.getFormHM().get("item");
				 hcontent=(String)this.getFormHM().get("hcontent");
				 vcontent=(String)this.getFormHM().get("vcontent");
				 //-----------都是过滤器惹的祸，转回来吧----zhaoxg 2013-6-5-----
				 hfactor = PubFunc.keyWord_reback(hfactor);
				 s_hfactor = PubFunc.keyWord_reback(s_hfactor);
				 vfactor = PubFunc.keyWord_reback(vfactor);
				 s_vfactor = PubFunc.keyWord_reback(s_vfactor);
				 item = PubFunc.keyWord_reback(item);
				 hcontent = PubFunc.keyWord_reback(hcontent);
				 vcontent = PubFunc.keyWord_reback(vcontent);
				 //----------------------
			}
			
			GzStandardItemBo bo=new GzStandardItemBo(this.getFrameconn(),hfactor,s_hfactor,hcontent,vfactor,s_vfactor,vcontent,item,standardID,opt,pkg_id);
			bo.init();
			GzStandardItemVo vo=bo.getGzStandardItemVo();
			String html=bo.getHtml(vo);
			
			
			///////////
			if(title.length()==0 && !"1".equals(typeSpec)){//防止刷新页面后标题丢失  zhaoxg add 2015-4-20
				title = (String) this.getFormHM().get("title");
			}
			
			this.getFormHM().put("title",title);
			this.getFormHM().put("gzStandardItemHtml",html);  //   title+html);
			this.getFormHM().put("gzItemList",vo.getGzItemList());
			this.getFormHM().put("gzStandardItemVo",vo);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	
}
