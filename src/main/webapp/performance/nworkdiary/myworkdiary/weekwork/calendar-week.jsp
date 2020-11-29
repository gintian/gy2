<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<html>
  <head>
    <title>My JSP 'calendar-day.jsp' starting page</title>
    <link href="/performance/nworkdiary/calendar.css" rel="stylesheet" type="text/css">
    <link href="/css/diary.css" rel="stylesheet" type="text/css">
    <script language="JavaScript" src="/performance/nworkdiary/calendar.js"></script>
	<script language="JavaScript" src="/js/popcalendar3.js"></script>
	<script language="JavaScript" src="/js/function.js"></script>
	<script language="JavaScript" src="/js/validate.js"></script>
	<script language="JavaScript" src="/js/constant.js"></script>
	<script language="JavaScript" src="/js/popcalendar.js"></script>
	<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
	<script language="JavaScript" src="/performance/nworkdiary/diary.js"></script>
  </head>
  
  <body>
  
	<input type="hidden" id="p0100">
   	<input type="hidden" id="record_num">
    <html:form action="/performance/nworkdiary/myworkdiary/weekwork">
    <html:hidden property="a0100" name="calendarWeekForm" styleId="a0100"/>
    <html:hidden property="nbase" name="calendarWeekForm" styleId="nbase"/>
    <div class="epm-g-all">
    	${calendarWeekForm.tableHtml}
	    <div class="epm-tb-all">
		<div class="epm-top-gun">
    	<div id="whole" class="epm-tb-top">
        	${calendarWeekForm.weekHtml}
        </div>
        
        </div>
        <div class="bh-clear"></div>
        <%
  		String clientName = SystemConfig.getPropertyValue("clientName");
		if(clientName==null || !clientName.equalsIgnoreCase("gw")){
   		%>
		<div class="epm-dandu-gdt">
        <div id="period" class="epm-tb-bottom">
			
        	<table width="100%" border="0" cellpadding="0" cellspacing="0">
             
             <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">0:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">1:00</td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
                <td width="13%"><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">2:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
               <td width="9%" align="right" valign="top" class="zuobiao">3:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">4:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">5:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
              	<td width="9%" align="right" valign="top" class="zuobiao">6:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">7:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">8:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">9:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">10:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">11:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">12:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">13:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">14:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
               <td width="9%" align="right" valign="top" class="zuobiao">15:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">16:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">17:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">18:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">19:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">20:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">21:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">22:00</td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
                <td><div class="epm-tb-zjxux"></div></td>
              </tr>
              
              <tr>
                <td width="9%" align="right" valign="top" class="zuobiao">23:00</td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
                <td class="epm-bottom-xian-td"><div class="epm-tb-zjxux"></div></td>
              </tr>
            </table>
</div>
</div>
<%} %>
    </div>
</div>
   </html:form>
   


