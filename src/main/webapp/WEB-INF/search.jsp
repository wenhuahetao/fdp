<%@ page contentType="text/html; charset=UTF-8" %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ page isELIgnored="false"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>非线大数据云服务平台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/bootstrap/js/bootstrap.min.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.ui.core.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/jquery.ui.datepicker.css" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css" />
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap-3.3.5.css" rel="stylesheet">
<link href="<%=request.getContextPath()%>/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/Jrquest.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/HdfsUtil.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-ui.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ui.core.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ui.datepicker.js"></script>
<style>
	.panel_inner_block{
		border-bottom:1px solid #e5e5e5;
		margin-bottom: 20px;
	}
	label.control-label span { cursor: pointer; }
	h2 {
		padding:0;
		margin-bottom: 20px !important;
		font-size:34px !importatn;
		font-family: "SimHei"
	}
</style>
<script type="text/javascript">
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="solr.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
});
</script>
</head>
<body>
<%@ include file="head.jsp"%>
<div id="loading" style="position:absolute; top:50%; left:50%; ">
		<img src="../images/ajax-loader.gif">
</div>
     <form method="post" action="${pageContext.request.contextPath }/download4txt.xhtml" id="file_form_d" >
        	<input type="hidden" value="" name="downfilename" id="current_downfile" />
        	<input type="hidden" value="" name="downfiledir" id="current_downdir" />
     </form>			
		
<div class="maincontent" style="margin-top:20px">
	<div class="row">
		<div class="col-md-3">
			<div class="panel panel-default">
			  <div class="panel-heading">
			    <h3 class="panel-title">数据字段</h3>
			  </div>
			  <div class="panel-body">
			  	 <div class="panel_inner_block">
			  	 	<p><strong>数据源</strong></p>
				  	<div class="radio">
					  <label>
					    <input type="radio" name="optionsRadios" id="optionsRadios1" value="1" checked>
					    文件
					  </label>
					</div>
					<div class="radio">
					  <label>
					    <input type="radio" name="optionsRadios" id="optionsRadios2" value="2" >
					    数据表
					  </label>
					</div>
					<div id="selectdatebase">
						<select id="select" class="form-control">
						<c:forEach items = "${DBlist}" var = "b" varStatus="status">
						  <option>${b}</option>
					 	 </c:forEach>
						</select>
					</div>
			  	 </div>
			  	 <div class="panel_inner_block" style="padding-bottom:15px">
			  	 	<p><strong>时间范围</strong></p>
			  	 	<div class="checkbox">
			  	 		<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox7" value="option1"> 
						</label>
					</div>
					<div id="Date">
					<div class="control-group">
				        <div class="controls">
				            <div class="input-group">
				                <input id="startDate" type="text" class="date-picker form-control" />
				                <label for="startDate" class="input-group-addon btn">
				                	<span class="glyphicon glyphicon-calendar"></span>
				                </label>
				            </div>
				        </div>
				    </div>
				    <div class="control-group" style="margin-top:15px">
				        <div class="controls">
				            <div class="input-group">
				                <input id="endDate" type="text" class="date-picker form-control" />
				                <label for="endDate" class="input-group-addon btn">
				                	<span class="glyphicon glyphicon-calendar"></span>
				                </label>
				            </div>
				        </div>
				    </div>
				    </div>
			  	 </div>
			  	 <div>
			  	 	<p><strong>标签</strong></p>
			  	 	<div>
				  	 	<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox1" value="option1"> 大数据
						</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp
						<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox2" value="option2"> 大数据
						</label>
					</div>
					<div>
						<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox3" value="option1"> 云服务平台
						</label>&nbsp;&nbsp;&nbsp;&nbsp;
						<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox4" value="option2"> 云服务平台
						</label>
					</div>
					<div>
						<label class="checkbox-inline">
						  <input type="checkbox" id="inlineCheckbox5" value="option1"> 集成
						</label>&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<label class="checkbox-inline" >
						  <input type="checkbox" id="inlineCheckbox6" value="option2"> 集成
						</label>
					</div>
			  	 </div>
			  </div>
			</div>
		</div>
		<div class="col-md-9">
			
			<div class="input-group">
			      <input type="text" class="form-control" id="filename" placeholder="搜索...">
			      <span class="input-group-btn">
			        <button class="btn btn-default" type="button" id="serach">搜索</button>
			      </span>
			</div>
			<div id="file"  class="bs-glyphicons" style="margin-top:30px">
			
			</div>
			
		</div>
	</div>
