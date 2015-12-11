<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.sql.*,java.text.*,java.util.*"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>区域碰撞分析</title>
<link rel="stylesheet" href="css/bootstrap.css" />
<link rel="stylesheet"
	href="js/include/ui-1.10.0/ui-lightness/jquery-ui-1.10.0.custom.min.css"
	type="text/css" />
<!--link rel="stylesheet" href="css/jquery-ui-1.10.0.custom.css"
		<link rel="stylesheet" href="js/jquery.ui.timepicker.css"/>-->
<link rel="stylesheet" href="js/jquery-ui-timepicker-addon.css" />


<script type="text/javascript" src="js/jquery-1.10.2.js"></script>
<script type="text/javascript" src="js/jquery-ui.js"></script>
<script type="text/javascript" src="js/jquery-ui-i18n.js"></script>
<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
<h1 align="center"><strong>区域碰撞分析</strong></h1>
<style>
.title_b {
	padding: 6px 12px;
    color: #555;
    text-align: center;
    background-color: #fff;
    border: 1px solid #ccc;
}
input,select{width:130px}
.qycol-lg-12{padding:5px;}
.btn-success{width:100px;font-size:16px;font-weight:bold;}
.table-hover>tbody>tr:hover>td, .table-hover>tbody>tr:hover>th {
    background-color: #e9f5fe;
 }
.table-striped>tbody>tr:nth-child(odd)>td, .table-striped>tbody>tr:nth-child(odd)>th {
    background-color: #e9f5fe;
}
#loading {
	position: fixed;
	_position: absolute;
	top: 40%;
	left: 45%;
	width: 120px;
	height: 120px;
	overflow: hidden;
	background: url(img/loading1.gif) no-repeat;
	z-index: 3000;
}

#progressMask {
	display: block;
	width: 100%;
	height: 100%;
	opacity: 0.6;
	filter: alpha(opacity = 60);
	background: black;
	position: absolute;
	top: 0;
	left: 0;
	z-index: 2000;
}
</style>
</head>

<body>
<div id="progressMask" style="display:none"><div id="loading"></div></div>