<!--新增和编辑div-->
   <!--  <div class="epm-j-all-dinwei">-->
   <div id="dataDiv" class="bh-epm-xin-all" >
    	<div class="bh-epm-xin-all-top"></div>
        <div class="bh-clear"></div>
        <div id="mid" class="bh-epm-xin-all-midder">
        	<h2 onmousedown="moveInit('dataDiv',event);" onmousemove="move('dataDiv',event)" onmouseup="stopMove();" onmouseout="stopMove();"><p id="addOrEdit">新增事件</p><a href="###" onclick="closeDiv('dataDiv')"></a></h2>
            
            <div class="epm-biaoti">
            	<table>
                  <tr>
                    <td width="69" class="epm-leixing" >标题：</td>
                    <td colspan="3"><input id="title" type="text" class="epm-title-form"></td>
                  </tr>
                  <tr>
                    <td class="epm-leixing"><span id='addwholespan'><span id="addperiodspan">类型：</span></span></td>
                    <td colspan="3"><span id='addwholespan'><span id="addperiodspan"><input id="type" onClick="showOrHideHour(this)" type="checkbox" value="1"> 全天</span></span></td>
                  </tr>
                  <tr>
                    <td class="epm-leixing"><span id='addwholespan'>开始时间：</span></td>
                    <td width="110"><span id='addwholespan'><input onblur="stimeBlurFunc();" id="start_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" class="epm-kaishi-form"></span></td>
                    
                        <td width="48">
                        <span id='addwholespan'>
                        <span id="startTimeDiv">
                        <select class="epm-select" id="startHour">
                    			   
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   
                                   </select>
                              </span>
                              </span>
                     </td>
                    <td width="156">
                    <span id='addwholespan'>
                    <span id="startTimeDiv">
                       时
                    </span>
                    <span id="startTimeDiv">
                    <select class="epm-select" id="startMinute">
                                   <option value="0">00</option>
                   				   <option value="1">01</option>
                                   <option value="2">02</option>
                                   <option value="3">03</option>
                                   <option value="4">04</option>
                                   <option value="5">05</option>
                                   <option value="6">06</option>
                                   <option value="7">07</option>
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   <option value="18">18</option>
                                   <option value="19">19</option>
                                   <option value="20">20</option>
                                   <option value="21">21</option>
                                   <option value="22">22</option>
                                   <option value="23">23</option>
                                   <option value="24">24</option>
                                   <option value="25">25</option>
                                   <option value="26">26</option>
                                   <option value="27">27</option>
                                   <option value="28">28</option>
                                   <option value="29">29</option>
                                   <option value="30">30</option>
                                   <option value="31">31</option>
                                   <option value="32">32</option>
                                   <option value="33">33</option>
                                   <option value="34">34</option>
                                   <option value="35">35</option>
                                   <option value="36">36</option>
                                   <option value="37">37</option>
                                   <option value="38">38</option>
                                   <option value="39">39</option>
                                   <option value="40">40</option>
                                   <option value="41">41</option>
                                   <option value="42">42</option>
                                   <option value="43">43</option>
                                   <option value="44">44</option>
                                   <option value="45">45</option>
                                   <option value="46">46</option>
                                   <option value="47">47</option>
                                   <option value="48">48</option>
                                   <option value="49">49</option>
                                   <option value="50">50</option>
                                   <option value="51">51</option>
                                   <option value="52">52</option>
                                   <option value="53">53</option>
                                   <option value="54">54</option>
                                   <option value="55">55</option>
                                   <option value="56">56</option>
                                   <option value="57">57</option>
                                   <option value="58">58</option>
                                   <option value="59">59</option>
                                   </select>
                       分
                    </span>
                    </span>
                    </td>
                  </tr>
                  <tr>
                    <td class="epm-leixing"><span id='addwholespan'>结束时间：</span></td>
                    <td width="110"><span id='addwholespan'><input id="end_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" class="epm-kaishi-form"></span></td>
                    
                       <td width="48">
                       <span id='addwholespan'>
                       <span id="endTimeDiv">
                       <select class="epm-select" id="endHour">
                    			   
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   
                                   </select>
                             </span>
                             </span>
                     </td>
                    <td width="156">
                    <span id='addwholespan'>
                    <span id="endTimeDiv">
                       时
                    </span>
                    </span>
                    <span id='addwholespan'>
                    <span id="endTimeDiv">
                    <select class="epm-select" id="endMinute">
                                   <option value="0">00</option>
                   				   <option value="1">01</option>
                                   <option value="2">02</option>
                                   <option value="3">03</option>
                                   <option value="4">04</option>
                                   <option value="5">05</option>
                                   <option value="6">06</option>
                                   <option value="7">07</option>
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   <option value="18">18</option>
                                   <option value="19">19</option>
                                   <option value="20">20</option>
                                   <option value="21">21</option>
                                   <option value="22">22</option>
                                   <option value="23">23</option>
                                   <option value="24">24</option>
                                   <option value="25">25</option>
                                   <option value="26">26</option>
                                   <option value="27">27</option>
                                   <option value="28">28</option>
                                   <option value="29">29</option>
                                   <option value="30">30</option>
                                   <option value="31">31</option>
                                   <option value="32">32</option>
                                   <option value="33">33</option>
                                   <option value="34">34</option>
                                   <option value="35">35</option>
                                   <option value="36">36</option>
                                   <option value="37">37</option>
                                   <option value="38">38</option>
                                   <option value="39">39</option>
                                   <option value="40">40</option>
                                   <option value="41">41</option>
                                   <option value="42">42</option>
                                   <option value="43">43</option>
                                   <option value="44">44</option>
                                   <option value="45">45</option>
                                   <option value="46">46</option>
                                   <option value="47">47</option>
                                   <option value="48">48</option>
                                   <option value="49">49</option>
                                   <option value="50">50</option>
                                   <option value="51">51</option>
                                   <option value="52">52</option>
                                   <option value="53">53</option>
                                   <option value="54">54</option>
                                   <option value="55">55</option>
                                   <option value="56">56</option>
                                   <option value="57">57</option>
                                   <option value="58">58</option>
                                   <option value="59">59</option>
                                   </select>
                       分
                    </span>
                    </span>
                    </td>
                  </tr>
                  <tr>
                    <td class="epm-leixing">内容：</td>
                    <td colspan="3"><textarea id="contentid" class="epm-nr"></textarea></td>
                  </tr>
                  <tr>
                  	<td></td>
                    <td  colspan="3" align="center"  id="okOrCancel">
                        
                    </td>
                  </tr>
                </table>
            </div>
            <div class="bh-clear"></div>
        </div>
        <div class="bh-clear"></div>
        <div class="bh-epm-xin-all-bottom"></div>
   </div>
   <!--</div>-->
   <!--查询div-->
   <div id="queryDiv" class="bh-epm-xin-all" >
    	<div class="bh-epm-xin-all-top"></div>
        <div class="bh-clear"></div>
        <div class="bh-epm-xin-all-midder" >
        	<h2 onmousedown="moveInit('queryDiv',event);" onmousemove="move('queryDiv',event)" onmouseup="stopMove();" onmouseout="stopMove();"><p >查询</p><a href="###" onclick="hideDiv('queryDiv')"></a></h2>
            
            <div class="epm-biaoti">
                <table>
                  <tr>
                    <td width="69" class="epm-leixing" >标题：</td>
                    <td colspan="2"><input id="queryTitle" type="text" class="epm-query-title-form"></td>
                  </tr>
                  
                  <tr>
                    <td class="epm-leixing">开始时间：</td>
                    <td colspan="2"><input id="queryStart_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" value="2013-01-25" class="epm-kaishi-form">
                    	<select class="epm-select" id="queryStartHour">
                    			   
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   
                                   </select>
                       时
                       <select class="epm-select" id="queryStartMinute">
                                   <option value="0">00</option>
                   				   <option value="1">01</option>
                                   <option value="2">02</option>
                                   <option value="3">03</option>
                                   <option value="4">04</option>
                                   <option value="5">05</option>
                                   <option value="6">06</option>
                                   <option value="7">07</option>
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   <option value="18">18</option>
                                   <option value="19">19</option>
                                   <option value="20">20</option>
                                   <option value="21">21</option>
                                   <option value="22">22</option>
                                   <option value="23">23</option>
                                   <option value="24">24</option>
                                   <option value="25">25</option>
                                   <option value="26">26</option>
                                   <option value="27">27</option>
                                   <option value="28">28</option>
                                   <option value="29">29</option>
                                   <option value="30">30</option>
                                   <option value="31">31</option>
                                   <option value="32">32</option>
                                   <option value="33">33</option>
                                   <option value="34">34</option>
                                   <option value="35">35</option>
                                   <option value="36">36</option>
                                   <option value="37">37</option>
                                   <option value="38">38</option>
                                   <option value="39">39</option>
                                   <option value="40">40</option>
                                   <option value="41">41</option>
                                   <option value="42">42</option>
                                   <option value="43">43</option>
                                   <option value="44">44</option>
                                   <option value="45">45</option>
                                   <option value="46">46</option>
                                   <option value="47">47</option>
                                   <option value="48">48</option>
                                   <option value="49">49</option>
                                   <option value="50">50</option>
                                   <option value="51">51</option>
                                   <option value="52">52</option>
                                   <option value="53">53</option>
                                   <option value="54">54</option>
                                   <option value="55">55</option>
                                   <option value="56">56</option>
                                   <option value="57">57</option>
                                   <option value="58">58</option>
                                   <option value="59">59</option>
                                   </select>分
                    </td>
                  </tr>
                  <tr>
                    <td class="epm-leixing">结束时间：</td>
                    <td colspan="2"><input id="queryEnd_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" value="2013-01-25" class="epm-kaishi-form">
						<select class="epm-select" id="queryEndHour">
                    			   
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   
                                   </select>
                       时
                       <select class="epm-select" id="queryEndMinute">
                                   <option value="0">00</option>
                   				   <option value="1">01</option>
                                   <option value="2">02</option>
                                   <option value="3">03</option>
                                   <option value="4">04</option>
                                   <option value="5">05</option>
                                   <option value="6">06</option>
                                   <option value="7">07</option>
                                   <option value="8">08</option>
                                   <option value="9">09</option>
                                   <option value="10">10</option>
                                   <option value="11">11</option>
                                   <option value="12">12</option>
                                   <option value="13">13</option>
                                   <option value="14">14</option>
                                   <option value="15">15</option>
                                   <option value="16">16</option>
                                   <option value="17">17</option>
                                   <option value="18">18</option>
                                   <option value="19">19</option>
                                   <option value="20">20</option>
                                   <option value="21">21</option>
                                   <option value="22">22</option>
                                   <option value="23">23</option>
                                   <option value="24">24</option>
                                   <option value="25">25</option>
                                   <option value="26">26</option>
                                   <option value="27">27</option>
                                   <option value="28">28</option>
                                   <option value="29">29</option>
                                   <option value="30">30</option>
                                   <option value="31">31</option>
                                   <option value="32">32</option>
                                   <option value="33">33</option>
                                   <option value="34">34</option>
                                   <option value="35">35</option>
                                   <option value="36">36</option>
                                   <option value="37">37</option>
                                   <option value="38">38</option>
                                   <option value="39">39</option>
                                   <option value="40">40</option>
                                   <option value="41">41</option>
                                   <option value="42">42</option>
                                   <option value="43">43</option>
                                   <option value="44">44</option>
                                   <option value="45">45</option>
                                   <option value="46">46</option>
                                   <option value="47">47</option>
                                   <option value="48">48</option>
                                   <option value="49">49</option>
                                   <option value="50">50</option>
                                   <option value="51">51</option>
                                   <option value="52">52</option>
                                   <option value="53">53</option>
                                   <option value="54">54</option>
                                   <option value="55">55</option>
                                   <option value="56">56</option>
                                   <option value="57">57</option>
                                   <option value="58">58</option>
                                   <option value="59">59</option>
                                   </select>分                    
                    </td>
                  </tr>
                  <tr>
                    <td class="epm-leixing" nowrap="nowrap" style="padding-top:5px;">内容：</td>
                    <td colspan="2"><input id="queryContent" type="text" class="epm-query-title-form">
                        <input type="button" value="查询" class="epm-baocun" onclick="queryData('');">
                    </td>
                  </tr>
                  
                  <tr>
                  <td colspan="3">
                  &nbsp;
                  </td>
                  </tr>
                  <tr>
                  	
                  	<td class="epm-leixing">结果：</td>
                    <td colspan="2">
                    <!--  <iframe id="resultIframe" style="overflow-y:auto;overflow-x:hidden;height:200;width:80%;"  scrolling="auto" name="result" src="/performance/nworkdiary/myworkdiary/queryData.do?b_query=link&init=init">
                    </iframe>-->
                    <div id="resultIframe" class="epm-j-jieguo"></div>
                    </td>
                  </tr>
                </table>  
            </div>
            <div class="bh-clear"></div>
        </div>
        <div class="bh-clear"></div>
        <div class="bh-epm-xin-all-bottom"></div>
   </div>
   
   <!-- 左边显示的详细div -->
   <div id="dataDetailLeftDiv" class="epm-dinwei" >
   		<div class="epm-dinwei-left"></div>
        <div class="epm-dinwei-center">
        	<div class="epm-dinwei-center-top">
            	<div class="epm-top-one">
                	<div id="leftTitleDetail" class="one"></div>
                    <div id="leftTimeDetail" class="two"></div>
                </div>
                
                <form>
                <div class="epm-top-two">
                    <a href="###" onclick="javascript:Element.hide('dataDetailLeftDiv')"></a>
                	<span id="leftEditButton"><input type="button" value="编辑" class="epm-bianji" onClick="showQueryDiv('update');"/>
                     </span>
                </div>
                </form>
             <div class="bh-clear"></div>
            </div>
            <div class="bh-clear"></div>
            <form>
            <div class="epm-dinwei-center-bottom">
                <textarea id="leftContentDetail"  class="epm-wenben" readonly="readonly"></textarea>
            </div>
            </form>
        </div>
        <div class="epm-dinwei-right"></div>
   </div>
   <!-- 右边显示的详细div -->
   <div id="dataDetailRightDiv" class="epm-dinwei">
   		<div class="epm-dinwei-left-bg"></div>
        <div class="epm-dinwei-center">
        	<div class="epm-dinwei-center-top">
            	<div class="epm-top-one">
                	<div id="rightTitleDetail" class="one"></div>
                    <div id="rightTimeDetail" class="two"></div>
                </div>
                
                <form>
                <div class="epm-top-two">
                	<a href="###" onclick="javascript:Element.hide('dataDetailRightDiv')"></a>
                    <span id="rightEditButton"><input type="button" value="编辑" class="epm-bianji" onClick="showQueryDiv('update');"/>
                    </span>
                </div>
                </form>
             <div class="bh-clear"></div>
            </div>
            <div class="bh-clear"></div>
            <form>
            <div class="epm-dinwei-center-bottom">
                <textarea id="rightContentDetail"  class="epm-wenben" readonly="readonly"></textarea>
            </div>
            </form>
        </div>
        <div class="epm-dinwei-right-bg"></div>
   </div>
   
   
   <script type="text/javascript">
    var frompage="";
    Element.hide('queryDiv');
    Element.hide('dataDiv');
    Element.hide('dataDetailLeftDiv');
    Element.hide('dataDetailRightDiv');
   </script>




















  </body>
