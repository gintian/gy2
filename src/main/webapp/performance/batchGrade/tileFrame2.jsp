<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hjsj.hrms.transaction.performance.LoadXml,
				 com.hjsj.hrms.businessobject.performance.batchGrade.BatchGradeBo,
				 java.util.*,
				 com.hrms.frame.utility.AdminDb,
				 java.sql.Connection,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.taglib.CommonData"%>
<html>

<%
	BatchGradeForm batchGradeForm=(BatchGradeForm)session.getAttribute("batchGradeForm");
	ArrayList dblist=(ArrayList)batchGradeForm.getDblist();
	HashMap   planScoreflagMap=(HashMap)batchGradeForm.getPlanScoreflagMap();
	String togetherCommit=batchGradeForm.getTogetherCommit(); ////多人打分统一提交, Ture, False, 默认为False
	String height_num="100%";
	StringBuffer str=new StringBuffer(",");
	StringBuffer str2=new StringBuffer(",");
	boolean flag=true;
	for(int i=0;i<dblist.size();i++)
	{
		CommonData data=(CommonData)dblist.get(i);
		str.append("aaa"+data.getDataValue()+",");
		str2.append(data.getDataValue()+",");
		String desc=data.getDataName();
		if(desc.indexOf("(已评价")==-1)
			flag=false;

	}
%>

<script type="text/javascript">
    <!--

    function test(name)
    {
        var str='<%=(str.toString())%>';
        if(str.indexOf(","+name+",")!=-1)
        {


            var obj=$('cardset');
            if(obj.setSelectedTab){
                obj.setSelectedTab(name);
            }else{
                str = str.substring(1,str.length-1);
                var arr = str.split(",");
                var index = 0;
                for(var i=0;i<arr.length;i++){
                    if(arr[i]==name){
                        index = i;
                        break;
                    }
                }
                if($('#_tabsetpane_cardset')){
                    $('#_tabsetpane_cardset').tabs('select', index);
                }

            }
        }
    }


    //-->
</script>

<%
	String operate=null;
	if(request.getParameter("operate")!=null)
		operate=request.getParameter("operate");

%>

<body  <%=(operate!=null?"onload=\"test('"+operate+"')\"":""  )%>   >
<html:form action="/selfservice/performance/batchGrade" style="height:100%;">
	<%

		if(dblist.size()>0)
		{

	%>

	<hrms:tabset name="cardset" width="100%" height="<%=height_num%>" type="true">
		<%
			Connection conn = null;
			try
			{
				conn =AdminDb.getConnection();
				for(int i=0;i<dblist.size();i++)
				{
					CommonData data=(CommonData)dblist.get(i);
					String name=data.getDataName();
					//String num="aaa"+String.valueOf(i);
					String plan_id=data.getDataValue();
					String num="aaa"+plan_id;
					String value="";

					Hashtable htxml=new Hashtable();
					LoadXml loadxml=null; //new LoadXml(this.getFrameconn(),plan_id);
					if(BatchGradeBo.getPlanLoadXmlMap().get(plan_id)==null)
					{

						loadxml = new LoadXml(conn,plan_id);
						BatchGradeBo.getPlanLoadXmlMap().put(plan_id,loadxml);
					}
					else
					{
						loadxml=(LoadXml)BatchGradeBo.getPlanLoadXmlMap().get(plan_id);
					}
					htxml=loadxml.getDegreeWhole();
					String pointEvalType=(String)htxml.get("PointEvalType");
					String MutiScoreOnePageOnePoint=(String)htxml.get("MutiScoreOnePageOnePoint");
					String model=batchGradeForm.getModel();
					if(((SystemConfig.getPropertyValue("batchgrade_radiotype")!=null && SystemConfig.getPropertyValue("batchgrade_radiotype").trim().equalsIgnoreCase("multiple")) ||( pointEvalType.equals("1")&&MutiScoreOnePageOnePoint.equalsIgnoreCase("True") )) && planScoreflagMap.get(plan_id)!=null && ((String)planScoreflagMap.get(plan_id)).equals("1"))
					{
						value="/performance/batchGradeSinglePoint.do?b_Desc="+data.getDataValue()+"&selectNewPlan=true&operate="+num+"&model="+model;
					}
					else
						value="/selfservice/performance/batchGrade.do?b_Desc="+data.getDataValue()+"&selectNewPlan=true&operate="+num+"&model="+model;

		%>


		<hrms:tab name="<%=num%>" label="<%=name%>" visible="true" url="<%=value%>">
		</hrms:tab>

		<%
				}

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}finally{
				try
				{
					if(conn!=null)
						conn.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

		%>
	</hrms:tabset>
	<%
		}
		else
		{
			out.print("<br><div align='center'>&nbsp;&nbsp;"+ResourceFactory.getProperty("lable.performance.info1")+"！</div>");

		}

	%>


</html:form>
</body>


</html>