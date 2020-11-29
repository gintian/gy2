package com.hjsj.hrms.module.template.historydata.businessobject;

import com.hjsj.hrms.businessobject.sys.report.Sys_Oth_Parameter;
import com.hjsj.hrms.module.template.utils.TemplateDataBo;
import com.hjsj.hrms.module.template.utils.TemplateFuncBo;
import com.hjsj.hrms.module.template.utils.TemplateLayoutBo;
import com.hjsj.hrms.module.template.utils.TemplateStaticDataBo;
import com.hjsj.hrms.module.template.utils.javabean.TemplateParam;
import com.hjsj.hrms.module.template.utils.javabean.TemplateSet;
import com.hjsj.hrms.utils.PubFunc;
import com.hjsj.hrms.utils.ResourceFactory;
import com.hjsj.hrms.utils.components.tablefactory.model.ColumnsInfo;
import com.hjsj.hrms.utils.components.tablefactory.model.TableDataConfigCache;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.frame.dao.RecordVo;
import com.hrms.frame.dao.utility.DateUtils;
import com.hrms.frame.dbstruct.DataType;
import com.hrms.frame.dbstruct.DbWizard;
import com.hrms.frame.dbstruct.Field;
import com.hrms.frame.dbstruct.Table;
import com.hrms.frame.utility.AdminCode;
import com.hrms.frame.utility.IDGenerator;
import com.hrms.hjsj.sys.Constant;
import com.hrms.hjsj.sys.DataDictionary;
import com.hrms.hjsj.sys.FieldItem;
import com.hrms.hjsj.sys.IResourceConstant;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.valueobject.UserView;
import com.hrms.virtualfilesystem.service.VfsCategoryEnum;
import com.hrms.virtualfilesystem.service.VfsFiletypeEnum;
import com.hrms.virtualfilesystem.service.VfsModulesEnum;
import com.hrms.virtualfilesystem.service.VfsService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import sun.misc.BASE64Encoder;

import javax.sql.RowSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
* @Title: HistoryDataBo
* @Description:历史数据归档相关bo类
* @author: hej
* @date 2019年11月13日 下午3:44:49
* @version
 */
public class HistoryDataBo {
	private Connection conn;
    private ContentDAO dao;
    private UserView userview;
    private String rootDir = "";
    private Random random = new Random();
    public HistoryDataBo(){
    	
    }

    public HistoryDataBo(Connection conn){
    	this.conn = conn;
    }
    
    public HistoryDataBo(Connection conn, UserView userview){
    	this.conn = conn;
        this.userview = userview;
        this.dao = new ContentDAO(conn);
    }
    
    /**
     * 获得流程归档列表显示的列
     * @return
     */
    public ArrayList<ColumnsInfo> getColumn() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("tabid");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.moduleid"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(70);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("name");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.modulename"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("operationname");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.operationname"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("archive_time");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.archiveflag"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(180);
			info.setTextAlign("center");
			info.setRendererFunc("templateProcessArchiving.showArchivingTime");
			list.add(info);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
    
