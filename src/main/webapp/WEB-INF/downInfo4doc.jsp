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
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HdfsUtil.js"></script>
</head>
<style>
.left_down_file{padding:20px;}
.left_down_file div{margin-top:10px}
  textarea{
    	border:0;
  	background-color:transparent;
  	/*scrollbar-arrow-color:yellow;
  	scrollbar-base-color:lightsalmon;
  	overflow: hidden;*/
  	color: #666464;
  	height: auto;
  }
</style>
<script>
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="wenjianjia.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
});
function toCurrent(obj){
	var filename = $(obj).html();
	var dir = $(obj).next().val();
	$("#current_downfile").val(filename);
	$("#current_downdir").val(dir.substr(0,dir.indexOf(filename)-1));
	if(filename.indexOf(".txt")>=0){
		$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/download4txt.xhtml" );
	}else if(filename.indexOf(".doc")>=0){
		$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/download4doc.xhtml" );
	}
	$("#file_form_d").submit();
}
function toDownCurrent(obj,type){
	$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/"+type+"Txt.xhtml" );
	$("#file_form_d").submit();
}
</script>
<body>
<div class="action-box">
<div class="mainbody">
		<%@ include file="head.jsp"%>
		<div class="maincontent">
			<div style="margin:30px 0 30px 0px">
		        <div style="height:20px;font-size:20px;" ><strong>&nbsp;&nbsp;知识库</strong></div>
			</div>
        <div class="seachBar clearfix">
            <div class="locationAdd fl"><a href="#" onClick="WindowOpener.jumpRoot()">Home</a>
            	<span id="into_dir"><c:forEach items = "${dir}" var = "b" varStatus="status">/<a href="#" onClick="WindowOpener.jump4recursion(this)">${b}</a></c:forEach><a href="#">/${downfilename}</a></span>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath }/downloadTxt.xhtml" id="file_form_d">
        	<input type="hidden" value="${fileinfo.filename}" name="downfilename" id="current_downfile" />
        	<input type="hidden" value="${fileinfo.filedir}" name="downfiledir" id="current_downdir" />
        </form>
        <div style="margin-top:20px">
        	<div style="float:left;width:20%;height:400px;border: 1px solid #ababab;background-color:#f5f5f5;">
	        	<div class="left_down_file">
	        	<div>操作</div>
	        	<div><a href='#' onclick="toDownCurrent(this,'download')">下载</a></div>
	        	<div>
	        		<div>INFO</div>
	        		<div>
	        			<div><strong>最新更新时间</strong></div>
	        			<div>&nbsp;${fileinfo.createDate}</div>
	        			<div><strong>用户</strong></div>
	        			<div>&nbsp;${fileinfo.username}</div>
	        			<div><strong>组</strong></div>
	        			<div>&nbsp;${fileinfo.group}</div>
	        			<div><strong>大小</strong></div>
	        			<div>&nbsp;${fileinfo.filesize} bytes</div>
	        		</div>
	        	</div>
	        	</div>
        	</div>
        	<div style="float:left;width:70%;height:170px;margin-left:20px;">
        		<div style="height:30px;">${fileinfo.filename} (${fileinfo.filesize} b)</div>
        		<div style="border: 1px solid #ababab;height:170px;background-color:#f5f5f5;">
					<!-- div id="editArea" style="height: 170px; width:100%;  overflow-y: auto;">
						<pre style="padding:5px;">${fileinfo.filecontent}</pre>
					</div-->
					<textarea rows="9" style="width:98%" name="filecontent" readonly="readonly">${fileinfo.filecontent}</textarea>
				</div>
				<form method="post" action="${pageContext.request.contextPath }/download.xhtml" id="file_form_d" >
		        	<input type="hidden" value="" name="downfilename" id="current_downfile" />
		        	<input type="hidden" value="" name="downfiledir" id="current_downdir" />
		        </form>
				<div style="margin:10px">相关推荐</div>
				<div style="border: 1px solid #ababab;height:150px;background-color:#f5f5f5;">
					<c:forEach items = "${solrDocBeanList}" var = "b" varStatus="status">
						<div style="margin:10px;">
							<div style="float:left"><a href="#" onclick='toCurrent(this)'>${b.filename}</a><input type="hidden" value="${b.hdfspath}" /></div>
							<div style="float:right">${b.createdTime}</div>
							<div style="clear:both"></div>
						</div>
					</c:forEach>
				</div>
        	</div>
        	<div style="clear:both"></div>
        </div>
    </div>
</div>
</div>
</body>
</html>