</div>
<script type="text/javascript">
 $(function(){
	 $("#loading").hide();
	 if($("#optionsRadios1").is(":checked")){
		$("#selectdatebase").hide();
	}
	 else{
		$("#selectdatebase").show();
	}
	 
	 
	 $("input:radio").click(function(){
		if($("#optionsRadios1").is(":checked")){
			$("#selectdatebase").hide();
		}
		else{
			$("#selectdatebase").show();
		}
     });
	 
	 $("#startDate").datepicker();
	 $("#endDate").datepicker();
	 
	 //搜索
	 $("#serach").click(function(){
		 $("#loading").show();
		 var flag=$(':radio[name="optionsRadios"]:checked').val();
		 var flagtime="0";
		 var filename=$("#filename").val();
		 var tablename="";
		 var st="";
		 var et="";
		 if(filename==""){
			 alert("请输入关键字");
			 return false;
		 }
		 if(flag==2){
			 tablename=$("#select  option:selected").text();
		 }
		 if( $("#inlineCheckbox7").attr("checked")){
			 flagtime="1";
			 st=$("#startDate").val();
			 et=$("#endDate").val();
		 }
		 var parms="flag="+flag+"&filename="+filename+"&tablename="+tablename+"&st="+st+"&et="+et+"&flagtime="+flagtime+"";
		 $.ax("ajaxJump",parms,"POST","text",function(msg){
			 $("#loading").hide();
			 var dataObj=eval("("+msg+")");
			 var html="";
			 if(flag==1){
				 var date=dataObj.listsolr; 
				  html="<div>搜索结果："+date.length+"个匹配,耗时:"+dataObj.allMili+"秒</div>";
				 for(var i=0;i<date.length;i++){
					 var sub=date[i].filecontent;
					 var strlength=sub.length;
					 if(strlength>150){
						 sub=sub.substring(0,150)+"......";
					 }
					 html=html+"<div style='margin-top:30px'>";
					 html+="<span style='font-size:22px'><a href='#' onclick=gofile('"+date[i].filename+"','"+date[i].hdfspath+"')>"+date[i].filename+"</a></span>";
					 html+="<span style='font-size:12px'></span><br>";
					 html+="<span style='font-size:17px; color:green'>"+date[i].hdfspath+"</span>";
					 html+="<span style='font-size:12px'>&nbsp;&nbsp;&nbsp;&nbsp;"+date[i].createdTime+"</span><br>"
					 html+="<span style='font-size:12px'>"+sub+"</span></div>";
				 }
			 }else{
				 var cols=dataObj.cols;
				 var rowData=dataObj.listsolr;
				 html="<div>搜索结果："+rowData.length+"个匹配,耗时:"+dataObj.allMili+"秒</div>";
				 html+="<div style='margin-top:20px'><table class='table table-bordered'>";
				 html+="<thead><tr>";
				 for(var i=0;i<cols.length;i++){
					 html+="<th>"+cols[i]+"</th>"
				 }
				 html+="</tr></thead><tbody>";
				 
				 for(var j=0;j<rowData.length;j++){
					 html+="<tr>";
				     for(var p=0; p<cols.length; p++) {
						html+="<td>"+rowData[j][cols[p]]+"</td>"
					 }
					 html+="</tr>"
				 }
			     html+="</tbody></table></div>";
			 }
			 $("#file").html(html); 
		 },
		 function(msg){
			 $("#loading").hide();
			 alert("数据查询有错误");
		 });
	 });
	 
 });
 
 function gofile(filename,filepath){
	 $("#current_downfile").val(filename);
	 $("#current_downdir").val(filepath.substring(0,filepath.lastIndexOf("/")));
	 $("#file_form_d").submit();
 }

</script>
</body>
</html>