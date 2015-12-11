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
function xzfield(){
	var checkedhtml = "";
	$(".field_table input[type=checkbox]").each(function(){
	    if(this.checked){
	    	checkedhtml = checkedhtml + "," + $(this).parent().next().find("span").text();
	    }
	});
	var hbase_rodom = $("#hbase_rodom").val();
	var type = $("#hbase_type").val();
	if(type==1){
		$("#"+hbase_rodom, window.parent.document).append(checkedhtml);
	}else{
		checkedhtml = checkedhtml.substr(1);
		$("#"+hbase_rodom, window.parent.document).val(checkedhtml);
	}
	api.reload();
	api.close();
}
$(document).ready(function(){
	$("#checkexzxd").click(function(){
		$('[name=dbzdxz]:checkbox').attr("checked", this.checked); 
	});
});

</script>
</head>
<body style="padding:10px">
<div style="height:300px;width:100%;margin:0 atuo;overflow:auto">
	<table class="table table-bordered field_table">
		<thead>
			<tr>
				<th>
					<input type="checkbox" id="checkexzxd"/>
				</th>
				<th>
					字段名称
				</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${fields}" var="field">
			<tr>
				<td>
					<input type="checkbox" name="dbzdxz"/>
				</td>
				<td><span>${field}</span></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<div align="center">
	<input type="hidden" id="hbase_rodom" value="${rodom}" />
	<input type="hidden" id="hbase_type" value="${type}" />
	<a style="padding:10px 80px 10px 80px;margin-top:20px" class="btn btn-default" href="#" id="confirmSelection" onclick="xzfield()">确定</a>
</div>
</body>