<div style="margin:0 10px 10px 15px">
<div style="background-color: #ebebeb;padding:20px;border-color: #1e1e1e">
		<div class="row" id="condContainer" >
				<div class="row" style="margin-left: 13px">
				<div class="col-lg-12 condGroup">
					<label> <span class="title_b">地点1</span> <select id="location1"
						class="fullfilled form-control">
							<%
								//以下是从数据库里面读取出来的数据  
								Connection connl1 = null;
								PreparedStatement psl1 = null;
								ResultSet rsl1 = null;
								try {
									String url = "jdbc:oracle:thin:@33.175.29.56:1521:sxtraffic";
									connl1 = DriverManager.getConnection(url, "traffic_flow",
											"sx123");
									String sql = "  select distinct(d.point_code) as sn, max(d.point_name) as bayonet_name from traffic_infrastru_adm.t_device_info d    where ( d.device_type_id = 15 or d.device_type_id = 16)  and d.point_code is not null  and d.point_name is not null  group by  d.point_code  order by bayonet_name";
									psl1 = connl1.prepareStatement(sql);
									rsl1 = psl1.executeQuery();
									while (rsl1.next()) {
										out.println("<option value='" + rsl1.getString("sn") + "'>"
												+ rsl1.getString("bayonet_name") + "</option>");

									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (rsl1 != null) {
										rsl1.close();
									}
									if (psl1 != null) {
										psl1.close();
									}
									if (connl1 != null) {
										connl1.close();
									}
								}
							%>

					</select> <!--<input type="text" id="location1" name="textfield1" class="form-control location" />-->
					</label> <label style="margin-left:20px;"> <span class="title_b">开始时间</span> <input type="text" id="startTime1"
						name="textfield2" class="form-control startTime" />
					</label> <label style="margin-left:20px;"> <span class="title_b">结束时间</span> <input type="text" id="endTime1"
						name="textfield3" class="form-control endTime" />
					</label>
					<label style="font-size:16px;color:red">&nbsp;&nbsp;建议时间间隔在1小时内，性能为最佳。 </label>
				</div>
			</div>

			<div class="row" style="margin-left: 13px">
				<div class="col-lg-12 condGroup">
					<label> <span class="title_b">地点2</span> <select id="location2" class="form-control">
							<%
								//以下是从数据库里面读取出来的数据  
								Connection connl2 = null;
								PreparedStatement psl2 = null;
								ResultSet rsl2 = null;
								try {
									String url = "jdbc:oracle:thin:@33.175.29.56:1521:sxtraffic";
									connl2 = DriverManager.getConnection(url, "traffic_flow",
											"sx123");
									String sql = "  select distinct(d.point_code) as sn, max(d.point_name) as bayonet_name from traffic_infrastru_adm.t_device_info d    where ( d.device_type_id = 15 or d.device_type_id = 16)  and d.point_code is not null  and d.point_name is not null  group by  d.point_code  order by bayonet_name";
									psl2 = connl2.prepareStatement(sql);
									rsl2 = psl2.executeQuery();
									while (rsl2.next()) {
										out.println("<option value='" + rsl2.getString("sn") + "'>"
												+ rsl2.getString("bayonet_name") + "</option>");

									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									if (rsl2 != null) {
										rsl2.close();
									}
									if (psl2 != null) {
										psl2.close();
									}
									if (connl2 != null) {
										connl2.close();
									}
								}
							%>
					</select> <!--<input type="text" id="location2"class="form-control location" />-->
					</label> <label style="margin-left:20px;"> <span class="title_b">开始时间</span> <input type="text" id="startTime2"
						name="textfield22" class="form-control startTime" />
					</label> <label style="margin-left:20px;"> <span class="title_b">结束时间</span> <input type="text" id="endTime2"
						name="textfield32" class="form-control endTime" />
					</label>
					 <label style="color:red">&nbsp;&nbsp;建议时间间隔在1小时内，性能为最佳。 </label>
				</div>
			</div>

			<div class="row" id="model" style="margin-left: 13px; display: none">
				<div class="col-lg-12">
					<label class="selectlable"> <span id="localLable" class="title_b">地点</span>
						<!--<input type="text" id="location"class="form-control location" />-->
					</label> <label style="margin-left:20px;"> <span class="title_b">开始时间</span> <input type="text" id="startTime"
						class="form-control startTime" />
					</label> <label style="margin-left:20px;"> <span class="title_b">结束时间</span> <input type="text" id="endTime"
						class="form-control endTime" /> <input type="button" value="X"
						class="removeBtn">
					</label>
					 <label style="color:red">&nbsp;&nbsp;建议时间间隔在1小时内，性能为最佳。 </label>
				</div>
			</div>
		</div>
			<div class="row" style="margin-left: 13px">
				<label> <span class="title_b">百分比</span> <select id="bfb" class="form-control"
					style="width: 110px">
						<!-- option value="10">10</option>
						<option value="20">20</option>
						<option value="30">30</option>
						<option value="40">40</option>
						<option value="50">50</option -->
						<option value="60">60</option>
						<option value="70">70</option>
						<option value="80">80</option>
						<option value="90">90</option>
						<option value="100" selected="selected">100</option>
				</select> %
				</label> <input name="addNewCondBtn" type="button" id="addNewCondBtn"
					value="新增碰撞条件" /> <input name="queryBtn" type="button"
					id="queryBtn" value="查询" />
			</div>
</div>
</div> 	
 	
<div id="right_info" style="margin-left:5px;height:100%;margin-top:20px">
       <!--表头-->
       <div id="mytable" style="margin-left: 13px;margin-right: 13px;width: 98%;">
           <table id="tab1" class="table table-striped table-bordered table-hover" style="width: 100%;margin-bottom:1px">
           	<tr class="right_gcsj"><td>车牌号<td>地点1</td><td>地点2</td></tr>
           </table>
       </div>
       <div id="container" style="margin-left: 13px;margin-right: 13px;width: 98%;">
           <table id="tab2" class="table table-striped table-bordered table-hover" >
           </table>
       </div>
</div>
 
<div id="hidden" align="center" style="display: 'none'">
	<form id="postForm" action="" method="post"></form>
</div>
<script type="text/javascript">
	function goDetail(plateno){
		var params = "[";
		var len = $(".condGroup").length;
		$(".condGroup").each(
			function(index, element) {
				var location = $(this).find(
						"select option:selected")
						.val();
				var startTime = $(this).find(
						".startTime").val();
				var endTime = $(this).find(
						".endTime").val();
				var st = $.myTime
						.DateToUnix(startTime)
						+ "000";
				var et = $.myTime
						.DateToUnix(endTime)
						+ "000"; 
				params += '{"bayonetId":"'
					+ location
					+ '","starttime":' + st
					+ ',"endtime":' + et + '}';
			if (index < len - 1)
				params += ",";
				if (index < len - 1)
					params += ",";

		});
		params += "]";
		params1 = '{"bayonetDetailDomains": ' + params
				+ ',"plateNo":"' + plateno + '"}';
		window.open("qypzfxDetail.jsp?params="+params1);
	}
	function gomingxi(code) {
		$("#postForm").html('');//防止元素重复
		$("#postForm").append(
				'<input type="hidden" name="data" value="'+code+'"/>');
		$("#postForm").attr("target", "newwin");
		$("#postForm").attr("action", "qypzfxmx.jsp");
		//window.open("about:blank","newwin","");//newWin 是上面form的target
		$("#postForm").submit();
	}
	(function($) {
		$.extend({
			myTime : {
				/**
				console.log($.myTime.DateToUnix('2014-5-15 20:20:20'));
				console.log($.myTime.UnixToDate(1325347200));
				 * 当前时间戳
				 * @return <int>        unix时间戳(秒)  
				 */
				CurTime : function() {
					return Date.parse(new Date()) / 1000;
				},
				/**              
				 * 日期 转换为 Unix时间戳
				 * @param <string> 2014-01-01 20:20:20  日期格式              
				 * @return <int>        unix时间戳(秒)              
				 */
				DateToUnix : function(string) {
					var f = string.split(' ', 2);
					var d = (f[0] ? f[0] : '').split('/', 3);
					var t = (f[1] ? f[1] : '').split(':', 3);
					var unixTime = (new Date(parseInt(d[0], 10) || null,
							(parseInt(d[1], 10) || 1) - 1, parseInt(d[2],
									10)
									|| null, parseInt(t[0], 10) || null,
							parseInt(t[1], 10) || null, parseInt(t[2], 10)
									|| null)).getTime() / 1000;
					return unixTime;
				},
				/**              
				 * 时间戳转换日期              
				 * @param <int> unixTime    待时间戳(秒)              
				 * @param <bool> isFull    返回完整时间(Y-m-d 或者 Y-m-d H:i:s)              
				 * @param <int>  timeZone   时区              
				 */
				UnixToDate : function(unixTime, isFull, timeZone) {
					if (typeof (timeZone) == 'number') {
						unixTime = parseInt(unixTime) + parseInt(timeZone)
								* 60 * 60;
					}
					var time = new Date(unixTime * 1000);
					var ymdhis = "";
					ymdhis += time.getUTCFullYear() + "-";
					ymdhis += (time.getUTCMonth() + 1) + "-";
					ymdhis += time.getUTCDate();
					if (isFull === true) {

						ymdhis += " " + time.getUTCHours() + ":";
						if (time.getUTCMinutes() > 9) {
							ymdhis += time.getUTCMinutes() + ":";
						} else
							ymdhis += "0" + time.getUTCMinutes() + ":";
						if (time.getUTCSeconds() > 9) {
							ymdhis += time.getUTCSeconds();
						} else
							ymdhis += "0" + time.getUTCSeconds();
					}
					return ymdhis;
				}
			}
		});
	})(jQuery);
	var indexNum = 3;

	function removeRow(rnum) {
		jQuery('#rowNum' + rnum).remove();
	}

	$(function() {
		//initlize datetime picker
		$("#startTime1").datetimepicker();
		$("#endTime1").datetimepicker();
		$("#startTime2").datetimepicker();
		$("#endTime2").datetimepicker();

		// add new query condition group
		$("#addNewCondBtn")
				.click(
						function(e) {
							var $newCond = $("#model").clone();
							$newCond.removeAttr("style").removeAttr("id");
							$newCond.css({
								"margin-left" : "13px"
							});
							$newCond.find(".qycol-lg-12").addClass(
									"condGroup");
							$newCond.find(".removeBtn").click(function() {
								$newCond.remove();
							});

							$newCond.find("#localLable").html(
									$newCond.find("#localLable").text()
											+ indexNum);
							var $clonedSelect = $(".fullfilled").clone();
							$clonedSelect.removeClass("fullfilled");
							$clonedSelect
									.attr("id", "localtion" + indexNum);
							$newCond.find(".selectlable").append(
									$clonedSelect);
							$newCond.find("#location").attr("id",
									"localtion" + indexNum);
							$newCond.find(".startTime").attr("id",
									"startTime" + indexNum)
									.datetimepicker();

							$newCond.find(".endTime").attr("id",
									"endTime" + indexNum).datetimepicker();
							indexNum++;

							$("#condContainer").append($newCond);
						});

		$("#progressMask").hide();

		$("#queryBtn")
				.click(
						function() {
							$("#tab1 tbody").html();
							$("#progressMask").show();
							var map = {};
							var params = "[";
							var len = $(".condGroup").length;
							$(".condGroup").each(
									function(index, element) {
										var location = $(this).find(
												"select option:selected")
												.val();
										map[location] = $(this).find("select option:selected").text();
										var startTime = $(this).find(
												".startTime").val();
										var endTime = $(this).find(
												".endTime").val();
										var st = $.myTime
												.DateToUnix(startTime)
												+ "000";
										var et = $.myTime
												.DateToUnix(endTime)
												+ "000";

										//	alert("location:" + location + "\n" + "startTime:" + startTime + "\nendTime:" + endTime)
										params += '{"bayonetId":"'
												+ location
												+ '","starttime":' + st
												+ ',"endtime":' + et + '}';
										if (index < len - 1)
											params += ",";

									});
							var bfb = $("#bfb").val() / 100;
							params += "]";
							params1 = '{"bayonetDomains": ' + params
									+ ',"similarity":"' + bfb + '"}';

							//	alert(params);
							//	alert(params1);

							$
									.ajax({
										url : 'http://33.175.29.27:8888/bdhp_apiserver/analyseQuery/areaCollision.do?s=1578c30d163c4a68b80df4a70cbcf954&j='
												+ params1,
										type : 'GET',
										dataType : 'jsonp',
										jsonp : 'callback',
										success : function(response) {
											if (response.isSuc) {
												var data = response.results.carInfos;
												var htmlthead = "";
												var htmlCode = "";
												var jd = "";
												//最大碰撞次数
												var maxcollisiontimes = 0;
												for ( var k = 0; k < data.length; k++) {
													if (data[k].plateNo == '车牌') {//无效数据过滤

													} else {
														if (maxcollisiontimes < data[k].capLists.length) {
															maxcollisiontimes = data[k].capLists.length;
														}
													}
												}
												htmlthead += "<tr><th width='100px'>车牌号</th><th width='100px'>碰撞次数</th>";
												var ii = 0;
												for(var prop in map){
													ii++;
												    if(map.hasOwnProperty(prop)){
												       // console.log('key is ' + prop +' and value is' + map[prop]);
												    	htmlthead += "<th width='100px'>"+map[prop]+"</th>";
												    }
												} 
												htmlthead += "</tr>";
												$("#tab1").html(
														htmlthead);
												for ( var i = 0; i < data.length; i++) {
													if (data[i].plateNo != '车牌' && data[i].plateNo !='0') {
														htmlCode += "<tr style='cursor: pointer;' onclick='goDetail(&quot"+data[i].plateNo+"&quot);'><td width='100px'>"
																+ data[i].plateNo
																+ "</td><td width='100px'>"
																+ data[i].collisiontimes
																+ "</td>";
														if (data[i].capLists.length > 0) {
															for(var prop in map){
																htmlCode += "<td width='100px'>";
																for(var j=0; j< data[i].capLists.length;j++){
																	if(prop == data[i].capLists[j].bayonetId){
																		var temtime=0;
																		temtime = data[i].capLists[j].time/1000;
																		htmlCode += $.myTime.UnixToDate(temtime,true,8);
																		htmlCode +="</br>";
																	}
																}
																htmlCode += "</td>";
															} 
															htmlCode += "</tr>";
														}
													}
												}
												$("#tab2").html(
														htmlCode);
												$("#progressMask").hide();
											} else {
												var message = response.results;
												alert(message);
												$("#progressMask").hide();
											}
										},
										error : function(jqXHR, textStatus,
												errorThrown) {
											if (textStatus == "timeout") {
												$("#progressMask").hide();
												$("#tips").html("加载超时，请重试");

											} else {
												$("#progressMask").hide();
												alert(textStatus);
											}
										},
										timeout : 120000
									});
						});
	});
</script>
</body>

</html>