</html>
<script language="javascript">
	var isowner="${calendarWeekForm.isowner}";
	executeWholeDay();
	executePeriod();
function executeWholeDay()
{
	<%
		int realwidth = 0;
		int realleft = 0;
		String clientName2 = SystemConfig.getPropertyValue("clientName");
		if(clientName2==null || !clientName2.equalsIgnoreCase("gw")){//如果不是国网
			realwidth = 13;
			realleft = 9;
		}else{
			realwidth = 14;
			realleft = 2;
		}
	%>
	var tmprealwidth=<%=realwidth%>;
	var realwidth=parseInt(tmprealwidth);
	var tmprealleft=<%=realleft%>;
	var realleft=parseInt(tmprealleft);
	//解析全天事件   格式为  {"lunfinish":"1","runfinish":"0","record_num":"1","p0100":"158","title":"郭峰测试 5月7日至5月8日","min":"2","max":"3","linecount":"2"}
	var obj = eval(${calendarWeekForm.jsonstr});//json对象
	for(var i=0;i<obj.length;i++)//将json数组循环
     {
     	 var lunfinish=obj[i].lunfinish;
     	 var runfinish=obj[i].runfinish;
         var record_num=obj[i].record_num;
         var p0100=obj[i].p0100;
         var title=obj[i].title;
         var min=parseInt(obj[i].min);
         var max=parseInt(obj[i].max);
         var linecount=parseInt(obj[i].linecount);
         var htmlstr="";
         if(lunfinish==1)
         {
         	//如果本周的第一天不是该记录的起始时间
		         	if(runfinish==1)
		         	{//如果左边是直线，右边是直线
		         		//如果本周的最后一天不是该记录的结束时间
		         		htmlstr="<div style='height:23px;width:"+((max-min+1)*realwidth)+"%;line-height:30px;position:absolute;top:"+(35+(linecount)*27)+"px;left:"+realleft+"%;color:#3e7e34;font-weight:bolder;cursor:pointer;' onclick='showDetail(\""+isowner+"\",\""+p0100+"\",\""+record_num+"\");'>";
				         ////htmlstr="<div class='epm-gf-td'>";
				         htmlstr+="<div class='rtop'><div class='lrunfinish_r1'></div><div class='lrunfinish_r2'></div><div class='lrunfinish_r3'></div><div class='lrunfinish_r4'></div></div>";
				         htmlstr+="<div class='epm-gff-yuanjiao'>&nbsp;"+title+"</div>";        
				         htmlstr+="<div class='rbottom'><div class='lrunfinish_r4'></div><div class='lrunfinish_r3'></div><div class='lrunfinish_r2'></div><div class='lrunfinish_r1'></div></div>";                   
				         htmlstr+="</div>";
		         		
		         	}
		         	else
		         	{//如果左边是直线，右边是弧线
		         		 htmlstr="<div style='height:23px;width:"+((max-min+1)*realwidth-0.5)+"%;line-height:30px;position:absolute;top:"+(35+(linecount)*27)+"px;left:"+realleft+"%;color:#3e7e34;font-weight:bolder;cursor:pointer;' onclick='showDetail(\""+isowner+"\",\""+p0100+"\",\""+record_num+"\");'>";
				         ////htmlstr="<div class='epm-gf-td'>";
				         htmlstr+="<div class='rtop'><div class='lunfinish_r1'></div><div class='lunfinish_r2'></div><div class='lunfinish_r3'></div><div class='lunfinish_r4'></div></div>";
				         htmlstr+="<div class='epm-gff-yuanjiao'>&nbsp;"+title+"</div>";        
				         htmlstr+="<div class='rbottom'><div class='lunfinish_r4'></div><div class='lunfinish_r3'></div><div class='lunfinish_r2'></div><div class='lunfinish_r1'></div></div>";                   
				         htmlstr+="</div>";
		         		
		         	}
         }
         else
         {
		         	if(runfinish==1)
		         	{//左边弧线，右边直线
		         		//如果本周的最后一天不是该记录的结束时间
		         		htmlstr="<div style='height:23px;width:"+((max-min+1)*realwidth-0.5)+"%;line-height:30px;position:absolute;top:"+(35+(linecount)*27)+"px;left:"+(realleft+(min*realwidth)+0.5)+"%;color:#3e7e34;font-weight:bolder;cursor:pointer;' onclick='showDetail(\""+isowner+"\",\""+p0100+"\",\""+record_num+"\");'>";
				         ////htmlstr="<div class='epm-gf-td'>";
				         htmlstr+="<div class='rtop'><div class='runfinish_r1'></div><div class='runfinish_r2'></div><div class='runfinish_r3'></div><div class='runfinish_r4'></div></div>";
				         htmlstr+="<div class='epm-gff-yuanjiao'>&nbsp;"+title+"</div>";        
				         htmlstr+="<div class='rbottom'><div class='runfinish_r4'></div><div class='runfinish_r3'></div><div class='runfinish_r2'></div><div class='runfinish_r1'></div></div>";                   
				         htmlstr+="</div>";
		         	}
		         	else
		         	{//左边弧线，右边弧线
		         		 htmlstr="<div style='height:23px;width:"+((max-min+1)*realwidth-1)+"%;line-height:30px;position:absolute;top:"+(35+(linecount)*27)+"px;left:"+(realleft+(min*realwidth)+0.5)+"%;color:#3e7e34;font-weight:bolder;cursor:pointer;' onclick='showDetail(\""+isowner+"\",\""+p0100+"\",\""+record_num+"\");'>";
				         ////htmlstr="<div class='epm-gf-td'>";
				         htmlstr+="<div class='rtop'><div class='r1'></div><div class='r2'></div><div class='r3'></div><div class='r4'></div></div>";
				         htmlstr+="<div class='epm-gff-yuanjiao'>&nbsp;"+title+"</div>";        
				         htmlstr+="<div class='rbottom'><div class='r4'></div><div class='r3'></div><div class='r2'></div><div class='r1'></div></div>";                   
				         htmlstr+="</div>";
		         	}
         }
         document.getElementById("whole").innerHTML+=htmlstr;
     }
	
}
function executePeriod()
{
	//解析时间段事件  格式为   record_num,p0100,title,start,end,average,zindex,indent,sequence,lindent
	var obj = eval(${calendarWeekForm.periodjsonstr});//json对象
	for(i in obj)
	{
		if(i=="extend")
			continue;
		var column=parseInt(i);
		var jsonobj=eval(obj[i]);
		for(var i=0;i<jsonobj.length;i++)//将json数组循环
		{
			 var record_num=jsonobj[i].record_num;
	         var p0100=jsonobj[i].p0100;
	         var title=jsonobj[i].title;
	         var start=parseFloat(jsonobj[i].start);
	         var end=parseFloat(jsonobj[i].end);
	         var average=parseInt(jsonobj[i].average);
	         var zindex=parseInt(jsonobj[i].zindex);
	         var indent=parseInt(jsonobj[i].indent);
	         var sequence=parseInt(jsonobj[i].sequence);
	         var lindent=parseInt(jsonobj[i].lindent);
	         var htmlstr="";
         	 htmlstr="<div class='axlefilter' style='height:"+(end-start)*31+"px;width:"+((1.0/average)*13-indent*1-lindent*1)+"%;top:"+(1+start*31)+"px;left:"+(9+column*13+lindent*1+(1.0/average)*13*(sequence-1))+"%;z-index:"+zindex+";' onclick='showDetail(\""+isowner+"\",\""+p0100+"\",\""+record_num+"\");'>"+title+"</div>";    
         	 document.getElementById("period").innerHTML+=htmlstr;
		}
	}
}

</script>
