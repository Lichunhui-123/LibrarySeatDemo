<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/common.jsp"%>
<!DOCTYPE html>
<html>
  <head>
    <title>选座位</title>
<script type="text/javascript">

	$(function(){
	
		//日期选择
		$("#s_date").combobox({
                        url: "${path }/seat/dateCombo.htm", //从远程加载列表数据的 URL
                        valueField: "value", //value 上的基础数据的名称
                        textField: "label",  // text 上的基础数据的名称
                        panelHeight: "auto",
                        editable: false, //不允许手动输入
                        onLoadSuccess: function () { //远程数据加载完毕时触发
                           var data = $('#s_date').combobox('getData');//返回加载的数据
						   $('#s_date').combobox('select',data[0].value);//选择指定的选项
                        }
        });
        
        //时间选择
        $("#s_time").combobox({
                        url: "${path }/seat/timeCombo.htm", //
                        valueField: "value",
                        textField: "label",
                        panelHeight: "auto",
                        editable: false, //不允许手动输入
                        onLoadSuccess: function () { //数据加载完毕事件
                           var data = $('#s_time').combobox('getData');
						   $('#s_time').combobox('select',data[0].value);
                        }
        });
        
        //阅览室选择
         $("#s_room").combobox({
                        url: "${path }/seat/roomCombo.htm", //获取所有私有域
                        valueField: "id",
                        textField: "name",
                        panelHeight: "auto",
                        editable: false, //不允许手动输入
                        onLoadSuccess: function () { //数据加载完毕事件
                           var data = $('#s_room').combobox('getData');
						   $('#s_room').combobox('select',data[0].id);
                        }
        });

       // alert('searchseat');
        //展示座位
        searchSeat();

        //alert('begin remark');
        
        //标记自己的座位
       remarkMyselfseat();
        
	});

	

	//展示所有座位
	function searchSeat(){
		var date = $("#s_date").combobox("getValue");//加载选中的日期
		var time = $("#s_time").combobox("getValue");//加载选中的时间段
		var roomid = $("#s_room").combobox("getValue");//加载选中的阅览室id
		//发送ajax同步请求
		//作用：将锁住浏览器，用户其它操作必须等待请求完成才可以执行
		//如何发送Ajax 格式如下：
		$.ajax({
			url:'${path}/seat/combolist.htm',            //表示服务器的路径
			data:{"roomid":roomid,"date":date,"time":time},//data:表示要传给服务器的数据
			async:false,   //同步，等待请求完成后才执行remarkMyselfseat()，使用同步才能显示自己选中的座位样式
			type:'post',   //请求类型
			dataType:'json',//服务端返回的数据类型
			// 当请求成功时运行的函数
			//回调函数 result表示服务端返回的json都封装在里面
			success:function(result){
				var l = result.length; //座位总数
				var rowMax = result[l-1].row;//取最后一个座位的行：最大行
				var colMax = result[l-1].col;//取最后一个座位的列：最大列
				//console.log("行"+rowMax);
				//console.log("列"+colMax);
				var str = "<tr><td></td>";
				for(var i=0;i<colMax;i++){//生成所有的列号
					str += "<td>&nbsp;</td><td>&nbsp;</td><td>"+(i+1)+"列</td>";//列号
				}
				str += "</tr>";
				//按行显示所有座位信息
				for(var i=1;i<=rowMax;i++){   
					str += "<tr><td>第"+i+"排</td>";    //行号
					for(var j=1;j<=colMax;j++){  
						var seat = result[j +(i-1)*colMax -1];     //第i排第j列在ajax返回的json中的位置
						var id = seat.date + "-" + seat.time + "-" + seat.roomid + "-" + i + "-" + j;//id存的是日期-时间段-阅览室-行-列
						str += "<td>&nbsp;</td><td>&nbsp;</td><td>";
						str += "<section title='.squaredOne'>";
    					str += "<div class='squaredOne' ";
    					if(seat.studentno=="1"){   //学号为1代表座位可选
    						str += " onclick='sel(\""+id+"\")' ";
    					}
    					str += " >";
      					str += "<input type='checkbox' value='"+seat.studentno+"' id='"+id+"' name='check'";
      					if(seat.studentno!="1" && i == seat.row && j == seat.col){//不为1代表座位已经选中，设置不可选
      						str += " checked disable='true' ";
      					} 
      					str += "/>";
      					str += "<label for='squaredOne' id='"+id+"LABEL'></label></div></section>";
      					str += "</td>";
					}
					str += "</tr>";
				}
				$("#tableID").html(str);//获取座位显示表格的id，将元素的内容设置为变量“str”的值
			},
			//请求失败时调用此函数
			error:function(error){
				console.log(error);//控制台打印错误信息
			}
		});
		//显示自己选中的座位
		remarkMyselfseat();
	}
	
	
	// 个人选座样式变化
	//id存的是日期-时间段-阅览室-行-列
	function sel(id){
		var oldValue = $("#oldLabel").val();
		if(id==oldValue){  //说明两次点在一个上面，表示取消
			$("#newLabel").val("");
			$("#oldLabel").val("");
			$(document.getElementById(oldValue + "LABEL")).after(function(){
				$(this).css({"background":"#38393d"});
			});
			return;
		}		
		
		// 去掉旧值样式
		var oldLabelID = oldValue + "LABEL";
		$(document.getElementById(oldLabelID)).after(function(){
			$(this).css({"background":"#38393d"});
		});
		
		// 改变此次样式
		var a = document.getElementById(id);
		var labelID = $(a).nextAll('label').eq(0).attr("id");//获取选中座位的label标签的id
		var l = document.getElementById(labelID);
		$(l).after(function(){
			$(this).css({"background":"red"});//选中后背景变成红色
		});
		// 设置新值
		$("#newLabel").val(id);
		$("#oldLabel").val(id);
	}
	
	//显示自己选中的座位
	function remarkMyselfseat(){
		//alert('remark');
		var date = $("#s_date").combobox("getValue");
		var time = $("#s_time").combobox("getValue");
		var roomid = $("#s_room").combobox("getValue");
		
		$.ajax({
			url:'myselfSeat.htm',
			data:{"roomid":roomid,"date":date,"time":time},
			async:false,
			type:'post',
			//加载成功时触发
			success:function(result){
				//alert(result);
				if(result=="no"){
					//不做操作
				}else{
					//标记颜色。并设置值
					$("#newLabel").val(result);
					$("#oldLabel").val(result);
					$("#mySeat").val(result);
					$(document.getElementById(result + "LABEL")).after(function(){
						$(this).css({"background":"red"});//标记为红色，表示自己选的
					});
				}
			}
		
		});
	}
	
	
	
	// 保存选座
	function saveSeat(){
		var keyword = $("#newLabel").val();//获取value值
		if(keyword==null || keyword.length==0){
			$.messager.alert('系统提示',"请选择一个位置");
			return;
		}
		//异步发送ajax请求
		//作用：提高用户体验，实现局部刷新
		$.ajax({
			url:'saveSeat.htm',  //表示服务器的路径
			data:{keyword:keyword},   //data:表示要传给服务器的数据
			//加载成功时触发
			//回调函数result表示服务端返回的json都封装在里面
			success:function(result){
				if(result=="ok"){
					$.messager.alert('系统提示',"成功预约");
					searchSeat();//局部刷新
				} else{
					$.messager.alert('系统提示',result);
				}
			}
		});
	}
	
	
	// 取消选座
	function cancelSeat(){
		var oldKey = $("#mySeat").val();//获取自己选中的座位keyword
		if(oldKey==null || oldKey.length==0){
			$.messager.alert('系统提示',"您未在该阅览室的这个日期时间段选座！");
			return;
		} else {
			//异步发送ajax请求
			$.ajax({
				url:'cancelSeat.htm',
				data:{"seatkeyword":oldKey},
				type:'post',
				success:function(data){
					if(data=="ok"){//取消成功
						$.messager.alert('系统提示',"取消成功");
						searchSeat();//局部刷新
					} else{//取消失败
						$.messager.alert('系统提示',result);return;
					}
				}
			});
		}
	}
