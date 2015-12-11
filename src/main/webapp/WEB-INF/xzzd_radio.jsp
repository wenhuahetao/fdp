<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>大数据平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.1/jquery-1.7.1.min.js"></script>
<script type="text/javascript"src="<%=request.getContextPath()%>/js/jquery-1.7.1/jquery.colorbox-min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css" />
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap.css" rel="stylesheet">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HbaseUtil.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Jrquest.js"></script>
<script type="text/javascript">
var api = frameElement.api;//调用父页面数据  
var W = api.opener;//获取父页面对象  
$(document).ready(function() {
	$(".radioItem").change(function() {
		var checkedhtml = $("input[name='rl$tt']:checked").val();
		var checkedval = $("input[name='rl$tt']:checked").parent().next().find("span").text();
		var hbase_rodom = $("#hbase_rodom").val();
		var option = "<option value='"+checkedhtml+"' selected>"+checkedval+"</option>";
		$("#"+hbase_rodom, window.parent.document).append(option);
		$("."+hbase_rodom, window.parent.document).val(checkedval);
		//去重
		/*$("#"+hbase_rodom+" option").each(function () {
	        var text = $(this).text();
	        if ($("#"+hbase_rodom+" option:contains('" + text + "')").length > 1){
	        	$("#"+hbase_rodom+" option:contains('" + text + "'):gt(0)").remove();
	        }
		});*/
		
		api.reload();
		api.close();
	});
});
</script>
</head>
<table class="table table-bordered field_table">
	<thead>
		<tr>
			<th>
			</th>
			<th>
				字段名称
			</th>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="${fields}" var="field" varStatus="status">
		<tr>
			<td>
				<input type="radio" class="radioItem" name="rl$tt" value="${status.index}"/>
			</td>
			<td>
				<span>${field}</span>
			</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
<input type="hidden" id="hbase_rodom" value="${rodom}" />