    /**
	 * 模板样式归档表【t_cells_archive】插入数据
	 * @param tabid
	 * @param processdate 
     * @throws GeneralException 
	 */
	public int insertIntoArchiveCells(int tabid, String processdate) throws GeneralException {
		RowSet rset = null;
		int num = 0;
		//得到与模板相关的设置数据
		StringBuffer xml = new StringBuffer();
     	xml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
     	xml.append("<data>");
     	try {
     		ContentDAO dao = new ContentDAO(this.conn);
	        RecordVo tab_vo = TemplateStaticDataBo.getTableVo(tabid,this.conn);
	        //添加template_table节点
	        this.addTemplatetable(xml,tabid,tab_vo);
	        //添加template_page节点
	        this.addTemplatepage(xml,tabid);
	        //添加template_title节点
	        this.addTemplatetitle(xml,tabid);
	        //添加template_set节点
	        this.addTemplateset(xml,tabid);
	        //添加template_signature节点
	        this.addTemplatesignature(xml,"MB_PARAM");
	        xml.append("</data>");
	        String archived_data = xml.toString();
	        String sql = "select "+Sql_switcher.isnull("MAX(id)","0")+"+1 as num from t_cells_archive";
			rset = dao.search(sql);
			while(rset.next()){
				num = rset.getInt("num");
			}
			RecordVo vo = new RecordVo("t_cells_archive");
        	vo.setInt("id", num);
        	vo.setInt("tabid", tabid);
        	vo.setString("name", tab_vo.getString("name"));
        	vo.setString("operationname", tab_vo.getString("operationname"));
        	Date date = (Date) DateUtils.getDate(processdate,"yyyy-MM-dd");
        	vo.setDate("archive_time", date);
        	vo.setString("archive_user", this.userview.getUserName());
        	vo.setString("archive_fullname", this.userview.getUserFullName());
        	vo.setString("archived_data", archived_data);
        	dao.addValueObject(vo);
     	}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
     	return num;
	}
	/**
	 * 添加template_signature节点
	 * @param xml
	 * @param constant
	 */
	private void addTemplatesignature(StringBuffer xml, String constant) {
		RowSet rset = null;
		Document doc=null;
		Element element=null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer strsql = new StringBuffer("");
			ArrayList param = new ArrayList();
			if(Sql_switcher.searchDbServer()==Constant.KUNLUN)
				   strsql.append("select str_value from \"constant\"  where \"constant\"=? ");
			    else
			    	strsql.append("select str_value from constant where constant=? ");
			param.add(constant);
			rset = dao.search(strsql.toString(),param);
			if(rset.next()) {
				String str_value = rset.getString("str_value");
				doc = PubFunc.generateDom(str_value);
				String xpath="/params";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					Element rootelement = (Element) childlist.get(0);
					List child = rootelement.getChildren();
					xml.append("<template_signature>");
					for(int i=0;i<child.size();i++) {
						element=(Element)child.get(i);
						String text = element.getName();
						if("signature_type".equals(text)) {
							xml.append("<signature_type>");
							xml.append(element.getValue());
							xml.append("</signature_type>");
						}else if("signature_usb".equals(text)) {
							xml.append("<signature_usb>");
							xml.append(element.getValue());
							xml.append("</signature_usb>");
						}
					}
					xml.append("</template_signature>");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}

	/**
	 * 添加template_table节点
	 * @param xml
	 * @param tabid
	 * @param tab_vo 
	 */
	private void addTemplatetable(StringBuffer xml, int tabid, RecordVo tab_vo) {
		String _static="static";
		if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
			_static="static_o";
		}
		xml.append("<template_table>");
		xml.append("<tabid>");
		xml.append(tab_vo.getString("tabid"));
		xml.append("</tabid>");
		
		xml.append("<name>");
		xml.append(tab_vo.getString("name"));
		xml.append("</name>");
		
		xml.append("<noticeid>");
		xml.append(tab_vo.getString("noticeid"));
		xml.append("</noticeid>");
		
		xml.append("<gzstandid>");
		xml.append(tab_vo.getString("gzstandid"));
		xml.append("</gzstandid>");
		
		xml.append("<flag>");
		xml.append(tab_vo.getString("flag"));
		xml.append("</flag>");
		
		xml.append("<static>");
		xml.append(tab_vo.getString(_static));
		xml.append("</static>");
		
		xml.append("<operationcode>");
		xml.append(tab_vo.getString("operationcode"));
		xml.append("</operationcode>");
		//获得业务类型
		int operationType = TemplateStaticDataBo.getOperationType(tab_vo.getString("operationcode"),conn);
		xml.append("<operationtype>");
		xml.append(operationType+"");
		xml.append("</operationtype>");
		
		xml.append("<operationname>");
		xml.append(tab_vo.getString("operationname"));
		xml.append("</operationname>");
		
		xml.append("<factor>");
		if(StringUtils.isNotBlank(tab_vo.getString("factor")))
			xml.append("<![CDATA["+tab_vo.getString("factor")+"]]>");
		else
			xml.append(tab_vo.getString("factor"));
		xml.append("</factor>");
		
		xml.append("<lexpr>");
		if(StringUtils.isNotBlank(tab_vo.getString("lexpr")))
			xml.append("<![CDATA["+tab_vo.getString("lexpr")+"]]>");
		else
			xml.append(tab_vo.getString("lexpr"));
		xml.append("</lexpr>");
		
		xml.append("<llexpr>");
		if(StringUtils.isNotBlank(tab_vo.getString("llexpr")))
			xml.append("<![CDATA["+tab_vo.getString("llexpr")+"]]>");
		else
			xml.append(tab_vo.getString("llexpr"));
		xml.append("</llexpr>");
		
		xml.append("<userfalg>");
		xml.append(tab_vo.getString("userfalg"));
		xml.append("</userfalg>");
		
		xml.append("<username>");
		xml.append(tab_vo.getString("username"));
		xml.append("</username>");
		
		xml.append("<userflag>");
		xml.append(tab_vo.getString("userflag"));
		xml.append("</userflag>");
		
		xml.append("<sp_flag>");
		xml.append(tab_vo.getString("sp_flag"));
		xml.append("</sp_flag>");
		
		xml.append("<dest_base>");
		xml.append(tab_vo.getString("dest_base"));
		xml.append("</dest_base>");
		
		/*xml.append("<content>");
		if(StringUtils.isNotBlank(tab_vo.getString("content")))
			xml.append("<![CDATA["+tab_vo.getString("content")+"]]>");
		else
			xml.append(tab_vo.getString("content"));
		xml.append("</content>");*/
		
		xml.append("<ctrl_para>");
		if(StringUtils.isNotBlank(tab_vo.getString("ctrl_para")))
			xml.append("<![CDATA["+tab_vo.getString("ctrl_para")+"]]>");
		else
			xml.append(tab_vo.getString("ctrl_para"));
		xml.append("</ctrl_para>");
		
		xml.append("<paperori>");
		xml.append(tab_vo.getString("paperori"));
		xml.append("</paperori>");
		
		xml.append("<paper>");
		xml.append(tab_vo.getString("paper"));
		xml.append("</paper>");
		
		xml.append("<tmargin>");
		xml.append(tab_vo.getString("tmargin"));
		xml.append("</tmargin>");
		
		xml.append("<bmargin>");
		xml.append(tab_vo.getString("bmargin"));
		xml.append("</bmargin>");
		
		xml.append("<rmargin>");
		xml.append(tab_vo.getString("rmargin"));
		xml.append("</rmargin>");
		
		xml.append("<lmargin>");
		xml.append(tab_vo.getString("lmargin"));
		xml.append("</lmargin>");
		
		xml.append("<paperw>");
		xml.append(tab_vo.getString("paperw"));
		xml.append("</paperw>");
		
		xml.append("<paperh>");
		xml.append(tab_vo.getString("paperh"));
		xml.append("</paperh>");
		xml.append("</template_table>");
	}
	
