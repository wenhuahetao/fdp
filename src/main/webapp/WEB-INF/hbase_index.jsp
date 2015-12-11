<%@page import="java.util.Set"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.jasper.tagplugins.jstl.core.ForEach"%>
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
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tdchange.js"></script>
</head>
<style>
ul.licss{ margin:0 auto; background:url(ul-bg.gif); width:100%; text-align:left} 
/* 背景只引人图片 不用设置其它参数即可对象内全屏平铺 */ 
ul.licss li{text-indent:10px; height:34px; line-height:34px;overflow:hidden;} 
/* 高度需要计算好，与布局图片一定关系 */ 
ul.licss li:hover{ background:#EBEBEB} 
/* 为了有动感背景变色换色，对li设置hover伪类 */ 
label{
	font-size: 16px;
}
.form-inline label {
	font-size: 14px;
	display:block !important;
	font-weight:bold;
}
.form-group {
    margin-bottom: 15px;
}
.bkgdEb{background:#EBEBEB}
.left_down_file{padding:20px;}
.left_down_file div{margin-top:10px}
.col-md-6{
	position: relative;
  min-height: 1px;
  padding-right: 15px;
  padding-left: 15px;
	width: 45%;
	
}
.import_err_tip{display: none}
.list-group {
    padding-left: 0;
    margin-bottom: 20px;
}
a.list-group-item, button.list-group-item {
    color: #555;
}
.list-group-item {
    position: relative;
    display: block;
    padding: 10px 15px;
    margin-bottom: -1px;
    background-color: #fff;
    border: 1px solid #ddd;
}
a {
    color: #337ab7;
    text-decoration: none;
}
a {
    background-color: transparent;
}
.list-group a:hover{
	background-color: #eeeeee;
}
td{
 text-align:center !important;
 vertical-align:middle !important;
}
a:link {
text-decoration: none;
}
a:visited {
text-decoration: none;
}
a:hover {
text-decoration: none;
}
a:active {
text-decoration: none;
}
li{
 margin-top:1px !important;
 padding-right:5px !important;
}
.hbase_set_step{font-size:16px;margin-left:20px}
#box1,#box2,#box3,#box4{ display:none;}
.circle {
	border-radius: 50%;
	width: 40px;
	height: 40px; 
	font-size:20px;
	color:#ffffff;
	line-height:40px;
	text-align:center;
	-webkit-box-shadow: 4px 4px 3px 0px rgba(50, 50, 50, 0.75);
	-moz-box-shadow:    4px 4px 5px 0px rgba(50, 50, 50, 0.75);
	box-shadow:         4px 4px 3px 0px rgba(50, 50, 50, 0.75);
	float:left;
}
.active {
	background-color:#43c400;
}
.inactive {
	background-color:#555555;
}
span{
  font-size:14px;
  display:inline;
  vertical-align: middle;
    line-height: 40px;
    margin-left: 10px;
}
.btn{
 margin-left:20px
}
table{
    table-layout:fixed;/* 只有定义了表格的布局算法为fixed，下面td的定义才能起作用。 */
}
td{
	height:100%;
    word-break:keep-all;/* 不换行 */
    white-space:nowrap;/* 不换行 */
    overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
    text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
td div{
	width :90%
	height:100%;
    word-break:keep-all;/* 不换行 */
    white-space:nowrap;/* 不换行 */
    overflow:hidden;/* 内容超出宽度时隐藏超出部分的内容 */
    text-overflow:ellipsis;/* 当对象内文本溢出时显示省略标记(...) ；需与overflow:hidden;一起使用。*/
}
.maskpb {
   display:none;
   background-color:#dddddd;z-index:999999;position:absolute;width:100%;height:372px;
   opacity: 0.5;
  -ms-filter:"progid:DXImageTransform.Microsoft.Alpha(Opacity=50)";
  filter: alpha(opacity=50);
}
</style>
<script type="text/javascript">
$(document).ready(function() { 
	$(".head_fx").find(".fx img").each(function(){
		var src = $(this).attr("src");
		var filename = src.substr(src.lastIndexOf("/")+1);
		if(filename=="hbase.png"){
			var newFilename = filename.replace(".png","_1.png");
			var newSrc = src.substr(0,src.lastIndexOf("/")+1)+newFilename;
			$(this).attr("src",newSrc);
		}
	});
});
</script>
<script>
(function () {
    var _skin, _lhgcore;
    var _search = window.location.search;
    if (_search) {
        _skin = _search.split('demoSkin=')[1];
    };
    document.write('<scr' + 'ipt src="<%=request.getContextPath()%>/js/lhgdialog.min.js?skin=' + (_skin || 'default') + '"></sc' + 'ript>');
    window._isDemoSkin = !!_skin;
})();
/*function db_xz(obj){
	var db_name = $(obj).html();
	var parms = "dbtablename="+db_name;
	$(".licss li").each(function(){
		$(obj).removeClass("bkgdEb");
	});
	$(obj).parent().addClass("bkgdEb");
	WindowOpener.ajaxGetDBField(parms);
}*/

function hbase_xzzd(vl,type){
	var checkedhtml = "";
	$(".hbase_table_field_list tbody input[type=checkbox]").each(function(){
	    if(this.checked){
	    	checkedhtml = checkedhtml + "," + $(this).parent().next().html();
	    }
	});  
	vl = "hcheckbox_"+vl;
	opento(checkedhtml,vl,type);
}
function opento(p,rodom,type){
	var parms = "?xzcols="+p+"&rodom="+rodom+"&type="+type;
	$.dialog({
		id: 'xz_field_dialog',
		closeOnEscape: false,
		title:'选择字段',
		width: '400px', 
		height: '400px',
		zIndex: 999999,
		fixed:true,
		cache:false,
		content: "url:listXzzd"+parms,
		close: function() {
		}
	}
	);
}
function toDownCurrent(obj,type){
	$("#file_form_d").attr("action", HdfsUtil.getRootPath() + "/"+type+"Txt.xhtml" );
	$("#file_form_d").submit();
}
$(document).ready(function() {  
	var MaxInputs       = 8; //maximum input boxes allowed  
	var InputsWrapper   = $("#InputsWrapper"); //Input boxes wrapper ID  
	var AddButton       = $("#AddMoreFileBox"); //Add button ID  
	var x = InputsWrapper.length; //initlal text box count  
	var FieldCount=1; //to keep track of text box added  
	$(AddButton).click(function (e) {  
	        if(x <= MaxInputs) //max input box allowed  
	        {  
	            FieldCount++; //text box added increment  
	            //add input box  
	            $(InputsWrapper).append('<div style="margin-top:5px"><input type="text" name="familyname" id="field_'+ FieldCount +'" /><a href="#" class="removeclass btn btn-info">×</a></div>');  
	            x++; //text box increment  
	        }  
	return false;  
	});  
	$("body").on("click",".removeclass", function(e){ //user click on remove text  
	        if( x > 1 ) {  
	                $(this).parent('div').remove(); //remove text box  
	                x--; //decrement textbox  
	        }  
			return false;  
	});   
});  
$(document).ready(function(){
	//submit form
		$(".btn1").click(function(){
		    $("#box").css('display','none');
			$("#box1").css('display','block');
			$("#box2").css('display','none');
			$("#selectTFLoader").show();
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#tbl").removeClass("inactive").addClass("active");
			
			var dbtype = $("#dbtype").val();
			var dbname = $("#dbname").val();
			var url = $("#url").val();
			var username = $("#username").val();
			var passwd = $("#passwd").val();
			var params = "dbtype="+dbtype+"&dbname="+dbname+"&url="+url+"&username="+username+"&passwd="+passwd;
			WindowOpener.ajaxGetDateBase(params);
			
		  });
		  $(".btn2").click(function(){
		    $("#box").css('display','none');
			$("#box1").css('display','none');
			$("#box2").css('display','block');
			
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#mapping").removeClass("inactive").addClass("active");
		  });
		  $(".btn3").click(function(){
		    $("#box").css('display','block');
			$("#box1").css('display','none');
			$("#box2").css('display','none');
			
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#dbcon").removeClass("inactive").addClass("active");
		  });
		  
		  $(".btn5").click(function(){
		    $("#box").css('display','none');
			$("#box1").css('display','block');
			$("#box2").css('display','none');
			
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#tbl").removeClass("inactive").addClass("active");
		  });
		  $(".btn4").click(function(){
			$("#box").css('display','none');
			$("#box1").css('display','none');
			$("#box2").css('display','none');
			$("#box3").css('display','block');
			
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#import").removeClass("inactive").addClass("active");
			var dbtablename = $(".licss .bkgdEb").find("a").html();
			var dbtype = $("#dbtype").val();
			var dbname = $("#dbname").val();
			var url = $("#url").val();
			var username = $("#username").val();
			var passwd = $("#passwd").val();
			var params = "dbtype="+dbtype+"&dbname="+dbname+"&url="+url+"&username="+username+"&passwd="+passwd+"&dbtablename="+dbtablename;
			WindowOpener.ajaxGetBaseCount(params);
			
		  });
		  
     //submit form 
     $(".btn6").click(function(){
    	    $("#progressBar").show();
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#import").removeClass("inactive").addClass("active");
			
			// checkbox 选中值 
			var checkedhtml = "";
			$(".hbase_table_field_list tbody input[type=checkbox]").each(function(){
			    if(this.checked){
			    	checkedhtml = checkedhtml + "," + $(this).parent().next().html();
			    }
			});  
			//1.数据库选中列
			checkedhtml = checkedhtml.substr(1);
			$("#rdbcolumns").val(checkedhtml);
			
			//2.Rowkey组合
			var sel1 = $("#hbase_sel_1").val();
			var sel2 = $("#hbase_sel_2").val();
			var sel3 = $("#hbase_sel_3").val();
			$("#hbase_rowkey").val(sel1+","+sel2+","+sel3);
			
			//3.数据库表名
			$("#rdbtablename").val($(".licss .bkgdEb").find("a").html());
			
			//4.partitioncolumn
			var hstotal="";
			var hs1 = $(".hbase_sel_1").val();
			if(hs1!=0){
				hstotal += "," + hs1;
			}
			var hs2 = $(".hbase_sel_2").val();
			if(hs2!=0){
				hstotal += "," + hs2;
			}
			var hs3 = $(".hbase_sel_3").val();
			if(hs3!=0){
				hstotal += "," + hs3;
			}
			if(hstotal.length>0){
				hstotal = hstotal.substr(1);
			}
			$("#partitioncolumn").val(hstotal);
			
			//4.family
			var familyParms="[";
			var len = $(".hbase_family tbody tr").length;
			$(".hbase_family tbody tr").each(function(index){
				
				var family = $(".family_td").html();
				if($(this).find(".family_td input").length>0){
					family = $(this).find(".family_td input").val();
				}
				var family_cols = $(".family_cols_td").html();
				if($(this).find(".family_cols_td input").length>0){
					family_cols = $(this).find(".family_cols_td input").val();
				}
				
				var family_colsStr = family_cols.split(",");      
				for (i=0;i<family_colsStr.length ;i++ ){
					familyParms += '{"family_td":' + '"'+family+'"'
						+ ',"family_cols_td":' + '"'+family_colsStr[i] +'"'+ '}';
						
						if(i!=family_colsStr.length-1){
							familyParms += ",";
						}
			    }
				if (index < len - 1){
					familyParms = familyParms + ",";
				}
				
			});
			familyParms += "]";
			var parms = '{"familyCols": ' + familyParms+ '}';
			$('#hbaseImportForm').attr("action", HbaseUtil.getRootPath()+"/hbase/hbaseImport.xhtml?familyParms="+parms);
			//进度条
			//$('#myModal1').modal('show');  
			//MyModal.initRunParm();
			//MyModal.run();
			
			$.ajax({type:'POST', url: '<%=request.getContextPath()%>/hbase/hbaseImport.xhtml?familyParms='+parms, 
					data:$('#hbaseImportForm').serialize(), 
				success: function(response) {
					$("#progressBar").hide();
					//close dialog
					window.location.reload();
				},
				error: function(response, errorCode) {
					//display error message
					$("#progressBar").hide();
				}
			});
			
			$("#hbaseImportForm").submit(function(ev){
				$("#progressBar").dialog("open");
				return true;
			});
	});
		  
		  $(".btn7").click(function(){
			$("#box").css('display','none');
			$("#box1").css('display','none');
			$("#box2").css('display','block');
			$("#box3").css('display','none');
			
			$(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#mapping").removeClass("inactive").addClass("active");
		  });
		  $(".btn8").click(function(){
		    $(".mibao").css('display','none');
		    $(".circle").each(function(){
				$(this).removeClass("active").addClass("inactive");
			});
			$("#import").removeClass("inactive").addClass("active");
		  });
		  
		});
</script>
<body>

<div class="action-box">

<!-- 新建-->
<div class="modal fade" id="myModal3" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
      <form method="post" action="${pageContext.request.contextPath }/hbase/createTable.xhtml" id="newForm" class="form-inline"  >
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">新建<span id="file_dn"></span></h4>
                </div>
                <div class="modal-body">
                	<div class="form-group">
					    <label for="hbase_family">表名</label>
					    <input type="text" id="tabname" name="tabname" class="form-control" placeholder="请输入表名" aria-describedby="sizing-addon1">
					</div>
                
                	<div class="form-group">
						<label for="hbase_family">family</label>
						
						<div id="InputsWrapper">  
							<div><input type="text" name="familyname" id="field_1" value=""/><a href="#" id="AddMoreFileBox" class="btn btn-success">+</a></div>  
						</div>
					</div>
					<div style="float:right">
						<a class="btn btn-default hidebar" href="#" role="button">取消</a> 
						<a class="btn btn-success btn1" id="new_tj"　href="#" role="button">提交</a>
					</div>
                </div>
               
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div><!-- /.modal -->


<div class="modal fade" id="myModal1" tabindex="-1" role="dialog" 
   aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static" style="width:300px;z-index:-1000"> 
   <div class="modal-dialog">
       <div class="modal-content" >
           <span style="text-align:center;">文件正在下载请勿刷新页面！</span><br />
          
           <div class="progress progress-striped active">
               <div class="bar">
               </div>
           </div>
       </div>
   </div>
</div>


<div  class="modal fade" id="myModal2" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static" 
  style="width:60%;margin-left:-30%;min-height:450px;z-index:-1000">
  <form method="post" action="${pageContext.request.contextPath }/hbase/hbaseImport.xhtml" id="hbaseImportForm">
        <input type="text" name="rdbtablename" id="rdbtablename">
		<input type="hidden" name="rdbcolumns" id="rdbcolumns">
		<input type="hidden" name="rowkeyparam" id="hbase_rowkey">
        <input type="hidden" name="partitioncolumn" id="partitioncolumn">
      
        <input type="hidden" name="rowkey_select_1" class="hbase_sel_1" value="0">
        <input type="hidden" name="rowkey_select_2" class="hbase_sel_2" value="0">
        <input type="hidden" name="rowkey_select_3" class="hbase_sel_3" value="0">
        
        <div class="modal-dialog">
            <div class="modal-content" >
                <div class="modal-header">
                	<h2>导入表</h2>
                </div>
                <div id="progressBar" class="maskpb">
					<div style="width:100%; margin-top:20%;vertical-align:middle;text-align:center;">
				    	<img src="/fline_hdfs/images/ajax-loader-progress.gif">
				    </div>
				</div>
                <div class="modal-body">
                	
                	<div class="fl hbase_set_step"  style="margin-left:0 !important"><div id="dbcon" class="circle active">1</div><span>设置数据库连接</span></div>
                	<div class="fl hbase_set_step"><div id="tbl" class="circle inactive">2</div><span>选择导入表和字段</span></div>
                	<div class="fl hbase_set_step"><div id="mapping" class="circle inactive">3</div><span>Hbase字段映射设置</span></div>
                	<div class="fl hbase_set_step"><div id="import" class="circle inactive">4</div><span>导入</span></div>
                	<div class="fl"></div>
                	<div style="clear:both"></div>
               		<br />
	                <div class="mibao">
						<div id="box" style="padding-bottom:20px">
							<table class="table table-bordered">
								<tr><td>数据库类型</td><td><select id="dbtype" name="dbtype"><option>oracle</option><option selected>mysql</option></select></td></tr>
								<tr><td>数据库名</td><td><input type="text" value="lvbb" id="dbname" name="dbname"/></td></tr>
								<tr><td>数据库连接</td><td><input type="text" value="121.40.19.144"  id="url" name="connectionurl"/></td></tr>
								<tr><td>用户名</td><td><input type="text" id="username" value="zhangyue" name="username"/></td></tr>
								<tr><td>密码</td><td><input type="text" id="passwd" value="123456" name="password"/></td></tr>
							</table>
						
							<div style="float:right">
								<a class="btn btn-default hidebar" href="#" role="button">取消</a> 
								<a class="btn btn-success btn1" href="#" role="button">下一步</a>
						    </div>
						</div>
						
						<div id="box1" style="padding-bottom:20px">
						    <div id="selectTFLoader" style="display:none;width:100%; height:80px; text-align:center;vertical-align:middle">
								<img src="/fline_hdfs/images/ajax-loader-progress.gif">
							</div>
							<div class="fl hbase_table_list col-md-6" style="overflow:auto; height:300px">
								<table class="table table-bordered">
									<thead>
										<tr>
											<th>
												表名
											</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>
												
											</td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="fl hbase_table_field_list col-md-6" style="overflow:auto; height:300px">
								<table class="table table-bordered">
									<thead>
										<tr>
											<th style="text-align:center;"><input type="checkbox" id="checkedall"/></th>
											<th>
												字段名
											</th>
											<th>
												字段类型
											</th>
										</tr>
									</thead>
									<tbody>
										
									</tbody>
								</table>
							</div>
							<div style="clear:both"></div>
							<div style="float:right;margin-top:10px">
								<a class="btn btn-default hidebar" href="#"role="button">取消</a> 
								<a class="btn btn-default btn3" href="#" role="button">上一步</a>
								<a class="btn btn-success btn2" href="#" role="button">下一步</a>
						    </div>
						</div>
						
						<div id="box2" style="padding-bottom:20px;">
						    	<div class="form-group">
								    <label for="hbase_table_name">表名</label>
								    <div>
								    	<input type="text" id="hbase_table_name" name="hbasetable" class="form-control" />
								    	<button type="button"  data-toggle="modal"  class="btn btn-default showdispalybar" style="margin-bottom:10px" describe="c" id="showdispaly_copy" >...</button>
								    </div>
								    <div style="display:none;" id="show_i" class="modal-body">
								    
								    </div>
								</div>
						    
						    	<div class="form-group">
								    <label for="hbase_rowkey">设置Rowkey</label>
								     	<select id="hbase_sel_1">
								     		<option value="">--选择--</option>
								     		<option value="0">ID</option>
											<option value="timestamp" selected>时间戳(Timestamp)</option>
											<option value="radom16">16位随机数(0-9|a-z)</option>
											<option value="md5">MD5</option>
											<option value="5">选择字段</option>
										</select>
										+<select id="hbase_sel_2">
								     		<option value="">--选择--</option>
								     		<option value="0" selected>ID</option>
											<option value="timestamp">时间戳(Timestamp)</option>
											<option value="radom16">16位随机数(0-9|a-z)</option>
											<option value="md5">MD5</option>
											<option value="5">选择字段</option>
										</select>
										+<select id="hbase_sel_3">
								     		<option value="" selected>--选择--</option>
								     		<option value="0">ID</option>
											<option value="timestamp">时间戳(Timestamp)</option>
											<option value="radom16">16位随机数(0-9|a-z)</option>
											<option value="md5">MD5</option>
											<option value="5">选择字段</option>
										</select>
								</div>
								
								<div class="form-group">
								    <label for="hbase_family">设置Family</label>
								     	<table class="table table-bordered hbase_family">
											<thead>
												<tr>
													<th width="30%">
														Family名称
													</th>
													<th width="70%">
														<div style="float:left;margin-left:5px">Family Columns</div>
														<div style="float:right;"><a href='#' id='hbase_add_family' class="btn btn-success">+</a>&nbsp<a href="#" class="btn btn-info"id="hbase_del_family">×</a></div>
													</th>
												</tr>
											</thead>
											<tbody>
												
											</tbody>
										</table>
								</div>
							
							<div style="float:right">
								<a class="btn btn-default hidebar" href="#" role="button">取消</a> 
								<a class="btn btn-default btn5" href="#" role="button">上一步</a>
								<a class="btn btn-success btn4" href="#" role="button">下一步</a>
						    </div>
						</div>
						
						<div id="box3" style="padding-bottom:20px">
						    <div style="padding:30px 0 30px 0">
								<strong>导入行数 :</strong>
						    	<input type="text" id="hbase_colums_count" name="linenum" class="form-control" />
						    	<div class="import_err_tip" style="color:red"><strong>导入有错误，请重新导入</strong></div>
						    </div>
							<div style="float:left">
								<a class="btn btn-default hidebar" href="#" role="button" style="margin-left:0 !important">取消</a> 
								<a class="btn btn-default btn7" href="#" role="button">上一步</a>
								<a class="btn btn-success btn6" href="#" role="button">导入</a>
						    </div>
						</div>
						
						<!-- <div id="box4" style="padding-bottom:20px">
							<p>3</p>
							
							<a class="btn btn-default btn8" href="#" role="button">完成</a>
						</div> -->
						
					</div>	 
                </div>
                
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
     </form>
</div> 
<!-- /.modal -->
<div class="mainbody" style="z-index:9999;padding-bottom:50px">
		<%@ include file="head.jsp"%>
		<div class="maincontent">
			<div style="margin:30px 0 30px 0px">
		        <div style="height:20px;font-size:20px;" ><strong>&nbsp;&nbsp;Hbase</strong></div>
			</div>
        <div>
        	<div style="float:left;width:20%;min-height:500px;border: 1px solid #ababab;">
	        	<div class="left_down_file">
		        	<div>
		        		 <div class="fl" style="font-size:14px;padding:5px;"><strong>数据表</strong></div>
			        	 <div class="btn-group fl">
							  <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
							  	 操作
							    <span class="caret"></span>
							  </a>
							  <ul class="dropdown-menu">
							     <li><a href="#" data-toggle="modal"  id="new_b" >&nbsp;新建</a></li>
					             <li><a href="#"  data-toggle="moda2"  id="import_b">&nbsp;导入</a></li>
					             <li><a href="#"  data-toggle="moda3"  id="export_b">&nbsp;导出 </a></li>
							  </ul>
						 </div>
						 <div style="clear:both"></div>
		        	</div>
		        	
		        	<div>
		        	<ul class="licss"> 
			        	<c:forEach items = "${tableList}" var = "b" varStatus="status">
						<c:if test="${tablename==b}">
			        	<li style="background:#EBEBEB"><a href="#">${b}</a></li>
			        	</c:if>
			        	<c:if test="${tablename!=b}"><li><a href="#" onclick="WindowOpener.ajaxQueryTables(this);return false;">${b}</a></li></c:if>
						</c:forEach>
					</ul> 
		        	</div>
	        	</div>
        	</div>
        	<div style="float:left;width:70%;height:500px;margin-left:20px;">
        		<!-- curd function -->
        		<div style="height:30px;" class="fdp_curd" align="center">
		        	<div class="fl" style="margin-right:30px"><span style="font-size:20px">${tablename}</span></div>
		        	<div style="float:right">
		        		<div class="fl"><img src="<%=request.getContextPath()%>/images/edit.png" alt="" class="slogo"/></div><div class="fl" style="margin-right:20px">&nbsp;
		        		<a href='javascript:void' onclick='Cudq.Modify("${tablename}")'>修改</a>
		        		</div>
		        		<div class="fl"><img src="<%=request.getContextPath()%>/images/add.png" alt="" class="slogo"/></div><div class="fl" style="margin-right:20px">&nbsp;
		        		<a href='javascript:void' onclick='Cudq.addLine("${tablename}","${width}")'>新增行</a>
		        		</div>
		        		<div class="fl"><img src="<%=request.getContextPath()%>/images/shanchu.png" alt="" class="slogo"/></div><div class="fl" style="margin-right:20px">&nbsp;
		        		<a href='javascript:void' onclick='Cudq.delLines()'>删除</a>
		        		</div>
		        		<div class="fl"><img src="<%=request.getContextPath()%>/images/save.png" alt="" class="slogo"/></div><div class="fl" style="margin-right:20px">&nbsp;
		        		<a href='javascript:void' onclick='return false'>保存</a>
		        		</div>
	        		</div>
        		</div>
        		<div>
        			<table class="table table-bordered" id = "hbase_index_table">
        				<tr>
        					<td width="5%"><input type="checkbox" name="chall"/></td>
        					<td width="10%">Rowkey</td>
        					<c:forEach items = "${familyColums}" var = "basic" varStatus="status">
        						<td style="padding:0">
	        						  <table width="100%">
	        							<tr><td colspan="${sizeMap[basic.key]}" title="${basic.key}">${basic.key}</td></tr>
	        		 		 		    <tr>
	        							<td style="padding:0" colspan="${sizeMap[basic.key]}" title="">
	        								<hr style="height:1px;border:none;border-top:1px double #e5e5e5; width:100%; padding:0; margin:0"/>
	        							</td>
	        							</tr> 
	        							<tr>
	        								<c:forEach items="${basic.value}" var="colname">
		        								<td width="${width}%" title="${colname}">
		        								${colname}
		        								</td>
		        							</c:forEach>
	        							</tr>
	        						</table>
        					</c:forEach>
        				</tr>
        			
        			<c:forEach items = "${hbaselist}" var = "hbase" varStatus="status">
        				<tr>
        					<td><input type="checkbox" name="ch"/></td>
        					<td title="${hbase.rowkey}">${hbase.rowkey}</td>
        				<c:forEach items="${hbase.family}" var="family">
        					<td style="padding:0">
        						<table class="table" style="padding:0;margin:0">
        							<tr>
	        							<c:forEach items = "${familyColums}" var = "basic1" varStatus="status">
	        							<c:if test="${family.key==basic1.key}">
		        							<c:forEach items="${basic1.value}" var="colname">
		        								<!-- colum  -->
			        								<td width="${width}%"  onclick="switchtdinput(this)">
			        								<c:forEach items="${family.value}" var="colv">
			        									<c:if test="${colv.colum==colname}">
			        										 <div title="${colv.value}">${colv.value}</div>
			        									</c:if>
	        										</c:forEach>
			        								</td>
		        							</c:forEach>
	        							</c:if>
	        							</c:forEach>
        							</tr>
        						</table>
        					</td>
        				</c:forEach>
        				</tr>
        			</c:forEach>
        			</table>
        			<table>
        			<tr>
		        		<td colspan="9">
						<span>共${total}条;<span id='count'>总共${countPage}</span>页;</span>
						<span>第${pageIndex}页;</span>
						<c:if test="${pageIndex==1}">
							前一页
						</c:if>
						<c:if test="${pageIndex!=1}">
							<a href='javascript:void' onclick='Pager.PrePage(${countPage},${pageIndex},"/hbase/queryList?tableName=${tablename}")' id='aPrePage' >前一页</a>
						</c:if>
						<c:if test="${pageIndex==countPage}">
							下一页
						</c:if>
						<c:if test="${pageIndex<countPage}">
							<a href='javascript:void' onclick='Pager.NextPage(${countPage},${pageIndex},"/hbase/queryList?tableName=${tablename}")' id='aNextPage'>下一页</a>&nbsp;&nbsp;
						</c:if>
						&nbsp;&nbsp;
						</td>
        			</tr>
        			</table>
        		</div>
        		
        		
       
        	</div>
        	<div style="clear:both"></div>
        </div>
    </div>
</div>
</div>
</body>
</html>
