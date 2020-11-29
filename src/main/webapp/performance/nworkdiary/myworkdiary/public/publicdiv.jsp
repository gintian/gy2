 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList"%>
<link href="/css/diary.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/performance/nworkdiary/diary.js"></script>

   <!--新增和编辑div-->
   <!--  <div class="epm-j-all-dinwei">-->
   <div id="dataDiv" class="bh-epm-xin-all" >
    	<div class="bh-epm-xin-all-top"></div>
        <div class="bh-clear"></div>
        <div class="bh-epm-xin-all-midder">
        	<h2 onmousedown="moveInit('dataDiv',event);" onmousemove="move('dataDiv',event)" onmouseup="stopMove();" onmouseout="stopMove();"><p id="addOrEdit">新增事件</p><a href="###" onclick="closeDiv('dataDiv')"></a></h2>
            
            <div class="epm-biaoti">
            	<table>
                  <tr>
                    <td width="69" class="epm-leixing" >标题：</td>
                    <td colspan="3"><input id="title" type="text" class="epm-title-form"></td>
                  </tr>
                  <tr>
                    <td class="epm-leixing">类型：</td>
                    <td colspan="3"><input id="type" onClick="showOrHideHour(this)" type="checkbox" value="1"> 全天</td>
                  </tr>
                  <tr>
                    <td class="epm-leixing">开始时间：</td>
                    <td width="110"><input onblur="stimeBlurFunc();" id="start_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" class="epm-kaishi-form"></td>
                    
                        <td width="48">
                        <span id="startTimeDiv">
                        <select class="epm-select" id="startHour">
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
                                   </select>
                              </span>
                     </td>
                    <td width="156">
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
                    </td>
                  </tr>
                  <tr>
                    <td class="epm-leixing">结束时间：</td>
                    <td width="110"><input id="end_time" type="text" size="12" extra="editor" id="editor2" dropDown="dropDownDate" class="epm-kaishi-form"></td>
                    
                       <td width="48">
                       <span id="endTimeDiv">
                       <select class="epm-select" id="endHour">
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
                                   </select>
                             </span>
                     </td>
                    <td width="156">
                    <span id="endTimeDiv">
                       时
                    </span>
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