	/**
	 * 添加template_page节点
	 * @param xml
	 * @param tabid
	 */
	private void addTemplatepage(StringBuffer xml, int tabid) {
		RowSet rset = null;
		try {
			xml.append("<template_page>");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList param = new ArrayList();
			String sql = "select * from template_page where tabid=? order by pageid";
			param.add(tabid);
			rset = dao.search(sql,param);
			while(rset.next()) {
				xml.append("<record>");
				
				xml.append("<tabid>");
				xml.append(rset.getString("tabid"));
				xml.append("</tabid>");
				
				xml.append("<pageid>");
				xml.append(rset.getString("pageid"));
				xml.append("</pageid>");
				
				xml.append("<title>");
				if(StringUtils.isNotBlank(rset.getString("title")))
					xml.append("<![CDATA["+rset.getString("title")+"]]>");
				else
					xml.append(rset.getString("title"));
				xml.append("</title>");
				
				xml.append("<flag>");
				xml.append(rset.getString("flag"));
				xml.append("</flag>");
				
				xml.append("<isprn>");
				xml.append(rset.getString("isprn"));
				xml.append("</isprn>");
				
				xml.append("<ismobile>");
				xml.append(rset.getString("ismobile"));
				xml.append("</ismobile>");
				
				xml.append("<paperorientation>");
				xml.append(rset.getString("paperorientation"));
				xml.append("</paperorientation>");
				
				xml.append("<isshow>");
				xml.append(rset.getString("isshow"));
				xml.append("</isshow>");
				
				xml.append("</record>");
			}
			xml.append("</template_page>");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	
	/**
	 * 添加template_title节点
	 * @param xml
	 * @param tabid
	 */
	private void addTemplatetitle(StringBuffer xml, int tabid) {
		RowSet rset = null;
		try {
			xml.append("<template_title>");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList param = new ArrayList();
			String sql = "select * from template_title where tabid=? order by pageid,rtop,rleft";
			param.add(tabid);
			rset = dao.search(sql,param);
			while(rset.next()) {
				xml.append("<record>");
				
				xml.append("<tabid>");
				xml.append(rset.getString("tabid"));
				xml.append("</tabid>");
				
				xml.append("<pageid>");
				xml.append(rset.getString("pageid"));
				xml.append("</pageid>");
				
				xml.append("<gridno>");
				xml.append(rset.getString("gridno"));
				xml.append("</gridno>");
				
				xml.append("<hz>");
				if(StringUtils.isNotBlank(rset.getString("hz")))
					xml.append("<![CDATA["+rset.getString("hz")+"]]>");
				else
					xml.append(rset.getString("hz"));
				xml.append("</hz>");
				
				xml.append("<rleft>");
				xml.append(rset.getString("rleft"));
				xml.append("</rleft>");
				
				xml.append("<rtop>");
				xml.append(rset.getString("rtop"));
				xml.append("</rtop>");
				
				xml.append("<rwidth>");
				xml.append(rset.getString("rwidth"));
				xml.append("</rwidth>");
				
				xml.append("<rheight>");
				xml.append(rset.getString("rheight"));
				xml.append("</rheight>");
				
				xml.append("<fontsize>");
				xml.append(rset.getString("fontsize"));
				xml.append("</fontsize>");
				
				xml.append("<fontname>");
				xml.append(rset.getString("fontname"));
				xml.append("</fontname>");
				
				xml.append("<fonteffect>");
				xml.append(rset.getString("fonteffect"));
				xml.append("</fonteffect>");
				
				xml.append("<flag>");
				xml.append(rset.getString("flag"));
				xml.append("</flag>");
			
				xml.append("<extendattr>");
				if(StringUtils.isNotBlank(rset.getString("extendattr")))
					xml.append("<![CDATA["+rset.getString("extendattr")+"]]>");
				else
					xml.append(rset.getString("extendattr"));
				xml.append("</extendattr>");
				
				/*xml.append("<content>");
				if(StringUtils.isNotBlank(rset.getString("content")))
					xml.append("<![CDATA["+rset.getString("content")+"]]>");
				else
					xml.append(rset.getString("content"));
				xml.append("</content>");*/
				
				xml.append("</record>");
			}
			xml.append("</template_title>");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	
	/**
	 * 添加template_set节点
	 * @param xml
	 * @param tabid
	 */
	private void addTemplateset(StringBuffer xml, int tabid) {
		RowSet rset = null;
		try {
			xml.append("<template_set>");
			ContentDAO dao = new ContentDAO(this.conn);
			ArrayList param = new ArrayList();
			String sql = "select * from template_set where tabid=? order by pageid,rtop,rleft";
			param.add(tabid);
			rset = dao.search(sql,param);
			while(rset.next()) {
				xml.append("<record>");
				
				xml.append("<tabid>");
				xml.append(rset.getString("tabid"));
				xml.append("</tabid>");
				
				xml.append("<pageid>");
				xml.append(rset.getString("pageid"));
				xml.append("</pageid>");
				
				xml.append("<gridno>");
				xml.append(rset.getString("gridno"));
				xml.append("</gridno>");
				
				xml.append("<hz>");
				if(StringUtils.isNotBlank(rset.getString("hz")))
					xml.append("<![CDATA["+rset.getString("hz")+"]]>");
				else
					xml.append(rset.getString("hz"));
				xml.append("</hz>");
				
				xml.append("<field_name>");
				xml.append(rset.getString("field_name"));
				xml.append("</field_name>");
				
				xml.append("<field_type>");
				xml.append(rset.getString("field_type"));
				xml.append("</field_type>");
				
				xml.append("<field_hz>");
				if(StringUtils.isNotBlank(rset.getString("field_hz")))
					xml.append("<![CDATA["+rset.getString("field_hz")+"]]>");
				else
					xml.append(rset.getString("field_hz"));
				xml.append("</field_hz>");
				
				xml.append("<codeid>");
				xml.append(rset.getString("codeid"));
				xml.append("</codeid>");
				
				xml.append("<flag>");
				xml.append(rset.getString("flag"));
				xml.append("</flag>");
				
				xml.append("<rtop>");
				xml.append(rset.getString("rtop"));
				xml.append("</rtop>");
				
				xml.append("<rleft>");
				xml.append(rset.getString("rleft"));
				xml.append("</rleft>");
				
				xml.append("<rwidth>");
				xml.append(rset.getString("rwidth"));
				xml.append("</rwidth>");
				
				xml.append("<rheight>");
				xml.append(rset.getString("rheight"));
				xml.append("</rheight>");
				
				xml.append("<setname>");
				xml.append(rset.getString("setname"));
				xml.append("</setname>");
				
				xml.append("<fontname>");
				xml.append(rset.getString("fontname"));
				xml.append("</fontname>");
				
				xml.append("<fontsize>");
				xml.append(rset.getString("fontsize"));
				xml.append("</fontsize>");
				
				xml.append("<fonteffect>");
				xml.append(rset.getString("fonteffect"));
				xml.append("</fonteffect>");
				
				xml.append("<formula>");
				if(StringUtils.isNotBlank(rset.getString("formula")))
					xml.append("<![CDATA["+rset.getString("formula")+"]]>");
				else
					xml.append(rset.getString("formula"));
				xml.append("</formula>");
				
				xml.append("<l>");
				xml.append(rset.getString("l"));
				xml.append("</l>");
				
				xml.append("<t>");
				xml.append(rset.getString("t"));
				xml.append("</t>");
				
				xml.append("<r>");
				xml.append(rset.getString("r"));
				xml.append("</r>");
				
				xml.append("<b>");
				xml.append(rset.getString("b"));
				xml.append("</b>");
				
				xml.append("<sl>");
				xml.append(rset.getString("sl"));
				xml.append("</sl>");
				
				xml.append("<align>");
				xml.append(rset.getString("align"));
				xml.append("</align>");
				
				xml.append("<chgstate>");
				xml.append(rset.getString("chgstate"));
				xml.append("</chgstate>");
				
				xml.append("<hismode>");
				xml.append(rset.getString("hismode"));
				xml.append("</hismode>");
				
				xml.append("<disformat>");
				xml.append(rset.getString("disformat"));
				xml.append("</disformat>");

				xml.append("<nsort>");
				xml.append(rset.getString("nsort"));
				xml.append("</nsort>");
				
				xml.append("<rcount>");
				xml.append(rset.getString("rcount"));
				xml.append("</rcount>");
				
				/*xml.append("<mode>");
				xml.append(rset.getString("mode"));
				xml.append("</mode>");*/
				
				xml.append("<subflag>");
				xml.append(rset.getString("subflag"));
				xml.append("</subflag>");
				
				xml.append("<sub_domain>");
				if(StringUtils.isNotBlank(rset.getString("sub_domain")))
					xml.append("<![CDATA["+rset.getString("sub_domain")+"]]>");
				else
					xml.append(rset.getString("sub_domain"));
				xml.append("</sub_domain>");
				
				xml.append("<yneed>");
				xml.append(rset.getString("yneed"));
				xml.append("</yneed>");
				
				xml.append("<nhide>");
				xml.append(rset.getString("nhide"));
				xml.append("</nhide>");
				
				xml.append("</record>");
			}
			xml.append("</template_set>");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	
	/**
	 * 各单据记录JSON格式数据文件存储
	 * @param tabid
	 * @param year
	 * @param ins_id
	 * @param task_id 
	 * @param j
	 * @param archivecellid 
	 * @throws GeneralException 
	 */
	public void saveDataTocells(int tabid, int year, int ins_id, int task_id, int j, int archivecellid) throws GeneralException {
		RowSet rset = null;
		RowSet rset1 = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
			Sys_Oth_Parameter sysbo = new Sys_Oth_Parameter(this.conn);
            String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS, "0", "name");
            HashMap<String,String> structMap = new HashMap();
            rset = dao.search("select * from templet_"+tabid+" where 1=2");
			ResultSetMetaData resultSetMetaData = rset.getMetaData();
			for(int i=1;i<=resultSetMetaData.getColumnCount();i++){
				String columnName = resultSetMetaData.getColumnName(i).toLowerCase();
				int columnType = resultSetMetaData.getColumnType(i);
				switch(columnType)
				{
					case java.sql.Types.BIGINT:
					case java.sql.Types.INTEGER:
					case java.sql.Types.DOUBLE:
					case java.sql.Types.NUMERIC:
						structMap.put(columnName, "N");
						break;
					case java.sql.Types.TIMESTAMP:
					case java.sql.Types.DATE:
					case java.sql.Types.TIME :
						structMap.put(columnName, "D");
						break;
					case java.sql.Types.VARCHAR:
						structMap.put(columnName, "A");
						break;	
					case java.sql.Types.CLOB:
					case java.sql.Types.LONGVARCHAR:
					case java.sql.Types.LONGVARBINARY:
						structMap.put(columnName, "M");
						break;
				}
			}
			//将数据搞成json格式数据
			String sql = "select * from templet_"+tabid+" where ins_id=?";
			ArrayList param = new ArrayList();
			param.add(ins_id);
			rset = dao.search(sql,param);
			RecordVo vo=new RecordVo("templet_"+tabid);
			TemplateDataBo dataBo = new TemplateDataBo(this.conn,this.userview, tabid);
			TemplateParam paramBo = new TemplateParam(this.conn,this.userview, tabid);
			ArrayList allCellList = dataBo.getUtilBo().getAllCell(tabid);
			boolean isHaveB0110 = false;
			boolean isHaveE0122 = false;
			for(int i=0;i<allCellList.size();i++) {
        		TemplateSet setbo = (TemplateSet) allCellList.get(i);
        		if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
					setbo.setFlag("H");
        		if("H".equalsIgnoreCase(setbo.getFlag())) {
        			continue;
        		}
        		String fldname  = setbo.getTableFieldName();
        		if("b0110_2".equalsIgnoreCase(fldname)) {
        			isHaveB0110 = true;
        		}else if("e0122_2".equalsIgnoreCase(fldname)) {
        			isHaveE0122 = true;
        		}
			}
			int number = 0;
			while(rset.next()) {
				JSONObject jsonData = new JSONObject();
				String a0101="a0101_1";
				if(paramBo.getOperationType()==0&&vo.hasAttribute("a0101_2")){
					a0101="a0101_2";
				}
				if(paramBo.getInfor_type()==2||paramBo.getInfor_type()==3){
					a0101="codeitemdesc_1";
					if(paramBo.getOperationType()==5)
						a0101="codeitemdesc_2";
				}
				String name = rset.getString(a0101);//人员姓名或机构名称
				String b0110code = "b0110_2";
				if(!isHaveB0110)
					b0110code = "b0110_1";
				String e0122 = "";
				if(paramBo.getTemplateStatic()==10) {
					b0110code = "b0110";
				}else if(paramBo.getTemplateStatic()==11) {
					b0110code = "e01a1";
				}
				String b0110 = rset.getString(b0110code);
				String record_id = this.getMaxEitId();
				String nbase = "";
				String only_value = "";
				String a0100 = "";
				int record_type = 2;
				String seq = "";
				String objectid = b0110;
				if(paramBo.getInfor_type()==1) {
					String e0122code = "e0122_2";
					if(!isHaveE0122)
						e0122code = "e0122_1";
					e0122 = rset.getString(e0122code);
					record_type = 1;
					nbase = rset.getString("basepre");
					a0100 = rset.getString("a0100");
					seq = rset.getString("a0000");	
					if(StringUtils.isNotBlank(onlyname)) {
						String onlyname_ = onlyname+"_1";
						if(paramBo.getOperationType()==0)
							onlyname_ = onlyname_+"_2";
						if(vo.hasAttribute(onlyname_))
							only_value = rset.getString(onlyname_);
						else {
							//查询人员库
							if(paramBo.getOperationType()!=0) {
								sql = "select "+onlyname+" from "+nbase+"A01 where a0100=?"; 
								param.clear();
								param.add(a0100);
								rset1 = dao.search(sql,param);
								if(rset1.next()) {
									only_value = rset1.getString(onlyname);
								}
							}
						}
					}
					jsonData.put("a0100", a0100);
					jsonData.put("basepre", nbase);
					jsonData.put("a0000", seq);
					objectid = a0100;
					/*if(StringUtils.isBlank(b0110)) {
						b0110 = rset.getString("b0110_1");
					}
					if(StringUtils.isBlank(e0122)) {
						e0122 = rset.getString("e0122_1");
					}*/
				}else {
					jsonData.put(b0110code, b0110);
				}
				RecordVo recordVo = new RecordVo("t_data_"+year);
				recordVo.setString("record_id", record_id);
				recordVo.setString("name", name);
				recordVo.setString("only_value", only_value);
				recordVo.setInt("ins_id", ins_id);
				recordVo.setInt("task_id", task_id);
				recordVo.setInt("record_type", record_type);
				recordVo.setString("seq", seq);
				recordVo.setInt("tabid", tabid);
				recordVo.setString("b0110", b0110);
				recordVo.setString("e0122", e0122);
				recordVo.setString("nbase", nbase);
				recordVo.setInt("archive_id", archivecellid);
	        	//将指标对应数据保存到json文件中
	        	for(int i=0;i<allCellList.size();i++) {
	        		TemplateSet setbo = (TemplateSet) allCellList.get(i);
	        		if(setbo.getFlag()==null|| "".equalsIgnoreCase(setbo.getFlag()))
						setbo.setFlag("H");
	        		if("H".equalsIgnoreCase(setbo.getFlag())) {
	        			continue;
	        		}
	        		String fldname  = setbo.getTableFieldName();
					if(StringUtils.isNotBlank(fldname)) {
						fldname =fldname.toLowerCase();
						fldname ="photo".equals(fldname)?"fileid":fldname;
					}
					
					int disformat=setbo.getDisformat();
					String value = "";
					//个人附件或者公共附件单独处理
					if(!structMap.containsKey(fldname)&&!"F".equalsIgnoreCase(setbo.getFlag())) {
						jsonData.put(fldname, value);
						continue;
					}
					if(setbo.isABKItem()) {
						if("M".equals(setbo.getField_type())) {
							value = Sql_switcher.readMemo(rset,fldname);
						}else if("N".equals(setbo.getField_type())) {
							value = rset.getString(fldname);
						}else if("D".equals(setbo.getField_type())) {
							String realType = structMap.get(fldname);
							if("M".equals(realType)) {
								value = Sql_switcher.readMemo(rset,fldname);
							}else {
								if (rset.getTimestamp(fldname) != null) {
									if(disformat==25){
										value=dateFormat.format(rset.getTimestamp(fldname)); 
									}
									else{
										value=dateFormat2.format(rset.getTimestamp(fldname)); 
									}
									if(StringUtils.isNotBlank(value))
										value=dataBo.getUtilBo().getFormatDate(value,disformat);
								}
							}
						}else {
							value = rset.getString(fldname);
						}
					}else if("P".equals(setbo.getFlag())){//照片
						InputStream in = null;
						String ext=rset.getString("ext");
		                if(ext==null|| "".equalsIgnoreCase(ext)){
		                	value = "";
		                	ext = "";
		                }            
		                if(StringUtils.isNotEmpty(rset.getString("fileid"))) {
		                	if("2".equals(VfsService.getFileEntity(rset.getString("fileid")).getStatus())) {//文件存在
		                		in=VfsService.getFile(rset.getString("fileid"));
		                	}
		                }else {
		                	in = rset.getBinaryStream("photo");
		                }
		                
		                if(in!=null) {
		                	byte[] output = steamToByte(in);
			                BASE64Encoder encoder = new BASE64Encoder();
			                value = encoder.encode(output);
		                }else {
		                	value = "";
		                }
		                jsonData.put("ext", ext);
					}else if("S".equals(setbo.getFlag())){//签章
						value = rset.getString(fldname);
					}else if("F".equals(setbo.getFlag())){//附件 特殊处理
						this.getAttachment(setbo,objectid,nbase,ins_id,tabid,paramBo,jsonData);
					}else {
						value = rset.getString(fldname);
					}
					jsonData.put(fldname, value);
	        	}
	        	//TEMPLATE_ARCHIVE  /  Y_年份 /  T_模板ID_1,2 /  INS_实例ID / p_6位随机数.json
	        	String random = getRandomID(6);
	        	/*String file_patch = "TEMPLATE_ARCHIVE"+File.separator+"Y_"+year+File.separator+"T_"+tabid+"_"+j+
	        			File.separator+"INS_"+ins_id+File.separator+"p_"+random+number+".json";*/
	        	String file_name="Y_"+year+"_T_"+tabid+"_"+j+"_ins_"+ins_id+"_p_"+random+number+".json";
	        	String fileid=this.saveFilePatch(jsonData.toString(),/*rootDir+*/file_name);
	        	recordVo.setString("file_patch", fileid);
	        	dao.addValueObject(recordVo);
	        	number++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		} finally {
			PubFunc.closeDbObj(rset);
			PubFunc.closeDbObj(rset1);
		}
	}
	
	/**
	 * 给table添加字段
	 * @param table
	 */
	public void addFieldItem(Table table) {
		Field temp = null;
		temp=new Field("record_id",ResourceFactory.getProperty("template.processArchiving.recordid"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(false);
		temp.setKeyable(true);
		table.addField(temp);
		
		temp=new Field("archive_id",ResourceFactory.getProperty("template.processArchiving.archiveid"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(false);
		temp.setSortable(true);
		table.addField(temp);
		
		temp=new Field("name",ResourceFactory.getProperty("template.processArchiving.name"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(100);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("only_value",ResourceFactory.getProperty("template.processArchiving.onlyid"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("ins_id",ResourceFactory.getProperty("template.processArchiving.insid"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("record_type",ResourceFactory.getProperty("template.processArchiving.recordtype"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("seq",ResourceFactory.getProperty("template.processArchiving.seqnum"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("tabid",ResourceFactory.getProperty("template.processArchiving.moduleid"));
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("b0110",ResourceFactory.getProperty("template.processArchiving.orgname"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("e0122","e0122");
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("nbase",ResourceFactory.getProperty("template.processArchiving.nbase"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(50);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("file_patch",ResourceFactory.getProperty("template.processArchiving.filepatch"));
		temp.setDatatype(DataType.STRING);
		temp.setLength(200);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
		
		temp=new Field("task_id","task_id");
		temp.setDatatype(DataType.INT);
		temp.setLength(30);
		temp.setVisible(false);
		temp.setNullable(true);
		temp.setSortable(false);
		table.addField(temp);
	}
	
	/**
     * 得到主键值
     * @return
     */
	public String getMaxEitId()
    {
		String record_id = "";
        StringBuffer sql = new StringBuffer();
        sql.append("select * from id_factory where sequence_name='TEMPLATE_DATA.ID'");
        RowSet rs=null;
        try{
            ContentDAO dao =new ContentDAO(this.conn);
            rs=dao.search(sql.toString());
            if(!rs.next()){
                StringBuffer insertSQL=new StringBuffer();
                insertSQL.append("insert into id_factory  (sequence_name, sequence_desc, minvalue, maxvalue, auto_increase, increase_order, prefix, suffix, currentid, id_length, increment_O)");
                insertSQL.append(" values ('TEMPLATE_DATA.ID', '"+ResourceFactory.getProperty("template.processArchiving.archiveid")+"', 1, 99999999, 1, 1, Null, Null, 0, 8, 1)");
                ArrayList list=new ArrayList();
                dao.insert(insertSQL.toString(),list);              
            }
            IDGenerator idg = new IDGenerator(2, this.conn);
            record_id = idg.getId("TEMPLATE_DATA.ID");
        }catch(Exception e){
           e.printStackTrace();
        }finally {
        	PubFunc.closeDbObj(rs);
        }
        return  record_id;
    }
	
	public static byte[] steamToByte(InputStream input) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = 0;
        byte[] b = new byte[1024];
        while ((len = input.read(b, 0, b.length)) != -1) {                     
            baos.write(b, 0, len);
        }
        byte[] buffer =  baos.toByteArray();
        return buffer;
	}
	
	/**
	 * 查询附件
	 * @param setbo
	 * @param objectid
	 * @param basepre
	 * @param ins_id
	 * @param tabid
	 * @param paramBo 
	 * @param jsonData 
	 */
	public void getAttachment(TemplateSet setbo, String objectid, String basepre, int ins_id, int tabid, TemplateParam paramBo, JSONObject jsonData) {
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			StringBuffer sb = new StringBuffer("");
			String attachmenttype = setbo.getAttachmentType();
			InputStream in = null;
			if("0".equals(attachmenttype)){//公共附件
				sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
				sb.append(ins_id);
				sb.append(" and t.tabid=");
				sb.append(tabid);
				sb.append(" and (t.attachmenttype=0 or t.attachmenttype is null) and (state=0 or state is null) ");
			}else if("1".equals(attachmenttype)&&objectid.length()>0){//个人附件
				sb.append("select t.*,m.sortname from t_wf_file t left join mediasort m on t.filetype=m.id where t.ins_id=");
				sb.append(ins_id);
				sb.append(" and t.tabid=");
				sb.append(tabid);
				sb.append(" and t.attachmenttype=1");
				sb.append(" and t.objectid='");
				sb.append(objectid);
				sb.append("' and (state=0 or state is null) ");
				if(StringUtils.isNotBlank(basepre)){
					sb.append(" and t.basepre='");
					sb.append(basepre);
					sb.append("'");
				}
			}
			String sub_domain=setbo.getXml_param();
			if(StringUtils.isNotBlank(sub_domain)){
				Document doc=null;
				Element element=null;
				doc = PubFunc.generateDom(sub_domain);
				String xpath="/sub_para/para";
				XPath findPath = XPath.newInstance(xpath);// 取得符合条件的节点
				List childlist=findPath.selectNodes(doc);	
				if(childlist!=null&&childlist.size()>0)
				{
					element=(Element) childlist.get(0);
					String file_type=(String)element.getAttributeValue("file_type");
					if(StringUtils.isNotBlank(file_type)){
						sb.append(" and m.flag='"+file_type+"'");
					}
				}
			}
			if(sb.length()>0){
				JSONArray jsonArray = new JSONArray();
				sb.append(" order by file_id");
				rset = dao.search(sb.toString());
				while (rset.next()) {
					JSONObject json = new JSONObject();
					json.put("file_id", rset.getString("file_id"));
					json.put("name", rset.getString("name"));
					json.put("sortname", rset.getString("sortname"));
					json.put("ext", rset.getString("ext"));
					json.put("ins_id", rset.getString("ins_id"));
					json.put("filetype", rset.getString("filetype"));
					Date d_create=rset.getDate("create_time");
					String d_str=DateUtils.format(d_create,"yyyy-MM-dd hh:mm:ss");
					json.put("create_time", d_str);
					String name = rset.getString("fullname");
					String user_name = rset.getString("create_user");
					json.put("create_user", user_name);
					json.put("fullname", name);
					in = rset.getBinaryStream("content");
					String content = "";
	                if(in!=null) {
	                	byte[] output = steamToByte(in);
		                BASE64Encoder encoder = new BASE64Encoder();
		                content = encoder.encode(output);
	                }else
	                	content = "";
	                json.put("content", content);
					json.put("tabid", rset.getString("tabid"));
					json.put("basepre", rset.getString("basepre"));
					json.put("attachmenttype", rset.getString("attachmenttype"));
					json.put("objectid", rset.getString("objectid"));
					json.put("filepath", rset.getString("filepath"));
					json.put("state", rset.getString("state"));
					json.put("i9999", rset.getString("i9999"));
					jsonArray.add(json);
				}
				jsonData.put("t_wf_file_"+attachmenttype, jsonArray);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	
	private String saveFilePatch(String jsonString, String file_patch) {
			InputStream in=null;
			String fileid="";
		try {
           /* // 保证创建一个新文件
            File file = new File(file_patch);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();*/
			byte[] bt=jsonString.getBytes();
			jsonString.getBytes("UTF-8");
			in = new ByteArrayInputStream(bt);
            // 将格式化后的字符串写入文件
			fileid = VfsService.addFile(this.userview.getUserName(), VfsFiletypeEnum.multimedia,
					VfsModulesEnum.RS, VfsCategoryEnum.other, "", in, file_patch, "", false);
			
           /* Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();*/
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	PubFunc.closeIoResource(in);
        }
		return fileid;
	}
	
	/**
	 * 复制t_wf_task 以及t_wf_task_objlink
	 * @param processdate
	 * @param insList 
	 * @throws GeneralException 
	 */
	public void copyData2Archive(String processdate, ArrayList insList) throws GeneralException {
		RowSet rset = null;
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			DbWizard dbwizard=new DbWizard(this.conn);
			//查询归档时间之前流程数据都包含的年份
			String sql = "select "+Sql_switcher.year("start_date")+" year from t_wf_instance where 1=1 "+PubFunc.getDateSql("<=","start_date",processdate)+" group by "+Sql_switcher.year("start_date") +" order by "+Sql_switcher.year("start_date");
			rset = dao.search(sql);
			while(rset.next()) {
				int year = rset.getInt("year");
				//复制表t_wf_task和t_wf_task_objlink
				if(!dbwizard.isExistTable("t_wf_task_"+year,false)) {
					sql = "select * into t_wf_task_"+year+" from t_wf_task where 1=2";//复制表结构
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql = "create table t_wf_task_"+year+" as select * from t_wf_task where 1=2";
					dao.update(sql);
					sql = "alter table t_wf_task_"+year+" add primary key(task_id)";//添加主键
					dao.update(sql);
				}
				//复制数据
				sql = "insert into t_wf_task_"+year+" (task_id,task_topic,node_id,actorid,actor_type,actorname,ins_id,start_date,end_date,"
						+ "task_type,task_pri,bs_flag,bread,A0100,A0101,A0100_1,A0101_1,sp_yj,content,state,task_state,url_addr,params,appuser,"
						+ "flag,pri_task_id,task_id_pro,originate_id) select a.task_id,a.task_topic,a.node_id,a.actorid,a.actor_type,"
						+ "a.actorname,a.ins_id,a.start_date,a.end_date,a.task_type,a.task_pri,a.bs_flag,a.bread,a.A0100,a.A0101,a.A0100_1,a.A0101_1,a.sp_yj,a.content,a.state,"
						+ "a.task_state,a.url_addr,a.params,a.appuser,a.flag,a.pri_task_id,a.task_id_pro,a.originate_id from t_wf_task a,t_wf_instance b where a.ins_id=b.ins_id and "+Sql_switcher.year("b.start_date")+"="+year+" and b.ins_id =?";
				dao.batchUpdate(sql,insList);
				if(!dbwizard.isExistTable("t_wf_task_objlink_"+year,false)) {
					sql = "select * into t_wf_task_objlink_"+year+" from t_wf_task_objlink where 1=2";//复制表结构
					if(Sql_switcher.searchDbServer()==Constant.ORACEL)
						sql = "create table t_wf_task_objlink_"+year+" as select * from t_wf_task_objlink where 1=2";
					dao.update(sql);
					sql = "alter table t_wf_task_objlink_"+year+" add primary key(ins_id,task_id,tab_id,node_id,seqnum)";//添加主键
					dao.update(sql);
				}
				//复制数据
				sql = "insert into t_wf_task_objlink_"+year+" (seqnum,ins_id,task_id,node_id,tab_id,username,submitflag,flag,count,state,special_node,"
						+ "task_type,locked_time) select a.seqnum,a.ins_id,a.task_id,a.node_id,a.tab_id,a.username,"
						+ "a.submitflag,a.flag,a.count,a.state,a.special_node,a.task_type,a.locked_time from t_wf_task_objlink a,t_wf_instance b where a.ins_id=b.ins_id and "+Sql_switcher.year("b.start_date")+"="+year+" and b.ins_id =?";
				dao.batchUpdate(sql,insList);
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw GeneralExceptionHandler.Handle(e);
		}finally {
			PubFunc.closeDbObj(rset);
		}
	}
	
	/**
	 * 获得符合查询条件的历史数据
	 * @param tableCache
	 * @return
	 */
	public ArrayList getAllRecordList(TableDataConfigCache tableCache) {
		RowSet rset = null;
		ArrayList recordList = new ArrayList();
		try {
			String format_str="yyyy-MM-dd HH:mm:ss";
			if(Sql_switcher.searchDbServer()==Constant.ORACEL)
				format_str="yyyy-MM-dd hh24:mi:ss";
			String tableSql = tableCache.getTableSql();
	    	String querySql = tableCache.getQuerySql();
	    	String sql = "select record_id,name,ins_id,tabid,b0110,nbase,e0122,only_value,year,task_id,tablename,archive_id,"+
	    			Sql_switcher.dateToChar("start_date",format_str)+" start_date,"+Sql_switcher.dateToChar("end_date",format_str)+
	    			" end_date from ("+tableSql+") aa where 1=1 ";
	    	if(StringUtils.isNotBlank(querySql)) {
	    		sql += querySql;
	    	}
			ContentDAO dao = new ContentDAO(this.conn);
			rset = dao.search(sql);
			while(rset.next()) {
				HashMap map = new HashMap();
				String record_id = rset.getString("record_id");
				String name = rset.getString("name");
				String ins_id = rset.getString("ins_id");
				String tabid = rset.getString("tabid");
				String b0110 = rset.getString("b0110");
				String e0122 = rset.getString("e0122");
				b0110 = b0110==null?"":b0110;
				b0110 = AdminCode.getCodeName("UN",b0110);
				e0122 = e0122==null?"":e0122;
				e0122 = AdminCode.getCodeName("UM",e0122);
				String nbase = rset.getString("nbase");
				String only_value = rset.getString("only_value");
				String start_date = rset.getString("start_date");
				start_date=start_date==null?"":start_date;
				String end_date = rset.getString("end_date");
				end_date=end_date==null?"":end_date;
				String year = rset.getString("year");
				String task_id = rset.getString("task_id");
				String tablename = rset.getString("tablename");
				String archive_id = rset.getString("archive_id");
				map.put("record_id", record_id);
				map.put("name", name);
				map.put("ins_id", ins_id);
				map.put("b0110", b0110);
				map.put("e0122", e0122);
				map.put("nbase", nbase);
				map.put("only_value", only_value==null?"":only_value);
				map.put("start_date", start_date);
				map.put("end_date", end_date);
				map.put("year", year);
				map.put("task_id", task_id);
				map.put("tablename", tablename);
				map.put("tabid", tabid);
				map.put("archive_id", archive_id);
				recordList.add(map);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return recordList;
	}
	
	/**
	 * 获得历史数据列表列
	 * @return
	 */
	public ArrayList<ColumnsInfo> getHisColumn() {
		ArrayList<ColumnsInfo> list = new ArrayList<ColumnsInfo>();
		try
		{
			FieldItem item = new FieldItem();
			item.setItemid("nbase");
			item.setItemdesc(ResourceFactory.getProperty("label.dbase"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			ColumnsInfo info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("b0110");
			item.setItemdesc(ResourceFactory.getProperty("b0110.label"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("UN");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("e0122");
			item.setItemdesc(ResourceFactory.getProperty("hrmsNew.e0122"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("UM");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("name");
			item.setItemdesc(ResourceFactory.getProperty("label.title.name"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(50);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			/***
			 * 唯一性指标名称
			 */
			Sys_Oth_Parameter sysbo=new Sys_Oth_Parameter(this.conn);
			String onlyname = sysbo.getCHKValue(Sys_Oth_Parameter.CHK_UNIQUENESS,"0","name");//唯一标识 唯一标识关联人员
			FieldItem item_key=null;
			String desc_key=ResourceFactory.getProperty("template.processArchiving.onlyfieldcode");
			if(StringUtils.isNotEmpty(onlyname)) {
				item_key=DataDictionary.getFieldItem(onlyname);
				if(StringUtils.isNotEmpty(item_key.getItemdesc())) {
					desc_key=item_key.getItemdesc();
				}
			}
			item = new FieldItem();
			item.setItemid("only_value");
			item.setItemdesc(desc_key);
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("tablename");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.templatename"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("ins_id");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.instanceid"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("start_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.applyStartDate"));
			item.setItemtype("D");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setColumnLength(20);
			info.setDisFormat("Y-m-d H:i:s");
            info.setColumnType("D");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("end_date");
			item.setItemdesc(ResourceFactory.getProperty("general.template.spEndDate"));
			item.setItemtype("D");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setColumnLength(20);
			info.setDisFormat("Y-m-d H:i:s");
			info.setColumnType("D");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("print");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.showprint"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(100);
			info.setTextAlign("center");
			info.setRendererFunc("TemplateHistoryData.showPrint");
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("record_id");
			item.setItemdesc(ResourceFactory.getProperty("lable.menu.main.id"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("year");
			item.setItemdesc(ResourceFactory.getProperty("datestyle.year"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("tabid");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.templateno"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("task_id");
			item.setItemdesc("task_id");
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			info.setEncrypted(true);
			list.add(info);
			
			item = new FieldItem();
			item.setItemid("archive_id");
			item.setItemdesc(ResourceFactory.getProperty("template.processArchiving.archiveid"));
			item.setItemtype("A");
			item.setReadonly(true);
			item.setItemlength(80);
			item.setCodesetid("0");
			info = new ColumnsInfo(item);
			info.setEditableValidFunc("false");
			info.setColumnWidth(150);
			info.setLoadtype(ColumnsInfo.LOADTYPE_ONLYLOAD);
			list.add(info);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获得历史数据列表导航栏
	 * @param module_id 
	 * @return
	 */
	public ArrayList getButtonList(String module_id) {
		ArrayList buttons = new ArrayList();
        ArrayList<LazyDynaBean> menuList = new ArrayList<LazyDynaBean>();
        LazyDynaBean buttonInfo = null;
        ArrayList list = new ArrayList();
        list.add(getMenuBean(ResourceFactory.getProperty("template.processArchiving.outall"),"outall",
                "TemplateHistoryData.export_his(0)","",new ArrayList()));
        list.add(getMenuBean(ResourceFactory.getProperty("template.processArchiving.outpart"),"outpart",
                "TemplateHistoryData.export_his(1)","",new ArrayList()));
        
        LazyDynaBean oneBean = new LazyDynaBean();
        oneBean.set("text", ResourceFactory.getProperty("template.processArchiving.outexcel"));
        oneBean.set("menu", list);
		if (("1".equals(module_id) && TemplateFuncBo.haveFunctionIds("320261", this.userview))
				|| ("2".equals(module_id) && TemplateFuncBo.haveFunctionIds("3240101231", this.userview))
				|| ("4".equals(module_id) && TemplateFuncBo.haveFunctionIds("3250101231", this.userview))) {
			menuList.add(oneBean);
		}
        //流程归档
        buttonInfo = new LazyDynaBean();
        buttonInfo.set("id", "archiveData");
        buttonInfo.set("text", ResourceFactory.getProperty("template.processArchiving"));
        buttonInfo.set("handler", "TemplateHistoryData.processArchiving()");
        if (("1".equals(module_id) && TemplateFuncBo.haveFunctionIds("320262", this.userview))
				|| ("2".equals(module_id) && TemplateFuncBo.haveFunctionIds("3240101232", this.userview))
				|| ("4".equals(module_id) && TemplateFuncBo.haveFunctionIds("3250101232", this.userview))){
        	menuList.add(buttonInfo);
        }
        if (menuList.size() > 0) {
            //功能导航
            String menu = TemplateLayoutBo.getMenuStr(ResourceFactory.getProperty("gz_new.gz_accounting.FunctionNavigation"), "archivebar", menuList);
            buttons.add(menu);
        }
		return buttons;
	}
	
	public LazyDynaBean getMenuBean(String text,String id,String handler,String icon,ArrayList list){
		LazyDynaBean bean = new LazyDynaBean();
		try{
			if(text!=null&&text.length()>0)
				bean.set("text", text);
			if(id!=null&&id.length()>0)
				bean.set("id", id);
			if(icon!=null&&icon.length()>0)
				bean.set("icon", icon);
			if(handler!=null&&handler.length()>0){
				if(list!=null&&list.size()>0){
					bean.set("menu", list);
				}else{
					bean.set("handler", handler);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}
	/**
	 * 求权限范围下的模板串
	 * @param module_id 
	 * @return
	 */
	public String getTemplates(String module_id) {
		StringBuffer mb=new StringBuffer();
		if("1".equals(module_id)||"3".equals(module_id)||"6".equals(module_id)||"12".equals(module_id)
				||"5".equals(module_id)||"10".equals(module_id)||"11".equals(module_id)) {//人事异动
			String rsbd=this.userview.getResourceString(IResourceConstant.RSBD);
			mb.append(rsbd);
			mb.append(",");
		}else if("2".equals(module_id)) {//工资
			String gzbd=this.userview.getResourceString(IResourceConstant.GZBD);
			mb.append(gzbd);
			mb.append(",");
		}else if("4".equals(module_id)) {//保险
			String bybd=this.userview.getResourceString(IResourceConstant.INS_BD);
			mb.append(bybd);
			mb.append(",");
		}else if("7".equals(module_id)) {//机构
			String orgbd=this.userview.getResourceString(IResourceConstant.ORG_BD);
			mb.append(orgbd);
			mb.append(",");	
		}else if("8".equals(module_id)) {//岗位
			String posbd=this.userview.getResourceString(IResourceConstant.POS_BD);
			mb.append(posbd);
			mb.append(",");	
		}else if("-1".equals(module_id)) {
			String posbd=this.userview.getResourceString(IResourceConstant.POS_BD);
			mb.append(posbd);
			mb.append(",");	
			String orgbd=this.userview.getResourceString(IResourceConstant.ORG_BD);
			mb.append(orgbd);
			mb.append(",");	
			String bybd=this.userview.getResourceString(IResourceConstant.INS_BD);
			mb.append(bybd);
			mb.append(",");
			String gzbd=this.userview.getResourceString(IResourceConstant.GZBD);
			mb.append(gzbd);
			mb.append(",");
			String rsbd=this.userview.getResourceString(IResourceConstant.RSBD);
			mb.append(rsbd);
			mb.append(",");
			String pso=this.userview.getResourceString(IResourceConstant.PSORGANS);
			mb.append(pso);
			mb.append(",");	
			String fg=this.userview.getResourceString(IResourceConstant.PSORGANS_FG);
			mb.append(fg);
			mb.append(",");	
			String gx=this.userview.getResourceString(IResourceConstant.PSORGANS_GX);
			mb.append(gx);
			mb.append(",");	
			String jcg=this.userview.getResourceString(IResourceConstant.PSORGANS_JCG);
			mb.append(jcg);
			mb.append(",");	
		}else {
			String pso=this.userview.getResourceString(IResourceConstant.PSORGANS);
			mb.append(pso);
			mb.append(",");	
			String fg=this.userview.getResourceString(IResourceConstant.PSORGANS_FG);
			mb.append(fg);
			mb.append(",");	
			String gx=this.userview.getResourceString(IResourceConstant.PSORGANS_GX);
			mb.append(gx);
			mb.append(",");	
			String jcg=this.userview.getResourceString(IResourceConstant.PSORGANS_JCG);
			mb.append(jcg);
			mb.append(",");	
		}
		String[] bdarr=StringUtils.split(mb.toString(),",");
		if(bdarr==null || bdarr.length==0) {
			return "";
		}
		String tmp=StringUtils.join(bdarr, ',');
		tmp = tmp.replace("r", "");
		tmp = tmp.replace("R", "");
		tmp = tmp.replace(" ", "");
		tmp = tmp.replace(",,", ",");
		return tmp;
	}

	public String getTabids(String module_id) {
		RowSet rset = null;
		String tabids = "";
		try {
			ContentDAO dao = new ContentDAO(this.conn);
			String _static = "1";
	    	if("1".equals(module_id)){//人事异动
	    		_static = "1";
	    	}else if("2".equals(module_id)){//2、薪资管理
	    		_static = "2";
	    	}else if("4".equals(module_id)){//4、保险管理
	    		_static = "8";
	    	}else if("7".equals(module_id)){//7、机构管理
	    		_static = "10";
	    	}else if("8".equals(module_id)){//8、岗位管理
	    		_static = "11";
	    	}
	    	String static_="static";
	    	if(Sql_switcher.searchDbServerFlag()==Constant.DAMENG) {
	    		static_="static_o";
	    	}
	    	String sql = "select tabid from template_table where "+static_+"=?";
	    	ArrayList list = new ArrayList();
	    	list.add(_static);
	    	rset = dao.search(sql,list);
	    	while(rset.next()) {
	    		int tabid = rset.getInt("tabid");
	    		tabids+=tabid+",";
	    	}
	    	if(tabids.length()>0) {
	    		tabids = tabids.substring(0,tabids.length()-1);
	    	}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			PubFunc.closeDbObj(rset);
		}
		return tabids;
	}
	
	private String getRandomID(int n) throws Exception{
        String val = "";
        for ( int i = 0; i < n; i++ ){
            String str = random.nextInt( 2 ) % 2 == 0 ? "num" : "char";
            if ( "char".equalsIgnoreCase( str ) ){ // 产生字母
                int nextInt = random.nextInt( 2 ) % 2 == 0 ? 65 : 97;
                val += (char) ( nextInt + random.nextInt( 26 ) );
            }
            else if ( "num".equalsIgnoreCase( str ) ){ // 产生数字
                val += String.valueOf( random.nextInt( 10 ) );
            }
        }
        return val;
    }
}
