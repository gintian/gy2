<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<div class="evaluation">
	<table id="doEvaluate" class="doEvaluate">
		<colgroup>
			<col width="60" />
			<col />
		</colgroup>
		<tr>
			<td colspan="2">
				<label>对<font id="evaluation-object"></font>的任务完成情况进行评价</label>
			</td>
		</tr>
		<tr class="h30">
			<td class="f13">打分</td>
			<td><span id="evaluation-score"></span></td>
		</tr>
		<tr>
			<td class="vAlignT f13" style="">评语</td>
			<td>
				<textarea type="text" id="evaluation-description" class="evaluation-description" /></textarea>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="tAlignR">
				<input type="button" value="发布" class="evaluation-publish" onclick="basic.biz.publishEvaluation()" />
			</td>
		</tr>
	</table>
	
	<div class="evaluations">
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<colgroup>
				<col width="100" />
				<col />
			</colgroup>
			
			<tbody id="eva-myself">
<!-- 				<tr> -->
<!-- 					<td colspan="2"><label>我的评价</label></td> -->
<!-- 				</tr> -->
			</tbody>
			
			<tbody id="eva-superior">
<!-- 				<tr> -->
<!-- 					<td colspan="2"><label>上级的评价</label></td> -->
<!-- 				</tr> -->
			</tbody>
			
			<tbody id="eva-dm">
				<tr>
					<td colspan="2"><label>负责人和参与人的评价</label></td>
				</tr>
			</tbody>
			
			<tbody id="eva-member">
				<tr>
					<td colspan="2"><label>参与人的评价</label></td>
				</tr>
			</tbody>
			
			<tbody id="eva-follower">
				<tr>
					<td colspan="2"><label>关注人的评价</label></td>
				</tr>
			</tbody>
			
		</table>
	</div>
</div>
