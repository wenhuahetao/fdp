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
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Jrquest.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/CollectionUtil.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HdfsUtil.js"></script>
</head>
<style>
.clear{clear: both}
</style>
<body>
<script type="text/javascript">
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="caiji.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
	$("#error_commit").click(function(){
		var errorOn = false;
	    if($("#error_on").attr("checked")=="checked"){
	    	errorOn = true;
	    }
		var url = CollectionUtil.getRootPath()+'/collection/uploadFileGo?errorOn='+errorOn;
		$("#uploadLocalFileForm").attr("action",url);
		$("#uploadLocalFileForm").submit();
	});
	$("#error_cancle").click(function(){
		var url = CollectionUtil.getRootPath()+'/collection/unstructureCheckView?type=1';
		window.location.href=url;
	});
	if($("#checkCode").val()=='1'){
		$("#errorline_operate").hide();
		$(".errorline_check_1").show();
	}else{
		$(".errorline_check_1").hide();
	}
});
</script>
<%@ include file="../../head.jsp"%>
<div style="margin:10px 20px 0 20px;">
	<div class="maincontent" style="margin-top:20px;margin-bottom:30px;">
		
		 <div class="seachBar clearfix" style="margin-top:20px;color: #468847;background-color: #dff0d8; border-color: #d6e9c6;">
             
             <div class="locationAdd fl">&nbsp;&nbsp;<a style="cursor:pointer;" href="<%=request.getContextPath()%>/collection/unstructureCheckView?type=1">返回</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span class="errorline_check_1">已导${fileName}文件至
            	<span id="into_dir"><c:forEach items = "${dir}" var = "b" varStatus="status">/<a href="#" onClick="WindowOpener.jump4recursion(this)">${b}</a></c:forEach><a></a></span>
               	目录中,</span><font>${fileName}文件的校验信息如下</font>
            </div>
            
        </div>
        <div style="margin:10px 10px 10px 10px;overflow:auto; height:200px"  style=""> 
			<c:forEach items = "${checkFileBeanList}" var = "b" varStatus="status">
				<div style="font-size:16px;margin-top:10px">${b.checkInfo}</div>
			</c:forEach>
			
	 	</div>
        <c:if test="${!empty checkFileBeanList}">
		<form class="form-horizontal" role="form" id="uploadLocalFileForm" method="post" >
			<div style="margin:10px"  id="errorline_operate">
				<input type="hidden" name="hdfsPath" value="${hdfsPath}" />
				<input type="hidden" name="path" value="${path}" />
				<input type="hidden" name="fileName" value="${fileName}" />
				<input type="hidden" name="checkCode" id="checkCode" value="${checkCode}" />
				<input type="hidden" name="lines" value="${lines}" />
			   <div class="form-group">
			      <div class="col-sm-offset-2 col-sm-10">
			         <div class="checkbox" style="float:left;margin-top:5px">
			            <label>
			               <input type="checkbox" id="error_on"> 删除错误行
			               
			            </label>
			         </div>
			         <div style="float:left;margin-left:10px">
			         	<button type="button" class="btn btn-default" id="error_cancle">取消</button>
			       		<button type="button" class="btn btn-success" id="error_commit">提交</button>
			         </div>
			         <div style="clear:both"></div>
			      </div>
			   </div>
		  </div>
	  </form>
	  </c:if>
	</div>
</div>
</body>



</html>