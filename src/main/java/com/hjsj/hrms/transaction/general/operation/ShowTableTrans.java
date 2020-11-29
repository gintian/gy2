package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.businessobject.general.operation.OperationSQLStr;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowTableTrans extends IBusiness {

	public void execute() throws GeneralException {
		try
		{
			HashMap hm=this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			/**暂时只考虑固定模板*/
			String usertype="0";
			String operationcode=(String)reqhm.get("operationcode");
			//String usertype=(String) reqhm.get("usertype");
			//reqhm.remove("usertype");
	//		查询
			String[] sqlstr=new String[4];
			if("0".equals(usertype)){
					sqlstr=OperationSQLStr.getCustomSql((String) reqhm.get("operationcode"),this.getFrameconn());
				
			}else{
					sqlstr=OperationSQLStr.getstaticSql((String) reqhm.get("operationcode"));
			}
			
			ArrayList pageList=new ArrayList();
			this.frowset=dao.search(sqlstr[0]+sqlstr[1]+sqlstr[3]);
			LazyDynaBean abean=null;
			while(this.frowset.next())
			{
				abean=new LazyDynaBean();
				
				String _tabid=this.frowset.getString("tabid");
				boolean isCorrect = false;
				
				if(userView.isSuper_admin())
					isCorrect=true;
				else
				{
	      			if (userView.isHaveResource(IResourceConstant.RSBD, _tabid))//人事移动
	      				isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(IResourceConstant.ORG_BD,
	      						_tabid))//组织变动
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(IResourceConstant.POS_BD,
	      						_tabid))//岗位变动
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(IResourceConstant.GZBD,
	      						_tabid))//工资变动
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(IResourceConstant.INS_BD,
	      						_tabid))//保险变动
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(IResourceConstant.PSORGANS,
	      						_tabid))
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(
	      						IResourceConstant.PSORGANS_FG, _tabid))
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(
	      						IResourceConstant.PSORGANS_GX, _tabid))
	      					isCorrect = true;
	      			if (!isCorrect)
	      				if (userView.isHaveResource(
	      						IResourceConstant.PSORGANS_JCG, _tabid))
	      					isCorrect = true;
				}
				if(isCorrect)
				{
					abean.set("operationname",this.frowset.getString("operationname")==null?"":this.frowset.getString("operationname"));
					abean.set("tabid",this.frowset.getString("tabid"));
					abean.set("sp_flag",this.frowset.getString("sp_flag"));
					abean.set("name",this.frowset.getString("name"));
					abean.set("tabid",this.frowset.getString("tabid"));
					pageList.add(abean);
				}
			}
			
			this.getFormHM().put("pageList",pageList);
			
			hm.put("sql",sqlstr[0]);
			hm.put("where",sqlstr[1]);
			hm.put("column",sqlstr[2]);
			hm.put("orderby",sqlstr[3]);
			hm.put("usertype",usertype);
			this.frowset = dao.search(sqlstr[0]+sqlstr[1]);
			int sp_mode =1;
			HashMap map = new HashMap();
			while(this.frowset.next()){
			String sxml =	Sql_switcher.readMemo(this.frowset,"ctrl_para");
			sp_mode = parse_xml_param(sxml);//0表示自动流转1表示手工指派
			if(sp_mode==0)
				map.put(""+this.frowset.getInt("tabid"),""+sp_mode);
			}
			hm.put("spflagmap", map);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw GeneralExceptionHandler.Handle(ex);
		}
	}
	/**
	 * 解释业务模板定义的审批方法的参数
	 * @param sxml
	 * @return
	 */
	private int  parse_xml_param(String sxml)
	{
		Document doc=null;
		Element element=null;
		int sp_mode =1;
		if(sxml==null|| "".equals(sxml))
				return 1;
		try
		{
			doc=PubFunc.generateDom(sxml);
			
			/**审批方法*/
			String xpath="/params/sp_flag";
			XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
			List childlist=findPath.selectNodes(doc);			
			if(childlist!=null&&childlist.size()>0)
			{
				element=(Element)childlist.get(0);
				sp_mode=Integer.parseInt((String)element.getAttributeValue("mode"));
			}
		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}	
		
		return sp_mode;
	}
}
