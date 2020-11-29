package com.hjsj.hrms.transaction.report.edit_report;

import com.hjsj.hrms.businessobject.report.TgridBo;
import com.hjsj.hrms.businessobject.report.TnameExtendBo;
import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.codec.SafeCode;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Table;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.sys.ResourceFactory;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.valueobject.UserView;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import java.util.ArrayList;

/**
 * 校验是否已有数据
 * @author Owner
 *
 */
public class ValidateIsPigeonholeTrans extends IBusiness {

	public void execute() throws GeneralException {
		ContentDAO dao=new ContentDAO(this.frameconn);
		try
		{
			 String tabid = (String) this.getFormHM().get("tabid");
			 String username = SafeCode.decode((String) this.getFormHM().get("username"));
				if(username==null|| "".equals(username)){
					username = this.userView.getUserName();
				}
				userView=new UserView(username, this.frameconn); 
				userView.canLogin();
			 if(!userView.isHaveResource(IResourceConstant.REPORT,tabid))
					throw GeneralExceptionHandler.Handle(new Exception(ResourceFactory.getProperty("report.noResource.info")+"!"));
			 
			String year = (String) this.getFormHM().get("year");
			String reportType = (String) this.getFormHM().get("reportType");
			String count="";
			String auto_archive=(String)this.getFormHM().get("auto_archive");
			if(Integer.parseInt(reportType)>2)
				count = (String) this.getFormHM().get("count");
			String week="";
			if(Integer.parseInt(reportType)==6)
				week = (String) this.getFormHM().get("week");
			String scopeid = (String) this.getFormHM().get("scopeid");
		
			
			String unitcode = "";
			if ("1".equals((String) this.getFormHM().get("operateObject"))) {
				this.frowset = dao
						.search("select unitcode from operUser where userName='"
								+ this.getUserView().getUserName() + "'");
				if (this.frowset.next())
					unitcode = this.frowset.getString(1);
				
			} else
			{
				unitcode = (String) this.getFormHM().get("appealUnitcode");
				
			}
			if(scopeid!=null&&!"0".equals(scopeid)){
				//判断是否存在scopeid字段
				DbWizard dbWizard=new DbWizard(this.getFrameconn());
				if(dbWizard.isExistField("ta_"+tabid, "scopeid",false)){//判断字段是否存在，没则生成，同时付默认值0
				}else{
					TgridBo tgridBo=new TgridBo(this.getFrameconn());
					Table table=new Table("ta_"+tabid);
					table.addField(tgridBo.getField2("scopeid","统计口径id","I"));
					dbWizard.addColumns(table);
					dao.update(" update ta_"+tabid+" set scopeid=0 ");
				}
				try{
				RecordVo vo = new RecordVo("tscope");
				vo.setInt("scopeid", Integer.parseInt(scopeid));
				vo = dao.findByPrimaryKey(vo);
				if(vo.getString("owner_unit").indexOf("UN")!=-1||vo.getString("owner_unit").indexOf("UM")!=-1){
					unitcode = vo.getString("owner_unit").substring(2, vo.getString("owner_unit").length()).replace("`", "");
				}
				}catch(Exception e){
					
				}
			}else{
				scopeid="0";
			}
			this.frowset=dao.search("select tsortid,xmlstyle from tname where tabid="+tabid);
			String xml="";
			if(this.frowset.next()){
				 xml=Sql_switcher.readMemo(this.frowset, "xmlstyle");
			}
			
			Document doc=null;
			if(xml!=null&&xml.trim().length()!=0){
				doc = PubFunc.generateDom(xml);
			}else{
				StringBuffer strxml=new StringBuffer();
				strxml.append("<?xml version='1.0' encoding='UTF-8' ?>");
				strxml.append("<param>");
				strxml.append("</param>");	
				xml=strxml.toString();
				doc = PubFunc.generateDom(xml);
			}
			if(doc!=null){
				XPath xPath = XPath.newInstance("/param/auto_archive");
				Element auto_archiv = (Element) xPath.selectSingleNode(doc);
				Element root = doc.getRootElement();
				if (auto_archiv != null) {
					auto_archiv.setText(auto_archive);
				}else{
					auto_archiv=new Element("auto_archive");
					auto_archiv.setText(auto_archive);
					root.addContent(auto_archiv);
				}
			}
			//PreparedStatement pstmt = null;	
			XMLOutputter outputter = new XMLOutputter();
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			outputter.setFormat(format);
			String str_value= outputter.outputString(doc);
			StringBuffer strsql = new StringBuffer();
			strsql.append("update tname set xmlstyle=? where tabid='"+tabid+"'");
			ArrayList list = new ArrayList();
			list.add(str_value);
			dao.update(strsql.toString(), list);
			/*pstmt = this.frameconn.prepareStatement(strsql.toString());	
			switch(Sql_switcher.searchDbServer())
			{
				  case Constant.MSSQL:
				  {
					  pstmt.setString(1, str_value);
					  pstmt.execute();
					  break;
				  }
				  case Constant.ORACEL:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  pstmt.execute();
					  break;
				  }
				  case Constant.DB2:
				  {
					  pstmt.setCharacterStream(1,new InputStreamReader(new ByteArrayInputStream(str_value.
					          getBytes())), str_value.length());
					  pstmt.execute();
					  break;
				  }
				 
			}*/
			
			TnameExtendBo   tnameExtendBo=new TnameExtendBo(this.frameconn);
			tnameExtendBo.setScopeid(scopeid);
			if(tnameExtendBo.isExistData(reportType,year,count,unitcode,tabid,week))
			{
				this.getFormHM().put("isData","yes");
			}
			else
			{
				this.getFormHM().put("isData","no");
			}
			this.getFormHM().put("reportType",reportType);
			this.getFormHM().put("year",year);
			if(Integer.parseInt(reportType)>2)
				this.getFormHM().put("count",count);
			if(Integer.parseInt(reportType)==6)
				this.getFormHM().put("week",week);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		

	}

}