</script>
</head>
<body style="margin: 1px;">
	
	<input id="oldLabel"  type="hidden"/><!-- 已选的 -->
	<input id="newLabel"  type="hidden"/><!-- 可选的 -->
	<input id="mySeat"    type="hidden"/><!-- 自己选的 -->
	<table title="座位信息【说明：绿色表示已选，黑色表示可选，红色表示您】 " class="easyui-datagrid" toolbar="#tb">
	</table>
	<!-- 日期、时间段、阅览室、查询、保存座位、取消占座 -->
	<div id="tb">
	<div class="updownInterval"> </div>
	<div>
		&nbsp;日期&nbsp;<input class="easyui-combobox"  id="s_date"  size="20" />
		&nbsp;&nbsp;&nbsp;时间段&nbsp;<input class="easyui-combobox"  id="s_time"  size="20"/>
		&nbsp;&nbsp;&nbsp;阅览室&nbsp;<input class="easyui-combobox"  id="s_room"  size="20" />
		<a href="javascript:searchSeat()" class="easyui-linkbutton" iconCls="icon-search" >查询</a>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<a href="javascript:saveSeat()" class="easyui-linkbutton" iconCls="icon-ok" >保存位置</a>
		<a href="javascript:cancelSeat()" class="easyui-linkbutton" iconCls="icon-cancel" >取消占座</a>
	</div>
	<div class="updownInterval"> </div>
	</div>
	
	
	
	<!-- 显示座位 -->
  	<table id="tableID" align="center">
  		
  	</table>
  	
</body>
</html>