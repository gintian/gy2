package com.hjsj.hrms.transaction.general.operation;

import com.hjsj.hrms.utils.PubFunc;
import com.hrms.frame.dao.ContentDAO;
import com.hrms.hjsj.utils.Sql_switcher;
import com.hrms.struts.exception.GeneralException;
import com.hrms.struts.exception.GeneralExceptionHandler;
import com.hrms.struts.facade.transaction.IBusiness;
import com.hrms.struts.taglib.CommonData;
import org.apache.commons.beanutils.LazyDynaBean;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/**
 * 选择分组指标
 * @author Administrator
 *
 */
public class CodeitemDiffReportTrans extends IBusiness {

	public void execute() throws GeneralException {
		// TODO Auto-generated method stub
		try {

			HashMap hm=this.getFormHM();
			HashMap reqhm=(HashMap) hm.get("requestPamaHM");
			String tabid=(String) reqhm.get("tab_id");
			String sql="select * from template_set where tabid="+tabid+" and field_type='A' and flag<>'V'";
			ContentDAO dao=new ContentDAO(this.getFrameconn());
			
		//解析XML
			RowSet rowSet=null;
			String ctrl_para="";
			String fields="";
			rowSet=dao.search("select * from template_table where tabid="+tabid);
			if(rowSet.next())
			{
				ctrl_para = Sql_switcher.readMemo(rowSet,"ctrl_para");
				if(ctrl_para.trim().length()>0){
	                Document doc = PubFunc.generateDom(ctrl_para);
	                String xpath = "//params";
	                XPath xpath_ = XPath.newInstance(xpath);
	                Element ele = (Element) xpath_.selectSingleNode(doc);
	                Element child;
	                if (ele != null){
	                    child = ele.getChild("split_data");
	                    if (child != null)
	                    {
	                        fields = child.getAttributeValue("fields");
	                    }
	                }
				}
			}
			String xml_fields[]=fields.toString().split(",");
			String buffer="";
			String code_buffer="";
			HashMap map=new HashMap();
			String key="";
			String value="";
			for(int i=0;i<xml_fields.length;i++){
				buffer=xml_fields[i];
				if(!"_".equalsIgnoreCase(fields)&&!"".equalsIgnoreCase(fields)&&xml_fields[i].indexOf("(")!=-1){
					value=buffer.substring(xml_fields[i].indexOf("(")+1, xml_fields[i].indexOf(")"));
					key=buffer.substring(0, xml_fields[i].indexOf("("));
					code_buffer+=buffer.substring(0, xml_fields[i].indexOf("_"))+",";
				}
				map.put(key, value);
			}
			
			
			ResultSet rs=null;
			rs=dao.search(sql);
			String codeitemid="";//代码
			String codeitemname="";//名称
			String codeid="";//相关代码类 um/un....
			String chgstate="";//变化状态
			String checked="0";//是否选中  0未选
			String hismode="";
			LazyDynaBean bean=null;
			ArrayList fieldList=new ArrayList();
			String layer="";
			String layer_max="";
			if(!rs.next()){
				throw GeneralExceptionHandler.Handle(new Exception("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp当前模板没有分组指标！  ") );
			}
			
			int num=0;
			List list=new ArrayList();//用于校验fieldList是否存储相同的指标
			rs.beforeFirst();
			while(rs.next()){
				num++;
				codeitemid=rs.getString("field_name");
				codeitemname=rs.getString("field_hz");
				codeid=rs.getString("codeid");
				chgstate=rs.getString("chgstate");
				hismode=rs.getString("hismode");
				if(hismode!=null&&Integer.parseInt(hismode)>1)//多条记录的不能做拆单指标
				    continue;
				if(!list.contains((codeitemid+"_"+chgstate).toLowerCase())) {//去除模板中重复指标
					list.add((codeitemid+"_"+chgstate).toLowerCase());
				}else {
					continue;
				}
				if("UN".equalsIgnoreCase(codeid)||"UM".equalsIgnoreCase(codeid)||"@K".equalsIgnoreCase(codeid)){
					String sql1="select max(layer) from organization where codesetid='"+codeid+"'";
					ResultSet rs1=dao.search(sql1);
					while(rs1.next()){
						layer_max=rs1.getString(1);
					}
				}else{
					String sql1="select max(layer) from codeitem where codesetid='"+codeid+"'";
					ResultSet rs1=dao.search(sql1);
					while(rs1.next()){
						layer_max=rs1.getString(1);
					}
				}
				bean=new LazyDynaBean();
				layer=(String) map.get(codeitemid+"_"+chgstate)!=null?(String) map.get(codeitemid+"_"+chgstate):"";
				bean.set("codeitemid", codeitemid+"_"+chgstate);
				if("1".equals(chgstate)){
					bean.set("codeitemname", "现"+codeitemname);
				}else if("2".equals(chgstate)){
					bean.set("codeitemname","拟"+codeitemname);
				}
				if(fields.toString().indexOf(codeitemid+"_"+chgstate)!=-1){// bug 23753 陈芳提  设置拆单中的分组指标时，如勾选了拟单位，现单位也会自动勾选上。
					checked="1";
				}else{
					checked="0";
				}
				bean.set("layerList", this.getLayerList(layer_max));
				bean.set("layer", layer);
				bean.set("chgstate", chgstate);
				bean.set("checked", checked);
				fieldList.add(bean);
			}
			//20140917 dengcan 拆单时，定义模板分组指标少了第一个代码指标
			if(num==0){
				throw GeneralExceptionHandler.Handle(new Exception("&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp当前模板没有分组指标！    ") );
			}
			
			this.getFormHM().put("fieldList", fieldList);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw GeneralExceptionHandler.Handle(e);
		}

	}
	/**
	 * 获得层级选择列
	 * @param layer_max
	 * @return
	 */
	public ArrayList getLayerList(String layer_max){
		ArrayList layerList=new ArrayList();
		CommonData cmd=new CommonData();
		cmd.setDataName("");
		cmd.setDataValue("");
		layerList.add(cmd);
		if(layer_max!=null){
			for(int i=1;i<=Integer.parseInt(layer_max);i++){
				CommonData cd=new CommonData();
				cd.setDataName(String.valueOf(i));
				cd.setDataValue(String.valueOf(i));
				layerList.add(cd);
			}
		}
		
		return layerList;
	}

}
